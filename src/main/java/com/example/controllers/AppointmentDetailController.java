package com.example.controllers;

import com.example.DAO.*;
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
import java.util.Optional;


public class AppointmentDetailController {

    @FXML private TextField txtMaBenhNhan,txtHoTen, txtSoDienThoai, txtGioBatDau, txtGioKetThuc;
    @FXML private DatePicker dateNgaySinh, dateNgayKham;
    @FXML private ChoiceBox<String> cbGioiTinh;
    @FXML private TextArea txtLyDo;
    @FXML private Button btnLuu,btnPhieuKhamBenh,btnChonBenhNhanCu;

    private AppointmentEntry entry;
    private AppointmentModel model;
    private Runnable onRefreshCallback;
    private boolean isNewAppointment = false; // Thêm flag để xác định lịch hẹn mới

    public void setOnRefreshCallback(Runnable callback) {
        this.onRefreshCallback = callback;
    }

    public void setEntry(AppointmentEntry entry) {
        this.entry = entry;
        this.model = entry.getModel();

        // Kiểm tra xem đây có phải là lịch hẹn mới không
        if (isNullOrEmpty(model.getMaBenhNhan()) && isNullOrEmpty(model.getHoTen())) {
            // Đây là lịch hẹn mới - chỉ hiển thị placeholder, chưa sinh mã
            isNewAppointment = true;
            txtMaBenhNhan.setPromptText("Mã sẽ được tự động tạo");
            txtMaBenhNhan.setText(""); // Để trống
        } else {
            // Đây là lịch hẹn đã tồn tại
            isNewAppointment = false;
            txtMaBenhNhan.setText(model.getMaBenhNhan());
        }

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

        // Gán sự kiện cho button Lưu dựa trên trạng thái
        if (isNewAppointment) {
            btnLuu.setOnAction(e -> handleLuuMoi());
            btnLuu.setText("Lưu mới"); // Thay đổi text để phân biệt
        } else {
            btnLuu.setOnAction(e -> handleLuu());
            btnLuu.setText("Cập nhật");
        }

        // Thêm listener để tự động sinh mã khi người dùng bắt đầu nhập thông tin
        addAutoGenerateListeners();

        handlePermission();
    }

    private void addAutoGenerateListeners() {
        if (isNewAppointment) {
            // Listener cho trường họ tên
            txtHoTen.focusedProperty().addListener((obs, oldVal, newVal) -> {
                if (newVal && txtMaBenhNhan.getText().trim().isEmpty()) {
                    generateNewPatientCode();
                }
            });

            // Listener cho trường số điện thoại
            txtSoDienThoai.focusedProperty().addListener((obs, oldVal, newVal) -> {
                if (newVal && txtMaBenhNhan.getText().trim().isEmpty()) {
                    generateNewPatientCode();
                }
            });

            // Listener cho DatePicker ngày sinh
            dateNgaySinh.focusedProperty().addListener((obs, oldVal, newVal) -> {
                if (newVal && txtMaBenhNhan.getText().trim().isEmpty()) {
                    generateNewPatientCode();
                }
            });

            // Listener cho ChoiceBox giới tính
            cbGioiTinh.focusedProperty().addListener((obs, oldVal, newVal) -> {
                if (newVal && txtMaBenhNhan.getText().trim().isEmpty()) {
                    generateNewPatientCode();
                }
            });
        }
    }

    private void generateNewPatientCode() {
        if (isNewAppointment && txtMaBenhNhan.getText().trim().isEmpty()) {
            int nextId = com.example.DAO.PatientDAO.getNextIdNumber("BN");
            String maBenhNhan = "BN" + nextId;
            txtMaBenhNhan.setText(maBenhNhan);
            System.out.println("🔧 DEBUG: Đã sinh mã bệnh nhân mới: " + maBenhNhan);
        }
    }

    private boolean isNullOrEmpty(String s) {
        return s == null || s.trim().isEmpty();
    }

