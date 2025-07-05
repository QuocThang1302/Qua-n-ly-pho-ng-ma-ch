package com.example.controllers;

import com.example.DAO.HenKhamBenhDAO;
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
    @FXML private Button btnLuu,btnPhieuKhamBenh;

    private AppointmentEntry entry;
    private AppointmentModel model;

    public void setEntry(AppointmentEntry entry) {
        this.entry = entry;
        this.model = entry.getModel();

        // ✅ Đổ dữ liệu từ model ra UI
        txtMaBenhNhan.setText(model.getMaBenhNhan() != null ? model.getMaBenhNhan() : "");
        txtHoTen.setText(model.getHoTen() != null ? model.getHoTen() : "");
        txtSoDienThoai.setText(model.getSoDienThoai() != null ? model.getSoDienThoai() : "");
        dateNgaySinh.setValue(model.getNgaySinh());
        cbGioiTinh.getItems().setAll("Nam", "Nữ");
        cbGioiTinh.setValue(model.getGioiTinh() != null ? model.getGioiTinh() : "Nam");

        txtLyDo.setText(model.getLyDoKham() != null ? model.getLyDoKham() : "");

        // Gán ngày khám chung
        dateNgayKham.setValue(entry.getStartDate());
        txtGioBatDau.setText(entry.getStartTime().toString());
        txtGioKetThuc.setText(entry.getEndTime().toString());

        btnPhieuKhamBenh.setOnAction(e-> handlePhieuKham());
        btnLuu.setOnAction(e -> handleLuu());
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
            String hoTen = txtHoTen.getText() != null ? txtHoTen.getText().trim() : "";
            String sdt = txtSoDienThoai.getText() != null ? txtSoDienThoai.getText().trim() : "";
            LocalDate ngaySinh = dateNgaySinh.getValue();
            String gioiTinh = cbGioiTinh.getValue() != null ? cbGioiTinh.getValue() : "Nam";
            String lyDo = txtLyDo.getText() != null ? txtLyDo.getText().trim() : "";
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
            
            // ✅ Lưu vào database
            boolean success = HenKhamBenhDAO.update(model);
            if (success) {
                System.out.println("✅ Đã lưu thành công: " + model.getMaKhamBenh());
            } else {
                System.err.println("❌ Lỗi khi lưu vào database");
            }
            
            Stage stage = (Stage) btnLuu.getScene().getWindow();
            stage.close();

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
                // ✅ Cập nhật UI
                txtMaBenhNhan.setText(selected.getMaBenhNhan() != null ? selected.getMaBenhNhan() : "");
                txtHoTen.setText(selected.getHoTen() != null ? selected.getHoTen() : "");
                txtSoDienThoai.setText(selected.getSoDienThoai() != null ? selected.getSoDienThoai() : "");
                dateNgaySinh.setValue(selected.getNgaySinh());
                cbGioiTinh.setValue(selected.getGioiTinh() != null ? selected.getGioiTinh() : "Nam");
                
                // ✅ Cập nhật model
                model.setMaBenhNhan(selected.getMaBenhNhan() != null ? selected.getMaBenhNhan() : "");
                model.setHoTen(selected.getHoTen() != null ? selected.getHoTen() : "");
                model.setSoDienThoai(selected.getSoDienThoai() != null ? selected.getSoDienThoai() : "");
                model.setNgaySinh(selected.getNgaySinh());
                model.setGioiTinh(selected.getGioiTinh() != null ? selected.getGioiTinh() : "Nam");
                
                // ✅ Cập nhật entry title
                String hoTen = selected.getHoTen() != null ? selected.getHoTen() : "Chưa có tên";
                String lyDo = model.getLyDoKham() != null ? model.getLyDoKham() : "Chưa có lý do";
                entry.setTitle(hoTen + " - " + lyDo);
                
                // ✅ Lưu vào database
                boolean success = HenKhamBenhDAO.update(model);
                if (success) {
                    System.out.println("✅ Đã cập nhật bệnh nhân: " + selected.getMaBenhNhan());
                } else {
                    System.err.println("❌ Lỗi khi cập nhật bệnh nhân");
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
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
