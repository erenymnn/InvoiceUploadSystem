package org.example.app;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Map;

public class invoiceSelectDialog extends JDialog {
    private DBHelper dbHelper;
    private int selectedInvoiceId = -1;

    public invoiceSelectDialog(Frame owner, DBHelper dbHelper) {
        super(owner, "Fatura Seç", true);
        this.dbHelper = dbHelper;
        setSize(400, 300);
        setLocationRelativeTo(owner);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        List<Map<String, Object>> invoices = dbHelper.getInvoicesMaps();

        DefaultListModel<String> listModel = new DefaultListModel<>();
        for (Map<String, Object> inv : invoices) {
            String itemText = "ID: " + inv.get("id") + " - Seri: " + inv.get("series") + ", No: " + inv.get("invoice");
            listModel.addElement(itemText);
        }

        JList<String> invoiceList = new JList<>(listModel);
        invoiceList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scrollPane = new JScrollPane(invoiceList);
        add(scrollPane, BorderLayout.CENTER);

        JButton btnSelect = new JButton("Seç");
        btnSelect.setEnabled(false);
        add(btnSelect, BorderLayout.SOUTH);

        invoiceList.addListSelectionListener(e -> btnSelect.setEnabled(!invoiceList.isSelectionEmpty()));

        btnSelect.addActionListener(e -> {
            int index = invoiceList.getSelectedIndex();
            if (index != -1) {
                Map<String, Object> selectedInvoice = invoices.get(index);
                selectedInvoiceId = (int) selectedInvoice.get("id");
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Lütfen bir fatura seçin.");
            }
        });
    }

    public int getSelectedInvoiceId() {
        return selectedInvoiceId;
    }
}
