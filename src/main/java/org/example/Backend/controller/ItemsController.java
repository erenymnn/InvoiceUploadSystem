package org.example.Backend.controller;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.example.Backend.converter.ItemsConverter;
import org.example.Backend.model.Items;
import org.example.Backend.service.ItemsService;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class ItemsController implements HttpHandler {
    private final ItemsService service;
    public ItemsController(ItemsService service) { this.service = service; }

    @Override
    public void handle(HttpExchange exchange) {
        try {
            String method = exchange.getRequestMethod();
            String path = exchange.getRequestURI().getPath();
            String[] parts = path.split("/");

            switch (method.toUpperCase()) {
                case "GET":
                    if (parts.length == 2) {
                        List<Items> list = service.getAllItems();
                        JSONArray arr = new JSONArray();
                        for (Items i : list) arr.put(ItemsConverter.toJson(i));
                        send(exchange, 200, arr.toString(4));
                    } else if (parts.length == 3) {
                        int id = Integer.parseInt(parts[2]);
                        Items item = service.getItemById(id);
                        if (item != null) send(exchange, 200, ItemsConverter.toJson(item).toString());
                        else send(exchange, 404, "{\"error\":\"Item not found\"}");
                    } else send(exchange, 400, "{\"error\":\"Invalid GET\"}");
                    break;

                case "POST":
                    JSONObject postObj = new JSONObject(readBody(exchange));
                    Items newItem = ItemsConverter.fromJson(postObj);
                    boolean added = service.addItem(newItem);
                    if (added) send(exchange, 201, ItemsConverter.toJson(newItem).toString());
                    else send(exchange, 400, "{\"error\":\"Cannot add item\"}");
                    break;

                case "PUT":
                    if (parts.length != 3) { send(exchange, 400, "{\"error\":\"ID missing\"}"); return; }
                    int idPut = Integer.parseInt(parts[2]);
                    JSONObject putObj = new JSONObject(readBody(exchange));
                    Items updItem = ItemsConverter.fromJson(putObj);
                    updItem.setId(idPut);
                    boolean updated = service.updateItem(updItem);
                    if (updated) send(exchange, 200, ItemsConverter.toJson(updItem).toString());
                    else send(exchange, 404, "{\"error\":\"Item not found\"}");
                    break;

                case "DELETE":
                    if (parts.length != 3) { send(exchange, 400, "{\"error\":\"ID missing\"}"); return; }
                    int idDel = Integer.parseInt(parts[2]);
                    boolean deleted = service.deleteItem(idDel);
                    if (deleted) send(exchange, 200, "{\"status\":\"deleted\"}");
                    else send(exchange, 404, "{\"error\":\"Item not found\"}");
                    break;

                default: send(exchange, 405, "{\"error\":\"Method not allowed\"}");
            }

        } catch (Exception e) {
            e.printStackTrace();
            try { send(exchange, 500, "{\"error\":\"Server error\"}"); } catch (Exception ignored) {}
        }
    }

    private String readBody(HttpExchange exchange) throws Exception {
        try (InputStream is = exchange.getRequestBody()) { return new String(is.readAllBytes(), StandardCharsets.UTF_8); }
    }

    private void send(HttpExchange exchange, int code, String resp) throws Exception {
        byte[] bytes = resp.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=utf-8");
        exchange.sendResponseHeaders(code, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) { os.write(bytes); }
    }
}
