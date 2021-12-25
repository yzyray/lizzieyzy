package featurecat.lizzie;

import featurecat.lizzie.analysis.EngineManager;
import featurecat.lizzie.gui.LizzieFrame;
import featurecat.lizzie.theme.Theme;
import featurecat.lizzie.util.Utils;
import java.awt.Color;
import java.awt.Frame;
import java.awt.Toolkit;
import java.io.*;
import java.util.*;
import javax.swing.*;
import org.jdesktop.swingx.util.OS;
import org.json.*;

public class Config {
  public String language = "en";
  // public boolean showBorder = false;
  public boolean showMoveNumber = false;
  public int onlyLastMoveNumber = 10;
  // 0: Do not show; -1: Show all move number; other: Show last move number
  public int allowMoveNumber = -1;
  public boolean showMoveNumberFromOne = false;
  public boolean newMoveNumberInBranch = true;
  public boolean showWinrateGraph = true;
  public boolean largeWinrateGraph = false;
  public boolean showBlunderBar = true;
  public boolean showMoveAllInBranch = false;
  // public boolean weightedBlunderBarHeight = false;
  // public boolean dynamicWinrateGraphWidth = true;
  public boolean showVariationGraph = true;
  public boolean showComment = true;
  public boolean showRawBoard = false;
  public boolean showBestMovesTemporarily = false;
  public boolean showCaptured = true;
  // public boolean handicapInsteadOfWinrate = false;
  //  public boolean showDynamicKomi = false;
  public double replayBranchIntervalSeconds = 0.9;
  public boolean showCoordinates = true;
  public boolean colorByWinrateInsteadOfVisits = false;
  // public boolean showlcbwinrate = false;
  public boolean playponder = true;
  public int showrect = 1;
  public boolean showlcbcolor = false;
  public boolean fastChange = true;
  public boolean showKataGoScoreLeadWithKomi = false;
  //  public boolean kataGoScoreMeanAlwaysBlack = false;
  // public boolean scoreMeanWinrateGraphBoard = false;
  public boolean showKataGoEstimate = false;
  // public boolean allowDrageDoubleClick = true;
  public boolean showKataGoEstimateOnSubbord = false;
  public boolean showKataGoEstimateOnMainbord = true;
  public boolean showSuggestionOrder = true;
  public boolean showSuggestionMaxRed = true;
  public boolean showStatus = true;
  public boolean isClassicMode = false;
  // public boolean changedStatus = false;
  public boolean showBranch = true;
  public boolean showBestMoves = true;
  public boolean showNextMoves = true;
  public boolean showSubBoard = true;
  public boolean hideSubBoardFromLargeWinrate = false;
  public boolean largeSubBoard = false;
  public boolean startMaximized = true;
  public boolean loadEstimateEngine = false;
  public String estimateCommand =
      "katago"
          + File.separator
          + "katago.exe gtp -model weights"
          + File.separator
          + "kata20bs530.bin.gz -config katago"
          + File.separator
          + "estimate.cfg";
  public Double estimateThreshold = 0.4;

  public boolean showSuggestionVariations = true;
  public boolean showWinrateInSuggestion = true;
  public boolean showPlayoutsInSuggestion = true;
  public boolean showScoremeanInSuggestion = true;

  public boolean showNameInBoard = true;
  public boolean openHtmlOnLive = true;
  public boolean readKomi = true;
  public boolean alwaysGotoLastOnLive = false;
  // public String readBoardArg1 = "0";
  //  public int readBoardArg2 = 200;
  //  public boolean readBoardArg3 = true;
  public boolean alwaysSyncBoardStat = true;
  public boolean playSound = true;
  public boolean notPlaySoundInSync = true;
  public boolean noRefreshOnMouseMove = true;
  // public boolean syncBoth = false;
  public boolean showBlueRing = true;
  public boolean isShowingWinrateGraph = true;
  public boolean isShowingMoveList = true;
  public int moveListSelectedBranch = 0;
  // public double matchAiTemperature = 1;
  public boolean showMoveListGraph = true;
  public boolean whiteSuggestionWhite = false;
  public boolean whiteSuggestionOrderWhite = false;
  public boolean advanceTimeSettings = false;
  public String advanceTimeTxt = "time_settings 120 2 1";

  public boolean kataTimeSettings = false;
  public int kataTimeType = 0; // 0=读秒,1=加秒.2=包干
  public int kataTimeMainTimeMins = 10;
  public int kataTimeByoyomiSecs = 5;
  public int kataTimeByoyomiTimes = 3;
  public int kataTimeFisherIncrementSecs = 5;

  public boolean pkAdvanceTimeSettings = false;
  public String advanceBlackTimeTxt = "time_settings 120 2 1";
  public String advanceWhiteTimeTxt = "time_settings 120 2 1";

  public ExtraMode extraMode = ExtraMode.Normal;

  public JSONObject config;
  public JSONObject leelazConfig;
  public JSONObject uiConfig;
  public JSONObject persisted;
  public JSONObject persistedUi;
  public JSONObject saveBoard;
  public JSONObject saveBoardConfig;

  private String configFilename = "config.txt";
  private String persistFilename = "persist";
  private String saveBoardFilename = "save" + File.separator + "save";

  public Theme theme;
  public float winrateStrokeWidth = 1.7f;
  public int leelaversion = 17;
  public int minimumBlunderBarWidth = 3;
  public int shadowSize = 75;
  public static String sysDefaultFontName = "Dialog.plain";
  public String fontName = null;
  public String uiFontName = null;
  public String winrateFontName = null;
  public int commentFontSize = 0;
  public Color commentFontColor = null;
  public Color commentBackgroundColor = null;
  public Color winrateLineColor = null;
  public Color winrateMissLineColor = null;
  public Color scoreMeanLineColor = null;
  public Color blunderBarColor = null;
  public float bestMoveColor;
  public Color bestMoveColorC;
  public boolean solidStoneIndicator = false;
  public int stoneIndicatorType = 1; // 0: non, 1: circle, 2: solid
  public boolean showCommentNodeColor = true;
  public boolean badmovesalwaysontop = false;
  public boolean mainsalwaysontop = false;
  public boolean suggestionsalwaysontop = false;
  public Color commentNodeColor = null;
  public Optional<List<Double>> blunderWinrateThresholds;
  public Optional<Map<Double, Color>> blunderNodeColors;
  // public int nodeColorMode = 0;
  public boolean appendWinrateToComment = true;
  public int boardPositionProportion = 4;
  public int limitBranchLength = 0;
  public int limitMaxSuggestion = 10;
  public boolean showNoSuggCircle = false;
  public int moveListWinrateThreshold = 0;
  public int moveListScoreThreshold = 0;
  public int moveListVisitsThreshold = 0;
  public int analyzeUpdateIntervalCentisec;
  public int analyzeUpdateIntervalCentisecSSH;
  public boolean showHeat = false;
  public boolean showHeatAfterCalc = false;
  public Color bestColor;
  //  public String gtpConsoleStyle = "";
  //  private final String defaultGtpConsoleStyle =
  //      "body {background:#000000; color:#d0d0d0; font-family:Consolas, Menlo, Monaco, 'Ubuntu
  // Mono', monospace;font-size:10px; margin:4px;} .command {color:#ffffff;font-weight:bold;}
  // .winrate {color:#ffffff;font-weight:bold;} .coord {color:#ffffff;font-weight:bold;}";

  public boolean firstButton = true;
  public boolean lastButton = true;
  public boolean clearButton = true;
  public boolean countButton = true;
  public boolean finalScore = true;
  public boolean forward10 = true;
  public boolean backward10 = true;
  public boolean forward1 = true;
  public boolean gotomove = true;
  public boolean backward1 = true;
  public boolean openfile = false;
  public boolean savefile = false;
  public boolean analyse = true;
  public boolean kataEstimate = true;
  public boolean heatMap = true;
  public boolean backMain = false;
  public boolean setMain = true;
  public boolean batchOpen = true;
  public boolean refresh = true;
  public boolean tryPlay = true;
  public boolean analyzeList = true;
  public boolean move = true;
  public boolean moveRank = true;
  public boolean coords = true;
  public boolean liveButton = true;
  public boolean badMoves = false;
  public boolean autoPlay = true;
  public boolean deleteMove = true;
  public boolean share = true;
  public boolean flashAnalyze = false;
  public boolean enableLizzieCache = true;
  public boolean showQuickLinks = false;
  public double minPlayoutRatioForStats = 0.0;

  public int matchAiMoves = 5;
  public double matchAiPercentsPlayouts = 20.0;
  public int matchAiFirstMove = -1;
  public int matchAiLastMove = 1000;
  public int movelistSelectedIndex = 0;
  public int movelistSelectedIndexTop = 0;
  public boolean moveListTopCurNode = false;

  public int winrateDiffRange1 = 3;
  public int winrateDiffRange2 = 10;

  public int scoreDiffRange1 = 2;
  public int scoreDiffRange2 = 5;

  public int openingEndMove = 60;
  public int middleEndMove = 160;

  public boolean checkRandomVisits = false;;
  public double percentsRandomVisits = 10;

  public int suggestionColorRatio = 2;
  public boolean showBestMovesGraph = false;
  public boolean showBestMovesList = true;
  public String uploadUser = "";
  public String uploadPassWd = "";

  public String shareLabel1 = "";
  public String shareLabel2 = "";
  public String shareLabel3 = "";
  public String shareLabel4 = "";
  public String shareLabel5 = "";
  public boolean sharePublic = true;

  public boolean autoCheckVersion = true;
  public String autoCheckDate = "";
  public int ignoreVersion = 0;

  public String kataRules = "";
  public boolean autoLoadKataRules = false;
  public boolean showTitleWr = true;

  public boolean chkLzsaiEngineMem = false;
  public String txtLzsaiEngineMem = "";
  public boolean autoLoadLzsaiEngineMem = false;

  public boolean chkLzsaiEngineVisits = false;
  public String txtLzsaiEngineVisits = "";
  public boolean autoLoadLzsaiEngineVisits = false;

  public boolean chkLzsaiEngineLagbuffer = false;
  public String txtLzsaiEngineLagbuffer = "";
  public boolean autoLoadLzsaiEngineLagbuffer = false;

  public boolean chkLzsaiEngineResign = false;
  public String txtLzsaiEngineResign = "";
  public boolean autoLoadLzsaiEngineResign = false;

  public boolean chkKataEnginePDA = false;
  public String txtKataEnginePDA = "";
  public String autoLoadTxtKataEnginePDA = "";
  public boolean autoLoadKataEnginePDA = false;

  //  public boolean chkKataEngineRPT = false;
  //  public String txtKataEngineRPT = "";
  //  public boolean autoLoadKataEngineRPT = false;

  public boolean chkKataEngineWRN = false;
  public String txtKataEngineWRN = "";
  public String autoLoadTxtKataEngineWRN = "";
  public boolean autoLoadKataEngineWRN = false;

  public boolean chkKataEngineThreads = false;
  public String txtKataEngineThreads = "";
  public boolean autoLoadKataEngineThreads = false;

  public boolean showWRNInMenu = true;
  public boolean showPDAInMenu = true;

  public boolean enginePkPonder = false;
  public boolean alwaysGtp = true;
  public boolean noCapture = false;

  public boolean chkPkStartNum = false;
  public int pkStartNum = 0;

  public boolean chkDymPDA = false;
  public boolean chkStaticPDA = false;
  public String dymPDACap;
  public String staticPDAcur;
  public boolean chkAutoPDA = false;
  public String AutoPDA;

  public boolean allowDoubleClick = true;
  public boolean allowDrag = false;
  public boolean noRefreshOnSub = true;

  public boolean estimateArea = false;

  public int anaGameResignStartMove = 150;
  public int anaGameResignMove = 2;
  public Double anaGameResignPercent = 5.0;

  public boolean chkEngineSgfStart = false;
  public boolean engineSgfStartRandom = false;

  public boolean showEditbar = true;
  public boolean showForceMenu = true;
  public boolean showRuleMenu = true;
  public boolean showParamMenu = true;
  public boolean showGobanMenu = true;
  public boolean showSaveLoadMenu = true;
  public boolean showDoubleMenuBtn = false;

  public boolean showRightMenu = true;
  public boolean showVarMove = true;

  public boolean showBasicBtn = true;

  public boolean subBoardRaw = false;
  // public boolean firstUse = false;

  public boolean loadSgfLast = false;
  public boolean useShortcutKataEstimate;
  public int backgroundFilter = 20;

  public boolean enableAnaGameRamdonStart = false;
  public int anaGameRandomMove = 20;
  public double anaGameRandomWinrateDiff = 0.5;
  public double anaGameRandomPlayoutsDiff = 5;

  // 自动分析
  public int autoAnaStartMove = -1;
  public int autoAnaEndMove = -1;
  public boolean isAutoAna = false;
  public boolean isStartingAutoAna = false;
  public int autoAnaTime = 0;
  public int autoAnaPlayouts = 0;
  public int autoAnaFirstPlayouts = 0;
  public boolean anaBlack = true;
  public boolean anaWhite = true;

  //
  public boolean usePipeReadBoard = false;
  public boolean anaFrameShowNext = true;
  public boolean anaFrameUseMouseMove = false;
  public boolean userKnownX = false;

  public boolean useZenEstimate = false;
  public String zenEstimateCommand = "ZenEstimate" + File.separator + "ZenGTP.exe";

  public boolean showDoubleMenu = true;
  public boolean showDoubleMenuVar = true;
  public boolean showDoubleMenuMoveInfo = false;
  public boolean showDoubleMenuGameControl = true;

  public String hostName = "";
  public boolean firstTimeLoad = true;

  public boolean showListPane = true;

  public int firstEngineMinMove = 0;
  public int secondEngineMinMove = 0;
  public int firstEngineResignMoveCounts = 2;
  public int secondEngineResignMoveCounts = 2;
  public double firstEngineResignWinrate = 10.0;
  public double secondEngineResignWinrate = 10.0;

  public boolean independentSubBoardLocked = false;
  public boolean independentMainBoardLocked = false;

  public boolean isShowingIndependentMain = false;
  public boolean isShowingIndependentSub = false;
  public boolean independentMainBoardTop = false;
  public boolean independentSubBoardTop = false;

  public boolean analyzeAllBranch = false;

  // public int autoInterval = -1;
  public boolean autoResume = false;
  public boolean autoSaveOnExit = true;
  public boolean loadASaveZoom = true;

  public boolean checkPlayBlack = true;
  public boolean checkContinuePlay = false;
  public boolean genmoveGameNoTime = false;
  public Double newGameKomi = 7.5;
  public int newGameHandicap = 0;

  public Double newEngineGameKomi = 7.5;
  public int newEngineGameHandicap = 0;

  public boolean UsePlayMode = true;
  public int useLanguage = 0; // 0默认 1中文2英文3韩语4日本語..
  public boolean needReopenFirstUseSettings = false;

  public boolean autoCheckEngineAlive = true;

  public boolean limitMyTime = false;
  public int mySaveTime = 3;
  public int myByoyomiSeconds = 4;
  public int myByoyomiTimes = 2;

  public boolean showPvVisits = false;
  public boolean showPvVisitsLastMove = false;
  public boolean showPvVisitsAllMove = false;
  public int pvVisitsLimit = 0;
  //  public int allFontSize = 12;
  public boolean removeDeadChainInVariation = true;
  public boolean UsePureNetInGame = false;
  public boolean isShowingMarkupTools = false;
  public boolean useFactor = false;
  public boolean firstTimeFactor = true;
  public static boolean isScaled = false;
  // public Float scaleFactor = 1f;
  //  java.awt.Toolkit.getDefaultToolkit().getScreenResolution() / 96.0f;

  public boolean useFreeHandicap = true;
  public String currentKataGoRules = "";

