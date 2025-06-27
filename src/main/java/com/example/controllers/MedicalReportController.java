package com.example.controllers;

import com.example.model.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Cell;
import java.io.File;

import java.time.format.DateTimeFormatter;
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import java.awt.Desktop;

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
            try {
                String fileName = "PhieuKham_" + tfMaPhieuKham.getText() + ".pdf";
                File pdfFile = new File(System.getProperty("user.home"), fileName);
                if (pdfFile.exists()) {
                    if (Desktop.isDesktopSupported()) {
                        Desktop.getDesktop().open(pdfFile);
                    } else {
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Lỗi in phiếu");
                        alert.setHeaderText(null);
                        alert.setContentText("Không hỗ trợ mở file PDF trên hệ điều hành này.");
                        alert.showAndWait();
                    }
                } else {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Lỗi in phiếu");
                    alert.setHeaderText(null);
                    alert.setContentText("Chưa có file PDF phiếu khám. Hãy xuất PDF trước khi in!");
                    alert.showAndWait();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Lỗi in phiếu");
                alert.setHeaderText(null);
                alert.setContentText("Không thể mở file PDF: " + ex.getMessage());
                alert.showAndWait();
            }
        });

        btnXuatPdf.setOnAction(e -> {
            try {
                String fileName = "PhieuKham_" + tfMaPhieuKham.getText() + ".pdf";
                File pdfFile = new File(System.getProperty("user.home"), fileName);
                PdfWriter writer = new PdfWriter(pdfFile.getAbsolutePath());
                PdfDocument pdf = new PdfDocument(writer);
                Document document = new Document(pdf);

                // Load Times New Roman font (Unicode)
                String fontPath = "src/main/resources/assets/Times New Roman.ttf";
                PdfFont font = PdfFontFactory.createFont(fontPath, PdfEncodings.IDENTITY_H);

                document.setFont(font);

                document.add(new Paragraph("PHIẾU KHÁM BỆNH").setFont(font).setBold().setFontSize(16));
                document.add(new Paragraph("Mã phiếu khám: " + tfMaPhieuKham.getText()).setFont(font));
                document.add(new Paragraph("Mã bệnh nhân: " + tfMaBenhNhan.getText()).setFont(font));
                document.add(new Paragraph("Họ tên: " + tfHoTen.getText()).setFont(font));
                document.add(new Paragraph("Ngày sinh: " + tfNgaySinh.getText()).setFont(font));
                document.add(new Paragraph("Giới tính: " + tfGioiTinh.getText()).setFont(font));
                document.add(new Paragraph("Số điện thoại: " + tfSoDienThoai.getText()).setFont(font));
                document.add(new Paragraph("Bác sĩ: " + tfTenBacSi.getText()).setFont(font));
                document.add(new Paragraph("Lý do khám: " + tfLyDoKham.getText()).setFont(font));
                document.add(new Paragraph("Ngày lập: " + tfNgayLap.getText()).setFont(font));
                document.add(new Paragraph("Chẩn đoán: " + txtChanDoan.getText()).setFont(font));
                document.add(new Paragraph(" ").setFont(font));
                document.add(new Paragraph("--- DANH SÁCH THUỐC ---").setFont(font));

                Table table = new Table(new float[]{4, 2, 3});
                table.addHeaderCell(new Cell().add(new Paragraph("Tên thuốc").setFont(font)));
                table.addHeaderCell(new Cell().add(new Paragraph("Số lượng").setFont(font)));
                table.addHeaderCell(new Cell().add(new Paragraph("Đơn giá").setFont(font)));
                for (MedicineModel thuoc : danhSachThuoc) {
                    table.addCell(new Cell().add(new Paragraph(thuoc.getTenThuoc()).setFont(font)));
                    table.addCell(new Cell().add(new Paragraph(String.valueOf(thuoc.getSoLuong())).setFont(font)));
                    table.addCell(new Cell().add(new Paragraph(String.format("%.0f", thuoc.getGiaTien())).setFont(font)));
                }
                document.add(table);
                document.add(new Paragraph(" ").setFont(font));
                document.add(new Paragraph("Tiền thuốc: " + tfTienThuoc.getText()).setFont(font));
                document.add(new Paragraph("Tiền khám: " + tfTienKham.getText()).setFont(font));
                document.add(new Paragraph("Tổng tiền: " + tfTongTien.getText()).setFont(font));

                document.close();
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Xuất PDF thành công");
                alert.setHeaderText(null);
                alert.setContentText("Đã xuất phiếu khám ra file: " + pdfFile.getAbsolutePath());
                alert.showAndWait();
            } catch (Exception ex) {
                ex.printStackTrace();
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Lỗi xuất PDF");
                alert.setHeaderText(null);
                alert.setContentText("Không thể xuất PDF: " + ex.getMessage());
                alert.showAndWait();
            }
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
