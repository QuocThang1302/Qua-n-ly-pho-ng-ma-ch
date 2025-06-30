package com.example.controllers;

import com.example.DAO.StaffDAO;
import com.example.model.StaffModel;
import javafx.beans.property.SimpleStringProperty;
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
import java.io.IOException;
import java.time.format.DateTimeFormatter;

public class StaffController implements StaffDataChangeListener {
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
    private Button btnAdd;

    private ObservableList<StaffModel> staffList;

    @FXML
    public void initialize() {
        idCol.prefWidthProperty().bind(tvStaff.widthProperty().multiply(0.15));
        nameCol.prefWidthProperty().bind(tvStaff.widthProperty().multiply(0.2));
        salaryCol.prefWidthProperty().bind(tvStaff.widthProperty().multiply(0.18));
        roleCol.prefWidthProperty().bind(tvStaff.widthProperty().multiply(0.1));
        phoneCol.prefWidthProperty().bind(tvStaff.widthProperty().multiply(0.17));
        birthCol.prefWidthProperty().bind(tvStaff.widthProperty().multiply(0.2));

        tvStaff.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                StaffModel staffModel = tvStaff.getSelectionModel().getSelectedItem();
                if (staffModel != null) {
                    showStaffDetailPopUp(staffModel);
                }
            }
        });

        btnAdd.setOnMouseClicked(event -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/staff_detail.fxml"));
                Parent root = loader.load();
                StaffDetailController controller = loader.getController();
                controller.setDataChangeListener(this);
                Stage dialogStage = new Stage();
                dialogStage.setTitle("Thêm nhân viên");
                dialogStage.initModality(Modality.APPLICATION_MODAL);
                dialogStage.setScene(new Scene(root));
                dialogStage.showAndWait();
            } catch (IOException ex) {
                ex.printStackTrace();
                showAlert("Lỗi", "Không thể mở cửa sổ thêm nhân viên: " + ex.getMessage(), Alert.AlertType.ERROR);
            }
        });

        loadStaffData();
    }

    private void showStaffDetailPopUp(StaffModel staffModel) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/staff_detail.fxml"));
            Parent root = loader.load();
            StaffDetailController controller = loader.getController();
            controller.setStaff(staffModel);
            controller.setDataChangeListener(this);
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Chi tiết nhân viên");
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            dialogStage.setScene(new Scene(root));
            dialogStage.showAndWait();
        } catch (IOException ex) {
            ex.printStackTrace();
            showAlert("Lỗi", "Không thể mở cửa sổ chi tiết nhân viên: " + ex.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @Override
    public void onDataChanged(StaffModel updatedStaff, String action) {
        ObservableList<TableColumn<StaffModel, ?>> sortOrder = FXCollections.observableArrayList(tvStaff.getSortOrder());
        int selectedIndex = tvStaff.getSelectionModel().getSelectedIndex();

        if ("INSERT".equals(action)) {
            staffList.add(updatedStaff);
        } else if ("UPDATE".equals(action)) {
            for (int i = 0; i < staffList.size(); i++) {
                if (staffList.get(i).getId().equals(updatedStaff.getId())) {
                    staffList.set(i, updatedStaff);
                    break;
                }
            }
        } else if ("DELETE".equals(action)) {
            staffList.removeIf(staff -> staff.getId().equals(updatedStaff.getId()));
        }

        lblTotalStaffs.setText("Tổng: " + staffList.size() + " nhân viên");
        tvStaff.getSortOrder().setAll(sortOrder);
        if (selectedIndex >= 0 && selectedIndex < tvStaff.getItems().size()) {
            tvStaff.getSelectionModel().select(selectedIndex);
            tvStaff.scrollTo(selectedIndex);
        }
    }

    public void loadStaffData() {
        try {
            staffList = FXCollections.observableArrayList(StaffDAO.getAll());
            idCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getId()));
            nameCol.setCellValueFactory(data -> new SimpleStringProperty(
                    data.getValue().getLastname() + " " + data.getValue().getFirstname()));
            salaryCol.setCellValueFactory(data -> new SimpleStringProperty(
                    String.format("%.0f", data.getValue().getLuong())));
            roleCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getRole()));
            phoneCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getPhone()));
            birthCol.setCellValueFactory(data -> new SimpleStringProperty(
                    data.getValue().getBirthday().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))));

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
                lblTotalStaffs.setText("Tổng: " + filteredData.size() + " nhân viên");
            });

            SortedList<StaffModel> sortedData = new SortedList<>(filteredData);
            sortedData.comparatorProperty().bind(tvStaff.comparatorProperty());
            tvStaff.setItems(sortedData);
            lblTotalStaffs.setText("Tổng: " + staffList.size() + " nhân viên");
        } catch (Exception ex) {
            ex.printStackTrace();
            showAlert("Lỗi", "Không thể tải dữ liệu nhân viên: " + ex.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void showAlert(String title, String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}