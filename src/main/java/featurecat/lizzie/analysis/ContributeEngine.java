package featurecat.lizzie.analysis;

import featurecat.lizzie.Lizzie;
import featurecat.lizzie.gui.EngineFailedMessage;
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
import java.util.ArrayList;
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
  private int watchingGameIndex = -1;
  private ContributeGameInfo currentWatchGame;

  private Process process;
  public boolean isNormalEnd = false;

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
  private String engineParentPath = "";

  public ContributeEngine() {
    if (Lizzie.config.contributeUseCommand) {
      engineCommand = Lizzie.config.contributeCommand;
      try {
        String katagoPath =
            Lizzie.config.contributeCommand.substring(
                0, Lizzie.config.contributeCommand.toLowerCase().lastIndexOf("katago"));
        engineParentPath = katagoPath.substring(0, getLastIndexOfFileSep(katagoPath));
      } catch (Exception e) {
        e.printStackTrace();
      }
    } else {
      engineCommand = Lizzie.config.contributeEnginePath + " contribute";
      try {
        engineParentPath =
            Lizzie.config.contributeEnginePath.substring(
                0, getLastIndexOfFileSep(Lizzie.config.contributeEnginePath));
      } catch (Exception e) {
        e.printStackTrace();
      }
      boolean useConfigFile = Lizzie.config.contributeConfigPath.trim().length() > 0;
      if (useConfigFile) engineCommand += "-config " + Lizzie.config.contributeConfigPath;
      engineCommand += " -override-config ";
      if (!useConfigFile) engineCommand += "\"serverUrl = https://katagotraining.org/\",";
      engineCommand += "\"username = " + Lizzie.config.contributeUserName + "\",";
      engineCommand += "\"password = " + Lizzie.config.contributePassword + "\",";
      engineCommand += "\"maxSimultaneousGames = " + Lizzie.config.contributeBatchGames + "\",";
      engineCommand +=
          "\"includeOwnership = "
              + (Lizzie.config.contributeShowEstimate ? "true" : "false")
              + "\",";
      engineCommand += "\"logGamesAsJson = true\",";
    }
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
        process = null;
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
    if (this.useJavaSSH) javaSSHClosed = true;
    if (!isNormalEnd) {
      tryToDignostic(Lizzie.resourceBundle.getString("Leelaz.engineEndUnormalHint"));
    }
    process = null;
    shutdown();
    return;
  }

  private void parseLineForError(String line) {
    // TODO Auto-generated method stub

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
        }
      }
    } else if (line.contains("Finished game")) {
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
              if (!useJavaSSH) {
                game.gameResult = SGFParser.getResult(engineParentPath + File.separator + sgfPath);
                if (game == currentWatchGame) setReultToView(game.gameResult);
              }
            }
          }
        }
      }
    }
  }

  private void setReultToView(String result) {
    if (Lizzie.frame.contributeView != null) Lizzie.frame.contributeView.setResult(result);
  }

  private int getLastIndexOfFileSep(String path) {
    return Math.max(path.lastIndexOf("/"), path.lastIndexOf("\\"));
  }

  private void normalQuit() {
    isNormalEnd = true;
    if (this.useJavaSSH) this.javaSSH.close();
    else this.process.destroyForcibly();
  }

  private void shutdown() {
    if (useJavaSSH) javaSSH.close();
    process.destroy();
  }

  private void initializeStreams() {
    inputStream = new BufferedReader(new InputStreamReader(process.getInputStream()));
    //    outputStream = new BufferedOutputStream(process.getOutputStream());
    errorStream = new BufferedReader(new InputStreamReader(process.getErrorStream()));
  }

  // public void test() {
  //	  String jsonTestMove1 =
  //
  // "{\"blackPlayer\":\"kata1-b40c256-s10312780288-d2513725330\",\"boardXSize\":19,\"boardYSize\":19,\"gameId\":\"279066229A41A62D\",\"initialPlayer\":\"W\",\"initialStones\":[[\"W\",\"D17\"],[\"W\",\"G17\"],[\"B\",\"H17\"],[\"W\",\"O17\"],[\"B\",\"C16\"],[\"W\",\"E16\"],[\"B\",\"G16\"],[\"B\",\"Q16\"],[\"W\",\"B15\"],[\"B\",\"C15\"],[\"B\",\"E15\"],[\"W\",\"B14\"],[\"W\",\"C14\"],[\"B\",\"D14\"],[\"B\",\"R14\"],[\"W\",\"A13\"],[\"W\",\"C13\"],[\"B\",\"D13\"],[\"W\",\"B12\"],[\"B\",\"C12\"],[\"W\",\"D12\"],[\"B\",\"B11\"],[\"B\",\"C11\"],[\"W\",\"D11\"],[\"W\",\"C10\"],[\"B\",\"M6\"],[\"B\",\"P4\"],[\"B\",\"Q4\"],[\"B\",\"R4\"],[\"W\",\"C3\"],[\"B\",\"N3\"],[\"W\",\"O3\"],[\"B\",\"P3\"],[\"W\",\"Q3\"],[\"W\",\"R3\"],[\"B\",\"O2\"],[\"W\",\"P2\"],[\"W\",\"R2\"],[\"W\",\"Q1\"]],\"initialTurnNumber\":41,\"move\":[\"B\",\"E3\"],\"moveInfos\":[{\"lcb\":0.575600009,\"move\":\"C17\",\"order\":0,\"prior\":0.265088469,\"pv\":[\"C17\",\"F18\",\"E3\",\"D5\",\"E5\",\"E6\",\"F5\",\"D4\",\"F6\"],\"scoreLead\":1.08615072,\"scoreMean\":1.08615072,\"scoreSelfplay\":1.72183899,\"scoreStdev\":16.3849241,\"utility\":0.180461722,\"utilityLcb\":0.124862682,\"visits\":84,\"winrate\":0.596192246},{\"lcb\":0.562160151,\"move\":\"E3\",\"order\":1,\"prior\":0.169679642,\"pv\":[\"E3\",\"D5\",\"E5\",\"E6\",\"F5\",\"F6\",\"G6\",\"G5\"],\"scoreLead\":1.02266215,\"scoreMean\":1.02266215,\"scoreSelfplay\":1.64333649,\"scoreStdev\":16.2331243,\"utility\":0.173880349,\"utilityLcb\":0.0972798356,\"visits\":48,\"winrate\":0.590530712},{\"lcb\":0.549030164,\"move\":\"D4\",\"order\":2,\"prior\":0.131525815,\"pv\":[\"D4\",\"D3\",\"F4\",\"E4\",\"E5\",\"E3\",\"B10\",\"C9\",\"B9\",\"C8\"],\"scoreLead\":0.979179367,\"scoreMean\":0.979179367,\"scoreSelfplay\":1.67010226,\"scoreStdev\":16.5875115,\"utility\":0.158598786,\"utilityLcb\":0.0565568453,\"visits\":30,\"winrate\":0.586823476},{\"lcb\":0.559852593,\"move\":\"F4\",\"order\":3,\"prior\":0.0420230627,\"pv\":[\"F4\",\"F12\",\"G12\",\"G11\",\"H11\",\"G13\"],\"scoreLead\":1.2060264,\"scoreMean\":1.2060264,\"scoreSelfplay\":1.94697895,\"scoreStdev\":16.4633322,\"utility\":0.211466356,\"utilityLcb\":0.085237853,\"visits\":30,\"winrate\":0.60660389},{\"lcb\":0.485358475,\"move\":\"B10\",\"order\":4,\"prior\":0.131278798,\"pv\":[\"B10\",\"C9\",\"C17\",\"F18\",\"F11\",\"E10\"],\"scoreLead\":0.66319796,\"scoreMean\":0.66319796,\"scoreSelfplay\":1.16149337,\"scoreStdev\":16.5298758,\"utility\":0.0856993078,\"utilityLcb\":-0.11990448,\"visits\":17,\"winrate\":0.561508026},{\"lcb\":0.526083141,\"move\":\"F3\",\"order\":5,\"prior\":0.0346966982,\"pv\":[\"F3\",\"D5\",\"F5\",\"F12\",\"C17\"],\"scoreLead\":1.07306611,\"scoreMean\":1.07306611,\"scoreSelfplay\":1.74286962,\"scoreStdev\":16.4714684,\"utility\":0.184024152,\"utilityLcb\":-0.0019910976,\"visits\":11,\"winrate\":0.594977678},{\"lcb\":0.505757676,\"move\":\"E4\",\"order\":6,\"prior\":0.0212336313,\"pv\":[\"E4\",\"F12\",\"G12\",\"G11\"],\"scoreLead\":1.16878547,\"scoreMean\":1.16878547,\"scoreSelfplay\":1.92860043,\"scoreStdev\":16.6304298,\"utility\":0.193384295,\"utilityLcb\":-0.0715703784,\"visits\":11,\"winrate\":0.603889036},{\"lcb\":0.452107705,\"move\":\"K17\",\"order\":7,\"prior\":0.0398792885,\"pv\":[\"K17\",\"F4\",\"G18\",\"F17\"],\"scoreLead\":0.940272769,\"scoreMean\":0.940272769,\"scoreSelfplay\":1.54148039,\"scoreStdev\":16.7809488,\"utility\":0.130361631,\"utilityLcb\":-0.206843859,\"visits\":8,\"winrate\":0.576998627},{\"lcb\":0.202285321,\"move\":\"G18\",\"order\":8,\"prior\":0.063391462,\"pv\":[\"G18\",\"F17\",\"K17\",\"J17\",\"J16\"],\"scoreLead\":0.65016347,\"scoreMean\":0.65016347,\"scoreSelfplay\":1.15165872,\"scoreStdev\":16.5944316,\"utility\":0.0637146234,\"utilityLcb\":-0.888338312,\"visits\":6,\"winrate\":0.554897519},{\"lcb\":-0.318763853,\"move\":\"F11\",\"order\":9,\"prior\":0.018193569,\"pv\":[\"F11\",\"D10\",\"E3\",\"D5\"],\"scoreLead\":0.816999339,\"scoreMean\":0.816999339,\"scoreSelfplay\":1.30824296,\"scoreStdev\":16.4787323,\"utility\":0.103293271,\"utilityLcb\":-2.29380311,\"visits\":4,\"winrate\":0.569049623}],\"moves\":[[\"W\",\"F16\"],[\"B\",\"F15\"],[\"W\",\"H18\"],[\"B\",\"H16\"],[\"W\",\"J18\"]],\"policy\":[3.85994463e-06,4.5898737e-06,4.1230328e-06,4.1340686e-06,3.89024854e-06,4.88592786e-06,4.87075658e-06,4.07845391e-06,4.18148738e-06,4.70350324e-06,4.9613509e-06,4.88806245e-06,5.17216176e-06,4.27576924e-06,5.69123904e-06,4.94482765e-06,4.27726627e-06,4.68825465e-06,3.63846289e-06,4.52725089e-06,5.79629841e-06,2.47403896e-05,1.21821013e-05,2.95791269e-05,4.2973028e-05,0.063391462,-1.0,-1.0,9.2931341e-06,1.00932029e-05,1.00695224e-05,1.34367829e-05,1.41410565e-05,3.96248288e-05,1.94925597e-05,7.68301561e-06,5.86631813e-06,4.08863798e-06,4.48944547e-06,6.55278791e-06,0.265088469,-1.0,6.08308892e-06,0.00682745129,-1.0,-1.0,6.31834264e-05,0.0398792885,7.57255912e-05,0.000103204919,5.20802423e-05,-1.0,0.000869563373,2.77736908e-05,1.10493183e-05,6.93228048e-06,4.34016283e-06,4.88897649e-06,4.87059187e-05,-1.0,0.000203838819,-1.0,-1.0,-1.0,-1.0,8.17873024e-06,0.000862720713,0.000297790539,0.000237493252,0.000162482786,0.000101167068,1.10115116e-05,-1.0,8.29272449e-06,6.64964773e-06,3.84621308e-06,5.34664878e-06,-1.0,-1.0,4.4293206e-06,-1.0,-1.0,6.11325459e-06,5.07707819e-06,1.14664963e-05,0.000183663258,0.000367481931,0.000117969481,7.47261874e-05,4.91936516e-05,1.43086745e-05,7.31502405e-06,6.01531019e-06,6.45410728e-06,4.48131823e-06,4.73648879e-06,-1.0,-1.0,-1.0,4.16598004e-06,5.03627143e-06,1.04504152e-05,7.37962473e-05,5.78533327e-05,0.000125230319,9.67909364e-05,8.29186683e-05,8.28005286e-05,6.83888284e-05,1.75997193e-05,9.06298101e-06,-1.0,6.09189556e-06,3.5617752e-06,-1.0,-1.0,-1.0,-1.0,5.44591785e-06,1.02903796e-05,3.18880338e-05,0.000107325039,7.113225e-05,8.77469356e-05,9.17615544e-05,9.52863265e-05,9.23635744e-05,7.43965356e-05,3.51344934e-05,1.66164082e-05,9.96564813e-06,1.25108854e-05,4.53750772e-06,4.2655015e-06,-1.0,-1.0,-1.0,0.0073341555,0.00175204407,0.00948126614,0.000131042398,8.07988836e-05,7.50454928e-05,8.06972384e-05,0.000100923025,0.00011368571,0.000103359758,7.4003372e-05,7.13320405e-05,5.76540078e-05,1.42911504e-05,4.03967442e-06,4.46020886e-06,-1.0,-1.0,-1.0,9.04234639e-06,0.018193569,0.00176781346,0.000115142684,8.31261132e-05,7.53808199e-05,8.65382681e-05,0.000115117378,0.000159817646,0.000227050725,0.000277144922,0.0013509019,0.00021546903,1.45389795e-05,4.89262857e-06,4.05051878e-06,0.131278798,-1.0,0.000884849229,3.29604409e-05,7.62444397e-05,0.000133397916,8.8314795e-05,7.35178619e-05,7.31431646e-05,8.56080005e-05,0.000108944281,0.000152322085,0.000182602787,0.000276273553,0.00112469622,8.08081386e-05,1.2261592e-05,4.3453565e-06,4.01940724e-06,6.3951411e-06,6.32985302e-06,6.7703063e-06,1.81219093e-05,6.01919819e-05,8.68133648e-05,7.95930318e-05,7.27273582e-05,6.06098874e-05,6.41841762e-05,8.02908398e-05,7.20960379e-05,6.81930542e-05,5.76106395e-05,6.65518965e-05,1.83695302e-05,1.09136663e-05,4.09284758e-06,4.90481261e-06,4.50668131e-06,1.15759976e-05,1.43027537e-05,5.89565389e-05,0.000106818268,0.000103223472,8.37199259e-05,6.10200477e-05,3.59001715e-05,3.20882464e-05,2.62034282e-05,1.69225623e-05,1.53496349e-05,1.33967978e-05,1.53864949e-05,1.46555967e-05,1.21258963e-05,3.6794811e-06,5.41604368e-06,5.82888197e-06,4.19811222e-05,8.52374142e-05,0.000180865842,0.000126891216,0.00011168301,8.57324339e-05,5.51622506e-05,2.12204268e-05,1.07294336e-05,9.28245208e-06,9.78310618e-06,1.25254501e-05,9.65422987e-06,1.04290393e-05,1.43549132e-05,1.37319521e-05,3.38108953e-06,6.53842426e-06,7.6958122e-06,0.000339520368,0.00093252107,0.000524876406,0.000162574244,0.000101481171,7.44364806e-05,3.64815169e-05,1.30520984e-05,7.53090717e-06,-1.0,8.01355145e-06,9.33879983e-06,7.75047738e-06,7.57547377e-06,9.80761797e-06,1.25568331e-05,3.32509171e-06,6.48456671e-06,9.95211449e-05,0.00698277028,0.0111793149,0.000193329935,0.000159281437,0.000111911068,5.44242139e-05,1.61774333e-05,1.16899437e-05,8.93352444e-06,7.24966549e-06,8.70071835e-06,9.20843195e-06,4.85412284e-06,4.77248113e-06,4.86344106e-06,1.34216834e-05,4.71998055e-06,5.63531876e-06,3.05125395e-05,0.0122732548,0.131525815,0.0212336313,0.0420230627,0.000434801826,7.08543303e-05,1.74374291e-05,1.22801175e-05,8.70473559e-06,8.3211562e-06,6.22907964e-06,7.25980135e-06,-1.0,-1.0,-1.0,2.35384286e-05,5.5998853e-06,4.8093807e-06,2.22835697e-05,-1.0,0.00585763529,0.169679642,0.0346966982,0.000204027194,4.35904331e-05,1.42364524e-05,1.19058059e-05,9.38749145e-06,5.21113179e-06,-1.0,-1.0,-1.0,-1.0,-1.0,0.000115337374,4.4784897e-06,4.89658669e-06,7.69366943e-06,1.73707213e-05,3.55397679e-05,0.000110495501,3.53127944e-05,1.32399246e-05,1.11204527e-05,8.87290844e-06,7.67869369e-06,6.31117291e-06,6.99956945e-06,5.04497712e-06,-1.0,-1.0,-1.0,-1.0,4.83677104e-06,4.54418569e-06,3.41088821e-06,5.22051278e-06,4.51955475e-06,5.09063011e-06,5.01708701e-06,5.20207004e-06,4.48636365e-06,3.72131308e-06,3.79954031e-06,3.97980375e-06,3.99402734e-06,4.53978873e-06,4.65084349e-06,1.82651547e-05,4.97769224e-06,-1.0,4.08508322e-06,6.35743982e-06,3.80894767e-06,4.19335629e-06],\"rootInfo\":{\"currentPlayer\":\"B\",\"scoreLead\":1.03658954,\"scoreSelfplay\":1.68420193,\"scoreStdev\":16.4279875,\"symHash\":\"13D325B9884B1C847FAA0CDDD1607E02\",\"thisHash\":\"A259B41B94FC114D43A7F388FF438B84\",\"utility\":0.170826714,\"visits\":250,\"winrate\":0.591778947},\"rules\":{\"friendlyPassOk\":false,\"hasButton\":true,\"ko\":\"POSITIONAL\",\"komi\":6.0,\"scoring\":\"AREA\",\"suicide\":true,\"tax\":\"NONE\",\"whiteHandicapBonus\":\"0\"},\"turnNumber\":5,\"whitePlayer\":\"kata1-b40c256-s10312780288-d2513725330\"}";
  //		    String jsonTestMove2 =
  //
  // "{\"blackPlayer\":\"kata1-b40c256-s10312780288-d2513725330\",\"boardXSize\":19,\"boardYSize\":19,\"gameId\":\"105844FE92B031D9\",\"initialPlayer\":\"B\",\"initialStones\":[],\"initialTurnNumber\":0,\"move\":[\"W\",\"D16\"],\"moveInfos\":[{\"lcb\":0.487189075,\"move\":\"D16\",\"order\":0,\"prior\":0.823609948,\"pv\":[\"D16\",\"J3\",\"R3\",\"S3\",\"R10\",\"Q10\",\"S11\",\"Q3\",\"P4\",\"Q2\",\"C10\",\"K4\",\"M5\",\"S8\",\"D17\",\"D11\"],\"scoreLead\":-0.297153029,\"scoreMean\":-0.297153029,\"scoreSelfplay\":-0.388022193,\"scoreStdev\":8.73023537,\"utility\":-0.0640903973,\"utilityLcb\":0.00170650349,\"visits\":601,\"winrate\":0.462819852},{\"lcb\":1.32457619,\"move\":\"D17\",\"order\":1,\"prior\":0.172933906,\"pv\":[\"D17\",\"Q10\",\"J3\",\"D16\"],\"scoreLead\":2.96939391,\"scoreMean\":2.96939391,\"scoreSelfplay\":3.64311456,\"scoreStdev\":9.51754688,\"utility\":0.703302145,\"utilityLcb\":2.08845723,\"visits\":7,\"winrate\":0.811555789}],\"moves\":[[\"B\",\"R4\"],[\"W\",\"Q16\"],[\"B\",\"C4\"],[\"W\",\"C16\"],[\"B\",\"F4\"],[\"W\",\"P3\"],[\"B\",\"Q5\"],[\"W\",\"M3\"],[\"B\",\"R17\"],[\"W\",\"Q17\"],[\"B\",\"J14\"],[\"W\",\"R16\"],[\"B\",\"O4\"],[\"W\",\"O3\"],[\"B\",\"R11\"],[\"W\",\"R9\"],[\"B\",\"S14\"],[\"W\",\"P9\"],[\"B\",\"O17\"],[\"W\",\"S15\"],[\"B\",\"E17\"],[\"W\",\"R6\"],[\"B\",\"C13\"],[\"W\",\"D15\"],[\"B\",\"E13\"],[\"W\",\"F14\"],[\"B\",\"H16\"],[\"W\",\"K17\"],[\"B\",\"C18\"],[\"W\",\"F13\"],[\"B\",\"E11\"],[\"W\",\"K15\"],[\"B\",\"O14\"],[\"W\",\"C14\"],[\"B\",\"B14\"],[\"W\",\"P13\"],[\"B\",\"F16\"],[\"W\",\"D12\"],[\"B\",\"D13\"],[\"W\",\"K14\"],[\"B\",\"J13\"],[\"W\",\"J15\"],[\"B\",\"H15\"],[\"W\",\"K13\"],[\"B\",\"J12\"],[\"W\",\"O13\"],[\"B\",\"P14\"],[\"W\",\"Q13\"],[\"B\",\"M17\"],[\"W\",\"N14\"],[\"B\",\"Q14\"],[\"W\",\"R13\"],[\"B\",\"N15\"],[\"W\",\"L18\"],[\"B\",\"P16\"],[\"W\",\"M14\"],[\"B\",\"P18\"],[\"W\",\"N18\"],[\"B\",\"Q18\"],[\"W\",\"S17\"],[\"B\",\"M18\"],[\"W\",\"B15\"],[\"B\",\"B13\"],[\"W\",\"G11\"],[\"B\",\"E12\"],[\"W\",\"J11\"],[\"B\",\"K12\"],[\"W\",\"B18\"],[\"B\",\"C17\"],[\"W\",\"G12\"],[\"B\",\"K11\"],[\"W\",\"R14\"],[\"B\",\"E15\"],[\"W\",\"N16\"],[\"B\",\"N17\"],[\"W\",\"O15\"],[\"B\",\"M15\"],[\"W\",\"M16\"],[\"B\",\"H17\"],[\"W\",\"J10\"],[\"B\",\"K10\"],[\"W\",\"J8\"],[\"B\",\"K9\"],[\"W\",\"G8\"],[\"B\",\"L7\"],[\"W\",\"E8\"],[\"B\",\"F9\"],[\"W\",\"F10\"],[\"B\",\"E9\"],[\"W\",\"G9\"],[\"B\",\"Q8\"],[\"W\",\"M19\"],[\"B\",\"L17\"],[\"W\",\"J17\"],[\"B\",\"B1\"],[\"W\",\"J18\"],[\"B\",\"F8\"],[\"W\",\"J4\"],[\"B\",\"F7\"],[\"W\",\"E10\"],[\"B\",\"D10\"],[\"W\",\"G6\"],[\"B\",\"H5\"],[\"W\",\"J6\"],[\"B\",\"J5\"],[\"W\",\"K6\"],[\"B\",\"H4\"],[\"W\",\"K5\"],[\"B\",\"Q9\"],[\"W\",\"P7\"],[\"B\",\"Q7\"],[\"W\",\"B17\"],[\"B\",\"Q6\"],[\"W\",\"E5\"],[\"B\",\"F6\"],[\"W\",\"F5\"],[\"B\",\"G5\"],[\"W\",\"C5\"],[\"B\",\"D5\"],[\"W\",\"D6\"],[\"B\",\"D4\"],[\"W\",\"E6\"],[\"B\",\"C6\"],[\"W\",\"C7\"],[\"B\",\"B5\"],[\"W\",\"D9\"],[\"B\",\"F11\"],[\"W\",\"G10\"],[\"B\",\"K18\"],[\"W\",\"K19\"],[\"B\",\"A2\"],[\"W\",\"D14\"],[\"B\",\"E4\"],[\"W\",\"D8\"],[\"B\",\"E14\"]],\"policy\":[5.9945437e-06,7.02433044e-06,1.02003205e-05,6.29290116e-06,5.9085678e-06,6.06618278e-06,5.64770244e-06,6.0159914e-06,5.29377439e-06,-1.0,4.47222783e-06,-1.0,3.82775897e-06,4.24283826e-06,4.59863122e-06,5.0811027e-06,4.98126246e-06,5.08181029e-06,5.09124538e-06,6.43821477e-06,-1.0,-1.0,0.000222201255,1.08129652e-05,6.18105105e-06,5.35424533e-06,5.99747727e-06,-1.0,5.05583239e-06,-1.0,-1.0,-1.0,2.912019e-06,-1.0,-1.0,4.8495458e-06,5.13844861e-06,5.09625352e-06,5.93991217e-06,-1.0,-1.0,0.172933906,-1.0,4.96925304e-06,5.25234782e-06,-1.0,-1.0,-1.0,-1.0,-1.0,-1.0,-1.0,4.56831322e-06,-1.0,-1.0,-1.0,4.95449876e-06,5.63159392e-06,6.54740188e-06,-1.0,0.823609948,2.2266729e-06,-1.0,5.33940693e-06,-1.0,4.41773909e-06,6.50407492e-06,4.57073611e-06,-1.0,-1.0,4.70758596e-06,-1.0,-1.0,-1.0,4.79143364e-06,5.15061583e-06,7.30552711e-06,-1.0,4.93948664e-06,-1.0,-1.0,5.055987e-06,7.40426412e-06,-1.0,-1.0,-1.0,4.32659499e-06,-1.0,-1.0,-1.0,5.13512714e-06,5.40147039e-06,4.9937953e-06,-1.0,5.50024151e-06,6.92287131e-06,-1.0,-1.0,-1.0,-1.0,-1.0,8.1776916e-06,0.000867421739,-1.0,-1.0,5.2377768e-06,-1.0,-1.0,-1.0,-1.0,-1.0,-1.0,-1.0,5.8634364e-06,5.84254894e-06,-1.0,-1.0,-1.0,-1.0,-1.0,6.75326191e-06,7.83736505e-06,-1.0,-1.0,4.77175672e-06,5.32693093e-06,5.88872081e-06,-1.0,-1.0,-1.0,-1.0,6.84995484e-06,5.94739458e-06,6.00766134e-06,5.06371725e-06,5.18800334e-06,-1.0,-1.0,6.10467214e-06,-1.0,6.48785044e-06,-1.0,-1.0,6.07560287e-06,6.43434123e-06,7.12635892e-06,6.559339e-06,6.55351823e-06,7.45819625e-06,9.47022545e-06,7.25987593e-06,6.2395543e-06,5.61419347e-06,6.05859168e-06,5.42676526e-06,6.69884821e-06,-1.0,-1.0,-1.0,5.3589556e-06,-1.0,-1.0,6.02061755e-06,7.84445456e-06,7.6645938e-06,9.02484862e-06,1.21333442e-05,1.48786212e-05,-1.0,2.4980005e-05,5.82571874e-06,5.60191393e-06,6.18757986e-06,7.90640843e-06,-1.0,-1.0,-1.0,-1.0,5.51697894e-06,-1.0,-1.0,5.94968105e-06,2.46804575e-05,6.22271618e-05,9.24186752e-05,4.87290927e-05,0.000165614372,8.43101006e-05,9.03502678e-06,5.24867755e-06,5.83383007e-06,7.22158575e-06,5.21582933e-06,-1.0,-1.0,-1.0,-1.0,5.69077702e-06,5.93699042e-06,-1.0,6.50586753e-06,4.0418221e-05,6.92876638e-05,1.9261548e-05,-1.0,-1.0,-1.0,1.12472335e-05,5.60195122e-06,5.77002265e-06,5.85097996e-06,5.43980741e-06,-1.0,-1.0,-1.0,-1.0,6.18164086e-06,-1.0,1.01094811e-05,8.85810277e-06,1.22405954e-05,1.50928327e-05,7.0991241e-06,7.75920307e-06,-1.0,7.35637559e-06,6.23622327e-06,5.58112788e-06,6.1578462e-06,5.75795139e-06,-1.0,5.06122569e-06,5.20325011e-06,-1.0,6.22323569e-06,5.04898071e-06,6.63777928e-06,6.97468658e-06,-1.0,1.36716844e-05,0.00010322345,7.68261179e-06,-1.0,-1.0,6.52720246e-06,6.72850501e-06,5.53986638e-06,5.731692e-06,6.52816152e-06,-1.0,-1.0,-1.0,-1.0,-1.0,5.17847775e-06,-1.0,-1.0,6.82168775e-06,2.9438721e-05,2.84897524e-05,4.59873008e-05,7.13722693e-06,-1.0,-1.0,6.54706491e-06,5.25422638e-06,5.36190009e-06,-1.0,-1.0,-1.0,-1.0,-1.0,-1.0,-1.0,-1.0,-1.0,7.08717107e-06,8.37986317e-06,7.57212092e-06,1.07441047e-05,7.28234272e-06,-1.0,9.12757332e-06,1.33047288e-05,5.33316279e-06,4.91791661e-06,4.63040897e-06,-1.0,-1.0,-1.0,-1.0,5.32437116e-06,-1.0,-1.0,6.8883819e-06,7.45053239e-06,7.81751623e-06,7.41282747e-06,-1.0,1.80053648e-05,8.02401883e-06,-1.0,2.78274802e-05,5.55912493e-06,4.98104828e-06,4.90685579e-06,4.26245515e-06,4.53675375e-06,4.75405568e-06,5.10445943e-06,7.55985457e-06,2.6133248e-05,6.590267e-05,6.95785184e-06,7.150697e-06,-1.0,6.20428455e-06,-1.0,-1.0,8.12475264e-06,5.72638928e-05,2.57403608e-05,5.53568316e-06,-1.0,5.18596562e-06,5.06871265e-06,5.24173947e-06,5.8621672e-06,6.19884941e-06,5.7975908e-06,6.12643771e-06,6.77430899e-06,6.01869442e-06,6.93743777e-06,6.09136032e-06,6.50722041e-06,5.97014832e-06,6.5576628e-06,8.21513368e-06,2.49097084e-05,9.09296796e-06,6.16545003e-06,-1.0,-1.0,4.83915574e-06,5.25914948e-06,5.74609703e-06,5.92123706e-06,6.11503765e-06,6.3341331e-06,5.78555046e-06,5.91391199e-06,5.66032531e-06,5.80015694e-06,5.73021634e-06,6.12010172e-06,6.13545944e-06,6.34796925e-06,5.81785707e-06,5.36176731e-06,5.5913778900000004e-06,1.06019877e-06],\"rootInfo\":{\"currentPlayer\":\"W\",\"scoreLead\":-0.265064736,\"scoreSelfplay\":-0.348400569,\"scoreStdev\":8.74696019,\"symHash\":\"0FF8B6E4FEEA7B5673ECCA456AFEB88F\",\"thisHash\":\"26A7CCB8ACFCDF6F9AF9F6F7C462CABD\",\"utility\":-0.0565347644,\"visits\":609,\"winrate\":0.466263851},\"rules\":{\"friendlyPassOk\":false,\"hasButton\":false,\"ko\":\"POSITIONAL\",\"komi\":-36.0,\"scoring\":\"TERRITORY\",\"suicide\":true,\"tax\":\"NONE\",\"whiteHandicapBonus\":\"0\"},\"turnNumber\":135,\"whitePlayer\":\"kata1-b40c256-s10312780288-d2513725330\"}";
  //		    String jsonTestMove3 =
  //
  // "{\"blackPlayer\":\"kata1-b40c256-s10312780288-d2513725330\",\"boardXSize\":19,\"boardYSize\":19,\"gameId\":\"105844FE92B031D9\",\"initialPlayer\":\"B\",\"initialStones\":[],\"initialTurnNumber\":0,\"move\":[\"B\",\"J3\"],\"moveInfos\":[{\"lcb\":0.430815853,\"move\":\"J3\",\"order\":0,\"prior\":0.16544427,\"pv\":[\"J3\",\"R3\",\"S3\",\"R10\",\"Q10\",\"S11\",\"R2\",\"C10\",\"N4\",\"L2\",\"K4\",\"Q3\",\"K2\",\"S2\",\"S1\"],\"scoreLead\":-0.40152266,\"scoreMean\":-0.40152266,\"scoreSelfplay\":-0.531906974,\"scoreStdev\":9.15213063,\"utility\":-0.0988364037,\"utilityLcb\":-0.141818664,\"visits\":1237,\"winrate\":0.446735208},{\"lcb\":0.231062937,\"move\":\"Q10\",\"order\":1,\"prior\":0.196953416,\"pv\":[\"Q10\",\"J3\",\"Q2\",\"G3\",\"H3\",\"H2\",\"G2\",\"F2\"],\"scoreLead\":-1.09391724,\"scoreMean\":-1.09391724,\"scoreSelfplay\":-1.47745694,\"scoreStdev\":7.93888159,\"utility\":-0.396421215,\"utilityLcb\":-0.609949342,\"visits\":33,\"winrate\":0.310147429},{\"lcb\":0.20198922,\"move\":\"S12\",\"order\":2,\"prior\":0.121686354,\"pv\":[\"S12\",\"J3\",\"Q2\",\"Q10\",\"R10\"],\"scoreLead\":-1.36503146,\"scoreMean\":-1.36503146,\"scoreSelfplay\":-1.8796747599999999,\"scoreStdev\":9.85625507,\"utility\":-0.388591878,\"utilityLcb\":-0.664436128,\"visits\":21,\"winrate\":0.304153757},{\"lcb\":0.238628132,\"move\":\"P11\",\"order\":3,\"prior\":0.0943727344,\"pv\":[\"P11\",\"J3\",\"Q2\",\"G3\",\"H3\"],\"scoreLead\":-1.26031673,\"scoreMean\":-1.26031673,\"scoreSelfplay\":-1.70147704,\"scoreStdev\":8.86598983,\"utility\":-0.348779638,\"utilityLcb\":-0.560317401,\"visits\":19,\"winrate\":0.316975451},{\"lcb\":0.132132912,\"move\":\"L13\",\"order\":4,\"prior\":0.0608975925,\"pv\":[\"L13\",\"L12\",\"J3\",\"R10\",\"Q10\",\"S11\"],\"scoreLead\":-1.27474619,\"scoreMean\":-1.27474619,\"scoreSelfplay\":-1.72575511,\"scoreStdev\":8.80601327,\"utility\":-0.390761022,\"utilityLcb\":-0.842904691,\"visits\":14,\"winrate\":0.29959353},{\"lcb\":0.0862741277,\"move\":\"L3\",\"order\":5,\"prior\":0.0299970843,\"pv\":[\"L3\",\"R2\",\"M2\",\"M4\"],\"scoreLead\":-1.65057393,\"scoreMean\":-1.65057393,\"scoreSelfplay\":-2.25918277,\"scoreStdev\":9.89471241,\"utility\":-0.475496636,\"utilityLcb\":-0.944879238,\"visits\":10,\"winrate\":0.260119536},{\"lcb\":-0.00278644284,\"move\":\"F18\",\"order\":6,\"prior\":0.0654440522,\"pv\":[\"F18\",\"J3\",\"P11\",\"R2\",\"S2\"],\"scoreLead\":-4.99380175,\"scoreMean\":-4.99380175,\"scoreSelfplay\":-5.69604028,\"scoreStdev\":7.64507169,\"utility\":-1.02520477,\"utilityLcb\":-1.10352483,\"visits\":15,\"winrate\":0.0262209853},{\"lcb\":0.00254441945,\"move\":\"H6\",\"order\":7,\"prior\":0.0339142047,\"pv\":[\"H6\",\"G7\",\"J3\",\"R10\",\"Q10\",\"S11\",\"Q2\",\"K2\"],\"scoreLead\":-1.99267087,\"scoreMean\":-1.99267087,\"scoreSelfplay\":-2.53085314,\"scoreStdev\":8.44001473,\"utility\":-0.573281825,\"utilityLcb\":-1.10250888,\"visits\":11,\"winrate\":0.198554439},{\"lcb\":0.0577722425,\"move\":\"P10\",\"order\":8,\"prior\":0.0251434483,\"pv\":[\"P10\",\"J3\",\"Q2\",\"G3\",\"H3\",\"H2\"],\"scoreLead\":-1.57010683,\"scoreMean\":-1.57010683,\"scoreSelfplay\":-2.08191748,\"scoreStdev\":8.19570706,\"utility\":-0.529040468,\"utilityLcb\":-1.04234351,\"visits\":9,\"winrate\":0.24788448},{\"lcb\":0.0132564879,\"move\":\"M4\",\"order\":9,\"prior\":0.024624208,\"pv\":[\"M4\",\"R2\",\"S2\",\"R3\"],\"scoreLead\":-2.36608871,\"scoreMean\":-2.36608871,\"scoreSelfplay\":-3.02687714,\"scoreStdev\":8.85534411,\"utility\":-0.679160803,\"utilityLcb\":-1.10622741,\"visits\":9,\"winrate\":0.171429304},{\"lcb\":0.106310693,\"move\":\"Q2\",\"order\":10,\"prior\":0.020450918,\"pv\":[\"Q2\",\"J3\",\"S12\",\"Q10\"],\"scoreLead\":-1.34188693,\"scoreMean\":-1.34188693,\"scoreSelfplay\":-1.82195361,\"scoreStdev\":8.77153394,\"utility\":-0.456464619,\"utilityLcb\":-0.938503696,\"visits\":8,\"winrate\":0.284843685},{\"lcb\":-0.394718983,\"move\":\"Q3\",\"order\":11,\"prior\":0.0105356546,\"pv\":[\"Q3\",\"R10\",\"Q10\",\"S11\",\"J3\"],\"scoreLead\":-1.24471872,\"scoreMean\":-1.24471872,\"scoreSelfplay\":-1.63594914,\"scoreStdev\":8.623612,\"utility\":-0.354045436,\"utilityLcb\":-2.2539779,\"visits\":6,\"winrate\":0.308959706},{\"lcb\":0.0313850881,\"move\":\"O16\",\"order\":12,\"prior\":0.0206256472,\"pv\":[\"O16\",\"L15\",\"S12\",\"J3\",\"Q2\"],\"scoreLead\":-2.20917735,\"scoreMean\":-2.20917735,\"scoreSelfplay\":-2.88182557,\"scoreStdev\":8.81665836,\"utility\":-0.685703611,\"utilityLcb\":-1.07465992,\"visits\":8,\"winrate\":0.174953436},{\"lcb\":0.107296433,\"move\":\"Q11\",\"order\":13,\"prior\":0.0113245174,\"pv\":[\"Q11\",\"J3\",\"Q2\",\"S11\",\"S12\"],\"scoreLead\":-2.1601662,\"scoreMean\":-2.1601662,\"scoreSelfplay\":-2.64215242,\"scoreStdev\":8.88134779,\"utility\":-0.643944865,\"utilityLcb\":-0.880309758,\"visits\":6,\"winrate\":0.194838986},{\"lcb\":0.0291352263,\"move\":\"O19\",\"order\":14,\"prior\":0.00878981221,\"pv\":[\"O19\",\"N19\",\"O18\",\"L19\"],\"scoreLead\":-1.6077363,\"scoreMean\":-1.6077363,\"scoreSelfplay\":-2.26305945,\"scoreStdev\":9.05480575,\"utility\":-0.521164619,\"utilityLcb\":-1.10542223,\"visits\":6,\"winrate\":0.245526933},{\"lcb\":-3.77968475,\"move\":\"L15\",\"order\":15,\"prior\":0.00293598371,\"pv\":[\"L15\",\"L16\",\"L13\"],\"scoreLead\":-0.743081013,\"scoreMean\":-0.743081013,\"scoreSelfplay\":-1.1183821,\"scoreStdev\":9.30451338,\"utility\":-0.221609508,\"utilityLcb\":-11.4894428,\"visits\":3,\"winrate\":0.393490901},{\"lcb\":-0.0145148407,\"move\":\"S6\",\"order\":16,\"prior\":0.0121592078,\"pv\":[\"S6\",\"J3\",\"P11\",\"G3\",\"N4\"],\"scoreLead\":-7.41746929,\"scoreMean\":-7.41746929,\"scoreSelfplay\":-7.83015589,\"scoreStdev\":8.21705661,\"utility\":-1.07034573,\"utilityLcb\":-1.14755182,\"visits\":7,\"winrate\":0.0140800068},{\"lcb\":-0.301655538,\"move\":\"K4\",\"order\":17,\"prior\":0.00955134444,\"pv\":[\"K4\",\"R2\",\"L3\"],\"scoreLead\":-2.19575556,\"scoreMean\":-2.19575556,\"scoreSelfplay\":-2.99395562,\"scoreStdev\":9.04520998,\"utility\":-0.661950557,\"utilityLcb\":-1.96368311,\"visits\":6,\"winrate\":0.180467628},{\"lcb\":-0.0389014874,\"move\":\"N4\",\"order\":18,\"prior\":0.00895216037,\"pv\":[\"N4\",\"R2\",\"P11\"],\"scoreLead\":-2.17355697,\"scoreMean\":-2.17355697,\"scoreSelfplay\":-2.76665872,\"scoreStdev\":8.61053892,\"utility\":-0.586956516,\"utilityLcb\":-1.21142662,\"visits\":6,\"winrate\":0.192383737},{\"lcb\":-0.135128379,\"move\":\"R10\",\"order\":19,\"prior\":0.00702199386,\"pv\":[\"R10\",\"J3\",\"Q2\",\"G3\",\"H3\"],\"scoreLead\":-2.06921413,\"scoreMean\":-2.06921413,\"scoreSelfplay\":-2.65888472,\"scoreStdev\":8.04512965,\"utility\":-0.705614502,\"utilityLcb\":-1.50173329,\"visits\":5,\"winrate\":0.15973043},{\"lcb\":-0.0571549593,\"move\":\"S7\",\"order\":20,\"prior\":0.00600847835,\"pv\":[\"S7\",\"J3\",\"H3\",\"C10\"],\"scoreLead\":-6.5768306,\"scoreMean\":-6.5768306,\"scoreSelfplay\":-7.23732991,\"scoreStdev\":8.52626624,\"utility\":-1.05069052,\"utilityLcb\":-1.25271483,\"visits\":5,\"winrate\":0.0176688585},{\"lcb\":-0.225669131,\"move\":\"C10\",\"order\":21,\"prior\":0.00546428934,\"pv\":[\"C10\",\"J3\",\"S12\"],\"scoreLead\":-1.85793102,\"scoreMean\":-1.85793102,\"scoreSelfplay\":-2.40616825,\"scoreStdev\":8.24258229,\"utility\":-0.635705101,\"utilityLcb\":-1.78426535,\"visits\":5,\"winrate\":0.199723553},{\"lcb\":-0.90933986,\"move\":\"J2\",\"order\":22,\"prior\":0.00504765101,\"pv\":[\"J2\",\"R10\",\"Q10\"],\"scoreLead\":-3.18632583,\"scoreMean\":-3.18632583,\"scoreSelfplay\":-4.05984775,\"scoreStdev\":8.59715962,\"utility\":-0.799194043,\"utilityLcb\":-3.52919379,\"visits\":4,\"winrate\":0.101771157},{\"lcb\":-0.962500156,\"move\":\"B7\",\"order\":23,\"prior\":0.00488622906,\"pv\":[\"B7\",\"C10\",\"J3\"],\"scoreLead\":-2.40918217,\"scoreMean\":-2.40918217,\"scoreSelfplay\":-3.13289893,\"scoreStdev\":9.29980225,\"utility\":-0.701548171,\"utilityLcb\":-3.77640339,\"visits\":4,\"winrate\":0.176335109},{\"lcb\":-0.858794755,\"move\":\"E7\",\"order\":24,\"prior\":0.00481157424,\"pv\":[\"E7\",\"D7\",\"Q10\",\"J3\"],\"scoreLead\":-3.16230214,\"scoreMean\":-3.16230214,\"scoreSelfplay\":-3.58654177,\"scoreStdev\":8.17545891,\"utility\":-0.82040485,\"utilityLcb\":-3.46104529,\"visits\":4,\"winrate\":0.118796428},{\"lcb\":-0.472943418,\"move\":\"S8\",\"order\":25,\"prior\":0.00444644829,\"pv\":[\"S8\",\"J3\",\"Q2\",\"G3\"],\"scoreLead\":-5.47796237,\"scoreMean\":-5.47796237,\"scoreSelfplay\":-5.81049049,\"scoreStdev\":8.19962311,\"utility\":-1.03365063,\"utilityLcb\":-2.3835636,\"visits\":4,\"winrate\":0.027024348},{\"lcb\":-0.122917483,\"move\":\"G19\",\"order\":26,\"prior\":0.00439604651,\"pv\":[\"G19\",\"J3\",\"S12\"],\"scoreLead\":-7.80047278,\"scoreMean\":-7.80047278,\"scoreSelfplay\":-8.15680159,\"scoreStdev\":8.62356379,\"utility\":-1.07426654,\"utilityLcb\":-1.44546883,\"visits\":4,\"winrate\":0.0145648461},{\"lcb\":-2.47524091,\"move\":\"A15\",\"order\":27,\"prior\":0.00347745488,\"pv\":[\"A15\",\"A16\",\"J3\"],\"scoreLead\":-1.93678915,\"scoreMean\":-1.93678915,\"scoreSelfplay\":-2.67851724,\"scoreStdev\":9.99030322,\"utility\":-0.503893546,\"utilityLcb\":-7.85358251,\"visits\":4,\"winrate\":0.246866117},{\"lcb\":-1.12855161,\"move\":\"N19\",\"order\":28,\"prior\":0.00327888969,\"pv\":[\"N19\",\"O19\",\"J3\",\"R10\"],\"scoreLead\":-1.92224544,\"scoreMean\":-1.92224544,\"scoreSelfplay\":-2.52115977,\"scoreStdev\":8.84672547,\"utility\":-0.599174436,\"utilityLcb\":-4.24542957,\"visits\":4,\"winrate\":0.221843192},{\"lcb\":-0.519132091,\"move\":\"O18\",\"order\":29,\"prior\":0.00326960161,\"pv\":[\"O18\",\"N19\",\"O19\",\"L19\"],\"scoreLead\":-1.64658424,\"scoreMean\":-1.64658424,\"scoreSelfplay\":-2.32867324,\"scoreStdev\":9.65454348,\"utility\":-0.52901175,\"utilityLcb\":-2.62384992,\"visits\":4,\"winrate\":0.256733898},{\"lcb\":-2.45769102,\"move\":\"C9\",\"order\":30,\"prior\":0.00242334302,\"pv\":[\"C9\",\"C10\",\"D11\"],\"scoreLead\":-2.95945382,\"scoreMean\":-2.95945382,\"scoreSelfplay\":-3.26577608,\"scoreStdev\":8.62485286,\"utility\":-0.738618406,\"utilityLcb\":-7.8103986,\"visits\":3,\"winrate\":0.161486831},{\"lcb\":-0.191132567,\"move\":\"C5\",\"order\":31,\"prior\":0.00233952166,\"pv\":[\"C5\",\"J3\"],\"scoreLead\":-10.6876146,\"scoreMean\":-10.6876146,\"scoreSelfplay\":-10.9745042,\"scoreStdev\":8.90956772,\"utility\":-1.1208001,\"utilityLcb\":-1.65100943,\"visits\":3,\"winrate\":0.00524125709},{\"lcb\":-2.17837534,\"move\":\"J16\",\"order\":32,\"prior\":0.00166551233,\"pv\":[\"J16\",\"K16\",\"J3\"],\"scoreLead\":-1.23019816,\"scoreMean\":-1.23019816,\"scoreSelfplay\":-1.69050344,\"scoreStdev\":9.4947255,\"utility\":-0.384864667,\"utilityLcb\":-7.14461571,\"visits\":3,\"winrate\":0.325236162},{\"lcb\":-1.59439626,\"move\":\"R2\",\"order\":33,\"prior\":0.0016535176,\"pv\":[\"R2\",\"R10\"],\"scoreLead\":-1.2299521,\"scoreMean\":-1.2299521,\"scoreSelfplay\":-1.64771248,\"scoreStdev\":9.08908578,\"utility\":-0.357780591,\"utilityLcb\":-5.52157896,\"visits\":3,\"winrate\":0.318121652},{\"lcb\":-0.906160594,\"move\":\"L14\",\"order\":34,\"prior\":0.00133472169,\"pv\":[\"L14\",\"M12\"],\"scoreLead\":-4.30955982,\"scoreMean\":-4.30955982,\"scoreSelfplay\":-5.00663948,\"scoreStdev\":9.21536709,\"utility\":-0.907098074,\"utilityLcb\":-2.7,\"visits\":2,\"winrate\":0.093839406},{\"lcb\":-0.948311004,\"move\":\"S18\",\"order\":35,\"prior\":0.00111473724,\"pv\":[\"S18\",\"J3\"],\"scoreLead\":-8.5282321,\"scoreMean\":-8.5282321,\"scoreSelfplay\":-7.09235668,\"scoreStdev\":11.70981,\"utility\":-0.955516177,\"utilityLcb\":-2.7,\"visits\":2,\"winrate\":0.0516889961},{\"lcb\":-0.98415416,\"move\":\"D18\",\"order\":36,\"prior\":0.00110456371,\"pv\":[\"D18\",\"J3\"],\"scoreLead\":-7.18082428,\"scoreMean\":-7.18082428,\"scoreSelfplay\":-7.46874523,\"scoreStdev\":8.36073379,\"utility\":-1.06131231,\"utilityLcb\":-2.7,\"visits\":2,\"winrate\":0.0158458399}],\"moves\":[[\"B\",\"R4\"],[\"W\",\"Q16\"],[\"B\",\"C4\"],[\"W\",\"C16\"],[\"B\",\"F4\"],[\"W\",\"P3\"],[\"B\",\"Q5\"],[\"W\",\"M3\"],[\"B\",\"R17\"],[\"W\",\"Q17\"],[\"B\",\"J14\"],[\"W\",\"R16\"],[\"B\",\"O4\"],[\"W\",\"O3\"],[\"B\",\"R11\"],[\"W\",\"R9\"],[\"B\",\"S14\"],[\"W\",\"P9\"],[\"B\",\"O17\"],[\"W\",\"S15\"],[\"B\",\"E17\"],[\"W\",\"R6\"],[\"B\",\"C13\"],[\"W\",\"D15\"],[\"B\",\"E13\"],[\"W\",\"F14\"],[\"B\",\"H16\"],[\"W\",\"K17\"],[\"B\",\"C18\"],[\"W\",\"F13\"],[\"B\",\"E11\"],[\"W\",\"K15\"],[\"B\",\"O14\"],[\"W\",\"C14\"],[\"B\",\"B14\"],[\"W\",\"P13\"],[\"B\",\"F16\"],[\"W\",\"D12\"],[\"B\",\"D13\"],[\"W\",\"K14\"],[\"B\",\"J13\"],[\"W\",\"J15\"],[\"B\",\"H15\"],[\"W\",\"K13\"],[\"B\",\"J12\"],[\"W\",\"O13\"],[\"B\",\"P14\"],[\"W\",\"Q13\"],[\"B\",\"M17\"],[\"W\",\"N14\"],[\"B\",\"Q14\"],[\"W\",\"R13\"],[\"B\",\"N15\"],[\"W\",\"L18\"],[\"B\",\"P16\"],[\"W\",\"M14\"],[\"B\",\"P18\"],[\"W\",\"N18\"],[\"B\",\"Q18\"],[\"W\",\"S17\"],[\"B\",\"M18\"],[\"W\",\"B15\"],[\"B\",\"B13\"],[\"W\",\"G11\"],[\"B\",\"E12\"],[\"W\",\"J11\"],[\"B\",\"K12\"],[\"W\",\"B18\"],[\"B\",\"C17\"],[\"W\",\"G12\"],[\"B\",\"K11\"],[\"W\",\"R14\"],[\"B\",\"E15\"],[\"W\",\"N16\"],[\"B\",\"N17\"],[\"W\",\"O15\"],[\"B\",\"M15\"],[\"W\",\"M16\"],[\"B\",\"H17\"],[\"W\",\"J10\"],[\"B\",\"K10\"],[\"W\",\"J8\"],[\"B\",\"K9\"],[\"W\",\"G8\"],[\"B\",\"L7\"],[\"W\",\"E8\"],[\"B\",\"F9\"],[\"W\",\"F10\"],[\"B\",\"E9\"],[\"W\",\"G9\"],[\"B\",\"Q8\"],[\"W\",\"M19\"],[\"B\",\"L17\"],[\"W\",\"J17\"],[\"B\",\"B1\"],[\"W\",\"J18\"],[\"B\",\"F8\"],[\"W\",\"J4\"],[\"B\",\"F7\"],[\"W\",\"E10\"],[\"B\",\"D10\"],[\"W\",\"G6\"],[\"B\",\"H5\"],[\"W\",\"J6\"],[\"B\",\"J5\"],[\"W\",\"K6\"],[\"B\",\"H4\"],[\"W\",\"K5\"],[\"B\",\"Q9\"],[\"W\",\"P7\"],[\"B\",\"Q7\"],[\"W\",\"B17\"],[\"B\",\"Q6\"],[\"W\",\"E5\"],[\"B\",\"F6\"],[\"W\",\"F5\"],[\"B\",\"G5\"],[\"W\",\"C5\"],[\"B\",\"D5\"],[\"W\",\"D6\"],[\"B\",\"D4\"],[\"W\",\"E6\"],[\"B\",\"C6\"],[\"W\",\"C7\"],[\"B\",\"B5\"],[\"W\",\"D9\"],[\"B\",\"F11\"],[\"W\",\"G10\"],[\"B\",\"K18\"],[\"W\",\"K19\"],[\"B\",\"A2\"],[\"W\",\"D14\"],[\"B\",\"E4\"],[\"W\",\"D8\"],[\"B\",\"E14\"],[\"W\",\"D16\"]],\"policy\":[7.83243377e-06,1.77625625e-05,1.0992484e-05,9.73477927e-06,9.87713884e-06,9.64389983e-06,1.04934861e-05,1.31532261e-05,9.93021877e-06,-1.0,-1.0,-1.0,0.00310152629,0.00182882103,7.11272951e-05,9.81367066e-06,9.71492682e-06,9.05295656e-06,8.52255562e-06,1.09122066e-05,-1.0,-1.0,1.02835002e-05,1.11228128e-05,1.36155568e-05,1.09138273e-05,1.53187539e-05,-1.0,-1.0,-1.0,-1.0,-1.0,0.0011462901,-1.0,-1.0,5.1082854e-05,0.000789459504,2.01228231e-05,1.21918811e-05,-1.0,-1.0,1.26436998e-05,-1.0,8.870682e-06,8.91536456e-06,-1.0,-1.0,-1.0,-1.0,-1.0,-1.0,-1.0,1.06584339e-05,-1.0,-1.0,-1.0,2.40012942e-05,3.34428551e-05,2.57357879e-05,-1.0,-1.0,1.0775203e-05,-1.0,8.9151963e-06,-1.0,0.00146587193,0.000879493775,6.87810971e-05,-1.0,-1.0,0.000134084112,-1.0,-1.0,-1.0,1.14897866e-05,8.91490708e-06,0.00108797452,-1.0,-1.0,-1.0,-1.0,1.09979519e-05,9.66834796e-06,-1.0,-1.0,-1.0,0.00235965941,-1.0,-1.0,-1.0,0.000101275982,9.21179344e-06,8.32469232e-06,-1.0,9.62107151e-06,1.06320986e-05,-1.0,-1.0,-1.0,-1.0,-1.0,1.21812382e-05,1.02682079e-05,-1.0,-1.0,1.56122333e-05,-1.0,-1.0,-1.0,-1.0,-1.0,-1.0,-1.0,9.20960247e-06,8.56168754e-06,-1.0,-1.0,-1.0,-1.0,-1.0,2.53017388e-05,9.61434398e-06,-1.0,-1.0,0.0401590541,0.000308173883,2.59902863e-05,-1.0,-1.0,-1.0,-1.0,1.40643242e-05,9.98983614e-06,8.39937638e-06,9.0132753e-06,9.17120269e-06,-1.0,-1.0,1.11792433e-05,-1.0,8.84654401e-06,-1.0,-1.0,1.58643525e-05,1.6804499e-05,2.01022267e-05,1.3061117e-05,1.26909954e-05,1.2153615e-05,4.01470752e-05,0.156847432,1.27078993e-05,9.3188246e-06,1.06987936e-05,1.03080438e-05,1.08296008e-05,-1.0,-1.0,-1.0,9.08318634e-06,-1.0,-1.0,9.79031756e-06,1.37749912e-05,3.28700262e-05,8.76759514e-05,0.114378795,0.00292732404,-1.0,0.00051989255,1.47416995e-05,1.00215411e-05,1.39033145e-05,0.00536277052,-1.0,-1.0,-1.0,-1.0,8.48823584e-06,-1.0,-1.0,9.82075562e-06,1.32182613e-05,1.80480092e-05,5.40119145e-05,0.0237949118,0.277655065,0.00722311623,2.41387243e-05,1.1326465e-05,1.02638442e-05,1.44036367e-05,0.000534986088,-1.0,-1.0,-1.0,-1.0,1.03084676e-05,1.03768634e-05,-1.0,1.00930683e-05,1.2605351e-05,1.4547245e-05,1.51780932e-05,-1.0,-1.0,-1.0,1.60523232e-05,1.0981249e-05,1.0328713e-05,1.42997105e-05,1.76634567e-05,-1.0,-1.0,-1.0,-1.0,1.28886395e-05,-1.0,1.23787186e-05,1.01755732e-05,1.20992718e-05,1.34933016e-05,1.23054488e-05,1.71229331e-05,-1.0,1.32805544e-05,1.33917611e-05,1.01164269e-05,1.03699967e-05,0.00423822692,-1.0,4.30628643e-05,0.0001782646,-1.0,1.71678348e-05,1.57753057e-05,1.35651189e-05,1.63208224e-05,-1.0,1.33333961e-05,1.56188944e-05,1.16863193e-05,-1.0,-1.0,1.11660429e-05,1.24207136e-05,9.95662504e-06,8.84754172e-06,9.56117765e-06,-1.0,-1.0,-1.0,-1.0,-1.0,0.0147764524,-1.0,-1.0,1.30202916e-05,2.59456137e-05,1.74985289e-05,1.47290793e-05,1.09913126e-05,-1.0,-1.0,1.14822751e-05,9.89991622e-06,8.50834203e-06,-1.0,8.10868187e-06,-1.0,-1.0,-1.0,-1.0,-1.0,-1.0,-1.0,1.37502011e-05,3.10223732e-05,1.500291e-05,1.41696701e-05,1.23746313e-05,-1.0,1.08083359e-05,1.28710872e-05,9.9911349e-06,8.15028579e-06,8.45170052e-06,-1.0,-1.0,-1.0,-1.0,8.8157949e-06,-1.0,-1.0,0.010167988,5.88638504e-05,0.029077854,0.00195801468,-1.0,1.79638573e-05,1.13154338e-05,-1.0,1.53079527e-05,1.16943647e-05,7.8643734e-06,8.36287472e-06,8.12938742e-06,8.22761922e-06,8.14132909e-06,8.75823662e-06,9.62451031e-06,1.80465177e-05,0.228943914,0.000249291566,0.0360464156,-1.0,6.0575876e-05,-1.0,-1.0,0.00921394303,5.12204424e-05,1.30760127e-05,1.04646597e-05,-1.0,8.11460359e-06,8.03502735e-06,8.19172965e-06,8.43107955e-06,9.40407972e-06,1.10729661e-05,1.63035784e-05,0.000144352933,6.6641398e-05,8.93634642e-05,0.000283885805,4.9857339e-05,1.52568609e-05,1.76765388e-05,0.0177478585,0.00139267778,1.75393725e-05,9.88513602e-06,6.55878466e-06,-1.0,7.65903133e-06,7.9317806e-06,8.12635517e-06,8.64910453e-06,9.42229053e-06,1.11707923e-05,1.16097608e-05,1.11376103e-05,1.0890808e-05,1.10326837e-05,1.08700724e-05,1.03793518e-05,1.07799342e-05,1.13841716e-05,1.29203436e-05,1.07284304e-05,8.2147908e-06,5.42439648e-06],\"rootInfo\":{\"currentPlayer\":\"B\",\"scoreLead\":-0.481219132,\"scoreSelfplay\":-0.637727802,\"scoreStdev\":9.1332818,\"symHash\":\"05AB2CAD7CBE4C45D3F96ED5294FD1BA\",\"thisHash\":\"C3768C6E6B32760FD991176BCE1F7641\",\"utility\":-0.124391587,\"visits\":1500,\"winrate\":0.434396218},\"rules\":{\"friendlyPassOk\":false,\"hasButton\":false,\"ko\":\"POSITIONAL\",\"komi\":-36.0,\"scoring\":\"TERRITORY\",\"suicide\":true,\"tax\":\"NONE\",\"whiteHandicapBonus\":\"0\"},\"turnNumber\":136,\"whitePlayer\":\"kata1-b40c256-s10312780288-d2513725330\"}";
  //		    contributeGames = new ArrayList<ContributeGameInfo>();
  //		    unParseGameInfos = new ArrayList<ContributeUnParseGameInfo>();
  //		    testGetJsonGameInfo(tryToGetJsonString(jsonTestMove1), contributeGames, unParseGameInfos);
  //		    testGetJsonGameInfo(tryToGetJsonString(jsonTestMove2), contributeGames, unParseGameInfos);
  //		    testGetJsonGameInfo(tryToGetJsonString(jsonTestMove3), contributeGames, unParseGameInfos);
  //		    watchGame(0, true);
  //  }

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
      } else {
        currentGame = new ContributeGameInfo();
        if (tryToParseJsonGame(currentGame, true, jsonInfo, unParseInfos))
          tryToUseUnParseGameInfos(currentGame, unParseInfos);
        games.add(currentGame);
        if (watchingGameIndex == -1) watchGame(0, Lizzie.config.contributeWatchAlwaysLastMove);
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
    }
    JSONArray initStones = jsonInfo.getJSONArray("initialStones");
    if (initStones.length() > 0) {
      ArrayList<ContributeMoveInfo> initStoneList = new ArrayList<ContributeMoveInfo>();
      for (int i = 0; i < initStones.length(); i++) {
        ContributeMoveInfo move = new ContributeMoveInfo();
        List<Object> moveInfo = initStones.getJSONArray(i).toList();
        move.isBlack = moveInfo.get(0).toString().equals("B");
        move.pos = Board.convertNameToCoordinates(moveInfo.get(1).toString());
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
        move.pos = Board.convertNameToCoordinates(moveInfo.get(1).toString());
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
        move.pos = Board.convertNameToCoordinates(lastMove.get(1).toString());
        move.isPass = move.pos[0] < 0;
        JSONArray moveInfos = jsonInfo.getJSONArray("moveInfos");
        move.candidates = Utils.getBestMovesFromJsonArray(moveInfos);
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

  public void watchGame(int index, boolean loadToLast) {
    int currentMoveNumber = Lizzie.board.getHistory().getCurrentHistoryNode().getData().moveNumber;
    boolean changedGame = false;
    ContributeGameInfo watchGame = contributeGames.get(index);
    ArrayList<ContributeMoveInfo> remainList = new ArrayList<ContributeMoveInfo>();
    if (currentWatchGame == watchGame
        && isContributeGameAndCurrentBoardSame(watchGame, remainList)) {
      if (remainList != null && remainList.size() > 0) setContributeMoveList(remainList);
      else if (currentWatchGame.complete) {
        // 判断是否跳转下一局,watchingGameIndex
        watchGame(index + 1, loadToLast);
        return;
      }
    } else {
      if (Lizzie.config.contributeWatchSkipNone19
          && (watchGame.sizeX != 19 || watchGame.sizeY != 19)) {
        watchGame(index + 1, loadToLast);
        return;
      }
      changedGame = true;
      Lizzie.board.clear(false);
      currentWatchGame = watchGame;
      watchingGameIndex = index;
      if (Lizzie.frame.contributeView != null)
        Lizzie.frame.contributeView.setWathGameIndex(watchingGameIndex);
      Lizzie.board.reopen(currentWatchGame.sizeX, currentWatchGame.sizeY);
      Lizzie.board
          .getHistory()
          .getGameInfo()
          .setPlayerBlack(currentWatchGame.blackPlayer.replaceAll(" ", ""));
      Lizzie.board
          .getHistory()
          .getGameInfo()
          .setPlayerWhite(currentWatchGame.whitePlayer.replaceAll(" ", ""));
      Lizzie.board.getHistory().getGameInfo().setKomi(currentWatchGame.komi);
      if (currentWatchGame.complete) {
        Lizzie.board.getHistory().getGameInfo().setResult(currentWatchGame.gameResult);
        setReultToView(currentWatchGame.gameResult);
      }
      if (currentWatchGame.initMoveList != null && currentWatchGame.initMoveList.size() > 0)
        setContributeMoveList(currentWatchGame.initMoveList);
      if (currentWatchGame.moveList != null && currentWatchGame.moveList.size() > 0)
        setContributeMoveList(currentWatchGame.moveList);
    }
    if (!changedGame && !loadToLast) {
      //      if (Lizzie.board.getHistory().getCurrentHistoryNode().getData().moveNumber -
      // currentMoveNumber
      //          > 1)
      Lizzie.board.goToMoveNumber(currentMoveNumber);
    } else if (loadToLast) {
      while (Lizzie.board.nextMove(false)) ;
    }
    Lizzie.frame.refresh();
    // 切换到不同局,同步ContributeGameInfo到界面上(如是同一局直接返回),或直接跳倒数第二手
    // 还是显示前一手模式+跳到最后一手?
    // watchingGameIndex
  }

  private void setContributeMoveList(ArrayList<ContributeMoveInfo> remainList) {
    while (Lizzie.board.nextMove(false)) ;
    for (ContributeMoveInfo move : remainList) {
      // 把所有move place进去 再设置bestmoves进去
      if (move.candidates != null)
        Lizzie.board
            .getData()
            .tryToSetBestMoves(
                move.candidates,
                Lizzie.board.getHistory().getCurrentTurnPlayerShortName(),
                false,
                MoveData.getPlayouts(move.candidates));
      if (move.isPass) Lizzie.board.getHistory().pass(move.isBlack ? Stone.BLACK : Stone.WHITE);
      else
        Lizzie.board
            .getHistory()
            .place(move.pos[0], move.pos[1], move.isBlack ? Stone.BLACK : Stone.WHITE);
    }
  }

  private boolean isContributeGameAndCurrentBoardSame(
      ContributeGameInfo watchGame, ArrayList<ContributeMoveInfo> remainList) {
    boolean isSame = true;
    BoardHistoryNode startNode = Lizzie.board.getHistory().getStart();
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
            commands, engineCommand, message, !useJavaSSH && OS.isWindows(), false);
    engineFailedMessage.setModal(true);
    engineFailedMessage.setVisible(true);
  }
}
