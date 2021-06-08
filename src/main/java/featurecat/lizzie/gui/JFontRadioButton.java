package featurecat.lizzie.gui;

import featurecat.lizzie.Config;
import java.awt.Font;
import javax.swing.JRadioButton;

public class JFontRadioButton extends JRadioButton {
  public JFontRadioButton() {
    super();
    this.setFont(new Font("", Font.PLAIN, Config.frameFontSize));
  }

  public JFontRadioButton(String text) {
    super();
    this.setText(text);
    this.setFont(new Font("", Font.PLAIN, Config.frameFontSize));
  }
}
