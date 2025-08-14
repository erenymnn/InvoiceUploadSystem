package org.example.Backend.controller;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.example.Backend.converter.CustomersConverter;
import org.example.Backend.model.Customers;
import org.example.Backend.service.CustomersService;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class CustomersController implements HttpHandler {
    private final CustomersService customersService;

    public CustomersController(CustomersService customersService) {
        this.customersService = customersService;
    }

    @Override
    public void handle(HttpExchange exchange) {
        try {
            String method = exchange.getRequestMethod();
            String path = exchange.getRequestURI().getPath();
            String[] parts = path.split("/");

            switch(method.toUpperCase()) {
                case "GET":
                    if(parts.length == 2) {
                        List<Customers> list = customersService.getAllCustomers();
                        JSONArray arr = new JSONArray();
                        list.forEach(c -> arr.put(CustomersConverter.toJson(c)));
                        sendResponse(exchange, 200, arr.toString(4));
                    } else if(parts.length == 3) {
                        int id = Integer.parseInt(parts[2]);
                        Customers c = customersService.getCustomerById(id);
                        if(c != null) sendResponse(exchange, 200, CustomersConverter.toJson(c).toString());
                        else sendResponse(exchange, 404, "{\"error\":\"Müşteri bulunamadı\"}");
                    }
                    break;

                case "POST":
                    String body = readBody(exchange);
                    JSONObject obj = new JSONObject(body);
                    Customers newCustomer = CustomersConverter.fromJson(obj);
                    boolean success = customersService.addCustomer(
                            newCustomer.getName(),
                            newCustomer.getSurname(),
                            newCustomer.getTckn()
                    );
                    if(success) sendResponse(exchange, 201, "{\"status\":\"success\",\"message\":\"Müşteri eklendi\"}");
                    else sendResponse(exchange, 400, "{\"status\":\"error\",\"message\":\"Müşteri eklenemedi\"}");
                    break;

                case "PUT":
                    if(parts.length != 3) { sendResponse(exchange, 400, "{\"error\":\"ID eksik\"}"); return; }
                    int idPut = Integer.parseInt(parts[2]);
                    JSONObject objPut = new JSONObject(readBody(exchange));
                    boolean updated = customersService.updateCustomer(
                            idPut,
                            objPut.getString("name"),
                            objPut.getString("surname"),
                            objPut.getString("tckn")
                    );
                    if(updated) sendResponse(exchange, 200, "{\"status\":\"success\",\"message\":\"Müşteri güncellendi\"}");
                    else sendResponse(exchange, 404, "{\"status\":\"error\",\"message\":\"Müşteri bulunamadı\"}");
                    break;

                case "DELETE":
                    if(parts.length != 3) { sendResponse(exchange, 400, "{\"error\":\"ID eksik\"}"); return; }
                    int idDel = Integer.parseInt(parts[2]);
                    boolean deleted = customersService.deleteCustomer(idDel);
                    if(deleted) sendResponse(exchange, 200, "{\"status\":\"success\",\"message\":\"Müşteri silindi\"}");
                    else sendResponse(exchange, 404, "{\"error\":\"Müşteri bulunamadı\"}");
                    break;

                default:
                    sendResponse(exchange, 405, "{\"error\":\"Metod desteklenmiyor\"}");
            }

        } catch(Exception e) {
            e.printStackTrace();
            try { sendResponse(exchange, 500, "{\"error\":\"Sunucu hatası\"}"); } catch(Exception ignored) {}
        }
    }

    private String readBody(HttpExchange exchange) throws Exception {
        try(InputStream is = exchange.getRequestBody()) {
            return new String(is.readAllBytes(), StandardCharsets.UTF_8);
        }
    }

    private void sendResponse(HttpExchange exchange, int code, String resp) throws Exception {
        byte[] bytes = resp.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=utf-8");
        exchange.sendResponseHeaders(code, bytes.length);
        try(OutputStream os = exchange.getResponseBody()) { os.write(bytes); }
    }
}
