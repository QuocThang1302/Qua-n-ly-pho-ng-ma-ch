package com.example.controllers;

import com.example.model.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Cell;
import java.io.File;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import java.awt.Desktop;

public class MedicalReportController implements MedicineDataChangeListener {

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
            double tienThuoc = bill.getDanhSachThuoc().stream().mapToDouble(thuoc -> thuoc.getGiaTien() * thuoc.getSoLuong()).sum();
            tfTienThuoc.setText(String.format("%.0f", tienThuoc));
            tfTienKham.setText(String.format("%.0f", bill.getTienKham()));
            tfTongTien.setText(String.format("%.0f", bill.getTongTien()));
            if (bill.getDanhSachThuoc() != null) {
                danhSachThuoc.setAll(bill.getDanhSachThuoc());
            }
        }

        colTenThuoc.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("tenThuoc"));
        colSoLuong.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("soLuong"));
        colDonGia.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("giaTien"));
        tableThuocHoaDon.setItems(danhSachThuoc);

        setupButtons();
        applyRolePermissions();
    }

    private void setupButtons() {
        btnThemThuoc.setOnAction(event -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/medicine_detail.fxml"));
                Parent root = loader.load();
                MedicineDetailController controller = loader.getController();
                controller.setDataChangeListener(this);
                Stage dialogStage = new Stage();
                dialogStage.setTitle("Thêm thuốc");
                dialogStage.initModality(Modality.APPLICATION_MODAL);
                dialogStage.setScene(new Scene(root));
                dialogStage.showAndWait();
            } catch (IOException ex) {
                ex.printStackTrace();
                showAlert("Lỗi", "Không thể mở cửa sổ thêm thuốc: " + ex.getMessage(), Alert.AlertType.ERROR);
            }
        });

        btnLuuPhieu.setOnAction(eventLuu -> {
            try {
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
                if (tienKham < 0) throw new IllegalArgumentException("Tiền khám không thể âm!");

                boolean reportSuccess = false;
                boolean isUpdate = false;
                try {
                    MedicalReportModel existing = com.example.DAO.MedicalReportDAO.getByMaPhieuKham(tfMaPhieuKham.getText());
                    if (existing == null) {
                        reportSuccess = com.example.DAO.MedicalReportDAO.insertPhieuKhamBenh(report, now, now, "", "", tienKham);
                    } else {
                        reportSuccess = com.example.DAO.MedicalReportDAO.updatePhieuKhamBenh(report, now, now, "", "", tienKham);
                        isUpdate = true;
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    showAlert("Lỗi", "Lưu phiếu khám thất bại: " + ex.getMessage(), Alert.AlertType.ERROR);
                    return;
                }

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
                    showAlert("Thành công", (isUpdate ? "Cập nhật" : "Lưu mới") + " phiếu khám và hóa đơn thành công!", Alert.AlertType.INFORMATION);
                } else {
                    showAlert("Thất bại", "Lưu phiếu khám hoặc hóa đơn thất bại!", Alert.AlertType.ERROR);
                }
            } catch (NumberFormatException ex) {
                showAlert("Lỗi", "Vui lòng nhập tiền khám hợp lệ!", Alert.AlertType.ERROR);
            } catch (IllegalArgumentException ex) {
                showAlert("Lỗi", ex.getMessage(), Alert.AlertType.ERROR);
            } catch (Exception ex) {
                ex.printStackTrace();
                showAlert("Lỗi", "Lưu phiếu khám thất bại: " + ex.getMessage(), Alert.AlertType.ERROR);
            }
        });

        btnInPhieu.setOnAction(eventIn -> {
            try {
                String fileName = "PhieuKham_" + tfMaPhieuKham.getText() + ".pdf";
                File pdfFile = new File(System.getProperty("user.home"), fileName);
                if (pdfFile.exists()) {
                    if (Desktop.isDesktopSupported()) {
                        Desktop.getDesktop().open(pdfFile);
                    } else {
                        showAlert("Lỗi in phiếu", "Không hỗ trợ mở file PDF trên hệ điều hành này.", Alert.AlertType.ERROR);
                    }
                } else {
                    showAlert("Lỗi in phiếu", "Chưa có file PDF phiếu khám. Hãy xuất PDF trước!", Alert.AlertType.ERROR);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                showAlert("Lỗi in phiếu", "Không thể mở file PDF: " + ex.getMessage(), Alert.AlertType.ERROR);
            }
        });

        btnXuatPdf.setOnAction(eventXuat -> {
            try {
                String fileName = "PhieuKham_" + tfMaPhieuKham.getText() + ".pdf";
                File pdfFile = new File(System.getProperty("user.home"), fileName);
                PdfWriter writer = new PdfWriter(pdfFile.getAbsolutePath());
                PdfDocument pdf = new PdfDocument(writer);
                Document document = new Document(pdf);
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
                showAlert("Xuất PDF thành công", "Đã xuất phiếu khám ra file: " + pdfFile.getAbsolutePath(), Alert.AlertType.INFORMATION);
            } catch (Exception ex) {
                ex.printStackTrace();
                showAlert("Lỗi xuất PDF", "Không thể xuất PDF: " + ex.getMessage(), Alert.AlertType.ERROR);
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
        } catch (NumberFormatException ex) {
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

    @Override
    public void onDataChanged(MedicineModel updatedMedicine, String action) {
        if ("INSERT".equals(action)) {
            danhSachThuoc.add(updatedMedicine);
            updateTongTien();
        }
        // Không xử lý UPDATE hoặc DELETE vì MedicalReportController chỉ cần thêm thuốc mới
    }

    private void showAlert(String title, String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}