package featurecat.lizzie.gui;

import featurecat.lizzie.Config;
import java.awt.Font;
import javax.swing.JCheckBoxMenuItem;

public class JFontCheckBoxMenuItem extends JCheckBoxMenuItem {

  public JFontCheckBoxMenuItem() {
    super();
    this.setFont(new Font(Config.sysDefaultFontName, Font.PLAIN, Config.frameFontSize));
  }

  public JFontCheckBoxMenuItem(String text) {
    super();
    this.setText(text);
    this.setFont(new Font(Config.sysDefaultFontName, Font.PLAIN, Config.frameFontSize));
  }
}
