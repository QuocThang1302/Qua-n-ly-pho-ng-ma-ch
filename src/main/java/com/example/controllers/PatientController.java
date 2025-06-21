package com.example.controllers;

import com.example.helper.NavigationHelper;
import com.example.model.PatientModel;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Region;

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
    private ImageView ivAdd;

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

        ivAdd.setOnMouseClicked(event -> {
            //NavigationHelper.setContent(contentArea, "/views/dashboard.fxml");
        });
    }
}
