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

    @FXML
    public void initialize() {
        // Thiết lập độ rộng cột
        idCol.prefWidthProperty().bind(tvMedicine.widthProperty().multiply(0.15));
        nameCol.prefWidthProperty().bind(tvMedicine.widthProperty().multiply(0.2));
        useCol.prefWidthProperty().bind(tvMedicine.widthProperty().multiply(0.35));
        quantityCol.prefWidthProperty().bind(tvMedicine.widthProperty().multiply(0.1));
        costCol.prefWidthProperty().bind(tvMedicine.widthProperty().multiply(0.2));

        // Xử lý sự kiện double-click trên TableView
        tvMedicine.setOnMouseClicked((event) -> {
            if (event.getClickCount() == 2) {
                MedicineModel medicineModel = tvMedicine.getSelectionModel().getSelectedItem();
                if (medicineModel != null) {
                    showMedicineDetailPopUp(medicineModel);
                }
            }
        });

        // Xử lý sự kiện nút Add
        btnAdd.setOnMouseClicked((event) -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/medicine_detail.fxml"));
                Parent root = loader.load();

                // Lấy controller và truyền callback
                MedicineDetailController controller = loader.getController();
                controller.setDataChangeListener(this); // Truyền callback

                // Tạo stage mới
                Stage dialogStage = new Stage();
                dialogStage.setTitle("Thêm thuốc");
                dialogStage.initModality(Modality.APPLICATION_MODAL);
                dialogStage.setScene(new Scene(root));
                dialogStage.showAndWait();
            } catch (IOException e) {
                e.printStackTrace();
                showAlert("Lỗi", "Không thể mở cửa sổ thêm thuốc: " + e.getMessage());
            }
        });

        loadMedicineData();
    }

    private void showMedicineDetailPopUp(MedicineModel medicineModel) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/medicine_detail.fxml"));
            Parent root = loader.load();

            // Lấy controller và truyền dữ liệu + callback
            MedicineDetailController controller = loader.getController();
            controller.setMedicine(medicineModel);
            controller.setDataChangeListener(this); // Truyền callback

            // Tạo stage mới
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Chi tiết thuốc");
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            dialogStage.setScene(new Scene(root));
            dialogStage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Lỗi", "Không thể mở cửa sổ chi tiết thuốc: " + e.getMessage());
        }
    }

    @Override
    public void onDataChanged() {
        loadMedicineData(); // Làm mới dữ liệu khi được gọi từ callback
    }

    public void loadMedicineData() {
        try {
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

            // Tìm kiếm realtime
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
            showAlert("Lỗi", "Không thể tải dữ liệu thuốc: " + e.getMessage());
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}