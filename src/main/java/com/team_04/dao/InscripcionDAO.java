package com.team_04.dao;

import com.team_04.model.Inscripcion;
import com.team_04.util.ConexionBD;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class InscripcionDAO {

    public boolean guardar(Inscripcion i) {
        String sql = "INSERT INTO inscripciones (id_participante, categoria, fecha_inscripcion, estado) VALUES (?, ?, ?, ?)";
        try (Connection conn = ConexionBD.getConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, i.getIdParticipante());
            pstmt.setString(2, i.getCategoria());
            pstmt.setString(3, i.getFechaInscripcion());
            pstmt.setString(4, i.getEstado());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Inscripcion> listar() {
        List<Inscripcion> lista = new ArrayList<>();
        String sql = "SELECT * FROM inscripciones";
        try (Connection conn = ConexionBD.getConexion();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Inscripcion i = new Inscripcion(
                        rs.getInt("id_inscripcion"),
                        rs.getInt("id_participante"),
                        rs.getString("categoria"),
                        rs.getString("fecha_inscripcion"),
                        rs.getString("estado")
                );
                lista.add(i);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    public boolean eliminar(int idInscripcion) {
        String sql = "DELETE FROM inscripciones WHERE id_inscripcion = ?";
        try (Connection conn = ConexionBD.getConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idInscripcion);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}