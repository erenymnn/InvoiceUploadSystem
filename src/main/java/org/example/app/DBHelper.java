package org.example.app;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DBHelper {

    private static final String CONNECTION_STRING = "jdbc:mysql://localhost:3306/upload_system";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "1234";

    // -------- INNER CLASS'LAR --------

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

    public static class InvoiceItem {
        private int id;             // invoiceitems tablosundaki primary key
        private int invoiceId;
        private int itemId;
        private int quantity;
        private double total;

        public InvoiceItem(int id, int invoiceId, int itemId, int quantity, double total) {
            this.id = id;
            this.invoiceId = invoiceId;
            this.itemId = itemId;
            this.quantity = quantity;
            this.total = total;
        }

        public int getId() { return id; }
        public int getInvoiceId() { return invoiceId; }
        public int getItemId() { return itemId; }
        public int getQuantity() { return quantity; }
        public double getTotal() { return total; }
    }

    // -------- VERİTABANI BAĞLANTISI --------

    public Connection connect() throws SQLException {
        return DriverManager.getConnection(CONNECTION_STRING, USERNAME, PASSWORD);
    }

    // -------- CUSTOMER METODLARI --------

    public List<Customer> getAllCustomers() {
        List<Customer> customers = new ArrayList<>();
        String sql = "SELECT id, name, surname, tckn FROM customers";
        try (Connection conn = connect();
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

    public Customer getCustomerById(int id) {
        String sql = "SELECT id, name, surname, tckn FROM customers WHERE id = ?";
        try (Connection conn = connect();
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

    public boolean musteriEkle(int id, String name, String surname, String tckn) {
        String sql = "INSERT INTO customers (id, name, surname, tckn) VALUES (?, ?, ?, ?)";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.setString(2, name);
            pstmt.setString(3, surname);
            pstmt.setString(4, tckn);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


    public boolean updateMusteri(int id, String name, String surname, String tckn) {
        String sql = "UPDATE customers SET name = ?, surname = ?, tckn = ? WHERE id = ?";
        try (Connection conn = connect();
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

    public boolean deleteCustomerById(int id) {
        String sql = "DELETE FROM customers WHERE id = ?";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // -------- PRODUCT / ITEM METODLARI --------

    public List<Product> getAllProducts() {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT id, name, price FROM items";
        try (Connection conn = connect();
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
    public String getProductNameById(int itemId) {
        String productName = null;
        String sql = "SELECT name FROM products WHERE id = ?";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, itemId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    productName = rs.getString("name");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return productName;
    }

    public double getProductPriceById(int itemId) {
        double price = 0.0;
        String sql = "SELECT price FROM products WHERE id = ?";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, itemId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    price = rs.getDouble("price");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return price;
    }



    public boolean addProduct(String name, double price) {
        String sql = "INSERT INTO items (name, price) VALUES (?, ?)";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, name);
            pstmt.setDouble(2, price);




            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


    public boolean updateProduct(int id, String name, double price) {
        String sql = "UPDATE items SET name = ?, price = ? WHERE id = ?";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, name);
            pstmt.setDouble(2, price);
            pstmt.setInt(3, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteProduct(int id) {
        String sql = "DELETE FROM items WHERE id = ?";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    public Product getProductById(int id) {
        String sql = "SELECT id, name, price FROM items WHERE id = ?";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new Product(
                            rs.getInt("id"),
                            rs.getString("name"),
                            rs.getDouble("price")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }


    // -------- INVOICE METODLARI --------

    public int addInvoice(String series, String invoice, int customerId, double discount, double total) {
        String sql = "INSERT INTO invoices (series, invoice, customer_id, discount, total) VALUES (?, ?, ?, ?, ?)";
        int generatedId = -1;

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, series);
            pstmt.setString(2, invoice);
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

    public boolean updateInvoice(int id, String series, String invoiceNum, int customerId, double discount, double total) {
        String sql = "UPDATE invoices SET series = ?, invoice = ?, customer_id = ?, discount = ?, total = ? WHERE id = ?";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, series);
            pstmt.setString(2, invoiceNum);
            pstmt.setInt(3, customerId);
            pstmt.setDouble(4, discount);
            pstmt.setDouble(5, total);
            pstmt.setInt(6, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteInvoiceById(int invoiceId) {
        String sqlDeleteItems = "DELETE FROM invoiceitems WHERE invoice_id = ?";
        String sqlDeleteInvoice = "DELETE FROM invoices WHERE id = ?";
        try (Connection conn = connect()) {
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

    public int findInvoiceIdBySeriesAndNumber(String series, String number) {
        String sql = "SELECT id FROM invoices WHERE series = ? AND invoice = ?";
        try (Connection conn = connect();
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
        return -1;
    }

    public boolean invoiceExists(String series, String invoiceNum) {
        String sql = "SELECT COUNT(*) FROM invoices WHERE series = ? AND invoice = ?";
        try (Connection conn = connect();
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

    public List<InvoiceSummary> getInvoices() {
        List<InvoiceSummary> list = new ArrayList<>();
        String sql = "SELECT id, series, invoice, customer_id, discount, total FROM invoices";
        try (Connection conn = connect();
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


    public InvoiceSummary getInvoiceById(int id) {
        String sql = "SELECT id, series, invoice, customer_id, discount, total FROM invoices WHERE id = ?";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new InvoiceSummary(
                            rs.getInt("id"),
                            rs.getString("series"),
                            rs.getString("invoice"),
                            rs.getInt("customer_id"),
                            rs.getDouble("discount"),
                            rs.getDouble("total")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }


    // -------- INVOICE ITEMS --------
    public List<Map<String, Object>> getInvoicesMaps() {
        List<Map<String, Object>> list = new ArrayList<>();
        String sql = "SELECT id, series, invoice, customer_id, discount, total FROM invoices";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();

            while (rs.next()) {
                Map<String, Object> row = new HashMap<>();
                for (int i = 1; i <= columnCount; i++) {
                    row.put(metaData.getColumnName(i), rs.getObject(i));
                }
                list.add(row);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<InvoiceItem> getInvoiceItems(int invoiceId) {
        List<InvoiceItem> items = new ArrayList<>();
        String sql = "SELECT id, invoice_id, item_id, quantity, total FROM invoiceitems WHERE invoice_id = ?";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, invoiceId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    items.add(new InvoiceItem(
                            rs.getInt("id"),
                            rs.getInt("invoice_id"),
                            rs.getInt("item_id"),
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


    public boolean addInvoiceItem(int invoiceId, int itemId, int quantity, double total) {
        String sql = "INSERT INTO invoiceitems (invoice_id, item_id, quantity, total) VALUES (?, ?, ?, ?)";
        try (Connection conn = connect();
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

    public List<InvoiceItem> getAllInvoiceItems() {
        List<InvoiceItem> items = new ArrayList<>();
        String sql = "SELECT id, invoice_id, item_id, quantity, total FROM invoiceitems";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                items.add(new InvoiceItem(
                        rs.getInt("id"),
                        rs.getInt("invoice_id"),
                        rs.getInt("item_id"),
                        rs.getInt("quantity"),
                        rs.getDouble("total")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return items;
    }

    public boolean updateInvoiceItemById(int id, int quantity, double total) {
        String sql = "UPDATE invoiceitems SET quantity = ?, total = ? WHERE id = ?";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, quantity);
            pstmt.setDouble(2, total);
            pstmt.setInt(3, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteInvoiceItemById(int id) {
        String sql = "DELETE FROM invoiceitems WHERE id = ?";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

}
