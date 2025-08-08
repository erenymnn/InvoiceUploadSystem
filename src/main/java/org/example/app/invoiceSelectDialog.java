package org.example.app;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class invoiceSelectDialog extends JDialog {

    private invoiceXMLForm parent;
    private JTable table;
    private DefaultTableModel tableModel;
    private List<DBHelper.InvoiceSummary> invoices;
    private DBHelper db;

    public invoiceSelectDialog(invoiceXMLForm parent) {
        super(parent, "Fatura Seç", true);
        this.parent = parent;
        this.db = new DBHelper();

        setSize(600, 400);
        setLocationRelativeTo(parent);

        tableModel = new DefaultTableModel();
        tableModel.setColumnIdentifiers(new String[]{"ID", "Seri", "Numara", "Müşteri ID", "İndirim", "Toplam"});

        table = new JTable(tableModel);
        table.removeColumn(table.getColumnModel().getColumn(0)); // ID gizle

        loadInvoices();

        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        JButton btnSelect = new JButton("Seç");
        btnSelect.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Lütfen bir fatura seçin.");
                return;
            }
            int modelRow = table.convertRowIndexToModel(selectedRow);
            DBHelper.InvoiceSummary selectedInvoice = invoices.get(modelRow);
            parent.invoiceSelected(selectedInvoice);
            dispose();
        });

        JPanel btnPanel = new JPanel();
        btnPanel.add(btnSelect);
        add(btnPanel, BorderLayout.SOUTH);
    }

    private void loadInvoices() {
        invoices = db.getAllInvoiceSummaries();
        tableModel.setRowCount(0);

        for (DBHelper.InvoiceSummary invoice : invoices) {
            tableModel.addRow(new Object[]{
                    invoice.getId(),
                    invoice.getSeries(),
                    invoice.getInvoiceNum(),
                    invoice.getCustomerId(),
                    invoice.getDiscount(),
                    invoice.getTotal()
            });
        }
    }
}
