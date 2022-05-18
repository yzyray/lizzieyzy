package featurecat.lizzie.gui;

import featurecat.lizzie.Config;
import featurecat.lizzie.Lizzie;
import featurecat.lizzie.analysis.EngineManager;
import featurecat.lizzie.rules.BoardHistoryNode;
import featurecat.lizzie.rules.Movelist;
import featurecat.lizzie.rules.SGFParser;
import featurecat.lizzie.util.Utils;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.IOException;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.ResourceBundle;
import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Document;
import org.jdesktop.swingx.util.OS;
import org.json.JSONArray;

public class BottomToolbar extends JPanel {
  private final ResourceBundle resourceBundle = Lizzie.resourceBundle;
  public boolean showDetail = Lizzie.config.showDetailedToolbarMenu && Lizzie.config.isChinese;
  JButton firstButton;
  JButton lastButton;
  JButton clearButton;
  JButton countButton;
  JButton finalScore;
  JButton forward10;
  JButton backward10;
  JButton forward1;
  JButton gotomove;
  JButton backward1;
  JButton openfile;
  JButton savefile;
  public JButton analyse;
  JButton kataEstimate;
  JButton heatMap;
  JButton backMain;
  JButton setMain;
  JButton batchOpen;
  JButton refresh;
  JPopupMenu yike;
  JPopupMenu sharePopup;
  JButton tryPlay;
  JButton analyzeList;
  JButton move;
  JButton moveRank;
  JButton coords;
  JButton liveButton;
  // ActionListener liveButtonListener;
  //  ActionListener shareListener;
  JButton badMoves;
  JButton autoPlay;
  JButton deleteMove;
  JButton share;
  JButton flashAnalyze;

  PanelWithToolTips buttonPane;
  //  JPanel buttonPane2;

  JButton rightMove;
  JButton leftMove;
  private JButton detail;
  public SetKomi setkomi;

  // int savedbroadmid;
  JTextField txtMoveNumber;
  private int changeMoveNumber = 0;
  // public static boolean isAutoAna = false;
  public boolean isAutoPlay = false;
  public int firstMove = -1;
  public int lastMove = -1;
  // public boolean star1tAutoAna = false;
  // public boolean keepAutoAna = false;
  Thread threadNotiAna;
  //  public int pkBlackWins = 0;
  //  public int pkWhiteWins = 0;

  //  public long pkBlackPlayouts = 0;
  //  public long pkWhitePlayouts = 0;
  //
  //  public int pkBlackTimeAll = 0;
  //  public int pkWhiteTimeAll = 0;
  //
  //  public int pkBlackWinAsBlack = 0;
  //  public int pkBlackWinAsWhite = 0;
  //  public int pkWhiteWinAsBlack = 0;
  //  public int pkWhiteWinAsWhite = 0;

  //  public int timeb = -1;
  //  public int timew = -1;

  //  public int doublePassGame = 0;
  public int maxMoveGame = 0;
  public int maxGameMoves = 450;
  public boolean checkGameMaxMove = false;

  // public int minGanmeMove = 100;
  //  public int minMove = -1;
  //  public boolean checkGameMinMove = false;

  // public boolean isEnginePk = false;
  public int displayedSubBoardBranchLength = 1;
  public int engineBlackToolbar = -1;
  public int engineWhiteToolbar = -1;
  // public String engineBlackName = "";
  //  public String engineWhiteName = "";
  // public boolean isSameEngine = false;
  //  public int pkResignMoveCounts = 2;
  //  public double pkResginWinrate = 10;
  // public boolean isEnginePkBatch = false;
  public boolean enginePkSaveWinrate = false;

  public boolean isRandomMove = false;
  public int randomMove = 16;
  public double randomDiffWinrate = 0.3;
  // public int EnginePkBatchNumber = 1;
  // public int EnginePkBatchNumberNow = 1;
  public String batchPkNameToolbar = "";
  //  public String SF = "";
  public boolean isGenmoveToolbar = false;
  public boolean isEngineGameHandicapToolbar = false;
  public boolean AutosavePk = true;
  public boolean exChangeToolbar = true;
  public JCheckBox chkAutoAnalyse;
  public JCheckBox chkAnaTime;
  public JCheckBox chkAnaPlayouts;
  public JCheckBox chkAnaFirstPlayouts;
  public JCheckBox chkAnaAutoSave;
  public JCheckBox chkAnaBlack;
  public JCheckBox chkAnaWhite;

  private JCheckBox chkShowBlack;
  private JCheckBox chkShowWhite;

  public JCheckBox chkAutoMain;
  public JCheckBox chkAutoSub;

  public JTextField txtAutoMain;
  public JTextField txtAutoSub;

  public JCheckBox chkAutoPlay;
  public JCheckBox chkAutoPlayBlack;
  public JCheckBox chkAutoPlayWhite;
  public JCheckBox chkAutoPlayTime;
  public JCheckBox chkAutoPlayPlayouts;
  public JCheckBox chkAutoPlayFirstPlayouts;

  public JTextField txtAnaTime;
  public JTextField txtAnaPlayouts;
  public JTextField txtAnaFirstPlayouts;
  public JTextField txtFirstAnaMove;
  public JTextField txtLastAnaMove;

  public JTextField txtAutoPlayTime;
  public JTextField txtAutoPlayPlayouts;
  public JTextField txtAutoPlayFirstPlayouts;
  public int anaPanelOrder = 0;
  public int enginePkOrder = 1;
  public int autoPlayOrder = 2;
  public boolean isPkStop = false;
  public boolean isPkGenmoveStop = false;
  public boolean isPkStopGenmoveB;
  // JButton cancelAutoAna;

  JLabel lblchkShowBlack;
  JLabel lblchkShowWhite;

  JLabel lblchkAutoAnalyse;
  JLabel lbltxtAnaTime;
  JLabel lbltxtAnaPlayouts;
  JLabel lblAnaFirstPlayouts;
  JLabel lblAnaMove;
  JLabel lblAnaAutoSave;
  JLabel lblAnaMoveAnd;

  JLabel lblAutoPlay;
  JLabel lblAutoPlayBlack;
  JLabel lblAutoPlayWhite;
  JLabel lblAutoPlayTime;
  JLabel lblAutoPlayPlayouts;
  JLabel lblAutoPlayFirstPlayouts;

  JPanel anaPanel;
  JButton start;
  JPanel autoPlayPanel;
  JPanel enginePkPanel;
  private Thread threadAnalyzeAllNode;
  private Thread threadAnalyzeDiffNode;

  private BoardHistoryNode autoAnaStartNode;
  // public int enginePKGenmoveBestMovesSize;
  public JCheckBox chkenginePk;
  // public JCheckBox chkenginePkgenmove;
  public JCheckBox chkenginePkTime;
  public JCheckBox chkenginePkPlayouts;
  public JCheckBox chkenginePkFirstPlayputs;

  public JCheckBox chkenginePkBatch;
  public JCheckBox chkenginePkContinue;
  public ArrayList<Movelist> startGame;
  // public JCheckBox chkenginePkAutosave;

  public JButton btnStartPk;
  public JButton btnEnginePkConfig;
  public JButton btnEnginePkStop;
  public JButton btnEngineMannul;

  JLabel lblenginePk;
  // JLabel lblgenmove;
  JLabel lblenginePkTime;
  JLabel lblenginePkTimeWhite;
  public JLabel lblengineBlack;
  public JLabel lblengineWhite;
  public JLabel lblenginePkPlayputs;
  public JLabel lblenginePkFirstPlayputs;
  public JLabel lblenginePkPlayputsWhite;
  public JLabel lblenginePkFirstPlayputsWhite;

  JLabel lblenginePkBatch;
  JLabel lblenginePkExchange;
  JLabel lblenginePkAutosave;

  public JLabel lblenginePkResult;

  public JTextField txtenginePkTime;
  public JTextField txtenginePkTimeWhite;
  public JTextField txtenginePkPlayputs;
  public JTextField txtenginePkFirstPlayputs;
  public JTextField txtenginePkPlayputsWhite;
  public JTextField txtenginePkFirstPlayputsWhite;
  public JTextField txtenginePkBatch;
  Message msg;
  public JComboBox<String> enginePkBlack;
  ItemListener enginePkBlackLis;
  public JComboBox<String> enginePkWhite;
  ItemListener enginePkWhiteLis;

  private ImageIcon iconUp;
  private ImageIcon iconDown;
  public boolean rightMode = false;
  public int currentEnginePkSgfNum = -1;
  private boolean isAutoPlayMain = false;
  private boolean isAutoPlaySub = false;

  public BottomToolbar() {
    Color hsbColor =
        Color.getHSBColor(
            Color.RGBtoHSB(232, 232, 232, null)[0],
            Color.RGBtoHSB(232, 232, 232, null)[1],
            Color.RGBtoHSB(232, 232, 232, null)[2]);
    this.setBackground(hsbColor);

    setLayout(null);
    NumberFormat nf = NumberFormat.getIntegerInstance();
    nf.setGroupingUsed(false);

    rightMove = new JButton("》");
    leftMove = new JButton("《");
    deleteMove =
        new JFontButton(Lizzie.resourceBundle.getString("BottomToolbar.deleteMove")); // ("删除");
    badMoves =
        new JFontButton(Lizzie.resourceBundle.getString("BottomToolbar.badMoves")); // ("超级鹰眼");
    share = new JFontButton(Lizzie.resourceBundle.getString("BottomToolbar.share")); // ("分享");
    flashAnalyze = new JFontButton(Lizzie.resourceBundle.getString("BottomToolbar.flashAnalyze"));
    liveButton =
        new JFontButton(Lizzie.resourceBundle.getString("BottomToolbar.liveButton")); // ("直播");
    clearButton =
        new JFontButton(Lizzie.resourceBundle.getString("BottomToolbar.clearButton")); // ("清空棋盘");
    firstButton = new JFontButton("|<");
    lastButton = new JFontButton(">|");
    countButton =
        new JFontButton(Lizzie.resourceBundle.getString("BottomToolbar.countButton")); // ("形势判断");
    finalScore =
        new JFontButton(Lizzie.resourceBundle.getString("BottomToolbar.finalScore")); // ("终局数子");
    forward10 = new JFontButton(">>");
    backward10 = new JFontButton("<<");
    gotomove =
        new JFontButton(Lizzie.resourceBundle.getString("BottomToolbar.gotomove")); // ("跳转");
    savefile =
        new JFontButton(Lizzie.resourceBundle.getString("BottomToolbar.savefile")); // ("保存");
    backward1 = new JFontButton("<");
    forward1 = new JFontButton(">");
    openfile =
        new JFontButton(Lizzie.resourceBundle.getString("BottomToolbar.openfile")); // ("打开");
    kataEstimate =
        new JFontButton(
            Lizzie.resourceBundle.getString("BottomToolbar.kataEstimate")); // ("Kata评估");
    analyse = new JFontButton(Lizzie.resourceBundle.getString("BottomToolbar.analyse")); // ("分析");
    heatMap = new JFontButton(Lizzie.resourceBundle.getString("BottomToolbar.heatMap")); // ("纯网络");
    backMain =
        new JFontButton(Lizzie.resourceBundle.getString("BottomToolbar.backMain")); // ("返回主分支");
    setMain =
        new JFontButton(Lizzie.resourceBundle.getString("BottomToolbar.setMain")); // ("设为主分支");
    batchOpen =
        new JFontButton(Lizzie.resourceBundle.getString("BottomToolbar.batchOpen")); // ("批量分析");
    refresh = new JFontButton(Lizzie.resourceBundle.getString("BottomToolbar.refresh")); // ("刷新");
    tryPlay = new JFontButton(Lizzie.resourceBundle.getString("BottomToolbar.tryPlay")); // ("试下");
    analyzeList =
        new JFontButton(Lizzie.resourceBundle.getString("BottomToolbar.analyzeList")); // ("选点列表");
    move = new JFontButton(Lizzie.resourceBundle.getString("BottomToolbar.move")); // ("手数");
    moveRank = new JFontButton(Lizzie.resourceBundle.getString("BottomToolbar.moveRank"));
    coords = new JFontButton(Lizzie.resourceBundle.getString("BottomToolbar.coords")); // ("坐标");
    autoPlay =
        new JFontButton(Lizzie.resourceBundle.getString("BottomToolbar.autoPlay")); // ("自动播放");

    iconUp = new ImageIcon();

    try {
      iconUp.setImage(ImageIO.read(getClass().getResourceAsStream("/assets/up.png")));
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    iconDown = new ImageIcon();
    try {
      iconDown.setImage(ImageIO.read(getClass().getResourceAsStream("/assets/down.png")));
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    detail = new JButton("");

    //    buttonPane2=new JPanel();
    //    buttonPane2.setLayout(null);
    //   this.add(buttonPane2);

    buttonPane = new PanelWithToolTips();
    buttonPane.setLayout(null);
    this.add(buttonPane);

    add(rightMove);
    add(leftMove);
    detail.setVisible(this.showDetail);
    add(detail);
    detail.setBounds(0, 0, 20, 26);
    if (showDetail) leftMove.setBounds(Config.isScaled ? 20 : 19, 0, 20, 26);
    else leftMove.setBounds(0, 0, 20, 26);
    buttonPane.add(share);
    buttonPane.add(flashAnalyze);
    buttonPane.add(liveButton);
    buttonPane.add(deleteMove);
    buttonPane.add(badMoves);
    buttonPane.add(clearButton);
    buttonPane.add(lastButton);
    buttonPane.add(firstButton);
    buttonPane.add(countButton);
    buttonPane.add(finalScore);
    buttonPane.add(forward10);
    buttonPane.add(savefile);
    buttonPane.add(backward10);
    buttonPane.add(gotomove);
    buttonPane.add(backward1);
    buttonPane.add(forward1);
    buttonPane.add(openfile);
    buttonPane.add(kataEstimate);
    buttonPane.add(analyse);
    buttonPane.add(heatMap);
    buttonPane.add(backMain);
    buttonPane.add(setMain);
    buttonPane.add(batchOpen);
    buttonPane.add(refresh);
    buttonPane.add(tryPlay);
    buttonPane.add(analyzeList);
    buttonPane.add(moveRank);
    buttonPane.add(move);
    buttonPane.add(coords);
    buttonPane.add(autoPlay);

    rightMove.setVisible(false);
    leftMove.setVisible(false);
    firstButton.setFocusable(false);
    lastButton.setFocusable(false);
    clearButton.setFocusable(false);
    countButton.setFocusable(false);
    finalScore.setFocusable(false);
    forward10.setFocusable(false);
    backward10.setFocusable(false);
    gotomove.setFocusable(false);
    openfile.setFocusable(false);
    kataEstimate.setFocusable(false);
    analyse.setFocusable(false);
    forward1.setFocusable(false);
    backward1.setFocusable(false);
    savefile.setFocusable(false);
    detail.setFocusable(false);
    heatMap.setFocusable(false);
    backMain.setFocusable(false);
    setMain.setFocusable(false);
    batchOpen.setFocusable(false);
    refresh.setFocusable(false);
    tryPlay.setFocusable(false);
    analyzeList.setFocusable(false);
    move.setFocusable(false);
    moveRank.setFocusable(false);
    coords.setFocusable(false);
    liveButton.setFocusable(false);
    share.setFocusable(false);
    flashAnalyze.setFocusable(false);
    rightMove.setFocusable(false);
    leftMove.setFocusable(false);
    badMoves.setFocusable(false);
    deleteMove.setFocusable(false);
    autoPlay.setFocusable(false);

    share.setMargin(new Insets(0, 0, 0, 0));
    flashAnalyze.setMargin(new Insets(0, 0, 0, 0));
    deleteMove.setMargin(new Insets(0, 0, 0, 0));
    autoPlay.setMargin(new Insets(0, 0, 0, 0));
    badMoves.setMargin(new Insets(0, 0, 0, 0));
    firstButton.setMargin(new Insets(0, 0, 0, 0));
    lastButton.setMargin(new Insets(0, 0, 0, 0));
    clearButton.setMargin(new Insets(0, 0, 0, 0));
    countButton.setMargin(new Insets(0, 0, 0, 0));
    finalScore.setMargin(new Insets(0, 0, 0, 0));
    forward10.setMargin(new Insets(0, 0, 0, 0));
    backward10.setMargin(new Insets(0, 0, 0, 0));
    gotomove.setMargin(new Insets(0, 0, 0, 0));
    openfile.setMargin(new Insets(0, 0, 0, 0));
    kataEstimate.setMargin(new Insets(0, 0, 0, 0));
    analyse.setMargin(new Insets(0, 0, 0, 0));
    forward1.setMargin(new Insets(0, 0, 0, 0));
    backward1.setMargin(new Insets(0, 0, 0, 0));
    savefile.setMargin(new Insets(0, 0, 0, 0));
    detail.setMargin(new Insets(0, 0, 0, 0));
    heatMap.setMargin(new Insets(0, 0, 0, 0));
    backMain.setMargin(new Insets(0, 0, 0, 0));
    setMain.setMargin(new Insets(0, 0, 0, 0));
    batchOpen.setMargin(new Insets(0, 0, 0, 0));
    refresh.setMargin(new Insets(0, 0, 0, 0));
    tryPlay.setMargin(new Insets(0, 0, 0, 0));
    analyzeList.setMargin(new Insets(0, 0, 0, 0));
    coords.setMargin(new Insets(0, 0, 0, 0));
    move.setMargin(new Insets(0, 0, 0, 0));
    moveRank.setMargin(new Insets(0, 0, 0, 0));
    liveButton.setMargin(new Insets(0, 0, 0, 0));
    rightMove.setMargin(new Insets(0, 0, 0, 0));
    leftMove.setMargin(new Insets(0, 0, 0, 0));

    //    int extraLength = 0;
    //    if (System.getProperty("os.name").toLowerCase().contains("mac")
    //        && !Lizzie.config.useJavaLooks) {
    //      extraLength = 20;
    //    }
    //
    //    if (Config.frameFontSize > 12) {
    //      extraLength += (Config.frameFontSize - 12) * 5;
    //    }
    setButtonSize(autoPlay, false);
    setButtonSize(flashAnalyze, false);
    setButtonSize(deleteMove, true);
    setButtonSize(badMoves, false);
    setButtonSize(share, true);
    setButtonSize(liveButton, true);
    setButtonSize(kataEstimate, false);
    setButtonSize(batchOpen, false);
    setButtonSize(openfile, true);
    setButtonSize(savefile, true);
    setButtonSize(analyzeList, false);
    setButtonSize(refresh, true);
    setButtonSize(analyse);
    setButtonSize(tryPlay, true);
    setButtonSize(setMain, false);
    setButtonSize(backMain, false);
    setButtonSize(clearButton, false);
    setButtonSize(countButton, false);
    setButtonSize(finalScore, false);
    setButtonSize(heatMap, false);
    setButtonSize(move, true);
    setButtonSize(moveRank, false);
    setButtonSize(coords, true);
    setButtonSize(gotomove, true);
    firstButton.setSize(30, 26);
    backward10.setSize(30, 26);
    backward1.setSize(30, 26);
    forward1.setSize(30, 26);
    forward10.setSize(30, 26);
    lastButton.setSize(30, 26);
    // NumberFormat nf = NumberFormat.getNumberInstance();

    // nf.setGroupingUsed(false);
    // nf.setParseIntegerOnly(true);

    txtMoveNumber = new JTextField();
    txtMoveNumber.setSize(28, 24);
    buttonPane.add(txtMoveNumber);
    txtMoveNumber.setColumns(3);

    txtMoveNumber.addKeyListener(
        new KeyListener() {
          @Override
          public void keyPressed(KeyEvent arg0) {
            int key = arg0.getKeyCode();
            if (key == '\n') {
              checkMove();
              txtMoveNumber.setFocusable(false);
              txtMoveNumber.setFocusable(true);
              txtMoveNumber.setBackground(Color.WHITE);
              txtMoveNumber.setText("");
              if (changeMoveNumber != 0) Lizzie.board.goToMoveNumberBeyondBranch(changeMoveNumber);
            }
          }

          @Override
          public void keyReleased(KeyEvent e) {}

          @Override
          public void keyTyped(KeyEvent e) {}
        });

    leftMove.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            rightMode = false;
            setTxtUnfocuse();
            reSetButtonLocation();
          }
        });

