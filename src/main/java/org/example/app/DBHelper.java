package org.example.app;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DBHelper {

    private static final String CONNECTION_STRING = "jdbc:mysql://localhost:3306/upload_system";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "1234";

    // Customer iç sınıfı
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

    // Product iç sınıfı
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

    // InvoiceSummary iç sınıfı
    public static class InvoiceSummary {
        private int id;
        private String series;
        private String invoiceNum;
        private int customerId;
        private double discount;
        private double total;

        public InvoiceSummary(int id, String series, String invoiceNum, int customerId, double discount, double total) {
            this.id = id;
            this.series = series;
            this.invoiceNum = invoiceNum;
            this.customerId = customerId;
            this.discount = discount;
            this.total = total;
        }

        public int getId() { return id; }
        public String getSeries() { return series; }
        public String getInvoiceNum() { return invoiceNum; }
        public int getCustomerId() { return customerId; }
        public double getDiscount() { return discount; }
        public double getTotal() { return total; }
    }

    // InvoiceItem iç sınıfı
    public static class InvoiceItem {
        private int itemId;
        private String itemName;
        private double price;
        private int quantity;
        private double total;

        public InvoiceItem(int itemId, String itemName, double price, int quantity, double total) {
            this.itemId = itemId;
            this.itemName = itemName;
            this.price = price;
            this.quantity = quantity;
            this.total = total;
        }

        public int getItemId() { return itemId; }
        public String getItemName() { return itemName; }
        public double getPrice() { return price; }
        public int getQuantity() { return quantity; }
        public double getTotal() { return total; }
    }

    // Tüm müşterileri getir
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

    // Tüm ürünleri getir
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

    // Fatura ekle
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

    // Fatura ürünlerini ekle
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

    // Seri ve numaraya göre faturanın ID'sini getir
    public int findInvoiceIdBySeriesAndNumber(String series, String number) {
        String sql = "SELECT id FROM invoices WHERE series = ? AND invoice = ?";
        try (Connection conn = DriverManager.getConnection(CONNECTION_STRING, USERNAME, PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, series);
            pstmt.setString(2, number);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1; // bulunamazsa
    }

    // Fatura sil (invoiceitems + invoices)
    public boolean deleteInvoiceById(int invoiceId) {
        String sqlDeleteItems = "DELETE FROM invoiceitems WHERE invoice_id = ?";
        String sqlDeleteInvoice = "DELETE FROM invoices WHERE id = ?";
        try (Connection conn = DriverManager.getConnection(CONNECTION_STRING, USERNAME, PASSWORD)) {
            conn.setAutoCommit(false);
            try (PreparedStatement pstmtItems = conn.prepareStatement(sqlDeleteItems);
                 PreparedStatement pstmtInvoice = conn.prepareStatement(sqlDeleteInvoice)) {

                pstmtItems.setInt(1, invoiceId);
                pstmtItems.executeUpdate();

                pstmtInvoice.setInt(1, invoiceId);
                int affectedRows = pstmtInvoice.executeUpdate();

                conn.commit();

                return affectedRows > 0;
            } catch (SQLException ex) {
                conn.rollback();
                ex.printStackTrace();
                return false;
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Fatura var mı kontrol et
    public boolean invoiceExists(String series, String invoiceNum) {
        String sql = "SELECT COUNT(*) FROM invoices WHERE series = ? AND invoice = ?";
        try (Connection conn = DriverManager.getConnection(CONNECTION_STRING, USERNAME, PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, series);
            pstmt.setString(2, invoiceNum);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    int count = rs.getInt(1);
                    return count > 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Tüm fatura özetlerini getir
    public List<InvoiceSummary> getAllInvoiceSummaries() {
        List<InvoiceSummary> list = new ArrayList<>();
        String sql = "SELECT id, series, invoice, customer_id, discount, total FROM invoices";
        try (Connection conn = DriverManager.getConnection(CONNECTION_STRING, USERNAME, PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                list.add(new InvoiceSummary(
                        rs.getInt("id"),
                        rs.getString("series"),
                        rs.getString("invoice"),
                        rs.getInt("customer_id"),
                        rs.getDouble("discount"),
                        rs.getDouble("total")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // Faturaya ait ürünleri getir
    public List<InvoiceItem> getInvoiceItems(int invoiceId) {
        List<InvoiceItem> items = new ArrayList<>();
        String sql = "SELECT ii.item_id, it.name, it.price, ii.quantity, ii.total FROM invoiceitems ii JOIN items it ON ii.item_id = it.id WHERE ii.invoice_id = ?";
        try (Connection conn = DriverManager.getConnection(CONNECTION_STRING, USERNAME, PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, invoiceId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    items.add(new InvoiceItem(
                            rs.getInt("item_id"),
                            rs.getString("name"),
                            rs.getDouble("price"),
                            rs.getInt("quantity"),
                            rs.getDouble("total")
                    ));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return items;
    }

    // ID ile müşteri getir
    public Customer getCustomerById(int id) {
        String sql = "SELECT id, name, surname, tckn FROM customers WHERE id = ?";
        try (Connection conn = DriverManager.getConnection(CONNECTION_STRING, USERNAME, PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new Customer(
                            rs.getInt("id"),
                            rs.getString("name"),
                            rs.getString("surname"),
                            rs.getString("tckn")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
