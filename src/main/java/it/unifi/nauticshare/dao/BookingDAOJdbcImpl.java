package it.unifi.nauticshare.dao;

import it.unifi.nauticshare.model.Booking;
import it.unifi.nauticshare.model.RegistrationType;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class BookingDAOJdbcImpl implements BookingDAO {

    @Override
    public boolean insert(Booking booking) {
        String sql = "INSERT INTO booking " +
                "(member_id, boat_id, skipper_id, date, " +
                "seats_booked, total_price, reg_type) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, booking.getMemberId());
            ps.setInt(2, booking.getBoatId());

            // skipperId è Integer nullable — usiamo setNull se assente
            if (booking.getSkipperId() != null) {
                ps.setInt(3, booking.getSkipperId());
            } else {
                ps.setNull(3, Types.INTEGER);
            }

            ps.setDate(4, Date.valueOf(booking.getDate()));
            ps.setInt(5, booking.getSeatsBooked());
            ps.setDouble(6, booking.getTotalPrice());
            ps.setString(7, booking.getRegType().name()); // enum → String

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Errore insert Booking: " + e.getMessage());
            return false;
        }
    }

    @Override
    public Booking findById(int id) {
        String sql = "SELECT * FROM booking WHERE id = ?";

        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToBooking(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Errore findById Booking: " + e.getMessage());
        }
        return null;
    }

    @Override
    public List<Booking> findAll() {
        List<Booking> bookings = new ArrayList<>();
        String sql = "SELECT * FROM booking ORDER BY date ASC";

        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                bookings.add(mapResultSetToBooking(rs));
            }
        } catch (SQLException e) {
            System.err.println("Errore findAll Booking: " + e.getMessage());
        }
        return bookings;
    }

    @Override
    public boolean delete(int id) {
        String sql = "DELETE FROM booking WHERE id = ?";

        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Errore delete Booking: " + e.getMessage());
            return false;
        }
    }

    @Override
    public List<Booking> findByMemberId(int memberId) {
        List<Booking> bookings = new ArrayList<>();
        String sql = "SELECT * FROM booking WHERE member_id = ? ORDER BY date ASC";

        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, memberId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    bookings.add(mapResultSetToBooking(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Errore findByMemberId Booking: " + e.getMessage());
        }
        return bookings;
    }

    @Override
    public List<Booking> findFuture(int memberId) {
        List<Booking> bookings = new ArrayList<>();
        // CURRENT_DATE è una funzione PostgreSQL che restituisce la data odierna
        String sql = "SELECT * FROM booking WHERE member_id = ? " +
                "AND date >= CURRENT_DATE ORDER BY date ASC";

        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, memberId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    bookings.add(mapResultSetToBooking(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Errore findFuture Booking: " + e.getMessage());
        }
        return bookings;
    }

    @Override
    public List<Booking> findPastBySkipperId(int skipperId) {
        List<Booking> bookings = new ArrayList<>();
        // Recupera i booking passati con quello skipper —
        // usati per abilitare la valutazione (UC-11)
        String sql = "SELECT * FROM booking WHERE skipper_id = ? " +
                "AND date < CURRENT_DATE ORDER BY date DESC";

        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, skipperId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    bookings.add(mapResultSetToBooking(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Errore findPastBySkipperId Booking: " + e.getMessage());
        }
        return bookings;
    }

    @Override
    public boolean hasOverlap(int boatId, LocalDate date) {
        // Una barca è occupata in una data se esiste già un booking
        // per quella barca in quel giorno, OPPURE un rental che copre quel giorno
        String sql = "SELECT COUNT(*) FROM (" +
                "  SELECT id FROM booking " +
                "  WHERE boat_id = ? AND date = ? " +
                "  UNION ALL " +
                "  SELECT id FROM rental " +
                "  WHERE boat_id = ? AND start_date <= ? AND end_date >= ?" +
                ") AS conflicts";

        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, boatId);
            ps.setDate(2, Date.valueOf(date));
            ps.setInt(3, boatId);
            ps.setDate(4, Date.valueOf(date));
            ps.setDate(5, Date.valueOf(date));

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            System.err.println("Errore hasOverlap Booking: " + e.getMessage());
        }
        return false;
    }

    // Helper: converte una riga ResultSet in oggetto Booking
    private Booking mapResultSetToBooking(ResultSet rs) throws SQLException {
        // skipperId può essere NULL nel DB — usiamo getObject per gestirlo
        Integer skipperId = (Integer) rs.getObject("skipper_id");

        return new Booking(
                rs.getInt("id"),
                rs.getInt("member_id"),
                rs.getInt("boat_id"),
                skipperId,                                    // nullable
                rs.getDate("date").toLocalDate(),
                rs.getInt("seats_booked"),
                rs.getDouble("total_price"),
                RegistrationType.valueOf(rs.getString("reg_type")) // String → enum
        );
    }
}