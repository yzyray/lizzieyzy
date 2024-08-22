package featurecat.lizzie.gui;

import featurecat.lizzie.Config;
import featurecat.lizzie.Lizzie;
import featurecat.lizzie.rules.BoardHistoryNode;
import featurecat.lizzie.rules.Movelist;
import featurecat.lizzie.rules.Stone;
import featurecat.lizzie.util.Utils;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.Locale;
import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import org.json.JSONObject;

public class ScoreResult extends JDialog {
  private JPanel dialogPane = new JPanel();
  private JPanel contentPanel = new JPanel();
  private JPanel buttonBar = new JPanel();
  private JFontLabel lblRule = new JFontLabel("");
  private JFontLabel blackScore =
      new JFontLabel(Lizzie.resourceBundle.getString("ScoreResult.lblBlackScore"));
  private JFontLabel whiteScore =
      new JFontLabel(Lizzie.resourceBundle.getString("ScoreResult.lblWhiteScore"));
  private JFontLabel scoreResult = new JFontLabel();

  private JButton btnRecalculate;
  private JButton btnClose;
  private Color backColor = new Color(210, 210, 210);
  private JButton confirmResult;
  private JButton btnToggleRules;
  int saved_blackAlive;
  int saved_blackPoint;
  int saved_whiteAlive;
  int saved_whitePoint;
  int saved_blackCaptures;
  int saved_whiteCaptures;
  double saved_komi;

