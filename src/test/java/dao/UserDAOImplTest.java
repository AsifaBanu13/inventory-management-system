package dao;

import Models.User;
import util.DBConnection;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;
import java.sql.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UserDAOImplTest {

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
        DBConnection.enableTestMode();
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

    // Test addUser() - success
    @Test
    void testAddUserSuccess() throws Exception {
        User user = new User(1, "ajit", "1234", "ADMIN");

        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);

        userDAO.addUser(user);

        verify(mockPreparedStatement).setString(1, "ajit");
        verify(mockPreparedStatement).setString(2, "1234");
        verify(mockPreparedStatement).setString(3, "ADMIN");
        verify(mockPreparedStatement).executeUpdate();
    }

    @Test
    void testAddUserDuplicateUsername() throws Exception {
        User user = new User(2, "existingUser", "abcd", "USER");

        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        doThrow(new SQLIntegrityConstraintViolationException("Duplicate username"))
                .when(mockPreparedStatement).executeUpdate();

        assertDoesNotThrow(() -> userDAO.addUser(user));
        verify(mockPreparedStatement).executeUpdate();
    }

    //Test getUserByUsername() - success
    @Test
    void testGetUserByUsernameSuccess() throws Exception {
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);

        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getInt("id")).thenReturn(1);
        when(mockResultSet.getString("username")).thenReturn("ajit");
        when(mockResultSet.getString("password")).thenReturn("1234");
        when(mockResultSet.getString("role")).thenReturn("ADMIN");

        User result = userDAO.getUserByUsername("ajit");

        assertNotNull(result);
        assertEquals("ajit", result.getUsername());
        assertEquals("ADMIN", result.getRole());
    }

    // Test getUserByUsername() - user not found
    @Test
    void testGetUserByUsernameNotFound() throws Exception {
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);

        when(mockResultSet.next()).thenReturn(false);

        User result = userDAO.getUserByUsername("nonexistent");
        assertNull(result);
    }
}