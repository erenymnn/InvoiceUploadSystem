package org.example.Backend.controller;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.example.converter.InvoiceItemsConverter;
import org.example.model.InvoiceItems;
import org.example.service.InvoiceItemsService;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class InvoiceItemsController implements HttpHandler {
    private final InvoiceItemsService service;
    public InvoiceItemsController(InvoiceItemsService service) { this.service = service; }

    @Override
    public void handle(HttpExchange exchange) {
        try {
            String method = exchange.getRequestMethod();
            String path = exchange.getRequestURI().getPath();
            String[] parts = path.split("/");

            switch (method.toUpperCase()) {
                case "GET":
                    if (parts.length == 2) {
                        List<InvoiceItems> list = service.getAllInvoiceItems();
                        JSONArray arr = new JSONArray();
                        for (InvoiceItems i : list) arr.put(InvoiceItemsConverter.toJson(i));
                        send(exchange, 200, arr.toString(4));
                    } else if (parts.length == 3) {
                        int invoiceId = Integer.parseInt(parts[2]);
                        List<InvoiceItems> items = service.getInvoiceItemsByInvoiceId(invoiceId);
                        JSONArray arr = new JSONArray();
                        for (InvoiceItems i : items) arr.put(InvoiceItemsConverter.toJson(i));
                        send(exchange, 200, arr.toString(4));
                    } else send(exchange, 400, "{\"error\":\"Invalid GET\"}");
                    break;

                case "POST":
                    JSONObject postObj = new JSONObject(readBody(exchange));
                    InvoiceItems newItem = InvoiceItemsConverter.fromJson(postObj);
                    boolean added = service.addInvoiceItem(newItem);
                    if (added) send(exchange, 201, InvoiceItemsConverter.toJson(newItem).toString());
                    else send(exchange, 400, "{\"error\":\"Cannot add item\"}");
                    break;

                case "PUT":
                    if (parts.length != 3) { send(exchange, 400, "{\"error\":\"ID missing\"}"); return; }
                    int idPut = Integer.parseInt(parts[2]);
                    JSONObject putObj = new JSONObject(readBody(exchange));
                    InvoiceItems updItem = InvoiceItemsConverter.fromJson(putObj);
                    updItem.setId(idPut);
                    boolean updated = service.updateInvoiceItem(updItem);
                    if (updated) send(exchange, 200, InvoiceItemsConverter.toJson(updItem).toString());
                    else send(exchange, 404, "{\"error\":\"Item not found\"}");
                    break;

                case "DELETE":
                    if (parts.length != 3) { send(exchange, 400, "{\"error\":\"ID missing\"}"); return; }
                    int idDel = Integer.parseInt(parts[2]);
                    boolean deleted = service.deleteInvoiceItem(idDel);
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
