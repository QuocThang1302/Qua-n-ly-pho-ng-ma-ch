package com.example.controllers;

import com.example.model.StaffModel;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.StringConverter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class StaffDetailController {
    @FXML
    private TextField tfId, tfLastName, tfName, tfEmail, tfPhone, tfAddress, tfPassword, tfCCCD, tfLuong;
    @FXML
    private ComboBox<String> cbRole;
    @FXML
    private DatePicker dpBirth;
    @FXML
    private ToggleButton btnMale;
    @FXML
    private ToggleButton btnFemale;
    @FXML
    Button btnRegister, btnUpdate, btnDelete;

    @FXML
    public void initialize() {
        // code của combo box
        cbRole.getItems().addAll("Bác sĩ", "Y tá", "Quản lý", "Admin");

        // code của toggle button
        ToggleGroup genderGroup = new ToggleGroup();
        btnMale.setToggleGroup(genderGroup);
        btnFemale.setToggleGroup(genderGroup);

        btnMale.setSelected(true);

        // code của date picker
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        dpBirth.setConverter(new StringConverter<LocalDate>() {
            @Override
            public String toString(LocalDate date) {
                return (date != null) ? formatter.format(date) : "";
            }

            @Override
            public LocalDate fromString(String string) {
                return (string != null && !string.isEmpty()) ? LocalDate.parse(string, formatter) : null;
            }
        });

        // code của button
        btnRegister.setVisible(true);
        btnRegister.setManaged(true);

        btnUpdate.setVisible(false);
        btnUpdate.setManaged(false);

        btnDelete.setVisible(false);
        btnDelete.setManaged(false);

        // Bỏ lấy tfLuong, tạm thời không dùng trường lương
    }

    public void setStaff(StaffModel staffModel) {
        if (staffModel != null) {
            btnRegister.setVisible(false);
            btnRegister.setManaged(false);

            btnUpdate.setVisible(true);
            btnUpdate.setManaged(true);

            btnDelete.setVisible(true);
            btnDelete.setManaged(true);

            tfId.setText(staffModel.getId());
            tfLastName.setText(staffModel.getLastname());
            tfName.setText(staffModel.getFirstname());
            tfEmail.setText(staffModel.getEmail());
            tfPhone.setText(staffModel.getPhone());
            tfAddress.setText(staffModel.getAddress());
            tfPassword.setText(staffModel.getPassword());
            tfCCCD.setText(staffModel.getCccd());

            cbRole.setValue(staffModel.getRole());

            String gioitinh = staffModel.getGender();
            if (gioitinh == "Nam")
                btnMale.setSelected(true);
            else
                btnFemale.setSelected(true);

            dpBirth.setValue(staffModel.getBirthday());
        }
    }

    public void register(ActionEvent actionEvent) {
        try {
            String id = tfId.getText();
            String lastName = tfLastName.getText();
            String firstName = tfName.getText();
            String email = tfEmail.getText();
            String phone = tfPhone.getText();
            String address = tfAddress.getText();
            String password = tfPassword.getText();
            String cccd = tfCCCD.getText();
            String role = cbRole.getValue();
            LocalDate birthday = dpBirth.getValue();
            String gender = btnMale.isSelected() ? "Nam" : "Nữ";
            double luong = 0; // tạm thời lương mặc định là 0
            StaffModel staff = new StaffModel(id, lastName, firstName, role, luong, birthday, gender, cccd, address, email, phone, password);
            boolean success = false;
            String errorMsg = null;
            try {
                success = com.example.DAO.StaffDAO.insertStaff(staff);
            } catch (Exception ex) {
                Throwable cause = ex;
                while (cause.getCause() != null) cause = cause.getCause();
                if (cause instanceof java.sql.SQLIntegrityConstraintViolationException) {
                    String msg = cause.getMessage();
                    if (msg.contains("PRIMARY") || msg.contains("MaNhanVien")) {
                        errorMsg = "Mã nhân viên đã tồn tại!";
                    } else if (msg.contains("Email")) {
                        errorMsg = "Email đã tồn tại!";
                    } else if (msg.contains("CCCD")) {
                        errorMsg = "CCCD đã tồn tại!";
                    } else {
                        errorMsg = "Dữ liệu bị trùng lặp!";
                    }
                } else {
                    errorMsg = cause.getMessage();
                }
            }
            if (success) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Thành công");
                alert.setHeaderText(null);
                alert.setContentText("Đăng ký nhân viên thành công!");
                alert.showAndWait();
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Thất bại");
                alert.setHeaderText(null);
                alert.setContentText(errorMsg != null ? errorMsg : "Đăng ký nhân viên thất bại! Vui lòng kiểm tra lại thông tin nhập vào.");
                alert.showAndWait();
            }
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Lỗi");
            alert.setHeaderText(null);
            alert.setContentText("Vui lòng kiểm tra lại thông tin nhập vào!");
            alert.showAndWait();
        }
    }

    public void update(ActionEvent actionEvent) {
    }

    public void delete(ActionEvent actionEvent) {

    }
}
