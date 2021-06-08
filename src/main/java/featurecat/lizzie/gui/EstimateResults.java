package featurecat.lizzie.gui;

import featurecat.lizzie.Config;
import featurecat.lizzie.Lizzie;
import featurecat.lizzie.util.Utils;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.TexturePaint;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;

public class EstimateResults extends JDialog {
  private final ResourceBundle resourceBundle =
      Lizzie.config.useLanguage == 0
          ? ResourceBundle.getBundle("l10n.DisplayStrings")
          : (Lizzie.config.useLanguage == 1
              ? ResourceBundle.getBundle("l10n.DisplayStrings", new Locale("zh", "CN"))
              : ResourceBundle.getBundle("l10n.DisplayStrings", new Locale("en", "US")));
  public int allblackcounts = 0;
  public int allwhitecounts = 0;

  int blackPoints = 0;
  int whitePoints = 0;
  private boolean isBlackToPlay = true;

  int blackEat = 0;
  int whiteEat = 0;
  int blackAlives = 0;
  int whiteAlives = 0;
  int allBlackAreas = 0;
  int allWhiteAreas = 0;
  JPanel buttonpanel = new JPanel();
  // private BufferedImage cachedImage;
  public boolean iscounted = false;
  // public boolean isAutocounting = false;
  public JButton btnEstimate =
      new JButton(resourceBundle.getString("EstimateResults.estimate")); // "形式判断");
  public JButton btnAuto =
      new JButton(resourceBundle.getString("EstimateResults.autoEstimate")); // "自动判断");
  private JButton btnArea =
      new JButton(resourceBundle.getString("EstimateResults.areas")); // "数子模式");
  public JButton btnSettings;
  private BufferedImage cachedBackgroundImage;
  private TexturePaint paint;

