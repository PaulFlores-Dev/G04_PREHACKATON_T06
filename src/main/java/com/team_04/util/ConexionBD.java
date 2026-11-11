package com.team_04.util;

import java.sql.Connection;
import java.sql.SQLException;
import vallegrande.edu.pe.gestion_eventos.DatabaseConnection;

/** Both DAOs and the Jasper report use the same external database settings. */
public final class ConexionBD {
    private ConexionBD() {}
    public static Connection getConexion() throws SQLException {
        return DatabaseConnection.getConnection();
    }
}