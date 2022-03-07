/*
 * Created by JFormDesigner on Wed Apr 04 22:17:33 CEST 2018
 */

package featurecat.lizzie.gui;

import featurecat.lizzie.Config;
import featurecat.lizzie.Lizzie;
import featurecat.lizzie.analysis.EngineManager;
import featurecat.lizzie.analysis.GameInfo;
import featurecat.lizzie.util.Utils;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

/** @author unknown */
public class NewGameDialog extends JDialog {
  // create formatters
  public static final DecimalFormat FORMAT_HANDICAP = new DecimalFormat("0");

  static {
    FORMAT_HANDICAP.setMaximumIntegerDigits(1);
  }

  private JPanel dialogPane = new JPanel();
  private JPanel contentPanel = new JPanel();
  private JPanel buttonBar = new JPanel();
  private JFontButton okButton = new JFontButton();
  private JFontComboBox<String> engine;

  private JFontComboBox<String> checkBoxPlayerIsBlack;
  private JFontCheckBox checkContinuePlay;
  private JFontTextField textFieldKomi;
  private JFontComboBox<Integer> textFieldHandicap;
  private JTextField textTime;
  private JFontCheckBox chkPonder;
  private JFontCheckBox chkUsePlayMode;
  private JFontCheckBox chkNoTime;
  JFontCheckBox chkLimitMyTime;
  JFontComboBox<String> textSaveTime;
  JFontComboBox<String> texByoSeconds;
  JFontComboBox<String> texByoTimes;

  JFontCheckBox chkNormalTime;
  JFontCheckBox chkKataTime;
  JFontCheckBox chkUseAdvTime;

  JFontComboBox<String> kataTimeComboBox;

  private boolean cancelled = true;
  public GameInfo gameInfo = new GameInfo();

  public NewGameDialog(Window owner) {
    super(owner);
    initComponents();
  }

  private ResourceBundle resourceBundle = Lizzie.resourceBundle;
  private JFontTextField txtAdvanceTime;
  private JFontButton btnAboutAdvTime;
  private JFontCheckBox chkShowBlack;
  private JFontCheckBox chkShowWhite;
  private JTextField txtKataTimeSaveMins;
  private JTextField txtKataTimeByoyomiSecs;
  private JTextField txtKataTimeByoyomiTimes;
  private JTextField txtKataTimeFisherIncrementSecs;
  private Window thisDialog = this;

