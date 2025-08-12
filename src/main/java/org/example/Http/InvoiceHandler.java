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

public class InvoiceHandler implements HttpHandler {
    private DBHelper dbHelper;

    public InvoiceHandler(DBHelper dbHelper) {
        this.dbHelper = dbHelper;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();
        String[] parts = path.split("/");

        try {
            switch (method.toUpperCase()) {
                case "GET":
                    if (parts.length == 2) {
                        // Tüm faturalar
                        handleGetAll(exchange);
                    } else if (parts.length == 3) {
                        // Tek fatura id ile
                        handleGetById(exchange, parts[2]);
                    } else {
                        sendJsonResponse(exchange, 400, "{\"error\":\"Geçersiz istek\"}");
                    }
                    break;

                case "POST":
                    handlePost(exchange);
                    break;

                case "PUT":
                    if (parts.length == 3) {
                        handlePut(exchange, parts[2]);
                    } else {
                        sendJsonResponse(exchange, 400, "{\"error\":\"ID gerekli\"}");
                    }
                    break;

                case "DELETE":
                    if (parts.length == 3) {
                        handleDelete(exchange, parts[2]);
                    } else {
                        sendJsonResponse(exchange, 400, "{\"error\":\"ID gerekli\"}");
                    }
                    break;

                default:
                    sendJsonResponse(exchange, 405, "{\"error\":\"Yöntem desteklenmiyor\"}");
            }
        } catch (Exception e) {
            sendJsonResponse(exchange, 500, "{\"error\":\"Sunucu hatası\"}");
        }
    }

    private void handleGetAll(HttpExchange exchange) throws IOException {
        List<DBHelper.InvoiceSummary> invoices = dbHelper.getInvoices();
        JSONArray arr = new JSONArray();
        for (DBHelper.InvoiceSummary inv : invoices) {
            arr.put(invoiceToJson(inv));
        }
        sendJsonResponse(exchange, 200, arr.toString(4));
    }

    private void handleGetById(HttpExchange exchange, String idStr) throws IOException {
        try {
            int id = Integer.parseInt(idStr);
            DBHelper.InvoiceSummary invoice = dbHelper.getInvoiceById(id);
            if (invoice != null) {
                sendJsonResponse(exchange, 200, invoiceToJson(invoice).toString());
            } else {
                sendJsonResponse(exchange, 404, "{\"error\":\"Fatura bulunamadı\"}");
            }
        } catch (NumberFormatException e) {
            sendJsonResponse(exchange, 400, "{\"error\":\"Geçersiz ID formatı\"}");
        }
    }

    private void handlePost(HttpExchange exchange) throws IOException {
        String body = readRequestBody(exchange);
        try {
            JSONObject obj = new JSONObject(body);

            String series = obj.getString("series");
            String invoiceNum = obj.getString("invoice");
            int customerId = obj.getInt("customerId");
            double discount = obj.optDouble("discount", 0);
            double total = obj.optDouble("total", 0);

            if (dbHelper.invoiceExists(series, invoiceNum)) {
                sendJsonResponse(exchange, 409, "{\"status\":\"error\",\"message\":\"Bu fatura zaten mevcut.\"}");
                return;
            }

            int invoiceId = dbHelper.addInvoice(series, invoiceNum, customerId, discount, total);
            if (invoiceId == -1) {
                sendJsonResponse(exchange, 400, "{\"status\":\"error\",\"message\":\"Fatura eklenemedi.\"}");
                return;
            }

            sendJsonResponse(exchange, 201, "{\"status\":\"success\",\"message\":\"Fatura eklendi.\",\"id\":" + invoiceId + "}");
        } catch (Exception e) {
            sendJsonResponse(exchange, 400, "{\"status\":\"error\",\"message\":\"Geçersiz veri ya da işlem başarısız.\"}");
        }
    }

    private void handlePut(HttpExchange exchange, String idStr) throws IOException {
        try {
            int idToUpdate = Integer.parseInt(idStr);
            String body = readRequestBody(exchange);
            JSONObject obj = new JSONObject(body);

            String series = obj.getString("series");
            String invoiceNum = obj.getString("invoice");
            int customerId = obj.getInt("customerId");
            double discount = obj.optDouble("discount", 0);
            double total = obj.optDouble("total", 0);

            boolean updated = dbHelper.updateInvoice(idToUpdate, series, invoiceNum, customerId, discount, total);

            if (updated) {
                sendJsonResponse(exchange, 200, "{\"status\":\"success\",\"message\":\"Fatura güncellendi.\"}");
            } else {
                sendJsonResponse(exchange, 404, "{\"status\":\"error\",\"message\":\"Fatura bulunamadı veya güncellenemedi.\"}");
            }
        } catch (NumberFormatException e) {
            sendJsonResponse(exchange, 400, "{\"error\":\"Geçersiz ID formatı\"}");
        } catch (Exception e) {
            sendJsonResponse(exchange, 400, "{\"status\":\"error\",\"message\":\"Geçersiz veri ya da işlem başarısız.\"}");
        }
    }

    private void handleDelete(HttpExchange exchange, String idStr) throws IOException {
        try {
            int idToDelete = Integer.parseInt(idStr);
            boolean deleted = dbHelper.deleteInvoiceById(idToDelete);
            if (deleted) {
                sendJsonResponse(exchange, 200, "{\"status\":\"success\",\"message\":\"Fatura silindi.\"}");
            } else {
                sendJsonResponse(exchange, 404, "{\"status\":\"error\",\"message\":\"Fatura bulunamadı.\"}");
            }
        } catch (NumberFormatException e) {
            sendJsonResponse(exchange, 400, "{\"error\":\"Geçersiz ID formatı\"}");
        }
    }

    private JSONObject invoiceToJson(DBHelper.InvoiceSummary invoice) {
        JSONObject obj = new JSONObject();
        obj.put("id", invoice.getId());
        obj.put("series", invoice.getSeries());
        obj.put("invoice", invoice.getInvoiceNum());
        obj.put("customerId", invoice.getCustomerId());
        obj.put("discount", invoice.getDiscount());
        obj.put("total", invoice.getTotal());
        return obj;
    }

    private void sendJsonResponse(HttpExchange exchange, int statusCode, String json) throws IOException {
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.sendResponseHeaders(statusCode, json.getBytes(StandardCharsets.UTF_8).length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(json.getBytes(StandardCharsets.UTF_8));
        }
    }

    private String readRequestBody(HttpExchange exchange) throws IOException {
        try (InputStream is = exchange.getRequestBody()) {
            return new String(is.readAllBytes(), StandardCharsets.UTF_8);
        }
    }
}
