package org.example.app;

import javax.swing.text.*;

public class NumberDocument extends PlainDocument {
    private int limit;

    public NumberDocument(int limit) {
        this.limit = limit;
    }

    @Override
    public void insertString(int offset, String str, AttributeSet attr) throws BadLocationException {
        if (str == null) return;

       //  Only allow numbers
        if (!str.matches("\\d+")) {
            java.awt.Toolkit.getDefaultToolkit().beep(); // Geçersiz giriş için sesli uyarı
            return;
        }

        // Check character limit
        if ((getLength() + str.length()) <= limit) {
            super.insertString(offset, str, attr);
        } else {
            java.awt.Toolkit.getDefaultToolkit().beep(); // Sound warning for length limit
        }
    }
}
