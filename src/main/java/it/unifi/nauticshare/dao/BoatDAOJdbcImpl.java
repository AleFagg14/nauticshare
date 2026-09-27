package it.unifi.nauticshare.dao;

import it.unifi.nauticshare.model.Boat;
import it.unifi.nauticshare.model.BoatType;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class BoatDAOJdbcImpl implements BoatDAO {

    @Override
    public boolean insert(Boat boat) {
        String sql = "INSERT INTO boat (reg_num, name, type, seats, photo_url, description) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, boat.getRegNum());
            ps.setString(2, boat.getName());
            ps.setString(3, boat.getType().name()); // enum → String
            ps.setInt(4, boat.getSeats());
            ps.setString(5, boat.getPhotoUrl());
            ps.setString(6, boat.getDescription());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Errore insert Boat: " + e.getMessage());
            return false;
        }
    }

    @Override
    public Boat findById(int id) {
        String sql = "SELECT * FROM boat WHERE id = ?";

        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToBoat(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Errore findById Boat: " + e.getMessage());
        }
        return null;
    }

    @Override
    public List<Boat> findAll() {
        List<Boat> boats = new ArrayList<>();
        String sql = "SELECT * FROM boat";

        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                boats.add(mapResultSetToBoat(rs));
            }
        } catch (SQLException e) {
            System.err.println("Errore findAll Boat: " + e.getMessage());
        }
        return boats;
    }

    @Override
    public boolean update(Boat boat) {
        String sql = "UPDATE boat SET reg_num = ?, name = ?, type = ?, " +
                "seats = ?, photo_url = ?, description = ? WHERE id = ?";

        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, boat.getRegNum());
            ps.setString(2, boat.getName());
            ps.setString(3, boat.getType().name());
            ps.setInt(4, boat.getSeats());
            ps.setString(5, boat.getPhotoUrl());
            ps.setString(6, boat.getDescription());
            ps.setInt(7, boat.getId());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Errore update Boat: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean delete(int id) {
        String sql = "DELETE FROM boat WHERE id = ?";

        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Errore delete Boat: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean existsByRegistrationNumber(String registrationNumber) {
        String sql = "SELECT COUNT(*) FROM boat WHERE reg_num = ?";

        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, registrationNumber);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            System.err.println("Errore existsByRegNum Boat: " + e.getMessage());
        }
        return false;
    }

    @Override
    public List<Boat> findAvailable(LocalDate startDate, LocalDate endDate) {
        List<Boat> availableBoats = new ArrayList<>();

        // Esclude barche occupate sia da Rental che da Booking nel periodo
        String sql = "SELECT * FROM boat WHERE id NOT IN (" +
                "  SELECT boat_id FROM rental " +
                "  WHERE NOT (end_date < ? OR start_date > ?)" +
                ") AND id NOT IN (" +
                "  SELECT boat_id FROM booking " +
                "  WHERE NOT (date < ? OR date > ?)" +
                ")";

        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setDate(1, Date.valueOf(startDate));
            ps.setDate(2, Date.valueOf(endDate));
            ps.setDate(3, Date.valueOf(startDate));
            ps.setDate(4, Date.valueOf(endDate));

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    availableBoats.add(mapResultSetToBoat(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Errore findAvailable Boat: " + e.getMessage());
        }
        return availableBoats;
    }

    // Helper: converte una riga del ResultSet in un oggetto Boat
    private Boat mapResultSetToBoat(ResultSet rs) throws SQLException {
        return new Boat(
                rs.getInt("id"),
                rs.getString("reg_num"),
                rs.getString("name"),
                BoatType.valueOf(rs.getString("type")), // String → enum
                rs.getInt("seats"),
                rs.getString("photo_url"),
                rs.getString("description")
        );
    }
}