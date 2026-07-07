package com.example.sara_ap;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("/com/example/sara_ap/hello-view.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 600, 500);
            stage.setTitle("SARA - Panel de Acceso");
            stage.setScene(scene);
            stage.centerOnScreen();
            stage.show();
        } catch (IOException e) {
            System.err.println("Error al iniciar FXML: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        // 🔑 ESTA LÍNEA ES VITAL: Si no está, JavaFX jamás levanta el entorno de hilos gráfico
        launch(args);
    }
}