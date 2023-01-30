package featurecat.lizzie.gui;

import static java.awt.image.BufferedImage.TYPE_INT_ARGB;
import static java.awt.image.BufferedImage.TYPE_INT_RGB;
import static java.lang.Math.max;
import static java.lang.Math.min;

import com.jhlabs.image.GaussianFilter;
import featurecat.lizzie.Config;
import featurecat.lizzie.ExtraMode;
import featurecat.lizzie.Lizzie;
import featurecat.lizzie.analysis.AnalysisEngine;
import featurecat.lizzie.analysis.CaptureTsumeGo;
import featurecat.lizzie.analysis.ContributeEngine;
import featurecat.lizzie.analysis.EngineManager;
import featurecat.lizzie.analysis.GameInfo;
import featurecat.lizzie.analysis.KataEstimate;
import featurecat.lizzie.analysis.Leelaz;
import featurecat.lizzie.analysis.MoveData;
import featurecat.lizzie.analysis.ReadBoard;
import featurecat.lizzie.rules.Board;
import featurecat.lizzie.rules.BoardData;
import featurecat.lizzie.rules.BoardHistoryNode;
import featurecat.lizzie.rules.EngineCountDown;
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
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.StyleSheet;
import me.friwi.jcefmaven.CefInitializationException;
import me.friwi.jcefmaven.UnsupportedPlatformException;
import org.jdesktop.swingx.util.OS;
import org.json.JSONArray;
import org.json.JSONObject;

/** The window used to display the game. */
public class LizzieFrame extends JFrame {
  private String[] commands = {
    Lizzie.resourceBundle.getString("LizzieFrame.commands.keySpace"),
    Lizzie.resourceBundle.getString("LizzieFrame.commands.keyN"),
    Lizzie.resourceBundle.getString("LizzieFrame.commands.keyEnter"),
    // "Enter(回车)|与引擎继续对弈",
    Lizzie.resourceBundle.getString("LizzieFrame.commands.mouseWheelScroll"),
    Lizzie.resourceBundle.getString("LizzieFrame.commands.keyComma"),
    // ",(逗号)或滚轮单击|落最佳一手,如果鼠标指向变化图则落子到变化图结束",
    Lizzie.resourceBundle.getString("LizzieFrame.commands.rightClick"),
    Lizzie.resourceBundle.getString("LizzieFrame.commands.keyA"),
    Lizzie.resourceBundle.getString("LizzieFrame.commands.keyG"),
    Lizzie.resourceBundle.getString("LizzieFrame.commands.keyR"),
    // "滚轮单击|落子到当前变化图结束",
    // "滚轮长按或R|快速回放鼠标指向的变化图",
    Lizzie.resourceBundle.getString("LizzieFrame.commands.mousePointSub"),
    // "鼠标指向小棋盘|左键/右键点击可切换小棋盘变化图,滚轮可控制变化图前进后退",
    Lizzie.resourceBundle.getString("LizzieFrame.commands.keyY"),
    // "B|显示超级鹰眼",
    Lizzie.resourceBundle.getString("LizzieFrame.commands.keyU"),
    // "U|显示AI选点列表",
    Lizzie.resourceBundle.getString("LizzieFrame.commands.keyI"),
    // "I|编辑棋局信息",
    Lizzie.resourceBundle.getString("LizzieFrame.commands.keySlash"),
    Lizzie.resourceBundle.getString("LizzieFrame.commands.keyB"),
    //  "T|返回主分支",
    Lizzie.resourceBundle.getString("LizzieFrame.commands.keyV"),
    // "V|试下",
    Lizzie.resourceBundle.getString("LizzieFrame.commands.keyF"),
    Lizzie.resourceBundle.getString("LizzieFrame.commands.keyZ"),
    Lizzie.resourceBundle.getString("LizzieFrame.commands.keyShiftF"),
    // "F|关闭/显示AI选点",
    Lizzie.resourceBundle.getString("LizzieFrame.commands.keyHandY"),
    // "H或Y|显示纯网络分析结果",
    // Lizzie.resourceBundle.getString("LizzieFrame.commands.keyI"),
    Lizzie.resourceBundle.getString("LizzieFrame.commands.key123456789"),
    Lizzie.resourceBundle.getString("LizzieFrame.commands.keyUpDownArrow"),
    // Lizzie.resourceBundle.getString("LizzieFrame.commands.keyDownArrow"),
    Lizzie.resourceBundle.getString("LizzieFrame.commands.keyC"),
    Lizzie.resourceBundle.getString("LizzieFrame.commands.keyP"),
    Lizzie.resourceBundle.getString("LizzieFrame.commands.keyM"),
    Lizzie.resourceBundle.getString("LizzieFrame.commands.keyAltC"),
    Lizzie.resourceBundle.getString("LizzieFrame.commands.keyAltV"),
    Lizzie.resourceBundle.getString("LizzieFrame.commands.keyJ"),
    // Lizzie.resourceBundle.getString("LizzieFrame.commands.keyV"),
    Lizzie.resourceBundle.getString("LizzieFrame.commands.keyW"),
    Lizzie.resourceBundle.getString("LizzieFrame.commands.keyCtrlW"),
    Lizzie.resourceBundle.getString("LizzieFrame.commands.keyShiftG"),
    Lizzie.resourceBundle.getString("LizzieFrame.commands.keyAltZ"),
    Lizzie.resourceBundle.getString("LizzieFrame.commands.keyBracket"),
    Lizzie.resourceBundle.getString("LizzieFrame.commands.keyCtrlT"),
    Lizzie.resourceBundle.getString("LizzieFrame.commands.keyHome"),
    Lizzie.resourceBundle.getString("LizzieFrame.commands.keyEnd"),
    Lizzie.resourceBundle.getString("LizzieFrame.commands.keyControl"),
    Lizzie.resourceBundle.getString("LizzieFrame.commands.keyDelete"),
    Lizzie.resourceBundle.getString("LizzieFrame.commands.keyE"),
  };
  private String DEFAULT_TITLE = Lizzie.resourceBundle.getString("LizzieFrame.title");
  private JLayeredPane basePanel;
  public static BoardRenderer boardRenderer;
  public static BoardRenderer boardRenderer2;
  public static SubBoardRenderer subBoardRenderer;
  public SubBoardRenderer subBoardRenderer2;
  public SubBoardRenderer subBoardRenderer3;
  public SubBoardRenderer subBoardRenderer4;
  public EstimateResults estimateResults;
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
  // private long lastAutosaveTime = System.currentTimeMillis();
  private int autosaveTime = 0;
  public boolean isReplayVariation = false;
  public RightClickMenu RightClickMenu;
  public RightClickMenu2 RightClickMenu2;
  //  private int boardPos = 0;
  // public String komi = "7.5";
  double winRate;
  double score;
  double scoreLead;
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
  private JLabel commentTextArea;
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
  // private ScheduledExecutorService updateTitleSchedual = Executors.newScheduledThreadPool(1);
  private String visitsString = "";
  private long visitsStringTime;
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
  javax.swing.Timer tableTimer;
  // javax.swing.Timer blunderTableTimer;
  private TableModel listDataModel;
  private boolean scoreColumnIsHidden = false;
  private boolean scoreIsHiddenInBlunderTable = false;

  public int selectedorder = -1;
  public int clickOrder = -1;
  public int currentRow = -1;
  // public JPanel statusPanel;
  //  public int mainPanleX;
  //  public int mainPanleY;
  public int toolbarHeight = 26;
  public int topPanelHeight = Config.menuHeight;
  boolean isSmallCap = false;
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
  private boolean refreshWinratePane = false;

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

  // public static int extraMode = Lizzie.config.extraMode; // 1=四方图2=双引擎3=思考 8=浮动棋盘模式

  public boolean selectForceAllow = true;

  public boolean isTrying = false;
  ArrayList<Movelist> tryMoveList;
  String tryString;
  String titleBeforeTrying;
  public BrowserFrame browserFrame;
  public BrowserInitializing browserInitializing;
  //  JFrame frame;
  //  ArrayList<String> urlList;
  //  int urlIndex;
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
  public boolean redrawTree = false;
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
  private int lastNumLabel;
  private boolean hasMarkup;
  private String markupKey;
  private String markupValue;
  public ArrayList<String> priorityMoveCoords = new ArrayList<String>();

  public AnalysisEngine analysisEngine;
  private boolean redrawWinratePaneOnly = false;
  public boolean mouseOverChanged = false;
  public boolean isAutoReplying = false;
  public boolean isBatchAnalysisMode = false;
  // int testFontSize = 12;
  private Color blunderBackground = new Color(225, 225, 225);
  private Color blunderForeground = Color.BLACK;
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
  public FoxKifuDownload foxKifuDownload;
  public int noneMaxX, noneMaxY, noneMaxWidth, noneMaxHeight;

  private boolean tempShowBlack;
  private boolean tempShowWhite;
  public boolean isInTemporaryBoard;

  public boolean allowPlaceStone = true;
  private Process processClockHelper;

  public ContributeEngine contributeEngine;
  public boolean isContributing = false;
  public ContributeView contributeView;
  public boolean isShowingContributeGame = false;

  private TsumeGoFrame tsumeGoFrame;
  private CaptureTsumeGoFrame captureTsumeGoFrame;

  /** Creates a window */
  public LizzieFrame() {
    setTitle(DEFAULT_TITLE);
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
    openInVisibleFrame();
    // MenuTest menu = new MenuTest();
    // add(menu);
    // this.setJMenuBar(menu);
    // this.setVisible(true);
    this.setAlwaysOnTop(Lizzie.config.mainsalwaysontop);
    if (Lizzie.config.extraMode == ExtraMode.Float_Board) setMinimumSize(new Dimension(0, 0));
    else setMinimumSize(new Dimension(520, 400));
    if (Lizzie.config.isFourSubMode()) {
      subBoardRenderer2 = new SubBoardRenderer(false);
      subBoardRenderer3 = new SubBoardRenderer(false);
      subBoardRenderer4 = new SubBoardRenderer(false);
      subBoardRenderer2.setOrder(1);
      subBoardRenderer3.setOrder(2);
      subBoardRenderer4.setOrder(3);
      subBoardRenderer.showHeat = false;
      subBoardRenderer.showHeatAfterCalc = false;
    }
    if (Lizzie.config.isThinkingMode()) {
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
          public void paintComponent(Graphics g) {
            Utils.ajustScale(g);
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

    tempGamePanelAll.add(tempGamePanelTop, new Integer(2));
    tempGamePanelAll.add(tempGameScrollPanel, new Integer(1));

    varTreePane =
        new JPanel() {
          @Override
          public void paintComponent(Graphics g) {
            if (cachedVarImage2 != null && Lizzie.config.showVariationGraph) {
              if (Lizzie.isMultiScreen) {
                final Graphics2D g0 = (Graphics2D) g;
                final AffineTransform t = g0.getTransform();
                final double scaling = t.getScaleX();
                if (scaling > 1) {
                  Graphics2D g1 = (Graphics2D) g;
                  g1.scale(1.0 / scaling, 1.0 / scaling);
                  g1.drawImage(cachedVarImage2, -1, -1, null);
                } else {
                  g.drawImage(cachedVarImage2, 0, 0, null);
                }
              } else {
                if (Config.isScaled) {
                  Graphics2D g1 = (Graphics2D) g;
                  g1.scale(1.0 / Lizzie.javaScaleFactor, 1.0 / Lizzie.javaScaleFactor);
                  g1.drawImage(cachedVarImage2, -1, -1, null);
                } else {
                  g.drawImage(cachedVarImage2, 0, 0, null);
                }
              }
            }
          }
        };
    varTreePane.setOpaque(false);
    varTreePane.setFocusable(false);
    toolbar.setFocusable(false);
    menu.setFocusable(false);
    varTreeScrollPane = new JScrollPane(varTreePane);
    varTreeScrollPane.getViewport().setOpaque(false);
    varTreeScrollPane.setOpaque(false);
    // varTreeScrollPane.setBackground(Color.BLACK);
    varTreeScrollPane.setBorder(BorderFactory.createEmptyBorder());
    varTreeScrollPane.setFocusable(false);
    varTreeScrollPane.getHorizontalScrollBar().setFocusable(false);
    varTreeScrollPane.getVerticalScrollBar().setUI(new DemoScrollBarUI());
    varTreeScrollPane.getHorizontalScrollBar().setUI(new DemoScrollBarUI());
    varTreeScrollPane.getVerticalScrollBar().setUnitIncrement(16);
    varTreeScrollPane.getHorizontalScrollBar().setUnitIncrement(16);
    varTreeScrollPane.setVisible(Lizzie.config.showVariationGraph);
    //    varTreeScrollPane
    //        .getVerticalScrollBar()
    //        .addAdjustmentListener(
    //            new AdjustmentListener() {
    //              @Override
    //              public void adjustmentValueChanged(AdjustmentEvent e) {
    //                // TODO Auto-generated method stub
    //            	  varTreeScrollPane.repaint();
    //              }
    //            });
    // varTreeScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
    // varTreeScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
    commentEditTextPane = new JIMSendTextPane(true);
    commentEditTextPane.setBorder(BorderFactory.createEmptyBorder());
    commentEditTextPane.setBackground(Color.LIGHT_GRAY);
    commentEditTextPane.setForeground(Color.BLACK);
    commentEditPane = new JScrollPane(commentEditTextPane);
    commentEditPane.setBorder(BorderFactory.createEmptyBorder());
    commentEditPane.getVerticalScrollBar().setUI(new DemoScrollBarUI());
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
              if (!EngineManager.isEngineGame)
                variationTree.onClicked(Utils.zoomOut(e.getX()), Utils.zoomOut(e.getY()));
              renderVarTree(0, 0, false, false);
            }
            setCommentEditable(false);
          }
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

    listTable
        .getTableHeader()
        .setFont(new Font(Config.sysDefaultFontName, Font.PLAIN, Config.frameFontSize));
    listTable.setRowHeight(Config.menuHeight - 4);
    listTable.getTableHeader().setReorderingAllowed(false);
    listTable.setFont(new Font(Config.sysDefaultFontName, Font.PLAIN, Config.frameFontSize));
    DefaultTableCellRenderer cellRenderer = new DefaultTableCellRenderer();
    DefaultTableCellRenderer cellRenderer2 = new DefaultTableCellRenderer();
    cellRenderer.setBackground(new Color(208, 208, 208));
    cellRenderer2.setBackground(new Color(178, 178, 178));
    cellRenderer.setHorizontalAlignment(SwingConstants.CENTER);
    cellRenderer2.setHorizontalAlignment(SwingConstants.CENTER);
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
    boolean hasSetBounds = false;
    if (persisted) {
      if (Lizzie.config.persistedUi.optJSONArray("main-window-position") != null
          && Lizzie.config.persistedUi.optJSONArray("main-window-position").length() == 4) {
        JSONArray pos = Lizzie.config.persistedUi.getJSONArray("main-window-position");
        this.setBounds(pos.getInt(0), pos.getInt(1), pos.getInt(2), pos.getInt(3));
        Dimension screensize = Toolkit.getDefaultToolkit().getScreenSize();
        int width = (int) screensize.getWidth();
        int height = (int) screensize.getHeight();
        if (pos.getInt(0) >= width || pos.getInt(1) >= height) this.setLocation(0, 0);
        hasSetBounds = true;
      }
      if (Lizzie.config.persistedUi.getBoolean("window-maximized"))
        setExtendedState(Frame.MAXIMIZED_BOTH);
      if (Lizzie.config.persistedUi.optJSONArray("winrate-graph") != null
          && Lizzie.config.persistedUi.optJSONArray("winrate-graph").length() == 1) {
        JSONArray winrateG = Lizzie.config.persistedUi.getJSONArray("winrate-graph");
        winrateGraph.mode = winrateG.getInt(0);
      }
      this.BoardPositionProportion =
          Lizzie.config.persistedUi.optInt("board-postion-propotion", this.BoardPositionProportion);

      if (Lizzie.config.persistedUi.optJSONArray("main-window-other") != null
          && Lizzie.config.persistedUi.optJSONArray("main-window-other").length() == 5) {
        JSONArray value = Lizzie.config.persistedUi.getJSONArray("main-window-other");
        this.toolbarHeight = value.getInt(0);
        if (toolbarHeight > 26 && !Lizzie.config.isChinese) toolbarHeight = 26;
        this.bowserX = value.getInt(1);
        this.bowserY = value.getInt(2);
        this.bowserWidth = value.getInt(3);
        this.bowserHeight = value.getInt(4);
      }

      if (Lizzie.config.persistedUi.optJSONArray("main-window-list") != null
          && Lizzie.config.persistedUi.optJSONArray("main-window-list").length() == 5) {
        JSONArray value = Lizzie.config.persistedUi.getJSONArray("main-window-list");
        listTable.getColumnModel().getColumn(0).setPreferredWidth(value.getInt(0));
        listTable.getColumnModel().getColumn(2).setPreferredWidth(value.getInt(1));
        listTable.getColumnModel().getColumn(3).setPreferredWidth(value.getInt(2));
        listTable.getColumnModel().getColumn(4).setPreferredWidth(value.getInt(3));
        listTableColum5Width = value.getInt(4);
        listTable.getColumnModel().getColumn(5).setPreferredWidth(listTableColum5Width);
      }

      if (Lizzie.config.persistedUi.optJSONArray("main-window-blunder") != null
          && Lizzie.config.persistedUi.optJSONArray("main-window-blunder").length() == 3) {
        JSONArray value = Lizzie.config.persistedUi.getJSONArray("main-window-blunder");
        blunderTableColum0Width = value.getInt(0);
        blunderTableColum2Width = value.getInt(1);
        blunderTableColum3Width = value.getInt(2);
      }
    }
    if (!hasSetBounds) {
      setSize(1065, 700);
      setLocationRelativeTo(null); // Start centered, needs to be called *after* setSize...
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
          public void mouseClicked(MouseEvent e) {
            setCommentEditable(false);
            int row = listTable.rowAtPoint(e.getPoint());
            int col = listTable.columnAtPoint(e.getPoint());
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
          }
        });

    tableTimer =
        new javax.swing.Timer(
            100,
            new ActionListener() {
              public void actionPerformed(ActionEvent evt) {
                if (listTable.isVisible()) {
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
                  blunderTabelBlack.revalidate();
                  blunderTabelWhite.revalidate();
                }
              }
            });
    tableTimer.start();
    setJMenuBar(menu);
    if (Lizzie.config.isDoubleEngineMode()) {
      boardRenderer2 = new BoardRenderer(false);
      boardRenderer2.setOrder(1);
    } else {
      LizzieFrame.menu.setEngineMenuone2status(false);
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
                boolean ponder = Lizzie.leelaz.isPondering() || !Lizzie.leelaz.isLoaded;
                File file = new File(filepath);
                File files[] = new File[1];
                files[0] = file;
                loadFile(file, true, true);
                curFile = file;
                if (Lizzie.frame.analysisTable != null
                    && Lizzie.frame.analysisTable.frame.isVisible()) {
                  Lizzie.frame.analysisTable.refreshTable();
                }
                if (ponder) {
                  Lizzie.leelaz.ponder();
                }
                refresh();
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
                StartAnaDialog newgame = new StartAnaDialog(false, Lizzie.frame);
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
                : commentPaneFontSize > 0 ? commentPaneFontSize : Config.frameFontSize)
            + "}";
    htmlStyle.addRule(style);
    commentTextPane = new JPaintTextPane();
    commentTextPane.setBorder(BorderFactory.createEmptyBorder());
    // commentTextPane.setOpaque(false);
    commentTextPane.setEditorKit(htmlKit);
    commentTextPane.setDocument(htmlDoc);
    commentTextPane.setEditable(false);
    commentTextPane.setForeground(Lizzie.config.commentFontColor);
    commentTextPane.setBackground(Lizzie.config.commentBackgroundColor);

    commentTextArea = new JLabel();
    commentTextArea.setHorizontalAlignment(SwingConstants.LEFT);
    commentTextArea.setVerticalAlignment(SwingConstants.TOP);
    //    DefaultCaret caret = (DefaultCaret) commentTextArea.getCaret();
    //    caret.setUpdatePolicy(DefaultCaret.NEVER_UPDATE);
    //  DefaultCaret caret2 = (DefaultCaret) commentTextPane.getCaret();
    //  caret2.setUpdatePolicy(DefaultCaret.NEVER_UPDATE);
    // commentTextArea.setEditable(false);
    commentTextArea.setFont(
        new Font(
            Lizzie.config.uiFontName,
            Font.PLAIN,
            Lizzie.config.commentFontSize > 0
                ? Lizzie.config.commentFontSize
                : Config.frameFontSize));
    commentTextArea.setBorder(BorderFactory.createEmptyBorder());
    commentTextArea.setOpaque(false);
    commentTextArea.setForeground(Lizzie.config.commentFontColor);
    commentTextArea.setBackground(Lizzie.config.commentBackgroundColor);
    //   commentTextArea.setLineWrap(true);

    commentScrollPane = new JScrollPane();
    commentScrollPane.setBackground(Lizzie.config.commentBackgroundColor);
    commentScrollPane.setOpaque(false);
    commentScrollPane.getViewport().setOpaque(false);
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

