package org.example.app;

import javax.swing.*;
import java.awt.*;

public class CustomerUpdate extends JFrame {

    private JTextField txtname;
    private JTextField txtsurname;
    private JTextField txttckn;
    private int musteriId;

    public CustomerUpdate(int id, String name, String surname, String tckn) {
        this.musteriId = id;

        setTitle("Update Customer");
        setSize(400, 250);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel panel = new JPanel(new GridLayout(4, 2, 10, 10));

        panel.add(new JLabel("Name:"));
        txtname = new JTextField(name);
        panel.add(txtname);

        panel.add(new JLabel("Surname:"));
        txtsurname = new JTextField(surname);
        panel.add(txtsurname);

        panel.add(new JLabel("TCKN:"));
        txttckn = new JTextField(tckn);
        panel.add(txttckn);

        JButton btnGuncelle = new JButton("Update");
        panel.add(btnGuncelle);

        add(panel);

        btnGuncelle.addActionListener(e -> {
            String yeniAd = txtname.getText().trim();
            String yeniSoyad = txtsurname.getText().trim();
            String yeniTckn = txttckn.getText().trim();

            DBHelper db = new DBHelper();
            boolean updated = db.updateMusteri(musteriId, yeniAd, yeniSoyad, yeniTckn);

            if (updated) {
                JOptionPane.showMessageDialog(this, "Customer updated successfully.");
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Update failed.");
            }
        });

        setVisible(true);
    }
}
