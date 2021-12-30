package featurecat.lizzie.analysis;

import featurecat.lizzie.Lizzie;
import featurecat.lizzie.gui.EngineFailedMessage;
import featurecat.lizzie.gui.Menu;
import featurecat.lizzie.gui.RemoteEngineData;
import featurecat.lizzie.rules.Board;
import featurecat.lizzie.rules.BoardHistoryNode;
import featurecat.lizzie.rules.SGFParser;
import featurecat.lizzie.rules.Stone;
import featurecat.lizzie.util.Utils;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import org.jdesktop.swingx.util.OS;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ContributeEngine {
  private ArrayList<ContributeGameInfo> contributeGames;
  private ArrayList<ContributeUnParseGameInfo> unParseGameInfos;
  public int watchingGameIndex = -1;
  private int changeWatchingGameIndex = -1;
  private Thread watchGameThread;
  private ContributeGameInfo currentWatchGame;
  private BoardHistoryNode startNode;

  private Process process;
  private boolean isNormalEnd = false;

  private BufferedReader inputStream;
  // private BufferedOutputStream outputStream;
  private BufferedReader errorStream;

  private String engineCommand;
  private ScheduledExecutorService executor;
  private ScheduledExecutorService executorErr;
  private List<String> commands;

  private boolean useJavaSSH = false;
  private ContributeSSHController javaSSH;
  private String ip;
  private String port;
  private String userName;
  private String password;
  private boolean useKeyGen;
  private String keyGenPath;
  public boolean javaSSHClosed;
  private String courseFile = "";
  private String timeStamp;
  private int errorTimes;
  private String errorTips = "";

  public ContributeEngine() {
    if (Lizzie.frame.isContributing) {
      Utils.showMsg("已在跑普贡献中!");
      return;
    }
    if (Lizzie.config.contributeUseCommand) {
      engineCommand = Lizzie.config.contributeCommand;
      if (!engineCommand.contains("-override")) engineCommand += " -override-config ";
      if (!engineCommand.contains(" -config")) {
        engineCommand += " \"serverUrl = https://katagotraining.org/\",";
      }
    } else {
      engineCommand = Lizzie.config.contributeEnginePath + " contribute";
      try {
        File file = new File("");
        courseFile = file.getCanonicalPath();
      } catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
      boolean useConfigFile = Lizzie.config.contributeConfigPath.trim().length() > 0;
      if (useConfigFile) engineCommand += "-config " + Lizzie.config.contributeConfigPath;
      engineCommand += " -override-config ";
      if (!useConfigFile) engineCommand += "\"serverUrl = https://katagotraining.org/\",";
    }
    engineCommand += "\"username = " + Lizzie.config.contributeUserName + "\",";
    engineCommand += "\"password = " + Lizzie.config.contributePassword + "\",";
    engineCommand += "\"maxSimultaneousGames = " + Lizzie.config.contributeBatchGames + "\",";
    engineCommand +=
        "\"includeOwnership = " + (Lizzie.config.contributeShowEstimate ? "true" : "false") + "\",";
    engineCommand += "\"logGamesAsJson = true\"";
    RemoteEngineData remoteData = Utils.getContributeRemoteEngineData();
    this.useJavaSSH = remoteData.useJavaSSH;
    this.ip = remoteData.ip;
    this.port = remoteData.port;
    this.userName = remoteData.userName;
    this.password = remoteData.password;
    this.useKeyGen = remoteData.useKeyGen;
    this.keyGenPath = remoteData.keyGenPath;

    contributeGames = new ArrayList<ContributeGameInfo>();
    unParseGameInfos = new ArrayList<ContributeUnParseGameInfo>();
    startEngine(engineCommand);
    timeStamp = new SimpleDateFormat("yyyy-MM-dd-HH").format(new Date());
    Lizzie.frame.isContributing = true;
    Lizzie.board.clear(false);
    Menu.engineMenu.setText("跑普贡献中");
    Lizzie.frame.refresh();
  }

  private void startEngine(String engineCommand2) {
    commands = Utils.splitCommand(engineCommand);
    if (this.useJavaSSH) {
      this.javaSSH = new ContributeSSHController(this, this.ip, this.port);
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
        //    this.outputStream = new BufferedOutputStream(this.javaSSH.getStdin());
        this.errorStream = new BufferedReader(new InputStreamReader(this.javaSSH.getSterr()));
        javaSSHClosed = false;
      } else {
        javaSSHClosed = true;
        return;
      }
    } else {
      ProcessBuilder processBuilder = new ProcessBuilder(commands);
      processBuilder.redirectErrorStream(true);
      try {
        process = processBuilder.start();
      } catch (IOException e) {
        tryToDignostic(
            Lizzie.resourceBundle.getString("Leelaz.engineFailed")
                + ": "
                + e.getLocalizedMessage());
        return;
      }
      initializeStreams();
    }
    executor = Executors.newSingleThreadScheduledExecutor();
    executor.execute(this::read);
    executorErr = Executors.newSingleThreadScheduledExecutor();
    executorErr.execute(this::readError);
    isNormalEnd = false;
    clearBoardAndView();
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
      e.printStackTrace();
    }
  }

  private void read() {
    try {
      String line = "";
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
    shutdown();
    if (this.useJavaSSH) javaSSHClosed = true;
    if (!isNormalEnd) {
      if (errorTimes < 3) {
        errorTimes++;
        Lizzie.frame.isContributing = true;
        startEngine(engineCommand);
      } else
        tryToDignostic(
            errorTips.length() > 0
                ? errorTips
                : Lizzie.resourceBundle.getString("Leelaz.engineEndUnormalHint"));
    }
    // process = null;
    return;
  }

  private void parseLineForError(String line) {
    Lizzie.frame.addContributeLine(line, false);
    parseTips(line);
  }

  private void parseLine(String line) {
    // TODO Auto-generated method stub
    if (line.startsWith("{")) {
      // json game info
      if (getJsonGameInfo(tryToGetJsonString(line), contributeGames, unParseGameInfos)) {
        if (contributeGames != null) {
          int finishedGames = 0;
          int playingGames = 0;
          for (ContributeGameInfo game : contributeGames) { // 已完成10局,正在进行5局,共15局,正在观看第3局
            if (game.complete) finishedGames++;
            else playingGames++;
          }
          if (Lizzie.frame.contributeView != null)
            Lizzie.frame.contributeView.setGames(finishedGames, playingGames);
          if (watchingGameIndex == -1 && contributeGames.size() > 0) {
            watchingGameIndex = 0;
            currentWatchGame = contributeGames.get(0);
            setGameToBoard(currentWatchGame, watchingGameIndex, false);
            Lizzie.frame.refresh();
            Lizzie.frame.renderVarTree(0, 0, false, false);
            startWatchingGameThread();
          }
        }
      }
      Lizzie.gtpConsole.addLine(line);
    } else {
      Lizzie.frame.addContributeLine(line, true);
      if (line.contains("Finis")) {
        // 2021-11-02 09:21:45+0800: Finished game 8 (training), uploaded sgf
        // katago_contribute/kata1/sgfs/kata1-b40c256-s10312780288-d2513725330/155A1E55A4145135.sgf
        // and training data
        // katago_contribute/kata1/tdata/kata1-b40c256-s10312780288-d2513725330/273F621AC4CF6ACF.npz
        // (8 rows)
        String params[] = line.split(" ");
        String sgfPath = "";
        for (int i = 0; i < params.length - 1; i++) {
          if (params[i].equals("sgf")) sgfPath = params[i + 1];
        }
        // katago_contribute/kata1/sgfs/kata1-b40c256-s10312780288-d2513725330/155A1E55A4145135.sgf
        if (sgfPath.length() > 0) {
          String gameId =
              sgfPath.substring(getLastIndexOfFileSep(sgfPath) + 1, sgfPath.lastIndexOf("."));
          if (contributeGames != null) {
            for (ContributeGameInfo game : contributeGames) {
              if (game.gameId.equals(gameId)) {
                game.complete = true;
                if (game == currentWatchGame) maybePlayLastMove(game);
                if (!useJavaSSH) {
                  game.gameResult = SGFParser.getResult(courseFile + File.separator + sgfPath);
                  if (game == currentWatchGame) setReultToView(game.gameResult);
                }
              }
            }
          }
        }
      }
      parseTips(line);
    }
  }

  private void parseTips(String line) {
    // TODO Auto-generated method stub
    if (line.contains("Starting game")) {
      setTip("开始新的一局");
    }
    if (line.toLowerCase().contains("predownload")) {
      setTip("预先下载权重中...");
    } else if (line.toLowerCase().contains("download")) {
      setTip("正在下载权重...");
    }
    if (line.toLowerCase().contains("Starting")) {
      setTip("开始新的一局...");
    }
    if (line.contains("Invalid username/password")) {
      setTip("错误的用户名或密码");
      errorTips = "错误的用户名或密码";
    }
    if (line.contains("No response from server")) {
      setTip("服务器无回应");
      errorTips = "服务器无回应";
    }
    if (line.contains("Error connecting to server")) {
      setTip("服务器连接失败");
      errorTips = "服务器连接失败";
    }
    if (line.contains(" When uploading")) {
      setTip("上传数据出错");
      errorTips = "上传数据出错";
    }
    if (line.contains("status 200 for initial query")) {
      setTip("初始化错误,请查看控制台");
      errorTips = "初始化错误,请查看控制台";
    }
    if (line.contains("Could not parse serverUrl")) {
      setTip("无法解析服务器地址");
      errorTips = "无法解析服务器地址";
    }
    if (line.contains("Did you verify your email address")) {
      setTip("请确认用户是否经过邮箱验证!");
      errorTips = "请确认用户是否经过邮箱验证!";
    }
    if (line.contains("Model file was incompletely downloaded")) {
      setTip("权重未完全下载");
      errorTips = "权重未完全下载";
    }
    //	      When uploading
    //	      status 200 for initial query
    //	      Could not parse serverUrl
    //	      Did you verify your email address
    //	      Model file was incompletely downloaded
  }

  private void clearBoardAndView() {
    Lizzie.board.clear(false);
  }

  private void setTip(String text) {
    if (Lizzie.frame.contributeView != null) Lizzie.frame.contributeView.setTip(text);
  }

  private void setReultToView(String result) {
    if (Lizzie.frame.contributeView != null) Lizzie.frame.contributeView.setResult(result);
  }

  private void setRulesToView(JSONObject rules) {
    if (Lizzie.frame.contributeView != null) {
      Lizzie.frame.contributeView.setRules(rules);
    }
  }

  private void setTypeAndKomiToView(boolean isMatchGame, double komi) {
    if (Lizzie.frame.contributeView != null) {
      Lizzie.frame.contributeView.setKomi(komi);
      Lizzie.frame.contributeView.setType(isMatchGame ? "评分对局" : "自对弈");
    }
  }

  private int getLastIndexOfFileSep(String path) {
    return Math.max(path.lastIndexOf("/"), path.lastIndexOf("\\"));
  }

  public void normalQuit() {
    isNormalEnd = true;
    Lizzie.frame.isShowingContributeGame = false;
    Lizzie.frame.isContributing = false;
    if (useJavaSSH) javaSSH.close();
    try {
      process.destroy();
    } catch (Exception e) {

    }
    if (watchGameThread != null) watchGameThread.interrupt();
    Menu.engineMenu.setText(Lizzie.resourceBundle.getString("Menu.noEngine"));
    Lizzie.frame.contributeEngine = null;
  }

  private void shutdown() {
    if (useJavaSSH) javaSSH.close();
    try {
      process.destroy();
    } catch (Exception e) {

    }
    Lizzie.frame.isContributing = false;
  }

  private void initializeStreams() {
    inputStream = new BufferedReader(new InputStreamReader(process.getInputStream()));
    //    outputStream = new BufferedOutputStream(process.getOutputStream());
    errorStream = new BufferedReader(new InputStreamReader(process.getErrorStream()));
  }

  private JSONObject tryToGetJsonString(String input) {
    JSONObject json = null;
    try {
      json = new JSONObject(input);
    } catch (JSONException e) {
      e.printStackTrace();
    }

    return json;
  }

  public boolean getJsonGameInfo(
      JSONObject jsonInfo,
      ArrayList<ContributeGameInfo> games,
      ArrayList<ContributeUnParseGameInfo> unParseInfos) {
    if (jsonInfo == null) return false;
    try {
      String gameId = jsonInfo.getString("gameId");
      ContributeGameInfo currentGame = null;
      boolean isExistGame = false;
      if (!games.isEmpty()) {
        for (ContributeGameInfo game : games) {
          if (game.gameId.equals(gameId)) {
            currentGame = game;
            isExistGame = true;
            break;
          }
        }
      }
      if (isExistGame) {
        if (tryToParseJsonGame(currentGame, false, jsonInfo, unParseInfos))
          tryToUseUnParseGameInfos(currentGame, unParseInfos);
        if (currentGame == currentWatchGame) {
          setGameToBoard(currentGame, watchingGameIndex, false);
          if (Lizzie.board.getHistory().getCurrentHistoryNode().next().isPresent()
              && !Lizzie.board.getHistory().getCurrentHistoryNode().next().get().next().isPresent())
            Lizzie.board.nextMove(true);
        }
      } else {
        currentGame = new ContributeGameInfo();
        if (tryToParseJsonGame(currentGame, true, jsonInfo, unParseInfos))
          tryToUseUnParseGameInfos(currentGame, unParseInfos);
        games.add(currentGame);
      }
    } catch (Exception e) {
      e.printStackTrace();
      return false;
    }
    return true;
  }

  private void tryToUseUnParseGameInfos(
      ContributeGameInfo currentGame, ArrayList<ContributeUnParseGameInfo> unParseInfos) {
    ContributeUnParseGameInfo unUsedParseInfo = null;
    if (!unParseInfos.isEmpty()) {
      for (ContributeUnParseGameInfo unParseInfo : unParseInfos) {
        if (unParseInfo.gameId.equals(currentGame.gameId)) {
          unUsedParseInfo = unParseInfo;
          break;
        }
      }
    }
    if (unUsedParseInfo == null) return;
    if (tryToParseJsonGame(currentGame, false, unUsedParseInfo.gameInfo, unParseInfos)) {
      unParseInfos.remove(unUsedParseInfo);
      tryToUseUnParseGameInfos(currentGame, unParseInfos);
    }
  }

  private boolean tryToParseJsonGame(
      ContributeGameInfo currentGame,
      boolean newGame,
      JSONObject jsonInfo,
      ArrayList<ContributeUnParseGameInfo> unParseInfos) {
    boolean success = false;
    // 从jsonInfo中解析已有的一局,更新currentGame,如果isWatching,更新到界面上
    if (newGame) {
      currentGame.gameId = jsonInfo.getString("gameId");
      currentGame.blackPlayer = jsonInfo.getString("blackPlayer");
      currentGame.whitePlayer = jsonInfo.getString("whitePlayer");
      currentGame.sizeX = jsonInfo.getInt("boardXSize");
      currentGame.sizeY = jsonInfo.getInt("boardYSize");
      currentGame.rules = jsonInfo.getJSONObject("rules");
      currentGame.komi = currentGame.rules.getDouble("komi");
      currentGame.isMatchGame = !currentGame.blackPlayer.equals(currentGame.whitePlayer);
    }
    JSONArray initStones = jsonInfo.getJSONArray("initialStones");
    if (initStones.length() > 0) {
      ArrayList<ContributeMoveInfo> initStoneList = new ArrayList<ContributeMoveInfo>();
      for (int i = 0; i < initStones.length(); i++) {
        ContributeMoveInfo move = new ContributeMoveInfo();
        List<Object> moveInfo = initStones.getJSONArray(i).toList();
        move.isBlack = moveInfo.get(0).toString().equals("B");
        move.pos = Board.convertNameToCoordinates(moveInfo.get(1).toString(), currentGame.sizeY);
        move.isPass = move.pos[0] < 0;
        initStoneList.add(move);
      }
      currentGame.initMoveList = initStoneList;
    }
    JSONArray historyMoves = jsonInfo.getJSONArray("moves");
    if (historyMoves.length() > 0) {
      ArrayList<ContributeMoveInfo> historyMoveList = new ArrayList<ContributeMoveInfo>();
      for (int i = 0; i < historyMoves.length(); i++) {
        ContributeMoveInfo move = new ContributeMoveInfo();
        List<Object> moveInfo = historyMoves.getJSONArray(i).toList();
        move.isBlack = moveInfo.get(0).toString().equals("B");
        move.pos = Board.convertNameToCoordinates(moveInfo.get(1).toString(), currentGame.sizeY);
        move.isPass = move.pos[0] < 0;
        historyMoveList.add(move);
      }
      if (newGame) {
        currentGame.moveList = historyMoveList;
      }
      if (newGame || (compareMoveList(historyMoveList, currentGame.moveList))) {
        // 成功,获取最后一手和bestmoves
        ContributeMoveInfo move = new ContributeMoveInfo();
        List<Object> lastMove = jsonInfo.getJSONArray("move").toList();
        move.isBlack = lastMove.get(0).toString().equals("B");
        move.pos = Board.convertNameToCoordinates(lastMove.get(1).toString(), currentGame.sizeY);
        move.isPass = move.pos[0] < 0;
        JSONArray moveInfos = jsonInfo.getJSONArray("moveInfos");
        move.candidates = Utils.getBestMovesFromJsonArray(moveInfos, false, move.isBlack);
        currentGame.moveList.add(move);
      } else {
        ContributeUnParseGameInfo unParseInfo = new ContributeUnParseGameInfo();
        unParseInfo.gameId = jsonInfo.getString("gameId");
        unParseInfo.gameInfo = jsonInfo;
        unParseInfos.add(unParseInfo);
        success = false;
      }
    }
    return success;
  }

  private boolean compareMoveList(
      ArrayList<ContributeMoveInfo> list1, ArrayList<ContributeMoveInfo> list2) {
    if (list1 == null && list2 == null) return true;
    if (list1 == null && list2 != null || list1 != null && list2 == null) return false;
    if (list1.size() != list2.size()) return false;
    for (int i = 0; i < list1.size(); i++) {
      ContributeMoveInfo move1 = list1.get(i);
      ContributeMoveInfo move2 = list2.get(i);
      if (move1.isBlack != move2.isBlack
          || move1.isPass != move2.isPass
          || move1.pos[0] != move2.pos[0]
          || move1.pos[1] != move2.pos[1]) return false;
    }
    return true;
  }

  public synchronized void setGameToBoard(ContributeGameInfo game, int index, boolean saveGame) {
    ArrayList<ContributeMoveInfo> remainList = new ArrayList<ContributeMoveInfo>();
    if (currentWatchGame == game
        && Board.boardWidth == currentWatchGame.sizeX
        && Board.boardHeight == currentWatchGame.sizeY
        && isContributeGameAndCurrentBoardSame(game, remainList)) {
      if (remainList != null && remainList.size() > 0) {
        setContributeMoveList(remainList, !currentWatchGame.complete);
        Lizzie.frame.redrawTree = true;
        Lizzie.frame.refresh();
      }
    } else {
      currentWatchGame = game;
      if (Lizzie.frame.contributeView != null)
        Lizzie.frame.contributeView.setWathGameIndex(watchingGameIndex + 1);
      Lizzie.board.reopen(currentWatchGame.sizeX, currentWatchGame.sizeY);
      Lizzie.board.clear(false);
      Lizzie.board.isKataBoard = true;
      if (game.isMatchGame) {
        Lizzie.board.isPkBoard = true;
        Lizzie.board.isPkBoardKataB = true;
      }
      Lizzie.frame.isShowingContributeGame = true;
      Lizzie.board
          .getHistory()
          .getGameInfo()
          .setPlayerBlack(currentWatchGame.blackPlayer.replaceAll(" ", ""));
      Lizzie.board
          .getHistory()
          .getGameInfo()
          .setPlayerWhite(currentWatchGame.whitePlayer.replaceAll(" ", ""));
      Lizzie.board.getHistory().getGameInfo().setKomi(currentWatchGame.komi);
      setTypeAndKomiToView(currentWatchGame.isMatchGame, currentWatchGame.komi);
      setRulesToView(currentWatchGame.rules);
      if (currentWatchGame.complete) {
        Lizzie.board.getHistory().getGameInfo().setResult(currentWatchGame.gameResult);
        setReultToView(currentWatchGame.gameResult);
      }
      if (currentWatchGame.initMoveList != null && currentWatchGame.initMoveList.size() > 0)
        setContributeMoveList(currentWatchGame.initMoveList, false);
      if (currentWatchGame.moveList != null && currentWatchGame.moveList.size() > 0)
        setContributeMoveList(currentWatchGame.moveList, !currentWatchGame.complete);
      boolean moved = false;
      while (Lizzie.board.getHistory().getCurrentHistoryNode().getData().getPlayouts() <= 0) {
        Lizzie.board.nextMove(false);
        moved = true;
      }
      if (moved) {
        Lizzie.frame.redrawTree = true;
        Lizzie.frame.refresh();
      }
      if (!saveGame
          && Lizzie.config.contributeAutoSave
          && currentWatchGame.complete
          && !currentWatchGame.saved) {
        saveGame(game, index);
      }
    }
  }

  public void saveAllGames() {
    if (contributeGames == null || contributeGames.isEmpty()) {
      Utils.showMsg("没有可以保存的棋局");
      return;
    }
    for (int i = 0; i < contributeGames.size(); i++) {
      saveGame(contributeGames.get(i), i);
    }
    setGameToBoard(currentWatchGame, watchingGameIndex, false);
    Utils.showMsg("棋谱保存在LizzieYzy目录内\"ContributeGames-" + timeStamp + "\"文件夹中");
  }

  private void saveGame(ContributeGameInfo game, int index) {
    if (game != currentWatchGame) {
      setGameToBoard(game, index, true);
    }
    File file = new File("");
    String courseFile = "";
    try {
      courseFile = file.getCanonicalPath();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    File autoSaveFile =
        new File(
            courseFile
                + File.separator
                + "ContributeGames"
                + File.separator
                + timeStamp
                + File.separator
                + (index + 1)
                + "_"
                + game.gameId
                + "_"
                + getShortName(Lizzie.board.getHistory().getGameInfo().getPlayerBlack())
                + "_VS_"
                + getShortName(Lizzie.board.getHistory().getGameInfo().getPlayerWhite())
                + "("
                + Lizzie.board.getHistory().getGameInfo().getResult()
                + ").sgf");
    File fileParent = autoSaveFile.getParentFile();
    if (!fileParent.exists()) {
      fileParent.mkdirs();
    }
    try {
      SGFParser.save(Lizzie.board, autoSaveFile.getPath());
      if (game.complete) game.saved = true;
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  private String getShortName(String name) {
    if (name.length() > 19) return name.substring(0, 19);
    else return name;
  }

  private void maybePlayLastMove(ContributeGameInfo game) {
    // TODO Auto-generated method stub
    if (game.moveList != null && game.moveList.size() > 0) {
      ContributeMoveInfo move = game.moveList.get(game.moveList.size() - 1);
      BoardHistoryNode lastNode = Lizzie.board.getHistory().getMainEnd();
      if (move.isPass) {
        if (lastNode.getData().lastMoveColor.isBlack() != move.isBlack)
          Lizzie.board
              .getHistory()
              .pass(move.isBlack ? Stone.BLACK : Stone.WHITE, false, false, false, true);
      } else
        Lizzie.board
            .getHistory()
            .place(
                move.pos[0],
                move.pos[1],
                move.isBlack ? Stone.BLACK : Stone.WHITE,
                false,
                false,
                true);
    }
  }

  private void setContributeMoveList(
      ArrayList<ContributeMoveInfo> remainList, boolean holdLastMove) {
    for (int i = 0; i < remainList.size(); i++) {
      ContributeMoveInfo move = remainList.get(i);
      if (move.candidates != null) {
        Lizzie.board
            .getHistory()
            .getMainEnd()
            .getData()
            .tryToSetBestMoves(
                move.candidates,
                Lizzie.board.getHistory().getCurrentTurnPlayerShortName(),
                false,
                MoveData.getPlayouts(move.candidates));
        if (Lizzie.board.getHistory().getMainEnd().getData().getPlayouts() > 0) {
          Lizzie.board.getHistory().getMainEnd().getData().comment =
              SGFParser.formatCommentPk(Lizzie.board.getHistory().getMainEnd());
        }
      }
      if (i < (holdLastMove ? remainList.size() - 1 : remainList.size())) {
        if (move.isPass)
          Lizzie.board
              .getHistory()
              .pass(move.isBlack ? Stone.BLACK : Stone.WHITE, false, false, false, true);
        else
          Lizzie.board
              .getHistory()
              .place(
                  move.pos[0],
                  move.pos[1],
                  move.isBlack ? Stone.BLACK : Stone.WHITE,
                  false,
                  false,
                  true);
      }
    }
  }

  private boolean isContributeGameAndCurrentBoardSame(
      ContributeGameInfo watchGame, ArrayList<ContributeMoveInfo> remainList) {
    boolean isSame = true;
    startNode = Lizzie.board.getHistory().getStart();
    while (startNode.next().isPresent() && !startNode.getData().lastMove.isPresent())
      startNode = startNode.next().get();
    if (watchGame.initMoveList != null && watchGame.initMoveList.size() > 0) {
      isSame = compareListAndNode(watchGame.initMoveList, startNode, remainList);
    }
    if (watchGame.moveList != null && watchGame.moveList.size() > 0) {
      isSame = compareListAndNode(watchGame.moveList, startNode, remainList);
    }
    return isSame;
  }

  private boolean compareListAndNode(
      ArrayList<ContributeMoveInfo> list,
      BoardHistoryNode node,
      ArrayList<ContributeMoveInfo> remainList) {
    boolean started = false;

    for (int i = 0; i < list.size(); i++) {
      ContributeMoveInfo move = list.get(i);
      if (started || !move.isPass) {
        started = true;
        if (node.getData().lastMove.isPresent()) {
          if (node.getData().lastMove.get()[0] != move.pos[0]
              || node.getData().lastMove.get()[1] != move.pos[1]) return false;
          if (node.next().isPresent()) node = node.next().get();
          else {
            getRemainList(list, remainList, i + 1);
            return true;
          }

        } else {
          if (move.isPass) {
            if (node.next().isPresent()) node = node.next().get();
            else return true;
          } else {
            getRemainList(list, remainList, i + 1);
            return true;
          }
        }
      }
    }
    return true;
  }

  private void getRemainList(
      ArrayList<ContributeMoveInfo> list, ArrayList<ContributeMoveInfo> remainList, int i) {
    for (; i < list.size(); i++) {
      remainList.add(list.get(i));
    }
  }

  public void tryToDignostic(String message) {
    EngineFailedMessage engineFailedMessage =
        new EngineFailedMessage(
            commands, engineCommand, message, !useJavaSSH && OS.isWindows(), false, true);
    engineFailedMessage.setModal(true);
    engineFailedMessage.setVisible(true);
  }

  private void startWatchingGameThread() {
    Runnable runnable =
        new Runnable() {
          public void run() {
            int timeCount = 0;
            BoardHistoryNode node = Lizzie.board.getHistory().getCurrentHistoryNode();
            while (true) {
              try {
                Thread.sleep(50);
                timeCount++;
              } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                break;
              }
              if (changeWatchingGameIndex >= 0) {
                if (changeWatchingGameIndex > contributeGames.size() - 1)
                  changeWatchingGameIndex = contributeGames.size() - 1;
                if (changeWatchingGameIndex != watchingGameIndex) {
                  watchingGameIndex = changeWatchingGameIndex;
                  changeWatchingGameIndex = -1;
                  changeGame(watchingGameIndex);
                  node = Lizzie.board.getHistory().getCurrentHistoryNode();
                  timeCount = 0;
                }
              }
              if (Lizzie.config.contributeWatchAutoPlay) {
                if (timeCount * 50.0 / 1000.0 >= Lizzie.config.contributeWatchAutoPlayInterval) {
                  if (node == Lizzie.board.getHistory().getCurrentHistoryNode()) {
                    if (!Lizzie.board.nextMove(true)) {
                      boolean shouldGoToNextGame = Lizzie.config.contributeWatchAutoPlayNextGame;
                      if (shouldGoToNextGame && currentWatchGame.complete) {
                        boolean needChange = false;
                        while (shouldGoToNextGame
                            && contributeGames.size() > watchingGameIndex + 1) {
                          watchingGameIndex++;
                          needChange = true;
                          shouldGoToNextGame = false;
                          if (Lizzie.config.contributeWatchSkipNone19) {
                            if (contributeGames.get(watchingGameIndex).sizeX != 19
                                || contributeGames.get(watchingGameIndex).sizeY != 19)
                              shouldGoToNextGame = true;
                          }
                        }
                        if (needChange) {
                          changeGame(watchingGameIndex);
                          node = Lizzie.board.getHistory().getCurrentHistoryNode();
                          timeCount = 0;
                        }
                      }
                    }
                  }
                  node = Lizzie.board.getHistory().getCurrentHistoryNode();
                  timeCount = 0;
                }
              }
            }
          }

          private void changeGame(int index) {
            // TODO Auto-generated method stub
            setGameToBoard(contributeGames.get(watchingGameIndex), watchingGameIndex, false);
            if (Lizzie.config.contributeWatchAlwaysLastMove) while (Lizzie.board.nextMove(false)) ;
            Lizzie.frame.redrawTree = true;
            Lizzie.frame.refresh();

            new Thread() {
              public void run() {
                try {
                  Thread.sleep(100);
                } catch (InterruptedException e1) {
                  // TODO Auto-generated catch block
                  e1.printStackTrace();
                }
                Lizzie.frame.renderVarTree(0, 0, false, true);
              }
            }.start();
          }
        };
    watchGameThread = new Thread(runnable);
    watchGameThread.start();
  }

  public void setWatchGame(int index) {
    // TODO Auto-generated method stub
    changeWatchingGameIndex = index;
  }
}