package vallegrande.edu.pe.gestion_eventos;

import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public final class DatabaseConnection {

    private DatabaseConnection() {
    }

    public static Connection getConnection() throws SQLException {
        Properties properties = new Properties();
        Path configPath = Path.of("db.properties");

        try (Reader reader = Files.newBufferedReader(
                configPath, StandardCharsets.UTF_8)) {

            properties.load(reader);

        } catch (IOException e) {
            throw new SQLException(
                    "No se pudo leer: " + configPath.toAbsolutePath(), e);
        }

        String url = properties.getProperty("db.url");
        String user = properties.getProperty("db.user");
        String password = properties.getProperty("db.password");

        if (url == null || user == null || password == null) {
            throw new SQLException(
                    "Faltan db.url, db.user o db.password en db.properties.");
        }

        return DriverManager.getConnection(url, user, password);
    }
}
