package com.example.controllers;

import com.example.model.MedicineModel;
import com.example.model.PatientModel;
import com.example.model.PatientReportModel;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.time.LocalDate;

public class MonthlyReportController {
    @FXML
    private Label lblPatient, lblMedicine;
    @FXML
    private ComboBox<Integer> cbMonthPatient, cbMonthMedicine, cbYearPatient, cbYearMedicine;
    @FXML
    private TableView<MedicineModel> tvMedicine;
    @FXML
    private TableColumn<MedicineModel,String> numberMedicineCol, medicineCol, unitCol, quantityCol, useCol;
    @FXML
    private TableView<PatientReportModel> tvPatient;
    @FXML
    private TableColumn<PatientReportModel,String> numberPatientCol, patientCountCol, dateCol, revenueCol, rateCol;

    @FXML
    private void initialize() {
        // tableview của patient
        numberPatientCol.prefWidthProperty().bind(tvPatient.widthProperty().multiply(0.2));
        patientCountCol.prefWidthProperty().bind(tvPatient.widthProperty().multiply(0.2));
        dateCol.prefWidthProperty().bind(tvPatient.widthProperty().multiply(0.2));
        revenueCol.prefWidthProperty().bind(tvPatient.widthProperty().multiply(0.2));
        rateCol.prefWidthProperty().bind(tvPatient.widthProperty().multiply(0.2));

        // combobox của patient
        cbMonthPatient.getItems().addAll(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12);
        cbMonthPatient.getSelectionModel().select(LocalDate.now().getMonthValue() - 1);

        int currentYear = LocalDate.now().getYear();
        for (int i = 2015; i <= currentYear; i++) {
            cbYearPatient.getItems().add(i);
        }
        cbYearPatient.getSelectionModel().select(Integer.valueOf(currentYear));

        // tableview của medicine
        numberMedicineCol.prefWidthProperty().bind(tvMedicine.widthProperty().multiply(0.2));
        medicineCol.prefWidthProperty().bind(tvMedicine.widthProperty().multiply(0.2));
        unitCol.prefWidthProperty().bind(tvMedicine.widthProperty().multiply(0.2));
        quantityCol.prefWidthProperty().bind(tvMedicine.widthProperty().multiply(0.2));
        useCol.prefWidthProperty().bind(tvMedicine.widthProperty().multiply(0.2));

        // combobox của medicine
        cbMonthMedicine.getItems().addAll(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12);
        cbMonthMedicine.getSelectionModel().select(LocalDate.now().getMonthValue() - 1);

        for (int i = 2015; i <= currentYear; i++) {
            cbYearMedicine.getItems().add(i);
        }
        cbYearMedicine.getSelectionModel().select(Integer.valueOf(currentYear));
    }

}
