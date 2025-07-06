package com.example.controllers;

import com.example.DAO.MedicineDAO;
import com.example.model.MedicineModel;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.util.List;
import java.util.function.Consumer;

public class MedicineSelectionDialogController {

    @FXML private TextField tfSearch;
    @FXML private TableView<MedicineModel> tableMedicine;
    @FXML private TableColumn<MedicineModel, String> colMaThuoc;
    @FXML private TableColumn<MedicineModel, String> colTenThuoc;
    @FXML private TableColumn<MedicineModel, String> colCongDung;
    @FXML private TableColumn<MedicineModel, Integer> colSoLuong;
    @FXML private TableColumn<MedicineModel, Double> colGiaTien;
    @FXML private TableColumn<MedicineModel, String> colDonVi;

    @FXML private TextField tfSelectedMaThuoc, tfSelectedTenThuoc, tfSelectedSoLuongKho, tfSelectedSoLuongCan, tfSelectedGiaTien;
    @FXML private TextArea tfSelectedHuongDan;

    private ObservableList<MedicineModel> allMedicines = FXCollections.observableArrayList();
    private MedicineModel selectedMedicine;
    private Consumer<MedicineModel> onMedicineSelected;

    @FXML
    public void initialize() {
        setupTableColumns();
        loadAllMedicines();
        setupTableSelection();
        setupSearchListener();
    }

    private void setupTableColumns() {
        colMaThuoc.setCellValueFactory(new PropertyValueFactory<>("maThuoc"));
        colTenThuoc.setCellValueFactory(new PropertyValueFactory<>("tenThuoc"));
        colCongDung.setCellValueFactory(new PropertyValueFactory<>("congDung"));
        colSoLuong.setCellValueFactory(new PropertyValueFactory<>("soLuong"));
        colGiaTien.setCellValueFactory(new PropertyValueFactory<>("giaTien"));
        colDonVi.setCellValueFactory(new PropertyValueFactory<>("donVi"));

        // Format giá tiền
        colGiaTien.setCellFactory(column -> new TableCell<MedicineModel, Double>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(String.format("%,.0f", item));
                }
            }
        });
    }

    private void loadAllMedicines() {
        List<MedicineModel> medicines = MedicineDAO.getAllMedicines();
        allMedicines.setAll(medicines);
        tableMedicine.setItems(allMedicines);
    }

    private void setupTableSelection() {
        tableMedicine.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                selectedMedicine = newSelection;
                updateSelectedMedicineInfo();
            }
        });
    }

    private void setupSearchListener() {
        tfSearch.textProperty().addListener((obs, oldText, newText) -> {
            filterMedicines(newText);
        });
    }

    private void filterMedicines(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            tableMedicine.setItems(allMedicines);
        } else {
            String lowerKeyword = keyword.toLowerCase();
            ObservableList<MedicineModel> filtered = allMedicines.filtered(medicine ->
                (medicine.getTenThuoc() != null && medicine.getTenThuoc().toLowerCase().contains(lowerKeyword)) ||
                (medicine.getMaThuoc() != null && medicine.getMaThuoc().toLowerCase().contains(lowerKeyword))
            );
            tableMedicine.setItems(filtered);
        }
    }

    private void updateSelectedMedicineInfo() {
        if (selectedMedicine != null) {
            tfSelectedMaThuoc.setText(selectedMedicine.getMaThuoc());
            tfSelectedTenThuoc.setText(selectedMedicine.getTenThuoc());
            tfSelectedSoLuongKho.setText(String.valueOf(selectedMedicine.getSoLuong()));
            tfSelectedGiaTien.setText(String.format("%,.0f", selectedMedicine.getGiaTien()));
            tfSelectedHuongDan.setText(selectedMedicine.getHuongDanSuDung());
            tfSelectedSoLuongCan.clear();
        }
    }

    @FXML
    private void handleSearch() {
        filterMedicines(tfSearch.getText());
    }

    @FXML
    private void handleAddToBill() {
        if (selectedMedicine == null) {
            showAlert("Lỗi", "Vui lòng chọn một loại thuốc!", Alert.AlertType.ERROR);
            return;
        }

        String soLuongText = tfSelectedSoLuongCan.getText().trim();
        if (soLuongText.isEmpty()) {
            showAlert("Lỗi", "Vui lòng nhập số lượng cần!", Alert.AlertType.ERROR);
            return;
        }

        try {
            int soLuongCan = Integer.parseInt(soLuongText);
            if (soLuongCan <= 0) {
                showAlert("Lỗi", "Số lượng phải lớn hơn 0!", Alert.AlertType.ERROR);
                return;
            }

            if (soLuongCan > selectedMedicine.getSoLuong()) {
                showAlert("Lỗi", "Số lượng trong kho không đủ! Kho chỉ còn " + selectedMedicine.getSoLuong() + " " + selectedMedicine.getDonVi(), Alert.AlertType.ERROR);
                return;
            }

            // Tạo thuốc mới với số lượng đã chọn
            MedicineModel medicineForBill = new MedicineModel(
                selectedMedicine.getMaThuoc(),
                selectedMedicine.getTenThuoc(),
                selectedMedicine.getCongDung(),
                soLuongCan, // Số lượng cần
                selectedMedicine.getGiaTien(),
                selectedMedicine.getDonVi(),
                selectedMedicine.getHuongDanSuDung()
            );

            // Giảm số lượng trong kho
            boolean success = MedicineDAO.reduceMedicineQuantity(selectedMedicine.getMaThuoc(), soLuongCan);
            if (success) {
                showAlert("Thành công", "Đã thêm thuốc vào hóa đơn và cập nhật kho!", Alert.AlertType.INFORMATION);
                
                // Gọi callback để thêm thuốc vào hóa đơn
                if (onMedicineSelected != null) {
                    onMedicineSelected.accept(medicineForBill);
                }
                
                // Đóng dialog
                Stage stage = (Stage) tfSelectedMaThuoc.getScene().getWindow();
                stage.close();
            } else {
                showAlert("Lỗi", "Không thể cập nhật số lượng trong kho!", Alert.AlertType.ERROR);
            }

        } catch (NumberFormatException e) {
            showAlert("Lỗi", "Vui lòng nhập số lượng hợp lệ!", Alert.AlertType.ERROR);
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Lỗi", "Có lỗi xảy ra: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleCancel() {
        Stage stage = (Stage) tfSelectedMaThuoc.getScene().getWindow();
        stage.close();
    }

    public void setOnMedicineSelected(Consumer<MedicineModel> callback) {
        this.onMedicineSelected = callback;
    }

    private void showAlert(String title, String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
} 