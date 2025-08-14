package org.example.Backend.repository;

import org.example.Backend.model.Customers;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CustomersRepository {
    private final Connection conn;

    public CustomersRepository(Connection conn) {
        this.conn = conn;
    }

    // Fetch all customers
    public List<Customers> getAll() {
        List<Customers> list = new ArrayList<>();
        String sql = "SELECT * FROM customers";

        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                list.add(mapResultSetToCustomer(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    // Get customer by ID
    public Customers getById(int id) {
        String sql = "SELECT * FROM customers WHERE id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if(rs.next()) return mapResultSetToCustomer(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Get customer with TR ID No.
    public Customers findByTckn(String tckn) {
        String sql = "SELECT * FROM customers WHERE tckn=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, tckn);
            try (ResultSet rs = ps.executeQuery()) {
                if(rs.next()) return mapResultSetToCustomer(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    //add customer
    public boolean add(Customers c) {
        String sql = "INSERT INTO customers (name, surname, tckn) VALUES (?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, c.getName());
            ps.setString(2, c.getSurname());
            ps.setString(3, c.getTckn());

            int rows = ps.executeUpdate();
            if(rows > 0) {
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if(keys.next()) c.setId(keys.getInt(1));
                }
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // customer update
    public boolean update(Customers c) {
        String sql = "UPDATE customers SET name=?, surname=?, tckn=? WHERE id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, c.getName());
            ps.setString(2, c.getSurname());
            ps.setString(3, c.getTckn());
            ps.setInt(4, c.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // customer delete
    public boolean delete(int id) {
        String sql = "DELETE FROM customers WHERE id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // ResultSet -> Customer mapleme
    private Customers mapResultSetToCustomer(ResultSet rs) throws SQLException {
        return new Customers(
                rs.getInt("id"),
                rs.getString("name"),
                rs.getString("surname"),
                rs.getString("tckn")
        );
    }
    public Customers findByInvoice(String series, String number) {
        String sql = "SELECT c.* FROM customers c " +
                "JOIN invoices i ON c.id = i.customer_id " +
                "WHERE i.series=? AND i.invoice_num=?"; // burayı invoice_num yap
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, series);
            ps.setString(2, number);
            try (ResultSet rs = ps.executeQuery()) {
                if(rs.next()) {
                    return new Customers(
                            rs.getInt("id"),
                            rs.getString("name"),
                            rs.getString("surname"),
                            rs.getString("tckn")
                    );
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

}
