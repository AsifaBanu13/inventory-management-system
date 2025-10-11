package util;

import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    // change to your password

    private static final String DB_URL = System.getenv("DB_URL");
    private static final String DB_USER = System.getenv("DB_USER");
    private static final String DB_PASS = System.getenv("DB_PASS");


    private static Connection connection;
    private DBConnection() {}

    public static Connection getConnection()  {
        if(DB_URL==null || DB_USER==null || DB_PASS==null){
            throw new RuntimeException("Database environment variables not set!");
        }
        try {
            return DriverManager.getConnection(DB_URL,DB_USER,DB_PASS);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

     //   if (connection == null) {
       //     try {
                // Ensure the MySQL JDBC driver is available (for older drivers)
                // Class.forName("com.mysql.cj.jdbc.Driver");
         //       connection = DriverManager.getConnection(URL, USER, PASSWORD);
           // } catch (SQLException e) {
            //    e.printStackTrace();
             //   throw new RuntimeException("Unable to establish database connection", e);
           // }
       // }
        //return connection;
    }
}
