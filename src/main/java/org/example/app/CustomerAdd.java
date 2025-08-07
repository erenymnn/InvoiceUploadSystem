package org.example.app;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

public class CustomerAdd extends JFrame {

    private JTextField txtname;
    private JTextField txtsurname;
    private JTextField txtTCKN;


    public CustomerAdd() {
        setTitle("Yeni Müşteri Ekle");
        setSize(300, 200);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel panel = new JPanel(new GridLayout(4, 2, 10, 10));

        panel.add(new JLabel("name:"));
        txtname = new JTextField();
        panel.add(txtname); // ekrana yazmak için .

        panel.add(new JLabel("surname:"));
        txtsurname = new JTextField();
        panel.add(txtsurname);

        panel.add(new JLabel("TCKN:"));
        txtTCKN = new JTextField();
        panel.add(txtTCKN);


        JButton btnKaydet = new JButton("Kaydet");
        panel.add(btnKaydet); //ing

        add(panel);

        btnKaydet.addActionListener(e -> { // Things that will pop up when I press the save button // yorumlar ingilizce
            String name = txtname.getText().trim(); //trim removes leading and trailing spaces.
            String surname = txtsurname.getText().trim();
            String tckn = txtTCKN.getText().trim();

            if (name.isEmpty() || surname.isEmpty() || tckn.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Lütfen tüm alanları doldurun!");
            } else {



                // I haven't written the database part yet.
                try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/upload_system")) {
                    String sql = "INSERT INTO customers (name, surname, tckn) VALUES (?, ?, ?)" ; //I save it to the database with insert into question marks placeholder
                    PreparedStatement pstmt = conn.prepareStatement(sql); // securely adds data..
                    pstmt.setString(1, name );
                    pstmt.setString(2, surname);
                    pstmt.setString(3, tckn);


                    int affectedRows = pstmt.executeUpdate(); //running query. freezes how many rows are affected. 0 fails, 1 succeeds
                    if (affectedRows > 0) {
                        JOptionPane.showMessageDialog(this, "Müşteri başarıyla kaydedildi.");
                        this.dispose();
                    } else {
                        JOptionPane.showMessageDialog(this, "Müşteri kaydedilirken hata oluştu.");
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Hata: " + ex.getMessage());
                }
            }
        });
    }
}
