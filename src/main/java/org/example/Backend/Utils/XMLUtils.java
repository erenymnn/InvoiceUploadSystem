package org.example.Backend.Utils;

import org.example.Backend.model.Customers;
import org.example.Backend.model.InvoiceItems;
import org.example.Backend.model.Invoices;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.util.List;

public class XMLUtils {

    public static void generateInvoiceXML(Invoices invoice, Customers customer, List<InvoiceItems> items, String filePath) throws Exception {
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();

        Document doc = dBuilder.newDocument();
        Element rootElement = doc.createElement("Invoice");
        doc.appendChild(rootElement);

        // Customer
        Element customerElement = doc.createElement("Customer");
        customerElement.appendChild(createElement(doc, "Id", String.valueOf(customer.getId())));
        customerElement.appendChild(createElement(doc, "Name", customer.getName()));
        customerElement.appendChild(createElement(doc, "Surname", customer.getSurname()));
        customerElement.appendChild(createElement(doc, "TCKN", customer.getTckn()));
        rootElement.appendChild(customerElement);

        // Invoice
        Element invoiceElement = doc.createElement("InvoiceInfo");
        invoiceElement.appendChild(createElement(doc, "Id", String.valueOf(invoice.getId())));
        invoiceElement.appendChild(createElement(doc, "Series", invoice.getSeries()));
        invoiceElement.appendChild(createElement(doc, "InvoiceNum", invoice.getInvoice()));
        invoiceElement.appendChild(createElement(doc, "Discount", String.valueOf(invoice.getDiscount())));
        invoiceElement.appendChild(createElement(doc, "Total", String.valueOf(invoice.getTotal())));
        rootElement.appendChild(invoiceElement);

        // Items
        Element itemsElement = doc.createElement("Items");
        for (InvoiceItems item : items) {
            Element itemElement = doc.createElement("Item");
            itemElement.appendChild(createElement(doc, "ItemId", String.valueOf(item.getItemId())));
            itemElement.appendChild(createElement(doc, "Quantity", String.valueOf(item.getQuantity())));
            itemElement.appendChild(createElement(doc, "Total", String.valueOf(item.getTotal())));
            itemsElement.appendChild(itemElement);
        }
        rootElement.appendChild(itemsElement);

        // Write to file
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");

        DOMSource source = new DOMSource(doc);
        StreamResult result = new StreamResult(new File(filePath));
        transformer.transform(source, result);
    }

    private static Element createElement(Document doc, String name, String value) {
        Element elem = doc.createElement(name);
        elem.appendChild(doc.createTextNode(value));
        return elem;
    }
}
