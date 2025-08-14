package org.example.UI.Delete;

import org.example.model.Customers;
import org.example.model.Invoices;
import org.example.repository.CustomersRepository;
import org.example.repository.InvoiceItemsRepository;
import org.example.repository.InvoicesRepository;
import org.example.service.CustomersService;
import org.example.service.InvoiceItemsService;
import org.example.service.InvoiceService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

public class DeleteInvoiceForm extends JFrame {

    private InvoiceService invoiceService;
    private InvoiceItemsService invoiceItemsService;
    private CustomersService customerService;

    private JTextField seriesField;
    private JTextField invoiceNumField;
    private JTextArea infoArea;
    private JButton searchButton;
    private JButton deleteButton;
    private JButton closeButton;

    private Invoices selectedInvoice;

    public DeleteInvoiceForm(JFrame parent) {
        super("Fatura Silme");

        // JDBC Connection ve servisleri oluştur
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/upload_system?serverTimezone=UTC",
                    "root",
                    "1234"
            );

            // Repository’leri oluştur
            InvoicesRepository invoicesRepo = new InvoicesRepository(conn);
            InvoiceItemsRepository invoiceItemsRepo = new InvoiceItemsRepository(conn);
            CustomersRepository customerRepo = new CustomersRepository(conn);

            // Servisleri oluştur
            invoiceService = new InvoiceService(invoicesRepo);
            invoiceItemsService = new InvoiceItemsService(invoiceItemsRepo);
            customerService = new CustomersService(customerRepo);

        } catch (ClassNotFoundException | SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Veritabanı bağlantısı başarısız:\n" + ex.getMessage(),
                    "Hata",
                    JOptionPane.ERROR_MESSAGE);
            dispose();
            return;
        }

        setSize(500, 400);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        initComponents();
    }

    private void initComponents() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5,5,5,5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Fatura Serisi
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Fatura Serisi:"), gbc);

        seriesField = new JTextField();
        gbc.gridx = 1; gbc.gridy = 0;
        panel.add(seriesField, gbc);

        // Fatura Numarası
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Fatura Numarası:"), gbc);

        invoiceNumField = new JTextField();
        gbc.gridx = 1; gbc.gridy = 1;
        panel.add(invoiceNumField, gbc);

        // Bilgi Alanı
        infoArea = new JTextArea(10, 30);
        infoArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(infoArea);
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        panel.add(scrollPane, gbc);

        // Butonlar
        JPanel buttonPanel = new JPanel();

        searchButton = new JButton("Faturayı Ara");
        searchButton.addActionListener(this::searchInvoice);
        buttonPanel.add(searchButton);

        deleteButton = new JButton("Faturayı Sil");
        deleteButton.addActionListener(this::deleteInvoice);
        deleteButton.setEnabled(false);
        buttonPanel.add(deleteButton);

        closeButton = new JButton("Kapat");
        closeButton.addActionListener(e -> dispose());
        buttonPanel.add(closeButton);

        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        panel.add(buttonPanel, gbc);

        add(panel);
    }

    private void searchInvoice(ActionEvent e) {
        if (invoiceService == null || customerService == null) return;

        String series = seriesField.getText().trim();
        String invoiceNum = invoiceNumField.getText().trim();
        if (series.isEmpty() || invoiceNum.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Fatura serisi ve numarası giriniz!");
            return;
        }

        try {
            List<Invoices> invoices = invoiceService.getAllInvoices();
            selectedInvoice = invoices.stream()
                    .filter(inv -> inv.getSeries().equals(series) && inv.getInvoiceNum().equals(invoiceNum))
                    .findFirst().orElse(null);

            if (selectedInvoice != null) {
                Customers customer = customerService.getCustomerById(selectedInvoice.getCustomerId());

                infoArea.setText("Fatura Bulundu:\n");
                infoArea.append("Müşteri Ad: " + customer.getName() + "\n");
                infoArea.append("Müşteri Soyad: " + customer.getSurname() + "\n");
                infoArea.append("TCKN: " + customer.getTckn() + "\n");
                infoArea.append("Toplam: " + selectedInvoice.getTotal() + "\n");
                infoArea.append("İndirim: " + selectedInvoice.getDiscount() + "\n");

                deleteButton.setEnabled(true);
            } else {
                infoArea.setText("Fatura bulunamadı!");
                deleteButton.setEnabled(false);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Faturalara erişim sırasında hata oluştu:\n" + ex.getMessage());
        }
    }

    private void deleteInvoice(ActionEvent e) {
        if (invoiceItemsService == null || selectedInvoice == null) return;

        int confirm = JOptionPane.showConfirmDialog(this,
                "Bu faturayı silmek istediğinize emin misiniz?",
                "Onay", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                // Önce invoiceItems sil
                invoiceItemsService.getInvoiceItemsByInvoiceId(selectedInvoice.getId())
                        .forEach(item -> invoiceItemsService.deleteInvoiceItem(item.getId()));

                // Ardından invoice sil
                boolean success = invoiceService.deleteInvoice(selectedInvoice.getId());

                if (success) {
                    JOptionPane.showMessageDialog(this, "Fatura başarıyla silindi.");
                    infoArea.setText("");
                    deleteButton.setEnabled(false);
                } else {
                    JOptionPane.showMessageDialog(this, "Fatura silinemedi!");
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Silme işlemi sırasında hata oluştu:\n" + ex.getMessage());
            }
        }
    }
}
