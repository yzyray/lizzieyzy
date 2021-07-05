package featurecat.lizzie.gui;

import featurecat.lizzie.Lizzie;
import featurecat.lizzie.util.Utils;
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

public class SetDiffAnalyze extends JDialog {
  private JPanel dialogPane = new JPanel();
  private JPanel contentPanel = new JPanel();
  private JPanel buttonBar = new JPanel();

  private JCheckBox chkBlack;
  private JCheckBox chkWhite;
  private JCheckBox chkWin;
  private JCheckBox chkScore;

  private JFontTextField txtWinDiff;
  private JFontTextField txtScoreDiff;
  private JFontTextField txtTime;
  private JFontTextField txtPlayouts;
  private JFontTextField txtFirstPlayouts;

  private JButton btnConfirm;
  private JButton btnCancel;

  public SetDiffAnalyze(Window owner) {
    super(owner);
    initComponents();
    try {
      this.setIconImage(ImageIO.read(AnalysisFrame.class.getResourceAsStream("/assets/logo.png")));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private void initComponents() {
    setMinimumSize(new Dimension(100, 100));
    setResizable(false);
    setTitle(Lizzie.resourceBundle.getString("SetDiffAnalyze.title"));

    Container contentPane = getContentPane();
    contentPane.setLayout(new BorderLayout());

    initDialogPane(contentPane);

    pack();
    setLocationRelativeTo(getOwner());
  }

  private void initDialogPane(Container contentPane) {
    dialogPane.setBorder(new EmptyBorder(12, 12, 12, 12));
    dialogPane.setLayout(new BorderLayout());

    initContentPanel();
    initButtonBar();

    contentPane.add(dialogPane, BorderLayout.CENTER);
  }

  private void initContentPanel() {
    GridLayout gridLayout = new GridLayout(7, 2, 4, 4);
    contentPanel.setLayout(gridLayout);
    chkBlack = new JCheckBox();
    chkWhite = new JCheckBox();

    contentPanel.add(
        new JFontLabel(Lizzie.resourceBundle.getString("StartAnaDialog.analyzeBlack")));
    contentPanel.add(chkBlack);

    contentPanel.add(
        new JFontLabel(Lizzie.resourceBundle.getString("StartAnaDialog.analyzeWhite")));
    contentPanel.add(chkWhite);

    chkWin = new JCheckBox();
    chkScore = new JCheckBox();
    txtWinDiff = new JFontTextField();
    txtScoreDiff = new JFontTextField();
    txtScoreDiff.setColumns(6);
    txtWinDiff.setColumns(6);
    txtWinDiff.setDocument(new DoubleDocument());
    txtScoreDiff.setDocument(new DoubleDocument());
    JPanel winPanel = new JPanel();
    winPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
    winPanel.add(chkWin);
    winPanel.add(txtWinDiff);
    contentPanel.add(
        new JFontLabel(Lizzie.resourceBundle.getString("SetDiffAnalyze.lblWinThreshold")));
    contentPanel.add(winPanel);

    JPanel scorePanel = new JPanel();
    scorePanel.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
    scorePanel.add(chkScore);
    scorePanel.add(txtScoreDiff);
    contentPanel.add(
        new JFontLabel(Lizzie.resourceBundle.getString("SetDiffAnalyze.lblScoreThreshold")));
    contentPanel.add(scorePanel);

    txtTime = new JFontTextField();
    txtTime.setDocument(new IntDocument());
    txtPlayouts = new JFontTextField();
    txtPlayouts.setDocument(new IntDocument());
    txtFirstPlayouts = new JFontTextField();
    txtFirstPlayouts.setDocument(new IntDocument());

    contentPanel.add(new JFontLabel(Lizzie.resourceBundle.getString("StartAnaDialog.timePerMove")));
    contentPanel.add(txtTime);
    contentPanel.add(
        new JFontLabel(Lizzie.resourceBundle.getString("StartAnaDialog.totalVisitsPerMove")));
    contentPanel.add(txtPlayouts);
    contentPanel.add(
        new JFontLabel(Lizzie.resourceBundle.getString("StartAnaDialog.firstVisitsPerMove")));
    contentPanel.add(txtFirstPlayouts);

    chkBlack.setSelected(Lizzie.config.autoAnaDiffBlack);
    chkWhite.setSelected(Lizzie.config.autoAnaDiffWhite);
    chkWin.setSelected(Lizzie.config.autoAnaDiffUseWin);
    chkScore.setSelected(Lizzie.config.autoAnaDiffUseScore);
    if (Lizzie.config.autoAnaDiffWinThreshold > 0)
      txtWinDiff.setText(Lizzie.config.autoAnaDiffWinThreshold + "");
    if (Lizzie.config.autoAnaDiffScoreThreshold > 0)
      txtScoreDiff.setText(Lizzie.config.autoAnaDiffScoreThreshold + "");
    if (Lizzie.config.autoAnaDiffTime > 0) txtTime.setText(Lizzie.config.autoAnaDiffTime + "");
    if (Lizzie.config.autoAnaDiffPlayouts > 0)
      txtPlayouts.setText(Lizzie.config.autoAnaDiffPlayouts + "");
    if (Lizzie.config.autoAnaDiffFirstPlayouts > 0)
      txtFirstPlayouts.setText(Lizzie.config.autoAnaDiffFirstPlayouts + "");
    txtWinDiff.setEnabled(chkWin.isSelected());
    txtScoreDiff.setEnabled(chkScore.isSelected());
    chkWin.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            txtWinDiff.setEnabled(chkWin.isSelected());
          }
        });
    chkScore.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            txtScoreDiff.setEnabled(chkScore.isSelected());
          }
        });

    dialogPane.add(contentPanel, BorderLayout.CENTER);
  }

  private void initButtonBar() {
    buttonBar.setLayout(new GridBagLayout());
    buttonBar.setBorder(new EmptyBorder(12, 0, 0, 0));
    btnConfirm = new JFontButton(Lizzie.resourceBundle.getString("SetKataEngines.btnApply"));
    GridBagConstraints gbc_button = new GridBagConstraints();
    gbc_button.anchor = GridBagConstraints.EAST;
    gbc_button.insets = new Insets(0, 0, 0, 5);
    gbc_button.gridx = 0;
    gbc_button.gridy = 0;
    buttonBar.add(btnConfirm, gbc_button);
    btnConfirm.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {

            if (!chkBlack.isSelected() && !chkWhite.isSelected()) {
              Utils.showMsg(Lizzie.resourceBundle.getString("SetDiffAnalyze.noBlackWhite"));
              return;
            }
            if (!chkWin.isSelected() && !chkScore.isSelected()) {
              Utils.showMsg(Lizzie.resourceBundle.getString("SetDiffAnalyze.noWinScore"));
              return;
            }
            if (Utils.parseTextToInt(txtTime, -1) <= 0
                && Utils.parseTextToInt(txtPlayouts, -1) <= 0
                && Utils.parseTextToInt(txtFirstPlayouts, -1) <= 0) {
              Utils.showMsg(Lizzie.resourceBundle.getString("SetDiffAnalyze.noCondition"));
              return;
            }

            Lizzie.config.autoAnaDiffBlack = chkBlack.isSelected();
            Lizzie.config.autoAnaDiffWhite = chkWhite.isSelected();
            Lizzie.config.autoAnaDiffUseWin = chkWin.isSelected();
            Lizzie.config.autoAnaDiffUseScore = chkScore.isSelected();
            Lizzie.config.autoAnaDiffWinThreshold =
                Utils.parseTextToDouble(txtWinDiff, Lizzie.config.autoAnaDiffWinThreshold);
            Lizzie.config.autoAnaDiffScoreThreshold =
                Utils.parseTextToDouble(txtScoreDiff, Lizzie.config.autoAnaDiffScoreThreshold);
            Lizzie.config.autoAnaDiffTime = Utils.parseTextToInt(txtTime, -1);
            Lizzie.config.autoAnaDiffPlayouts = Utils.parseTextToInt(txtPlayouts, -1);
            Lizzie.config.autoAnaDiffFirstPlayouts = Utils.parseTextToInt(txtFirstPlayouts, -1);

            Lizzie.config.uiConfig.put("auto-ana-diff-black", Lizzie.config.autoAnaDiffBlack);
            Lizzie.config.uiConfig.put("auto-ana-diff-white", Lizzie.config.autoAnaDiffWhite);
            Lizzie.config.uiConfig.put("auto-ana-diff-use-win", Lizzie.config.autoAnaDiffUseWin);
            Lizzie.config.uiConfig.put(
                "auto-ana-diff-use-score", Lizzie.config.autoAnaDiffUseScore);
            Lizzie.config.uiConfig.put(
                "auto-ana-diff-win-threshold", Lizzie.config.autoAnaDiffWinThreshold);
            Lizzie.config.uiConfig.put(
                "auto-ana-diff-score-threshold", Lizzie.config.autoAnaDiffScoreThreshold);
            Lizzie.config.uiConfig.put("auto-ana-diff-time", Lizzie.config.autoAnaDiffTime);
            Lizzie.config.uiConfig.put("auto-ana-diff-playouts", Lizzie.config.autoAnaDiffPlayouts);
            Lizzie.config.uiConfig.put(
                "auto-ana-diff-first-playouts", Lizzie.config.autoAnaDiffFirstPlayouts);

            setVisible(false);
          }
        });

    dialogPane.add(buttonBar, BorderLayout.SOUTH);

    btnCancel = new JFontButton(Lizzie.resourceBundle.getString("SetKataEngines.btnCancel"));
    GridBagConstraints gbc_button_1 = new GridBagConstraints();
    gbc_button_1.anchor = GridBagConstraints.EAST;
    gbc_button_1.gridx = 1;
    gbc_button_1.gridy = 0;
    buttonBar.add(btnCancel, gbc_button_1);
    btnCancel.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            setVisible(false);
          }
        });
  }
}
