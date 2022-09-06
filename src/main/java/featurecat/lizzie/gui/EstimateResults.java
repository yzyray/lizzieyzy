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
import java.util.ResourceBundle;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;

public class EstimateResults extends JDialog {
  private final ResourceBundle resourceBundle = Lizzie.resourceBundle;
  public int allBlackCounts = 0;
  public int allWhiteCounts = 0;

  int blackPoints = 0;
  int whitePoints = 0;
  private boolean isBlackToPlay = true;

  int blackAlives = 0;
  int whiteAlives = 0;
  int allBlackAreas = 0;
  int allWhiteAreas = 0;
  int blackTerritory = 0;
  int whiteTerritory = 0;
  int blackCaptured = 0;
  int whiteCaptured = 0;
  JPanel buttonpanel = new JPanel();
  // private BufferedImage cachedImage;
  public boolean iscounted = false;
  // public boolean isAutocounting = false;
  public JButton btnEstimate =
      new JButton(resourceBundle.getString("EstimateResults.estimate")); // "形式判断");
  public JButton btnAuto =
      new JButton(resourceBundle.getString("EstimateResults.autoEstimate")); // "自动判断");
  private JButton btnArea =
      new JButton(resourceBundle.getString("EstimateResults.areaMode")); // "数子模式");
  public JButton btnSettings;
  private BufferedImage cachedBackgroundImage;
  private TexturePaint paint;

