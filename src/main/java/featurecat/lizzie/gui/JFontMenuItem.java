package featurecat.lizzie.gui;

import featurecat.lizzie.Config;
import java.awt.Font;
import javax.swing.JMenuItem;

public class JFontMenuItem extends JMenuItem {

  public JFontMenuItem(String text) {
    super();
    this.setText(text);
    this.setFont(new Font("", Font.PLAIN, Config.frameFontSize));
  }

  public JFontMenuItem() {
    super();
    this.setFont(new Font("", Font.PLAIN, Config.frameFontSize));
  }
}
