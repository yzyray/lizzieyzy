package featurecat.lizzie.gui;

import featurecat.lizzie.Config;
import featurecat.lizzie.Lizzie;
import java.awt.Font;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.JDialog;
import javax.swing.JTextArea;

public class SMessage extends JDialog {
  JTextArea lblmessage;

  public SMessage() {
    this.setModal(true);
    this.setResizable(false);
    // setType(Type.POPUP);
    setTitle(Lizzie.resourceBundle.getString("Message.title"));
    setAlwaysOnTop(true);
    setLocationByPlatform(true);
    lblmessage = new JTextArea("");
    lblmessage.setFont(new Font("", Font.PLAIN, Config.frameFontSize));
    this.add(lblmessage);
    try {
      this.setIconImage(ImageIO.read(MoreEngines.class.getResourceAsStream("/assets/logo.png")));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void setMessage(String message, int rows) {
    String regex = "[\u4e00-\u9fa5]";
    lblmessage.setText(message);
    setSize(
        (int) (message.replaceAll(regex, "12").length() * (Lizzie.config.frameFontSize / 1.6)),
        80 + (rows - 1) * Lizzie.config.menuHeight);
    setLocationRelativeTo(null);
    setVisible(true);
    Lizzie.setFrameSize(
        this,
        (int) (message.replaceAll(regex, "12").length() * (Lizzie.config.frameFontSize / 1.6)),
        80 + (rows - 1) * Lizzie.config.menuHeight);
    this.setModal(true);
    setVisible(false);
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
  }
}
