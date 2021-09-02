package featurecat.lizzie.gui;

import featurecat.lizzie.Lizzie;
import featurecat.lizzie.util.Utils;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ResourceBundle;
import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

public class AutoPlay extends JFrame {

  private JPanel contentPane;
  private JTextField txtAutoPlayMain;
  private JTextField txtAutoPlaySub;
  private final ResourceBundle resourceBundle = Lizzie.resourceBundle;
  private JTextField txtAutoPlayBranch;
  private JTextField txtDisplayEntireVariationFirst;
  /** Create the frame. */
  public AutoPlay() {
    setBounds(100, 100, 491, 233);
    Lizzie.setFrameSize(
        this,
        Lizzie.config.isFrameFontSmall() ? 270 : Lizzie.config.isFrameFontMiddle() ? 280 : 330,
        175);
    setResizable(false);
    if (Lizzie.frame != null) setAlwaysOnTop(Lizzie.frame.isAlwaysOnTop());
    setTitle(resourceBundle.getString("AutpPlay.title")); // "设置自动播放");
    setLocationRelativeTo(Lizzie.frame);
    contentPane = new JPanel();
    contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
    setContentPane(contentPane);
    contentPane.setLayout(null);
    try {
      this.setIconImage(ImageIO.read(AnalysisFrame.class.getResourceAsStream("/assets/logo.png")));
    } catch (IOException e) {
      e.printStackTrace();
    }

    JCheckBox chkAutoPlayMainboard =
        new JFontCheckBox(
            resourceBundle.getString("AutpPlay.chkAutoPlayMainbord")); // "自动播放(大棋盘)(秒)");
    chkAutoPlayMainboard.setBounds(6, 6, 168, 24);
    contentPane.add(chkAutoPlayMainboard);
    chkAutoPlayMainboard.setFocusable(false);

    JCheckBox chkAutoPlaySubboard =
        new JFontCheckBox(
            resourceBundle.getString("AutpPlay.chkAutoPlaySubbord")); // "自动播放(小棋盘)(毫秒)");
    chkAutoPlaySubboard.setBounds(6, 33, 168, 24);
    contentPane.add(chkAutoPlaySubboard);
    chkAutoPlaySubboard.setFocusable(false);

    txtAutoPlayMain = new JFontTextField();
    txtAutoPlayMain.setBounds(180, 6, 66, 24);
    contentPane.add(txtAutoPlayMain);
    txtAutoPlayMain.setColumns(10);

    txtAutoPlaySub = new JFontTextField();
    txtAutoPlaySub.setBounds(180, 33, 66, 24);
    contentPane.add(txtAutoPlaySub);
    txtAutoPlaySub.setColumns(10);

    if (LizzieFrame.toolbar.chkAutoMain.isSelected()) {
      chkAutoPlayMainboard.setSelected(true);
    }
    txtAutoPlayMain.setText(LizzieFrame.toolbar.txtAutoMain.getText());
    if (LizzieFrame.toolbar.chkAutoSub.isSelected()) {
      chkAutoPlaySubboard.setSelected(true);
    }
    txtAutoPlaySub.setText(LizzieFrame.toolbar.txtAutoSub.getText());

    JCheckBox chkDisplayEntireVariationFirst =
        new JFontCheckBox(resourceBundle.getString("AutpPlay.chbDisplayEntireVariationFirst"));
    chkDisplayEntireVariationFirst.setBounds(
        17,
        87,
        Lizzie.config.isFrameFontSmall() ? 185 : Lizzie.config.isFrameFontMiddle() ? 195 : 230,
        23);
    contentPane.add(chkDisplayEntireVariationFirst);
    chkDisplayEntireVariationFirst.setEnabled(Lizzie.config.autoReplayBranch);
    chkDisplayEntireVariationFirst.setSelected(
        Lizzie.config.autoReplayDisplayEntireVariationsFirst);

    txtDisplayEntireVariationFirst = new JFontTextField();
    txtDisplayEntireVariationFirst.setBounds(
        Lizzie.config.isFrameFontSmall() ? 203 : Lizzie.config.isFrameFontMiddle() ? 218 : 268,
        87,
        43,
        24);
    contentPane.add(txtDisplayEntireVariationFirst);
    txtDisplayEntireVariationFirst.setEnabled(Lizzie.config.autoReplayBranch);
    txtDisplayEntireVariationFirst.setText(Lizzie.config.displayEntireVariationsFirstSeconds + "");

    JCheckBox chkAutoPlayBranch =
        new JFontCheckBox(resourceBundle.getString("AutpPlay.chkAutoPlayBranch"));
    chkAutoPlayBranch.setBounds(6, 60, 168, 24);
    contentPane.add(chkAutoPlayBranch);
    chkAutoPlayBranch.setSelected(Lizzie.config.autoReplayBranch);
    chkAutoPlayBranch.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            chkDisplayEntireVariationFirst.setEnabled(chkAutoPlayBranch.isSelected());
            txtDisplayEntireVariationFirst.setEnabled(chkAutoPlayBranch.isSelected());
          }
        });

    txtAutoPlayBranch = new JTextField();
    txtAutoPlayBranch.setBounds(180, 60, 66, 24);
    contentPane.add(txtAutoPlayBranch);
    txtAutoPlayBranch.setText((int) (Lizzie.config.replayBranchIntervalSeconds * 1000) + "");

    JButton okButton = new JFontButton(resourceBundle.getString("AutpPlay.okButton"));
    okButton.setMargin(new Insets(0, 0, 0, 0));
    okButton.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            LizzieFrame.toolbar.txtAutoMain.setText(txtAutoPlayMain.getText());
            LizzieFrame.toolbar.txtAutoSub.setText(txtAutoPlaySub.getText());
            if (chkAutoPlayMainboard.isSelected() || chkAutoPlaySubboard.isSelected()) {
              if (chkAutoPlayMainboard.isSelected()) {
                LizzieFrame.toolbar.chkAutoMain.setSelected(true);
                LizzieFrame.toolbar.autoPlayMain();
                // }
              } else {
                LizzieFrame.toolbar.chkAutoMain.setSelected(false);
              }
              if (chkAutoPlaySubboard.isSelected()) {
                LizzieFrame.toolbar.chkAutoSub.setSelected(true);
                LizzieFrame.toolbar.autoPlaySub();
                //  }
              } else {
                LizzieFrame.toolbar.chkAutoSub.setSelected(false);
                LizzieFrame.toolbar.autoPlaySub();
              }

            } else {
              LizzieFrame.toolbar.chkAutoMain.setSelected(false);
              LizzieFrame.toolbar.chkAutoSub.setSelected(false);
              LizzieFrame.toolbar.autoPlaySub();
            }
            Lizzie.config.autoReplayDisplayEntireVariationsFirst =
                chkDisplayEntireVariationFirst.isSelected();
            Lizzie.config.displayEntireVariationsFirstSeconds =
                Utils.parseTextToDouble(
                    txtDisplayEntireVariationFirst,
                    Lizzie.config.displayEntireVariationsFirstSeconds);
            Lizzie.config.uiConfig.put(
                "auto-replay-display-entire-variations-first",
                Lizzie.config.autoReplayDisplayEntireVariationsFirst);
            Lizzie.config.uiConfig.put(
                "display-entire-variations-first-seconds",
                Lizzie.config.displayEntireVariationsFirstSeconds);
            Lizzie.config.replayBranchIntervalSeconds =
                Utils.parseTextToDouble(
                        txtAutoPlayBranch, Lizzie.config.replayBranchIntervalSeconds * 1000)
                    / 1000;
            Lizzie.config.uiConfig.put(
                "replay-branch-interval-seconds", Lizzie.config.replayBranchIntervalSeconds);
            Lizzie.config.autoReplayBranch = chkAutoPlayBranch.isSelected();
            if (Lizzie.config.autoReplayBranch) {
              Lizzie.frame.autoReplayBranch();
            }
            Lizzie.config.uiConfig.put("auto-replay-branch", Lizzie.config.autoReplayBranch);
            setVisible(false);
          }
        });
    okButton.setBounds(
        Lizzie.config.isFrameFontSmall() ? 84 : Lizzie.config.isFrameFontMiddle() ? 92 : 110,
        117,
        93,
        25);
    contentPane.add(okButton);
  }
}
