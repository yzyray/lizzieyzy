package featurecat.lizzie.analysis;

import featurecat.lizzie.Lizzie;
import featurecat.lizzie.gui.EngineData;
import featurecat.lizzie.gui.EngineFailedMessage;
import featurecat.lizzie.gui.JFontCheckBox;
import featurecat.lizzie.gui.JFontLabel;
import featurecat.lizzie.gui.LizzieFrame;
import featurecat.lizzie.gui.Message;
import featurecat.lizzie.rules.Board;
import featurecat.lizzie.rules.BoardData;
import featurecat.lizzie.rules.BoardHistoryNode;
import featurecat.lizzie.rules.Stone;
import featurecat.lizzie.util.Utils;
import java.awt.Component;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.Box;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import org.jdesktop.swingx.util.OS;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * An interface with leelaz go engine. Can be adapted for GTP, but is specifically designed for
 * GCP's Leela Zero. leelaz is modified to output information as it ponders see
 * www.github.com/gcp/leela-zero
 */
public class Leelaz {
  // private static final long MINUTE = 60 * 1000; // number of milliseconds in a minute

  // private long maxAnalyzeTimeMillis; // , maxThinkingTimeMillis;
  int cmdNumber;
  int modifyNumber;
  private int currentCmdNum;
  // public int modifyCmdNum;
  // private boolean isResponse=false;
  private ArrayDeque<String> cmdQueue;

  private Process process;

  private BufferedReader inputStream;
  private BufferedOutputStream outputStream;
  private BufferedReader errorStream;

  // public Board board;
  private List<MoveData> bestMoves;
  private List<MoveData> bestMovesPrevious;
  // private List<MoveData> bestMovesTemp;
  // public boolean canGetGenmoveInfo = false;
  private boolean underPonder = false;
  public boolean canGetSummaryInfo = false;
  // public boolean canGetChatInfo = false;
  // public boolean canGetGenmoveInfoGen = false;
  // public boolean getGenmoveInfoPrevious= false;
  // private List<LeelazListener> listeners;

  private boolean isPondering;
  private long startPonderTime;
  private boolean showStopTips = true;

  // fixed_handicap
  public boolean isSettingHandicap = false;

  // genmove
  public boolean isThinking = false;
  public boolean isInputCommand = false;

  public boolean getRcentLine = false;
  private int recentLineNumber = 0;
  public String recentRulesLine = "";
  public int usingSpecificRules = -1; // 1=中国规则2=中古规则3=日本规则4=TT规则5=其他规则
  public boolean preload = false;
  public boolean started = false;
  public boolean isDownWithError = false;
  public boolean isLoaded = false;
  public boolean isCheckingVersion;
  public boolean isCheckingName;
  public String initialCommand;
  private boolean isCheckingPda = false;
  public boolean isKataGoPda = false;
  public boolean isDymPda = false;
  public boolean isStaticPda = false;
  public boolean canRestoreDymPda = false;
  public double pda = 0;
  public double wrn = 0;
  private double pdaBeforeGame = 0;
  public double pdaCap = 0;
  public boolean startAutoAna = false;
  // for Multiple Engine
  public String oriEngineCommand = "";
  public String engineCommand;
  private List<String> commands;
  //	private String currentWeightFile = "";
  //	private String currentWeight = "";
  // public boolean switching = false;
  private int currentEngineN = -1;
  private ScheduledExecutorService executor;
  private ScheduledExecutorService executorErr;
  ArrayList<Double> tempcount = new ArrayList<Double>();
  // dynamic komi and opponent komi as reported by dynamic-komi version of leelaz
  //	private float dynamicKomi = Float.NaN;
  //	private float dynamicOppKomi = Float.NaN;

  public int version = -1;
  //	public ArrayList<Integer> heatcount = new ArrayList<Integer>();
  public String currentEnginename = "";
  public String bestMovesEnginename = "";
  public String oriEnginename = "";
  public boolean autoAnalysed = false;
  //	private boolean isSaving = false;
  public boolean isResigning = false;
  //	public boolean isClosingAutoAna = false;
  public boolean isColorEngine = false;
  public int stage = -1;
  public float komi = 7.5f;
  public float orikomi = 7.5f;
  public int blackResignMoveCounts = 0;
  public int whiteResignMoveCounts = 0;
  public boolean resigned = false;
  //	public boolean isManualB=false;
  //	public boolean isManualW=false;
  public boolean doublePass = false;
  public boolean outOfMoveNum = false;
  public boolean played = false;
  private boolean canSetNotPlayed = false;

  public boolean isKatago = false;
  public boolean isKatagoCustom = false;
  public boolean noAnalyze = false;
  public boolean isSai = false;
  private boolean isLeela = false;
  public boolean isChanged = false;
  public double scoreMean = 0;
  public double scoreStdev = 0;
  private boolean isCommandLine = false;
  public int width = 19;
  public int height = 19;
  public int oriWidth = 19;
  public int oriHeight = 19;
  public boolean firstLoad = false;
  Message msg;
  public boolean playNow = false;
  public boolean isZen = false;
  public boolean canAddPlayer = true;
  public boolean requireResponseBeforeSend = false;
  public boolean noLcb = false;
  // private boolean isInfoLine = false;
  // private boolean isNotifying = false;
  public boolean isSSH = false;
  // public boolean isScreen = false;
  public boolean isheatmap = false;
  public boolean iskataHeatmapShowOwner = false;
  public ArrayList<Integer> heatcount = new ArrayList<Integer>();

  public long pkMoveStartTime;
  public long pkMoveTime;
  // private int prepareNoGetGenmoveInfo = -1;
  // public long pkMoveTimeAll=0;
  public long pkMoveTimeGame = 0;
  public boolean canSuicidal = false;
  // public int genmoveNode = 0;
  public int anaGameResignCount = 0;
  public double heatwinrate = -1;
  public int symmetry = 0;
  public double heatScore;
  private boolean heatCanGetPolicy;
  private boolean heatCanGetOwnership;

  private boolean canheatRedraw = false;
  public ArrayList<Double> heatPolicy = new ArrayList<Double>();
  public ArrayList<Double> heatOwnership = new ArrayList<Double>();
  public boolean isGamePaused = false;
  // public boolean isReadyForGenmoveGame=false;
  // private boolean isModifying=false;
  // private int ignoreCmdNumber=0;
  public boolean isTuning = false;
  public boolean isNormalEnd = false;
  public boolean canCheckAlive = true;
  public boolean isLeela0110 = false;
  private List<MoveData> leela0110BestMoves;
  private Timer leela0110PonderingTimer;
  private BoardData leela0110PonderingBoardData;
  private static final int LEELA0110_PONDERING_INTERVAL_MILLIS = 1000;
  public boolean javaSSHClosed = false;
  public boolean useJavaSSH = false;
  public String ip;
  public String port;
  public String userName;
  public String password;
  public boolean useKeyGen;
  public String keyGenPath;
  public SSHController javaSSH;
  private boolean stopByLimit = false;
  public boolean stopByPlayouts = false;
  public boolean outOfPlayoutsLimit = false;
  private EngineFailedMessage engineFailedMessage;
  public List<String> commandLists = new ArrayList<String>();
  private boolean startGetCommandList = false;
  private boolean endGetCommandList = false;
  private int currentTotalPlayouts;
  // private int refreshNumber=0;
  // private boolean isEstimating=true;
  /**
   * Initializes the leelaz process and starts reading output
   *
   * @throws IOException
   */
  public Leelaz(String engineCommand) throws IOException, JSONException {
    // board = new Board();
    bestMoves = new ArrayList<>();
    currentTotalPlayouts = 0;
    bestMovesPrevious = new ArrayList<>();
    // bestMovesTemp = new ArrayList<>();
    //	listeners = new CopyOnWriteArrayList<>();

    isPondering = false;
    startPonderTime = System.currentTimeMillis();
    cmdNumber = 1;
    currentCmdNum = 0;
    cmdQueue = new ArrayDeque<>();
    setEngineCommand(engineCommand);
  }

  public String getEngineCommand() {
    if (oriEngineCommand.startsWith("encryption||"))
      return Lizzie.resourceBundle.getString("Leelaz.encryption");
    return engineCommand;
  }

  public void setEngineCommand(String commandString) {
    oriEngineCommand = commandString;
    if (commandString.startsWith("encryption||")) {
      commandString = commandString.substring(12);
      commandString = Utils.doDecrypt2(commandString);
    }
    this.engineCommand = commandString == null ? oriEngineCommand : commandString;
    if (this.engineCommand.toLowerCase().contains("katajigo")) {
      this.noAnalyze = true;
    }
    if (this.engineCommand.toLowerCase().contains("gogui")) {
      this.requireResponseBeforeSend = true;
    }
    if (this.engineCommand.toLowerCase().contains("ssh")
        || engineCommand.toLowerCase().contains("plink")) {
      this.isSSH = true;
    }
    //		if (this.engineCommand.startsWith("screen")) {
    //			this.engineCommand=this.engineCommand.substring(6);
    //			this.isScreen = true;
    //			}
  }
  //	public void updateCommand(String engineCommand) {
  //		this.engineCommand = engineCommand;
  //		if (engineCommand.toLowerCase().contains("override-version")) {
  //			this.isKatago = true;
  //		}
  //		if (engineCommand.toLowerCase().contains("zen")) {
  //			this.isZen = true;
  //		}
  //		if (engineCommand.toLowerCase().contains("ssh")) {
  //			this.isSSH = true;
  //		}
  //	}

  //	private String formateSaveString (String filename)
  //	{
  //		filename=filename.replaceAll("[/\\\\:*?|]", ".");
  //		filename=filename.replaceAll("[\"<>]", "'");
  //		return filename;
  //	}

  public String getEngineName(int index) {
    if (index < 0) return Lizzie.resourceBundle.getString("Menu.noEngine");
    ArrayList<EngineData> engineData = Utils.getEngineData();
    currentEnginename = engineData.get(index).name;
    oriEnginename = currentEnginename;
    String regEx = "[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
    String aa = "";
    Pattern p = Pattern.compile(regEx);
    Matcher m = p.matcher(currentEnginename);
    currentEnginename = m.replaceAll(aa).trim();
    bestMovesEnginename = currentEnginename.replaceAll(" ", "");
    return currentEnginename;
  }

  public void startEngine(int index) throws IOException {
    if (engineCommand.trim().isEmpty()) {
      Utils.showMsg(Lizzie.resourceBundle.getString("EngineFaied.empty"));
      return;
    }
    canAddPlayer = false;
    currentEngineN = index;
    canRestoreDymPda = false;
    commands = Utils.splitCommand(engineCommand);
    pda = 0;
    // Get weight name
    //	Pattern wPattern = Pattern.compile("(?s).*?(--weights |-w |-model )([^'\" ]+)(?s).*");
    // Matcher wMatcher = wPattern.matcher(engineCommand);
    currentEnginename = getEngineName(index);
    isDownWithError = false;
    if (this.useJavaSSH) {
      process = null;
      this.javaSSH = new SSHController(this, this.ip, this.port);
      boolean loginStatus = false;
      if (this.useKeyGen) {
        loginStatus =
            this.javaSSH
                .loginByFileKey(this.engineCommand, this.userName, new File(this.keyGenPath))
                .booleanValue();
      } else {
        loginStatus =
            this.javaSSH.login(this.engineCommand, this.userName, this.password).booleanValue();
      }
      if (loginStatus) {
        this.javaSSHClosed = false;
        this.inputStream = new BufferedReader(new InputStreamReader(this.javaSSH.getStdout()));
        this.outputStream = new BufferedOutputStream(this.javaSSH.getStdin());
        this.errorStream = new BufferedReader(new InputStreamReader(this.javaSSH.getSterr()));
      } else {
        isDownWithError = true;
        return;
      }
    } else {
      ProcessBuilder processBuilder = new ProcessBuilder(commands);
      processBuilder.redirectErrorStream(false);
      try {
        process = processBuilder.start();
      } catch (IOException e) {
        String err = e.getLocalizedMessage();
        try {
          tryToDignostic(
              Lizzie.resourceBundle.getString("Leelaz.engineFailed")
                  + ": "
                  + ((err == null)
                      ? Lizzie.resourceBundle.getString("Leelaz.engineStartNoExceptionMessage")
                      : err),
              true);
          LizzieFrame.openMoreEngineDialog();
        } catch (JSONException e1) {
          // TODO Auto-generated catch block
          e1.printStackTrace();
          isDownWithError = true;
        }
        return;
      }
      initializeStreams();
    }
    // Send a version request to check that we have a supported version
    // Response handled in parseLine
    isCheckingVersion = true;
    isCheckingName = true;
    endGetCommandList = false;
    // sendCommand("turnon");
    if (!isSSH) {
      sendCommand("name");
      sendCommand("version");
      sendCommand("list_commands");
      if (!(Lizzie.frame.isPlayingAgainstLeelaz || Lizzie.frame.isAnaPlayingAgainstLeelaz))
        sendCommand("komi " + komi);
      boardSize(width, height);
      if (initialCommand != null && !initialCommand.equals("")) {
        String[] initialCommands = initialCommand.trim().split(";");
        for (String command : initialCommands) {
          sendCommand(command);
        }
      }
    }
    if (this == Lizzie.leelaz) Lizzie.board.getHistory().getGameInfo().setKomi(komi);
    if (isSSH) {
      Runnable runnable =
          new Runnable() {
            public void run() {
              try {
                Thread.sleep(500);
              } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
              }
              sendCommand("name");
              sendCommand("name");
              sendCommand("name");
              sendCommand("version");
              sendCommand("list_commands");
              boardSize(width, height);
              if (!(Lizzie.frame.isPlayingAgainstLeelaz || Lizzie.frame.isAnaPlayingAgainstLeelaz))
                sendCommand("komi " + komi);
              if (initialCommand != null && !initialCommand.equals("")) {
                String[] initialCommands = initialCommand.trim().split(";");
                for (String command : initialCommands) {
                  sendCommand(command);
                }
              }
              setResponseUpToDate();
            }
          };
      Thread thread = new Thread(runnable);
      thread.start();
    }
    // if(width!=19||height!=19)

    // start a thread to continuously read Leelaz output
    // new Thread(this::read).start();
    // can stop engine for switching weights
    executor = Executors.newSingleThreadScheduledExecutor();
    isNormalEnd = false;
    executor.execute(this::read);
    executorErr = Executors.newSingleThreadScheduledExecutor();
    executorErr.execute(this::readError);
    started = true;

