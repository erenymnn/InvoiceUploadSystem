package org.example.Backend.repository;

import org.example.Backend.model.Invoices;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class InvoicesRepository {
    private final Connection conn;

    public InvoicesRepository(Connection conn) { this.conn = conn; }

    // Tüm faturaları getir
    public List<Invoices> getAll() {
        List<Invoices> list = new ArrayList<>();
        String sql = "SELECT * FROM invoices";

        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while(rs.next()) {
                list.add(mapResultSetToInvoice(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    // ID ile fatura getir
    public Invoices getById(int id) {
        String sql = "SELECT * FROM invoices WHERE id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if(rs.next()) return mapResultSetToInvoice(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Fatura ekle
    public boolean add(Invoices inv) {
        String sql = "INSERT INTO invoices (series, invoice, customer_id, discount, total) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, inv.getSeries());
            ps.setString(2, inv.getInvoice());
            ps.setInt(3, inv.getCustomerId());
            ps.setDouble(4, inv.getDiscount());
            ps.setDouble(5, inv.getTotal());

            int rows = ps.executeUpdate();
            if(rows > 0) {
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if(keys.next()) inv.setId(keys.getInt(1));
                }
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Fatura güncelle
    public boolean update(Invoices inv) {
        String sql = "UPDATE invoices SET series=?, invoice=?, customer_id=?, discount=?, total=? WHERE id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, inv.getSeries());
            ps.setString(2, inv.getInvoice());
            ps.setInt(3, inv.getCustomerId());
            ps.setDouble(4, inv.getDiscount());
            ps.setDouble(5, inv.getTotal());
            ps.setInt(6, inv.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Fatura sil
    public boolean delete(int id) {
        String sql = "DELETE FROM invoices WHERE id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // ResultSet -> Invoice mapleme
    private Invoices mapResultSetToInvoice(ResultSet rs) throws SQLException {
        return new Invoices(
                rs.getInt("id"),
                rs.getString("series"),
                rs.getString("invoice"), // veritabanına göre kolon adını ayarla
                rs.getInt("customer_id"),
                rs.getDouble("discount"),
                rs.getDouble("total")
        );
    }
    public int addAndReturnId(Invoices inv) {
        String sql = "INSERT INTO invoices (series, invoice, customer_id, discount, total) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, inv.getSeries());
            ps.setString(2, inv.getInvoice());
            ps.setInt(3, inv.getCustomerId());
            ps.setDouble(4, inv.getDiscount());
            ps.setDouble(5, inv.getTotal());
            int rows = ps.executeUpdate();
            if (rows > 0) {
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) {
                        int id = keys.getInt(1);
                        inv.setId(id);
                        return id;  // fatura ID'sini döndür
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;  // ekleme başarısızsa
    }

}
