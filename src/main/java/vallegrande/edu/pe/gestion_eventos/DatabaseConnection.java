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
    private DatabaseConnection() {}

    public static Connection getConnection() throws SQLException {
        Properties properties = new Properties();
        // Explicit override, then root configuration, then the former module location.
        String explicit = System.getProperty("db.config", System.getenv("DB_CONFIG"));
        Path path = explicit != null ? Path.of(explicit) : Path.of("db.properties");
        if (explicit == null && !Files.exists(path)) {
            path = Path.of("gestion_eventos", "db.properties");
        }
        if (explicit != null || Files.exists(path)) {
            try (Reader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
                properties.load(reader);
            } catch (IOException ex) {
                throw new SQLException("No se pudo leer " + path.toAbsolutePath(), ex);
            }
        }
        String url = setting(properties, "db.url", "DB_URL");
        String user = setting(properties, "db.user", "DB_USER");
        String password = setting(properties, "db.password", "DB_PASSWORD");
        if (url == null || user == null || password == null) {
            throw new SQLException("Configura db.url, db.user y db.password en db.properties "
                    + "(raíz del proyecto) o DB_URL, DB_USER y DB_PASSWORD.");
        }
        return DriverManager.getConnection(url, user, password);
    }

    private static String setting(Properties properties, String key, String environment) {
        String value = System.getenv(environment);
        return value != null ? value : properties.getProperty(key);
    }
}