package featurecat.lizzie.gui;

import featurecat.lizzie.Config;
import featurecat.lizzie.Lizzie;
import java.awt.Color;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JRootPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;

public class EngineFailedMessage extends JDialog {
  public EngineFailedMessage(
      List<String> commands,
      String command,
      String message,
      boolean canUseCmdDignostic,
      boolean isGtpEngine) {
    // this.setModal(true);
    // setType(Type.POPUP);
    setTitle(Lizzie.resourceBundle.getString("Leelaz.engineFailed")); // "消息提醒");
    setAlwaysOnTop(true);
    try {
      this.setIconImage(ImageIO.read(getClass().getResourceAsStream("/assets/logo.png")));
    } catch (IOException e) {
      e.printStackTrace();
    }
    getContentPane().setLayout(null);

    JLabel lblEngineFaied = new JFontLabel(message);
    getContentPane().add(lblEngineFaied);
    String regex = "[\u4e00-\u9fa5]";
    lblEngineFaied.setBounds(
        10,
        9,
        (int)
            (lblEngineFaied.getText().replaceAll(regex, "12").length()
                * (Config.frameFontSize / 1.9)),
        20);
    Lizzie.setFrameSize(
        this,
        Math.max(
            Lizzie.config.isFrameFontSmall()
                ? 580
                : (Lizzie.config.isFrameFontMiddle() ? 660 : 730),
            (int)
                (lblEngineFaied.getText().replaceAll(regex, "12").length()
                    * (Config.frameFontSize / 1.9))),
        canUseCmdDignostic ? 190 : 160);

    JTextArea engineCmd = new JTextArea();
    engineCmd.setLineWrap(true);
    engineCmd.setFont(new Font(Config.sysDefaultFontName, Font.PLAIN, Config.frameFontSize));
    engineCmd.setText(command);

    JScrollPane scrollPane = new JScrollPane(engineCmd);
    scrollPane.setBounds(
        Lizzie.config.isFrameFontSmall() ? 72 : (Lizzie.config.isFrameFontMiddle() ? 90 : 110),
        40,
        Lizzie.config.isFrameFontSmall() ? 476 : (Lizzie.config.isFrameFontMiddle() ? 550 : 600),
        70);
    getContentPane().add(scrollPane);

    JLabel lblEngineCmd =
        new JFontLabel(Lizzie.resourceBundle.getString("EngineFailedMessage.engineCmd"));
    lblEngineCmd.setBounds(10, 40, 120, 20);
    getContentPane().add(lblEngineCmd);

    if (canUseCmdDignostic) {
      JButton btnRunInCmd =
          new JFontButton(Lizzie.resourceBundle.getString("EngineFailedMessage.btnRunInCmd"));
      btnRunInCmd.setForeground(Color.RED);
      btnRunInCmd.addActionListener(
          new ActionListener() {
            public void actionPerformed(ActionEvent e) {
              try {
                BufferedWriter bw =
                    new BufferedWriter(
                        new OutputStreamWriter(new FileOutputStream("dignostic.bat"), "UTF-8"));
                if (isGtpEngine) {
                  bw.write("CHCP 65001");
                  bw.newLine();
                  bw.write(
                      "@echo " + Lizzie.resourceBundle.getString("EngineFailedMessage.batTips"));
                  bw.newLine();
                }
                if (commands != null && commands.size() > 1) {
                  bw.write(
                      "\""
                          + commands.get(0).trim()
                          + "\""
                          + " "
                          + command.substring(command.indexOf(commands.get(1))).trim());
                } else if (commands.size() == 1) {
                  bw.write("\"" + commands.get(0).trim() + "\"");
                } else bw.write(command.trim());
                if (isGtpEngine) {
                  bw.write(" < test_commands.txt");
                  BufferedWriter bw2 = new BufferedWriter(new FileWriter("test_commands.txt"));
                  bw2.write("name");
                  bw2.newLine();
                  bw2.write("version");
                  bw2.newLine();
                  bw2.write("time_settings 0 2 1");
                  bw2.newLine();
                  bw2.write("genmove b");
                  bw2.newLine();
                  bw2.write("lz-genmove_analyze w 100");
                  bw2.newLine();
                  bw2.write("showboard");
                  bw2.newLine();
                  bw2.close();
                }
                bw.newLine();
                bw.write("pause");
                bw.newLine();
                bw.close();
                Runtime.getRuntime().exec("powershell /c start dignostic.bat");
              } catch (IOException s) {
                // TODO Auto-generated catch block
                s.printStackTrace();
              }
            }
          });
      btnRunInCmd.setFocusPainted(false);
      btnRunInCmd.setMargin(new Insets(0, 0, 0, 0));
      btnRunInCmd.setContentAreaFilled(false);
      btnRunInCmd.setBounds(
          Lizzie.config.isFrameFontSmall() ? 40 : (Lizzie.config.isFrameFontMiddle() ? 45 : 50),
          Lizzie.config.isFrameFontSmall() ? 120 : (Lizzie.config.isFrameFontMiddle() ? 120 : 121),
          Lizzie.config.isFrameFontSmall() ? 41 : (Lizzie.config.isFrameFontMiddle() ? 50 : 60),
          19);
      getContentPane().add(btnRunInCmd);

      JLabel lblClick =
          new JFontLabel(Lizzie.resourceBundle.getString("EngineFailedMessage.lblClick"));
      lblClick.setBounds(13, 119, 45, 20);
      getContentPane().add(lblClick);

      JLabel lblRunInCmd =
          new JFontLabel(Lizzie.resourceBundle.getString("EngineFailedMessage.lblRunInCmd"));
      lblRunInCmd.setBounds(
          Lizzie.config.isFrameFontSmall() ? 85 : (Lizzie.config.isFrameFontMiddle() ? 95 : 108),
          119,
          332,
          20);
      getContentPane().add(lblRunInCmd);
    }

    JRootPane rp = this.getRootPane();
    KeyStroke stroke = KeyStroke.getKeyStroke(KeyEvent.VK_E, 0);
    InputMap inputMap = rp.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
    inputMap.put(stroke, KeyEvent.VK_E);
    rp.getActionMap()
        .put(
            KeyEvent.VK_E,
            new AbstractAction() {
              public void actionPerformed(ActionEvent e) {
                Lizzie.frame.toggleGtpConsole();
              }
            });

    setLocationRelativeTo(Lizzie.frame != null ? Lizzie.frame : null);
    setVisible(true);
    setVisible(false);
  }
}
