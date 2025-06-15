package com.example.controllers;

import com.example.helper.NavigationHelper;
import com.example.model.Role;
import com.example.model.UserContext;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.List;

public class MenuController {
    @FXML private HBox btnDashboard, btnStaff, btnPatients,btnAnalytics, btnLogout;

    @FXML
    private void initialize() {
        highlightMenu(btnStaff); // Nhân viên là mặc định
    }

    private void highlightMenu(HBox activeItem) {
        List<HBox> allButtons = List.of(btnDashboard, btnStaff, btnPatients, btnAnalytics);
        for (HBox box : allButtons) {
            box.getStyleClass().remove("active");
        }
        activeItem.getStyleClass().add("active");
    }

    public void handleDashboard() {
        highlightMenu(btnDashboard);
        // Load view tương ứng
    }

    public void handleStaff() {
        highlightMenu(btnStaff);
        // Load staff view
    }

    public void handlePatients() {
        highlightMenu(btnPatients);
    }

    public void handleAnalytics() {
        highlightMenu(btnAnalytics);
    }

    public void handleSignOut() {
        UserContext.getInstance().clear();
        NavigationHelper.switchTo("/views/login.fxml");
    }
}
