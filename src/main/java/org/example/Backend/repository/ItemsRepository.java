package org.example.Backend.repository;

import org.example.model.Items;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ItemsRepository {
    private final Connection conn;

    public ItemsRepository(Connection conn) { this.conn = conn; }

    public List<Items> getAll() {
        List<Items> list = new ArrayList<>();
        String sql = "SELECT * FROM items";
        try (Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new Items(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getDouble("price")
                ));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public Items getById(int id) {
        String sql = "SELECT * FROM items WHERE id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return new Items(rs.getInt("id"), rs.getString("name"), rs.getDouble("price"));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    public boolean add(Items item) {
        String sql = "INSERT INTO items (name, price) VALUES (?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, item.getName());
            ps.setDouble(2, item.getPrice());
            int rows = ps.executeUpdate();
            if (rows > 0) {
                try (ResultSet keys = ps.getGeneratedKeys()) { if (keys.next()) item.setId(keys.getInt(1)); }
                return true;
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    public boolean update(Items item) {
        String sql = "UPDATE items SET name=?, price=? WHERE id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, item.getName());
            ps.setDouble(2, item.getPrice());
            ps.setInt(3, item.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    public boolean delete(int id) {
        String sql = "DELETE FROM items WHERE id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }
}