    private void handleLuuMoi() {
        System.out.println("🔧 DEBUG: handleLuuMoi() được gọi");

        // Sinh mã bệnh nhân nếu chưa có
        if (txtMaBenhNhan.getText().trim().isEmpty()) {
            generateNewPatientCode();
        }

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

        System.out.println("🔧 DEBUG: Dữ liệu đầu vào:");
        System.out.println("  - Họ tên: " + hoTen);
        System.out.println("  - Mã bệnh nhân: " + maBenhNhan);
        System.out.println("  - SĐT: " + soDienThoai);
        System.out.println("  - Ngày sinh: " + ngaySinh);
        System.out.println("  - Giới tính: " + gioiTinh);
        System.out.println("  - Lý do: " + lyDo);
        System.out.println("  - Ngày khám: " + ngayKham);
        System.out.println("  - Giờ bắt đầu: " + gioBatDauStr);
        System.out.println("  - Giờ kết thúc: " + gioKetThucStr);
        System.out.println("  - Mã bác sĩ: " + maBacSi);

        // Kiểm tra thông tin đầu vào
        if (hoTen.isEmpty() || soDienThoai.isEmpty() || ngaySinh == null || gioiTinh == null
                || ngayKham == null || maBenhNhan.isEmpty() || gioBatDauStr.isEmpty() || gioKetThucStr.isEmpty()) {
            System.out.println("❌ DEBUG: Thiếu thông tin bắt buộc");
            showAlert("Vui lòng nhập đầy đủ thông tin.");
            return;
        }

        // Chuyển đổi thời gian
        LocalTime gioBatDau;
        LocalTime gioKetThuc;
        try {
            gioBatDau = LocalTime.parse(gioBatDauStr);
            gioKetThuc = LocalTime.parse(gioKetThucStr);
            System.out.println("✅ DEBUG: Đã parse thời gian thành công");
        } catch (Exception e) {
            System.out.println("❌ DEBUG: Lỗi parse thời gian: " + e.getMessage());
            showAlert("Thời gian không hợp lệ! Vui lòng nhập đúng định dạng HH:mm.");
            return;
        }

        if (!gioBatDau.isBefore(gioKetThuc)) {
            System.out.println("❌ DEBUG: Giờ bắt đầu không trước giờ kết thúc");
            showAlert("Giờ bắt đầu phải trước giờ kết thúc.");
            return;
        }

        // Kiểm tra và thêm bệnh nhân nếu chưa tồn tại
        System.out.println("🔧 DEBUG: Kiểm tra bệnh nhân tồn tại...");
        PatientModel existingPatient = PatientDAO.getById(maBenhNhan);
        boolean isNewPatient = false;

        if (existingPatient == null) {
            System.out.println("🔧 DEBUG: Bệnh nhân chưa tồn tại, tạo mới...");
            // Bệnh nhân chưa tồn tại, tạo mới
            PatientModel newPatient = new PatientModel(
                    maBenhNhan,
                    hoTen,
                    ngaySinh,
                    soDienThoai,
                    gioiTinh
            );

            boolean patientInserted = PatientDAO.insert(newPatient);
            if (!patientInserted) {
                System.out.println("❌ DEBUG: Không thể thêm bệnh nhân mới");
                showAlert("Không thể thêm bệnh nhân mới! Vui lòng thử lại.");
                return;
            }
            isNewPatient = true;
            System.out.println("✅ DEBUG: Đã thêm bệnh nhân mới: " + maBenhNhan);
        } else {
            System.out.println("🔧 DEBUG: Bệnh nhân đã tồn tại, kiểm tra cập nhật...");
            // Logic cập nhật bệnh nhân (giữ nguyên như cũ)
            if (!existingPatient.getHoTen().equals(hoTen) ||
                    !existingPatient.getSoDienThoai().equals(soDienThoai) ||
                    !existingPatient.getNgaySinh().equals(ngaySinh) ||
                    !existingPatient.getGioiTinh().equals(gioiTinh)) {

                System.out.println("🔧 DEBUG: Thông tin bệnh nhân khác, cần cập nhật");
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Xác nhận");
                alert.setHeaderText(null);
                alert.setContentText("Bệnh nhân đã tồn tại với thông tin khác. Bạn có muốn cập nhật thông tin không?");

                Optional<ButtonType> result = alert.showAndWait();
                boolean updatePatient = result.isPresent() && result.get() == ButtonType.OK;

                if (updatePatient) {
                    PatientModel updatedPatient = new PatientModel(
                            maBenhNhan,
                            hoTen,
                            ngaySinh,
                            soDienThoai,
                            gioiTinh
                    );

                    boolean patientUpdated = PatientDAO.update(updatedPatient);
                    if (!patientUpdated) {
                        System.out.println("❌ DEBUG: Không thể cập nhật thông tin bệnh nhân");
                        showAlert("Không thể cập nhật thông tin bệnh nhân!");
                        return;
                    }
                    System.out.println("✅ DEBUG: Đã cập nhật thông tin bệnh nhân: " + maBenhNhan);
                }
            }
        }

        // Tạo mã khám bệnh mới
        String maKhamBenh = "KB" + HenKhamBenhDAO.getNextIdNumber("KB");
        System.out.println("🔧 DEBUG: Mã khám bệnh mới: " + maKhamBenh);

        // Tạo model lịch hẹn
        AppointmentModel model = new AppointmentModel();
        model.setMaKhamBenh(maKhamBenh);
        model.setMaBenhNhan(maBenhNhan);
        model.setLyDoKham(lyDo);
        model.setNgayKham(ngayKham);
        model.setGioBatDau(gioBatDau);
        model.setGioKetThuc(gioKetThuc);
        model.setMaBacSi(maBacSi);

        System.out.println("🔧 DEBUG: Thông tin lịch hẹn chuẩn bị lưu:");
        System.out.println("  - Mã khám bệnh: " + maKhamBenh);
        System.out.println("  - Mã bệnh nhân: " + maBenhNhan);
        System.out.println("  - Lý do khám: " + lyDo);
        System.out.println("  - Ngày khám: " + ngayKham);
        System.out.println("  - Giờ bắt đầu: " + gioBatDau);
        System.out.println("  - Giờ kết thúc: " + gioKetThuc);
        System.out.println("  - Mã bác sĩ: " + maBacSi);

        // Gọi DAO để lưu lịch hẹn
        System.out.println("🔧 DEBUG: Đang lưu lịch hẹn...");
        boolean success = HenKhamBenhDAO.insert(model);

        if (success) {
            System.out.println("✅ DEBUG: Lưu lịch hẹn thành công!");

            // TẠO PHIẾU KHÁM BỆNH RỖNG
            System.out.println("🔧 DEBUG: Tạo PhieuKhamBenh rỗng...");
            String maPhieuKham = "PKB" + MedicalReportDAO.getNextIdNumber("PKB");

            MedicalReportModel emptyReport = new MedicalReportModel();
            emptyReport.setMaPhieuKham(maPhieuKham);
            emptyReport.setMaBenhNhan(maBenhNhan);
            emptyReport.setMaBacSi(maBacSi);
            emptyReport.setChanDoan(""); // Chẩn đoán rỗng

            // Tạo phiếu khám với thông tin rỗng
            LocalDateTime ngayKhamDateTime = ngayKham.atTime(gioBatDau); // Ngày khám với giờ bắt đầu
            LocalDateTime ngayLapPhieu = LocalDateTime.now(); // Ngày lập phiếu là hiện tại

            boolean phieuKhamCreated = MedicalReportDAO.insertPhieuKhamBenh(
                    emptyReport,
                    ngayKhamDateTime,
                    ngayLapPhieu,
                    "", // Điều trị rỗng
                    ""  // Kết quả khám rỗng
            );

            if (phieuKhamCreated) {
                System.out.println("✅ DEBUG: Tạo PhieuKhamBenh rỗng thành công: " + maPhieuKham);
            } else {
                System.out.println("⚠️ DEBUG: Không thể tạo PhieuKhamBenh rỗng, nhưng lịch hẹn đã được lưu");
            }
            DonThuocModel newDonThuoc = new DonThuocModel();
            String maDonThuoc = "DT" + DonThuocDAO.getNextIdNumber("DT");
            newDonThuoc.setNgayLapDon(ngayLapPhieu);
            newDonThuoc.setMaDonThuoc(maDonThuoc);
            newDonThuoc.setMaPhieuKham(maPhieuKham);
            boolean success2 = DonThuocDAO.insert(newDonThuoc);
            if (success2) {
                System.out.println("Thêm đơn thuốc thành công" + maDonThuoc );
            } else {
                System.out.println("Thêm đơn thuốc thất bại!");
            }

            // Tạo hóa đơn rỗng duy nhất cho phiếu khám này nếu chưa có
            String maHoaDon = "HD" + maPhieuKham;
            String tenHoaDon = "Hóa đơn khám bệnh - " + hoTen;
            BillModel newBill = new BillModel();
            newBill.setMaHoaDon(maHoaDon);
            newBill.setMaPhieuKham(maPhieuKham);
            newBill.setMaDonThuoc(maDonThuoc);
            newBill.setTongTien(QuiDinhDAO.getGiaTri("DEFAULT_TIEN_KHAM").doubleValue()); // Tiền khám mặc định
            newBill.setTrangThai("Chưa thanh toán");
            LocalDateTime ngayLapHoaDon = LocalDateTime.now();

            // Kiểm tra nếu chưa có hóa đơn thì mới tạo
            BillModel existingBill = BillDAO.getBillById(maHoaDon);
            if (existingBill == null) {
                boolean billCreated = BillDAO.insertBill(newBill, tenHoaDon, ngayLapHoaDon);
                if (billCreated) {
                    System.out.println("✅ DEBUG: Tạo hóa đơn thành công: " + maHoaDon);
                } else {
                    System.out.println("⚠️ DEBUG: Không thể tạo hóa đơn, nhưng lịch hẹn và phiếu khám đã được lưu");
                }
            } else {
                System.out.println("ℹ️ DEBUG: Hóa đơn đã tồn tại, không tạo mới");
            }

            // Cập nhật model vào entry để hiển thị trên calendar
            this.model.setMaKhamBenh(maKhamBenh);
            this.model.setMaBenhNhan(maBenhNhan);
            this.model.setHoTen(hoTen);
            this.model.setSoDienThoai(soDienThoai);
            this.model.setNgaySinh(ngaySinh);
            this.model.setGioiTinh(gioiTinh);
            this.model.setLyDoKham(lyDo);
            this.model.setNgayKham(ngayKham);
            this.model.setGioBatDau(gioBatDau);
            this.model.setGioKetThuc(gioKetThuc);
            this.model.setMaBacSi(maBacSi);

            // Cập nhật entry title
            entry.setTitle(hoTen + " - " + lyDo);

            String message = isNewPatient ?
                    "Đã thêm bệnh nhân mới, lưu lịch hẹn và tạo phiếu khám thành công!" :
                    "Đã lưu lịch hẹn mới và tạo phiếu khám thành công!";
            showInfo(message);

            if (onRefreshCallback != null) {
                onRefreshCallback.run(); // gọi refresh ở controller cha
            }

            // Đóng cửa sổ sau khi lưu thành công
            Stage stage = (Stage) btnLuu.getScene().getWindow();
            stage.close();

        } else {
            System.out.println("❌ DEBUG: Lưu lịch hẹn thất bại!");
            showAlert("Không thể lưu lịch hẹn!");

            // Nếu là bệnh nhân mới và lưu lịch hẹn thất bại, có thể cần xóa bệnh nhân vừa tạo
            if (isNewPatient) {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Xác nhận");
                alert.setHeaderText(null);
                alert.setContentText("Lưu lịch hẹn thất bại. Bạn có muốn xóa bệnh nhân vừa tạo không?");

                Optional<ButtonType> result = alert.showAndWait();
                boolean rollbackPatient = result.isPresent() && result.get() == ButtonType.OK;

                if (rollbackPatient) {
                    PatientDAO.delete(maBenhNhan);
                    txtMaBenhNhan.setText(""); // Reset mã bệnh nhân
                    System.out.println("✅ DEBUG: Đã xóa bệnh nhân do lưu lịch hẹn thất bại: " + maBenhNhan);
                }
            }
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
            controller.refreshMedicineList();
            // Sau khi load phiếu khám, load lại đơn thuốc
            controller.refreshMedicineList();

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
            String maBenhNhan = txtMaBenhNhan.getText().trim();
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
            model.setMaBenhNhan(maBenhNhan);
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