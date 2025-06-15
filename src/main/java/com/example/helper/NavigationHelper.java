package com.example.helper;

import com.example.LoginScreen;
import com.example.model.Role;
import com.example.model.UserContext;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Arrays;
public class NavigationHelper {
    public static void switchTo(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(NavigationHelper.class.getResource(fxmlPath));
            Scene scene = new Scene(loader.load());
            Stage stage = LoginScreen.getPrimaryStage();
            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
