package org.example.UI.Item;

import org.example.Backend.infrastructure.DatabaseConnection;
import org.example.Backend.model.Items;
import org.example.Backend.repository.ItemsRepository;
import org.example.Backend.service.ItemsService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ItemsSelectionDialog extends JDialog {

    private JTable table;
    private DefaultTableModel model;
    private JButton selectButton, newItemButton;
    private Items selectedItem;
    private ItemsService itemService;

    public ItemsSelectionDialog(JFrame parent) {
        super(parent, "Ürün Seç", true); // modal
        setSize(500, 400);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());

        try {
            itemService = new ItemsService(new ItemsRepository(DatabaseConnection.getConnection()));
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(parent, "Ürün servisi başlatılamadı: " + ex.getMessage());
            return;
        }

        // Tablo
        model = new DefaultTableModel(new Object[]{"ID", "Ürün Adı", "Fiyat"}, 0);
        table = new JTable(model);
        loadItems();
        add(new JScrollPane(table), BorderLayout.CENTER);

        // Buton paneli
        JPanel buttonPanel = new JPanel();
        selectButton = new JButton("Seç");
        selectButton.addActionListener(e -> selectItem());
        buttonPanel.add(selectButton);

        newItemButton = new JButton("Yeni Ürün");
        newItemButton.addActionListener(e -> addNewItem());
        buttonPanel.add(newItemButton);

        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void loadItems() {
        model.setRowCount(0);
        List<Items> items = itemService.getAllItems();
        for (Items i : items) {
            model.addRow(new Object[]{i.getId(), i.getName(), i.getPrice()});
        }
    }

    private void selectItem() {
        int row = table.getSelectedRow();
        if (row == -1) return;
        int id = (int) model.getValueAt(row, 0);
        selectedItem = itemService.getItemById(id);
        dispose();
    }

    private void addNewItem() {
        // NewItemForm modal olarak açılır
        NewItemForm form = new NewItemForm((JFrame) getParent(), itemService);
        form.setVisible(true);

        // Form kapatıldıktan sonra tabloyu güncelle
        loadItems();
    }

    public Items getSelectedItem() {
        return selectedItem;
    }
}
