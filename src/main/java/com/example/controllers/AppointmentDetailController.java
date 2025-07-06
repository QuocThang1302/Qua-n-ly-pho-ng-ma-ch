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
    @FXML private Button btnLuu,btnPhieuKhamBenh,btnChonBenhNhanCu;

    private AppointmentEntry entry;
    private AppointmentModel model;
    private Runnable onRefreshCallback;

    public void setOnRefreshCallback(Runnable callback) {
        this.onRefreshCallback = callback;
    }

    public void setEntry(AppointmentEntry entry) {
        this.entry = entry;
        this.model = entry.getModel();

        // Đổ dữ liệu từ model ra UI
        txtMaBenhNhan.setText(model.getMaBenhNhan());
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

        if (isNullOrEmpty(txtMaBenhNhan.getText()) && isNullOrEmpty(txtHoTen.getText())) {
            // Tự động sinh mã bệnh nhân mới
            String prefix = "BN";
            int nextId = com.example.DAO.PatientDAO.getNextIdNumber(prefix);
            String newMaBenhNhan = prefix + String.format("%03d", nextId);
            txtMaBenhNhan.setText(newMaBenhNhan);
            btnLuu.setOnAction(e -> handleLuuMoi());
        } else {
            btnLuu.setOnAction(e -> handleLuu());
        }

        handlePermission();

    }

    private boolean isNullOrEmpty(String s) {
        return s == null || s.trim().isEmpty();
    }

    private void handleLuuMoi() {
        // Lấy dữ liệu từ giao diện
        String hoTen = txtHoTen.getText().trim();
        String maBenhNhan = txtMaBenhNhan.getText().trim();
        String soDienThoai = txtSoDienThoai.getText().trim();
        LocalDate ngaySinh = dateNgaySinh.getValue();
        String gioiTinh = cbGioiTinh.getValue();
        String lyDo = txtLyDo.getText().trim();
        LocalDate ngayKham = dateNgayKham.getValue();
        String gioBatDauStr = txtGioBatDau.getText().trim();
        String gioKetThucStr = txtGioKetThuc.getText().trim();
        String maBacSi = UserContext.getInstance().getUserId();

        if (hoTen.isEmpty() || soDienThoai.isEmpty() || ngaySinh == null || gioiTinh == null
                || ngayKham == null || maBenhNhan.isEmpty() || gioBatDauStr.isEmpty() || gioKetThucStr.isEmpty()) {
            showAlert("Vui lòng nhập đầy đủ thông tin.");
            return;
        }

        // Thêm mới bệnh nhân nếu chưa tồn tại
        PatientModel patient = new PatientModel(
            maBenhNhan,
            hoTen,
            ngaySinh,
            soDienThoai,
            gioiTinh
        );
        if (com.example.DAO.PatientDAO.getById(maBenhNhan) == null) {
            boolean inserted = com.example.DAO.PatientDAO.insert(patient);
            if (!inserted) {
                showAlert("Không thể thêm mới bệnh nhân!");
                return;
            }
        }

        // Chuyển đổi thời gian
        LocalTime gioBatDau;
        LocalTime gioKetThuc;
        try {
            gioBatDau = LocalTime.parse(gioBatDauStr);
            gioKetThuc = LocalTime.parse(gioKetThucStr);
        } catch (Exception e) {
            showAlert("Thời gian không hợp lệ! Vui lòng nhập đúng định dạng HH:mm.");
            return;
        }
        if (!gioBatDau.isBefore(gioKetThuc)) {
            showAlert("Giờ bắt đầu phải trước giờ kết thúc.");
            return;
        }

        // Sinh mã khám bệnh mới
        String maKhamBenh = "KB" + System.currentTimeMillis();

        // Tạo model lịch hẹn
        AppointmentModel model = new AppointmentModel();
        model.setMaKhamBenh(maKhamBenh);
        model.setMaBenhNhan(maBenhNhan);
        model.setLyDoKham(lyDo);
        model.setNgayKham(ngayKham);
        model.setGioBatDau(gioBatDau);
        model.setGioKetThuc(gioKetThuc);
        model.setMaBacSi(maBacSi);

        // Gọi DAO để lưu lịch hẹn
        boolean success = HenKhamBenhDAO.insert(model);

        if (success) {
            txtMaBenhNhan.setText(maBenhNhan);
            showInfo("Đã lưu lịch hẹn mới thành công!");

            if (onRefreshCallback != null) {
                onRefreshCallback.run(); // gọi refresh ở controller cha
            }
        } else {
            showAlert("Không thể lưu lịch hẹn!");
        }
    }


    private void handlePhieuKham() {
        try {
            // Load phiếu khám bệnh từ database dựa vào mã khám bệnh
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/medical_report.fxml"));
            Parent view = loader.load();

            MedicalReportController controller = loader.getController();
            
            // Sử dụng phương thức mới để load dữ liệu từ database
            controller.loadMedicalReportByMaKhamBenh(model.getMaKhamBenh());

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
            String hoTen = txtHoTen.getText().trim();
            String sdt = txtSoDienThoai.getText().trim();
            LocalDate ngaySinh = dateNgaySinh.getValue();
            String gioiTinh = cbGioiTinh.getValue();
            String lyDo = txtLyDo.getText().trim();
            LocalDate ngayKham = dateNgayKham.getValue();
            LocalTime gioBatDau = LocalTime.parse(txtGioBatDau.getText().trim());
            LocalTime gioKetThuc = LocalTime.parse(txtGioKetThuc.getText().trim());

            if (!gioBatDau.isBefore(gioKetThuc)) {
                showAlert("Giờ bắt đầu phải trước giờ kết thúc.");
                return;
            }

            // Cập nhật entry trong calendar view
            entry.changeStartDate(ngayKham);
            entry.changeStartTime(gioBatDau);
            entry.changeEndDate(ngayKham);
            entry.changeEndTime(gioKetThuc);
            entry.setTitle(hoTen + " - " + lyDo);

            // Cập nhật dữ liệu trong model
            model.setHoTen(hoTen);
            model.setSoDienThoai(sdt);
            model.setNgaySinh(ngaySinh);
            model.setGioiTinh(gioiTinh);
            model.setLyDoKham(lyDo);
            model.setNgayKham(ngayKham);
            model.setGioBatDau(gioBatDau);
            model.setGioKetThuc(gioKetThuc);
            model.setMaBacSi("BS001"); // hoặc lấy từ combobox nếu có

            // Cập nhật vào database
            boolean success = HenKhamBenhDAO.update(model);
            if (success) {
                System.out.println("✅ Đã cập nhật lịch hẹn: " + model.getMaKhamBenh());
                Stage stage = (Stage) btnLuu.getScene().getWindow();
                stage.close();
            } else {
                showAlert("Không thể cập nhật lịch hẹn!");
            }

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
                cbGioiTinh.setValue(selected.getGioiTinh());
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

    private void showInfo(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Thông báo");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

}