  public EstimateResults(Window owner) {
    super(owner);
    getContentPane().add(buttonpanel, BorderLayout.SOUTH);
    this.setResizable(false);
    this.setTitle(resourceBundle.getString("EstimateResults.title")); // "Zen形式判断");
    if (Lizzie.config.estimateArea)
      btnArea.setText(resourceBundle.getString("EstimateResults.territory")); // "数目模式");
    this.addWindowListener(
        new WindowAdapter() {
          public void windowClosing(WindowEvent e) {
            setVisible(false);
            Lizzie.frame.clearKataEstimate();
            Lizzie.frame.refresh();
            Lizzie.frame.isCounting = false;
            iscounted = false;
            Lizzie.frame.refresh();
            btnAuto.setText(resourceBundle.getString("EstimateResults.autoEstimate")); // ""自动判断");
            Lizzie.frame.isAutocounting = false;
          }
        });
    btnEstimate.setMargin(new Insets(1, 2, 1, 2));
    btnAuto.setMargin(new Insets(1, 2, 1, 2));
    btnArea.setMargin(new Insets(1, 2, 1, 2));

    ImageIcon iconSettings = new ImageIcon();
    try {
      iconSettings.setImage(
          ImageIO.read(AnalysisFrame.class.getResourceAsStream("/assets/config.png")));
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    btnSettings = new JButton(iconSettings);
    btnSettings.setMargin(new Insets(1, 1, 1, 1));
    btnSettings.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            SetEstimateParam setEstimateParam = new SetEstimateParam();
            setEstimateParam.setVisible(true);
          }
        });

    //    boolean persisted = Lizzie.config.persistedUi != null;
    //    if (persisted
    //        && Lizzie.config.persistedUi.optJSONArray("movecount-position") != null
    //        && Lizzie.config.persistedUi.optJSONArray("movecount-position").length() == 2) {
    //      JSONArray pos = Lizzie.config.persistedUi.getJSONArray("movecount-position");
    //      setBounds(pos.getInt(0), pos.getInt(1), 255, 180);
    //    } else {
    //      Dimension screensize = Toolkit.getDefaultToolkit().getScreenSize();
    //      setBounds(0, (int) screensize.getHeight() / 2 - 125, 255, 180); // 240
    //    }
    Lizzie.setFrameSize(
        this,
        Lizzie.config.isChinese
            ? (Lizzie.javaVersion == 8 ? 255 : 245)
            : (Lizzie.javaVersion == 8 ? 265 : 255),
        201);

    this.setLocNearBoard();

    try {
      this.setIconImage(ImageIO.read(AnalysisFrame.class.getResourceAsStream("/assets/logo.png")));
    } catch (IOException e) {
      e.printStackTrace();
    }
    btnAuto.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            // Lizzie.frame.zen.noread = false;

            if (!Lizzie.frame.isAutocounting) {
              Lizzie.frame.isAutocounting = true;
              Lizzie.frame.zen.syncboradstat();
              Lizzie.frame.zen.countStones();
              btnAuto.setText(resourceBundle.getString("EstimateResults.stopEstimate")); // "停止判断");
            } else {
              Lizzie.frame.clearKataEstimate();
              Lizzie.frame.refresh();
              // Lizzie.frame.iscounting=false;
              btnAuto.setText(resourceBundle.getString("EstimateResults.autoEstimate")); // "自动判断");
              Lizzie.frame.isAutocounting = false;
            }
          }
        });
    btnEstimate.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            // Lizzie.frame.zen.noread = false;
            if (!iscounted) {
              Lizzie.frame.countstones(true);
              Lizzie.frame.isCounting = true;
              btnEstimate.setText(
                  resourceBundle.getString("EstimateResults.closeEstimate")); // "关闭判断");
            } else {
              Lizzie.frame.clearKataEstimate();
              Lizzie.frame.refresh();
              Lizzie.frame.isCounting = false;
              btnEstimate.setText(resourceBundle.getString("EstimateResults.estimate")); // "判断形势");
              repaint();
              // Lizzie.frame.setVisible(true);
            }
            iscounted = !iscounted;
          }
        });

    btnArea.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            // setSize(220,260);
            Lizzie.config.estimateArea = !Lizzie.config.estimateArea;
            if (Lizzie.config.estimateArea) {
              btnArea.setText(resourceBundle.getString("EstimateResults.territory"));
              if (!Lizzie.config.useZenEstimate)
                Lizzie.frame.zen.sendCommand("kata-set-rules chinese");
              // "数目模式");
            } else {
              btnArea.setText(resourceBundle.getString("EstimateResults.areas"));
              if (!Lizzie.config.useZenEstimate)
                Lizzie.frame.zen.sendCommand("kata-set-rules japanese");
              // "数子模式");
            }
            Lizzie.frame.countstones(true);
            Lizzie.frame.isCounting = true;
            Lizzie.config.uiConfig.put("estimate-area", Lizzie.config.estimateArea);
            repaint();
          }
        });
    btnAuto.setBounds(0, 240, 40, 20);
    btnArea.setBounds(60, 240, 40, 20);
    btnEstimate.setBounds(120, 240, 40, 20);
    buttonpanel.setBounds(0, 240, 60, 20);
    buttonpanel.add(btnEstimate);
    buttonpanel.add(btnAuto);
    buttonpanel.add(btnArea);
    // if (!Lizzie.config.useZenEstimate)
    buttonpanel.add(btnSettings);
    btnEstimate.setFocusable(false);
    btnAuto.setFocusable(false);
    btnArea.setFocusable(false);
    btnSettings.setFocusable(false);
  }

  public void showEstimate() {
    setLocNearBoard();
    this.setVisible(true);
  }

  private void setLocNearBoard() {
    // TODO Auto-generated method stub
    int width = this.getWidth();
    int height = this.getHeight();
    int frameX = Lizzie.frame.getX();
    int frameY = Lizzie.frame.getY();
    Dimension screensize = Toolkit.getDefaultToolkit().getScreenSize();
    int screenWidth = screensize.width;
    int screenHeight = screensize.height;
    int boardX = Lizzie.frame.boardX;
    int boardY =
        Lizzie.frame.boardY
            + Utils.zoomIn(Lizzie.frame.mainPanel.getY())
            + Config.menuHeight
            + Lizzie.frame.topPanel.getHeight();
    int boardLenght = Lizzie.frame.maxSize;
    if (Lizzie.frame.extraMode == 8 && Lizzie.frame.independentMainBoard != null) {
      frameX = Lizzie.frame.independentMainBoard.getX();
      frameY = Lizzie.frame.independentMainBoard.getY();
      boardX = 0;
      boardY = 0;
      boardLenght = Lizzie.frame.independentMainBoard.getWidth();
    }
    if (frameX + boardX + boardLenght + width + 5 <= screenWidth)
      this.setLocation(
          frameX + boardX + boardLenght + 5,
          Math.min(frameY + boardY + boardLenght / 2 - height * 2 / 3, screenHeight - height));
    else if (frameX + boardX - width >= 0)
      this.setLocation(
          frameX + boardX - width,
          Math.min(frameY + boardY + boardLenght / 2 - height * 2 / 3, screenHeight - height));
    else if (frameY + boardY - height > 0)
      this.setLocation(frameX + boardX + boardLenght / 2 - width / 2, frameY + boardY - height);
    else if (frameY + boardY + boardLenght + height <= screenHeight)
      this.setLocation(
          frameX + boardX + boardLenght / 2 - width / 2, frameY + boardY + boardLenght);
    else
      setLocation(
          screenWidth - width,
          Math.min(frameY + boardY + boardLenght / 2 - height * 2 / 3, screenHeight - height));
  }

  public void Counts(
      int blackEatCount,
      int whiteEatCount,
      int blackPrisonerCount,
      int whitePrisonerCount,
      int blackpont,
      int whitepoint,
      int blackAlive,
      int whiteAlive) {
    // synchronized (this) {
    allblackcounts = 0;
    allwhitecounts = 0;
    blackEat = 0;
    whiteEat = 0;
    this.isBlackToPlay = Lizzie.board.getHistory().getData().blackToPlay;

    allblackcounts = blackpont + blackEatCount + whitePrisonerCount;
    allwhitecounts = whitepoint + whiteEatCount + blackPrisonerCount;

    this.blackPoints = blackpont;
    this.whitePoints = whitepoint;

    allBlackAreas = blackAlive + blackpont;
    allWhiteAreas = whiteAlive + whitepoint;

    blackEat = blackEatCount;
    whiteEat = whiteEatCount;
    blackAlives = blackAlive;
    whiteAlives = whiteAlive;
    if (!Lizzie.frame.isAutocounting) {
      btnEstimate.setText(resourceBundle.getString("EstimateResults.closeEstimate")); // "关闭判断");
      iscounted = true;
    }

    repaint();
    //  }
  }

  public void paint(Graphics g) // 画图对象
      {
    int width = getWidth();
    int height = getHeight();
    int topCap = (int) ((Lizzie.sysScaleFactor - Lizzie.javaScaleFactor) * 20) + 5;
    Graphics2D g2 = (Graphics2D) g;
    // if (Lizzie.config.isScaled) {
    //     g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
    //     g2.setRenderingHint(KEY_ANTIALIASING, VALUE_ANTIALIAS_ON);
    //   }

    if (cachedBackgroundImage == null) {
      try {
        cachedBackgroundImage =
            ImageIO.read(getClass().getResourceAsStream("/assets/estimateBackground.png"));
        paint =
            new TexturePaint(
                cachedBackgroundImage,
                new Rectangle(
                    0, 0, cachedBackgroundImage.getWidth(), cachedBackgroundImage.getHeight()));
      } catch (Exception e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
        paint = null;
      }
    }
    if (paint != null) g2.setPaint(paint);
    else g2.setColor(Color.GRAY);
    g2.fill(new Rectangle(0, 0, width, height));

    // cachedImage = new BufferedImage(width, height, TYPE_INT_ARGB);
    // Graphics2D g2 = (Graphics2D) cachedImage.getGraphics();

    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    g2.setColor(Color.BLACK);
    g2.setStroke(new BasicStroke(2f));
    g2.fillOval(33, topCap + 61, 20, 20);
    // g2.drawOval(260,50, 32, 32);
    g2.setColor(Color.WHITE);
    g2.fillOval(202, topCap + 61, 20, 20);
    g2.setColor(Color.BLACK);
    int lead = Math.abs(allBlackAreas - allWhiteAreas);
    boolean estimateAreaBlackLead = false;
    if (Lizzie.config.estimateArea) {
      g2.setFont(new Font("SimHei", Font.PLAIN, 20));
      if (lead < 20) {
        if (allBlackAreas >= allWhiteAreas
            && allBlackAreas - (allWhiteAreas + (this.isBlackToPlay ? 0 : 1)) >= 0) {
          estimateAreaBlackLead = true;
          g2.setColor(Color.BLACK);
          g2.drawString(resourceBundle.getString("EstimateResults.Black"), 23, topCap + 51);
        } else {
          estimateAreaBlackLead = false;
          g2.setColor(Color.WHITE);
          g2.drawString(resourceBundle.getString("EstimateResults.White"), 23, topCap + 51);
        }
      } else if (lead < 100) {
        if (allBlackAreas >= allWhiteAreas
            && allBlackAreas - (allWhiteAreas + (this.isBlackToPlay ? 0 : 1)) >= 0) {
          estimateAreaBlackLead = true;
          g2.setColor(Color.BLACK);
          g2.drawString(resourceBundle.getString("EstimateResults.Black"), 19, topCap + 51);
        } else {
          estimateAreaBlackLead = false;
          g2.setColor(Color.WHITE);
          g2.drawString(resourceBundle.getString("EstimateResults.White"), 19, topCap + 51);
        }
      } else {
        if (allBlackAreas >= allWhiteAreas
            && allBlackAreas - (allWhiteAreas + (this.isBlackToPlay ? 0 : 1)) >= 0) {
          estimateAreaBlackLead = true;
          g2.setColor(Color.BLACK);
          g2.drawString(resourceBundle.getString("EstimateResults.Black"), 13, topCap + 51);
        } else {
          estimateAreaBlackLead = false;
          g2.setColor(Color.WHITE);
          g2.drawString(resourceBundle.getString("EstimateResults.White"), 13, topCap + 51);
        }
      }
    } else {
      g2.setFont(new Font("SimHei", Font.PLAIN, 23));
      if (allblackcounts >= allwhitecounts) {
        g2.setColor(Color.BLACK);
        g2.drawString(resourceBundle.getString("EstimateResults.Black"), 29, topCap + 52);
      } else {
        g2.setColor(Color.WHITE);
        g2.drawString(resourceBundle.getString("EstimateResults.White"), 29, topCap + 52);
      }
    }
    // allFont = new Font("allFont", Font.BOLD, 20);
    g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
    if (Lizzie.config.estimateArea) {
      g2.setFont(new Font("SimHei", Font.PLAIN, 17));

      if (lead < 20)
        g2.drawString(
            resourceBundle.getString("EstimateResults.lead")
                + lead / 2.0
                + resourceBundle.getString("EstimateResults.area")
                + "("
                + (estimateAreaBlackLead
                    ? allBlackAreas - (allWhiteAreas + (this.isBlackToPlay ? 0 : 1))
                    : (allWhiteAreas + (this.isBlackToPlay ? 0 : 1)) - allBlackAreas)
                + resourceBundle.getString("EstimateResults.pts")
                + ")",
            53,
            topCap + 50);
      else if (lead < 100)
        g2.drawString(
            resourceBundle.getString("EstimateResults.lead")
                + lead / 2.0
                + resourceBundle.getString("EstimateResults.area")
                + "("
                + (estimateAreaBlackLead
                    ? allBlackAreas - (allWhiteAreas + (this.isBlackToPlay ? 0 : 1))
                    : (allWhiteAreas + (this.isBlackToPlay ? 0 : 1)) - allBlackAreas)
                + resourceBundle.getString("EstimateResults.pts")
                + ")",
            48,
            topCap + 50);
      else
        g2.drawString(
            resourceBundle.getString("EstimateResults.lead")
                + lead / 2.0
                + resourceBundle.getString("EstimateResults.area")
                + "("
                + (estimateAreaBlackLead
                    ? allBlackAreas - (allWhiteAreas + (this.isBlackToPlay ? 0 : 1))
                    : (allWhiteAreas + (this.isBlackToPlay ? 0 : 1)) - allBlackAreas)
                + resourceBundle.getString("EstimateResults.pts")
                + ")",
            37,
            topCap + 50);

    } else {
      g2.setFont(new Font("SimHei", Font.PLAIN, 20));
      g2.drawString(
          "  "
              + resourceBundle.getString("EstimateResults.lead")
              + Math.abs(allblackcounts - allwhitecounts)
              + resourceBundle.getString("EstimateResults.points"),
          53,
          topCap + 51);
    }
    // allFont = new Font("allFont", Font.BOLD, 15);
    int sumB = 0;
    int sumW = 0;
    if (Lizzie.config.estimateArea) {
      sumB = blackAlives + blackPoints;
      sumW = whiteAlives + whitePoints;
    } else {
      sumB = allblackcounts + blackEat;
      sumW = allwhitecounts + whiteEat;
    }
    g2.setColor(Color.BLACK);
    g2.setFont(new Font("SimHei", Font.PLAIN, 17));
    g2.drawString(
        Lizzie.config.estimateArea
            ? resourceBundle.getString("EstimateResults.area2")
            : resourceBundle.getString("EstimateResults.points2"),
        Lizzie.config.isChinese ? 112 : 95,
        topCap + 104);
    g2.drawString(
        Lizzie.config.estimateArea
            ? resourceBundle.getString("EstimateResults.alives")
            : resourceBundle.getString("EstimateResults.captures"),
        Lizzie.config.isChinese ? 112 : 95,
        topCap + 130);
    g2.drawString(
        resourceBundle.getString("EstimateResults.sums"),
        Lizzie.config.isChinese ? 112 : 95,
        topCap + 156);

    if ((Lizzie.config.estimateArea ? blackPoints : allblackcounts) < 10)
      g2.drawString(
          (Lizzie.config.estimateArea ? blackPoints : allblackcounts) + "",
          37,
          topCap + 104); // 黑目数
    else if ((Lizzie.config.estimateArea ? blackPoints : allblackcounts) < 100)
      g2.drawString(
          (Lizzie.config.estimateArea ? blackPoints : allblackcounts) + "",
          33,
          topCap + 104); // 黑目数
    else
      g2.drawString(
          (Lizzie.config.estimateArea ? blackPoints : allblackcounts) + "",
          29,
          topCap + 104); // 黑目数

    if (Lizzie.config.estimateArea) {
      if (blackAlives < 10) g2.drawString(blackAlives + "", 37, topCap + 130); // 黑活子
      else if (blackAlives < 100) g2.drawString(blackAlives + "", 33, topCap + 130); // 黑活子
      else g2.drawString(blackAlives + "", 29, 130); // 黑活子
    } else {
      if (blackEat < 10) g2.drawString(blackEat + "", 37, topCap + 130); // 黑提子
      else if (blackEat < 100) g2.drawString(blackEat + "", 33, topCap + 130); // 黑提子
      else g2.drawString(blackEat + "", 29, 130); // 黑提子
    }

    if (sumB < 10) g2.drawString(sumB + "", 37, topCap + 156);
    else if (sumB < 100) g2.drawString(sumB + "", 33, topCap + 156);
    else g2.drawString(sumB + "", 29, topCap + 156);

    g2.setColor(Color.WHITE);

    if ((Lizzie.config.estimateArea ? whitePoints : allwhitecounts) < 10)
      g2.drawString(
          (Lizzie.config.estimateArea ? whitePoints : allwhitecounts) + "",
          206,
          topCap + 104); // 白目数
    else if ((Lizzie.config.estimateArea ? whitePoints : allwhitecounts) < 100)
      g2.drawString(
          (Lizzie.config.estimateArea ? whitePoints : allwhitecounts) + "",
          202,
          topCap + 104); // 白目数
    else
      g2.drawString(
          (Lizzie.config.estimateArea ? whitePoints : allwhitecounts) + "",
          198,
          topCap + 104); // 白目数

    if (Lizzie.config.estimateArea) {
      if (whiteAlives < 10) g2.drawString(whiteAlives + "", 206, topCap + 130); // 白活子
      else if (whiteAlives < 100) g2.drawString(whiteAlives + "", 202, topCap + 130); // 白活子
      else g2.drawString(whiteAlives + "", 198, topCap + 130); // 白活子
    } else {
      if (whiteEat < 10) g2.drawString(whiteEat + "", 206, topCap + 130); // 白提子
      else if (whiteEat < 100) g2.drawString(whiteEat + "", 202, topCap + 130); // 白提子
      else g2.drawString(whiteEat + "", 198, topCap + 130); // 白提子
    }

    if (sumW < 10) g2.drawString(sumW + "", 206, topCap + 156);
    else if (sumB < 100) g2.drawString(sumW + "", 202, topCap + 156);
    else g2.drawString(sumW + "", 198, topCap + 156);

    btnEstimate.repaint();
    btnAuto.repaint();
    btnArea.repaint();
    btnSettings.repaint();
  }

  //  private void invisiable() {
  //    this.setVisible(false);
  //  }
}
