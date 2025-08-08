package org.example.app;

import javax.swing.*;
import java.awt.*;

public class invoiceDeleteForm extends JFrame {

    public invoiceDeleteForm() {
        setTitle("Fatura Silme Formu");
        setSize(400, 300);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JLabel label = new JLabel("Fatura silme işlemleri burada yapılacak.");
        label.setHorizontalAlignment(SwingConstants.CENTER);
        add(label, BorderLayout.CENTER);

        setVisible(true);
    }
}
