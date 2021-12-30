package featurecat.lizzie.gui;

import featurecat.lizzie.Config;
import featurecat.lizzie.Lizzie;
import java.awt.Font;
import java.awt.Window;
import java.io.IOException;
import java.util.ResourceBundle;
import javax.imageio.ImageIO;
import javax.swing.JDialog;
import javax.swing.JLabel;

public class Message extends JDialog {
  JLabel lblmessage;
  private final ResourceBundle resourceBundle = Lizzie.resourceBundle;

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
    setSize((int) (message.replaceAll(regex, "12").length() * (Config.frameFontSize / 1.6)), 80);
    setLocationRelativeTo(Lizzie.frame != null ? Lizzie.frame : null);
    setVisible(true);
    Lizzie.setFrameSize(
        this, (int) (message.replaceAll(regex, "12").length() * (Config.frameFontSize / 1.6)), 80);
    this.setModal(true);
    setVisible(false);
    setVisible(true);
  }

  public void setMessageNoModal(String message) {
    String regex = "[\u4e00-\u9fa5]";
    lblmessage.setText(message);
    setSize((int) (message.replaceAll(regex, "12").length() * (Config.frameFontSize / 1.6)), 80);
    setLocationRelativeTo(Lizzie.frame != null ? Lizzie.frame : null);
    setVisible(true);
    Lizzie.setFrameSize(
        this, (int) (message.replaceAll(regex, "12").length() * (Config.frameFontSize / 1.6)), 80);
    setVisible(false);
    setVisible(true);
  }

  public void setMessageNoModal(String message, int seconds) {
    String regex = "[\u4e00-\u9fa5]";
    lblmessage.setText(message);
    setSize((int) (message.replaceAll(regex, "12").length() * (Config.frameFontSize / 1.6)), 80);
    setLocationRelativeTo(Lizzie.frame != null ? Lizzie.frame : null);
    setVisible(true);
    Lizzie.setFrameSize(
        this, (int) (message.replaceAll(regex, "12").length() * (Config.frameFontSize / 1.6)), 80);
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

  public void setMessage(String message, Window owner) {
    // TODO Auto-generated method stub
    String regex = "[\u4e00-\u9fa5]";
    lblmessage.setText(message);
    setSize((int) (message.replaceAll(regex, "12").length() * (Config.frameFontSize / 1.6)), 80);
    setLocationRelativeTo(owner);
    setVisible(true);
    Lizzie.setFrameSize(
        this, (int) (message.replaceAll(regex, "12").length() * (Config.frameFontSize / 1.6)), 80);
    setVisible(false);
    setVisible(true);
  }
}
