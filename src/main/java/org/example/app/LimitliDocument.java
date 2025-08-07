package org.example.app;

import javax.swing.text.*;

public class LimitliDocument extends PlainDocument {
    private int limit;

    public LimitliDocument(int limit) {
        this.limit = limit;
    }

    @Override
    public void insertString(int offset, String str, AttributeSet attr) throws BadLocationException {
        if (str == null) return;

        if ((getLength() + str.length()) <= limit) {
            super.insertString(offset, str, attr);
        } else {
            // Karakter sınırı aşıldıysa ekleme yapma
            java.awt.Toolkit.getDefaultToolkit().beep(); // İsteğe bağlı: kullanıcıyı sesle uyar
        }
    }
}
