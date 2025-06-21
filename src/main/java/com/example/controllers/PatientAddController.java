package com.example.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;

public class PatientAddController {
    @FXML
    private TextField tfSearch;
    @FXML
    private Button btnSave;
    @FXML
    private Button btnBack;
    @FXML
    private TextField tfFirstName;
    @FXML
    private TextField tfLastName;
    @FXML
    private DatePicker dpBirth;
    /*Cách in ra ngày theo định dạng dd/mm/yyyy nè
      LocalDate date = dpBirth.getValue();
      String format = date.format(DateTimeFormatter.ofPattern("dd-MM-yyyy));*/

    @FXML
    public void initialize(){

    }
}
