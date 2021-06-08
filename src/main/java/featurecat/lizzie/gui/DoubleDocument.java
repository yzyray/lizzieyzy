package featurecat.lizzie.gui;

import java.math.BigDecimal;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

public class DoubleDocument extends PlainDocument {

  private static final long serialVersionUID = 1001689415662878505L;

  // int maxLen;  //最长字符长度
  // int decimalLen; //小数位数

  public DoubleDocument() {
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
    //		System.out.println("inStr===" + inStr + ",offset==" + offset
    //			+ ",getLength()=" + getLength() + ",value=" + numStr);
    // 校验字符长度限制
    //		if (getLength() + inStr.length() > maxLen) {
    //			return;
    //		}
    // 校验是否是有效数字
    try {
      new BigDecimal(numStr);
    } catch (NumberFormatException e1) {
      return;
    }
    // 校验小数位数限制
    //		int indexNum = numStr.indexOf(".");
    //		if (indexNum > 0) {
    //			int len = numStr.substring(indexNum + 1).length();
    ////			if (len > decimalLen) {
    ////				return;
    ////			}
    //		}
    super.insertString(offset, inStr, attrSet);
  }
}
