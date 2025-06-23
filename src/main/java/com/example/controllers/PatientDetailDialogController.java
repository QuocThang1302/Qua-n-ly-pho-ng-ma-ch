package com.example.controllers;

import com.example.model.MedicalReportModel;
import com.example.model.PatientModel;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.io.IOException;
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
    private TableView<MedicalReportModel> tvKhamBenh;
    @FXML
    private TableColumn<MedicalReportModel, String> dateCol;
    @FXML
    private TableColumn<MedicalReportModel, String> resultCol;
    @FXML
    private TableColumn<MedicalReportModel, String> costCol;
    @FXML
    private TableColumn<MedicalReportModel, String> treatCol;
    @FXML
    private TableColumn<MedicalReportModel, String> doctorCol;

    @FXML
    public void initialize() {
        // code của toggle button
        ToggleGroup genderGroup = new ToggleGroup();
        btnMale.setToggleGroup(genderGroup);
        btnFemale.setToggleGroup(genderGroup);

        btnMale.setSelected(true);
        
        // code của date picker
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
        
        // code của table view
        dateCol.prefWidthProperty().bind(tvKhamBenh.widthProperty().multiply(0.2));
        resultCol.prefWidthProperty().bind(tvKhamBenh.widthProperty().multiply(0.2));
        treatCol.prefWidthProperty().bind(tvKhamBenh.widthProperty().multiply(0.2));
        costCol.prefWidthProperty().bind(tvKhamBenh.widthProperty().multiply(0.2));
        doctorCol.prefWidthProperty().bind(tvKhamBenh.widthProperty().multiply(0.2));

        tvKhamBenh.setOnMouseClicked((event) -> {
            if (event.getClickCount() == 2) {
                MedicalReportModel medicalReportModel = tvKhamBenh.getSelectionModel().getSelectedItem();
                if (medicalReportModel != null) {
                    showMedicalReportPopUp(medicalReportModel);
                }
            }
        });
    }

    private void showMedicalReportPopUp(MedicalReportModel medicalReportModel) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/medical_report.fxml"));
            Parent root = loader.load();

            // Lấy controller để truyền dữ liệu
            MedicalReportController controller = loader.getController();
            controller.setData(medicalReportModel, medicalReportModel.getHoaDon());

            // Tạo stage mới (window mới)
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Phiếu khám bệnh");
            dialogStage.initModality(Modality.APPLICATION_MODAL); // chặn tương tác với window chính
            dialogStage.setScene(new Scene(root));
            dialogStage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
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
