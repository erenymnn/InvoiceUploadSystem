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

public class itemsHandler implements HttpHandler {
    private final DBHelper dbHelper;

    public itemsHandler(DBHelper dbHelper) {
        this.dbHelper = dbHelper;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();
        String[] pathParts = path.split("/");

        try {
            switch (method.toUpperCase()) {
                case "GET":
                    // /items veya /items/{id}
                    if (pathParts.length == 2) {
                        // Tüm ürünleri getir
                        List<DBHelper.Product> products = dbHelper.getAllProducts();
                        JSONArray arr = new JSONArray();
                        for (DBHelper.Product p : products) {
                            JSONObject obj = new JSONObject();
                            obj.put("id", p.getId());
                            obj.put("name", p.getName());
                            obj.put("price", p.getPrice());
                            arr.put(obj);
                        }
                        sendResponse(exchange, 200, arr.toString());
                    } else if (pathParts.length == 3) {
                        // Belirli bir ürün getir
                        int id = Integer.parseInt(pathParts[2]);
                        DBHelper.Product product = dbHelper.getProductById(id);
                        if (product != null) {
                            JSONObject obj = new JSONObject();
                            obj.put("id", product.getId());
                            obj.put("name", product.getName());
                            obj.put("price", product.getPrice());
                            sendResponse(exchange, 200, obj.toString());
                        } else {
                            sendResponse(exchange, 404, "{\"error\":\"Ürün bulunamadı.\"}");
                        }
                    } else {
                        sendResponse(exchange, 400, "{\"error\":\"Geçersiz istek.\"}");
                    }
                    break;

                case "POST":
                    // Yeni ürün ekle
                    InputStream isPost = exchange.getRequestBody();
                    String bodyPost = new String(isPost.readAllBytes(), StandardCharsets.UTF_8);
                    JSONObject objPost = new JSONObject(bodyPost);

                    String namePost = objPost.getString("name");
                    double pricePost = objPost.getDouble("price");

                    boolean added = dbHelper.addProduct(namePost, pricePost);
                    if (added) {
                        sendResponse(exchange, 201, "{\"status\":\"success\",\"message\":\"Ürün eklendi.\"}");
                    } else {
                        sendResponse(exchange, 400, "{\"status\":\"error\",\"message\":\"Ürün eklenemedi.\"}");
                    }
                    break;

                case "PUT":
                    // Ürün güncelle /items/{id}
                    if (pathParts.length != 3) {
                        sendResponse(exchange, 400, "{\"error\":\"Ürün ID eksik veya fazla.\"}");
                        return;
                    }
                    int idPut = Integer.parseInt(pathParts[2]);
                    InputStream isPut = exchange.getRequestBody();
                    String bodyPut = new String(isPut.readAllBytes(), StandardCharsets.UTF_8);
                    JSONObject objPut = new JSONObject(bodyPut);

                    String namePut = objPut.getString("name");
                    double pricePut = objPut.getDouble("price");

                    boolean updated = dbHelper.updateProduct(idPut, namePut, pricePut);
                    if (updated) {
                        sendResponse(exchange, 200, "{\"status\":\"success\",\"message\":\"Ürün güncellendi.\"}");
                    } else {
                        sendResponse(exchange, 404, "{\"status\":\"error\",\"message\":\"Ürün bulunamadı veya güncellenemedi.\"}");
                    }
                    break;

                case "DELETE":
                    // /items/{id} silme
                    if (pathParts.length != 3) {
                        sendResponse(exchange, 400, "{\"error\":\"Ürün ID eksik veya fazla.\"}");
                        return;
                    }
                    int idDelete = Integer.parseInt(pathParts[2]);
                    boolean deleted = dbHelper.deleteProduct(idDelete);
                    if (deleted) {
                        sendResponse(exchange, 200, "{\"status\":\"success\",\"message\":\"Ürün silindi.\"}");
                    } else {
                        sendResponse(exchange, 404, "{\"status\":\"error\",\"message\":\"Ürün bulunamadı veya silinemedi.\"}");
                    }
                    break;

                default:
                    exchange.sendResponseHeaders(405, -1); // Method Not Allowed
            }
        } catch (NumberFormatException e) {
            sendResponse(exchange, 400, "{\"error\":\"ID sayısal olmalı.\"}");
        } catch (Exception e) {
            e.printStackTrace();
            sendResponse(exchange, 500, "{\"error\":\"Sunucu hatası.\"}");
        }
    }

    private void sendResponse(HttpExchange exchange, int statusCode, String response) throws IOException {
        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=utf-8");
        byte[] bytes = response.getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(statusCode, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }
}
