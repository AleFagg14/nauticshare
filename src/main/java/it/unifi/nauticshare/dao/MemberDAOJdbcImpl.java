package it.unifi.nauticshare.dao;

import it.unifi.nauticshare.model.Member;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


public class MemberDAOJdbcImpl implements MemberDAO{

    @Override  //Inserimento nuovo membro
    public boolean insert(Member member){
        String sql = "INSERT INTO member " +
                "(name, surname, email, password_hash, " +
                "city, birthday, has_license) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, member.getName());
            ps.setString(2, member.getSurname());
            ps.setString(3, member.getEmail());
            ps.setString(4, member.getPasswordHash());
            ps.setString(5, member.getCity());
            ps.setDate(6, Date.valueOf(member.getBirthday()));
            ps.setBoolean(7, member.isHasLicense());

            return ps.executeUpdate() > 0;  //Restituisce il numero di righe da modificare. Se >0 allora l'inserimento è andato a buon fine
        } catch (SQLException e){
            System.err.println("Errore insert Member: " + e.getMessage());
            return false;
        }
    }
    @Override
    public Member findById(int id){
        String sql = "SELECT * FROM member WHERE id = ?";
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()){
                if (rs.next()){
                return mapResultSetToMember(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Errore findById Member: " + e.getMessage());
        }
            return null;
        }

    @Override
    public List<Member> findAll() {
        List<Member> members = new ArrayList<>();
        String sql = "SELECT * FROM member";

        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                members.add(mapResultSetToMember(rs));
            }
        } catch (SQLException e) {
            System.err.println("Errore findAll Member: " + e.getMessage());
        }
        return members;
    }

    @Override
    public boolean update(Member member) {
        String sql = "UPDATE member SET name = ?, surname = ?, email = ?, password_hash = ?, " +
                "city = ?, birthday = ?, has_license = ? WHERE id = ?";

        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, member.getName());
            ps.setString(2, member.getSurname());
            ps.setString(3, member.getEmail());
            ps.setString(4, member.getPasswordHash());
            ps.setString(5, member.getCity());
            ps.setDate(6, Date.valueOf(member.getBirthday()));
            ps.setBoolean(7, member.isHasLicense());
            ps.setInt(8, member.getId());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Errore update Member: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean delete(int id) {
        String sql = "DELETE FROM member WHERE id = ?";

        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Errore delete Member: " + e.getMessage());
            return false;
        }
    }

    @Override
    public Member findByEmail(String email) {
        String sql = "SELECT * FROM member WHERE email = ?";

        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, email);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToMember(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Errore findByEmail Member: " + e.getMessage());
        }
        return null;
    }

    @Override
    public boolean existsByEmail(String email) {
        String sql = "SELECT COUNT(*) FROM member WHERE email = ?";

        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, email);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            System.err.println("Errore existsByEmail Member: " + e.getMessage());
        }
        return false;
    }

    // Metodo helper privato per evitare la duplicazione del codice di conversione ResultSet -> Member
    private Member mapResultSetToMember(ResultSet rs) throws SQLException {
        Date birthdayDate = rs.getDate("birthday");
        LocalDate birthday = (birthdayDate != null)
                ? birthdayDate.toLocalDate()
                : null;
        return new Member(
                rs.getInt("id"),
                rs.getString("name"),
                rs.getString("surname"),
                rs.getString("email"),
                rs.getString("password_hash"),
                rs.getString("city"),
                birthday,
                rs.getBoolean("has_license")
        );
    }
}