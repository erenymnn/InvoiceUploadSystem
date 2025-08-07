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

        // Sadece rakamlara izin ver
        if (!str.matches("\\d+")) {
            java.awt.Toolkit.getDefaultToolkit().beep(); // Geçersiz giriş için sesli uyarı
            return;
        }

        // Karakter sınırını kontrol et
        if ((getLength() + str.length()) <= limit) {
            super.insertString(offset, str, attr);
        } else {
            java.awt.Toolkit.getDefaultToolkit().beep(); // Uzunluk sınırı için sesli uyarı
        }
    }
}
