package dao;

import Models.User;
import util.DBConnection;

import java.sql.*;

public class UserDAOImpl implements UserDAO {

    @Override
    public void addUser(User user) throws SQLException {
        String sql = "INSERT INTO users (username, password, role, email, otp, verified) VALUES (?, ?, ?, ?, NULL, FALSE)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, user.getUsername());
            ps.setString(2, user.getPassword());
            ps.setString(3, user.getRole());
            ps.setString(4, user.getEmail());

            ps.executeUpdate();
        } catch (SQLIntegrityConstraintViolationException e) {
            // Duplicate username/email
            throw new SQLException("User with this username or email already exists.", e);
        }
    }

    @Override
    public User getUserByUsername(String username) throws SQLException {
        String sql = "SELECT * FROM users WHERE username = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRowToUser(rs);
                }
            }
        }
        return null;
    }

    @Override
    public User getUserByEmail(String email) throws SQLException {
        String sql = "SELECT * FROM users WHERE email = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRowToUser(rs);
                }
            }
        }
        return null;
    }

    @Override
    public void updateOTP(String email, String otp) throws SQLException {
        String sql = "UPDATE users SET otp = ?, verified = FALSE WHERE email = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, otp);
            ps.setString(2, email);
            int updated = ps.executeUpdate();
            if (updated == 0) {
                throw new SQLException("No user found with email: " + email);
            }
        }
    }

    @Override
    public void setVerified(String email, boolean verified) throws SQLException {
        String sql = "UPDATE users SET verified = ?, otp = NULL WHERE email = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setBoolean(1, verified);
            ps.setString(2, email);
            int updated = ps.executeUpdate();
            if (updated == 0) {
                throw new SQLException("No user found with email: " + email);
            }
        }
    }

    private User mapRowToUser(ResultSet rs) throws SQLException {
        return new User(
                rs.getInt("id"),
                rs.getString("username"),
                rs.getString("password"),
                rs.getString("role"),
                rs.getString("email"),
                rs.getString("otp"),
                rs.getBoolean("verified")
        );
    }
}
