package org.example.UI.Invoice;

import org.example.Backend.infrastructure.DatabaseConnection;
import org.example.Backend.model.Customers;
import org.example.Backend.model.InvoiceItems;
import org.example.Backend.model.Invoices;
import org.example.Backend.model.Items;
import org.example.Backend.repository.CustomersRepository;
import org.example.Backend.repository.InvoiceItemsRepository;
import org.example.Backend.repository.InvoicesRepository;
import org.example.Backend.service.CustomersService;
import org.example.UI.Customer.CustomerSelectDialog;
import org.example.UI.Item.ItemsSelectionDialog;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import java.awt.*;
import java.sql.Connection;

public class InvoiceForm extends JFrame {

    private JTextField seriesField;
    private JTextField numberField;
    private JTextField customerNameField;
    private JTextField customerTcknField;
    private JTable itemsTable;
    private JTextField discountField;
    private JTextField totalBeforeDiscountField;
    private JTextField totalAfterDiscountField;

    private CustomersService customerService;

    public InvoiceForm(JFrame parent) {
        try {
            customerService = new CustomersService(
                    new CustomersRepository(DatabaseConnection.getConnection())
            );
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(parent, "Servis başlatılamadı: " + ex.getMessage());
            return;
        }

        setTitle("Fatura Oluştur");
        setSize(850, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        initTopPanel();
        initCenterPanel();
        initBottomPanel();

        setInvoiceFieldLimits(); // Seri ve numara kısıtlamaları
    }

    private void initTopPanel() {
        JPanel topPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        seriesField = new JTextField(10);
        numberField = new JTextField(10);

        customerNameField = new JTextField(20);
        customerNameField.setEditable(false);
        customerTcknField = new JTextField(20);
        customerTcknField.setEditable(false);

        JButton selectCustomerButton = new JButton("Müşteri Seç");

        gbc.gridx = 0; gbc.gridy = 0;
        topPanel.add(new JLabel("Fatura Serisi:"), gbc);
        gbc.gridx = 1; topPanel.add(seriesField, gbc);

        gbc.gridx = 2;
        topPanel.add(new JLabel("Fatura Numarası:"), gbc);
        gbc.gridx = 3;
        topPanel.add(numberField, gbc);

        gbc.gridx = 4;
        topPanel.add(selectCustomerButton, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        topPanel.add(new JLabel("Müşteri Adı Soyadı:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 4;
        topPanel.add(customerNameField, gbc);
        gbc.gridwidth = 1;

        gbc.gridx = 0; gbc.gridy = 2;
        topPanel.add(new JLabel("TCKN:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 4;
        topPanel.add(customerTcknField, gbc);
        gbc.gridwidth = 1;

        selectCustomerButton.addActionListener(e -> selectCustomerAction());

        add(topPanel, BorderLayout.NORTH);
    }

    private void initCenterPanel() {
        JPanel centerPanel = new JPanel(new BorderLayout(5, 5));
        String[] columnNames = {"Ürün Adı", "Birim Fiyat", "Miktar", "Tutar"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0);
        itemsTable = new JTable(model);
        centerPanel.add(new JScrollPane(itemsTable), BorderLayout.CENTER);

        JButton addItemButton = new JButton("Ürün Ekle");
        addItemButton.addActionListener(e -> addInvoiceItem());
        centerPanel.add(addItemButton, BorderLayout.SOUTH);

        add(centerPanel, BorderLayout.CENTER);
    }

    private void initBottomPanel() {
        JPanel bottomPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(3, 3, 3, 3);

        gbc.gridx = 0; gbc.gridy = 0;
        bottomPanel.add(new JLabel("İndirim Tutarı:"), gbc);
        discountField = new JTextField("0", 10);
        gbc.gridx = 1; bottomPanel.add(discountField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        bottomPanel.add(new JLabel("Toplam (İndirim Öncesi):"), gbc);
        totalBeforeDiscountField = new JTextField(10);
        totalBeforeDiscountField.setEditable(false);
        gbc.gridx = 1; bottomPanel.add(totalBeforeDiscountField, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        bottomPanel.add(new JLabel("Toplam (İndirim Sonrası):"), gbc);
        totalAfterDiscountField = new JTextField(10);
        totalAfterDiscountField.setEditable(false);
        gbc.gridx = 1; bottomPanel.add(totalAfterDiscountField, gbc);

        JButton calculateButton = new JButton("Faturayı Hesapla");
        gbc.gridx = 0; gbc.gridy = 3; bottomPanel.add(calculateButton, gbc);

        JButton saveButton = new JButton("Kaydet");
        gbc.gridx = 1; bottomPanel.add(saveButton, gbc);

        JButton cancelButton = new JButton("Vazgeç");
        gbc.gridx = 2; bottomPanel.add(cancelButton, gbc);

        // Buton aksiyonları
        calculateButton.addActionListener(e -> updateTotals());
        saveButton.addActionListener(e -> saveInvoice());
        cancelButton.addActionListener(e -> dispose());

        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void selectCustomerAction() {
        String series = seriesField.getText().trim();
        String invoiceNum = numberField.getText().trim();

        if(series.isEmpty() || invoiceNum.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Fatura serisi ve numarası giriniz!");
            return;
        }

        CustomerSelectDialog dialog = new CustomerSelectDialog(this);
        dialog.setVisible(true);

        Customers selected = dialog.getSelectedCustomer();
        if(selected != null) {
            customerNameField.setText(selected.getName() + " " + selected.getSurname());
            customerTcknField.setText(selected.getTckn());
        }
    }

    private void addInvoiceItem() {
        ItemsSelectionDialog dialog = new ItemsSelectionDialog(this);
        dialog.setVisible(true);

        Items selectedItem = dialog.getSelectedItem();
        if(selectedItem != null) {
            String quantityStr = JOptionPane.showInputDialog(this, "Miktarı giriniz:");
            if (quantityStr == null || quantityStr.trim().isEmpty()) return;

            int quantity;
            try {
                quantity = Integer.parseInt(quantityStr);
                if(quantity <= 0) throw new NumberFormatException();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Miktar pozitif bir tam sayı olmalıdır!");
                return;
            }

            DefaultTableModel model = (DefaultTableModel) itemsTable.getModel();
            double price = selectedItem.getPrice();
            double total = price * quantity;

            model.addRow(new Object[]{
                    selectedItem.getName(),
                    price,
                    quantity,
                    total
            });

            updateTotals();
        }
    }

    private void updateTotals() {
        DefaultTableModel model = (DefaultTableModel) itemsTable.getModel();
        double sum = 0;

        for (int i = 0; i < model.getRowCount(); i++) {
            sum += (double) model.getValueAt(i, 3);
        }

        totalBeforeDiscountField.setText(String.valueOf(sum));

        double discount = 0;
        try {
            discount = Double.parseDouble(discountField.getText().trim());
        } catch (NumberFormatException ignored) {}

        totalAfterDiscountField.setText(String.valueOf(sum - discount));
    }

    private void saveInvoice() {
        String series = seriesField.getText().trim();
        String number = numberField.getText().trim();
        String customerTckn = customerTcknField.getText().trim();

        if(series.isEmpty() || number.isEmpty() || customerTckn.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Seri, numara ve müşteri bilgilerini eksiksiz giriniz!");
            return;
        }

        DefaultTableModel model = (DefaultTableModel) itemsTable.getModel();
        if(model.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "Fatura için en az bir ürün ekleyin!");
            return;
        }

        try {
            Connection conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            CustomersRepository customersRepo = new CustomersRepository(conn);
            Customers customer = customersRepo.findByTckn(customerTckn);

            if(customer == null) {
                JOptionPane.showMessageDialog(this, "Müşteri bulunamadı!");
                return;
            }

            // 1. Fatura kaydı
            Invoices invoice = new Invoices();
            invoice.setSeries(series);
            invoice.setInvoice(number);
            invoice.setCustomerId(customer.getId());
            double totalBeforeDiscount = 0;
            for(int i = 0; i < model.getRowCount(); i++) {
                totalBeforeDiscount += (double) model.getValueAt(i, 3);
            }
            invoice.setDiscount(Double.parseDouble(discountField.getText().trim()));
            invoice.setTotal(totalBeforeDiscount - invoice.getDiscount());

            InvoicesRepository invoiceRepo = new InvoicesRepository(conn);
            int invoiceId = invoiceRepo.addAndReturnId(invoice); // burada yeni metod ile id alıyoruz

            // 2. Fatura ürün detayları
            InvoiceItemsRepository itemsRepo = new InvoiceItemsRepository(conn);
            for(int i = 0; i < model.getRowCount(); i++) {
                InvoiceItems item = new InvoiceItems();
                item.setInvoiceId(invoiceId);
                item.setItemId(0); // Ürün tablosu varsa burada set edilebilir
                item.setQuantity((int) model.getValueAt(i, 2));
                item.setTotal((double) model.getValueAt(i, 3));

                itemsRepo.addAndReturnId(item);
            }

            conn.commit();
            JOptionPane.showMessageDialog(this, "Fatura başarıyla kaydedildi!");
            dispose();

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Fatura kaydedilemedi: " + ex.getMessage());
        }
    }






    // ----------------- Seri ve Numara Kısıtlamaları -----------------
    private void setInvoiceFieldLimits() {
        ((AbstractDocument) seriesField.getDocument()).setDocumentFilter(new DocumentFilter() {
            @Override
            public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
                if ((fb.getDocument().getLength() + string.length()) <= 8 && string.matches("[a-zA-Z0-9]*")) {
                    super.insertString(fb, offset, string, attr);
                }
            }

            @Override
            public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
                if ((fb.getDocument().getLength() - length + text.length()) <= 8 && text.matches("[a-zA-Z0-9]*")) {
                    super.replace(fb, offset, length, text, attrs);
                }
            }
        });

        ((AbstractDocument) numberField.getDocument()).setDocumentFilter(new DocumentFilter() {
            @Override
            public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
                if ((fb.getDocument().getLength() + string.length()) <= 10 && string.matches("\\d*")) {
                    super.insertString(fb, offset, string, attr);
                }
            }

            @Override
            public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
                if ((fb.getDocument().getLength() - length + text.length()) <= 10 && text.matches("\\d*")) {
                    super.replace(fb, offset, length, text, attrs);
                }
            }
        });
    }
}
