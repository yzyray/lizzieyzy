package featurecat.lizzie.gui;

import featurecat.lizzie.Lizzie;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ResourceBundle;
import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.UIManager;

public class CheckVersion extends JDialog {
  JDialog dialog = this;
  private final ResourceBundle resourceBundle = Lizzie.resourceBundle;

  public CheckVersion(boolean hasNewVersion, String remoteVersion, String newVersionDis) {
    this.setModal(true);
    // setType(Type.POPUP);
    setTitle(resourceBundle.getString("CheckVersion.titile"));
    setAlwaysOnTop(true);
    setResizable(false);
    if (Lizzie.frame != null) setLocationRelativeTo(Lizzie.frame);

    Lizzie.setFrameSize(this, 471, 522);
    // setSize(471, 522);

    try {
      this.setIconImage(ImageIO.read(MoreEngines.class.getResourceAsStream("/assets/logo.png")));
      Dimension screensize = Toolkit.getDefaultToolkit().getScreenSize();
      int x = (int) screensize.getWidth() / 2 - this.getWidth() / 2;
      int y = (int) screensize.getHeight() / 2 - this.getHeight() / 2;
      setLocation(x, y);
    } catch (IOException e1) {
      // TODO Auto-generated catch block
      e1.printStackTrace();
    }

    getContentPane().setLayout(null);

    JScrollPane scrollPane = new JScrollPane();
    scrollPane.setBounds(0, 129, 465, 364);
    getContentPane().add(scrollPane);

    JTextPane textPane = new JTextPane();
    textPane.setBackground(Color.WHITE);
    newVersionDis = newVersionDis.replaceAll("\\\\r\\\\n", "\r\n");
    textPane.setText(resourceBundle.getString("CheckVersion.newVersion") + newVersionDis);
    scrollPane.setViewportView(textPane);
    textPane.setCaretPosition(0);

    JLabel label =
        new JLabel(resourceBundle.getString("CheckVersion.currentVersion") + Lizzie.checkVersion);
    label.setBounds(5, 5, 341, 15);
    getContentPane().add(label);

    JLabel label_1 =
        new JLabel(resourceBundle.getString("CheckVersion.newestVersion") + remoteVersion);
    label_1.setBounds(5, 25, 341, 15);
    getContentPane().add(label_1);

    JLabel lblUpdateHint = new JLabel(resourceBundle.getString("CheckVersion.newVersionHint"));
    lblUpdateHint.setForeground(Color.RED);
    lblUpdateHint.setBounds(160, 5, 305, 34);
    lblUpdateHint.setFont(new Font("宋体", Font.PLAIN, 20));
    if (!hasNewVersion) {
      lblUpdateHint.setText(resourceBundle.getString("CheckVersion.noNewVersionHint"));
    }
    getContentPane().add(lblUpdateHint);

    JScrollPane scrollPane_1 = new JScrollPane();
    scrollPane_1.setBounds(0, 63, 465, 68);
    getContentPane().add(scrollPane_1);

    JTextPane txtpnhttpspanbaiducomsqghdfmnzbtyfcxa = new JTextPane();
    txtpnhttpspanbaiducomsqghdfmnzbtyfcxa.setBackground(UIManager.getColor("Button.background"));
    txtpnhttpspanbaiducomsqghdfmnzbtyfcxa.setText(
        resourceBundle.getString("CheckVersion.download")
            + "https://aistudio.baidu.com/aistudio/datasetdetail/116865"
            + resourceBundle.getString("CheckVersion.download2"));
    scrollPane_1.setViewportView(txtpnhttpspanbaiducomsqghdfmnzbtyfcxa);
    txtpnhttpspanbaiducomsqghdfmnzbtyfcxa.setCaretPosition(0);

    JCheckBox checkBox = new JCheckBox(resourceBundle.getString("CheckVersion.checkEveryDay"));
    checkBox.setBounds(2, 40, 117, 23);
    getContentPane().add(checkBox);
    checkBox.setSelected(Lizzie.config.autoCheckVersion);
    checkBox.setFocusable(false);

    JButton btnNewButton = new JButton(resourceBundle.getString("CheckVersion.copyDownload"));
    btnNewButton.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            try {
              Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
              Transferable transferableString =
                  new StringSelection("https://aistudio.baidu.com/aistudio/datasetdetail/116865");
              clipboard.setContents(transferableString, null);
              JOptionPane.showMessageDialog(
                  dialog, resourceBundle.getString("CheckVersion.copySuccess"));
            } catch (Exception ex) {
              JOptionPane.showMessageDialog(
                  dialog, resourceBundle.getString("CheckVersion.copyFailed"));
            }
          }
        });
    btnNewButton.setBounds(238, 40, 105, 23);
    getContentPane().add(btnNewButton);
    btnNewButton.setMargin(new Insets(0, 0, 0, 0));

    JButton btnqq = new JButton(resourceBundle.getString("CheckVersion.copyQQgroup"));
    btnqq.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            try {
              Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
              Transferable transferableString = new StringSelection("867298807");
              clipboard.setContents(transferableString, null);
              JOptionPane.showMessageDialog(
                  dialog, resourceBundle.getString("CheckVersion.copySuccess"));
            } catch (Exception ex) {
              JOptionPane.showMessageDialog(
                  dialog, resourceBundle.getString("CheckVersion.copyFailed"));
            }
          }
        });
    btnqq.setBounds(350, 40, 105, 23);
    getContentPane().add(btnqq);
    btnqq.setMargin(new Insets(0, 0, 0, 0));

    JButton btnIgnore = new JButton(resourceBundle.getString("CheckVersion.ignore"));
    btnIgnore.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            setVisible(false);
            Lizzie.config.ignoreVersion = Integer.parseInt(remoteVersion);
            Lizzie.config.uiConfig.put("ignore-version", Lizzie.config.ignoreVersion);
          }
        });
    btnIgnore.setBounds(125, 40, 106, 23);
    btnIgnore.setMargin(new Insets(0, 0, 0, 0));
    getContentPane().add(btnIgnore);
    checkBox.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            // TBD
            if (checkBox.isSelected()) {
              Lizzie.config.autoCheckVersion = true;
            } else {
              Lizzie.config.autoCheckVersion = false;
            }
            Lizzie.config.uiConfig.put("auto-check-version", Lizzie.config.autoCheckVersion);
          }
        });
  }
}
