package dao;
import dao.ProductDAOImpl;
import dao.*;
import Models.Product;
import exception.ProductNotFoundException;
import exception.ValidationException;
import util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductDAOImpl implements ProductDAO {
    private final Connection conn;

    public ProductDAOImpl() throws SQLException {
        this.conn = DBConnection.getConnection();
    }

    @Override
    public void addProduct(Product product) throws SQLException {
        String checkSql = "SELECT COUNT(*) FROM products WHERE id = ?";
        try (PreparedStatement check = conn.prepareStatement(checkSql)) {
            check.setInt(1, product.getId());
            try (ResultSet rs = check.executeQuery()) {
                rs.next();
                if (rs.getInt(1) > 0) {
                    throw new SQLException("⚠️ A product with id " + product.getId() + " already exists.");
                }
            }
        }

        String sql = "INSERT INTO products (id, name, category, quantity, price) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, product.getId());
            ps.setString(2, product.getName());
            ps.setString(3, product.getCategory());
            ps.setInt(4, product.getQuantity());
            ps.setDouble(5, product.getPrice());
            ps.executeUpdate();
        }
    }

    @Override
    public List<Product> getAllProducts() throws SQLException {
        String sql = "SELECT id, name, category, quantity, price FROM products ORDER BY id";
        List<Product> list = new ArrayList<>();
        try (Statement stmt = conn.createStatement();
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
                    System.err.println("⚠️ Skipping invalid product from DB: " + e.getMessage());
                }
            }
        }
        return list;
    }

    @Override
    public int updateProduct(Product product) throws SQLException {
        String sql = "UPDATE products SET name = ?, category = ?, quantity = ?, price = ? WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, product.getName());
            ps.setString(2, product.getCategory());
            ps.setInt(3, product.getQuantity());
            ps.setDouble(4, product.getPrice());
            ps.setInt(5, product.getId());
            int rows = ps.executeUpdate();
            if (rows == 0) throw new ProductNotFoundException("❌ Product with id " + product.getId() + " not found.");
        }
        return 0;
    }

    @Override
    public void deleteProduct(int id) throws SQLException {
        String sql = "DELETE FROM products WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            int rows = ps.executeUpdate();
            if (rows == 0) throw new ProductNotFoundException("❌ Product with id " + id + " not found.");
        }
    }

    @Override
    public List<Product> searchProductsByName(String name) throws SQLException {
        String sql = "SELECT id, name, category, quantity, price FROM products WHERE name LIKE ? ORDER BY id";
        List<Product> list = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
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
                        System.err.println("⚠️ Skipping invalid product from DB: " + e.getMessage());
                    }
                }
            }
        }
        return list;
    }

    @Override
    public Product getProductById(int id) throws SQLException {
        String sql = "SELECT id, name, category, quantity, price FROM products WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
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
    public int getTotalProductCount(Connection conn) throws SQLException {
        String sql = "SELECT COUNT(*) FROM products";
        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }

    @Override
    public List<Product> getProductsByPage(Connection conn, int pageNumber, int pageSize) throws SQLException {
        List<Product> products = new ArrayList<>();
        int offset = (pageNumber - 1) * pageSize;
        String sql = "SELECT * FROM products LIMIT ? OFFSET ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, pageSize);
            stmt.setInt(2, offset);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Product p = new Product(
                            rs.getInt("id"),
                            rs.getString("name"),
                            rs.getString("category"),
                            rs.getInt("quantity"),
                            rs.getDouble("price")
                    );
                    products.add(p);
                }
            } catch (ValidationException e) {
                System.err.println("⚠️ Skipping invalid product from DB: " + e.getMessage());
            }
        }
        return products;
    }

    @Override
    public List<Product> filterProductsByPriceRange(double min, double max) throws SQLException {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT * FROM products WHERE price BETWEEN ? AND ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDouble(1, min);
            stmt.setDouble(2, max);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    try {
                        Product p = new Product(
                                rs.getInt("id"),
                                rs.getString("name"),
                                rs.getString("category"),
                                rs.getInt("quantity"),
                                rs.getDouble("price")
                        );
                        products.add(p);
                    } catch (ValidationException e) {
                        System.err.println("⚠️ Skipping invalid product from DB: " + e.getMessage());
                    }
                }
            }
            return products;
        }


    }
}
