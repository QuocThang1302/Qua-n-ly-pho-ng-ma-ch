package com.example.controllers;

import com.example.helper.NavigationHelper;
import com.example.model.PatientModel;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.awt.event.ActionEvent;
import java.io.IOException;

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
    public void initialize(){
        nameCol.prefWidthProperty().bind(tvPatient.widthProperty().multiply(0.2));
        idCol.prefWidthProperty().bind(tvPatient.widthProperty().multiply(0.2));
        genderCol.prefWidthProperty().bind(tvPatient.widthProperty().multiply(0.2));
        phoneCol.prefWidthProperty().bind(tvPatient.widthProperty().multiply(0.2));
        birthCol.prefWidthProperty().bind(tvPatient.widthProperty().multiply(0.2));

        tvPatient.widthProperty().addListener((obs, oldVal, newVal) -> {
            ((Region) tvPatient.lookup("TableHeaderRow")).setPrefHeight(45);
        });

        tvPatient.setOnMouseClicked((event) -> {
           if (event.getClickCount() == 2){
                PatientModel patientModel = tvPatient.getSelectionModel().getSelectedItem();
                if (patientModel != null){
                    showPatientDetailPopUp(patientModel);
                }
           }
        });

        PatientModel patientModel = tvPatient.getSelectionModel().getSelectedItem();
        showPatientDetailPopUp(patientModel);
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
