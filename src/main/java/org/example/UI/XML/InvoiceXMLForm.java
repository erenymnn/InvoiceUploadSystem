package org.example.UI.XML;

import org.example.infrastructure.DatabaseConnection;
import org.example.model.Customers;
import org.example.model.Invoices;
import org.example.repository.CustomersRepository;
import org.example.repository.InvoicesRepository;
import org.example.ui.Invoice.InvoiceSelectDialog;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.sql.Connection;

public class InvoiceXMLForm extends JFrame {

    private JTextField seriesField;
    private JTextField numberField;
    private JTextField customerField;
    private JTextField totalField;

    public InvoiceXMLForm() {
        initialize();
    }

    private void initialize() {
        setTitle("Fatura XML İşlemleri");
        setSize(600, 300);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel panel = new JPanel();
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        panel.setLayout(new GridLayout(5, 2, 10, 10));
        add(panel, BorderLayout.CENTER);

        // Alanlar
        panel.add(new JLabel("Fatura Serisi:"));
        seriesField = new JTextField();
        seriesField.setEditable(false);
        panel.add(seriesField);

        panel.add(new JLabel("Fatura Numarası:"));
        numberField = new JTextField();
        numberField.setEditable(false);
        panel.add(numberField);

        panel.add(new JLabel("Müşteri:"));
        customerField = new JTextField();
        customerField.setEditable(false);
        panel.add(customerField);

        panel.add(new JLabel("Toplam:"));
        totalField = new JTextField();
        totalField.setEditable(false);
        panel.add(totalField);

        // Butonlar paneli
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton selectInvoiceBtn = new JButton("Fatura Seç");
        JButton saveXmlBtn = new JButton("Faturayı XML Kaydet");
        JButton closeBtn = new JButton("Kapat");

        buttonPanel.add(selectInvoiceBtn);
        buttonPanel.add(saveXmlBtn);
        buttonPanel.add(closeBtn);
        add(buttonPanel, BorderLayout.SOUTH);

        // Buton aksiyonları
        selectInvoiceBtn.addActionListener(this::selectInvoice);
        saveXmlBtn.addActionListener(this::saveInvoiceAsXML);
        closeBtn.addActionListener(e -> dispose());
    }

    private void selectInvoice(ActionEvent e) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            InvoiceSelectDialog dialog = new InvoiceSelectDialog(this, conn);

            dialog.setVisible(true);

            int selectedInvoiceId = dialog.getSelectedInvoiceId();
            if (selectedInvoiceId > 0) {
                InvoicesRepository invoiceRepo = new InvoicesRepository(conn);
                Invoices invoice = invoiceRepo.getById(selectedInvoiceId);

                CustomersRepository customerRepo = new CustomersRepository(conn);
                Customers customer = customerRepo.getById(invoice.getCustomerId());

                seriesField.setText(invoice.getSeries());
                numberField.setText(invoice.getInvoiceNum());
                customerField.setText(customer.getName() + " " + customer.getSurname() + " (" + customer.getTckn() + ")");
                totalField.setText(String.valueOf(invoice.getTotal()));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Fatura seçilemedi: " + ex.getMessage());
        }
    }

    private void saveInvoiceAsXML(ActionEvent e) {
        try {
            String series = seriesField.getText();
            String number = numberField.getText();
            String customer = customerField.getText();
            String total = totalField.getText();

            if (series.isEmpty() || number.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Önce bir fatura seçin!");
                return;
            }

            // JFileChooser ile kaydetme yeri seç
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("XML Dosyasını Kaydet");
            int userSelection = fileChooser.showSaveDialog(this);
            if (userSelection != JFileChooser.APPROVE_OPTION) {
                return; // kullanıcı iptal ettiyse çık
            }

            File fileToSave = fileChooser.getSelectedFile();
            // Eğer kullanıcı dosya uzantısını yazmadıysa ".xml" ekle
            if (!fileToSave.getName().toLowerCase().endsWith(".xml")) {
                fileToSave = new File(fileToSave.getAbsolutePath() + ".xml");
            }

            // XML oluşturma
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

            Document doc = docBuilder.newDocument();
            Element root = doc.createElement("Invoice");
            doc.appendChild(root);

            Element seriesEl = doc.createElement("Series");
            seriesEl.appendChild(doc.createTextNode(series));
            root.appendChild(seriesEl);

            Element numberEl = doc.createElement("Number");
            numberEl.appendChild(doc.createTextNode(number));
            root.appendChild(numberEl);

            Element customerEl = doc.createElement("Customer");
            customerEl.appendChild(doc.createTextNode(customer));
            root.appendChild(customerEl);

            Element totalEl = doc.createElement("Total");
            totalEl.appendChild(doc.createTextNode(total));
            root.appendChild(totalEl);

            // XML'i dosyaya kaydet
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");

            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(fileToSave);

            transformer.transform(source, result);

            JOptionPane.showMessageDialog(this, "XML dosyası başarıyla kaydedildi:\n" + fileToSave.getAbsolutePath());

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "XML kaydedilemedi: " + ex.getMessage());
        }
    }



    private void createElement(Document doc, Element parent, String tagName, String value) {
        Element el = doc.createElement(tagName);
        el.appendChild(doc.createTextNode(value));
        parent.appendChild(el);
    }
}
