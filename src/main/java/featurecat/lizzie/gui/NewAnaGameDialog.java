/*
 * Created by JFormDesigner on Wed Apr 04 22:17:33 CEST 2018
 */

package featurecat.lizzie.gui;

import featurecat.lizzie.Config;
import featurecat.lizzie.Lizzie;
import featurecat.lizzie.analysis.EngineManager;
import featurecat.lizzie.analysis.GameInfo;
import featurecat.lizzie.rules.Board;
import featurecat.lizzie.rules.Stone;
import featurecat.lizzie.util.Utils;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

/** @author unknown */
public class NewAnaGameDialog extends JDialog {
  // create formatters
  public static final DecimalFormat FORMAT_KOMI = new DecimalFormat("#0.0");
  public static final DecimalFormat FORMAT_HANDICAP = new DecimalFormat("0");
  // public static final JFontLabel PLACEHOLDER = new JFontLabel("");

  static {
    FORMAT_HANDICAP.setMaximumIntegerDigits(99999);
  }

  private JPanel dialogPane = new JPanel();
  private JPanel contentPanel = new JPanel();
  private JPanel buttonBar = new JPanel();
  private JFontButton okButton = new JFontButton();
  private JFontComboBox<String> engine;

  private JFontComboBox<String> checkBoxPlayerIsBlack;
  private JFontCheckBox checkContinuePlay;
  // private JFontTextField textFieldBlack;
  // private JFontTextField textFieldWhite;
  private JFontCheckBox chkLimitMyTime;
  private JFontComboBox<String> textSaveTime;
  private JFontComboBox<String> texByoSeconds;
  private JFontComboBox<String> texByoTimes;
  private JFontCheckBox chkAiPureNet;
  private JFontTextField textFieldKomi;
  private JFontComboBox<Integer> textFieldHandicap;
  private JTextField textTime;
  private JTextField textPlayouts;
  private JTextField textFirstPlayouts;
  private JFontCheckBox chkPonder;
  private JFontCheckBox chkUsePlayMode;
  private JFontCheckBox chkShowBlack;
  private JFontCheckBox chkShowWhite;
  private JPanel panelResign;
  private JFontTextField textResignStartMove;
  private JFontTextField textResignMove;
  private JFontTextField textResignPercent;
  private JCheckBox chkUseTime;
  private JCheckBox chkUsePlayouts;
  private JCheckBox chkUseFirstPlayouts;

  private boolean cancelled = true;
  public GameInfo gameInfo = new GameInfo();
  private int handicap;
  private double komi;

  public NewAnaGameDialog(Window owner) {
    super(owner);
    initComponents();
  }

  private ResourceBundle resourceBundle = // ResourceBundle.getBundle("l10n.DisplayStrings");
      Lizzie.resourceBundle;
  //          ? ResourceBundle.getBundle("l10n.DisplayStrings")
  //          : (Lizzie.config.useLanguage == 1
  //              ? ResourceBundle.getBundle("l10n.DisplayStrings", new Locale("zh", "CN"))
  //              : ResourceBundle.getBundle("l10n.DisplayStrings", new Locale("en", "US")));

