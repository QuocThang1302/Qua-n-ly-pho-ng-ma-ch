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
    }

    public void update(ActionEvent actionEvent) {
    }

    public void delete(ActionEvent actionEvent) {

    }
}
