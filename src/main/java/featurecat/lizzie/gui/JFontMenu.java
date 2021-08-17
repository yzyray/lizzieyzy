package featurecat.lizzie.gui;

import featurecat.lizzie.Config;
import featurecat.lizzie.Lizzie;
import java.awt.Font;
import javax.swing.JMenu;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;

public class JFontMenu extends JMenu {
  public JFontMenu() {
    super();
    this.setFont(new Font(Config.sysDefaultFontName, Font.PLAIN, Config.frameFontSize));
  }

  public JFontMenu(String text) {
    super();
    this.setText(text);
    this.setFont(new Font(Config.sysDefaultFontName, Font.PLAIN, Config.frameFontSize));
    this.addMenuListener(
        new MenuListener() {

          public void menuSelected(MenuEvent e) {
            // if (Lizzie.config.isScaled) Lizzie.frame.repaint();
          }

          public void menuDeselected(MenuEvent e) {
            if (Lizzie.config.isScaled) Lizzie.frame.repaint();
          }

          public void menuCanceled(MenuEvent e) {
            //  if (Lizzie.config.isScaled) Lizzie.frame.repaint();
          }
        });
  }
}
