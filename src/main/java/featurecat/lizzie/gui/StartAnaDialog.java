/*
 * Created by JFormDesigner on Wed Apr 04 22:17:33 CEST 2018
 */

package featurecat.lizzie.gui;

import featurecat.lizzie.Config;
import featurecat.lizzie.Lizzie;
import featurecat.lizzie.util.Utils;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Timer;
import java.util.TimerTask;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

/** @author unknown */
public class StartAnaDialog extends JDialog {
  // create formatters
  public static final DecimalFormat FORMAT_KOMI = new DecimalFormat("#0.0");
  public static final DecimalFormat FORMAT_HANDICAP = new DecimalFormat("0");
  public static final JFontLabel PLACEHOLDER = new JFontLabel("");

  static {
    FORMAT_HANDICAP.setMaximumIntegerDigits(1);
  }

  private JDialog thisDialog = this;
  private JPanel dialogPane = new JPanel();
  private JPanel contentPanel = new JPanel();
  private JPanel buttonBar = new JPanel();
  private JFontButton okButton = new JFontButton();
  private JFontButton stopButton = new JFontButton();
  private JFontCheckBox chkAnalyzeAllBracnh; //
  private JTextField txtAnalysisPlayouts;
  private boolean cancelled = true;
  private JCheckBox chkUseDiff;
  private boolean isAnalysisMode = false;

  public StartAnaDialog(boolean isAnalysisMode) {
    this.isAnalysisMode = isAnalysisMode;
    initComponents();

    addWindowListener(
        new WindowAdapter() {
          public void windowClosing(WindowEvent e) {
            setTxtFontSize(true);
            Lizzie.frame.isBatchAna = false;
          }
        });
  }

