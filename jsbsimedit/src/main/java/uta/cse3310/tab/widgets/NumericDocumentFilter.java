package uta.cse3310.tab.widgets;

import javax.swing.text.*;

public class NumericDocumentFilter extends DocumentFilter {

    private static final String NUMERIC_REGEX = "[0-9.\\-]*";

    @Override
    public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr)
            throws BadLocationException {
        if (string != null && string.matches(NUMERIC_REGEX)) {
            super.insertString(fb, offset, string, attr);
        }
    }

    @Override
    public void replace(FilterBypass fb, int offset, int length,
                        String text, AttributeSet attrs) throws BadLocationException {
        if (text != null && text.matches(NUMERIC_REGEX)) {
            super.replace(fb, offset, length, text, attrs);
        }
    }
}
