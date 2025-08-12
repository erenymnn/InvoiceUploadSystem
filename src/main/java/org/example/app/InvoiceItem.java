package org.example.app;

public class InvoiceItem {
    private int itemId;
    private String itemName;
    private double price;
    private int quantity;
    private double total;

    public InvoiceItem(int itemId, String itemName, double price, int quantity, double total) {
        this.itemId = itemId;
        this.itemName = itemName;
        this.price = price;
        this.quantity = quantity;
        this.total = total;
    }

    public int getItemId() {
        return itemId;
    }

    public String getItemName() {
        return itemName;
    }

    public double getPrice() {
        return price;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getTotal() {
        return total;
    }
}
