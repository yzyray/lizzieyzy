package featurecat.lizzie.gui;

import featurecat.lizzie.Config;
import java.awt.Font;
import javax.swing.JComboBox;

public class JFontComboBox extends JComboBox {
  public JFontComboBox() {
    super();
    this.setFont(new Font("", Font.PLAIN, Config.frameFontSize));
  }
}
