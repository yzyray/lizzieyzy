package featurecat.lizzie.gui;

import featurecat.lizzie.Config;
import java.awt.Font;
import javax.swing.JMenu;

public class JFontMenu extends JMenu {
  public JFontMenu() {
    super();
    this.setFont(new Font(Config.sysDefaultFontName, Font.PLAIN, Config.frameFontSize));
  }

  public JFontMenu(String text) {
    super();
    this.setText(text);
    this.setFont(new Font(Config.sysDefaultFontName, Font.PLAIN, Config.frameFontSize));
  }
}
