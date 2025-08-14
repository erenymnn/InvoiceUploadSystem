package org.example.UI.Item;

import org.example.model.Items;
import org.example.service.ItemsService;

import javax.swing.*;
import java.awt.*;

public class NewItemForm extends JDialog {

    private JTextField nameField;
    private JTextField priceField;
    private JButton saveButton, cancelButton;
    private ItemsService itemService;

    public NewItemForm(JFrame parent, ItemsService itemService) {
        super(parent, "Yeni Ürün Ekle", true); // modal
        this.itemService = itemService;

        setSize(300, 200);
        setLocationRelativeTo(parent);
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5,5,5,5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Ürün adı
        gbc.gridx = 0; gbc.gridy = 0;
        add(new JLabel("Ürün Adı:"), gbc);
        nameField = new JTextField(15);
        gbc.gridx = 1; add(nameField, gbc);

        // Fiyat
        gbc.gridx = 0; gbc.gridy = 1;
        add(new JLabel("Fiyat:"), gbc);
        priceField = new JTextField(15);
        gbc.gridx = 1; add(priceField, gbc);

        // Butonlar
        saveButton = new JButton("Kaydet");
        saveButton.addActionListener(e -> saveItem());
        cancelButton = new JButton("Vazgeç");
        cancelButton.addActionListener(e -> dispose());

        gbc.gridx = 0; gbc.gridy = 2;
        add(saveButton, gbc);
        gbc.gridx = 1;
        add(cancelButton, gbc);
    }

    private void saveItem() {
        String name = nameField.getText().trim();
        String priceStr = priceField.getText().trim();

        if (name.isEmpty() || priceStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ürün adı ve fiyat boş olamaz!");
            return;
        }

        double price;
        try {
            price = Double.parseDouble(priceStr);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Fiyat geçerli bir sayı olmalıdır!");
            return;
        }

        Items item = new Items(0, name, price);
        boolean success = itemService.addItem(item);

        if (success) {
            JOptionPane.showMessageDialog(this, "Ürün başarıyla eklendi!");
            dispose(); // formu kapat
        } else {
            JOptionPane.showMessageDialog(this, "Ürün eklenemedi!");
        }
    }
}
