package com.example.controllers;

import com.example.model.PatientModel;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.util.StringConverter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class PatientDetailDialogController {
    @FXML
    private TextField tfName;
    @FXML
    private TextField tfId;
    @FXML
    private TextField tfPhone;
    @FXML
    private ToggleButton btnMale;
    @FXML
    private ToggleButton btnFemale;
    @FXML
    private DatePicker dpBirth;
    /*Cách in ra ngày theo định dạng dd/mm/yyyy nè
      LocalDate date = dpBirth.getValue();
      String format = date.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));*/

    @FXML
    public void initialize() {
        ToggleGroup genderGroup = new ToggleGroup();
        btnMale.setToggleGroup(genderGroup);
        btnFemale.setToggleGroup(genderGroup);

        btnMale.setSelected(true);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        dpBirth.setConverter(new StringConverter<LocalDate>() {
            @Override
            public String toString(LocalDate date) {
                return (date != null) ? formatter.format(date) : "";
            }

            @Override
            public LocalDate fromString(String string) {
                return (string != null && !string.isEmpty()) ? LocalDate.parse(string, formatter) : null;
            }
        });
    }

    public void setPatient(PatientModel patient) {
        if (patient != null) {
            tfName.setText(patient.getHoTen());
            tfId.setText(patient.getMaBenhNhan());
            tfPhone.setText(patient.getSoDienThoai());

            String gioitinh = patient.getGioiTinh();
            if (gioitinh == "Nam")
                btnMale.setSelected(true);
            else
                btnFemale.setSelected(true);

            LocalDate date = patient.getNgaySinh();
            dpBirth.setValue(date);
        }
    }

    public void save(ActionEvent actionEvent) {

    }
}