  public ScoreResult(Window owner) {
    super(owner);
    initComponents();
    dialogPane.setBackground(backColor);
    contentPanel.setBackground(backColor);
    buttonBar.setBackground(backColor);
    addWindowListener(
        new WindowAdapter() {
          public void windowClosing(WindowEvent e) {
            setVisible(false);
            Lizzie.frame.endFinalScore();
          }
        });
    try {
      this.setIconImage(ImageIO.read(getClass().getResourceAsStream("/assets/logo.png")));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private void initComponents() {
    setMinimumSize(new Dimension(100, 100));
    setResizable(false);
    setTitle(Lizzie.resourceBundle.getString("ScoreResult.title"));

    Container contentPane = getContentPane();
    contentPane.setLayout(new BorderLayout());

    initDialogPane(contentPane);

    pack();
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
    int boardLenght = Lizzie.frame.maxSize;
    if (Lizzie.config.isFloatBoardMode() && Lizzie.frame.independentMainBoard != null) {
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

  private void initDialogPane(Container contentPane) {
    dialogPane.setBorder(new EmptyBorder(5, 12, 5, 12));
    dialogPane.setLayout(new BorderLayout());

    initContentPanel();
    initButtonBar();

    contentPane.add(dialogPane, BorderLayout.CENTER);
  }

  private void initContentPanel() {
    GridLayout gridLayout = new GridLayout(5, 1, 4, 4);
    contentPanel.setLayout(gridLayout);
    JLabel hint = new JFontLabel(Lizzie.resourceBundle.getString("ScoreResult.hint"));
    hint.setForeground(Color.BLUE);
    setLblRule();
    contentPanel.add(hint);
    contentPanel.add(lblRule);
    contentPanel.add(blackScore);
    contentPanel.add(whiteScore);
    scoreResult.setOpaque(true);
    contentPanel.add(scoreResult);
    dialogPane.add(contentPanel, BorderLayout.CENTER);
  }

  private void initButtonBar() {
    buttonBar.setLayout(new GridBagLayout());
    buttonBar.setBorder(new EmptyBorder(5, 0, 0, 0));
    btnToggleRules =
        new JFontButton(
            Lizzie.config.useTerritoryInScore
                ? Lizzie.resourceBundle.getString("ScoreResult.btnToggleRulesArea")
                : Lizzie.resourceBundle.getString("ScoreResult.btnToggleRulesTerritory"));
    btnToggleRules.setFocusable(false);
    GridBagConstraints gbc_btnToggleRules = new GridBagConstraints();
    gbc_btnToggleRules.anchor = GridBagConstraints.EAST;
    gbc_btnToggleRules.insets = new Insets(0, 0, 0, 5);
    gbc_btnToggleRules.gridx = 0;
    gbc_btnToggleRules.gridy = 0;
    buttonBar.add(btnToggleRules, gbc_btnToggleRules);
    btnToggleRules.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            Lizzie.config.useTerritoryInScore = !Lizzie.config.useTerritoryInScore;
            Lizzie.config.uiConfig.put("use-territory-in-score", Lizzie.config.useTerritoryInScore);
            btnToggleRules.setText(
                Lizzie.config.useTerritoryInScore
                    ? Lizzie.resourceBundle.getString("ScoreResult.btnToggleRulesArea")
                    : Lizzie.resourceBundle.getString("ScoreResult.btnToggleRulesTerritory"));
            pack();
            setScore(
                saved_blackAlive,
                saved_blackPoint,
                saved_whiteAlive,
                saved_whitePoint,
                saved_blackCaptures,
                saved_whiteCaptures,
                saved_komi);
          }
        });

    btnRecalculate = new JFontButton(Lizzie.resourceBundle.getString("ScoreResult.btnRecalculate"));
    btnRecalculate.setFocusable(false);
    GridBagConstraints gbc_btnRecalculate = new GridBagConstraints();
    gbc_btnRecalculate.anchor = GridBagConstraints.EAST;
    gbc_btnRecalculate.insets = new Insets(0, 0, 0, 5);
    gbc_btnRecalculate.gridx = 1;
    gbc_btnRecalculate.gridy = 0;
    buttonBar.add(btnRecalculate, gbc_btnRecalculate);
    btnRecalculate.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            setVisible(false);
            Lizzie.frame.startFinalScore();
          }
        });

    dialogPane.add(buttonBar, BorderLayout.SOUTH);

    confirmResult = new JFontButton(Lizzie.resourceBundle.getString("ScoreResult.confirmResult"));
    confirmResult.setFocusable(false);
    GridBagConstraints gbc_confirmResult = new GridBagConstraints();
    gbc_confirmResult.insets = new Insets(0, 0, 0, 5);
    gbc_confirmResult.gridx = 2;
    gbc_confirmResult.gridy = 0;
    buttonBar.add(confirmResult, gbc_confirmResult);
    confirmResult.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            String result = scoreResult.getText();
            Lizzie.board.getHistory().getGameInfo().setResult(result);
            Utils.showMsg(Lizzie.resourceBundle.getString("ScoreResult.addResultTip") + result);
            setVisible(false);
          }
        });

    btnClose = new JFontButton(Lizzie.resourceBundle.getString("ScoreResult.btnClose"));
    btnClose.setFocusable(false);
    btnClose.setPreferredSize(btnRecalculate.getPreferredSize());
    GridBagConstraints gbc_button_1 = new GridBagConstraints();
    gbc_button_1.anchor = GridBagConstraints.EAST;
    gbc_button_1.gridx = 3;
    gbc_button_1.gridy = 0;
    buttonBar.add(btnClose, gbc_button_1);
    btnClose.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            setVisible(false);
            Lizzie.frame.endFinalScore();
          }
        });
  }

  public void setScore(
      int blackAlive,
      int blackPoint,
      int whiteAlive,
      int whitePoint,
      int blackCaptures,
      int whiteCaptures,
      double komi) {
    saved_blackAlive = blackAlive;
    saved_blackPoint = blackPoint;
    saved_whiteAlive = whiteAlive;
    saved_whitePoint = whitePoint;
    saved_blackCaptures = blackCaptures;
    saved_whiteCaptures = whiteCaptures;
    saved_komi = komi;

    if (Lizzie.leelaz.recentRulesLine != null && Lizzie.leelaz.recentRulesLine.length() > 2) {
      JSONObject jo = new JSONObject(new String(Lizzie.leelaz.recentRulesLine.substring(2)));
      boolean whiteHandicapBonusN = false;
      boolean whiteHandicapBonusN1 = false;
      if (jo.optString("whiteHandicapBonus", "").contentEquals("N")) whiteHandicapBonusN = true;
      else if (jo.optString("whiteHandicapBonus", "").contentEquals("N-1"))
        whiteHandicapBonusN1 = true;
      if (whiteHandicapBonusN1 || whiteHandicapBonusN) {
        int blackStoneForCalcHandicap = 0;
        BoardHistoryNode node = Lizzie.board.getHistory().getCurrentHistoryNode();

        while (node.previous().isPresent()) {
          if (node.getData().lastMove.isPresent()) {
            if (node.getData().lastMoveColor == Stone.BLACK) blackStoneForCalcHandicap++;
            else if (node.getData().lastMoveColor == Stone.WHITE) blackStoneForCalcHandicap = 0;
          }
          node = node.previous().get();
        }
        if (Lizzie.board.hasStartStone) {
          for (Movelist move : Lizzie.board.startStonelist) {
            if (!move.ispass) {
              if (move.isblack) blackStoneForCalcHandicap++;
              else blackStoneForCalcHandicap = 0;
            }
          }
        }
        if (blackStoneForCalcHandicap > 1) {
          if (whiteHandicapBonusN) {
            blackAlive = blackAlive - blackStoneForCalcHandicap;
          } else {
            blackAlive = blackAlive - (blackStoneForCalcHandicap - 1);
          }
        }
      }
    }
    double blackAll = blackAlive + blackPoint;
    double whiteAll = whiteAlive + whitePoint + komi;
    if (Lizzie.config.useTerritoryInScore) {
      blackAll = blackPoint - whiteCaptures;
      whiteAll = whitePoint - blackCaptures + komi;
      blackScore.setText(
          Lizzie.resourceBundle.getString("ScoreResult.lblBlackScore")
              + blackPoint
              + "-"
              + whiteCaptures
              + "="
              + blackAll);
      whiteScore.setText(
          Lizzie.resourceBundle.getString("ScoreResult.lblWhiteScore")
              + whitePoint
              + "-"
              + blackCaptures
              + "+"
              + komi
              + "="
              + whiteAll);
    } else {
      blackScore.setText(
          Lizzie.resourceBundle.getString("ScoreResult.lblBlackScore")
              + blackAlive
              + "+"
              + blackPoint
              + "="
              + blackAll);
      whiteScore.setText(
          Lizzie.resourceBundle.getString("ScoreResult.lblWhiteScore")
              + whiteAlive
              + "+"
              + whitePoint
              + "+"
              + komi
              + "="
              + whiteAll);
    }
    scoreResult.setText(
        (blackAll >= whiteAll
                ? Lizzie.resourceBundle.getString("ScoreResult.blackWin")
                : Lizzie.resourceBundle.getString("ScoreResult.whiteWin"))
            + String.format(Locale.ENGLISH, "%.1f", Math.abs(blackAll - whiteAll))
            + Lizzie.resourceBundle.getString("ScoreResult.points"));
    if (blackAll >= whiteAll) {
      scoreResult.setBackground(Color.BLACK);
      scoreResult.setForeground(Color.WHITE);
    } else {
      scoreResult.setBackground(Color.WHITE);
      scoreResult.setForeground(Color.BLACK);
    }
    setLblRule();
  }

  private void setLblRule() {
    if (Lizzie.config.useTerritoryInScore)
      lblRule.setText(Lizzie.resourceBundle.getString("ScoreResult.lblUseTerritoryScoring"));
    else lblRule.setText(Lizzie.resourceBundle.getString("ScoreResult.lblUseAreaScoring"));
  }
}
