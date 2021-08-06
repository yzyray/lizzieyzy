package featurecat.lizzie.analysis;

import featurecat.lizzie.Lizzie;
import featurecat.lizzie.gui.EngineFailedMessage;
import featurecat.lizzie.gui.EstimateResults;
import featurecat.lizzie.gui.LizzieFrame;
import featurecat.lizzie.gui.RemoteEngineData;
import featurecat.lizzie.gui.SetEstimateParam;
import featurecat.lizzie.rules.Board;
import featurecat.lizzie.rules.Movelist;
import featurecat.lizzie.rules.Stone;
import featurecat.lizzie.util.Utils;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import org.jdesktop.swingx.util.OS;

public class KataEstimate {
  public Process process;
  private final ResourceBundle resourceBundle =
      Lizzie.config.useLanguage == 0
          ? ResourceBundle.getBundle("l10n.DisplayStrings")
          : (Lizzie.config.useLanguage == 1
              ? ResourceBundle.getBundle("l10n.DisplayStrings", new Locale("zh", "CN"))
              : ResourceBundle.getBundle("l10n.DisplayStrings", new Locale("en", "US")));
  // private boolean isShuttingdown = false;
  private BufferedReader inputStream;
  private BufferedOutputStream outputStream;
  private BufferedReader errorStream;

  // public boolean gtpConsole;
  private boolean hasResult = false;
  // private boolean isLoaded = false;
  // private Double threshold = Lizzie.config.estimateThreshold;
  private String engineCommand;
  // private List<String> commands;
  private int cmdNumber;
  // private int currentCmdNum;
  private ArrayDeque<String> cmdQueue;
  private ScheduledExecutorService executor;
  private ScheduledExecutorService executorErr;
  public String currentEnginename = "";
  ArrayList<Double> tempcount = new ArrayList<Double>();
  public int blackEatCount = 0;
  public int whiteEatCount = 0;
  public int blackPrisonerCount = 0;
  public int whitePrisonerCount = 0;
  EstimateResults results;
  boolean firstcount = true;
  private int numberofcount = 0;
  private int territoryCount = 0;
  // private boolean hasTempTerritory = false;
  private boolean canGetOwnership = false;
  private List<String> commands;
  //  private boolean first = true;
  private boolean isPreLoad;

  public boolean useJavaSSH = false;
  public String ip;
  public String port;
  public String userName;
  public String password;
  public boolean useKeyGen;
  public String keyGenPath;
  public EstimateEngineSSHController javaSSH;
  public boolean javaSSHClosed = false;
  public boolean isNormalEnd = false;
  private boolean noGtp = false;
  // public boolean noread = false;

