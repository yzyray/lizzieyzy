package featurecat.lizzie.gui;

import featurecat.lizzie.Lizzie;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.JTextArea;

public class JPaintTextArea extends JTextArea {
  public JPaintTextArea() {
    super();
  }

  protected void paintComponent(Graphics g) {
    if (Lizzie.config.usePureBackground) g.setColor(Lizzie.config.pureBackgroundColor);
    else ((Graphics2D) g).setPaint(Lizzie.frame.backgroundPaint);
    g.fillRect(0, 0, getWidth(), getHeight());
    super.paintComponent(g);
  }
}
