package featurecat.lizzie.gui;

import featurecat.lizzie.Lizzie;
import featurecat.lizzie.rules.Tsumego;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

public class TsumeGoFrame extends JDialog {
  public TsumeGoFrame(Window owner) {
    super(owner);
    setTitle("死活题");
    setResizable(false);
    JPanel contentPane = new JPanel();
    contentPane.setBorder(new EmptyBorder(6, 15, 12, 15));
    JPanel buttonPane = new JPanel();
    buttonPane.setBorder(new EmptyBorder(0, 15, 5, 15));
    getContentPane().add(contentPane, BorderLayout.CENTER);
    getContentPane().add(buttonPane, BorderLayout.SOUTH);
    buttonPane.setLayout(new BorderLayout(0, 0));
    JFontButton btnCapture = new JFontButton("屏幕抓取");
    btnCapture.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            CaptureTsumeGoFrame captureTsumeGoFrame = new CaptureTsumeGoFrame();
            captureTsumeGoFrame.setVisible(true);
            dispose();
          }
        });
    buttonPane.add(btnCapture, BorderLayout.WEST);

    JPanel panel = new JPanel();
    panel.setBorder(new EmptyBorder(0, 0, 0, 0));
    buttonPane.add(panel, BorderLayout.EAST);

    JFontButton btnConfirm = new JFontButton("确定");
    panel.add(btnConfirm);

    JFontButton btnCancel = new JFontButton("取消");
    btnCancel.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            setVisible(false);
            dispose();
          }
        });
    panel.add(btnCancel);
    contentPane.setLayout(new GridLayout(3, 5, 8, 15));

    JFontLabel lblToPlay = new JFontLabel("先手方");
    contentPane.add(lblToPlay);

    JFontRadioButton rdoExtend = new JFontRadioButton("New radio button");
    rdoExtend.setText("继承");
    contentPane.add(rdoExtend);

    JFontRadioButton rdoBlackToPlay = new JFontRadioButton("New radio button");
    rdoBlackToPlay.setText("黑先");
    contentPane.add(rdoBlackToPlay);

    JFontRadioButton rdoWhiteToPlay = new JFontRadioButton("New radio button");
    rdoWhiteToPlay.setText("白先");
    contentPane.add(rdoWhiteToPlay);

    Box box1 = Box.createVerticalBox();
    contentPane.add(box1);

    JFontLabel lblAttacker = new JFontLabel("进攻方");
    contentPane.add(lblAttacker);

    JFontRadioButton rdoAutoDetect = new JFontRadioButton("New radio button");
    rdoAutoDetect.setText("自动检测");
    contentPane.add(rdoAutoDetect);

    JFontRadioButton rdoBlackAttack = new JFontRadioButton("New radio button");
    rdoBlackAttack.setText("黑方");
    contentPane.add(rdoBlackAttack);

    JFontRadioButton rdoWhiteAttack = new JFontRadioButton("New radio button");
    rdoWhiteAttack.setText("白方");
    contentPane.add(rdoWhiteAttack);

    Box box2 = Box.createVerticalBox();
    contentPane.add(box2);

    JFontLabel lblKoThreat = new JFontLabel("New label");
    lblKoThreat.setText("添加劫财");
    contentPane.add(lblKoThreat);

    JFontRadioButton rdoKoBoth = new JFontRadioButton("New radio button");
    rdoKoBoth.setText("双方");
    contentPane.add(rdoKoBoth);

    JFontRadioButton rdoKoAttacker = new JFontRadioButton("New radio button");
    rdoKoAttacker.setText("进攻方");
    contentPane.add(rdoKoAttacker);

    JFontRadioButton rdoKoDefender = new JFontRadioButton("New radio button");
    rdoKoDefender.setText("防守方");
    contentPane.add(rdoKoDefender);

    JFontRadioButton rdoKoNone = new JFontRadioButton("New radio button");
    rdoKoNone.setText("无");
    contentPane.add(rdoKoNone);

    ButtonGroup group1 = new ButtonGroup();
    group1.add(rdoExtend);
    group1.add(rdoBlackToPlay);
    group1.add(rdoWhiteToPlay);

    ButtonGroup group2 = new ButtonGroup();
    group2.add(rdoAutoDetect);
    group2.add(rdoBlackAttack);
    group2.add(rdoWhiteAttack);

    ButtonGroup group3 = new ButtonGroup();
    group3.add(rdoKoBoth);
    group3.add(rdoKoAttacker);
    group3.add(rdoKoDefender);
    group3.add(rdoKoNone);

    if (Lizzie.config.tesumeGoToPlay == 1) rdoExtend.setSelected(true);
    else if (Lizzie.config.tesumeGoToPlay == 2) rdoBlackToPlay.setSelected(true);
    else if (Lizzie.config.tesumeGoToPlay == 3) rdoWhiteToPlay.setSelected(true);

    if (Lizzie.config.tesumeGoAttaker == 1) rdoAutoDetect.setSelected(true);
    else if (Lizzie.config.tesumeGoAttaker == 2) rdoBlackAttack.setSelected(true);
    else if (Lizzie.config.tesumeGoAttaker == 3) rdoWhiteAttack.setSelected(true);

    if (Lizzie.config.tesumeGoKoThreat == 1) rdoKoBoth.setSelected(true);
    else if (Lizzie.config.tesumeGoKoThreat == 2) rdoKoAttacker.setSelected(true);
    else if (Lizzie.config.tesumeGoKoThreat == 3) rdoKoDefender.setSelected(true);
    else if (Lizzie.config.tesumeGoKoThreat == 4) rdoKoNone.setSelected(true);

    btnConfirm.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            if (rdoExtend.isSelected()) Lizzie.config.tesumeGoToPlay = 1;
            else if (rdoBlackToPlay.isSelected()) Lizzie.config.tesumeGoToPlay = 2;
            else if (rdoWhiteToPlay.isSelected()) Lizzie.config.tesumeGoToPlay = 3;

            if (rdoAutoDetect.isSelected()) Lizzie.config.tesumeGoAttaker = 1;
            else if (rdoBlackAttack.isSelected()) Lizzie.config.tesumeGoAttaker = 2;
            else if (rdoWhiteAttack.isSelected()) Lizzie.config.tesumeGoAttaker = 3;

            if (rdoKoBoth.isSelected()) Lizzie.config.tesumeGoKoThreat = 1;
            else if (rdoKoAttacker.isSelected()) Lizzie.config.tesumeGoKoThreat = 2;
            else if (rdoKoDefender.isSelected()) Lizzie.config.tesumeGoKoThreat = 3;
            else if (rdoKoNone.isSelected()) Lizzie.config.tesumeGoKoThreat = 4;

            Lizzie.config.uiConfig.put("tesume-go-to-play", Lizzie.config.tesumeGoToPlay);
            Lizzie.config.uiConfig.put("tesume-go-attaker", Lizzie.config.tesumeGoAttaker);
            Lizzie.config.uiConfig.put("tesume-go-ko-threat", Lizzie.config.tesumeGoKoThreat);

            boolean forceSide = false;
            boolean forceBlack = false;

            boolean forceToPlay = false;
            boolean blackToPlay = false;

            boolean addKoThreatAttacker = false;
            boolean addKoThreatDefender = false;

            if (Lizzie.config.tesumeGoAttaker == 2) {
              forceSide = true;
              forceBlack = true;
            } else if (Lizzie.config.tesumeGoAttaker == 3) {
              forceSide = true;
              forceBlack = false;
            }

            if (Lizzie.config.tesumeGoToPlay == 2) {
              forceToPlay = true;
              blackToPlay = true;
            } else if (Lizzie.config.tesumeGoToPlay == 3) {
              forceToPlay = true;
              blackToPlay = false;
            }

            if (Lizzie.config.tesumeGoKoThreat == 1) {
              addKoThreatAttacker = true;
              addKoThreatDefender = true;
            } else if (Lizzie.config.tesumeGoKoThreat == 2) {
              addKoThreatAttacker = true;
              addKoThreatDefender = false;
            } else if (Lizzie.config.tesumeGoKoThreat == 3) {
              addKoThreatAttacker = false;
              addKoThreatDefender = true;
            } else if (Lizzie.config.tesumeGoKoThreat == 4) {
              addKoThreatAttacker = false;
              addKoThreatDefender = false;
            }

            Tsumego tsumego = new Tsumego();

            tsumego.getCoverSideAndIndex(forceSide, forceBlack);
            tsumego.buildCoverWall(
                addKoThreatDefender, addKoThreatAttacker, forceToPlay, blackToPlay);
            LizzieFrame.menu.clearInsert();
            Lizzie.frame.refresh();
            setVisible(false);
            dispose();
          }
        });

    pack();
    setLocationRelativeTo(owner);
  }
}
