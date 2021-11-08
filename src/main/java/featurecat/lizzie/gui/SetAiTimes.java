package featurecat.lizzie.gui;

import featurecat.lizzie.Config;
import featurecat.lizzie.Lizzie;
import featurecat.lizzie.util.Utils;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ResourceBundle;
import javax.imageio.ImageIO;
import javax.swing.Action;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import javax.swing.text.InternationalFormatter;

public class SetAiTimes extends JDialog {
  private JFormattedTextField txtSetTime;
  private final ResourceBundle resourceBundle = Lizzie.resourceBundle;
  private int changeMoveNumber;
  JFontRadioButton rdoNoPonder;
  JFontRadioButton rdoPonder;
  private JFontTextField txtAdvanceTime;
  private JFontTextField txtAnaGameTime;
  private JFontTextField txtAnaGmaePlayouts;
  private JFontTextField txtAnaGmaeFirstPlayouts;

  JCheckBox chkUseNormal;
  JCheckBox chkUseAdvTime;
  JCheckBox chkUseKataTime;
  JComboBox<String> kataTimeComboBox;
  private JTextField txtKataTimeSaveMins;
  private JTextField txtKataTimeByoyomiSecs;
  private JTextField txtKataTimeByoyomiTimes;
  private JTextField txtKataTimeFisherIncrementSecs;
  private Window thisDialog = this;

