package featurecat.lizzie.gui;

import featurecat.lizzie.Lizzie;
import javax.swing.JPopupMenu;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

public class JFontPopupMenu extends JPopupMenu {
  public JFontPopupMenu() {
    super();
    this.addPopupMenuListener(
        new PopupMenuListener() {

          @Override
          public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
            // TODO Auto-generated method stub
            if (Lizzie.config.isScaled && Lizzie.frame != null) Lizzie.frame.repaint();
          }

          @Override
          public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
            // TODO Auto-generated method stub
            //
          }

          @Override
          public void popupMenuCanceled(PopupMenuEvent e) {
            // TODO Auto-generated method stub
            if (Lizzie.config.isScaled && Lizzie.frame != null) Lizzie.frame.repaint();
          }
        });
  }
}
