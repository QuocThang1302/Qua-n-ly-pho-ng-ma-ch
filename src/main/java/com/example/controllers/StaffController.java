package com.example.controllers;

import com.example.model.StaffModel;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class StaffController {
    @FXML
    private TableView<StaffModel> tvStaff;
    @FXML
    private TableColumn<StaffModel, Integer> idCol;
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
}
