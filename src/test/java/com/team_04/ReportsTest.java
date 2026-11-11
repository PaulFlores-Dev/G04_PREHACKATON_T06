package com.team_04;

import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Date;
import java.util.List;
import java.util.Map;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRMapCollectionDataSource;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import vallegrande.edu.pe.gestion_eventos.Participant;
import vallegrande.edu.pe.gestion_eventos.ParticipantReport;
import static org.junit.jupiter.api.Assertions.*;

class ReportsTest {
    @TempDir Path temporary;
    @Test void participantHtmlEscapesDataAndRetainsPrintAndFilters() throws Exception {
        Path report = temporary.resolve("participants.html");
        ParticipantReport.write(report, List.of(new Participant(1, "<Ana>", "Pérez",
                "12345678", "ana@example.com", null, "A & B")), "A & B", "<Ana>");
        String html = Files.readString(report);
        assertTrue(html.contains("&lt;Ana&gt;"));
        assertTrue(html.contains("A &amp; B"));
        assertFalse(html.contains("<Ana>"));
        assertTrue(html.contains("window.print()"));
        assertTrue(html.contains("Total de participantes: 1"));
    }
    @Test void eventTemplateCompilesFillsAndExportsPdfWithStates() throws Exception {
        try (var template = App.class.getResourceAsStream("/reports/eventos_reporte.jrxml")) {
            var report = JasperCompileManager.compileReport(template);
            Map<String, ?> row = Map.of("id_evento", 1, "nombre", "Integración",
                    "fecha", Date.valueOf("2026-11-10"), "lugar", "Lima",
                    "modalidad", "Virtual", "estado", "Cancelado");
            var print = JasperFillManager.fillReport(report, new java.util.HashMap<>(),
                    new JRMapCollectionDataSource(List.of(row)));
            byte[] pdf = JasperExportManager.exportReportToPdf(print);
            assertTrue(pdf.length > 1000);
            assertEquals("%PDF", new String(pdf, 0, 4, java.nio.charset.StandardCharsets.US_ASCII));
        }
    }
}