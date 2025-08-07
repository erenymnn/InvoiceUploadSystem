package org.example.app;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainMenu extends JFrame {
    public MainMenu() {
        setTitle("Fatura Yükleme Sistemi - Ana Menü");
        setSize(400,200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // ekranın ortasında açılır

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(2, 2,10,10));

        JButton btnFaturaOlustur=new JButton("Fatura Olustur");
        btnFaturaOlustur.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
               new CreateInvoice();
            }
        });
        JButton btnFaturaXML=new JButton("XML İşlemleri");
        JButton btnFaturaJSON=new JButton("JSON İşlemleri");
        JButton btnFaturaSil=new JButton("Fatura Sil");
        JButton btnCikis=new JButton("Cikis");

        panel.add(btnFaturaOlustur);
        panel.add(btnFaturaJSON);
        panel.add(btnFaturaXML);
        panel.add(btnFaturaSil);
        panel.add(btnCikis);

        btnFaturaOlustur.addActionListener(e ->{


        });


        btnCikis.addActionListener(e -> System.exit(0)); // programı kapatmaya yarar.


        add(panel);

        setVisible(true);//oolusturdgumuz pencere gorunur olmasını saglıyor pencere ekranda açmak için zorunlu cagrı



    }
}
