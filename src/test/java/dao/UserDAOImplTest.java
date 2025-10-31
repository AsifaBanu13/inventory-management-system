package dao;

import Models.User;
import org.junit.jupiter.api.*;
import org.mockito.*;
import util.DBConnection;

import java.sql.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserDAOImplTest {

    @Mock
    private Connection mockConnection;

    @Mock
    private PreparedStatement mockPreparedStatement;

    @Mock
    private ResultSet mockResultSet;

    private UserDAOImpl userDAO;

    private User testUser;

    private MockedStatic<DBConnection> dbConnectionMockedStatic;

    @BeforeEach
    void setUp() throws SQLException {
        MockitoAnnotations.openMocks(this);

        testUser = new User(1, "testUser", "password123", "user", "test@example.com", null, false);
        userDAO = new UserDAOImpl();

        // Mock static DBConnection.getConnection()
        dbConnectionMockedStatic = mockStatic(DBConnection.class);
        dbConnectionMockedStatic.when(DBConnection::getConnection).thenReturn(mockConnection);

        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
    }

    @AfterEach
    void tearDown() {
        // Close the static mock to avoid "already registered" error
        if (dbConnectionMockedStatic != null) {
            dbConnectionMockedStatic.close();
        }
    }

    @Test
    void testAddUserSuccess() throws SQLException {
        assertDoesNotThrow(() -> userDAO.addUser(testUser));

        verify(mockPreparedStatement).setString(1, testUser.getUsername());
        verify(mockPreparedStatement).setString(2, testUser.getPassword());
        verify(mockPreparedStatement).setString(3, testUser.getRole());
        verify(mockPreparedStatement).setString(4, testUser.getEmail());
        verify(mockPreparedStatement).executeUpdate();
    }

    @Test
    void testAddUserDuplicate() throws SQLException {
        doThrow(SQLIntegrityConstraintViolationException.class).when(mockPreparedStatement).executeUpdate();

        SQLException ex = assertThrows(SQLException.class, () -> userDAO.addUser(testUser));
        assertEquals("User with this username or email already exists.", ex.getMessage());
    }

    @Test
    void testGetUserByUsernameFound() throws SQLException {
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getInt("id")).thenReturn(testUser.getId());
        when(mockResultSet.getString("username")).thenReturn(testUser.getUsername());
        when(mockResultSet.getString("password")).thenReturn(testUser.getPassword());
        when(mockResultSet.getString("role")).thenReturn(testUser.getRole());
        when(mockResultSet.getString("email")).thenReturn(testUser.getEmail());
        when(mockResultSet.getString("otp")).thenReturn(null);
        when(mockResultSet.getBoolean("verified")).thenReturn(false);

        User user = userDAO.getUserByUsername(testUser.getUsername());

        assertNotNull(user);
        assertEquals(testUser.getUsername(), user.getUsername());
    }

    @Test
    void testGetUserByUsernameNotFound() throws SQLException {
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(false);

        User user = userDAO.getUserByUsername("unknownUser");
        assertNull(user);
    }

    @Test
    void testUpdateOTPSuccess() throws SQLException {
        when(mockPreparedStatement.executeUpdate()).thenReturn(1);

        assertDoesNotThrow(() -> userDAO.updateOTP(testUser.getEmail(), "123456"));
        verify(mockPreparedStatement).setString(1, "123456");
        verify(mockPreparedStatement).setString(2, testUser.getEmail());
    }

    @Test
    void testUpdateOTPUserNotFound() throws SQLException {
        when(mockPreparedStatement.executeUpdate()).thenReturn(0);

        SQLException ex = assertThrows(SQLException.class, () -> userDAO.updateOTP(testUser.getEmail(), "123456"));
        assertTrue(ex.getMessage().contains("No user found with email"));
    }

    @Test
    void testSetVerifiedSuccess() throws SQLException {
        when(mockPreparedStatement.executeUpdate()).thenReturn(1);

        assertDoesNotThrow(() -> userDAO.setVerified(testUser.getEmail(), true));
        verify(mockPreparedStatement).setBoolean(1, true);
        verify(mockPreparedStatement).setString(2, testUser.getEmail());
    }

    @Test
    void testSetVerifiedUserNotFound() throws SQLException {
        when(mockPreparedStatement.executeUpdate()).thenReturn(0);

        SQLException ex = assertThrows(SQLException.class, () -> userDAO.setVerified(testUser.getEmail(), true));
        assertTrue(ex.getMessage().contains("No user found with email"));
    }
}