  public String analysisEngineCommand =
      "katago analysis -model model.bin.gz -config analysis.cfg -quit-without-waiting";
  public int analysisMaxVisits = 1;
  public int analysisStartMove = -1;
  public int analysisEndMove = -1;
  public boolean analysisUseCurrentRules = true;
  public boolean analysisEnginePreLoad = false;
  public boolean analysisAlwaysOverride = false;
  public String analysisSpecificRules = "";

  public boolean analysisRecentIsPartGame = false;
  public boolean showScoreLeadLine = true;
  public boolean showMouseOverWinrateGraph = true;
  public boolean isChinese;

  public static int frameFontSize = 12;
  public static int menuHeight = 20;
  public static int menuIconSize = 16;

  public boolean isDeletingPersist = false;
  public boolean showPreviousBestmovesInEngineGame = false;
  public boolean showPreviousBestmovesOnlyFirstMove = true;
  public boolean showDetailedToolbarMenu = false;

  public int selectAllowMoves = 999;
  public int selectAvoidMoves = 999;
  public int selectAllowCustomMoves = 5;
  public int selectAvoidCustomMoves = 5;

  public boolean showTimeControlInMenu = false;
  public boolean showPlayoutControlInMenu = false;

  public int suggestionInfoWinrate = 1;
  public int suggestionInfoPlayouts = 2;
  public int suggestionInfoScoreLead = 3;
  public boolean useDefaultInfoRowOrder = true;

  public boolean useIinCoordsName = false;
  public boolean useFoxStyleCoords = false;
  public boolean useNumCoordsFromTop = false;
  public boolean useNumCoordsFromBottom = false;

  public boolean autoReplayBranch = false;
  public boolean autoReplayDisplayEntireVariationsFirst = false;
  public double displayEntireVariationsFirstSeconds = 3.0;
  public boolean showStoneShadow = true;
  public boolean usePureBackground = false;
  public Color pureBackgroundColor = null;
  public boolean usePureBoard = false;
  public Color pureBoardColor = null;
  public boolean usePureStone = false;
  public boolean winrateAlwaysBlack = false;
  private String blackStoneImageString = "";
  private String whiteStoneImageString = "";
  private String boardImageString = "";
  private String backgroundImageString = "";
  public int maxGameThinkingTimeSeconds = 2;

  public boolean showAnalyzeController = false;
  public boolean analyzeBlack = true;
  public boolean analyzeWhite = true;
  public boolean autoWrapToolBar = true;
  public boolean showTopToolBar = true;
  public boolean autoSavePlayedGame = true;

  public boolean limitPlayout = false;
  public boolean limitTime = true;
  public long limitPlayouts = 2000;
  public long maxAnalyzeTimeMillis = 600000; // 600*1000

  public boolean showKataGoEstimateBySize = false;
  public boolean showKataGoEstimateBigBelow = false;
  public boolean showKataGoEstimateNormal = true;
  public boolean showKataGoEstimateNotOnlive = false;

  public boolean showPureEstimateBySize = false;
  public boolean showPureEstimateBigBelow = true;
  public boolean showPureEstimateNormal = false;
  public boolean showPureEstimateNotOnlive = false;

  public boolean useJavaLooks = false;
  public boolean shouldWidenCheckBox = false;
  public boolean showNextMoveBlunder = true;

  public int batchAnalysisPlayouts = 100;
  public int minPlayoutsForNextMove = 30;

  public boolean isShowingBlunderTabel = false;
  public boolean blunderTabelOnlyAfter = false;
  public boolean hideBlunderControlPane = false;
  public boolean allowCloseCommentControlHint = true;

  public int blunderSortNumNAF = 2;
  public boolean blunderIsSortedNAF = false;
  public boolean blunderSortIsOriginOrderNAF = true;

  public int blunderSortNumAF = 0;
  public boolean blunderIsSortedAF = false;
  public boolean blunderSortIsOriginOrderAF = true;

  public int blunderWinThreshold = 0;
  public int blunderScoreThreshold = 0;
  public int blunderPlayoutsThreshold = 0;

  public boolean autoAnaDiffEnable = false;

  public boolean autoAnaDiffBlack = true;
  public boolean autoAnaDiffWhite = true;

  public boolean autoAnaDiffUseWin = true;
  public boolean autoAnaDiffUseScore = false;
  public double autoAnaDiffWinThreshold = 10.0;
  public double autoAnaDiffScoreThreshold = 3.0;

  public int autoAnaDiffTime = 10;
  public int autoAnaDiffPlayouts = -1;
  public int autoAnaDiffFirstPlayouts = -1;

  public boolean isHiddenKataEstimate = false;
  public boolean saveKataEstimateStatus = false;

  public int fastCommandsWidth = 500;
  public int fastCommandsHeight = 500;

  public ArrayList<String> recentFilePaths;
  public boolean showReplaceFileHint = true;
  public JSONObject customLayout1;
  public JSONObject customLayout2;

  public boolean showScrollVariation = true;
  public boolean ignoreOutOfWidth = false;
  public int maxTreeWidth = 10000;

  public boolean disableWRNInGame = true;
  public boolean notStartPondering = false;

  public int gameStatisticsCustomStart = 10;
  public int gameStatisticsCustomEnd = 200;
  public boolean moveListFilterCurrent = false;
  public boolean lossPanelSelectWinrate = false;
  public boolean analysisAutoQuit = true;
  public boolean firstLoadKataGo = true;

  public int txtMoveRankMarkLastMove = 3;
  public int moveRankMarkLastMove = 1; // -1关闭 0全部
  public boolean disableMoveRankInOrigin = false;
  public boolean logConsoleToFile = false;
  public boolean logGtpToFile = false;
  public boolean readBoardPonder = false;
  public boolean readBoardGetFocus = true;

  public int otherSizeWidth = 21;
  public int otherSizeHeight = 21;

  public boolean stopAtEmptyBoard = false;

  public boolean useScoreDiffInVariationTree = true;
  public double scoreDiffInVariationTreeFactor = 0.5;

  public boolean useScoreLossInMoveRank = true;
  public boolean useWinLossInMoveRank = true;

  public double winLossThreshold1 = -1;
  public double winLossThreshold2 = -3;
  public double winLossThreshold3 = -6;
  public double winLossThreshold4 = -12;
  public double winLossThreshold5 = -24;

  public double scoreLossThreshold1 = -0.5;
  public double scoreLossThreshold2 = -1.5;
  public double scoreLossThreshold3 = -3;
  public double scoreLossThreshold4 = -6;
  public double scoreLossThreshold5 = -12;

  public boolean showPonderLimitedTips = true;
  public int foxAfterGet = 0; // 0=最小化,1=关闭,2=无动作
  public String lastFoxName = "";

  public boolean continueWithBestMove = false;

  public boolean delayShowCandidates = false;
  public double delayCandidatesSeconds = 10;

  public boolean showBlackCandidates = true;
  public boolean showWhiteCandidates = true;

  public boolean useTerritoryInScore = false;

  private JSONObject loadAndMergeSaveBoardConfig(
      JSONObject defaultCfg, String fileName, boolean needValidation) throws IOException {
    File file = new File(fileName);
    File dir = new File("save");
    if (!dir.exists()) {
      dir.mkdirs();
    }
    if (!file.canRead()) {
      System.err.printf("Creating config file %s\n", fileName);

      try {
        writeConfig(defaultCfg, file);
      } catch (JSONException e) {
        e.printStackTrace();
        System.exit(1);
      }
    }

    FileInputStream fp = new FileInputStream(file);
    InputStreamReader reader = new InputStreamReader(fp, "utf-8");

    JSONObject mergedcfg = new JSONObject(new JSONTokener(reader));
    boolean modified = mergeDefaults(mergedcfg, defaultCfg);

    fp.close();

    // Validate and correct settings
    //    if (needValidation && validateAndCorrectSettings(mergedcfg)) {
    //      modified = true;
    //    }
    if (needValidation) checkEmptyBlunderThresholds(mergedcfg);
    if (modified) {
      writeConfig(mergedcfg, file);
    }
    return mergedcfg;
  }

  private JSONObject loadAndMergeConfig(
      JSONObject defaultCfg, String fileName, boolean needValidation) throws IOException {
    File file = new File(fileName);
    if (!file.canRead()) {
      System.err.printf("Creating config file %s\n", fileName);
      try {
        writeConfig(defaultCfg, file);
      } catch (JSONException e) {
        e.printStackTrace();
        System.exit(1);
      }
    }

    FileInputStream fp = new FileInputStream(file);
    InputStreamReader reader = new InputStreamReader(fp, "utf-8");
    JSONObject mergedcfg = new JSONObject(new JSONTokener(reader));
    boolean modified = mergeDefaults(mergedcfg, defaultCfg);

    fp.close();

    // Validate and correct settings
    //    if (needValidation && validateAndCorrectSettings(mergedcfg)) {
    //      modified = true;
    //    }
    if (needValidation) checkEmptyBlunderThresholds(mergedcfg);
    if (modified) {
      writeConfig(mergedcfg, file);
    }
    return mergedcfg;
  }

  private JSONObject loadAndMergeConfigdef(
      JSONObject defaultCfg, String fileName, boolean needValidation) throws IOException {
    File file = new File(fileName);
    System.err.printf("Creating config file %s\n", fileName);
    try {
      writeConfig(defaultCfg, file);
    } catch (JSONException e) {
      e.printStackTrace();
      System.exit(1);
    }

    FileInputStream fp = new FileInputStream(file);
    InputStreamReader reader = new InputStreamReader(fp, "utf-8");

    JSONObject mergedcfg = new JSONObject(new JSONTokener(reader));
    boolean modified = mergeDefaults(mergedcfg, defaultCfg);

    fp.close();

    // Validate and correct settings
    // if (needValidation && validateAndCorrectSettings(mergedcfg)) {
    //  modified = true;
    // }
    if (needValidation) checkEmptyBlunderThresholds(mergedcfg);
    if (modified) {
      writeConfig(mergedcfg, file);
    }
    return mergedcfg;
  }

  /**
   * Check settings to ensure its consistency, especially for those whose types are not <code>
   * boolean</code>. If any inconsistency is found, try to correct it or to report it. <br>
   * For example, we only support square boards of size >= 2x2. If the configured board size is not
   * in the list above, we should correct it.
   *
   * @param config The config json object to check
   * @return if any correction has been made.
   */
  //    private boolean validateAndCorrectSettings(JSONObject config) {
  //      boolean madeCorrections = false;
  //
  //      // Check ui configs
  //      JSONObject ui = config.getJSONObject("ui");
  //
  //      // Check board-size
  //      int boardSize = ui.optInt("board-size", 19);
  //      if (boardSize < 2) {
  //        // Correct it to default 19x19
  //        ui.put("board-size", 19);
  //        madeCorrections = true;
  //      }
  //
  //      // Check engine configs
  //      JSONObject leelaz = config.getJSONObject("leelaz");
  //      // Checks for startup directory. It should exist and should be a directory.
  //      String engineStartLocation = getBestDefaultLeelazPath();
  //      if (!(Files.exists(Paths.get(engineStartLocation))
  //          && Files.isDirectory(Paths.get(engineStartLocation)))) {
  //       // leelaz.put("engine-start-location", ".");
  //        madeCorrections = true;
  //      }
  //      if (checkEmptyBlunderThresholds(ui)) {
  //        madeCorrections = true;
  //      }
  //
  //      return madeCorrections;
  //    }

  /**
   * Apply the default blunder thresholds in Default theme if they are empty. This works only once
   * as the fix of #423 (missing default thresholds) so that users can set them empty again if they
   * like.
   *
   * @param ui The UI config json object to check
   * @return if any correction has been made.
   */
  private boolean checkEmptyBlunderThresholds(JSONObject config) {
    boolean alreadyTriedOnce = persisted.optBoolean("checked-empty-blunder-thresholds", false);
    if (alreadyTriedOnce) return false;
    JSONObject ui = config.getJSONObject("ui");
    boolean modified = false;
    String theme = ui.optString("theme", "");
    JSONArray blunderWinrateThresholds = ui.optJSONArray("blunder-winrate-thresholds");
    JSONArray blunderNodeColors = ui.optJSONArray("blunder-node-colors");
    boolean isDefaultTheme = theme.toLowerCase().equals("default") || theme.equals("默认");
    boolean isEmptyBlunderWinrateThresholds =
        (blunderWinrateThresholds == null || blunderWinrateThresholds.length() == 0);
    boolean isEmptyBlunderNodeColors =
        (blunderNodeColors == null || blunderNodeColors.length() == 0);

    if (isDefaultTheme && isEmptyBlunderWinrateThresholds && isEmptyBlunderNodeColors) {
      // https://github.com/featurecat/lizzie/issues/423#issuecomment-438878060
      ui.put("blunder-winrate-thresholds", new JSONArray("[-24,-12,-6,-3,-1,3,100]"));
      ui.put(
          "blunder-node-colors",
          new JSONArray(
              "[[155, 25, 150],[208, 16, 19],[200, 140, 50],[180, 180, 0],[140, 202, 34],[0, 220, 0],[0,230,230]]"));
      modified = true;
    }

    persisted.put("checked-empty-blunder-thresholds", true);
    return modified;
  }

  public void resetBlunderColor() {
    Theme theme = new Theme();
    if (theme.getTheme(uiConfig)) {
      theme.config.put("blunder-winrate-thresholds", new JSONArray("[-24,-12,-6,-3,-1,3,100]"));
      theme.config.put(
          "blunder-node-colors",
          new JSONArray(
              "[[155, 25, 150],[208, 16, 19],[200, 140, 50],[180, 180, 0],[140, 202, 34],[0, 220, 0],[0,210,210]]"));
      theme.save();
    } else {
      uiConfig.put("blunder-winrate-thresholds", new JSONArray("[-24,-12,-6,-3,-1,3,100]"));
      uiConfig.put(
          "blunder-node-colors",
          new JSONArray(
              "[[155, 25, 150],[208, 16, 19],[200, 140, 50],[180, 180, 0],[140, 202, 34],[0, 220, 0],[0,210,210]]"));
      try {
        save();
      } catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }
  }

  public static void copyFile(File sourceFile, File targetFile) throws IOException {
    FileInputStream input = new FileInputStream(sourceFile);
    BufferedInputStream inBuff = new BufferedInputStream(input);
    FileOutputStream output = new FileOutputStream(targetFile);
    BufferedOutputStream outBuff = new BufferedOutputStream(output);

    byte[] b = new byte[1024 * 5];
    int len;
    while ((len = inBuff.read(b)) != -1) {
      outBuff.write(b, 0, len);
    }
    outBuff.flush();

    inBuff.close();
    outBuff.close();
    output.close();
    input.close();
  }

