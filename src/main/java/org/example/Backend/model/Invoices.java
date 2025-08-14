package org.example.Backend.model;

public class Invoices {
    private int id;
    private String series;
    private String Invoice; // eskiden "Invoice"
    private int customerId;    // eskiden "customer_id"
    private double discount;
    private double total;

    public Invoices() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getSeries() { return series; }
    public void setSeries(String series) { this.series = series; }

    public String getInvoice() {return Invoice;}
    public void setInvoice(String invoice) {Invoice = invoice;}

    public int getCustomerId() { return customerId; } // getter düzeltilmiş
    public void setCustomerId(int customerId) { this.customerId = customerId; }

    public double getDiscount() { return discount; }
    public void setDiscount(double discount) { this.discount = discount; }

    public double getTotal() { return total; }
    public void setTotal(double total) { this.total = total; }

    public Invoices(int id, String series, String invoiceNum, int customerId, double discount, double total) {
        this.id = id;
        this.series = series;
        this.Invoice = invoiceNum;
        this.customerId = customerId;
        this.discount = discount;
        this.total = total;
    }
}
