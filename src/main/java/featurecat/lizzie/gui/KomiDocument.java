package featurecat.lizzie.gui;

import java.math.BigDecimal;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

public class KomiDocument extends PlainDocument {

  private static final long serialVersionUID = 1001689415662878505L;

  // int maxLen;  //最长字符长度
  // int decimalLen; //小数位数

  public KomiDocument() {
    super();
  }
  //
  //	public DoubleDocument(int newDecimalLen, int newMaxLen) {
  //		super();
  //	//	decimalLen = newDecimalLen;
  //	//	maxLen = newMaxLen;
  //	}

  public void insertString(int offset, String inStr, AttributeSet attrSet)
      throws BadLocationException {
    // 获得输入框有效值
    String numStr = getText(0, offset) + inStr + getText(offset, getLength() - offset);
    if (!numStr.trim().equals("-")) {
      try {
        new BigDecimal(numStr);
      } catch (NumberFormatException e1) {
        return;
      }
    }

    super.insertString(offset, inStr, attrSet);
  }
}
