package featurecat.lizzie.gui;

import java.math.BigDecimal;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

public class KomiDocument extends PlainDocument {
  private boolean limitHalfInteger;

  public KomiDocument(boolean limitHalfInteger) {
    super();
    this.limitHalfInteger = limitHalfInteger;
  }

  public void insertString(int offset, String inStr, AttributeSet attrSet)
      throws BadLocationException {
    String numStr = getText(0, offset) + inStr + getText(offset, getLength() - offset);
    if (!numStr.trim().equals("-")) {
      try {
        new BigDecimal(numStr);
      } catch (NumberFormatException e1) {
        return;
      }
    }
    String oldString = getText(0, getLength());
    String newString = oldString.substring(0, offset) + inStr + oldString.substring(offset);
    int decimalPosition = oldString.lastIndexOf(".");
    if (decimalPosition > -1 && decimalPosition < offset) {
      if ((newString.length() - (decimalPosition + 1)) > 1) return;
      else if (limitHalfInteger) {
        try {
          if (Integer.parseInt(inStr) != 5) return;
        } catch (NumberFormatException e) {
          return;
        }
      }
    }
    super.insertString(offset, inStr, attrSet);
  }
}
