package org.example.app;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainMenu extends JFrame {
    public MainMenu() {
        setTitle("Fatura Yükleme Sistemi - Ana Menü");
        setSize(400, 250);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // ekranın ortasında açılır

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(3, 2, 10, 10)); // 3 satır, 2 sütun, aralar 10px

        JButton btnFaturaOlustur = new JButton("Fatura Oluştur");
        JButton btnFaturaXML = new JButton("XML İşlemleri");
        JButton btnFaturaJSON = new JButton("JSON İşlemleri");
        JButton btnFaturaSil = new JButton("Fatura Sil");
        JButton btnCikis = new JButton("Çıkış");

        // Fatura Oluşturma butonu farklı pencere açacak
        btnFaturaOlustur.addActionListener(e -> {
            CreateInvoice createInvoiceForm = new CreateInvoice();
            createInvoiceForm.setVisible(true);
        });

        // XML İşlemleri butonu farklı pencere açacak
        btnFaturaXML.addActionListener(e -> {
            invoiceXMLForm xmlForm = new invoiceXMLForm();
            xmlForm.setVisible(true);
        });

        // JSON İşlemleri butonu farklı pencere açacak
        btnFaturaJSON.addActionListener(e -> {
            invoiceJSONForm jsonForm = new invoiceJSONForm();
            jsonForm.setVisible(true);
        });

        // Fatura Silme işlemi farklı pencere açacak
        btnFaturaSil.addActionListener(e -> {
            invoiceDeleteForm deleteForm = new invoiceDeleteForm();
            deleteForm.setVisible(true);
        });

        btnCikis.addActionListener(e -> System.exit(0)); // programı kapatmaya yarar.

        panel.add(btnFaturaOlustur);
        panel.add(btnFaturaXML);
        panel.add(btnFaturaJSON);
        panel.add(btnFaturaSil);
        panel.add(btnCikis);

        add(panel);

        setVisible(true);
    }
}
