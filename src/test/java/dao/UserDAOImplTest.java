package dao;

import static org.junit.jupiter.api.Assertions.*;
import Models.User;
import org.junit.jupiter.api.*;
import java.sql.SQLException;
import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)

public class UserDAOImplTest {
    private static UserDAOImpl userDAO;

    @BeforeAll
    public static void setup() {
        userDAO = new UserDAOImpl();
        System.out.println("🧩 Starting UserDAOImpl Tests...");
    }

    @Test
    @Order(1)
    public void testAddUser() {
        User user = new User(0, "testuser", "password123", "admin");
        assertDoesNotThrow(() -> userDAO.addUser(user), "Adding user should not throw an exception");
    }

    @Test
    @Order(2)
    public void testGetUserByUsername() throws SQLException {
        User user = userDAO.getUserByUsername("testuser");
        assertNotNull(user, "User should exist in the database");
        assertEquals("testuser", user.getUsername(), "Username should match");
        assertEquals("admin", user.getRole(), "Role should match");
    }

    @Test
    @Order(3)
    public void testInvalidUser() throws SQLException {
        User user = userDAO.getUserByUsername("nonexistent");
        assertNull(user, "User should be null for non-existing username");
    }

    @AfterAll
    public static void cleanup() {
        System.out.println("✅ All UserDAOImpl tests completed successfully!");
    }


}