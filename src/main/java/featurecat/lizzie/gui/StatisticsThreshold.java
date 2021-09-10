package featurecat.lizzie.gui;

import featurecat.lizzie.Config;
import featurecat.lizzie.Lizzie;
import featurecat.lizzie.util.Utils;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class StatisticsThreshold extends JDialog {
  private JPanel labelPanel;
  private JPanel thresholdPanel;
  private JPanel winPanel;
  private JPanel scorePanel;
  private JPanel buttonPanel;

  private JFontTextField txtWin0;
  private JFontTextField txtWin1;
  private JFontTextField txtWin2;
  private JFontTextField txtWin3;
  private JFontTextField txtWin4;
  private JFontTextField txtWin5;

  private JFontTextField txtScore0;
  private JFontTextField txtScore1;
  private JFontTextField txtScore2;
  private JFontTextField txtScore3;
  private JFontTextField txtScore4;
  private JFontTextField txtScore5;

  public StatisticsThreshold(Window owner) {
    super(owner);
    setTitle(Lizzie.resourceBundle.getString("StatisticsThreshold.title"));
    initComponents();
    loadValue();
    pack();
    setLocationRelativeTo(owner);
  }

  private void initComponents() {
    labelPanel = new JPanel();
    thresholdPanel = new JPanel();
    buttonPanel = new JPanel();
    getContentPane().add(labelPanel, BorderLayout.NORTH);
    getContentPane().add(thresholdPanel, BorderLayout.CENTER);
    getContentPane().add(buttonPanel, BorderLayout.SOUTH);
    labelPanel.setLayout(new GridLayout(1, 2, 3, 3));
    labelPanel.add(
        new JFontLabel(Lizzie.resourceBundle.getString("StatisticsThreshold.lblScoreLoss"), 0));
    labelPanel.add(
        new JFontLabel(Lizzie.resourceBundle.getString("StatisticsThreshold.lblWinLoss"), 0));
    labelPanel.setBorder(new EmptyBorder(5, 0, 5, 0));

    buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
    JFontButton btnConfirm =
        new JFontButton(Lizzie.resourceBundle.getString("StatisticsThreshold.btnConfirm"));
    btnConfirm.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            if (saveSettings()) setVisible(false);
          }
        });
    JFontButton btnCancel =
        new JFontButton(Lizzie.resourceBundle.getString("StatisticsThreshold.btnCancel"));
    btnCancel.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            setVisible(false);
          }
        });
    JFontButton btnCalcMethod =
        new JFontButton(Lizzie.resourceBundle.getString("StatisticsThreshold.btnCalcMethod"));
    btnCalcMethod.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            Utils.showHtmlMessage(
                Lizzie.resourceBundle.getString("StatisticsThreshold.calcMethod.title"),
                Lizzie.resourceBundle.getString("StatisticsThreshold.calcMethod.content"));
          }
        });
    JFontButton btnReset =
        new JFontButton(Lizzie.resourceBundle.getString("StatisticsThreshold.btnReset"));
    btnReset.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            resetDefauleValue();
          }
        });
    btnCalcMethod.setMargin(new Insets(2, 8, 2, 8));
    btnReset.setMargin(new Insets(2, 8, 2, 8));
    btnConfirm.setMargin(new Insets(2, 8, 2, 8));
    btnCancel.setMargin(new Insets(2, 8, 2, 8));
    buttonPanel.add(btnCalcMethod);
    buttonPanel.add(btnReset);
    buttonPanel.add(btnConfirm);
    buttonPanel.add(btnCancel);

    thresholdPanel.setLayout(new GridLayout(1, 2, 3, 3));
    thresholdPanel.setBorder(new EmptyBorder(0, 5, 0, 5));
    winPanel = new JPanel();
    scorePanel = new JPanel();
    thresholdPanel.add(scorePanel);
    thresholdPanel.add(winPanel);

    winPanel.setLayout(new GridLayout(6, 3, 3, 3));
    winPanel.add(
        new JFontLabel(Lizzie.resourceBundle.getString("StatisticsThreshold.loss") + ">="));
    txtWin5 = new JFontTextField();
    txtWin5.setDocument(new KomiDocument(false));
    txtWin5.setColumns(3);
    winPanel.add(txtWin5);
    addColorLabel(winPanel, new Color(85, 25, 80));

    winPanel.add(
        new JFontLabel(Lizzie.resourceBundle.getString("StatisticsThreshold.loss") + ">="));
    txtWin4 = new JFontTextField();
    txtWin4.setDocument(new KomiDocument(false));
    txtWin4.setColumns(3);
    winPanel.add(txtWin4);
    addColorLabel(winPanel, new Color(172, 16, 19));

    winPanel.add(
        new JFontLabel(Lizzie.resourceBundle.getString("StatisticsThreshold.loss") + ">="));
    txtWin3 = new JFontTextField();
    txtWin3.setDocument(new KomiDocument(false));
    txtWin3.setColumns(3);
    winPanel.add(txtWin3);
    addColorLabel(winPanel, new Color(200, 140, 50));

    winPanel.add(
        new JFontLabel(Lizzie.resourceBundle.getString("StatisticsThreshold.loss") + ">="));
    txtWin2 = new JFontTextField();
    txtWin2.setDocument(new KomiDocument(false));
    txtWin2.setColumns(3);
    winPanel.add(txtWin2);
    addColorLabel(winPanel, new Color(222, 222, 0));

    winPanel.add(
        new JFontLabel(Lizzie.resourceBundle.getString("StatisticsThreshold.loss") + ">="));
    txtWin1 = new JFontTextField();
    txtWin1.setDocument(new KomiDocument(false));
    txtWin1.setColumns(3);
    winPanel.add(txtWin1);
    addColorLabel(winPanel, new Color(158, 232, 34));

    txtWin1
        .getDocument()
        .addDocumentListener(
            new DocumentListener() {
              public void insertUpdate(DocumentEvent e) {
                txtWin0.setText(txtWin1.getText());
              }

              public void removeUpdate(DocumentEvent e) {
                txtWin0.setText(txtWin1.getText());
              }

              public void changedUpdate(DocumentEvent e) {
                txtWin0.setText(txtWin1.getText());
              }
            });

    winPanel.add(
        new JFontLabel(Lizzie.resourceBundle.getString("StatisticsThreshold.loss") + " <"));
    txtWin0 = new JFontTextField();
    txtWin0.setColumns(3);
    txtWin0.setEnabled(false);
    winPanel.add(txtWin0);
    addColorLabel(winPanel, new Color(22, 222, 0));

    scorePanel.setLayout(new GridLayout(6, 3, 3, 3));
    scorePanel.add(
        new JFontLabel(Lizzie.resourceBundle.getString("StatisticsThreshold.loss") + ">="));
    txtScore5 = new JFontTextField();
    txtScore5.setDocument(new KomiDocument(false));
    txtScore5.setColumns(3);
    scorePanel.add(txtScore5);
    addColorLabel(scorePanel, new Color(85, 25, 80));

    scorePanel.add(
        new JFontLabel(Lizzie.resourceBundle.getString("StatisticsThreshold.loss") + ">="));
    txtScore4 = new JFontTextField();
    txtScore4.setDocument(new KomiDocument(false));
    txtScore4.setColumns(3);
    scorePanel.add(txtScore4);
    addColorLabel(scorePanel, new Color(172, 16, 19));

    scorePanel.add(
        new JFontLabel(Lizzie.resourceBundle.getString("StatisticsThreshold.loss") + ">="));
    txtScore3 = new JFontTextField();
    txtScore3.setDocument(new KomiDocument(false));
    txtScore3.setColumns(3);
    scorePanel.add(txtScore3);
    addColorLabel(scorePanel, new Color(200, 140, 50));

    scorePanel.add(
        new JFontLabel(Lizzie.resourceBundle.getString("StatisticsThreshold.loss") + ">="));
    txtScore2 = new JFontTextField();
    txtScore2.setDocument(new KomiDocument(false));
    txtScore2.setColumns(3);
    scorePanel.add(txtScore2);
    addColorLabel(scorePanel, new Color(222, 222, 0));

    scorePanel.add(
        new JFontLabel(Lizzie.resourceBundle.getString("StatisticsThreshold.loss") + ">="));
    txtScore1 = new JFontTextField();
    txtScore1.setDocument(new KomiDocument(false));
    txtScore1.setColumns(3);
    scorePanel.add(txtScore1);
    addColorLabel(scorePanel, new Color(158, 232, 34));

    txtScore1
        .getDocument()
        .addDocumentListener(
            new DocumentListener() {
              public void insertUpdate(DocumentEvent e) {
                txtScore0.setText(txtScore1.getText());
              }

              public void removeUpdate(DocumentEvent e) {
                txtScore0.setText(txtScore1.getText());
              }

              public void changedUpdate(DocumentEvent e) {
                txtScore0.setText(txtScore1.getText());
              }
            });

    scorePanel.add(
        new JFontLabel(Lizzie.resourceBundle.getString("StatisticsThreshold.loss") + " <"));
    txtScore0 = new JFontTextField();
    txtScore0.setColumns(3);
    txtScore0.setEnabled(false);
    scorePanel.add(txtScore0);
    addColorLabel(scorePanel, new Color(22, 222, 0));
  }

  protected void resetDefauleValue() {
    txtWin0.setText(String.valueOf(1.0));
    txtWin1.setText(String.valueOf(1.0));
    txtWin2.setText(String.valueOf(3.0));
    txtWin3.setText(String.valueOf(6.0));
    txtWin4.setText(String.valueOf(12.0));
    txtWin5.setText(String.valueOf(24.0));

    txtScore0.setText(String.valueOf(0.5));
    txtScore1.setText(String.valueOf(0.5));
    txtScore2.setText(String.valueOf(1.5));
    txtScore3.setText(String.valueOf(3.0));
    txtScore4.setText(String.valueOf(6.0));
    txtScore5.setText(String.valueOf(12.0));
  }

  private void addColorLabel(JPanel panel, Color color) {
    Dimension FILLER_DIMENSION = new Dimension(Config.menuHeight, Config.menuHeight);
    JLabel filler = new JLabel();
    filler.setOpaque(true);
    filler.setPreferredSize(FILLER_DIMENSION);
    filler.setBackground(color);
    panel.add(filler);
  }

  private void loadValue() {
    txtWin0.setText(String.valueOf(-Lizzie.config.winLossThreshold1));
    txtWin1.setText(String.valueOf(-Lizzie.config.winLossThreshold1));
    txtWin2.setText(String.valueOf(-Lizzie.config.winLossThreshold2));
    txtWin3.setText(String.valueOf(-Lizzie.config.winLossThreshold3));
    txtWin4.setText(String.valueOf(-Lizzie.config.winLossThreshold4));
    txtWin5.setText(String.valueOf(-Lizzie.config.winLossThreshold5));

    txtScore0.setText(String.valueOf(-Lizzie.config.scoreLossThreshold1));
    txtScore1.setText(String.valueOf(-Lizzie.config.scoreLossThreshold1));
    txtScore2.setText(String.valueOf(-Lizzie.config.scoreLossThreshold2));
    txtScore3.setText(String.valueOf(-Lizzie.config.scoreLossThreshold3));
    txtScore4.setText(String.valueOf(-Lizzie.config.scoreLossThreshold4));
    txtScore5.setText(String.valueOf(-Lizzie.config.scoreLossThreshold5));
  }

  private boolean saveSettings() {
    double win1 = -Utils.parseTextToDouble(txtWin1, -Lizzie.config.winLossThreshold1);
    double win2 = -Utils.parseTextToDouble(txtWin2, -Lizzie.config.winLossThreshold2);
    double win3 = -Utils.parseTextToDouble(txtWin3, -Lizzie.config.winLossThreshold3);
    double win4 = -Utils.parseTextToDouble(txtWin4, -Lizzie.config.winLossThreshold4);
    double win5 = -Utils.parseTextToDouble(txtWin5, -Lizzie.config.winLossThreshold5);

    double score1 = -Utils.parseTextToDouble(txtScore1, -Lizzie.config.scoreLossThreshold1);
    double score2 = -Utils.parseTextToDouble(txtScore2, -Lizzie.config.scoreLossThreshold2);
    double score3 = -Utils.parseTextToDouble(txtScore3, -Lizzie.config.scoreLossThreshold3);
    double score4 = -Utils.parseTextToDouble(txtScore4, -Lizzie.config.scoreLossThreshold4);
    double score5 = -Utils.parseTextToDouble(txtScore5, -Lizzie.config.scoreLossThreshold5);
    if (!(win1 > win2 && win2 > win3 && win3 > win4 && win4 > win5)) {
      Utils.showMsg(Lizzie.resourceBundle.getString("StatisticsThreshold.wrongTHR"));
      return false;
    }
    if (!(score1 > score2 && score2 > score3 && score3 > score4 && score4 > score5)) {
      Utils.showMsg(Lizzie.resourceBundle.getString("StatisticsThreshold.wrongTHR"));
      return false;
    }
    Lizzie.config.winLossThreshold1 = win1;
    Lizzie.config.winLossThreshold2 = win2;
    Lizzie.config.winLossThreshold3 = win3;
    Lizzie.config.winLossThreshold4 = win4;
    Lizzie.config.winLossThreshold5 = win5;
    Lizzie.config.uiConfig.put("win-loss-threshold-1", Lizzie.config.winLossThreshold1);
    Lizzie.config.uiConfig.put("win-loss-threshold-2", Lizzie.config.winLossThreshold2);
    Lizzie.config.uiConfig.put("win-loss-threshold-3", Lizzie.config.winLossThreshold3);
    Lizzie.config.uiConfig.put("win-loss-threshold-4", Lizzie.config.winLossThreshold4);
    Lizzie.config.uiConfig.put("win-loss-threshold-5", Lizzie.config.winLossThreshold5);

    Lizzie.config.scoreLossThreshold1 = score1;
    Lizzie.config.scoreLossThreshold2 = score2;
    Lizzie.config.scoreLossThreshold3 = score3;
    Lizzie.config.scoreLossThreshold4 = score4;
    Lizzie.config.scoreLossThreshold5 = score5;
    Lizzie.config.uiConfig.put("score-loss-threshold-1", Lizzie.config.scoreLossThreshold1);
    Lizzie.config.uiConfig.put("score-loss-threshold-2", Lizzie.config.scoreLossThreshold2);
    Lizzie.config.uiConfig.put("score-loss-threshold-3", Lizzie.config.scoreLossThreshold3);
    Lizzie.config.uiConfig.put("score-loss-threshold-4", Lizzie.config.scoreLossThreshold4);
    Lizzie.config.uiConfig.put("score-loss-threshold-5", Lizzie.config.scoreLossThreshold5);
    try {
      Lizzie.config.save();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    Lizzie.frame.refresh();
    getOwner().repaint();
    return true;
  }
}
