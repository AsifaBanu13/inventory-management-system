package Models;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class User {
    int id;
    String username;
    String password;
    String role;

    // ✅ Default Constructor
    public User() {}

    // ✅ Parameterized Constructor
    public User(int id, String username, String password, String role) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.role = role;
    }

    // ✅ Getters and Setters
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }
    public void setRole(String role) {
        this.role = role;
    }


    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username=" + username + '\'' +
                ", password=" + password + '\'' +
                ", role=" + role + '\''+
    "}";
    }
    // ✅ Function 1: Add User to Database
    public boolean addUser(Connection conn) {
        String sql = "INSERT INTO users (username, password, role) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, this.username);
            stmt.setString(2, this.password);
            stmt.setString(3, this.role);
            int rows = stmt.executeUpdate();
            return rows > 0; // true if user inserted successfully
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // ✅ Function 2: Get User by Username
    public static User getUser(Connection conn, String username) {
        String sql = "SELECT * FROM users WHERE username = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new User(
                            rs.getInt("id"),
                            rs.getString("username"),
                            rs.getString("password"),
                            rs.getString("role")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; // no user found
    }
}

