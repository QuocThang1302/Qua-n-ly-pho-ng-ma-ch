package com.example.controllers;

import com.example.model.StaffModel;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class StaffDetailController {
    @FXML
    private TextField tfId, tfLastName, tfName, tfEmail, tfPhone, tfAddress, tfPassword, tfCCCD, tfSalary;
    @FXML
    private ComboBox<String> cbRole;
    @FXML
    private DatePicker dpBirth;
    @FXML
    private ToggleButton btnMale;
    @FXML
    private ToggleButton btnFemale;
    @FXML
    private Button btnRegister, btnUpdate, btnDelete;

    private StaffDataChangeListener dataChangeListener; // Callback

    // Setter để thiết lập callback
    public void setDataChangeListener(StaffDataChangeListener listener) {
        this.dataChangeListener = listener;
    }

    @FXML
    public void initialize() {
        // Code của combo box
        cbRole.getItems().addAll("DOCTOR", "NURSE", "MANAGER", "ADMIN");

        // Code của toggle button
        ToggleGroup genderGroup = new ToggleGroup();
        btnMale.setToggleGroup(genderGroup);
        btnFemale.setToggleGroup(genderGroup);

        btnMale.setSelected(true);

        // Code của date picker
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

        // Code của button
        btnRegister.setVisible(true);
        btnRegister.setManaged(true);

        btnUpdate.setVisible(false);
        btnUpdate.setManaged(false);

        btnDelete.setVisible(false);
        btnDelete.setManaged(false);
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
            tfSalary.setText(String.valueOf(staffModel.getLuong()));

            cbRole.setValue(staffModel.getRole());

            String gioitinh = staffModel.getGender();
            if ("Nam".equals(gioitinh)) {
                btnMale.setSelected(true);
            } else {
                btnFemale.setSelected(true);
            }

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
            double luong = 1000; // Tạm thời lương mặc định
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

                // Đóng cửa sổ và gọi callback
                Stage stage = (Stage) btnRegister.getScene().getWindow();
                stage.close();
                if (dataChangeListener != null) {
                    dataChangeListener.onDataChanged();
                }
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Thất bại");
                alert.setHeaderText(null);
                alert.setContentText(errorMsg != null ? errorMsg : "Đăng ký nhân viên thất bại! Vui lòng kiểm tra lại thông tin nhập vào.");
                alert.showAndWait();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Lỗi");
            alert.setHeaderText(null);
            alert.setContentText("Vui lòng kiểm tra lại thông tin nhập vào!\n" + e.getMessage());
            alert.showAndWait();
        }
    }

    public void update(ActionEvent actionEvent) {
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
            double luong = 1000; // Tạm thời lương mặc định
            StaffModel staff = new StaffModel(id, lastName, firstName, role, luong, birthday, gender, cccd, address, email, phone, password);
            boolean success = false;
            String errorMsg = null;
            try {
                success = com.example.DAO.StaffDAO.updateStaff(staff);
            } catch (Exception ex) {
                Throwable cause = ex;
                while (cause.getCause() != null) cause = cause.getCause();
                if (cause instanceof java.sql.SQLIntegrityConstraintViolationException) {
                    String msg = cause.getMessage();
                    if (msg.contains("Email")) {
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
                alert.setContentText("Cập nhật nhân viên thành công!");
                alert.showAndWait();

                // Đóng cửa sổ và gọi callback
                Stage stage = (Stage) btnUpdate.getScene().getWindow();
                stage.close();
                if (dataChangeListener != null) {
                    dataChangeListener.onDataChanged();
                }
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Thất bại");
                alert.setHeaderText(null);
                alert.setContentText(errorMsg != null ? errorMsg : "Cập nhật nhân viên thất bại! Vui lòng kiểm tra lại thông tin nhập vào.");
                alert.showAndWait();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Lỗi");
            alert.setHeaderText(null);
            alert.setContentText("Vui lòng kiểm tra lại thông tin nhập vào!\n" + e.getMessage());
            alert.showAndWait();
        }
    }

    public void delete(ActionEvent actionEvent) {
        try {
            String id = tfId.getText();
            boolean success = false;
            String errorMsg = null;
            try {
                success = com.example.DAO.StaffDAO.deleteStaff(id);
            } catch (Exception ex) {
                Throwable cause = ex;
                while (cause.getCause() != null) cause = cause.getCause();
                if (cause instanceof java.sql.SQLIntegrityConstraintViolationException) {
                    String msg = cause.getMessage();
                    if (msg.contains("quidinh") || msg.contains("nguoicapnhat")) {
                        errorMsg = "Không thể xóa nhân viên với mã " + id + " vì đang được tham chiếu trong bảng quy định (quidinh).\n" +
                                "Vui lòng xóa hoặc cập nhật các bản ghi liên quan trong bảng quy định trước khi xóa nhân viên này.";
                    } else if (msg.contains("foreign key")) {
                        errorMsg = "Không thể xóa nhân viên vì đang được sử dụng ở bảng khác!";
                    } else {
                        errorMsg = "Lỗi ràng buộc dữ liệu!";
                    }
                } else {
                    errorMsg = cause.getMessage();
                }
            }
            if (success) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Thành công");
                alert.setHeaderText(null);
                alert.setContentText("Xóa nhân viên thành công!");
                alert.showAndWait();

                // Đóng cửa sổ và gọi callback
                Stage stage = (Stage) btnDelete.getScene().getWindow();
                stage.close();
                if (dataChangeListener != null) {
                    dataChangeListener.onDataChanged();
                }
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Thất bại");
                alert.setHeaderText(null);
                alert.setContentText(errorMsg != null ? errorMsg : "Không thể xóa nhân viên với mã " + id + " vì đang được tham chiếu trong bảng quy định (quidinh).\n" +
                        "Vui lòng xóa hoặc cập nhật các bản ghi liên quan trong bảng quy định trước khi xóa nhân viên này.");
                alert.showAndWait();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Lỗi");
            alert.setHeaderText(null);
            alert.setContentText("Vui lòng kiểm tra lại!\n" + e.getMessage());
            alert.showAndWait();
        }
    }
}