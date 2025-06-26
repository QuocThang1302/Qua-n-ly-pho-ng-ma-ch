package com.example.controllers;

import com.example.DAO.MedicineDAO;
import com.example.DAO.PatientReportDAO;
import com.example.model.MedicineModel;
import com.example.model.PatientModel;
import com.example.model.PatientReportModel;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.application.Platform;

import java.time.LocalDate;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

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

    // Cache để lưu trữ prescription count
    private Map<String, Integer> prescriptionCountCache = new HashMap<>();

    @FXML
    private void initialize() {
        setupPatientTable();
        setupMedicineTable();
        setupComboBoxes();

        // Load dữ liệu ban đầu
        loadPatientReportTable();
        loadMedicineReportTable();
    }

    private void setupPatientTable() {
        // Thiết lập độ rộng cột
        numberPatientCol.prefWidthProperty().bind(tvPatient.widthProperty().multiply(0.2));
        patientCountCol.prefWidthProperty().bind(tvPatient.widthProperty().multiply(0.2));
        dateCol.prefWidthProperty().bind(tvPatient.widthProperty().multiply(0.2));
        revenueCol.prefWidthProperty().bind(tvPatient.widthProperty().multiply(0.2));
        rateCol.prefWidthProperty().bind(tvPatient.widthProperty().multiply(0.2));

        // Thiết lập CellValueFactory một lần duy nhất
        numberPatientCol.setCellValueFactory(cellData ->
                new SimpleStringProperty(String.valueOf(tvPatient.getItems().indexOf(cellData.getValue()) + 1)));

        patientCountCol.setCellValueFactory(cellData ->
                new SimpleStringProperty(String.valueOf(cellData.getValue().getPatientCount())));

        dateCol.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getDate().toString()));

        revenueCol.setCellValueFactory(cellData ->
                new SimpleStringProperty(String.format("%.2f", cellData.getValue().getRevenue())));

        rateCol.setCellValueFactory(cellData ->
                new SimpleStringProperty(String.format("%.2f%%", cellData.getValue().getRate() * 100)));
    }

    private void setupMedicineTable() {
        // Thiết lập độ rộng cột
        numberMedicineCol.prefWidthProperty().bind(tvMedicine.widthProperty().multiply(0.2));
        medicineCol.prefWidthProperty().bind(tvMedicine.widthProperty().multiply(0.2));
        unitCol.prefWidthProperty().bind(tvMedicine.widthProperty().multiply(0.2));
        quantityCol.prefWidthProperty().bind(tvMedicine.widthProperty().multiply(0.2));
        useCol.prefWidthProperty().bind(tvMedicine.widthProperty().multiply(0.2));

        // Thiết lập CellValueFactory một lần duy nhất
        numberMedicineCol.setCellValueFactory(cellData ->
                new SimpleStringProperty(String.valueOf(tvMedicine.getItems().indexOf(cellData.getValue()) + 1)));

        medicineCol.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getTenThuoc()));

        unitCol.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getDonVi()));

        quantityCol.setCellValueFactory(cellData ->
                new SimpleStringProperty(String.valueOf(cellData.getValue().getSoLuong())));

        // Sử dụng cache cho prescription count
        useCol.setCellValueFactory(cellData -> {
            String maThuoc = cellData.getValue().getMaThuoc();
            String cacheKey = getCacheKey(maThuoc);
            Integer count = prescriptionCountCache.get(cacheKey);
            return new SimpleStringProperty(String.valueOf(count != null ? count : 0));
        });
    }

    private void setupComboBoxes() {
        // Setup patient comboboxes
        cbMonthPatient.getItems().addAll(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12);
        cbMonthPatient.getSelectionModel().select(LocalDate.now().getMonthValue() - 1);

        int currentYear = LocalDate.now().getYear();
        for (int i = 2015; i <= currentYear; i++) {
            cbYearPatient.getItems().add(i);
        }
        cbYearPatient.getSelectionModel().select(Integer.valueOf(currentYear));

        cbMonthPatient.setOnAction(event -> loadPatientReportTable());
        cbYearPatient.setOnAction(event -> loadPatientReportTable());

        // Setup medicine comboboxes
        cbMonthMedicine.getItems().addAll(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12);
        cbMonthMedicine.getSelectionModel().select(LocalDate.now().getMonthValue() - 1);

        for (int i = 2015; i <= currentYear; i++) {
            cbYearMedicine.getItems().add(i);
        }
        cbYearMedicine.getSelectionModel().select(Integer.valueOf(currentYear));

        cbMonthMedicine.setOnAction(event -> loadMedicineReportTableAsync());
        cbYearMedicine.setOnAction(event -> loadMedicineReportTableAsync());
    }

    private void loadPatientReportTable() {
        int selectedMonth = cbMonthPatient.getValue();
        int selectedYear = cbYearPatient.getValue();

        List<PatientReportModel> reportList = PatientReportDAO.getDailyPatientReportsByMonth(selectedYear, selectedMonth);
        ObservableList<PatientReportModel> data = FXCollections.observableArrayList(reportList);

        // Chỉ gán dữ liệu mới, không thiết lập lại CellValueFactory
        tvPatient.setItems(data);
    }

    private void loadMedicineReportTableAsync() {
        // Disable combobox để tránh spam click
        cbMonthMedicine.setDisable(true);
        cbYearMedicine.setDisable(true);

        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                loadMedicineReportTable();
                return null;
            }

            @Override
            protected void succeeded() {
                Platform.runLater(() -> {
                    cbMonthMedicine.setDisable(false);
                    cbYearMedicine.setDisable(false);
                });
            }

            @Override
            protected void failed() {
                Platform.runLater(() -> {
                    cbMonthMedicine.setDisable(false);
                    cbYearMedicine.setDisable(false);
                });
            }
        };

        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }

    private void loadMedicineReportTable() {
        int month = cbMonthMedicine.getValue();
        int year = cbYearMedicine.getValue();

        // Clear cache cũ
        prescriptionCountCache.clear();

        // Lấy danh sách thuốc
        List<MedicineModel> list = MedicineDAO.getMedicinesUsedInMonth(year, month);

        // Preload prescription counts để tránh multiple queries
        for (MedicineModel medicine : list) {
            String cacheKey = getCacheKey(medicine.getMaThuoc());
            int count = MedicineDAO.countDistinctPrescriptionsByMedicineInMonth(
                    medicine.getMaThuoc(), year, month);
            prescriptionCountCache.put(cacheKey, count);
        }

        // Update UI trên JavaFX Application Thread
        Platform.runLater(() -> {
            ObservableList<MedicineModel> data = FXCollections.observableArrayList(list);
            tvMedicine.setItems(data);
        });
    }

    private String getCacheKey(String maThuoc) {
        int month = cbMonthMedicine.getValue();
        int year = cbYearMedicine.getValue();
        return maThuoc + "_" + year + "_" + month;
    }
}