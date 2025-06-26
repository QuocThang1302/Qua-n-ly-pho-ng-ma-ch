package com.example.controllers;

import com.example.model.MedicineModel;
import com.example.model.StaffModel;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.StringConverter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class MedicineDetailController {
    @FXML
    private TextField tfId, tfUse, tfName, tfQuantity, tfCost, tfGuide;
    @FXML
    private ComboBox<String> cbUnit;
    @FXML
    private Button btnAdd, btnUpdate, btnDelete;

    @FXML
    public void initialize() {
        // code của button
        btnAdd.setVisible(true);
        btnAdd.setManaged(true);

        btnUpdate.setVisible(false);
        btnUpdate.setManaged(false);

        btnDelete.setVisible(false);
        btnDelete.setManaged(false);

        // code của combo box
        cbUnit.getItems().addAll( "viên",
                "vỉ",
                "gói",
                "ống",
                "chai",
                "lọ",
                "tuýp",
                "ml",
                "mg",
                "g",
                "mcg",
                "IU");
    }

    public void setMedicine(MedicineModel medicineModel) {
        if (medicineModel != null) {
            btnAdd.setVisible(false);
            btnAdd.setManaged(false);

            btnUpdate.setVisible(true);
            btnUpdate.setManaged(true);

            btnDelete.setVisible(true);
            btnDelete.setManaged(true);

            tfId.setText(medicineModel.getMaThuoc());
            tfUse.setText(medicineModel.getCongDung());
            tfName.setText(medicineModel.getTenThuoc());
            tfQuantity.setText(String.valueOf(medicineModel.getSoLuong()));
            tfCost.setText(String.valueOf(medicineModel.getGiaTien()));
            tfGuide.setText(medicineModel.getHuongDanSuDung());
            cbUnit.getEditor().setText(medicineModel.getDonVi());
        }
    }

    public void add(ActionEvent actionEvent) {
        try {
            String id = tfId.getText();
            String ten = tfName.getText();
            String congDung = tfUse.getText();
            int soLuong = Integer.parseInt(tfQuantity.getText());
            double giaTien = Double.parseDouble(tfCost.getText());
            String donVi = cbUnit.getEditor().getText();
            String huongDan = tfGuide.getText();
            MedicineModel medicine = new MedicineModel(id, ten, congDung, soLuong, giaTien, donVi, huongDan);
            boolean success = false;
            String errorMsg = null;
            try {
                success = com.example.DAO.MedicineDAO.insertMedicine(medicine, java.time.LocalDate.now());
            } catch (Exception ex) {
                Throwable cause = ex;
                while (cause.getCause() != null) cause = cause.getCause();
                if (cause instanceof java.sql.SQLIntegrityConstraintViolationException) {
                    String msg = cause.getMessage();
                    if (msg.contains("PRIMARY") || msg.contains("MaThuoc")) {
                        errorMsg = "Mã thuốc đã tồn tại!";
                    } else {
                        errorMsg = "Dữ liệu bị trùng lặp!";
                    }
                } else {
                    errorMsg = cause.getMessage();
                }
            }
            if (success) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Thành công");
                alert.setHeaderText(null);
                alert.setContentText("Thêm thuốc thành công!");
                alert.showAndWait();
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Thất bại");
                alert.setHeaderText(null);
                alert.setContentText(errorMsg != null ? errorMsg : "Thêm thuốc thất bại! Vui lòng kiểm tra lại thông tin nhập vào.");
                alert.showAndWait();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Lỗi");
            alert.setHeaderText(null);
            alert.setContentText("Vui lòng kiểm tra lại thông tin nhập vào!\n" + e.getMessage());
            alert.showAndWait();
        }
    }

    public void update(ActionEvent actionEvent) {
        try {
            String id = tfId.getText();
            String ten = tfName.getText();
            String congDung = tfUse.getText();
            int soLuong = Integer.parseInt(tfQuantity.getText());
            double giaTien = Double.parseDouble(tfCost.getText());
            String donVi = cbUnit.getEditor().getText();
            String huongDan = tfGuide.getText();
            MedicineModel medicine = new MedicineModel(id, ten, congDung, soLuong, giaTien, donVi, huongDan);
            boolean success = false;
            String errorMsg = null;
            try {
                com.example.DAO.MedicineDAO.updateMedicine(medicine);
                success = true;
            } catch (Exception ex) {
                Throwable cause = ex;
                while (cause.getCause() != null) cause = cause.getCause();
                if (cause instanceof java.sql.SQLIntegrityConstraintViolationException) {
                    String msg = cause.getMessage();
                    if (msg.contains("MaThuoc")) {
                        errorMsg = "Mã thuốc bị trùng!";
                    } else {
                        errorMsg = "Dữ liệu bị trùng lặp!";
                    }
                } else {
                    errorMsg = cause.getMessage();
                }
            }
            if (success) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Thành công");
                alert.setHeaderText(null);
                alert.setContentText("Cập nhật thuốc thành công!");
                alert.showAndWait();
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Thất bại");
                alert.setHeaderText(null);
                alert.setContentText(errorMsg != null ? errorMsg : "Cập nhật thuốc thất bại! Vui lòng kiểm tra lại thông tin nhập vào.");
                alert.showAndWait();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Lỗi");
            alert.setHeaderText(null);
            alert.setContentText("Vui lòng kiểm tra lại thông tin nhập vào!\n" + e.getMessage());
            alert.showAndWait();
        }
    }

    public void delete(ActionEvent actionEvent) {
        try {
            String id = tfId.getText();
            boolean success = false;
            String errorMsg = null;
            try {
                success = com.example.DAO.MedicineDAO.deleteMedicine(id);
            } catch (Exception ex) {
                Throwable cause = ex;
                while (cause.getCause() != null) cause = cause.getCause();
                if (cause instanceof java.sql.SQLIntegrityConstraintViolationException) {
                    String msg = cause.getMessage();
                    if (msg.contains("ctdonthuoc") || msg.contains("mathuoc")) {
                        errorMsg = "Không thể xóa thuốc với mã " + id + " vì đang được tham chiếu trong bảng chi tiết đơn thuốc (ctdonthuoc).\n" +
                                   "Vui lòng xóa hoặc cập nhật các bản ghi liên quan trong bảng chi tiết đơn thuốc trước khi xóa thuốc này.";
                    } else if (msg.contains("foreign key")) {
                        errorMsg = "Không thể xóa thuốc vì đang được sử dụng ở bảng khác!";
                    } else {
                        errorMsg = "Lỗi ràng buộc dữ liệu!";
                    }
                } else {
                    errorMsg = cause.getMessage();
                }
            }
            if (success) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Thành công");
                alert.setHeaderText(null);
                alert.setContentText("Xóa thuốc thành công!");
                alert.showAndWait();
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Thất bại");
                alert.setHeaderText(null);
                alert.setContentText(errorMsg != null ? errorMsg : "Không thể xóa thuốc với mã " + id + " vì đang được tham chiếu trong bảng chi tiết đơn thuốc (ctdonthuoc).\n" +
                        "Vui lòng xóa hoặc cập nhật các bản ghi liên quan trong bảng chi tiết đơn thuốc trước khi xóa thuốc này.");
                alert.showAndWait();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Lỗi");
            alert.setHeaderText(null);
            alert.setContentText("Vui lòng kiểm tra lại!\n" + e.getMessage());
            alert.showAndWait();
        }
    }
}
