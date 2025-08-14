package org.example.UI.model;

public class InvoiceItems {
    private int id;
    private int invoiceId;
    private int itemId;
    private int quantity;
    private double total;

    public InvoiceItems() {}

    public InvoiceItems(int id, int invoiceId, int itemId, int quantity, double total) {
        this.id = id;
        this.invoiceId = invoiceId;
        this.itemId = itemId;
        this.quantity = quantity;
        this.total = total;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getInvoiceId() { return invoiceId; }
    public void setInvoiceId(int invoiceId) { this.invoiceId = invoiceId; }
    public int getItemId() { return itemId; }
    public void setItemId(int itemId) { this.itemId = itemId; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public double getTotal() { return total; }
    public void setTotal(double total) { this.total = total; }
}
