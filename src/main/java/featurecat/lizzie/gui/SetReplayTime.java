package featurecat.lizzie.gui;

import featurecat.lizzie.Lizzie;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.text.NumberFormat;
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
import javax.swing.text.InternationalFormatter;

public class SetReplayTime extends JDialog {
  private JFormattedTextField time;
  //  private final ResourceBundle resourceBundle = Lizzie.config.useLanguage==0?
  // ResourceBundle.getBundle("l10n.DisplayStrings"):(Lizzie.config.useLanguage==1?
  // ResourceBundle.getBundle("l10n.DisplayStrings", new Locale("zh", "CN")):
  // ResourceBundle.getBundle("l10n.DisplayStrings", new Locale("en", "US")));
  private int replaytime;

  public SetReplayTime() {
    // setType(Type.POPUP);
    setTitle(Lizzie.resourceBundle.getString("SetReplayTime.title")); // ("设置变化图回放间隔");
    setAlwaysOnTop(Lizzie.frame.isAlwaysOnTop());
    Lizzie.setFrameSize(this, 357, 130);
    getContentPane().setLayout(new BorderLayout());
    JPanel buttonPane = new JPanel();
    getContentPane().add(buttonPane, BorderLayout.CENTER);
    JButton okButton =
        new JButton(Lizzie.resourceBundle.getString("SetReplayTime.okButton")); // ("确定");
    okButton.setBounds(138, 50, 74, 29);

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

    JLabel lblInterval =
        new JLabel(
            Lizzie.resourceBundle.getString("SetReplayTime.lblInterval")); // ("设置变化图回放间隔(毫秒)：");
    lblInterval.setBounds(92, 18, 104, 20);
    buttonPane.add(lblInterval);
    lblInterval.setHorizontalAlignment(SwingConstants.LEFT);

    time =
        new JFormattedTextField(
            new InternationalFormatter(nf) {
              protected DocumentFilter getDocumentFilter() {
                return filter;
              }

              private DocumentFilter filter = new DigitOnlyFilter();
            });
    time.setBounds(187, 19, 70, 19);
    buttonPane.add(time);
    time.setColumns(3);

    time.setText((int) (Lizzie.config.replayBranchIntervalSeconds * 1000) + "");
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
    Lizzie.config.replayBranchIntervalSeconds = replaytime / 1000.0;
    Lizzie.config.uiConfig.put("replay-branch-interval-seconds", replaytime / 1000.0);
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
