package org.example.app;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class CreateInvoice extends JFrame {

    private JTextField txtFaturaSerisi;
    private JTextField txtFaturaNumarasi;
    private JLabel lblSecilenMusteri;

    private JTable tblUrunler;
    private DefaultTableModel tableModel;

    private JTextField txtIndirim;
    private JLabel lblToplamOncesi;
    private JLabel lblToplamSonrasi;

    public CreateInvoice() {
        setTitle("Fatura Oluştur");
        setSize(600, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel ustPanel = new JPanel(new GridLayout(4, 2, 10, 10));

        ustPanel.add(new JLabel("Invoice Series:"));
        txtFaturaSerisi = new JTextField();
        ustPanel.add(txtFaturaSerisi);

        ustPanel.add(new JLabel("Invoice Number:"));
        txtFaturaNumarasi = new JTextField();
        ustPanel.add(txtFaturaNumarasi);

        JButton btnMusteriSec = new JButton("Select Customer");
        ustPanel.add(btnMusteriSec);

        lblSecilenMusteri = new JLabel("Customer not selected");
        ustPanel.add(lblSecilenMusteri);

        JButton btnUrunEkle = new JButton("Add Product");
        ustPanel.add(btnUrunEkle);

        tableModel = new DefaultTableModel();
        tableModel.setColumnIdentifiers(new String[]{"Product Name", "Price", "Quantity", "Total"});
        tblUrunler = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(tblUrunler);

        JPanel altPanel = new JPanel(new GridLayout(2, 3, 10, 10));
        altPanel.add(new JLabel("Discount Amount:"));
        txtIndirim = new JTextField("0");
        altPanel.add(txtIndirim);

        JButton btnHesapla = new JButton("Calculate Invoice");
        altPanel.add(btnHesapla);

        altPanel.add(new JLabel("Total (Before Discount):"));
        lblToplamOncesi = new JLabel("0.00");
        altPanel.add(lblToplamOncesi);

        altPanel.add(new JLabel("Total (After Discount):"));
        lblToplamSonrasi = new JLabel("0.00");
        altPanel.add(lblToplamSonrasi);

        setLayout(new BorderLayout(10, 10));
        add(ustPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(altPanel, BorderLayout.SOUTH);

        btnMusteriSec.addActionListener(e -> {
            SelectCustomer musteriSecFrame = new SelectCustomer(this);
            musteriSecFrame.setVisible(true);
        });

        btnUrunEkle.addActionListener(e -> {
            ProductSelectFrame productSelectFrame = new ProductSelectFrame(this);
            productSelectFrame.setVisible(true);
        });

        btnHesapla.addActionListener(e -> hesaplaToplam());

        setVisible(true);
    }

    public void musteriSecildi(String adSoyad, String tckn) {
        lblSecilenMusteri.setText("Selected Customer: " + adSoyad + " (TCKN: " + tckn + ")");
    }

    public void urunEklendi(String urunAdi, double fiyat, int miktar, double toplam) {
        tableModel.addRow(new Object[]{urunAdi, fiyat, miktar, toplam});
        hesaplaToplam();
    }

    private void hesaplaToplam() {
        double toplamOncesi = 0.0;
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            toplamOncesi += (double) tableModel.getValueAt(i, 3);
        }

        lblToplamOncesi.setText(String.format("%.2f", toplamOncesi));

        double indirim = 0.0;
        try {
            // Replace comma with dot for decimal format if needed
            String indirimText = txtIndirim.getText().trim().replace(',', '.');
            indirim = Double.parseDouble(indirimText);
            if (indirim < 0) {
                JOptionPane.showMessageDialog(this, "Discount cannot be negative.");
                indirim = 0;
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter a valid discount amount.");
            indirim = 0;
        }

        double toplamSonrasi = toplamOncesi - indirim;
        if (toplamSonrasi < 0) toplamSonrasi = 0;

        lblToplamSonrasi.setText(String.format("%.2f", toplamSonrasi));
    }
}