  private void initComponents() {
    setMinimumSize(new Dimension(100, 150));
    setResizable(false);
    setTitle(Lizzie.resourceBundle.getString("StartAnaDialog.title")); // ("自动分析设置");
    setModal(true);
    try {
      this.setIconImage(ImageIO.read(MoreEngines.class.getResourceAsStream("/assets/logo.png")));
    } catch (IOException e) {
      e.printStackTrace();
    }
    Container contentPane = getContentPane();
    contentPane.setLayout(new BorderLayout());

    initDialogPane(contentPane);
    setAlwaysOnTop(Lizzie.frame.isAlwaysOnTop());
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
    GridLayout gridLayout =
        new GridLayout(isAnalysisMode ? 3 : Lizzie.frame.isBatchAna ? 9 : 10, 2, 4, 4);
    contentPanel.setLayout(gridLayout);

    //  checkBoxPlayerIsBlack =
    //      new JFontCheckBox(resourceBundle.getString("NewGameDialog.PlayBlack"), true);
    //   checkBoxPlayerIsBlack.addChangeListener(evt -> togglePlayerIsBlack());

    // contentPanel.add(checkBoxPlayerIsBlack);
    // contentPanel.add(PLACEHOLDER);
    // textFieldDelay = new JFormattedTextField(FORMAT_HANDICAP);
    Lizzie.frame.toolbar.chkAnaBlack.setText("");
    Lizzie.frame.toolbar.chkAnaWhite.setText("");
    contentPanel.add(
        new JFontLabel(
            Lizzie.frame.isBatchAna
                ? Lizzie.resourceBundle.getString("StartAnaDialog.startMoveBatch")
                : Lizzie.resourceBundle.getString("StartAnaDialog.startMove"))); // ("开始手数(选填)"));
    contentPanel.add(Lizzie.frame.toolbar.txtFirstAnaMove);
    contentPanel.add(
        new JFontLabel(
            Lizzie.resourceBundle.getString("StartAnaDialog.endMove"))); // ("结束手数(选填)"));
    contentPanel.add(Lizzie.frame.toolbar.txtLastAnaMove);
    if (isAnalysisMode) {
      NumberFormat nf = NumberFormat.getIntegerInstance();
      nf.setGroupingUsed(false);
      txtAnalysisPlayouts = new JTextField();
      txtAnalysisPlayouts.setDocument(new IntDocument());
      txtAnalysisPlayouts.setText(Lizzie.config.batchAnalysisPlayouts + "");
      txtAnalysisPlayouts.setFont(
          new Font(Config.sysDefaultFontName, Font.PLAIN, Config.frameFontSize));
      contentPanel.add(
          new JFontLabel(
              Lizzie.resourceBundle.getString(
                  "StartAnaDialog.totalVisitsPerMove"))); // ("每手总计算量"));
      contentPanel.add(txtAnalysisPlayouts);
    } else {
      contentPanel.add(
          new JFontLabel(
              Lizzie.resourceBundle.getString("StartAnaDialog.timePerMove"))); // ("每手时间(秒)"));
      contentPanel.add(Lizzie.frame.toolbar.txtAnaTime);
      contentPanel.add(
          new JFontLabel(
              Lizzie.resourceBundle.getString(
                  "StartAnaDialog.totalVisitsPerMove"))); // ("每手总计算量"));
      contentPanel.add(Lizzie.frame.toolbar.txtAnaPlayouts);
      contentPanel.add(
          new JFontLabel(
              Lizzie.resourceBundle.getString(
                  "StartAnaDialog.firstVisitsPerMove"))); // ("每手首位计算量"));
      contentPanel.add(Lizzie.frame.toolbar.txtAnaFirstPlayouts);
      contentPanel.add(
          new JFontLabel(
              Lizzie.resourceBundle.getString("StartAnaDialog.analyzeBlack"))); // ("分析黑棋"));
      contentPanel.add(Lizzie.frame.toolbar.chkAnaBlack);
      contentPanel.add(
          new JFontLabel(
              Lizzie.resourceBundle.getString("StartAnaDialog.analyzeWhite"))); // ("分析白棋"));
      contentPanel.add(Lizzie.frame.toolbar.chkAnaWhite);
      contentPanel.add(
          new JFontLabel(
              Lizzie.resourceBundle.getString("StartAnaDialog.analyzeAllBranch"))); // ("分析所有分支"));
      chkAnalyzeAllBracnh = new JFontCheckBox();
      chkAnalyzeAllBracnh.setSelected(Lizzie.config.analyzeAllBranch);
      chkAnalyzeAllBracnh.addActionListener(
          new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
              Lizzie.config.analyzeAllBranch = chkAnalyzeAllBracnh.isSelected();
              Lizzie.config.uiConfig.put("analyze-all-branch", Lizzie.config.analyzeAllBranch);
            }
          });
      contentPanel.add(chkAnalyzeAllBracnh);
    }
    if (!isAnalysisMode) {
      contentPanel.add(
          new JFontLabel(
              Lizzie.resourceBundle.getString("StartAnaDialog.lblDiffAnalyze"))); // 波动过大时加强分析
      JPanel bigMistakeSetPanel = new JPanel();
      JButton btnSetDiff =
          new JFontButton(Lizzie.resourceBundle.getString("StartAnaDialog.btnSetDiff"));
      btnSetDiff.addActionListener(
          new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
              SetDiffAnalyze setDiffAnalyze = new SetDiffAnalyze(thisDialog);
              setDiffAnalyze.setVisible(true);
            }
          });
      bigMistakeSetPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
      chkUseDiff = new JCheckBox();
      chkUseDiff.setSelected(Lizzie.config.autoAnaDiffEnable);
      btnSetDiff.setEnabled(Lizzie.config.autoAnaDiffEnable);
      chkUseDiff.addActionListener(
          new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
              btnSetDiff.setEnabled(chkUseDiff.isSelected());
            }
          });
      bigMistakeSetPanel.add(chkUseDiff);
      bigMistakeSetPanel.add(btnSetDiff);
      contentPanel.add(bigMistakeSetPanel);
    }
    if (!Lizzie.frame.isBatchAna) {
      contentPanel.add(
          new JFontLabel(
              Lizzie.resourceBundle.getString("StartAnaDialog.autoSaveKifu"))); // ("自动保存棋谱"));
      contentPanel.add(Lizzie.frame.toolbar.chkAnaAutoSave);
    }
    //    if (Lizzie.config.showHiddenYzy) {
    //      contentPanel.add(new JFontLabel("延时(分)"));
    //      contentPanel.add(textFieldDelay);
    //    }

    dialogPane.add(contentPanel, BorderLayout.CENTER);
    setTxtFontSize(false);
  }

  private void setTxtFontSize(boolean isReset) {
    if (isReset) {
      Lizzie.frame.toolbar.txtFirstAnaMove.setFont(
          new Font(Config.sysDefaultFontName, Font.PLAIN, 12));
      Lizzie.frame.toolbar.txtLastAnaMove.setFont(
          new Font(Config.sysDefaultFontName, Font.PLAIN, 12));
      Lizzie.frame.toolbar.txtAnaTime.setFont(new Font(Config.sysDefaultFontName, Font.PLAIN, 12));
      Lizzie.frame.toolbar.txtAnaPlayouts.setFont(
          new Font(Config.sysDefaultFontName, Font.PLAIN, 12));
      Lizzie.frame.toolbar.txtAnaFirstPlayouts.setFont(
          new Font(Config.sysDefaultFontName, Font.PLAIN, 12));
    } else {
      Lizzie.frame.toolbar.txtFirstAnaMove.setFont(
          new Font(Config.sysDefaultFontName, Font.PLAIN, Config.frameFontSize));
      Lizzie.frame.toolbar.txtLastAnaMove.setFont(
          new Font(Config.sysDefaultFontName, Font.PLAIN, Config.frameFontSize));
      Lizzie.frame.toolbar.txtAnaTime.setFont(
          new Font(Config.sysDefaultFontName, Font.PLAIN, Config.frameFontSize));
      Lizzie.frame.toolbar.txtAnaPlayouts.setFont(
          new Font(Config.sysDefaultFontName, Font.PLAIN, Config.frameFontSize));
      Lizzie.frame.toolbar.txtAnaFirstPlayouts.setFont(
          new Font(Config.sysDefaultFontName, Font.PLAIN, Config.frameFontSize));
    }
  }

  private void initButtonBar() {
    buttonBar.setBorder(new EmptyBorder(12, 0, 0, 0));
    buttonBar.setLayout(new GridBagLayout());
    ((GridBagLayout) buttonBar.getLayout()).columnWidths = new int[] {0, 80};
    ((GridBagLayout) buttonBar.getLayout()).columnWeights = new double[] {1.0, 0.0};

    // ---- okButton ----
    okButton.setText(Lizzie.resourceBundle.getString("StartAnaDialog.startAnalyze")); // ("开始分析");
    okButton.addActionListener(e -> apply());

    stopButton.setText(Lizzie.resourceBundle.getString("StartAnaDialog.stopAnalyze")); // ("终止分析");
    stopButton.addActionListener(e -> stop());

    int center = GridBagConstraints.CENTER;
    int both = GridBagConstraints.BOTH;
    buttonBar.add(
        okButton,
        new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, center, both, new Insets(0, 0, 0, 0), 0, 0));
    buttonBar.add(
        stopButton,
        new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0, center, both, new Insets(0, 0, 0, 0), 0, 0));

    dialogPane.add(buttonBar, BorderLayout.SOUTH);
  }

  public void stop() {
    this.setVisible(false);
    setTxtFontSize(true);
    Lizzie.frame.toolbar.resetAutoAna();
    Lizzie.frame.toolbar.stopAutoAna(true, true);
  }

  public void apply() {
    try {
      Lizzie.frame.toolbar.firstMove =
          Integer.parseInt(Lizzie.frame.toolbar.txtFirstAnaMove.getText().replace(" ", ""));
    } catch (Exception ex) {
      Lizzie.frame.toolbar.firstMove = -1;
    }
    try {
      Lizzie.frame.toolbar.lastMove =
          Integer.parseInt(Lizzie.frame.toolbar.txtLastAnaMove.getText().replace(" ", ""));
    } catch (Exception ex) {
      Lizzie.frame.toolbar.lastMove = -1;
    }
    if (!isAnalysisMode) {
      try {
        if (Integer.parseInt(Lizzie.frame.toolbar.txtAnaTime.getText().replace(" ", "")) > 0)
          Lizzie.frame.toolbar.chkAnaTime.setSelected(true);
        else Lizzie.frame.toolbar.chkAnaTime.setSelected(false);
      } catch (Exception ex) {
        Lizzie.frame.toolbar.chkAnaTime.setSelected(false);
      }
      try {
        if (Integer.parseInt(Lizzie.frame.toolbar.txtAnaPlayouts.getText().replace(" ", "")) > 0)
          Lizzie.frame.toolbar.chkAnaPlayouts.setSelected(true);
        else Lizzie.frame.toolbar.chkAnaPlayouts.setSelected(false);
      } catch (Exception ex) {
        Lizzie.frame.toolbar.chkAnaPlayouts.setSelected(false);
      }
      try {
        if (Integer.parseInt(Lizzie.frame.toolbar.txtAnaFirstPlayouts.getText().replace(" ", ""))
            > 0) Lizzie.frame.toolbar.chkAnaFirstPlayouts.setSelected(true);
        else Lizzie.frame.toolbar.chkAnaFirstPlayouts.setSelected(false);
      } catch (Exception ex) {
        Lizzie.frame.toolbar.chkAnaFirstPlayouts.setSelected(false);
      }

      if (!Lizzie.frame.toolbar.chkAnaBlack.isSelected()
          && !Lizzie.frame.toolbar.chkAnaWhite.isSelected()) {
        Utils.showMsg(Lizzie.resourceBundle.getString("SetDiffAnalyze.noBlackWhite"));
        return;
      }

      if (!Lizzie.frame.toolbar.chkAnaFirstPlayouts.isSelected()
          && !Lizzie.frame.toolbar.chkAnaPlayouts.isSelected()
          && !Lizzie.frame.toolbar.chkAnaTime.isSelected()) {
        Utils.showMsg(Lizzie.resourceBundle.getString("SetDiffAnalyze.noCondition"));
        return;
      }
    }
    Lizzie.config.autoAnaDiffEnable = chkUseDiff.isSelected();
    Lizzie.config.uiConfig.put("auto-ana-diff-enable", Lizzie.config.autoAnaDiffEnable);

    Lizzie.leelaz.nameCmd();
    cancelled = false;
    this.setVisible(false);
    setTxtFontSize(true);
    if (isAnalysisMode) {
      Lizzie.config.batchAnalysisPlayouts =
          Utils.parseTextToInt(txtAnalysisPlayouts, Lizzie.config.batchAnalysisPlayouts);
      Lizzie.config.uiConfig.put("batch-analysis-playouts", Lizzie.config.batchAnalysisPlayouts);
      Lizzie.frame.flashAnalyzeGameBatch(
          Lizzie.frame.toolbar.firstMove, Lizzie.frame.toolbar.lastMove);
    } else {
      Timer timer = new Timer();
      timer.schedule(
          new TimerTask() {
            public void run() {
              Lizzie.frame.toolbar.startAutoAna();
              this.cancel();
            }
          },
          300);
      Lizzie.frame.toolbar.chkAutoAnalyse.setSelected(true);
    }
    Lizzie.frame.toolbar.resetAutoAna();
  }

  public boolean isCancelled() {

    return cancelled;
  }

  //  public static void main(String[] args) {
  //    EventQueue.invokeLater(
  //        () -> {
  //          try {
  //            StartAnaDialog window = new StartAnaDialog();
  //            window.setVisible(true);
  //          } catch (Exception e) {
  //            e.printStackTrace();
  //          }
  //        });
  //  }
}
