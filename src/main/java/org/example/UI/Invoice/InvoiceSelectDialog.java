package org.example.UI.Invoice;

import org.example.model.Invoices;
import org.example.repository.InvoicesRepository;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.util.List;

public class InvoiceSelectDialog extends JDialog {

    private int selectedInvoiceId = -1;

    public InvoiceSelectDialog(Frame owner, Connection conn) {
        super(owner, "Fatura Seç", true);
        setSize(400, 300);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout());

        // Tablo modeli oluştur
        DefaultTableModel model = new DefaultTableModel(new Object[]{"ID", "Seri", "Numara"}, 0);
        JTable table = new JTable(model);
        add(new JScrollPane(table), BorderLayout.CENTER);

        // Faturaları getir ve tabloya ekle
        try {
            InvoicesRepository repo = new InvoicesRepository(conn);
            List<Invoices> invoices = repo.getAll();
            for (Invoices inv : invoices) {
                model.addRow(new Object[]{inv.getId(), inv.getSeries(), inv.getInvoiceNum()});
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Faturalar getirilemedi: " + ex.getMessage());
        }

        // Seç butonu
        JButton selectBtn = new JButton("Seç");
        selectBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row >= 0) {
                selectedInvoiceId = (int) table.getValueAt(row, 0);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Önce bir fatura seçin!");
            }
        });
        add(selectBtn, BorderLayout.SOUTH);
    }

    public int getSelectedInvoiceId() {
        return selectedInvoiceId;
    }
}
