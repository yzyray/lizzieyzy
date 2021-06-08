package featurecat.lizzie.gui;

import featurecat.lizzie.Config;
import java.awt.Font;
import javax.swing.JLabel;

public class JFontLabel extends JLabel {
  public JFontLabel() {
    super();
    this.setFont(new Font("", Font.PLAIN, Config.frameFontSize));
  }

  public JFontLabel(String text) {
    super();
    this.setText(text);
    this.setFont(new Font("", Font.PLAIN, Config.frameFontSize));
  }
}
