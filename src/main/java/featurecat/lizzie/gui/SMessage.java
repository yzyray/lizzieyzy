package featurecat.lizzie.gui;

import featurecat.lizzie.Config;
import featurecat.lizzie.Lizzie;
import java.awt.Font;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.JDialog;
import javax.swing.JTextArea;

public class SMessage extends JDialog {
  JTextArea lblMessage;

  public SMessage() {
    this.setModal(true);
    this.setResizable(false);
    // setType(Type.POPUP);
    setTitle(Lizzie.resourceBundle.getString("Message.title"));
    setAlwaysOnTop(true);
    setLocationByPlatform(true);
    lblMessage = new JTextArea("");
    lblMessage.setBackground(this.getBackground());
    lblMessage.setLineWrap(true);
    lblMessage.setFont(new Font("", Font.PLAIN, Config.frameFontSize));
    this.add(lblMessage);
    try {
      this.setIconImage(ImageIO.read(MoreEngines.class.getResourceAsStream("/assets/logo.png")));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void setMessage(String message, int rows) {
    String regex = "[\u4e00-\u9fa5]";
    lblMessage.setText(message);
    String[] rowlines = message.split("\r\n");
    int width = 100;
    for (int i = 0; i < rowlines.length; i++) {
      width =
          Math.max(
              width,
              (int) (rowlines[i].replaceAll(regex, "12").length() * (Config.frameFontSize / 1.6)));
    }
    int height = 80 + (rows - 1) * Config.menuHeight;
    setSize(width, height);
    setLocationRelativeTo(null);
    setVisible(true);
    Lizzie.setFrameSize(this, width, height);
    this.setModal(true);
    setVisible(false);
  }

  public void setMessage(String message) {
    String regex = "[\u4e00-\u9fa5]";
    lblMessage.setText(message);
    setSize((int) (message.replaceAll(regex, "12").length() * (Config.frameFontSize / 1.6)), 80);
    setLocationRelativeTo(null);
    setVisible(true);
    Lizzie.setFrameSize(
        this, (int) (message.replaceAll(regex, "12").length() * (Config.frameFontSize / 1.6)), 80);
    this.setModal(true);
    setVisible(false);
  }
}
