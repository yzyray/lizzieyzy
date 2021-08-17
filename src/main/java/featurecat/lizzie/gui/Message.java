package featurecat.lizzie.gui;

import featurecat.lizzie.Config;
import featurecat.lizzie.Lizzie;
import java.awt.Font;
import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;
import javax.imageio.ImageIO;
import javax.swing.JDialog;
import javax.swing.JLabel;

public class Message extends JDialog {
  JLabel lblmessage;
  private final ResourceBundle resourceBundle =
      Lizzie.config.useLanguage == 0
          ? ResourceBundle.getBundle("l10n.DisplayStrings")
          : (Lizzie.config.useLanguage == 1
              ? ResourceBundle.getBundle("l10n.DisplayStrings", new Locale("zh", "CN"))
              : ResourceBundle.getBundle("l10n.DisplayStrings", new Locale("en", "US")));

  public Message() {
    // this.setModal(true);
    // setType(Type.POPUP);
    this.setResizable(false);
    setTitle(resourceBundle.getString("Message.title")); // "消息提醒");
    setAlwaysOnTop(true);
    //  setLocationByPlatform(true);
    lblmessage = new JLabel("", JLabel.CENTER);
    lblmessage.setFont(new Font(Config.sysDefaultFontName, Font.PLAIN, Config.frameFontSize));
    this.add(lblmessage);
    try {
      this.setIconImage(ImageIO.read(MoreEngines.class.getResourceAsStream("/assets/logo.png")));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void setMessage(String message) {
    String regex = "[\u4e00-\u9fa5]";
    lblmessage.setText(message);
    setSize(
        (int) (message.replaceAll(regex, "12").length() * (Lizzie.config.frameFontSize / 1.6)), 80);
    setLocationRelativeTo(null);
    setVisible(true);
    Lizzie.setFrameSize(
        this,
        (int) (message.replaceAll(regex, "12").length() * (Lizzie.config.frameFontSize / 1.6)),
        80);
    this.setModal(true);
    setVisible(false);
    setVisible(true);
  }

  public void setMessageNoModal(String message) {
    String regex = "[\u4e00-\u9fa5]";
    lblmessage.setText(message);
    setSize(
        (int) (message.replaceAll(regex, "12").length() * (Lizzie.config.frameFontSize / 1.6)), 80);
    setLocationRelativeTo(null);
    setVisible(true);
    Lizzie.setFrameSize(
        this,
        (int) (message.replaceAll(regex, "12").length() * (Lizzie.config.frameFontSize / 1.6)),
        80);
    setVisible(false);
    setVisible(true);
  }

  public void setMessageNoModal(String message, int seconds) {
    String regex = "[\u4e00-\u9fa5]";
    lblmessage.setText(message);
    setSize(
        (int) (message.replaceAll(regex, "12").length() * (Lizzie.config.frameFontSize / 1.6)), 80);
    setLocationRelativeTo(null);
    setVisible(true);
    Lizzie.setFrameSize(
        this,
        (int) (message.replaceAll(regex, "12").length() * (Lizzie.config.frameFontSize / 1.6)),
        80);
    setVisible(false);
    setVisible(true);

    Runnable runnable =
        new Runnable() {
          public void run() {
            try {
              Thread.sleep(seconds * 1000);
              setVisible(false);
            } catch (InterruptedException e) {
              // TODO Auto-generated catch block
              e.printStackTrace();
            }
          }
        };
    Thread closeTh = new Thread(runnable);
    closeTh.start();
  }
}
