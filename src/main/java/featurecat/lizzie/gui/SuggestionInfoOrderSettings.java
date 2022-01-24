package featurecat.lizzie.gui;

import static java.awt.RenderingHints.KEY_INTERPOLATION;
import static java.awt.RenderingHints.VALUE_INTERPOLATION_BILINEAR;
import static java.awt.image.BufferedImage.TYPE_INT_ARGB;
import static java.lang.Math.min;
import static java.lang.Math.round;

import featurecat.lizzie.Config;
import featurecat.lizzie.Lizzie;
import featurecat.lizzie.analysis.MoveData;
import featurecat.lizzie.rules.Board;
import featurecat.lizzie.util.Utils;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.TexturePaint;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.font.TextAttribute;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import javax.imageio.ImageIO;
import javax.swing.ButtonGroup;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.Timer;

public class SuggestionInfoOrderSettings extends JDialog {
  private PanelWithToolTips middlePanel;
  private PanelWithToolTips infoPanel;
  private Timer timer;
  private JPanel previewPanel;
  private JFontRadioButton rdoWinrate3;
  private JFontRadioButton rdoWinrate2;
  private JFontRadioButton rdoWinrate1;

  private JFontRadioButton rdoPlayouts3;
  private JFontRadioButton rdoPlayouts2;
  private JFontRadioButton rdoPlayouts1;

  private JFontRadioButton rdoScoreLead3;
  private JFontRadioButton rdoScoreLead2;
  private JFontRadioButton rdoScoreLead1;

  private JFontCheckBox chkWinrate;
  private JFontCheckBox chkPlayouts;
  private JFontCheckBox chkScoreLead;

  private int currentSuggestionInfoWinrate = Lizzie.config.suggestionInfoWinrate;
  private int currentSuggestionInfoPlayouts = Lizzie.config.suggestionInfoPlayouts;
  private int currentSuggestionInfoScoreLead = Lizzie.config.suggestionInfoScoreLead;
  private boolean currentShowWinrateInSuggestion = Lizzie.config.showWinrateInSuggestion;
  private boolean currentShowPlayoutsInSuggestion = Lizzie.config.showPlayoutsInSuggestion;
  private boolean currentShowScoremeanInSuggestion = Lizzie.config.showScoremeanInSuggestion;

