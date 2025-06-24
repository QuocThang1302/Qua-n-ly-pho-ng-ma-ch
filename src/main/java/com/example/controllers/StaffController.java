package com.example.controllers;

import com.example.model.PatientModel;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class StaffController {
    @FXML
    private TableView<PatientModel> tvStaff;
    @FXML
    private TableColumn<PatientModel, Integer> idCol;
    @FXML
    private TableColumn<PatientModel, String> nameCol;
    @FXML
    private TableColumn<PatientModel, String> salaryCol;
    @FXML
    private TableColumn<PatientModel, String> roleCol;
    @FXML
    private TableColumn<PatientModel, String> phoneCol;
    @FXML
    private TableColumn<PatientModel, String> birthCol;
    @FXML
    private TextField tfSearch;
    @FXML
    private Label lblTotalStaffs;

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
                PatientModel patientModel = tvStaff.getSelectionModel().getSelectedItem();
                if (patientModel != null) {
                    showPatientDetailPopUp(patientModel);
                }
            }
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
}
