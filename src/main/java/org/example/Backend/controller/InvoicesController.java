package org.example.Backend.controller;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.example.Backend.converter.InvoicesConverter;
import org.example.Backend.model.Invoices;
import org.example.Backend.service.InvoiceService;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class InvoicesController implements HttpHandler {
    private final InvoiceService service;

    public InvoicesController(InvoiceService service) { this.service = service; }

    @Override
    public void handle(HttpExchange exchange) {
        try {
            String method = exchange.getRequestMethod();
            String path = exchange.getRequestURI().getPath();
            String[] parts = path.split("/");

            switch (method.toUpperCase()) {
                case "GET":
                    if (parts.length == 2) {
                        List<Invoices> list = service.getAllInvoices();
                        JSONArray arr = new JSONArray();
                        for (Invoices i : list) arr.put(InvoicesConverter.toJson(i));
                        send(exchange, 200, arr.toString(4));
                    } else if (parts.length == 3) {
                        int id = Integer.parseInt(parts[2]);
                        Invoices inv = service.getInvoiceById(id);
                        if (inv != null) send(exchange, 200, InvoicesConverter.toJson(inv).toString());
                        else send(exchange, 404, "{\"error\":\"Invoice not found\"}");
                    } else send(exchange, 400, "{\"error\":\"Invalid GET\"}");
                    break;

                case "POST":
                    JSONObject postObj = new JSONObject(readBody(exchange));
                    Invoices newInv = InvoicesConverter.fromJson(postObj);
                    boolean added = service.addInvoice(newInv);
                    if (added) send(exchange, 201, InvoicesConverter.toJson(newInv).toString());
                    else send(exchange, 400, "{\"error\":\"Cannot add invoice\"}");
                    break;

                case "PUT":
                    if (parts.length != 3) { send(exchange, 400, "{\"error\":\"ID missing\"}"); return; }
                    int idPut = Integer.parseInt(parts[2]);
                    JSONObject putObj = new JSONObject(readBody(exchange));
                    Invoices updInv = InvoicesConverter.fromJson(putObj);
                    updInv.setId(idPut);
                    boolean updated = service.updateInvoice(updInv);
                    if (updated) send(exchange, 200, InvoicesConverter.toJson(updInv).toString());
                    else send(exchange, 404, "{\"error\":\"Invoice not found\"}");
                    break;

                case "DELETE":
                    if (parts.length != 3) { send(exchange, 400, "{\"error\":\"ID missing\"}"); return; }
                    int idDel = Integer.parseInt(parts[2]);
                    boolean deleted = service.deleteInvoice(idDel);
                    if (deleted) send(exchange, 200, "{\"status\":\"deleted\"}");
                    else send(exchange, 404, "{\"error\":\"Invoice not found\"}");
                    break;

                default: send(exchange, 405, "{\"error\":\"Method not allowed\"}");
            }
        } catch (Exception e) {
            e.printStackTrace();
            try { send(exchange, 500, "{\"error\":\"Server error\"}"); } catch (Exception ignored) {}
        }
    }

    private String readBody(HttpExchange exchange) throws Exception {
        try (InputStream is = exchange.getRequestBody()) {
            return new String(is.readAllBytes(), StandardCharsets.UTF_8);
        }
    }

    private void send(HttpExchange exchange, int code, String resp) throws Exception {
        byte[] bytes = resp.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=utf-8");
        exchange.sendResponseHeaders(code, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) { os.write(bytes); }
    }
}
