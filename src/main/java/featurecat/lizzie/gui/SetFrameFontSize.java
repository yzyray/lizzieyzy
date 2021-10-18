package featurecat.lizzie.gui;

import featurecat.lizzie.Config;
import featurecat.lizzie.Lizzie;
import featurecat.lizzie.util.Utils;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.NumberFormat;
import java.util.ResourceBundle;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import javax.swing.text.InternationalFormatter;

public class SetFrameFontSize extends JDialog {
  private JFormattedTextField fontSize;
  private final ResourceBundle resourceBundle = Lizzie.resourceBundle;
  private int fontSizeNumber;

  public SetFrameFontSize() {
    // setType(Type.POPUP);
    setModal(true);
    setTitle(resourceBundle.getString("SetFrameFontSize.title")); // ("设置全局字体大小");
    setAlwaysOnTop(Lizzie.frame.isAlwaysOnTop());
    // setBounds(0, 0, 318, 109);
    setResizable(false);
    Lizzie.setFrameSize(
        this,
        Lizzie.config.isFrameFontSmall() ? 300 : (Lizzie.config.isFrameFontMiddle() ? 313 : 352),
        109);
    getContentPane().setLayout(new BorderLayout());
    JPanel buttonPane = new JPanel();
    getContentPane().add(buttonPane, BorderLayout.CENTER);
    JButton okButton =
        new JButton(resourceBundle.getString("SetFrameFontSize.okButton")); // ("确定");
    okButton.setBounds(
        Lizzie.config.isFrameFontSmall() ? 102 : (Lizzie.config.isFrameFontMiddle() ? 110 : 115),
        45,
        87,
        25);

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

    JFontLabel lblSetSize =
        new JFontLabel(
            resourceBundle.getString("SetFrameFontSize.lblSetSize")); // ("设置全局字体大小(默认12)：");
    lblSetSize.setBounds(10, 8, 341, 24);
    buttonPane.add(lblSetSize);
    lblSetSize.setHorizontalAlignment(SwingConstants.LEFT);

    fontSize =
        new JFormattedTextField(
            new InternationalFormatter(nf) {
              protected DocumentFilter getDocumentFilter() {
                return filter;
              }

              private DocumentFilter filter = new DigitOnlyFilter();
            });
    fontSize.setFont(new Font(Config.sysDefaultFontName, Font.PLAIN, Config.frameFontSize));
    fontSize.setBounds(
        Lizzie.config.isFrameFontSmall() ? 202 : (Lizzie.config.isFrameFontMiddle() ? 220 : 260),
        8,
        70,
        24);
    buttonPane.add(fontSize);
    fontSize.setColumns(3);

    fontSize.setText(String.valueOf(Config.frameFontSize));
    setLocationRelativeTo(Lizzie.frame);
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
    Config.frameFontSize = fontSizeNumber;
    Lizzie.config.uiConfig.put("frame-font-size", Config.frameFontSize);
    if (!Lizzie.config.isChinese && Config.frameFontSize > 12)
      Utils.showMsg(resourceBundle.getString("menu.setFrameSizeAlart"));
    Utils.showMsg(resourceBundle.getString("SetFrameFontSize.successHint"));
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

    fontSizeNumber = txtFieldValue(fontSize);
    if (fontSizeNumber < 12 || fontSizeNumber > 20) {
      fontSize.setToolTipText(resourceBundle.getString("LizzieChangeMove.txtMoveNumber.error"));
      Action action = fontSize.getActionMap().get("postTip");
      if (action != null) {
        ActionEvent ae =
            new ActionEvent(
                fontSize,
                ActionEvent.ACTION_PERFORMED,
                "postTip",
                EventQueue.getMostRecentEventTime(),
                0);
        action.actionPerformed(ae);
      }
      fontSize.setBackground(Color.red);
      return false;
    }
    return true;
  }
}
