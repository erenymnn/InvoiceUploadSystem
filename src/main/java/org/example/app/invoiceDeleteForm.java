package org.example.app;

import javax.swing.*;
import java.awt.*;

public class invoiceDeleteForm extends JFrame {
    private DBHelper dbHelper;

    private JTextField txtSeries;
    private JTextField txtinvoice;
    private JButton btnDelete;

    public invoiceDeleteForm(DBHelper dbHelper) {
        this.dbHelper = dbHelper;

        setTitle("Fatura Sil");
        setSize(350, 150);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new GridLayout(3, 2, 5, 5));

        add(new JLabel("Fatura Serisi:"));
        txtSeries = new JTextField();
        add(txtSeries);

        add(new JLabel("Fatura Numarası:"));
        txtinvoice = new JTextField();
        add(txtinvoice);

        btnDelete = new JButton("Sil");
        add(new JLabel()); // boş hücre
        add(btnDelete);

        btnDelete.addActionListener(e -> {
            String series = txtSeries.getText().trim();
            String invoice = txtinvoice.getText().trim();

            if (series.isEmpty() || invoice.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Lütfen hem seri hem numarayı giriniz!", "Hata", JOptionPane.ERROR_MESSAGE);
                return;
            }

            int invoiceId = dbHelper.findInvoiceIdBySeriesAndNumber(series, invoice);

            if (invoiceId == -1) {
                JOptionPane.showMessageDialog(this, "Fatura bulunamadı!", "Bilgi", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            int confirm = JOptionPane.showConfirmDialog(this, "Faturayı silmek istediğinize emin misiniz?", "Onay", JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                boolean deleted = dbHelper.deleteInvoiceById(invoiceId);
                if (deleted) {
                    JOptionPane.showMessageDialog(this, "Fatura başarıyla silindi.");
                    txtSeries.setText("");
                    txtinvoice.setText("");
                } else {
                    JOptionPane.showMessageDialog(this, "Fatura silinemedi.", "Hata", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }
}
