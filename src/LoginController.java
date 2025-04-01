import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.animation.PauseTransition;
import javafx.util.Duration;


public class LoginController {
    @FXML
    private TextField txtTaiKhoan;
    @FXML
    private PasswordField txtMatKhau;
    @FXML
    private Button btnDangNhap;
    @FXML
    private Hyperlink linkQuenMatKhau;
    @FXML
    private Hyperlink linkDangKy;
    @FXML
    private Label lbltest;

    public void initialize() {
        // Ban đầu ẩn lbltest
        lbltest.setVisible(false);

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
            String matKhau = txtMatKhau.getText();
            System.out.println("Tài khoản: " + taiKhoan + ", Mật khẩu: " + matKhau);

            // Kiểm tra tài khoản và mật khẩu
            if (isValidLogin(taiKhoan, matKhau)) {
                // Đăng nhập thành công
                lbltest.setVisible(false); // Ẩn thông báo lỗi nếu có
                System.out.println("Đăng nhập thành công!");
                // TODO: Chuyển sang màn hình chính hoặc thực hiện hành động khác
            } else {
                // Đăng nhập thất bại
                lbltest.setVisible(true); // Hiển thị thông báo lỗi

                // Ẩn lbltest sau 3 giây
                PauseTransition delay = new PauseTransition(Duration.seconds(3));
                delay.setOnFinished(event -> lbltest.setVisible(false));
                delay.play();
            }
        });
    }

    // Phương thức kiểm tra tài khoản và mật khẩu
    private boolean isValidLogin(String taiKhoan, String matKhau) {
        // Ví dụ: Tài khoản là "admin" và mật khẩu là "123456"
        // Bạn có thể thay thế bằng logic kiểm tra từ cơ sở dữ liệu
        return "admin".equals(taiKhoan) && "123456".equals(matKhau);
    }
}