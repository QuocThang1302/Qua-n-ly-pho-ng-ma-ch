package com.example.controllers;

import com.example.DAO.PatientDAO;
import com.example.helper.NavigationHelper;
import com.example.model.PatientModel;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.awt.event.ActionEvent;
import java.io.IOException;

import java.time.format.DateTimeFormatter;

public class PatientController {
    @FXML
    private TableView<PatientModel> tvPatient;
    @FXML
    private TableColumn<PatientModel, String> nameCol;
    @FXML
    private TableColumn<PatientModel, String> idCol;
    @FXML
    private TableColumn<PatientModel, String> genderCol;
    @FXML
    private TableColumn<PatientModel, String> phoneCol;
    @FXML
    private TableColumn<PatientModel, String> birthCol;
    @FXML
    private TextField tfSearch;
    @FXML
    private Label lblTotalPatients;

    private ObservableList<PatientModel> patientList;
    private FilteredList<PatientModel> filteredData;

    @FXML
    public void initialize() {
        nameCol.prefWidthProperty().bind(tvPatient.widthProperty().multiply(0.2));
        idCol.prefWidthProperty().bind(tvPatient.widthProperty().multiply(0.2));
        genderCol.prefWidthProperty().bind(tvPatient.widthProperty().multiply(0.2));
        phoneCol.prefWidthProperty().bind(tvPatient.widthProperty().multiply(0.2));
        birthCol.prefWidthProperty().bind(tvPatient.widthProperty().multiply(0.2));

        tvPatient.widthProperty().addListener((obs, oldVal, newVal) -> {
            ((Region) tvPatient.lookup("TableHeaderRow")).setPrefHeight(45);
        });
        // Thiết lập cách hiển thị dữ liệu cho mỗi cột
        setupTableColumns();

        // Load dữ liệu
        loadPatientData();

        tvPatient.setOnMouseClicked((event) -> {
            if (event.getClickCount() == 2) {
                PatientModel patientModel = tvPatient.getSelectionModel().getSelectedItem();
                if (patientModel != null) {
                    showPatientDetailPopUp(patientModel);
                }
            }
            // Thiết lập tìm kiếm
            setupSearch();

            // Số lượng bệnh nhân
            updatePatientCount();

            PatientModel patientModel = tvPatient.getSelectionModel().getSelectedItem();
            showPatientDetailPopUp(patientModel);
        });
    }

    private void showPatientDetailPopUp(PatientModel patientModel) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/patient_detail_dialog.fxml"));
            Parent root = loader.load();

            // Lấy controller để truyền dữ liệu
            PatientDetailDialogController controller = loader.getController();
            controller.setPatient(patientModel);

            // Tạo stage mới (window mới)
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Chi tiết bệnh nhân");
            dialogStage.initModality(Modality.APPLICATION_MODAL); // chặn tương tác với window chính
            dialogStage.setScene(new Scene(root));
            dialogStage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
                                    
    private void setupTableColumns() {
        // Thiết lập cách hiển thị dữ liệu cho từng cột
        idCol.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(cellData.getValue().getMaBenhNhan()));

        nameCol.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(cellData.getValue().getHoTen()));

        phoneCol.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(cellData.getValue().getSoDienThoai()));

        birthCol.setCellValueFactory(cellData -> {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            return new javafx.beans.property.SimpleStringProperty(
                    cellData.getValue().getNgaySinh().format(formatter));
        });

        genderCol.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(cellData.getValue().getGioiTinh()));
    }

    private void loadPatientData() {
        try {
            // Lấy dữ liệu từ database
            patientList = FXCollections.observableArrayList(PatientDAO.getAll());

            // Tạo filtered list để hỗ trợ tìm kiếm
            filteredData = new FilteredList<>(patientList, p -> true);

            // Gán dữ liệu cho TableView
            tvPatient.setItems(filteredData);

            // Cập nhật số lượng bệnh nhân (nếu cần thiết)
            updatePatientCount();

        } catch (Exception e) {
            System.err.println("Lỗi khi load dữ liệu bệnh nhân: " + e.getMessage());
            // Có thể hiển thị alert cho user
            showAlert("Lỗi", "Không thể tải dữ liệu bệnh nhân: " + e.getMessage());
        }
    }

    private void setupSearch() {
        // Lắng nghe thay đổi trong ô tìm kiếm
        tfSearch.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(patient -> {
                // Nếu filter text rỗng, hiển thị tất cả
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }

                // So sánh tên, mã bệnh nhân, số điện thoại
                String lowerCaseFilter = newValue.toLowerCase();

                if (patient.getHoTen().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                } else if (patient.getMaBenhNhan().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                } else if (patient.getSoDienThoai().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                }
                return false; // Không khớp
            });
        });
    }

    private void updatePatientCount() {
        lblTotalPatients.setText("Tổng số bệnh nhân (" + patientList.size() + ")");
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Method để refresh dữ liệu sau khi thêm/sửa/xóa
    public void refreshData() {
        loadPatientData();
    }
}
