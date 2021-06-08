package featurecat.lizzie.gui;

import featurecat.lizzie.Lizzie;
import java.awt.Desktop;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JTextArea;

public class ShareMessage extends JDialog {
  JLabel lblUrl;
  JTextArea lblmessage;
  JButton copy;
  JButton close;
  JButton open;
  JButton openHtml;
  String links = "";

  public ShareMessage() {
    this.setModal(true);
    //  setType(Type.POPUP);
    setTitle(Lizzie.resourceBundle.getString("ShareMessage.title")); // ("分享成功");
    setAlwaysOnTop(true);
    setLocationByPlatform(true);
    lblmessage = new JTextArea("");
    lblmessage.setLineWrap(true); // 激活自动换行功能
    lblmessage.setWrapStyleWord(true);
    copy = new JButton(Lizzie.resourceBundle.getString("ShareMessage.copy")); // ("复制链接");
    open = new JButton(Lizzie.resourceBundle.getString("ShareMessage.open")); // ("直接打开");
    openHtml = new JButton(Lizzie.resourceBundle.getString("ShareMessage.openHtml")); // ("网页打开");
    openHtml.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent arg0) {
            Desktop desktop = Desktop.getDesktop();
            try {
              desktop.browse(new URI(links));
            } catch (IOException e1) {
              // TODO Auto-generated catch block
              e1.printStackTrace();
            } catch (URISyntaxException e1) {
              // TODO Auto-generated catch block
              e1.printStackTrace();
            }
          }
        });
    close = new JButton(Lizzie.resourceBundle.getString("ShareMessage.close")); // ("关闭");
    lblUrl = new JLabel(Lizzie.resourceBundle.getString("ShareMessage.lblUrl")); // ("链接:");
    lblUrl.setBounds(13, 5, 35, 25);
    close.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            setVisible(false);
          }
        });
    open.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            Lizzie.frame.bowser(links, "Lizzie Player", false);
          }
        });

    copy.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            Transferable transferableString = new StringSelection(links);
            clipboard.setContents(transferableString, null);
          }
        });
    getContentPane().add(lblmessage);
    getContentPane().setLayout(null);
    getContentPane().add(lblUrl);
    getContentPane().add(copy);
    getContentPane().add(openHtml);
    getContentPane().add(close);
    getContentPane().add(open);
    copy.setFocusable(false);
    close.setFocusable(false);
    open.setFocusable(false);
    openHtml.setFocusable(false);
    try {
      this.setIconImage(ImageIO.read(MoreEngines.class.getResourceAsStream("/assets/logo.png")));
    } catch (IOException e) {
      e.printStackTrace();
    }
    // setBounds(0, 0, 673, 166);
    Lizzie.setFrameSize(this, 673, 166);
    lblmessage.setBounds(48, 6, 602, 88);
    copy.setBounds(150, 102, 80, 20);
    open.setBounds(245, 102, 80, 20);
    close.setBounds(435, 102, 80, 20);
    openHtml.setBounds(340, 102, 80, 20);
  }

  public void setMessage(String message) {
    //    String regex = "[\u4e00-\u9fa5]";
    lblmessage.setText(message);
    links = message;
    //    int width = message.replaceAll(regex, "12").length() * 8;
    //    setBounds(0, 0, width, 100);
    //    lblmessage.setBounds(50, 0, 400, 60);
    //    copy.setBounds(400 / 2 - 180, 30, 80, 20);
    //    open.setBounds(400 / 2 - 80, 30, 80, 20);
    //    close.setBounds(400 / 2 + 20, 30, 60, 20);
    setLocationRelativeTo(getOwner());
  }
}
