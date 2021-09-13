package featurecat.lizzie.gui;

import featurecat.lizzie.Lizzie;
import featurecat.lizzie.util.Utils;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ResourceBundle;
import javax.imageio.ImageIO;
import javax.swing.JDialog;
import javax.swing.JPanel;

public class SetAnaGameRandomStart extends JDialog {
  // private JFormattedTextField time;
  private final ResourceBundle resourceBundle = Lizzie.resourceBundle;
  public static final DecimalFormat FORMAT_PERCENT = new DecimalFormat("#0.0");
  private JFontTextField txtFirstMove;
  private JFontTextField txtWinrate;
  private JFontTextField txtPlayouts;
  private JFontCheckBox chkEnable;

  public SetAnaGameRandomStart() {
    setModal(true);
    setTitle(resourceBundle.getString("SetAnaGameRandomStart.title"));
    setAlwaysOnTop(true);
    Lizzie.setFrameSize(this, 314, 200);
    getContentPane().setLayout(new BorderLayout());
    JPanel buttonPane = new JPanel();
    getContentPane().add(buttonPane, BorderLayout.CENTER);
    buttonPane.setLayout(null);

    JFontButton closeButton =
        new JFontButton(resourceBundle.getString("SetAnaGameRandomStart.cancel"));
    closeButton.setBounds(158, 128, 79, 33);

    closeButton.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            setVisible(false);
          }
        });
    buttonPane.add(closeButton);
    NumberFormat nf = NumberFormat.getIntegerInstance();
    nf.setGroupingUsed(false);

    JFontButton applyButton =
        new JFontButton(resourceBundle.getString("SetAnaGameRandomStart.apply"));
    applyButton.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            if (applyChange()) setVisible(false);
            else {
              Utils.showMsg(resourceBundle.getString("SetAnaGameRandomStart.wrongFormat"));
            }
          }
        });
    applyButton.setBounds(72, 128, 79, 33);
    buttonPane.add(applyButton);

    chkEnable = new JFontCheckBox(resourceBundle.getString("SetAnaGameRandomStart.chkEnable"));
    chkEnable.setBounds(6, 6, 103, 23);
    buttonPane.add(chkEnable);

    chkEnable.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            if (chkEnable.isSelected()) {
              txtFirstMove.setEnabled(true);
              txtWinrate.setEnabled(true);
              txtPlayouts.setEnabled(true);
            } else {
              txtFirstMove.setEnabled(false);
              txtWinrate.setEnabled(false);
              txtPlayouts.setEnabled(false);
            }
          }
        });

    JFontLabel lblFirst =
        new JFontLabel(
            resourceBundle.getString("SetAnaGameRandomStart.lblFirst.text")); // $NON-NLS-1$
    lblFirst.setBounds(26, 32, 43, 21);
    buttonPane.add(lblFirst);

    txtFirstMove = new JFontTextField();
    txtFirstMove.setBounds(72, 32, 66, 21);
    buttonPane.add(txtFirstMove);
    txtFirstMove.setColumns(10);

    JFontLabel lblMove =
        new JFontLabel(
            resourceBundle.getString("SetAnaGameRandomStart.lblMove.text")); // $NON-NLS-1$
    lblMove.setBounds(148, 32, 54, 21);
    buttonPane.add(lblMove);

    txtWinrate = new JFontTextField();
    txtWinrate.setBounds(229, 60, 36, 21);
    buttonPane.add(txtWinrate);
    txtWinrate.setColumns(10);

    txtPlayouts = new JFontTextField();
    txtPlayouts.setBounds(229, 91, 36, 21);
    buttonPane.add(txtPlayouts);
    txtPlayouts.setColumns(10);

    JFontLabel lblWinrate =
        new JFontLabel(
            resourceBundle.getString("SetAnaGameRandomStart.lblWinrate.text")); // $NON-NLS-1$
    lblWinrate.setBounds(26, 60, 326, 21);
    buttonPane.add(lblWinrate);

    JFontLabel lblpercents1 = new JFontLabel("%"); // $NON-NLS-1$
    lblpercents1.setBounds(275, 63, 54, 15);
    buttonPane.add(lblpercents1);

    JFontLabel lblPlayouts =
        new JFontLabel(
            resourceBundle.getString("SetAnaGameRandomStart.lblVisits.text")); // $NON-NLS-1$
    lblPlayouts.setBounds(26, 89, 326, 24);
    buttonPane.add(lblPlayouts);

    JFontLabel lblpercents2 = new JFontLabel("%");
    lblpercents2.setBounds(275, 94, 54, 15);
    buttonPane.add(lblpercents2);
    try {
      this.setIconImage(ImageIO.read(MoreEngines.class.getResourceAsStream("/assets/logo.png")));
    } catch (IOException e1) {
      // TODO Auto-generated catch block
      e1.printStackTrace();
    }

    chkEnable.setSelected(Lizzie.config.enableAnaGameRamdonStart);
    txtFirstMove.setText(String.valueOf(Lizzie.config.anaGameRandomMove));
    txtWinrate.setText(String.valueOf(Lizzie.config.anaGameRandomWinrateDiff));
    txtPlayouts.setText(String.valueOf(Lizzie.config.anaGameRandomPlayoutsDiff));
    if (chkEnable.isSelected()) {
      txtFirstMove.setEnabled(true);
      txtWinrate.setEnabled(true);
      txtPlayouts.setEnabled(true);
    } else {
      txtFirstMove.setEnabled(false);
      txtWinrate.setEnabled(false);
      txtPlayouts.setEnabled(false);
    }
    setLocationRelativeTo(getOwner());
  }

  private boolean applyChange() {
    Lizzie.config.enableAnaGameRamdonStart = chkEnable.isSelected();
    try {
      Lizzie.config.anaGameRandomMove = Integer.parseInt(txtFirstMove.getText());
      Lizzie.config.anaGameRandomWinrateDiff = Double.parseDouble(txtWinrate.getText());
      Lizzie.config.anaGameRandomPlayoutsDiff = Double.parseDouble(txtPlayouts.getText());
    } catch (NumberFormatException e) {
      // TODO Auto-generated catch block
      return false;
    }
    Lizzie.config.uiConfig.put(
        "enable-anagame-randomstart", Lizzie.config.enableAnaGameRamdonStart);
    Lizzie.config.uiConfig.put("anagame-random-move", Lizzie.config.anaGameRandomMove);
    Lizzie.config.uiConfig.put(
        "anagame-random-winratediff", Lizzie.config.anaGameRandomWinrateDiff);
    Lizzie.config.uiConfig.put(
        "anagame-random-playoutdiff", Lizzie.config.anaGameRandomPlayoutsDiff);
    return true;
  }
}
