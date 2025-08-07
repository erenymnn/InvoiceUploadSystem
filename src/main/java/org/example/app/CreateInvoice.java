package org.example.app;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class CreateInvoice extends JFrame {

    private JTextField txtFaturaSerisi;
    private JTextField txtFaturaNumarasi;
    private JLabel lblSecilenMusteri;

    private JTable tblUrunler;
    private DefaultTableModel tableModel;

    private JTextField txtIndirim;
    private JLabel lblToplamOncesi;
    private JLabel lblToplamSonrasi;

    private int secilenMusteriId = -1;

    public CreateInvoice() {
        setTitle("Fatura Oluştur");
        setSize(700, 450);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel ustPanel = new JPanel(new GridLayout(4, 2, 10, 10));

        ustPanel.add(new JLabel("Fatura Serisi:"));
        txtFaturaSerisi = new JTextField();
        txtFaturaSerisi.setDocument(new LimitliDocument(5)); // Maksimum 5 karakter
        ustPanel.add(txtFaturaSerisi);




        ustPanel.add(new JLabel("Fatura Numarası:"));
        txtFaturaNumarasi = new JTextField();
        txtFaturaNumarasi.setDocument(new NumberDocument(10));
        ustPanel.add(txtFaturaNumarasi);

        JButton btnMusteriSec = new JButton("Müşteri Seç");
        ustPanel.add(btnMusteriSec);

        lblSecilenMusteri = new JLabel("Müşteri seçilmedi");
        ustPanel.add(lblSecilenMusteri);

        // Tablo modelinde "Ürün ID" gizli sütun olarak eklendi
        tableModel = new DefaultTableModel();
        tableModel.setColumnIdentifiers(new String[]{"Ürün ID", "Ürün Adı", "Fiyat", "Miktar", "Tutar"});

        tblUrunler = new JTable(tableModel);
        // "Ürün ID" sütununu gizlemek için:
        tblUrunler.removeColumn(tblUrunler.getColumnModel().getColumn(0));

        JScrollPane scrollPane = new JScrollPane(tblUrunler);

        JPanel altPanel = new JPanel(new GridLayout(2, 3, 10, 10));

        altPanel.add(new JLabel("İndirim Tutarı:"));
        txtIndirim = new JTextField("0");
        altPanel.add(txtIndirim);

        JButton btnHesapla = new JButton("Faturayı Hesapla");
        altPanel.add(btnHesapla);

        altPanel.add(new JLabel("Toplam (İndirim Öncesi):"));
        lblToplamOncesi = new JLabel("0.00");
        altPanel.add(lblToplamOncesi);

        altPanel.add(new JLabel("Toplam (İndirim Sonrası):"));
        lblToplamSonrasi = new JLabel("0.00");
        altPanel.add(lblToplamSonrasi);

        JButton btnUrunEkle = new JButton("Ürün Ekle");
        ustPanel.add(btnUrunEkle);

        JButton btnKaydet = new JButton("Faturayı Kaydet");
        altPanel.add(btnKaydet);

        setLayout(new BorderLayout(10, 10));
        add(ustPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(altPanel, BorderLayout.SOUTH);

        btnMusteriSec.addActionListener(e -> {
            SelectCustomer musteriSecFrame = new SelectCustomer(this);
            musteriSecFrame.setVisible(true);
        });

        btnUrunEkle.addActionListener(e -> {
            ProductSelectFrame productSelectFrame = new ProductSelectFrame(this);
            productSelectFrame.setVisible(true);
        });

        btnHesapla.addActionListener(e -> hesaplaToplam());

        btnKaydet.addActionListener(e -> {
            kaydetFatura();
        });

        setVisible(true);
    }


    public void musteriSecildi(int id, String adSoyad, String tckn) {
        this.secilenMusteriId = id;
        lblSecilenMusteri.setText("Seçilen Müşteri: " + adSoyad + " (TCKN: " + tckn + ")");
    }

    public void urunEklendi(int itemId, String urunAdi, double fiyat, int miktar, double toplam) {
        tableModel.addRow(new Object[]{itemId, urunAdi, fiyat, miktar, toplam});
        hesaplaToplam();
    }

    private void hesaplaToplam() {
        double toplamOncesi = 0.0;
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            toplamOncesi += (double) tableModel.getValueAt(i, 4);
        }

        lblToplamOncesi.setText(String.format("%.2f", toplamOncesi));

        double indirim = 0.0;
        try {
            String text = txtIndirim.getText().replace(",", ".").trim();
            indirim = Double.parseDouble(text);
            if (indirim < 0) {
                JOptionPane.showMessageDialog(this, "İndirim negatif olamaz.");
                indirim = 0;
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Geçerli bir indirim giriniz.");
            indirim = 0;
        }

        double toplamSonrasi = toplamOncesi - indirim;
        if (toplamSonrasi < 0) toplamSonrasi = 0;

        lblToplamSonrasi.setText(String.format("%.2f", toplamSonrasi));
    }

    private void kaydetFatura() {

        String series = txtFaturaSerisi.getText().trim();
        String invoiceNum = txtFaturaNumarasi.getText().trim();



        if (series.length() < 5 || invoiceNum.length() < 10) {
            JOptionPane.showMessageDialog(this, "fatura seri numarası veya fatura numarasını Eksik girdiniz. Lütfen tüm alanları doğru doldurun.");
            return; // Devam etme, işlem durur
        }



        if (series.isEmpty() || invoiceNum.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Fatura serisi ve numarası boş olamaz.");
            return;
        }

        if (secilenMusteriId == -1) {
            JOptionPane.showMessageDialog(this, "Lütfen müşteri seçin.");
            return;
        }

        double discount = 0;
        double total = 0;
        try {
            String text = txtIndirim.getText().replace(",", ".").trim();
            discount = Double.parseDouble(text);
        } catch (NumberFormatException e) {
            discount = 0;
        }

        for (int i = 0; i < tableModel.getRowCount(); i++) {
            total += (double) tableModel.getValueAt(i, 4);
        }
        double totalAfterDiscount = total - discount;
        if (totalAfterDiscount < 0) totalAfterDiscount = 0;

        DBHelper db = new DBHelper();

        int invoiceId = db.addInvoice(series, invoiceNum, secilenMusteriId, discount, totalAfterDiscount);
        if (invoiceId == -1) {
            JOptionPane.showMessageDialog(this, "Fatura kaydedilirken hata oluştu.");
            return;
        }

        boolean itemsSaved = true;
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            int itemId = (int) tableModel.getValueAt(i, 0);
            int quantity = (int) tableModel.getValueAt(i, 3);
            double itemTotal = (double) tableModel.getValueAt(i, 4);

            boolean added = db.addInvoiceItem(invoiceId, itemId, quantity, itemTotal);
            if (!added) {
                itemsSaved = false;
                break;
            }
        }

        if (itemsSaved) {
            JOptionPane.showMessageDialog(this, "Fatura başarıyla kaydedildi.");
            this.dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Fatura ürünleri kaydedilirken hata oluştu.");
        }
    }


}
