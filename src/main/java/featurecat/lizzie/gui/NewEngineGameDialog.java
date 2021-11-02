/*
 * Created by JFormDesigner on Wed Apr 04 22:17:33 CEST 2018
 */

package featurecat.lizzie.gui;

import featurecat.lizzie.Config;
import featurecat.lizzie.Lizzie;
import featurecat.lizzie.analysis.GameInfo;
import featurecat.lizzie.rules.Board;
import featurecat.lizzie.rules.Stone;
import featurecat.lizzie.util.Utils;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

/** @author unknown */
public class NewEngineGameDialog extends JDialog {
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

  // private JCheckBox checkBoxPlayerIsBlack;
  private JFontTextField textFieldKomi;
  private JTextField textFieldHandicap;
  //  private JFontTextField textFieldDelay;

  private JFontTextField txtresignSettingBlackMinMove;
  private JFontTextField txtresignSettingBlack;
  private JFontTextField txtresignSettingBlack2;

  private JFontTextField txtresignSettingWhiteMinMove;
  private JFontTextField txtresignSettingWhite;
  private JFontTextField txtresignSettingWhite2;

  public JComboBox<String> enginePkBlack;
  public JComboBox<String> enginePkWhite;
  private JComboBox<String> cbxRandomSgf;

  private JCheckBox chkDisableWRNInGame;
  private JCheckBox chkUseAdvanceTime;
  private JCheckBox chkSGFstart;
  private JFontCheckBox chkContinuePlay;
  private JFontButton btnSGFstart;
  // private ActionListener chkEnginePkContinueListener;
  // private ActionListener chkBatchGameListener;
  private JFontLabel lblsgf =
      new JFontLabel(Lizzie.resourceBundle.getString("NewEngineGameDialog.lblsgf"));

  private boolean cancelled = true;
  private GameInfo gameInfo;

  public NewEngineGameDialog(Window owner) {
    super(owner);
    // Lizzie.frame.removeInput();
    initComponents();
  }

  // private final ResourceBundle resourceBundle = Lizzie.resourceBundle;
  private JFontTextField txtBlackAdvanceTime;
  private JFontTextField txtWhiteAdvanceTime;
  private Window thisDialog = this;

  private void initComponents() {
    // if (Lizzie.config.showHiddenYzy) setMinimumSize(new Dimension(380, 330));
    // else
    // setMinimumSize(new Dimension(580, 530));
    Lizzie.setFrameSize(
        this,
        Lizzie.config.isFrameFontSmall() ? 445 : (Lizzie.config.isFrameFontMiddle() ? 515 : 595),
        Lizzie.config.isFrameFontSmall() ? 408 : (Lizzie.config.isFrameFontMiddle() ? 409 : 421));
    setResizable(false);
    setTitle(Lizzie.resourceBundle.getString("NewEngineGameDialog.title")); // "引擎对战");
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
    // pack();
    setLocationRelativeTo(getOwner());
  }

  private void initDialogPane(Container contentPane) {
    dialogPane.setBorder(new EmptyBorder(0, 0, 0, 0));
    dialogPane.setLayout(new BorderLayout());

    initContentPanel();
    initButtonBar();

    contentPane.add(dialogPane, BorderLayout.CENTER);
  }

