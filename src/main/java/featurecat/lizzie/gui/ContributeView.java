package featurecat.lizzie.gui;

import featurecat.lizzie.Config;
import featurecat.lizzie.Lizzie;
import featurecat.lizzie.util.DocType;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.NoSuchElementException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.border.BevelBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.SoftBevelBorder;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

public class ContributeView extends JDialog {
  private JTextField txtGameIndex;
  private JTextField txtAutoPlayInterval;
  private JLabel lblGameInfos;
  private String result = "B+4";
  private JLabel lblCurrentGameResult;
  private JLabel lblGameType;
  private JLabel lblKomi;
  private JLabel lblTip;
  private JIMSendTextPane console;
  private JTextPane txtRules;
  private int finishedGames = 0;
  private int playingGames = 0;
  private int watchingGameIndex = 0;
  private JTextField txtMoveNumber;
  private ArrayDeque<DocType> docQueue;
  private int checkCount = 0;
  private int scrollLength = 0;
  private ScheduledExecutorService executor;

  public ContributeView(Window owner) {
    super(owner);
    setTitle("KataGo跑谱贡献");
    setResizable(false);
    JPanel mainPanel = new JPanel();
    mainPanel.setBorder(new EmptyBorder(0, 0, 0, 0));
    getContentPane().add(mainPanel, BorderLayout.CENTER);
    GridBagLayout gbl_mainPanel = new GridBagLayout();
    gbl_mainPanel.rowWeights = new double[] {1.0, 0.0, 0.0, 0.0, 0.0};
    gbl_mainPanel.columnWeights = new double[] {1.0};
    //    gbl_mainPanel.columnWidths = new int[] {336, 0};
    //    gbl_mainPanel.rowHeights = new int[] {37, 37, 0, 37, 0, 0};
    //    gbl_mainPanel.columnWeights = new double[] {1.0, Double.MIN_VALUE};
    //    gbl_mainPanel.rowWeights = new double[] {0.0, 0.0, 1.0, 0.0, 1.0, Double.MIN_VALUE};
    mainPanel.setLayout(gbl_mainPanel);

    JPanel labelPanel = new JPanel();
    labelPanel.setBorder(new SoftBevelBorder(BevelBorder.LOWERED));
    GridBagConstraints gbc_panel_1 = new GridBagConstraints();
    gbc_panel_1.insets = new Insets(0, 0, 0, 0);
    gbc_panel_1.fill = GridBagConstraints.BOTH;
    gbc_panel_1.gridx = 0;
    gbc_panel_1.gridy = 0;
    mainPanel.add(labelPanel, gbc_panel_1);

    //		JPanel gameInfoPanel = new JPanel();
    //		mainPanel.add(gameInfoPanel);

    lblGameInfos = new JFontLabel("已完成0局,正在进行0局,共0局,正在观看第0局");
    labelPanel.add(lblGameInfos);

    JPanel gameControlPanel = new JPanel();
    gameControlPanel.setLayout(new FlowLayout(1, 4, 2));
    gameControlPanel.setBorder(new SoftBevelBorder(BevelBorder.LOWERED));
    GridBagConstraints gbc_gameControlPanel = new GridBagConstraints();
    gbc_gameControlPanel.fill = GridBagConstraints.BOTH;
    gbc_gameControlPanel.insets = new Insets(0, 0, 0, 0);
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

    JPanel playPanel = new JPanel();
    playPanel.setLayout(new FlowLayout(1, 4, 4));
    playPanel.setBorder(new SoftBevelBorder(BevelBorder.LOWERED));
    GridBagConstraints gbc_playPanel = new GridBagConstraints();
    gbc_playPanel.insets = new Insets(0, 0, 0, 0);
    gbc_playPanel.fill = GridBagConstraints.BOTH;
    gbc_playPanel.gridx = 0;
    gbc_playPanel.gridy = 2;
    mainPanel.add(playPanel, gbc_playPanel);

    JButton btnFirst = new JFontButton("|<");
    playPanel.add(btnFirst);

    JButton btnPrevious10 = new JFontButton("<<");
    playPanel.add(btnPrevious10);

    JButton btnPrevious1 = new JFontButton("<");
    playPanel.add(btnPrevious1);

    JButton btnNext1 = new JFontButton(">");
    playPanel.add(btnNext1);

    JButton btnNext10 = new JFontButton(">>");
    playPanel.add(btnNext10);

    JButton btnLast = new JFontButton(">|");
    playPanel.add(btnLast);

    btnFirst.setMargin(new Insets(2, 5, 2, 5));
    btnPrevious10.setMargin(new Insets(2, 5, 2, 5));
    btnPrevious1.setMargin(new Insets(2, 8, 2, 8));
    btnNext1.setMargin(new Insets(2, 8, 2, 8));
    btnNext10.setMargin(new Insets(2, 5, 2, 5));
    btnLast.setMargin(new Insets(2, 5, 2, 5));

    txtMoveNumber = new JFontTextField();
    playPanel.add(txtMoveNumber);
    txtMoveNumber.setColumns(3);

    JButton btnGoto = new JFontButton("跳转");
    playPanel.add(btnGoto);
    btnGoto.setMargin(new Insets(2, 5, 2, 5));

    JCheckBox chkAlwaysLastMove = new JFontCheckBox("总是最后一手");
    playPanel.add(chkAlwaysLastMove);

    JPanel autoPlayPanel = new JPanel();
    autoPlayPanel.setLayout(new FlowLayout(1, 2, 2));
    autoPlayPanel.setBorder(new SoftBevelBorder(BevelBorder.LOWERED));
    GridBagConstraints gbc_autoPlayPanel = new GridBagConstraints();
    gbc_autoPlayPanel.insets = new Insets(0, 0, 0, 0);
    gbc_autoPlayPanel.fill = GridBagConstraints.BOTH;
    gbc_autoPlayPanel.gridx = 0;
    gbc_autoPlayPanel.gridy = 3;
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
    chkIgnoreNone19.setText("跳过非19x19");
    autoPlayPanel.add(chkIgnoreNone19);

    JPanel panel = new JPanel();
    panel.setLayout(new FlowLayout(1, 10, 2));
    panel.setBorder(new SoftBevelBorder(BevelBorder.LOWERED));
    GridBagConstraints gbc_panel = new GridBagConstraints();
    gbc_panel.fill = GridBagConstraints.BOTH;
    gbc_panel.gridx = 0;
    gbc_panel.gridy = 4;
    mainPanel.add(panel, gbc_panel);

    lblGameType = new JLabel("本局类型: 自对弈");
    panel.add(lblGameType);

    lblKomi = new JLabel("贴目: 7.5");
    panel.add(lblKomi);

    lblCurrentGameResult = new JFontLabel("结果: ");
    panel.add(lblCurrentGameResult);

    JButton btnHideShowResult = new JFontButton();
    btnHideShowResult.setText(Lizzie.config.contributeHideResult ? "显示" : "隐藏");
    setResult(result);
    btnHideShowResult.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            Lizzie.config.contributeHideResult = !Lizzie.config.contributeHideResult;
            btnHideShowResult.setText(Lizzie.config.contributeHideResult ? "显示" : "隐藏");
            setResult(result);
          }
        });
    panel.add(btnHideShowResult);

    btnHideShowResult.setMargin(new Insets(1, 7, 1, 7));

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

    docQueue = new ArrayDeque<>();
    console = new JIMSendTextPane(false);
    console.setFont(new Font(Config.sysDefaultFontName, Font.PLAIN, Config.frameFontSize));
    console.setBorder(BorderFactory.createEmptyBorder());
    console.setEditable(false);
    console.setBackground(Color.BLACK);
    console.setForeground(Color.LIGHT_GRAY);

    Font gtpFont;
    try {
      gtpFont =
          Font.createFont(
              Font.TRUETYPE_FONT,
              Thread.currentThread()
                  .getContextClassLoader()
                  .getResourceAsStream("fonts/SourceCodePro-Regular.ttf"));

      gtpFont = gtpFont.deriveFont(Font.PLAIN, Config.frameFontSize);
    } catch (IOException | FontFormatException e) {
      e.printStackTrace();
      gtpFont = new Font(Font.MONOSPACED, Font.PLAIN, Config.frameFontSize);
    }
    console.setFont(gtpFont);

    JScrollPane scrollConsole = new JScrollPane(console);
    JPanel consoleTextPane = new JPanel();
    consoleTextPane.setLayout(new BorderLayout());
    lblTip = new JFontLabel("New label");
    lblTip.setText("正在初始化...正在下载权重...");
    consoleTextPane.add(lblTip, BorderLayout.SOUTH);
    consoleTextPane.add(scrollConsole, BorderLayout.CENTER);
    consolePanel.add(consoleTextPane, BorderLayout.CENTER);
    consoleTextPane.setPreferredSize(
        new Dimension((int) consoleTextPane.getPreferredSize().getWidth(), Config.menuHeight * 7));

    JPanel consoleButtonPane = new JPanel();
    consoleButtonPane.setLayout(new BorderLayout(0, 0));
    JButton btnHideShowConsole = new JFontButton("隐藏控制台");
    consoleButtonPane.add(btnHideShowConsole);
    JButton btnMore = new JFontButton("更多信息");
    consoleButtonPane.add(btnMore, BorderLayout.EAST);
    consolePanel.add(consoleButtonPane, BorderLayout.SOUTH);

    executor = Executors.newSingleThreadScheduledExecutor();
    executor.execute(this::read);

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

  public void setType(String text) {
    lblGameType.setText("本局类型: " + text);
  }

  public void setKomi(String text) {
    lblKomi.setText("贴目: " + text);
  }

  public void setResult(String text) {
    result = text;
    lblCurrentGameResult.setText("结果: " + (Lizzie.config.contributeHideResult ? "---" : text));
  }

  public void setGames(int finishedGames, int playingGames) {
    this.finishedGames = finishedGames;
    this.playingGames = playingGames;
    updateLblGameInfos();
  }

  public void setWathGameIndex(int watchingGameIndex) {
    this.watchingGameIndex = watchingGameIndex;
    updateLblGameInfos();
  }

  private void updateLblGameInfos() {
    lblGameInfos.setText(
        "已完成"
            + finishedGames
            + "局,正在进行"
            + playingGames
            + "局,共"
            + (finishedGames + playingGames)
            + "局,正在观看第"
            + watchingGameIndex
            + "局");
  }

  private void checkConsole() {
    if (console.getText().length() > 200000) {
      console.setText(
          console
              .getText()
              .substring(console.getText().length() - 70000, console.getText().length()));
      console.setCaretPosition(console.getDocument().getLength());
    }
  }

  public void addDocs(DocType doc) {
    SimpleAttributeSet attrSet = new SimpleAttributeSet();
    StyleConstants.setForeground(attrSet, doc.contentColor);
    if (doc.isCommand) {
      StyleConstants.setFontFamily(attrSet, Lizzie.config.uiFontName);
    }
    StyleConstants.setFontSize(attrSet, doc.fontSize);
    insert(doc.content, attrSet);
  }

  private void insert(String str, AttributeSet attrSet) {
    Document doc = console.getDocument();
    try {
      doc.insertString(doc.getLength(), str, attrSet);
    } catch (BadLocationException e) {
      e.printStackTrace();
    }
  }

  private void setDocs(String str, Color col, boolean isCommand, int fontSize) {
    DocType doc = new DocType();
    doc.content = str;
    doc.contentColor = col;
    doc.isCommand = isCommand;
    doc.fontSize = fontSize;
    docQueue.addLast(doc);
  }

  public void addErrorLine(String line) {
    setDocs(line, new Color(255, 0, 0), false, Config.frameFontSize);
  }

  public void addLine(String line) {
    if (line == null || line.trim().length() == 0) {
      return;
    }
    setDocs(" " + line, Color.GREEN, false, Config.frameFontSize);
  }

  private void read() {
    while (true) {
      try {
        Thread.sleep(100);
      } catch (InterruptedException e1) {
        // TODO Auto-generated catch block
        e1.printStackTrace();
      }
      synchronized (docQueue) {
        while (!docQueue.isEmpty()) {
          try {
            DocType doc = docQueue.removeFirst();
            addDocs(doc);
          } catch (NoSuchElementException e) {
            e.printStackTrace();
            docQueue = new ArrayDeque<>();
            docQueue.clear();
            break;
          }
        }
      }
      checkCount++;
      if (checkCount > 300) {
        checkCount = 0;
        checkConsole();
      } else {
        int length = console.getDocument().getLength();
        if (length != scrollLength) {
          scrollLength = length;
          console.setCaretPosition(scrollLength);
        }
      }
    }
  }
}
