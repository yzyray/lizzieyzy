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
    super.add(label);
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
    if (displayedText != labelText)
      // {ToolTipManager.sharedInstance().setDismissDelay(99999);
      label.setToolTipText(labelText); // }
    SwingUtilities.invokeLater(
        new Runnable() {
          public void run() {
            remove(label);
            addImpl(label, null, -1);
          }
        });
  }

  public void add(JRadioButton radioButton) {
    super.add(radioButton);
    if (!Lizzie.config.isChinese) {
      String texts = radioButton.getText();
      if (texts.length() > 0) radioButton.setToolTipText(texts);
    }
  }

  public void add(JCheckBox checkBox) {
    super.add(checkBox);
    if (!Lizzie.config.isChinese) {
      String texts = checkBox.getText();
      if (texts.length() > 0) checkBox.setToolTipText(texts);
    }
  }

  public void add(JButton button) {
    super.add(button);
    if (!Lizzie.config.isChinese) {
      String texts = button.getText();
      if (texts.length() > 0 && button.getIcon() == null) button.setToolTipText(texts);
    }
  }
}
