package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    // ✅ Fetch from environment variables
    private static final String DB_URL = System.getenv("DB_URL");      // e.g. jdbc:mysql://localhost:3306/inventorydb
    private static final String DB_USER = System.getenv("DB_USER");    // e.g. root
    private static final String DB_PASS = System.getenv("DB_PASS");    // e.g. your_password

    private static Connection connection;
  //  private static boolean testMode = false;

    private DBConnection() {
        // prevent instantiation
    }

    // ✅ Enable test mode for mock databases (optional for JUnit)
  //  public static void enableTestMode() {
  //      testMode = true;
  //  }

    // ✅ Get or create a database connection
    public static Connection getConnection() {
        try {
        //    if (testMode) {
        //        System.out.println("⚙️ DBConnection: Running in TEST MODE (mock connection).");
        //        return null; // You can replace this with a mock DB for testing
        //    }

            // Validate environment variables
            if (DB_URL == null || DB_USER == null || DB_PASS == null) {
                throw new IllegalStateException("""
                        ❌ Missing database environment variables!
                        Please set the following system environment variables:
                        DB_URL (e.g., jdbc:mysql://localhost:3306/inventorydb)
                        DB_USER (e.g., root)
                        DB_PASS (e.g., your_password)
                        """);
            }

            // Reuse existing connection if still valid
            if (connection != null && !connection.isClosed()) {
                return connection;
            }

            // ✅ Establish a new connection
            connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
           // System.out.println("✅ Database connection established successfully.");
            return connection;

        } catch (SQLException e) {
            throw new RuntimeException("❌ Failed to connect to the database: " + e.getMessage(), e);
        }
    }

    // ✅ Close the connection when done
    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("🔒 Database connection closed.");
            }
        } catch (SQLException e) {
            System.err.println("⚠️ Error closing database connection: " + e.getMessage());
        }
    }
}
