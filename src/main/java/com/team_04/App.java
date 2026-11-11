package com.team_04;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

/** A single window; cached views preserve forms and filters while navigating. */
public class App extends Application {
    private final BorderPane root = new BorderPane();
    private final Map<String, Parent> screens = new HashMap<>();

    @Override
    public void start(Stage stage) throws IOException {
        // Botones originales de tu equipo
        Button events = new Button("Eventos");
        events.setId("navEventos");

        Button participants = new Button("Participantes");
        participants.setId("navParticipantes");

        // Tu nuevo botón
        Button inscripciones = new Button("Inscripciones");
        inscripciones.setId("navInscripciones");

        // Rutas originales intactas + la tuya
        events.setOnAction(event -> navigate("/view/eventos.fxml"));
        participants.setOnAction(event -> navigate("/vallegrande/edu/pe/gestion_eventos/hello-view.fxml"));
        inscripciones.setOnAction(event -> navigate("/view/inscripciones.fxml"));

        // Se incluyen los tres botones juntos en la barra superior
        root.setTop(new ToolBar(new Label("Gestión de eventos"), events, participants, inscripciones));

        showScreen("/view/eventos.fxml");
        stage.setTitle("Gestión de Eventos y Participantes");
        stage.setMinWidth(1000);
        stage.setMinHeight(700);
        stage.setScene(new Scene(root, 1200, 850));
        stage.show();
    }

    private void navigate(String resource) {
        try {
            showScreen(resource);
        } catch (IOException ex) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "No se pudo abrir la pantalla: " + ex.getMessage());
            alert.setHeaderText("Error de navegación");
            alert.showAndWait();
        }
    }

    private void showScreen(String resource) throws IOException {
        Parent screen = screens.get(resource);
        if (screen == null) {
            screen = FXMLLoader.load(App.class.getResource(resource));
            screens.put(resource, screen);
        }
        root.setCenter(screen);
    }
}