  public KataEstimate(boolean isPreLoad) throws IOException {
    cmdNumber = 1;
    // currentCmdNum = 0;
    cmdQueue = new ArrayDeque<>();
    noGtp = isPreLoad;
    // gtpConsole = true;
    engineCommand =
        Lizzie.config.useZenEstimate
            ? Lizzie.config.zenEstimateCommand
            : Lizzie.config.estimateCommand;
    this.isPreLoad = isPreLoad;
    RemoteEngineData remoteData = Utils.getEstimateEngineRemoteEngineData();
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
    currentEnginename = engineCommand;
    // isShuttingdown = false;
    commands = Utils.splitCommand(engineCommand);

    if (this.useJavaSSH) {
      this.javaSSH = new EstimateEngineSSHController(this, this.ip, this.port, this.isPreLoad);
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
        // TODO Auto-generated catch block
        showErrMsg(e);
        process = null;
        return;
      }
      initializeStreams();
    }
    executor = Executors.newSingleThreadScheduledExecutor();
    executor.execute(this::read);
    executorErr = Executors.newSingleThreadScheduledExecutor();
    executorErr.execute(this::readError);
    if (!Lizzie.config.useZenEstimate) {
      if (Lizzie.config.estimateArea) {
        sendCommand("kata-set-rules chinese");
      } else {
        sendCommand("kata-set-rules japanese");
      }

      //      sendCommand("clear_board");
      //      boardSize(Lizzie.board.boardWidth, Lizzie.board.boardHeight);
      //      sendCommand("kata-raw-nn 0");
    }
  }

  private void showErrMsg(Exception e) {
    if (isPreLoad) return;
    String errMas = "";
    if (Lizzie.config.useZenEstimate) {
      errMas = resourceBundle.getString("KataEstimate.errorHint2");
    } else {
      errMas = resourceBundle.getString("KataEstimate.errorHint"); // "加载形势判断引擎失败,请确认形势判断引擎设置正确");
    }
    String err = e.getLocalizedMessage();
    tryToDignostic(
        errMas
            + ": "
            + ((err == null)
                ? resourceBundle.getString("Leelaz.engineStartNoExceptionMessage")
                : err));
    if (Lizzie.frame.setEstimateParam == null || !Lizzie.frame.setEstimateParam.isVisible()) {
      Lizzie.frame.setEstimateParam = new SetEstimateParam();
      Lizzie.frame.setEstimateParam.setVisible(true);
    }
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
    if (!noGtp) Lizzie.gtpConsole.addErrorLine(line + "\n");
  }

  private void read() {
    try {
      int c;
      StringBuilder line = new StringBuilder();
      // while ((c = inputStream.read()) != -1) {
      while ((c = inputStream.read()) != -1) {
        line.append((char) c);
        if ((c == '\n')) {
          try {
            parseLine(line.toString());
          } catch (Exception ex) {
          }
          line = new StringBuilder();
        }
      }
      // this line will be reached when engine shuts down
      System.out.println("estimate process ended.");
      if (this.useJavaSSH) javaSSHClosed = true;
      if (!isNormalEnd) tryToDignostic(resourceBundle.getString("KataEstimate.errorHint"));
      shutdown();
      // Do no exit for switching weights
      // System.exit(-1);
    } catch (IOException e) {
      if (this.useJavaSSH) javaSSHClosed = true;
      showErrMsg(e);
      process = null;
      return;
    }
  }

  private void parseLine(String line) {
    synchronized (this) {
      //      if (line.startsWith("=  ")) {
      //        String[] params = line.trim().split(" ");
      //        // Lizzie.gtpConsole.addLineforce("这是详细点目第一行,21分参数");
      //        for (int i = 2; i < params.length; i++) tempcount.add(Integer.parseInt(params[i]));
      //      }
      // # 5 5 5 0 0 0 0 0 0 0 0 0 0 0 0 -4 -5 -5 -5
      if (Lizzie.config.useZenEstimate) {
        if (line.startsWith("#")) {
          //  if (first) first = false;
          String[] params = line.substring(1).trim().split("\\s+");
          if (params.length == Board.boardWidth) {
            for (int i = 0; i < params.length; i++) {
              try {
                Double temp = -Double.parseDouble(params[i]);
                if (temp >= 300 || temp <= -300) tempcount.add(-temp);
                else tempcount.add(0.0);
                // tempcount.add(-Double.parseDouble(params[i]));
              } catch (NumberFormatException ex) {
                tempcount.add(0.0);
              }
            }
          }
          if (tempcount.size() == Board.boardHeight * Board.boardWidth) {
            //   canGetOwnership = false;
            // if (first) first = false;
            // else
            territory();
            tempcount = new ArrayList<Double>();
            // 结束并显示
          }
        }
        if (!noGtp) Lizzie.gtpConsole.addLineEstimate(line);
      } else {
        if (line.startsWith("whiteOwnership")) {
          canGetOwnership = true;
          tempcount = new ArrayList<Double>();
        }

        if (canGetOwnership) {
          String[] params = line.trim().split("\\s+");
          if (params.length == Board.boardWidth) {
            for (int i = 0; i < params.length; i++) {
              try {
                Double temp = -Double.parseDouble(params[i]);
                if (temp >= Lizzie.config.estimateThreshold
                    || temp <= -Lizzie.config.estimateThreshold) tempcount.add(temp);
                else tempcount.add(0.0);
                // tempcount.add(-Double.parseDouble(params[i]));
              } catch (NumberFormatException ex) {
                tempcount.add(0.0);
              }
            }
          }
          if (tempcount.size() == Board.boardHeight * Board.boardWidth) {
            canGetOwnership = false;
            territory();
            // 结束并显示
          }
        }
        if (!noGtp) Lizzie.gtpConsole.addLineEstimate(line);
      }
      //      if (line.startsWith("#")) {
      //
      //        String[] params = line.substring(2).trim().split(" ");
      //        if (params.length == Lizzie.board.boardWidth) {
      //          for (int i = 0; i < params.length; i++) {
      //            int temp = Integer.parseInt(params[i]);
      //            if (temp >= threshold || temp <= -threshold) tempcount.add(temp);
      //            else tempcount.add(0);
      //          }
      //          // Lizzie.gtpConsole.addLineforce("这是详细点目");
      //        }
      //      }
      //      //  }
      //      if (line.startsWith("= territory")) {
      //        // String[] params = line.trim().split(" ");
      //        // if (params.length == 14) {
      //        // Lizzie.gtpConsole.addLineforce("这是点目结果");
      //        // if (noread) {
      //        //   numberofcount = 0;
      //        // } else {
      //        territory();
      //        // }
      //        //  }
      //      }
    }
  }

  private Double getRealCountWhite(int[] coords, Double count) {
    if (coords[0] >= 1) {
      int left = (coords[0] - 1) + coords[1] * Board.boardWidth;
      if (tempcount.get(left) >= 0) {
        return 0.0;
      }
    }
    if (coords[0] <= (Board.boardWidth - 2)) {
      int right = (coords[0] + 1) + coords[1] * Board.boardWidth;
      if (tempcount.get(right) >= 0) {
        return 0.0;
      }
    }
    if (coords[1] >= 1) {
      int top = coords[0] + (coords[1] - 1) * Board.boardWidth;
      if (tempcount.get(top) >= 0) {
        return 0.0;
      }
    }
    if (coords[1] <= (Board.boardHeight - 2)) {
      int bottom = coords[0] + (coords[1] + 1) * Board.boardWidth;
      if (tempcount.get(bottom) >= 0) {
        return 0.0;
      }
    }
    return count;
  }

  private Double getRealCountBlack(int[] coords, Double count) {
    if (coords[0] >= 1) {
      int left = (coords[0] - 1) + coords[1] * Board.boardWidth;
      if (tempcount.get(left) <= 0) {
        return 0.0;
      }
    }
    if (coords[0] <= (Board.boardWidth - 2)) {
      int right = (coords[0] + 1) + coords[1] * Board.boardWidth;
      if (tempcount.get(right) <= 0) {
        return 0.0;
      }
    }
    if (coords[1] >= 1) {
      int top = coords[0] + (coords[1] - 1) * Board.boardWidth;
      if (tempcount.get(top) <= 0) {
        return 0.0;
      }
    }
    if (coords[1] <= (Board.boardHeight - 2)) {
      int bottom = coords[0] + (coords[1] + 1) * Board.boardWidth;
      if (tempcount.get(bottom) <= 0) {
        return 0.0;
      }
    }
    return count;
  }

  private void territory() {
    blackEatCount = Lizzie.board.getData().blackCaptures;
    whiteEatCount = Lizzie.board.getData().whiteCaptures;
    blackPrisonerCount = 0;
    whitePrisonerCount = 0;
    int blackpoint = 0;
    int whitepoint = 0;
    int blackAlive = 0;
    int whiteAlive = 0;
    ArrayList<Double> tempCountForRender = new ArrayList<Double>();
    int limit = 0;
    for (Double counts : tempcount) {
      tempCountForRender.add(counts);
      limit++;
      if (limit >= Board.boardWidth * Board.boardHeight) break;
    }

    Stone[] stones = Lizzie.board.getStones();
    for (int i = 0; i < stones.length; i++) {
      int[] coords = Board.getCoord(i);
      int countOrder = coords[0] + coords[1] * Board.boardWidth;
      Double count = tempcount.get(countOrder);
      if (stones[i] == Stone.BLACK) {
        if (count < 0) {
          blackPrisonerCount++;
          whitepoint++;
        }
        if (count >= 0) {
          blackAlive++;
          if (count > 0) {
            tempCountForRender.set(countOrder, 0.0);
          }
        }
      }
      if (stones[i] == Stone.WHITE) {
        if (count > 0) {
          whitePrisonerCount++;
          blackpoint++;
        }
        if (count <= 0) {
          whiteAlive++;
          if (count < 0) {
            tempCountForRender.set(countOrder, 0.0);
          }
        }
      }
      if (stones[i] == Stone.EMPTY) {
        if (count > 0) {
          Double trueCount = getRealCountBlack(coords, count);
          tempCountForRender.set(countOrder, trueCount);
          if (trueCount > 0) {
            blackpoint++;
          }
        }
        if (count < 0) {
          Double trueCount = getRealCountWhite(coords, count);
          tempCountForRender.set(countOrder, trueCount);
          if (trueCount < 0) {
            whitepoint++;
          }
        }
      }
    }
    LizzieFrame.boardRenderer.drawEstimateImage(tempCountForRender);
    if (Lizzie.frame.floatBoard != null && Lizzie.frame.floatBoard.isVisible())
      Lizzie.frame.floatBoard.boardRenderer.drawEstimateImage(tempCountForRender);
    Lizzie.frame.refresh();
    hasResult = true;
    if (firstcount) {
      results = Lizzie.estimateResults;
      results.Counts(
          blackEatCount,
          whiteEatCount,
          blackPrisonerCount,
          whitePrisonerCount,
          blackpoint,
          whitepoint,
          blackAlive,
          whiteAlive);
      if (!results.isVisible()) {
        results.showEstimate();
        if (Lizzie.frame.isAutocounting) Lizzie.frame.setVisible(true);
      } else if (!Lizzie.frame.isAutocounting) results.setVisible(true);
      firstcount = false;
    } else {
      results.Counts(
          blackEatCount,
          whiteEatCount,
          blackPrisonerCount,
          whitePrisonerCount,
          blackpoint,
          whitepoint,
          blackAlive,
          whiteAlive);
      if (!results.isVisible()) {
        results.showEstimate();
        if (Lizzie.frame.isAutocounting) Lizzie.frame.setVisible(true);
      } else if (!Lizzie.frame.isAutocounting) results.setVisible(true);
    }
    numberofcount = 0;
  }

  public void shutdown() {
    // isShuttingdown = true;
    isNormalEnd = true;
    if (this.useJavaSSH) this.javaSSH.close();
    else if (process != null && process.isAlive()) process.destroy();
  }

  public void sendAndEstimate(String command, boolean needVerify) {
    new Thread() {
      public void run() {
        if (!needVerify
            || command.startsWith("play")
            || command.startsWith("undo")
            || command.startsWith("clear")
            || command.startsWith("boardsize")
            || command.startsWith("rectan")) {
          sendCommand(command);
          countStones();
        }
      }
    }.start();
  }

  public void sendCommand(String command) {
    if (!cmdQueue.isEmpty()) {
      cmdQueue.removeLast();
    }
    cmdQueue.addLast(command);
    trySendCommandFromQueue();
  }

  private void trySendCommandFromQueue() {
    // synchronized (cmdQueue) {
    if (cmdQueue.isEmpty()) {
      return;
    }
    String command = cmdQueue.removeFirst();
    sendCommandToZen(command);
    // }
  }

  private void sendCommandToZen(String command) {
    // System.out.printf("> %d %s\n", cmdNumber, command);
    // try {
    if (!noGtp) Lizzie.gtpConsole.addEstimateCommand(command, cmdNumber);
    // } catch (Exception ex) {
    // }
    cmdNumber++;
    try {
      outputStream.write((command + "\n").getBytes());
      outputStream.flush();
    } catch (Exception e) {
      // e.printStackTrace();
    }
  }

  private void playmove(int x, int y, boolean isblack) {
    String coordsname = Board.convertCoordinatesToName(x, y);
    String color = isblack ? "b" : "w";

    sendCommand("play" + " " + color + " " + coordsname);
  }

  public void boardSize(int width, int height) {
    if (width != height) sendCommand("rectangular_boardsize " + width + " " + height);
    else sendCommand("boardsize " + width);
  }

  public void syncboradstat() {
    noGtp = false;
    sendCommand("clear_board");
    boardSize(Board.boardWidth, Board.boardHeight);
    cmdNumber = 1;
    ArrayList<Movelist> movelist = Lizzie.board.getMoveList();
    if (Lizzie.board.hasStartStone) {
      for (int i = 0; i < Lizzie.board.startStonelist.size(); i++) {
        movelist.add(Lizzie.board.startStonelist.get(i));
      }
    }
    int lenth = movelist.size();
    for (int i = 0; i < lenth; i++) {
      Movelist move = movelist.get(lenth - 1 - i);
      if (!move.ispass) {
        playmove(move.x, move.y, move.isblack);
      }
    }
  }

  public void countStones() {
    if (numberofcount > 0) {
      // if (hasTempTerritory) return;
      Runnable runnable =
          new Runnable() {
            public void run() {
              int territoryCountTemp = territoryCount;
              try {
                Thread.sleep(300);
                //  hasTempTerritory = false;
              } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
              }
              if (territoryCountTemp == territoryCount) {
                // tempcount = new ArrayList<Double>();
                territoryCount++;
                numberofcount++;
                blackEatCount = 0;
                whiteEatCount = 0;
                blackPrisonerCount = 0;
                whitePrisonerCount = 0;
                if (Lizzie.config.useZenEstimate) sendCommand("territory");
                else sendCommand("kata-raw-nn 0");
                // Lizzie.gtpConsole.addLineforce("count");
              }
            }
          };
      Thread thread = new Thread(runnable);
      thread.start();
      return;
    }
    territoryCount++;
    numberofcount++;
    // tempcount = new ArrayList<Double>();
    blackEatCount = 0;
    whiteEatCount = 0;
    blackPrisonerCount = 0;
    whitePrisonerCount = 0;
    if (Lizzie.config.useZenEstimate) sendCommand("territory");
    else sendCommand("kata-raw-nn 0");
    // Lizzie.gtpConsole.addLineforce("count");
    //
    //  sendCommand("score_statistics");
    if (!hasResult) {
      Runnable runnable2 =
          new Runnable() {
            public void run() {
              try {
                Thread.sleep(5000);
              } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
              }
              if (!hasResult) {
                results = Lizzie.estimateResults;
                results.Counts(0, 0, 0, 0, 0, 0, 0, 0);
                if (!results.isVisible()) {
                  results.showEstimate();
                  if (Lizzie.frame.isAutocounting) Lizzie.frame.setVisible(true);
                } else if (!Lizzie.frame.isAutocounting) results.setVisible(true);
                firstcount = false;
                //  numberofcount = 0;
              }
            }
          };
      Thread thread2 = new Thread(runnable2);
      thread2.start();
    }
  }

  public void tryToDignostic(String message) {
    EngineFailedMessage engineFailedMessage =
        new EngineFailedMessage(
            commands, engineCommand, message, !this.useJavaSSH && OS.isWindows());
    engineFailedMessage.setVisible(true);
  }
}
