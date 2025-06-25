package com.example.controllers;

import com.example.helper.NavigationHelper;
import com.example.model.UserContext;
import javafx.animation.FadeTransition;
import javafx.fxml.FXML;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

import java.util.List;

public class MenuController {
    @FXML private HBox btnDashboard, btnStaff, btnPatients, btnAnalytics,btnAppointment,btnDutySchedule,btnMedicalReport,btnMedicine, btnLogout;
    @FXML private StackPane contentArea;

    private HBox currentActiveItem;

    @FXML
    private void initialize() {
        setMenuItemHandlers();
        setMenuActive(btnDashboard);
        NavigationHelper.setContent(contentArea, "/views/dashboard.fxml");
    }

    private void setMenuItemHandlers() {
        btnDashboard.setOnMouseClicked(e -> {
            setMenuActive(btnDashboard);
            NavigationHelper.setContent(contentArea, "/views/dashboard.fxml");
        });

        btnStaff.setOnMouseClicked(e -> {
            setMenuActive(btnStaff);
            NavigationHelper.setContent(contentArea, "/views/staff.fxml");
        });
        btnDutySchedule.setOnMouseClicked(e -> {
            setMenuActive(btnDutySchedule);
            NavigationHelper.setContent(contentArea, "/views/schedule_view.fxml");
        });

        btnMedicalReport.setOnMouseClicked(e ->{
            setMenuActive(btnMedicalReport);
            NavigationHelper.setContent(contentArea,"/views/ListMedicalReport.fxml");
        });
        btnMedicine.setOnMouseClicked(e ->{
            setMenuActive(btnMedicine);
            NavigationHelper.setContent(contentArea,"/views/Medicine.fxml");
        });

        btnPatients.setOnMouseClicked(e -> {
            setMenuActive(btnPatients);
            NavigationHelper.setContent(contentArea, "/views/patient.fxml");
        });
        //bao cao thang
        btnAnalytics.setOnMouseClicked(e -> {
            setMenuActive(btnAnalytics);
            //NavigationHelper.setContent(contentArea, "/views/appointment.fxml");
        });
        btnAppointment.setOnMouseClicked(e -> {
            setMenuActive(btnAppointment);
            NavigationHelper.setContent(contentArea,"/views/appointment.fxml");
        });

        btnLogout.setOnMouseClicked(e -> {
            UserContext.getInstance().clear();
            NavigationHelper.switchTo("/views/login.fxml");
        });
    }

    private void setMenuActive(HBox selectedItem) {
        if (currentActiveItem != null) {
            currentActiveItem.getStyleClass().remove("active");
        }
        selectedItem.getStyleClass().add("active");
        currentActiveItem = selectedItem;

        // Optional: Apply fade transition for soft feedback
        applyFadeAnimation(selectedItem);
    }

    private void applyFadeAnimation(HBox item) {
        FadeTransition fade = new FadeTransition(Duration.millis(200), item);
        fade.setFromValue(0.85);
        fade.setToValue(1.0);
        fade.play();
    }
}
