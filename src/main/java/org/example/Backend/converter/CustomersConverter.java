package org.example.Backend.converter;

import org.example.Backend.model.Customers;
import org.json.JSONObject;

public class CustomersConverter {

    public static Customers fromJson(JSONObject json) {
        Customers c = new Customers();
        if(json.has("id")) c.setId(json.getInt("id"));
        c.setName(json.getString("name"));
        c.setSurname(json.getString("surname"));
        c.setTckn(json.getString("tckn"));
        return c;
    }

    public static JSONObject toJson(Customers c) {
        return new JSONObject()
                .put("id", c.getId())
                .put("name", c.getName())
                .put("surname", c.getSurname())
                .put("tckn", c.getTckn());
    }
}
