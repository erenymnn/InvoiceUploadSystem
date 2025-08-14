package org.example.Backend.repository;

import org.example.model.InvoiceItems;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class InvoiceItemsRepository {
    private final Connection conn;

    public InvoiceItemsRepository(Connection conn) { this.conn = conn; }

    public List<InvoiceItems> getAll() {
        List<InvoiceItems> list = new ArrayList<>();
        String sql = "SELECT * FROM invoiceitems";
        try (Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new InvoiceItems(
                        rs.getInt("id"),
                        rs.getInt("invoice_id"),
                        rs.getInt("item_id"),
                        rs.getInt("quantity"),
                        rs.getDouble("total")
                ));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public List<InvoiceItems> getByInvoiceId(int invoiceId) {
        List<InvoiceItems> list = new ArrayList<>();
        String sql = "SELECT * FROM invoiceitems WHERE invoice_id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, invoiceId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new InvoiceItems(
                            rs.getInt("id"),
                            rs.getInt("invoice_id"),
                            rs.getInt("item_id"),
                            rs.getInt("quantity"),
                            rs.getDouble("total")
                    ));
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public boolean add(InvoiceItems item) {
        String sql = "INSERT INTO invoiceitems (invoice_id, item_id, quantity, total) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, item.getInvoiceId());
            ps.setInt(2, item.getItemId());
            ps.setInt(3, item.getQuantity());
            ps.setDouble(4, item.getTotal());
            int rows = ps.executeUpdate();
            if (rows > 0) {
                try (ResultSet keys = ps.getGeneratedKeys()) { if (keys.next()) item.setId(keys.getInt(1)); }
                return true;
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    public boolean update(InvoiceItems item) {
        String sql = "UPDATE invoiceitems SET invoice_id=?, item_id=?, quantity=?, total=? WHERE id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, item.getInvoiceId());
            ps.setInt(2, item.getItemId());
            ps.setInt(3, item.getQuantity());
            ps.setDouble(4, item.getTotal());
            ps.setInt(5, item.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    public boolean delete(int id) {
        String sql = "DELETE FROM invoiceitems WHERE id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }public int addAndReturnId(InvoiceItems item) {
        String sql = "INSERT INTO invoiceitems (invoice_id, item_id, quantity, total) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, item.getInvoiceId());
            ps.setInt(2, item.getItemId());
            ps.setInt(3, item.getQuantity());
            ps.setDouble(4, item.getTotal());
            int rows = ps.executeUpdate();
            if (rows > 0) {
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) {
                        int id = keys.getInt(1);
                        item.setId(id);
                        return id; // Ürün detay ID'si döner
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1; // ekleme başarısızsa
    }


}
