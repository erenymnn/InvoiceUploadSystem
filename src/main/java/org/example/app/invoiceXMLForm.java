package org.example.app;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.util.List;

public class invoiceXMLForm extends JFrame {

    private JLabel lblSelectedInvoice;
    private JLabel lblCustomerInfo;
    private JLabel lblTotalAmount;
    private JButton btnSelectInvoice;
    private JButton btnSaveXML;
    private JButton btnClose;

    private DBHelper db;
    private DBHelper.InvoiceSummary selectedInvoice;
    private DBHelper.Customer selectedCustomer;
    private List<DBHelper.InvoiceItem> selectedItems;

    public invoiceXMLForm() {
        setTitle("Fatura XML İşlemleri");
        setSize(500, 300);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        db = new DBHelper();

        lblSelectedInvoice = new JLabel("Seçilen fatura: Yok");
        lblCustomerInfo = new JLabel("Müşteri bilgisi: Yok");
        lblTotalAmount = new JLabel("Toplam tutar: 0.00");

        btnSelectInvoice = new JButton("Fatura Seç");
        btnSaveXML = new JButton("Faturayı XML Servisi için Kaydet");
        btnClose = new JButton("Kapat");

        btnSelectInvoice.addActionListener(e -> {
            invoiceSelectDialog dialog = new invoiceSelectDialog(this);
            dialog.setVisible(true);
        });

        btnSaveXML.addActionListener(e -> saveInvoiceXML());

        btnClose.addActionListener(e -> this.dispose());

        JPanel panel = new JPanel(new GridLayout(6, 1, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        panel.add(lblSelectedInvoice);
        panel.add(lblCustomerInfo);
        panel.add(lblTotalAmount);
        panel.add(btnSelectInvoice);
        panel.add(btnSaveXML);
        panel.add(btnClose);

        add(panel);

        setVisible(true);
    }

    public void invoiceSelected(DBHelper.InvoiceSummary invoice) {
        this.selectedInvoice = invoice;
        this.selectedCustomer = db.getCustomerById(invoice.getCustomerId());
        this.selectedItems = db.getInvoiceItems(invoice.getId());

        lblSelectedInvoice.setText("Seçilen fatura: Seri " + invoice.getSeries() + ", No: " + invoice.getInvoiceNum());
        if (selectedCustomer != null) {
            lblCustomerInfo.setText("Müşteri: " + selectedCustomer.getName() + " " + selectedCustomer.getSurname() + " (TCKN: " + selectedCustomer.getTckn() + ")");
        } else {
            lblCustomerInfo.setText("Müşteri bilgisi bulunamadı.");
        }
        lblTotalAmount.setText(String.format("Toplam tutar: %.2f", invoice.getTotal()));
    }

    private void saveInvoiceXML() {
        if (selectedInvoice == null) {
            JOptionPane.showMessageDialog(this, "Lütfen önce bir fatura seçin.");
            return;
        }

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("XML dosyasını kaydet");

        int userSelection = fileChooser.showSaveDialog(this);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            try {
                XMLUtils.saveInvoiceToXML(selectedInvoice, selectedCustomer, selectedItems, fileToSave);
                JOptionPane.showMessageDialog(this, "Fatura XML dosyası başarıyla kaydedildi:\n" + fileToSave.getAbsolutePath());
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "XML dosyası kaydedilirken hata oluştu:\n" + e.getMessage());
            }
        }
    }
}
