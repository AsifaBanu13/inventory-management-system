package Services;

import Models.Product;
import dao.ProductDAOImpl;
import exception.ProductNotFoundException;
import exception.ValidationException;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

public class inventorymanagementsystem {

    private final ProductDAOImpl productDAO;

    public inventorymanagementsystem() {
        this.productDAO = new ProductDAOImpl();
    }

    public void addProduct(Product product) {
        try {
            productDAO.addProduct(product);
            System.out.println("✅ Product added successfully: " + product.getName());
        } catch (SQLException e) {
            System.err.println("❌ Error adding product: " + e.getMessage());
        }
    }

    public void viewProducts() {
        try {
            List<Product> products = productDAO.getAllProducts();
            if (products.isEmpty()) {
                System.out.println("⚠️ No products available!");
                return;
            }

            System.out.printf("%-5s %-20s %-15s %-10s %-10s%n", "ID", "Name", "Category", "Quantity", "Price");
            System.out.println("-----------------------------------------------------------");
            for (Product p : products) {
                System.out.printf("%-5d %-20s %-15s %-10d %-10.2f%n",
                        p.getId(), p.getName(), p.getCategory(), p.getQuantity(), p.getPrice());
            }
        } catch (SQLException e) {
            System.err.println("❌ Could not fetch products: " + e.getMessage());
        }
    }

    public void updateProduct(int id, String name, String category, int quantity, double price) {
        try {
            Product p = new Product(id, name, category, quantity, price);
            productDAO.updateProduct(p);
            System.out.println("✅ Product updated successfully!");
        } catch (ProductNotFoundException | SQLException | ValidationException e) {
            System.err.println("❌ Update failed: " + e.getMessage());
        }
    }

    public void deleteProduct(int id) {
        try {
            productDAO.deleteProduct(id);
            System.out.println("🗑️ Product deleted successfully!");
        } catch (ProductNotFoundException | SQLException e) {
            System.err.println("❌ Delete failed: " + e.getMessage());
        }
    }

    public void searchProduct(String name) {
        try {
            List<Product> list = productDAO.searchProductsByName(name);
            if (list.isEmpty()) {
                System.out.println("⚠️ No products found matching: " + name);
                return;
            }

            System.out.printf("%-5s %-20s %-15s %-10s %-10s%n", "ID", "Name", "Category", "Quantity", "Price");
            System.out.println("-----------------------------------------------------------");
            for (Product p : list) {
                System.out.printf("%-5d %-20s %-15s %-10d %-10.2f%n",
                        p.getId(), p.getName(), p.getCategory(), p.getQuantity(), p.getPrice());
            }
        } catch (SQLException e) {
            System.err.println("❌ Search failed: " + e.getMessage());
        }
    }

    public void filterProductsByPriceRange(double min, double max) {
        try {
            List<Product> list = productDAO.filterProductsByPriceRange(min, max);
            if (list.isEmpty()) {
                System.out.println("⚠️ No products found in price range " + min + " - " + max);
                return;
            }

            System.out.printf("%-5s %-20s %-15s %-10s %-10s%n", "ID", "Name", "Category", "Quantity", "Price");
            System.out.println("-----------------------------------------------------------");
            for (Product p : list) {
                System.out.printf("%-5d %-20s %-15s %-10d %-10.2f%n",
                        p.getId(), p.getName(), p.getCategory(), p.getQuantity(), p.getPrice());
            }
        } catch (SQLException e) {
            System.err.println("❌ Filter failed: " + e.getMessage());
        }
    }

    public void viewProductById(int id) {
        try {
            Product p = productDAO.getProductById(id);
            System.out.println("📌 Product Details: " + p);
        } catch (ProductNotFoundException | SQLException e) {
            System.err.println("❌ Could not find product: " + e.getMessage());
        }
    }

    public List<Product> getAllProductsForReport() {
        try {
            return productDAO.getAllProducts();
        } catch (SQLException e) {
            System.err.println("❌ Could not fetch products for report: " + e.getMessage());
            return Collections.emptyList();
        }
    }
}
