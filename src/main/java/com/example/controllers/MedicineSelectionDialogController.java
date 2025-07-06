package com.example.controllers;

import com.example.model.MedicineModel;
import com.example.DAO.MedicineDAO;

import javafx.collections.FXCollections;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.beans.property.SimpleStringProperty;

import java.text.DecimalFormat;
import java.util.List;

public class MedicineSelectionDialogController {

    @FXML private TextField txtSearch;
    @FXML private TableView<MedicineModel> tableThuoc;
    @FXML private TableColumn<MedicineModel, String> colTenThuoc;
    @FXML private TableColumn<MedicineModel, String> colSoLuong;
    @FXML private TableColumn<MedicineModel, String> colDonVi;
    @FXML private TableColumn<MedicineModel, String> colGiaTien;
    @FXML private TableColumn<MedicineModel, String> colHuongDan;
    @FXML private Button btnChon;
    @FXML private Button btnHuy;

    private MedicineModel selectedThuoc;

    public MedicineModel getSelectedThuoc() {
        return selectedThuoc;
    }

    @FXML
    private void initialize() {
        colTenThuoc.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getTenThuoc()));
        colSoLuong.setCellValueFactory(data -> new SimpleStringProperty(String.valueOf(data.getValue().getSoLuong())));
        colDonVi.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getDonVi()));
        colGiaTien.setCellValueFactory(data -> new SimpleStringProperty(formatGiaTien(data.getValue().getGiaTien())));
        colHuongDan.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getHuongDanSuDung()));

        List<MedicineModel> list = MedicineDAO.getAllMedicines();
        FilteredList<MedicineModel> filteredList = new FilteredList<>(FXCollections.observableArrayList(list), p -> true);
        tableThuoc.setItems(filteredList);

        txtSearch.textProperty().addListener((obs, oldVal, newVal) -> {
            String lower = newVal.toLowerCase();
            filteredList.setPredicate(t ->
                    t.getTenThuoc().toLowerCase().contains(lower) ||
                            t.getDonVi().toLowerCase().contains(lower) ||
                            t.getHuongDanSuDung().toLowerCase().contains(lower) ||
                            t.getMaThuoc().toLowerCase().contains(lower)
            );
        });

        btnChon.setOnAction(e -> {
            selectedThuoc = tableThuoc.getSelectionModel().getSelectedItem();
            closeStage();
        });

        btnHuy.setOnAction(e -> {
            selectedThuoc = null;
            closeStage();
        });
    }

    private void closeStage() {
        Stage stage = (Stage) txtSearch.getScene().getWindow();
        stage.close();
    }

    private String formatGiaTien(double gia) {
        DecimalFormat df = new DecimalFormat("#,###.##");
        return df.format(gia) + " đ";
    }
}
