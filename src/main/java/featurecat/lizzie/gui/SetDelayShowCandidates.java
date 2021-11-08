package featurecat.lizzie.gui;

import featurecat.lizzie.Lizzie;
import featurecat.lizzie.util.Utils;
import java.awt.BorderLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

public class SetDelayShowCandidates extends JDialog {
  private JTextField txtDelayCandidates;

  public SetDelayShowCandidates(Window owner) {
    super(owner);
    setTitle(Lizzie.resourceBundle.getString("SetDelayShowCandidates.title"));
    JPanel mainPanel = new JPanel();
    mainPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
    getContentPane().add(mainPanel, BorderLayout.CENTER);

    JCheckBox chkDelayCandidates =
        new JFontCheckBox(
            Lizzie.resourceBundle.getString("SetDelayShowCandidates.lblDelayCandidates"));
    mainPanel.add(chkDelayCandidates);
    chkDelayCandidates.setSelected(Lizzie.config.delayShowCandidates);
    chkDelayCandidates.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            txtDelayCandidates.setEnabled(chkDelayCandidates.isSelected());
          }
        });

    txtDelayCandidates = new JFontTextField();
    mainPanel.add(txtDelayCandidates);
    txtDelayCandidates.setColumns(10);
    txtDelayCandidates.setToolTipText(
        Lizzie.resourceBundle.getString(
            "SetDelayShowCandidates.txtDelayCandidates.tooltips")); // ("负数或0则代表需手动开启显示(快捷键F)");
    txtDelayCandidates.setEnabled(Lizzie.config.delayShowCandidates);
    txtDelayCandidates.setDocument(new DoubleDocument());
    txtDelayCandidates.setText(String.valueOf(Lizzie.config.delayCandidatesSeconds));

    JPanel buttonPanel = new JPanel();
    getContentPane().add(buttonPanel, BorderLayout.SOUTH);

    JButton apply =
        new JFontButton(Lizzie.resourceBundle.getString("SetDelayShowCandidates.apply"));
    apply.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            Lizzie.config.delayShowCandidates = chkDelayCandidates.isSelected();
            Lizzie.config.delayCandidatesSeconds =
                Utils.parseTextToDouble(txtDelayCandidates, Lizzie.config.delayCandidatesSeconds);
            Lizzie.config.uiConfig.put("delay-show-candidates", Lizzie.config.delayShowCandidates);
            Lizzie.config.uiConfig.put(
                "delay-candidates-seconds", Lizzie.config.delayCandidatesSeconds);
            setVisible(false);
            if (Lizzie.config.delayShowCandidates) Lizzie.board.handleCandidatesDelay();
            else Lizzie.frame.showCandidates();
          }
        });
    buttonPanel.add(apply);

    JButton cancel =
        new JFontButton(Lizzie.resourceBundle.getString("SetDelayShowCandidates.cancel"));
    cancel.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            setVisible(false);
          }
        });
    buttonPanel.add(cancel);
    pack();
    setLocationRelativeTo(owner);
  }
}
