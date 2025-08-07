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

        // Sadece rakam içermesine izin ver
        if (str.matches("[0-9]+")) {
            if ((getLength() + str.length()) <= limit) {
                super.insertString(offset, str, attr);
            } else {
                java.awt.Toolkit.getDefaultToolkit().beep(); // İsteğe bağlı sesli uyarı
            }
        } else {
            java.awt.Toolkit.getDefaultToolkit().beep(); // Rakam değilse de uyarı
        }
    }
}
