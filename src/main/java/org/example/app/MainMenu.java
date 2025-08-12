package org.example.app;

import javax.swing.*;
import java.awt.*;

public class MainMenu extends JFrame {

    private DBHelper dbHelper;

    public MainMenu() {
        setTitle("Fatura Yükleme Sistemi - Ana Menü");
        setSize(400, 250);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        dbHelper = new DBHelper();  //db is created here

        JPanel panel = new JPanel(new GridLayout(3, 2, 10, 10));

        JButton btnFaturaOlustur = new JButton("Fatura Oluştur");
        JButton btnFaturaXML = new JButton("XML İşlemleri");
        JButton btnFaturaJSON = new JButton("JSON İşlemleri");
        JButton btnFaturaSil = new JButton("Fatura Sil");
        JButton btnCikis = new JButton("Çıkış");

        btnFaturaOlustur.addActionListener(e -> {
            CreateInvoice createInvoiceForm = new CreateInvoice();
            createInvoiceForm.setVisible(true);
        });

        btnFaturaXML.addActionListener(e -> {
            invoiceXMLForm xmlForm = new invoiceXMLForm(dbHelper);
            xmlForm.setVisible(true);
        });



        btnFaturaJSON.addActionListener(e -> {
            // Open the invoice selection dialog
            invoiceSelectDialog selectDialog = new invoiceSelectDialog(this, dbHelper);
            selectDialog.setVisible(true);

            // Get the selected invoice ID after the dialog closes
            int selectedInvoiceId = selectDialog.getSelectedInvoiceId();

            if (selectedInvoiceId != -1) {
                // Open JSON form with selected invoice
                invoiceJSONForm jsonForm = new invoiceJSONForm(dbHelper, selectedInvoiceId);
                jsonForm.setVisible(true);
            }
        });

        btnFaturaSil.addActionListener(e -> {
            invoiceDeleteForm deleteForm = new invoiceDeleteForm(dbHelper);
            deleteForm.setVisible(true);
        });


        btnCikis.addActionListener(e -> System.exit(0));

        panel.add(btnFaturaOlustur);
        panel.add(btnFaturaXML);
        panel.add(btnFaturaJSON);
        panel.add(btnFaturaSil);
        panel.add(btnCikis);

        add(panel);
        setVisible(true);
    }
}
