package org.example.UI.Customer;



import org.example.Backend.infrastructure.DatabaseConnection;
import org.example.Backend.model.Customers;
import org.example.Backend.repository.CustomersRepository;
import org.example.Backend.service.CustomersService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.SQLException;
import java.util.List;

public class CustomerSelectDialog extends JDialog {

    private JTable table;
    private DefaultTableModel tableModel;
    private JButton selectButton;
    private JButton newCustomerButton;
    private Customers selectedCustomer;
    private CustomersService service;

    public CustomerSelectDialog(JFrame parent) {
        super(parent, "Müşteri Seç", true);
        setSize(500, 400);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());

        try {
            service = new CustomersService(new CustomersRepository(DatabaseConnection.getConnection()));
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Veritabanına bağlanılamadı: " + ex.getMessage());
            dispose(); // Dialog açılamazsa kapat
            return;
        }

        tableModel = new DefaultTableModel(new Object[]{"ID", "Ad-Soyad", "TCKN"}, 0);
        table = new JTable(tableModel);
        loadCustomers();

        JPanel buttonPanel = new JPanel();
        selectButton = new JButton("Seç");
        selectButton.addActionListener(this::selectCustomer);
        buttonPanel.add(selectButton);

        newCustomerButton = new JButton("Yeni Müşteri");
        newCustomerButton.addActionListener(this::addNewCustomer);
        buttonPanel.add(newCustomerButton);

        add(new JScrollPane(table), BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }


    private void loadCustomers() {
        tableModel.setRowCount(0); // önce tabloyu temizle
        List<Customers> customers = service.getAllCustomers();
        for (Customers c : customers) {
            tableModel.addRow(new Object[]{c.getId(), c.getName() + " " + c.getSurname(), c.getTckn()});
        }
    }

    private void selectCustomer(ActionEvent e) {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Lütfen bir müşteri seçin!");
            return;
        }
        int id = (int) tableModel.getValueAt(row, 0);
        selectedCustomer = service.getCustomerById(id);
        dispose();
    }

    private void addNewCustomer(ActionEvent e) {
        JTextField nameField = new JTextField();
        JTextField surnameField = new JTextField();
        JTextField tcknField = new JTextField();
        Object[] message = {
                "Ad:", nameField,
                "Soyad:", surnameField,
                "TCKN:", tcknField
        };
        int option = JOptionPane.showConfirmDialog(this, message, "Yeni Müşteri Ekle", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            String name = nameField.getText().trim();
            String surname = surnameField.getText().trim();
            String tckn = tcknField.getText().trim();
            if (name.isEmpty() || surname.isEmpty() || tckn.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Ad, Soyad ve TCKN boş olamaz!");
                return;
            }
            boolean success = service.addCustomer(name, surname, tckn);
            if (success) {
                loadCustomers();
                // en son eklenen müşteriyi seçili yap
                int lastRow = tableModel.getRowCount() - 1;
                table.setRowSelectionInterval(lastRow, lastRow);
            } else {
                JOptionPane.showMessageDialog(this, "Müşteri eklenemedi!");
            }
        }
    }

    public Customers getSelectedCustomer() {
        return selectedCustomer;
    }
}
