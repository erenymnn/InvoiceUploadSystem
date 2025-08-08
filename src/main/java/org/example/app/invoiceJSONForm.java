package org.example.app;

import javax.swing.*;
import java.awt.*;

public class invoiceJSONForm extends JFrame {

    public invoiceJSONForm() {
        setTitle("Fatura JSON İşlemleri");
        setSize(400, 300);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JLabel label = new JLabel("JSON işlemleri burada yapılacak.");
        label.setHorizontalAlignment(SwingConstants.CENTER);
        add(label, BorderLayout.CENTER);

        setVisible(true);
    }
}
