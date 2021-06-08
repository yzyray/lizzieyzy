package featurecat.lizzie.gui;

import featurecat.lizzie.Config;
import featurecat.lizzie.Lizzie;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import javax.swing.JDialog;
import javax.swing.JPanel;

public class AwareScaled extends JDialog {
  public AwareScaled() {
    this.setUndecorated(true);
    JPanel mainPanel =
        new JPanel(true) {
          @Override
          protected void paintComponent(Graphics g) {
            //  super.paintComponent(g);
            final Graphics2D g0 = (Graphics2D) g;
            final AffineTransform t = g0.getTransform();
            final double scaling = t.getScaleX(); // Assuming square pixels :P
            if (scaling > 1) {
              Config.isScaled = true;
              Lizzie.javaScaleFactor = (float) scaling;
            }
            this.setVisible(false);
          }
        };
    getContentPane().add(mainPanel);
    setSize(1, 1);
  }
}
