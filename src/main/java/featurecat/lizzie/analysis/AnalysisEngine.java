package featurecat.lizzie.analysis;

import featurecat.lizzie.Lizzie;
import featurecat.lizzie.gui.AnalysisSettings;
import featurecat.lizzie.gui.EngineFailedMessage;
import featurecat.lizzie.gui.RemoteEngineData;
import featurecat.lizzie.gui.WaitForAnalysis;
import featurecat.lizzie.rules.Board;
import featurecat.lizzie.rules.BoardHistoryNode;
import featurecat.lizzie.rules.Movelist;
import featurecat.lizzie.rules.SGFParser;
import featurecat.lizzie.util.Utils;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import org.jdesktop.swingx.util.OS;
import org.json.JSONArray;
import org.json.JSONObject;

public class AnalysisEngine {
  public Process process;
  public boolean isNormalEnd = false;
  private final ResourceBundle resourceBundle = Lizzie.resourceBundle;

  private BufferedReader inputStream;
  private BufferedOutputStream outputStream;
  private BufferedReader errorStream;

  private String engineCommand;
  private ScheduledExecutorService executor;
  private ScheduledExecutorService executorErr;
  private List<String> commands;
  private boolean isPreLoad;
  private HashMap<Integer, List<MoveData>> resultMap = new HashMap<Integer, List<MoveData>>();
  private int analyzeNumberCount;
  private BoardHistoryNode startAnalyzeNode;
  private int startAnalyzeNumber;
  public WaitForAnalysis waitFrame;

  public boolean useJavaSSH = false;
  public String ip;
  public String port;
  public String userName;
  public String password;
  public boolean useKeyGen;
  public String keyGenPath;
  public AnalysisEngineSSHController javaSSH;
  public boolean javaSSHClosed;
  private boolean shouldRePonder = false;
  private boolean isLoaded = false;

  public AnalysisEngine(boolean isPreLoad) throws IOException {
    engineCommand = Lizzie.config.analysisEngineCommand;
    this.isPreLoad = isPreLoad;
    RemoteEngineData remoteData = Utils.getAnalysisEngineRemoteEngineData();
    this.useJavaSSH = remoteData.useJavaSSH;
    this.ip = remoteData.ip;
    this.port = remoteData.port;
    this.userName = remoteData.userName;
    this.password = remoteData.password;
    this.useKeyGen = remoteData.useKeyGen;
    this.keyGenPath = remoteData.keyGenPath;

    startEngine(engineCommand);
  }

