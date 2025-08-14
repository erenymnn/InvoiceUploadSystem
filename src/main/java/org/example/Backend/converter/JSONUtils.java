package org.example.Backend.converter;

import org.example.Backend.model.Customers;
import org.example.Backend.model.InvoiceItems;
import org.example.Backend.model.Invoices;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class JSONUtils {

    public static void generateInvoiceJSON(Invoices invoice, Customers customer, List<InvoiceItems> items, String filePath) throws IOException {
        JSONObject root = new JSONObject();

        // Customer
        JSONObject customerJson = new JSONObject();
        customerJson.put("id", customer.getId());
        customerJson.put("name", customer.getName());
        customerJson.put("surname", customer.getSurname());
        customerJson.put("tckn", customer.getTckn());
        root.put("customer", customerJson);

        // Invoice
        JSONObject invoiceJson = new JSONObject();
        invoiceJson.put("id", invoice.getId());
        invoiceJson.put("series", invoice.getSeries());
        invoiceJson.put("invoiceNum", invoice.getInvoice());
        invoiceJson.put("discount", invoice.getDiscount());
        invoiceJson.put("total", invoice.getTotal());
        root.put("invoice", invoiceJson);

        // Items
        JSONArray itemsArray = new JSONArray();
        for (InvoiceItems item : items) {
            JSONObject itemJson = new JSONObject();
            itemJson.put("itemId", item.getItemId());
            itemJson.put("quantity", item.getQuantity());
            itemJson.put("total", item.getTotal());
            itemsArray.put(itemJson);
        }
        root.put("items", itemsArray);

        // Write to file
        try (FileWriter writer = new FileWriter(filePath)) {
            writer.write(root.toString(4));
        }
    }
}
