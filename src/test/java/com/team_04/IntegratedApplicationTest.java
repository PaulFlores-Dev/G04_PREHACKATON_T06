package com.team_04;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.concurrent.*;
import javafx.application.Platform;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import net.sf.jasperreports.engine.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import vallegrande.edu.pe.gestion_eventos.*;
import static org.junit.jupiter.api.Assertions.*;

/** Opt-in: opens a real JavaFX window and queries the configured MySQL; no data writes. */
@EnabledIfSystemProperty(named = "integrationTests", matches = "true")
class IntegratedApplicationTest {
    private <T> T fx(Callable<T> operation) throws Exception {
        FutureTask<T> task = new FutureTask<>(operation);
        Platform.runLater(task);
        return task.get(20, TimeUnit.SECONDS);
    }

    @Test void realEntryPointNavigatesBothWaysQueriesTablesAndGeneratesReports() throws Exception {
        Platform.startup(() -> {});
        Stage stage = null;
        try {
            int participantCount;
            int eventCount;
            try (var connection = DatabaseConnection.getConnection(); var statement = connection.createStatement()) {
                try (var rows = statement.executeQuery("SELECT COUNT(*) FROM participantes")) {
                    rows.next(); participantCount = rows.getInt(1);
                }
                try (var rows = statement.executeQuery("SELECT COUNT(*) FROM eventos")) {
                    rows.next(); eventCount = rows.getInt(1);
                }
            }
            stage = fx(() -> {
                Stage window = new Stage();
                new App().start(window);
                return window;
            });
            final Stage window = stage;
            Parent events = fx(() -> (Parent) ((BorderPane) window.getScene().getRoot()).getCenter());
            assertEquals(eventCount, fx(() -> ((TableView<?>) events.lookup("#tablaEventos")).getItems().size()));
            fx(() -> {
                ((Button) window.getScene().lookup("#navParticipantes")).fire();
                return null;
            });
            Parent participants = fx(() -> (Parent) ((BorderPane) window.getScene().getRoot()).getCenter());
            long deadline = System.nanoTime() + TimeUnit.SECONDS.toNanos(15);
            while (!fx(() -> ((Label) participants.lookup("#statusLabel")).getText().startsWith("Mostrando "))) {
                if (System.nanoTime() > deadline) fail("Participants did not finish loading");
                Thread.sleep(50);
            }
            assertEquals(participantCount, fx(() -> ((TableView<?>) participants.lookup("#participantsTable")).getItems().size()));
            fx(() -> {
                ((Button) window.getScene().lookup("#navEventos")).fire();
                return null;
            });
            assertSame(events, fx(() -> ((BorderPane) window.getScene().getRoot()).getCenter()));
            fx(() -> {
                ((Button) window.getScene().lookup("#navParticipantes")).fire();
                return null;
            });
            assertSame(participants, fx(() -> ((BorderPane) window.getScene().getRoot()).getCenter()));
            Path output = Path.of("target", "verification");
            Files.createDirectories(output);
            ParticipantReport.write(output.resolve("participantes.html"), new ParticipantDao().findAll(), null, "");
            try (var connection = DatabaseConnection.getConnection();
                 var template = App.class.getResourceAsStream("/reports/eventos_reporte.jrxml")) {
                var print = JasperFillManager.fillReport(JasperCompileManager.compileReport(template), new java.util.HashMap<>(), connection);
                assertFalse(print.getPages().isEmpty());
                JasperExportManager.exportReportToPdfFile(print, output.resolve("eventos.pdf").toString());
            }
            System.out.printf("VERIFIED: Eventos=%d, Participantes=%d; navigation both ways; HTML and PDF reports.%n",
                    eventCount, participantCount);
        } finally {
            if (stage != null) {
                Stage window = stage;
                fx(() -> { window.close(); return null; });
            }
            Platform.exit();
        }
    }
}