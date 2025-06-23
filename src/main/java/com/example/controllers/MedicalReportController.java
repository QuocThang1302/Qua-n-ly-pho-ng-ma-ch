package com.example.controllers;

import com.example.model.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.time.format.DateTimeFormatter;

public class MedicalReportController {

    @FXML private Label lblMaPhieuKham, lblMaBenhNhan, lblHoTen, lblNgaySinh, lblGioiTinh,
            lblSoDienThoai, lblTenBacSi, lblLyDoKham, lblNgayLap;

    @FXML private TextArea txtChanDoan;

    @FXML private Label lblNgayHoaDon, lblTienThuoc, lblTienKham, lblTongTien;

    @FXML private TableView<MedicineModel> tableThuocHoaDon;
    @FXML private TableColumn<MedicineModel, String> colTenThuoc;
    @FXML private TableColumn<MedicineModel, Integer> colSoLuong;
    @FXML private TableColumn<MedicineModel, Double> colDonGia;

    @FXML private Button btnThemThuoc, btnLuuPhieu, btnInPhieu, btnXuatPdf;

    private ObservableList<MedicineModel> danhSachThuoc = FXCollections.observableArrayList();
    private final DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public void setData(MedicalReportModel report, BillModel bill) {
        lblMaPhieuKham.setText(report.getMaPhieuKham());
        lblMaBenhNhan.setText(report.getMaBenhNhan());
        lblHoTen.setText(report.getHoTen());
        lblNgaySinh.setText(report.getNgaySinh().format(fmt));
        lblGioiTinh.setText(report.getGioiTinh());
        lblSoDienThoai.setText(report.getSoDienThoai());
        lblTenBacSi.setText(report.getTenBacSi());
        lblLyDoKham.setText(report.getLyDoKham());
        lblNgayLap.setText(report.getNgayLap().format(fmt));
        txtChanDoan.setText(report.getChanDoan());

        if (bill != null) {
            lblNgayHoaDon.setText(bill.getNgayLapDon().format(fmt));
            double tienThuoc = 0;
            for(MedicineModel thuoc : bill.getDanhSachThuoc()){
                tienThuoc += thuoc.getGiaTien();
            }
            lblTienThuoc.setText(String.format("%.0f", tienThuoc));
            lblTienKham.setText(String.format("%.0f", bill.getTienKham()));
            lblTongTien.setText(String.format("%.0f", bill.getTongTien()));
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
            System.out.println("💾 Phiếu khám đã được lưu.");
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
        lblTienThuoc.setText(String.format("%.0f", tongTien));
        try {
            double tienKham = Double.parseDouble(lblTienKham.getText());
            lblTongTien.setText(String.format("%.0f", tongTien + tienKham));
        } catch (NumberFormatException e) {
            lblTongTien.setText(String.format("%.0f", tongTien));
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
