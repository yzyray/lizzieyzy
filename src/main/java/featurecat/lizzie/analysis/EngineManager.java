package featurecat.lizzie.analysis;

import featurecat.lizzie.Config;
import featurecat.lizzie.Lizzie;
import featurecat.lizzie.gui.EngineData;
import featurecat.lizzie.gui.LizzieFrame;
import featurecat.lizzie.gui.Menu;
import featurecat.lizzie.gui.SgfWinLossList;
import featurecat.lizzie.rules.Board;
import featurecat.lizzie.rules.EngineCountDown;
import featurecat.lizzie.rules.Movelist;
import featurecat.lizzie.rules.SGFParser;
import featurecat.lizzie.rules.Zobrist;
import featurecat.lizzie.util.Utils;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import org.json.JSONException;

public class EngineManager {
  private final ResourceBundle resourceBundle = Lizzie.resourceBundle;
  public static boolean isUpdating = false;
  public List<Leelaz> engineList;
  public static int currentEngineNo;
  private int engineNo = 1;
  public static int currentEngineNo2 = -1;
  public static boolean isEmpty = false;
  String name = "";
  public static EngineGameInfo engineGameInfo = new EngineGameInfo();
  public static boolean isEngineGame = false;
  public static boolean isPreEngineGame = false;
  public static boolean isSaveingEngineSGF = false;
  public EngineCountDown playingAgainstHumanEngineCountDown;
  public EngineCountDown firstEngineCountDown;
  public EngineCountDown secondEngineCountDown;
  private ScheduledThreadPoolExecutor timeScheduled;
  private int timeScheduledTimes;
  Timer timer;

