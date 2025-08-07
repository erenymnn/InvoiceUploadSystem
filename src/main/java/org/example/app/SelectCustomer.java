package org.example.app;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class SelectCustomer extends JFrame {

    private CreateInvoice parent;
    private DefaultListModel<String> musteriListModel;
    private JList<String> musteriList;
    private List<Customer> musteriler;

    public SelectCustomer(CreateInvoice parent) {
        this.parent = parent;

        setTitle("Select Customer");
        setSize(400, 300);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        musteriListModel = new DefaultListModel<>();
        musteriList = new JList<>(musteriListModel);
        JScrollPane scrollPane = new JScrollPane(musteriList);

        JButton btnYeniMusteri = new JButton("Add New Customer");
        JButton btnSec = new JButton("Select");
        JButton btnMusteriUpdate = new JButton("Update Customer");

        JPanel panelButtons = new JPanel();
        panelButtons.add(btnYeniMusteri);
        panelButtons.add(btnMusteriUpdate);
        panelButtons.add(btnSec);

        add(scrollPane, BorderLayout.CENTER);
        add(panelButtons, BorderLayout.SOUTH);

        loadCustomersFromDB();

        btnYeniMusteri.addActionListener(e -> yeniMusteriEkle());

        btnSec.addActionListener(e -> {
            int selectedIndex = musteriList.getSelectedIndex();
            if (selectedIndex != -1) {
                Customer secilen = musteriler.get(selectedIndex);
                parent.musteriSecildi(secilen.getName() + " " + secilen.getSurname(), secilen.getTckn());
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Please select a customer.");
            }
        });

        btnMusteriUpdate.addActionListener(e -> {
            int seciliIndex = musteriList.getSelectedIndex();
            if (seciliIndex == -1) {
                JOptionPane.showMessageDialog(this, "Please select a customer to update.");
                return;
            }

            Customer secilen = musteriler.get(seciliIndex);

            CustomerUpdate updateFrame = new CustomerUpdate(
                    secilen.getId(),
                    secilen.getName(),
                    secilen.getSurname(),
                    secilen.getTckn());

            updateFrame.setVisible(true);
        });
    }

    private void loadCustomersFromDB() {
        DBHelper db = new DBHelper();
        musteriler = db.getAllCustomers();

        musteriListModel.clear();
        for (Customer m : musteriler) {
            musteriListModel.addElement(m.getName() + " " + m.getSurname() + " - " + m.getTckn());
        }
    }

    private void yeniMusteriEkle() {
        String adSoyad = JOptionPane.showInputDialog(this, "Customer Full Name:");
        if (adSoyad == null || adSoyad.trim().isEmpty()) return;

        String tckn = JOptionPane.showInputDialog(this, "Customer TCKN:");
        if (tckn == null || tckn.trim().isEmpty()) return;

        String[] parts = adSoyad.trim().split(" ", 2);
        String ad = parts.length > 0 ? parts[0] : "";
        String soyad = parts.length > 1 ? parts[1] : "";

        DBHelper db = new DBHelper();
        boolean added = db.musteriEkle(ad, soyad, tckn);

        if (added) {
            loadCustomersFromDB();
            JOptionPane.showMessageDialog(this, "New customer added successfully.");
        } else {
            JOptionPane.showMessageDialog(this, "Failed to add customer (DB error).");
        }
    }
}