    if (Lizzie.leelaz2 != null && this == Lizzie.leelaz2) {
      if (index > 19) LizzieFrame.menu.changeEngineIcon2(20, 1);
      else LizzieFrame.menu.changeEngineIcon2(index, 1);
    } else {
      if (index > 19) LizzieFrame.menu.changeEngineIcon(20, 1);
      else LizzieFrame.menu.changeEngineIcon(index, 1);
    }
    if (Lizzie.frame.isShowingHeatmap) Lizzie.frame.isShowingHeatmap = false;
    if (Lizzie.frame.isShowingPolicy) Lizzie.frame.isShowingPolicy = false;
  }

  //	public void restartEngine(int index) throws IOException {
  //		if (engineCommand.trim().isEmpty()) {
  //			return;
  //		}
  //		//switching = true;
  //		this.engineCommand = engineCommand;
  //		// stop the ponder
  //		if (Lizzie.leelaz.isPondering()) {
  //			Lizzie.leelaz.togglePonder();
  //		}
  //		normalQuit();
  //		startEngine(index);
  //		// currentEngineN = index;
  //		togglePonder();
  //	}

  public void restartClosedEngine(int index) throws IOException {
    boolean isPondering = this.isPondering;
    if (engineCommand.trim().isEmpty()) {
      return;
    }
    isLoaded = false;
    canCheckAlive = false;
    startEngine(index);
    Leelaz thisLeelz = this;
    Runnable syncBoard =
        new Runnable() {
          public void run() {
            while (!isLoaded() || isCheckingName) {
              try {
                Thread.sleep(100);
              } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
              }
            }
            if (isPondering) Lizzie.board.resendMoveToEngine(thisLeelz);
            else {
              Lizzie.board.resetMoves();
            }
          }
        };
    Thread syncBoardTh = new Thread(syncBoard);
    syncBoardTh.start();
  }

  public void normalQuit() {
    isNormalEnd = true;
    leela0110StopPonder();
    if (Lizzie.leelaz2 != null && this == Lizzie.leelaz2) {
      if (currentEngineN > 20) LizzieFrame.menu.changeEngineIcon2(20, 0);
      else LizzieFrame.menu.changeEngineIcon2(currentEngineN, 0);
    } else {
      if (currentEngineN > 20) LizzieFrame.menu.changeEngineIcon(20, 0);
      else LizzieFrame.menu.changeEngineIcon(currentEngineN, 0);
    }

    //		if(isScreen)
    //			sendCommand("name");
    sendCommand("quit");
    if (this.useJavaSSH) {
      javaSSH.close();
    } else {
      executor.shutdown();
      try {
        while (!executor.awaitTermination(1, TimeUnit.SECONDS)) {
          executor.shutdownNow();
        }
        if (executor.awaitTermination(1, TimeUnit.SECONDS)) {
          shutdown();
        }
      } catch (InterruptedException e) {
        executor.shutdownNow();
        Thread.currentThread().interrupt();
      }
    }
    started = false;
    isLoaded = false;
  }

  public void forceQuit() {
    isNormalEnd = true;
    started = false;
    isLoaded = false;
    leela0110StopPonder();
    //		if(isScreen)
    //			sendCommand("name");
    if (Lizzie.leelaz2 != null && this == Lizzie.leelaz2) {
      if (currentEngineN > 20) LizzieFrame.menu.changeEngineIcon2(20, 0);
      else LizzieFrame.menu.changeEngineIcon2(currentEngineN, 0);
    } else {
      if (currentEngineN > 20) LizzieFrame.menu.changeEngineIcon(20, 0);
      else LizzieFrame.menu.changeEngineIcon(currentEngineN, 0);
    }
    if (this.useJavaSSH) {
      javaSSH.close();
    } else {
      try {
        process.destroyForcibly();
      } catch (Exception e) {
      }
    }
    outputStream = null;
  }

  /** Initializes the input and output streams */
  public void initializeStreams() {
    inputStream = new BufferedReader(new InputStreamReader(process.getInputStream()));
    outputStream = new BufferedOutputStream(process.getOutputStream());
    errorStream = new BufferedReader(new InputStreamReader(process.getErrorStream()));
  }

  public List<MoveData> parseInfoSai(String line) {
    List<MoveData> bestMoves = new ArrayList<>();
    String[] variations = line.split(" info ");
    for (String var : variations) {
      if (!var.trim().isEmpty()) {
        bestMoves.add(MoveData.fromInfoSai(var));
      }
    }
    currentTotalPlayouts = MoveData.getPlayouts(bestMoves);
    if (Lizzie.config.isDoubleEngineMode() && Lizzie.leelaz2 != null && this == Lizzie.leelaz2)
      Lizzie.board
          .getData()
          .tryToSetBestMoves2(bestMoves, bestMovesEnginename, true, currentTotalPlayouts);
    else {
      if (EngineManager.isEngineGame && Lizzie.config.enginePkPonder) {
        if ((Lizzie.board.getHistory().isBlacksTurn()
                && this
                    == Lizzie.engineManager.engineList.get(
                        EngineManager.engineGameInfo.blackEngineIndex))
            || !Lizzie.board.getHistory().isBlacksTurn()
                && this
                    == Lizzie.engineManager.engineList.get(
                        EngineManager.engineGameInfo.whiteEngineIndex)) {
          Lizzie.board
              .getData()
              .tryToSetBestMoves(bestMoves, bestMovesEnginename, true, currentTotalPlayouts);
        }
      } else
        Lizzie.board
            .getData()
            .tryToSetBestMoves(bestMoves, bestMovesEnginename, true, currentTotalPlayouts);
    }
    return bestMoves;
  }

  public List<MoveData> parseInfo(String line) {
    List<MoveData> bestMoves = new ArrayList<>();
    String[] variations = line.split(" info ");
    //	int k = (Lizzie.config.limitMaxSuggestion > 0&&!Lizzie.config.showNoSuggCircle ?
    // Lizzie.config.limitMaxSuggestion : 361);
    for (String var : variations) {
      if (!var.trim().isEmpty()) {
        bestMoves.add(MoveData.fromInfo(var));
        //	k = k - 1;
        //	if (k < 1)
        //		break;
      }
    }
    currentTotalPlayouts = MoveData.getPlayouts(bestMoves);
    if (Lizzie.config.isDoubleEngineMode() && Lizzie.leelaz2 != null && this == Lizzie.leelaz2)
      Lizzie.board
          .getData()
          .tryToSetBestMoves2(bestMoves, bestMovesEnginename, true, currentTotalPlayouts);
    else {
      if (EngineManager.isEngineGame && Lizzie.config.enginePkPonder) {
        if ((Lizzie.board.getHistory().isBlacksTurn()
                && this
                    == Lizzie.engineManager.engineList.get(
                        EngineManager.engineGameInfo.blackEngineIndex))
            || !Lizzie.board.getHistory().isBlacksTurn()
                && this
                    == Lizzie.engineManager.engineList.get(
                        EngineManager.engineGameInfo.whiteEngineIndex)) {
          // if(!isModifying)
          Lizzie.board
              .getData()
              .tryToSetBestMoves(bestMoves, bestMovesEnginename, true, currentTotalPlayouts);
        }
      } else
        Lizzie.board
            .getData()
            .tryToSetBestMoves(bestMoves, bestMovesEnginename, true, currentTotalPlayouts);
    }
    return bestMoves;
  }

  public List<MoveData> parseInfoKatago(String line) {
    List<MoveData> bestMoves = new ArrayList<>();
    String[] variations = line.split(" info ");
    // int k = (Lizzie.config.limitMaxSuggestion > 0&&!Lizzie.config.showNoSuggCircle ?
    // Lizzie.config.limitMaxSuggestion : 361);
    for (String var : variations) {
      if (!var.trim().isEmpty()) {
        bestMoves.add(MoveData.fromInfoKatago(var));
        //		k = k - 1;
        //		if (k < 1)
        //			break;
      }
    }
    currentTotalPlayouts = MoveData.getPlayouts(bestMoves);
    ArrayList<Double> estimateArray = new ArrayList<Double>();
    if (Lizzie.config.showKataGoEstimate) {
      if (line.contains("ownership")) {
        String[] params = line.trim().split("ownership");
        String[] params2 = params[1].trim().split(" ");
        for (int i = 0; i < params2.length; i++) estimateArray.add(Double.parseDouble(params2[i]));
      }
    } else estimateArray = null;
    if (Lizzie.config.isDoubleEngineMode() && Lizzie.leelaz2 != null && this == Lizzie.leelaz2)
      Lizzie.board
          .getData()
          .tryToSetBestMoves2(
              bestMoves, bestMovesEnginename, true, currentTotalPlayouts, estimateArray);
    else {
      if (EngineManager.isEngineGame && Lizzie.config.enginePkPonder) {
        if ((Lizzie.board.getHistory().isBlacksTurn()
                && this
                    == Lizzie.engineManager.engineList.get(
                        EngineManager.engineGameInfo.blackEngineIndex))
            || !Lizzie.board.getHistory().isBlacksTurn()
                && this
                    == Lizzie.engineManager.engineList.get(
                        EngineManager.engineGameInfo.whiteEngineIndex)) {
          //	if(!isModifying)
          Lizzie.board
              .getData()
              .tryToSetBestMoves(
                  bestMoves, bestMovesEnginename, true, currentTotalPlayouts, estimateArray);
        }
      } else
        Lizzie.board
            .getData()
            .tryToSetBestMoves(
                bestMoves, bestMovesEnginename, true, currentTotalPlayouts, estimateArray);
    }
    return bestMoves;
  }

  /**
   * Parse a line of Leelaz output
   *
   * @param line output line
   * @throws IOException
   */
  private void parseLineForGenmovePk(String line) throws IOException {
    // Lizzie.gtpConsole.addLineforce(line);

    if (line.startsWith("info")) {
      if (this != Lizzie.leelaz && isResponseUpToDate()) {
        if (isKatago) {
          this.bestMoves = parseInfoKatago(line.substring(5));
        } else if (isSai) {
          this.bestMoves = parseInfoSai(line.substring(5));
        } else {
          this.bestMoves = parseInfo(line.substring(5));
        }
        Lizzie.frame.refresh(1);
      }
      return;
    } else if (Lizzie.gtpConsole.isVisible() || Lizzie.config.alwaysGtp || !this.isLoaded)
      Lizzie.gtpConsole.addLine(line + "\n");
    if (isCheckingPda) {
      if (line.startsWith("pda:")) {
        isDymPda = true;
        String[] params = line.trim().split(" ");
        if (params.length == 2) pda = Double.parseDouble(params[1]);
        LizzieFrame.menu.txtPDA.setText(String.format(Locale.ENGLISH, "%.3f", pda));
        if (LizzieFrame.menu.setPda != null)
          LizzieFrame.menu.setPda.curPDA.setText(String.format(Locale.ENGLISH, "%.3f", pda));
        if (Lizzie.config.chkAutoPDA) {
          sendCommand(Lizzie.config.AutoPDA);
          if (Lizzie.config.chkDymPDA) {
            this.pdaCap = Double.parseDouble(Lizzie.config.dymPDACap.trim());
            if (LizzieFrame.menu.setPda != null)
              LizzieFrame.menu.setPda.txtDymCap.setText(Lizzie.config.dymPDACap);
          }
          if (Lizzie.config.chkStaticPDA) {
            LizzieFrame.menu.txtPDA.setText(Lizzie.config.staticPDAcur);
            this.pda = Double.parseDouble(Lizzie.config.staticPDAcur.trim());
            isStaticPda = true;
          } else {
            isStaticPda = false;
          }
        }
      }
      if (line.startsWith("PDACap:")) {
        String[] params = line.trim().split(" ");
        if (params.length == 2) {
          //	if(pdaCap==0)
          pdaCap = Double.parseDouble(params[1]);
          if (pdaCap != 0 && !isStaticPda) {
            isStaticPda = false;
            Runnable syncDymPda =
                new Runnable() {
                  public void run() {
                    int i = 0;
                    while (!canRestoreDymPda) {
                      try {
                        i++;
                        if (i > 19) break;
                        Thread.sleep(50);
                      } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                      }
                    }
                    canRestoreDymPda = false;
                    if (Lizzie.config.chkAutoPDA) sendCommand(Lizzie.config.AutoPDA);
                    else sendCommand("dympdacap " + pdaCap);
                    if (isPondering()) ponder();
                  }
                };
            Thread syncDymPdaTh = new Thread(syncDymPda);
            syncDymPdaTh.start();
          } else {
            isStaticPda = true;
          }
          if (LizzieFrame.menu.setPda != null)
            LizzieFrame.menu.setPda.txtDymCap.setText(String.valueOf(pdaCap));
        }
      }
    }

    if (line.startsWith("=") || line.startsWith("play")) {
      isCommandLine = true;
      String[] params = line.trim().split(" ");
      // currentCmdNum = Integer.parseInt(params[0].substring(1).trim());
      if (params.length <= 1) return;
      if (EngineManager.isEngineGame && params.length >= 2) {
        if (Lizzie.board.getHistory().isBlacksTurn()
            && (this.currentEngineN == EngineManager.engineGameInfo.whiteEngineIndex)) {
          return;
        }
        if (!Lizzie.board.getHistory().isBlacksTurn()
            && (this.currentEngineN == EngineManager.engineGameInfo.blackEngineIndex)) {
          return;
        }
        if (this.isZen) {
          synchronized (bestMoves) {
            try {
              if (bestMoves != null && !bestMoves.isEmpty()) {
                currentTotalPlayouts = MoveData.getPlayouts(bestMoves);
                Lizzie.board
                    .getData()
                    .tryToSetBestMoves(bestMoves, bestMovesEnginename, true, currentTotalPlayouts);
                this.bestMoves = new ArrayList<>();
              }
            } catch (Exception e) {
              this.bestMoves = new ArrayList<>();
              e.printStackTrace();
            }
          }
        }
        if (params[1].contains("resign")) {
          pkMoveTime = System.currentTimeMillis() - pkMoveStartTime;
          pkMoveTimeGame = pkMoveTimeGame + pkMoveTime;

          nameCmdfornoponder();
          genmoveResign(false);
          return;
        }
        if (Lizzie.board.getHistory().getMoveNumber() > EngineManager.engineGameInfo.maxGameMoves) {
          pkMoveTime = System.currentTimeMillis() - pkMoveStartTime;
          pkMoveTimeGame = pkMoveTimeGame + pkMoveTime;
          outOfMoveNum = true;
          nameCmdfornoponder();
          genmoveResign(false);
          return;
        }
        checkForGomokuFullBoard(true);
        boolean isPassingLose = false;
        if (params[1].startsWith("Passing")) {
          isPassingLose = true;
        }
        if (!isPassingLose && params[1].startsWith("pass")) {
          pkMoveTime = System.currentTimeMillis() - pkMoveStartTime;
          pkMoveTimeGame = pkMoveTimeGame + pkMoveTime;
          Optional<int[]> passStep = Optional.empty();
          Optional<int[]> lastMove = Lizzie.board.getLastMove();

          if (lastMove == passStep) {
            doublePass = true;
            nameCmdfornoponder();
            genmoveResign(true);
            return;
          }
          Lizzie.engineManager
              .engineList
              .get(EngineManager.engineGameInfo.whiteEngineIndex)
              .clearPkMoveStartTime();
          Lizzie.engineManager
              .engineList
              .get(EngineManager.engineGameInfo.blackEngineIndex)
              .clearPkMoveStartTime();
          Lizzie.board.pass();
          if (this.currentEngineN == EngineManager.engineGameInfo.blackEngineIndex) {
            if (!Lizzie.engineManager
                .engineList
                .get(EngineManager.engineGameInfo.whiteEngineIndex)
                .playMoveGenmove("B", "pass")) {
              return;
            }
            Lizzie.engineManager
                .engineList
                .get(EngineManager.engineGameInfo.whiteEngineIndex)
                .genmoveForPk("W");
            if (!Lizzie.config.enginePkPonder)
              Lizzie.engineManager
                  .engineList
                  .get(EngineManager.engineGameInfo.blackEngineIndex)
                  .nameCmdfornoponder();
            Lizzie.leelaz =
                Lizzie.engineManager.engineList.get(EngineManager.engineGameInfo.blackEngineIndex);
          } else {
            if (!Lizzie.engineManager
                .engineList
                .get(EngineManager.engineGameInfo.blackEngineIndex)
                .playMoveGenmove("W", "pass")) {
              return;
            }
            Lizzie.engineManager
                .engineList
                .get(EngineManager.engineGameInfo.blackEngineIndex)
                .genmoveForPk("B");
            if (!Lizzie.config.enginePkPonder)
              Lizzie.engineManager
                  .engineList
                  .get(EngineManager.engineGameInfo.whiteEngineIndex)
                  .nameCmdfornoponder();
            Lizzie.leelaz =
                Lizzie.engineManager.engineList.get(EngineManager.engineGameInfo.whiteEngineIndex);
          }
          return;
        } else {
          //	try {
          Optional<int[]> coords;
          if (isPassingLose) {
            coords = Board.asCoordinates(inputStream.readLine());
          } else coords = Board.asCoordinates(params[1]);
          if (!coords.isPresent()) {
            return;
          }
          canCheckAlive = true;
          pkMoveTime = System.currentTimeMillis() - pkMoveStartTime;
          pkMoveTimeGame = pkMoveTimeGame + pkMoveTime;
          Lizzie.engineManager
              .engineList
              .get(EngineManager.engineGameInfo.whiteEngineIndex)
              .clearPkMoveStartTime();
          Lizzie.engineManager
              .engineList
              .get(EngineManager.engineGameInfo.blackEngineIndex)
              .clearPkMoveStartTime();
          Lizzie.board.place(coords.get()[0], coords.get()[1]);

          //					}
          //					catch (Exception e)
          //					{
          //						return;
          //					}
          String coordsString = Board.convertCoordinatesToName(coords.get()[0], coords.get()[1]);
          if (this.currentEngineN == EngineManager.engineGameInfo.blackEngineIndex) {

            if (!Lizzie.engineManager
                .engineList
                .get(EngineManager.engineGameInfo.whiteEngineIndex)
                .playMoveGenmove("B", coordsString)) {
              return;
            }
            Lizzie.engineManager
                .engineList
                .get(EngineManager.engineGameInfo.whiteEngineIndex)
                .genmoveForPk("W");
            if (!Lizzie.config.enginePkPonder)
              Lizzie.engineManager
                  .engineList
                  .get(EngineManager.engineGameInfo.blackEngineIndex)
                  .nameCmdfornoponder();
            Lizzie.leelaz =
                Lizzie.engineManager.engineList.get(EngineManager.engineGameInfo.blackEngineIndex);

          } else {
            if (!Lizzie.engineManager
                .engineList
                .get(EngineManager.engineGameInfo.blackEngineIndex)
                .playMoveGenmove("W", coordsString)) {
              return;
            }
            Lizzie.engineManager
                .engineList
                .get(EngineManager.engineGameInfo.blackEngineIndex)
                .genmoveForPk("B");
            if (!Lizzie.config.enginePkPonder)
              Lizzie.engineManager
                  .engineList
                  .get(EngineManager.engineGameInfo.whiteEngineIndex)
                  .nameCmdfornoponder();
            Lizzie.leelaz =
                Lizzie.engineManager.engineList.get(EngineManager.engineGameInfo.whiteEngineIndex);
          }
          return;
        }
      }
      if (isCheckingName) {
        pkMoveStartTime = System.currentTimeMillis();
        isCheckingName = false;
        // isReadyForGenmoveGame =true;
        isKataGoPda = false;
        if (params[1].toLowerCase().startsWith("zen")) this.isZen = true;
        if (params[1].toLowerCase().startsWith("llzero")) {
          this.noLcb = true;
          this.isLeela = true;
          canAddPlayer = true;
        }
        if (params[1].toLowerCase().startsWith("leela")
            && params.length > 2
            && params[2].toLowerCase().startsWith("zero")) {
          this.isLeela = true;
          canAddPlayer = true;
        }
        if (params[1].equals("Leela") && params.length == 2) {
          isLeela0110 = true;
          isLoaded = true;
        }
        if (params[1].toLowerCase().startsWith("sai")) this.isSai = true;
        //				if (params[1].startsWith("KataGoYm"))
        //					sendCommandToLeelazWithOutLog("lizzie_use");
        if (params[1].startsWith("KataGo")) {
          canAddPlayer = true;
          if (params[1].startsWith("KataGoPda")) {
            isKatagoCustom = true;
            isCheckingPda = true;
            isKataGoPda = true;
            sendCommand("getpda");
            sendCommand("getdympdacap");
            Runnable runnable =
                new Runnable() {
                  public void run() {
                    try {
                      Thread.sleep(5000);
                    } catch (InterruptedException e) {
                      // TODO Auto-generated catch block
                      e.printStackTrace();
                    }
                    isCheckingPda = false;
                  }
                };
            Thread thread = new Thread(runnable);
            thread.start();
          }
          setKataEnginePara();
          if (Lizzie.config.autoLoadKataRules)
            sendCommand("kata-set-rules " + Lizzie.config.kataRules);
          getParameterScadule(true);
          this.isKatago = true;
          if (params[1].startsWith("KataGoCustom")) isKatagoCustom = true;
          this.version = 17;
          isCheckingVersion = false;

          if (this.currentEngineN == EngineManager.currentEngineNo) {
            Lizzie.config.leelaversion = version;
          }
          //	isLoaded = true;
          // Lizzie.frame.menu.showWRNandPDA(true);
        } else {
          isKatago = false;
          setLeelaSaiEnginePara();
          // Lizzie.frame.menu.showWRNandPDA(false);
        }
      } else if (isCheckingVersion && !isKatago && !isLeela0110) {
        String[] ver = params[1].split("\\.");
        try {
          int minor = Integer.parseInt(ver[1]);
          // Gtp support added in version 15
          version = minor;
          if (version == 15) canAddPlayer = false;
        } catch (Exception ex) {
          version = 17;
        }
        if (this.currentEngineN == EngineManager.currentEngineNo) {
          Lizzie.config.leelaversion = version;
        }
        if (version == 7) {
          version = 17;
        }
        isCheckingVersion = false;
        //	isLoaded = true;
      }
    } else if (line.startsWith("?")) {
      isCommandLine = true;
    }

    if (Lizzie.gtpConsole.isVisible() || Lizzie.config.alwaysGtp)
      Lizzie.gtpConsole.addLine(line + "\n");
    else if (line.startsWith("PDA:")) {
      parsePDALine(line);
    }
  }

  private void checkForGomokuFullBoard(boolean isGenmove) {
    // TODO Auto-generated method stub
    if (!Lizzie.config.noCapture) return;
    Stone[] stones = Lizzie.board.getData().stones;
    for (Stone stone : stones) {
      if (stone == Stone.EMPTY) return;
    }
    if (isGenmove) {
      pkMoveTime = System.currentTimeMillis() - pkMoveStartTime;
      pkMoveTimeGame = pkMoveTimeGame + pkMoveTime;
      outOfMoveNum = true;
      nameCmdfornoponder();
      genmoveResign(false);
    } else {
      outOfMoveNum = true;
      resigned = true;
    }
  }

  private void parseLine(String line) {
    // System.out.println(line);
    synchronized (this) {
      if (line.startsWith("info")) {
        if ((isResponseUpToDate())) {
          if (EngineManager.isEngineGame) {
            // Lizzie.frame.subBoardRenderer.reverseBestmoves = false;
            // Lizzie.frame.boardRenderer.reverseBestmoves = false;
            if (Lizzie.config.enginePkPonder) {
              if ((Lizzie.board.getHistory().isBlacksTurn()
                      && this
                          == Lizzie.engineManager.engineList.get(
                              EngineManager.engineGameInfo.blackEngineIndex))
                  || !Lizzie.board.getHistory().isBlacksTurn()
                      && this
                          == Lizzie.engineManager.engineList.get(
                              EngineManager.engineGameInfo.whiteEngineIndex)) {
                Lizzie.leelaz = this;
              }
            } else Lizzie.leelaz = this;
          }
          // Clear switching prompt
          // switching = false;

          // Display engine command in the title
          Lizzie.frame.updateTitle();

          // This should not be stale data when the command number match
          if (isKatago) {
            this.bestMoves = parseInfoKatago(line.substring(5));
          } else if (isSai) {
            this.bestMoves = parseInfoSai(line.substring(5));
          } else {
            this.bestMoves = parseInfo(line.substring(5));
          }
          if (!this.bestMoves.isEmpty()) {
            notifyAutoPK(false);
            notifyAutoPlay(false);
            if (Lizzie.config.isAutoAna) {
              if (Lizzie.frame.isAutoAnalyzingDiffNode) {
                nofityDiffAna();
              } else if (Lizzie.config.analyzeAllBranch) {
                notifyAutoAnaAllBranch();
              } else {
                notifyAutoAna();
              }
            }
          }
          if (!EngineManager.isEngineGame || (!played && this == Lizzie.leelaz))
            Lizzie.frame.refresh(1);
          // don't follow the maxAnalyzeTime rule if we are in game
          if (!Lizzie.frame.isPlayingAgainstLeelaz
              && !Lizzie.frame.isAnaPlayingAgainstLeelaz
              && !EngineManager.isEngineGame
              && !Lizzie.config.isAutoAna) {
            if (!outOfPlayoutsLimit
                && ((Lizzie.config.limitPlayout
                        && getBestMovesPlayouts() > Lizzie.config.limitPlayouts)
                    || (Lizzie.config.stopAtEmptyBoard
                        && Lizzie.board.getHistory().noStoneBoard()))) {
              stopByLimit = true;
              stopByPlayouts = true;
              isPondering = !isPondering;
              nameCmd();
              if (!Lizzie.config.stopAtEmptyBoard && !Lizzie.board.getHistory().noStoneBoard()) {
                showStopPonderTips();
              }
            } else if ((Lizzie.config.limitTime
                && (System.currentTimeMillis() - startPonderTime)
                    > Lizzie.config.maxAnalyzeTimeMillis)) {
              stopByLimit = true;
              isPondering = !isPondering;
              nameCmd();
              showStopPonderTips();
            }
          }
          this.canCheckAlive = true;
        } else {
          if (Lizzie.config.isAutoAna) {
            bestMoves = new ArrayList<>();
            currentTotalPlayouts = 0;
          }
          if (Lizzie.config.isAutoAna)
            Lizzie.board.getHistory().getCurrentHistoryNode().getData().tryToClearBestMoves();
        }
        return;
      } else {
        if (Lizzie.gtpConsole.isVisible() || Lizzie.config.alwaysGtp || !this.isLoaded)
          Lizzie.gtpConsole.addLine(line + "\n");
      }
      //			if (Lizzie.engineManager.isEngineGame && this.isPondering) {
      //				Lizzie.engineManager.startInfoTime = System.currentTimeMillis();
      //			}
      if (isCheckingPda) {
        if (line.startsWith("pda:")) {
          isDymPda = true;
          String[] params = line.trim().split(" ");
          if (params.length == 2) {
            pda = Double.parseDouble(params[1]);
            LizzieFrame.menu.txtPDA.setText(String.format(Locale.ENGLISH, "%.3f", pda));
            if (LizzieFrame.menu.setPda != null)
              LizzieFrame.menu.setPda.curPDA.setText(String.format(Locale.ENGLISH, "%.3f", pda));
            if (Lizzie.config.chkAutoPDA) {
              sendCommand(Lizzie.config.AutoPDA);
              if (Lizzie.config.chkDymPDA) {
                this.pdaCap = Double.parseDouble(Lizzie.config.dymPDACap.trim());
                if (LizzieFrame.menu.setPda != null)
                  LizzieFrame.menu.setPda.txtDymCap.setText(Lizzie.config.dymPDACap);
              }
              if (Lizzie.config.chkStaticPDA) {
                LizzieFrame.menu.txtPDA.setText(Lizzie.config.staticPDAcur);
                isStaticPda = true;
                this.pda = Double.parseDouble(Lizzie.config.staticPDAcur.trim());
              } else {
                isStaticPda = false;
              }
            }
          }
          if (!EngineManager.isEngineGame && this == Lizzie.leelaz) ponder();
        }
        if (line.startsWith("PDACap:")) {
          String[] params = line.trim().split(" ");
          if (params.length == 2) {
            // if(pdaCap==0)
            pdaCap = Double.parseDouble(params[1]);
            if (pdaCap != 0 && !isStaticPda) {
              isStaticPda = false;
              Runnable syncDymPda =
                  new Runnable() {
                    public void run() {
                      int i = 0;
                      while (!canRestoreDymPda) {
                        try {
                          i++;
                          if (i > 19) break;
                          Thread.sleep(50);
                        } catch (InterruptedException e) {
                          // TODO Auto-generated catch block
                          e.printStackTrace();
                        }
                      }
                      canRestoreDymPda = false;
                      if (Lizzie.config.chkAutoPDA) sendCommand(Lizzie.config.AutoPDA);
                      else sendCommand("dympdacap " + pdaCap);
                      if (isPondering() || Lizzie.config.isDoubleEngineMode()) ponder();
                    }
                  };
              Thread syncDymPdaTh = new Thread(syncDymPda);
              syncDymPdaTh.start();
            } else {
              isStaticPda = true;
            }
            if (LizzieFrame.menu.setPda != null)
              LizzieFrame.menu.setPda.txtDymCap.setText(String.valueOf(pdaCap));
          }
        }
      }
      if (this.isKatago) {
        if (line.startsWith("PDA:")) {
          parsePDALine(line);
        }
      } else {
        if (line.startsWith("| ST")) {
          String[] params = line.trim().split(" ");
          if (params.length == 13) {
            isColorEngine = true;
            if (Lizzie.gtpConsole.isVisible() || Lizzie.config.alwaysGtp)
              Lizzie.gtpConsole.addLine(oriEnginename + ": " + line);
            stage = Integer.parseInt(params[3].substring(0, params[3].length() - 1));
            komi = Float.parseFloat(params[6].substring(0, params[6].length() - 1));
          }
        }
      }
      // if (!this.isScreen&&line.startsWith("play")) {
      if (line.startsWith("play")) {
        // In lz-genmove_analyze
        String[] params = line.trim().split(" ");
        if (isInputCommand) {
          //	getGenmoveInfoPrevious = true;
          Lizzie.board.place(params[1]);
          if (isPondering) ponder();
          else {
            nameCmdfornoponder();
          }
          if (Lizzie.frame.isAutocounting) {
            String command =
                "play " + (Lizzie.board.getHistory().isBlacksTurn() ? "w " : "b ") + params[1];
            Lizzie.frame.zen.sendAndEstimate(command, false);
          }
        }
        if (Lizzie.frame.isPlayingAgainstLeelaz && isResponseUpToDate()) {
          if (params.length > 1) {
            if (params[1].startsWith("resign")) {
              if (Lizzie.frame.playerIsBlack) {

                if (msg == null || !msg.isVisible()) {
                  msg = new Message();
                  msg.setMessage(Lizzie.resourceBundle.getString("Leelaz.blackWinAiResign"));
                  //     msg.setVisible(true);
                }
                GameInfo gameInfo = Lizzie.board.getHistory().getGameInfo();
                gameInfo.setResult(Lizzie.resourceBundle.getString("Leelaz.blackWin"));
                Lizzie.frame.setResult(Lizzie.resourceBundle.getString("Leelaz.blackWin"));

              } else {
                if (msg == null || !msg.isVisible()) {
                  msg = new Message();
                  msg.setMessage(Lizzie.resourceBundle.getString("Leelaz.whiteWinAiResign"));
                  //     msg.setVisible(true);
                }
                GameInfo gameInfo = Lizzie.board.getHistory().getGameInfo();
                gameInfo.setResult(Lizzie.resourceBundle.getString("Leelaz.whiteWin"));
                Lizzie.frame.setResult(Lizzie.resourceBundle.getString("Leelaz.whiteWin"));
              }
              togglePonder();
              return;
            }

            if (params[1].startsWith("pass")) {
              // getGenmoveInfoPrevious = true;
              Lizzie.board.pass();
              LizzieFrame.menu.toggleEngineMenuStatus(false, false);
            } else {
              // getGenmoveInfoPrevious = true;
              Lizzie.board.place(params[1]);
              LizzieFrame.menu.toggleEngineMenuStatus(false, false);
            }
          }
          if (Lizzie.frame.isAutocounting) {
            String command =
                "play " + (Lizzie.board.getHistory().isBlacksTurn() ? "w " : "b ") + params[1];
            Lizzie.frame.zen.sendAndEstimate(command, false);
          }
          if (!Lizzie.config.playponder) Lizzie.leelaz.nameCmdfornoponder();
        }
        if (!isInputCommand && params.length == 2) {
          isPondering = false;
        }
        isThinking = false;
        if (isInputCommand) {
          isInputCommand = false;
        }
      } else if (line.startsWith("=")) {
        isCommandLine = true;
        if (startGetCommandList) {
          startGetCommandList = false;
          endGetCommandList = true;
        }
        String[] params = line.trim().split(" ");
        if (params.length == 1) return;
        if (!endGetCommandList && params.length == 2 && params[1].equals("protocol_version")) {
          startGetCommandList = true;
        }
        if (isInputCommand) {
          //	getGenmoveInfoPrevious = true;
          Lizzie.board.place(params[1]);
          if (isPondering) ponder();
          else this.nameCmdfornoponder();
          if (Lizzie.frame.isAutocounting) {
            String command =
                "play " + (Lizzie.board.getHistory().isBlacksTurn() ? "w " : "b ") + params[1];
            Lizzie.frame.zen.sendAndEstimate(command, false);
          }
          isInputCommand = false;
          isThinking = false;
        }
        if (isSettingHandicap) {
          bestMoves = new ArrayList<>();
          currentTotalPlayouts = 0;
          Lizzie.board.hasStartStone = true;
          for (int i = 1; i < params.length; i++) {
            Optional<int[]> coordsOpt = Board.asCoordinates(params[i]);
            if (coordsOpt.isPresent()) {
              int[] coords = coordsOpt.get();
              Lizzie.board.getHistory().setStone(coords, Stone.BLACK);
              Lizzie.board.getHistory().getData().blackToPlay = false;
              Lizzie.board.setStartListStone(coords, true);
            }
          }
          isSettingHandicap = false;
          Lizzie.frame.allowPlaceStone = true;
          if (Lizzie.frame.isAnaPlayingAgainstLeelaz) {
            if (Lizzie.config.UsePureNetInGame && !Lizzie.leelaz.isheatmap)
              Lizzie.leelaz.toggleHeatmap(false);
            Lizzie.leelaz.Pondering();
            if (Lizzie.config.playponder
                || (Lizzie.board.getHistory().isBlacksTurn() && !Lizzie.frame.playerIsBlack)
                || (!Lizzie.board.getHistory().isBlacksTurn() && Lizzie.frame.playerIsBlack)) {
              Lizzie.leelaz.ponder();
            }
          }
          Lizzie.frame.refresh();
        } else if (isThinking && !isPondering) {
          if (isInputCommand) {
            Lizzie.board.place(params[1]);
            togglePonder();
            if (Lizzie.frame.isAutocounting) {
              String command =
                  "play " + (Lizzie.board.getHistory().isBlacksTurn() ? "w " : "b ") + params[1];
              Lizzie.frame.zen.sendAndEstimate(command, false);
            }
          }
          if (Lizzie.frame.isPlayingAgainstLeelaz && isResponseUpToPreDate()) {
            if (params[1].startsWith("resign")) {
              if (Lizzie.frame.playerIsBlack) {

                if (msg == null || !msg.isVisible()) {
                  msg = new Message();
                  msg.setMessage(Lizzie.resourceBundle.getString("Leelaz.blackWinAiResign"));
                }
                GameInfo gameInfo = Lizzie.board.getHistory().getGameInfo();
                gameInfo.setResult(Lizzie.resourceBundle.getString("Leelaz.blackWin"));
                Lizzie.frame.setResult(Lizzie.resourceBundle.getString("Leelaz.blackWin"));

              } else {
                if (msg == null || !msg.isVisible()) {
                  msg = new Message();
                  msg.setMessage(Lizzie.resourceBundle.getString("Leelaz.whiteWinAiResign"));
                }
                GameInfo gameInfo = Lizzie.board.getHistory().getGameInfo();
                gameInfo.setResult(Lizzie.resourceBundle.getString("Leelaz.whiteWin"));
                Lizzie.frame.setResult(Lizzie.resourceBundle.getString("Leelaz.whiteWin"));
              }
              togglePonder();
              return;
            }

            if (params[1].startsWith("pass")) {
              Lizzie.board.pass();
              LizzieFrame.menu.toggleEngineMenuStatus(false, false);
            } else {
              Lizzie.board.place(params[1]);
              LizzieFrame.menu.toggleEngineMenuStatus(false, false);
            }
            if (Lizzie.frame.isAutocounting) {
              String command =
                  "play " + (Lizzie.board.getHistory().isBlacksTurn() ? "w " : "b ") + params[1];
              Lizzie.frame.zen.sendAndEstimate(command, false);
            }
            if (!Lizzie.config.playponder) Lizzie.leelaz.nameCmdfornoponder();
          }
          isThinking = false;
          if (isInputCommand) {
            isInputCommand = false;
          }
        }

        if (isCheckingName) {
          noAnalyze = false;
          isCheckingName = false;
          isKataGoPda = false;
          pkMoveStartTime = System.currentTimeMillis();
          if (params[1].toLowerCase().startsWith("golaxy")) requireResponseBeforeSend = true;
          else requireResponseBeforeSend = false;
          if (params[1].toLowerCase().startsWith("zen")) this.isZen = true;
          if (params[1].toLowerCase().startsWith("llzero")) {
            this.noLcb = true;
            canAddPlayer = true;
          }
          if (params[1].toLowerCase().startsWith("sai")) this.isSai = true;
          if (params[1].toLowerCase().startsWith("leela")
              && params.length > 2
              && params[2].toLowerCase().startsWith("zero")) {
            this.isLeela = true;
            canAddPlayer = true;
          }
          if (params[1].equals("Leela") && params.length == 2) {
            isLeela0110 = true;
            isLoaded = true;
          }
          //						if (params[1].startsWith("KataGoYm"))
          //							sendCommandToLeelazWithOutLog("lizzie_use");
          if (params[1].startsWith("KataGo")) {
            canAddPlayer = true;
            if (Lizzie.config.firstLoadKataGo) {
              Lizzie.config.firstLoadKataGo = false;
              SwingUtilities.invokeLater(
                  new Runnable() {
                    public void run() {
                      Utils.showHtmlMessage(
                          Lizzie.resourceBundle.getString("Message.title"),
                          Lizzie.resourceBundle.getString("Leelaz.kataGoPerformance"),
                          Lizzie.frame);
                    }
                  });
              Lizzie.config.uiConfig.put("first-load-katago", Lizzie.config.firstLoadKataGo);
            }
            if (params[1].startsWith("KataGoPda")) {
              isKatagoCustom = true;
              isCheckingPda = true;
              isKataGoPda = true;
              sendCommand("getpda");
              sendCommand("getdympdacap");
              Runnable runnable =
                  new Runnable() {
                    public void run() {
                      try {
                        Thread.sleep(5000);
                      } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                      }
                      isCheckingPda = false;
                    }
                  };
              Thread thread = new Thread(runnable);
              thread.start();
            }
            setKataEnginePara();
            if (Lizzie.config.autoLoadKataRules)
              sendCommand("kata-set-rules " + Lizzie.config.kataRules);
            getParameterScadule(true);
            this.isKatago = true;
            if (params[1].startsWith("KataGoCustom")) isKatagoCustom = true;
            this.version = 17;
            isCheckingVersion = false;

            if (this.currentEngineN == EngineManager.currentEngineNo) {
              Lizzie.config.leelaversion = version;
            }
            isLoaded = true;
            isTuning = false;
            if (Lizzie.leelaz2 != null && this == Lizzie.leelaz2) {
              if (currentEngineN > 20) LizzieFrame.menu.changeEngineIcon2(20, 2);
              else LizzieFrame.menu.changeEngineIcon2(currentEngineN, 2);
            } else {
              if (currentEngineN > 20) LizzieFrame.menu.changeEngineIcon(20, 2);
              else LizzieFrame.menu.changeEngineIcon(currentEngineN, 2);
            }
          } else {
            isKatago = false;
            setLeelaSaiEnginePara();
          }
          if (params[1].toLowerCase().startsWith("katajigo")) {
            this.isKatago = true;
            this.noAnalyze = true;
          }
        } else if (isCheckingVersion && !isKatago && !isLeela0110) {
          String[] ver = params[1].split("\\.");
          try {
            int minor = Integer.parseInt(ver[1]);
            // Gtp support added in version 15
            version = minor;
            if (version == 15) canAddPlayer = false;
          } catch (Exception ex) {
            version = 17;
          }
          if (this.currentEngineN == EngineManager.currentEngineNo) {
            Lizzie.config.leelaversion = version;
          }
          if (version == 7) {
            version = 17;
          }
          isCheckingVersion = false;
          isLoaded = true;
          isTuning = false;
          // Lizzie.initializeAfterVersionCheck();
          if (Lizzie.leelaz2 != null && this == Lizzie.leelaz2) {
            if (currentEngineN > 20) LizzieFrame.menu.changeEngineIcon2(20, 2);
            else LizzieFrame.menu.changeEngineIcon2(currentEngineN, 2);

          } else {
            if (currentEngineN > 20) LizzieFrame.menu.changeEngineIcon(20, 2);
            else LizzieFrame.menu.changeEngineIcon(currentEngineN, 2);
          }
        }
      } else if (line.startsWith("?")) {
        isCommandLine = true;
      }
      parseHeatMap(line);
    }
  }

  private void parsePDALine(String line) {
    String[] params = line.trim().split(" ");
    if (params.length == 2) {
      pda = Double.parseDouble(params[1]);
      LizzieFrame.menu.txtPDA.setText(String.format(Locale.ENGLISH, "%.3f", pda));
      if (LizzieFrame.menu.setPda != null)
        LizzieFrame.menu.setPda.curPDA.setText(String.format(Locale.ENGLISH, "%.3f", pda));
    }
  }

  private void showStopPonderTips() {
    // TODO Auto-generated method stub
    if (!Lizzie.config.showPonderLimitedTips) return;
    if (!showStopTips) return;
    showStopTips = false;
    Box box = Box.createVerticalBox();
    JFontLabel label = new JFontLabel(Lizzie.resourceBundle.getString("leelaz.stopByLimit"));
    label.setAlignmentX(Component.LEFT_ALIGNMENT);
    box.add(label);
    Utils.addFiller(box, 5, 5);
    Utils.addFiller(box, 5, 5);
    JFontLabel label2 = new JFontLabel(Lizzie.resourceBundle.getString("leelaz.stopByLimit2"));
    label2.setAlignmentX(Component.LEFT_ALIGNMENT);
    box.add(label2);
    Utils.addFiller(box, 5, 5);
    JFontCheckBox disableCheckBox =
        new JFontCheckBox(Lizzie.resourceBundle.getString("LizzieFrame.noNoticeAgain"));
    disableCheckBox.setAlignmentX(Component.LEFT_ALIGNMENT);
    box.add(disableCheckBox);
    JOptionPane optionPane = new JOptionPane(box, JOptionPane.INFORMATION_MESSAGE);
    JDialog dialog =
        optionPane.createDialog(
            Lizzie.frame, Lizzie.resourceBundle.getString("leelaz.stopByLimitTitle"));
    dialog.setVisible(true);
    if (disableCheckBox.isSelected()) {
      Lizzie.config.showPonderLimitedTips = false;
      Lizzie.config.uiConfig.put("show-ponder-limited-tips", Lizzie.config.showPonderLimitedTips);
    }
    dialog.dispose();
  }

  private void notifyAutoPlay(boolean playImmediately) {
    if (this != Lizzie.leelaz) return;
    if (LizzieFrame.toolbar.isAutoPlay) {
      if ((Lizzie.board.getHistory().isBlacksTurn()
              && LizzieFrame.toolbar.chkAutoPlayBlack.isSelected())
          || (!Lizzie.board.getHistory().isBlacksTurn()
              && LizzieFrame.toolbar.chkAutoPlayWhite.isSelected())) {
        int time = 0;
        int playouts = 0;
        int firstPlayouts = 0;
        if (LizzieFrame.toolbar.chkAutoPlayTime.isSelected()) {
          try {
            time =
                1000
                    * Integer.parseInt(
                        LizzieFrame.toolbar.txtAutoPlayTime.getText().replace(" ", ""));
          } catch (NumberFormatException err) {
          }
        }
        if (LizzieFrame.toolbar.chkAutoPlayPlayouts.isSelected()) {
          try {
            playouts =
                Integer.parseInt(
                    LizzieFrame.toolbar.txtAutoPlayPlayouts.getText().replace(" ", ""));
          } catch (NumberFormatException err) {
          }
        }
        if (LizzieFrame.toolbar.chkAutoPlayFirstPlayouts.isSelected()) {
          try {
            firstPlayouts =
                Integer.parseInt(
                    LizzieFrame.toolbar.txtAutoPlayFirstPlayouts.getText().replace(" ", ""));
          } catch (NumberFormatException err) {
          }
        }
        boolean playNow = false;
        if (playImmediately) playNow = true;
        if (firstPlayouts > 0) {
          if (bestMoves.get(0).playouts >= firstPlayouts) {
            playNow = true;
          }
        }
        if (playouts > 0) {
          if (currentTotalPlayouts >= playouts) {
            playNow = true;
          }
        }

        if (time > 0) {
          if (System.currentTimeMillis() - startPonderTime >= time) {
            playNow = true;
          }
        }
        if (playNow) {
          notifyAnaResign(false);
          MoveData playMove = null;
          if (!Lizzie.frame.bothSync
              && Lizzie.config.enableAnaGameRamdonStart
              && Lizzie.board.getHistory().getMoveNumber() <= Lizzie.config.anaGameRandomMove)
            playMove = this.randomBestmove(bestMoves, Lizzie.config.anaGameRandomWinrateDiff, true);
          else playMove = bestMoves.get(0);

          int coords[] = Board.convertNameToCoordinates(playMove.coordinate);
          Lizzie.board.place(coords[0], coords[1]);
          if ((Lizzie.board.getData().blackToPlay
                  && LizzieFrame.toolbar.chkAutoPlayBlack.isSelected())
              || (!Lizzie.board.getData().blackToPlay
                  && LizzieFrame.toolbar.chkAutoPlayWhite.isSelected())) {
            Lizzie.board.place(coords[0], coords[1]);
          }
          if (Lizzie.frame.bothSync) {
            if (!Lizzie.config.readBoardPonder) nameCmd();
            else ponder();
          } else if (!Lizzie.config.playponder) {
            nameCmd();
          } else ponder();
        }
      }
    }
  }

  private void notifyAnaResign(boolean isResgined) {
    // TODO Auto-generated method stub
    if (isResgined) {
      Lizzie.frame.togglePonderMannul();
      Utils.showMsg(oriEnginename + " " + Lizzie.resourceBundle.getString("Leelaz.resign"));
    } else if (Lizzie.frame.isAnaPlayingAgainstLeelaz && !Lizzie.frame.bothSync) {
      if (Lizzie.board.getHistory().getMoveNumber() >= Lizzie.config.anaGameResignStartMove) {
        if (bestMoves.get(0).winrate < Lizzie.config.anaGameResignPercent) {
          this.anaGameResignCount++;
        } else this.anaGameResignCount = 0;
      }
      if (this.anaGameResignCount >= Lizzie.config.anaGameResignMove) {
        Lizzie.frame.togglePonderMannul();
        Utils.showMsg(oriEnginename + " " + Lizzie.resourceBundle.getString("Leelaz.resign"));
        return;
      }
    }
  }

  public void analyzeNextMove(boolean isLastMove) {
    autoAnalysed = true;
    Lizzie.board.getHistory().getCurrentHistoryNode().analyzed = true;
    bestMoves = new ArrayList<>();
    currentTotalPlayouts = 0;
    if (isLastMove) {
      LizzieFrame.toolbar.stopAutoAna(true, false);
    } else {
      Lizzie.board.nextMove(true);
    }
  }

  private void nofityDiffAna() {
    // TODO Auto-generated method stub
    if (this != Lizzie.leelaz) return;
    if (Lizzie.config.autoAnaDiffFirstPlayouts > 0) {
      if (!bestMoves.isEmpty()
          && bestMoves.get(0).playouts >= Lizzie.config.autoAnaDiffFirstPlayouts) {
        Lizzie.board.getHistory().getCurrentHistoryNode().diffAnalyzed = true;
        return;
      }
    }
    if ((isZen && Lizzie.board.getHistory().getCurrentHistoryNode().getData().moveNumber < 3)) {
      Lizzie.board.getHistory().getCurrentHistoryNode().diffAnalyzed = true;
      return;
    }
    if (Lizzie.config.autoAnaDiffPlayouts > 0) {
      if (currentTotalPlayouts >= Lizzie.config.autoAnaDiffPlayouts) {
        Lizzie.board.getHistory().getCurrentHistoryNode().diffAnalyzed = true;
        return;
      }
    }

    if (Lizzie.config.autoAnaDiffTime > 0) {
      long curTime = System.currentTimeMillis();
      if (curTime - startPonderTime >= Lizzie.config.autoAnaDiffTime * 1000) {
        Lizzie.board.getHistory().getCurrentHistoryNode().diffAnalyzed = true;
        return;
      }
    }
  }

  public void notifyAutoAnaAllBranch() {
    if (this != Lizzie.leelaz) return;
    if (Lizzie.board.getHistory().isBlacksTurn() && !Lizzie.config.anaBlack) {
      Lizzie.board.getHistory().getCurrentHistoryNode().analyzed = true;
      return;
    }
    if (!Lizzie.board.getHistory().isBlacksTurn() && !Lizzie.config.anaWhite) {
      Lizzie.board.getHistory().getCurrentHistoryNode().analyzed = true;
      return;
    }
    if (Lizzie.config.autoAnaFirstPlayouts > 0) {
      if (!bestMoves.isEmpty() && bestMoves.get(0).playouts >= Lizzie.config.autoAnaFirstPlayouts) {
        Lizzie.board.getHistory().getCurrentHistoryNode().analyzed = true;
        autoAnalysed = true;
        return;
      }
    }
    if ((isZen && Lizzie.board.getHistory().getCurrentHistoryNode().getData().moveNumber < 3)) {
      Lizzie.board.getHistory().getCurrentHistoryNode().analyzed = true;
      autoAnalysed = true;
      return;
    }
    if (Lizzie.config.autoAnaPlayouts > 0) {
      if (currentTotalPlayouts >= Lizzie.config.autoAnaPlayouts) {
        Lizzie.board.getHistory().getCurrentHistoryNode().analyzed = true;
        autoAnalysed = true;
        return;
      }
    }

    if (Lizzie.config.autoAnaTime > 0) {
      long curTime = System.currentTimeMillis();
      if (curTime - startPonderTime >= Lizzie.config.autoAnaTime) {
        Lizzie.board.getHistory().getCurrentHistoryNode().analyzed = true;
        autoAnalysed = true;
        return;
      }
    }
  }

  public void notifyAutoAna() {
    if (this != Lizzie.leelaz) return;
    if (Lizzie.config.autoAnaEndMove != -1) {
      if (Lizzie.config.autoAnaEndMove < Lizzie.board.getHistory().getData().moveNumber) {
        LizzieFrame.toolbar.stopAutoAna(true, false);
        return;
      }
    }
    boolean isLastMove = !Lizzie.board.getHistory().getNext().isPresent();
    if (Lizzie.board.getHistory().isBlacksTurn() && !Lizzie.config.anaBlack) {
      analyzeNextMove(isLastMove);
      return;
    }
    if (!Lizzie.board.getHistory().isBlacksTurn() && !Lizzie.config.anaWhite) {
      analyzeNextMove(isLastMove);
      return;
    }
    if (Lizzie.config.autoAnaFirstPlayouts > 0) {
      if (!bestMoves.isEmpty() && bestMoves.get(0).playouts >= Lizzie.config.autoAnaFirstPlayouts) {
        analyzeNextMove(isLastMove);
        return;
      }
    }
    if ((isZen && Lizzie.board.getHistory().getCurrentHistoryNode().getData().moveNumber < 3)) {
      analyzeNextMove(isLastMove);
      return;
    }
    if (Lizzie.config.autoAnaPlayouts > 0) {
      if (currentTotalPlayouts >= Lizzie.config.autoAnaPlayouts) {
        analyzeNextMove(isLastMove);
        return;
      }
    }

    if (Lizzie.config.autoAnaTime > 0) {
      long curTime = System.currentTimeMillis();
      if (curTime - startPonderTime >= Lizzie.config.autoAnaTime) {
        analyzeNextMove(isLastMove);
        return;
      }
    }
  }

  public void genmoveResign(boolean needPass) {
    // if(resigned)
    //	return;
    if (!bestMoves.isEmpty()) {
      currentTotalPlayouts = MoveData.getPlayouts(bestMoves);
      Lizzie.board
          .getHistory()
          .getData()
          .tryToSetBestMoves(bestMoves, bestMovesEnginename, true, currentTotalPlayouts);
    }
    this.resigned = true;
    if (!this.doublePass
        && !this.outOfMoveNum
        && (Lizzie.gtpConsole.isVisible() || Lizzie.config.alwaysGtp))
      Lizzie.gtpConsole.addLine(
          oriEnginename + " " + Lizzie.resourceBundle.getString("Leelaz.resign") + "\n");
    Lizzie.board.updateComment();
    if (needPass) Lizzie.board.pass();
    Lizzie.engineManager.stopEngineGame(currentEngineN, false);
  }

  //	public void resignGame() {
  //		if (!resigned || isResigning)
  //			return;
  //		isResigning = true;
  //		if(Lizzie.gtpConsole.isVisible()||Lizzie.config.alwaysGtp)
  //		Lizzie.gtpConsole.addLine(oriEnginename+ resourceBundle.getString("Leelaz.resign")+"\n");
  //	Lizzie.engineManager.stopEngineGame(currentEngineN, false);
  //	}

  private void notifyAutoPK(boolean playImmediately) {
    if (!EngineManager.isEngineGame || played || LizzieFrame.toolbar.isPkStop) {
      return;
    }
    if (resigned) {
      nameCmd();
      isResigning = true;
      if (Lizzie.gtpConsole.isVisible() || Lizzie.config.alwaysGtp)
        Lizzie.gtpConsole.addLine(
            oriEnginename + " " + Lizzie.resourceBundle.getString("Leelaz.resign") + "\n");
      Lizzie.engineManager.stopEngineGame(currentEngineN, false);
      return;
    }
    boolean shouldPlay = false;
    int time = 0;
    int playouts = 0;
    int firstPlayouts = 0;
    int minMove = 0;
    int resginMoveCounts = 2;
    double resignWinrate = 10.0;
    MoveData best;
    try {
      best = this.bestMoves.get(0);
    } catch (Exception e) {
      e.printStackTrace();
      return;
    }
    double curWR = best.winrate;
    int thisIdx = currentEngineN;
    int blackIdx = EngineManager.engineGameInfo.blackEngineIndex;
    // int whiteIdx=Lizzie.engineManager.engineGameInfo.whiteEngineIndex;
    boolean isBlackEngine = thisIdx == blackIdx;
    if (isBlackEngine) {
      time = EngineManager.engineGameInfo.timeBlack * 1000;
      playouts = EngineManager.engineGameInfo.playoutsBlack;
      firstPlayouts = EngineManager.engineGameInfo.firstPlayoutsBlack;
      minMove = EngineManager.engineGameInfo.blackMinMove;
      resginMoveCounts = EngineManager.engineGameInfo.blackResignMoveCounts;
      resignWinrate = EngineManager.engineGameInfo.blackResignWinrate;
    } else {
      time = EngineManager.engineGameInfo.timeWhite * 1000;
      playouts = EngineManager.engineGameInfo.playoutsWhite;
      firstPlayouts = EngineManager.engineGameInfo.firstPlayoutsWhite;
      minMove = EngineManager.engineGameInfo.whiteMinMove;
      resginMoveCounts = EngineManager.engineGameInfo.whiteResignMoveCounts;
      resignWinrate = EngineManager.engineGameInfo.whiteResignWinrate;
    }

    if (Lizzie.board.getHistory().getMoveNumber() > EngineManager.engineGameInfo.maxGameMoves) {
      outOfMoveNum = true;
      resigned = true;
    }
    if (isZen) {
      if (Lizzie.board.getHistory().getCurrentHistoryNode().getData().moveNumber < 3)
        shouldPlay = true;
    }
    if (playImmediately || playNow) shouldPlay = true;
    if (firstPlayouts > 0 && best.playouts >= firstPlayouts) shouldPlay = true;
    if (firstPlayouts > 0 && best.playouts >= firstPlayouts) shouldPlay = true;
    if (playouts > 0) {
      if (currentTotalPlayouts >= playouts) shouldPlay = true;
    }
    if (time > 0) {
      if (System.currentTimeMillis() - startPonderTime >= time) {
        shouldPlay = true;
      }
    }
    if (shouldPlay) {
      played = true;
      playNow = false;
      if ((curWR < resignWinrate) && Lizzie.board.getHistory().getMoveNumber() > minMove) {
        if (isBlackEngine) {
          blackResignMoveCounts = blackResignMoveCounts + 1;
          if (blackResignMoveCounts >= resginMoveCounts) resigned = true;
        } else {
          whiteResignMoveCounts = whiteResignMoveCounts + 1;
          if (whiteResignMoveCounts >= resginMoveCounts) resigned = true;
        }
      } else {
        if (isBlackEngine) blackResignMoveCounts = 0;
        else whiteResignMoveCounts = 0;
      }
      if (!resigned) {
        MoveData playMove = null;
        if (LizzieFrame.toolbar.isRandomMove
            && Lizzie.board.getHistory().getMoveNumber() <= LizzieFrame.toolbar.randomMove)
          playMove = this.randomBestmove(bestMoves, LizzieFrame.toolbar.randomDiffWinrate, false);
        else playMove = best;
        int coords[] = Board.convertNameToCoordinates(playMove.coordinate);
        if (coords[0] >= 0 && coords[1] >= 0) {
          Lizzie.board.place(coords[0], coords[1]);
        } else {
          if (!Lizzie.board.getLastMove().isPresent()) {
            doublePass = true;
            resigned = true;
          }
          Lizzie.board.pass();
        }
        if (!resigned) {
          if (isBlackEngine) {
            Lizzie.engineManager
                .engineList
                .get(EngineManager.engineGameInfo.blackEngineIndex)
                .playMoveNoPonder("B", playMove.coordinate);
            Lizzie.engineManager
                .engineList
                .get(EngineManager.engineGameInfo.whiteEngineIndex)
                .playMovePonder("B", playMove.coordinate);
          } else {
            Lizzie.engineManager
                .engineList
                .get(EngineManager.engineGameInfo.whiteEngineIndex)
                .playMoveNoPonder("W", playMove.coordinate);
            Lizzie.engineManager
                .engineList
                .get(EngineManager.engineGameInfo.blackEngineIndex)
                .playMovePonder("W", playMove.coordinate);
          }
        }
      }
      checkForGomokuFullBoard(false);
    }
    if (resigned) {
      nameCmd();
      isResigning = true;
      if (Lizzie.gtpConsole.isVisible() || Lizzie.config.alwaysGtp)
        Lizzie.gtpConsole.addLine(
            oriEnginename + " " + Lizzie.resourceBundle.getString("Leelaz.resign") + "\n");
      Lizzie.engineManager.stopEngineGame(currentEngineN, false);
    }
  }

  public void nameCmd() {
    if (isKatago) sendCommand("stop");
    else sendCommand("name");
    LizzieFrame.menu.toggleEngineMenuStatus(false, false);
  }

  public void boardSize(int width, int height) {
    if (width != height) sendCommand("rectangular_boardsize " + width + " " + height);
    else sendCommand("boardsize " + width);
    this.width = width;
    this.height = height;
    Lizzie.board.reopen(width, height);
    if (firstLoad) {
      Lizzie.board.getHistory().getGameInfo().setKomi(komi);
      Lizzie.board.getHistory().getGameInfo();
      GameInfo.DEFAULT_KOMI = (double) komi;
      firstLoad = false;
    }
  }

  public void komi(double komi) {
    synchronized (this) {
      sendCommand("komi " + (komi == 0.0 ? "0" : komi));
      Lizzie.board.getHistory().getGameInfo().setKomi(komi);
      //  Lizzie.board.getHistory().getGameInfo().changeKomi();
      Lizzie.board.clearBestMovesAfter(Lizzie.board.getHistory().getStart());
      if (isPondering) ponder();
    }
  }

  public void komiNoMenu(double komi) {
    synchronized (this) {
      sendCommand("komi " + (komi == 0.0 ? "0" : komi));
      Lizzie.board.getHistory().getGameInfo().setKomiNoMenu(komi);
      //  Lizzie.board.getHistory().getGameInfo().changeKomi();
      Lizzie.board.clearBestMovesAfter(Lizzie.board.getHistory().getStart());
      if (isPondering) ponder();
    }
  }

  public void nameCmdfornoponder() {
    if (isKatago) sendCommand("stop");
    else sendCommand("name");
  }

  private void readError() {
    String line = "";
    try {
      while ((line = errorStream.readLine()) != null) {
        try {
          parseLineForError(line);
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  private void parseLineForError(String line) {
    // TODO Auto-generated method stub
    if (!this.isLoaded) {
      if (line.toLowerCase().contains("cl_platform_not_found"))
        Utils.showMsg(Lizzie.resourceBundle.getString("Leelaz.openclPlatfromNotFound"));
    }
    if (!this.isLeela0110 || Lizzie.frame.isPlayingAgainstLeelaz)
      if (Lizzie.gtpConsole.isVisible() || Lizzie.config.alwaysGtp || !this.isLoaded)
        if (!line.startsWith("info")) Lizzie.gtpConsole.addErrorLine(line + "\n");
    if (isZen) {
      if ((EngineManager.isEngineGame && !EngineManager.engineGameInfo.isGenmove)
          || LizzieFrame.toolbar.isAutoPlay) {
        if ((isResponseUpToDate())) {
          if (line.contains("Nodes:")) {
            if (!this.bestMoves.isEmpty()) {
              notifyAutoPK(true);
              notifyAutoPlay(true);
            } else {
              if (EngineManager.isEngineGame) playPassInEngineGame();
              else Lizzie.board.pass();
            }
          } else if (line.contains("I pass")) {
            if (EngineManager.isEngineGame) playPassInEngineGame();
            else Lizzie.board.pass();
          } else if (line.toLowerCase().contains("resign")) {
            if (EngineManager.isEngineGame) {
              resigned = true;
              nameCmd();
              isResigning = true;
              if (Lizzie.gtpConsole.isVisible() || Lizzie.config.alwaysGtp)
                Lizzie.gtpConsole.addLine(
                    oriEnginename + " " + Lizzie.resourceBundle.getString("Leelaz.resign") + "\n");
              Lizzie.engineManager.stopEngineGame(currentEngineN, false);
            } else notifyAnaResign(true);
          }
        }
      }
      if (line.startsWith("info") && isLoaded) {
        isLoaded = false;
        SwingUtilities.invokeLater(
            new Runnable() {
              public void run() {
                Utils.showHtmlMessage(
                    Lizzie.resourceBundle.getString("Message.title"),
                    Lizzie.resourceBundle.getString("Leelaz.updateZenGtp"),
                    Lizzie.frame);
              }
            });
        shutdown();
      }
      if (EngineManager.isEngineGame && EngineManager.engineGameInfo.isGenmove) {
        if (line.contains("->")) {
          synchronized (bestMoves) {
            try {
              MoveData mv = MoveData.fromSummaryZen(line);
              if (mv != null) {
                mv.order = bestMoves.size();
                bestMoves.add(mv);
              }
            } catch (Exception ex) {
              Lizzie.gtpConsole.addLine("genmovepk summary err");
            }
          }
        }
      }

      if ((Lizzie.frame.isPlayingAgainstLeelaz || isInputCommand)) {
        if (line.contains("->")) {
          int k =
              (Lizzie.config.limitMaxSuggestion > 0 && !Lizzie.config.showNoSuggCircle
                  ? Lizzie.config.limitMaxSuggestion
                  : 361);
          if (bestMoves.size() < k) {
            MoveData mv = MoveData.fromSummaryZen(line);
            if (mv != null) {

              mv.order = bestMoves.size();
              bestMoves.add(mv);
              currentTotalPlayouts = MoveData.getPlayouts(bestMoves);
              Lizzie.board
                  .getData()
                  .tryToSetBestMoves(bestMoves, bestMovesEnginename, true, currentTotalPlayouts);
            }
          }
        }
      }
    }
    if ((isLeela || isSai) && Lizzie.frame.isPlayingAgainstLeelaz && canGetSummaryInfo) {
      int k =
          (Lizzie.config.limitMaxSuggestion > 0 && !Lizzie.config.showNoSuggCircle
              ? Lizzie.config.limitMaxSuggestion
              : 361);
      if (bestMovesPrevious.size() < k) {
        if (line.contains("->")) {
          try {
            MoveData mv = isSai ? MoveData.fromSummarySai(line) : MoveData.fromSummary(line);
            if (mv != null) {
              mv.order = bestMovesPrevious.size();
              bestMovesPrevious.add(mv);
            }
          } catch (Exception ex) {
            Lizzie.gtpConsole.addLine("genmovepk summary err");
          }
        }
      }
    }
    if (isLeela0110 && !(EngineManager.isEngineGame && EngineManager.engineGameInfo.isGenmove)) {
      if (line.contains(" -> ")) {
        if (!isLoaded) {
          Lizzie.frame.refresh();
        }
        isLoaded = true;
        List<MoveData> bm = leela0110BestMoves;
        int k =
            (Lizzie.config.limitMaxSuggestion > 0 && !Lizzie.config.showNoSuggCircle
                ? Lizzie.config.limitMaxSuggestion
                : 361);
        if (!Lizzie.frame.isPlayingAgainstLeelaz && (bm.size() < k)) {
          MoveData mv = MoveData.fromSummaryLeela0110(line);
          mv.order = bm.size();
          bm.add(mv);
        }
      } else if (isLeela0110 && line.startsWith("=====")) {
        this.canCheckAlive = true;
        if (isLeela0110PonderingValid() && !leela0110BestMoves.isEmpty()) {
          bestMoves = leela0110BestMoves;
          currentTotalPlayouts = MoveData.getPlayouts(bestMoves);
          if (Lizzie.config.isDoubleEngineMode()
              && Lizzie.leelaz2 != null
              && this == Lizzie.leelaz2)
            Lizzie.board
                .getData()
                .tryToSetBestMoves2(bestMoves, bestMovesEnginename, true, currentTotalPlayouts);
          else
            Lizzie.board
                .getData()
                .tryToSetBestMoves(bestMoves, bestMovesEnginename, true, currentTotalPlayouts);
        }
        leela0110UpdatePonder();
        Lizzie.frame.refresh(1);
        Lizzie.frame.updateTitle();
        if (!this.bestMoves.isEmpty()) {
          notifyAutoPK(false);
          notifyAutoPlay(false);
          if (Lizzie.config.isAutoAna) {
            if (Lizzie.frame.isAutoAnalyzingDiffNode) {
              nofityDiffAna();
            } else if (Lizzie.config.analyzeAllBranch) {
              notifyAutoAnaAllBranch();
            } else {
              notifyAutoAna();
            }
          }
        }
        return;
      } else {
        if (Lizzie.gtpConsole.isVisible() || Lizzie.config.alwaysGtp || !this.isLoaded)
          Lizzie.gtpConsole.addErrorLine(line + "\n");
      }
    }
    if (!this.isKatago) {
      if (line.startsWith("NN eval")) {
        String[] params = line.trim().split("=");
        heatwinrate =
            Double.valueOf(params[1].length() > 5 ? params[1].substring(0, 5) : params[1]);
      }
      if (line.startsWith("root eval")) {
        String[] params = line.trim().split("=");
        heatwinrate =
            Double.valueOf(params[1].length() > 5 ? params[1].substring(0, 5) : params[1]);
      }

      if (line.endsWith("nodes")) {
        if (!this.bestMoves.isEmpty()) {
          if (EngineManager.isEngineGame && !EngineManager.engineGameInfo.isGenmove) {
            if ((Lizzie.board.getHistory().isBlacksTurn()
                    && this
                        == Lizzie.engineManager.engineList.get(
                            EngineManager.engineGameInfo.blackEngineIndex))
                || !Lizzie.board.getHistory().isBlacksTurn()
                    && this
                        == Lizzie.engineManager.engineList.get(
                            EngineManager.engineGameInfo.whiteEngineIndex)) {
              if (isResponseUpToDate()) {
                if (!isGamePaused) {
                  notifyAutoPK(true);
                }
              }
            }
          }
          if (Lizzie.frame.isAnaPlayingAgainstLeelaz && !isGamePaused) notifyAutoPlay(true);
        }
      }
      if (line.startsWith("| ST")) {
        String[] params = line.trim().split(" ");
        if (params.length == 13) {
          isColorEngine = true;
          if (Lizzie.gtpConsole.isVisible() || Lizzie.config.alwaysGtp)
            Lizzie.gtpConsole.addLine(oriEnginename + ": " + line);
          stage = Integer.parseInt(params[3].substring(0, params[3].length() - 1));
          komi = Float.parseFloat(params[6].substring(0, params[6].length() - 1));
        }
      }
    } else {
      if ((Lizzie.frame.isPlayingAgainstLeelaz || EngineManager.isEngineGame)
          && line.startsWith("CHAT:")) {
        if (line.contains("PDA")) {
          String value = line.substring(line.indexOf("PDA") + 4);
          value = value.substring(0, value.indexOf(")"));
          this.pda = Double.parseDouble(value);
        }
      }
    }
    if (!isLoaded) {
      if (line.startsWith("Started OpenCL SGEMM")
          || line.startsWith("Tuning xGemmDirect")
          || line.contains("long time")) {
        isTuning = true;
      }
    }
    parseHeatMap(line);
  }

  private void playPassInEngineGame() {
    // TODO Auto-generated method stub
    played = true;
    Lizzie.board.pass();
    boolean isBlackEngine = currentEngineN == EngineManager.engineGameInfo.blackEngineIndex;
    if (isBlackEngine) {
      Lizzie.engineManager
          .engineList
          .get(EngineManager.engineGameInfo.blackEngineIndex)
          .playMoveNoPonder("B", "pass");
      Lizzie.engineManager
          .engineList
          .get(EngineManager.engineGameInfo.whiteEngineIndex)
          .playMovePonder("B", "pass");
    } else {
      Lizzie.engineManager
          .engineList
          .get(EngineManager.engineGameInfo.whiteEngineIndex)
          .playMoveNoPonder("W", "pass");
      Lizzie.engineManager
          .engineList
          .get(EngineManager.engineGameInfo.blackEngineIndex)
          .playMovePonder("W", "pass");
    }
  }

  private void parseHeatMap(String line) {
    if (isheatmap) {
      if (isKatago) {
        if (line.startsWith("=")) {
          heatPolicy = new ArrayList<Double>();
          heatOwnership = new ArrayList<Double>();
          canheatRedraw = true;
          isCommandLine = true;
          String[] params = line.trim().split(" ");
          if (params.length == 3) {
            if (params[1].startsWith("symmetry")) symmetry = Integer.parseInt(params[2]);
          }
        }
        if (line.startsWith("whiteWin")) {
          String[] params = line.trim().split(" ");
          heatwinrate = Double.valueOf(params[1]);
        }
        if (line.startsWith("whiteLead")) {
          String[] params = line.trim().split(" ");
          heatScore = Double.valueOf(params[1]);
        }
        if (line.startsWith("policy")) {
          heatCanGetPolicy = true;
          heatCanGetOwnership = false;
        }
        if (line.startsWith("whiteOwnership")) {
          heatCanGetPolicy = false;
          heatCanGetOwnership = true;
        }

        if (heatCanGetPolicy) {
          String[] params = line.trim().split("\\s+");
          if (params.length == Board.boardWidth) {
            for (int i = 0; i < params.length; i++) {
              try {
                heatPolicy.add((Double.parseDouble(params[i]) * 1000.0));
              } catch (NumberFormatException ex) {
                heatPolicy.add(0.0);
              }
            }
          }
        }

        if (heatCanGetOwnership) {
          String[] params = line.trim().split("\\s+");
          if (params.length == Board.boardWidth) {
            boolean blackToPlay = Lizzie.board.getHistory().isBlacksTurn();
            for (int i = 0; i < params.length; i++) {
              try {
                heatOwnership.add(
                    blackToPlay ? -Double.parseDouble(params[i]) : Double.parseDouble(params[i]));
              } catch (NumberFormatException ex) {
                heatOwnership.add(0.0);
              }
            }
          }
          if (heatOwnership.size() == Board.boardHeight * Board.boardWidth) {
            // 结束并显示
            if (canheatRedraw) {
              canheatRedraw = false;
              if (iskataHeatmapShowOwner) Lizzie.frame.drawKataEstimate(this, heatOwnership);
              heatcount = new ArrayList<Integer>();
              for (int i = 0; i < heatPolicy.size(); i++) {
                heatcount.add(heatPolicy.get(i).intValue());
              }
              if (!Lizzie.frame.isShowingHeatmap) Lizzie.frame.isShowingHeatmap = true;
              heatCanGetOwnership = false;
              Lizzie.frame.refresh();
            }
          }
        }
      } else {
        if (line.startsWith(" ") || line.length() > 0 && Character.isDigit(line.charAt(0))) {
          try {
            String[] params = line.trim().split("\\s+");
            if (params.length == Board.boardWidth) {
              for (int i = 0; i < params.length; i++) heatcount.add(Integer.parseInt(params[i]));
            }
          } catch (Exception ex) {
          }
          if (heatcount.size() == Board.boardHeight * Board.boardWidth) Lizzie.frame.refresh();
        }
        if (line.contains("winrate:")) {
          // isheatmap = false;
          if (!Lizzie.frame.isShowingHeatmap) Lizzie.frame.isShowingHeatmap = true;
          // Lizzie.frame.refresh();
          if (!isZen) {
            String[] params = line.trim().split(" ");
            heatwinrate = Double.valueOf(params[1]);
          }
        }
      }
    }
  }

  /** Continually reads and processes output from leelaz */
  private void read() {
    try {
      String line = "";
      while ((line = inputStream.readLine()) != null) {
        if (getRcentLine) {
          if (line.startsWith("= {")) {
            recentRulesLine = line;
            Lizzie.config.currentKataGoRules = line;
            getSuicidalAndRules();
            getRcentLine = false;
          } else if (line.startsWith("=")) {
            String[] params = line.trim().split(" ");
            if (params.length == 2) {
              try {
                if (recentLineNumber == 0) {
                  this.pda = Double.parseDouble(params[1]);
                } else if (recentLineNumber == 1) {
                  wrn = Double.parseDouble(params[1]);
                  Lizzie.frame.setPdaAndWrn(pda, wrn);
                  recentLineNumber++;
                }
                recentLineNumber++;
              } catch (NumberFormatException e) {
              }
            }
          }
        }
        if (EngineManager.isEngineGame && EngineManager.engineGameInfo.isGenmove && isLoaded) {
          try {
            parseLineForGenmovePk(line);
          } catch (Exception e) {
            e.printStackTrace();
          }

        } else {
          if (startGetCommandList) {
            String cmd = line.trim();
            if (!cmd.equals("") && !cmd.equals("=")) commandLists.add(cmd);
          }
          try {
            parseLine(line);
          } catch (Exception e) {
            e.printStackTrace();
          }
        }
        if (isCommandLine) {
          if (!this.isKatago && !this.isLeela0110 && Lizzie.frame.isPlayingAgainstLeelaz) {
            Runnable runnable =
                new Runnable() {
                  public void run() {
                    try {
                      while (!isResponseUpToDate()) Thread.sleep(10);
                    } catch (InterruptedException e) {
                      // TODO Auto-generated catch block
                      e.printStackTrace();
                    }
                    if (Lizzie.board.getHistory().getCurrentHistoryNode().previous().isPresent()
                        && !bestMovesPrevious.isEmpty()) {
                      Lizzie.board
                          .getHistory()
                          .getCurrentHistoryNode()
                          .previous()
                          .get()
                          .getData()
                          .tryToSetBestMoves(
                              bestMovesPrevious,
                              bestMovesEnginename,
                              true,
                              MoveData.getPlayouts(bestMovesPrevious));
                      bestMovesPrevious = new ArrayList<>();
                    }
                    canGetSummaryInfo = false;
                  }
                };
            Thread thread = new Thread(runnable);
            thread.start();
          }
          currentCmdNum++;

          //						if(isModifying&&isResponseUpToDate())
          //							setModifyEnd();
          if (currentCmdNum > cmdNumber - 1) currentCmdNum = cmdNumber - 1;
          try {
            trySendCommandFromQueue();
          } catch (Exception e) {
            e.printStackTrace();
          }
        }
        isCommandLine = false;
        // line = new StringBuilder();
        //					if(isInfoLine)
        //					{
        //						if (!this.bestMoves.isEmpty()) {
        //							  notifyAutoPK();
        //				        	  notifyAutoPlay();
        //						}
        //					}

        //	isInfoLine=false;
        // }
        //				else if (c == '='||c=='?') {
        //					isCommandLine = true;
        //				}
      }
      // this line will be reached when engine shuts down
      System.out.println("engine process ended.");
      // process.destroy();
      shutdown();
      if (useJavaSSH) javaSSHClosed = true;
      // Do no exit for switching weights
      // System.exit(-1);
    } catch (IOException e) {
      e.printStackTrace();
      //	System.out.println("读出错");
      // System.exit(-1);
      // read();
    }
    if (!isNormalEnd) {
      started = false;
      isDownWithError = true;
      // isLoaded=false;
      tryToDignostic(Lizzie.resourceBundle.getString("Leelaz.engineEndUnormalHint"), false);
      if (!Lizzie.gtpConsole.isVisible()) Lizzie.gtpConsole.setVisible(true);
      // ("打开Gtp窗口(快捷键E)查看报错信息");
      // LizzieFrame.openMoreEngineDialog();
    }
  }

  //	private void stopAutoAna() {
  //		//if (!isClosing) {
  //		      			//isClosing=true;
  //		      			Lizzie.frame.toolbar.stopAutoAna();
  //		      			//Lizzie.frame.addInput();
  //
  //		      //			}
  //	}

  public void setPda(String pda) {
    try {
      this.pda = Double.parseDouble(pda);
      pdaBeforeGame = Double.parseDouble(pda);
    } catch (NumberFormatException e) {
      e.printStackTrace();
      return;
    }
    sendCommand("kata-set-param playoutDoublingAdvantage " + pda);
  }

  public void setGameStatus(boolean isStart) {
    if (!Lizzie.leelaz.isKatagoCustom || Lizzie.leelaz.isKataGoPda) return;
    if (isStart) {
      sendCommand("startGame");
      pdaBeforeGame = pda;
    } else {
      sendCommand("stopGame");
      if (Lizzie.config.autoLoadKataEnginePDA) {
        this.pda = Double.parseDouble(Lizzie.config.txtKataEnginePDA);
      } else this.pda = pdaBeforeGame;
    }
  }
  /**
   * Sends a command to command queue for leelaz to execute
   *
   * @param command a GTP command containing no newline characters
   */
  public void sendCommand(String command) {
    if (Lizzie.config.isDoubleEngineMode()) {
      if ((command.startsWith("heat") || command.startsWith("kata-raw"))
          && !this.isKatago
          && Lizzie.leelaz2 != null
          && this == Lizzie.leelaz2) heatcount = new ArrayList<Integer>();
      if (Lizzie.leelaz2 != null && this == Lizzie.leelaz2)
        if (this.isLeela0110) {
          if (command.startsWith("lz-") || command.startsWith("kata-")) this.leela0110Ponder(true);
          return;
        } else if (this.isKatago && !Lizzie.leelaz.isKatago) {
          if (command.startsWith("lz-")) {
            command = "kata-" + command.substring(3);
          }
          if (command.startsWith("heat")) {
            command = ("kata-raw-nn " + new Random().nextInt(8));
          }
        }
      if (Lizzie.leelaz2 != null
          && this == Lizzie.leelaz2
          && !this.isKatago
          && Lizzie.leelaz.isKatago) {
        if (command.startsWith("kata-raw")) {
          command = "heatmap";
        }
        if (command.startsWith("kata-")) {
          command = "lz-" + command.substring(5);
        }

        String[] params = command.trim().split(" ");
        if (params.length > 2) {
          if (params[params.length - 2].equals("ownership")) {
            command = command.substring(0, command.length() - 14);
          }
        }
      }
    }
    synchronized (cmdQueue) {
      // For efficiency, delete unnecessary "lz-analyze" that will be stopped
      // immediately
      cmdNumber++;
      calculateModifyNumber();
      if (!cmdQueue.isEmpty()
          && (cmdQueue.peekLast().startsWith("lz-analyze")
              || cmdQueue.peekLast().startsWith("kata-analyze")
              || cmdQueue.peekLast().startsWith("kata-raw")
              || cmdQueue.peekLast().startsWith("heatmap")
              || cmdQueue.peekLast().startsWith("stop-ponder"))) {
        cmdQueue.removeLast();
        cmdNumber--;
      }
      cmdQueue.addLast(command);
      trySendCommandFromQueue();
    }
    if (Lizzie.frame.isAutocounting) {
      Lizzie.frame.zen.sendAndEstimate(command, true);
    }
    if (Lizzie.config.isDoubleEngineMode()) {
      if (Lizzie.leelaz2 != null && this != Lizzie.leelaz2) {
        Lizzie.leelaz2.sendCommand(command);
        Lizzie.leelaz2.startPonderTime = this.startPonderTime;
      }
    }
  }

  public void sendCommandNoLeelaz2(String command) {
    if (Lizzie.config.isDoubleEngineMode()) {
      if ((command.startsWith("heat") || command.startsWith("kata-raw"))
          && !this.isKatago
          && Lizzie.leelaz2 != null
          && this == Lizzie.leelaz2) heatcount = new ArrayList<Integer>();
      if (Lizzie.leelaz2 != null
          && this == Lizzie.leelaz2
          && this.isKatago
          && !Lizzie.leelaz.isKatago) {
        if (command.startsWith("lz-")) {
          command = "kata-" + command.substring(3);
        }
        if (command.startsWith("heat")) {
          command = ("kata-raw-nn " + new Random().nextInt(8));
        }
      }
      if (Lizzie.leelaz2 != null
          && this == Lizzie.leelaz2
          && !this.isKatago
          && Lizzie.leelaz.isKatago) {
        if (command.startsWith("kata-raw")) {
          command = "heatmap";
        }
        if (command.startsWith("kata-")) {
          command = "lz-" + command.substring(5);
        }

        String[] params = command.trim().split(" ");
        if (params.length > 2) {
          if (params[params.length - 2].equals("ownership")) {
            command = command.substring(0, command.length() - 14);
          }
        }
      }
    }
    synchronized (cmdQueue) {
      // For efficiency, delete unnecessary "lz-analyze" that will be stopped
      // immediately
      if (!cmdQueue.isEmpty()
          && (cmdQueue.peekLast().startsWith("lz-analyze")
              || cmdQueue.peekLast().startsWith("kata-analyze")
              || cmdQueue.peekLast().startsWith("kata-raw")
              || cmdQueue.peekLast().startsWith("heatmap"))) {
        cmdQueue.removeLast();
      }
      cmdQueue.addLast(command);
      trySendCommandFromQueue();
    }
    if (Lizzie.frame.isAutocounting) {
      Lizzie.frame.zen.sendAndEstimate(command, true);
    }
    if (canSetNotPlayed) {
      canSetNotPlayed = false;
      played = false;
    }
  }

  /** Sends a command from command queue for leelaz to execute if it is ready */
  private void trySendCommandFromQueue() {
    // Defer sending "lz-analyze" if leelaz is not ready yet.
    // Though all commands should be deferred theoretically,
    // only "lz-analyze" is differed here for fear of
    // possible hang-up by missing response for some reason.
    // cmdQueue can be replaced with a mere String variable in this case,
    // but it is kept for future change of our mind.
    synchronized (cmdQueue) {
      if (requireResponseBeforeSend && !isResponseUpToPreDate()) {
        return;
      }
      if (cmdQueue.isEmpty()) {
        return;
      }
      if (!isResponseUpToPreCommand()) {
        if (cmdQueue.peekFirst().startsWith("lz-analyze")
            || cmdQueue.peekFirst().startsWith("kata-analyze")
            || cmdQueue.peekFirst().startsWith("kata-raw")
            || cmdQueue.peekFirst().startsWith("heatmap")
            || cmdQueue.peekFirst().startsWith("stop-ponder")) return;
      }
      String command = cmdQueue.removeFirst();
      if (command.equals("stop-ponder")) command = "stop";
      sendCommandToLeelaz(command);
    }
  }

  /**
   * Sends a command for leelaz to execute
   *
   * @param command a GTP command containing no newline characters
   */
  private void sendCommandToLeelaz(String command) {
    if (command.startsWith("fixed_handicap")
        || (isKatago && command.startsWith("place_free_handicap"))) isSettingHandicap = true;
    if (command.startsWith("benchmark")) {
      currentCmdNum++;
    }
    if (outputStream != null) {
      try {
        outputStream.write((command + "\n").getBytes());
        outputStream.flush();
      } catch (Exception e) {
        //  e.printStackTrace();
      }
      if (Lizzie.engineManager.isEngineGame()) {
        Lizzie.gtpConsole.addCommandForEngineGame(
            command,
            cmdNumber,
            oriEnginename,
            EngineManager.engineGameInfo.isBlackEngine(currentEngineN()));

      } else if (Lizzie.config.alwaysGtp || Lizzie.gtpConsole.isVisible())
        Lizzie.gtpConsole.addCommand(command, cmdNumber, oriEnginename);
    }
    if (canSetNotPlayed) {
      canSetNotPlayed = false;
      played = false;
    }
  }

  /** Check whether leelaz is responding to the last command */
  public boolean isResponseUpToDate() {
    // Use >= instead of == for avoiding hang-up, though it cannot happen
    return currentCmdNum >= cmdNumber - 1; // &&currentCmdNum >=ignoreCmdNumber;
  }

  private boolean isResponseUpToPreDate() {
    // Use >= instead of == for avoiding hang-up, though it cannot happen
    return currentCmdNum >= cmdNumber - 2; // &&currentCmdNum >=ignoreCmdNumber;
  }

  private boolean isResponseUpToPreCommand() {
    // Use >= instead of == for avoiding hang-up, though it cannot happen
    return currentCmdNum >= cmdNumber - 3; // &&currentCmdNum >=ignoreCmdNumber;
  }

  public void setResponseUpToDate() {
    // Use >= instead of == for avoiding hang-up, though it cannot happen
    currentCmdNum = cmdNumber - 1;
    //	ignoreCmdNumber=cmdNumber-1;
  }

  /**
   * @param color color of stone to play
   * @param move coordinate of the coordinate
   */
  public void playMove(Stone color, String move) {
    playMove(color, move, false, false);
  }

  public void playMove(Stone color, String move, boolean addPlayer, boolean blackToPlay) {
    if (!isKatago || isSai) {
      if (move == "pass") {
        if (Lizzie.board.getHistory().getCurrentHistoryNode()
            != Lizzie.board.getHistory().getStart()) {
          Optional<int[]> lastMove = Lizzie.board.getLastMove();
          if (!lastMove.isPresent()) {
            this.setModifyEnd();
            return;
          }
        }
      }
    }
    //		canGetGenmoveInfoGen = true;
    //	getGenmoveInfoPrevious = true;
    synchronized (this) {
      String colorString;
      switch (color) {
        case BLACK:
          colorString = "B";
          break;
        case WHITE:
          colorString = "W";
          break;
        default:
          throw new IllegalArgumentException(
              "The stone color must be B or W, but was " + color.toString());
      }

      sendCommand("play " + colorString + " " + move);
      bestMoves = new ArrayList<>();
      currentTotalPlayouts = 0;
      if (Lizzie.frame.isPlayingAgainstLeelaz) this.canGetSummaryInfo = true;
      //				bestMovesPrevious = new ArrayList<>();
      if (Lizzie.frame.isAnaPlayingAgainstLeelaz && Lizzie.frame.playerIsBlack == blackToPlay)
        return;
      if ((stopByLimit || isPondering) && !Lizzie.frame.isPlayingAgainstLeelaz)
        if (Lizzie.config.isAutoAna
            || ((Lizzie.config.analyzeBlack && color == Stone.WHITE)
                || (Lizzie.config.analyzeWhite && color == Stone.BLACK)))
          ponder(addPlayer, blackToPlay);
        else {
          nameCmdfornoponder();
          underPonder = true;
        }
      if (!isPondering && !Lizzie.config.playponder && isKatago) sendCommand("stop-ponder");
    }
  }

  public void playMoveNoPonder(Stone color, String move) {
    synchronized (this) {
      String colorString;
      switch (color) {
        case BLACK:
          colorString = "B";
          break;
        case WHITE:
          colorString = "W";
          break;
        default:
          throw new IllegalArgumentException(
              "The stone color must be B or W, but was " + color.toString());
      }
      sendCommand("play " + colorString + " " + move);
      // Lizzie.frame.subBoardRenderer.reverseBestmoves = true;
      // Lizzie.frame.boardRenderer.reverseBestmoves = true;
      // bestMoves = new ArrayList<>();
    }
  }

  public void playMoveNoPonder(String colorString, String move) {
    if (Lizzie.config.enginePkPonder) {
      synchronized (this) {
        bestMoves = new ArrayList<>();
        currentTotalPlayouts = 0;
        sendCommand("play " + colorString + " " + move);
        pkponder();
      }
      pkMoveTime = System.currentTimeMillis() - pkMoveStartTime;
      pkMoveTimeGame = pkMoveTimeGame + pkMoveTime;
      return;
    }
    synchronized (this) {
      sendCommand("play " + colorString + " " + move);
      nameCmdfornoponder();
      // Lizzie.frame.subBoardRenderer.reverseBestmoves = true;
      // Lizzie.frame.boardRenderer.reverseBestmoves = true;
      // bestMoves = new ArrayList<>();
    }
    pkMoveTime = System.currentTimeMillis() - pkMoveStartTime;
    pkMoveTimeGame = pkMoveTimeGame + pkMoveTime;
  }

  public void playMovePonder(String colorString, String move) {
    Lizzie.frame.mouseOverCoordinate = LizzieFrame.outOfBoundCoordinate;
    synchronized (this) {
      canSetNotPlayed = true;
      if (Lizzie.config.enginePkPonder) {
        bestMoves = new ArrayList<>();
        currentTotalPlayouts = 0;
      }
      sendCommand("play " + colorString + " " + move);
      pkponder();
    }
    pkMoveStartTime = System.currentTimeMillis();
  }

  public boolean playMoveGenmove(String colorString, String move) {
    // genmoveNode++;
    //	canGetGenmoveInfo = false;
    if (this.resigned) {
      return false;
    }
    synchronized (this) {
      sendCommand("play " + colorString + " " + move);
    }
    Lizzie.frame.updateTitle();
    return true;
  }

  public void genmove(String color) {
    String command =
        (this.isKatago
            ? ("kata-genmove_analyze "
                + color
                + " "
                + getInterval()
                + (Lizzie.config.showKataGoEstimate ? " ownership true" : "")
                + (Lizzie.config.showPvVisits ? " pvVisits true" : ""))
            : (this.isSai || this.isLeela
                ? ("lz-genmove_analyze " + color + " " + getInterval())
                : ("genmove " + color)));
    /*
     * We don't support displaying this while playing, so no reason to request it
     * (for now) if (isPondering) { command = "lz-genmove_analyze " + color + " 10";
     * }
     */
    sendCommand(command);
    isThinking = true;
    LizzieFrame.menu.toggleEngineMenuStatus(false, true);
    // canGetGenmoveInfo = false;

    // isPondering = false;
    //	genmovenoponder = false;
  }

  public void genmoveForPk(String color) {
    if (LizzieFrame.toolbar.isPkStop) {
      LizzieFrame.toolbar.isPkGenmoveStop = true;
      if (color.equals("B")) {
        LizzieFrame.toolbar.isPkStopGenmoveB = true;
      } else {
        LizzieFrame.toolbar.isPkStopGenmoveB = false;
      }
      Lizzie.engineManager
          .engineList
          .get(EngineManager.engineGameInfo.whiteEngineIndex)
          .nameCmdfornoponder();
      Lizzie.engineManager
          .engineList
          .get(EngineManager.engineGameInfo.blackEngineIndex)
          .nameCmdfornoponder();
      return;
    }
    String command =
        (this.isKatago
            ? ("kata-genmove_analyze "
                + color
                + " "
                + getIntervalForGenmovePk()
                + (Lizzie.config.showKataGoEstimate ? " ownership true" : "")
                + (Lizzie.config.showPvVisits ? " pvVisits true" : ""))
            : (this.isSai || this.isLeela
                ? ("lz-genmove_analyze " + color + " " + getInterval())
                : ("genmove " + color)));
    /*
     * We don't support displaying this while playing, so no reason to request it
     * (for now) if (isPondering) { command = "lz-genmove_analyze " + color + " 10";
     * }
     */
    // bestMoves = new ArrayList<>();
    // canGetGenmoveInfo = true;
    sendCommand(command);
    // isThinking = true;

    // isPondering = false;
    // genmovenoponder =false;
  }

  public void clearPkMoveStartTime() {
    pkMoveStartTime = System.currentTimeMillis();
  }

  //	public void genmove_analyze(String color) {
  //		String command = "lz-genmove_analyze " + color + " " +
  // Lizzie.config.analyzeUpdateIntervalCentisec;
  //		sendCommand(command);
  //		isThinking = true;
  //		isPondering = false;
  //	}

  //  public void time_settings() {
  //    Lizzie.leelaz.sendCommand("time_settings 0 " + Lizzie.config.maxGameThinkingTimeSeconds + "
  // 1");
  //  }

  public void clear() {
    synchronized (this) {
      sendCommand("clear_board");
      if (isKatago) {
        scoreMean = 0;
        scoreStdev = 0;
      }
      bestMoves = new ArrayList<>();
      currentTotalPlayouts = 0;
      if (isPondering) ponder();
      currentCmdNum = Math.max(cmdNumber - 2, currentCmdNum);
    }
  }

  public void clearWithoutPonder() {
    synchronized (this) {
      this.notPondering();
      nameCmdfornoponder();
      sendCommand("clear_board");
      bestMoves = new ArrayList<>();
      currentTotalPlayouts = 0;
      currentCmdNum = Math.max(cmdNumber - 2, currentCmdNum);
    }
  }

  public void undo() {
    undo(false, false);
  }

  public void undo(boolean addPlayer, boolean blackToPlay) {
    synchronized (this) {
      sendCommand("undo");
      bestMoves = new ArrayList<>();
      currentTotalPlayouts = 0;
      if (isPondering)
        if (Lizzie.config.isAutoAna
            || ((Lizzie.config.analyzeBlack && Lizzie.board.getHistory().isBlacksTurn())
                || (Lizzie.config.analyzeWhite && !Lizzie.board.getHistory().isBlacksTurn())))
          ponder(addPlayer, blackToPlay);
        else {
          nameCmdfornoponder();
          underPonder = true;
        }
    }
  }

  public void analyzeAvoid(String type, String color, String coordList, int untilMove) {
    analyzeAvoid(
        String.format(
            Locale.ENGLISH, "%s %s %s %d", type, color, coordList, untilMove <= 0 ? 1 : untilMove));
    Lizzie.board.clearbestmoves();
  }

  public void analyzeAvoid(String type, String coordList, int untilMove) {
    analyzeAvoid(type, coordList, untilMove, false, false);
  }

  public void analyzeAvoid(
      String type, String coordList, int untilMove, boolean addPlayer, boolean blackToPlay) {
    bestMoves = new ArrayList<>();
    currentTotalPlayouts = 0;
    if (!isPondering) {
      isPondering = true;
      startPonderTime = System.currentTimeMillis();
    }
    String parameters =
        String.format(
            Locale.ENGLISH, "%s %s %s %d", type, "b", coordList, untilMove <= 0 ? 1 : untilMove);
    parameters =
        parameters
            + " "
            + String.format(
                Locale.ENGLISH,
                "%s %s %s %d",
                type,
                "w",
                coordList,
                untilMove <= 0 ? 1 : untilMove);
    sendCommand(
        String.format(
            (isKatago
                ? "kata-analyze %s%d %s"
                    + (Lizzie.config.showKataGoEstimate ? " ownership true" : "")
                    + (Lizzie.config.showPvVisits ? " pvVisits true" : "")
                : "lz-analyze %s%d %s"),
            maybeAddPlayer(addPlayer, blackToPlay),
            getInterval(),
            parameters));
    Lizzie.board.clearbestmoves();
  }

  public void analyzeAvoid(String parameters) {
    bestMoves = new ArrayList<>();
    currentTotalPlayouts = 0;
    if (!isPondering) {
      isPondering = true;
      startPonderTime = System.currentTimeMillis();
    }
    sendCommand(
        String.format(
            (isKatago
                ? "kata-analyze %s%d %s"
                    + (Lizzie.config.showKataGoEstimate ? " ownership true" : "")
                    + (Lizzie.config.showPvVisits ? " pvVisits true" : "")
                : "lz-analyze %s%d %s"),
            maybeAddPlayer(),
            getInterval(),
            parameters));
    // Lizzie.board.getHistory().getData().tryToClearBestMoves();
    Lizzie.board.clearbestmoves();
  }

  /** This initializes leelaz's pondering mode at its current position */
  public void ponder() {
    ponder(false, false);
  }

  public void ponder(boolean addPlayer, boolean blackToPlay) {
    if (noAnalyze) return;
    isPondering = true;
    underPonder = false;
    if (stopByPlayouts) outOfPlayoutsLimit = true;
    stopByPlayouts = false;
    stopByLimit = false;
    startPonderTime = System.currentTimeMillis();
    if (EngineManager.isEngineGame) pkMoveStartTime = startPonderTime;
    if (!Lizzie.config.playponder && Lizzie.frame.isPlayingAgainstLeelaz) {
      return;
    }
    if (isheatmap) {
      heatcount = new ArrayList<Integer>();
      sendHeatCommand();
      return;
    }
    if (isLeela0110) {
      leela0110Ponder(true);
      return;
    }
    if (Lizzie.frame.isKeepingForce || LizzieFrame.isKeepForcing) {
      if (LizzieFrame.allowcoords != "") {
        Lizzie.leelaz.analyzeAvoid(
            "allow",
            LizzieFrame.allowcoords,
            Lizzie.config.selectAllowMoves,
            addPlayer,
            blackToPlay);
      } else {
        Lizzie.leelaz.analyzeAvoid(
            "avoid",
            LizzieFrame.avoidcoords,
            Lizzie.config.selectAvoidMoves,
            addPlayer,
            blackToPlay);
      }
    } else {
      LizzieFrame.isTempForcing = false;
      LizzieFrame.allowcoords = "";
      LizzieFrame.avoidcoords = "";
      Lizzie.frame.clearSelectImage();
      if (this.isKatago) {
        sendCommand(
            "kata-analyze "
                + maybeAddPlayer(addPlayer, blackToPlay)
                + getInterval()
                + (Lizzie.config.showKataGoEstimate ? " ownership true" : "")
                + (Lizzie.config.showPvVisits ? " pvVisits true" : ""));
      } else {
        sendCommand("lz-analyze " + maybeAddPlayer(addPlayer, blackToPlay) + getInterval());
      }
    }
    LizzieFrame.menu.toggleEngineMenuStatus(true, false);
  }

  private String maybeAddPlayer() {
    return maybeAddPlayer(false, false);
  }

  private String maybeAddPlayer(boolean addPlayer, boolean reverse) {
    if (!canAddPlayer) return "";
    else if (addPlayer) return (reverse ? "B " : "W ");
    else return (Lizzie.board.getHistory().isBlacksTurn() ? "B " : "W ");
  }

  public int getInterval() {
    if (isSSH || useJavaSSH) return Lizzie.config.analyzeUpdateIntervalCentisecSSH;
    else return Lizzie.config.analyzeUpdateIntervalCentisec;
  }

  public int getIntervalForGenmovePk() {
    if (isKatago && Lizzie.config.showPreviousBestmovesInEngineGame) return Integer.MAX_VALUE;
    if (isSSH || useJavaSSH) return Lizzie.config.analyzeUpdateIntervalCentisecSSH;
    else return Lizzie.config.analyzeUpdateIntervalCentisec;
  }

  public void pkponder() {
    isPondering = true;
    startPonderTime = System.currentTimeMillis();
    if (isLeela0110) {
      leela0110Ponder(true);
      return;
    }
    if (this.isKatago) {
      if (Lizzie.config.showKataGoEstimate)
        sendCommand(
            "kata-analyze "
                + getInterval()
                + (Lizzie.config.showPvVisits ? " pvVisits true" : "")
                + " ownership true");
      else
        sendCommand(
            "kata-analyze " + getInterval() + (Lizzie.config.showPvVisits ? " pvVisits true" : ""));
    } else {
      sendCommand("lz-analyze " + getInterval());
    } // until it responds to this, incoming
    // ponder results are obsolete

  }

  public void togglePonder() {
    if (underPonder) {
      ponder();
      return;
    }
    isPondering = !isPondering;
    // if(isPondering)
    if (Lizzie.frame.isShowingHeatmap) {
      Lizzie.frame.isShowingHeatmap = false;
      ponder();
    }
    if (isPondering) {
      ponder();
    } else {
      nameCmd();
    }
  }

  public void clearPonderLimit() {
    outOfPlayoutsLimit = false;
    stopByPlayouts = false;
  }

  /** End the process */
  public void shutdown() {
    leela0110StopPonder();
    if (this.useJavaSSH) {
      javaSSH.close();
    } else {
      if (process != null) process.destroy();
    }
  }

  public List<MoveData> getBestMoves() {
    //	synchronized (this) {
    return bestMoves;
    //	}
  }

  public void clearBestMoves() {
    bestMoves = new ArrayList<>();
    currentTotalPlayouts = 0;
  }

  // public Optional<String> getDynamicKomi() {
  // if (Float.isNaN(dynamicKomi) || Float.isNaN(dynamicOppKomi)) {
  // return Optional.empty();
  // } else {
  // return Optional.of(String.format(Locale.ENGLISH,"%.1f / %.1f", dynamicKomi,
  // dynamicOppKomi));
  // }
  // }

  //	public void setModifying() {
  //		//isModifying=true;
  //	//	ignoreCmdNumber=cmdNumber;
  //	}
  //
  //	public void setModifyEnd(boolean fromBoard) {
  //	//	isModifying=false;
  //	//	if(fromBoard)
  //		//	ignoreCmdNumber=cmdNumber-1;
  //	}

  public boolean isPondering() {
    return isPondering;
  }

  public void Pondering() {
    isPondering = true;
  }

  public void notPondering() {
    isPondering = false;
  }

  public class WinrateStats {
    public double maxWinrate;
    public int totalPlayouts;
    public double scoreLead;

    public WinrateStats(double maxWinrate, int totalPlayouts, double score) {
      this.maxWinrate = maxWinrate;
      this.totalPlayouts = totalPlayouts;
      this.scoreLead = score;
    }
  }

  /*
   * Return the best win rate and total number of playouts. If no analysis
   * available, win rate is negative and playouts is 0.
   */
  public WinrateStats getWinrateStats() {
    WinrateStats stats = new WinrateStats(-100, 0, 0);
    if (!bestMoves.isEmpty()) {
      // we should match the Leelaz UCTNode get_eval, which is a weighted average
      // copy the list to avoid concurrent modification exception... TODO there must
      // be a better way
      // (note the concurrent modification exception is very very rare)
      // We should use Lizzie Board's best moves as they will generally be the most
      // accurate
      // final List<MoveData> moves = new ArrayList<MoveData>(Lizzie.board.getData().bestMoves);

      // get the total number of playouts in moves
      stats.totalPlayouts = currentTotalPlayouts;

      // stats.maxWinrate = bestMoves.get(0).winrate;
      stats.maxWinrate = BoardData.getWinrateFromBestMoves(bestMoves);
      stats.scoreLead = BoardData.getScoreLeadFromBestMoves(bestMoves);
      // BoardData.getWinrateFromBestMoves(moves);
    }

    return stats;
  }

  /*
   * initializes the normalizing factor for winrate_to_handicap_stones conversion.
   */
  //	public void estimatePassWinrate() {
  //		// we use A1 instead of pass, because valuenetwork is more accurate for A1 on
  //		// empty board than a
  //		// pass.
  //		// probably the reason for higher accuracy is that networks have randomness
  //		// which produces
  //		// occasionally A1 as first move, but never pass.
  //		// for all practical purposes, A1 should equal pass for the value it provides,
  //		// hence good
  //		// replacement.
  //		// this way we avoid having to run lots of playouts for accurate winrate for
  //		// pass.
  //		playMove(Stone.BLACK, "A1");
  //		togglePonder();
  //		WinrateStats stats = getWinrateStats();
  //
  //		// we could use a timelimit or higher minimum playouts to get a more accurate
  //		// measurement.
  //		while (stats.totalPlayouts < 1) {
  //			try {
  //				Thread.sleep(100);
  //			} catch (InterruptedException e) {
  //				throw new Error(e);
  //			}
  //			stats = getWinrateStats();
  //		}
  //		mHandicapWinrate = stats.maxWinrate;
  //		togglePonder();
  //		undo();
  //		Lizzie.board.clear(false);
  //	}

  // public static double mHandicapWinrate = 25;

  /**
   * Convert winrate to handicap stones, by normalizing winrate by first move pass winrate (one
   * stone handicap).
   */
  //	public static double winrateToHandicap(double pWinrate) {
  //		// we assume each additional handicap lowers winrate by fixed percentage.
  //		// this is pretty accurate for human handicap games at least.
  //		// also this kind of property is a requirement for handicaps to determined based
  //		// on rank
  //		// difference.
  //
  //		// lets convert the 0%-50% range and 100%-50% from both the move and and pass
  //		// into range of 0-1
  //		double moveWinrateSymmetric = 1 - Math.abs(1 - (pWinrate / 100) * 2);
  //		double passWinrateSymmetric = 1 - Math.abs(1 - (mHandicapWinrate / 100) * 2);
  //
  //		// convert the symmetric move winrate into correctly scaled log scale, so that
  //		// winrate of
  //		// passWinrate equals 1 handicap.
  //		double handicapSymmetric = Math.log(moveWinrateSymmetric) / Math.log(passWinrateSymmetric);
  //
  //		// make it negative if we had low winrate below 50.
  //		return Math.signum(pWinrate - 50) * handicapSymmetric;
  //	}

  // public synchronized void addListener(LeelazListener listener) {
  // listeners.add(listener);
  // }

  // Beware, due to race conditions, bestMoveNotification can be called once even
  // after item is
  // removed
  // with removeListener
  //	public synchronized void removeListener(LeelazListener listener) {
  //		listeners.remove(listener);
  //	}

  // private synchronized void notifyBestMoveListeners() {
  // for (LeelazListener listener : listeners) {
  // listener.bestMoveNotification(bestMoves);
  // }
  // }

  public boolean isStarted() {
    return started;
  }

  public void clearPDA() {
    pda = 0.0;
    LizzieFrame.menu.txtPDA.setText("0.0");
  }

  // 随机落子
  public MoveData randomBestmove(List<MoveData> bestMoves, double diffWinrate, boolean isAutoPlay) {
    int maxPlayouts = 0;
    if (Lizzie.config.checkRandomVisits) {
      for (MoveData move : bestMoves) {
        if (move.playouts > maxPlayouts) maxPlayouts = move.playouts;
      }
    }
    double minWinrate = bestMoves.get(0).winrate - diffWinrate;
    List<MoveData> bestMovesTemp = new ArrayList<>();
    bestMovesTemp.add(bestMoves.get(0));
    for (int i = 1; i < bestMoves.size(); i++) {
      if (bestMoves.get(i).winrate >= minWinrate) {
        if (isAutoPlay) {
          if (Lizzie.config.anaGameRandomPlayoutsDiff > 0) {
            if (bestMoves.get(i).playouts / (float) maxPlayouts
                >= Lizzie.config.anaGameRandomPlayoutsDiff / 100)
              bestMovesTemp.add(bestMoves.get(i));
          }
          bestMovesTemp.add(bestMoves.get(i));
        } else {
          if (Lizzie.config.checkRandomVisits && i > 0) {
            if (bestMoves.get(i).playouts / (float) maxPlayouts
                >= Lizzie.config.percentsRandomVisits / 100) bestMovesTemp.add(bestMoves.get(i));
          } else bestMovesTemp.add(bestMoves.get(i));
        }
      }
    }
    Random random = new Random();
    int n = random.nextInt(bestMovesTemp.size());
    return bestMovesTemp.get(n);
  }

  public boolean isLoaded() {
    return isLoaded;
  }

  public void tryToDignostic(String message, boolean isModal) {
    if (!Lizzie.config.autoCheckEngineAlive && Lizzie.engineManager.isEngineGame())
      Lizzie.engineManager.clearEngineGame();
    if (engineFailedMessage != null && engineFailedMessage.isVisible()) return;
    engineFailedMessage =
        new EngineFailedMessage(
            commands, engineCommand, message, !useJavaSSH && OS.isWindows(), true, false);
    engineFailedMessage.setModal(isModal);
    engineFailedMessage.setVisible(true);
  }

  //	public String currentWeight() {
  //		return currentWeight;
  //	}
  //
  //	public String currentShortWeight() {
  //		if (currentWeight != null && currentWeight.length() > 18) {
  //			return currentWeight.substring(0, 16) + "..";
  //		}
  //		return currentWeight;
  //	}

  //	public boolean switching() {
  //		return switching;
  //	}

  public int currentEngineN() {
    return currentEngineN;
  }

  public String engineCommand() {
    return this.engineCommand;
  }

  //	public void toggleGtpConsole() {
  //		gtpConsole = !gtpConsole;
  //	}
  //
  private void setLeelaSaiEnginePara() {
    if (Lizzie.config.chkLzsaiEngineMem && Lizzie.config.autoLoadLzsaiEngineMem)
      sendCommand(
          "lz-setoption name Maximum Memory Use (MiB) value " + Lizzie.config.txtLzsaiEngineMem);

    if (Lizzie.config.chkLzsaiEngineVisits && Lizzie.config.autoLoadLzsaiEngineVisits)
      sendCommand("lz-setoption name Visits value " + Lizzie.config.txtLzsaiEngineVisits);

    if (Lizzie.config.chkLzsaiEngineLagbuffer && Lizzie.config.autoLoadLzsaiEngineLagbuffer)
      sendCommand("lz-setoption name Lagbuffer value " + Lizzie.config.txtLzsaiEngineLagbuffer);

    if (Lizzie.config.chkLzsaiEngineResign && Lizzie.config.autoLoadLzsaiEngineResign)
      sendCommand(
          "lz-setoption name Resign Percentage value " + Lizzie.config.txtLzsaiEngineResign);
  }

  private void setKataEnginePara() {
    if (Lizzie.config.autoLoadKataEnginePDA && !isKataGoPda) {
      setPda(Lizzie.config.autoLoadTxtKataEnginePDA);
    }
    if (Lizzie.config.autoLoadKataEngineThreads)
      Lizzie.leelaz.sendCommand(
          "kata-set-param numSearchThreads " + Lizzie.config.txtKataEngineThreads);
    if (Lizzie.config.autoLoadKataEngineWRN) {
      try {
        this.wrn = Double.parseDouble(Lizzie.config.autoLoadTxtKataEngineWRN);
      } catch (NumberFormatException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
        return;
      }
      sendCommand("kata-set-param analysisWideRootNoise " + wrn);
    }
  }

  public void setHeatmap() {
    Lizzie.frame.isShowingHeatmap = true;
    isheatmap = true;
    heatcount = new ArrayList<Integer>();
    heatPolicy = new ArrayList<Double>();
    heatOwnership = new ArrayList<Double>();
  }

  public void toggleHeatmap(boolean bySpace) {
    // TODO Auto-generated method stub
    if (EngineManager.isEmpty) {
      Lizzie.frame.togglePolicy();
      return;
    }
    Lizzie.frame.isShowingPolicy = false;
    if (isKatago) Lizzie.frame.clearKataEstimate();
    if ((isKatago && !bySpace)
        || (Lizzie.config.isDoubleEngineMode()
            && Lizzie.leelaz2 != null
            && Lizzie.leelaz2.isKatago)) {
      if (isheatmap) {
        if (iskataHeatmapShowOwner) {
          Lizzie.frame.isShowingHeatmap = !Lizzie.frame.isShowingHeatmap;
          isheatmap = Lizzie.frame.isShowingHeatmap;
          iskataHeatmapShowOwner = false;
        } else {
          iskataHeatmapShowOwner = true;
        }
      } else {
        Lizzie.frame.isShowingHeatmap = !Lizzie.frame.isShowingHeatmap;
        isheatmap = Lizzie.frame.isShowingHeatmap;
      }
    } else {
      Lizzie.frame.isShowingHeatmap = !Lizzie.frame.isShowingHeatmap;
      isheatmap = Lizzie.frame.isShowingHeatmap;
      iskataHeatmapShowOwner = false;
    }
    heatcount = new ArrayList<Integer>();
    heatPolicy = new ArrayList<Double>();
    heatOwnership = new ArrayList<Double>();
    if (isheatmap) {
      sendHeatCommand();
    } else {
      Lizzie.board.clearBestHeatMove();
      if (isPondering) {
        ponder();
      }
      // Lizzie.frame.handleAfterDrawGobanBottom();
    }
    if (Lizzie.config.isDoubleEngineMode() && Lizzie.leelaz2 != null)
      Lizzie.leelaz2.toggleHeatmapSub(bySpace);
  }

  public void toggleHeatmapSub(boolean bySpace) {
    // TODO Auto-generated method stub
    if (isKatago && !bySpace) {
      if (isheatmap) {
        if (iskataHeatmapShowOwner) {
          //  Lizzie.frame.isShowingHeatmap=!Lizzie.frame.isShowingHeatmap;
          isheatmap = Lizzie.frame.isShowingHeatmap;
          iskataHeatmapShowOwner = false;
        } else {
          iskataHeatmapShowOwner = true;
        }
      } else {
        //	Lizzie.frame.isShowingHeatmap=!Lizzie.frame.isShowingHeatmap;
        isheatmap = Lizzie.frame.isShowingHeatmap;
      }
    } else {
      //  Lizzie.frame.isShowingHeatmap=!Lizzie.frame.isShowingHeatmap;
      isheatmap = Lizzie.frame.isShowingHeatmap;
      iskataHeatmapShowOwner = false;
    }
    heatcount = new ArrayList<Integer>();
    heatPolicy = new ArrayList<Double>();
    heatOwnership = new ArrayList<Double>();
    if (isheatmap) {
      // sendHeatCommand();
    } else {
      Lizzie.board.clearBestHeatMove();
      if (isKatago) Lizzie.frame.clearKataEstimate();
      if (isPondering) {
        ponder();
      }
      // Lizzie.frame.handleAfterDrawGobanBottomSub();
    }
  }

  private void sendHeatCommand() {
    if (isKatago) {
      sendCommand("kata-raw-nn " + new Random().nextInt(8));
    } else sendCommand("heatmap");
  }

  public void getParameterScadule(boolean sendCommand) {
    getRcentLine = true;
    if (sendCommand) {
      recentLineNumber = 0;
      sendCommand("kata-get-param playoutDoublingAdvantage");
      sendCommand("kata-get-param analysisWideRootNoise");
      sendCommand("kata-get-rules");
    }
    Runnable runnable =
        new Runnable() {
          public void run() {
            try {
              Thread.sleep(30000);
            } catch (InterruptedException e) {
              // TODO Auto-generated catch block
              e.printStackTrace();
            }
            Lizzie.leelaz.getRcentLine = false;
          }
        };
    Thread thread = new Thread(runnable);
    thread.start();
  }

  public void getSuicidalAndRules() {
    usingSpecificRules = -1;
    if (recentRulesLine.equals("")) {
      canSuicidal = false;
    } else {
      try {
        String line = recentRulesLine;
        JSONObject jo = new JSONObject(new String(line.substring(2)));
        if (jo.optBoolean("suicide", false)) canSuicidal = true;
        else canSuicidal = false;
        if (jo.optString("scoring", "").contentEquals("AREA")
            && jo.optString("ko", "").contentEquals("POSITIONAL")
            && jo.optBoolean("suicide", false)
            && jo.optString("tax", "").contentEquals("NONE")
            && jo.optString("whiteHandicapBonus", "").contentEquals("N")
            && !jo.optBoolean("hasButton", true)) {
          usingSpecificRules = 4; // tt规则
        } else if (jo.optString("scoring", "").contentEquals("AREA")
            && jo.optString("tax", "").contentEquals("NONE")
            && !jo.optBoolean("hasButton", true)) {
          usingSpecificRules = 1; // 中国规则
        } else if (jo.optString("scoring", "").contentEquals("AREA")
            && jo.optString("tax", "").contentEquals("ALL")
            && !jo.optBoolean("hasButton", true)) {
          usingSpecificRules = 2; // 中古规则
        } else if (jo.optString("scoring", "").contentEquals("TERRITORY")
            && jo.optString("tax", "").contentEquals("SEKI")) {
          usingSpecificRules = 3; // 日本规则
        } else if (jo.optString("scoring", "").contentEquals("AREA")
            || jo.optString("scoring", "").contentEquals("TERRITORY")) {
          usingSpecificRules = 5; // 其他规则
        }
      } catch (Exception e) {
      }
    }
  }

  private void leela0110Ponder(boolean first) {
    if (first)
      if (Lizzie.config.isDoubleEngineMode()) {
        if (Lizzie.leelaz2 != null && this != Lizzie.leelaz2) {
          Lizzie.leelaz2.sendCommand("lz-analyze " + getInterval());
        }
      }
    synchronized (this) {
      if (leela0110PonderingBoardData != null) return;
      leela0110PonderingBoardData = Lizzie.board.getData();
      leela0110BestMoves = new ArrayList<>();
      sendCommandNoLeelaz2("time_left b 0 0");
      leela0110PonderingTimer = new Timer();
      leela0110PonderingTimer.schedule(
          new TimerTask() {
            public void run() {
              sendCommandNoLeelaz2("name");
            }
          },
          LEELA0110_PONDERING_INTERVAL_MILLIS);
    }
  }

  public void leela0110StopPonder() {
    if (leela0110PonderingTimer != null) {
      leela0110PonderingTimer.cancel();
      leela0110PonderingTimer = null;
    }
    leela0110PonderingBoardData = null;
  }

  private void leela0110UpdatePonder() {
    leela0110StopPonder();
    if (isPondering) leela0110Ponder(false);
  }

  private boolean isLeela0110PonderingValid() {
    return leela0110PonderingBoardData == Lizzie.board.getData();
  }

  public int getBestMovesPlayouts() {
    return currentTotalPlayouts;
  }

  public boolean isStopPonderingByLimit() {
    return stopByLimit;
  }

  public long getStartPonderTime() {
    return startPonderTime;
  }

  public synchronized void modifyStart() {
    // TODO Auto-generated method stub
    this.cmdNumber++;
    this.modifyNumber++;
  }

  public synchronized void setModifyEnd() {
    // TODO Auto-generated method stub
    cmdNumber -= modifyNumber;
    modifyNumber = 0;
  }

  private synchronized void calculateModifyNumber() {
    // TODO Auto-generated method stub
    cmdNumber -= modifyNumber;
    modifyNumber = 0;
  }

  public void timeLeft(String color, int seconds, int moves, boolean isDuringMove) {
    seconds = Math.max(0, seconds);
    sendCommand("time_left " + color + " " + seconds + " " + moves);
    if (isDuringMove) currentCmdNum++;
  }

  public void timeLeft(String color, float seconds, int moves, boolean isDuringMove) {
    seconds = Math.max(0, seconds);
    sendCommand(
        "time_left " + color + " " + String.format(Locale.ENGLISH, "%.2f", seconds) + " " + moves);
    if (isDuringMove) currentCmdNum++;
  }

  public boolean isProcessDead() {
    return Lizzie.leelaz.process != null && !Lizzie.leelaz.process.isAlive();
  }

  public void maybeAjustPDA(BoardHistoryNode node) {
    // TODO Auto-generated method stub
    if (!isDymPda) return;
    if (Lizzie.board.isFirstWhiteNodeWithHandicap(node)) {
      if (Lizzie.config.chkAutoPDA) sendCommand(Lizzie.config.AutoPDA);
      else sendCommand("dympdacap " + pdaCap);
      if (isPondering()) ponder(true, !Lizzie.board.getHistory().isBlacksTurn());
    }
  }
}
