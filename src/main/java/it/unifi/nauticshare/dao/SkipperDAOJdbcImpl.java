package it.unifi.nauticshare.dao;

import it.unifi.nauticshare.model.Skipper;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class SkipperDAOJdbcImpl implements SkipperDAO {

    // Query base con JOIN su member per recuperare tutti i campi
    private static final String SELECT_BASE =
            "SELECT s.id AS skipper_id, s.boat_id, s.certificate, s.avg_rating, s.bio, " +
                    "m.id AS member_id, m.name, m.surname, m.email, m.password_hash, " +
                    "m.city, m.birthday, m.has_license " +
                    "FROM skipper s JOIN member m ON s.member_id = m.id ";

    @Override
    public boolean insert(Skipper skipper) {
        String sql = "INSERT INTO skipper (member_id, boat_id, certificate, avg_rating, bio) " +
                "VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, skipper.getId()); // member_id = id ereditato da Member
            ps.setInt(2, skipper.getBoatId());
            ps.setString(3, skipper.getCertificate());
            ps.setDouble(4, skipper.getAvgRating());
            ps.setString(5, skipper.getBio());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Errore insert Skipper: " + e.getMessage());
            return false;
        }
    }

    @Override
    public Skipper findById(int id) {
        String sql = SELECT_BASE + "WHERE s.id = ?";

        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToSkipper(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Errore findById Skipper: " + e.getMessage());
        }
        return null;
    }

    @Override
    public List<Skipper> findAll() {
        List<Skipper> skippers = new ArrayList<>();

        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(SELECT_BASE);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                skippers.add(mapResultSetToSkipper(rs));
            }
        } catch (SQLException e) {
            System.err.println("Errore findAll Skipper: " + e.getMessage());
        }
        return skippers;
    }

    @Override
    public boolean update(Skipper skipper) {
        String sql = "UPDATE skipper SET boat_id = ?, certificate = ?, " +
                "avg_rating = ?, bio = ? WHERE member_id = ?";

        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, skipper.getBoatId());
            ps.setString(2, skipper.getCertificate());
            ps.setDouble(3, skipper.getAvgRating());
            ps.setString(4, skipper.getBio());
            ps.setInt(5, skipper.getId()); // member_id

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Errore update Skipper: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean delete(int id) {
        String sql = "DELETE FROM skipper WHERE id = ?";

        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Errore delete Skipper: " + e.getMessage());
            return false;
        }
    }

    @Override
    public Skipper findByMemberId(int memberId) {
        String sql = SELECT_BASE + "WHERE s.member_id = ?";

        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, memberId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToSkipper(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Errore findByMemberId Skipper: " + e.getMessage());
        }
        return null;
    }

    @Override
    public Skipper findByBoatId(int boatId) {
        String sql = SELECT_BASE + "WHERE s.boat_id = ?";

        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, boatId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToSkipper(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Errore findByBoatId Skipper: " + e.getMessage());
        }
        return null;
    }

    @Override
    public boolean updateRating(int skipperId, double newRating) {
        String sql = "UPDATE skipper SET avg_rating = ? WHERE id = ?";

        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setDouble(1, newRating);
            ps.setInt(2, skipperId);

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Errore updateRating Skipper: " + e.getMessage());
            return false;
        }
    }

    // Helper: JOIN tra skipper e member per ricostruire l'oggetto completo
    private Skipper mapResultSetToSkipper(ResultSet rs) throws SQLException {
        Date birthdayDate = rs.getDate("birthday");
        LocalDate birthday = (birthdayDate != null)
                ? birthdayDate.toLocalDate()
                : null;
        return new Skipper(
                rs.getInt("member_id"),     // id di Member
                rs.getString("name"),
                rs.getString("surname"),
                rs.getString("email"),
                rs.getString("password_hash"),
                rs.getString("city"),
                birthday,
                rs.getBoolean("has_license"),
                rs.getInt("boat_id"),
                rs.getString("certificate"),
                rs.getDouble("avg_rating"),
                rs.getString("bio")
        );
    }
}