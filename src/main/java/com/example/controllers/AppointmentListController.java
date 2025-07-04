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
import com.example.DAO.MedicalReportDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.SimpleDoubleProperty;

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

    private ObservableList<MedicalReportModel> allAppointments = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // Thiết lập độ rộng cột
        reasonCol.prefWidthProperty().bind(tvAppointment.widthProperty().multiply(0.18));
        nameCol.prefWidthProperty().bind(tvAppointment.widthProperty().multiply(0.18));
        resultCol.prefWidthProperty().bind(tvAppointment.widthProperty().multiply(0.14));
        treatCol.prefWidthProperty().bind(tvAppointment.widthProperty().multiply(0.18));
        costCol.prefWidthProperty().bind(tvAppointment.widthProperty().multiply(0.14));
        diagnoseCol.prefWidthProperty().bind(tvAppointment.widthProperty().multiply(0.18));

        // Gán cell value factory cho các cột
        nameCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getHoTen()));
        reasonCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getLyDoKham()));
        resultCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getKetQuaKham()));
        diagnoseCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getChanDoan()));
        treatCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDieuTri()));
        costCol.setCellValueFactory(cellData -> {
            Double tien = cellData.getValue().getTienKham();
            if (tien == null) tien = 0.0;
            return new SimpleStringProperty(String.format("%,.0f", tien));
        });

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

        // Load dữ liệu lần đầu
        loadAppointmentsByDate(dpDate.getValue());

        // Khi đổi ngày thì load lại dữ liệu
        dpDate.valueProperty().addListener((obs, oldDate, newDate) -> {
            if (newDate != null) {
                loadAppointmentsByDate(newDate);
            }
        });

        // Lắng nghe thay đổi ô tìm kiếm
        tfSearch.textProperty().addListener((obs, oldText, newText) -> {
            filterAppointmentsByName(newText);
        });

        tvAppointment.setOnMouseClicked((event) -> {
            if (event.getClickCount() == 2) {
                MedicalReportModel medicalReportModel = tvAppointment.getSelectionModel().getSelectedItem();
                if (medicalReportModel != null) {
                    showMedicalReportPopUp(medicalReportModel);
                }
            }
        });
    }

    private void loadAppointmentsByDate(LocalDate date) {
        allAppointments.setAll(MedicalReportDAO.getMedicalReportsByDate(date));
        filterAppointmentsByName(tfSearch.getText());
    }

    private void filterAppointmentsByName(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            tvAppointment.setItems(allAppointments);
        } else {
            String lowerKeyword = keyword.toLowerCase();
            ObservableList<MedicalReportModel> filtered = allAppointments.filtered(
                report -> report.getHoTen() != null && report.getHoTen().toLowerCase().contains(lowerKeyword)
            );
            tvAppointment.setItems(filtered);
        }
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
