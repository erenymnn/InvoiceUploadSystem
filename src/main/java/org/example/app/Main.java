package org.example.app;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Main {

    private static final String CONNECTION_STRING = "jdbc:mysql://localhost:3306/upload_system";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "1234";

    public static void main(String[] args) {
        try (Connection connect = DriverManager.getConnection(CONNECTION_STRING, USERNAME, PASSWORD)) {
            System.out.println("Veritabanina baglanildi.");
            // İstersen burada tablo oluşturma vb işlemler yapabilirsin.
        } catch (SQLException e) {
            System.out.println("Veritabanina baglanirken hata: " + e.getMessage());
        }

        new MainMenu();
    }
}
