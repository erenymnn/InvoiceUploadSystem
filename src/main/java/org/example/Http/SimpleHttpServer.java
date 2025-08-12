package org.example.Http;

import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import org.example.app.DBHelper;

import java.net.InetSocketAddress;

public class SimpleHttpServer {
    public static void main(String[] args) throws Exception {
        // SimpleHttpServer.java içinde:

        try {
            HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
            DBHelper dbHelper = new DBHelper();
            server.createContext("/invoices", new InvoiceHandler(dbHelper));
            server.createContext("/customers", new CustomerHandler(dbHelper));
            server.createContext("/invoiceitems",new InvoiceitemsHandler(dbHelper));
            server.createContext("/items",new itemsHandler(dbHelper));

            // Diğer endpointler

            server.setExecutor(null);
            server.start();
            System.out.println("Sunucu 8080 portunda calisiyor...");
        }catch(Exception e) {
            e.printStackTrace();
        }
    }

}
