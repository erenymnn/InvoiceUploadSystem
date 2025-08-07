package org.example.app;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;



public class TCKNLimitFilter extends DocumentFilter {
    private int maxLength;

    public TCKNLimitFilter(int maxLength) {
        this.maxLength = maxLength;
    }

    private boolean isNumeric(String text) {
        return text.matches("\\d*");  // sadece rakam
    }

    @Override
    public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr)
            throws BadLocationException {
        StringBuilder newText = new StringBuilder(fb.getDocument().getText(0, fb.getDocument().getLength()));
        newText.insert(offset, string);

        if (newText.length() <= maxLength && isNumeric(string)) {
            super.insertString(fb, offset, string, attr);
        }
    }

    @Override
    public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs)
            throws BadLocationException {
        StringBuilder newText = new StringBuilder(fb.getDocument().getText(0, fb.getDocument().getLength()));
        newText.replace(offset, offset + length, text);

        if (newText.length() <= maxLength && isNumeric(text)) {
            super.replace(fb, offset, length, text, attrs);
        }
    }
}