  private void initComponents() {
    setMinimumSize(new Dimension(100, 150));
    setResizable(false);
    setTitle(resourceBundle.getString("NewAnaGameDialog.title")); // ("新对局(分析模式)");
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
    NumberFormat nf = NumberFormat.getIntegerInstance();
    nf.setGroupingUsed(false);
    engine = new JFontComboBox<String>();
    ArrayList<EngineData> engineData = Utils.getEngineData();
    for (int i = 0; i < engineData.size(); i++) {
      EngineData engineDt = engineData.get(i);
      engine.addItem("[" + (i + 1) + "]" + engineDt.name);
    }
    if (engine.getItemCount() > 0)
      engine.setSelectedIndex(
          EngineManager.currentEngineNo >= 0 ? EngineManager.currentEngineNo : 0);
    GridBagLayout gbl_contentPanel = new GridBagLayout();
    gbl_contentPanel.columnWidths = new int[] {225, 280, 0};
    gbl_contentPanel.rowHeights =
        new int[] {31, 31, 31, 31, 31, 31, 0, 31, 31, 31, 31, 31, 31, 0, 31, 31, 31, 31};
    gbl_contentPanel.columnWeights = new double[] {0.0, 0.0, Double.MIN_VALUE};
    gbl_contentPanel.rowWeights =
        new double[] {
          0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0
        };
    contentPanel.setLayout(gbl_contentPanel);
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.fill = GridBagConstraints.BOTH;
    gbc.insets = new Insets(0, 0, 5, 5);
    gbc.gridx = 0;
    gbc.gridy = 0;
    JFontLabel label_11 = new JFontLabel(resourceBundle.getString("NewAnaGameDialog.chooseEngine"));
    contentPanel.add(label_11, gbc);

    GridBagConstraints gbc_engine = new GridBagConstraints();
    gbc_engine.fill = GridBagConstraints.BOTH;
    gbc_engine.insets = new Insets(0, 0, 5, 0);
    gbc_engine.gridx = 1;
    gbc_engine.gridy = 0;
    contentPanel.add(engine, gbc_engine);
    JFontLabel label = new JFontLabel(resourceBundle.getString("NewGameDialog.chooseBlackWhite"));
    GridBagConstraints gbc_label = new GridBagConstraints();
    gbc_label.fill = GridBagConstraints.BOTH;
    gbc_label.insets = new Insets(0, 0, 5, 5);
    gbc_label.gridx = 0;
    gbc_label.gridy = 1;
    contentPanel.add(label, gbc_label);
    textFieldHandicap = new JFontComboBox<Integer>();
    textFieldHandicap.addItem(0);
    textFieldHandicap.addItem(2);
    textFieldHandicap.addItem(3);
    textFieldHandicap.addItem(4);
    textFieldHandicap.addItem(5);
    textFieldHandicap.addItem(6);
    textFieldHandicap.addItem(7);
    textFieldHandicap.addItem(8);
    textFieldHandicap.addItem(9);
    JPanel handicapPanel = new JPanel();
    JFontCheckBox chkUseFreeHandicap;
    chkUseFreeHandicap =
        new JFontCheckBox(resourceBundle.getString("NewGameDialog.useFreeHandicap"));
    handicapPanel.add(textFieldHandicap);
    handicapPanel.add(chkUseFreeHandicap);
    chkUseFreeHandicap.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            Lizzie.config.useFreeHandicap = chkUseFreeHandicap.isSelected();
            Lizzie.config.uiConfig.put("use-free-handicap", Lizzie.config.useFreeHandicap);
          }
        });
    chkUseFreeHandicap.setSelected(Lizzie.config.useFreeHandicap);
    checkBoxPlayerIsBlack =
        new JFontComboBox<String>(); // resourceBundle.getString("NewGameDialog.PlayBlack"));
    checkBoxPlayerIsBlack.addItem(resourceBundle.getString("NewGameDialog.playBlack"));
    checkBoxPlayerIsBlack.addItem(resourceBundle.getString("NewGameDialog.playWhite"));
    checkBoxPlayerIsBlack.setFocusable(false);
    GridBagConstraints gbc_checkBoxPlayerIsBlack = new GridBagConstraints();
    gbc_checkBoxPlayerIsBlack.fill = GridBagConstraints.BOTH;
    gbc_checkBoxPlayerIsBlack.insets = new Insets(0, 0, 5, 0);
    gbc_checkBoxPlayerIsBlack.gridx = 1;
    gbc_checkBoxPlayerIsBlack.gridy = 1;
    contentPanel.add(checkBoxPlayerIsBlack, gbc_checkBoxPlayerIsBlack);

    checkBoxPlayerIsBlack.setSelectedIndex(Lizzie.config.checkPlayBlack ? 0 : 1);
    JFontLabel label_1 = new JFontLabel(resourceBundle.getString("NewGameDialog.ContinuePlay"));
    GridBagConstraints gbc_label_1 = new GridBagConstraints();
    gbc_label_1.fill = GridBagConstraints.BOTH;
    gbc_label_1.insets = new Insets(0, 0, 5, 5);
    gbc_label_1.gridx = 0;
    gbc_label_1.gridy = 2;
    contentPanel.add(label_1, gbc_label_1);
    checkContinuePlay = new JFontCheckBox();
    checkContinuePlay.setFocusable(false);
    GridBagConstraints gbc_checkContinuePlay = new GridBagConstraints();
    gbc_checkContinuePlay.fill = GridBagConstraints.BOTH;
    gbc_checkContinuePlay.insets = new Insets(0, 0, 5, 0);
    gbc_checkContinuePlay.gridx = 1;
    gbc_checkContinuePlay.gridy = 2;
    contentPanel.add(checkContinuePlay, gbc_checkContinuePlay);

    checkContinuePlay.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            if (checkContinuePlay.isSelected()) textFieldHandicap.setEnabled(false);
            else textFieldHandicap.setEnabled(true);
          }
        });
    checkContinuePlay.setSelected(Lizzie.config.checkContinuePlay);
    GridBagConstraints gbc_1 = new GridBagConstraints();
    gbc_1.fill = GridBagConstraints.BOTH;
    gbc_1.insets = new Insets(0, 0, 5, 5);
    gbc_1.gridx = 0;
    gbc_1.gridy = 3;
    JFontLabel lblHandicap = new JFontLabel(resourceBundle.getString("NewGameDialog.Handicap"));
    contentPanel.add(lblHandicap, gbc_1); // ("让子(仅支持19路棋盘)"));
    GridBagConstraints gbc_textFieldHandicap = new GridBagConstraints();
    gbc_textFieldHandicap.anchor = GridBagConstraints.WEST;
    gbc_textFieldHandicap.fill = GridBagConstraints.VERTICAL;
    gbc_textFieldHandicap.insets = new Insets(0, 0, 5, 0);
    gbc_textFieldHandicap.gridx = 1;
    gbc_textFieldHandicap.gridy = 3;
    contentPanel.add(handicapPanel, gbc_textFieldHandicap);
    textFieldHandicap.setSelectedIndex(Lizzie.config.newGameHandicap);
    JFontLabel label_2 = new JFontLabel(resourceBundle.getString("NewGameDialog.Komi"));
    GridBagConstraints gbc_label_2 = new GridBagConstraints();
    gbc_label_2.fill = GridBagConstraints.BOTH;
    gbc_label_2.insets = new Insets(0, 0, 5, 5);
    gbc_label_2.gridx = 0;
    gbc_label_2.gridy = 4;
    contentPanel.add(label_2, gbc_label_2);
    // checkBoxPlayerIsBlack.addChangeListener(evt -> togglePlayerIsBlack());
    // textFieldWhite = new JFontTextField();
    //   textFieldBlack = new JFontTextField();
    textFieldKomi = new JFontTextField();
    textFieldKomi.setDocument(new KomiDocument(true));
    GridBagConstraints gbc_textFieldKomi = new GridBagConstraints();
    gbc_textFieldKomi.fill = GridBagConstraints.BOTH;
    gbc_textFieldKomi.insets = new Insets(0, 0, 5, 0);
    gbc_textFieldKomi.gridx = 1;
    gbc_textFieldKomi.gridy = 4;
    contentPanel.add(textFieldKomi, gbc_textFieldKomi);

    // togglePlayerIsBlack();
    textFieldKomi.setEnabled(true);
    textFieldKomi.setText(String.valueOf(Lizzie.config.newGameKomi));
    GridBagConstraints gbc_2 = new GridBagConstraints();
    gbc_2.fill = GridBagConstraints.BOTH;
    gbc_2.insets = new Insets(0, 0, 5, 5);
    gbc_2.gridx = 0;
    gbc_2.gridy = 5;

    chkLimitMyTime = new JFontCheckBox(resourceBundle.getString("Byoyomi.newGame.limitMyTime"));
    contentPanel.add(chkLimitMyTime, gbc_2);
    JPanel myTimePanle = new JPanel();
    textSaveTime = new JFontComboBox<String>();
    texByoSeconds = new JFontComboBox<String>();
    texByoTimes = new JFontComboBox<String>();
    textSaveTime.addItem(resourceBundle.getString("Byoyomi.none"));
    textSaveTime.addItem("5");
    textSaveTime.addItem("10");
    textSaveTime.addItem("20");
    textSaveTime.addItem("30");
    textSaveTime.addItem("60");
    textSaveTime.addItem("120");
    textSaveTime.addItem("180");

    texByoSeconds.addItem(resourceBundle.getString("Byoyomi.none"));
    texByoSeconds.addItem("10");
    texByoSeconds.addItem("15");
    texByoSeconds.addItem("20");
    texByoSeconds.addItem("30");
    texByoSeconds.addItem("40");
    texByoSeconds.addItem("60");
    texByoSeconds.addItem("120");

    texByoTimes.addItem("1");
    texByoTimes.addItem("2");
    texByoTimes.addItem("3");
    texByoTimes.addItem("5");
    texByoTimes.addItem("10");
    texByoTimes.addItem("20");

    chkLimitMyTime.setSelected(Lizzie.config.limitMyTime);
    textSaveTime.setSelectedIndex(Lizzie.config.mySaveTime);
    texByoSeconds.setSelectedIndex(Lizzie.config.myByoyomiSeconds);
    texByoTimes.setSelectedIndex(Lizzie.config.myByoyomiTimes);

    textSaveTime.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            if (textSaveTime.getSelectedIndex() == 0) {
              if (texByoSeconds.getSelectedIndex() == 0) {
                Utils.showMsg(resourceBundle.getString("Byoyomi.emptyTimeHint"));
                textSaveTime.setSelectedIndex(1);
              }
            }
          }
        });

    texByoSeconds.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            if (texByoSeconds.getSelectedIndex() == 0) {
              if (textSaveTime.getSelectedIndex() == 0) {
                Utils.showMsg(resourceBundle.getString("Byoyomi.emptyTimeHint"));
                texByoSeconds.setSelectedIndex(1);
                return;
              }
              texByoTimes.setEnabled(false);
            } else {
              texByoTimes.setEnabled(true);
            }
          }
        });

    chkLimitMyTime.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            if (chkLimitMyTime.isSelected()) {
              textSaveTime.setEnabled(true);
              texByoSeconds.setEnabled(true);
              texByoTimes.setEnabled(true);
            } else {
              textSaveTime.setEnabled(false);
              texByoSeconds.setEnabled(false);
              texByoTimes.setEnabled(false);
            }
          }
        });

    if (chkLimitMyTime.isSelected()) {
      textSaveTime.setEnabled(true);
      texByoSeconds.setEnabled(true);
      texByoTimes.setEnabled(true);
      if (texByoSeconds.getSelectedIndex() == 0) {
        texByoTimes.setEnabled(false);
      } else {
        texByoTimes.setEnabled(true);
      }
    } else {
      textSaveTime.setEnabled(false);
      texByoSeconds.setEnabled(false);
      texByoTimes.setEnabled(false);
    }

    myTimePanle.add(new JFontLabel(resourceBundle.getString("Byoyomi.newGame.saveTime")));
    myTimePanle.add(textSaveTime);
    myTimePanle.add(new JFontLabel(resourceBundle.getString("Byoyomi.newGame.byoyomi")));
    myTimePanle.add(texByoSeconds);
    myTimePanle.add(new JFontLabel(resourceBundle.getString("Byoyomi.newGame.byoyomiTimes")));
    myTimePanle.add(texByoTimes);
    GridBagConstraints gbc_myTimePanle = new GridBagConstraints();
    gbc_myTimePanle.fill = GridBagConstraints.BOTH;
    gbc_myTimePanle.insets = new Insets(0, 0, 5, 0);
    gbc_myTimePanle.gridx = 1;
    gbc_myTimePanle.gridy = 5;
    contentPanel.add(myTimePanle, gbc_myTimePanle);

    JFontLabel lblAiUsePureNet =
        new JFontLabel(
            resourceBundle.getString("NewAnaGameDialog.lblAiUsePureNet")); // ("AI只使用纯网络思考");
    GridBagConstraints gbc_lblAi = new GridBagConstraints();
    gbc_lblAi.fill = GridBagConstraints.BOTH;
    gbc_lblAi.insets = new Insets(0, 0, 5, 5);
    gbc_lblAi.gridx = 0;
    gbc_lblAi.gridy = 6;
    contentPanel.add(lblAiUsePureNet, gbc_lblAi);

    chkAiPureNet = new JFontCheckBox();
    chkAiPureNet.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            Lizzie.config.UsePureNetInGame = chkAiPureNet.isSelected();
            Lizzie.config.uiConfig.put("use-pure-net-in-game", Lizzie.config.UsePureNetInGame);
            if (Lizzie.config.UsePureNetInGame) {
              textTime.setEnabled(false);
              textFirstPlayouts.setEnabled(false);
              textPlayouts.setEnabled(false);
            } else {
              textTime.setEnabled(chkUseTime.isSelected());
              textFirstPlayouts.setEnabled(chkUseFirstPlayouts.isSelected());
              textPlayouts.setEnabled(chkUsePlayouts.isSelected());
            }
          }
        });
    GridBagConstraints gbc_checkBox = new GridBagConstraints();
    gbc_checkBox.anchor = GridBagConstraints.WEST;
    gbc_checkBox.insets = new Insets(0, 0, 5, 0);
    gbc_checkBox.gridx = 1;
    gbc_checkBox.gridy = 6;
    contentPanel.add(chkAiPureNet, gbc_checkBox);

    GridBagConstraints gbc_3 = new GridBagConstraints();
    gbc_3.fill = GridBagConstraints.BOTH;
    gbc_3.insets = new Insets(0, 0, 5, 5);
    gbc_3.gridx = 0;
    gbc_3.gridy = 7;
    chkUseTime = new JFontCheckBox(resourceBundle.getString("NewAnaGameDialog.moveTime"));
    contentPanel.add(chkUseTime, gbc_3); // ("AI每手用时(秒)"));
    // textFieldHandicap.addPropertyChangeListener(evt -> modifyHandicap());
    textTime = new JTextField();
    textTime.setDocument(new IntDocument());
    textTime.setFont(new Font(Config.sysDefaultFontName, Font.PLAIN, Config.frameFontSize));
    textTime.setText(String.valueOf(Lizzie.config.maxGameThinkingTimeSeconds));
    chkUseTime.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            textTime.setEnabled(chkUseTime.isSelected());
          }
        });
    chkUseTime.setSelected(LizzieFrame.toolbar.chkAutoPlayTime.isSelected());
    textTime.setEnabled(LizzieFrame.toolbar.chkAutoPlayTime.isSelected());
    GridBagConstraints gbc_textTime = new GridBagConstraints();
    gbc_textTime.fill = GridBagConstraints.BOTH;
    gbc_textTime.insets = new Insets(0, 0, 5, 0);
    gbc_textTime.gridx = 1;
    gbc_textTime.gridy = 7;
    contentPanel.add(textTime, gbc_textTime);
    GridBagConstraints gbc_4 = new GridBagConstraints();
    gbc_4.fill = GridBagConstraints.BOTH;
    gbc_4.insets = new Insets(0, 0, 5, 5);
    gbc_4.gridx = 0;
    gbc_4.gridy = 8;
    chkUsePlayouts = new JFontCheckBox(resourceBundle.getString("NewAnaGameDialog.moveVisits"));
    contentPanel.add(chkUsePlayouts, gbc_4); // ("AI每手总计算量(选填)"));
    textPlayouts = new JTextField();
    textPlayouts.setDocument(new IntDocument());
    textPlayouts.setFont(new Font(Config.sysDefaultFontName, Font.PLAIN, Config.frameFontSize));
    textPlayouts.setText(LizzieFrame.toolbar.txtAutoPlayPlayouts.getText());
    chkUsePlayouts.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            textPlayouts.setEnabled(chkUsePlayouts.isSelected());
          }
        });
    chkUsePlayouts.setSelected(LizzieFrame.toolbar.chkAutoPlayPlayouts.isSelected());
    textPlayouts.setEnabled(LizzieFrame.toolbar.chkAutoPlayPlayouts.isSelected());
    GridBagConstraints gbc_textPlayouts = new GridBagConstraints();
    gbc_textPlayouts.fill = GridBagConstraints.BOTH;
    gbc_textPlayouts.insets = new Insets(0, 0, 5, 0);
    gbc_textPlayouts.gridx = 1;
    gbc_textPlayouts.gridy = 8;
    contentPanel.add(textPlayouts, gbc_textPlayouts);
    GridBagConstraints gbc_5 = new GridBagConstraints();
    gbc_5.fill = GridBagConstraints.BOTH;
    gbc_5.insets = new Insets(0, 0, 5, 5);
    gbc_5.gridx = 0;
    gbc_5.gridy = 9;
    chkUseFirstPlayouts =
        new JFontCheckBox(resourceBundle.getString("NewAnaGameDialog.firstVisits"));
    contentPanel.add(chkUseFirstPlayouts, gbc_5); // ("AI每手首位计算量(选填)"));
    textFirstPlayouts = new JTextField();
    textFirstPlayouts.setDocument(new IntDocument());
    textFirstPlayouts.setFont(
        new Font(Config.sysDefaultFontName, Font.PLAIN, Config.frameFontSize));
    textFirstPlayouts.setText(LizzieFrame.toolbar.txtAutoPlayFirstPlayouts.getText());
    chkUseFirstPlayouts.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            textFirstPlayouts.setEnabled(chkUseFirstPlayouts.isSelected());
          }
        });
    chkUseFirstPlayouts.setSelected(LizzieFrame.toolbar.chkAutoPlayFirstPlayouts.isSelected());
    textFirstPlayouts.setEnabled(LizzieFrame.toolbar.chkAutoPlayFirstPlayouts.isSelected());
    GridBagConstraints gbc_textFirstPlayouts = new GridBagConstraints();
    gbc_textFirstPlayouts.fill = GridBagConstraints.BOTH;
    gbc_textFirstPlayouts.insets = new Insets(0, 0, 5, 0);
    gbc_textFirstPlayouts.gridx = 1;
    gbc_textFirstPlayouts.gridy = 9;
    contentPanel.add(textFirstPlayouts, gbc_textFirstPlayouts);
    if (Lizzie.config.UsePureNetInGame) {
      textTime.setEnabled(false);
      textFirstPlayouts.setEnabled(false);
      textPlayouts.setEnabled(false);
      chkAiPureNet.setSelected(true);
    } else {
      chkAiPureNet.setSelected(false);
    }
    GridBagConstraints gbc_6 = new GridBagConstraints();
    gbc_6.fill = GridBagConstraints.BOTH;
    gbc_6.insets = new Insets(0, 0, 5, 5);
    gbc_6.gridx = 0;
    gbc_6.gridy = 10;
    JFontLabel label_9 = new JFontLabel(resourceBundle.getString("NewAnaGameDialog.ponder"));
    contentPanel.add(label_9, gbc_6); // ("AI是否后台思考"));
    chkPonder = new JFontCheckBox();
    chkPonder.setSelected(Lizzie.config.playponder);
    GridBagConstraints gbc_chkPonder = new GridBagConstraints();
    gbc_chkPonder.fill = GridBagConstraints.BOTH;
    gbc_chkPonder.insets = new Insets(0, 0, 5, 0);
    gbc_chkPonder.gridx = 1;
    gbc_chkPonder.gridy = 10;
    contentPanel.add(chkPonder, gbc_chkPonder);
    GridBagConstraints gbc_7 = new GridBagConstraints();
    gbc_7.fill = GridBagConstraints.BOTH;
    gbc_7.insets = new Insets(0, 0, 5, 5);
    gbc_7.gridx = 0;
    gbc_7.gridy = 11;
    JFontLabel label_8 = new JFontLabel(resourceBundle.getString("NewAnaGameDialog.random"));
    contentPanel.add(label_8, gbc_7); // ("AI随机开局"));

    JFontButton btnRandomStart =
        new JFontButton(resourceBundle.getString("NewAnaGameDialog.btnRandomStart")); // ("设置随机性");
    btnRandomStart.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            SetAnaGameRandomStart setAnaGameRandomStart = new SetAnaGameRandomStart();
            setAnaGameRandomStart.setVisible(true);
          }
        });
    GridBagConstraints gbc_btnRandomStart = new GridBagConstraints();
    gbc_btnRandomStart.fill = GridBagConstraints.BOTH;
    gbc_btnRandomStart.insets = new Insets(0, 0, 5, 0);
    gbc_btnRandomStart.gridx = 1;
    gbc_btnRandomStart.gridy = 11;
    contentPanel.add(btnRandomStart, gbc_btnRandomStart);
    GridBagConstraints gbc_8 = new GridBagConstraints();
    gbc_8.fill = GridBagConstraints.BOTH;
    gbc_8.insets = new Insets(0, 0, 5, 5);
    gbc_8.gridx = 0;
    gbc_8.gridy = 12;
    JFontLabel label_4 = new JFontLabel(resourceBundle.getString("NewAnaGameDialog.resign"));
    contentPanel.add(label_4, gbc_8); // ("AI认输条件"));

    panelResign = new JPanel();
    // panelResign.setLayout(null);
    //  panelResign.setPreferredSize(new Dimension(160, 25));
    JFontLabel resign0 =
        new JFontLabel(resourceBundle.getString("NewAnaGameDialog.resign0")); // ("最小手数");
    textResignStartMove = new JFontTextField();
    textResignStartMove.setColumns(2);
    JFontLabel resign1 =
        new JFontLabel(resourceBundle.getString("NewAnaGameDialog.resign1")); // ("连续");
    textResignMove = new JFontTextField();
    textResignMove.setColumns(2);
    JFontLabel resign2 =
        new JFontLabel(resourceBundle.getString("NewAnaGameDialog.resign2")); // ("手,胜率低于");
    textResignPercent = new JFontTextField();
    textResignPercent.setColumns(3);
    JFontLabel resign3 = new JFontLabel("%");

    textResignStartMove.setText(String.valueOf(Lizzie.config.anaGameResignStartMove));
    textResignMove.setText(String.valueOf(Lizzie.config.anaGameResignMove));
    textResignPercent.setText(String.valueOf(Lizzie.config.anaGameResignPercent));

    // resign1.setBounds(0, 0, 48, 20);
    //  textResignMove.setBounds(50, 0, 25, 20);
    //   resign2.setBounds(77, 0, 120, 20);
    //   textResignPercent.setBounds(192, 0, 33, 18);
    //   resign3.setBounds(225, 0, 25, 20);
    panelResign.add(resign0);
    panelResign.add(textResignStartMove);
    panelResign.add(resign1);
    panelResign.add(textResignMove);
    panelResign.add(resign2);
    panelResign.add(textResignPercent);
    panelResign.add(resign3);
    GridBagConstraints gbc_panelResign = new GridBagConstraints();
    gbc_panelResign.fill = GridBagConstraints.BOTH;
    gbc_panelResign.insets = new Insets(0, 0, 5, 0);
    gbc_panelResign.gridx = 1;
    gbc_panelResign.gridy = 12;
    contentPanel.add(panelResign, gbc_panelResign);

    JFontLabel lblDisableWRNInGame =
        new JFontLabel(
            resourceBundle.getString("NewAnaGameDialog.lblDisableWRNInGame")); // "对局时不使用分析广度拓展");
    GridBagConstraints gbc_lblDisableWRNInGame = new GridBagConstraints();
    gbc_lblDisableWRNInGame.anchor = GridBagConstraints.WEST;
    gbc_lblDisableWRNInGame.insets = new Insets(0, 0, 5, 5);
    gbc_lblDisableWRNInGame.gridx = 0;
    gbc_lblDisableWRNInGame.gridy = 13;
    contentPanel.add(lblDisableWRNInGame, gbc_lblDisableWRNInGame);

    JCheckBox chkDisableWRNInGame = new JCheckBox();
    GridBagConstraints gbc_chkDisableWRNInGame = new GridBagConstraints();
    gbc_chkDisableWRNInGame.anchor = GridBagConstraints.WEST;
    gbc_chkDisableWRNInGame.insets = new Insets(0, 0, 5, 0);
    gbc_chkDisableWRNInGame.gridx = 1;
    gbc_chkDisableWRNInGame.gridy = 13;
    contentPanel.add(chkDisableWRNInGame, gbc_chkDisableWRNInGame);
    chkDisableWRNInGame.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            Lizzie.config.disableWRNInGame = chkDisableWRNInGame.isSelected();
            Lizzie.config.uiConfig.put("disable-wrn-in-game", Lizzie.config.disableWRNInGame);
          }
        });
    chkDisableWRNInGame.setSelected(Lizzie.config.disableWRNInGame);

    GridBagConstraints gbc_9 = new GridBagConstraints();
    gbc_9.fill = GridBagConstraints.BOTH;
    gbc_9.insets = new Insets(0, 0, 5, 5);
    gbc_9.gridx = 0;
    gbc_9.gridy = 14;
    JFontLabel label_3 =
        new JFontLabel(resourceBundle.getString("NewAnaGameDialog.chkUsePlayMode"));
    contentPanel.add(label_3, gbc_9);
    chkUsePlayMode = new JFontCheckBox();
    chkUsePlayMode.setSelected(Lizzie.config.UsePlayMode);
    chkUsePlayMode.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            Lizzie.config.UsePlayMode = chkUsePlayMode.isSelected();
            Lizzie.config.uiConfig.put("use-play-mode", Lizzie.config.UsePlayMode);
          }
        });
    GridBagConstraints gbc_chkUsePlayMode = new GridBagConstraints();
    gbc_chkUsePlayMode.fill = GridBagConstraints.BOTH;
    gbc_chkUsePlayMode.insets = new Insets(0, 0, 5, 0);
    gbc_chkUsePlayMode.gridx = 1;
    gbc_chkUsePlayMode.gridy = 14;
    contentPanel.add(chkUsePlayMode, gbc_chkUsePlayMode);

    GridBagConstraints gbc_10 = new GridBagConstraints();
    gbc_10.fill = GridBagConstraints.BOTH;
    gbc_10.insets = new Insets(0, 0, 5, 5);
    gbc_10.gridx = 0;
    gbc_10.gridy = 15;
    JFontLabel label_10 = new JFontLabel(resourceBundle.getString("NewAnaGameDialog.showBlack"));
    contentPanel.add(label_10, gbc_10); // ("显示分析结果(黑)"));
    chkShowBlack = new JFontCheckBox();
    chkShowBlack.setSelected(false);
    GridBagConstraints gbc_chkShowBlack = new GridBagConstraints();
    gbc_chkShowBlack.fill = GridBagConstraints.BOTH;
    gbc_chkShowBlack.insets = new Insets(0, 0, 5, 0);
    gbc_chkShowBlack.gridx = 1;
    gbc_chkShowBlack.gridy = 15;
    contentPanel.add(chkShowBlack, gbc_chkShowBlack);
    GridBagConstraints gbc_11 = new GridBagConstraints();
    gbc_11.fill = GridBagConstraints.BOTH;
    gbc_11.insets = new Insets(0, 0, 5, 5);
    gbc_11.gridx = 0;
    gbc_11.gridy = 16;
    JFontLabel label_7 = new JFontLabel(resourceBundle.getString("NewAnaGameDialog.showWhite"));
    contentPanel.add(label_7, gbc_11); // ("显示分析结果(白)"));
    if (checkContinuePlay.isSelected()) textFieldHandicap.setEnabled(false);
    else textFieldHandicap.setEnabled(true);

    dialogPane.add(contentPanel, BorderLayout.CENTER);
    chkShowWhite = new JFontCheckBox();
    chkShowWhite.setSelected(false);
    GridBagConstraints gbc_chkShowWhite = new GridBagConstraints();
    gbc_chkShowWhite.insets = new Insets(0, 0, 5, 0);
    gbc_chkShowWhite.fill = GridBagConstraints.BOTH;
    gbc_chkShowWhite.gridx = 1;
    gbc_chkShowWhite.gridy = 16;
    contentPanel.add(chkShowWhite, gbc_chkShowWhite);

    JLabel lblAutoSave =
        new JFontLabel(resourceBundle.getString("NewAnaGameDialog.lblAutoSave")); // ("自动保存棋谱");
    GridBagConstraints gbc_lblNewLabel = new GridBagConstraints();
    gbc_lblNewLabel.anchor = GridBagConstraints.WEST;
    gbc_lblNewLabel.insets = new Insets(0, 0, 0, 5);
    gbc_lblNewLabel.gridx = 0;
    gbc_lblNewLabel.gridy = 17;
    contentPanel.add(lblAutoSave, gbc_lblNewLabel);

    JCheckBox chkAutoSave =
        new JFontCheckBox(
            resourceBundle.getString("NewAnaGameDialog.chkAutoSaveDirectory")
                + File.separator
                + resourceBundle.getString(
                    "NewAnaGameDialog.chkAutoSaveFolder")); // directory folder
    chkAutoSave.setSelected(Lizzie.config.autoSavePlayedGame);
    chkAutoSave.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            Lizzie.config.autoSavePlayedGame = chkAutoSave.isSelected();
            Lizzie.config.uiConfig.put("auto-save-played-game", Lizzie.config.autoSavePlayedGame);
          }
        });
    GridBagConstraints gbc_chkAutoSave = new GridBagConstraints();
    gbc_chkAutoSave.anchor = GridBagConstraints.WEST;
    gbc_chkAutoSave.gridx = 1;
    gbc_chkAutoSave.gridy = 17;
    contentPanel.add(chkAutoSave, gbc_chkAutoSave);
  }

  //  private void togglePlayerIsBlack() {
  //    JFontTextField humanTextField = playerIsBlack() ? textFieldBlack : textFieldWhite;
  //    JFontTextField computerTextField = playerIsBlack() ? textFieldWhite : textFieldBlack;
  //
  //    humanTextField.setEnabled(true);
  //    humanTextField.setText(GameInfo.DEFAULT_NAME_HUMAN_PLAYER);
  //    computerTextField.setEnabled(false);
  //    computerTextField.setText(Lizzie.leelaz.currentEnginename);
  //  }

  //  private void modifyHandicap() {
  //    try {
  //      int handicap = FORMAT_HANDICAP.parse(textFieldHandicap.getText()).intValue();
  //      if (handicap < 0) throw new IllegalArgumentException();
  //
  //      // textFieldKomi.setText(FORMAT_KOMI.format(GameInfo.DEFAULT_KOMI));
  //    } catch (ParseException | RuntimeException e) {
  //      // do not correct user mistakes
  //    }
  //  }

  private void initButtonBar() {
    buttonBar.setBorder(new EmptyBorder(12, 0, 0, 0));
    buttonBar.setLayout(new GridBagLayout());
    ((GridBagLayout) buttonBar.getLayout()).columnWidths = new int[] {0, 80};
    ((GridBagLayout) buttonBar.getLayout()).columnWeights = new double[] {1.0, 0.0};

    // ---- okButton ----
    okButton.setText(resourceBundle.getString("NewAnaGameDialog.okButton")); // "确定");
    okButton.addActionListener(e -> apply());

    int center = GridBagConstraints.CENTER;
    int both = GridBagConstraints.BOTH;
    buttonBar.add(
        okButton,
        new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, center, both, new Insets(0, 0, 0, 0), 0, 0));

    dialogPane.add(buttonBar, BorderLayout.SOUTH);
  }

  public void apply() {
    try {
      Lizzie.frame.isAnaPlayingAgainstLeelaz = true;
      if (engine.getSelectedIndex() == -1) {
        Utils.showMsg(resourceBundle.getString("NewAnaGameDialog.noEngineHint"));
        return;
      }
      if (!checkContinuePlay.isSelected()) Lizzie.board.clear(false);
      komi = 7.5;
      handicap = 0;
      try {
        komi = FORMAT_KOMI.parse(textFieldKomi.getText()).doubleValue();
        handicap = FORMAT_HANDICAP.parse(textFieldHandicap.getSelectedItem().toString()).intValue();
      } catch (Exception e) {
      }

      // apply new values
      // gameInfo.setKomi(komi);
      // gameInfo.changeKomi();
      if (!checkContinuePlay.isSelected()) gameInfo.setHandicap(handicap);
      Lizzie.config.playponder = chkPonder.isSelected();

      Lizzie.config.leelazConfig.put("play-ponder", Lizzie.config.playponder);

      LizzieFrame.toolbar.setChkShowBlack(chkShowBlack.isSelected());
      LizzieFrame.toolbar.setChkShowWhite(chkShowWhite.isSelected());
      LizzieFrame.menu.setChkShowBlack(chkShowBlack.isSelected());
      LizzieFrame.menu.setChkShowWhite(chkShowWhite.isSelected());
      if (playerIsBlack()) {
        Lizzie.frame.playerIsBlack = true;
        LizzieFrame.toolbar.chkAutoPlayBlack.setSelected(false);
        LizzieFrame.toolbar.chkAutoPlayWhite.setSelected(true);
      } else {
        Lizzie.frame.playerIsBlack = false;
        LizzieFrame.toolbar.chkAutoPlayBlack.setSelected(true);
        LizzieFrame.toolbar.chkAutoPlayWhite.setSelected(false);
      }
      LizzieFrame.toolbar.chkAutoPlay.setSelected(true);
      try {
        if (FORMAT_HANDICAP.parse(textTime.getText().trim()).intValue() > 0) {
          LizzieFrame.toolbar.chkAutoPlayTime.setSelected(chkUseTime.isSelected());
          LizzieFrame.toolbar.txtAutoPlayTime.setText(
              String.valueOf(FORMAT_HANDICAP.parse(textTime.getText().trim()).intValue()));
          Lizzie.config.maxGameThinkingTimeSeconds =
              FORMAT_HANDICAP.parse(textTime.getText().trim()).intValue();
          Lizzie.config.leelazConfig.putOpt(
              "max-game-thinking-time-seconds",
              FORMAT_HANDICAP.parse(textTime.getText().trim()).intValue());
        } else {
          LizzieFrame.toolbar.chkAutoPlayTime.setSelected(false);
          LizzieFrame.toolbar.txtAutoPlayTime.setText("");
        }
      } catch (Exception ex) {
        LizzieFrame.toolbar.chkAutoPlayTime.setSelected(false);
        LizzieFrame.toolbar.txtAutoPlayTime.setText("");
      }
      try {
        if (FORMAT_HANDICAP.parse(textPlayouts.getText().trim()).intValue() > 0) {
          LizzieFrame.toolbar.chkAutoPlayPlayouts.setSelected(chkUsePlayouts.isSelected());
          LizzieFrame.toolbar.txtAutoPlayPlayouts.setText(
              String.valueOf(FORMAT_HANDICAP.parse(textPlayouts.getText().trim()).intValue()));
        } else {
          LizzieFrame.toolbar.chkAutoPlayPlayouts.setSelected(false);
          LizzieFrame.toolbar.txtAutoPlayPlayouts.setText("");
        }
      } catch (Exception ex) {
        LizzieFrame.toolbar.chkAutoPlayPlayouts.setSelected(false);
        LizzieFrame.toolbar.txtAutoPlayPlayouts.setText("");
      }
      try {
        if (FORMAT_HANDICAP.parse(textFirstPlayouts.getText().trim()).intValue() > 0) {
          LizzieFrame.toolbar.chkAutoPlayFirstPlayouts.setSelected(
              chkUseFirstPlayouts.isSelected());
          LizzieFrame.toolbar.txtAutoPlayFirstPlayouts.setText(
              String.valueOf(FORMAT_HANDICAP.parse(textFirstPlayouts.getText().trim()).intValue()));
        } else {
          LizzieFrame.toolbar.chkAutoPlayFirstPlayouts.setSelected(false);
          LizzieFrame.toolbar.txtAutoPlayFirstPlayouts.setText("");
        }
      } catch (Exception ex) {
        LizzieFrame.toolbar.chkAutoPlayFirstPlayouts.setSelected(false);
        LizzieFrame.toolbar.txtAutoPlayFirstPlayouts.setText("");
      }
      if (!LizzieFrame.toolbar.chkAutoPlayFirstPlayouts.isSelected()
          && !LizzieFrame.toolbar.chkAutoPlayPlayouts.isSelected()
          && !LizzieFrame.toolbar.chkAutoPlayTime.isSelected()
          && !Lizzie.config.UsePureNetInGame) {
        Utils.showMsg(resourceBundle.getString("NewAnaGameDialog.wrongAiMoveSettings"));
        return;
      }
      if (EngineManager.currentEngineNo != engine.getSelectedIndex())
        Lizzie.engineManager.switchEngine(engine.getSelectedIndex(), true);
      Lizzie.config.anaGameResignStartMove =
          Utils.parseTextToInt(textResignStartMove, Lizzie.config.anaGameResignStartMove);
      Lizzie.config.anaGameResignMove =
          Utils.parseTextToInt(textResignMove, Lizzie.config.anaGameResignMove);
      Lizzie.config.anaGameResignPercent =
          Utils.parseTextToDouble(textResignPercent, Lizzie.config.anaGameResignPercent);

      Lizzie.config.limitMyTime = chkLimitMyTime.isSelected();
      Lizzie.config.mySaveTime = textSaveTime.getSelectedIndex();
      Lizzie.config.myByoyomiSeconds = texByoSeconds.getSelectedIndex();
      Lizzie.config.myByoyomiTimes = texByoTimes.getSelectedIndex();
      Lizzie.leelaz.anaGameResignCount = 0;
      LizzieFrame.menu.txtKomi.setText(String.valueOf(gameInfo.getKomi()));

      Lizzie.config.checkPlayBlack = checkBoxPlayerIsBlack.getSelectedIndex() == 0;
      Lizzie.config.checkContinuePlay = checkContinuePlay.isSelected();
      Lizzie.config.newGameKomi = komi;
      Lizzie.config.newGameHandicap = textFieldHandicap.getSelectedIndex();

      Lizzie.config.uiConfig.put("limit-my-time", Lizzie.config.limitMyTime);
      Lizzie.config.uiConfig.put("my-save-time", Lizzie.config.mySaveTime);
      Lizzie.config.uiConfig.put("my-byoyomo-seconds", Lizzie.config.myByoyomiSeconds);
      Lizzie.config.uiConfig.put("my-byoyomo-times", Lizzie.config.myByoyomiTimes);
      Lizzie.config.uiConfig.put("check-play-black", Lizzie.config.checkPlayBlack);
      Lizzie.config.uiConfig.put("check-continue-play", Lizzie.config.checkContinuePlay);
      Lizzie.config.uiConfig.put("genmove-game-notime", Lizzie.config.genmoveGameNoTime);
      Lizzie.config.uiConfig.put("new-game-komi", Lizzie.config.newGameKomi);
      Lizzie.config.uiConfig.put("new-game-handicap", Lizzie.config.newGameHandicap);
      Lizzie.config.uiConfig.put("anagame-resign-start-move", Lizzie.config.anaGameResignStartMove);
      Lizzie.config.uiConfig.put("anagame-resign-move", Lizzie.config.anaGameResignMove);
      Lizzie.config.uiConfig.put("anagame-resign-percent", Lizzie.config.anaGameResignPercent);

      // close window
      cancelled = false;
      setVisible(false);
      Lizzie.frame.allowPlaceStone = false;
      Runnable syncBoard =
          new Runnable() {
            public void run() {
              while (!Lizzie.leelaz.isLoaded() || EngineManager.isEmpty) {
                try {
                  Thread.sleep(100);
                } catch (InterruptedException e) {
                  // TODO Auto-generated catch block
                  e.printStackTrace();
                }
              }
              try {
                Thread.sleep(1000);
              } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
              }
              Lizzie.leelaz.setGameStatus(true);
              LizzieFrame.menu.toggleDoubleMenuGameStatus();
              Lizzie.leelaz.clearBestMoves();
              Lizzie.board.isGameBoard = true;
              gameInfo.setPlayerBlack(
                  playerIsBlack()
                      ? resourceBundle.getString("NewAnaGameDialog.me")
                      : Lizzie.engineManager.engineList.get(engine.getSelectedIndex())
                          .oriEnginename);
              gameInfo.setPlayerWhite(
                  playerIsBlack()
                      ? Lizzie.engineManager.engineList.get(engine.getSelectedIndex()).oriEnginename
                      : resourceBundle.getString("NewAnaGameDialog.me"));
              Lizzie.board.getHistory().setGameInfo(gameInfo);
              if (Lizzie.config.limitMyTime)
                Lizzie.frame.countDownForHuman(
                    Lizzie.config.getMySaveTime(),
                    Lizzie.config.getMyByoyomiSeconds(),
                    Lizzie.config.getMyByoyomiTimes());
              Lizzie.frame.clearWRNforGame(false);
              //                if (handicap >= 2
              //                    && Lizzie.board.boardWidth == 19
              //                    && Lizzie.board.boardHeight == 19) {
              // placeHandicap(handicap);
              //  }
              Lizzie.leelaz.komi(komi);
              boolean isHandicap = false;
              if (!checkContinuePlay.isSelected()) {
                if (!Lizzie.leelaz.isZen && handicap >= 2) {
                  if (Lizzie.leelaz.isKatago && Lizzie.config.useFreeHandicap)
                    Lizzie.leelaz.sendCommand("place_free_handicap " + gameInfo.getHandicap());
                  else Lizzie.leelaz.sendCommand("fixed_handicap " + gameInfo.getHandicap());
                  isHandicap = true;
                }
                if (Lizzie.leelaz.isZen && Board.boardWidth == 19 && Board.boardHeight == 19) {
                  placeHandicap(handicap);
                }
              }
              if (!isHandicap) {
                Lizzie.frame.allowPlaceStone = true;
                if (Lizzie.config.UsePureNetInGame && !Lizzie.leelaz.isheatmap)
                  Lizzie.leelaz.toggleHeatmap(false);
                Lizzie.leelaz.Pondering();
                if (Lizzie.config.playponder
                    || (Lizzie.board.getHistory().isBlacksTurn() && !Lizzie.frame.playerIsBlack)
                    || (!Lizzie.board.getHistory().isBlacksTurn() && Lizzie.frame.playerIsBlack)) {
                  Lizzie.leelaz.ponder();
                }
              }
            }
          };
      Thread syncBoardTh = new Thread(syncBoard);
      syncBoardTh.start();

    } catch (Exception e) {
      // hide input mistakes.
    }
  }

  private void placeHandicap(int handicap) {
    // TODO Auto-generated method stub
    switch (handicap) {
      case 2:
        Lizzie.board.place(3, 15, Stone.BLACK);
        Lizzie.board.place(15, 3, Stone.BLACK);
        break;
      case 3:
        Lizzie.board.place(3, 3, Stone.BLACK);
        Lizzie.board.place(15, 3, Stone.BLACK);
        Lizzie.board.place(3, 15, Stone.BLACK);
        break;
      case 4:
        Lizzie.board.place(3, 3, Stone.BLACK);
        Lizzie.board.place(3, 15, Stone.BLACK);
        Lizzie.board.place(15, 3, Stone.BLACK);
        Lizzie.board.place(15, 15, Stone.BLACK);
        break;
      case 5:
        Lizzie.board.place(3, 3, Stone.BLACK);
        Lizzie.board.place(3, 15, Stone.BLACK);
        Lizzie.board.place(15, 3, Stone.BLACK);
        Lizzie.board.place(15, 15, Stone.BLACK);
        Lizzie.board.place(9, 9, Stone.BLACK);
        break;
      case 6:
        Lizzie.board.place(3, 3, Stone.BLACK);
        Lizzie.board.place(3, 15, Stone.BLACK);
        Lizzie.board.place(15, 3, Stone.BLACK);
        Lizzie.board.place(15, 15, Stone.BLACK);
        Lizzie.board.place(3, 9, Stone.BLACK);
        Lizzie.board.place(15, 9, Stone.BLACK);
        break;
      case 7:
        Lizzie.board.place(3, 3, Stone.BLACK);
        Lizzie.board.place(3, 15, Stone.BLACK);
        Lizzie.board.place(15, 3, Stone.BLACK);
        Lizzie.board.place(15, 15, Stone.BLACK);
        Lizzie.board.place(15, 9, Stone.BLACK);
        Lizzie.board.place(3, 9, Stone.BLACK);
        Lizzie.board.place(9, 9, Stone.BLACK);
        break;
      case 8:
        Lizzie.board.place(3, 3, Stone.BLACK);
        Lizzie.board.place(3, 15, Stone.BLACK);
        Lizzie.board.place(15, 3, Stone.BLACK);
        Lizzie.board.place(15, 15, Stone.BLACK);
        Lizzie.board.place(9, 3, Stone.BLACK);
        Lizzie.board.place(9, 15, Stone.BLACK);
        Lizzie.board.place(3, 9, Stone.BLACK);
        Lizzie.board.place(15, 9, Stone.BLACK);
        break;
      case 9:
        Lizzie.board.place(3, 3, Stone.BLACK);
        Lizzie.board.place(3, 15, Stone.BLACK);
        Lizzie.board.place(15, 3, Stone.BLACK);
        Lizzie.board.place(15, 15, Stone.BLACK);
        Lizzie.board.place(9, 3, Stone.BLACK);
        Lizzie.board.place(9, 15, Stone.BLACK);
        Lizzie.board.place(3, 9, Stone.BLACK);
        Lizzie.board.place(15, 9, Stone.BLACK);
        Lizzie.board.place(9, 9, Stone.BLACK);
        break;
    }
    Lizzie.board.hasStartStone = true;
    Lizzie.board.addStartListAll();
    Lizzie.board.flatten();
  }

  //  public void setGameInfo(GameInfo gameInfo) {
  //    this.gameInfo = gameInfo;
  //
  //    // textFieldBlack.setText(gameInfo.getPlayerBlack());
  //    // textFieldWhite.setText(gameInfo.getPlayerWhite());
  //   // textFieldHandicap.(FORMAT_HANDICAP.format(gameInfo.getHandicap()));
  //    textFieldKomi.setText(FORMAT_KOMI.format(gameInfo.getKomi()));
  //
  //    // update player names
  //
  //  }

  public boolean playerIsBlack() {
    return checkBoxPlayerIsBlack.getSelectedIndex() == 0;
  }

  public boolean isCancelled() {
    return cancelled;
  }
}
