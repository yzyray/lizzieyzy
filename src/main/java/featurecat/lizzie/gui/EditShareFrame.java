package featurecat.lizzie.gui;

import featurecat.lizzie.Lizzie;
import featurecat.lizzie.util.Utils;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

public class EditShareFrame extends JDialog {
  private final ResourceBundle resourceBundle =
      Lizzie.config.useLanguage == 0
          ? ResourceBundle.getBundle("l10n.DisplayStrings")
          : (Lizzie.config.useLanguage == 1
              ? ResourceBundle.getBundle("l10n.DisplayStrings", new Locale("zh", "CN"))
              : ResourceBundle.getBundle("l10n.DisplayStrings", new Locale("en", "US")));
  private JTextField txtBlack;
  private JTextField txtWhite;
  public JTextField txtUploader;
  private JTextField txtOtherInfo;
  private JTextField otherLabel;
  private JCheckBox checkBoxPublic;
  private JDialog shareFrame = this;
  int selectedLabelIndex = 0;
  JComboBox<String> cboxLabel;
  ItemListener lis;
  JDialog dialog;

  public EditShareFrame(
      String id,
      String inBlack,
      String inWhite,
      String inLabel,
      String inOtherInfo,
      String inFileName,
      boolean isPublic) {
    this.setModal(true);
    dialog = this;
    // setType(Type.POPUP);
    setResizable(false);
    setTitle(resourceBundle.getString("EditShareFrame.title")); // "修改分享信息");
    setAlwaysOnTop(Lizzie.frame.isAlwaysOnTop());
    setLocationRelativeTo(Lizzie.frame);
    getContentPane().setLayout(null);

    JButton btnEdit = new JButton(resourceBundle.getString("EditShareFrame.btnEdit")); // ("修改");
    btnEdit.setBounds(141, 131, 97, 23);
    getContentPane().add(btnEdit);

    JButton btnCancel =
        new JButton(resourceBundle.getString("EditShareFrame.btnCancel")); // ("关闭");
    btnCancel.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            setVisible(false);
          }
        });
    btnCancel.setBounds(355, 131, 97, 23);
    getContentPane().add(btnCancel);

    txtBlack = new JTextField();
    txtBlack.setBounds(70, 10, 229, 23);
    getContentPane().add(txtBlack);
    txtBlack.setColumns(10);

    txtWhite = new JTextField();
    txtWhite.setBounds(369, 10, 211, 23);
    getContentPane().add(txtWhite);
    txtWhite.setColumns(10);
    txtBlack.setText(inBlack);
    txtWhite.setText(inWhite);

    txtUploader = new JTextField();
    txtUploader.setBounds(369, 44, 211, 23);
    getContentPane().add(txtUploader);
    txtUploader.setColumns(10);
    txtUploader.setEnabled(false);
    txtUploader.setText(Lizzie.config.uploadUser);

    txtOtherInfo = new JTextField();
    txtOtherInfo.setBounds(127, 77, 453, 23);
    getContentPane().add(txtOtherInfo);
    txtOtherInfo.setColumns(10);
    txtOtherInfo.setText(inOtherInfo);

    JLabel labelBlack =
        new JLabel(resourceBundle.getString("EditShareFrame.labelBlack")); // ("黑:");
    labelBlack.setBounds(10, 13, 50, 15);
    getContentPane().add(labelBlack);

    JLabel labelWhite =
        new JLabel(resourceBundle.getString("EditShareFrame.labelWhite")); // ("白:");
    labelWhite.setBounds(309, 14, 50, 15);
    getContentPane().add(labelWhite);

    JLabel labeluploader =
        new JLabel(resourceBundle.getString("EditShareFrame.labeluploader")); // ("上传者:");
    labeluploader.setBounds(309, 48, 65, 15);
    getContentPane().add(labeluploader);

    JLabel labelother =
        new JLabel(resourceBundle.getString("EditShareFrame.labelother")); // ("其他信息:");
    labelother.setBounds(70, 81, 57, 15);
    getContentPane().add(labelother);

    JLabel labellabel =
        new JLabel(resourceBundle.getString("EditShareFrame.labellabel")); // ("标签:");
    labellabel.setBounds(10, 48, 50, 15);
    getContentPane().add(labellabel);

    otherLabel = new JTextField();
    otherLabel.setBounds(202, 43, 97, 23);
    getContentPane().add(otherLabel);

    //    otherLabel.setEnabled(false);

    cboxLabel = new JComboBox<String>();
    setShareLabelCombox();
    cboxLabel.setBounds(107, 45, 85, 21);
    getContentPane().add(cboxLabel);
    cboxLabel.setSelectedIndex(cboxLabel.getItemCount() - 1);
    cboxLabel.addItemListener(
        new ItemListener() {
          public void itemStateChanged(ItemEvent e) {
            if (e.getStateChange() == ItemEvent.SELECTED) {
              selectedLabelIndex = cboxLabel.getSelectedIndex();
            }
            if (selectedLabelIndex == (cboxLabel.getItemCount() - 1)) otherLabel.setEnabled(true);
            else {
              otherLabel.setEnabled(false);
              otherLabel.setText("");
            }
          }
        });
    otherLabel.setText(inLabel);
    otherLabel.setEnabled(true);
    ImageIcon iconSettings = new ImageIcon();
    try {
      iconSettings.setImage(ImageIO.read(getClass().getResourceAsStream("/assets/config.png")));
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

    JButton btnConfig = new JButton(iconSettings);
    btnConfig.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            SetShareLabel setShareLabel = new SetShareLabel(shareFrame);
            setShareLabel.setVisible(true);
          }
        });
    btnConfig.setBounds(70, 44, 27, 23);
    getContentPane().add(btnConfig);

    JButton btnDelete =
        new JButton(resourceBundle.getString("EditShareFrame.btnDelete")); // ("删除");
    btnDelete.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            int re =
                JOptionPane.showConfirmDialog(
                    dialog,
                    resourceBundle.getString("EditShareFrame.deleteHint1"),
                    resourceBundle.getString("EditShareFrame.deleteHint2"),
                    JOptionPane.DEFAULT_OPTION);
            // null, "删除后将无法查询或从链接中打开棋谱,确定删除吗?", "确认删除", JOptionPane.DEFAULT_OPTION);

            if (re == JOptionPane.OK_OPTION) {
              SocketEditFile socketEditFile = new SocketEditFile();
              String result =
                  socketEditFile.SocketEditFile(
                      true,
                      id,
                      inBlack.replaceAll(">", ""),
                      inWhite.replaceAll(">", ""),
                      inLabel.replaceAll(">", ""),
                      inOtherInfo.replaceAll(">", ""),
                      inFileName,
                      Lizzie.config.uploadUser,
                      isPublic);
              if (result.startsWith("success")) {
                Utils.showMsg(
                    resourceBundle.getString("EditShareFrame.deleteSuccess")); // ("删除成功!");
                setVisible(false);
              } else {
                Utils.showMsg(resourceBundle.getString("EditShareFrame.deleteFail")); // ("删除失败!");
              }
            }
          }
        });
    btnDelete.setBounds(248, 131, 97, 23);
    getContentPane().add(btnDelete);

    JLabel labelNotice =
        new JLabel(
            resourceBundle.getString(
                "EditShareFrame.labelNotice")); // ("注:请勿使用包含符号(例如><|\\/.,等)的黑白名字,标签,上传者,其他信息");
    labelNotice.setBounds(10, 106, 516, 15);
    getContentPane().add(labelNotice);

    checkBoxPublic =
        new JCheckBox(resourceBundle.getString("EditShareFrame.checkBoxPublic")); // ("公开");
    checkBoxPublic.setBounds(6, 77, 74, 23);
    getContentPane().add(checkBoxPublic);
    if (isPublic) checkBoxPublic.setSelected(true);

    btnEdit.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            SocketEditFile socketEditFile = new SocketEditFile();
            String result =
                socketEditFile.SocketEditFile(
                    false,
                    id,
                    txtBlack.getText(),
                    txtWhite.getText(),
                    getLabel(),
                    txtOtherInfo.getText(),
                    inFileName,
                    Lizzie.config.uploadUser,
                    checkBoxPublic.isSelected());
            if (result.startsWith("success")) {
              Utils.showMsg(resourceBundle.getString("EditShareFrame.editSucess"));
              // msg.setVisible(true);
            } else {
              Utils.showMsg(resourceBundle.getString("EditShareFrame.editFail"));
              //  msg.setVisible(true);
            }
          }
        });
    try {
      this.setIconImage(ImageIO.read(MoreEngines.class.getResourceAsStream("/assets/logo.png")));
    } catch (IOException e) {
      e.printStackTrace();
    }
    // setSize(606, 192);
    Lizzie.setFrameSize(this, 606, 192);
  }

  public void setShareLabelCombox() {
    cboxLabel.removeAllItems();

    // cboxLabel.removeItemListener(lis);
    if (!Lizzie.config.shareLabel1.equals("")) {
      cboxLabel.addItem(Lizzie.config.shareLabel1);
    }
    if (!Lizzie.config.shareLabel2.equals("")) {
      cboxLabel.addItem(Lizzie.config.shareLabel2);
    }
    if (!Lizzie.config.shareLabel3.equals("")) {
      cboxLabel.addItem(Lizzie.config.shareLabel3);
    }
    if (!Lizzie.config.shareLabel4.equals("")) {
      cboxLabel.addItem(Lizzie.config.shareLabel4);
    }
    if (!Lizzie.config.shareLabel5.equals("")) {
      cboxLabel.addItem(Lizzie.config.shareLabel5);
    }
    cboxLabel.addItem(resourceBundle.getString("EditShareFrame.other")); // ("其他");
    otherLabel.setEnabled(false);
    otherLabel.setText("");
    // cboxLabel.addItemListener(lis);
  }

  public String getLabel() {
    if (otherLabel.isEnabled()) return otherLabel.getText();
    else {
      switch (selectedLabelIndex) {
        case 0:
          return Lizzie.config.shareLabel1;
        case 1:
          return Lizzie.config.shareLabel2;
        case 2:
          return Lizzie.config.shareLabel3;
        case 3:
          return Lizzie.config.shareLabel4;
        case 4:
          return Lizzie.config.shareLabel5;
      }
    }
    return "";
  }

  public String formatStr(String inputStr) {
    inputStr = inputStr.replaceAll("[/\\\\:*?|]", "");
    inputStr = inputStr.replaceAll("[\"<>]", "");
    return inputStr;
  }
}
