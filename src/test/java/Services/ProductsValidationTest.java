package Services;

import Models.Product;
import exception.ValidationException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ProductsValidationTest {

    // Test for invalid price (negative)
    @Test
    public void testInvalidPriceThrowsException() {
        ValidationException ex = assertThrows(ValidationException.class, () -> {
            new Product(1, "Laptop", "Electronics", 10, -100);
        });
        assertEquals("❌ Price cannot be negative.", ex.getMessage());
    }

    // Test for invalid quantity (negative)
    @Test
    public void testInvalidQuantityThrowsException() {
        ValidationException ex = assertThrows(ValidationException.class, () -> {
            new Product(2, "Laptop", "Electronics", -5, 50000);
        });
        assertEquals("❌ Quantity cannot be negative.", ex.getMessage());
    }

    // Test for invalid name (empty or numeric-only)
    @Test
    public void testInvalidNameThrowsExceptionEmpty() {
        ValidationException ex = assertThrows(ValidationException.class, () -> {
            new Product(3, "", "Electronics", 5, 1000);
        });
        assertEquals("❌ Product name cannot be empty.", ex.getMessage());
    }


    // Test for valid product creation
    @Test
    public void testValidProductCreation() {
        assertDoesNotThrow(() -> {
            Product product = new Product(5, "Laptop", "Electronics", 10, 50000);
            assertEquals(5, product.getId());
            assertEquals("Laptop", product.getName());
            assertEquals("Electronics", product.getCategory());
            assertEquals(10, product.getQuantity());
            assertEquals(50000, product.getPrice());
        });
    }
}



/*package Services;

import Models.Product;
import exception.ValidationException;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ProductsValidationTest {

    @Test
    public void testValidateProductCreation() throws ValidationException {
        Product p=new Product(1,"Laptop", "Electronics",10,50000);
        assertEquals(1,p.getId());
        assertEquals("Laptop", p.getName());
    }

    @Test
    public void testInvalidPriceThrowsException() {
        ValidationException ex = assertThrows(ValidationException.class, () -> {
            Product product = new Product(1, "Laptop", "electronics", 10, -100);
        });
        assertEquals("❌ Price cannot be negative.", ex.getMessage());
    }
}
 */
