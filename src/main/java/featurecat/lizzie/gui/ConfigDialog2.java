package featurecat.lizzie.gui;

import static java.awt.RenderingHints.KEY_ANTIALIASING;
import static java.awt.RenderingHints.VALUE_ANTIALIAS_ON;
import static java.awt.image.BufferedImage.TYPE_INT_ARGB;
import static java.lang.Math.max;

import featurecat.lizzie.Lizzie;
import featurecat.lizzie.gui.LizzieFrame.HtmlKit;
import featurecat.lizzie.rules.Board;
import featurecat.lizzie.theme.Theme;
import featurecat.lizzie.util.DigitOnlyFilter;
import featurecat.lizzie.util.Utils;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Desktop;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Paint;
import java.awt.RadialGradientPaint;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.TexturePaint;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.nio.file.Path;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Vector;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.stream.Collectors;
import javax.imageio.ImageIO;
import javax.swing.AbstractCellEditor;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JFileChooser;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.ListCellRenderer;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.text.DocumentFilter;
import javax.swing.text.InternationalFormatter;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.StyleSheet;
import org.json.JSONArray;
import org.json.JSONObject;

public class ConfigDialog2 extends JDialog {
  private ResourceBundle resourceBundle =
      Lizzie.resourceBundle; // ResourceBundle.getBundle("l10n.DisplayStrings");

  public String enginePath = "";
  public String weightPath = "";
  public String commandHelp = "";

  private String osName;
  private BufferedInputStream inputStream;
  private JSONObject leelazConfig = Lizzie.config.leelazConfig;
  private List<String> fontList;
  private Theme theme;

  public JPanel uiTab;
  public JPanel themeTab;
  public JPanel aboutTab;
  public JButton okButton;

  // UI Tab
  private JFormattedTextField txtMaxAnalyzeTime;
  private JFormattedTextField txtMaxGameThinkingTime;
  private JFormattedTextField txtAnalyzeUpdateInterval;
  private JFormattedTextField txtAnalyzeUpdateIntervalSSH;

  private JRadioButton rdoAIbackground;
  private JRadioButton rdoNoAIbackground;

  private JRadioButton rdoFastSwitch;
  private JRadioButton rdoNoFastSwitch;

  private JRadioButton rdoShowMoveRect;
  private JRadioButton rdoShowMoveRectOnPlay;
  private JRadioButton rdoNoShowMoveRect;

  private JRadioButton rdoLoadZen;
  private JRadioButton rdoNoLoadZen;

  public JLabel lblBoardSign;
  public JTextField txtBoardWidth;
  public JTextField txtBoardHeight;
  public JRadioButton rdoBoardSizeOther;
  public JRadioButton rdoBoardSize19;
  public JRadioButton rdoBoardSize13;
  public JRadioButton rdoBoardSize9;
  public JRadioButton rdoBoardSize7;
  public JRadioButton rdoBoardSize5;
  public JRadioButton rdoBoardSize4;
  public JCheckBox chkShowName;
  public JCheckBox chkShowBlueRing;
  public JCheckBox chkShowNoSuggCircle;
  public JFormattedTextField txtMinPlayoutRatioForStats;
  public JCheckBox chkShowCaptured;
  public JCheckBox chkShowWinrate;
  public JCheckBox chkShowVariationGraph;
  public JCheckBox chkShowComment;
  public JCheckBox chkShowSubBoard;
  public JCheckBox chkShowStatus;
  public JCheckBox chkShowCoordinates;
  public JRadioButton rdoShowMoveNumberNo;
  public JRadioButton rdoShowMoveNumberAll;
  public JRadioButton rdoShowMoveNumberLast;
  public JTextField txtShowMoveNumber;
  public JCheckBox chkShowMoveAllInBranch;
  public JCheckBox chkShowBlunderBar;
  public JComboBox<String> chkShowWhiteSuggWhite;

  public JRadioButton rdoShowWinrateBlack;
  public JRadioButton rdoShowWinrateBoth;
  //  public JCheckBox chkDynamicWinrateGraphWidth;
  public JCheckBox chkAppendWinrateToComment;
  public JCheckBox chkShowSuggLabel;
  public JCheckBox chkMaxValueReverseColor;
  public JCheckBox chkShowVairationsOnMouse;
  public JCheckBox chkShowVairationsOnMouseNoRefresh;

  public JCheckBox chkAlwaysShowBlackWinrate;
  public JCheckBox chkAlwaysOnTop;
  public JCheckBox chkShowQuickLinks;

  //  public JCheckBox chkHoldBestMovesToSgf;
  //  public JCheckBox chkShowBestMovesByHold;
  //  public JCheckBox chkColorByWinrateInsteadOfVisits;
  public JSlider sldBoardPositionProportion;
  public JTextField txtLimitBestMoveNum;
  public JTextField txtLimitBranchLength;
  public JCheckBox chkShowWinrateInSuggestion;
  public JCheckBox chkShowPlayoutsInSuggestion;
  public JCheckBox chkShowScoremeanInSuggestion;
  // public JTextPane tpGtpConsoleStyle;

  // Theme Tab
  public boolean isLoadedTheme = false;
  public JComboBox<String> cmbThemes;
  public JSpinner spnWinrateStrokeWidth;
  public JSpinner spnMinimumBlunderBarWidth;
  public JSpinner spnShadowSize;
  public JComboBox<String> cmbFontName;
  public JComboBox<String> cmbUiFontName;
  public JComboBox<String> cmbWinrateFontName;
  public JTextField txtBackgroundPath;
  public JTextField txtBoardPath;
  public JTextField txtBlackStonePath;
  public JTextField txtWhiteStonePath;
  public ColorLabel lblWinrateLineColor;
  public ColorLabel lblWinrateMissLineColor;
  public ColorLabel lblBlunderBarColor;
  public ColorLabel lblScoreMeanLineColor;
  public ColorLabel lblCommentBackgroundColor;
  public ColorLabel lblCommentFontColor;
  public ColorLabel lblBestMoveColor;
  public JTextField txtCommentFontSize;
  public JTextField txtBackgroundFilter;
  public JRadioButton rdoStoneIndicatorDelta;
  public JRadioButton rdoStoneIndicatorCircle;
  public JRadioButton rdoStoneIndicatorSolid;
  public JRadioButton rdoStoneIndicatorNo;
  public JCheckBox chkShowCommentNodeColor;
  public ColorLabel lblCommentNodeColor;
  public JTable tblBlunderNodes;
  public String[] columsBlunderNodes;
  public JButton btnBackgroundPath;
  public JButton btnBoardPath;
  public JButton btnBlackStonePath;
  public JButton btnWhiteStonePath;
  public JPanel pnlBoardPreview;
  JTabbedPane tabbedPane;
  private JTextField txtAdvanceTime;
  private JLabel lblShowTitleWinInfo;
  private JCheckBox chkShowTitleWr;
  private JCheckBox chkAlwaysGtp;
  private JCheckBox chkNoCapture;
  private JCheckBox chkEnableDoubCli;
  private JCheckBox chkEnableDragStone;
  private JCheckBox chkNoRefreshSub;
  private JCheckBox chkLizzieCache;

  private JRadioButton rdoRightClickBack;
  private JRadioButton rdoRightClickMenu;
  private JRadioButton rdoBranchMoveContinue;
  private JRadioButton rdoBranchMoveOne;
  private JRadioButton rdbtnKatago;
  private JRadioButton rdbtnZen;
  private JCheckBox chkShowVarMove;
  private JCheckBox chkSgfLoadLast;
  private JCheckBox chkAutoLoadEstimate;
  private JCheckBox chkShowMoveList;
  private JLabel lblShowMoveNumInVariationPane;
  private JLabel lblLoadEstimate;
  private JCheckBox chkShowIndependentMoveList;
  private JCheckBox chkShowIndependentHawkEye;
  private JCheckBox chkUseIinCoordsName;
  private JCheckBox chkLimitTime;

  private JCheckBox chkShowIndependentMainBoard;
  private JCheckBox chkCheckEngineAlive;
  private JCheckBox chkVariationRemoveDeadChain;
  private JCheckBox chkShowScoreLeadLine;
  private JCheckBox chkShowMouseOverWinrateGraph;
  private JComboBox<String> comboBoxPvVisits;
  private JComboBox<String> chkShowIndependentSubBoard;
  private JTextField txtPvVisitsLimit;
  private JTextField txtVariationReplayInterval;
  private JCheckBox chkShowStoneShaow;
  private JCheckBox chkPureBackground;
  private JCheckBox chkPureBoard;
  private JCheckBox chkPureStone;
  public ColorLabel lblPureBackgroundColor;
  public ColorLabel lblPureBoardColor;
  private JTextField txtLimitPlayouts;
  private JCheckBox chkLimitPlayouts;

