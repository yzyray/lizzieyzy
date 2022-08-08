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
import java.util.ResourceBundle;
import java.util.Stack;
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
  // private HashMap<Integer, List<MoveData>> resultMap = new HashMap<Integer, List<MoveData>>();
  private HashMap<Integer, BoardHistoryNode> analyzeMap = new HashMap<Integer, BoardHistoryNode>();
  private int globalID;
  private int resultCount;
  // private int analyzeNumberCount;
  // private BoardHistoryNode startAnalyzeNode;
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
    if (waitFrame != null) waitFrame.setVisible(false);
    tryToDignostic(errMsg);
    AnalysisSettings analysisSettings = new AnalysisSettings(true, true);
    analysisSettings.setVisible(true);
  }

  public void tryToDignostic(String message) {
    EngineFailedMessage engineFailedMessage =
        new EngineFailedMessage(
            commands, engineCommand, message, !useJavaSSH && OS.isWindows(), false, false);
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
      System.out.println("Flash analyze process ended.");
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
        try {
          parseResult(line);
        } catch (Exception e) {
          e.printStackTrace();
        }
      } else Lizzie.gtpConsole.addLine(line);
    }
  }

  public void parseResult(String line) {
    JSONObject result;
    result = new JSONObject(line);
    JSONArray moveInfos = result.getJSONArray("moveInfos");
    int id = Integer.parseInt(result.getString("id"));
    BoardHistoryNode node = analyzeMap.get(id);
    List<MoveData> moves = Utils.getBestMovesFromJsonArray(moveInfos, true, true);
    if (result.has("ownership")) {
      JSONArray ownership = result.getJSONArray("ownership");
      List<Object> list = ownership.toList();
      node.getData()
          .tryToSetBestMoves(
              moves,
              resourceBundle.getString("AnalysisEngine.flashAnalyze"),
              false,
              MoveData.getPlayouts(moves),
              (ArrayList<Double>) (List) list);
    } else
      node.getData()
          .tryToSetBestMoves(
              moves,
              resourceBundle.getString("AnalysisEngine.flashAnalyze"),
              false,
              MoveData.getPlayouts(moves));

    node.getData().comment = SGFParser.formatComment(node);
    resultCount++;
    waitFrame.setProgress(resultCount, analyzeMap.size());
    if (resultCount == analyzeMap.size()) setResult();
  }

  private void setResult() {
    Lizzie.board.clearPkBoardStat();
    Lizzie.board.isKataBoard = true;
    boolean oriEnableLizzieCache = Lizzie.config.enableLizzieCache;
    if (Lizzie.config.analysisAlwaysOverride) {
      Lizzie.config.enableLizzieCache = false;
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

  public void normalQuit() {
    // TODO Auto-generated method stub
    isNormalEnd = true;
    if (this.useJavaSSH) this.javaSSH.close();
    else this.process.destroyForcibly();
  }

  public void startRequestAllBranches() {
    if (!isLoaded) return;
    analyzeMap.clear();
    globalID = 1;
    resultCount = 0;
    if (Lizzie.leelaz.isPondering()) {
      Lizzie.leelaz.togglePonder();
      shouldRePonder = true;
    } else shouldRePonder = false;
    BoardHistoryNode node = Lizzie.board.getHistory().getStart();
    Stack<BoardHistoryNode> stack = new Stack<>();
    stack.push(node);
    while (!stack.isEmpty()) {
      BoardHistoryNode cur = stack.pop();
      sendRequest(cur);
      if (cur.numberOfChildren() >= 1) {
        for (int i = cur.numberOfChildren() - 1; i >= 0; i--)
          stack.push(cur.getVariations().get(i));
      }
    }
    if (analyzeMap.size() > 0) {
      waitFrame = new WaitForAnalysis();
      if (Lizzie.config.analysisEnginePreLoad) waitFrame.setProgress(0, analyzeMap.size());
      waitFrame.setLocationRelativeTo(Lizzie.frame != null ? Lizzie.frame : null);
      waitFrame.setVisible(true);
    } else if (Lizzie.frame.isBatchAnalysisMode) {
      Lizzie.frame.flashAutoAnaSaveAndLoad();
    }
  }

  public void startRequest(int startMove, int endMove) {
    if (!isLoaded) return;
    analyzeMap.clear();
    globalID = 1;
    resultCount = 0;
    if (Lizzie.leelaz.isPondering()) {
      Lizzie.leelaz.togglePonder();
      shouldRePonder = true;
    } else shouldRePonder = false;
    BoardHistoryNode node = Lizzie.board.getHistory().getStart();
    while (!node.getData().lastMove.isPresent() && node.next().isPresent()) {
      node = node.next().get();
    }
    int moveNum = 1;
    boolean startAnalyze = false;
    if (startMove < 0 || startMove == 1) {
      startAnalyze = true;
    }
    while (node.next().isPresent()) {
      if (startAnalyze) sendRequest(node);
      moveNum++;
      node = node.next().get();
      if (moveNum == startMove) {
        startAnalyze = true;
      }
      if (moveNum == endMove) {
        startAnalyze = false;
        break;
      }
    }
    if (startAnalyze) sendRequest(node);
    if (analyzeMap.size() > 0) {
      waitFrame = new WaitForAnalysis();
      if (Lizzie.config.analysisEnginePreLoad) waitFrame.setProgress(0, analyzeMap.size());
      waitFrame.setLocationRelativeTo(Lizzie.frame != null ? Lizzie.frame : null);
      waitFrame.setVisible(true);
    } else if (Lizzie.frame.isBatchAnalysisMode) {
      Lizzie.frame.flashAutoAnaSaveAndLoad();
    }
  }

  public void sendRequest(BoardHistoryNode analyzeNode) {
    JSONObject request = new JSONObject();
    int maxVisits =
        Lizzie.frame.isBatchAnalysisMode
            ? Math.max(2, Lizzie.config.batchAnalysisPlayouts)
            : Lizzie.config.analysisMaxVisits + 1;
    request.put("id", String.valueOf(globalID));
    request.put("maxVisits", maxVisits);
    request.put("includePVVisits", Lizzie.config.showPvVisits);
    request.put("includeOwnership", Lizzie.config.showKataGoEstimate);
    request.put(
        "includeMovesOwnership",
        Lizzie.config.showKataGoEstimate && Lizzie.config.useMovesOwnership);
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
      request.put("initialStones", initialStoneList);
    }
    JSONObject ruleSettings;
    if (!Lizzie.config.analysisUseCurrentRules) {
      if (!Lizzie.config.analysisSpecificRules.equals("")) {
        ruleSettings = new JSONObject(Lizzie.config.analysisSpecificRules);
        request.put("rules", ruleSettings);
      } else request.put("rules", "tromp-taylor");
    } else if (!Lizzie.config.currentKataGoRules.equals("")) {
      ruleSettings = new JSONObject(new String(Lizzie.config.currentKataGoRules.substring(2)));
      request.put("rules", ruleSettings);
    } else if (Lizzie.config.autoLoadKataRules && !Lizzie.config.kataRules.equals("")) {
      ruleSettings = new JSONObject(Lizzie.config.kataRules);
      request.put("rules", ruleSettings);
    } else request.put("rules", "tromp-taylor");
    request.put("komi", Lizzie.board.getHistory().getGameInfo().getKomi());
    request.put("boardXSize", Board.boardWidth);
    request.put("boardYSize", Board.boardHeight);
    ArrayList<Integer> moveTurns = new ArrayList<Integer>();
    ArrayList<String[]> moveList = new ArrayList<String[]>();
    BoardHistoryNode node = analyzeNode;
    while (node.previous().isPresent()) {
      if (node.getData().lastMove.isPresent()) {
        int[] move = node.getData().lastMove.get();
        if (node.getData().lastMoveColor.isBlack())
          moveList.add(new String[] {"B", Board.convertCoordinatesToName(move[0], move[1])});
        else moveList.add(new String[] {"W", Board.convertCoordinatesToName(move[0], move[1])});
      } else {
        if (node.getData().lastMoveColor.isBlack()) moveList.add(new String[] {"B", "pass"});
        else moveList.add(new String[] {"W", "pass"});
      }
      node = node.previous().get();
    }
    ArrayList<String[]> moveList2 = new ArrayList<String[]>();
    for (int i = moveList.size() - 1; i >= 0; i--) {
      moveList2.add(moveList.get(i));
    }
    moveTurns.add(moveList2.size());
    request.put("moves", moveList2);
    request.put("analyzeTurns", moveTurns);
    JSONObject overrideSettings = new JSONObject();
    overrideSettings.put("reportAnalysisWinratesAs", "SIDETOMOVE");
    request.put("overrideSettings", overrideSettings);
    sendCommand(request.toString());
    analyzeMap.put(globalID, analyzeNode);
    globalID++;
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
