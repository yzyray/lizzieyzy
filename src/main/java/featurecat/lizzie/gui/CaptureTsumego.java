package featurecat.lizzie.gui;

import static java.awt.image.BufferedImage.TYPE_INT_ARGB;

import java.awt.AWTException;
import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import javax.swing.JDialog;
import javax.swing.JPanel;

public class CaptureTsumego extends JDialog {
  CaptureTsumego t;
  private int startX;
  private int startY;
  private int capWidth;
  private int capHeight;
  private JPanel mainPanel;

  public CaptureTsumego() {
    try {
      t =
          new CaptureTsumego(null) {
            private static final long serialVersionUID = 1L;

            protected void capture() {
              super.capture();
            }
          };
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public void start() {
    try {
      t.open();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private static final long serialVersionUID = 1L;

  private int directionIndex = -1; // 1左上2右上3左下4右下
  private int x1 = -1, y1 = -1, x2 = -1, y2 = -1, x3 = -1, y3 = -1;
  private int widthGap, heightGap;
  private double curWidthGap, curHeightGap;
  private int clickTimes = 0;
  private boolean capFailed;
  private int curX, curY;
  private int tempWidthGap;
  private int tempHeightGap;
  //  private int orgx, orgy, endx, endy;
  //  private int orgxMouse, orgyMouse, endxMouse, endyMouse;

  private Dimension screenSize;
  private BufferedImage imageShow;
  private BufferedImage imageOut;
  private Color backGroundColor = new Color(0, 0, 0, 25);

  public void paintMianPanel(Graphics g) {
    if (imageShow != null) {
      g.drawImage(imageShow, 0, 0, this);
    }
  }

  private void drawSelectArea() {
    //	    if (x1 == -1 || y1 == -1)
    //	    	return;
    imageShow = new BufferedImage(getWidth(), getHeight(), TYPE_INT_ARGB);
    Graphics2D g = (Graphics2D) imageShow.getGraphics();

    //	    startX = Math.min(orgxMouse, endxMouse);
    //	    startY = Math.min(orgyMouse, endyMouse);
    //	    capWidth = width;
    //	    capHeight = height;
    g.setColor(backGroundColor);
    g.fillRect(0, 0, getWidth(), getHeight());

    if (clickTimes == 0) {
      g.setColor(new Color(0, 245, 0, 200));
      g.setStroke(new BasicStroke(1));
      g.drawLine(0, curY, getWidth(), curY);
      g.drawLine(curX, 0, curX, getHeight());
    } else if (clickTimes == 1) {
      g.setColor(new Color(0, 245, 0, 200));
      g.setStroke(new BasicStroke(2));
      g.drawLine(0, curY, getWidth(), curY);
      g.drawLine(curX, 0, curX, getHeight());
      g.setColor(new Color(0, 0, 200, 120));
      g.setStroke(new BasicStroke(4));
      tempWidthGap = Math.abs(x1 - curX);
      tempHeightGap = Math.abs(y1 - curY);
      if (curX > x1 && curY > y1) {
        for (int i = 0; i < 5; i++) {
          g.drawLine(x1 + tempWidthGap * i, y1, x1 + tempWidthGap * i, y1 + tempHeightGap * 5);
        }
        for (int i = 0; i < 5; i++) {
          g.drawLine(x1, y1 + tempHeightGap * i, x1 + tempWidthGap * 5, y1 + tempHeightGap * i);
        }
      } else if (curX < x1 && curY > y1) {
        for (int i = 0; i < 5; i++) {
          g.drawLine(x1 - tempWidthGap * i, y1, x1 - tempWidthGap * i, y1 + tempHeightGap * 5);
        }
        for (int i = 0; i < 5; i++) {
          g.drawLine(x1, y1 + tempHeightGap * i, x1 - tempWidthGap * 5, y1 + tempHeightGap * i);
        }
      } else if (curX < x1 && curY < y1) {
        for (int i = 0; i < 5; i++) {
          g.drawLine(x1 - tempWidthGap * i, y1, x1 - tempWidthGap * i, y1 - tempHeightGap * 5);
        }
        for (int i = 0; i < 5; i++) {
          g.drawLine(x1, y1 - tempHeightGap * i, x1 - tempWidthGap * 5, y1 - tempHeightGap * i);
        }
      } else if (curX > x1 && curY < y1) {
        for (int i = 0; i < 5; i++) {
          g.drawLine(x1 + tempWidthGap * i, y1, x1 + tempWidthGap * i, y1 - tempHeightGap * 5);
        }
        for (int i = 0; i < 5; i++) {
          g.drawLine(x1, y1 - tempHeightGap * i, x1 + tempWidthGap * 5, y1 - tempHeightGap * i);
        }
      }

    } else if (clickTimes == 2) {
      int x = Math.min(x1, curX);
      int y = Math.min(y1, curY);
      int width = Math.abs(x1 - curX);
      int height = Math.abs(y1 - curY);
      g.setColor(new Color(0, 0, 200, 200));
      g.setStroke(new BasicStroke(3));
      g.drawRect(x, y, width, height);
      int verticalLines = (int) Math.round(width / curWidthGap);
      int horizonLines = (int) Math.round(height / curHeightGap);
      for (int i = 1; i < verticalLines; i++) {
        g.drawLine((int) (x + curWidthGap * i), y, (int) (x + curWidthGap * i), y + height);
      }
      for (int i = 1; i < horizonLines; i++) {
        g.drawLine(x, (int) (y + curHeightGap * i), x + width, (int) (y + curHeightGap * i));
      }
    }
    repaint();
    //		int tx = endx + 5;
    //		int ty = endy + 20;
    //		if (tx + 100 > screenSize.width || ty + 30 > screenSize.height) {
    //			tx = endx - 100;
    //			ty = endy - 30;
    //		}
    //		g.setColor(Color.RED);
    //		g.drawString("w: " + width + ", h: " + height, tx, ty);
  }

  private void bindSelectAreaListener() {
    this.addMouseListener(
        new MouseAdapter() {
          public void mouseClicked(MouseEvent e) {
            if (e.getButton() == MouseEvent.BUTTON1) {
              switch (clickTimes) {
                case 0:
                  x1 = e.getX();
                  y1 = e.getY();
                  break;
                case 1:
                  widthGap = tempWidthGap;
                  heightGap = tempHeightGap;
                  if (tempWidthGap == 0 || tempHeightGap == 0) {
                    capFailed = true;
                    capture();
                  }
                  if (x2 > x1 && y2 > y1) directionIndex = 1;
                  else if (x2 < x1 && y2 > y1) directionIndex = 2;
                  else if (x2 > x1 && y2 < y1) directionIndex = 3;
                  else if (x2 < x1 && y2 < y1) directionIndex = 3;
                  break;
                case 2:
                  x3 = e.getX();
                  y3 = e.getY();
                  capture();
                  break;
              }
              clickTimes++;
              //	              orgx = e.getX();
              //	              orgy = e.getY();
              //	              Point point = MouseInfo.getPointerInfo().getLocation();
              //	              orgxMouse = point.x;
              //	              orgyMouse = point.y;
            }
          }

          //	          public void mouseReleased(MouseEvent e) {
          //	            orgx = -1;
          //	            orgy = -1;
          //	            capture();
          //	          }
        });
    //	    this.addMouseMotionListener(
    //	        new MouseMotionAdapter() {
    //	          public void mouseDragged(MouseEvent e) {
    //	            endx = e.getX();
    //	            endy = e.getY();
    //	            drawSelectArea();
    //	            Point point = MouseInfo.getPointerInfo().getLocation();
    //	            endxMouse = point.x;
    //	            endyMouse = point.y;
    //	          }
    //	        });

    addMouseMotionListener(
        new MouseAdapter() {
          @Override
          public void mouseMoved(MouseEvent e) {
            curX = e.getX();
            curY = e.getY();
            if (clickTimes == 2) {
              int curWidth = Math.abs(curX - x1);
              int curHeight = Math.abs(curY - y1);
              curWidthGap = curWidth / (double) Math.round(curWidth / (double) widthGap);
              curHeightGap = curHeight / (double) Math.round(curHeight / (double) heightGap);
            }
            drawSelectArea();
          }
        });

    addKeyListener(
        new KeyAdapter() {
          public void keyPressed(KeyEvent e) {
            if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
              quit();
            }
          }
        });
  }

  public CaptureTsumego(JDialog owner) throws Exception {
    super(owner);
  }

  public void open() throws Exception {
    bindSelectAreaListener();
    screenSize = Toolkit.getDefaultToolkit().getScreenSize();

    mainPanel =
        new JPanel() {
          @Override
          public void paintComponent(Graphics g) {
            ((Graphics2D) g).setComposite(AlphaComposite.getInstance(AlphaComposite.SRC));
            paintMianPanel(g);
          }
        };
    this.getContentPane().add(mainPanel);
    this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    this.setUndecorated(true);
    this.setBackground(backGroundColor);
    this.setSize(screenSize);
    this.setLocation(0, 0);
    this.setVisible(true);
  }

  protected void close() {
    this.dispose();
    //	    if (capWidth > 0 && capHeight > 0) {
    //	      Robot robot;
    //	      try {
    //	        robot = new Robot();
    //	        startX = startX - capWidth / BoardSyncTool.boardWidth;
    //	        startY = startY - capWidth / BoardSyncTool.boardHeight;
    //	        capWidth = capWidth + 2 * capWidth / BoardSyncTool.boardWidth;
    //	        capHeight = capHeight + 2 * capWidth / BoardSyncTool.boardHeight;
    //	        imageOut = robot.createScreenCapture(new Rectangle(startX, startY, capWidth,
    // capHeight));
    //	      } catch (AWTException e) {
    //	        // TODO Auto-generated catch block
    //	        e.printStackTrace();
    //	      }
    //	      BoardSyncTool.screenImage = imageOut;
    //	      BoardSyncTool.screenImageStartX = startX;
    //	      BoardSyncTool.screenImageStartY = startY;
    //	    }
    //	    this.dispose();
    //	    BoardSyncTool.isGettingScreen = false;
  }

  private void capture() {
    if (capFailed) quit();
    else close();
  }

  private void quit() {
    // TODO Auto-generated method stub
    this.dispose();
  }

  public BufferedImage screenCapture(Dimension screenSize) throws AWTException {
    Robot robot = new Robot();
    return robot.createScreenCapture(new Rectangle(0, 0, screenSize.width, screenSize.height));
  }
}
