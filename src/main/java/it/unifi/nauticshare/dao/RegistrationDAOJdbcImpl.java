package it.unifi.nauticshare.dao;

import it.unifi.nauticshare.model.Registration;
import it.unifi.nauticshare.model.RegistrationType;

import java.sql.*;

public class RegistrationDAOJdbcImpl implements RegistrationDAO {

    @Override
    public boolean insert(Registration registration) {
        String sql = "INSERT INTO registration (member_id, type, year) " +
                "VALUES (?, ?, ?)";

        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, registration.getMemberId());
            ps.setString(2, registration.getType().name()); // enum → String
            ps.setInt(3, registration.getYear());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Errore insert Registration: " + e.getMessage());
            return false;
        }
    }

    @Override
    public Registration findById(int id) {
        String sql = "SELECT * FROM registration WHERE id = ?";

        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToRegistration(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Errore findById Registration: " + e.getMessage());
        }
        return null;
    }

    @Override
    public boolean update(Registration registration) {
        String sql = "UPDATE registration SET type = ?, year = ? WHERE id = ?";

        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, registration.getType().name());
            ps.setInt(2, registration.getYear());
            ps.setInt(3, registration.getId());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Errore update Registration: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean delete(int id) {
        String sql = "DELETE FROM registration WHERE id = ?";

        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Errore delete Registration: " + e.getMessage());
            return false;
        }
    }

    @Override
    public Registration findByMemberId(int memberId) {
        // Ogni membro ha al massimo una iscrizione (vincolo UNIQUE su member_id)
        // quindi findByMemberId restituisce un singolo oggetto, non una lista
        String sql = "SELECT * FROM registration WHERE member_id = ?";

        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, memberId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToRegistration(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Errore findByMemberId Registration: " + e.getMessage());
        }
        return null;
    }

    @Override
    public boolean existsByMemberId(int memberId) {
        String sql = "SELECT COUNT(*) FROM registration WHERE member_id = ?";

        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, memberId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            System.err.println("Errore existsByMemberId Registration: " + e.getMessage());
        }
        return false;
    }

    private Registration mapResultSetToRegistration(ResultSet rs) throws SQLException {
        return new Registration(
                rs.getInt("id"),
                rs.getInt("member_id"),
                RegistrationType.valueOf(rs.getString("type")), // String → enum
                rs.getInt("year")
        );
    }
}