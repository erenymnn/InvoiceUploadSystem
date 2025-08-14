package org.example.Backend.converter;

import org.example.Backend.model.Items;
import org.json.JSONObject;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ItemsConverter {
    public static Items fromResultSet(ResultSet rs) throws SQLException {
        return new Items(rs.getInt("id"), rs.getString("name"), rs.getDouble("price"));
    }

    public static Items fromJson(JSONObject json) {
        Items item = new Items();
        if (json.has("id")) item.setId(json.getInt("id"));
        item.setName(json.getString("name"));
        item.setPrice(json.getDouble("price"));
        return item;
    }

    public static JSONObject toJson(Items item) {
        return new JSONObject()
                .put("id", item.getId())
                .put("name", item.getName())
                .put("price", item.getPrice());
    }
}
