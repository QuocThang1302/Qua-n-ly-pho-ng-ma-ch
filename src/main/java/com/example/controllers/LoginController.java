package com.example.controllers;

import com.example.model.Role;
import com.example.model.UserContext;
import com.example.utils.DatabaseConnector;
import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class LoginController {
    @FXML
    private TextField txtTaiKhoan;
    @FXML
    private PasswordField txtMatKhau;
    @FXML
    private TextField txtMatKhauVisible; // TextField để hiển thị mật khẩu
    @FXML
    private CheckBox chkShowPassword; // CheckBox để hiển thị/ẩn mật khẩu
    @FXML
    private Button btnDangNhap;
    @FXML
    private Label lbltest;

    @FXML
    public void initialize() {
        // Ban đầu ẩn lbltest
        lbltest.setVisible(false);

        // Đồng bộ dữ liệu giữa PasswordField và TextField
        txtMatKhau.textProperty().addListener((observable, oldValue, newValue) -> {
            txtMatKhauVisible.setText(newValue);
        });

        txtMatKhauVisible.textProperty().addListener((observable, oldValue, newValue) -> {
            txtMatKhau.setText(newValue);
        });

        // Xử lý sự kiện khi CheckBox được chọn/bỏ chọn
        chkShowPassword.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                // Hiển thị mật khẩu: Ẩn PasswordField, hiển thị TextField
                txtMatKhau.setVisible(false);
                txtMatKhauVisible.setVisible(true);
                txtMatKhauVisible.setText(txtMatKhau.getText());
            } else {
                // Ẩn mật khẩu: Hiển thị PasswordField, ẩn TextField
                txtMatKhau.setVisible(true);
                txtMatKhauVisible.setVisible(false);
                txtMatKhau.setText(txtMatKhauVisible.getText());
            }
        });

        // Sự kiện cho btnDangNhap
        btnDangNhap.setOnAction(e -> {
            // Lấy giá trị từ TextField và PasswordField
            String taiKhoan = txtTaiKhoan.getText();
            String matKhau = txtMatKhau.getText(); // Lấy mật khẩu từ PasswordField
            System.out.println("Tài khoản: " + taiKhoan + ", Mật khẩu: " + matKhau);

            // Kiểm tra tài khoản và mật khẩu
            if (taiKhoan.isEmpty() || matKhau.isEmpty()) {
                lbltest.setText("Vui lòng nhập đầy đủ tài khoản và mật khẩu");
                lbltest.setVisible(true);
                PauseTransition delay = new PauseTransition(Duration.seconds(3));
                delay.setOnFinished(event -> lbltest.setVisible(false));
                delay.play();
                return;
            }

            if (isValidLogin(taiKhoan, matKhau)) {
                // Đăng nhập thành công
                lbltest.setVisible(false); // Ẩn thông báo lỗi nếu có
                System.out.println("Đăng nhập thành công!");

                // Chuyển sang màn hình chính
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/menu.fxml"));
                    Parent root = loader.load();

                    Stage stage = (Stage) btnDangNhap.getScene().getWindow();
                    Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();

                    Scene scene = new Scene(root, screenBounds.getWidth(), screenBounds.getHeight());
                    scene.getStylesheets().add(getClass().getResource("/css/menu-style.css").toExternalForm());
                    stage.setScene(scene);
                    stage.setTitle("Màn hình chính");
                    stage.setResizable(true);
                    stage.setMaximized(true);;
                    stage.show();
                } catch (Exception ex) {
                    ex.printStackTrace();
                    lbltest.setText("Lỗi khi chuyển màn hình: " + ex.getMessage());
                    lbltest.setVisible(true);
                }
            } else {
                // Đăng nhập thất bại
                lbltest.setVisible(true);

                // Ẩn lbltest sau 3 giây
                PauseTransition delay = new PauseTransition(Duration.seconds(3));
                delay.setOnFinished(event -> lbltest.setVisible(false));
                delay.play();
            }
        });
    }

    @FXML
    private void handleForgotPassword(MouseEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/email_verification.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Xác thực Email");
            stage.setScene(new Scene(root));
            stage.setResizable(false);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR, "Không thể mở form xác thực email.");
            alert.showAndWait();
        }
    }

    // Phương thức kiểm tra tài khoản và mật khẩu từ cơ sở dữ liệu
    private boolean isValidLogin(String taiKhoan, String matKhau) {
        try (Connection conn = DatabaseConnector.connect()) {
            String query = "SELECT * FROM NhanVien WHERE MaNhanVien = ? AND MatKhau = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, taiKhoan);
            stmt.setString(2, matKhau);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                // Lấy vai trò từ CSDL
                String userId = rs.getString("MaNhanVien");
                String vaiTroStr = rs.getString("RoleID");
                Role role = Role.valueOf(vaiTroStr.toUpperCase());

                // Lưu vào context
                UserContext.getInstance().setUser(taiKhoan, userId, role);
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            lbltest.setText("Lỗi kết nối CSDL: " + e.getMessage());
            lbltest.setVisible(true);
            return false;
        }
    }

}