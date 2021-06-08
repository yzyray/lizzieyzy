package featurecat.lizzie.gui;

import featurecat.lizzie.Lizzie;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JTextField;

public class SetShareLabel extends JDialog {
  private JTextField textField;
  private JTextField textField_1;
  private JTextField textField_2;
  private JTextField textField_3;
  private JTextField textField_4;

  public SetShareLabel(Window owner) {
    this.setModal(true);
    // setType(Type.POPUP);
    setTitle(Lizzie.resourceBundle.getString("SetShareLabel.title")); // ("设置常用标签");
    setAlwaysOnTop(true);
    setLocationRelativeTo(owner);
    // setSize(260, 203);
    Lizzie.setFrameSize(this, 255, 200);
    setResizable(false);
    try {
      this.setIconImage(ImageIO.read(MoreEngines.class.getResourceAsStream("/assets/logo.png")));
      getContentPane().setLayout(null);

      JLabel label_0 =
          new JLabel(Lizzie.resourceBundle.getString("SetShareLabel.label_0")); // ("标签1:");
      label_0.setBounds(10, 10, 54, 15);
      getContentPane().add(label_0);

      JLabel label_1 =
          new JLabel(Lizzie.resourceBundle.getString("SetShareLabel.label_1")); // ("标签2:");
      label_1.setBounds(10, 35, 54, 15);
      getContentPane().add(label_1);

      JLabel label_2 =
          new JLabel(Lizzie.resourceBundle.getString("SetShareLabel.label_2")); // ("标签3:");
      label_2.setBounds(10, 60, 54, 15);
      getContentPane().add(label_2);

      JLabel label_3 =
          new JLabel(Lizzie.resourceBundle.getString("SetShareLabel.label_3")); // ("标签4:");
      label_3.setBounds(10, 85, 54, 15);
      getContentPane().add(label_3);

      JLabel label_4 =
          new JLabel(Lizzie.resourceBundle.getString("SetShareLabel.label_4")); // ("标签5:");
      label_4.setBounds(10, 110, 54, 15);
      getContentPane().add(label_4);

      textField = new JTextField();
      textField.setBounds(54, 7, 180, 21);
      getContentPane().add(textField);
      textField.setColumns(10);

      textField_1 = new JTextField();
      textField_1.setColumns(10);
      textField_1.setBounds(54, 32, 180, 21);
      getContentPane().add(textField_1);

      textField_2 = new JTextField();
      textField_2.setColumns(10);
      textField_2.setBounds(54, 57, 180, 21);
      getContentPane().add(textField_2);

      textField_3 = new JTextField();
      textField_3.setColumns(10);
      textField_3.setBounds(54, 82, 180, 21);
      getContentPane().add(textField_3);

      textField_4 = new JTextField();
      textField_4.setColumns(10);
      textField_4.setBounds(54, 107, 180, 21);
      getContentPane().add(textField_4);

      textField.setText(Lizzie.config.shareLabel1);
      textField_1.setText(Lizzie.config.shareLabel2);
      textField_2.setText(Lizzie.config.shareLabel3);
      textField_3.setText(Lizzie.config.shareLabel4);
      textField_4.setText(Lizzie.config.shareLabel5);

      JButton saveButton =
          new JButton(Lizzie.resourceBundle.getString("SetShareLabel.saveButton")); // ("保存");
      saveButton.addActionListener(
          new ActionListener() {
            public void actionPerformed(ActionEvent e) {
              Lizzie.config.shareLabel1 = textField.getText();
              Lizzie.config.uiConfig.put("share-label-1", Lizzie.config.shareLabel1);

              Lizzie.config.shareLabel2 = textField_1.getText();
              Lizzie.config.uiConfig.put("share-label-2", Lizzie.config.shareLabel2);

              Lizzie.config.shareLabel3 = textField_2.getText();
              Lizzie.config.uiConfig.put("share-label-3", Lizzie.config.shareLabel3);

              Lizzie.config.shareLabel4 = textField_3.getText();
              Lizzie.config.uiConfig.put("share-label-4", Lizzie.config.shareLabel4);

              Lizzie.config.shareLabel5 = textField_4.getText();
              Lizzie.config.uiConfig.put("share-label-5", Lizzie.config.shareLabel5);
              Lizzie.frame.shareFrame.setShareLabelCombox();
              setVisible(false);
            }
          });
      saveButton.setBounds(94, 138, 66, 23);
      getContentPane().add(saveButton);
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }
}
