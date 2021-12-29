package featurecat.lizzie.gui;

import featurecat.lizzie.Lizzie;
import featurecat.lizzie.util.Utils;
import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URI;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

public class ContributeSettings extends JDialog {
  private JTextField txtEnginePath;
  private JTextField txtConfigPath;
  private JTextField txtUserName;
  private JTextField txtPassword;
  private JTextField txtGames;
  private JTextField txtCommand;
  private JCheckBox chkUseCommand;
  private JCheckBox chkRemote;
  private JCheckBox chkShowOwnerShip;
  private JCheckBox chkAutoSave;
  private JButton btnRemoteSetting;
  private JDialog thisDialog = this;

  public ContributeSettings(Window owner) {
    super(owner);
    setTitle("KataGo跑谱贡献设置");

    String engineTip = "请务必使用官方KataGo引擎,否则无法跑谱贡献";
    String gamesTip = "同时进行多盘对局以增加GPU/CPU利用率,设置为default_gtp.cfg中numSearchThreads的数值即可";
    String configTip = "可配置使用的显卡数量等,不同于普通的分析引擎配置文件,默认名字为contribute_example.cfg";
    String ownerShipTip = "以跑谱速度略微降低为代价,显示领地(可在底部工具栏-Kata评估关闭显示,但速度不会恢复)";
    String autoSaveTip = "自动保存观看过且已对局结束的棋谱,保存到LizzieYzy目录内\"ContributeGames\"文件夹中";

    JPanel mainPanel = new JPanel();
    mainPanel.setBorder(new EmptyBorder(2, 0, 0, 5));
    getContentPane().add(mainPanel, BorderLayout.CENTER);
    GridBagLayout gbl_mainPanel = new GridBagLayout();
    gbl_mainPanel.columnWidths = new int[] {217, 217, 0};
    gbl_mainPanel.rowHeights = new int[] {41, 41, 0, 41, 41, 41, 41, 0, 0};
    gbl_mainPanel.columnWeights = new double[] {1.0, 1.0, Double.MIN_VALUE};
    gbl_mainPanel.rowWeights =
        new double[] {1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, Double.MIN_VALUE};
    mainPanel.setLayout(gbl_mainPanel);

    JPanel engineFilePanel = new JPanel();
    GridBagConstraints gbc_engineFilePanel = new GridBagConstraints();
    gbc_engineFilePanel.fill = GridBagConstraints.VERTICAL;
    gbc_engineFilePanel.insets = new Insets(0, 0, 5, 5);
    gbc_engineFilePanel.gridx = 0;
    gbc_engineFilePanel.gridy = 0;
    mainPanel.add(engineFilePanel, gbc_engineFilePanel);

    JLabel lblEngine = new JFontLabel("KataGo引擎路径");
    engineFilePanel.add(lblEngine);
    lblEngine.setToolTipText(engineTip);

    JButton btnScanEngine = new JFontButton("浏览");
    btnScanEngine.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            GetEngineLine getEngineLine = new GetEngineLine();
            String el = getEngineLine.getEngineLine(thisDialog, true, false, true, false);
            if (!el.isEmpty()) txtEnginePath.setText(el);
          }
        });
    engineFilePanel.add(btnScanEngine);
    btnScanEngine.setToolTipText(engineTip);

    txtEnginePath = new JFontTextField();
    GridBagConstraints gbc_txtEngineFile = new GridBagConstraints();
    gbc_txtEngineFile.fill = GridBagConstraints.BOTH;
    gbc_txtEngineFile.insets = new Insets(0, 0, 5, 0);
    gbc_txtEngineFile.gridx = 1;
    gbc_txtEngineFile.gridy = 0;
    mainPanel.add(txtEnginePath, gbc_txtEngineFile);
    txtEnginePath.setColumns(10);
    txtEnginePath.setToolTipText(engineTip);
    txtEnginePath.setText(Lizzie.config.contributeEnginePath);

    JPanel configFilePanel = new JPanel();
    GridBagConstraints gbc_configFilePanel = new GridBagConstraints();
    gbc_configFilePanel.fill = GridBagConstraints.VERTICAL;
    gbc_configFilePanel.insets = new Insets(0, 0, 5, 5);
    gbc_configFilePanel.gridx = 0;
    gbc_configFilePanel.gridy = 1;
    mainPanel.add(configFilePanel, gbc_configFilePanel);

    JLabel lblConfig = new JFontLabel("配置文件路径(可选)");
    configFilePanel.add(lblConfig);
    lblConfig.setToolTipText(configTip);

    JButton btnScanConfig = new JFontButton("浏览");
    btnScanConfig.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            GetEngineLine getEngineLine = new GetEngineLine();
            String el = getEngineLine.getEngineLine(thisDialog, true, false, false, true);
            if (!el.isEmpty()) txtConfigPath.setText(el);
          }
        });
    configFilePanel.add(btnScanConfig);
    btnScanConfig.setToolTipText(configTip);

    txtConfigPath = new JFontTextField();
    GridBagConstraints gbc_txtConfigFile = new GridBagConstraints();
    gbc_txtConfigFile.fill = GridBagConstraints.BOTH;
    gbc_txtConfigFile.insets = new Insets(0, 0, 5, 0);
    gbc_txtConfigFile.gridx = 1;
    gbc_txtConfigFile.gridy = 1;
    mainPanel.add(txtConfigPath, gbc_txtConfigFile);
    txtConfigPath.setColumns(10);
    txtConfigPath.setToolTipText(configTip);
    txtConfigPath.setText(Lizzie.config.contributeConfigPath);

    JPanel panel_1 = new JPanel();
    GridBagConstraints gbc_panel_1 = new GridBagConstraints();
    gbc_panel_1.insets = new Insets(0, 0, 5, 5);
    gbc_panel_1.fill = GridBagConstraints.BOTH;
    gbc_panel_1.gridx = 0;
    gbc_panel_1.gridy = 2;
    mainPanel.add(panel_1, gbc_panel_1);

    chkUseCommand = new JFontCheckBox("自定义命令行");
    panel_1.add(chkUseCommand);
    chkUseCommand.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            boolean useCommand = chkUseCommand.isSelected();
            chkRemote.setEnabled(useCommand);
            btnRemoteSetting.setEnabled(useCommand);
            txtCommand.setEnabled(useCommand);
          }
        });

    chkRemote = new JFontCheckBox("远程SSH");
    chkRemote.setSelected(Utils.getContributeRemoteEngineData().useJavaSSH);
    panel_1.add(chkRemote);

    btnRemoteSetting = new JFontButton("设置");
    btnRemoteSetting.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            RemoteEngineSettings remoteEngineSettings =
                new RemoteEngineSettings(thisDialog, false, true);
            remoteEngineSettings.setVisible(true);
          }
        });
    panel_1.add(btnRemoteSetting);

    txtCommand = new JFontTextField();
    GridBagConstraints gbc_txtCommand = new GridBagConstraints();
    gbc_txtCommand.insets = new Insets(0, 0, 5, 0);
    gbc_txtCommand.fill = GridBagConstraints.BOTH;
    gbc_txtCommand.gridx = 1;
    gbc_txtCommand.gridy = 2;
    mainPanel.add(txtCommand, gbc_txtCommand);
    txtCommand.setColumns(10);
    txtCommand.setText(Lizzie.config.contributeCommand);

    chkUseCommand.setSelected(Lizzie.config.contributeUseCommand);
    chkRemote.setEnabled(Lizzie.config.contributeUseCommand);
    btnRemoteSetting.setEnabled(Lizzie.config.contributeUseCommand);
    txtCommand.setEnabled(Lizzie.config.contributeUseCommand);

    JPanel panel = new JPanel();
    GridBagConstraints gbc_panel = new GridBagConstraints();
    gbc_panel.fill = GridBagConstraints.VERTICAL;
    gbc_panel.insets = new Insets(0, 0, 5, 5);
    gbc_panel.gridx = 0;
    gbc_panel.gridy = 3;
    mainPanel.add(panel, gbc_panel);

    JLabel lblUserName = new JFontLabel("用户名");
    panel.add(lblUserName);

    JButton btnSignUp = new JFontButton("注册");
    btnSignUp.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            try {
              URI uri = new URI("https://katagotraining.org/accounts/signup/");
              java.awt.Desktop.getDesktop().browse(uri);
            } catch (Exception e1) {
              // TODO Auto-generated catch block
              e1.printStackTrace();
            }
          }
        });
    panel.add(btnSignUp);

    txtUserName = new JFontTextField();
    GridBagConstraints gbc_txtUserName = new GridBagConstraints();
    gbc_txtUserName.fill = GridBagConstraints.BOTH;
    gbc_txtUserName.insets = new Insets(0, 0, 5, 0);
    gbc_txtUserName.gridx = 1;
    gbc_txtUserName.gridy = 3;
    mainPanel.add(txtUserName, gbc_txtUserName);
    txtUserName.setColumns(10);
    txtUserName.setText(Lizzie.config.contributeUserName);

    JLabel lblPassword = new JFontLabel("密码");
    GridBagConstraints gbc_lblPassword = new GridBagConstraints();
    gbc_lblPassword.fill = GridBagConstraints.VERTICAL;
    gbc_lblPassword.insets = new Insets(0, 0, 5, 5);
    gbc_lblPassword.gridx = 0;
    gbc_lblPassword.gridy = 4;
    mainPanel.add(lblPassword, gbc_lblPassword);

    txtPassword = new JFontTextField();
    GridBagConstraints gbc_txtPassword = new GridBagConstraints();
    gbc_txtPassword.fill = GridBagConstraints.BOTH;
    gbc_txtPassword.insets = new Insets(0, 0, 5, 0);
    gbc_txtPassword.gridx = 1;
    gbc_txtPassword.gridy = 4;
    mainPanel.add(txtPassword, gbc_txtPassword);
    txtPassword.setColumns(10);
    txtPassword.setText(Lizzie.config.contributePassword);

    JLabel lblGames = new JFontLabel("同时对局数");
    GridBagConstraints gbc_lblGames = new GridBagConstraints();
    gbc_lblGames.fill = GridBagConstraints.VERTICAL;
    gbc_lblGames.insets = new Insets(0, 0, 5, 5);
    gbc_lblGames.gridx = 0;
    gbc_lblGames.gridy = 5;
    mainPanel.add(lblGames, gbc_lblGames);
    lblGames.setToolTipText(gamesTip);

    txtGames = new JFontTextField();
    GridBagConstraints gbc_txtGames = new GridBagConstraints();
    gbc_txtGames.fill = GridBagConstraints.BOTH;
    gbc_txtGames.insets = new Insets(0, 0, 5, 0);
    gbc_txtGames.gridx = 1;
    gbc_txtGames.gridy = 5;
    mainPanel.add(txtGames, gbc_txtGames);
    txtGames.setColumns(10);
    txtGames.setToolTipText(gamesTip);
    txtGames.setDocument(new IntDocument());
    txtGames.setText(Lizzie.config.contributeBatchGames);

    JLabel lblShowOwnerShip = new JFontLabel("显示领地");
    GridBagConstraints gbc_lblShowOwnerShip = new GridBagConstraints();
    gbc_lblShowOwnerShip.fill = GridBagConstraints.VERTICAL;
    gbc_lblShowOwnerShip.insets = new Insets(0, 0, 5, 5);
    gbc_lblShowOwnerShip.gridx = 0;
    gbc_lblShowOwnerShip.gridy = 6;
    mainPanel.add(lblShowOwnerShip, gbc_lblShowOwnerShip);
    lblShowOwnerShip.setToolTipText(ownerShipTip);

    chkShowOwnerShip = new JFontCheckBox();
    GridBagConstraints gbc_chkShowOwnerShip = new GridBagConstraints();
    gbc_chkShowOwnerShip.insets = new Insets(0, 0, 5, 0);
    gbc_chkShowOwnerShip.fill = GridBagConstraints.BOTH;
    gbc_chkShowOwnerShip.gridx = 1;
    gbc_chkShowOwnerShip.gridy = 6;
    mainPanel.add(chkShowOwnerShip, gbc_chkShowOwnerShip);
    chkShowOwnerShip.setToolTipText(ownerShipTip);
    chkShowOwnerShip.setSelected(Lizzie.config.contributeShowEstimate);

    JPanel panel_2 = new JPanel();
    GridBagConstraints gbc_panel_2 = new GridBagConstraints();
    gbc_panel_2.insets = new Insets(0, 0, 0, 5);
    gbc_panel_2.fill = GridBagConstraints.BOTH;
    gbc_panel_2.gridx = 0;
    gbc_panel_2.gridy = 7;
    mainPanel.add(panel_2, gbc_panel_2);

    JLabel lblAutoSave = new JFontLabel("自动保存棋谱");
    lblAutoSave.setText("保存已观看棋谱");
    panel_2.add(lblAutoSave);
    lblAutoSave.setToolTipText(autoSaveTip);

    chkAutoSave = new JFontCheckBox();
    chkAutoSave.setToolTipText(autoSaveTip);
    GridBagConstraints gbc_chkAutoSave = new GridBagConstraints();
    gbc_chkAutoSave.anchor = GridBagConstraints.WEST;
    gbc_chkAutoSave.gridx = 1;
    gbc_chkAutoSave.gridy = 7;
    mainPanel.add(chkAutoSave, gbc_chkAutoSave);
    chkAutoSave.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            if (chkAutoSave.isSelected()) Utils.showMsg(autoSaveTip);
          }
        });

    JPanel buttonPanel = new JPanel();
    getContentPane().add(buttonPanel, BorderLayout.SOUTH);

    JButton btnSave = new JFontButton("保存设置");
    btnSave.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            saveConfig();
            // setVisible(false);
          }
        });
    buttonPanel.add(btnSave);

    JButton btnStart = new JFontButton("开始跑谱贡献");
    btnStart.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            saveConfig();
            Lizzie.frame.startContributeEngine();
            Lizzie.frame.openContributeView();
            setVisible(false);
          }
        });
    buttonPanel.add(btnStart);
    pack();
    setLocationRelativeTo(owner);
    setVisible(true);
  }

  private void saveConfig() {
    Lizzie.config.contributeEnginePath = this.txtEnginePath.getText();
    Lizzie.config.uiConfig.put("contribute-engine-path", Lizzie.config.contributeEnginePath);
    Lizzie.config.contributeConfigPath = this.txtConfigPath.getText();
    Lizzie.config.uiConfig.put("contribute-config-path", Lizzie.config.contributeConfigPath);
    Lizzie.config.contributeUserName = this.txtUserName.getText();
    Lizzie.config.uiConfig.put("contribute-user-name", Lizzie.config.contributeUserName);
    Lizzie.config.contributePassword = this.txtPassword.getText();
    Lizzie.config.uiConfig.put("contribute-password", Lizzie.config.contributePassword);
    Lizzie.config.contributeBatchGames = this.txtGames.getText();
    Lizzie.config.uiConfig.put("contribute-batch-games", Lizzie.config.contributeBatchGames);
    Lizzie.config.contributeShowEstimate = this.chkShowOwnerShip.isSelected();
    Lizzie.config.uiConfig.put("contribute-show-estimate", Lizzie.config.contributeShowEstimate);
    Lizzie.config.contributeUseCommand = this.chkUseCommand.isSelected();
    Lizzie.config.uiConfig.put("contribute-use-command", Lizzie.config.contributeUseCommand);
    Lizzie.config.contributeCommand = this.txtCommand.getText();
    Lizzie.config.uiConfig.put("contribute-command", Lizzie.config.contributeCommand);
    Lizzie.config.contributeAutoSave = this.chkAutoSave.isSelected();
    Lizzie.config.uiConfig.put("contribute-auto-save", Lizzie.config.contributeAutoSave);
  }
}