  private void initContentPanel() {
    contentPanel.setLayout(null);

    //    checkBoxPlayerIsBlack =
    //        new JCheckBox(resourceBundle.getString("NewGameDialog.PlayBlack"), true);
    //    checkBoxPlayerIsBlack.addChangeListener(evt -> togglePlayerIsBlack());

    JTextArea resignThresoldHint =
        new JTextArea(Lizzie.resourceBundle.getString("EnginePkConfig.lblresignGenmove"));
    resignThresoldHint.setFont(
        new Font(Config.sysDefaultFontName, Font.PLAIN, Config.frameFontSize));
    resignThresoldHint.setLineWrap(true);
    resignThresoldHint.setFocusable(false);
    resignThresoldHint.setBackground(this.getBackground());

    JFontLabel lblresignSettingBlack =
        new JFontLabel(
            Lizzie.resourceBundle.getString("EnginePkConfig.lblresignSettingBlack")); // ("认输阈值:");
    JFontLabel lblresignSettingBlackConsistent =
        new JFontLabel(
            Lizzie.resourceBundle.getString("EnginePkConfig.lblresignSettingConsistent"));
    JFontLabel lblresignSettingBlack2 =
        new JFontLabel(
            Lizzie.resourceBundle.getString("EnginePkConfig.lblresignSetting2")); // ("手胜率低于");
    JFontLabel lblresignSettingBlack3 =
        new JFontLabel(Lizzie.resourceBundle.getString("EnginePkConfig.lblresignSetting3"));
    JFontLabel lblresignSettingBlack4 = new JFontLabel("%");

    txtresignSettingBlack = new JFontTextField();
    txtresignSettingBlack.setDocument(new IntDocument());
    txtresignSettingBlack2 = new JFontTextField();
    txtresignSettingBlack2.setDocument(new DoubleDocument());
    txtresignSettingBlackMinMove = new JFontTextField();
    txtresignSettingBlackMinMove.setDocument(new IntDocument());

    contentPanel.add(resignThresoldHint);
    contentPanel.add(lblresignSettingBlack);
    contentPanel.add(lblresignSettingBlackConsistent);
    contentPanel.add(lblresignSettingBlack2);
    contentPanel.add(lblresignSettingBlack3);
    contentPanel.add(lblresignSettingBlack4);
    contentPanel.add(txtresignSettingBlack);
    contentPanel.add(txtresignSettingBlack2);
    contentPanel.add(txtresignSettingBlackMinMove);

    resignThresoldHint.setBounds(
        5,
        180,
        Lizzie.config.isFrameFontSmall() ? 425 : (Lizzie.config.isFrameFontMiddle() ? 495 : 575),
        55);
    resignThresoldHint.setVisible(false);
    lblresignSettingBlack.setBounds(
        5,
        180,
        Lizzie.config.isFrameFontSmall() ? 435 : (Lizzie.config.isFrameFontMiddle() ? 505 : 585),
        25);
    lblresignSettingBlackConsistent.setBounds(
        Lizzie.config.isFrameFontSmall() ? 140 : (Lizzie.config.isFrameFontMiddle() ? 125 : 143),
        180,
        197,
        25);
    lblresignSettingBlack2.setBounds(
        Lizzie.config.isFrameFontSmall() ? 225 : (Lizzie.config.isFrameFontMiddle() ? 228 : 257),
        180,
        57,
        25);
    lblresignSettingBlack3.setBounds(
        Lizzie.config.isFrameFontSmall() ? 295 : (Lizzie.config.isFrameFontMiddle() ? 295 : 317),
        180,
        122,
        25);
    lblresignSettingBlack4.setBounds(
        Lizzie.config.isFrameFontSmall() ? 411 : (Lizzie.config.isFrameFontMiddle() ? 426 : 472),
        180,
        25,
        25);
    txtresignSettingBlackMinMove.setBounds(
        Lizzie.config.isFrameFontSmall() ? 192 : (Lizzie.config.isFrameFontMiddle() ? 195 : 226),
        Lizzie.config.isFrameFontSmall() ? 183 : (Lizzie.config.isFrameFontMiddle() ? 182 : 181),
        30,
        Lizzie.config.isFrameFontSmall() ? 20 : (Lizzie.config.isFrameFontMiddle() ? 22 : 24));
    txtresignSettingBlack.setBounds(
        Lizzie.config.isFrameFontSmall() ? 272 : (Lizzie.config.isFrameFontMiddle() ? 272 : 297),
        Lizzie.config.isFrameFontSmall() ? 183 : (Lizzie.config.isFrameFontMiddle() ? 182 : 181),
        20,
        Lizzie.config.isFrameFontSmall() ? 20 : (Lizzie.config.isFrameFontMiddle() ? 22 : 24));
    txtresignSettingBlack2.setBounds(
        Lizzie.config.isFrameFontSmall() ? 373 : (Lizzie.config.isFrameFontMiddle() ? 383 : 425),
        Lizzie.config.isFrameFontSmall() ? 183 : (Lizzie.config.isFrameFontMiddle() ? 182 : 181),
        Lizzie.config.isFrameFontSmall() ? 35 : (Lizzie.config.isFrameFontMiddle() ? 40 : 45),
        Lizzie.config.isFrameFontSmall() ? 20 : (Lizzie.config.isFrameFontMiddle() ? 22 : 24));

    JFontLabel lblresignSettingWhite =
        new JFontLabel(Lizzie.resourceBundle.getString("EnginePkConfig.lblresignSettingWhite"));
    JFontLabel lblresignSettingWhiteConsistent =
        new JFontLabel(
            Lizzie.resourceBundle.getString("EnginePkConfig.lblresignSettingConsistent"));
    JFontLabel lblresignSettingWhite2 =
        new JFontLabel(Lizzie.resourceBundle.getString("EnginePkConfig.lblresignSetting2"));
    JFontLabel lblresignSettingWhite3 =
        new JFontLabel(Lizzie.resourceBundle.getString("EnginePkConfig.lblresignSetting3"));
    JFontLabel lblresignSettingWhite4 = new JFontLabel("%");

    txtresignSettingWhite = new JFontTextField();
    txtresignSettingWhite.setDocument(new IntDocument());
    txtresignSettingWhite2 = new JFontTextField();
    txtresignSettingWhite2.setDocument(new DoubleDocument());
    txtresignSettingWhiteMinMove = new JFontTextField();
    txtresignSettingWhiteMinMove.setDocument(new IntDocument());

    lblresignSettingWhite.setBounds(5, 210, 197, 25);
    lblresignSettingWhiteConsistent.setBounds(
        Lizzie.config.isFrameFontSmall() ? 140 : (Lizzie.config.isFrameFontMiddle() ? 125 : 143),
        210,
        197,
        25);
    lblresignSettingWhite2.setBounds(
        Lizzie.config.isFrameFontSmall() ? 225 : (Lizzie.config.isFrameFontMiddle() ? 228 : 257),
        210,
        57,
        25);
    lblresignSettingWhite3.setBounds(
        Lizzie.config.isFrameFontSmall() ? 295 : (Lizzie.config.isFrameFontMiddle() ? 295 : 317),
        210,
        122,
        25);
    lblresignSettingWhite4.setBounds(
        Lizzie.config.isFrameFontSmall() ? 411 : (Lizzie.config.isFrameFontMiddle() ? 426 : 472),
        210,
        25,
        25);

    txtresignSettingWhiteMinMove.setBounds(
        Lizzie.config.isFrameFontSmall() ? 192 : (Lizzie.config.isFrameFontMiddle() ? 195 : 226),
        Lizzie.config.isFrameFontSmall() ? 214 : (Lizzie.config.isFrameFontMiddle() ? 213 : 212),
        30,
        Lizzie.config.isFrameFontSmall() ? 20 : (Lizzie.config.isFrameFontMiddle() ? 22 : 24));
    txtresignSettingWhite.setBounds(
        Lizzie.config.isFrameFontSmall() ? 272 : (Lizzie.config.isFrameFontMiddle() ? 272 : 297),
        Lizzie.config.isFrameFontSmall() ? 214 : (Lizzie.config.isFrameFontMiddle() ? 213 : 212),
        20,
        Lizzie.config.isFrameFontSmall() ? 20 : (Lizzie.config.isFrameFontMiddle() ? 22 : 24));
    txtresignSettingWhite2.setBounds(
        Lizzie.config.isFrameFontSmall() ? 373 : (Lizzie.config.isFrameFontMiddle() ? 383 : 425),
        Lizzie.config.isFrameFontSmall() ? 214 : (Lizzie.config.isFrameFontMiddle() ? 213 : 212),
        Lizzie.config.isFrameFontSmall() ? 35 : (Lizzie.config.isFrameFontMiddle() ? 40 : 45),
        Lizzie.config.isFrameFontSmall() ? 20 : (Lizzie.config.isFrameFontMiddle() ? 22 : 24));

    contentPanel.add(lblresignSettingWhite);
    contentPanel.add(lblresignSettingWhiteConsistent);
    contentPanel.add(lblresignSettingWhite3);
    contentPanel.add(lblresignSettingWhite2);
    contentPanel.add(lblresignSettingWhite4);
    contentPanel.add(txtresignSettingWhiteMinMove);
    contentPanel.add(txtresignSettingWhite);
    contentPanel.add(txtresignSettingWhite2);

    txtresignSettingBlack.setText(String.valueOf(Lizzie.config.firstEngineResignMoveCounts));
    txtresignSettingBlack2.setText(String.valueOf(Lizzie.config.firstEngineResignWinrate));
    txtresignSettingBlackMinMove.setText(String.valueOf(Lizzie.config.firstEngineMinMove));

    txtresignSettingWhite.setText(String.valueOf(Lizzie.config.secondEngineResignMoveCounts));
    txtresignSettingWhite2.setText(String.valueOf(Lizzie.config.secondEngineResignWinrate));
    txtresignSettingWhiteMinMove.setText(String.valueOf(Lizzie.config.secondEngineMinMove));

    textFieldKomi = new JFontTextField();
    textFieldKomi.setDocument(new KomiDocument(true));
    NumberFormat nf = NumberFormat.getIntegerInstance();
    nf.setGroupingUsed(false);
    textFieldHandicap = new JTextField();
    textFieldHandicap.setDocument(new IntDocument());
    textFieldHandicap.setFont(
        new Font(Config.sysDefaultFontName, Font.PLAIN, Config.frameFontSize));
    textFieldHandicap.addPropertyChangeListener(evt -> modifyHandicap());

    enginePkBlack = LizzieFrame.toolbar.enginePkBlack;
    enginePkBlack.setFont(new Font(Config.sysDefaultFontName, Font.PLAIN, Config.frameFontSize));
    enginePkWhite = LizzieFrame.toolbar.enginePkWhite;
    enginePkWhite.setFont(new Font(Config.sysDefaultFontName, Font.PLAIN, Config.frameFontSize));
    JFontButton btnConfig =
        new JFontButton(
            Lizzie.resourceBundle.getString("NewEngineGameDialog.btnConfig")); // ("更多设置");
    btnConfig.setMargin(new Insets(0, 0, 0, 0));
    btnConfig.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            // TBD未完成
            EnginePkConfig engineconfig = new EnginePkConfig(false);
            engineconfig.setVisible(true);
            engineconfig.setAlwaysOnTop(true);
            if (LizzieFrame.toolbar.isGenmoveToolbar) {
              chkDisableWRNInGame.setVisible(false);
              txtresignSettingBlack.setVisible(false);
              txtresignSettingBlack2.setVisible(false);
              txtresignSettingBlackMinMove.setVisible(false);
              resignThresoldHint.setVisible(true);
              lblresignSettingBlack.setVisible(false); // "Genmove模式下,认输由引擎控制,请在引擎参数中设置认输阈值");
              lblresignSettingBlackConsistent.setVisible(false);
              lblresignSettingBlack2.setVisible(false);
              lblresignSettingBlack3.setVisible(false);
              lblresignSettingBlack4.setVisible(false);

              txtresignSettingWhite.setVisible(false);
              txtresignSettingWhite2.setVisible(false);
              txtresignSettingWhiteMinMove.setVisible(false);
              lblresignSettingWhite.setVisible(false);
              lblresignSettingWhiteConsistent.setVisible(false);
              lblresignSettingWhite2.setVisible(false);
              lblresignSettingWhite3.setVisible(false);
              lblresignSettingWhite4.setVisible(false);
              txtBlackAdvanceTime.setEnabled(true);
              txtWhiteAdvanceTime.setEnabled(true);
              chkUseAdvanceTime.setEnabled(true);
              if (Lizzie.config.pkAdvanceTimeSettings) {
                LizzieFrame.toolbar.chkenginePkTime.setEnabled(false);
                LizzieFrame.toolbar.txtenginePkTime.setEnabled(false);
                LizzieFrame.toolbar.txtenginePkTimeWhite.setEnabled(false);
                txtBlackAdvanceTime.setEnabled(true);
                txtWhiteAdvanceTime.setEnabled(true);
              } else {
                LizzieFrame.toolbar.chkenginePkTime.setEnabled(true);
                if (LizzieFrame.toolbar.chkenginePkTime.isSelected()) {
                  LizzieFrame.toolbar.txtenginePkTime.setEnabled(true);
                  LizzieFrame.toolbar.txtenginePkTimeWhite.setEnabled(true);
                }
                txtBlackAdvanceTime.setEnabled(false);
                txtWhiteAdvanceTime.setEnabled(false);
              }
            } else {
              chkDisableWRNInGame.setVisible(true);
              txtresignSettingBlack.setVisible(true);
              txtresignSettingBlack2.setVisible(true);
              txtresignSettingBlackMinMove.setVisible(true);
              resignThresoldHint.setVisible(false);
              lblresignSettingBlack.setVisible(true);
              lblresignSettingBlackConsistent.setVisible(true);
              lblresignSettingBlack2.setVisible(true);
              lblresignSettingBlack3.setVisible(true);
              lblresignSettingBlack4.setVisible(true);

              txtresignSettingWhite.setVisible(true);
              txtresignSettingWhite2.setVisible(true);
              txtresignSettingWhiteMinMove.setVisible(true);
              lblresignSettingWhite.setVisible(true);
              lblresignSettingWhiteConsistent.setVisible(true);
              lblresignSettingWhite2.setVisible(true);
              lblresignSettingWhite3.setVisible(true);
              lblresignSettingWhite4.setVisible(true);
              LizzieFrame.toolbar.chkenginePkTime.setEnabled(true);
              if (LizzieFrame.toolbar.chkenginePkTime.isSelected()) {
                LizzieFrame.toolbar.txtenginePkTime.setEnabled(true);
                LizzieFrame.toolbar.txtenginePkTimeWhite.setEnabled(true);
              }
              txtBlackAdvanceTime.setEnabled(false);
              txtWhiteAdvanceTime.setEnabled(false);
              chkUseAdvanceTime.setEnabled(false);
            }
          }
        });

    JFontLabel lblB =
        new JFontLabel(Lizzie.resourceBundle.getString("NewEngineGameDialog.lblB")); // ("黑方设置");
    lblB.setHorizontalAlignment(JLabel.CENTER);
    JFontLabel lblW =
        new JFontLabel(Lizzie.resourceBundle.getString("NewEngineGameDialog.lblW")); // ("白方设置");
    lblW.setHorizontalAlignment(JLabel.CENTER);
    lblB.setBounds(165, 2, 113, 20);
    lblW.setBounds(288, 2, 113, 20);

    JFontLabel lblengine =
        new JFontLabel(
            Lizzie.resourceBundle.getString("NewEngineGameDialog.lblengine")); // ("选择引擎");
    lblengine.setBounds(5, 30, 120, 20);

    enginePkBlack.setBounds(
        167,
        Lizzie.config.isFrameFontSmall() ? 30 : (Lizzie.config.isFrameFontMiddle() ? 29 : 28),
        113,
        Lizzie.config.isFrameFontSmall()
            ? 20
            : (Lizzie.config.isFrameFontMiddle() ? 22 : 24)); // (134, 30, 113, 20);
    enginePkWhite.setBounds(
        290,
        Lizzie.config.isFrameFontSmall() ? 30 : (Lizzie.config.isFrameFontMiddle() ? 29 : 28),
        113,
        Lizzie.config.isFrameFontSmall()
            ? 20
            : (Lizzie.config.isFrameFontMiddle() ? 22 : 24)); // (255, 30, 113, 20);
    contentPanel.add(lblengine);
    contentPanel.add(lblB);
    contentPanel.add(lblW);
    contentPanel.add(enginePkBlack);
    contentPanel.add(enginePkWhite);
    if (enginePkBlack.getSelectedIndex() == enginePkWhite.getSelectedIndex())
      enginePkWhite.setSelectedIndex(
          enginePkBlack.getSelectedIndex() <= enginePkWhite.getItemCount() - 2
              ? (enginePkWhite.getItemCount() > enginePkBlack.getSelectedIndex() + 1
                  ? enginePkBlack.getSelectedIndex() + 1
                  : enginePkBlack.getSelectedIndex())
              : (enginePkBlack.getSelectedIndex() - 1 >= 0
                  ? enginePkBlack.getSelectedIndex() - 1
                  : enginePkBlack.getSelectedIndex()));

    JFontLabel lblTime =
        new JFontLabel(
            Lizzie.resourceBundle.getString("NewEngineGameDialog.lblTime")); // ("每手时间(秒)");
    lblTime.setBounds(5, 60, 136, 20);
    LizzieFrame.toolbar.chkenginePkTime.setBounds(
        143,
        Lizzie.config.isFrameFontSmall() ? 60 : (Lizzie.config.isFrameFontMiddle() ? 59 : 58),
        20,
        Lizzie.config.isFrameFontSmall() ? 20 : (Lizzie.config.isFrameFontMiddle() ? 22 : 24));
    LizzieFrame.toolbar.txtenginePkTime.setBounds(
        167,
        Lizzie.config.isFrameFontSmall() ? 60 : (Lizzie.config.isFrameFontMiddle() ? 59 : 58),
        113,
        Lizzie.config.isFrameFontSmall()
            ? 20
            : (Lizzie.config.isFrameFontMiddle() ? 22 : 24)); // (134, 60, 113, 20);
    LizzieFrame.toolbar.txtenginePkTimeWhite.setBounds(
        290,
        Lizzie.config.isFrameFontSmall() ? 60 : (Lizzie.config.isFrameFontMiddle() ? 59 : 58),
        113,
        Lizzie.config.isFrameFontSmall()
            ? 20
            : (Lizzie.config.isFrameFontMiddle() ? 22 : 24)); // (255, 60, 113, 20);
    contentPanel.add(lblTime);
    contentPanel.add(LizzieFrame.toolbar.chkenginePkTime);
    contentPanel.add(LizzieFrame.toolbar.txtenginePkTime);
    contentPanel.add(LizzieFrame.toolbar.txtenginePkTimeWhite);
    if (!LizzieFrame.toolbar.chkenginePkTime.isSelected()) {
      LizzieFrame.toolbar.txtenginePkTime.setEnabled(false);
      LizzieFrame.toolbar.txtenginePkTimeWhite.setEnabled(false);
    }

    JFontLabel lblPlayout =
        new JFontLabel(
            Lizzie.resourceBundle.getString("NewEngineGameDialog.lblPlayout")); // ("总计算量");

    lblPlayout.setBounds(5, 120, 136, 20);
    LizzieFrame.toolbar.chkenginePkPlayouts.setBounds(
        143,
        Lizzie.config.isFrameFontSmall() ? 120 : (Lizzie.config.isFrameFontMiddle() ? 119 : 118),
        20,
        Lizzie.config.isFrameFontSmall() ? 20 : (Lizzie.config.isFrameFontMiddle() ? 22 : 24));
    LizzieFrame.toolbar.txtenginePkPlayputs.setBounds(
        167,
        Lizzie.config.isFrameFontSmall() ? 120 : (Lizzie.config.isFrameFontMiddle() ? 119 : 118),
        113,
        Lizzie.config.isFrameFontSmall()
            ? 20
            : (Lizzie.config.isFrameFontMiddle() ? 22 : 24)); // (134, 120, 113, 20);
    LizzieFrame.toolbar.txtenginePkPlayputsWhite.setBounds(
        290,
        Lizzie.config.isFrameFontSmall() ? 120 : (Lizzie.config.isFrameFontMiddle() ? 119 : 118),
        113,
        Lizzie.config.isFrameFontSmall()
            ? 20
            : (Lizzie.config.isFrameFontMiddle() ? 22 : 24)); // (255, 120, 113, 20);
    contentPanel.add(lblPlayout);
    contentPanel.add(LizzieFrame.toolbar.chkenginePkPlayouts);
    contentPanel.add(LizzieFrame.toolbar.txtenginePkPlayputs);
    contentPanel.add(LizzieFrame.toolbar.txtenginePkPlayputsWhite);
    if (!LizzieFrame.toolbar.chkenginePkPlayouts.isSelected()) {
      LizzieFrame.toolbar.txtenginePkPlayputs.setEnabled(false);
      LizzieFrame.toolbar.txtenginePkPlayputsWhite.setEnabled(false);
    }

    JFontLabel lblFirstPlayout =
        new JFontLabel(
            Lizzie.resourceBundle.getString("NewEngineGameDialog.lblFirstPlayout")); // ("首位计算量");
    lblFirstPlayout.setBounds(5, 150, 136, 20);
    LizzieFrame.toolbar.chkenginePkFirstPlayputs.setBounds(143, 150, 20, 20);
    LizzieFrame.toolbar.txtenginePkFirstPlayputs.setBounds(
        167,
        Lizzie.config.isFrameFontSmall() ? 150 : (Lizzie.config.isFrameFontMiddle() ? 149 : 148),
        113,
        Lizzie.config.isFrameFontSmall()
            ? 20
            : (Lizzie.config.isFrameFontMiddle() ? 22 : 24)); // (134, 150, 113, 20);
    LizzieFrame.toolbar.txtenginePkFirstPlayputsWhite.setBounds(
        290,
        Lizzie.config.isFrameFontSmall() ? 150 : (Lizzie.config.isFrameFontMiddle() ? 149 : 148),
        113,
        Lizzie.config.isFrameFontSmall()
            ? 20
            : (Lizzie.config.isFrameFontMiddle() ? 22 : 24)); // (255, 150, 113, 20);

    contentPanel.add(lblFirstPlayout);
    contentPanel.add(LizzieFrame.toolbar.chkenginePkFirstPlayputs);
    contentPanel.add(LizzieFrame.toolbar.txtenginePkFirstPlayputs);
    contentPanel.add(LizzieFrame.toolbar.txtenginePkFirstPlayputsWhite);
    if (!LizzieFrame.toolbar.chkenginePkFirstPlayputs.isSelected()) {
      LizzieFrame.toolbar.txtenginePkFirstPlayputs.setEnabled(false);
      LizzieFrame.toolbar.txtenginePkFirstPlayputsWhite.setEnabled(false);
    }

    JFontLabel komi = new JFontLabel(Lizzie.resourceBundle.getString("NewEngineGameDialog.komi"));
    JFontLabel handicap =
        new JFontLabel(
            Lizzie.resourceBundle.getString("NewEngineGameDialog.handicap")); // ("让子(仅支持19路棋盘)");
    komi.setBounds(5, 240, 45, 20);
    textFieldKomi.setBounds(
        Lizzie.config.isFrameFontSmall() ? 38 : (Lizzie.config.isFrameFontMiddle() ? 42 : 47),
        Lizzie.config.isFrameFontSmall() ? 241 : (Lizzie.config.isFrameFontMiddle() ? 240 : 239),
        Lizzie.config.isFrameFontSmall() ? 30 : (Lizzie.config.isFrameFontMiddle() ? 35 : 40),
        Lizzie.config.isFrameFontSmall() ? 20 : (Lizzie.config.isFrameFontMiddle() ? 22 : 24));
    handicap.setBounds(
        Lizzie.config.isFrameFontSmall() ? 78 : (Lizzie.config.isFrameFontMiddle() ? 88 : 93),
        240,
        320,
        20);
    textFieldHandicap.setBounds(
        Lizzie.config.isFrameFontSmall() ? 194 : (Lizzie.config.isFrameFontMiddle() ? 242 : 284),
        Lizzie.config.isFrameFontSmall() ? 241 : (Lizzie.config.isFrameFontMiddle() ? 240 : 239),
        Lizzie.config.isFrameFontSmall() ? 30 : (Lizzie.config.isFrameFontMiddle() ? 35 : 40),
        Lizzie.config.isFrameFontSmall() ? 20 : (Lizzie.config.isFrameFontMiddle() ? 22 : 24));
    contentPanel.add(komi);
    contentPanel.add(textFieldKomi);
    contentPanel.add(handicap);
    contentPanel.add(textFieldHandicap);

    chkContinuePlay =
        new JFontCheckBox(
            Lizzie.resourceBundle.getString("NewEngineGameDialog.lblContinue")); // ("当前局面续弈");

    chkContinuePlay.setBounds(5, 270, 156, 20);
    // LizzieFrame.toolbar.chkenginePkContinue.setBounds(5, 270, 20, 20);
    contentPanel.add(chkContinuePlay);
    // contentPanel.add(LizzieFrame.toolbar.chkenginePkContinue);
    chkContinuePlay.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            LizzieFrame.toolbar.chkenginePkContinue.setSelected(chkContinuePlay.isSelected());
            if (chkContinuePlay.isSelected()) {
              chkSGFstart.setSelected(false);
              Lizzie.config.chkEngineSgfStart = false;
              btnSGFstart.setEnabled(false);
              cbxRandomSgf.setEnabled(false);
              handicap.setEnabled(false);
              textFieldHandicap.setEnabled(false);
            } else {
              handicap.setEnabled(true);
              textFieldHandicap.setEnabled(true);
              if (chkSGFstart.isSelected()) {
                btnSGFstart.setEnabled(true);
                cbxRandomSgf.setEnabled(true);
              } else {
                btnSGFstart.setEnabled(false);
                cbxRandomSgf.setEnabled(false);
              }
            }
          }
        });

    //    chkBatchGameListener =
    //        new ActionListener() {
    //          public void actionPerformed(ActionEvent e) {
    //            if (LizzieFrame.toolbar.chkenginePkBatch.isSelected()) {
    //              chkSGFstart.setEnabled(true);
    //              lblsgf.setEnabled(true);
    //            } else {
    //              chkSGFstart.setEnabled(false);
    //              lblsgf.setEnabled(false);
    //            }
    //          }
    //        };
    // LizzieFrame.toolbar.chkenginePkContinue.addActionListener(chkEnginePkContinueListener);
    // LizzieFrame.toolbar.chkenginePkBatch.addActionListener(chkBatchGameListener);

    JFontLabel lblBatchGame =
        new JFontLabel(
            Lizzie.resourceBundle.getString("NewEngineGameDialog.lblBatchGame")); // ("多盘");
    lblBatchGame.setBounds(
        Lizzie.config.isFrameFontSmall() ? 279 : (Lizzie.config.isFrameFontMiddle() ? 283 : 288),
        240,
        160,
        20);
    LizzieFrame.toolbar.chkenginePkBatch.setBounds(
        Lizzie.config.isFrameFontSmall() ? 350 : (Lizzie.config.isFrameFontMiddle() ? 375 : 410),
        240,
        20,
        20);
    LizzieFrame.toolbar.txtenginePkBatch.setBounds(
        Lizzie.config.isFrameFontSmall() ? 370 : (Lizzie.config.isFrameFontMiddle() ? 395 : 430),
        Lizzie.config.isFrameFontSmall() ? 241 : (Lizzie.config.isFrameFontMiddle() ? 240 : 239),
        40,
        Lizzie.config.isFrameFontSmall() ? 20 : (Lizzie.config.isFrameFontMiddle() ? 22 : 24));
    btnConfig.setBounds(
        Lizzie.config.isFrameFontSmall() ? 331 : (Lizzie.config.isFrameFontMiddle() ? 365 : 430),
        Lizzie.config.isFrameFontSmall() ? 322 : (Lizzie.config.isFrameFontMiddle() ? 320 : 321),
        Lizzie.config.isFrameFontSmall() ? 80 : (Lizzie.config.isFrameFontMiddle() ? 80 : 100),
        Lizzie.config.isFrameFontSmall() ? 23 : (Lizzie.config.isFrameFontMiddle() ? 25 : 26));
    contentPanel.add(lblBatchGame);
    contentPanel.add(LizzieFrame.toolbar.chkenginePkBatch);
    contentPanel.add(LizzieFrame.toolbar.txtenginePkBatch);
    contentPanel.add(btnConfig);

    textFieldKomi.setEnabled(true);

    //    if (Lizzie.config.showHiddenYzy) {
    //      JFontLabel delay = new JFontLabel("延时(分):"); // $NON-NLS-1$
    //      delay.setBounds(269, 237, 60, 20);
    //      contentPanel.add(delay);
    //
    //      textFieldDelay = new JFormattedTextField(FORMAT_HANDICAP);
    //      textFieldDelay.setBounds(324, 237, 40, 20);
    //      contentPanel.add(textFieldDelay);
    //    }
    dialogPane.add(contentPanel, BorderLayout.CENTER);

    JFontLabel lblAdvanceTime =
        new JFontLabel(
            Lizzie.resourceBundle.getString(
                "NewEngineGameDialog.lblAdvanceTime")); // ("高级时间设置"); // $NON-NLS-1$
    lblAdvanceTime.setBounds(5, 90, 136, 20);
    contentPanel.add(lblAdvanceTime);

    txtBlackAdvanceTime = new JFontTextField();
    txtBlackAdvanceTime.setBounds(
        167,
        Lizzie.config.isFrameFontSmall() ? 90 : (Lizzie.config.isFrameFontMiddle() ? 89 : 88),
        113,
        Lizzie.config.isFrameFontSmall() ? 20 : (Lizzie.config.isFrameFontMiddle() ? 22 : 24));
    contentPanel.add(txtBlackAdvanceTime);
    txtBlackAdvanceTime.setColumns(10);

    txtWhiteAdvanceTime = new JFontTextField();
    txtWhiteAdvanceTime.setBounds(
        290,
        Lizzie.config.isFrameFontSmall() ? 90 : (Lizzie.config.isFrameFontMiddle() ? 89 : 88),
        113,
        Lizzie.config.isFrameFontSmall() ? 20 : (Lizzie.config.isFrameFontMiddle() ? 22 : 24));
    contentPanel.add(txtWhiteAdvanceTime);
    txtWhiteAdvanceTime.setColumns(10);

    txtBlackAdvanceTime.setText(Lizzie.config.advanceBlackTimeTxt);
    txtWhiteAdvanceTime.setText(Lizzie.config.advanceWhiteTimeTxt);
    if (!LizzieFrame.toolbar.isGenmoveToolbar) {
      txtBlackAdvanceTime.setEnabled(false);
      txtWhiteAdvanceTime.setEnabled(false);
    } else {
      txtresignSettingBlack.setVisible(false);
      txtresignSettingBlack2.setVisible(false);
      txtresignSettingBlackMinMove.setVisible(false);
      resignThresoldHint.setVisible(true);
      lblresignSettingBlack.setVisible(false);
      lblresignSettingBlackConsistent.setVisible(false);
      lblresignSettingBlack2.setVisible(false);
      lblresignSettingBlack3.setVisible(false);
      lblresignSettingBlack4.setVisible(false);

      txtresignSettingWhite.setVisible(false);
      txtresignSettingWhite2.setVisible(false);
      txtresignSettingWhiteMinMove.setVisible(false);
      lblresignSettingWhiteConsistent.setVisible(false);
      lblresignSettingWhite.setVisible(false);
      lblresignSettingWhite2.setVisible(false);
      lblresignSettingWhite3.setVisible(false);
      lblresignSettingWhite4.setVisible(false);
    }
    ImageIcon iconSettings = new ImageIcon();
    try {
      iconSettings.setImage(ImageIO.read(getClass().getResourceAsStream("/assets/settings.png")));
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    JFontButton aboutAdvanceTimeSettings = new JFontButton(iconSettings); // $NON-NLS-1$
    aboutAdvanceTimeSettings.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            Utils.showHtmlMessageModal(
                Lizzie.resourceBundle.getString("AdvanceTimeSettings.title"),
                Lizzie.resourceBundle.getString("AdvanceTimeSettings.describe"),
                thisDialog);
          }
        });
    aboutAdvanceTimeSettings.setBounds(125, 91, 18, 18);
    aboutAdvanceTimeSettings.setFocusable(false);
    contentPanel.add(aboutAdvanceTimeSettings);

    chkUseAdvanceTime = new JCheckBox(); // $NON-NLS-1$
    chkUseAdvanceTime.setBounds(143, 88, 20, 20);
    contentPanel.add(chkUseAdvanceTime);
    if (!LizzieFrame.toolbar.isGenmoveToolbar) chkUseAdvanceTime.setEnabled(false);

    chkUseAdvanceTime.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            // TODO Auto-generated method stub
            Lizzie.config.pkAdvanceTimeSettings = chkUseAdvanceTime.isSelected();
            if (Lizzie.config.pkAdvanceTimeSettings) {
              LizzieFrame.toolbar.chkenginePkTime.setEnabled(false);
              LizzieFrame.toolbar.txtenginePkTime.setEnabled(false);
              LizzieFrame.toolbar.txtenginePkTimeWhite.setEnabled(false);
              txtBlackAdvanceTime.setEnabled(true);
              txtWhiteAdvanceTime.setEnabled(true);
            } else {
              LizzieFrame.toolbar.chkenginePkTime.setEnabled(true);
              if (LizzieFrame.toolbar.chkenginePkTime.isSelected()) {
                LizzieFrame.toolbar.txtenginePkTime.setEnabled(true);
                LizzieFrame.toolbar.txtenginePkTimeWhite.setEnabled(true);
              }
              txtBlackAdvanceTime.setEnabled(false);
              txtWhiteAdvanceTime.setEnabled(false);
            }
            Lizzie.config.uiConfig.put(
                "pk-advance-time-settings", Lizzie.config.pkAdvanceTimeSettings);
          }
        });
    chkUseAdvanceTime.setSelected(Lizzie.config.pkAdvanceTimeSettings);

    JCheckBox checkBoxAllowPonder =
        new JFontCheckBox(
            Lizzie.resourceBundle.getString(
                "NewEngineGameDialog.checkBoxAllowPonder")); // ("允许后台计算(同一台电脑对战时不可勾选)");
    checkBoxAllowPonder.setBounds(
        5,
        296,
        Lizzie.config.isFrameFontSmall() ? 317 : (Lizzie.config.isFrameFontMiddle() ? 357 : 419),
        23);
    checkBoxAllowPonder.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            if (checkBoxAllowPonder.isSelected()) Lizzie.config.enginePkPonder = true;
            else Lizzie.config.enginePkPonder = false;
            Lizzie.config.uiConfig.put("engine-pk-ponder", Lizzie.config.enginePkPonder);
          }
        });
    checkBoxAllowPonder.setSelected(Lizzie.config.enginePkPonder);
    contentPanel.add(checkBoxAllowPonder);

    chkDisableWRNInGame =
        new JFontCheckBox(Lizzie.resourceBundle.getString("NewAnaGameDialog.lblDisableWRNInGame"));
    chkDisableWRNInGame.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            Lizzie.config.disableWRNInGame = chkDisableWRNInGame.isSelected();
            Lizzie.config.uiConfig.put("disable-wrn-in-game", Lizzie.config.disableWRNInGame);
          }
        });
    chkDisableWRNInGame.setSelected(Lizzie.config.disableWRNInGame);
    chkDisableWRNInGame.setBounds(
        5,
        322,
        Lizzie.config.isFrameFontSmall() ? 300 : (Lizzie.config.isFrameFontMiddle() ? 350 : 400),
        23);
    chkDisableWRNInGame.setSelected(Lizzie.config.disableWRNInGame);
    contentPanel.add(chkDisableWRNInGame);
    chkDisableWRNInGame.setVisible(!LizzieFrame.toolbar.isGenmoveToolbar);

    chkSGFstart = new JCheckBox();
    chkSGFstart.setBounds(
        Lizzie.config.isFrameFontSmall() ? 257 : (Lizzie.config.isFrameFontMiddle() ? 257 : 277),
        269,
        20,
        23);
    contentPanel.add(chkSGFstart);
    chkSGFstart.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            if (chkSGFstart.isSelected()) {
              chkContinuePlay.setSelected(false);
              Lizzie.config.chkEngineSgfStart = true;
              btnSGFstart.setEnabled(true);
              cbxRandomSgf.setEnabled(true);
              handicap.setEnabled(false);
              textFieldHandicap.setEnabled(false);
            } else {
              Lizzie.config.chkEngineSgfStart = false;
              btnSGFstart.setEnabled(false);
              cbxRandomSgf.setEnabled(false);
              handicap.setEnabled(true);
              textFieldHandicap.setEnabled(true);
            }
          }
        });
    // chkSGFstart.setEnabled(LizzieFrame.toolbar.chkenginePkBatch.isSelected());

    btnSGFstart =
        new JFontButton(
            Lizzie.resourceBundle.getString("NewEngineGameDialog.btnSGFstart")); // ("多选SGF");
    btnSGFstart.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            Lizzie.frame.openSgfStart();
          }
        });
    btnSGFstart.setBounds(
        Lizzie.config.isFrameFontSmall() ? 348 : (Lizzie.config.isFrameFontMiddle() ? 348 : 368),
        Lizzie.config.isFrameFontSmall() ? 270 : (Lizzie.config.isFrameFontMiddle() ? 268 : 267),
        Lizzie.config.isFrameFontSmall() ? 63 : (Lizzie.config.isFrameFontMiddle() ? 73 : 88),
        Lizzie.config.isFrameFontSmall() ? 20 : (Lizzie.config.isFrameFontMiddle() ? 25 : 26));
    contentPanel.add(btnSGFstart);
    btnSGFstart.setMargin(new Insets(0, 0, 0, 0));

    lblsgf.setBounds(
        Lizzie.config.isFrameFontSmall() ? 160 : (Lizzie.config.isFrameFontMiddle() ? 130 : 110),
        270,
        180,
        20);
    contentPanel.add(lblsgf);
    // lblsgf.setEnabled(LizzieFrame.toolbar.chkenginePkBatch.isSelected());

    cbxRandomSgf = new JFontComboBox<String>();
    cbxRandomSgf.addItem(
        Lizzie.resourceBundle.getString("NewEngineGameDialog.cbxRandomSgf1")); // ("顺序");
    cbxRandomSgf.addItem(
        Lizzie.resourceBundle.getString("NewEngineGameDialog.cbxRandomSgf2")); // ("随机");
    cbxRandomSgf.setBounds(
        Lizzie.config.isFrameFontSmall() ? 277 : (Lizzie.config.isFrameFontMiddle() ? 277 : 297),
        Lizzie.config.isFrameFontSmall() ? 270 : (Lizzie.config.isFrameFontMiddle() ? 269 : 268),
        69,
        Lizzie.config.isFrameFontSmall() ? 20 : (Lizzie.config.isFrameFontMiddle() ? 23 : 24));
    contentPanel.add(cbxRandomSgf);
    cbxRandomSgf.addItemListener(
        new ItemListener() {
          public void itemStateChanged(final ItemEvent e) {
            if (cbxRandomSgf.getSelectedIndex() == 0) {
              Lizzie.config.engineSgfStartRandom = false;
            } else {
              Lizzie.config.engineSgfStartRandom = true;
            }
            Lizzie.config.uiConfig.put("engine-sgf-random", Lizzie.config.engineSgfStartRandom);
          }
        });
    cbxRandomSgf.setSelectedIndex(Lizzie.config.engineSgfStartRandom ? 1 : 0);
    if (Lizzie.config.pkAdvanceTimeSettings && LizzieFrame.toolbar.isGenmoveToolbar) {
      LizzieFrame.toolbar.chkenginePkTime.setEnabled(false);
      txtBlackAdvanceTime.setEnabled(true);
      txtWhiteAdvanceTime.setEnabled(true);
    } else {
      LizzieFrame.toolbar.chkenginePkTime.setEnabled(true);
      if (LizzieFrame.toolbar.chkenginePkTime.isSelected()) {
        LizzieFrame.toolbar.txtenginePkTime.setEnabled(true);
        LizzieFrame.toolbar.txtenginePkTimeWhite.setEnabled(true);
      }
      txtBlackAdvanceTime.setEnabled(false);
      txtWhiteAdvanceTime.setEnabled(false);
    }
    chkSGFstart.setSelected(Lizzie.config.chkEngineSgfStart);
    if (LizzieFrame.toolbar.chkenginePkContinue.isSelected()) {
      chkContinuePlay.setSelected(true);
      chkSGFstart.setSelected(false);
      btnSGFstart.setEnabled(false);
      cbxRandomSgf.setEnabled(false);
      textFieldHandicap.setEnabled(false);
    } else {
      chkContinuePlay.setSelected(false);
      textFieldHandicap.setEnabled(true);
      if (chkSGFstart.isSelected()) {
        btnSGFstart.setEnabled(true);
        cbxRandomSgf.setEnabled(true);
      } else {
        btnSGFstart.setEnabled(false);
        cbxRandomSgf.setEnabled(false);
      }
    }

    addWindowListener(
        new WindowAdapter() {
          public void windowClosing(WindowEvent e) {
            //  LizzieFrame.toolbar.chkenginePkContinue.removeActionListener(
            //      chkEnginePkContinueListener);
            //  LizzieFrame.toolbar.chkenginePkBatch.removeActionListener(chkBatchGameListener);
            resetFont();
          }
        });
    LizzieFrame.toolbar.txtenginePkPlayputs.setFont(
        new Font(Config.sysDefaultFontName, Font.PLAIN, Config.frameFontSize));
    LizzieFrame.toolbar.txtenginePkPlayputsWhite.setFont(
        new Font(Config.sysDefaultFontName, Font.PLAIN, Config.frameFontSize));
    LizzieFrame.toolbar.txtenginePkFirstPlayputsWhite.setFont(
        new Font(Config.sysDefaultFontName, Font.PLAIN, Config.frameFontSize));
    LizzieFrame.toolbar.txtenginePkFirstPlayputs.setFont(
        new Font(Config.sysDefaultFontName, Font.PLAIN, Config.frameFontSize));
    LizzieFrame.toolbar.txtenginePkBatch.setFont(
        new Font(Config.sysDefaultFontName, Font.PLAIN, Config.frameFontSize));
    LizzieFrame.toolbar.txtenginePkTime.setFont(
        new Font(Config.sysDefaultFontName, Font.PLAIN, Config.frameFontSize));
    LizzieFrame.toolbar.txtenginePkTimeWhite.setFont(
        new Font(Config.sysDefaultFontName, Font.PLAIN, Config.frameFontSize));
  }

  //  private void togglePlayerIsBlack() {
  //    JFontTextField humanTextField = playerIsBlack() ? textFieldBlack : textFieldWhite;
  //    JFontTextField computerTextField = playerIsBlack() ? textFieldWhite : textFieldBlack;
  //
  //    humanTextField.setEnabled(true);
  //    humanTextField.setText(GameInfo.DEFAULT_NAME_HUMAN_PLAYER);
  //    computerTextField.setEnabled(false);
  //    computerTextField.setText(GameInfo.DEFAULT_NAME_CPU_PLAYER);
  //  }

  private void modifyHandicap() {
    try {
      int handicap = FORMAT_HANDICAP.parse(textFieldHandicap.getText()).intValue();
      if (handicap < 0) throw new IllegalArgumentException();

      // textFieldKomi.setText(FORMAT_KOMI.format(GameInfo.DEFAULT_KOMI));
    } catch (ParseException | RuntimeException e) {
      // do not correct user mistakes
    }
  }

  private void initButtonBar() {
    buttonBar.setBorder(new EmptyBorder(7, 0, 0, 0));
    buttonBar.setLayout(new GridBagLayout());
    ((GridBagLayout) buttonBar.getLayout()).columnWidths = new int[] {0, 70};
    ((GridBagLayout) buttonBar.getLayout()).columnWeights = new double[] {1.0, 0.0};

    // ---- okButton ----
    okButton.setText(Lizzie.resourceBundle.getString("NewEngineGameDialog.okButton")); // ("确定");
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
      // validate data
      if (Lizzie.config.chkEngineSgfStart) {
        if (Lizzie.frame.enginePKSgfString == null || Lizzie.frame.enginePKSgfString.isEmpty()) {
          Utils.showMsg(Lizzie.resourceBundle.getString("NewEngineGameDialog.message"));
          return;
        } else if (Lizzie.frame.enginePKSgfString.size() > 1
            && !LizzieFrame.toolbar.chkenginePkBatch.isSelected())
          Utils.showMsg(Lizzie.resourceBundle.getString("NewEngineGameDialog.multiSgfNotBatch"));
      }
      //
      // LizzieFrame.toolbar.chkenginePkContinue.removeActionListener(chkEnginePkContinueListener);
      //  LizzieFrame.toolbar.chkenginePkBatch.removeActionListener(chkBatchGameListener);
      String playerBlack =
          Lizzie.engineManager.engineList.get(LizzieFrame.toolbar.engineBlackToolbar)
              .currentEnginename;
      String playerWhite =
          Lizzie.engineManager.engineList.get(LizzieFrame.toolbar.engineWhiteToolbar)
              .currentEnginename;
      double komi = 7.5;
      try {
        komi = FORMAT_KOMI.parse(textFieldKomi.getText()).doubleValue();
      } catch (NumberFormatException err) {
      }
      int handicap =
          !textFieldHandicap.isEnabled()
              ? 0
              : FORMAT_HANDICAP.parse(textFieldHandicap.getText()).intValue();
      try {
        Lizzie.config.firstEngineResignMoveCounts =
            Integer.parseInt(txtresignSettingBlack.getText());
      } catch (NumberFormatException err) {
      }
      try {
        Lizzie.config.firstEngineResignWinrate =
            Double.parseDouble(txtresignSettingBlack2.getText());
      } catch (NumberFormatException err) {
      }
      try {
        Lizzie.config.firstEngineMinMove = Integer.parseInt(txtresignSettingBlackMinMove.getText());
      } catch (NumberFormatException err) {
      }

      try {
        Lizzie.config.secondEngineResignMoveCounts =
            Integer.parseInt(txtresignSettingWhite.getText());
      } catch (NumberFormatException err) {
      }
      try {
        Lizzie.config.secondEngineResignWinrate =
            Double.parseDouble(txtresignSettingWhite2.getText());
      } catch (NumberFormatException err) {
      }
      try {
        Lizzie.config.secondEngineMinMove =
            Integer.parseInt(txtresignSettingWhiteMinMove.getText());
      } catch (NumberFormatException err) {
      }
      Lizzie.config.newEngineGameHandicap = handicap;
      Lizzie.config.newEngineGameKomi = komi;
      Lizzie.config.uiConfig.put("new-engine-game-komi", Lizzie.config.newEngineGameKomi);
      Lizzie.config.uiConfig.put("new-engine-game-handicap", Lizzie.config.newEngineGameHandicap);

      Lizzie.config.uiConfig.put(
          "first-engine-resign-move-counts", Lizzie.config.firstEngineResignMoveCounts);
      Lizzie.config.uiConfig.put(
          "first-engine-resign-winrate", Lizzie.config.firstEngineResignWinrate);
      Lizzie.config.uiConfig.put("first-engine-min-move", Lizzie.config.firstEngineMinMove);

      Lizzie.config.uiConfig.put(
          "second-engine-resign-move-counts", Lizzie.config.secondEngineResignMoveCounts);
      Lizzie.config.uiConfig.put(
          "second-engine-resign-winrate", Lizzie.config.secondEngineResignWinrate);
      Lizzie.config.uiConfig.put("second-engine-min-move", Lizzie.config.secondEngineMinMove);

      if (Lizzie.config.pkAdvanceTimeSettings) {
        Lizzie.config.advanceBlackTimeTxt = txtBlackAdvanceTime.getText().trim();
        Lizzie.config.advanceWhiteTimeTxt = txtWhiteAdvanceTime.getText().trim();
        Lizzie.config.uiConfig.put("advance-black-time-txt", txtBlackAdvanceTime.getText().trim());
        Lizzie.config.uiConfig.put("advance-white-time-txt", txtWhiteAdvanceTime.getText().trim());
      }
      // apply new values
      gameInfo.setPlayerBlack(playerBlack);
      gameInfo.setPlayerWhite(playerWhite);
      gameInfo.setKomi(komi);
      // gameInfo.changeKomi();
      gameInfo.setHandicap(handicap);

      // close window
      cancelled = false;

      resetFont();

      if (handicap >= 2 && Board.boardWidth == 19 && Board.boardHeight == 19) {
        Lizzie.board.getHistory().clear();
        placeHandicap(handicap);
        LizzieFrame.toolbar.isEngineGameHandicapToolbar = true;
      } else LizzieFrame.toolbar.isEngineGameHandicapToolbar = false;

      Lizzie.board.getHistory().setGameInfo(gameInfo);
      Lizzie.leelaz.komi(gameInfo.getKomi());
      // Lizzie.frame.komi = gameInfo.getKomi() );

      LizzieFrame.toolbar.chkenginePk.setSelected(true);
      if (LizzieFrame.toolbar.startEngineGame()) setVisible(false);
    } catch (ParseException e) {
      // hide input mistakes.
    }
  }

  private void resetFont() {
    LizzieFrame.toolbar.enginePkBlack.setFont(new Font(Config.sysDefaultFontName, Font.PLAIN, 12));
    LizzieFrame.toolbar.enginePkWhite.setFont(new Font(Config.sysDefaultFontName, Font.PLAIN, 12));
    LizzieFrame.toolbar.txtenginePkPlayputs.setFont(
        new Font(Config.sysDefaultFontName, Font.PLAIN, 12));
    LizzieFrame.toolbar.txtenginePkPlayputsWhite.setFont(
        new Font(Config.sysDefaultFontName, Font.PLAIN, 12));
    LizzieFrame.toolbar.txtenginePkFirstPlayputsWhite.setFont(
        new Font(Config.sysDefaultFontName, Font.PLAIN, 12));
    LizzieFrame.toolbar.txtenginePkFirstPlayputs.setFont(
        new Font(Config.sysDefaultFontName, Font.PLAIN, 12));
    LizzieFrame.toolbar.txtenginePkBatch.setFont(
        new Font(Config.sysDefaultFontName, Font.PLAIN, 12));
    LizzieFrame.toolbar.txtenginePkTime.setFont(
        new Font(Config.sysDefaultFontName, Font.PLAIN, 12));
    LizzieFrame.toolbar.txtenginePkTimeWhite.setFont(
        new Font(Config.sysDefaultFontName, Font.PLAIN, 12));
  }

  private void placeHandicap(int handicap) {
    // TODO Auto-generated method stub
    switch (handicap) {
      case 2:
        Lizzie.board.getHistory().place(3, 15, Stone.BLACK);
        Lizzie.board.getHistory().place(15, 3, Stone.BLACK);
        break;
      case 3:
        Lizzie.board.getHistory().place(3, 3, Stone.BLACK);
        Lizzie.board.getHistory().place(15, 3, Stone.BLACK);
        Lizzie.board.getHistory().place(3, 15, Stone.BLACK);
        break;
      case 4:
        Lizzie.board.getHistory().place(3, 3, Stone.BLACK);
        Lizzie.board.getHistory().place(3, 15, Stone.BLACK);
        Lizzie.board.getHistory().place(15, 3, Stone.BLACK);
        Lizzie.board.getHistory().place(15, 15, Stone.BLACK);
        break;
      case 5:
        Lizzie.board.getHistory().place(3, 3, Stone.BLACK);
        Lizzie.board.getHistory().place(3, 15, Stone.BLACK);
        Lizzie.board.getHistory().place(15, 3, Stone.BLACK);
        Lizzie.board.getHistory().place(15, 15, Stone.BLACK);
        Lizzie.board.getHistory().place(9, 9, Stone.BLACK);
        break;
      case 6:
        Lizzie.board.getHistory().place(3, 3, Stone.BLACK);
        Lizzie.board.getHistory().place(3, 15, Stone.BLACK);
        Lizzie.board.getHistory().place(15, 3, Stone.BLACK);
        Lizzie.board.getHistory().place(15, 15, Stone.BLACK);
        Lizzie.board.getHistory().place(3, 9, Stone.BLACK);
        Lizzie.board.getHistory().place(15, 9, Stone.BLACK);
        break;
      case 7:
        Lizzie.board.getHistory().place(3, 3, Stone.BLACK);
        Lizzie.board.getHistory().place(3, 15, Stone.BLACK);
        Lizzie.board.getHistory().place(15, 3, Stone.BLACK);
        Lizzie.board.getHistory().place(15, 15, Stone.BLACK);
        Lizzie.board.getHistory().place(15, 9, Stone.BLACK);
        Lizzie.board.getHistory().place(3, 9, Stone.BLACK);
        Lizzie.board.getHistory().place(9, 9, Stone.BLACK);
        break;
      case 8:
        Lizzie.board.getHistory().place(3, 3, Stone.BLACK);
        Lizzie.board.getHistory().place(3, 15, Stone.BLACK);
        Lizzie.board.getHistory().place(15, 3, Stone.BLACK);
        Lizzie.board.getHistory().place(15, 15, Stone.BLACK);
        Lizzie.board.getHistory().place(9, 3, Stone.BLACK);
        Lizzie.board.getHistory().place(9, 15, Stone.BLACK);
        Lizzie.board.getHistory().place(3, 9, Stone.BLACK);
        Lizzie.board.getHistory().place(15, 9, Stone.BLACK);
        break;
      case 9:
        Lizzie.board.getHistory().place(3, 3, Stone.BLACK);
        Lizzie.board.getHistory().place(3, 15, Stone.BLACK);
        Lizzie.board.getHistory().place(15, 3, Stone.BLACK);
        Lizzie.board.getHistory().place(15, 15, Stone.BLACK);
        Lizzie.board.getHistory().place(9, 3, Stone.BLACK);
        Lizzie.board.getHistory().place(9, 15, Stone.BLACK);
        Lizzie.board.getHistory().place(3, 9, Stone.BLACK);
        Lizzie.board.getHistory().place(15, 9, Stone.BLACK);
        Lizzie.board.getHistory().place(9, 9, Stone.BLACK);
        break;
    }
  }

  public void setGameInfo(GameInfo gameInfo) {
    this.gameInfo = gameInfo;

    textFieldHandicap.setText(String.valueOf(Lizzie.config.newEngineGameHandicap));
    textFieldKomi.setText(String.valueOf(Lizzie.config.newEngineGameKomi));

    // update player names
    // togglePlayerIsBlack();
  }

  //  public boolean playerIsBlack() {
  //    return checkBoxPlayerIsBlack.isSelected();
  //  }

  public boolean isCancelled() {
    return cancelled;
  }
}