  public EngineManager(Config config, int index) throws JSONException, IOException {
    ArrayList<EngineData> engineData = Utils.getEngineData();
    if (index > engineData.size() - 1) {
      index = 0;
    }
    engineList = new ArrayList<Leelaz>();
    // engineList.add(lz);
    for (int i = 0; i < engineData.size(); i++) {
      EngineData engineDt = engineData.get(i);
      Leelaz e;
      e = new Leelaz(engineDt.commands);
      e.preload = engineDt.preload;
      e.width = engineDt.width;
      e.height = engineDt.height;
      e.oriWidth = engineDt.width;
      e.oriHeight = engineDt.height;
      e.komi = engineDt.komi;
      e.orikomi = engineDt.komi;
      e.useJavaSSH = engineDt.useJavaSSH;
      e.ip = engineDt.ip;
      e.port = engineDt.port;
      e.useKeyGen = engineDt.useKeyGen;
      e.keyGenPath = engineDt.keyGenPath;
      e.userName = engineDt.userName;
      e.password = engineDt.password;
      e.initialCommand = engineDt.initialCommand;
      if (i == index) {
        if (e.oriWidth != 19 || e.oriHeight != 19) {
          Board.boardWidth = e.oriWidth;
          Board.boardHeight = e.oriHeight;
          Zobrist.init();
          Lizzie.board.clear(false);
        }
        Lizzie.leelaz = e;
        e.preload = true;
        e.firstLoad = true;
        new Thread() {
          public void run() {
            try {
              e.startEngine(engineDt.index);
              featurecat.lizzie.gui.Menu.engineMenu.setText(
                  "[" + (e.currentEngineN() + 1) + "]: " + e.oriEnginename);
            } catch (IOException e2) {
              // TODO Auto-generated catch block
              e2.printStackTrace();
            }
            while (!e.isLoaded() || e.isCheckingName) {
              try {
                Thread.sleep(100);
              } catch (InterruptedException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
              }
            }
            if (currentEngineNo > 20) LizzieFrame.menu.changeEngineIcon(20, 3);
            else LizzieFrame.menu.changeEngineIcon(currentEngineNo, 3);
            Lizzie.board.resendMoveToEngine(Lizzie.leelaz);
          }
        }.start();
      } else {
        if (e.preload) {
          new Thread() {
            public void run() {
              try {
                e.startEngine(engineDt.index);
              } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
              }
            }
          }.start();
        }
      }
      engineList.add(e);
    }
    currentEngineNo = index;
    engineNo = index;
    if (index == -1) {
      Lizzie.leelaz.isKatago = true;
      Lizzie.leelaz.isLoaded = true;
      featurecat.lizzie.gui.Menu.engineMenu.setText(resourceBundle.getString("Menu.noEngine"));
      if (Lizzie.config.isDoubleEngineMode())
        Menu.engineMenu2.setText(resourceBundle.getString("Menu.noEngine"));
      isEmpty = true;
      LizzieFrame.menu.updateMenuStatusForEngine();
      Lizzie.frame.reSetLoc();
      Lizzie.frame.addInput(false);

      SwingUtilities.invokeLater(
          new Runnable() {
            public void run() {
              if (Lizzie.config.uiConfig.optBoolean("show-badmoves-frame", false)) {
                Lizzie.frame.toggleBadMoves();
              }
              if (Lizzie.config.uiConfig.optBoolean("show-suggestions-frame", false)) {
                Lizzie.frame.toggleBestMoves();
              }
            }
          });
    }
    Lizzie.gtpConsole.console.setText("");
    autoCheckEngineAlive(Lizzie.config.autoCheckEngineAlive);
    if (Lizzie.config.uiConfig.optBoolean("autoload-empty", false) && Lizzie.config.showStatus)
      Lizzie.frame.refresh();
  }

  public void autoCheckEngineAlive(boolean enable) {
    if (enable) {
      if (timer == null) {
        timer =
            new Timer(
                5000,
                new ActionListener() {
                  public void actionPerformed(ActionEvent evt) {
                    checkEngineAlive();
                    try {
                    } catch (Exception e) {
                    }
                  }
                });
        timer.start();
      } else timer.start();
    } else {
      if (timer != null) timer.stop();
    }
  }

  public boolean startEngineGame(
      int engineBlack,
      int engineWhite,
      int timeBlack,
      int timeWhite,
      int playoutsBlack,
      int playoutsWhite,
      int firstPlayoutsBlack,
      int firstPlayoutsWhite,
      boolean isBatchGame,
      int batchGameNumber,
      String batchGameName,
      boolean isContinueGame,
      boolean isGenmove,
      boolean isExchange,
      boolean checkGameMaxMove,
      int maxGameMoves) {
    if (Lizzie.frame.isTrying) Lizzie.frame.tryPlay(false);
    engineGameInfo = new EngineGameInfo();
    if (Lizzie.frame.isShowingHeatmap) Lizzie.leelaz.toggleHeatmap(true);
    if (!isEmpty && Lizzie.leelaz != null) {
      Lizzie.leelaz.clearBestMoves();
    }
    Lizzie.frame.hasEnginePkTitile = false;
    Lizzie.frame.enginePkTitile = "";
    if (engineBlack == engineWhite) {
      Utils.showMsg(resourceBundle.getString("EngineManager.engineGameSameEngine"));
      return false;
    }
    if (!isGenmove) {
      if (timeBlack <= 0 && playoutsBlack <= 0 && firstPlayoutsBlack <= 0) {
        Utils.showMsg(resourceBundle.getString("EngineManager.engineGameBlackSettingWrong"));
        return false;
      }
      if (timeWhite <= 0 && playoutsWhite <= 0 && firstPlayoutsWhite <= 0) {
        Utils.showMsg(resourceBundle.getString("EngineManager.engineGameWhiteSettingWrong"));
        return false;
      }
    }
    engineGameInfo = new EngineGameInfo();
    engineGameInfo.isGenmove = isGenmove;
    engineGameInfo.blackEngineIndex = engineBlack;
    engineGameInfo.whiteEngineIndex = engineWhite;
    engineGameInfo.firstEngineIndex = engineBlack;
    engineGameInfo.secondEngineIndex = engineWhite;
    engineGameInfo.timeBlack = timeBlack;
    engineGameInfo.timeWhite = timeWhite;
    engineGameInfo.timeFirstEngine = timeBlack;
    engineGameInfo.timeSecondEngine = timeWhite;
    engineGameInfo.playoutsBlack = playoutsBlack;
    engineGameInfo.playoutsWhite = playoutsWhite;
    engineGameInfo.playoutsFirstEngine = playoutsBlack;
    engineGameInfo.firstPlayoutsFirstEngine = firstPlayoutsBlack;
    engineGameInfo.playoutsSecondEngine = playoutsWhite;
    engineGameInfo.firstPlayoutsSecondEngine = firstPlayoutsWhite;
    engineGameInfo.firstPlayoutsBlack = firstPlayoutsBlack;
    engineGameInfo.firstPlayoutsWhite = firstPlayoutsWhite;
    engineGameInfo.isBatchGame = isBatchGame;
    engineGameInfo.batchNumber = batchGameNumber;
    engineGameInfo.isExchange = isExchange;
    engineGameInfo.batchNumberCurrent = 1;
    engineGameInfo.isContinueGame = isContinueGame;
    engineGameInfo.blackMinMove = Lizzie.config.firstEngineMinMove;
    engineGameInfo.blackResignMoveCounts = Lizzie.config.firstEngineResignMoveCounts;
    engineGameInfo.blackResignWinrate = Lizzie.config.firstEngineResignWinrate;

    engineGameInfo.whiteMinMove = Lizzie.config.secondEngineMinMove;
    engineGameInfo.whiteResignMoveCounts = Lizzie.config.secondEngineResignMoveCounts;
    engineGameInfo.whiteResignWinrate = Lizzie.config.secondEngineResignWinrate;
    if (checkGameMaxMove) engineGameInfo.maxGameMoves = maxGameMoves;
    else engineGameInfo.maxGameMoves = Board.boardHeight * Board.boardWidth * 2;
    engineGameInfo.SF = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
    if (Lizzie.frame.enginePkSgfWinLoss != null)
      engineGameInfo.engineGameSgfWinLoss = Lizzie.frame.enginePkSgfWinLoss;
    isEmpty = false;
    if (isGenmove) {
      engineGameInfo.settingFirst =
          resourceBundle.getString("EngineGameInfo.settingFirst"); // "第一引擎设置:";
      if (Lizzie.config.pkAdvanceTimeSettings) {
        engineGameInfo.settingFirst +=
            resourceBundle.getString("EngineGameInfo.time") + Lizzie.config.advanceBlackTimeTxt;
        engineGameInfo.settingSecond +=
            resourceBundle.getString("EngineGameInfo.time") + Lizzie.config.advanceWhiteTimeTxt;
        firstEngineCountDown = new EngineCountDown();
        boolean firstEngineParseSucess =
            firstEngineCountDown.setEngineCountDown(
                Lizzie.config.advanceBlackTimeTxt, engineList.get(engineGameInfo.firstEngineIndex));
        secondEngineCountDown = new EngineCountDown();
        boolean secondEngineParseSucess =
            secondEngineCountDown.setEngineCountDown(
                Lizzie.config.advanceWhiteTimeTxt,
                engineList.get(engineGameInfo.secondEngineIndex));
        if (!firstEngineParseSucess) {
          firstEngineCountDown = null;
          Utils.showMsgNoModal(
              resourceBundle.getString("EngineManager.parseAdvcanceTimeSettingsFailed"));
        }
        if (!secondEngineParseSucess) {
          secondEngineCountDown = null;
          Utils.showMsgNoModal(
              resourceBundle.getString("EngineManager.parseAdvcanceTimeSettingsFailed"));
        }

      } else {
        if (engineGameInfo.timeFirstEngine > 0)
          engineGameInfo.settingFirst +=
              resourceBundle.getString("EngineGameInfo.time")
                  + engineGameInfo.timeFirstEngine
                  + resourceBundle.getString("SGFParse.seconds");

        engineGameInfo.settingFirst +=
            "\r\n"
                + resourceBundle.getString("EngineGameInfo.command")
                + engineList.get(engineGameInfo.firstEngineIndex).getEngineCommand();

        engineGameInfo.settingSecond =
            resourceBundle.getString("EngineGameInfo.settingSecond"); // "第二引擎设置:";
        if (engineGameInfo.timeSecondEngine > 0)
          engineGameInfo.settingSecond +=
              resourceBundle.getString("EngineGameInfo.time")
                  + engineGameInfo.timeSecondEngine
                  + resourceBundle.getString("SGFParse.seconds");

        engineGameInfo.settingSecond +=
            "\r\n"
                + resourceBundle.getString("EngineGameInfo.command")
                + engineList.get(engineGameInfo.secondEngineIndex).getEngineCommand();
      }
    } else {
      engineGameInfo.settingFirst =
          resourceBundle.getString("EngineGameInfo.settingFirst"); // "第一引擎设置:";
      if (engineGameInfo.timeFirstEngine > 0)
        engineGameInfo.settingFirst +=
            resourceBundle.getString("EngineGameInfo.time")
                + engineGameInfo.timeFirstEngine
                + resourceBundle.getString("SGFParse.seconds");
      if (engineGameInfo.playoutsFirstEngine > 0)
        engineGameInfo.settingFirst +=
            resourceBundle.getString("EngineGameInfo.totalVisits")
                + engineGameInfo.playoutsFirstEngine;
      if (engineGameInfo.firstPlayoutsFirstEngine > 0)
        engineGameInfo.settingFirst +=
            resourceBundle.getString("EngineGameInfo.firstVisits")
                + engineGameInfo.firstPlayoutsFirstEngine;

      engineGameInfo.settingFirst +=
          "\r\n"
              + resourceBundle.getString("EngineGameInfo.resignThreshold")
              + Lizzie.config.firstEngineMinMove
              + resourceBundle.getString("EngineGameInfo.resignThreshold2")
              + Lizzie.config.firstEngineResignMoveCounts
              + resourceBundle.getString("EngineGameInfo.resignThreshold3")
              + Lizzie.config.firstEngineResignWinrate;

      engineGameInfo.settingFirst +=
          "\r\n"
              + resourceBundle.getString("EngineGameInfo.command")
              + engineList.get(engineGameInfo.firstEngineIndex).getEngineCommand();

      engineGameInfo.settingSecond =
          resourceBundle.getString("EngineGameInfo.settingSecond"); // "第二引擎设置:";
      if (engineGameInfo.timeSecondEngine > 0)
        engineGameInfo.settingSecond +=
            resourceBundle.getString("EngineGameInfo.time")
                + engineGameInfo.timeSecondEngine
                + resourceBundle.getString("SGFParse.seconds");
      if (engineGameInfo.playoutsSecondEngine > 0)
        engineGameInfo.settingSecond +=
            resourceBundle.getString("EngineGameInfo.totalVisits")
                + engineGameInfo.playoutsSecondEngine;
      if (engineGameInfo.firstPlayoutsSecondEngine > 0)
        engineGameInfo.settingSecond +=
            resourceBundle.getString("EngineGameInfo.firstVisits")
                + engineGameInfo.firstPlayoutsSecondEngine;
      engineGameInfo.settingSecond +=
          "\r\n"
              + resourceBundle.getString("EngineGameInfo.resignThreshold")
              + Lizzie.config.secondEngineMinMove
              + resourceBundle.getString("EngineGameInfo.resignThreshold2")
              + Lizzie.config.secondEngineResignMoveCounts
              + resourceBundle.getString("EngineGameInfo.resignThreshold3")
              + Lizzie.config.secondEngineResignWinrate;

      engineGameInfo.settingSecond +=
          "\r\n"
              + resourceBundle.getString("EngineGameInfo.command")
              + engineList.get(engineGameInfo.secondEngineIndex).getEngineCommand();
    }

    if (engineGameInfo.isContinueGame) {
      engineGameInfo.continueGameList = Lizzie.board.getMoveList();
    }
    if (engineGameInfo.isBatchGame) {
      if (batchGameName.equals("")) {
        // batchPkName = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        String SF = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        SF =
            getEngineName(engineGameInfo.firstEngineIndex)
                + "_VS_"
                + getEngineName(engineGameInfo.secondEngineIndex)
                + "_"
                + SF;
        SF = SF.replaceAll("[/\\\\:*?|]", ".");
        SF = SF.replaceAll("[\"<>]", "'");
        engineGameInfo.batchGameName = SF;
      } else {
        engineGameInfo.batchGameName = batchGameName;
      }
    }

    Lizzie.frame.removeInput(true);
    LizzieFrame.winrateGraph.maxcoreMean = 15;
    Lizzie.frame.isPlayingAgainstLeelaz = false;
    Lizzie.frame.isAnaPlayingAgainstLeelaz = false;

    Lizzie.config.isAutoAna = false;
    Lizzie.board.isPkBoard = true;
    LizzieFrame.toolbar.lblenginePkResult.setText("0:0");
    featurecat.lizzie.gui.Menu.engineMenu.setText(
        resourceBundle.getString("EngineManager.engineGamePlaying")); // 对战中
    LizzieFrame.menu.toggleEngineMenuStatus(true, false);
    // 禁用某些按钮
    LizzieFrame.toolbar.enableDisabelForEngineGame(false);
    // 开始新的一局
    startNewEngineGame(true);
    return true;
  }

  public ArrayList<Movelist> getStartListForEnginePk() {
    if (engineGameInfo.isContinueGame) {
      return engineGameInfo.continueGameList;
    }
    if (Lizzie.config.chkEngineSgfStart) {
      int length = Lizzie.frame.enginePKSgfString.size();
      if (Lizzie.config.engineSgfStartRandom) {
        Random random = new Random();
        LizzieFrame.toolbar.currentEnginePkSgfNum = random.nextInt(length);
      } else {
        LizzieFrame.toolbar.currentEnginePkSgfNum = Lizzie.frame.enginePKSgfNum % length;
        Lizzie.frame.enginePKSgfNum++;
      }
      return Lizzie.frame.enginePKSgfString.get(LizzieFrame.toolbar.currentEnginePkSgfNum);
    }
    return null;
  }

  private String formateSaveString(String filename) {
    filename = filename.replaceAll("[/\\\\:*?|]", ".");
    filename = filename.replaceAll("[\"<>]", "'");
    return filename;
  }

  private void saveEngineGameFile(int resignIndex) {
    File file = new File("");
    String courseFile = "";
    try {
      courseFile = file.getCanonicalPath();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

    String sf = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());

    String df = "";
    if (engineGameInfo.isBatchGame) {
      df =
          engineGameInfo.batchNumberCurrent
              + (Lizzie.config.chkPkStartNum ? (Lizzie.config.pkStartNum - 1) : 0)
              + "_"
              + (Lizzie.config.chkEngineSgfStart
                  ? resourceBundle.getString("EngineGameInfo.openingSGFindex")
                      + LizzieFrame.toolbar.currentEnginePkSgfNum
                      + "_"
                  : "");
    }
    df =
        df
            + resourceBundle.getString("Leelaz.black")
            + "("
            + Lizzie.engineManager.engineList.get(engineGameInfo.blackEngineIndex).currentEnginename
            + ")"
            + "_vs_"
            + resourceBundle.getString("Leelaz.white")
            + "("
            + engineList.get(engineGameInfo.whiteEngineIndex).currentEnginename
            + ")";
    // 添加结果
    if (engineList.get(resignIndex).doublePass) {
      df += resourceBundle.getString("EngineManager.doublePassFileName"); // "_双pass对局";
    } else if (Lizzie.board.getHistory().getMoveNumber() > engineGameInfo.maxGameMoves) {
      df += resourceBundle.getString("EngineManager.outOfMoveFileName"); // "_超手数对局";
    } else {
      if (resignIndex == engineGameInfo.whiteEngineIndex) {
        GameInfo gameInfo = Lizzie.board.getHistory().getGameInfo();
        gameInfo.setResult(resourceBundle.getString("Leelaz.blackWin"));
        df =
            df
                + "_"
                + resourceBundle.getString("Leelaz.black")
                + "("
                + engineList.get(engineGameInfo.blackEngineIndex).currentEnginename
                + ")"
                + resourceBundle.getString("Leelaz.win");
      } else {
        GameInfo gameInfo = Lizzie.board.getHistory().getGameInfo();
        gameInfo.setResult(resourceBundle.getString("Leelaz.whiteWin"));
        df =
            df
                + "_"
                + resourceBundle.getString("Leelaz.white")
                + "("
                + engineList.get(engineGameInfo.whiteEngineIndex).currentEnginename
                + ")"
                + resourceBundle.getString("Leelaz.win");
      }
    }
    df = df + "_" + sf;
    // 增加如果已命名,则保存在命名的文件夹下
    df = formateSaveString(df);

    File autoSaveFile;
    File autoSaveFile2 = null;
    if (engineGameInfo.isBatchGame) {
      autoSaveFile =
          new File(
              courseFile
                  + File.separator
                  + "EngineGames"
                  + File.separator
                  + engineGameInfo.batchGameName
                  + File.separator
                  + df
                  + ".sgf");
      autoSaveFile2 =
          new File(
              courseFile
                  + File.separator
                  + "EngineGames"
                  + File.separator
                  + engineGameInfo.SF
                  + File.separator
                  + df
                  + ".sgf");
    } else {
      autoSaveFile =
          new File(courseFile + File.separator + "EngineGames" + File.separator + df + ".sgf");
      autoSaveFile2 =
          new File(courseFile + File.separator + "EngineGames" + File.separator + df + ".sgf");
    }

    File fileParent = autoSaveFile.getParentFile();
    if (!fileParent.exists()) {
      fileParent.mkdirs();
    }
    try {
      SGFParser.save(Lizzie.board, autoSaveFile.getPath());
      if (LizzieFrame.toolbar.enginePkSaveWinrate) {
        String autoSavePng;
        if (engineGameInfo.isBatchGame) {
          autoSavePng =
              courseFile
                  + File.separator
                  + "EngineGames"
                  + File.separator
                  + engineGameInfo.batchGameName
                  + File.separator
                  + df
                  + ".png";

        } else {
          autoSavePng = courseFile + File.separator + "EngineGames" + File.separator + df + ".png";
        }
        Lizzie.frame.saveImage(
            Lizzie.frame.statx,
            Lizzie.frame.staty,
            (int) (Lizzie.frame.grw * 1.03),
            Lizzie.frame.grh + Lizzie.frame.stath,
            autoSavePng);
      }
    } catch (IOException e) {
      // TODO Auto-generated catch block
      if (engineGameInfo.isBatchGame) {
        try {
          File fileParent2 = autoSaveFile2.getParentFile();
          if (!fileParent2.exists()) {
            fileParent2.mkdirs();
          }
          SGFParser.save(Lizzie.board, autoSaveFile2.getPath());

          if (LizzieFrame.toolbar.enginePkSaveWinrate) {

            String autoSavePng2 = null;
            if (engineGameInfo.isBatchGame) {
              autoSavePng2 =
                  courseFile
                      + File.separator
                      + "EngineGames"
                      + File.separator
                      + engineGameInfo.SF
                      + File.separator
                      + df
                      + ".png";
            } else {
              autoSavePng2 =
                  courseFile + File.separator + "EngineGames" + File.separator + df + ".png";
            }
            Lizzie.frame.saveImage(
                Lizzie.frame.statx,
                Lizzie.frame.staty,
                (int) (Lizzie.frame.grw * 1.03),
                Lizzie.frame.grh + Lizzie.frame.stath,
                autoSavePng2);
          }
        } catch (IOException e1) {
          // TODO Auto-generated catch block
          e1.printStackTrace();
        }
      }
      e.printStackTrace();
    }
  }

  private void writeToFile(
      File file,
      String settingAll,
      String settingB,
      String settingW,
      String resultB,
      String resultW,
      String resultOther)
      throws IOException {

    FileOutputStream writerStream = new FileOutputStream(file);
    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(writerStream, "UTF-8"));
    double games = (double) engineGameInfo.batchNumberCurrent;
    double wr =
        (double) engineGameInfo.getFirstEngineWins()
            / (double) (engineGameInfo.getFirstEngineWins() + engineGameInfo.getSecondEngineWins());

    double elo = Math.log10(1.0 / wr - 1.0) * 400;
    double zxwr = (wr + 1.0 / (2.0 * games)) / (1.0 + 1.0 / games);
    double zxwrc =
        1.0
            * Math.sqrt(wr * (1.0 - wr) / games + 1.0 / ((2.0 * games) * (2.0 * games)))
            / (1.0 + 1.0 / games);
    double zxwr2 = (wr + 4.0 / (2.0 * games)) / (1.0 + 4.0 / games);
    double zxwrc2 =
        2.0
            * Math.sqrt(wr * (1.0 - wr) / games + 4.0 / ((2.0 * games) * (2.0 * games)))
            / (1.0 + 4.0 / games);
    double zxwr3 = (wr + 9.0 / (2.0 * games)) / (1.0 + 9.0 / games);
    double zxwrc3 =
        3.0
            * Math.sqrt(wr * (1.0 - wr) / games + 9.0 / ((2.0 * games) * (2.0 * games)))
            / (1.0 + 9.0 / games);
    double elo2 = Math.log10(1.0 / ((zxwr2 > 0.5 ? zxwr2 + zxwrc2 : zxwr2 - zxwrc2)) - 1.0) * 400;

    writer.write(
        settingAll
            + resourceBundle.getString("EngineGameInfo.backgroundPonder")
            + (Lizzie.config.enginePkPonder
                ? resourceBundle.getString("EngineGameInfo.yes")
                : resourceBundle.getString("EngineGameInfo.no")));
    writer.write("\r\n");
    writer.write("\r\n");
    writer.write(settingB);
    writer.write("\r\n");
    writer.write("\r\n");
    writer.write(settingW);
    writer.write("\r\n");
    writer.write("\r\n");
    writer.write(
        resourceBundle.getString("EngineGameInfo.totalGameResults")
            + engineGameInfo.batchNumberCurrent
            + resourceBundle.getString("EngineGameInfo.gameScore")
            + engineGameInfo.getFirstEngineWins()
            + ":"
            + engineGameInfo.getSecondEngineWins()
            + resourceBundle.getString("EngineGameInfo.gameWinrate")
            + String.format(Locale.ENGLISH, "%.2f", wr * 100)
            + "%");
    writer.write("\r\n");
    writer.write("\r\n");
    writer.write(resultB);
    writer.write("\r\n");
    writer.write(resultW);
    writer.write("\r\n");
    writer.write("\r\n");
    writer.write(resourceBundle.getString("EngineGameInfo.timeVisitsTips"));
    // 注:用时和计算量以界面接收为准,因为有输出间隔存在,实际引擎用时更短,计算量可能更高
    writer.write("\r\n");
    writer.write("\r\n");
    writer.write(resultOther);
    writer.write("\r\n");
    writer.write("\r\n");
    if (engineGameInfo.getFirstEngineWins() == 0 || engineGameInfo.getFirstEngineWins() == 0)
      writer.write(
          resourceBundle.getString("EngineGameInfo.secondEngineElo")
              + resourceBundle.getString("EngineGameInfo.elo100Wr"));
    else {
      writer.write(
          resourceBundle.getString("EngineGameInfo.secondEngineElo")
              + (elo > 0 ? "+" : "")
              + String.format(Locale.ENGLISH, "%.2f", elo)
              + " ± "
              + (zxwr2 + zxwrc2 < 1 && zxwr2 + zxwrc2 > 0
                  ? String.format(Locale.ENGLISH, "%.2f", Math.abs(elo2 - elo))
                  : ""));
      if (EngineManager.engineGameInfo.batchNumberCurrent < 50)
        writer.write("?(" + resourceBundle.getString("EngineGameInfo.notEnoughGames") + ")");
    }
    writer.write("\r\n");
    writer.write(
        resourceBundle.getString("EngineGameInfo.oneStdev") // "一个标准差置信区间为:"
            + String.format(Locale.ENGLISH, "%.2f", zxwr * 100)
            + "% ± "
            + String.format(Locale.ENGLISH, "%.2f", zxwrc * 100)
            + "%");
    writer.write("\r\n");
    writer.write(
        resourceBundle.getString("EngineGameInfo.twoStdev") // "两个标准差置信区间为:"
            + String.format(Locale.ENGLISH, "%.2f", zxwr2 * 100)
            + "% ± "
            + String.format(Locale.ENGLISH, "%.2f", zxwrc2 * 100)
            + "%");
    writer.write("\r\n");
    writer.write(
        resourceBundle.getString("EngineGameInfo.threeStdev") // "三个标准差置信区间为:"
            + String.format(Locale.ENGLISH, "%.2f", zxwr3 * 100)
            + "% ± "
            + String.format(Locale.ENGLISH, "%.2f", zxwrc3 * 100)
            + "%");
    writer.write("\r\n");

    Lizzie.frame.hasEnginePkTitile = true;
    Lizzie.frame.enginePkTitile =
        resourceBundle.getString("EngineGameInfo.titleScore") //  " 比分 "
            + engineGameInfo.getFirstEngineWins()
            + ":"
            + engineGameInfo.getSecondEngineWins()
            + " "
            + engineList.get(engineGameInfo.firstEngineIndex).oriEnginename
            + " VS "
            + engineList.get(engineGameInfo.secondEngineIndex).oriEnginename
            + resourceBundle.getString("EngineGameInfo.titleWinRate") //  + " 胜率 "
            + String.format(Locale.ENGLISH, "%.1f", wr * 100)
            + "%"
            + " 2σ "
            + String.format(Locale.ENGLISH, "%.2f", zxwr2 * 100)
            + "%±"
            + String.format(Locale.ENGLISH, "%.2f", zxwrc2 * 100)
            + "%"; // +" 3σ "+ String.format(Locale.ENGLISH,"%.2f", zxwr3*100)+"%±"+
    // String.format(Locale.ENGLISH,"%.2f",
    // zxwrc3*100)+"%";

    if (Lizzie.config.chkEngineSgfStart) {
      writer.write("\r\n");
      writer.write(
          resourceBundle.getString("EngineGameInfo.sgfStartNumber") // "使用开局SGF集: 是    开局SGF数量: "
              + Lizzie.frame.enginePKSgfString.size());
      writer.write("\r\n");
      for (SgfWinLossList wl : Lizzie.frame.enginePkSgfWinLoss) {
        writer.write(
            resourceBundle.getString("EngineGameInfo.sgfStartOpen")
                + (Lizzie.config.isChinese ? "" : " ") //  "开局"
                + wl.SgfNumber
                + ":\n"
                + resourceBundle.getString("EngineGameInfo.engine1")
                + ": "
                + resourceBundle.getString("EngineGameInfo.allWins") // "引擎1:总胜局 "
                + wl.engineOneWins
                + resourceBundle.getString("EngineGameInfo.sgfStartBlackWin") // "  执黑胜局 "
                + wl.engineOneWinsAsBlack
                + resourceBundle.getString("EngineGameInfo.sgfStartWhiteWin") // "  执白胜局"
                + wl.engineOneWinsAsWhite
                + "   "
                + resourceBundle.getString("EngineGameInfo.engine2")
                + ": "
                + resourceBundle.getString("EngineGameInfo.allWins") // "\n引擎2:总胜局 "
                + wl.engineTwoWins
                + resourceBundle.getString("EngineGameInfo.sgfStartBlackWin") // "  执黑胜局 "
                + wl.engineTwoWinsAsBlack
                + resourceBundle.getString("EngineGameInfo.sgfStartWhiteWin") // "  执白胜局"
                + wl.engineTwoWinsAsWhite);
        writer.write("\r\n");
      }
    }
    try {
      writer.flush();
      writer.close();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  private void savePkTxt(
      String settingB,
      String settingW,
      String settingAll,
      String resultB,
      String resultW,
      String resultOther) {
    File file = new File("");
    String courseFile = "";
    try {
      courseFile = file.getCanonicalPath();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    // 增加如果已命名,则保存在命名的文件夹下
    File autoSaveFile;
    File autoSaveFile2 = null;
    autoSaveFile =
        new File(
            courseFile
                + File.separator
                + "EngineGames"
                + File.separator
                + engineGameInfo.batchGameName
                + File.separator
                + resourceBundle.getString("Leelaz.result")
                + engineGameInfo.SF
                + ".txt");
    autoSaveFile2 =
        new File(
            courseFile
                + File.separator
                + "EngineGames"
                + File.separator
                + engineGameInfo.SF
                + File.separator
                + resourceBundle.getString("Leelaz.result")
                + engineGameInfo.SF
                + ".txt");

    File fileParent = autoSaveFile.getParentFile();
    if (!fileParent.exists()) {
      fileParent.mkdirs();
    }
    try {
      writeToFile(autoSaveFile, settingAll, settingB, settingW, resultB, resultW, resultOther);
    } catch (IOException e) {
      // TODO Auto-generated catch block

      try {
        File fileParent2 = autoSaveFile2.getParentFile();
        if (!fileParent2.exists()) {
          fileParent2.mkdirs();
        }
        writeToFile(autoSaveFile2, settingAll, settingB, settingW, resultB, resultW, resultOther);

      } catch (IOException e1) {
        // TODO Auto-generated catch block
        e1.printStackTrace();
      }
      e.printStackTrace();
    }
  }

  public void stopEngineGame(int resgnEngineIndex, boolean mannul) {
    SGFParser.appendComment();
    isPreEngineGame = false;
    if (!isEngineGame) return;
    isEngineGame = false;
    isSaveingEngineSGF = true;
    stopCountDown();
    LizzieFrame.menu.toggleDoubleMenuGameStatus();
    LizzieFrame.toolbar.isPkStop = false;
    // 保存SGF文件
    if (mannul) {
      engineList.get(engineGameInfo.blackEngineIndex).notPondering();
      engineList.get(engineGameInfo.blackEngineIndex).nameCmd();
      engineList.get(engineGameInfo.whiteEngineIndex).notPondering();
      engineList.get(engineGameInfo.whiteEngineIndex).nameCmd();
      engineList.get(engineGameInfo.blackEngineIndex).played = false;
      engineList.get(engineGameInfo.whiteEngineIndex).played = false;
      changeEngIcoForEndPk();
      LizzieFrame.toolbar.enableDisabelForEngineGame(true);
      Lizzie.frame.addInput(true);
      if (engineGameInfo.isBatchGame && engineGameInfo.batchNumberCurrent > 1) {
        File file = new File("");
        String courseFile = "";
        try {
          courseFile = file.getCanonicalPath();
        } catch (IOException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
        String passandMove = "";
        if (engineGameInfo.doublePassGame > 0)
          passandMove =
              resourceBundle.getString("EngineGameInfo.doublePassGame")
                  + engineGameInfo.doublePassGame;
        if (engineGameInfo.maxMoveGame > 0)
          passandMove +=
              (passandMove.equals("") ? "" : " ")
                  + resourceBundle.getString("EngineGameInfo.outOfMoveGame")
                  + engineGameInfo.maxMoveGame;
        Utils.showMsgNoModal(
            (resourceBundle.getString("EngineGameInfo.batchGameEndAndScore")
                + engineList.get(engineGameInfo.firstEngineIndex).oriEnginename
                + "   "
                + engineGameInfo.getFirstEngineWins()
                + ":"
                + engineGameInfo.getSecondEngineWins()
                + "   "
                + engineList.get(engineGameInfo.secondEngineIndex).oriEnginename
                + (passandMove.equals("") ? "" : " ")
                + passandMove
                + ","
                + resourceBundle.getString("EngineGameInfo.engineGameEndHintKifuPos")
                + courseFile
                + File.separator
                + "EngineGames"));
      }
      isSaveingEngineSGF = false;
      return;
    }
    SGFParser.appendGameTimeAndPlayouts();
    if (engineGameInfo.isBatchGame || LizzieFrame.toolbar.AutosavePk) {
      saveEngineGameFile(resgnEngineIndex);
    }
    if (engineGameInfo.isBatchGame) {
      if (engineList.get(resgnEngineIndex).doublePass) {
        engineGameInfo.doublePassGame++;
      } else if (engineList.get(resgnEngineIndex).outOfMoveNum) {
        engineGameInfo.maxMoveGame++;
      } else {
        if (resgnEngineIndex == engineGameInfo.firstEngineIndex) {
          if (resgnEngineIndex == engineGameInfo.blackEngineIndex) {
            engineGameInfo.secondEngineWinAsWhite++;
            for (SgfWinLossList wl : engineGameInfo.engineGameSgfWinLoss) {
              if (wl.SgfNumber == LizzieFrame.toolbar.currentEnginePkSgfNum) {
                wl.engineTwoWins++;
                wl.engineTwoWinsAsWhite++;
                break;
              }
            }
          } else {
            engineGameInfo.secondEngineWinAsBlack++;
            for (SgfWinLossList wl : engineGameInfo.engineGameSgfWinLoss) {
              if (wl.SgfNumber == LizzieFrame.toolbar.currentEnginePkSgfNum) {
                wl.engineTwoWins++;
                wl.engineTwoWinsAsBlack++;
                break;
              }
            }
          }
        } else {
          if (resgnEngineIndex == engineGameInfo.blackEngineIndex) {
            engineGameInfo.firstEngineWinAsWhite++;
            for (SgfWinLossList wl : engineGameInfo.engineGameSgfWinLoss) {
              if (wl.SgfNumber == LizzieFrame.toolbar.currentEnginePkSgfNum) {
                wl.engineOneWins++;
                wl.engineOneWinsAsWhite++;
                break;
              }
            }
          } else {
            engineGameInfo.firstEngineWinAsBlack++;
            for (SgfWinLossList wl : engineGameInfo.engineGameSgfWinLoss) {
              if (wl.SgfNumber == LizzieFrame.toolbar.currentEnginePkSgfNum) {
                wl.engineOneWins++;
                wl.engineOneWinsAsBlack++;
                break;
              }
            }
          }
        }
      }
      // 保存对局结果txt

      // resultOther, resultFirst, resultSecond;
      engineGameInfo.resultFirst =
          resourceBundle.getString("EngineGameInfo.engine1")
              + "("
              + engineList.get(engineGameInfo.firstEngineIndex).oriEnginename
              + "):\n"
              + resourceBundle.getString("EngineGameInfo.allWins")
              + ": "
              + engineGameInfo.getFirstEngineWins();
      engineGameInfo.resultFirst +=
          " "
              + resourceBundle.getString("EngineGameInfo.sgfStartBlackWin")
              + ": "
              + engineGameInfo.firstEngineWinAsBlack
              + " "
              + resourceBundle.getString("EngineGameInfo.sgfStartWhiteWin")
              + ": "
              + engineGameInfo.firstEngineWinAsWhite;
      engineGameInfo.resultFirst +=
          resourceBundle.getString("EngineGameInfo.totalTime")
              + engineGameInfo.firstEngineTotleTime / (float) 1000
              + resourceBundle.getString("SGFParse.seconds");
      engineGameInfo.resultFirst +=
          resourceBundle.getString("EngineGameInfo.result.totalVisits")
              + engineGameInfo.firstEngineTotlePlayouts;

      engineGameInfo.resultSecond =
          resourceBundle.getString("EngineGameInfo.engine2")
              + "("
              + engineList.get(engineGameInfo.secondEngineIndex).oriEnginename
              + "):\n"
              + resourceBundle.getString("EngineGameInfo.allWins")
              + ": "
              + engineGameInfo.getSecondEngineWins();
      engineGameInfo.resultSecond +=
          " "
              + resourceBundle.getString("EngineGameInfo.sgfStartBlackWin")
              + ": "
              + engineGameInfo.secondEngineWinAsBlack
              + " "
              + resourceBundle.getString("EngineGameInfo.sgfStartWhiteWin")
              + ": "
              + engineGameInfo.secondEngineWinAsWhite;
      engineGameInfo.resultSecond +=
          resourceBundle.getString("EngineGameInfo.totalTime")
              + engineGameInfo.secondEngineTotleTime / (float) 1000
              + resourceBundle.getString("SGFParse.seconds");
      engineGameInfo.resultSecond +=
          resourceBundle.getString("EngineGameInfo.result.totalVisits")
              + engineGameInfo.secondEngineTotlePlayouts;

      engineGameInfo.resultOther =
          resourceBundle.getString("EngineGameInfo.doublePassGame") + engineGameInfo.doublePassGame;
      engineGameInfo.resultOther +=
          " "
              + resourceBundle.getString("EngineGameInfo.outOfMoveGame")
              + engineGameInfo.maxMoveGame;
      if (engineGameInfo.isGenmove) {
        engineGameInfo.settingAll =
            resourceBundle.getString("EngineGameInfo.otherSettings")
                + resourceBundle.getString("EngineGameInfo.genmoveMode");
        engineGameInfo.settingAll +=
            resourceBundle.getString("EngineGameInfo.komi")
                + +Lizzie.board.getHistory().getGameInfo().getKomi();

        if (engineGameInfo.isBatchGame) {
          engineGameInfo.settingAll +=
              resourceBundle.getString("EngineGameInfo.totalGames") + engineGameInfo.batchNumber;
        }
        if (engineGameInfo.isContinueGame) {
          engineGameInfo.settingAll +=
              resourceBundle.getString("EngineGameInfo.continueGame")
                  + resourceBundle.getString("EngineGameInfo.yes");
        } else {
          engineGameInfo.settingAll +=
              resourceBundle.getString("EngineGameInfo.continueGame")
                  + resourceBundle.getString("EngineGameInfo.no");
        }
        if (engineGameInfo.isExchange) {
          engineGameInfo.settingAll +=
              resourceBundle.getString("EngineGameInfo.exchange")
                  + resourceBundle.getString("EngineGameInfo.yes"); // " 交换黑白: 是";
        } else {
          engineGameInfo.settingAll +=
              resourceBundle.getString("EngineGameInfo.exchange")
                  + resourceBundle.getString("EngineGameInfo.no"); // " 交换黑白: 否";
        }

        engineGameInfo.settingAll +=
            resourceBundle.getString("EngineGameInfo.maxMoves") + engineGameInfo.maxGameMoves;
      } else {
        engineGameInfo.settingAll =
            resourceBundle.getString("EngineGameInfo.otherSettings")
                + resourceBundle.getString("EngineGameInfo.analyzeMode");
        engineGameInfo.settingAll +=
            resourceBundle.getString("EngineGameInfo.komi")
                + Lizzie.board.getHistory().getGameInfo().getKomi();
        //      engineGameInfo.settingAll +=
        //          " 认输阈值:连续"
        //              + Lizzie.frame.toolbar.pkResignMoveCounts
        //              + "手,胜率低于"
        //              + Lizzie.frame.toolbar.pkResginWinrate
        //              + "%";
        if (engineGameInfo.isBatchGame) {
          engineGameInfo.settingAll +=
              resourceBundle.getString("EngineGameInfo.totalGames") + engineGameInfo.batchNumber;
        }
        if (engineGameInfo.isContinueGame) {
          engineGameInfo.settingAll +=
              resourceBundle.getString("EngineGameInfo.continueGame")
                  + resourceBundle.getString("EngineGameInfo.yes"); // " 续弈: 是";
        } else {
          engineGameInfo.settingAll +=
              resourceBundle.getString("EngineGameInfo.continueGame")
                  + resourceBundle.getString("EngineGameInfo.no"); // " 续弈: 否";
        }
        if (engineGameInfo.isExchange) {
          engineGameInfo.settingAll +=
              resourceBundle.getString("EngineGameInfo.exchange")
                  + resourceBundle.getString("EngineGameInfo.yes"); // " 交换黑白: 是";
        } else {
          engineGameInfo.settingAll +=
              resourceBundle.getString("EngineGameInfo.exchange")
                  + resourceBundle.getString("EngineGameInfo.no"); // " 交换黑白: 否";
        }

        engineGameInfo.settingAll +=
            resourceBundle.getString("EngineGameInfo.maxMoves") + engineGameInfo.maxGameMoves;

        if (LizzieFrame.toolbar.isRandomMove) {
          engineGameInfo.settingAll +=
              resourceBundle.getString("EngineGameInfo.randomPlay1") //    " 随机落子: 前"
                  + LizzieFrame.toolbar.randomMove
                  + resourceBundle.getString("EngineGameInfo.randomPlay2") // "手,胜率不低于首位"
                  + LizzieFrame.toolbar.randomDiffWinrate
                  + "%";
          if (Lizzie.config.checkRandomVisits)
            engineGameInfo.settingAll +=
                resourceBundle.getString("EngineGameInfo.randomPlay3") // ",计算量不低于最高值"
                    + String.format(Locale.ENGLISH, "%.1f", Lizzie.config.percentsRandomVisits)
                    + "%";
        }
      }
      savePkTxt(
          engineGameInfo.settingFirst,
          engineGameInfo.settingSecond,
          engineGameInfo.settingAll,
          engineGameInfo.resultFirst,
          engineGameInfo.resultSecond,
          engineGameInfo.resultOther);

      if (engineGameInfo.batchNumberCurrent < engineGameInfo.batchNumber) {
        engineGameInfo.batchNumberCurrent++;
        if (engineGameInfo.isExchange) engineGameInfo.exChangeBlackWhite();
        isSaveingEngineSGF = false;
        startNewEngineGame(false);
        return;
      }
    }
    LizzieFrame.toolbar.enableDisabelForEngineGame(true);
    Lizzie.board.clearBestMovesAfter(Lizzie.board.getHistory().getStart());
    File file = new File("");
    String courseFile = "";
    try {
      courseFile = file.getCanonicalPath();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    // showmsg 多局
    if (engineGameInfo.isBatchGame) {
      String passandMove = "";
      if (engineGameInfo.doublePassGame > 0)
        passandMove =
            passandMove
                + resourceBundle.getString("EngineGameInfo.doublePassGame")
                + engineGameInfo.doublePassGame;
      if (engineGameInfo.maxMoveGame > 0)
        passandMove =
            passandMove
                + (passandMove.equals("") ? "" : " ")
                + resourceBundle.getString("EngineGameInfo.outOfMoveGame")
                + engineGameInfo.maxMoveGame;
      Utils.showMsgNoModal(
          (resourceBundle.getString("EngineGameInfo.batchGameEndAndScore")
              + engineList.get(engineGameInfo.firstEngineIndex).oriEnginename
              + "   "
              + engineGameInfo.getFirstEngineWins()
              + ":"
              + engineGameInfo.getSecondEngineWins()
              + "   "
              + engineList.get(engineGameInfo.secondEngineIndex).oriEnginename
              + (passandMove.equals("") ? "" : " ")
              + passandMove
              + ","
              + resourceBundle.getString("EngineGameInfo.engineGameEndHintKifuPos")
              + courseFile
              + File.separator
              + "EngineGames"));
    } else {
      // 单局
      String jg = resourceBundle.getString("EngineGameInfo.gameFinished"); // "对战已结束，";
      if (engineList.get(resgnEngineIndex).outOfMoveNum)
        jg = jg + resourceBundle.getString("EngineGameInfo.finishedByMoves"); // "超过手数限制";
      else {
        if (engineList.get(resgnEngineIndex).doublePass) {
          jg = jg + resourceBundle.getString("EngineGameInfo.finishedByDoublePass"); // "双Pass对局";
        } else if (resgnEngineIndex == engineGameInfo.blackEngineIndex) {
          // df=df+"_白胜";
          jg =
              jg
                  + resourceBundle.getString("GameInfoDialog.white")
                  + "("
                  + engineList.get(engineGameInfo.whiteEngineIndex).oriEnginename
                  + ")"
                  + resourceBundle.getString("EngineGameInfo.finishedWin");
        } else {
          jg =
              jg
                  + resourceBundle.getString("GameInfoDialog.black")
                  + "("
                  + engineList.get(engineGameInfo.blackEngineIndex).oriEnginename
                  + ")"
                  + resourceBundle.getString("EngineGameInfo.finishedWin");
        }
      }
      if (LizzieFrame.toolbar.AutosavePk) {
        jg =
            jg
                + ","
                + resourceBundle.getString("EngineGameInfo.engineGameEndHintKifuPos")
                + courseFile
                + File.separator
                + "EngineGames";
      }
      Utils.showMsgNoModal(jg);
    }
    isSaveingEngineSGF = false;
    engineList.get(engineGameInfo.blackEngineIndex).notPondering();
    engineList.get(engineGameInfo.blackEngineIndex).nameCmd();
    engineList.get(engineGameInfo.whiteEngineIndex).notPondering();
    engineList.get(engineGameInfo.whiteEngineIndex).nameCmd();
    engineList.get(engineGameInfo.blackEngineIndex).played = false;
    engineList.get(engineGameInfo.whiteEngineIndex).played = false;
    Lizzie.frame.addInput(true);
    changeEngIcoForEndPk();
  }

  public void startNewEngineGame(boolean firstTime) {
    // engineGameInfo
    Lizzie.frame.setResult("");
    isPreEngineGame = true;
    if (firstTime) {
      killOtherEngines(engineGameInfo.blackEngineIndex, engineGameInfo.whiteEngineIndex);
      Lizzie.leelaz.notPondering();
      if (currentEngineNo == engineGameInfo.blackEngineIndex
          || currentEngineNo == engineGameInfo.whiteEngineIndex) {
        Lizzie.leelaz.nameCmd();
        Lizzie.leelaz.clearBestMoves();
      } else {
        if (!isEmpty) {
          try {
            Lizzie.leelaz.normalQuit();
          } catch (Exception ex) {
          }
        }
      }
    }
    if (!engineGameInfo.isGenmove) {
      // 分析模式对战
      Lizzie.board.clear(true);
      ArrayList<Movelist> startList = getStartListForEnginePk();
      if (startList != null) {
        Lizzie.board.setMoveList(startList, false, true);
      }
      if (!firstTime) {
        engineList.get(engineGameInfo.blackEngineIndex).notPondering();
        engineList.get(engineGameInfo.blackEngineIndex).clear();
        engineList.get(engineGameInfo.whiteEngineIndex).notPondering();
        engineList.get(engineGameInfo.whiteEngineIndex).clear();
      } else if (startList == null) {
        int width = engineList.get(engineGameInfo.blackEngineIndex).width;
        int height = engineList.get(engineGameInfo.blackEngineIndex).height;
        if (width != Board.boardWidth || height != Board.boardHeight)
          Lizzie.board.reopen(width, height);
      }
      startEngineForPk(engineGameInfo.blackEngineIndex);
      startEngineForPk(engineGameInfo.whiteEngineIndex);
      Runnable runnable =
          new Runnable() {
            public void run() {
              while (!engineList.get(engineGameInfo.blackEngineIndex).isLoaded()
                  || !engineList.get(engineGameInfo.whiteEngineIndex).isLoaded()) {
                try {
                  Thread.sleep(500);
                } catch (InterruptedException e) {
                  // TODO Auto-generated catch block
                  e.printStackTrace();
                }
              }
              if (startList != null) {
                try {
                  Thread.sleep(1000);
                } catch (InterruptedException e) {
                  // TODO Auto-generated catch block
                  e.printStackTrace();
                }
              }
              Lizzie.frame.reSetLoc();
              Lizzie.frame.clearWRNforGame(false);
              if (Lizzie.config.autoLoadLzsaiEngineVisits) {
                Lizzie.engineManager
                    .engineList
                    .get(engineGameInfo.blackEngineIndex)
                    .sendCommand("lz-setoption name Visits value 1000000000");
                Lizzie.engineManager
                    .engineList
                    .get(engineGameInfo.whiteEngineIndex)
                    .sendCommand("lz-setoption name Visits value 1000000000");
              }
              Lizzie.engineManager
                  .engineList
                  .get(engineGameInfo.blackEngineIndex)
                  .sendCommand("clear_cache");
              Lizzie.engineManager
                  .engineList
                  .get(engineGameInfo.whiteEngineIndex)
                  .sendCommand("clear_cache");
              if (firstTime) {
                if (engineList.get(engineGameInfo.firstEngineIndex).isKatago) {
                  if (!engineList.get(engineGameInfo.firstEngineIndex).recentRulesLine.equals("")
                      && engineList.get(engineGameInfo.firstEngineIndex).recentRulesLine.length()
                          > 2) {
                    engineGameInfo.settingFirst +=
                        "\r\n"
                            + resourceBundle.getString("EngineGameInfo.rules")
                            + ": "
                            + new String(
                                engineList
                                    .get(engineGameInfo.firstEngineIndex)
                                    .recentRulesLine
                                    .substring(2));
                  }
                }

                if (engineList.get(engineGameInfo.secondEngineIndex).isKatago) {
                  if (!engineList.get(engineGameInfo.secondEngineIndex).recentRulesLine.equals("")
                      && engineList.get(engineGameInfo.secondEngineIndex).recentRulesLine.length()
                          > 2) {
                    engineGameInfo.settingSecond +=
                        "\r\n"
                            + resourceBundle.getString("EngineGameInfo.rules")
                            + ": "
                            + new String(
                                engineList
                                    .get(engineGameInfo.secondEngineIndex)
                                    .recentRulesLine
                                    .substring(2));
                  }
                }
              }
              if (Lizzie.board.getHistory().isBlacksTurn()) {
                Lizzie.leelaz = engineList.get(engineGameInfo.blackEngineIndex);
              } else {
                Lizzie.leelaz = engineList.get(engineGameInfo.whiteEngineIndex);
              }
              if (Lizzie.config.newEngineGameHandicap > 0) {
                Lizzie.board.hasStartStone = true;
                Lizzie.board.addStartListAll();
                Lizzie.board.flatten();
              }
              int cmdNumberTemp = Lizzie.leelaz.cmdNumber;
              Runnable runnable1 =
                  new Runnable() {
                    public void run() {
                      while (!Lizzie.leelaz.isResponseUpToDate()) {
                        try {
                          Thread.sleep(100);
                        } catch (InterruptedException e) {
                          // TODO Auto-generated catch block
                          e.printStackTrace();
                        }
                      }
                      Lizzie.leelaz.ponder();
                      Lizzie.leelaz.clearBestMoves();
                    }
                  };
              Thread thread1 = new Thread(runnable1);
              thread1.start();

              Runnable runnable =
                  new Runnable() {
                    public void run() {
                      while (Lizzie.leelaz.cmdNumber == cmdNumberTemp) {
                        try {
                          Thread.sleep(100);
                        } catch (InterruptedException e) {
                          // TODO Auto-generated catch block
                          e.printStackTrace();
                        }
                      }
                      isEngineGame = true;
                      isPreEngineGame = false;
                      Lizzie.leelaz.played = false;
                      setInfoAfterEngineGame();
                      if (firstTime) {
                        Lizzie.frame.resetMovelistFrameandAnalysisFrame();
                        LizzieFrame.menu.updateMenuStatusForEngine();
                      }
                    }
                  };
              Thread thread = new Thread(runnable);
              thread.start();
            }
          };
      Thread thread = new Thread(runnable);
      thread.start();
    } else {
      // genmove对战
      if (engineList.get(engineGameInfo.blackEngineIndex) != null) {
        engineList.get(engineGameInfo.blackEngineIndex).clearBestMoves();
      }
      if (engineList.get(engineGameInfo.whiteEngineIndex) != null) {
        engineList.get(engineGameInfo.whiteEngineIndex).clearBestMoves();
      }
      Lizzie.board.clear(true);
      ArrayList<Movelist> startList = getStartListForEnginePk();
      if (startList != null) {
        Lizzie.board.setlist(startList);
      } else if (firstTime) {
        int width = engineList.get(engineGameInfo.blackEngineIndex).width;
        int height = engineList.get(engineGameInfo.blackEngineIndex).height;
        if (width != Board.boardWidth || height != Board.boardHeight)
          Lizzie.board.reopen(width, height);
      }
      startEngineForPk(engineGameInfo.blackEngineIndex);
      startEngineForPk(engineGameInfo.whiteEngineIndex);
      Runnable runnable =
          new Runnable() {
            public void run() {
              while (!engineList.get(engineGameInfo.blackEngineIndex).isLoaded()
                  || !engineList.get(engineGameInfo.whiteEngineIndex).isLoaded()) {
                try {
                  Thread.sleep(500);
                } catch (InterruptedException e) {
                  // TODO Auto-generated catch block
                  e.printStackTrace();
                }
              }
              Lizzie.frame.reSetLoc();
              Lizzie.frame.clearWRNforGame(true);
              isEngineGame = true;
              isPreEngineGame = false;
              engineList.get(engineGameInfo.blackEngineIndex).nameCmd();
              engineList.get(engineGameInfo.blackEngineIndex).notPondering();
              engineList.get(engineGameInfo.whiteEngineIndex).nameCmd();
              engineList.get(engineGameInfo.whiteEngineIndex).notPondering();

              if (Lizzie.config.pkAdvanceTimeSettings) {
                Lizzie.engineManager
                    .engineList
                    .get(engineGameInfo.blackEngineIndex)
                    .sendCommand(Lizzie.config.advanceBlackTimeTxt);
                Lizzie.engineManager
                    .engineList
                    .get(engineGameInfo.whiteEngineIndex)
                    .sendCommand(Lizzie.config.advanceWhiteTimeTxt);
                if (firstEngineCountDown != null || secondEngineCountDown != null) {
                  if (firstEngineCountDown != null)
                    firstEngineCountDown.initialize(engineGameInfo.isFirstEnginePlayBlack());
                  if (secondEngineCountDown != null)
                    secondEngineCountDown.initialize(!engineGameInfo.isFirstEnginePlayBlack());
                  StartCountDown();
                }
              } else {
                if (engineGameInfo.timeWhite > 0)
                  Lizzie.engineManager
                      .engineList
                      .get(engineGameInfo.whiteEngineIndex)
                      .sendCommand("time_settings 0 " + engineGameInfo.timeWhite + " 1");
                if (engineGameInfo.timeBlack > 0)
                  Lizzie.engineManager
                      .engineList
                      .get(engineGameInfo.blackEngineIndex)
                      .sendCommand("time_settings 0 " + engineGameInfo.timeBlack + " 1");
              }
              Lizzie.engineManager
                  .engineList
                  .get(engineGameInfo.blackEngineIndex)
                  .sendCommand("clear_cache");
              Lizzie.engineManager
                  .engineList
                  .get(engineGameInfo.whiteEngineIndex)
                  .sendCommand("clear_cache");
              if (startList != null) {
                if (Lizzie.config.newEngineGameHandicap > 0) {
                  Lizzie.board.hasStartStone = true;
                  Lizzie.board.addStartListAll();
                  Lizzie.board.flatten();
                }
                try {
                  Thread.sleep(1000);
                } catch (InterruptedException e) {
                  // TODO Auto-generated catch block
                  e.printStackTrace();
                }
              }
              if (Lizzie.board.getHistory().isBlacksTurn()) {
                Lizzie.leelaz = engineList.get(engineGameInfo.blackEngineIndex);
                Lizzie.leelaz.genmoveForPk("b");
                Lizzie.leelaz = engineList.get(engineGameInfo.whiteEngineIndex);
              } else {
                Lizzie.leelaz = engineList.get(engineGameInfo.whiteEngineIndex);
                Lizzie.leelaz.genmoveForPk("w");
                Lizzie.leelaz = engineList.get(engineGameInfo.blackEngineIndex);
              }
              setInfoAfterEngineGame();
              if (firstTime) {
                Lizzie.frame.resetMovelistFrameandAnalysisFrame();
                LizzieFrame.menu.updateMenuStatusForEngine();
                if (engineList.get(engineGameInfo.firstEngineIndex).isKatago) {
                  if (!engineList.get(engineGameInfo.firstEngineIndex).recentRulesLine.equals("")
                      && engineList.get(engineGameInfo.firstEngineIndex).recentRulesLine.length()
                          > 2) {
                    engineGameInfo.settingFirst +=
                        "\r\n"
                            + resourceBundle.getString("EngineGameInfo.rules")
                            + ": "
                            + new String(
                                engineList
                                    .get(engineGameInfo.firstEngineIndex)
                                    .recentRulesLine
                                    .substring(2));
                  }
                }

                if (engineList.get(engineGameInfo.secondEngineIndex).isKatago) {
                  if (!engineList.get(engineGameInfo.secondEngineIndex).recentRulesLine.equals("")
                      && engineList.get(engineGameInfo.secondEngineIndex).recentRulesLine.length()
                          > 2) {
                    engineGameInfo.settingSecond +=
                        "\r\n"
                            + resourceBundle.getString("EngineGameInfo.rules")
                            + ": "
                            + new String(
                                engineList
                                    .get(engineGameInfo.secondEngineIndex)
                                    .recentRulesLine
                                    .substring(2));
                  }
                }
              }
            }
          };
      Thread thread = new Thread(runnable);
      thread.start();
    }
  }

  private void setInfoAfterEngineGame() {
    Lizzie.frame.setPlayers(
        engineList.get(engineGameInfo.whiteEngineIndex).oriEnginename,
        engineList.get(engineGameInfo.blackEngineIndex).oriEnginename);
    GameInfo gameInfo = Lizzie.board.getHistory().getGameInfo();
    gameInfo.setPlayerWhite(engineList.get(engineGameInfo.whiteEngineIndex).oriEnginename);
    gameInfo.setPlayerBlack(engineList.get(engineGameInfo.blackEngineIndex).oriEnginename);
    Lizzie.frame.updateTitle();
    LizzieFrame.menu.toggleDoubleMenuGameStatus();
  }

  //  private void checkEngineNotHang() {
  //    if (isEngineGame
  //        && !Lizzie.frame.toolbar.isGenmoveToolbar
  //        && !Lizzie.frame.toolbar.isPkStop
  //        && System.currentTimeMillis() - startInfoTime > 1000 * 240) {
  //      Lizzie.leelaz.process.destroy();
  //      Lizzie.gtpConsole.addLine("EnginePkHangs");
  //      startInfoTime = System.currentTimeMillis();
  //    }
  //    //    try {
  //    //      timer3.stop();
  //    //      // timer3 = null;
  //    //    } catch (Exception ex) {
  //    //
  //    //    }
  //  }

  private void checkEngineAlive() {
    if (isEmpty) return;
    if (!isEngineGame && Lizzie.leelaz != null) {
      if (Lizzie.leelaz.isLoaded() && Lizzie.leelaz.canCheckAlive && Lizzie.leelaz.isProcessDead())
        try {
          Lizzie.leelaz.restartClosedEngine(currentEngineNo);
        } catch (IOException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
      if (Lizzie.leelaz.useJavaSSH && Lizzie.leelaz.isLoaded() && Lizzie.leelaz.canCheckAlive) {
        if (Lizzie.leelaz.javaSSHClosed)
          try {
            Lizzie.leelaz.restartClosedEngine(currentEngineNo);
          } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
          }
      }
    }
    //   if (isEngineGame) {
    //    {
    // checkEngineNotHang();
    checkEnginePK();
    // if (Lizzie.leelaz.resigned) Lizzie.leelaz.pkResign();
    //        if (Lizzie.leelaz.isPondering() && (timer3 == null || !timer3.isRunning())) {
    //          timer3 =
    //              new Timer(
    //                  5000,
    //                  new ActionListener() {
    //                    public void actionPerformed(ActionEvent evt) {
    //
    //
    //                      try {
    //                      } catch (Exception e) {
    //                      }
    //                    }
    //                  });
    //          timer3.start();
    //        }
    //      }
    //      if ((timer2 == null || !timer2.isRunning())) {
    //        timer2 =
    //            new Timer(
    //                20000,
    //                new ActionListener() {
    //                  public void actionPerformed(ActionEvent evt) {
    //                    checkEnginePK();
    //                    try {
    //                    } catch (Exception e) {
    //                    }
    //                  }
    //                });
    //        timer2.start();
    //    }
    //   }
  }

  private void checkEnginePK() {
    if (!isEngineGame) {
      return;
    }
    if (engineList.get(engineGameInfo.firstEngineIndex).canCheckAlive
        && ((engineList.get(engineGameInfo.firstEngineIndex).isProcessDead())
            || (engineList.get(engineGameInfo.firstEngineIndex).useJavaSSH
                && engineList.get(engineGameInfo.firstEngineIndex).javaSSHClosed))) {
      try {
        restartEngineForPk(engineGameInfo.firstEngineIndex);
      } catch (Exception e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }
    if (engineList.get(engineGameInfo.secondEngineIndex).canCheckAlive
        && ((engineList.get(engineGameInfo.secondEngineIndex).isProcessDead())
            || (engineList.get(engineGameInfo.secondEngineIndex).useJavaSSH
                && engineList.get(engineGameInfo.secondEngineIndex).javaSSHClosed))) {
      try {
        restartEngineForPk(engineGameInfo.secondEngineIndex);
      } catch (Exception e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }
    //    try {
    //      timer2.stop();
    //      // timer2 = null;
    //    } catch (Exception ex) {
    //
    //    }
  }

  public void updateEngines() {
    isUpdating = true;
    ArrayList<EngineData> engineData = Utils.getEngineData();
    for (int i = 0; i < engineData.size(); i++) {
      EngineData engineDt = engineData.get(i);
      if (i < engineList.size()) {
        boolean changedLeelazStatus = false;
        if (!this.engineList.get(i).oriEngineCommand.equals(engineDt.commands))
          changedLeelazStatus = true;
        if (engineList.get(i).useJavaSSH != engineDt.useJavaSSH) changedLeelazStatus = true;
        if (!this.engineList.get(i).ip.equals(engineDt.ip)) changedLeelazStatus = true;
        if (!this.engineList.get(i).port.equals(engineDt.port)) changedLeelazStatus = true;
        if (engineList.get(i).useKeyGen != engineDt.useKeyGen) changedLeelazStatus = true;
        if (!this.engineList.get(i).keyGenPath.equals(engineDt.keyGenPath))
          changedLeelazStatus = true;
        if (!this.engineList.get(i).userName.equals(engineDt.userName)) changedLeelazStatus = true;
        if (!this.engineList.get(i).password.equals(engineDt.password)) changedLeelazStatus = true;
        if (!this.engineList.get(i).initialCommand.equals(engineDt.initialCommand))
          changedLeelazStatus = true;
        engineList.get(i).setEngineCommand(engineDt.commands);
        engineList.get(i).width = engineDt.width;
        engineList.get(i).height = engineDt.height;
        engineList.get(i).oriWidth = engineDt.width;
        engineList.get(i).oriHeight = engineDt.height;
        engineList.get(i).komi = engineDt.komi;
        engineList.get(i).isKatago = false;
        engineList.get(i).isKatagoCustom = false;
        engineList.get(i).orikomi = engineDt.komi;
        engineList.get(i).isSai = false;
        engineList.get(i).getEngineName(i);
        engineList.get(i).useJavaSSH = engineDt.useJavaSSH;
        engineList.get(i).ip = engineDt.ip;
        engineList.get(i).port = engineDt.port;
        engineList.get(i).useKeyGen = engineDt.useKeyGen;
        engineList.get(i).keyGenPath = engineDt.keyGenPath;
        engineList.get(i).userName = engineDt.userName;
        engineList.get(i).password = engineDt.password;
        engineList.get(i).initialCommand = engineDt.initialCommand;
        if (!isEmpty && Lizzie.leelaz != null && engineList.get(i) == Lizzie.leelaz) {
          if (changedLeelazStatus) {
            engineList.get(i).isKatago = false;
            engineList.get(i).isSai = false;
            engineList.get(i).isZen = false;
            engineList.get(i).noAnalyze = false;
            reStartEngine(i);
          } else {
            if (Lizzie.leelaz.oriWidth != Board.boardWidth
                || Lizzie.leelaz.oriHeight != Board.boardHeight) {
              Lizzie.board.reopen(Lizzie.leelaz.oriWidth, Lizzie.leelaz.oriHeight);
            }
            if (Lizzie.leelaz.orikomi != Lizzie.board.getHistory().getGameInfo().getKomi()
                && !Lizzie.board.getHistory().getGameInfo().changedKomi)
              Lizzie.leelaz.komi(Lizzie.leelaz.orikomi);
          }
          Lizzie.leelaz.isCheckingName = true;
          Lizzie.leelaz.nameCmd();
          Runnable startPonder =
              new Runnable() {
                public void run() {
                  while (Lizzie.leelaz.isCheckingName) {
                    try {
                      Thread.sleep(100);
                    } catch (InterruptedException e) {
                      // TODO Auto-generated catch block
                      e.printStackTrace();
                    }
                  }
                  Lizzie.leelaz.ponder();
                }
              };
          Thread startPonderTh = new Thread(startPonder);
          startPonderTh.start();
        } else if (engineList.get(i).isStarted() && changedLeelazStatus) {
          engineList.get(i).normalQuit();
          engineList.get(i).isKatago = false;
          engineList.get(i).isSai = false;
          engineList.get(i).isZen = false;
          engineList.get(i).noAnalyze = false;
          engineList.get(i).setEngineCommand(engineDt.commands);
          try {
            engineList.get(i).startEngine(i);
          } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
          }
        }
        // engineList.get(i).currentEnginename = engineDt.name;
      } else {
        Leelaz e;
        try {
          e = new Leelaz(engineDt.commands);
          e.width = engineDt.width;
          e.height = engineDt.height;
          e.oriWidth = engineDt.width;
          e.oriHeight = engineDt.height;
          e.orikomi = engineDt.komi;
          e.komi = engineDt.komi;
          e.useJavaSSH = engineDt.useJavaSSH;
          e.ip = engineDt.ip;
          e.port = engineDt.port;
          e.useKeyGen = engineDt.useKeyGen;
          e.keyGenPath = engineDt.keyGenPath;
          e.userName = engineDt.userName;
          e.password = engineDt.password;
          e.initialCommand = engineDt.initialCommand;
          engineList.add(e);
        } catch (JSONException e1) {
          // TODO Auto-generated catch block
          e1.printStackTrace();
        } catch (IOException e1) {
          // TODO Auto-generated catch block
          e1.printStackTrace();
        }
      }
    }

    int j = LizzieFrame.toolbar.enginePkBlack.getItemCount();
    LizzieFrame.toolbar.removeEngineLis();
    for (int i = 0; i < j; i++) {
      LizzieFrame.toolbar.enginePkBlack.removeItemAt(0);
      LizzieFrame.toolbar.enginePkWhite.removeItemAt(0);
    }
    for (int i = 0; i < engineData.size(); i++) {
      EngineData engineDt = engineData.get(i);
      LizzieFrame.toolbar.enginePkBlack.addItem("[" + (i + 1) + "]" + engineDt.name);
      LizzieFrame.toolbar.enginePkWhite.addItem("[" + (i + 1) + "]" + engineDt.name);
    }
    LizzieFrame.toolbar.engineBlackToolbar = 0;
    LizzieFrame.toolbar.engineWhiteToolbar = 0;
    LizzieFrame.toolbar.addEngineLis();
    LizzieFrame.menu.updateEngineMenu();
    if (!isEmpty) {
      Menu.engineMenu.setText(
          "["
              + (EngineManager.currentEngineNo > 0
                  ? EngineManager.currentEngineNo + 1
                  : engineNo + 1)
              + "]: "
              + Lizzie.leelaz.oriEnginename);
    }
    isUpdating = false;
  }

  public void killAllEngines() {
    // currentEngineNo = -1;
    for (int i = 0; i < engineList.size(); i++) {
      if (engineList.get(i).isStarted()) {
        try {
          engineList.get(i).forceQuit();
        } catch (Exception e) {
        }
      }
    }
    currentEngineNo2 = -1;
    currentEngineNo = -1;
    EngineManager.isEmpty = true;
    Lizzie.leelaz.notPondering();
    Lizzie.leelaz.isLoaded = true;
    Menu.engineMenu.setText(resourceBundle.getString("Menu.noEngine"));
    Lizzie.frame.refresh();
  }

  public void forceKillAllEngines() {
    // currentEngineNo = -1;
    for (int i = 0; i < engineList.size(); i++) {
      if (engineList.get(i).isStarted()) {
        try {
          engineList.get(i).forceQuit();
        } catch (Exception e) {
        }
      }
    }
    Lizzie.leelaz.notPondering();
  }

  public void reStartEngine() {
    // currentEngineNo = -1;
    if (isEmpty || Lizzie.leelaz == null) return;
    try {
      //  Lizzie.leelaz.normalQuit();
      Lizzie.leelaz.isNormalEnd = true;
      Lizzie.leelaz.shutdown();
      Thread.sleep(200);
      Lizzie.leelaz.started = false;
      Lizzie.leelaz.isLoaded = false;
      if (Lizzie.leelaz.isLeela0110) Lizzie.leelaz.leela0110StopPonder();

    } catch (Exception e) {
    }
    switchEngine(currentEngineNo > 0 ? currentEngineNo : engineNo, true);
  }

  public void reStartEngine(int index) {
    // currentEngineNo = -1;
    if (isEmpty || Lizzie.leelaz == null) return;
    try {
      engineList.get(index).isNormalEnd = true;
      engineList.get(index).shutdown();
      Thread.sleep(200);
      engineList.get(index).started = false;
      engineList.get(index).isLoaded = false;
      if (engineList.get(index).isLeela0110) engineList.get(index).leela0110StopPonder();

    } catch (Exception e) {
    }
    switchEngine(index, true);
  }

  public void reStartEngine2() {
    // currentEngineNo = -1;
    if (Lizzie.leelaz2 == null) return;
    try {
      Lizzie.leelaz2.isNormalEnd = true;
      Lizzie.leelaz2.shutdown();
      Thread.sleep(200);
      Lizzie.leelaz2.started = false;
      Lizzie.leelaz2.isLoaded = false;
      if (Lizzie.leelaz2.isLeela0110) Lizzie.leelaz2.leela0110StopPonder();
    } catch (Exception e) {
    }
    switchEngine(EngineManager.currentEngineNo2, false);
  }

  public void killOtherEngines() {
    for (int i = 0; i < engineList.size(); i++) {
      if (engineList.get(i).isStarted()) {
        if (engineList.get(i) != Lizzie.leelaz)
          try {
            // engineList.get(i).normalQuit();
            engineList.get(i).forceQuit();
          } catch (Exception e) {
          }
      }
    }
    currentEngineNo2 = -1;
  }

  public void killOtherEngines(int engineBlack, int engineWhite) {
    for (int i = 0; i < engineList.size(); i++) {
      if (engineList.get(i).isStarted()) {
        if (i != engineBlack && i != engineWhite) engineList.get(i).normalQuit();
      }
    }
  }

  public void killThisEngines() {
    if (engineList.get(currentEngineNo).isStarted()) {
      engineList.get(currentEngineNo).forceQuit();
    }
    currentEngineNo = -1;
    isEmpty = true;
    Lizzie.leelaz.isLoaded = true;
    Lizzie.leelaz.notPondering();
    Lizzie.leelaz.clearBestMoves();
  }

  public void killThisEngines2() {
    engineList.get(currentEngineNo2).normalQuit();
    currentEngineNo2 = -1;
    Lizzie.leelaz2.notPondering();
    Lizzie.leelaz2.clearBestMoves();
  }

  /**
   * Switch the Engine by index number
   *
   * @param index engine index
   */
  public void startEngineForPk(int index) {
    if (index > this.engineList.size()) return;
    // Lizzie.board.saveMoveNumber();
    Leelaz newEng = engineList.get(index);
    // newEng.played = false;
    //    newEng.isManualW=false;
    //    newEng.isManualB=false;
    newEng.outOfMoveNum = false;
    newEng.blackResignMoveCounts = 0;
    newEng.whiteResignMoveCounts = 0;
    newEng.doublePass = false;
    newEng.genmoveNode = 0;
    newEng.resigned = false;
    newEng.isResigning = false;
    newEng.width = Board.boardWidth;
    newEng.height = Board.boardHeight;
    newEng.pkMoveTimeGame = 0;
    newEng.notPondering();
    newEng.clearBestMoves();
    newEng.komi = (float) Lizzie.board.getHistory().getGameInfo().getKomi();
    ArrayList<Movelist> mv = Lizzie.board.getMoveList();
    if (!newEng.isStarted()) {
      try {
        newEng.startEngine(index);
      } catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    } else {
      newEng.canRestoreDymPda = false;
      newEng.boardSize(newEng.width, newEng.height);
      newEng.sendCommand("komi " + newEng.komi);
      // newEng.sendCommand("name");
      //  newEng.isCheckingName = true;
      newEng.pkMoveStartTime = System.currentTimeMillis();
    }
    // else {newEng.initializeStreams();}
    // Lizzie.leelaz = newEng;
    newEng.isResigning = false;
    engineList.get(index).clearWithoutPonder();
    // this.currentEngineNo = index;
    // Lizzie.leelaz.notPondering();
    Runnable syncBoard =
        new Runnable() {
          public void run() {
            while (!newEng.isLoaded() || newEng.isCheckingName) {
              try {
                Thread.sleep(100);
              } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
              }
            }
            Lizzie.board.restoreMoveNumber(mv, true, newEng);
            if (newEng.isKataGoPda) newEng.sendCommand("dympdacap " + newEng.pdaCap);
          }
        };
    Thread syncBoardTh = new Thread(syncBoard);
    syncBoardTh.start();
    // newEng.setResponseUpToDate();
    //  newEng.canGetGenmoveInfo = false;
    Lizzie.frame.clearKataEstimate();
    // Lizzie.leelaz.Pondering();
  }

  public void clearEngineGame() {
    if (isEngineGame || isPreEngineGame) {
      Lizzie.frame.addInput(true);
      isPreEngineGame = false;
      if (!isEngineGame) return;
      isEngineGame = false;
      LizzieFrame.menu.toggleDoubleMenuGameStatus();
      LizzieFrame.toolbar.isPkStop = false;
    }
  }

  public void restartEngineForPk(int index) {
    if (index > this.engineList.size()) return;
    // Lizzie.board.saveMoveNumber();
    Leelaz newEng = engineList.get(index);
    newEng.isLoaded = false;
    newEng.played = false;
    newEng.width = Board.boardWidth;
    newEng.height = Board.boardHeight;
    newEng.komi = (float) Lizzie.board.getHistory().getGameInfo().getKomi();
    // if (!newEng.isStarted()) {
    try {
      newEng.startEngine(index);
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    // }
    // else {newEng.initializeStreams();}
    // Lizzie.leelaz = newEng;
    // Lizzie.leelaz.clear();
    EngineManager.currentEngineNo = index;
    // Lizzie.leelaz.notPondering();
    Runnable syncBoard =
        new Runnable() {
          public void run() {
            while (!newEng.isLoaded() || newEng.isCheckingName) {
              try {
                Thread.sleep(100);
              } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
              }
            }
            Lizzie.board.resendMoveToEngine(newEng);
            newEng.nameCmd();

            newEng.setResponseUpToDate();

            if (engineGameInfo.isGenmove) {
              if (Lizzie.config.pkAdvanceTimeSettings) {
                newEng.sendCommand(Lizzie.config.advanceBlackTimeTxt);
              } else {
                if (index == engineGameInfo.whiteEngineIndex && engineGameInfo.timeWhite > 0)
                  newEng.sendCommand("time_settings 0 " + engineGameInfo.timeWhite + " 1");
                else if (index == engineGameInfo.blackEngineIndex && engineGameInfo.timeBlack > 0)
                  newEng.sendCommand("time_settings 0 " + engineGameInfo.timeBlack + " 1");
              }
              if (Lizzie.board.getHistory().isBlacksTurn()) {
                Lizzie.leelaz = engineList.get(engineGameInfo.blackEngineIndex);
                Lizzie.leelaz.genmoveForPk("b");
              } else {
                Lizzie.leelaz = engineList.get(engineGameInfo.whiteEngineIndex);
                Lizzie.leelaz.genmoveForPk("w");
              }
            } else {
              if (Lizzie.board.getHistory().isBlacksTurn()) {
                engineList.get(engineGameInfo.blackEngineIndex).ponder();
              } else {
                engineList.get(engineGameInfo.whiteEngineIndex).ponder();
              }
            }
          }
        };
    Thread syncBoardTh = new Thread(syncBoard);
    syncBoardTh.start();

    // Lizzie.leelaz.Pondering();
  }

  public void switchEngine(int index, boolean isMain) {
    engineNo = index;
    if (Lizzie.config.isDoubleEngineMode()
        && index == (isMain ? currentEngineNo2 : currentEngineNo)) {
      Utils.showMsg(resourceBundle.getString("EngineManager.sameEngineHint"));
      return;
    }
    if (isEmpty) isEmpty = false;
    if (index > this.engineList.size()) return;
    Leelaz newEng = engineList.get(index);
    if (newEng == null) return;
    // newEng.isReadyForGenmoveGame = false;
    boolean changeBoard = true;
    if (newEng.width == Board.boardWidth && newEng.height == Board.boardHeight) changeBoard = false;
    boolean changeOriBoard = true;
    if (newEng.oriWidth == Board.boardWidth && newEng.oriHeight == Board.boardHeight)
      changeOriBoard = false;
    boolean isEmptyBoard = false;
    if (Lizzie.board.getHistory().getStart() == Lizzie.board.getHistory().getEnd())
      isEmptyBoard = true;

    // Lizzie.frame.menu.showPda(false);
    try {
      if (isEmptyBoard && changeOriBoard && isMain)
        Lizzie.board.reopenOnlyBoard(newEng.oriWidth, newEng.oriHeight);
      if ((isMain && currentEngineNo != -1) || (!isMain && Lizzie.leelaz2 != null)) {
        Leelaz curEng = null;
        if (!isMain) {
          if (Lizzie.leelaz2 != null) curEng = Lizzie.leelaz2;
        } else curEng = engineList.get(EngineManager.currentEngineNo);
        // curEng.switching = true;
        try {
          if (!Lizzie.config.fastChange) {
            curEng.normalQuit();
          } else {
            if (curEng.isLeela0110) curEng.leela0110StopPonder();
            curEng.nameCmdfornoponder();
          }
        } catch (Exception e) {
          e.printStackTrace();
        }
        curEng.notPondering();
      }
      if (isMain) Lizzie.leelaz = newEng;
      else Lizzie.leelaz2 = newEng;
      // boolean changedKomi = Lizzie.board.getHistory().getGameInfo().changedKomi;
      if (!isMain) {
        newEng.komi = (float) Lizzie.board.getHistory().getGameInfo().getKomi();
      } else newEng.komi = newEng.orikomi;
      if (!newEng.isStarted()) {
        newEng.isLoaded = false;
        if (isEmptyBoard && isMain) {
          newEng.width = newEng.oriWidth;
          newEng.height = newEng.oriHeight;
        } else {
          newEng.width = Board.boardWidth;
          newEng.height = Board.boardHeight;
        }
        newEng.startEngine(index);
      } else {
        // newEng.getEngineName(index);
        newEng.canRestoreDymPda = false;
        if (!(isEmptyBoard && changeBoard) || !isMain) {
          newEng.width = Board.boardWidth;
          newEng.height = Board.boardHeight;
          newEng.boardSize(newEng.width, newEng.height);
        }
        if (isEmptyBoard && changeOriBoard && isMain) {
          newEng.width = newEng.oriWidth;
          newEng.height = newEng.oriHeight;
          newEng.boardSize(newEng.width, newEng.height);
        }
        newEng.sendCommand("komi " + newEng.komi);
        newEng.isCheckingName = true;
        newEng.sendCommand("name");

        Lizzie.board.getHistory().getGameInfo().setKomi(newEng.komi);
        Lizzie.config.leelaversion = newEng.version;
        Runnable runnable =
            new Runnable() {
              public void run() {
                LizzieFrame.toolbar.reSetButtonLocation();
                if (Lizzie.frame.resetMovelistFrameandAnalysisFrame())
                  Lizzie.frame.setVisible(true);
              }
            };
        Thread thread = new Thread(runnable);
        thread.start();
      }
      newEng.anaGameResignCount = 0;
      if (isMain) {
        Runnable syncBoard =
            new Runnable() {
              public void run() {
                do {
                  try {
                    Thread.sleep(100);
                  } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                  }
                } while (!newEng.isLoaded() || newEng.isCheckingName);
                newEng.notPondering();
                Lizzie.board.resendMoveToEngine(newEng);
                if (newEng == Lizzie.leelaz) {
                  Lizzie.board.clearBestMovesAfterForFirstEngine(
                      Lizzie.board.getHistory().getStart());
                  currentEngineNo = Lizzie.leelaz.currentEngineN();
                  featurecat.lizzie.gui.Menu.engineMenu.setText(
                      "["
                          + (currentEngineNo + 1)
                          + "]: "
                          + engineList.get(currentEngineNo).oriEnginename);

                  changeEngIco(1);
                  LizzieFrame.toolbar.reSetButtonLocation();
                  LizzieFrame.boardRenderer.removeKataEstimateImage();
                  if (Lizzie.frame.floatBoard != null)
                    Lizzie.frame.floatBoard.boardRenderer.removeKataEstimateImage();
                  if (Lizzie.config.showSubBoard)
                    LizzieFrame.subBoardRenderer.removeKataEstimateImage();
                  if (currentEngineNo > 20) LizzieFrame.menu.changeEngineIcon(20, 3);
                  else LizzieFrame.menu.changeEngineIcon(currentEngineNo, 3);
                  newEng.setResponseUpToDate();
                }
              }
            };
        Thread syncBoardTh = new Thread(syncBoard);
        syncBoardTh.start();
      } else if (Lizzie.leelaz2 != null) {
        Runnable syncBoard =
            new Runnable() {
              public void run() {
                while (!Lizzie.leelaz2.isLoaded() || Lizzie.leelaz2.isCheckingName) {
                  try {
                    Thread.sleep(100);
                  } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                  }
                }
                EngineManager.currentEngineNo2 = Lizzie.leelaz2.currentEngineN();
                if (currentEngineNo2 > 20) LizzieFrame.menu.changeEngineIcon2(20, 3);
                else LizzieFrame.menu.changeEngineIcon2(currentEngineNo2, 3);

                Lizzie.board.clearBestMovesAfterForSecondEngine(
                    Lizzie.board.getHistory().getStart());
                Lizzie.board.resendMoveToEngine(newEng);
                featurecat.lizzie.gui.Menu.engineMenu2.setText(
                    "["
                        + (currentEngineNo2 + 1)
                        + "]: "
                        + engineList.get(currentEngineNo2).currentEnginename);
                changeEngIco(2);
                LizzieFrame.boardRenderer2.removeKataEstimateImage();
                Lizzie.leelaz.ponder();
                Lizzie.leelaz2.setResponseUpToDate();
              }
            };
        Thread syncBoardTh = new Thread(syncBoard);
        syncBoardTh.start();
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void changeEngIcoForEndPk() {
    // Lizzie.frame.subBoardRenderer.reverseBestmoves = false;
    //  Lizzie.frame.boardRenderer.reverseBestmoves = false;
    clearFirstSecondEngineCountDown();
    Menu.engineMenu.setEnabled(true);
    if (Lizzie.board.getData().blackToPlay) {
      // switchEngine(Lizzie.frame.toolbar.engineWhite);
      Lizzie.leelaz = engineList.get(engineGameInfo.firstEngineIndex);
      engineList.get(engineGameInfo.firstEngineIndex).nameCmd();

      // switchEngine(Lizzie.frame.toolbar.engineBlack);
    } else {
      // switchEngine(Lizzie.frame.toolbar.engineBlack);
      Lizzie.leelaz = engineList.get(engineGameInfo.secondEngineIndex);
      engineList.get(engineGameInfo.secondEngineIndex).nameCmd();
      // engineList.get(Lizzie.frame.toolbar.engineWhite).clear();
      // switchEngine(Lizzie.frame.toolbar.engineWhite);
    }
    // this.currentEngineNo = Lizzie.leelaz.currentEngineN();
    // double komi = Lizzie.board.getHistory().getGameInfo().getKomi();
    Lizzie.config.notStartPondering = true;
    // switchEngine(Lizzie.leelaz.currentEngineN(), true);
    // Lizzie.board.setKomi(komi);
    //  Lizzie.board.clearAfterMove();
    EngineManager.currentEngineNo = Lizzie.leelaz.currentEngineN();
    Menu.engineMenu.setText(
        resourceBundle.getString("EngineManager.engine")
            + (Lizzie.leelaz.currentEngineN() + 1)
            + ": "
            + Lizzie.leelaz.oriEnginename);
    changeEngIco(1);
    LizzieFrame.menu.setBtnRankMark();
    if (engineList.get(engineGameInfo.whiteEngineIndex).isKatago
        || engineList.get(engineGameInfo.whiteEngineIndex).isSai)
      Lizzie.board.isPkBoardKataW = true;
    else if (engineList.get(engineGameInfo.blackEngineIndex).isKatago
        || engineList.get(engineGameInfo.blackEngineIndex).isSai)
      Lizzie.board.isPkBoardKataB = true;
    Lizzie.config.chkPkStartNum = false;
    Lizzie.frame.restoreWRN(engineGameInfo.isGenmove);
    Lizzie.frame.refresh();
  }

  public String getEngineName(int index) {
    return engineList.get(index).getEngineName(index);
  }

  private void changeEngIco(int index) {
    LizzieFrame.menu.changeicon(index);
  }

  public boolean isEngineGame() {
    return isPreEngineGame || isEngineGame;
  }

  //  public void setEngineCountDown(
  //      EngineCountDown engineCountDown,
  //      int leftMinutes,
  //      int countDownSeconds,
  //      int countDownMoves,
  //      Leelaz engine) {
  //    engineCountDown.leftSeconds = leftMinutes * 60;
  //    engineCountDown.countDownSeconds = countDownSeconds;
  //    engineCountDown.countDownMoves = countDownMoves;
  //    engineCountDown.engine = engine;
  //  }

  private void clearFirstSecondEngineCountDown() {
    firstEngineCountDown = null;
    secondEngineCountDown = null;
  }

  public void clearPlayingAgainstHumanEngineCountDown() {
    playingAgainstHumanEngineCountDown = null;
  }

  public void stopCountDown() {
    if (timeScheduled != null) {
      timeScheduled.shutdownNow();
      timeScheduled = null;
    }
  }

  public void StartCountDown() {
    stopCountDown();
    timeScheduledTimes = 0;
    timeScheduled = new ScheduledThreadPoolExecutor(1);
    timeScheduled.scheduleAtFixedRate(
        new Runnable() {
          @Override
          public void run() {
            timeScheduledTimes++;
            if (timeScheduledTimes >= 10) {
              timeScheduledTimes = 0;
              EngineCountDown countDown = null;
              if (isEngineGame) {
                if (LizzieFrame.toolbar.isPkStop) return;
                if (Lizzie.board.getHistory().isBlacksTurn()) {
                  if (firstEngineCountDown != null && firstEngineCountDown.isPlayBlack)
                    countDown = firstEngineCountDown;
                  else if (secondEngineCountDown != null && secondEngineCountDown.isPlayBlack)
                    countDown = secondEngineCountDown;
                } else {
                  if (firstEngineCountDown != null && !firstEngineCountDown.isPlayBlack)
                    countDown = firstEngineCountDown;
                  else if (secondEngineCountDown != null && !secondEngineCountDown.isPlayBlack)
                    countDown = secondEngineCountDown;
                }
              } else if (Lizzie.frame.isPlayingAgainstLeelaz
                  && playingAgainstHumanEngineCountDown != null
                  && Lizzie.board.getHistory().isBlacksTurn()
                      == playingAgainstHumanEngineCountDown.isPlayBlack)
                countDown = playingAgainstHumanEngineCountDown;
              if (countDown != null) {
                countDown.countDownCentiseconds();
              }
            }
          }
        },
        0,
        1,
        TimeUnit.MILLISECONDS);
  }
}
