package com.example.controllers;

import com.example.model.MedicineModel;
import com.example.DAO.MedicineDAO;
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

public class MedicineController implements MedicineDataChangeListener {
    @FXML
    private TableView<MedicineModel> tvMedicine;
    @FXML
    private TableColumn<MedicineModel, String> idCol;
    @FXML
    private TableColumn<MedicineModel, String> nameCol;
    @FXML
    private TableColumn<MedicineModel, String> useCol;
    @FXML
    private TableColumn<MedicineModel, String> quantityCol;
    @FXML
    private TableColumn<MedicineModel, String> costCol;
    @FXML
    private TextField tfSearch;
    @FXML
    private Label lblTotalMedicines;
    @FXML
    private Button btnAdd;

    private ObservableList<MedicineModel> medicineList;

    @FXML
    public void initialize() {
        idCol.prefWidthProperty().bind(tvMedicine.widthProperty().multiply(0.15));
        nameCol.prefWidthProperty().bind(tvMedicine.widthProperty().multiply(0.2));
        useCol.prefWidthProperty().bind(tvMedicine.widthProperty().multiply(0.35));
        quantityCol.prefWidthProperty().bind(tvMedicine.widthProperty().multiply(0.1));
        costCol.prefWidthProperty().bind(tvMedicine.widthProperty().multiply(0.2));

        tvMedicine.setOnMouseClicked((event) -> {
            if (event.getClickCount() == 2) {
                MedicineModel medicineModel = tvMedicine.getSelectionModel().getSelectedItem();
                if (medicineModel != null) {
                    showMedicineDetailPopUp(medicineModel);
                }
            }
        });

        btnAdd.setOnMouseClicked((event) -> {
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
            } catch (IOException e) {
                e.printStackTrace();
                showAlert("Lỗi", "Không thể mở cửa sổ thêm thuốc: " + e.getMessage(), Alert.AlertType.ERROR);
            }
        });

        loadMedicineData();
    }

    private void showMedicineDetailPopUp(MedicineModel medicineModel) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/medicine_detail.fxml"));
            Parent root = loader.load();
            MedicineDetailController controller = loader.getController();
            controller.setMedicine(medicineModel);
            controller.setDataChangeListener(this);
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Chi tiết thuốc");
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            dialogStage.setScene(new Scene(root));
            dialogStage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Lỗi", "Không thể mở cửa sổ chi tiết thuốc: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @Override
    public void onDataChanged(MedicineModel updatedMedicine, String action) {
        ObservableList<TableColumn<MedicineModel, ?>> sortOrder = FXCollections.observableArrayList(tvMedicine.getSortOrder());
        int selectedIndex = tvMedicine.getSelectionModel().getSelectedIndex();

        if ("INSERT".equals(action)) {
            medicineList.add(updatedMedicine);
        } else if ("UPDATE".equals(action)) {
            for (int i = 0; i < medicineList.size(); i++) {
                if (medicineList.get(i).getMaThuoc().equals(updatedMedicine.getMaThuoc())) {
                    medicineList.set(i, updatedMedicine);
                    break;
                }
            }
        } else if ("DELETE".equals(action)) {
            medicineList.removeIf(med -> med.getMaThuoc().equals(updatedMedicine.getMaThuoc()));
        }

        lblTotalMedicines.setText("Tổng số thuốc: " + medicineList.size());
        tvMedicine.getSortOrder().setAll(sortOrder);
        if (selectedIndex >= 0 && selectedIndex < tvMedicine.getItems().size()) {
            tvMedicine.getSelectionModel().select(selectedIndex);
            tvMedicine.scrollTo(selectedIndex);
        }
    }

    public void loadMedicineData() {
        try {
            medicineList = FXCollections.observableArrayList(MedicineDAO.getAllMedicines());
            idCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getMaThuoc()));
            nameCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getTenThuoc()));
            useCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getCongDung()));
            quantityCol.setCellValueFactory(data -> new SimpleStringProperty(String.valueOf(data.getValue().getSoLuong())));
            costCol.setCellValueFactory(data -> new SimpleStringProperty(String.format("%,.0f", data.getValue().getGiaTien())));
            FilteredList<MedicineModel> filteredList = new FilteredList<>(medicineList, p -> true);
            SortedList<MedicineModel> sortedList = new SortedList<>(filteredList);
            sortedList.comparatorProperty().bind(tvMedicine.comparatorProperty());
            tvMedicine.setItems(sortedList);
            lblTotalMedicines.setText("Tổng số thuốc: " + medicineList.size());
            tfSearch.textProperty().addListener((obs, oldVal, newVal) -> {
                filteredList.setPredicate(med -> {
                    if (newVal == null || newVal.isEmpty()) return true;
                    String lower = newVal.toLowerCase();
                    return med.getMaThuoc().toLowerCase().contains(lower)
                            || med.getTenThuoc().toLowerCase().contains(lower)
                            || med.getCongDung().toLowerCase().contains(lower);
                });
                lblTotalMedicines.setText("Tổng số thuốc: " + filteredList.size());
            });
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Lỗi", "Không thể tải dữ liệu thuốc: " + e.getMessage(), Alert.AlertType.ERROR);
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