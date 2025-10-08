package Services;

import Models.Product;
import dao.ProductDAO;
import dao.ProductDAOImpl;
import exception.ValidationException;
import exception.ProductNotFoundException;

import java.sql.SQLException;
import java.util.List;

public class inventorymanagementsystem {
    private final ProductDAO dao;

    public inventorymanagementsystem() {
        this.dao = new ProductDAOImpl();
    }

    // Add a product
    public void addProduct(Product product) {
        try {
            dao.addProduct(product);
            System.out.println("✅ Product added successfully: " + product.getName());
        } catch (SQLException e) {
            System.err.println("❌ Error adding product: " + e.getMessage());
        } catch (RuntimeException e) {
            System.err.println("⚠️ Operation failed: " + e.getMessage());
        }
    }

    // View all products (Tabular format)
    public void viewProducts() {
        try {
            List<Product> products = dao.getAllProducts();
            if (products.isEmpty()) {
                System.out.println("📭 No products available.");
                return;
            }

            System.out.println("\n📦 Product List:");
            System.out.printf("%-5s %-20s %-15s %-10s %-10s%n",
                    "ID", "Name", "Category", "Quantity", "Price");
            System.out.println("---------------------------------------------------------------");

            for (Product p : products) {
                System.out.printf("%-5d %-20s %-15s %-10d %-10.2f%n",
                        p.getId(), p.getName(), p.getCategory(), p.getQuantity(), p.getPrice());
            }
        } catch (SQLException e) {
            System.err.println("❌ Error fetching products: " + e.getMessage());
        } catch (RuntimeException e) {
            System.err.println("⚠️ Unexpected error while fetching products: " + e.getMessage());
        }
    }

    // Update product
    public void updateProduct(int id, String newName, String newCategory, int quantity, double price) {
        try {
            Product p = new Product(id, newName, newCategory, quantity, price);
            dao.updateProduct(p);
            System.out.println("✅ Product updated successfully!");
        } catch (ValidationException e) {
            System.err.println("⚠️ Invalid product data: " + e.getMessage());
        } catch (ProductNotFoundException e) {
            System.err.println("❌ Update failed: " + e.getMessage());
        } catch (SQLException e) {
            System.err.println("❌ Update failed (DB): " + e.getMessage());
        } catch (RuntimeException e) {
            System.err.println("⚠️ Unexpected error during update: " + e.getMessage());
        }
    }

    // Delete product
    public void deleteProduct(int id) {
        try {
            dao.deleteProduct(id);
            System.out.println("🗑️ Product deleted successfully!");
        } catch (ProductNotFoundException e) {
            System.err.println("❌ Delete failed: " + e.getMessage());
        } catch (SQLException e) {
            System.err.println("❌ Delete failed (DB): " + e.getMessage());
        } catch (RuntimeException e) {
            System.err.println("⚠️ Unexpected error during delete: " + e.getMessage());
        }
    }

    // Search product by name (Tabular format)
    public void searchProduct(String name) {
        try {
            List<Product> list = dao.searchProductsByName(name);
            if (list.isEmpty()) {
                System.out.println("🔍 No products found matching: " + name);
                return;
            }

            System.out.println("\n🔍 Search Results:");
            System.out.printf("%-5s %-20s %-15s %-10s %-10s%n",
                    "ID", "Name", "Category", "Quantity", "Price");
            System.out.println("---------------------------------------------------------------");

            for (Product p : list) {
                System.out.printf("%-5d %-20s %-15s %-10d %-10.2f%n",
                        p.getId(), p.getName(), p.getCategory(), p.getQuantity(), p.getPrice());
            }
        } catch (SQLException e) {
            System.err.println("❌ Search failed: " + e.getMessage());
        } catch (RuntimeException e) {
            System.err.println("⚠️ Unexpected error during search: " + e.getMessage());
        }
    }

    // Return list for report generation
    public List<Product> getAllProductsForReport() {
        try {
            return dao.getAllProducts();
        } catch (SQLException e) {
            System.err.println("❌ Could not load products for report: " + e.getMessage());
            return java.util.Collections.emptyList();
        } catch (RuntimeException e) {
            System.err.println("⚠️ Unexpected error while loading report data: " + e.getMessage());
            return java.util.Collections.emptyList();
        }
    }

    // Get by id
    public void viewProductById(int id) {
        try {
            Product p = dao.getProductById(id);
            System.out.println("📌 Product Details: " + p);
        } catch (ProductNotFoundException e) {
            System.err.println("❌ " + e.getMessage());
        } catch (SQLException e) {
            System.err.println("❌ Error fetching product: " + e.getMessage());
        } catch (RuntimeException e) {
            System.err.println("⚠️ Unexpected error: " + e.getMessage());
        }
    }
}
