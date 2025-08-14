package org.example.Backend.converter;

import org.example.Backend.model.InvoiceItems;
import org.json.JSONObject;

import java.sql.ResultSet;
import java.sql.SQLException;

public class InvoiceItemsConverter {
    public static InvoiceItems fromResultSet(ResultSet rs) throws SQLException {
        return new InvoiceItems(
                rs.getInt("id"),
                rs.getInt("invoice_id"),
                rs.getInt("item_id"),
                rs.getInt("quantity"),
                rs.getDouble("total")
        );
    }

    public static InvoiceItems fromJson(JSONObject json) {
        InvoiceItems item = new InvoiceItems();
        if (json.has("id")) item.setId(json.getInt("id"));
        item.setInvoiceId(json.getInt("invoice_id"));
        item.setItemId(json.getInt("item_id"));
        item.setQuantity(json.getInt("quantity"));
        item.setTotal(json.getDouble("total"));
        return item;
    }

    public static JSONObject toJson(InvoiceItems item) {
        return new JSONObject()
                .put("id", item.getId())
                .put("invoice_id", item.getInvoiceId())
                .put("item_id", item.getItemId())
                .put("quantity", item.getQuantity())
                .put("total", item.getTotal());
    }
}
