package org.example.UI.JSON;

import org.example.Backend.infrastructure.DatabaseConnection;
import org.example.Backend.model.Customers;
import org.example.Backend.model.Invoices;
import org.example.Backend.repository.CustomersRepository;
import org.example.Backend.repository.InvoicesRepository;
import org.example.UI.Invoice.InvoiceSelectDialog;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;

public class InvoiceJSONForm extends JFrame {

    private JTextField seriesField;
    private JTextField numberField;
    private JTextField customerField;
    private JTextField totalField;

    public InvoiceJSONForm() {
        initialize();
    }

    private void initialize() {
        setTitle("Fatura JSON İşlemleri");
        setSize(600, 300);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel panel = new JPanel();
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        panel.setLayout(new GridLayout(5, 2, 10, 10));
        add(panel, BorderLayout.CENTER);

        panel.add(new JLabel("Fatura Serisi:"));
        seriesField = new JTextField();
        seriesField.setEditable(false);
        panel.add(seriesField);

        panel.add(new JLabel("Fatura Numarası:"));
        numberField = new JTextField();
        numberField.setEditable(false);
        panel.add(numberField);

        panel.add(new JLabel("Müşteri:"));
        customerField = new JTextField();
        customerField.setEditable(false);
        panel.add(customerField);

        panel.add(new JLabel("Toplam:"));
        totalField = new JTextField();
        totalField.setEditable(false);
        panel.add(totalField);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout());

        JButton selectInvoiceBtn = new JButton("Fatura Seç");
        JButton saveJsonBtn = new JButton("Faturayı JSON Kaydet");
        JButton closeBtn = new JButton("Kapat");

        buttonPanel.add(selectInvoiceBtn);
        buttonPanel.add(saveJsonBtn);
        buttonPanel.add(closeBtn);

        add(buttonPanel, BorderLayout.SOUTH);

        selectInvoiceBtn.addActionListener(this::selectInvoice);
        saveJsonBtn.addActionListener(this::saveInvoiceAsJSON);
        closeBtn.addActionListener(e -> dispose());
    }

    private void selectInvoice(ActionEvent e) {
        try {
            Connection conn = DatabaseConnection.getConnection();
            InvoiceSelectDialog dialog = new InvoiceSelectDialog(this, conn);
            dialog.setVisible(true);
            int selectedInvoiceId = dialog.getSelectedInvoiceId();
            if (selectedInvoiceId > 0) {
                InvoicesRepository invoiceRepo = new InvoicesRepository(conn);
                Invoices invoice = invoiceRepo.getById(selectedInvoiceId);

                CustomersRepository customerRepo = new CustomersRepository(conn);
                Customers customer = customerRepo.getById(invoice.getCustomerId());

                seriesField.setText(invoice.getSeries());
                numberField.setText(invoice.getInvoice());
                customerField.setText(customer.getName() + " " + customer.getSurname() + " (" + customer.getTckn() + ")");
                totalField.setText(String.valueOf(invoice.getTotal()));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Fatura seçilemedi: " + ex.getMessage());
        }
    }

    private void saveInvoiceAsJSON(ActionEvent e) {
        try {
            String series = seriesField.getText();
            String number = numberField.getText();
            String customer = customerField.getText();
            String total = totalField.getText();

            if(series.isEmpty() || number.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Önce bir fatura seçin!");
                return;
            }

            // JSON string (geçerli format)
            String jsonData = "{\n" +
                    "  \"series\": \"" + series + "\",\n" +
                    "  \"number\": \"" + number + "\",\n" +
                    "  \"customer\": \"" + customer + "\",\n" +
                    "  \"total\": \"" + total + "\"\n" +
                    "}";

            // Kaydetme penceresi
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("JSON Dosyasını Kaydet");
            int userSelection = fileChooser.showSaveDialog(this);
            if (userSelection != JFileChooser.APPROVE_OPTION) {
                return;
            }

            File fileToSave = fileChooser.getSelectedFile();
            if (!fileToSave.getAbsolutePath().toLowerCase().endsWith(".json")) {
                fileToSave = new File(fileToSave.getAbsolutePath() + ".json");
            }

            // UTF-8 ile yazma
            try (OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(fileToSave), StandardCharsets.UTF_8)) {
                writer.write(jsonData);
            }

            JOptionPane.showMessageDialog(this, "Fatura JSON olarak kaydedildi:\n" + fileToSave.getAbsolutePath());

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "JSON kaydedilemedi: " + ex.getMessage());
        }
    }


}
