package featurecat.lizzie.gui;

import featurecat.lizzie.Lizzie;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.ResourceBundle;
import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import javax.swing.text.DocumentFilter.FilterBypass;
import javax.swing.text.InternationalFormatter;

public class SetBoardSyncTime extends JDialog {
  private JFormattedTextField time;
  private final ResourceBundle resourceBundle = Lizzie.resourceBundle;
  //      Lizzie.config.useLanguage == 0
  //          ? ResourceBundle.getBundle("l10n.DisplayStrings")
  //          : (Lizzie.config.useLanguage == 1
  //              ? ResourceBundle.getBundle("l10n.DisplayStrings", new Locale("zh", "CN"))
  //              : ResourceBundle.getBundle("l10n.DisplayStrings", new Locale("en", "US")));
  private int replaytime;
  private static JTextField defaultText = new JTextField();

  public SetBoardSyncTime() {
    // setType(Type.POPUP);
    setTitle(resourceBundle.getString("SetBoardSyncTime.title")); // ("设置持续同步间隔");
    setAlwaysOnTop(Lizzie.frame.isAlwaysOnTop());
    // setBounds(0, 0, 300, 130);
    Lizzie.setFrameSize(this, 285, 105);
    setResizable(false);
    getContentPane().setLayout(new BorderLayout());
    JPanel buttonPane = new JPanel();
    getContentPane().add(buttonPane, BorderLayout.CENTER);
    JButton okButton =
        new JButton(resourceBundle.getString("SetBoardSyncTime.okButton")); // ("确定");
    okButton.setBounds(97, 44, 87, 22);

    okButton.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {

            setVisible(false);
            applyChange();
          }
        });
    buttonPane.setLayout(null);
    okButton.setActionCommand("OK");
    buttonPane.add(okButton);
    getRootPane().setDefaultButton(okButton);
    NumberFormat nf = NumberFormat.getIntegerInstance();
    nf.setGroupingUsed(false);

    JLabel lblSetInterval =
        new JLabel(
            resourceBundle.getString("SetBoardSyncTime.lblSetInterval")); // ("设置持续同步间隔(毫秒)：");
    lblSetInterval.setBounds(40, 14, 155, 20);
    buttonPane.add(lblSetInterval);
    lblSetInterval.setHorizontalAlignment(SwingConstants.LEFT);

    time =
        new JFormattedTextField(
            new InternationalFormatter(nf) {
              protected DocumentFilter getDocumentFilter() {
                return filter;
              }

              private DocumentFilter filter = new DigitOnlyFilter();
            });
    time.setBounds(190, 15, 40, 19);
    buttonPane.add(time);
    time.setColumns(3);

    time.setText(Lizzie.config.readBoardArg2 + "");
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

  private void applyChange() {
    replaytime = txtFieldValue(time);
    Lizzie.config.readBoardArg2 = replaytime;
    Lizzie.config.uiConfig.put("read-board-arg2", Lizzie.config.readBoardArg2);
  }

  private Integer txtFieldValue(JTextField txt) {
    if (txt.getText().trim().isEmpty()
        || txt.getText().trim().length() >= String.valueOf(Integer.MAX_VALUE).length()) {
      return 0;
    } else {
      return Integer.parseInt(txt.getText().trim());
    }
  }
}
