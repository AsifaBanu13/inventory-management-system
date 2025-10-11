package dao;

import dao.UserDAO;
import Models.User;
import util.DBConnection;
import java.sql.*;

public class UserDAOImpl implements UserDAO {
 @Override
 public void addUser(User user) throws SQLException {
     String sql = "INSERT INTO users (username, password, role) VALUES (?, ?, ?)";

     try (Connection conn = DBConnection.getConnection();
          PreparedStatement ps = conn.prepareStatement(sql)) {

         ps.setString(1, user.getUsername());
         ps.setString(2, user.getPassword());
         ps.setString(3, user.getRole());

         ps.executeUpdate();
         System.out.println("✅ User added successfully!");
     } catch (SQLIntegrityConstraintViolationException e) {
         System.out.println("⚠️ Username already exists!");
     }
 }

    @Override
    public User getUserByUsername(String username) throws SQLException {
        String sql = "SELECT * FROM users WHERE username = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return new User(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("role")
                );
            }
        }
        return null;
    }

}
