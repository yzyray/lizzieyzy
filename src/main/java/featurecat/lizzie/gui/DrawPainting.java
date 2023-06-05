package featurecat.lizzie.gui;

import static java.awt.RenderingHints.KEY_ANTIALIASING;
import static java.awt.RenderingHints.VALUE_ANTIALIAS_ON;
import static java.awt.image.BufferedImage.TYPE_INT_ARGB;

import featurecat.lizzie.Config;
import featurecat.lizzie.Lizzie;
import featurecat.lizzie.util.Utils;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

public class DrawPainting extends JDialog {
  private BufferedImage cachedImage;
  private List<DrawPoint> list = new ArrayList<DrawPoint>();
  private List<List<DrawPoint>> drawlist = new ArrayList<List<DrawPoint>>();
  private JPanel mainPanel;
  private JPanel btnPanel;
  private int totalWidth, totalHeight;
  private int startX, startY;

  public enum PEN_COLOR {
    BLUEPEN,
    GREENPEN,
    REDPEN;
  }

  private PEN_COLOR colorIndex = PEN_COLOR.BLUEPEN; // 0=蓝
  private Color backgroundColor = new Color(255, 255, 255, 1);
  private JButton btnRed;
  private JButton btnBlue;
  private JButton btnGreen;
  private ImageIcon iconGreen;
  private ImageIcon iconRed;
  private ImageIcon iconBlue;
  private ImageIcon iconGreen2;
  private ImageIcon iconRed2;
  private ImageIcon iconBlue2;

  public DrawPainting(int x, int y, int width, int height) {
    totalWidth = width;
    totalHeight = height;
    startX = x;
    startY = y;
    setSize(totalWidth, totalHeight);
    setUndecorated(true);
    setBackground(backgroundColor);
    setResizable(false);
    setLocation(startX, startY);
    switch (Lizzie.config.lastPaintingColor) {
      case 0:
        colorIndex = PEN_COLOR.BLUEPEN;
        break;
      case 1:
        colorIndex = PEN_COLOR.GREENPEN;
        break;
      case 2:
        colorIndex = PEN_COLOR.REDPEN;
        break;
    }

    Toolkit tk = Toolkit.getDefaultToolkit();
    try {
      Image image = ImageIO.read(getClass().getResourceAsStream("/assets/paint2.png"));
      Cursor cursor = tk.createCustomCursor(image, new Point(0, 0), "norm");
      this.setCursor(cursor);
    } catch (IOException e1) {
      // TODO Auto-generated catch block
      e1.printStackTrace();
    }

    mainPanel = new JPanel();
    mainPanel.setBackground(new Color(0, 0, 0, 0));
    mainPanel.enableInputMethods(false);
    getContentPane().enableInputMethods(false);
    getContentPane().setLayout(new BorderLayout());
    getContentPane().add(mainPanel, BorderLayout.CENTER);

    addKeyListener(
        new KeyAdapter() {
          public void keyPressed(KeyEvent e) {
            if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
              saveLastColor();
              setVisible(false);
              dispose();
            }
          }
        });

    addMouseListener(
        new MouseAdapter() {
          public void mouseReleased(MouseEvent e) {
            if (e.getButton() == MouseEvent.BUTTON1) // left click
            {
              if (list.size() > 0) {
                drawlist.add(list);
                list = new ArrayList<DrawPoint>();
              }
            }
          }
        });

    addMouseMotionListener(
        new MouseMotionListener() {
          @Override
          public void mouseDragged(MouseEvent e) {
            // TODO Auto-generated method stub
            //  System.out.println("1");
            DrawPoint p1 =
                new DrawPoint((Utils.zoomOut(e.getX())), (Utils.zoomOut(e.getY())), colorIndex);
            list.add(p1);
            draw();
          }

          @Override
          public void mouseMoved(MouseEvent arg0) {
            // TODO Auto-generated method stub

          }
        });
    btnPanel = new JPanel();
    btnPanel.setBackground(new Color(0, 0, 0, 0));
    btnPanel.setLayout(new FlowLayout(FlowLayout.RIGHT, 0, 0));
    getContentPane().add(btnPanel, BorderLayout.NORTH);

