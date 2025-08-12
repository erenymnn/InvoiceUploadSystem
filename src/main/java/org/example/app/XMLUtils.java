package org.example.app;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.util.List;

public class XMLUtils {
    private DBHelper dbHelper;

    public XMLUtils(DBHelper dbHelper) {
        this.dbHelper = dbHelper;
    }

    public void saveInvoiceToXML(DBHelper.InvoiceSummary invoice, DBHelper.Customer customer,
                                 List<DBHelper.InvoiceItem> items, File file) throws Exception {

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();

        Document doc = builder.newDocument();

        Element invoiceElement = doc.createElement("Invoice");
        invoiceElement.setAttribute("series", invoice.getSeries());
        invoiceElement.setAttribute("number", invoice.getInvoiceNum());
        doc.appendChild(invoiceElement);

        Element customerElement = doc.createElement("Customer");
        customerElement.setAttribute("id", String.valueOf(customer.getId()));
        customerElement.setAttribute("name", customer.getName() + " " + customer.getSurname());
        customerElement.setAttribute("tckn", customer.getTckn());
        invoiceElement.appendChild(customerElement);

        Element itemsElement = doc.createElement("Items");
        invoiceElement.appendChild(itemsElement);

        for (DBHelper.InvoiceItem item : items) {
            Element itemElement = doc.createElement("Item");
            itemElement.setAttribute("id", String.valueOf(item.getItemId()));

            String itemName = dbHelper.getProductNameById(item.getItemId());
            double price = dbHelper.getProductPriceById(item.getItemId());

            itemElement.setAttribute("name", itemName);
            itemElement.setAttribute("price", String.valueOf(price));
            itemElement.setAttribute("quantity", String.valueOf(item.getQuantity()));
            itemElement.setAttribute("total", String.valueOf(item.getTotal()));
            itemsElement.appendChild(itemElement);
        }

        Element discountElement = doc.createElement("Discount");
        discountElement.appendChild(doc.createTextNode(String.valueOf(invoice.getDiscount())));
        invoiceElement.appendChild(discountElement);

        Element totalBeforeElement = doc.createElement("TotalBeforeDiscount");
        totalBeforeElement.appendChild(doc.createTextNode(String.valueOf(invoice.getTotal())));
        invoiceElement.appendChild(totalBeforeElement);

        Element totalAfterElement = doc.createElement("TotalAfterDiscount");
        double totalAfter = invoice.getTotal() - invoice.getDiscount();
        if (totalAfter < 0) totalAfter = 0;
        totalAfterElement.appendChild(doc.createTextNode(String.valueOf(totalAfter)));
        invoiceElement.appendChild(totalAfterElement);

        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");

        DOMSource source = new DOMSource(doc);
        StreamResult result = new StreamResult(file);

        transformer.transform(source, result);
    }
}