  public Config() throws IOException {
    JSONObject defaultConfig = createDefaultConfig();
    JSONObject persistConfig = createPersistConfig();
    JSONObject saveBoardConf = createSaveBoardConfig();
    try {
      this.persisted = loadAndMergeConfig(persistConfig, persistFilename, false);
    } catch (Exception e) {
      e.printStackTrace();
      this.persisted = persistConfig;
    }
    // Main properties
    try {
      this.config = loadAndMergeConfig(defaultConfig, configFilename, true);
    } catch (Exception e) {
      try {
        File file = new File("");
        String courseFile = file.getCanonicalPath();
        File fileconfig = new File(courseFile + File.separator + configFilename);
        File wrongfileconfig = new File(courseFile + File.separator + "config_wrong.txt");
        copyFile(fileconfig, wrongfileconfig);
      } catch (Exception ex) {
      }
      this.config = loadAndMergeConfigdef(defaultConfig, configFilename, true);
    }
    // Persisted properties

    this.saveBoard = loadAndMergeSaveBoardConfig(saveBoardConf, saveBoardFilename, false);

    firstTimeFactor = persisted.optBoolean("first-time-factor", true);
    leelazConfig = config.getJSONObject("leelaz");
    saveBoardConfig = saveBoard.getJSONObject("save");
    uiConfig = config.getJSONObject("ui");
    persistedUi = persisted.getJSONObject("ui-persist");

    fastCommandsWidth = persistedUi.optInt("fast-commands-width", 500);
    fastCommandsHeight = persistedUi.optInt("fast-commands-height", 500);

    theme = new Theme();
    theme.getTheme(uiConfig);

    // showBorder = uiConfig.optBoolean("show-border", false);
    showMoveNumber = uiConfig.getBoolean("show-move-number");
    onlyLastMoveNumber = uiConfig.optInt("only-last-move-number");

    allowMoveNumber = showMoveNumber ? (onlyLastMoveNumber > 0 ? onlyLastMoveNumber : -1) : 0;
    allowMoveNumber = uiConfig.optInt("allow-move-number", allowMoveNumber);
    newMoveNumberInBranch = uiConfig.optBoolean("new-move-number-in-branch", true);
    isClassicMode = uiConfig.optBoolean("is-classic-mode", false);
    showStatus = isClassicMode ? false : uiConfig.getBoolean("show-status");
    // changedStatus = uiConfig.optBoolean("changed-status", false);
    showBranch = uiConfig.getBoolean("show-leelaz-variation");
    showWinrateGraph = uiConfig.getBoolean("show-winrate-graph");
    largeWinrateGraph = uiConfig.optBoolean("large-winrate-graph", false);
    showBlunderBar = uiConfig.optBoolean("show-blunder-bar", true);
    showMoveAllInBranch = uiConfig.optBoolean("show-moveall-inbranch", false);
    // weightedBlunderBarHeight = uiConfig.optBoolean("weighted-blunder-bar-height", false);
    // dynamicWinrateGraphWidth = uiConfig.optBoolean("dynamic-winrate-graph-width", true);
    showVariationGraph = uiConfig.getBoolean("show-variation-graph");
    showComment = uiConfig.optBoolean("show-comment", true);
    if (extraMode == ExtraMode.Double_Engine) showComment = false;
    showCaptured = uiConfig.getBoolean("show-captured");
    // showKataGoScoreMean = uiConfig.optBoolean("show-katago-scoremean", true);
    showKataGoScoreLeadWithKomi = uiConfig.optBoolean("show-katago-score-lead-with-komi", false);
    // kataGoScoreMeanAlwaysBlack = uiConfig.optBoolean("katago-scoremean-alwaysblack", false);
    showHeat = uiConfig.optBoolean("show-heat", false);
    showHeatAfterCalc = uiConfig.optBoolean("show-heat-aftercalc", false);
    showMoveNumberFromOne = uiConfig.optBoolean("movenumber-from-one", false);
    // kataGoNotShowWinrate = uiConfig.optBoolean("katago-notshow-winrate", false);
    showKataGoEstimate = uiConfig.optBoolean("show-katago-estimate", false);
    // scoreMeanWinrateGraphBoard = uiConfig.optBoolean("scoremean-winrategraph-board", false);
    showSuggestionOrder = uiConfig.optBoolean("show-suggestion-order", true);
    showSuggestionMaxRed = uiConfig.optBoolean("show-suggestion-maxred", true);

    estimateCommand =
        uiConfig.optString(
            "estimate-command",
            "katago"
                + File.separator
                + "katago.exe gtp -model weights"
                + File.separator
                + "kata20bs530.bin.gz -config katago"
                + File.separator
                + "estimate.cfg");
    estimateThreshold = uiConfig.optDouble("estimate-threshold", 0.4);

    showKataGoEstimateNormal = uiConfig.optBoolean("show-katago-estimate-normal", true);
    showKataGoEstimateBySize = uiConfig.optBoolean("show-katago-estimate-by-size", false);
    showKataGoEstimateBigBelow = uiConfig.optBoolean("show-katago-estimate-big-below", false);
    showKataGoEstimateNotOnlive = uiConfig.optBoolean("show-katago-estimate-not-on-live", false);

    showPureEstimateNormal = uiConfig.optBoolean("show-pure-estimate-normal", false);
    showPureEstimateBySize = uiConfig.optBoolean("show-pure-estimate-by-size", false);
    showPureEstimateBigBelow = uiConfig.optBoolean("show-pure-estimate-big-below", true);
    showPureEstimateNotOnlive = uiConfig.optBoolean("show-pure-estimate-not-on-live", false);

    useJavaLooks = uiConfig.optBoolean("use-java-looks", !OS.isWindows());
    showNextMoveBlunder = uiConfig.optBoolean("show-next-move-blunder", true);
    batchAnalysisPlayouts = uiConfig.optInt("batch-analysis-playouts", 100);
    minPlayoutsForNextMove = uiConfig.optInt("min-playouts-for-next-move", 30);
    shouldWidenCheckBox =
        useJavaLooks
            || !(Boolean) Toolkit.getDefaultToolkit().getDesktopProperty("win.xpstyle.themeActive");
    showSuggestionVariations = uiConfig.optBoolean("show-suggestion-variations", true);
    subBoardRaw = uiConfig.optBoolean("subboard-raw", false);
    backgroundFilter = theme.backgroundFilter();
    enableAnaGameRamdonStart = uiConfig.optBoolean("enable-anagame-randomstart", false);
    usePipeReadBoard = uiConfig.optBoolean("use-pipeline-readboard", false);
    anaFrameShowNext = uiConfig.optBoolean("anaframe-show-next", true);
    anaFrameUseMouseMove = uiConfig.optBoolean("anaframe-use-mousemove", false);
    anaGameRandomMove = uiConfig.optInt("anagame-random-move", 20);
    anaGameRandomWinrateDiff = uiConfig.optDouble("anagame-random-winratediff", 0.5);
    anaGameRandomPlayoutsDiff = uiConfig.optDouble("anagame-random-playoutdiff", 5);
    // allowDrageDoubleClick = uiConfig.optBoolean("allow-drag-doubleclick", true);
    userKnownX = uiConfig.optBoolean("user-known-x", false);
    useZenEstimate = uiConfig.optBoolean("use-zen-estimate", false);
    zenEstimateCommand =
        uiConfig.optString("use-estimate-command", "ZenEstimate" + File.separator + "ZenGTP.exe");
    showDoubleMenu = uiConfig.optBoolean("show-double-menu", true);
    showDoubleMenuGameControl = uiConfig.optBoolean("show-double-menu-game-control", true);
    showDoubleMenuVar = uiConfig.optBoolean("show-double-menu-var", true);
    showDoubleMenuMoveInfo = uiConfig.optBoolean("show-double-menu-moveinfo", false);
    firstTimeLoad = uiConfig.optBoolean("first-time-load", true);
    hostName = uiConfig.optString("host-name", "");
    showListPane = uiConfig.optBoolean("show-list-pane", true);

    isShowingBlunderTabel = uiConfig.optBoolean("is-showing-blunder-table", false);
    blunderTabelOnlyAfter = uiConfig.optBoolean("blunder-table-only-after", false);
    allowCloseCommentControlHint = uiConfig.optBoolean("allow-close-comment-control-hint", true);
    hideBlunderControlPane = uiConfig.optBoolean("hide-blunder-table-control-pane", false);
    blunderSortNumNAF = uiConfig.optInt("blunder-sort-num-NAF", 2);
    blunderIsSortedNAF = uiConfig.optBoolean("blunder-is-sorted-NAF", false);
    blunderSortIsOriginOrderNAF = uiConfig.optBoolean("blunder-sort-is-origin-order-NAF", true);
    blunderSortNumAF = uiConfig.optInt("blunder-sort-num-AF", 0);
    blunderIsSortedAF = uiConfig.optBoolean("blunder-is-sorted-AF", false);
    blunderSortIsOriginOrderAF = uiConfig.optBoolean("blunder-sort-is-origin-order-AF", true);

    blunderWinThreshold = uiConfig.optInt("blunder-win-threshold", 0);
    blunderScoreThreshold = uiConfig.optInt("blunder-score-threshold", 0);
    blunderPlayoutsThreshold = uiConfig.optInt("blunder-playouts-threshold", 0);

    autoAnaDiffEnable = uiConfig.optBoolean("auto-ana-diff-enable", false);
    autoAnaDiffBlack = uiConfig.optBoolean("auto-ana-diff-black", true);
    autoAnaDiffWhite = uiConfig.optBoolean("auto-ana-diff-white", true);

    autoAnaDiffUseWin = uiConfig.optBoolean("auto-ana-diff-use-win", true);
    autoAnaDiffUseScore = uiConfig.optBoolean("auto-ana-diff-use-score", false);
    autoAnaDiffWinThreshold = uiConfig.optDouble("auto-ana-diff-win-threshold", 10.0);
    autoAnaDiffScoreThreshold = uiConfig.optDouble("auto-ana-diff-score-threshold", 3.0);

    autoAnaDiffTime = uiConfig.optInt("auto-ana-diff-time", 10);
    autoAnaDiffPlayouts = uiConfig.optInt("auto-ana-diff-playouts", -1);
    autoAnaDiffFirstPlayouts = uiConfig.optInt("auto-ana-diff-first-playouts", -1);

    saveKataEstimateStatus = uiConfig.optBoolean("save-kata-estimate-status", false);

    firstEngineMinMove = uiConfig.optInt("first-engine-min-move", 0);
    secondEngineMinMove = uiConfig.optInt("second-engine-min-move", 0);
    firstEngineResignMoveCounts = uiConfig.optInt("first-engine-resign-move-counts", 2); // =2;
    secondEngineResignMoveCounts = uiConfig.optInt("second-engine-resign-move-counts", 2);
    firstEngineResignWinrate = uiConfig.optDouble("first-engine-resign-winrate", 10.0); // 10.0;
    secondEngineResignWinrate = uiConfig.optDouble("second-engine-resign-winrate", 10.0);

    independentSubBoardLocked = uiConfig.optBoolean("independent-subboard-locked", false);
    independentMainBoardLocked = uiConfig.optBoolean("independent-mainboard-locked", false);

    isShowingIndependentMain = uiConfig.optBoolean("showing-independent-main", false);
    isShowingIndependentSub = uiConfig.optBoolean("showing-independent-sub", false);
    independentMainBoardTop = uiConfig.optBoolean("independent-main-board-top", false);
    independentSubBoardTop = uiConfig.optBoolean("independent-sub-board-top", false);

    analyzeAllBranch = uiConfig.optBoolean("analyze-all-branch", false);

    // autoInterval = uiConfig.optInt("autosave-interval-millseconds", -1);
    autoResume = uiConfig.optBoolean("resume-previous-game", false);
    autoSaveOnExit = uiConfig.optBoolean("auto-save-exit", true);
    loadASaveZoom = uiConfig.optBoolean("load-save-zoom", true);

    checkPlayBlack = uiConfig.optBoolean("check-play-black", true);
    checkContinuePlay = uiConfig.optBoolean("check-continue-play", false);
    genmoveGameNoTime = uiConfig.optBoolean("genmove-game-notime", false);
    newGameKomi = uiConfig.optDouble("new-game-komi", 7.5);
    newGameHandicap = uiConfig.optInt("new-game-handicap", 0);

    newEngineGameKomi = uiConfig.optDouble("new-engine-game-komi", 7.5);
    newEngineGameHandicap = uiConfig.optInt("new-engine-game-handicap", 0);
    //    showKataPdaWrnHint = uiConfig.optBoolean("show-kata-pdawrn-hint", true);
    UsePlayMode = uiConfig.optBoolean("use-play-mode", true);
    useLanguage = uiConfig.optInt("use-language", 0);
    autoCheckEngineAlive = uiConfig.optBoolean("auto-check-engine-alive", true);
    limitMyTime = uiConfig.optBoolean("limit-my-time", false);
    mySaveTime = uiConfig.optInt("my-save-time", 3);
    myByoyomiSeconds = uiConfig.optInt("my-byoyomo-seconds", 4);
    myByoyomiTimes = uiConfig.optInt("my-byoyomo-times", 2);
    showKataGoEstimateOnSubbord = uiConfig.optBoolean("show-katago-estimate-onsubbord", false);
    showKataGoEstimateOnMainbord = uiConfig.optBoolean("show-katago-estimate-onmainboard", true);
    if (!showKataGoEstimateOnMainbord && !showKataGoEstimateOnSubbord && showKataGoEstimate)
      isHiddenKataEstimate = true;
    // showBestMoves = uiConfig.getBoolean("show-best-moves");
    showNextMoves = uiConfig.getBoolean("show-next-moves");
    showSubBoard = uiConfig.getBoolean("show-subboard");
    hideSubBoardFromLargeWinrate = uiConfig.optBoolean("hide-subboard-from-large-winrate");
    largeSubBoard = uiConfig.getBoolean("large-subboard");
    // handicapInsteadOfWinrate =
    // uiConfig.getBoolean("handicap-instead-of-winrate");
    // showDynamicKomi = uiConfig.getBoolean("show-dynamic-komi");
    showWinrateInSuggestion = uiConfig.optBoolean("show-winrate-in-suggestion", true);
    showPlayoutsInSuggestion = uiConfig.optBoolean("show-playouts-in-suggestion", true);
    showScoremeanInSuggestion = uiConfig.optBoolean("show-scoremean-in-suggestion", true);
    showNameInBoard = uiConfig.optBoolean("show-name-in-board", true);
    openHtmlOnLive = uiConfig.optBoolean("open-html-onlive", true);
    readKomi = uiConfig.optBoolean("read-komi", true);
    alwaysGotoLastOnLive = uiConfig.optBoolean("always-gotolast-onlive", false);
    alwaysSyncBoardStat = uiConfig.optBoolean("always-sync-boardstat", true);
    whiteSuggestionWhite = uiConfig.optBoolean("white-suggestion-white", false);
    whiteSuggestionOrderWhite = uiConfig.optBoolean("white-suggestion-order-white", false);
    advanceTimeSettings = uiConfig.optBoolean("advance-time-settings", false);
    advanceTimeTxt = uiConfig.optString("advance-time-txt", "time_settings 10 2 1");

    kataTimeSettings = uiConfig.optBoolean("kata-time-settings", false);
    kataTimeType = uiConfig.optInt("kata-time-type", 0);
    kataTimeMainTimeMins = uiConfig.optInt("kata-time-main-time-mins", 10);
    kataTimeByoyomiSecs = uiConfig.optInt("kata-time-byoyomi-secs", 5);
    kataTimeByoyomiTimes = uiConfig.optInt("kata-time-byoyomi-times", 3);
    kataTimeFisherIncrementSecs = uiConfig.optInt("kata-time-fisher-increment-secs", 5);

    pkAdvanceTimeSettings = uiConfig.optBoolean("pk-advance-time-settings", false);
    advanceBlackTimeTxt = uiConfig.optString("advance-black-time-txt", "time_settings 10 2 1");
    advanceWhiteTimeTxt = uiConfig.optString("advance-white-time-txt", "time_settings 10 2 1");

    extraMode = getExtraMode(uiConfig.optInt("extra-mode", 0));
    playSound = uiConfig.optBoolean("play-sound", true);
    notPlaySoundInSync = uiConfig.optBoolean("not-play-sound-insync", true);
    noRefreshOnMouseMove = uiConfig.optBoolean("norefresh-onmouse-move", true);
    // syncBoth = uiConfig.optBoolean("sync-both", false);
    isShowingWinrateGraph = uiConfig.optBoolean("show-winrate-matchai", true);
    isShowingMoveList = uiConfig.optBoolean("show-movelist-matchai", true);
    moveListSelectedBranch = uiConfig.optInt("moveList-selected-branch", 0);
    // matchAiTemperature = uiConfig.optDouble("match-ai-temperature", 1);
    moveListTopCurNode = uiConfig.optBoolean("movelist-top-curnode", false);
    showMoveListGraph = uiConfig.optBoolean("show-movelist-graph", true);
    showBlueRing = uiConfig.optBoolean("show-blue-ring", true);

    showEditbar = uiConfig.optBoolean("show-edit-bar", true);
    showForceMenu = uiConfig.optBoolean("show-force-menu", true);
    showRuleMenu = uiConfig.optBoolean("show-rule-menu", true);
    showParamMenu = uiConfig.optBoolean("show-param-menu", true);
    showGobanMenu = uiConfig.optBoolean("show-goban-menu", true);
    showSaveLoadMenu = uiConfig.optBoolean("show-saveload-menu", true);
    showDoubleMenuBtn = uiConfig.optBoolean("show-double-menu-btn", false);
    showRightMenu = uiConfig.optBoolean("show-right-menu", true);
    showVarMove = uiConfig.optBoolean("show-var-move", true);
    showBasicBtn = uiConfig.optBoolean("show-basic-btn", true);
    badmovesalwaysontop = uiConfig.optBoolean("badmoves-always-ontop", false);
    mainsalwaysontop = uiConfig.optBoolean("mains-always-ontop", false);
    suggestionsalwaysontop = uiConfig.optBoolean("suggestions-always-ontop", false);
    appendWinrateToComment = uiConfig.optBoolean("append-winrate-to-comment", true);
    showCoordinates = uiConfig.optBoolean("show-coordinates", true);
    replayBranchIntervalSeconds = uiConfig.optDouble("replay-branch-interval-seconds", 0.9);
    colorByWinrateInsteadOfVisits = uiConfig.optBoolean("color-by-winrate-instead-of-visits");
    boardPositionProportion = uiConfig.optInt("board-postion-proportion", 4);
    showPvVisits = uiConfig.optBoolean("show-pv-visits", false);
    showPvVisitsLastMove = uiConfig.optBoolean("show-pv-visits-last-move", false);
    showPvVisitsAllMove = uiConfig.optBoolean("show-pv-visits-all-move", false);
    pvVisitsLimit = uiConfig.optInt("pv-visits-limit", 0);
    // allFontSize = uiConfig.optInt("all-font-size", 12);
    removeDeadChainInVariation = uiConfig.optBoolean("remove-dead-in-variation", true);
    UsePureNetInGame = uiConfig.optBoolean("use-pure-net-in-game", false);
    isShowingMarkupTools = uiConfig.optBoolean("show-markup-tools", false);
    useFactor = uiConfig.optBoolean("use-factor", false);
    useFreeHandicap = uiConfig.optBoolean("use-free-handicap", false);

    analysisEngineCommand =
        uiConfig.optString(
            "analysis-engine-command",
            "katago analysis -model model.bin.gz -config analysis.cfg -quit-without-waiting");
    analysisMaxVisits = uiConfig.optInt("analysis-max-visits", 1);
    analysisStartMove = uiConfig.optInt("analysis-start-move", -1);
    analysisEndMove = uiConfig.optInt("analysis-end-move", -1);
    analysisUseCurrentRules = uiConfig.optBoolean("analysis-use-current-rules", true);
    analysisEnginePreLoad = uiConfig.optBoolean("analysis-engine-preload", false);
    analysisAlwaysOverride = uiConfig.optBoolean("analysis-always-override", false);
    analysisSpecificRules = uiConfig.optString("analysis-specific-rules", "");
    showScoreLeadLine = uiConfig.optBoolean("show-score-lead-line", true);
    showMouseOverWinrateGraph = uiConfig.optBoolean("show-mouse-over-winrate-graph", true);
    frameFontSize = uiConfig.optInt("frame-font-size", 12);
    menuHeight = isFrameFontSmall() ? 20 : (isFrameFontMiddle() ? 25 : 30);
    menuIconSize = isFrameFontSmall() ? 16 : (isFrameFontMiddle() ? 20 : 24);
    showPreviousBestmovesInEngineGame =
        uiConfig.optBoolean("show-previous-bestmoves-in-enginegame", false);
    showPreviousBestmovesOnlyFirstMove =
        uiConfig.optBoolean("show-previous-bestmoves-only-first-move", true);
    showDetailedToolbarMenu = uiConfig.optBoolean("show-detailed-toolbar-menu", false);
    selectAllowMoves = uiConfig.optInt("select-allow-moves", 999);
    selectAvoidMoves = uiConfig.optInt("select-avoid-moves", 999);
    selectAllowCustomMoves = uiConfig.optInt("select-allow-custom-moves", 5);
    selectAvoidCustomMoves = uiConfig.optInt("select-allow-custom-moves", 5);
    customLayout1 = uiConfig.optJSONObject("custom-layout-1");
    customLayout2 = uiConfig.optJSONObject("custom-layout-2");

    limitBranchLength = leelazConfig.optInt("limit-branch-length", 0);
    limitMaxSuggestion = leelazConfig.optInt("limit-max-suggestion", 10);
    showNoSuggCircle = leelazConfig.optBoolean("show-nosugg-circle", false);
    enableLizzieCache = leelazConfig.optBoolean("enable-lizzie-cache", true);

    moveListScoreThreshold = uiConfig.optInt("move-list-score-threshold", 0);
    moveListWinrateThreshold = uiConfig.optInt("move-list-winrate-threshold", 0);
    moveListVisitsThreshold = uiConfig.optInt("move-list-visits-threshold", 0);
    showTimeControlInMenu = uiConfig.optBoolean("show-time-control-in-menu", false);
    showPlayoutControlInMenu = uiConfig.optBoolean("show-playout-control-in-menu", false);
    suggestionInfoWinrate = uiConfig.optInt("suggestion-info-winrate", 1);
    suggestionInfoPlayouts = uiConfig.optInt("suggestion-info-playouts", 2);
    suggestionInfoScoreLead = uiConfig.optInt("suggestion-info-scorelead", 3);
    useDefaultInfoRowOrder =
        suggestionInfoWinrate == 1 && suggestionInfoPlayouts == 2 && suggestionInfoScoreLead == 3;
    useIinCoordsName = uiConfig.optBoolean("use-i-in-coords-name", false);
    autoReplayBranch = uiConfig.optBoolean("auto-replay-branch", false);
    autoReplayDisplayEntireVariationsFirst =
        uiConfig.optBoolean("auto-replay-display-entire-variations-first", false);
    displayEntireVariationsFirstSeconds =
        uiConfig.optDouble("display-entire-variations-first-seconds", 3.0);
    firstButton = uiConfig.optBoolean("firstButton", true);
    lastButton = uiConfig.optBoolean("lastButton", true);
    clearButton = uiConfig.optBoolean("clearButton", true);
    countButton = uiConfig.optBoolean("countButton", true);
    finalScore = uiConfig.optBoolean("finalScore", false);
    forward10 = uiConfig.optBoolean("forward10", true);
    backward10 = uiConfig.optBoolean("backward10", true);
    forward1 = uiConfig.optBoolean("forward1", true);
    gotomove = uiConfig.optBoolean("gotomove", true);
    backward1 = uiConfig.optBoolean("backward1", true);
    openfile = uiConfig.optBoolean("openfile", false);
    savefile = uiConfig.optBoolean("savefile", false);
    analyse = uiConfig.optBoolean("analyse", true);
    kataEstimate = uiConfig.optBoolean("kataEstimate", true);
    heatMap = uiConfig.optBoolean("heatMap", true);
    backMain = uiConfig.optBoolean("backMain", false);
    setMain = uiConfig.optBoolean("setMain", true);
    batchOpen = uiConfig.optBoolean("batchOpen", true);
    refresh = uiConfig.optBoolean("refresh", true);
    tryPlay = uiConfig.optBoolean("tryPlay", true);
    analyzeList = uiConfig.optBoolean("analyze-list", true);
    move = uiConfig.optBoolean("move", true);
    moveRank = uiConfig.optBoolean("move-rank", true);
    coords = uiConfig.optBoolean("coords", true);
    autoPlay = uiConfig.optBoolean("autoPlay", true);
    showQuickLinks = uiConfig.optBoolean("show-quick-links", false);
    liveButton = uiConfig.optBoolean("liveButton", true);
    share = uiConfig.optBoolean("share", true);
    flashAnalyze =
        uiConfig.optBoolean("flash-analyze", showDoubleMenu && showTopToolBar ? false : true);
    badMoves = uiConfig.optBoolean("badMoves", showDoubleMenu && showTopToolBar ? false : true);
    showAnalyzeController = uiConfig.optBoolean("show-analyze-controller", false);
    autoWrapToolBar = uiConfig.optBoolean("auto-wrap-tool-bar", true);
    showTopToolBar = uiConfig.optBoolean("show-top-tool-bar", true);
    autoSavePlayedGame = uiConfig.optBoolean("auto-save-played-game", true);
    limitPlayout = uiConfig.optBoolean("limit-playout", false);
    limitTime = uiConfig.optBoolean("limit-time", true);
    limitPlayouts = uiConfig.optLong("limit-playouts", 100000);
    minPlayoutRatioForStats = uiConfig.optDouble("min-playout-ratio-for-stats", 0.0);
    matchAiMoves = uiConfig.optInt("match-ai-moves", 3);
    matchAiPercentsPlayouts = uiConfig.optDouble("match-ai-percents-playouts", 20.0);
    matchAiFirstMove = uiConfig.optInt("match-ai-firstmove", -1);
    matchAiLastMove = uiConfig.optInt("match-ai-lastmove", 1000);
    movelistSelectedIndex = uiConfig.optInt("movelist-selected-index", 0);
    movelistSelectedIndexTop = uiConfig.optInt("movelist-selected-indextop", 0);
    winrateDiffRange1 = uiConfig.optInt("winrate-diff-range1", 3);
    winrateDiffRange2 = uiConfig.optInt("winrate-diff-range2", 10);
    scoreDiffRange1 = uiConfig.optInt("score-diff-range1", 2);
    scoreDiffRange2 = uiConfig.optInt("score-diff-range2", 5);
    openingEndMove = uiConfig.optInt("opening-end-move", 60);
    middleEndMove = uiConfig.optInt("middle-end-move", 160);
    checkRandomVisits = uiConfig.optBoolean("check-random-visits", false);
    percentsRandomVisits = uiConfig.optDouble("percents-random-visits", 10.0);

    anaGameResignMove = uiConfig.optInt("anagame-resign-move", 2);
    anaGameResignPercent = uiConfig.optDouble("anagame-resign-percent", 5.0);
    anaGameResignStartMove = uiConfig.optInt("anagame-resign-start-move", 150);

    suggestionColorRatio = uiConfig.optInt("suggestion-color-ratio", 2);
    showBestMovesGraph = uiConfig.optBoolean("show-bestmoves-graph", false);
    showBestMovesList = uiConfig.optBoolean("show-bestmoves-list", true);
    uploadUser = uiConfig.optString("up-load-user", "");
    uploadPassWd = uiConfig.optString("up-load-passwd", "");
    shareLabel4 = uiConfig.optString("share-label-4", "");
    shareLabel5 = uiConfig.optString("share-label-5", "");
    sharePublic = uiConfig.optBoolean("share-public", true);
    autoCheckVersion = uiConfig.optBoolean("auto-check-version", true);
    autoCheckDate = uiConfig.optString("auto-check-date", "");
    ignoreVersion = uiConfig.optInt("ignore-version", 0);
    // firstUse = uiConfig.optBoolean("first-time-use", true);
    loadSgfLast = uiConfig.optBoolean("load-sgf-last", false);
    useShortcutKataEstimate = uiConfig.optBoolean("shortcut-kata-estimate", false);
    kataRules = uiConfig.optString("kata-rules", "");
    autoLoadKataRules = uiConfig.optBoolean("auto-load-kata-rules", false);
    showTitleWr = uiConfig.optBoolean("show-title-wr", true);

    chkLzsaiEngineMem = uiConfig.optBoolean("chk-lzsai-enginemem", false);
    autoLoadLzsaiEngineMem = uiConfig.optBoolean("autoload-Lzsai-enginemem", false);
    txtLzsaiEngineMem = uiConfig.optString("txt-lzsai-enginemem", "");
    loadEstimateEngine = uiConfig.optBoolean("load-estimate-engine", false);

    chkLzsaiEngineVisits = uiConfig.optBoolean("chk-lzsai-enginevisits", false);
    autoLoadLzsaiEngineVisits = uiConfig.optBoolean("autoload-Lzsai-enginevisits", false);
    txtLzsaiEngineVisits = uiConfig.optString("txt-lzsai-enginevisits", "");

    chkLzsaiEngineLagbuffer = uiConfig.optBoolean("chk-lzsai-enginelagbuffer", false);
    autoLoadLzsaiEngineLagbuffer = uiConfig.optBoolean("autoload-Lzsai-enginelagbuffer", false);
    txtLzsaiEngineLagbuffer = uiConfig.optString("txt-lzsai-enginelagbuffer", "");

    chkLzsaiEngineResign = uiConfig.optBoolean("chk-lzsai-engineresign", false);
    autoLoadLzsaiEngineResign = uiConfig.optBoolean("autoload-Lzsai-engineresign", false);
    txtLzsaiEngineResign = uiConfig.optString("txt-lzsai-engineresign", "");

    // chkKataEnginePDA  = uiConfig.optBoolean("chk-kata-engine-pda", false);
    txtKataEnginePDA = uiConfig.optString("txt-kata-engine-pda", "");
    autoLoadKataEnginePDA = uiConfig.optBoolean("autoload-kata-engine-pda", false);

    // chkKataEngineRPT  = uiConfig.optBoolean("chk-kata-engine-rpt", false);
    // txtKataEngineRPT = uiConfig.optString("txt-kata-engine-rpt", "");
    //   autoLoadKataEngineRPT = uiConfig.optBoolean("autoload-kata-engine-rpt", false);
    autoLoadTxtKataEnginePDA = uiConfig.optString("auto-load-txt-kata-engine-pda", "");
    autoLoadTxtKataEngineWRN = uiConfig.optString("auto-load-txt-kata-engine-wrn", "");

    //  chkKataEngineWRN  = uiConfig.optBoolean("chk-kata-engine-wrn", false);
    txtKataEngineWRN = uiConfig.optString("txt-kata-engine-wrn", "");
    autoLoadKataEngineWRN = uiConfig.optBoolean("autoload-kata-engine-wrn", false);

    txtKataEngineThreads = uiConfig.optString("txt-kata-engine-threads", "");
    autoLoadKataEngineThreads = uiConfig.optBoolean("autoload-kata-engine-threads", false);

    showWRNInMenu = uiConfig.optBoolean("show-wrn-in-menu", true);
    showPDAInMenu = uiConfig.optBoolean("show-pda-in-menu", true);
    disableWRNInGame = uiConfig.optBoolean("disable-wrn-in-game", true);

    showReplaceFileHint = uiConfig.optBoolean("show-replace-file-hint", true);
    maxTreeWidth = uiConfig.optInt("max-tree-width", 10000);
    gameStatisticsCustomStart = uiConfig.optInt("game-statistics-custom-start", 10);
    gameStatisticsCustomEnd = uiConfig.optInt("game-statistics-custom-end", 200);
    moveListFilterCurrent = uiConfig.optBoolean("move-list-filter-current", false);
    lossPanelSelectWinrate = uiConfig.optBoolean("loss-panel-select-winrate", false);
    analysisAutoQuit = uiConfig.optBoolean("analysis-auto-quit", true);
    firstLoadKataGo = uiConfig.optBoolean("first-load-katago", true);
    txtMoveRankMarkLastMove = uiConfig.optInt("txt-move-rank-mark-last-move", 3);
    moveRankMarkLastMove = uiConfig.optInt("move-rank-mark-last-move", 1);
    disableMoveRankInOrigin = uiConfig.optBoolean("disable-move-rank-in-origin", false);
    logConsoleToFile = uiConfig.optBoolean("log-console-to-file", false);
    logGtpToFile = uiConfig.optBoolean("log-gtp-to-file", false);
    readBoardGetFocus = uiConfig.optBoolean("read-board-get-focus", true);
    useScoreLossInMoveRank = uiConfig.optBoolean("use-score-loss-in-move-rank", true);
    useWinLossInMoveRank = uiConfig.optBoolean("use-win-loss-in-move-rank", true);
    winLossThreshold1 = uiConfig.optDouble("win-loss-threshold-1", -1);
    winLossThreshold2 = uiConfig.optDouble("win-loss-threshold-2", -3);
    winLossThreshold3 = uiConfig.optDouble("win-loss-threshold-3", -6);
    winLossThreshold4 = uiConfig.optDouble("win-loss-threshold-4", -12);
    winLossThreshold5 = uiConfig.optDouble("win-loss-threshold-5", -24);
    scoreLossThreshold1 = uiConfig.optDouble("score-loss-threshold-1", -0.5);
    scoreLossThreshold2 = uiConfig.optDouble("score-loss-threshold-2", -1.5);
    scoreLossThreshold3 = uiConfig.optDouble("score-loss-threshold-3", -3);
    scoreLossThreshold4 = uiConfig.optDouble("score-loss-threshold-4", -6);
    scoreLossThreshold5 = uiConfig.optDouble("score-loss-threshold-5", -12);
    showPonderLimitedTips = uiConfig.optBoolean("show-ponder-limited-tips", true);
    foxAfterGet = uiConfig.optInt("fox-after-get", 0);
    lastFoxName = uiConfig.optString("last-fox-name", "");
    continueWithBestMove = uiConfig.optBoolean("continue-with-best-move", false);
    delayShowCandidates = uiConfig.optBoolean("delay-show-candidates", false);
    delayCandidatesSeconds = uiConfig.optDouble("delay-candidates-seconds", 10.0);
    otherSizeWidth = uiConfig.optInt("other-size-width", 21);
    otherSizeHeight = uiConfig.optInt("other-size-height", 21);
    useFoxStyleCoords = uiConfig.optBoolean("use-fox-style-coords", false);
    useNumCoordsFromTop = uiConfig.optBoolean("use-num-coords-from-top", false);
    useNumCoordsFromBottom = uiConfig.optBoolean("use-num-coords-from-bottom", false);
    showScrollVariation = uiConfig.optBoolean("show-scroll-variation", true);
    ignoreOutOfWidth = uiConfig.optBoolean("ignore-out-of-width", false);
    enginePkPonder = uiConfig.optBoolean("engine-pk-ponder", false);
    alwaysGtp = uiConfig.optBoolean("always-gtp", true);
    noCapture = uiConfig.optBoolean("no-capture", false);
    showrect = uiConfig.optInt("show-move-rect", 1);
    winrateAlwaysBlack = uiConfig.optBoolean("win-rate-always-black", false);

    // chkPkStartNum = uiConfig.optBoolean("chkpk-start-num", false);
    // pkStartNum = uiConfig.optInt("pk-start-num", 1);

    chkDymPDA = uiConfig.optBoolean("chk-dym-pda", false);
    chkStaticPDA = uiConfig.optBoolean("chk-static-pda", false);
    chkAutoPDA = uiConfig.optBoolean("chk-auto-pda", false);
    dymPDACap = uiConfig.optString("dym-pda-cap", "");
    staticPDAcur = uiConfig.optString("static-pda-cur", "");
    AutoPDA = uiConfig.optString("auto-pda", "");

    allowDoubleClick = uiConfig.optBoolean("allow-double-click", true);
    allowDrag = uiConfig.optBoolean("allow-drag", false);
    noRefreshOnSub = uiConfig.optBoolean("no-refresh-on-sub", true);
    useTerritoryInScore = uiConfig.optBoolean("use-territory-in-score", false);

    // chkEngineSgfStart = uiConfig.optBoolean("engine-sgf-start", true);
    engineSgfStartRandom = uiConfig.optBoolean("engine-sgf-random", true);

    estimateArea = uiConfig.optBoolean("estimate-area", false);

    deleteMove = uiConfig.optBoolean("deleteMove", true);
    // showlcbwinrate = config.getJSONObject("leelaz").optBoolean("show-lcb-winrate", false);
    playponder = leelazConfig.optBoolean("play-ponder", true);
    stopAtEmptyBoard = leelazConfig.optBoolean("stop-at-empty-board", false);
    maxGameThinkingTimeSeconds = leelazConfig.optInt("max-game-thinking-time-seconds", 2);

    // showlcbcolor = config.getJSONObject("leelaz").optBoolean("show-lcb-color", false);
    fastChange = leelazConfig.optBoolean("fast-engine-change", true);
    maxAnalyzeTimeMillis = 1000 * leelazConfig.optInt("max-analyze-time-seconds", 600);
    recentFilePaths = getRecentFilePaths();
    //    if (config.getJSONObject("leelaz").optInt("max-analyze-time-minutes", 70) < 60) {
    //      maxAnalyzeTimeMillis =
    //          60 * 1000 * config.getJSONObject("leelaz").optInt("max-analyze-time-minutes", 70);
    //    }
    if (maxAnalyzeTimeMillis == 0) {
      maxAnalyzeTimeMillis = 9999 * 60 * 1000;
    }
    analyzeUpdateIntervalCentisec = leelazConfig.optInt("analyze-update-interval-centisec", 10);
    analyzeUpdateIntervalCentisecSSH =
        leelazConfig.optInt("analyze-update-interval-centisecssh", 10);
    if (theme.fontName() != null) fontName = theme.fontName();
    // else fontName = "微软雅黑";

    if (theme.uiFontName() != null) uiFontName = theme.uiFontName();
    // else uiFontName = "微软雅黑";

    if (theme.winrateFontName() != null) winrateFontName = theme.winrateFontName();
    readThemeVaule(true);
  }