  public void startEngine(String engineCommand) {
    commands = Utils.splitCommand(engineCommand);
    if (this.useJavaSSH) {
      this.javaSSH = new AnalysisEngineSSHController(this, this.ip, this.port, this.isPreLoad);
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
        this.inputStream = new BufferedReader(new InputStreamReader(this.javaSSH.getStdout()));
        this.outputStream = new BufferedOutputStream(this.javaSSH.getStdin());
        this.errorStream = new BufferedReader(new InputStreamReader(this.javaSSH.getSterr()));
        javaSSHClosed = false;
        isLoaded = true;
      } else {
        javaSSHClosed = true;
        isLoaded = false;
        return;
      }
    } else {
      ProcessBuilder processBuilder = new ProcessBuilder(commands);
      processBuilder.redirectErrorStream(true);
      try {
        process = processBuilder.start();
        isLoaded = true;
      } catch (IOException e) {
        // TODO Auto-generated catch block
        // System.out.println(e.getLocalizedMessage());
        showErrMsg(
            resourceBundle.getString("Leelaz.engineFailed") + ": " + e.getLocalizedMessage());
        process = null;
        isLoaded = false;
        return;
      }
      initializeStreams();
    }
    executor = Executors.newSingleThreadScheduledExecutor();
    executor.execute(this::read);
    executorErr = Executors.newSingleThreadScheduledExecutor();
    executorErr.execute(this::readError);
    isNormalEnd = false;
  }

  private void showErrMsg(String errMsg) {
    if (isPreLoad) return;
    tryToDignostic(errMsg);
    AnalysisSettings analysisSettings = new AnalysisSettings(true, true);
    analysisSettings.setVisible(true);
  }

  public void tryToDignostic(String message) {
    EngineFailedMessage engineFailedMessage =
        new EngineFailedMessage(
            commands, engineCommand, message, !useJavaSSH && OS.isWindows(), false);
    engineFailedMessage.setModal(true);
    engineFailedMessage.setVisible(true);
  }

  private void initializeStreams() {
    inputStream = new BufferedReader(new InputStreamReader(process.getInputStream()));
    outputStream = new BufferedOutputStream(process.getOutputStream());
    errorStream = new BufferedReader(new InputStreamReader(process.getErrorStream()));
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
    Lizzie.gtpConsole.addErrorLine(line + "\n");
  }

  private void read() {
    try {
      String line = "";
      // while ((c = inputStream.read()) != -1) {
      while ((line = inputStream.readLine()) != null) {
        try {
          parseLine(line.toString());
        } catch (Exception ex) {
          ex.printStackTrace();
        }
      }
      // this line will be reached when engine shuts down
      if (this.useJavaSSH) javaSSHClosed = true;
      System.out.println("estimate process ended.");
      // Do no exit for switching weights
      // System.exit(-1);
    } catch (IOException e) {
    }
    if (this.useJavaSSH) javaSSHClosed = true;
    isLoaded = false;
    if (!isNormalEnd) {
      showErrMsg(resourceBundle.getString("Leelaz.engineEndUnormalHint"));
      if (!isPreLoad && !Lizzie.gtpConsole.isVisible()) Lizzie.gtpConsole.setVisible(true);
    }
    process = null;
    shutdown();
    return;
  }

  private void parseLine(String line) {
    synchronized (this) {
      if (line.startsWith("{")) {
        parseResult(line);
      } else Lizzie.gtpConsole.addLine(line);
    }
  }

  public void parseResult(String line) {
    JSONObject result;
    result = new JSONObject(line);
    int turnNumber = result.getInt("turnNumber");
    JSONArray moveInfos = result.getJSONArray("moveInfos");
    resultMap.put(turnNumber, Utils.getBestMovesFromJsonArray(moveInfos));
    waitFrame.setProgress(resultMap.size(), analyzeNumberCount);
    tryToSetResult();
  }

  private void tryToSetResult() {
    if (resultMap.size() == analyzeNumberCount) {
      Lizzie.board.clearPkBoardStat();
      Lizzie.board.isKataBoard = true;
      boolean oriEnableLizzieCache = Lizzie.config.enableLizzieCache;
      if (Lizzie.config.analysisAlwaysOverride) {
        Lizzie.config.enableLizzieCache = false;
      }
      int moveNumber = startAnalyzeNumber;
      int times = 0;
      while (times < resultMap.size() && startAnalyzeNode.next().isPresent()) {
        List<MoveData> moves = resultMap.get(moveNumber);
        startAnalyzeNode
            .getData()
            .tryToSetBestMoves(
                moves,
                resourceBundle.getString("AnalysisEngine.flashAnalyze"),
                false,
                MoveData.getPlayouts(moves));
        times++;
        startAnalyzeNode.getData().comment = SGFParser.formatComment(startAnalyzeNode);
        moveNumber++;
        startAnalyzeNode = startAnalyzeNode.next().get();
      }
      if (times < resultMap.size()) {
        List<MoveData> moves = resultMap.get(moveNumber);
        startAnalyzeNode
            .getData()
            .tryToSetBestMoves(
                moves,
                resourceBundle.getString("AnalysisEngine.flashAnalyze"),
                false,
                MoveData.getPlayouts(moves));
        startAnalyzeNode.getData().comment = SGFParser.formatComment(startAnalyzeNode);
      }
      Lizzie.board.setMovelistAll();
      if (Lizzie.board.getHistory().getCurrentHistoryNode() == Lizzie.board.getHistory().getStart())
        Lizzie.board.nextMove(true);
      Lizzie.frame.refresh();
      if (Lizzie.config.analysisAutoQuit && !Lizzie.frame.isBatchAna) {
        normalQuit();
      }
      if (Lizzie.config.analysisAlwaysOverride)
        Lizzie.config.enableLizzieCache = oriEnableLizzieCache;
      if (shouldRePonder && !Lizzie.leelaz.isPondering()) Lizzie.leelaz.togglePonder();
      Lizzie.frame.renderVarTree(0, 0, false, false);
    }
  }

  public void normalQuit() {
    // TODO Auto-generated method stub
    isNormalEnd = true;
    if (this.useJavaSSH) this.javaSSH.close();
    else this.process.destroyForcibly();
  }

  public void sendRequest(int startMove, int endMove) {
    if (!isLoaded) return;
    resultMap.clear();
    if (Lizzie.leelaz.isPondering()) {
      Lizzie.leelaz.togglePonder();
      shouldRePonder = true;
    } else shouldRePonder = false;
    JSONObject testRequest = new JSONObject();
    boolean isEmptyGame = false;
    int maxVisits =
        Lizzie.frame.isBatchAnalysisMode
            ? Math.max(2, Lizzie.config.batchAnalysisPlayouts)
            : Lizzie.config.analysisMaxVisits + 1;
    testRequest.put("id", "mainTrunk");
    testRequest.put("maxVisits", maxVisits);
    testRequest.put("includePVVisits", Lizzie.config.showPvVisits);
    if (Lizzie.board.hasStartStone) {
      ArrayList<String[]> initialStoneList = new ArrayList<String[]>();
      for (Movelist mv : Lizzie.board.startStonelist) {
        if (!mv.ispass) {
          if (mv.isblack) {
            initialStoneList.add(new String[] {"B", Board.convertCoordinatesToName(mv.x, mv.y)});

          } else {
            initialStoneList.add(new String[] {"W", Board.convertCoordinatesToName(mv.x, mv.y)});
          }
        }
      }
      testRequest.put("initialStones", initialStoneList);
    }
    JSONObject ruleSettings;
    if (!Lizzie.config.analysisUseCurrentRules) {
      if (!Lizzie.config.analysisSpecificRules.equals("")) {
        ruleSettings = new JSONObject(Lizzie.config.analysisSpecificRules);
        testRequest.put("rules", ruleSettings);
      } else testRequest.put("rules", "tromp-taylor");
    } else if (!Lizzie.config.currentKataGoRules.equals("")) {
      ruleSettings = new JSONObject(new String(Lizzie.config.currentKataGoRules.substring(2)));
      testRequest.put("rules", ruleSettings);
    } else if (Lizzie.config.autoLoadKataRules && !Lizzie.config.kataRules.equals("")) {
      ruleSettings = new JSONObject(Lizzie.config.kataRules);
      testRequest.put("rules", ruleSettings);
    } else testRequest.put("rules", "tromp-taylor");
    testRequest.put("komi", Lizzie.board.getHistory().getGameInfo().getKomi());
    testRequest.put("boardXSize", Board.boardHeight);
    testRequest.put("boardYSize", Board.boardWidth);
    // analyzeTurns
    // moves
    ArrayList<Integer> moveTurns = new ArrayList<Integer>();
    ArrayList<String[]> moveList = new ArrayList<String[]>();
    // while (nextMove()) ;
    Optional<int[]> passStep = Optional.empty();
    BoardHistoryNode node = Lizzie.board.getHistory().getStart();
    while (!node.getData().lastMove.isPresent() && node.next().isPresent()) {
      node = node.next().get();
    }
    int moveNum = 1;
    boolean startCountAnalyzeTurns = false;
    if (startMove < 0 || startMove == 1) {
      startAnalyzeNode = node;
      startAnalyzeNumber = 1;
      startCountAnalyzeTurns = true;
    }
    while (node.next().isPresent()) {
      Optional<int[]> move = node.getData().lastMove;
      if (move == passStep) {
        if (node.getData().lastMoveColor.isBlack()) moveList.add(new String[] {"B", "pass"});
        else moveList.add(new String[] {"W", "pass"});
      } else {
        if (node.getData().lastMoveColor.isBlack())
          moveList.add(
              new String[] {"B", Board.convertCoordinatesToName(move.get()[0], move.get()[1])});
        else
          moveList.add(
              new String[] {"W", Board.convertCoordinatesToName(move.get()[0], move.get()[1])});
      }
      if (startCountAnalyzeTurns) moveTurns.add(moveNum);
      moveNum++;
      node = node.next().get();
      if (moveNum == startMove) {
        startAnalyzeNode = node;
        startAnalyzeNumber = moveNum;
        startCountAnalyzeTurns = true;
      }
      if (moveNum == endMove) {
        startCountAnalyzeTurns = false;
        break;
      }
    }

    Optional<int[]> move = node.getData().lastMove;
    if (move == passStep) {
      if (moveList.isEmpty()) isEmptyGame = true;
      if (node.getData().lastMoveColor.isBlack()) moveList.add(new String[] {"B", "pass"});
      else moveList.add(new String[] {"W", "pass"});
    } else {
      if (node.getData().lastMoveColor.isBlack())
        moveList.add(
            new String[] {"B", Board.convertCoordinatesToName(move.get()[0], move.get()[1])});
      else
        moveList.add(
            new String[] {"W", Board.convertCoordinatesToName(move.get()[0], move.get()[1])});
    }
    if (moveList.isEmpty()) isEmptyGame = true;
    // if (endMove > 0 && moveNum-2 > endMove) startCountAnalyzeTurns = false;
    // if (startCountAnalyzeTurns)
    moveTurns.add(moveNum);
    testRequest.put("moves", moveList);
    testRequest.put("analyzeTurns", moveTurns);
    JSONObject overrideSettings = new JSONObject();
    overrideSettings.put("reportAnalysisWinratesAs", "SIDETOMOVE");
    testRequest.put("overrideSettings", overrideSettings);
    if (!isEmptyGame) {
      analyzeNumberCount = moveTurns.size();
      sendCommand(testRequest.toString());
      waitFrame = new WaitForAnalysis();
      if (Lizzie.config.analysisEnginePreLoad) waitFrame.setProgress(0, analyzeNumberCount);
      waitFrame.setLocationRelativeTo(Lizzie.frame != null ? Lizzie.frame : null);
      waitFrame.setVisible(true);
    } else if (Lizzie.frame.isBatchAnalysisMode) {
      Lizzie.frame.flashAutoAnaSaveAndLoad();
    }
  }

  public void shutdown() {
    // isShuttingdown = true;
    if (useJavaSSH) javaSSH.close();
    process.destroy();
  }

  public void sendCommand(String command) {
    try {
      outputStream.write((command + "\n").getBytes());
      outputStream.flush();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
