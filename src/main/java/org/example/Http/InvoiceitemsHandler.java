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

public class InvoiceitemsHandler implements HttpHandler {
    private final DBHelper dbHelper;

    public InvoiceitemsHandler(DBHelper dbHelper) {
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
                    if (pathParts.length == 2) {
                        // /invoiceitems -> tüm invoiceitems'lar
                        List<DBHelper.InvoiceItem> allItems = dbHelper.getAllInvoiceItems();
                        JSONArray arrAll = new JSONArray();
                        for (DBHelper.InvoiceItem item : allItems) {
                            JSONObject obj = new JSONObject();
                            obj.put("id", item.getId());
                            obj.put("invoiceId", item.getInvoiceId());
                            obj.put("itemId", item.getItemId());
                            obj.put("quantity", item.getQuantity());
                            obj.put("total", item.getTotal());
                            arrAll.put(obj);
                        }
                        sendResponse(exchange, 200, arrAll.toString());

                    } else if (pathParts.length == 3) {
                        // /invoiceitems/{invoiceId} -> sadece belirtilen faturaya ait kalemler
                        int invoiceId = Integer.parseInt(pathParts[2]);
                        List<DBHelper.InvoiceItem> items = dbHelper.getInvoiceItems(invoiceId);
                        JSONArray arr = new JSONArray();
                        for (DBHelper.InvoiceItem item : items) {
                            JSONObject obj = new JSONObject();
                            obj.put("id", item.getId());
                            obj.put("invoiceId", item.getInvoiceId());
                            obj.put("itemId", item.getItemId());
                            obj.put("quantity", item.getQuantity());
                            obj.put("total", item.getTotal());
                            arr.put(obj);
                        }
                        sendResponse(exchange, 200, arr.toString());

                    } else {
                        sendResponse(exchange, 400, "{\"error\":\"Geçersiz GET isteği.\"}");
                    }
                    break;

                case "POST":
                    // Yeni invoice item ekleme
                    InputStream isPost = exchange.getRequestBody();
                    String bodyPost = new String(isPost.readAllBytes(), StandardCharsets.UTF_8);
                    JSONObject objPost = new JSONObject(bodyPost);

                    int invoiceIdPost = objPost.getInt("invoiceId");
                    int itemIdPost = objPost.getInt("itemId");
                    int quantityPost = objPost.getInt("quantity");
                    double totalPost = objPost.getDouble("total");

                    boolean successPost = dbHelper.addInvoiceItem(invoiceIdPost, itemIdPost, quantityPost, totalPost);

                    if (successPost) {
                        sendResponse(exchange, 201, "{\"status\":\"success\",\"message\":\"Fatura kalemi eklendi.\"}");
                    } else {
                        sendResponse(exchange, 400, "{\"status\":\"error\",\"message\":\"Fatura kalemi eklenemedi.\"}");
                    }
                    break;

                case "PUT":
                    // /invoiceitems/{itemId} güncelleme
                    if (pathParts.length < 3) {
                        sendResponse(exchange, 400, "{\"error\":\"Invoice Item ID eksik.\"}");
                        return;
                    }
                    int idPut = Integer.parseInt(pathParts[2]);
                    InputStream isPut = exchange.getRequestBody();
                    String bodyPut = new String(isPut.readAllBytes(), StandardCharsets.UTF_8);
                    JSONObject objPut = new JSONObject(bodyPut);

                    int quantityPut = objPut.getInt("quantity");
                    double totalPut = objPut.getDouble("total");

                    boolean successPut = dbHelper.updateInvoiceItemById(idPut, quantityPut, totalPut);

                    if (successPut) {
                        sendResponse(exchange, 200, "{\"status\":\"success\",\"message\":\"Fatura kalemi güncellendi.\"}");
                    } else {
                        sendResponse(exchange, 400, "{\"status\":\"error\",\"message\":\"Güncelleme başarısız.\"}");
                    }
                    break;

                case "DELETE":
                    // /invoiceitems/{itemId} silme
                    if (pathParts.length < 3) {
                        sendResponse(exchange, 400, "{\"error\":\"Invoice Item ID eksik.\"}");
                        return;
                    }
                    int idDelete = Integer.parseInt(pathParts[2]);
                    boolean successDelete = dbHelper.deleteInvoiceItemById(idDelete);

                    if (successDelete) {
                        sendResponse(exchange, 200, "{\"status\":\"success\",\"message\":\"Fatura kalemi silindi.\"}");
                    } else {
                        sendResponse(exchange, 400, "{\"status\":\"error\",\"message\":\"Silme işlemi başarısız.\"}");
                    }
                    break;

                default:
                    exchange.sendResponseHeaders(405, -1); // Method Not Allowed
            }
        } catch (NumberFormatException e) {
            sendResponse(exchange, 400, "{\"error\":\"ID değeri sayısal olmalı.\"}");
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
