package org.example.app;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DBHelper {

    private static final String CONNECTION_STRING = "jdbc:mysql://localhost:3306/upload_system";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "1234";

    // Customer sınıfı (iç sınıf)
    public static class Customer {
        private int id;
        private String name;
        private String surname;
        private String tckn;

        public Customer(int id, String name, String surname, String tckn) {
            this.id = id;
            this.name = name;
            this.surname = surname;
            this.tckn = tckn;
        }

        public int getId() { return id; }
        public String getName() { return name; }
        public String getSurname() { return surname; }
        public String getTckn() { return tckn; }
    }

    // Product sınıfı (iç sınıf)
    public static class Product {
        private int id;
        private String name;
        private double price;

        public Product(int id, String name, double price) {
            this.id = id;
            this.name = name;
            this.price = price;
        }

        public int getId() { return id; }
        public String getName() { return name; }
        public double getPrice() { return price; }
    }

    // Tüm müşterileri veritabanından çek
    public List<Customer> getAllCustomers() {
        List<Customer> customers = new ArrayList<>();
        String sql = "SELECT id, name, surname, tckn FROM customers";

        try (Connection conn = DriverManager.getConnection(CONNECTION_STRING, USERNAME, PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                customers.add(new Customer(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("surname"),
                        rs.getString("tckn")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return customers;
    }

    // Yeni müşteri ekle
    public boolean musteriEkle(String name, String surname, String tckn) {
        String sql = "INSERT INTO customers (name, surname, tckn) VALUES (?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(CONNECTION_STRING, USERNAME, PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, name);
            pstmt.setString(2, surname);
            pstmt.setString(3, tckn);

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Müşteri güncelle
    public boolean updateMusteri(int id, String name, String surname, String tckn) {
        String sql = "UPDATE customers SET name = ?, surname = ?, tckn = ? WHERE id = ?";
        try (Connection conn = DriverManager.getConnection(CONNECTION_STRING, USERNAME, PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, name);
            pstmt.setString(2, surname);
            pstmt.setString(3, tckn);
            pstmt.setInt(4, id);

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Tüm ürünleri veritabanından çek
    public List<Product> getAllProducts() {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT id, name, price FROM items";

        try (Connection conn = DriverManager.getConnection(CONNECTION_STRING, USERNAME, PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                products.add(new Product(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getDouble("price")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return products;
    }

    // Fatura ekle (invoice)
    public int addInvoice(String series, String invoiceNum, int customerId, double discount, double total) {
        String sql = "INSERT INTO invoices (series, invoice, customer_id, discount, total) VALUES (?, ?, ?, ?, ?)";
        int generatedId = -1;

        try (Connection conn = DriverManager.getConnection(CONNECTION_STRING, USERNAME, PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, series);
            pstmt.setString(2, invoiceNum);
            pstmt.setInt(3, customerId);
            pstmt.setDouble(4, discount);
            pstmt.setDouble(5, total);

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Fatura eklenemedi, hiç satır etkilenmedi.");
            }

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    generatedId = generatedKeys.getInt(1);
                } else {
                    throw new SQLException("Fatura ID'si alınamadı.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return generatedId;
    }

    // Fatura ürünlerini ekle (invoice_items)
    public boolean addInvoiceItem(int invoiceId, int itemId, int quantity, double total) {
        String sql = "INSERT INTO invoiceitems (invoice_id, item_id, quantity, total) VALUES (?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(CONNECTION_STRING, USERNAME, PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, invoiceId);
            pstmt.setInt(2, itemId);
            pstmt.setInt(3, quantity);
            pstmt.setDouble(4, total);

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
