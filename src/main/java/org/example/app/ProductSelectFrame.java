package org.example.app;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class ProductSelectFrame extends JFrame {

    private CreateInvoice parent;
    private DefaultListModel<String> model;
    private JList<String> productList;
    private List<DBHelper.Product> products;

    public ProductSelectFrame(CreateInvoice parent) {
        this.parent = parent;
        setTitle("Ürün Seç");
        setSize(400, 300);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        model = new DefaultListModel<>();
        productList = new JList<>(model);
        JScrollPane scrollPane = new JScrollPane(productList);

        JButton btnSelect = new JButton("Seç");
        JPanel panelButtons = new JPanel();
        panelButtons.add(btnSelect);

        add(scrollPane, BorderLayout.CENTER);
        add(panelButtons, BorderLayout.SOUTH);

        // Fetch products from database
        DBHelper db = new DBHelper();
        products = db.getAllProducts();

        // Fetch products from database
        for (DBHelper.Product p : products) {
            model.addElement(p.getName() + " - " + String.format("%.2f", p.getPrice()) + " TL");
        }

        btnSelect.addActionListener(e -> {
            int selectedIndex = productList.getSelectedIndex();
            if (selectedIndex != -1) {
                DBHelper.Product selectedProduct = products.get(selectedIndex);
                int miktar = 1;
                double toplam = selectedProduct.getPrice() * miktar;

                parent.urunEklendi(selectedProduct.getId(), selectedProduct.getName(), selectedProduct.getPrice(), miktar, toplam);

                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Lütfen bir ürün seçin.");
            }
        });
    }
}
