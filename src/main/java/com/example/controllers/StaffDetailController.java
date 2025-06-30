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

    private StaffDataChangeListener dataChangeListener;

    public void setDataChangeListener(StaffDataChangeListener listener) {
        this.dataChangeListener = listener;
    }

    @FXML
    public void initialize() {
        cbRole.getItems().addAll("DOCTOR", "NURSE", "MANAGER", "ADMIN");

        ToggleGroup genderGroup = new ToggleGroup();
        btnMale.setToggleGroup(genderGroup);
        btnFemale.setToggleGroup(genderGroup);
        btnMale.setSelected(true);

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
            dpBirth.setValue(staffModel.getBirthday());

            String gioitinh = staffModel.getGender();
            if ("Nam".equals(gioitinh)) {
                btnMale.setSelected(true);
                btnFemale.setSelected(false);
            } else {
                btnFemale.setSelected(true);
                btnMale.setSelected(false);
            }
        }
    }

    public void register(ActionEvent actionEvent) {
        try {
            String id = tfId.getText();
            if (id == null || id.trim().isEmpty()) throw new IllegalArgumentException("Mã nhân viên không được để trống!");
            String lastName = tfLastName.getText();
            if (lastName == null || lastName.trim().isEmpty()) throw new IllegalArgumentException("Họ không được để trống!");
            String firstName = tfName.getText();
            if (firstName == null || firstName.trim().isEmpty()) throw new IllegalArgumentException("Tên không được để trống!");
            String email = tfEmail.getText();
            String phone = tfPhone.getText();
            String address = tfAddress.getText();
            String password = tfPassword.getText();
            String cccd = tfCCCD.getText();
            String role = cbRole.getValue();
            if (role == null) throw new IllegalArgumentException("Vui lòng chọn vai trò!");
            LocalDate birthday = dpBirth.getValue();
            if (birthday == null) throw new IllegalArgumentException("Vui lòng chọn ngày sinh!");
            String gender = btnMale.isSelected() ? "Nam" : "Nữ";
            double luong = tfSalary.getText().isEmpty() ? 1000 : Double.parseDouble(tfSalary.getText());
            if (luong < 0) throw new IllegalArgumentException("Lương không thể âm!");

            StaffModel staff = new StaffModel(id, lastName, firstName, role, luong, birthday, gender, cccd, address, email, phone, password);
            try {
                boolean success = com.example.DAO.StaffDAO.insertStaff(staff);
                if (success) {
                    showAlert("Thành công", "Đăng ký nhân viên thành công!", Alert.AlertType.INFORMATION);
                    Stage stage = (Stage) btnRegister.getScene().getWindow();
                    stage.close();
                    if (dataChangeListener != null) {
                        dataChangeListener.onDataChanged(staff, "INSERT");
                    }
                } else {
                    showAlert("Thất bại", "Đăng ký nhân viên thất bại! Vui lòng kiểm tra lại thông tin.", Alert.AlertType.ERROR);
                }
            } catch (Exception ex) {
                Throwable cause = ex;
                while (cause.getCause() != null) cause = cause.getCause();
                if (cause instanceof java.sql.SQLIntegrityConstraintViolationException) {
                    String msg = cause.getMessage();
                    if (msg.contains("PRIMARY") || msg.contains("MaNhanVien")) {
                        showAlert("Thất bại", "Mã nhân viên đã tồn tại!", Alert.AlertType.ERROR);
                    } else if (msg.contains("Email")) {
                        showAlert("Thất bại", "Email đã tồn tại!", Alert.AlertType.ERROR);
                    } else if (msg.contains("CCCD")) {
                        showAlert("Thất bại", "CCCD đã tồn tại!", Alert.AlertType.ERROR);
                    } else {
                        showAlert("Thất bại", "Dữ liệu bị trùng lặp!", Alert.AlertType.ERROR);
                    }
                } else {
                    showAlert("Thất bại", "Lỗi: " + cause.getMessage(), Alert.AlertType.ERROR);
                }
            }
        } catch (NumberFormatException ex) {
            showAlert("Lỗi", "Vui lòng nhập lương hợp lệ!", Alert.AlertType.ERROR);
        } catch (IllegalArgumentException ex) {
            showAlert("Lỗi", ex.getMessage(), Alert.AlertType.ERROR);
        } catch (Exception ex) {
            ex.printStackTrace();
            showAlert("Lỗi", "Lỗi không xác định: " + ex.getMessage(), Alert.AlertType.ERROR);
        }
    }

    public void update(ActionEvent actionEvent) {
        try {
            String id = tfId.getText();
            if (id == null || id.trim().isEmpty()) throw new IllegalArgumentException("Mã nhân viên không được để trống!");
            String lastName = tfLastName.getText();
            if (lastName == null || lastName.trim().isEmpty()) throw new IllegalArgumentException("Họ không được để trống!");
            String firstName = tfName.getText();
            if (firstName == null || firstName.trim().isEmpty()) throw new IllegalArgumentException("Tên không được để trống!");
            String email = tfEmail.getText();
            String phone = tfPhone.getText();
            String address = tfAddress.getText();
            String password = tfPassword.getText();
            String cccd = tfCCCD.getText();
            String role = cbRole.getValue();
            if (role == null) throw new IllegalArgumentException("Vui lòng chọn vai trò!");
            LocalDate birthday = dpBirth.getValue();
            if (birthday == null) throw new IllegalArgumentException("Vui lòng chọn ngày sinh!");
            String gender = btnMale.isSelected() ? "Nam" : "Nữ";
            double luong = tfSalary.getText().isEmpty() ? 1000 : Double.parseDouble(tfSalary.getText());
            if (luong < 0) throw new IllegalArgumentException("Lương không thể âm!");

            StaffModel staff = new StaffModel(id, lastName, firstName, role, luong, birthday, gender, cccd, address, email, phone, password);
            try {
                boolean success = com.example.DAO.StaffDAO.updateStaff(staff);
                if (success) {
                    showAlert("Thành công", "Cập nhật nhân viên thành công!", Alert.AlertType.INFORMATION);
                    Stage stage = (Stage) btnUpdate.getScene().getWindow();
                    stage.close();
                    if (dataChangeListener != null) {
                        dataChangeListener.onDataChanged(staff, "UPDATE");
                    }
                } else {
                    showAlert("Thất bại", "Cập nhật nhân viên thất bại! Nhân viên không tồn tại.", Alert.AlertType.ERROR);
                }
            } catch (Exception ex) {
                Throwable cause = ex;
                while (cause.getCause() != null) cause = cause.getCause();
                if (cause instanceof java.sql.SQLIntegrityConstraintViolationException) {
                    String msg = cause.getMessage();
                    if (msg.contains("Email")) {
                        showAlert("Thất bại", "Email đã tồn tại!", Alert.AlertType.ERROR);
                    } else if (msg.contains("CCCD")) {
                        showAlert("Thất bại", "CCCD đã tồn tại!", Alert.AlertType.ERROR);
                    } else {
                        showAlert("Thất bại", "Dữ liệu bị trùng lặp!", Alert.AlertType.ERROR);
                    }
                } else {
                    showAlert("Thất bại", "Lỗi: " + cause.getMessage(), Alert.AlertType.ERROR);
                }
            }
        } catch (NumberFormatException ex) {
            showAlert("Lỗi", "Vui lòng nhập lương hợp lệ!", Alert.AlertType.ERROR);
        } catch (IllegalArgumentException ex) {
            showAlert("Lỗi", ex.getMessage(), Alert.AlertType.ERROR);
        } catch (Exception ex) {
            ex.printStackTrace();
            showAlert("Lỗi", "Lỗi không xác định: " + ex.getMessage(), Alert.AlertType.ERROR);
        }
    }

    public void delete(ActionEvent actionEvent) {
        try {
            String id = tfId.getText();
            try {
                boolean success = com.example.DAO.StaffDAO.deleteStaff(id);
                if (success) {
                    showAlert("Thành công", "Xóa nhân viên thành công!", Alert.AlertType.INFORMATION);
                    Stage stage = (Stage) btnDelete.getScene().getWindow();
                    stage.close();
                    if (dataChangeListener != null) {
                        dataChangeListener.onDataChanged(new StaffModel(id, "", "", "", 0, null, "", "", "", "", "", ""), "DELETE");
                    }
                } else {
                    showAlert("Thất bại", "Xóa nhân viên thất bại! Nhân viên không tồn tại.", Alert.AlertType.ERROR);
                }
            } catch (Exception ex) {
                Throwable cause = ex;
                while (cause.getCause() != null) cause = cause.getCause();
                if (cause instanceof java.sql.SQLIntegrityConstraintViolationException) {
                    String msg = cause.getMessage();
                    if (msg.contains("quidinh") || msg.contains("nguoicapnhat")) {
                        showAlert("Thất bại", "Không thể xóa nhân viên với mã " + id + " vì đang được tham chiếu trong bảng quy định (quidinh).", Alert.AlertType.ERROR);
                    } else if (msg.contains("foreign key")) {
                        showAlert("Thất bại", "Không thể xóa nhân viên vì đang được sử dụng ở bảng khác!", Alert.AlertType.ERROR);
                    } else {
                        showAlert("Thất bại", "Lỗi ràng buộc dữ liệu!", Alert.AlertType.ERROR);
                    }
                } else {
                    showAlert("Thất bại", "Lỗi: " + cause.getMessage(), Alert.AlertType.ERROR);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            showAlert("Lỗi", "Lỗi không xác định: " + ex.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void showAlert(String title, String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}