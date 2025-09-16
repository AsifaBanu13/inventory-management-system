package Services;

import util.DBConnection;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class QueryTest {
    public static void main(String[] args) {
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement()) {

            // 1. Show all products
            System.out.println("1. All products:");
            ResultSet rs1 = stmt.executeQuery("SELECT * FROM products");
            while (rs1.next()) {
                System.out.println(rs1.getInt("id") + " | " +
                        rs1.getString("name") + " | " +
                        rs1.getString("category") + " | " +
                        rs1.getInt("quantity") + " | " +
                        rs1.getDouble("price"));
            }

            // 2. Show only product names and categories
            System.out.println("\n2. Product names & categories:");
            ResultSet rs2 = stmt.executeQuery("SELECT name, category FROM products");
            while (rs2.next()) {
                System.out.println(rs2.getString("name") + " | " + rs2.getString("category"));
            }

            // 3. Products where quantity > 10
            System.out.println("\n3. Products with quantity > 10:");
            ResultSet rs3 = stmt.executeQuery("SELECT * FROM products WHERE quantity > 10");
            while (rs3.next()) {
                System.out.println(rs3.getString("name") + " | Qty: " + rs3.getInt("quantity"));
            }

            // 4. Products where price < 5000
            System.out.println("\n4. Products with price < 5000:");
            ResultSet rs4 = stmt.executeQuery("SELECT * FROM products WHERE price < 5000");
            while (rs4.next()) {
                System.out.println(rs4.getString("name") + " | Price: " + rs4.getDouble("price"));
            }

            // 5. All Electronics products
            System.out.println("\n5. Electronics products:");
            ResultSet rs5 = stmt.executeQuery("SELECT * FROM products WHERE category = 'Electronics'");
            while (rs5.next()) {
                System.out.println(rs5.getString("name") + " | " + rs5.getString("category"));
            }

            // 6. All products sorted by price (highest first)
            System.out.println("\n6. Products sorted by price (highest first):");
            ResultSet rs6 = stmt.executeQuery("SELECT * FROM products ORDER BY price DESC");
            while (rs6.next()) {
                System.out.println(rs6.getString("name") + " | Price: " + rs6.getDouble("price"));
            }

            // 7. Top 3 most expensive products
            System.out.println("\n7. Top 3 most expensive products:");
            ResultSet rs7 = stmt.executeQuery("SELECT * FROM products ORDER BY price DESC LIMIT 3");
            while (rs7.next()) {
                System.out.println(rs7.getString("name") + " | Price: " + rs7.getDouble("price"));
            }

            // 8. Total number of products (sum of quantity)
            System.out.println("\n8. Total number of products:");
            ResultSet rs8 = stmt.executeQuery("SELECT SUM(quantity) AS total_quantity FROM products");
            if (rs8.next()) {
                System.out.println("Total Quantity: " + rs8.getInt("total_quantity"));
            }

            // 9. Average price of products
            System.out.println("\n9. Average price of products:");
            ResultSet rs9 = stmt.executeQuery("SELECT AVG(price) AS average_price FROM products");
            if (rs9.next()) {
                System.out.println("Average Price: " + rs9.getDouble("average_price"));
            }

            // 10. Highest priced product
            System.out.println("\n10. Highest priced product:");
            ResultSet rs10 = stmt.executeQuery("SELECT * FROM products ORDER BY price DESC LIMIT 1");
            if (rs10.next()) {
                System.out.println("Highest: " + rs10.getString("name") +
                        " | Price: " + rs10.getDouble("price"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
