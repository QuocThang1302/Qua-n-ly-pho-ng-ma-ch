package com.example.controllers;

import com.example.model.AppointmentEntry;
import com.example.model.AppointmentModel;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;

public class AppointmentDetailController {

    @FXML private TextField txtHoTen, txtSoDienThoai, txtGioBatDau, txtGioKetThuc;
    @FXML private DatePicker dateNgaySinh, dateNgayKham;
    @FXML private ChoiceBox<String> cbGioiTinh;
    @FXML private TextArea txtLyDo;
    @FXML private Button btnLuu;

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

        btnLuu.setOnAction(e -> handleLuu());
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

            System.out.println("✅ Đã lưu: " + model.getMaKhamBenh());

        } catch (DateTimeParseException ex) {
            showAlert("Định dạng giờ không hợp lệ. Vui lòng nhập HH:mm");
        } catch (Exception ex) {
            showAlert("Lỗi khi lưu thông tin: " + ex.getMessage());
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