  private ArrayList<String> getRecentFilePaths() {
    ArrayList<String> paths = new ArrayList<>();
    JSONArray filePaths = uiConfig.optJSONArray("recent-file-paths");
    if (filePaths != null)
      for (int i = 0; i < filePaths.length(); i++) {
        JSONObject pathJson = filePaths.getJSONObject(i);
        String path = pathJson.optString("path");
        paths.add(path);
      }
    return paths;
  }

  public void saveRecentFilePaths(String recentPath) {
    for (int i = 0; i < recentFilePaths.size(); i++) {
      if (recentFilePaths.get(i).equals(recentPath)) {
        recentFilePaths.remove(i);
      }
    }
    if (recentFilePaths.size() < 5) recentFilePaths.add(recentPath);
    else {
      recentFilePaths.remove(0);
      recentFilePaths.add(recentPath);
    }
    JSONArray filePaths = new JSONArray();
    for (int i = 0; i < recentFilePaths.size(); i++) {
      JSONObject pathJson = new JSONObject();
      pathJson.put("path", recentFilePaths.get(i));
      filePaths.put(pathJson);
    }
    Lizzie.config.uiConfig.put("recent-file-paths", filePaths);
  }

  public void readThemeVaule(boolean first) {
    if (!first) {
      theme = new Theme();
      theme.getTheme(uiConfig);
    }
    int oriShadowSize = shadowSize;
    boolean oriShowStoneShadow = showStoneShadow;
    boolean oriUsePureBackground = usePureBackground;
    boolean oriUsePureBoard = usePureBoard;
    boolean oriUsePureStone = usePureStone;
    Color oriPureBackgroundColor = pureBackgroundColor;
    Color oriPureBoardColor = pureBoardColor;
    winrateStrokeWidth = theme.winrateStrokeWidth();
    minimumBlunderBarWidth = theme.minimumBlunderBarWidth();
    shadowSize = theme.shadowSize();
    commentFontSize = theme.commentFontSize();
    commentFontColor = theme.commentFontColor();
    commentBackgroundColor = theme.commentBackgroundColor();
    winrateLineColor = theme.winrateLineColor();
    scoreMeanLineColor = theme.scoreMeanLineColor();
    bestMoveColorC = theme.bestMoveColor();
    stoneIndicatorType = theme.stoneIndicatorType();
    bestMoveColor =
        Color.RGBtoHSB(
            bestMoveColorC.getRed(), bestMoveColorC.getGreen(), bestMoveColorC.getBlue(), null)[0];
    bestColor = reverseColor(bestMoveColorC);
    winrateMissLineColor = theme.winrateMissLineColor();
    blunderBarColor = theme.blunderBarColor();
    showCommentNodeColor = theme.showCommentNodeColor(true);
    showStoneShadow = theme.showStoneShadow(true);
    commentNodeColor = theme.commentNodeColor();
    blunderWinrateThresholds = theme.blunderWinrateThresholds();
    blunderNodeColors = theme.blunderNodeColors();
    useScoreDiffInVariationTree = theme.useScoreDiffInVariationTree(true);
    scoreDiffInVariationTreeFactor = theme.scoreDiffInVariationTreeFactor(true);
    usePureBackground = theme.usePureBackground(true);
    pureBackgroundColor = theme.pureBackgroundColor();
    if (usePureBackground && !first) Lizzie.frame.setBackgroundColor(pureBackgroundColor);
    usePureBoard = theme.usePureBoard(true);
    pureBoardColor = theme.pureBoardColor();
    usePureStone = theme.usePureStone(true);
    String oriBlackStoneImageString = blackStoneImageString;
    String oriWhiteStoneImageString = whiteStoneImageString;
    String oriBoardImageString = boardImageString;
    String oriBackgroundImageString = backgroundImageString;
    blackStoneImageString = theme.blackStoneImageString();
    whiteStoneImageString = theme.whiteStoneImageString();
    boardImageString = theme.boardImageString();
    backgroundImageString = theme.backgroundImageString();

    if (!first
        && (!blackStoneImageString.equals(oriBlackStoneImageString)
            || !whiteStoneImageString.equals(oriWhiteStoneImageString))) {
      LizzieFrame.boardRenderer.reCreateStoneImageAnyway();
      if (Lizzie.frame.independentMainBoard != null)
        Lizzie.frame.independentMainBoard.boardRenderer.reCreateStoneImageAnyway();
      if (LizzieFrame.boardRenderer2 != null) LizzieFrame.boardRenderer2.reCreateStoneImageAnyway();
    } else if (!first
        && (oriShadowSize != shadowSize
            || oriShowStoneShadow != showStoneShadow
            || oriUsePureStone != usePureStone)) {
      LizzieFrame.boardRenderer.reDrawStoneAnyway();
      if (Lizzie.frame.independentMainBoard != null)
        Lizzie.frame.independentMainBoard.boardRenderer.reDrawStoneAnyway();
      if (LizzieFrame.boardRenderer2 != null) LizzieFrame.boardRenderer2.reDrawStoneAnyway();
    }
    if (!first
        && (oriUsePureBoard != usePureBoard
            || oriPureBoardColor != pureBoardColor
            || !oriBoardImageString.equals(boardImageString))) {
      LizzieFrame.boardRenderer.reDrawGobanAnyway();
      LizzieFrame.subBoardRenderer.reDrawGobanAnyway();
      if (LizzieFrame.boardRenderer2 != null) LizzieFrame.boardRenderer2.reDrawGobanAnyway();
      if (Lizzie.frame.independentMainBoard != null)
        Lizzie.frame.independentMainBoard.boardRenderer.reDrawGobanAnyway();
      if (Lizzie.frame.independentSubBoard != null)
        Lizzie.frame.independentSubBoard.subBoardRenderer.reDrawGobanAnyway();
    }
    if (!first
        && (oriUsePureBackground != usePureBackground
            || oriPureBackgroundColor != pureBoardColor
            || !oriBackgroundImageString.equals(backgroundImageString))) {
      Lizzie.frame.redrawBackgroundAnyway = true;
      LizzieFrame.boardRenderer.reDrawBackgroundAnyway();
      if (LizzieFrame.boardRenderer2 != null) LizzieFrame.boardRenderer2.reDrawBackgroundAnyway();
      LizzieFrame.subBoardRenderer.reDrawBackgroundAnyway();
    }
  }

