package vallegrande.edu.pe.gestion_eventos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ParticipantDao {

    public List<Participant> findAll() throws SQLException {
        String sql = """
                SELECT id_participante, nombres, apellidos,
                       dni, correo, telefono, institucion
                FROM participantes
                ORDER BY institucion, apellidos, nombres, id_participante
                """;

        List<Participant> participants = new ArrayList<>();

        try (
                Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql);
                ResultSet result = statement.executeQuery()
        ) {
            while (result.next()) {
                participants.add(new Participant(
                        result.getInt("id_participante"),
                        result.getString("nombres"),
                        result.getString("apellidos"),
                        result.getString("dni"),
                        result.getString("correo"),
                        result.getString("telefono"),
                        result.getString("institucion")
                ));
            }
        }

        return participants;
    }

    public void insert(Participant participant) throws SQLException {
        String sql = """
                INSERT INTO participantes
                    (nombres, apellidos, dni, correo, telefono, institucion)
                VALUES (?, ?, ?, ?, ?, ?)
                """;

        try (
                Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            setParticipantParameters(statement, participant);
            statement.executeUpdate();
        }
    }

    public void update(Participant participant) throws SQLException {
        String sql = """
                UPDATE participantes
                SET nombres = ?, apellidos = ?, dni = ?,
                    correo = ?, telefono = ?, institucion = ?
                WHERE id_participante = ?
                """;

        try (
                Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            setParticipantParameters(statement, participant);
            statement.setInt(7, participant.getId());

            if (statement.executeUpdate() == 0) {
                throw new SQLException(
                        "No se encontró el participante. Actualiza el listado."
                );
            }
        }
    }

    public void delete(int id) throws SQLException {
        String sql = """
                DELETE FROM participantes
                WHERE id_participante = ?
                """;

        try (
                Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setInt(1, id);

            if (statement.executeUpdate() == 0) {
                throw new SQLException(
                        "El participante ya no existe. Actualiza el listado."
                );
            }
        }
    }

    private void setParticipantParameters(
            PreparedStatement statement,
            Participant participant
    ) throws SQLException {
        statement.setString(1, participant.getNombres());
        statement.setString(2, participant.getApellidos());
        statement.setString(3, participant.getDni());
        statement.setString(4, participant.getCorreo());
        statement.setString(5, participant.getTelefono());
        statement.setString(6, participant.getInstitucion());
    }
}