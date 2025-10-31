package dao;

import Models.Product;
import exception.ProductNotFoundException;
import exception.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import util.DBConnection;

import java.sql.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProductDAOImplMockitoTest {

    private ProductDAOImpl productDAO;

    @BeforeEach
    void setUp() {
        productDAO = new ProductDAOImpl();
    }

    @Test
    void testAddProductSuccess() throws SQLException, ValidationException {
        Product product = new Product(1, "Laptop", "Electronics", 10, 500.0);

        try (MockedStatic<DBConnection> mockedDBConnection = mockStatic(DBConnection.class)) {
            Connection mockConnection = mock(Connection.class);
            PreparedStatement checkStmt = mock(PreparedStatement.class);
            PreparedStatement insertStmt = mock(PreparedStatement.class);
            ResultSet checkRS = mock(ResultSet.class);

            // Mock DBConnection
            mockedDBConnection.when(DBConnection::getConnection).thenReturn(mockConnection);

            // Mock SELECT COUNT(*)
            when(mockConnection.prepareStatement("SELECT COUNT(*) FROM products WHERE id = ?"))
                    .thenReturn(checkStmt);
            when(checkStmt.executeQuery()).thenReturn(checkRS);
            when(checkRS.next()).thenReturn(true);
            when(checkRS.getInt(1)).thenReturn(0);

            // Mock INSERT
            when(mockConnection.prepareStatement(
                    "INSERT INTO products (id, name, category, quantity, price) VALUES (?, ?, ?, ?, ?)")
            ).thenReturn(insertStmt);

            // Run test
            assertDoesNotThrow(() -> productDAO.addProduct(product));

            // Verify insert called
            verify(insertStmt).executeUpdate();
        }
    }

    @Test
    void testAddProductDuplicate() throws SQLException, ValidationException {
        Product product = new Product(1, "Laptop", "Electronics", 10, 500.0);

        try (MockedStatic<DBConnection> mockedDBConnection = mockStatic(DBConnection.class)) {
            Connection mockConnection = mock(Connection.class);
            PreparedStatement checkStmt = mock(PreparedStatement.class);
            ResultSet checkRS = mock(ResultSet.class);

            mockedDBConnection.when(DBConnection::getConnection).thenReturn(mockConnection);

            when(mockConnection.prepareStatement("SELECT COUNT(*) FROM products WHERE id = ?"))
                    .thenReturn(checkStmt);
            when(checkStmt.executeQuery()).thenReturn(checkRS);
            when(checkRS.next()).thenReturn(true);
            when(checkRS.getInt(1)).thenReturn(1); // Duplicate exists

            SQLException exception = assertThrows(SQLException.class,
                    () -> productDAO.addProduct(product));

            assertTrue(exception.getMessage().contains("already exists"));
        }
    }

    @Test
    void testGetProductByIdSuccess() throws SQLException, ValidationException {
        Product product = new Product(1, "Laptop", "Electronics", 10, 500.0);

        try (MockedStatic<DBConnection> mockedDBConnection = mockStatic(DBConnection.class)) {
            Connection mockConnection = mock(Connection.class);
            PreparedStatement ps = mock(PreparedStatement.class);
            ResultSet rs = mock(ResultSet.class);

            mockedDBConnection.when(DBConnection::getConnection).thenReturn(mockConnection);

            when(mockConnection.prepareStatement("SELECT * FROM products WHERE id = ?")).thenReturn(ps);
            when(ps.executeQuery()).thenReturn(rs);
            when(rs.next()).thenReturn(true);
            when(rs.getInt("id")).thenReturn(product.getId());
            when(rs.getString("name")).thenReturn(product.getName());
            when(rs.getString("category")).thenReturn(product.getCategory());
            when(rs.getInt("quantity")).thenReturn(product.getQuantity());
            when(rs.getDouble("price")).thenReturn(product.getPrice());

            Product result = productDAO.getProductById(1);
            assertEquals(product.getName(), result.getName());
        }
    }

    @Test
    void testGetProductByIdNotFound() throws SQLException {
        try (MockedStatic<DBConnection> mockedDBConnection = mockStatic(DBConnection.class)) {
            Connection mockConnection = mock(Connection.class);
            PreparedStatement ps = mock(PreparedStatement.class);
            ResultSet rs = mock(ResultSet.class);

            mockedDBConnection.when(DBConnection::getConnection).thenReturn(mockConnection);

            when(mockConnection.prepareStatement("SELECT * FROM products WHERE id = ?")).thenReturn(ps);
            when(ps.executeQuery()).thenReturn(rs);
            when(rs.next()).thenReturn(false); // Not found

            assertThrows(ProductNotFoundException.class, () -> productDAO.getProductById(999));
        }
    }

    @Test
    void testDeleteProductNotFound() throws SQLException {
        try (MockedStatic<DBConnection> mockedDBConnection = mockStatic(DBConnection.class)) {
            Connection mockConnection = mock(Connection.class);
            PreparedStatement ps = mock(PreparedStatement.class);

            mockedDBConnection.when(DBConnection::getConnection).thenReturn(mockConnection);

            when(mockConnection.prepareStatement("DELETE FROM products WHERE id = ?")).thenReturn(ps);
            when(ps.executeUpdate()).thenReturn(0); // No rows deleted

            assertThrows(ProductNotFoundException.class, () -> productDAO.deleteProduct(999));
        }
    }
}