    JPopupMenu exportBlunderBlack = new JPopupMenu();
    final JMenuItem exportMenuBlunderBlack =
        new JFontMenuItem(Lizzie.resourceBundle.getString("JTabel.export"));
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
                      chooser.getCurrentDirectory() + File.separator + fname + ".xls");
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
                int[] coords =
                    Board.convertNameToCoordinates(blunderTabelBlack.getValueAt(row, 1).toString());
                Lizzie.board.goToMoveNumber(movenumber - 1);
                Lizzie.frame.clickbadmove = coords;
                Lizzie.frame.repaint();
              } catch (Exception ex) {
                ex.printStackTrace();
              }
            }
          }
        });

    JPopupMenu exportBlunderWhite = new JPopupMenu();
    final JMenuItem exportMenuBlunderWhite =
        new JFontMenuItem(Lizzie.resourceBundle.getString("JTabel.export"));
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
                      chooser.getCurrentDirectory() + File.separator + fname + ".xls");
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
                int[] coords =
                    Board.convertNameToCoordinates(blunderTabelWhite.getValueAt(row, 1).toString());
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
    commentScrollPane.setViewportView(commentTextArea);
    // commentScrollPane.getViewport().setOpaque(false);
    commentScrollPane.setVerticalScrollBarPolicy(
        javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
    commentScrollPane.getVerticalScrollBar().setUnitIncrement(16);
    commentScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
    commentScrollPane.getVerticalScrollBar().setUI(new DemoScrollBarUI());
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
            boolean notPondering =
                Lizzie.leelaz == null || EngineManager.isEmpty || !Lizzie.leelaz.isPondering();
            try {
              autosaveMaybe();
              updateMoveList(notPondering);
            } catch (Exception e) {
              e.printStackTrace();
            }
            if (!isDrawVisitsInTitle) {
              visitsString = "";
              updateTitle();
              return;
            }
            if (notPondering) {
              updateTitle();
              return;
            }
            try {
              int totalPlayouts =
                  Lizzie.board.getHistory().getCurrentHistoryNode().getData().getPlayouts();
              int tempCount = getLastVisitsCount(visitsCount);
              if (tempCount >= 0) {
                long speed = (totalPlayouts - lastPlayouts) / tempCount;
                if (speed >= 0) {
                  visitsString =
                      String.format(
                          " %d " + Lizzie.resourceBundle.getString("LizzieFrame.speedUnit"), speed);
                  visitsStringTime = System.currentTimeMillis();
                }
              } else if (System.currentTimeMillis() - visitsStringTime > 5000)
                visitsString = " - " + Lizzie.resourceBundle.getString("LizzieFrame.speedUnit");
              visitsCount++;
              if (visitsCount > 3) visitsCount = 0;
              if (totalPlayouts > 0) {
                visitsTemp[visitsCount].node = Lizzie.board.getHistory().getCurrentHistoryNode();
                visitsTemp[visitsCount].Playouts = totalPlayouts;
              }
            } catch (Exception e) {
              e.printStackTrace();
            }
            updateTitle();
          }
        },
        1,
        1,
        TimeUnit.SECONDS);

    //    updateTitleSchedual.scheduleAtFixedRate(
    //        new Runnable() {
    //          @Override
    //          public void run() {
    //            updateTitle();
    //          }
    //        },
    //        1000,
    //        300,
    //        TimeUnit.MILLISECONDS);
    mainPanel.addMouseMotionListener(input);
    toolbar.addMouseWheelListener(input);
    addInput(false);
    basePanel = new JLayeredPane();
    if (Lizzie.config.usePureBackground) {
      basePanel.setBackground(Lizzie.config.pureBackgroundColor);
    } else basePanel.setBackground(Color.GRAY);
    getContentPane().add(basePanel);
    basePanel.setLayout(null);
    basePanel.add(commentBlunderControlPane, new Integer(10));
    basePanel.add(tempGamePanelAll, new Integer(9));
    basePanel.add(varTreeScrollPane, new Integer(8));
    basePanel.add(listScrollpane, new Integer(7));
    basePanel.add(blunderContentPane, new Integer(6));
    basePanel.add(commentEditPane, new Integer(5));
    basePanel.add(commentScrollPane, new Integer(4));
    basePanel.add(topPanel, new Integer(3));
    basePanel.add(toolbar, new Integer(2));
    basePanel.add(mainPanel, new Integer(1));
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
                ? Lizzie.resourceBundle.getString("BlunderTabel.black")
                : Lizzie.resourceBundle.getString("BlunderTabel.white");
          case 1:
            return Lizzie.resourceBundle.getString("BlunderTabel.coords");
          case 2:
            return Lizzie.resourceBundle.getString("BlunderTabel.winRate");
          case 3:
            return Lizzie.resourceBundle.getString("BlunderTabel.score");
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
        if (data2.size() > row) {
          NodeInfo data = data2.get(row);
          if (Lizzie.board.isPkBoard) {
            switch (col) {
              case 0:
                return data.moveNum;
              case 1:
                return Board.convertCoordinatesToName(data.coords[0], data.coords[1]);
              case 2:
                return (data.diffWinrate < 0 ? "+" : "-")
                    + String.format(Locale.ENGLISH, "%.2f", Math.abs(data.diffWinrate));
              case 3:
                return (data.scoreMeanDiff < 0 ? "+" : "-")
                    + String.format(Locale.ENGLISH, "%.2f", Math.abs(data.scoreMeanDiff));
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
                    + String.format(Locale.ENGLISH, "%.2f", Math.abs(data.diffWinrate));

              case 3:
                return (data.scoreMeanDiff > 0 ? "+" : "-")
                    + String.format(Locale.ENGLISH, "%.2f", Math.abs(data.scoreMeanDiff));
              default:
                return "";
            }
          }
        } else return "";
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
    table
        .getTableHeader()
        .setFont(new Font(Config.sysDefaultFontName, Font.PLAIN, Config.frameFontSize));
    table.setRowHeight(Config.menuHeight - 4);
    table.setFont(new Font(Config.sysDefaultFontName, Font.PLAIN, Config.frameFontSize));

    //    table.getTableHeader().setBackground(new Color(51, 102, 255));
    ////    ((DefaultTableCellRenderer) table.getTableHeader().getDefaultRenderer())
    ////        .setHorizontalAlignment(JLabel.CENTER);

    DefaultTableCellRenderer cellRenderer = new DefaultTableCellRenderer();
    cellRenderer.setBackground(Color.LIGHT_GRAY);
    DefaultTableCellRenderer cellRenderer2 = new DefaultTableCellRenderer();
    cellRenderer2.setBackground(new Color(158, 158, 158));
    cellRenderer.setHorizontalAlignment(SwingConstants.CENTER);
    cellRenderer2.setHorizontalAlignment(SwingConstants.CENTER);
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
    JCheckBox chkComment = new JCheckBox(Lizzie.resourceBundle.getString("LizzieFrame.chkComment"));
    chkComment.setSelected(!Lizzie.config.isShowingBlunderTabel);
    JCheckBox chkBlunder = new JCheckBox(Lizzie.resourceBundle.getString("LizzieFrame.chkBlunder"));
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
      JCheckBox chkOnlyAfter =
          new JCheckBox(Lizzie.resourceBundle.getString("LizzieFrame.chkOnlyAfter"));
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
      JButton setThreshold =
          new JButton(Lizzie.resourceBundle.getString("LizzieFrame.setThreshold"));
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

    JButton close = new JButton(Lizzie.resourceBundle.getString("LizzieFrame.close"));
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
                        Lizzie.resourceBundle.getString("LizzieFrame.confirm"),
                        Lizzie.resourceBundle.getString("LizzieFrame.cancel"),
                        Lizzie.resourceBundle.getString("LizzieFrame.noNoticeAgain")
                      };
                      int response =
                          JOptionPane.showOptionDialog(
                              Lizzie.frame,
                              Lizzie.resourceBundle.getString("LizzieFrame.closeCommentBar"),
                              Lizzie.resourceBundle.getString("LizzieFrame.closeCommentBarTitle"),
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
            refreshWinratePane = true;
            reSetLoc();
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

  public void openOnlineDialog() {
    if (onlineDialog == null) {
      onlineDialog = new OnlineDialog(this);
      onlineDialog.setVisible(true);
    } else {
      try {
        onlineDialog.stopSync();
        onlineDialog.paste();
        OnlineDialog.fromBrowser = false;
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
    Utils.changeFontRecursive(configDialog2, Config.sysDefaultFontName);
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
      boardRenderer.startNormalBoard();
      Lizzie.frame.suggestionclick = LizzieFrame.outOfBoundCoordinate;
      Lizzie.frame.mouseOverCoordinate = LizzieFrame.outOfBoundCoordinate;
      boardRenderer.clearBranch();
      if (Lizzie.config.isDoubleEngineMode()) {
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

  public void toggleGtpConsole() {
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
    if (EngineManager.isEngineGame || EngineManager.isPreEngineGame) return;
    if (floatBoard != null && floatBoard.isVisible() && floatBoard.editMode)
      floatBoard.changeEetEditMode();
    if (!isTrying) {
      isTrying = true;
      try {
        tryString = SGFParser.saveToString(false);
      } catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
      titleBeforeTrying = this.getTitle();
      this.setTitle(Lizzie.resourceBundle.getString("LizzieFrame.tryTitle")); // "试下中...");
      toolbar.tryPlay.setText(
          Lizzie.resourceBundle.getString("BottomToolbar.tryplayBack")); // ("恢复");
      tryMoveList = Lizzie.board.getMoveList();
      Lizzie.board.getHistory().getCurrentHistoryNode().getData().moveNumber = 0;
      Lizzie.board.deleteMoveNoHintAfter();
    } else {
      isTrying = false;
      toolbar.tryPlay.setText(Lizzie.resourceBundle.getString("BottomToolbar.tryPlay")); // ("试下");
      SGFParser.loadFromString(tryString);
      Lizzie.board.resetMoveList(tryMoveList);
      Lizzie.board.setMovelistAll();
      if (Lizzie.board.getCurrentMovenumber() == 0 && Lizzie.leelaz.isPondering())
        Lizzie.leelaz.ponder();
      this.setTitle(titleBeforeTrying);
      if (needRefresh) refresh();
    }
  }

  public void toggleAnalysisFrameAlwaysontop() {
    if (analysisFrame != null && analysisFrame.isVisible()) {
      if (analysisFrame.isAlwaysOnTop()) {
        if (Lizzie.config.isDoubleEngineMode())
          if (analysisFrame2 != null && analysisFrame2.isVisible()) {
            analysisFrame2.setAlwaysOnTop(false);
            analysisFrame2.setTopTitle();
          }
        analysisFrame.setAlwaysOnTop(false);
        Lizzie.config.uiConfig.put("suggestions-always-ontop", false);
      } else {
        if (Lizzie.config.isDoubleEngineMode())
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
      if (Lizzie.config.isDoubleEngineMode()) {
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
      if (Lizzie.config.isDoubleEngineMode() && analysisFrame2 != null) {
        analysisFrame2.setVisible(false);
      }
      Lizzie.frame.suggestionclick = LizzieFrame.outOfBoundCoordinate;
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
    if (estimateResults == null || !estimateResults.isVisible()) {
      if (Lizzie.frame.floatBoard != null && Lizzie.frame.floatBoard.isVisible())
        estimateResults = new EstimateResults(null);
      else estimateResults = new EstimateResults(this);
    }
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

  public void extraMode(ExtraMode currentMode, ExtraMode previousMode) {
    setMinimumSize(new Dimension(520, 400));
    boolean windowIsMaximized = Lizzie.frame.getExtendedState() == JFrame.MAXIMIZED_BOTH;
    boardRenderer = new BoardRenderer(false);
    subBoardXmouse = 0;
    subBoardYmouse = 0;
    subBoardLengthmouse = 0;
    subMaxSize = 0;
    if (previousMode == ExtraMode.Double_Engine && currentMode != ExtraMode.Double_Engine) {
      if (analysisFrame2 != null && analysisFrame2.isVisible()) analysisFrame2.setVisible(false);
    }
    if (currentMode == ExtraMode.Four_Sub) {
      Lizzie.frame.subBoardRenderer2 = new SubBoardRenderer(false);
      Lizzie.frame.subBoardRenderer3 = new SubBoardRenderer(false);
      Lizzie.frame.subBoardRenderer4 = new SubBoardRenderer(false);
      Lizzie.frame.subBoardRenderer2.setOrder(1);
      Lizzie.frame.subBoardRenderer3.setOrder(2);
      Lizzie.frame.subBoardRenderer4.setOrder(3);
      LizzieFrame.subBoardRenderer.showHeat = false;
      LizzieFrame.subBoardRenderer.showHeatAfterCalc = false;
      try {
        LizzieFrame.subBoardRenderer.removeHeat();
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
      }
    }
    if (currentMode == ExtraMode.Double_Engine) {
      if (Lizzie.config.showSubBoard) Lizzie.config.toggleShowSubBoard();
      if (Lizzie.config.showComment) Lizzie.config.toggleShowComment();
      if (Lizzie.config.showCaptured) Lizzie.config.toggleShowCaptured();
      //      if (!Lizzie.config.changedStatus && Lizzie.config.showStatus)
      //        Lizzie.config.toggleShowStatus(true);
      if (Lizzie.config.showVariationGraph) Lizzie.config.toggleShowVariationGraph();
      if (Lizzie.config.showWinrateGraph) Lizzie.config.toggleShowWinrate();
      LizzieFrame.menu.setEngineMenuone2status(true);
      boardRenderer2 = new BoardRenderer(false);
      boardRenderer2.setOrder(1);
      if (!windowIsMaximized) {
        Lizzie.frame.setBounds(
            Lizzie.frame.getX(),
            Lizzie.frame.getY(),
            (Lizzie.frame.getHeight() - toolbarHeight - 65) * 2,
            Lizzie.frame.getHeight());
      }
      if (previousMode != ExtraMode.Double_Engine) {
        Lizzie.board.setMovelistAll2();
        if (moveListFrame != null && moveListFrame.isVisible()) {
          toggleBadMoves();
          toggleBadMoves();
        }
        if (analysisFrame != null && analysisFrame.isVisible()) {
          toggleBestMoves();
          toggleBestMoves();
        }
      }
      if (Lizzie.leelaz2 != null) {
        Lizzie.engineManager.switchEngine(Lizzie.leelaz2.currentEngineN(), false);
      }
    } else {
      if (Lizzie.leelaz2 != null) {
        Lizzie.leelaz2.nameCmdfornoponder();
        LizzieFrame.menu.changeEngineIcon(EngineManager.currentEngineNo2, 2);
      }
      LizzieFrame.menu.setEngineMenuone2status(false);
      if (moveListFrame2 != null && moveListFrame2.isVisible()) moveListFrame2.setVisible(false);
    }

    if (currentMode == ExtraMode.Thinking) {
      if (!Lizzie.config.showSubBoard) Lizzie.config.toggleShowSubBoard();
      if (!Lizzie.config.showWinrateGraph) Lizzie.config.toggleShowWinrate();
      if (Lizzie.config.showLargeWinrateOnly()) Lizzie.config.toggleLargeWinrate();
      if (!Lizzie.config.showLargeSubBoard()) Lizzie.config.toggleLargeSubBoard();
      if (Lizzie.config.showComment) Lizzie.config.toggleShowComment();
      if (!Lizzie.config.showCaptured) Lizzie.config.toggleShowCaptured();
      if (!Lizzie.config.showListPane) Lizzie.config.toggleShowListPane();
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
      }
    }
    if (currentMode != ExtraMode.Thinking && currentMode != ExtraMode.Four_Sub)
      setHideListScrollpane(false);
    else if (Lizzie.config.showListPane()) setHideListScrollpane(true);
    reSetLoc();
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
    if (Lizzie.config.isFloatBoardMode()) {
      Lizzie.config.toggleExtraMode(0);
      Lizzie.frame.toggleIndependentMainBoard();
      Lizzie.frame.refresh();
    } else Lizzie.frame.onlyIndependMainBoard();
  }

  public void toggleShowIndependMainBoard() {
    if (!Lizzie.config.isShowingIndependentMain) Lizzie.frame.toggleIndependentMainBoard();
    else {
      if (Lizzie.config.isFloatBoardMode()) {
        Lizzie.config.toggleExtraMode(0);
        Lizzie.frame.refresh();
      } else {
        Lizzie.frame.toggleIndependentMainBoard();
      }
    }
  }

  public void onlyIndependMainBoard() {
    setMinimumSize(new Dimension(0, 0));
    Lizzie.config.toggleExtraMode(8);
    if (!Lizzie.config.isShowingIndependentMain) toggleIndependentMainBoard();
    Lizzie.frame.refresh();
  }

  public void independentBoardMode(boolean showSubBoard) {
    setMinimumSize(new Dimension(0, 0));
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
    LizzieFrame.subBoardRenderer.showHeat = Lizzie.config.showHeat;
    LizzieFrame.subBoardRenderer.showHeatAfterCalc = Lizzie.config.showHeatAfterCalc;
    try {
      LizzieFrame.subBoardRenderer.clearBranch();
      LizzieFrame.subBoardRenderer.removeHeat();
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
    LizzieFrame.subBoardRenderer.showHeat = Lizzie.config.showHeat;
    LizzieFrame.subBoardRenderer.showHeatAfterCalc = Lizzie.config.showHeatAfterCalc;
    try {
      LizzieFrame.subBoardRenderer.clearBranch();
      LizzieFrame.subBoardRenderer.removeHeat();
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
      Lizzie.config.uiConfig.put("show-badmoves-frame", true);
      moveListFrame = new MoveListFrame(1);
      if (Lizzie.config.isDoubleEngineMode()) {
        if (moveListFrame2 == null || !moveListFrame2.isVisible()) {
          moveListFrame2 = new MoveListFrame(2);
          moveListFrame2.setVisible(true);
          if (Lizzie.config.badmovesalwaysontop) moveListFrame2.setAlwaysOnTop(true);
        }
      }
      moveListFrame.setVisible(true);
      if (Lizzie.config.badmovesalwaysontop) moveListFrame.setAlwaysOnTop(true);
    } else {
      Lizzie.config.uiConfig.put("show-badmoves-frame", false);
      moveListFrame.setVisible(false);
      if (Lizzie.config.isDoubleEngineMode() && moveListFrame2 != null) {
        moveListFrame2.setVisible(false);
      }
      clickbadmove = LizzieFrame.outOfBoundCoordinate;
      Lizzie.frame.refresh();
      try {
        Lizzie.config.persist();
      } catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }
  }

  public static void sendAiTime(boolean needCountDown, Leelaz engine, boolean showTimeMsg) {
    if (Lizzie.config.advanceTimeSettings) {
      Lizzie.leelaz.sendCommand(Lizzie.config.advanceTimeTxt);
      if (needCountDown) {
        Lizzie.engineManager.playingAgainstHumanEngineCountDown = new EngineCountDown();
        if (!Lizzie.engineManager.playingAgainstHumanEngineCountDown.setEngineCountDown(
            Lizzie.config.advanceTimeTxt, Lizzie.leelaz)) {
          Lizzie.engineManager.playingAgainstHumanEngineCountDown = null;
          Utils.showMsgNoModal(
              Lizzie.resourceBundle.getString("EngineManager.parseAdvcanceTimeSettingsFailed"));
        } else {
          Lizzie.engineManager.playingAgainstHumanEngineCountDown.initialize(
              !Lizzie.frame.playerIsBlack);
          Lizzie.engineManager.StartCountDown();
        }
      }
    } else {
      if (Lizzie.config.kataTimeSettings) {
        // kata-time_settings fischer byoyomi absolute
        String txtKataTimeSettings = "kata-time_settings ";
        switch (Lizzie.config.kataTimeType) {
          case 0:
            txtKataTimeSettings +=
                "byoyomi "
                    + Lizzie.config.kataTimeMainTimeMins * 60
                    + " "
                    + Lizzie.config.kataTimeByoyomiSecs
                    + " "
                    + Lizzie.config.kataTimeByoyomiTimes;
            break;
          case 1:
            txtKataTimeSettings +=
                "fischer "
                    + Lizzie.config.kataTimeMainTimeMins * 60
                    + " "
                    + Lizzie.config.kataTimeFisherIncrementSecs;
            break;
          case 2:
            txtKataTimeSettings += "absolute " + Lizzie.config.kataTimeMainTimeMins * 60;
            break;
        }
        engine.sendCommand(txtKataTimeSettings);
        if (needCountDown) {
          Lizzie.engineManager.playingAgainstHumanEngineCountDown = new EngineCountDown();
          if (!Lizzie.engineManager.playingAgainstHumanEngineCountDown.setEngineCountDown(
              txtKataTimeSettings, Lizzie.leelaz)) {
            Lizzie.engineManager.playingAgainstHumanEngineCountDown = null;
            Utils.showMsgNoModal(
                Lizzie.resourceBundle.getString("EngineManager.parseAdvcanceTimeSettingsFailed"));
          } else {
            Lizzie.engineManager.playingAgainstHumanEngineCountDown.initialize(
                !Lizzie.frame.playerIsBlack);
            Lizzie.engineManager.StartCountDown();
          }
        }
        if (showTimeMsg && !engine.isKatago) {
          Utils.showMsg(
              Lizzie.resourceBundle.getString(
                  "LizzieFrame.sendTimes.kataGoTimeMismatch")); // "引擎时间设置为KataGo专用,但当前引擎不是KataGo,可能无法正确控制时间!");
        }
      } else
        engine.sendCommand("time_settings 0 " + Lizzie.config.maxGameThinkingTimeSeconds + " 1");
    }
  }

  public void startNewGame() {
    if (Lizzie.frame.isContributing) {
      Utils.showMsg(
          Lizzie.resourceBundle.getString("Contribute.tips.contributingAndStartAnotherLizzieYzy"));
      return;
    }
    Lizzie.frame.stopAiPlayingAndPolicy();
    boolean isPondering = false;
    if (Lizzie.leelaz.isPondering()) {
      Lizzie.leelaz.togglePonder();
      isPondering = true;
    }
    NewGameDialog newGameDialog = new NewGameDialog(this);
    newGameDialog.setVisible(true);
    boolean playerIsBlack = newGameDialog.playerIsBlack();
    newGameDialog.dispose();
    if (newGameDialog.isCancelled()) {
      if (isPondering) Lizzie.leelaz.togglePonder();
      Lizzie.frame.isPlayingAgainstLeelaz = false;
      return;
    }
    Lizzie.frame.isAnaPlayingAgainstLeelaz = false;
    Lizzie.board.clear(false);
    if (Lizzie.board.tempmovelistForGenMoveGame != null)
      Lizzie.board.setlist(Lizzie.board.tempmovelistForGenMoveGame);
    GameInfo gameInfo = newGameDialog.gameInfo;
    Lizzie.board.getHistory().setGameInfo(gameInfo);
    Lizzie.leelaz.komi(gameInfo.getKomi());
    Lizzie.frame.playerIsBlack = playerIsBlack;
    // Lizzie.leelaz.isSettingHandicap=true;
    boolean isHandicapGame = gameInfo.getHandicap() != 0;
    Lizzie.frame.allowPlaceStone = false;
    Lizzie.frame.isPlayingAgainstLeelaz = true;
    Runnable syncBoard =
        new Runnable() {
          public void run() {
            while (!Lizzie.leelaz.isLoaded() || EngineManager.isEmpty) {
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
            if (Lizzie.config.limitMyTime)
              countDownForHuman(
                  Lizzie.config.getMySaveTime(),
                  Lizzie.config.getMyByoyomiSeconds(),
                  Lizzie.config.getMyByoyomiTimes());
            if (!Lizzie.config.genmoveGameNoTime) sendAiTime(true, Lizzie.leelaz, true);
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
            //发送检测版本+检测maxVisits等的命令
            GameInfo gameInfo = Lizzie.board.getHistory().getGameInfo();
            gameInfo.setPlayerBlack(
                playerIsBlack
                    ? Lizzie.resourceBundle.getString("NewAnaGameDialog.me")
                    : Lizzie.leelaz.oriEnginename);
            gameInfo.setPlayerWhite(
                playerIsBlack
                    ? Lizzie.leelaz.oriEnginename
                    : Lizzie.resourceBundle.getString("NewAnaGameDialog.me"));

            Lizzie.leelaz.isGamePaused = false;
            Lizzie.board.isGameBoard = true;
            menu.toggleDoubleMenuGameStatus();
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
                Lizzie.resourceBundle.getString("LizzieFrame.prompt.sgfExists"),
                Lizzie.resourceBundle.getString("LizzieFrame.warning"),
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
        Utils.showMsg(Lizzie.resourceBundle.getString("LizzieFrame.saveFileFailed"));
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
                Lizzie.resourceBundle.getString("LizzieFrame.ifReplaceFile")
                    + curFile.getName()
                    + "\" ?");
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        box.add(label);
        Utils.addFiller(box, 5, 5);
        JFontLabel label2 =
            new JFontLabel(Lizzie.resourceBundle.getString("LizzieFrame.replaceFileNotice"));
        label2.setAlignmentX(Component.LEFT_ALIGNMENT);
        box.add(label2);
        Utils.addFiller(box, 5, 5);
        JFontCheckBox disableCheckBox =
            new JFontCheckBox(
                Lizzie.resourceBundle.getString(
                    "LizzieFrame.noNoticeAgain")); // LizzieFrame.noNoticeAgain
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
        options[0] = Lizzie.resourceBundle.getString("LizzieFrame.confirm");
        options[1] = Lizzie.resourceBundle.getString("LizzieFrame.cancel");
        Object defaultOption = Lizzie.resourceBundle.getString("LizzieFrame.cancel");
        JOptionPane optionPane =
            new JOptionPane(
                box,
                JOptionPane.QUESTION_MESSAGE,
                JOptionPane.YES_NO_OPTION,
                null,
                options,
                defaultOption);
        JDialog dialog =
            optionPane.createDialog(
                this, Lizzie.resourceBundle.getString("LizzieFrame.replaceFileTitle"));
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
    if (Lizzie.leelaz.isPondering() && !EngineManager.isEngineGame) {
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
                Lizzie.resourceBundle.getString("LizzieFrame.prompt.sgfExists"),
                Lizzie.resourceBundle.getString("LizzieFrame.warning"),
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
        Utils.showMsg(Lizzie.resourceBundle.getString("LizzieFrame.saveFileFailed")); // 保存失败
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
                Lizzie.resourceBundle.getString("LizzieFrame.prompt.sgfExists"),
                Lizzie.resourceBundle.getString("LizzieFrame.warning"),
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
        Lizzie.board.saveListForEdit();
        Lizzie.board.clearforedit();
        Lizzie.board.setMoveListWithFlatten(
            Lizzie.board.tempallmovelist, startMoveNumber, blackToPlay);
        isSavingRaw = true;
        SGFParser.save(Lizzie.board, file.getPath());
        isSavingRaw = false;
        if (file.getParent() != null) {
          filesystem.put("last-folder", file.getParent());
        }
        Lizzie.board.clearEditStuff();
      } catch (IOException err) {
        Utils.showMsg(Lizzie.resourceBundle.getString("LizzieFrame.saveFileFailed"));
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
        new FileDialog(this, Lizzie.resourceBundle.getString("LizzieFrame.chooseKifu"));

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
        new FileDialog(this, Lizzie.resourceBundle.getString("LizzieFrame.chooseOpeningSgf"));

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
        enginePKSgfString.add(Lizzie.board.getallmovelist());
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
        new FileDialog(this, Lizzie.resourceBundle.getString("LizzieFrame.chooseKifu"));

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
      LizzieFrame.toolbar.chkAnaAutoSave.setSelected(true);
      StartAnaDialog newgame = new StartAnaDialog(isFlashMode, Lizzie.frame);
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
    File file = new File("save" + File.separator + "autoGame1.sgf");
    if (file.exists()) loadFile(file, true, true);
    else {
      File file2 = new File("save" + File.separator + "autoGame2.sgf");
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
      // System.out.println(file.getPath());
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
      SwingUtilities.invokeLater(
          new Runnable() {
            public void run() {
              JOptionPane.showConfirmDialog(
                  Lizzie.frame,
                  Lizzie.resourceBundle.getString("LizzieFrame.prompt.failedToOpenFile"),
                  "Error",
                  JOptionPane.ERROR);
            }
          });
    }
    Lizzie.board.setMovelistAll();
    if (showHint) {
      Lizzie.frame.resetMovelistFrameandAnalysisFrame();
      if (!Lizzie.config.isFloatBoardMode()
          && !(analysisTable != null && analysisTable.frame.isVisible()))
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
  public int varBigX;
  public int varBigY;
  private BufferedImage cachedVariationTreeBigImage;
  public Paint backgroundPaint;
  private int cachedBackgroundWidth = 0, cachedBackgroundHeight = 0;
  public boolean redrawBackgroundAnyway = false;

  /**
   * Draws the game board and interface
   *
   * @param g0 not used
   */
  public void paintMianPanel(Graphics g0) {
    if (redrawWinratePaneOnly) {
      drawWinratePane(this.grx, this.gry, this.grw, this.grh);
      redrawWinratePaneOnly = false;
    } else {
      isSmallCap = false;
      int width = mainPanel.getWidth();
      int height = mainPanel.getHeight();

      Optional<Graphics2D> backgroundG = Optional.empty();
      if (cachedBackgroundWidth != width
          || cachedBackgroundHeight != height
          || redrawBackgroundAnyway) {
        backgroundG = Optional.of(createBackground(width, height));
      }
      if (!showControls) {
        BufferedImage cachedImage = new BufferedImage(width, height, TYPE_INT_ARGB);
        Graphics2D g = (Graphics2D) cachedImage.getGraphics();
        // g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        if (Lizzie.config.isFourSubMode()) {
          int topInset = mainPanel.getInsets().top;
          int leftInset = mainPanel.getInsets().left;
          int rightInset = mainPanel.getInsets().right;
          int bottomInset = mainPanel.getInsets().bottom;

          boolean noWinrate = !Lizzie.config.showWinrateGraph;
          boolean noVariation = !Lizzie.config.showVariationGraph;
          boolean noBasic = !Lizzie.config.showCaptured;
          boolean noComment = !Lizzie.config.showComment || Lizzie.config.showListPane();
          boolean noListPane = !Lizzie.config.showListPane();
          boolean noCommentAndListPane = noComment && noListPane;
          // board
          subMaxSize = (int) (min(width - leftInset - rightInset, height - topInset - bottomInset));
          subMaxSize = max(subMaxSize, max(Board.boardWidth, Board.boardHeight) + 5);
          subBoardRenderer.setLocation(topInset, leftInset);
          subBoardRenderer.setBoardLength(subMaxSize / 2, subMaxSize / 2);
          subBoardRenderer.draw(g);

          subBoardRenderer2.setLocation(subMaxSize / 2, leftInset);
          subBoardRenderer2.setBoardLength(subMaxSize / 2, subMaxSize / 2);
          subBoardRenderer2.draw(g);

          subBoardRenderer3.setLocation(topInset, subMaxSize / 2);
          subBoardRenderer3.setBoardLength(subMaxSize / 2, subMaxSize / 2);
          subBoardRenderer3.draw(g);

          subBoardRenderer4.setLocation(subMaxSize / 2, subMaxSize / 2);
          subBoardRenderer4.setBoardLength(subMaxSize / 2, subMaxSize / 2);
          subBoardRenderer4.draw(g);

          subBoardLengthmouse = subMaxSize;

          int trueWidth = width - leftInset - rightInset - subMaxSize;
          int trueHeight = height - topInset - bottomInset;

          boolean isWidth = trueWidth * 0.72 > trueHeight;
          if (isWidth) {
            maxSize = (int) (min(trueWidth, trueHeight));
            maxSize = max(maxSize, max(Board.boardWidth, Board.boardHeight) + 5);
            boardX = width - maxSize;
            boardY = trueHeight - maxSize;
            boardRenderer.setLocation(boardX, boardY);
            boardRenderer.setBoardLength(maxSize, maxSize);
            boardRenderer.draw(g);

            int vh = trueHeight;
            int vw = boardX - subMaxSize;
            int vx = subMaxSize;
            int vy = 0;
            if (!noVariation) {
              if (!noCommentAndListPane) {
                if (noWinrate && noBasic) {
                  if (backgroundG.isPresent()) {
                    drawContainer(backgroundG.get(), vx, vy, vw, vh);
                  }
                  createVarTreeImage(vx, vy + vh, vw, vh / 2, g);
                  if (noComment) setListScrollpane(vx, vy + vh / 2, vw, vh / 2);
                  else if (noListPane) drawComment(g, vx, vy + vh / 2, vw, vh / 2);
                } else {
                  if (backgroundG.isPresent()) {
                    drawContainer(backgroundG.get(), vx, vy + vh / 2, vw, vh / 2);
                  }
                  createVarTreeImage(vx, vy + vh / 2, vw, vh / 4, g);
                  if (noComment) setListScrollpane(vx, vy + vh * 3 / 4, vw, vh / 4);
                  else if (noListPane) drawComment(g, vx, vy + vh * 3 / 4, vw, vh / 4);
                }
              } else {
                if (noWinrate && noBasic) {
                  if (backgroundG.isPresent()) {
                    drawContainer(backgroundG.get(), vx, vy, vw, vh);
                  }
                  createVarTreeImage(vx, vy, vw, vh, g);
                } else {
                  if (backgroundG.isPresent()) {
                    drawContainer(backgroundG.get(), vx, vy + vh / 2, vw, vh / 2);
                  }
                  createVarTreeImage(vx, vy + vh / 2, vw, vh / 2, g);
                }
              }
            } else if (!noCommentAndListPane) {
              if (noComment) setListScrollpane(vx, vy + vh / 2, vw, vh / 2);
              else if (noListPane) drawComment(g, vx, vy + vh / 2, vw, vh / 2);
            }
            if (!noWinrate) {
              if (backgroundG.isPresent()) {
                drawContainer(backgroundG.get(), subMaxSize, 0, vw, vh / 2);
              }
              if (!noBasic) {
                grw = vw;
                grx = vx;
                gry = vy + vh / 4;
                grh = vh / 4;
                drawWinratePane(grx, gry, grw, grh);
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
              }
            } else if (!noBasic) {
              if (backgroundG.isPresent()) {
                drawContainer(backgroundG.get(), subMaxSize, 0, vw, vh / 2);
              }
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
            boardX = subMaxSize;
            boardY = trueHeight - maxSize;
            boardRenderer.setLocation(boardX, boardY);
            boardRenderer.setBoardLength(maxSize, maxSize);
            boardRenderer.draw(g);

            int vx = boardX;
            int vy = 0;
            int vw = trueWidth;
            int vh = boardY;

            if (!noVariation) {
              if (noWinrate && noBasic) {
                if (backgroundG.isPresent()) {
                  drawContainer(backgroundG.get(), vx, vy, vw, vh);
                }
                if (!noCommentAndListPane) {
                  if (noComment) setListScrollpane(vx, vy, vw / 2, vh);
                  else if (noListPane) drawComment(g, vx, vy, vw / 2, vh);
                  createVarTreeImage(vx + vw / 2, vy, vw / 2, vh, g);
                } else createVarTreeImage(vx, vy, vw, vh, g);
              } else {
                if (backgroundG.isPresent()) {
                  drawContainer(backgroundG.get(), vx, vy + vh / 2, vw, vh / 2);
                }
                if (!noCommentAndListPane) {
                  if (noComment) setListScrollpane(vx, vy + vh / 2, vw / 2, vh / 2);
                  else if (noListPane) drawComment(g, vx, vy + vh / 2, vw / 2, vh / 2);
                  createVarTreeImage(vx + vw / 2, vy + vh / 2, vw / 2, vh / 2, g);
                } else createVarTreeImage(vx, vy + vh / 2, vw, vh / 2, g);
              }
            } else if (noWinrate && noBasic) {
              if (backgroundG.isPresent()) {
                drawContainer(backgroundG.get(), vx, vy, vw, vh);
              }
              if (noComment) setListScrollpane(vx, vy, vw, vh);
              else if (noListPane) drawComment(g, vx, vy, vw, vh);
            } else {
              if (!noCommentAndListPane) {
                if (backgroundG.isPresent()) {
                  drawContainer(backgroundG.get(), vx, vy + vh / 2, vw, vh / 2);
                }
                if (noComment) setListScrollpane(vx, vy + vh / 2, vw, vh / 2);
                else if (noListPane) drawComment(g, vx, vy + vh / 2, vw, vh / 2);
              }
            }

            if (!noWinrate) {
              if (noCommentAndListPane && noVariation) {
                if (backgroundG.isPresent()) {
                  drawContainer(backgroundG.get(), vx, vy, vw, vh);
                }

                if (!noBasic) {
                  grx = vx + vw / 2;
                  gry = vy;
                  grw = vw / 2;
                  grh = vh;
                  drawWinratePane(grx, gry, grw, grh);
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
                }

              } else {
                if (backgroundG.isPresent()) {
                  drawContainer(backgroundG.get(), vx, vy, vw, vh / 2);
                }
                if (!noBasic) {
                  grx = vx + vw / 2;
                  gry = vy;
                  grw = vw / 2;
                  grh = vh / 2;
                  drawWinratePane(grx, gry, grw, grh);
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
                }
              }
            } else if (!noBasic) {

              if (noCommentAndListPane && noVariation) {
                if (backgroundG.isPresent()) {
                  drawContainer(backgroundG.get(), vx, vy, vw, vh);
                }
                statx = vx;
                staty = vy;
                statw = vw;
                stath = vh;
                drawCaptured(g, statx, staty, statw, stath / 2, true);
                drawMoveStatistics(g, statx, staty + stath / 2, statw, stath / 2);
              } else {
                if (backgroundG.isPresent()) {
                  drawContainer(backgroundG.get(), vx, vy, vw, vh / 2);
                }
                statx = vx;
                staty = vy;
                statw = vw;
                stath = vh / 2;
                drawCaptured(g, statx, staty, statw, stath / 2, true);
                drawMoveStatistics(g, statx, staty + stath / 2, statw, stath / 2);
              }
            }
          }
        } else if (Lizzie.config.isDoubleEngineMode()) {
          int topInset = mainPanel.getInsets().top;
          int leftInset = mainPanel.getInsets().left;
          int rightInset = mainPanel.getInsets().right;
          int bottomInset = mainPanel.getInsets().bottom;

          int trueWidth = width - leftInset - rightInset;
          int trueHeight = height - topInset - bottomInset;
          maxSize = (int) (min(trueWidth / 2, trueHeight - 20));
          maxSize = max(maxSize, max(Board.boardWidth, Board.boardHeight) + 5);
          boardX = leftInset;
          boardY = topInset;
          boardRenderer.setLocation(boardX, boardY);
          boardRenderer.setBoardLength(maxSize, maxSize);
          boardRenderer.draw(g);

          int maxSize2 = maxSize;
          int boardX2 = maxSize2 + leftInset; // (width - maxSize) / 8 * BoardPositionProportion;
          int boardY2 = topInset;
          boardRenderer2.setLocation(boardX2, boardY2);
          boardRenderer2.setBoardLength(maxSize2, maxSize2);
          boardRenderer2.draw(g);

          int commentX1 = 0;
          int commentX2 = 0;

          String statusKey = "LizzieFrame.display." + (Lizzie.leelaz.isPondering() ? "on" : "off");
          String statusText =
              Lizzie.resourceBundle.getString(statusKey)
                  + (Lizzie.config.userKnownX
                      ? ""
                      : Lizzie.resourceBundle.getString("LizzieFrame.display.space"));
          String ponderingText = Lizzie.resourceBundle.getString("LizzieFrame.display.pondering");
          weightText = Lizzie.leelaz.oriEnginename;
          if (weightText.length() > 15) weightText = weightText.substring(0, 10);
          String text1 =
              Lizzie.resourceBundle.getString("LizzieFrame.mainEngine")
                  + weightText
                  + " "
                  + ponderingText
                  + " "
                  + statusText;

          commentX1 = drawPonderingStateForExtraMode2(g, text1, leftInset, maxSize, 18);
          if (Lizzie.leelaz2 != null) {
            weightText2 = Lizzie.leelaz2.oriEnginename;
            String statusKey2 =
                "LizzieFrame.display." + (Lizzie.leelaz.isPondering() ? "on" : "off");
            String statusText2 = Lizzie.resourceBundle.getString(statusKey2);
            String ponderingText2 =
                Lizzie.resourceBundle.getString("LizzieFrame.display.pondering");
            if (weightText2.length() > 15) weightText2 = weightText2.substring(0, 10);
            String text2 =
                Lizzie.resourceBundle.getString("LizzieFrame.subEngine")
                    + weightText2
                    + " "
                    + ponderingText2
                    + " "
                    + statusText2;
            commentX2 = drawPonderingStateForExtraMode2(g, text2, maxSize, maxSize, 18);
          } else {
            String text2 = Lizzie.resourceBundle.getString("LizzieFrame.subEngine") + weightText2;
            commentX2 = drawPonderingStateForExtraMode2(g, text2, maxSize, maxSize, 18);
          }
          String text1comm =
              Lizzie.resourceBundle.getString("LizzieFrame.visits")
                  + Utils.getPlayoutsString(Lizzie.board.getData().getPlayouts())
                  + " "
                  + Lizzie.resourceBundle.getString("LizzieFrame.winrate")
                  + String.format(Locale.ENGLISH, "%.1f%%", Lizzie.board.getData().winrate);
          drawPonderingStateForExtraMode2(g, text1comm, leftInset + commentX1 + 5, maxSize, 18);

          String text2comm =
              Lizzie.resourceBundle.getString("LizzieFrame.visits")
                  + Utils.getPlayoutsString(Lizzie.board.getData().getPlayouts2())
                  + " "
                  + Lizzie.resourceBundle.getString("LizzieFrame.winrate")
                  + String.format(Locale.ENGLISH, "%.1f%%", Lizzie.board.getData().winrate2);
          drawPonderingStateForExtraMode2(
              g, text2comm, maxSize + leftInset + commentX2 + 5, maxSize, 18);
          //  }
        } else if (Lizzie.config.isThinkingMode()) {
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
          boardRenderer2.setLocation(topInset, leftInset);
          boardRenderer2.setBoardLength(subMaxSize, subMaxSize);
          boardRenderer2.draw(g);

          int trueWidth = width - leftInset - rightInset - subMaxSize;
          int trueHeight = height - topInset - bottomInset;

          boolean isWidth = trueWidth * 0.72 > trueHeight;
          if (isWidth) {
            maxSize = (int) (min(trueWidth, trueHeight));
            maxSize = max(maxSize, max(Board.boardWidth, Board.boardHeight) + 5);
            boardX = width - maxSize;
            boardY = trueHeight - maxSize;
            boardRenderer.setLocation(boardX, boardY);
            boardRenderer.setBoardLength(maxSize, maxSize);
            boardRenderer.draw(g);

            int vh = trueHeight;
            int vw = boardX - subMaxSize;
            int vx = subMaxSize;
            int vy = 0;

            if (!noVariation) {
              if (!noCommentAndListPane) {
                if (noWinrate && noBasic) {
                  if (backgroundG.isPresent()) {
                    drawContainer(backgroundG.get(), vx, vy, vw, vh);
                  }
                  createVarTreeImage(vx, vy + vh, vw, vh / 2, g);
                  if (noComment) setListScrollpane(vx, vy + vh / 2, vw, vh / 2);
                  else if (noListPane) drawComment(g, vx, vy + vh / 2, vw, vh / 2);
                } else {
                  if (backgroundG.isPresent()) {
                    drawContainer(backgroundG.get(), vx, vy + vh / 2, vw, vh / 2);
                  }
                  createVarTreeImage(vx, vy + vh / 2, vw, vh / 4, g);
                  if (noComment) setListScrollpane(vx, vy + vh * 3 / 4, vw, vh / 4);
                  else if (noListPane) drawComment(g, vx, vy + vh * 3 / 4, vw, vh / 4);
                }
              } else {
                if (noWinrate && noBasic) {
                  if (backgroundG.isPresent()) {
                    drawContainer(backgroundG.get(), vx, vy, vw, vh);
                  }
                  createVarTreeImage(vx, vy, vw, vh, g);
                } else {
                  if (backgroundG.isPresent()) {
                    drawContainer(backgroundG.get(), vx, vy + vh / 2, vw, vh / 2);
                  }
                  createVarTreeImage(vx, vy + vh / 2, vw, vh / 2, g);
                }
              }
            } else if (!noCommentAndListPane) {
              if (noComment) setListScrollpane(vx, vy + vh / 2, vw, vh / 2);
              else if (noListPane) drawComment(g, vx, vy + vh / 2, vw, vh / 2);
            }
            if (!noWinrate) {
              if (backgroundG.isPresent()) {
                drawContainer(backgroundG.get(), subMaxSize, 0, vw, vh / 2);
              }
              if (!noBasic) {
                grw = vw;
                grx = vx;
                gry = vy + vh / 4;
                grh = vh / 4;
                drawWinratePane(grx, gry, grw, grh);
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
              }
            } else if (!noBasic) {
              if (backgroundG.isPresent()) {
                drawContainer(backgroundG.get(), subMaxSize, 0, vw, vh / 2);
              }
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
            boardRenderer.draw(g);

            int vx = boardX;
            int vy = 0;
            int vw = trueWidth;
            int vh = boardY;

            if (!noVariation) {
              if (noWinrate && noBasic) {
                if (backgroundG.isPresent()) {
                  drawContainer(backgroundG.get(), vx, vy, vw, vh);
                }
                if (!noCommentAndListPane) {
                  if (noComment) setListScrollpane(vx, vy, vw / 2, vh);
                  else if (noListPane) drawComment(g, vx, vy, vw / 2, vh);
                  createVarTreeImage(vx + vw / 2, vy, vw / 2, vh, g);
                } else {
                  createVarTreeImage(vx, vy, vw, vh, g);
                }
              } else {
                if (backgroundG.isPresent()) {
                  drawContainer(backgroundG.get(), vx, vy + vh / 2, vw, vh / 2);
                }
                if (!noCommentAndListPane) {
                  if (noComment) setListScrollpane(vx, vy + vh / 2, vw / 2, vh / 2);
                  else if (noListPane) drawComment(g, vx, vy + vh / 2, vw / 2, vh / 2);
                  createVarTreeImage(vx + vw / 2, vy + vh / 2, vw / 2, vh / 2, g);
                } else createVarTreeImage(vx, vy + vh / 2, vw, vh / 2, g);
              }
            } else if (noWinrate && noBasic) {
              if (backgroundG.isPresent()) {
                drawContainer(backgroundG.get(), vx, vy, vw, vh);
              }
              if (noComment) setListScrollpane(vx, vy, vw, vh);
              else if (noListPane) drawComment(g, vx, vy, vw, vh);
            } else {
              if (!noCommentAndListPane) {
                if (backgroundG.isPresent()) {
                  drawContainer(backgroundG.get(), vx, vy + vh / 2, vw, vh / 2);
                }
                if (noComment) setListScrollpane(vx, vy + vh / 2, vw, vh / 2);
                else if (noListPane) drawComment(g, vx, vy + vh / 2, vw, vh / 2);
              }
            }

            if (!noWinrate) {
              if (noCommentAndListPane && noVariation) {
                if (backgroundG.isPresent()) {
                  drawContainer(backgroundG.get(), vx, vy, vw, vh);
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
                if (backgroundG.isPresent()) {
                  drawContainer(backgroundG.get(), vx, vy, vw, vh / 2);
                }
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
                if (backgroundG.isPresent()) {
                  drawContainer(backgroundG.get(), vx, vy, vw, vh);
                }
                statx = vx;
                staty = vy;
                statw = vw;
                stath = vh;
                drawCaptured(g, statx, staty, statw, stath / 2, true);
                drawMoveStatistics(g, statx, staty + stath / 2, statw, stath / 2);
              } else {
                if (backgroundG.isPresent()) {
                  drawContainer(backgroundG.get(), vx, vy, vw, vh / 2);
                }
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
        else if (Lizzie.config.isFloatBoardMode()) // 8浮动棋盘模式
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
            LizzieFrame.boardRenderer = independentMainBoard.boardRenderer;
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
          if (Lizzie.config.showStatus && !Lizzie.config.userKnownX) drawCommandString(g);
          if (Lizzie.config.showStatus) {
            if (Lizzie.leelaz != null && (Lizzie.leelaz.isLoaded() || Lizzie.leelaz.isNormalEnd)) {
              String statusKey =
                  "LizzieFrame.display." + (Lizzie.leelaz.isPondering() ? "on" : "off");
              String statusText =
                  Lizzie.resourceBundle.getString(statusKey)
                      + (Lizzie.config.userKnownX
                          ? ""
                          : Lizzie.resourceBundle.getString("LizzieFrame.display.space"));
              String ponderingText =
                  Lizzie.resourceBundle.getString("LizzieFrame.display.pondering");
              //            String switching =
              // Lizzie.resourceBundle.getString("LizzieFrame.prompt.switching");
              //            String switchingText = Lizzie.leelaz.switching() ? switching : "";
              String weightText = "";
              if (isContributing)
                weightText = Lizzie.resourceBundle.getString("LizzieFrame.weightText.contributing");
              if (EngineManager.isEmpty)
                weightText = Lizzie.resourceBundle.getString("LizzieFrame.noEngineText");
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
            drawContainer(backgroundG.get(), vx, vy, trueWidth, trueHeight);
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
            }

            // initialize

            //    cachedImage = new BufferedImage(width, height, TYPE_INT_ARGB);
            //     Graphics2D g = (Graphics2D) cachedImage.getGraphics();
            //     g.setRenderingHint(RenderingHints.KEY_RENDERING,
            // RenderingHints.VALUE_RENDER_QUALITY);

            if (Lizzie.config.showStatus && !Lizzie.config.isMinMode() && !Lizzie.config.userKnownX)
              drawCommandString(g);
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
            if (Lizzie.config.showStatus && !Lizzie.config.isMinMode()) {
              if (Lizzie.leelaz != null
                  && (Lizzie.leelaz.isLoaded() || Lizzie.leelaz.isNormalEnd)) {
                String statusKey =
                    "LizzieFrame.display." + (Lizzie.leelaz.isPondering() ? "on" : "off");
                String statusText =
                    Lizzie.resourceBundle.getString(statusKey)
                        + (Lizzie.config.userKnownX
                            ? ""
                            : Lizzie.resourceBundle.getString("LizzieFrame.display.space"));
                String ponderingText =
                    Lizzie.resourceBundle.getString("LizzieFrame.display.pondering");
                //   String switching
                // =Lizzie.resourceBundle.getString("LizzieFrame.prompt.switching");
                // String switchingText = Lizzie.leelaz.switching() ? switching : "";
                String weightText = "";
                if (isContributing)
                  weightText =
                      Lizzie.resourceBundle.getString("LizzieFrame.weightText.contributing");
                else if (EngineManager.isEmpty)
                  weightText = Lizzie.resourceBundle.getString("LizzieFrame.noEngineText");
                else weightText = Lizzie.leelaz.oriEnginename;
                String text2 = ponderingText + " " + statusText; // + " " + switchingText;
                drawPonderingState(
                    g, weightText, text2, ponderingX, ponderingY, ponderingY2, ponderingSize);
              } else {
                String loadingText = getLoadingText();
                drawPonderingState(g, loadingText, loadingX, loadingY, loadingSize);
              }
            }

            //  if (firstTime) {
            // toolbar.setAllUnfocuse();
            //  firstTime = false;
            //   }
            // Optional<String> dynamicKomi = Lizzie.leelaz.getDynamicKomi();
            // if (Lizzie.config.showDynamicKomi && dynamicKomi.isPresent()) {
            // String text =Lizzie.resourceBundle.getString("LizzieFrame.display.dynamic-komi");
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
            if (Lizzie.config.showStatus && !Lizzie.config.isMinMode() && !Lizzie.config.userKnownX)
              drawCommandString(g);

            if (Lizzie.config.showWinrateGraph) {
              drawMoveStatistics(g, statx, staty, statw, stath);
            }

            if (Lizzie.config.showStatus && !Lizzie.config.isMinMode()) {
              if (Lizzie.leelaz != null && Lizzie.leelaz.isLoaded()) {
                String statusKey =
                    "LizzieFrame.display." + (Lizzie.leelaz.isPondering() ? "on" : "off");
                String statusText =
                    Lizzie.resourceBundle.getString(statusKey)
                        + (Lizzie.config.userKnownX
                            ? ""
                            : Lizzie.resourceBundle.getString("LizzieFrame.display.space"));
                String ponderingText =
                    Lizzie.resourceBundle.getString("LizzieFrame.display.pondering");
                //      String switching
                // =Lizzie.resourceBundle.getString("LizzieFrame.prompt.switching");
                // String switchingText = Lizzie.leelaz.switching() ? switching : "";
                String weightText = "";
                if (isContributing)
                  weightText =
                      Lizzie.resourceBundle.getString("LizzieFrame.weightText.contributing");
                if (EngineManager.isEmpty)
                  weightText = Lizzie.resourceBundle.getString("LizzieFrame.noEngineText");
                else weightText = Lizzie.leelaz.oriEnginename;
                String text2 = ponderingText + " " + statusText; // + " " + switchingText;
                drawPonderingState(
                    g, weightText, text2, ponderingX, ponderingY, ponderingY2, ponderingSize);
              }
            }
            boardRenderer.setLocation(boardX, boardY);
            boardRenderer.setBoardLength(maxSize, maxSize);
            boardRenderer.draw(g);
            if (backgroundG.isPresent()) {
              drawContainer(backgroundG.get(), capx, capy, spaceW, spaceH);
              drawContainer(backgroundG.get(), leftInset, vy, spaceW, vh);
            }
            // if (Lizzie.leelaz != null && Lizzie.leelaz.isLoaded()) {
            if (Lizzie.config.showStatus && !Lizzie.config.isMinMode()) {
              if (Lizzie.leelaz == null || !Lizzie.leelaz.isLoaded()) {
                String loadingText = getLoadingText();
                drawPonderingState(g, loadingText, loadingX, loadingY, loadingSize);
              }
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

    g0.drawImage(cachedBackground, 0, 0, null);
    g0.drawImage(cachedImage, 0, 0, null);
    if (Lizzie.config.showWinrateGraph && cachedWinrateImage != null && !showControls)
      g0.drawImage(cachedWinrateImage, grx, gry, null);
    if (Lizzie.config.showVariationGraph
        && shouldShowSimpleVariation()
        && cachedVariationTreeBigImage != null
        && !showControls) g0.drawImage(cachedVariationTreeBigImage, varBigX, varBigY, null);
  }

  private String getLoadingText() {
    // TODO Auto-generated method stub
    if (Lizzie.leelaz.isDownWithError)
      return Lizzie.resourceBundle.getString("LizzieFrame.display.down");
    else if (Lizzie.leelaz.isTuning)
      return Lizzie.resourceBundle.getString("LizzieFrame.display.tuning");
    else return Lizzie.resourceBundle.getString("LizzieFrame.display.loading");
  }

  /**
   * temporary measure to refresh background. ideally we shouldn't need this (but we want to release
   * Lizzie 0.5 today, not tomorrow!). Refactor me out please! (you need to get blurring to work
   * properly on startup).
   */
  public void refreshContainer() {
    redrawBackgroundAnyway = true;
    if (Lizzie.config.isFloatBoardMode()) this.paintMianPanel(mainPanel.getGraphics());
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
    appendComment();
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
    appendComment();
  }

  private void updateMoveList(boolean notPondering) {
    if (notPondering) {
      int lastMoveCandidateNo = Lizzie.board.getData().lastMoveMatchCandidteNo;
      Lizzie.board.updateMovelist(Lizzie.board.getHistory().getCurrentHistoryNode());
      if (Lizzie.board.getData().lastMoveMatchCandidteNo != lastMoveCandidateNo) refresh();
    } else Lizzie.board.updateMovelist(Lizzie.board.getHistory().getCurrentHistoryNode());
  }

  private Graphics2D createBackground(int width, int height) {
    cachedBackground = new BufferedImage(width, height, TYPE_INT_RGB);
    cachedBackgroundWidth = cachedBackground.getWidth();
    cachedBackgroundHeight = cachedBackground.getHeight();
    Graphics2D g = cachedBackground.createGraphics();

    BufferedImage wallpaper = boardRenderer.getWallpaper();
    int drawWidth = max(wallpaper.getWidth(), mainPanel.getWidth());
    int drawHeight = max(wallpaper.getHeight(), mainPanel.getHeight());
    // Support seamless texture
    if (Lizzie.config.usePureBackground) {
      g.setColor(Lizzie.config.pureBackgroundColor);
      g.fillRect(0, 0, width, height);
      g.dispose();
      return g;
    }
    boardRenderer.drawTextureImage(g, wallpaper, 0, 0, drawWidth, drawHeight, false);
    Lizzie.board.setForceRefresh(true);
    if (backgroundPaint == null) {
      BufferedImage result = new BufferedImage(100, 100, TYPE_INT_ARGB);
      filter20.filter(cachedBackground.getSubimage(0, 0, 100, 100), result);
      backgroundPaint =
          new TexturePaint(result, new Rectangle(0, 0, result.getWidth(), result.getHeight()));
    }
    redrawBackgroundAnyway = false;
    return g;
  }

  private void drawContainer(Graphics g, int vx, int vy, int vw, int vh) {
    if (Lizzie.config.usePureBackground
        || vw <= 0
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
    if (!Lizzie.config.isFloatBoardMode()) {
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
    if (!Lizzie.config.isFloatBoardMode()) {
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
    // commandsToShow.add(Lizzie.resourceBundle.getString("LizzieFrame.commands.keyD"));
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
    String commandString = Lizzie.resourceBundle.getString("LizzieFrame.prompt.showControlsHint");

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
    if (isInPlayMode()||isShowingByoTime) {
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
                    ? (Lizzie.resourceBundle.getString("Byoyomi.time")
                        + this.leftMinuts
                        + ":"
                        + this.leftSeconds
                        + " ")
                    : "")
                + (this.byoSeconds >= 0
                    ? (" "
                        + Lizzie.resourceBundle.getString("Byoyomi.byoyomi")
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
    double lastWR = 50; // winrate the previous move
    double lastScore = 0;
    boolean validLastWinrate = false; // whether it was actually calculated
    Optional<BoardHistoryNode> previous =
        Lizzie.board.getHistory().getCurrentHistoryNode().previous();
    BoardData curData = Lizzie.board.getHistory().getCurrentHistoryNode().getData();
    if (EngineManager.isEngineGame && Lizzie.board.getHistory().getMoveNumber() > 3) {
      previous = Lizzie.board.getHistory().getCurrentHistoryNode().previous().get().previous();
    } else if (isPlayingAgainstLeelaz || isAnaPlayingAgainstLeelaz)
      if (Lizzie.board.getHistory().isBlacksTurn() == playerIsBlack && previous.isPresent()) {
        curData = previous.get().getData();
        previous = Lizzie.board.getHistory().getCurrentHistoryNode().previous().get().previous();
      }
    if (previous.isPresent()) {
      if (previous.get().getData().getPlayouts() > 0) {
        lastWR = previous.get().getData().winrate;
        lastScore = previous.get().getData().scoreMean;
        validLastWinrate = true;
      } else {
        if (previous.get().previous().isPresent()) {
          BoardData prePreData = previous.get().previous().get().getData();
          if (prePreData.getPlayouts() > 0) {
            lastWR = 100 - prePreData.winrate;
            lastScore = -prePreData.scoreMean;
            validLastWinrate = true;
          }
        }
      }
    }
    if (EngineManager.isEngineGame && Lizzie.board.getHistory().getMoveNumber() > 3) {
      lastWR = 100 - lastWR;
    }
    // Leelaz.WinrateStats stats = Lizzie.leelaz.getWinrateStats();
    double curWR = curData.winrate; // stats.maxWinrate; // winrate on this move
    double curScore = curData.scoreMean;
    boolean validWinrate = (curData.getPlayouts() > 0);
    //    if (isPlayingAgainstLeelaz
    //        && playerIsBlack == !Lizzie.board.getHistory().getData().blackToPlay) {
    //      validWinrate = false;
    //    }

    if (!validWinrate) {
      curWR = 100 - lastWR; // display last move's winrate for now (with color difference)
      curScore = -lastScore;
    }
    double whiteWR, blackWR;
    if (curData.blackToPlay) {
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
    g.setColor(Color.WHITE);
    // Last move
    // validLastWinrate && validWinrate
    //   if (true) {
    String text = "";
    // if (Lizzie.config.handicapInsteadOfWinrate) {
    // double currHandicapedWR = Lizzie.leelaz.winrateToHandicap(100 - curWR);
    // double lastHandicapedWR = Lizzie.leelaz.winrateToHandicap(lastWR);
    // text = String.format(Locale.ENGLISH,": %.2f", currHandicapedWR - lastHandicapedWR);
    // } else {

    // }
    //    if (EngineManager.isEngineGame && Lizzie.board.getHistory().getMoveNumber() <= 3) {
    //      text = "";
    //    }
    boolean isKataStyle = false;
    if (curData.isKataData
        || curData.isSaiData
        || (Lizzie.leelaz.isKatago && !EngineManager.isEmpty)
        || (EngineManager.isEngineGame
            && (Lizzie.engineManager.engineList.get(EngineManager.engineGameInfo.blackEngineIndex)
                    .isKatago
                || Lizzie.engineManager.engineList.get(
                        EngineManager.engineGameInfo.whiteEngineIndex)
                    .isKatago))) {
      isKataStyle = true;
      if (!curData.bestMoves.isEmpty()) {
        double score = curData.bestMoves.get(0).scoreMean;
        if (Lizzie.config.showKataGoScoreLeadWithKomi) {
          if (curData.blackToPlay) {
            score = score + curData.getKomi();
          } else {
            score = -score + curData.getKomi();
          }
        } else if (!curData.blackToPlay) {
          score = -score;
        }
        scoreLead = score;
        scoreStdev = Lizzie.leelaz.scoreStdev;
      } // +"目差:""复杂度:"

      text +=
          (Lizzie.config.showKataGoScoreLeadWithKomi
                  ? Lizzie.resourceBundle.getString("LizzieFrame.scoreLeadWithKomi")
                  : Lizzie.resourceBundle.getString("LizzieFrame.scoreLeadJustScore"))
              + String.format(Locale.ENGLISH, "%.1f", scoreLead);
      if (Lizzie.config.isThinkingMode() || Lizzie.config.isFourSubMode())
        text += " (±" + String.format(Locale.ENGLISH, "%.1f", curData.scoreStdev) + ")";
      if (EngineManager.isEngineGame && !Lizzie.leelaz.isSai)
        text =
            text
                + " "
                + Lizzie.resourceBundle.getString("LizzieFrame.scoreStdev")
                + String.format(Locale.ENGLISH, "%.1f", scoreStdev)
                + " ";
    }
    if (Lizzie.leelaz.isColorEngine) {
      // "阶段:""贴目:"
      text =
          text
              + Lizzie.resourceBundle.getString("LizzieFrame.scoreStdev")
              + Lizzie.leelaz.stage
              + " "
              + Lizzie.resourceBundle.getString("LizzieFrame.komi")
              + Lizzie.leelaz.komi;
    }
    if (EngineManager.isEngineGame) {
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
      double wr = validLastWinrate ? 100 - lastWR - curWR : 0;
      double score = validLastWinrate ? (-lastScore) - curScore : 0;
      text = text + " " + Lizzie.resourceBundle.getString("LizzieFrame.display.lastMove");
      int lastNo = Lizzie.board.getData().lastMoveMatchCandidteNo;
      if (lastNo > 0) {
        text += "(#" + lastNo + ")";
      } else text += "(#  )";
      text += ": " + ((wr > 0 ? "+" : "-") + String.format(Locale.ENGLISH, "%.1f%%", Math.abs(wr)));
      if (isKataStyle && !EngineManager.isEngineGame) {
        text +=
            " "
                + ((score > 0 ? "+" : "-") + String.format(Locale.ENGLISH, "%.1f", Math.abs(score)))
                + Lizzie.resourceBundle.getString("LizzieFrame.pts"); // + "目";
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
      // Draw change of winrate bars
      if (validWinrate && validLastWinrate) {
        double gain = 100 - lastWR - curWR;
        double blackLastWR = curData.blackToPlay ? 100 - lastWR : lastWR;
        int lastPosxW = barPosxB + (int) (blackLastWR * maxBarwidth / 100);
        int diffPosX = Math.min(barPosxW, lastPosxW);
        int diffWidth = Math.abs(barPosxW - lastPosxW);
        if (diffWidth > 0) {
          Stroke oldstroke = g.getStroke();
          boolean isGig = barHeight > 30;
          g.setStroke(new BasicStroke(isGig ? 2f : 1f));
          boolean isGain = gain >= 0;
          g.setColor(isGain ? Color.GREEN : Color.RED);
          boolean rightTri;
          if (curData.blackToPlay) {
            if (isGain) rightTri = false;
            else rightTri = true;
          } else {
            if (isGain) rightTri = true;
            else rightTri = false;
          }
          if (rightTri) {
            if (diffWidth > 3) g.drawLine(diffPosX, barPosY, diffPosX + diffWidth - 3, barPosY);
            int triStart = Math.max(diffPosX, diffPosX + diffWidth - (isGig ? 7 : 5));
            int[] xPoints = {triStart, triStart, diffPosX + diffWidth};
            int[] yPoints = {barPosY + 1 - (isGig ? 5 : 3), barPosY + 1 + (isGig ? 5 : 3), barPosY};
            g.fillPolygon(xPoints, yPoints, 3);
          } else {
            int posXEnd = diffPosX + diffWidth - 1;
            if (diffWidth > 3) {
              g.drawLine(diffPosX + 2, barPosY, posXEnd, barPosY);
            }
            int triStart = Math.min(posXEnd + 1, diffPosX + (isGig ? 7 : 5));
            int[] xPoints = {triStart, triStart, diffPosX};
            int[] yPoints = {
              barPosY + 1 - (isGig ? 5 : 3), barPosY + 1 + (isGig ? 5 : 3), barPosY + 1
            };
            g.fillPolygon(xPoints, yPoints, 3);
          }
          if (diffWidth > (isGig ? 7 : 5)) {
            g.setColor(Color.GRAY);
            g.drawLine(lastPosxW, barPosY, lastPosxW, barPosY + barHeight - 1);
          }
          g.setStroke(oldstroke);
        }
      }

      // Show percentage above bars
      setPanelFont(g, (int) (min(maxBarwidth * 0.63, height) * 0.24));

      int fontHeigt = g.getFontMetrics().getAscent() - g.getFontMetrics().getDescent();
      g.setColor(Color.WHITE);
      String winStringB = String.format(Locale.ENGLISH, "%.1f%%", blackWR);
      String winStringW = String.format(Locale.ENGLISH, "%.1f%%", whiteWR);
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
            String.valueOf(Lizzie.board.getHistory().getCurrentHistoryNode().getData().moveNumber);
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
            String.valueOf(Lizzie.board.getHistory().getCurrentHistoryNode().getData().moveNumber);
        int swM = g.getFontMetrics().stringWidth(moveNumber);
        if (width > 2 * swM) {
          g.drawString(moveNumber, posX + (width - swM) / 2, posY + height / 6 + fontHeigt / 2);
        }
      }
    }
  }

  private boolean shouldDrawMoveNumberDown() {
    if (EngineManager.isEngineGame) {
      if (Lizzie.engineManager.engineList.get(EngineManager.engineGameInfo.whiteEngineIndex)
              .isKatago
          && Lizzie.engineManager.engineList.get(EngineManager.engineGameInfo.whiteEngineIndex)
                  .usingSpecificRules
              > 0) return true;
      if (Lizzie.engineManager.engineList.get(EngineManager.engineGameInfo.blackEngineIndex)
              .isKatago
          && Lizzie.engineManager.engineList.get(EngineManager.engineGameInfo.blackEngineIndex)
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

    bval = String.format(Locale.ENGLISH, "%d", Lizzie.board.getData().blackCaptures);
    wval = String.format(Locale.ENGLISH, "%d", Lizzie.board.getData().whiteCaptures);

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
      if (!EngineManager.isEngineGame) {
        BoardHistoryNode node = Lizzie.board.getHistory().getCurrentHistoryNode();
        if (node.nodeInfo.analyzedMatchValue) {
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
          if (nodeInfo.analyzedMatchValue) {
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
    String bAiScore = String.format(Locale.ENGLISH, "%.1f", blackValue * 100 / analyzedBlack);
    String wAiScore = String.format(Locale.ENGLISH, "%.1f", whiteValue * 100 / analyzedWhite);
    if (!isSmallCap) {
      drawStringMid(
          g,
          posX + width / 4,
          posY + height * 28 / 32,
          uiFont,
          Font.PLAIN,
          Lizzie.resourceBundle.getString("LizzieFrame.captures") + bval, // 提子
          height / 6,
          width * 3 / 10,
          0);
      drawStringMid(
          g,
          posX + width * 3 / 4,
          posY + height * 28 / 32,
          uiFont,
          Font.PLAIN,
          Lizzie.resourceBundle.getString("LizzieFrame.captures") + wval,
          height / 6,
          width * 3 / 10,
          0);

      if (analyzedBlack > 0)
        drawStringMid(
            g,
            posX + width / 4,
            posY + height * 19 / 32,
            uiFont,
            Font.PLAIN,
            Lizzie.resourceBundle.getString("LizzieFrame.AIscore") + bAiScore, // "AI总评分:"
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
            Lizzie.resourceBundle.getString("LizzieFrame.AIscore") + wAiScore,
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
            Lizzie.resourceBundle.getString("LizzieFrame.AIscore") + bAiScore,
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
            Lizzie.resourceBundle.getString("LizzieFrame.AIscore") + wAiScore,
            height * 2 / 5,
            width * 4 / 10,
            0);
    }
    // Komi
    if (isSmallCap)
      setPanelFont(
          g,
          (float) (min(width * 0.4, height * 0.85) * 0.2) > Config.frameFontSize + 6
              ? Config.frameFontSize + 6
              : Math.max((float) (min(width * 0.4, height * 0.85) * 0.2), 11f));
    else setPanelFont(g, Math.max(11f, (float) (height * 0.18)));
    String komi = String.valueOf(Lizzie.board.getHistory().getGameInfo().getKomi());
    int kw = g.getFontMetrics().stringWidth(komi);
    // g.setFont(new Font(g.getFont().getName(),Font.BOLD,g.getFont().getSize()));
    if (isSmallCap)
      g.drawString(komi, posX - strokeRadius + width / 2 - kw / 2, posY + height * 15 / 16);
    else g.drawString(komi, posX - strokeRadius + width / 2 - kw / 2, posY + height * 7 / 8);

    // Move or rules
    String moveOrRules = "";
    boolean usingSpecificRues = false;
    Leelaz leela = null;
    if (EngineManager.isEngineGame && EngineManager.engineGameInfo.isGenmove)
      leela =
          Lizzie.board.getHistory().isBlacksTurn()
              ? Lizzie.engineManager.engineList.get(EngineManager.engineGameInfo.blackEngineIndex)
              : Lizzie.engineManager.engineList.get(EngineManager.engineGameInfo.whiteEngineIndex);
    else leela = Lizzie.leelaz;
    // ||(Lizzie.engineManager.isEngineGame&&Lizzie.engineManager.engineGameInfo.isGenmove)
    if (leela.isKatago && !EngineManager.isEmpty) {
      switch (leela.usingSpecificRules) {
        case 1:
          moveOrRules = Lizzie.resourceBundle.getString("LizzieFrame.currentRules.chinese");
          usingSpecificRues = true;
          break;
        case 2:
          moveOrRules = Lizzie.resourceBundle.getString("LizzieFrame.currentRules.chn-ancient");
          usingSpecificRues = true;
          break;
        case 3:
          moveOrRules = Lizzie.resourceBundle.getString("LizzieFrame.currentRules.japanese");
          usingSpecificRues = true;
          break;
        case 4:
          moveOrRules = Lizzie.resourceBundle.getString("LizzieFrame.currentRules.tromp-taylor");
          usingSpecificRues = true;
          break;
        case 5:
          moveOrRules = Lizzie.resourceBundle.getString("LizzieFrame.currentRules.others");
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
    }
    if (!shouldDrawMoveNumberDown()) {
      moveOrRules =
          String.valueOf(Lizzie.board.getHistory().getCurrentHistoryNode().getData().moveNumber);
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
      winrateGraph.clearParames();
      return;
    }
    if (lastGrw != w
        || lastGrh != h
        || refreshWinratePane
        || !refreshFromInfo
        || (System.currentTimeMillis() - winratePaneTime) >= 200) {
      BufferedImage cachedWinrateImage = new BufferedImage(w, h, TYPE_INT_ARGB);
      BufferedImage cachedWinrateBackgroundImage = new BufferedImage(w, h, TYPE_INT_ARGB);
      BufferedImage cachedWinrateBlunderImage = new BufferedImage(w, h, TYPE_INT_ARGB);
      Graphics2D g = (Graphics2D) cachedWinrateImage.getGraphics();
      Graphics2D gBlunder = (Graphics2D) cachedWinrateBlunderImage.getGraphics();
      Graphics2D gBackground = (Graphics2D) cachedWinrateBackgroundImage.getGraphics();
      g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
      g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
      gBlunder.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
      gBlunder.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
      gBackground.setRenderingHint(
          RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
      gBackground.setRenderingHint(
          RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
      winrateGraph.draw(g, gBlunder, gBackground, 0, 0, w, h);
      gBackground.drawImage(cachedWinrateBlunderImage, 0, 0, null);
      gBackground.drawImage(cachedWinrateImage, 0, 0, null);
      Lizzie.frame.cachedWinrateImage = cachedWinrateBackgroundImage;
      g.dispose();
      gBlunder.dispose();
      gBackground.dispose();
      refreshWinratePane = false;
      refreshFromInfo = false;
      winratePaneTime = System.currentTimeMillis();
      lastGrw = w;
      lastGrh = h;
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
    if (Lizzie.config.isThinkingMode()) {
      boardCoordinates = boardRenderer2.convertScreenToCoordinates(x, y);
      if (!boardCoordinates.isPresent())
        boardCoordinates = boardRenderer.convertScreenToCoordinates(x, y);
    } else {
      boardCoordinates = boardRenderer.convertScreenToCoordinates(x, y);
    }
    if (boardCoordinates.isPresent()) {
      int[] coords = boardCoordinates.get();
      if (!isPlayingAgainstLeelaz && !isAnaPlayingAgainstLeelaz) {
        if (Lizzie.board.getHistory().getStones()[Board.getIndex(coords[0], coords[1])]
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
    if (Lizzie.config.isThinkingMode()) {
      boardCoordinates = boardRenderer2.convertScreenToCoordinates(x, y);
      if (!boardCoordinates.isPresent())
        boardCoordinates = boardRenderer.convertScreenToCoordinates(x, y);
    } else {
      boardCoordinates = boardRenderer.convertScreenToCoordinates(x, y);
    }
    int moveNumber = winrateGraph.moveNumber(x - grx, y - gry);

    if (boardCoordinates.isPresent()) {
      if (Lizzie.frame.isContributing) return;
      int[] coords = boardCoordinates.get();
      if (Lizzie.board.hasStoneAt(coords)) {
        Lizzie.board.setPressStoneInfo(coords, false);
      }
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
            || !LizzieFrame.toolbar.chkAutoPlayBlack.isSelected()
                == Lizzie.board.getData().blackToPlay) {
          if (isPlayingAgainstLeelaz || isAnaPlayingAgainstLeelaz) {
            if (Lizzie.leelaz.isGamePaused) return;
            if (allowPlaceStone && Lizzie.leelaz.isLoaded() && !EngineManager.isEmpty)
              Lizzie.board.place(coords[0], coords[1]);
            else
              Utils.showMsg(
                  Lizzie.resourceBundle.getString(
                      "LizzieFrame.waitEngineLoadingHint")); // ("请等待引擎加载完毕");
            if (Lizzie.config.showrect == 1) boardRenderer.removeblock();
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
        && !EngineManager.isEngineGame) {
      variationTreeBig.onClicked(x, y);
    }
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
      return Lizzie.board.getMovenumberInBranch(Board.getIndex(coords[0], coords[1]));
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
      return Board.convertCoordinatesToName(coords[0], coords[1]);
    }
    return "N";
  }

  public int[] convertmousexytocoords(int x, int y) {
    Optional<int[]> boardCoordinates = boardRenderer.convertScreenToCoordinates(x, y);
    if (boardCoordinates.isPresent()) {
      int[] coords = boardCoordinates.get();
      return coords;
    }
    return LizzieFrame.outOfBoundCoordinate;
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
      Optional<int[]> c = Board.asCoordinates(bestMoves.get(i).coordinate);
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

  public void genmove() {
    Lizzie.leelaz.isInputCommand = true;
    Lizzie.leelaz.genmove(Lizzie.board.getHistory().isBlacksTurn() ? "B" : "W");
  }

  public boolean processSubOnMouseMoved(int x, int y) {
    if (Lizzie.config.isFourSubMode()) {
      if (x < subBoardLengthmouse && y < subBoardLengthmouse) {
        // 1
        if (!LizzieFrame.subBoardRenderer.isMouseOver
            && (EngineManager.isEmpty || !Lizzie.leelaz.isPondering())) Lizzie.frame.refresh();
        LizzieFrame.subBoardRenderer.isMouseOver = true;
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
        if (!LizzieFrame.subBoardRenderer.isMouseOver
            && (EngineManager.isEmpty || !Lizzie.leelaz.isPondering())) Lizzie.frame.refresh();
        LizzieFrame.subBoardRenderer.isMouseOver = true;
        return true;
      } else {
        return false;
      }
    }
    return false;
  }

  public void onMouseExited() {
    boolean needRepaint = false;
    if (Lizzie.config.isFourSubMode()) {
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
      if (LizzieFrame.subBoardRenderer.isMouseOver) {
        LizzieFrame.subBoardRenderer.isMouseOver = false;
        needRepaint = true;
        LizzieFrame.subBoardRenderer.clearAfterMove();
      }
    }
    mouseOverCoordinate = outOfBoundCoordinate;
    if (isMouseOver) {
      isMouseOver = false;
      needRepaint = true;
      suggestionclick = outOfBoundCoordinate;
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
      if (Lizzie.config.isDoubleEngineMode()) {
        boardRenderer2.removeblock();
      }
    }
    Lizzie.board.clearPressStoneInfo(null);
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
    if (EngineManager.isEngineGame && Lizzie.config.showPreviousBestmovesInEngineGame) {
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
        redrawWinratePaneOnly = true;
        refreshWinratePane = true;
        repaint();
      }
      return true;
    }
    return false;
  }

  public boolean onMouseMoved(int x, int y) {
    if (Lizzie.config.showMouseOverWinrateGraph
        && Lizzie.config.showWinrateGraph
        && processMouseMoveOnWinrateGraph(x, y)) return false;
    if (Lizzie.config.showMouseOverWinrateGraph
        && Lizzie.config.showWinrateGraph
        && winrateGraph.mouseOverNode != null) {
      winrateGraph.clearMouseOverNode();
      this.redrawWinratePaneOnly = true;
      repaint();
      return false;
    }
    boolean needRepaint = false;
    curSuggestionMoveOrderByNumber = -1;
    if (!mainPanel.isFocusOwner() && !commentEditPane.isVisible()) {
      mainPanel.requestFocus();
    }
    if (RightClickMenu.isVisible() || RightClickMenu2.isVisible()) {
      return false;
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
        if (!isMouseOnSub && (!Lizzie.leelaz.isPondering() || EngineManager.isEmpty)) repaint();
        return false;
      } else {
        if (isMouseOnSub) {
          if ((!Lizzie.leelaz.isPondering() || EngineManager.isEmpty)) needRepaint = true;
          isMouseOnSub = false;
          if (Lizzie.config.isFourSubMode()) {
            Lizzie.frame.subBoardRenderer2.isMouseOver = false;
            Lizzie.frame.subBoardRenderer3.isMouseOver = false;
            Lizzie.frame.subBoardRenderer4.isMouseOver = false;
            Lizzie.frame.subBoardRenderer2.clearAfterMove();
            Lizzie.frame.subBoardRenderer3.clearAfterMove();
            Lizzie.frame.subBoardRenderer4.clearAfterMove();
          }
          if (Lizzie.config.showSubBoard) {
            LizzieFrame.subBoardRenderer.isMouseOver = false;
            LizzieFrame.subBoardRenderer.clearAfterMove();
          }
        }
      }
    }
    if (clickOrder != -1) {
      return false;
    }
    // mouseOverCoordinate = outOfBoundCoordinate;
    Optional<int[]> coords = boardRenderer.convertScreenToCoordinates(x, y);
    boolean inBoard = coords.isPresent();
    if (inBoard) {
      int[] curCoords = coords.get();
      Lizzie.board.clearPressStoneInfo(curCoords);
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
        if (Lizzie.config.isDoubleEngineMode()) {
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
              LizzieFrame.boardRenderer.setDisplayedBranchLength(1);
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
        if (Lizzie.config.isDoubleEngineMode()) {
          Optional<int[]> coords2 = boardRenderer2.convertScreenToCoordinates(x, y);
          if (coords2.isPresent()) {
            boardRenderer2.drawmoveblock(
                curCoords[0], curCoords[1], Lizzie.board.getHistory().isBlacksTurn());
          } else
            boardRenderer.drawmoveblock(
                curCoords[0], curCoords[1], Lizzie.board.getHistory().isBlacksTurn());
        } else
          boardRenderer.drawmoveblock(
              curCoords[0], curCoords[1], Lizzie.board.getHistory().isBlacksTurn());
      } else if (Lizzie.frame.isAnaPlayingAgainstLeelaz || Lizzie.frame.isPlayingAgainstLeelaz)
        boardRenderer.removeblock();
      if (Lizzie.config.isDoubleEngineMode()) {
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
          if (Lizzie.config.isDoubleEngineMode()) {
            boardRenderer2.removeblock();
          }
          isShowingRect = false;
        }
      }
    }
    if (needRepaint) refresh();
    return inBoard;
  }

  public void clearMoved() {
    isReplayVariation = false;
    Lizzie.frame.isMouseOver = false;
    boardRenderer.startNormalBoard();
    boardRenderer.clearBranch();
    boardRenderer.notShowingBranch();
    if (Lizzie.config.isDoubleEngineMode()) {
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
    if ((Lizzie.board.getHistory().isBlacksTurn() && !Lizzie.config.showBlackCandidates)
        || (!Lizzie.board.getHistory().isBlacksTurn() && !Lizzie.config.showWhiteCandidates)) {
      return false;
    }
    if (Lizzie.config.showSuggestionVariations)
      return mouseOverCoordinate[0] == x && mouseOverCoordinate[1] == y;
    else return false;
  }

  public boolean isMouseOverIndependMainBoard(int x, int y) {
    if ((Lizzie.board.getHistory().isBlacksTurn() && !Lizzie.config.showBlackCandidates)
        || (!Lizzie.board.getHistory().isBlacksTurn() && !Lizzie.config.showWhiteCandidates)) {
      return false;
    }
    if (Lizzie.config.showSuggestionVariations)
      return independentMainBoard.mouseOverCoordinate[0] == x
          && independentMainBoard.mouseOverCoordinate[1] == y;
    else return false;
  }

  public boolean isMouseOverFloatBoard(int x, int y) {
    if (floatBoard == null) return false;
    if (!Lizzie.config.showBlackCandidates && !Lizzie.config.showWhiteCandidates) {
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

  public boolean isInPlayMode() {
    return Lizzie.config.UsePlayMode
        && (isPlayingAgainstLeelaz || isAnaPlayingAgainstLeelaz)
        && !syncBoard;
  }

  public boolean processCommentMousePressed(MouseEvent e) {
    if (commentEditPane.isVisible()) {
      mainPanel.requestFocus();
      setCommentEditable(false);
    }
    return false;
  }

  public boolean processPressOnSub(MouseEvent e) {
    if (isInPlayMode() || Lizzie.config.isThinkingMode()) return false;
    if (Lizzie.config.isFourSubMode()) {
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
    if (Lizzie.config.isFourSubMode()) {
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

  private void setComment(boolean needReaddText) {
    boolean isLoadingEngine = false;
    boolean isTuningEngine = false;
    if (((Lizzie.leelaz != null && !Lizzie.leelaz.isLoaded())
        || (EngineManager.isPreEngineGame
            && (!Lizzie.engineManager
                    .engineList
                    .get(EngineManager.engineGameInfo.whiteEngineIndex)
                    .isLoaded()
                || !Lizzie.engineManager
                    .engineList
                    .get(EngineManager.engineGameInfo.blackEngineIndex)
                    .isLoaded())))) isLoadingEngine = true;
    if (isLoadingEngine) {
      if ((Lizzie.leelaz != null && Lizzie.leelaz.isTuning)
          || (EngineManager.isPreEngineGame
              && (!Lizzie.engineManager.engineList.get(
                          EngineManager.engineGameInfo.whiteEngineIndex)
                      .isTuning
                  || !Lizzie.engineManager.engineList.get(
                          EngineManager.engineGameInfo.blackEngineIndex)
                      .isTuning))) {
        isTuningEngine = true;
      }
    }
    String comment = "";
    if (!isInPlayMode()) {
      if (isLoadingEngine) {
        commentScrollPane
            .getVerticalScrollBar()
            .setValue(commentScrollPane.getVerticalScrollBar().getMaximum());
      }
      if (isLoadingEngine) {
        if (Lizzie.gtpConsole != null) {
          comment = Lizzie.gtpConsole.console.getText();
          if (!Lizzie.config.showStatus && isTuningEngine)
            comment += Lizzie.resourceBundle.getString("LizzieFrame.display.tuning");
        }
      } else {
        if (EngineManager.isEngineGame
            && Lizzie.config.showPreviousBestmovesInEngineGame
            && Lizzie.board.getHistory().getCurrentHistoryNode().previous().isPresent())
          comment =
              Lizzie.board.getHistory().getCurrentHistoryNode().previous().get().getData().comment;
        else {
          if (Lizzie.board.getHistory().getData().comment.equals("")) {
            if ((Lizzie.leelaz.isPondering()
                    || EngineManager.isEngineGame
                    || Lizzie.frame.isPlayingAgainstLeelaz)
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
          if (EngineManager.isEngineGame) {
            int index =
                comment.indexOf("\n" + Lizzie.resourceBundle.getString("SGFParse.moveTime"));
            if (index > 0) comment = comment.substring(0, index);
          }
        }
      }
      if (EngineManager.isEngineGame && !Lizzie.config.showPreviousBestmovesInEngineGame) {
        comment =
            comment
                + (comment.equals("") ? "" : "\n")
                + Lizzie.resourceBundle.getString("SGFParse.moveTime")
                + (System.currentTimeMillis()
                        - (EngineManager.engineGameInfo.isGenmove
                            ? (Lizzie.engineManager.engineList.get(
                                    Lizzie.board.getHistory().isBlacksTurn()
                                        ? EngineManager.engineGameInfo.blackEngineIndex
                                        : EngineManager.engineGameInfo.whiteEngineIndex)
                                .pkMoveStartTime)
                            : Lizzie.leelaz.getStartPonderTime()))
                    / 1000
                + Lizzie.resourceBundle.getString("SGFParse.seconds");
      }
    }
    if (Lizzie.config.commentFontSize <= 0) {
      int fontSize;
      if (Lizzie.config.isFloatBoardMode()) {
        fontSize = (int) (min(getWidth() * 1.2, getHeight()) * 0.0225);
      } else {
        if (Lizzie.config.showLargeSubBoard() || Lizzie.config.showLargeWinrate()) {
          fontSize =
              (int)
                  (min(
                          (getWidth() > 1.75 * getHeight() ? 1.75 * getHeight() : getWidth())
                              * 0.43,
                          getHeight())
                      * 0.0225);
        } else fontSize = (int) (min(getWidth() * 0.6, getHeight()) * 0.0225);
      }
      if (fontSize > Config.frameFontSize + 3) {
        fontSize = Config.frameFontSize + 3;
      } else if (fontSize < Config.frameFontSize - 2) {
        fontSize = Config.frameFontSize - 2;
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
      // if (isLoadingEngine) {
      if (!cachedComment.equals(comment) || needReaddText && isCommentArea) setCommentText(comment);
      cachedComment = comment;
      //  } else {
      //    setCommentText(comment);
      //  }
    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }

  private void appendComment() {
    if (Lizzie.config.showComment) {
      if (!EngineManager.isEmpty) {
        if (Lizzie.config.appendWinrateToComment || EngineManager.isEngineGame) {
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
              if (!Lizzie.board.getHistory().getData().bestMoves.isEmpty())
                SGFParser.appendComment();
              //     }
              if (!Lizzie.leelaz.isPondering()
                  && !isPlayingAgainstLeelaz
                  && !EngineManager.isEngineGame
                  && !(Lizzie.board.getHistory().getData().comment).equals(comment)) refresh();
            }
          }
        }
      }
      setComment(false);
    }
  }

  private void autosaveMaybe() {
    if (Lizzie.config.autoSaveOnExit && !EngineManager.isEngineGame) {
      autosaveTime++;
      if (autosaveTime >= 60) {
        autosaveTime = 0;
        saveAutoGame(2);
      }
    }
  }

  public void setPlayers(String whitePlayer, String blackPlayer) {
    playerTitle =
        String.format(
            "- ["
                + Lizzie.resourceBundle.getString("Menu.Black")
                + "]%s vs["
                + Lizzie.resourceBundle.getString("Menu.White")
                + "]%s",
            blackPlayer,
            whitePlayer);
    //  updateTitle();
  }

  public void setResult(String result) {
    if (result.equals("")) resultTitle = "";
    else
      resultTitle =
          String.format(
              "(" + Lizzie.resourceBundle.getString("LizzieFrame.result") + "%s)", result);
    //  updateTitle();
  }

  public void updateTitle() {
    if (isTrying) {
      return;
    }
    StringBuilder sb = new StringBuilder();
    if ((EngineManager.isEngineGame && EngineManager.engineGameInfo.isGenmove)) {
      sb.append(DEFAULT_TITLE + "-");
      sb.append(
          (Lizzie.board.getHistory().getData().blackToPlay
                  ? Lizzie.engineManager.engineList.get(
                          EngineManager.engineGameInfo.blackEngineIndex)
                      .oriEnginename
                  : Lizzie.engineManager.engineList.get(
                          EngineManager.engineGameInfo.whiteEngineIndex)
                      .oriEnginename)
              + " "
              + Lizzie.resourceBundle.getString("LizzieFrame.thinking"));
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
        && (!(isPlayingAgainstLeelaz || isAnaPlayingAgainstLeelaz)
            || (syncBoard || !isInPlayMode()))) {

      if (Lizzie.board.getHistory().getData().getPlayouts() > 0) {
        sb.append("[");
        // if (Lizzie.leelaz != null) {
        double winRateC = Lizzie.board.getHistory().getData().winrate;
        if (!Lizzie.board.getHistory().isBlacksTurn()) winRateC = 100 - winRateC;
        winRate = winRateC > -100 && winRateC < 100 ? winRateC : winRate;

        sb.append(String.format(Locale.ENGLISH, "%.1f", winRate));
        if (Lizzie.board.getHistory().getData().isKataData) {
          double scoreC = Lizzie.board.getHistory().getCurrentHistoryNode().getData().scoreMean;
          if (scoreC != 0) {
            if (Lizzie.board.getHistory().isBlacksTurn()) {
              if (Lizzie.config.showKataGoScoreLeadWithKomi)
                scoreC = scoreC + Lizzie.board.getHistory().getGameInfo().getKomi();
            } else {
              if (Lizzie.config.showKataGoScoreLeadWithKomi)
                scoreC = -scoreC + Lizzie.board.getHistory().getGameInfo().getKomi();
              else scoreC = -scoreC;
            }
            score = scoreC;
          }
          sb.append(" " + String.format(Locale.ENGLISH, "%.1f", score));
        }
        sb.append(" " + Utils.getPlayoutsString(Lizzie.board.getHistory().getData().getPlayouts()));
        sb.append("] ");
      } else if (Lizzie.board.getHistory().getCurrentHistoryNode().previous().isPresent()
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
            String.format(
                Locale.ENGLISH, "%.1f", data.blackToPlay ? data.winrate : 100 - data.winrate));
        if (data.isKataData) {
          sb.append(
              " "
                  + String.format(
                      Locale.ENGLISH, "%.1f", data.blackToPlay ? data.scoreMean : -data.scoreMean));
        }
        sb.append(" " + Utils.getPlayoutsString(data.getPlayouts()));
        sb.append("] ");
      } else if (isPlayingAgainstLeelaz || isAnaPlayingAgainstLeelaz) {
        sb.append("[ ---   ---   --- ] ");
      }
    }
    if (hasEnginePkTitile && enginePkTitile != null) {
      sb.append(Lizzie.leelaz.oriEnginename);
      sb.append(visitsString + " ");
      setTitle(enginePkTitile + " " + sb.toString());
    } else {
      // sb.append(DEFAULT_TITLE);
      if (EngineManager.isEmpty) {
        sb.append("Lizzie ");
      } else sb.append(Lizzie.leelaz.oriEnginename);
      if (!EngineManager.isEmpty) {
        if (Lizzie.leelaz.isPondering()) sb.append(visitsString + " ");
        else sb.append(" - " + Lizzie.resourceBundle.getString("LizzieFrame.speedUnit") + " ");
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
    boolean setFrame = false;
    if (Lizzie.config.uiConfig.optBoolean("show-suggestions-frame", false)) {
      if (analysisFrame == null) toggleBestMoves();
      else {
        SwingUtilities.invokeLater(
            new Runnable() {
              public void run() {
                toggleBestMoves();
                toggleBestMoves();
              }
            });
      }
      setFrame = true;
    } else if (analysisFrame != null && analysisFrame.isVisible()) {
      SwingUtilities.invokeLater(
          new Runnable() {
            public void run() {
              toggleBestMoves();
              toggleBestMoves();
            }
          });
      setFrame = true;
    }
    if (Lizzie.config.uiConfig.optBoolean("show-badmoves-frame", false)) {
      if (moveListFrame == null) toggleBadMoves();
      else {
        SwingUtilities.invokeLater(
            new Runnable() {
              public void run() {
                toggleBestMoves();
                toggleBestMoves();
              }
            });
      }
      setFrame = true;
    } else if (moveListFrame != null && moveListFrame.isVisible()) {
      SwingUtilities.invokeLater(
          new Runnable() {
            public void run() {
              toggleBestMoves();
              toggleBestMoves();
            }
          });
      setFrame = true;
    }
    return setFrame;
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
  private void drawComment(Graphics2D g, int x, int y, int w, int h) {
    if (isCommentArea) {
      g.setColor(Lizzie.config.commentBackgroundColor);
      g.fillRect(x, y, w, h);
    }
    if (w < 10 || h < 10) {
      commentScrollPane.setBounds(0, 0, 0, 0);
      blunderContentPane.setBounds(0, 0, 0, 0);
      return;
    }
    x = Utils.zoomIn(x);
    y = Utils.zoomIn(y);
    w = Utils.zoomIn(w);
    h = Utils.zoomIn(h);
    if (Lizzie.config.isShowingBlunderTabel) {
      if (x != blunderContentPane.getX()
          || y + (Lizzie.config.showDoubleMenu ? topPanelHeight : 0) != blunderContentPane.getY()
          || w != blunderContentPane.getWidth()
          || h != blunderContentPane.getHeight()) {
        {
          blunderContentPane.setBounds(
              x, y + (Lizzie.config.showDoubleMenu ? topPanelHeight : 0), w, h);
          blunderContentPane.revalidate();
        }
      }
    } else {
      if (x != commentScrollPane.getX()
          || y + (Lizzie.config.showDoubleMenu ? topPanelHeight : 0) != commentScrollPane.getY()
          || w != commentScrollPane.getWidth()
          || h != commentScrollPane.getHeight()) {
        commentScrollPane.setBounds(
            x, y + (Lizzie.config.showDoubleMenu ? topPanelHeight : 0), w, h);
        commentTextArea.setSize(w, h);
        commentTextPane.setSize(w, h);
        commentEditPane.setBounds(x, y + (Lizzie.config.showDoubleMenu ? topPanelHeight : 0), w, h);
        setComment(true);
      }
    }
  }

  public void doCommentAfterMove() {
    commentScrollPane.getVerticalScrollBar().setValue(0);
  }

  public void setCommentEditable(boolean isEditable) {
    if (isEditable) {
      if (((Lizzie.leelaz != null && !Lizzie.leelaz.isLoaded())
          || (EngineManager.isPreEngineGame
              && (!Lizzie.engineManager
                      .engineList
                      .get(EngineManager.engineGameInfo.whiteEngineIndex)
                      .isLoaded()
                  || !Lizzie.engineManager
                      .engineList
                      .get(EngineManager.engineGameInfo.blackEngineIndex)
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
      appendComment();
    }
  }

  public void setCommentPaneOrArea(boolean isArea) {
    isCommentArea = isArea;
    SwingUtilities.invokeLater(
        new Thread() {
          public void run() {
            setCommentComponet();
          }
        });
  }

  public void resetCommentComponent() {
    commentTextPane.setForeground(Lizzie.config.commentFontColor);
    commentTextPane.setBackground(Lizzie.config.commentBackgroundColor);
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
                : commentFontSize > 0 ? commentFontSize : Config.frameFontSize)
            + "}";
    htmlStyle.addRule(style);
    commentTextArea.setFont(
        new Font(
            Lizzie.config.uiFontName,
            Font.PLAIN,
            Lizzie.config.commentFontSize > 0
                ? Lizzie.config.commentFontSize
                : commentFontSize > 0 ? commentFontSize : Config.frameFontSize));
    commentTextArea.setForeground(Lizzie.config.commentFontColor);
    commentTextArea.setBackground(Lizzie.config.commentBackgroundColor);
    commentScrollPane.setBackground(Lizzie.config.commentBackgroundColor);
  }

  private void setCommentComponet() {
    try {
      if (cachedIsCommentArea != isCommentArea) {
        cachedIsCommentArea = isCommentArea;
        if (isCommentArea) commentScrollPane.setViewportView(commentTextArea);
        else commentScrollPane.setViewportView(commentTextPane);
        //    commentScrollPane.getViewport().setOpaque(false);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private void setCommentText(String comment) {
    if (isCommentArea) {
      int width = (commentScrollPane.getWidth() - 1);
      if (width < 0) return;
      try {
        JlabelSetText(commentTextArea, comment, width, commentScrollPane.getViewport().getHeight());
      } catch (InterruptedException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
      // commentTextArea.setText(comment);
    } else commentTextPane.setText(comment);
  }

  private void JlabelSetText(JLabel jLabel, String longString, int width, int maxHeight)
      throws InterruptedException {
    int lines = 0;
    StringBuilder builder = new StringBuilder("<html>");
    String[] longStrings = longString.split("\n");
    FontMetrics fontMetrics = jLabel.getFontMetrics(jLabel.getFont());
    char[] symbolBefore = {
      ' ', ')', ':', '。', '：', '，', '！', '？', ',', '?', '!', '’', '”', '\'', '"', '[', '<'
    };
    char[] symbolAfter = {'(', '“', '‘', ']', '>'};
    for (String line : longStrings) {
      char[] chars = line.toCharArray();
      int start = 0;
      int len = 1;
      int emptyBeforIndex = -1;
      int emptyAfterIndex = -1;
      boolean outOfLength = false;
      while (start + len < line.length()) {
        while (true) {
          len++;
          if (start + len > line.length()) break;
          if (fontMetrics.charsWidth(chars, start, len) > width) {
            outOfLength = true;
            for (int i = start + len; i > start; i--) {
              char ch = line.charAt(i - 1);
              boolean found = false;
              for (char sym : symbolBefore) {
                if (ch == sym) {
                  emptyBeforIndex = i - start;
                  found = true;
                  break;
                }
              }
              if (found) break;
              for (char sym : symbolAfter) {
                if (ch == sym) {
                  emptyAfterIndex = i - start;
                  found = true;
                  break;
                }
              }
              if (found) break;
            }
            break;
          }
        }
        boolean truncated = false;
        if (outOfLength) {
          if (emptyBeforIndex > 0 && emptyBeforIndex > len - 10) {
            truncated = true;
            builder.append(chars, start, emptyBeforIndex).append("<br/>");
            start += emptyBeforIndex;
          } else if (emptyAfterIndex > 1 && emptyAfterIndex > len - 9) {
            truncated = true;
            builder.append(chars, start, emptyAfterIndex - 1).append("<br/>");
            start += emptyAfterIndex - 1;
          }
        }
        if (!truncated) {
          builder.append(chars, start, len - 1).append("<br/>");
          start += len - 1;
        }
        lines++;
        len = 1;
        emptyBeforIndex = -1;
        emptyAfterIndex = -1;
      }
      if (line.length() == 0) builder.append("<br/>");
      builder.append(chars, start, line.length() - start);
    }
    builder.append("</html>");
    if (maxHeight > 0 && fontMetrics.getHeight() * lines > maxHeight)
      JlabelSetText(jLabel, longString, width - 12, -1);
    else jLabel.setText(builder.toString());
  }

  private double[] lastWinrateScoreDiff(BoardHistoryNode node) {
    // Last winrate
    double[] winScoreDiff = new double[2];

    Optional<BoardData> lastNode = node.previous().flatMap(n -> Optional.of(n.getData()));
    boolean validLastWinrate = lastNode.map(d -> d.getPlayouts() > 0).orElse(false);
    double lastWR = validLastWinrate ? lastNode.get().winrate : 50;
    double lastScore = validLastWinrate ? lastNode.get().scoreMean : 0;

    // Current winrate
    BoardData data = node.getData();
    boolean validWinrate = false;
    double curWR = 50;
    double curScore = 0;
    validWinrate = (data.getPlayouts() > 0);
    curWR = validWinrate ? data.winrate : 100 - lastWR;
    curScore = validWinrate ? data.scoreMean : -lastScore;
    if (validLastWinrate && validWinrate) {
      double lastWinDiff = 100 - lastWR - curWR;
      double lastScoreDiff = -lastScore - curScore;
      if ((lastWinDiff < 0 || lastScoreDiff < 0) && node.getData().lastMove.isPresent()) {
        if (node.isBest) {
          winScoreDiff[0] = 0;
          winScoreDiff[1] = 0;
          return winScoreDiff;
        }
      }
      winScoreDiff[0] = lastWinDiff;
      winScoreDiff[1] = lastScoreDiff;
      return winScoreDiff;
    } else {
      winScoreDiff[0] = 301;
      return winScoreDiff;
    }
  }

  public Color getBlunderNodeColor(BoardHistoryNode node) {
    if (EngineManager.isEngineGame || Lizzie.board.isPkBoard || Lizzie.frame.isContributing) {
      if (node.previous().isPresent() && node.previous().get().previous().isPresent()) {
        if (node.previous().get().previous().get().getData().getPlayouts() == 0
            || node.getData().getPlayouts() == 0) return Color.WHITE;
        double diffWinrate =
            node.getData().getWinrate()
                - node.previous().get().previous().get().getData().getWinrate();
        Optional<Double> st;
        if (node.getData().isKataData && Lizzie.config.useScoreDiffInVariationTree) {
          double diffSocre =
              node.getData().scoreMean - node.previous().get().previous().get().getData().scoreMean;
          st =
              Lizzie.config.blunderWinrateThresholds.flatMap(
                  l ->
                      l.stream()
                          .filter(
                              t ->
                                  (t
                                      >= Math.min(
                                          diffWinrate,
                                          diffSocre
                                              * (1.0
                                                  / Lizzie.config.scoreDiffInVariationTreeFactor))))
                          .reduce((f, s) -> f));
        } else {
          st =
              Lizzie.config.blunderWinrateThresholds.flatMap(
                  l -> l.stream().filter(t -> (t >= diffWinrate)).reduce((f, s) -> f));
        }
        //            diffWinrate >= 0
        //                ? Lizzie.config.blunderWinrateThresholds.flatMap(
        //                    l -> l.stream().filter(t -> (t >= 0 && t <= diffWinrate)).reduce((f,
        // s) -> s))
        //                : Lizzie.config.blunderWinrateThresholds.flatMap(
        //                    l -> l.stream().filter(t -> (t <= 0 && t >= diffWinrate)).reduce((f,
        // s) -> f));
        if (st.isPresent()) {
          return Lizzie.config.blunderNodeColors.map(m -> m.get(st.get())).get();
        } else {
          return Color.WHITE;
        }
      } else return Color.WHITE;
    }
    double diff[] = lastWinrateScoreDiff(node);
    if (diff[0] > 300) return Color.WHITE;
    Optional<Double> st;
    if (Lizzie.config.useScoreDiffInVariationTree)
      st =
          Lizzie.config.blunderWinrateThresholds.flatMap(
              l ->
                  l.stream()
                      .filter(
                          t ->
                              (t
                                  >= Math.min(
                                      diff[0],
                                      diff[1]
                                          * (1.0 / Lizzie.config.scoreDiffInVariationTreeFactor))))
                      .reduce((f, s) -> f));
    else
      st =
          Lizzie.config.blunderWinrateThresholds.flatMap(
              l -> l.stream().filter(t -> (t >= diff[0])).reduce((f, s) -> f));
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
        if (Lizzie.config.isDoubleEngineMode())
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
        Lizzie.board.saveListForEdit();
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
        String coordsName = Board.convertCoordinatesToName(x, y);
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
        Lizzie.board.clearBestMovesAfter(Lizzie.board.getHistory().getStart());
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
      // handleAfterDrawGobanBottom();
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
    if (!Lizzie.board.getHistory().getCurrentHistoryNode().isMainTrunk()
        && !Lizzie.board.getHistory().getCurrentHistoryNode().next().isPresent())
      Lizzie.frame.playCurrentVariation();
    else boardRenderer.addSuggestionAsBranch();
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
        if (Lizzie.config.isDoubleEngineMode()) setDisplayedBranchLength2(2);
      } else if (boardRenderer.isShowingUnImportantBoard()) {
        setDisplayedBranchLength(2);
        if (Lizzie.config.isDoubleEngineMode()) setDisplayedBranchLength2(2);
      } else {
        if (boardRenderer.getReplayBranch() > boardRenderer.getDisplayedBranchLength()) {
          boardRenderer.incrementDisplayedBranchLength(1);
        }
        if (Lizzie.config.isDoubleEngineMode())
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
      if (Lizzie.config.isDoubleEngineMode()) {
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
              JFileChooser chooser =
                  new JFileChooser(
                      filesystem.optString(
                          "last-image-folder", filesystem.getString("last-folder")));
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
                          Lizzie.resourceBundle.getString("LizzieFrame.fileExists"),
                          Lizzie.resourceBundle.getString("LizzieFrame.warning"),
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
                if (Lizzie.config.showWinrateGraph && cachedWinrateImage != null)
                  g1.drawImage(cachedWinrateImage, grx, gry, null);
                g1.dispose();
                try {
                  boolean supported = ImageIO.write(bImg, ext, file);
                  if (!supported) {
                    String displayedMessage =
                        String.format(
                            Lizzie.resourceBundle.getString("LizzieFrame.saveImageErrorHint1")
                                + " \"%s\"\n("
                                + Lizzie.resourceBundle.getString("LizzieFrame.saveImageErrorHint2")
                                + ")",
                            file.getName());
                    JOptionPane.showMessageDialog(
                        Lizzie.frame,
                        displayedMessage,
                        Lizzie.resourceBundle.getString("LizzieFrame.lizzieError"),
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
    if (Lizzie.config.isFloatBoardMode()) saveImageToFile(getIndependMainBoardToClipboard());
    else {
      saveImage(
          Lizzie.frame.boardX, Lizzie.frame.boardY, Lizzie.frame.maxSize, Lizzie.frame.maxSize);
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
      Utils.showMsg(Lizzie.resourceBundle.getString("LizzieFrame.saveSubBoardHint"));
    }
  }

  public void saveImageToFile(BufferedImage image) {
    Runnable runnable =
        new Runnable() {
          public void run() {
            try {
              JSONObject filesystem = Lizzie.config.persisted.getJSONObject("filesystem");
              JFileChooser chooser =
                  new JFileChooser(
                      filesystem.optString(
                          "last-image-folder", filesystem.getString("last-folder")));
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
                          Lizzie.resourceBundle.getString("LizzieFrame.fileExists"),
                          Lizzie.resourceBundle.getString("LizzieFrame.warning"),
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
                            Lizzie.resourceBundle.getString("LizzieFrame.saveImageErrorHint1")
                                + " \"%s\"\n("
                                + Lizzie.resourceBundle.getString("LizzieFrame.saveImageErrorHint2")
                                + ")",
                            file.getName());
                    JOptionPane.showMessageDialog(
                        Lizzie.frame,
                        displayedMessage,
                        Lizzie.resourceBundle.getString("LizzieFrame.lizzieError"),
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
              JFileChooser chooser =
                  new JFileChooser(
                      filesystem.optString(
                          "last-image-folder", filesystem.getString("last-folder")));
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
                          Lizzie.resourceBundle.getString("LizzieFrame.fileExists"),
                          Lizzie.resourceBundle.getString("LizzieFrame.warning"),
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
                if (Lizzie.config.showWinrateGraph && cachedWinrateImage != null)
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
                            Lizzie.resourceBundle.getString("LizzieFrame.saveImageErrorHint1")
                                + " \"%s\"\n("
                                + Lizzie.resourceBundle.getString("LizzieFrame.saveImageErrorHint2")
                                + ")",
                            file.getName());
                    JOptionPane.showMessageDialog(
                        Lizzie.frame,
                        displayedMessage,
                        Lizzie.resourceBundle.getString("LizzieFrame.lizzieError"),
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
    if (font.getSize() > Math.round(Config.frameFontSize * Lizzie.javaScaleFactor) + 4) {
      font =
          new Font(
              font.getName(),
              font.getStyle(),
              Math.round(Config.frameFontSize * Lizzie.javaScaleFactor) + 4);
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
    if (font.getSize() > Math.round(Config.frameFontSize * Lizzie.javaScaleFactor) + 6) {
      font =
          new Font(
              font.getName(),
              font.getStyle(),
              Math.round(Config.frameFontSize * Lizzie.javaScaleFactor) + 6);
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
              if (Lizzie.config.showWinrateGraph && cachedWinrateImage != null)
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
    if (Lizzie.config.isFloatBoardMode()) saveIndependMainBoardToClipboard();
    else
      savePicToClipboard(
          Lizzie.frame.boardX, Lizzie.frame.boardY, Lizzie.frame.maxSize, Lizzie.frame.maxSize);
  }

  private void saveIndependMainBoardToClipboard() {
    if (Config.isScaled || Lizzie.isMultiScreen) {
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
    if (Config.isScaled || Lizzie.isMultiScreen) {
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
    if (Config.isScaled || Lizzie.isMultiScreen) {
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

  public void syncOnline(String url) {
    if (onlineDialog == null) onlineDialog = new OnlineDialog(this);
    else {
      try {
        onlineDialog.stopSync();
      } catch (Exception ex) {
      }
    }

    onlineDialog.applyChangeWeb(url);
    syncLiveBoardStat();
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
    String url =
        courseFile + File.separator + Lizzie.resourceBundle.getString("Menu.introduction.fileName");
    bowser(url, Lizzie.resourceBundle.getString("LizzieFrame.introduction"), false);
  }

  public void bowser(String url, String title, boolean isYike) {
    if (browserFrame == null) {
      if (!Lizzie.config.browserInitiazed && browserInitializing != null) {
        browserInitializing.setVisible(true);
        return;
      }
      new Thread() {
        public void run() {
          try {
            browserFrame = new BrowserFrame(url, title, isYike);
          } catch (UnsupportedPlatformException
              | CefInitializationException
              | IOException
              | InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
          }
        }
      }.start();
    } else {
      browserFrame.openURL(url, title, isYike);
    }
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
                  SwingUtilities.invokeLater(
                      new Thread() {
                        public void run() {
                          if (((Lizzie.board.getHistory().getCurrentHistoryNode().isMainTrunk()
                                      && Lizzie.board
                                              .getHistory()
                                              .getCurrentHistoryNode()
                                              .getData()
                                              .moveNumber
                                          == maxMvNum)
                                  || firstSync)
                              || Lizzie.config.alwaysGotoLastOnLive) {
                            moveToMainTrunk();
                            Lizzie.board.goToMoveNumberBeyondBranch(moveNumber);
                            if (firstSync) {
                              renderVarTree(0, 0, false, false);
                              new Thread() {
                                public void run() {
                                  try {
                                    Thread.sleep(500);
                                  } catch (InterruptedException e1) {
                                    // TODO Auto-generated catch block
                                    e1.printStackTrace();
                                  }
                                  renderVarTree(0, 0, false, true);
                                }
                              }.start();
                              firstSync = false;
                            }
                          }
                          maxMvNum = moveNumber;
                          redrawTree = true;
                          Lizzie.frame.refresh();
                        }
                      });
                }
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
    shareFrame = new ShareFrame();
    shareFrame.setVisible(true);
  }

  public void batchShareSGF() {
    batchShareFrame = new BatchShareFrame();
    batchShareFrame.setVisible(true);
  }

  public void setLzSaiEngine() {
    if (EngineManager.isEmpty || !Lizzie.leelaz.isLoaded()) {
      Utils.showMsg(Lizzie.resourceBundle.getString("LizzieFrame.setParamNoEngineHint"));
      return;
    }
    if (Lizzie.leelaz.isKatago) {
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
              if (setkatarules.isVisible())
                JOptionPane.showMessageDialog(
                    setkatarules, Lizzie.resourceBundle.getString("LizzieFrame.ruleWarning"));
            }
          }
        };
    Thread thread = new Thread(runnable);
    thread.start();
  }

  public void startEngineGameDialog() {
    if (EngineManager.isEngineGame) {
      Utils.showMsg(
          Lizzie.resourceBundle.getString(
              "LizzieFrame.engineGameStopFirstHint")); // "请等待当前引擎对战结束,或手动终止对局");
      return;
    }
    if (Lizzie.frame.isPlayingAgainstLeelaz || Lizzie.frame.isAnaPlayingAgainstLeelaz) {
      Lizzie.frame.togglePonderMannul();
    }
    LizzieFrame.toolbar.enginePkBlack.setEnabled(true);
    LizzieFrame.toolbar.enginePkWhite.setEnabled(true);
    NewEngineGameDialog engineGame = new NewEngineGameDialog(this);
    GameInfo gameInfo = Lizzie.board.getHistory().getGameInfo();
    engineGame.setGameInfo(gameInfo);
    engineGame.setVisible(true);
    LizzieFrame.toolbar.resetEnginePk();
    if (engineGame.isCancelled()) {
      // Lizzie.frame.addInput();
      LizzieFrame.toolbar.chkenginePk.setSelected(false);
      LizzieFrame.toolbar.enginePkBlack.setEnabled(false);
      LizzieFrame.toolbar.enginePkWhite.setEnabled(false);
      return;
    }
  }

  public void startAnalyzeGameDialog() {
    if (Lizzie.frame.isContributing) {
      Utils.showMsg(
          Lizzie.resourceBundle.getString("Contribute.tips.contributingAndStartAnotherLizzieYzy"));
      return;
    }
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
      NewAnaGameDialog newgame = new NewAnaGameDialog(this);
      // newgame.setGameInfo(gameInfo);
      newgame.setVisible(true);
      newgame.dispose();
      if (newgame.isCancelled()) {
        if (isPondering) Lizzie.leelaz.togglePonder();
        Lizzie.frame.isAnaPlayingAgainstLeelaz = false;
        return;
      }
      LizzieFrame.toolbar.isAutoPlay = true;
      Lizzie.leelaz.isGamePaused = false;
    }
  }

  public void continueAiPlaying(
      boolean isGenmove, boolean continueNow, boolean playerIsB, boolean fromShortCut) {
    if (Lizzie.frame.isContributing) {
      Utils.showMsg(
          Lizzie.resourceBundle.getString("Contribute.tips.contributingAndStartAnotherLizzieYzy"));
      return;
    }
    if (EngineManager.isEmpty) return;
    if (isPlayingAgainstLeelaz || isAnaPlayingAgainstLeelaz) {
      stopAiPlayingAndPolicy();
    }
    if (Lizzie.config.limitMyTime)
      countDownForHuman(
          Lizzie.config.getMySaveTime(),
          Lizzie.config.getMyByoyomiSeconds(),
          Lizzie.config.getMyByoyomiTimes());
    if (isGenmove) {
      if (!Lizzie.leelaz.isThinking) {
        if (!Lizzie.config.genmoveGameNoTime) sendAiTime(true, Lizzie.leelaz, true);
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
        toolbar.setChkShowBlack(true);
        toolbar.setChkShowWhite(true);
        menu.setChkShowBlack(false);
        menu.setChkShowWhite(false);
      } else {
        toolbar.setChkShowBlack(true);
        toolbar.setChkShowWhite(true);
        menu.setChkShowBlack(true);
        menu.setChkShowWhite(true);
      }
      Lizzie.frame.updateTitle();
      Lizzie.frame.refresh();
    } else {
      if (!toolbar.chkAutoPlayTime.isSelected()
          && !toolbar.chkAutoPlayFirstPlayouts.isSelected()
          && !toolbar.chkAutoPlayPlayouts.isSelected()) {
        toolbar.txtAutoPlayTime.setText(
            String.valueOf(Math.max(1, Lizzie.config.maxGameThinkingTimeSeconds)));
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
        toolbar.setChkShowBlack(false);
        toolbar.setChkShowWhite(false);
        menu.setChkShowBlack(false);
        menu.setChkShowWhite(false);
      } else {
        toolbar.setChkShowBlack(true);
        toolbar.setChkShowWhite(true);
        menu.setChkShowBlack(true);
        menu.setChkShowWhite(true);
      }
      toolbar.chkAutoPlay.setSelected(true);
      isAnaPlayingAgainstLeelaz = true;
      toolbar.isAutoPlay = true;
      Lizzie.leelaz.anaGameResignCount = 0;
      if (Lizzie.config.UsePureNetInGame && !Lizzie.leelaz.isheatmap)
        Lizzie.leelaz.toggleHeatmap(false);
      Lizzie.leelaz.ponder();
    }
    LizzieFrame.menu.toggleDoubleMenuGameStatus();
    Lizzie.leelaz.isGamePaused = false;
    if (fromShortCut)
      Utils.showMsg(Lizzie.resourceBundle.getString("LizzieFrame.startContinueGame"));
  }

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
    g.setColor(new Color(0, 0, 0, 130));
    g.fillRect(vx, vy, vw, vh);
    if (!Lizzie.config.showVariationGraph) return;
    if (shouldShowSimpleVariation()) {
      new Thread() {
        public void run() {
          BufferedImage variationTreeBigImage = new BufferedImage(vw, vh, TYPE_INT_ARGB);
          Graphics2D g1 = (Graphics2D) variationTreeBigImage.getGraphics();
          g1.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
          g1.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
          try {
            variationTreeBig.draw(g1, 0, 0, vw, vh);
          } catch (Exception e) {
          }
          varBigX = vx;
          varBigY = vy;
          cachedVariationTreeBigImage = variationTreeBigImage;
          if (varTreeScrollPane.isVisible()) {
            varTreeScrollPane.setVisible(false);
          }
        }
      }.start();
      return;
    } else if (vw < 10 || vh < 10) {
      varTreeScrollPane.setVisible(false);
      return;
    } else if (!varTreeScrollPane.isVisible()) {
      varTreeScrollPane.setVisible(true);
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
                  new Dimension(
                      (int) (cachedVarImage2.getWidth() / Lizzie.javaScaleFactor),
                      (int) (cachedVarImage2.getHeight() / Lizzie.javaScaleFactor)));
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

  public void renderVarTreeCur() {
    if (shouldShowSimpleVariation()) return;
    BoardHistoryNode cur = Lizzie.board.getHistory().getCurrentHistoryNode();
    if (cur == Lizzie.board.getHistory().getStart()) return;
    Graphics2D g = (Graphics2D) cachedVarImage2.getGraphics();
    g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

    if (Lizzie.config.showCommentNodeColor && !cur.getData().comment.isEmpty()) {
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
    g.setColor(Color.BLACK);
    g.fillOval(
        tree_curposx + (tree_DOT_DIAM + tree_diff - tree_CENTER_DIAM) / 2,
        tree_posy + (tree_DOT_DIAM + tree_diff - tree_CENTER_DIAM) / 2,
        tree_CENTER_DIAM,
        tree_CENTER_DIAM);
    g.dispose();
  }

  //  private Color reverseColor(Color color) {
  //    // System.out.println("color=="+color);
  //    int r = color.getRed();
  //    int g = color.getGreen();
  //    int b = color.getBlue();
  //    int r_ = 255 - r;
  //    int g_ = 255 - g;
  //    int b_ = 255 - b;
  //    Color newColor = new Color(r_, g_, b_);
  //    return newColor;
  //  }

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
                new Dimension(
                    (int) (cachedVarImage2.getWidth() / Lizzie.javaScaleFactor),
                    (int) (cachedVarImage2.getHeight() / Lizzie.javaScaleFactor)));
            varTreePane.revalidate();
            canDrawCurColor = true;
            JScrollBar jScrollBarW = varTreeScrollPane.getHorizontalScrollBar();
            if (varTreeCurX <= varTreeW / 2
                || Lizzie.board.getHistory().getCurrentHistoryNode()
                    == Lizzie.board.getHistory().getStart()) jScrollBarW.setValue(0);
            else {
              jScrollBarW.setValue(
                  (int)
                      ((((varTreeCurX - varTreeW / 2f) / varTreeMaxX) * jScrollBarW.getMaximum())));
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
                        ((((varTreeCurY - varTreeH / 2f) / varTreeMaxY)
                            * jScrollBarH.getMaximum()))); // 设置垂直滚动条位置
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

  //  public void handleAfterDrawGobanBottom() {
  //    if (Lizzie.board.getHistory().getGameInfo().getPlayerWhite().equals("")
  //        && Lizzie.board.getHistory().getGameInfo().getPlayerBlack().equals("")) {
  //      boardRenderer.changedName = true;
  //    //  boardRenderer.emptyName = true;
  //    }
  //    refresh();
  //  }
  //
  //  public void handleAfterDrawGobanBottomSub() {
  //    if (Lizzie.board.getHistory().getGameInfo().getPlayerWhite().equals("")
  //        && Lizzie.board.getHistory().getGameInfo().getPlayerBlack().equals("")) {
  //      boardRenderer2.changedName = true;
  //    //  boardRenderer2.emptyName = true;
  //    }
  //  }

  public void clearEstimate() {
    boardRenderer.removeEstimateImage();
    if (floatBoard != null) floatBoard.boardRenderer.removeEstimateImage();
  }

  public void clearKataEstimate() {
    boardRenderer.removeKataEstimateImage();
    if (Lizzie.config.showSubBoard) subBoardRenderer.removeKataEstimateImage();
    if (Lizzie.config.isDoubleEngineMode()) boardRenderer2.removeKataEstimateImage();
    if (floatBoard != null) floatBoard.boardRenderer.removeKataEstimateImage();
    if (estimateResults != null && estimateResults.isVisible()) estimateResults.repaint();
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
      Lizzie.frame.refresh();
    } else {
      if (Lizzie.frame.isCounting) {
        clearKataEstimate();
        Lizzie.frame.refresh();
        Lizzie.frame.isCounting = false;
        estimateResults.setVisible(false);
      } else {
        Lizzie.frame.countstones(true);
      }
    }
  }

  public void togglePonderMannul() {
    if (!stopAiPlayingAndPolicy()) Lizzie.leelaz.togglePonder();
  }

  public void drawKataEstimate(Leelaz engine, ArrayList<Double> tempcount) {
    if (isInScoreMode || !isShowingHeatmap) return;
    if ((Lizzie.leelaz.iskataHeatmapShowOwner && Lizzie.config.showPureEstimateBySize)) {
      if (Lizzie.config.isDoubleEngineMode()) {
        if (engine == Lizzie.leelaz)
          LizzieFrame.boardRenderer.drawKataEstimateBySize(tempcount, false);
        if (Lizzie.leelaz2 != null && engine == Lizzie.leelaz2)
          LizzieFrame.boardRenderer2.drawKataEstimateBySize(tempcount, false);
      } else {
        LizzieFrame.boardRenderer.drawKataEstimateBySize(tempcount, false);
        if (floatBoard != null && floatBoard.isVisible())
          floatBoard.boardRenderer.drawKataEstimateBySize(tempcount, false);
      }

      if (!Lizzie.config.isDoubleEngineMode()) {
        if (Lizzie.config.showSubBoard)
          LizzieFrame.subBoardRenderer.drawKataEstimateBySize(tempcount, false);
        if (independentSubBoard != null && independentSubBoard.isVisible())
          independentSubBoard.subBoardRenderer.drawKataEstimateBySize(tempcount, false);
      }
    } else {
      if (Lizzie.config.isDoubleEngineMode()) {
        if (engine == Lizzie.leelaz)
          LizzieFrame.boardRenderer.drawKataEstimateByTransparent(tempcount, false, true);
        if (Lizzie.leelaz2 != null && engine == Lizzie.leelaz2)
          LizzieFrame.boardRenderer2.drawKataEstimateByTransparent(tempcount, false, true);
      } else {
        LizzieFrame.boardRenderer.drawKataEstimateByTransparent(tempcount, false, true);
        if (floatBoard != null && floatBoard.isVisible())
          floatBoard.boardRenderer.drawKataEstimateByTransparent(tempcount, false, true);
      }
      if (!Lizzie.config.isDoubleEngineMode()) {
        if (Lizzie.config.showSubBoard)
          LizzieFrame.subBoardRenderer.drawKataEstimateByTransparent(tempcount, false, true);
        if (independentSubBoard != null && independentSubBoard.isVisible())
          independentSubBoard.subBoardRenderer.drawKataEstimateByTransparent(
              tempcount, false, true);
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
        autoSaveFile =
            new File(courseFile + File.separator + "MyGames" + File.separator + fileName + ".sgf");
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
    toolbar.isPkStop = false;
    Lizzie.leelaz.isGamePaused = false;
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
      Lizzie.engineManager.clearPlayingAgainstHumanEngineCountDown();
      Lizzie.engineManager.stopCountDown();
      setAsMain();
      restoreWRN(true);
      Lizzie.leelaz.setGameStatus(false);
      if (Lizzie.config.autoSavePlayedGame) autoSavePlayedGame();
      Lizzie.frame.isPlayingAgainstLeelaz = false;
      Lizzie.leelaz.isThinking = false;
      Lizzie.leelaz.notPondering();
      boardRenderer.removeblock();
      if (Lizzie.config.isDoubleEngineMode()) {
        boardRenderer2.removeblock();
      }
      toolbar.setChkShowBlack(true);
      toolbar.setChkShowWhite(true);
      menu.setChkShowBlack(true);
      menu.setChkShowWhite(true);
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
      LizzieFrame.toolbar.chkAutoPlay.setSelected(false);
      LizzieFrame.toolbar.isAutoPlay = false;
      LizzieFrame.toolbar.chkAutoPlayBlack.setSelected(false);
      LizzieFrame.toolbar.chkAutoPlayWhite.setSelected(false);
      toolbar.setChkShowBlack(true);
      toolbar.setChkShowWhite(true);
      menu.setChkShowBlack(true);
      menu.setChkShowWhite(true);
      Lizzie.leelaz.anaGameResignCount = 0;
      Lizzie.leelaz.notPondering();
      boardRenderer.removeblock();
      if (Lizzie.config.isDoubleEngineMode()) {
        boardRenderer2.removeblock();
      }
    }
    if (Lizzie.config.isAutoAna) {
      if (Lizzie.config.exitAutoAnalyzeByPause) {
        if (Lizzie.config.exitAutoAnalyzeTip) {
          Object[] options = new Object[2];
          options[0] = Lizzie.resourceBundle.getString("LizzieFrame.autoAnalyze.notShowAgain");
          options[1] = Lizzie.resourceBundle.getString("LizzieFrame.confirm");
          Object defaultOption = Lizzie.resourceBundle.getString("LizzieFrame.confirm");
          JOptionPane optionPane =
              new JOptionPane(
                  new JFontLabel(
                      Lizzie.resourceBundle.getString("LizzieFrame.autoAnalyze.tip.content")),
                  JOptionPane.INFORMATION_MESSAGE,
                  JOptionPane.YES_NO_OPTION,
                  null,
                  options,
                  defaultOption);
          JDialog dialog =
              optionPane.createDialog(
                  this, Lizzie.resourceBundle.getString("LizzieFrame.autoAnalyze.tip.title"));
          dialog.setVisible(true);
          dialog.dispose();
          if (optionPane.getValue().equals(options[0])) {
            Lizzie.config.exitAutoAnalyzeTip = false;
            Lizzie.config.uiConfig.put("exit-auto-analyze-tip", Lizzie.config.exitAutoAnalyzeTip);
          }
        }
        Lizzie.config.isAutoAna = false;
        LizzieFrame.toolbar.chkAutoAnalyse.setSelected(false);
        Lizzie.leelaz.notPondering();
      }
    }
    LizzieFrame.menu.toggleDoubleMenuGameStatus();
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
                topPanel.setBounds(
                    0, 0, width, Config.menuHeight + (Lizzie.config.useJavaLooks ? 1 : 0));
                int curHeight = topPanel.getPreferredSize().height + 8;
                topPanelHeight = Config.menuHeight;
                if (curHeight / Config.menuHeight > 1) {
                  topPanelHeight = (curHeight / Config.menuHeight) * Config.menuHeight;
                  topPanel.setBounds(
                      0, 0, width, topPanelHeight + (Lizzie.config.useJavaLooks ? 1 : 0));
                }
                topPanel.revalidate();
              } else {
                topPanel.setBounds(
                    0, 0, 9999, Config.menuHeight + (Lizzie.config.useJavaLooks ? 1 : 0));
                topPanelHeight = Config.menuHeight;
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
            if (Lizzie.frame.getExtendedState() != Frame.MAXIMIZED_BOTH) {
              noneMaxX = Lizzie.frame.getX();
              noneMaxY = Lizzie.frame.getY();
              noneMaxWidth = Lizzie.frame.getWidth();
              noneMaxHeight = Lizzie.frame.getHeight();
            }
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
    if (Config.isScaled || Lizzie.isMultiScreen) {
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
    if (Config.isScaled || Lizzie.isMultiScreen) {
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
    if (Lizzie.config.isFloatBoardMode()) {
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
    isMouseOver = true;
    curSuggestionMoveOrderByNumber = index;
    mouseOverCoordinate =
        Board.convertNameToCoordinates(
            Lizzie.board.getHistory().getData().bestMoves.get(index).coordinate);
  }

  private void handleTableClick(int row, int col) {
    LizzieFrame.boardRenderer.startNormalBoard();
    if (listTable.getValueAt(row, 1).toString().startsWith("pass")) return;
    if (clickOrder != -1
        && selectedorder >= 0
        && Board.convertNameToCoordinates(listTable.getValueAt(row, 1).toString())[0]
            == Lizzie.frame.suggestionclick[0]
        && Board.convertNameToCoordinates(listTable.getValueAt(row, 1).toString())[1]
            == Lizzie.frame.suggestionclick[1]) {
      Lizzie.frame.suggestionclick = LizzieFrame.outOfBoundCoordinate;
      Lizzie.frame.mouseOverCoordinate = LizzieFrame.outOfBoundCoordinate;
      LizzieFrame.boardRenderer.clearBranch();
      selectedorder = -1;
      clickOrder = -1;
      currentRow = -1;
      isMouseOver = true;
      Lizzie.frame.refresh();
    } else {
      clickOrder = row;
      selectedorder = row;
      currentRow = row;
      int[] coords = Board.convertNameToCoordinates(listTable.getValueAt(row, 1).toString());
      Lizzie.frame.mouseOverCoordinate = coords;
      isMouseOver = true;
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
      int[] coords = Board.convertNameToCoordinates(listTable.getValueAt(row, 1).toString());
      Lizzie.frame.suggestionclick = coords;
      Lizzie.frame.mouseOverCoordinate = LizzieFrame.outOfBoundCoordinate;
      Lizzie.frame.refresh();
      selectedorder = row;
    } else {
      Lizzie.frame.suggestionclick = LizzieFrame.outOfBoundCoordinate;
      Lizzie.frame.refresh();
      selectedorder = -1;
    }
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
        if (EngineManager.isEngineGame && Lizzie.config.showPreviousBestmovesInEngineGame) {
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
                if (Board.convertNameToCoordinates(move.coordinate)[0] == coords[0]
                    && Board.convertNameToCoordinates(move.coordinate)[1] == coords[1]) {
                  if (move.order == 0) {
                    move.isNextMove = true;
                    move.bestWinrate = data2.get(0).winrate;
                    move.bestScoreMean = data2.get(0).scoreMean;
                  } else {
                    if (data2.size() > 0 && !hasData && !next.getData().bestMoves.isEmpty()) {
                      if (next.getData().getPlayouts() > move.playouts) {
                        MoveData curMove = new MoveData();
                        curMove.playouts = next.getData().getPlayouts();
                        curMove.coordinate = Board.convertCoordinatesToName(coords[0], coords[1]);
                        curMove.winrate = 100.0 - next.getData().winrate;
                        curMove.scoreMean = -next.getData().scoreMean;
                        curMove.order = move.order;
                        curMove.isNextMove = true;
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
                    curMove.order = move.order;
                    curMove.isNextMove = true;
                    curMove.bestWinrate = data2.get(0).winrate;
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
                curMove.coordinate = Board.convertCoordinatesToName(coords[0], coords[1]);
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
        if (column == 0) return Lizzie.resourceBundle.getString("AnalysisFrame.column1"); // "序号";
        if (column == 1) return Lizzie.resourceBundle.getString("AnalysisFrame.column2"); // "坐标";
        if (column == 2)
          return Lizzie.resourceBundle.getString("LizzieFrame.listColumn2"); // "胜率(%)";
        if (column == 3) return Lizzie.resourceBundle.getString("AnalysisFrame.column5"); // "计算量";
        if (column == 4)
          return Lizzie.resourceBundle.getString("LizzieFrame.listColumn4"); // "占比(%)";
        if (column == 5) return Lizzie.resourceBundle.getString("AnalysisFrame.column8"); // "目差";
        return "";
      }

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
              return "\n" + Lizzie.resourceBundle.getString("AnalysisFrame.actual") + "\n";
            else if (data.isNextMove)
              return data.order
                  + 1
                  + "("
                  + Lizzie.resourceBundle.getString("AnalysisFrame.actual")
                  + ")";
            if (data.coordinate.startsWith("pas")) return "Pass";
            return data.order + 1;
          case 1:
            return data.coordinate;
          case 2:
            if (data.isNextMove) {
              if (data.order != 0) {
                double diff = data.winrate - data.bestWinrate;
                return (diff > 0 ? "↑" : "↓")
                    + String.format(Locale.ENGLISH, "%.1f", diff)
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
            return Utils.getPlayoutsString(data.playouts);
          case 4:
            return String.format(
                Locale.ENGLISH, "%.1f", (double) data.playouts * 100 / totalPlayouts);
          case 5:
            double score = data.scoreMean;
            if (EngineManager.isEngineGame && EngineManager.engineGameInfo.isGenmove) {
              if (!Lizzie.board.getHistory().isBlacksTurn()) {
                if (Lizzie.config.showKataGoScoreLeadWithKomi) {
                  score = score + Lizzie.board.getHistory().getGameInfo().getKomi();
                }
              } else {
                if (Lizzie.config.showKataGoScoreLeadWithKomi) {
                  score = score - Lizzie.board.getHistory().getGameInfo().getKomi();
                }
                if (Lizzie.config.winrateAlwaysBlack) {
                  score = -score;
                }
              }
            } else {
              if (Lizzie.board.getHistory().isBlacksTurn()) {
                if (Lizzie.config.showKataGoScoreLeadWithKomi) {
                  score = score + Lizzie.board.getHistory().getGameInfo().getKomi();
                }
              } else {
                if (Lizzie.config.showKataGoScoreLeadWithKomi) {
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
                  + String.format(Locale.ENGLISH, "%.1f", diff)
                  + "("
                  + String.format(Locale.ENGLISH, "%.1f", score)
                  + ")";
            } else return String.format(Locale.ENGLISH, "%.1f", score);
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
        coords = Board.convertNameToCoordinates(coordsName);
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
        JLabel label =
            (JLabel) super.getTableCellRendererComponent(table, value, false, false, row, column);
        if (isNextMove) label.setToolTipText(value.toString());
        else label.setToolTipText(null);
        return label;
      } else {
        isSelect = false;
        isChanged = false;
        JLabel label =
            (JLabel) super.getTableCellRendererComponent(table, value, false, false, row, column);
        if (isNextMove) label.setToolTipText(value.toString());
        else label.setToolTipText(null);
        return label;
      }
    }

    @Override
    public void paintComponent(Graphics g) {
      if (isPlayoutPercents) {
        Graphics2D g2 = (Graphics2D) g;
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
            if (diff <= Lizzie.config.winLossThreshold5
                || scoreDiff <= Lizzie.config.scoreLossThreshold5)
              setBackground(new Color(85, 25, 80, 120));
            else if (diff <= Lizzie.config.winLossThreshold4
                || scoreDiff <= Lizzie.config.scoreLossThreshold4)
              setBackground(new Color(208, 16, 19, 100));
            else if (diff <= Lizzie.config.winLossThreshold3
                || scoreDiff <= Lizzie.config.scoreLossThreshold3)
              setBackground(new Color(200, 140, 50, 100));
            else if (diff <= Lizzie.config.winLossThreshold2
                || scoreDiff <= Lizzie.config.scoreLossThreshold2)
              setBackground(new Color(180, 180, 0, 100));
            else if (diff <= Lizzie.config.winLossThreshold1
                || scoreDiff <= Lizzie.config.scoreLossThreshold1)
              setBackground(new Color(140, 202, 34, 100));
            else setBackground(new Color(0, 180, 0, 100));
          } else {
            if (diff <= Lizzie.config.winLossThreshold5
                || scoreDiff <= Lizzie.config.scoreLossThreshold5)
              setBackground(new Color(85, 25, 80, 70));
            else if (diff <= Lizzie.config.winLossThreshold4
                || scoreDiff <= Lizzie.config.scoreLossThreshold4)
              setBackground(new Color(208, 16, 19, 50));
            else if (diff <= Lizzie.config.winLossThreshold3
                || scoreDiff <= Lizzie.config.scoreLossThreshold3)
              setBackground(new Color(200, 140, 50, 50));
            else if (diff <= Lizzie.config.winLossThreshold2
                || scoreDiff <= Lizzie.config.scoreLossThreshold2)
              setBackground(new Color(180, 180, 0, 50));
            else if (diff <= Lizzie.config.winLossThreshold1
                || scoreDiff <= Lizzie.config.scoreLossThreshold1)
              setBackground(new Color(140, 202, 34, 50));
            else setBackground(new Color(0, 180, 0, 60));
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
    if (Config.isScaled || Lizzie.isMultiScreen) {
      if (Lizzie.config.isFloatBoardMode()) {
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
      } else {
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
      }
    } else {
      if (Lizzie.config.isFloatBoardMode()) {
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
      } else {
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
      }
    }
  }

  public Image zoomImage(BufferedImage src, int w, int h) {
    double wr = 0, hr = 0;
    Image Itemp = src.getScaledInstance(w, h, Image.SCALE_SMOOTH);
    wr = w * 1.0 / src.getWidth();
    hr = h * 1.0 / src.getHeight();
    AffineTransformOp ato = new AffineTransformOp(AffineTransform.getScaleInstance(wr, hr), null);
    Itemp = ato.filter(src, null);
    return Itemp;
  }

  public void deleteTempGame(int index) {
    ArrayList<TempGameData> data = getSaveGameList();
    File file = new File("save" + File.separator + "game" + index + ".bmp");
    if (file.exists() && file.isFile()) file.delete();
    File file2 = new File("save" + File.separator + "game" + index + ".sgf");
    if (file2.exists() && file2.isFile()) file2.delete();
    for (int i = index + 1; i <= data.size(); i++) {
      File oldfile = new File("save" + File.separator + "game" + i + ".bmp");
      File newfile = new File("save" + File.separator + "game" + (i - 1) + ".bmp");
      if (oldfile.exists()) {
        oldfile.renameTo(newfile);
      }
    }
    for (int i = index + 1; i <= data.size(); i++) {
      File oldfile = new File("save" + File.separator + "game" + i + ".sgf");
      File newfile = new File("save" + File.separator + "game" + (i - 1) + ".sgf");
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
      File file = new File("save" + File.separator + "game" + index + ".bmp");
      if (file.exists() && file.isFile()) file.delete();
      File file2 = new File("save" + File.separator + "game" + index + ".sgf");
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
    data.get(index - 1).curMoveNumer = Lizzie.board.getCurrentMovenumber();
    data.get(index - 1).moves =
        Lizzie.board.moveListToString(Lizzie.board.getmovelistForSaveLoad());
    saveTempGame(data);
    File file = new File("save" + File.separator + "game" + index + ".bmp");
    try {
      SGFParser.save(Lizzie.board, "save" + File.separator + "game" + index + ".sgf");
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
    newData.curMoveNumer = Lizzie.board.getCurrentMovenumber();
    newData.moves = Lizzie.board.moveListToString(Lizzie.board.getMoveList());
    data.add(newData);
    saveTempGame(data);
    File file = new File("save" + File.separator + "game" + index + ".bmp");
    try {
      SGFParser.save(Lizzie.board, "save" + File.separator + "game" + index + ".sgf");
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
        "save-auto-game-move-number" + index, Lizzie.board.getCurrentMovenumber());
    Lizzie.config.saveBoardConfig.put(
        "save-auto-game-move-list" + index,
        Lizzie.board.moveListToString(Lizzie.board.getmovelistForSaveLoad()));
    if (index == 1) Lizzie.config.saveBoardConfig.put("save-auto-game-index2", -1);
    File file = new File("save" + File.separator + "autoGame" + index + ".bmp");
    try {
      SGFParser.save(Lizzie.board, "save" + File.separator + "autoGame" + index + ".sgf", true);
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
            (isAutoSave ? "save" + File.separator + "autoGame" : "save" + File.separator + "game")
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
                    (isAutoSave
                            ? "save" + File.separator + "autoGame"
                            : "save" + File.separator + "game")
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
            new Thread() {
              public void run() {
                try {
                  Thread.sleep(500);
                } catch (InterruptedException e) {
                  // TODO Auto-generated catch block
                  e.printStackTrace();
                }
                if (bigBoardPanel != null && bigBoardPanel.isVisible())
                  bigBoardPanel.setVisible(false);
              }
            }.start();
            int ret =
                JOptionPane.showConfirmDialog(
                    Lizzie.frame,
                    Lizzie.resourceBundle.getString("LizzieFrame.recordExists"),
                    Lizzie.resourceBundle.getString("LizzieFrame.warning"),
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
        new JButton(Lizzie.resourceBundle.getString("LizzieFrame.saveAndLoad.close")); // ("关闭");

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
                            Lizzie.resourceBundle.getString(
                                "LizzieFrame.saveAndLoad.deleteAllWarining"),
                            Lizzie.resourceBundle.getString("LizzieFrame.warning"),
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
                                  ? "save" + File.separator + "autoGame"
                                  : "save" + File.separator + "game")
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
    tempGamePanelAll.repaint();
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
            (isAutoSave ? "save" + File.separator + "autoGame" : "save" + File.separator + "game")
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
                      (isAutoSave
                              ? "save" + File.separator + "autoGame"
                              : "save" + File.separator + "game")
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
                  ? (Lizzie.resourceBundle.getString("Byoyomi.time")
                      + this.leftMinuts
                      + ":"
                      + this.leftSeconds
                      + " ")
                  : "")
              + (this.byoSeconds >= 0
                  ? (" "
                      + Lizzie.resourceBundle.getString("Byoyomi.byoyomi")
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

  public void tryToResetByoTime() {
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
            if (Lizzie.board.getHistory().isEmptyBoard()
                && Lizzie.board.getHistory().getGameInfo().getHandicap() > 0) return;
            if (playerIsBlack && !Lizzie.board.getHistory().isBlacksTurn()) return;
            if (!playerIsBlack && Lizzie.board.getHistory().isBlacksTurn()) return;
            if (Lizzie.leelaz.isGamePaused) return;
            if (!Lizzie.leelaz.isLoaded()) return;
            if (leftSeconds > 0) {
              leftSeconds--;
            } else if (leftMinuts > 0) {
              leftMinuts--;
              leftSeconds = 59;
            } else if (byoSeconds >= 0) {
              if (byoSeconds <= 10) {
                int seconds = byoSeconds - 1;
                Runnable runnable =
                    new Runnable() {
                      public void run() {
                        if (seconds >= 0) Utils.playByoyomi(seconds);
                      }
                    };
                Thread thread = new Thread(runnable);
                thread.start();
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
                    .setResult(
                        Lizzie.resourceBundle.getString("Byoyomi.timeOutBlack")); // ("白胜,黑超时");
              else
                Lizzie.board
                    .getHistory()
                    .getGameInfo()
                    .setResult(
                        Lizzie.resourceBundle.getString("Byoyomi.timeOutWhite")); // ("黑胜,白超时");
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
    // 增加6=数字
    this.markupType = type;
  }

  public boolean tryToRemoveMarkup(int x, int y) {
    // TODO Auto-generated method stub
    if (isMarkuping) {
      Optional<int[]> boardCoordinates;
      if (Lizzie.config.isThinkingMode()) {
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
      if (Lizzie.config.isThinkingMode()) {
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
        lastNumLabel = 0;
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
                      if (markupType == 7) {
                        if ("LB".equals(key) && moves.length > 1) {
                          // Number
                          try {
                            lastNumLabel = Math.max(lastNumLabel, Integer.parseInt(moves[1]));
                          } catch (NumberFormatException e) {
                          }
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
        } else if (markupType == 7) {
          lastNumLabel = lastNumLabel + 1;
          String value = SGFParser.asCoord(coords) + ":" + lastNumLabel;
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

  public void flashAnalyzeGameBatch(int firstMove, int lastMove, boolean isAllBranches) {
    // TODO Auto-generated method stub
    Lizzie.config.analysisStartMove = firstMove;
    Lizzie.config.analysisEndMove = lastMove;
    isBatchAnalysisMode = true;
    if (analysisTable != null) {
      analysisTable.resetAnalysisMode();
    }
    flashAnalyzeGame(false, isAllBranches);
  }

  public void flashAutoAnaSaveAndLoad() {
    if (Lizzie.frame.Batchfiles.size() > (Lizzie.frame.BatchAnaNum)) {
      if (Lizzie.leelaz.autoAnalysed) SGFParser.appendAiScoreBlunder();
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
              + Lizzie.resourceBundle.getString("Leelaz.analyzed")
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
      analysisEngine.waitFrame.setVisible(false);
      if (Lizzie.frame.Batchfiles.size() > (Lizzie.frame.BatchAnaNum + 1)) {
        double komi = Lizzie.board.getHistory().getGameInfo().getKomi();
        toolbar.loadAutoBatchFile();
        Lizzie.leelaz.komi(komi);
        flashAnalyzeGameBatch(
            LizzieFrame.toolbar.firstMove,
            LizzieFrame.toolbar.lastMove,
            Lizzie.config.analysisRecentIsAllBranches);
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
        Utils.showMsg(Lizzie.resourceBundle.getString("Leelaz.batchAutoAnalyzeComplete"));
        if (Lizzie.config.analysisAutoQuit) {
          analysisEngine.normalQuit();
        }
      }
    }
  }

  public void flashAnalyzeGame(boolean isAllGame, boolean isAllBranches) {
    Lizzie.config.analysisRecentIsPartGame = isAllGame;
    Lizzie.config.analysisRecentIsAllBranches = isAllBranches;
    if (analysisEngine == null
        || analysisEngine.useJavaSSH && analysisEngine.javaSSHClosed
        || (!analysisEngine.useJavaSSH
            && (analysisEngine.process == null || !analysisEngine.process.isAlive()))) {
      try {
        analysisEngine = new AnalysisEngine(false);
        if (isAllBranches) analysisEngine.startRequestAllBranches();
        else
          analysisEngine.startRequest(
              isAllGame ? -1 : Lizzie.config.analysisStartMove,
              isAllGame ? -1 : Lizzie.config.analysisEndMove);
      } catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    } else {
      if (isAllBranches) analysisEngine.startRequestAllBranches();
      else
        analysisEngine.startRequest(
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
    if (LizzieFrame.boardRenderer.incrementDisplayedBranchLength(movesToAdvance)) {
      Lizzie.frame.refresh();
      return;
    }
    if (Lizzie.config.isDoubleEngineMode()
        && LizzieFrame.boardRenderer2.incrementDisplayedBranchLength(movesToAdvance)) {
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
    if (!EngineManager.isEngineGame && !EngineManager.isPreEngineGame) {
      for (int i = 0; i < movesToAdvance; i++) Lizzie.board.nextMove(false);
      Lizzie.board.clearAfterMove();
      Lizzie.frame.refresh();
    }
  }

  public static void redoNoRefresh(int movesToAdvance) {
    if (Lizzie.frame.isPlayingAgainstLeelaz || Lizzie.frame.isAnaPlayingAgainstLeelaz) {
      return;
    }
    if (LizzieFrame.boardRenderer.incrementDisplayedBranchLength(movesToAdvance)) {
      return;
    }
    if (Lizzie.config.isDoubleEngineMode()
        && LizzieFrame.boardRenderer2.incrementDisplayedBranchLength(movesToAdvance)) {
      return;
    }
    if (Lizzie.frame.independentMainBoard != null) {
      if (Lizzie.frame.independentMainBoard.boardRenderer.incrementDisplayedBranchLength(
          movesToAdvance)) {
        return;
      }
    }
    if (!EngineManager.isEngineGame && !EngineManager.isPreEngineGame) {
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
    if (Lizzie.config.isDoubleEngineMode() && boardRenderer2.isShowingBranch()) {
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
    if (!EngineManager.isEngineGame && !EngineManager.isPreEngineGame) {
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
    if (Lizzie.config.isDoubleEngineMode() && boardRenderer2.isShowingBranch()) {
      Lizzie.frame.doBranch(-movesToAdvance);
      return;
    }
    if (Lizzie.frame.independentMainBoard != null) {
      if (Lizzie.frame.independentMainBoard.boardRenderer.isShowingBranch()) {
        Lizzie.frame.independentMainBoard.doBranch(-movesToAdvance);
        return;
      }
    }
    if (!EngineManager.isEngineGame && !EngineManager.isPreEngineGame) {
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
      Utils.showMsg(Lizzie.resourceBundle.getString("menu.setFrameSizeAlart"));
    Utils.showMsg(Lizzie.resourceBundle.getString("menu.setFrameSizeRestart"));
  }

  //  public void processMiddleClickOnWinrateGraph(MouseEvent e) {
  //    // TODO Auto-generated method stub
  //    int x = Utils.zoomOut(e.getX());
  //    int y = Utils.zoomOut(e.getY());
  //    if (grx <= x && x <= grx + grw && gry <= y && y <= gry + grh)
  //      Lizzie.config.toggleLargeWinrate();
  //  }

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

  public static void openSuggestionInfoCustom(Window owner) {
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
    Lizzie.board.saveListForEdit();
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

  public void startTemporaryBoard() {
    if (isInTemporaryBoard) return;
    isInTemporaryBoard = true;
    tempShowBlack = Lizzie.config.showBlackCandidates;
    tempShowWhite = Lizzie.config.showWhiteCandidates;
    toolbar.setChkShowBlack(false);
    toolbar.setChkShowWhite(false);
    menu.setChkShowBlack(false);
    menu.setChkShowWhite(false);
    boardRenderer.clearAfterMove();
    if (independentMainBoard != null) independentMainBoard.boardRenderer.clearAfterMove();
  }

  public void stopTemporaryBoardMaybe() {
    if (isInTemporaryBoard) stopTemporaryBoard();
  }

  public void stopTemporaryBoard() {
    toolbar.setChkShowBlack(tempShowBlack);
    toolbar.setChkShowWhite(tempShowWhite);
    menu.setChkShowBlack(tempShowWhite);
    menu.setChkShowWhite(tempShowWhite);
    isInTemporaryBoard = false;
  }

  public void clearSelectImage() {
    // TODO Auto-generated method stub
    LizzieFrame.boardRenderer.removeSelectedRect();
    if (independentMainBoard != null) independentMainBoard.boardRenderer.removeSelectedRect();
  }

  class BlunderTableCellRenderer extends DefaultTableCellRenderer {
    public Component getTableCellRendererComponent(
        JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
      String coordStr = table.getValueAt(row, 1).toString();
      int[] coords = Board.convertNameToCoordinates(coordStr);
      if (coords[0] == Lizzie.frame.clickbadmove[0] && coords[1] == Lizzie.frame.clickbadmove[1]) {
        setBackground(new Color(238, 221, 130));
      } else setBackground(blunderBackground);
      try {
        double diffWinrate =
            Float.parseFloat(
                table
                    .getValueAt(row, 2)
                    .toString()
                    .substring(0, table.getValueAt(row, 2).toString().length() - 1));
        if (Lizzie.board.isKataBoard || Lizzie.leelaz.isKatago) {
          double scoreDiff =
              Float.parseFloat(
                  table
                      .getValueAt(row, 3)
                      .toString()
                      .substring(0, table.getValueAt(row, 3).toString().length() - 1));
          if (column == 3) {
            if (scoreDiff >= 1.5) setForeground(new Color(0, 170, 170));
            else if (scoreDiff <= Lizzie.config.scoreLossThreshold5)
              setForeground(new Color(165, 25, 160));
            else if (scoreDiff <= Lizzie.config.scoreLossThreshold4)
              setForeground(new Color(175, 16, 19));
            else if (scoreDiff <= Lizzie.config.scoreLossThreshold3)
              setForeground(new Color(105, 162, 34));
            else if (scoreDiff <= Lizzie.config.scoreLossThreshold2)
              setForeground(new Color(150, 150, 0));
            else if (scoreDiff <= Lizzie.config.scoreLossThreshold1)
              setForeground(new Color(180, 120, 45));
            else setForeground(new Color(0, 150, 0));
          } else if (column == 2) {
            if (diffWinrate >= 3) setForeground(new Color(0, 170, 170));
            else if (diffWinrate <= Lizzie.config.winLossThreshold5)
              setForeground(new Color(165, 25, 160));
            else if (diffWinrate <= Lizzie.config.winLossThreshold4)
              setForeground(new Color(175, 16, 19));
            else if (diffWinrate <= Lizzie.config.winLossThreshold3)
              setForeground(new Color(105, 162, 34));
            else if (diffWinrate <= Lizzie.config.winLossThreshold2)
              setForeground(new Color(150, 150, 0));
            else if (diffWinrate <= Lizzie.config.winLossThreshold1)
              setForeground(new Color(180, 120, 45));
            else setForeground(new Color(0, 150, 0));
          } else setForeground(blunderForeground);
        } else {
          if (column == 2) {
            if (diffWinrate >= 3) setForeground(new Color(0, 170, 170));
            else if (diffWinrate <= Lizzie.config.winLossThreshold5)
              setForeground(new Color(165, 25, 160));
            else if (diffWinrate <= Lizzie.config.winLossThreshold4)
              setForeground(new Color(175, 16, 19));
            else if (diffWinrate <= Lizzie.config.winLossThreshold3)
              setForeground(new Color(105, 162, 34));
            else if (diffWinrate <= Lizzie.config.winLossThreshold2)
              setForeground(new Color(150, 150, 0));
            else if (diffWinrate <= Lizzie.config.winLossThreshold1)
              setForeground(new Color(180, 120, 45));
            else setForeground(new Color(0, 150, 0));
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
        Lizzie.resourceBundle.getString("LizzieFrame.aboutAnalyzeGenmoveInfo"),
        Lizzie.resourceBundle.getString("LizzieFrame.aboutAnalyzeGenmoveInfoTitle"),
        this);
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
      estimateResults.setVisible(false);
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
    if (Lizzie.config.isFloatBoardMode() && independentMainBoard != null)
      this.independentMainBoard.boardRenderer.drawScore(boardGroupInfo);
    else boardRenderer.drawScore(boardGroupInfo);
    this.refresh();
  }

  public void clearScore() {
    // TODO Auto-generated method stub
    if (Lizzie.config.isFloatBoardMode() && independentMainBoard != null)
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
    SetCustomMode setCustomMode = new SetCustomMode(index, true, this);
    setCustomMode.setVisible(true);
  }

  public void visualizedPanelSettings() {
    SetCustomMode setCustomMode = new SetCustomMode(-1, false, this);
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
      Lizzie.config.txtKataEnginePDA = String.valueOf(pda);
    }
    if (!Lizzie.config.autoLoadKataEngineWRN) {
      if (wrn == 0) {
        Lizzie.config.chkKataEngineWRN = false;
        Lizzie.config.txtKataEngineWRN = "0";
      } else {
        Lizzie.config.chkKataEngineWRN = true;
        Lizzie.config.txtKataEngineWRN = String.valueOf(wrn);
      }
    } else {
      if (wrn != 0) {
        Lizzie.config.chkKataEngineWRN = true;
        Lizzie.config.txtKataEngineWRN = String.valueOf(wrn);
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
      if ((EngineManager.isPreEngineGame || EngineManager.isEngineGame || isAnaPlayingAgainstLeelaz)
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
            if (Lizzie.engineManager.engineList.get(EngineManager.engineGameInfo.firstEngineIndex)
                .isKatago) {
              Lizzie.engineManager
                  .engineList
                  .get(EngineManager.engineGameInfo.firstEngineIndex)
                  .sendCommand("kata-set-param analysisWideRootNoise 0");
              Lizzie.engineManager.engineList.get(EngineManager.engineGameInfo.firstEngineIndex)
                      .wrn =
                  0;
            }
            if (Lizzie.engineManager.engineList.get(EngineManager.engineGameInfo.secondEngineIndex)
                .isKatago) {
              Lizzie.engineManager
                  .engineList
                  .get(EngineManager.engineGameInfo.secondEngineIndex)
                  .sendCommand("kata-set-param analysisWideRootNoise 0");
              Lizzie.engineManager.engineList.get(EngineManager.engineGameInfo.secondEngineIndex)
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
        double wrn = Double.parseDouble(LizzieFrame.menu.txtWRN.getText());
        if (Lizzie.leelaz.isKatago) {
          Lizzie.leelaz.sendCommand("kata-set-param analysisWideRootNoise " + wrn);
          Lizzie.leelaz.wrn = wrn;
        }
        menu.setWrnText(wrn);
        Lizzie.config.txtKataEngineWRN = String.valueOf(wrn);
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
              ? Lizzie.resourceBundle.getString("SGFParse.black")
              : Lizzie.resourceBundle.getString("SGFParse.white");
    return player;
  }

  public void setBackgroundColor(Color color) {
    basePanel.setBackground(color);
  }

  public void openFoxReq() {
    // TODO Auto-generated method stub
    foxKifuDownload = new FoxKifuDownload();
    foxKifuDownload.setVisible(true);
  }

  public void tryToRefreshVariation() {
    // TODO Auto-generated method stub
    boardRenderer.refreshVariation();
    if (Lizzie.config.isDoubleEngineMode()) boardRenderer2.refreshVariation();
  }

  public void redrawBoardrendererBackground() {
    boardRenderer.boardWidth = 1;
    if (boardRenderer2 != null) boardRenderer2.boardWidth = 1;
    if (independentMainBoard != null) independentMainBoard.boardRenderer.boardWidth = 1;
    subBoardRenderer.boardWidth = 1;
    if (subBoardRenderer2 != null) subBoardRenderer2.boardWidth = 1;
    if (subBoardRenderer3 != null) subBoardRenderer3.boardWidth = 1;
    if (subBoardRenderer4 != null) subBoardRenderer4.boardWidth = 1;
  }

  public void hideCandidates() {
    if (Lizzie.config.showBlackCandidates || Lizzie.config.showWhiteCandidates) {
      toolbar.setChkShowBlack(false);
      toolbar.setChkShowWhite(false);
      menu.setChkShowBlack(false);
      menu.setChkShowWhite(false);
    }
  }

  public void toggleShowCandidates() {
    // TODO Auto-generated method stub
    if (Lizzie.config.showBlackCandidates || Lizzie.config.showWhiteCandidates) {
      toolbar.setChkShowBlack(false);
      toolbar.setChkShowWhite(false);
      menu.setChkShowBlack(false);
      menu.setChkShowWhite(false);
      boardRenderer.clearAfterMove();
      if (Lizzie.config.isDoubleEngineMode() && boardRenderer2 != null)
        boardRenderer2.clearAfterMove();
    } else {
      toolbar.setChkShowBlack(true);
      toolbar.setChkShowWhite(true);
      menu.setChkShowBlack(true);
      menu.setChkShowWhite(true);
    }
  }

  public void showCandidates() {
    toolbar.setChkShowBlack(true);
    toolbar.setChkShowWhite(true);
    menu.setChkShowBlack(true);
    menu.setChkShowWhite(true);
  }

  public void openCandidatesDelaySettings(Window owner) {
    // TODO Auto-generated method stub
    SetDelayShowCandidates setDelayShowCandidates = new SetDelayShowCandidates(owner);
    setDelayShowCandidates.setVisible(true);
  }

  private void openInVisibleFrame() {
    String javaReadBoardName = "invisibleFrame.jar";
    File javaReadBoard = new File("clockHelper" + File.separator + javaReadBoardName);
    if (!javaReadBoard.exists()) Utils.copyClockHelper();
    try {
      if (OS.isWindows()) {
        boolean success = false;
        File java64_1 = new File(Utils.java64Path1);

        if (java64_1.exists()) {
          try {
            processClockHelper =
                Runtime.getRuntime()
                    .exec(
                        Utils.java64Path1
                            + " -jar clockHelper"
                            + File.separator
                            + javaReadBoardName);
            success = true;
          } catch (Exception e) {
            success = false;
            e.printStackTrace();
          }
        }
        if (!success) {
          File java64_2 = new File(Utils.java64Path2);
          if (java64_2.exists()) {
            try {
              processClockHelper =
                  Runtime.getRuntime()
                      .exec(
                          Utils.java64Path2
                              + " -jar clockHelper"
                              + File.separator
                              + javaReadBoardName);
              success = true;
            } catch (Exception e) {
              success = false;
              e.printStackTrace();
            }
          }
        }
        if (!success) {
          File java32 = new File(Utils.java32Path);
          if (java32.exists()) {
            try {
              processClockHelper =
                  Runtime.getRuntime()
                      .exec(
                          Utils.java32Path
                              + " -jar clockHelper"
                              + File.separator
                              + javaReadBoardName);
              success = true;
            } catch (Exception e) {
              success = false;
              e.printStackTrace();
            }
          }
        }
        if (!success) {
          processClockHelper =
              Runtime.getRuntime()
                  .exec("java -jar clockHelper" + File.separator + javaReadBoardName);
        }
      } else {
        processClockHelper =
            Runtime.getRuntime().exec("java -jar clockHelper" + File.separator + javaReadBoardName);
      }
    } catch (Exception e) {
      Utils.showMsg(e.getLocalizedMessage());
    }
  }

  public void shutdownClockHelper() {
    processClockHelper.destroy();
  }

  public void flattenBoard() {
    Lizzie.board.hasStartStone = true;
    Lizzie.board.addStartListAll();
    Lizzie.board.flatten();
    refresh();
  }

  public void addContributeLine(String line, boolean stdout) {
    // TODO Auto-generated method stub
    if (stdout) {
      Lizzie.gtpConsole.addLine(line + "\n");
      if (contributeView != null) contributeView.addLine(line + "\n");
    } else {
      Lizzie.gtpConsole.addErrorLine(line + "\n");
      if (contributeView != null) contributeView.addErrorLine(line + "\n");
    }
  }

  private boolean savedIsHiddenKataEstimate;
  private boolean savedShowKataGoEstimate;
  private boolean savedShowKataGoEstimateOnMainbord;
  private boolean savedShowKataGoEstimateOnSubbord;

  public void startContributeEngine() {
    if (Lizzie.frame.isContributing) {
      Utils.showMsg(Lizzie.resourceBundle.getString("Contribute.tips.alreadyTraining"));
      return;
    }
    if (Lizzie.config.contributeUserName.length() <= 0) {
      Utils.showMsg(Lizzie.resourceBundle.getString("Contribute.tips.noUserName"));
      openContributeSettings();
      return;
    }
    if (Lizzie.config.contributeEnginePath.length() <= 0)
      if (!Lizzie.config.contributeUseCommand || Lizzie.config.contributeCommand.length() <= 0) {
        Utils.showMsg(Lizzie.resourceBundle.getString("Contribute.tips.noEnginePath"));
        openContributeSettings();
        return;
      }
    if (contributeEngine != null) contributeEngine.normalQuit();
    Lizzie.engineManager.forceKillAllEngines();
    Lizzie.leelaz.isLoaded = true;
    EngineManager.isEmpty = true;
    contributeEngine = new ContributeEngine();
    Lizzie.frame.openContributeView();
    if (Lizzie.config.contributeShowEstimate) {
      savedIsHiddenKataEstimate = Lizzie.config.isHiddenKataEstimate;
      savedShowKataGoEstimate = Lizzie.config.showKataGoEstimate;
      savedShowKataGoEstimateOnMainbord = Lizzie.config.showKataGoEstimateOnMainbord;
      savedShowKataGoEstimateOnSubbord = Lizzie.config.showKataGoEstimateOnSubbord;

      Lizzie.config.isHiddenKataEstimate = false;
      Lizzie.config.showKataGoEstimate = true;
      if (!Lizzie.config.showKataGoEstimateOnMainbord
          && !Lizzie.config.showKataGoEstimateOnSubbord) {
        Lizzie.config.showKataGoEstimateOnMainbord = true;
        Lizzie.config.showKataGoEstimateOnSubbord = true;
      } else if (!Lizzie.config.showKataGoEstimateOnMainbord && !Lizzie.config.showSubBoard) {
        Lizzie.config.showKataGoEstimateOnMainbord = true;
      }
    }
  }

  public void closeContributeEngine() {
    if (contributeEngine != null) {
      contributeEngine.normalQuit();
    }
    Lizzie.config.isHiddenKataEstimate = savedIsHiddenKataEstimate;
    Lizzie.config.showKataGoEstimate = savedShowKataGoEstimate;
    Lizzie.config.showKataGoEstimateOnMainbord = savedShowKataGoEstimateOnMainbord;
    Lizzie.config.showKataGoEstimateOnSubbord = savedShowKataGoEstimateOnSubbord;
  }

  public void openContributeView() {
    if (contributeView != null) {
      contributeView.setVisible(false);
      contributeView.dispose();
    }
    contributeView = new ContributeView();
  }

  public void openContributeSettings() {
    ContributeSettings contributeSettings = new ContributeSettings(this);
    contributeSettings.setVisible(true);
  }

  public void openTsumego() {
    if (tsumeGoFrame != null && tsumeGoFrame.isVisible()) {
      tsumeGoFrame.setVisible(false);
      tsumeGoFrame.dispose();
    }
    tsumeGoFrame = new TsumeGoFrame(this);
    tsumeGoFrame.setVisible(true);
  }

  public void startCaptureTsumeGo() {
    setExtendedState(JFrame.ICONIFIED);
    if (captureTsumeGoFrame != null && captureTsumeGoFrame.isVisible())
      captureTsumeGoFrame.setVisible(false);
    new CaptureTsumeGo();
  }

  public void openCaptureTsumego() {
    if (captureTsumeGoFrame != null && captureTsumeGoFrame.isVisible()) {
      captureTsumeGoFrame.setVisible(false);
    }
    captureTsumeGoFrame = new CaptureTsumeGoFrame();
    captureTsumeGoFrame.setVisible(true);
  }

  public void newEmptyBoard() {
    if (EngineManager.isEngineGame()) return;
    if (Lizzie.config.showNewBoardHint
        && (Lizzie.board.getHistory().getCurrentHistoryNode().previous().isPresent()
            || Lizzie.board.getHistory().getCurrentHistoryNode().next().isPresent())) {
      Box box = Box.createVerticalBox();
      JFontLabel label =
          new JFontLabel(Lizzie.resourceBundle.getString("LizzieFrame.confirmNewBoard"));
      label.setAlignmentX(Component.LEFT_ALIGNMENT);
      box.add(label);
      Utils.addFiller(box, 5, 5);
      JFontCheckBox disableCheckBox =
          new JFontCheckBox(Lizzie.resourceBundle.getString("LizzieFrame.noNoticeAgain"));
      disableCheckBox.addActionListener(
          new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
              Lizzie.config.showNewBoardHint = !disableCheckBox.isSelected();
              Lizzie.config.uiConfig.put("show-new-board-hint", Lizzie.config.showNewBoardHint);
            }
          });
      disableCheckBox.setAlignmentX(Component.LEFT_ALIGNMENT);
      box.add(disableCheckBox);
      Object[] options = new Object[2];
      options[0] = Lizzie.resourceBundle.getString("LizzieFrame.confirm");
      options[1] = Lizzie.resourceBundle.getString("LizzieFrame.cancel");
      Object defaultOption = Lizzie.resourceBundle.getString("LizzieFrame.cancel");
      JOptionPane optionPane =
          new JOptionPane(
              box,
              JOptionPane.QUESTION_MESSAGE,
              JOptionPane.YES_NO_OPTION,
              null,
              options,
              defaultOption);
      JDialog dialog =
          optionPane.createDialog(
              this, Lizzie.resourceBundle.getString("LizzieFrame.confirmNewBoardTitle"));
      dialog.setVisible(true);
      dialog.dispose();
      if (optionPane.getValue() == null || optionPane.getValue().equals(defaultOption))
        // System.out.println("取消");
        return;
    }
    Lizzie.board.clear(false);
    Lizzie.frame.refresh();
  }
}
