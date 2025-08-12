package org.example.Http;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.example.app.DBHelper;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class CustomerHandler implements HttpHandler {
    private DBHelper dbHelper;

    public CustomerHandler(DBHelper dbHelper) {
        this.dbHelper = dbHelper;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();

        try {
            if ("GET".equalsIgnoreCase(method)) {
                List<DBHelper.Customer> customers = dbHelper.getAllCustomers();
                JSONArray arr = new JSONArray();
                for (DBHelper.Customer c : customers) {
                    JSONObject obj = new JSONObject();
                    obj.put("id", c.getId());
                    obj.put("name", c.getName());
                    obj.put("surname", c.getSurname());
                    obj.put("tckn", c.getTckn());
                    arr.put(obj);
                }
                sendJsonResponse(exchange, 200, arr.toString());

            } else if ("POST".equalsIgnoreCase(method)) {
                JSONObject obj = new JSONObject(readRequestBody(exchange));
                int id = obj.getInt("id"); // id zorunlu
                boolean success = dbHelper.musteriEkle(
                        id,
                        obj.getString("name"),
                        obj.getString("surname"),
                        obj.getString("tckn")
                );
                if (success) {
                    sendJsonResponse(exchange, 201, "{\"status\":\"success\",\"message\":\"Müşteri eklendi.\"}");
                } else {
                    sendJsonResponse(exchange, 400, "{\"status\":\"error\",\"message\":\"Müşteri eklenemedi.\"}");
                }

            } else if ("PUT".equalsIgnoreCase(method)) {
                try (InputStream is = exchange.getRequestBody()) {
                    String body = new String(is.readAllBytes(), StandardCharsets.UTF_8);
                    JSONObject obj = new JSONObject(body);

                    int id = obj.getInt("id"); // Güncellenecek müşteri id'si
                    String name = obj.getString("name");
                    String surname = obj.getString("surname");
                    String tckn = obj.getString("tckn");

                    boolean updated = dbHelper.updateMusteri(id, name, surname, tckn);

                    String response;
                    if (updated) {
                        response = "{\"status\":\"success\",\"message\":\"Müşteri güncellendi.\"}";
                        exchange.sendResponseHeaders(200, response.getBytes(StandardCharsets.UTF_8).length);
                    } else {
                        response = "{\"status\":\"error\",\"message\":\"Müşteri güncellenemedi.\"}";
                        exchange.sendResponseHeaders(400, response.getBytes(StandardCharsets.UTF_8).length);
                    }
                    exchange.getResponseHeaders().set("Content-Type", "application/json");
                    try (OutputStream os = exchange.getResponseBody()) {
                        os.write(response.getBytes(StandardCharsets.UTF_8));
                    }
                } catch (Exception e) {
                    String response = "{\"status\":\"error\",\"message\":\"Geçersiz veri.\"}";
                    exchange.getResponseHeaders().set("Content-Type", "application/json");
                    exchange.sendResponseHeaders(400, response.getBytes(StandardCharsets.UTF_8).length);
                    try (OutputStream os = exchange.getResponseBody()) {
                        os.write(response.getBytes(StandardCharsets.UTF_8));
                    }
                }
            }
            else if ("DELETE".equalsIgnoreCase(method)) {
                String path = exchange.getRequestURI().getPath();
                String[] parts = path.split("/");
                if (parts.length == 3) {
                    try {
                        int idToDelete = Integer.parseInt(parts[2]);
                        boolean deleted = dbHelper.deleteCustomerById(idToDelete);
                        if (deleted) {
                            sendJsonResponse(exchange, 200, "{\"status\":\"success\",\"message\":\"Müşteri silindi.\"}");
                        } else {
                            sendJsonResponse(exchange, 404, "{\"status\":\"error\",\"message\":\"Müşteri bulunamadı.\"}");
                        }
                    } catch (NumberFormatException e) {
                        sendJsonResponse(exchange, 400, "{\"status\":\"error\",\"message\":\"Geçersiz ID.\"}");
                    }
                } else {
                    sendJsonResponse(exchange, 400, "{\"status\":\"error\",\"message\":\"Eksik parametre.\"}");
                }

            } else {
                sendJsonResponse(exchange, 405, "{\"status\":\"error\",\"message\":\"Metod desteklenmiyor.\"}");
            }
        } catch (Exception e) {
            sendJsonResponse(exchange, 500, "{\"status\":\"error\",\"message\":\"Sunucu hatası.\"}");
        }
    }

    private String readRequestBody(HttpExchange exchange) throws IOException {
        try (InputStream is = exchange.getRequestBody()) {
            return new String(is.readAllBytes(), StandardCharsets.UTF_8);
        }
    }

    private void sendJsonResponse(HttpExchange exchange, int statusCode, String json) throws IOException {
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        byte[] bytes = json.getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(statusCode, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }
}
