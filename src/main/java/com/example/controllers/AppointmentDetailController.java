package com.example.controllers;

import com.example.DAO.HenKhamBenhDAO;
import com.example.DAO.PatientDAO;
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
    @FXML private Button btnLuu,btnPhieuKhamBenh;

    private AppointmentEntry entry;
    private AppointmentModel model;

    public void setEntry(AppointmentEntry entry) {
        this.entry = entry;
        this.model = entry.getModel();

        // ✅ Đổ dữ liệu từ model ra UI
        String maBenhNhan = model.getMaBenhNhan() != null ? model.getMaBenhNhan() : "";
        
        // ✅ Nếu mã bệnh nhân rỗng, tạo mã mới
        if (maBenhNhan.isEmpty()) {
            maBenhNhan = PatientDAO.generateNewMaBenhNhan();
            model.setMaBenhNhan(maBenhNhan);
        }
        
        txtMaBenhNhan.setText(maBenhNhan);
        txtHoTen.setText(model.getHoTen() != null ? model.getHoTen() : "");
        txtSoDienThoai.setText(model.getSoDienThoai() != null ? model.getSoDienThoai() : "");
        dateNgaySinh.setValue(model.getNgaySinh());
        cbGioiTinh.getItems().setAll("Nam", "Nữ");
        cbGioiTinh.setValue(model.getGioiTinh() != null ? model.getGioiTinh() : "Nam");

        txtLyDo.setText(model.getLyDoKham() != null ? model.getLyDoKham() : "");

        // Gán ngày khám chung
        dateNgayKham.setValue(entry.getStartDate());
        txtGioBatDau.setText(entry.getStartTime().toString());
        txtGioKetThuc.setText(entry.getEndTime().toString());

        btnPhieuKhamBenh.setOnAction(e-> handlePhieuKham());
        btnLuu.setOnAction(e -> handleLuu());
    }

    private void handlePhieuKham() {
        try {
            // ⚠️ TODO: Gọi DAO thực tế để lấy report và bill từ maKhamBenh
            // MedicalReportModel report = MedicalReportDAO.getByMaKhamBenh(model.getMaKhamBenh());
            // BillModel bill = report.getHoaDon();
            // Dữ liệu mẫu (tạm thời, chưa dùng DAO)
            List<MedicineModel> thuocList = List.of(
                    new MedicineModel("T001", "Paracetamol", "Hạ sốt", 10, 5000, "viên", "Uống sau ăn"),
                    new MedicineModel("T002", "Amoxicillin", "Kháng sinh", 20, 3000, "viên", "2 lần/ngày"),
                    new MedicineModel("T003", "Vitamin C", "Tăng đề kháng", 15, 2000, "viên", "Uống buổi sáng")
            );

            BillModel bill = new BillModel(
                    "HD001",300000,20000,"Da thanh toan","Ma don Thuoc",LocalDateTime.now(),thuocList, "MaPhieuKham");

            MedicalReportModel report = new MedicalReportModel(
                    model.getMaKhamBenh(),
                    "PK001",
                    model.getMaBenhNhan(),
                    model.getMaBacSi(),
                    model.getHoTen(),
                    "Bác sĩ Minh",
                    model.getNgaySinh(),
                    model.getSoDienThoai(),
                    model.getGioiTinh(),
                    model.getLyDoKham(),
                    LocalDateTime.now(),
                    "Viêm mũi dị ứng",
                    bill
            );

            // Load giao diện phiếu khám
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/medical_report.fxml"));
            Parent view = loader.load();

            MedicalReportController controller = loader.getController();
            controller.setData(report, bill);

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
            // ✅ Lấy dữ liệu từ form
            String maBenhNhan = txtMaBenhNhan.getText().trim();
            String hoTen = txtHoTen.getText().trim();
            String lyDoKham = txtLyDo.getText().trim();
            String sdt = txtSoDienThoai.getText().trim();
            LocalDate ngaySinh = dateNgaySinh.getValue();
            String gioiTinh = cbGioiTinh.getValue() != null ? cbGioiTinh.getValue() : "Nam";
            LocalDate ngayKham = dateNgayKham.getValue();
            
            // ✅ Kiểm tra dữ liệu bắt buộc
            if (hoTen.isEmpty()) {
                showAlert("Vui lòng nhập họ tên bệnh nhân!");
                return;
            }
            
            if (lyDoKham.isEmpty()) {
                showAlert("Vui lòng nhập lý do khám!");
                return;
            }
            
            // ✅ Xử lý MaBenhNhan
            if (maBenhNhan.isEmpty()) {
                // Tạo mã bệnh nhân mới nếu chưa có
                maBenhNhan = PatientDAO.generateNewMaBenhNhan();
                txtMaBenhNhan.setText(maBenhNhan);
                System.out.println("🆕 Đã tạo mã bệnh nhân mới: " + maBenhNhan);
            }
            
            // ✅ Xử lý MaKhamBenh
            String maKhamBenh = model.getMaKhamBenh();
            if (maKhamBenh == null || maKhamBenh.isEmpty()) {
                // Tạo mã khám bệnh mới nếu chưa có
                maKhamBenh = HenKhamBenhDAO.generateNewMaKhamBenh();
                model.setMaKhamBenh(maKhamBenh);
                System.out.println("🆕 Đã tạo mã khám bệnh mới: " + maKhamBenh);
            }
            
            // ✅ Tách họ và tên
            String[] nameParts = hoTen.split(" ", 2);
            String ho = nameParts.length > 0 ? nameParts[0] : "";
            String ten = nameParts.length > 1 ? nameParts[1] : "";
            
            // ✅ Cập nhật model bệnh nhân
            PatientModel patientModel = new PatientModel();
            patientModel.setMaBenhNhan(maBenhNhan);
            patientModel.setHoTen(hoTen);
            patientModel.setNgaySinh(ngaySinh != null ? ngaySinh : LocalDate.now());
            patientModel.setSoDienThoai(sdt);
            patientModel.setGioiTinh(gioiTinh);
            
            // ✅ Cập nhật model lịch hẹn
            model.setMaBenhNhan(maBenhNhan);
            model.setHoTen(hoTen);
            model.setLyDoKham(lyDoKham);
            model.setNgayKham(ngayKham != null ? ngayKham : LocalDate.now());
            model.setNgayKetThuc(ngayKham != null ? ngayKham : LocalDate.now());
            model.setMaBacSi("NV002"); // Mặc định
            model.setTinhTrang("Chưa khám");
            
            // ✅ Lưu vào database BenhNhan
            boolean patientSuccess = false;
            if (PatientDAO.exists(maBenhNhan)) {
                patientSuccess = PatientDAO.update(patientModel);
                System.out.println("🔄 Đã cập nhật bệnh nhân: " + maBenhNhan);
            } else {
                patientSuccess = PatientDAO.insert(patientModel);
                System.out.println("🆕 Đã thêm bệnh nhân mới: " + maBenhNhan);
            }
            
            if (patientSuccess) {
                System.out.println("✅ Đã lưu thông tin bệnh nhân: " + maBenhNhan);
            } else {
                System.err.println("❌ Lỗi khi lưu thông tin bệnh nhân");
            }
            
            // ✅ Lưu vào database HenKhamBenh
            System.out.println("🔍 Debug - Model data before save:");
            System.out.println("  - MaKhamBenh: " + model.getMaKhamBenh());
            System.out.println("  - MaBenhNhan: " + model.getMaBenhNhan());
            System.out.println("  - HoTen: " + model.getHoTen());
            System.out.println("  - LyDoKham: " + model.getLyDoKham());
            System.out.println("  - NgayKham: " + model.getNgayKham());
            System.out.println("  - MaBacSi: " + model.getMaBacSi());
            System.out.println("  - TinhTrang: " + model.getTinhTrang());
            
            // ✅ Debug chi tiết
            HenKhamBenhDAO.debugAppointmentSave(model);
            
            boolean appointmentSuccess = HenKhamBenhDAO.updateOrInsert(model);
            if (appointmentSuccess) {
                System.out.println("✅ Đã cập nhật/thêm lịch hẹn: " + model.getMaKhamBenh());
            } else {
                System.err.println("❌ Lỗi khi cập nhật/thêm lịch hẹn");
            }
            
            // ✅ Hiển thị thông báo thành công
            if (patientSuccess && appointmentSuccess) {
                showSuccessAlert("Đã lưu thông tin bệnh nhân và lịch hẹn thành công!\n" +
                    "Mã bệnh nhân: " + maBenhNhan + "\n" +
                    "Mã khám bệnh: " + maKhamBenh);
                
                // ✅ Đóng dialog
                Stage stage = (Stage) btnLuu.getScene().getWindow();
                stage.close();
            } else {
                showAlert("Có lỗi xảy ra khi lưu dữ liệu!");
            }
            
        } catch (Exception e) {
            System.err.println("❌ Lỗi trong handleLuu: " + e.getMessage());
            e.printStackTrace();
            showAlert("Có lỗi xảy ra: " + e.getMessage());
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
                // ✅ Cập nhật UI
                txtMaBenhNhan.setText(selected.getMaBenhNhan() != null ? selected.getMaBenhNhan() : "");
                txtHoTen.setText(selected.getHoTen() != null ? selected.getHoTen() : "");
                txtSoDienThoai.setText(selected.getSoDienThoai() != null ? selected.getSoDienThoai() : "");
                dateNgaySinh.setValue(selected.getNgaySinh());
                cbGioiTinh.setValue(selected.getGioiTinh() != null ? selected.getGioiTinh() : "Nam");
                
                // ✅ Cập nhật model
                model.setMaBenhNhan(selected.getMaBenhNhan() != null ? selected.getMaBenhNhan() : "");
                model.setHoTen(selected.getHoTen() != null ? selected.getHoTen() : "");
                model.setSoDienThoai(selected.getSoDienThoai() != null ? selected.getSoDienThoai() : "");
                model.setNgaySinh(selected.getNgaySinh());
                model.setGioiTinh(selected.getGioiTinh() != null ? selected.getGioiTinh() : "Nam");
                
                // ✅ Cập nhật entry title
                String hoTen = selected.getHoTen() != null ? selected.getHoTen() : "Chưa có tên";
                String lyDo = model.getLyDoKham() != null ? model.getLyDoKham() : "Chưa có lý do";
                entry.setTitle(hoTen + " - " + lyDo);
                
                // ✅ Lưu vào database
                boolean success = HenKhamBenhDAO.update(model);
                if (success) {
                    System.out.println("✅ Đã cập nhật bệnh nhân: " + selected.getMaBenhNhan());
                } else {
                    System.err.println("❌ Lỗi khi cập nhật bệnh nhân");
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleTaoBenhNhanMoi() {
        // ✅ Tạo mã bệnh nhân mới
        String newMaBenhNhan = PatientDAO.generateNewMaBenhNhan();
        txtMaBenhNhan.setText(newMaBenhNhan);
        
        // ✅ Xóa thông tin cũ để nhập thông tin mới
        txtHoTen.clear();
        txtSoDienThoai.clear();
        dateNgaySinh.setValue(LocalDate.now());
        cbGioiTinh.setValue("Nam");
        
        // ✅ Focus vào ô họ tên để nhập
        txtHoTen.requestFocus();
        
        System.out.println("✅ Đã tạo mã bệnh nhân mới: " + newMaBenhNhan);
        showSuccessAlert("Đã tạo mã bệnh nhân mới: " + newMaBenhNhan + "\nVui lòng nhập thông tin bệnh nhân.");
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Lỗi");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showSuccessAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Thành công");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
