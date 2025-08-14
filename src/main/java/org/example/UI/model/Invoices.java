package org.example.UI.model;

public class Invoices {
    private int id;
    private String series;
    private String Invoice;
    private int customer_id;
    private double discount;
    private double total;

    public Invoices() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSeries() {
        return series;
    }

    public void setSeries(String series) {
        this.series = series;
    }

    public String getInvoice() {
        return Invoice;
    }

    public void setInvoice(String invoice) {
        Invoice = invoice;
    }

    public int getCustomer_id() {
        return customer_id;
    }

    public void setCustomer_id(int customer_id) {
        this.customer_id = customer_id;
    }

    public double getDiscount() {
        return discount;
    }

    public void setDiscount(double discount) {
        this.discount = discount;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public Invoices(int id, String series, String invoiceNum, int customerId, double discount, double total) {
        this.id = id;
        this.series = series;
        this.Invoice = Invoice;
        this.customer_id = customer_id;
        this.discount = discount;
        this.total = total;

    }
}
