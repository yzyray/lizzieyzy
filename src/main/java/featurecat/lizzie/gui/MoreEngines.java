package featurecat.lizzie.gui;

import featurecat.lizzie.Config;
import featurecat.lizzie.Lizzie;
import featurecat.lizzie.util.Utils;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FileDialog;
import java.awt.Font;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;

public class MoreEngines extends JPanel {
  public static Config config;
  public TableModel dataModel;
  JPanel tablepanel;
  JPanel selectpanel = new JPanel();
  JScrollPane scrollpane;
  public static JTable table;
  Font headFont;
  Font winrateFont;
  static boolean needUpdateEngine = false;
  static JDialog engjf;
  Timer timer;
  int sortnum = 3;
  public static int selectedorder = -1;
  boolean issorted = false;
  JTextArea command;
  JFontTextField txtName;
  JFontLabel engineName;
  JFontTextField txtInitialCommand;
  JFontTextField txtWidth;
  JFontTextField txtHeight;
  JFontTextField txtKomi;
  JFontButton exit;
  JFontButton scan;
  JFontButton delete;
  JFontButton add;
  JFontButton save;
  JFontButton cancel;
  JFontButton moveUp;
  JFontButton moveDown;
  JFontButton moveUp5;
  JFontButton moveDown5;
  JFontButton moveFirst;
  JFontButton moveLast;
  JFontCheckBox preload;
  JFontCheckBox chkDefault;
  JFontRadioButton rdoDefault;
  JFontRadioButton rdoLast;
  JFontRadioButton rdoMannul;
  JFontRadioButton rdoNone;
  int curIndex = -1;
  String keyGenPath = "";
  JFontCheckBox chkRemoteEngine;
  JFontRadioButton rdoUsePassword;
  JFontRadioButton rdoKeyGen;
  JFontTextField txtIP;
  JFontTextField txtPort;
  JFontTextField txtUserName;
  JPasswordField txtPassword;
  JFontButton scanKeygen;

  private final ResourceBundle resourceBundle = Lizzie.resourceBundle;

