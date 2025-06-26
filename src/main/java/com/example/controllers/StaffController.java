package com.example.controllers;

import com.example.DAO.StaffDAO;
import com.example.model.StaffModel;


import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

import javafx.beans.property.SimpleStringProperty;

import java.io.IOException;
import java.time.format.DateTimeFormatter;


import java.io.IOException;

public class StaffController {
    @FXML
    private TableView<StaffModel> tvStaff;
    @FXML
    private TableColumn<StaffModel, String> idCol;
    @FXML
    private TableColumn<StaffModel, String> nameCol;
    @FXML
    private TableColumn<StaffModel, String> salaryCol;
    @FXML
    private TableColumn<StaffModel, String> roleCol;
    @FXML
    private TableColumn<StaffModel, String> phoneCol;
    @FXML
    private TableColumn<StaffModel, String> birthCol;
    @FXML
    private TextField tfSearch;
    @FXML
    private Label lblTotalStaffs;
    @FXML
    Button btnAdd;

    @FXML
    public void initialize() {
        idCol.prefWidthProperty().bind(tvStaff.widthProperty().multiply(0.15));
        nameCol.prefWidthProperty().bind(tvStaff.widthProperty().multiply(0.2));
        salaryCol.prefWidthProperty().bind(tvStaff.widthProperty().multiply(0.18));
        roleCol.prefWidthProperty().bind(tvStaff.widthProperty().multiply(0.1));
        phoneCol.prefWidthProperty().bind(tvStaff.widthProperty().multiply(0.17));
        birthCol.prefWidthProperty().bind(tvStaff.widthProperty().multiply(0.2));

        tvStaff.setOnMouseClicked((event) -> {
            if (event.getClickCount() == 2) {
                StaffModel staffModel = tvStaff.getSelectionModel().getSelectedItem();
                if (staffModel != null) {
                    showStaffDetailPopUp(staffModel);
                }
            }
        });

        btnAdd.setOnMouseClicked((event) -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/staff_detail.fxml"));
                Parent root = loader.load();

                // Tạo stage mới (window mới)
                Stage dialogStage = new Stage();
                dialogStage.setTitle("Chi tiết nhân viên");
                dialogStage.initModality(Modality.APPLICATION_MODAL); // chặn tương tác với window chính
                dialogStage.setScene(new Scene(root));
                dialogStage.showAndWait();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        loadStaffData();
    }

    private void showStaffDetailPopUp(StaffModel staffModel) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/staff_detail.fxml"));
            Parent root = loader.load();

            // Lấy controller để truyền dữ liệu
            StaffDetailController controller = loader.getController();
            controller.setStaff(staffModel);

            // Tạo stage mới (window mới)
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Chi tiết nhân viên");
            dialogStage.initModality(Modality.APPLICATION_MODAL); // chặn tương tác với window chính
            dialogStage.setScene(new Scene(root));
            dialogStage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadStaffData() {
        ObservableList<StaffModel> staffList = FXCollections.observableArrayList(StaffDAO.getAll());

        // Tìm kiếm
        FilteredList<StaffModel> filteredData = new FilteredList<>(staffList, p -> true);
        tfSearch.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(staff -> {
                if (newValue == null || newValue.isEmpty()) return true;
                String filter = newValue.toLowerCase();
                return staff.getId().toLowerCase().contains(filter)
                        || staff.getFirstname().toLowerCase().contains(filter)
                        || staff.getLastname().toLowerCase().contains(filter)
                        || staff.getRole().toLowerCase().contains(filter)
                        || staff.getPhone().toLowerCase().contains(filter);
            });
        });

        // Sắp xếp
        SortedList<StaffModel> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(tvStaff.comparatorProperty());
        tvStaff.setItems(sortedData);

        // Cột hiển thị
        idCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getId()));
        nameCol.setCellValueFactory(data -> new SimpleStringProperty(
                data.getValue().getLastname() + " " + data.getValue().getFirstname()));
        salaryCol.setCellValueFactory(data -> new SimpleStringProperty(
                String.format("%.0f", data.getValue().getLuong())));
        roleCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getRole()));
        phoneCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getPhone()));
        birthCol.setCellValueFactory(data -> new SimpleStringProperty(
                data.getValue().getBirthday().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))));

        // Tổng số nhân viên
        lblTotalStaffs.setText("Tổng: " + staffList.size() + " nhân viên");
    }


}
