import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.sql.Connection;
import java.sql.DriverManager;
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
    private Hyperlink linkQuenMatKhau;
    @FXML
    private Hyperlink linkDangKy;
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

        // Sự kiện cho linkQuenMatKhau
        linkQuenMatKhau.setOnAction(e -> {
            System.out.println("Mở chức năng quên mật khẩu...");
        });

        // Sự kiện cho linkDangKy
        linkDangKy.setOnAction(e -> {
            System.out.println("Mở chức năng đăng ký...");
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
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/main.fxml"));
                    Parent root = loader.load();
                    Stage stage = (Stage) btnDangNhap.getScene().getWindow();
                    stage.setScene(new Scene(root));
                    stage.setTitle("Màn hình chính");
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

    // Phương thức kiểm tra tài khoản và mật khẩu từ cơ sở dữ liệu
    private boolean isValidLogin(String taiKhoan, String matKhau) {
        // Thông tin kết nối cơ sở dữ liệu
        String url = "jdbc:mysql://localhost:3306/your_database"; // Sửa URL
        String dbUser = "your_username";
        String dbPassword = "your_password";

        try (Connection conn = DriverManager.getConnection(url, dbUser, dbPassword)) {
            String query = "SELECT * FROM users WHERE username = ? AND password = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, taiKhoan);
            stmt.setString(2, matKhau); // Lưu ý: Nên mã hóa mật khẩu trong thực tế
            ResultSet rs = stmt.executeQuery();

            return rs.next(); // Trả về true nếu tìm thấy bản ghi khớp
        } catch (Exception e) {
            e.printStackTrace();
            lbltest.setText("Lỗi kết nối cơ sở dữ liệu: " + e.getMessage());
            lbltest.setVisible(true);
            return false;
        }
    }
}