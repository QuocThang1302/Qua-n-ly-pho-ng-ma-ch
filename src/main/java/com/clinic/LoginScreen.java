package com.clinic;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

public class LoginScreen extends Application {

    @Override
    public void start(Stage primaryStage) throws IOException {
        // Tải file FXML
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/login.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 800, 550);
        primaryStage.setTitle("Login Application");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        // Test kết nối database trước khi chạy ứng dụng
        DatabaseConnector.connect();
        launch(args);
    }
}