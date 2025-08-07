package org.example.app;

import javax.swing.*;
import java.awt.*;

public class ProductSelectFrame extends JFrame {
    private JList<String> productList;
    private JTextField txtMiktar;
    private CreateInvoice parent;

    public ProductSelectFrame(CreateInvoice parent) {
        this.parent = parent;

        setTitle("Ürün Seçimi");
        setSize(350, 250);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        String[] products = {"Kalem -4.5tl", "Silgi -5.0tl", "Çanta -20.0tl"};

        productList = new JList<>(products);
        JScrollPane scrollPane = new JScrollPane(productList);

        JLabel lblMiktar = new JLabel("Miktar:");
        txtMiktar = new JTextField("1", 5);

        JButton btnEkle = new JButton("Ekle");

        JPanel bottomPanel = new JPanel();
        bottomPanel.add(lblMiktar);
        bottomPanel.add(txtMiktar);
        bottomPanel.add(btnEkle);

        setLayout(new BorderLayout());
        add(scrollPane, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        btnEkle.addActionListener(e -> {
            String secilenUrun = productList.getSelectedValue();
            String miktarStr = txtMiktar.getText().trim();

            if (secilenUrun == null) {
                JOptionPane.showMessageDialog(this, "Lütfen bir ürün seçiniz.");
                return;
            }
            if (miktarStr.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Lütfen miktar giriniz.");
                return;
            }

            int miktar;
            try {
                miktar = Integer.parseInt(miktarStr);
                if (miktar <= 0) {
                    JOptionPane.showMessageDialog(this, "Miktar pozitif bir sayı olmalıdır.");
                    return;
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Geçerli bir sayı giriniz.");
                return;
            }

            // Separate product name and price
            String[] parts = secilenUrun.split(" -");
            String urunAdi = parts[0];
            double fiyat = Double.parseDouble(parts[1].replace("tl", "").trim());

            double toplam = fiyat * miktar;

            // Send product to main window
            parent.urunEklendi(urunAdi, fiyat, miktar, toplam);

            // close window
            dispose();
        });

        setVisible(true);
    }
}
