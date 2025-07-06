package com.example.controllers;

import com.example.model.*;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.List;

public class AppointmentDetailController {

    @FXML private TextField txtMaBenhNhan,txtHoTen, txtSoDienThoai, txtGioBatDau, txtGioKetThuc;
    @FXML private DatePicker dateNgaySinh, dateNgayKham;
    @FXML private ChoiceBox<String> cbGioiTinh;
    @FXML private TextArea txtLyDo;
    @FXML private Button btnLuu,btnPhieuKhamBenh,btnChonBenhNhanCu;

    private AppointmentEntry entry;
    private AppointmentModel model;

    public void setEntry(AppointmentEntry entry) {
        this.entry = entry;
        this.model = entry.getModel();

        // Đổ dữ liệu từ model ra UI
        txtHoTen.setText(model.getHoTen());
        txtSoDienThoai.setText(model.getSoDienThoai());
        dateNgaySinh.setValue(model.getNgaySinh());
        cbGioiTinh.getItems().setAll("Nam", "Nữ");
        cbGioiTinh.setValue(model.getGioiTinh());

        txtLyDo.setText(model.getLyDoKham());

        // Gán ngày khám chung
        dateNgayKham.setValue(entry.getStartDate());
        txtGioBatDau.setText(entry.getStartTime().toString());
        txtGioKetThuc.setText(entry.getEndTime().toString());

        btnPhieuKhamBenh.setOnAction(e-> handlePhieuKham());
        btnLuu.setOnAction(e -> handleLuu());
        handlePermission();
    }

    private void handlePhieuKham() {
        try {
            // ⚠️ TODO: Gọi DAO thực tế để lấy report và bill từ maKhamBenh
            // MedicalReportModel report = MedicalReportDAO.getByMaKhamBenh(model.getMaKhamBenh());
            // BillModel bill = report.getHoaDon();
            // Dữ liệu mẫu (tạm thời, chưa dùng DAO)
            List<MedicineModel> thuocList = List.of(
                    new MedicineModel("T001", "Paracetamol", "Hạ sốt", 10, 5000, "viên", "Uống sau ăn"),
                    new MedicineModel("T002", "Amoxicillin", "Kháng sinh", 20, 3000, "viên", "2 lần/ngày"),
                    new MedicineModel("T003", "Vitamin C", "Tăng đề kháng", 15, 2000, "viên", "Uống buổi sáng")
            );

            BillModel bill = new BillModel(
                    "HD001",300000,20000,"Da thanh toan","Ma don Thuoc",LocalDateTime.now(),thuocList, "MaPhieuKham");

            MedicalReportModel report = new MedicalReportModel(
                    model.getMaKhamBenh(),
                    "PK001",
                    model.getMaBenhNhan(),
                    model.getMaBacSi(),
                    model.getHoTen(),
                    "Bác sĩ Minh",
                    model.getNgaySinh(),
                    model.getSoDienThoai(),
                    model.getGioiTinh(),
                    model.getLyDoKham(),
                    LocalDateTime.now(),
                    "Viêm mũi dị ứng",
                    bill
            );

            // Load giao diện phiếu khám
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/medical_report.fxml"));
            Parent view = loader.load();

            MedicalReportController controller = loader.getController();
            controller.setData(report, bill);

// Tạo một cửa sổ mới (Stage)
            Stage stage = new Stage();
            stage.setTitle("Phiếu khám bệnh");
            stage.setScene(new Scene(view, 800, 600)); // Set kích thước cửa sổ

            stage.setResizable(false); // Không cho resize
            stage.initModality(Modality.APPLICATION_MODAL); // Chặn các cửa sổ khác cho đến khi đóng

// Hiển thị cửa sổ và chờ đóng
            stage.showAndWait();



        } catch (IOException e) {
            showAlert("Không thể mở phiếu khám: " + e.getMessage());
        }
    }


    private void handleLuu() {
        try {
            // Lấy thông tin từ giao diện
            String hoTen = txtHoTen.getText();
            String sdt = txtSoDienThoai.getText();
            LocalDate ngaySinh = dateNgaySinh.getValue();
            String gioiTinh = cbGioiTinh.getValue();
            String lyDo = txtLyDo.getText();
            LocalDate ngayKham = dateNgayKham.getValue();

            LocalTime gioBatDau = LocalTime.parse(txtGioBatDau.getText());
            LocalTime gioKetThuc = LocalTime.parse(txtGioKetThuc.getText());

            // Cập nhật entry (giờ và ngày giống nhau)
            entry.changeStartDate(ngayKham);
            entry.changeStartTime(gioBatDau);
            entry.changeEndDate(ngayKham); // cùng ngày
            entry.changeEndTime(gioKetThuc);
            entry.setTitle(hoTen + " - " + lyDo);

            // Cập nhật model
            model.setHoTen(hoTen);
            model.setSoDienThoai(sdt);
            model.setNgaySinh(ngaySinh);
            model.setGioiTinh(gioiTinh);
            model.setLyDoKham(lyDo);
            model.setNgayKham(ngayKham);
            model.setNgayKetThuc(ngayKham); // luôn đồng bộ
            Stage stage = (Stage) btnLuu.getScene().getWindow();
            stage.close();
            System.out.println("✅ Đã lưu: " + model.getMaKhamBenh());

        } catch (DateTimeParseException ex) {
            showAlert("Định dạng giờ không hợp lệ. Vui lòng nhập HH:mm");
        } catch (Exception ex) {
            showAlert("Lỗi khi lưu thông tin: " + ex.getMessage());
        }
    }
    @FXML
    private void handleChonBenhNhanCu() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/PatientSelectionDialog.fxml"));
            Parent root = loader.load();

            Stage dialogStage = new Stage();
            dialogStage.setScene(new Scene(root));
            dialogStage.setTitle("Chọn bệnh nhân");

            dialogStage.initModality(Modality.APPLICATION_MODAL);

            PatientSelectionDialogController controller = loader.getController();

            dialogStage.showAndWait();

            PatientModel selected = controller.getSelectedBenhNhan();
            if (selected != null) {
                txtMaBenhNhan.setText(selected.getMaBenhNhan());
                txtHoTen.setText(selected.getHoTen());
                txtSoDienThoai.setText(selected.getSoDienThoai());
                dateNgaySinh.setValue(selected.getNgaySinh());
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void handlePermission(){
        Role role = UserContext.getInstance().getRole();
        switch (role) {
            case ADMIN -> {

            }
            case DOCTOR -> {
                btnChonBenhNhanCu.setVisible(false);
                txtMaBenhNhan.setEditable(false);
                txtHoTen.setEditable(false);
                txtSoDienThoai.setEditable(false);
                txtGioBatDau.setEditable(false);
                txtGioKetThuc.setEditable(false);
                txtLyDo.setEditable(false);
                btnLuu.setVisible(false);
                cbGioiTinh.setDisable(true);
                dateNgaySinh.setEditable(false);
                dateNgayKham.setEditable(false);
            }
            case NURSE -> {

            }
            case MANAGER -> {
                btnChonBenhNhanCu.setVisible(false);
                txtMaBenhNhan.setEditable(false);
                txtHoTen.setEditable(false);
                txtSoDienThoai.setEditable(false);
                txtGioBatDau.setEditable(false);
                txtGioKetThuc.setEditable(false);
                txtLyDo.setEditable(false);
                btnLuu.setVisible(false);
                cbGioiTinh.setDisable(true);
                dateNgaySinh.setEditable(false);
                dateNgayKham.setEditable(false);
            }
        }
    }
    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Lỗi");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