  public EstimateResults(Window owner) {
    super(owner);
    if (owner == null) setAlwaysOnTop(true);
    getContentPane().add(buttonpanel, BorderLayout.SOUTH);
    this.setResizable(false);
    this.setTitle(resourceBundle.getString("EstimateResults.title")); // "Zen形式判断");
    if (Lizzie.config.estimateArea)
      btnArea.setText(resourceBundle.getString("EstimateResults.territoryMode")); // "数目模式");
    this.addWindowListener(
        new WindowAdapter() {
          public void windowClosing(WindowEvent e) {
            setVisible(false);
            Lizzie.frame.clearEstimate();
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
      iconSettings.setImage(ImageIO.read(getClass().getResourceAsStream("/assets/config.png")));
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
      this.setIconImage(ImageIO.read(getClass().getResourceAsStream("/assets/logo.png")));
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
              Lizzie.frame.clearEstimate();
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
              Lizzie.frame.clearEstimate();
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
              btnArea.setText(resourceBundle.getString("EstimateResults.territoryMode"));
              if (!Lizzie.config.useZenEstimate)
                Lizzie.frame.zen.sendCommand("kata-set-rules chinese");
              // "数目模式");
            } else {
              btnArea.setText(resourceBundle.getString("EstimateResults.areaMode"));
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
    int width = this.getWidth();
    int height = this.getHeight();
    int frameX = Lizzie.frame.getX();
    int frameY = Lizzie.frame.getY();
    Dimension screensize = Toolkit.getDefaultToolkit().getScreenSize();
    int screenWidth = screensize.width;
    int screenHeight = screensize.height;
    int boardX = Utils.zoomIn(Lizzie.frame.boardX);
    int boardY =
        Utils.zoomIn(Lizzie.frame.boardY + Lizzie.frame.mainPanel.getY())
            + Config.menuHeight
            + Lizzie.frame.topPanel.getHeight();
    int boardLenght = Utils.zoomIn(Lizzie.frame.maxSize);
    if (Lizzie.config.isFloatBoardMode() && Lizzie.frame.independentMainBoard != null) {
      frameX = Lizzie.frame.independentMainBoard.getX();
      frameY = Lizzie.frame.independentMainBoard.getY();
      boardX = 0;
      boardY = 0;
      boardLenght = Lizzie.frame.independentMainBoard.getWidth();
    }
    if (Lizzie.frame.floatBoard != null
        && Lizzie.frame.floatBoard.isVisible()
        && !Lizzie.frame.mainPanel.hasFocus()) {
      if (!(Lizzie.config.isFloatBoardMode()
          && Lizzie.frame.independentMainBoard != null
          && Lizzie.frame.independentMainBoard.hasFocus())) {
        frameX = Lizzie.frame.floatBoard.getX();
        frameY = Lizzie.frame.floatBoard.getY();
        boardX = 0;
        boardY = 0;
        boardLenght = Lizzie.frame.floatBoard.getWidth();
      }
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
      int blackPoints,
      int whitePoints,
      int blackAlive,
      int whiteAlive) {
    // synchronized (this) {
    allBlackCounts = 0;
    allWhiteCounts = 0;
    this.isBlackToPlay = Lizzie.board.getHistory().getData().blackToPlay;

    allBlackCounts = blackPoints + blackEatCount + whitePrisonerCount;
    allWhiteCounts = whitePoints + whiteEatCount + blackPrisonerCount;

    this.blackPoints = blackPoints;
    this.whitePoints = whitePoints;

    allBlackAreas = blackAlive + blackPoints;
    allWhiteAreas = whiteAlive + whitePoints;

    blackAlives = blackAlive;
    whiteAlives = whiteAlive;

    blackTerritory = blackPoints;
    whiteTerritory = whitePoints;
    blackCaptured = blackPrisonerCount + whiteEatCount;
    whiteCaptured = whitePrisonerCount + blackEatCount;
    if (!Lizzie.frame.isAutocounting) {
      btnEstimate.setText(resourceBundle.getString("EstimateResults.closeEstimate")); // "关闭判断");
      iscounted = true;
    }

    repaint();
    //  }
  }

  @Override
  public void paint(Graphics g) // 画图对象
      {
    int width = getWidth();
    int height = getHeight();
    int topCap = (int) ((Lizzie.sysScaleFactor - Lizzie.javaScaleFactor) * 20) + 5;
    Graphics2D g2 = (Graphics2D) g;
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
      g2.setFont(
          new Font(Lizzie.config.isChinese ? "SimHei" : Lizzie.config.uiFontName, Font.PLAIN, 20));
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
      g2.setFont(
          new Font(Lizzie.config.isChinese ? "SimHei" : Lizzie.config.uiFontName, Font.PLAIN, 23));
      if (allBlackCounts >= allWhiteCounts) {
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
      g2.setFont(
          new Font(Lizzie.config.isChinese ? "SimHei" : Lizzie.config.uiFontName, Font.PLAIN, 17));

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
      g2.setFont(
          new Font(Lizzie.config.isChinese ? "SimHei" : Lizzie.config.uiFontName, Font.PLAIN, 20));
      g2.drawString(
          "  "
              + resourceBundle.getString("EstimateResults.lead")
              + Math.abs(allBlackCounts - allWhiteCounts)
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
      sumB = blackTerritory - blackCaptured;
      sumW = whiteTerritory - whiteCaptured;
    }
    g2.setColor(Color.BLACK);
    g2.setFont(
        new Font(Lizzie.config.isChinese ? "SimHei" : Lizzie.config.uiFontName, Font.PLAIN, 17));
    g2.drawString(
        Lizzie.config.estimateArea
            ? resourceBundle.getString("EstimateResults.areaPoints")
            : resourceBundle.getString("EstimateResults.territoryPoints"),
        Lizzie.config.isChinese ? 112 : 95,
        topCap + 104);
    g2.drawString(
        Lizzie.config.estimateArea
            ? resourceBundle.getString("EstimateResults.areaAlives")
            : resourceBundle.getString("EstimateResults.territoryCaptured"),
        Lizzie.config.isChinese ? 112 : 95,
        topCap + 130);
    g2.drawString(
        Lizzie.config.estimateArea
            ? resourceBundle.getString("EstimateResults.areaSums")
            : resourceBundle.getString("EstimateResults.territoryDifference"),
        Lizzie.config.isChinese ? 112 : 95,
        topCap + 156);

    if ((Lizzie.config.estimateArea ? blackPoints : blackTerritory) < 10)
      g2.drawString(
          (String.valueOf(Lizzie.config.estimateArea ? blackPoints : blackTerritory)),
          37,
          topCap + 104); // 黑目数
    else if ((Lizzie.config.estimateArea ? blackPoints : blackTerritory) < 100)
      g2.drawString(
          (String.valueOf(Lizzie.config.estimateArea ? blackPoints : blackTerritory)),
          33,
          topCap + 104); // 黑目数
    else
      g2.drawString(
          (String.valueOf(Lizzie.config.estimateArea ? blackPoints : blackTerritory)),
          29,
          topCap + 104); // 黑目数

    if (Lizzie.config.estimateArea) {
      if (blackAlives < 10) g2.drawString(String.valueOf(blackAlives), 37, topCap + 130); // 黑活子
      else if (blackAlives < 100)
        g2.drawString(String.valueOf(blackAlives), 33, topCap + 130); // 黑活子
      else g2.drawString(String.valueOf(blackAlives), 29, topCap + 130); // 黑活子
    } else {
      if (blackCaptured < 10) g2.drawString(String.valueOf(blackCaptured), 37, topCap + 130); // 黑提子
      else if (blackCaptured < 100)
        g2.drawString(String.valueOf(blackCaptured), 33, topCap + 130); // 黑提子
      else g2.drawString(String.valueOf(blackCaptured), 29, topCap + 130); // 黑提子
    }

    if (sumB < 10) g2.drawString(String.valueOf(sumB), 37, topCap + 156);
    else if (sumB < 100) g2.drawString(String.valueOf(sumB), 33, topCap + 156);
    else g2.drawString(String.valueOf(sumB), 29, topCap + 156);

    g2.setColor(Color.WHITE);

    if ((Lizzie.config.estimateArea ? whitePoints : whiteTerritory) < 10)
      g2.drawString(
          (String.valueOf(Lizzie.config.estimateArea ? whitePoints : whiteTerritory)),
          206,
          topCap + 104); // 白目数
    else if ((Lizzie.config.estimateArea ? whitePoints : whiteTerritory) < 100)
      g2.drawString(
          (String.valueOf(Lizzie.config.estimateArea ? whitePoints : whiteTerritory)),
          202,
          topCap + 104); // 白目数
    else
      g2.drawString(
          (String.valueOf(Lizzie.config.estimateArea ? whitePoints : whiteTerritory)),
          198,
          topCap + 104); // 白目数

    if (Lizzie.config.estimateArea) {
      if (whiteAlives < 10) g2.drawString(String.valueOf(whiteAlives), 206, topCap + 130); // 白活子
      else if (whiteAlives < 100)
        g2.drawString(String.valueOf(whiteAlives), 202, topCap + 130); // 白活子
      else g2.drawString(String.valueOf(whiteAlives), 198, topCap + 130); // 白活子
    } else {
      if (whiteCaptured < 10)
        g2.drawString(String.valueOf(whiteCaptured), 206, topCap + 130); // 白提子
      else if (whiteCaptured < 100)
        g2.drawString(String.valueOf(whiteCaptured), 202, topCap + 130); // 白提子
      else g2.drawString(String.valueOf(whiteCaptured), 198, topCap + 130); // 白提子
    }

    if (sumW < 10) g2.drawString(String.valueOf(sumW), 206, topCap + 156);
    else if (sumW < 100) g2.drawString(String.valueOf(sumW), 202, topCap + 156);
    else g2.drawString(String.valueOf(sumW), 198, topCap + 156);

    btnEstimate.repaint();
    btnAuto.repaint();
    btnArea.repaint();
    btnSettings.repaint();
  }

  //  private void invisiable() {
  //    this.setVisible(false);
  //  }
}
