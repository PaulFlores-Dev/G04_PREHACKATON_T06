package vallegrande.edu.pe.gestion_eventos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DatabaseCheck {

    public static void main(String[] args) {
        String sql = """
                SELECT id_participante, nombres, apellidos, institucion
                FROM participantes
                ORDER BY id_participante
                """;

        try (
                Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql);
                ResultSet result = statement.executeQuery()
        ) {
            System.out.println("Conexión y consulta correctas.");

            int total = 0;

            while (result.next()) {
                System.out.printf(
                        "%d | %s %s | %s%n",
                        result.getInt("id_participante"),
                        result.getString("nombres"),
                        result.getString("apellidos"),
                        result.getString("institucion")
                );

                total++;
            }

            System.out.println("Total de participantes: " + total);

        } catch (SQLException e) {
            System.err.println("Error de base de datos: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
