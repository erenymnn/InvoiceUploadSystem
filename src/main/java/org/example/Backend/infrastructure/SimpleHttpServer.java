package org.example.Backend.infrastructure;

import com.sun.net.httpserver.HttpServer;
import org.example.controller.CustomersController;
import org.example.controller.InvoiceItemsController;
import org.example.controller.InvoicesController;
import org.example.controller.ItemsController;
import org.example.repository.CustomersRepository;
import org.example.repository.InvoiceItemsRepository;
import org.example.repository.InvoicesRepository;
import org.example.repository.ItemsRepository;
import org.example.service.CustomersService;
import org.example.service.InvoiceItemsService;
import org.example.service.InvoiceService;
import org.example.service.ItemsService;

import java.net.InetSocketAddress;
import java.sql.Connection;

public class SimpleHttpServer {
    public static void main(String[] args) {
        try {
            // 1. Veritabanı bağlantısını oluştur (uygulama boyunca açık)
            Connection conn = DatabaseConnection.getConnection();

            // 2. Repository oluştur
            CustomersRepository customerRepo = new CustomersRepository(conn);
            InvoicesRepository invoiceRepo = new InvoicesRepository(conn);
            InvoiceItemsRepository invoiceItemsRepo = new InvoiceItemsRepository(conn);
            ItemsRepository itemsRepo = new ItemsRepository(conn);

            // 3. Service oluştur
            CustomersService customersService = new CustomersService(customerRepo);
            InvoiceService invoiceService = new InvoiceService(invoiceRepo);
            InvoiceItemsService invoiceItemsService = new InvoiceItemsService(invoiceItemsRepo);
            ItemsService itemsService = new ItemsService(itemsRepo);

            // 4. Controller’ları bağla
            HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
            server.createContext("/customers", new CustomersController(customersService));
            server.createContext("/invoices", new InvoicesController(invoiceService));
            server.createContext("/invoiceitems", new InvoiceItemsController(invoiceItemsService));
            server.createContext("/items", new ItemsController(itemsService));

            server.setExecutor(null); // default executor
            server.start();

            System.out.println("✅ Sunucu 8080 portunda çalışıyor...");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
