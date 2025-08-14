package org.example.Backend.HttpServer;

import com.sun.net.httpserver.HttpServer;
import org.example.Backend.DBHelper.DatabaseConnection;
import org.example.Backend.controller.CustomersController;
import org.example.Backend.controller.InvoiceItemsController;
import org.example.Backend.controller.InvoicesController;
import org.example.Backend.controller.ItemsController;
import org.example.Backend.repository.CustomersRepository;
import org.example.Backend.repository.InvoiceItemsRepository;
import org.example.Backend.repository.InvoicesRepository;
import org.example.Backend.repository.ItemsRepository;
import org.example.Backend.service.CustomersService;
import org.example.Backend.service.InvoiceItemsService;
import org.example.Backend.service.InvoiceService;
import org.example.Backend.service.ItemsService;

import java.net.InetSocketAddress;
import java.sql.Connection;

public class SimpleHttpServer {
    public static void main(String[] args) {
        try {
            // 1. Creates database files (open throughout the application)
            Connection conn = DatabaseConnection.getConnection();

            // 2. Create Repository
            CustomersRepository customerRepo = new CustomersRepository(conn);
            InvoicesRepository invoiceRepo = new InvoicesRepository(conn);
            InvoiceItemsRepository invoiceItemsRepo = new InvoiceItemsRepository(conn);
            ItemsRepository itemsRepo = new ItemsRepository(conn);

            // 3.  create service
            CustomersService customersService = new CustomersService(customerRepo);
            InvoiceService invoiceService = new InvoiceService(invoiceRepo);
            InvoiceItemsService invoiceItemsService = new InvoiceItemsService(invoiceItemsRepo);
            ItemsService itemsService = new ItemsService(itemsRepo);

            // 4. Connect controllers
            HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
            server.createContext("/customers", new CustomersController(customersService));
            server.createContext("/invoices", new InvoicesController(invoiceService));
            server.createContext("/invoiceitems", new InvoiceItemsController(invoiceItemsService));
            server.createContext("/items", new ItemsController(itemsService));

            server.setExecutor(null); // default executor
            server.start();

            System.out.println("✅ Sunucu 8080 portunda calisiyor...");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
