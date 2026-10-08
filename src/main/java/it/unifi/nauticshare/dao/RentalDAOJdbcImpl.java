package it.unifi.nauticshare.dao;

import it.unifi.nauticshare.model.Rental;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class RentalDAOJdbcImpl implements RentalDAO {

    @Override
    public boolean insert(Rental rental) {
        String sql = "INSERT INTO rental " +
                "(member_id, boat_id, start_date, end_date, " +
                "num_participants, total_price) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, rental.getMemberId());
            ps.setInt(2, rental.getBoatId());
            ps.setDate(3, Date.valueOf(rental.getStartDate()));
            ps.setDate(4, Date.valueOf(rental.getEndDate()));
            ps.setInt(5, rental.getNumParticipants());
            ps.setDouble(6, rental.getTotalPrice());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Errore insert Rental: " + e.getMessage());
            return false;
        }
    }

    @Override
    public Rental findById(int id) {
        String sql = "SELECT * FROM rental WHERE id = ?";

        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToRental(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Errore findById Rental: " + e.getMessage());
        }
        return null;
    }

    @Override
    public List<Rental> findAll() {
        List<Rental> rentals = new ArrayList<>();
        String sql = "SELECT * FROM rental";

        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                rentals.add(mapResultSetToRental(rs));
            }
        } catch (SQLException e) {
            System.err.println("Errore findAll Rental: " + e.getMessage());
        }
        return rentals;
    }

    @Override
    public boolean delete(int id) {
        String sql = "DELETE FROM rental WHERE id = ?";

        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Errore delete Rental: " + e.getMessage());
            return false;
        }
    }

    @Override
    public List<Rental> findByMemberId(int memberId) {
        List<Rental> rentals = new ArrayList<>();
        String sql = "SELECT * FROM rental WHERE member_id = ?";

        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, memberId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    rentals.add(mapResultSetToRental(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Errore findByMemberId Rental: " + e.getMessage());
        }
        return rentals;
    }

    @Override
    public List<Rental> findFuture(int memberId) {
        List<Rental> rentals = new ArrayList<>();
        String sql = "SELECT * FROM rental WHERE member_id = ? " +
                "AND start_date >= CURRENT_DATE " +
                "ORDER BY start_date ASC";

        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, memberId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    rentals.add(mapResultSetToRental(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Errore findFuture Rental: " + e.getMessage());
        }
        return rentals;
    }

    @Override
    public boolean hasOverlap(int boatId, LocalDate startDate, LocalDate endDate) {
        // Logica: c'è overlap se NON è vero che il rental finisce prima
        // dell'inizio o inizia dopo la fine del periodo cercato
        String sql = "SELECT COUNT(*) FROM rental " +
                "WHERE boat_id = ? " +
                "AND NOT (end_date < ? OR start_date > ?)";

        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, boatId);
            ps.setDate(2, Date.valueOf(startDate));
            ps.setDate(3, Date.valueOf(endDate));

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            System.err.println("Errore hasOverlap Rental: " + e.getMessage());
        }
        return false;
    }


    @Override
    public boolean update(Rental rental) {
        String sql = "UPDATE rental " +
                "SET num_participants = ?, total_price = ? " +
                "WHERE id = ?";

        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, rental.getNumParticipants());
            ps.setDouble(2, rental.getTotalPrice());
            ps.setInt(3, rental.getId());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Errore update Rental: " + e.getMessage());
            return false;
        }
    }

    private Rental mapResultSetToRental(ResultSet rs) throws SQLException {
        return new Rental(
                rs.getInt("id"),
                rs.getInt("member_id"),
                rs.getInt("boat_id"),
                rs.getDate("start_date").toLocalDate(),
                rs.getDate("end_date").toLocalDate(),
                rs.getInt("num_participants"),
                rs.getDouble("total_price")
        );
    }
}