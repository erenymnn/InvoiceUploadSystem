package org.example.app;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import org.json.JSONObject;




public class invoiceJSONForm extends JFrame {
    private final DBHelper dbHelper;
    private final int selectedInvoiceId;

    public invoiceJSONForm(DBHelper dbHelper, int selectedInvoiceId) {
        this.dbHelper = dbHelper;
        this.selectedInvoiceId = selectedInvoiceId;

        setTitle("Fatura JSON");
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null); // Ortada aç
        setLayout(new BorderLayout());

        JTextArea jsonTextArea = new JTextArea();
        jsonTextArea.setEditable(false);
        jsonTextArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        jsonTextArea.setLineWrap(true);
        jsonTextArea.setWrapStyleWord(true);

        JScrollPane scrollPane = new JScrollPane(jsonTextArea);
        add(scrollPane, BorderLayout.CENTER);

        // Fetch the invoice from the database and convert it to JSON
        try (Connection conn = dbHelper.connect();
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT id, series, invoice, customer_id, discount, total " +
                             "FROM invoices WHERE id = ?"
             )) {

            stmt.setInt(1, selectedInvoiceId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    JSONObject json = new JSONObject();
                    json.put("id", rs.getInt("id"));
                    json.put("series", rs.getString("series"));
                    json.put("invoice", rs.getString("invoice"));
                    json.put("customer_id", rs.getInt("customer_id"));
                    json.put("discount", rs.getDouble("discount"));
                    json.put("total", rs.getDouble("total"));

                    jsonTextArea.setText(json.toString(4)); // 4 boşluk ile formatlı gösterim
                } else {
                    jsonTextArea.setText("Fatura bulunamadı.");
                }
            }

        } catch (SQLException e) {
            jsonTextArea.setText("Veritabanı hatası: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            jsonTextArea.setText("Beklenmeyen hata: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