  public SetAiTimes(Window owner) {
    super(owner);
    setTitle(resourceBundle.getString("SetAiTimes.title")); // ("修改AI用时");
    setAlwaysOnTop(Lizzie.frame.isAlwaysOnTop());
    Lizzie.setFrameSize(
        this,
        Lizzie.config.isFrameFontSmall() ? 715 : (Lizzie.config.isFrameFontMiddle() ? 790 : 940),
        325);
    // this.setSize(new Dimension(700,400));
    getContentPane().setLayout(new BorderLayout());
    JPanel buttonPane = new JPanel();
    getContentPane().add(buttonPane, BorderLayout.CENTER);
    JButton okButton = new JFontButton(resourceBundle.getString("SetAiTimes.okButton")); // ("确定");
    okButton.setBounds(165, 260, 104, 29);

    okButton.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            if (checkMove()) {
              setVisible(false);
              applyChange();
            }
          }
        });
    buttonPane.setLayout(null);
    okButton.setActionCommand("OK");
    buttonPane.add(okButton);
    getRootPane().setDefaultButton(okButton);
    NumberFormat nf = NumberFormat.getIntegerInstance();
    nf.setGroupingUsed(false);

    JFontLabel lblGenmoveMode =
        new JFontLabel(
            resourceBundle.getString("SetAiTimes.lblGenmoveMode")); // ("Genmove模式  每手用时(秒):");
    lblGenmoveMode.setBounds(10, 47, 150, 20);
    buttonPane.add(lblGenmoveMode);

    JFontLabel lblGenmoveTime =
        new JFontLabel(
            resourceBundle.getString("SetAiTimes.lblGenmoveTime")); // ("Genmove模式  每手用时(秒):");
    lblGenmoveTime.setBounds(165, 47, 201, 20);
    buttonPane.add(lblGenmoveTime);

    txtSetTime =
        new JFormattedTextField(
            new InternationalFormatter(nf) {
              protected DocumentFilter getDocumentFilter() {
                return filter;
              }

              private DocumentFilter filter = new DigitOnlyFilter();
            });
    txtSetTime.setFont(new Font(Config.sysDefaultFontName, Font.PLAIN, Config.frameFontSize));
    txtSetTime.setBounds(
        Lizzie.config.isFrameFontSmall() ? 326 : (Lizzie.config.isFrameFontMiddle() ? 336 : 376),
        47,
        149,
        22);
    buttonPane.add(txtSetTime);
    txtSetTime.setColumns(10);

    txtSetTime.setText(
        String.valueOf(Lizzie.config.leelazConfig.getInt("max-game-thinking-time-seconds")));

    JFontLabel lblPonder =
        new JFontLabel(resourceBundle.getString("SetAiTimes.lblPonder")); // ("对弈时AI是否后台计算");
    lblPonder.setBounds(10, 9, 160, 20);
    buttonPane.add(lblPonder);

    rdoPonder = new JFontRadioButton(resourceBundle.getString("SetAiTimes.rdoPonder")); // ("是");
    rdoPonder.setBounds(165, 9, 53, 23);
    buttonPane.add(rdoPonder);

    rdoNoPonder =
        new JFontRadioButton(resourceBundle.getString("SetAiTimes.rdoNoPonder")); // ("否");
    rdoNoPonder.setBounds(220, 9, 60, 23);
    buttonPane.add(rdoNoPonder);

    ButtonGroup rdopondergp = new ButtonGroup();
    rdopondergp.add(rdoNoPonder);
    rdopondergp.add(rdoPonder);

    JFontLabel lblAdvTime =
        new JFontLabel(resourceBundle.getString("SetAiTimes.lblAdvTime")); // ("高级时间设置:");
    lblAdvTime.setBounds(165, 75, 227, 20);
    buttonPane.add(lblAdvTime);

    chkUseNormal = new JCheckBox();
    chkUseNormal.setBounds(
        Lizzie.config.isFrameFontSmall() ? 299 : (Lizzie.config.isFrameFontMiddle() ? 309 : 353),
        47,
        21,
        23);
    buttonPane.add(chkUseNormal);

    chkUseAdvTime = new JCheckBox();
    chkUseAdvTime.setBounds(
        Lizzie.config.isFrameFontSmall() ? 299 : (Lizzie.config.isFrameFontMiddle() ? 309 : 353),
        75,
        21,
        23);
    buttonPane.add(chkUseAdvTime);

    JFontLabel lblKataTimes = new JFontLabel(resourceBundle.getString("SetAiTimes.lblKataTime"));
    lblKataTimes.setBounds(165, 103, 227, 20);
    buttonPane.add(lblKataTimes);

    chkUseKataTime = new JCheckBox();
    chkUseKataTime.setBounds(
        Lizzie.config.isFrameFontSmall() ? 299 : (Lizzie.config.isFrameFontMiddle() ? 309 : 353),
        103,
        21,
        23);
    buttonPane.add(chkUseKataTime);

    JPanel kataTimePanel = new JPanel();
    kataTimePanel.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
    kataTimePanel.setBounds(
        Lizzie.config.isFrameFontSmall() ? 326 : (Lizzie.config.isFrameFontMiddle() ? 336 : 376),
        103,
        800,
        38);
    kataTimeComboBox = new JFontComboBox<String>();
    kataTimeComboBox.addItem(resourceBundle.getString("NewGameDialog.kataTime.byoyomi")); // "读秒制");
    kataTimeComboBox.addItem(resourceBundle.getString("NewGameDialog.kataTime.fisher")); // "加秒制");
    kataTimeComboBox.addItem(
        resourceBundle.getString("NewGameDialog.kataTime.absolute")); // "包干制");
    kataTimePanel.add(kataTimeComboBox);

    JLabel lblKataTimeSaveMins =
        new JFontLabel(resourceBundle.getString("Byoyomi.newGame.saveTime"));
    kataTimePanel.add(lblKataTimeSaveMins);
    txtKataTimeSaveMins = new JFontTextField();
    kataTimePanel.add(txtKataTimeSaveMins);
    txtKataTimeSaveMins.setColumns(3);
    txtKataTimeSaveMins.setDocument(new IntDocument());
    txtKataTimeSaveMins.setText(String.valueOf(Lizzie.config.kataTimeMainTimeMins));

    JLabel lblKataTimeByoyomiSecs =
        new JFontLabel(resourceBundle.getString("Byoyomi.newGame.byoyomi"));
    kataTimePanel.add(lblKataTimeByoyomiSecs);
    txtKataTimeByoyomiSecs = new JFontTextField();
    kataTimePanel.add(txtKataTimeByoyomiSecs);
    txtKataTimeByoyomiSecs.setColumns(3);
    txtKataTimeByoyomiSecs.setDocument(new IntDocument());
    txtKataTimeByoyomiSecs.setText(String.valueOf(Lizzie.config.kataTimeByoyomiSecs));

    JLabel lblKataTimeByoyomiTimes =
        new JFontLabel(resourceBundle.getString("Byoyomi.newGame.byoyomiTimes"));
    kataTimePanel.add(lblKataTimeByoyomiTimes);
    txtKataTimeByoyomiTimes = new JFontTextField();
    kataTimePanel.add(txtKataTimeByoyomiTimes);
    txtKataTimeByoyomiTimes.setColumns(3);
    txtKataTimeByoyomiTimes.setDocument(new IntDocument());
    txtKataTimeByoyomiTimes.setText(String.valueOf(Lizzie.config.kataTimeByoyomiTimes));

    JLabel lblKataTimeFisherIncrementSecs =
        new JFontLabel(
            resourceBundle.getString("NewGameDialog.kataTime.increment")); // ("每手增加(秒)");
    kataTimePanel.add(lblKataTimeFisherIncrementSecs);
    txtKataTimeFisherIncrementSecs = new JFontTextField();
    kataTimePanel.add(txtKataTimeFisherIncrementSecs);
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
          }
        });
    buttonPane.add(kataTimePanel);

    ImageIcon iconSettings = new ImageIcon();
    try {
      this.setIconImage(ImageIO.read(MoreEngines.class.getResourceAsStream("/assets/logo.png")));
      iconSettings.setImage(ImageIO.read(getClass().getResourceAsStream("/assets/settings.png")));
    } catch (IOException e1) {
      // TODO Auto-generated catch block
      e1.printStackTrace();
    }
    JButton btnNewButton = new JButton(iconSettings);
    btnNewButton.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            Utils.showHtmlMessageModal(
                resourceBundle.getString("AdvanceTimeSettings.title"),
                resourceBundle.getString("AdvanceTimeSettings.describe"),
                thisDialog);
          }
        });
    btnNewButton.setBounds(144, 77, 18, 18);
    buttonPane.add(btnNewButton);

    txtAdvanceTime = new JFontTextField();
    txtAdvanceTime.setBounds(
        Lizzie.config.isFrameFontSmall() ? 326 : (Lizzie.config.isFrameFontMiddle() ? 336 : 376),
        74,
        149,
        24);
    buttonPane.add(txtAdvanceTime);
    txtAdvanceTime.setColumns(13);
    txtAdvanceTime.setText(Lizzie.config.advanceTimeTxt);

    JFontLabel analyzeMode =
        new JFontLabel(resourceBundle.getString("SetAiTimes.analyzeMode")); // ("分析模式");
    analyzeMode.setBounds(10, 157, 151, 20);
    buttonPane.add(analyzeMode);

    JFontLabel lblTime =
        new JFontLabel(resourceBundle.getString("SetAiTimes.lblTime")); // ("每手用时(秒):");
    lblTime.setBounds(165, 157, 260, 20);
    buttonPane.add(lblTime);

    txtAnaGameTime = new JFontTextField();
    txtAnaGameTime.setBounds(
        Lizzie.config.isFrameFontSmall() ? 326 : (Lizzie.config.isFrameFontMiddle() ? 336 : 376),
        157,
        149,
        22);
    buttonPane.add(txtAnaGameTime);
    txtAnaGameTime.setColumns(10);

    JFontLabel lblPo =
        new JFontLabel(resourceBundle.getString("SetAiTimes.lblPo")); // ("每手总计算量(选填):");
    lblPo.setBounds(165, 183, 260, 20);
    buttonPane.add(lblPo);

    JFontLabel lblFirstPo =
        new JFontLabel(resourceBundle.getString("SetAiTimes.lblFirstPo")); // ("每手首位计算量(选填):");
    lblFirstPo.setBounds(165, 208, 287, 20);
    buttonPane.add(lblFirstPo);

    txtAnaGmaePlayouts = new JFontTextField();
    txtAnaGmaePlayouts.setColumns(10);
    txtAnaGmaePlayouts.setBounds(
        Lizzie.config.isFrameFontSmall() ? 326 : (Lizzie.config.isFrameFontMiddle() ? 336 : 376),
        183,
        149,
        22);
    buttonPane.add(txtAnaGmaePlayouts);

    txtAnaGmaeFirstPlayouts = new JFontTextField();
    txtAnaGmaeFirstPlayouts.setColumns(10);
    txtAnaGmaeFirstPlayouts.setBounds(
        Lizzie.config.isFrameFontSmall() ? 326 : (Lizzie.config.isFrameFontMiddle() ? 336 : 376),
        208,
        149,
        22);
    buttonPane.add(txtAnaGmaeFirstPlayouts);

    JButton cancel = new JFontButton(resourceBundle.getString("SetAiTimes.cancel")); // ("取消");
    cancel.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            setVisible(false);
          }
        });
    cancel.setActionCommand("OK");
    cancel.setBounds(283, 260, 104, 29);
    buttonPane.add(cancel);

    if (LizzieFrame.toolbar.chkAutoPlayTime.isSelected())
      txtAnaGameTime.setText(LizzieFrame.toolbar.txtAutoPlayTime.getText().trim());

    if (LizzieFrame.toolbar.chkAutoPlayPlayouts.isSelected())
      txtAnaGmaePlayouts.setText(LizzieFrame.toolbar.txtAutoPlayPlayouts.getText().trim());

    if (LizzieFrame.toolbar.chkAutoPlayFirstPlayouts.isSelected())
      txtAnaGmaeFirstPlayouts.setText(
          LizzieFrame.toolbar.txtAutoPlayFirstPlayouts.getText().trim());

    if (Lizzie.config.advanceTimeSettings) txtSetTime.setEnabled(false);
    else txtAdvanceTime.setEditable(false);

    if (Lizzie.config.playponder) {
      rdoPonder.setSelected(true);
    } else {
      rdoNoPonder.setSelected(true);
    }

    chkUseNormal.addActionListener(
        new ActionListener() {
          @Override
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

    chkUseKataTime.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            chkTimeChanged();
          }
        });
    chkUseAdvTime.setSelected(Lizzie.config.advanceTimeSettings);
    chkUseKataTime.setSelected(Lizzie.config.kataTimeSettings);
    chkUseNormal.setSelected(!Lizzie.config.advanceTimeSettings && !Lizzie.config.kataTimeSettings);
    chkTimeChanged();
    ButtonGroup chkTimeGroup = new ButtonGroup();
    chkTimeGroup.add(chkUseNormal);
    chkTimeGroup.add(chkUseKataTime);
    chkTimeGroup.add(chkUseAdvTime);

    try {
      this.setIconImage(ImageIO.read(MoreEngines.class.getResourceAsStream("/assets/logo.png")));
    } catch (IOException e) {
      e.printStackTrace();
    }
    setLocationRelativeTo(owner);
  }

  private void chkTimeChanged() {
    Lizzie.config.kataTimeSettings = chkUseKataTime.isSelected();
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
      txtSetTime.setEnabled(false);
    } else {
      txtSetTime.setEnabled(true);
    }
  }

  private class DigitOnlyFilter extends DocumentFilter {
    @Override
    public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr)
        throws BadLocationException {
      String newStr = string != null ? string.replaceAll("\\D++", "") : "";
      if (!newStr.isEmpty()) {
        fb.insertString(offset, newStr, attr);
      }
    }
  }

  private boolean getPonder() {
    if (rdoPonder.isSelected()) {
      Lizzie.config.playponder = true;
      return true;
    }
    if (rdoNoPonder.isSelected()) {
      Lizzie.config.playponder = false;
      return false;
    }
    return true;
  }

  private void applyChange() {
    try {
      if (Lizzie.config.advanceTimeSettings) {
        Lizzie.config.advanceTimeTxt = txtAdvanceTime.getText();
        Lizzie.config.uiConfig.put("advance-time-txt", txtAdvanceTime.getText());
      }
      int time = txtFieldValue(txtSetTime);
      if (time > 0) {
        Lizzie.config.maxGameThinkingTimeSeconds = time;
        Lizzie.config.leelazConfig.putOpt("max-game-thinking-time-seconds", time);
      }
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
      Lizzie.config.playponder = getPonder();
      Lizzie.config.leelazConfig.putOpt("play-ponder", Lizzie.config.playponder);
      LizzieFrame.sendAiTime(true, Lizzie.leelaz, true);

      DecimalFormat FORMAT_HANDICAP = new DecimalFormat("0");

      try {
        if (FORMAT_HANDICAP.parse(txtAnaGameTime.getText().trim()).intValue() > 0) {
          LizzieFrame.toolbar.chkAutoPlayTime.setSelected(true);
          LizzieFrame.toolbar.txtAutoPlayTime.setText(
              String.valueOf(FORMAT_HANDICAP.parse(txtAnaGameTime.getText().trim()).intValue()));
        } else LizzieFrame.toolbar.chkAutoPlayTime.setSelected(false);
      } catch (Exception ex) {
        LizzieFrame.toolbar.chkAutoPlayTime.setSelected(false);
      }

      try {
        if (FORMAT_HANDICAP.parse(txtAnaGmaePlayouts.getText().trim()).intValue() > 0) {
          LizzieFrame.toolbar.chkAutoPlayPlayouts.setSelected(true);
          LizzieFrame.toolbar.txtAutoPlayPlayouts.setText(
              String.valueOf(
                  FORMAT_HANDICAP.parse(txtAnaGmaePlayouts.getText().trim()).intValue()));
        } else LizzieFrame.toolbar.chkAutoPlayPlayouts.setSelected(false);
      } catch (Exception ex) {
        LizzieFrame.toolbar.chkAutoPlayPlayouts.setSelected(false);
      }
      if (FORMAT_HANDICAP.parse(txtAnaGmaeFirstPlayouts.getText().trim()).intValue() > 0) {
        LizzieFrame.toolbar.chkAutoPlayFirstPlayouts.setSelected(true);
        LizzieFrame.toolbar.txtAutoPlayFirstPlayouts.setText(
            String.valueOf(
                FORMAT_HANDICAP.parse(txtAnaGmaeFirstPlayouts.getText().trim()).intValue()));
      } else LizzieFrame.toolbar.chkAutoPlayFirstPlayouts.setSelected(false);

    } catch (Exception ex) {
      LizzieFrame.toolbar.chkAutoPlayFirstPlayouts.setSelected(false);
    }
    if (Lizzie.frame.isAnaPlayingAgainstLeelaz || Lizzie.leelaz.isPondering())
      Lizzie.leelaz.ponder();
  }

  private Integer txtFieldValue(JTextField txt) {
    if (txt.getText().trim().isEmpty()
        || txt.getText().trim().length() >= String.valueOf(Integer.MAX_VALUE).length()) {
      return 0;
    } else {
      return Integer.parseInt(txt.getText().trim());
    }
  }

  private boolean checkMove() {

    changeMoveNumber = txtFieldValue(txtSetTime);
    if (changeMoveNumber <= 0) {
      txtSetTime.setToolTipText(resourceBundle.getString("LizzieChangeMove.txtMoveNumber.error"));
      Action action = txtSetTime.getActionMap().get("postTip");
      if (action != null) {
        ActionEvent ae =
            new ActionEvent(
                txtSetTime,
                ActionEvent.ACTION_PERFORMED,
                "postTip",
                EventQueue.getMostRecentEventTime(),
                0);
        action.actionPerformed(ae);
      }
      txtSetTime.setBackground(Color.red);
      return false;
    }
    return true;
  }
}
