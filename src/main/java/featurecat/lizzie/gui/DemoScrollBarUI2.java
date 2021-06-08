package featurecat.lizzie.gui;

import featurecat.lizzie.Lizzie;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JScrollBar;
import javax.swing.plaf.basic.BasicScrollBarUI;

// 自定义滚动条UI
public class DemoScrollBarUI2 extends BasicScrollBarUI {
  private boolean needAddAction;

  public DemoScrollBarUI2(boolean needAddAction) {
    this.needAddAction = needAddAction;
  }

  @Override
  protected void configureScrollBarColors() {

    // 把手

    // thumbColor = Color.GRAY;

    // thumbHighlightColor = Color.BLUE;

    // thumbDarkShadowColor = Color.BLACK;

    // thumbLightShadowColor = Color.YELLOW;

    // 滑道

    // trackColor = Color.black;

    // setThumbBounds(0, 0, 3, 10);

    // trackHighlightColor = Color.GREEN;

  }

  /** 设置滚动条的宽度 */
  @Override
  public Dimension getPreferredSize(JComponent c) {

    // TODO Auto-generated method stub

    c.setPreferredSize(new Dimension(8, 8));

    return super.getPreferredSize(c);
  }

  // 重绘滑块的滑动区域背景

  public void paintTrack(Graphics g, JComponent c, Rectangle trackBounds) {

    Graphics2D g2 = (Graphics2D) g;

    //  判断滚动条是垂直的 还是水平的
    //
    //        if (this.scrollbar.getOrientation() == JScrollBar.VERTICAL) {
    //
    //          // 设置画笔
    //
    //          gp =
    //              new GradientPaint(
    //                  0, 0, new Color(80, 80, 80), 0,0, new Color(80, 80, 80));
    //        }
    //
    //        if (this.scrollbar.getOrientation() == JScrollBar.HORIZONTAL) {

    //    gp =
    //        new GradientPaint(
    //            0, 0, new Color(80, 80, 80), 0, trackBounds.height, new Color(80, 80, 80));
    // //      }
    //
    //    g2.setPaint(gp);
    if (needAddAction) g.setColor(new Color(215, 215, 215));
    else g2.setColor(new Color(235, 235, 235));
    // 填充Track

    g2.fillRect(trackBounds.x, trackBounds.y, trackBounds.width, trackBounds.height);

    // 绘制Track的边框
    //         g2.setColor(new Color(120, 120, 120));
    //   g2.drawRect(trackBounds.x, trackBounds.y, trackBounds.width - 1,
    //         trackBounds.height-1);

    if (trackHighlight == BasicScrollBarUI.DECREASE_HIGHLIGHT) this.paintDecreaseHighlight(g);

    if (trackHighlight == BasicScrollBarUI.INCREASE_HIGHLIGHT) this.paintIncreaseHighlight(g);
  }