  // Modifies config by adding in values from default_config that are missing.
  // Returns whether it added anything.
  public boolean mergeDefaults(JSONObject config, JSONObject defaultsConfig) {
    boolean modified = false;
    Iterator<String> keys = defaultsConfig.keys();
    while (keys.hasNext()) {
      String key = keys.next();
      Object newVal = defaultsConfig.get(key);
      if (newVal instanceof JSONObject) {
        if (!config.has(key)) {
          config.put(key, new JSONObject());
          modified = true;
        }
        Object oldVal = config.get(key);
        modified |= mergeDefaults((JSONObject) oldVal, (JSONObject) newVal);
      } else {
        if (!config.has(key)) {
          config.put(key, newVal);
          modified = true;
        }
      }
    }
    return modified;
  }

  public void setClassicMode(boolean status) {
    isClassicMode = status;
    uiConfig.put("is-classic-mode", isClassicMode);
    try {
      save();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  public int getMySaveTime() {
    switch (mySaveTime) {
      case 0:
        return -1;
      case 1:
        return 5;
      case 2:
        return 10;
      case 3:
        return 20;
      case 4:
        return 30;
      case 5:
        return 60;
      case 6:
        return 120;
      case 7:
        return 180;
      default:
        return -1;
    }
  }

  public int getMyByoyomiSeconds() {
    switch (myByoyomiSeconds) {
      case 0:
        return -1;
      case 1:
        return 10;
      case 2:
        return 15;
      case 3:
        return 20;
      case 4:
        return 30;
      case 5:
        return 40;
      case 6:
        return 60;
      case 7:
        return 120;
      default:
        return -1;
    }
  }

  public int getMyByoyomiTimes() {
    switch (myByoyomiTimes) {
      case 0:
        return 1;
      case 1:
        return 2;
      case 2:
        return 3;
      case 3:
        return 5;
      case 4:
        return 10;
      case 5:
        return 20;
      default:
        return 1;
    }
  }

  public void setTxtMoveRankMarkLastMove(int value) {
    txtMoveRankMarkLastMove = value;
    uiConfig.put("txt-move-rank-mark-last-move", txtMoveRankMarkLastMove);
  }

  public void setShowRankMark(boolean showAll, boolean showLast, int showLimit) {
    if (showAll) moveRankMarkLastMove = 0;
    else if (showLast) moveRankMarkLastMove = 1;
    else {
      moveRankMarkLastMove = showLimit;
    }
    uiConfig.put("move-rank-mark-last-move", moveRankMarkLastMove);
    LizzieFrame.menu.setBtnRankMark();
  }

  public void toggleDisableMoveRankInOrigin() {
    disableMoveRankInOrigin = !disableMoveRankInOrigin;
    uiConfig.put("disable-move-rank-in-origin", disableMoveRankInOrigin);
  }

  public void toggleShowMoveRankMark() {
    if (moveRankMarkLastMove < 0) moveRankMarkLastMove = 1;
    else if (moveRankMarkLastMove == 1) {
      if (txtMoveRankMarkLastMove > 1) moveRankMarkLastMove = txtMoveRankMarkLastMove;
      else moveRankMarkLastMove = 0;
    } else if (txtMoveRankMarkLastMove > 1 && moveRankMarkLastMove == txtMoveRankMarkLastMove) {
      moveRankMarkLastMove = 0;
    } else if (moveRankMarkLastMove == 0) moveRankMarkLastMove = -1;
    if (moveRankMarkLastMove > 0) Lizzie.config.hiddenMoveNumber();
    uiConfig.put("move-rank-mark-last-move", moveRankMarkLastMove);
    LizzieFrame.menu.setBtnRankMark();
  }

  public void toggleExtraMode(int mode) {
    ExtraMode previousMode = extraMode;
    extraMode = getExtraMode(mode);
    Lizzie.frame.extraMode(extraMode, previousMode);
    uiConfig.put("extra-mode", getExtraModeValue(extraMode));
  }

  public void toggleappendWinrateToComment() {
    this.appendWinrateToComment = !appendWinrateToComment;
    uiConfig.put("append-winrate-to-comment", appendWinrateToComment);
  }

  public void toggleShowMoveAllInBranch() {
    showMoveAllInBranch = !showMoveAllInBranch;
    uiConfig.put("show-moveall-inbranch", showMoveAllInBranch);
  }

  public void hiddenMoveNumber() {
    allowMoveNumber = 0;
    uiConfig.put("allow-move-number", allowMoveNumber);
  }

  public void toggleShowMoveNumber() {
    onlyLastMoveNumber = 1;
    if (EngineManager.isEngineGame && EngineManager.engineGameInfo.isGenmove) {
      allowMoveNumber = (allowMoveNumber == -1 ? onlyLastMoveNumber : -1);
    } else {
      if (this.onlyLastMoveNumber > 0) {
        allowMoveNumber =
            (allowMoveNumber == -1 ? onlyLastMoveNumber : (allowMoveNumber == 0 ? -1 : 0));
      } else {
        allowMoveNumber = (allowMoveNumber == 0 ? -1 : 0);
      }
    }
    uiConfig.put("allow-move-number", allowMoveNumber);
    LizzieFrame.menu.setBtnRankMark();
  }

  //  public void toggleNodeColorMode() {
  //    this.nodeColorMode = this.nodeColorMode > 1 ? 0 : this.nodeColorMode + 1;
  //  }

  //  public void toggleShowBranch() {
  //    this.showBranch = !this.showBranch;
  //  }

  public void toggleShowWinrate() {
    this.showWinrateGraph = !this.showWinrateGraph;
    uiConfig.put("show-winrate-graph", showWinrateGraph);
    if (extraMode == ExtraMode.Min && showWinrateGraph) toggleExtraMode(0);
    Lizzie.frame.refreshContainer();
    Lizzie.frame.refresh();
  }

  public boolean showListPane() {
    if (showListPane
        && (extraMode == ExtraMode.Float_Board
            || extraMode == ExtraMode.Thinking
            || extraMode == ExtraMode.Four_Sub)) return true;
    if (showListPane
        && (extraMode == ExtraMode.Normal || extraMode == ExtraMode.Min)
        && !this.largeWinrateGraph) return true;
    else return false;
  }

  public void toggleShowListPane() {
    if (showListPane()) showListPane = false;
    else showListPane = true;
    if (showListPane
        && extraMode != ExtraMode.Float_Board
        && extraMode != ExtraMode.Thinking
        && extraMode != ExtraMode.Four_Sub) {
      if (extraMode != ExtraMode.Normal && extraMode != ExtraMode.Min) Lizzie.frame.defaultMode();
      if (largeWinrateGraph) this.largeWinrateGraph = false;
      uiConfig.put("large-winrate-graph", largeWinrateGraph);
    }
    Lizzie.frame.setHideListScrollpane(showListPane);
    uiConfig.put("show-list-pane", showListPane);
    if (extraMode == ExtraMode.Min && showListPane) toggleExtraMode(0);
    Lizzie.frame.refreshContainer();
    Lizzie.frame.refresh();
  }

  public void toggleLargeWinrate() {
    if (!this.largeWinrateGraph && this.largeSubBoard) {
      toggleLargeSubBoard();
      this.largeWinrateGraph = true;
    } else {
      this.largeWinrateGraph = !this.largeWinrateGraph;
    }
    if (showListPane && !largeWinrateGraph && extraMode == ExtraMode.Normal) {
      Lizzie.frame.setHideListScrollpane(showListPane);
    } else if (extraMode != ExtraMode.Float_Board) {
      Lizzie.frame.setHideListScrollpane(false);
    }
    if (largeWinrateGraph) {
      if (showVariationGraph && showSubBoard) {
        showSubBoard = !showSubBoard;
        uiConfig.put("show-subboard", showSubBoard);
        hideSubBoardFromLargeWinrate = true;
        uiConfig.put("hide-subboard-from-large-winrate", hideSubBoardFromLargeWinrate);
      }
    } else if (hideSubBoardFromLargeWinrate && !showSubBoard) {
      showSubBoard = !showSubBoard;
      uiConfig.put("show-subboard", showSubBoard);
      hideSubBoardFromLargeWinrate = false;
      uiConfig.put("hide-subboard-from-large-winrate", hideSubBoardFromLargeWinrate);
    }
    uiConfig.put("large-winrate-graph", largeWinrateGraph);
    Lizzie.frame.refreshContainer();
    Lizzie.frame.refresh();
  }

  public void toggleLargeSubBoard() {
    if (this.largeWinrateGraph) toggleLargeWinrate();
    if (!this.showSubBoard) {
      toggleShowSubBoard();
      this.largeSubBoard = true;
    } else this.largeSubBoard = !this.largeSubBoard;
    Lizzie.frame.redrawBackgroundAnyway = true;
    uiConfig.put("large-subboard", largeSubBoard);
    LizzieFrame.subBoardRenderer.isMouseOver = false;
    Lizzie.frame.refreshContainer();
    Lizzie.frame.refresh();
  }

  public void toggleShowVariationGraph() {
    this.showVariationGraph = !this.showVariationGraph;
    uiConfig.put("show-variation-graph", showVariationGraph);
    try {
      Lizzie.config.save();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    Lizzie.frame.setVarTreeVisible(showVariationGraph);
    if (extraMode == ExtraMode.Min && showVariationGraph) toggleExtraMode(0);
    Lizzie.frame.refreshContainer();
    Lizzie.frame.refresh();
  }

  public void toggleShowCaptured() {
    this.showCaptured = !this.showCaptured;
    uiConfig.put("show-captured", showCaptured);
    if (extraMode == ExtraMode.Min && showCaptured) toggleExtraMode(0);
    Lizzie.frame.refreshContainer();
    Lizzie.frame.refresh();
  }

  public void toggleShowComment() {
    this.showComment = !this.showComment;
    uiConfig.put("show-comment", showComment);
    if (showComment) Lizzie.frame.setCommentPaneContent();
    else {
      Lizzie.frame.commentScrollPane.setVisible(false);
      Lizzie.frame.blunderContentPane.setVisible(false);
    }
    Lizzie.frame.commentEditPane.setVisible(false);
    Lizzie.frame.refreshContainer();
    Lizzie.frame.refresh();
    if (extraMode == ExtraMode.Min && showComment) toggleExtraMode(0);
  }

  public void toggleShowStatus() {
    //    if (!fromMode && !changedStatus) {
    //      changedStatus = true;
    //      uiConfig.put("changed-status", true);
    //    }
    if (Lizzie.readMode) {
      this.showStatus = false;
      return;
    }
    this.showStatus = !this.showStatus;
    if (showStatus) setClassicMode(false);
    uiConfig.put("show-status", showStatus);
    Lizzie.frame.refreshContainer();
    Lizzie.frame.refresh();
  }

  public void toggleShowCommentNodeColor() {
    this.showCommentNodeColor = !this.showCommentNodeColor;
  }

  // public void toggleShowBestMoves() {
  // this.showBestMoves = !this.showBestMoves;
  // }

  public void toggleShowNextMoves() {
    if (showNextMoves && showNextMoveBlunder) this.showNextMoveBlunder = false;
    else if (showNextMoves && !showNextMoveBlunder) showNextMoves = false;
    else if (!showNextMoves) {
      showNextMoves = true;
      showNextMoveBlunder = true;
    }
    uiConfig.put("show-next-move-blunder", showNextMoveBlunder);
    uiConfig.put("show-next-moves", showNextMoves);
  }

  public void setShowNextMoves(boolean showNext, boolean showBlunder) {
    showNextMoves = showNext;
    showNextMoveBlunder = showBlunder;
    uiConfig.put("show-next-move-blunder", showNextMoveBlunder);
    uiConfig.put("show-next-moves", showNextMoves);
    Lizzie.frame.refresh();
  }
  // public void toggleHandicapInsteadOfWinrate() {
  // this.handicapInsteadOfWinrate = !this.handicapInsteadOfWinrate;
  // }

  public void toggleCoordinates() {
    showCoordinates = !showCoordinates;
    uiConfig.put("show-coordinates", showCoordinates);
  }

  public void toggleShowSubBoard() {
    showSubBoard = !showSubBoard;
    Lizzie.frame.refreshContainer();
    Lizzie.frame.refresh();
    uiConfig.put("show-subboard", showSubBoard);
    if (extraMode == ExtraMode.Min && showSubBoard) toggleExtraMode(0);
  }

  public void toggleShowSuggestionVariations() {
    showSuggestionVariations = !showSuggestionVariations;
    uiConfig.put("show-suggestion-variations", showSuggestionVariations);
  }

  public void toggleEvaluationColoring() {
    if (leelaversion < 17) {
      return;
    }
    showlcbcolor = !showlcbcolor;
  }

  public boolean showLargeSubBoard() {
    return showSubBoard && largeSubBoard;
  }

  public boolean showLargeWinrate() {
    return showWinrateGraph && largeWinrateGraph;
  }

  public boolean showLargeWinrateOnly() {
    return largeWinrateGraph;
  }

  public boolean showBestMovesNow() {
    return showBestMoves || showBestMovesTemporarily;
  }

  public boolean showBranchNow() {
    return showBranch || showBestMovesTemporarily;
  }

  public void saveBlunderTableSortSettings(int sortNum, boolean isSorted, boolean isOriginOrder) {
    if (blunderTabelOnlyAfter) {
      blunderSortNumAF = sortNum;
      blunderIsSortedAF = isSorted;
      blunderSortIsOriginOrderAF = isOriginOrder;
      uiConfig.put("blunder-sort-num-AF", blunderSortNumAF);
      uiConfig.put("blunder-is-sorted-AF", blunderIsSortedAF);
      uiConfig.put("blunder-sort-is-origin-order-AF", blunderSortIsOriginOrderAF);
    } else {
      blunderSortNumNAF = sortNum;
      blunderIsSortedNAF = isSorted;
      blunderSortIsOriginOrderNAF = isOriginOrder;
      uiConfig.put("blunder-sort-num-NAF", blunderSortNumNAF);
      uiConfig.put("blunder-is-sorted-NAF", blunderIsSortedNAF);
      uiConfig.put("blunder-sort-is-origin-order-NAF", blunderSortIsOriginOrderNAF);
    }
  }

  /**
   * Scans the current directory as well as the current PATH to find a reasonable default leelaz
   * binary.
   *
   * @return A working path to a leelaz binary. If there are none on the PATH, "./leelaz" is
   *     returned for backwards compatibility.
   */
  //  public static String getBestDefaultLeelazPath() {
  //    List<String> potentialPaths = new ArrayList<>();
  //    potentialPaths.add(".");
  //    potentialPaths.addAll(Arrays.asList(System.getenv("PATH").split(":")));
  //
  //    for (String potentialPath : potentialPaths) {
  //      for (String potentialExtension : Arrays.asList(new String[] {"", ".exe"})) {
  //        File potentialLeelaz = new File(potentialPath, "leelaz" + potentialExtension);
  //        if (potentialLeelaz.exists() && potentialLeelaz.canExecute()) {
  //          return potentialLeelaz.getPath();
  //        }
  //      }
  //    }
  //
  //    return "./leelaz";
  //  }

  private JSONObject createDefaultConfig() {
    JSONObject config = new JSONObject();

    // About engine parameter
    JSONObject leelaz = new JSONObject();
    //    leelaz.put("network-file", "network.gz");
    //    leelaz.put(
    //        "engine-command",
    //        String.format(
    //            "%s --gtp --lagbuffer 0 --weights %%network-file", getBestDefaultLeelazPath()));
    //  leelaz.put("engine-start-location", ".");
    // leelaz.put("max-analyze-time-minutes", 10);
    leelaz.put("limit-max-suggestion", 10);
    leelaz.put("limit-branch-length", 0);
    // leelaz.put("badmoves-winrate-limits", 0);
    // leelaz.put("badmoves-playouts-limits", 0);
    leelaz.put("max-game-thinking-time-seconds", 2);
    leelaz.put("analyze-update-interval-centisec", 10);
    //    leelaz.put("show-lcb-winrate", false);
    //    leelaz.put("show-lcb-color", false);
    //  leelaz.put("leela-version", 17);
    config.put("leelaz", leelaz);

    // About User Interface display
    JSONObject ui = new JSONObject();

    ui.put("shadows-enabled", true);

    ui.put("shadow-size", 85);
    ui.put("show-move-number", false);
    ui.put("show-status", true);
    ui.put("show-leelaz-variation", true);
    ui.put("show-winrate-graph", true);
    ui.put("large-winrate-graph", false);
    ui.put("winrate-stroke-width", 1.7);
    ui.put("show-blunder-bar", true);
    ui.put("minimum-blunder-bar-width", 3);
    ui.put("weighted-blunder-bar-height", false);
    // ui.put("dynamic-winrate-graph-width", true);
    ui.put("show-comment", true);
    ui.put("comment-font-size", 0);
    ui.put("show-variation-graph", true);
    ui.put("show-captured", true);
    ui.put("show-best-moves", true);
    ui.put("show-coordinates", true);
    ui.put("show-next-moves", true);
    ui.put("show-subboard", true);
    ui.put("large-subboard", false);
    ui.put("win-rate-always-black", false);
    ui.put("confirm-exit", false);
    ui.put("resume-previous-game", false);
    ui.put("autosave-interval-seconds", -1);
    ui.put("handicap-instead-of-winrate", false);
    ui.put("board-size", 19);
    ui.put("show-dynamic-komi", false);
    // ui.put("min-playout-ratio-for-stats", 0.0);
    ui.put("theme", "default");
    ui.put("only-last-move-number", 1);
    ui.put("new-move-number-in-branch", true);
    ui.put("append-winrate-to-comment", true);
    ui.put("replay-branch-interval-seconds", 0.9);
    //   ui.put("gtp-console-style", defaultGtpConsoleStyle);
    config.put("ui", ui);
    return config;
  }

  private JSONObject createSaveBoardConfig() {
    JSONObject config = new JSONObject();

    // About engine parameter
    JSONObject saveNumber = new JSONObject();
    saveNumber.put("save-config", true);

    config.put("save", saveNumber);

    return config;
  }

  private JSONObject createPersistConfig() {
    JSONObject config = new JSONObject();

    // About engine parameter
    JSONObject filesys = new JSONObject();
    filesys.put("last-folder", "");

    // config.put("filesystem", filesys);

    // About autosave
    config.put("autosave", "");

    // About User Interface display
    JSONObject ui = new JSONObject();

    // ui.put("window-height", 657);
    // ui.put("window-width", 687);
    // ui.put("max-alpha", 240);

    // Main Window Position & Size
    ui.put("main-window-position", new JSONArray("[]"));
    ui.put("gtp-console-position", new JSONArray("[]"));
    ui.put("window-maximized", false);
    ui.put("gtp-console-opened", true);

    config.put("filesystem", filesys);

    // Avoid the key "ui" because it was used to distinguish "config" and "persist"
    // in old version of validateAndCorrectSettings().
    // If we use "ui" here, we will have trouble to run old lizzie.
    config.put("ui-persist", ui);
    return config;
  }

  private void writeConfig(JSONObject config, File file) throws IOException, JSONException {
    file.createNewFile();
    FileOutputStream fp = new FileOutputStream(file);
    OutputStreamWriter writer = new OutputStreamWriter(fp, "utf-8");
    writer.write(config.toString(2));
    writer.close();
    fp.close();
  }

  public void persist() throws IOException {
    boolean windowIsMaximized = Lizzie.frame.getExtendedState() == JFrame.MAXIMIZED_BOTH;
    persistedUi.put("gtp-console-opened", Lizzie.gtpConsole.isVisible());
    JSONArray mainPos = new JSONArray();
    JSONArray mainOhter = new JSONArray();
    JSONArray mainList = new JSONArray();
    JSONArray mainBlunder = new JSONArray();
    int list1 = Lizzie.frame.listTable.getColumnModel().getColumn(0).getWidth();
    int list2 = Lizzie.frame.listTable.getColumnModel().getColumn(2).getWidth();
    int list3 = Lizzie.frame.listTable.getColumnModel().getColumn(3).getWidth();
    int list4 = Lizzie.frame.listTable.getColumnModel().getColumn(4).getWidth();
    int length = Lizzie.frame.listTable.getColumnModel().getColumn(5).getWidth();
    if (length > 0) {
      mainList.put(list1);
      mainList.put(list2);
      mainList.put(list3);
      mainList.put(list4);
      mainList.put(length);
    }
    int blunder1 = Lizzie.frame.blunderTabelBlack.getColumnModel().getColumn(0).getWidth();
    int blunder2 = Lizzie.frame.blunderTabelBlack.getColumnModel().getColumn(2).getWidth();
    int length2 = Lizzie.frame.blunderTabelBlack.getColumnModel().getColumn(3).getWidth();
    if (length2 > 0) {
      mainBlunder.put(blunder1);
      mainBlunder.put(blunder2);
      mainBlunder.put(length2);
    }

    if (!windowIsMaximized) {
      mainPos.put(Lizzie.frame.getX());
      mainPos.put(Lizzie.frame.getY());
      mainPos.put(Lizzie.frame.getWidth());
      mainPos.put(Lizzie.frame.getHeight());
    } else {
      if (Lizzie.frame.noneMaxX > 0
          && Lizzie.frame.noneMaxY > 0
          && Lizzie.frame.noneMaxWidth > 0
          && Lizzie.frame.noneMaxHeight > 0) {
        mainPos.put(Lizzie.frame.noneMaxX);
        mainPos.put(Lizzie.frame.noneMaxY);
        mainPos.put(Lizzie.frame.noneMaxWidth);
        mainPos.put(Lizzie.frame.noneMaxHeight);
      } else if (persistedUi.optJSONArray("main-window-position") != null
          && persistedUi.optJSONArray("main-window-position").length() == 4) {
        JSONArray pos = persistedUi.getJSONArray("main-window-position");
        mainPos.put(pos.get(0));
        mainPos.put(pos.get(1));
        mainPos.put(pos.get(2));
        mainPos.put(pos.get(3));
      }
    }

    mainOhter.put(Lizzie.frame.toolbarHeight);
    mainOhter.put(Lizzie.frame.bowserX);
    mainOhter.put(Lizzie.frame.bowserY);
    mainOhter.put(Lizzie.frame.bowserWidth);
    mainOhter.put(Lizzie.frame.bowserHeight);

    persistedUi.put("main-window-position", mainPos);
    persistedUi.put("main-window-other", mainOhter);
    persistedUi.put("main-window-list", mainList);
    persistedUi.put("main-window-blunder", mainBlunder);

    JSONArray gtpPos = new JSONArray();
    gtpPos.put(Lizzie.gtpConsole.getX());
    gtpPos.put(Lizzie.gtpConsole.getY());
    gtpPos.put(Lizzie.gtpConsole.getWidth());
    gtpPos.put(Lizzie.gtpConsole.getHeight());
    persistedUi.put("gtp-console-position", gtpPos);
    persistedUi.put("board-postion-propotion", Lizzie.frame.BoardPositionProportion);
    persistedUi.put("window-maximized", windowIsMaximized);

    if (Lizzie.frame.analysisFrame != null) {
      JSONArray suggestionlistPos = new JSONArray();
      if (Lizzie.frame.analysisFrame.table.getColumnCount() == 7) {
        suggestionlistPos.put(Lizzie.frame.analysisFrame.getX());
        suggestionlistPos.put(Lizzie.frame.analysisFrame.getY());
        suggestionlistPos.put(Lizzie.frame.analysisFrame.getWidth());
        suggestionlistPos.put(Lizzie.frame.analysisFrame.getHeight());
        suggestionlistPos.put(
            Lizzie.frame.analysisFrame.table.getColumnModel().getColumn(0).getWidth());
        suggestionlistPos.put(
            Lizzie.frame.analysisFrame.table.getColumnModel().getColumn(1).getWidth());
        suggestionlistPos.put(
            Lizzie.frame.analysisFrame.table.getColumnModel().getColumn(2).getWidth());
        suggestionlistPos.put(
            Lizzie.frame.analysisFrame.table.getColumnModel().getColumn(3).getWidth());
        suggestionlistPos.put(
            Lizzie.frame.analysisFrame.table.getColumnModel().getColumn(4).getWidth());
        suggestionlistPos.put(
            Lizzie.frame.analysisFrame.table.getColumnModel().getColumn(5).getWidth());
        suggestionlistPos.put(
            Lizzie.frame.analysisFrame.table.getColumnModel().getColumn(6).getWidth());
        persistedUi.put("suggestions-list-position-7", suggestionlistPos);
        boolean persisted = Lizzie.config.persistedUi != null;
        if (persisted
            && Lizzie.config.persistedUi.optJSONArray("suggestions-list-position") != null) {
          JSONArray pos = Lizzie.config.persistedUi.getJSONArray("suggestions-list-position-9");
          persistedUi.put("suggestions-list-position-9", pos);
        }
      }
      if (Lizzie.frame.analysisFrame.table.getColumnCount() == 9) {
        suggestionlistPos.put(Lizzie.frame.analysisFrame.getX());
        suggestionlistPos.put(Lizzie.frame.analysisFrame.getY());
        suggestionlistPos.put(Lizzie.frame.analysisFrame.getWidth());
        suggestionlistPos.put(Lizzie.frame.analysisFrame.getHeight());
        suggestionlistPos.put(
            Lizzie.frame.analysisFrame.table.getColumnModel().getColumn(0).getWidth());
        suggestionlistPos.put(
            Lizzie.frame.analysisFrame.table.getColumnModel().getColumn(1).getWidth());
        suggestionlistPos.put(
            Lizzie.frame.analysisFrame.table.getColumnModel().getColumn(2).getWidth());
        suggestionlistPos.put(
            Lizzie.frame.analysisFrame.table.getColumnModel().getColumn(3).getWidth());
        suggestionlistPos.put(
            Lizzie.frame.analysisFrame.table.getColumnModel().getColumn(4).getWidth());
        suggestionlistPos.put(
            Lizzie.frame.analysisFrame.table.getColumnModel().getColumn(5).getWidth());
        suggestionlistPos.put(
            Lizzie.frame.analysisFrame.table.getColumnModel().getColumn(6).getWidth());
        suggestionlistPos.put(
            Lizzie.frame.analysisFrame.table.getColumnModel().getColumn(7).getWidth());
        suggestionlistPos.put(
            Lizzie.frame.analysisFrame.table.getColumnModel().getColumn(8).getWidth());
        persistedUi.put("suggestions-list-position-9", suggestionlistPos);
        boolean persisted = Lizzie.config.persistedUi != null;
        if (persisted
            && Lizzie.config.persistedUi.optJSONArray("suggestions-list-position") != null) {
          JSONArray pos = Lizzie.config.persistedUi.getJSONArray("suggestions-list-position-7");
          persistedUi.put("suggestions-list-position-7", pos);
        }
      }
    } else {
      boolean persisted = Lizzie.config.persistedUi != null;
      if (persisted
          && Lizzie.config.persistedUi.optJSONArray("suggestions-list-position") != null) {
        JSONArray pos = Lizzie.config.persistedUi.getJSONArray("suggestions-list-position-7");
        persistedUi.put("suggestions-list-position-7", pos);
        JSONArray pos2 = Lizzie.config.persistedUi.getJSONArray("suggestions-list-position-9");
        persistedUi.put("suggestions-list-position-9", pos2);
      }
    }
    if (Lizzie.frame.search != null) {
      JSONArray searchPos = new JSONArray();

      searchPos.put(Lizzie.frame.search.getX());
      searchPos.put(Lizzie.frame.search.getY());
      searchPos.put(Lizzie.frame.search.getWidth());
      searchPos.put(Lizzie.frame.search.getHeight());

      persistedUi.put("public-kifu-search", searchPos);
    }
    if (Lizzie.frame.moveListFrame != null) {
      JSONArray badmoveslistPos = new JSONArray();

      badmoveslistPos.put(Lizzie.frame.moveListFrame.sortnum);
      badmoveslistPos.put(Lizzie.frame.moveListFrame.getX());
      badmoveslistPos.put(Lizzie.frame.moveListFrame.getY());
      badmoveslistPos.put(Lizzie.frame.moveListFrame.getWidth());
      badmoveslistPos.put(Lizzie.frame.moveListFrame.getHeight());
      badmoveslistPos.put(
          Lizzie.frame.moveListFrame.table.getColumnModel().getColumn(0).getWidth());
      badmoveslistPos.put(
          Lizzie.frame.moveListFrame.table.getColumnModel().getColumn(1).getWidth());
      badmoveslistPos.put(
          Lizzie.frame.moveListFrame.table.getColumnModel().getColumn(2).getWidth());
      badmoveslistPos.put(
          Lizzie.frame.moveListFrame.table.getColumnModel().getColumn(3).getWidth());
      badmoveslistPos.put(
          Lizzie.frame.moveListFrame.table.getColumnModel().getColumn(4).getWidth());
      badmoveslistPos.put(
          Lizzie.frame.moveListFrame.table.getColumnModel().getColumn(5).getWidth());
      badmoveslistPos.put(
          Lizzie.frame.moveListFrame.table.getColumnModel().getColumn(6).getWidth());
      badmoveslistPos.put(
          Lizzie.frame.moveListFrame.table.getColumnModel().getColumn(7).getWidth());
      badmoveslistPos.put(
          Lizzie.frame.moveListFrame.table.getColumnModel().getColumn(8).getWidth());
      if (Lizzie.frame.moveListFrame.table.getColumnCount() == 12) {
        badmoveslistPos.put(
            Lizzie.frame.moveListFrame.table.getColumnModel().getColumn(9).getWidth());
        badmoveslistPos.put(
            Lizzie.frame.moveListFrame.table.getColumnModel().getColumn(10).getWidth());
        badmoveslistPos.put(
            Lizzie.frame.moveListFrame.table.getColumnModel().getColumn(11).getWidth());
      }
      persistedUi.put("badmoves-list-position", badmoveslistPos);
    } else {
      boolean persisted = Lizzie.config.persistedUi != null;
      if (persisted && Lizzie.config.persistedUi.optJSONArray("badmoves-list-position") != null) {
        JSONArray pos = Lizzie.config.persistedUi.getJSONArray("badmoves-list-position");
        persistedUi.put("badmoves-list-position", pos);
      }
    }

    JSONArray toolbarParameter = new JSONArray();
    try {
      toolbarParameter.put(Integer.parseInt(LizzieFrame.toolbar.txtFirstAnaMove.getText()));
    } catch (NumberFormatException err) {
      toolbarParameter.put(-1);
    }
    try {
      toolbarParameter.put(Integer.parseInt(LizzieFrame.toolbar.txtLastAnaMove.getText()));
    } catch (NumberFormatException err) {
      toolbarParameter.put(-1);
    }
    if (LizzieFrame.toolbar.chkAnaTime.isSelected()) toolbarParameter.put(1);
    else toolbarParameter.put(-1);
    try {
      toolbarParameter.put(Integer.parseInt(LizzieFrame.toolbar.txtAnaTime.getText()));
    } catch (NumberFormatException err) {
      toolbarParameter.put(-1);
    }
    if (LizzieFrame.toolbar.chkAnaAutoSave.isSelected()) toolbarParameter.put(1);
    else toolbarParameter.put(-1);
    if (LizzieFrame.toolbar.chkAnaPlayouts.isSelected()) toolbarParameter.put(1);
    else toolbarParameter.put(-1);
    try {
      toolbarParameter.put(Integer.parseInt(LizzieFrame.toolbar.txtAnaPlayouts.getText()));
    } catch (NumberFormatException err) {
      toolbarParameter.put(-1);
    }
    if (LizzieFrame.toolbar.chkAnaFirstPlayouts.isSelected()) toolbarParameter.put(1);
    else toolbarParameter.put(-1);
    try {
      toolbarParameter.put(Integer.parseInt(LizzieFrame.toolbar.txtAnaFirstPlayouts.getText()));
    } catch (NumberFormatException err) {
      toolbarParameter.put(-1);
    }
    if (LizzieFrame.toolbar.chkAutoPlayBlack.isSelected()) toolbarParameter.put(1);
    else toolbarParameter.put(-1);
    if (LizzieFrame.toolbar.chkAutoPlayWhite.isSelected()) toolbarParameter.put(1);
    else toolbarParameter.put(-1);
    if (LizzieFrame.toolbar.chkAutoPlayTime.isSelected()) toolbarParameter.put(1);
    else toolbarParameter.put(-1);
    try {
      toolbarParameter.put(Integer.parseInt(LizzieFrame.toolbar.txtAutoPlayTime.getText()));
    } catch (NumberFormatException err) {
      toolbarParameter.put(-1);
    }
    if (LizzieFrame.toolbar.chkAutoPlayPlayouts.isSelected()) toolbarParameter.put(1);
    else toolbarParameter.put(-1);
    try {
      toolbarParameter.put(Integer.parseInt(LizzieFrame.toolbar.txtAutoPlayPlayouts.getText()));
    } catch (NumberFormatException err) {
      toolbarParameter.put(-1);
    }
    if (LizzieFrame.toolbar.chkAutoPlayFirstPlayouts.isSelected()) toolbarParameter.put(1);
    else toolbarParameter.put(-1);
    try {
      toolbarParameter.put(
          Integer.parseInt(LizzieFrame.toolbar.txtAutoPlayFirstPlayouts.getText()));
    } catch (NumberFormatException err) {
      toolbarParameter.put(-1);
    }

    try {
      toolbarParameter.put(
          Integer.parseInt(LizzieFrame.toolbar.txtenginePkFirstPlayputs.getText()));
    } catch (NumberFormatException err) {
      toolbarParameter.put(-1);
    }

    try {
      toolbarParameter.put(
          Integer.parseInt(LizzieFrame.toolbar.txtenginePkFirstPlayputsWhite.getText()));
    } catch (NumberFormatException err) {
      toolbarParameter.put(-1);
    }

    try {
      toolbarParameter.put(Integer.parseInt(LizzieFrame.toolbar.txtenginePkTime.getText()));
    } catch (NumberFormatException err) {
      toolbarParameter.put(-1);
    }

    try {
      toolbarParameter.put(Integer.parseInt(LizzieFrame.toolbar.txtenginePkPlayputs.getText()));
    } catch (NumberFormatException err) {
      toolbarParameter.put(-1);
    }
    try {
      toolbarParameter.put(
          Integer.parseInt(LizzieFrame.toolbar.txtenginePkPlayputsWhite.getText()));
    } catch (NumberFormatException err) {
      toolbarParameter.put(-1);
    }
    try {
      toolbarParameter.put(Integer.parseInt(LizzieFrame.toolbar.txtenginePkBatch.getText()));
    } catch (NumberFormatException err) {
      toolbarParameter.put(-1);
    }
    if (LizzieFrame.toolbar.chkenginePkBatch.isSelected()) toolbarParameter.put(1);
    else toolbarParameter.put(-1);
    if (LizzieFrame.toolbar.chkenginePkContinue.isSelected()) toolbarParameter.put(1);
    else toolbarParameter.put(-1);
    if (LizzieFrame.toolbar.chkenginePkFirstPlayputs.isSelected()) toolbarParameter.put(1);
    else toolbarParameter.put(-1);
    if (LizzieFrame.toolbar.chkenginePkPlayouts.isSelected()) toolbarParameter.put(1);
    else toolbarParameter.put(-1);
    if (LizzieFrame.toolbar.chkenginePkTime.isSelected()) toolbarParameter.put(1);
    else toolbarParameter.put(-1);
    toolbarParameter.put("-100"); // (Lizzie.frame.toolbar.pkResginWinrate);
    toolbarParameter.put("-100"); // (Lizzie.frame.toolbar.pkResignMoveCounts);
    toolbarParameter.put(LizzieFrame.toolbar.AutosavePk);
    toolbarParameter.put(LizzieFrame.toolbar.isGenmoveToolbar);
    toolbarParameter.put(LizzieFrame.toolbar.anaPanelOrder);
    toolbarParameter.put(LizzieFrame.toolbar.enginePkOrder);
    toolbarParameter.put(LizzieFrame.toolbar.autoPlayOrder);
    toolbarParameter.put(LizzieFrame.toolbar.exChangeToolbar);
    toolbarParameter.put(LizzieFrame.toolbar.maxGameMoves);
    toolbarParameter.put(LizzieFrame.toolbar.checkGameMaxMove);
    try {
      toolbarParameter.put(Integer.parseInt(LizzieFrame.toolbar.txtenginePkTimeWhite.getText()));
    } catch (NumberFormatException err) {
      toolbarParameter.put(-1);
    }
    if (LizzieFrame.toolbar.chkAutoSub.isSelected()) toolbarParameter.put(1);
    else toolbarParameter.put(-1);
    try {
      toolbarParameter.put(Integer.parseInt(LizzieFrame.toolbar.txtAutoMain.getText()));
    } catch (NumberFormatException err) {
      toolbarParameter.put(-1);
    }
    try {
      toolbarParameter.put(Integer.parseInt(LizzieFrame.toolbar.txtAutoSub.getText()));
    } catch (NumberFormatException err) {
      toolbarParameter.put(-1);
    }
    toolbarParameter.put("-100"); // (Lizzie.frame.toolbar.minGanmeMove);
    toolbarParameter.put("-100"); // (Lizzie.frame.toolbar.checkGameMinMove);
    toolbarParameter.put(LizzieFrame.toolbar.isRandomMove);
    toolbarParameter.put(LizzieFrame.toolbar.randomMove);
    toolbarParameter.put(LizzieFrame.toolbar.randomDiffWinrate);
    toolbarParameter.put(LizzieFrame.toolbar.chkAnaBlack.isSelected());
    toolbarParameter.put(LizzieFrame.toolbar.chkAnaWhite.isSelected());
    toolbarParameter.put(LizzieFrame.toolbar.enginePkSaveWinrate);
    toolbarParameter.put(LizzieFrame.toolbar.rightMode);
    persistedUi.put("toolbar-parameter", toolbarParameter);

    //    JSONArray movecountPos = new JSONArray();
    //    movecountPos.put(Lizzie.estimateResults.getX());
    //    movecountPos.put(Lizzie.estimateResults.getY());
    //    persistedUi.put("movecount-position", movecountPos);

    JSONArray winrateGraph = new JSONArray();
    winrateGraph.put(LizzieFrame.winrateGraph.mode);
    persistedUi.put("winrate-graph", winrateGraph);

    if (Lizzie.frame.independentSubBoard != null) {
      JSONArray independentSub = new JSONArray();
      independentSub.put(Lizzie.frame.independentSubBoard.getX());
      independentSub.put(Lizzie.frame.independentSubBoard.getY());
      independentSub.put(Lizzie.frame.independentSubBoard.getWidth());
      independentSub.put(Lizzie.frame.independentSubBoard.getHeight());
      persistedUi.put("independent-sub-board", independentSub);
    }

    if (Lizzie.frame.independentMainBoard != null) {
      JSONArray independentMain = new JSONArray();
      independentMain.put(Lizzie.frame.independentMainBoard.getX());
      independentMain.put(Lizzie.frame.independentMainBoard.getY());
      independentMain.put(Lizzie.frame.independentMainBoard.getWidth());
      independentMain.put(Lizzie.frame.independentMainBoard.getHeight());
      persistedUi.put("independent-main-board", independentMain);
    }
    persistedUi.put("fast-commands-width", fastCommandsWidth);
    persistedUi.put("fast-commands-height", fastCommandsHeight);
    // if (!isDeletingPersist)
    writeConfig(this.persisted, new File(persistFilename));
  }

  public void deletePersist(boolean showMsg) {
    if (!showMsg && !new File(persistFilename).exists()) return;
    new File(persistFilename).delete();
    isDeletingPersist = true;
    if (showMsg) Utils.showMsg(Lizzie.resourceBundle.getString("Config.deletePersistFile"));
    else {
      JSONObject persistConfig = createPersistConfig();
      try {
        this.persisted = loadAndMergeConfig(persistConfig, persistFilename, false);
      } catch (Exception e) {
        e.printStackTrace();
        this.persisted = persistConfig;
      }
      persistedUi = persisted.getJSONObject("ui-persist");
      fastCommandsWidth = persistedUi.optInt("fast-commands-width", 500);
      fastCommandsHeight = persistedUi.optInt("fast-commands-height", 500);
    }
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

  public void save() throws IOException {
    writeConfig(this.config, new File(configFilename));
  }

  public void saveTempBoard() throws IOException {
    writeConfig(this.saveBoard, new File(saveBoardFilename));
  }

  public boolean isFrameFontSmall() {
    if (Config.frameFontSize == 12) return true;
    else return false;
  }

  public boolean isFrameFontMiddle() {
    if (frameFontSize > 12 && frameFontSize <= 16) return true;
    else return false;
  }

  public void saveKataEstimateConfigs() {
    uiConfig.put("show-katago-estimate-normal", showKataGoEstimateNormal);
    uiConfig.put("show-katago-estimate-by-size", showKataGoEstimateBySize);
    uiConfig.put("show-katago-estimate-big-below", showKataGoEstimateBigBelow);
    uiConfig.put("show-katago-estimate-not-on-live", showKataGoEstimateNotOnlive);

    uiConfig.put("show-pure-estimate-normal", showPureEstimateNormal);
    uiConfig.put("show-pure-estimate-by-size", showPureEstimateBySize);
    uiConfig.put("show-pure-estimate-big-below", showPureEstimateBigBelow);
    uiConfig.put("show-pure-estimate-not-on-live", showPureEstimateNotOnlive);
  }

  public void setSuggestionInfoOrdr(int winrateOrder, int playoutsOrder, int scoreLeadOrder) {
    suggestionInfoWinrate = winrateOrder;
    suggestionInfoPlayouts = playoutsOrder;
    suggestionInfoScoreLead = scoreLeadOrder;
    useDefaultInfoRowOrder =
        suggestionInfoWinrate == 1 && suggestionInfoPlayouts == 2 && suggestionInfoScoreLead == 3;
    uiConfig.put("suggestion-info-winrate", suggestionInfoWinrate);
    uiConfig.put("suggestion-info-playouts", suggestionInfoPlayouts);
    uiConfig.put("suggestion-info-scorelead", suggestionInfoScoreLead);
    try {
      save();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  public void saveThreshold(int winRateDiff, int scoreDiff, int playouts) {
    blunderWinThreshold = winRateDiff;
    blunderScoreThreshold = scoreDiff;
    blunderPlayoutsThreshold = playouts;
    uiConfig.put("blunder-win-threshold", blunderWinThreshold);
    uiConfig.put("blunder-score-threshold", blunderScoreThreshold);
    uiConfig.put("blunder-playouts-threshold", blunderPlayoutsThreshold);
  }

  public void saveThreshold(int winRateDiff, int scoreDiff, int playouts, boolean topCurrentMove) {
    moveListWinrateThreshold = winRateDiff;
    moveListScoreThreshold = scoreDiff;
    moveListVisitsThreshold = playouts;
    moveListTopCurNode = topCurrentMove;
    uiConfig.putOpt("move-list-winrate-threshold", moveListWinrateThreshold);
    uiConfig.putOpt("move-list-score-threshold", moveListScoreThreshold);
    uiConfig.putOpt("move-list-visits-threshold", moveListVisitsThreshold);
    uiConfig.put("movelist-top-curnode", moveListTopCurNode);
  }

  public void savePanelConfig() {
    uiConfig.put("extra-mode", extraMode);
    uiConfig.put("show-subboard", showSubBoard);
    uiConfig.put("show-winrate-graph", showWinrateGraph);
    uiConfig.put("show-comment", showComment);
    uiConfig.put("show-status", showStatus);
    uiConfig.put("show-variation-graph", showVariationGraph);
    uiConfig.put("show-captured", showCaptured);
    uiConfig.put("show-list-pane", showListPane);
    uiConfig.put("large-winrate-graph", largeWinrateGraph);
    uiConfig.put("large-subboard", largeSubBoard);
  }

  public void saveCustomMode(int index) {
    JSONObject jsonLayout = new JSONObject();
    jsonLayout.put("show-subboard", showSubBoard);
    jsonLayout.put("show-winrate-graph", showWinrateGraph);
    jsonLayout.put("show-comment", showComment);
    jsonLayout.put("show-status", showStatus);
    jsonLayout.put("show-variation-graph", showVariationGraph);
    jsonLayout.put("show-captured", showCaptured);
    jsonLayout.put("show-list-pane", showListPane);
    jsonLayout.put("large-winrate-graph", largeWinrateGraph);
    jsonLayout.put("large-subboard", largeSubBoard);
    jsonLayout.put("board-position-proportion", Lizzie.frame.BoardPositionProportion);
    boolean showIndependentMain =
        Lizzie.frame.independentMainBoard != null && Lizzie.frame.independentMainBoard.isVisible();
    boolean showIndependentSub =
        Lizzie.frame.independentSubBoard != null && Lizzie.frame.independentSubBoard.isVisible();
    boolean isMainFrameMaxSize = Lizzie.frame.getExtendedState() == JFrame.MAXIMIZED_BOTH;
    jsonLayout.put("main-frame-maxsize", isMainFrameMaxSize);
    if (!isMainFrameMaxSize) {
      JSONArray mainFramePos = new JSONArray();
      mainFramePos.put(Lizzie.frame.getX());
      mainFramePos.put(Lizzie.frame.getY());
      mainFramePos.put(Lizzie.frame.getWidth());
      mainFramePos.put(Lizzie.frame.getHeight());
      jsonLayout.put("main-frame-position", mainFramePos);
    }
    jsonLayout.put("independent-main-board", showIndependentMain);
    jsonLayout.put("independent-sub-board", showIndependentSub);
    if (showIndependentMain) {
      JSONArray independentMain = new JSONArray();
      independentMain.put(Lizzie.frame.independentMainBoard.getX());
      independentMain.put(Lizzie.frame.independentMainBoard.getY());
      independentMain.put(Lizzie.frame.independentMainBoard.getWidth());
      independentMain.put(Lizzie.frame.independentMainBoard.getHeight());
      jsonLayout.put("independent-main-board-position", independentMain);
    }

    if (showIndependentSub) {
      JSONArray independentSub = new JSONArray();
      independentSub.put(Lizzie.frame.independentSubBoard.getX());
      independentSub.put(Lizzie.frame.independentSubBoard.getY());
      independentSub.put(Lizzie.frame.independentSubBoard.getWidth());
      independentSub.put(Lizzie.frame.independentSubBoard.getHeight());
      jsonLayout.put("independent-sub-board-position", independentSub);
    }
    if (index == 1) customLayout1 = jsonLayout;
    else customLayout2 = jsonLayout;
    uiConfig.put("custom-layout-" + index, jsonLayout);
  }

  public void loadCustomLayout(int index) {
    JSONObject jsonLayout = null;
    if (index == 1) jsonLayout = customLayout1;
    else if (index == 2) jsonLayout = customLayout2;
    if (jsonLayout == null) return;

    showSubBoard = jsonLayout.optBoolean("show-subboard");
    showWinrateGraph = jsonLayout.optBoolean("show-winrate-graph");
    showComment = jsonLayout.optBoolean("show-comment");
    showStatus = jsonLayout.optBoolean("show-status");
    showVariationGraph = jsonLayout.optBoolean("show-variation-graph");
    showCaptured = jsonLayout.optBoolean("show-captured");
    largeSubBoard = jsonLayout.optBoolean("large-subboard");
    largeWinrateGraph = jsonLayout.optBoolean("large-winrate-graph");
    Lizzie.frame.BoardPositionProportion = jsonLayout.optInt("board-position-proportion");
    Lizzie.frame.setVarTreeVisible(showVariationGraph);
    if (jsonLayout.optBoolean("show-list-pane") && !Lizzie.config.showListPane())
      Lizzie.config.toggleShowListPane();
    if (!jsonLayout.optBoolean("show-list-pane") && Lizzie.config.showListPane())
      Lizzie.config.toggleShowListPane();
    boolean showIndependentMain = jsonLayout.optBoolean("independent-main-board");
    boolean showIndependentSub = jsonLayout.optBoolean("independent-sub-board");
    if (showIndependentMain) {
      extraMode = ExtraMode.Float_Board;
      if (Lizzie.frame.independentMainBoard == null
          || !Lizzie.frame.independentMainBoard.isVisible())
        Lizzie.frame.toggleIndependentMainBoard();
      Lizzie.frame.independentMainBoard.setBounds(
          jsonLayout.getJSONArray("independent-main-board-position").getInt(0),
          jsonLayout.getJSONArray("independent-main-board-position").getInt(1),
          jsonLayout.getJSONArray("independent-main-board-position").getInt(2),
          jsonLayout.getJSONArray("independent-main-board-position").getInt(3));
    } else {
      extraMode = ExtraMode.Normal;
    }
    if (showIndependentSub) {
      if (Lizzie.frame.independentSubBoard == null || !Lizzie.frame.independentSubBoard.isVisible())
        Lizzie.frame.toggleIndependentSubBoard();
      Lizzie.frame.independentSubBoard.setBounds(
          jsonLayout.getJSONArray("independent-sub-board-position").getInt(0),
          jsonLayout.getJSONArray("independent-sub-board-position").getInt(1),
          jsonLayout.getJSONArray("independent-sub-board-position").getInt(2),
          jsonLayout.getJSONArray("independent-sub-board-position").getInt(3));
    }
    if (jsonLayout.getBoolean("main-frame-maxsize")) {
      Lizzie.frame.setExtendedState(Frame.MAXIMIZED_BOTH);
    } else {
      Lizzie.frame.setBounds(
          jsonLayout.getJSONArray("main-frame-position").getInt(0),
          jsonLayout.getJSONArray("main-frame-position").getInt(1),
          jsonLayout.getJSONArray("main-frame-position").getInt(2),
          jsonLayout.getJSONArray("main-frame-position").getInt(3));
    }
    Lizzie.frame.refreshContainer();
    Lizzie.frame.repaint();
  }

  public void setMoveNumber(int num) {
    // TODO Auto-generated method stub
    Lizzie.config.allowMoveNumber = num;
    Lizzie.config.uiConfig.put("allow-move-number", Lizzie.config.allowMoveNumber);
    if (num != 0 && num != -1) {
      Lizzie.config.onlyLastMoveNumber = 1;
      Lizzie.config.uiConfig.put("only-last-move-number", 1);
    }
    Lizzie.frame.refresh();
    LizzieFrame.menu.setBtnRankMark();
  }

  public void saveOtherBoardSize(int width, int height) {
    otherSizeWidth = width;
    otherSizeHeight = height;
    uiConfig.put("other-size-width", otherSizeWidth);
    uiConfig.put("other-size-height", otherSizeHeight);
  }

  public int getExtraModeValue(ExtraMode mode) {
    switch (mode) {
      case Normal:
        return 0;
      case Four_Sub:
        return 1;
      case Double_Engine:
        return 2;
      case Thinking:
        return 3;
      case Min:
        return 7;
      case Float_Board:
        return 8;
      default:
        return 0;
    }
  }

  public ExtraMode getExtraMode(int value) {
    // 1=四方图2=双引擎3=思考 8=浮动棋盘模式
    switch (value) {
      case 0:
        return ExtraMode.Normal;
      case 1:
        return ExtraMode.Four_Sub;
      case 2:
        return ExtraMode.Double_Engine;
      case 3:
        return ExtraMode.Thinking;
      case 7:
        return ExtraMode.Min;
      case 8:
        return ExtraMode.Float_Board;
      default:
        return ExtraMode.Normal;
    }
  }

  public boolean isDoubleEngineMode() {
    return extraMode == ExtraMode.Double_Engine;
  }

  public boolean isThinkingMode() {
    return extraMode == ExtraMode.Thinking;
  }

  public boolean isFourSubMode() {
    return extraMode == ExtraMode.Four_Sub;
  }

  public boolean isFloatBoardMode() {
    return extraMode == ExtraMode.Float_Board;
  }

  public boolean isMinMode() {
    return extraMode == ExtraMode.Min;
  }

  public boolean isNormalMode() {
    return extraMode == ExtraMode.Normal;
  }
}
