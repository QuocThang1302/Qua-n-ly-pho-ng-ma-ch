package com.example.controllers;

import com.example.model.AppointmentModel;
import com.example.model.MedicalReportModel;
import com.example.model.StaffModel;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class AppointmentListController {
    @FXML
    TextField tfSearch;
    @FXML
    DatePicker dpDate;
    @FXML
    private TableView<MedicalReportModel> tvAppointment;
    @FXML
    private TableColumn<MedicalReportModel, String> reasonCol;
    @FXML
    private TableColumn<MedicalReportModel, String> diagnoseCol;
    @FXML
    private TableColumn<MedicalReportModel, String> nameCol;
    @FXML
    private TableColumn<MedicalReportModel, String> resultCol;
    @FXML
    private TableColumn<MedicalReportModel, String> treatCol;
    @FXML
    private TableColumn<MedicalReportModel, String> costCol;

    @FXML
    public void initialize() {
        // Thiết lập độ rộng cột
        reasonCol.prefWidthProperty().bind(tvAppointment.widthProperty().multiply(0.18));
        nameCol.prefWidthProperty().bind(tvAppointment.widthProperty().multiply(0.18));
        resultCol.prefWidthProperty().bind(tvAppointment.widthProperty().multiply(0.14));
        treatCol.prefWidthProperty().bind(tvAppointment.widthProperty().multiply(0.18));
        costCol.prefWidthProperty().bind(tvAppointment.widthProperty().multiply(0.14));
        diagnoseCol.prefWidthProperty().bind(tvAppointment.widthProperty().multiply(0.18));

        // Thiết lập datepicker
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        dpDate.setConverter(new StringConverter<LocalDate>() {
            @Override
            public String toString(LocalDate date) {
                return (date != null) ? formatter.format(date) : "";
            }

            @Override
            public LocalDate fromString(String string) {
                return (string != null && !string.isEmpty()) ? LocalDate.parse(string, formatter) : null;
            }
        });

        LocalDateTime dateTime = LocalDateTime.now();
        dpDate.setValue(dateTime.toLocalDate());

        tvAppointment.setOnMouseClicked((event) -> {
            if (event.getClickCount() == 2) {
                MedicalReportModel medicalReportModel = tvAppointment.getSelectionModel().getSelectedItem();
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
}
