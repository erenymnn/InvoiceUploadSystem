package org.example.Http;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class InvoiceStorage {

    // Basit fatura modeli
    public static class Invoice {
        public int id;
        public String series;
        public String invoice;

        public Invoice(int id, String series, String invoice) {
            this.id = id;
            this.series = series;
            this.invoice = invoice;
        }

        public JSONObject toJson() {
            JSONObject obj = new JSONObject();
            obj.put("id", id);
            obj.put("series", series);
            obj.put("invoice", invoice);
            return obj;
        }
    }

    private static List<Invoice> invoiceList = new ArrayList<>();
    private static int nextId = 1;

    static {
        // Başlangıçta örnek fatura
        invoiceList.add(new Invoice(nextId++, "A", "123"));
    }

    public static String getInvoicesJson() {
        JSONArray arr = new JSONArray();
        for (Invoice inv : invoiceList) {
            arr.put(inv.toJson());
        }
        return arr.toString(4); // 4 boşlukla formatlı
    }

    public static boolean addInvoiceFromJson(String json) {
        try {
            JSONObject obj = new JSONObject(json);

            // Gerekli alanlar var mı kontrol et
            if (!obj.has("series") || !obj.has("invoice")) {
                return false;
            }

            String series = obj.getString("series");
            String invoice = obj.getString("invoice");

            // Yeni fatura oluştur ve listeye ekle
            invoiceList.add(new Invoice(nextId++, series, invoice));
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    public static boolean deleteInvoiceById(int id) {
        return invoiceList.removeIf(inv -> inv.id == id);
    }

}
