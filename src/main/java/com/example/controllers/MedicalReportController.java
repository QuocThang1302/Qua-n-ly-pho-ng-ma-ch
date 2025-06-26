package com.example.controllers;

import com.example.model.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.time.format.DateTimeFormatter;

public class MedicalReportController {

    @FXML private TextField tfMaPhieuKham, tfMaBenhNhan, tfHoTen, tfNgaySinh, tfGioiTinh,
            tfSoDienThoai, tfTenBacSi, tfLyDoKham, tfNgayLap;

    @FXML private TextArea txtChanDoan;

    @FXML private TextField tfNgayHoaDon, tfTienThuoc, tfTienKham, tfTongTien;

    @FXML private TableView<MedicineModel> tableThuocHoaDon;
    @FXML private TableColumn<MedicineModel, String> colTenThuoc;
    @FXML private TableColumn<MedicineModel, Integer> colSoLuong;
    @FXML private TableColumn<MedicineModel, Double> colDonGia;

    @FXML private Button btnThemThuoc, btnLuuPhieu, btnInPhieu, btnXuatPdf;

    private ObservableList<MedicineModel> danhSachThuoc = FXCollections.observableArrayList();
    private final DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public void setData(MedicalReportModel report, BillModel bill) {
        colTenThuoc.prefWidthProperty().bind(tableThuocHoaDon.widthProperty().multiply(0.33));
        colDonGia.prefWidthProperty().bind(tableThuocHoaDon.widthProperty().multiply(0.33));
        colSoLuong.prefWidthProperty().bind(tableThuocHoaDon.widthProperty().multiply(0.33));

        tfMaPhieuKham.setText(report.getMaPhieuKham());
        tfMaBenhNhan.setText(report.getMaBenhNhan());
        tfHoTen.setText(report.getHoTen());
        tfNgaySinh.setText(report.getNgaySinh().format(fmt));
        tfGioiTinh.setText(report.getGioiTinh());
        tfSoDienThoai.setText(report.getSoDienThoai());
        tfTenBacSi.setText(report.getTenBacSi());
        tfLyDoKham.setText(report.getLyDoKham());
        tfNgayLap.setText(report.getNgayLap().format(fmt));
        txtChanDoan.setText(report.getChanDoan());

        if (bill != null) {
            tfNgayHoaDon.setText(bill.getNgayLapDon().format(fmt));
            double tienThuoc = 0;
            for(MedicineModel thuoc : bill.getDanhSachThuoc()){
                tienThuoc += thuoc.getGiaTien();
            }
            tfTienThuoc.setText(String.format("%.0f", tienThuoc));
            tfTienKham.setText(String.format("%.0f", bill.getTienKham()));
            tfTongTien.setText(String.format("%.0f", bill.getTongTien()));
            if (bill.getDanhSachThuoc() != null) {
                danhSachThuoc.setAll(bill.getDanhSachThuoc());
            }
        }

        colTenThuoc.setCellValueFactory(new PropertyValueFactory<>("tenThuoc"));
        colSoLuong.setCellValueFactory(new PropertyValueFactory<>("soLuong"));
        colDonGia.setCellValueFactory(new PropertyValueFactory<>("donGia"));
        tableThuocHoaDon.setItems(danhSachThuoc);

        setupButtons();
        applyRolePermissions();
    }

