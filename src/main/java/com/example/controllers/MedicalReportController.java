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
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.printing.PDFPageable;
import java.awt.print.PrinterJob;
import com.example.controllers.MedicineSelectionDialogController;

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
        if (report.getNgaySinh() != null) {
            tfNgaySinh.setText(report.getNgaySinh().format(fmt));
        } else {
            tfNgaySinh.setText("");
        }

        tfGioiTinh.setText(report.getGioiTinh());
        tfSoDienThoai.setText(report.getSoDienThoai());
        tfTenBacSi.setText(report.getTenBacSi());
        tfLyDoKham.setText(report.getLyDoKham());
        if (report.getNgayLap() != null) {
            tfNgayLap.setText(report.getNgayLap().toLocalDate().format(fmt));
        } else {
            tfNgayLap.setText("");
        }
        txtChanDoan.setText(report.getChanDoan());

        if (bill != null) {
            tfNgayHoaDon.setText(bill.getNgayLapDon().format(fmt));
            double tienThuoc = bill.getDanhSachThuoc().stream().mapToDouble(thuoc -> thuoc.getGiaTien() * thuoc.getSoLuong()).sum();
            tfTienThuoc.setText(String.format("%.0f", tienThuoc));
            tfTienKham.setText(String.format("%.0f", bill.getTienKham()));
            tfTongTien.setText(String.format("%.0f", bill.getTongTien()));
            if (bill.getDanhSachThuoc() != null) {
                danhSachThuoc.setAll(bill.getDanhSachThuoc());
                System.out.println(bill.getDanhSachThuoc().size());
            }
        }

        colTenThuoc.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("tenThuoc"));
        colSoLuong.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("soLuong"));
        colDonGia.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("giaTien"));
        tableThuocHoaDon.setItems(danhSachThuoc);

        setupButtons();
        applyRolePermissions();
    }

    // Phương thức mới: Load phiếu khám bệnh từ database dựa vào mã khám bệnh
    public void loadMedicalReportByMaKhamBenh(String maKhamBenh) {
        try {
            MedicalReportModel report = com.example.DAO.MedicalReportDAO.getCompleteMedicalReportByMaKhamBenh(maKhamBenh);
            if (report != null) {
                BillModel bill = report.getHoaDon();
                setData(report, bill);
            } else {
                showAlert("Lỗi", "Không tìm thấy phiếu khám bệnh với mã: " + maKhamBenh, Alert.AlertType.ERROR);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            showAlert("Lỗi", "Không thể load phiếu khám bệnh: " + ex.getMessage(), Alert.AlertType.ERROR);
        }
    }

    // Phương thức mới: Refresh danh sách thuốc từ database
    public void refreshMedicineList() {
        try {
            String maPhieuKham = tfMaPhieuKham.getText();
            if (maPhieuKham != null && !maPhieuKham.trim().isEmpty()) {
                // Lấy lại thông tin hóa đơn từ database
                String maHoaDon = getMaHoaDonByMaPhieuKham(maPhieuKham);
                if (maHoaDon != null) {
                    BillModel bill = com.example.DAO.BillDAO.getBillById(maHoaDon);
                    if (bill != null && bill.getDanhSachThuoc() != null) {
                        danhSachThuoc.setAll(bill.getDanhSachThuoc());
                        // Cập nhật tổng tiền từ database thay vì tính toán local
                        tfTienThuoc.setText(String.format("%.0f", bill.getDanhSachThuoc().stream()
                                .mapToDouble(t -> t.getSoLuong() * t.getGiaTien()).sum()));
                        try {
                            double tienKham = Double.parseDouble(tfTienKham.getText());
                            tfTongTien.setText(String.format("%.0f", bill.getTongTien()));
                        } catch (NumberFormatException ex) {
                            tfTongTien.setText(String.format("%.0f", bill.getTongTien()));
                        }
                    } else {
                        // Nếu không có thuốc, reset về 0
                        danhSachThuoc.clear();
                        updateTongTien();
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            showAlert("Lỗi", "Không thể refresh danh sách thuốc: " + ex.getMessage(), Alert.AlertType.ERROR);
        }
    }

    // Phương thức hỗ trợ: Lấy mã hóa đơn từ mã phiếu khám
    private String getMaHoaDonByMaPhieuKham(String maPhieuKham) {
        String sql = "SELECT MaHoaDon FROM HoaDon WHERE MaPhieuKham = ?";
        try (java.sql.Connection conn = com.example.utils.DatabaseConnector.connect();
             java.sql.PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, maPhieuKham);
            java.sql.ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getString("MaHoaDon");
            }
        } catch (java.sql.SQLException e) {
            System.err.println("Lỗi khi lấy mã hóa đơn: " + e.getMessage());
        }
        return null;
    }

    private void setupButtons() {
        btnThemThuoc.setOnAction(event -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/medicine_selection_dialog.fxml"));
                Parent root = loader.load();
                MedicineSelectionDialogController controller = loader.getController();
                
                // Set callback để nhận thuốc được chọn
                controller.setOnMedicineSelected(medicine -> {
                    // Lưu thuốc vào database
                    String maPhieuKham = tfMaPhieuKham.getText();
                    if (maPhieuKham != null && !maPhieuKham.trim().isEmpty()) {
                        boolean success = com.example.DAO.BillDAO.addMedicineToExistingDonThuoc(maPhieuKham, medicine);
                        if (success) {
                            // Refresh danh sách thuốc từ database để đảm bảo dữ liệu đồng bộ
                            refreshMedicineList();
                            // Cập nhật tổng tiền trên giao diện
                            updateTongTien();
                            showAlert("Thành công", "Đã thêm thuốc vào hóa đơn và lưu vào database!", Alert.AlertType.INFORMATION);
                        } else {
                            showAlert("Lỗi", "Không thể lưu thuốc vào database!", Alert.AlertType.ERROR);
                        }
                    } else {
                        showAlert("Lỗi", "Không có mã phiếu khám để lưu thuốc!", Alert.AlertType.ERROR);
                    }
                });
                
                Stage dialogStage = new Stage();
                dialogStage.setTitle("Chọn thuốc từ kho");
                dialogStage.initModality(Modality.APPLICATION_MODAL);
                dialogStage.setScene(new Scene(root));
                dialogStage.showAndWait();
            } catch (IOException ex) {
                ex.printStackTrace();
                showAlert("Lỗi", "Không thể mở cửa sổ chọn thuốc: " + ex.getMessage(), Alert.AlertType.ERROR);
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
                        reportSuccess = com.example.DAO.MedicalReportDAO.insertPhieuKhamBenh(report, now, now, "", "");
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
                Stage stage = (Stage) btnLuuPhieu.getScene().getWindow();
                stage.close();
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
                    try {
                        PDDocument document = PDDocument.load(pdfFile);
                        PrinterJob job = PrinterJob.getPrinterJob();
                        job.setPageable(new PDFPageable(document));
                        if (job.printDialog()) {
                            job.print();
                        }
                        document.close();
                    } catch (Exception ex) {
                        // Nếu in lỗi, fallback mở file PDF bằng Edge hoặc mặc định
                        try {
                            String edgePath = "C:/Program Files (x86)/Microsoft/Edge/Application/msedge.exe";
                            if (!new File(edgePath).exists()) {
                                edgePath = "C:/Program Files/Microsoft/Edge/Application/msedge.exe";
                            }
                            if (new File(edgePath).exists()) {
                                new ProcessBuilder(edgePath, pdfFile.getAbsolutePath()).start();
                            } else {
                                Desktop.getDesktop().open(pdfFile);
                            }
                        } catch (Exception e2) {
                            showAlert("Lỗi in phiếu", "Không thể in hoặc mở file PDF: " + e2.getMessage(), Alert.AlertType.ERROR);
                        }
                    }
                } else {
                    showAlert("Lỗi in phiếu", "Chưa có file PDF phiếu khám. Hãy xuất PDF trước!", Alert.AlertType.ERROR);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                showAlert("Lỗi in phiếu", "Không thể in file PDF: " + ex.getMessage(), Alert.AlertType.ERROR);
            }
        });

        btnXuatPdf.setOnAction(eventXuat -> {
            try {
                String fileName = "PhieuKham_" + tfMaPhieuKham.getText() + ".pdf";
                File pdfFile = new File(System.getProperty("user.home"), fileName);

                // 1. Tạo HTML động
                StringBuilder html = new StringBuilder();
                html.append("<html><head>");
                html.append("<style>");
                html.append("body { font-family: 'Times New Roman', serif; }\n");
                html.append("h1 { text-align: center; font-size: 22px; }\n");
                html.append("table { width: 100%; border-collapse: collapse; margin-top: 10px; }\n");
                html.append("th, td { border: 1px solid #333; padding: 6px; text-align: center; }\n");
                html.append("th { background: #eee; }\n");
                html.append(".right { text-align: right; }\n");
                html.append("</style>");
                html.append("</head><body>");
                html.append("<h1>PHIẾU KHÁM BỆNH</h1>");
                html.append("<p><b>Mã phiếu khám:</b> ").append(tfMaPhieuKham.getText()).append("</p>");
                html.append("<p><b>Mã bệnh nhân:</b> ").append(tfMaBenhNhan.getText()).append("</p>");
                html.append("<p><b>Họ tên:</b> ").append(tfHoTen.getText()).append("</p>");
                html.append("<p><b>Ngày sinh:</b> ").append(tfNgaySinh.getText()).append("</p>");
                html.append("<p><b>Giới tính:</b> ").append(tfGioiTinh.getText()).append("</p>");
                html.append("<p><b>Số điện thoại:</b> ").append(tfSoDienThoai.getText()).append("</p>");
                html.append("<p><b>Bác sĩ:</b> ").append(tfTenBacSi.getText()).append("</p>");
                html.append("<p><b>Lý do khám:</b> ").append(tfLyDoKham.getText()).append("</p>");
                html.append("<p><b>Ngày lập:</b> ").append(tfNgayLap.getText()).append("</p>");
                html.append("<p><b>Chẩn đoán:</b> ").append(txtChanDoan.getText()).append("</p>");
                html.append("<h3 style='text-align:center;'>--- DANH SÁCH THUỐC ---</h3>");
                html.append("<table>");
                html.append("<tr><th>Tên thuốc</th><th>Số lượng</th><th>Đơn giá</th></tr>");
                for (MedicineModel thuoc : danhSachThuoc) {
                    html.append("<tr>");
                    html.append("<td>").append(thuoc.getTenThuoc()).append("</td>");
                    html.append("<td>").append(thuoc.getSoLuong()).append("</td>");
                    html.append("<td>").append(String.format("%.0f", thuoc.getGiaTien())).append("</td>");
                    html.append("</tr>");
                }
                html.append("</table>");
                html.append("<p class='right'><b>Tiền thuốc:</b> ").append(tfTienThuoc.getText()).append("</p>");
                html.append("<p class='right'><b>Tiền khám:</b> ").append(tfTienKham.getText()).append("</p>");
                html.append("<p class='right'><b>Tổng tiền:</b> ").append(tfTongTien.getText()).append("</p>");
                html.append("<br><div style='text-align:right;'>");
                html.append("Người lập phiếu: ____________________<br>");
                html.append("Ngày ....... tháng ....... năm .......");
                html.append("</div>");
                html.append("</body></html>");

                // 2. Chuyển HTML sang PDF
                com.itextpdf.html2pdf.HtmlConverter.convertToPdf(html.toString(), new java.io.FileOutputStream(pdfFile));

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