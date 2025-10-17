package dao;

import Models.Product;
import exception.ProductNotFoundException;
import exception.ValidationException;
import util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductDAOImpl implements ProductDAO {

    public ProductDAOImpl() {
        // No persistent connection needed; each method opens its own
    }

    @Override
    public void addProduct(Product product) throws SQLException {
        String checkSql = "SELECT COUNT(*) FROM products WHERE id = ?";
        String insertSql = "INSERT INTO products (id, name, category, quantity, price) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement check = conn.prepareStatement(checkSql)) {

            check.setInt(1, product.getId());
            try (ResultSet rs = check.executeQuery()) {
                rs.next();
                if (rs.getInt(1) > 0) {
                    throw new SQLException("⚠️ A product with id " + product.getId() + " already exists.");
                }
            }

            try (PreparedStatement ps = conn.prepareStatement(insertSql)) {
                ps.setInt(1, product.getId());
                ps.setString(2, product.getName());
                ps.setString(3, product.getCategory());
                ps.setInt(4, product.getQuantity());
                ps.setDouble(5, product.getPrice());
                ps.executeUpdate();
            }
        }
    }

    @Override
    public List<Product> getAllProducts() throws SQLException {
        String sql = "SELECT * FROM products ORDER BY id";
        List<Product> list = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                try {
                    list.add(new Product(
                            rs.getInt("id"),
                            rs.getString("name"),
                            rs.getString("category"),
                            rs.getInt("quantity"),
                            rs.getDouble("price")
                    ));
                } catch (ValidationException e) {
                    System.err.println("⚠️ Skipping invalid product: " + e.getMessage());
                }
            }
        }

        return list;
    }

    @Override
    public int updateProduct(Product product) throws SQLException {
        String sql = "UPDATE products SET name = ?, category = ?, quantity = ?, price = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, product.getName());
            ps.setString(2, product.getCategory());
            ps.setInt(3, product.getQuantity());
            ps.setDouble(4, product.getPrice());
            ps.setInt(5, product.getId());

            int rows = ps.executeUpdate();
            if (rows == 0) throw new ProductNotFoundException("❌ Product with id " + product.getId() + " not found.");
            return rows;
        }
    }

    @Override
    public void deleteProduct(int id) throws SQLException {
        String sql = "DELETE FROM products WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            int rows = ps.executeUpdate();
            if (rows == 0) throw new ProductNotFoundException("❌ Product with id " + id + " not found.");
        }
    }

    @Override
    public List<Product> searchProductsByName(String name) throws SQLException {
        String sql = "SELECT * FROM products WHERE name LIKE ? ORDER BY id";
        List<Product> list = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, "%" + name + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    try {
                        list.add(new Product(
                                rs.getInt("id"),
                                rs.getString("name"),
                                rs.getString("category"),
                                rs.getInt("quantity"),
                                rs.getDouble("price")
                        ));
                    } catch (ValidationException e) {
                        System.err.println("⚠️ Skipping invalid product: " + e.getMessage());
                    }
                }
            }
        }

        return list;
    }

    @Override
    public List<Product> filterProductsByPriceRange(double min, double max) throws SQLException {
        String sql = "SELECT * FROM products WHERE price BETWEEN ? AND ?";
        List<Product> list = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setDouble(1, min);
            ps.setDouble(2, max);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    try {
                        list.add(new Product(
                                rs.getInt("id"),
                                rs.getString("name"),
                                rs.getString("category"),
                                rs.getInt("quantity"),
                                rs.getDouble("price")
                        ));
                    } catch (ValidationException e) {
                        System.err.println("⚠️ Skipping invalid product: " + e.getMessage());
                    }
                }
            }
        }

        return list;
    }

    @Override
    public Product getProductById(int id) throws SQLException {
        String sql = "SELECT * FROM products WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    try {
                        return new Product(
                                rs.getInt("id"),
                                rs.getString("name"),
                                rs.getString("category"),
                                rs.getInt("quantity"),
                                rs.getDouble("price")
                        );
                    } catch (ValidationException e) {
                        throw new SQLException("⚠️ Invalid product data in DB: " + e.getMessage());
                    }
                } else {
                    throw new ProductNotFoundException("❌ Product with id " + id + " not found.");
                }
            }
        }
    }

    @Override
    public int getTotalProductCount() throws SQLException {
        String sql = "SELECT COUNT(*) FROM products";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) return rs.getInt(1);
        }
        return 0;
    }

    @Override
    public List<Product> getProductsByPage(int pageNumber, int pageSize) throws SQLException {
        List<Product> list = new ArrayList<>();
        int offset = (pageNumber - 1) * pageSize;
        String sql = "SELECT * FROM products ORDER BY id LIMIT ? OFFSET ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, pageSize);
            ps.setInt(2, offset);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    try {
                        list.add(new Product(
                                rs.getInt("id"),
                                rs.getString("name"),
                                rs.getString("category"),
                                rs.getInt("quantity"),
                                rs.getDouble("price")
                        ));
                    } catch (ValidationException e) {
                        System.err.println("⚠️ Skipping invalid product: " + e.getMessage());
                    }
                }
            }
        }

        return list;
    }
}