    ImageIcon iconClose = new ImageIcon();
    ImageIcon iconRevoke = new ImageIcon();
    ImageIcon iconEmpty = new ImageIcon();
    iconGreen = new ImageIcon();
    iconRed = new ImageIcon();
    iconBlue = new ImageIcon();
    iconGreen2 = new ImageIcon();
    iconRed2 = new ImageIcon();
    iconBlue2 = new ImageIcon();
    try {
      iconClose.setImage(
          ImageIO.read(getClass().getResourceAsStream("/assets/drclose.png"))
              .getScaledInstance(
                  Config.menuIconSize * 2, Config.menuIconSize * 2, java.awt.Image.SCALE_SMOOTH));
      iconRevoke.setImage(
          ImageIO.read(getClass().getResourceAsStream("/assets/backmain.png"))
              .getScaledInstance(
                  Config.menuIconSize * 2, Config.menuIconSize * 2, java.awt.Image.SCALE_SMOOTH));
      iconRed.setImage(
          ImageIO.read(getClass().getResourceAsStream("/assets/red.png"))
              .getScaledInstance(
                  Config.menuIconSize * 2, Config.menuIconSize * 2, java.awt.Image.SCALE_SMOOTH));
      iconRed2.setImage(
          ImageIO.read(getClass().getResourceAsStream("/assets/red2.png"))
              .getScaledInstance(
                  Config.menuIconSize * 2, Config.menuIconSize * 2, java.awt.Image.SCALE_SMOOTH));
      iconGreen.setImage(
          ImageIO.read(getClass().getResourceAsStream("/assets/green.png"))
              .getScaledInstance(
                  Config.menuIconSize * 2, Config.menuIconSize * 2, java.awt.Image.SCALE_SMOOTH));
      iconGreen2.setImage(
          ImageIO.read(getClass().getResourceAsStream("/assets/green2.png"))
              .getScaledInstance(
                  Config.menuIconSize * 2, Config.menuIconSize * 2, java.awt.Image.SCALE_SMOOTH));
      iconBlue.setImage(
          ImageIO.read(getClass().getResourceAsStream("/assets/blue.png"))
              .getScaledInstance(
                  Config.menuIconSize * 2, Config.menuIconSize * 2, java.awt.Image.SCALE_SMOOTH));
      iconBlue2.setImage(
          ImageIO.read(getClass().getResourceAsStream("/assets/blue2.png"))
              .getScaledInstance(
                  Config.menuIconSize * 2, Config.menuIconSize * 2, java.awt.Image.SCALE_SMOOTH));
      iconEmpty.setImage(
          ImageIO.read(getClass().getResourceAsStream("/assets/drempty.png"))
              .getScaledInstance(
                  Config.menuIconSize * 2, Config.menuIconSize * 2, java.awt.Image.SCALE_SMOOTH));

    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

    btnBlue = new JButton(iconBlue);
    btnBlue.setFocusable(false);
    btnBlue.setBorder(new EmptyBorder(5, 5, 5, 5));
    btnPanel.add(btnBlue);
    btnBlue.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            colorIndex = PEN_COLOR.BLUEPEN;
            setColorButton();
          }
        });

    btnGreen = new JButton(iconGreen);
    btnGreen.setFocusable(false);
    btnGreen.setBorder(new EmptyBorder(5, 5, 5, 5));
    btnPanel.add(btnGreen);
    btnGreen.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            colorIndex = PEN_COLOR.GREENPEN;
            setColorButton();
          }
        });

    btnRed = new JButton(iconRed);
    btnRed.setFocusable(false);
    btnRed.setBorder(new EmptyBorder(5, 5, 5, 5));
    btnPanel.add(btnRed);
    btnRed.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            colorIndex = PEN_COLOR.REDPEN;
            setColorButton();
          }
        });
    setColorButton();

    JButton btnRevoke = new JButton(iconRevoke);
    btnRevoke.setFocusable(false);
    btnRevoke.setBorder(new EmptyBorder(5, 5, 5, 5));
    btnPanel.add(btnRevoke);
    btnRevoke.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            if (drawlist.size() > 0) {
              drawlist.remove(drawlist.size() - 1);
              draw();
            }
          }
        });

    JButton btnEmpty = new JButton(iconEmpty);
    btnEmpty.setFocusable(false);
    btnEmpty.setBorder(new EmptyBorder(5, 5, 5, 5));
    btnPanel.add(btnEmpty);
    btnEmpty.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            list.clear();
            drawlist.clear();
            draw();
          }
        });

    JButton btnExit = new JButton(iconClose);
    btnExit.setToolTipText("ESC");
    btnExit.setFocusable(false);
    btnExit.setBorder(new EmptyBorder(5, 5, 5, 5));
    btnPanel.add(btnExit);
    btnExit.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            saveLastColor();
            setVisible(false);
            dispose();
          }
        });
  }

  protected void saveLastColor() {
    // TODO Auto-generated method stub
    switch (colorIndex) {
      case BLUEPEN:
        Lizzie.config.lastPaintingColor = 0;
        break;
      case GREENPEN:
        Lizzie.config.lastPaintingColor = 1;
        break;
      case REDPEN:
        Lizzie.config.lastPaintingColor = 2;
        break;
    }
    Lizzie.config.uiConfig.put("last-painting-color", Lizzie.config.lastPaintingColor);
  }

  protected void setColorButton() {
    // TODO Auto-generated method stub
    switch (colorIndex) {
      case BLUEPEN:
        btnBlue.setIcon(iconBlue2);
        btnGreen.setIcon(iconGreen);
        btnRed.setIcon(iconRed);
        break;
      case GREENPEN:
        btnBlue.setIcon(iconBlue);
        btnGreen.setIcon(iconGreen2);
        btnRed.setIcon(iconRed);
        break;
      case REDPEN:
        btnBlue.setIcon(iconBlue);
        btnGreen.setIcon(iconGreen);
        btnRed.setIcon(iconRed2);
        break;
    }
  }

  private void draw() {
    cachedImage =
        new BufferedImage(Utils.zoomOut(totalWidth), Utils.zoomOut(totalHeight), TYPE_INT_ARGB);
    Graphics2D g0 = (Graphics2D) cachedImage.getGraphics();
    g0.setBackground(backgroundColor);
    g0.clearRect(0, 0, cachedImage.getWidth(), cachedImage.getHeight());
    g0.setRenderingHint(KEY_ANTIALIASING, VALUE_ANTIALIAS_ON);
    g0.setStroke(new BasicStroke(3));

    for (int s = 0; s < drawlist.size(); s++) {
      List<DrawPoint> list = drawlist.get(s);

      for (int i = 1; i < list.size(); i++) {
        int x0 = list.get(i - 1).x;
        int y0 = list.get(i - 1).y;
        int x1 = list.get(i).x;
        int y1 = list.get(i).y;
        setColor(list.get(i).color, g0);
        g0.drawLine(x1, y1, x0, y0);
      }
    }
    for (int i = 1; i < list.size(); i++) {
      int x0 = list.get(i - 1).x;
      int y0 = list.get(i - 1).y;
      int x1 = list.get(i).x;
      int y1 = list.get(i).y;
      setColor(list.get(i).color, g0);
      g0.drawLine(x1, y1, x0, y0);
    }
    g0.dispose();
    repaint();
  }

  private void setColor(PEN_COLOR color, Graphics2D g0) {
    // TODO Auto-generated method stub
    switch (color) {
      case BLUEPEN:
        g0.setColor(Color.BLUE);
        break;
      case GREENPEN:
        g0.setColor(Color.GREEN);
        break;
      case REDPEN:
        g0.setColor(Color.RED);
        break;
    }
  }

  @Override
  public void paint(Graphics g) {
    super.paint(g);
    // ((Graphics2D) g).setComposite(AlphaComposite.getInstance(AlphaComposite.SRC));
    if (cachedImage != null) {
      g.drawImage(cachedImage, 0, 0, this);
    }
    btnPanel.repaint();
  }

  public class DrawPoint {
    public int x, y;
    public PEN_COLOR color;

    public DrawPoint(int x, int y, PEN_COLOR color) {
      this.x = x;
      this.y = y;
      this.color = color;
    }
  }
}