  public ConfigDialog2() {
    setAlwaysOnTop(Lizzie.frame.isAlwaysOnTop());
    setTitle(resourceBundle.getString("LizzieConfig.title.config"));
    setModalityType(ModalityType.APPLICATION_MODAL);
    // setType(Type.POPUP);
    setBounds(100, 100, 890, 800);
    Lizzie.setFrameSize(this, 890, 800);
    try {
      setIconImage(ImageIO.read(AnalysisFrame.class.getResourceAsStream("/assets/logo.png")));
    } catch (IOException e) {
      e.printStackTrace();
    }
    getContentPane().setLayout(new BorderLayout());
    JPanel buttonPane = new JPanel();
    buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
    getContentPane().add(buttonPane, BorderLayout.SOUTH);
    okButton = new JButton(resourceBundle.getString("LizzieConfig.button.ok"));
    okButton.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            finalizeEditedBlunderColors();
            setVisible(false);
            saveConfig();
            Lizzie.frame.menu.refreshDoubleMoveInfoStatus();
            Lizzie.frame.menu.refreshLimitStatus(false);
            Lizzie.frame.resetCommentComponent();
            applyChange();
            Lizzie.frame.refresh();
          }
        });
    okButton.setActionCommand("OK");
    // okButton.setEnabled(false);
    buttonPane.add(okButton);
    getRootPane().setDefaultButton(okButton);
    JButton cancelButton = new JButton(resourceBundle.getString("LizzieConfig.button.cancel"));
    cancelButton.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            setVisible(false);
          }
        });
    cancelButton.setActionCommand("Cancel");
    buttonPane.add(cancelButton);
    tabbedPane = new JTabbedPane(JTabbedPane.TOP);
    getContentPane().add(tabbedPane, BorderLayout.CENTER);

    NumberFormat nf = NumberFormat.getIntegerInstance();
    nf.setGroupingUsed(false);
    // Theme Tab
    themeTab = new JPanel();

    themeTab.setLayout(null);

    // About Tab
    aboutTab = new JPanel();
    LinkLabel lblLizzieName =
        new LinkLabel(
            "<html><div align=\"center\"><b>Lizzie Yzy 2.3</b></div>"
                + "<div align=\"center\"><font style=\"font-weight:plain;font-size:12;\">Java version: "
                + Lizzie.javaVersionString
                + "</font></div></html>");
    lblLizzieName.setFont(new Font("Tahoma", Font.BOLD, 24));
    LinkLabel lblLizzieInfo =
        new LinkLabel(resourceBundle.getString("LizzieConfig.about.lblLizzieInfo"));
    lblLizzieInfo.setFont(new Font("Tahoma", Font.PLAIN, 14));

    LinkLabel lblOriginTitle =
        new LinkLabel(resourceBundle.getString("LizzieConfig.about.lblOriginTitle"));
    lblOriginTitle.setFont(new Font("Tahoma", Font.BOLD, 14));

    LinkLabel lblOriginLizzieInfo =
        new LinkLabel(
            resourceBundle.getString("LizzieConfig.about.lblOriginLizzieInfo1")
                + Lizzie.checkVersion
                + resourceBundle.getString("LizzieConfig.about.lblOriginLizzieInfo2"));

    lblOriginLizzieInfo.setFont(new Font("Tahoma", Font.PLAIN, 14));
    // 注释这里
    GroupLayout gl = new GroupLayout(aboutTab);
    gl.setHorizontalGroup(
        gl.createParallelGroup(Alignment.LEADING)
            .addGroup(
                gl.createSequentialGroup()
                    .addGroup(
                        gl.createParallelGroup(Alignment.LEADING)
                            .addGroup(
                                gl.createSequentialGroup()
                                    .addContainerGap()
                                    .addComponent(
                                        lblLizzieInfo,
                                        GroupLayout.DEFAULT_SIZE,
                                        628,
                                        Short.MAX_VALUE))
                            .addGroup(
                                gl.createSequentialGroup()
                                    .addContainerGap()
                                    .addComponent(lblOriginTitle))
                            .addGroup(
                                gl.createSequentialGroup()
                                    .addContainerGap()
                                    .addComponent(
                                        lblOriginLizzieInfo,
                                        GroupLayout.PREFERRED_SIZE,
                                        620,
                                        GroupLayout.PREFERRED_SIZE))
                            .addGroup(
                                gl.createSequentialGroup().addComponent(lblLizzieName).addGap(225)))
                    .addContainerGap()));
    gl.setVerticalGroup(
        gl.createParallelGroup(Alignment.LEADING)
            .addGroup(
                gl.createSequentialGroup()
                    .addGap(18)
                    .addComponent(lblLizzieName)
                    .addGap(18)
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addComponent(
                        lblLizzieInfo, GroupLayout.PREFERRED_SIZE, 183, GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addComponent(lblOriginTitle)
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addComponent(
                        lblOriginLizzieInfo,
                        GroupLayout.PREFERRED_SIZE,
                        282,
                        GroupLayout.PREFERRED_SIZE)
                    .addGap(126)));
    aboutTab.setLayout(gl);
    ButtonGroup group = new ButtonGroup();
    nf.setGroupingUsed(false);
    ButtonGroup showMoveGroup = new ButtonGroup();

    ButtonGroup ShowWinratGroup = new ButtonGroup();

    uiTab = new JPanel();
    tabbedPane.addTab(resourceBundle.getString("LizzieConfig.title.ui"), null, uiTab, null);
    uiTab.setLayout(null);
    // setShowLcbWinrate();
    JLabel lblBoardSize = new JLabel(resourceBundle.getString("LizzieConfig.boardSize"));
    lblBoardSize.setBounds(10, 517, 113, 16);
    lblBoardSize.setHorizontalAlignment(SwingConstants.LEFT);
    uiTab.add(lblBoardSize);

    rdoBoardSize19 = new JRadioButton("19x19");
    rdoBoardSize19.setBounds(130, 514, 64, 23);
    uiTab.add(rdoBoardSize19);

    rdoBoardSize13 = new JRadioButton("13x13");
    rdoBoardSize13.setBounds(199, 514, 64, 23);
    uiTab.add(rdoBoardSize13);

    rdoBoardSize9 = new JRadioButton("9x9");
    rdoBoardSize9.setBounds(267, 514, 45, 23);
    uiTab.add(rdoBoardSize9);

    rdoBoardSize7 = new JRadioButton("7x7");
    rdoBoardSize7.setBounds(322, 514, 52, 23);
    uiTab.add(rdoBoardSize7);

    rdoBoardSize5 = new JRadioButton("5x5");
    rdoBoardSize5.setBounds(377, 514, 45, 23);
    uiTab.add(rdoBoardSize5);

    rdoBoardSize4 = new JRadioButton("4x4");
    rdoBoardSize4.setBounds(429, 514, 45, 23);
    uiTab.add(rdoBoardSize4);

    rdoBoardSizeOther = new JRadioButton("");
    rdoBoardSizeOther.addChangeListener(
        new ChangeListener() {
          public void stateChanged(ChangeEvent e) {
            if (rdoBoardSizeOther.isSelected()) {
              txtBoardWidth.setEnabled(true);
              txtBoardHeight.setEnabled(true);
            } else {
              txtBoardWidth.setEnabled(false);
              txtBoardHeight.setEnabled(false);
            }
          }
        });
    rdoBoardSizeOther.setBounds(479, 514, 23, 23);
    uiTab.add(rdoBoardSizeOther);
    group.add(rdoBoardSize19);
    group.add(rdoBoardSize13);
    group.add(rdoBoardSize9);
    group.add(rdoBoardSize7);
    group.add(rdoBoardSize5);
    group.add(rdoBoardSize4);
    group.add(rdoBoardSizeOther);
    txtBoardWidth =
        new JFormattedTextField(
            new InternationalFormatter(nf) {
              protected DocumentFilter getDocumentFilter() {
                return filter;
              }

              private DocumentFilter filter = new DigitOnlyFilter();
            });
    txtBoardWidth.setBounds(504, 514, 38, 26);
    uiTab.add(txtBoardWidth);
    txtBoardWidth.setColumns(10);

    lblBoardSign = new JLabel("x");
    lblBoardSign.setBounds(544, 516, 26, 20);
    uiTab.add(lblBoardSign);

    txtBoardHeight =
        new JFormattedTextField(
            new InternationalFormatter(nf) {
              protected DocumentFilter getDocumentFilter() {
                return filter;
              }

              private DocumentFilter filter = new DigitOnlyFilter();
            });
    txtBoardHeight.setBounds(551, 514, 38, 26);
    uiTab.add(txtBoardHeight);
    txtBoardHeight.setColumns(10);

    JLabel lblAlwaysOnTop =
        new JLabel(resourceBundle.getString("LizzieConfig.lblAlwaysOnTop")); // ("窗口总在最前");
    lblAlwaysOnTop.setBounds(10, 25, 228, 16);
    uiTab.add(lblAlwaysOnTop);
    chkAlwaysOnTop = new JCheckBox("");
    chkAlwaysOnTop.setBounds(237, 23, 45, 23);
    uiTab.add(chkAlwaysOnTop);

    JLabel lblShowQuickLinks =
        new JLabel(resourceBundle.getString("LizzieConfig.lblShowQuickLinks")); // ("显示快速启动");
    lblShowQuickLinks.setBounds(312, 25, 214, 16);
    uiTab.add(lblShowQuickLinks);
    chkShowQuickLinks = new JCheckBox("");
    chkShowQuickLinks.setBounds(532, 23, 57, 23);
    uiTab.add(chkShowQuickLinks);

    //        JLabel lblMinPlayoutRatioForStats =
    //            new
    // JLabel(resourceBundle.getString("LizzieConfig.title.minPlayoutRatioForStats"));
    //        lblMinPlayoutRatioForStats.setBounds(6, 362, 157, 16);
    //        uiTab.add(lblMinPlayoutRatioForStats);

    //        txtMinPlayoutRatioForStats.setColumns(10);
    //        txtMinPlayoutRatioForStats.setBounds(170, 357, 52, 24);
    //        uiTab.add(txtMinPlayoutRatioForStats);

    JLabel lblShowCaptured =
        new JLabel(resourceBundle.getString("LizzieConfig.title.showCaptured"));
    lblShowCaptured.setBounds(10, 52, 221, 16);
    uiTab.add(lblShowCaptured);
    chkShowCaptured = new JCheckBox("");
    chkShowCaptured.addChangeListener(
        new ChangeListener() {
          public void stateChanged(ChangeEvent e) {
            if (chkShowCaptured.isSelected() != Lizzie.config.showCaptured) {
              Lizzie.config.toggleShowCaptured();
            }
          }
        });
    chkShowCaptured.setBounds(237, 50, 38, 23);
    uiTab.add(chkShowCaptured);

    JLabel lblShowWinrate = new JLabel(resourceBundle.getString("LizzieConfig.title.showWinrate"));
    lblShowWinrate.setBounds(10, 78, 228, 16);
    uiTab.add(lblShowWinrate);
    chkShowWinrate = new JCheckBox("");
    chkShowWinrate.addChangeListener(
        new ChangeListener() {
          public void stateChanged(ChangeEvent e) {
            if (chkShowWinrate.isSelected() != Lizzie.config.showWinrateGraph) {
              Lizzie.config.toggleShowWinrate();
            }
          }
        });
    chkShowWinrate.setBounds(237, 76, 45, 23);
    uiTab.add(chkShowWinrate);

    JLabel lblShowVariationGraph =
        new JLabel(resourceBundle.getString("LizzieConfig.title.showVariationGraph"));
    lblShowVariationGraph.setBounds(312, 52, 214, 16);
    uiTab.add(lblShowVariationGraph);
    chkShowVariationGraph = new JCheckBox("");
    chkShowVariationGraph.addChangeListener(
        new ChangeListener() {
          public void stateChanged(ChangeEvent e) {
            if (chkShowVariationGraph.isSelected() != Lizzie.config.showVariationGraph) {
              Lizzie.config.toggleShowVariationGraph();
            }
          }
        });
    chkShowVariationGraph.setBounds(532, 50, 57, 23);
    uiTab.add(chkShowVariationGraph);

    JLabel lblShowComment = new JLabel(resourceBundle.getString("LizzieConfig.title.showComment"));
    lblShowComment.setBounds(312, 78, 214, 16);
    uiTab.add(lblShowComment);
    chkShowComment = new JCheckBox("");
    chkShowComment.addChangeListener(
        new ChangeListener() {
          public void stateChanged(ChangeEvent e) {
            if (chkShowComment.isSelected() != Lizzie.config.showComment) {
              Lizzie.config.toggleShowComment();
            }
          }
        });
    chkShowComment.setBounds(532, 76, 57, 23);
    uiTab.add(chkShowComment);

    JLabel lblShowSubBoard =
        new JLabel(resourceBundle.getString("LizzieConfig.title.showSubBoard"));
    lblShowSubBoard.setBounds(608, 25, 223, 16);
    uiTab.add(lblShowSubBoard);
    chkShowSubBoard = new JCheckBox("");
    chkShowSubBoard.addChangeListener(
        new ChangeListener() {
          public void stateChanged(ChangeEvent e) {
            if (chkShowSubBoard.isSelected() != Lizzie.config.showSubBoard) {
              Lizzie.config.toggleShowSubBoard();
            }
          }
        });
    chkShowSubBoard.setBounds(837, 23, 57, 23);
    uiTab.add(chkShowSubBoard);

    JLabel lblShowStatus =
        new JLabel(resourceBundle.getString("LizzieConfig.lblShowStatus")); // ("显示状态面板");
    lblShowStatus.setBounds(608, 52, 223, 16);
    uiTab.add(lblShowStatus);
    chkShowStatus = new JCheckBox("");
    chkShowStatus.addChangeListener(
        new ChangeListener() {
          public void stateChanged(ChangeEvent e) {
            if (chkShowStatus.isSelected() != Lizzie.config.showStatus) {
              Lizzie.config.toggleShowStatus();
            }
          }
        });
    chkShowStatus.setBounds(837, 50, 57, 23);
    uiTab.add(chkShowStatus);

    JLabel lblShowCoordinates =
        new JLabel(resourceBundle.getString("LizzieConfig.title.showCoordinates"));
    lblShowCoordinates.setBounds(608, 104, 223, 16);
    uiTab.add(lblShowCoordinates);
    chkShowCoordinates = new JCheckBox("");
    chkShowCoordinates.addChangeListener(
        new ChangeListener() {
          public void stateChanged(ChangeEvent e) {
            if (chkShowCoordinates.isSelected() != Lizzie.config.showCoordinates) {
              Lizzie.config.toggleCoordinates();
            }
          }
        });
    chkShowCoordinates.setBounds(837, 102, 57, 23);
    uiTab.add(chkShowCoordinates);

    JLabel lblShowMoveNumber =
        new JLabel(resourceBundle.getString("LizzieConfig.title.showMoveNumber"));
    lblShowMoveNumber.setBounds(10, 104, 113, 16);
    uiTab.add(lblShowMoveNumber);

    rdoShowMoveNumberNo =
        new JRadioButton(resourceBundle.getString("LizzieConfig.title.showMoveNumberNo"));
    rdoShowMoveNumberNo.setBounds(Lizzie.config.isChinese ? 112 : 121, 101, 62, 23);
    uiTab.add(rdoShowMoveNumberNo);

    rdoShowMoveNumberAll =
        new JRadioButton(resourceBundle.getString("LizzieConfig.title.showMoveNumberAll"));
    rdoShowMoveNumberAll.setBounds(
        Lizzie.config.isChinese ? 176 : 181, 101, Lizzie.config.isChinese ? 52 : 42, 23);
    uiTab.add(rdoShowMoveNumberAll);

    rdoShowMoveNumberLast =
        new JRadioButton(resourceBundle.getString("LizzieConfig.title.showMoveNumberLast"));
    rdoShowMoveNumberLast.addChangeListener(
        new ChangeListener() {
          public void stateChanged(ChangeEvent e) {
            if (rdoShowMoveNumberLast.isSelected()) {
              txtShowMoveNumber.setEnabled(true);
            } else {
              txtShowMoveNumber.setEnabled(false);
            }
          }
        });
    rdoShowMoveNumberLast.setBounds(225, 101, 50, 23);
    uiTab.add(rdoShowMoveNumberLast);
    showMoveGroup.add(rdoShowMoveNumberNo);
    showMoveGroup.add(rdoShowMoveNumberAll);
    showMoveGroup.add(rdoShowMoveNumberLast);

    txtShowMoveNumber =
        new JFormattedTextField(
            new InternationalFormatter(nf) {
              protected DocumentFilter getDocumentFilter() {
                return filter;
              }

              private DocumentFilter filter = new DigitOnlyFilter();
            });
    txtShowMoveNumber.setBounds(275, 103, 28, 20);
    uiTab.add(txtShowMoveNumber);
    txtShowMoveNumber.setColumns(10);

    JLabel lblShowAllMove =
        new JLabel(
            resourceBundle.getString(
                "LizzieConfig.lblShowAllMove")); // ("分支内总是显示手数"); // $NON-NLS-1$
    lblShowAllMove.setBounds(312, 104, 198, 16);
    uiTab.add(lblShowAllMove);
    chkShowMoveAllInBranch = new JCheckBox("");
    chkShowMoveAllInBranch.setBounds(532, 102, 57, 23);
    uiTab.add(chkShowMoveAllInBranch);

    JLabel lblShowBlunderBar =
        new JLabel(resourceBundle.getString("LizzieConfig.title.showBlunderBar"));
    lblShowBlunderBar.setBounds(608, 204, 214, 16);
    uiTab.add(lblShowBlunderBar);
    chkShowBlunderBar = new JCheckBox("");
    chkShowBlunderBar.setBounds(837, 201, 38, 23);
    uiTab.add(chkShowBlunderBar);

    JLabel lblWinratePerspective =
        new JLabel(
            resourceBundle.getString(
                "LizzieConfig.lblWinratePerspective")); // ("胜率图视角"); //$NON-NLS-1$
    lblWinratePerspective.setBounds(10, 204, 184, 16);
    uiTab.add(lblWinratePerspective);

    rdoShowWinrateBlack =
        new JRadioButton(resourceBundle.getString("LizzieConfig.rdoShowWinrateBlack")); // ("黑方视角");
    rdoShowWinrateBlack.setBounds(155, 202, 72, 23);
    uiTab.add(rdoShowWinrateBlack);

    rdoShowWinrateBoth =
        new JRadioButton(resourceBundle.getString("LizzieConfig.rdoShowWinrateBoth")); // ("双方视角");
    rdoShowWinrateBoth.setBounds(225, 202, 78, 23);
    uiTab.add(rdoShowWinrateBoth);

    JLabel lblShowWhiteSuggestionWhite =
        new JLabel(
            resourceBundle.getString("LizzieConfig.lblShowWhiteSuggestionWhite")); // ("轮白下选点字体白色");
    lblShowWhiteSuggestionWhite.setBounds(312, 258, 214, 16);
    uiTab.add(lblShowWhiteSuggestionWhite);
    chkShowWhiteSuggWhite = new JComboBox<String>();
    chkShowWhiteSuggWhite.addItem(
        resourceBundle.getString("LizzieConfig.chkShowWhiteSuggWhite1")); // ("无");
    chkShowWhiteSuggWhite.addItem(
        resourceBundle.getString("LizzieConfig.chkShowWhiteSuggWhite2")); // ("仅选点");
    chkShowWhiteSuggWhite.addItem(
        resourceBundle.getString("LizzieConfig.chkShowWhiteSuggWhite3")); // ("仅角标");
    chkShowWhiteSuggWhite.addItem(
        resourceBundle.getString("LizzieConfig.chkShowWhiteSuggWhite4")); // ("全部");
    chkShowWhiteSuggWhite.setBounds(504, 254, 67, 23);
    uiTab.add(chkShowWhiteSuggWhite);
    ShowWinratGroup.add(rdoShowWinrateBlack);
    ShowWinratGroup.add(rdoShowWinrateBoth);

    JLabel lblSuggestionMoveColorConcentration =
        new JLabel(
            resourceBundle.getString(
                "LizzieConfig.lblSuggestionMoveColorConcentration")); // ("选点颜色集中程度");
    lblSuggestionMoveColorConcentration.setBounds(10, 366, 222, 16);
    uiTab.add(lblSuggestionMoveColorConcentration);
    JComboBox<String> SuggestionColorRatio = new JComboBox<String>();
    SuggestionColorRatio.addItem(
        resourceBundle.getString("LizzieConfig.SuggestionMoveColorConcentration1")); // ("集中");
    SuggestionColorRatio.addItem(
        resourceBundle.getString("LizzieConfig.SuggestionMoveColorConcentration2")); // ("一般");
    SuggestionColorRatio.addItem(
        resourceBundle.getString("LizzieConfig.SuggestionMoveColorConcentration3")); // ("分散");
    SuggestionColorRatio.setBounds(Lizzie.config.isChinese ? 201 : 221, 363, 66, 23);
    uiTab.add(SuggestionColorRatio);
    SuggestionColorRatio.setSelectedIndex(Lizzie.config.suggestionColorRatio - 1);
    SuggestionColorRatio.addItemListener(
        new ItemListener() {
          public void itemStateChanged(final ItemEvent e) {
            Lizzie.config.suggestionColorRatio = SuggestionColorRatio.getSelectedIndex() + 1;
            Lizzie.config.uiConfig.put(
                "suggestion-color-ratio", Lizzie.config.suggestionColorRatio);
          }
        });

    JLabel lblAppendWinrateToComment =
        new JLabel(
            resourceBundle.getString("LizzieConfig.lblAppendWinrateToComment")); // ("记录胜率到评论中");
    lblAppendWinrateToComment.setBounds(608, 577, 221, 16);
    uiTab.add(lblAppendWinrateToComment);
    chkAppendWinrateToComment = new JCheckBox("");
    chkAppendWinrateToComment.setBounds(837, 576, 23, 23);
    uiTab.add(chkAppendWinrateToComment);

    JLabel lblShowSuggestionMoveOrder =
        new JLabel(
            resourceBundle.getString("LizzieConfig.lblShowSuggestionMoveOrder")); // ("显示选点右上方角标");
    lblShowSuggestionMoveOrder.setBounds(609, 231, 207, 16);
    uiTab.add(lblShowSuggestionMoveOrder);
    chkShowSuggLabel = new JCheckBox("");
    chkShowSuggLabel.setBounds(837, 228, 57, 23);
    uiTab.add(chkShowSuggLabel);

    JLabel lblMaxValueReverseColor =
        new JLabel(
            resourceBundle.getString(
                "LizzieConfig.lblMaxValueReverseColor")); // ("最高胜率计算量反色显示"); // $NON-NLS-1$
    lblMaxValueReverseColor.setBounds(608, 285, 223, 16);
    uiTab.add(lblMaxValueReverseColor);
    chkMaxValueReverseColor = new JCheckBox("");
    chkMaxValueReverseColor.setBounds(837, 282, 57, 23);
    uiTab.add(chkMaxValueReverseColor);

    JLabel lblShowVariationsOnMouse =
        new JLabel(
            resourceBundle.getString("LizzieConfig.lblShowVariationsOnMouse")); // ("鼠标悬停显示变化图");
    lblShowVariationsOnMouse.setBounds(10, 231, 228, 16);
    uiTab.add(lblShowVariationsOnMouse);
    chkShowVairationsOnMouse = new JCheckBox("");
    chkShowVairationsOnMouse.setBounds(237, 228, 57, 23);
    uiTab.add(chkShowVairationsOnMouse);

    JLabel lblNotRereshVairationsOnMouseOver =
        new JLabel(
            resourceBundle.getString(
                "LizzieConfig.lblNotRereshVairationsOnMouseOver")); // ("鼠标悬停时 变化图不刷新");
    lblNotRereshVairationsOnMouseOver.setBounds(312, 231, 230, 16);
    uiTab.add(lblNotRereshVairationsOnMouseOver);
    chkShowVairationsOnMouseNoRefresh = new JCheckBox("");
    chkShowVairationsOnMouseNoRefresh.setBounds(532, 228, 57, 23);
    uiTab.add(chkShowVairationsOnMouseNoRefresh);

    JLabel lblBoardPositionProportion =
        new JLabel(
            resourceBundle.getString("LizzieConfig.lblBoardPositionProportion")); // ("主界面偏移");
    lblBoardPositionProportion.setBounds(312, 579, 162, 16);
    uiTab.add(lblBoardPositionProportion);
    sldBoardPositionProportion = new JSlider();
    sldBoardPositionProportion.setPaintTicks(true);
    sldBoardPositionProportion.setSnapToTicks(true);
    sldBoardPositionProportion.addChangeListener(
        new ChangeListener() {
          public void stateChanged(ChangeEvent e) {
            if (Lizzie.frame.BoardPositionProportion != sldBoardPositionProportion.getValue()) {
              Lizzie.frame.BoardPositionProportion = sldBoardPositionProportion.getValue();
              Lizzie.frame.refreshContainer();
            }
          }
        });
    sldBoardPositionProportion.setValue(Lizzie.frame.BoardPositionProportion);
    sldBoardPositionProportion.setMaximum(8);
    sldBoardPositionProportion.setBounds(439, 577, 157, 28);
    uiTab.add(sldBoardPositionProportion);

    JLabel showBlueRing =
        new JLabel(resourceBundle.getString("LizzieConfig.showBlueRing")); // ("第一选点上显示蓝圈");
    showBlueRing.setBounds(609, 258, 222, 16);
    uiTab.add(showBlueRing);

    chkShowBlueRing = new JCheckBox("");
    chkShowBlueRing.setBounds(837, 255, 57, 23);
    uiTab.add(chkShowBlueRing);

    JLabel showNameInboard =
        new JLabel(resourceBundle.getString("LizzieConfig.showNameInboard")); // "在棋盘下方显示黑白名字");
    showNameInboard.setBounds(10, 579, 184, 16);
    uiTab.add(showNameInboard);

    chkShowName = new JCheckBox("");
    chkShowName.setBounds(237, 576, 26, 23);
    uiTab.add(chkShowName);

    JLabel lblAlwaysShowBlackWinrate =
        new JLabel(
            resourceBundle.getString("LizzieConfig.lblAlwaysShowBlackWinrate")); // ("总是显示黑胜率");
    lblAlwaysShowBlackWinrate.setBounds(10, 258, 194, 16);
    uiTab.add(lblAlwaysShowBlackWinrate);
    chkAlwaysShowBlackWinrate = new JCheckBox("");
    chkAlwaysShowBlackWinrate.setBounds(237, 255, 57, 23);
    uiTab.add(chkAlwaysShowBlackWinrate);

    JLabel lblLimitBestMoveNum =
        new JLabel(resourceBundle.getString("LizzieConfig.title.limitBestMoveNum"));
    lblLimitBestMoveNum.setBounds(10, 285, 157, 16);
    uiTab.add(lblLimitBestMoveNum);
    txtLimitBestMoveNum =
        new JFormattedTextField(
            new InternationalFormatter(nf) {
              protected DocumentFilter getDocumentFilter() {
                return filter;
              }

              private DocumentFilter filter = new DigitOnlyFilter();
            });
    txtLimitBestMoveNum.setBounds(147, 283, 45, 20);
    uiTab.add(txtLimitBestMoveNum);
    txtLimitBestMoveNum.setColumns(10);

    JLabel lblLimitBranchLength =
        new JLabel(resourceBundle.getString("LizzieConfig.title.limitBranchLength"));
    lblLimitBranchLength.setBounds(224, 285, 122, 16);
    uiTab.add(lblLimitBranchLength);
    txtLimitBranchLength =
        new JFormattedTextField(
            new InternationalFormatter(nf) {
              protected DocumentFilter getDocumentFilter() {
                return filter;
              }

              private DocumentFilter filter = new DigitOnlyFilter();
            });
    txtLimitBranchLength.setBounds(358, 283, 45, 20);
    uiTab.add(txtLimitBranchLength);
    txtLimitBranchLength.setColumns(10);

    JLabel lblShowCircleForOutOfLimit =
        new JLabel(
            resourceBundle.getString(
                "LizzieConfig.lblShowCircleForOutOfLimit")); // ("超限制(低计算)选点依然显示推荐圈");
    lblShowCircleForOutOfLimit.setBounds(312, 312, 228, 16);
    uiTab.add(lblShowCircleForOutOfLimit);
    chkShowNoSuggCircle = new JCheckBox("");
    chkShowNoSuggCircle.setBounds(532, 310, 57, 23);
    uiTab.add(chkShowNoSuggCircle);

    JLabel lblNotShowMinPlayoutRatio =
        new JLabel(resourceBundle.getString("LizzieConfig.lblNotShowMinPlayoutRatio"));
    // 不显示低于最高计算量(%)的选点
    lblNotShowMinPlayoutRatio.setBounds(10, 312, 228, 17);
    uiTab.add(lblNotShowMinPlayoutRatio);

    txtMinPlayoutRatioForStats =
        new JFormattedTextField(
            new InternationalFormatter() {
              protected DocumentFilter getDocumentFilter() {
                return filter;
              }

              private DocumentFilter filter = new DigitOnlyFilter("[^0-9\\.]++");
            });
    txtMinPlayoutRatioForStats.setBounds(230, 310, 45, 24);
    uiTab.add(txtMinPlayoutRatioForStats);

    JLabel lblSuggestionMoveInfo =
        new JLabel(resourceBundle.getString("LizzieConfig.title.suggestionMoveInfo"));
    lblSuggestionMoveInfo.setBounds(10, 339, 64, 16);
    uiTab.add(lblSuggestionMoveInfo);
    chkShowWinrateInSuggestion =
        new JCheckBox(resourceBundle.getString("LizzieConfig.title.showWinrateInSuggestion"));
    chkShowWinrateInSuggestion.setBounds(80, 336, 68, 23);
    uiTab.add(chkShowWinrateInSuggestion);
    chkShowPlayoutsInSuggestion =
        new JCheckBox(resourceBundle.getString("LizzieConfig.title.showPlayoutsInSuggestion"));
    chkShowPlayoutsInSuggestion.setBounds(145, 336, 75, 23);
    uiTab.add(chkShowPlayoutsInSuggestion);
    chkShowScoremeanInSuggestion =
        new JCheckBox(resourceBundle.getString("LizzieConfig.title.showScoremeanInSuggestion"));
    chkShowScoremeanInSuggestion.setBounds(216, 336, 86, 23);
    uiTab.add(chkShowScoremeanInSuggestion);

    JLabel lblGtpConsoleStyle =
        new JLabel(resourceBundle.getString("LizzieConfig.title.gtpConsoleStyle"));
    lblGtpConsoleStyle.setBounds(600, 172, 28, 23);
    // uiTab.add(lblGtpConsoleStyle);
    //    tpGtpConsoleStyle = new JTextPane();
    //    tpGtpConsoleStyle.setBounds(598, 192, 30, 3);
    //  uiTab.add(tpGtpConsoleStyle);
    chkShowName.setSelected(Lizzie.config.showNameInBoard);
    chkShowBlueRing.setSelected(Lizzie.config.showBlueRing);
    chkShowNoSuggCircle.setSelected(Lizzie.config.showNoSuggCircle);
    chkAlwaysShowBlackWinrate.setSelected(Lizzie.config.winrateAlwaysBlack);
    chkAlwaysOnTop.setSelected(Lizzie.frame.isAlwaysOnTop());
    chkShowQuickLinks.setSelected(Lizzie.config.showQuickLinks);
    txtMinPlayoutRatioForStats.setText(String.valueOf(Lizzie.config.minPlayoutRatioForStats * 100));
    chkShowCaptured.setSelected(Lizzie.config.showCaptured);
    chkShowWinrate.setSelected(Lizzie.config.showWinrateGraph);
    chkShowVariationGraph.setSelected(Lizzie.config.showVariationGraph);
    chkShowComment.setSelected(Lizzie.config.showComment);
    chkShowSubBoard.setSelected(Lizzie.config.showSubBoard);
    chkShowStatus.setSelected(Lizzie.config.showStatus);
    chkShowCoordinates.setSelected(Lizzie.config.showCoordinates);
    chkShowBlunderBar.setSelected(Lizzie.config.showBlunderBar);
    if (Lizzie.config.whiteSuggestionWhite) {
      if (Lizzie.config.whiteSuggestionOrderWhite) chkShowWhiteSuggWhite.setSelectedIndex(3);
      else chkShowWhiteSuggWhite.setSelectedIndex(1);
    } else {
      if (Lizzie.config.whiteSuggestionOrderWhite) chkShowWhiteSuggWhite.setSelectedIndex(2);
      else chkShowWhiteSuggWhite.setSelectedIndex(0);
    }

    chkShowMoveAllInBranch.setSelected(Lizzie.config.showMoveAllInBranch);
    // chkDynamicWinrateGraphWidth.setSelected(Lizzie.config.dynamicWinrateGraphWidth);
    chkAppendWinrateToComment.setSelected(Lizzie.config.appendWinrateToComment);
    chkShowSuggLabel.setSelected(Lizzie.config.showSuggestionOrder);
    chkMaxValueReverseColor.setSelected(Lizzie.config.showSuggestionMaxRed);
    chkShowVairationsOnMouse.setSelected(Lizzie.config.showSuggestionVariations);
    chkShowVairationsOnMouseNoRefresh.setSelected(Lizzie.config.noRefreshOnMouseMove);
    //  chkHoldBestMovesToSgf.setSelected(Lizzie.config.holdBestMovesToSgf);
    //  chkShowBestMovesByHold.setSelected(Lizzie.config.showBestMovesByHold);
    // chkColorByWinrateInsteadOfVisits.setSelected(Lizzie.config.colorByWinrateInsteadOfVisits);
    // sldBoardPositionProportion.setValue(Lizzie.config.boardPositionProportion);
    txtLimitBestMoveNum.setText(String.valueOf(Lizzie.config.limitMaxSuggestion));
    txtLimitBranchLength.setText(String.valueOf(Lizzie.config.limitBranchLength));
    chkShowWinrateInSuggestion.setSelected(Lizzie.config.showWinrateInSuggestion);
    chkShowPlayoutsInSuggestion.setSelected(Lizzie.config.showPlayoutsInSuggestion);
    chkShowScoremeanInSuggestion.setSelected(Lizzie.config.showScoremeanInSuggestion);
    // tpGtpConsoleStyle.setText(Lizzie.config.gtpConsoleStyle);

    JLabel lblViewSettings =
        new JLabel(
            resourceBundle.getString(
                "LizzieConfig.lblViewSettings")); // ("界面面板选项:"); // $NON-NLS-1$
    lblViewSettings.setFont(new Font("宋体", Font.BOLD, 14));
    lblViewSettings.setBounds(10, 2, 408, 23);
    uiTab.add(lblViewSettings);

    JLabel lblSuggestionMoveAndWinrateSettings =
        new JLabel(
            resourceBundle.getString(
                "LizzieConfig.lblSuggestionMoveAndWinrateSettings")); // ("选点与胜率图选项:"); //
    // $NON-NLS-1$
    lblSuggestionMoveAndWinrateSettings.setFont(new Font("宋体", Font.BOLD, 14));
    lblSuggestionMoveAndWinrateSettings.setBounds(10, 179, 395, 23);
    uiTab.add(lblSuggestionMoveAndWinrateSettings);

    JLabel lblOtherSettings =
        new JLabel(
            resourceBundle.getString(
                "LizzieConfig.lblEngineSettings")); // ("其他选项:"); // $NON-NLS-1$
    lblOtherSettings.setFont(new Font("宋体", Font.BOLD, 14));
    lblOtherSettings.setBounds(10, 389, 492, 23);
    uiTab.add(lblOtherSettings);

    JLabel lblEngineSettings =
        new JLabel(
            resourceBundle.getString("LizzieConfig.lblOtherSettings")); // ("引擎选项:"); // $NON-NLS-1$
    lblEngineSettings.setFont(new Font("宋体", Font.BOLD, 14));
    lblEngineSettings.setBounds(10, 490, 492, 23);
    uiTab.add(lblEngineSettings);

    JLabel lblMaxAnalyzeTime =
        new JLabel(
            resourceBundle.getString(
                "LizzieConfig.title.maxAnalyzeTime")); // ("最大分析时间"); // $NON-NLS-1$
    lblMaxAnalyzeTime.setBounds(10, 415, 130, 16);
    uiTab.add(lblMaxAnalyzeTime);

    JLabel lblManAiGameTime =
        new JLabel(
            resourceBundle.getString(
                "LizzieConfig.title.lblManAiGameTime")); // ("人机对局AI每手用时"); // $NON-NLS-1$
    lblManAiGameTime.setBounds(10, 441, 207, 16);
    uiTab.add(lblManAiGameTime);

    txtMaxGameThinkingTime =
        new JFormattedTextField(
            new InternationalFormatter(nf) {
              protected DocumentFilter getDocumentFilter() {
                return filter;
              }

              private DocumentFilter filter = new DigitOnlyFilter();
            });
    txtMaxGameThinkingTime.setText("0");
    txtMaxGameThinkingTime.setColumns(10);
    txtMaxGameThinkingTime.setBounds(215, 439, 40, 21);
    uiTab.add(txtMaxGameThinkingTime);

    JLabel lblSeconds2 = new JLabel(resourceBundle.getString("LizzieConfig.title.seconds"));
    lblSeconds2.setBounds(257, 441, 56, 16);
    uiTab.add(lblSeconds2);
    chkShowWinrateInSuggestion.addChangeListener(
        new ChangeListener() {
          public void stateChanged(ChangeEvent e) {
            suggestionMoveInfoChanged();
          }
        });
    chkShowPlayoutsInSuggestion.addChangeListener(
        new ChangeListener() {
          public void stateChanged(ChangeEvent e) {
            suggestionMoveInfoChanged();
          }
        });
    chkShowScoremeanInSuggestion.addChangeListener(
        new ChangeListener() {
          public void stateChanged(ChangeEvent e) {
            suggestionMoveInfoChanged();
          }
        });

    JLabel lblAnalyzeUpdateInterval =
        new JLabel(
            resourceBundle.getString("LizzieConfig.title.analyzeUpdateInterval")); // ("分析结果刷新时间");
    lblAnalyzeUpdateInterval.setBounds(312, 415, 184, 16);
    uiTab.add(lblAnalyzeUpdateInterval);

    txtAnalyzeUpdateInterval =
        new JFormattedTextField(
            new InternationalFormatter(nf) {
              protected DocumentFilter getDocumentFilter() {
                return filter;
              }

              private DocumentFilter filter = new DigitOnlyFilter();
            });
    // formattedTextField_1.setText("0");
    txtAnalyzeUpdateInterval.setColumns(10);
    txtAnalyzeUpdateInterval.setBounds(495, 413, 31, 21);
    uiTab.add(txtAnalyzeUpdateInterval);

    JLabel lblSsh =
        new JLabel(
            resourceBundle.getString(
                "LizzieConfig.title.SSHanalyzeUpdateInterval")); // ("SSH分析结果刷新时间");
    lblSsh.setBounds(608, 415, 173, 16);
    uiTab.add(lblSsh);

    txtAnalyzeUpdateIntervalSSH =
        new JFormattedTextField(
            new InternationalFormatter(nf) {
              protected DocumentFilter getDocumentFilter() {
                return filter;
              }

              private DocumentFilter filter = new DigitOnlyFilter();
            });
    // txtAnalyzeUpdateInterval.setText("1");
    txtAnalyzeUpdateIntervalSSH.setColumns(10);
    txtAnalyzeUpdateIntervalSSH.setBounds(767, 413, 30, 21);
    uiTab.add(txtAnalyzeUpdateIntervalSSH);

    JLabel lblMillseconds =
        new JLabel(
            resourceBundle.getString(
                "LizzieConfig.title.centisecond")); // $NON-NLS-1$LizzieConfig.title.centisecond
    lblMillseconds.setBounds(800, 415, 86, 16);
    uiTab.add(lblMillseconds);

    JLabel lblMillseconds2 =
        new JLabel(resourceBundle.getString("LizzieConfig.title.centisecond")); // $NON-NLS-1$
    lblMillseconds2.setBounds(530, 415, 72, 16);
    uiTab.add(lblMillseconds2);

    JLabel lblEngineFastSwitch =
        new JLabel(
            resourceBundle.getString("ConfigDialog2.lblEngineFastSwitch")); // ("是否启用引擎快速切换");
    lblEngineFastSwitch.setBounds(312, 469, 179, 16);
    uiTab.add(lblEngineFastSwitch);

    rdoFastSwitch = new JRadioButton(resourceBundle.getString("ConfigDialog2.yes"));
    rdoFastSwitch.setBounds(487, 466, 52, 23);
    uiTab.add(rdoFastSwitch);

    rdoNoFastSwitch = new JRadioButton(resourceBundle.getString("ConfigDialog2.no")); // ("否");
    rdoNoFastSwitch.setBounds(536, 466, 53, 23);
    uiTab.add(rdoNoFastSwitch);

    ButtonGroup fastgroup = new ButtonGroup();
    fastgroup.add(rdoFastSwitch);
    fastgroup.add(rdoNoFastSwitch);

    if (Lizzie.config.fastChange) {
      rdoFastSwitch.setSelected(true);
    } else {
      rdoNoFastSwitch.setSelected(true);
    }

    JLabel label_11 = new JLabel("是否预加载Zen(用于点目)");
    label_11.setBounds(277, 634, 157, 16);
    // uiTab.add(label_11);

    rdoLoadZen = new JRadioButton("是");
    rdoLoadZen.setBounds(442, 542, 41, 23);
    //  uiTab.add(rdoLoadZen);

    rdoNoLoadZen = new JRadioButton("否");
    rdoNoLoadZen.setBounds(491, 542, 42, 23);
    //  uiTab.add(rdoNoLoadZen);

    ButtonGroup zengroup = new ButtonGroup();
    zengroup.add(rdoLoadZen);
    zengroup.add(rdoNoLoadZen);

    JLabel lblMouseMoveRect =
        new JLabel(
            resourceBundle.getString(
                "LizzieConfig.lblMouseMoveRect")); // ("鼠标移动时显示小方块"); // $NON-NLS-1$
    lblMouseMoveRect.setBounds(10, 549, 184, 16);
    uiTab.add(lblMouseMoveRect);

    rdoShowMoveRect =
        new JRadioButton(resourceBundle.getString("LizzieConfig.rdoShowMoveRect")); // ("是");
    rdoShowMoveRect.setBounds(200, 546, 50, 23);
    uiTab.add(rdoShowMoveRect);

    rdoShowMoveRectOnPlay =
        new JRadioButton(
            resourceBundle.getString("LizzieConfig.rdoShowMoveRectOnPlay")); // ("仅对局时");
    rdoShowMoveRectOnPlay.setBounds(273, 546, 105, 23);
    uiTab.add(rdoShowMoveRectOnPlay);

    rdoNoShowMoveRect =
        new JRadioButton(resourceBundle.getString("LizzieConfig.rdoNoShowMoveRect")); // ("否");
    rdoNoShowMoveRect.setBounds(380, 546, 42, 23);
    uiTab.add(rdoNoShowMoveRect);

    ButtonGroup rectgroup = new ButtonGroup();
    rectgroup.add(rdoShowMoveRect);
    rectgroup.add(rdoShowMoveRectOnPlay);
    rectgroup.add(rdoNoShowMoveRect);

    if (Lizzie.config.showrect == 0) {
      rdoShowMoveRect.setSelected(true);
    } else if (Lizzie.config.showrect == 1) {
      rdoShowMoveRectOnPlay.setSelected(true);
    } else {
      rdoNoShowMoveRect.setSelected(true);
    }

    JLabel lblBackgroundPonder =
        new JLabel(
            resourceBundle.getString("ConfigDialog2.lblBackgroundPonder")); // ("对弈时AI是否后台计算");
    lblBackgroundPonder.setBounds(10, 469, 194, 16);
    uiTab.add(lblBackgroundPonder);

    rdoAIbackground = new JRadioButton(resourceBundle.getString("ConfigDialog2.yes")); // ("是");
    rdoAIbackground.setBounds(199, 466, 58, 23);
    uiTab.add(rdoAIbackground);

    rdoNoAIbackground = new JRadioButton(resourceBundle.getString("ConfigDialog2.no")); // ("否");
    rdoNoAIbackground.setBounds(254, 466, 58, 23);
    uiTab.add(rdoNoAIbackground);

    ButtonGroup backgroup = new ButtonGroup();
    backgroup.add(rdoAIbackground);
    backgroup.add(rdoNoAIbackground);

    if (Lizzie.config.playponder) {
      rdoAIbackground.setSelected(true);
    } else {
      rdoNoAIbackground.setSelected(true);
    }

    tabbedPane.addTab(resourceBundle.getString("LizzieConfig.title.theme"), null, themeTab, null);
    tabbedPane.addTab(resourceBundle.getString("LizzieConfig.title.about"), null, aboutTab, null);
    // txtMaxAnalyzeTime.setText(String.valueOf(leelazConfig.getInt("max-analyze-time-minutes")));
    txtAnalyzeUpdateInterval.setText(Lizzie.config.analyzeUpdateIntervalCentisec + "");
    txtAnalyzeUpdateIntervalSSH.setText(Lizzie.config.analyzeUpdateIntervalCentisecSSH + "");
    // txtAvoidKeepVariations.setText(String.valueOf(leelazConfig.getInt("avoid-keep-variations")));
    txtMaxGameThinkingTime.setText(
        String.valueOf(leelazConfig.getInt("max-game-thinking-time-seconds")));

    JLabel lblNoticeLimit =
        new JLabel(
            resourceBundle.getString(
                "LizzieConfig.lblNoticeLimit")); // ("注: 0代表无限制"); // $NON-NLS-1$
    lblNoticeLimit.setBounds(440, 285, 172, 15);
    uiTab.add(lblNoticeLimit);

    JLabel lblAdvanceTime = new JLabel(resourceBundle.getString("ConfigDialog2.lblAdvanceTime"));
    lblAdvanceTime.setBounds(312, 441, 122, 15);
    uiTab.add(lblAdvanceTime);

    ImageIcon iconSettings = new ImageIcon();
    try {
      iconSettings.setImage(
          ImageIO.read(AnalysisFrame.class.getResourceAsStream("/assets/settings.png")));
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    JButton btnNewButton = new JButton(iconSettings); // $NON-NLS-1$
    btnNewButton.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            Discribe advancedTimeDiscribe = new Discribe();
            advancedTimeDiscribe.setInfo(
                resourceBundle.getString("AdvanceTimeSettings.descibe"),
                resourceBundle.getString("AdvanceTimeSettings.title"),
                600,
                300);
          }
        });
    btnNewButton.setBounds(435, 441, 18, 18);
    uiTab.add(btnNewButton);

    JCheckBox chckbxNewCheckBox = new JCheckBox(""); // $NON-NLS-1$
    chckbxNewCheckBox.setBounds(452, 438, 22, 23);
    uiTab.add(chckbxNewCheckBox);

    chckbxNewCheckBox.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            // TODO Auto-generated method stub
            Lizzie.config.advanceTimeSettings = chckbxNewCheckBox.isSelected();
            if (Lizzie.config.advanceTimeSettings) {
              txtMaxGameThinkingTime.setEnabled(false);
              txtAdvanceTime.setEditable(true);
            } else {
              txtMaxGameThinkingTime.setEnabled(true);
              txtAdvanceTime.setEditable(false);
            }
          }
        });
    chckbxNewCheckBox.setSelected(Lizzie.config.advanceTimeSettings);

    txtAdvanceTime = new JTextField();
    txtAdvanceTime.setText(Lizzie.config.advanceTimeTxt); // $NON-NLS-1$
    txtAdvanceTime.setBounds(474, 440, 130, 21);
    uiTab.add(txtAdvanceTime);

    lblShowTitleWinInfo =
        new JLabel(
            resourceBundle.getString(
                "LizzieConfig.lblShowTitleWinInfo")); // "标题上显示胜率等信息"); // $NON-NLS-1$
    lblShowTitleWinInfo.setBounds(608, 549, 223, 15);
    uiTab.add(lblShowTitleWinInfo);

    chkShowTitleWr = new JCheckBox(); // $NON-NLS-1$
    chkShowTitleWr.setBounds(837, 546, 27, 23);
    uiTab.add(chkShowTitleWr);
    chkShowTitleWr.setSelected(Lizzie.config.showTitleWr);

    JLabel lblAlwaysLogGtpInfo =
        new JLabel(
            resourceBundle.getString(
                "LizzieConfig.lblAlwaysLogGtpInfo")); // ("总是记录GTP信息"); // $NON-NLS-1$
    lblAlwaysLogGtpInfo.setBounds(608, 518, 223, 15);
    uiTab.add(lblAlwaysLogGtpInfo);

    chkAlwaysGtp = new JCheckBox(); // $NON-NLS-1$
    chkAlwaysGtp.setBounds(837, 514, 26, 23);
    uiTab.add(chkAlwaysGtp);
    // txtAdvanceTime.setColumns(10);
    chkAlwaysGtp.setSelected(Lizzie.config.alwaysGtp);

    chkNoCapture = new JCheckBox(); // $NON-NLS-1$
    chkNoCapture.setBounds(375, 636, 26, 23);
    uiTab.add(chkNoCapture);

    chkNoCapture.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            if (chkNoCapture.isSelected()) {
              int ret =
                  JOptionPane.showConfirmDialog(
                      Lizzie.frame.configDialog2,
                      "选择五子棋则无法提子,无法用于围棋分析",
                      "五子棋?",
                      JOptionPane.OK_CANCEL_OPTION);
              if (ret == JOptionPane.CANCEL_OPTION) {
                chkNoCapture.setSelected(false);
                ;
              }
            }
          }
        });

    JLabel lblGomoku =
        new JLabel(resourceBundle.getString("ConfigDialog2.lblGomoku")); // "五子棋"); // $NON-NLS-1$
    lblGomoku.setBounds(312, 639, 110, 15);
    uiTab.add(lblGomoku);

    JLabel lblSubBoardNotRefreshOnMouseOver =
        new JLabel(
            resourceBundle.getString(
                "LizzieConfig.lblSubBoardNotRefreshOnMouseOver")); // ("鼠标悬停在小棋盘上时不刷新"); //
    // $NON-NLS-1$
    lblSubBoardNotRefreshOnMouseOver.setBounds(10, 609, 228, 15);
    uiTab.add(lblSubBoardNotRefreshOnMouseOver);

    chkNoRefreshSub = new JCheckBox(); // $NON-NLS-1$
    chkNoRefreshSub.setBounds(237, 599, 23, 23);
    uiTab.add(chkNoRefreshSub);

    JLabel lblEnableDoubleClickFindMove =
        new JLabel(
            resourceBundle.getString("LizzieConfig.lblEnableDoubleClickFindMove")); // ("启用双击找子");
    lblEnableDoubleClickFindMove.setBounds(312, 609, 214, 15);
    uiTab.add(lblEnableDoubleClickFindMove);

    JLabel lblEnableDragStone =
        new JLabel(resourceBundle.getString("LizzieConfig.lblEnableDragStone")); // ("启用拖拽棋子功能");
    lblEnableDragStone.setBounds(608, 609, 223, 15);
    uiTab.add(lblEnableDragStone);

    chkEnableDoubCli = new JCheckBox(); // $NON-NLS-1$
    chkEnableDoubCli.setBounds(532, 606, 26, 23);
    uiTab.add(chkEnableDoubCli);

    chkEnableDragStone = new JCheckBox();
    chkEnableDragStone.setBounds(837, 606, 26, 23);
    uiTab.add(chkEnableDragStone);

    JLabel lblLizzieCache = new JLabel(resourceBundle.getString("LizzieConfig.lizzieCache"));
    lblLizzieCache.setBounds(608, 470, 122, 15);
    uiTab.add(lblLizzieCache);

    chkLizzieCache = new JCheckBox();
    chkLizzieCache.setBounds(837, 467, 23, 23);
    uiTab.add(chkLizzieCache);

    ImageIcon btnLizzieCacheIcon = new ImageIcon();
    try {
      btnLizzieCacheIcon.setImage(
          ImageIO.read(AnalysisFrame.class.getResourceAsStream("/assets/settings.png")));
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

    JButton btnLizzieCache = new JButton(btnLizzieCacheIcon);
    btnLizzieCache.setBounds(723, 469, 18, 18);

    btnLizzieCache.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            Discribe lizzieCacheDiscribe = new Discribe();
            lizzieCacheDiscribe.setInfo(
                resourceBundle.getString("LizzieConfig.aboutLizzieCache"),
                // "回退局面时,有时候引擎已经遗忘了之前的计算结果会从0计算。如启用Lizzie缓存,则界面上依然显示之前的计算结果,直到新的计算结果总计算量超过以前的结算结果为止。",
                resourceBundle.getString("LizzieConfig.aboutLizzieCacheTitle")); //  "Lizzie缓存说明");
          }
        });
    uiTab.add(btnLizzieCache);

    JLabel lblRightClickFunction =
        new JLabel(resourceBundle.getString("LizzieConfig.lblRightClickFunction")); // "鼠标右键功能");
    lblRightClickFunction.setBounds(10, 639, 139, 15);
    uiTab.add(lblRightClickFunction);

    rdoRightClickMenu =
        new JRadioButton(
            resourceBundle.getString("LizzieConfig.rdoRightClickMenu")); // ("弹出菜单"); // $NON-NLS-1$
    rdoRightClickMenu.setBounds(145, 635, 73, 23);

    rdoRightClickBack =
        new JRadioButton(
            resourceBundle.getString("LizzieConfig.rdoRightClickBack")); // ("回退一手"); // $NON-NLS-1$
    rdoRightClickBack.setBounds(218, 635, 80, 23);

    ButtonGroup bgp = new ButtonGroup();
    bgp.add(rdoRightClickMenu);
    bgp.add(rdoRightClickBack);

    uiTab.add(rdoRightClickMenu);
    uiTab.add(rdoRightClickBack);

    JLabel lblMoveNumInBracnh =
        new JLabel(
            resourceBundle.getString(
                "LizzieConfig.lblMoveNumInBracnh")); // ("分支内手数"); // $NON-NLS-1$
    lblMoveNumInBracnh.setBounds(10, 130, 139, 15);
    uiTab.add(lblMoveNumInBracnh);

    rdoBranchMoveOne =
        new JRadioButton(
            resourceBundle.getString("LizzieConfig.rdoBranchMoveOne")); // ("从1开始"); // $NON-NLS-1$
    rdoBranchMoveOne.setBounds(200, 128, 104, 23);
    uiTab.add(rdoBranchMoveOne);

    rdoBranchMoveContinue =
        new JRadioButton(
            resourceBundle.getString(
                "LizzieConfig.rdoBranchMoveContinue")); // ("继续"); // $NON-NLS-1$
    rdoBranchMoveContinue.setBounds(124, 128, 80, 23);
    uiTab.add(rdoBranchMoveContinue);

    ButtonGroup branchMove = new ButtonGroup();
    branchMove.add(rdoBranchMoveContinue);
    branchMove.add(rdoBranchMoveOne);

    lblShowMoveNumInVariationPane =
        new JLabel(
            resourceBundle.getString(
                "LizzieConfig.lblShowMoveNumInVariationPane")); // ("分支面板上显示手数"); // $NON-NLS-1$
    lblShowMoveNumInVariationPane.setBounds(312, 130, 214, 15);
    uiTab.add(lblShowMoveNumInVariationPane);

    chkShowVarMove = new JCheckBox(""); // $NON-NLS-1$
    chkShowVarMove.setBounds(532, 128, 38, 23);

    if (Lizzie.config.showVarMove) chkShowVarMove.setSelected(true);
    if (Lizzie.config.newMoveNumberInBranch) rdoBranchMoveOne.setSelected(true);
    else rdoBranchMoveContinue.setSelected(true);

    uiTab.add(chkShowVarMove);
    if (Lizzie.config.showRightMenu) {
      rdoRightClickMenu.setSelected(true);
    } else {
      rdoRightClickBack.setSelected(true);
    }

    JLabel lblKifuLoadLast =
        new JLabel(
            resourceBundle.getString(
                "LizzieConfig.lblKifuLoadLast")); // ("加载棋谱后跳转到最后"); // $NON-NLS-1$
    lblKifuLoadLast.setBounds(608, 639, 223, 15);
    uiTab.add(lblKifuLoadLast);

    chkSgfLoadLast = new JCheckBox(); // $NON-NLS-1$
    chkSgfLoadLast.setBounds(837, 636, 27, 23);
    uiTab.add(chkSgfLoadLast);

    lblLoadEstimate =
        new JLabel(
            resourceBundle.getString(
                "LizzieConfig.lblLoadEstimate")); // ("预加载形式判断引擎"); // $NON-NLS-1$
    lblLoadEstimate.setBounds(312, 669, 214, 15);
    uiTab.add(lblLoadEstimate);

    chkAutoLoadEstimate = new JCheckBox(); // $NON-NLS-1$
    chkAutoLoadEstimate.setBounds(532, 666, 30, 23);
    uiTab.add(chkAutoLoadEstimate);

    JLabel lblEstimateEngine =
        new JLabel(resourceBundle.getString("LizzieConfig.lblEstimateEngine")); // ("形势判断引擎");
    lblEstimateEngine.setBounds(10, 669, 139, 15);
    uiTab.add(lblEstimateEngine);

    rdbtnKatago = new JRadioButton("KataGo"); // $NON-NLS-1$
    rdbtnKatago.setBounds(145, 665, 67, 23);
    uiTab.add(rdbtnKatago);

    rdbtnZen = new JRadioButton("Zen"); // $NON-NLS-1$
    rdbtnZen.setBounds(218, 665, 64, 23);
    uiTab.add(rdbtnZen);

    ButtonGroup estimateEngineGroup = new ButtonGroup();
    estimateEngineGroup.add(rdbtnKatago);
    estimateEngineGroup.add(rdbtnZen);

    JLabel lblShowMoveList = new JLabel(resourceBundle.getString("LizzieConfig.lblShowMoveList"));
    lblShowMoveList.setBounds(608, 78, 173, 15);
    uiTab.add(lblShowMoveList);

    chkShowMoveList = new JCheckBox();
    chkShowMoveList.setBounds(837, 76, 45, 23);
    uiTab.add(chkShowMoveList);

    JLabel lblShowIndependentMoveList =
        new JLabel(resourceBundle.getString("LizzieConfig.lblShowIndependentMoveList"));
    lblShowIndependentMoveList.setBounds(10, 156, 173, 15);
    uiTab.add(lblShowIndependentMoveList);

    chkShowIndependentMoveList = new JCheckBox();
    chkShowIndependentMoveList.setBounds(237, 154, 45, 23);
    uiTab.add(chkShowIndependentMoveList);

    JLabel lblShowIndependentHawkEye =
        new JLabel(resourceBundle.getString("LizzieConfig.lblShowIndependentHawkEye"));
    lblShowIndependentHawkEye.setBounds(312, 156, 214, 15);
    uiTab.add(lblShowIndependentHawkEye);

    chkShowIndependentHawkEye = new JCheckBox();
    chkShowIndependentHawkEye.setBounds(532, 154, 45, 23);
    uiTab.add(chkShowIndependentHawkEye);

    JLabel lblIndepentMainBoard =
        new JLabel(resourceBundle.getString("LizzieConfig.lblIndepentMainBoard"));
    lblIndepentMainBoard.setBounds(608, 130, 173, 15);
    uiTab.add(lblIndepentMainBoard);

    JLabel lblIndepentSubBoard =
        new JLabel(resourceBundle.getString("LizzieConfig.lblIndepentSubBoard"));
    lblIndepentSubBoard.setBounds(608, 156, 198, 15);
    uiTab.add(lblIndepentSubBoard);

    chkShowIndependentMainBoard = new JCheckBox(); // $NON-NLS-1$
    chkShowIndependentMainBoard.setBounds(837, 128, 26, 23);
    uiTab.add(chkShowIndependentMainBoard);
    chkShowIndependentMainBoard.setSelected(Lizzie.config.isShowingIndependentMain);

    chkShowIndependentSubBoard = new JComboBox<String>();
    chkShowIndependentSubBoard.addItem(
        resourceBundle.getString("LizzieConfig.chkShowIndependentSubBoard0"));
    chkShowIndependentSubBoard.addItem(
        resourceBundle.getString("LizzieConfig.chkShowIndependentSubBoard1"));
    chkShowIndependentSubBoard.addItem(
        resourceBundle.getString("LizzieConfig.chkShowIndependentSubBoard2"));
    chkShowIndependentSubBoard.setBounds(765, 152, 102, 23);
    uiTab.add(chkShowIndependentSubBoard);

    chkCheckEngineAlive = new JCheckBox();
    chkCheckEngineAlive.setBounds(837, 438, 23, 23);
    uiTab.add(chkCheckEngineAlive);
    chkCheckEngineAlive.setSelected(Lizzie.config.autoCheckEngineAlive);

    JLabel lblCheckEngineAlive =
        new JLabel(resourceBundle.getString("LizzieConfig.chkCheckEngineAlive"));
    lblCheckEngineAlive.setBounds(608, 441, 173, 15);
    uiTab.add(lblCheckEngineAlive);

    JLabel lblShowPvVisits =
        new JLabel(resourceBundle.getString("ConfigDialog2.lblShowPvVisits")); // $NON-NLS-1$
    lblShowPvVisits.setBounds(608, 339, 173, 15);
    uiTab.add(lblShowPvVisits);

    comboBoxPvVisits = new JComboBox<String>();
    comboBoxPvVisits.setBounds(767, 337, 99, 21);
    comboBoxPvVisits.addItem(resourceBundle.getString("FirstUseSettings.rdoNoPvVistits"));
    comboBoxPvVisits.addItem(resourceBundle.getString("FirstUseSettings.rdoLastPvVistits"));
    comboBoxPvVisits.addItem(resourceBundle.getString("FirstUseSettings.rdoEveryPvVistits"));
    uiTab.add(comboBoxPvVisits);

    JLabel lblPvVisitsLimit =
        new JLabel(resourceBundle.getString("ConfigDialog2.lblPvVisitsLimit"));
    lblPvVisitsLimit.setBounds(608, 366, 184, 15);
    uiTab.add(lblPvVisitsLimit);

    txtPvVisitsLimit =
        new JFormattedTextField(
            new InternationalFormatter(nf) {
              protected DocumentFilter getDocumentFilter() {
                return filter;
              }

              private DocumentFilter filter = new DigitOnlyFilter();
            });
    txtPvVisitsLimit.setBounds(800, 363, 66, 21);
    uiTab.add(txtPvVisitsLimit);
    txtPvVisitsLimit.setColumns(10);
    txtPvVisitsLimit.setText(Lizzie.config.pvVisitsLimit + "");

    JLabel lblVariationRemoveDeadChain =
        new JLabel(
            resourceBundle.getString("ConfigDialog2.lblVariationRemoveDeadChain")); // $NON-NLS-1$
    lblVariationRemoveDeadChain.setBounds(608, 312, 223, 15);
    uiTab.add(lblVariationRemoveDeadChain);

    chkVariationRemoveDeadChain = new JCheckBox();
    chkVariationRemoveDeadChain.setBounds(837, 307, 23, 23);
    uiTab.add(chkVariationRemoveDeadChain);

    txtVariationReplayInterval = new JTextField();
    txtVariationReplayInterval.setBounds(803, 668, 52, 18);
    uiTab.add(txtVariationReplayInterval);
    txtVariationReplayInterval.setText(Lizzie.config.replayBranchIntervalSeconds * 1000 + "");

    JLabel lblVariationReplayInterval =
        new JLabel(
            resourceBundle.getString("ConfigDialog2.lblVariationReplayInterval")); // $NON-NLS-1$
    lblVariationReplayInterval.setBounds(608, 669, 189, 15);
    uiTab.add(lblVariationReplayInterval);

    chkShowScoreLeadLine = new JCheckBox();
    chkShowScoreLeadLine.setBounds(532, 201, 26, 23);
    uiTab.add(chkShowScoreLeadLine);

    JLabel lblShowScoreLeadLine =
        new JLabel(resourceBundle.getString("ConfigDialog2.lblShowScoreLeadLine"));
    lblShowScoreLeadLine.setBounds(312, 204, 190, 15);
    uiTab.add(lblShowScoreLeadLine);

    chkShowScoreLeadLine.setSelected(Lizzie.config.showScoreLeadLine);
    chkVariationRemoveDeadChain.setSelected(Lizzie.config.removeDeadChainInVariation);

    JLabel lblShowMouseOverWinrateGraph =
        new JLabel(resourceBundle.getString("ConfigDialog2.lblShowMouseOverWinrateGraph"));
    lblShowMouseOverWinrateGraph.setBounds(312, 366, 367, 15);
    uiTab.add(lblShowMouseOverWinrateGraph);

    chkShowMouseOverWinrateGraph = new JCheckBox();
    chkShowMouseOverWinrateGraph.setBounds(Lizzie.config.isChinese ? 532 : 577, 363, 23, 23);
    uiTab.add(chkShowMouseOverWinrateGraph);

    chkShowMouseOverWinrateGraph.setSelected(Lizzie.config.showMouseOverWinrateGraph);

    JButton btnSetOrder = new JButton(resourceBundle.getString("ConfigDialog2.btnSetOrder"));
    btnSetOrder.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            Lizzie.frame.openSuggestionInfoCustom(Lizzie.frame.configDialog2);
          }
        });
    btnSetOrder.setBounds(310, 337, Lizzie.config.isChinese ? 165 : 240, 23);
    uiTab.add(btnSetOrder);

    JLabel lblUseIinCoords =
        new JLabel(resourceBundle.getString("ConfigDialog2.lblUseIinCoords")); // "在坐标中使用I");
    lblUseIinCoords.setBounds(430, 639, 94, 15);
    uiTab.add(lblUseIinCoords);

    chkUseIinCoordsName = new JCheckBox();
    chkUseIinCoordsName.setBounds(532, 636, 26, 23);
    uiTab.add(chkUseIinCoordsName);
    chkUseIinCoordsName.setSelected(Lizzie.config.useIinCoordsName);

    chkLimitTime = new JCheckBox();
    uiTab.add(chkLimitTime);

    txtMaxAnalyzeTime =
        new JFormattedTextField(
            new InternationalFormatter(nf) {
              protected DocumentFilter getDocumentFilter() {
                return filter;
              }

              private DocumentFilter filter = new DigitOnlyFilter();
            });
    txtMaxAnalyzeTime.setText(String.valueOf(Lizzie.config.maxAnalyzeTimeMillis / 1000));
    uiTab.add(txtMaxAnalyzeTime);

    JLabel lblSeconds = new JLabel(resourceBundle.getString("LizzieConfig.title.seconds"));

    uiTab.add(lblSeconds);

    chkLimitPlayouts = new JCheckBox();
    uiTab.add(chkLimitPlayouts);
    txtLimitPlayouts =
        new JFormattedTextField(
            new InternationalFormatter(nf) {
              protected DocumentFilter getDocumentFilter() {
                return filter;
              }

              private DocumentFilter filter = new DigitOnlyFilter();
            });
    uiTab.add(txtLimitPlayouts);

    JLabel lblLimitPlayouts = new JLabel(resourceBundle.getString("LizzieConfig.playouts"));
    uiTab.add(lblLimitPlayouts);

    chkLimitTime.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            txtMaxAnalyzeTime.setEnabled(chkLimitTime.isSelected());
          }
        });

    chkLimitPlayouts.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            txtLimitPlayouts.setEnabled(chkLimitPlayouts.isSelected());
          }
        });
    chkLimitTime.setSelected(Lizzie.config.limitTime);
    chkLimitPlayouts.setSelected(Lizzie.config.limitPlayout);
    txtLimitPlayouts.setText(Lizzie.config.limitPlayouts + "");
    txtLimitPlayouts.setEnabled(Lizzie.config.limitPlayout);
    txtMaxAnalyzeTime.setEnabled(Lizzie.config.limitTime);

    if (Lizzie.config.isChinese) {
      chkLimitTime.setBounds(102, 412, 20, 23);
      txtMaxAnalyzeTime.setBounds(123, 413, 40, 21);
      lblSeconds.setBounds(165, 415, 63, 16);

      chkLimitPlayouts.setBounds(181, 412, 20, 23);
      txtLimitPlayouts.setBounds(203, 413, 52, 21);
      lblLimitPlayouts.setBounds(257, 415, 54, 16);
    } else {
      chkLimitTime.setBounds(96, 412, 20, 23);
      txtMaxAnalyzeTime.setBounds(117, 413, 40, 21);
      lblSeconds.setBounds(160, 415, 63, 16);

      chkLimitPlayouts.setBounds(209, 412, 20, 23);
      txtLimitPlayouts.setBounds(230, 413, 52, 21);
      lblLimitPlayouts.setBounds(285, 415, 54, 16);
    }

    if (!Lizzie.config.showPvVisits) {
      comboBoxPvVisits.setSelectedIndex(0);
    } else if (!Lizzie.config.showPvVisitsAllMove) {
      comboBoxPvVisits.setSelectedIndex(1);
    } else {
      comboBoxPvVisits.setSelectedIndex(2);
    }

    if (Lizzie.config.extraMode == 8) chkShowIndependentSubBoard.setSelectedIndex(1);
    else if (Lizzie.config.isShowingIndependentMain) chkShowIndependentSubBoard.setSelectedIndex(2);
    else chkShowIndependentSubBoard.setSelectedIndex(0);

    if (Lizzie.config.showListPane()) chkShowMoveList.setSelected(true);
    else chkShowMoveList.setSelected(false);

    if (Lizzie.frame != null
        && Lizzie.frame.movelistframe != null
        && Lizzie.frame.movelistframe.isVisible()) chkShowIndependentHawkEye.setSelected(true);
    else chkShowIndependentHawkEye.setSelected(false);

    if (Lizzie.frame != null
        && Lizzie.frame.analysisFrame != null
        && Lizzie.frame.analysisFrame.isVisible()) chkShowIndependentMoveList.setSelected(true);
    else chkShowIndependentMoveList.setSelected(false);

    if (Lizzie.config.useZenEstimate) {
      rdbtnZen.setSelected(true);
    } else {
      rdbtnKatago.setSelected(true);
    }

    if (Lizzie.config.loadEstimateEngine) {
      chkAutoLoadEstimate.setSelected(true);
    }

    if (Lizzie.config.loadSgfLast) {
      chkSgfLoadLast.setSelected(true);
    }
    if (Lizzie.config.advanceTimeSettings) txtMaxGameThinkingTime.setEnabled(false);
    else txtAdvanceTime.setEditable(false);
    if (Lizzie.config.noCapture) chkNoCapture.setSelected(true);

    if (Lizzie.config.allowDoubleClick) chkEnableDoubCli.setSelected(true);
    if (Lizzie.config.allowDrag) chkEnableDragStone.setSelected(true);
    if (Lizzie.config.noRefreshOnSub) chkNoRefreshSub.setSelected(true);
    if (Lizzie.config.enableLizzieCache) chkLizzieCache.setSelected(true);

    new ComsWorker(this).execute();
    setBoardSize();
    setShowMoveNumber();
    setShowWinrateSide();
    setLocationRelativeTo(getOwner());
  }

  class ComsWorker extends SwingWorker<Void, Integer> {

    private JDialog owner;

    public ComsWorker(JDialog owner) {
      this.owner = owner;
    }

    @Override
    protected Void doInBackground() throws Exception {

      isLoadedTheme = false;
      File themeFolder = new File(Theme.pathPrefix);
      File[] themes =
          themeFolder.listFiles(
              new FileFilter() {
                public boolean accept(File f) {
                  return f.isDirectory() && !".".equals(f.getName());
                }
              });
      List<String> themeList =
          themes == null
              ? new ArrayList<String>()
              : Arrays.asList(themes).stream().map(t -> t.getName()).collect(Collectors.toList());
      themeList.add(0, resourceBundle.getString("LizzieConfig.title.defaultTheme"));

      JLabel lblThemes = new JLabel(resourceBundle.getString("LizzieConfig.title.theme"));
      lblThemes.setBounds(10, 11, 163, 20);
      themeTab.add(lblThemes);

      JButton btnDeleteTheme = new JButton(resourceBundle.getString("ConfigDialog2.deleteTheme"));
      btnDeleteTheme.addActionListener(
          new ActionListener() {
            public void actionPerformed(ActionEvent e) {
              SwingUtilities.invokeLater(
                  new Runnable() {
                    public void run() {
                      int ret =
                          JOptionPane.showConfirmDialog(
                              Lizzie.frame.configDialog2,
                              resourceBundle.getString("ConfigDialog2.deleteThemeWarning")
                                  + "\'"
                                  + cmbThemes.getSelectedItem().toString()
                                  + "\'"
                                  + " ?",
                              resourceBundle.getString("LizzieFrame.warning"),
                              JOptionPane.OK_CANCEL_OPTION);
                      if (ret == JOptionPane.YES_NO_OPTION) {
                        String currentRealPath = "";
                        File file = new File("");
                        try {
                          currentRealPath = file.getCanonicalPath();
                        } catch (IOException e) {
                          // TODO Auto-generated catch block
                          e.printStackTrace();
                        }
                        if (Utils.deleteDir(
                            new File(
                                currentRealPath
                                    + Utils.pwd
                                    + "theme"
                                    + Utils.pwd
                                    + cmbThemes.getSelectedItem().toString()))) {
                          Utils.showMsg(
                              resourceBundle.getString("ConfigDialog2.deleteThemeSuccess"));
                          setVisible(false);
                          Lizzie.frame.openConfigDialog2(1);
                        } else
                          Utils.showMsg(
                              resourceBundle.getString("ConfigDialog2.deleteThemeFailed"));
                      }
                    }
                  });
            }
          });
      btnDeleteTheme.setMargin(new Insets(0, 0, 0, 0));
      btnDeleteTheme.setBounds(435, 11, 50, 20);
      themeTab.add(btnDeleteTheme);

      cmbThemes = new JComboBox(themeList.toArray(new String[0]));
      cmbThemes.addItemListener(
          new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
              if (isLoadedTheme) readThemeValues();
              if (cmbThemes.getSelectedIndex() == 0) btnDeleteTheme.setEnabled(false);
              else btnDeleteTheme.setEnabled(true);
            }
          });
      cmbThemes.setBounds(175, 11, 199, 20);
      themeTab.add(cmbThemes);

      JButton btnAddTheme = new JButton(resourceBundle.getString("ConfigDialog2.addTheme"));
      btnAddTheme.addActionListener(
          new ActionListener() {
            public void actionPerformed(ActionEvent e) {
              SwingUtilities.invokeLater(
                  new Runnable() {
                    public void run() {
                      String themeName =
                          JOptionPane.showInputDialog(
                              Lizzie.frame.configDialog2,
                              null,
                              resourceBundle.getString("ConfigDialog2.inputThemeNameTitle"),
                              JOptionPane.INFORMATION_MESSAGE);
                      if (themeName != null) {
                        for (String name : themeList) {
                          if (themeName.toLowerCase().equals(name.toLowerCase())) {
                            Utils.showMsg(
                                resourceBundle.getString("ConfigDialog2.duplicateThemeName"));
                            return;
                          }
                        }
                        Utils.addNewThemeAs(themeName);
                        setVisible(false);
                        Lizzie.frame.openConfigDialog2(1);
                      }
                    }
                  });
            }
          });
      btnAddTheme.setMargin(new Insets(0, 0, 0, 0));
      btnAddTheme.setBounds(385, 11, 50, 20);
      themeTab.add(btnAddTheme);

      JLabel lblWinrateStrokeWidth =
          new JLabel(resourceBundle.getString("LizzieConfig.title.winrateStrokeWidth"));
      lblWinrateStrokeWidth.setBounds(10, 44, 163, 16);
      themeTab.add(lblWinrateStrokeWidth);
      spnWinrateStrokeWidth = new JSpinner();
      spnWinrateStrokeWidth.setModel(new SpinnerNumberModel(1.7, 0.1, 10, 0.1));
      spnWinrateStrokeWidth.setBounds(175, 42, 69, 20);
      themeTab.add(spnWinrateStrokeWidth);

      JLabel lblMinimumBlunderBarWidth =
          new JLabel(resourceBundle.getString("LizzieConfig.title.minimumBlunderBarWidth"));
      lblMinimumBlunderBarWidth.setBounds(10, 74, 163, 16);
      themeTab.add(lblMinimumBlunderBarWidth);
      spnMinimumBlunderBarWidth = new JSpinner();
      spnMinimumBlunderBarWidth.setModel(new SpinnerNumberModel(1, 1, 10, 1));
      spnMinimumBlunderBarWidth.setBounds(175, 72, 69, 20);
      themeTab.add(spnMinimumBlunderBarWidth);
      spnShadowSize = new JSpinner();
      spnShadowSize.setModel(new SpinnerNumberModel(50, 1, 150, 1));
      spnShadowSize.setBounds(175, 102, 69, 20);
      themeTab.add(spnShadowSize);

      fontList =
          Arrays.asList(
                  GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames())
              .stream()
              .collect(Collectors.toList());
      // fontList.add(0, " ");
      fontList.add(0, resourceBundle.getString("FontList.systemDefault"));
      fontList.add(0, resourceBundle.getString("FontList.lizzieDefault"));
      String fonts[] = fontList.toArray(new String[0]);

      JLabel lblFontName = new JLabel(resourceBundle.getString("LizzieConfig.title.fontName"));
      lblFontName.setBounds(10, 134, 163, 16);
      themeTab.add(lblFontName);
      cmbFontName = new JComboBox(fonts);
      cmbFontName.setMaximumRowCount(16);
      cmbFontName.setBounds(175, 133, 200, 20);
      cmbFontName.setRenderer(new FontComboBoxRenderer());
      cmbFontName.addItemListener(
          new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
              String fontName = (String) e.getItem();
              if (fontName.equals("Lizzie默认") || fontName.equals("Lizzie Default"))
                cmbFontName.setFont(Lizzie.frame.uiFont);
              else
                cmbFontName.setFont(
                    new Font(fontName, Font.PLAIN, cmbUiFontName.getFont().getSize()));
            }
          });
      themeTab.add(cmbFontName);

      JLabel lblUiFontName = new JLabel(resourceBundle.getString("LizzieConfig.title.uiFontName"));
      lblUiFontName.setBounds(10, 164, 163, 16);
      themeTab.add(lblUiFontName);
      cmbUiFontName = new JComboBox(fonts);
      cmbUiFontName.setMaximumRowCount(16);
      cmbUiFontName.setBounds(175, 163, 200, 20);
      cmbUiFontName.setRenderer(new UiFontComboBoxRenderer());
      cmbUiFontName.addItemListener(
          new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
              cmbUiFontName.setFont(
                  new Font((String) e.getItem(), Font.PLAIN, cmbFontName.getFont().getSize()));
            }
          });
      themeTab.add(cmbUiFontName);

      JLabel lblWinrateFontName =
          new JLabel(resourceBundle.getString("LizzieConfig.title.winrateFontName"));
      lblWinrateFontName.setBounds(10, 194, 163, 16);
      themeTab.add(lblWinrateFontName);
      cmbWinrateFontName = new JComboBox(fonts);
      cmbWinrateFontName.setMaximumRowCount(16);
      cmbWinrateFontName.setBounds(175, 193, 200, 20);
      cmbWinrateFontName.setRenderer(new WinrateFontComboBoxRenderer());
      cmbWinrateFontName.addItemListener(
          new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
              String fontName = (String) e.getItem();
              if (fontName.equals("Lizzie默认") || fontName.equals("Lizzie Default"))
                cmbWinrateFontName.setFont(Lizzie.frame.uiFont);
              else
                cmbWinrateFontName.setFont(
                    new Font(fontName, Font.PLAIN, cmbUiFontName.getFont().getSize()));
            }
          });
      themeTab.add(cmbWinrateFontName);

      JLabel lblBackgroundPath =
          new JLabel(resourceBundle.getString("LizzieConfig.title.backgroundPath"));
      lblBackgroundPath.setHorizontalAlignment(SwingConstants.LEFT);
      lblBackgroundPath.setBounds(175, 226, 163, 16);
      themeTab.add(lblBackgroundPath);
      txtBackgroundPath = new JTextField();
      txtBackgroundPath.setText((String) null);
      txtBackgroundPath.setColumns(10);
      txtBackgroundPath.setBounds(336, 225, 421, 20);
      themeTab.add(txtBackgroundPath);

      JLabel lblBoardPath = new JLabel(resourceBundle.getString("LizzieConfig.title.boardPath"));
      lblBoardPath.setHorizontalAlignment(SwingConstants.LEFT);
      lblBoardPath.setBounds(175, 256, 163, 16);
      themeTab.add(lblBoardPath);
      txtBoardPath = new JTextField();
      txtBoardPath.setText((String) null);
      txtBoardPath.setColumns(10);
      txtBoardPath.setBounds(336, 255, 421, 20);
      themeTab.add(txtBoardPath);

      JLabel lblBlackStonePath =
          new JLabel(resourceBundle.getString("LizzieConfig.title.blackStonePath"));
      lblBlackStonePath.setHorizontalAlignment(SwingConstants.LEFT);
      lblBlackStonePath.setBounds(175, 286, 163, 16);
      themeTab.add(lblBlackStonePath);
      txtBlackStonePath = new JTextField();
      txtBlackStonePath.setText((String) null);
      txtBlackStonePath.setColumns(10);
      txtBlackStonePath.setBounds(336, 285, 421, 20);
      themeTab.add(txtBlackStonePath);

      JLabel lblWhiteStonePath =
          new JLabel(resourceBundle.getString("LizzieConfig.title.whiteStonePath"));
      lblWhiteStonePath.setHorizontalAlignment(SwingConstants.LEFT);
      lblWhiteStonePath.setBounds(175, 316, 163, 16);
      themeTab.add(lblWhiteStonePath);
      txtWhiteStonePath = new JTextField();
      txtWhiteStonePath.setText((String) null);
      txtWhiteStonePath.setColumns(10);
      txtWhiteStonePath.setBounds(336, 315, 421, 20);
      themeTab.add(txtWhiteStonePath);

      JLabel lblWinrateLineColorTitle =
          new JLabel(resourceBundle.getString("LizzieConfig.title.winrateLineColor"));
      lblWinrateLineColorTitle.setHorizontalAlignment(SwingConstants.LEFT);
      lblWinrateLineColorTitle.setBounds(10, 345, 163, 16);
      themeTab.add(lblWinrateLineColorTitle);
      lblWinrateLineColor = new ColorLabel(owner);
      lblWinrateLineColor.setBounds(175, 350, 167, 9);
      themeTab.add(lblWinrateLineColor);

      JLabel lblWinrateMissLineColorTitle =
          new JLabel(resourceBundle.getString("LizzieConfig.title.winrateMissLineColor"));
      lblWinrateMissLineColorTitle.setHorizontalAlignment(SwingConstants.LEFT);
      lblWinrateMissLineColorTitle.setBounds(10, 370, 163, 16);
      themeTab.add(lblWinrateMissLineColorTitle);
      lblWinrateMissLineColor = new ColorLabel(owner);
      lblWinrateMissLineColor.setBounds(175, 375, 167, 9);
      themeTab.add(lblWinrateMissLineColor);

      JLabel lblBlunderBarColorTitle =
          new JLabel(resourceBundle.getString("LizzieConfig.title.blunderBarColor"));
      lblBlunderBarColorTitle.setHorizontalAlignment(SwingConstants.LEFT);
      lblBlunderBarColorTitle.setBounds(10, 395, 163, 16);
      themeTab.add(lblBlunderBarColorTitle);
      lblBlunderBarColor = new ColorLabel(owner);
      lblBlunderBarColor.setBounds(175, 400, 167, 9);
      themeTab.add(lblBlunderBarColor);

      JLabel lblScoreMeanLineColorTitle =
          new JLabel(resourceBundle.getString("LizzieConfig.title.scoreMeanLineColor"));
      lblScoreMeanLineColorTitle.setHorizontalAlignment(SwingConstants.LEFT);
      lblScoreMeanLineColorTitle.setBounds(10, 420, 163, 16);
      themeTab.add(lblScoreMeanLineColorTitle);
      lblScoreMeanLineColor = new ColorLabel(owner);
      lblScoreMeanLineColor.setBounds(175, 425, 167, 9);
      themeTab.add(lblScoreMeanLineColor);

      JLabel lblCommentBackgroundColorTitle =
          new JLabel(resourceBundle.getString("LizzieConfig.title.commentBackgroundColor"));
      lblCommentBackgroundColorTitle.setHorizontalAlignment(SwingConstants.LEFT);
      lblCommentBackgroundColorTitle.setBounds(370, 345, 148, 16);
      themeTab.add(lblCommentBackgroundColorTitle);
      lblCommentBackgroundColor = new ColorLabel(owner);
      lblCommentBackgroundColor.setBounds(529, 342, 22, 22);
      themeTab.add(lblCommentBackgroundColor);

      JLabel lblCommentFontColorTitle =
          new JLabel(resourceBundle.getString("LizzieConfig.title.commentFontColor"));
      lblCommentFontColorTitle.setHorizontalAlignment(SwingConstants.LEFT);
      lblCommentFontColorTitle.setBounds(370, 375, 148, 16);
      themeTab.add(lblCommentFontColorTitle);
      lblCommentFontColor = new ColorLabel(owner);
      lblCommentFontColor.setBounds(529, 372, 22, 22);
      themeTab.add(lblCommentFontColor);

      // JLabel lblVarPanelTitile = new JLabel("分支面板背景");
      // lblVarPanelTitile.setHorizontalAlignment(SwingConstants.LEFT);
      // lblVarPanelTitile.setBounds(370, 465, 148, 16);
      //  themeTab.add(lblVarPanelTitile);
      //  lblVarPanelColor = new ColorLabel(owner);
      //   lblVarPanelColor.setBounds(529, 462, 22, 22);
      // themeTab.add(lblVarPanelColor);

      JLabel labelBestMoveColor =
          new JLabel(resourceBundle.getString("ConfigDialog2.lblBestMoveColor")); // ("第一选点颜色");
      labelBestMoveColor.setHorizontalAlignment(SwingConstants.LEFT);
      labelBestMoveColor.setBounds(370, 435, 148, 16);
      themeTab.add(labelBestMoveColor);
      lblBestMoveColor = new ColorLabel(owner);
      lblBestMoveColor.setBounds(529, 432, 22, 22);
      themeTab.add(lblBestMoveColor);

      NumberFormat nf = NumberFormat.getIntegerInstance();
      JLabel lblBackgroundFilter =
          new JLabel(
              resourceBundle.getString("ConfigDialog2.lblBackgroundFilter")); // ("面板背景模糊程度");
      lblBackgroundFilter.setHorizontalAlignment(SwingConstants.LEFT);
      lblBackgroundFilter.setBounds(370, 465, 148, 16);
      themeTab.add(lblBackgroundFilter);
      txtBackgroundFilter =
          new JFormattedTextField(
              new InternationalFormatter(nf) {
                protected DocumentFilter getDocumentFilter() {
                  return filter;
                }

                private DocumentFilter filter = new DigitOnlyFilter();
              });
      txtBackgroundFilter.setBounds(529, 463, 52, 24);
      themeTab.add(txtBackgroundFilter);
      //      JButton applyBackgroundFilter = new JButton("应用");
      //      applyBackgroundFilter.addActionListener(
      //          new ActionListener() {
      //            public void actionPerformed(ActionEvent e) {
      //              Lizzie.frame.testFilter(txtFieldIntValue(txtBackgroundFilter));
      //            }
      //          });
      //      applyBackgroundFilter.setBounds(588, 463, 60, 24);
      //      themeTab.add(applyBackgroundFilter);

      JLabel lblCommentFontSize =
          new JLabel(resourceBundle.getString("LizzieConfig.title.commentFontSize"));
      lblCommentFontSize.setHorizontalAlignment(SwingConstants.LEFT);
      lblCommentFontSize.setBounds(370, 405, 148, 16);
      themeTab.add(lblCommentFontSize);
      txtCommentFontSize =
          new JFormattedTextField(
              new InternationalFormatter(nf) {
                protected DocumentFilter getDocumentFilter() {
                  return filter;
                }

                private DocumentFilter filter = new DigitOnlyFilter();
              });
      txtCommentFontSize.setBounds(529, 403, 52, 24);
      themeTab.add(txtCommentFontSize);

      JLabel lblStoneIndicatorType =
          new JLabel(resourceBundle.getString("LizzieConfig.title.stoneIndicatorType"));
      lblStoneIndicatorType.setBounds(10, 442, 163, 16);
      themeTab.add(lblStoneIndicatorType);
      rdoStoneIndicatorDelta =
          new JRadioButton(resourceBundle.getString("ConfigDialog2.triangle")); // ("三角");
      rdoStoneIndicatorDelta.setBounds(170, 439, 52, 23);
      themeTab.add(rdoStoneIndicatorDelta);

      rdoStoneIndicatorCircle =
          new JRadioButton(resourceBundle.getString("ConfigDialog2.circle")); // ("圆圈");
      rdoStoneIndicatorCircle.setBounds(220, 439, 52, 23);
      themeTab.add(rdoStoneIndicatorCircle);
      rdoStoneIndicatorSolid =
          new JRadioButton(resourceBundle.getString("ConfigDialog2.solid")); // ("实心");
      rdoStoneIndicatorSolid.setBounds(270, 439, 52, 23);
      themeTab.add(rdoStoneIndicatorSolid);
      rdoStoneIndicatorNo =
          new JRadioButton(resourceBundle.getString("ConfigDialog2.empty")); // ("无");
      rdoStoneIndicatorNo.setBounds(320, 439, 46, 23);
      themeTab.add(rdoStoneIndicatorNo);

      ButtonGroup stoneIndicatorTypeGroup = new ButtonGroup();
      stoneIndicatorTypeGroup.add(rdoStoneIndicatorDelta);
      stoneIndicatorTypeGroup.add(rdoStoneIndicatorCircle);
      stoneIndicatorTypeGroup.add(rdoStoneIndicatorSolid);
      stoneIndicatorTypeGroup.add(rdoStoneIndicatorNo);

      JLabel lblShowCommentNodeColor =
          new JLabel(resourceBundle.getString("LizzieConfig.title.showCommentNodeColor"));
      lblShowCommentNodeColor.setBounds(10, 465, 163, 16);
      themeTab.add(lblShowCommentNodeColor);
      chkShowCommentNodeColor = new JCheckBox("");
      chkShowCommentNodeColor.setBounds(170, 462, 33, 23);
      themeTab.add(chkShowCommentNodeColor);

      JLabel lblCommentNodeColorTitle =
          new JLabel(resourceBundle.getString("LizzieConfig.title.commentNodeColor"));
      lblCommentNodeColorTitle.setHorizontalAlignment(SwingConstants.LEFT);
      lblCommentNodeColorTitle.setBounds(210, 465, 138, 16);
      themeTab.add(lblCommentNodeColorTitle);
      lblCommentNodeColor = new ColorLabel(owner);
      lblCommentNodeColor.setBounds(Lizzie.config.isChinese ? 311 : 341, 462, 22, 22);
      themeTab.add(lblCommentNodeColor);

      JLabel lblBlunderNodes =
          new JLabel(resourceBundle.getString("LizzieConfig.title.blunderNodes"));
      lblBlunderNodes.setHorizontalAlignment(SwingConstants.LEFT);
      lblBlunderNodes.setBounds(10, 497, 163, 16);
      themeTab.add(lblBlunderNodes);
      tblBlunderNodes = new JTable();
      columsBlunderNodes =
          new String[] {
            resourceBundle.getString("LizzieConfig.title.blunderThresholds"),
            resourceBundle.getString("LizzieConfig.title.blunderColor")
          };
      JScrollPane pnlScrollBlunderNodes = new JScrollPane();
      pnlScrollBlunderNodes.setViewportView(tblBlunderNodes);
      pnlScrollBlunderNodes.setBounds(175, 497, 199, 108);
      themeTab.add(pnlScrollBlunderNodes);

      JButton btnAdd = new JButton(resourceBundle.getString("LizzieConfig.button.add"));
      btnAdd.setBounds(80, 527, 89, 23);
      btnAdd.addActionListener(
          new ActionListener() {
            public void actionPerformed(ActionEvent e) {
              ((BlunderNodeTableModel) tblBlunderNodes.getModel()).addRow(null, Color.WHITE);
            }
          });
      themeTab.add(btnAdd);

      JButton btnRemove = new JButton(resourceBundle.getString("LizzieConfig.button.remove"));
      btnRemove.setBounds(80, 557, 89, 23);
      btnRemove.addActionListener(
          new ActionListener() {
            public void actionPerformed(ActionEvent e) {
              ((BlunderNodeTableModel) tblBlunderNodes.getModel())
                  .removeRow(tblBlunderNodes.getSelectedRow());
            }
          });
      themeTab.add(btnRemove);

      btnBackgroundPath = new JButton("...");
      btnBackgroundPath.addActionListener(
          new ActionListener() {
            public void actionPerformed(ActionEvent e) {
              String ip = getImagePath();
              if (!ip.isEmpty()) {
                txtBackgroundPath.setText(ip);
                pnlBoardPreview.repaint();
              }
            }
          });
      btnBackgroundPath.setBounds(759, 223, 40, 26);
      themeTab.add(btnBackgroundPath);

      btnBoardPath = new JButton("...");
      btnBoardPath.addActionListener(
          new ActionListener() {
            public void actionPerformed(ActionEvent e) {
              String ip = getImagePath();
              if (!ip.isEmpty()) {
                txtBoardPath.setText(ip);
                pnlBoardPreview.repaint();
              }
            }
          });
      btnBoardPath.setBounds(759, 253, 40, 26);
      themeTab.add(btnBoardPath);

      btnBlackStonePath = new JButton("...");
      btnBlackStonePath.addActionListener(
          new ActionListener() {
            public void actionPerformed(ActionEvent e) {
              String ip = getImagePath();
              if (!ip.isEmpty()) {
                txtBlackStonePath.setText(ip);
                pnlBoardPreview.repaint();
              }
            }
          });
      btnBlackStonePath.setBounds(759, 283, 40, 26);
      themeTab.add(btnBlackStonePath);

      btnWhiteStonePath = new JButton("...");
      btnWhiteStonePath.addActionListener(
          new ActionListener() {
            public void actionPerformed(ActionEvent e) {
              String ip = getImagePath();
              if (!ip.isEmpty()) {
                txtWhiteStonePath.setText(ip);
                pnlBoardPreview.repaint();
              }
            }
          });
      btnWhiteStonePath.setBounds(759, 313, 40, 26);
      themeTab.add(btnWhiteStonePath);

      cmbThemes.setSelectedItem(
          Lizzie.config.uiConfig.optString(
              "theme", resourceBundle.getString("LizzieConfig.title.defaultTheme")));
      if (cmbThemes.getSelectedIndex() == 0) btnDeleteTheme.setEnabled(false);
      else btnDeleteTheme.setEnabled(true);

      chkShowStoneShaow = new JCheckBox(resourceBundle.getString("LizzieConfig.title.shadowSize"));
      chkShowStoneShaow.setBounds(6, 101, 131, 23);
      themeTab.add(chkShowStoneShaow);
      chkShowStoneShaow.addActionListener(
          new ActionListener() {
            public void actionPerformed(ActionEvent e) {
              spnShadowSize.setEnabled(chkShowStoneShaow.isSelected());
            }
          });

      lblPureBackgroundColor = new ColorLabel(owner);
      lblPureBackgroundColor.setBounds(Lizzie.config.isChinese ? 86 : 126, 223, 22, 22);
      lblPureBackgroundColor.setColor(Color.BLACK);
      themeTab.add(lblPureBackgroundColor);
      chkPureBackground =
          new JCheckBox(resourceBundle.getString("LizzieConfig.title.chkPureBackground"));
      chkPureBackground.setBounds(6, 223, Lizzie.config.isChinese ? 80 : 120, 23);
      themeTab.add(chkPureBackground);
      chkPureBackground.addActionListener(
          new ActionListener() {
            public void actionPerformed(ActionEvent e) {
              if (cmbThemes.getSelectedIndex() > 0) {
                btnBackgroundPath.setEnabled(!chkPureBackground.isSelected());
                txtBackgroundPath.setEnabled(!chkPureBackground.isSelected());
              }
              txtBackgroundFilter.setEnabled(!chkPureBackground.isSelected());
            }
          });

      lblPureBoardColor = new ColorLabel(owner);
      lblPureBoardColor.setBounds(Lizzie.config.isChinese ? 86 : 126, 253, 22, 22);
      lblPureBoardColor.setColor(Color.BLACK);
      themeTab.add(lblPureBoardColor);

      chkPureBoard = new JCheckBox(resourceBundle.getString("LizzieConfig.title.chkPureBoard"));
      chkPureBoard.setBounds(6, 253, Lizzie.config.isChinese ? 80 : 120, 23);
      themeTab.add(chkPureBoard);
      chkPureBoard.addActionListener(
          new ActionListener() {
            public void actionPerformed(ActionEvent e) {
              if (cmbThemes.getSelectedIndex() > 0) {
                btnBoardPath.setEnabled(!chkPureBoard.isSelected());
                txtBoardPath.setEnabled(!chkPureBoard.isSelected());
              }
            }
          });

      chkPureStone = new JCheckBox(resourceBundle.getString("LizzieConfig.title.chkPureStone"));
      chkPureStone.setBounds(6, 283, 103, 23);
      themeTab.add(chkPureStone);
      chkPureStone.addActionListener(
          new ActionListener() {
            public void actionPerformed(ActionEvent e) {
              if (cmbThemes.getSelectedIndex() > 0) {
                btnBlackStonePath.setEnabled(!chkPureStone.isSelected());
                btnWhiteStonePath.setEnabled(!chkPureStone.isSelected());
                txtBlackStonePath.setEnabled(!chkPureStone.isSelected());
                txtWhiteStonePath.setEnabled(!chkPureStone.isSelected());
              }
            }
          });

      readThemeValues();

      pnlBoardPreview =
          new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
              super.paintComponent(g);
              if (g instanceof Graphics2D) {
                int width = getWidth();
                int height = getHeight();
                Graphics2D bsGraphics = (Graphics2D) g;
                Paint originalPaint = bsGraphics.getPaint();
                bsGraphics.setRenderingHint(
                    RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                bsGraphics.setRenderingHint(KEY_ANTIALIASING, VALUE_ANTIALIAS_ON);

                BufferedImage backgroundImage = null;
                try {
                  if (cmbThemes.getSelectedIndex() <= 0) {
                    backgroundImage =
                        ImageIO.read(getClass().getResourceAsStream(txtBackgroundPath.getText()));
                  } else {
                    backgroundImage =
                        ImageIO.read(
                            new File(
                                theme == null ? "" : theme.path + txtBackgroundPath.getText()));
                  }
                  TexturePaint paint =
                      new TexturePaint(
                          backgroundImage,
                          new Rectangle(
                              0, 0, backgroundImage.getWidth(), backgroundImage.getHeight()));
                  if (chkPureBackground.isSelected()) g.setColor(lblPureBackgroundColor.getColor());
                  else bsGraphics.setPaint(paint);
                  int drawWidth = max(backgroundImage.getWidth(), width);
                  int drawHeight = max(backgroundImage.getHeight(), height);
                  bsGraphics.fill(new Rectangle(0, 0, drawWidth, drawHeight));
                  bsGraphics.setPaint(originalPaint);
                } catch (IOException e0) {
                }
                BufferedImage boardImage = null;
                try {
                  if (cmbThemes.getSelectedIndex() <= 0) {
                    boardImage =
                        ImageIO.read(getClass().getResourceAsStream(txtBoardPath.getText()));
                  } else {
                    boardImage =
                        ImageIO.read(
                            new File(theme == null ? "" : theme.path + txtBoardPath.getText()));
                  }
                  TexturePaint paint =
                      new TexturePaint(
                          boardImage,
                          new Rectangle(0, 0, boardImage.getWidth(), boardImage.getHeight()));
                  if (chkPureBoard.isSelected()) g.setColor(lblPureBoardColor.getColor());
                  else bsGraphics.setPaint(paint);
                  int drawWidth = max(boardImage.getWidth(), width);
                  int drawHeight = max(boardImage.getHeight(), height);
                  bsGraphics.fill(new Rectangle(30, 30, drawWidth, drawHeight));
                  bsGraphics.setPaint(originalPaint);
                } catch (IOException e0) {
                }
                // Draw the lines
                int x = 60;
                int y = 60;
                int squareLength = 30;
                int stoneRadius = squareLength < 4 ? 1 : squareLength / 2 - 1;
                int size = stoneRadius * 2 + 1;
                double r = stoneRadius * (int) spnShadowSize.getValue() / 100;
                int shadowSize = (int) (r * 0.2) == 0 ? 1 : (int) (r * 0.2);
                int fartherShadowSize = (int) (r * 0.17) == 0 ? 1 : (int) (r * 0.17);
                int stoneX = x + squareLength * 2;
                int stoneY = y + squareLength * 3;

                g.setColor(Color.BLACK);
                for (int i = 0; i < Board.boardWidth; i++) {
                  g.drawLine(x, y + squareLength * i, height, y + squareLength * i);
                }
                for (int i = 0; i < Board.boardHeight; i++) {
                  g.drawLine(x + squareLength * i, y, x + squareLength * i, width);
                }

                BufferedImage blackStoneImage = null;
                try {
                  if (cmbThemes.getSelectedIndex() <= 0) {
                    blackStoneImage =
                        ImageIO.read(getClass().getResourceAsStream(txtBlackStonePath.getText()));
                  } else {
                    blackStoneImage =
                        ImageIO.read(
                            new File(
                                theme == null ? "" : theme.path + txtBlackStonePath.getText()));
                  }
                  BufferedImage stoneImage = new BufferedImage(size, size, TYPE_INT_ARGB);
                  if (chkShowStoneShaow.isSelected()) {
                    RadialGradientPaint TOP_GRADIENT_PAINT =
                        new RadialGradientPaint(
                            new Point2D.Float(stoneX, stoneY),
                            stoneRadius + shadowSize,
                            new float[] {0.3f, 1.0f},
                            new Color[] {new Color(50, 50, 50, 150), new Color(0, 0, 0, 0)});
                    RadialGradientPaint LOWER_RIGHT_GRADIENT_PAINT =
                        new RadialGradientPaint(
                            new Point2D.Float(stoneX + shadowSize, stoneY + shadowSize),
                            stoneRadius + fartherShadowSize,
                            new float[] {0.6f, 1.0f},
                            new Color[] {new Color(0, 0, 0, 140), new Color(0, 0, 0, 0)});
                    originalPaint = bsGraphics.getPaint();

                    bsGraphics.setPaint(TOP_GRADIENT_PAINT);
                    bsGraphics.fillOval(
                        stoneX - stoneRadius - shadowSize,
                        stoneY - stoneRadius - shadowSize,
                        2 * (stoneRadius + shadowSize) + 1,
                        2 * (stoneRadius + shadowSize) + 1);
                    bsGraphics.setPaint(LOWER_RIGHT_GRADIENT_PAINT);
                    bsGraphics.fillOval(
                        stoneX + shadowSize - stoneRadius - fartherShadowSize,
                        stoneY + shadowSize - stoneRadius - fartherShadowSize,
                        2 * (stoneRadius + fartherShadowSize) + 1,
                        2 * (stoneRadius + fartherShadowSize) + 1);
                    bsGraphics.setPaint(originalPaint);
                  }
                  if (chkPureStone.isSelected()) {
                    drawStoneSimple(bsGraphics, stoneX, stoneY, true, stoneRadius);
                  } else {
                    Image img = blackStoneImage;
                    Graphics2D g2 = stoneImage.createGraphics();
                    g2.setRenderingHint(
                        RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                    g2.setRenderingHint(KEY_ANTIALIASING, VALUE_ANTIALIAS_ON);
                    g2.drawImage(
                        img.getScaledInstance(size, size, java.awt.Image.SCALE_SMOOTH), 0, 0, null);
                    g2.dispose();
                    bsGraphics.drawImage(
                        stoneImage, stoneX - stoneRadius, stoneY - stoneRadius, null);
                  }
                } catch (IOException e0) {
                }

                stoneX = x + squareLength * 1;
                stoneY = y + squareLength * 2;

                BufferedImage whiteStoneImage = null;
                try {
                  if (cmbThemes.getSelectedIndex() <= 0) {
                    whiteStoneImage =
                        ImageIO.read(getClass().getResourceAsStream(txtWhiteStonePath.getText()));
                  } else {
                    whiteStoneImage =
                        ImageIO.read(
                            new File(
                                theme == null ? "" : theme.path + txtWhiteStonePath.getText()));
                  }
                  BufferedImage stoneImage = new BufferedImage(size, size, TYPE_INT_ARGB);
                  if (chkShowStoneShaow.isSelected()) {
                    RadialGradientPaint TOP_GRADIENT_PAINT =
                        new RadialGradientPaint(
                            new Point2D.Float(stoneX, stoneY),
                            stoneRadius + shadowSize,
                            new float[] {0.3f, 1.0f},
                            new Color[] {new Color(50, 50, 50, 150), new Color(0, 0, 0, 0)});
                    RadialGradientPaint LOWER_RIGHT_GRADIENT_PAINT =
                        new RadialGradientPaint(
                            new Point2D.Float(stoneX + shadowSize, stoneY + shadowSize),
                            stoneRadius + fartherShadowSize,
                            new float[] {0.6f, 1.0f},
                            new Color[] {new Color(0, 0, 0, 140), new Color(0, 0, 0, 0)});
                    originalPaint = bsGraphics.getPaint();

                    bsGraphics.setPaint(TOP_GRADIENT_PAINT);
                    bsGraphics.fillOval(
                        stoneX - stoneRadius - shadowSize,
                        stoneY - stoneRadius - shadowSize,
                        2 * (stoneRadius + shadowSize) + 1,
                        2 * (stoneRadius + shadowSize) + 1);
                    bsGraphics.setPaint(LOWER_RIGHT_GRADIENT_PAINT);
                    bsGraphics.fillOval(
                        stoneX + shadowSize - stoneRadius - fartherShadowSize,
                        stoneY + shadowSize - stoneRadius - fartherShadowSize,
                        2 * (stoneRadius + fartherShadowSize) + 1,
                        2 * (stoneRadius + fartherShadowSize) + 1);
                    bsGraphics.setPaint(originalPaint);
                  }
                  if (chkPureStone.isSelected()) {
                    drawStoneSimple(bsGraphics, stoneX, stoneY, false, stoneRadius);
                  } else {
                    Image img = whiteStoneImage;
                    Graphics2D g2 = stoneImage.createGraphics();
                    g2.setRenderingHint(
                        RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                    g2.setRenderingHint(KEY_ANTIALIASING, VALUE_ANTIALIAS_ON);
                    g2.drawImage(
                        img.getScaledInstance(size, size, java.awt.Image.SCALE_SMOOTH), 0, 0, null);
                    g2.dispose();
                    bsGraphics.drawImage(
                        stoneImage, stoneX - stoneRadius, stoneY - stoneRadius, null);
                  }
                } catch (IOException e0) {
                }
              }
            }
          };
      pnlBoardPreview.setBounds(530, 11, 200, 200);
      themeTab.add(pnlBoardPreview);
      isLoadedTheme = true;
      javax.swing.Timer timer =
          new javax.swing.Timer(
              100,
              new ActionListener() {
                public void actionPerformed(ActionEvent evt) {
                  pnlBoardPreview.repaint();
                  if (tabbedPane.getSelectedIndex() == 1) tabbedPane.repaint();
                  // table.validate();
                  // table.updateUI();
                }
              });
      timer.start();
      return null;
    }

    //  @Override
    // protected void done() {
    // okButton.setEnabled(true);
    //  pnlBoardPreview.repaint();
    // }
  }

  private void drawStoneSimple(
      Graphics2D g, int centerX, int centerY, boolean isBlack, int stoneRadius) {
    g.setColor(isBlack ? Color.BLACK : Color.WHITE);
    fillCircle(g, centerX, centerY, stoneRadius);
    if (!isBlack) {
      g.setColor(Color.BLACK);
      g.setStroke(new BasicStroke(Math.max(stoneRadius / 16f, 1f)));
      drawCircle(g, centerX, centerY, stoneRadius);
    }
  }

  private void fillCircle(Graphics2D g, int centerX, int centerY, int radius) {
    g.fillOval(centerX - radius, centerY - radius, 2 * radius + 1, 2 * radius + 1);
  }

  private void drawCircle(Graphics2D g, int centerX, int centerY, int radius) {
    // g.setStroke(new BasicStroke(radius / 11.5f));
    g.drawOval(centerX - radius, centerY - radius, 2 * radius, 2 * radius);
  }

  private String getEngineLine() {
    String engineLine = "";
    //    File engineFile = null;
    //    File weightFile = null;
    //    JFileChooser chooser = new JFileChooser(".");
    //    if (isWindows()) {
    //      FileNameExtensionFilter filter =
    //          new FileNameExtensionFilter(
    //              resourceBundle.getString("LizzieConfig.title.engine"), "exe", "bat", "sh");
    //      chooser.setFileFilter(filter);
    //    } else {
    //      setVisible(false);
    //    }
    //    chooser.setMultiSelectionEnabled(false);
    //    chooser.setDialogTitle(resourceBundle.getString("LizzieConfig.prompt.selectEngine"));
    //    int result = chooser.showOpenDialog(this);
    //    if (result == JFileChooser.APPROVE_OPTION) {
    //      engineFile = chooser.getSelectedFile();
    //      if (engineFile != null) {
    //        enginePath = engineFile.getAbsolutePath();
    //        enginePath = relativizePath(engineFile.toPath(), this.curPath);
    //        getCommandHelp();
    //        JFileChooser chooserw = new JFileChooser(".");
    //        chooserw.setMultiSelectionEnabled(false);
    //        chooserw.setDialogTitle(resourceBundle.getString("LizzieConfig.prompt.selectWeight"));
    //        result = chooserw.showOpenDialog(this);
    //        if (result == JFileChooser.APPROVE_OPTION) {
    //          weightFile = chooserw.getSelectedFile();
    //          if (weightFile != null) {
    //            weightPath = relativizePath(weightFile.toPath(), this.curPath);
    //            EngineParameter ep = new EngineParameter(enginePath, weightPath, this);
    //            ep.setVisible(true);
    //            if (!ep.commandLine.isEmpty()) {
    //              engineLine = ep.commandLine;
    //            }
    //          }
    //        }
    //      }
    //    }
    return engineLine;
  }

  private String getImagePath() {
    String imagePath = "";
    File imageFile = null;
    JFileChooser chooser = new JFileChooser(theme.path);
    FileNameExtensionFilter filter =
        new FileNameExtensionFilter("Image", "jpg", "png", "jpeg", "gif");
    chooser.setFileFilter(filter);
    chooser.setMultiSelectionEnabled(false);
    chooser.setDialogTitle("Image");
    int result = chooser.showOpenDialog(this);
    if (result == JFileChooser.APPROVE_OPTION) {
      imageFile = chooser.getSelectedFile();
      if (imageFile != null) {
        imagePath = imageFile.getAbsolutePath();
        imagePath =
            relativizePath(imageFile.toPath(), new File(theme.path).getAbsoluteFile().toPath());
      }
    }
    return imagePath;
  }

  private String relativizePath(Path path, Path curPath) {
    Path relatPath;
    if (path.startsWith(curPath)) {
      relatPath = curPath.relativize(path);
    } else {
      relatPath = path;
    }
    return relatPath.toString();
  }

  private void getCommandHelp() {

    List<String> commands = new ArrayList<String>();
    commands.add(enginePath);
    commands.add("-h");

    ProcessBuilder processBuilder = new ProcessBuilder(commands);
    processBuilder.directory();
    processBuilder.redirectErrorStream(true);
    try {
      Process process = processBuilder.start();
      inputStream = new BufferedInputStream(process.getInputStream());
      ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
      executor.execute(this::read);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private void read() {
    try {
      int c;
      StringBuilder line = new StringBuilder();
      while ((c = inputStream.read()) != -1) {
        line.append((char) c);
      }
      commandHelp = line.toString();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private void applyChange() {
    int[] size = getBoardSize();
    Lizzie.board.reopen(size[0], size[1]);
  }

  private Integer txtFieldIntValue(JTextField txt) {
    if (txt.getText().trim().isEmpty()) {
      return 0;
    } else {
      return Integer.parseInt(txt.getText().trim());
    }
  }

  private class FontComboBoxRenderer<E> extends JLabel implements ListCellRenderer<E> {
    @Override
    public Component getListCellRendererComponent(
        JList<? extends E> list, E value, int index, boolean isSelected, boolean cellHasFocus) {
      final String fontName = (String) value;
      setText(fontName);
      if (fontName != null && (fontName.equals("Lizzie默认") || fontName.equals("Lizzie Default")))
        setFont(Lizzie.frame.uiFont);
      else setFont(new Font(fontName, Font.PLAIN, 12));
      return this;
    }
  }

  private class WinrateFontComboBoxRenderer<E> extends JLabel implements ListCellRenderer<E> {
    @Override
    public Component getListCellRendererComponent(
        JList<? extends E> list, E value, int index, boolean isSelected, boolean cellHasFocus) {
      final String fontName = (String) value;
      setText(fontName);
      if (fontName != null && (fontName.equals("Lizzie默认") || fontName.equals("Lizzie Default")))
        setFont(Lizzie.frame.winrateFont);
      else setFont(new Font(fontName, Font.PLAIN, 12));
      return this;
    }
  }

  private class UiFontComboBoxRenderer<E> extends JLabel implements ListCellRenderer<E> {
    @Override
    public Component getListCellRendererComponent(
        JList<? extends E> list, E value, int index, boolean isSelected, boolean cellHasFocus) {
      final String fontName = (String) value;
      setText(fontName);
      setFont(new Font(fontName, Font.PLAIN, 12));
      return this;
    }
  }

  private class ColorLabel extends JLabel {

    private Color curColor;
    private JDialog owner;

    public ColorLabel(JDialog owner) {
      super();
      setOpaque(true);
      this.owner = owner;

      addMouseListener(
          new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
              ColorLabel cl = (ColorLabel) e.getSource();
              if (!isWindows()) {
                cl.owner.setVisible(false);
              }
              Color color =
                  JColorChooser.showDialog(
                      (Component) e.getSource(), "Choose a color", cl.getColor());
              if (color != null) {
                cl.setColor(color);
              }
              if (!isWindows()) {
                cl.owner.setVisible(true);
              }
            }
          });
    }

    public void setColor(Color c) {
      curColor = c;
      setBackground(c);
    }

    public Color getColor() {
      return curColor;
    }
  }

  private class ColorRenderer extends JLabel implements TableCellRenderer {
    Border unselectedBorder = null;
    Border selectedBorder = null;
    boolean isBordered = true;

    public ColorRenderer(boolean isBordered) {
      this.isBordered = isBordered;
      setOpaque(true);
    }

    public Component getTableCellRendererComponent(
        JTable table, Object color, boolean isSelected, boolean hasFocus, int row, int column) {
      Color newColor = (Color) color;
      setBackground(newColor);
      if (isBordered) {
        if (isSelected) {
          if (selectedBorder == null) {
            selectedBorder =
                BorderFactory.createMatteBorder(2, 5, 2, 5, table.getSelectionBackground());
          }
          setBorder(selectedBorder);
        } else {
          if (unselectedBorder == null) {
            unselectedBorder = BorderFactory.createMatteBorder(2, 5, 2, 5, table.getBackground());
          }
          setBorder(unselectedBorder);
        }
      }

      return this;
    }
  }

  private class ColorEditor extends AbstractCellEditor implements TableCellEditor, ActionListener {
    ColorLabel cl;
    int row;

    public ColorEditor(JDialog owner) {
      cl = new ColorLabel(owner);
    }

    public Object getCellEditorValue() {
      return cl.getColor();
    }

    public Component getTableCellEditorComponent(
        JTable table, Object value, boolean isSelected, int row, int column) {
      this.row = row;
      cl.setColor((Color) value);
      return cl;
    }

    @Override
    public void actionPerformed(ActionEvent e) {}
  }

  private class LinkLabel extends JTextPane {
    public LinkLabel(String text) {
      super();

      HTMLDocument htmlDoc;
      HtmlKit htmlKit;
      StyleSheet htmlStyle;

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
              + Lizzie.config.fontName
              + ", Consolas, Menlo, Monaco, 'Ubuntu Mono', monospace;"
              + (Lizzie.config.commentFontSize > 0
                  ? "font-size:" + Lizzie.config.commentFontSize
                  : "")
              + "}";
      htmlStyle.addRule(style);

      setEditorKit(htmlKit);
      setDocument(htmlDoc);
      setText(text);
      setEditable(false);
      setOpaque(false);
      putClientProperty(JEditorPane.HONOR_DISPLAY_PROPERTIES, Boolean.TRUE);
      addHyperlinkListener(
          new HyperlinkListener() {
            public void hyperlinkUpdate(HyperlinkEvent e) {
              if (HyperlinkEvent.EventType.ACTIVATED.equals(e.getEventType())) {
                if (Desktop.isDesktopSupported()) {
                  try {
                    Desktop.getDesktop().browse(e.getURL().toURI());
                  } catch (Exception ex) {
                  }
                }
              }
            }
          });
    }
  }

  class BlunderNodeTableModel extends AbstractTableModel {
    private String[] columnNames;
    private Vector<Vector<Object>> data;

    public BlunderNodeTableModel(
        List<Double> blunderWinrateThresholds,
        Map<Double, Color> blunderNodeColors,
        String[] columnNames) {
      this.columnNames = columnNames;
      data = new Vector<Vector<Object>>();
      if (blunderWinrateThresholds != null) {
        for (Double d : blunderWinrateThresholds) {
          Vector<Object> row = new Vector<Object>();
          row.add(d);
          row.add(blunderNodeColors.get(d));
          data.add(row);
        }
      }
    }

    public Vector<Vector<Object>> getData() {
      return data;
    }

    public JSONArray getThresholdArray() {
      JSONArray thresholds = new JSONArray("[]");
      data.forEach(d -> thresholds.put(toDouble(d.get(0))));
      return thresholds;
    }

    public JSONArray getColorArray() {
      JSONArray colors = new JSONArray("[]");
      data.forEach(d -> colors.put(Theme.color2Array((Color) d.get(1))));
      return colors;
    }

    public void addRow(Double threshold, Color color) {
      Vector<Object> row = new Vector<Object>();
      row.add(threshold);
      row.add(color);
      data.add(row);
      fireTableRowsInserted(0, data.size() - 1);
    }

    public void removeRow(int index) {
      if (index >= 0 && index < data.size()) {
        data.remove(index);
        if (data.size() > 0) fireTableRowsDeleted(0, data.size() - 1);
        tblBlunderNodes.updateUI();
      }
    }

    public int getColumnCount() {
      return columnNames.length;
    }

    public int getRowCount() {
      return data.size();
    }

    public String getColumnName(int col) {
      return columnNames[col];
    }

    public Object getValueAt(int row, int col) {
      return data.get(row).get(col);
    }

    public Class<?> getColumnClass(int c) {
      return c == 0 ? Double.class : Color.class;
    }

    public void setValueAt(Object value, int row, int col) {
      data.get(row).set(col, value);
      fireTableCellUpdated(row, col);
    }

    public boolean isCellEditable(int row, int col) {
      return true;
    }

    private double toDouble(Object x) {
      final double invalid = 0.0;
      try {
        return (Double) x;
      } catch (Exception e) {
        return invalid;
      }
    }

    public void sortData() {
      data.sort(
          new Comparator<Vector<Object>>() {
            public int compare(Vector<Object> a, Vector<Object> b) {
              return Double.compare(toDouble(a.get(0)), toDouble(b.get(0)));
            }
          });
    }
  }

  public boolean isWindows() {
    return osName != null && !osName.contains("darwin") && osName.contains("win");
  }

  //  private void setShowLcbWinrate() {
  //
  //    if (Lizzie.config.showLcbWinrate) {
  //      rdoLcb.setSelected(true);
  //    } else {
  //      rdoWinrate.setSelected(true);
  //    }
  //  }
  //
  //  private boolean getShowLcbWinrate() {
  //
  //    if (rdoLcb.isSelected()) {
  //      Lizzie.config.showLcbWinrate = true;
  //      return true;
  //    }
  //    if (rdoWinrate.isSelected()) {
  //      Lizzie.config.showLcbWinrate = false;
  //      return false;
  //    }
  //    return true;
  //  }

  private void setBoardSize() {
    int size = Lizzie.board.boardWidth;
    int width = Lizzie.board.boardWidth;
    int height = Lizzie.board.boardHeight;
    size = width == height ? width : 0;
    txtBoardWidth.setEnabled(false);
    txtBoardHeight.setEnabled(false);
    switch (size) {
      case 19:
        rdoBoardSize19.setSelected(true);
        break;
      case 13:
        rdoBoardSize13.setSelected(true);
        break;
      case 9:
        rdoBoardSize9.setSelected(true);
        break;
      case 7:
        rdoBoardSize7.setSelected(true);
        break;
      case 5:
        rdoBoardSize5.setSelected(true);
        break;
      case 4:
        rdoBoardSize4.setSelected(true);
        break;
      default:
        txtBoardWidth.setText(String.valueOf(width));
        txtBoardHeight.setText(String.valueOf(height));
        rdoBoardSizeOther.setSelected(true);
        txtBoardWidth.setEnabled(true);
        txtBoardHeight.setEnabled(true);
        break;
    }
  }

  private int[] getBoardSize() {
    if (rdoBoardSize19.isSelected()) {
      return new int[] {19, 19};
    } else if (rdoBoardSize13.isSelected()) {
      return new int[] {13, 13};
    } else if (rdoBoardSize9.isSelected()) {
      return new int[] {9, 9};
    } else if (rdoBoardSize7.isSelected()) {
      return new int[] {7, 7};
    } else if (rdoBoardSize5.isSelected()) {
      return new int[] {5, 5};
    } else if (rdoBoardSize4.isSelected()) {
      return new int[] {4, 4};
    } else {
      int width = Integer.parseInt(txtBoardWidth.getText().trim());
      if (width < 2) {
        width = 19;
      }
      int height = Integer.parseInt(txtBoardHeight.getText().trim());
      if (height < 2) {
        height = 19;
      }
      return new int[] {width, height};
    }
  }

  private void setShowMoveNumber() {
    txtShowMoveNumber.setEnabled(false);
    if (Lizzie.config.allowMoveNumber > 0) {
      if (Lizzie.config.onlyLastMoveNumber >= 0) {
        rdoShowMoveNumberLast.setSelected(true);
        txtShowMoveNumber.setText(
            String.valueOf(
                Lizzie.config.onlyLastMoveNumber <= 0 ? 1 : Lizzie.config.onlyLastMoveNumber));
        txtShowMoveNumber.setEnabled(true);
      }
    } else {
      if (Lizzie.config.allowMoveNumber == -1) {
        rdoShowMoveNumberAll.setSelected(true);
      } else rdoShowMoveNumberNo.setSelected(true);
    }
  }

  private void setShowWinrateSide() {
    if (Lizzie.frame.winrateGraph.mode == 0) {
      rdoShowWinrateBlack.setSelected(true);
    } else {
      rdoShowWinrateBoth.setSelected(true);
    }
  }

  private void setStoneIndicatorType(int type) {
    switch (type) {
      case 0:
        rdoStoneIndicatorDelta.setSelected(true);
        break;
      case 1:
        rdoStoneIndicatorCircle.setSelected(true);
        break;
      case 2:
        rdoStoneIndicatorSolid.setSelected(true);
        break;
      case 3:
        rdoStoneIndicatorNo.setSelected(true);
        break;
    }
  }

  private int getStoneIndicatorType() {
    if (rdoStoneIndicatorDelta.isSelected()) {
      return 0;
    } else if (rdoStoneIndicatorCircle.isSelected()) {
      return 1;
    } else if (rdoStoneIndicatorSolid.isSelected()) {
      return 2;
    } else return 3;
  }

  private void suggestionMoveInfoChanged() {
    Lizzie.config.showWinrateInSuggestion = chkShowWinrateInSuggestion.isSelected();
    Lizzie.config.showPlayoutsInSuggestion = chkShowPlayoutsInSuggestion.isSelected();
    Lizzie.config.showScoremeanInSuggestion = chkShowScoremeanInSuggestion.isSelected();
  }

  private void setFontValue(JComboBox<String> cmb, String fontName) {
    cmb.setSelectedIndex(-1);
    cmb.setSelectedItem(fontName);
  }

  private void readThemeValues() {
    if (cmbThemes.getSelectedIndex() <= 0) {
      // Default
      readDefaultTheme();
    } else {
      // Read the Theme
      String themeName = (String) cmbThemes.getSelectedItem();
      if (themeName == null || themeName.isEmpty()) {
        readDefaultTheme();
      } else {
        theme = new Theme(themeName);
        chkPureStone.setSelected(theme.usePureStone(false));
        chkPureBackground.setSelected(theme.usePureBackground(false));
        lblPureBackgroundColor.setColor(theme.pureBackgroundColor());
        chkShowStoneShaow.setSelected(theme.showStoneShadow(false));
        spnShadowSize.setEnabled(chkShowStoneShaow.isSelected());
        chkPureBoard.setSelected(theme.usePureBoard(false));
        lblPureBoardColor.setColor(theme.pureBoardColor());
        spnWinrateStrokeWidth.setValue(theme.winrateStrokeWidth());
        spnMinimumBlunderBarWidth.setValue(theme.minimumBlunderBarWidth());
        spnShadowSize.setValue(theme.shadowSize());
        setFontValue(cmbFontName, theme.fontName());
        setFontValue(cmbUiFontName, theme.uiFontName());
        setFontValue(cmbWinrateFontName, theme.winrateFontName());
        btnBackgroundPath.setEnabled(!chkPureBackground.isSelected());
        txtBackgroundPath.setEnabled(!chkPureBackground.isSelected());
        txtBackgroundFilter.setEnabled(!chkPureBackground.isSelected());
        txtBackgroundPath.setText(theme.backgroundPath());
        btnBoardPath.setEnabled(!chkPureBoard.isSelected());
        txtBoardPath.setEnabled(!chkPureBoard.isSelected());
        txtBoardPath.setText(theme.boardPath());
        txtBlackStonePath.setText(theme.blackStonePath());
        btnBlackStonePath.setEnabled(!chkPureStone.isSelected());
        btnWhiteStonePath.setEnabled(!chkPureStone.isSelected());
        txtBlackStonePath.setEnabled(!chkPureStone.isSelected());
        txtWhiteStonePath.setEnabled(!chkPureStone.isSelected());
        txtWhiteStonePath.setText(theme.whiteStonePath());
        lblWinrateLineColor.setColor(theme.winrateLineColor());
        lblWinrateMissLineColor.setColor(theme.winrateMissLineColor());
        lblBlunderBarColor.setColor(theme.blunderBarColor());
        lblScoreMeanLineColor.setColor(theme.scoreMeanLineColor());
        setStoneIndicatorType(theme.stoneIndicatorType());
        chkShowCommentNodeColor.setSelected(theme.showCommentNodeColor(false));
        lblCommentNodeColor.setColor(theme.commentNodeColor());
        lblCommentBackgroundColor.setColor(theme.commentBackgroundColor());
        lblCommentFontColor.setColor(theme.commentFontColor());
        Color BestCl = theme.bestMoveColor();
        lblBestMoveColor.setColor(
            new Color(BestCl.getRed(), BestCl.getGreen(), BestCl.getBlue(), 240));
        txtCommentFontSize.setText(String.valueOf(theme.commentFontSize()));
        txtBackgroundFilter.setText(String.valueOf(theme.backgroundFilter()));
        tblBlunderNodes.setModel(
            new BlunderNodeTableModel(
                theme.blunderWinrateThresholds().orElse(null),
                theme.blunderNodeColors().orElse(null),
                columsBlunderNodes));
        TableColumn colorCol = tblBlunderNodes.getColumnModel().getColumn(1);
        colorCol.setCellRenderer(new ColorRenderer(false));
        colorCol.setCellEditor(new ColorEditor(this));
      }
    }
    if (this.pnlBoardPreview != null) {
      tabbedPane.repaint();
    }
  }

  private void writeThemeValues() {
    if (cmbThemes.getSelectedIndex() <= 0) {
      // Default
      writeDefaultTheme();
    } else {
      // Write the Theme
      String themeName = (String) cmbThemes.getSelectedItem();
      if (themeName == null || themeName.isEmpty()) {
        writeDefaultTheme();
      } else {
        if (theme == null) {
          theme = new Theme(themeName);
        }
        theme.config.put("use-pure-stone", chkPureStone.isSelected());
        theme.config.put("use-pure-board", chkPureBoard.isSelected());
        theme.config.put("pure-board-color", Theme.color2Array(lblPureBoardColor.getColor()));
        theme.config.put("use-pure-background", chkPureBackground.isSelected());
        theme.config.put(
            "pure-background-color", Theme.color2Array(lblPureBackgroundColor.getColor()));
        theme.config.put("show-stone-shadow", chkShowStoneShaow.isSelected());
        theme.config.put("winrate-stroke-width", spnWinrateStrokeWidth.getValue());
        theme.config.put("minimum-blunder-bar-width", spnMinimumBlunderBarWidth.getValue());
        theme.config.put("shadow-size", spnShadowSize.getValue());
        theme.config.put("font-name", cmbFontName.getSelectedItem());
        theme.config.put("ui-font-name", cmbUiFontName.getSelectedItem());
        theme.config.put("winrate-font-name", cmbWinrateFontName.getSelectedItem());

        if (theme.fontName().equals("Lizzie默认") || theme.fontName().equals("Lizzie Default")) {
          Lizzie.config.fontName = "SansSerif";
        } else if (theme.fontName() != null) {
          Lizzie.config.fontName = theme.fontName();
        }

        if (theme.uiFontName().equals("Lizzie默认") || theme.uiFontName().equals("Lizzie Default")) {
          Lizzie.frame.uiFont = new Font("Microsoft YaHei", Font.TRUETYPE_FONT, 12);
        } else if (theme.uiFontName() != null) {
          Lizzie.frame.uiFont = new Font(theme.uiFontName(), Font.PLAIN, 12);
        }

        if (theme.winrateFontName().equals("Lizzie默认")
            || theme.winrateFontName().equals("Lizzie Default")) {
          try {
            Lizzie.frame.winrateFont =
                Font.createFont(
                    Font.TRUETYPE_FONT,
                    Thread.currentThread()
                        .getContextClassLoader()
                        .getResourceAsStream("fonts/OpenSans-Semibold.ttf"));
          } catch (IOException | FontFormatException e) {
            e.printStackTrace();
          }
        } else if (theme.winrateFontName() != null) {
          Lizzie.frame.winrateFont = new Font(theme.winrateFontName(), Font.PLAIN, 12);
        }

        theme.config.put("background-image", txtBackgroundPath.getText().trim());
        theme.config.put("board-image", txtBoardPath.getText().trim());
        theme.config.put("black-stone-image", txtBlackStonePath.getText().trim());
        theme.config.put("white-stone-image", txtWhiteStonePath.getText().trim());
        theme.config.put("winrate-line-color", Theme.color2Array(lblWinrateLineColor.getColor()));
        theme.config.put(
            "winrate-miss-line-color", Theme.color2Array(lblWinrateMissLineColor.getColor()));
        theme.config.put("blunder-bar-color", Theme.color2Array(lblBlunderBarColor.getColor()));
        theme.config.put(
            "scoremean-line-color", Theme.color2Array(lblScoreMeanLineColor.getColor()));
        Lizzie.config.stoneIndicatorType = getStoneIndicatorType();
        theme.config.put("stone-indicator-type", Lizzie.config.stoneIndicatorType);
        theme.config.put("show-comment-node-color", chkShowCommentNodeColor.isSelected());
        theme.config.put("comment-node-color", Theme.color2Array(lblCommentNodeColor.getColor()));
        theme.config.put(
            "comment-background-color", Theme.color2Array(lblCommentBackgroundColor.getColor()));
        theme.config.put("comment-font-color", Theme.color2Array(lblCommentFontColor.getColor()));
        theme.config.put("best-move-color", Theme.color2Array(lblBestMoveColor.getColor()));
        theme.config.put("comment-font-size", txtFieldIntValue(txtCommentFontSize));
        if (txtFieldIntValue(txtBackgroundFilter) != Lizzie.frame.filter20.getRadius()) {
          Lizzie.frame.testFilter(txtFieldIntValue(txtBackgroundFilter));
        }
        theme.config.put("background-filter", txtFieldIntValue(txtBackgroundFilter));
        theme.config.put(
            "blunder-winrate-thresholds",
            ((BlunderNodeTableModel) tblBlunderNodes.getModel()).getThresholdArray());
        theme.config.put(
            "blunder-node-colors",
            ((BlunderNodeTableModel) tblBlunderNodes.getModel()).getColorArray());
        theme.save();
      }
    }
  }

  private void readDefaultTheme() {
    chkPureStone.setSelected(Lizzie.config.uiConfig.optBoolean("use-pure-stone", false));
    chkPureBoard.setSelected(Lizzie.config.uiConfig.optBoolean("use-pure-board", false));
    lblPureBoardColor.setColor(
        Theme.array2Color(
            Lizzie.config.uiConfig.optJSONArray("pure-board-color"), new Color(217, 152, 77)));
    chkShowStoneShaow.setSelected(Lizzie.config.uiConfig.optBoolean("show-stone-shadow", true));
    spnShadowSize.setEnabled(chkShowStoneShaow.isSelected());
    chkPureBackground.setSelected(Lizzie.config.uiConfig.optBoolean("use-pure-background", false));
    lblPureBackgroundColor.setColor(
        Theme.array2Color(
            Lizzie.config.uiConfig.optJSONArray("pure-background-color"), Color.GRAY));
    txtBackgroundFilter.setEnabled(!chkPureBackground.isSelected());
    spnWinrateStrokeWidth.setValue(Lizzie.config.uiConfig.optFloat("winrate-stroke-width", 1.7f));
    spnMinimumBlunderBarWidth.setValue(
        Lizzie.config.uiConfig.optInt("minimum-blunder-bar-width", 3));
    spnShadowSize.setValue(Lizzie.config.uiConfig.optInt("shadow-size", 100));
    setFontValue(cmbFontName, Lizzie.config.uiConfig.optString("font-name", null));
    setFontValue(cmbUiFontName, Lizzie.config.uiConfig.optString("ui-font-name", null));
    setFontValue(cmbWinrateFontName, Lizzie.config.uiConfig.optString("winrate-font-name", null));
    txtBackgroundPath.setEnabled(false);
    btnBackgroundPath.setEnabled(false);
    txtBackgroundPath.setText("/assets/background.jpg");
    txtBoardPath.setEnabled(false);
    btnBoardPath.setEnabled(false);
    txtBoardPath.setText("/assets/board.png");
    txtBlackStonePath.setEnabled(false);
    btnBlackStonePath.setEnabled(false);
    txtBlackStonePath.setText("/assets/black0.png");
    txtWhiteStonePath.setEnabled(false);
    btnWhiteStonePath.setEnabled(false);
    txtWhiteStonePath.setText("/assets/white0.png");
    lblWinrateLineColor.setColor(
        Theme.array2Color(Lizzie.config.uiConfig.optJSONArray("winrate-line-color"), Color.green));
    lblWinrateMissLineColor.setColor(
        Theme.array2Color(
            Lizzie.config.uiConfig.optJSONArray("winrate-miss-line-color"), Color.blue.darker()));
    lblBlunderBarColor.setColor(
        Theme.array2Color(
            Lizzie.config.uiConfig.optJSONArray("blunder-bar-color"), new Color(255, 204, 255)));
    lblScoreMeanLineColor.setColor(
        Theme.array2Color(
            Lizzie.config.uiConfig.optJSONArray("scoremean-line-color"), new Color(255, 0, 255)));
    setStoneIndicatorType(Lizzie.config.uiConfig.optInt("stone-indicator-type", 0));
    chkShowCommentNodeColor.setSelected(
        Lizzie.config.uiConfig.optBoolean("show-comment-node-color", true));
    lblCommentNodeColor.setColor(
        Theme.array2Color(
            Lizzie.config.uiConfig.optJSONArray("comment-node-color"), Color.BLUE.brighter()));
    lblCommentBackgroundColor.setColor(
        Theme.array2Color(
            Lizzie.config.uiConfig.optJSONArray("comment-background-color"),
            new Color(0, 0, 0, 200)));
    lblCommentFontColor.setColor(
        Theme.array2Color(Lizzie.config.uiConfig.optJSONArray("comment-font-color"), Color.WHITE));

    Color BestCl =
        Theme.array2Color(Lizzie.config.uiConfig.optJSONArray("best-move-color"), Color.CYAN);
    lblBestMoveColor.setColor(new Color(BestCl.getRed(), BestCl.getGreen(), BestCl.getBlue(), 240));
    txtCommentFontSize.setText(
        String.valueOf(Lizzie.config.uiConfig.optInt("comment-font-size", 0)));
    txtBackgroundFilter.setText(
        String.valueOf(Lizzie.config.uiConfig.optInt("background-filter", 20)));
    Theme defTheme = new Theme("");
    tblBlunderNodes.setModel(
        new BlunderNodeTableModel(
            defTheme.blunderWinrateThresholds().orElse(null),
            defTheme.blunderNodeColors().orElse(null),
            columsBlunderNodes));
    TableColumn colorCol = tblBlunderNodes.getColumnModel().getColumn(1);
    colorCol.setCellRenderer(new ColorRenderer(false));
    colorCol.setCellEditor(new ColorEditor(this));
  }

  private void writeDefaultTheme() {
    Lizzie.config.uiConfig.put("use-pure-stone", chkPureStone.isSelected());
    Lizzie.config.uiConfig.put("use-pure-board", chkPureBoard.isSelected());
    Lizzie.config.uiConfig.put("pure-board-color", Theme.color2Array(lblPureBoardColor.getColor()));
    Lizzie.config.uiConfig.put("use-pure-background", chkPureBackground.isSelected());
    Lizzie.config.uiConfig.put(
        "pure-background-color", Theme.color2Array(lblPureBackgroundColor.getColor()));
    Lizzie.config.uiConfig.put("winrate-stroke-width", spnWinrateStrokeWidth.getValue());
    Lizzie.config.uiConfig.put("minimum-blunder-bar-width", spnMinimumBlunderBarWidth.getValue());
    Lizzie.config.uiConfig.put("shadow-size", spnShadowSize.getValue());
    Lizzie.config.uiConfig.put("show-stone-shadow", chkShowStoneShaow.isSelected());
    Lizzie.config.uiConfig.put("font-name", cmbFontName.getSelectedItem());
    Lizzie.config.uiConfig.put("ui-font-name", cmbUiFontName.getSelectedItem());
    Lizzie.config.uiConfig.put("winrate-font-name", cmbWinrateFontName.getSelectedItem());
    Lizzie.config.uiConfig.put(
        "winrate-line-color", Theme.color2Array(lblWinrateLineColor.getColor()));
    Lizzie.config.uiConfig.put(
        "winrate-miss-line-color", Theme.color2Array(lblWinrateMissLineColor.getColor()));
    Lizzie.config.uiConfig.put(
        "blunder-bar-color", Theme.color2Array(lblBlunderBarColor.getColor()));
    Lizzie.config.uiConfig.put(
        "scoremean-line-color", Theme.color2Array(lblScoreMeanLineColor.getColor()));
    Lizzie.config.stoneIndicatorType = getStoneIndicatorType();
    Lizzie.config.uiConfig.put("stone-indicator-type", Lizzie.config.stoneIndicatorType);
    Lizzie.config.uiConfig.put("show-comment-node-color", chkShowCommentNodeColor.isSelected());
    Lizzie.config.uiConfig.put(
        "comment-node-color", Theme.color2Array(lblCommentNodeColor.getColor()));
    Lizzie.config.uiConfig.put(
        "comment-background-color", Theme.color2Array(lblCommentBackgroundColor.getColor()));
    Lizzie.config.uiConfig.put(
        "comment-font-color", Theme.color2Array(lblCommentFontColor.getColor()));
    Lizzie.config.uiConfig.put("best-move-color", Theme.color2Array(lblBestMoveColor.getColor()));
    Lizzie.config.uiConfig.put("comment-font-size", txtFieldIntValue(txtCommentFontSize));
    Lizzie.config.uiConfig.put("background-filter", txtFieldIntValue(txtBackgroundFilter));
    if (txtFieldIntValue(txtBackgroundFilter) != Lizzie.frame.filter20.getRadius()) {
      Lizzie.frame.testFilter(txtFieldIntValue(txtBackgroundFilter));
    }
    Lizzie.config.uiConfig.put(
        "blunder-winrate-thresholds",
        ((BlunderNodeTableModel) tblBlunderNodes.getModel()).getThresholdArray());
    Lizzie.config.uiConfig.put(
        "blunder-node-colors",
        ((BlunderNodeTableModel) tblBlunderNodes.getModel()).getColorArray());

    if (!Lizzie.config.uiConfig.optString("font-name").isEmpty()) {
      if (Lizzie.config.uiConfig.getString("font-name").equals("Lizzie默认")
          || Lizzie.config.uiConfig.getString("font-name").equals("Lizzie Default"))
        Lizzie.config.fontName = "SansSerif";
      else Lizzie.config.fontName = Lizzie.config.uiConfig.optString("font-name");
    }

    if (!Lizzie.config.uiConfig.optString("ui-font-name").isEmpty()
        && (Lizzie.config.uiConfig.getString("ui-font-name").equals("Lizzie默认")
            || Lizzie.config.uiConfig.getString("ui-font-name").equals("Lizzie Default"))) {
      Lizzie.frame.uiFont = new Font("Microsoft YaHei", Font.TRUETYPE_FONT, 12);
    } else if (!Lizzie.config.uiConfig.optString("ui-font-name").isEmpty()) {
      Lizzie.frame.uiFont =
          new Font(Lizzie.config.uiConfig.optString("ui-font-name"), Font.PLAIN, 12);
    }

    if (!Lizzie.config.uiConfig.optString("winrate-font-name").isEmpty()
        && (Lizzie.config.uiConfig.getString("winrate-font-name").equals("Lizzie默认")
            || Lizzie.config.uiConfig.getString("winrate-font-name").equals("Lizzie Default"))) {
      try {
        Lizzie.frame.winrateFont =
            Font.createFont(
                Font.TRUETYPE_FONT,
                Thread.currentThread()
                    .getContextClassLoader()
                    .getResourceAsStream("fonts/OpenSans-Semibold.ttf"));
      } catch (IOException | FontFormatException e) {
        e.printStackTrace();
      }
    } else if (!Lizzie.config.uiConfig.optString("winrate-font-name").isEmpty()) {
      Lizzie.frame.winrateFont =
          new Font(Lizzie.config.uiConfig.optString("winrate-font-name"), Font.PLAIN, 12);
    }
  }

  private void finalizeEditedBlunderColors() {
    if (tblBlunderNodes == null) return;
    TableCellEditor editor = tblBlunderNodes.getCellEditor();
    BlunderNodeTableModel model = (BlunderNodeTableModel) tblBlunderNodes.getModel();
    if (editor != null) editor.stopCellEditing();
    if (model != null) model.sortData();
  }

  private void saveConfig() {
    Lizzie.config.limitTime = chkLimitTime.isSelected();
    Lizzie.config.limitPlayout = chkLimitPlayouts.isSelected();
    Lizzie.config.limitPlayouts =
        Utils.parseTextToLong(txtLimitPlayouts, Lizzie.config.limitPlayouts);
    Lizzie.config.uiConfig.put("limit-playout", Lizzie.config.limitPlayout);
    Lizzie.config.uiConfig.put("limit-playouts", Lizzie.config.limitPlayouts);
    Lizzie.config.uiConfig.put("limit-time", Lizzie.config.limitTime);
    boolean oriUseIinCoordsName = Lizzie.config.useIinCoordsName;
    Lizzie.config.useIinCoordsName = chkUseIinCoordsName.isSelected();
    Lizzie.config.uiConfig.put("use-i-in-coords-name", Lizzie.config.useIinCoordsName);
    if (Lizzie.config.useIinCoordsName != oriUseIinCoordsName) {
      Lizzie.frame.boardRenderer.reDrawGobanAnyway();
      if (Lizzie.frame.boardRenderer2 != null) Lizzie.frame.boardRenderer2.reDrawGobanAnyway();
    }
    boolean oriShowMouseOverWinrateGraph = Lizzie.config.showMouseOverWinrateGraph;
    Lizzie.config.showMouseOverWinrateGraph = chkShowMouseOverWinrateGraph.isSelected();
    Lizzie.config.uiConfig.put(
        "show-mouse-over-winrate-graph", Lizzie.config.showMouseOverWinrateGraph);
    if (oriShowMouseOverWinrateGraph && !Lizzie.config.showMouseOverWinrateGraph)
      Lizzie.frame.clearMouseOverWinrateGraph();
    Lizzie.config.showScoreLeadLine = chkShowScoreLeadLine.isSelected();
    Lizzie.config.uiConfig.put("show-score-lead-line", Lizzie.config.showScoreLeadLine);
    Lizzie.config.removeDeadChainInVariation = chkVariationRemoveDeadChain.isSelected();
    Lizzie.config.uiConfig.put(
        "remove-dead-in-variation", Lizzie.config.removeDeadChainInVariation);

    Lizzie.config.replayBranchIntervalSeconds =
        Utils.parseTextToDouble(
                txtVariationReplayInterval, Lizzie.config.replayBranchIntervalSeconds * 1000)
            / 1000;
    Lizzie.config.uiConfig.put(
        "replay-branch-interval-seconds", Lizzie.config.replayBranchIntervalSeconds);

    if (comboBoxPvVisits.getSelectedIndex() == 0) {
      Lizzie.config.showPvVisits = false;
      Lizzie.config.showPvVisitsLastMove = false;
      Lizzie.config.showPvVisitsAllMove = false;
    } else if (comboBoxPvVisits.getSelectedIndex() == 1) {
      Lizzie.config.showPvVisits = true;
      Lizzie.config.showPvVisitsLastMove = true;
      Lizzie.config.showPvVisitsAllMove = false;
    } else if (comboBoxPvVisits.getSelectedIndex() == 2) {
      Lizzie.config.showPvVisits = true;
      Lizzie.config.showPvVisitsLastMove = true;
      Lizzie.config.showPvVisitsAllMove = true;
    }
    Lizzie.config.pvVisitsLimit = txtFieldIntValue(txtPvVisitsLimit);
    Lizzie.config.uiConfig.put("pv-visits-limit", Lizzie.config.pvVisitsLimit);

    Lizzie.config.uiConfig.put("show-pv-visits", Lizzie.config.showPvVisits);
    Lizzie.config.uiConfig.put("show-pv-visits-last-move", Lizzie.config.showPvVisitsLastMove);
    Lizzie.config.uiConfig.put("show-pv-visits-all-move", Lizzie.config.showPvVisitsAllMove);

    Lizzie.config.autoCheckEngineAlive = chkCheckEngineAlive.isSelected();
    Lizzie.config.uiConfig.put("auto-check-engine-alive", Lizzie.config.autoCheckEngineAlive);
    Lizzie.engineManager.autoCheckEngineAlive(Lizzie.config.autoCheckEngineAlive);
    if (chkShowIndependentMainBoard.isSelected()) {
      if (!Lizzie.config.isShowingIndependentMain) Lizzie.frame.toggleIndependentMainBoard();
    } else {
      if (Lizzie.config.isShowingIndependentMain) Lizzie.frame.toggleIndependentMainBoard();
    }
    int n = chkShowIndependentSubBoard.getSelectedIndex();
    if (n == 0) {
      if (Lizzie.config.extraMode == 8) Lizzie.config.toggleExtraMode(0);
      if (Lizzie.config.isShowingIndependentMain) Lizzie.frame.toggleIndependentMainBoard();
    } else if (n == 1) {
      if (Lizzie.config.extraMode != 8) Lizzie.frame.toggleOnlyIndependMainBoard();
    } else {
      if (Lizzie.config.extraMode == 8) Lizzie.config.toggleExtraMode(0);
      if (!Lizzie.config.isShowingIndependentMain) Lizzie.frame.toggleIndependentMainBoard();
    }
    //    if (chkShowIndependentSubBoard.isSelected()) {
    //      if (!Lizzie.config.isShowingIndependentSub) Lizzie.frame.toggleIndependentSubBoard();
    //    } else {
    //      if (Lizzie.config.isShowingIndependentSub) Lizzie.frame.toggleIndependentSubBoard();
    //    }
    try {
      if (chkShowMoveList.isSelected()) {
        if (!Lizzie.config.showListPane()) Lizzie.config.toggleShowListPane();
      } else {
        if (Lizzie.config.showListPane()) Lizzie.config.toggleShowListPane();
      }

      if (chkShowIndependentHawkEye.isSelected()) {
        if (Lizzie.frame != null
            && Lizzie.frame.movelistframe != null
            && Lizzie.frame.movelistframe.isVisible()) {
        } else Lizzie.frame.toggleBadMoves();
      } else {
        if (Lizzie.frame != null
            && Lizzie.frame.movelistframe != null
            && Lizzie.frame.movelistframe.isVisible()) Lizzie.frame.toggleBadMoves();
      }

      if (chkShowIndependentMoveList.isSelected()) {
        if (Lizzie.frame != null && Lizzie.frame.analysisFrame == null
            || !Lizzie.frame.analysisFrame.isVisible()) {}
        Lizzie.frame.toggleBestMoves();
      } else {
        if (Lizzie.frame != null
            && Lizzie.frame.analysisFrame != null
            && Lizzie.frame.analysisFrame.isVisible()) Lizzie.frame.toggleBestMoves();
      }

      if (rdbtnKatago.isSelected()) {
        if (Lizzie.config.useZenEstimate) {
          Lizzie.config.useZenEstimate = false;
          Lizzie.frame.restartZen();
        }
      } else {
        if (!Lizzie.config.useZenEstimate) {
          Lizzie.config.useZenEstimate = true;
          Lizzie.frame.restartZen();
        }
      }
      Lizzie.config.uiConfig.put("use-zen-estimate", Lizzie.config.useZenEstimate);
      Lizzie.config.loadEstimateEngine = chkAutoLoadEstimate.isSelected();
      Lizzie.config.uiConfig.put("load-estimate-engine", Lizzie.config.loadEstimateEngine);
      Lizzie.config.loadSgfLast = chkSgfLoadLast.isSelected();
      Lizzie.config.uiConfig.put("load-sgf-last", Lizzie.config.loadSgfLast);
      Lizzie.config.showVarMove = chkShowVarMove.isSelected();
      Lizzie.config.uiConfig.put("show-var-move", Lizzie.config.showVarMove);
      Lizzie.config.newMoveNumberInBranch = rdoBranchMoveOne.isSelected();
      Lizzie.config.uiConfig.put("new-move-number-in-branch", Lizzie.config.newMoveNumberInBranch);
      Lizzie.config.showRightMenu = rdoRightClickMenu.isSelected();
      Lizzie.config.uiConfig.put("show-right-menu", Lizzie.config.showRightMenu);
      Lizzie.config.enableLizzieCache = chkLizzieCache.isSelected();
      Lizzie.config.leelazConfig.put("enable-lizzie-cache", Lizzie.config.enableLizzieCache);
      Lizzie.config.allowDoubleClick = chkEnableDoubCli.isSelected();
      Lizzie.config.uiConfig.put("allow-double-click", Lizzie.config.allowDoubleClick);
      Lizzie.config.allowDrag = chkEnableDragStone.isSelected();
      Lizzie.config.uiConfig.put("allow-drag", Lizzie.config.allowDrag);
      Lizzie.config.noRefreshOnSub = chkNoRefreshSub.isSelected();
      Lizzie.config.uiConfig.put("no-refresh-on-sub", Lizzie.config.noRefreshOnSub);

      Lizzie.config.noCapture = chkNoCapture.isSelected();
      Lizzie.config.uiConfig.put("no-capture", Lizzie.config.noCapture);
      Lizzie.config.alwaysGtp = chkAlwaysGtp.isSelected();
      Lizzie.config.uiConfig.put("always-gtp", Lizzie.config.alwaysGtp);
      Lizzie.config.showTitleWr = chkShowTitleWr.isSelected();
      Lizzie.config.uiConfig.put("show-title-wr", Lizzie.config.showTitleWr);
      if (Lizzie.config.advanceTimeSettings) {
        Lizzie.config.advanceTimeTxt = txtAdvanceTime.getText();
        Lizzie.config.uiConfig.put("advance-time-txt", txtAdvanceTime.getText());
      }

      Lizzie.config.playponder = rdoAIbackground.isSelected();
      leelazConfig.putOpt("play-ponder", Lizzie.config.playponder);
      Lizzie.config.fastChange = rdoFastSwitch.isSelected();
      leelazConfig.putOpt("fast-engine-change", Lizzie.config.fastChange);
      if (Lizzie.frame.shouldShowRect() && !rdoShowMoveRect.isSelected()) {
        if (Lizzie.frame.boardRenderer != null) Lizzie.frame.boardRenderer.removeblock();
        if (Lizzie.config.extraMode == 2) {
          if (Lizzie.frame.boardRenderer2 != null) Lizzie.frame.boardRenderer2.removeblock();
        }
      }
      Lizzie.config.showrect =
          rdoShowMoveRect.isSelected() ? 0 : (rdoShowMoveRectOnPlay.isSelected() ? 1 : 2);
      // System.out.println(Lizzie.config.showrect);
      Lizzie.config.uiConfig.putOpt("show-move-rect", Lizzie.config.showrect);

      Lizzie.config.maxAnalyzeTimeMillis = 1000 * txtFieldIntValue(txtMaxAnalyzeTime);
      if (Lizzie.config.maxAnalyzeTimeMillis <= 0) {
        Lizzie.config.maxAnalyzeTimeMillis = 9999 * 60 * 1000;
      }
      leelazConfig.putOpt("max-analyze-time-seconds", Lizzie.config.maxAnalyzeTimeMillis / 1000);
      leelazConfig.putOpt(
          "max-game-thinking-time-seconds",
          txtFieldIntValue(txtMaxGameThinkingTime) > 0
              ? txtFieldIntValue(txtMaxGameThinkingTime)
              : 2);
      Lizzie.config.analyzeUpdateIntervalCentisec = txtFieldIntValue(txtAnalyzeUpdateInterval);
      if (Lizzie.config.analyzeUpdateIntervalCentisec <= 0)
        Lizzie.config.analyzeUpdateIntervalCentisec = 10;
      Lizzie.config.analyzeUpdateIntervalCentisecSSH =
          txtFieldIntValue(txtAnalyzeUpdateIntervalSSH);
      if (Lizzie.config.analyzeUpdateIntervalCentisecSSH <= 0)
        Lizzie.config.analyzeUpdateIntervalCentisecSSH = 10;
      leelazConfig.putOpt(
          "analyze-update-interval-centisec", Lizzie.config.analyzeUpdateIntervalCentisec);
      leelazConfig.putOpt(
          "analyze-update-interval-centisecssh", Lizzie.config.analyzeUpdateIntervalCentisecSSH);

      int[] size = getBoardSize();
      if (size[0] == size[1]) {
        Lizzie.config.uiConfig.put("board-size", size[0]);
      }
      Lizzie.config.uiConfig.put("board-width", size[0]);
      Lizzie.config.uiConfig.put("board-height", size[1]);
      Lizzie.config.uiConfig.putOpt("show-name-in-board", chkShowName.isSelected());
      Lizzie.config.showNameInBoard = chkShowName.isSelected();

      Lizzie.config.uiConfig.putOpt("show-blue-ring", chkShowBlueRing.isSelected());
      Lizzie.config.showBlueRing = chkShowBlueRing.isSelected();

      Lizzie.config.leelazConfig.putOpt("show-nosugg-circle", chkShowNoSuggCircle.isSelected());
      Lizzie.config.showNoSuggCircle = chkShowNoSuggCircle.isSelected();

      Lizzie.frame.setAlwaysOnTop(chkAlwaysOnTop.isSelected());
      Lizzie.config.uiConfig.put("mains-always-ontop", chkAlwaysOnTop.isSelected());

      Lizzie.config.showQuickLinks = chkShowQuickLinks.isSelected();
      Lizzie.config.uiConfig.put("show-quick-links", chkShowQuickLinks.isSelected());
      Lizzie.config.winrateAlwaysBlack = chkAlwaysShowBlackWinrate.isSelected();
      Lizzie.config.uiConfig.put("win-rate-always-black", Lizzie.config.winrateAlwaysBlack);
      Lizzie.config.minPlayoutRatioForStats =
          Utils.txtFieldDoubleValue(txtMinPlayoutRatioForStats) / 100;
      Lizzie.config.uiConfig.put(
          "min-playout-ratio-for-stats", Lizzie.config.minPlayoutRatioForStats);
      Lizzie.config.showCaptured = chkShowCaptured.isSelected();
      Lizzie.config.showWinrateGraph = chkShowWinrate.isSelected();
      Lizzie.config.showVariationGraph = chkShowVariationGraph.isSelected();
      Lizzie.config.showComment = chkShowComment.isSelected();
      Lizzie.config.showSubBoard = chkShowSubBoard.isSelected();
      Lizzie.config.showStatus = chkShowStatus.isSelected();
      Lizzie.config.showCoordinates = chkShowCoordinates.isSelected();
      if (Lizzie.config.extraMode == 7
          && (Lizzie.config.showListPane()
              || Lizzie.config.showCaptured
              || Lizzie.config.showWinrateGraph
              || Lizzie.config.showVariationGraph
              || Lizzie.config.showComment
              || Lizzie.config.showSubBoard)) {
        Lizzie.config.extraMode = 0;
        Lizzie.config.uiConfig.put("extra-mode", 0);
      }
      Lizzie.config.uiConfig.putOpt("show-captured", Lizzie.config.showCaptured);
      Lizzie.config.uiConfig.putOpt("show-winrate-graph", Lizzie.config.showWinrateGraph);
      Lizzie.config.uiConfig.putOpt("show-variation-graph", Lizzie.config.showVariationGraph);
      Lizzie.config.uiConfig.putOpt("show-comment", Lizzie.config.showComment);
      Lizzie.config.uiConfig.putOpt("show-subboard", Lizzie.config.showSubBoard);
      Lizzie.config.uiConfig.putOpt("show-coordinates", Lizzie.config.showCoordinates);
      Lizzie.config.showMoveNumber = !rdoShowMoveNumberNo.isSelected();
      Lizzie.config.onlyLastMoveNumber =
          rdoShowMoveNumberLast.isSelected() ? txtFieldIntValue(txtShowMoveNumber) : 0;
      Lizzie.config.allowMoveNumber =
          Lizzie.config.showMoveNumber
              ? (Lizzie.config.onlyLastMoveNumber > 0 ? Lizzie.config.onlyLastMoveNumber : -1)
              : 0;
      Lizzie.config.uiConfig.put("show-move-number", Lizzie.config.showMoveNumber);
      Lizzie.config.uiConfig.put("only-last-move-number", Lizzie.config.onlyLastMoveNumber);
      Lizzie.config.uiConfig.put("allow-move-number", Lizzie.config.onlyLastMoveNumber);

      if (this.rdoShowWinrateBlack.isSelected()) Lizzie.frame.winrateGraph.mode = 0;
      if (this.rdoShowWinrateBoth.isSelected()) Lizzie.frame.winrateGraph.mode = 1;
      Lizzie.config.showBlunderBar = chkShowBlunderBar.isSelected();
      Lizzie.config.uiConfig.putOpt("show-blunder-bar", Lizzie.config.showBlunderBar);

      switch (chkShowWhiteSuggWhite.getSelectedIndex()) {
        case 0:
          Lizzie.config.whiteSuggestionOrderWhite = false;
          Lizzie.config.whiteSuggestionWhite = false;
          break;
        case 1:
          Lizzie.config.whiteSuggestionOrderWhite = false;
          Lizzie.config.whiteSuggestionWhite = true;
          break;
        case 2:
          Lizzie.config.whiteSuggestionOrderWhite = true;
          Lizzie.config.whiteSuggestionWhite = false;
          break;
        case 3:
          Lizzie.config.whiteSuggestionOrderWhite = true;
          Lizzie.config.whiteSuggestionWhite = true;
          break;
      }
      Lizzie.config.uiConfig.putOpt("white-suggestion-white", Lizzie.config.whiteSuggestionWhite);
      Lizzie.config.uiConfig.putOpt(
          "white-suggestion-order-white", Lizzie.config.whiteSuggestionOrderWhite);
      //  Lizzie.config.whiteSuggestionWhite = chkShowWhiteSuggWhite.isSelected();
      Lizzie.config.uiConfig.putOpt("white-suggestion-white", Lizzie.config.whiteSuggestionWhite);

      if (chkShowMoveAllInBranch.isSelected()) {
        Lizzie.config.showMoveNumberFromOne = false;
        Lizzie.config.uiConfig.putOpt("movenumber-from-one", false);
      }
      Lizzie.config.showMoveAllInBranch = chkShowMoveAllInBranch.isSelected();
      Lizzie.config.uiConfig.putOpt("show-moveall-inbranch", Lizzie.config.showMoveAllInBranch);
      //   Lizzie.config.dynamicWinrateGraphWidth = chkDynamicWinrateGraphWidth.isSelected();
      //   Lizzie.config.uiConfig.putOpt(
      //      "dynamic-winrate-graph-width", Lizzie.config.dynamicWinrateGraphWidth);
      Lizzie.config.appendWinrateToComment = chkAppendWinrateToComment.isSelected();
      Lizzie.config.uiConfig.putOpt(
          "append-winrate-to-comment", Lizzie.config.appendWinrateToComment);
      Lizzie.config.showSuggestionOrder = chkShowSuggLabel.isSelected();
      Lizzie.config.uiConfig.putOpt("show-suggestion-order", Lizzie.config.showSuggestionOrder);
      Lizzie.config.showSuggestionMaxRed = chkMaxValueReverseColor.isSelected();
      Lizzie.config.uiConfig.putOpt("show-suggestion-maxred", Lizzie.config.showSuggestionMaxRed);

      Lizzie.config.showSuggestionVariations = chkShowVairationsOnMouse.isSelected();
      Lizzie.config.uiConfig.putOpt(
          "show-suggestion-variations", Lizzie.config.showSuggestionVariations);

      Lizzie.config.noRefreshOnMouseMove = chkShowVairationsOnMouseNoRefresh.isSelected();
      Lizzie.config.uiConfig.putOpt("norefresh-onmouse-move", Lizzie.config.noRefreshOnMouseMove);
      //   Lizzie.config.holdBestMovesToSgf = chkHoldBestMovesToSgf.isSelected();
      //    Lizzie.config.uiConfig.putOpt("hold-bestmoves-to-sgf",
      // Lizzie.config.holdBestMovesToSgf);
      //   Lizzie.config.showBestMovesByHold = chkShowBestMovesByHold.isSelected();
      //     Lizzie.config.uiConfig.putOpt("show-bestmoves-by-hold",
      // Lizzie.config.showBestMovesByHold);
      //    Lizzie.config.colorByWinrateInsteadOfVisits =
      // chkColorByWinrateInsteadOfVisits.isSelected();
      Lizzie.config.uiConfig.putOpt(
          "color-by-winrate-instead-of-visits", Lizzie.config.colorByWinrateInsteadOfVisits);
      Lizzie.config.boardPositionProportion = sldBoardPositionProportion.getValue();
      Lizzie.config.uiConfig.putOpt(
          "board-position-proportion", Lizzie.config.boardPositionProportion);
      Lizzie.config.limitMaxSuggestion = txtFieldIntValue(txtLimitBestMoveNum);
      Lizzie.config.leelazConfig.put("limit-max-suggestion", Lizzie.config.limitMaxSuggestion);
      Lizzie.config.limitBranchLength = txtFieldIntValue(txtLimitBranchLength);
      Lizzie.config.leelazConfig.put("limit-branch-length", Lizzie.config.limitBranchLength);
      suggestionMoveInfoChanged();
      Lizzie.config.uiConfig.putOpt(
          "show-winrate-in-suggestion", Lizzie.config.showWinrateInSuggestion);
      Lizzie.config.uiConfig.putOpt(
          "show-playouts-in-suggestion", Lizzie.config.showPlayoutsInSuggestion);
      Lizzie.config.uiConfig.putOpt(
          "show-scoremean-in-suggestion", Lizzie.config.showScoremeanInSuggestion);
      // Lizzie.config.uiConfig.put("gtp-console-style", tpGtpConsoleStyle.getText());
      Lizzie.config.uiConfig.put("theme", cmbThemes.getSelectedItem());
      writeThemeValues();
      Lizzie.config.readThemeVaule(false);
      Lizzie.config.save();
    } catch (IOException e) {
      e.printStackTrace();
    }
    Lizzie.frame.menu.updateFastLinks();
  }

  public void switchTab(int index) {
    tabbedPane.setSelectedIndex(index);
  }
}