    rightMove.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            rightMode = true;
            setTxtUnfocuse();
            reSetButtonLocation();
          }
        });

    refresh.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            Lizzie.frame.refreshCurrentMove();
          }
        });

    moveRank.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            Lizzie.config.toggleShowMoveRankMark();
            Lizzie.frame.refresh();
          }
        });

    JPopupMenu flashAnalyzePopup = new JPopupMenu();

    final JFontMenuItem flashAnalyzeAllGame =
        new JFontMenuItem(resourceBundle.getString("Menu.flashAnalyzeAllGame"));
    flashAnalyzePopup.add(flashAnalyzeAllGame);
    flashAnalyzeAllGame.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            Lizzie.frame.flashAnalyzeGame(true, false);
          }
        });

    final JFontMenuItem flashAnalyzePartGame =
        new JFontMenuItem(resourceBundle.getString("Menu.flashAnalyzePartGame"));
    flashAnalyzePopup.add(flashAnalyzePartGame);
    flashAnalyzePartGame.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            Lizzie.frame.flashAnalyzePart();
          }
        });

    final JFontMenuItem flashAnalyzeAllBranches =
        new JFontMenuItem(resourceBundle.getString("Menu.flashAnalyzeAllBranches"));
    flashAnalyzePopup.add(flashAnalyzeAllBranches);
    flashAnalyzeAllBranches.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            Lizzie.frame.flashAnalyzeGame(false, true);
          }
        });

    final JFontMenuItem flashAnalyzeSettings =
        new JFontMenuItem(resourceBundle.getString("Menu.flashAnalyzeSettings"));
    flashAnalyzePopup.add(flashAnalyzeSettings);
    flashAnalyzeSettings.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            Lizzie.frame.flashAnalyzeSettings();
          }
        });
    flashAnalyzePopup.setVisible(true);
    flashAnalyzePopup.setVisible(false);

    flashAnalyze.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            flashAnalyzePopup.show(
                buttonPane,
                flashAnalyze.getX(),
                flashAnalyze.getY() - flashAnalyzePopup.getHeight());
          }
        });

    sharePopup = new JPopupMenu();
    JFontMenuItem shareCurSgf =
        new JFontMenuItem(
            Lizzie.resourceBundle.getString("BottomToolbar.shareCurSgf")); // ("分享当前棋谱");
    shareCurSgf.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            Lizzie.frame.shareSGF();
          }
        });

    JFontMenuItem shareBatchSgf =
        new JFontMenuItem(
            Lizzie.resourceBundle.getString("BottomToolbar.shareBatchSgf")); // ("分享当前棋谱");
    shareBatchSgf.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            Lizzie.frame.batchShareSGF();
          }
        });

    //    JMenuItem shareHistory =
    //        new JMenuItem(
    //            Lizzie.resourceBundle.getString("BottomToolbar.shareHistory")); // ("历史记录(本地)");
    //    shareHistory.addActionListener(
    //        new ActionListener() {
    //          public void actionPerformed(ActionEvent e) {
    //            File file = new File("");
    //            String courseFile = "";
    //            try {
    //              courseFile = file.getCanonicalPath();
    //            } catch (IOException se) {
    //              // TODO Auto-generated catch block
    //            }
    //            try {
    //              File linkHistory = new File(courseFile + File.separator + "shareLinks.txt"); //
    // 创建文件对象
    //              if (!linkHistory.exists()) {
    //                Utils.showMsg(
    //                    Lizzie.resourceBundle.getString("BottomToolbar.linkHistoryHint")); // );
    //                //                Message msg = new Message();
    //                //                msg.setMessage("历史记录为空");
    //                //                msg.setVisible(true);
    //                return;
    //              }
    //              Desktop.getDesktop().open(linkHistory); // 启动已在本机桌面上注册的关联应用程序，打开文件文件file。
    //            } catch (IOException e1) {
    //              // TODO Auto-generated catch block
    //              Utils.showMsg(Lizzie.resourceBundle.getString("BottomToolbar.linkHistoryFail"));
    // // );
    //              //   Message msg = new Message();
    //              //              msg.setMessage("打开失败");
    //              //              msg.setVisible(true);
    //            }
    //          }
    //        });

    JFontMenuItem editHistoryRemote =
        new JFontMenuItem(
            Lizzie.resourceBundle.getString(
                "BottomToolbar.editHistoryRemote")); // ("查询(修改)已分享棋谱信息");
    editHistoryRemote.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            Lizzie.frame.openPrivateKifuSearch();
          }
        });

    JFontMenuItem shareHistoryRemote =
        new JFontMenuItem(
            Lizzie.resourceBundle.getString("BottomToolbar.shareHistoryRemote")); // ("公开棋谱查询");
    shareHistoryRemote.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            Lizzie.frame.openPublicKifuSearch();
          }
        });

    sharePopup.add(shareCurSgf);
    sharePopup.add(shareBatchSgf);
    sharePopup.add(editHistoryRemote);
    sharePopup.add(shareHistoryRemote);
    // sharePopup.add(shareHistory);
    sharePopup.setVisible(true);
    sharePopup.setVisible(false);

    yike = new JPopupMenu();
    JFontMenuItem yikeLive =
        new JFontMenuItem(Lizzie.resourceBundle.getString("BottomToolbar.yikeLive")); // ("弈客直播");
    yikeLive.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            Lizzie.frame.bowser(
                "https://home.yikeweiqi.com/#/live",
                (Lizzie.resourceBundle.getString("BottomToolbar.yikeLive")),
                true);
          }
        });
    yike.add(yikeLive);

    JFontMenuItem yikeRoom =
        new JFontMenuItem(Lizzie.resourceBundle.getString("BottomToolbar.yikeRoom")); // ("弈客大厅");
    yikeRoom.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            Lizzie.frame.bowser(
                "https://home.yikeweiqi.com/#/game",
                (Lizzie.resourceBundle.getString("BottomToolbar.yikeRoom")),
                true);
          }
        });
    yike.add(yikeRoom);

    JFontMenuItem foxKifu =
        new JFontMenuItem(Lizzie.resourceBundle.getString("Menu.foxKifu")); // ("野狐");
    foxKifu.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            Lizzie.frame.openFoxReq();
          }
        });
    yike.add(foxKifu);

    JFontMenuItem syncBoardJava =
        new JFontMenuItem(
            Lizzie.resourceBundle.getString("BottomToolbar.syncBoardJava")); // ("棋盘同步");
    syncBoardJava.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            Lizzie.frame.openReadBoardJava();
          }
        });
    yike.add(syncBoardJava);

    JFontMenuItem syncBoard =
        new JFontMenuItem(Lizzie.resourceBundle.getString("BottomToolbar.syncBoard")); // ("棋盘同步");
    syncBoard.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            Lizzie.frame.openBoardSync();
          }
        });
    if (OS.isWindows()) yike.add(syncBoard);
    yike.setVisible(true);
    yike.setVisible(false);

    autoPlay.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            AutoPlay autoPlay = new AutoPlay();
            autoPlay.setVisible(true);
          }
        });

    deleteMove.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            Lizzie.board.deleteMoveNoHint();
          }
        });

    badMoves.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            Lizzie.frame.toggleBadMoves();
          }
        });

    tryPlay.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            Lizzie.frame.tryPlay(true);
          }
        });
    analyzeList.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            Lizzie.frame.toggleBestMoves();
          }
        });
    move.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            Lizzie.config.toggleShowMoveNumber();
            Lizzie.frame.refresh();
          }
        });
    coords.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            Lizzie.config.toggleCoordinates();
            Lizzie.frame.refresh();
          }
        });
    JPopupMenu autoAnalyzePopup = new JPopupMenu();
    final JFontMenuItem autoAnalyze =
        new JFontMenuItem(resourceBundle.getString("Menu.autoAnalyze")); // ("自动分析(A)");
    // aboutItem.setMnemonic('A');
    autoAnalyze.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            StartAnaDialog newgame = new StartAnaDialog(false, Lizzie.frame);
            newgame.setVisible(true);
            if (newgame.isCancelled()) {
              LizzieFrame.toolbar.resetAutoAna();
              return;
            }
          }
        });
    autoAnalyzePopup.add(autoAnalyze);

    final JFontMenuItem batchAnalyze =
        new JFontMenuItem(resourceBundle.getString("Menu.batchAnalyze")); // ("批量分析(Ctrl+O)");
    batchAnalyze.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            Lizzie.frame.openFileWithAna(false);
          }
        });
    autoAnalyzePopup.add(batchAnalyze);

    final JFontMenuItem batchAnalysisMode =
        new JFontMenuItem(resourceBundle.getString("Menu.batchAnalysisMode")); // ("批量分析(闪电模式)");
    batchAnalysisMode.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            Lizzie.frame.openFileWithAna(true);
          }
        });
    autoAnalyzePopup.add(batchAnalysisMode);

    final JFontMenuItem stopAutoAnalyze =
        new JFontMenuItem(resourceBundle.getString("Menu.stopAutoAnalyze")); // ("停止自动(批量)分析");
    autoAnalyzePopup.addSeparator();
    autoAnalyzePopup.add(stopAutoAnalyze);
    stopAutoAnalyze.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            LizzieFrame.toolbar.stopAutoAna(true, true);
          }
        });

    final JFontMenuItem batchAnalyzeTable =
        new JFontMenuItem(resourceBundle.getString("Menu.batchAnalyzeTable")); // ("批量分析进度表");
    autoAnalyzePopup.add(batchAnalyzeTable);
    batchAnalyzeTable.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            Lizzie.frame.openAnalysisTable();
          }
        });
    autoAnalyzePopup.setVisible(true);
    autoAnalyzePopup.setVisible(false);
    batchOpen.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            autoAnalyzePopup.show(
                buttonPane, batchOpen.getX(), batchOpen.getY() - autoAnalyzePopup.getHeight());
          }
        });

    backMain.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            Lizzie.frame.moveToMainTrunk();
          }
        });
    setMain.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            Lizzie.frame.setAsMain();
          }
        });
    heatMap.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            Lizzie.leelaz.toggleHeatmap(false);
          }
        });
    detail.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            if (Lizzie.frame.toolbarHeight == 26) {
              Lizzie.frame.toolbarHeight = 70;

            } else {
              Lizzie.frame.toolbarHeight = 26;
            }
            Lizzie.frame.reSetLoc();
          }
        });
    analyse.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            Lizzie.frame.togglePonderMannul();
            if (!Lizzie.leelaz.isPondering()) Lizzie.frame.refresh();
            setTxtUnfocuse();
          }
        });
    forward10.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            if (EngineManager.isEngineGame) return;
            for (int i = 0; i < 10; i++) Lizzie.board.nextMove(false);
            if (Lizzie.frame.commentEditPane.isVisible()) Lizzie.frame.setCommentEditable(false);
            Lizzie.board.clearAfterMove();
            Lizzie.frame.refresh();
            setTxtUnfocuse();
          }
        });
    backward10.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            if (EngineManager.isEngineGame) return;
            for (int i = 0; i < 10; i++) Lizzie.board.previousMove(false);
            if (Lizzie.frame.commentEditPane.isVisible()) Lizzie.frame.setCommentEditable(false);
            Lizzie.board.clearAfterMove();
            Lizzie.frame.refresh();
            setTxtUnfocuse();
          }
        });
    forward1.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            if (EngineManager.isEngineGame) return;
            if (Lizzie.frame.commentEditPane.isVisible()) Lizzie.frame.setCommentEditable(false);
            Lizzie.board.nextMove(true);
            setTxtUnfocuse();
          }
        });
    backward1.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            if (EngineManager.isEngineGame) return;
            if (Lizzie.frame.commentEditPane.isVisible()) Lizzie.frame.setCommentEditable(false);
            if (Lizzie.frame.isPlayingAgainstLeelaz || Lizzie.frame.isAnaPlayingAgainstLeelaz) {
              Lizzie.board.previousMove(false);
              Lizzie.board.previousMove(true);
            } else Lizzie.board.previousMove(true);
            setTxtUnfocuse();
          }
        });
    gotomove.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            if (EngineManager.isEngineGame) return;
            checkMove();
            if (Lizzie.frame.commentEditPane.isVisible()) Lizzie.frame.setCommentEditable(false);
            txtMoveNumber.setBackground(Color.WHITE);
            txtMoveNumber.setText("");
            // Lizzie.board.savelist(changeMoveNumber);
            // Lizzie.board.setlist();
            if (changeMoveNumber != 0) Lizzie.board.goToMoveNumberBeyondBranch(changeMoveNumber);

            setTxtUnfocuse();
          }
        });

    liveButton.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            yike.show(buttonPane, yike.getX(), yike.getY() - yike.getHeight());
          }
        });

    share.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            sharePopup.show(buttonPane, share.getX(), share.getY() - sharePopup.getHeight());
          }
        });

    // backward1.addActionListener(
    // new ActionListener() {
    // public void actionPerformed(ActionEvent e) {
    // Input.undo(1);
    // setAllUnfocuse();
    // }
    // });
    openfile.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            Lizzie.frame.openFile();
            setTxtUnfocuse();
          }
        });
    savefile.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            Lizzie.frame.saveOriFile();
            setTxtUnfocuse();
          }
        });
    clearButton.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            if (Lizzie.engineManager.isEngineGame()) return;
            Lizzie.board.clear(false);
            if (Lizzie.leelaz.isPondering()) {
              Lizzie.leelaz.ponder();
            }
            Lizzie.frame.refresh();
            setTxtUnfocuse();
          }
        });
    lastButton.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            if (Lizzie.engineManager.isEngineGame()) return;
            if (Lizzie.frame.commentEditPane.isVisible()) Lizzie.frame.setCommentEditable(false);
            Lizzie.frame.lastMove();
            setTxtUnfocuse();
          }
        });
    firstButton.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            if (Lizzie.engineManager.isEngineGame()) return;
            if (Lizzie.frame.commentEditPane.isVisible()) Lizzie.frame.setCommentEditable(false);
            Lizzie.frame.firstMove();
            setTxtUnfocuse();
          }
        });
    countButton.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            if (Lizzie.frame.isCounting) {
              Lizzie.frame.clearKataEstimate();
              Lizzie.frame.refresh();
              Lizzie.frame.isCounting = false;
              Lizzie.frame.estimateResults.setVisible(false);
            } else {
              Lizzie.frame.countstones(true);
            }
            setTxtUnfocuse();
          }
        });
    finalScore.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            Lizzie.frame.toggleScoreMode();
            setTxtUnfocuse();
          }
        });
    kataEstimate.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            Lizzie.frame.toggleShowKataEstimate();
            setTxtUnfocuse();
          }
        });

    this.addMouseListener(
        new MouseListener() {
          public void mouseClicked(MouseEvent e) {
            setTxtUnfocuse();
          }

          @Override
          public void mousePressed(MouseEvent e) {
            // TODO Auto-generated method stub
          }

          @Override
          public void mouseReleased(MouseEvent e) {
            // TODO Auto-generated method stub

          }

          @Override
          public void mouseEntered(MouseEvent e) {
            // TODO Auto-generated method stub

          }

          @Override
          public void mouseExited(MouseEvent e) {
            // TODO Auto-generated method stub

          }
        });
    anaPanel = new JPanel();
    anaPanel.setLayout(null);
    add(anaPanel);
    anaPanel.setBounds(0, 26, 400, 44);
    anaPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));

    autoPlayPanel = new JPanel();
    autoPlayPanel.setLayout(null);
    add(autoPlayPanel);
    autoPlayPanel.setBounds(1000, 26, 495, 44);
    autoPlayPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));

    enginePkPanel = new JPanel();
    enginePkPanel.setLayout(null);
    add(enginePkPanel);
    enginePkPanel.setBounds(400, 26, 600, 44);
    enginePkPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));

    chkAutoAnalyse = new JCheckBox();
    anaPanel.add(chkAutoAnalyse);
    lblchkAutoAnalyse = new JLabel("自动分析");
    chkAutoAnalyse.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            if (chkAutoAnalyse.isSelected()) {
              startAutoAna();
            } else {
              stopAutoAna(true, true);
            }
            setTxtUnfocuse();
            //            if (chkAutoAnalyse.isSelected()) {
            //              Lizzie.frame.removeInput();
            //            } else {
            //              Lizzie.frame.addInput();
            //            }
          }
        });
    anaPanel.add(lblchkAutoAnalyse);
    chkAutoAnalyse.setBounds(1, 1, 20, 18);
    lblchkAutoAnalyse.setBounds(21, 0, 60, 20);
    chkAnaBlack = new JCheckBox("黑");
    chkAnaWhite = new JCheckBox("白");
    chkAnaBlack.setFocusable(false);
    chkAnaWhite.setFocusable(false);
    chkAnaBlack.setSelected(true);
    chkAnaWhite.setSelected(true);
    chkAnaBlack.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            // TBD
            setTxtUnfocuse();
          }
        });
    chkAnaWhite.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            // TBD
            setTxtUnfocuse();
          }
        });
    anaPanel.add(chkAnaBlack);
    anaPanel.add(chkAnaWhite);
    chkAnaBlack.setBounds(68, 1, 40, 20);

    lblAnaMove = new JLabel("手数:");
    lblAnaMove.setBounds(108, 0, 40, 20);
    anaPanel.add(lblAnaMove);

    txtFirstAnaMove = new JTextField();
    txtFirstAnaMove.setDocument(new IntDocument());
    txtFirstAnaMove.setText("1");
    anaPanel.add(txtFirstAnaMove);
    txtFirstAnaMove.setBounds(138, 2, 30, 18);

    txtFirstAnaMove.addFocusListener(
        new FocusListener() {
          @Override
          public void focusLost(FocusEvent e) {
            // 失去焦点执行的代码
            try {
              firstMove = Integer.parseInt(txtFirstAnaMove.getText().replace(" ", ""));
            } catch (Exception ex) {
            }
          }

          @Override
          public void focusGained(FocusEvent e) {
            // 获得焦点执行的代码
          }
        });

    lblAnaMoveAnd = new JLabel("到");
    lblAnaMoveAnd.setBounds(170, 0, 15, 20);
    anaPanel.add(lblAnaMoveAnd);

    txtLastAnaMove = new JTextField();
    txtLastAnaMove.setDocument(new IntDocument());
    anaPanel.add(txtLastAnaMove);
    txtLastAnaMove.setBounds(185, 2, 30, 18);

    txtLastAnaMove.addFocusListener(
        new FocusListener() {
          @Override
          public void focusLost(FocusEvent e) {
            // 失去焦点执行的代码
            try {
              lastMove = Integer.parseInt(txtLastAnaMove.getText().replace(" ", ""));
            } catch (Exception ex) {
            }
          }

          @Override
          public void focusGained(FocusEvent e) {
            // 获得焦点执行的代码
          }
        });
    lblAnaFirstPlayouts = new JLabel("首位计算量:");
    chkAnaFirstPlayouts = new JCheckBox();
    chkAnaFirstPlayouts.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            // TBD
            setTxtUnfocuse();
          }
        });

    anaPanel.add(chkAnaFirstPlayouts);
    anaPanel.add(lblAnaFirstPlayouts);
    chkAnaFirstPlayouts.setBounds(215, 1, 20, 18);
    lblAnaFirstPlayouts.setBounds(234, 0, 80, 20);
    txtAnaFirstPlayouts = new JTextField();
    txtAnaFirstPlayouts.setDocument(new IntDocument());
    anaPanel.add(txtAnaFirstPlayouts);
    txtAnaFirstPlayouts.setBounds(298, 2, 42, 18);

    start = new JButton("开始");
    start.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            if (Lizzie.config.isAutoAna) {
              start.setText("开始");
              stopAutoAna(true, true);
            } else {
              start.setText("终止");
              startAutoAna();
            }
          }
        });
    JButton stopGo = new JButton("暂停");
    stopGo.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            Lizzie.leelaz.togglePonder();
            if (Lizzie.leelaz.isPondering()) stopGo.setText("暂停");
            else stopGo.setText("继续");
          }
        });
    start.setMargin(new Insets(0, 0, 0, 0));
    stopGo.setMargin(new Insets(0, 0, 0, 0));
    start.setFocusable(false);
    stopGo.setFocusable(false);
    start.setBounds(340, 1, 30, 20);
    stopGo.setBounds(369, 1, 30, 20);
    anaPanel.add(start);
    anaPanel.add(stopGo);

    chkAnaAutoSave = new JCheckBox();
    chkAnaAutoSave.setSelected(true);
    anaPanel.add(chkAnaAutoSave);
    chkAnaAutoSave.setBounds(1, 22, 20, 20);
    lblAnaAutoSave = new JLabel("保存棋谱");
    anaPanel.add(lblAnaAutoSave);
    lblAnaAutoSave.setBounds(21, 22, 50, 20);
    chkAnaAutoSave.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            // TBD
            setTxtUnfocuse();
          }
        });

    chkAnaWhite.setBounds(68, 22, 40, 20);
    chkAnaPlayouts = new JCheckBox();
    chkAnaPlayouts.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            // TBD
            setTxtUnfocuse();
          }
        });
    lbltxtAnaPlayouts = new JLabel("总计算量:");
    anaPanel.add(chkAnaPlayouts);
    anaPanel.add(lbltxtAnaPlayouts);
    chkAnaPlayouts.setBounds(104, 22, 20, 20);
    lbltxtAnaPlayouts.setBounds(124, 22, 80, 20);
    txtAnaPlayouts = new JTextField();
    txtAnaPlayouts.setDocument(new IntDocument());
    anaPanel.add(txtAnaPlayouts);
    txtAnaPlayouts.setBounds(175, 23, 50, 18);

    chkAnaTime = new JCheckBox();
    chkAnaTime.setSelected(true);
    lbltxtAnaTime = new JLabel("按时间(秒):");

    chkAnaTime.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            // TBD
            setTxtUnfocuse();
          }
        });

    anaPanel.add(chkAnaTime);
    anaPanel.add(lbltxtAnaTime);
    chkAnaTime.setBounds(225, 22, 20, 20);
    lbltxtAnaTime.setBounds(245, 22, 80, 20);
    txtAnaTime = new JTextField();
    txtAnaTime.setDocument(new IntDocument());
    txtAnaTime.setText("2");
    anaPanel.add(txtAnaTime);
    txtAnaTime.setBounds(305, 23, 25, 18);

    JButton analysisTable = new JButton("批量进度表");
    analysisTable.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            Lizzie.frame.openAnalysisTable();
          }
        });
    analysisTable.setMargin(new Insets(0, 0, 0, 0));
    analysisTable.setBounds(330, 22, 69, 20);
    analysisTable.setFocusable(false);
    anaPanel.add(analysisTable);

    chkAutoPlay = new JCheckBox();
    chkAutoPlay.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            // TBD
            setTxtUnfocuse();
            isAutoPlay = chkAutoPlay.isSelected();
          }
        });

    chkShowBlack = new JCheckBox();
    chkShowWhite = new JCheckBox();
    chkShowBlack.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            // TBD
            setTxtUnfocuse();
            if (Lizzie.config.showDoubleMenu) {
              LizzieFrame.menu.setChkShowBlack(chkShowBlack.isSelected());
            }
          }
        });
    chkShowWhite.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            // TBD
            setTxtUnfocuse();
            if (Lizzie.config.showDoubleMenu) {
              LizzieFrame.menu.setChkShowWhite(chkShowWhite.isSelected());
            }
          }
        });
    lblchkShowBlack = new JLabel("显示黑");
    lblchkShowWhite = new JLabel("显示白");

    JLabel autoMain = new JLabel("自动播放(大)(秒)");
    JLabel autoSub = new JLabel("自动变化图(毫秒)");
    chkAutoMain = new JCheckBox();
    chkAutoSub = new JCheckBox();
    chkAutoMain.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            autoPlayMain(false);
            setTxtUnfocuse();
          }
        });
    chkAutoSub.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            // TBD
            // autoPlaySub();
            Runnable runnable =
                new Runnable() {
                  public void run() {
                    Lizzie.config.autoReplayBranch = chkAutoSub.isSelected();
                    Lizzie.config.uiConfig.put(
                        "auto-replay-branch", Lizzie.config.autoReplayBranch);
                    try {
                      Thread.sleep(1000);
                    } catch (InterruptedException e) {
                      // TODO Auto-generated catch block
                      e.printStackTrace();
                    }
                    if (Lizzie.config.autoReplayBranch) {
                      Lizzie.frame.autoReplayBranch();
                    }
                  }
                };
            Thread thread = new Thread(runnable);
            thread.start();
            setTxtUnfocuse();
          }
        });
    txtAutoMain = new JTextField();
    txtAutoSub = new JTextField();

    chkAutoMain.setBounds(325, 1, 20, 18);
    chkAutoSub.setBounds(325, 22, 20, 18);
    autoMain.setBounds(345, 0, 100, 18);
    autoSub.setBounds(345, 22, 100, 18);
    txtAutoMain.setBounds(445, 2, 40, 18);
    txtAutoSub.setBounds(445, 23, 40, 18);
    autoPlayPanel.add(lblchkShowBlack);
    autoPlayPanel.add(lblchkShowWhite);
    autoPlayPanel.add(chkShowWhite);
    autoPlayPanel.add(chkShowBlack);

    autoPlayPanel.add(autoMain);
    autoPlayPanel.add(autoSub);
    autoPlayPanel.add(chkAutoMain);
    autoPlayPanel.add(chkAutoSub);
    autoPlayPanel.add(txtAutoMain);
    autoPlayPanel.add(txtAutoSub);

    chkShowBlack.setBounds(5, 1, 20, 18);
    lblchkShowBlack.setBounds(25, 0, 40, 18);
    chkShowWhite.setBounds(5, 22, 20, 18);
    lblchkShowWhite.setBounds(25, 22, 40, 18);
    lblAutoPlay = new JLabel("自动落子");
    autoPlayPanel.add(chkAutoPlay);
    autoPlayPanel.add(lblAutoPlay);
    chkAutoPlay.setBounds(60, 1, 20, 18);
    lblAutoPlay.setBounds(80, 0, 60, 20);
    chkAutoPlayBlack = new JCheckBox();
    chkAutoPlayWhite = new JCheckBox();
    chkAutoPlayBlack.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            // TBD
            setTxtUnfocuse();
          }
        });
    chkAutoPlayWhite.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            // TBD
            setTxtUnfocuse();
          }
        });
    lblAutoPlayBlack = new JLabel("黑");
    lblAutoPlayWhite = new JLabel("白");
    autoPlayPanel.add(lblAutoPlayBlack);
    autoPlayPanel.add(lblAutoPlayWhite);
    autoPlayPanel.add(chkAutoPlayBlack);
    autoPlayPanel.add(chkAutoPlayWhite);
    lblAutoPlayBlack.setBounds(150, 0, 20, 20);
    chkAutoPlayBlack.setBounds(130, 1, 20, 18);

    lblAutoPlayWhite.setBounds(185, 0, 20, 20);
    chkAutoPlayWhite.setBounds(165, 1, 20, 18);

    chkAutoPlayTime = new JCheckBox();
    chkAutoPlayTime.setSelected(true);
    lblAutoPlay = new JLabel("按时间(秒):");
    txtAutoPlayTime = new JTextField();
    chkAutoPlayTime.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            // TBD
            setTxtUnfocuse();
          }
        });
    autoPlayPanel.add(chkAutoPlayTime);
    autoPlayPanel.add(lblAutoPlay);
    autoPlayPanel.add(txtAutoPlayTime);
    chkAutoPlayTime.setBounds(205, 1, 20, 18);
    lblAutoPlay.setBounds(225, 0, 70, 20);
    txtAutoPlayTime.setBounds(290, 2, 33, 18);

    chkAutoPlayPlayouts = new JCheckBox();
    lblAutoPlayPlayouts = new JLabel("总计算量:");
    txtAutoPlayPlayouts = new JTextField();
    chkAutoPlayPlayouts.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            // TBD
            setTxtUnfocuse();
          }
        });
    autoPlayPanel.add(chkAutoPlayPlayouts);
    autoPlayPanel.add(lblAutoPlayPlayouts);
    autoPlayPanel.add(txtAutoPlayPlayouts);
    chkAutoPlayPlayouts.setBounds(60, 23, 20, 18);
    lblAutoPlayPlayouts.setBounds(80, 22, 60, 20);
    txtAutoPlayPlayouts.setBounds(135, 23, 50, 18);

    chkAutoPlayFirstPlayouts = new JCheckBox();
    lblAutoPlayFirstPlayouts = new JLabel("首位计算量:");
    txtAutoPlayFirstPlayouts = new JTextField();
    chkAutoPlayFirstPlayouts.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            // TBD
            setTxtUnfocuse();
          }
        });
    autoPlayPanel.add(chkAutoPlayFirstPlayouts);
    autoPlayPanel.add(lblAutoPlayFirstPlayouts);
    autoPlayPanel.add(txtAutoPlayFirstPlayouts);
    chkAutoPlayFirstPlayouts.setBounds(185, 23, 20, 18);
    lblAutoPlayFirstPlayouts.setBounds(205, 22, 70, 20);
    txtAutoPlayFirstPlayouts.setBounds(272, 23, 50, 18);

    chkenginePk = new JCheckBox();
    lblenginePk = new JLabel("引擎对战");
    enginePkPanel.add(chkenginePk);
    enginePkPanel.add(lblenginePk);

    chkenginePk.setBounds(2, 1, 20, 18);
    lblenginePk.setBounds(22, 0, 60, 18);
    chkenginePk.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            // TBD未完成
            setTxtUnfocuse();
            if (!chkenginePk.isSelected()) {
              enginePkBlack.setEnabled(false);
              enginePkWhite.setEnabled(false);
            } else {
              enginePkBlack.setEnabled(true);
              enginePkWhite.setEnabled(true);
            }
          }
        });

    // chkenginePkgenmove = new JCheckBox();
    // lblgenmove = new JLabel("genmove");
    // enginePkPanel.add(chkenginePkgenmove);
    // enginePkPanel.add(lblgenmove);
    // chkenginePkgenmove.setBounds(70, 23, 20, 18);
    // lblgenmove.setBounds(90, 22, 60, 18);

    // chkenginePkgenmove.addActionListener(
    // new ActionListener() {
    // @Override
    // public void actionPerformed(ActionEvent e) {
    // // TBD未完成
    // setTxtUnfocuse();
    // if (chkenginePkgenmove.isSelected()) {
    // chkenginePkPlayouts.setSelected(false);
    // chkenginePkPlayouts.setEnabled(false);
    // chkenginePkFirstPlayputs.setSelected(false);
    // chkenginePkFirstPlayputs.setEnabled(false);
    // } else {
    // chkenginePkFirstPlayputs.setEnabled(true);
    // chkenginePkPlayouts.setEnabled(true);
    // }
    // }
    // });

    btnEnginePkConfig = new JButton("设置");
    enginePkPanel.add(btnEnginePkConfig);
    btnEnginePkConfig.setBounds(42, 22, 35, 20);
    btnEnginePkConfig.setMargin(new Insets(0, 0, 0, 0));
    btnEnginePkConfig.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            // TBD未完成
            setTxtUnfocuse();
            EnginePkConfig engineconfig = new EnginePkConfig(true);
            engineconfig.setVisible(true);
          }
        });
    btnEnginePkStop = new JButton("暂停");
    enginePkPanel.add(btnEnginePkStop);
    btnEnginePkStop.setBounds(76, 22, 35, 20);
    btnEnginePkStop.setMargin(new Insets(0, 0, 0, 0));
    btnEnginePkStop.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            // TBD未完成
            setTxtUnfocuse();
            if (isGenmoveToolbar) {
              if (isPkStop) {
                if (!isPkGenmoveStop) {
                  Utils.showMsg(
                      Lizzie.resourceBundle.getString(
                          "BottomToolbar.genmoveStopHint")); // (BottomToolbar.genmoveStopHint);
                  //                  Message msg = new Message();
                  //                  msg.setMessage("Genmove模式下暂停后须等待最后一步落子完成");
                  //                  msg.setVisible(true);
                  return;
                }
                btnEnginePkStop.setText("暂停");
                isPkStop = false;
                if (isPkStopGenmoveB) {
                  Lizzie.engineManager
                      .engineList
                      .get(EngineManager.engineGameInfo.blackEngineIndex)
                      .nameCmd();
                  Lizzie.engineManager
                      .engineList
                      .get(EngineManager.engineGameInfo.blackEngineIndex)
                      .genmoveForPk("B");
                  if (Lizzie.config.enginePkPonder)
                    Lizzie.engineManager
                        .engineList
                        .get(EngineManager.engineGameInfo.whiteEngineIndex)
                        .ponder();
                } else {
                  Lizzie.engineManager
                      .engineList
                      .get(EngineManager.engineGameInfo.whiteEngineIndex)
                      .nameCmd();
                  Lizzie.engineManager
                      .engineList
                      .get(EngineManager.engineGameInfo.whiteEngineIndex)
                      .genmoveForPk("W");
                  if (Lizzie.config.enginePkPonder)
                    Lizzie.engineManager
                        .engineList
                        .get(EngineManager.engineGameInfo.blackEngineIndex)
                        .ponder();
                }

              } else {
                btnEnginePkStop.setText("继续");
                isPkStop = true;
                isPkGenmoveStop = false;
              }

            } else {
              if (isPkStop) {
                btnEnginePkStop.setText("暂停");
                isPkStop = false;
                if (Lizzie.config.enginePkPonder) {
                  Lizzie.engineManager
                      .engineList
                      .get(EngineManager.engineGameInfo.blackEngineIndex)
                      .ponder();
                  Lizzie.engineManager
                      .engineList
                      .get(EngineManager.engineGameInfo.whiteEngineIndex)
                      .ponder();
                } else {
                  if (Lizzie.board.getData().blackToPlay) {
                    Lizzie.engineManager
                        .engineList
                        .get(EngineManager.engineGameInfo.blackEngineIndex)
                        .ponder();
                  } else {
                    Lizzie.engineManager
                        .engineList
                        .get(EngineManager.engineGameInfo.whiteEngineIndex)
                        .ponder();
                  }
                }
              } else {
                btnEnginePkStop.setText("继续");
                Lizzie.engineManager
                    .engineList
                    .get(EngineManager.engineGameInfo.blackEngineIndex)
                    .nameCmd();
                Lizzie.engineManager
                    .engineList
                    .get(EngineManager.engineGameInfo.whiteEngineIndex)
                    .nameCmd();
                isPkStop = true;
              }
              //  Lizzie.engineManager.startInfoTime = System.currentTimeMillis();
              //  Lizzie.engineManager.gameTime = System.currentTimeMillis();
            }
            LizzieFrame.menu.toggleDoubleMenuGameStatus();
          }
        });

    chkenginePkTime = new JCheckBox();
    chkenginePkTime.setSelected(true);
    lblenginePkTime = new JLabel("时间(秒) 黑");
    lblenginePkTimeWhite = new JLabel("白");
    txtenginePkTime = new JTextField();
    txtenginePkTime.setDocument(new IntDocument());
    txtenginePkTime.setText("2");
    txtenginePkTimeWhite = new JTextField();
    txtenginePkTimeWhite.setDocument(new IntDocument());
    txtenginePkTimeWhite.setText("2");
    enginePkPanel.add(chkenginePkTime);
    enginePkPanel.add(lblenginePkTime);
    enginePkPanel.add(txtenginePkTime);
    enginePkPanel.add(lblenginePkTimeWhite);
    enginePkPanel.add(txtenginePkTimeWhite);
    chkenginePkTime.setBounds(110, 23, 20, 18);
    lblenginePkTime.setBounds(130, 22, 70, 18);
    txtenginePkTime.setBounds(190, 24, 23, 18);
    lblenginePkTimeWhite.setBounds(215, 22, 15, 18);
    txtenginePkTimeWhite.setBounds(227, 24, 23, 18);
    txtenginePkTime.setEnabled(false);
    txtenginePkTimeWhite.setEnabled(false);
    chkenginePkTime.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            setTxtUnfocuse();
            if (chkenginePkTime.isSelected()) {
              txtenginePkTime.setEnabled(true);
              txtenginePkTimeWhite.setEnabled(true);
            } else {
              txtenginePkTime.setEnabled(false);
              txtenginePkTimeWhite.setEnabled(false);
            }
          }
        });

    btnStartPk = new JButton("开始");
    enginePkPanel.add(btnStartPk);
    btnStartPk.setBounds(8, 22, 35, 20);
    btnStartPk.setMargin(new Insets(0, 0, 0, 0));

    btnStartPk.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            //            Lizzie.frame.removeInput();
            //            // TBD未完成
            //            if (!chkenginePk.isSelected()) {
            //              if (msg == null || !msg.isVisible()) {
            //                msg = new Message();
            //                msg.setMessage("请先勾选[引擎对战],并选择黑白引擎后再开始");
            //                msg.setVisible(true);
            //              }
            //              Lizzie.frame.addInput();
            //              return;
            //            }

            if (!EngineManager.isEngineGame) {
              startEngineGame();
            } else {
              Lizzie.engineManager.stopEngineGame(-1, true);
            }
            setTxtUnfocuse();
          }
        });

    enginePkBlack = new JComboBox<String>();
    enginePkPanel.add(enginePkBlack);
    enginePkBlack.setBounds(90, 2, 88, 18);
    lblengineBlack = new JLabel("黑:");
    enginePkPanel.add(lblengineBlack);
    lblengineBlack.setBounds(75, 0, 15, 20);
    UI ui = new UI();
    enginePkBlack.setUI(ui);
    enginePkBlack.setBackground(Color.WHITE);
    ((Popup) ui.getPopup()).setDisplaySize(200, 200);

    lblenginePkResult = new JLabel("0:0");
    enginePkPanel.add(lblenginePkResult);
    lblenginePkResult.setBounds(186, 0, 45, 20);

    enginePkWhite = new JComboBox<String>();
    addEngineLis();
    enginePkPanel.add(enginePkWhite);
    enginePkWhite.setBounds(255, 2, 88, 18);

    UI ui2 = new UI();
    enginePkWhite.setUI(ui2);
    enginePkWhite.setBackground(Color.WHITE);
    ((Popup) ui2.getPopup()).setDisplaySize(200, 200);

    lblengineWhite = new JLabel("白:");
    enginePkPanel.add(lblengineWhite);
    lblengineWhite.setBounds(237, 0, 15, 20);

    enginePkBlack.setEnabled(false);
    enginePkWhite.setEnabled(false);

    lblenginePkFirstPlayputs = new JLabel("首位计算量  黑:");
    chkenginePkFirstPlayputs = new JCheckBox();
    txtenginePkFirstPlayputs = new JTextField();
    txtenginePkFirstPlayputs.setDocument(new IntDocument());
    enginePkPanel.add(lblenginePkFirstPlayputs);
    enginePkPanel.add(chkenginePkFirstPlayputs);
    enginePkPanel.add(txtenginePkFirstPlayputs);
    chkenginePkFirstPlayputs.setBounds(250, 23, 20, 18);
    lblenginePkFirstPlayputs.setBounds(269, 22, 90, 18);
    txtenginePkFirstPlayputs.setBounds(350, 24, 50, 18);
    lblenginePkFirstPlayputsWhite = new JLabel("白:");
    txtenginePkFirstPlayputsWhite = new JTextField();
    txtenginePkFirstPlayputsWhite.setDocument(new IntDocument());
    enginePkPanel.add(lblenginePkFirstPlayputsWhite);
    enginePkPanel.add(txtenginePkFirstPlayputsWhite);
    lblenginePkFirstPlayputsWhite.setBounds(400, 22, 20, 18);
    txtenginePkFirstPlayputsWhite.setBounds(415, 24, 50, 18);
    txtenginePkFirstPlayputs.setEnabled(false);
    txtenginePkFirstPlayputsWhite.setEnabled(false);
    chkenginePkFirstPlayputs.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            setTxtUnfocuse();
            if (chkenginePkFirstPlayputs.isSelected()) {
              txtenginePkFirstPlayputs.setEnabled(true);
              txtenginePkFirstPlayputsWhite.setEnabled(true);
            } else {
              txtenginePkFirstPlayputs.setEnabled(false);
              txtenginePkFirstPlayputsWhite.setEnabled(false);
            }
          }
        });

    chkenginePkBatch = new JCheckBox();
    lblenginePkBatch = new JLabel("多盘:");
    txtenginePkBatch = new JTextField();
    txtenginePkBatch.setDocument(new IntDocument());
    enginePkPanel.add(chkenginePkBatch);
    enginePkPanel.add(lblenginePkBatch);
    enginePkPanel.add(txtenginePkBatch);
    chkenginePkBatch.setBounds(465, 23, 20, 18);
    lblenginePkBatch.setBounds(485, 22, 30, 20);
    txtenginePkBatch.setBounds(515, 24, 30, 18);

    Document dt = txtenginePkBatch.getDocument();
    dt.addDocumentListener(
        new DocumentListener() {
          public void insertUpdate(DocumentEvent e) {
            if (EngineManager.isEngineGame || EngineManager.isPreEngineGame) {
              EngineManager.engineGameInfo.batchNumber =
                  Utils.parseTextToInt(txtenginePkBatch, EngineManager.engineGameInfo.batchNumber);
            }
          }

          public void removeUpdate(DocumentEvent e) {
            if (EngineManager.isEngineGame || EngineManager.isPreEngineGame) {
              EngineManager.engineGameInfo.batchNumber =
                  Utils.parseTextToInt(txtenginePkBatch, EngineManager.engineGameInfo.batchNumber);
            }
          }

          public void changedUpdate(DocumentEvent e) {
            if (EngineManager.isEngineGame || EngineManager.isPreEngineGame) {
              EngineManager.engineGameInfo.batchNumber =
                  Utils.parseTextToInt(txtenginePkBatch, EngineManager.engineGameInfo.batchNumber);
            }
          }
        });

    chkenginePkBatch.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            setTxtUnfocuse();
            if (chkenginePkBatch.isSelected()) txtenginePkBatch.setEnabled(true);
            else txtenginePkBatch.setEnabled(false);
          }
        });

    chkenginePkContinue = new JCheckBox();
    lblenginePkExchange = new JLabel("续弈");
    enginePkPanel.add(chkenginePkContinue);
    enginePkPanel.add(lblenginePkExchange);
    chkenginePkContinue.setBounds(545, 23, 20, 18);
    lblenginePkExchange.setBounds(565, 22, 50, 20);
    chkenginePkContinue.setToolTipText("先摆好一个局面,然后勾选就可以每盘都从这个局面开始对战");
    chkenginePkContinue.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            setTxtUnfocuse();
          }
        });

    lblenginePkPlayputs = new JLabel("总计算量  黑:");
    chkenginePkPlayouts = new JCheckBox();
    txtenginePkPlayputs = new JTextField();
    txtenginePkPlayputs.setDocument(new IntDocument());
    enginePkPanel.add(lblenginePkPlayputs);
    enginePkPanel.add(chkenginePkPlayouts);
    enginePkPanel.add(txtenginePkPlayputs);
    chkenginePkPlayouts.setBounds(355, 1, 20, 18);
    lblenginePkPlayputs.setBounds(375, 0, 70, 18);
    txtenginePkPlayputs.setBounds(445, 2, 50, 18);

    lblenginePkPlayputsWhite = new JLabel("白:");
    txtenginePkPlayputsWhite = new JTextField();
    txtenginePkPlayputsWhite.setDocument(new IntDocument());
    enginePkPanel.add(lblenginePkPlayputsWhite);
    enginePkPanel.add(txtenginePkPlayputsWhite);
    lblenginePkPlayputsWhite.setBounds(498, 0, 15, 18);
    txtenginePkPlayputsWhite.setBounds(513, 2, 50, 18);
    txtenginePkPlayputs.setEnabled(false);
    txtenginePkPlayputsWhite.setEnabled(false);
    chkenginePkPlayouts.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            setTxtUnfocuse();
            if (chkenginePkPlayouts.isSelected()) {
              txtenginePkPlayputs.setEnabled(true);
              txtenginePkPlayputsWhite.setEnabled(true);
            } else {
              txtenginePkPlayputs.setEnabled(false);
              txtenginePkPlayputsWhite.setEnabled(false);
            }
          }
        });

    btnEngineMannul = new JButton("干预");

    btnEngineMannul.setBounds(563, 1, 35, 20);
    btnEngineMannul.setMargin(new Insets(0, 0, 0, 0));
    btnEngineMannul.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            // 打开干预面板
            Manual manul = new Manual();
            manul.setVisible(true);
            setTxtUnfocuse();
          }
        });

    enginePkPanel.add(btnEngineMannul);
    // chkenginePkAutosave = new JCheckBox();
    // lblenginePkAutosave = new JLabel("自动保存");
    // enginePkPanel.add(chkenginePkAutosave);
    // enginePkPanel.add(lblenginePkAutosave);
    // chkenginePkAutosave.setBounds(515, 1, 20, 18);
    // lblenginePkAutosave.setBounds(535, 0, 50, 20);
    //
    // chkenginePkAutosave.addActionListener(
    // new ActionListener() {
    // @Override
    // public void actionPerformed(ActionEvent e) {
    // setTxtUnfocuse();
    // }
    // });
    chkAutoMain.setFocusable(false);
    chkAutoSub.setFocusable(false);
    chkAutoPlay.setFocusable(false);
    chkAutoPlayBlack.setFocusable(false);
    chkAutoPlayWhite.setFocusable(false);
    chkAutoPlayTime.setFocusable(false);
    chkAutoPlayPlayouts.setFocusable(false);
    chkAutoPlayFirstPlayouts.setFocusable(false);
    chkAutoAnalyse.setFocusable(false);
    chkAnaTime.setFocusable(false);
    chkAnaPlayouts.setFocusable(false);
    chkAnaFirstPlayouts.setFocusable(false);
    chkAnaAutoSave.setFocusable(false);
    chkShowBlack.setFocusable(false);
    chkShowWhite.setFocusable(false);
    chkAutoPlayBlack.setFocusable(false);
    chkAutoPlayWhite.setFocusable(false);
    chkAutoPlay.setFocusable(false);
    chkAutoPlayTime.setFocusable(false);
    chkAutoPlayPlayouts.setFocusable(false);
    chkAutoPlayFirstPlayouts.setFocusable(false);
    btnStartPk.setFocusable(false);
    btnEngineMannul.setFocusable(false);
    chkenginePk.setFocusable(false);
    chkenginePkTime.setFocusable(false);
    chkenginePkPlayouts.setFocusable(false);
    chkenginePkFirstPlayputs.setFocusable(false);
    // chkenginePkgenmove.setFocusable(false);
    enginePkBlack.setFocusable(false);
    enginePkWhite.setFocusable(false);
    btnEnginePkConfig.setFocusable(false);
    // chkenginePkAutosave.setFocusable(false);
    chkenginePkContinue.setFocusable(false);
    chkenginePkBatch.setFocusable(false);
    btnEnginePkStop.setFocusable(false);

    chkShowBlack.setSelected(true);
    chkShowWhite.setSelected(true);
    chkAutoPlayBlack.setSelected(true);
    chkAutoPlayWhite.setSelected(true);
    boolean persisted = Lizzie.config.persistedUi != null;
    if (persisted
        && Lizzie.config.persistedUi.optJSONArray("toolbar-parameter") != null
        && Lizzie.config.persistedUi.optJSONArray("toolbar-parameter").length() == 51) {
      JSONArray pos = Lizzie.config.persistedUi.getJSONArray("toolbar-parameter");
      if (pos.getInt(0) >= 0) {
        this.txtFirstAnaMove.setText(String.valueOf(pos.getInt(0)));
      }
      if (pos.getInt(1) > 0) {
        this.txtLastAnaMove.setText(String.valueOf(pos.getInt(1)));
      }
      if (pos.getInt(2) > 0) {
        this.chkAnaTime.setSelected(true);
      }
      if (pos.getInt(3) > 0) {
        this.txtAnaTime.setText(String.valueOf(pos.getInt(3)));
      }
      if (pos.getInt(4) > 0) {
        this.chkAnaAutoSave.setSelected(true);
      } else this.chkAnaAutoSave.setSelected(false);
      if (pos.getInt(5) > 0) {
        this.chkAnaPlayouts.setSelected(true);
      }
      if (pos.getInt(6) > 0) {
        this.txtAnaPlayouts.setText(String.valueOf(pos.getInt(6)));
      }
      if (pos.getInt(7) > 0) {
        this.chkAnaFirstPlayouts.setSelected(true);
      }
      if (pos.getInt(8) > 0) {
        this.txtAnaFirstPlayouts.setText(String.valueOf(pos.getInt(8)));
      }
      if (pos.getInt(9) > 0) {
        this.chkAutoPlayBlack.setSelected(true);
      }
      if (pos.getInt(10) > 0) {
        this.chkAutoPlayWhite.setSelected(true);
      }
      if (pos.getInt(11) > 0) {
        this.chkAutoPlayTime.setSelected(true);
      }
      if (pos.getInt(12) > 0) {
        this.txtAutoPlayTime.setText(String.valueOf(pos.getInt(12)));
      }
      if (pos.getInt(13) > 0) {
        this.chkAutoPlayPlayouts.setSelected(true);
      }
      if (pos.getInt(14) > 0) {
        this.txtAutoPlayPlayouts.setText(String.valueOf(pos.getInt(14)));
      }
      if (pos.getInt(15) > 0) {
        this.chkAutoPlayFirstPlayouts.setSelected(true);
      }
      if (pos.getInt(16) > 0) {
        this.txtAutoPlayFirstPlayouts.setText(String.valueOf(pos.getInt(16)));
      }

      if (pos.getInt(17) > 0) {
        this.txtenginePkFirstPlayputs.setText(String.valueOf(pos.getInt(17)));
      }

      if (pos.getInt(18) > 0) {
        this.txtenginePkFirstPlayputsWhite.setText(String.valueOf(pos.getInt(18)));
      }

      if (pos.getInt(19) > 0) {
        this.txtenginePkTime.setText(String.valueOf(pos.getInt(19)));
      } else this.txtenginePkTime.setText("");

      if (pos.getInt(20) > 0) {
        this.txtenginePkPlayputs.setText(String.valueOf(pos.getInt(20)));
      }

      if (pos.getInt(21) > 0) {
        this.txtenginePkPlayputsWhite.setText(String.valueOf(pos.getInt(21)));
      }

      if (pos.getInt(22) > 0) {
        this.txtenginePkBatch.setText(String.valueOf(pos.getInt(22)));
      }
      if (pos.getInt(23) > 0) {
        this.chkenginePkBatch.setSelected(true);
      }
      if (pos.getInt(24) > 0) {
        this.chkenginePkContinue.setSelected(true);
      }
      if (pos.getInt(25) > 0) {
        this.chkenginePkFirstPlayputs.setSelected(true);
        txtenginePkFirstPlayputs.setEnabled(true);
        txtenginePkFirstPlayputsWhite.setEnabled(true);
      } else {
        txtenginePkFirstPlayputs.setEnabled(false);
        txtenginePkFirstPlayputsWhite.setEnabled(false);
      }
      if (pos.getInt(26) > 0) {
        this.chkenginePkPlayouts.setSelected(true);
        txtenginePkPlayputs.setEnabled(true);
        txtenginePkPlayputsWhite.setEnabled(true);
      } else {
        txtenginePkPlayputs.setEnabled(false);
        txtenginePkPlayputsWhite.setEnabled(false);
      }
      if (pos.getInt(27) > 0) {
        this.chkenginePkTime.setSelected(true);
        txtenginePkTime.setEnabled(true);
        txtenginePkTimeWhite.setEnabled(true);
      } else {
        txtenginePkTime.setEnabled(false);
        txtenginePkTimeWhite.setEnabled(false);
      }
      //   pkResginWinrate = pos.getDouble(28);
      //  pkResignMoveCounts = pos.getInt(29);
      AutosavePk = pos.getBoolean(30);
      isGenmoveToolbar = pos.getBoolean(31);
      anaPanelOrder = pos.getInt(32);
      enginePkOrder = pos.getInt(33);
      autoPlayOrder = pos.getInt(34);
      exChangeToolbar = pos.getBoolean(35);
      maxGameMoves = pos.getInt(36);
      checkGameMaxMove = pos.getBoolean(37);
      if (pos.getInt(38) > 0) {
        txtenginePkTimeWhite.setText(String.valueOf(pos.getInt(38)));
      } else this.txtenginePkTimeWhite.setText("");
      if (pos.getInt(39) > 0) {
        this.chkAutoSub.setSelected(true);
      }
      if (pos.getInt(40) > 0) {
        this.txtAutoMain.setText(String.valueOf(pos.getInt(40)));
      }

      if (pos.getInt(41) > 0) {
        this.txtAutoSub.setText(String.valueOf(pos.getInt(41)));
      }
      //     minGanmeMove = pos.getInt(42);
      //    checkGameMinMove = pos.getBoolean(43);
      isRandomMove = pos.getBoolean(44);
      randomMove = pos.getInt(45);
      randomDiffWinrate = pos.getDouble(46);
      chkAnaBlack.setSelected(pos.getBoolean(47));
      chkAnaWhite.setSelected(pos.getBoolean(48));
      enginePkSaveWinrate = pos.getBoolean(49);
      rightMode = pos.getBoolean(50);
      setOrder();
    }
    if (chkAutoSub.isSelected()) {
      autoPlaySub();
    }
    setGenmove();
    if (chkenginePkBatch.isSelected()) txtenginePkBatch.setEnabled(true);
    else txtenginePkBatch.setEnabled(false);
    //  setFontSize(Lizzie.config.bottomFontSize);
  }

  private void setButtonSize(JButton button, boolean widden) {
    button.setSize(
        button.getFontMetrics(button.getFont()).stringWidth(button.getText()) + (widden ? 14 : 12),
        26);
  }

  private void setButtonSize(JButton button) {
    button.setSize(button.getFontMetrics(button.getFont()).stringWidth(button.getText()) + 33, 26);
  }

  public void setDetailIcon() {
    // TODO Auto-generated method stub
    if (Lizzie.frame.toolbarHeight == 26) detail.setIcon(iconUp);
    else if (Lizzie.frame.toolbarHeight == 70) detail.setIcon(iconDown);
  }

  public void restShowDetail() {
    // TODO Auto-generated method stub
    showDetail = Lizzie.config.showDetailedToolbarMenu && Lizzie.config.isChinese;
    detail.setVisible(this.showDetail);
    if (showDetail) leftMove.setBounds(Config.isScaled ? 20 : 19, 0, 20, 26);
    else leftMove.setBounds(0, 0, 20, 26);
    setDetailIcon();
    reSetButtonLocation();
  }

  public void getAllDiffNodesAfter(BoardHistoryNode node, ArrayList<BoardHistoryNode> diffList) {
    // 待完成
    if (!node.getData().blackToPlay && Lizzie.config.autoAnaDiffBlack
        || node.getData().blackToPlay && Lizzie.config.autoAnaDiffWhite) {
      if (node.analyzed) {
        if (Lizzie.config.autoAnaDiffUseWin
            && Math.abs(Lizzie.board.lastWinrateDiff(node))
                >= Lizzie.config.autoAnaDiffWinThreshold) {
          diffList.add(node.previous().get());
          diffList.add(node);
        } else if (Lizzie.config.autoAnaDiffUseScore
            && Math.abs(Lizzie.board.lastScoreMeanDiff(node))
                >= Lizzie.config.autoAnaDiffScoreThreshold) {
          diffList.add(node.previous().get());
          diffList.add(node);
        }
      }
    }
    if (Lizzie.board.getHistory().getCurrentHistoryNode().isMainTrunk()) {
      if (Lizzie.config.autoAnaEndMove != -1) {
        if (Lizzie.config.autoAnaEndMove < Lizzie.board.getHistory().getData().moveNumber) {
          return;
        }
      }
      if (!node.next().isPresent()) {
        return;
      }
    }
    if (node.numberOfChildren() > 1) {
      // Variation
      List<BoardHistoryNode> subNodes = node.getVariations();
      for (int i = subNodes.size() - 1; i >= 0; i--) {
        getAllDiffNodesAfter(subNodes.get(i), diffList);
      }
    } else if (node.numberOfChildren() == 1) {
      getAllDiffNodesAfter(node.next().orElse(null), diffList);
    }
  }

  private ArrayList<BoardHistoryNode> getDiffNodes() {
    ArrayList<BoardHistoryNode> diffList = new ArrayList<BoardHistoryNode>();
    BoardHistoryNode node = autoAnaStartNode;
    if (Lizzie.config.analyzeAllBranch) {
      getAllDiffNodesAfter(node, diffList);
    } else {
      while (node.next().isPresent()
          && (Lizzie.config.autoAnaEndMove == -1
              || node.next().get().getData().moveNumber <= Lizzie.config.autoAnaEndMove)) {
        if (!node.getData().blackToPlay && Lizzie.config.autoAnaDiffBlack
            || node.getData().blackToPlay && Lizzie.config.autoAnaDiffWhite) {
          if (node.analyzed) {
            if (Lizzie.config.autoAnaDiffUseWin
                && Math.abs(Lizzie.board.lastWinrateDiff(node))
                    >= Lizzie.config.autoAnaDiffWinThreshold) {
              diffList.add(node.previous().get());
              diffList.add(node);
            } else if (Lizzie.config.autoAnaDiffUseScore
                && Math.abs(Lizzie.board.lastScoreMeanDiff(node))
                    >= Lizzie.config.autoAnaDiffScoreThreshold) {
              diffList.add(node.previous().get());
              diffList.add(node);
            }
          }
        }
        node = node.next().get();
      }
      if (!node.getData().blackToPlay && Lizzie.config.autoAnaDiffBlack
          || node.getData().blackToPlay && Lizzie.config.autoAnaDiffWhite) {
        if (node.analyzed) {
          if (Lizzie.config.autoAnaDiffUseWin
              && Math.abs(Lizzie.board.lastWinrateDiff(node))
                  >= Lizzie.config.autoAnaDiffWinThreshold) {
            diffList.add(node.previous().get());
            diffList.add(node);
          } else if (Lizzie.config.autoAnaDiffUseScore
              && Math.abs(Lizzie.board.lastScoreMeanDiff(node))
                  >= Lizzie.config.autoAnaDiffScoreThreshold) {
            diffList.add(node.previous().get());
            diffList.add(node);
          }
        }
      }
    }
    return diffList;
  }

  private int notDuplicateSize(ArrayList<BoardHistoryNode> list) {
    int dup = 0;
    for (int i = 0; i < list.size(); i++) {
      BoardHistoryNode node = list.get(i);
      for (int j = 0; j < list.size(); j++) {
        BoardHistoryNode node2 = list.get(j);
        if (i != j && node == node2) dup++;
      }
    }
    return list.size() - dup / 2;
  }

  private boolean checkDiffAnalyze(boolean isForceStop) {
    if (autoAnaStartNode == null) return false;
    if (!Lizzie.config.autoAnaDiffEnable) return false;
    if (threadAnalyzeDiffNode != null) threadAnalyzeDiffNode.interrupt();
    if (Lizzie.config.analyzeAllBranch)
      if (threadAnalyzeAllNode != null) threadAnalyzeAllNode.interrupt();
    ArrayList<BoardHistoryNode> diffList = getDiffNodes();
    if (diffList.isEmpty()) {
      return false;
    }
    int diffMoves = diffList.size() / 2;
    int needAnalyzeMoves = notDuplicateSize(diffList);
    if (!isForceStop) {
      Lizzie.frame.isAutoAnalyzingDiffNode = true;
      Lizzie.board.clearDiffAnalyzeStatusAfter(diffList);
      Runnable runnable =
          new Runnable() {
            public void run() {
              Lizzie.board.analyzeAllDiffNodes(diffList);
              Lizzie.frame.isAutoAnalyzingDiffNode = false;
            }
          };
      threadAnalyzeDiffNode = new Thread(runnable);
      threadAnalyzeDiffNode.start();
      Utils.showMsgNoModalForTime(
          resourceBundle.getString("BottomToolbar.noticeBigDiff")
              + diffMoves
              + "("
              + resourceBundle.getString("BottomToolbar.needAnalyze")
              + needAnalyzeMoves
              + ")"
              + resourceBundle.getString("BottomToolbar.startReAnalyze"),
          3);
      return true;
    } else {
      if (Lizzie.frame.isAutoAnalyzingDiffNode) {
        return false;
      }
      Lizzie.config.isAutoAna = false;
      int ret =
          JOptionPane.showConfirmDialog(
              Lizzie.frame,
              resourceBundle.getString("BottomToolbar.noticeBigDiff")
                  + diffMoves
                  + "("
                  + resourceBundle.getString("BottomToolbar.needAnalyze")
                  + needAnalyzeMoves
                  + ")"
                  + resourceBundle.getString("BottomToolbar.askStartReAnalyze"),
              resourceBundle.getString("BottomToolbar.askStartReAnalyzeTitle"),
              JOptionPane.OK_CANCEL_OPTION);
      if (ret == JOptionPane.CANCEL_OPTION || ret == -1) {
        return false;
      }
      Lizzie.config.isAutoAna = true;
      Lizzie.frame.isAutoAnalyzingDiffNode = true;
      Lizzie.board.clearDiffAnalyzeStatusAfter(diffList);
      Runnable runnable =
          new Runnable() {
            public void run() {
              Lizzie.board.analyzeAllDiffNodes(diffList);
              Lizzie.frame.isAutoAnalyzingDiffNode = false;
            }
          };
      threadAnalyzeDiffNode = new Thread(runnable);
      threadAnalyzeDiffNode.start();
      return true;
    }
  }

  public void stopAutoAna(boolean needCheckDiff, boolean isForceStop) {
    if (needCheckDiff) {
      if (checkDiffAnalyze(isForceStop)) return;
    } else {
      if (threadAnalyzeDiffNode != null) threadAnalyzeDiffNode.interrupt();
      Lizzie.frame.isAutoAnalyzingDiffNode = false;
    }
    Lizzie.config.isAutoAna = false;
    // startAutoAna = false;
    chkAutoAnalyse.setSelected(false);
    start.setText("开始");
    if (Lizzie.leelaz.isPondering()) Lizzie.leelaz.togglePonder();
    if (Lizzie.frame.isBatchAna || LizzieFrame.toolbar.chkAnaAutoSave.isSelected()) {
      autoAnaSaveAndLoad();
    } else {
      if (Lizzie.config.analyzeAllBranch)
        if (threadAnalyzeAllNode != null) threadAnalyzeAllNode.interrupt();
      Utils.showMsgNoModal(
          Lizzie.resourceBundle.getString(
              "BottomToolbar.stopAutoAnaHint")); // (BottomToolbar.stopAutoAnaHint);
      //      if (msg == null || !msg.isVisible()) {
      //        msg = new Message();
      //        msg.setMessage("自动分析已完毕");
      //        msg.setVisible(true);
      //      }
    }
  }

  private void autoAnaSaveAndLoad() {
    if (Lizzie.leelaz.autoAnalysed) SGFParser.appendAiScoreBlunder();
    if (!Lizzie.frame.isBatchAna) {
      if (!Lizzie.leelaz.autoAnalysed) {
        if (Lizzie.config.analyzeAllBranch)
          if (threadAnalyzeAllNode != null) threadAnalyzeAllNode.interrupt();
        return;
      }
      if (LizzieFrame.curFile != null) {
        String name = LizzieFrame.curFile.getName();
        String path = LizzieFrame.curFile.getParent();
        String df = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        String prefix = name.substring(name.lastIndexOf("."));
        int num = prefix.length();
        String fileOtherName = name.substring(0, name.length() - num);
        String filename =
            path
                + File.separator
                + fileOtherName
                + "_"
                + resourceBundle.getString("Leelaz.analyzed")
                + "_"
                + df
                + ".sgf";
        File autoSaveFile = new File(filename);
        try {
          SGFParser.save(Lizzie.board, autoSaveFile.getPath());
        } catch (IOException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
        if (msg == null || !msg.isVisible()) {
          msg = new Message();
          msg.setMessageNoModal(resourceBundle.getString("Leelaz.autoAnalyzeComplete") + path);
        }
      } else {
        File file = new File("");
        String courseFile = "";
        try {
          courseFile = file.getCanonicalPath();
        } catch (IOException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
        String df = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        File autoSaveFile =
            new File(courseFile + File.separator + "AnalyzedGames" + File.separator + df + ".sgf");
        File fileParent = autoSaveFile.getParentFile();
        if (!fileParent.exists()) {
          fileParent.mkdirs();
        }
        try {
          SGFParser.save(Lizzie.board, autoSaveFile.getPath());
        } catch (IOException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
        if (msg == null || !msg.isVisible()) {
          msg = new Message();
          msg.setMessageNoModal(
              resourceBundle.getString("Leelaz.autoAnalyzeComplete")
                  + courseFile
                  + File.separator
                  + "AnalyzedGames");
        }
      }
      if (Lizzie.config.analyzeAllBranch)
        if (threadAnalyzeAllNode != null) threadAnalyzeAllNode.interrupt();
      return;
    } else {
      String name = Lizzie.frame.Batchfiles.get(Lizzie.frame.BatchAnaNum).getName();
      String path = Lizzie.frame.Batchfiles.get(Lizzie.frame.BatchAnaNum).getParent();
      String df = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
      String prefix = name.substring(name.lastIndexOf("."));
      int num = prefix.length();
      String fileOtherName = name.substring(0, name.length() - num);
      String filename =
          path
              + File.separator
              + fileOtherName
              + "_"
              + resourceBundle.getString("Leelaz.analyzed")
              + "_"
              + df
              + ".sgf";
      File autoSaveFile = new File(filename);
      try {
        SGFParser.save(Lizzie.board, autoSaveFile.getPath());
      } catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
      if (Lizzie.frame.Batchfiles.size() > (Lizzie.frame.BatchAnaNum + 1)) {
        // double komi = Lizzie.board.getHistory().getGameInfo().getKomi();
        loadAutoBatchFile();
        // Lizzie.leelaz.komi(komi);
        if (Lizzie.config.analyzeAllBranch)
          if (threadAnalyzeAllNode != null) threadAnalyzeAllNode.interrupt();
        startAutoAna();
      } else {
        Lizzie.frame.isBatchAna = false;
        LizzieFrame.toolbar.chkAnaAutoSave.setEnabled(true);
        //	isSaving = false;
        Lizzie.frame.Batchfiles = new ArrayList<File>();
        Lizzie.frame.BatchAnaNum = 0;
        Lizzie.frame.addInput(true);
        if (Lizzie.frame.analysisTable != null && Lizzie.frame.analysisTable.frame.isVisible()) {
          Lizzie.frame.analysisTable.refreshTable();
        }
        if (Lizzie.config.analyzeAllBranch)
          if (threadAnalyzeAllNode != null) threadAnalyzeAllNode.interrupt();
        if (msg == null || !msg.isVisible()) {
          msg = new Message();
          msg.setMessageNoModal(resourceBundle.getString("Leelaz.batchAutoAnalyzeComplete"));
        }
        return;
      }
    }
  }

  public void loadAutoBatchFile() {
    Lizzie.frame.BatchAnaNum = Lizzie.frame.BatchAnaNum + 1;
    try {
      if (Lizzie.frame.analysisTable != null && Lizzie.frame.analysisTable.frame.isVisible()) {
        Lizzie.frame.analysisTable.refreshTable();
      }
    } catch (Exception ex) {
    }
    Lizzie.frame.loadFile(Lizzie.frame.Batchfiles.get(Lizzie.frame.BatchAnaNum), false, true);
    try {
      LizzieFrame.toolbar.firstMove =
          Integer.parseInt(LizzieFrame.toolbar.txtFirstAnaMove.getText());
    } catch (Exception ex) {
    }
    try {
      LizzieFrame.toolbar.lastMove = Integer.parseInt(LizzieFrame.toolbar.txtLastAnaMove.getText());
    } catch (Exception ex) {
    }
    // startAutoAna = true;
  }

  public void setGenmove() {
    if (isGenmoveToolbar) {
      this.chkenginePkFirstPlayputs.setEnabled(false);
      this.chkenginePkPlayouts.setEnabled(false);
      this.txtenginePkFirstPlayputs.setEnabled(false);
      this.txtenginePkFirstPlayputsWhite.setEnabled(false);
      this.txtenginePkPlayputs.setEnabled(false);
      this.txtenginePkPlayputsWhite.setEnabled(false);
      //   this.btnEnginePkStop.setEnabled(false);
      //   this.btnEngineMannul.setEnabled(false);
    } else {
      this.chkenginePkFirstPlayputs.setEnabled(true);
      this.chkenginePkPlayouts.setEnabled(true);
      if (chkenginePkFirstPlayputs.isSelected()) {
        txtenginePkFirstPlayputs.setEnabled(true);
        txtenginePkFirstPlayputsWhite.setEnabled(true);
      } else {
        txtenginePkFirstPlayputs.setEnabled(false);
        txtenginePkFirstPlayputsWhite.setEnabled(false);
      }

      if (chkenginePkPlayouts.isSelected()) {
        txtenginePkPlayputs.setEnabled(true);
        txtenginePkPlayputsWhite.setEnabled(true);
      } else {
        txtenginePkPlayputs.setEnabled(false);
        txtenginePkPlayputsWhite.setEnabled(false);
      }
    }
  }

  public void setOrder() {
    if ((anaPanelOrder != enginePkOrder)
        && (anaPanelOrder != autoPlayOrder)
        && (enginePkOrder != autoPlayOrder)) {
      if ((anaPanelOrder < enginePkOrder) && (anaPanelOrder < autoPlayOrder)) {
        if (enginePkOrder < autoPlayOrder) {
          anaPanel.setBounds(0, 26, 400, 44);
          autoPlayPanel.setBounds(1000, 26, 495, 44);
          enginePkPanel.setBounds(400, 26, 600, 44);
        } else {
          anaPanel.setBounds(0, 26, 400, 44);
          autoPlayPanel.setBounds(400, 26, 495, 44);
          enginePkPanel.setBounds(895, 26, 600, 44);
        }
      }

      if ((enginePkOrder < anaPanelOrder) && (enginePkOrder < autoPlayOrder)) {
        if (anaPanelOrder < autoPlayOrder) {
          anaPanel.setBounds(600, 26, 400, 44);
          autoPlayPanel.setBounds(1000, 26, 495, 44);
          enginePkPanel.setBounds(0, 26, 600, 44);
        } else {
          anaPanel.setBounds(1095, 26, 400, 44);
          autoPlayPanel.setBounds(600, 26, 495, 44);
          enginePkPanel.setBounds(0, 26, 600, 44);
        }
      }

      if ((autoPlayOrder < anaPanelOrder) && (autoPlayOrder < enginePkOrder)) {
        if (anaPanelOrder < enginePkOrder) {
          anaPanel.setBounds(495, 26, 400, 44);
          autoPlayPanel.setBounds(0, 26, 495, 44);
          enginePkPanel.setBounds(895, 26, 600, 44);
        } else {
          anaPanel.setBounds(1095, 26, 400, 44);
          autoPlayPanel.setBounds(0, 26, 495, 44);
          enginePkPanel.setBounds(495, 26, 600, 44);
        }
      }
    }
  }

  public void setTxtUnfocuse() {
    if (txtMoveNumber.isFocusOwner()) {
      txtAutoPlayTime.setFocusable(false);
      txtAutoMain.setFocusable(false);
      txtAutoSub.setFocusable(false);
      txtAutoPlayPlayouts.setFocusable(false);
      txtAutoPlayFirstPlayouts.setFocusable(false);
      txtAnaTime.setFocusable(false);
      txtenginePkTime.setFocusable(false);
      txtenginePkTimeWhite.setFocusable(false);
      txtenginePkPlayputsWhite.setFocusable(false);
      txtenginePkFirstPlayputsWhite.setFocusable(false);
      txtenginePkBatch.setFocusable(false);
      txtenginePkPlayputs.setFocusable(false);
      txtenginePkFirstPlayputs.setFocusable(false);
      txtAnaPlayouts.setFocusable(false);
      txtAnaFirstPlayouts.setFocusable(false);
      txtFirstAnaMove.setFocusable(false);
      txtLastAnaMove.setFocusable(false);
      txtMoveNumber.setFocusable(false);
      txtAutoPlayTime.setFocusable(true);
      txtAutoPlayPlayouts.setFocusable(true);
      txtAutoPlayFirstPlayouts.setFocusable(true);
      txtenginePkTimeWhite.setFocusable(true);
      txtenginePkTime.setFocusable(true);
      txtAutoMain.setFocusable(true);
      txtAutoSub.setFocusable(true);
      txtenginePkPlayputs.setFocusable(true);
      txtenginePkFirstPlayputs.setFocusable(true);
      txtAnaTime.setFocusable(true);
      txtAnaPlayouts.setFocusable(true);
      txtAnaFirstPlayouts.setFocusable(true);
      txtLastAnaMove.setFocusable(true);
      txtFirstAnaMove.setFocusable(true);
      txtenginePkPlayputsWhite.setFocusable(true);
      txtenginePkFirstPlayputsWhite.setFocusable(true);
      txtenginePkBatch.setFocusable(true);
      txtMoveNumber.setFocusable(true);
    }
    if (txtAnaTime.isFocusOwner()) {
      txtAutoPlayTime.setFocusable(false);
      txtAutoMain.setFocusable(false);
      txtAutoSub.setFocusable(false);
      txtAutoPlayPlayouts.setFocusable(false);
      txtenginePkTimeWhite.setFocusable(false);
      txtAutoPlayFirstPlayouts.setFocusable(false);
      txtAnaPlayouts.setFocusable(false);
      txtenginePkPlayputs.setFocusable(false);
      txtenginePkFirstPlayputs.setFocusable(false);
      txtFirstAnaMove.setFocusable(false);
      txtenginePkPlayputsWhite.setFocusable(false);
      txtenginePkFirstPlayputsWhite.setFocusable(false);
      txtenginePkBatch.setFocusable(false);
      txtenginePkTime.setFocusable(false);
      txtenginePkPlayputs.setFocusable(false);
      txtenginePkFirstPlayputs.setFocusable(false);
      txtLastAnaMove.setFocusable(false);
      txtAnaFirstPlayouts.setFocusable(false);
      txtMoveNumber.setFocusable(false);
      txtAnaTime.setFocusable(false);
      txtAutoPlayTime.setFocusable(true);
      txtAutoPlayPlayouts.setFocusable(true);
      txtAutoPlayFirstPlayouts.setFocusable(true);
      txtAnaPlayouts.setFocusable(true);
      txtAnaFirstPlayouts.setFocusable(true);
      txtLastAnaMove.setFocusable(true);
      txtAutoMain.setFocusable(true);
      txtAutoSub.setFocusable(true);
      txtenginePkTime.setFocusable(true);
      txtenginePkPlayputs.setFocusable(true);
      txtenginePkFirstPlayputs.setFocusable(true);
      txtLastAnaMove.setFocusable(true);
      txtFirstAnaMove.setFocusable(true);
      txtAnaFirstPlayouts.setFocusable(true);
      txtenginePkTimeWhite.setFocusable(true);
      txtenginePkPlayputsWhite.setFocusable(true);
      txtenginePkFirstPlayputsWhite.setFocusable(true);
      txtenginePkBatch.setFocusable(true);
      txtMoveNumber.setFocusable(true);
      txtAnaTime.setFocusable(true);
    }
    if (txtAnaPlayouts.isFocusOwner()) {
      txtAutoPlayTime.setFocusable(false);
      txtAutoMain.setFocusable(false);
      txtAutoSub.setFocusable(false);
      txtAutoPlayPlayouts.setFocusable(false);
      txtenginePkPlayputs.setFocusable(false);
      txtenginePkFirstPlayputs.setFocusable(false);
      txtenginePkTimeWhite.setFocusable(false);
      txtAutoPlayFirstPlayouts.setFocusable(false);
      txtAnaTime.setFocusable(false);
      txtenginePkPlayputsWhite.setFocusable(false);
      txtenginePkFirstPlayputsWhite.setFocusable(false);
      txtenginePkBatch.setFocusable(false);
      txtFirstAnaMove.setFocusable(false);
      txtenginePkTime.setFocusable(false);
      txtLastAnaMove.setFocusable(false);
      txtAnaFirstPlayouts.setFocusable(false);
      txtMoveNumber.setFocusable(false);
      txtAnaPlayouts.setFocusable(false);
      txtAutoPlayTime.setFocusable(true);
      txtAutoPlayPlayouts.setFocusable(true);
      txtAutoPlayFirstPlayouts.setFocusable(true);
      txtAnaTime.setFocusable(true);
      txtLastAnaMove.setFocusable(true);
      txtFirstAnaMove.setFocusable(true);
      txtenginePkTime.setFocusable(true);
      txtenginePkPlayputs.setFocusable(true);
      txtenginePkPlayputsWhite.setFocusable(true);
      txtAutoMain.setFocusable(true);
      txtAutoSub.setFocusable(true);
      txtenginePkFirstPlayputsWhite.setFocusable(true);
      txtenginePkTimeWhite.setFocusable(true);
      txtenginePkBatch.setFocusable(true);
      txtenginePkFirstPlayputs.setFocusable(true);
      txtAnaFirstPlayouts.setFocusable(true);
      txtMoveNumber.setFocusable(true);
      txtAnaPlayouts.setFocusable(true);
    }
    if (txtAnaFirstPlayouts.isFocusOwner()) {
      txtAutoPlayTime.setFocusable(false);
      txtAutoPlayPlayouts.setFocusable(false);
      txtAutoMain.setFocusable(false);
      txtAutoSub.setFocusable(false);
      txtenginePkPlayputs.setFocusable(false);
      txtenginePkTimeWhite.setFocusable(false);
      txtenginePkFirstPlayputs.setFocusable(false);
      txtAutoPlayFirstPlayouts.setFocusable(false);
      txtAnaTime.setFocusable(false);
      txtenginePkTime.setFocusable(false);
      txtFirstAnaMove.setFocusable(false);
      txtenginePkPlayputsWhite.setFocusable(false);
      txtenginePkFirstPlayputsWhite.setFocusable(false);
      txtenginePkBatch.setFocusable(false);
      txtLastAnaMove.setFocusable(false);
      txtAnaPlayouts.setFocusable(false);
      txtMoveNumber.setFocusable(false);
      txtAnaFirstPlayouts.setFocusable(false);
      txtAutoPlayTime.setFocusable(true);
      txtAutoPlayPlayouts.setFocusable(true);
      txtAutoPlayFirstPlayouts.setFocusable(true);
      txtAnaTime.setFocusable(true);
      txtLastAnaMove.setFocusable(true);
      txtenginePkTime.setFocusable(true);
      txtAutoMain.setFocusable(true);
      txtAutoSub.setFocusable(true);
      txtFirstAnaMove.setFocusable(true);
      txtenginePkPlayputs.setFocusable(true);
      txtenginePkFirstPlayputs.setFocusable(true);
      txtenginePkPlayputsWhite.setFocusable(true);
      txtenginePkTimeWhite.setFocusable(true);
      txtenginePkFirstPlayputsWhite.setFocusable(true);
      txtenginePkBatch.setFocusable(true);
      txtAnaPlayouts.setFocusable(true);
      txtMoveNumber.setFocusable(true);
      txtAnaFirstPlayouts.setFocusable(true);
    }
    if (txtLastAnaMove.isFocusOwner()) {
      txtAutoPlayTime.setFocusable(false);
      txtAutoPlayPlayouts.setFocusable(false);
      txtenginePkPlayputs.setFocusable(false);
      txtenginePkTimeWhite.setFocusable(false);
      txtAutoMain.setFocusable(false);
      txtAutoSub.setFocusable(false);
      txtenginePkFirstPlayputs.setFocusable(false);
      txtAutoPlayFirstPlayouts.setFocusable(false);
      txtenginePkTime.setFocusable(false);
      txtAnaTime.setFocusable(false);
      txtFirstAnaMove.setFocusable(false);
      txtenginePkPlayputsWhite.setFocusable(false);
      txtenginePkFirstPlayputsWhite.setFocusable(false);
      txtenginePkBatch.setFocusable(false);
      txtAnaPlayouts.setFocusable(false);
      txtMoveNumber.setFocusable(false);
      txtAnaFirstPlayouts.setFocusable(false);
      txtLastAnaMove.setFocusable(false);
      txtAutoPlayTime.setFocusable(true);
      txtAutoPlayPlayouts.setFocusable(true);
      txtAutoPlayFirstPlayouts.setFocusable(true);
      txtenginePkTime.setFocusable(true);
      txtAnaTime.setFocusable(true);
      txtenginePkPlayputs.setFocusable(true);
      txtenginePkFirstPlayputs.setFocusable(true);
      txtenginePkTimeWhite.setFocusable(true);
      txtAutoMain.setFocusable(true);
      txtAutoSub.setFocusable(true);
      txtFirstAnaMove.setFocusable(true);
      txtAnaPlayouts.setFocusable(true);
      txtMoveNumber.setFocusable(true);
      txtenginePkPlayputsWhite.setFocusable(true);
      txtenginePkFirstPlayputsWhite.setFocusable(true);
      txtenginePkBatch.setFocusable(true);
      txtAnaFirstPlayouts.setFocusable(true);
      txtLastAnaMove.setFocusable(true);
    }
    if (txtFirstAnaMove.isFocusOwner()) {
      txtAnaTime.setFocusable(false);
      txtAutoPlayTime.setFocusable(false);
      txtAutoPlayPlayouts.setFocusable(false);
      txtenginePkPlayputs.setFocusable(false);
      txtAutoMain.setFocusable(false);
      txtAutoSub.setFocusable(false);
      txtenginePkTimeWhite.setFocusable(false);
      txtenginePkFirstPlayputs.setFocusable(false);
      txtAutoPlayFirstPlayouts.setFocusable(false);
      txtenginePkPlayputsWhite.setFocusable(false);
      txtenginePkFirstPlayputsWhite.setFocusable(false);
      txtenginePkBatch.setFocusable(false);
      txtenginePkTime.setFocusable(false);
      txtLastAnaMove.setFocusable(false);
      txtAnaPlayouts.setFocusable(false);
      txtMoveNumber.setFocusable(false);
      txtAnaFirstPlayouts.setFocusable(false);
      txtFirstAnaMove.setFocusable(false);
      txtAnaTime.setFocusable(true);
      txtLastAnaMove.setFocusable(true);
      txtAutoPlayTime.setFocusable(true);
      txtAutoPlayPlayouts.setFocusable(true);
      txtAutoPlayFirstPlayouts.setFocusable(true);
      txtenginePkPlayputsWhite.setFocusable(true);
      txtenginePkFirstPlayputsWhite.setFocusable(true);
      txtenginePkBatch.setFocusable(true);
      txtAutoMain.setFocusable(true);
      txtAutoSub.setFocusable(true);
      txtAnaPlayouts.setFocusable(true);
      txtenginePkTime.setFocusable(true);
      txtenginePkPlayputs.setFocusable(true);
      txtenginePkTimeWhite.setFocusable(true);
      txtenginePkFirstPlayputs.setFocusable(true);
      txtMoveNumber.setFocusable(true);
      txtAnaFirstPlayouts.setFocusable(true);
      txtFirstAnaMove.setFocusable(true);
    }

    if (txtAutoPlayTime.isFocusOwner()) {
      txtAnaTime.setFocusable(false);
      txtenginePkPlayputsWhite.setFocusable(false);
      txtenginePkFirstPlayputsWhite.setFocusable(false);
      txtenginePkBatch.setFocusable(false);
      txtAutoMain.setFocusable(false);
      txtAutoSub.setFocusable(false);
      txtAutoPlayPlayouts.setFocusable(false);
      txtAutoPlayFirstPlayouts.setFocusable(false);
      txtenginePkTimeWhite.setFocusable(false);
      txtenginePkPlayputs.setFocusable(false);
      txtenginePkFirstPlayputs.setFocusable(false);
      txtLastAnaMove.setFocusable(false);
      txtAnaPlayouts.setFocusable(false);
      txtenginePkTime.setFocusable(false);
      txtMoveNumber.setFocusable(false);
      txtAnaFirstPlayouts.setFocusable(false);
      txtFirstAnaMove.setFocusable(false);
      txtAutoPlayTime.setFocusable(false);
      txtAnaTime.setFocusable(true);
      txtLastAnaMove.setFocusable(true);
      txtenginePkPlayputs.setFocusable(true);
      txtenginePkFirstPlayputs.setFocusable(true);
      txtAutoPlayPlayouts.setFocusable(true);
      txtAutoPlayFirstPlayouts.setFocusable(true);
      txtAnaPlayouts.setFocusable(true);
      txtMoveNumber.setFocusable(true);
      txtenginePkPlayputsWhite.setFocusable(true);
      txtenginePkFirstPlayputsWhite.setFocusable(true);
      txtAutoMain.setFocusable(true);
      txtAutoSub.setFocusable(true);
      txtenginePkBatch.setFocusable(true);
      txtenginePkTime.setFocusable(true);
      txtenginePkTimeWhite.setFocusable(true);
      txtAnaFirstPlayouts.setFocusable(true);
      txtFirstAnaMove.setFocusable(true);
      txtAutoPlayTime.setFocusable(true);
    }

    if (txtAutoPlayPlayouts.isFocusOwner()) {
      txtAnaTime.setFocusable(false);
      txtAutoPlayTime.setFocusable(false);
      txtenginePkTime.setFocusable(false);
      txtAutoPlayFirstPlayouts.setFocusable(false);
      txtLastAnaMove.setFocusable(false);
      txtenginePkPlayputs.setFocusable(false);
      txtAutoMain.setFocusable(false);
      txtAutoSub.setFocusable(false);
      txtenginePkPlayputsWhite.setFocusable(false);
      txtenginePkFirstPlayputsWhite.setFocusable(false);
      txtenginePkTimeWhite.setFocusable(false);
      txtenginePkBatch.setFocusable(false);
      txtenginePkFirstPlayputs.setFocusable(false);
      txtAnaPlayouts.setFocusable(false);
      txtMoveNumber.setFocusable(false);
      txtAnaFirstPlayouts.setFocusable(false);
      txtFirstAnaMove.setFocusable(false);
      txtAutoPlayPlayouts.setFocusable(false);
      txtAnaTime.setFocusable(true);
      txtLastAnaMove.setFocusable(true);
      txtAutoPlayTime.setFocusable(true);
      txtenginePkTime.setFocusable(true);
      txtAutoPlayFirstPlayouts.setFocusable(true);
      txtAnaPlayouts.setFocusable(true);
      txtenginePkPlayputs.setFocusable(true);
      txtenginePkFirstPlayputs.setFocusable(true);
      txtAutoMain.setFocusable(true);
      txtAutoSub.setFocusable(true);
      txtenginePkPlayputsWhite.setFocusable(true);
      txtenginePkFirstPlayputsWhite.setFocusable(true);
      txtenginePkTimeWhite.setFocusable(true);
      txtenginePkBatch.setFocusable(true);
      txtMoveNumber.setFocusable(true);
      txtAnaFirstPlayouts.setFocusable(true);
      txtFirstAnaMove.setFocusable(true);
      txtAutoPlayPlayouts.setFocusable(true);
    }

    if (txtAutoPlayFirstPlayouts.isFocusOwner()) {
      txtAnaTime.setFocusable(false);
      txtAutoPlayTime.setFocusable(false);
      txtAutoPlayPlayouts.setFocusable(false);
      txtenginePkTime.setFocusable(false);
      txtAutoMain.setFocusable(false);
      txtAutoSub.setFocusable(false);
      txtLastAnaMove.setFocusable(false);
      txtenginePkPlayputs.setFocusable(false);
      txtenginePkFirstPlayputs.setFocusable(false);
      txtenginePkTimeWhite.setFocusable(false);
      txtenginePkPlayputsWhite.setFocusable(false);
      txtenginePkFirstPlayputsWhite.setFocusable(false);
      txtenginePkBatch.setFocusable(false);
      txtAnaPlayouts.setFocusable(false);
      txtMoveNumber.setFocusable(false);
      txtAnaFirstPlayouts.setFocusable(false);
      txtFirstAnaMove.setFocusable(false);
      txtAutoPlayFirstPlayouts.setFocusable(false);
      txtAnaTime.setFocusable(true);
      txtLastAnaMove.setFocusable(true);
      txtAutoPlayTime.setFocusable(true);
      txtAutoPlayPlayouts.setFocusable(true);
      txtenginePkTime.setFocusable(true);
      txtAnaPlayouts.setFocusable(true);
      txtMoveNumber.setFocusable(true);
      txtAutoMain.setFocusable(true);
      txtAutoSub.setFocusable(true);
      txtenginePkPlayputs.setFocusable(true);
      txtenginePkFirstPlayputs.setFocusable(true);
      txtAnaFirstPlayouts.setFocusable(true);
      txtenginePkTimeWhite.setFocusable(true);
      txtenginePkPlayputsWhite.setFocusable(true);
      txtenginePkFirstPlayputsWhite.setFocusable(true);
      txtenginePkBatch.setFocusable(true);
      txtFirstAnaMove.setFocusable(true);
      txtAutoPlayFirstPlayouts.setFocusable(true);
    }

    if (txtenginePkTime.isFocusOwner()) {
      txtAnaTime.setFocusable(false);
      txtAutoPlayTime.setFocusable(false);
      txtAutoMain.setFocusable(false);
      txtAutoSub.setFocusable(false);
      txtAutoPlayPlayouts.setFocusable(false);
      txtenginePkPlayputs.setFocusable(false);
      txtenginePkFirstPlayputs.setFocusable(false);
      txtenginePkPlayputsWhite.setFocusable(false);
      txtenginePkFirstPlayputsWhite.setFocusable(false);
      txtenginePkTimeWhite.setFocusable(false);
      txtenginePkBatch.setFocusable(false);
      txtLastAnaMove.setFocusable(false);
      txtAnaPlayouts.setFocusable(false);
      txtMoveNumber.setFocusable(false);
      txtAnaFirstPlayouts.setFocusable(false);
      txtFirstAnaMove.setFocusable(false);
      txtAutoPlayFirstPlayouts.setFocusable(false);
      txtenginePkTime.setFocusable(false);
      txtenginePkTime.setFocusable(true);
      txtAnaTime.setFocusable(true);
      txtLastAnaMove.setFocusable(true);
      txtAutoMain.setFocusable(true);
      txtAutoSub.setFocusable(true);
      txtAutoPlayTime.setFocusable(true);
      txtAutoPlayPlayouts.setFocusable(true);
      txtenginePkPlayputs.setFocusable(true);
      txtenginePkFirstPlayputs.setFocusable(true);
      txtenginePkPlayputsWhite.setFocusable(true);
      txtenginePkFirstPlayputsWhite.setFocusable(true);
      txtenginePkTimeWhite.setFocusable(true);
      txtenginePkBatch.setFocusable(true);
      txtAnaPlayouts.setFocusable(true);
      txtMoveNumber.setFocusable(true);
      txtAnaFirstPlayouts.setFocusable(true);
      txtFirstAnaMove.setFocusable(true);
      txtAutoPlayFirstPlayouts.setFocusable(true);
    }

    if (txtenginePkPlayputs.isFocusOwner()) {
      txtAnaTime.setFocusable(false);
      txtAutoPlayTime.setFocusable(false);
      txtAutoPlayPlayouts.setFocusable(false);
      txtenginePkPlayputsWhite.setFocusable(false);
      txtenginePkTimeWhite.setFocusable(false);
      txtAutoMain.setFocusable(false);
      txtAutoSub.setFocusable(false);
      txtenginePkFirstPlayputsWhite.setFocusable(false);
      txtenginePkBatch.setFocusable(false);
      txtenginePkFirstPlayputs.setFocusable(false);
      txtLastAnaMove.setFocusable(false);
      txtAnaPlayouts.setFocusable(false);
      txtMoveNumber.setFocusable(false);
      txtAnaFirstPlayouts.setFocusable(false);
      txtFirstAnaMove.setFocusable(false);
      txtAutoPlayFirstPlayouts.setFocusable(false);
      txtenginePkTime.setFocusable(false);
      txtenginePkPlayputs.setFocusable(false);
      txtenginePkPlayputs.setFocusable(true);
      txtenginePkTime.setFocusable(true);
      txtAnaTime.setFocusable(true);
      txtLastAnaMove.setFocusable(true);
      txtAutoMain.setFocusable(true);
      txtAutoSub.setFocusable(true);
      txtAutoPlayTime.setFocusable(true);
      txtAutoPlayPlayouts.setFocusable(true);
      txtenginePkPlayputsWhite.setFocusable(true);
      txtenginePkFirstPlayputsWhite.setFocusable(true);
      txtenginePkBatch.setFocusable(true);
      txtenginePkFirstPlayputs.setFocusable(true);
      txtenginePkTimeWhite.setFocusable(true);
      txtAnaPlayouts.setFocusable(true);
      txtMoveNumber.setFocusable(true);
      txtAnaFirstPlayouts.setFocusable(true);
      txtFirstAnaMove.setFocusable(true);
      txtAutoPlayFirstPlayouts.setFocusable(true);
    }

    if (txtenginePkFirstPlayputs.isFocusOwner()) {
      txtAnaTime.setFocusable(false);
      txtAutoPlayTime.setFocusable(false);
      txtAutoPlayPlayouts.setFocusable(false);
      txtenginePkPlayputs.setFocusable(false);
      txtenginePkPlayputsWhite.setFocusable(false);
      txtAutoMain.setFocusable(false);
      txtAutoSub.setFocusable(false);
      txtenginePkTimeWhite.setFocusable(false);
      txtenginePkFirstPlayputsWhite.setFocusable(false);
      txtenginePkBatch.setFocusable(false);
      txtLastAnaMove.setFocusable(false);
      txtAnaPlayouts.setFocusable(false);
      txtMoveNumber.setFocusable(false);
      txtAnaFirstPlayouts.setFocusable(false);
      txtFirstAnaMove.setFocusable(false);
      txtAutoPlayFirstPlayouts.setFocusable(false);
      txtenginePkTime.setFocusable(false);
      txtenginePkFirstPlayputs.setFocusable(false);
      txtenginePkFirstPlayputs.setFocusable(true);
      txtenginePkTime.setFocusable(true);
      txtAnaTime.setFocusable(true);
      txtLastAnaMove.setFocusable(true);
      txtAutoPlayTime.setFocusable(true);
      txtAutoPlayPlayouts.setFocusable(true);
      txtenginePkPlayputs.setFocusable(true);
      txtenginePkPlayputsWhite.setFocusable(true);
      txtenginePkFirstPlayputsWhite.setFocusable(true);
      txtenginePkBatch.setFocusable(true);
      txtenginePkTimeWhite.setFocusable(true);
      txtAnaPlayouts.setFocusable(true);
      txtAutoMain.setFocusable(true);
      txtAutoSub.setFocusable(true);
      txtMoveNumber.setFocusable(true);
      txtAnaFirstPlayouts.setFocusable(true);
      txtFirstAnaMove.setFocusable(true);
      txtAutoPlayFirstPlayouts.setFocusable(true);
    }

    if (txtenginePkPlayputsWhite.isFocusOwner()) {
      txtAnaTime.setFocusable(false);
      txtAutoPlayTime.setFocusable(false);
      txtAutoPlayPlayouts.setFocusable(false);
      txtAutoMain.setFocusable(false);
      txtAutoSub.setFocusable(false);
      txtenginePkPlayputs.setFocusable(false);
      txtenginePkTimeWhite.setFocusable(false);
      txtenginePkFirstPlayputsWhite.setFocusable(false);
      txtenginePkBatch.setFocusable(false);
      txtLastAnaMove.setFocusable(false);
      txtAnaPlayouts.setFocusable(false);
      txtMoveNumber.setFocusable(false);
      txtAnaFirstPlayouts.setFocusable(false);
      txtFirstAnaMove.setFocusable(false);
      txtAutoPlayFirstPlayouts.setFocusable(false);
      txtenginePkTime.setFocusable(false);
      txtenginePkFirstPlayputs.setFocusable(false);
      txtenginePkPlayputsWhite.setFocusable(false);
      txtenginePkPlayputsWhite.setFocusable(true);
      txtenginePkFirstPlayputs.setFocusable(true);
      txtenginePkTime.setFocusable(true);
      txtAnaTime.setFocusable(true);
      txtAutoMain.setFocusable(true);
      txtAutoSub.setFocusable(true);
      txtLastAnaMove.setFocusable(true);
      txtAutoPlayTime.setFocusable(true);
      txtAutoPlayPlayouts.setFocusable(true);
      txtenginePkPlayputs.setFocusable(true);
      txtenginePkTimeWhite.setFocusable(true);
      txtenginePkFirstPlayputsWhite.setFocusable(true);
      txtenginePkBatch.setFocusable(true);
      txtAnaPlayouts.setFocusable(true);
      txtMoveNumber.setFocusable(true);
      txtAnaFirstPlayouts.setFocusable(true);
      txtFirstAnaMove.setFocusable(true);
      txtAutoPlayFirstPlayouts.setFocusable(true);
    }

    if (txtenginePkFirstPlayputsWhite.isFocusOwner()) {
      txtAnaTime.setFocusable(false);
      txtAutoPlayTime.setFocusable(false);
      txtAutoMain.setFocusable(false);
      txtAutoSub.setFocusable(false);
      txtAutoPlayPlayouts.setFocusable(false);
      txtenginePkPlayputs.setFocusable(false);
      txtenginePkPlayputsWhite.setFocusable(false);
      txtenginePkTimeWhite.setFocusable(false);
      txtenginePkBatch.setFocusable(false);
      txtLastAnaMove.setFocusable(false);
      txtAnaPlayouts.setFocusable(false);
      txtMoveNumber.setFocusable(false);
      txtAnaFirstPlayouts.setFocusable(false);
      txtFirstAnaMove.setFocusable(false);
      txtAutoPlayFirstPlayouts.setFocusable(false);
      txtenginePkTime.setFocusable(false);
      txtenginePkFirstPlayputs.setFocusable(false);
      txtenginePkFirstPlayputsWhite.setFocusable(false);
      txtenginePkFirstPlayputsWhite.setFocusable(true);
      txtenginePkFirstPlayputs.setFocusable(true);
      txtenginePkTime.setFocusable(true);
      txtAnaTime.setFocusable(true);
      txtLastAnaMove.setFocusable(true);
      txtAutoPlayTime.setFocusable(true);
      txtAutoPlayPlayouts.setFocusable(true);
      txtAutoMain.setFocusable(true);
      txtAutoSub.setFocusable(true);
      txtenginePkPlayputs.setFocusable(true);
      txtenginePkPlayputsWhite.setFocusable(true);
      txtenginePkTimeWhite.setFocusable(true);
      txtenginePkBatch.setFocusable(true);
      txtAnaPlayouts.setFocusable(true);
      txtMoveNumber.setFocusable(true);
      txtAnaFirstPlayouts.setFocusable(true);
      txtFirstAnaMove.setFocusable(true);
      txtAutoPlayFirstPlayouts.setFocusable(true);
    }

    if (txtenginePkBatch.isFocusOwner()) {
      txtAnaTime.setFocusable(false);
      txtAutoPlayTime.setFocusable(false);
      txtAutoPlayPlayouts.setFocusable(false);
      txtenginePkPlayputs.setFocusable(false);
      txtenginePkPlayputsWhite.setFocusable(false);
      txtenginePkFirstPlayputsWhite.setFocusable(false);
      txtenginePkTimeWhite.setFocusable(false);
      txtLastAnaMove.setFocusable(false);
      txtAnaPlayouts.setFocusable(false);
      txtMoveNumber.setFocusable(false);
      txtAutoMain.setFocusable(false);
      txtAutoSub.setFocusable(false);
      txtAnaFirstPlayouts.setFocusable(false);
      txtFirstAnaMove.setFocusable(false);
      txtAutoPlayFirstPlayouts.setFocusable(false);
      txtenginePkTime.setFocusable(false);
      txtenginePkFirstPlayputs.setFocusable(false);
      txtenginePkBatch.setFocusable(false);
      txtenginePkBatch.setFocusable(true);
      txtenginePkFirstPlayputs.setFocusable(true);
      txtenginePkTime.setFocusable(true);
      txtAnaTime.setFocusable(true);
      txtLastAnaMove.setFocusable(true);
      txtAutoPlayTime.setFocusable(true);
      txtAutoPlayPlayouts.setFocusable(true);
      txtenginePkPlayputs.setFocusable(true);
      txtAutoMain.setFocusable(true);
      txtAutoSub.setFocusable(true);
      txtenginePkPlayputsWhite.setFocusable(true);
      txtenginePkFirstPlayputsWhite.setFocusable(true);
      txtenginePkTimeWhite.setFocusable(true);
      txtAnaPlayouts.setFocusable(true);
      txtMoveNumber.setFocusable(true);
      txtAnaFirstPlayouts.setFocusable(true);
      txtFirstAnaMove.setFocusable(true);
      txtAutoPlayFirstPlayouts.setFocusable(true);
    }
    if (txtenginePkTimeWhite.isFocusOwner()) {
      txtAnaTime.setFocusable(false);
      txtAutoPlayTime.setFocusable(false);
      txtAutoPlayPlayouts.setFocusable(false);
      txtenginePkPlayputs.setFocusable(false);
      txtenginePkPlayputsWhite.setFocusable(false);
      txtenginePkFirstPlayputsWhite.setFocusable(false);
      txtAutoMain.setFocusable(false);
      txtAutoSub.setFocusable(false);
      txtLastAnaMove.setFocusable(false);
      txtAnaPlayouts.setFocusable(false);
      txtMoveNumber.setFocusable(false);
      txtAnaFirstPlayouts.setFocusable(false);
      txtFirstAnaMove.setFocusable(false);
      txtAutoPlayFirstPlayouts.setFocusable(false);
      txtenginePkTime.setFocusable(false);
      txtenginePkFirstPlayputs.setFocusable(false);
      txtenginePkBatch.setFocusable(false);
      txtenginePkTimeWhite.setFocusable(false);
      txtenginePkTimeWhite.setFocusable(true);
      txtenginePkBatch.setFocusable(true);
      txtenginePkFirstPlayputs.setFocusable(true);
      txtenginePkTime.setFocusable(true);
      txtAnaTime.setFocusable(true);
      txtLastAnaMove.setFocusable(true);
      txtAutoPlayTime.setFocusable(true);
      txtAutoPlayPlayouts.setFocusable(true);
      txtenginePkPlayputs.setFocusable(true);
      txtenginePkPlayputsWhite.setFocusable(true);
      txtenginePkFirstPlayputsWhite.setFocusable(true);
      txtAutoMain.setFocusable(true);
      txtAutoSub.setFocusable(true);
      txtAnaPlayouts.setFocusable(true);
      txtMoveNumber.setFocusable(true);
      txtAnaFirstPlayouts.setFocusable(true);
      txtFirstAnaMove.setFocusable(true);
      txtAutoPlayFirstPlayouts.setFocusable(true);
    }
    if (txtAutoMain.isFocusOwner()) {
      txtAnaTime.setFocusable(false);
      txtAutoPlayTime.setFocusable(false);
      txtAutoPlayPlayouts.setFocusable(false);
      txtenginePkPlayputs.setFocusable(false);
      txtenginePkPlayputsWhite.setFocusable(false);
      txtenginePkFirstPlayputsWhite.setFocusable(false);

      txtAutoSub.setFocusable(false);
      txtLastAnaMove.setFocusable(false);
      txtAnaPlayouts.setFocusable(false);
      txtMoveNumber.setFocusable(false);
      txtAnaFirstPlayouts.setFocusable(false);
      txtFirstAnaMove.setFocusable(false);
      txtAutoPlayFirstPlayouts.setFocusable(false);
      txtenginePkTime.setFocusable(false);
      txtenginePkFirstPlayputs.setFocusable(false);
      txtenginePkBatch.setFocusable(false);
      txtenginePkTimeWhite.setFocusable(false);
      txtAutoMain.setFocusable(false);
      txtAutoMain.setFocusable(true);
      txtenginePkTimeWhite.setFocusable(true);
      txtenginePkBatch.setFocusable(true);
      txtenginePkFirstPlayputs.setFocusable(true);
      txtenginePkTime.setFocusable(true);
      txtAnaTime.setFocusable(true);
      txtLastAnaMove.setFocusable(true);
      txtAutoPlayTime.setFocusable(true);
      txtAutoPlayPlayouts.setFocusable(true);
      txtenginePkPlayputs.setFocusable(true);
      txtenginePkPlayputsWhite.setFocusable(true);
      txtenginePkFirstPlayputsWhite.setFocusable(true);

      txtAutoSub.setFocusable(true);
      txtAnaPlayouts.setFocusable(true);
      txtMoveNumber.setFocusable(true);
      txtAnaFirstPlayouts.setFocusable(true);
      txtFirstAnaMove.setFocusable(true);
      txtAutoPlayFirstPlayouts.setFocusable(true);
    }
    if (txtAutoSub.isFocusOwner()) {
      txtAnaTime.setFocusable(false);
      txtAutoPlayTime.setFocusable(false);
      txtAutoPlayPlayouts.setFocusable(false);
      txtenginePkPlayputs.setFocusable(false);
      txtenginePkPlayputsWhite.setFocusable(false);
      txtenginePkFirstPlayputsWhite.setFocusable(false);
      txtAutoMain.setFocusable(false);

      txtLastAnaMove.setFocusable(false);
      txtAnaPlayouts.setFocusable(false);
      txtMoveNumber.setFocusable(false);
      txtAnaFirstPlayouts.setFocusable(false);
      txtFirstAnaMove.setFocusable(false);
      txtAutoPlayFirstPlayouts.setFocusable(false);
      txtenginePkTime.setFocusable(false);
      txtenginePkFirstPlayputs.setFocusable(false);
      txtenginePkBatch.setFocusable(false);
      txtenginePkTimeWhite.setFocusable(false);
      txtAutoSub.setFocusable(false);
      txtAutoSub.setFocusable(true);
      txtenginePkTimeWhite.setFocusable(true);
      txtenginePkBatch.setFocusable(true);
      txtenginePkFirstPlayputs.setFocusable(true);
      txtenginePkTime.setFocusable(true);
      txtAnaTime.setFocusable(true);
      txtLastAnaMove.setFocusable(true);
      txtAutoPlayTime.setFocusable(true);
      txtAutoPlayPlayouts.setFocusable(true);
      txtenginePkPlayputs.setFocusable(true);
      txtenginePkPlayputsWhite.setFocusable(true);
      txtenginePkFirstPlayputsWhite.setFocusable(true);
      txtAutoMain.setFocusable(true);

      txtAnaPlayouts.setFocusable(true);
      txtMoveNumber.setFocusable(true);
      txtAnaFirstPlayouts.setFocusable(true);
      txtFirstAnaMove.setFocusable(true);
      txtAutoPlayFirstPlayouts.setFocusable(true);
    }
  }

  class UI extends javax.swing.plaf.basic.BasicComboBoxUI {
    protected javax.swing.plaf.basic.ComboPopup createPopup() {
      Popup popup = new Popup(comboBox);
      popup.getAccessibleContext().setAccessibleParent(comboBox);
      return popup;
    }

    public javax.swing.plaf.basic.ComboPopup getPopup() {
      return popup;
    }
  }

  class Popup extends javax.swing.plaf.basic.BasicComboPopup {
    public Popup(JComboBox combo) {
      super(combo);
    }

    public void setDisplaySize(int width, int height) {
      scroller.setSize(width, height);
      scroller.setPreferredSize(new Dimension(width, height));
    }

    public void show() {
      setListSelection(comboBox.getSelectedIndex());
      //  java.awt.Point location = getPopupLocation();
      show(comboBox, 0, 0);
    }

    private void setListSelection(int selectedIndex) {
      if (selectedIndex == -1) {
        list.clearSelection();
      } else {
        list.setSelectedIndex(selectedIndex);
        list.ensureIndexIsVisible(selectedIndex);
      }
    }

    //    private java.awt.Point getPopupLocation() {
    //      Dimension popupSize = comboBox.getSize();
    //      Insets insets = getInsets();
    //
    //      // reduce the width of the scrollpane by the insets so that the popup
    //      // is the same width as the combo box.
    //      popupSize.setSize(
    //          popupSize.width - (insets.right + insets.left),
    //          getPopupHeightForRowCount(comboBox.getMaximumRowCount()));
    //      Rectangle popupBounds =
    //          computePopupBounds(0, comboBox.getBounds().height, popupSize.width,
    // popupSize.height);
    //      Dimension scrollSize = popupBounds.getSize();
    //      java.awt.Point popupLocation = popupBounds.getLocation();
    //
    //      //          scroller.setMaximumSize( scrollSize );
    //      //          scroller.setPreferredSize( scrollSize );
    //      //          scroller.setMinimumSize( scrollSize );
    //
    //      list.revalidate();
    //
    //      return popupLocation;
    //    }
  }

  public void resetAutoAna() {
    chkAnaBlack.setText("黑");
    chkAnaWhite.setText("白");
    txtFirstAnaMove.setBounds(138, 2, 30, 18);
    txtLastAnaMove.setBounds(185, 2, 30, 18);
    txtAnaFirstPlayouts.setBounds(298, 2, 45, 18);
    chkAnaWhite.setBounds(68, 22, 40, 20);
    chkAnaBlack.setBounds(68, 1, 40, 20);
    txtAnaTime.setBounds(310, 23, 33, 18);
    chkAnaAutoSave.setBounds(1, 22, 20, 20);
    txtAnaPlayouts.setBounds(175, 23, 50, 18);
    anaPanel.add(txtFirstAnaMove);
    anaPanel.add(txtLastAnaMove);
    anaPanel.add(txtAnaFirstPlayouts);
    anaPanel.add(chkAnaWhite);
    anaPanel.add(chkAnaBlack);
    anaPanel.add(txtAnaTime);
    anaPanel.add(chkAnaAutoSave);
    anaPanel.add(txtAnaPlayouts);
  }

  public void startAutoAna() {
    if (threadAnalyzeDiffNode != null) threadAnalyzeDiffNode.interrupt();
    Lizzie.frame.isAutoAnalyzingDiffNode = false;
    chkAutoAnalyse.setSelected(true);
    Lizzie.config.autoAnaStartMove = Utils.parseTextToInt(txtFirstAnaMove, -1);
    Lizzie.config.autoAnaEndMove = Utils.parseTextToInt(txtLastAnaMove, -1);
    Lizzie.config.isAutoAna = true;
    Lizzie.leelaz.autoAnalysed = false;
    // Lizzie.config.isStartingAutoAna=true;
    //    Lizzie.leelaz.isStartingAutoAna = true;
    //    Lizzie.leelaz.isClosingAutoAna = false;
    if (Lizzie.frame.isBatchAna && Lizzie.config.autoAnaStartMove <= 0) {
      Lizzie.frame.firstMove();
    }
    if (chkAnaTime.isSelected()) {
      Lizzie.config.autoAnaTime = Utils.parseTextToInt(txtAnaTime, 0) * 1000;
    } else {
      Lizzie.config.autoAnaTime = -1;
    }
    if (chkAnaPlayouts.isSelected()) {
      Lizzie.config.autoAnaPlayouts = Utils.parseTextToInt(txtAnaPlayouts, 0);
    } else {
      Lizzie.config.autoAnaPlayouts = -1;
    }
    if (chkAnaFirstPlayouts.isSelected()) {
      Lizzie.config.autoAnaFirstPlayouts = Utils.parseTextToInt(txtAnaFirstPlayouts, 0);
    } else {
      Lizzie.config.autoAnaFirstPlayouts = -1;
    }
    Lizzie.config.anaBlack = chkAnaBlack.isSelected();
    Lizzie.config.anaWhite = chkAnaWhite.isSelected();
    if (Lizzie.config.autoAnaStartMove >= 0) {
      if (Lizzie.config.autoAnaStartMove > 0)
        Lizzie.config.autoAnaStartMove = Lizzie.config.autoAnaStartMove - 1;
      Lizzie.board.goToMoveNumber(Lizzie.config.autoAnaStartMove);
    } else if (Lizzie.config.analyzeAllBranch
        && !Lizzie.board.getHistory().getCurrentHistoryNode().isMainTrunk()) {
      Lizzie.board.goToMoveNumber(0);
    }
    if (Lizzie.config.autoAnaDiffEnable)
      autoAnaStartNode = Lizzie.board.getHistory().getCurrentHistoryNode();
    Lizzie.board.clearBoardStat();
    Lizzie.leelaz.clearBestMoves();
    Lizzie.board.clearAnalyzeStatusAfter(Lizzie.board.getHistory().getStart());
    if (Lizzie.config.analyzeAllBranch) {
      Runnable runnable =
          new Runnable() {
            public void run() {
              Lizzie.board.analyzeAllNodesAfter(Lizzie.board.getHistory().getCurrentHistoryNode());
            }
          };
      threadAnalyzeAllNode = new Thread(runnable);
      threadAnalyzeAllNode.start();
    }
    Lizzie.leelaz.ponder();
    if (Lizzie.frame.isBatchAna) chkAnaAutoSave.setSelected(true);
    if (Lizzie.frame.isBatchAna
        && Lizzie.frame.Batchfiles != null
        && Lizzie.frame.Batchfiles.size() > 1
        && Lizzie.frame.BatchAnaNum == 0) Lizzie.frame.openAnalysisTable();
    start.setText("终止");
    //   Lizzie.board.canGetBestMoves = true;
  }

  private void checkMove() {
    try {
      changeMoveNumber = Integer.parseInt(LizzieFrame.toolbar.txtMoveNumber.getText());
    } catch (NumberFormatException err) {
      changeMoveNumber = 0;
    }
  }

  public void addEngineLis() {
    enginePkBlackLis =
        new ItemListener() {
          public void itemStateChanged(final ItemEvent e) {
            engineBlackToolbar = enginePkBlack.getSelectedIndex();
            setTxtUnfocuse();
          }
        };
    enginePkWhiteLis =
        new ItemListener() {
          public void itemStateChanged(final ItemEvent e) {
            engineWhiteToolbar = enginePkWhite.getSelectedIndex();
            setTxtUnfocuse();
          }
        };
    enginePkBlack.addItemListener(enginePkBlackLis);
    enginePkWhite.addItemListener(enginePkWhiteLis);
  }

  public void removeEngineLis() {
    enginePkBlack.removeItemListener(enginePkBlackLis);
    enginePkWhite.removeItemListener(enginePkWhiteLis);
  }

  //  public void stopEnginePK(boolean mannul) {
  //    if (!isEnginePk) return;
  //    Lizzie.frame.winrateGraph.maxcoreMean = 30;
  //    isEnginePk = false;
  //    btnStartPk.setText("开始");
  //    Lizzie.frame.subBoardRenderer.reverseBestmoves = false;
  //    Lizzie.frame.boardRenderer.reverseBestmoves = false;
  //    Lizzie.engineManager.engineList.get(engineBlack).played = false;
  //    Lizzie.engineManager.engineList.get(engineWhite).played = false;
  //    Lizzie.frame.addInput();
  //    analyse.setEnabled(true);
  //    enginePkBlack.setEnabled(true);
  //    enginePkWhite.setEnabled(true);
  //    // txtenginePkBatch.setEnabled(true);
  //    // chkenginePkAutosave.setEnabled(true);
  //    // AutosavePk=true;
  //    btnEnginePkConfig.setEnabled(true);
  //    chkenginePkBatch.setEnabled(true);
  //    chkenginePkTime.setEnabled(true);
  //    txtenginePkTime.setEnabled(true);
  //    txtenginePkTimeWhite.setEnabled(true);
  //    batchPkName = "";
  //    // chkenginePkgenmove.setEnabled(true);
  //    chkenginePk.setEnabled(true);
  //    Lizzie.board.clearbestmovesafter(Lizzie.board.getHistory().getStart());
  //    Lizzie.engineManager.changeEngIcoForEndPk();
  //    Lizzie.leelaz.notPondering();
  //    Lizzie.leelaz.nameCmd();
  //    Lizzie.frame.addInput();
  //    if (mannul && isEnginePkBatch) {
  //      msg = new Message();
  //      String passandMove = "";
  //      if (Lizzie.frame.toolbar.doublePassGame > 0)
  //        passandMove = passandMove + "双方Pass局数 " + doublePassGame;
  //      if (Lizzie.frame.toolbar.maxMoveGame > 0)
  //        passandMove = passandMove + "超最大手数局数  " + maxMoveGame;
  //      msg.setMessage(
  //          "批量对战已结束,比分为["
  //              + Lizzie.frame.toolbar.engineBlackName
  //              + "   "
  //              + pkBlackWins
  //              + ":"
  //              + pkWhiteWins
  //              + "   "
  //              + Lizzie.frame.toolbar.engineWhiteName
  //              + "]"
  //              + passandMove
  //              + ",棋谱保存在"
  //              + "Lizzie目录PkAutoSave文件夹下");
  //      msg.setVisible(true);
  //    }
  //  }

  public ArrayList<Movelist> getStartListForEnginePk() {
    if (chkenginePkContinue.isSelected()) {
      return startGame;
    }
    if (Lizzie.config.chkEngineSgfStart) {
      int length = Lizzie.frame.enginePKSgfString.size();
      if (Lizzie.config.engineSgfStartRandom) {
        Random random = new Random();
        currentEnginePkSgfNum = random.nextInt(length);
      } else {
        currentEnginePkSgfNum = Lizzie.frame.enginePKSgfNum % length;
        Lizzie.frame.enginePKSgfNum++;
      }
      return Lizzie.frame.enginePKSgfString.get(currentEnginePkSgfNum);
    }
    return null;
  }

  public void enableDisabelForEngineGame(boolean enable) {
    if (!enable) btnStartPk.setText("终止");
    else btnStartPk.setText("开始");
    btnEnginePkStop.setText("暂停");
    txtenginePkTime.setEnabled(enable);
    txtenginePkTimeWhite.setEnabled(enable);
    txtenginePkPlayputsWhite.setEnabled(enable);
    txtenginePkPlayputs.setEnabled(enable);
    txtenginePkFirstPlayputs.setEnabled(enable);
    txtenginePkFirstPlayputsWhite.setEnabled(enable);
    chkenginePkContinue.setEnabled(enable);
    chkenginePkTime.setEnabled(enable);
    chkenginePkPlayouts.setEnabled(enable);
    chkenginePkFirstPlayputs.setEnabled(enable);
    chkenginePkBatch.setEnabled(enable);
    enginePkBlack.setEnabled(enable);
    enginePkWhite.setEnabled(enable);
    chkenginePk.setEnabled(enable);
    btnEnginePkConfig.setEnabled(enable);
    Menu.engineMenu.setEnabled(enable);
    analyse.setEnabled(enable);

    chkenginePkTime.setEnabled(enable);
    txtenginePkTime.setEnabled(enable);
    txtenginePkTimeWhite.setEnabled(enable);

    // setGenmove();
  }

  public boolean startEngineGame() {
    int engineBlack = engineBlackToolbar;
    int engineWhite = engineWhiteToolbar;
    int timeBlack = -1,
        timeWhite = -1,
        playoutsBlack = -1,
        playoutsWhite = -1,
        firstPlayoutsBlack = -1,
        firstPlayoutsWhite = -1;
    if (chkenginePkTime.isSelected()) {
      timeBlack = Utils.parseTextToInt(txtenginePkTime, -1);
      timeWhite = Utils.parseTextToInt(txtenginePkTimeWhite, -1);
    }
    if (chkenginePkPlayouts.isSelected()) {
      playoutsBlack = Utils.parseTextToInt(txtenginePkPlayputs, -1);
      playoutsWhite = Utils.parseTextToInt(txtenginePkPlayputsWhite, -1);
    }
    if (chkenginePkFirstPlayputs.isSelected()) {
      firstPlayoutsBlack = Utils.parseTextToInt(txtenginePkFirstPlayputs, -1);
      firstPlayoutsWhite = Utils.parseTextToInt(txtenginePkFirstPlayputsWhite, -1);
    }
    boolean isBatchGame = chkenginePkBatch.isSelected();
    int batchGameNumber = Utils.parseTextToInt(txtenginePkBatch, 1);
    String batchGameName = batchPkNameToolbar;
    boolean isContinueGame = chkenginePkContinue.isSelected() || isEngineGameHandicapToolbar;
    boolean isGenmove = isGenmoveToolbar;
    boolean isExchange = exChangeToolbar;
    return Lizzie.engineManager.startEngineGame(
        engineBlack,
        engineWhite,
        timeBlack,
        timeWhite,
        playoutsBlack,
        playoutsWhite,
        firstPlayoutsBlack,
        firstPlayoutsWhite,
        isBatchGame,
        batchGameNumber,
        batchGameName,
        isContinueGame,
        isGenmove,
        isExchange,
        checkGameMaxMove,
        maxGameMoves);
  }

  //  public void startEnginePk() {
  //    if (!Lizzie.engineManager.isEmpty && Lizzie.leelaz != null) {
  //      Lizzie.leelaz.clearBestMoves();
  //    }
  //    Lizzie.frame.hasEnginePkTitile = false;
  //    Lizzie.frame.enginePkTitile = "";
  //    pkBlackPlayouts = 0;
  //    pkWhitePlayouts = 0;
  //    pkBlackWinAsBlack = 0;
  //    pkBlackWinAsWhite = 0;
  //    pkWhiteWinAsBlack = 0;
  //    pkWhiteWinAsWhite = 0;
  //    if (engineWhite == engineBlack) {
  ////      // if (isGenmove) {
  ////      boolean onTop = false;
  ////      if (Lizzie.frame.isAlwaysOnTop()) {
  ////        Lizzie.frame.setAlwaysOnTop(false);
  ////        onTop = true;
  ////      }
  ////      JOptionPane.showMessageDialog(null, "黑白必须为不同引擎");
  ////      if (onTop) Lizzie.frame.setAlwaysOnTop(true);
  //    	Utils.showMsg("黑白不能选择相同引擎");
  //      Lizzie.frame.addInput();
  //      return;
  //      //    }
  //      //   isSameEngine = true;
  //    }
  //    //    else {
  //    //      isSameEngine = false;
  //    //    }
  //    if (Lizzie.engineManager.isEmpty) Lizzie.engineManager.isEmpty = false;
  //    pkBlackTimeAll = 0;
  //    pkWhiteTimeAll = 0;
  //    timeb = -1;
  //    timew = -1;
  //    engineBlackName =
  // Lizzie.engineManager.engineList.get(engineBlack).getEngineName(engineBlack);
  //    engineWhiteName =
  // Lizzie.engineManager.engineList.get(engineWhite).getEngineName(engineWhite);
  //    Lizzie.frame.winrateGraph.maxcoreMean = 15;
  //    if (isGenmove && chkenginePkTime.isSelected()) {
  //      try {
  //        timeb = Integer.parseInt(txtenginePkTime.getText().replace(" ", ""));
  //      } catch (NumberFormatException err) {
  //      }
  //      try {
  //        timew = Integer.parseInt(txtenginePkTimeWhite.getText().replace(" ", ""));
  //      } catch (NumberFormatException err) {
  //      }
  //      // if (timeb <= 0 || timew <= 0) {
  //      // boolean onTop = false;
  //      // if (Lizzie.frame.isAlwaysOnTop()) {
  //      // Lizzie.frame.setAlwaysOnTop(false);
  //      // onTop = true;
  //      // }
  //      // JOptionPane.showMessageDialog(Lizzie.frame,
  //      // "genmove模式下必须设置黑白双方用时");
  //      // if (onTop) Lizzie.frame.setAlwaysOnTop(true);
  //      // return;
  //      // }
  //    }
  //    Lizzie.config.isAutoAna=false;
  //    isAutoPlay = false;
  //    Lizzie.board.isPkBoard = true;
  //    //    if (checkGameTime) {
  //    //      Lizzie.engineManager.gameTime = System.currentTimeMillis();
  //    //    }
  //
  //    Lizzie.frame.isPlayingAgainstLeelaz = false;
  //    Lizzie.frame.isAnaPlayingAgainstLeelaz = false;
  //    btnStartPk.setText("终止");
  //    btnEnginePkStop.setText("暂停");
  //    isPkStop = false;
  //    EnginePkBatchNumberNow = 1;
  //    isEnginePkBatch = chkenginePkBatch.isSelected();
  //    if (batchPkName.equals("")) {
  //      // batchPkName = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
  //      String SF = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
  //      SF =
  //          Lizzie.engineManager.getEngineName(Lizzie.frame.toolbar.engineBlack)
  //              + "_VS_"
  //              + Lizzie.engineManager.getEngineName(Lizzie.frame.toolbar.engineWhite)
  //              + "_"
  //              + SF;
  //      SF = SF.replaceAll("[/\\\\:*?|]", ".");
  //      SF = SF.replaceAll("[\"<>]", "'");
  //      batchPkName = SF;
  //    }
  //    SF = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
  //
  //    // txtenginePkBatch.setEnabled(false);
  //
  //    chkenginePkBatch.setEnabled(false);
  //    enginePkBlack.setEnabled(false);
  //    enginePkWhite.setEnabled(false);
  //    // chkenginePkgenmove.setEnabled(false);
  //    chkenginePk.setEnabled(false);
  //    btnEnginePkConfig.setEnabled(false);
  //    // if (isEnginePkBatch) {
  //    // chkenginePkAutosave.setSelected(true);
  //    // chkenginePkAutosave.setEnabled(false);
  //    // AutosavePk=true;
  //    // }
  //    if (chkenginePkContinue.isSelected()) {
  //      startGame = Lizzie.board.getmovelist();
  //    }
  //    lblenginePkResult.setText("0:0");
  //    pkBlackWins = 0;
  //    pkWhiteWins = 0;
  //
  //    featurecat.lizzie.gui.Menu.engineMenu.setText("对战中");
  //    featurecat.lizzie.gui.Menu.engineMenu.setEnabled(false);
  //    analyse.setEnabled(false);
  //    Lizzie.frame.setResult("");
  //    Lizzie.engineManager.killOtherEngines(engineBlack, engineWhite);
  //    if (Lizzie.engineManager.currentEngineNo == engineWhite
  //        || Lizzie.engineManager.currentEngineNo == engineBlack) {
  //      Lizzie.leelaz.nameCmd();
  //
  //    } else {
  //      if (!Lizzie.engineManager.isEmpty) {
  //        try {
  //          Lizzie.leelaz.normalQuit();
  //        } catch (Exception ex) {
  //        }
  //      } else {
  //        Lizzie.engineManager.switchEngine(engineBlack);
  //      }
  //    }
  //    doublePassGame = 0;
  //    maxMoveGame = 0;
  //    if (!isGenmove) {
  //      // 分析模式对战
  //      if (checkGameMinMove) {
  //        minMove = minGanmeMove;
  //      } else minMove = -1;
  //      Lizzie.engineManager.engineList.get(engineBlack).blackResignMoveCounts = 0;
  //      Lizzie.engineManager.engineList.get(engineBlack).whiteResignMoveCounts = 0;
  //      Lizzie.engineManager.engineList.get(engineWhite).blackResignMoveCounts = 0;
  //      Lizzie.engineManager.engineList.get(engineBlack).whiteResignMoveCounts = 0;
  //
  //      Lizzie.board.clearforpk();
  //
  //      ArrayList<Movelist> startList = getStartListForEnginePk();
  //      if (startList != null) {
  //        Lizzie.engineManager.isEmpty = true;
  //        Lizzie.board.setlist(startList);
  //        Lizzie.engineManager.isEmpty = false;
  //      }
  //      //      if (chkenginePkContinue.isSelected()) {
  //      //        Lizzie.engineManager.isEmpty = true;
  //      //        Lizzie.board.setlist(startGame);
  //      //        Lizzie.engineManager.isEmpty = false;
  //      //      }
  //      isEnginePk = true;
  //      if (Lizzie.board.getHistory().isBlacksTurn()) {
  //        Lizzie.engineManager.startEngineForPk(engineBlack);
  //        Lizzie.engineManager.startEngineForPk(engineWhite);
  //        Runnable runnable =
  //            new Runnable() {
  //              public void run() {
  //                while (!Lizzie.engineManager.engineList.get(engineWhite).isLoaded()
  //                    || !Lizzie.engineManager.engineList.get(engineBlack).isLoaded()) {
  //                  try {
  //                    Thread.sleep(500);
  //                  } catch (InterruptedException e) {
  //                    // TODO Auto-generated catch block
  //                    e.printStackTrace();
  //                  }
  //                }
  //                if (Lizzie.config.autoLoadLzsaiEngineVisits) {
  //                  Lizzie.engineManager
  //                      .engineList
  //                      .get(engineBlack)
  //                      .sendCommand("lz-setoption name Visits value 1000000000");
  //                  Lizzie.engineManager
  //                      .engineList
  //                      .get(engineWhite)
  //                      .sendCommand("lz-setoption name Visits value 1000000000");
  //                }
  //                Lizzie.leelaz = Lizzie.engineManager.engineList.get(engineBlack);
  //                Lizzie.leelaz.ponder();
  //              }
  //            };
  //        Thread thread = new Thread(runnable);
  //        thread.start();
  //      } else {
  //        chkenginePkTime.setEnabled(false);
  //        txtenginePkTime.setEnabled(false);
  //        txtenginePkTimeWhite.setEnabled(false);
  //        Lizzie.engineManager.startEngineForPk(engineWhite);
  //        Lizzie.engineManager.startEngineForPk(engineBlack);
  //        Runnable runnable =
  //            new Runnable() {
  //              public void run() {
  //                while (!Lizzie.engineManager.engineList.get(engineWhite).isLoaded()
  //                    || !Lizzie.engineManager.engineList.get(engineBlack).isLoaded()) {
  //                  try {
  //                    Thread.sleep(500);
  //                  } catch (InterruptedException e) {
  //                    // TODO Auto-generated catch block
  //                    e.printStackTrace();
  //                  }
  //                }
  //                if (Lizzie.config.autoLoadLzsaiEngineVisits) {
  //                  Lizzie.engineManager
  //                      .engineList
  //                      .get(engineBlack)
  //                      .sendCommand("lz-setoption name Visits value 1000000000");
  //                  Lizzie.engineManager
  //                      .engineList
  //                      .get(engineWhite)
  //                      .sendCommand("lz-setoption name Visits value 1000000000");
  //                }
  //                Lizzie.leelaz = Lizzie.engineManager.engineList.get(engineWhite);
  //                Lizzie.leelaz.ponder();
  //              }
  //            };
  //        Thread thread = new Thread(runnable);
  //        thread.start();
  //      }
  //      Lizzie.board.clearbestmovesafter2(Lizzie.board.getHistory().getStart());
  //
  //      Lizzie.frame.setPlayers(
  //          Lizzie.engineManager.engineList.get(engineWhite).oriEnginename,
  //          Lizzie.engineManager.engineList.get(engineBlack).oriEnginename);
  //      GameInfo gameInfo = Lizzie.board.getHistory().getGameInfo();
  //      gameInfo.setPlayerWhite(Lizzie.engineManager.engineList.get(engineWhite).oriEnginename);
  //      gameInfo.setPlayerBlack(Lizzie.engineManager.engineList.get(engineBlack).oriEnginename);
  //      Runnable runnable =
  //          new Runnable() {
  //            public void run() {
  //              while (isEnginePk) {
  //                try {
  //                  Thread.sleep(Lizzie.config.analyzeUpdateIntervalCentisec * 10);
  //                } catch (InterruptedException e) {
  //                  // TODO Auto-generated catch block
  //                  e.printStackTrace();
  //                }
  //                Lizzie.engineManager.engineList.get(engineBlack).pkResign();
  //                Lizzie.engineManager.engineList.get(engineWhite).pkResign();
  //              }
  //            }
  //          };
  //      Thread thread = new Thread(runnable);
  //      thread.start();
  //    } else {
  //      // genmove对战
  //      chkenginePkTime.setEnabled(false);
  //      txtenginePkTime.setEnabled(false);
  //      txtenginePkTimeWhite.setEnabled(false);
  //      Lizzie.board.clearforpk();
  //      ArrayList<Movelist> startList = getStartListForEnginePk();
  //      if (startList != null) {
  //        Lizzie.engineManager.isEmpty = true;
  //        Lizzie.board.setlist(startList);
  //        Lizzie.engineManager.isEmpty = false;
  //      }
  //      //      if (chkenginePkContinue.isSelected()) {
  //      //        Lizzie.engineManager.isEmpty = true;
  //      //        Lizzie.board.setlist(startGame);
  //      //        Lizzie.engineManager.isEmpty = false;
  //      //      }
  //      isEnginePk = true;
  //      enginePKGenmoveBestMovesSize =
  //          (Lizzie.config.limitMaxSuggestion > 0 && !Lizzie.config.showNoSuggCircle
  //              ? Lizzie.config.limitMaxSuggestion
  //              : 361);
  //      if (Lizzie.board.getHistory().isBlacksTurn()) {
  //        Lizzie.engineManager.startEngineForPk(engineWhite);
  //        Lizzie.engineManager.startEngineForPk(engineBlack);
  //        Runnable runnable =
  //            new Runnable() {
  //              public void run() {
  //                while (!Lizzie.engineManager.engineList.get(engineWhite).isLoaded()
  //                    || !Lizzie.engineManager.engineList.get(engineBlack).isLoaded()) {
  //                  try {
  //                    Thread.sleep(500);
  //                  } catch (InterruptedException e) {
  //                    // TODO Auto-generated catch block
  //                    e.printStackTrace();
  //                  }
  //                }
  //                Lizzie.engineManager.engineList.get(engineBlack).nameCmd();
  //                Lizzie.engineManager.engineList.get(engineBlack).notPondering();
  //                Lizzie.engineManager.engineList.get(engineWhite).nameCmd();
  //                Lizzie.engineManager.engineList.get(engineWhite).notPondering();
  //                if (Lizzie.config.pkAdvanceTimeSettings) {
  //                  Lizzie.engineManager
  //                      .engineList
  //                      .get(engineBlack)
  //                      .sendCommand(Lizzie.config.advanceBlackTimeTxt);
  //                  Lizzie.engineManager
  //                      .engineList
  //                      .get(engineWhite)
  //                      .sendCommand(Lizzie.config.advanceWhiteTimeTxt);
  //                } else {
  //                  if (timew > 0)
  //                    Lizzie.engineManager
  //                        .engineList
  //                        .get(engineWhite)
  //                        .sendCommand("time_settings 0 " + timew + " 1");
  //                  if (timeb > 0)
  //                    Lizzie.engineManager
  //                        .engineList
  //                        .get(engineBlack)
  //                        .sendCommand("time_settings 0 " + timeb + " 1");
  //                }
  //                Lizzie.engineManager.engineList.get(engineBlack).genmoveForPk("B");
  //              }
  //            };
  //        Thread thread = new Thread(runnable);
  //        thread.start();
  //
  //      } else {
  //        Lizzie.engineManager.startEngineForPk(engineBlack);
  //        Lizzie.engineManager.startEngineForPk(engineWhite);
  //        Runnable runnable =
  //            new Runnable() {
  //              public void run() {
  //                while (!Lizzie.engineManager.engineList.get(engineWhite).isLoaded()
  //                    || !Lizzie.engineManager.engineList.get(engineBlack).isLoaded()) {
  //                  try {
  //                    Thread.sleep(500);
  //                  } catch (InterruptedException e) {
  //                    // TODO Auto-generated catch block
  //                    e.printStackTrace();
  //                  }
  //                }
  //                Lizzie.engineManager.engineList.get(engineBlack).nameCmd();
  //                Lizzie.engineManager.engineList.get(engineBlack).notPondering();
  //                Lizzie.engineManager.engineList.get(engineWhite).nameCmd();
  //                Lizzie.engineManager.engineList.get(engineWhite).notPondering();
  //                if (Lizzie.config.pkAdvanceTimeSettings) {
  //                  Lizzie.engineManager
  //                      .engineList
  //                      .get(engineBlack)
  //                      .sendCommand(Lizzie.config.advanceBlackTimeTxt);
  //                  Lizzie.engineManager
  //                      .engineList
  //                      .get(engineWhite)
  //                      .sendCommand(Lizzie.config.advanceWhiteTimeTxt);
  //                } else {
  //                  if (timew > 0)
  //                    Lizzie.engineManager
  //                        .engineList
  //                        .get(engineWhite)
  //                        .sendCommand("time_settings 0 " + timew + " 1");
  //                  if (timeb > 0)
  //                    Lizzie.engineManager
  //                        .engineList
  //                        .get(engineBlack)
  //                        .sendCommand("time_settings 0 " + timeb + " 1");
  //                }
  //                Lizzie.engineManager.engineList.get(engineWhite).genmoveForPk("W");
  //              }
  //            };
  //        Thread thread = new Thread(runnable);
  //        thread.start();
  //      }
  //
  //      Lizzie.board.clearbestmovesafter2(Lizzie.board.getHistory().getStart());
  //      Lizzie.frame.setPlayers(
  //          Lizzie.engineManager.engineList.get(engineWhite).oriEnginename,
  //          Lizzie.engineManager.engineList.get(engineBlack).oriEnginename);
  //      GameInfo gameInfo = Lizzie.board.getHistory().getGameInfo();
  //      gameInfo.setPlayerWhite(Lizzie.engineManager.engineList.get(engineWhite).oriEnginename);
  //      gameInfo.setPlayerBlack(Lizzie.engineManager.engineList.get(engineBlack).oriEnginename);
  //    }
  //  }

  public void resetEnginePk() {
    enginePkPanel.add(chkenginePkPlayouts);
    enginePkPanel.add(txtenginePkPlayputs);
    txtenginePkPlayputs.setBounds(445, 2, 50, 18);
    chkenginePkPlayouts.setBounds(355, 1, 20, 18);
    enginePkPanel.add(txtenginePkPlayputsWhite);
    txtenginePkPlayputsWhite.setBounds(513, 2, 50, 18);
    enginePkPanel.add(enginePkBlack);
    enginePkBlack.setBounds(90, 2, 95, 18);
    enginePkPanel.add(enginePkWhite);
    enginePkWhite.setBounds(255, 2, 95, 18);
    enginePkPanel.add(chkenginePkFirstPlayputs);
    enginePkPanel.add(txtenginePkFirstPlayputs);
    chkenginePkFirstPlayputs.setBounds(248, 23, 20, 18);
    enginePkPanel.add(txtenginePkFirstPlayputsWhite);
    txtenginePkFirstPlayputs.setBounds(350, 24, 50, 18);
    txtenginePkFirstPlayputsWhite.setBounds(415, 24, 50, 18);

    enginePkPanel.add(chkenginePkTime);
    enginePkPanel.add(txtenginePkTime);
    enginePkPanel.add(txtenginePkTimeWhite);
    chkenginePkTime.setBounds(110, 23, 20, 18);
    txtenginePkTime.setBounds(190, 24, 23, 18);
    txtenginePkTimeWhite.setBounds(227, 24, 23, 18);

    enginePkPanel.add(chkenginePkFirstPlayputs);
    chkenginePkFirstPlayputs.setBounds(248, 23, 20, 18);
    enginePkPanel.add(txtenginePkFirstPlayputsWhite);
    enginePkPanel.add(txtenginePkFirstPlayputs);
    txtenginePkFirstPlayputs.setBounds(350, 24, 50, 18);

    txtenginePkFirstPlayputsWhite.setBounds(415, 24, 50, 18);

    enginePkPanel.add(chkenginePkBatch);
    chkenginePkBatch.setBounds(465, 23, 20, 18);
    txtenginePkBatch.setBounds(515, 24, 30, 18);
    enginePkPanel.add(txtenginePkBatch);

    chkenginePkContinue.setBounds(545, 23, 20, 18);
    enginePkPanel.add(chkenginePkContinue);
  }

  private void setButtonVisiable() {
    if (Lizzie.config.liveButton) liveButton.setVisible(true);
    else liveButton.setVisible(false);
    if (Lizzie.config.share) share.setVisible(true);
    else share.setVisible(false);
    if (Lizzie.config.flashAnalyze) flashAnalyze.setVisible(true);
    else flashAnalyze.setVisible(false);
    if (Lizzie.config.kataEstimate) kataEstimate.setVisible(true);
    else kataEstimate.setVisible(false);
    if (Lizzie.config.batchOpen) batchOpen.setVisible(true);
    else batchOpen.setVisible(false);
    if (Lizzie.config.openfile) openfile.setVisible(true);
    else openfile.setVisible(false);
    if (Lizzie.config.savefile) savefile.setVisible(true);
    else savefile.setVisible(false);
    if (Lizzie.config.analyzeList) analyzeList.setVisible(true);
    else analyzeList.setVisible(false);

    if (Lizzie.config.refresh) refresh.setVisible(true);
    else refresh.setVisible(false);
    if (Lizzie.config.analyse) analyse.setVisible(true);
    else analyse.setVisible(false);
    if (Lizzie.config.tryPlay) tryPlay.setVisible(true);
    else tryPlay.setVisible(false);
    if (Lizzie.config.setMain) setMain.setVisible(true);
    else setMain.setVisible(false);
    if (Lizzie.config.backMain) backMain.setVisible(true);
    else backMain.setVisible(false);
    if (Lizzie.config.clearButton) clearButton.setVisible(true);
    else clearButton.setVisible(false);
    if (Lizzie.config.countButton) countButton.setVisible(true);
    else countButton.setVisible(false);
    if (Lizzie.config.finalScore) finalScore.setVisible(true);
    else finalScore.setVisible(false);
    if (Lizzie.config.heatMap) heatMap.setVisible(true);
    else heatMap.setVisible(false);
    if (Lizzie.config.badMoves) badMoves.setVisible(true);
    else badMoves.setVisible(false);
    if (Lizzie.config.deleteMove) deleteMove.setVisible(true);
    else deleteMove.setVisible(false);
    if (Lizzie.config.move) move.setVisible(true);
    else move.setVisible(false);
    if (Lizzie.config.coords) coords.setVisible(true);
    else coords.setVisible(false);
    if (Lizzie.config.moveRank) moveRank.setVisible(true);
    else moveRank.setVisible(false);
    if (Lizzie.config.autoPlay) autoPlay.setVisible(true);
    else autoPlay.setVisible(false);
  }

  public void autoPlayMain(boolean autoQuit) {
    if (isAutoPlayMain) return;
    isAutoPlayMain = true;
    Runnable runnable =
        new Runnable() {
          public void run() {
            double time = -1;
            try {
              time = 1000 * Double.parseDouble(txtAutoMain.getText().replace(" ", ""));
            } catch (NumberFormatException err) {
            }
            if (time <= 0) {
              chkAutoMain.setSelected(false);
              isAutoPlayMain = false;
              return;
            }
            BoardHistoryNode curNode = Lizzie.board.getHistory().getCurrentHistoryNode();
            while (chkAutoMain.isSelected()) {
              try {
                if (curNode == Lizzie.board.getHistory().getCurrentHistoryNode()) {
                  if (Lizzie.config.directlyWithBestMove) {
                    Lizzie.frame.playBestMove();
                  } else if (!Lizzie.board.nextMove(true)) {
                    if (Lizzie.config.continueWithBestMove) {
                      BoardHistoryNode cur = Lizzie.board.getHistory().getCurrentHistoryNode();
                      if (!cur.getData().lastMove.isPresent()
                          && cur.previous().isPresent()
                          && !cur.previous().get().getData().lastMove.isPresent()) break;
                      Lizzie.frame.playBestMove();
                    } else {
                      if (autoQuit) break;
                    }
                    try {
                      time = 1000 * Integer.parseInt(txtAutoMain.getText().replace(" ", ""));
                    } catch (NumberFormatException err) {
                    }
                  }
                }
                curNode = Lizzie.board.getHistory().getCurrentHistoryNode();
                Thread.sleep((int) time);
              } catch (InterruptedException e) {
                e.printStackTrace();
              }
            }
            isAutoPlayMain = false;
            chkAutoMain.setSelected(false);
          }
        };
    Thread thread = new Thread(runnable);
    thread.start();
  }

  public void autoPlaySub() {
    if (isAutoPlaySub) return;
    isAutoPlaySub = true;
    if (chkAutoSub.isSelected()) {
      Runnable runnable =
          new Runnable() {
            public void run() {
              double time = -1;
              try {
                time = Double.parseDouble(txtAutoSub.getText().replace(" ", ""));
              } catch (NumberFormatException err) {
              }
              if (time <= 0) {
                chkAutoSub.setSelected(false);
                isAutoPlaySub = false;
                return;
              }
              while (chkAutoSub.isSelected()) {
                if (!LizzieFrame.subBoardRenderer.wheeled)
                  LizzieFrame.subBoardRenderer.setDisplayedBranchLength(
                      displayedSubBoardBranchLength);
                if (Lizzie.config.isShowingIndependentSub)
                  if (!Lizzie.frame.independentSubBoard.subBoardRenderer.wheeled)
                    Lizzie.frame.independentSubBoard.subBoardRenderer.setDisplayedBranchLength(
                        displayedSubBoardBranchLength);

                try {
                  try {
                    time = Integer.parseInt(txtAutoSub.getText().replace(" ", ""));
                  } catch (NumberFormatException err) {
                  }
                  Thread.sleep((int) time);
                } catch (InterruptedException e) {
                  e.printStackTrace();
                }
                displayedSubBoardBranchLength = displayedSubBoardBranchLength + 1;
                if (EngineManager.isEmpty
                    || !Lizzie.leelaz.isLoaded()
                    || !Lizzie.leelaz.isPondering()) Lizzie.frame.refresh();
              }
              isAutoPlaySub = false;
            }
          };
      Thread thread = new Thread(runnable);
      thread.start();
    } else {
      isAutoPlaySub = false;
      LizzieFrame.subBoardRenderer.setDisplayedBranchLength(-2);
      if (Lizzie.config.isShowingIndependentSub)
        Lizzie.frame.independentSubBoard.subBoardRenderer.setDisplayedBranchLength(-2);
    }
  }

  private int calcButtonLength() {
    int length = 0;
    if (liveButton.isVisible()) {
      length = length + liveButton.getWidth() - (Config.isScaled ? 0 : 1);
    }
    if (share.isVisible()) {
      length = length + share.getWidth() - (Config.isScaled ? 0 : 1);
    }
    if (kataEstimate.isVisible()) {
      length = length + kataEstimate.getWidth() - (Config.isScaled ? 0 : 1);
    }
    if (flashAnalyze.isVisible()) {
      length = length + flashAnalyze.getWidth() - (Config.isScaled ? 0 : 1);
    }
    if (batchOpen.isVisible()) {
      length = length + batchOpen.getWidth() - (Config.isScaled ? 0 : 1);
    }
    if (openfile.isVisible()) {
      length = length + openfile.getWidth() - (Config.isScaled ? 0 : 1);
    }
    if (savefile.isVisible()) {
      length = length + savefile.getWidth() - (Config.isScaled ? 0 : 1);
    }
    if (countButton.isVisible()) {
      length = length + countButton.getWidth() - (Config.isScaled ? 0 : 1);
    }
    if (finalScore.isVisible()) {
      length = length + finalScore.getWidth() - (Config.isScaled ? 0 : 1);
    }
    if (heatMap.isVisible()) {
      length = length + heatMap.getWidth() - (Config.isScaled ? 0 : 1);
    }
    if (badMoves.isVisible()) {
      length = length + badMoves.getWidth() - (Config.isScaled ? 0 : 1);
    }
    if (analyzeList.isVisible()) {
      length = length + analyzeList.getWidth() - (Config.isScaled ? 0 : 1);
    }
    if (refresh.isVisible()) {
      length = length + refresh.getWidth() - (Config.isScaled ? 0 : 1);
    }
    if (analyse.isVisible()) {
      length = length + analyse.getWidth() - (Config.isScaled ? 0 : 1);
    }
    if (tryPlay.isVisible()) {
      length = length + tryPlay.getWidth() - (Config.isScaled ? 0 : 1);
    }
    if (setMain.isVisible()) {
      length = length + setMain.getWidth() - (Config.isScaled ? 0 : 1);
    }
    if (backMain.isVisible()) {
      length = length + backMain.getWidth() - (Config.isScaled ? 0 : 1);
    }

    if (clearButton.isVisible()) {
      length = length + clearButton.getWidth() - (Config.isScaled ? 0 : 1);
    }

    if (deleteMove.isVisible()) {
      length = length + deleteMove.getWidth() - (Config.isScaled ? 0 : 1);
    }
    if (txtMoveNumber.isVisible()) {
      length = length + txtMoveNumber.getWidth() + (Config.isScaled ? 0 : 1);
    }

    if (gotomove.isVisible()) {
      length = length + gotomove.getWidth() - (Config.isScaled ? 0 : 1);
    }

    if (firstButton.isVisible()) {
      length = length + firstButton.getWidth() - (Config.isScaled ? 0 : 1);
    }
    if (backward10.isVisible()) {
      length = length + backward10.getWidth() - (Config.isScaled ? 0 : 1);
    }
    if (backward1.isVisible()) {
      length = length + backward1.getWidth() - (Config.isScaled ? 0 : 1);
    }
    if (forward1.isVisible()) {
      length = length + forward1.getWidth() - (Config.isScaled ? 0 : 1);
    }
    if (forward10.isVisible()) {
      length = length + forward10.getWidth() - (Config.isScaled ? 0 : 1);
    }
    if (lastButton.isVisible()) {
      length = length + lastButton.getWidth() - (Config.isScaled ? 0 : 1);
    }
    if (moveRank.isVisible()) {
      length = length + moveRank.getWidth() - (Config.isScaled ? 0 : 1);
    }
    if (move.isVisible()) {
      length = length + move.getWidth() - (Config.isScaled ? 0 : 1);
    }
    if (coords.isVisible()) {
      length = length + coords.getWidth() - (Config.isScaled ? 0 : 1);
    }
    if (autoPlay.isVisible()) {
      length = length + autoPlay.getWidth() - (Config.isScaled ? 0 : 1);
    }
    return length;
  }

  public int setLocationRight(int w) {

    if (autoPlay.isVisible()) {
      w = w - (autoPlay.getWidth() - (Config.isScaled ? 0 : 1));
      autoPlay.setLocation(w, 0);
    }
    if (coords.isVisible()) {
      w = w - (coords.getWidth() - (Config.isScaled ? 0 : 1));
      coords.setLocation(w, 0);
    }
    if (move.isVisible()) {
      w = w - (move.getWidth() - (Config.isScaled ? 0 : 1));
      move.setLocation(w, 0);
    }
    if (moveRank.isVisible()) {
      w = w - (moveRank.getWidth() - (Config.isScaled ? 0 : 1));
      moveRank.setLocation(w, 0);
    }
    if (lastButton.isVisible()) {
      w = w - (lastButton.getWidth() - (Config.isScaled ? 0 : 1));
      lastButton.setLocation(w, 0);
    }
    if (forward10.isVisible()) {
      w = w - (forward10.getWidth() - (Config.isScaled ? 0 : 1));
      forward10.setLocation(w, 0);
    }
    if (forward1.isVisible()) {
      w = w - (forward1.getWidth() - (Config.isScaled ? 0 : 1));
      forward1.setLocation(w, 0);
    }
    if (backward1.isVisible()) {
      w = w - (backward1.getWidth() - (Config.isScaled ? 0 : 1));
      backward1.setLocation(w, 0);
    }
    if (backward10.isVisible()) {
      w = w - (backward10.getWidth() - (Config.isScaled ? 0 : 1));
      backward10.setLocation(w, 0);
    }
    if (firstButton.isVisible()) {
      w = w - (firstButton.getWidth() - (Config.isScaled ? 0 : 1));
      firstButton.setLocation(w, 0);
    }
    if (gotomove.isVisible()) {
      w = w - (gotomove.getWidth() - (Config.isScaled ? 0 : 1));
      gotomove.setLocation(w, 0);
    }
    if (txtMoveNumber.isVisible()) {
      w = w - (txtMoveNumber.getWidth() + (Config.isScaled ? 0 : 1));
      txtMoveNumber.setLocation(w + (Config.isScaled ? 0 : 1), 1);
    }
    if (deleteMove.isVisible()) {
      w = w - (deleteMove.getWidth() - (Config.isScaled ? 0 : 1));
      deleteMove.setLocation(w, 0);
    }
    if (clearButton.isVisible()) {
      w = w - (clearButton.getWidth() - (Config.isScaled ? 0 : 1));
      clearButton.setLocation(w, 0);
    }

    if (backMain.isVisible()) {
      w = w - (backMain.getWidth() - (Config.isScaled ? 0 : 1));
      backMain.setLocation(w, 0);
    }
    if (setMain.isVisible()) {
      w = w - (setMain.getWidth() - (Config.isScaled ? 0 : 1));
      setMain.setLocation(w, 0);
    }
    if (tryPlay.isVisible()) {
      w = w - (tryPlay.getWidth() - (Config.isScaled ? 0 : 1));
      tryPlay.setLocation(w, 0);
    }

    if (analyse.isVisible()) {
      w = w - (analyse.getWidth() - (Config.isScaled ? 0 : 1));
      analyse.setLocation(w, 0);
    }
    if (refresh.isVisible()) {
      w = w - (refresh.getWidth() - (Config.isScaled ? 0 : 1));
      refresh.setLocation(w, 0);
    }
    if (analyzeList.isVisible()) {
      w = w - (analyzeList.getWidth() - (Config.isScaled ? 0 : 1));
      analyzeList.setLocation(w, 0);
    }
    if (badMoves.isVisible()) {
      w = w - (badMoves.getWidth() - (Config.isScaled ? 0 : 1));
      badMoves.setLocation(w, 0);
    }
    if (heatMap.isVisible()) {
      w = w - (heatMap.getWidth() - (Config.isScaled ? 0 : 1));
      heatMap.setLocation(w, 0);
    }
    if (countButton.isVisible()) {
      w = w - (countButton.getWidth() - (Config.isScaled ? 0 : 1));
      countButton.setLocation(w, 0);
    }
    if (finalScore.isVisible()) {
      w = w - (finalScore.getWidth() - (Config.isScaled ? 0 : 1));
      finalScore.setLocation(w, 0);
    }
    if (savefile.isVisible()) {
      w = w - (savefile.getWidth() - (Config.isScaled ? 0 : 1));
      savefile.setLocation(w, 0);
    }
    if (openfile.isVisible()) {
      w = w - (openfile.getWidth() - (Config.isScaled ? 0 : 1));
      openfile.setLocation(w, 0);
    }
    if (batchOpen.isVisible()) {
      w = w - (batchOpen.getWidth() - (Config.isScaled ? 0 : 1));
      batchOpen.setLocation(w, 0);
    }
    if (flashAnalyze.isVisible()) {
      w = w - (flashAnalyze.getWidth() - (Config.isScaled ? 0 : 1));
      flashAnalyze.setLocation(w, 0);
    }
    if (kataEstimate.isVisible()) {
      w = w - (kataEstimate.getWidth() - (Config.isScaled ? 0 : 1));
      kataEstimate.setLocation(w, 0);
    }
    if (share.isVisible()) {
      w = w - (share.getWidth() - (Config.isScaled ? 0 : 1));
      share.setLocation(w, 0);
    }
    if (liveButton.isVisible()) {
      w = w - (liveButton.getWidth() - (Config.isScaled ? 0 : 1));
      liveButton.setLocation(w, 0);
    }

    return w;
  }

  public int setLocationLeft(int w) {
    if (liveButton.isVisible()) {
      liveButton.setLocation(w, 0);
      w = w + liveButton.getWidth() - (Config.isScaled ? 0 : 1);
    }
    if (share.isVisible()) {
      share.setLocation(w, 0);
      w = w + share.getWidth() - (Config.isScaled ? 0 : 1);
    }
    if (kataEstimate.isVisible()) {
      kataEstimate.setLocation(w, 0);
      w = w + kataEstimate.getWidth() - (Config.isScaled ? 0 : 1);
    }
    if (flashAnalyze.isVisible()) {
      flashAnalyze.setLocation(w, 0);
      w = w + flashAnalyze.getWidth() - (Config.isScaled ? 0 : 1);
    }
    if (batchOpen.isVisible()) {
      batchOpen.setLocation(w, 0);
      w = w + batchOpen.getWidth() - (Config.isScaled ? 0 : 1);
    }
    if (openfile.isVisible()) {
      openfile.setLocation(w, 0);
      w = w + openfile.getWidth() - (Config.isScaled ? 0 : 1);
    }
    if (savefile.isVisible()) {
      savefile.setLocation(w, 0);
      w = w + savefile.getWidth() - (Config.isScaled ? 0 : 1);
    }
    if (countButton.isVisible()) {
      countButton.setLocation(w, 0);
      w = w + countButton.getWidth() - (Config.isScaled ? 0 : 1);
    }
    if (finalScore.isVisible()) {
      finalScore.setLocation(w, 0);
      w = w + finalScore.getWidth() - (Config.isScaled ? 0 : 1);
    }
    if (heatMap.isVisible()) {
      heatMap.setLocation(w, 0);
      w = w + heatMap.getWidth() - (Config.isScaled ? 0 : 1);
    }
    if (badMoves.isVisible()) {
      badMoves.setLocation(w, 0);
      w = w + badMoves.getWidth() - (Config.isScaled ? 0 : 1);
    }
    if (analyzeList.isVisible()) {
      analyzeList.setLocation(w, 0);
      w = w + analyzeList.getWidth() - (Config.isScaled ? 0 : 1);
    }
    if (refresh.isVisible()) {
      refresh.setLocation(w, 0);
      w = w + refresh.getWidth() - (Config.isScaled ? 0 : 1);
    }
    if (analyse.isVisible()) {
      analyse.setLocation(w, 0);
      w = w + analyse.getWidth() - (Config.isScaled ? 0 : 1);
    }
    if (tryPlay.isVisible()) {
      tryPlay.setLocation(w, 0);
      w = w + tryPlay.getWidth() - (Config.isScaled ? 0 : 1);
    }
    if (setMain.isVisible()) {
      setMain.setLocation(w, 0);
      w = w + setMain.getWidth() - (Config.isScaled ? 0 : 1);
    }
    if (backMain.isVisible()) {
      backMain.setLocation(w, 0);
      w = w + backMain.getWidth() - (Config.isScaled ? 0 : 1);
    }

    if (clearButton.isVisible()) {
      clearButton.setLocation(w, 0);
      w = w + clearButton.getWidth() - (Config.isScaled ? 0 : 1);
    }

    if (deleteMove.isVisible()) {
      deleteMove.setLocation(w, 0);
      w = w + deleteMove.getWidth() - (Config.isScaled ? 0 : 1);
    }
    if (txtMoveNumber.isVisible()) {
      txtMoveNumber.setLocation(w + (Config.isScaled ? 0 : 1), 1);
      w = w + txtMoveNumber.getWidth() + (Config.isScaled ? 0 : 1);
    }

    if (gotomove.isVisible()) {
      gotomove.setLocation(w, 0);
      w = w + gotomove.getWidth() - (Config.isScaled ? 0 : 1);
    }

    if (firstButton.isVisible()) {
      firstButton.setLocation(w, 0);
      w = w + firstButton.getWidth() - (Config.isScaled ? 0 : 1);
    }
    if (backward10.isVisible()) {
      backward10.setLocation(w, 0);
      w = w + backward10.getWidth() - (Config.isScaled ? 0 : 1);
    }
    if (backward1.isVisible()) {
      backward1.setLocation(w, 0);
      w = w + backward1.getWidth() - (Config.isScaled ? 0 : 1);
    }
    if (forward1.isVisible()) {
      forward1.setLocation(w, 0);
      w = w + forward1.getWidth() - (Config.isScaled ? 0 : 1);
    }
    if (forward10.isVisible()) {
      forward10.setLocation(w, 0);
      w = w + forward10.getWidth() - (Config.isScaled ? 0 : 1);
    }
    if (lastButton.isVisible()) {
      lastButton.setLocation(w, 0);
      w = w + lastButton.getWidth() - (Config.isScaled ? 0 : 1);
    }
    if (moveRank.isVisible()) {
      moveRank.setLocation(w, 0);
      w = w + moveRank.getWidth() - (Config.isScaled ? 0 : 1);
    }
    if (move.isVisible()) {
      move.setLocation(w, 0);
      w = w + move.getWidth() - (Config.isScaled ? 0 : 1);
    }
    if (coords.isVisible()) {
      coords.setLocation(w, 0);
      w = w + coords.getWidth() - (Config.isScaled ? 0 : 1);
    }
    if (autoPlay.isVisible()) {
      autoPlay.setLocation(w, 0);
      w = w + autoPlay.getWidth() - (Config.isScaled ? 0 : 1);
    }
    return w;
  }

  public void reSetButtonLocation() {
    setButtonVisiable();
    int w = Lizzie.frame.getWidth();
    if (Lizzie.leelaz == null || !Lizzie.leelaz.isKatago) {
      kataEstimate.setVisible(false);
    } else if (Lizzie.config.kataEstimate) {
      kataEstimate.setVisible(true);
    }
    int length = calcButtonLength();
    if (length < w - (this.showDetail ? (Config.isScaled ? 34 : 33) : 16)) {
      rightMove.setVisible(false);
      leftMove.setVisible(false);
      setLocationLeft(0);
      if (showDetail)
        buttonPane.setBounds(
            (Math.max(0, w - 16 - length - (Config.isScaled ? 20 : 19)) / 2)
                + (Config.isScaled ? 20 : 19),
            0,
            length,
            26);
      else buttonPane.setBounds(Math.max(0, (w - 16 - length) / 2), 0, length, 26);
    } else if (rightMode) {
      rightMove.setVisible(false);
      if (showDetail) w = setLocationRight(w - (Config.isScaled ? 20 : 19));
      else w = setLocationRight(w);
      if (w < 15) {
        if (showDetail)
          buttonPane.setBounds(Config.isScaled ? 40 : 38, 0, Lizzie.frame.getWidth(), 26);
        else buttonPane.setBounds(Config.isScaled ? 20 : 19, 0, Lizzie.frame.getWidth(), 26);
        leftMove.setVisible(true);
        w = Lizzie.frame.getWidth();
        setLocationRight(w - (showDetail ? 55 : 35));
      } else {
        buttonPane.setBounds(showDetail ? 19 : 0, 0, Lizzie.frame.getWidth(), 26);
        leftMove.setVisible(false);
      }
    } else {
      leftMove.setVisible(false);
      int x = setLocationLeft(0);
      if (w < x + (showDetail ? (Config.isScaled ? 34 : 33) : 16)) {
        if (showDetail)
          buttonPane.setBounds(
              (Config.isScaled ? 20 : 19), 0, this.getWidth() - (Config.isScaled ? 41 : 39), 26);
        else buttonPane.setBounds(0, 0, this.getWidth() - (Config.isScaled ? 20 : 19), 26);
        rightMove.setVisible(true);
        if (showDetail)
          rightMove.setBounds(this.getWidth() - (Config.isScaled ? 21 : 20), 0, 20, 26);
        else rightMove.setBounds(this.getWidth() - (Config.isScaled ? 20 : 19), 0, 20, 26);
      } else {
        buttonPane.setBounds(showDetail ? 20 : 0, 0, w - (Config.isScaled ? 20 : 19), 26);
        rightMove.setVisible(false);
      }
    }
    //    if (liveButtonListener != null) liveButton.removeActionListener(liveButtonListener);
    //    liveButtonListener =
    //        new ActionListener() {
    //          public void actionPerformed(ActionEvent e) {
    //            yike.show(buttonPane, yike.getX(), yike.getY() - yike.getHeight());
    //          }
    //        };
    //    liveButton.addActionListener(liveButtonListener);
    //
    //    if (shareListener != null) share.removeActionListener(shareListener);
    //    shareListener =
    //        new ActionListener() {
    //          public void actionPerformed(ActionEvent e) {
    //            sharePopup.show(buttonPane, share.getX(), share.getY() - sharePopup.getHeight());
    //          }
    //        };
    //    share.addActionListener(shareListener);
  }

  public void setChkShowBlack(boolean show) {
    if (chkShowBlack != null) chkShowBlack.setSelected(show);
    Lizzie.config.showBlackCandidates = show;
  }

  public void setChkShowWhite(boolean show) {
    if (chkShowWhite != null) chkShowWhite.setSelected(show);
    Lizzie.config.showWhiteCandidates = show;
  }

  //  public void setFontSize(int fontSize) {
  //    Font smallFont = new Font(Config.sysDefaultFontName, Font.PLAIN, fontSize);
  //    deleteMove.setFont(smallFont);
  //    share.setFont(smallFont);
  //    badMoves.setFont(smallFont);
  //    liveButton.setFont(smallFont);
  //    clearButton.setFont(smallFont);
  //    countButton.setFont(smallFont);
  //    gotomove.setFont(smallFont);
  //    savefile.setFont(smallFont);
  //    openfile.setFont(smallFont);
  //    kataEstimate.setFont(smallFont);
  //    analyse.setFont(smallFont);
  //    heatMap.setFont(smallFont);
  //    backMain.setFont(smallFont);
  //    setMain.setFont(smallFont);
  //    batchOpen.setFont(smallFont);
  //    refresh.setFont(smallFont);
  //    tryPlay.setFont(smallFont);
  //    analyzeList.setFont(smallFont);
  //    move.setFont(smallFont);
  //    coords.setFont(smallFont);
  //    autoPlay.setFont(smallFont);
  //  }
}
