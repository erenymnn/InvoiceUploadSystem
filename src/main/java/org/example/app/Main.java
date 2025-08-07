package org.example.app;

import java.sql.Connection;
import java.sql.SQLException;

public class Main {

    public static void main(String[] args) {

        try (Connection connect = DBHelper.open()) {
            if (connect != null) {
                System.out.println("Database connection successful.");
                // createTables(connect); // Tablo oluşturma varsa açabilirsin
            } else {
                System.out.println("Failed to connect to database.");
            }
        } catch (SQLException e) {
            System.out.println("Error connecting to database: " + e.getMessage());
        }

        new MainMenu();
    }
}