  private void initComponents() {
    setMinimumSize(new Dimension(100, 150));
    setResizable(false);
    setTitle(resourceBundle.getString("NewGameDialog.title")); // "新对局(Genmove模式)");
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
    ArrayList<EngineData> engineData = Utils.getEngineData();
    engine = new JFontComboBox<String>();
    for (int i = 0; i < engineData.size(); i++) {
      EngineData engineDt = engineData.get(i);
      engine.addItem("[" + (i + 1) + "]" + engineDt.name);
    }
    if (engine.getItemCount() > 0)
      engine.setSelectedIndex(
          EngineManager.currentEngineNo >= 0 ? EngineManager.currentEngineNo : 0);
    GridBagLayout gbl_contentPanel = new GridBagLayout();
    gbl_contentPanel.columnWidths = new int[] {176, 92, 0};
    gbl_contentPanel.rowHeights = new int[] {31, 31, 31, 31, 31, 31, 31, 0, 31, 31, 31, 31, 31};
    gbl_contentPanel.columnWeights = new double[] {1.0, 1.0, Double.MIN_VALUE};
    gbl_contentPanel.rowWeights =
        new double[] {0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0};
    contentPanel.setLayout(gbl_contentPanel);
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.fill = GridBagConstraints.BOTH;
    gbc.insets = new Insets(0, 0, 5, 5);
    gbc.gridx = 0;
    gbc.gridy = 0;
    JFontLabel label_6 = new JFontLabel(resourceBundle.getString("NewAnaGameDialog.chooseEngine"));
    contentPanel.add(label_6, gbc);

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
    JFontLabel label_3 = new JFontLabel(resourceBundle.getString("NewGameDialog.Handicap"));
    contentPanel.add(label_3, gbc_1);
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
    chkUseFreeHandicap.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            Lizzie.config.useFreeHandicap = chkUseFreeHandicap.isSelected();
            Lizzie.config.uiConfig.put("use-free-handicap", Lizzie.config.useFreeHandicap);
          }
        });
    chkUseFreeHandicap.setSelected(Lizzie.config.useFreeHandicap);
    handicapPanel.add(textFieldHandicap);
    handicapPanel.add(chkUseFreeHandicap);
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
    textFieldKomi = new JFontTextField();
    textFieldKomi.setDocument(new KomiDocument(true));
    GridBagConstraints gbc_textFieldKomi = new GridBagConstraints();
    gbc_textFieldKomi.fill = GridBagConstraints.BOTH;
    gbc_textFieldKomi.insets = new Insets(0, 0, 5, 0);
    gbc_textFieldKomi.gridx = 1;
    gbc_textFieldKomi.gridy = 4;
    contentPanel.add(textFieldKomi, gbc_textFieldKomi);
    textFieldKomi.setEnabled(true);
    textFieldKomi.setText(String.valueOf(Lizzie.config.newGameKomi));

    GridBagConstraints gbc_2 = new GridBagConstraints();
    gbc_2.anchor = GridBagConstraints.WEST;
    gbc_2.fill = GridBagConstraints.VERTICAL;
    gbc_2.insets = new Insets(0, 0, 5, 5);
    gbc_2.gridx = 0;
    gbc_2.gridy = 5;

    JPanel limitMyPanel = new JPanel();
    chkLimitMyTime = new JFontCheckBox(resourceBundle.getString("Byoyomi.newGame.limitMyTime"));
    limitMyPanel.add(chkLimitMyTime);
    contentPanel.add(limitMyPanel, gbc_2);
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

    JPanel myTimeSettingsPanel = new JPanel();
    myTimeSettingsPanel.add(new JFontLabel(resourceBundle.getString("Byoyomi.newGame.saveTime")));
    myTimeSettingsPanel.add(textSaveTime);
    myTimeSettingsPanel.add(new JFontLabel(resourceBundle.getString("Byoyomi.newGame.byoyomi")));
    myTimeSettingsPanel.add(texByoSeconds);
    myTimeSettingsPanel.add(
        new JFontLabel(resourceBundle.getString("Byoyomi.newGame.byoyomiTimes")));
    myTimeSettingsPanel.add(texByoTimes);
    GridBagConstraints gbc_myTimePanle = new GridBagConstraints();
    gbc_myTimePanle.anchor = GridBagConstraints.WEST;
    gbc_myTimePanle.fill = GridBagConstraints.VERTICAL;
    gbc_myTimePanle.insets = new Insets(0, 0, 5, 0);
    gbc_myTimePanle.gridx = 1;
    gbc_myTimePanle.gridy = 5;
    contentPanel.add(myTimeSettingsPanel, gbc_myTimePanle);

    GridBagConstraints gbc_3 = new GridBagConstraints();
    gbc_3.anchor = GridBagConstraints.WEST;
    gbc_3.fill = GridBagConstraints.VERTICAL;
    gbc_3.insets = new Insets(0, 0, 5, 5);
    gbc_3.gridx = 0;
    gbc_3.gridy = 6;

    JPanel NormalTimePanel = new JPanel();
    chkNormalTime = new JFontCheckBox(resourceBundle.getString("NewGameDialog.time"));
    NormalTimePanel.add(chkNormalTime);
    contentPanel.add(NormalTimePanel, gbc_3); // "AI每手用时(秒)"));
    //  textFieldHandicap.addPropertyChangeListener(evt -> modifyHandicap());
    textTime = new JTextField();
    textTime.setDocument(new IntDocument());
    textTime.setFont(new Font(Config.sysDefaultFontName, Font.PLAIN, Config.frameFontSize));
    textTime.setText(String.valueOf(Lizzie.config.maxGameThinkingTimeSeconds));
    GridBagConstraints gbc_textTime = new GridBagConstraints();
    gbc_textTime.fill = GridBagConstraints.BOTH;
    gbc_textTime.insets = new Insets(0, 0, 5, 0);
    gbc_textTime.gridx = 1;
    gbc_textTime.gridy = 6;
    contentPanel.add(textTime, gbc_textTime);

    JPanel kataTimeLabelPanel = new JPanel();
    GridBagConstraints gbc_kataTimeLabelPanel = new GridBagConstraints();
    gbc_kataTimeLabelPanel.anchor = GridBagConstraints.WEST;
    gbc_kataTimeLabelPanel.fill = GridBagConstraints.VERTICAL;
    gbc_kataTimeLabelPanel.insets = new Insets(0, 0, 5, 5);
    gbc_kataTimeLabelPanel.gridx = 0;
    gbc_kataTimeLabelPanel.gridy = 7;
    contentPanel.add(kataTimeLabelPanel, gbc_kataTimeLabelPanel);

    chkKataTime =
        new JFontCheckBox(resourceBundle.getString("NewGameDialog.kataTime")); // "KataGo专用时间设置");
    kataTimeLabelPanel.add(chkKataTime);

    JPanel kataTimeSettingsPanel = new JPanel();
    GridBagConstraints gbc_panel = new GridBagConstraints();
    gbc_panel.anchor = GridBagConstraints.WEST;
    gbc_panel.insets = new Insets(0, 0, 5, 0);
    gbc_panel.fill = GridBagConstraints.VERTICAL;
    gbc_panel.gridx = 1;
    gbc_panel.gridy = 7;
    contentPanel.add(kataTimeSettingsPanel, gbc_panel);

    kataTimeComboBox = new JFontComboBox<String>();
    kataTimeComboBox.addItem(resourceBundle.getString("NewGameDialog.kataTime.byoyomi")); // "读秒制");
    kataTimeComboBox.addItem(resourceBundle.getString("NewGameDialog.kataTime.fisher")); // "加秒制");
    kataTimeComboBox.addItem(
        resourceBundle.getString("NewGameDialog.kataTime.absolute")); // "包干制");
    kataTimeSettingsPanel.add(kataTimeComboBox);

    JLabel lblKataTimeSaveMins =
        new JFontLabel(resourceBundle.getString("Byoyomi.newGame.saveTime"));
    kataTimeSettingsPanel.add(lblKataTimeSaveMins);
    txtKataTimeSaveMins = new JFontTextField();
    kataTimeSettingsPanel.add(txtKataTimeSaveMins);
    txtKataTimeSaveMins.setColumns(3);
    txtKataTimeSaveMins.setDocument(new IntDocument());
    txtKataTimeSaveMins.setText(String.valueOf(Lizzie.config.kataTimeMainTimeMins));

    JLabel lblKataTimeByoyomiSecs =
        new JFontLabel(resourceBundle.getString("Byoyomi.newGame.byoyomi"));
    kataTimeSettingsPanel.add(lblKataTimeByoyomiSecs);
    txtKataTimeByoyomiSecs = new JFontTextField();
    kataTimeSettingsPanel.add(txtKataTimeByoyomiSecs);
    txtKataTimeByoyomiSecs.setColumns(3);
    txtKataTimeByoyomiSecs.setDocument(new IntDocument());
    txtKataTimeByoyomiSecs.setText(String.valueOf(Lizzie.config.kataTimeByoyomiSecs));

    JLabel lblKataTimeByoyomiTimes =
        new JFontLabel(resourceBundle.getString("Byoyomi.newGame.byoyomiTimes"));
    kataTimeSettingsPanel.add(lblKataTimeByoyomiTimes);
    txtKataTimeByoyomiTimes = new JFontTextField();
    kataTimeSettingsPanel.add(txtKataTimeByoyomiTimes);
    txtKataTimeByoyomiTimes.setColumns(3);
    txtKataTimeByoyomiTimes.setDocument(new IntDocument());
    txtKataTimeByoyomiTimes.setText(String.valueOf(Lizzie.config.kataTimeByoyomiTimes));

    JLabel lblKataTimeFisherIncrementSecs =
        new JFontLabel(
            resourceBundle.getString("NewGameDialog.kataTime.increment")); // ("每手增加(秒)");
    kataTimeSettingsPanel.add(lblKataTimeFisherIncrementSecs);
    txtKataTimeFisherIncrementSecs = new JFontTextField();
    kataTimeSettingsPanel.add(txtKataTimeFisherIncrementSecs);
    txtKataTimeFisherIncrementSecs.setColumns(5);
    txtKataTimeFisherIncrementSecs.setDocument(new IntDocument());
    txtKataTimeFisherIncrementSecs.setText(
        String.valueOf(Lizzie.config.kataTimeFisherIncrementSecs));

    kataTimeComboBox.setSelectedIndex(Lizzie.config.kataTimeType);
    int index = kataTimeComboBox.getSelectedIndex();
    if (index == 0) {
      txtKataTimeSaveMins.setColumns(3);
      lblKataTimeFisherIncrementSecs.setVisible(false);
      txtKataTimeFisherIncrementSecs.setVisible(false);
      lblKataTimeByoyomiSecs.setVisible(true);
      txtKataTimeByoyomiSecs.setVisible(true);
      lblKataTimeByoyomiTimes.setVisible(true);
      txtKataTimeByoyomiTimes.setVisible(true);
    } else if (index == 1) {
      txtKataTimeSaveMins.setColumns(5);
      lblKataTimeFisherIncrementSecs.setVisible(true);
      txtKataTimeFisherIncrementSecs.setVisible(true);
      lblKataTimeByoyomiSecs.setVisible(false);
      txtKataTimeByoyomiSecs.setVisible(false);
      lblKataTimeByoyomiTimes.setVisible(false);
      txtKataTimeByoyomiTimes.setVisible(false);
    } else {
      txtKataTimeSaveMins.setColumns(5);
      lblKataTimeFisherIncrementSecs.setVisible(false);
      txtKataTimeFisherIncrementSecs.setVisible(false);
      lblKataTimeByoyomiSecs.setVisible(false);
      txtKataTimeByoyomiSecs.setVisible(false);
      lblKataTimeByoyomiTimes.setVisible(false);
      txtKataTimeByoyomiTimes.setVisible(false);
    }
    kataTimeComboBox.addItemListener(
        new ItemListener() {
          public void itemStateChanged(final ItemEvent e) {
            int index = kataTimeComboBox.getSelectedIndex();
            Lizzie.config.kataTimeType = index;
            Lizzie.config.uiConfig.put("kata-time-type", index);
            if (index == 0) {
              txtKataTimeSaveMins.setColumns(3);
              lblKataTimeFisherIncrementSecs.setVisible(false);
              txtKataTimeFisherIncrementSecs.setVisible(false);
              lblKataTimeByoyomiSecs.setVisible(true);
              txtKataTimeByoyomiSecs.setVisible(true);
              lblKataTimeByoyomiTimes.setVisible(true);
              txtKataTimeByoyomiTimes.setVisible(true);
            } else if (index == 1) {
              txtKataTimeSaveMins.setColumns(5);
              lblKataTimeFisherIncrementSecs.setVisible(true);
              txtKataTimeFisherIncrementSecs.setVisible(true);
              lblKataTimeByoyomiSecs.setVisible(false);
              txtKataTimeByoyomiSecs.setVisible(false);
              lblKataTimeByoyomiTimes.setVisible(false);
              txtKataTimeByoyomiTimes.setVisible(false);
            } else {
              txtKataTimeSaveMins.setColumns(5);
              lblKataTimeFisherIncrementSecs.setVisible(false);
              txtKataTimeFisherIncrementSecs.setVisible(false);
              lblKataTimeByoyomiSecs.setVisible(false);
              txtKataTimeByoyomiSecs.setVisible(false);
              lblKataTimeByoyomiTimes.setVisible(false);
              txtKataTimeByoyomiTimes.setVisible(false);
            }
            pack();
          }
        });

    JPanel advTimeLabelPanel = new JPanel();
    advTimeLabelPanel.setBorder(BorderFactory.createEmptyBorder());
    GridBagConstraints gbc_advTimeLabelPanel = new GridBagConstraints();
    gbc_advTimeLabelPanel.anchor = GridBagConstraints.WEST;
    gbc_advTimeLabelPanel.fill = GridBagConstraints.VERTICAL;
    gbc_advTimeLabelPanel.insets = new Insets(0, 0, 5, 5);
    gbc_advTimeLabelPanel.gridx = 0;
    gbc_advTimeLabelPanel.gridy = 8;
    contentPanel.add(advTimeLabelPanel, gbc_advTimeLabelPanel);

    chkUseAdvTime = new JFontCheckBox(resourceBundle.getString("NewGameDialog.lblAdvanceTime"));
    chkUseAdvTime.setBounds(99, 4, 18, 18);
    chkUseAdvTime.setSelected(Lizzie.config.advanceTimeSettings);
    advTimeLabelPanel.add(chkUseAdvTime);

    ImageIcon iconSettings = new ImageIcon();
    try {
      iconSettings.setImage(ImageIO.read(getClass().getResourceAsStream("/assets/settings.png")));
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

    btnAboutAdvTime = new JFontButton(iconSettings); // $NON-NLS-1$
    btnAboutAdvTime.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            Utils.showHtmlMessageModal(
                resourceBundle.getString("AdvanceTimeSettings.title"),
                resourceBundle.getString("AdvanceTimeSettings.describe"),
                thisDialog);
          }
        });
    btnAboutAdvTime.setPreferredSize(new Dimension(Config.menuHeight, Config.menuHeight));
    btnAboutAdvTime.setFocusable(false);
    advTimeLabelPanel.add(btnAboutAdvTime);

    chkNormalTime.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            chkTimeChanged();
          }
        });

    chkKataTime.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            chkTimeChanged();
          }
        });

    chkUseAdvTime.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            chkTimeChanged();
          }
        });
    chkUseAdvTime.setSelected(Lizzie.config.advanceTimeSettings);
    chkKataTime.setSelected(Lizzie.config.kataTimeSettings);
    chkNormalTime.setSelected(
        !Lizzie.config.advanceTimeSettings && !Lizzie.config.kataTimeSettings);

    ButtonGroup chkTimeGroup = new ButtonGroup();
    chkTimeGroup.add(chkNormalTime);
    chkTimeGroup.add(chkKataTime);
    chkTimeGroup.add(chkUseAdvTime);

    txtAdvanceTime = new JFontTextField();
    GridBagConstraints gbc_txtAdvanceTime = new GridBagConstraints();
    gbc_txtAdvanceTime.fill = GridBagConstraints.BOTH;
    gbc_txtAdvanceTime.insets = new Insets(0, 0, 5, 0);
    gbc_txtAdvanceTime.gridx = 1;
    gbc_txtAdvanceTime.gridy = 8;
    contentPanel.add(txtAdvanceTime, gbc_txtAdvanceTime);
    txtAdvanceTime.setColumns(13);
    txtAdvanceTime.setText(Lizzie.config.advanceTimeTxt);

    GridBagConstraints gbc_4 = new GridBagConstraints();
    gbc_4.fill = GridBagConstraints.BOTH;
    gbc_4.insets = new Insets(0, 0, 5, 5);
    gbc_4.gridx = 0;
    gbc_4.gridy = 9;
    JFontLabel label_4 = new JFontLabel(resourceBundle.getString("NewGameDialog.noTime"));
    contentPanel.add(label_4, gbc_4);
    chkNoTime = new JFontCheckBox();
    chkNoTime.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            // TODO Auto-generated method stub
            if (chkNoTime.isSelected()) {
              textTime.setEnabled(false);
              chkUseAdvTime.setEnabled(false);
              btnAboutAdvTime.setEnabled(false);
              txtAdvanceTime.setEnabled(false);
              Lizzie.config.genmoveGameNoTime = true;
            } else {
              textTime.setEnabled(true);
              chkUseAdvTime.setEnabled(true);
              btnAboutAdvTime.setEnabled(true);
              txtAdvanceTime.setEnabled(true);
              Lizzie.config.genmoveGameNoTime = false;
            }
          }
        });
    GridBagConstraints gbc_chkNoTime = new GridBagConstraints();
    gbc_chkNoTime.fill = GridBagConstraints.BOTH;
    gbc_chkNoTime.insets = new Insets(0, 0, 5, 0);
    gbc_chkNoTime.gridx = 1;
    gbc_chkNoTime.gridy = 9;
    contentPanel.add(chkNoTime, gbc_chkNoTime);

    GridBagConstraints gbc_5 = new GridBagConstraints();
    gbc_5.fill = GridBagConstraints.BOTH;
    gbc_5.insets = new Insets(0, 0, 5, 5);
    gbc_5.gridx = 0;
    gbc_5.gridy = 10;
    JFontLabel label_5 = new JFontLabel(resourceBundle.getString("NewGameDialog.chkPonder"));
    contentPanel.add(label_5, gbc_5); // ("AI是否后台思考"));
    chkPonder = new JFontCheckBox(resourceBundle.getString("NewGameDialog.chkPonderDescribe"));
    chkPonder.setSelected(Lizzie.config.playponder);
    GridBagConstraints gbc_chkPonder = new GridBagConstraints();
    gbc_chkPonder.fill = GridBagConstraints.BOTH;
    gbc_chkPonder.insets = new Insets(0, 0, 5, 0);
    gbc_chkPonder.gridx = 1;
    gbc_chkPonder.gridy = 10;
    contentPanel.add(chkPonder, gbc_chkPonder);

    GridBagConstraints gbc_6 = new GridBagConstraints();
    gbc_6.fill = GridBagConstraints.BOTH;
    gbc_6.insets = new Insets(0, 0, 5, 5);
    gbc_6.gridx = 0;
    gbc_6.gridy = 11;
    JFontLabel label_7 = new JFontLabel(resourceBundle.getString("NewGameDialog.chkUsePlayMode"));
    contentPanel.add(label_7, gbc_6);
    chkUsePlayMode =
        new JFontCheckBox(resourceBundle.getString("NewGameDialog.chkUsePlayModeDescribe"));
    chkUsePlayMode.setSelected(Lizzie.config.UsePlayMode);
    chkUsePlayMode.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            Lizzie.config.UsePlayMode = chkUsePlayMode.isSelected();
            Lizzie.config.uiConfig.put("use-play-mode", Lizzie.config.UsePlayMode);
          }
        });
    GridBagConstraints gbc_chkUsePlayMode = new GridBagConstraints();
    gbc_chkUsePlayMode.insets = new Insets(0, 0, 5, 0);
    gbc_chkUsePlayMode.fill = GridBagConstraints.BOTH;
    gbc_chkUsePlayMode.gridx = 1;
    gbc_chkUsePlayMode.gridy = 11;
    contentPanel.add(chkUsePlayMode, gbc_chkUsePlayMode);

    if (Lizzie.config.genmoveGameNoTime) {
      chkNoTime.setSelected(true);
      textTime.setEnabled(false);
      chkUseAdvTime.setEnabled(false);
      btnAboutAdvTime.setEnabled(false);
      txtAdvanceTime.setEnabled(false);
    } else {
      chkNoTime.setSelected(false);
      textTime.setEnabled(true);
      chkUseAdvTime.setEnabled(true);
      btnAboutAdvTime.setEnabled(true);
      txtAdvanceTime.setEnabled(true);
    }

    if (Lizzie.config.advanceTimeSettings) textTime.setEnabled(false);
    else txtAdvanceTime.setEnabled(false);
    if (checkContinuePlay.isSelected()) textFieldHandicap.setEnabled(false);
    else textFieldHandicap.setEnabled(true);

    dialogPane.add(contentPanel, BorderLayout.CENTER);

    JFontLabel lblShowBlack =
        new JFontLabel(resourceBundle.getString("NewAnaGameDialog.showBlack"));
    GridBagConstraints gbc_lblNewLabel = new GridBagConstraints();
    gbc_lblNewLabel.anchor = GridBagConstraints.WEST;
    gbc_lblNewLabel.insets = new Insets(0, 0, 5, 5);
    gbc_lblNewLabel.gridx = 0;
    gbc_lblNewLabel.gridy = 12;
    contentPanel.add(lblShowBlack, gbc_lblNewLabel);

    chkShowBlack = new JFontCheckBox();
    GridBagConstraints gbc_chckbxNewCheckBox = new GridBagConstraints();
    gbc_chckbxNewCheckBox.fill = GridBagConstraints.HORIZONTAL;
    gbc_chckbxNewCheckBox.insets = new Insets(0, 0, 5, 0);
    gbc_chckbxNewCheckBox.gridx = 1;
    gbc_chckbxNewCheckBox.gridy = 12;
    contentPanel.add(chkShowBlack, gbc_chckbxNewCheckBox);

    JFontLabel lblShowWhite =
        new JFontLabel(resourceBundle.getString("NewAnaGameDialog.showWhite"));
    GridBagConstraints gbc_lblNewLabel_1 = new GridBagConstraints();
    gbc_lblNewLabel_1.anchor = GridBagConstraints.WEST;
    gbc_lblNewLabel_1.insets = new Insets(0, 0, 5, 5);
    gbc_lblNewLabel_1.gridx = 0;
    gbc_lblNewLabel_1.gridy = 13;
    contentPanel.add(lblShowWhite, gbc_lblNewLabel_1);

    chkShowWhite = new JFontCheckBox();
    GridBagConstraints gbc_chckbxNewCheckBox_1 = new GridBagConstraints();
    gbc_chckbxNewCheckBox_1.fill = GridBagConstraints.HORIZONTAL;
    gbc_chckbxNewCheckBox_1.insets = new Insets(0, 0, 5, 0);
    gbc_chckbxNewCheckBox_1.gridx = 1;
    gbc_chckbxNewCheckBox_1.gridy = 13;
    contentPanel.add(chkShowWhite, gbc_chckbxNewCheckBox_1);
    chkShowBlack.setSelected(false);
    chkShowWhite.setSelected(false);

    JLabel lblAutoSave =
        new JFontLabel(resourceBundle.getString("NewAnaGameDialog.lblAutoSave")); // ("自动保存棋谱");
    GridBagConstraints gbc_lblAutoSave = new GridBagConstraints();
    gbc_lblAutoSave.anchor = GridBagConstraints.WEST;
    gbc_lblAutoSave.insets = new Insets(0, 0, 0, 5);
    gbc_lblAutoSave.gridx = 0;
    gbc_lblAutoSave.gridy = 14;
    contentPanel.add(lblAutoSave, gbc_lblAutoSave);

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
    gbc_chkAutoSave.fill = GridBagConstraints.HORIZONTAL;
    gbc_chkAutoSave.gridx = 1;
    gbc_chkAutoSave.gridy = 14;
    contentPanel.add(chkAutoSave, gbc_chkAutoSave);
    chkTimeChanged();
  }

  private void chkTimeChanged() {
    Lizzie.config.kataTimeSettings = chkKataTime.isSelected();
    Lizzie.config.advanceTimeSettings = chkUseAdvTime.isSelected();
    Lizzie.config.uiConfig.put("advance-time-settings", Lizzie.config.advanceTimeSettings);
    Lizzie.config.uiConfig.put("kata-time-settings", Lizzie.config.kataTimeSettings);

    kataTimeComboBox.setEnabled(Lizzie.config.kataTimeSettings);
    txtKataTimeSaveMins.setEnabled(Lizzie.config.kataTimeSettings);
    txtKataTimeByoyomiSecs.setEnabled(Lizzie.config.kataTimeSettings);
    txtKataTimeByoyomiTimes.setEnabled(Lizzie.config.kataTimeSettings);
    txtKataTimeFisherIncrementSecs.setEnabled(Lizzie.config.kataTimeSettings);
    txtAdvanceTime.setEnabled(Lizzie.config.advanceTimeSettings);
    if (Lizzie.config.kataTimeSettings || Lizzie.config.advanceTimeSettings) {
      textTime.setEnabled(false);
    } else {
      textTime.setEnabled(true);
    }
  }

  private void initButtonBar() {
    buttonBar.setBorder(new EmptyBorder(12, 0, 0, 0));
    GridBagLayout gbl_buttonBar = new GridBagLayout();
    gbl_buttonBar.rowWeights = new double[] {1.0, 1.0, 0.0};
    buttonBar.setLayout(gbl_buttonBar);
    ((GridBagLayout) buttonBar.getLayout()).columnWidths = new int[] {0, 80};
    ((GridBagLayout) buttonBar.getLayout()).columnWeights = new double[] {1.0, 0.0};

    // ---- okButton ----
    okButton.setText(resourceBundle.getString("NewGameDialog.okButton"));
    okButton.addActionListener(e -> apply());

    int center = GridBagConstraints.CENTER;
    int both = GridBagConstraints.BOTH;
    buttonBar.add(
        okButton,
        new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0, center, both, new Insets(0, 0, 0, 0), 0, 0));

    dialogPane.add(buttonBar, BorderLayout.SOUTH);
  }

  public void apply() {
    try {
      if (engine.getSelectedIndex() == -1) {
        Utils.showMsg(resourceBundle.getString("NewAnaGameDialog.noEngineHint"));
        return;
      }
      Lizzie.frame.isPlayingAgainstLeelaz = false;
      if (EngineManager.currentEngineNo != engine.getSelectedIndex())
        Lizzie.engineManager.switchEngine(engine.getSelectedIndex(), true);
      double komi = 7.5;
      int handicap = 0;
      try {
        komi = Double.parseDouble(textFieldKomi.getText());
        handicap = FORMAT_HANDICAP.parse(textFieldHandicap.getSelectedItem().toString()).intValue();
      } catch (Exception e) {
      }
      // apply new values
      gameInfo.setPlayerBlack(
          checkBoxPlayerIsBlack.getSelectedIndex() == 0
              ? resourceBundle.getString("NewAnaGameDialog.me")
              : Lizzie.engineManager.engineList.get(engine.getSelectedIndex()).oriEnginename);
      gameInfo.setPlayerWhite(
          checkBoxPlayerIsBlack.getSelectedIndex() == 0
              ? Lizzie.engineManager.engineList.get(engine.getSelectedIndex()).oriEnginename
              : resourceBundle.getString("NewAnaGameDialog.me"));
      gameInfo.setKomi(komi);
      //     gameInfo.changeKomi();
      if (!checkContinuePlay.isSelected()) {
        gameInfo.setHandicap(handicap);
        Lizzie.board.tempmovelistForGenMoveGame = null;
      } else Lizzie.board.tempmovelistForGenMoveGame = Lizzie.board.getMoveList();
      Lizzie.config.playponder = chkPonder.isSelected();
      // if (!chkNoTime.isSelected()) {
      Lizzie.config.maxGameThinkingTimeSeconds =
          FORMAT_HANDICAP.parse(textTime.getText().trim()).intValue();
      Lizzie.config.leelazConfig.putOpt(
          "max-game-thinking-time-seconds", FORMAT_HANDICAP.parse(textTime.getText()).intValue());
      Lizzie.config.leelazConfig.putOpt("play-ponder", Lizzie.config.playponder);

      Lizzie.config.advanceTimeTxt = txtAdvanceTime.getText();
      Lizzie.config.uiConfig.put("advance-time-txt", txtAdvanceTime.getText());

      Lizzie.config.kataTimeMainTimeMins =
          Utils.parseTextToInt(txtKataTimeSaveMins, Lizzie.config.kataTimeMainTimeMins);
      Lizzie.config.kataTimeByoyomiSecs =
          Utils.parseTextToInt(txtKataTimeByoyomiSecs, Lizzie.config.kataTimeByoyomiSecs);
      Lizzie.config.kataTimeByoyomiTimes =
          Utils.parseTextToInt(txtKataTimeByoyomiTimes, Lizzie.config.kataTimeByoyomiTimes);
      Lizzie.config.kataTimeFisherIncrementSecs =
          Utils.parseTextToInt(
              txtKataTimeFisherIncrementSecs, Lizzie.config.kataTimeFisherIncrementSecs);
      Lizzie.config.uiConfig.put("kata-time-main-time-mins", Lizzie.config.kataTimeMainTimeMins);
      Lizzie.config.uiConfig.put("kata-time-byoyomi-secs", Lizzie.config.kataTimeByoyomiSecs);
      Lizzie.config.uiConfig.put("kata-time-byoyomi-times", Lizzie.config.kataTimeByoyomiTimes);
      Lizzie.config.uiConfig.put(
          "kata-time-fisher-increment-secs", Lizzie.config.kataTimeFisherIncrementSecs);

      Lizzie.config.checkPlayBlack = checkBoxPlayerIsBlack.getSelectedIndex() == 0;
      Lizzie.config.checkContinuePlay = checkContinuePlay.isSelected();
      Lizzie.config.genmoveGameNoTime = chkNoTime.isSelected();
      Lizzie.config.newGameKomi = komi;
      Lizzie.config.newGameHandicap = textFieldHandicap.getSelectedIndex();
      Lizzie.config.limitMyTime = chkLimitMyTime.isSelected();
      Lizzie.config.limitMyTime = chkLimitMyTime.isSelected();
      Lizzie.config.mySaveTime = textSaveTime.getSelectedIndex();
      Lizzie.config.myByoyomiSeconds = texByoSeconds.getSelectedIndex();
      Lizzie.config.myByoyomiTimes = texByoTimes.getSelectedIndex();

      Lizzie.config.uiConfig.put("check-play-black", Lizzie.config.checkPlayBlack);
      Lizzie.config.uiConfig.put("check-continue-play", Lizzie.config.checkContinuePlay);
      Lizzie.config.uiConfig.put("genmove-game-notime", Lizzie.config.genmoveGameNoTime);
      Lizzie.config.uiConfig.put("new-game-komi", Lizzie.config.newGameKomi);
      Lizzie.config.uiConfig.put("new-game-handicap", Lizzie.config.newGameHandicap);

      Lizzie.config.uiConfig.put("limit-my-time", Lizzie.config.limitMyTime);
      Lizzie.config.uiConfig.put("my-save-time", Lizzie.config.mySaveTime);
      Lizzie.config.uiConfig.put("my-byoyomo-seconds", Lizzie.config.myByoyomiSeconds);
      Lizzie.config.uiConfig.put("my-byoyomo-times", Lizzie.config.myByoyomiTimes);
      LizzieFrame.toolbar.setChkShowBlack(chkShowBlack.isSelected());
      LizzieFrame.toolbar.setChkShowWhite(chkShowWhite.isSelected());
      LizzieFrame.menu.setChkShowBlack(chkShowBlack.isSelected());
      LizzieFrame.menu.setChkShowWhite(chkShowWhite.isSelected());
      // close window
      cancelled = false;
      setVisible(false);
    } catch (ParseException e) {
      // hide input mistakes.
    }
  }

  //  public void setGameInfo(GameInfo gameInfo) {
  //    this.gameInfo = gameInfo;
  //
  //    //  textFieldBlack.setText(gameInfo.getPlayerBlack());
  //    // textFieldWhite.setText(gameInfo.getPlayerWhite());
  //    textFieldHandicap.setText(FORMAT_HANDICAP.format(gameInfo.getHandicap()));
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
