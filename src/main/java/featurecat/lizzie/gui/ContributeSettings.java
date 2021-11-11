package featurecat.lizzie.gui;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Window;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

public class ContributeSettings extends JDialog {
  private JTextField txtEngineFile;
  private JTextField txtConfigFile;
  private JTextField txtUserName;
  private JTextField txtPassword;
  private JTextField txtGames;

  public ContributeSettings(Window owner) {
    super(owner);
    setTitle("KataGo跑谱贡献设置");

    String gamesTip = "同时进行多盘对局以增加GPU/CPU利用率,设置为default_gtp.cfg中numSearchThreads的数值即可";
    String configTip = "跑谱贡献配置文件将不同于普通的分析引擎配置文件,默认名字为contribute_example.cfg";
    String ownerShipTip = "以跑谱速度略微降低为代价,显示领地";

    JPanel mainPanel = new JPanel();
    mainPanel.setBorder(new EmptyBorder(2, 0, 0, 5));
    getContentPane().add(mainPanel, BorderLayout.CENTER);
    GridBagLayout gbl_mainPanel = new GridBagLayout();
    gbl_mainPanel.columnWidths = new int[] {217, 217, 0};
    gbl_mainPanel.rowHeights = new int[] {41, 41, 41, 41, 41, 41, 0};
    gbl_mainPanel.columnWeights = new double[] {1.0, 0.0, Double.MIN_VALUE};
    gbl_mainPanel.rowWeights = new double[] {1.0, 1.0, 1.0, 1.0, 1.0, 1.0, Double.MIN_VALUE};
    mainPanel.setLayout(gbl_mainPanel);

    JPanel engineFilePanel = new JPanel();
    GridBagConstraints gbc_engineFilePanel = new GridBagConstraints();
    gbc_engineFilePanel.fill = GridBagConstraints.VERTICAL;
    gbc_engineFilePanel.insets = new Insets(0, 0, 5, 5);
    gbc_engineFilePanel.gridx = 0;
    gbc_engineFilePanel.gridy = 0;
    mainPanel.add(engineFilePanel, gbc_engineFilePanel);

    JLabel lblEngine = new JFontLabel("KataGo引擎");
    engineFilePanel.add(lblEngine);

    JButton btnScanEngine = new JFontButton("浏览");
    engineFilePanel.add(btnScanEngine);

    txtEngineFile = new JFontTextField();
    GridBagConstraints gbc_txtEngineFile = new GridBagConstraints();
    gbc_txtEngineFile.fill = GridBagConstraints.BOTH;
    gbc_txtEngineFile.insets = new Insets(0, 0, 5, 0);
    gbc_txtEngineFile.gridx = 1;
    gbc_txtEngineFile.gridy = 0;
    mainPanel.add(txtEngineFile, gbc_txtEngineFile);
    txtEngineFile.setColumns(10);

    JPanel configFilePanel = new JPanel();
    GridBagConstraints gbc_configFilePanel = new GridBagConstraints();
    gbc_configFilePanel.fill = GridBagConstraints.VERTICAL;
    gbc_configFilePanel.insets = new Insets(0, 0, 5, 5);
    gbc_configFilePanel.gridx = 0;
    gbc_configFilePanel.gridy = 1;
    mainPanel.add(configFilePanel, gbc_configFilePanel);

    JLabel lblConfig = new JFontLabel("配置文件(可选)");
    configFilePanel.add(lblConfig);
    lblConfig.setToolTipText(configTip);

    JButton btnScanConfig = new JFontButton("浏览");
    configFilePanel.add(btnScanConfig);
    btnScanConfig.setToolTipText(configTip);

    txtConfigFile = new JFontTextField();
    GridBagConstraints gbc_txtConfigFile = new GridBagConstraints();
    gbc_txtConfigFile.fill = GridBagConstraints.BOTH;
    gbc_txtConfigFile.insets = new Insets(0, 0, 5, 0);
    gbc_txtConfigFile.gridx = 1;
    gbc_txtConfigFile.gridy = 1;
    mainPanel.add(txtConfigFile, gbc_txtConfigFile);
    txtConfigFile.setColumns(10);
    txtConfigFile.setToolTipText(configTip);

    JPanel panel = new JPanel();
    GridBagConstraints gbc_panel = new GridBagConstraints();
    gbc_panel.fill = GridBagConstraints.VERTICAL;
    gbc_panel.insets = new Insets(0, 0, 5, 5);
    gbc_panel.gridx = 0;
    gbc_panel.gridy = 2;
    mainPanel.add(panel, gbc_panel);

    JLabel lblUserName = new JFontLabel("用户名");
    panel.add(lblUserName);

    JButton btnSignUp = new JFontButton("注册");
    panel.add(btnSignUp);

    txtUserName = new JTextField();
    GridBagConstraints gbc_txtUserName = new GridBagConstraints();
    gbc_txtUserName.fill = GridBagConstraints.BOTH;
    gbc_txtUserName.insets = new Insets(0, 0, 5, 0);
    gbc_txtUserName.gridx = 1;
    gbc_txtUserName.gridy = 2;
    mainPanel.add(txtUserName, gbc_txtUserName);
    txtUserName.setColumns(10);

    JLabel lblPassword = new JFontLabel("密码");
    GridBagConstraints gbc_lblPassword = new GridBagConstraints();
    gbc_lblPassword.fill = GridBagConstraints.VERTICAL;
    gbc_lblPassword.insets = new Insets(0, 0, 5, 5);
    gbc_lblPassword.gridx = 0;
    gbc_lblPassword.gridy = 3;
    mainPanel.add(lblPassword, gbc_lblPassword);

    txtPassword = new JFontTextField();
    GridBagConstraints gbc_txtPassword = new GridBagConstraints();
    gbc_txtPassword.fill = GridBagConstraints.BOTH;
    gbc_txtPassword.insets = new Insets(0, 0, 5, 0);
    gbc_txtPassword.gridx = 1;
    gbc_txtPassword.gridy = 3;
    mainPanel.add(txtPassword, gbc_txtPassword);
    txtPassword.setColumns(10);

    JLabel lblGames = new JFontLabel("同时对局数");
    GridBagConstraints gbc_lblGames = new GridBagConstraints();
    gbc_lblGames.fill = GridBagConstraints.VERTICAL;
    gbc_lblGames.insets = new Insets(0, 0, 5, 5);
    gbc_lblGames.gridx = 0;
    gbc_lblGames.gridy = 4;
    mainPanel.add(lblGames, gbc_lblGames);
    lblGames.setToolTipText(gamesTip);

    txtGames = new JFontTextField();
    GridBagConstraints gbc_txtGames = new GridBagConstraints();
    gbc_txtGames.fill = GridBagConstraints.BOTH;
    gbc_txtGames.insets = new Insets(0, 0, 5, 0);
    gbc_txtGames.gridx = 1;
    gbc_txtGames.gridy = 4;
    mainPanel.add(txtGames, gbc_txtGames);
    txtGames.setColumns(10);
    txtGames.setToolTipText(gamesTip);

    JLabel lblShowOwnerShip = new JFontLabel("显示领地");
    GridBagConstraints gbc_lblShowOwnerShip = new GridBagConstraints();
    gbc_lblShowOwnerShip.fill = GridBagConstraints.VERTICAL;
    gbc_lblShowOwnerShip.insets = new Insets(0, 0, 0, 5);
    gbc_lblShowOwnerShip.gridx = 0;
    gbc_lblShowOwnerShip.gridy = 5;
    mainPanel.add(lblShowOwnerShip, gbc_lblShowOwnerShip);
    lblShowOwnerShip.setToolTipText(ownerShipTip);

    JCheckBox chkShowOwnerShip = new JFontCheckBox();
    GridBagConstraints gbc_chkShowOwnerShip = new GridBagConstraints();
    gbc_chkShowOwnerShip.fill = GridBagConstraints.BOTH;
    gbc_chkShowOwnerShip.gridx = 1;
    gbc_chkShowOwnerShip.gridy = 5;
    mainPanel.add(chkShowOwnerShip, gbc_chkShowOwnerShip);
    chkShowOwnerShip.setToolTipText(ownerShipTip);

    JPanel buttonPanel = new JPanel();
    getContentPane().add(buttonPanel, BorderLayout.SOUTH);

    JButton btnStart = new JFontButton("开始跑谱贡献");
    buttonPanel.add(btnStart);
    pack();
    setLocationRelativeTo(owner);
    setVisible(true);
  }
}
