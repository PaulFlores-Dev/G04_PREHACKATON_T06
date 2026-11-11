package vallegrande.edu.pe.gestion_eventos;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class ParticipantReport {

    private ParticipantReport() {
    }

    public static void write(
            Path destination,
            List<Participant> participants,
            String institution,
            String search
    ) throws IOException {
        String generatedAt = LocalDateTime.now().format(
                DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")
        );

        Map<String, List<Participant>> groups = new LinkedHashMap<>();

        for (Participant participant : participants) {
            groups.computeIfAbsent(
                    participant.getInstitucion(),
                    key -> new java.util.ArrayList<>()
            ).add(participant);
        }

        StringBuilder html = new StringBuilder("""
                <!DOCTYPE html>
                <html lang="es">
                <head>
                    <meta charset="UTF-8">
                    <meta name="viewport"
                          content="width=device-width, initial-scale=1">
                    <title>Participantes por institución</title>
                    <style>
                        body {
                            font-family: Arial, sans-serif;
                            color: #182536;
                            margin: 32px;
                            font-size: 12px;
                        }
                        h1 { color: #075985; font-size: 24px; }
                        h2 {
                            background: #e0f2fe;
                            padding: 10px;
                            font-size: 16px;
                            break-after: avoid;
                        }
                        table {
                            width: 100%;
                            border-collapse: collapse;
                            table-layout: fixed;
                            margin-bottom: 10px;
                        }
                        th, td {
                            border: 1px solid #cbd5e1;
                            padding: 8px;
                            text-align: left;
                            overflow-wrap: anywhere;
                        }
                        th { background: #f1f5f9; }
                        thead { display: table-header-group; }
                        tr { break-inside: avoid; }
                        .total { font-weight: bold; }
                        button {
                            padding: 10px 18px;
                            cursor: pointer;
                        }
                        @page { size: A4 landscape; margin: 14mm; }
                        @media print {
                            body { margin: 0; }
                            .actions { display: none; }
                        }
                    </style>
                </head>
                <body>
                    <div class="actions">
                        <button onclick="window.print()">
                            Imprimir / Guardar PDF
                        </button>
                        <p>También puedes utilizar Ctrl + P.</p>
                    </div>
                    <h1>Reporte de participantes por institución</h1>
                """);

        html.append("<p><strong>Generado:</strong> ")
                .append(escape(generatedAt)).append("</p>");

        html.append("<p><strong>Institución:</strong> ")
                .append(escape(institution == null
                        ? "Todas las instituciones" : institution))
                .append("</p>");

        html.append("<p><strong>Búsqueda:</strong> ")
                .append(escape(search.isBlank() ? "Sin búsqueda" : search))
                .append("</p>");

        if (participants.isEmpty()) {
            html.append("<p>No se encontraron participantes "
                    + "para los filtros seleccionados.</p>");
        }

        for (Map.Entry<String, List<Participant>> group : groups.entrySet()) {
            html.append("<h2>").append(escape(group.getKey())).append("</h2>");

            html.append("""
                    <table>
                        <thead>
                            <tr>
                                <th style="width:5%">ID</th>
                                <th style="width:11%">DNI</th>
                                <th style="width:18%">Nombres</th>
                                <th style="width:21%">Apellidos</th>
                                <th style="width:29%">Correo</th>
                                <th style="width:16%">Teléfono</th>
                            </tr>
                        </thead>
                        <tbody>
                    """);

            for (Participant participant : group.getValue()) {
                html.append("<tr>")
                        .append("<td>").append(participant.getId()).append("</td>")
                        .append("<td>").append(escape(participant.getDni())).append("</td>")
                        .append("<td>").append(escape(participant.getNombres())).append("</td>")
                        .append("<td>").append(escape(participant.getApellidos())).append("</td>")
                        .append("<td>").append(escape(participant.getCorreo())).append("</td>")
                        .append("<td>").append(escape(participant.getTelefono())).append("</td>")
                        .append("</tr>");
            }

            html.append("</tbody></table>")
                    .append("<p class=\"total\">Subtotal de la institución: ")
                    .append(group.getValue().size())
                    .append("</p>");
        }

        html.append("<hr><p class=\"total\">Total de participantes: ")
                .append(participants.size())
                .append("</p></body></html>");

        Files.writeString(destination, html.toString(), StandardCharsets.UTF_8);
    }

    private static String escape(String value) {
        if (value == null) {
            return "";
        }

        return value.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }
}