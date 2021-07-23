package featurecat.lizzie.gui;

import static java.awt.RenderingHints.KEY_ANTIALIASING;
import static java.awt.RenderingHints.VALUE_ANTIALIAS_ON;
import static java.awt.image.BufferedImage.TYPE_INT_ARGB;
import static java.awt.image.BufferedImage.TYPE_INT_RGB;
import static java.lang.Math.max;
import static java.lang.Math.min;
import static java.lang.Math.round;

import com.jhlabs.image.GaussianFilter;
import com.teamdev.jxbrowser.chromium.Browser;
import com.teamdev.jxbrowser.chromium.PopupContainer;
import com.teamdev.jxbrowser.chromium.PopupHandler;
import com.teamdev.jxbrowser.chromium.PopupParams;
import com.teamdev.jxbrowser.chromium.swing.BrowserView;
import featurecat.lizzie.Config;
import featurecat.lizzie.Lizzie;
import featurecat.lizzie.analysis.AnalysisEngine;
import featurecat.lizzie.analysis.GameInfo;
import featurecat.lizzie.analysis.KataEstimate;
import featurecat.lizzie.analysis.Leelaz;
import featurecat.lizzie.analysis.MoveData;
import featurecat.lizzie.analysis.ReadBoard;
import featurecat.lizzie.rules.Board;
import featurecat.lizzie.rules.BoardData;
import featurecat.lizzie.rules.BoardHistoryNode;
import featurecat.lizzie.rules.GIBParser;
import featurecat.lizzie.rules.GroupInfo;
import featurecat.lizzie.rules.MoveLinkedList;
import featurecat.lizzie.rules.Movelist;
import featurecat.lizzie.rules.NodeInfo;
import featurecat.lizzie.rules.SGFParser;
import featurecat.lizzie.rules.Stone;
import featurecat.lizzie.util.Utils;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.font.TextAttribute;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import javax.swing.text.DefaultCaret;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.StyleSheet;
import org.json.JSONArray;
import org.json.JSONObject;

/** The window used to display the game. */
public class LizzieFrame extends JFrame {
  private static final ResourceBundle resourceBundle = Lizzie.resourceBundle;
  //      Lizzie.config.useLanguage == 0
  //          ? ResourceBundle.getBundle("l10n.DisplayStrings")
  //          : (Lizzie.config.useLanguage == 1
  //              ? ResourceBundle.getBundle("l10n.DisplayStrings", new Locale("zh", "CN"))
  //              : ResourceBundle.getBundle("l10n.DisplayStrings", new Locale("en", "US")));

  private static final String[] commands = {
    resourceBundle.getString("LizzieFrame.commands.keySpace"),
    resourceBundle.getString("LizzieFrame.commands.keyN"),
    resourceBundle.getString("LizzieFrame.commands.keyEnter"),
    // "Enter(回车)|与引擎继续对弈",
    resourceBundle.getString("LizzieFrame.commands.mouseWheelScroll"),
    resourceBundle.getString("LizzieFrame.commands.keyComma"),
    // ",(逗号)或滚轮单击|落最佳一手,如果鼠标指向变化图则落子到变化图结束",
    resourceBundle.getString("LizzieFrame.commands.rightClick"),
    resourceBundle.getString("LizzieFrame.commands.keyA"),
    resourceBundle.getString("LizzieFrame.commands.wheelAndR"),
    // "滚轮单击|落子到当前变化图结束",
    // "滚轮长按或R|快速回放鼠标指向的变化图",
    resourceBundle.getString("LizzieFrame.commands.mousePointSub"),
    // "鼠标指向小棋盘|左键/右键点击可切换小棋盘变化图,滚轮可控制变化图前进后退",
    resourceBundle.getString("LizzieFrame.commands.keyY"),
    // "B|显示超级鹰眼",
    resourceBundle.getString("LizzieFrame.commands.keyU"),
    // "U|显示AI选点列表",
    resourceBundle.getString("LizzieFrame.commands.keyI"),
    // "I|编辑棋局信息",
    resourceBundle.getString("LizzieFrame.commands.keySlash"),
    resourceBundle.getString("LizzieFrame.commands.keyB"),
    //  "T|返回主分支",
    resourceBundle.getString("LizzieFrame.commands.keyV"),
    // "V|试下",
    resourceBundle.getString("LizzieFrame.commands.keyF"),
    resourceBundle.getString("LizzieFrame.commands.keyZ"),
    resourceBundle.getString("LizzieFrame.commands.keyShiftF"),
    // "F|关闭/显示AI选点",
    resourceBundle.getString("LizzieFrame.commands.keyHandY"),
    // "H或Y|显示纯网络分析结果",
    // resourceBundle.getString("LizzieFrame.commands.keyI"),
    resourceBundle.getString("LizzieFrame.commands.key123456789"),
    resourceBundle.getString("LizzieFrame.commands.keyUpDownArrow"),
    // resourceBundle.getString("LizzieFrame.commands.keyDownArrow"),
    resourceBundle.getString("LizzieFrame.commands.keyC"),
    resourceBundle.getString("LizzieFrame.commands.keyP"),
    resourceBundle.getString("LizzieFrame.commands.keyM"),
    resourceBundle.getString("LizzieFrame.commands.keyAltC"),
    resourceBundle.getString("LizzieFrame.commands.keyAltV"),
    resourceBundle.getString("LizzieFrame.commands.keyJ"),
    // resourceBundle.getString("LizzieFrame.commands.keyV"),
    resourceBundle.getString("LizzieFrame.commands.keyW"),
    resourceBundle.getString("LizzieFrame.commands.keyCtrlW"),
    resourceBundle.getString("LizzieFrame.commands.keyG"),
    resourceBundle.getString("LizzieFrame.commands.keyAltZ"),
    resourceBundle.getString("LizzieFrame.commands.keyBracket"),
    resourceBundle.getString("LizzieFrame.commands.keyCtrlT"),
    resourceBundle.getString("LizzieFrame.commands.keyHome"),
    resourceBundle.getString("LizzieFrame.commands.keyEnd"),
    resourceBundle.getString("LizzieFrame.commands.keyControl"),
    resourceBundle.getString("LizzieFrame.commands.keyDelete"),
    resourceBundle.getString("LizzieFrame.commands.keyBackspace"),
    resourceBundle.getString("LizzieFrame.commands.keyE"),
  };
  private static final String DEFAULT_TITLE = resourceBundle.getString("LizzieFrame.title");
  private JLayeredPane basePanel = new JLayeredPane();
  public static BoardRenderer boardRenderer;
  public static BoardRenderer boardRenderer2;
  public static SubBoardRenderer subBoardRenderer;
  public SubBoardRenderer subBoardRenderer2;
  public SubBoardRenderer subBoardRenderer3;
  public SubBoardRenderer subBoardRenderer4;
  public int subBoardXmouse;
  public int subBoardYmouse;
  public int subBoardLengthmouse;
  private static VariationTree variationTree;
  private static VariationTreeBig variationTreeBig;
  public static WinrateGraph winrateGraph;
  public static Menu menu;
  public static BottomToolbar toolbar;
  // public static EditToolbar editToolbar;
  public Optional<List<String>> variationOpt;

  public static boolean urlSgf = false;
  public boolean syncBoard = false;
  public boolean bothSync = false;
  int maxMvNum;
  boolean firstSync = false;
  javax.swing.Timer timer;
  public static Font uiFont;
  public static Font playoutsFont;
  public static Font winrateFont;
  public boolean isShowingRightMenu;
  public ArrayList<Movelist> movelist;
  public AnalysisFrame analysisFrame;
  public AnalysisFrame analysisFrame2;
  public MoveListFrame moveListFrame;
  public MoveListFrame moveListFrame2;
  public int blackorwhite = 0;
  // private final BufferStrategy bs;
  public boolean isCounting = false;
  public boolean isAutocounting = false;

  public static final int[] outOfBoundCoordinate = new int[] {-1, -1};

  public boolean isBatchAna = false;
  public int BatchAnaNum = 0;
  public static File curFile;
  public ArrayList<File> Batchfiles = new ArrayList<File>();
  public int[] suggestionclick = outOfBoundCoordinate;
  public int[] clickbadmove = outOfBoundCoordinate;
  public int[] mouseOverCoordinate = outOfBoundCoordinate;
  private int curSuggestionMoveOrderByNumber = -1;
  public boolean showControls = false;
  private long showControlTime;
  public boolean isPlayingAgainstLeelaz = false;
  public boolean isAnaPlayingAgainstLeelaz = false;
  public boolean playerIsBlack = true;
  public static boolean canGoAfterload = true;
  public int winRateGridLines = 3;
  public int BoardPositionProportion = Lizzie.config.boardPositionProportion;
  private long lastAutocomTime = System.currentTimeMillis();
  private int autoIntervalCom;
  // private int autoInterval;
  private long lastAutosaveTime = System.currentTimeMillis();
  public boolean isReplayVariation = false;
  public RightClickMenu RightClickMenu;
  public RightClickMenu2 RightClickMenu2;
  //  private int boardPos = 0;
  // public String komi = "7.5";
  double winRate;
  double score;
  double scoreOnStatic;
  double scoreStdev;
  // private ChangeMoveDialog2 ChangeMoveDialog2 = new ChangeMoveDialog2();

  // Save the player title
  private String playerTitle = "";
  private String resultTitle = "";
  public static String fileNameTitle = "";

  // private JScrollPane variationScrollPane;
  // private Rectangle variationCommentRect;

  // Display Comment
  private boolean isCommentArea = true;
  private boolean cachedIsCommentArea = true;
  // private BufferedImage cachedCommentImage = new BufferedImage(1, 1, TYPE_INT_ARGB);
  public JScrollPane commentScrollPane;
  public JPanel commentBlunderControlPane;
  public JPanel blunderContentPane;

  private TableModel blunderModelBlack;
  private TableModel blunderModelWhite;
  public JTable blunderTabelBlack;
  private JTable blunderTabelWhite;
  private int blunderSortNum = 2;
  private boolean blunderIsSorted = false;
  private boolean blunderSortIsOriginOrder = true;

  private JPanel tablePanelMinBlack;
  private JPanel tablePanelMinWhite;
  public JScrollPane minScrollpaneBlack;
  public JScrollPane minScrollpaneWhite;
  //  private boolean isMouseOverComment = false;
  //  private boolean isMouseOverBlunderControl = false;
  private JPaintTextPane commentTextPane;
  private JPaintTextArea commentTextArea;
  private String cachedComment = "";
  private int commentFontSize;
  private int commentPaneFontSize;
  // private Rectangle commentRect;
  // private int commentPos = 0;
  //  private boolean redrawCommentForce = false;
  public KataEstimate zen;
  public SetEstimateParam setEstimateParam;
  public ReadBoard readBoard;
  public ConfigDialog2 configDialog2;
  public boolean isShowingPolicy = false;
  public boolean isShowingHeatmap = false;
  public boolean isMouseOver = false;
  private boolean isShowingRect = false;
  public boolean isMouseOnSub = false;
  // Show the playouts in the title
  private ScheduledExecutorService showPlayouts = Executors.newScheduledThreadPool(1);
  private ScheduledExecutorService updateTitleSchedual = Executors.newScheduledThreadPool(1);
  private String visitsString = "";
  private int visitsCount = 4;
  private VisitsTemp[] visitsTemp = new VisitsTemp[visitsCount];
  // private long lastPlayouts0 = 0;
  // private long lastPlayouts1 = 0;
  // private long lastPlayouts2 = 0;
  private long lastPlayouts = 0;
  public boolean isDrawVisitsInTitle = true;
  private Stone draggedstone;
  private int[] startcoords = new int[2];
  private int[] draggedCoords;
  public JPanel mainPanel;
  public JToolBar topPanel;
  // private JPanel listPanel;
  private boolean canShowBigBoardImage = true;
  private boolean oriShowListPane;
  private boolean OriShowVariationGraph;
  private JLayeredPane tempGamePanelAll;
  private JPanel tempGamePanelTop;
  private JScrollPane tempGameScrollPanel;
  private JPanel tempGamePanel;
  private JPopupMenu bigBoardPanel;
  private boolean isShowingBigBoardPanel = false;
  MouseMotionListener tempGamePanelLis;
  MouseListener tempGamePanelMoveLis;
  MouseListener bigBoardPanelLis;
  private int bigBoardIndex = -1;
  private int bigBoardLastX = -1;
  private int bigBoardLastY = -1;
  public JScrollPane listScrollpane;
  public JTable listTable;
  public int listTableColum5Width;
  private int blunderTableColum0Width;
  private int blunderTableColum2Width;
  public int blunderTableColum3Width;
  javax.swing.Timer listTabletimer;
  javax.swing.Timer blunderTableTimer;
  private TableModel listDataModel;
  private boolean scoreColumnIsHidden = false;
  private boolean scoreIsHiddenInBlunderTable = false;

  public int selectedorder = -1;
  public int clickOrder = -1;
  public boolean hasMoveOutOfList = false;
  public int currentRow = -1;
  // public JPanel statusPanel;
  //  public int mainPanleX;
  //  public int mainPanleY;
  public int toolbarHeight = 26;
  public int topPanelHeight = Lizzie.config.menuHeight;
  boolean isSmallCap = false;
  private BufferedImage bufferedContainer;
  boolean firstTime = true;
  private HTMLDocument htmlDoc;
  private HtmlKit htmlKit;
  private StyleSheet htmlStyle;
  public Input input = new Input();
  public InputSubboard input2 = new InputSubboard();
  public boolean noInput = true;
  public AnalysisTable analysisTable;
  static JTextField text;
  //  private long startSyncTime = System.currentTimeMillis();
  //  private boolean isSyncing = false;
  //    private boolean noRedrawComment = false;

  // private boolean isSavingImage = false;
  public boolean isKeepingForce = false;

  public int grx;
  public int gry;
  public int grw;
  public int grh;

  public int lastGrw = -1;
  public int lastGrh = -1;
  private long winratePaneTime;
  private boolean refreshFromInfo = false;
  private boolean refreshFromResized = false;

  public int statx;
  public int staty;
  public int statw;
  public int stath;

  public int boardX;
  public int boardY;
  public int maxSize;

  public int subMaxSize;

  public int bowserX = -5;
  public int bowserY = 0;
  public int bowserWidth = 1240;
  public int bowserHeight = 750;

  private int selectX1;
  private int selectY1;
  // private int selectX2;
  // private int selectY2;

  public int selectCoordsX1;
  public int selectCoordsY1;
  public int selectCoordsX2;
  public int selectCoordsY2;

  public double extraModeWinrate1 = 0;
  public double extraModeWinrate2 = 0;

  // public static int extraMode = 3;
  public static int extraMode = Lizzie.config.extraMode; // 1=四方图2=双引擎3=思考 8=浮动棋盘模式

  public boolean selectForceAllow = true;

  public boolean isTrying = false;
  ArrayList<Movelist> tryMoveList;
  String tryString;
  String titleBeforeTrying;
  public Browser browser;
  JFrame frame;
  ArrayList<String> urlList;
  int urlIndex;
  public static OnlineDialog onlineDialog;
  // public int mode1;

  String weightText = "";
  String weightText2 = "";
  public static boolean isSavingRaw = false;
  public static boolean isSavingRawComment = false;
  public static boolean isShareing = false;
  //  private long shareTime = -1;
  public ShareFrame shareFrame;
  public BatchShareFrame batchShareFrame;
  SetKataRules setkatarules;
  public PublicKifuSearch search;

  public boolean isEnginePKSgfStart = false;
  public int enginePKSgfNum = 0;
  public ArrayList<ArrayList<Movelist>> enginePKSgfString = new ArrayList<ArrayList<Movelist>>();
  public ArrayList<SgfWinLossList> enginePkSgfWinLoss = new ArrayList<SgfWinLossList>();

  public int varTreeMaxX = 1;
  public int varTreeMaxY = 1;
  public int varTreeCurX;
  public int varTreeCurY;

  private int varTreeX;
  private int varTreeY;
  private int varTreeW;
  private int varTreeH;
  // private long startTreeRenderTime;
  // private boolean drawWrong = false;
  //   private boolean mouseOnVarTree = false;

  private BoardHistoryNode treeNode;
  private boolean redrawTree = false;
  private boolean completeDrawTree = true;
  private boolean redrawTreeLater = false;
  private boolean canDrawCurColor = false;
  public static boolean forceRecreate = false;
  public int tree_curposx;
  public int tree_posy;
  public int tree_diam;
  public int tree_DOT_DIAM;
  public int tree_RING_DIAM;
  public int tree_diff;
  public int tree_CENTER_DIAM;
  private JPanel varTreePane;
  private JScrollPane varTreeScrollPane;

  private JIMSendTextPane commentEditTextPane;
  public JScrollPane commentEditPane;

  public String enginePkTitile;
  public boolean hasEnginePkTitile = false;
  public IndependentSubBoard independentSubBoard;
  public IndependentMainBoard independentMainBoard;
  public FloatBoard floatBoard;

  private ScheduledThreadPoolExecutor timeScheduled;
  public int leftMinuts, leftSeconds, byoTimes, byoSeconds, maxByoTimes;
  public static boolean isShowingByoTime = false;
  public boolean isMarkuping = false;
  public int markupType = 0;
  private int lastLabel;
  private boolean hasMarkup;
  private String markupKey;
  private String markupValue;
  public ArrayList<String> priorityMoveCoords = new ArrayList<String>();

  public AnalysisEngine analysisEngine;
  private boolean redrawWinratePaneOnly = false;
  public boolean mouseOverChanged = false;
  public boolean isAutoReplying = false;
  private boolean cachedIsLoading = false;
  public boolean isBatchAnalysisMode = false;
  // int testFontSize = 12;
  private Color blunderBackground = new Color(225, 225, 225);
  private Color blunderForeground = Color.BLACK;
  private Color blunderGoodMove = Color.GREEN.darker().darker();
  private Color listTableBackground = new Color(0, 0, 0, 10);
  public boolean isAutoAnalyzingDiffNode = false;

  public boolean isInScoreMode = false;
  public boolean ponderStatusBeforeScore = false;
  private KeyListener gtpShortKey;

  private boolean WRNStatusBeforeGame = Lizzie.config.chkKataEngineWRN;
  private boolean autoWRNStatusBeforeGame = Lizzie.config.autoLoadKataEngineWRN;
  private double WRNValueBeforeGenmove = 0;
  private boolean WRNSelectedBeforeGenmove = false;

  public static String allowcoords = "";
  public static String avoidcoords = "";
  public static boolean isforcing = false;
  public static boolean isallow = false;
  public static boolean isKeepForcing = false;
  public static boolean isTempForcing = false;

  static {
    // load fonts

    try {
      uiFont = new Font("SansSerif", Font.TRUETYPE_FONT, 12);
      //          Font.createFont(
      //              Font.TRUETYPE_FONT,
      //              Thread.currentThread()
      //                  .getContextClassLoader()
      //                  .getResourceAsStream("fonts/OpenSans-Regular.ttf"));
      winrateFont =
          Font.createFont(
              Font.TRUETYPE_FONT,
              Thread.currentThread()
                  .getContextClassLoader()
                  .getResourceAsStream("fonts/OpenSans-Semibold.ttf"));
    } catch (IOException | FontFormatException e) {
      e.printStackTrace();
    }
  }

  /** Creates a window */
  public LizzieFrame() {
    super(DEFAULT_TITLE);
    boardRenderer = new BoardRenderer(false);
    subBoardRenderer = new SubBoardRenderer(false);
    variationTree = new VariationTree();
    variationTreeBig = new VariationTreeBig();
    winrateGraph = new WinrateGraph();
    toolbar = new BottomToolbar();
    topPanel = new JToolBar();
    menu = new Menu();
    RightClickMenu = new RightClickMenu();
    RightClickMenu2 = new RightClickMenu2();
    // MenuTest menu = new MenuTest();
    // add(menu);
    // this.setJMenuBar(menu);
    // this.setVisible(true);
    this.setAlwaysOnTop(Lizzie.config.mainsalwaysontop);
    if (extraMode == 8) setMinimumSize(new Dimension(0, 0));
    else setMinimumSize(new Dimension(520, 400));
    if (extraMode == 1) {
      subBoardRenderer2 = new SubBoardRenderer(false);
      subBoardRenderer3 = new SubBoardRenderer(false);
      subBoardRenderer4 = new SubBoardRenderer(false);
      subBoardRenderer2.setOrder(1);
      subBoardRenderer3.setOrder(2);
      subBoardRenderer4.setOrder(3);
      subBoardRenderer.showHeat = false;
      subBoardRenderer.showHeatAfterCalc = false;
    }
    if (extraMode == 3) {
      boardRenderer2 = new BoardRenderer(false);
      boardRenderer2.setOrder(2);
      boardRenderer2.setDisplayedBranchLength(BoardRenderer.SHOW_RAW_BOARD);
    }
    for (int i = 0; i < visitsCount; i++) {
      visitsTemp[i] = new VisitsTemp();
      visitsTemp[i].node = Lizzie.board.getHistory().getCurrentHistoryNode();
    }

    gtpShortKey =
        new KeyAdapter() {
          public void keyPressed(KeyEvent e) {
            if (e.getKeyCode() == KeyEvent.VK_E) {
              Lizzie.frame.toggleGtpConsole();
            }
          }
        };
    mainPanel =
        new JPanel(true) {
          @Override
          protected void paintComponent(Graphics g) {
            //  super.paintComponent(g);
            //        	  final Graphics2D g1 = (Graphics2D) g;
            //        	  final AffineTransform t = g1.getTransform();
            //        	  t.setToScale(1, 1);
            //        	  g1.setTransform(t);
            //        	  g.setColor(Color.DARK_GRAY);
            //        	  g.fillRect(0, 0, mainPanel.getWidth(), mainPanel.getHeight());
            paintMianPanel(g);
          }
        };
    mainPanel.enableInputMethods(false);

    mainPanel.addMouseListener(
        new MouseAdapter() {
          public void mouseEntered(MouseEvent e) {
            if (Lizzie.frame.isInTemporaryBoard) {
              Lizzie.frame.stopTemporaryBoardMaybe();
              Lizzie.frame.refresh();
            }
          }
        });
    tempGamePanelAll = new JLayeredPane();
    tempGamePanelAll.setLayout(null);
    tempGamePanelAll.setVisible(false);
    tempGamePanelAll.setFocusable(false);
    tempGamePanelAll.enableInputMethods(false);
    tempGamePanelAll.setBackground(new Color(100, 100, 100));
    tempGamePanel = new JPanel();
    tempGameScrollPanel = new JScrollPane(tempGamePanel);
    tempGameScrollPanel.setVisible(false);
    tempGameScrollPanel.setFocusable(false);
    tempGameScrollPanel.enableInputMethods(false);

    tempGamePanelTop = new JPanel();
    tempGamePanelTop.setLayout(null);
    tempGamePanelTop.setFocusable(false);
    tempGamePanelTop.enableInputMethods(false);
    tempGamePanelTop.setBackground(new Color(100, 100, 100));
    tempGameScrollPanel.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

    tempGamePanel.setBackground(new Color(100, 100, 100));
    tempGamePanel.setFocusable(false);
    tempGamePanel.enableInputMethods(false);
    tempGameScrollPanel.getVerticalScrollBar().setUnitIncrement(16);
    tempGameScrollPanel.getVerticalScrollBar().setUI(new DemoScrollBarUI());

    tempGamePanelAll.add(tempGamePanelTop, new Integer(200));
    tempGamePanelAll.add(tempGameScrollPanel, new Integer(100));

    varTreePane =
        new JPanel(true) {
          @Override
          protected void paintComponent(Graphics g) {
            if (cachedVarImage2 != null && Lizzie.config.showVariationGraph) {
              if (Lizzie.config.isScaled) {
                Graphics2D g1 = (Graphics2D) g;
                g1.setRenderingHint(
                    RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                g1.setRenderingHint(KEY_ANTIALIASING, VALUE_ANTIALIAS_ON);
                g1.drawImage(cachedVarImage2, 0, 0, null);
              } else {
                g.drawImage(cachedVarImage2, 0, 0, null);
              }
            }
          }
        };
    // varTreePane.setBackground(Lizzie.config.varPanelColor);
    // varTreePane.setBackground(new Color(0, 0, 0, 0));
    varTreePane.setFocusable(false);
    toolbar.setFocusable(false);
    menu.setFocusable(false);
    varTreeScrollPane = new JScrollPane(varTreePane);
    varTreeScrollPane.setBorder(BorderFactory.createEmptyBorder());
    varTreeScrollPane.setFocusable(false);
    varTreeScrollPane.getHorizontalScrollBar().setFocusable(false);
    varTreeScrollPane.getVerticalScrollBar().setUI(new DemoScrollBarUI());
    varTreeScrollPane.getHorizontalScrollBar().setUI(new DemoScrollBarUI());
    varTreeScrollPane.getVerticalScrollBar().setUnitIncrement(16);
    varTreeScrollPane.getHorizontalScrollBar().setUnitIncrement(16);
    varTreeScrollPane.setVisible(Lizzie.config.showVariationGraph);
    // varTreeScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
    // varTreeScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
    commentEditTextPane = new JIMSendTextPane(true);
    commentEditTextPane.setBorder(BorderFactory.createEmptyBorder());
    commentEditTextPane.setBackground(Color.LIGHT_GRAY);
    commentEditTextPane.setForeground(Color.BLACK);
    commentEditPane = new JScrollPane(commentEditTextPane);
    commentEditPane.setBorder(BorderFactory.createEmptyBorder());
    commentEditPane.getVerticalScrollBar().setUI(new DemoScrollBarUI());
    //    commentEditTextPane.addMouseListener(
    //        new MouseAdapter() {
    //          public void mouseClicked(MouseEvent e) {
    //            LizzieFrame.this.mainPanel.requestFocus();
    //            LizzieFrame.this.commentEditPane.setVisible(false);
    //            if (Config.isScaled) LizzieFrame.this.repaint();
    //            String text = LizzieFrame.this.commentEditTextPane.getText();
    //            if (text.endsWith("\n")) text = text.substring(0, text.length() - 1);
    //            (Lizzie.board.getHistory().getCurrentHistoryNode().getData()).comment = text;
    //          }
    //        });
    commentEditPane.setVisible(false);
    varTreePane.addMouseListener(
        new MouseAdapter() {
          public void mouseClicked(MouseEvent e) {
            if (e.getButton() == MouseEvent.BUTTON3) // right click
            {
              if (isShowingRightMenu) return;
              if (RightClickMenu2 != null && RightClickMenu2.isVisible()) return;
              undoForRightClick();
            } else {
              if (!Lizzie.engineManager.isEngineGame) variationTree.onClicked(e.getX(), e.getY());
              renderVarTree(0, 0, false, false);
            }
            setCommentEditable(false);
          }
          //                    public void mouseEntered(MouseEvent e) {
          //                      mouseOnVarTree = true;
          //                    }
          //
          //                    public void mouseExited(MouseEvent e) {
          //                      mouseOnVarTree = false;
          //                    }
        });
    varTreePane.addMouseWheelListener(
        new MouseWheelListener() {
          @Override
          public void mouseWheelMoved(MouseWheelEvent e) {
            // TODO Auto-generated method stub
            if (e.getWheelRotation() > 0) {
              Input.redo();
            } else if (e.getWheelRotation() < 0) {
              Input.undo();
            }
          }
        });
    topPanel.setLayout(new ModifiedFlowLayout(FlowLayout.LEFT, 0, -2));
    topPanel.setFloatable(false);
    listDataModel = getTableModel();
    listTable = new JTable(listDataModel);
    TableCellRenderer tcr = new ColorTableCellRenderer();
    listTable.setDefaultRenderer(Object.class, tcr);
    listTable
        .getTableHeader()
        .setPreferredSize(
            new Dimension(
                listTable.getColumnModel().getTotalColumnWidth(),
                Lizzie.config.isFrameFontSmall()
                    ? 20
                    : (Lizzie.config.isFrameFontMiddle() ? 24 : 28)));

    listTable.getTableHeader().setFont(new Font("", Font.PLAIN, Config.frameFontSize));
    listTable.setRowHeight(Lizzie.config.menuHeight - 4);
    listTable.getTableHeader().setReorderingAllowed(false);
    listTable.setFont(new Font("", Font.PLAIN, Config.frameFontSize));
    DefaultTableCellRenderer cellRenderer = new DefaultTableCellRenderer();
    DefaultTableCellRenderer cellRenderer2 = new DefaultTableCellRenderer();
    cellRenderer.setBackground(new Color(208, 208, 208));
    cellRenderer2.setBackground(new Color(178, 178, 178));
    cellRenderer.setHorizontalAlignment(cellRenderer.CENTER);
    cellRenderer2.setHorizontalAlignment(cellRenderer.CENTER);
    /** 循环修改表头列 */
    for (int i = 0; i < listTable.getColumnCount(); i++) {
      TableColumn column = listTable.getTableHeader().getColumnModel().getColumn(i);
      if (i == 2 || i == 4) column.setHeaderRenderer(cellRenderer);
      else column.setHeaderRenderer(cellRenderer2);
    }
    listScrollpane = new JScrollPane(listTable);
    listScrollpane.getViewport().setBackground(new Color(243, 243, 243));
    varTreePane.addMouseMotionListener(
        new MouseAdapter() {
          public void mouseMoved(MouseEvent e) {
            if (!mainPanel.isFocusOwner()) mainPanel.requestFocus();
          }
        });
    listScrollpane.addMouseMotionListener(
        new MouseAdapter() {
          public void mouseMoved(MouseEvent e) {
            if (!mainPanel.isFocusOwner()) mainPanel.requestFocus();
          }
        });
    listScrollpane.addMouseListener(
        new MouseAdapter() {
          public void mouseClicked(MouseEvent e) {
            setCommentEditable(false);
          }
        });
    listScrollpane.setVerticalScrollBarPolicy(
        javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
    listScrollpane.getVerticalScrollBar().setUI(new DemoScrollBarUI2(false));
    listScrollpane.setBackground(new Color(235, 235, 235));
    hiddenColumn(1, listTable);
    listTable.getColumnModel().getColumn(0).setPreferredWidth(10);
    listTable.getColumnModel().getColumn(2).setPreferredWidth(30);
    listTable.getColumnModel().getColumn(3).setPreferredWidth(30);
    listTable.getColumnModel().getColumn(4).setPreferredWidth(45);
    listTable.getColumnModel().getColumn(5).setPreferredWidth(30);
    listTableColum5Width = 30;
    blunderTableColum0Width = 30;
    blunderTableColum2Width = 50;
    blunderTableColum3Width = 50;
    boolean persisted = Lizzie.config.persistedUi != null;
    if (persisted
        && Lizzie.config.persistedUi.optJSONArray("winrate-graph") != null
        && Lizzie.config.persistedUi.optJSONArray("winrate-graph").length() == 1) {
      JSONArray winrateG = Lizzie.config.persistedUi.getJSONArray("winrate-graph");
      winrateGraph.mode = winrateG.getInt(0);
    }
    if (persisted
        && Lizzie.config.persistedUi.optJSONArray("main-window-position") != null
        && Lizzie.config.persistedUi.optJSONArray("main-window-position").length() == 17) {
      JSONArray pos = Lizzie.config.persistedUi.getJSONArray("main-window-position");
      this.setBounds(pos.getInt(0), pos.getInt(1), pos.getInt(2), pos.getInt(3));
      Dimension screensize = Toolkit.getDefaultToolkit().getScreenSize();
      int width = (int) screensize.getWidth();
      int height = (int) screensize.getHeight();
      if (pos.getInt(0) >= width || pos.getInt(1) >= height) this.setLocation(0, 0);
      this.toolbarHeight = pos.getInt(4);
      if (toolbarHeight > 26 && !Lizzie.config.isChinese) toolbarHeight = 26;
      this.bowserX = pos.getInt(5);
      this.bowserY = pos.getInt(6);
      this.bowserWidth = pos.getInt(7);
      this.bowserHeight = pos.getInt(8);
      this.BoardPositionProportion =
          Lizzie.config.persistedUi.optInt("board-postion-propotion", this.BoardPositionProportion);

      listTable.getColumnModel().getColumn(0).setPreferredWidth(pos.getInt(9));
      listTable.getColumnModel().getColumn(2).setPreferredWidth(pos.getInt(10));
      listTable.getColumnModel().getColumn(3).setPreferredWidth(pos.getInt(11));
      listTable.getColumnModel().getColumn(4).setPreferredWidth(pos.getInt(12));
      listTableColum5Width = pos.getInt(13);
      listTable.getColumnModel().getColumn(5).setPreferredWidth(listTableColum5Width);

      blunderTableColum0Width = pos.getInt(14);
      blunderTableColum2Width = pos.getInt(15);
      blunderTableColum3Width = pos.getInt(16);

    } else {
      setSize(1065, 700);
      setLocationRelativeTo(null); // Start centered, needs to be called *after* setSize...
    }
    if (Lizzie.config.startMaximized && !persisted) {
      setExtendedState(Frame.MAXIMIZED_BOTH);

    } else if (persisted && Lizzie.config.persistedUi.getBoolean("window-maximized")) {
      setExtendedState(Frame.MAXIMIZED_BOTH);
      if (persisted
          && Lizzie.config.persistedUi.optJSONArray("main-window-position") != null
          && Lizzie.config.persistedUi.optJSONArray("main-window-position").length() == 13) {
        JSONArray pos = Lizzie.config.persistedUi.getJSONArray("main-window-position");
        this.toolbarHeight = pos.getInt(0);
        this.bowserX = pos.getInt(1);
        this.bowserY = pos.getInt(2);
        this.bowserWidth = pos.getInt(3);
        this.bowserHeight = pos.getInt(4);
        listTable.getColumnModel().getColumn(0).setPreferredWidth(pos.getInt(5));
        listTable.getColumnModel().getColumn(2).setPreferredWidth(pos.getInt(6));
        listTable.getColumnModel().getColumn(3).setPreferredWidth(pos.getInt(7));
        listTable.getColumnModel().getColumn(4).setPreferredWidth(pos.getInt(8));
        listTableColum5Width = pos.getInt(9);
        listTable.getColumnModel().getColumn(5).setPreferredWidth(listTableColum5Width);
        blunderTableColum0Width = pos.getInt(10);
        blunderTableColum2Width = pos.getInt(11);
        blunderTableColum3Width = pos.getInt(12);
      }
      this.BoardPositionProportion =
          Lizzie.config.persistedUi.optInt("board-postion-propotion", this.BoardPositionProportion);
    }

    listTable.addMouseWheelListener(
        new MouseWheelListener() {
          @Override
          public void mouseWheelMoved(MouseWheelEvent e) {
            // TODO Auto-generated method stub
            if (clickOrder != -1) {
              if (e.getWheelRotation() > 0) {
                doBranch(1);
              } else if (e.getWheelRotation() < 0) {
                doBranch(-1);
              }
              refresh();
            } else {
              listScrollpane.dispatchEvent(e);
            }
          }
        });

    listTable.addMouseListener(
        new MouseAdapter() {
          //         public void mouseExited(MouseEvent e) {
          //            if (clickOrder == -1) return;
          //            if (Lizzie.frame.suggestionclick != Lizzie.frame.outOfBoundCoordinate) {
          //              Lizzie.frame.boardRenderer.startNormalBoard();
          //              Lizzie.frame.boardRenderer.clearBranch();
          //              Lizzie.frame.suggestionclick = Lizzie.frame.outOfBoundCoordinate;
          //              Lizzie.frame.mouseOverCoordinate = Lizzie.frame.outOfBoundCoordinate;
          //              selectedorder = -1;
          //              currentRow = -1;
          //              Lizzie.frame.refresh();
          //            }
          //          }

          public void mouseClicked(MouseEvent e) {
            setCommentEditable(false);
            int row = listTable.rowAtPoint(e.getPoint());
            int col = listTable.columnAtPoint(e.getPoint());
            //            if (e.getClickCount() == 2) {
            //              if (row >= 0 && col >= 0) {
            //                try {
            //                  handleTableDoubleClick(row, col);
            //                } catch (Exception ex) {
            //                  ex.printStackTrace();
            //                }
            //              }
            //            } else {
            if (row >= 0 && col >= 0) {
              if (e.getButton() == MouseEvent.BUTTON3) {
                try {
                  handleTableRightClick(row, col);
                } catch (Exception ex) {
                  ex.printStackTrace();
                }
              } else
                try {
                  handleTableClick(row, col);
                } catch (Exception ex) {
                  ex.printStackTrace();
                }
            }
            //  }
          }
        });

    listTabletimer =
        new javax.swing.Timer(
            100,
            new ActionListener() {
              public void actionPerformed(ActionEvent evt) {
                //  listDataModel.getColumnCount();
                // listScrollpane.repaint();
                // table.validate();
                if (!Lizzie.board.getHistory().getData().bestMoves.isEmpty()) {
                  if (scoreColumnIsHidden && Lizzie.board.getHistory().getData().isKataData)
                    resumColumn(5, listTable, listTableColum5Width);
                  if (!scoreColumnIsHidden && !Lizzie.board.getHistory().getData().isKataData) {
                    listTableColum5Width = listTable.getColumnModel().getColumn(5).getWidth();
                    hiddenColumn(5, listTable);
                  }
                }
                listTable.revalidate();
              }
            });
    listTabletimer.start();

    blunderTableTimer =
        new javax.swing.Timer(
            150,
            new ActionListener() {
              public void actionPerformed(ActionEvent evt) {
                if (Lizzie.config.isShowingBlunderTabel) {
                  if (Lizzie.leelaz.isLoaded()) {
                    if (Lizzie.board.isKataBoard || Lizzie.leelaz.isKatago || Lizzie.leelaz.isSai) {
                      if (scoreIsHiddenInBlunderTable) {
                        resumColumn(3, blunderTabelBlack, blunderTableColum3Width);
                        resumColumn(3, blunderTabelWhite, blunderTableColum3Width);
                        scoreIsHiddenInBlunderTable = false;
                      }
                    } else {
                      if (!scoreIsHiddenInBlunderTable) {
                        blunderTableColum3Width =
                            blunderTabelBlack.getColumnModel().getColumn(3).getWidth();
                        hiddenColumn(3, blunderTabelBlack);
                        hiddenColumn(3, blunderTabelWhite);
                        scoreIsHiddenInBlunderTable = true;
                      }
                    }
                  }
                  // blunderContentPane.revalidate();
                  blunderTabelBlack.revalidate();
                  blunderTabelWhite.revalidate();
                }
              }
            });
    blunderTableTimer.start();

    getContentPane().setLayout(new BorderLayout());
    getContentPane().setBackground(Color.GRAY);

    setJMenuBar(menu);

    if (extraMode == 2) {
      boardRenderer2 = new BoardRenderer(false);
      boardRenderer2.setOrder(1);
    } else {
      Lizzie.frame.menu.setEngineMenuone2status(false);
    }
    mainPanel.setTransferHandler(
        new TransferHandler() {
          @Override
          public boolean importData(JComponent comp, Transferable t) {
            try {
              Object o = t.getTransferData(DataFlavor.javaFileListFlavor);
              String filepath = o.toString();
              if (filepath.startsWith("[")) {
                filepath = filepath.substring(1);
              }
              if (filepath.endsWith("]")) {
                filepath = filepath.substring(0, filepath.length() - 1);
              }
              String[] filePaths = filepath.split(", ");
              if (filePaths.length == 1) {
                //                if (!(filepath.toLowerCase().endsWith(".sgf")
                //                    || filepath.toLowerCase().endsWith(".gib"))) {
                //                  return false;
                //                }
                File file = new File(filepath);
                File files[] = new File[1];
                files[0] = file;
                loadFile(file, true, true);
                curFile = file;
                if (Lizzie.frame.analysisTable != null
                    && Lizzie.frame.analysisTable.frame.isVisible()) {
                  Lizzie.frame.analysisTable.refreshTable();
                }
                Lizzie.frame.refresh();
                return true;
              } else if (filePaths.length > 1) {
                File files[] = new File[filePaths.length];
                for (int i = 0; i < filePaths.length; i++) {
                  files[i] = new File(filePaths[i]);
                }
                isBatchAna = true;
                BatchAnaNum = 0;
                Batchfiles = new ArrayList<File>();
                for (int i = 0; i < files.length; i++) {
                  Batchfiles.add(files[i]);
                }
                loadFile(files[0], true, true);
                // 打开分析界面
                StartAnaDialog newgame = new StartAnaDialog(false);
                newgame.setVisible(true);
                if (newgame.isCancelled()) {
                  isBatchAna = false;
                  toolbar.resetAutoAna();
                  if (Lizzie.frame.analysisTable != null
                      && Lizzie.frame.analysisTable.frame.isVisible()) {
                    Lizzie.frame.analysisTable.refreshTable();
                  }
                  Lizzie.frame.refresh();
                  return true;
                }
              }
            } catch (Exception e) {
              e.printStackTrace();
            }
            return false;
          }

          @Override
          public boolean canImport(JComponent comp, DataFlavor[] flavors) {
            for (int i = 0; i < flavors.length; i++) {
              if (DataFlavor.javaFileListFlavor.equals(flavors[i])) {
                return true;
              }
            }
            return false;
          }
        });

    mainPanel.setFocusable(true);
    this.getJMenuBar().setBorder(new EmptyBorder(0, 0, 0, 0));
    if (this.toolbarHeight == 0) toolbar.setVisible(false);

    this.addComponentListener(
        new ComponentAdapter() {
          @Override
          public void componentMoved(ComponentEvent e) {
            if (Lizzie.config.isScaled) {
              Dimension screensize = Toolkit.getDefaultToolkit().getScreenSize();
              int width = (int) screensize.getWidth();
              int height = (int) screensize.getHeight();
              if ((getX() + getWidth()) >= width || (getY() + getHeight()) > height) repaint();
            }
          }
        });

    // Allow change font in the config
    if (Lizzie.config.uiFontName != null
        && !(Lizzie.config.uiFontName.equals("Lizzie默认")
            || Lizzie.config.uiFontName.equals("Lizzie Default"))) {
      uiFont = new Font(Lizzie.config.uiFontName, Font.PLAIN, 12);
    }
    playoutsFont = new Font(Lizzie.config.fontName, Font.PLAIN, 12);
    if (Lizzie.config.winrateFontName != null
        && !(Lizzie.config.winrateFontName.equals("Lizzie默认")
            || Lizzie.config.winrateFontName.equals("Lizzie Default"))) {
      winrateFont = new Font(Lizzie.config.winrateFontName, Font.BOLD, 12);
    }

    htmlKit = new HtmlKit();
    htmlDoc = (HTMLDocument) htmlKit.createDefaultDocument();
    htmlStyle = htmlKit.getStyleSheet();
    String style =
        "body {background:"
            + String.format(
                "%02x%02x%02x",
                Lizzie.config.commentBackgroundColor.getRed(),
                Lizzie.config.commentBackgroundColor.getGreen(),
                Lizzie.config.commentBackgroundColor.getBlue())
            + "; color:#"
            + String.format(
                "%02x%02x%02x",
                Lizzie.config.commentFontColor.getRed(),
                Lizzie.config.commentFontColor.getGreen(),
                Lizzie.config.commentFontColor.getBlue())
            + "; font-family:"
            + Lizzie.config.uiFontName
            + ", Consolas, Menlo, Monaco, 'Ubuntu Mono', monospace;"
            + (Lizzie.config.commentFontSize > 0
                ? Lizzie.config.commentFontSize
                : commentPaneFontSize > 0 ? commentPaneFontSize : Lizzie.config.frameFontSize)
            + "}";
    htmlStyle.addRule(style);
    commentTextPane = new JPaintTextPane();
    commentTextPane.setBorder(BorderFactory.createEmptyBorder());
    commentTextPane.setEditorKit(htmlKit);
    commentTextPane.setDocument(htmlDoc);
    commentTextPane.setEditable(false);
    commentTextPane.setBackground(Lizzie.config.commentBackgroundColor);
    commentTextPane.setForeground(Lizzie.config.commentFontColor);

    commentTextArea = new JPaintTextArea();
    DefaultCaret caret = (DefaultCaret) commentTextArea.getCaret();
    caret.setUpdatePolicy(DefaultCaret.NEVER_UPDATE);
    DefaultCaret caret2 = (DefaultCaret) commentTextPane.getCaret();
    caret2.setUpdatePolicy(DefaultCaret.NEVER_UPDATE);
    commentTextArea.setEditable(false);
    commentTextArea.setFont(
        new Font(
            Lizzie.config.uiFontName,
            Font.PLAIN,
            Lizzie.config.commentFontSize > 0
                ? Lizzie.config.commentFontSize
                : Lizzie.config.frameFontSize));
    commentTextArea.setBorder(BorderFactory.createEmptyBorder());
    commentTextArea.setBackground(Lizzie.config.commentBackgroundColor);
    commentTextArea.setForeground(Lizzie.config.commentFontColor);
    commentTextArea.setLineWrap(true);

    commentScrollPane = new JScrollPane();

    commentBlunderControlPane = new JPanel();
    commentBlunderControlPane.setBackground(Color.BLACK);
    commentBlunderControlPane.setVisible(false);
    commentBlunderControlPane.setLayout(null);

    blunderContentPane = new JPanel(new GridLayout(1, 2));
    blunderContentPane.setBackground(Color.GRAY);
    blunderContentPane.addMouseListener(
        new MouseAdapter() {
          public void mouseExited(MouseEvent e) {
            if (Lizzie.config.hideBlunderControlPane) {
              return;
            }
            commentBlunderControlPane.setVisible(false);
          }

          public void mouseEntered(MouseEvent e) {
            if (Lizzie.config.hideBlunderControlPane) {
              return;
            }
            setBlunderControlPane(false, true);
            commentBlunderControlPane.setVisible(true);
          }
        });

    setBlunderSort();
    blunderModelBlack = getBlunderModel(true);
    blunderModelWhite = getBlunderModel(false);
    blunderTabelBlack = new JTable(blunderModelBlack);
    blunderTabelWhite = new JTable(blunderModelWhite);

    hiddenColumn(1, blunderTabelBlack);
    hiddenColumn(1, blunderTabelWhite);

    JFontPopupMenu exportBlunderBlack = new JFontPopupMenu();
    final JMenuItem exportMenuBlunderBlack =
        new JFontMenuItem(resourceBundle.getString("JTabel.export"));
    exportBlunderBlack.add(exportMenuBlunderBlack);
    exportMenuBlunderBlack.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            try {
              JFileChooser chooser = new JFileChooser();
              FileNameExtensionFilter filter = new FileNameExtensionFilter("(*.xls)", "xls");
              chooser.setFileFilter(filter);
              int option = chooser.showSaveDialog(Lizzie.frame);
              if (option == JFileChooser.APPROVE_OPTION) {
                File file = chooser.getSelectedFile();
                String fname = chooser.getName(file);
                if (fname.indexOf(".xlsx") == -1) {
                  Utils.exportTable(
                      blunderTabelBlack,
                      chooser.getCurrentDirectory() + Utils.pwd + fname + ".xls");
                }
              }

            } catch (IOException e1) {
              // TODO Auto-generated catch block
              e1.printStackTrace();
            }
          }
        });

    blunderTabelBlack.addMouseListener(
        new MouseAdapter() {
          public void mouseClicked(MouseEvent e) {
            if (e.getButton() == MouseEvent.BUTTON3) {
              exportBlunderBlack.show(blunderTabelBlack, e.getX(), e.getY());
              return;
            }
            int row = blunderTabelBlack.rowAtPoint(e.getPoint());
            int col = blunderTabelBlack.columnAtPoint(e.getPoint());
            if (row >= 0 && col >= 0) {
              try {
                blunderTabelBlack.repaint();
                int movenumber = Integer.parseInt(blunderTabelBlack.getValueAt(row, 0).toString());
                // Lizzie.board.goToMoveNumber(1);
                int[] coords =
                    Lizzie.board.convertNameToCoordinates(
                        blunderTabelBlack.getValueAt(row, 1).toString());
                int moveNumber = Lizzie.board.moveNumberByCoord(coords);
                Lizzie.board.goToMoveNumber(movenumber - 1);
                Lizzie.frame.clickbadmove = coords;
                Lizzie.frame.repaint();
              } catch (Exception ex) {
                ex.printStackTrace();
              }
            }
          }
        });

    JFontPopupMenu exportBlunderWhite = new JFontPopupMenu();
    final JMenuItem exportMenuBlunderWhite =
        new JFontMenuItem(resourceBundle.getString("JTabel.export"));
    exportBlunderWhite.add(exportMenuBlunderWhite);
    exportMenuBlunderWhite.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            try {
              JFileChooser chooser = new JFileChooser();
              FileNameExtensionFilter filter = new FileNameExtensionFilter("(*.xls)", "xls");
              chooser.setFileFilter(filter);
              int option = chooser.showSaveDialog(Lizzie.frame);
              if (option == JFileChooser.APPROVE_OPTION) {
                File file = chooser.getSelectedFile();
                String fname = chooser.getName(file);
                if (fname.indexOf(".xlsx") == -1) {
                  Utils.exportTable(
                      blunderTabelWhite,
                      chooser.getCurrentDirectory() + Utils.pwd + fname + ".xls");
                }
              }

            } catch (IOException e1) {
              // TODO Auto-generated catch block
              e1.printStackTrace();
            }
          }
        });

    blunderTabelWhite.addMouseListener(
        new MouseAdapter() {
          public void mouseClicked(MouseEvent e) {
            if (e.getButton() == MouseEvent.BUTTON3) {
              exportBlunderWhite.show(blunderTabelWhite, e.getX(), e.getY());
              return;
            }
            int row = blunderTabelWhite.rowAtPoint(e.getPoint());
            int col = blunderTabelWhite.columnAtPoint(e.getPoint());
            if (row >= 0 && col >= 0) {
              try {
                blunderTabelWhite.repaint();
                int movenumber = Integer.parseInt(blunderTabelWhite.getValueAt(row, 0).toString());
                // Lizzie.board.goToMoveNumber(1);
                int[] coords =
                    Lizzie.board.convertNameToCoordinates(
                        blunderTabelWhite.getValueAt(row, 1).toString());
                int moveNumber = Lizzie.board.moveNumberByCoord(coords);
                Lizzie.board.goToMoveNumber(movenumber - 1);
                Lizzie.frame.clickbadmove = coords;
                Lizzie.frame.repaint();
              } catch (Exception ex) {
                ex.printStackTrace();
              }
            }
          }
        });

    blunderTabelBlack
        .getTableHeader()
        .addMouseListener(
            new MouseAdapter() {
              public void mouseReleased(MouseEvent e) {
                int pick = blunderTabelBlack.getTableHeader().columnAtPoint(e.getPoint());
                if (pick == blunderSortNum) {
                  if (blunderSortNum == 2 || blunderSortNum == 3) {
                    if (blunderSortIsOriginOrder) {
                      blunderSortIsOriginOrder = false;
                      blunderIsSorted = false;
                    } else if (!blunderIsSorted) blunderIsSorted = true;
                    else {
                      blunderSortIsOriginOrder = true;
                      blunderIsSorted = false;
                    }
                  } else {
                    blunderIsSorted = !blunderIsSorted;
                  }
                } else {
                  blunderSortNum = pick;
                  blunderSortIsOriginOrder = true;
                  blunderIsSorted = false;
                }
                Lizzie.config.saveBlunderTableSortSettings(
                    blunderSortNum, blunderIsSorted, blunderSortIsOriginOrder);
                blunderTabelBlack.repaint();
                blunderTabelWhite.repaint();
              }
            });
    blunderTabelBlack
        .getTableHeader()
        .addMouseListener(
            new MouseAdapter() {
              public void mouseExited(MouseEvent e) {
                if (Lizzie.config.hideBlunderControlPane) {
                  return;
                }
                commentBlunderControlPane.setVisible(false);
              }

              public void mouseEntered(MouseEvent e) {
                if (Lizzie.config.hideBlunderControlPane) {
                  return;
                }
                setBlunderControlPane(false, true);
                commentBlunderControlPane.setVisible(true);
              }
            });
    blunderTabelBlack.addMouseListener(
        new MouseAdapter() {
          public void mouseExited(MouseEvent e) {
            if (Lizzie.config.hideBlunderControlPane) {
              return;
            }
            commentBlunderControlPane.setVisible(false);
          }

          public void mouseEntered(MouseEvent e) {
            if (Lizzie.config.hideBlunderControlPane) {
              return;
            }
            setBlunderControlPane(false, true);
            commentBlunderControlPane.setVisible(true);
          }
        });

    blunderTabelWhite
        .getTableHeader()
        .addMouseListener(
            new MouseAdapter() {
              public void mouseReleased(MouseEvent e) {
                int pick = blunderTabelWhite.getTableHeader().columnAtPoint(e.getPoint());
                if (pick == blunderSortNum) {
                  if (blunderSortNum == 2 || blunderSortNum == 3) {
                    if (blunderSortIsOriginOrder) {
                      blunderSortIsOriginOrder = false;
                      blunderIsSorted = false;
                    } else if (!blunderIsSorted) blunderIsSorted = true;
                    else {
                      blunderSortIsOriginOrder = true;
                      blunderIsSorted = false;
                    }
                  } else {
                    blunderIsSorted = !blunderIsSorted;
                  }
                } else {
                  blunderSortNum = pick;
                  blunderSortIsOriginOrder = true;
                  blunderIsSorted = false;
                }
                Lizzie.config.saveBlunderTableSortSettings(
                    blunderSortNum, blunderIsSorted, blunderSortIsOriginOrder);
                blunderTabelWhite.repaint();
                blunderTabelBlack.repaint();
              }
            });
    blunderTabelWhite
        .getTableHeader()
        .addMouseListener(
            new MouseAdapter() {
              public void mouseExited(MouseEvent e) {
                if (Lizzie.config.hideBlunderControlPane) {
                  return;
                }
                commentBlunderControlPane.setVisible(false);
              }

              public void mouse(MouseEvent e) {
                if (Lizzie.config.hideBlunderControlPane) {
                  return;
                }
                setBlunderControlPane(false, true);
                commentBlunderControlPane.setVisible(true);
              }
            });
    blunderTabelWhite.addMouseListener(
        new MouseAdapter() {
          public void mouseExited(MouseEvent e) {
            if (Lizzie.config.hideBlunderControlPane) {
              return;
            }
            commentBlunderControlPane.setVisible(false);
          }

          public void mouseEntered(MouseEvent e) {
            if (Lizzie.config.hideBlunderControlPane) {
              return;
            }
            setBlunderControlPane(false, true);
            commentBlunderControlPane.setVisible(true);
          }
        });

    setTabelStyle(
        blunderTabelBlack,
        blunderTableColum0Width,
        blunderTableColum2Width,
        blunderTableColum3Width);
    setTabelStyle(
        blunderTabelWhite,
        blunderTableColum0Width,
        blunderTableColum2Width,
        blunderTableColum3Width);

    minScrollpaneBlack = new JScrollPane(blunderTabelBlack);
    minScrollpaneWhite = new JScrollPane(blunderTabelWhite);
    minScrollpaneBlack.getViewport().setBackground(blunderBackground);
    minScrollpaneWhite.getViewport().setBackground(blunderBackground);
    minScrollpaneBlack.addMouseListener(
        new MouseAdapter() {
          public void mouseExited(MouseEvent e) {
            if (Lizzie.config.hideBlunderControlPane) {
              return;
            }
            commentBlunderControlPane.setVisible(false);
          }

          public void mouseEntered(MouseEvent e) {
            if (Lizzie.config.hideBlunderControlPane) {
              return;
            }
            setBlunderControlPane(false, true);
            commentBlunderControlPane.setVisible(true);
          }
        });
    minScrollpaneWhite.addMouseListener(
        new MouseAdapter() {
          public void mouseExited(MouseEvent e) {
            if (Lizzie.config.hideBlunderControlPane) {
              return;
            }
            commentBlunderControlPane.setVisible(false);
          }

          public void mouseEntered(MouseEvent e) {
            if (Lizzie.config.hideBlunderControlPane) {
              return;
            }
            setBlunderControlPane(false, true);
            commentBlunderControlPane.setVisible(true);
          }
        });
    tablePanelMinBlack = new JPanel(new BorderLayout());
    tablePanelMinWhite = new JPanel(new BorderLayout());
    tablePanelMinBlack.add(minScrollpaneBlack);
    tablePanelMinWhite.add(minScrollpaneWhite);
    blunderContentPane.add(tablePanelMinBlack);
    blunderContentPane.add(tablePanelMinWhite);
    minScrollpaneBlack.setBackground(new Color(158, 158, 158));
    minScrollpaneBlack.getVerticalScrollBar().setUI(new DemoScrollBarUI2(true));
    //    minScrollpaneBlack.setVerticalScrollBarPolicy(
    //        javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
    minScrollpaneWhite.setBackground(new Color(158, 158, 158));
    minScrollpaneWhite.getVerticalScrollBar().setUI(new DemoScrollBarUI2(true));
    //    minScrollpaneWhite.setVerticalScrollBarPolicy(
    //        javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
    minScrollpaneBlack
        .getVerticalScrollBar()
        .addMouseListener(
            new MouseAdapter() {
              public void mouseExited(MouseEvent e) {
                if (Lizzie.config.hideBlunderControlPane) {
                  return;
                }
                commentBlunderControlPane.setVisible(false);
              }

              public void mouseEntered(MouseEvent e) {
                if (Lizzie.config.hideBlunderControlPane) {
                  return;
                }
                setBlunderControlPane(false, true);
                commentBlunderControlPane.setVisible(true);
              }
            });
    minScrollpaneWhite
        .getVerticalScrollBar()
        .addMouseListener(
            new MouseAdapter() {
              public void mouseExited(MouseEvent e) {
                if (Lizzie.config.hideBlunderControlPane) {
                  return;
                }
                commentBlunderControlPane.setVisible(false);
              }

              public void mouseEntered(MouseEvent e) {
                if (Lizzie.config.hideBlunderControlPane) {
                  return;
                }
                setBlunderControlPane(false, true);
                commentBlunderControlPane.setVisible(true);
              }
            });
    commentBlunderControlPane.addMouseListener(
        new MouseAdapter() {
          public void mouseExited(MouseEvent e) {
            commentBlunderControlPane.setVisible(false);
          }

          public void mouseEntered(MouseEvent e) {
            commentBlunderControlPane.setVisible(true);
          }
        });

    commentTextArea.addMouseListener(
        new MouseAdapter() {
          public void mouseExited(MouseEvent e) {
            if (Lizzie.config.hideBlunderControlPane) {
              return;
            }
            commentBlunderControlPane.setVisible(false);
          }

          public void mouseEntered(MouseEvent e) {
            if (Lizzie.config.hideBlunderControlPane) {
              return;
            }
            setBlunderControlPane(true, true);
            commentBlunderControlPane.setVisible(true);
          }
        });

    commentTextPane.addMouseListener(
        new MouseAdapter() {
          public void mouseExited(MouseEvent e) {
            if (Lizzie.config.hideBlunderControlPane) {
              return;
            }
            commentBlunderControlPane.setVisible(false);
          }

          public void mouseEntered(MouseEvent e) {
            if (Lizzie.config.hideBlunderControlPane) {
              return;
            }
            setBlunderControlPane(true, true);
            commentBlunderControlPane.setVisible(true);
          }
        });

    commentScrollPane.setBorder(BorderFactory.createEmptyBorder());
    commentScrollPane.setBackground(Lizzie.config.commentBackgroundColor);
    commentScrollPane.setViewportView(commentTextArea);
    commentScrollPane.setVerticalScrollBarPolicy(
        javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
    commentScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
    commentScrollPane.getVerticalScrollBar().setUI(new DemoScrollBarUI());
    //  commentRect = new Rectangle(0, 0, 0, 0);
    commentTextArea.addMouseListener(
        new MouseAdapter() {
          public void mouseClicked(MouseEvent e) {
            if (e.getButton() == MouseEvent.BUTTON1) // right click
            {
              setCommentEditable(true);
            }
          }
        });

    commentTextPane.addMouseListener(
        new MouseAdapter() {
          public void mouseClicked(MouseEvent e) {
            if (e.getButton() == MouseEvent.BUTTON1) // right click
            {
              setCommentEditable(true);
            }
          }
        });
    try {
      this.setIconImage(ImageIO.read(getClass().getResourceAsStream("/assets/logo.png")));
    } catch (IOException e) {
      e.printStackTrace();
    }

    autoIntervalCom = Lizzie.config.analyzeUpdateIntervalCentisec * 5;
    this.addWindowListener(
        new WindowAdapter() {
          public void windowClosing(WindowEvent e) {
            Lizzie.shutdown();
          }
        });

    // Show the playouts in the title
    showPlayouts.scheduleAtFixedRate(
        new Runnable() {
          @Override
          public void run() {
            if (!isDrawVisitsInTitle) {
              visitsString = "";
              return;
            }
            if (Lizzie.leelaz == null
                || Lizzie.engineManager.isEmpty
                || !Lizzie.leelaz.isPondering()) return;
            try {
              int totalPlayouts =
                  Lizzie.board.getHistory().getCurrentHistoryNode().getData().getPlayouts();
              int tempCount = getLastVisitsCount(visitsCount);
              if (tempCount >= 0) {
                long speed = (totalPlayouts - lastPlayouts) / tempCount;
                if (speed >= 0) {
                  visitsString = String.format(" %d v/s", speed);
                  // updateTitle();
                }
              }
              // if(visitsString.equals(""))
              //  visitsString = " - v/s";
              // else {
              // visitsString = " - v/s";
              // }
              visitsCount++;
              if (visitsCount > 3) visitsCount = 0;
              if (totalPlayouts > 0) {
                visitsTemp[visitsCount].node = Lizzie.board.getHistory().getCurrentHistoryNode();
                visitsTemp[visitsCount].Playouts = totalPlayouts;
              }
            } catch (Exception e) {
              e.printStackTrace();
            }
          }
        },
        1,
        1,
        TimeUnit.SECONDS);

    updateTitleSchedual.scheduleAtFixedRate(
        new Runnable() {
          @Override
          public void run() {
            updateTitle();
          }
        },
        1000,
        300,
        TimeUnit.MILLISECONDS);
    mainPanel.addMouseMotionListener(input);
    toolbar.addMouseWheelListener(input);
    addInput(false);
    getContentPane().add(basePanel);
    basePanel.add(commentBlunderControlPane, new Integer(900));

    basePanel.add(tempGamePanelAll, new Integer(800));
    basePanel.add(varTreeScrollPane, new Integer(700));
    basePanel.add(listScrollpane, new Integer(600));
    basePanel.add(blunderContentPane, new Integer(550));
    basePanel.add(commentEditPane, new Integer(500));
    basePanel.add(commentScrollPane, new Integer(400));
    basePanel.add(topPanel, new Integer(300));
    basePanel.add(toolbar, new Integer(200));
    basePanel.add(mainPanel, new Integer(100));
    mainPanel.setVisible(false);
    commentScrollPane.setVisible(false);
    blunderContentPane.setVisible(false);
    setVisible(true);
  }

  private void setBlunderSort() {
    // TODO Auto-generated method stub
    if (Lizzie.config.blunderTabelOnlyAfter) {
      blunderSortNum = Lizzie.config.blunderSortNumAF;
      blunderIsSorted = Lizzie.config.blunderIsSortedAF;
      blunderSortIsOriginOrder = Lizzie.config.blunderSortIsOriginOrderAF;
    } else {
      blunderSortNum = Lizzie.config.blunderSortNumNAF;
      blunderIsSorted = Lizzie.config.blunderIsSortedNAF;
      blunderSortIsOriginOrder = Lizzie.config.blunderSortIsOriginOrderNAF;
    }
  }

  private AbstractTableModel getBlunderModel(boolean isBlack) {
    return new AbstractTableModel() {
      public int getColumnCount() {
        return 4;
      }

      public int getRowCount() {
        int row = 0;
        BoardHistoryNode lastNode = Lizzie.board.getHistory().getEnd();
        while (!Lizzie.config.blunderTabelOnlyAfter && lastNode.previous().isPresent()
            || (Lizzie.config.blunderTabelOnlyAfter
                && lastNode != Lizzie.board.getHistory().getCurrentHistoryNode()
                && lastNode.previous().isPresent())) {
          NodeInfo nodeInfoThis = lastNode.nodeInfo;
          if (nodeInfoThis.analyzed)
            if (nodeInfoThis.isBlack == isBlack)
              if (Math.abs(nodeInfoThis.diffWinrate) >= Lizzie.config.blunderWinThreshold)
                if (nodeInfoThis.playouts >= Lizzie.config.blunderPlayoutsThreshold
                    && nodeInfoThis.previousPlayouts >= Lizzie.config.blunderPlayoutsThreshold)
                  if (!lastNode.getData().isKataData
                      || Math.abs(nodeInfoThis.scoreMeanDiff)
                          >= Lizzie.config.blunderScoreThreshold) row = row + 1;
          lastNode = lastNode.previous().get();
        }
        NodeInfo nodeInfoThis = lastNode.nodeInfo;
        if (nodeInfoThis.analyzed)
          if (nodeInfoThis.isBlack == isBlack)
            if (Math.abs(nodeInfoThis.diffWinrate) >= Lizzie.config.blunderWinThreshold)
              if (nodeInfoThis.playouts >= Lizzie.config.blunderPlayoutsThreshold
                  && nodeInfoThis.previousPlayouts >= Lizzie.config.blunderPlayoutsThreshold)
                if (!lastNode.getData().isKataData
                    || Math.abs(nodeInfoThis.scoreMeanDiff) >= Lizzie.config.blunderScoreThreshold)
                  row = row + 1;
        return row;
      }

      public String getColumnName(int column) {
        switch (column) {
          case 0:
            return isBlack
                ? resourceBundle.getString("BlunderTabel.black")
                : resourceBundle.getString("BlunderTabel.white");
          case 1:
            return resourceBundle.getString("BlunderTabel.coords");
          case 2:
            return resourceBundle.getString("BlunderTabel.winRate");
          case 3:
            return resourceBundle.getString("BlunderTabel.score");
        }
        return "";
      }

      public Object getValueAt(int row, int col) {
        ArrayList<NodeInfo> data2 = new ArrayList<NodeInfo>();
        BoardHistoryNode lastNode = Lizzie.board.getHistory().getEnd();
        while (!Lizzie.config.blunderTabelOnlyAfter && lastNode.previous().isPresent()
            || (Lizzie.config.blunderTabelOnlyAfter
                && lastNode != Lizzie.board.getHistory().getCurrentHistoryNode()
                && lastNode.previous().isPresent())) {
          NodeInfo nodeInfoThis = lastNode.nodeInfo;
          if (nodeInfoThis.analyzed)
            if (nodeInfoThis.isBlack == isBlack)
              if (Math.abs(nodeInfoThis.diffWinrate) >= Lizzie.config.blunderWinThreshold)
                if (nodeInfoThis.playouts >= Lizzie.config.blunderPlayoutsThreshold
                    && nodeInfoThis.previousPlayouts >= Lizzie.config.blunderPlayoutsThreshold)
                  if (!lastNode.getData().isKataData
                      || Math.abs(nodeInfoThis.scoreMeanDiff)
                          >= Lizzie.config.blunderScoreThreshold) data2.add(nodeInfoThis);
          lastNode = lastNode.previous().get();
        }
        NodeInfo nodeInfoThis = lastNode.nodeInfo;
        if (nodeInfoThis.analyzed)
          if (nodeInfoThis.isBlack == isBlack)
            if (Math.abs(nodeInfoThis.diffWinrate) >= Lizzie.config.blunderWinThreshold)
              if (nodeInfoThis.playouts >= Lizzie.config.blunderPlayoutsThreshold
                  && nodeInfoThis.previousPlayouts >= Lizzie.config.blunderPlayoutsThreshold)
                if (!lastNode.getData().isKataData
                    || Math.abs(nodeInfoThis.scoreMeanDiff) >= Lizzie.config.blunderScoreThreshold)
                  data2.add(nodeInfoThis);
        Collections.sort(
            data2,
            new Comparator<NodeInfo>() {
              @Override
              public int compare(NodeInfo s1, NodeInfo s2) {
                // 降序
                if (!blunderIsSorted) {
                  if (blunderSortNum == 0) {
                    if (s1.moveNum > s2.moveNum) return 1;
                    if (s1.moveNum < s2.moveNum) return -1;
                  }
                  if (blunderSortNum == 2) {
                    if (blunderSortIsOriginOrder) {
                      if (Math.abs(s1.diffWinrate) < Math.abs(s2.diffWinrate)) return 1;
                      if (Math.abs(s1.diffWinrate) > Math.abs(s2.diffWinrate)) return -1;
                    } else {
                      if (s1.diffWinrate < s2.diffWinrate) return 1;
                      if (s1.diffWinrate > s2.diffWinrate) return -1;
                    }
                  }
                  if (blunderSortNum == 3) {
                    if (blunderSortIsOriginOrder) {
                      if (Math.abs(s1.scoreMeanDiff) < Math.abs(s2.scoreMeanDiff)) return 1;
                      if (Math.abs(s1.scoreMeanDiff) > Math.abs(s2.scoreMeanDiff)) return -1;
                    } else {
                      if (s1.scoreMeanDiff < s2.scoreMeanDiff) return 1;
                      if (s1.scoreMeanDiff > s2.scoreMeanDiff) return -1;
                    }
                  }
                } else {
                  if (blunderSortNum == 0) {
                    if (s1.moveNum > s2.moveNum) return -1;
                    if (s1.moveNum < s2.moveNum) return 1;
                  }
                  if (blunderSortNum == 2) {
                    if (blunderSortIsOriginOrder) {
                      if (Math.abs(s1.diffWinrate) < Math.abs(s2.diffWinrate)) return -1;
                      if (Math.abs(s1.diffWinrate) > Math.abs(s2.diffWinrate)) return 1;
                    } else {
                      if (s1.diffWinrate < s2.diffWinrate) return -1;
                      if (s1.diffWinrate > s2.diffWinrate) return 1;
                    }
                  }
                  if (blunderSortNum == 3) {
                    if (blunderSortIsOriginOrder) {
                      if (Math.abs(s1.scoreMeanDiff) < Math.abs(s2.scoreMeanDiff)) return -1;
                      if (Math.abs(s1.scoreMeanDiff) > Math.abs(s2.scoreMeanDiff)) return 1;
                    } else {
                      if (s1.scoreMeanDiff < s2.scoreMeanDiff) return -1;
                      if (s1.scoreMeanDiff > s2.scoreMeanDiff) return 1;
                    }
                  }
                }
                return 0;
              }
            });

        NodeInfo data = data2.get(row);
        if (Lizzie.board.isPkBoard) {
          switch (col) {
            case 0:
              return data.moveNum;
            case 1:
              return Board.convertCoordinatesToName(data.coords[0], data.coords[1]);
            case 2:
              return (data.diffWinrate < 0 ? "+" : "-")
                  + String.format("%.2f", Math.abs(data.diffWinrate));
            case 3:
              return (data.scoreMeanDiff < 0 ? "+" : "-")
                  + String.format("%.2f", Math.abs(data.scoreMeanDiff));
            default:
              return "";
          }
        } else {
          switch (col) {
            case 0:
              return data.moveNum;
            case 1:
              return Board.convertCoordinatesToName(data.coords[0], data.coords[1]);
            case 2:
              return (data.diffWinrate > 0 ? "+" : "-")
                  + String.format("%.2f", Math.abs(data.diffWinrate));

            case 3:
              return (data.scoreMeanDiff > 0 ? "+" : "-")
                  + String.format("%.2f", Math.abs(data.scoreMeanDiff));
            default:
              return "";
          }
        }
      }
    };
  }

  private void setTabelStyle(JTable table, int column0, int column1, int column2) {
    // TODO Auto-generated method stub

    table.getColumnModel().getColumn(0).setPreferredWidth(column0);
    table.getColumnModel().getColumn(2).setPreferredWidth(column1);
    table.getColumnModel().getColumn(3).setPreferredWidth(column2);
    table
        .getTableHeader()
        .setPreferredSize(
            new Dimension(
                table.getColumnModel().getTotalColumnWidth(),
                Lizzie.config.isFrameFontSmall()
                    ? 20
                    : (Lizzie.config.isFrameFontMiddle() ? 24 : 28)));
    table.getTableHeader().setFont(new Font("", Font.PLAIN, Config.frameFontSize));
    table.setRowHeight(Lizzie.config.menuHeight - 4);
    table.setFont(new Font("", Font.PLAIN, Config.frameFontSize));

    //    table.getTableHeader().setBackground(new Color(51, 102, 255));
    ////    ((DefaultTableCellRenderer) table.getTableHeader().getDefaultRenderer())
    ////        .setHorizontalAlignment(JLabel.CENTER);

    DefaultTableCellRenderer cellRenderer = new DefaultTableCellRenderer();
    cellRenderer.setBackground(Color.LIGHT_GRAY);
    DefaultTableCellRenderer cellRenderer2 = new DefaultTableCellRenderer();
    cellRenderer2.setBackground(new Color(158, 158, 158));
    cellRenderer.setHorizontalAlignment(cellRenderer.CENTER);
    cellRenderer2.setHorizontalAlignment(cellRenderer.CENTER);
    /** 循环修改表头列 */
    for (int i = 0; i < table.getColumnCount(); i++) {
      TableColumn column = table.getTableHeader().getColumnModel().getColumn(i);
      if (i == 2) column.setHeaderRenderer(cellRenderer);
      else column.setHeaderRenderer(cellRenderer2);
    }

    DefaultTableCellRenderer tcr = new BlunderTableCellRenderer();
    table.setDefaultRenderer(Object.class, tcr);
    table.getTableHeader().setReorderingAllowed(false);
    tcr.setHorizontalAlignment(JLabel.CENTER);
  }

  public void setBlunderControlPane(boolean fromComment, boolean resetPos) {
    if (Lizzie.config.hideBlunderControlPane) {
      commentBlunderControlPane.setBounds(0, 0, 0, 0);
      commentBlunderControlPane.setVisible(false);
      return;
    }
    commentBlunderControlPane.removeAll();
    JCheckBox chkComment = new JCheckBox(resourceBundle.getString("LizzieFrame.chkComment"));
    chkComment.setSelected(!Lizzie.config.isShowingBlunderTabel);
    JCheckBox chkBlunder = new JCheckBox(resourceBundle.getString("LizzieFrame.chkBlunder"));
    chkBlunder.setSelected(Lizzie.config.isShowingBlunderTabel);
    ButtonGroup group = new ButtonGroup();
    group.add(chkComment);
    group.add(chkBlunder);
    chkComment.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            // TBD
            menu.txtKomi.setFocusable(false);
            Lizzie.config.isShowingBlunderTabel = !chkComment.isSelected();
            Lizzie.config.uiConfig.put(
                "is-showing-blunder-table", Lizzie.config.isShowingBlunderTabel);
            setBlunderControlPane(!Lizzie.config.isShowingBlunderTabel, false);
            setCommentPaneContent();
            repaint();
            menu.txtKomi.setFocusable(true);
          }
        });
    chkBlunder.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            // TBD
            menu.txtKomi.setFocusable(false);
            Lizzie.config.isShowingBlunderTabel = chkBlunder.isSelected();
            Lizzie.config.uiConfig.put(
                "is-showing-blunder-table", Lizzie.config.isShowingBlunderTabel);
            setBlunderControlPane(!Lizzie.config.isShowingBlunderTabel, false);
            setCommentPaneContent();
            repaint();
            menu.txtKomi.setFocusable(true);
          }
        });
    if (Lizzie.config.isChinese) chkComment.setBounds(0, 0, 50, 20);
    else chkComment.setBounds(0, 0, 80, 20);
    chkComment.setBackground(Color.BLACK);
    chkComment.setForeground(Color.WHITE);
    if (Lizzie.config.isChinese) chkBlunder.setBounds(50, 0, 50, 20);
    else chkBlunder.setBounds(80, 0, 70, 20);
    chkBlunder.setBackground(Color.BLACK);
    chkBlunder.setForeground(Color.WHITE);
    chkBlunder.addMouseListener(
        new MouseAdapter() {
          public void mouseExited(MouseEvent e) {
            commentBlunderControlPane.setVisible(false);
          }

          public void mouseEntered(MouseEvent e) {
            commentBlunderControlPane.setVisible(true);
          }
        });
    chkComment.addMouseListener(
        new MouseAdapter() {
          public void mouseExited(MouseEvent e) {
            commentBlunderControlPane.setVisible(false);
          }

          public void mouseEntered(MouseEvent e) {
            commentBlunderControlPane.setVisible(true);
          }
        });
    commentBlunderControlPane.add(chkComment);
    commentBlunderControlPane.add(chkBlunder);
    if (!fromComment) {
      JCheckBox chkOnlyAfter = new JCheckBox(resourceBundle.getString("LizzieFrame.chkOnlyAfter"));
      chkOnlyAfter.setSelected(Lizzie.config.blunderTabelOnlyAfter);
      chkOnlyAfter.addActionListener(
          new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
              // TBD未完成
              Lizzie.config.blunderTabelOnlyAfter = chkOnlyAfter.isSelected();
              Lizzie.config.uiConfig.put(
                  "blunder-table-only-after", Lizzie.config.blunderTabelOnlyAfter);
              setBlunderSort();
              blunderTabelBlack.revalidate();
              blunderTabelWhite.revalidate();
            }
          });
      JButton setThreshold = new JButton(resourceBundle.getString("LizzieFrame.setThreshold"));
      setThreshold.addActionListener(
          new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
              SetThreshold setThreshold = new SetThreshold(Lizzie.frame, false);
              setThreshold.setVisible(true);
            }
          });
      setThreshold.setFocusPainted(false);
      setThreshold.setContentAreaFilled(false);
      setThreshold.setMargin(new Insets(0, 0, 0, 0));
      chkOnlyAfter.addMouseListener(
          new MouseAdapter() {
            public void mouseExited(MouseEvent e) {
              commentBlunderControlPane.setVisible(false);
            }

            public void mouseEntered(MouseEvent e) {
              commentBlunderControlPane.setVisible(true);
            }
          });
      setThreshold.addMouseListener(
          new MouseAdapter() {
            public void mouseExited(MouseEvent e) {
              commentBlunderControlPane.setVisible(false);
            }

            public void mouseEntered(MouseEvent e) {
              commentBlunderControlPane.setVisible(true);
            }
          });
      if (Lizzie.config.isChinese) {
        chkOnlyAfter.setBounds(100, 0, 65, 20);
        setThreshold.setBounds(154, 0, 50, 20);
      } else {
        chkOnlyAfter.setBounds(150, 0, 50, 20);
        setThreshold.setBounds(200, 0, 60, 20);
      }
      chkOnlyAfter.setBackground(Color.BLACK);
      chkOnlyAfter.setForeground(Color.WHITE);
      setThreshold.setBackground(Color.BLACK);
      setThreshold.setForeground(Color.WHITE);
      commentBlunderControlPane.add(chkOnlyAfter);
      commentBlunderControlPane.add(setThreshold);
    }

    JButton close = new JButton(resourceBundle.getString("LizzieFrame.close"));
    close.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            // TBD未完成
            SwingUtilities.invokeLater(
                new Runnable() {
                  public void run() {
                    if (Lizzie.config.allowCloseCommentControlHint) {
                      Object[] options = {
                        resourceBundle.getString("LizzieFrame.confirm"),
                        resourceBundle.getString("LizzieFrame.cancel"),
                        resourceBundle.getString("LizzieFrame.noNoticeAgain")
                      };
                      int response =
                          JOptionPane.showOptionDialog(
                              Lizzie.frame,
                              resourceBundle.getString("LizzieFrame.closeCommentBar"),
                              resourceBundle.getString("LizzieFrame.closeCommentBarTitle"),
                              JOptionPane.YES_OPTION,
                              JOptionPane.QUESTION_MESSAGE,
                              null,
                              options,
                              options[0]);
                      if (response == -1) {

                      } else if (response == 0) {
                        Lizzie.config.hideBlunderControlPane = true;
                        Lizzie.config.uiConfig.put(
                            "hide-blunder-table-control-pane",
                            Lizzie.config.hideBlunderControlPane);
                        commentBlunderControlPane.setBounds(0, 0, 0, 0);
                        commentBlunderControlPane.setVisible(false);
                      } else if (response == 1) {
                      } else if (response == 2) {
                        Lizzie.config.hideBlunderControlPane = true;
                        Lizzie.config.uiConfig.put(
                            "hide-blunder-table-control-pane",
                            Lizzie.config.hideBlunderControlPane);
                        commentBlunderControlPane.setBounds(0, 0, 0, 0);
                        commentBlunderControlPane.setVisible(false);
                        Lizzie.config.allowCloseCommentControlHint = false;
                        Lizzie.config.uiConfig.put(
                            "allow-close-comment-control-hint",
                            Lizzie.config.allowCloseCommentControlHint);
                      }
                    } else {
                      Lizzie.config.hideBlunderControlPane = true;
                      Lizzie.config.uiConfig.put(
                          "hide-blunder-table-control-pane", Lizzie.config.hideBlunderControlPane);
                      commentBlunderControlPane.setBounds(0, 0, 0, 0);
                      commentBlunderControlPane.setVisible(false);
                    }
                  }
                });
          }
        });
    close.setFocusPainted(false);
    close.setContentAreaFilled(false);
    close.setMargin(new Insets(0, 0, 0, 0));
    close.addMouseListener(
        new MouseAdapter() {
          public void mouseExited(MouseEvent e) {
            commentBlunderControlPane.setVisible(false);
          }

          public void mouseEntered(MouseEvent e) {
            commentBlunderControlPane.setVisible(true);
          }
        });
    close.setBackground(Color.BLACK);
    close.setForeground(Color.WHITE);
    if (Lizzie.config.isChinese) close.setBounds(fromComment ? 94 : 190, 0, 45, 20);
    else close.setBounds(fromComment ? 147 : 260, 0, 45, 20);
    commentBlunderControlPane.add(close);

    if (resetPos) {
      if (fromComment) {
        if (commentScrollPane.getY() > 20)
          commentBlunderControlPane.setBounds(
              commentScrollPane.getX(),
              commentScrollPane.getY() - 20,
              Lizzie.config.isChinese ? 135 : 192,
              20);
        else if (commentScrollPane.getX() + commentScrollPane.getHeight() + 20
            <= Lizzie.frame.getHeight()
                - Lizzie.frame.getJMenuBar().getHeight()
                - Lizzie.frame.getInsets().top
                - Lizzie.frame.getInsets().bottom
                - toolbarHeight)
          commentBlunderControlPane.setBounds(
              commentScrollPane.getX(),
              commentScrollPane.getY() + commentScrollPane.getHeight(),
              Lizzie.config.isChinese ? 135 : 192,
              20);
        else
          commentBlunderControlPane.setBounds(
              commentScrollPane.getX(),
              commentScrollPane.getY() + commentScrollPane.getHeight() - 20,
              Lizzie.config.isChinese ? 135 : 192,
              20);
        //        commentBlunderControlPane.setBounds(
        //            commentScrollPane.getX(),
        //            commentScrollPane.getY() + commentScrollPane.getHeight() - 20,
        //            137,
        //            20);
      } else {
        if (blunderContentPane.getY() > 20)
          commentBlunderControlPane.setBounds(
              blunderContentPane.getX(),
              blunderContentPane.getY() - 20,
              Lizzie.config.isChinese ? 231 : 305,
              20);
        else if (blunderContentPane.getX() + blunderContentPane.getHeight() + 20
            <= Lizzie.frame.getHeight()
                - Lizzie.frame.getJMenuBar().getHeight()
                - Lizzie.frame.getInsets().top
                - Lizzie.frame.getInsets().bottom
                - toolbarHeight)
          commentBlunderControlPane.setBounds(
              blunderContentPane.getX(),
              blunderContentPane.getY() + blunderContentPane.getHeight(),
              Lizzie.config.isChinese ? 231 : 305,
              20);
        else
          commentBlunderControlPane.setBounds(
              blunderContentPane.getX(),
              blunderContentPane.getY() + blunderContentPane.getHeight() - 20,
              Lizzie.config.isChinese ? 231 : 305,
              20);
      }
    } else {
      commentBlunderControlPane.setSize(
          fromComment ? Lizzie.config.isChinese ? 135 : 192 : Lizzie.config.isChinese ? 231 : 305,
          20);
    }
  }

  public void setCommentPaneContent() {
    // TODO Auto-generated method stub
    if (Lizzie.config.isShowingBlunderTabel) {
      blunderContentPane.setVisible(true);
      commentScrollPane.setVisible(false);
    } else {
      blunderContentPane.setVisible(false);
      commentScrollPane.setVisible(true);
    }
  }

  public void addResizeLis() {
    addComponentListener(
        new ComponentAdapter() {
          @Override
          public void componentResized(ComponentEvent e) {
            refreshFromResized = true;
            reSetLoc();
            repaintLaterMaybe();
          }
        });
  }

  public int getLastVisitsCount(int curCount) {
    BoardHistoryNode curNode = Lizzie.board.getHistory().getCurrentHistoryNode();
    switch (curCount) {
      case 0:
        if (curNode == visitsTemp[1].node) {
          lastPlayouts = visitsTemp[1].Playouts;
          return 4;
        } else if (curNode == visitsTemp[2].node) {
          lastPlayouts = visitsTemp[2].Playouts;
          return 3;
        } else if (curNode == visitsTemp[3].node) {
          lastPlayouts = visitsTemp[3].Playouts;
          return 2;
        } else if (curNode == visitsTemp[0].node) {
          lastPlayouts = visitsTemp[0].Playouts;
          return 1;
        } else return -1;
      case 1:
        if (curNode == visitsTemp[2].node) {
          lastPlayouts = visitsTemp[2].Playouts;
          return 4;
        } else if (curNode == visitsTemp[3].node) {
          lastPlayouts = visitsTemp[3].Playouts;
          return 3;
        } else if (curNode == visitsTemp[0].node) {
          lastPlayouts = visitsTemp[0].Playouts;
          return 2;
        } else if (curNode == visitsTemp[1].node) {
          lastPlayouts = visitsTemp[1].Playouts;
          return 1;
        } else return -1;
      case 2:
        if (curNode == visitsTemp[3].node) {
          lastPlayouts = visitsTemp[3].Playouts;
          return 4;
        } else if (curNode == visitsTemp[0].node) {
          lastPlayouts = visitsTemp[0].Playouts;
          return 3;
        } else if (curNode == visitsTemp[1].node) {
          lastPlayouts = visitsTemp[1].Playouts;
          return 2;
        } else if (curNode == visitsTemp[2].node) {
          lastPlayouts = visitsTemp[2].Playouts;
          return 1;
        } else return -1;
      case 3:
        if (curNode == visitsTemp[0].node) {
          lastPlayouts = visitsTemp[0].Playouts;
          return 4;
        } else if (curNode == visitsTemp[1].node) {
          lastPlayouts = visitsTemp[1].Playouts;
          return 3;
        } else if (curNode == visitsTemp[2].node) {
          lastPlayouts = visitsTemp[2].Playouts;
          return 2;
        } else if (curNode == visitsTemp[3].node) {
          lastPlayouts = visitsTemp[3].Playouts;
          return 1;
        } else return -1;
        //	  case 4:
        //		  if(curNode==visitsTemp[0].node)
        //		  {
        //			  lastPlayouts=visitsTemp[0].Playouts;
        //			  return 5;
        //		  }
        //			  else
        //				  if(curNode==visitsTemp[1].node)
        //				  {
        //					  lastPlayouts=visitsTemp[1].Playouts;
        //					  return 4;
        //				  }
        //				  else
        //					  if(curNode==visitsTemp[2].node)
        //					  {
        //						  lastPlayouts=visitsTemp[2].Playouts;
        //						  return 3;
        //					  }
        //					  else
        //						  if(curNode==visitsTemp[3].node)
        //						  {
        //							  lastPlayouts=visitsTemp[3].Playouts;
        //							  return 2;
        //						  }
        //						  else
        //							  if(curNode==visitsTemp[4].node)
        //							  {
        //								  lastPlayouts=visitsTemp[4].Playouts;
        //								  return 1;
        //							  }
        //						  else
        //							  return -1;
    }
    return -1;
  }

  public void addInput(boolean forEngineGame) {
    if (noInput) {
      mainPanel.addKeyListener(input);
      mainPanel.addMouseListener(input);
      mainPanel.addMouseWheelListener(input);
      mainPanel.removeMouseListener(input2);
      mainPanel.removeMouseWheelListener(input2);
      // varTreePane.addMouseWheelListener(input);
      mainPanel.removeKeyListener(input2);
      varTreeScrollPane.addKeyListener(input);
      noInput = false;
    }
    if (forEngineGame) mainPanel.removeKeyListener(gtpShortKey);
  }

  public void removeInput(boolean forEngineGame) {
    if (!noInput) {
      mainPanel.removeKeyListener(input);
      mainPanel.removeMouseListener(input);
      mainPanel.removeMouseWheelListener(input);
      mainPanel.addMouseListener(input2);
      mainPanel.addMouseWheelListener(input2);
      mainPanel.addKeyListener(input2);
      varTreeScrollPane.removeKeyListener(input);
      // varTreePane.removeMouseWheelListener(input);
      noInput = true;
    }
    if (forEngineGame) mainPanel.addKeyListener(gtpShortKey);
  }

  /** Clears related status from empty board. */
  public void clear() {
    if (winrateGraph != null) {
      winrateGraph.clear();
    }
  }

  public void openOnlineDialog() {
    if (onlineDialog == null) {
      onlineDialog = new OnlineDialog();
      onlineDialog.setVisible(true);
    } else {
      try {
        onlineDialog.stopSync();
        onlineDialog.paste();
        onlineDialog.fromBrowser = false;
        onlineDialog.setVisible(true);
      } catch (Exception ex) {
      }
    }
    //  onlineDialog = new OnlineDialog();
    // onlineDialog.applyChangeWeb("https://home.yikeweiqi.com/#/live/room/20595/1/18748590");

  }

  //  public void openEditToolbar() {
  //	  editToolbar = new EditToolbar(this);
  //	  editToolbar.setVisible(true);
  //	  if((mainPanel.getWidth()/2-30)<400)
  //		  editToolbar.setLocation(this.getX()+400,this.getY()+ this.getInsets().top);
  //	  else
  //	  editToolbar.setLocation(this.getX()+mainPanel.getWidth()/2-30,this.getY()+
  // this.getInsets().top);
  //	  }
  //  public void resetEditToolbarLocation(){
  //	  if((mainPanel.getWidth()/2-30)<400)
  //		  editToolbar.setLocation(this.getX()+400,this.getY()+ this.getInsets().top);
  //	  else
  //	  editToolbar.setLocation(this.getX()+mainPanel.getWidth()/2-30,this.getY()+
  // this.getInsets().top);
  //	  }

  //  public static void openConfigDialog() {
  //    boolean oriPonder = Lizzie.leelaz != null && Lizzie.leelaz.isPondering();
  //    if (Lizzie.leelaz != null && Lizzie.leelaz.isPondering()) Lizzie.leelaz.togglePonder();
  //    ConfigDialog configDialog = new ConfigDialog();
  //    configDialog.setVisible(true);
  //    if (oriPonder) Lizzie.leelaz.togglePonder();
  //  }

  public void openAnalysisTable() {
    //	  if(!isBatchAna||Batchfiles.size()==0)
    //		  return;
    if (analysisTable == null) {
      analysisTable = new AnalysisTable();
      analysisTable.frame.setVisible(true);
    } else {
      analysisTable.frame.setVisible(true);
      analysisTable.refreshTable();
    }
  }

  public void closeAnalysisTable() {
    if (analysisTable == null || !analysisTable.frame.isVisible()) return;
    analysisTable.frame.setVisible(false);
  }

  public void openReadBoardJava() {
    if (readBoard != null) {
      try {
        readBoard.shutdown();
      } catch (Exception e) {
        e.printStackTrace();
        // Failed to save config
      }
    }
    try {
      readBoard = new ReadBoard(true, true);
    } catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  public void openBoardSync() {
    if (readBoard == null) {
      try {
        readBoard = new ReadBoard(true, false);
      } catch (Exception e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
        try {
          readBoard = new ReadBoard(false, false);
        } catch (Exception e1) {
          // TODO Auto-generated catch block
          e1.printStackTrace();
        }
      }

    } else {
      try {
        readBoard.shutdown();
      } catch (Exception e) {
        e.printStackTrace();
        // Failed to save config
      }
      try {
        readBoard = new ReadBoard(true, false);
      } catch (Exception e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
        try {
          readBoard = new ReadBoard(false, false);
        } catch (Exception e1) {
          // TODO Auto-generated catch block
          e1.printStackTrace();
        }
      }
    }
  }

  public void openConfigDialog2(int index) {
    boolean oriPonder = Lizzie.leelaz.isPondering();
    if (Lizzie.leelaz.isPondering()) Lizzie.leelaz.togglePonder();
    configDialog2 = new ConfigDialog2();
    configDialog2.switchTab(index);
    configDialog2.setVisible(true);
    if (oriPonder) Lizzie.leelaz.togglePonder();
  }

  public static void openMoreEngineDialog() {
    //    boolean oriPonder = Lizzie.leelaz != null && Lizzie.leelaz.isPondering();
    //    if (Lizzie.leelaz != null && Lizzie.leelaz.isPondering()) Lizzie.leelaz.togglePonder();
    //    ConfigDialog configDialog = new ConfigDialog();
    //    configDialog.setVisible(true);
    //    if (oriPonder) Lizzie.leelaz.togglePonder();
    boolean oriPonder = Lizzie.leelaz != null && Lizzie.leelaz.isPondering();
    if (Lizzie.leelaz != null && Lizzie.leelaz.isPondering()) Lizzie.leelaz.togglePonder();
    JDialog moreEngines;
    moreEngines = MoreEngines.createDialog();
    moreEngines.setVisible(true);
    if (oriPonder) Lizzie.leelaz.ponder();
  }

  public static void openProgramDialog() {
    JDialog programs;
    programs = OtherPrograms.createDialog();
    programs.setVisible(true);
  }

  //  public static void openAvoidmoves() {
  //    Avoidmoves Avoidmoves = new Avoidmoves();
  //    Avoidmoves.setVisible(true);
  //  }

  public boolean openRightClickMenu(int x, int y) {
    if (Lizzie.frame.clickOrder != -1) {
      Lizzie.frame.clickOrder = -1;
      Lizzie.frame.hasMoveOutOfList = false;
      boardRenderer.startNormalBoard();
      Lizzie.frame.suggestionclick = Lizzie.frame.outOfBoundCoordinate;
      Lizzie.frame.mouseOverCoordinate = Lizzie.frame.outOfBoundCoordinate;
      boardRenderer.clearBranch();
      if (extraMode == 2) {
        boardRenderer2.startNormalBoard();
        boardRenderer2.clearBranch();
      }
      Lizzie.frame.selectedorder = -1;
      Lizzie.frame.currentRow = -1;
      return true;
    }
    if (!Lizzie.config.showRightMenu && !isMouseOverSuggestions()) {
      return false;
    }
    Optional<int[]> boardCoordinates = boardRenderer.convertScreenToCoordinates(x, y);

    if (!boardCoordinates.isPresent()) {

      return false;
    }
    //    if (isPlayingAgainstLeelaz) {
    //
    //      return true;
    //    }
    // if (Lizzie.leelaz.isPondering()) {
    // Lizzie.leelaz.sendCommand("name");
    // }

    // isshowrightmenu = true;

    int[] coords = boardCoordinates.get();

    if (Lizzie.board.getstonestat(coords) == Stone.BLACK
        || Lizzie.board.getstonestat(coords) == Stone.WHITE) {
      //  RightClickMenu2.Store(x, y);
      //      Timer timer = new Timer();
      //      timer.schedule(
      //          new TimerTask() {
      //            public void run() {
      //              Lizzie.frame.showmenu2(x, y, coords);
      //              this.cancel();
      //            }
      //          },
      //          50);
      showmenu2(x, y, coords);
      return true;
    } else {
      showmenu(x, y, coords);
      //      RightClickMenu.Store(x, y);
      //      Timer timer = new Timer();
      //      timer.schedule(
      //          new TimerTask() {
      //            public void run() {
      //              Lizzie.frame.showmenu(x, y, coords);
      //              this.cancel();
      //            }
      //          },
      //          50);
    }
    return true;
  }

  public void showmenu(int x, int y, int[] coords) {
    RightClickMenu.setCoords(coords);
    RightClickMenu.show(mainPanel, Utils.zoomIn(x), Utils.zoomIn(y));
  }

  public void showmenu2(int x, int y, int[] coords) {
    RightClickMenu2.setCoords(coords);
    Lizzie.frame.RightClickMenu2.setFromIndependent(false);
    RightClickMenu2.show(mainPanel, Utils.zoomIn(x), Utils.zoomIn(y));
  }

  public static void openAvoidMoveDialog() {
    AvoidMoveDialog avoidMoveDialog = new AvoidMoveDialog();
    avoidMoveDialog.setVisible(true);
  }
  // this is copyed from https://github.com/zsalch/lizzie/tree/n_avoiddialog

  public void toggleGtpConsole() {
    // Lizzie.leelaz.toggleGtpConsole();
    if (Lizzie.gtpConsole != null) {
      Lizzie.gtpConsole.setVisible(!Lizzie.gtpConsole.isVisible());
      if (Lizzie.gtpConsole.isVisible()) Lizzie.gtpConsole.setViewEnd();
    } else {
      Lizzie.gtpConsole = new GtpConsolePane(this);
      Lizzie.gtpConsole.setVisible(true);
      Lizzie.gtpConsole.setViewEnd();
    }
  }

  public void tryPlay(boolean needRefresh) {
    if (Lizzie.engineManager.isEngineGame || Lizzie.engineManager.isPreEngineGame) return;
    if (!isTrying) {
      isTrying = true;
      try {
        tryString = SGFParser.saveToString(false);
      } catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
      titleBeforeTrying = this.getTitle();
      this.setTitle(resourceBundle.getString("LizzieFrame.tryTitle")); // "试下中...");
      toolbar.tryPlay.setText(resourceBundle.getString("BottomToolbar.tryplayBack")); // ("恢复");
      tryMoveList = Lizzie.board.getmovelist();
      Lizzie.board.getHistory().getCurrentHistoryNode().getData().moveNumber = 0;
      Lizzie.board.deleteMoveNoHintAfter();
    } else {
      isTrying = false;
      toolbar.tryPlay.setText(resourceBundle.getString("BottomToolbar.tryPlay")); // ("试下");
      SGFParser.loadFromString(tryString);
      Lizzie.board.setmovelist(tryMoveList, false);
      Lizzie.board.setMovelistAll();
      if (Lizzie.board.getcurrentmovenumber() == 0 && Lizzie.leelaz.isPondering())
        Lizzie.leelaz.ponder();
      this.setTitle(titleBeforeTrying);
      if (needRefresh) refresh();
    }
  }

  public void toggleAnalysisFrameAlwaysontop() {
    if (analysisFrame != null && analysisFrame.isVisible()) {
      if (analysisFrame.isAlwaysOnTop()) {
        if (extraMode == 2)
          if (analysisFrame2 != null && analysisFrame2.isVisible()) {
            analysisFrame2.setAlwaysOnTop(false);
            analysisFrame2.setTopTitle();
          }
        analysisFrame.setAlwaysOnTop(false);
        Lizzie.config.uiConfig.put("suggestions-always-ontop", false);
      } else {
        if (extraMode == 2)
          if (analysisFrame2 != null && analysisFrame2.isVisible()) {
            analysisFrame2.setAlwaysOnTop(true);
            analysisFrame2.setTopTitle();
          }
        analysisFrame.setAlwaysOnTop(true);
        Lizzie.config.uiConfig.put("suggestions-always-ontop", true);
        // if (Lizzie.frame.isAlwaysOnTop()) Lizzie.frame.toggleAlwaysOntop();
      }
      analysisFrame.setTopTitle();
    }
  }

  public void toggleBestMoves() {
    if (analysisFrame == null || !analysisFrame.isVisible()) {
      analysisFrame = new AnalysisFrame(1);
      if (this.extraMode == 2) {
        if (analysisFrame2 == null || !analysisFrame2.isVisible()) {
          analysisFrame2 = new AnalysisFrame(2);
          analysisFrame2.setVisible(true);
        }
      }
      analysisFrame.setVisible(true);
      if (Lizzie.config.uiConfig.optBoolean("suggestions-always-ontop", false))
        analysisFrame.setAlwaysOnTop(true);
    } else {
      analysisFrame.setVisible(false);
      if (this.extraMode == 2 && analysisFrame2 != null) {
        analysisFrame2.setVisible(false);
      }
      Lizzie.frame.suggestionclick = Lizzie.frame.outOfBoundCoordinate;
      Lizzie.frame.refresh();
      try {
        Lizzie.config.persist();
      } catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }
    Lizzie.config.uiConfig.put("show-suggestions-frame", analysisFrame.isVisible());
  }

  public void countstones(boolean shouldRetart) {
    if (Lizzie.config.showKataGoEstimate
        && !Lizzie.config.isHiddenKataEstimate
        && Lizzie.leelaz.isKatago) {
      Lizzie.config.showKataGoEstimate = false;
      clearKataEstimate();
      Lizzie.leelaz.ponder();
    }
    if (shouldRetart) {
      if (zen == null
          || zen.useJavaSSH && zen.javaSSHClosed
          || (!zen.useJavaSSH && (zen.process == null || !zen.process.isAlive()))) {
        try {
          zen = new KataEstimate(false);
        } catch (IOException e1) {
          e1.printStackTrace();
        }
      }
    }
    //  zen.noread = false;
    zen.syncboradstat();
    zen.countStones();
    isCounting = true;
  }

  public void restartZen() {
    if (zen != null) {
      try {
        zen.shutdown();
      } catch (Exception e) {
      }
    }
    try {
      zen = new KataEstimate(false);
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  public void toggleAlwaysOntop() {
    if (this.isAlwaysOnTop()) {
      this.setAlwaysOnTop(false);
      Lizzie.config.uiConfig.put("mains-always-ontop", false);
    } else {
      this.setAlwaysOnTop(true);
      Lizzie.config.uiConfig.put("mains-always-ontop", true);
    }
  }

  //  public void togglePanelMode() {
  //    switch (mode) {
  //      case 0:
  //        classicMode();
  //        break;
  //      case 1:
  //        minMode();
  //        break;
  //      case 2:
  //        defaultMode();
  //        break;
  //      default:
  //        return;
  //    }
  //  }

  public void extraMode(int mode) { // 3=思考模式 2=双引擎模式 1=四方图模式 7=精简模式 8=浮动棋盘模式
    setMinimumSize(new Dimension(520, 400));
    boolean windowIsMaximized = Lizzie.frame.getExtendedState() == JFrame.MAXIMIZED_BOTH;
    boardRenderer = new BoardRenderer(false);
    subBoardXmouse = 0;
    subBoardYmouse = 0;
    subBoardLengthmouse = 0;
    subMaxSize = 0;
    if (extraMode == 2 && mode != 2) {
      if (analysisFrame2 != null && analysisFrame2.isVisible()) analysisFrame2.setVisible(false);
    }
    if (mode == 1) {
      Lizzie.frame.subBoardRenderer2 = new SubBoardRenderer(false);
      Lizzie.frame.subBoardRenderer3 = new SubBoardRenderer(false);
      Lizzie.frame.subBoardRenderer4 = new SubBoardRenderer(false);
      Lizzie.frame.subBoardRenderer2.setOrder(1);
      Lizzie.frame.subBoardRenderer3.setOrder(2);
      Lizzie.frame.subBoardRenderer4.setOrder(3);
      Lizzie.frame.subBoardRenderer.showHeat = false;
      Lizzie.frame.subBoardRenderer.showHeatAfterCalc = false;
      try {
        Lizzie.frame.subBoardRenderer.removeHeat();
      } catch (Exception ex) {
      }
      if (!Lizzie.config.showSubBoard) Lizzie.config.toggleShowSubBoard();
      if (!Lizzie.config.showWinrateGraph) Lizzie.config.toggleShowWinrate();
      if (Lizzie.config.showComment) Lizzie.config.toggleShowComment();
      if (!Lizzie.config.showCaptured) Lizzie.config.toggleShowCaptured();
      if (!Lizzie.config.showVariationGraph) Lizzie.config.toggleShowVariationGraph();
      if (!Lizzie.config.showListPane) Lizzie.config.toggleShowListPane();
      if (!windowIsMaximized) {
        Lizzie.frame.setBounds(
            Lizzie.frame.getX(),
            Lizzie.frame.getY(),
            (Lizzie.frame.getHeight() - toolbarHeight) * 162 / 100,
            Lizzie.frame.getHeight());
        reSetLoc();
      }
      repaint();
    }
    if (mode == 2) {
      if (Lizzie.config.showSubBoard) Lizzie.config.toggleShowSubBoard();
      if (Lizzie.config.showComment) Lizzie.config.toggleShowComment();
      if (Lizzie.config.showCaptured) Lizzie.config.toggleShowCaptured();
      //      if (!Lizzie.config.changedStatus && Lizzie.config.showStatus)
      //        Lizzie.config.toggleShowStatus(true);
      if (Lizzie.config.showVariationGraph) Lizzie.config.toggleShowVariationGraph();
      if (Lizzie.config.showWinrateGraph) Lizzie.config.toggleShowWinrate();
      Lizzie.frame.menu.setEngineMenuone2status(true);
      boardRenderer2 = new BoardRenderer(false);
      boardRenderer2.setOrder(1);
      if (!windowIsMaximized) {
        Lizzie.frame.setBounds(
            Lizzie.frame.getX(),
            Lizzie.frame.getY(),
            (Lizzie.frame.getHeight() - toolbarHeight - 65) * 2,
            Lizzie.frame.getHeight());
        reSetLoc();
      }
      repaint();
      if (extraMode != 2) {
        Lizzie.board.setMovelistAll2();
        if (moveListFrame != null && moveListFrame.isVisible()) {
          toggleBadMoves();
          toggleBadMoves();
        }
      }
      if (Lizzie.leelaz2 != null) {
        Lizzie.engineManager.switchEngine(Lizzie.leelaz2.currentEngineN(), false);
      }
    } else {
      if (Lizzie.leelaz2 != null) {
        Lizzie.leelaz2.nameCmdfornoponder();
        Lizzie.frame.menu.changeEngineIcon(Lizzie.engineManager.currentEngineNo2, 2);
      }
      Lizzie.frame.menu.setEngineMenuone2status(false);
      if (extraMode == 2) {
        if (moveListFrame2 != null && moveListFrame2.isVisible()) moveListFrame2.setVisible(false);
      }
    }

    if (mode == 3) {
      if (!Lizzie.config.showSubBoard) Lizzie.config.toggleShowSubBoard();
      if (!Lizzie.config.showWinrateGraph) Lizzie.config.toggleShowWinrate();
      if (Lizzie.config.showLargeWinrateOnly()) Lizzie.config.toggleLargeWinrate();
      if (!Lizzie.config.showLargeSubBoard()) Lizzie.config.toggleLargeSubBoard();
      if (Lizzie.config.showComment) Lizzie.config.toggleShowComment();
      if (!Lizzie.config.showCaptured) Lizzie.config.toggleShowCaptured();
      if (!Lizzie.config.showListPane) Lizzie.config.toggleShowListPane();
      //      if (!Lizzie.config.changedStatus && Lizzie.config.showStatus)
      //        Lizzie.config.toggleShowStatus(true);
      if (!Lizzie.config.showVariationGraph) Lizzie.config.toggleShowVariationGraph();
      boardRenderer2 = new BoardRenderer(false);
      boardRenderer2.setOrder(2);
      boardRenderer2.setDisplayedBranchLength(BoardRenderer.SHOW_RAW_BOARD);
      if (!windowIsMaximized) {
        Lizzie.frame.setBounds(
            Lizzie.frame.getX(),
            Lizzie.frame.getY(),
            (Lizzie.frame.getHeight() - toolbarHeight) * 166 / 100,
            Lizzie.frame.getHeight());
        reSetLoc();
      }
      repaint();
    }

    extraMode = mode;
    if (mode == 2) {
      if (analysisFrame != null && analysisFrame.isVisible()) {
        toggleBestMoves();
        toggleBestMoves();
      }
    }
    if (mode > 0 && mode != 3 && mode != 1) setHideListScrollpane(false);
    else if (Lizzie.config.showListPane()) setHideListScrollpane(true);
  }

  public void minMode() {
    Lizzie.config.setClassicMode(false);
    boolean windowIsMaximized = Lizzie.frame.getExtendedState() == JFrame.MAXIMIZED_BOTH;
    boardRenderer = new BoardRenderer(false);
    Lizzie.config.toggleExtraMode(7);
    // mode = 2;
    if (Lizzie.config.showSubBoard) Lizzie.config.toggleShowSubBoard();
    if (Lizzie.config.showComment) Lizzie.config.toggleShowComment();
    if (Lizzie.config.showCaptured) Lizzie.config.toggleShowCaptured();
    if (Lizzie.config.showListPane()) Lizzie.config.toggleShowListPane();
    // if(Lizzie.config.showStatus)Lizzie.config.toggleShowStatus();
    if (Lizzie.config.showVariationGraph) Lizzie.config.toggleShowVariationGraph();
    if (Lizzie.config.showWinrateGraph) Lizzie.config.toggleShowWinrate();
    if (!windowIsMaximized) {
      int minlength =
          Math.min(Lizzie.frame.getWidth(), Lizzie.frame.getHeight() - Lizzie.frame.toolbarHeight);
      Lizzie.frame.setBounds(
          Lizzie.frame.getX(),
          Lizzie.frame.getY(),
          (int) (minlength * 0.94),
          minlength + Lizzie.frame.toolbarHeight);
      reSetLoc();
    }
    Lizzie.frame.refresh();
  }

  public void toggleOnlyIndependMainBoard() {
    if (Lizzie.config.extraMode == 8) {
      Lizzie.config.toggleExtraMode(0);
      Lizzie.frame.toggleIndependentMainBoard();
      Lizzie.frame.refresh();
    } else Lizzie.frame.onlyIndependMainBoard();
  }

  public void toggleShowIndependMainBoard() {
    if (!Lizzie.config.isShowingIndependentMain) Lizzie.frame.toggleIndependentMainBoard();
    else {
      if (Lizzie.config.extraMode == 8) {
        Lizzie.config.toggleExtraMode(0);
        Lizzie.frame.refresh();
      } else {
        Lizzie.frame.toggleIndependentMainBoard();
      }
    }
  }

  public void onlyIndependMainBoard() {
    setMinimumSize(new Dimension(0, 0));
    extraMode = 8;
    Lizzie.config.toggleExtraMode(8);
    if (!Lizzie.config.isShowingIndependentMain) toggleIndependentMainBoard();
    Lizzie.frame.refresh();
  }

  public void independentBoardMode(boolean showSubBoard) {
    setMinimumSize(new Dimension(0, 0));
    extraMode = 8;
    Lizzie.config.toggleExtraMode(8);
    if (!Lizzie.config.showListPane) Lizzie.config.toggleShowListPane();
    setHideListScrollpane(true);
    if (!Lizzie.config.showWinrateGraph) Lizzie.config.toggleShowWinrate();
    if (!Lizzie.config.showComment) Lizzie.config.toggleShowComment();
    if (!Lizzie.config.showCaptured) Lizzie.config.toggleShowCaptured();
    if (!Lizzie.config.showVariationGraph) Lizzie.config.toggleShowVariationGraph();
    if (!Lizzie.config.isShowingIndependentMain) toggleIndependentMainBoard();
    if (Lizzie.frame.getExtendedState() != JFrame.MAXIMIZED_BOTH) {
      Lizzie.frame.setBounds(
          Lizzie.frame.getX(),
          Lizzie.frame.getY(),
          (Lizzie.frame.getHeight() - toolbarHeight) * 65 / 100,
          Lizzie.frame.getHeight());
    }
    if (showSubBoard) {
      if (!Lizzie.config.showSubBoard) Lizzie.config.toggleShowSubBoard();
    } else {
      if (Lizzie.config.showSubBoard) Lizzie.config.toggleShowSubBoard();
      if (!Lizzie.config.isShowingIndependentSub) toggleIndependentSubBoard();
    }
    Lizzie.frame.refresh();
  }

  public void classicMode() {
    boardRenderer = new BoardRenderer(false);
    boolean windowIsMaximized = Lizzie.frame.getExtendedState() == JFrame.MAXIMIZED_BOTH;
    Lizzie.config.toggleExtraMode(0);
    // mode = 1;
    Lizzie.config.showStatus = false;
    Lizzie.config.setClassicMode(true);
    if (Lizzie.config.showVariationGraph) Lizzie.config.toggleShowVariationGraph();
    if (!Lizzie.config.showSubBoard) Lizzie.config.toggleShowSubBoard();
    if (!Lizzie.config.showWinrateGraph) Lizzie.config.toggleShowWinrate();
    if (Lizzie.config.showLargeWinrateOnly()) Lizzie.config.toggleLargeWinrate();
    if (!Lizzie.config.showLargeSubBoard()) Lizzie.config.toggleLargeSubBoard();
    if (!Lizzie.config.showComment) Lizzie.config.toggleShowComment();
    if (!Lizzie.config.showCaptured) Lizzie.config.toggleShowCaptured();
    //    if (!Lizzie.config.changedStatus && Lizzie.config.showStatus)
    //      Lizzie.config.toggleShowStatus(true);
    // if (!Lizzie.config.showVariationGraph) Lizzie.config.toggleShowVariationGraph();
    // if (Lizzie.frame.getWidth() - Lizzie.frame.getHeight() < 485)
    Lizzie.frame.subBoardRenderer.showHeat = Lizzie.config.showHeat;
    Lizzie.frame.subBoardRenderer.showHeatAfterCalc = Lizzie.config.showHeatAfterCalc;
    try {
      Lizzie.frame.subBoardRenderer.clearBranch();
      Lizzie.frame.subBoardRenderer.removeHeat();
    } catch (Exception ex) {
    }
    if (!windowIsMaximized) {
      Lizzie.frame.setBounds(
          Lizzie.frame.getX(),
          Lizzie.frame.getY(),
          (Lizzie.frame.getHeight() - toolbarHeight) * 145 / 100,
          Lizzie.frame.getHeight());
      reSetLoc();
    }
    // Lizzie.frame.redrawBackgroundAnyway=true;
    Lizzie.frame.refresh();
  }

  public void defaultMode() {
    Lizzie.config.setClassicMode(false);
    boardRenderer = new BoardRenderer(false);
    boolean windowIsMaximized = Lizzie.frame.getExtendedState() == JFrame.MAXIMIZED_BOTH;
    Lizzie.config.toggleExtraMode(0);
    //   mode = 0;
    Lizzie.config.showStatus = Lizzie.config.uiConfig.getBoolean("show-status");
    if (!Lizzie.config.showSubBoard) Lizzie.config.toggleShowSubBoard();
    if (!Lizzie.config.showListPane) Lizzie.config.toggleShowListPane();
    if (!Lizzie.config.showWinrateGraph) Lizzie.config.toggleShowWinrate();
    if (Lizzie.config.showLargeSubBoard()) Lizzie.config.toggleLargeSubBoard();
    if (Lizzie.config.showLargeWinrate()) Lizzie.config.toggleLargeWinrate();
    if (!Lizzie.config.showComment) Lizzie.config.toggleShowComment();
    if (!Lizzie.config.showCaptured) Lizzie.config.toggleShowCaptured();
    //    if (!Lizzie.config.changedStatus && !Lizzie.config.showStatus)
    //      Lizzie.config.toggleShowStatus(true);
    if (!Lizzie.config.showVariationGraph) Lizzie.config.toggleShowVariationGraph();
    // if (Lizzie.frame.getWidth() - Lizzie.frame.getHeight() < 600)
    Lizzie.frame.subBoardRenderer.showHeat = Lizzie.config.showHeat;
    Lizzie.frame.subBoardRenderer.showHeatAfterCalc = Lizzie.config.showHeatAfterCalc;
    try {
      Lizzie.frame.subBoardRenderer.clearBranch();
      Lizzie.frame.subBoardRenderer.removeHeat();
    } catch (Exception ex) {
    }
    if (!windowIsMaximized) {
      Lizzie.frame.setBounds(
          Lizzie.frame.getX(),
          Lizzie.frame.getY(),
          (Lizzie.frame.getHeight() - toolbarHeight) * 165 / 100,
          Lizzie.frame.getHeight());
      reSetLoc();
    }
    Lizzie.frame.refresh();
  }

  public void toggleBadMoves() {
    if (moveListFrame == null || !moveListFrame.isVisible()) {
      moveListFrame = new MoveListFrame(1);
      if (Lizzie.config.extraMode == 2) {
        if (moveListFrame2 == null || !moveListFrame2.isVisible()) {
          moveListFrame2 = new MoveListFrame(2);
          moveListFrame2.setVisible(true);
          if (Lizzie.config.badmovesalwaysontop) moveListFrame2.setAlwaysOnTop(true);
        }
      }
      moveListFrame.setVisible(true);
      if (Lizzie.config.badmovesalwaysontop) moveListFrame.setAlwaysOnTop(true);
    } else {
      moveListFrame.setVisible(false);
      if (this.extraMode == 2 && moveListFrame2 != null) {
        moveListFrame2.setVisible(false);
      }
      clickbadmove = Lizzie.frame.outOfBoundCoordinate;
      Lizzie.frame.refresh();
      try {
        Lizzie.config.persist();
      } catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }
    Lizzie.config.uiConfig.put("show-badmoves-frame", moveListFrame.isVisible());
  }

  public static void sendAiTime() {
    if (Lizzie.config.advanceTimeSettings) {
      Lizzie.leelaz.sendCommand(Lizzie.config.advanceTimeTxt);
    } else {
      Lizzie.leelaz.sendCommand(
          "time_settings 0 " + Lizzie.config.maxGameThinkingTimeSeconds + " 1");
    }
  }

  public void startNewGame() {
    // GameInfo gameInfo = Lizzie.board.getHistory().getGameInfo();
    Lizzie.frame.stopAiPlayingAndPolicy();
    boolean isPondering = false;
    if (Lizzie.leelaz.isPondering()) {
      Lizzie.leelaz.togglePonder();
      isPondering = true;
    }
    NewGameDialog newGameDialog = new NewGameDialog();
    // newGameDialog.setGameInfo(gameInfo);
    newGameDialog.setVisible(true);
    boolean playerIsBlack = newGameDialog.playerIsBlack();
    newGameDialog.dispose();
    if (newGameDialog.isCancelled()) {
      if (isPondering) Lizzie.leelaz.togglePonder();
      return;
    }
    Lizzie.frame.isAnaPlayingAgainstLeelaz = false;
    Lizzie.board.clear(false);
    if (Lizzie.board.tempmovelistForGenMoveGame != null)
      Lizzie.board.setlist(Lizzie.board.tempmovelistForGenMoveGame);
    GameInfo gameInfo = newGameDialog.gameInfo;
    Lizzie.board.getHistory().setGameInfo(gameInfo);
    Lizzie.leelaz.komi(gameInfo.getKomi());
    // Lizzie.frame.komi = gameInfo.getKomi() + "";
    if (!Lizzie.config.genmoveGameNoTime) sendAiTime();
    //    Lizzie.leelaz.sendCommand(
    //        "time_settings 0 "
    //            +
    // Lizzie.config.config.getJSONObject("leelaz").getInt("max-game-thinking-time-seconds")
    //            + " 1");
    Lizzie.frame.playerIsBlack = playerIsBlack;
    Lizzie.frame.isPlayingAgainstLeelaz = true;
    // Lizzie.leelaz.isSettingHandicap=true;
    boolean isHandicapGame = gameInfo.getHandicap() != 0;
    Lizzie.frame.allowPlaceStone = false;
    Runnable syncBoard =
        new Runnable() {
          public void run() {
            while (!Lizzie.leelaz.isLoaded() || Lizzie.engineManager.isEmpty) {
              try {
                Thread.sleep(100);
              } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
              }
            }
            try {
              Thread.sleep(1000);
            } catch (InterruptedException e) {
              // TODO Auto-generated catch block
              e.printStackTrace();
            }
            Lizzie.leelaz.setGameStatus(true);
            menu.toggleDoubleMenuGameStatus();
            if (Lizzie.config.limitMyTime)
              countDownForHuman(
                  Lizzie.config.getMySaveTime(),
                  Lizzie.config.getMyByoyomiSeconds(),
                  Lizzie.config.getMyByoyomiTimes());
            clearWRNforGame(true);
            if (isHandicapGame) {
              Lizzie.board.getHistory().getData().blackToPlay = false;
              if (Lizzie.leelaz.isKatago && Lizzie.config.useFreeHandicap)
                Lizzie.leelaz.sendCommand("place_free_handicap " + gameInfo.getHandicap());
              else Lizzie.leelaz.sendCommand("fixed_handicap " + gameInfo.getHandicap());
              if (playerIsBlack) Lizzie.leelaz.genmove("w");
            } else {
              Lizzie.frame.allowPlaceStone = true;
              if (!playerIsBlack && Lizzie.board.getHistory().isBlacksTurn()) {
                Lizzie.leelaz.genmove("b");
              } else if (playerIsBlack && !Lizzie.board.getHistory().isBlacksTurn())
                Lizzie.leelaz.genmove("w");
            }
            GameInfo gameInfo = Lizzie.board.getHistory().getGameInfo();
            gameInfo.setPlayerBlack(
                playerIsBlack
                    ? resourceBundle.getString("NewAnaGameDialog.me")
                    : Lizzie.leelaz.oriEnginename);
            gameInfo.setPlayerWhite(
                playerIsBlack
                    ? Lizzie.leelaz.oriEnginename
                    : resourceBundle.getString("NewAnaGameDialog.me"));

            Lizzie.leelaz.isGamePaused = false;
            Lizzie.board.isGameBoard = true;
            Lizzie.frame.updateTitle();
          }
        };
    Thread syncBoardTh = new Thread(syncBoard);
    syncBoardTh.start();
  }

  public static void editGameInfo() {
    GameInfo gameInfo = Lizzie.board.getHistory().getGameInfo();

    GameInfoDialog gameInfoDialog = new GameInfoDialog();
    gameInfoDialog.setGameInfo(gameInfo);
    gameInfoDialog.setVisible(true);
    gameInfoDialog.dispose();
  }

  public static JTextField getTextField(Container c) {
    JTextField textField = null;
    for (int i = 0; i < c.getComponentCount(); i++) {
      Component cnt = c.getComponent(i);
      if (cnt instanceof JTextField) {
        return (JTextField) cnt;
      }
      if (cnt instanceof Container) {
        textField = getTextField((Container) cnt);
        if (textField != null) {
          return textField;
        }
      }
    }
    return textField;
  }

  public void saveRawFileComment() {
    isSavingRaw = true;
    isSavingRawComment = true;
    FileNameExtensionFilter filter = new FileNameExtensionFilter("*.sgf", "SGF");
    JSONObject filesystem = Lizzie.config.persisted.getJSONObject("filesystem");
    JFileChooser chooser = new JFileChooser(filesystem.getString("last-folder"));
    chooser.setFileFilter(filter);
    JFrame frame = new JFrame();
    frame.setAlwaysOnTop(Lizzie.frame.isAlwaysOnTop());
    chooser.setMultiSelectionEnabled(false);
    String fileName = Lizzie.board.getHistory().getGameInfo().getSaveFileName();
    String sf = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
    if (!fileName.equals("")) {
      text = getTextField(chooser);
      text.setText(fileName + "_" + sf);
      text.setEnabled(false);
    } else {
      text = getTextField(chooser);
      text.setText(sf);
      text.setEnabled(false);
    }
    Runnable runnable =
        new Runnable() {
          public void run() {
            try {
              Thread.sleep(400);
            } catch (InterruptedException e) {
              // TODO Auto-generated catch block
              e.printStackTrace();
            }
            text.setEnabled(true);
            text.requestFocus(true);
            text.selectAll();
          }
        };
    Thread thread = new Thread(runnable);
    thread.start();

    int result = chooser.showSaveDialog(frame);
    if (result == JFileChooser.APPROVE_OPTION) {
      File file = chooser.getSelectedFile();
      if (!file.getName().contains("sgf")) file = new File(file.getAbsolutePath() + ".sgf");
      if (file.exists()) {
        int ret =
            JOptionPane.showConfirmDialog(
                Lizzie.frame,
                resourceBundle.getString("LizzieFrame.prompt.sgfExists"),
                resourceBundle.getString("LizzieFrame.warning"),
                JOptionPane.OK_CANCEL_OPTION);
        if (ret == JOptionPane.CANCEL_OPTION || ret == -1) {
          return;
        }
      }
      if (!file.getPath().endsWith(".sgf")) {
        file = new File(file.getPath() + ".sgf");
      }
      try {
        SGFParser.save(Lizzie.board, file.getPath());
        if (file.getParent() != null) {
          filesystem.put("last-folder", file.getParent());
        }
      } catch (IOException err) {
        //   Message msg = new Message();
        //  msg.setMessage("保存失败");
        Utils.showMsg(resourceBundle.getString("LizzieFrame.saveFileFailed"));
        // msg.setVisible(true);LizzieFrame.saveFileFailed
      }
      isSavingRawComment = false;
      isSavingRaw = false;
    }
  }

  public void saveOriFile() {
    if (curFile != null && !curFile.getName().toLowerCase().endsWith(".gib")) {
      if (Lizzie.config.showReplaceFileHint) {
        Box box = Box.createVerticalBox();
        JFontLabel label =
            new JFontLabel(
                resourceBundle.getString("LizzieFrame.ifReplaceFile") + curFile.getName() + "\" ?");
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        box.add(label);
        Utils.addFiller(box, 5, 5);
        Utils.addFiller(box, 5, 5);
        JFontLabel label2 =
            new JFontLabel(resourceBundle.getString("LizzieFrame.replaceFileNotice"));
        label2.setAlignmentX(Component.LEFT_ALIGNMENT);
        box.add(label2);
        JFontCheckBox disableCheckBox =
            new JFontCheckBox(
                resourceBundle.getString("LizzieFrame.noNoticeAgain")); // LizzieFrame.noNoticeAgain
        disableCheckBox.addActionListener(
            new ActionListener() {
              @Override
              public void actionPerformed(ActionEvent e) {
                Lizzie.config.showReplaceFileHint = !disableCheckBox.isSelected();
                Lizzie.config.uiConfig.put(
                    "show-replace-file-hint", Lizzie.config.showReplaceFileHint);
              }
            });
        disableCheckBox.setAlignmentX(Component.LEFT_ALIGNMENT);
        box.add(disableCheckBox);
        Object[] options = new Object[2];
        options[0] = resourceBundle.getString("LizzieFrame.confirm");
        options[1] = resourceBundle.getString("LizzieFrame.cancel");
        Object defaultOption = resourceBundle.getString("LizzieFrame.cancel");
        JOptionPane optionPane =
            new JOptionPane(
                box,
                JOptionPane.QUESTION_MESSAGE,
                JOptionPane.YES_NO_OPTION,
                null,
                options,
                defaultOption);
        JDialog dialog =
            optionPane.createDialog(this, resourceBundle.getString("LizzieFrame.replaceFileTitle"));
        dialog.setVisible(true);
        dialog.dispose();
        if (optionPane.getValue() == null || optionPane.getValue().equals(defaultOption))
          // System.out.println("取消");
          return;
      }
      try {
        SGFParser.save(Lizzie.board, curFile.getPath());
      } catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    } else {
      saveFile(false);
    }
  }

  public static void saveFile(boolean savingRaw) {
    boolean pondering = false;
    if (Lizzie.leelaz.isPondering() && !Lizzie.engineManager.isEngineGame) {
      pondering = true;
      Lizzie.leelaz.togglePonder();
    }
    isSavingRaw = savingRaw;
    FileNameExtensionFilter filter = new FileNameExtensionFilter("*.sgf", "SGF");
    JSONObject filesystem = Lizzie.config.persisted.getJSONObject("filesystem");
    JFileChooser chooser = new JFileChooser(filesystem.getString("last-folder"));
    chooser.setFileFilter(filter);
    JFrame frame = new JFrame();
    frame.setAlwaysOnTop(Lizzie.frame.isAlwaysOnTop());
    chooser.setMultiSelectionEnabled(false);
    String fileName = Lizzie.board.getHistory().getGameInfo().getSaveFileName();
    String sf = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
    if (!fileName.equals("")) {
      text = getTextField(chooser);
      text.setText(fileName + "_" + sf);
      text.setEnabled(false);
    } else {
      text = getTextField(chooser);
      text.setText(sf);
      text.setEnabled(false);
    }
    Runnable runnable =
        new Runnable() {
          public void run() {
            try {
              Thread.sleep(400);
            } catch (InterruptedException e) {
              // TODO Auto-generated catch block
              e.printStackTrace();
            }
            text.setEnabled(true);
            text.requestFocus(true);
            text.selectAll();
          }
        };
    Thread thread = new Thread(runnable);
    thread.start();

    int result = chooser.showSaveDialog(frame);
    if (result == JFileChooser.APPROVE_OPTION) {
      File file = chooser.getSelectedFile();
      if (!file.getName().contains("sgf")) file = new File(file.getAbsolutePath() + ".sgf");
      if (file.exists()) {
        int ret =
            JOptionPane.showConfirmDialog(
                Lizzie.frame,
                resourceBundle.getString("LizzieFrame.prompt.sgfExists"),
                resourceBundle.getString("LizzieFrame.warning"),
                JOptionPane.OK_CANCEL_OPTION);
        if (ret == JOptionPane.CANCEL_OPTION || ret == -1) {
          return;
        }
      }
      if (!file.getPath().endsWith(".sgf")) {
        file = new File(file.getPath() + ".sgf");
      }
      try {
        SGFParser.save(Lizzie.board, file.getPath());
        curFile = file;
        if (file.getParent() != null) {
          filesystem.put("last-folder", file.getParent());
        }
      } catch (IOException err) {
        Utils.showMsg(resourceBundle.getString("LizzieFrame.saveFileFailed")); // 保存失败
        // msg.setVisible(true);
      }
      isSavingRaw = false;
    }
    if (pondering) Lizzie.leelaz.togglePonder();
  }

  public void setMainPanelFocus() {
    mainPanel.requestFocus();
  }

  public static void saveCurrentBranch() {
    FileNameExtensionFilter filter = new FileNameExtensionFilter("*.sgf", "SGF");
    JSONObject filesystem = Lizzie.config.persisted.getJSONObject("filesystem");
    JFileChooser chooser = new JFileChooser(filesystem.getString("last-folder"));
    chooser.setFileFilter(filter);
    JFrame frame = new JFrame();
    frame.setAlwaysOnTop(Lizzie.frame.isAlwaysOnTop());
    chooser.setMultiSelectionEnabled(false);
    String fileName = Lizzie.board.getHistory().getGameInfo().getSaveFileName();
    String sf = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
    if (!fileName.equals("")) {
      text = getTextField(chooser);
      text.setText(fileName + "_" + sf);
      text.setEnabled(false);
    } else {
      text = getTextField(chooser);
      text.setText(sf);
      text.setEnabled(false);
    }
    Runnable runnable =
        new Runnable() {
          public void run() {
            try {
              Thread.sleep(400);
            } catch (InterruptedException e) {
              // TODO Auto-generated catch block
              e.printStackTrace();
            }
            text.setEnabled(true);
            text.requestFocus(true);
            text.selectAll();
          }
        };
    Thread thread = new Thread(runnable);
    thread.start();

    int result = chooser.showSaveDialog(frame);
    if (result == JFileChooser.APPROVE_OPTION) {
      File file = chooser.getSelectedFile();
      if (!file.getName().contains("sgf")) file = new File(file.getAbsolutePath() + ".sgf");
      if (file.exists()) {
        int ret =
            JOptionPane.showConfirmDialog(
                Lizzie.frame,
                resourceBundle.getString("LizzieFrame.prompt.sgfExists"),
                resourceBundle.getString("LizzieFrame.warning"),
                JOptionPane.OK_CANCEL_OPTION);
        if (ret == JOptionPane.CANCEL_OPTION || ret == -1) {
          return;
        }
      }
      if (!file.getPath().endsWith(".sgf")) {
        file = new File(file.getPath() + ".sgf");
      }
      try {

        int startMoveNumber = 0;
        boolean blackToPlay = Lizzie.board.getHistory().getStart().getData().blackToPlay;
        if (Lizzie.board.hasStartStone) startMoveNumber += Lizzie.board.startStonelist.size();
        Lizzie.board.savelistforeditmode();
        Lizzie.board.clearforedit();
        Lizzie.board.setMoveListWithFlatten(
            Lizzie.board.tempallmovelist, startMoveNumber, blackToPlay);
        isSavingRaw = true;
        SGFParser.save(Lizzie.board, file.getPath());
        isSavingRaw = false;
        if (file.getParent() != null) {
          filesystem.put("last-folder", file.getParent());
        }
        Lizzie.board.cleanedit();
      } catch (IOException err) {
        Utils.showMsg(resourceBundle.getString("LizzieFrame.saveFileFailed"));
        //        Message msg = new Message();
        //        msg.setMessage("保存失败");
        //        msg.setVisible(true);
      }
    }
  }

  public void openFile() {
    boolean ponder = false;
    if (Lizzie.leelaz.isPondering() || !Lizzie.leelaz.isLoaded) {
      ponder = true;
      Lizzie.leelaz.togglePonder();
    }

    JSONObject filesystem = Lizzie.config.persisted.getJSONObject("filesystem");
    this.setAlwaysOnTop(false);
    FileDialog fileDialog =
        new FileDialog(this, resourceBundle.getString("LizzieFrame.chooseKifu"));

    fileDialog.setLocationRelativeTo(this);
    fileDialog.setDirectory(filesystem.getString("last-folder"));
    fileDialog.setFile("*.sgf;*.gib;*.SGF;*.GIB");

    fileDialog.setMultipleMode(false);
    fileDialog.setMode(0);
    fileDialog.setVisible(true);

    File[] file = fileDialog.getFiles();

    if (file.length > 0) loadFile(file[0], false, true);
    if (file.length > 0) {
      curFile = file[0];
    }

    if (ponder) {
      Lizzie.leelaz.ponder();
    }
    if (Lizzie.leelaz.isheatmap) Lizzie.leelaz.setHeatmap();
    this.setAlwaysOnTop(Lizzie.config.mainsalwaysontop);
    refresh();
  }

  public void openSgfStart() {
    if (Lizzie.leelaz.isPondering()) {
      Lizzie.leelaz.togglePonder();
    }
    isEnginePKSgfStart = false;
    enginePKSgfNum = 0;
    enginePkSgfWinLoss = new ArrayList<SgfWinLossList>();
    JSONObject filesystem = Lizzie.config.persisted.getJSONObject("filesystem");
    this.setAlwaysOnTop(false);
    FileDialog fileDialog =
        new FileDialog(this, resourceBundle.getString("LizzieFrame.chooseOpeningSgf"));

    fileDialog.setLocationRelativeTo(this);
    fileDialog.setDirectory(filesystem.getString("last-folder"));
    fileDialog.setFile("*.sgf;*.gib;*.SGF;*.GIB");

    fileDialog.setMultipleMode(true);
    fileDialog.setMode(0);
    fileDialog.setVisible(true);

    File[] files = fileDialog.getFiles();

    if (files.length > 0) {
      isEnginePKSgfStart = true;
      enginePKSgfString = new ArrayList<ArrayList<Movelist>>();
      Lizzie.board.isLoadingFile = true;
      boolean oriSound = Lizzie.config.playSound;
      Lizzie.config.playSound = false;
      for (int i = 0; i < files.length; i++) {
        loadFile(files[i], true, true);
        Lizzie.board.isLoadingFile = true;
        enginePKSgfString.add(Lizzie.board.getallmovelist()); // 改为读取到String组中
        SgfWinLossList sgfWinLoss = new SgfWinLossList();
        sgfWinLoss.SgfNumber = i;
        enginePkSgfWinLoss.add(sgfWinLoss);
      }
      Lizzie.board.isLoadingFile = false;
      Lizzie.config.playSound = oriSound;
      Lizzie.board.clear(false);
    }

    this.setAlwaysOnTop(Lizzie.config.mainsalwaysontop);
  }

  public void openFileWithAna(boolean isFlashMode) {
    //   boolean ponder = false;
    //  double komi = Lizzie.board.getHistory().getGameInfo().getKomi();
    //    if (Lizzie.leelaz.isPondering()) {
    //      ponder = true;
    //      Lizzie.leelaz.togglePonder();
    //    }
    JSONObject filesystem = Lizzie.config.persisted.getJSONObject("filesystem");
    // JFrame frame = new JFrame();
    this.setAlwaysOnTop(false);
    FileDialog fileDialog =
        new FileDialog(this, resourceBundle.getString("LizzieFrame.chooseKifu"));

    fileDialog.setLocationRelativeTo(this);
    fileDialog.setDirectory(filesystem.getString("last-folder"));
    fileDialog.setFile("*.sgf;*.gib;*.SGF;*.GIB");

    fileDialog.setMultipleMode(true);
    fileDialog.setMode(0);
    fileDialog.setVisible(true);

    File[] files = fileDialog.getFiles();
    if (files.length > 0) {
      isBatchAna = true;
      BatchAnaNum = 0;
      curFile = files[0];
      Batchfiles = new ArrayList<File>();
      for (int i = 0; i < files.length; i++) {
        Batchfiles.add(files[i]);
      }
      loadFile(files[0], false, true);
      // toolbar.chkAnaAutoSave.setSelected(true);
      // toolbar.chkAnaAutoSave.setEnabled(false);
      // 打开分析界面
      if (Lizzie.frame.analysisTable != null && Lizzie.frame.analysisTable.frame.isVisible()) {
        Lizzie.frame.analysisTable.refreshTable();
      }
      // Lizzie.leelaz.komi(komi);
      Lizzie.frame.toolbar.chkAnaAutoSave.setSelected(true);
      StartAnaDialog newgame = new StartAnaDialog(isFlashMode);
      newgame.setVisible(true);
      if (newgame.isCancelled()) {
        toolbar.resetAutoAna();
        isBatchAna = false;
        return;
      }
    }
    this.setAlwaysOnTop(Lizzie.config.mainsalwaysontop);
  }

  public void resumeFile() {
    File file = new File("save" + Utils.pwd + "autoGame1.sgf");
    if (file.exists()) loadFile(file, true, true);
    else {
      File file2 = new File("save" + Utils.pwd + "autoGame2.sgf");
      if (file2.exists()) loadFile(file2, true, true);
    }
    while (Lizzie.board.nextMove(false)) ;
    Lizzie.board.clearAfterMove();
    refresh();
  }

  public void loadFile(File file, boolean fromTemp, boolean showHint) {
    boolean oriSound = Lizzie.config.playSound;
    canGoAfterload = false;
    Lizzie.config.playSound = false;
    JSONObject filesystem = Lizzie.config.persisted.getJSONObject("filesystem");
    //    if (!(file.getPath().toLowerCase().endsWith(".sgf")
    //        || file.getPath().toLowerCase().endsWith(".gib"))) {
    //      file = new File(file.getPath() + ".sgf");
    //    }
    try {
      System.out.println(file.getPath());
      if (file.getPath().toLowerCase().endsWith(".gib")) {
        GIBParser.load(file.getPath());
      } else {
        SGFParser.load(file.getPath(), showHint);
      }

      if (!fromTemp) {
        Lizzie.config.saveRecentFilePaths(file.getPath());
        menu.updateRecentFileMenu();
        if (file.getParent() != null) {
          filesystem.put("last-folder", file.getParent());
        }
      }
    } catch (IOException err) {
      JOptionPane.showConfirmDialog(
          Lizzie.frame,
          resourceBundle.getString("LizzieFrame.prompt.failedToOpenFile"),
          "Error",
          JOptionPane.ERROR);
    }
    Lizzie.board.setMovelistAll();
    if (showHint) {
      Lizzie.frame.resetMovelistFrameandAnalysisFrame();
      if (extraMode != 8 && !(analysisTable != null && analysisTable.frame.isVisible()))
        Lizzie.frame.setVisible(true);
    }
    Lizzie.config.playSound = oriSound;
    fileNameTitle = file.getName();
    updateTitle();
    Runnable runnable =
        new Runnable() {
          public void run() {
            try {
              Thread.sleep(1000);
            } catch (InterruptedException e) {
              // TODO Auto-generated catch block
              e.printStackTrace();
            }
            canGoAfterload = true;
          }
        };
    Thread thread = new Thread(runnable);
    thread.start();
  }

  private BufferedImage cachedImage;
  private BufferedImage cachedVarImage;
  private BufferedImage cachedVarImage2;
  private BufferedImage cachedBackground;
  private BufferedImage cachedWinrateImage;
  public Paint backgroundPaint;
  private int cachedBackgroundWidth = 0, cachedBackgroundHeight = 0;
  //  private boolean cachedBackgroundShowControls = false;
  //  private boolean cachedShowWinrate = true;
  //  private boolean cachedShowVariationGraph = true;
  //  private boolean cachedShowLargeSubBoard = true;
  //  private boolean cachedLargeWinrate = true;
  //  private boolean cachedShowComment = true;
  public boolean redrawBackgroundAnyway = false;
  // private int cachedBoardPositionProportion = BoardPositionProportion;

  /**
   * Draws the game board and interface
   *
   * @param g0 not used
   */
  public void paintMianPanel(Graphics g0) {
    if (this.redrawWinratePaneOnly) {
      drawWinratePane(this.grx, this.gry, this.grw, this.grh);
      redrawWinratePaneOnly = false;
    } else {
      isSmallCap = false;
      appendComment();
      int width = mainPanel.getWidth();
      int height = mainPanel.getHeight();

      Optional<Graphics2D> backgroundG;
      if (cachedBackgroundWidth != width
          || cachedBackgroundHeight != height
          || redrawBackgroundAnyway) {
        if (Lizzie.config.isScaled && redrawBackgroundAnyway) {
          repaint();
        }
        backgroundG = Optional.of(createBackground(width, height));
      } else {
        backgroundG = Optional.empty();
      }
      if (!showControls) {
        BufferedImage cachedImage = new BufferedImage(width, height, TYPE_INT_ARGB);
        Graphics2D g = (Graphics2D) cachedImage.getGraphics();
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        if (extraMode == 1) {
          int topInset = mainPanel.getInsets().top;
          int leftInset = mainPanel.getInsets().left;
          int rightInset = mainPanel.getInsets().right;
          int bottomInset = mainPanel.getInsets().bottom; // + this.getJMenuBar().getHeight();
          // int maxBound = Math.max(width, height);

          boolean noWinrate = !Lizzie.config.showWinrateGraph;
          boolean noVariation = !Lizzie.config.showVariationGraph;
          boolean noBasic = !Lizzie.config.showCaptured;
          // boolean noSubBoard = !Lizzie.config.showSubBoard;
          boolean noComment = !Lizzie.config.showComment || Lizzie.config.showListPane();
          boolean noListPane = !Lizzie.config.showListPane();
          boolean noCommentAndListPane = noComment && noListPane;
          // board
          subMaxSize = (int) (min(width - leftInset - rightInset, height - topInset - bottomInset));
          subMaxSize = max(subMaxSize, max(Board.boardWidth, Board.boardHeight) + 5);
          //         subBoardX = 0;//(width - maxSize) / 8 * BoardPositionProportion;
          //         subBoardY = 0;

          // cachedImage = new BufferedImage(width, height, TYPE_INT_ARGB);
          //  Graphics2D g = (Graphics2D) cachedImage.getGraphics();
          //    g.setRenderingHint(RenderingHints.KEY_RENDERING,
          // RenderingHints.VALUE_RENDER_QUALITY);

          // subBoardRenderer.setLocation( cx,cy);
          // subBoardXmouse = subBoardX;
          // subBoardYmouse = subBoardY;
          //  subBoardLengthmouse = subBoardLength;
          subBoardRenderer.setLocation(topInset, leftInset);
          subBoardRenderer.setBoardLength(subMaxSize / 2, subMaxSize / 2);
          subBoardRenderer.setupSizeParameters();
          subBoardRenderer.draw(g);

          subBoardRenderer2.setLocation(subMaxSize / 2, leftInset);
          subBoardRenderer2.setBoardLength(subMaxSize / 2, subMaxSize / 2);
          subBoardRenderer2.setupSizeParameters();
          subBoardRenderer2.draw(g);

          subBoardRenderer3.setLocation(topInset, subMaxSize / 2);
          subBoardRenderer3.setBoardLength(subMaxSize / 2, subMaxSize / 2);
          subBoardRenderer3.setupSizeParameters();
          subBoardRenderer3.draw(g);

          subBoardRenderer4.setLocation(subMaxSize / 2, subMaxSize / 2);
          subBoardRenderer4.setBoardLength(subMaxSize / 2, subMaxSize / 2);
          subBoardRenderer4.setupSizeParameters();
          subBoardRenderer4.draw(g);

          // subBoardXmouse = 0;
          // subBoardYmouse = 0;
          subBoardLengthmouse = subMaxSize;

          int trueWidth = width - leftInset - rightInset - subMaxSize;
          int trueHeight = height - topInset - bottomInset;

          boolean isWidth = trueWidth * 0.72 > trueHeight;
          if (isWidth) {
            maxSize = (int) (min(trueWidth, trueHeight));
            maxSize = max(maxSize, max(Board.boardWidth, Board.boardHeight) + 5);
            boardX = width - maxSize; // ) / 8 * BoardPositionProportion;
            boardY = trueHeight - maxSize;
            boardRenderer.setLocation(boardX, boardY);
            boardRenderer.setBoardLength(maxSize, maxSize);
            boardRenderer.setupSizeParameters();
            boardRenderer.draw(g);

            int vh = trueHeight;
            int vw = boardX - subMaxSize;
            int vx = subMaxSize;
            int vy = 0;
            if (backgroundG.isPresent()) {
              bufferedContainer = drawContainer(vx, vy, vw, vh / 2);
            }

            if (!noVariation) {
              if (!noCommentAndListPane) {
                if (noWinrate && noBasic) {
                  if (bufferedContainer != null) {
                    g.drawImage(bufferedContainer, vx, vy, null);
                    g.drawImage(bufferedContainer, vx, vy + vh / 2, null);
                  }
                  createVarTreeImage(vx, vy + vh, vw, vh / 2, g);
                  if (noComment) setListScrollpane(vx, vy + vh / 2, vw, vh / 2);
                  else if (noListPane) drawComment(g, vx, vy + vh / 2, vw, vh / 2);
                } else {
                  if (bufferedContainer != null) {
                    g.drawImage(bufferedContainer, vx, vy + vh / 2, null);
                  }
                  createVarTreeImage(vx, vy + vh / 2, vw, vh / 4, g);
                  if (noComment) setListScrollpane(vx, vy + vh * 3 / 4, vw, vh / 4);
                  else if (noListPane) drawComment(g, vx, vy + vh * 3 / 4, vw, vh / 4);
                }
              } else {
                if (noWinrate && noBasic) {
                  if (bufferedContainer != null) {
                    g.drawImage(bufferedContainer, vx, vy, null);
                    g.drawImage(bufferedContainer, vx, vy + vh / 2, null);
                  }
                  createVarTreeImage(vx, vy, vw, vh, g);
                } else {
                  if (bufferedContainer != null) {
                    g.drawImage(bufferedContainer, vx, vy + vh / 2, null);
                  }
                  createVarTreeImage(vx, vy + vh / 2, vw, vh / 2, g);
                }
              }
              // drawComment(g, cx, cy, cw, ch);
              //  variationTree.drawsmall(g, treex, treey, treew, treeh);
            } else if (!noCommentAndListPane) {
              if (noComment) setListScrollpane(vx, vy + vh / 2, vw, vh / 2);
              else if (noListPane) drawComment(g, vx, vy + vh / 2, vw, vh / 2);
            }
            if (!noWinrate) {
              if (bufferedContainer != null) g.drawImage(bufferedContainer, subMaxSize, 0, null);
              if (!noBasic) {
                grw = vw;
                grx = vx;
                gry = vy + vh / 4;
                grh = vh / 4;
                drawWinratePane(grx, gry, grw, grh);
                // winrateGraph.draw(g, grx, gry, grw, grh);
                statx = vx;
                staty = vy;
                statw = vw;
                stath = vh / 4;
                drawMoveStatistics(g, statx, staty + stath / 2, statw, stath / 2);
                drawCaptured(g, statx, staty, statw, stath / 2, true);
              } else {
                grw = vw;
                grx = vx;
                gry = vy;
                grh = vh / 2;
                drawWinratePane(grx, gry, grw, grh);
                // winrateGraph.draw(g, grx, gry, grw, grh);
              }
            } else if (!noBasic) {
              if (bufferedContainer != null) g.drawImage(bufferedContainer, subMaxSize, 0, null);
              statx = vx;
              staty = vy;
              statw = vw;
              stath = vh / 2;
              drawMoveStatistics(g, statx, staty + stath / 2, statw, stath / 2);
              drawCaptured(g, statx, staty, statw, stath / 2, true);
            }

          } else {

            maxSize = (int) (min(trueWidth, 0.77 * trueHeight));
            maxSize = max(maxSize, max(Board.boardWidth, Board.boardHeight) + 5);
            boardX = subMaxSize; // ) / 8 * BoardPositionProportion;
            boardY = trueHeight - maxSize;
            boardRenderer.setLocation(boardX, boardY);
            boardRenderer.setBoardLength(maxSize, maxSize);
            boardRenderer.setupSizeParameters();
            boardRenderer.draw(g);

            int vx = boardX;
            int vy = 0;
            int vw = trueWidth;
            int vh = boardY;

            if (backgroundG.isPresent()) {
              bufferedContainer = drawContainer(vx, vy, vw, vh / 2);
            }
            //        if (backgroundG.isPresent()) {
            //          drawContainer(
            //              backgroundG.get(), vx, vy, vw, vh);
            //        }
            if (!noVariation) {
              if (noWinrate && noBasic) {
                if (bufferedContainer != null) {
                  g.drawImage(bufferedContainer, vx, vy, null);
                  g.drawImage(bufferedContainer, vx, vy + vh / 2, null);
                }
                if (!noCommentAndListPane) {
                  if (noComment) setListScrollpane(vx, vy, vw / 2, vh);
                  else if (noListPane) drawComment(g, vx, vy, vw / 2, vh);
                  createVarTreeImage(vx + vw / 2, vy, vw / 2, vh, g);
                } else createVarTreeImage(vx, vy, vw, vh, g);
              } else {
                if (bufferedContainer != null)
                  g.drawImage(bufferedContainer, vx, vy + vh / 2, null);
                if (!noCommentAndListPane) {
                  if (noComment) setListScrollpane(vx, vy + vh / 2, vw / 2, vh / 2);
                  else if (noListPane) drawComment(g, vx, vy + vh / 2, vw / 2, vh / 2);
                  createVarTreeImage(vx + vw / 2, vy + vh / 2, vw / 2, vh / 2, g);
                } else createVarTreeImage(vx, vy + vh / 2, vw, vh / 2, g);
              }
            } else if (noWinrate && noBasic) {
              if (bufferedContainer != null) {
                g.drawImage(bufferedContainer, vx, vy, null);
                g.drawImage(bufferedContainer, vx, vy + vh / 2, null);
              }
              if (noComment) setListScrollpane(vx, vy, vw, vh);
              else if (noListPane) drawComment(g, vx, vy, vw, vh);
            } else {
              if (!noCommentAndListPane) {
                if (bufferedContainer != null)
                  g.drawImage(bufferedContainer, vx, vy + vh / 2, null);
                if (noComment) setListScrollpane(vx, vy + vh / 2, vw, vh / 2);
                else if (noListPane) drawComment(g, vx, vy + vh / 2, vw, vh / 2);
              }
            }

            if (!noWinrate) {
              if (noCommentAndListPane && noVariation) {
                if (bufferedContainer != null) {
                  g.drawImage(bufferedContainer, vx, vy, null);
                  g.drawImage(bufferedContainer, vx, vy + vh / 2, null);
                }

                if (!noBasic) {
                  grx = vx + vw / 2;
                  gry = vy;
                  grw = vw / 2;
                  grh = vh;
                  drawWinratePane(grx, gry, grw, grh);
                  // winrateGraph.draw(g, grx, gry, grw, grh);
                  statx = vx;
                  staty = vy;
                  statw = vw / 2;
                  stath = vh;
                  drawCaptured(g, statx, staty, statw, stath / 2, true);
                  drawMoveStatistics(g, statx, staty + stath / 2, statw, stath / 2);
                } else {
                  grx = vx;
                  gry = vy;
                  grw = vw;
                  grh = vh;
                  drawWinratePane(grx, gry, grw, grh);
                  // winrateGraph.draw(g, grx, gry, grw, grh);
                }

              } else {
                if (bufferedContainer != null) g.drawImage(bufferedContainer, vx, vy, null);
                if (!noBasic) {
                  grx = vx + vw / 2;
                  gry = vy;
                  grw = vw / 2;
                  grh = vh / 2;
                  drawWinratePane(grx, gry, grw, grh);
                  // winrateGraph.draw(g, grx, gry, grw, grh);
                  statx = vx;
                  staty = vy;
                  statw = vw / 2;
                  stath = vh / 2;
                  drawCaptured(g, statx, staty, statw, stath / 2, true);
                  drawMoveStatistics(g, statx, staty + stath / 2, statw, stath / 2);
                } else {
                  grx = vx;
                  gry = vy;
                  grw = vw;
                  grh = vh / 2;
                  drawWinratePane(grx, gry, grw, grh);
                  // winrateGraph.draw(g, grx, gry, grw, grh);
                }
              }
            } else if (!noBasic) {

              if (noCommentAndListPane && noVariation) {
                if (bufferedContainer != null) {
                  g.drawImage(bufferedContainer, vx, vy, null);
                  g.drawImage(bufferedContainer, vx, vy + vh / 2, null);
                }
                statx = vx;
                staty = vy;
                statw = vw;
                stath = vh;
                drawCaptured(g, statx, staty, statw, stath / 2, true);
                drawMoveStatistics(g, statx, staty + stath / 2, statw, stath / 2);
              } else {
                if (bufferedContainer != null) g.drawImage(bufferedContainer, vx, vy, null);
                statx = vx;
                staty = vy;
                statw = vw;
                stath = vh / 2;
                drawCaptured(g, statx, staty, statw, stath / 2, true);
                drawMoveStatistics(g, statx, staty + stath / 2, statw, stath / 2);
              }
            }
          }
        } else if (extraMode == 2) {
          int topInset = mainPanel.getInsets().top;
          int leftInset = mainPanel.getInsets().left;
          int rightInset = mainPanel.getInsets().right;
          int bottomInset = mainPanel.getInsets().bottom; // + this.getJMenuBar().getHeight();

          int trueWidth = width - leftInset - rightInset;
          int trueHeight = height - topInset - bottomInset;
          // board
          //   cachedImage = new BufferedImage(width, height, TYPE_INT_ARGB);
          //       Graphics2D g = (Graphics2D) cachedImage.getGraphics();
          //      g.setRenderingHint(RenderingHints.KEY_RENDERING,
          // RenderingHints.VALUE_RENDER_QUALITY);
          maxSize = (int) (min(trueWidth / 2, trueHeight - 20));
          maxSize = max(maxSize, max(Board.boardWidth, Board.boardHeight) + 5);
          boardX = leftInset; // (width - maxSize) / 8 * BoardPositionProportion;
          boardY = topInset;
          boardRenderer.setLocation(boardX, boardY);
          boardRenderer.setBoardLength(maxSize, maxSize);
          boardRenderer.setupSizeParameters();
          boardRenderer.draw(g);

          int maxSize2 = maxSize;
          int boardX2 = maxSize2 + leftInset; // (width - maxSize) / 8 * BoardPositionProportion;
          int boardY2 = topInset;
          boardRenderer2.setLocation(boardX2, boardY2);
          boardRenderer2.setBoardLength(maxSize2, maxSize2);
          boardRenderer2.setupSizeParameters();
          boardRenderer2.draw(g);

          int commentX1 = 0;
          int commentX2 = 0;

          String statusKey = "LizzieFrame.display." + (Lizzie.leelaz.isPondering() ? "on" : "off");
          String statusText =
              resourceBundle.getString(statusKey)
                  + (Lizzie.config.userKnownX
                      ? ""
                      : resourceBundle.getString("LizzieFrame.display.space"));
          String ponderingText = resourceBundle.getString("LizzieFrame.display.pondering");
          // String switching = resourceBundle.getString("LizzieFrame.prompt.switching");
          //   String switchingText =
          //       !Lizzie.leelaz.isLoaded()&&!Lizzie.leelaz.isNormalEnd ?
          // resourceBundle.getString("LizzieFrame.loading") : "";
          weightText = Lizzie.leelaz.oriEnginename;
          if (weightText.length() > 15) weightText = weightText.substring(0, 10);
          String text1 =
              resourceBundle.getString("LizzieFrame.mainEngine")
                  + weightText
                  + " "
                  + ponderingText
                  + " "
                  + statusText;
          // + " "
          //  + switchingText;

          commentX1 = drawPonderingStateForExtraMode2(g, text1, leftInset, maxSize, 18);
          if (Lizzie.leelaz2 != null) {
            weightText2 = Lizzie.leelaz2.oriEnginename;
            String statusKey2 =
                "LizzieFrame.display." + (Lizzie.leelaz.isPondering() ? "on" : "off");
            String statusText2 = resourceBundle.getString(statusKey2);
            String ponderingText2 = resourceBundle.getString("LizzieFrame.display.pondering");
            //    String switching2 = resourceBundle.getString("LizzieFrame.prompt.switching");
            // String switchingText2 =
            //		  !Lizzie.leelaz2.isLoaded()&&!Lizzie.leelaz2.isNormalEnd ?
            // resourceBundle.getString("LizzieFrame.loading") : "";
            // String weightText2 = Lizzie.leelaz2.currentEnginename;
            if (weightText2.length() > 15) weightText2 = weightText2.substring(0, 10);
            String text2 =
                resourceBundle.getString("LizzieFrame.subEngine")
                    + weightText2
                    + " "
                    + ponderingText2
                    + " "
                    + statusText2;
            //     + " "
            //     + switchingText2;
            commentX2 = drawPonderingStateForExtraMode2(g, text2, maxSize, maxSize, 18);
          } else {
            String text2 = resourceBundle.getString("LizzieFrame.subEngine") + weightText2;
            commentX2 = drawPonderingStateForExtraMode2(g, text2, maxSize, maxSize, 18);
          }
          // if (Lizzie.leelaz != null) {
          // WinrateStats stats = Lizzie.leelaz.getWinrateStats();
          //  if (stats.maxWinrate > 0) {
          //   extraModeWinrate1 = stats.maxWinrate;
          //  }
          String text1comm =
              resourceBundle.getString("LizzieFrame.visits")
                  + getPlayoutsString(Lizzie.board.getData().getPlayouts())
                  + " "
                  + resourceBundle.getString("LizzieFrame.winrate")
                  + String.format("%.1f%%", Lizzie.board.getData().winrate);
          drawPonderingStateForExtraMode2(g, text1comm, leftInset + commentX1 + 5, maxSize, 18);
          // }

          // if (Lizzie.leelaz2 != null) {
          //   WinrateStats stats = Lizzie.leelaz2.getWinrateStats();
          //   if (stats.maxWinrate > 0) {
          //      extraModeWinrate2 = stats.maxWinrate;
          //    }
          String text2comm =
              resourceBundle.getString("LizzieFrame.visits")
                  + getPlayoutsString(Lizzie.board.getData().getPlayouts2())
                  + " "
                  + resourceBundle.getString("LizzieFrame.winrate")
                  + String.format("%.1f%%", Lizzie.board.getData().winrate2);
          drawPonderingStateForExtraMode2(
              g, text2comm, maxSize + leftInset + commentX2 + 5, maxSize, 18);
          //  }
        } else if (extraMode == 3) {
          int topInset = mainPanel.getInsets().top;
          int leftInset = mainPanel.getInsets().left;
          int rightInset = mainPanel.getInsets().right;
          int bottomInset = mainPanel.getInsets().bottom; // + this.getJMenuBar().getHeight();
          // int maxBound = Math.max(width, height);

          boolean noWinrate = !Lizzie.config.showWinrateGraph;
          boolean noVariation = !Lizzie.config.showVariationGraph;
          boolean noBasic = !Lizzie.config.showCaptured;
          //   boolean noSubBoard = !Lizzie.config.showSubBoard;
          boolean noComment = !Lizzie.config.showComment || Lizzie.config.showListPane();
          boolean noListPane = !Lizzie.config.showListPane();
          boolean noCommentAndListPane = noComment && noListPane;

          // board
          subMaxSize = (int) (min(width - leftInset - rightInset, height - topInset - bottomInset));
          subMaxSize = max(subMaxSize, max(Board.boardWidth, Board.boardHeight) + 5);
          //         subBoardX = 0;//(width - maxSize) / 8 * BoardPositionProportion;
          //         subBoardY = 0;

          //  cachedImage = new BufferedImage(width, height, TYPE_INT_ARGB);
          // Graphics2D g = (Graphics2D) cachedImage.getGraphics();
          //     g.setRenderingHint(RenderingHints.KEY_RENDERING,
          // RenderingHints.VALUE_RENDER_QUALITY);

          // subBoardRenderer.setLocation( cx,cy);
          // subBoardXmouse = subBoardX;
          // subBoardYmouse = subBoardY;
          //  subBoardLengthmouse = subBoardLength;
          boardRenderer2.setLocation(topInset, leftInset);
          boardRenderer2.setBoardLength(subMaxSize, subMaxSize);
          boardRenderer2.setupSizeParameters();
          boardRenderer2.draw(g);

          //  subBoardLengthmouse = subMaxSize;

          int trueWidth = width - leftInset - rightInset - subMaxSize;
          int trueHeight = height - topInset - bottomInset;

          boolean isWidth = trueWidth * 0.72 > trueHeight;
          if (isWidth) {
            maxSize = (int) (min(trueWidth, trueHeight));
            maxSize = max(maxSize, max(Board.boardWidth, Board.boardHeight) + 5);
            boardX = width - maxSize; // ) / 8 * BoardPositionProportion;
            boardY = trueHeight - maxSize;
            boardRenderer.setLocation(boardX, boardY);
            boardRenderer.setBoardLength(maxSize, maxSize);
            boardRenderer.setupSizeParameters();
            boardRenderer.draw(g);

            int vh = trueHeight;
            int vw = boardX - subMaxSize;
            int vx = subMaxSize;
            int vy = 0;
            if (backgroundG.isPresent()) {
              bufferedContainer = drawContainer(vx, vy, vw, vh / 2);
            }

            if (!noVariation) {
              if (!noCommentAndListPane) {
                if (noWinrate && noBasic) {
                  if (bufferedContainer != null) {
                    g.drawImage(bufferedContainer, vx, vy, null);
                    g.drawImage(bufferedContainer, vx, vy + vh / 2, null);
                  }
                  createVarTreeImage(vx, vy + vh, vw, vh / 2, g);
                  if (noComment) setListScrollpane(vx, vy + vh / 2, vw, vh / 2);
                  else if (noListPane) drawComment(g, vx, vy + vh / 2, vw, vh / 2);
                } else {
                  if (bufferedContainer != null) {
                    g.drawImage(bufferedContainer, vx, vy + vh / 2, null);
                  }
                  createVarTreeImage(vx, vy + vh / 2, vw, vh / 4, g);
                  if (noComment) setListScrollpane(vx, vy + vh * 3 / 4, vw, vh / 4);
                  else if (noListPane) drawComment(g, vx, vy + vh * 3 / 4, vw, vh / 4);
                }
              } else {
                if (noWinrate && noBasic) {
                  if (bufferedContainer != null) {
                    g.drawImage(bufferedContainer, vx, vy, null);
                    g.drawImage(bufferedContainer, vx, vy + vh / 2, null);
                  }
                  createVarTreeImage(vx, vy, vw, vh, g);
                } else {
                  if (bufferedContainer != null) {
                    g.drawImage(bufferedContainer, vx, vy + vh / 2, null);
                  }
                  createVarTreeImage(vx, vy + vh / 2, vw, vh / 2, g);
                }
              }
              // drawComment(g, cx, cy, cw, ch);
              //  variationTree.drawsmall(g, treex, treey, treew, treeh);
            } else if (!noCommentAndListPane) {
              if (noComment) setListScrollpane(vx, vy + vh / 2, vw, vh / 2);
              else if (noListPane) drawComment(g, vx, vy + vh / 2, vw, vh / 2);
            }
            if (!noWinrate) {
              if (bufferedContainer != null) g.drawImage(bufferedContainer, subMaxSize, 0, null);
              if (!noBasic) {
                grw = vw;
                grx = vx;
                gry = vy + vh / 4;
                grh = vh / 4;
                drawWinratePane(grx, gry, grw, grh);
                // winrateGraph.draw(g, grx, gry, grw, grh);
                statx = vx;
                staty = vy;
                statw = vw;
                stath = vh / 4;
                drawMoveStatistics(g, statx, staty + stath / 2, statw, stath / 2);
                drawCaptured(g, statx, staty, statw, stath / 2, true);
              } else {
                grw = vw;
                grx = vx;
                gry = vy;
                grh = vh / 2;
                drawWinratePane(grx, gry, grw, grh);
                // winrateGraph.draw(g, grx, gry, grw, grh);
              }
            } else if (!noBasic) {
              if (bufferedContainer != null) g.drawImage(bufferedContainer, subMaxSize, 0, null);
              statx = vx;
              staty = vy;
              statw = vw;
              stath = vh / 2;
              drawMoveStatistics(g, statx, staty + stath / 2, statw, stath / 2);
              drawCaptured(g, statx, staty, statw, stath / 2, true);
            }

          } else {
            maxSize = (int) (min(trueWidth, 0.77 * trueHeight));
            maxSize = max(maxSize, max(Board.boardWidth, Board.boardHeight) + 5);
            boardX = subMaxSize; // ) / 8 * BoardPositionProportion;
            boardY = trueHeight - maxSize;
            boardRenderer.setLocation(boardX, boardY);
            boardRenderer.setBoardLength(maxSize, maxSize);
            boardRenderer.setupSizeParameters();
            boardRenderer.draw(g);

            int vx = boardX;
            int vy = 0;
            int vw = trueWidth;
            int vh = boardY;

            if (backgroundG.isPresent()) {
              bufferedContainer = drawContainer(vx, vy, vw, vh / 2);
            }
            //        if (backgroundG.isPresent()) {
            //          drawContainer(
            //              backgroundG.get(), vx, vy, vw, vh);
            //        }
            if (!noVariation) {
              if (noWinrate && noBasic) {
                if (bufferedContainer != null) {
                  g.drawImage(bufferedContainer, vx, vy, null);
                  g.drawImage(bufferedContainer, vx, vy + vh / 2, null);
                }
                if (!noCommentAndListPane) {
                  if (noComment) setListScrollpane(vx, vy, vw / 2, vh);
                  else if (noListPane) drawComment(g, vx, vy, vw / 2, vh);
                  createVarTreeImage(vx + vw / 2, vy, vw / 2, vh, g);
                } else {
                  // variationTree.drawsmall(g, vx, vy, vw, vh);//这里
                  createVarTreeImage(vx, vy, vw, vh, g);
                }
              } else {
                if (bufferedContainer != null)
                  g.drawImage(bufferedContainer, vx, vy + vh / 2, null);
                if (!noCommentAndListPane) {
                  if (noComment) setListScrollpane(vx, vy + vh / 2, vw / 2, vh / 2);
                  else if (noListPane) drawComment(g, vx, vy + vh / 2, vw / 2, vh / 2);
                  createVarTreeImage(vx + vw / 2, vy + vh / 2, vw / 2, vh / 2, g);
                } else createVarTreeImage(vx, vy + vh / 2, vw, vh / 2, g);
              }
            } else if (noWinrate && noBasic) {
              if (bufferedContainer != null) {
                g.drawImage(bufferedContainer, vx, vy, null);
                g.drawImage(bufferedContainer, vx, vy + vh / 2, null);
              }
              if (noComment) setListScrollpane(vx, vy, vw, vh);
              else if (noListPane) drawComment(g, vx, vy, vw, vh);
            } else {
              if (!noCommentAndListPane) {
                if (bufferedContainer != null)
                  g.drawImage(bufferedContainer, vx, vy + vh / 2, null);
                if (noComment) setListScrollpane(vx, vy + vh / 2, vw, vh / 2);
                else if (noListPane) drawComment(g, vx, vy + vh / 2, vw, vh / 2);
              }
            }

            if (!noWinrate) {
              if (noCommentAndListPane && noVariation) {
                if (bufferedContainer != null) {
                  g.drawImage(bufferedContainer, vx, vy, null);
                  g.drawImage(bufferedContainer, vx, vy + vh / 2, null);
                }

                if (!noBasic) {
                  grx = vx + vw / 2;
                  gry = vy;
                  grw = vw / 2;
                  grh = vh;
                  drawWinratePane(grx, gry, grw, grh);
                  // winrateGraph.draw(g, grx, gry, grw, grh);
                  statx = vx;
                  staty = vy;
                  statw = vw / 2;
                  stath = vh;
                  drawCaptured(g, statx, staty, statw, stath / 2, true);
                  drawMoveStatistics(g, statx, staty + stath / 2, statw, stath / 2);
                } else {
                  grx = vx;
                  gry = vy;
                  grw = vw;
                  grh = vh;
                  drawWinratePane(grx, gry, grw, grh);
                  // winrateGraph.draw(g, grx, gry, grw, grh);
                }

              } else {
                if (bufferedContainer != null) g.drawImage(bufferedContainer, vx, vy, null);
                if (!noBasic) {
                  grx = vx + vw / 2;
                  gry = vy;
                  grw = vw / 2;
                  grh = vh / 2;
                  drawWinratePane(grx, gry, grw, grh);
                  // winrateGraph.draw(g, grx, gry, grw, grh);
                  statx = vx;
                  staty = vy;
                  statw = vw / 2;
                  stath = vh / 2;
                  drawCaptured(g, statx, staty, statw, stath / 2, true);
                  drawMoveStatistics(g, statx, staty + stath / 2, statw, stath / 2);
                } else {
                  grx = vx;
                  gry = vy;
                  grw = vw;
                  grh = vh / 2;
                  drawWinratePane(grx, gry, grw, grh);
                  // winrateGraph.draw(g, grx, gry, grw, grh);
                }
              }
            } else if (!noBasic) {

              if (noCommentAndListPane && noVariation) {
                if (bufferedContainer != null) {
                  g.drawImage(bufferedContainer, vx, vy, null);
                  g.drawImage(bufferedContainer, vx, vy + vh / 2, null);
                }
                statx = vx;
                staty = vy;
                statw = vw;
                stath = vh;
                drawCaptured(g, statx, staty, statw, stath / 2, true);
                drawMoveStatistics(g, statx, staty + stath / 2, statw, stath / 2);
              } else {
                if (bufferedContainer != null) g.drawImage(bufferedContainer, vx, vy, null);
                statx = vx;
                staty = vy;
                statw = vw;
                stath = vh / 2;
                drawCaptured(g, statx, staty, statw, stath / 2, true);
                drawMoveStatistics(g, statx, staty + stath / 2, statw, stath / 2);
              }
            }
          }
        }
        //  extrmode 8
        else if (extraMode == 8) // 8浮动棋盘模式
        {
          int topInset = mainPanel.getInsets().top;
          int leftInset = mainPanel.getInsets().left;
          int rightInset = mainPanel.getInsets().right;
          int bottomInset = mainPanel.getInsets().bottom;

          boolean noBasic = !Lizzie.config.showCaptured;
          boolean noWinrate = !Lizzie.config.showWinrateGraph;
          boolean noComment = !Lizzie.config.showComment;

          boolean noVariation = !Lizzie.config.showVariationGraph;
          boolean noListPane = !Lizzie.config.showListPane();
          boolean noSubBoard = !Lizzie.config.showSubBoard;

          int trueWidth = width - leftInset - rightInset;
          int trueHeight = height - topInset - bottomInset;

          int vh = trueHeight;
          int vw = trueWidth / 8 * BoardPositionProportion;
          if (noVariation && noListPane && noSubBoard) vw = trueWidth;
          int vx = 0;
          int vy = 0;
          if (this.independentMainBoard != null)
            this.boardRenderer = independentMainBoard.boardRenderer;
          int maxBound = Math.max(width, height);
          int ponderingX = leftInset;
          double ponderingSize = Lizzie.config.userKnownX ? 0.025 : 0.04;
          maxSize = (int) (min(width - leftInset - rightInset, height - topInset - bottomInset));

          int ponderingY =
              height - bottomInset; // - (int) (maxSize * 0.023) - (int) (maxBound * ponderingSize);
          int ponderingY2 =
              height - bottomInset; // - (int) (maxSize * 0.023) - (int) (maxBound * ponderingSize *
          // 0.4);
          if (Lizzie.config.showStatus) {
            ponderingY = ponderingY - (int) (maxSize * 0.023) - (int) (maxBound * ponderingSize);
            ponderingY2 =
                ponderingY2
                    - (int) (maxSize * 0.023)
                    - (int) (maxBound * ponderingSize * (Lizzie.config.userKnownX ? 0.3 : 0.4));
          }
          double loadingSize = 0.03;
          int loadingX = ponderingX;
          int loadingY =
              ponderingY
                  - (int)
                      (maxBound
                          * (loadingSize
                              - ponderingSize * (Lizzie.config.userKnownX ? 1.15 : 0.75)));
          g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
          if (Lizzie.config.showStatus && !Lizzie.config.userKnownX) drawCommandString(g);
          if (Lizzie.config.showStatus) {
            if (Lizzie.leelaz != null && (Lizzie.leelaz.isLoaded() || Lizzie.leelaz.isNormalEnd)) {
              String statusKey =
                  "LizzieFrame.display." + (Lizzie.leelaz.isPondering() ? "on" : "off");
              String statusText =
                  resourceBundle.getString(statusKey)
                      + (Lizzie.config.userKnownX
                          ? ""
                          : resourceBundle.getString("LizzieFrame.display.space"));
              String ponderingText = resourceBundle.getString("LizzieFrame.display.pondering");
              //            String switching =
              // resourceBundle.getString("LizzieFrame.prompt.switching");
              //            String switchingText = Lizzie.leelaz.switching() ? switching : "";
              String weightText = "";
              if (Lizzie.engineManager.isEmpty)
                weightText = resourceBundle.getString("LizzieFrame.noEngineText");
              else weightText = Lizzie.leelaz.oriEnginename;
              String text2 = ponderingText + " " + statusText; // + " " + switchingText;
              drawPonderingState(
                  g, weightText, text2, ponderingX, ponderingY, ponderingY2, ponderingSize);
              vh = ponderingY;
            } else {
              String loadingText = getLoadingText();
              drawPonderingState(g, loadingText, loadingX, loadingY, loadingSize);
              vh = loadingY;
            }
          }
          if (backgroundG.isPresent()) {
            drawContainer(backgroundG.get(), vx, vy, vw, vh);
          }
          if (!noBasic) {
            if (noComment && noWinrate) {
              statx = vx;
              staty = vy;
              statw = vw;
              stath = vh;
              drawMoveStatistics(g, statx, staty + stath / 2, statw, stath / 2);
              drawCaptured(g, statx, staty, statw, stath / 2, false);
            } else if (noComment || noWinrate) {
              statx = vx;
              staty = vy;
              statw = vw;
              stath = vh / 2;
              drawMoveStatistics(g, statx, staty + stath / 2, statw, stath / 2);
              drawCaptured(g, statx, staty, statw, stath / 2, false);
            } else {
              statx = vx;
              staty = vy;
              statw = vw;
              stath = vh / 3;
              drawMoveStatistics(g, statx, staty + stath / 2, statw, stath / 2);
              drawCaptured(g, statx, staty, statw, stath / 2, false);
            }
          }

          if (!noWinrate) {
            if (noComment && noBasic) {
              grw = vw;
              grx = vx;
              gry = vy;
              grh = vh;
            } else if (noComment || noBasic) {
              if (noComment) {
                grw = vw;
                grx = vx;
                gry = vy + vh / 2;
                grh = vh / 2;
              } else {
                grw = vw;
                grx = vx;
                gry = vy;
                grh = vh / 2;
              }
            } else {
              grw = vw;
              grx = vx;
              gry = vy + vh / 3;
              grh = vh / 3;
            }
          }

          if (!noComment) {
            if (noWinrate && noBasic) drawComment(g, vx, vy, vw, vh);
            else if (noWinrate || noBasic) drawComment(g, vx, vy + vh / 2, vw, vh / 2);
            else drawComment(g, vx, vy + vh * 2 / 3, vw, vh * 1 / 3);
          }

          vh = trueHeight;
          if (noBasic && noWinrate && noComment) vw = trueWidth;
          else vw = trueWidth - trueWidth / 8 * BoardPositionProportion;
          vx = trueWidth - vw;
          vy = 0;

          int subBoardLength = 0;
          if (!noSubBoard) {
            int subBoardX = 0;
            int subBoardY = 0;
            if (noSubBoard && noVariation) {
              subBoardX = vx;
              subBoardY = vy;
              subBoardLength = Math.min(vw, vh);
            } else {
              subBoardX = vx;
              subBoardLength = Math.min(vw, vh * 3 / 4);
              subBoardY = vh - subBoardLength;
            }
            subBoardRenderer.setLocation(subBoardX, subBoardY);
            subBoardRenderer.setBoardLength(subBoardLength, subBoardLength);

            subBoardXmouse = subBoardX;
            subBoardYmouse = subBoardY;
            subBoardLengthmouse = subBoardLength;
            subBoardRenderer.setupSizeParameters();
            subBoardRenderer.draw(g);
          }

          if (!noVariation) {
            if (noSubBoard) {
              if (noListPane) {
                createVarTreeImage(vx, vy, vw, vh, g);
              } else {
                createVarTreeImage(vx, vy, vw, vh / 2, g);
              }
            } else {
              if (noListPane) {
                createVarTreeImage(vx, vy, vw, vh - subBoardLength, g);
              } else {
                createVarTreeImage(vx, vy, vw, (vh - subBoardLength) / 2, g);
              }
            }
          }

          if (!noListPane) {
            if (noSubBoard) {
              if (noVariation) {
                setListScrollpane(vx, vy, vw, vh);
              } else {
                setListScrollpane(vx, vy + vh / 2, vw, vh / 2);
              }
            } else {
              if (noVariation) {
                setListScrollpane(vx, vy, vw, vh - subBoardLength);
              } else {
                setListScrollpane(
                    vx, vy + (vh - subBoardLength) / 2, vw, (vh - subBoardLength) / 2);
              }
            }
          }
          if (!noWinrate) {
            drawWinratePane(grx, gry, grw, grh);
          }
        } else {
          // layout parameters

          int topInset = mainPanel.getInsets().top;
          int leftInset = mainPanel.getInsets().left;
          int rightInset = mainPanel.getInsets().right;
          int bottomInset = mainPanel.getInsets().bottom; // + this.getJMenuBar().getHeight();
          int maxBound = Math.max(width, height);

          //      boolean noWinrate = !Lizzie.config.showWinrate;
          boolean showListPane = Lizzie.config.showListPane();
          boolean noVariation = !Lizzie.config.showVariationGraph && !showListPane;
          //  boolean noBasic = !Lizzie.config.showCaptured;
          boolean noSubBoard = !Lizzie.config.showSubBoard;
          boolean noComment = !Lizzie.config.showComment;
          boolean isLargeSubboard =
              Lizzie.config.showLargeSubBoard() && !Lizzie.config.largeWinrateGraph;
          // board
          maxSize = (int) (min(width - leftInset - rightInset, height - topInset - bottomInset));
          maxSize = max(maxSize, max(Board.boardWidth, Board.boardHeight) + 5);
          boardX = (width - maxSize) / 8 * BoardPositionProportion;
          boardY = topInset + (height - topInset - bottomInset - maxSize) / 2;

          int panelMargin = (int) (maxSize * 0.02);

          // captured stones
          int capx = leftInset;
          int capy = topInset;
          int capw = boardX - panelMargin - leftInset;
          int caph = boardY + maxSize / 8 - topInset;

          // move statistics (winrate bar)
          // boardX equals width of space on each side
          statx = capx;
          staty = capy + caph;
          statw = capw;
          stath = maxSize / 10;

          // winrate graph
          grx = statx;
          gry = staty + stath;
          grw = statw;
          grh = maxSize / 3;

          // variation tree container
          int vx = boardX + maxSize + panelMargin;
          int vy = capy;
          int vw = width - vx - rightInset;
          int vh = height - vy - bottomInset;

          // pondering message
          double ponderingSize = Lizzie.config.userKnownX ? 0.025 : 0.04;
          int ponderingX = leftInset;

          int ponderingY =
              height - bottomInset; // - (int) (maxSize * 0.023) - (int) (maxBound * ponderingSize);
          int ponderingY2 =
              height - bottomInset; // - (int) (maxSize * 0.023) - (int) (maxBound * ponderingSize *
          // 0.4);
          if (Lizzie.config.showStatus) {
            ponderingY = ponderingY - (int) (maxSize * 0.023) - (int) (maxBound * ponderingSize);
            ponderingY2 =
                ponderingY2
                    - (int) (maxSize * 0.023)
                    - (int) (maxBound * ponderingSize * (Lizzie.config.userKnownX ? 0.3 : 0.4));
          }
          // dynamic komi
          // double dynamicKomiSize = .02;
          // int dynamicKomiX = leftInset;
          // int dynamicKomiY = ponderingY - (int) (maxBound * dynamicKomiSize);
          // int dynamicKomiLabelX = leftInset;
          // int dynamicKomiLabelY = dynamicKomiY - (int) (maxBound * dynamicKomiSize);

          // loading message;
          double loadingSize = 0.03;
          int loadingX = ponderingX;
          int loadingY =
              ponderingY
                  - (int)
                      (maxBound
                          * (loadingSize
                              - ponderingSize * (Lizzie.config.userKnownX ? 1.15 : 0.75)));

          // subboard
          int subBoardY = gry + grh;
          int subBoardWidth = grw;
          int subBoardHeight = ponderingY - subBoardY;
          int subBoardLength = min(subBoardWidth, subBoardHeight);
          int subBoardX = statx + (statw - subBoardLength) / 2;
          boolean isWidthMode = width >= height;

          if (isWidthMode) {
            // Landscape mode
            if (Lizzie.config.showLargeSubBoard()) {
              boardX = width - maxSize - panelMargin;
              int spaceW = boardX - panelMargin - leftInset;
              int spaceH = height - topInset - bottomInset;
              int panelW = spaceW / 2;
              int panelH = spaceH * 2 / 7;

              // captured stones
              capw = (noVariation && noComment) ? spaceW : panelW;
              caph = (int) (panelH * 0.2);
              // move statistics (winrate bar)
              staty = capy + caph;
              statw = capw;
              stath = (int) (panelH * 0.33);
              // winrate graph
              gry = staty + stath;
              grw = spaceW;
              grh = panelH - caph - stath;
              //              if (noComment && !Lizzie.config.showVariationGraph) {
              //                grw = grw * 2;
              //              }
              // variation tree container
              vx = statx + statw;
              vw = panelW;
              vh = stath + caph;
              // subboard
              subBoardY = gry + grh;
              subBoardWidth = spaceW;
              subBoardHeight = ponderingY - subBoardY;
              subBoardLength = Math.min(subBoardWidth, subBoardHeight);
              if (subBoardHeight > subBoardWidth) {
                subBoardY = subBoardY + subBoardHeight - subBoardWidth;
                panelH = spaceH * 2 / 7 + (subBoardHeight - subBoardWidth);
                caph = (int) (panelH * 0.2);
                staty = capy + caph;
                stath = (int) (panelH * 0.33);
                gry = staty + stath;
                // staty=staty+(subBoardHeight-subBoardWidth);
                grh = panelH - caph - stath;
                vh = stath + caph;
              }
              subBoardX = statx + (spaceW - subBoardLength) / 2;
              isSmallCap = true;
            } else if (Lizzie.config.showLargeWinrate()) {
              boardX = width - maxSize - panelMargin;
              int spaceW = boardX - panelMargin - leftInset;
              int spaceH = height - topInset - bottomInset;
              int panelW = spaceW / 2;
              int panelH = spaceH / 4;

              // captured stones
              capy = topInset + panelH + 1;
              capw = spaceW;
              caph = (int) ((ponderingY - topInset - panelH) * 0.15);
              // move statistics (winrate bar)
              staty = capy + caph;
              statw = capw;
              stath = caph;
              // winrate graph
              gry = staty + stath;
              grw = statw;
              grh = ponderingY - gry;
              // variation tree container
              vx = leftInset + panelW;
              vw = panelW;
              vh = panelH;
              // subboard
              subBoardY = topInset;
              subBoardWidth = panelW - leftInset;
              subBoardHeight = panelH;
              subBoardLength = Math.min(subBoardWidth, subBoardHeight);
              subBoardX = statx + (vw - subBoardLength) / 2;
            }

            // graph container
            int contx = statx;
            int conty = staty;
            int contw = statw;
            int conth = stath + grh;
            // variation tree
            //            if (!Lizzie.config.showWinrateGraph &&
            // (Lizzie.config.showLargeSubBoard())) {
            //              vh = vh + grh;
            //            }
            int treex = vx;
            int treey = vy;
            int treew = vw;
            int treeh = vh;

            // comment panel
            int cx = vx, cy = vy, cw = vw, ch = vh;
            if (Lizzie.config.showComment) {
              if (Lizzie.config.showVariationGraph || showListPane) {
                treeh = vh / 2;
                cy = vy + treeh;
                ch = treeh;
              }

              if (!Lizzie.config.showLargeSubBoard()) {
                int tempx = cx;
                int tempy = cy;
                int tempw = cw;
                int temph = ch;
                if (subBoardWidth > subBoardHeight) {
                  cx = subBoardX - (subBoardWidth - subBoardHeight) / 2;
                } else {
                  cx = subBoardX;
                }
                cy = subBoardY;
                cw = subBoardWidth;
                ch = subBoardHeight;
                subBoardX = tempx;
                subBoardY = tempy;
                subBoardLength = Math.min(tempw, temph);
              }
              // super.paintComponents(g0);
            }

            // initialize

            //    cachedImage = new BufferedImage(width, height, TYPE_INT_ARGB);
            //     Graphics2D g = (Graphics2D) cachedImage.getGraphics();
            //     g.setRenderingHint(RenderingHints.KEY_RENDERING,
            // RenderingHints.VALUE_RENDER_QUALITY);

            if (Lizzie.config.showStatus
                && Lizzie.config.extraMode != 7
                && !Lizzie.config.userKnownX) drawCommandString(g);
            //
            //          if (boardPos != boardX + maxSize / 2) {
            //            boardPos = boardX + maxSize / 2;
            //            //   toolbar.setButtonLocation((int) (boardPos - 22));
            //          }
            if (Lizzie.config.showWinrateGraph) {
              if (Lizzie.config.showLargeSubBoard()
                  && noComment
                  && noVariation
                  && noVariation
                  && !showListPane
                  && !Lizzie.config.showCaptured) {
                staty -= caph;
              }
              drawMoveStatistics(g, statx, staty, statw, stath);
            }
            boardRenderer.setLocation(boardX, boardY);
            boardRenderer.setBoardLength(maxSize, maxSize);
            boardRenderer.setupSizeParameters();
            boardRenderer.draw(g);
            if (!Lizzie.config.showLargeSubBoard() && !Lizzie.config.showLargeWinrate()) {
              // treeh = vh/2;
              if (Lizzie.config.showSubBoard && Lizzie.config.showComment) {
                treeh = treeh + vh / 2 - subBoardLength;
                if (noVariation) subBoardY = subBoardY + vh - subBoardLength;
                else subBoardY = subBoardY + vh / 2 - subBoardLength;
              }
            }
            if (backgroundG.isPresent()) {
              if (Lizzie.config.showWinrateGraph) {
                if (Lizzie.config.showLargeSubBoard()
                    && noComment
                    && noVariation
                    && noVariation
                    && !showListPane
                    && !Lizzie.config.showCaptured) {
                  drawContainer(backgroundG.get(), contx, 0, grw, conth + caph);
                } else {
                  if (isSmallCap) {
                    drawContainer(backgroundG.get(), contx, conty, grw, conth);
                  } else drawContainer(backgroundG.get(), contx, conty, contw, conth);
                }
              }
              //        if (!Lizzie.config.showLargeSubBoard() && !Lizzie.config.showLargeWinrate())
              // {
              //          treeh = vh;
              //        }
              if (Lizzie.config.showVariationGraph || showListPane) {
                if (!Lizzie.config.showSubBoard && Lizzie.config.showComment) treeh = vh;
                drawContainer(backgroundG.get(), vx, vy, vw, treeh);
              }
              //        {

              //          drawContainer(backgroundG.get(), vx, vy, vw, vh);
              //        	else if(Lizzie.config.showComment)
              //        		  drawContainer(backgroundG.get(), vx, vy, vw, vh);
              //        }
              if (Lizzie.config.showComment) drawContainer(backgroundG.get(), cx, cy, cw, ch);
              if (Lizzie.config.showCaptured) {
                if (Lizzie.config.showLargeSubBoard()
                    && !noSubBoard
                    && !Lizzie.config.showWinrateGraph)
                  drawContainer(backgroundG.get(), capx, capy, capw, treeh);
                else drawContainer(backgroundG.get(), capx, capy, capw, caph);
              }
            }
            // if (Lizzie.leelaz != null && Lizzie.leelaz.isLoaded()) {
            if (Lizzie.config.showStatus && Lizzie.config.extraMode != 7) {
              if (Lizzie.leelaz != null
                  && (Lizzie.leelaz.isLoaded() || Lizzie.leelaz.isNormalEnd)) {
                String statusKey =
                    "LizzieFrame.display." + (Lizzie.leelaz.isPondering() ? "on" : "off");
                String statusText =
                    resourceBundle.getString(statusKey)
                        + (Lizzie.config.userKnownX
                            ? ""
                            : resourceBundle.getString("LizzieFrame.display.space"));
                String ponderingText = resourceBundle.getString("LizzieFrame.display.pondering");
                //   String switching = resourceBundle.getString("LizzieFrame.prompt.switching");
                // String switchingText = Lizzie.leelaz.switching() ? switching : "";
                String weightText = "";
                if (Lizzie.engineManager.isEmpty)
                  weightText = resourceBundle.getString("LizzieFrame.noEngineText");
                else weightText = Lizzie.leelaz.oriEnginename;
                String text2 = ponderingText + " " + statusText; // + " " + switchingText;
                drawPonderingState(
                    g, weightText, text2, ponderingX, ponderingY, ponderingY2, ponderingSize);
              } else {
                String loadingText = getLoadingText();
                drawPonderingState(g, loadingText, loadingX, loadingY, loadingSize);
              }
            }

            if (firstTime) {
              // toolbar.setAllUnfocuse();
              firstTime = false;
            }
            // Optional<String> dynamicKomi = Lizzie.leelaz.getDynamicKomi();
            // if (Lizzie.config.showDynamicKomi && dynamicKomi.isPresent()) {
            // String text = resourceBundle.getString("LizzieFrame.display.dynamic-komi");
            // drawPonderingState(g, text, dynamicKomiLabelX, dynamicKomiLabelY,
            // dynamicKomiSize);
            // drawPonderingState(g, dynamicKomi.get(), dynamicKomiX, dynamicKomiY,
            // dynamicKomiSize);
            // }

            // Todo: Make board move over when there is no space beside the board
            if (Lizzie.config.showCaptured) {
              if (Lizzie.config.showLargeSubBoard()
                  && !noSubBoard
                  && !Lizzie.config.showWinrateGraph)
                drawCaptured(g, capx, capy, capw, treeh, isSmallCap);
              else drawCaptured(g, capx, capy, capw, caph, isSmallCap);
            }
            // dcl

            if (Lizzie.config.showVariationGraph || showListPane || Lizzie.config.showComment) {
              // if (backgroundG.isPresent()) {
              // drawContainer(backgroundG.get(), vx, vy, vw, vh);
              // }
              if (Lizzie.config.showVariationGraph || showListPane) {
                if (!Lizzie.config.showLargeSubBoard() && !Lizzie.config.showLargeWinrate()) {
                  if ((Lizzie.config.showSubBoard && !Lizzie.config.showComment)) treeh = vh;
                }
                if (!Lizzie.config.showSubBoard && Lizzie.config.showComment) treeh = vh;

                if (showListPane && !isLargeSubboard) {
                  if (Lizzie.config.showVariationGraph) {
                    treeh = treeh / 2;
                    setListScrollpane(treex, treey + treeh, treew, treeh);
                  } else {
                    setListScrollpane(treex, treey, treew, treeh);
                  }
                }
                //            if (isSmallCap) {
                //              createVarTreeImage(treex, treey, treew, treeh);
                //            } else
                // drawVariationTree(g, treex, treey, treew, treeh);
                if ((Lizzie.config.showLargeSubBoard() || Lizzie.config.showLargeWinrate())
                    && !Lizzie.config.showCaptured)
                  createVarTreeImage(treex - treew, treey, treew * 2, treeh, g);
                else createVarTreeImage(treex, treey, treew, treeh, g);
              }

              if (Lizzie.config.showComment) {
                if (Lizzie.config.showLargeSubBoard()) {
                  if (!noSubBoard) {
                    if (!Lizzie.config.showVariationGraph && showListPane) {
                      cy = ch; // bbb
                      // ch = ch * 2;
                    }
                    if (!Lizzie.config.showWinrateGraph) {
                      cx = cx - cw;
                      cw = cw * 2;
                    }
                  }
                }
                drawComment(g, cx, cy, cw, ch);
              }
            }
            // 更改布局为大棋盘,一整条分支列表,小棋盘,评论放在左下,做到这里
            if (Lizzie.config.showSubBoard) {
              try {

                subBoardRenderer.setLocation(subBoardX, subBoardY);
                // subBoardRenderer.setLocation( cx,cy);
                subBoardRenderer.setBoardLength(subBoardLength, subBoardLength);

                subBoardXmouse = subBoardX;
                subBoardYmouse = subBoardY;
                subBoardLengthmouse = subBoardLength;
                subBoardRenderer.setupSizeParameters();
                subBoardRenderer.draw(g);

              } catch (Exception e) {
                // This can happen when no space is left for subboard.
              }
            }
            if (Lizzie.config.showWinrateGraph) {
              // drawMoveStatistics(g, statx, staty, statw, stath);
              // if (backgroundG.isPresent()) {
              // if (isSmallCap) {
              // contw = contw + contw;
              // }
              // drawContainer(backgroundG.get(), contx, conty, contw, conth);
              // }
              if (showListPane && isLargeSubboard) {
                if (!Lizzie.config.showVariationGraph) {
                  if (noComment) setListScrollpane(vx, vy, vw, vh);
                  else setListScrollpane(grx + grw / 2, 0, grw / 2, ch); // bbb
                } else {
                  setListScrollpane(grx + grw / 2, gry, grw / 2, grh);
                  grw = grw / 2;
                }
              }
              if (Lizzie.config.showLargeSubBoard()
                  && noComment
                  && noVariation
                  && noVariation
                  && !showListPane
                  && !Lizzie.config.showCaptured) {
                gry -= caph;
                grh += caph;
              }
              drawWinratePane(grx, gry, grw, grh);
              //  winrateGraph.draw(g, grx, gry, grw, grh);
              //  }
            } else if (isLargeSubboard) {
              setListScrollpane(grx, gry, grw, grh);
            }
          } else {
            // Portrait mode
            boardY = (height - maxSize + topInset - bottomInset) / 2;
            int spaceW = width - leftInset - rightInset;
            int spaceH = boardY - topInset;
            int panelW = spaceW / 2;
            int panelH = spaceH / 2;
            // subboard
            subBoardLength = Math.min(spaceW, spaceH);
            subBoardX = spaceW - subBoardLength;
            subBoardWidth = subBoardLength;
            subBoardHeight = subBoardLength;
            subBoardY = capy + (boardY - topInset - subBoardLength) / 2;

            // captured stones
            capw = (spaceW - subBoardLength) / 2;
            caph = panelH * 4 / 5;
            // move statistics (winrate bar)
            statx = capx + capw;
            staty = capy;
            statw = capw;
            stath = caph;
            // winrate graph
            grx = capx;
            gry = staty + stath;
            grw = spaceW - subBoardLength;
            grh = boardY - gry;
            if (!Lizzie.config.showSubBoard) {

              grw = spaceW;
              capw = spaceW / 2;
              statw = capw;
              statx = capx + capw;
            }
            if (!Lizzie.config.showCaptured) {
              statx = capx;
              statw = spaceW;
            }
            if (!Lizzie.config.showWinrateGraph) {
              capw = grw;
              caph = spaceH;
            }
            // variation tree container
            vx = leftInset + panelW;
            vy = boardY + maxSize;
            vw = panelW;
            vh = height - vy - bottomInset;
            // graph container
            int contx = statx;
            int conty = staty;
            int contw = statw;
            int conth = stath + grh;
            // variation tree

            int treex = leftInset;
            int treey = vy;
            int treew = spaceW;
            int treeh = vh;
            if (Lizzie.config.showComment) {
              treew = spaceW * 6 / 10;
              treex = leftInset + spaceW * 4 / 10;
            }
            // comment panel
            int cx = capx, cy = vy, cw = spaceW, ch = vh;
            if (Lizzie.config.showVariationGraph || showListPane) cw = spaceW * 4 / 10;
            if (Lizzie.config.showStatus
                && Lizzie.config.extraMode != 7
                && !Lizzie.config.userKnownX) drawCommandString(g);

            if (Lizzie.config.showWinrateGraph) {
              drawMoveStatistics(g, statx, staty, statw, stath);
            }

            if (Lizzie.config.showStatus && Lizzie.config.extraMode != 7) {
              if (Lizzie.leelaz != null && Lizzie.leelaz.isLoaded()) {
                String statusKey =
                    "LizzieFrame.display." + (Lizzie.leelaz.isPondering() ? "on" : "off");
                String statusText =
                    resourceBundle.getString(statusKey)
                        + (Lizzie.config.userKnownX
                            ? ""
                            : resourceBundle.getString("LizzieFrame.display.space"));
                String ponderingText = resourceBundle.getString("LizzieFrame.display.pondering");
                //      String switching = resourceBundle.getString("LizzieFrame.prompt.switching");
                // String switchingText = Lizzie.leelaz.switching() ? switching : "";
                String weightText = "";
                if (Lizzie.engineManager.isEmpty)
                  weightText = resourceBundle.getString("LizzieFrame.noEngineText");
                else weightText = Lizzie.leelaz.oriEnginename;
                String text2 = ponderingText + " " + statusText; // + " " + switchingText;
                drawPonderingState(
                    g, weightText, text2, ponderingX, ponderingY, ponderingY2, ponderingSize);
              }
            }
            boardRenderer.setLocation(boardX, boardY);
            boardRenderer.setBoardLength(maxSize, maxSize);
            boardRenderer.setupSizeParameters();
            boardRenderer.draw(g);
            if (backgroundG.isPresent()) {
              drawContainer(backgroundG.get(), capx, capy, spaceW, spaceH);
              drawContainer(backgroundG.get(), leftInset, vy, spaceW, vh);
            }
            // if (Lizzie.leelaz != null && Lizzie.leelaz.isLoaded()) {
            if (Lizzie.config.showStatus && Lizzie.config.extraMode != 7) {
              if (Lizzie.leelaz == null || !Lizzie.leelaz.isLoaded()) {
                String loadingText = getLoadingText();
                drawPonderingState(g, loadingText, loadingX, loadingY, loadingSize);
              }
            }

            if (firstTime) {
              firstTime = false;
            }
            // Todo: Make board move over when there is no space beside the board
            if (Lizzie.config.showCaptured) {
              drawCaptured(g, capx, capy, capw, caph, isSmallCap);
            }
            // dcl

            if (Lizzie.config.showVariationGraph || showListPane || Lizzie.config.showComment) {
              // if (backgroundG.isPresent()) {
              // drawContainer(backgroundG.get(), vx, vy, vw, vh);
              // }
              if (Lizzie.config.showVariationGraph || showListPane) {
                if (showListPane) {
                  if (Lizzie.config.showVariationGraph) {
                    setListScrollpane(treex, treey, treew / 2, treeh);
                    createVarTreeImage(treex + treew / 2, treey, treew / 2, treeh, g);
                  } else {
                    setListScrollpane(treex, treey, treew, treeh);
                  }
                } else createVarTreeImage(treex, treey, treew, treeh, g);
              }

              if (Lizzie.config.showComment) {
                drawComment(g, cx, cy, cw, ch - (height + topInset - bottomInset - ponderingY));
              }
            }

            if (Lizzie.config.showSubBoard) {
              try {
                subBoardRenderer.setLocation(subBoardX, subBoardY);
                subBoardRenderer.setBoardLength(subBoardLength, subBoardLength);
                subBoardXmouse = subBoardX;
                subBoardYmouse = subBoardY;
                subBoardLengthmouse = subBoardLength;
                subBoardRenderer.setupSizeParameters();
                subBoardRenderer.draw(g);
              } catch (Exception e) {
                // This can happen when no space is left for subboard.
              }
            }
            if (Lizzie.config.showWinrateGraph) {
              drawWinratePane(grx, gry, grw, grh);
            }
          }
        }
        // cleanup
        g.dispose();
        this.cachedImage = cachedImage;
      }
    }
    // draw the image
    // Graphics2D bsGraphics = (Graphics2D) bs.getDrawGraphics();
    // bsGraphics.setRenderingHint(RenderingHints.KEY_RENDERING,
    // RenderingHints.VALUE_RENDER_QUALITY);
    // bsGraphics.drawImage(cachedBackground, 0, 0, null);
    // bsGraphics.drawImage(cachedImage, 0, 0, null);

    // cleanup
    // bsGraphics.dispose();
    // bs.show();

    if (Lizzie.config.isScaled) {
      Graphics2D g1 = (Graphics2D) g0;
      final AffineTransform t = g1.getTransform();
      t.setToScale(1, 1);
      g1.setTransform(t);
      g1.drawImage(
          cachedBackground,
          0,
          Lizzie.config.showTopToolBar
              ? Utils.zoomOut(
                  Lizzie.frame.getJMenuBar().getHeight() * (Lizzie.config.showDoubleMenu ? 2 : 1)
                      + (Lizzie.config.showDoubleMenu
                          ? topPanelHeight - Lizzie.config.menuHeight
                          : 0))
              : Utils.zoomOut(Lizzie.frame.getJMenuBar().getHeight()),
          null);
      g1.drawImage(
          cachedImage,
          0,
          Lizzie.config.showTopToolBar
              ? Utils.zoomOut(
                  Lizzie.frame.getJMenuBar().getHeight() * (Lizzie.config.showDoubleMenu ? 2 : 1)
                      + (Lizzie.config.showDoubleMenu
                          ? topPanelHeight - Lizzie.config.menuHeight
                          : 0))
              : Utils.zoomOut(Lizzie.frame.getJMenuBar().getHeight()),
          null);
      if (Lizzie.config.showWinrateGraph && cachedWinrateImage != null && !showControls)
        g1.drawImage(
            cachedWinrateImage,
            grx,
            gry
                + (Lizzie.config.showTopToolBar
                    ? Utils.zoomOut(
                        Lizzie.frame.getJMenuBar().getHeight()
                                * (Lizzie.config.showDoubleMenu ? 2 : 1)
                            + (Lizzie.config.showDoubleMenu
                                ? topPanelHeight - Lizzie.config.menuHeight
                                : 0))
                    : Utils.zoomOut(Lizzie.frame.getJMenuBar().getHeight())),
            null);
      //      if (extraMode != 8) boardRenderer.drawSuggestion(g1);
      //      if (extraMode == 2) boardRenderer2.drawSuggestion(g1);
    } else {
      g0.drawImage(cachedBackground, 0, 0, null);
      g0.drawImage(cachedImage, 0, 0, null);
      if (Lizzie.config.showWinrateGraph && cachedWinrateImage != null && !showControls)
        g0.drawImage(cachedWinrateImage, grx, gry, null);
    }
    autosaveMaybe();
  }

  private String getLoadingText() {
    // TODO Auto-generated method stub
    if (Lizzie.leelaz.isDownWithError) return resourceBundle.getString("LizzieFrame.display.down");
    else if (Lizzie.leelaz.isTuning) return resourceBundle.getString("LizzieFrame.display.tuning");
    else return resourceBundle.getString("LizzieFrame.display.loading");
  }

  /**
   * temporary measure to refresh background. ideally we shouldn't need this (but we want to release
   * Lizzie 0.5 today, not tomorrow!). Refactor me out please! (you need to get blurring to work
   * properly on startup).
   */
  public void refreshContainer() {
    redrawBackgroundAnyway = true;
    if (extraMode == 8) this.paintMianPanel(mainPanel.getGraphics());
  }

  public void refresh() {
    // 分开各部分刷新,1代表来自info move的刷新
    redrawWinratePaneOnly = false;
    repaint();
    if (independentSubBoard != null && independentSubBoard.isVisible())
      independentSubBoard.refresh();
    if (independentMainBoard != null && independentMainBoard.isVisible())
      independentMainBoard.refresh();
    if (floatBoard != null && floatBoard.isVisible()) floatBoard.refresh();
  }

  public void refresh(int mode) {
    // 分开各部分刷新,1代表来自info move的刷新
    redrawWinratePaneOnly = false;
    switch (mode) {
      case 1:
        refreshFromInfo = true;
        repaint();
      default:
    }
    if (independentSubBoard != null && independentSubBoard.isVisible())
      independentSubBoard.refresh();
    if (independentMainBoard != null && independentMainBoard.isVisible())
      independentMainBoard.refresh();
    if (floatBoard != null && floatBoard.isVisible()) floatBoard.refresh();
  }

  private Graphics2D createBackground(int width, int hight) {
    cachedBackground = new BufferedImage(width, hight, TYPE_INT_RGB);
    cachedBackgroundWidth = cachedBackground.getWidth();
    cachedBackgroundHeight = cachedBackground.getHeight();
    //    cachedBackgroundShowControls = showControls;
    //    cachedShowWinrate = Lizzie.config.showWinrate;
    //    cachedShowVariationGraph = Lizzie.config.showVariationGraph;
    //    cachedShowLargeSubBoard = Lizzie.config.showLargeSubBoard();
    //    cachedLargeWinrate = Lizzie.config.showLargeWinrate();
    //    cachedShowComment = Lizzie.config.showComment;
    //    cachedBoardPositionProportion = BoardPositionProportion;
    boolean needRedrawBackgroundPaint = redrawBackgroundAnyway;
    redrawBackgroundAnyway = false;

    Graphics2D g = cachedBackground.createGraphics();
    if (Lizzie.config.usePureBackground) {
      g.setColor(Lizzie.config.pureBackgroundColor);
      g.fillRect(0, 0, width, hight);
      g.dispose();
      return g;
    }
    g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

    BufferedImage wallpaper = boardRenderer.getWallpaper();
    int drawWidth = max(wallpaper.getWidth(), mainPanel.getWidth());
    int drawHeight = max(wallpaper.getHeight(), mainPanel.getHeight());
    // Support seamless texture
    boardRenderer.drawTextureImage(g, wallpaper, 0, 0, drawWidth, drawHeight, false);
    Lizzie.board.setForceRefresh(true);
    if (backgroundPaint == null || needRedrawBackgroundPaint) {
      BufferedImage result = new BufferedImage(100, 100, TYPE_INT_ARGB);
      filter20.filter(cachedBackground.getSubimage(0, 0, 100, 100), result);
      backgroundPaint =
          new TexturePaint(result, new Rectangle(0, 0, result.getWidth(), result.getHeight()));
    }
    return g;
  }

  private void drawContainer(Graphics g, int vx, int vy, int vw, int vh) {
    if (vw <= 0
        || vh <= 0
        || vx < cachedBackground.getMinX()
        || vx + vw > cachedBackground.getMinX() + cachedBackground.getWidth()
        || vy < cachedBackground.getMinY()
        || vy + vh > cachedBackground.getMinY() + cachedBackground.getHeight()) {
      return;
    }
    BufferedImage result = new BufferedImage(vw, vh, TYPE_INT_ARGB);
    filter20.filter(cachedBackground.getSubimage(vx, vy, vw, vh), result);
    g.drawImage(result, vx, vy, null);
  }

  private BufferedImage drawContainer(int vx, int vy, int vw, int vh) {
    if (Lizzie.config.usePureBackground) return null;
    if (vw <= 0
        || vh <= 0
        || vx < cachedBackground.getMinX()
        || vx + vw > cachedBackground.getMinX() + cachedBackground.getWidth()
        || vy < cachedBackground.getMinY()
        || vy + vh > cachedBackground.getMinY() + cachedBackground.getHeight()) {
      return null;
    }
    BufferedImage result = new BufferedImage(vw, vh, TYPE_INT_ARGB);
    filter20.filter(cachedBackground.getSubimage(vx, vy, vw, vh), result);
    return result;
  }

  private void drawPonderingState(
      Graphics2D g, String text1, String text2, int x, int y, int y2, double size) {
    drawPonderingState(g, text1, x, y, size * (Lizzie.config.userKnownX ? 0.7 : 0.6));
    drawPonderingState2(g, text2, x, y2, size * (Lizzie.config.userKnownX ? 0.75 : 0.4));
  }

  private int drawPonderingStateForExtraMode2(Graphics2D g, String text, int x, int y, int size) {
    if (Lizzie.readMode) {
      return 0;
    }
    int fontSize = size;
    Font font = new Font(Lizzie.config.fontName, Font.PLAIN, fontSize);
    FontMetrics fm = g.getFontMetrics(font);
    int stringWidth = fm.stringWidth(text);
    // Truncate too long text when display switching prompt
    //	    if (Lizzie.leelaz != null && Lizzie.leelaz.isLoaded()) {
    //	      int mainBoardX = boardRenderer.getLocation().x;
    //	      if (mainPanel.getWidth() > mainPanel.getHeight()
    //	          && (mainBoardX > x)
    //	          && stringWidth > (mainBoardX - x)) {
    //	        text = truncateStringByWidth(text, fm, mainBoardX - x);
    //	        stringWidth = fm.stringWidth(text);
    //	      }
    //	    }
    //	    // Do nothing when no text
    //	    if (stringWidth <= 0) {
    //	      return;
    //	    }
    int stringHeight = fm.getAscent() - fm.getDescent();
    int width = max(stringWidth, 1);
    int height = max((int) (stringHeight * 1.2), 1);

    // BufferedImage result = new BufferedImage(width, height, TYPE_INT_ARGB);
    // commenting this out for now... always causing an exception on startup. will
    // fix in the
    // upcoming refactoring
    // filter20.filter(cachedBackground.getSubimage(x, y, result.getWidth(),
    // result.getHeight()), result);
    //    g.drawImage(result, x, y, null);

    g.setColor(new Color(0, 0, 0, 130));
    g.fillRect(x, y, width, height);
    g.drawRect(x, y, width, height);

    g.setColor(Color.white);
    g.setFont(font);
    g.drawString(
        text, x + (width - stringWidth) / 2, y + stringHeight + (height - stringHeight) / 2);
    return stringWidth;
  }

  private void drawPonderingState(Graphics2D g, String text, int x, int y, double size) {
    if (Lizzie.readMode) {
      return;
    }

    int fontSize = (int) (max(mainPanel.getWidth(), mainPanel.getHeight()) * size);
    Font font = new Font(Lizzie.config.fontName, Font.PLAIN, fontSize);
    FontMetrics fm = g.getFontMetrics(font);
    int stringWidth = fm.stringWidth(text);
    // Truncate too long text when display switching prompt
    if (extraMode != 8) {
      if (Lizzie.leelaz != null && Lizzie.leelaz.isLoaded()) {
        int mainBoardX = boardRenderer.getLocation().x;
        if (mainPanel.getWidth() > mainPanel.getHeight()
            && (mainBoardX > x)
            && stringWidth > (mainBoardX - x)) {
          text = truncateStringByWidth(text, fm, mainBoardX - x);
          stringWidth = fm.stringWidth(text);
        }
      }
    }
    // Do nothing when no text
    if (stringWidth <= 0) {
      return;
    }
    int stringHeight = fm.getAscent() - fm.getDescent();
    int width = max(stringWidth, 1);
    int height = max((int) (stringHeight * 1.2), 1);

    //  BufferedImage result = new BufferedImage(width, height, TYPE_INT_ARGB);
    // commenting this out for now... always causing an exception on startup. will
    // fix in the
    // upcoming refactoring
    // filter20.filter(cachedBackground.getSubimage(x, y, result.getWidth(),
    // result.getHeight()), result);
    // g.drawImage(result, x, y, null);

    g.setColor(new Color(0, 0, 0, 130));
    g.fillRect(x, y, width, height);
    g.drawRect(x, y, width, height);

    g.setColor(Color.white);
    g.setFont(font);
    g.drawString(
        text, x + (width - stringWidth) / 2, y + stringHeight + (height - stringHeight) / 2);
  }

  private void drawPonderingState2(Graphics2D g, String text, int x, int y, double size) {
    if (Lizzie.readMode) {
      return;
    }
    int maxWidth = mainPanel.getWidth();
    int maxHeight = mainPanel.getHeight();
    if (maxWidth > maxHeight * 3) maxWidth = maxWidth * 3 / 5;
    else if (maxWidth > maxHeight * 2) maxWidth = maxHeight * 2;
    int fontSize = (int) (max(maxWidth, maxHeight) * size);
    Font font = new Font(Lizzie.config.fontName, Font.PLAIN, fontSize);
    FontMetrics fm = g.getFontMetrics(font);
    int stringWidth = fm.stringWidth(text);
    // Truncate too long text when display switching prompt
    if (extraMode != 8) {
      if (Lizzie.leelaz != null && Lizzie.leelaz.isLoaded()) {
        int mainBoardX = boardRenderer.getLocation().x;
        if (mainPanel.getWidth() > mainPanel.getHeight()
            && (mainBoardX > x)
            && stringWidth > (mainBoardX - x)) {
          text = truncateStringByWidth(text, fm, mainBoardX - x);
          stringWidth = fm.stringWidth(text);
        }
      }
    }
    // Do nothing when no text
    if (stringWidth <= 0) {
      return;
    }
    int stringHeight = fm.getAscent() - fm.getDescent();
    int width = max(stringWidth, 1);
    int height = max((int) (stringHeight * 1.2), 1);

    //  BufferedImage result = new BufferedImage(width, height, TYPE_INT_ARGB);
    // commenting this out for now... always causing an exception on startup. will
    // fix in the
    // upcoming refactoring
    // filter20.filter(cachedBackground.getSubimage(x, y, result.getWidth(),
    // result.getHeight()), result);
    // g.drawImage(result, x, y, null);

    g.setColor(new Color(0, 0, 0, 130));
    g.fillRect(x, y, width, height);
    g.drawRect(x, y, width, height);

    g.setColor(Color.white);
    g.setFont(font);
    g.drawString(
        text, x + (width - stringWidth) / 2, y + stringHeight + (height - stringHeight) / 2);
  }

  /**
   * @return a shorter, rounded string version of playouts. e.g. 345 -> 345, 1265 -> 1.3k, 44556 ->
   *     45k, 133523 -> 134k, 1234567 -> 1.2m
   */
  public String getPlayoutsString(int playouts) {
    //    if (Lizzie.leelaz != null && Lizzie.leelaz.isZen) {
    //      if (playouts < 0) return "库";
    //    }
    if (playouts >= 10_000_000) {
      double playoutsDouble = (double) playouts / 100_000; // 1234567 -> 12.34567
      return round(playoutsDouble) / 10.0 + "m";
    } else if (playouts >= 10_000) {
      double playoutsDouble = (double) playouts / 1_000; // 13265 -> 13.265
      return round(playoutsDouble) + "k";
    } else if (playouts >= 1_000) {
      double playoutsDouble = (double) playouts / 1_000; // 1265 -> 12.65
      return String.format("%.1f", playoutsDouble) + "k"; // round(playoutsDouble) / 10.0 + "k";
    } else {
      return String.valueOf(playouts);
    }
  }

  /**
   * Truncate text that is too long for the given width
   *
   * @param line
   * @param fm
   * @param fitWidth
   * @return fitted
   */
  private static String truncateStringByWidth(String line, FontMetrics fm, int fitWidth) {
    if (line.isEmpty()) {
      return "";
    }
    int width = fm.stringWidth(line);
    if (width > fitWidth) {
      int guess = line.length() * fitWidth / width;
      String before = line.substring(0, guess).trim();
      width = fm.stringWidth(before);
      if (width > fitWidth) {
        int diff = width - fitWidth;
        int i = 0;
        for (; (diff > 0 && i < 5); i++) {
          diff = diff - fm.stringWidth(line.substring(guess - i - 1, guess - i));
        }
        return line.substring(0, guess - i).trim();
      } else {
        return before;
      }
    } else {
      return line;
    }
  }

  public GaussianFilter filter20 = new GaussianFilter(Lizzie.config.backgroundFilter);
  // private GaussianFilter filter10 = new GaussianFilter(10);

  /** Display the controls */
  void drawControls() {
    // userAlreadyKnowsAboutCommandString = true;
    showControlTime = System.currentTimeMillis();
    if (showControls) {
      return;
    }
    cachedImage = new BufferedImage(mainPanel.getWidth(), mainPanel.getHeight(), TYPE_INT_ARGB);

    // redraw background
    // createBackground(mainPanel.getWidth(), mainPanel.getHeight());

    List<String> commandsToShow = new ArrayList<>(Arrays.asList(commands));
    // if (Lizzie.leelaz.getDynamicKomi().isPresent()) {
    // commandsToShow.add(resourceBundle.getString("LizzieFrame.commands.keyD"));
    // }

    Graphics2D g = cachedImage.createGraphics();

    int maxSize = mainPanel.getHeight();
    int fontSize = (int) (maxSize / 1.2 / commandsToShow.size());
    Font font = new Font(Lizzie.config.fontName, Font.PLAIN, fontSize);
    g.setFont(font);

    FontMetrics metrics = g.getFontMetrics(font);
    int maxCmdWidth = commandsToShow.stream().mapToInt(c -> metrics.stringWidth(c)).max().orElse(0);
    int lineHeight = (int) (font.getSize() * 1.22);

    int boxWidth = min((int) (maxCmdWidth * 1.4), mainPanel.getWidth());
    int boxHeight = min(commandsToShow.size() * lineHeight, mainPanel.getHeight());

    int commandsX = min(mainPanel.getWidth() / 2 - boxWidth / 2, mainPanel.getWidth());
    int top = mainPanel.getInsets().top;
    int commandsY =
        top + min((mainPanel.getHeight() - top) / 2 - boxHeight / 2, mainPanel.getHeight() - top);

    //    BufferedImage result = new BufferedImage(boxWidth, boxHeight, TYPE_INT_ARGB);
    //    filter10.filter(
    //        cachedBackground.getSubimage(commandsX, commandsY, boxWidth, boxHeight), result);
    //    g.drawImage(result, commandsX, commandsY, null);

    g.setColor(new Color(0, 0, 0, 130));
    g.fillRect(commandsX, commandsY, boxWidth, boxHeight);
    int strokeRadius = 1;
    g.setStroke(new BasicStroke(strokeRadius == 1 ? strokeRadius : 2 * strokeRadius));

    int verticalLineX = (int) (commandsX + boxWidth * 0.3);
    g.setColor(new Color(0, 0, 0, 60));
    g.drawLine(
        verticalLineX,
        commandsY + 2 * strokeRadius,
        verticalLineX,
        commandsY + boxHeight - 2 * strokeRadius);

    g.setStroke(new BasicStroke(1));

    g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

    g.setColor(Color.WHITE);
    int lineOffset = commandsY;
    for (String command : commandsToShow) {
      String[] split = command.split("\\|");
      g.drawString(
          split[0],
          verticalLineX - metrics.stringWidth(split[0]) - strokeRadius * 4,
          font.getSize() + lineOffset);
      g.drawString(split[1], verticalLineX + strokeRadius * 4, font.getSize() + lineOffset);
      lineOffset += lineHeight;
    }
    showControls = true;
    refreshContainer();
    Lizzie.board.setForceRefresh(true);
  }

  // private boolean userAlreadyKnowsAboutCommandString = false;

  private void drawCommandString(Graphics2D g) {
    // if (userAlreadyKnowsAboutCommandString) return;

    int maxSize = (int) (min(mainPanel.getWidth(), mainPanel.getHeight()) * 0.98);

    Font font = new Font(Lizzie.config.fontName, Font.PLAIN, (int) (maxSize * 0.023));
    String commandString = resourceBundle.getString("LizzieFrame.prompt.showControlsHint");

    int showCommandsHeight = (int) (font.getSize() * 1.1);
    int showCommandsWidth = g.getFontMetrics(font).stringWidth(commandString);
    int showCommandsX = mainPanel.getInsets().left;
    int showCommandsY = mainPanel.getHeight() - showCommandsHeight - mainPanel.getInsets().bottom;
    // - this.getJMenuBar().getHeight();
    g.setColor(new Color(0, 0, 0, 130));
    g.fillRect(showCommandsX, showCommandsY, showCommandsWidth, showCommandsHeight);

    g.setStroke(new BasicStroke(1));

    g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    g.setColor(Color.WHITE);
    g.setFont(font);
    g.drawString(commandString, showCommandsX, showCommandsY + font.getSize());
  }

  private void drawMoveStatistics(Graphics2D g, int posX, int posY, int width, int height) {
    if (width < 10 || height < 5) return;
    g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    if (isInPlayMode()) {
      g.setColor(new Color(0, 0, 0, 130));
      g.fillRect(posX, posY, width, height);
      int strokeRadius = 1;
      g.setStroke(new BasicStroke(strokeRadius == 1 ? strokeRadius : 2 * strokeRadius));
      g.drawLine(
          posX + strokeRadius,
          posY + strokeRadius,
          posX - strokeRadius + width,
          posY + strokeRadius);
      if (isShowingByoTime) {
        String byoString =
            ((this.leftMinuts > 0 || this.leftSeconds > 0)
                    ? (resourceBundle.getString("Byoyomi.time")
                        + this.leftMinuts
                        + ":"
                        + this.leftSeconds
                        + " ")
                    : "")
                + (this.byoSeconds >= 0
                    ? (" "
                        + resourceBundle.getString("Byoyomi.byoyomi")
                        + this.byoSeconds
                        + "("
                        + Lizzie.frame.byoTimes
                        + ")")
                    : "");
        g.setColor(Color.WHITE);
        drawString(
            g, posX, posY + height / 2, uiFont, Font.PLAIN, byoString, height, width, 0, true);
      }
      return;
    }
    if (width < 0 || height < 0) return; // we don't have enough space
    double lastWR = 50; // winrate the previous move
    double lastScore = 0;
    boolean validLastWinrate = false; // whether it was actually calculated
    Optional<BoardHistoryNode> previous =
        Lizzie.board.getHistory().getCurrentHistoryNode().previous();
    if (Lizzie.engineManager.isEngineGame && Lizzie.board.getHistory().getMoveNumber() > 3) {
      previous = Lizzie.board.getHistory().getCurrentHistoryNode().previous().get().previous();
    }

    if (previous.isPresent() && previous.get().getData().getPlayouts() > 0) {
      lastWR = previous.get().getData().winrate;
      lastScore = previous.get().getData().scoreMean;
      validLastWinrate = true;
    }
    if (Lizzie.engineManager.isEngineGame && Lizzie.board.getHistory().getMoveNumber() > 3) {
      lastWR = 100 - lastWR;
    }
    // Leelaz.WinrateStats stats = Lizzie.leelaz.getWinrateStats();
    double curWR =
        Lizzie.board.getHistory().getData().winrate; // stats.maxWinrate; // winrate on this move
    double curScore = Lizzie.board.getHistory().getData().scoreMean;
    boolean validWinrate =
        (Lizzie.board.getHistory().getData().getPlayouts()
            > 0); // and whether it was actually calculated
    if (!validWinrate) {
      curWR = Lizzie.board.getHistory().getData().winrate;
      curScore = Lizzie.board.getHistory().getData().scoreMean;
      validWinrate = Lizzie.board.getHistory().getData().getPlayouts() > 0;
    }
    if (isPlayingAgainstLeelaz
        && playerIsBlack == !Lizzie.board.getHistory().getData().blackToPlay) {
      validWinrate = false;
    }

    if (!validWinrate) {
      curWR = 100 - lastWR; // display last move's winrate for now (with color difference)
      curScore = -lastScore;
    }
    double whiteWR, blackWR;
    if (Lizzie.board.getData().blackToPlay) {
      blackWR = curWR;
    } else {
      blackWR = 100 - curWR;
    }

    whiteWR = 100 - blackWR;

    // Background rectangle
    g.setColor(new Color(0, 0, 0, 130));
    g.fillRect(posX, posY, width, height);

    // border. does not include bottom edge
    int strokeRadius = 1;
    g.setStroke(new BasicStroke(strokeRadius == 1 ? strokeRadius : 2 * strokeRadius));
    g.drawLine(
        posX + strokeRadius, posY + strokeRadius, posX - strokeRadius + width, posY + strokeRadius);
    // resize the box now so it's inside the border
    posX += 2 * strokeRadius;
    posY += 2 * strokeRadius;
    width -= 4 * strokeRadius;
    height -= 4 * strokeRadius;

    // Title
    strokeRadius = 2;
    g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    g.setColor(Color.WHITE);
    // Last move
    // validLastWinrate && validWinrate
    //   if (true) {
    String text = "";
    // if (Lizzie.config.handicapInsteadOfWinrate) {
    // double currHandicapedWR = Lizzie.leelaz.winrateToHandicap(100 - curWR);
    // double lastHandicapedWR = Lizzie.leelaz.winrateToHandicap(lastWR);
    // text = String.format(": %.2f", currHandicapedWR - lastHandicapedWR);
    // } else {

    // }
    if (Lizzie.engineManager.isEngineGame && Lizzie.board.getHistory().getMoveNumber() <= 3) {
      text = "";
    }
    boolean isKataStyle = false;
    if (Lizzie.board.getHistory().getData().isKataData
        || Lizzie.board.getHistory().getData().isSaiData
        || (Lizzie.leelaz.isKatago && !Lizzie.engineManager.isEmpty)
        || (Lizzie.engineManager.isEngineGame
            && (Lizzie.engineManager.engineList.get(
                        Lizzie.engineManager.engineGameInfo.blackEngineIndex)
                    .isKatago
                || Lizzie.engineManager.engineList.get(
                        Lizzie.engineManager.engineGameInfo.whiteEngineIndex)
                    .isKatago))) {
      isKataStyle = true;
      if (!Lizzie.board.getHistory().getData().bestMoves.isEmpty()) {
        double score = Lizzie.board.getHistory().getData().bestMoves.get(0).scoreMean;
        if (Lizzie.board.getHistory().isBlacksTurn()) {
          // if (Lizzie.config.showKataGoBoardScoreMean) {
          score = score + Lizzie.board.getHistory().getGameInfo().getKomi();
          //  }
        } else {
          //  if (Lizzie.config.showKataGoBoardScoreMean) {
          score = -score + Lizzie.board.getHistory().getGameInfo().getKomi();
          //  }
          //  if (Lizzie.config.kataGoScoreMeanAlwaysBlack) {
          //    score = -score;
          //   }
        }
        scoreOnStatic = score;
        scoreStdev = Lizzie.leelaz.scoreStdev;
      } // +"目差:""复杂度:"

      text =
          text
              + resourceBundle.getString("LizzieFrame.scoreLead")
              + String.format("%.1f", scoreOnStatic);
      if (Lizzie.engineManager.isEngineGame && !Lizzie.leelaz.isSai)
        text =
            text
                + " "
                + resourceBundle.getString("LizzieFrame.scoreStdev")
                + String.format("%.1f", scoreStdev)
                + " ";
    }
    if (Lizzie.leelaz.isColorEngine) {
      // "阶段:""贴目:"
      text =
          text
              + resourceBundle.getString("LizzieFrame.scoreStdev")
              + Lizzie.leelaz.stage
              + " "
              + resourceBundle.getString("LizzieFrame.komi")
              + Lizzie.leelaz.komi;
    } // else {
    //  if (!komi.equals("7.5")) text = text + "贴目:" + komi;
    //  }
    //      if (Lizzie.engineManager.isSaveingEngineSGF) {
    //          text =
    //              // "黑:""白:"
    //              text
    //                  + " "
    //                  + resourceBundle.getString("LizzieFrame.black")
    //                  + Lizzie.engineManager.engineList.get(
    //                          Lizzie.engineManager.engineGameInfo.blackEngineIndex)
    //                      .oriEnginename
    //                  + " "
    //                  + resourceBundle.getString("LizzieFrame.white")
    //                  + Lizzie.engineManager.engineList.get(
    //                          Lizzie.engineManager.engineGameInfo.whiteEngineIndex)
    //                      .oriEnginename;
    //      }
    if (Lizzie.engineManager.isEngineGame) {
      drawString(
          g,
          posX,
          posY + height * 17 / 20,
          uiFont,
          Font.PLAIN,
          text,
          height / 4,
          width * 20 / 21,
          0,
          false);
    } else {
      double wr = 100 - lastWR - curWR;
      double score = (-lastScore) - curScore;
      text =
          text
              + " "
              + resourceBundle.getString("LizzieFrame.display.lastMove")
              + ((wr > 0 ? "+" : "-") + String.format("%.1f%%", Math.abs(wr)));
      if (isKataStyle && !Lizzie.engineManager.isEngineGame) {
        text =
            text
                + " "
                + ((score > 0 ? "+" : "-") + String.format("%.1f", Math.abs(score)))
                + resourceBundle.getString("LizzieFrame.pts"); // + "目";
      }
      drawString(
          g,
          posX,
          posY + height * 17 / 20,
          uiFont,
          Font.PLAIN,
          text,
          height / 4,
          width * 20 / 21,
          0,
          false);
    }
    //    }

    if (validWinrate || validLastWinrate) {
      int maxBarwidth = (int) (width);
      int barWidthB = (int) (blackWR * maxBarwidth / 100);
      int barWidthW = (int) (whiteWR * maxBarwidth / 100);
      int barPosY = posY + height / 3;
      int barPosxB = (int) (posX);
      int barPosxW = barPosxB + barWidthB;
      int barHeight = height / 3;

      // Draw winrate bars
      g.fillRect(barPosxW, barPosY, barWidthW, barHeight);
      g.setColor(Color.BLACK);
      g.fillRect(barPosxB, barPosY, barWidthB, barHeight);

      // Show percentage above bars
      setPanelFont(g, (int) (min(maxBarwidth * 0.63, height) * 0.24));

      int fontHeigt = g.getFontMetrics().getAscent() - g.getFontMetrics().getDescent();
      g.setColor(Color.WHITE);
      String winStringB = String.format("%.1f%%", blackWR);
      String winStringW = String.format("%.1f%%", whiteWR);
      g.drawString(
          winStringB, barPosxB + 2 * strokeRadius, posY + barHeight - (barHeight - fontHeigt) / 2);
      int swW = g.getFontMetrics().stringWidth(winStringW);
      g.drawString(
          winStringW,
          barPosxB + maxBarwidth - swW - 2 * strokeRadius,
          posY + barHeight - (barHeight - fontHeigt) / 2);
      if (shouldDrawMoveNumberDown()) {
        int swB = g.getFontMetrics().stringWidth(winStringB);
        String moveNumber =
            Lizzie.board.getHistory().getCurrentHistoryNode().getData().moveNumber + "";
        int swM = g.getFontMetrics().stringWidth(moveNumber);
        if (maxBarwidth > 2 * (swM) + swB + swW) {
          g.drawString(
              moveNumber,
              barPosxB + (maxBarwidth - swM) / 2,
              posY + barHeight - (barHeight - fontHeigt) / 2);
        }
      }
      g.setColor(Color.GRAY);
      Stroke oldstroke = g.getStroke();
      Stroke dashed =
          new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[] {4}, 0);
      g.setStroke(dashed);

      for (int i = 1; i <= winRateGridLines; i++) {
        int x = barPosxB + (int) (i * (maxBarwidth / (winRateGridLines + 1)));
        g.drawLine(x, barPosY, x, barPosY + barHeight);
      }
      g.setStroke(oldstroke);
    } else {
      if (shouldDrawMoveNumberDown()) {
        setPanelFont(g, (int) (min(width * 0.63, height) * 0.24));
        int fontHeigt = g.getFontMetrics().getAscent() - g.getFontMetrics().getDescent();
        String moveNumber =
            Lizzie.board.getHistory().getCurrentHistoryNode().getData().moveNumber + "";
        int swM = g.getFontMetrics().stringWidth(moveNumber);
        if (width > 2 * swM) {
          g.drawString(moveNumber, posX + (width - swM) / 2, posY + height / 6 + fontHeigt / 2);
        }
      }
    }
  }

  private boolean shouldDrawMoveNumberDown() {
    if (Lizzie.engineManager.isEngineGame) {
      if (Lizzie.engineManager.engineList.get(Lizzie.engineManager.engineGameInfo.whiteEngineIndex)
              .isKatago
          && Lizzie.engineManager.engineList.get(
                      Lizzie.engineManager.engineGameInfo.whiteEngineIndex)
                  .usingSpecificRules
              > 0) return true;
      if (Lizzie.engineManager.engineList.get(Lizzie.engineManager.engineGameInfo.blackEngineIndex)
              .isKatago
          && Lizzie.engineManager.engineList.get(
                      Lizzie.engineManager.engineGameInfo.blackEngineIndex)
                  .usingSpecificRules
              > 0) return true;
    }
    if (Lizzie.leelaz.isKatago && Lizzie.leelaz.usingSpecificRules > 0) return true;
    return false;
  }

  private void drawCaptured(
      Graphics2D g, int posX, int posY, int width, int height, boolean isSmallCap) {
    if (width < 5 || height < 5) return;
    // Draw border
    g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    g.setColor(new Color(0, 0, 0, 130));
    g.fillRect(posX, posY, width, height);

    // border. does not include bottom edge
    int strokeRadius = 1;
    g.setStroke(new BasicStroke(strokeRadius == 1 ? strokeRadius : 2 * strokeRadius));
    //    if (Lizzie.config.showBorder) {
    //      g.drawLine(
    //          posX + strokeRadius,
    //          posY + strokeRadius,
    //          posX - strokeRadius + width,
    //          posY + strokeRadius);
    //      g.drawLine(
    //          posX + strokeRadius,
    //          posY + 3 * strokeRadius,
    //          posX + strokeRadius,
    //          posY - strokeRadius + height);
    //      g.drawLine(
    //          posX - strokeRadius + width,
    //          posY + 3 * strokeRadius,
    //          posX - strokeRadius + width,
    //          posY - strokeRadius + height);
    //    }

    // Draw middle line
    g.drawLine(
        posX - strokeRadius + width / 2,
        posY + 3 * strokeRadius,
        posX - strokeRadius + width / 2,
        posY - strokeRadius + height);
    g.setColor(Color.white);

    // Draw black and white "stone"
    int diam = min(width / 2, height) / 3;
    int smallDiam = diam / 2;
    int bdiam = diam, wdiam = diam;
    if (isCounting) {
      // do nothing
      bdiam = smallDiam * 5 / 4;
      wdiam = smallDiam * 5 / 4;
    }

    // } else {

    // }
    else if (Lizzie.board.getHistory().isBlacksTurn()) {
      wdiam = smallDiam;
      bdiam = smallDiam * 3 / 2;
    } else {
      bdiam = smallDiam;
      wdiam = smallDiam * 3 / 2;
    }
    g.setColor(Color.black);
    // if (isSmallCap) {
    diam = diam * 3 / 2;
    bdiam = bdiam * 3 / 2;
    wdiam = wdiam * 3 / 2;
    g.fillOval(posX + width / 4 - bdiam / 2, posY + (diam - bdiam) / 2, bdiam, bdiam);

    g.setColor(Color.WHITE);
    g.fillOval(posX + width * 3 / 4 - wdiam / 2, posY + (diam - wdiam) / 2, wdiam, wdiam);
    // Status Indicator
    int statusDiam = 10;
    if ((height / 4) < 10) statusDiam = height / 4;

    g.setColor((Lizzie.leelaz != null && Lizzie.leelaz.isPondering()) ? Color.GREEN : Color.RED);
    g.fillOval(
        posX - strokeRadius + width / 2 - statusDiam / 2,
        posY + height * 7 / 26 + (diam - statusDiam) / 2,
        statusDiam,
        statusDiam);
    // }
    //    else {
    //    	bdiam=bdiam*4/3;
    //    	wdiam=wdiam*4/3;
    //      g.fillOval(
    //          posX + width / 4 - bdiam / 2, posY  + (diam - bdiam), bdiam, bdiam);
    //
    //      g.setColor(Color.WHITE);
    //      g.fillOval(
    //          posX + width * 3 / 4 - wdiam / 2,
    //          posY + (diam - wdiam) ,
    //          wdiam,
    //          wdiam);
    //      // Status Indicator
    //      int statusDiam = height / 8;
    //      g.setColor((Lizzie.leelaz != null && Lizzie.leelaz.isPondering()) ? Color.GREEN :
    // Color.RED);
    //      g.fillOval(
    //          posX - strokeRadius + width / 2 - statusDiam / 2,
    //          posY + height * 3 / 8 + (diam - statusDiam) / 2,
    //          statusDiam,
    //          statusDiam);
    //    }
    // Draw captures
    String bval = "", wval = "";
    if (isSmallCap)
      setPanelFont(
          g,
          (float) (min(width * 0.4, height * 0.85) * 0.2) > 18
              ? 18
              : (float) (min(width * 0.4, height * 0.85) * 0.2));
    else setPanelFont(g, (float) (height * 0.18));
    if (isCounting || isAutocounting) {
      bval = String.format("%d", Lizzie.estimateResults.allblackcounts);
      wval = String.format("%d", Lizzie.estimateResults.allwhitecounts);
    } else {
      bval = String.format("%d", Lizzie.board.getData().blackCaptures);
      wval = String.format("%d", Lizzie.board.getData().whiteCaptures);
    }

    g.setColor(Color.WHITE);
    //    int bw = g.getFontMetrics().stringWidth(bval);
    //    int ww = g.getFontMetrics().stringWidth(wval);
    //  boolean largeSubBoard = Lizzie.config.showLargeSubBoard() || extraMode == 1;
    //  int bx = (largeSubBoard ? width / 12 : -bw / 2);
    //  int wx = (largeSubBoard ? width / 12 : -ww / 2);

    int analyzedBlack = 0;
    int analyzedWhite = 0;
    double blackValue = 0;
    double whiteValue = 0;
    if (!isInPlayMode()) {
      if (!Lizzie.engineManager.isEngineGame) {
        BoardHistoryNode node = Lizzie.board.getHistory().getCurrentHistoryNode();
        if (node.nodeInfo.analyzed) {
          if (node.nodeInfo.isBlack) {
            blackValue = blackValue + node.nodeInfo.percentsMatch;
            analyzedBlack = analyzedBlack + 1;
          } else {
            whiteValue = whiteValue + node.nodeInfo.percentsMatch;
            analyzedWhite = analyzedWhite + 1;
          }
        }
        while (node.previous().isPresent()) {
          node = node.previous().get();
          NodeInfo nodeInfo = node.nodeInfo;
          if (nodeInfo.analyzed) {
            if (nodeInfo.isBlack) {
              blackValue = blackValue + nodeInfo.percentsMatch;
              analyzedBlack = analyzedBlack + 1;
            } else {
              whiteValue = whiteValue + nodeInfo.percentsMatch;
              analyzedWhite = analyzedWhite + 1;
            }
          }
        }
      }
    }
    String bAiScore = String.format("%.1f", blackValue * 100 / analyzedBlack);
    String wAiScore = String.format("%.1f", whiteValue * 100 / analyzedWhite);
    if (!isSmallCap) {
      if (isCounting) {
        drawStringMid(
            g,
            posX + width / 4,
            posY + height * 28 / 32,
            uiFont,
            Font.PLAIN,
            resourceBundle.getString("LizzieFrame.points") + bval, // "目数:"
            height / 6,
            width * 3 / 10,
            0);
        drawStringMid(
            g,
            posX + width * 3 / 4,
            posY + height * 28 / 32,
            uiFont,
            Font.PLAIN,
            resourceBundle.getString("LizzieFrame.points") + wval,
            height / 6,
            width * 3 / 10,
            0);
      } else {
        drawStringMid(
            g,
            posX + width / 4,
            posY + height * 28 / 32,
            uiFont,
            Font.PLAIN,
            resourceBundle.getString("LizzieFrame.captures") + bval, // 提子
            height / 6,
            width * 3 / 10,
            0);
        drawStringMid(
            g,
            posX + width * 3 / 4,
            posY + height * 28 / 32,
            uiFont,
            Font.PLAIN,
            resourceBundle.getString("LizzieFrame.captures") + wval,
            height / 6,
            width * 3 / 10,
            0);
      }

      if (analyzedBlack > 0)
        drawStringMid(
            g,
            posX + width / 4,
            posY + height * 19 / 32,
            uiFont,
            Font.PLAIN,
            resourceBundle.getString("LizzieFrame.AIscore") + bAiScore, // "AI总评分:"
            height / 5,
            width * 4 / 10,
            0);
      if (analyzedWhite > 0)
        drawStringMid(
            g,
            posX + width * 3 / 4,
            posY + height * 19 / 32,
            uiFont,
            Font.PLAIN,
            resourceBundle.getString("LizzieFrame.AIscore") + wAiScore,
            height / 5,
            width * 4 / 10,
            0);
      //   drawString(g,wAiScore, posX + width * 3 / 4 + wx, posY + height * 7 / 8);
    } else {
      if (analyzedBlack > 0)
        drawStringMid(
            g,
            posX + width / 4,
            posY + height * 5 / 7,
            uiFont,
            Font.PLAIN,
            resourceBundle.getString("LizzieFrame.AIscore") + bAiScore,
            height * 2 / 5,
            width * 4 / 10,
            0);
      if (analyzedWhite > 0)
        drawStringMid(
            g,
            posX + width * 3 / 4,
            posY + height * 5 / 7,
            uiFont,
            Font.PLAIN,
            resourceBundle.getString("LizzieFrame.AIscore") + wAiScore,
            height * 2 / 5,
            width * 4 / 10,
            0);
    }
    // Komi
    if (isSmallCap)
      setPanelFont(
          g,
          (float) (min(width * 0.4, height * 0.85) * 0.2) > Lizzie.config.frameFontSize + 6
              ? Lizzie.config.frameFontSize + 6
              : Math.max((float) (min(width * 0.4, height * 0.85) * 0.2), 11f));
    else setPanelFont(g, Math.max(11f, (float) (height * 0.18)));
    String komi =
        GameInfoDialog.FORMAT_KOMI.format(Lizzie.board.getHistory().getGameInfo().getKomi());
    int kw = g.getFontMetrics().stringWidth(komi);
    // g.setFont(new Font(g.getFont().getName(),Font.BOLD,g.getFont().getSize()));
    if (isSmallCap)
      g.drawString(komi, posX - strokeRadius + width / 2 - kw / 2, posY + height * 15 / 16);
    else g.drawString(komi, posX - strokeRadius + width / 2 - kw / 2, posY + height * 7 / 8);

    // Move or rules
    String moveOrRules = "";
    boolean usingSpecificRues = false;
    Leelaz leela = null;
    if (Lizzie.engineManager.isEngineGame && Lizzie.engineManager.engineGameInfo.isGenmove)
      leela =
          Lizzie.board.getHistory().isBlacksTurn()
              ? Lizzie.engineManager.engineList.get(
                  Lizzie.engineManager.engineGameInfo.blackEngineIndex)
              : Lizzie.engineManager.engineList.get(
                  Lizzie.engineManager.engineGameInfo.whiteEngineIndex);
    else leela = Lizzie.leelaz;
    // ||(Lizzie.engineManager.isEngineGame&&Lizzie.engineManager.engineGameInfo.isGenmove)
    if (leela.isKatago && !Lizzie.engineManager.isEmpty) {
      switch (leela.usingSpecificRules) {
        case 1:
          moveOrRules = resourceBundle.getString("LizzieFrame.currentRules.chinese");
          usingSpecificRues = true;
          break;
        case 2:
          moveOrRules = resourceBundle.getString("LizzieFrame.currentRules.chn-ancient");
          usingSpecificRues = true;
          break;
        case 3:
          moveOrRules = resourceBundle.getString("LizzieFrame.currentRules.japanese");
          usingSpecificRues = true;
          break;
        case 4:
          moveOrRules = resourceBundle.getString("LizzieFrame.currentRules.tromp-taylor");
          usingSpecificRues = true;
          break;
        case 5:
          moveOrRules = resourceBundle.getString("LizzieFrame.currentRules.others");
          usingSpecificRues = true;
          break;
      }
      if (usingSpecificRues)
        if (isSmallCap) {
          int mw = g.getFontMetrics().stringWidth(moveOrRules);
          g.drawString(
              moveOrRules, posX - strokeRadius + width / 2 - mw / 2, posY + height * 5 / 16);
        } else {
          int mw = g.getFontMetrics().stringWidth(moveOrRules);
          g.drawString(
              moveOrRules, posX - strokeRadius + width / 2 - mw / 2, posY + height * 3 / 10);
        }
    } else if (!shouldDrawMoveNumberDown()) {
      moveOrRules = Lizzie.board.getHistory().getCurrentHistoryNode().getData().moveNumber + "";
      if (isSmallCap) {
        int mw = g.getFontMetrics().stringWidth(moveOrRules);
        g.drawString(moveOrRules, posX - strokeRadius + width / 2 - mw / 2, posY + height * 5 / 16);
      } else {
        int mw = g.getFontMetrics().stringWidth(moveOrRules);
        g.drawString(moveOrRules, posX - strokeRadius + width / 2 - mw / 2, posY + height * 3 / 10);
      }
    }
  }

  private void setPanelFont(Graphics2D g, float size) {
    Font font = new Font(Lizzie.config.uiFontName, Font.PLAIN, (int) size);
    g.setFont(font);
  }

  private void drawWinratePane(int x, int y, int w, int h) {
    if (w < 10 || h < 10) {
      cachedWinrateImage = new BufferedImage(1, 1, TYPE_INT_ARGB);
      return;
    }
    if (refreshFromInfo && !refreshFromResized) {
      new Thread() {
        public void run() {
          if (lastGrw != w || lastGrh != h) {
            lastGrw = w;
            lastGrh = h;
            BufferedImage cachedWinrateImage = new BufferedImage(w, h, TYPE_INT_ARGB);
            BufferedImage cachedWinrateBackgroundImage = new BufferedImage(w, h, TYPE_INT_ARGB);
            BufferedImage cachedWinrateBlunderImage = new BufferedImage(w, h, TYPE_INT_ARGB);
            Graphics2D g = (Graphics2D) cachedWinrateImage.getGraphics();
            Graphics2D gBlunder = (Graphics2D) cachedWinrateBlunderImage.getGraphics();
            Graphics2D gBackground = (Graphics2D) cachedWinrateBackgroundImage.getGraphics();
            g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            gBlunder.setRenderingHint(
                RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            gBlunder.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            gBackground.setRenderingHint(
                RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            gBackground.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            winrateGraph.draw(g, gBlunder, gBackground, 0, 0, w, h);
            gBackground.drawImage(cachedWinrateBlunderImage, 0, 0, null);
            gBackground.drawImage(cachedWinrateImage, 0, 0, null);
            Lizzie.frame.cachedWinrateImage = cachedWinrateBackgroundImage;
            g.dispose();
          } else {
            BufferedImage cachedWinrateImage = new BufferedImage(w, h, TYPE_INT_ARGB);
            BufferedImage cachedWinrateBackgroundImage = new BufferedImage(w, h, TYPE_INT_ARGB);
            BufferedImage cachedWinrateBlunderImage = new BufferedImage(w, h, TYPE_INT_ARGB);
            Graphics2D g = (Graphics2D) cachedWinrateImage.getGraphics();
            Graphics2D gBlunder = (Graphics2D) cachedWinrateBlunderImage.getGraphics();
            Graphics2D gBackground = (Graphics2D) cachedWinrateBackgroundImage.getGraphics();
            g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            gBlunder.setRenderingHint(
                RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            gBlunder.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            gBackground.setRenderingHint(
                RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            gBackground.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            winrateGraph.draw(g, gBlunder, gBackground, 0, 0, w, h);
            gBackground.drawImage(cachedWinrateBlunderImage, 0, 0, null);
            gBackground.drawImage(cachedWinrateImage, 0, 0, null);
            Lizzie.frame.cachedWinrateImage = cachedWinrateBackgroundImage;
          }
          winratePaneTime = System.currentTimeMillis();
        }
      }.start();
    } else {
      refreshFromResized = false;
      if (lastGrw != w || lastGrh != h) {
        lastGrw = w;
        lastGrh = h;
        BufferedImage cachedWinrateImage = new BufferedImage(w, h, TYPE_INT_ARGB);
        BufferedImage cachedWinrateBackgroundImage = new BufferedImage(w, h, TYPE_INT_ARGB);
        BufferedImage cachedWinrateBlunderImage = new BufferedImage(w, h, TYPE_INT_ARGB);
        Graphics2D g = (Graphics2D) cachedWinrateImage.getGraphics();
        Graphics2D gBlunder = (Graphics2D) cachedWinrateBlunderImage.getGraphics();
        Graphics2D gBackground = (Graphics2D) cachedWinrateBackgroundImage.getGraphics();
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        gBlunder.setRenderingHint(
            RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        gBlunder.setRenderingHint(
            RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        gBackground.setRenderingHint(
            RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        gBackground.setRenderingHint(
            RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        winrateGraph.draw(g, gBlunder, gBackground, 0, 0, w, h);
        gBackground.drawImage(cachedWinrateBlunderImage, 0, 0, null);
        gBackground.drawImage(cachedWinrateImage, 0, 0, null);
        Lizzie.frame.cachedWinrateImage = cachedWinrateBackgroundImage;
      } else {
        if (refreshFromInfo && (System.currentTimeMillis() - winratePaneTime) < 200) {
          refreshFromInfo = false;
          return;
        }
        BufferedImage cachedWinrateImage = new BufferedImage(w, h, TYPE_INT_ARGB);
        BufferedImage cachedWinrateBackgroundImage = new BufferedImage(w, h, TYPE_INT_ARGB);
        BufferedImage cachedWinrateBlunderImage = new BufferedImage(w, h, TYPE_INT_ARGB);
        Graphics2D g = (Graphics2D) cachedWinrateImage.getGraphics();
        Graphics2D gBlunder = (Graphics2D) cachedWinrateBlunderImage.getGraphics();
        Graphics2D gBackground = (Graphics2D) cachedWinrateBackgroundImage.getGraphics();
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        gBlunder.setRenderingHint(
            RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        gBlunder.setRenderingHint(
            RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        gBackground.setRenderingHint(
            RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        gBackground.setRenderingHint(
            RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        winrateGraph.draw(g, gBlunder, gBackground, 0, 0, w, h);
        gBackground.drawImage(cachedWinrateBlunderImage, 0, 0, null);
        gBackground.drawImage(cachedWinrateImage, 0, 0, null);
        Lizzie.frame.cachedWinrateImage = cachedWinrateBackgroundImage;
      }
      winratePaneTime = System.currentTimeMillis();
    }
  }

  /**
   * Checks whether or not something was clicked and performs the appropriate action
   *
   * @param x x coordinate
   * @param y y coordinate
   */
  public void onClickedForManul(int x, int y) {
    Optional<int[]> boardCoordinates = boardRenderer.convertScreenToCoordinates(x, y);
    if (boardCoordinates.isPresent()) {
      int[] coords = boardCoordinates.get();
      if (blackorwhite == 0) Lizzie.board.placeForManual(coords[0], coords[1]);
      if (blackorwhite == 1) Lizzie.board.placeForManual(coords[0], coords[1], Stone.BLACK);
      if (blackorwhite == 2) Lizzie.board.placeForManual(coords[0], coords[1], Stone.WHITE);
    }
  }

  public void onClickedWinrateOnly(int x, int y) {
    if (isPlayingAgainstLeelaz || isAnaPlayingAgainstLeelaz) return;
    int moveNumber = winrateGraph.moveNumber(x - grx, y - gry);
    if (Lizzie.config.showWinrateGraph && moveNumber >= 0) {
      // isPlayingAgainstLeelaz = false;
      // menu.toggleDoubleMenuGameStatus();
      // noautocounting();
      if (canGoAfterload) Lizzie.board.goToMoveNumberBeyondBranch(moveNumber);
    }
  }

  public boolean onClickedRight(int x, int y) {
    if (blackorwhite == 0) return false;
    Optional<int[]> boardCoordinates;
    if (extraMode == 3) {
      boardCoordinates = boardRenderer2.convertScreenToCoordinates(x, y);
      if (!boardCoordinates.isPresent())
        boardCoordinates = boardRenderer.convertScreenToCoordinates(x, y);
    } else {
      boardCoordinates = boardRenderer.convertScreenToCoordinates(x, y);
    }
    if (boardCoordinates.isPresent()) {
      int[] coords = boardCoordinates.get();
      if (!isPlayingAgainstLeelaz && !isAnaPlayingAgainstLeelaz) {
        if (Lizzie.board.getHistory().getStones()[Lizzie.board.getIndex(coords[0], coords[1])]
            != Stone.EMPTY) {
          showmenu2(x, y, coords);
        } else {
          if (blackorwhite == 1) Lizzie.board.place(coords[0], coords[1], Stone.WHITE);
          if (blackorwhite == 2) Lizzie.board.place(coords[0], coords[1], Stone.BLACK);
        }
        return true;
      }
    }
    return false;
  }

  public void setDragStartInfo(int[] coords, boolean fromRightClick) {
    startcoords[0] = coords[0];
    startcoords[1] = coords[1];
    draggedstone = Lizzie.board.getstonestat(coords);
    if (draggedstone == Stone.BLACK || draggedstone == Stone.WHITE) {
      draggedCoords = coords;
      if (fromRightClick) Input.tempDrag = true;
      else Input.Draggedmode = true;
    }
  }

  public void onClicked(int x, int y) {
    // Check for board click
    Optional<int[]> boardCoordinates;
    if (extraMode == 3) {
      boardCoordinates = boardRenderer2.convertScreenToCoordinates(x, y);
      if (!boardCoordinates.isPresent())
        boardCoordinates = boardRenderer.convertScreenToCoordinates(x, y);
    } else {
      boardCoordinates = boardRenderer.convertScreenToCoordinates(x, y);
    }
    int moveNumber = winrateGraph.moveNumber(x - grx, y - gry);

    if (boardCoordinates.isPresent()) {
      // 增加判断是否为插入模式
      int[] coords = boardCoordinates.get();
      if (Lizzie.frame.bothSync) {
        if (blackorwhite == 0) Lizzie.board.place(coords[0], coords[1]);
        if (blackorwhite == 1) Lizzie.board.place(coords[0], coords[1], Stone.BLACK);
        if (blackorwhite == 2) Lizzie.board.place(coords[0], coords[1], Stone.WHITE);
      } else if (Lizzie.config.allowDrag) {
        setDragStartInfo(coords, false);
      }
      //  if (Lizzie.board.inAnalysisMode()) Lizzie.board.toggleAnalysis();
      if (!isPlayingAgainstLeelaz || (playerIsBlack == Lizzie.board.getData().blackToPlay)) {
        if (!isAnaPlayingAgainstLeelaz
            || !Lizzie.frame.toolbar.chkAutoPlayBlack.isSelected()
                == Lizzie.board.getData().blackToPlay) {
          if (isPlayingAgainstLeelaz || isAnaPlayingAgainstLeelaz) {
            if (Lizzie.leelaz.isGamePaused) return;
            if (allowPlaceStone && Lizzie.leelaz.isLoaded() && !Lizzie.engineManager.isEmpty)
              Lizzie.board.place(coords[0], coords[1]);
            else
              Utils.showMsg(
                  resourceBundle.getString("LizzieFrame.waitEngineLoadingHint")); // ("请等待引擎加载完毕");
            if (Lizzie.config.showrect == 1) boardRenderer.removeblock();
            tryToResetByoTime();
          } else {
            if (blackorwhite == 0) Lizzie.board.place(coords[0], coords[1]);
            if (blackorwhite == 1) Lizzie.board.place(coords[0], coords[1], Stone.BLACK);
            if (blackorwhite == 2) Lizzie.board.place(coords[0], coords[1], Stone.WHITE);
          }
        }
      }
    }
    if (Lizzie.config.showWinrateGraph && moveNumber >= 0) {
      if (isPlayingAgainstLeelaz || isAnaPlayingAgainstLeelaz) return;
      // isPlayingAgainstLeelaz = false;
      // noautocounting();
      if (canGoAfterload) Lizzie.board.goToMoveNumberBeyondBranch(moveNumber);
    }
    // if (Lizzie.config.showSubBoard && subBoardRenderer.isInside(x, y)) {
    // Lizzie.config.toggleLargeSubBoard();
    // }
    if (shouldShowSimpleVariation()
        && Lizzie.config.showVariationGraph
        && !Lizzie.engineManager.isEngineGame) {
      variationTreeBig.onClicked(x, y);
    }
    repaint();
  }

  public int getmovenumber(int x, int y) {
    Optional<int[]> boardCoordinates = boardRenderer.convertScreenToCoordinates(x, y);
    if (boardCoordinates.isPresent()) {
      int[] coords = boardCoordinates.get();
      return Lizzie.board.getmovenumber(coords);
    }
    return -1;
  }

  public int getmovenumberinbranch(int x, int y) {
    Optional<int[]> boardCoordinates = boardRenderer.convertScreenToCoordinates(x, y);
    if (boardCoordinates.isPresent()) {
      int[] coords = boardCoordinates.get();
      return Lizzie.board.getmovenumberinbranch(Lizzie.board.getIndex(coords[0], coords[1]));
    }
    return -1;
  }

  public void allow() {

    // Lizzie.leelaz.analyzeAvoid();
  }

  public boolean iscoordsempty(int x, int y) {
    Optional<int[]> boardCoordinates = boardRenderer.convertScreenToCoordinates(x, y);
    if (boardCoordinates.isPresent()) {
      return Lizzie.board.iscoordsempty(boardCoordinates.get()[0], boardCoordinates.get()[1]);
    }
    return false;
  }

  public String convertmousexy(int x, int y) {
    Optional<int[]> boardCoordinates = boardRenderer.convertScreenToCoordinates(x, y);
    if (boardCoordinates.isPresent()) {
      int[] coords = boardCoordinates.get();
      return Lizzie.board.convertCoordinatesToName(coords[0], coords[1]);
    }
    return "N";
  }

  public int[] convertmousexytocoords(int x, int y) {
    Optional<int[]> boardCoordinates = boardRenderer.convertScreenToCoordinates(x, y);
    if (boardCoordinates.isPresent()) {
      int[] coords = boardCoordinates.get();
      return coords;
    }
    return this.outOfBoundCoordinate;
  }

  public void onDoubleClicked(int x, int y) {
    Optional<int[]> boardCoordinates = boardRenderer.convertScreenToCoordinates(x, y);
    if (boardCoordinates.isPresent()) {
      int[] coords = boardCoordinates.get();
      if (!isPlayingAgainstLeelaz) {
        Lizzie.board.gotoAnyMoveByCoords(coords);
        refresh();
      }
    }
  }

  private final Consumer<String> placeVariation =
      v -> Board.asCoordinates(v).ifPresent(c -> Lizzie.board.place(c[0], c[1]));

  public boolean playCurrentVariation() {
    if (Lizzie.config.showSuggestionVariations) {
      if (boardRenderer.getDisplayedBranchLength() > 0) {
        if (boardRenderer.variationOpt.isPresent()) {
          for (int i = 0;
              i
                  < Math.min(
                      boardRenderer.variationOpt.get().size(),
                      boardRenderer.getDisplayedBranchLength());
              i++) {
            Optional<int[]> coords = Board.asCoordinates(boardRenderer.variationOpt.get().get(i));
            if (coords.isPresent()) Lizzie.board.place(coords.get()[0], coords.get()[1]);
          }
        }
      } else boardRenderer.variationOpt.ifPresent(vs -> vs.forEach(placeVariation));
      redrawTreeLater = true;
      return boardRenderer.variationOpt.isPresent();
    } else {
      variationOpt.ifPresent(vs -> vs.forEach(placeVariation));
      redrawTreeLater = true;
      return variationOpt.isPresent();
    }
  }

  //  public boolean playCurrentVariation2() {
  //    if (Lizzie.engineManager.currentEngineNo >= 0) Lizzie.engineManager.isEmpty = true;
  //    if (Lizzie.config.showSuggestionVariations) {
  //      boardRenderer.variationOpt.ifPresent(vs -> vs.forEach(placeVariation));
  //      if (!boardRenderer.variationOpt.isPresent())
  //        if (Lizzie.engineManager.currentEngineNo >= 0) Lizzie.engineManager.isEmpty = false;
  //      return boardRenderer.variationOpt.isPresent();
  //    } else {
  //      variationOpt.ifPresent(vs -> vs.forEach(placeVariation));
  //      if (!variationOpt.isPresent())
  //        if (Lizzie.engineManager.currentEngineNo >= 0) Lizzie.engineManager.isEmpty = false;
  //      return variationOpt.isPresent();
  //    }
  //  }

  public boolean isMouseOverSuggestions() {
    List<MoveData> bestMoves = Lizzie.board.getHistory().getData().bestMoves;
    for (int i = 0; i < bestMoves.size(); i++) {
      Optional<int[]> c = Lizzie.board.asCoordinates(bestMoves.get(i).coordinate);
      if (c.isPresent()) {
        if (Lizzie.frame.isMouseOver2(c.get()[0], c.get()[1])) {
          List<String> variation = bestMoves.get(i).variation;
          variationOpt = Optional.of(variation);
          return true;
        }
      }
    }
    return false;
  }

  public void playBestMove() {
    if (Lizzie.frame.isShowingHeatmap) {
      Lizzie.board.playBestHeatMove();
    } else boardRenderer.bestMoveCoordinateName().ifPresent(placeVariation);
  }

  public boolean processSubOnMouseMoved(int x, int y) {
    if (Lizzie.frame.extraMode == 1) {
      if (x < subBoardLengthmouse && y < subBoardLengthmouse) {
        // 1
        if (!Lizzie.frame.subBoardRenderer.isMouseOver
            && (Lizzie.engineManager.isEmpty || !Lizzie.leelaz.isPondering()))
          Lizzie.frame.refresh();
        Lizzie.frame.subBoardRenderer.isMouseOver = true;
        Lizzie.frame.subBoardRenderer2.isMouseOver = true;
        Lizzie.frame.subBoardRenderer3.isMouseOver = true;
        Lizzie.frame.subBoardRenderer4.isMouseOver = true;
        return true;
      } else return false;
    }

    if (Lizzie.config.showSubBoard) {
      // int x = e.getX()*3/2;
      //  int y = e.getY()*3/2;
      if (x >= subBoardXmouse
          && x <= subBoardXmouse + subBoardLengthmouse
          && y <= subBoardYmouse + subBoardLengthmouse
          && y >= subBoardYmouse) {
        if (!Lizzie.frame.subBoardRenderer.isMouseOver
            && (Lizzie.engineManager.isEmpty || !Lizzie.leelaz.isPondering()))
          Lizzie.frame.refresh();
        Lizzie.frame.subBoardRenderer.isMouseOver = true;
        return true;
      } else {
        return false;
      }
    }
    return false;
  }

  public void onMouseExited() {
    boolean needRepaint = false;
    if (Lizzie.frame.extraMode == 1) {
      if (Lizzie.frame.subBoardRenderer2.isMouseOver) {
        Lizzie.frame.subBoardRenderer2.isMouseOver = false;
        needRepaint = true;
        Lizzie.frame.subBoardRenderer2.clearAfterMove();
      }
      if (Lizzie.frame.subBoardRenderer3.isMouseOver) {
        Lizzie.frame.subBoardRenderer3.isMouseOver = false;
        needRepaint = true;
        Lizzie.frame.subBoardRenderer3.clearAfterMove();
      }
      if (Lizzie.frame.subBoardRenderer4.isMouseOver) {
        Lizzie.frame.subBoardRenderer4.isMouseOver = false;
        needRepaint = true;
        Lizzie.frame.subBoardRenderer4.clearAfterMove();
      }
    }
    if (Lizzie.config.showSubBoard) {
      if (Lizzie.frame.subBoardRenderer.isMouseOver) {
        Lizzie.frame.subBoardRenderer.isMouseOver = false;
        needRepaint = true;
        Lizzie.frame.subBoardRenderer.clearAfterMove();
      }
    }
    mouseOverCoordinate = outOfBoundCoordinate;
    if (isMouseOver) {
      isMouseOver = false;
      needRepaint = true;
      clearMoved();
    }
    if (Lizzie.config.showMouseOverWinrateGraph
        && Lizzie.config.showWinrateGraph
        && winrateGraph.mouseOverNode != null) {
      winrateGraph.clearMouseOverNode();
      needRepaint = true;
    }
    if (draggedstone != Stone.EMPTY) {
      draggedstone = Stone.EMPTY;
      boardRenderer.removedrawmovestone();
      needRepaint = true;
      featurecat.lizzie.gui.Input.Draggedmode = false;
    }
    if (shouldShowRect()) {
      needRepaint = true;
      boardRenderer.removeblock();
      if (extraMode == 2) {
        boardRenderer2.removeblock();
      }
    }
    if (needRepaint) refresh();
  }

  public boolean shouldShowRect() {
    if (isInScoreMode) return false;
    else
      return Lizzie.config.showrect == 0
          || (Lizzie.config.showrect == 1
              && (isPlayingAgainstLeelaz || isAnaPlayingAgainstLeelaz)
              && Lizzie.leelaz.isLoaded()
              && ((Lizzie.board.getHistory().isBlacksTurn() && Lizzie.frame.playerIsBlack)
                  || (!Lizzie.board.getHistory().isBlacksTurn() && !Lizzie.frame.playerIsBlack)));
  }

  public List<MoveData> getBestMoves() {
    List<MoveData> bestMoves;
    if (Lizzie.engineManager.isEngineGame && Lizzie.config.showPreviousBestmovesInEngineGame) {
      if (Lizzie.board.getHistory().getCurrentHistoryNode().previous().isPresent())
        bestMoves =
            Lizzie.board.getHistory().getCurrentHistoryNode().previous().get().getData().bestMoves;
      else bestMoves = new ArrayList<>();
    } else bestMoves = Lizzie.board.getHistory().getCurrentHistoryNode().getData().bestMoves;
    return bestMoves;
  }

  public boolean processMouseMoveOnWinrateGraph(int x, int y) {
    if (winrateGraph.mode == 1) return false;
    int moveNumber = winrateGraph.moveNumber(x - this.grx, y - this.gry);
    boolean noRefresh = false;
    if (moveNumber >= 0) {
      BoardHistoryNode curNode = Lizzie.board.getHistory().getCurrentHistoryNode();
      BoardHistoryNode mouseOverNode = curNode;
      int curMoveNumber = (mouseOverNode.getData()).moveNumber;
      if (curMoveNumber > moveNumber) {
        for (int i = 0; i < curMoveNumber - moveNumber; i++) {
          if (mouseOverNode.previous().isPresent()) {
            mouseOverNode = mouseOverNode.previous().get();
          } else {
            noRefresh = true;
            break;
          }
        }
      } else if (curMoveNumber < moveNumber) {
        for (int i = 0; i < moveNumber - curMoveNumber; i++) {
          if (mouseOverNode.next().isPresent()) {
            mouseOverNode = mouseOverNode.next().get();
          } else {
            noRefresh = true;
            break;
          }
        }
      }
      winrateGraph.setMouseOverNode(mouseOverNode);
      if (mouseOverNode != curNode || !noRefresh) {
        this.redrawWinratePaneOnly = true;
        repaint();
      }
      return true;
    }
    return false;
  }

  public void onMouseMoved(int x, int y) {
    if (Lizzie.config.showMouseOverWinrateGraph
        && Lizzie.config.showWinrateGraph
        && processMouseMoveOnWinrateGraph(x, y)) return;
    if (Lizzie.config.showMouseOverWinrateGraph
        && Lizzie.config.showWinrateGraph
        && winrateGraph.mouseOverNode != null) {
      winrateGraph.clearMouseOverNode();
      this.redrawWinratePaneOnly = true;
      repaint();
      return;
    }
    boolean needRepaint = false;
    curSuggestionMoveOrderByNumber = -1;
    if (!mainPanel.isFocusOwner() && !commentEditPane.isVisible()) {
      mainPanel.requestFocus();
    }
    if (RightClickMenu.isVisible() || RightClickMenu2.isVisible()) {
      return;
    }
    //    if (isshowrightmenu) {
    //      isshowrightmenu = false;
    //    }
    if (Lizzie.config.noRefreshOnSub) {
      if (processSubOnMouseMoved(x, y)) {
        isMouseOnSub = true;
        if (isMouseOver) {
          isMouseOver = false;
          clearMoved();
        }
        if (!isMouseOnSub && (!Lizzie.leelaz.isPondering() || Lizzie.engineManager.isEmpty))
          repaint();
        return;
      } else {
        if (isMouseOnSub) {
          if ((!Lizzie.leelaz.isPondering() || Lizzie.engineManager.isEmpty)) needRepaint = true;
          isMouseOnSub = false;
          if (Lizzie.frame.extraMode == 1) {
            Lizzie.frame.subBoardRenderer2.isMouseOver = false;
            Lizzie.frame.subBoardRenderer3.isMouseOver = false;
            Lizzie.frame.subBoardRenderer4.isMouseOver = false;
            Lizzie.frame.subBoardRenderer2.clearAfterMove();
            Lizzie.frame.subBoardRenderer3.clearAfterMove();
            Lizzie.frame.subBoardRenderer4.clearAfterMove();
          }
          if (Lizzie.config.showSubBoard) {
            Lizzie.frame.subBoardRenderer.isMouseOver = false;
            Lizzie.frame.subBoardRenderer.clearAfterMove();
          }
        }
      }
    }
    if (clickOrder != -1) {
      hasMoveOutOfList = true;
      return;
    }
    // mouseOverCoordinate = outOfBoundCoordinate;
    Optional<int[]> coords = boardRenderer.convertScreenToCoordinates(x, y);
    if (coords.isPresent()) {
      int[] curCoords = coords.get();
      boolean isCoordsChanged = false;
      if (mouseOverCoordinate[0] != curCoords[0] || mouseOverCoordinate[1] != curCoords[1]) {
        isCoordsChanged = true;
        mouseOverCoordinate = curCoords;
      }
      if (isCoordsChanged) {
        boolean isCurMouseOver = false;
        if (Lizzie.config.showNextMoveBlunder) {
          if (Lizzie.board.getHistory().getCurrentHistoryNode().next().isPresent()) {
            BoardData nextData =
                Lizzie.board.getHistory().getCurrentHistoryNode().next().get().getData();
            if (nextData.getPlayouts() > 0)
              if (nextData.lastMove.isPresent())
                if (nextData.lastMove.get()[0] == curCoords[0]
                    && nextData.lastMove.get()[1] == curCoords[1]) {
                  isCurMouseOver = true;
                }
          }
        }
        List<MoveData> bestMoves = getBestMoves();
        if (!bestMoves.isEmpty())
          for (int i = 0; i < bestMoves.size(); i++) {
            Optional<int[]> bestCoords = Board.asCoordinates(bestMoves.get(i).coordinate);
            if (bestCoords.isPresent()) {
              if (bestCoords.get()[0] == curCoords[0] && bestCoords.get()[1] == curCoords[1]) {
                isCurMouseOver = true;
                break;
              }
            }
          }
        if (extraMode == 2) {
          List<MoveData> bestMoves2 =
              Lizzie.board.getHistory().getCurrentHistoryNode().getData().bestMoves2;
          if (!bestMoves2.isEmpty())
            for (int i = 0; i < bestMoves2.size(); i++) {
              Optional<int[]> bestCoords = Board.asCoordinates(bestMoves2.get(i).coordinate);
              if (bestCoords.isPresent()) {
                if (bestCoords.get()[0] == curCoords[0] && bestCoords.get()[1] == curCoords[1]) {
                  isCurMouseOver = true;
                  break;
                }
              }
            }
        }

        if (isCurMouseOver) {
          clearMoved();
          needRepaint = true;
          isMouseOver = true;
          if (Lizzie.config.autoReplayBranch) {
            mouseOverChanged = true;
            if (!Lizzie.config.autoReplayDisplayEntireVariationsFirst)
              this.boardRenderer.setDisplayedBranchLength(1);
          }
        } else {
          if (isMouseOver) {
            needRepaint = true;
          }
          clearMoved();
          isMouseOver = false;
        }
      }
      if (shouldShowRect()) {
        isShowingRect = true;
        needRepaint = true;
        if (extraMode == 2) {
          Optional<int[]> coords2 = boardRenderer2.convertScreenToCoordinates(x, y);
          if (coords2.isPresent()) {
            boardRenderer2.drawmoveblock(
                coords.get()[0], coords.get()[1], Lizzie.board.getHistory().isBlacksTurn());
          } else
            boardRenderer.drawmoveblock(
                coords.get()[0], coords.get()[1], Lizzie.board.getHistory().isBlacksTurn());
        } else
          boardRenderer.drawmoveblock(
              coords.get()[0], coords.get()[1], Lizzie.board.getHistory().isBlacksTurn());
      } else if (Lizzie.frame.isAnaPlayingAgainstLeelaz || Lizzie.frame.isPlayingAgainstLeelaz)
        boardRenderer.removeblock();
      if (extraMode == 2) {
        boardRenderer2.removeblock();
      }
    } else {
      mouseOverCoordinate = outOfBoundCoordinate;
      if (isMouseOver) {
        isMouseOver = false;
        needRepaint = true;
        clearMoved();
        isMouseOver = false;
      }
      if (shouldShowRect()) {
        if (isShowingRect) {

          needRepaint = true;
          boardRenderer.removeblock();
          if (extraMode == 2) {
            boardRenderer2.removeblock();
          }
          isShowingRect = false;
        }
      }
    }
    if (needRepaint) refresh();
  }

  public void clearMoved() {
    isReplayVariation = false;
    Lizzie.frame.isMouseOver = false;
    boardRenderer.startNormalBoard();
    boardRenderer.clearBranch();
    boardRenderer.notShowingBranch();
    if (extraMode == 2) {
      boardRenderer2.startNormalBoard();
      boardRenderer2.clearBranch();
      boardRenderer2.notShowingBranch();
    }
  }

  //  public void clearMoved2() {
  //    isReplayVariation = false;
  //    Lizzie.frame.isMouseOver = false;
  //    boardRenderer2.startNormalBoard();
  //    boardRenderer2.clearBranch();
  //    boardRenderer2.notShowingBranch();
  //  }

  public boolean isMouseOver(int x, int y) {
    if (!Lizzie.frame.toolbar.chkShowBlack.isSelected()
        && !Lizzie.frame.toolbar.chkShowBlack.isSelected()) {
      return false;
    }
    if (Lizzie.config.showSuggestionVariations)
      return mouseOverCoordinate[0] == x && mouseOverCoordinate[1] == y;
    else return false;
  }

  public boolean isMouseOverIndependMainBoard(int x, int y) {
    if (!Lizzie.frame.toolbar.chkShowBlack.isSelected()
        && !Lizzie.frame.toolbar.chkShowBlack.isSelected()) {
      return false;
    }
    if (Lizzie.config.showSuggestionVariations)
      return independentMainBoard.mouseOverCoordinate[0] == x
          && independentMainBoard.mouseOverCoordinate[1] == y;
    else return false;
  }

  public boolean isMouseOverFloatBoard(int x, int y) {
    if (floatBoard == null) return false;
    if (!Lizzie.frame.toolbar.chkShowBlack.isSelected()
        && !Lizzie.frame.toolbar.chkShowBlack.isSelected()) {
      return false;
    }
    if (Lizzie.config.showSuggestionVariations)
      return floatBoard.mouseOverCoordinate[0] == x && floatBoard.mouseOverCoordinate[1] == y;
    else return false;
  }

  public boolean isMouseOver2(int x, int y) {

    return mouseOverCoordinate[0] == x && mouseOverCoordinate[1] == y;
  }

  public boolean isMouseOversub(int x, int y) {
    return suggestionclick[0] == x && suggestionclick[1] == y;
  }

  public void onMouseDragged(int x, int y) {
    int moveNumber = winrateGraph.moveNumber(x - grx, y - gry);
    if (Lizzie.config.showWinrateGraph && moveNumber >= 0 && canGoAfterload) {
      Lizzie.board.goToMoveNumberWithinBranch(moveNumber);
    }
  }

  /**
   * Process Comment Mouse Wheel Moved
   *
   * @return true when the scroll event was processed by this method
   */
  //    public void processCommentMouseOverd(MouseEvent e) {
  //      //  noRedrawComment=true;
  //      if (Lizzie.config.showComment
  //          && commentRect.contains(Utils.zoomOut(e.getX()), Utils.zoomOut(e.getY()))) {
  //    		commentBlunderControlPane.setVisible(true);
  //      } else {
  //    		commentBlunderControlPane.setVisible(false);
  //      }
  //    }

  public boolean isInPlayMode() {
    return Lizzie.config.UsePlayMode
        && (isPlayingAgainstLeelaz || isAnaPlayingAgainstLeelaz)
        && !syncBoard;
  }

  public boolean processCommentMousePressed(MouseEvent e) {
    //  noRedrawComment=true;
    //    if (isInPlayMode()) return false;
    //    if (Lizzie.config.showComment
    //        && commentRect.contains(Utils.zoomOut(e.getX()), Utils.zoomOut(e.getY()))) {
    //      if (e.getButton() == MouseEvent.BUTTON3) Lizzie.frame.undoForRightClick();
    //      else if (e.getButton() == 1) {
    //        String text = Lizzie.board.getHistory().getCurrentHistoryNode().getData().comment;
    //        if (text.length() > 0) text = text + '\n';
    //        commentEditTextPane.setText(text);
    //        commentEditPane.setVisible(true);
    //        commentEditTextPane.requestFocus(true);
    //      }
    //      return true;
    //    } else {
    if (commentEditPane.isVisible()) {
      mainPanel.requestFocus();
      setCommentEditable(false);
    }
    return false;
    // }
  }

  public boolean processPressOnSub(MouseEvent e) {
    if (isInPlayMode()) return false;
    if (extraMode == 3) return false;
    // if (Lizzie.engineManager.isEngineGame) return false;
    if (Lizzie.frame.extraMode == 1) {
      int x = Utils.zoomOut(e.getX());
      int y = Utils.zoomOut(e.getY());
      if (x < subBoardLengthmouse / 2 && y < subBoardLengthmouse / 2) {
        // 1
        if (e.getButton() == MouseEvent.BUTTON1) {
          subBoardRenderer.statChanged = true;
          subBoardRenderer.bestmovesNum++;
          repaint();
        } else if (e.getButton() == MouseEvent.BUTTON3) {
          if (subBoardRenderer.bestmovesNum >= 1) {
            subBoardRenderer.statChanged = true;
            subBoardRenderer.bestmovesNum--;
            repaint();
          }
        }
        return true;

      } else if (x >= subBoardLengthmouse / 2
          && x < subBoardLengthmouse
          && y < subBoardLengthmouse / 2) {
        // 2
        if (e.getButton() == MouseEvent.BUTTON1) {
          subBoardRenderer2.statChanged = true;
          subBoardRenderer2.bestmovesNum++;
          repaint();
        } else if (e.getButton() == MouseEvent.BUTTON3) {
          if (subBoardRenderer2.bestmovesNum >= 1) {
            subBoardRenderer2.statChanged = true;
            subBoardRenderer2.bestmovesNum--;
            repaint();
          }
        }
        return true;

      } else if (x < subBoardLengthmouse / 2
          && y < subBoardLengthmouse
          && y >= subBoardLengthmouse / 2) {
        // 3
        if (e.getButton() == MouseEvent.BUTTON1) {
          subBoardRenderer3.statChanged = true;
          subBoardRenderer3.bestmovesNum++;
          repaint();
        } else if (e.getButton() == MouseEvent.BUTTON3) {
          if (subBoardRenderer3.bestmovesNum >= 1) {
            subBoardRenderer3.statChanged = true;
            subBoardRenderer3.bestmovesNum--;
            repaint();
          }
        }
        return true;

      } else if (x >= subBoardLengthmouse / 2
          && x < subBoardLengthmouse
          && y < subBoardLengthmouse
          && y >= subBoardLengthmouse / 2) {
        // 4
        if (e.getButton() == MouseEvent.BUTTON1) {
          subBoardRenderer4.statChanged = true;
          subBoardRenderer4.bestmovesNum++;
          repaint();
        } else if (e.getButton() == MouseEvent.BUTTON3) {
          if (subBoardRenderer4.bestmovesNum >= 1) {
            subBoardRenderer4.statChanged = true;
            subBoardRenderer4.bestmovesNum--;
            repaint();
          }
          return true;
        } else return false;
      }
    } else if (Lizzie.config.showSubBoard) {
      int x = Utils.zoomOut(e.getX());
      int y = Utils.zoomOut(e.getY());
      if (x >= subBoardXmouse
          && x <= subBoardXmouse + subBoardLengthmouse
          && y <= subBoardYmouse + subBoardLengthmouse
          && y >= subBoardYmouse) {
        if (e.getButton() == MouseEvent.BUTTON2) {
          if (Lizzie.config.showLargeSubBoard()) {
            if (!Lizzie.config.showVariationGraph) Lizzie.config.toggleShowVariationGraph();
          } else {
            if (Lizzie.config.showVariationGraph) Lizzie.config.toggleShowVariationGraph();
          }
          Lizzie.config.toggleLargeSubBoard();
          return true;
        }
        if (e.getButton() == MouseEvent.BUTTON1) {
          subBoardRenderer.statChanged = true;
          subBoardRenderer.bestmovesNum++;
          repaint();
        } else if (e.getButton() == MouseEvent.BUTTON3) {
          if (subBoardRenderer.bestmovesNum >= 1) {
            subBoardRenderer.statChanged = true;
            subBoardRenderer.bestmovesNum--;
            repaint();
          }
        }

        return true;
      } else {
        return false;
      }
    }
    return false;
  }

  public void processIndependentPressOnSub(MouseEvent e) {
    if (isInPlayMode()) return;
    independentSubBoard.processIndependentPressOnSub(e);
  }

  public boolean processSubboardMouseWheelMoved(MouseWheelEvent e) {
    if (isInPlayMode()) return false;
    if (Lizzie.frame.extraMode == 1) {
      int x = Utils.zoomOut(e.getX());
      int y = Utils.zoomOut(e.getY());
      if (x < subBoardLengthmouse / 2 && y < subBoardLengthmouse / 2) {
        // 1
        if (e.getWheelRotation() > 0) {
          doBranchSub(0, 1);
          refresh();
        } else if (e.getWheelRotation() < 0) {
          doBranchSub(0, -1);
          refresh();
        }
        return true;
      } else if (x >= subBoardLengthmouse / 2
          && x < subBoardLengthmouse
          && y < subBoardLengthmouse / 2) {
        // 2
        if (e.getWheelRotation() > 0) {
          doBranchSub(1, 1);
          refresh();
        } else if (e.getWheelRotation() < 0) {
          doBranchSub(1, -1);
          refresh();
        }
        return true;
      } else if (x < subBoardLengthmouse / 2
          && y < subBoardLengthmouse
          && y >= subBoardLengthmouse / 2) {
        // 3
        if (e.getWheelRotation() > 0) {
          doBranchSub(2, 1);
          refresh();
        } else if (e.getWheelRotation() < 0) {
          doBranchSub(2, -1);
          refresh();
        }
        return true;
      } else if (x >= subBoardLengthmouse / 2
          && x < subBoardLengthmouse
          && y < subBoardLengthmouse
          && y >= subBoardLengthmouse / 2) {
        // 4
        if (e.getWheelRotation() > 0) {
          doBranchSub(3, 1);
          refresh();
        } else if (e.getWheelRotation() < 0) {
          doBranchSub(3, -1);
          refresh();
        }
        return true;
      } else return false;
    }

    if (Lizzie.config.showSubBoard) {
      int x = Utils.zoomOut(e.getX());
      int y = Utils.zoomOut(e.getY());
      if (x >= subBoardXmouse
          && x <= subBoardXmouse + subBoardLengthmouse
          && y <= subBoardYmouse + subBoardLengthmouse
          && y >= subBoardYmouse) {

        if (e.getWheelRotation() > 0) {
          doBranchSub(0, 1);
          refresh();
        } else if (e.getWheelRotation() < 0) {
          doBranchSub(0, -1);
          refresh();
        }

        return true;
      } else {
        return false;
      }
    }
    return false;
  }

  public void processIndependentSubboardMouseWheelMoved(MouseWheelEvent e) {
    if (isInPlayMode()) return;
    if (e.getWheelRotation() > 0) {
      independentSubBoard.doBranch(1);
      refresh();
    } else if (e.getWheelRotation() < 0) {
      independentSubBoard.doBranch(-1);
      refresh();
    }
  }
  /**
   * Create comment cached image
   *
   * @param forceRefresh
   * @param w
   * @param h
   */
  //  public void createCommentImage(boolean forceRefresh, int w, int h, boolean isLoadingEngine) {
  //    if (forceRefresh || cachedCommentImage.getWidth() != w || cachedCommentImage.getHeight() !=
  // h) {
  //      if (w > 0 && h > 0) {
  //        commentScrollPane.setSize(w, h);
  //        cachedCommentImage =
  //            new BufferedImage(
  //                commentScrollPane.getWidth(), commentScrollPane.getHeight(), TYPE_INT_ARGB);
  //        Graphics2D g2 = cachedCommentImage.createGraphics();
  //        commentScrollPane.doLayout();
  //        commentScrollPane.addNotify();
  //        commentScrollPane.validate();
  //        if (isLoadingEngine) commentScrollPane.getVerticalScrollBar().setValue(9999);
  //        //   commentPos = commentScrollPane.getVerticalScrollBar().getValue();
  //        commentScrollPane.printAll(g2);
  //        g2.dispose();
  //      }
  //    }
  //  }

  private void appendComment() {
    if (!Lizzie.config.showComment || Lizzie.engineManager.isEmpty) return;
    if (Lizzie.config.appendWinrateToComment || Lizzie.engineManager.isEngineGame) {
      long currentTime = System.currentTimeMillis();
      if (autoIntervalCom > 0 && currentTime - lastAutocomTime >= autoIntervalCom) {
        lastAutocomTime = currentTime;
        // Append the winrate to the comment
        if (Lizzie.leelaz != null && !Lizzie.board.isLoadingFile) {
          // if (MoveData.getPlayouts(Lizzie.board.getHistory().getData().bestMoves) >
          // Lizzie.board.getHistory().getData().getPlayouts())
          String comment = Lizzie.board.getHistory().getData().comment;
          //          if (Lizzie.leelaz.isPondering()
          //              || Lizzie.frame.isPlayingAgainstLeelaz
          //              || Lizzie.engineManager.isEngineGame) {
          if (!Lizzie.board.getHistory().getData().bestMoves.isEmpty()) SGFParser.appendComment();
          //     }
          if (!Lizzie.leelaz.isPondering()
              && !isPlayingAgainstLeelaz
              && !Lizzie.engineManager.isEngineGame
              && !(Lizzie.board.getHistory().getData().comment).equals(comment)) refresh();
        }
      }
    }
  }

  private void autosaveMaybe() {
    if (Lizzie.config.autoSaveOnExit && !Lizzie.engineManager.isEngineGame) {
      long currentTime = System.currentTimeMillis();
      if (currentTime - lastAutosaveTime >= 60000) {
        lastAutosaveTime = currentTime;
        saveAutoGame(2);
      }
    }
  }

  public void setPlayers(String whitePlayer, String blackPlayer) {
    playerTitle =
        String.format(
            "- ["
                + resourceBundle.getString("Menu.Black")
                + "]%s vs["
                + resourceBundle.getString("Menu.White")
                + "]%s",
            blackPlayer,
            whitePlayer);
    //  updateTitle();
  }

  public void setResult(String result) {
    if (result.equals("")) resultTitle = "";
    else
      resultTitle =
          String.format("(" + resourceBundle.getString("LizzieFrame.result") + "%s)", result);
    //  updateTitle();
  }

  public void updateTitle() {
    if (isTrying) {
      return;
    }
    StringBuilder sb = new StringBuilder();
    if ((Lizzie.engineManager.isEngineGame && Lizzie.engineManager.engineGameInfo.isGenmove)) {
      sb.append(DEFAULT_TITLE + "-");
      sb.append(
          (Lizzie.board.getHistory().getData().blackToPlay
                  ? Lizzie.engineManager.engineList.get(
                          Lizzie.engineManager.engineGameInfo.blackEngineIndex)
                      .oriEnginename
                  : Lizzie.engineManager.engineList.get(
                          Lizzie.engineManager.engineGameInfo.whiteEngineIndex)
                      .oriEnginename)
              + " "
              + resourceBundle.getString("LizzieFrame.thinking"));
      // sb.append(playerTitle);
      // sb.append(resultTitle);
      if (hasEnginePkTitile && enginePkTitile != null) {
        setTitle(enginePkTitile + " " + sb.toString());
      } else {
        setTitle(sb.toString());
      }
      return;
    }
    if (Lizzie.config.showTitleWr
        && (!(isPlayingAgainstLeelaz || isAnaPlayingAgainstLeelaz) || syncBoard)) {

      if (Lizzie.board.getHistory().getData().getPlayouts() > 0) {
        sb.append("[");
        // if (Lizzie.leelaz != null) {
        double winRateC = Lizzie.board.getHistory().getData().winrate;
        if (!Lizzie.board.getHistory().isBlacksTurn()) winRateC = 100 - winRateC;
        winRate = winRateC > -100 && winRateC < 100 ? winRateC : winRate;
        sb.append(
            String.format("%.1f", winRate)
                + " "
                + Lizzie.frame.getPlayoutsString(
                    Lizzie.board.getHistory().getData().getPlayouts()));
        //   }

        if (Lizzie.board.getHistory().getData().isKataData) {
          double scoreC = Lizzie.board.getHistory().getCurrentHistoryNode().getData().scoreMean;
          if (scoreC != 0) {
            if (Lizzie.board.getHistory().isBlacksTurn()) {
              if (Lizzie.config.showKataGoBoardScoreMean)
                scoreC = scoreC + Lizzie.board.getHistory().getGameInfo().getKomi();
            } else {
              if (Lizzie.config.showKataGoBoardScoreMean)
                scoreC = -scoreC + Lizzie.board.getHistory().getGameInfo().getKomi();
              else scoreC = -scoreC;
            }
            score = scoreC;
          }
          sb.append(" " + String.format("%.1f", score));
        }
        sb.append("] ");
      } else if (Lizzie.leelaz.isPondering()
          && Lizzie.board.getHistory().getCurrentHistoryNode().previous().isPresent()
          && Lizzie.board
                  .getHistory()
                  .getCurrentHistoryNode()
                  .previous()
                  .get()
                  .getData()
                  .getPlayouts()
              > 0) {
        sb.append("[");
        BoardData data =
            Lizzie.board.getHistory().getCurrentHistoryNode().previous().get().getData();
        sb.append(
            String.format("%.1f", winRate)
                + " "
                + Lizzie.frame.getPlayoutsString(data.getPlayouts()));
        if (data.isKataData) {
          sb.append(" " + String.format("%.1f", score));
        }
        sb.append("] ");
      }
      //    	  else {
      //        sb.append("[ ---   ---   --- ] ");
      //      }
    }
    if (hasEnginePkTitile && enginePkTitile != null) {
      sb.append(Lizzie.leelaz.oriEnginename);
      sb.append(visitsString + " ");
      setTitle(enginePkTitile + " " + sb.toString());
    } else {
      // sb.append(DEFAULT_TITLE);
      if (Lizzie.engineManager.isEmpty) {
        sb.append("Lizzie ");
      } else sb.append(Lizzie.leelaz.oriEnginename);
      if (!Lizzie.engineManager.isEmpty) {
        if (Lizzie.leelaz.isPondering()) sb.append(visitsString + " ");
        else sb.append(" - v/s ");
      }
      sb.append(playerTitle);
      sb.append(resultTitle);
      if (!fileNameTitle.equals("")) sb.append(" - " + fileNameTitle);

      //      if (Lizzie.leelaz.engineCommand().length() < 100)
      //        sb.append(" [" + Lizzie.leelaz.engineCommand() + "]");
      //      else sb.append(" [" + Lizzie.leelaz.engineCommand().substring(0, 100) + "...]");
      setTitle(sb.toString());
    }
  }

  private void setDisplayedBranchLength(int n) {
    boardRenderer.setDisplayedBranchLength(n);
  }

  private void setDisplayedBranchLength2(int n) {
    boardRenderer2.setDisplayedBranchLength(n);
  }

  //  private void setDisplayedBranchLengthSub(int n) {
  //    subBoardRenderer.setDisplayedBranchLength(n);
  //  }

  public void startRawBoard() {
    boolean onBranch = boardRenderer.isShowingBranch();
    int n = (onBranch ? 1 : BoardRenderer.SHOW_RAW_BOARD);
    boardRenderer.setDisplayedBranchLength(n);
  }

  public void stopRawBoard() {
    boardRenderer.setDisplayedBranchLength(BoardRenderer.SHOW_NORMAL_BOARD);
  }

  public boolean incrementDisplayedBranchLength(int n) {
    return boardRenderer.incrementDisplayedBranchLength(n);
  }

  public void resetTitle() {
    playerTitle = "";
    updateTitle();
  }

  public void copySgf() {
    try {
      // Get sgf content from game
      String sgfContent = SGFParser.saveToString(false);

      // Save to clipboard
      Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
      Transferable transferableString = new StringSelection(sgfContent);
      clipboard.setContents(transferableString, null);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public void pasteSgf() {
    // Get string from clipboard
    String sgfContent =
        Optional.ofNullable(Toolkit.getDefaultToolkit().getSystemClipboard().getContents(null))
            .filter(cc -> cc.isDataFlavorSupported(DataFlavor.stringFlavor))
            .flatMap(
                cc -> {
                  try {
                    return Optional.of((String) cc.getTransferData(DataFlavor.stringFlavor));
                  } catch (UnsupportedFlavorException e) {
                    e.printStackTrace();
                  } catch (IOException e) {
                    e.printStackTrace();
                  }
                  return Optional.empty();
                })
            .orElse("");

    // Load game contents from sgf string
    if (!sgfContent.isEmpty()) {
      SGFParser.loadFromString(sgfContent);
      Lizzie.board.setMovelistAll();
      if (Lizzie.leelaz.isPondering()) Lizzie.leelaz.ponder();
    }
    Lizzie.frame.resetMovelistFrameandAnalysisFrame();
    Lizzie.frame.setVisible(true);
  }

  public boolean resetMovelistFrameandAnalysisFrame() {
    boolean hadSetFrame = false;
    if (Lizzie.config.uiConfig.optBoolean("show-suggestions-frame", false)) {
      if (analysisFrame == null) toggleBestMoves();
      else {
        toggleBestMoves();
        toggleBestMoves();
      }
      hadSetFrame = true;
    } else if (analysisFrame != null && analysisFrame.isVisible()) {
      toggleBestMoves();
      toggleBestMoves();
      hadSetFrame = true;
    }
    if (Lizzie.config.uiConfig.optBoolean("show-badmoves-frame", false)) {
      if (moveListFrame == null) toggleBadMoves();
      else {
        toggleBadMoves();
        toggleBadMoves();
      }
      hadSetFrame = true;
    } else if (moveListFrame != null && moveListFrame.isVisible()) {
      toggleBadMoves();
      toggleBadMoves();
      hadSetFrame = true;
    }
    return hadSetFrame;
  }

  /**
   * Draw the Comment of the Sgf file
   *
   * @param g
   * @param x
   * @param y
   * @param w
   * @param h
   */
  //  private void addText(String text) {
  //    try {
  //      htmlDoc.remove(0, htmlDoc.getLength());
  //      htmlKit.insertHTML(htmlDoc, htmlDoc.getLength(), text, 0, 0, null);
  //      commentPane.setCaretPosition(htmlDoc.getLength());
  //    } catch (BadLocationException | IOException e) {
  //      e.printStackTrace();
  //    }
  //  }

  //  private void drawVariationTree(Graphics2D g, int x, int y, int w, int h) {
  //
  //	  variationCommentRect = new Rectangle(x, y,w,h);
  //	    variationTree.draw(
  //	    		g,
  //	        commentRect.x,
  //	        commentRect.y,
  //	        commentRect.width,
  //	        commentRect.height
  //	        );
  //	   // variationScrollPane=new JScrollPane(variationCommentRect);
  //  }

  private void drawComment(Graphics2D g, int x, int y, int w, int h) {
    if (w < 10 || h < 10) {
      commentScrollPane.setBounds(0, 0, 0, 0);
      blunderContentPane.setBounds(0, 0, 0, 0);
      return;
    }
    if (Lizzie.config.isShowingBlunderTabel) {
      if (Utils.zoomIn(x) != blunderContentPane.getX()
          || Utils.zoomIn(y) + (Lizzie.config.showDoubleMenu ? topPanelHeight : 0)
              != blunderContentPane.getY()
          || Utils.zoomIn(w) != blunderContentPane.getWidth()
          || Utils.zoomIn(h) != blunderContentPane.getHeight()) {
        {
          blunderContentPane.setBounds(
              Utils.zoomIn(x),
              Utils.zoomIn(y) + (Lizzie.config.showDoubleMenu ? topPanelHeight : 0),
              Utils.zoomIn(w),
              Utils.zoomIn(h));
          blunderContentPane.revalidate();
        }
      }
    } else {
      if (Utils.zoomIn(x) != commentScrollPane.getX()
          || Utils.zoomIn(y) + (Lizzie.config.showDoubleMenu ? topPanelHeight : 0)
              != commentScrollPane.getY()
          || Utils.zoomIn(w) != commentScrollPane.getWidth()
          || Utils.zoomIn(h) != commentScrollPane.getHeight()) {
        commentScrollPane.setBounds(
            Utils.zoomIn(x),
            Utils.zoomIn(y) + (Lizzie.config.showDoubleMenu ? topPanelHeight : 0),
            Utils.zoomIn(w),
            Utils.zoomIn(h));
        commentEditPane.setBounds(
            Utils.zoomIn(x),
            Utils.zoomIn(y) + (Lizzie.config.showDoubleMenu ? topPanelHeight : 0),
            Utils.zoomIn(w),
            Utils.zoomIn(h));
      }

      boolean isLoadingEngine = false;
      boolean isTuningEngine = false;
      if (((Lizzie.leelaz != null && !Lizzie.leelaz.isLoaded())
          || (Lizzie.engineManager.isPreEngineGame
              && (!Lizzie.engineManager
                      .engineList
                      .get(Lizzie.engineManager.engineGameInfo.whiteEngineIndex)
                      .isLoaded()
                  || !Lizzie.engineManager
                      .engineList
                      .get(Lizzie.engineManager.engineGameInfo.blackEngineIndex)
                      .isLoaded())))) isLoadingEngine = true;
      if (isLoadingEngine) {
        if ((Lizzie.leelaz != null && Lizzie.leelaz.isTuning)
            || (Lizzie.engineManager.isPreEngineGame
                && (!Lizzie.engineManager.engineList.get(
                            Lizzie.engineManager.engineGameInfo.whiteEngineIndex)
                        .isTuning
                    || !Lizzie.engineManager.engineList.get(
                            Lizzie.engineManager.engineGameInfo.blackEngineIndex)
                        .isTuning))) {
          isTuningEngine = true;
        }
      }
      //    if (isLoadingEngine && !isCommentArea) {
      //    	isCommentArea = false;
      //    	setCommentComponet();
      //    }
      //    if (!isLoadingEngine) {
      //    	isCommentArea = !urlSgf;
      //    	setCommentComponet();
      //    }
      String comment = "";
      if (isInPlayMode()) comment = "";
      else {
        if (cachedIsLoading != isLoadingEngine) {
          cachedIsLoading = isLoadingEngine;
          if (isLoadingEngine) {
            DefaultCaret caret = (DefaultCaret) commentTextArea.getCaret();
            caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
            DefaultCaret caret2 = (DefaultCaret) commentTextPane.getCaret();
            caret2.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
          } else {
            commentScrollPane.getVerticalScrollBar().setValue(0);
            DefaultCaret caret = (DefaultCaret) commentTextArea.getCaret();
            caret.setUpdatePolicy(DefaultCaret.NEVER_UPDATE);
            DefaultCaret caret2 = (DefaultCaret) commentTextPane.getCaret();
            caret2.setUpdatePolicy(DefaultCaret.NEVER_UPDATE);
          }
        }
        if (isLoadingEngine) {
          if (Lizzie.gtpConsole != null) {
            comment = Lizzie.gtpConsole.console.getText();
            if (!Lizzie.config.showStatus && isTuningEngine)
              comment += Lizzie.resourceBundle.getString("LizzieFrame.display.tuning");
          }
        } else {
          if (Lizzie.engineManager.isEngineGame
              && Lizzie.config.showPreviousBestmovesInEngineGame
              && Lizzie.board.getHistory().getCurrentHistoryNode().previous().isPresent())
            comment =
                Lizzie.board
                    .getHistory()
                    .getCurrentHistoryNode()
                    .previous()
                    .get()
                    .getData()
                    .comment;
          else {
            if (Lizzie.board.getHistory().getData().comment.equals("")) {
              if ((Lizzie.leelaz.isPondering() || Lizzie.engineManager.isEngineGame)
                  && Lizzie.config.appendWinrateToComment
                  && Lizzie.board.getHistory().getCurrentHistoryNode().previous().isPresent())
                comment =
                    Lizzie.board
                        .getHistory()
                        .getCurrentHistoryNode()
                        .previous()
                        .get()
                        .getData()
                        .comment;
            } else comment = Lizzie.board.getHistory().getData().comment;
            if (Lizzie.engineManager.isEngineGame) {
              int index =
                  comment.indexOf("\n" + Lizzie.resourceBundle.getString("SGFParse.moveTime"));
              if (index > 0) comment = comment.substring(0, index);
            }
          }
        }
        if (Lizzie.engineManager.isEngineGame && !Lizzie.config.showPreviousBestmovesInEngineGame) {
          comment =
              comment
                  + (comment.equals("") ? "" : "\n")
                  + resourceBundle.getString("SGFParse.moveTime")
                  + (System.currentTimeMillis()
                          - (Lizzie.engineManager.engineGameInfo.isGenmove
                              ? (Lizzie.engineManager.engineList.get(
                                      Lizzie.board.getHistory().isBlacksTurn()
                                          ? Lizzie.engineManager.engineGameInfo.blackEngineIndex
                                          : Lizzie.engineManager.engineGameInfo.whiteEngineIndex)
                                  .pkMoveStartTime)
                              : Lizzie.leelaz.getStartPonderTime()))
                      / 1000
                  + resourceBundle.getString("SGFParse.seconds");
        }
      }
      // System.out.println(getWidth());
      if (Lizzie.config.commentFontSize <= 0) {
        int fontSize;
        if (Lizzie.config.showLargeSubBoard() || Lizzie.config.showLargeWinrate()) {
          fontSize =
              (int)
                  (min(
                          (getWidth() > 1.75 * getHeight() ? 1.75 * getHeight() : getWidth())
                              * 0.43,
                          getHeight())
                      * 0.0225);
        } else fontSize = (int) (min(getWidth() * 0.6, getHeight()) * 0.0225);
        if (fontSize > Lizzie.config.frameFontSize + 3) {
          fontSize = Lizzie.config.frameFontSize + 3;
        } else if (fontSize < Lizzie.config.frameFontSize - 2) {
          fontSize = Lizzie.config.frameFontSize - 2;
        }
        if (isCommentArea) {
          if (commentFontSize != fontSize) {
            commentFontSize = fontSize;
            if (isCommentArea) {
              commentTextArea.setFont(
                  new Font(Lizzie.config.uiFontName, Font.PLAIN, commentFontSize));
              commentEditTextPane.setFont(
                  new Font(Lizzie.config.uiFontName, Font.PLAIN, commentFontSize));
            }
          }
        } else {
          if (commentPaneFontSize != fontSize) {
            commentPaneFontSize = fontSize;
            String style =
                "body {background:"
                    + String.format(
                        "%02x%02x%02x",
                        Lizzie.config.commentBackgroundColor.getRed(),
                        Lizzie.config.commentBackgroundColor.getGreen(),
                        Lizzie.config.commentBackgroundColor.getBlue())
                    + "; color:#"
                    + String.format(
                        "%02x%02x%02x",
                        Lizzie.config.commentFontColor.getRed(),
                        Lizzie.config.commentFontColor.getGreen(),
                        Lizzie.config.commentFontColor.getBlue())
                    + "; font-family:"
                    + Lizzie.config.uiFontName
                    + ", Consolas, Menlo, Monaco, 'Ubuntu Mono', monospace;"
                    + (commentPaneFontSize > 0 ? "font-size:" + commentPaneFontSize : "")
                    + "}";
            htmlStyle.addRule(style);
          }
        }
      }
      if (!isCommentArea) {
        comment = comment.replaceAll("(\r\n)|(\n)", "<br />").replaceAll(" ", "&nbsp;");
      }
      try {
        if (isLoadingEngine) {
          if (!cachedComment.equals(comment)) setCommentText(comment);
          cachedComment = comment;
        } else {
          setCommentText(comment);
        }
      } catch (Exception ex) {
      }
    }
  }

  public void doCommentAfterMove() {
    commentScrollPane.getVerticalScrollBar().setValue(0);
  }

  public void setCommentEditable(boolean isEditable) {
    if (isEditable) {
      if (((Lizzie.leelaz != null && !Lizzie.leelaz.isLoaded())
          || (Lizzie.engineManager.isPreEngineGame
              && (!Lizzie.engineManager
                      .engineList
                      .get(Lizzie.engineManager.engineGameInfo.whiteEngineIndex)
                      .isLoaded()
                  || !Lizzie.engineManager
                      .engineList
                      .get(Lizzie.engineManager.engineGameInfo.blackEngineIndex)
                      .isLoaded())))) return;
      String text = Lizzie.board.getHistory().getCurrentHistoryNode().getData().comment;
      if (text.length() > 0) text = text + '\n';
      commentEditTextPane.setText(text);
      commentEditPane.setVisible(true);
      commentEditTextPane.requestFocus(true);
      commentScrollPane.setVisible(false);
    } else if (commentEditPane.isVisible()) {
      commentScrollPane.setVisible(true);
      commentEditPane.setVisible(false);
      String text = commentEditTextPane.getText();
      if (text.endsWith("\n")) text = text.substring(0, text.length() - 1);
      Lizzie.board.getHistory().getCurrentHistoryNode().getData().comment = text;
      if (Lizzie.config.isScaled) repaint();
    }
  }

  public void setCommentPaneOrArea(boolean isArea) {
    isCommentArea = isArea;
    setCommentComponet();
  }

  public void resetCommentComponent() {
    commentTextPane.setBackground(Lizzie.config.commentBackgroundColor);
    commentTextPane.setForeground(Lizzie.config.commentFontColor);
    String style =
        "body {background:"
            + String.format(
                "%02x%02x%02x",
                Lizzie.config.commentBackgroundColor.getRed(),
                Lizzie.config.commentBackgroundColor.getGreen(),
                Lizzie.config.commentBackgroundColor.getBlue())
            + "; color:#"
            + String.format(
                "%02x%02x%02x",
                Lizzie.config.commentFontColor.getRed(),
                Lizzie.config.commentFontColor.getGreen(),
                Lizzie.config.commentFontColor.getBlue())
            + "; font-family:"
            + Lizzie.config.uiFontName
            + ", Consolas, Menlo, Monaco, 'Ubuntu Mono', monospace;"
            + (Lizzie.config.commentFontSize > 0
                ? Lizzie.config.commentFontSize
                : commentFontSize > 0 ? commentFontSize : Lizzie.config.frameFontSize)
            + "}";
    htmlStyle.addRule(style);
    commentTextArea.setFont(
        new Font(
            Lizzie.config.uiFontName,
            Font.PLAIN,
            Lizzie.config.commentFontSize > 0
                ? Lizzie.config.commentFontSize
                : commentFontSize > 0 ? commentFontSize : Lizzie.config.frameFontSize));
    commentTextArea.setBackground(Lizzie.config.commentBackgroundColor);
    commentTextArea.setForeground(Lizzie.config.commentFontColor);
    commentScrollPane.setBackground(Lizzie.config.commentBackgroundColor);
  }

  private void setCommentComponet() {
    try {
      if (cachedIsCommentArea != isCommentArea) {
        cachedIsCommentArea = isCommentArea;
        if (isCommentArea) commentScrollPane.setViewportView(commentTextArea);
        else commentScrollPane.setViewportView(commentTextPane);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private void setCommentText(String comment) {
    if (isCommentArea) commentTextArea.setText(comment);
    else commentTextPane.setText(comment);
  }

  private void setCommentSize(int width, int height) {
    if (isCommentArea) commentTextArea.setSize(width, height);
    else commentTextPane.setSize(width, height);
  }

  public double lastWinrateDiff(BoardHistoryNode node) {
    // Last winrate
    Optional<BoardData> lastNode = node.previous().flatMap(n -> Optional.of(n.getData()));
    boolean validLastWinrate = lastNode.map(d -> d.getPlayouts() > 0).orElse(false);
    double lastWR = validLastWinrate ? lastNode.get().winrate : 50;

    // Current winrate
    BoardData data = node.getData();
    boolean validWinrate = false;
    double curWR = 50;
    //    if (data == Lizzie.board.getHistory().getData()) {
    //      Leelaz.WinrateStats stats = Lizzie.leelaz.getWinrateStats();
    //      curWR = stats.maxWinrate;
    //      validWinrate = (stats.totalPlayouts > 0);
    //      if (isPlayingAgainstLeelaz
    //          && playerIsBlack == !Lizzie.board.getHistory().getData().blackToPlay) {
    //        validWinrate = false;
    //      }
    //    } else {
    validWinrate = (data.getPlayouts() > 0);
    curWR = validWinrate ? data.winrate : 100 - lastWR;
    //  }

    // Last move difference winrate
    if (validLastWinrate && validWinrate) {
      return 100 - lastWR - curWR;
    } else {
      return 0;
    }
  }

  public Color getBlunderNodeColor(BoardHistoryNode node) {
    if (Lizzie.engineManager.isEngineGame || Lizzie.board.isPkBoard) {
      if (node.previous().isPresent() && node.previous().get().previous().isPresent()) {
        double diffWinrate =
            node.previous().get().previous().get().getData().getWinrate()
                - node.getData().getWinrate();
        Optional<Double> st =
            diffWinrate >= 0
                ? Lizzie.config.blunderWinrateThresholds.flatMap(
                    l -> l.stream().filter(t -> (t >= 0 && t <= diffWinrate)).reduce((f, s) -> s))
                : Lizzie.config.blunderWinrateThresholds.flatMap(
                    l -> l.stream().filter(t -> (t <= 0 && t >= diffWinrate)).reduce((f, s) -> f));
        if (st.isPresent()) {
          return Lizzie.config.blunderNodeColors.map(m -> m.get(st.get())).get();
        } else {
          return Color.WHITE;
        }
      } else return Color.WHITE;
    }
    double diffWinrate = lastWinrateDiff(node);
    Optional<Double> st =
        diffWinrate >= 0
            ? Lizzie.config.blunderWinrateThresholds.flatMap(
                l -> l.stream().filter(t -> (t >= 0 && t <= diffWinrate)).reduce((f, s) -> s))
            : Lizzie.config.blunderWinrateThresholds.flatMap(
                l -> l.stream().filter(t -> (t <= 0 && t >= diffWinrate)).reduce((f, s) -> f));
    if (st.isPresent()) {
      return Lizzie.config.blunderNodeColors.map(m -> m.get(st.get())).get();
    } else {
      return Color.WHITE;
    }
  }

  public void autoReplayBranch() {
    if (isAutoReplying) return;
    isAutoReplying = true;
    Runnable runnable =
        new Runnable() {
          public void run() {
            while (Lizzie.config.autoReplayBranch) {
              if (mouseOverChanged) {
                mouseOverChanged = false;
                if (Lizzie.config.autoReplayDisplayEntireVariationsFirst) {
                  for (int s = 0; s < 100; s++) {
                    if (mouseOverChanged) break;
                    try {
                      Thread.sleep((int) (Lizzie.config.displayEntireVariationsFirstSeconds * 10));
                    } catch (InterruptedException e) {
                      // TODO Auto-generated catch block
                      e.printStackTrace();
                    }
                  }
                } else {
                  for (int s = 0; s < 20; s++) {
                    if (mouseOverChanged) break;
                    try {
                      Thread.sleep((int) (Lizzie.config.replayBranchIntervalSeconds * 15));
                    } catch (InterruptedException e) {
                      // TODO Auto-generated catch block
                      e.printStackTrace();
                    }
                  }
                }
              }
              if (!mouseOverChanged) {
                if (floatBoard != null) floatBoard.boardRenderer.incrementDisplayedBranchLength(1);
                boardRenderer.incrementDisplayedBranchLength(1);
              }
              refresh();
              for (int i = 0; i < 20; i++) {
                try {
                  Thread.sleep((int) (Lizzie.config.replayBranchIntervalSeconds * 50));
                } catch (InterruptedException e) {
                  e.printStackTrace();
                }
                if (!Lizzie.config.autoReplayBranch) break;
                if (mouseOverChanged) {
                  break;
                }
              }
            }
            isAutoReplying = false;
          }
        };
    Thread thread = new Thread(runnable);
    thread.start();
  }

  public void replayBranch() {
    if (isReplayVariation || Lizzie.config.autoReplayBranch) return;
    int replaySteps = boardRenderer.getReplayBranch();
    if (replaySteps <= 0) return; // Bad steps or no branch
    int oriBranchLength = boardRenderer.getDisplayedBranchLength();
    isReplayVariation = true;
    final boolean oriPonder = Lizzie.leelaz.isPondering();
    if (!Lizzie.config.noRefreshOnMouseMove && Lizzie.leelaz.isPondering())
      Lizzie.leelaz.togglePonder();
    Runnable runnable =
        new Runnable() {
          public void run() {
            int secs = (int) (Lizzie.config.replayBranchIntervalSeconds * 1000);
            for (int i = 1; i < replaySteps + 1; i++) {
              if (!isReplayVariation) break;
              setDisplayedBranchLength(i + 1);
              repaint();
              try {
                Thread.sleep(secs);
              } catch (InterruptedException e) {
                e.printStackTrace();
              }
            }
            boardRenderer.setDisplayedBranchLength(oriBranchLength);
            isReplayVariation = false;
            if (!Lizzie.config.noRefreshOnMouseMove && oriPonder && !Lizzie.leelaz.isPondering())
              Lizzie.leelaz.togglePonder();
          }
        };
    Thread thread = new Thread(runnable);
    thread.start();
  }

  public void replayBranchIndependentMainBoard() {
    independentMainBoard.replayBranch();
  }

  public void DraggedMoved(int x, int y) {
    if (RightClickMenu.isVisible() || RightClickMenu2.isVisible()) {
      return;
    }

    //    if (isshowrightmenu) {
    //      isshowrightmenu = false;
    //    }

    repaint();
  }

  public void DraggedDragged(int x, int y) {
    if (draggedstone != Stone.EMPTY) {
      Optional<int[]> boardCoordinates = boardRenderer.convertScreenToCoordinates(x, y);
      if (boardCoordinates.isPresent()) {
        int[] coords = boardCoordinates.get();

        boardRenderer.drawmovestone(coords[0], coords[1], draggedstone);
        if (Lizzie.frame.extraMode == 2)
          boardRenderer2.drawmovestone(coords[0], coords[1], draggedstone);
        repaint();
      }
    }
  }

  public void DraggedReleased(int x, int y) {
    DraggedReleased(x, y, boardRenderer, draggedstone, Input.Draggedmode, draggedCoords);
  }

  public void DraggedReleased(
      int x,
      int y,
      BoardRenderer boardRenderer,
      Stone draggedstone,
      boolean Draggedmode,
      int[] draggedCoords) {
    GameInfo gameInfo = Lizzie.board.getHistory().getGameInfo();
    boardRenderer.removedrawmovestone();
    Optional<int[]> boardCoordinates = boardRenderer.convertScreenToCoordinates(x, y);
    if (boardCoordinates.isPresent()) {
      if (draggedstone != Stone.BLACK && draggedstone != Stone.WHITE) {
        draggedstone = Stone.EMPTY;
        return;
      }
      int[] coords = boardCoordinates.get();
      if (coords[0] == startcoords[0] && coords[1] == startcoords[1]) {
        // System.out.println("拖动前后一致");
        draggedstone = Stone.EMPTY;
        refresh();
      } else {
        // System.out.println("拖动前后不一致");
        // System.out.println("拖动的棋子序号:"+draggedmovenumer);
        boolean oriPlaySound = Lizzie.config.playSound;
        Lizzie.config.playSound = false;
        Stone stone = Lizzie.board.getstonestat(coords);
        if (stone != Stone.EMPTY) {
          draggedstone = Stone.EMPTY;
          refresh();
          return;
        }
        Lizzie.board.savelistforeditmode();
        int currentMoveNumber = Lizzie.board.getcurrentmovenumber();
        int moveNumber = Lizzie.board.moveNumberByCoord(draggedCoords);
        if (moveNumber > 0) {
          MoveLinkedList reStoreMainListHead =
              Lizzie.board.getMainMoveLinkedListBetween(
                  Lizzie.board.getBoardHistoryNodeByCoords(draggedCoords),
                  Lizzie.board.getHistory().getCurrentHistoryNode());
          if (reStoreMainListHead != null) {
            while (reStoreMainListHead.variations.size() > 0)
              reStoreMainListHead = reStoreMainListHead.variations.get(0);
            reStoreMainListHead.x = coords[0];
            reStoreMainListHead.y = coords[1];
          }
          Lizzie.board.gotoAnyMoveByCoords(draggedCoords);
          int index =
              Lizzie.board.getHistory().getCurrentHistoryNode().previous().isPresent()
                  ? Lizzie.board
                      .getHistory()
                      .getCurrentHistoryNode()
                      .previous()
                      .get()
                      .findIndexOfNode(Lizzie.board.getHistory().getCurrentHistoryNode())
                  : -1;
          MoveLinkedList listHead =
              Lizzie.board.getMoveLinkedListAfter(
                  Lizzie.board.getHistory().getCurrentHistoryNode());
          if (listHead == null) {
            Lizzie.board.deleteMove();
            Lizzie.board.place(coords[0], coords[1]);
          } else {
            Lizzie.board.deleteMoveNoHint();
            listHead.x = coords[0];
            listHead.y = coords[1];
            Lizzie.board.placeLinkedList(listHead, null, true, index);
            // 返回原点
            Lizzie.board.gotoAnyMoveByCoords(coords);
            if (reStoreMainListHead != null)
              Lizzie.board.placeLinkedListReverse(reStoreMainListHead);
          }
        } else {
          MoveLinkedList reStoreMainListHead =
              Lizzie.board.getMainMoveLinkedListBetween(
                  Lizzie.board.getHistory().getStart(),
                  Lizzie.board.getHistory().getCurrentHistoryNode());
          if (reStoreMainListHead != null) {
            while (reStoreMainListHead.variations.size() > 0)
              reStoreMainListHead = reStoreMainListHead.variations.get(0);
            if (reStoreMainListHead.isPass && reStoreMainListHead.previous.isPresent())
              reStoreMainListHead = reStoreMainListHead.previous.get();
          }
          while (Lizzie.board.previousMove(false)) ;
          MoveLinkedList listHead =
              Lizzie.board.getMoveLinkedListAfter(
                  Lizzie.board.getHistory().getCurrentHistoryNode());
          if (listHead == null) {
            int startMoveNumber = 0;
            boolean blackToPlay = Lizzie.board.getHistory().getStart().getData().blackToPlay;
            if (Lizzie.board.hasStartStone) startMoveNumber += Lizzie.board.startStonelist.size();
            Lizzie.board.editmovelist(
                Lizzie.board.tempallmovelist, draggedCoords, coords[0], coords[1]);
            Lizzie.board.clearforedit();
            Lizzie.board.setMoveListWithFlattenExit(
                Lizzie.board.tempallmovelist, startMoveNumber, blackToPlay);
          } else {
            int startMoveNumber = 0;
            boolean blackToPlay = Lizzie.board.getHistory().getStart().getData().blackToPlay;
            if (Lizzie.board.hasStartStone) startMoveNumber += Lizzie.board.startStonelist.size();
            Lizzie.board.editmovelist(
                Lizzie.board.tempallmovelist, draggedCoords, coords[0], coords[1]);
            Lizzie.board.clearforedit();
            Lizzie.board.setMoveListWithFlattenExit(
                Lizzie.board.tempallmovelist, startMoveNumber, blackToPlay);
            listHead.needSkip = true;
            Lizzie.board.placeLinkedList(listHead, null, false, -1);
            // 返回原点
            while (Lizzie.board.previousMove(false)) ;
            if (reStoreMainListHead != null)
              Lizzie.board.placeLinkedListReverse(reStoreMainListHead);
          }
        }
        refresh();
        Lizzie.config.playSound = oriPlaySound;
      }
    }
    draggedstone = Stone.EMPTY;
    Input.Draggedmode = false;
    if (independentMainBoard != null) independentMainBoard.Draggedmode = false;
    Lizzie.board.getHistory().setGameInfo(gameInfo);
  }

  public void selectForceAllowAvoid() {
    //  Lizzie.board.convertCoordinatesToName(coords[0], coords[1]);
    int minX = min(selectCoordsX1, selectCoordsX2);
    int minY = min(selectCoordsY1, selectCoordsY2);
    int xCounts = Math.abs(selectCoordsX1 - selectCoordsX2);
    int yCounts = Math.abs(selectCoordsY1 - selectCoordsY2);
    //    featurecat.lizzie.gui.RightClickMenu.kataAllowTopLeft =
    //        Lizzie.board.convertCoordinatesToName(minX, minY);
    //    featurecat.lizzie.gui.RightClickMenu.kataAllowBottomRight =
    //        Lizzie.board.convertCoordinatesToName(minX + xCounts, minY + yCounts);
    String[] exsitCoords;
    if (selectForceAllow) exsitCoords = LizzieFrame.allowcoords.split(",");
    else exsitCoords = LizzieFrame.avoidcoords.split(",");
    for (int i = 0; i <= xCounts; i++) {
      for (int j = 0; j <= yCounts; j++) {
        int x = minX + i;
        int y = minY + j;
        boolean needSkip = false;
        String coordsName = Lizzie.board.convertCoordinatesToName(x, y);
        for (String existedCoords : exsitCoords) {
          if (coordsName.equals(existedCoords)) {
            needSkip = true;
            break;
          }
        }
        if (needSkip) continue;
        if (selectForceAllow) {
          if (LizzieFrame.allowcoords != "") {
            LizzieFrame.allowcoords = LizzieFrame.allowcoords + "," + coordsName;
          } else {
            LizzieFrame.allowcoords = coordsName;
          }
        } else {
          if (LizzieFrame.avoidcoords != "") {
            LizzieFrame.avoidcoords = LizzieFrame.avoidcoords + "," + coordsName;
          } else {
            LizzieFrame.avoidcoords = coordsName;
          }
        }
      }
    }
    if (selectForceAllow) {
      LizzieFrame.avoidcoords = "";
      Lizzie.leelaz.analyzeAvoid("allow", LizzieFrame.allowcoords, Lizzie.config.selectAllowMoves);
    } else {
      LizzieFrame.allowcoords = "";
      Lizzie.leelaz.analyzeAvoid("avoid", LizzieFrame.avoidcoords, Lizzie.config.selectAvoidMoves);
    }
    Input.selectMode = false;
    menu.clearAllowAvoidButtonState();
  }

  public void selectDragged(int x, int y) {
    if (selectX1 > 0 && selectY1 > 0)
      boardRenderer.drawSelectedRect(selectX1, selectY1, x, y, selectForceAllow);
    else boardRenderer.removeSelectedRect();
    repaint();
  }

  public void selectReleased(int x, int y) {
    if (selectX1 > 0 && selectY1 > 0) {
      Optional<int[]> boardCoordinates =
          boardRenderer.convertScreenToCoordinatesForSelect(
              min(selectX1, x), max(selectX1, x), min(selectY1, y), max(selectY1, y));
      if (boardCoordinates.isPresent()) {
        //     selectX2 = x;
        //     selectY2 = y;
        int[] coords = boardCoordinates.get();
        selectCoordsX1 = coords[0];
        selectCoordsY1 = coords[1];
        selectCoordsX2 = coords[2];
        selectCoordsY2 = coords[3];
        selectForceAllowAvoid();
        if (selectForceAllow)
          boardRenderer.drawAllSelectedRectByCoords(selectForceAllow, LizzieFrame.allowcoords);
        else boardRenderer.drawAllSelectedRectByCoords(selectForceAllow, LizzieFrame.avoidcoords);
        Lizzie.board.clearbestmovesafter(Lizzie.board.getHistory().getStart());
        repaint();
      } else {
        selectCoordsX2 = -1;
        selectCoordsY2 = -1;
      }
    }
  }

  public void selectPressed(int x, int y, boolean isFromAlt) {
    //  Optional<int[]> boardCoordinates = boardRenderer.convertScreenToCoordinatesForSelect(x, y);
    //   if (boardCoordinates.isPresent()) {
    selectX1 = x;
    selectY1 = y;
    if (isFromAlt) {
      selectForceAllow = true;
      isKeepingForce = true;
    }
    //      int[] coords = boardCoordinates.get();
    //      selectCoordsX1 = coords[0];
    //      selectCoordsY1 = coords[1];
    //    } else {
    //      selectX1 = -1;
    //      selectY1 = -1;
    //      selectCoordsX1 = -1;
    //      selectCoordsY1 = -1;
    //   }
  }

  public void togglePolicy() {
    if (isShowingHeatmap) {
      Lizzie.leelaz.toggleHeatmap(true);
    }
    if (Lizzie.leelaz.isZen) {
      isShowingPolicy = false;
      Lizzie.leelaz.toggleHeatmap(false);
      return;
    }
    if (!isShowingPolicy) {
      // Lizzie.leelaz.isheatmap = true;
      isShowingPolicy = true;
      // if (!Lizzie.leelaz.isPondering()) lastponder = false;
      // else {
      // lastponder = true;
      // }
      //
      if (!Lizzie.leelaz.isPondering() && Lizzie.board.getData().bestMoves.isEmpty())
        Lizzie.leelaz.ponder();
    } else {
      isShowingPolicy = false;
      handleAfterDrawGobanBottom();
      // if (lastponder) Lizzie.leelaz.ponder();
    }
    Lizzie.frame.refresh();
  }

  public static class HtmlKit extends HTMLEditorKit {
    private StyleSheet style = new StyleSheet();

    @Override
    public void setStyleSheet(StyleSheet styleSheet) {
      style = styleSheet;
    }

    @Override
    public StyleSheet getStyleSheet() {
      if (style == null) {
        style = super.getStyleSheet();
      }
      return style;
    }
  }

  public void addSuggestionAsBranch() {
    boardRenderer.addSuggestionAsBranch();
    if (Lizzie.leelaz.isPondering()) Lizzie.leelaz.ponder();
  }

  public void doBranchSub(int subOrder, int moveTo) {
    SubBoardRenderer subBoardRendererThis;
    switch (subOrder) {
      case 0:
        subBoardRendererThis = subBoardRenderer;
        break;
      case 1:
        subBoardRendererThis = subBoardRenderer2;
        break;
      case 2:
        subBoardRendererThis = subBoardRenderer3;
        break;
      case 3:
        subBoardRendererThis = subBoardRenderer4;
        break;
      default:
        subBoardRendererThis = subBoardRenderer;
    }
    if (subBoardRendererThis.isShowingNormalBoard()) {
      subBoardRendererThis.setDisplayedBranchLength(1);
      subBoardRendererThis.wheeled = true;
    } else if (moveTo > 0) {
      {
        if (subBoardRendererThis.getReplayBranch()
            > subBoardRendererThis.getDisplayedBranchLength()) {
          subBoardRendererThis.incrementDisplayedBranchLength(1);
          subBoardRendererThis.wheeled = true;
        }
      }

    } else {
      if (subBoardRendererThis.isShowingNormalBoard()) {
        subBoardRendererThis.setDisplayedBranchLength(subBoardRendererThis.getReplayBranch());
      } else {
        if (subBoardRendererThis.getDisplayedBranchLength() > 1) {
          subBoardRendererThis.incrementDisplayedBranchLength(-1);
          subBoardRendererThis.wheeled = true;
        }
      }
    }
  }

  public void doBranch(int moveTo) {
    if (moveTo > 0) {
      if (boardRenderer.isShowingNormalBoard()) {
        setDisplayedBranchLength(2);
        if (extraMode == 2) setDisplayedBranchLength2(2);
      } else if (boardRenderer.isShowingUnImportantBoard()) {
        setDisplayedBranchLength(2);
        if (extraMode == 2) setDisplayedBranchLength2(2);
      } else {
        if (boardRenderer.getReplayBranch() > boardRenderer.getDisplayedBranchLength()) {
          boardRenderer.incrementDisplayedBranchLength(1);
        }
        if (extraMode == 2)
          if (boardRenderer2.getReplayBranch() > boardRenderer2.getDisplayedBranchLength()) {
            boardRenderer2.incrementDisplayedBranchLength(1);
          }
      }
    } else {
      if (boardRenderer.isShowingNormalBoard()) {
        setDisplayedBranchLength(boardRenderer.getBranchLength() - 1);
      } else {
        if (boardRenderer.getDisplayedBranchLength() > 1) {
          boardRenderer.incrementDisplayedBranchLength(-1);
        }
      }
      if (extraMode == 2) {
        if (boardRenderer2.isShowingNormalBoard()) {
          setDisplayedBranchLength2(boardRenderer.getReplayBranch());
        } else {
          if (boardRenderer2.getDisplayedBranchLength() > 1) {
            boardRenderer2.incrementDisplayedBranchLength(-1);
          }
        }
      }
    }
  }

  public void saveImage() {
    Runnable runnable =
        new Runnable() {
          public void run() {
            try {
              JSONObject filesystem = Lizzie.config.persisted.getJSONObject("filesystem");
              JFileChooser chooser = new JFileChooser(filesystem.getString("last-image-folder"));
              chooser.setAcceptAllFileFilterUsed(false);
              //    String writerNames[] = ImageIO.getWriterFormatNames();
              FileNameExtensionFilter filter1 = new FileNameExtensionFilter("*.png", "PNG");
              FileNameExtensionFilter filter2 = new FileNameExtensionFilter("*.jpg", "JPG", "JPEG");
              FileNameExtensionFilter filter3 = new FileNameExtensionFilter("*.gif", "GIF");
              FileNameExtensionFilter filter4 = new FileNameExtensionFilter("*.bmp", "BMP");
              chooser.addChoosableFileFilter(filter1);
              chooser.addChoosableFileFilter(filter2);
              chooser.addChoosableFileFilter(filter3);
              chooser.addChoosableFileFilter(filter4);
              chooser.setMultiSelectionEnabled(false);
              int result = chooser.showSaveDialog(null);
              if (result == JFileChooser.APPROVE_OPTION) {
                File file = chooser.getSelectedFile();
                filesystem.put("last-image-folder", file.getParent());
                String ext =
                    chooser.getFileFilter() instanceof FileNameExtensionFilter
                        ? ((FileNameExtensionFilter) chooser.getFileFilter())
                            .getExtensions()[0].toLowerCase()
                        : "";
                if (!Utils.isBlank(ext)) {
                  if (!chooser.getFileFilter().accept(file)) {
                    file = new File(file.getPath() + "." + ext);
                  }
                }
                if (file.exists()) {
                  int ret =
                      JOptionPane.showConfirmDialog(
                          Lizzie.frame,
                          resourceBundle.getString("LizzieFrame.fileExists"),
                          resourceBundle.getString("LizzieFrame.warning"),
                          JOptionPane.OK_CANCEL_OPTION);
                  if (ret == JOptionPane.CANCEL_OPTION || ret == -1) {
                    return;
                  }
                }
                BufferedImage bImg =
                    new BufferedImage(
                        mainPanel.getWidth(), mainPanel.getHeight(), BufferedImage.TYPE_INT_RGB);
                Graphics2D g1 = bImg.createGraphics();
                g1.drawImage(cachedBackground, 0, 0, null);
                g1.drawImage(cachedImage, 0, 0, null);
                if (Lizzie.config.showWinrateGraph && cachedWinrateImage != null && !showControls)
                  g1.drawImage(cachedWinrateImage, grx, gry, null);
                g1.dispose();
                try {
                  boolean supported = ImageIO.write(bImg, ext, file);
                  if (!supported) {
                    String displayedMessage =
                        String.format(
                            resourceBundle.getString("LizzieFrame.saveImageErrorHint1")
                                + " \"%s\"\n("
                                + resourceBundle.getString("LizzieFrame.saveImageErrorHint2")
                                + ")",
                            file.getName());
                    JOptionPane.showMessageDialog(
                        Lizzie.frame,
                        displayedMessage,
                        resourceBundle.getString("LizzieFrame.lizzieError"),
                        JOptionPane.ERROR_MESSAGE);
                  }
                } catch (IOException e) {
                  e.printStackTrace();
                }
              }
            } catch (Exception ex) {
            }
          }
        };
    Thread thread = new Thread(runnable);
    thread.start();
  }

  public void saveMainBoardPicture() {
    if (extraMode != 8)
      saveImage(
          Lizzie.frame.boardX, Lizzie.frame.boardY, Lizzie.frame.maxSize, Lizzie.frame.maxSize);
    else {
      saveImageToFile(getIndependMainBoardToClipboard());
    }
  }

  public void saveSubBoardPicture() {
    if (independentSubBoard != null && this.independentSubBoard.isVisible()) {
      saveImageToFile(getIndependSubBoardToClipboard());
    } else if (Lizzie.config.showSubBoard)
      saveImage(
          subBoardRenderer.x,
          subBoardRenderer.y,
          subBoardRenderer.boardWidth,
          subBoardRenderer.boardHeight);
    else {
      Utils.showMsg(resourceBundle.getString("LizzieFrame.saveSubBoardHint"));
    }
  }

  public void saveImageToFile(BufferedImage image) {
    Runnable runnable =
        new Runnable() {
          public void run() {
            try {
              JSONObject filesystem = Lizzie.config.persisted.getJSONObject("filesystem");
              JFileChooser chooser = new JFileChooser(filesystem.getString("last-image-folder"));
              chooser.setAcceptAllFileFilterUsed(false);
              FileNameExtensionFilter filter1 = new FileNameExtensionFilter("*.png", "PNG");
              FileNameExtensionFilter filter2 = new FileNameExtensionFilter("*.jpg", "JPG", "JPEG");
              FileNameExtensionFilter filter3 = new FileNameExtensionFilter("*.gif", "GIF");
              FileNameExtensionFilter filter4 = new FileNameExtensionFilter("*.bmp", "BMP");
              chooser.addChoosableFileFilter(filter1);
              chooser.addChoosableFileFilter(filter2);
              chooser.addChoosableFileFilter(filter3);
              chooser.addChoosableFileFilter(filter4);
              chooser.setMultiSelectionEnabled(false);
              int result = chooser.showSaveDialog(null);
              if (result == JFileChooser.APPROVE_OPTION) {
                File file = chooser.getSelectedFile();
                filesystem.put("last-image-folder", file.getParent());
                String ext =
                    chooser.getFileFilter() instanceof FileNameExtensionFilter
                        ? ((FileNameExtensionFilter) chooser.getFileFilter())
                            .getExtensions()[0].toLowerCase()
                        : "";
                if (!Utils.isBlank(ext)) {
                  if (!chooser.getFileFilter().accept(file)) {
                    file = new File(file.getPath() + "." + ext);
                  }
                }
                if (file.exists()) {
                  int ret =
                      JOptionPane.showConfirmDialog(
                          Lizzie.frame,
                          resourceBundle.getString("LizzieFrame.fileExists"),
                          resourceBundle.getString("LizzieFrame.warning"),
                          JOptionPane.OK_CANCEL_OPTION);
                  if (ret == JOptionPane.CANCEL_OPTION || ret == -1) {
                    return;
                  }
                }
                try {
                  boolean supported = ImageIO.write(image, ext, file);
                  if (!supported) {
                    String displayedMessage =
                        String.format(
                            resourceBundle.getString("LizzieFrame.saveImageErrorHint1")
                                + " \"%s\"\n("
                                + resourceBundle.getString("LizzieFrame.saveImageErrorHint2")
                                + ")",
                            file.getName());
                    JOptionPane.showMessageDialog(
                        Lizzie.frame,
                        displayedMessage,
                        resourceBundle.getString("LizzieFrame.lizzieError"),
                        JOptionPane.ERROR_MESSAGE);
                  }
                } catch (IOException e) {
                }
              }
            } catch (Exception ex) {
            }
          }
        };
    Thread thread = new Thread(runnable);
    thread.start();
  }

  public void saveImage(int x, int y, int width, int height) {
    Runnable runnable =
        new Runnable() {
          public void run() {
            try {
              JSONObject filesystem = Lizzie.config.persisted.getJSONObject("filesystem");
              JFileChooser chooser = new JFileChooser(filesystem.getString("last-image-folder"));
              chooser.setAcceptAllFileFilterUsed(false);
              FileNameExtensionFilter filter1 = new FileNameExtensionFilter("*.png", "PNG");
              FileNameExtensionFilter filter2 = new FileNameExtensionFilter("*.jpg", "JPG", "JPEG");
              FileNameExtensionFilter filter3 = new FileNameExtensionFilter("*.gif", "GIF");
              FileNameExtensionFilter filter4 = new FileNameExtensionFilter("*.bmp", "BMP");
              chooser.addChoosableFileFilter(filter1);
              chooser.addChoosableFileFilter(filter2);
              chooser.addChoosableFileFilter(filter3);
              chooser.addChoosableFileFilter(filter4);
              chooser.setMultiSelectionEnabled(false);
              int result = chooser.showSaveDialog(null);
              if (result == JFileChooser.APPROVE_OPTION) {
                File file = chooser.getSelectedFile();
                filesystem.put("last-image-folder", file.getParent());
                String ext =
                    chooser.getFileFilter() instanceof FileNameExtensionFilter
                        ? ((FileNameExtensionFilter) chooser.getFileFilter())
                            .getExtensions()[0].toLowerCase()
                        : "";
                if (!Utils.isBlank(ext)) {
                  if (!chooser.getFileFilter().accept(file)) {
                    file = new File(file.getPath() + "." + ext);
                  }
                }
                if (file.exists()) {
                  int ret =
                      JOptionPane.showConfirmDialog(
                          Lizzie.frame,
                          resourceBundle.getString("LizzieFrame.fileExists"),
                          resourceBundle.getString("LizzieFrame.warning"),
                          JOptionPane.OK_CANCEL_OPTION);
                  if (ret == JOptionPane.CANCEL_OPTION || ret == -1) {
                    return;
                  }
                }
                BufferedImage bImg =
                    new BufferedImage(
                        mainPanel.getWidth(), mainPanel.getHeight(), BufferedImage.TYPE_INT_RGB);
                Graphics2D g1 = bImg.createGraphics();
                g1.drawImage(cachedBackground, 0, 0, null);
                g1.drawImage(cachedImage, 0, 0, null);
                if (Lizzie.config.showWinrateGraph && cachedWinrateImage != null && !showControls)
                  g1.drawImage(cachedWinrateImage, grx, gry, null);
                g1.dispose();
                Rectangle rect = new Rectangle(x, y, width, height);
                BufferedImage areaImage = bImg.getSubimage(rect.x, rect.y, rect.width, rect.height);
                BufferedImage buffImg =
                    new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
                buffImg
                    .getGraphics()
                    .drawImage(
                        areaImage.getScaledInstance(width, height, java.awt.Image.SCALE_SMOOTH),
                        0,
                        0,
                        null);
                try {
                  boolean supported = ImageIO.write(buffImg, ext, file);
                  if (!supported) {
                    String displayedMessage =
                        String.format(
                            resourceBundle.getString("LizzieFrame.saveImageErrorHint1")
                                + " \"%s\"\n("
                                + resourceBundle.getString("LizzieFrame.saveImageErrorHint2")
                                + ")",
                            file.getName());
                    JOptionPane.showMessageDialog(
                        Lizzie.frame,
                        displayedMessage,
                        resourceBundle.getString("LizzieFrame.lizzieError"),
                        JOptionPane.ERROR_MESSAGE);
                  }
                } catch (IOException e) {
                }
              }
            } catch (Exception ex) {
            }
          }
        };
    Thread thread = new Thread(runnable);
    thread.start();
  }

  private Font makeFont(Font fontBase, int style) {
    Font font = fontBase.deriveFont(style, 100);
    Map<TextAttribute, Object> atts = new HashMap<>();
    atts.put(TextAttribute.KERNING, TextAttribute.KERNING_ON);
    return font.deriveFont(atts);
  }

  private void drawString(
      Graphics2D g,
      int x,
      int y,
      Font fontBase,
      int style,
      String string,
      float maximumFontHeight,
      double maximumFontWidth,
      int aboveOrBelow,
      boolean middle) {
    Font font = makeFont(fontBase, style);
    // set maximum size of font
    FontMetrics fm = g.getFontMetrics(font);
    font = font.deriveFont((float) (font.getSize2D() * maximumFontWidth / fm.stringWidth(string)));
    font = font.deriveFont(min(maximumFontHeight, font.getSize()));
    if (font.getSize() > Math.round(Lizzie.config.frameFontSize * Lizzie.javaScaleFactor) + 4) {
      font =
          new Font(
              font.getName(),
              font.getStyle(),
              Math.round(Lizzie.config.frameFontSize * Lizzie.javaScaleFactor) + 4);
    }
    g.setFont(font);
    int length = g.getFontMetrics().stringWidth(string);
    fm = g.getFontMetrics(font);
    int height = fm.getAscent() - fm.getDescent();
    int verticalOffset;
    if (aboveOrBelow == -1) {
      verticalOffset = height / 2;
    } else if (aboveOrBelow == 1) {
      verticalOffset = -height / 2;
    } else {
      verticalOffset = 0;
    }
    g.drawString(
        string,
        middle ? x + (int) (maximumFontWidth - length) / 2 : x,
        y + height / 2 + verticalOffset);
  }

  private void drawStringMid(
      Graphics2D g,
      int x,
      int y,
      Font fontBase,
      int style,
      String string,
      float maximumFontHeight,
      double maximumFontWidth,
      int aboveOrBelow) {

    Font font = makeFont(fontBase, style);

    // set maximum size of font
    FontMetrics fm = g.getFontMetrics(font);
    font = font.deriveFont((float) (font.getSize2D() * maximumFontWidth / fm.stringWidth(string)));
    font = font.deriveFont(min(maximumFontHeight, font.getSize()));
    if (font.getSize() > Math.round(Lizzie.config.frameFontSize * Lizzie.javaScaleFactor) + 6) {
      font =
          new Font(
              font.getName(),
              font.getStyle(),
              Math.round(Lizzie.config.frameFontSize * Lizzie.javaScaleFactor) + 6);
    }
    g.setFont(font);
    fm = g.getFontMetrics(font);
    int wid = fm.stringWidth(string);
    int height = fm.getAscent() - fm.getDescent();
    int verticalOffset;
    if (aboveOrBelow == -1) {
      verticalOffset = height / 2;
    } else if (aboveOrBelow == 1) {
      verticalOffset = -height / 2;
    } else {
      verticalOffset = 0;
    }
    g.drawString(string, x - wid / 2, y + height / 2 + verticalOffset);
  }

  public void saveImage(int x, int y, int width, int height, String path) {
    Runnable runnable =
        new Runnable() {
          public void run() {
            try {
              File file = new File(path);
              BufferedImage bImg =
                  new BufferedImage(
                      mainPanel.getWidth(), mainPanel.getHeight(), BufferedImage.TYPE_INT_ARGB);
              Graphics2D g1 = bImg.createGraphics();
              g1.drawImage(cachedBackground, 0, 0, null);
              g1.drawImage(cachedImage, 0, 0, null);
              if (Lizzie.config.showWinrateGraph && cachedWinrateImage != null && !showControls)
                g1.drawImage(cachedWinrateImage, grx, gry, null);
              g1.dispose();
              Rectangle rect = new Rectangle(x, y, width, height);
              BufferedImage areaImage = bImg.getSubimage(rect.x, rect.y, rect.width, rect.height);
              BufferedImage buffImg = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
              buffImg
                  .getGraphics()
                  .drawImage(
                      areaImage.getScaledInstance(width, height, java.awt.Image.SCALE_SMOOTH),
                      0,
                      0,
                      null);

              try {
                ImageIO.write(buffImg, "png", file);
              } catch (IOException e) {
                e.printStackTrace();
              }
            } catch (Exception ex) {
            }
          }
        };
    Thread thread = new Thread(runnable);
    thread.start();
  }

  public void saveMainBoardToClipboard() {
    if (extraMode != 8)
      savePicToClipboard(
          Lizzie.frame.boardX, Lizzie.frame.boardY, Lizzie.frame.maxSize, Lizzie.frame.maxSize);
    else {
      saveIndependMainBoardToClipboard();
    }
  }

  private void saveIndependMainBoardToClipboard() {
    if (Lizzie.config.isScaled) {
      int width = this.independentMainBoard.cachedImage.getWidth();
      int height = this.independentMainBoard.cachedImage.getHeight();
      Rectangle rect = new Rectangle(0, 0, width, height);
      BufferedImage areaImage =
          this.independentMainBoard.cachedImage.getSubimage(
              rect.x, rect.y, rect.width, rect.height);
      BufferedImage buffImg = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
      buffImg
          .getGraphics()
          .drawImage(
              areaImage.getScaledInstance(width, height, java.awt.Image.SCALE_SMOOTH), 0, 0, null);
      setClipboardImage(buffImg);
    } else {
      int width = this.independentMainBoard.getWidth();
      int height = this.independentMainBoard.getHeight();
      BufferedImage bImg = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
      Graphics2D cg = bImg.createGraphics();
      this.independentMainBoard.paintAll(cg);
      cg.dispose();
      Rectangle rect = new Rectangle(0, 0, width, height);
      BufferedImage areaImage = bImg.getSubimage(rect.x, rect.y, rect.width, rect.height);
      BufferedImage buffImg = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
      buffImg
          .getGraphics()
          .drawImage(
              areaImage.getScaledInstance(width, height, java.awt.Image.SCALE_SMOOTH), 0, 0, null);
      setClipboardImage(buffImg);
    }
  }

  private BufferedImage getIndependMainBoardToClipboard() {
    if (Lizzie.config.isScaled) {
      int width = this.independentMainBoard.cachedImage.getWidth();
      int height = this.independentMainBoard.cachedImage.getHeight();
      Rectangle rect = new Rectangle(0, 0, width, height);
      BufferedImage areaImage =
          this.independentMainBoard.cachedImage.getSubimage(
              rect.x, rect.y, rect.width, rect.height);
      BufferedImage buffImg = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
      buffImg
          .getGraphics()
          .drawImage(
              areaImage.getScaledInstance(width, height, java.awt.Image.SCALE_SMOOTH), 0, 0, null);
      return buffImg;
    } else {
      int width = this.independentMainBoard.getWidth();
      int height = this.independentMainBoard.getHeight();
      BufferedImage bImg = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
      Graphics2D cg = bImg.createGraphics();
      this.independentMainBoard.paintAll(cg);
      cg.dispose();
      Rectangle rect = new Rectangle(0, 0, width, height);
      BufferedImage areaImage = bImg.getSubimage(rect.x, rect.y, rect.width, rect.height);
      BufferedImage buffImg = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
      buffImg
          .getGraphics()
          .drawImage(
              areaImage.getScaledInstance(width, height, java.awt.Image.SCALE_SMOOTH), 0, 0, null);
      return buffImg;
    }
  }

  public void savePicToClipboard(int x, int y, int width, int height) {
    if (Lizzie.config.isScaled) {
      Rectangle rect = new Rectangle(x, y, width, height);
      BufferedImage areaImage = cachedImage.getSubimage(rect.x, rect.y, rect.width, rect.height);
      BufferedImage buffImg = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
      buffImg
          .getGraphics()
          .drawImage(
              areaImage.getScaledInstance(width, height, java.awt.Image.SCALE_SMOOTH), 0, 0, null);
      setClipboardImage(buffImg);
    } else {
      BufferedImage bImg =
          new BufferedImage(
              this.mainPanel.getWidth(), this.mainPanel.getHeight(), BufferedImage.TYPE_INT_ARGB);
      Graphics2D cg = bImg.createGraphics();

      this.mainPanel.paintAll(cg);
      cg.dispose();
      Rectangle rect = new Rectangle(x, y, width, height);
      BufferedImage areaImage = bImg.getSubimage(rect.x, rect.y, rect.width, rect.height);
      BufferedImage buffImg = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
      buffImg
          .getGraphics()
          .drawImage(
              areaImage.getScaledInstance(width, height, java.awt.Image.SCALE_SMOOTH), 0, 0, null);
      setClipboardImage(buffImg);
    }
  }

  protected static void setClipboardImage(final Image image) {
    Transferable trans =
        new Transferable() {
          public DataFlavor[] getTransferDataFlavors() {
            return new DataFlavor[] {DataFlavor.imageFlavor};
          }

          public boolean isDataFlavorSupported(DataFlavor flavor) {
            return DataFlavor.imageFlavor.equals(flavor);
          }

          public Object getTransferData(DataFlavor flavor)
              throws UnsupportedFlavorException, IOException {
            if (isDataFlavorSupported(flavor)) return image;
            throw new UnsupportedFlavorException(flavor);
          }
        };
    Toolkit.getDefaultToolkit().getSystemClipboard().setContents(trans, null);
  }

  private void syncOnline(String url) {
    if (onlineDialog == null) onlineDialog = new OnlineDialog();
    else {
      try {
        onlineDialog.stopSync();
      } catch (Exception ex) {
      }
    }

    onlineDialog.applyChangeWeb(url);
    syncLiveBoardStat();
    //    if (onlineDialog != null) {
    //      onlineDialog.dispose();
    //    }
    //    if (isSyncing && System.currentTimeMillis() - startSyncTime < 2000) {
    //      if (firstIsSyncing) firstIsSyncing = false;
    //      else return;
    //      Timer timer = new Timer();
    //      timer.schedule(
    //          new TimerTask() {
    //            public void run() {
    //              //    onlineDialog = new OnlineDialog();
    //              onlineDialog.applyChangeWeb(url);
    //              startSyncTime = System.currentTimeMillis();
    //              isSyncing = false;
    //              firstIsSyncing = true;
    //              this.cancel();
    //            }
    //          },
    //          2000);
    //      return;
    //    }
    //
    //    if (System.currentTimeMillis() - startSyncTime < 1000) {
    //      isSyncing = true;
    //      // onlineDialog = new OnlineDialog();
    //      onlineDialog.applyChangeWeb(url);
    //      startSyncTime = System.currentTimeMillis();
    //      return;
    //    }
    //    isSyncing = false;
    //    //  onlineDialog = new OnlineDialog();
    //    onlineDialog.applyChangeWeb(url);
    //       startSyncTime = System.currentTimeMillis();
  }

  public void openHelp() {
    File file = new File("");
    String courseFile = "";
    try {
      courseFile = file.getCanonicalPath();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    String url = courseFile + Utils.pwd + "readme.pdf";
    bowser(url, resourceBundle.getString("LizzieFrame.introduction"), false);
  }

  public void bowser(String url, String title, boolean toolbar) {
    JTextField thisUrl = new JTextField();
    JToolBar toolBar = new JToolBar(resourceBundle.getString("LizzieFrame.url")); // ("地址栏");
    urlList = new ArrayList<String>();
    urlList.add(url);
    urlIndex = 0;
    if (browser != null && !browser.isDisposed()) {
      browser.loadURL(url);
      frame.setTitle(title);
      frame.setVisible(true);
      return;
    } else {
      browser = new Browser();
      browser.loadURL(url);
    }
    browser.setPopupHandler(
        new PopupHandler() {
          @Override
          public PopupContainer handlePopup(PopupParams popupParams) {
            // browser.loadURL(popupParams.getURL());
            //  thisUrl.setText(popupParams.getURL());
            Runnable runnable =
                new Runnable() {
                  public void run() {
                    if (Lizzie.config.openHtmlOnLive) {
                      browser.loadURL(popupParams.getURL());
                      thisUrl.setText(popupParams.getURL());
                      urlList.add(popupParams.getURL());
                      urlIndex = urlList.size() - 1;
                    }
                    syncOnline(popupParams.getURL());
                  }
                };
            Thread thread = new Thread(runnable);
            thread.start();

            return null;
          }
        });
    BrowserView view = new BrowserView(browser);
    JPanel viewPanel = new JPanel();
    viewPanel.setLayout(null);
    viewPanel.add(view);
    frame = new JFrame();

    frame.setSize(bowserWidth, bowserHeight);
    frame.setTitle(title);
    frame.add(viewPanel, BorderLayout.CENTER);
    frame.setLocation(bowserX, bowserY);
    frame.setVisible(true);

    frame.addWindowListener(
        new WindowAdapter() {
          public void windowClosing(WindowEvent e) {
            if (toolbar) {
              bowserX = frame.getX();
              bowserY = frame.getY();
              bowserWidth = frame.getWidth();
              bowserHeight = frame.getHeight();
            }
            frame.setVisible(false);
            frame.dispose();
            browser.dispose();
          }
        });

    viewPanel.addComponentListener(
        new ComponentAdapter() {
          @Override
          public void componentResized(ComponentEvent e) {
            viewPanel.revalidate();
            view.setBounds(
                0,
                toolbar ? (int) (toolBar.getHeight() * (Lizzie.sysScaleFactor - 1)) : 0,
                (int) Math.ceil((viewPanel.getWidth() * Lizzie.sysScaleFactor)),
                (int) Math.ceil((viewPanel.getHeight() * Lizzie.sysScaleFactor)));
            viewPanel.revalidate();
          }
        });
    if (toolbar) {
      frame.addComponentListener(
          new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
              bowserWidth = frame.getWidth();
              bowserHeight = frame.getHeight();
            }

            public void componentMoved(ComponentEvent e) {
              bowserX = frame.getX();
              bowserY = frame.getY();
            }
          });
    }
    thisUrl.setText(url);
    try {
      frame.setIconImage(ImageIO.read(MoveListFrame.class.getResourceAsStream("/assets/logo.png")));
    } catch (IOException e1) {
      // TODO Auto-generated catch block
      e1.printStackTrace();
    }

    ImageIcon iconLeft = new ImageIcon();
    try {
      iconLeft.setImage(ImageIO.read(AnalysisFrame.class.getResourceAsStream("/assets/left.png")));
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    ImageIcon iconRight = new ImageIcon();
    try {
      iconRight.setImage(
          ImageIO.read(AnalysisFrame.class.getResourceAsStream("/assets/right.png")));
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

    thisUrl.addKeyListener(
        new KeyAdapter() {
          public void keyPressed(KeyEvent e) {
            if (e.getKeyChar() == KeyEvent.VK_ENTER) // 按回车键执行相应操作;
            {
              browser.loadURL(thisUrl.getText());
              if (!thisUrl.getText().equals(url)) {
                syncOnline(thisUrl.getText());
              }
            }
          }
        });
    JButton backward = new JButton(iconLeft);
    backward.setFocusable(false);
    backward.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            if (urlIndex > 0) {
              urlIndex = urlIndex - 1;
              browser.loadURL(urlList.get(urlIndex));
              thisUrl.setText(urlList.get(urlIndex));
              if (urlList.get(urlIndex) != url) {
                syncOnline(urlList.get(urlIndex));
              }
            }
          }
        });

    JButton forward = new JButton(iconRight);
    forward.setFocusable(false);
    forward.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            // TBD未完成
            if (urlIndex < urlList.size() - 1) {
              urlIndex = urlIndex + 1;
              browser.loadURL(urlList.get(urlIndex));
              thisUrl.setText(urlList.get(urlIndex));
              if (urlList.get(urlIndex) != url) {
                syncOnline(urlList.get(urlIndex));
              }
            }
          }
        });
    JButton refresh = new JButton(resourceBundle.getString("LizzieFrame.refresh")); // "刷新");
    refresh.setFocusable(false);
    refresh.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            // TBD未完成
            browser.loadURL(browser.getURL());
            thisUrl.setText(browser.getURL());
            if (!browser.getURL().equals(url)) {
              syncOnline(browser.getURL());
            }
          }
        });
    JButton load = new JButton(resourceBundle.getString("LizzieFrame.onLoad")); // ("加载");
    load.setFocusable(false);
    load.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            // TBD未完成
            browser.loadURL(thisUrl.getText());
            if (!thisUrl.getText().equals(url)) {
              syncOnline(thisUrl.getText());
            }
          }
        });

    JButton back = new JButton(resourceBundle.getString("LizzieFrame.backToHall")); // ("返回大厅");
    back.setFocusable(false);
    back.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            // TBD未完成
            browser.loadURL(url);
            thisUrl.setText(url);
          }
        });
    JButton stop = new JButton(resourceBundle.getString("LizzieFrame.stopSync")); // ("停止同步");
    stop.setFocusable(false);
    stop.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            // TBD
            if (onlineDialog != null) {
              onlineDialog.stopSync();
            }
          }
        });
    toolBar.setBorderPainted(false);
    toolBar.add(backward);
    toolBar.add(forward);
    toolBar.add(back);

    toolBar.add(thisUrl);
    toolBar.add(load);
    toolBar.addSeparator();
    toolBar.add(refresh);
    toolBar.addSeparator();
    toolBar.add(stop);

    view.requestFocus();
    if (toolbar) frame.add(toolBar, BorderLayout.PAGE_START);
    else frame.setExtendedState(Frame.MAXIMIZED_BOTH);
    view.setBounds(
        0,
        0,
        (int) (viewPanel.getWidth() * Lizzie.sysScaleFactor),
        (int) (viewPanel.getHeight() * Lizzie.sysScaleFactor));
    toolBar.setVisible(false);
    toolBar.setVisible(true);
    //  frame.add(back);
  }

  public void syncLiveBoardStat() {
    maxMvNum = 0;
    firstSync = true;
    if (timer != null) {
      timer.stop();
      timer = null;
    }
    timer =
        new javax.swing.Timer(
            500,
            new ActionListener() {
              public void actionPerformed(ActionEvent evt) {
                int moveNumber = Lizzie.board.getHistory().getMainEnd().getData().moveNumber;
                if (moveNumber > maxMvNum || (firstSync && moveNumber > 0)) {

                  if (((Lizzie.board.getHistory().getCurrentHistoryNode().isMainTrunk()
                              && Lizzie.board
                                      .getHistory()
                                      .getCurrentHistoryNode()
                                      .getData()
                                      .moveNumber
                                  == maxMvNum)
                          || firstSync)
                      || Lizzie.config.alwaysGotoLastOnLive) {
                    firstSync = false;
                    moveToMainTrunk();
                    Lizzie.board.goToMoveNumberBeyondBranch(moveNumber);
                  }
                  maxMvNum = moveNumber;
                }
                //                BoardHistoryNode node =
                // Lizzie.board.getHistory().getCurrentHistoryNode();
                //                if (node.getData().comment.equals("") && node.variations.size() >
                // 0)
                //                  //
                //	if(!node.variations.get(0).getData().comment.equals(""))
                //                  //
                //                  //
                //	node.getData().comment=node.variations.get(0).getData().comment;
                //                  //	else
                //                  if (node.variations.size() > 1
                //                      && !node.variations.get(1).getData().comment.equals(""))
                //                    node.getData().comment =
                // node.variations.get(1).getData().comment;
                Lizzie.frame.refresh();
                if (!urlSgf) {
                  timer.stop();
                  timer = null;
                }
              }
            });
    timer.start();
  }

  public void openPublicKifuSearch() {
    search = new PublicKifuSearch();
    search.setVisible(true);
  }

  public void shareSGF() {
    //    if (shareTime > 0) {
    //      if ((System.currentTimeMillis() - shareTime) < 10000) {
    //        Message msg = new Message();
    //        msg.setMessage("请勿频繁分享(10秒内)");
    //        msg.setVisible(true);
    //        return;
    //      }
    //    } else
    //    	shareTime = System.currentTimeMillis();
    shareFrame = new ShareFrame();
    shareFrame.setVisible(true);
  }

  public void batchShareSGF() {
    batchShareFrame = new BatchShareFrame();
    batchShareFrame.setVisible(true);
  }

  public void setLzSaiEngine() {
    if (Lizzie.engineManager.isEmpty || !Lizzie.leelaz.isLoaded()) {
      Utils.showMsg(resourceBundle.getString("LizzieFrame.setParamNoEngineHint"));
      return;
    }
    if (Lizzie.leelaz.isKatago) {
      // Utils.showMsg(resourceBundle.getString("LizzieFrame.setParamsWarning"));
      //      Message msg = new Message();
      //      msg.setMessage("当前引擎不是Leela或者Sai,设置参数可能无效");
      //      msg.setVisible(true);
      SetKataEngines setKataEngines = new SetKataEngines();
      setKataEngines.setVisible(true);
    } else {
      SetLeelaEngines setLeelaEngines = new SetLeelaEngines();
      setLeelaEngines.setVisible(true);
    }
  }

  public void setRules() {
    setkatarules = new SetKataRules();
    setkatarules.setVisible(true);
    Runnable runnable =
        new Runnable() {
          public void run() {
            boolean success = false;
            for (int i = 0; i < 10; i++) {
              try {
                Thread.sleep(200);
                if (setkatarules.getRules()) {
                  success = true;
                  break;
                }

              } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
              }
            }
            Lizzie.leelaz.getRcentLine = false;
            if (!success) {
              //              Message msg = new Message();
              //              msg.setMessage("无法获取当前引擎规则");
              //              msg.setVisible(true);"获取当前引擎规则失败"
              if (setkatarules.isVisible())
                JOptionPane.showMessageDialog(
                    setkatarules, resourceBundle.getString("LizzieFrame.ruleWarning"));
            }
          }
        };
    Thread thread = new Thread(runnable);
    thread.start();
  }

  public void startEngineGameDialog() {
    if (Lizzie.engineManager.isEngineGame) {
      Utils.showMsg(
          resourceBundle.getString(
              "LizzieFrame.engineGameStopFirstHint")); // "请等待当前引擎对战结束,或手动终止对局");
      return;
    }
    if (Lizzie.frame.isPlayingAgainstLeelaz || Lizzie.frame.isAnaPlayingAgainstLeelaz) {
      Lizzie.frame.togglePonderMannul();
    }
    Lizzie.frame.toolbar.enginePkBlack.setEnabled(true);
    Lizzie.frame.toolbar.enginePkWhite.setEnabled(true);
    NewEngineGameDialog engineGame = new NewEngineGameDialog();
    GameInfo gameInfo = Lizzie.board.getHistory().getGameInfo();
    engineGame.setGameInfo(gameInfo);
    engineGame.setVisible(true);
    Lizzie.frame.toolbar.resetEnginePk();
    if (engineGame.isCancelled()) {
      // Lizzie.frame.addInput();
      Lizzie.frame.toolbar.chkenginePk.setSelected(false);
      Lizzie.frame.toolbar.enginePkBlack.setEnabled(false);
      Lizzie.frame.toolbar.enginePkWhite.setEnabled(false);
      return;
    }
  }

  public void startAnalyzeGameDialog() {
    boolean isPondering = false;
    if (Lizzie.leelaz.isPondering()) {
      Lizzie.leelaz.togglePonder();
      isPondering = true;
    }
    if (Lizzie.leelaz.noAnalyze) {
      startNewGame();
    } else {
      Lizzie.frame.stopAiPlayingAndPolicy();
      // Lizzie.frame.isPlayingAgainstLeelaz = false;
      // GameInfo gameInfo = Lizzie.board.getHistory().getGameInfo();
      NewAnaGameDialog newgame = new NewAnaGameDialog();
      // newgame.setGameInfo(gameInfo);
      newgame.setVisible(true);
      newgame.dispose();
      if (newgame.isCancelled()) {
        if (isPondering) Lizzie.leelaz.togglePonder();
        return;
      }
      Lizzie.frame.isAnaPlayingAgainstLeelaz = true;
      // Lizzie.frame.komi = gameInfo.getKomi() + "";
      Lizzie.frame.toolbar.isAutoPlay = true;
      Lizzie.leelaz.isGamePaused = false;
    }
  }

  public void continueAiPlaying(
      boolean isGenmove, boolean continueNow, boolean playerIsB, boolean fromShortCut) {
    if (Lizzie.engineManager.isEmpty) return;
    if (isPlayingAgainstLeelaz || isAnaPlayingAgainstLeelaz) {
      stopAiPlayingAndPolicy();
    }
    if (isGenmove) {
      if (!Lizzie.leelaz.isThinking) {
        if (!Lizzie.config.genmoveGameNoTime) sendAiTime();
        isPlayingAgainstLeelaz = true;
        if (continueNow) {
          Lizzie.frame.playerIsBlack = !Lizzie.board.getData().blackToPlay;
          Lizzie.leelaz.genmove((Lizzie.board.getData().blackToPlay ? "B" : "W"));
        } else {
          playerIsBlack = playerIsB;
          if (playerIsB) {
            if (Lizzie.board.getData().blackToPlay != playerIsBlack) {
              Lizzie.leelaz.genmove("W");
            }
          } else {
            if (Lizzie.board.getData().blackToPlay != playerIsBlack) {
              Lizzie.leelaz.genmove("B");
            }
          }
        }
      }
      if (!Lizzie.frame.bothSync) {
        toolbar.chkShowBlack.setSelected(false);
        toolbar.chkShowWhite.setSelected(false);
        if (Lizzie.config.showDoubleMenu) {
          Lizzie.frame.menu.chkShowBlack.setSelected(false);
          Lizzie.frame.menu.chkShowWhite.setSelected(false);
        }
      } else {
        toolbar.chkShowBlack.setSelected(true);
        toolbar.chkShowWhite.setSelected(true);
        if (Lizzie.config.showDoubleMenu) {
          Lizzie.frame.menu.chkShowBlack.setSelected(true);
          Lizzie.frame.menu.chkShowWhite.setSelected(true);
        }
      }
      Lizzie.frame.updateTitle();
      Lizzie.frame.refresh();
    } else {
      if (!toolbar.chkAutoPlayTime.isSelected()
          && !toolbar.chkAutoPlayFirstPlayouts.isSelected()
          && !toolbar.chkAutoPlayPlayouts.isSelected()) {
        toolbar.txtAutoPlayTime.setText(Math.max(1, Lizzie.config.maxGameThinkingTimeSeconds) + "");
        toolbar.chkAutoPlayTime.setSelected(true);
      }
      if (continueNow) {
        if (Lizzie.board.getHistory().isBlacksTurn()) {
          playerIsBlack = false;
          toolbar.chkAutoPlayBlack.setSelected(true);
          toolbar.chkAutoPlayWhite.setSelected(false);
        } else {
          playerIsBlack = true;
          toolbar.chkAutoPlayBlack.setSelected(false);
          toolbar.chkAutoPlayWhite.setSelected(true);
        }
      } else {
        playerIsBlack = playerIsB;
        if (playerIsB) {
          toolbar.chkAutoPlayBlack.setSelected(false);
          toolbar.chkAutoPlayWhite.setSelected(true);
        } else {
          toolbar.chkAutoPlayBlack.setSelected(true);
          toolbar.chkAutoPlayWhite.setSelected(false);
        }
      }
      if (!Lizzie.frame.bothSync) {
        toolbar.chkShowBlack.setSelected(false);
        toolbar.chkShowWhite.setSelected(false);
        if (Lizzie.config.showDoubleMenu) {
          Lizzie.frame.menu.chkShowBlack.setSelected(false);
          Lizzie.frame.menu.chkShowWhite.setSelected(false);
        }
      } else {
        toolbar.chkShowBlack.setSelected(true);
        toolbar.chkShowWhite.setSelected(true);
        if (Lizzie.config.showDoubleMenu) {
          Lizzie.frame.menu.chkShowBlack.setSelected(true);
          Lizzie.frame.menu.chkShowWhite.setSelected(true);
        }
      }
      toolbar.chkAutoPlay.setSelected(true);
      isAnaPlayingAgainstLeelaz = true;
      toolbar.isAutoPlay = true;
      Lizzie.leelaz.anaGameResignCount = 0;
      if (Lizzie.config.UsePureNetInGame && !Lizzie.leelaz.isheatmap)
        Lizzie.leelaz.toggleHeatmap(false);
      Lizzie.leelaz.ponder();
    }
    Lizzie.frame.menu.toggleDoubleMenuGameStatus();
    Lizzie.leelaz.isGamePaused = false;
    if (fromShortCut) Utils.showMsg(resourceBundle.getString("LizzieFrame.startContinueGame"));
  }

  //  private void createVarTreeImage(int vx, int vy, int vw, int vh) {
  //    Runnable runnable =
  //        new Runnable() {
  //          public void run() {
  //            try {
  //              createVarTreeImageTh(vx, vy, vw, vh);
  //            } catch (Exception e) {
  //              // TODO Auto-generated catch block
  //              // e.printStackTrace();
  //            }
  //          }
  //        };
  //    Thread thread = new Thread(runnable);
  //    thread.start();
  //  }
  private void setListScrollpane(int vx, int vy, int vw, int vh) {
    if (vw < 10 || vh < 5) {
      listScrollpane.setVisible(false);
      return;
    } else if (!listScrollpane.isVisible()) {
      listScrollpane.setVisible(true);
    }
    if (listScrollpane.getX() != vx
        || listScrollpane.getY() != vy + (Lizzie.config.showDoubleMenu ? topPanelHeight : 0)
        || listScrollpane.getWidth() != vw
        || listScrollpane.getHeight() != vh)
      listScrollpane.setBounds(
          Utils.zoomIn(vx),
          Utils.zoomIn(vy) + (Lizzie.config.showDoubleMenu ? topPanelHeight : 0),
          Utils.zoomIn(vw),
          Utils.zoomIn(vh));
  }

  public void setHideListScrollpane(boolean visible) {
    listScrollpane.setVisible(visible);
    if (visible) clickOrder = -1;
  }

  private boolean shouldShowSimpleVariation() {
    return (!Lizzie.config.ignoreOutOfWidth && Lizzie.board.hasBigBranch())
        || !Lizzie.config.showScrollVariation;
  }

  private void createVarTreeImage(int vx, int vy, int vw, int vh, Graphics2D g) {
    if (!Lizzie.config.showVariationGraph) return;
    if (shouldShowSimpleVariation()) {
      variationTreeBig.draw(g, vx, vy, vw, vh);
      if (varTreeScrollPane.isVisible()) {
        varTreeScrollPane.setVisible(false);
        if (Lizzie.config.isScaled) repaint();
      }
      return;
    } else if (vw < 10 || vh < 10) {
      varTreeScrollPane.setVisible(false);
      return;
    } else if (!varTreeScrollPane.isVisible()) {
      varTreeScrollPane.setVisible(true);
      if (Lizzie.config.isScaled) repaint();
    }
    if (!completeDrawTree) {
      return;
    }

    //    if (mouseOnVarTree)
    //    	{
    //    	 if(canDrawCurColor)
    //         {
    //    		 renderVarTreeCur();
    //         }
    //    	return;}
    if (!forceRecreate && varTreeX == vx && varTreeY == vy && varTreeW == vw && varTreeH == vh) {
      if (redrawTree || treeNode != Lizzie.board.getHistory().getCurrentHistoryNode()) {
        treeNode = Lizzie.board.getHistory().getCurrentHistoryNode();
        renderVarTree(vw, vh, true, false);
        if (redrawTreeLater) {
          redrawTreeLater = false;
          Runnable runnable =
              new Runnable() {
                public void run() {
                  try {
                    Thread.sleep(150);
                    renderVarTree(vw, vh, true, false);
                    Thread.sleep(150);
                    renderVarTree(vw, vh, true, false);
                  } catch (Exception e) {
                    // TODO Auto-generated catch block
                    // e.printStackTrace();
                  }
                }
              };
          Thread thread = new Thread(runnable);
          thread.start();
        }
      }
      if (canDrawCurColor) {
        if (tree_curposx >= 0) renderVarTreeCur();
      }
      return;
    }
    if (forceRecreate) {
      tree_curposx = -1;
      forceRecreate = false;
    }
    redrawTree = true;
    // startTreeRenderTime=System.currentTimeMillis();
    varTreeX = vx;
    varTreeY = vy;
    varTreeW = vw;
    varTreeH = vh;
    varTreeMaxX = 1;
    varTreeMaxY = 1;
    if (varTreeMaxX < vw) varTreeMaxX = vw;
    if (varTreeMaxY < vh) varTreeMaxY = vh;
    completeDrawTree = false;
    setTreeMaxLimit();
    cachedVarImage = new BufferedImage(varTreeMaxX, varTreeMaxY, TYPE_INT_ARGB);
    Graphics2D g0 = (Graphics2D) cachedVarImage.getGraphics();
    g0.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    // treeNode = Lizzie.board.getHistory().getCurrentHistoryNode();
    canDrawCurColor = false;
    Runnable runnable =
        new Runnable() {
          public void run() {
            try {
              try {
                drawTree(g0);
              } catch (Exception ee) {
                //  drawWrong = true;
                // varTreePane.updateUI();
                completeDrawTree = true;
                return;
              }
              cachedVarImage2 = cachedVarImage;
              varTreePane.setPreferredSize(
                  new Dimension(cachedVarImage2.getWidth(), cachedVarImage2.getHeight()));
              varTreePane.updateUI();
              varTreeScrollPane.setBounds(
                  Utils.zoomIn(vx),
                  Utils.zoomIn(vy) + (Lizzie.config.showDoubleMenu ? topPanelHeight : 0),
                  Utils.zoomIn(vw),
                  Utils.zoomIn(vh));

              canDrawCurColor = true;
              completeDrawTree = true;
            } catch (Exception e) {
              completeDrawTree = true;
              // TODO Auto-generated catch block
              // e.printStackTrace();
            }
          }
        };
    Thread thread = new Thread(runnable);
    thread.start();
    if (vh < 100 || varTreeMaxX == vw)
      varTreeScrollPane.setHorizontalScrollBarPolicy(
          ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
    else varTreeScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
  }

  private void renderVarTreeCur() {
    if (shouldShowSimpleVariation()) return;
    BoardHistoryNode cur = Lizzie.board.getHistory().getCurrentHistoryNode();
    if (cur == Lizzie.board.getHistory().getStart()) return;
    Graphics2D g = (Graphics2D) cachedVarImage2.getGraphics();
    // Color curcolor = g.getColor();
    g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

    //    g.fillOval(
    //        tree_curposx - 1 + (tree_DOT_DIAM + tree_diff - tree_RING_DIAM) / 2,
    //        tree_posy - 1 + (tree_DOT_DIAM + tree_diff - tree_RING_DIAM) / 2,
    //        tree_RING_DIAM + 2,
    //        tree_RING_DIAM + 2);
    if (Lizzie.config.showCommentNodeColor && !cur.getData().comment.isEmpty()) {
      // g.setColor(Lizzie.config.varPanelColor);
      if (Lizzie.config.usePureBackground) g.setColor(Lizzie.config.pureBackgroundColor);
      else g.setPaint(Lizzie.frame.backgroundPaint);
      g.fillOval(
          tree_curposx + (tree_DOT_DIAM + tree_diff - tree_RING_DIAM) / 2,
          tree_posy + (tree_DOT_DIAM + tree_diff - tree_RING_DIAM) / 2,
          tree_RING_DIAM,
          tree_RING_DIAM);
      g.setColor(new Color(0, 0, 0, 130));
      g.fillOval(
          tree_curposx + (tree_DOT_DIAM + tree_diff - tree_RING_DIAM) / 2,
          tree_posy + (tree_DOT_DIAM + tree_diff - tree_RING_DIAM) / 2,
          tree_RING_DIAM,
          tree_RING_DIAM);
      g.setColor(Lizzie.config.commentNodeColor);
      g.fillOval(
          tree_curposx + (tree_DOT_DIAM + tree_diff - tree_RING_DIAM) / 2,
          tree_posy + (tree_DOT_DIAM + tree_diff - tree_RING_DIAM) / 2,
          tree_RING_DIAM,
          tree_RING_DIAM);
    } else {
      // g.setColor(Lizzie.config.varPanelColor);
      if (Lizzie.config.usePureBackground) g.setColor(Lizzie.config.pureBackgroundColor);
      else g.setPaint(Lizzie.frame.backgroundPaint);
      g.fillOval(
          tree_curposx + tree_diff - 1, tree_posy + tree_diff - 1, tree_diam + 2, tree_diam + 2);
      g.setColor(new Color(0, 0, 0, 130));
      g.fillOval(
          tree_curposx + tree_diff - 1, tree_posy + tree_diff - 1, tree_diam + 2, tree_diam + 2);
    }

    Color blunderColor = getBlunderNodeColor(cur);
    g.setColor(blunderColor);
    g.fillOval(tree_curposx + tree_diff, tree_posy + tree_diff, tree_diam, tree_diam);

    if (blunderColor != Color.WHITE) g.setColor(reverseColor(blunderColor));
    else g.setColor(Color.RED);
    g.fillOval(
        tree_curposx + (tree_DOT_DIAM + tree_diff - tree_CENTER_DIAM) / 2 - 1,
        tree_posy + (tree_DOT_DIAM + tree_diff - tree_CENTER_DIAM) / 2 - 1,
        tree_CENTER_DIAM + 2,
        tree_CENTER_DIAM + 2);
    g.dispose();
  }

  private Color reverseColor(Color color) {
    // System.out.println("color=="+color);
    int r = color.getRed();
    int g = color.getGreen();
    int b = color.getBlue();
    int r_ = 255 - r;
    int g_ = 255 - g;
    int b_ = 255 - b;
    Color newColor = new Color(r_, g_, b_);
    return newColor;
  }

  private void setTreeMaxLimit() {
    if (varTreeMaxX >= Lizzie.config.maxTreeWidth) {
      varTreeMaxX = Lizzie.config.maxTreeWidth;
      Lizzie.board.setBigBranch();
    }
  }

  private void drawTree(Graphics2D g0) {
    variationTree.draw(g0, 0, 0, varTreeMaxX, varTreeMaxY);
    if (varTreeMaxX >= Lizzie.config.maxTreeWidth) {
      varTreeMaxX = Lizzie.config.maxTreeWidth;
      Lizzie.board.setBigBranch();
    }
    g0.dispose();
  }

  public void renderVarTree(int vw, int vh, boolean changeSize, boolean needGetEnd) {
    if (shouldShowSimpleVariation()) return;
    if (!completeDrawTree) {
      return;
    }
    redrawTree = false;
    completeDrawTree = false;
    setTreeMaxLimit();
    cachedVarImage = new BufferedImage(varTreeMaxX, varTreeMaxY, TYPE_INT_ARGB);
    Graphics2D g0 = (Graphics2D) cachedVarImage.getGraphics();
    g0.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    canDrawCurColor = false;
    Runnable runnable =
        new Runnable() {
          public void run() {
            try {
              drawTree(g0);
            } catch (Exception ee) {
              // drawWrong = true;
              // varTreePane.updateUI();
              completeDrawTree = true;
              return;
            }

            cachedVarImage2 = cachedVarImage;

            varTreePane.setPreferredSize(
                new Dimension(cachedVarImage2.getWidth(), cachedVarImage2.getHeight()));
            varTreePane.updateUI();

            canDrawCurColor = true;
            // varTreeCurY
            JScrollBar jScrollBarW = varTreeScrollPane.getHorizontalScrollBar();
            varTreeCurX = Utils.zoomOut(varTreeCurX);
            varTreeCurY = Utils.zoomOut(varTreeCurY);
            if (varTreeCurX <= varTreeW / 2
                || Lizzie.board.getHistory().getCurrentHistoryNode()
                    == Lizzie.board.getHistory().getStart()) jScrollBarW.setValue(0);
            else {
              jScrollBarW.setValue(
                  (int)
                      ((float) (varTreeCurX - (varTreeW / 2 > Utils.zoomOut(60) ? varTreeW / 2 : 0))
                          / Utils.zoomOut(varTreeMaxX)
                          * jScrollBarW.getMaximum())); // 设置水平滚动条位置
            }

            JScrollBar jScrollBarH = varTreeScrollPane.getVerticalScrollBar();
            if (needGetEnd) {
              new Thread() {
                public void run() {
                  try {
                    Thread.sleep(1000);
                  } catch (InterruptedException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                  }
                  jScrollBarH.setValue(9999);
                }
              }.start();

            } else {
              if (varTreeCurY <= varTreeH / 2
                  || Lizzie.board.getHistory().getCurrentHistoryNode()
                      == Lizzie.board.getHistory().getStart()) // 129,155
              jScrollBarH.setValue(0);
              else
                jScrollBarH.setValue(
                    (int)
                        ((float)
                                (Math.min(varTreeCurY, Utils.zoomOut(varTreePane.getHeight()))
                                    - varTreeH / 2)
                            / Math.min(
                                Utils.zoomOut(varTreeMaxY), Utils.zoomOut(varTreePane.getHeight()))
                            * jScrollBarH.getMaximum())); // 设置垂直滚动条位置
            }
            if (changeSize) {
              if (vh < 100 || varTreeMaxX == vw)
                varTreeScrollPane.setHorizontalScrollBarPolicy(
                    ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
              else
                varTreeScrollPane.setHorizontalScrollBarPolicy(
                    JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
            }

            if (varTreeMaxY == vh)
              varTreeScrollPane.setVerticalScrollBarPolicy(
                  ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
            else
              varTreeScrollPane.setVerticalScrollBarPolicy(
                  ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
            completeDrawTree = true;
          }
        };
    Thread thread = new Thread(runnable);
    thread.start();
  }

  public void handleAfterDrawGobanBottom() {
    if (Lizzie.board.getHistory().getGameInfo().getPlayerWhite().equals("")
        && Lizzie.board.getHistory().getGameInfo().getPlayerBlack().equals("")) {
      boardRenderer.changedName = true;
      boardRenderer.emptyName = true;
    }
    refresh();
  }

  public void handleAfterDrawGobanBottomSub() {
    if (Lizzie.board.getHistory().getGameInfo().getPlayerWhite().equals("")
        && Lizzie.board.getHistory().getGameInfo().getPlayerBlack().equals("")) {
      boardRenderer2.changedName = true;
      boardRenderer2.emptyName = true;
    }
    //   refresh();
  }

  public void clearEstimate() {
    boardRenderer.removeEstimateImage();
    if (floatBoard != null) floatBoard.boardRenderer.removeEstimateImage();
  }

  public void clearKataEstimate() {
    boardRenderer.removecountblock();
    if (Lizzie.config.showSubBoard) subBoardRenderer.removecountblock();
    if (this.extraMode == 2) boardRenderer2.removecountblock();
    if (floatBoard != null) floatBoard.boardRenderer.removecountblock();
    if (Lizzie.estimateResults != null && Lizzie.estimateResults.isVisible())
      Lizzie.estimateResults.repaint();
  }

  public void toggleShowKataEstimate() {
    if (Lizzie.leelaz.isKatago) {
      if (!Lizzie.config.isHiddenKataEstimate) {
        Lizzie.config.showKataGoEstimate = !Lizzie.config.showKataGoEstimate;
        if (!Lizzie.config.showKataGoEstimateOnMainbord
            && !Lizzie.config.showKataGoEstimateOnSubbord)
          Lizzie.config.showKataGoEstimateOnSubbord = true;
        Lizzie.config.showKataGoEstimateOnMainbord = true;
      } else {
        Lizzie.config.showKataGoEstimateOnMainbord = !Lizzie.config.showKataGoEstimateOnMainbord;
        Lizzie.config.showKataGoEstimateOnSubbord = !Lizzie.config.showKataGoEstimateOnSubbord;
        Lizzie.frame.clearKataEstimate();
        return;
      }
      if (!Lizzie.config.showKataGoEstimate) {
        clearKataEstimate();
      }
      Lizzie.leelaz.ponder();
    } else {
      if (Lizzie.frame.isCounting) {
        clearKataEstimate();
        Lizzie.frame.refresh();
        Lizzie.frame.isCounting = false;
        Lizzie.estimateResults.setVisible(false);
      } else {
        Lizzie.frame.countstones(true);
      }
    }
  }

  public void togglePonderMannul() {
    if (!stopAiPlayingAndPolicy()) Lizzie.leelaz.togglePonder();
  }

  public void drawKataEstimate(Leelaz engine, ArrayList<Double> tempcount) {
    if (isInScoreMode) return;
    if ((!Lizzie.leelaz.iskataHeatmapShowOwner && Lizzie.config.showKataGoEstimateBySize)
        || (Lizzie.leelaz.iskataHeatmapShowOwner && Lizzie.config.showPureEstimateBySize)) {
      if (Lizzie.config.showKataGoEstimateOnMainbord || isShowingHeatmap) {
        if (extraMode == 2) {
          if (engine == Lizzie.leelaz) Lizzie.frame.boardRenderer.drawKataEstimateBySize(tempcount);
          if (Lizzie.leelaz2 != null && engine == Lizzie.leelaz2)
            Lizzie.frame.boardRenderer2.drawKataEstimateBySize(tempcount);
        } else {
          Lizzie.frame.boardRenderer.drawKataEstimateBySize(tempcount);
          if (floatBoard != null && floatBoard.isVisible())
            floatBoard.boardRenderer.drawKataEstimateBySize(tempcount);
        }
      }
      if ((Lizzie.config.showKataGoEstimateOnSubbord || isShowingHeatmap) && extraMode != 2) {
        if (Lizzie.config.showSubBoard)
          Lizzie.frame.subBoardRenderer.drawKataEstimateBySize(tempcount);
        if (independentSubBoard != null && independentSubBoard.isVisible())
          independentSubBoard.subBoardRenderer.drawKataEstimateBySize(tempcount);
      }
    } else {
      if (Lizzie.config.showKataGoEstimateOnMainbord || isShowingHeatmap) {
        if (extraMode == 2) {
          if (engine == Lizzie.leelaz)
            Lizzie.frame.boardRenderer.drawKataEstimateByTransparent(tempcount);
          if (Lizzie.leelaz2 != null && engine == Lizzie.leelaz2)
            Lizzie.frame.boardRenderer2.drawKataEstimateByTransparent(tempcount);
        } else {
          Lizzie.frame.boardRenderer.drawKataEstimateByTransparent(tempcount);
          if (floatBoard != null && floatBoard.isVisible())
            floatBoard.boardRenderer.drawKataEstimateByTransparent(tempcount);
        }
      }
      if ((Lizzie.config.showKataGoEstimateOnSubbord || isShowingHeatmap) && extraMode != 2) {
        if (Lizzie.config.showSubBoard)
          Lizzie.frame.subBoardRenderer.drawKataEstimateByTransparent(tempcount);
        if (independentSubBoard != null && independentSubBoard.isVisible())
          independentSubBoard.subBoardRenderer.drawKataEstimateByTransparent(tempcount);
      }
    }
  }

  public void setAsMain() {
    while (Lizzie.board.setAsMainBranch()) ;
    renderVarTree(0, 0, false, false);
    refresh();
  }

  public void autoSavePlayedGame() {
    new Thread() {
      public void run() {
        String fileName = Lizzie.board.getHistory().getGameInfo().getSaveFileName();
        if (fileName.equals("")) {
          fileName = new SimpleDateFormat("yyyy-MM-dd-HH-mmss").format(new Date());
        } else {
          fileName =
              new SimpleDateFormat("yyyy-MM-dd-HH-mmss").format(new Date()) + "(" + fileName + ")";
        }
        File file = new File("");
        String courseFile = "";
        try {
          courseFile = file.getCanonicalPath();
        } catch (IOException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
        File autoSaveFile;
        autoSaveFile = new File(courseFile + Utils.pwd + "MyGames" + Utils.pwd + fileName + ".sgf");
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
      }
    }.start();
  }

  public boolean stopAiPlayingAndPolicy() {
    boolean isGaming =
        Lizzie.frame.isPlayingAgainstLeelaz || Lizzie.frame.isAnaPlayingAgainstLeelaz;
    if (Lizzie.frame.isShowingHeatmap) {
      Lizzie.leelaz.toggleHeatmap(true);
      Lizzie.leelaz.notPondering();
      if (Lizzie.leelaz.isKatago) clearKataEstimate();
    }
    if (Lizzie.frame.isShowingPolicy && Lizzie.leelaz.isPondering()) {
      Lizzie.frame.togglePolicy();
      Lizzie.leelaz.notPondering();
    }
    if (Lizzie.frame.isPlayingAgainstLeelaz) {
      stopTimer();
      setAsMain();
      restoreWRN(true);
      Lizzie.leelaz.setGameStatus(false);
      if (Lizzie.config.autoSavePlayedGame) autoSavePlayedGame();
      Lizzie.frame.isPlayingAgainstLeelaz = false;
      Lizzie.leelaz.isThinking = false;
      Lizzie.leelaz.notPondering();
      boardRenderer.removeblock();
      if (extraMode == 2) {
        boardRenderer2.removeblock();
      }
      Lizzie.frame.toolbar.chkShowBlack.setSelected(true);
      Lizzie.frame.toolbar.chkShowWhite.setSelected(true);
      if (Lizzie.config.showDoubleMenu) {
        Lizzie.frame.menu.chkShowBlack.setSelected(true);
        Lizzie.frame.menu.chkShowWhite.setSelected(true);
      }
    }
    if (Lizzie.frame.isAnaPlayingAgainstLeelaz) {
      stopTimer();
      setAsMain();
      restoreWRN(false);
      if (Lizzie.leelaz.isheatmap) {
        Lizzie.leelaz.isheatmap = false;
        this.isShowingHeatmap = false;
      }
      Lizzie.leelaz.setGameStatus(false);
      if (Lizzie.config.autoSavePlayedGame) autoSavePlayedGame();
      Lizzie.frame.isAnaPlayingAgainstLeelaz = false;
      Lizzie.frame.toolbar.chkAutoPlay.setSelected(false);
      Lizzie.frame.toolbar.isAutoPlay = false;
      Lizzie.frame.toolbar.chkAutoPlayBlack.setSelected(false);
      Lizzie.frame.toolbar.chkAutoPlayWhite.setSelected(false);
      Lizzie.frame.toolbar.chkShowBlack.setSelected(true);
      Lizzie.frame.toolbar.chkShowWhite.setSelected(true);
      if (Lizzie.config.showDoubleMenu) {
        Lizzie.frame.menu.chkShowBlack.setSelected(true);
        Lizzie.frame.menu.chkShowWhite.setSelected(true);
      }
      Lizzie.leelaz.anaGameResignCount = 0;
      Lizzie.leelaz.notPondering();
      boardRenderer.removeblock();
      if (extraMode == 2) {
        boardRenderer2.removeblock();
      }
    }
    if (Lizzie.config.isAutoAna) {
      Lizzie.config.isAutoAna = false;
      Lizzie.frame.toolbar.chkAutoAnalyse.setSelected(false);
      Lizzie.leelaz.notPondering();
    }
    Lizzie.frame.menu.toggleDoubleMenuGameStatus();
    return isGaming;
  }

  public void showMainPanel() {
    setCommentPaneContent();
    mainPanel.setVisible(true);
  }

  public void reSetLoc() {
    SwingUtilities.invokeLater(
        new Thread() {
          public void run() {
            int width = getWidth() - getInsets().left - getInsets().right;
            if (Lizzie.config.showTopToolBar) {
              if (Lizzie.config.autoWrapToolBar) {
                topPanel.setBounds(0, 0, width, Lizzie.config.menuHeight);
                int curHeight = topPanel.getPreferredSize().height + 8;
                topPanelHeight = Lizzie.config.menuHeight;
                if (curHeight / Lizzie.config.menuHeight > 1) {
                  topPanelHeight =
                      (curHeight / Lizzie.config.menuHeight) * Lizzie.config.menuHeight;
                  topPanel.setBounds(0, 0, width, topPanelHeight);
                  if (Lizzie.config.isScaled) repaint();
                }
              } else {
                topPanel.setBounds(0, 0, 9999, Lizzie.config.menuHeight);
                topPanelHeight = Lizzie.config.menuHeight;
              }
            } else {
              topPanelHeight = 0;
              topPanel.setVisible(false);
            }
            mainPanel.setBounds(
                0,
                (Lizzie.config.showDoubleMenu ? topPanelHeight : 0),
                Utils.zoomOut(width),
                Utils.zoomOut(
                    Lizzie.frame.getHeight()
                        - Lizzie.frame.getJMenuBar().getHeight()
                        - Lizzie.frame.getInsets().top
                        - Lizzie.frame.getInsets().bottom
                        - toolbarHeight
                        - (Lizzie.config.showDoubleMenu ? topPanelHeight : 0)));
            toolbar.setBounds(
                0,
                Lizzie.frame.getHeight()
                    - Lizzie.frame.getJMenuBar().getHeight()
                    - Lizzie.frame.getInsets().top
                    - Lizzie.frame.getInsets().bottom
                    - toolbarHeight,
                width,
                toolbarHeight);
            if (toolbar.showDetail) toolbar.setDetailIcon();
            toolbar.reSetButtonLocation();
            if (tempGamePanelAll.isVisible()) showTempGamePanel();
          }
        });
  }

  public void testFilter(Integer txtFieldIntValue) {
    // TODO Auto-generated method stub
    filter20 = new GaussianFilter(txtFieldIntValue);
    redrawBackgroundAnyway = true;
    redrawTree = true;
    refresh();
  }

  private void saveIndependSubBoardToClipboard() {
    if (Lizzie.config.isScaled) {
      int width = this.independentSubBoard.cachedImage.getWidth();
      int height = this.independentSubBoard.cachedImage.getHeight();
      Rectangle rect = new Rectangle(0, 0, width, height);
      BufferedImage areaImage =
          this.independentSubBoard.cachedImage.getSubimage(rect.x, rect.y, rect.width, rect.height);
      BufferedImage buffImg = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
      buffImg
          .getGraphics()
          .drawImage(
              areaImage.getScaledInstance(width, height, java.awt.Image.SCALE_SMOOTH), 0, 0, null);
      setClipboardImage(buffImg);
    } else {
      int width = this.independentSubBoard.getWidth();
      int height = this.independentSubBoard.getHeight();
      BufferedImage bImg = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
      Graphics2D cg = bImg.createGraphics();

      this.independentSubBoard.paintAll(cg);
      cg.dispose();
      Rectangle rect = new Rectangle(0, 0, width, height);
      BufferedImage areaImage = bImg.getSubimage(rect.x, rect.y, rect.width, rect.height);
      BufferedImage buffImg = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
      buffImg
          .getGraphics()
          .drawImage(
              areaImage.getScaledInstance(width, height, java.awt.Image.SCALE_SMOOTH), 0, 0, null);
      setClipboardImage(buffImg);
    }
  }

  private BufferedImage getIndependSubBoardToClipboard() {
    if (Lizzie.config.isScaled) {
      int width = this.independentSubBoard.cachedImage.getWidth();
      int height = this.independentSubBoard.cachedImage.getHeight();
      Rectangle rect = new Rectangle(0, 0, width, height);
      BufferedImage areaImage =
          this.independentSubBoard.cachedImage.getSubimage(rect.x, rect.y, rect.width, rect.height);
      BufferedImage buffImg = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
      buffImg
          .getGraphics()
          .drawImage(
              areaImage.getScaledInstance(width, height, java.awt.Image.SCALE_SMOOTH), 0, 0, null);
      return buffImg;
    } else {
      int width = this.independentSubBoard.getWidth();
      int height = this.independentSubBoard.getHeight();
      BufferedImage bImg = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
      Graphics2D cg = bImg.createGraphics();

      this.independentSubBoard.paintAll(cg);
      cg.dispose();
      Rectangle rect = new Rectangle(0, 0, width, height);
      BufferedImage areaImage = bImg.getSubimage(rect.x, rect.y, rect.width, rect.height);
      BufferedImage buffImg = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
      buffImg
          .getGraphics()
          .drawImage(
              areaImage.getScaledInstance(width, height, java.awt.Image.SCALE_SMOOTH), 0, 0, null);
      return buffImg;
    }
  }

  public void copySubBoard() {
    if (independentSubBoard != null && independentSubBoard.isVisible()) {
      saveIndependSubBoardToClipboard();
    } else if (Lizzie.config.showSubBoard) {
      savePicToClipboard(
          subBoardRenderer.x,
          subBoardRenderer.y,
          subBoardRenderer.boardWidth,
          subBoardRenderer.boardHeight);
    }
  }

  public void undoForRightClick() {
    if (Lizzie.frame.isPlayingAgainstLeelaz || Lizzie.frame.isAnaPlayingAgainstLeelaz) {
      Lizzie.board.previousMove(false);
      Lizzie.board.previousMove(true);
    } else Input.undo();
  }

  public void setMouseOverCoordsIndependentMainBoard(int index) {
    independentMainBoard.setMouseOverCoords(index);
  }

  public void setMouseOverCoords(int index) {
    if (extraMode == 8) {
      this.independentMainBoard.setMouseOverCoords(index);
      return;
    }
    List<MoveData> bestMoves = Lizzie.board.getHistory().getData().bestMoves;
    if (bestMoves == null || bestMoves.isEmpty()) return;
    if (index >= bestMoves.size()) return;
    if (curSuggestionMoveOrderByNumber == index) {
      curSuggestionMoveOrderByNumber = -1;
      mouseOverCoordinate = outOfBoundCoordinate;
      clearMoved();
      return;
    }
    curSuggestionMoveOrderByNumber = index;
    mouseOverCoordinate =
        Lizzie.board.convertNameToCoordinates(
            Lizzie.board.getHistory().getData().bestMoves.get(index).coordinate);
  }

  private void handleTableClick(int row, int col) {
    if (hasMoveOutOfList) {
      hasMoveOutOfList = false;
      Lizzie.frame.boardRenderer.startNormalBoard();
      Lizzie.frame.suggestionclick = Lizzie.frame.outOfBoundCoordinate;
      Lizzie.frame.mouseOverCoordinate = Lizzie.frame.outOfBoundCoordinate;
      Lizzie.frame.boardRenderer.clearBranch();
      selectedorder = -1;
      clickOrder = -1;
      currentRow = -1;
      Lizzie.frame.refresh();
      return;
    }
    Lizzie.frame.boardRenderer.startNormalBoard();
    if (listTable.getValueAt(row, 1).toString().startsWith("pass")) return;
    if (clickOrder != -1
        && selectedorder >= 0
        && Lizzie.board.convertNameToCoordinates(listTable.getValueAt(row, 1).toString())[0]
            == Lizzie.frame.suggestionclick[0]
        && Lizzie.board.convertNameToCoordinates(listTable.getValueAt(row, 1).toString())[1]
            == Lizzie.frame.suggestionclick[1]) {
      Lizzie.frame.suggestionclick = Lizzie.frame.outOfBoundCoordinate;
      Lizzie.frame.mouseOverCoordinate = Lizzie.frame.outOfBoundCoordinate;
      Lizzie.frame.boardRenderer.clearBranch();
      selectedorder = -1;
      clickOrder = -1;
      currentRow = -1;
      Lizzie.frame.refresh();
    } else {

      clickOrder = row;
      selectedorder = row;
      currentRow = row;
      int[] coords = Lizzie.board.convertNameToCoordinates(listTable.getValueAt(row, 1).toString());
      Lizzie.frame.mouseOverCoordinate = coords;
      Lizzie.frame.suggestionclick = coords;
      Lizzie.frame.refresh();
    }
    if (Lizzie.frame.independentMainBoard != null) {
      Lizzie.frame.independentMainBoard.mouseOverCoordinate = Lizzie.frame.mouseOverCoordinate;
    }
  }

  private void handleTableRightClick(int row, int col) {
    if (listTable.getValueAt(row, 1).toString().startsWith("pass")) return;
    if (selectedorder != row) {
      int[] coords = Lizzie.board.convertNameToCoordinates(listTable.getValueAt(row, 1).toString());
      Lizzie.frame.suggestionclick = coords;
      Lizzie.frame.mouseOverCoordinate = Lizzie.frame.outOfBoundCoordinate;
      Lizzie.frame.refresh();
      selectedorder = row;
    } else {
      Lizzie.frame.suggestionclick = Lizzie.frame.outOfBoundCoordinate;
      Lizzie.frame.refresh();
      selectedorder = -1;
    }
  }

  private void handleTableDoubleClick(int row, int col) {
    String aa = listTable.getValueAt(row, 1).toString();
    int[] coords = Lizzie.board.convertNameToCoordinates(aa);
    Lizzie.board.place(coords[0], coords[1]);
  }

  public AbstractTableModel getTableModel() {

    return new AbstractTableModel() {
      List<MoveData> bestMoves = null;
      ArrayList<MoveData> data2 = new ArrayList<MoveData>();

      public int getColumnCount() {

        // if ((Lizzie.leelaz!=null&&(Lizzie.leelaz.isKatago || Lizzie.leelaz.isSai))
        //   || Lizzie.board.getData().isKataData) {
        return 6;
        // } else {
        //   return 5;
        //  }
      }

      public int getRowCount() {
        //   int rownum = 0;
        if (isInPlayMode()) return 0;
        data2 = new ArrayList<MoveData>();
        if (Lizzie.engineManager.isEngineGame && Lizzie.config.showPreviousBestmovesInEngineGame) {
          if (Lizzie.board.getHistory().getCurrentHistoryNode().previous().isPresent())
            if ((bestMoves = Lizzie.leelaz.getBestMoves()).isEmpty())
              bestMoves =
                  Lizzie.board
                      .getHistory()
                      .getCurrentHistoryNode()
                      .previous()
                      .get()
                      .getData()
                      .bestMoves;
        } else bestMoves = Lizzie.board.getHistory().getCurrentHistoryNode().getData().bestMoves;
        if (bestMoves != null)
          for (int i = 0; i < bestMoves.size(); i++) {
            data2.add(bestMoves.get(i));
          }
        try {
          if (Lizzie.board.getHistory().getCurrentHistoryNode().next().isPresent()) {
            BoardHistoryNode next = Lizzie.board.getHistory().getCurrentHistoryNode().next().get();
            if (next.getData().lastMove.isPresent()) {
              int[] coords = next.getData().lastMove.get();
              boolean hasData = false;
              for (MoveData move : data2) {
                if (Lizzie.board.convertNameToCoordinates(move.coordinate)[0] == coords[0]
                    && Lizzie.board.convertNameToCoordinates(move.coordinate)[1] == coords[1]) {
                  if (move.order == 0) {
                    move.isNextMove = true;
                    move.bestWinrate = data2.get(0).oriwinrate;
                    move.bestScoreMean = data2.get(0).scoreMean;
                  } else {
                    if (data2.size() > 0 && !hasData && !next.getData().bestMoves.isEmpty()) {
                      if (next.getData().getPlayouts() > move.playouts) {
                        MoveData curMove = new MoveData();
                        curMove.playouts = next.getData().getPlayouts();
                        curMove.coordinate =
                            Lizzie.board.convertCoordinatesToName(coords[0], coords[1]);
                        curMove.winrate = 100.0 - next.getData().winrate;
                        curMove.policy = 0;
                        curMove.scoreMean = -next.getData().scoreMean;
                        curMove.scoreStdev = 0;
                        curMove.order = move.order;
                        curMove.isNextMove = true;
                        curMove.lcb = 0;
                        curMove.bestWinrate = data2.get(0).winrate;
                        curMove.bestScoreMean = data2.get(0).scoreMean;
                        data2.add(0, curMove);
                        hasData = true;
                        break;
                      }
                    }
                    MoveData curMove = new MoveData();
                    curMove.playouts = move.playouts;
                    curMove.coordinate = move.coordinate;
                    curMove.winrate = move.winrate;
                    curMove.policy = move.policy;
                    curMove.scoreMean = move.scoreMean;
                    curMove.scoreStdev = move.scoreStdev;
                    curMove.order = move.order;
                    curMove.isNextMove = true;
                    curMove.lcb = move.lcb;
                    curMove.bestWinrate = data2.get(0).oriwinrate;
                    curMove.bestScoreMean = data2.get(0).scoreMean;
                    data2.add(0, curMove);
                  }
                  hasData = true;
                  break;
                }
              }
              if (data2.size() > 0 && !hasData && !next.getData().bestMoves.isEmpty()) {
                MoveData curMove = new MoveData();
                curMove.playouts = next.getData().getPlayouts();
                curMove.coordinate = Lizzie.board.convertCoordinatesToName(coords[0], coords[1]);
                curMove.winrate = 100.0 - next.getData().winrate;
                curMove.policy = 0;
                curMove.scoreMean = -next.getData().scoreMean;
                curMove.scoreStdev = 0;
                curMove.order = -100;
                curMove.isNextMove = true;
                curMove.lcb = 0;
                curMove.bestWinrate = data2.get(0).winrate;
                curMove.bestScoreMean = data2.get(0).scoreMean;
                data2.add(0, curMove);
                hasData = true;
              }
            }
          }
        } catch (Exception e) {

        }
        return Math.min(data2.size(), 20);
      }

      public String getColumnName(int column) {
        if (column == 0) return resourceBundle.getString("AnalysisFrame.column1"); // "序号";
        if (column == 1) return resourceBundle.getString("AnalysisFrame.column2"); // "坐标";
        if (column == 2) return resourceBundle.getString("LizzieFrame.listColumn2"); // "胜率(%)";
        if (column == 3) return resourceBundle.getString("AnalysisFrame.column5"); // "计算量";
        if (column == 4) return resourceBundle.getString("LizzieFrame.listColumn4"); // "占比(%)";
        if (column == 5) return resourceBundle.getString("AnalysisFrame.column8"); // "目差";
        return "";
      }

      @SuppressWarnings("unchecked")
      public Object getValueAt(int row, int col) {

        int totalPlayouts = 0;
        for (MoveData move : data2) {
          totalPlayouts = totalPlayouts + move.playouts;
        }
        if (row > data2.size() - 1) return "";
        MoveData data = data2.get(row);
        switch (col) {
          case 0:
            if (data.order == -100)
              return "\n" + resourceBundle.getString("AnalysisFrame.actual") + "\n";
            else if (data.isNextMove)
              return data.order + 1 + "(" + resourceBundle.getString("AnalysisFrame.actual") + ")";
            if (data.coordinate.startsWith("pas")) return "Pass";
            return data.order + 1;
          case 1:
            return data.coordinate;
          case 2:
            if (data.isNextMove) {
              if (data.order != 0) {
                double diff = data.winrate - data.bestWinrate;
                return (diff > 0 ? "↑" : "↓")
                    + String.format("%.1f", diff)
                    + "("
                    + String.format(
                        "%.1f",
                        Lizzie.config.winrateAlwaysBlack
                            ? (Lizzie.board.getHistory().isBlacksTurn()
                                ? data.winrate
                                : 100 - data.winrate)
                            : data.winrate)
                    + ")";
              }
            }
            return String.format(
                "%.1f",
                Lizzie.config.winrateAlwaysBlack
                    ? (Lizzie.board.getHistory().isBlacksTurn() ? data.winrate : 100 - data.winrate)
                    : data.winrate);
          case 3:
            return Lizzie.frame.getPlayoutsString(data.playouts);
          case 4:
            return String.format("%.1f", (double) data.playouts * 100 / totalPlayouts);
          case 5:
            double score = data.scoreMean;
            if (Lizzie.engineManager.isEngineGame
                && Lizzie.engineManager.engineGameInfo.isGenmove) {
              if (!Lizzie.board.getHistory().isBlacksTurn()) {
                if (Lizzie.config.showKataGoBoardScoreMean) {
                  score = score + Lizzie.board.getHistory().getGameInfo().getKomi();
                }
              } else {
                if (Lizzie.config.showKataGoBoardScoreMean) {
                  score = score - Lizzie.board.getHistory().getGameInfo().getKomi();
                }
                if (Lizzie.config.winrateAlwaysBlack) {
                  score = -score;
                }
              }
            } else {
              if (Lizzie.board.getHistory().isBlacksTurn()) {
                if (Lizzie.config.showKataGoBoardScoreMean) {
                  score = score + Lizzie.board.getHistory().getGameInfo().getKomi();
                }
              } else {
                if (Lizzie.config.showKataGoBoardScoreMean) {
                  score = score - Lizzie.board.getHistory().getGameInfo().getKomi();
                }
                if (Lizzie.config.winrateAlwaysBlack) {
                  score = -score;
                }
              }
            }
            if (data.isNextMove && data.order != 0) {
              double diff = data.scoreMean - data.bestScoreMean;
              return (diff > 0 ? "↑" : "↓")
                  + String.format("%.1f", diff)
                  + "("
                  + String.format("%.1f", score)
                  + ")";
            } else return String.format("%.1f", score);
          default:
            return "";
        }
      }
    };
  }

  public void hiddenColumn(int columnIndex, JTable table) {
    if (columnIndex == 5) scoreColumnIsHidden = true;
    TableColumnModel tcm = table.getColumnModel();
    TableColumn tc = tcm.getColumn(columnIndex);
    tc.setWidth(0);
    tc.setPreferredWidth(0);
    tc.setMaxWidth(0);
    tc.setMinWidth(0);
    table.getTableHeader().getColumnModel().getColumn(columnIndex).setMaxWidth(0);
    table.getTableHeader().getColumnModel().getColumn(columnIndex).setMinWidth(0);
  }

  public void resumColumn(int columnIndex, JTable table, int width) {
    if (columnIndex == 5) scoreColumnIsHidden = false;
    TableColumnModel tcm = table.getColumnModel();
    TableColumn tc = tcm.getColumn(columnIndex);
    tc.setMaxWidth(9999);
    tc.setMinWidth(0);
    table.getTableHeader().getColumnModel().getColumn(columnIndex).setMaxWidth(9999);
    table.getTableHeader().getColumnModel().getColumn(columnIndex).setMinWidth(0);
    tc.setWidth(width);
    tc.setPreferredWidth(width);
  }

  class ColorTableCellRenderer extends DefaultTableCellRenderer {
    Object mainValue;
    boolean isPlayoutPercents = false;
    boolean isSelect = false;
    boolean isChanged = false;
    boolean isNextMove;
    double diff = 0;
    double scoreDiff = 0;

    public Component getTableCellRendererComponent(
        JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
      // if(row%2 == 0){
      setHorizontalAlignment(CENTER);
      if (column == 4) {
        isPlayoutPercents = true;
        mainValue = value;
      } else {
        isPlayoutPercents = false;
      }
      String move = table.getValueAt(row, 0).toString();
      if (move.length() > 3 && !move.toLowerCase().equals("pass")) {
        isNextMove = true;
        String winrate = table.getValueAt(row, 2).toString();
        if (winrate.contains("("))
          diff = Double.parseDouble(winrate.substring(1, winrate.indexOf("(")));
        else diff = 0;
        String score = table.getValueAt(row, 5).toString();
        if (score.contains("("))
          scoreDiff = Double.parseDouble(score.substring(1, score.indexOf("(")));
        else scoreDiff = 0;
      } else isNextMove = false;

      String coordsName = table.getValueAt(row, 1).toString();
      int[] coords = new int[] {-2, -2};
      if (!coordsName.startsWith("pas") && coordsName.length() > 1) {
        coords = Lizzie.board.convertNameToCoordinates(coordsName);
      }
      if (coords[0] == Lizzie.frame.suggestionclick[0]
          && coords[1] == Lizzie.frame.suggestionclick[1]) {
        if (selectedorder >= 0 && selectedorder != row) {
          currentRow = row;
          // selectedorder = -1;
          isChanged = true;
          // setForeground(Color.RED);
        } else {
          isChanged = false;
        }
        isSelect = true;
        // setBackground(new Color(238, 221, 130));
        return super.getTableCellRendererComponent(table, value, false, false, row, column);

      } else {
        isSelect = false;
        isChanged = false;
        return super.getTableCellRendererComponent(table, value, false, false, row, column);
      }
    }

    public void paintComponent(Graphics g) {
      if (isPlayoutPercents) {
        //    if (isNextMove)
        //       setBackground(new Color(0, 221, 0, 50));
        //        } else setBackground(Color.WHITE);
        //	setForeground(Color.BLACK);
        Graphics2D g2 = (Graphics2D) g;
        //  final BasicStroke stroke=new BasicStroke(2.0f);

        //   g2.setStroke(stroke);
        //        	 if(isSelect)
        //          	   g2.setColor(new Color(238, 221, 130));
        //        	 else
        //        if (isNextMove) {
        //          g2.setColor(Color.LIGHT_GRAY);
        //        } else
        // setBackground(Color.YELLOW);
        g2.setColor(Color.LIGHT_GRAY);
        g2.fillRect(
            0,
            0,
            (int) (getWidth() * (Double.parseDouble(mainValue.toString()) / 100)),
            getHeight());

      } else {
        if (isSelect) {
          setForeground(Color.BLUE);
          setBackground(new Color(0, 0, 0, 70));
        }
        if (isChanged) {
          setForeground(Color.RED);
        }
        if (isNextMove) {
          if (isSelect) {
            if (diff < -20 || scoreDiff < -5) setBackground(new Color(150, 0, 0, 100));
            else if (diff < -5 || scoreDiff < -3) setBackground(new Color(150, 150, 0, 120));
            else setBackground(new Color(50, 150, 0, 100));
          } else {
            if (diff < -20 || scoreDiff < -5) setBackground(new Color(221, 0, 0, 50));
            else if (diff < -5 || scoreDiff < -3) setBackground(new Color(221, 221, 0, 70));
            else setBackground(new Color(0, 221, 0, 50));
          }
        } else if (!isSelect && !isChanged) {
          setForeground(Color.BLACK);
          setBackground(listTableBackground);
        }
      }
      super.paintComponent(g);
    }
  }

  public void openPrivateKifuSearch() {
    if (!Lizzie.config.uploadUser.equals("") && !Lizzie.config.uploadPassWd.equals("")) {
      SocketLoggin login = new SocketLoggin();
      String result = login.SocketLoggin(Lizzie.config.uploadUser, Lizzie.config.uploadPassWd);
      if (result.startsWith("success")) {
        PrivateKifuSearch search = new PrivateKifuSearch();
        search.setVisible(true);
      } else {
        Loggin loggin = new Loggin(null, true);
        loggin.setVisible(true);
      }
    } else {
      Loggin loggin = new Loggin(null, true);
      loggin.setVisible(true);
    }
  }

  public void toggleIndependentMainBoard() {
    Lizzie.config.isShowingIndependentMain = !Lizzie.config.isShowingIndependentMain;
    Lizzie.config.uiConfig.put("showing-independent-main", Lizzie.config.isShowingIndependentMain);
    if (independentMainBoard == null) {
      independentMainBoard = new IndependentMainBoard();
      independentMainBoard.setVisible(true);
      return;
    }
    if (!independentMainBoard.isVisible()) {
      independentMainBoard.setVisible(true);
      independentMainBoard.refresh();
      return;
    }
    if (independentMainBoard.isVisible()) independentMainBoard.setVisible(false);
  }

  public void openIndependentMainBoard() {
    independentMainBoard = new IndependentMainBoard();
    independentMainBoard.setVisible(true);
  }

  public void openIndependentSubBoard() {
    independentSubBoard = new IndependentSubBoard();
    independentSubBoard.setVisible(true);
  }

  public void toggleIndependentSubBoard() {
    Lizzie.config.isShowingIndependentSub = !Lizzie.config.isShowingIndependentSub;
    Lizzie.config.uiConfig.put("showing-independent-sub", Lizzie.config.isShowingIndependentSub);
    if (independentSubBoard == null) {
      independentSubBoard = new IndependentSubBoard();
      independentSubBoard.setVisible(true);
      return;
    }
    if (!independentSubBoard.isVisible()) {
      independentSubBoard.setVisible(true);
      independentSubBoard.refresh();
      return;
    }
    if (independentSubBoard.isVisible()) independentSubBoard.setVisible(false);
  }

  public void refreshIndependentSubBoard() {
    if (independentSubBoard == null || !independentSubBoard.isVisible()) return;
    independentSubBoard.refresh();
  }

  public void processIndependentSubboardMouseEntered() {
    // TODO Auto-generated method stub
    independentSubBoard.mouseEntered();
  }

  public void processIndependentSubboardMouseExited() {
    // TODO Auto-generated method stub
    independentSubBoard.mouseExited();
  }

  public void stopShowingControl() {
    if (Lizzie.frame.showControls) {
      if (Lizzie.config.showVariationGraph) varTreeScrollPane.setVisible(true);
      if (Lizzie.config.showListPane()) listScrollpane.setVisible(true);
      if (Lizzie.config.showComment) setCommentPaneContent();
      Lizzie.frame.showControls = false;
      Lizzie.frame.refresh();
      if (System.currentTimeMillis() - showControlTime > 2000) {
        Lizzie.config.userKnownX = true;
        Lizzie.config.uiConfig.put("user-known-x", true);
      }
      this.redrawBackgroundAnyway = true;
    }
  }

  public Image saveMainBoardToImageOri() {
    if (Lizzie.config.isScaled) {
      if (extraMode != 8) {
        int x = Lizzie.frame.boardX;
        int y = Lizzie.frame.boardY;
        int width = Lizzie.frame.maxSize;
        int height = Lizzie.frame.maxSize;
        Rectangle rect = new Rectangle(x, y, width, height);
        BufferedImage areaImage = cachedImage.getSubimage(rect.x, rect.y, rect.width, rect.height);
        BufferedImage buffImg = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        buffImg
            .getGraphics()
            .drawImage(
                areaImage.getScaledInstance(width, height, java.awt.Image.SCALE_SMOOTH),
                0,
                0,
                null);
        return buffImg;
      } else {
        int width = this.independentMainBoard.cachedImage.getWidth();
        int height = this.independentMainBoard.cachedImage.getHeight();
        Rectangle rect = new Rectangle(0, 0, width, height);
        BufferedImage areaImage =
            this.independentMainBoard.cachedImage.getSubimage(
                rect.x, rect.y, rect.width, rect.height);
        BufferedImage buffImg = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        buffImg
            .getGraphics()
            .drawImage(
                areaImage.getScaledInstance(width, height, java.awt.Image.SCALE_SMOOTH),
                0,
                0,
                null);
        return buffImg;
      }
    } else {
      if (extraMode != 8) {
        int x = Lizzie.frame.boardX;
        int y = Lizzie.frame.boardY;
        int width = Lizzie.frame.maxSize;
        int height = Lizzie.frame.maxSize;
        BufferedImage bImg =
            new BufferedImage(
                this.mainPanel.getWidth(), this.mainPanel.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D cg = bImg.createGraphics();
        this.mainPanel.paintAll(cg);
        cg.dispose();
        Rectangle rect = new Rectangle(x, y, width, height);
        BufferedImage areaImage = bImg.getSubimage(rect.x, rect.y, rect.width, rect.height);
        BufferedImage buffImg = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        buffImg
            .getGraphics()
            .drawImage(
                areaImage.getScaledInstance(width, height, java.awt.Image.SCALE_SMOOTH),
                0,
                0,
                null);
        return buffImg;
      } else {

        int width = this.independentMainBoard.getWidth();
        int height = this.independentMainBoard.getHeight();
        BufferedImage bImg = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D cg = bImg.createGraphics();

        this.independentMainBoard.paintAll(cg);
        cg.dispose();
        Rectangle rect = new Rectangle(0, 0, width, height);
        BufferedImage areaImage = bImg.getSubimage(rect.x, rect.y, rect.width, rect.height);
        BufferedImage buffImg = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        buffImg
            .getGraphics()
            .drawImage(
                areaImage.getScaledInstance(width, height, java.awt.Image.SCALE_SMOOTH),
                0,
                0,
                null);
        return buffImg;
      }
    }
  }

  public Image zoomImage(BufferedImage src, int w, int h) {
    double wr = 0, hr = 0;
    Image Itemp = src.getScaledInstance(w, h, src.SCALE_SMOOTH);
    wr = w * 1.0 / src.getWidth();
    hr = h * 1.0 / src.getHeight();
    AffineTransformOp ato = new AffineTransformOp(AffineTransform.getScaleInstance(wr, hr), null);
    Itemp = ato.filter(src, null);
    return Itemp;
  }

  public void deleteTempGame(int index) {
    ArrayList<TempGameData> data = getSaveGameList();
    File file = new File("save" + Utils.pwd + "game" + index + ".bmp");
    if (file.exists() && file.isFile()) file.delete();
    File file2 = new File("save" + Utils.pwd + "game" + index + ".sgf");
    if (file2.exists() && file2.isFile()) file2.delete();
    for (int i = index + 1; i <= data.size(); i++) {
      File oldfile = new File("save" + Utils.pwd + "game" + i + ".bmp");
      File newfile = new File("save" + Utils.pwd + "game" + (i - 1) + ".bmp");
      if (oldfile.exists()) {
        oldfile.renameTo(newfile);
      }
    }
    for (int i = index + 1; i <= data.size(); i++) {
      File oldfile = new File("save" + Utils.pwd + "game" + i + ".sgf");
      File newfile = new File("save" + Utils.pwd + "game" + (i - 1) + ".sgf");
      if (oldfile.exists()) {
        oldfile.renameTo(newfile);
      }
    }

    data.remove(index - 1);
    saveTempGame(data);
  }

  public void deleteAllTempGame() {
    ArrayList<TempGameData> data = getSaveGameList();
    for (int index = 1; index < data.size() + 1; index++) {
      File file = new File("save" + Utils.pwd + "game" + index + ".bmp");
      if (file.exists() && file.isFile()) file.delete();
      File file2 = new File("save" + Utils.pwd + "game" + index + ".sgf");
      if (file2.exists() && file2.isFile()) file2.delete();
    }
    saveTempGame(new ArrayList<TempGameData>());
    try {
      Lizzie.config.saveTempBoard();
    } catch (IOException es) {
      // TODO Auto-generated catch block
      es.printStackTrace();
    }
  }

  public void saveTempGame(int index, String name) {
    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    ArrayList<TempGameData> data = getSaveGameList();
    data.get(index - 1).name = name;
    data.get(index - 1).time = df.format(new Date());
    data.get(index - 1).curMoveNumer = Lizzie.board.getcurrentmovenumber();
    data.get(index - 1).moves =
        Lizzie.board.moveListToString(Lizzie.board.getmovelistForSaveLoad());
    saveTempGame(data);
    File file = new File("save" + Utils.pwd + "game" + index + ".bmp");
    try {
      SGFParser.save(Lizzie.board, "save" + Utils.pwd + "game" + index + ".sgf");
      ImageIO.write((RenderedImage) saveMainBoardToImageOri(), "bmp", file);
    } catch (IOException e1) {
      // TODO Auto-generated catch block
      e1.printStackTrace();
    }
    try {
      Lizzie.config.saveTempBoard();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  public void addTempGame(int index, String name) {
    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    ArrayList<TempGameData> data = getSaveGameList();
    TempGameData newData = new TempGameData();
    newData.name = name;
    newData.time = df.format(new Date());
    newData.curMoveNumer = Lizzie.board.getcurrentmovenumber();
    newData.moves = Lizzie.board.moveListToString(Lizzie.board.getmovelist());
    data.add(newData);
    saveTempGame(data);
    File file = new File("save" + Utils.pwd + "game" + index + ".bmp");
    try {
      SGFParser.save(Lizzie.board, "save" + Utils.pwd + "game" + index + ".sgf");
      ImageIO.write((RenderedImage) saveMainBoardToImageOri(), "bmp", file);
    } catch (IOException e1) {
      // TODO Auto-generated catch block
      e1.printStackTrace();
    }
    try {
      Lizzie.config.saveTempBoard();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  public void saveAutoGame(int index) {
    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    Lizzie.config.saveBoardConfig.put("save-auto-game-index" + index, index == 2 ? -5 : 1);
    Lizzie.config.saveBoardConfig.put("save-auto-game-time" + index, df.format(new Date()));
    Lizzie.config.saveBoardConfig.put(
        "save-auto-game-move-number" + index, Lizzie.board.getcurrentmovenumber());
    Lizzie.config.saveBoardConfig.put(
        "save-auto-game-move-list" + index,
        Lizzie.board.moveListToString(Lizzie.board.getmovelistForSaveLoad()));
    if (index == 1) Lizzie.config.saveBoardConfig.put("save-auto-game-index2", -1);
    File file = new File("save" + Utils.pwd + "autoGame" + index + ".bmp");
    try {
      SGFParser.save(Lizzie.board, "save" + Utils.pwd + "autoGame" + index + ".sgf", true);
      ImageIO.write((RenderedImage) saveMainBoardToImageOri(), "bmp", file);
    } catch (IOException e1) {
      // TODO Auto-generated catch block
      e1.printStackTrace();
    }
    try {
      Lizzie.config.saveTempBoard();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  public void addTempGameOne(
      int index,
      int x,
      int y,
      String name,
      String time,
      boolean isAutoSave,
      int moveNumber,
      String moveList,
      boolean oriShowListPane,
      boolean OriShowVariationGraph) {
    JLabel boardImage = new JLabel();
    File file =
        new File(
            (isAutoSave ? "save" + Utils.pwd + "autoGame" : "save" + Utils.pwd + "game")
                + index
                + ".bmp");
    try {
      BufferedImage img = ImageIO.read(file);
      Image img2 = zoomImage(img, 300, 300);
      boardImage.setIcon(new ImageIcon(img2));
    } catch (IOException e1) {
      // TODO Auto-generated catch block
      e1.printStackTrace();
    }
    boardImage.setBounds(x, y, 300, 300);
    JLabel lblIndex =
        new JLabel(
            isAutoSave
                ? Lizzie.resourceBundle.getString("LizzieFrame.saveAndLoad.autoRec")
                : Lizzie.resourceBundle.getString("LizzieFrame.saveAndLoad.rec") + index);
    lblIndex.setForeground(Color.WHITE);

    JTextField txtName = new JTextField();
    txtName.setForeground(Color.WHITE);
    txtName.setBackground(Color.DARK_GRAY);

    txtName.setText(name);
    if (isAutoSave) txtName.setEnabled(false);
    JButton btnLoad = new JButton(Lizzie.resourceBundle.getString("LizzieFrame.saveAndLoad.load"));
    btnLoad.setMargin(new Insets(0, 0, 0, 0));

    btnLoad.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            // TBD未完成
            canShowBigBoardImage = false;
            loadFile(
                new File(
                    (isAutoSave ? "save" + Utils.pwd + "autoGame" : "save" + Utils.pwd + "game")
                        + index
                        + ".sgf"),
                true,
                true);
            if (!moveList.equals("")) Lizzie.board.playList(moveList);
            else Lizzie.board.goToMoveNumber(moveNumber);
            if (Lizzie.leelaz.isPondering()) Lizzie.leelaz.ponder();
            hideTempGamePanel(oriShowListPane, OriShowVariationGraph);
          }
        });

    JButton btnSave = new JButton(Lizzie.resourceBundle.getString("LizzieFrame.saveAndLoad.save"));
    btnSave.setMargin(new Insets(0, 0, 0, 0));

    btnSave.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            // TBD未完成
            isShowingBigBoardPanel = true;
            if (bigBoardPanel != null && bigBoardPanel.isVisible()) bigBoardPanel.setVisible(false);
            int ret =
                JOptionPane.showConfirmDialog(
                    Lizzie.frame,
                    resourceBundle.getString("LizzieFrame.recordExists"),
                    resourceBundle.getString("LizzieFrame.warning"),
                    JOptionPane.YES_NO_OPTION);
            if (ret == JOptionPane.NO_OPTION) {
              isShowingBigBoardPanel = false;
              return;
            }
            isShowingBigBoardPanel = false;
            saveTempGame(index, Lizzie.board.getHistory().getGameInfo().getSaveFileName());
            Lizzie.config.showListPane = oriShowListPane;
            Lizzie.config.showVariationGraph = OriShowVariationGraph;
            showTempGamePanel();
          }
        });

    JButton btnDelete = new JButton(Lizzie.resourceBundle.getString("LizzieFrame.saveAndLoad.del"));
    btnDelete.setMargin(new Insets(0, 0, 0, 0));

    btnDelete.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            // TBD未完成
            if (isAutoSave) {
              Lizzie.config.saveBoardConfig.put("save-auto-game-index" + index, -2);
            } else {
              deleteTempGame(index);
            }
            try {
              Lizzie.config.saveTempBoard();
            } catch (IOException es) {
              // TODO Auto-generated catch block
              es.printStackTrace();
            }
            Lizzie.config.showListPane = oriShowListPane;
            Lizzie.config.showVariationGraph = OriShowVariationGraph;
            showTempGamePanel();
          }
        });

    JLabel lblTime = new JLabel(time);
    lblTime.setForeground(Color.WHITE);
    if (isAutoSave) btnSave.setEnabled(false);

    JButton btnRename =
        new JButton(Lizzie.resourceBundle.getString("LizzieFrame.saveAndLoad.reName"));
    btnRename.setMargin(new Insets(0, 0, 0, 0));
    if (isAutoSave) btnRename.setEnabled(false);
    btnRename.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            // TBD
            ArrayList<TempGameData> data = getSaveGameList();
            data.get(index - 1).name = txtName.getText();
            try {
              Lizzie.config.saveTempBoard();
            } catch (IOException es) {
              // TODO Auto-generated catch block
              es.printStackTrace();
            }
            saveTempGame(data);
            Lizzie.config.showListPane = oriShowListPane;
            Lizzie.config.showVariationGraph = OriShowVariationGraph;
            showTempGamePanel();
          }
        });
    lblIndex.setBounds(x + 5, y + 300, 65, 20);
    txtName.setBounds(x + (isAutoSave ? 70 : 45), y + 300, isAutoSave ? 169 : 194, 20);
    btnRename.setBounds(x + 240, y + 300, 60, 20);

    lblTime.setBounds(x + 5, y + 320, 200, 20);
    btnLoad.setBounds(x + 150, y + 320, 50, 20);
    btnSave.setBounds(x + 200, y + 320, 50, 20);
    btnDelete.setBounds(x + 250, y + 320, 50, 20);

    tempGamePanel.add(btnRename);
    tempGamePanel.add(lblTime);
    tempGamePanel.add(btnDelete);
    tempGamePanel.add(btnSave);
    tempGamePanel.add(btnLoad);
    tempGamePanel.add(txtName);
    tempGamePanel.add(lblIndex);
    tempGamePanel.add(boardImage);
  }

  public void addTempGameNew(
      int index, int x, int y, boolean oriShowListPane, boolean OriShowVariationGraph) {
    JButton boardImage =
        new JButton(Lizzie.resourceBundle.getString("LizzieFrame.saveAndLoad.newRecord"));
    boardImage.setFont(new Font("SansSerif", Font.TRUETYPE_FONT, 15));
    boardImage.setBounds(x, y, 300, 300);
    JLabel lblIndex =
        new JLabel(Lizzie.resourceBundle.getString("LizzieFrame.saveAndLoad.rec") + index);
    lblIndex.setForeground(Color.WHITE);
    lblIndex.setBounds(x + 5, y + 300, 65, 20);
    JTextField txtName = new JTextField();
    txtName.setForeground(Color.WHITE);
    txtName.setBackground(Color.DARK_GRAY);
    txtName.setBounds(x + 45, y + 300, 174, 20);

    JButton btnSave =
        new JButton(Lizzie.resourceBundle.getString("LizzieFrame.saveAndLoad.newRecord"));
    btnSave.setMargin(new Insets(0, 0, 0, 0));

    btnSave.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            // TBD未完成
            addTempGame(
                index,
                txtName.getText().length() > 0
                    ? txtName.getText()
                    : Lizzie.board.getHistory().getGameInfo().getSaveFileName());
            Lizzie.config.showListPane = oriShowListPane;
            Lizzie.config.showVariationGraph = OriShowVariationGraph;
            showTempGamePanel();
          }
        });
    boardImage.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            // TBD未完成
            addTempGame(
                index,
                txtName.getText().length() > 0
                    ? txtName.getText()
                    : Lizzie.board.getHistory().getGameInfo().getSaveFileName());
            Lizzie.config.showListPane = oriShowListPane;
            Lizzie.config.showVariationGraph = OriShowVariationGraph;
            showTempGamePanel();
          }
        });

    btnSave.setBounds(x + 220, y + 300, 80, 20);

    // tempGamePanel.add(btnDelete);
    tempGamePanel.add(btnSave);
    // tempGamePanel.add(btnLoad);
    tempGamePanel.add(txtName);
    tempGamePanel.add(lblIndex);
    tempGamePanel.add(boardImage);
  }

  public void saveTempGame(ArrayList<TempGameData> tempGameList) {
    JSONArray saveIndex = new JSONArray();
    JSONArray saveName = new JSONArray();
    JSONArray saveTime = new JSONArray();
    JSONArray saveMoveNumber = new JSONArray();
    JSONArray saveMoveList = new JSONArray();
    int s = 1;
    for (TempGameData data : tempGameList) {
      saveIndex.put(s);
      saveName.put(data.name);
      saveTime.put(data.time);
      saveMoveNumber.put(data.curMoveNumer);
      saveMoveList.put(data.moves);
      s++;
    }
    Lizzie.config.saveBoardConfig.put("save-game-index", saveIndex);
    Lizzie.config.saveBoardConfig.put("save-game-name", saveName);
    Lizzie.config.saveBoardConfig.put("save-game-time", saveTime);
    Lizzie.config.saveBoardConfig.put("save-game-move-number", saveMoveNumber);
    Lizzie.config.saveBoardConfig.put("save-game-move-list", saveMoveList);
  }

  public ArrayList<TempGameData> getSaveGameList() {
    ArrayList<TempGameData> tempGameList = new ArrayList<TempGameData>();
    Optional<JSONArray> saveIndex =
        Optional.ofNullable(Lizzie.config.saveBoardConfig.optJSONArray("save-game-index"));
    Optional<JSONArray> saveName =
        Optional.ofNullable(Lizzie.config.saveBoardConfig.optJSONArray("save-game-name"));
    Optional<JSONArray> saveTime =
        Optional.ofNullable(Lizzie.config.saveBoardConfig.optJSONArray("save-game-time"));
    Optional<JSONArray> saveMoveNumber =
        Optional.ofNullable(Lizzie.config.saveBoardConfig.optJSONArray("save-game-move-number"));
    Optional<JSONArray> saveMoveList =
        Optional.ofNullable(Lizzie.config.saveBoardConfig.optJSONArray("save-game-move-list"));
    for (int s = 0; s < (saveIndex.isPresent() ? saveIndex.get().length() : 0); s++) {
      TempGameData data = new TempGameData();
      data.index = saveIndex.get().getInt(s);
      data.name = saveName.get().getString(s);
      data.time = saveTime.get().getString(s);
      data.curMoveNumer = saveMoveNumber.get().getInt(s);
      if (saveMoveList.isPresent()) data.moves = saveMoveList.get().optString(s, "");
      else data.moves = "";
      data.isAutoSave = false;
      tempGameList.add(data);
    }

    return tempGameList;
  }

  public ArrayList<TempGameData> getTempGameList() {
    ArrayList<TempGameData> tempGameList = new ArrayList<TempGameData>();

    if (Lizzie.config.saveBoardConfig.optInt("save-auto-game-index1", -1) > 0) {
      String time = Lizzie.config.saveBoardConfig.optString("save-auto-game-time1", "");
      int moveNumer = Lizzie.config.saveBoardConfig.optInt("save-auto-game-move-number1", 0);
      TempGameData data = new TempGameData();
      data.index = 1;
      data.name = Lizzie.resourceBundle.getString("LizzieFrame.saveAndLoad.exitRecord");
      data.time = time;
      data.curMoveNumer = moveNumer;
      data.moves = Lizzie.config.saveBoardConfig.optString("save-auto-game-move-list1", "");
      data.isAutoSave = true;
      tempGameList.add(data);
    }

    Optional<JSONArray> saveIndex =
        Optional.ofNullable(Lizzie.config.saveBoardConfig.optJSONArray("save-game-index"));
    Optional<JSONArray> saveName =
        Optional.ofNullable(Lizzie.config.saveBoardConfig.optJSONArray("save-game-name"));
    Optional<JSONArray> saveTime =
        Optional.ofNullable(Lizzie.config.saveBoardConfig.optJSONArray("save-game-time"));
    Optional<JSONArray> saveMoveNumber =
        Optional.ofNullable(Lizzie.config.saveBoardConfig.optJSONArray("save-game-move-number"));
    Optional<JSONArray> saveMoveList =
        Optional.ofNullable(Lizzie.config.saveBoardConfig.optJSONArray("save-game-move-list"));
    for (int s = 0; s < (saveIndex.isPresent() ? saveIndex.get().length() : 0); s++) {
      TempGameData data = new TempGameData();
      data.index = saveIndex.get().getInt(s);
      data.name = saveName.get().getString(s);
      data.time = saveTime.get().getString(s);
      data.curMoveNumer = saveMoveNumber.get().getInt(s);
      if (saveMoveList.isPresent()) data.moves = saveMoveList.get().optString(s, "");
      else data.moves = "";
      data.isAutoSave = false;
      tempGameList.add(data);
    }

    return tempGameList;
  }

  public void hideTempGamePanel(boolean oriShowListPane, boolean OriShowVariationGraph) {
    Lizzie.config.showListPane = oriShowListPane;
    Lizzie.config.showVariationGraph = OriShowVariationGraph;
    if (Lizzie.config.showListPane()) setHideListScrollpane(true);
    if (Lizzie.config.showVariationGraph) Lizzie.frame.varTreeScrollPane.setVisible(true);
    if (Lizzie.config.showComment) setCommentPaneContent();
    commentEditPane.setVisible(false);
    tempGamePanelAll.setVisible(false);
    mainPanel.requestFocus();
    canShowBigBoardImage = false;
  }

  public void showTempGamePanel() {
    canShowBigBoardImage = true;
    if (!tempGamePanelAll.isVisible()) {
      oriShowListPane = Lizzie.config.showListPane();
      OriShowVariationGraph = Lizzie.config.showVariationGraph;
    }
    if (oriShowListPane) {
      Lizzie.config.showListPane = false;
      setHideListScrollpane(false);
    }
    commentScrollPane.setVisible(false);
    blunderContentPane.setVisible(false);
    Lizzie.config.showVariationGraph = false;
    Lizzie.frame.varTreeScrollPane.setVisible(false);
    tempGamePanel.removeAll();

    int width =
        Lizzie.frame.getWidth()
            - Lizzie.frame.getInsets().left
            - Lizzie.frame.getInsets().right
            - 12;
    tempGamePanelAll.setBounds(
        0,
        Lizzie.config.showDoubleMenu ? topPanelHeight : 0,
        Lizzie.frame.getWidth() - Lizzie.frame.getInsets().left - Lizzie.frame.getInsets().right,
        Lizzie.frame.getHeight()
            - Lizzie.frame.getJMenuBar().getHeight()
            - Lizzie.frame.getInsets().top
            - Lizzie.frame.getInsets().bottom
            - toolbarHeight
            - (Lizzie.config.showDoubleMenu ? topPanelHeight : 0));

    tempGamePanelTop.setBounds(
        0,
        0,
        Lizzie.frame.getWidth() - Lizzie.frame.getInsets().left - Lizzie.frame.getInsets().right,
        20);

    tempGameScrollPanel.setBounds(
        0,
        0,
        Lizzie.frame.getWidth() - Lizzie.frame.getInsets().left - Lizzie.frame.getInsets().right,
        Lizzie.frame.getHeight()
            - Lizzie.frame.getJMenuBar().getHeight()
            - Lizzie.frame.getInsets().top
            - Lizzie.frame.getInsets().bottom
            - toolbarHeight
            - (Lizzie.config.showDoubleMenu ? topPanelHeight : 0));
    tempGamePanelAll.setVisible(true);
    tempGameScrollPanel.setVisible(true);
    tempGamePanel.setLayout(null);

    JCheckBox chkZoomImage =
        new JCheckBox(
            Lizzie.resourceBundle.getString(
                "LizzieFrame.saveAndLoad.chkZoomImage")); // ("打开时自动恢复");
    JCheckBox chkAutoResume =
        new JCheckBox(
            Lizzie.resourceBundle.getString(
                "LizzieFrame.saveAndLoad.chkAutoResume")); // ("打开时自动恢复");
    JCheckBox chkAutoSaveOnExit =
        new JCheckBox(
            Lizzie.resourceBundle.getString(
                "LizzieFrame.saveAndLoad.chkAutoSaveOnExit")); // ("退出时自动存档");
    JButton btnDeleteAll =
        new JButton(
            Lizzie.resourceBundle.getString("LizzieFrame.saveAndLoad.btnDeleteAll")); // 全部删除
    btnDeleteAll.setMargin(new Insets(0, 0, 0, 0));
    JButton btnClose =
        new JButton(resourceBundle.getString("LizzieFrame.saveAndLoad.close")); // ("关闭");

    chkAutoSaveOnExit.setSelected(Lizzie.config.autoSaveOnExit);
    chkAutoSaveOnExit.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            Lizzie.config.autoSaveOnExit = chkAutoSaveOnExit.isSelected();
            Lizzie.config.uiConfig.put("auto-save-exit", Lizzie.config.autoSaveOnExit);
          }
        });

    chkAutoResume.setSelected(Lizzie.config.autoResume);
    chkAutoResume.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            Lizzie.config.autoResume = chkAutoResume.isSelected();
            Lizzie.config.uiConfig.put("resume-previous-game", Lizzie.config.autoResume);
          }
        });

    btnDeleteAll.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            SwingUtilities.invokeLater(
                new Runnable() {
                  public void run() {
                    int ret =
                        JOptionPane.showConfirmDialog(
                            Lizzie.frame,
                            resourceBundle.getString("LizzieFrame.saveAndLoad.deleteAllWarining"),
                            resourceBundle.getString("LizzieFrame.warning"),
                            JOptionPane.OK_CANCEL_OPTION);
                    if (ret == JOptionPane.CANCEL_OPTION || ret == -1) {
                      return;
                    }
                    deleteAllTempGame();
                    showTempGamePanel();
                  }
                });
          }
        });

    btnClose.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            hideTempGamePanel(oriShowListPane, OriShowVariationGraph);
            if (Lizzie.config.isScaled) refresh();
          }
        });

    chkZoomImage.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            Lizzie.config.loadASaveZoom = chkZoomImage.isSelected();
            Lizzie.config.uiConfig.put("load-save-zoom", Lizzie.config.loadASaveZoom);
          }
        });
    chkZoomImage.setSelected(Lizzie.config.loadASaveZoom);

    int startPos = Math.max(0, width - 445);
    chkZoomImage.setForeground(Color.WHITE);
    chkZoomImage.setBackground(tempGamePanel.getBackground());
    chkAutoResume.setBackground(tempGamePanel.getBackground());
    chkAutoResume.setForeground(Color.WHITE);
    chkAutoSaveOnExit.setBackground(tempGamePanel.getBackground());
    chkAutoSaveOnExit.setForeground(Color.WHITE);
    btnClose.setMargin(new Insets(0, 0, 0, 0));
    chkZoomImage.setBounds(startPos, 0, 150, 20);
    chkAutoSaveOnExit.setBounds(startPos + 150, 0, 80, 20);
    chkAutoResume.setBounds(startPos + 230, 0, 110, 20);
    btnDeleteAll.setBounds(startPos + 343, 0, 60, 19);
    btnClose.setBounds(Math.min(startPos + 403, width - 40), 0, 40, 19);
    tempGamePanelTop.removeAll();
    tempGamePanelTop.add(btnClose);
    tempGamePanelTop.add(btnDeleteAll);
    tempGamePanelTop.add(chkZoomImage);
    tempGamePanelTop.add(chkAutoResume);
    tempGamePanelTop.add(chkAutoSaveOnExit);

    int height = mainPanel.getHeight() - 5;
    ArrayList<TempGameData> tempGameList = getTempGameList();
    int newIndex = 1;
    int newX = 0;
    int newY = 20;
    int column = width / 310;
    if (column == 0) column = 1;
    for (int i = 0; i < tempGameList.size(); i++) {
      TempGameData data = tempGameList.get(i);
      int x = (i % column) * 310;
      int y = (i / column) * 345 + 20;
      data.x = x;
      data.y = y;
      addTempGameOne(
          data.index,
          x,
          y,
          data.name,
          data.time,
          data.isAutoSave,
          data.curMoveNumer,
          data.moves,
          oriShowListPane,
          OriShowVariationGraph);
      if (i == tempGameList.size() - 1) {
        if (data.isAutoSave) {
          newIndex = 1;
        } else newIndex = data.index + 1;
        newX = ((i + 1) % column) * 310;
        newY = ((i + 1) / column) * 345 + 20;
      }
    }
    addTempGameNew(newIndex, newX, newY, oriShowListPane, OriShowVariationGraph);
    if (height < newY + 345) height = newY + 345;
    //    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    //
    //    addTempGameOne(1, 0, 20, "未命名", df.format(new Date()));
    //    addTempGameOne(2, 310, 20, "未命名", df.format(new Date()));

    tempGamePanel.setPreferredSize(new Dimension(width, height));
    if (tempGamePanelLis != null) tempGamePanel.removeMouseMotionListener(tempGamePanelLis);
    tempGamePanelLis =
        new MouseAdapter() {
          public void mouseMoved(MouseEvent e) {
            if (!Lizzie.config.loadASaveZoom) return;
            int x = e.getX();
            int y = e.getY();
            if (bigBoardLastX == x && bigBoardLastY == y) return;
            bigBoardLastX = x;
            bigBoardLastY = y;
            boolean isMouseOnImage = false;
            for (TempGameData data : tempGameList) {
              if (data.x < x && (data.x + 300) > x)
                if (data.y < y && (data.y + 300) > y) {
                  isMouseOnImage = true;

                  int boardIndex = data.isAutoSave ? data.index : data.index + 20000;
                  if (bigBoardIndex != boardIndex) {
                    if (bigBoardPanel != null) {
                      bigBoardPanel.setVisible(false);
                      isShowingBigBoardPanel = false;
                    }
                  }
                  bigBoardIndex = boardIndex;
                  if (bigBoardPanel == null || !bigBoardPanel.isVisible()) {
                    Runnable runnable2 =
                        new Runnable() {
                          public void run() {
                            try {
                              Thread.sleep(800);
                            } catch (InterruptedException es) {
                              // TODO Auto-generated catch block
                              es.printStackTrace();
                            }
                            if (e.getX() == bigBoardLastX && e.getY() == bigBoardLastY) {
                              showBigBoardImage(
                                  data.isAutoSave,
                                  data.index,
                                  tempGamePanel,
                                  x,
                                  y,
                                  data.curMoveNumer,
                                  data.moves,
                                  oriShowListPane,
                                  OriShowVariationGraph);
                            }
                          }
                        };
                    Thread thread2 = new Thread(runnable2);
                    thread2.start();
                  }
                  break;
                }
            }
            if (!isMouseOnImage) {
              if (bigBoardPanel != null) {
                bigBoardPanel.setVisible(false);
                isShowingBigBoardPanel = false;
              }
            }
          }
        };
    tempGamePanel.addMouseMotionListener(tempGamePanelLis);

    if (tempGamePanelMoveLis != null) tempGamePanel.removeMouseListener(tempGamePanelMoveLis);
    tempGamePanelMoveLis =
        new MouseAdapter() {
          public void mouseClicked(MouseEvent e) {
            int x = e.getX();
            int y = e.getY();
            for (TempGameData data : tempGameList) {
              if (data.x < x && (data.x + 300) > x)
                if (data.y < y && (data.y + 300) > y) {
                  canShowBigBoardImage = false;
                  loadFile(
                      new File(
                          (data.isAutoSave
                                  ? "save" + Utils.pwd + "autoGame"
                                  : "save" + Utils.pwd + "game")
                              + data.index
                              + ".sgf"),
                      true,
                      true);
                  if (!data.moves.equals("")) Lizzie.board.playList(data.moves);
                  else Lizzie.board.goToMoveNumber(data.curMoveNumer);

                  if (Lizzie.leelaz.isPondering()) Lizzie.leelaz.ponder();
                  hideTempGamePanel(oriShowListPane, OriShowVariationGraph);
                  break;
                }
            }
          }
        };

    tempGamePanel.addMouseListener(tempGamePanelMoveLis);
    tempGamePanel.updateUI();
    tempGamePanel.repaint();
  }

  private void showBigBoardImage(
      boolean isAutoSave,
      int index,
      JPanel panel,
      int x,
      int y,
      int moveNumber,
      String moveList,
      boolean oriShowListPane,
      boolean OriShowVariationGraph) {
    if (isShowingBigBoardPanel) return;
    isShowingBigBoardPanel = true;
    if (bigBoardPanel != null) {
      bigBoardPanel.removeAll();
      bigBoardPanel.setVisible(false);
    }
    bigBoardPanel = new JPopupMenu();
    Image img2 = null;
    File file =
        new File(
            (isAutoSave ? "save" + Utils.pwd + "autoGame" : "save" + Utils.pwd + "game")
                + index
                + ".bmp");
    try {
      BufferedImage img = ImageIO.read(file);
      img2 = zoomImage(img, 600, 600);
    } catch (IOException e1) {
      // TODO Auto-generated catch block
      return;
    }
    bigBoardPanel.setSize(600, 600);
    bigBoardPanel.setLayout(null);
    JLabel label = new JLabel();
    label.setIcon(new ImageIcon(img2));
    label.setBounds(0, 0, 600, 600);
    bigBoardPanel.add(label);
    if (bigBoardPanelLis != null) bigBoardPanel.removeMouseListener(bigBoardPanelLis);
    bigBoardPanelLis =
        new MouseAdapter() {
          public void mouseClicked(MouseEvent e) {
            if (e.getX() == 0 && e.getY() == 0) {
              canShowBigBoardImage = false;
              loadFile(
                  new File(
                      (isAutoSave ? "save" + Utils.pwd + "autoGame" : "save" + Utils.pwd + "game")
                          + index
                          + ".sgf"),
                  true,
                  true);
              if (!moveList.equals("")) Lizzie.board.playList(moveList);
              else Lizzie.board.goToMoveNumber(moveNumber);
              if (Lizzie.leelaz.isPondering()) Lizzie.leelaz.ponder();
              hideTempGamePanel(oriShowListPane, OriShowVariationGraph);
              bigBoardPanel.setVisible(false);
            }
          }
        };
    bigBoardPanel.addMouseListener(bigBoardPanelLis);
    try {
      if (panel.isVisible() && canShowBigBoardImage) bigBoardPanel.show(panel, x, y);
    } catch (Exception es) {
    }
  }

  public void drawContDownForHuman(int leftMinuts, int leftSeconds, int byoTimes, int byoSeconds) {
    this.leftMinuts = leftMinuts;
    this.leftSeconds = leftSeconds;
    this.byoTimes = byoTimes;
    this.byoSeconds = byoSeconds;
    if (!Lizzie.config.showWinrateGraph) {
      String byoString =
          ((this.leftMinuts > 0 || this.leftSeconds > 0)
                  ? (resourceBundle.getString("Byoyomi.time")
                      + this.leftMinuts
                      + ":"
                      + this.leftSeconds
                      + " ")
                  : "")
              + (this.byoSeconds >= 0
                  ? (" "
                      + resourceBundle.getString("Byoyomi.byoyomi")
                      + this.byoSeconds
                      + "("
                      + Lizzie.frame.byoTimes
                      + ")")
                  : "");
      menu.byoyomiTime.setText("  " + byoString);
    }
    refresh();
  }

  public void stopTimer() {
    isShowingByoTime = false;
    menu.byoyomiTime.setVisible(false);
    if (timeScheduled != null) {
      timeScheduled.shutdownNow();
      timeScheduled = null;
    }
  }

  private void tryToResetByoTime() {
    if (isShowingByoTime) this.byoSeconds = this.maxByoTimes;
  }

  public void countDownForHuman(int minuts, int seconds, int times) {
    stopTimer();
    timeScheduled = new ScheduledThreadPoolExecutor(1);
    isShowingByoTime = true;
    menu.byoyomiTime.setVisible(true);
    this.leftMinuts = minuts;
    this.leftSeconds = 0;
    this.byoTimes = times;
    this.byoSeconds = seconds > 0 ? seconds : -1;
    this.maxByoTimes = seconds;
    timeScheduled.scheduleAtFixedRate(
        new Runnable() {
          int leftSeconds = 0;
          int leftMinuts = minuts;
          int byoTimes = times;
          // int byoSeconds=seconds;

          boolean shouldStop = false;

          @Override
          public void run() {
            if (playerIsBlack && !Lizzie.board.getHistory().isBlacksTurn()) return;
            if (!playerIsBlack && Lizzie.board.getHistory().isBlacksTurn()) return;
            if (Lizzie.leelaz.isGamePaused) return;
            if (!Lizzie.leelaz.isLoaded()) return;
            if (leftSeconds > 0) {
              leftSeconds--;
            } else if (leftMinuts > 0) {
              leftMinuts--;
              leftSeconds = 59;
            } else if (byoSeconds > 0) {
              if (byoSeconds < 10) {
                Utils.playByoyomi(byoSeconds);
              }
              byoSeconds--;
            } else if (byoTimes > 1) {
              byoTimes--;
              byoSeconds = seconds;
            } else {
              shouldStop = true;
            }
            drawContDownForHuman(leftMinuts, leftSeconds, byoTimes, byoSeconds);

            if (shouldStop) {
              stopTimer();
              if (playerIsBlack)
                Lizzie.board
                    .getHistory()
                    .getGameInfo()
                    .setResult(resourceBundle.getString("Byoyomi.timeOutBlack")); // ("白胜,黑超时");
              else
                Lizzie.board
                    .getHistory()
                    .getGameInfo()
                    .setResult(resourceBundle.getString("Byoyomi.timeOutWhite")); // ("黑胜,白超时");
              Utils.showMsg(Lizzie.board.getHistory().getGameInfo().getResult());
              stopAiPlayingAndPolicy();
            }
          }
        },
        0,
        1,
        TimeUnit.SECONDS);
  }

  public void setMarkupType(boolean isMarkuping, int type) {
    // TODO Auto-generated method stub
    this.isMarkuping = isMarkuping;
    // 0=无 1=字母 2=圈 3=X 4=方块 5=三角
    this.markupType = type;
  }

  public boolean tryToRemoveMarkup(int x, int y) {
    // TODO Auto-generated method stub
    if (isMarkuping) {
      Optional<int[]> boardCoordinates;
      if (extraMode == 3) {
        boardCoordinates = boardRenderer2.convertScreenToCoordinates(x, y);
        if (!boardCoordinates.isPresent())
          boardCoordinates = boardRenderer.convertScreenToCoordinates(x, y);
      } else {
        boardCoordinates = boardRenderer.convertScreenToCoordinates(x, y);
      }
      if (boardCoordinates.isPresent()) {
        int[] coords = boardCoordinates.get();
        BoardData data = Lizzie.board.getHistory().getData();
        data.getProperties()
            .forEach(
                (key, value) -> {
                  if (SGFParser.isListProperty(key)) {
                    String[] labels = value.split(",");
                    for (String label : labels) {
                      String[] moves = label.split(":");
                      int[] move = SGFParser.convertSgfPosToCoord(moves[0]);
                      if (move != null && (move[0] == coords[0] && move[1] == coords[1])) {
                        markupKey = key;
                        markupValue = value;
                      }
                    }
                  }
                });
        String newValue = "";
        String[] labels = markupValue.split(",");
        for (String label : labels) {
          String[] moves = label.split(":");
          int[] move = SGFParser.convertSgfPosToCoord(moves[0]);
          if (move != null && (move[0] != coords[0] || move[1] != coords[1])) {
            newValue += label + ",";
          }
        }
        if (newValue.endsWith(",")) newValue = newValue.substring(0, newValue.length() - 1);
        data.getProperties().replace(markupKey, newValue);
        refresh();
        return true;
      } else return false;
    } else return false;
  }

  public boolean tryToMarkup(int x, int y) {
    // TODO Auto-generated method stub
    if (isMarkuping) {
      Optional<int[]> boardCoordinates;
      if (extraMode == 3) {
        boardCoordinates = boardRenderer2.convertScreenToCoordinates(x, y);
        if (!boardCoordinates.isPresent())
          boardCoordinates = boardRenderer.convertScreenToCoordinates(x, y);
      } else {
        boardCoordinates = boardRenderer.convertScreenToCoordinates(x, y);
      }
      if (boardCoordinates.isPresent()) {
        int[] coords = boardCoordinates.get();
        BoardData data = Lizzie.board.getHistory().getData();
        lastLabel = 'A' - 1;
        hasMarkup = false;
        data.getProperties()
            .forEach(
                (key, value) -> {
                  if (SGFParser.isListProperty(key)) {
                    String[] labels = value.split(",");
                    for (String label : labels) {
                      String[] moves = label.split(":");
                      int[] move = SGFParser.convertSgfPosToCoord(moves[0]);
                      if (move != null && (move[0] == coords[0] && move[1] == coords[1])) {
                        hasMarkup = true;
                        break;
                      }
                      if (markupType == 1) {
                        if ("LB".equals(key) && moves.length > 1) {
                          // Label
                          if (moves[1].charAt(0) > lastLabel) lastLabel = moves[1].charAt(0);
                        }
                      }
                    }
                  }
                });
        if (hasMarkup) {
          tryToRemoveMarkup(x, y);
          return true;
        }
        if (markupType == 1) {
          lastLabel = lastLabel + 1;
          if (lastLabel >= 91 && lastLabel <= 96) lastLabel = 97;
          String value = SGFParser.asCoord(coords) + ":" + ((char) lastLabel);
          data.getProperties().merge("LB", value, (old, val) -> old + "," + val);
        } else if (markupType == 2) {
          String value = SGFParser.asCoord(coords);
          data.getProperties().merge("CR", value, (old, val) -> old + "," + val);
        } else if (markupType == 3) {
          String value = SGFParser.asCoord(coords);
          data.getProperties().merge("MA", value, (old, val) -> old + "," + val);
        } else if (markupType == 4) {
          String value = SGFParser.asCoord(coords);
          data.getProperties().merge("SQ", value, (old, val) -> old + "," + val);
        } else if (markupType == 5) {
          String value = SGFParser.asCoord(coords);
          data.getProperties().merge("TR", value, (old, val) -> old + "," + val);
        }
        refresh();
        return true;
      } else return false;
    } else return false;
  }

  public void destroyAnalysisEngine() {
    if (analysisEngine != null) {
      if (analysisEngine.useJavaSSH) analysisEngine.javaSSH.close();
      else if (analysisEngine.process != null && analysisEngine.process.isAlive()) {
        analysisEngine.isNormalEnd = true;
        analysisEngine.process.destroyForcibly();
      }
    }
  }

  public void flashAnalyzeGameBatch(int firstMove, int lastMove) {
    // TODO Auto-generated method stub
    Lizzie.config.analysisStartMove = firstMove;
    Lizzie.config.analysisEndMove = lastMove;
    isBatchAnalysisMode = true;
    if (analysisTable != null) {
      analysisTable.resetAnalysisMode();
    }
    flashAnalyzeGame(true);
  }

  public void flashAutoAnaSaveAndLoad() {
    if (Lizzie.leelaz.autoAnalysed) SGFParser.appendAiScoreBlunder();
    String name = Lizzie.frame.Batchfiles.get(Lizzie.frame.BatchAnaNum).getName();
    String path = Lizzie.frame.Batchfiles.get(Lizzie.frame.BatchAnaNum).getParent();
    String df = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
    String prefix = name.substring(name.lastIndexOf("."));
    int num = prefix.length();
    String fileOtherName = name.substring(0, name.length() - num);
    String filename =
        path
            + Utils.pwd
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
      double komi = Lizzie.board.getHistory().getGameInfo().getKomi();
      toolbar.loadAutoBatchFile();
      Lizzie.leelaz.komi(komi);
      flashAnalyzeGameBatch(Lizzie.frame.toolbar.firstMove, Lizzie.frame.toolbar.lastMove);
    } else {
      isBatchAna = false;
      isBatchAnalysisMode = false;
      toolbar.chkAnaAutoSave.setEnabled(true);
      //	isSaving = false;
      Batchfiles = new ArrayList<File>();
      BatchAnaNum = 0;
      if (Lizzie.frame.analysisTable != null && Lizzie.frame.analysisTable.frame.isVisible()) {
        Lizzie.frame.analysisTable.refreshTable();
      }
      Utils.showMsg(resourceBundle.getString("Leelaz.batchAutoAnalyzeComplete"));
    }
  }

  public void flashAnalyzeGame(boolean isAllGame) {
    Lizzie.config.analysisRecentIsPartGame = isAllGame;
    if (analysisEngine == null
        || analysisEngine.useJavaSSH && analysisEngine.javaSSHClosed
        || (!analysisEngine.useJavaSSH
            && (analysisEngine.process == null || !analysisEngine.process.isAlive()))) {
      try {
        analysisEngine = new AnalysisEngine(false);
        analysisEngine.sendRequest(
            isAllGame ? -1 : Lizzie.config.analysisStartMove,
            isAllGame ? -1 : Lizzie.config.analysisEndMove);
      } catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    } else {
      analysisEngine.sendRequest(
          isAllGame ? -1 : Lizzie.config.analysisStartMove,
          isAllGame ? -1 : Lizzie.config.analysisEndMove);
    }
  }

  public void flashAnalyzePart() {
    AnalysisPartGame analysisPartGame = new AnalysisPartGame();
    analysisPartGame.setVisible(true);
  }

  public void flashAnalyzeSettings() {
    AnalysisSettings analysisSettings = new AnalysisSettings(false, false);
    analysisSettings.setVisible(true);
  }

  public static void redo(int movesToAdvance) {
    if (Lizzie.frame.isPlayingAgainstLeelaz || Lizzie.frame.isAnaPlayingAgainstLeelaz) {
      return;
    }
    if (Lizzie.frame.boardRenderer.incrementDisplayedBranchLength(movesToAdvance)) {
      Lizzie.frame.refresh();
      return;
    }
    if (extraMode == 2
        && Lizzie.frame.boardRenderer2.incrementDisplayedBranchLength(movesToAdvance)) {
      Lizzie.frame.refresh();
      return;
    }
    if (Lizzie.frame.independentMainBoard != null) {
      if (Lizzie.frame.independentMainBoard.boardRenderer.incrementDisplayedBranchLength(
          movesToAdvance)) {
        Lizzie.frame.refresh();
        return;
      }
    }
    if (!Lizzie.engineManager.isEngineGame && !Lizzie.engineManager.isPreEngineGame) {
      for (int i = 0; i < movesToAdvance; i++) Lizzie.board.nextMove(false);
      Lizzie.board.clearAfterMove();
      Lizzie.frame.refresh();
    }
  }

  public static void redoNoRefresh(int movesToAdvance) {
    if (Lizzie.frame.isPlayingAgainstLeelaz || Lizzie.frame.isAnaPlayingAgainstLeelaz) {
      return;
    }
    if (Lizzie.frame.boardRenderer.incrementDisplayedBranchLength(movesToAdvance)) {
      return;
    }
    if (extraMode == 2
        && Lizzie.frame.boardRenderer2.incrementDisplayedBranchLength(movesToAdvance)) {
      return;
    }
    if (Lizzie.frame.independentMainBoard != null) {
      if (Lizzie.frame.independentMainBoard.boardRenderer.incrementDisplayedBranchLength(
          movesToAdvance)) {
        return;
      }
    }
    if (!Lizzie.engineManager.isEngineGame && !Lizzie.engineManager.isPreEngineGame) {
      for (int i = 0; i < movesToAdvance; i++) Lizzie.board.nextMove(false);
      Lizzie.board.clearAfterMove();
    }
  }

  public static void undo(int movesToAdvance) {
    if (Lizzie.frame.isPlayingAgainstLeelaz || Lizzie.frame.isAnaPlayingAgainstLeelaz) return;
    if (boardRenderer.isShowingBranch()) {
      Lizzie.frame.doBranch(-movesToAdvance);
      Lizzie.frame.refresh();
      return;
    }
    if (extraMode == 2 && boardRenderer2.isShowingBranch()) {
      Lizzie.frame.doBranch(-movesToAdvance);
      Lizzie.frame.refresh();
      return;
    }
    if (Lizzie.frame.independentMainBoard != null) {
      if (Lizzie.frame.independentMainBoard.boardRenderer.isShowingBranch()) {
        Lizzie.frame.independentMainBoard.doBranch(-movesToAdvance);
        Lizzie.frame.refresh();
        return;
      }
    }
    if (!Lizzie.engineManager.isEngineGame && !Lizzie.engineManager.isPreEngineGame) {
      for (int i = 0; i < movesToAdvance; i++) Lizzie.board.previousMove(false);
      Lizzie.board.clearAfterMove();
      Lizzie.frame.refresh();
    }
  }

  public static void undoNoRefresh(int movesToAdvance) {
    if (Lizzie.frame.isPlayingAgainstLeelaz || Lizzie.frame.isAnaPlayingAgainstLeelaz) return;
    if (boardRenderer.isShowingBranch()) {
      Lizzie.frame.doBranch(-movesToAdvance);
      return;
    }
    if (extraMode == 2 && boardRenderer2.isShowingBranch()) {
      Lizzie.frame.doBranch(-movesToAdvance);
      return;
    }
    if (Lizzie.frame.independentMainBoard != null) {
      if (Lizzie.frame.independentMainBoard.boardRenderer.isShowingBranch()) {
        Lizzie.frame.independentMainBoard.doBranch(-movesToAdvance);
        return;
      }
    }
    if (!Lizzie.engineManager.isEngineGame && !Lizzie.engineManager.isPreEngineGame) {
      for (int i = 0; i < movesToAdvance; i++) Lizzie.board.previousMove(false);
      Lizzie.board.clearAfterMove();
    }
  }

  public void moveToMainTrunk() {
    boolean moved = false;
    while (!Lizzie.board.getHistory().getCurrentHistoryNode().isMainTrunk()) {
      if (!moved) {
        moved = true;
      }
      Lizzie.board.previousMove(false);
    }
    if (moved) {
      Lizzie.board.clearAfterMove();
      Lizzie.frame.refresh();
    }
  }

  public void firstMove() {
    boolean moved = false;
    while (Lizzie.board.previousMove(false)) {
      moved = true;
    }
    if (moved) {
      Lizzie.board.clearAfterMove();
      Lizzie.frame.refresh();
    }
  }

  public void lastMove() {
    boolean moved = false;
    while (Lizzie.board.nextMove(false)) {
      if (!moved) {
        moved = true;
      }
    }
    if (moved) {
      Lizzie.board.clearAfterMove();
      Lizzie.frame.refresh();
    }
  }

  public void clearMouseOverWinrateGraph() {
    // TODO Auto-generated method stub
    if (winrateGraph.mouseOverNode != null) {
      winrateGraph.mouseOverNode = null;
      refresh();
    }
  }

  public void setFrameFontSize(int type) {
    // TODO Auto-generated method stub
    switch (type) {
      case 0:
        Config.frameFontSize = 12;
        break;
      case 1:
        Config.frameFontSize = 16;
        break;
      case 2:
        Config.frameFontSize = 20;
        break;
    }
    Lizzie.config.uiConfig.put("frame-font-size", Config.frameFontSize);
    if (!Lizzie.config.isChinese && Config.frameFontSize > 12)
      Utils.showMsg(resourceBundle.getString("menu.setFrameSizeAlart"));
    Utils.showMsg(resourceBundle.getString("menu.setFrameSizeRestart"));
  }

  //  public void processMiddleClickOnWinrateGraph(MouseEvent e) {
  //    // TODO Auto-generated method stub
  //    int x = Utils.zoomOut(e.getX());
  //    int y = Utils.zoomOut(e.getY());
  //    if (grx <= x && x <= grx + grw && gry <= y && y <= gry + grh)
  //      Lizzie.config.toggleLargeWinrate();
  //  }

  public void repaintLaterMaybe() {
    if (Lizzie.config.isScaled) {
      Runnable runnable =
          new Runnable() {
            public void run() {
              try {
                Thread.sleep(50);
              } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
              }
              repaint();
            }
          };
      Thread thread = new Thread(runnable);
      thread.start();
    }
  }

  public void destroyEstimateEngine() {
    // TODO Auto-generated method stub
    if (zen != null) {
      zen.isNormalEnd = true;
      if (zen.useJavaSSH) {
        if (!zen.javaSSHClosed) zen.javaSSH.close();
      } else if (Lizzie.frame.zen.process != null && Lizzie.frame.zen.process.isAlive()) {
        try {
          Lizzie.frame.zen.process.destroy();
        } catch (Exception e) {
        }
      }
    }
  }

  public void openSuggestionInfoCustom(Window owner) {
    // TODO Auto-generated method stub
    SuggestionInfoOrderSettings suggestionInfoOrderSettings =
        new SuggestionInfoOrderSettings(owner);
    suggestionInfoOrderSettings.setVisible(true);
  }

  public void clearMouseOverCoordinate(boolean isIndependBoard) {
    // TODO Auto-generated method stub
    if (isIndependBoard) {
      if (independentMainBoard != null)
        independentMainBoard.mouseOverCoordinate = outOfBoundCoordinate;
    } else {
      mouseOverCoordinate = outOfBoundCoordinate;
    }
  }

  public void insertMove(int[] coords, boolean isBlack) {
    GameInfo gameInfo = Lizzie.board.getHistory().getGameInfo();
    boolean oriPlaySound = Lizzie.config.playSound;
    Lizzie.config.playSound = false;
    Lizzie.board.savelistforeditmode();
    MoveLinkedList listHead =
        Lizzie.board.getMoveLinkedListAfter(Lizzie.board.getHistory().getCurrentHistoryNode());
    if (listHead == null) {
      Lizzie.board.place(coords[0], coords[1], isBlack ? Stone.BLACK : Stone.WHITE);
    } else {
      if (Lizzie.board.getHistory().getCurrentHistoryNode().previous().isPresent())
        Lizzie.board.deleteMoveNoHint();
      else Lizzie.board.deleteMoveNoHintAfter();

      MoveLinkedList move = new MoveLinkedList();
      move.x = coords[0];
      move.y = coords[1];
      move.isBlack = isBlack;
      for (MoveLinkedList sub : listHead.variations) {
        move.variations.add(sub);
        sub.previous = Optional.of(move);
      }
      move.previous = Optional.of(listHead);
      ;
      listHead.variations = new ArrayList<MoveLinkedList>();
      listHead.variations.add(move);

      Lizzie.board.placeLinkedList(listHead, null, false, -1);
      // 返回原点
      Lizzie.board.gotoAnyMoveByCoords(coords);
    }
    Lizzie.config.playSound = oriPlaySound;
    Lizzie.board.getHistory().setGameInfo(gameInfo);
  }

  private boolean tempShowBlack;
  private boolean tempShowWhite;
  public boolean isInTemporaryBoard;

  public boolean allowPlaceStone = true;

  public void startTemporaryBoard() {
    if (isInTemporaryBoard) return;
    isInTemporaryBoard = true;
    tempShowBlack = toolbar.chkShowBlack.isSelected();
    tempShowWhite = toolbar.chkShowWhite.isSelected();
    toolbar.chkShowBlack.setSelected(false);
    toolbar.chkShowWhite.setSelected(false);
    if (Lizzie.config.showDoubleMenu) {
      menu.chkShowBlack.setSelected(false);
      menu.chkShowWhite.setSelected(false);
    }
    boardRenderer.clearAfterMove();
    if (independentMainBoard != null) independentMainBoard.boardRenderer.clearAfterMove();
  }

  public void stopTemporaryBoardMaybe() {
    if (isInTemporaryBoard) stopTemporaryBoard();
  }

  public void stopTemporaryBoard() {
    toolbar.chkShowBlack.setSelected(tempShowBlack);
    toolbar.chkShowWhite.setSelected(tempShowWhite);
    if (Lizzie.config.showDoubleMenu) {
      menu.chkShowBlack.setSelected(tempShowBlack);
      menu.chkShowWhite.setSelected(tempShowWhite);
    }
    isInTemporaryBoard = false;
  }

  public void clearSelectImage() {
    // TODO Auto-generated method stub
    this.boardRenderer.removeSelectedRect();
    if (independentMainBoard != null) independentMainBoard.boardRenderer.removeSelectedRect();
  }

  class BlunderTableCellRenderer extends DefaultTableCellRenderer {
    public Component getTableCellRendererComponent(
        JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
      if (Lizzie.board.convertNameToCoordinates(table.getValueAt(row, 1).toString())[0]
              == Lizzie.frame.clickbadmove[0]
          && Lizzie.board.convertNameToCoordinates(table.getValueAt(row, 1).toString())[1]
              == Lizzie.frame.clickbadmove[1]) {
        setBackground(new Color(238, 221, 130));
      } else setBackground(blunderBackground);
      try {
        double diffWinrate =
            -Float.parseFloat(
                table
                    .getValueAt(row, 2)
                    .toString()
                    .substring(0, table.getValueAt(row, 2).toString().length() - 1));
        if (Lizzie.board.isKataBoard || Lizzie.leelaz.isKatago) {
          double scoreDiff =
              -Float.parseFloat(
                  table
                      .getValueAt(row, 3)
                      .toString()
                      .substring(0, table.getValueAt(row, 3).toString().length() - 1));
          if (column == 3) {
            if (scoreDiff < 0) setForeground(blunderGoodMove);
            else if (scoreDiff >= 3 && scoreDiff <= 5) setForeground(Color.BLUE);
            else if (scoreDiff > 5) setForeground(new Color(220, 0, 0));
            else setForeground(blunderForeground);
          } else if (column == 2) {
            if (diffWinrate < 0) setForeground(blunderGoodMove);
            else if (diffWinrate >= 5 && diffWinrate <= 20) setForeground(Color.BLUE);
            else if (diffWinrate > 20) setForeground(new Color(220, 0, 0));
          } else setForeground(blunderForeground);
        } else {
          if (column == 2) {
            if (diffWinrate < 0) setForeground(blunderGoodMove);
            else if (diffWinrate >= 5 && diffWinrate <= 20) setForeground(Color.BLUE);
            else if (diffWinrate > 20) setForeground(new Color(220, 0, 0));
          } else setForeground(blunderForeground);
        }
        return super.getTableCellRendererComponent(table, value, false, false, row, column);
      } catch (Exception e) {
        return super.getTableCellRendererComponent(table, value, false, false, row, column);
      }
    }
  }

  public void showAnalyzeGenmoveInfo() {
    Discribe lizzieCacheDiscribe = new Discribe();
    lizzieCacheDiscribe.setInfoWide(
        resourceBundle.getString("LizzieFrame.aboutAnalyzeGenmoveInfo"),
        resourceBundle.getString("LizzieFrame.aboutAnalyzeGenmoveInfoTitle"));
  }

  public void leftClickInScoreMode(int x, int y) {
    Optional<int[]> boardCoordinates = boardRenderer.convertScreenToCoordinates(x, y);
    if (boardCoordinates.isPresent()) {
      int[] coords = boardCoordinates.get();
      Lizzie.board.toggleDeadStoneOrEmptyPoint(coords[0], coords[1]);
    }
  }

  public void toggleScoreMode() {
    if (isInScoreMode) endFinalScore();
    else startFinalScore();
  }

  public void startFinalScore() {
    ponderStatusBeforeScore = Lizzie.leelaz.isPondering();
    if (ponderStatusBeforeScore) Lizzie.leelaz.togglePonder();
    isInScoreMode = true;
    Lizzie.board.getGroupInfo();
    clearKataEstimate();
    boardRenderer.removeblock();
    if (independentMainBoard != null) independentMainBoard.boardRenderer.removeblock();
    if (Lizzie.frame.isCounting) {
      Lizzie.frame.clearKataEstimate();
      Lizzie.frame.isCounting = false;
      Lizzie.estimateResults.setVisible(false);
    }
    refresh();
  }

  public void endFinalScore() {
    if (ponderStatusBeforeScore) Lizzie.leelaz.ponder();
    isInScoreMode = false;
    clearScore();
    refresh();
  }

  public void drawScore(GroupInfo boardGroupInfo) {
    // TODO Auto-generated method stub
    if (extraMode == 8 && independentMainBoard != null)
      this.independentMainBoard.boardRenderer.drawScore(boardGroupInfo);
    else boardRenderer.drawScore(boardGroupInfo);
    this.refresh();
  }

  public void clearScore() {
    // TODO Auto-generated method stub
    if (extraMode == 8 && independentMainBoard != null)
      this.independentMainBoard.boardRenderer.clearScore();
    else boardRenderer.clearScore();
    if (Lizzie.board.boardGroupInfo != null
        && Lizzie.board.boardGroupInfo.scoreResult != null
        && Lizzie.board.boardGroupInfo.scoreResult.isVisible())
      Lizzie.board.boardGroupInfo.scoreResult.setVisible(false);
  }

  public void switchToCustomMode(int index) {
    // System.out.println("switch to " + index);
    Lizzie.config.loadCustomLayout(index);
    Lizzie.config.savePanelConfig();
  }

  public void setCustomMode(int index) {
    // System.out.println("set " + index);
    SetCustomMode setCustomMode = new SetCustomMode(index, true);
    setCustomMode.setVisible(true);
  }

  public void visualizedPanelSettings() {
    SetCustomMode setCustomMode = new SetCustomMode(-1, false);
    setCustomMode.setVisible(true);
  }

  public void setVarTreeVisible(boolean visible) {
    if (shouldShowSimpleVariation()) return;
    this.varTreeScrollPane.setVisible(visible);
  }

  public void reRenderTree() {
    if (shouldShowSimpleVariation()) return;
    Lizzie.frame.renderVarTree(
        Lizzie.frame.varTreeScrollPane.getWidth(),
        Lizzie.frame.varTreeScrollPane.getHeight(),
        true,
        false);
  }

  public void setPdaAndWrn(double pda, double wrn) {
    if (pda == 0) {
      Lizzie.config.chkKataEnginePDA = false;
      Lizzie.config.txtKataEnginePDA = "0";
    } else {
      Lizzie.config.chkKataEnginePDA = true;
      Lizzie.config.txtKataEnginePDA = pda + "";
    }
    if (!Lizzie.config.autoLoadKataEngineWRN) {
      if (wrn == 0) {
        Lizzie.config.chkKataEngineWRN = false;
        Lizzie.config.txtKataEngineWRN = "0";
      } else {
        Lizzie.config.chkKataEngineWRN = true;
        Lizzie.config.txtKataEngineWRN = wrn + "";
      }
    } else {
      if (wrn != 0) {
        Lizzie.config.chkKataEngineWRN = true;
        Lizzie.config.txtKataEngineWRN = wrn + "";
      }
    }
    menu.setPdaAndWrn(pda, wrn);
  }

  public void clearWRNforGame(boolean isGenmove) {
    // TODO Auto-generated method stub
    if (isGenmove) {
      try {
        WRNValueBeforeGenmove = Double.parseDouble(menu.txtWRN.getText());
      } catch (NumberFormatException e) {
        WRNValueBeforeGenmove = 0;
      }
      WRNSelectedBeforeGenmove = menu.chkWRN.isSelected();
      menu.chkWRN.setSelected(false);
      menu.txtWRN.setEnabled(false);
      menu.chkWRN.setEnabled(false);
      Lizzie.config.chkKataEngineWRN = false;
    } else {
      if ((Lizzie.engineManager.isPreEngineGame
              || Lizzie.engineManager.isEngineGame
              || isAnaPlayingAgainstLeelaz)
          && Lizzie.config.disableWRNInGame) {
        WRNStatusBeforeGame = Lizzie.config.chkKataEngineWRN || Lizzie.config.autoLoadKataEngineWRN;
        autoWRNStatusBeforeGame = Lizzie.config.autoLoadKataEngineWRN;
        menu.chkWRN.setSelected(false);
        menu.txtWRN.setEnabled(false);
        Lizzie.config.chkKataEngineWRN = false;
        if (isAnaPlayingAgainstLeelaz) {
          if (Lizzie.leelaz.isKatago) {
            Lizzie.leelaz.wrn = 0;
            Lizzie.leelaz.sendCommand("kata-set-param analysisWideRootNoise 0");
          }
        } else {
          {
            if (Lizzie.engineManager.engineList.get(
                    Lizzie.engineManager.engineGameInfo.firstEngineIndex)
                .isKatago) {
              Lizzie.engineManager
                  .engineList
                  .get(Lizzie.engineManager.engineGameInfo.firstEngineIndex)
                  .sendCommand("kata-set-param analysisWideRootNoise 0");
              Lizzie.engineManager.engineList.get(
                          Lizzie.engineManager.engineGameInfo.firstEngineIndex)
                      .wrn =
                  0;
            }
            if (Lizzie.engineManager.engineList.get(
                    Lizzie.engineManager.engineGameInfo.secondEngineIndex)
                .isKatago) {
              Lizzie.engineManager
                  .engineList
                  .get(Lizzie.engineManager.engineGameInfo.secondEngineIndex)
                  .sendCommand("kata-set-param analysisWideRootNoise 0");
              Lizzie.engineManager.engineList.get(
                          Lizzie.engineManager.engineGameInfo.secondEngineIndex)
                      .wrn =
                  0;
            }
          }
        }
      }
    }
  }

  public void restoreWRN(boolean isGenmove) {
    // TODO Auto-generated method stub
    if (isGenmove) {
      menu.chkWRN.setEnabled(true);
      menu.txtWRN.setEnabled(true);
      menu.chkWRN.setSelected(WRNSelectedBeforeGenmove);
      menu.setWrnText(WRNValueBeforeGenmove);
      Lizzie.config.chkKataEngineWRN = WRNSelectedBeforeGenmove;
      Lizzie.leelaz.sendCommand("kata-set-param analysisWideRootNoise " + WRNValueBeforeGenmove);
    } else if (WRNStatusBeforeGame) {
      try {
        double wrn = Double.parseDouble(Lizzie.frame.menu.txtWRN.getText());
        if (Lizzie.leelaz.isKatago) {
          Lizzie.leelaz.sendCommand("kata-set-param analysisWideRootNoise " + wrn);
          Lizzie.leelaz.wrn = wrn;
        }
        menu.setWrnText(wrn);
        Lizzie.config.txtKataEngineWRN = wrn + "";
      } catch (NumberFormatException e) {
        return;
      }
      menu.chkWRN.setSelected(true);
      menu.txtWRN.setEnabled(true);
      Lizzie.config.chkKataEngineWRN = true;
      Lizzie.config.autoLoadKataEngineWRN = autoWRNStatusBeforeGame;
      Lizzie.config.uiConfig.put("autoload-kata-engine-wrn", Lizzie.config.autoLoadKataEngineWRN);
    }
  }

  public void refreshCurrentMove() {
    // TODO Auto-generated method stub
    Lizzie.board.clearbestmoves();
    Lizzie.leelaz.sendCommand("clear_cache");
    if (Lizzie.leelaz.isPondering()) Lizzie.leelaz.ponder();
    refresh();
  }

  public String getPlayerName(boolean isBlack, int length) {
    // TODO Auto-generated method stub
    String player =
        isBlack
            ? Lizzie.board.getHistory().getGameInfo().getPlayerBlack()
            : Lizzie.board.getHistory().getGameInfo().getPlayerWhite();
    if (player.length() > length) player = player.substring(0, 11);
    if (player.equals(""))
      player =
          isBlack
              ? resourceBundle.getString("SGFParse.black")
              : resourceBundle.getString("SGFParse.white");
    return player;
  }
}
