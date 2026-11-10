package com.team_04.dao;

import com.team_04.model.Evento;
import com.team_04.util.ConexionBD;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EventoDAO {

    public boolean insertar(Evento e) {
        String sql = "INSERT INTO eventos (nombre, descripcion, fecha, lugar, modalidad, estado) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = ConexionBD.getConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, e.getNombre());
            stmt.setString(2, e.getDescripcion());
            stmt.setDate(3, Date.valueOf(e.getFecha()));
            stmt.setString(4, e.getLugar());
            stmt.setString(5, e.getModalidad());
            stmt.setString(6, e.getEstado());
            return stmt.executeUpdate() > 0;
        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public List<Evento> listarTodos() {
        List<Evento> lista = new ArrayList<>();
        String sql = "SELECT * FROM eventos";
        try (Connection conn = ConexionBD.getConexion();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Evento e = new Evento(
                        rs.getInt("id_evento"),
                        rs.getString("nombre"),
                        rs.getString("descripcion"),
                        rs.getDate("fecha").toLocalDate(),
                        rs.getString("lugar"),
                        rs.getString("modalidad"),
                        rs.getString("estado")
                );
                lista.add(e);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return lista;
    }

    public boolean eliminar(int idEvento) {
        String sql = "DELETE FROM eventos WHERE id_evento=?";
        try (Connection conn = ConexionBD.getConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idEvento);
            return stmt.executeUpdate() > 0;
        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public boolean actualizar(Evento evento) {
        String sql = "UPDATE eventos SET nombre=?, descripcion=?, fecha=?, lugar=?, modalidad=?, estado=? WHERE id_evento=?";
        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, evento.getNombre());
            ps.setString(2, evento.getDescripcion());
            ps.setDate(3, java.sql.Date.valueOf(evento.getFecha()));
            ps.setString(4, evento.getLugar());
            ps.setString(5, evento.getModalidad());
            ps.setString(6, evento.getEstado());
            ps.setInt(7, evento.getIdEvento());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}