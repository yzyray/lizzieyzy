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
    Graphics2D g1 = (Graphics2D) g;
    if (Lizzie.config.usePureBackground) g1.setColor(Lizzie.config.pureBackgroundColor);
    else g1.setPaint(Lizzie.frame.backgroundPaint);
    g1.fillRect(0, 0, getWidth(), getHeight());
    super.paintComponent(g);
  }
}
