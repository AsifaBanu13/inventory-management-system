package Services;

import Models.Product;
import util.DBConnection;

import java.sql.*;

public class inventorymanagementsystem {
    private Connection conn;

    public inventorymanagementsystem() {
        conn = DBConnection.getConnection();
    }

    // Add Product
    public void addProduct(Product product) {
        try {
            System.out.println("Attempting to add product: " + product.getName());
            String checkSQL = "SELECT COUNT(*) FROM products WHERE name = ?";
            PreparedStatement psCheck = conn.prepareStatement(checkSQL);
            psCheck.setString(1, product.getName());
            ResultSet rs = psCheck.executeQuery();
            rs.next();

            if (rs.getInt(1) == 0) {
                String sql = "INSERT INTO products (name, category, quantity, price) VALUES (?, ?, ?, ?)";
                PreparedStatement ps = conn.prepareStatement(sql);
                ps.setString(1, product.getName());
                ps.setString(2, product.getCategory());
                ps.setInt(3, product.getQuantity());
                ps.setDouble(4, product.getPrice());
                int rows = ps.executeUpdate();
                System.out.println("Rows affected: " + rows);
                ps.close();
            } else {
                System.out.println("Product already exists!");
            }

            rs.close();
            psCheck.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // View all products
    public void viewProducts() {
        try {
            String sql = "SELECT * FROM products";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            System.out.println("\nID\tName\tCategory\tQuantity\tPrice");
            System.out.println("--------------------------------------------------------");
            while (rs.next()) {
                System.out.printf("%d\t%s\t%s\t%d\t%.2f\n",
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("category"),
                        rs.getInt("quantity"),
                        rs.getDouble("price"));
            }

            rs.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Update product
    public void updateProduct(String name, int quantity, double price) {
        try {
            String sql = "UPDATE products SET quantity = ?, price = ? WHERE name = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, quantity);
            ps.setDouble(2, price);
            ps.setString(3, name);

            int rows = ps.executeUpdate();
            if (rows > 0) System.out.println("Product updated successfully!");
            else System.out.println("Product not found!");
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Delete product
    public void deleteProduct(String name) {
        try {
            String sql = "DELETE FROM products WHERE name = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, name);

            int rows = ps.executeUpdate();
            if (rows > 0) System.out.println("Product deleted successfully!");
            else System.out.println("Product not found!");
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Search product
    public void searchProduct(String name) {
        try {
            String sql = "SELECT * FROM products WHERE name LIKE ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, "%" + name + "%");
            ResultSet rs = ps.executeQuery();

            boolean found = false;
            System.out.println("\nID\tName\tCategory\tQuantity\tPrice");
            System.out.println("--------------------------------------------------------");
            while (rs.next()) {
                found = true;
                System.out.printf("%d\t%s\t%s\t%d\t%.2f\n",
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("category"),
                        rs.getInt("quantity"),
                        rs.getDouble("price"));
            }
            if (!found) System.out.println("No products found matching: " + name);

            rs.close();
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
