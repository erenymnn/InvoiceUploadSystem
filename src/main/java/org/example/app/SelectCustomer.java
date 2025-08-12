package org.example.app;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class SelectCustomer extends JFrame {

    private CreateInvoice parent;
    private DefaultListModel<String> musteriListModel;
    private JList<String> musteriList;
    private List<DBHelper.Customer> musteriler;
    private DBHelper db;

    public SelectCustomer(CreateInvoice parent) {
        this.parent = parent;

        setTitle("Müşteri Seç");
        setSize(400, 300);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        db = new DBHelper();
        musteriler = db.getAllCustomers();

        musteriListModel = new DefaultListModel<>();
        for (DBHelper.Customer m : musteriler) {
            musteriListModel.addElement(m.getName() + " " + m.getSurname() + " - " + m.getTckn());
        }

        musteriList = new JList<>(musteriListModel);
        JScrollPane scrollPane = new JScrollPane(musteriList);

        JButton btnYeniMusteri = new JButton("Yeni Müşteri Ekle");
        JButton btnSec = new JButton("Seç");
        JButton btnMusteriUpdate = new JButton("Müşteri Güncelle");
        JPanel panelButtons = new JPanel();
        panelButtons.add(btnYeniMusteri);
        panelButtons.add(btnMusteriUpdate);
        panelButtons.add(btnSec);

        add(scrollPane, BorderLayout.CENTER);
        add(panelButtons, BorderLayout.SOUTH);

        btnYeniMusteri.addActionListener(e -> yeniMusteriEkle());

        btnSec.addActionListener(e -> {
            int selectedIndex = musteriList.getSelectedIndex();
            if (selectedIndex != -1) {
                DBHelper.Customer secilen = musteriler.get(selectedIndex);
                parent.musteriSecildi(secilen.getId(), secilen.getName() + " " + secilen.getSurname(), secilen.getTckn());
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Lütfen bir müşteri seçin.");
            }
        });

        btnMusteriUpdate.addActionListener(e -> {
            int seciliIndex = musteriList.getSelectedIndex();
            if (seciliIndex == -1) {
                JOptionPane.showMessageDialog(this, "Lütfen güncellenecek müşteriyi seçin.");
                return;
            }

            DBHelper.Customer secilen = musteriler.get(seciliIndex);

            CustomerUpdate guncelleFrame = new CustomerUpdate(
                    secilen.getId(),
                    secilen.getName(),
                    secilen.getSurname(),
                    secilen.getTckn());

            guncelleFrame.setVisible(true);
        });
    }

    private void yeniMusteriEkle() {
        String idStr = JOptionPane.showInputDialog(this, "Müşteri ID (benzersiz tam sayı):");
        if (idStr == null || idStr.trim().isEmpty()) return;

        int id;
        try {
            id = Integer.parseInt(idStr.trim());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Geçersiz ID! Lütfen tam sayı girin.");
            return;
        }

        String adSoyad = JOptionPane.showInputDialog(this, "Müşteri Ad-Soyad:");
        if (adSoyad == null || adSoyad.trim().isEmpty()) return;

        String tckn = JOptionPane.showInputDialog(this, "Müşteri TCKN (11 haneli rakam):");
        if (tckn == null || tckn.trim().isEmpty()) return;

        if (!tckn.matches("\\d{11}")) {
            JOptionPane.showMessageDialog(this, "Geçersiz TCKN! Lütfen 11 haneli rakam girin.");
            return;
        }

        String[] parts = adSoyad.trim().split(" ", 2);
        String ad = parts.length > 0 ? parts[0] : "";
        String soyad = parts.length > 1 ? parts[1] : "";

        boolean eklendi = db.musteriEkle(id, ad, soyad, tckn);

        if (eklendi) {
            musteriler = db.getAllCustomers();
            musteriListModel.clear();
            for (DBHelper.Customer m : musteriler) {
                musteriListModel.addElement(m.getName() + " " + m.getSurname() + " - " + m.getTckn());
            }
            JOptionPane.showMessageDialog(this, "Yeni müşteri başarıyla eklendi.");
        } else {
            JOptionPane.showMessageDialog(this, "Müşteri eklenemedi. ID benzersiz mi kontrol edin.");
        }
    }


}
