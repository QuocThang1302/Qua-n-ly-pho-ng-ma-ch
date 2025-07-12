package com.example.controllers;

import com.example.DAO.StaffDAO;
import com.example.model.Role;
import com.example.model.StaffModel;
import com.example.model.UserContext;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.util.StringConverter;

import java.net.URL;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

public class MyProfileController {

    // Personal Information Fields
    @FXML private TextField tfId;
    @FXML private ComboBox<Role> cbRole;
    @FXML private TextField tfLastName;
    @FXML private TextField tfName;
    @FXML private TextField tfCCCD;
    @FXML private ToggleButton btnMale;
    @FXML private ToggleButton btnFemale;
    @FXML private DatePicker dpBirth;
    @FXML private TextField tfPhone;

    // Contact Information Fields
    @FXML private TextField tfEmail;
    @FXML private PasswordField tfPassword;
    @FXML private TextField tfPasswordVisible; // For showing password
    @FXML private Button btnTogglePassword;
    @FXML private StackPane passwordContainer;
    @FXML private TextField tfAddress;
    @FXML private PasswordField tfConfirmPassword;
    @FXML private HBox confirmPasswordContainer;

    // Work Information Fields
    @FXML private TextField tfSalary;

    // ToggleGroup for gender selection
    private ToggleGroup genderGroup;

    // Current staff data
    private StaffModel currentStaff;

    // Password management
    private boolean isPasswordVisible = false;
    private boolean isPasswordChanged = false;
    private String originalPassword = "";

    // Decimal formatter for salary
    private DecimalFormat salaryFormatter = new DecimalFormat("#,###");

    // Role options
    private ObservableList<Role> roleOptions = FXCollections.observableArrayList(
            Role.NURSE, Role.DOCTOR, Role.MANAGER, Role.ADMIN
    );