  @Override
  protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {

    // 把绘制区的x，y点坐标定义为坐标系的原点

    // 这句一定一定要加上啊，不然拖动就失效了

    g.translate(thumbBounds.x, thumbBounds.y);

    // 设置把手颜色
    if (needAddAction) g.setColor(Color.GRAY);
    else g.setColor(new Color(180, 180, 180));

    // 画一个圆角矩形

    // 这里面前四个参数就不多讲了，坐标和宽高

    // 后两个参数需要注意一下，是用来控制角落的圆角弧度

    // g.drawRoundRect(0, 0, 5, thumbBounds.height - 1, 5, 5);

    // 消除锯齿

    Graphics2D g2 = (Graphics2D) g;

    RenderingHints rh =
        new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

    g2.addRenderingHints(rh);

    // 半透明

    g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.7f));

    // 设置填充颜色，这里设置了渐变，由下往上

    // g2.setPaint(new GradientPaint(c.getWidth() / 2, 1, Color.GRAY,

    // c.getWidth() / 2, c.getHeight(), Color.GRAY));

    // 填充圆角矩形
    if (this.scrollbar.getOrientation() == JScrollBar.VERTICAL)
      g2.fillRect(0, 0, 9, thumbBounds.height);
    else g2.fillRect(0, 0, thumbBounds.width, 9);
    //  drawPolygon(g2,6,0);
    //  drawPolygonDown(g2,6,thumbBounds.height);
  }

  //  private void drawPolygon(Graphics2D g, int centerX, int centerY) {
  //    int[] xPoints = {centerX, centerX - 6, centerX + 6};
  //    int[] yPoints = {centerY, centerY + 5, centerY + 5};
  //    g.fillPolygon(xPoints, yPoints, 3);
  //  }
  //
  //  private void drawPolygonDown(Graphics2D g, int centerX, int centerY) {
  //    int[] xPoints = {centerX, centerX - 6, centerX + 6};
  //    int[] yPoints = {centerY, centerY - 5, centerY - 5};
  //    g.fillPolygon(xPoints, yPoints, 3);
  //  }

  /** 创建滚动条上方的按钮 */
  @Override
  protected JButton createIncreaseButton(int orientation) {
    if (this.scrollbar.getOrientation() == JScrollBar.VERTICAL) {
      ImageIcon down = new ImageIcon();
      try {
        down.setImage(
            ImageIO.read(AnalysisFrame.class.getResourceAsStream("/assets/smallDown.png")));
      } catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
      JButton button = new JButton(down);
      if (needAddAction)
        button.addMouseListener(
            new MouseAdapter() {
              public void mouseExited(MouseEvent e) {
                if (Lizzie.config.hideBlunderControlPane) {
                  return;
                }
                Lizzie.frame.commentBlunderControlPane.setVisible(false);
              }

              public void mouseEntered(MouseEvent e) {
                if (Lizzie.config.hideBlunderControlPane) {
                  return;
                }
                Lizzie.frame.setBlunderControlPane(false, true);
                Lizzie.frame.commentBlunderControlPane.setVisible(true);
              }
            });
      button.setMargin(new Insets(0, 0, -2, 0));
      button.setBorderPainted(false);

      button.setContentAreaFilled(false);
      return button;
    } else {
      ImageIcon down = new ImageIcon();
      try {
        down.setImage(
            ImageIO.read(AnalysisFrame.class.getResourceAsStream("/assets/smallRight.png")));
      } catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
      JButton button = new JButton(down);
      button.setMargin(new Insets(0, 0, 0, -2));
      button.setBorderPainted(false);

      button.setContentAreaFilled(false);
      return button;
    }

    // button.setBorder(null);

  }

  /** 创建滚动条下方的按钮 */
  @Override
  protected JButton createDecreaseButton(int orientation) {
    if (this.scrollbar.getOrientation() == JScrollBar.VERTICAL) {
      ImageIcon up = new ImageIcon();
      try {
        up.setImage(ImageIO.read(AnalysisFrame.class.getResourceAsStream("/assets/smallUp.png")));
      } catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
      JButton button = new JButton(up);
      if (needAddAction)
        button.addMouseListener(
            new MouseAdapter() {
              public void mouseExited(MouseEvent e) {
                if (Lizzie.config.hideBlunderControlPane) {
                  return;
                }
                Lizzie.frame.commentBlunderControlPane.setVisible(false);
              }

              public void mouseEntered(MouseEvent e) {
                if (Lizzie.config.hideBlunderControlPane) {
                  return;
                }
                Lizzie.frame.setBlunderControlPane(false, true);
                Lizzie.frame.commentBlunderControlPane.setVisible(true);
              }
            });
      button.setMargin(new Insets(-2, 0, 0, 0));

      button.setBorderPainted(false);

      button.setContentAreaFilled(false);

      //  button.setBorder(null);

      return button;
    } else {
      ImageIcon up = new ImageIcon();
      try {
        up.setImage(ImageIO.read(AnalysisFrame.class.getResourceAsStream("/assets/smallLeft.png")));
      } catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
      JButton button = new JButton(up);
      button.setMargin(new Insets(0, -2, 0, 0));

      button.setBorderPainted(false);

      button.setContentAreaFilled(false);

      //  button.setBorder(null);

      return button;
    }
  }
}
