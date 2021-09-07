package featurecat.lizzie.gui;

import featurecat.lizzie.Config;
import featurecat.lizzie.Lizzie;
import featurecat.lizzie.util.Utils;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
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

  public SetAiTimes() {
    // setType(Type.POPUP);
    setTitle(resourceBundle.getString("SetAiTimes.title")); // ("修改AI用时");
    setAlwaysOnTop(Lizzie.frame.isAlwaysOnTop());
    Lizzie.setFrameSize(
        this,
        Lizzie.config.isFrameFontSmall() ? 515 : (Lizzie.config.isFrameFontMiddle() ? 525 : 565),
        295);
    getContentPane().setLayout(new BorderLayout());
    JPanel buttonPane = new JPanel();
    getContentPane().add(buttonPane, BorderLayout.CENTER);
    JButton okButton = new JButton(resourceBundle.getString("SetAiTimes.okButton")); // ("确定");
    okButton.setBounds(165, 220, 74, 29);

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

    JCheckBox chckbxNewCheckBox = new JCheckBox();
    chckbxNewCheckBox.setBounds(
        Lizzie.config.isFrameFontSmall() ? 299 : (Lizzie.config.isFrameFontMiddle() ? 309 : 349),
        75,
        21,
        23);
    buttonPane.add(chckbxNewCheckBox);

    ImageIcon iconSettings = new ImageIcon();
    try {
      this.setIconImage(ImageIO.read(MoreEngines.class.getResourceAsStream("/assets/logo.png")));
      iconSettings.setImage(
          ImageIO.read(AnalysisFrame.class.getResourceAsStream("/assets/settings.png")));
      Dimension screensize = Toolkit.getDefaultToolkit().getScreenSize();
      int x = (int) screensize.getWidth() / 2 - this.getWidth() / 2;
      int y = (int) screensize.getHeight() / 2 - this.getHeight() / 2;
      setLocation(x, y);
    } catch (IOException e1) {
      // TODO Auto-generated catch block
      e1.printStackTrace();
    }
    JButton btnNewButton = new JButton(iconSettings);
    btnNewButton.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            Utils.showHtmlMessage(
                resourceBundle.getString("AdvanceTimeSettings.title"),
                resourceBundle.getString("AdvanceTimeSettings.describe"));
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

    chckbxNewCheckBox.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            // TODO Auto-generated method stub
            Lizzie.config.advanceTimeSettings = chckbxNewCheckBox.isSelected();
            if (Lizzie.config.advanceTimeSettings) {
              txtSetTime.setEnabled(false);
              txtAdvanceTime.setEditable(true);
            } else {
              txtSetTime.setEnabled(true);
              txtAdvanceTime.setEditable(false);
            }
            Lizzie.config.uiConfig.put("advance-time-settings", Lizzie.config.advanceTimeSettings);
          }
        });
    chckbxNewCheckBox.setSelected(Lizzie.config.advanceTimeSettings);

    JFontLabel analyzeMode =
        new JFontLabel(resourceBundle.getString("SetAiTimes.analyzeMode")); // ("分析模式");
    analyzeMode.setBounds(10, 117, 151, 20);
    buttonPane.add(analyzeMode);

    JFontLabel lblTime =
        new JFontLabel(resourceBundle.getString("SetAiTimes.lblTime")); // ("每手用时(秒):");
    lblTime.setBounds(165, 117, 260, 20);
    buttonPane.add(lblTime);

    txtAnaGameTime = new JFontTextField();
    txtAnaGameTime.setBounds(
        Lizzie.config.isFrameFontSmall() ? 326 : (Lizzie.config.isFrameFontMiddle() ? 336 : 376),
        117,
        149,
        22);
    buttonPane.add(txtAnaGameTime);
    txtAnaGameTime.setColumns(10);

    JFontLabel lblPo =
        new JFontLabel(resourceBundle.getString("SetAiTimes.lblPo")); // ("每手总计算量(选填):");
    lblPo.setBounds(165, 143, 260, 20);
    buttonPane.add(lblPo);

    JFontLabel lblFirstPo =
        new JFontLabel(resourceBundle.getString("SetAiTimes.lblFirstPo")); // ("每手首位计算量(选填):");
    lblFirstPo.setBounds(165, 168, 287, 20);
    buttonPane.add(lblFirstPo);

    txtAnaGmaePlayouts = new JFontTextField();
    txtAnaGmaePlayouts.setColumns(10);
    txtAnaGmaePlayouts.setBounds(
        Lizzie.config.isFrameFontSmall() ? 326 : (Lizzie.config.isFrameFontMiddle() ? 336 : 376),
        143,
        149,
        22);
    buttonPane.add(txtAnaGmaePlayouts);

    txtAnaGmaeFirstPlayouts = new JFontTextField();
    txtAnaGmaeFirstPlayouts.setColumns(10);
    txtAnaGmaeFirstPlayouts.setBounds(
        Lizzie.config.isFrameFontSmall() ? 326 : (Lizzie.config.isFrameFontMiddle() ? 336 : 376),
        168,
        149,
        22);
    buttonPane.add(txtAnaGmaeFirstPlayouts);

    JButton cancel = new JButton(resourceBundle.getString("SetAiTimes.cancel")); // ("取消");
    cancel.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            setVisible(false);
          }
        });
    cancel.setActionCommand("OK");
    cancel.setBounds(243, 220, 74, 29);
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
    try {
      this.setIconImage(ImageIO.read(MoreEngines.class.getResourceAsStream("/assets/logo.png")));
    } catch (IOException e) {
      e.printStackTrace();
    }
    setLocationRelativeTo(getOwner());
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
      Lizzie.config.playponder = getPonder();
      Lizzie.config.leelazConfig.putOpt("play-ponder", Lizzie.config.playponder);
      LizzieFrame.sendAiTime(true, Lizzie.leelaz);

      DecimalFormat FORMAT_HANDICAP = new DecimalFormat("0");

      try {
        if (FORMAT_HANDICAP.parse(txtAnaGameTime.getText().trim()).intValue() > 0) {
          LizzieFrame.toolbar.chkAutoPlayTime.setSelected(true);
          LizzieFrame.toolbar.txtAutoPlayTime.setText(
              FORMAT_HANDICAP.parse(txtAnaGameTime.getText().trim()).intValue() + "");
        } else LizzieFrame.toolbar.chkAutoPlayTime.setSelected(false);
      } catch (Exception ex) {
        LizzieFrame.toolbar.chkAutoPlayTime.setSelected(false);
      }

      try {
        if (FORMAT_HANDICAP.parse(txtAnaGmaePlayouts.getText().trim()).intValue() > 0) {
          LizzieFrame.toolbar.chkAutoPlayPlayouts.setSelected(true);
          LizzieFrame.toolbar.txtAutoPlayPlayouts.setText(
              FORMAT_HANDICAP.parse(txtAnaGmaePlayouts.getText().trim()).intValue() + "");
        } else LizzieFrame.toolbar.chkAutoPlayPlayouts.setSelected(false);
      } catch (Exception ex) {
        LizzieFrame.toolbar.chkAutoPlayPlayouts.setSelected(false);
      }
      if (FORMAT_HANDICAP.parse(txtAnaGmaeFirstPlayouts.getText().trim()).intValue() > 0) {
        LizzieFrame.toolbar.chkAutoPlayFirstPlayouts.setSelected(true);
        LizzieFrame.toolbar.txtAutoPlayFirstPlayouts.setText(
            FORMAT_HANDICAP.parse(txtAnaGmaeFirstPlayouts.getText().trim()).intValue() + "");
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
