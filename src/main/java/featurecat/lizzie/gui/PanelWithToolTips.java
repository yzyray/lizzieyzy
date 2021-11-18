package featurecat.lizzie.gui;

import featurecat.lizzie.Lizzie;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.SwingUtilities;

public class PanelWithToolTips extends JPanel {
  public void add(JLabel label) {
    String labelText = label.getText();
    String displayedText =
        SwingUtilities.layoutCompoundLabel(
            label,
            label.getFontMetrics(label.getFont()),
            labelText,
            label.getIcon(),
            label.getVerticalAlignment(),
            label.getHorizontalAlignment(),
            label.getVerticalTextPosition(),
            label.getHorizontalTextPosition(),
            label.getBounds(),
            label.getBounds(),
            label.getBounds(),
            label.getIconTextGap());
    if (displayedText != labelText) label.setToolTipText(labelText);
    super.add(label);
    SwingUtilities.invokeLater(
        new Runnable() {
          public void run() {
            remove(label);
            addImpl(label, null, -1);
          }
        });
  }

  public void add(JRadioButton radioButton) {
    if (!Lizzie.config.isChinese) {
      String texts = radioButton.getText();
      if (texts.length() > 0) radioButton.setToolTipText(texts);
    }
    super.add(radioButton);
  }

  public void add(JCheckBox checkBox) {
    if (!Lizzie.config.isChinese) {
      String texts = checkBox.getText();
      if (texts.length() > 0) checkBox.setToolTipText(texts);
    }
    super.add(checkBox);
  }

  public void add(JButton button) {
    if (!Lizzie.config.isChinese) {
      String texts = button.getText();
      if (texts.length() > 0 && button.getIcon() == null) button.setToolTipText(texts);
    }
    super.add(button);
  }
}