    // Validation patterns
    private final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    );
    private final Pattern PHONE_PATTERN = Pattern.compile("^[0-9]{10,11}$");
    private final Pattern CCCD_PATTERN = Pattern.compile("^[0-9]{9,12}$");

    @FXML
    public void initialize() {
        setupGenderToggleGroup();
        setupRoleComboBox();
        setupDatePicker();
        setupNumericFields();
        setupPasswordFields();
        setupValidation();
        loadUserData();
    }

    private void setupGenderToggleGroup() {
        genderGroup = new ToggleGroup();
        btnMale.setToggleGroup(genderGroup);
        btnFemale.setToggleGroup(genderGroup);

        // Set default selection
        btnMale.setSelected(true);
    }

    private void setupRoleComboBox() {
        cbRole.setItems(roleOptions);
        cbRole.getSelectionModel().selectFirst();

        // Set up StringConverter to display Vietnamese names
        cbRole.setConverter(new StringConverter<Role>() {
            @Override
            public String toString(Role role) {
                return role != null ? role.toVietnamese() : "";
            }

            @Override
            public Role fromString(String string) {
                try {
                    return Role.fromVietnamese(string);
                } catch (IllegalArgumentException e) {
                    return null;
                }
            }
        });
    }

    private void setupDatePicker() {
        dpBirth.setConverter(new StringConverter<LocalDate>() {
            private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

            @Override
            public String toString(LocalDate date) {
                return date != null ? date.format(formatter) : "";
            }

            @Override
            public LocalDate fromString(String string) {
                return string != null && !string.isEmpty() ?
                        LocalDate.parse(string, formatter) : null;
            }
        });
    }

    // Trong phương thức setupPasswordFields(), thay đổi phần setup toggle button:

    private void setupPasswordFields() {
        // Initially hide confirm password
        confirmPasswordContainer.setVisible(false);

        // Setup password visibility toggle
        tfPasswordVisible.setVisible(false);

        // Bind visible password field with password field
        tfPasswordVisible.textProperty().bindBidirectional(tfPassword.textProperty());

        // Monitor password changes
        tfPassword.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.equals(originalPassword)) {
                if (!isPasswordChanged) {
                    isPasswordChanged = true;
                    showConfirmPasswordField();
                }
            } else {
                if (isPasswordChanged && newValue.equals(originalPassword)) {
                    isPasswordChanged = false;
                    hideConfirmPasswordField();
                }
            }
        });

        // Setup toggle button
        btnTogglePassword.setOnAction(e -> togglePasswordVisibility());

        // Set initial button text và style
        btnTogglePassword.setText("show");
    }

    @FXML
    private void togglePasswordVisibility() {
        isPasswordVisible = !isPasswordVisible;

        if (isPasswordVisible) {
            // Show password
            tfPassword.setVisible(false);

            tfPasswordVisible.setVisible(true);

            btnTogglePassword.setText("hide");
        } else {
            // Hide password
            tfPasswordVisible.setVisible(false);

            tfPassword.setVisible(true);

            btnTogglePassword.setText("show");
        }
    }

    private void showConfirmPasswordField() {
        confirmPasswordContainer.setVisible(true);

        tfConfirmPassword.setText(""); // Clear confirm password when showing
    }

    private void hideConfirmPasswordField() {
        confirmPasswordContainer.setVisible(false);

        tfConfirmPassword.setText(""); // Clear confirm password when hiding
    }

    private void setupNumericFields() {
        // Format salary with thousand separators
        tfSalary.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                tfSalary.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });

        // Format salary display when focus is lost
        tfSalary.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue && !tfSalary.getText().isEmpty()) {
                try {
                    double salary = Double.parseDouble(tfSalary.getText().replaceAll("[^\\d]", ""));
                    tfSalary.setText(salaryFormatter.format(salary));
                } catch (NumberFormatException e) {
                    // Keep original text if parsing fails
                }
            }
        });

        // Only allow numbers in phone field
        tfPhone.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                tfPhone.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });

        // Only allow numbers in CCCD field
        tfCCCD.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                tfCCCD.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });
    }

    private void setupValidation() {
        // Email validation
        tfEmail.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) { // When focus is lost
                validateEmail();
            }
        });

        // Phone validation
        tfPhone.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                validatePhone();
            }
        });

        // CCCD validation
        tfCCCD.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                validateCCCD();
            }
        });

        // Password confirmation validation
        tfConfirmPassword.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                validatePasswordMatch();
            }
        });
    }

    private void loadUserData() {
        try {
            String userId = UserContext.getInstance().getUserId();
            if (userId != null) {
                currentStaff = StaffDAO.getByID(userId);
                if (currentStaff != null) {
                    populateFields(currentStaff);
                } else {
                    showErrorAlert("Lỗi", "Không thể tải thông tin nhân viên");
                }
            } else {
                showErrorAlert("Lỗi", "Không tìm thấy thông tin người dùng hiện tại");
            }
        } catch (Exception e) {
            showErrorAlert("Lỗi", "Có lỗi xảy ra khi tải dữ liệu: " + e.getMessage());
            loadDefaultData(); // Load default data if error occurs
        }
    }

    private void populateFields(StaffModel staff) {
        tfId.setText(staff.getId());
        tfLastName.setText(staff.getLastname());
        tfName.setText(staff.getFirstname());
        tfCCCD.setText(staff.getCccd());
        tfPhone.setText(staff.getPhone());
        tfEmail.setText(staff.getEmail());
        tfAddress.setText(staff.getAddress());
        tfSalary.setText(salaryFormatter.format(staff.getLuong()));
        dpBirth.setValue(staff.getBirthday());

        // Set gender
        if ("Nam".equals(staff.getGender())) {
            btnMale.setSelected(true);
        } else {
            btnFemale.setSelected(true);
        }

        // Set role
        try {
            Role role = Role.valueOf(staff.getRole());
            cbRole.getSelectionModel().select(role);
        } catch (IllegalArgumentException e) {
            cbRole.getSelectionModel().selectFirst(); // Default selection if role not found
        }

        // Load password from database
        String password = staff.getPassword();
        if (password != null && !password.isEmpty()) {
            tfPassword.setText(password);
            originalPassword = password;
        } else {
            tfPassword.setText("");
            originalPassword = "";
        }

        // Reset password change flag
        isPasswordChanged = false;
        hideConfirmPasswordField();
    }

    private void loadDefaultData() {
        // Empty implementation for default data loading
    }

    @FXML
    private void handleSaveChanges() {
        if (validateForm()) {
            try {
                saveUserData();
                showSuccessAlert("Cập nhật thành công!", "Thông tin cá nhân đã được lưu.");

                // Update original password if changed
                if (isPasswordChanged) {
                    originalPassword = tfPassword.getText();
                    isPasswordChanged = false;
                    hideConfirmPasswordField();
                }
            } catch (Exception e) {
                showErrorAlert("Lỗi", "Có lỗi xảy ra khi lưu thông tin: " + e.getMessage());
            }
        }
    }

    private boolean validateForm() {
        StringBuilder errors = new StringBuilder();

        // Validate required fields
        if (tfId.getText().trim().isEmpty()) {
            errors.append("- Mã nhân viên không được để trống\n");
        }

        if (tfLastName.getText().trim().isEmpty()) {
            errors.append("- Họ và tên đệm không được để trống\n");
        }

        if (tfName.getText().trim().isEmpty()) {
            errors.append("- Tên không được để trống\n");
        }

        if (tfCCCD.getText().trim().isEmpty()) {
            errors.append("- CCCD/CMND không được để trống\n");
        } else if (!CCCD_PATTERN.matcher(tfCCCD.getText()).matches()) {
            errors.append("- CCCD/CMND không hợp lệ (9-12 chữ số)\n");
        }

        if (tfPhone.getText().trim().isEmpty()) {
            errors.append("- Số điện thoại không được để trống\n");
        } else if (!PHONE_PATTERN.matcher(tfPhone.getText()).matches()) {
            errors.append("- Số điện thoại không hợp lệ (10-11 chữ số)\n");
        }

        if (tfEmail.getText().trim().isEmpty()) {
            errors.append("- Email không được để trống\n");
        } else if (!EMAIL_PATTERN.matcher(tfEmail.getText()).matches()) {
            errors.append("- Email không hợp lệ\n");
        }

        if (tfPassword.getText().trim().isEmpty()) {
            errors.append("- Mật khẩu không được để trống\n");
        } else if (tfPassword.getText().length() < 6) {
            errors.append("- Mật khẩu phải có ít nhất 6 ký tự\n");
        }

        // Only validate password confirmation if password was changed
        if (isPasswordChanged) {
            if (tfConfirmPassword.getText().trim().isEmpty()) {
                errors.append("- Xác nhận mật khẩu không được để trống\n");
            } else if (!tfPassword.getText().equals(tfConfirmPassword.getText())) {
                errors.append("- Mật khẩu xác nhận không khớp\n");
            }
        }

        if (dpBirth.getValue() == null) {
            errors.append("- Ngày sinh không được để trống\n");
        } else if (dpBirth.getValue().isAfter(LocalDate.now().minusYears(16))) {
            errors.append("- Tuổi phải từ 16 trở lên\n");
        }

        if (tfSalary.getText().trim().isEmpty()) {
            errors.append("- Mức lương không được để trống\n");
        }

        if (errors.length() > 0) {
            showErrorAlert("Thông tin không hợp lệ", errors.toString());
            return false;
        }

        return true;
    }

    private boolean validateEmail() {
        if (!tfEmail.getText().trim().isEmpty() &&
                !EMAIL_PATTERN.matcher(tfEmail.getText()).matches()) {
            tfEmail.setStyle("-fx-border-color: red;");
            return false;
        }
        tfEmail.setStyle("");
        return true;
    }

    private boolean validatePhone() {
        if (!tfPhone.getText().trim().isEmpty() &&
                !PHONE_PATTERN.matcher(tfPhone.getText()).matches()) {
            tfPhone.setStyle("-fx-border-color: red;");
            return false;
        }
        tfPhone.setStyle("");
        return true;
    }

    private boolean validateCCCD() {
        if (!tfCCCD.getText().trim().isEmpty() &&
                !CCCD_PATTERN.matcher(tfCCCD.getText()).matches()) {
            tfCCCD.setStyle("-fx-border-color: red;");
            return false;
        }
        tfCCCD.setStyle("");
        return true;
    }

    private boolean validatePasswordMatch() {
        if (isPasswordChanged && !tfPassword.getText().equals(tfConfirmPassword.getText())) {
            tfConfirmPassword.setStyle("-fx-border-color: red;");
            return false;
        }
        tfConfirmPassword.setStyle("");
        return true;
    }

    private void saveUserData() {
        // Get selected gender
        String gender = btnMale.isSelected() ? "Nam" : "Nữ";

        // Parse salary (remove formatting)
        double salary = 0;
        try {
            salary = Double.parseDouble(tfSalary.getText().replaceAll("[^\\d]", ""));
        } catch (NumberFormatException e) {
            throw new RuntimeException("Lỗi định dạng mức lương");
        }

        // Update current staff model
        if (currentStaff == null) {
            currentStaff = new StaffModel();
        }

        currentStaff.setId(tfId.getText());
        currentStaff.setLastname(tfLastName.getText());
        currentStaff.setFirstname(tfName.getText());
        currentStaff.setRole(cbRole.getSelectionModel().getSelectedItem().toString());
        currentStaff.setLuong(salary);
        currentStaff.setBirthday(dpBirth.getValue());
        currentStaff.setGender(gender);
        currentStaff.setCccd(tfCCCD.getText());
        currentStaff.setAddress(tfAddress.getText());
        currentStaff.setEmail(tfEmail.getText());
        currentStaff.setPhone(tfPhone.getText());

        // Only update password if it was changed
        if (isPasswordChanged) {
            currentStaff.setPassword(tfPassword.getText());
        }

        // Debug output
        System.out.println("Saving user data:");
        System.out.println("ID: " + currentStaff.getId());
        System.out.println("Name: " + currentStaff.getLastname() + " " + currentStaff.getFirstname());
        System.out.println("Gender: " + currentStaff.getGender());
        System.out.println("Role: " + currentStaff.getRole());
        System.out.println("CCCD: " + currentStaff.getCccd());
        System.out.println("Phone: " + currentStaff.getPhone());
        System.out.println("Email: " + currentStaff.getEmail());
        System.out.println("Address: " + currentStaff.getAddress());
        System.out.println("Birth Date: " + currentStaff.getBirthday());
        System.out.println("Salary: " + currentStaff.getLuong());
        System.out.println("Password changed: " + isPasswordChanged);

        StaffDAO.updateStaff(currentStaff);
    }

    private void showSuccessAlert(String title, String message) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showErrorAlert(String title, String message) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Getter methods for accessing current staff data
    public StaffModel getCurrentStaff() {
        return currentStaff;
    }

    public String getEmployeeId() {
        return tfId.getText();
    }

    public String getFullName() {
        return tfLastName.getText() + " " + tfName.getText();
    }

    public String getGender() {
        return btnMale.isSelected() ? "Nam" : "Nữ";
    }

    public Role getRole() {
        return cbRole.getSelectionModel().getSelectedItem();
    }

    public String getRoleVietnamese() {
        Role selectedRole = cbRole.getSelectionModel().getSelectedItem();
        return selectedRole != null ? selectedRole.toVietnamese() : "";
    }

    public String getCCCD() {
        return tfCCCD.getText();
    }

    public String getPhone() {
        return tfPhone.getText();
    }

    public String getEmail() {
        return tfEmail.getText();
    }

    public String getAddress() {
        return tfAddress.getText();
    }

    public LocalDate getBirthDate() {
        return dpBirth.getValue();
    }

    public double getSalary() {
        try {
            return Double.parseDouble(tfSalary.getText().replaceAll("[^\\d]", ""));
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    public String getSalaryFormatted() {
        return tfSalary.getText();
    }

    public boolean isPasswordChanged() {
        return isPasswordChanged;
    }
}