  public SuggestionInfoOrderSettings(Window owner) {
    super(owner);
    setTitle(Lizzie.resourceBundle.getString("SuggestionInfoOrderSettings.title"));
    setResizable(false);
    getContentPane().setLayout(null);
    infoPanel = new PanelWithToolTips();
    infoPanel.setLayout(null);
    infoPanel.setBounds(0, 200, 500, 160);
    middlePanel = new PanelWithToolTips();
    middlePanel.setLayout(null);
    middlePanel.setBounds(0, 170, 500, 30);

    previewPanel =
        new JPanel(true) {
          @Override
          protected void paintComponent(Graphics g) {
            if (Config.isScaled) {
              Graphics2D g1 = (Graphics2D) g;
              g1.scale(1.0 / Lizzie.javaScaleFactor, 1.0 / Lizzie.javaScaleFactor);
            }
            paintMianPanel(g);
          }
        };
    previewPanel.setBounds(0, 0, 500, 170);
    getContentPane().add(infoPanel);
    getContentPane().add(middlePanel);
    getContentPane().add(previewPanel);

    JFontLabel lblSuggestionInfo =
        new JFontLabel(
            Lizzie.resourceBundle.getString("SuggestionInfoOrderSettings.lblSuggestionInfo"));
    lblSuggestionInfo.setBounds(10, 7, 124, 25);
    middlePanel.add(lblSuggestionInfo);

    chkWinrate =
        new JFontCheckBox(
            Lizzie.resourceBundle.getString("SuggestionInfoOrderSettings.rdoWinrate"));
    chkWinrate.setBounds(114, 8, 103, 23);
    middlePanel.add(chkWinrate);

    chkPlayouts =
        new JFontCheckBox(
            Lizzie.resourceBundle.getString("SuggestionInfoOrderSettings.rdoPlayouts"));
    chkPlayouts.setBounds(230, 8, 113, 23);
    middlePanel.add(chkPlayouts);

    chkScoreLead =
        new JFontCheckBox(
            Lizzie.resourceBundle.getString("SuggestionInfoOrderSettings.rdoScoreLead"));
    chkScoreLead.setBounds(360, 8, 121, 23);
    middlePanel.add(chkScoreLead);

    chkWinrate.setSelected(Lizzie.config.showWinrateInSuggestion);
    chkPlayouts.setSelected(Lizzie.config.showPlayoutsInSuggestion);
    chkScoreLead.setSelected(Lizzie.config.showScoremeanInSuggestion);

    JFontLabel lblFirstRow =
        new JFontLabel(Lizzie.resourceBundle.getString("SuggestionInfoOrderSettings.lblFirstRow"));
    lblFirstRow.setBounds(10, 5, 124, 25);
    infoPanel.add(lblFirstRow);

    JFontLabel lblSecondRow =
        new JFontLabel(Lizzie.resourceBundle.getString("SuggestionInfoOrderSettings.lblSecondRow"));
    lblSecondRow.setBounds(10, 35, 124, 25);
    infoPanel.add(lblSecondRow);

    JFontLabel lblThirdRow =
        new JFontLabel(Lizzie.resourceBundle.getString("SuggestionInfoOrderSettings.lblThirdRow"));
    lblThirdRow.setBounds(10, 65, 124, 25);
    infoPanel.add(lblThirdRow);

    rdoWinrate3 =
        new JFontRadioButton(
            Lizzie.resourceBundle.getString("SuggestionInfoOrderSettings.rdoWinrate"));
    rdoWinrate3.setBounds(114, 68, 103, 23);
    infoPanel.add(rdoWinrate3);

    rdoPlayouts3 =
        new JFontRadioButton(
            Lizzie.resourceBundle.getString("SuggestionInfoOrderSettings.rdoPlayouts"));
    rdoPlayouts3.setBounds(230, 68, 113, 23);
    infoPanel.add(rdoPlayouts3);

    rdoScoreLead3 =
        new JFontRadioButton(
            Lizzie.resourceBundle.getString("SuggestionInfoOrderSettings.rdoScoreLead"));
    rdoScoreLead3.setBounds(360, 68, 121, 23);
    infoPanel.add(rdoScoreLead3);

    rdoWinrate2 =
        new JFontRadioButton(
            Lizzie.resourceBundle.getString("SuggestionInfoOrderSettings.rdoWinrate"));
    rdoWinrate2.setBounds(114, 38, 103, 23);
    infoPanel.add(rdoWinrate2);

    rdoPlayouts2 =
        new JFontRadioButton(
            Lizzie.resourceBundle.getString("SuggestionInfoOrderSettings.rdoPlayouts"));
    rdoPlayouts2.setBounds(230, 38, 113, 23);
    infoPanel.add(rdoPlayouts2);

    rdoScoreLead2 =
        new JFontRadioButton(
            Lizzie.resourceBundle.getString("SuggestionInfoOrderSettings.rdoScoreLead"));
    rdoScoreLead2.setBounds(360, 38, 121, 23);
    infoPanel.add(rdoScoreLead2);

    rdoWinrate1 =
        new JFontRadioButton(
            Lizzie.resourceBundle.getString("SuggestionInfoOrderSettings.rdoWinrate"));
    rdoWinrate1.setBounds(114, 8, 103, 23);
    infoPanel.add(rdoWinrate1);

    rdoPlayouts1 =
        new JFontRadioButton(
            Lizzie.resourceBundle.getString("SuggestionInfoOrderSettings.rdoPlayouts"));
    rdoPlayouts1.setBounds(230, 8, 113, 23);
    infoPanel.add(rdoPlayouts1);

    rdoScoreLead1 =
        new JFontRadioButton(
            Lizzie.resourceBundle.getString("SuggestionInfoOrderSettings.rdoScoreLead"));
    rdoScoreLead1.setBounds(360, 8, 121, 23);
    infoPanel.add(rdoScoreLead1);

    ButtonGroup group1 = new ButtonGroup();
    group1.add(rdoWinrate1);
    group1.add(rdoPlayouts1);
    group1.add(rdoScoreLead1);

    ButtonGroup group2 = new ButtonGroup();
    group2.add(rdoWinrate2);
    group2.add(rdoPlayouts2);
    group2.add(rdoScoreLead2);

    ButtonGroup group3 = new ButtonGroup();
    group3.add(rdoWinrate3);
    group3.add(rdoPlayouts3);
    group3.add(rdoScoreLead3);

    switch (Lizzie.config.suggestionInfoWinrate) {
      case 1:
        rdoWinrate1.setSelected(true);
        break;
      case 2:
        rdoWinrate2.setSelected(true);
        break;
      case 3:
        rdoWinrate3.setSelected(true);
        break;
    }
    switch (Lizzie.config.suggestionInfoPlayouts) {
      case 1:
        rdoPlayouts1.setSelected(true);
        break;
      case 2:
        rdoPlayouts2.setSelected(true);
        break;
      case 3:
        rdoPlayouts3.setSelected(true);
        break;
    }
    switch (Lizzie.config.suggestionInfoScoreLead) {
      case 1:
        rdoScoreLead1.setSelected(true);
        break;
      case 2:
        rdoScoreLead2.setSelected(true);
        break;
      case 3:
        rdoScoreLead3.setSelected(true);
        break;
    }
    JFontButton okButton =
        new JFontButton(Lizzie.resourceBundle.getString("SuggestionInfoOrderSettings.okButton"));
    okButton.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            Lizzie.config.showWinrateInSuggestion = chkWinrate.isSelected();
            Lizzie.config.showPlayoutsInSuggestion = chkPlayouts.isSelected();
            Lizzie.config.showScoremeanInSuggestion = chkScoreLead.isSelected();
            Lizzie.config.uiConfig.put(
                "show-winrate-in-suggestion", Lizzie.config.showWinrateInSuggestion);
            Lizzie.config.uiConfig.put(
                "show-playouts-in-suggestion", Lizzie.config.showPlayoutsInSuggestion);
            Lizzie.config.uiConfig.put(
                "show-scoremean-in-suggestion", Lizzie.config.showScoremeanInSuggestion);
            int winrateOrder = -1, playoutsOrder = -1, scoreLeadOrder = -1;
            if (rdoWinrate1.isSelected()) winrateOrder = 1;
            else if (rdoWinrate2.isSelected()) winrateOrder = 2;
            else if (rdoWinrate3.isSelected()) winrateOrder = 3;

            if (rdoPlayouts1.isSelected()) playoutsOrder = 1;
            else if (rdoPlayouts2.isSelected()) playoutsOrder = 2;
            else if (rdoPlayouts3.isSelected()) playoutsOrder = 3;

            if (rdoScoreLead1.isSelected()) scoreLeadOrder = 1;
            else if (rdoScoreLead2.isSelected()) scoreLeadOrder = 2;
            else if (rdoScoreLead3.isSelected()) scoreLeadOrder = 3;

            if (winrateOrder < 0
                || playoutsOrder < 0
                || scoreLeadOrder < 0
                || winrateOrder == playoutsOrder
                || playoutsOrder == scoreLeadOrder
                || scoreLeadOrder == winrateOrder) {
              Utils.showMsg(
                  Lizzie.resourceBundle.getString("SuggestionInfoOrderSettings.wrongSettingsHint"));
            } else {
              Lizzie.config.setSuggestionInfoOrdr(winrateOrder, playoutsOrder, scoreLeadOrder);
              setVisible(false);
              Lizzie.frame.refresh();
            }
            if (Lizzie.frame != null && LizzieFrame.menu != null)
              LizzieFrame.menu.refreshDoubleMoveInfoStatus();
            if (Lizzie.frame.configDialog2 != null && Lizzie.frame.configDialog2.isVisible()) {
              Lizzie.frame.configDialog2.setChkSuggestionInfo();
            }
            if (Lizzie.firstUseSettings != null && Lizzie.firstUseSettings.isVisible()) {
              Lizzie.firstUseSettings.setChkSuggestionInfo();
            }
          }
        });
    okButton.setBounds(150, 101, 93, 25);
    infoPanel.add(okButton);

    JFontButton cancelButton =
        new JFontButton(
            Lizzie.resourceBundle.getString("SuggestionInfoOrderSettings.cancelButton"));
    cancelButton.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            setVisible(false);
          }
        });
    cancelButton.setBounds(250, 101, 93, 25);
    infoPanel.add(cancelButton);
    //  setSize(500, 400);

    timer =
        new Timer(
            100,
            new ActionListener() {
              public void actionPerformed(ActionEvent evt) {
                repaint();
                // table.validate();
                // table.updateUI();
              }
            });
    timer.start();
    Lizzie.setFrameSize(this, 480, 366);
    try {
      this.setIconImage(ImageIO.read(MoreEngines.class.getResourceAsStream("/assets/logo.png")));
    } catch (IOException e1) {
      // TODO Auto-generated catch block
      e1.printStackTrace();
    }
    addWindowListener(
        new WindowAdapter() {
          public void windowClosing(WindowEvent e) {
            if (timer != null) timer.stop();
          }
        });
    this.setLocationRelativeTo(owner);
  }

  private void paintMianPanel(Graphics g0) {
    int width = Utils.zoomOut(previewPanel.getWidth());
    int height = Utils.zoomOut(previewPanel.getHeight());
    BufferedImage cachedImage = new BufferedImage(width, height, TYPE_INT_ARGB);
    Graphics2D g = (Graphics2D) cachedImage.getGraphics();
    g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
    g.setRenderingHint(KEY_INTERPOLATION, VALUE_INTERPOLATION_BILINEAR);

    if (Lizzie.config.usePureBoard) {
      g.setColor(Lizzie.config.pureBoardColor);
      g.fillRect(0, 0, width, height);
    } else {
      BufferedImage cachedBoardImage = Lizzie.config.theme.board();
      TexturePaint paint =
          new TexturePaint(
              cachedBoardImage,
              new Rectangle(0, 0, cachedBoardImage.getWidth(), cachedBoardImage.getHeight()));
      g.setPaint(paint);
      g.fill(new Rectangle(0, 0, width, height));
    }

    int winrateOrder = -1, playoutsOrder = -1, scoreLeadOrder = -1;
    if (rdoWinrate1.isSelected()) winrateOrder = 1;
    else if (rdoWinrate2.isSelected()) winrateOrder = 2;
    else if (rdoWinrate3.isSelected()) winrateOrder = 3;

    if (rdoPlayouts1.isSelected()) playoutsOrder = 1;
    else if (rdoPlayouts2.isSelected()) playoutsOrder = 2;
    else if (rdoPlayouts3.isSelected()) playoutsOrder = 3;

    if (rdoScoreLead1.isSelected()) scoreLeadOrder = 1;
    else if (rdoScoreLead2.isSelected()) scoreLeadOrder = 2;
    else if (rdoScoreLead3.isSelected()) scoreLeadOrder = 3;

    if (winrateOrder < 0
        || playoutsOrder < 0
        || scoreLeadOrder < 0
        || winrateOrder == playoutsOrder
        || playoutsOrder == scoreLeadOrder
        || scoreLeadOrder == winrateOrder) {

    } else {
      currentSuggestionInfoWinrate = winrateOrder;
      currentSuggestionInfoPlayouts = playoutsOrder;
      currentSuggestionInfoScoreLead = scoreLeadOrder;
    }

    currentShowWinrateInSuggestion = chkWinrate.isSelected();
    currentShowPlayoutsInSuggestion = chkPlayouts.isSelected();
    currentShowScoremeanInSuggestion = chkScoreLead.isSelected();
    g.setFont(new Font(Config.sysDefaultFontName, Font.BOLD, Utils.zoomOut(27)));
    g.setColor(Color.BLACK);
    g.drawString(
        Lizzie.resourceBundle.getString("SuggestionInfoOrderSettings.preview"),
        Utils.zoomOut(Lizzie.config.isChinese ? 20 : 5),
        Utils.zoomOut(110));
    g.drawString("KataGo", Utils.zoomOut(134), Utils.zoomOut(30));
    g.drawString("Leela", Utils.zoomOut(315), Utils.zoomOut(30));

    MoveData move = new MoveData();
    move.order = 0;
    move.playouts = 1300;
    move.winrate = 68.5;
    move.scoreMean = 5.7;
    move.isKataData = true;
    Color colorMoveKata = new Color(0, 217, 240);
    drawMove(move, g, Utils.zoomOut(180), Utils.zoomOut(100), colorMoveKata, true);

    MoveData move2 = new MoveData();
    move2.order = 1;
    move2.playouts = 730;
    move2.winrate = 46.5;
    move2.isKataData = false;
    Color colorMoveLeela = new Color(11, 227, 0, 237);
    drawMove(move2, g, Utils.zoomOut(350), Utils.zoomOut(100), colorMoveLeela, false);

    g0.drawImage(cachedImage, 0, 0, null);

    g.dispose();
  }

  private void drawString(
      Graphics2D g,
      int x,
      int y,
      Font fontBase,
      String string,
      float maximumFontHeight,
      double maximumFontWidth) {
    drawString(g, x, y, fontBase, Font.PLAIN, string, maximumFontHeight, maximumFontWidth, 0);
  }

  private Font makeFont(Font fontBase, int style) {
    Font font = fontBase.deriveFont(style, 100);
    Map<TextAttribute, Object> atts = new HashMap<>();
    atts.put(TextAttribute.KERNING, TextAttribute.KERNING_ON);
    return font.deriveFont(atts);
  }

  private Font drawString(
      Graphics2D g,
      int x,
      int y,
      Font fontBase,
      int style,
      String string,
      float maximumFontHeight,
      double maximumFontWidth,
      int aboveOrBelow) {

    Font font = makeFont(fontBase, style);
    FontMetrics fm = g.getFontMetrics(font);
    font = font.deriveFont((float) (font.getSize2D() * maximumFontWidth / fm.stringWidth(string)));
    font = font.deriveFont(min(maximumFontHeight, font.getSize()));
    g.setFont(font);
    fm = g.getFontMetrics(font);
    int height = fm.getAscent() - fm.getDescent();
    int verticalOffset;
    if (aboveOrBelow == -1) {
      verticalOffset = height / 2;
    } else if (aboveOrBelow == 1) {
      verticalOffset = -height / 2;
    } else {
      verticalOffset = 0;
    }
    g.drawString(string, x - fm.stringWidth(string) / 2, y + height / 2 + verticalOffset);
    return font;
  }

  private void fillCircle(Graphics2D g, int centerX, int centerY, int radius) {
    g.fillOval(centerX - radius, centerY - radius, 2 * radius + 1, 2 * radius + 1);
  }

  private void fillCircleBest(Graphics2D g, int centerX, int centerY, int radius) {
    g.fillOval(centerX - radius - 1, centerY - radius - 1, 2 * radius + 3, 2 * radius + 3);
  }

  private void drawCircleBest(Graphics2D g, int centerX, int centerY, int radius, float f) {
    g.setStroke(new BasicStroke(radius / f));
    g.drawOval(centerX - radius - 1, centerY - radius - 1, 2 * radius + 2, 2 * radius + 2);
  }

  private void drawCircle(Graphics2D g, int centerX, int centerY, int radius, float f) {
    g.setStroke(new BasicStroke(radius / f));
    g.drawOval(centerX - radius, centerY - radius, 2 * radius, 2 * radius);
  }

  private Font drawStringForOrder(
      Graphics2D g,
      int x,
      int y,
      Font fontBase,
      int style,
      String string,
      float maximumFontHeight,
      double maximumFontWidth,
      int aboveOrBelow,
      boolean blackToPlay) {

    Font font = makeFont(fontBase, style);

    // set maximum size of font
    FontMetrics fm = g.getFontMetrics(font);
    font = font.deriveFont((float) (font.getSize2D() * maximumFontWidth / fm.stringWidth(string)));
    font = font.deriveFont(min(maximumFontHeight, font.getSize()));
    //    if(font.getSize()<15)
    //    	font=new Font(font.getName(),Font.BOLD,font.getSize());
    g.setFont(font);
    fm = g.getFontMetrics(font);
    int height = fm.getAscent() - fm.getDescent();
    int verticalOffset;
    if (aboveOrBelow == -1) {
      verticalOffset = height / 2;
    } else if (aboveOrBelow == 1) {
      verticalOffset = -height / 2;
    } else {
      verticalOffset = 0;
    }
    // bounding box for debugging
    // g.drawRect(x-(int)maximumFontWidth/2, y - height/2 + verticalOffset,
    // (int)maximumFontWidth,
    // height+verticalOffset );
    int x1 = x - fm.stringWidth(string) / 2;
    int y1 = y + height / 2 + verticalOffset;
    int width = fm.stringWidth(string);
    g.setColor(
        Lizzie.config.whiteSuggestionOrderWhite && !blackToPlay
            ? new Color(155, 118, 36)
            : Color.ORANGE);
    g.fillRect(x1, y1 - height, width, height + Math.max(1, height / 12));
    g.setColor(Lizzie.config.whiteSuggestionOrderWhite && !blackToPlay ? Color.WHITE : Color.BLACK);
    g.drawString(string, x1, y1);
    return font;
  }

  private void drawStringFor3row(
      Graphics2D g,
      int x,
      int y,
      Font fontBase,
      int style,
      String string,
      float maximumFontHeight,
      double maximumFontWidth) {

    Font font = makeFont(fontBase, style);
    FontMetrics fm = g.getFontMetrics(font);
    font = font.deriveFont((float) (font.getSize2D() * maximumFontWidth / fm.stringWidth(string)));
    font = font.deriveFont(min(maximumFontHeight, font.getSize()));
    g.setFont(font);
    fm = g.getFontMetrics(font);
    g.drawString(string, x - fm.stringWidth(string) / 2, y);
  }

  private String getSuggestionInfoRow1(String winrate, String playouts, String scoreLead) {
    if (currentSuggestionInfoWinrate == 1) return winrate;
    else if (currentSuggestionInfoPlayouts == 1) return playouts;
    else if (currentSuggestionInfoScoreLead == 1) return scoreLead;
    return winrate;
  }

  private String getSuggestionInfoRow2(String winrate, String playouts, String scoreLead) {
    if (currentSuggestionInfoPlayouts == 2) return playouts;
    else if (currentSuggestionInfoWinrate == 2) return winrate;
    else if (currentSuggestionInfoScoreLead == 2) return scoreLead;
    return playouts;
  }

  private String getSuggestionInfoRow3(String winrate, String playouts, String scoreLead) {
    if (currentSuggestionInfoScoreLead == 3) return scoreLead;
    else if (currentSuggestionInfoWinrate == 3) return winrate;
    else if (currentSuggestionInfoPlayouts == 3) return playouts;
    return scoreLead;
  }

  private void drawMove(
      MoveData move,
      Graphics2D g,
      int suggestionX,
      int suggestionY,
      Color color,
      boolean hasMaxWinrate) {
    boolean currerentUseDefaultInfoRowOrder =
        currentSuggestionInfoWinrate == 1
            && currentSuggestionInfoPlayouts == 2
            && currentSuggestionInfoScoreLead == 3;
    int stoneRadius = 44;
    int squareWidth = 91;
    int availableWidth = 1639;

    int maxPlayouts = 1300;
    double maxScoreMean = 5.7;

    boolean isBestMove = hasMaxWinrate;

    boolean isMouseOver = false;
    // drawShadow2(g, suggestionX, suggestionY, true, alpha / 255.0f);
    //   g.setColor(color);
    if (Lizzie.config.showSuggestionOrder && move.order == 0) {
      boolean blackToPlay = true;
      drawStringForOrder(
          g,
          (int) round(suggestionX + squareWidth * 0.43) + 1,
          (int) round(suggestionY - squareWidth * 0.358) - 1,
          LizzieFrame.winrateFont,
          Font.PLAIN,
          "1",
          squareWidth * 0.36f,
          squareWidth * 0.39,
          1,
          blackToPlay);
    }
    {
      fillCircle(g, suggestionX, suggestionY, stoneRadius);
      g.setColor(Color.GRAY);
      drawCircle(g, suggestionX, suggestionY, stoneRadius + 1, 26.5f);

      g.setColor(color);
      if (isBestMove) fillCircleBest(g, suggestionX, suggestionY, stoneRadius);
      else fillCircle(g, suggestionX, suggestionY, stoneRadius);
      if (isBestMove) {
        if (Lizzie.config.showBlueRing) {
          g.setColor(Color.BLUE);
          drawCircleBest(g, suggestionX, suggestionY, stoneRadius + 1, 15f);
        } else {
          g.setColor(Color.GRAY);
          drawCircle(g, suggestionX, suggestionY, stoneRadius + 1, 26.5f);
        }
      }
    }

    double roundedWinrate = round(move.winrate * 10) / 10.0;

    if (Lizzie.config.showSuggestionOrder && move.order < 9 && move.order > 0) {
      boolean blackToPlay = true;
      drawStringForOrder(
          g,
          (int) round(suggestionX + squareWidth * 0.43),
          (int) round(suggestionY - squareWidth * 0.358),
          LizzieFrame.winrateFont,
          Font.PLAIN,
          String.valueOf(move.order + 1),
          squareWidth * 0.36f,
          squareWidth * 0.39,
          1,
          blackToPlay);
    }

    if (!(Lizzie.config.limitMaxSuggestion > 0 && move.order + 1 > Lizzie.config.limitMaxSuggestion)
        || isMouseOver) {
      // number++;
      if (isMouseOver) {
        // Color oriColor = g.getColor();
        g.setColor(Color.RED);
        drawCircle(g, suggestionX, suggestionY, stoneRadius + 1, 11f);
        // g.setColor(oriColor);
      }

      g.setColor(Color.BLACK);

      boolean isGenmoveBest = false;

      Color maxColor;
      if (isBestMove) maxColor = Lizzie.config.bestColor;
      else maxColor = Color.RED;
      //
      boolean showWinrate = currentShowWinrateInSuggestion;
      boolean showPlayouts = currentShowPlayoutsInSuggestion;
      boolean showScoreLead = move.isKataData && currentShowScoremeanInSuggestion;
      boolean canShowMaxColor =
          Lizzie.config.showSuggestionMaxRed && !isMouseOver && !isGenmoveBest;
      Color oriColor = g.getColor();
      if (showScoreLead && showPlayouts && showWinrate) {
        double score = move.scoreMean;
        if (Lizzie.config.showKataGoScoreLeadWithKomi) {
          score = score + Lizzie.board.getHistory().getGameInfo().getKomi();
        }
        boolean shouldShowMaxColorWinrate = canShowMaxColor && hasMaxWinrate;
        boolean shouldShowMaxColorPlayouts = canShowMaxColor && move.playouts == maxPlayouts;
        boolean shouldShowMaxColorScoreLead = canShowMaxColor && move.scoreMean == maxScoreMean;
        String winrateText = String.format(Locale.ENGLISH, "%.1f", roundedWinrate);
        String playoutsText = Utils.getPlayoutsString(move.playouts);
        String scoreLeadText = Utils.convertScoreToString(score, 5.0);
        if (currerentUseDefaultInfoRowOrder) {
          if (shouldShowMaxColorWinrate) g.setColor(maxColor);
          if (roundedWinrate < 10)
            drawStringFor3row(
                g,
                suggestionX,
                suggestionY - (int) round(squareWidth * 0.127),
                LizzieFrame.winrateFont,
                Font.PLAIN,
                winrateText,
                squareWidth * 0.36f,
                squareWidth * 0.67);
          else
            drawStringFor3row(
                g,
                suggestionX,
                suggestionY - (int) round(squareWidth * 0.125),
                LizzieFrame.winrateFont,
                Font.PLAIN,
                winrateText,
                squareWidth * 0.35f,
                squareWidth * 0.67);
          if (shouldShowMaxColorWinrate) g.setColor(oriColor);
          if (shouldShowMaxColorPlayouts) g.setColor(maxColor);
          if (move.playouts >= 1000) {
            drawStringFor3row(
                g,
                suggestionX,
                suggestionY + (int) round(squareWidth * 0.18),
                LizzieFrame.playoutsFont,
                Font.PLAIN,
                playoutsText,
                squareWidth * 0.34f,
                stoneRadius * 1.8);
          } else {
            drawStringFor3row(
                g,
                suggestionX,
                suggestionY + (int) round(squareWidth * 0.18),
                LizzieFrame.playoutsFont,
                Font.PLAIN,
                playoutsText,
                squareWidth * 0.34f,
                stoneRadius * 1.3);
          }
          if (shouldShowMaxColorPlayouts) g.setColor(oriColor);
          if (shouldShowMaxColorScoreLead) g.setColor(maxColor);
          drawStringFor3row(
              g,
              suggestionX,
              suggestionY + (int) round(squareWidth * 0.435),
              LizzieFrame.winrateFont,
              Font.PLAIN,
              scoreLeadText,
              availableWidth * 0.273f / (Board.boardWidth - 1),
              stoneRadius * 1.6);
          if (shouldShowMaxColorScoreLead) g.setColor(oriColor);
        } else {
          String rowText1 = getSuggestionInfoRow1(winrateText, playoutsText, scoreLeadText);
          String rowText2 = getSuggestionInfoRow2(winrateText, playoutsText, scoreLeadText);
          String rowText3 = getSuggestionInfoRow3(winrateText, playoutsText, scoreLeadText);
          boolean shouldShowMaxColorRow1 =
              (shouldShowMaxColorWinrate && rowText1.equals(winrateText))
                  || (shouldShowMaxColorPlayouts && rowText1.equals(playoutsText))
                  || (shouldShowMaxColorScoreLead && rowText1.equals(scoreLeadText));
          boolean shouldShowMaxColorRow2 =
              (shouldShowMaxColorWinrate && rowText2.equals(winrateText))
                  || (shouldShowMaxColorPlayouts && rowText2.equals(playoutsText))
                  || (shouldShowMaxColorScoreLead && rowText2.equals(scoreLeadText));
          boolean shouldShowMaxColorRow3 =
              (shouldShowMaxColorWinrate && rowText3.equals(winrateText))
                  || (shouldShowMaxColorPlayouts && rowText3.equals(playoutsText))
                  || (shouldShowMaxColorScoreLead && rowText3.equals(scoreLeadText));
          if (shouldShowMaxColorRow1) g.setColor(maxColor);
          drawStringFor3row(
              g,
              suggestionX,
              suggestionY - (int) round(squareWidth * 0.125),
              currentSuggestionInfoPlayouts == 1
                  ? LizzieFrame.playoutsFont
                  : LizzieFrame.winrateFont,
              Font.PLAIN,
              rowText1,
              squareWidth * 0.35f,
              squareWidth * 0.67);
          if (shouldShowMaxColorRow1) g.setColor(oriColor);
          if (shouldShowMaxColorRow2) g.setColor(maxColor);
          drawStringFor3row(
              g,
              suggestionX,
              suggestionY + (int) round(squareWidth * 0.18),
              currentSuggestionInfoPlayouts == 2
                  ? LizzieFrame.playoutsFont
                  : LizzieFrame.winrateFont,
              Font.PLAIN,
              rowText2,
              squareWidth * 0.32f,
              stoneRadius * 1.8);
          if (shouldShowMaxColorRow2) g.setColor(oriColor);
          if (shouldShowMaxColorRow3) g.setColor(maxColor);
          drawStringFor3row(
              g,
              suggestionX,
              suggestionY + (int) round(squareWidth * 0.435),
              currentSuggestionInfoPlayouts == 3
                  ? LizzieFrame.playoutsFont
                  : LizzieFrame.winrateFont,
              Font.PLAIN,
              rowText3,
              availableWidth * 0.273f / (Board.boardWidth - 1),
              stoneRadius * 1.6);
          if (shouldShowMaxColorRow3) g.setColor(oriColor);
        }
      } else if (showWinrate && showPlayouts) {
        String winrateText = String.format(Locale.ENGLISH, "%.1f", roundedWinrate);
        String playoutsText = Utils.getPlayoutsString(move.playouts);
        boolean shouldShowMaxColorWinrate = canShowMaxColor && hasMaxWinrate;
        boolean shouldShowMaxColorPlayouts = canShowMaxColor && move.playouts == maxPlayouts;
        if (currerentUseDefaultInfoRowOrder
            || currentSuggestionInfoWinrate < currentSuggestionInfoPlayouts) {
          if (shouldShowMaxColorWinrate) g.setColor(maxColor);
          if (roundedWinrate < 10) {
            drawString(
                g,
                suggestionX,
                suggestionY - squareWidth / 15,
                LizzieFrame.winrateFont,
                Font.PLAIN,
                winrateText,
                stoneRadius,
                squareWidth * 0.57,
                1);
          } else {
            drawString(
                g,
                suggestionX,
                suggestionY - squareWidth / 16,
                LizzieFrame.winrateFont,
                Font.PLAIN,
                winrateText,
                stoneRadius,
                squareWidth * 0.735,
                1);
          }
          if (shouldShowMaxColorWinrate) g.setColor(oriColor);
          if (shouldShowMaxColorPlayouts) g.setColor(maxColor);
          drawString(
              g,
              suggestionX,
              suggestionY + stoneRadius * 15 / 35,
              LizzieFrame.playoutsFont,
              playoutsText,
              stoneRadius * 0.8f,
              stoneRadius * 1.4);

          if (shouldShowMaxColorPlayouts) g.setColor(oriColor);
        } else {
          if (shouldShowMaxColorWinrate) g.setColor(maxColor);
          drawString(
              g,
              suggestionX,
              suggestionY + stoneRadius * 15 / 35,
              LizzieFrame.winrateFont,
              winrateText,
              stoneRadius * 0.77f,
              stoneRadius * 1.8);

          if (shouldShowMaxColorWinrate) g.setColor(oriColor);
          if (shouldShowMaxColorPlayouts) g.setColor(maxColor);
          drawString(
              g,
              suggestionX,
              suggestionY - squareWidth / 15,
              LizzieFrame.playoutsFont,
              Font.PLAIN,
              playoutsText,
              squareWidth * 0.4f,
              squareWidth * 0.97,
              1);
          if (shouldShowMaxColorPlayouts) g.setColor(oriColor);
        }
      } else if (showWinrate && showScoreLead) {
        boolean shouldShowMaxColorWinrate = canShowMaxColor && hasMaxWinrate;
        boolean shouldShowMaxColorScoreLead = canShowMaxColor && move.scoreMean == maxScoreMean;
        double score = move.scoreMean;
        if (Lizzie.board.getHistory().isBlacksTurn()) {
          if (Lizzie.config.showKataGoScoreLeadWithKomi) {
            score = score + Lizzie.board.getHistory().getGameInfo().getKomi();
          }
        } else {
          if (Lizzie.config.showKataGoScoreLeadWithKomi) {
            score = score - Lizzie.board.getHistory().getGameInfo().getKomi();
          }
          if (Lizzie.config.winrateAlwaysBlack) {
            score = -score;
          }
        }
        String winrateText = String.format(Locale.ENGLISH, "%.1f", roundedWinrate);
        String scoreLeadText = String.format(Locale.ENGLISH, "%.1f", score);
        if (currerentUseDefaultInfoRowOrder
            || currentSuggestionInfoWinrate < currentSuggestionInfoScoreLead) {
          if (shouldShowMaxColorWinrate) g.setColor(maxColor);
          if (roundedWinrate < 10) {
            drawString(
                g,
                suggestionX,
                suggestionY - squareWidth / 15,
                LizzieFrame.winrateFont,
                Font.PLAIN,
                winrateText,
                stoneRadius,
                squareWidth * 0.57,
                1);
          } else {
            drawString(
                g,
                suggestionX,
                suggestionY - squareWidth / 16,
                LizzieFrame.winrateFont,
                Font.PLAIN,
                winrateText,
                stoneRadius,
                squareWidth * 0.735,
                1);
          }
          if (shouldShowMaxColorWinrate) g.setColor(oriColor);
          if (shouldShowMaxColorScoreLead) g.setColor(maxColor);
          drawString(
              g,
              suggestionX,
              suggestionY + stoneRadius * 4 / 9,
              LizzieFrame.winrateFont,
              scoreLeadText,
              stoneRadius * 0.75f,
              stoneRadius * 1.6);
          if (shouldShowMaxColorScoreLead) g.setColor(oriColor);
        } else {
          if (shouldShowMaxColorWinrate) g.setColor(maxColor);
          drawString(
              g,
              suggestionX,
              suggestionY + stoneRadius * 15 / 35,
              LizzieFrame.winrateFont,
              winrateText,
              stoneRadius * 0.75f,
              stoneRadius * 1.77);

          if (shouldShowMaxColorWinrate) g.setColor(oriColor);
          if (shouldShowMaxColorScoreLead) g.setColor(maxColor);
          drawString(
              g,
              suggestionX,
              suggestionY - squareWidth / 16,
              LizzieFrame.winrateFont,
              Font.PLAIN,
              scoreLeadText,
              stoneRadius * 0.88f,
              squareWidth * 0.735,
              1);
          if (shouldShowMaxColorScoreLead) g.setColor(oriColor);
        }
      } else if (showPlayouts && showScoreLead) {
        boolean shouldShowMaxColorPlayouts = canShowMaxColor && move.playouts == maxPlayouts;
        boolean shouldShowMaxColorScoreLead = canShowMaxColor && move.scoreMean == maxScoreMean;
        double score = move.scoreMean;
        if (Lizzie.board.getHistory().isBlacksTurn()) {
          if (Lizzie.config.showKataGoScoreLeadWithKomi) {
            score = score + Lizzie.board.getHistory().getGameInfo().getKomi();
          }
        } else {
          if (Lizzie.config.showKataGoScoreLeadWithKomi) {
            score = score - Lizzie.board.getHistory().getGameInfo().getKomi();
          }
          if (Lizzie.config.winrateAlwaysBlack) {
            score = -score;
          }
        }
        String playoutsText = Utils.getPlayoutsString(move.playouts);
        String scoreLeadText = String.format(Locale.ENGLISH, "%.1f", score);
        if (currerentUseDefaultInfoRowOrder
            || currentSuggestionInfoPlayouts < currentSuggestionInfoScoreLead) {
          if (shouldShowMaxColorPlayouts) g.setColor(maxColor);
          drawString(
              g,
              suggestionX,
              suggestionY - stoneRadius * 1 / 15,
              LizzieFrame.playoutsFont,
              Font.PLAIN,
              playoutsText,
              stoneRadius * 0.82f,
              stoneRadius * 1.73,
              1);
          if (shouldShowMaxColorPlayouts) g.setColor(oriColor);
          if (shouldShowMaxColorScoreLead) g.setColor(maxColor);
          drawString(
              g,
              suggestionX,
              suggestionY + stoneRadius * 4 / 9,
              LizzieFrame.winrateFont,
              scoreLeadText,
              stoneRadius * 0.75f,
              stoneRadius * 1.6);
          if (shouldShowMaxColorScoreLead) g.setColor(oriColor);
        } else {
          if (shouldShowMaxColorPlayouts) g.setColor(maxColor);

          drawString(
              g,
              suggestionX,
              suggestionY + stoneRadius * 15 / 35,
              LizzieFrame.playoutsFont,
              playoutsText,
              stoneRadius * 0.8f,
              stoneRadius * 1.4);

          if (shouldShowMaxColorPlayouts) g.setColor(oriColor);
          if (shouldShowMaxColorScoreLead) g.setColor(maxColor);
          drawString(
              g,
              suggestionX,
              suggestionY - squareWidth / 16,
              LizzieFrame.winrateFont,
              Font.PLAIN,
              scoreLeadText,
              stoneRadius * 0.88f,
              squareWidth * 0.735,
              1);
          if (shouldShowMaxColorScoreLead) g.setColor(oriColor);
        }

      } else if (showWinrate) {
        boolean shouldShowMaxColorWinrate = canShowMaxColor && hasMaxWinrate;
        if (shouldShowMaxColorWinrate) g.setColor(maxColor);
        if (roundedWinrate < 10) {
          drawString(
              g,
              suggestionX,
              suggestionY,
              LizzieFrame.winrateFont,
              String.format(Locale.ENGLISH, "%.1f", roundedWinrate),
              squareWidth * 0.46f,
              stoneRadius * 1.9);
        } else {
          drawString(
              g,
              suggestionX,
              suggestionY,
              LizzieFrame.winrateFont,
              String.format(Locale.ENGLISH, "%.1f", roundedWinrate),
              squareWidth * 0.46f,
              stoneRadius * 1.9);
        }
        if (shouldShowMaxColorWinrate) g.setColor(oriColor);
      } else if (showPlayouts) {
        boolean shouldShowMaxColorPlayouts = canShowMaxColor && move.playouts == maxPlayouts;
        if (shouldShowMaxColorPlayouts) g.setColor(maxColor);
        drawString(
            g,
            suggestionX,
            suggestionY,
            LizzieFrame.playoutsFont,
            Utils.getPlayoutsString(move.playouts),
            stoneRadius,
            stoneRadius * 1.9);
        if (shouldShowMaxColorPlayouts) g.setColor(oriColor);
      } else if (showScoreLead) {
        double score = move.scoreMean;
        if (Lizzie.board.getHistory().isBlacksTurn()) {
          if (Lizzie.config.showKataGoScoreLeadWithKomi) {
            score = score + Lizzie.board.getHistory().getGameInfo().getKomi();
          }
        } else {
          if (Lizzie.config.showKataGoScoreLeadWithKomi) {
            score = score - Lizzie.board.getHistory().getGameInfo().getKomi();
          }
          if (Lizzie.config.winrateAlwaysBlack) {
            score = -score;
          }
        }
        boolean shouldShowMaxColorScoreLead = canShowMaxColor && move.scoreMean == maxScoreMean;
        if (shouldShowMaxColorScoreLead) g.setColor(maxColor);
        drawString(
            g,
            suggestionX,
            suggestionY,
            LizzieFrame.winrateFont,
            String.format(Locale.ENGLISH, "%.1f", score),
            stoneRadius,
            stoneRadius * 1.7);
        if (shouldShowMaxColorScoreLead) g.setColor(oriColor);
      }
    }
  }
}
