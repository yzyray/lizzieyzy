package featurecat.lizzie.gui;

import featurecat.lizzie.Lizzie;
import featurecat.lizzie.rules.Tsumego;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

public class TsumeGoFrame extends JDialog {
  private JTextField txtWallDistance;

  public TsumeGoFrame(Window owner) {
    super(owner);
    setTitle(Lizzie.resourceBundle.getString("TsumeGoFrame.title"));
    setResizable(false);
    JPanel contentPane = new JPanel();
    contentPane.setBorder(new EmptyBorder(6, 15, 12, 15));
    JPanel buttonPane = new JPanel();
    buttonPane.setBorder(new EmptyBorder(0, 15, 5, 15));
    getContentPane().add(contentPane, BorderLayout.CENTER);
    getContentPane().add(buttonPane, BorderLayout.SOUTH);
    buttonPane.setLayout(new BorderLayout(0, 0));
    JFontButton btnCapture =
        new JFontButton(Lizzie.resourceBundle.getString("TsumeGoFrame.btnCapture"));
    btnCapture.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            Lizzie.frame.openCaptureTsumego();
            dispose();
          }
        });
    buttonPane.add(btnCapture, BorderLayout.WEST);
    btnCapture.setFocusable(false);

    JPanel panel = new JPanel();
    panel.setBorder(new EmptyBorder(0, 0, 0, 0));
    buttonPane.add(panel, BorderLayout.EAST);

    JFontButton btnConfirm =
        new JFontButton(Lizzie.resourceBundle.getString("TsumeGoFrame.btnConfirm"));
    panel.add(btnConfirm);

    JFontButton btnCancel =
        new JFontButton(Lizzie.resourceBundle.getString("TsumeGoFrame.btnCancel"));
    btnCancel.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            setVisible(false);
            dispose();
          }
        });
    panel.add(btnCancel);
    contentPane.setLayout(new GridLayout(4, 5, 8, 15));

    JFontLabel lblToPlay =
        new JFontLabel(Lizzie.resourceBundle.getString("TsumeGoFrame.lblToPlay"));
    contentPane.add(lblToPlay);

    JFontRadioButton rdoKeepOn =
        new JFontRadioButton(Lizzie.resourceBundle.getString("TsumeGoFrame.rdoKeepOn"));
    rdoKeepOn.setFocusable(false);
    contentPane.add(rdoKeepOn);

    JFontRadioButton rdoBlackToPlay =
        new JFontRadioButton(Lizzie.resourceBundle.getString("TsumeGoFrame.rdoBlackToPlay"));
    rdoBlackToPlay.setFocusable(false);
    contentPane.add(rdoBlackToPlay);

    JFontRadioButton rdoWhiteToPlay =
        new JFontRadioButton(Lizzie.resourceBundle.getString("TsumeGoFrame.rdoWhiteToPlay"));
    rdoWhiteToPlay.setFocusable(false);
    contentPane.add(rdoWhiteToPlay);

    Box box1 = Box.createVerticalBox();
    contentPane.add(box1);

    JFontLabel lblAttacker =
        new JFontLabel(Lizzie.resourceBundle.getString("TsumeGoFrame.lblAttacker"));
    contentPane.add(lblAttacker);

    JFontRadioButton rdoAutoDetect =
        new JFontRadioButton(Lizzie.resourceBundle.getString("TsumeGoFrame.rdoAutoDetect"));
    rdoAutoDetect.setFocusable(false);
    contentPane.add(rdoAutoDetect);

    JFontRadioButton rdoBlackAttack =
        new JFontRadioButton(Lizzie.resourceBundle.getString("TsumeGoFrame.rdoBlackAttack"));
    rdoBlackAttack.setFocusable(false);
    contentPane.add(rdoBlackAttack);

    JFontRadioButton rdoWhiteAttack =
        new JFontRadioButton(Lizzie.resourceBundle.getString("TsumeGoFrame.rdoWhiteAttack"));
    rdoWhiteAttack.setFocusable(false);
    contentPane.add(rdoWhiteAttack);

    Box box2 = Box.createVerticalBox();
    contentPane.add(box2);

    JFontLabel lblKoThreat =
        new JFontLabel(Lizzie.resourceBundle.getString("TsumeGoFrame.lblKoThreat"));
    contentPane.add(lblKoThreat);

    JFontRadioButton rdoKoBoth =
        new JFontRadioButton(Lizzie.resourceBundle.getString("TsumeGoFrame.rdoKoBoth"));
    contentPane.add(rdoKoBoth);
    rdoKoBoth.setFocusable(false);

    JFontRadioButton rdoKoAttacker =
        new JFontRadioButton(Lizzie.resourceBundle.getString("TsumeGoFrame.rdoKoAttacker"));
    contentPane.add(rdoKoAttacker);
    rdoKoAttacker.setFocusable(false);

    JFontRadioButton rdoKoDefender =
        new JFontRadioButton(Lizzie.resourceBundle.getString("TsumeGoFrame.rdoKoDefender"));
    contentPane.add(rdoKoDefender);
    rdoKoDefender.setFocusable(false);

    JFontRadioButton rdoKoNone =
        new JFontRadioButton(Lizzie.resourceBundle.getString("TsumeGoFrame.rdoKoNone"));
    contentPane.add(rdoKoNone);
    rdoKoNone.setFocusable(false);

    ButtonGroup group1 = new ButtonGroup();
    group1.add(rdoKeepOn);
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

    JLabel lblWallDistance =
        new JLabel(Lizzie.resourceBundle.getString("TsumeGoFrame.lblWallDistance"));
    contentPane.add(lblWallDistance);

    txtWallDistance = new JTextField();
    contentPane.add(txtWallDistance);
    txtWallDistance.setColumns(5);
    txtWallDistance.setDocument(new IntDocument());
    txtWallDistance.setText(String.valueOf(Lizzie.config.tsumeGoWallDistance));

    if (Lizzie.config.tsumeGoToPlay == 1) rdoKeepOn.setSelected(true);
    else if (Lizzie.config.tsumeGoToPlay == 2) rdoBlackToPlay.setSelected(true);
    else if (Lizzie.config.tsumeGoToPlay == 3) rdoWhiteToPlay.setSelected(true);

    if (Lizzie.config.tsumeGoAttaker == 1) rdoAutoDetect.setSelected(true);
    else if (Lizzie.config.tsumeGoAttaker == 2) rdoBlackAttack.setSelected(true);
    else if (Lizzie.config.tsumeGoAttaker == 3) rdoWhiteAttack.setSelected(true);

    if (Lizzie.config.tsumeGoKoThreat == 1) rdoKoBoth.setSelected(true);
    else if (Lizzie.config.tsumeGoKoThreat == 2) rdoKoAttacker.setSelected(true);
    else if (Lizzie.config.tsumeGoKoThreat == 3) rdoKoDefender.setSelected(true);
    else if (Lizzie.config.tsumeGoKoThreat == 4) rdoKoNone.setSelected(true);

    btnConfirm.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            int wallDistance = Lizzie.config.tsumeGoWallDistance;
            try {
              wallDistance = Integer.parseInt(txtWallDistance.getText());
            } catch (NumberFormatException s) {
              s.printStackTrace();
            }
            if (wallDistance > 0) {
              Lizzie.config.tsumeGoWallDistance = wallDistance;
            }
            if (rdoKeepOn.isSelected()) Lizzie.config.tsumeGoToPlay = 1;
            else if (rdoBlackToPlay.isSelected()) Lizzie.config.tsumeGoToPlay = 2;
            else if (rdoWhiteToPlay.isSelected()) Lizzie.config.tsumeGoToPlay = 3;

            if (rdoAutoDetect.isSelected()) Lizzie.config.tsumeGoAttaker = 1;
            else if (rdoBlackAttack.isSelected()) Lizzie.config.tsumeGoAttaker = 2;
            else if (rdoWhiteAttack.isSelected()) Lizzie.config.tsumeGoAttaker = 3;

            if (rdoKoBoth.isSelected()) Lizzie.config.tsumeGoKoThreat = 1;
            else if (rdoKoAttacker.isSelected()) Lizzie.config.tsumeGoKoThreat = 2;
            else if (rdoKoDefender.isSelected()) Lizzie.config.tsumeGoKoThreat = 3;
            else if (rdoKoNone.isSelected()) Lizzie.config.tsumeGoKoThreat = 4;

            Lizzie.config.uiConfig.put("tsume-go-wall-distance", Lizzie.config.tsumeGoWallDistance);
            Lizzie.config.uiConfig.put("tsume-go-to-play", Lizzie.config.tsumeGoToPlay);
            Lizzie.config.uiConfig.put("tsume-go-attaker", Lizzie.config.tsumeGoAttaker);
            Lizzie.config.uiConfig.put("tsume-go-ko-threat", Lizzie.config.tsumeGoKoThreat);

            boolean forceSide = false;
            boolean forceBlack = false;

            boolean forceToPlay = false;
            boolean blackToPlay = false;

            boolean addKoThreatAttacker = false;
            boolean addKoThreatDefender = false;

            if (Lizzie.config.tsumeGoAttaker == 2) {
              forceSide = true;
              forceBlack = true;
            } else if (Lizzie.config.tsumeGoAttaker == 3) {
              forceSide = true;
              forceBlack = false;
            }

            if (Lizzie.config.tsumeGoToPlay == 2) {
              forceToPlay = true;
              blackToPlay = true;
            } else if (Lizzie.config.tsumeGoToPlay == 3) {
              forceToPlay = true;
              blackToPlay = false;
            }

            if (Lizzie.config.tsumeGoKoThreat == 1) {
              addKoThreatAttacker = true;
              addKoThreatDefender = true;
            } else if (Lizzie.config.tsumeGoKoThreat == 2) {
              addKoThreatAttacker = true;
              addKoThreatDefender = false;
            } else if (Lizzie.config.tsumeGoKoThreat == 3) {
              addKoThreatAttacker = false;
              addKoThreatDefender = true;
            } else if (Lizzie.config.tsumeGoKoThreat == 4) {
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

    addKeyListener(
        new KeyAdapter() {
          public void keyPressed(KeyEvent e) {
            if (e.getKeyCode() == KeyEvent.VK_T) {
              if (e.isShiftDown()) {
                setVisible(false);
                Lizzie.frame.startCaptureTsumeGo();
              }
            }
          }
        });
    pack();
    btnConfirm.requestFocus();
    setLocationRelativeTo(owner);
  }
}
