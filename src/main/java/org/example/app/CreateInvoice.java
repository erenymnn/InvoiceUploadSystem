package org.example.app;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.html.parser.Element;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;

import java.awt.*;
import java.io.File;

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
        txtFaturaNumarasi.setDocument(new NumberDocument(11)); // Maksimum 11 karakter
        ustPanel.add(txtFaturaNumarasi);

        JButton btnMusteriSec = new JButton("Müşteri Seç");
        ustPanel.add(btnMusteriSec);

        lblSecilenMusteri = new JLabel("Müşteri seçilmedi");
        ustPanel.add(lblSecilenMusteri);

        JButton btnUrunEkle = new JButton("Ürün Ekle");
        ustPanel.add(btnUrunEkle);

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

        JButton btnKaydet = new JButton("Faturayı Kaydet");
        altPanel.add(btnKaydet);

        JButton btnXMLExport = new JButton("XML Olarak Dışa Aktar");
        altPanel.add(btnXMLExport);


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
        // Kaydetme kodun buraya gelecek (sende var zaten)
    }


    }

