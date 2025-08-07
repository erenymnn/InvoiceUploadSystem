package org.example.app;

import javax.swing.*;
import java.awt.*;

public class CustomerUpdate extends JFrame {

    private JTextField txtname;
    private JTextField txtsurname;
    private JTextField txtTckn;
    private int musteriId;

    public CustomerUpdate(int id, String name, String surname, String tckn) {
        this.musteriId = id;

        setTitle("Müşteri Güncelle");
        setSize(400, 250);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel panel = new JPanel(new GridLayout(5, 2, 10, 10));

        panel.add(new JLabel("Ad:"));
        txtname  = new JTextField(name);
        panel.add(txtname);

        panel.add(new JLabel("Soyad:"));
        txtsurname = new JTextField(surname);
        panel.add(txtsurname);

        panel.add(new JLabel("TCKN:"));
        txtTckn = new JTextField(tckn);
        txtTckn.setDocument(new LimitliDocument(5));
        panel.add(txtTckn);

        JButton btnGuncelle = new JButton("Güncelle");
        panel.add(btnGuncelle);

        add(panel);

        btnGuncelle.addActionListener(e -> {
            String yeniAd = txtname.getText().trim();
            String yeniSoyad = txtsurname.getText().trim();
            String yeniTckn = txtTckn.getText().trim();

            if (yeniAd.isEmpty() || yeniSoyad.isEmpty() || yeniTckn.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Lütfen tüm alanları doldurun!");
                return;
            }

            DBHelper db = new DBHelper();
            boolean updated = db.updateMusteri(musteriId, yeniAd, yeniSoyad, yeniTckn);

            if (updated) {
                JOptionPane.showMessageDialog(this, "Müşteri başarıyla güncellendi.");
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Güncelleme başarısız oldu.");
            }
        });

        setVisible(true);
    }
}
