package featurecat.lizzie.gui;

import featurecat.lizzie.Lizzie;
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

public class FastCommands extends JDialog {
  private JPanel dialogPane = new JPanel();
  private JPanel contentPanel = new JPanel();
  private JScrollPane scrollPane;
  private JPanel buttonBar = new JPanel();

  private JButton btnReset;
  private JButton btnClose;

  public FastCommands(Window owner) {
    super(owner);
    initComponents();
    try {
      this.setIconImage(ImageIO.read(AnalysisFrame.class.getResourceAsStream("/assets/logo.png")));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private void initComponents() {
    setMinimumSize(new Dimension(100, 100));
    setResizable(true);
    setTitle(Lizzie.resourceBundle.getString("FastCommands.title"));

    Container contentPane = getContentPane();
    contentPane.setLayout(new BorderLayout());

    initDialogPane(contentPane);
    initButtonBar();
    addComponentListener(
        new ComponentAdapter() {
          @Override
          public void componentResized(ComponentEvent e) {
            Lizzie.config.fastCommandsWidth = scrollPane.getWidth();
            Lizzie.config.fastCommandsHeight = scrollPane.getHeight();
          }
        });
    pack();
    int thisWidth = getContentPane().getWidth();
    int gtpX = getOwner().getX();
    int gtpY = getOwner().getY();
    int gtpWidth = Lizzie.gtpConsole.getContentPane().getWidth();
    Dimension screensize = Toolkit.getDefaultToolkit().getScreenSize();
    int screenWidth = screensize.width;
    if (gtpX - thisWidth >= 0) setLocation(gtpX - thisWidth, gtpY);
    else if (gtpX + gtpWidth + thisWidth <= screenWidth) setLocation(gtpX + gtpWidth, gtpY);
    else setLocation(screenWidth - thisWidth, gtpY);
    // setLocationRelativeTo(getOwner());

  }

  private void initDialogPane(Container contentPane) {
    dialogPane.setBorder(new EmptyBorder(2, 5, 2, 5));
    dialogPane.setLayout(new BorderLayout());

    initContentPanel();

    this.addWindowListener(
        new WindowAdapter() {
          public void windowClosing(WindowEvent e) {
            closeWindow();
          }
        });

    contentPane.add(dialogPane, BorderLayout.CENTER);
  }

  private void closeWindow() {
    // TODO Auto-generated method stub
    Lizzie.config.fastCommandsWidth = scrollPane.getWidth();
    Lizzie.config.fastCommandsHeight = scrollPane.getHeight();
    setVisible(false);
  }

  private void initContentPanel() {
    List<String> commands = Lizzie.leelaz.commandLists;
    GridLayout gridLayout = new GridLayout(commands.size(), 1, 0, 0);
    contentPanel.setLayout(gridLayout);
    for (String cmd : commands) {
      JPanel textPanel = new JPanel();
      JTextField txtCmd = new JFontTextField();
      txtCmd.setText(cmd);
      txtCmd.setColumns(15);

      textPanel.setLayout(new BorderLayout());
      textPanel.add(txtCmd, BorderLayout.CENTER);
      JPanel buttonPanel = new JPanel();
      JButton copy =
          new JFontButton(
              Lizzie.resourceBundle.getString("JTextPane.copy")); // 同时复制到剪贴板和gtp窗口的txtcommands
      copy.setMargin(new Insets(0, 5, 0, 5));
      copy.addActionListener(
          new ActionListener() {
            public void actionPerformed(ActionEvent e) {
              String command = txtCmd.getText();
              Lizzie.gtpConsole.txtCommand.setText(command);
              Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
              Transferable transferableString = new StringSelection(command);
              clipboard.setContents(transferableString, null);
            }
          });
      JButton send = new JFontButton(Lizzie.resourceBundle.getString("GtpConsolePane.send"));
      send.addActionListener(
          new ActionListener() {
            public void actionPerformed(ActionEvent e) {
              String command = txtCmd.getText();
              Lizzie.gtpConsole.txtCommand.setText(command);
              Lizzie.gtpConsole.send.doClick();
            }
          });
      send.setMargin(new Insets(0, 5, 0, 5));
      buttonPanel.setLayout(new BorderLayout());
      buttonPanel.add(copy, BorderLayout.WEST);
      buttonPanel.add(send, BorderLayout.EAST);
      textPanel.add(buttonPanel, BorderLayout.EAST);
      contentPanel.add(textPanel);
    }
    scrollPane = new JScrollPane(contentPanel);
    scrollPane.setPreferredSize(
        new Dimension(
            Lizzie.config.fastCommandsWidth, Lizzie.config.fastCommandsHeight)); // 记忆大小到persisit
    scrollPane.getVerticalScrollBar().setUnitIncrement(16);
    dialogPane.add(scrollPane, BorderLayout.CENTER);
  }

  private void initButtonBar() {
    buttonBar.setLayout(new GridBagLayout());
    buttonBar.setBorder(new EmptyBorder(2, 0, 0, 0));
    btnReset = new JFontButton(Lizzie.resourceBundle.getString("FastCommands.reset"));
    GridBagConstraints gbc_button = new GridBagConstraints();
    gbc_button.anchor = GridBagConstraints.EAST;
    gbc_button.insets = new Insets(0, 0, 0, 5);
    gbc_button.gridx = 0;
    gbc_button.gridy = 0;
    buttonBar.add(btnReset, gbc_button);
    btnReset.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            closeWindow();
            Lizzie.gtpConsole.openCommands();
          }
        });

    dialogPane.add(buttonBar, BorderLayout.SOUTH);

    btnClose = new JFontButton(Lizzie.resourceBundle.getString("LizzieFrame.saveAndLoad.close"));
    GridBagConstraints gbc_button_1 = new GridBagConstraints();
    gbc_button_1.anchor = GridBagConstraints.EAST;
    gbc_button_1.gridx = 1;
    gbc_button_1.gridy = 0;
    buttonBar.add(btnClose, gbc_button_1);
    btnClose.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            closeWindow();
          }
        });
  }
}
