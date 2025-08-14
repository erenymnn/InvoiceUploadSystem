package org.example.Backend.converter;

import org.example.Backend.model.Invoices;
import org.json.JSONObject;

import java.sql.ResultSet;
import java.sql.SQLException;

public class InvoicesConverter {
    public static Invoices fromResultSet(ResultSet rs) throws SQLException {
        return new Invoices(
                rs.getInt("id"),
                rs.getString("series"),
                rs.getString("Invoice"),
                rs.getInt("customer_id"),
                rs.getDouble("discount"),
                rs.getDouble("total")
        );
    }

    public static Invoices fromJson(JSONObject json) {
        Invoices inv = new Invoices();
        if (json.has("id")) inv.setId(json.getInt("id"));
        inv.setSeries(json.getString("series"));
        inv.setInvoice(json.getString("Invoice"));
        inv.setCustomerId(json.getInt("customer_id"));
        inv.setDiscount(json.getDouble("discount"));
        inv.setTotal(json.getDouble("total"));
        return inv;
    }

    public static JSONObject toJson(Invoices inv) {
        return new JSONObject()
                .put("id", inv.getId())
                .put("series", inv.getSeries())
                .put("Invoice", inv.getInvoice())
                .put("customer_id", inv.getCustomerId())
                .put("discount", inv.getDiscount())
                .put("total", inv.getTotal());
    }
}