  public MoreEngines() {
    setLayout((LayoutManager) null);
    this.dataModel = getTableModel();
    table = new JTable(this.dataModel);
    this.selectpanel.setLayout((LayoutManager) null);
    this.winrateFont = new Font("Microsoft YaHei", 0, Math.max(Config.frameFontSize, 14));
    this.headFont = new Font("Microsoft YaHei", 0, Math.max(Config.frameFontSize, 13));
    table.getTableHeader().setFont(this.headFont);
    table.setFont(this.winrateFont);
    table.getTableHeader().setReorderingAllowed(false);
    table.getTableHeader().setResizingAllowed(false);
    TableCellRenderer tcr = new ColorTableCellRenderer();
    table.setDefaultRenderer(Object.class, tcr);
    this.tablepanel = new JPanel(new BorderLayout());
    this.tablepanel.setBounds(0, 385, 885, 380);
    add(this.tablepanel, "South");
    this.selectpanel.setBounds(0, 0, 900, 385);
    add(this.selectpanel, "North");
    this.scrollpane = new JScrollPane(table);
    this.tablepanel.add(this.scrollpane);
    table.setSelectionMode(0);
    table.setFillsViewportHeight(true);
    table.getColumnModel().getColumn(0).setPreferredWidth(30);
    table.getColumnModel().getColumn(1).setPreferredWidth(235);
    table.getColumnModel().getColumn(2).setPreferredWidth(305);
    table.getColumnModel().getColumn(3).setPreferredWidth(40);
    table.getColumnModel().getColumn(4).setPreferredWidth(20);
    table.getColumnModel().getColumn(5).setPreferredWidth(20);
    table.getColumnModel().getColumn(6).setPreferredWidth(30);
    table.getColumnModel().getColumn(7).setPreferredWidth(30);
    table.getColumnModel().getColumn(8).setPreferredWidth(30);
    table.setRowHeight(Config.menuHeight);
    table.getTableHeader().setFont(this.headFont);
    table
        .getTableHeader()
        .setPreferredSize(
            new Dimension(table.getColumnModel().getTotalColumnWidth(), Config.menuHeight));
    table.setFont(this.winrateFont);
    this.engineName = new JFontLabel(this.resourceBundle.getString("MoreEngines.engineName"));
    this.engineName.setForeground(Color.BLUE);
    this.engineName.setFont(new Font("Microsoft YaHei", 0, 14));
    JFontLabel lblName = new JFontLabel(this.resourceBundle.getString("MoreEngines.lblName"));
    this.txtName = new JFontTextField();
    this.txtName.setFont(new Font(Config.sysDefaultFontName, 0, Config.frameFontSize));
    this.txtKomi = new JFontTextField();
    JFontLabel lblInitialCommand =
        new JFontLabel(resourceBundle.getString("MoreEngines.lblInitialCommand"));
    txtInitialCommand = new JFontTextField();
    txtInitialCommand.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
    txtInitialCommand.setForeground(Color.GRAY);
    txtInitialCommand.setText(resourceBundle.getString("MoreEngines.initialCommandHint"));
    txtInitialCommand.addFocusListener(
        new FocusListener() {
          @Override
          public void focusGained(FocusEvent e) {
            if (resourceBundle
                .getString("MoreEngines.initialCommandHint")
                .equalsIgnoreCase(txtInitialCommand.getText())) {
              txtInitialCommand.setForeground(Color.BLACK);
              txtInitialCommand.setText("");
            }
          }

          @Override
          public void focusLost(FocusEvent e) {
            if ("".equals(txtInitialCommand.getText())) {
              txtInitialCommand.setForeground(Color.GRAY);
              txtInitialCommand.setText(resourceBundle.getString("MoreEngines.initialCommandHint"));
            }
          }
        });
    this.command = new JFontTextArea(5, 80);
    command.setBackground(this.getBackground());

    this.command.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
    this.txtName.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
    JFontLabel lblCommand = new JFontLabel(this.resourceBundle.getString("MoreEngines.lblCommand"));
    this.preload = new JFontCheckBox(this.resourceBundle.getString("MoreEngines.lblpreload"));
    JFontLabel lblWidth = new JFontLabel(this.resourceBundle.getString("MoreEngines.lblWidth"));
    JFontLabel lblHeight = new JFontLabel(this.resourceBundle.getString("MoreEngines.lblHeight"));
    JFontLabel lblKomi = new JFontLabel(this.resourceBundle.getString("MoreEngines.lblKomi"));
    this.txtWidth = new JFontTextField();
    this.txtHeight = new JFontTextField();
    this.add = new JFontButton(this.resourceBundle.getString("MoreEngines.add"));
    this.save = new JFontButton(this.resourceBundle.getString("MoreEngines.save"));
    this.cancel = new JFontButton(this.resourceBundle.getString("MoreEngines.cancel"));
    this.exit = new JFontButton(this.resourceBundle.getString("MoreEngines.exit"));
    this.delete = new JFontButton(this.resourceBundle.getString("MoreEngines.delete"));
    this.scan = new JFontButton(this.resourceBundle.getString("MoreEngines.scan"));
    this.moveUp = new JFontButton(this.resourceBundle.getString("MoreEngines.moveUp"));
    this.moveDown = new JFontButton(this.resourceBundle.getString("MoreEngines.moveDown"));
    this.moveUp5 = new JFontButton(this.resourceBundle.getString("MoreEngines.moveUp5"));
    this.moveDown5 = new JFontButton(this.resourceBundle.getString("MoreEngines.moveDown5"));
    this.moveFirst = new JFontButton(this.resourceBundle.getString("MoreEngines.moveFirst"));
    this.moveLast = new JFontButton(this.resourceBundle.getString("MoreEngines.moveLast"));
    this.moveUp.setFocusable(false);
    this.moveUp.setMargin(new Insets(0, 0, 0, 0));
    this.moveDown.setFocusable(false);
    this.moveDown.setMargin(new Insets(0, 0, 0, 0));
    this.moveUp5.setFocusable(false);
    this.moveUp5.setMargin(new Insets(0, 0, 0, 0));
    this.moveDown5.setFocusable(false);
    this.moveDown5.setMargin(new Insets(0, 0, 0, 0));
    this.moveFirst.setFocusable(false);
    this.moveFirst.setMargin(new Insets(0, 0, 0, 0));
    this.moveLast.setFocusable(false);
    this.moveLast.setMargin(new Insets(0, 0, 0, 0));
    this.scan.setFocusable(false);
    this.scan.setMargin(new Insets(0, 0, 0, 0));
    this.add.setFocusable(false);
    this.add.setMargin(new Insets(0, 0, 0, 0));
    this.save.setFocusable(false);
    this.save.setMargin(new Insets(0, 0, 0, 0));
    this.cancel.setFocusable(false);
    this.cancel.setMargin(new Insets(0, 0, 0, 0));
    this.exit.setFocusable(false);
    this.exit.setMargin(new Insets(0, 0, 0, 0));
    this.delete.setFocusable(false);
    this.delete.setMargin(new Insets(0, 0, 0, 0));
    this.chkDefault = new JFontCheckBox(this.resourceBundle.getString("MoreEngines.lbldefault"));
    JFontLabel lblchooseStart =
        new JFontLabel(this.resourceBundle.getString("ChooseMoreEngine.lblchooseStart"));
    this.rdoDefault =
        new JFontRadioButton(this.resourceBundle.getString("MoreEngines.lblrdoDefault"));
    this.rdoLast =
        new JFontRadioButton(this.resourceBundle.getString("ChooseMoreEngine.lblrdoLast"));
    this.rdoMannul =
        new JFontRadioButton(this.resourceBundle.getString("ChooseMoreEngine.lblrdoMannul"));
    rdoNone = new JFontRadioButton(resourceBundle.getString("ChooseMoreEngine.lblrdoNone"));
    this.chkRemoteEngine =
        new JFontCheckBox(this.resourceBundle.getString("MoreEngines.chkRemoteEngine"));
    this.chkRemoteEngine.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            if (chkRemoteEngine.isSelected()) {
              txtIP.setEnabled(true);
              txtPort.setEnabled(true);
              rdoUsePassword.setEnabled(true);
              rdoKeyGen.setEnabled(true);
              txtUserName.setEnabled(true);
              if (rdoUsePassword.isSelected()) txtPassword.setEnabled(true);
              if (rdoKeyGen.isSelected()) scanKeygen.setEnabled(true);
            } else {
              txtIP.setEnabled(false);
              txtPort.setEnabled(false);
              rdoUsePassword.setEnabled(false);
              rdoKeyGen.setEnabled(false);
              txtUserName.setEnabled(false);
              txtPassword.setEnabled(false);
              scanKeygen.setEnabled(false);
            }
          }
        });
    ImageIcon btnRemoteEngineIcon = new ImageIcon();
    try {
      btnRemoteEngineIcon.setImage(
          ImageIO.read(getClass().getResourceAsStream("/assets/settings.png")));
    } catch (IOException e) {
      e.printStackTrace();
    }
    JFontButton btnRemoteEngine = new JFontButton(btnRemoteEngineIcon);
    btnRemoteEngine.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            Discribe lizzieCacheDiscribe = new Discribe();
            lizzieCacheDiscribe.setInfo(
                resourceBundle.getString("MoreEngines.aboutRemoteEngine"),
                resourceBundle.getString("MoreEngines.aboutRemoteEngineTitle"));
          }
        });
    this.txtIP = new JFontTextField();
    this.txtPort = new JFontTextField();
    this.txtUserName = new JFontTextField();
    this.txtPassword = new JPasswordField();
    JFontLabel lblIp = new JFontLabel("IP");
    JFontLabel lblPort = new JFontLabel(this.resourceBundle.getString("MoreEngines.lblPort"));
    JFontLabel lblUserName =
        new JFontLabel(this.resourceBundle.getString("MoreEngines.rdoUserName"));
    this.rdoKeyGen = new JFontRadioButton(this.resourceBundle.getString("MoreEngines.rdoKeygen"));
    this.rdoUsePassword =
        new JFontRadioButton(this.resourceBundle.getString("MoreEngines.lblPassword"));
    this.scanKeygen = new JFontButton(this.resourceBundle.getString("MoreEngines.scanKeygen"));
    this.scanKeygen.setFocusable(false);
    this.scanKeygen.setMargin(new Insets(0, 0, 0, 0));
    this.scanKeygen.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            MoreEngines.engjf.setAlwaysOnTop(false);
            FileDialog fileDialog =
                new FileDialog(
                    MoreEngines.engjf, resourceBundle.getString("MoreEngines.chooseKeygen"));
            fileDialog.setLocationRelativeTo(MoreEngines.engjf);
            fileDialog.setAlwaysOnTop(true);
            fileDialog.setModal(true);
            fileDialog.setMultipleMode(false);
            fileDialog.setMode(0);
            fileDialog.setVisible(true);
            File[] file = fileDialog.getFiles();
            if (file.length > 0) keyGenPath = file[0].getAbsolutePath();
            scanKeygen.setToolTipText(keyGenPath);
            rdoKeyGen.setToolTipText(keyGenPath);
            MoreEngines.engjf.setAlwaysOnTop(true);
          }
        });
    this.rdoUsePassword.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            scanKeygen.setEnabled(false);
            txtPassword.setEnabled(true);
          }
        });
    this.rdoKeyGen.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            scanKeygen.setEnabled(true);
            txtPassword.setEnabled(false);
          }
        });
    ButtonGroup gourpKeyPassword = new ButtonGroup();
    gourpKeyPassword.add(rdoUsePassword);
    gourpKeyPassword.add(rdoKeyGen);

    this.selectpanel.add(lblIp);
    this.selectpanel.add(lblPort);
    this.selectpanel.add(this.rdoUsePassword);
    this.selectpanel.add(lblUserName);
    this.selectpanel.add(this.chkRemoteEngine);
    this.selectpanel.add(btnRemoteEngine);
    this.selectpanel.add(this.txtIP);
    this.selectpanel.add(this.txtPort);
    this.selectpanel.add(this.txtUserName);
    this.selectpanel.add(this.txtPassword);
    this.selectpanel.add(this.rdoKeyGen);
    this.selectpanel.add(this.scanKeygen);
    this.engineName.setBounds(5, 3, 700, 24);
    lblName.setBounds(
        5,
        32,
        Lizzie.config.isFrameFontSmall() ? 45 : (Lizzie.config.isFrameFontMiddle() ? 45 : 60),
        24);
    lblCommand.setBounds(
        5,
        60,
        Lizzie.config.isFrameFontSmall() ? 73 : (Lizzie.config.isFrameFontMiddle() ? 73 : 95),
        24);
    this.scan.setBounds(
        5,
        83,
        Lizzie.config.isFrameFontSmall() ? 60 : (Lizzie.config.isFrameFontMiddle() ? 75 : 90),
        24);
    this.txtName.setBounds(
        Lizzie.config.isFrameFontSmall() ? 68 : (Lizzie.config.isFrameFontMiddle() ? 85 : 100),
        35,
        Lizzie.config.isFrameFontSmall() ? 812 : (Lizzie.config.isFrameFontMiddle() ? 793 : 780),
        24);
    this.command.setBounds(
        Lizzie.config.isFrameFontSmall() ? 68 : (Lizzie.config.isFrameFontMiddle() ? 85 : 100),
        65,
        Lizzie.config.isFrameFontSmall() ? 812 : (Lizzie.config.isFrameFontMiddle() ? 793 : 780),
        170);
    lblInitialCommand.setBounds(
        5,
        240,
        Lizzie.config.isFrameFontSmall() ? 60 : (Lizzie.config.isFrameFontMiddle() ? 75 : 90),
        24);
    this.txtInitialCommand.setBounds(
        Lizzie.config.isFrameFontSmall() ? 68 : (Lizzie.config.isFrameFontMiddle() ? 85 : 100),
        240,
        Lizzie.config.isFrameFontSmall() ? 812 : (Lizzie.config.isFrameFontMiddle() ? 793 : 780),
        24);
    this.preload.setBounds(
        5,
        270,
        Lizzie.config.isFrameFontSmall() ? 80 : (Lizzie.config.isFrameFontMiddle() ? 80 : 95),
        24);
    this.chkDefault.setBounds(
        Lizzie.config.isFrameFontSmall() ? 85 : (Lizzie.config.isFrameFontMiddle() ? 85 : 100),
        270,
        Lizzie.config.isFrameFontSmall() ? 64 : (Lizzie.config.isFrameFontMiddle() ? 60 : 80),
        24);
    lblWidth.setBounds(
        Lizzie.config.isFrameFontSmall() ? 156 : (Lizzie.config.isFrameFontMiddle() ? 156 : 190),
        270,
        30,
        24);
    this.txtWidth.setBounds(
        Lizzie.config.isFrameFontSmall() ? 177 : (Lizzie.config.isFrameFontMiddle() ? 177 : 217),
        271,
        Lizzie.config.isFrameFontSmall() ? 30 : (Lizzie.config.isFrameFontMiddle() ? 30 : 40),
        24);
    lblHeight.setBounds(
        Lizzie.config.isFrameFontSmall() ? 213 : (Lizzie.config.isFrameFontMiddle() ? 213 : 265),
        270,
        30,
        24);
    this.txtHeight.setBounds(
        Lizzie.config.isFrameFontSmall() ? 234 : (Lizzie.config.isFrameFontMiddle() ? 234 : 294),
        271,
        Lizzie.config.isFrameFontSmall() ? 30 : (Lizzie.config.isFrameFontMiddle() ? 30 : 40),
        24);
    lblKomi.setBounds(
        Lizzie.config.isFrameFontSmall() ? 269 : (Lizzie.config.isFrameFontMiddle() ? 269 : 345),
        270,
        40,
        24);
    this.txtKomi.setBounds(
        Lizzie.config.isFrameFontSmall() ? 302 : (Lizzie.config.isFrameFontMiddle() ? 302 : 393),
        271,
        Lizzie.config.isFrameFontSmall() ? 30 : (Lizzie.config.isFrameFontMiddle() ? 30 : 40),
        24);
    this.chkRemoteEngine.setBounds(
        5,
        300,
        Lizzie.config.isFrameFontSmall() ? 75 : (Lizzie.config.isFrameFontMiddle() ? 90 : 105),
        24);
    btnRemoteEngine.setBounds(
        Lizzie.config.isFrameFontSmall() ? 80 : (Lizzie.config.isFrameFontMiddle() ? 100 : 115),
        301,
        20,
        24);
    lblIp.setBounds(
        Lizzie.config.isFrameFontSmall() ? 120 : (Lizzie.config.isFrameFontMiddle() ? 140 : 155),
        300,
        30,
        24);
    this.txtIP.setBounds(
        Lizzie.config.isFrameFontSmall() ? 137 : (Lizzie.config.isFrameFontMiddle() ? 160 : 185),
        301,
        130,
        24);
    lblPort.setBounds(
        Lizzie.config.isFrameFontSmall() ? 280 : (Lizzie.config.isFrameFontMiddle() ? 310 : 327),
        300,
        40,
        24);
    this.txtPort.setBounds(
        Lizzie.config.isFrameFontSmall() ? 310 : (Lizzie.config.isFrameFontMiddle() ? 348 : 378),
        301,
        50,
        24);
    lblUserName.setBounds(
        Lizzie.config.isFrameFontSmall() ? 368 : (Lizzie.config.isFrameFontMiddle() ? 406 : 436),
        300,
        65,
        24);
    this.txtUserName.setBounds(
        Lizzie.config.isFrameFontSmall() ? 418 : (Lizzie.config.isFrameFontMiddle() ? 466 : 512),
        301,
        100,
        24);
    boolean isMac =
        !Lizzie.config.useJavaLooks && System.getProperty("os.name").toLowerCase().contains("mac");
    if (isMac) {
      this.rdoUsePassword.setBounds(
          Lizzie.config.isFrameFontSmall() ? 526 : (Lizzie.config.isFrameFontMiddle() ? 573 : 620),
          300,
          Lizzie.config.isFrameFontSmall() ? 70 : (Lizzie.config.isFrameFontMiddle() ? 85 : 70),
          24);
      this.txtPassword.setBounds(
          Lizzie.config.isFrameFontSmall() ? 600 : (Lizzie.config.isFrameFontMiddle() ? 660 : 693),
          301,
          Lizzie.config.isFrameFontSmall() ? 80 : (Lizzie.config.isFrameFontMiddle() ? 80 : 70),
          24);
      this.rdoKeyGen.setBounds(
          Lizzie.config.isFrameFontSmall() ? 685 : (Lizzie.config.isFrameFontMiddle() ? 750 : 763),
          300,
          Lizzie.config.isFrameFontSmall()
              ? (Lizzie.config.isChinese ? 70 : 95)
              : (Lizzie.config.isFrameFontMiddle() ? 85 : 70),
          24);
      this.scanKeygen.setBounds(
          Lizzie.config.isFrameFontSmall()
              ? (Lizzie.config.isChinese ? 755 : 755)
              : (Lizzie.config.isFrameFontMiddle() ? 840 : 840),
          300,
          50,
          24);
    } else {
      this.rdoUsePassword.setBounds(
          Lizzie.config.isFrameFontSmall() ? 526 : (Lizzie.config.isFrameFontMiddle() ? 573 : 620),
          300,
          Lizzie.config.isFrameFontSmall() ? 50 : (Lizzie.config.isFrameFontMiddle() ? 60 : 70),
          24);
      this.txtPassword.setBounds(
          Lizzie.config.isFrameFontSmall() ? 580 : (Lizzie.config.isFrameFontMiddle() ? 635 : 693),
          301,
          Lizzie.config.isFrameFontSmall() ? 80 : (Lizzie.config.isFrameFontMiddle() ? 80 : 70),
          24);
      this.rdoKeyGen.setBounds(
          Lizzie.config.isFrameFontSmall() ? 665 : (Lizzie.config.isFrameFontMiddle() ? 725 : 763),
          300,
          Lizzie.config.isFrameFontSmall()
              ? (Lizzie.config.isChinese ? 50 : 70)
              : (Lizzie.config.isFrameFontMiddle() ? 60 : 70),
          24);
      this.scanKeygen.setBounds(
          Lizzie.config.isFrameFontSmall()
              ? (Lizzie.config.isChinese ? 715 : 735)
              : (Lizzie.config.isFrameFontMiddle() ? 790 : 835),
          300,
          50,
          24);
    }
    this.moveUp.setBounds(5, 330, 55, 24);
    this.moveDown.setBounds(60, 330, 55, 24);
    this.moveUp5.setBounds(115, 330, 55, 24);
    this.moveDown5.setBounds(170, 330, 55, 24);
    this.moveFirst.setBounds(225, 330, 55, 24);
    this.moveLast.setBounds(280, 330, 55, 24);
    this.add.setBounds(585, 330, 54, 24);
    this.delete.setBounds(639, 330, 54, 24);
    this.cancel.setBounds(693, 330, 54, 24);
    this.save.setBounds(765, 330, 60, 24);
    this.exit.setBounds(825, 330, 60, 24);
    lblchooseStart.setBounds(5, 360, 120, 24);
    if (Lizzie.config.isChinese) {
      rdoDefault.setBounds(
          Lizzie.config.isFrameFontSmall() ? 74 : (Lizzie.config.isFrameFontMiddle() ? 100 : 120),
          360,
          (Lizzie.config.isFrameFontSmall() ? 76 : (Lizzie.config.isFrameFontMiddle() ? 90 : 110)),
          24);
      rdoLast.setBounds(
          (Lizzie.config.isFrameFontSmall()
              ? 150
              : (Lizzie.config.isFrameFontMiddle() ? 190 : 230)),
          360,
          Lizzie.config.isFrameFontSmall() ? 110 : (Lizzie.config.isFrameFontMiddle() ? 140 : 170),
          24);
      rdoMannul.setBounds(
          (Lizzie.config.isFrameFontSmall()
              ? 263
              : (Lizzie.config.isFrameFontMiddle() ? 330 : 400)),
          360,
          Lizzie.config.isFrameFontSmall() ? 80 : (Lizzie.config.isFrameFontMiddle() ? 93 : 110),
          24);
      rdoNone.setBounds(
          (Lizzie.config.isFrameFontSmall()
              ? 340
              : (Lizzie.config.isFrameFontMiddle() ? 423 : 510)),
          360,
          Lizzie.config.isFrameFontSmall() ? 70 : (Lizzie.config.isFrameFontMiddle() ? 90 : 120),
          24);
    } else {
      rdoDefault.setBounds(
          Lizzie.config.isFrameFontSmall() ? 70 : (Lizzie.config.isFrameFontMiddle() ? 95 : 115),
          360,
          (Lizzie.config.isFrameFontSmall()
              ? 110
              : (Lizzie.config.isFrameFontMiddle() ? 125 : 155)),
          24);
      rdoLast.setBounds(
          (Lizzie.config.isFrameFontSmall()
              ? 180
              : (Lizzie.config.isFrameFontMiddle() ? 220 : 270)),
          360,
          Lizzie.config.isFrameFontSmall() ? 130 : (Lizzie.config.isFrameFontMiddle() ? 155 : 190),
          24);
      rdoMannul.setBounds(
          (Lizzie.config.isFrameFontSmall()
              ? 312
              : (Lizzie.config.isFrameFontMiddle() ? 375 : 460)),
          360,
          Lizzie.config.isFrameFontSmall() ? 80 : (Lizzie.config.isFrameFontMiddle() ? 90 : 105),
          24);
      rdoNone.setBounds(
          (Lizzie.config.isFrameFontSmall()
              ? 392
              : (Lizzie.config.isFrameFontMiddle() ? 464 : 565)),
          360,
          Lizzie.config.isFrameFontSmall() ? 90 : (Lizzie.config.isFrameFontMiddle() ? 110 : 135),
          24);
    }
    //    this.rdoDefault.setBounds(
    //        Lizzie.config.isFrameFontSmall() ? 70 : (Lizzie.config.isFrameFontMiddle() ? 90 :
    // 110),
    //        360,
    //        (Lizzie.config.isFrameFontSmall() ? 130 : (Lizzie.config.isFrameFontMiddle() ? 160 :
    // 190))
    //            + (Lizzie.config.isChinese ? 0 : 15),
    //        24);
    //    this.rdoLast.setBounds(
    //        (Lizzie.config.isFrameFontSmall() ? 210 : (Lizzie.config.isFrameFontMiddle() ? 250 :
    // 310))
    //            + (Lizzie.config.isChinese ? 0 : 15),
    //        360,
    //        Lizzie.config.isFrameFontSmall() ? 160 : (Lizzie.config.isFrameFontMiddle() ? 215 :
    // 250),
    //        24);
    //    this.rdoMannul.setBounds(
    //        (Lizzie.config.isFrameFontSmall() ? 368 : (Lizzie.config.isFrameFontMiddle() ? 465 :
    // 570))
    //            + (Lizzie.config.isChinese ? 0 : 15),
    //        360,
    //        145,
    //        24);
    JFontButton btnEncrypt =
        new JFontButton(this.resourceBundle.getString("MoreEngines.btnEncrypt"));
    btnEncrypt.setMargin(new Insets(0, 0, 0, 0));
    btnEncrypt.setBounds(765, 360, 120, 24);
    this.selectpanel.add(btnEncrypt);
    btnEncrypt.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            if (command.getText().startsWith("encryption||")) return;
            command.setText("encryption||" + Utils.doEncrypt2(command.getText().trim()));
          }
        });
    ButtonGroup startGroup = new ButtonGroup();
    startGroup.add(this.rdoDefault);
    startGroup.add(this.rdoLast);
    startGroup.add(this.rdoMannul);
    startGroup.add(this.rdoNone);
    if (Lizzie.config.uiConfig.optBoolean("autoload-default", false)) {
      if (Lizzie.config.uiConfig.optBoolean("autoload-last", false)) {
        this.rdoLast.setSelected(true);
      } else {
        this.rdoDefault.setSelected(true);
      }
    } else {
      if (Lizzie.config.uiConfig.optBoolean("autoload-empty", false))
        this.rdoNone.setSelected(true);
      else this.rdoMannul.setSelected(true);
    }
    setEnable(false);
    this.selectpanel.add(this.engineName);
    this.selectpanel.add(lblName);
    this.selectpanel.add(this.txtName);
    this.selectpanel.add(this.command);
    this.selectpanel.add(lblCommand);
    this.selectpanel.add(lblInitialCommand);
    this.selectpanel.add(this.txtInitialCommand);
    this.selectpanel.add(this.preload);
    this.selectpanel.add(lblWidth);
    this.selectpanel.add(this.txtWidth);
    this.selectpanel.add(lblHeight);
    this.selectpanel.add(this.txtHeight);
    this.selectpanel.add(lblKomi);
    this.selectpanel.add(this.txtKomi);
    this.selectpanel.add(this.scan);
    this.selectpanel.add(this.add);
    this.selectpanel.add(this.save);
    this.selectpanel.add(this.cancel);
    this.selectpanel.add(this.exit);
    this.selectpanel.add(this.moveUp);
    this.selectpanel.add(this.moveUp5);
    this.selectpanel.add(this.moveFirst);
    this.selectpanel.add(this.moveLast);
    this.selectpanel.add(this.moveDown);
    this.selectpanel.add(this.moveDown5);
    this.selectpanel.add(this.chkDefault);
    this.selectpanel.add(lblchooseStart);
    this.selectpanel.add(this.rdoDefault);
    this.selectpanel.add(this.rdoLast);
    this.selectpanel.add(this.rdoMannul);
    this.selectpanel.add(this.rdoNone);
    this.selectpanel.add(this.delete);
    this.scan.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            GetEngineLine getEngineLine = new GetEngineLine();
            String el = getEngineLine.getEngineLine(MoreEngines.engjf, false, false);
            if (!el.isEmpty()) command.setText(el);
            setVisible(true);
          }
        });
    this.add.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            checkSave();
            ArrayList<EngineData> engData = Utils.getEngineData();
            EngineData newEng = new EngineData();
            newEng.commands = "";
            newEng.height = 19;
            newEng.index = 0;
            newEng.isDefault = false;
            newEng.komi = 7.5F;
            newEng.name = resourceBundle.getString("ChooseMoreEngine.newEngine");
            newEng.preload = false;
            newEng.width = 19;
            engData.add(0, newEng);
            Utils.saveEngineSettings(engData);
            needUpdateEngine = true;
            handleTableClick(0);
          }
        });
    this.delete.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            SwingUtilities.invokeLater(
                new Runnable() {
                  public void run() {
                    Object[] options1 = {
                      resourceBundle.getString("MoreEngines.deleteHint"),
                      resourceBundle.getString("MoreEngines.deleteHint2")
                    };
                    int ret1 =
                        JOptionPane.showOptionDialog(
                            MoreEngines.engjf,
                            resourceBundle.getString("MoreEngines.deleteHint5"),
                            resourceBundle.getString("MoreEngines.deleteHint6"),
                            0,
                            3,
                            null,
                            options1,
                            options1[0]);
                    if (ret1 != 0) return;
                    ArrayList<EngineData> engineData = Utils.getEngineData();
                    engineData.remove(curIndex);
                    Utils.saveEngineSettings(engineData);
                    table.validate();
                    table.updateUI();
                    table.getSelectionModel().clearSelection();
                    needUpdateEngine = true;
                    handleTableClick(curIndex);
                  }
                });
          }
        });
    this.exit.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            checkSave();
            MoreEngines.engjf.setVisible(false);
            if (needUpdateEngine) Lizzie.engineManager.updateEngines();
          }
        });
    this.cancel.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            command.setText("");
            engineName.setText(resourceBundle.getString("MoreEngines.engineName"));
            txtName.setText("");
            txtInitialCommand.setText("");
            txtInitialCommand.setForeground(Color.GRAY);
            txtInitialCommand.setText(resourceBundle.getString("MoreEngines.initialCommandHint"));
            preload.setSelected(false);
            txtWidth.setText("");
            txtHeight.setText("");
            chkDefault.setSelected(false);
            txtIP.setText("");
            txtPort.setText("");
            rdoUsePassword.setSelected(false);
            rdoKeyGen.setSelected(false);
            keyGenPath = "";
            txtUserName.setText("");
            txtPassword.setText("");
            scanKeygen.setToolTipText("");
            scanKeygen.setToolTipText("");
            curIndex = -1;
            setEnable(false);
            table.getSelectionModel().clearSelection();
          }
        });
    this.save.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            saveDefaultEngine();
            boolean empty = false;
            if (command.getText().equals("")) {
              empty = true;
              command.setText(" ");
            }
            if (curIndex >= 0) saveCurrentEngineConfig();
            if (empty) command.setText("");
            table.validate();
            table.updateUI();
          }
        });
    table.addMouseListener(
        new MouseAdapter() {
          public void mouseClicked(MouseEvent e) {
            int row = table.rowAtPoint(e.getPoint());
            checkSave();
            handleTableClick(row);
          }
        });
    this.moveFirst.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            ArrayList<EngineData> engData = Utils.getEngineData();
            if (curIndex < 1 || curIndex > engData.size() - 1) return;
            EngineData enginedt = engData.get(curIndex);
            engData.remove(curIndex);
            engData.add(0, enginedt);
            Utils.saveEngineSettings(engData);
            table.validate();
            table.updateUI();
            curIndex = 0;
          }
        });
    this.moveLast.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            ArrayList<EngineData> engData = Utils.getEngineData();
            if (curIndex < 0 || curIndex > engData.size() - 1) return;
            EngineData enginedt = engData.get(curIndex);
            engData.remove(curIndex);
            engData.add(engData.size(), enginedt);
            Utils.saveEngineSettings(engData);
            table.validate();
            table.updateUI();
            curIndex = engData.size() - 1;
          }
        });
    this.moveUp.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            ArrayList<EngineData> engData = Utils.getEngineData();
            if (curIndex < 1 || curIndex > engData.size() - 1) return;
            EngineData enginedt = engData.get(curIndex);
            engData.remove(curIndex);
            engData.add(curIndex - 1, enginedt);
            Utils.saveEngineSettings(engData);
            table.validate();
            table.updateUI();
            curIndex--;
          }
        });
    this.moveUp5.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            ArrayList<EngineData> engData = Utils.getEngineData();
            if (curIndex < 5 || curIndex > engData.size() - 1) return;
            EngineData enginedt = engData.get(curIndex);
            engData.remove(curIndex);
            engData.add(curIndex - 5, enginedt);
            Utils.saveEngineSettings(engData);
            table.validate();
            table.updateUI();
            curIndex -= 5;
          }
        });
    this.moveDown.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            ArrayList<EngineData> engData = Utils.getEngineData();
            if (curIndex < 0 || curIndex > engData.size() - 2) return;
            EngineData enginedt = engData.get(curIndex);
            engData.remove(curIndex);
            engData.add(curIndex + 1, enginedt);
            Utils.saveEngineSettings(engData);
            table.validate();
            table.updateUI();
            curIndex++;
          }
        });
    this.moveDown5.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            ArrayList<EngineData> engData = Utils.getEngineData();
            if (curIndex < 0 || curIndex > engData.size() - 6) return;
            EngineData enginedt = engData.get(curIndex);
            engData.remove(curIndex);
            engData.add(curIndex + 5, enginedt);
            Utils.saveEngineSettings(engData);
            table.validate();
            table.updateUI();
            curIndex += 5;
          }
        });
  }

  class ColorTableCellRenderer extends DefaultTableCellRenderer {
    DefaultTableCellRenderer renderer;

    ColorTableCellRenderer() {
      this.renderer = new DefaultTableCellRenderer();
    }

    public Component getTableCellRendererComponent(
        JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
      if (row == curIndex)
        return this.renderer.getTableCellRendererComponent(table, value, true, false, row, column);
      return this.renderer.getTableCellRendererComponent(table, value, false, false, row, column);
    }
  }

  public void saveDefaultEngine() {
    if (this.chkDefault.isSelected()) Lizzie.config.uiConfig.put("default-engine", this.curIndex);
    if (this.rdoDefault.isSelected()) {
      Lizzie.config.uiConfig.put("autoload-default", true);
      Lizzie.config.uiConfig.put("autoload-last", false);
      Lizzie.config.uiConfig.put("autoload-empty", false);
    } else {
      Lizzie.config.uiConfig.put("autoload-last", false);
      Lizzie.config.uiConfig.put("autoload-default", false);
    }
    if (this.rdoLast.isSelected()) {
      Lizzie.config.uiConfig.put("autoload-last", true);
      Lizzie.config.uiConfig.put("autoload-default", true);
      Lizzie.config.uiConfig.put("autoload-empty", false);
    }
    if (this.rdoMannul.isSelected()) {
      Lizzie.config.uiConfig.put("autoload-last", false);
      Lizzie.config.uiConfig.put("autoload-default", false);
      Lizzie.config.uiConfig.put("autoload-empty", false);
    }
    if (rdoNone.isSelected()) {
      Lizzie.config.uiConfig.put("autoload-last", false);
      Lizzie.config.uiConfig.put("autoload-default", false);
      Lizzie.config.uiConfig.put("autoload-empty", true);
    }
  }

  private void checkSave() {
    if (this.curIndex < 0) return;
    boolean isChanged = false;
    ArrayList<EngineData> engData = Utils.getEngineData();
    if (this.curIndex >= engData.size()) {
      isChanged = true;
    } else {
      EngineData engDt = engData.get(this.curIndex);
      if (!this.command.getText().startsWith("encryption||")
          && !this.command.getText().equals(engDt.commands)) isChanged = true;
      if (!this.txtName.getText().equals(engDt.name)) isChanged = true;
      if (!this.txtInitialCommand
              .getText()
              .equals(resourceBundle.getString("MoreEngines.initialCommandHint"))
          && !this.txtInitialCommand.getText().equals(engDt.initialCommand)) isChanged = true;
      if (this.preload.isSelected() != engDt.preload) isChanged = true;
      if (!this.txtWidth
          .getText()
          .equals((new StringBuilder(String.valueOf(engDt.width))).toString())) isChanged = true;
      if (!this.txtHeight
          .getText()
          .equals((new StringBuilder(String.valueOf(engDt.height))).toString())) isChanged = true;
      if (!this.txtKomi
          .getText()
          .equals((new StringBuilder(String.valueOf(engDt.komi))).toString())) isChanged = true;
      if (this.chkRemoteEngine.isSelected() != engDt.useJavaSSH) isChanged = true;
      if (this.chkRemoteEngine.isSelected()) {
        if (!this.txtIP.getText().equals(engDt.ip)) isChanged = true;
        if (!this.txtPort.getText().equals(engDt.port)) isChanged = true;
        if (!this.txtUserName.getText().equals(engDt.userName)) isChanged = true;
        if (this.rdoKeyGen.isSelected() != engDt.useKeyGen) isChanged = true;
        if (this.rdoKeyGen.isSelected()) {
          if (!this.keyGenPath.equals(engDt.keyGenPath)) isChanged = true;
        } else if (!Utils.doEncrypt(new String(this.txtPassword.getPassword()))
            .equals(engDt.password)) {
          isChanged = true;
        }
      }
      if (isChanged)
        if (this.command.getText().equals("")) {
          Object[] options1 = {
            this.resourceBundle.getString("MoreEngines.deleteHint"),
            this.resourceBundle.getString("MoreEngines.deleteHint2")
          };
          int ret1 =
              JOptionPane.showOptionDialog(
                  this,
                  this.resourceBundle.getString("MoreEngines.deleteHint3"),
                  this.resourceBundle.getString("MoreEngines.deleteHint4"),
                  0,
                  3,
                  null,
                  options1,
                  options1[0]);
          if (ret1 == 0) saveCurrentEngineConfig();
        } else {
          Object[] options = {
            this.resourceBundle.getString("MoreEngines.saveHint"),
            this.resourceBundle.getString("MoreEngines.saveHint2")
          };
          int ret =
              JOptionPane.showOptionDialog(
                  this,
                  this.resourceBundle.getString("MoreEngines.saveHint3"),
                  this.resourceBundle.getString("MoreEngines.saveHint4"),
                  0,
                  3,
                  null,
                  options,
                  options[0]);
          if (ret == 0) saveCurrentEngineConfig();
        }
    }
  }

  private void setEnable(boolean isEnable) {
    if (isEnable) {
      this.txtName.setEnabled(true);
      txtInitialCommand.setEnabled(true);
      this.command.setEnabled(true);
      command.setBackground(Color.WHITE);
      this.preload.setEnabled(true);
      this.txtWidth.setEnabled(true);
      this.txtHeight.setEnabled(true);
      this.txtKomi.setEnabled(true);
      this.chkDefault.setEnabled(true);
      this.chkRemoteEngine.setEnabled(true);
      if (this.chkRemoteEngine.isSelected()) {
        this.txtIP.setEnabled(true);
        this.txtPort.setEnabled(true);
        this.rdoUsePassword.setEnabled(true);
        this.rdoKeyGen.setEnabled(true);
        if (this.rdoUsePassword.isSelected()) this.txtPassword.setEnabled(true);
        if (this.rdoKeyGen.isSelected()) this.scanKeygen.setEnabled(true);
      }
      this.delete.setEnabled(true);
      this.save.setEnabled(true);
      this.cancel.setEnabled(true);
      this.scan.setEnabled(true);
      this.moveUp.setEnabled(true);
      this.moveUp5.setEnabled(true);
      this.moveFirst.setEnabled(true);
      this.moveLast.setEnabled(true);
      this.moveDown.setEnabled(true);
      this.moveDown5.setEnabled(true);
    } else {
      this.txtName.setEnabled(false);
      txtInitialCommand.setEnabled(false);
      this.command.setEnabled(false);
      command.setBackground(getBackground());
      this.preload.setEnabled(false);
      this.txtWidth.setEnabled(false);
      this.txtHeight.setEnabled(false);
      this.txtKomi.setEnabled(false);
      this.chkRemoteEngine.setEnabled(false);
      this.txtIP.setEnabled(false);
      this.txtPort.setEnabled(false);
      this.txtUserName.setEnabled(false);
      this.txtPassword.setEnabled(false);
      this.moveUp.setEnabled(false);
      this.moveDown.setEnabled(false);
      this.moveUp5.setEnabled(false);
      this.moveDown5.setEnabled(false);
      this.chkDefault.setEnabled(false);
      this.delete.setEnabled(false);
      this.moveFirst.setEnabled(false);
      this.moveLast.setEnabled(false);
      this.scan.setEnabled(false);
      this.cancel.setEnabled(false);
      this.rdoUsePassword.setEnabled(false);
      this.rdoKeyGen.setEnabled(false);
      this.scanKeygen.setEnabled(false);
    }
  }

  private void handleTableClick(int row) {
    ArrayList<EngineData> engineDatas = Utils.getEngineData();
    if (row < engineDatas.size()) {
      EngineData engineData = engineDatas.get(row);
      this.command.setText(engineData.commands);
      this.engineName.setText(
          String.valueOf(this.resourceBundle.getString("MoreEngines.editEngine"))
              + (engineData.index + 1));
      this.txtName.setText(engineData.name);
      this.txtInitialCommand.setText(engineData.initialCommand);
      if (engineData.initialCommand.equals("")) {
        txtInitialCommand.setForeground(Color.GRAY);
        txtInitialCommand.setText(resourceBundle.getString("MoreEngines.initialCommandHint"));
      } else {
        txtInitialCommand.setForeground(Color.BLACK);
      }
      this.preload.setSelected(engineData.preload);
      this.txtWidth.setText((new StringBuilder(String.valueOf(engineData.width))).toString());
      this.txtHeight.setText((new StringBuilder(String.valueOf(engineData.height))).toString());
      this.chkDefault.setSelected(engineData.isDefault);
      this.txtKomi.setText((new StringBuilder(String.valueOf(engineData.komi))).toString());
      this.chkRemoteEngine.setSelected(engineData.useJavaSSH);
      if (engineData.useJavaSSH) {
        this.txtIP.setText(engineData.ip);
        this.txtPort.setText(engineData.port);
        this.rdoUsePassword.setSelected(!engineData.useKeyGen);
        this.rdoKeyGen.setSelected(engineData.useKeyGen);
        this.txtUserName.setText(engineData.userName);
        if (engineData.useKeyGen) {
          this.keyGenPath = engineData.keyGenPath;
          this.scanKeygen.setToolTipText(this.keyGenPath);
          this.rdoKeyGen.setToolTipText(this.keyGenPath);
        } else {
          this.txtPassword.setText(Utils.doDecrypt(engineData.password));
        }
      } else {
        this.txtIP.setText("");
        this.txtPort.setText("");
        this.rdoUsePassword.setSelected(false);
        this.rdoKeyGen.setSelected(false);
        this.keyGenPath = "";
        this.txtUserName.setText("");
        this.txtPassword.setText("");
        this.scanKeygen.setToolTipText("");
        this.scanKeygen.setToolTipText("");
      }
    } else {
      this.command.setText("");
      this.txtName.setText("");
      this.txtInitialCommand.setText("");
      txtInitialCommand.setForeground(Color.GRAY);
      txtInitialCommand.setText(resourceBundle.getString("MoreEngines.initialCommandHint"));
      this.engineName.setText(
          String.valueOf(this.resourceBundle.getString("MoreEngines.editEngine")) + (row + 1));
      this.preload.setSelected(false);
      this.txtWidth.setText("19");
      this.txtHeight.setText("19");
      this.chkDefault.setSelected(false);
      this.txtKomi.setText("7.5");
      this.chkRemoteEngine.setSelected(false);
      this.txtIP.setText("");
      this.txtPort.setText("");
      this.rdoUsePassword.setSelected(false);
      this.rdoKeyGen.setSelected(false);
      this.keyGenPath = "";
      this.txtUserName.setText("");
      this.txtPassword.setText("");
      this.scanKeygen.setToolTipText("");
      this.scanKeygen.setToolTipText("");
    }
    if (this.chkRemoteEngine.isSelected()) {
      this.txtIP.setEnabled(true);
      this.txtPort.setEnabled(true);
      this.rdoUsePassword.setEnabled(true);
      this.rdoKeyGen.setEnabled(true);
      this.txtUserName.setEnabled(true);
      if (this.rdoUsePassword.isSelected()) this.txtPassword.setEnabled(true);
      if (this.rdoKeyGen.isSelected()) this.scanKeygen.setEnabled(true);
    } else {
      this.txtIP.setEnabled(false);
      this.txtPort.setEnabled(false);
      this.rdoUsePassword.setEnabled(false);
      this.rdoKeyGen.setEnabled(false);
      this.txtUserName.setEnabled(false);
      this.txtPassword.setEnabled(false);
      this.scanKeygen.setEnabled(false);
    }
    this.curIndex = Integer.parseInt(table.getModel().getValueAt(row, 0).toString()) - 1;
    setEnable(true);
    table.validate();
    table.updateUI();
  }

  private void saveCurrentEngineConfig() {
    needUpdateEngine = true;
    ArrayList<EngineData> engineData = Utils.getEngineData();
    EngineData engineDt = new EngineData();
    engineDt.index = this.curIndex;
    engineDt.commands = this.command.getText();
    engineDt.name = this.txtName.getText();
    if (txtInitialCommand
        .getText()
        .equals(resourceBundle.getString("MoreEngines.initialCommandHint")))
      engineDt.initialCommand = "";
    else engineDt.initialCommand = this.txtInitialCommand.getText();
    engineDt.preload = this.preload.isSelected();
    engineDt.width = Utils.parseTextToInt(this.txtWidth, 19);
    engineDt.height = Utils.parseTextToInt(this.txtHeight, 19);
    engineDt.isDefault = this.chkDefault.isSelected();
    engineDt.komi = Utils.parseTextToFloat(this.txtKomi, Float.valueOf(7.5F)).floatValue();
    engineDt.useJavaSSH = this.chkRemoteEngine.isSelected();
    if (engineDt.useJavaSSH) {
      engineDt.ip = this.txtIP.getText();
      engineDt.port = this.txtPort.getText();
      engineDt.useKeyGen = this.rdoKeyGen.isSelected();
      engineDt.userName = this.txtUserName.getText();
      if (engineDt.useKeyGen) {
        engineDt.keyGenPath = this.keyGenPath;
      } else {
        engineDt.password = Utils.doEncrypt(new String(this.txtPassword.getPassword()));
      }
    }
    if (engineDt.isDefault) {
      for (EngineData engine : engineData) {
        engine.isDefault = false;
      }
    }
    if (this.curIndex + 1 > engineData.size()) {
      engineData.add(engineDt);
    } else {
      engineData.remove(this.curIndex);
      engineData.add(this.curIndex, engineDt);
    }
    Utils.saveEngineSettings(engineData);
  }

  public AbstractTableModel getTableModel() {
    return new AbstractTableModel() {
      public int getColumnCount() {
        return 9;
      }

      public int getRowCount() {
        return 500;
      }

      public String getColumnName(int column) {
        if (column == 0) return resourceBundle.getString("MoreEngines.column0");
        if (column == 1) return resourceBundle.getString("MoreEngines.column1");
        if (column == 2) return resourceBundle.getString("MoreEngines.column2");
        if (column == 3) return resourceBundle.getString("MoreEngines.column3");
        if (column == 4) return resourceBundle.getString("MoreEngines.column4");
        if (column == 5) return resourceBundle.getString("MoreEngines.column5");
        if (column == 6) return resourceBundle.getString("MoreEngines.column6");
        if (column == 7) return resourceBundle.getString("MoreEngines.column7");
        if (column == 8) return resourceBundle.getString("MoreEngines.column8");
        return "";
      }

      public Object getValueAt(int row, int col) {
        ArrayList<EngineData> EngineDatas = Utils.getEngineData();
        if (row > EngineDatas.size() - 1) {
          if (col == 0) return Integer.valueOf(row + 1);
          return "";
        }
        EngineData data = EngineDatas.get(row);
        switch (col) {
          case 0:
            return Integer.valueOf(row + 1);
          case 1:
            return data.name;
          case 2:
            return data.commands;
          case 3:
            if (data.preload) return resourceBundle.getString("MoreEngines.yes");
            return resourceBundle.getString("MoreEngines.no");
          case 4:
            return Integer.valueOf(data.width);
          case 5:
            return Integer.valueOf(data.height);
          case 6:
            return Float.valueOf(data.komi);
          case 7:
            if (data.isDefault) return resourceBundle.getString("MoreEngines.yes");
            return resourceBundle.getString("MoreEngines.no");
          case 8:
            if (data.useJavaSSH) return resourceBundle.getString("MoreEngines.yes");
            return resourceBundle.getString("MoreEngines.no");
        }
        return "";
      }
    };
  }

  public static JDialog createDialog() {
    engjf = new JDialog();
    needUpdateEngine = false;
    engjf.setTitle(Lizzie.resourceBundle.getString("MoreEngines.title"));
    engjf.setModal(true);
    engjf.addWindowListener(
        new WindowAdapter() {
          public void windowClosing(WindowEvent e) {
            MoreEngines.engjf.setVisible(false);
            if (needUpdateEngine) Lizzie.engineManager.updateEngines();
          }
        });
    MoreEngines newContentPane = new MoreEngines();
    newContentPane.setOpaque(true);
    engjf.setContentPane(newContentPane);
    Lizzie.setFrameSize(engjf, 891, 794);
    engjf.setResizable(false);
    try {
      engjf.setIconImage(ImageIO.read(MoreEngines.class.getResourceAsStream("/assets/logo.png")));
    } catch (IOException e) {
      e.printStackTrace();
    }
    engjf.setAlwaysOnTop(Lizzie.frame.isAlwaysOnTop());
    engjf.setLocationRelativeTo(engjf.getOwner());
    return engjf;
  }
}
