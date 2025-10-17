package dao;

import Models.Product;
import java.sql.SQLException;
import java.util.List;

public interface ProductDAO {
    void addProduct(Product product) throws SQLException;

    List<Product> getAllProducts() throws SQLException;

    int updateProduct(Product product) throws SQLException;

    void deleteProduct(int id) throws SQLException;

    List<Product> searchProductsByName(String name) throws SQLException;

    List<Product> filterProductsByPriceRange(double min, double max) throws SQLException;

    Product getProductById(int id) throws SQLException;

    int getTotalProductCount() throws SQLException;

    List<Product> getProductsByPage(int pageNumber, int pageSize) throws SQLException;
}
