package org.example.Main;

import org.example.UI.Delete.DeleteInvoiceForm;
import org.example.UI.Invoice.InvoiceForm;
import org.example.UI.JSON.InvoiceJSONForm;
import org.example.UI.XML.InvoiceXMLForm;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class MainFrame extends JFrame {

    public MainFrame() {
        setTitle("Invoice Upload System");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        initUI();
    }

    private void initUI() {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new GridLayout(5, 1, 10, 10));

        JButton createInvoiceButton = new JButton("Fatura Oluştur");
        createInvoiceButton.addActionListener((ActionEvent e) ->
                new InvoiceForm(MainFrame.this).setVisible(true)
        );

        JButton deleteInvoiceButton = new JButton("Fatura Sil");
        deleteInvoiceButton.addActionListener((ActionEvent e) ->
                new DeleteInvoiceForm(MainFrame.this).setVisible(true)
        );

        JButton xmlButton = new JButton("Fatura XML İşlemleri");
        xmlButton.addActionListener((ActionEvent e) ->
                new InvoiceXMLForm().setVisible(true)
        );

        JButton jsonButton = new JButton("Fatura JSON İşlemleri");
        jsonButton.addActionListener((ActionEvent e) ->
                new InvoiceJSONForm().setVisible(true)
        );

        JButton exitButton = new JButton("Çıkış");
        exitButton.addActionListener((ActionEvent e) -> System.exit(0));

        mainPanel.add(createInvoiceButton);
        mainPanel.add(deleteInvoiceButton);
        mainPanel.add(xmlButton);
        mainPanel.add(jsonButton);
        mainPanel.add(exitButton);

        add(mainPanel, BorderLayout.CENTER);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainFrame().setVisible(true));
    }
}
