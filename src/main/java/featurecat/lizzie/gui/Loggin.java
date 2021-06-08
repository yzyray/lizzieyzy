package featurecat.lizzie.gui;

import featurecat.lizzie.Lizzie;
import featurecat.lizzie.util.Utils;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;
import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

public class Loggin extends JDialog {
  private JTextField txtUser;
  private JPasswordField passwordField;
  private JDialog thisDialog;
  private final ResourceBundle resourceBundle =
      Lizzie.config.useLanguage == 0
          ? ResourceBundle.getBundle("l10n.DisplayStrings")
          : (Lizzie.config.useLanguage == 1
              ? ResourceBundle.getBundle("l10n.DisplayStrings", new Locale("zh", "CN"))
              : ResourceBundle.getBundle("l10n.DisplayStrings", new Locale("en", "US")));

  public Loggin(Window owner, boolean editPrivate) {
    this.setModal(true);
    thisDialog = this;
    // setType(Type.POPUP);
    setTitle(resourceBundle.getString("loggin.title")); // ("登录");
    setAlwaysOnTop(true);
    setResizable(false);
    setLocationRelativeTo(owner);
    //  setSize(289, 131);
    Lizzie.setFrameSize(this, 289, 131);
    try {
      this.setIconImage(ImageIO.read(MoreEngines.class.getResourceAsStream("/assets/logo.png")));
      getContentPane().setLayout(null);

      txtUser = new JTextField();
      txtUser.setBounds(150, 10, 121, 21);
      getContentPane().add(txtUser);
      txtUser.setColumns(10);

      passwordField = new JPasswordField();
      passwordField.setBounds(150, 41, 121, 21);
      getContentPane().add(passwordField);

      JButton btnLoggin = new JButton(resourceBundle.getString("loggin.btnLoggin")); // ("注册/登录");
      btnLoggin.addActionListener(
          new ActionListener() {
            public void actionPerformed(ActionEvent e) {
              if (txtUser.getText().contains(" ")) {
                Utils.showMsg(resourceBundle.getString("loggin.noSpace"));
                return;
              }
              SocketLoggin socketLoggin = new SocketLoggin();
              String result =
                  socketLoggin.SocketLoggin(
                      txtUser.getText(), new String(passwordField.getPassword()));
              boolean normalResult = false;
              if (result.startsWith("success")) {
                String[] params = result.trim().split(">");
                if (params.length == 2) {

                  try {
                    int value = Integer.parseInt(params[1]);
                    if (value == 0) {
                      normalResult = true;
                      JOptionPane.showConfirmDialog(
                          thisDialog,
                          resourceBundle.getString("loggin.logginSucceed"),
                          resourceBundle.getString("loggin.logginInfo"),
                          JOptionPane.DEFAULT_OPTION);
                      // null, "登录成功", "登录信息", JOptionPane.DEFAULT_OPTION);
                      if (!editPrivate)
                        Lizzie.frame.shareFrame.txtUploader.setText(txtUser.getText());
                      Lizzie.config.uploadUser = txtUser.getText();
                      Lizzie.config.uploadPassWd = new String(passwordField.getPassword());
                      Lizzie.config.uiConfig.put("up-load-user", Lizzie.config.uploadUser);
                      Lizzie.config.uiConfig.put("up-load-passwd", Lizzie.config.uploadPassWd);
                      setVisible(false);
                      if (editPrivate) {
                        PrivateKifuSearch search = new PrivateKifuSearch();
                        search.setVisible(true);
                      }
                    }
                    if (value == 1) {
                      normalResult = true;
                      JOptionPane.showConfirmDialog(
                          thisDialog,
                          resourceBundle.getString("loggin.signUpSucceed"),
                          resourceBundle.getString("loggin.logginInfo"),
                          JOptionPane.DEFAULT_OPTION);
                      //  null, "注册并登录成功", "登录信息", JOptionPane.DEFAULT_OPTION);
                      if (!editPrivate)
                        Lizzie.frame.shareFrame.txtUploader.setText(txtUser.getText());
                      Lizzie.config.uploadUser = txtUser.getText();
                      Lizzie.config.uploadPassWd = new String(passwordField.getPassword());
                      Lizzie.config.uiConfig.put("up-load-user", Lizzie.config.uploadUser);
                      Lizzie.config.uiConfig.put("up-load-passwd", Lizzie.config.uploadPassWd);
                      setVisible(false);
                      if (editPrivate) {
                        PrivateKifuSearch search = new PrivateKifuSearch();
                        search.setVisible(true);
                      }
                    }
                  } catch (Exception ex) {

                  }
                }
              } else if (result.startsWith("error")) {
                String[] params = result.trim().split(">");
                if (params.length == 2) {

                  try {
                    int value = Integer.parseInt(params[1]);
                    if (value == 0) {
                      normalResult = true;
                      JOptionPane.showConfirmDialog(
                          thisDialog,
                          resourceBundle.getString("loggin.logginFailed"),
                          resourceBundle.getString("loggin.logginInfo"),
                          JOptionPane.DEFAULT_OPTION);
                      //  null, "登录失败,密码错误", "登录信息", JOptionPane.DEFAULT_OPTION);
                    }
                    if (value == 1) {
                      normalResult = true;
                      JOptionPane.showConfirmDialog(
                          thisDialog,
                          resourceBundle.getString("loggin.logginFailed2"),
                          resourceBundle.getString("loggin.logginInfo"),
                          JOptionPane.DEFAULT_OPTION);
                      //                          null, "登录失败,请使用无特殊符号用户名密码重新尝试", "登录信息",
                      // JOptionPane.DEFAULT_OPTION);
                    }
                  } catch (Exception ex) {

                  }
                }
              }

              if (!normalResult) {
                SMessage msg = new SMessage();
                msg.setMessage( //    "连接失败...请重试或下载最新版Lizzie,链接:"
                    resourceBundle.getString("loggin.connectFailed")
                        + "https://pan.baidu.com/s/1q615GHD62F92mNZbTYfcxA");
                // msg.setVisible(true);
              }
            }
          });
      btnLoggin.setBounds(91, 72, 105, 23);
      getContentPane().add(btnLoggin);

      JLabel labelUser =
          new JLabel(resourceBundle.getString("loggin.labelUser")); // ;("用户名:(勿用符号,空格)");
      labelUser.setBounds(10, 13, 154, 15);
      getContentPane().add(labelUser);

      JLabel labelPasswd = new JLabel(resourceBundle.getString("loggin.labelPasswd")); // ("密码:");
      labelPasswd.setBounds(10, 44, 71, 15);
      getContentPane().add(labelPasswd);

      JCheckBox checkBoxShow =
          new JCheckBox(resourceBundle.getString("loggin.checkBoxShow")); // ("显示");
      checkBoxShow.setBounds(71, 40, 73, 22);
      getContentPane().add(checkBoxShow);

      checkBoxShow.addActionListener(
          new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
              // TBD
              if (checkBoxShow.isSelected()) passwordField.setEchoChar((char) 0);
              else passwordField.setEchoChar((char) '*');
            }
          });

    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
