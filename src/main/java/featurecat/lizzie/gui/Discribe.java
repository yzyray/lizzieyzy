package featurecat.lizzie.gui;

import featurecat.lizzie.Config;
import featurecat.lizzie.Lizzie;
import java.awt.Font;
import java.awt.Window;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.JDialog;
import javax.swing.JTextArea;

public class Discribe extends JDialog {
  JTextArea textAreaDiscribe;

  public Discribe() {

    // setResizable(false);

    setAlwaysOnTop(true);

    // getContentPane().setLayout(null);
    textAreaDiscribe = new JTextArea();
    textAreaDiscribe.setFont(new Font(Config.sysDefaultFontName, Font.PLAIN, Config.frameFontSize));
    textAreaDiscribe.setEditable(false);
    textAreaDiscribe.setLineWrap(true);
    textAreaDiscribe.setBackground(this.getBackground());
    getContentPane().add(textAreaDiscribe);
    try {
      this.setIconImage(ImageIO.read(MoreEngines.class.getResourceAsStream("/assets/logo.png")));

    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void setInfo(String message, String title, Window owner) {
    textAreaDiscribe.setText(message);
    setTitle(title);
    // setSize(441, 272);
    setVisible(true);
    // setSize(441, 273);
    if (Lizzie.config.isFrameFontSmall()) Lizzie.setFrameSize(this, 441, 273);
    else if (Lizzie.config.isFrameFontMiddle()) Lizzie.setFrameSize(this, 541, 313);
    else Lizzie.setFrameSize(this, 741, 373);
    this.setModal(true);

    setLocationRelativeTo(owner);
    setVisible(false);
    setVisible(true);
  }

  public void setInfoWide(String message, String title, Window owner) {
    textAreaDiscribe.setText(message);
    setTitle(title);
    // setSize(441, 272);
    setVisible(true);
    // setSize(441, 273);
    if (Lizzie.config.isFrameFontSmall()) Lizzie.setFrameSize(this, 541, 200);
    else if (Lizzie.config.isFrameFontMiddle()) Lizzie.setFrameSize(this, 641, 243);
    else Lizzie.setFrameSize(this, 801, 303);
    setLocationRelativeTo(owner);
    setVisible(false);
    setModal(true);
    setVisible(true);
  }

  public void setInfo(String message, String title, int width, int height, Window owner) {
    textAreaDiscribe.setText(message);
    setTitle(title);
    setSize(width, height);

    setVisible(true);
    // setSize(441, 273);
    Lizzie.setFrameSize(this, width, height);
    this.setModal(true);
    setLocationRelativeTo(owner);
    setVisible(false);
    setVisible(true);
  }
}
