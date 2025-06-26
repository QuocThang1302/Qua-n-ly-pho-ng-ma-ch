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

public class MedicineController {
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
    Button btnAdd;

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

                // Tạo stage mới (window mới)
                Stage dialogStage = new Stage();
                dialogStage.setTitle("Thêm thuốc");
                dialogStage.initModality(Modality.APPLICATION_MODAL); // chặn tương tác với window chính
                dialogStage.setScene(new Scene(root));
                dialogStage.showAndWait();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        loadMedicineData();
    }

    private void loadMedicineData() {
        ObservableList<MedicineModel> observableList = FXCollections.observableArrayList(MedicineDAO.getAllMedicines());

        // Cột ID (mã thuốc)
        idCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getMaThuoc()));

        // Cột tên thuốc
        nameCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getTenThuoc()));

        // Cột công dụng
        useCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getCongDung()));

        // Cột số lượng
        quantityCol.setCellValueFactory(data -> new SimpleStringProperty(String.valueOf(data.getValue().getSoLuong())));

        // Cột giá tiền
        costCol.setCellValueFactory(data -> new SimpleStringProperty(String.format("%,.0f", data.getValue().getGiaTien())));

        // Sắp xếp + binding vào TableView
        FilteredList<MedicineModel> filteredList = new FilteredList<>(observableList, p -> true);
        SortedList<MedicineModel> sortedList = new SortedList<>(filteredList);
        sortedList.comparatorProperty().bind(tvMedicine.comparatorProperty());
        tvMedicine.setItems(sortedList);

        // Hiển thị tổng số thuốc
        lblTotalMedicines.setText("Tổng số thuốc: " + observableList.size());

        // Gợi ý thêm: search realtime nếu muốn
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
    }


    private void showMedicineDetailPopUp(MedicineModel medicineModel) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/medicine_detail.fxml"));
            Parent root = loader.load();

            // Lấy controller để truyền dữ liệu
            MedicineDetailController controller = loader.getController();
            controller.setMedicine(medicineModel);

            // Tạo stage mới (window mới)
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Chi tiết thuốc");
            dialogStage.initModality(Modality.APPLICATION_MODAL); // chặn tương tác với window chính
            dialogStage.setScene(new Scene(root));
            dialogStage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
