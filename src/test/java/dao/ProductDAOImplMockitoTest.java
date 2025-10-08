package dao;
import Models.Product;
import dao.ProductDAO;
import exception.ValidationException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class ProductDAOImplMockitoTest {
    private static ProductDAO productDAO;

    @BeforeAll
    static void setup() {
        // ✅ Create a mock object of ProductDAO
        productDAO = Mockito.mock(ProductDAO.class);
    }

    @Test
    void testAddProductSuccess() throws SQLException, ValidationException {
        // ✅ Create a sample product
        Product p = new Product(101, "Laptop", "Electronics", 10, 50000);

        // ✅ Stub: do nothing when addProduct() is called
        doNothing().when(productDAO).addProduct(p);

        // ✅ Call the method
        productDAO.addProduct(p);

        // ✅ Verify that addProduct() was called exactly once
        verify(productDAO, times(1)).addProduct(p);
    }

    @Test
    void testUpdateProduct() throws SQLException, ValidationException {
        // ✅ Create a product
        Product p = new Product(101, "Laptop", "Electronics", 10, 50000);

        // ✅ Stub: return 1 when updateProduct() is called
        when(productDAO.updateProduct(p)).thenReturn(1);

        // ✅ Call the method
        int rows = productDAO.updateProduct(p);

        // ✅ Verify return value and method call
        assertEquals(1, rows);
        verify(productDAO, times(1)).updateProduct(p);
    }
}

