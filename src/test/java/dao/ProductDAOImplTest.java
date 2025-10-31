package dao;

import Models.Product;
import exception.ProductNotFoundException;
import exception.ValidationException;
import org.junit.jupiter.api.*;
import org.mockito.MockedStatic;
import util.DBConnection;

import java.sql.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProductDAOImplTest {

    private ProductDAOImpl productDAO;
    private Connection mockConnection;
    private PreparedStatement mockPreparedStatement;
    private ResultSet mockResultSet;

    private static MockedStatic<DBConnection> mockedDBConnection;

    @BeforeAll
    static void init() {
        // Mock the static DBConnection class
        mockedDBConnection = mockStatic(DBConnection.class);
    }

    @AfterAll
    static void close() {
        mockedDBConnection.close();
    }

    @BeforeEach
    void setUp() throws SQLException {
        productDAO = new ProductDAOImpl();
        mockConnection = mock(Connection.class);
        mockPreparedStatement = mock(PreparedStatement.class);
        mockResultSet = mock(ResultSet.class);

        // Whenever DBConnection.getConnection() is called, return the mock connection
        mockedDBConnection.when(DBConnection::getConnection).thenReturn(mockConnection);
    }

    @Test
    void testAddProductSuccess() throws SQLException, ValidationException {
        Product product = new Product(1, "Laptop", "Electronics", 10, 500.0);

        PreparedStatement checkStmt = mock(PreparedStatement.class);
        ResultSet checkRS = mock(ResultSet.class);
        PreparedStatement insertStmt = mock(PreparedStatement.class);

        when(mockConnection.prepareStatement("SELECT COUNT(*) FROM products WHERE id = ?")).thenReturn(checkStmt);
        when(checkStmt.executeQuery()).thenReturn(checkRS);
        when(checkRS.next()).thenReturn(true);
        when(checkRS.getInt(1)).thenReturn(0); // Product does not exist

        when(mockConnection.prepareStatement("INSERT INTO products (id, name, category, quantity, price) VALUES (?, ?, ?, ?, ?)")).thenReturn(insertStmt);

        assertDoesNotThrow(() -> productDAO.addProduct(product));

        verify(insertStmt).setInt(1, product.getId());
        verify(insertStmt).setString(2, product.getName());
        verify(insertStmt).setString(3, product.getCategory());
        verify(insertStmt).setInt(4, product.getQuantity());
        verify(insertStmt).setDouble(5, product.getPrice());
        verify(insertStmt).executeUpdate();
    }

    @Test
    void testGetProductByIdFound() throws SQLException, ValidationException {
        Product expected = new Product(1, "Laptop", "Electronics", 10, 500.0);

        when(mockConnection.prepareStatement("SELECT * FROM products WHERE id = ?")).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getInt("id")).thenReturn(expected.getId());
        when(mockResultSet.getString("name")).thenReturn(expected.getName());
        when(mockResultSet.getString("category")).thenReturn(expected.getCategory());
        when(mockResultSet.getInt("quantity")).thenReturn(expected.getQuantity());
        when(mockResultSet.getDouble("price")).thenReturn(expected.getPrice());

        Product actual = productDAO.getProductById(1);

        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getName(), actual.getName());
        assertEquals(expected.getCategory(), actual.getCategory());
    }

    @Test
    void testGetProductByIdNotFound() throws SQLException {
        when(mockConnection.prepareStatement("SELECT * FROM products WHERE id = ?")).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(false);

        assertThrows(ProductNotFoundException.class, () -> productDAO.getProductById(999));
    }

    @Test
    void testDeleteProductNotFound() throws SQLException {
        when(mockConnection.prepareStatement("DELETE FROM products WHERE id = ?")).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeUpdate()).thenReturn(0);

        assertThrows(ProductNotFoundException.class, () -> productDAO.deleteProduct(999));
    }

    @Test
    void testUpdateProductNotFound() throws SQLException, ValidationException {
        Product product = new Product(999, "Phone", "Electronics", 5, 300.0);

        when(mockConnection.prepareStatement("UPDATE products SET name = ?, category = ?, quantity = ?, price = ? WHERE id = ?")).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeUpdate()).thenReturn(0);

        assertThrows(ProductNotFoundException.class, () -> productDAO.updateProduct(product));
    }
}
