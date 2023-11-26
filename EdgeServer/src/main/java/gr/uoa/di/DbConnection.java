package gr.uoa.di;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


public class DbConnection {

    private static final String URL = "jdbc:mysql://localhost:3306/project";
    private static final String USER = "root";
    private static final String PASSWORD = "project123";

    // Establish a database connection
    public static Connection connect() {
        try {

            return DriverManager.getConnection(URL, USER, PASSWORD);


        } catch (SQLException e) {
            throw new RuntimeException("Failed to connect to the database: " + e.getMessage());
        }
    }

    // Close the database connection
    public static void close(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                throw new RuntimeException("Failed to close the database connection: " + e.getMessage());
            }
        }
    }



}
