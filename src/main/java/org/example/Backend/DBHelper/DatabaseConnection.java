package org.example.Backend.DBHelper;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/upload_system?serverTimezone=UTC";
    private static final String USER = "root";      // MySQL username
    private static final String PASSWORD = "1234";  // MySQL password

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
