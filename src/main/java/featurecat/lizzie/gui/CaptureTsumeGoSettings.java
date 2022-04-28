package featurecat.lizzie.gui;

import featurecat.lizzie.Lizzie;
import featurecat.lizzie.util.Utils;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

public class CaptureTsumeGoSettings extends JDialog {
  private JTextField txtBlackOffset;
  private JTextField txtWhiteOffset;
  private JTextField txtBlackPercent;
  private JTextField txtWhitePercent;
  private JTextField txtGrayOffset;

  public CaptureTsumeGoSettings(Window owner) {
    super(owner);
    setTitle("参数设置");
    setResizable(false);

    JPanel contentPane = new JPanel();
    contentPane.setBorder(new EmptyBorder(6, 15, 12, 15));

    JPanel buttonPane = new JPanel();
    buttonPane.setBorder(new EmptyBorder(0, 15, 5, 15));

    getContentPane().add(contentPane, BorderLayout.CENTER);
    getContentPane().add(buttonPane, BorderLayout.SOUTH);

    contentPane.setLayout(new GridLayout(3, 4, 5, 5));

    JLabel lblBlackOffset = new JLabel("黑最大偏色(0-255):");
    contentPane.add(lblBlackOffset);

    txtBlackOffset = new JTextField();
    contentPane.add(txtBlackOffset);
    txtBlackOffset.setColumns(5);
    txtBlackOffset.setDocument(new IntDocument());

    JLabel lblWhiteOffset = new JLabel("白最大偏色(0-255):");
    contentPane.add(lblWhiteOffset);

    txtWhiteOffset = new JTextField();
    contentPane.add(txtWhiteOffset);
    txtWhiteOffset.setColumns(5);
    txtWhiteOffset.setDocument(new IntDocument());

    JLabel lblBlackPercent = new JLabel("黑最低占比(0-100):");
    contentPane.add(lblBlackPercent);

    txtBlackPercent = new JTextField();
    contentPane.add(txtBlackPercent);
    txtBlackPercent.setColumns(5);
    txtBlackPercent.setDocument(new IntDocument());

    JLabel lblWhitePercent = new JLabel("白最低占比(0-100):");
    contentPane.add(lblWhitePercent);

    txtWhitePercent = new JTextField();
    contentPane.add(txtWhitePercent);
    txtWhitePercent.setColumns(5);
    txtWhitePercent.setDocument(new IntDocument());

    JLabel lblGrayOffset = new JLabel("最大灰度偏色(0-255):");
    contentPane.add(lblGrayOffset);

    txtGrayOffset = new JTextField();
    contentPane.add(txtGrayOffset);
    txtGrayOffset.setColumns(5);
    txtGrayOffset.setDocument(new IntDocument());

    txtBlackOffset.setText(String.valueOf(Lizzie.config.captureBlackOffset));
    txtBlackPercent.setText(String.valueOf(Lizzie.config.captureBlackPercent));
    txtWhiteOffset.setText(String.valueOf(Lizzie.config.captureWhiteOffset));
    txtWhitePercent.setText(String.valueOf(Lizzie.config.captureWhitePercent));
    txtGrayOffset.setText(String.valueOf(Lizzie.config.captureGrayOffset));

    JFontButton btnConfirm = new JFontButton("确定");
    btnConfirm.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            saveSettings();
            setVisible(false);
          }
        });
    buttonPane.add(btnConfirm);

    JFontButton btnCancel = new JFontButton("取消");
    btnCancel.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent arg0) {
            setVisible(false);
          }
        });
    buttonPane.add(btnCancel);

    pack();
    setLocationRelativeTo(owner);
  }

  private void saveSettings() {
    int blackOffset = Lizzie.config.captureBlackOffset;
    int blackPercent = Lizzie.config.captureBlackPercent;
    int whiteOffset = Lizzie.config.captureWhiteOffset;
    int whitePercent = Lizzie.config.captureWhitePercent;
    int grayOffset = Lizzie.config.captureGrayOffset;
    try {
      blackOffset = Integer.parseInt(txtBlackOffset.getText());
      blackPercent = Integer.parseInt(txtBlackPercent.getText());
      whiteOffset = Integer.parseInt(txtWhiteOffset.getText());
      whitePercent = Integer.parseInt(txtWhitePercent.getText());
      grayOffset = Integer.parseInt(txtGrayOffset.getText());
    } catch (NumberFormatException e) {
      Utils.showMsg(Lizzie.resourceBundle.getString("Menu.inputIntegerHint"));
      e.printStackTrace();
      return;
    }

    Lizzie.config.captureBlackOffset = blackOffset;
    Lizzie.config.captureBlackPercent = blackPercent;
    Lizzie.config.captureWhiteOffset = whiteOffset;
    Lizzie.config.captureWhitePercent = whitePercent;
    Lizzie.config.captureGrayOffset = grayOffset;

    Lizzie.config.uiConfig.put("capture-black-offset", Lizzie.config.captureBlackOffset);
    Lizzie.config.uiConfig.put("capture-black-percent", Lizzie.config.captureBlackPercent);
    Lizzie.config.uiConfig.put("capture-white-offset", Lizzie.config.captureWhiteOffset);
    Lizzie.config.uiConfig.put("capture-white-percent", Lizzie.config.captureWhitePercent);
    Lizzie.config.uiConfig.put("capture-gray-percent", Lizzie.config.captureGrayOffset);
  }
}
