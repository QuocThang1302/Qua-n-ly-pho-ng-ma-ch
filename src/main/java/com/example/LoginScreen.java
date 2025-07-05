package com.example;
import com.example.utils.DatabaseConnector;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

import static javafx.application.Application.launch;

public class LoginScreen extends Application {
    private static Stage primaryStage;

    @Override
    public void start(Stage stage) throws IOException {

        primaryStage = stage;
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/login.fxml"));
        Scene scene = new Scene(loader.load());
        stage.setScene(scene);
        stage.show();
    }

    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    public static void main(String[] args) {
        launch(args);
        // Test kết nối database trước khi chạy ứng dụng
        DatabaseConnector.connect();
    }
}
