package org.example.app;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DBHelper {

    private static final String CONNECTION_STRING = "jdbc:mysql://localhost:3306/upload_system";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "1234";

    // Statik connection açma metodu (Main'de kullanmak için)
    public static Connection open() throws SQLException {
        return DriverManager.getConnection(CONNECTION_STRING, USERNAME, PASSWORD);
    }

    // Tüm müşterileri veritabanından çek
    public List<Customer> getAllCustomers() {
        List<Customer> customers = new ArrayList<>();

        String sql = "SELECT id, name, surname, tckn FROM customers";

        try (Connection conn = open();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                String surname = rs.getString("surname");
                String tckn = rs.getString("tckn");

                customers.add(new Customer(id, name, surname, tckn));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return customers;
    }

    // Yeni müşteri ekle
    public boolean musteriEkle(String name, String surname, String tckn) {
        String sql = "INSERT INTO customers (name, surname, tckn) VALUES (?, ?, ?)";

        try (Connection conn = open();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, name);
            pstmt.setString(2, surname);
            pstmt.setString(3, tckn);

            int affected = pstmt.executeUpdate();
            return affected > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Müşteri güncelle
    public boolean updateMusteri(int id, String name, String surname, String tckn) {
        String sql = "UPDATE customers SET name = ?, surname = ?, tckn = ? WHERE id = ?";

        try (Connection conn = open();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, name);
            pstmt.setString(2, surname);
            pstmt.setString(3, tckn);
            pstmt.setInt(4, id);

            int affected = pstmt.executeUpdate();
            return affected > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

}
