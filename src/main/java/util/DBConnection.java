package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DBConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/inventorydb";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "Asifa@1325";

    public static Connection getConnection() {
        Connection conn = null;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);

            // Ensure auto-commit is enabled
            conn.setAutoCommit(true);

            // ✅ Print the connected DB name
            System.out.println("Connected to DB: " + conn.getCatalog());

            // Create table if it doesn't exist
            String createTableSQL = "CREATE TABLE IF NOT EXISTS products (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY," +
                    "name VARCHAR(100) NOT NULL UNIQUE," +
                    "category VARCHAR(50) NOT NULL," +
                    "quantity INT NOT NULL," +
                    "price DECIMAL(10,2) NOT NULL" +
                    ")";
            Statement stmt = conn.createStatement();
            stmt.execute(createTableSQL);
            stmt.close();

            System.out.println("Table 'products' ready.");

        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
        return conn;
    }
}
