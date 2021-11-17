package featurecat.lizzie.gui;

import featurecat.lizzie.Config;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.border.EmptyBorder;

public class ContributeView extends JDialog {
  private JTextField txtGameIndex;
  private JTextField txtAutoPlayInterval;
  private JLabel lblGameInfos;
  private JTextPane txtRules;

  public ContributeView(Window owner) {
    super(owner);
    setTitle("KataGo跑谱贡献");

    JPanel mainPanel = new JPanel();
    mainPanel.setBorder(new EmptyBorder(2, 0, 0, 5));
    getContentPane().add(mainPanel, BorderLayout.CENTER);
    GridBagLayout gbl_mainPanel = new GridBagLayout();
    gbl_mainPanel.columnWidths = new int[] {336, 0};
    gbl_mainPanel.rowHeights = new int[] {37, 37, 37, 0};
    gbl_mainPanel.columnWeights = new double[] {0.0, Double.MIN_VALUE};
    gbl_mainPanel.rowWeights = new double[] {0.0, 0.0, 0.0, Double.MIN_VALUE};
    mainPanel.setLayout(gbl_mainPanel);

    //		JPanel gameInfoPanel = new JPanel();
    //		mainPanel.add(gameInfoPanel);

    lblGameInfos = new JFontLabel("已完成10局,正在进行5局,可观看15局,正在观看第3局");
    GridBagConstraints gbc_lblGameInfos = new GridBagConstraints();
    gbc_lblGameInfos.fill = GridBagConstraints.VERTICAL;
    gbc_lblGameInfos.insets = new Insets(0, 0, 5, 0);
    gbc_lblGameInfos.gridx = 0;
    gbc_lblGameInfos.gridy = 0;
    mainPanel.add(lblGameInfos, gbc_lblGameInfos);

    JPanel gameControlPanel = new JPanel();
    GridBagConstraints gbc_gameControlPanel = new GridBagConstraints();
    gbc_gameControlPanel.fill = GridBagConstraints.VERTICAL;
    gbc_gameControlPanel.insets = new Insets(0, 0, 5, 0);
    gbc_gameControlPanel.gridx = 0;
    gbc_gameControlPanel.gridy = 1;
    mainPanel.add(gameControlPanel, gbc_gameControlPanel);

    JButton btnPrevious = new JFontButton("上一局");
    gameControlPanel.add(btnPrevious);

    JButton btnNext = new JFontButton("下一局");
    gameControlPanel.add(btnNext);

    JLabel lblGoto = new JFontLabel("跳转");
    gameControlPanel.add(lblGoto);

    txtGameIndex = new JFontTextField();
    gameControlPanel.add(txtGameIndex);
    txtGameIndex.setColumns(3);

    JButton btnConfirm = new JFontButton("确定");
    gameControlPanel.add(btnConfirm);

    JPanel autoPlayPanel = new JPanel();
    GridBagConstraints gbc_autoPlayPanel = new GridBagConstraints();
    gbc_autoPlayPanel.fill = GridBagConstraints.VERTICAL;
    gbc_autoPlayPanel.gridx = 0;
    gbc_autoPlayPanel.gridy = 2;
    mainPanel.add(autoPlayPanel, gbc_autoPlayPanel);

    JCheckBox chkAutoPlay = new JFontCheckBox("自动播放");
    autoPlayPanel.add(chkAutoPlay);

    JLabel lblAutoPlayInterval = new JFontLabel("每手时间(秒)");
    autoPlayPanel.add(lblAutoPlayInterval);

    txtAutoPlayInterval = new JFontTextField();
    autoPlayPanel.add(txtAutoPlayInterval);
    txtAutoPlayInterval.setColumns(5);

    JCheckBox chkAutoPlayNextGame = new JFontCheckBox("自动播放下一局");
    chkAutoPlayNextGame.setText("跳转下一局");
    autoPlayPanel.add(chkAutoPlayNextGame);

    JCheckBox chkIgnoreNone19 = new JFontCheckBox("跳过非19路");
    autoPlayPanel.add(chkIgnoreNone19);

    txtRules = new JTextPane();
    txtRules.setText(
        "本局规则:\r\n"
            + "胜负判断:数子\r\n"
            + "打劫:严格禁全同\r\n"
            + "允许棋块自杀:是\r\n"
            + "还棋头:否\r\n"
            + "让子贴还(让N子): 贴还N目\r\n"
            + "收后贴还0.5目: 否");

    JPanel ruleAndButtonPanel = new JPanel();
    getContentPane().add(ruleAndButtonPanel, BorderLayout.SOUTH);

    ruleAndButtonPanel.setLayout(new BorderLayout());
    JPanel rulePanel = new JPanel();
    rulePanel.setLayout(new BorderLayout(0, 0));
    rulePanel.add(txtRules);
    ruleAndButtonPanel.add(rulePanel, BorderLayout.CENTER);

    JButton btnShowHideRules = new JFontButton("隐藏规则");
    btnShowHideRules.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            txtRules.setVisible(false);
            pack();
          }
        });
    rulePanel.add(btnShowHideRules, BorderLayout.SOUTH);
    JPanel buttonPanel = new JPanel();
    ruleAndButtonPanel.add(buttonPanel, BorderLayout.SOUTH);

    JButton btnShutdown = new JFontButton("结束跑谱贡献");
    buttonPanel.add(btnShutdown);

    JPanel consolePanel = new JPanel();
    consolePanel.setLayout(new BorderLayout());
    getContentPane().add(consolePanel, BorderLayout.NORTH);

    JIMSendTextPane textPane = new JIMSendTextPane(false);
    textPane.setFont(new Font(Config.sysDefaultFontName, Font.PLAIN, Config.frameFontSize));
    textPane.setPreferredSize(
        new Dimension((int) textPane.getPreferredSize().getWidth(), Config.menuHeight * 7));

    JScrollPane scrollConsole = new JScrollPane(textPane);
    consolePanel.add(scrollConsole, BorderLayout.CENTER);

    JButton btnHideShowConsole = new JFontButton("隐藏控制台");
    consolePanel.add(btnHideShowConsole, BorderLayout.SOUTH);

    btnHideShowConsole.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            scrollConsole.setVisible(false);
            pack();
          }
        });

    pack();
    setLocationRelativeTo(owner);
    setVisible(true);
  }
}
