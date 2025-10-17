package dao;

import Models.User;
import util.DBConnection;
import org.junit.jupiter.api.*;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;

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
    private MockedStatic<DBConnection> mockedDBConnection;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);

        // Enable test mode
        DBConnection.enableTestMode();

        // Mock static DBConnection.getConnection()
        mockedDBConnection = mockStatic(DBConnection.class);
        mockedDBConnection.when(DBConnection::getConnection).thenReturn(mockConnection);

        userDAO = new UserDAOImpl();
    }

    @AfterEach
    void tearDown() {
        if (mockedDBConnection != null) {
            mockedDBConnection.close();
        }
    }

    @Test
    void testAddUserSuccess() throws Exception {
        User user = new User("ajit", "1234", "ADMIN", "ajit@example.com");

        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);

        userDAO.addUser(user);

        verify(mockPreparedStatement).setString(1, "ajit");
        verify(mockPreparedStatement).setString(2, "1234");
        verify(mockPreparedStatement).setString(3, "ADMIN");
        verify(mockPreparedStatement).setString(4, "ajit@example.com");
        verify(mockPreparedStatement).executeUpdate();
    }

    @Test
    void testAddUserDuplicateUsername() throws Exception {
        User user = new User("existingUser", "abcd", "USER", "existing@example.com");

        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        doThrow(new SQLIntegrityConstraintViolationException("Duplicate username"))
                .when(mockPreparedStatement).executeUpdate();

        assertThrows(SQLIntegrityConstraintViolationException.class, () -> userDAO.addUser(user));
        verify(mockPreparedStatement).executeUpdate();
    }

    @Test
    void testGetUserByUsernameFound() throws Exception {
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);

        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getInt("id")).thenReturn(1);
        when(mockResultSet.getString("username")).thenReturn("ajit");
        when(mockResultSet.getString("password")).thenReturn("1234");
        when(mockResultSet.getString("role")).thenReturn("ADMIN");
        when(mockResultSet.getString("email")).thenReturn("ajit@example.com");
        when(mockResultSet.getString("otp")).thenReturn(null);
        when(mockResultSet.getBoolean("verified")).thenReturn(false);

        User result = userDAO.getUserByUsername("ajit");

        assertNotNull(result);
        assertEquals("ajit", result.getUsername());
        assertEquals("ADMIN", result.getRole());
        assertEquals("ajit@example.com", result.getEmail());
        assertFalse(result.isVerified());
    }

    @Test
    void testGetUserByUsernameNotFound() throws Exception {
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(false);

        User result = userDAO.getUserByUsername("nonexistent");
        assertNull(result);
    }

    @Test
    void testGetUserByEmailFound() throws Exception {
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);

        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getInt("id")).thenReturn(2);
        when(mockResultSet.getString("username")).thenReturn("ajit2");
        when(mockResultSet.getString("password")).thenReturn("pass2");
        when(mockResultSet.getString("role")).thenReturn("USER");
        when(mockResultSet.getString("email")).thenReturn("ajit2@example.com");
        when(mockResultSet.getBoolean("verified")).thenReturn(true);

        User result = userDAO.getUserByEmail("ajit2@example.com");

        assertNotNull(result);
        assertEquals("ajit2@example.com", result.getEmail());
        assertTrue(result.isVerified());
    }

    @Test
    void testUpdateOTP() throws Exception {
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);

        userDAO.updateOTP("ajit@example.com", "123456");

        verify(mockPreparedStatement).setString(1, "123456");
        verify(mockPreparedStatement).setString(2, "ajit@example.com");
        verify(mockPreparedStatement).executeUpdate();
    }

    @Test
    void testSetVerified() throws Exception {
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);

        userDAO.setVerified("ajit@example.com", true);

        verify(mockPreparedStatement).setBoolean(1, true);
        verify(mockPreparedStatement).setString(2, "ajit@example.com");
        verify(mockPreparedStatement).executeUpdate();
    }
}
