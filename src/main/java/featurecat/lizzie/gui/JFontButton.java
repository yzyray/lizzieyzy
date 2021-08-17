package featurecat.lizzie.gui;

import featurecat.lizzie.Config;
import java.awt.Font;
import javax.swing.ImageIcon;
import javax.swing.JButton;

public class JFontButton extends JButton {
  public JFontButton() {
    super();
    this.setFont(new Font(Config.sysDefaultFontName, Font.PLAIN, Config.frameFontSize));
  }

  public JFontButton(String text) {
    super();
    this.setText(text);
    this.setFont(new Font(Config.sysDefaultFontName, Font.PLAIN, Config.frameFontSize));
  }

  public JFontButton(ImageIcon icon) {
    super();
    this.setIcon(icon);
    this.setFont(new Font(Config.sysDefaultFontName, Font.PLAIN, Config.frameFontSize));
  }
}