    private void setupButtons() {
        btnThemThuoc.setOnAction(e -> {
            MedicineModel thuocMoi = new MedicineModel("T004", "Thuốc mới", "Chống mỏi", 1, 10000, "viên", "Uống khi cần");
            danhSachThuoc.add(thuocMoi);
            updateTongTien();
        });

        btnLuuPhieu.setOnAction(e -> {
            try {
                // Lấy dữ liệu từ các trường giao diện
                MedicalReportModel report = new MedicalReportModel();
                report.setMaPhieuKham(tfMaPhieuKham.getText());
                report.setMaBenhNhan(tfMaBenhNhan.getText());
                report.setHoTen(tfHoTen.getText());
                report.setNgaySinh(java.time.LocalDate.parse(tfNgaySinh.getText(), fmt));
                report.setGioiTinh(tfGioiTinh.getText());
                report.setSoDienThoai(tfSoDienThoai.getText());
                report.setTenBacSi(tfTenBacSi.getText());
                report.setLyDoKham(tfLyDoKham.getText());
                report.setChanDoan(txtChanDoan.getText());
                java.time.LocalDateTime now = java.time.LocalDateTime.now();
                report.setNgayKham(now);
                double tienKham = Double.parseDouble(tfTienKham.getText());

                // Kiểm tra phiếu khám đã tồn tại chưa
                boolean reportSuccess = false;
                boolean isUpdate = false;
                try {
                    MedicalReportModel existing = com.example.DAO.MedicalReportDAO.getByMaPhieuKham(tfMaPhieuKham.getText());
                    if (existing == null) {
                        // Thêm mới
                        reportSuccess = com.example.DAO.MedicalReportDAO.insertPhieuKhamBenh(report, now, now, "", "", tienKham);
                    } else {
                        // Cập nhật
                        reportSuccess = com.example.DAO.MedicalReportDAO.updatePhieuKhamBenh(report, now, now, "", "", tienKham);
                        isUpdate = true;
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }

                // Lưu hóa đơn (nếu có)
                BillModel bill = new BillModel();
                bill.setMaHoaDon("HD" + System.currentTimeMillis());
                bill.setMaPhieuKham(tfMaPhieuKham.getText());
                bill.setTienKham(tienKham);
                bill.setTongTien(Double.parseDouble(tfTongTien.getText()));
                bill.setDanhSachThuoc(danhSachThuoc);
                bill.setNgayLapDon(now);
                bill.setTrangThai("Đã lưu");
                boolean billSuccess = com.example.DAO.BillDAO.insertBill(bill, "Hóa đơn khám bệnh", now);

                if (reportSuccess && billSuccess) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Thành công");
                    alert.setHeaderText(null);
                    alert.setContentText((isUpdate ? "Cập nhật" : "Lưu mới") + " phiếu khám và hóa đơn thành công!");
                    alert.showAndWait();
                } else {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Thất bại");
                    alert.setHeaderText(null);
                    alert.setContentText("Lưu phiếu khám hoặc hóa đơn thất bại! Vui lòng kiểm tra lại dữ liệu hoặc kết nối.");
                    alert.showAndWait();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Lỗi");
                alert.setHeaderText(null);
                alert.setContentText("Lưu phiếu khám thất bại! Vui lòng kiểm tra lại dữ liệu hoặc kết nối.\n" + ex.getMessage());
                alert.showAndWait();
            }
        });

        btnInPhieu.setOnAction(e -> {
            System.out.println("🖨 In phiếu...");
        });

        btnXuatPdf.setOnAction(e -> {
            System.out.println("📄 Xuất PDF...");
        });
    }

    private void updateTongTien() {
        double tongTien = danhSachThuoc.stream()
                .mapToDouble(t -> t.getSoLuong() * t.getGiaTien())
                .sum();
        tfTienThuoc.setText(String.format("%.0f", tongTien));
        try {
            double tienKham = Double.parseDouble(tfTienKham.getText());
            tfTongTien.setText(String.format("%.0f", tongTien + tienKham));
        } catch (NumberFormatException e) {
            tfTongTien.setText(String.format("%.0f", tongTien));
        }
    }

    private void applyRolePermissions() {
        Role role = UserContext.getInstance().getRole();
        boolean editable = role == Role.DOCTOR;
        txtChanDoan.setEditable(editable);
        btnThemThuoc.setVisible(!editable);
        btnLuuPhieu.setVisible(!editable);
    }
}
