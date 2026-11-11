package com.team_04;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApp extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Carga la nueva vista de inscripciones
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/inscripciones.fxml"));
        Scene scene = new Scene(loader.load());

        primaryStage.setTitle("Sistema Hackathon - Gestión de Inscripciones");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}