package featurecat.lizzie.analysis;

import featurecat.lizzie.Config;
import featurecat.lizzie.Lizzie;
import featurecat.lizzie.gui.FloatBoard;
import featurecat.lizzie.gui.LizzieFrame;
import featurecat.lizzie.gui.SMessage;
import featurecat.lizzie.rules.Board;
import featurecat.lizzie.rules.BoardHistoryNode;
import featurecat.lizzie.rules.Stone;
import featurecat.lizzie.util.Utils;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import org.jdesktop.swingx.util.OS;

public class ReadBoard {
  public Process process;
  private InputStreamReader inputStream;
  private BufferedOutputStream outputStream;
  private ScheduledExecutorService executor;
  ArrayList<Integer> tempcount = new ArrayList<Integer>();
  // private long startSyncTime = 0;

  public boolean isLoaded = false;
  private int version = 220430;
  private String engineCommand;
  public String currentEnginename = "";
  private int port = -1;

  boolean firstcount = true;
  public int numberofcount = 0;
  public boolean firstSync = true;
  // public boolean syncBoth = Lizzie.config.syncBoth;
  private ReadBoardStream readBoardStream;
  private Socket socket;
  private ServerSocket s;
  private boolean noMsg = false;
  private boolean usePipe = true;
  private boolean needGenmove = false;
  private boolean showInBoard = false;
  private boolean isSyncing = false;
  // private long startTime;
  private boolean javaReadBoard = false;
  private String javaReadBoardName = "readboard-1.6.1-shaded.jar";
  private boolean waitSocket = true;
  public boolean lastMovePlayByLizzie = false;
  private boolean hideFloadBoardBeforePlace = false;
  private boolean hideFromPlace = false;
  public boolean editMode = false;

  public ReadBoard(boolean usePipe, boolean isJavaReadBoard) throws Exception {
    this.usePipe = usePipe;
    this.javaReadBoard = isJavaReadBoard;
    if (s != null && !s.isClosed()) {
      s.close();
    }
    if (usePipe) engineCommand = "readboard\\readboard.exe";
    else engineCommand = "readboard\\readboard.bat";
    startEngine(engineCommand, 0);
  }

  private void createSocketServer() {
    try {
      s = new ServerSocket(0);
      port = s.getLocalPort();
      waitSocket = false;
      while (true) {
        socket = s.accept();
        readBoardStream = new ReadBoardStream(socket);
        break;
      }
    } catch (Exception e) {
      if (!noMsg)
        Utils.showMsg(
            Lizzie.resourceBundle.getString("ReadBoard.port")
                + " "
                + port
                + " "
                + Lizzie.resourceBundle.getString("ReadBoard.portUsed")
                + e.getMessage());
      try {
        s.close();
      } catch (Exception e1) {
        e1.printStackTrace();
      }
      e.printStackTrace();
    }
  }

  public void startEngine(String engineCommand, int index) throws Exception {
    if (javaReadBoard) {
      File javaReadBoard = new File("readboard_java" + File.separator + javaReadBoardName);
      if (!javaReadBoard.exists()) {
        Utils.deleteDir(new File("readboard_java"));
        Utils.copyReadBoardJava(javaReadBoardName);
      }
      // 共传入5个参数,语言 是否java外观 字体大小 宽 高
      String param = "";
      param = param + " " + Lizzie.resourceBundle.getString("ReadBoard.language") + " ";
      if (Lizzie.config.useJavaLooks) param = param + "true ";
      else param = param + "false ";
      param = param + (int) Math.round(Config.frameFontSize * Lizzie.javaScaleFactor);
      param = " " + param + " " + Board.boardWidth + " " + Board.boardHeight;
      try {
        if (OS.isWindows()) {
          boolean success = false;
          String java64Path = "jre\\java11\\bin\\java.exe";
          File java64 = new File(java64Path);

          if (java64.exists()) {
            try {
              process =
                  Runtime.getRuntime()
                      .exec(
                          java64Path
                              + " -jar -Dsun.java2d.uiScale=1.0 readboard_java"
                              + File.separator
                              + javaReadBoardName
                              + param);
              success = true;
            } catch (Exception e) {
              success = false;
              e.printStackTrace();
            }
          }
          if (!success) {
            String java32Path = "jre\\java8_32\\bin\\java.exe";
            File java32 = new File(java32Path);
            if (java32.exists()) {
              try {
                process =
                    Runtime.getRuntime()
                        .exec(
                            java32
                                + " -jar -Dsun.java2d.uiScale=1.0 readboard_java"
                                + File.separator
                                + javaReadBoardName
                                + param);
                success = true;
              } catch (Exception e) {
                success = false;
                e.printStackTrace();
              }
            }
          }
          if (!success) {
            process =
                Runtime.getRuntime()
                    .exec(
                        "java -Dsun.java2d.uiScale=1.0 -jar readboard_java"
                            + File.separator
                            + javaReadBoardName
                            + param);
          }
        } else {
          process =
              Runtime.getRuntime()
                  .exec(
                      "java -Dsun.java2d.uiScale=1.0 -jar readboard_java"
                          + File.separator
                          + javaReadBoardName
                          + param);
        }
      } catch (Exception e) {
        Utils.showMsg(e.getLocalizedMessage());
      }
    } else {
      if (!usePipe) {
        waitSocket = true;
        noMsg = false;
        Runnable runnable2 =
            new Runnable() {
              public void run() {
                if (s == null || s.isClosed()) createSocketServer();
              }
            };
        Thread thread2 = new Thread(runnable2);
        thread2.start();
        int times = 300;
        while (waitSocket && times > 0) {
          Thread.sleep(10);
          times--;
        }
      }
      List<String> commands = new ArrayList<String>();
      commands.add(engineCommand);
      commands.add("yzy");
      commands.add(
          !LizzieFrame.toolbar.chkAutoPlayTime.isSelected()
                  || LizzieFrame.toolbar.txtAutoPlayTime.getText().equals("")
              ? " "
              : LizzieFrame.toolbar.txtAutoPlayTime.getText());
      commands.add(
          !LizzieFrame.toolbar.chkAutoPlayPlayouts.isSelected()
                  || LizzieFrame.toolbar.txtAutoPlayPlayouts.getText().equals("")
              ? " "
              : LizzieFrame.toolbar.txtAutoPlayPlayouts.getText());
      commands.add(
          !LizzieFrame.toolbar.chkAutoPlayFirstPlayouts.isSelected()
                  || LizzieFrame.toolbar.txtAutoPlayFirstPlayouts.getText().equals("")
              ? " "
              : LizzieFrame.toolbar.txtAutoPlayFirstPlayouts.getText());

      if (usePipe) commands.add("0");
      else commands.add("1");
      // if (Lizzie.config.isChinese)
      commands.add(Lizzie.resourceBundle.getString("ReadBoard.language"));
      // else commands.add("1");
      if (usePipe) commands.add("-1");
      else commands.add(String.valueOf(port));
      ProcessBuilder processBuilder = new ProcessBuilder(commands);
      if (usePipe) processBuilder.directory(new File("readboard"));
      processBuilder.redirectErrorStream(true);
      try {
        process = processBuilder.start();
      } catch (IOException e) {
        // TODO Auto-generated catch block
        if (!usePipe) {
          Utils.showMsg(e.getLocalizedMessage());
          SMessage msg = new SMessage();
          msg.setMessage(Lizzie.resourceBundle.getString("ReadBoard.loadFailed"), 2);
          s.close();
          return;
        } else {
          System.out.print(e.getLocalizedMessage());
          throw new Exception("Start pipe failed");
        }
      }
    }
    if (usePipe) {
      initializeStreams();
      executor = Executors.newSingleThreadScheduledExecutor();
      executor.execute(this::read);
    }
  }

  private void initializeStreams() throws UnsupportedEncodingException {
    inputStream = new InputStreamReader(process.getInputStream(), "UTF-8");
    outputStream = new BufferedOutputStream(process.getOutputStream());
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
            if (!isLoaded) {
              isLoaded = true;
              if (!javaReadBoard) checkVersion();
            }
          } catch (Exception ex) {
            ex.printStackTrace();
          }
          line = new StringBuilder();
        }
      }
      // this line will be reached when BoardSync shuts down
      if (Lizzie.leelaz.isPondering()) Lizzie.leelaz.ponder();
      showInBoard = false;
      if (Lizzie.frame.floatBoard != null) {
        Lizzie.frame.floatBoard.setVisible(false);
      }
      System.out.println("Board synchronization tool process ended.");
      if (!javaReadBoard && !isLoaded) {
        try {
          Runtime.getRuntime().exec("powershell /c start readboard\\readboard.bat");
        } catch (IOException e) {
          try {
            Runtime.getRuntime().exec("powershell /c start readboard\\readboard.exe");
          } catch (Exception s) {
            s.printStackTrace();
          }
          e.printStackTrace();
        }
        SMessage msg = new SMessage();
        msg.setMessage(Lizzie.resourceBundle.getString("ReadBoard.loadFailed"), 2);
        shutdown();
      } else shutdown();
      // Do no exit for switching weights
      // System.exit(-1);
    } catch (IOException e) {
      e.printStackTrace();
      Lizzie.frame.bothSync = false;
      Lizzie.frame.syncBoard = false;
      // System.exit(-1);
    }
  }

  public void parseLine(String line) {
    // if (Lizzie.gtpConsole.isVisible())
    // Lizzie.gtpConsole.addLine(line);
    //  System.out.println(line);
    //    if (Lizzie.frame.isPlayingAgainstLeelaz) {
    //      if (Lizzie.frame.playerIsBlack && !Lizzie.board.getHistory().isBlacksTurn()) return;
    //      if (!Lizzie.frame.playerIsBlack && Lizzie.board.getHistory().isBlacksTurn()) return;
    //    }
    if (line.startsWith("playpon")) {
      String[] params = line.split(" ");
      if (params.length == 2) {
        if (params[1].startsWith("on")) {
          Lizzie.config.readBoardPonder = true;
        } else if (params[1].startsWith("off")) {
          Lizzie.config.readBoardPonder = false;
        }
      }
    }
    if (line.startsWith("re=")) {
      String[] params = line.substring(3).split(",");
      if (params.length == Board.boardWidth) {
        for (int i = 0; i < params.length; i++)
          tempcount.add(Integer.parseInt(params[i].substring(0, 1)));
      }
    }
    if (line.startsWith("version")) {
      Lizzie.gtpConsole.addLineReadBoard("Board synchronization tool " + line + "\n");
      String[] params = line.trim().split(" ");
      if (Integer.parseInt(params[1]) < version) {
        SMessage msg = new SMessage();
        msg.setMessage(
            Lizzie.resourceBundle.getString(
                "#Field ResourceBundle: resourceBundle\r\n"
                    + "#Wed Dec 02 16:58:49 CST 2020\r\n"
                    + "CaptureTsumeGoSettings.title=Settings\r\n"
                    + "CaptureTsumeGoSettings.lblBlackOffset=Black Max Color Offsets(0-255):\r\n"
                    + "CaptureTsumeGoSettings.lblWhiteOffset=White Max Color Offsets(0-255):\r\n"
                    + "CaptureTsumeGoSettings.lblBlackPercent=Black Min Percents(1-100):\r\n"
                    + "CaptureTsumeGoSettings.lblWhitePercent=White Min Percents(1-100):\r\n"
                    + "CaptureTsumeGoSettings.lblGrayOffset=Grayscale Max Offset(0-255):\r\n"
                    + "CaptureTsumeGoSettings.btnConfirm=Confirm\r\n"
                    + "CaptureTsumeGoSettings.btnCancel=Cancel\r\n"
                    + "CaptureTsumeGoFrame.title=Capture Tsumego\r\n"
                    + "CaptureTsumeGoFrame.lblStep1=<html>Step 1:<br /><br />&nbsp &nbsp Left click on the start corner (0,0).</html>\r\n"
                    + "CaptureTsumeGoFrame.lblStep2=<html>Step 2:<br /><br />&nbsp &nbsp Left click on the intersection of the first <br /> square (1,1).</html>\r\n"
                    + "CaptureTsumeGoFrame.lblStep3=<html>Step 3:<br /><br />&nbsp &nbsp Move the mouse until the blue square cover <br /> the entire tsumego area and left click to finish</html>\r\n"
                    + "CaptureTsumeGoFrame.btnStart=Start (Shift+T)\r\n"
                    + "CaptureTsumeGoFrame.btnSettings=Settings\r\n"
                    + "CaptureTsumeGoFrame.lblTip=Tip: Press 'Esc' to interrupt capture. \r\n"
                    + "TsumeGoFrame.title=Tsumego Frame\r\n"
                    + "TsumeGoFrame.btnCapture=Capture\r\n"
                    + "TsumeGoFrame.btnConfirm=Confirm\r\n"
                    + "TsumeGoFrame.btnCancel=Cancel\r\n"
                    + "TsumeGoFrame.lblToPlay=To Play\r\n"
                    + "TsumeGoFrame.rdoKeepOn=Keep On\r\n"
                    + "TsumeGoFrame.rdoBlackToPlay=Black\r\n"
                    + "TsumeGoFrame.rdoWhiteToPlay=White\r\n"
                    + "TsumeGoFrame.lblAttacker=Attacker\r\n"
                    + "TsumeGoFrame.rdoAutoDetect=Auto Detect\r\n"
                    + "TsumeGoFrame.rdoBlackAttack=Black\r\n"
                    + "TsumeGoFrame.rdoWhiteAttack=White\r\n"
                    + "TsumeGoFrame.lblKoThreat=Add Ko Threat\r\n"
                    + "TsumeGoFrame.rdoKoBoth=Both\r\n"
                    + "TsumeGoFrame.rdoKoAttacker=Attacker\r\n"
                    + "TsumeGoFrame.rdoKoDefender=Defender\r\n"
                    + "TsumeGoFrame.rdoKoNone=None\r\n"
                    + "TsumeGoFrame.lblWallDistance=Wall Distance\r\n"
                    + "Tsumego.noRoomForKoThreat1=No room for\r\n"
                    + "Tsumego.noRoomForKoThreat.black=Black\r\n"
                    + "Tsumego.noRoomForKoThreat.white=White\r\n"
                    + "Tsumego.noRoomForKoThreat2=ko threat\r\n"
                    + "BrowserFrame.initializing=Browser initializing, downloading dependency, please wait...\r\n"
                    + "ContributeView.title=KataGo Distributed Training\r\n"
                    + "ContributeView.btnPrevious=Previous\r\n"
                    + "ContributeView.btnNext=Next Game\r\n"
                    + "ContributeView.lblGoto=Jump\r\n"
                    + "ContributeView.btnGotoGame=Goto\r\n"
                    + "ContributeView.btnGotoMove=Goto\r\n"
                    + "ContributeView.chkAlwaysLastMove=Auto Jump To Last\r\n"
                    + "ContributeView.chkAutoPlay=Auto Play\r\n"
                    + "ContributeView.lblAutoPlayInterval=Interval(S)\r\n"
                    + "ContributeView.chkAutoPlayNextGame=Goto Next Game\r\n"
                    + "ContributeView.chkIgnoreNone19=Ignore none 19x19\r\n"
                    + "ContributeView.lblGameType=Game Type: \r\n"
                    + "ContributeView.lblKomi=Komi: \r\n"
                    + "ContributeView.lblGameResult=Result: \r\n"
                    + "ContributeView.lblGameResult.none=None\r\n"
                    + "ContributeView.show=Show\r\n"
                    + "ContributeView.hide=Hide\r\n"
                    + "ContributeView.rules.scoring=ScoringRule: \r\n"
                    + "ContributeView.rules.scoring.area=Area\r\n"
                    + "ContributeView.rules.scoring.territory=Territory\r\n"
                    + "ContributeView.rules.ko=KoRule: \r\n"
                    + "ContributeView.rules.ko.simple=Simple\r\n"
                    + "ContributeView.rules.ko.situational=Situational\r\n"
                    + "ContributeView.rules.ko.positional=Positional\r\n"
                    + "ContributeView.rules.suicide=MultiStoneSuicide: \r\n"
                    + "ContributeView.rules.yes=Yes\r\n"
                    + "ContributeView.rules.no=No\r\n"
                    + "ContributeView.rules.tax=TaxRule: \r\n"
                    + "ContributeView.rules.tax.none=None\r\n"
                    + "ContributeView.rules.tax.all=All\r\n"
                    + "ContributeView.rules.tax.seki=Seki\r\n"
                    + "ContributeView.rules.whiteHandicapBonus=WhiteHandicapBonus(Handicap N): \r\n"
                    + "ContributeView.rules.whiteHandicapBonus.0=0\r\n"
                    + "ContributeView.rules.whiteHandicapBonus.N=N\r\n"
                    + "ContributeView.rules.whiteHandicapBonus.N-1=N-1\r\n"
                    + "ContributeView.rules.button=Button: \r\n"
                    + "ContributeView.showRules=Show Rules\r\n"
                    + "ContributeView.hideRules=Hide Rules\r\n"
                    + "ContributeView.btnShutdown=Stop Contributing\r\n"
                    + "ContributeView.btnForceShutdown=Force Stop\r\n"
                    + "ContributeView.btnForceShutdownTip=Stop contribute immediately,unfinished game will be throw away. \r\n"
                    + "ContributeView.btnSlowShutdown=Normal Stop(Delay)\r\n"
                    + "ContributeView.btnSlowShutdownTip=New games will not be started,stop contribute after current games finished.\r\n"
                    + "ContributeView.btnSaveGameRecords=Save Games\r\n"
                    + "ContributeView.lblTip=Initializing...\r\n"
                    + "ContributeView.showConsole=Show Console\r\n"
                    + "ContributeView.hideConsole=Hide Console\r\n"
                    + "ContributeView.btnFullConsole=Full Console\r\n"
                    + "ContributeView.closeTip=Please use \"Stop\" button on bottom.\r\n"
                    + "ContributeView.lblGameInfos.watching=Watching \r\n"
                    + "ContributeView.lblGameInfos.games=\\ Game(s)\r\n"
                    + "ContributeView.lblGameInfos.complete=Completed \r\n"
                    + "ContributeView.btnCloseView=Close Dialog\r\n"
                    + "ContributeView.chkAlwaysTop=On Top\r\n"
                    + "ContributeView.btnResume=Resume\r\n"
                    + "ContributeView.btnPause=Pause\r\n"
                    + "ContributeSettings.title=KataGo Distributed Training Settings\r\n"
                    + "ContributeSettings.lblEnableSlowShutdown=Enable Normal Stop(Delay)\r\n"
                    + "ContributeSettings.slowShutdownTip=When request to stop,new games will not be started,stop contribute after current games finished. If can't get started cancel it.\r\n"
                    + "ContributeSettings.aboutSlowShutdown=<html>For technical reasons,this function is based on .netframework4.0<br />It has been integrated in windows10,but for older system extra installation is needed</html>\r\n"
                    + "ContributeSettings.lblChkNoRatingMatches=No Rating Matches\r\n"
                    + "ContributeSettings.noRatingMatchesTip=Check to disable rating matches,for saving disk space(no longer download elder models).\r\n"
                    + "ContributeSettings.engineTip=Use official engine which support distributed training.\r\n"
                    + "ContributeSettings.gamesTip=Set simultaneous games,best equal to your optimal numSearchThreads in normal gtp config file.\r\n"
                    + "ContributeSettings.gamesTip2=By the end,unfinished games will not be saved.\r\n"
                    + "ContributeSettings.configTip=Use to set used GPU(s),different from normal gtp config file,default name is contribute_example.cfg.\r\n"
                    + "ContributeSettings.ownerShipTip=Slightly slows down training,but allows ownership visualization(Can be hidden by bottom toolbar-KataEstimate,but speed will not return).\r\n"
                    + "ContributeSettings.autoSaveTip=Automatically save game record after a game complete,saved in ContributeGames in LizzieYzy's folder.\r\n"
                    + "ContributeSettings.customCommandTip=Use custom command instead of engine path and config path,allow to use tools like SSH or plink to connect remote engine.\r\n"
                    + "ContributeSettings.lblEngine=KataGo Engine Path\r\n"
                    + "ContributeSettings.btnScan=Scan\r\n"
                    + "ContributeSettings.lblConfig=Config File Path(optional)\r\n"
                    + "ContributeSettings.chkUseCommand=Use Custom Command\r\n"
                    + "ContributeSettings.lblUserName=User Name\r\n"
                    + "ContributeSettings.btnSignUp=Sign Up\r\n"
                    + "ContributeSettings.lblPassword=Password\r\n"
                    + "ContributeSettings.lblSimultaneousGames=Simultaneous Games\r\n"
                    + "ContributeSettings.lblShowOwnerShip=Enable Ownership \r\n"
                    + "ContributeSettings.lblAutoSave=Auto Save Complete Games\r\n"
                    + "ContributeSettings.btnSave=Save Settings\r\n"
                    + "ContributeSettings.btnStart=Start Contribute!\r\n"
                    + "ContributeSettings.chkRemote=Remote SSH\r\n"
                    + "ContributeSettings.btnRemoteSetting=Set\r\n"
                    + "ContributeSettings.chkIgnoreSettings=Just use command, ignore after setting\r\n"
                    + "contributeView.gameType.trainingGame=Training Game\r\n"
                    + "contributeView.gameType.ratingGame=Rating Game\r\n"
                    + "Contribute.engineMenu.contributing=Contributing\r\n"
                    + "Contribute.slowQuit.repeatedly=Stop request has sent.\r\n"
                    + "Contribute.tips.signalToStop=Stop signal received,shut down once all current games are finished.\r\n"
                    + "Contribute.tips.exitedAfterSignal=All current games are finished,contributing stopped.\r\n"
                    + "Contribute.tips.startingNewGame=Starting a new game...\r\n"
                    + "Contribute.tips.predownload=Predownloading models...\r\n"
                    + "Contribute.tips.download=Downloading models...\r\n"
                    + "Contribute.tips.invalidUserNamePassword=Invalid UserName or Password\r\n"
                    + "Contribute.tips.noResponseFromServer=No response from server\r\n"
                    + "Contribute.tips.errorConnectingToServer=Error connecting to server\r\n"
                    + "Contribute.tips.uploadError=Errors when uploading,see details in console\r\n"
                    + "Contribute.tips.initialQuery=Initial query errors,see details in console\r\n"
                    + "Contribute.tips.couldNotParseServerUrl=Could not parse serverUrl\r\n"
                    + "Contribute.tips.didYouVerifyYourEmailAddress=Did you verify your email address?\r\n"
                    + "Contribute.tips.modelFileWasIncompletelyDownloaded=Model file was incompletely downloaded\r\n"
                    + "Contribute.tips.useOfficialEngineSupportDistributedTraining=Use official engine which support distributed training!\r\n"
                    + "Contribute.tips.noGameToSave=No game can be saved.\r\n"
                    + "Contribute.tips.contributingAndStartAnotherLizzieYzy=Contribute training,you can start another LizzieYzy!\r\n"
                    + "Contribute.tips.alreadyTraining=Contribute training processing,please stop training first!\r\n"
                    + "Contribute.tips.noUserName=Please set user name first.\r\n"
                    + "Contribute.tips.noEnginePath=Please set engine first.\r\n"
                    + "Contribute.tips.noCAcerts=Could not find CA certs.\r\n"
                    + "Contribute.tips.PauseHaveSent=Pause request have sent(note: this may take a minute).\r\n"
                    + "Contribute.tips.ResumeHaveSent=Resume request have sent.\r\n"
                    + "Contribute.version.outOfDate=<html>Current engine verion is out of date\r\n"
                    + "Contribute.version.outOfDate2=Please update to newest official verion<br /><a color=\"blue\" href=\"https://github.com/lightvector/KataGo/releases\">Official Releases</a></html></html>\r\n"
                    + "Contribute.pauseResumeTooFrequently=Already sent request,do not Pasuse/Resume too frequently!\r\n"
                    + "BatchShareFrame.title=Share Game records\r\n"
                    + "BatchShareFrame.btnApply=Choose and upload\r\n"
                    + "BatchShareFrame.btnCancel=Cancel\r\n"
                    + "SetDelayShowCandidates.title=Set Candidates Display Delay\r\n"
                    + "SetDelayShowCandidates.lblDelayCandidates=Display Delay(Seconds)\r\n"
                    + "SetDelayShowCandidates.apply=Confirm\r\n"
                    + "SetDelayShowCandidates.cancel=Cancel\r\n"
                    + "SetDelayShowCandidates.txtDelayCandidates.tooltips=Zero or negative means require manually display candidate(key F).\r\n"
                    + "FoxKifuDownload.title=Search game records\r\n"
                    + "FoxKifuDownload.lblUserName=User Name:\r\n"
                    + "FoxKifuDownload.btnSearch=Search\r\n"
                    + "FoxKifuDownload.lblAfterGet=After open game this frame:\r\n"
                    + "FoxKifuDownload.cbxAfterGet.min=Minimize\r\n"
                    + "FoxKifuDownload.cbxAfterGet.close=Close\r\n"
                    + "FoxKifuDownload.cbxAfterGet.none=Keep\r\n"
                    + "FoxKifuDownload.noUser=Please input user name!\r\n"
                    + "FoxKifuDownload.waitLastSearch=Please wait for last search!\r\n"
                    + "FoxKifuDownload.noMoreKifu=No more game records.\r\n"
                    + "FoxKifuDownload.noKifu=Can't find game record.\r\n"
                    + "FoxKifuDownload.rank.dan=dan\r\n"
                    + "FoxKifuDownload.rank.kyu=kyu\r\n"
                    + "FoxKifuDownload.winByRes=+Resign\r\n"
                    + "FoxKifuDownload.winByTime=+Time\r\n"
                    + "FoxKifuDownload.win=Win\r\n"
                    + "FoxKifuDownload.black=Black \r\n"
                    + "FoxKifuDownload.white=White \r\n"
                    + "FoxKifuDownload.points=\\ points\r\n"
                    + "FoxKifuDownload.stones=\\ stones\r\n"
                    + "FoxKifuDownload.other=Other\r\n"
                    + "FoxKifuDownload.column.index=Index\r\n"
                    + "FoxKifuDownload.column.time=Time\r\n"
                    + "FoxKifuDownload.column.black=Black\r\n"
                    + "FoxKifuDownload.column.rank=Rank\r\n"
                    + "FoxKifuDownload.column.white=White\r\n"
                    + "FoxKifuDownload.column.result=Result\r\n"
                    + "FoxKifuDownload.column.moves=Moves\r\n"
                    + "FoxKifuDownload.column.open=Open\r\n"
                    + "FoxKifuDownload.getKifuFailed=Get game record failed,message: \r\n"
                    + "StatisticsThreshold.title=Set Statistics Threshold\r\n"
                    + "StatisticsThreshold.lblWinLoss=WinRate\r\n"
                    + "StatisticsThreshold.lblScoreLoss=Score\r\n"
                    + "StatisticsThreshold.btnConfirm=Confirm\r\n"
                    + "StatisticsThreshold.btnCancel=Cancel\r\n"
                    + "StatisticsThreshold.loss=Loss\r\n"
                    + "StatisticsThreshold.wrongTHR=Wrong threshold! Upper must not smaller than lower.\r\n"
                    + "StatisticsThreshold.btnCalcMethod=CalcMethod\r\n"
                    + "StatisticsThreshold.btnReset=Default\r\n"
                    + "StatisticsThreshold.calcMethod.title=Calculate Method\r\n"
                    + "StatisticsThreshold.calcMethod.content=<html>If placed the best candidate,loss is 0.<br />Otherwise,calculate loss by compare win rate or score between previous and next.</html>\r\n"
                    + "VisualizedPanelSettings.title=Visualized Panel Settings\r\n"
                    + "SetCustomMode.floatMainBoard=Independent Main Board\r\n"
                    + "SetCustomMode.floatSubBoard=Independent Sub Board\r\n"
                    + "SetCustomMode.title=Set Custom Layout \r\n"
                    + "SetCustomMode.subBoard=Show Sub Board\r\n"
                    + "SetCustomMode.winratePanel=Show WinRate Graph\r\n"
                    + "SetCustomMode.commentPanel=Show Comment Panel\r\n"
                    + "SetCustomMode.variationPanel=Show Variation Panel\r\n"
                    + "SetCustomMode.listPanel=Show Candidates List\r\n"
                    + "SetCustomMode.informationPanel=Show Information Panel\r\n"
                    + "SetCustomMode.statusPanel=Show Status Panel\r\n"
                    + "SetCustomMode.bigSubBoard=Show Large Sub Board\r\n"
                    + "SetCustomMode.bigWinrate=Show Large WinRate\r\n"
                    + "SetCustomMode.sldBoardPositionProportion=Board/Frame Position Proportion\r\n"
                    + "Socket.connectFailed=Connect failed...Try again or download the latest version, URL: https://aistudio.baidu.com/aistudio/datasetdetail/116865\r\n"
                    + "ShareMessage.title=Share Successfully\r\n"
                    + "ShareMessage.copy=Copy\r\n"
                    + "ShareMessage.open=Open\r\n"
                    + "ShareMessage.openHtml=Browser\r\n"
                    + "ShareMessage.close=Close\r\n"
                    + "ShareMessage.lblUrl=URL:\r\n"
                    + "ShareFrame.title=Shafre Infomation\r\n"
                    + "ShareFrame.btnApply=Share\r\n"
                    + "ShareFrame.btnCancel=Cancel\r\n"
                    + "ShareFrame.btnLogin=SignUp/Login\r\n"
                    + "ShareFrame.reLogin=ReLogin\r\n"
                    + "ShareFrame.lblBlack=B:\r\n"
                    + "ShareFrame.lblWhite=W:\r\n"
                    + "ShareFrame.lblUploader=Uploader:\r\n"
                    + "ShareFrame.lblOther=Remark:\r\n"
                    + "ShareFrame.lblLabel=Label:\r\n"
                    + "ShareFrame.chkPublic=Public\r\n"
                    + "ShareFrame.lblNotice=Tip: Do not use symbol(><|\\\\/.,etc) in player name,label,uploader,remark.\r\n"
                    + "ShareFrame.emptyKifu=Game record is empty,please open a Game first.\r\n"
                    + "ShareFrame.emptyUploader=Uploader is empty,please login.\r\n"
                    + "ShareFrame.shareLabel1=Personal\r\n"
                    + "ShareFrame.shareLabel2=Pro\r\n"
                    + "ShareFrame.shareLabel3=Match\r\n"
                    + "ShareFrame.shareLabelOther=Other\r\n"
                    + "ScoreResult.title=Score Result\r\n"
                    + "ScoreResult.hint=Click to toggle dead stones and empty points.\r\n"
                    + "ScoreResult.lblBlackScore=Black: \r\n"
                    + "ScoreResult.lblWhiteScore=White: \r\n"
                    + "ScoreResult.blackWin=Black win \r\n"
                    + "ScoreResult.whiteWin=White win \r\n"
                    + "ScoreResult.points=\\ points \r\n"
                    + "ScoreResult.confirmResult=Confirm\r\n"
                    + "ScoreResult.btnRecalculate=Recalculate\r\n"
                    + "ScoreResult.btnToggleRulesArea=Area scoring\r\n"
                    + "ScoreResult.btnToggleRulesTerritory=Territory scoring\r\n"
                    + "ScoreResult.btnClose=Close\r\n"
                    + "ScoreResult.addResultTip=Added result to game info: \r\n"
                    + "ScoreResult.lblUseTerritoryScoring=Territory Scoring Result:\r\n"
                    + "ScoreResult.lblUseAreaScoring=Area Scoring Result:\r\n"
                    + "FastCommands.title=All Commands\r\n"
                    + "FastCommands.reset=Reset\r\n"
                    + "SetThreshold.title=Min Threshold\r\n"
                    + "JTabel.export=Export\r\n"
                    + "JTextPane.copy=Copy\r\n"
                    + "JTextPane.paste=Paste\r\n"
                    + "JTextPane.cut=Cut\r\n"
                    + "SetDiffAnalyze.lblWinThreshold=WinRate threshold\r\n"
                    + "SetDiffAnalyze.lblScoreThreshold=Score threshold\r\n"
                    + "SetDiffAnalyze.noBlackWhite=Wrong settings! Please choose at least one of analyze white/analyze black.\r\n"
                    + "SetDiffAnalyze.noWinScore=Wrong settings! Please choose at least one of winRate threshold/score threshold.\r\n"
                    + "SetDiffAnalyze.noCondition=Wrong settings! Please set at least one of time/visits/best candidate visits.\r\n"
                    + "SetDiffAnalyze.title=Enhance Analyze Condition\r\n"
                    + "StartAnaDialog.lblDiffAnalyze=Enhance analyze on big difference\r\n"
                    + "StartAnaDialog.chkAllBranches=Analyze all branches\r\n"
                    + "StartAnaDialog.btnSetDiff=Set condition\r\n"
                    + "StartAnaDialog.title=Auto Analyze\r\n"
                    + "StartAnaDialog.startMove=Start move(Optional,default current move)\r\n"
                    + "StartAnaDialog.startMoveBatch=Start move(Optional,default first move)\r\n"
                    + "StartAnaDialog.endMove=End move(Optional,default last move)\r\n"
                    + "StartAnaDialog.timePerMove=Time per move(Seconds)\r\n"
                    + "StartAnaDialog.totalVisitsPerMove=Total visits per move\r\n"
                    + "StartAnaDialog.firstVisitsPerMove=Best candidate visits per move\r\n"
                    + "StartAnaDialog.analyzeBlack=Analyze black\r\n"
                    + "StartAnaDialog.analyzeWhite=Analyze white\r\n"
                    + "StartAnaDialog.autoSaveKifu=Auto save game\r\n"
                    + "StartAnaDialog.analyzeAllBranch=Analyze all branch\r\n"
                    + "StartAnaDialog.startAnalyze=Start\r\n"
                    + "StartAnaDialog.stopAnalyze=Stop\r\n"
                    + "Utils.noSoundFile=Can not find sound file \"\r\n"
                    + "AdvanceTimeSettings.describe=<html>Advance time settings allow user to control engine's time settings by command,if use this settings other time settings will be disabled.<br />Currently two kind commands is supported:<br />1.time_settings<br />&nbsp;&nbsp;&nbsp;&nbsp;Standard GTP command,Format: time_settings a b c,a is keep time(seconds),b is boyoyomi time(seconds),c is the number of moves in each byoyomi should be played.<br />2.kata-time_settings<br />&nbsp;&nbsp;&nbsp;&nbsp;KataGo's command,Reference:<a color=\"blue\" href=\"https://github.com/lightvector/KataGo/blob/master/docs/GTP_Extensions.md\">&nbsp;here</a></html>\r\n"
                    + "AdvanceTimeSettings.title=About Advance time Settings\r\n"
                    + "SpinAndMirror.noneSquareError=Can't rotate none square board\r\n"
                    + "SpinAndMirror.inGameError=Can't rotate or flip board when playing game\r\n"
                    + "SuggestionInfoOrderSettings.title=Set Suggetion Info Display Order\r\n"
                    + "SuggestionInfoOrderSettings.lblFirstRow=First row:\r\n"
                    + "SuggestionInfoOrderSettings.lblSecondRow=Second row:\r\n"
                    + "SuggestionInfoOrderSettings.lblThirdRow=Third row:\r\n"
                    + "SuggestionInfoOrderSettings.rdoWinrate=WinRate\r\n"
                    + "SuggestionInfoOrderSettings.rdoPlayouts=Visits\r\n"
                    + "SuggestionInfoOrderSettings.rdoScoreLead=ScoreLead\r\n"
                    + "SuggestionInfoOrderSettings.okButton=Confirm\r\n"
                    + "SuggestionInfoOrderSettings.cancelButton=Cancel\r\n"
                    + "SuggestionInfoOrderSettings.wrongSettingsHint=Wrong settings,duplicated order!\r\n"
                    + "SuggestionInfoOrderSettings.preview=Preview:\r\n"
                    + "SuggestionInfoOrderSettings.lblSuggestionInfo=Info:\r\n"
                    + "Menu.newBoard=New File\r\n"
                    + "Menu.inputDoubleTip=Please input a number.\r\n"
                    + "Menu.initialMaxScoreLead.title=Set Initial Max Score Lead \r\n"
                    + "Menu.initialMaxScoreLead.message=Initial Max Score Lead,Current \r\n"
                    + "Menu.initialMaxScoreLead=Set initial max score lead \r\n"
                    + "Menu.kataGoDistributedTraining=Visualized KataGo distributed training\r\n"
                    + "Menu.kataGoTrainingSettings=KataGo training settings\r\n"
                    + "Menu.kataGoOfficialWebsite=Katago official website\r\n"
                    + "Menu.showContribute=Show contribute menu\r\n"
                    + "Menu.contributeMenu=Contribute\r\n"
                    + "Menu.flattenBoard=Flatten all moves(Can't revoke)\r\n"
                    + "Menu.introductionJpn=LizzieYzy\\u306E\\u4F7F\\u3044\\u65B9\r\n"
                    + "Menu.foxKifu=Fox game records\r\n"
                    + "Menu.rankMenu.base=Base:\r\n"
                    + "Menu.rankMenu.win=Win\r\n"
                    + "Menu.rankMenu.score=Score\r\n"
                    + "Menu.rankMenu.base.tooltips=Threshold are consistent with 'HawkEye-Statistics'\r\n"
                    + "Menu.readBoardGetFocus=Auto get focus when mouse wheel on candidates\r\n"
                    + "Menu.showMoveRankInOrigin=Not show rank mark on origin board(Board synchronization tool) \r\n"
                    + "Menu.rankMenu.lblCustomMove=Show last moves:\r\n"
                    + "Menu.rankMenu.rankNoneMove=None\r\n"
                    + "Menu.rankMenu.rankAllMove=Show all moves\r\n"
                    + "Menu.rankMenu.rankLastMove=Show last move\r\n"
                    + "Menu.rankMenu.setCustomMoves.message=Set last moves:\r\n"
                    + "Menu.rankMenu.setCustomMoves=Set custom last moves: \r\n"
                    + "Menu.moveRankMenu=Move rank mark (Alt+M)\r\n"
                    + "Menu.chkWRN.toolTopText=analysisWideRootNoise,only for analyze,click 'Params' for details. \r\n"
                    + "Menu.chkPDA.toolTopText=playoutDoublingAdvantage,click 'Params' for details. \r\n"
                    + "Menu.visualizedPanelSettings=Visualized panel settings\r\n"
                    + "Menu.scoreGame=Final score (Ctrl+Q)\r\n"
                    + "Menu.showCommentConrolPane=Show comment control bar\r\n"
                    + "Menu.minPlayoutsForNextMoveMessage=Low visits threshold:\r\n"
                    + "Menu.minPlayoutsForNextMove=Set low visits candidate threshold(Current: \r\n"
                    + "Menu.batchAnalysisMode=Batch analyze (Flash)\r\n"
                    + "Menu.changeEngineGameNumbers=Modify engine game batch numbers\r\n"
                    + "Menu.changeEngineGameNumbersTitle=Engine Game \r\n"
                    + "Menu.changeEngineGameNumbersMessage=Batch game numbers\r\n"
                    + "Menu.inputIntegerHint=Please input integer.\r\n"
                    + "Menu.nextMoveHint=Next move hint (J)\r\n"
                    + "Menu.nextMoveHintInformation=Circle with winRate/score difference(Not candidates or low visits candidate)\r\n"
                    + "Menu.nextMoveHintSimple=Simple circle\r\n"
                    + "Menu.nextMoveHintNone=None\r\n"
                    + "Menu.kataEstimateInPureNet=Display in raw net\r\n"
                    + "Menu.btnRankMark.toolTipText=Toggle show move rank mark (Alt+M)\r\n"
                    + "Menu.playBlackToolTipText=Add black(RightClick add white)\r\n"
                    + "Menu.playWhiteToolTipText=Add white(RightClick add black)\r\n"
                    + "Menu.playAlternatelyToolTipText=Play alternately\r\n"
                    + "Menu.playPassToolTipText=Play pass\r\n"
                    + "Menu.selectAllowToolTipText=Set calculation region(Alt+Mouse drag)\r\n"
                    + "Menu.selectAvoidToolTipText=Set not calculation region\r\n"
                    + "Menu.clearSelectToolTipText=Clear region(Alt+Mouse press)\r\n"
                    + "Menu.chkTime=Time\r\n"
                    + "Menu.chkPlayOut=V\r\n"
                    + "Menu.restartCurrentEngine=Restart current engine\r\n"
                    + "Menu.shutdownEngine=Shutdown engine\r\n"
                    + "Menu.shutdownCurrentEngine=Shutdown current engine\r\n"
                    + "Menu.shutdownOtherEngine=Shutdown other engines\r\n"
                    + "Menu.shutdownAllEngine=Shutdown all engines\r\n"
                    + "Menu.moreEngines=More engine...\r\n"
                    + "Menu.checkUpdate=Check update\r\n"
                    + "Menu.separateLblPda=PDA \r\n"
                    + "Menu.separateLblWrn=WRN \r\n"
                    + "Menu.rulesBtn=Rules\r\n"
                    + "Menu.tsumeGoBtn=Tsumego\r\n"
                    + "Menu.paramsBtn=Params\r\n"
                    + "Menu.gobanBtn=Goban\r\n"
                    + "Menu.saveBtn=Save\r\n"
                    + "Menu.lblShowAnalyze=Analyze\r\n"
                    + "Menu.lblShowCandidate=Candidate\r\n"
                    + "Menu.introduction=Manual\r\n"
                    + "Menu.introduction.fileName=readme_en.pdf\r\n"
                    + "Menu.Black=B\r\n"
                    + "Menu.White=W\r\n"
                    + "Menu.about=About\r\n"
                    + "Menu.editProgram=settings\r\n"
                    + "Menu.engineConfig=Engines (Alt+X)\r\n"
                    + "Menu.engineRules=Set engine rules(KataGo) (Shift+D)\r\n"
                    + "Menu.engineParameters=Set engine parameters (Alt+D)\r\n"
                    + "Menu.initSettings=Initialization settings\r\n"
                    + "Menu.comprehensiveSettings=Config (Shif+X)\r\n"
                    + "Menu.theme=Theme\r\n"
                    + "Menu.inVisible=Invisible\r\n"
                    + "Menu.visible=Visible\r\n"
                    + "Menu.customToolbar=Custom items\r\n"
                    + "Menu.showDoubleMenuBtn=Combine/Separate tool bar button\r\n"
                    + "Menu.playMoveTools=Play move tools\r\n"
                    + "Menu.allowAvoidTools=Allow/avoid region tools\r\n"
                    + "Menu.showBasicBtn=Basic buttons(Separated only)\r\n"
                    + "Menu.showGameController=Game controller\r\n"
                    + "Menu.showTimeLimitController=Max analyze per move(Time)(Seconds)\r\n"
                    + "Menu.showPlayoutLimitController=Max analyze per move(Playout)\r\n"
                    + "Menu.showAnalyzeController=Candidate analyze Controller\r\n"
                    + "Menu.showCandidateController=Candidate display Controller\r\n"
                    + "Menu.showCandidateInfoController=Candidate info Controller\r\n"
                    + "Menu.showRuleMenu=Rule\r\n"
                    + "Menu.showTsumeGoMenu=Tsumego\r\n"
                    + "Menu.showParamMenu=Parameter\r\n"
                    + "Menu.showGobanMenu=Goban\r\n"
                    + "Menu.showSaveLoadMenu=Save\r\n"
                    + "Menu.EnableEnterYikeGame=Enable yike live/room enter game room\r\n"
                    + "Menu.alwaysGoToLastMove=Always goto last move when a new move is played\r\n"
                    + "Menu.readBoardSettings=Board synchronization tool settings\r\n"
                    + "Menu.defaultPlatform=Default platform\r\n"
                    + "Menu.foxWeiqi=FoxWeiqi\r\n"
                    + "Menu.tygem=Tygem\r\n"
                    + "Menu.sina=Sina\r\n"
                    + "Menu.otherForeground=Other (foreground)\r\n"
                    + "Menu.otherBackground=Other (background)\r\n"
                    + "Menu.usePipeline=Use pipeline communication\r\n"
                    + "Menu.recognizeLastMove=Enable recognize last move (disable can be faster)\r\n"
                    + "Menu.defaultSyncBothSide=Default synchronize for both side\r\n"
                    + "Menu.alwaysKeepBoardStatSync=Always keep board state synchronized\r\n"
                    + "Menu.setSyncInterval=Set synchronization interval\r\n"
                    + "SGFParse.doubleEngineHint=This game record contains two engine's analysis,do you want enter double engine mode?\r\n"
                    + "SGFParse.doubleEngineHintTitle=Double engine game record\r\n"
                    + "SGFParse.blackAiScore=Black AI accuracy: \r\n"
                    + "SGFParse.whiteAiScore=White AI accuracy: \r\n"
                    + "SGFParse.blackTop10=Black mismatch(Top 10): \r\n"
                    + "SGFParse.whiteTop10=White mismatch(Top 10): \r\n"
                    + "SGFParse.black=Black\r\n"
                    + "SGFParse.white=White\r\n"
                    + "SGFParse.winrate=win rate:\r\n"
                    + "SGFParse.leadWithKomi=lead:\r\n"
                    + "SGFParse.leadJustScore=lead:\r\n"
                    + "SGFParse.playouts=visits\r\n"
                    + "SGFParse.stdev=stdev:\r\n"
                    + "SGFParse.komi=komi:\r\n"
                    + "SGFParse.pda=PDA: \r\n"
                    + "SGFParse.wrn=WRN: \r\n"
                    + "SGFParse.moveTime=Move time: \r\n"
                    + "SGFParse.startGameSgf=Start Sgf index:\r\n"
                    + "SGFParse.blackTotalTime=Black total time: \r\n"
                    + "SGFParse.whiteTotalTime=White total time: \r\n"
                    + "SGFParse.seconds=\\ seconds\r\n"
                    + "SGFParse.totalVisits=Total visits:  \r\n"
                    + "SGFParse.rules=Engine Rules: \r\n"
                    + "SGFParse.whiteRules=White Rules: \r\n"
                    + "SGFParse.blackRules=Black Rules: \r\n"
                    + "Menu.exchange=Exchange white/black (Ctrl+Shift+Alt+\\u2192)\r\n"
                    + "Menu.spinRight=Rotate right (Ctrl+Alt+\\u2192)\r\n"
                    + "Menu.spinLeft=Rotate left (Ctrl+Alt+\\u2190)\r\n"
                    + "Menu.mirrorVertical=Flip horizontally (Ctrl+Alt+\\u2191)\r\n"
                    + "Menu.mirrorHorizon=Flip vertically (Ctrl+Alt+\\u2193)\r\n"
                    + "Menu.shareCurrentSgf=Share current game (Ctrl+E) \r\n"
                    + "Menu.shareBatchSgf=Share batch games\r\n"
                    + "Menu.shareSearch=Search public game records\r\n"
                    + "Menu.shareEdit=Search(Edit) shared information\r\n"
                    + "Menu.shareHistoryLoacl=History(Local)\r\n"
                    + "Menu.shareHistoryLoaclHintEmpty=History is empty!\r\n"
                    + "Menu.shareHistoryLoaclHintOpenFailed=Open history failed!\r\n"
                    + "Menu.yikeLive=Yike live(Shift+O)\r\n"
                    + "Menu.yikeRoom=Yike room\r\n"
                    + "Menu.readBoard=Board synchronization tool(Alt+O)\r\n"
                    + "Menu.readBoardJava=Board synchronization tool(Simplified)\r\n"
                    + "Menu.lblLimit=Limit \r\n"
                    + "SetEstimateParam.setRemoteEngine=Settings\r\n"
                    + "RemoteEngineSettings.title=Remote Engine Settings\r\n"
                    + "RemoteEngineSettings.lblIP=IP\r\n"
                    + "RemoteEngineSettings.lblProt=Port\r\n"
                    + "RemoteEngineSettings.lblUserName=User name\r\n"
                    + "RemoteEngineSettings.chkUsePassword=Password\r\n"
                    + "RemoteEngineSettings.chkUseKeyGen=KeyGen\r\n"
                    + "RemoteEngineSettings.okButton=Confrim\r\n"
                    + "RemoteEngineSettings.scanKeyGen=Scan\r\n"
                    + "SSHController.connectFailed=Remote SSH engine connect error: Authentication failed!\r\n"
                    + "SSHController.engineFailed=Remote SSH engine connect failed\r\n"
                    + "MoreEngines.lblInitialCommand=Init Cmds:\r\n"
                    + "MoreEngines.initialCommandHint=Initial commands: (;-separated)\r\n"
                    + "MoreEngines.chooseKeygen=Choose keygen file\r\n"
                    + "MoreEngines.column8=SSH\r\n"
                    + "Menu.insertBlack=Insert black\r\n"
                    + "Menu.insertWhite=Insert white\r\n"
                    + "Menu.clearBoard=Clear board (Ctrl+Home)\r\n"
                    + "Menu.setAsMain=Set as main trunk (L)\r\n"
                    + "Menu.backToMainBranch=Back to main trunk (B)\r\n"
                    + "Menu.jumpToFirst=Go to first move (Home)\r\n"
                    + "Menu.jumpToLast=Go to last move (End)\r\n"
                    + "Menu.jumpToLeft=Go to left branch (\\u2190)\r\n"
                    + "Menu.jumpToRight=Go to right branch (\\u2192)\r\n"
                    + "Menu.delete=Delete move (Delete)\r\n"
                    + "Menu.deleteBranch=Delete branch (Shift+Delete)\r\n"
                    + "Menu.undoDelete=Undo delete (also in right click menu)\r\n"
                    + "Menu.redoDelete=Redo delete (also in right click menu)\r\n"
                    + "Menu.setInfo=Edit game info (I)\r\n"
                    + "Menu.setBoard=Set board size (Ctrl+I)\r\n"
                    + "Menu.showHeatmap=Show raw net (H)\r\n"
                    + "Menu.showPolicy=Show policy (T)\r\n"
                    + "Menu.estimateStones=Estimate (/ or numpad .)\r\n"
                    + "Menu.clearAllLizzieCache=Clear Lizzie cache (All moves) (Shift+A)\r\n"
                    + "Menu.clearThisLizzieCache=Clear Lizzie cache (This move)\r\n"
                    + "Menu.clearAllLizzieBestmoves=Clear analysis(All moves)\r\n"
                    + "Menu.clearThisLizzieBestmoves=Clear analysis(This move)\r\n"
                    + "Menu.addBlack=Add black\r\n"
                    + "Menu.addWhite=Add white\r\n"
                    + "Menu.alternatelyMoves=Play alternately\r\n"
                    + "Menu.allowDoubleClick=Enable double click finding move\r\n"
                    + "Menu.allowDrag=Enable drag stone\r\n"
                    + "Menu.allowClickReview=Enable click stone review\r\n"
                    + "Menu.playBestMove=Make AI play a best move (,)\r\n"
                    + "Menu.playPassMove=Pass (P)\r\n"
                    + "Menu.togglePonder=Pondering on/off (Space)\r\n"
                    + "Menu.makeAutoPlay=Auto play (Ctrl+A)\r\n"
                    + "Menu.autoAnalyze=Auto analyze (A)\r\n"
                    + "Menu.batchAnalyze=Batch analyze (Ctrl+O)\r\n"
                    + "Menu.stopAutoAnalyze=Stop auto(batch) analyze\r\n"
                    + "Menu.batchAnalyzeTable=Batch analyze progress table\r\n"
                    + "Menu.continueAnalyzeGameAsWhite=Continue game(AI plays black)(AnalyzeMode)(Alt+Enter)\r\n"
                    + "Menu.continueAnalyzeGameAsBlack=Continue game(AI plays white)(AnalyzeMode)(Alt+Enter)\r\n"
                    + "Menu.continueGenmoveGameAsWhite=Continue game(AI plays black)(Genmove)(Enter)\r\n"
                    + "Menu.continueGenmoveGameAsBlack=Continue game(AI plays white)(Genmove)(Enter)\r\n"
                    + "Menu.setAiTime=AI time use settings\r\n"
                    + "Menu.breakGame=Stop game (Space)\r\n"
                    + "Menu.intervention=Intervention engine game\r\n"
                    + "Menu.breakEngineGame=Stop engine game (ALT+R)\r\n"
                    + "Menu.pauseEngineGame=Pause/continue engine game (ALT+T)\r\n"
                    + "Menu.selectLimitCustomMoves=Limit variation moves\r\n"
                    + "Menu.selectLimitAllMoves=Limit variation all moves\r\n"
                    + "Menu.selectLimitOneMove=Limit variation first move\r\n"
                    + "Menu.deletePersistFile=Reset frame location\r\n"
                    + "menu.frameFontSizeOther=Other\r\n"
                    + "menu.setFrameSizeRestart=Set successfully,Please restart Lizzie\\!\r\n"
                    + "menu.setFrameSizeAlart=Language is not chinese,maybe some label can't be displayed completely!\r\n"
                    + "Menu.komi=Komi\r\n"
                    + "Menu.newGameBtn=NewGame\r\n"
                    + "Menu.pauseGameBtn=Pause\r\n"
                    + "Menu.continueGameBtn=Continue\r\n"
                    + "Menu.endGameBtn=StopGame\r\n"
                    + "Menu.resignBtn=Resign\r\n"
                    + "Menu.quickLinks=QuickLinks\r\n"
                    + "Menu.newGame=New game\r\n"
                    + "Menu.continueGameAgainstAi=Continue game against AI\r\n"
                    + "Menu.newAnalyzeModeGame=New game (Analyze mode) (Alt+N)\r\n"
                    + "Menu.newGenmoveGame=New game (Genmove mode) (N)\r\n"
                    + "Menu.newEngineGame=New engine game (Alt+E)\r\n"
                    + "Menu.kataEstimateBySize=Display occupancy by square size\r\n"
                    + "Menu.kataEstimateByTransparentSmall=Display occupancy by transparent(small)\r\n"
                    + "Menu.kataEstimateByTransparent=Display occupancy by transparent\r\n"
                    + "Menu.kataEstimateByTransparentNotOnLive=Display occupancy by transparent(exclude live stones)\r\n"
                    + "Menu.kataEstimateByBigSquare=Display occupancy by big square under stone\r\n"
                    + "Menu.useMovesOwnership=Use per-move ownership(at least v 1.11.0)\r\n"
                    + "Menu.useKataEstimateShortcut=Use short cut (.)\r\n"
                    + "Menu.kataEstimateSaveState=Remember display status\r\n"
                    + "Menu.showMouseOverWinrateGraph=Show mouse over winRate graph stuff(Black perspective only)\r\n"
                    + "ConfigDialog2.chooseColor=Choose a color\r\n"
                    + "ConfigDialog2.deleteThemeWarning=Delete theme \r\n"
                    + "ConfigDialog2.deleteThemeSuccess=Delete successfully\r\n"
                    + "ConfigDialog2.deleteThemeFailed=Delete failed\r\n"
                    + "ConfigDialog2.deleteTheme=Delete\r\n"
                    + "ConfigDialog2.duplicateThemeName=Duplicate theme name,please change name and try again!\r\n"
                    + "ConfigDialog2.inputThemeNameTitle=Theme Name\r\n"
                    + "ConfigDialog2.addTheme=New\r\n"
                    + "ConfigDialog2.triangle=TR\r\n"
                    + "ConfigDialog2.circle=CR\r\n"
                    + "ConfigDialog2.solid=SL\r\n"
                    + "ConfigDialog2.empty=NO\r\n"
                    + "ConfigDialog2.lblBestMoveColor=Best Move Color\r\n"
                    + "ConfigDialog2.lblBackgroundFilter=Background Filter\r\n"
                    + "ConfigDialog2.lblGomoku=Gomoku\r\n"
                    + "ConfigDialog2.SpecialCoords=Coords\r\n"
                    + "ConfigDialog2.SpecialCoordsNormal=Normal\r\n"
                    + "ConfigDialog2.SpecialCoordsWithI=Use I\r\n"
                    + "ConfigDialog2.SpecialCoordsFox=Fox Style\r\n"
                    + "ConfigDialog2.SpecialCoordsNumberFromTop=Num From Top\r\n"
                    + "ConfigDialog2.SpecialCoordsNumberFromBottom=Num From Bottom\r\n"
                    + "ConfigDialog2.btnSetOrder=Custom Order\r\n"
                    + "ConfigDialog2.btnSetDelay=Set Delay\r\n"
                    + "ConfigDialog2.lblShowMouseOverWinrateGraph=Show MouseOver WrGraph Stuff(Black Perspec)\r\n"
                    + "ConfigDialog2.lblShowWinRateOrScoreLeadLine=Show WinRate or ScoreLead Line \r\n"
                    + "ConfigDialog2.cbxShowWinrateOrScoreLeadLine.winRate=WinRate\r\n"
                    + "ConfigDialog2.cbxShowWinrateOrScoreLeadLine.scoreLead=ScoreLead\r\n"
                    + "ConfigDialog2.cbxShowWinrateOrScoreLeadLine.both=Both\r\n"
                    + "WaitForAnalysis.title=Analyzing,please wait...\r\n"
                    + "WaitForAnalysis.lblAnalsisProgress=Progress: Loading engine...\r\n"
                    + "WaitForAnalysis.btnSettings=Settings\r\n"
                    + "WaitForAnalysis.btnHide=Hide\r\n"
                    + "WaitForAnalysis.btnCancel=Cancel\r\n"
                    + "WaitForAnalysis.progress=Progress: \r\n"
                    + "AnalysisSettings.title=Flash Analysis Settings\r\n"
                    + "SetAnalysisRules.title=Analysis Rules Settings\r\n"
                    + "AnalysisSettings.example=Example:katago analysis -model model.bin.gz -config analysis.cfg -quit-without-waiting\r\n"
                    + "AnalysisSettings.lblMaxVisits=MaxVisits:\r\n"
                    + "AnalysisSettings.lblRules=Rules:\r\n"
                    + "AnalysisSettings.rdoUseCurrentRules=Use current engine rules (if no rules,use chinese rules)\r\n"
                    + "AnalysisSettings.rdoUseSpecificRules=Use specific rules\r\n"
                    + "AnalysisSettings.chkPreLoad=Preload on Lizzie start\r\n"
                    + "AnalysisSettings.chkAutoExit=Quit engines after analysis finished\r\n"
                    + "AnalysisSettings.chkAlwaysOverride=Always override exists analyze results\r\n"
                    + "AnalysisSettings.btnSetRules=Set Rules\r\n"
                    + "AnalysisSettings.btnConfirmAndRedo=ReAnalyze\r\n"
                    + "AnalysisSettings.btnConfirm=Confirm\r\n"
                    + "AnalysisSettings.lblEngineCmd=Command:\r\n"
                    + "AnalysisPartGame.title=Flash Analyze\r\n"
                    + "AnalysisPartGame.btnStart=Start\r\n"
                    + "AnalysisPartGame.startMove=Start Move:\r\n"
                    + "AnalysisPartGame.endMove=End Move:\r\n"
                    + "AnalysisPartGame.lblNotice=Tip: empty means analyze from first to last move.\r\n"
                    + "AnalysisEngine.analyzeComplete=Analyze Complete\r\n"
                    + "AnalysisEngine.flashAnalyze=Flash_Analyze\r\n"
                    + "EngineFaied.empty=Engine command is empty\r\n"
                    + "FontList.systemDefault=System Default\r\n"
                    + "FontList.lizzieDefault=Lizzie Default\r\n"
                    + "EngineFailedMessage.engineCmd=Command:\r\n"
                    + "EngineFailedMessage.btnRunInCmd=here\r\n"
                    + "EngineFailedMessage.lblClick=Click\r\n"
                    + "EngineFailedMessage.lblRunInCmd=to run engine in command,for diagnostic.\r\n"
                    + "EngineFailedMessage.batTips=This window will try to execute engine and request play moves.A board with one or two stones will be shown,otherwise there is something wrong or the engine does not support GTP command. \r\n"
                    + "EngineFailedMessage.btnRestart=Restart\r\n"
                    + "AnalysisFrame.actual=actual\r\n"
                    + "AnalysisFrame.checkGraph=Graph\r\n"
                    + "AnalysisFrame.checkList=List\r\n"
                    + "AnalysisFrame.checkShowNext=Current\r\n"
                    + "AnalysisFrame.checkUseMouseMove=MousePoint\r\n"
                    + "AnalysisFrame.column1=Sort\r\n"
                    + "AnalysisFrame.column2=Coord\r\n"
                    + "AnalysisFrame.column3=Lcb(%)\r\n"
                    + "AnalysisFrame.column4=WinRate(%)\r\n"
                    + "AnalysisFrame.column5=Visits\r\n"
                    + "AnalysisFrame.column6=Percents(%)\r\n"
                    + "AnalysisFrame.column7=Policy(%)\r\n"
                    + "AnalysisFrame.column8=ScoreLead\r\n"
                    + "AnalysisFrame.column9=ScoreStdev\r\n"
                    + "AnalysisFrame.concentration=Concentration\\:\r\n"
                    + "AnalysisFrame.exclude=exclude\r\n"
                    + "AnalysisFrame.maxVisits=maxVisits\\:\r\n"
                    + "AnalysisFrame.title=Candidates list,click show variation,up pre/down next,Q toggle top\r\n"
                    + "AnalysisFrame.titleMain=Candidates list(main),click show variation,up pre/down next,Q toggle top\r\n"
                    + "AnalysisFrame.titleSub=Candidates list(sub),click show variation,up pre/down next,Q toggle top\r\n"
                    + "AnalysisFrame.totalVisits=TotalVisits\\:\r\n"
                    + "AnalysisTable.clearAllFiles=Clear\r\n"
                    + "AnalysisTable.stopStartAnalysisMode=Start/Stop(Flash)\r\n"
                    + "AnalysisTable.addFile=Add Files\r\n"
                    + "AnalysisTable.column1=Sort\r\n"
                    + "AnalysisTable.column2=File\r\n"
                    + "AnalysisTable.current=Current\r\n"
                    + "AnalysisTable.delete=Delete\r\n"
                    + "AnalysisTable.down=Down\r\n"
                    + "AnalysisTable.fileDialog=Choose file\r\n"
                    + "AnalysisTable.prior=Prior\r\n"
                    + "AnalysisTable.stopGo=Pause/Go\r\n"
                    + "AnalysisTable.stopStart=Start/Stop\r\n"
                    + "AnalysisTable.title=Batch analyze table\r\n"
                    + "AnalysisTable.up=Up\r\n"
                    + "AutoPlay.chkContinueWithBestMove=Play best candidate by end\r\n"
                    + "AutoPlay.chkDirectlyWithBestMove=Play best candidate\r\n"
                    + "AutoPlay.chbDisplayEntireVariationFirst=EntireVariationsFirst(second)\r\n"
                    + "AutoPlay.chkAutoPlayMainbord=MainBoard(second)\r\n"
                    + "AutoPlay.chkAutoPlaySubbord=SubBoard(MilliSecond)\r\n"
                    + "AutoPlay.chkAutoPlayBranch=Branch(MilliSecond)\r\n"
                    + "AutoPlay.title=Set Auto Play\r\n"
                    + "AutoPlay.okButton=Confirm\r\n"
                    + "BoardRenderer.pass=Pass\r\n"
                    + "BoardRenderer.pureNetWhiteWinrate=RawNet\\: WhiteWinRate \r\n"
                    + "BoardRenderer.pureNetWinrate=RawNet Winrate\\: \r\n"
                    + "BoardRenderer.noPureNetWinrate=No RawNet Winrate\r\n"
                    + "BoardRenderer.symmetry=Symmetry \r\n"
                    + "BoardRenderer.whiteScore=WhiteScoreLead \r\n"
                    + "BottomToolbar.finalScore=FinalScore\r\n"
                    + "BottomToolbar.noticeBigDiff=Discovered big difference moves: \r\n"
                    + "BottomToolbar.isAutoAnalyzing=Auto analyze is processing,please stop it first!\r\n"
                    + "BottomToolbar.needAnalyze=need analyze \r\n"
                    + "BottomToolbar.startReAnalyze=start enhance analysis\r\n"
                    + "BottomToolbar.askStartReAnalyze=start enhance analysis?\r\n"
                    + "BottomToolbar.askStartReAnalyzeTitle=Start enhance analysis?\r\n"
                    + "BottomToolbar.tryplayBack=Back\r\n"
                    + "BottomToolbar.flashAnalyze=FlashAnalyze\r\n"
                    + "BottomToolbar.analyse=StartAnalyze\r\n"
                    + "BottomToolbar.pauseAnalyse=StopAnalyze\r\n"
                    + "BottomToolbar.analyzeList=AnalyzeList\r\n"
                    + "BottomToolbar.autoPlay=AutoPlay\r\n"
                    + "BottomToolbar.backMain=BackMain\r\n"
                    + "BottomToolbar.badMoves=HawkEye\r\n"
                    + "BottomToolbar.batchOpen=AutoAnalyze\r\n"
                    + "BottomToolbar.clearButton=Clear\r\n"
                    + "BottomToolbar.coords=Coords\r\n"
                    + "BottomToolbar.countButton=Estimate\r\n"
                    + "BottomToolbar.deleteMove=Delete\r\n"
                    + "BottomToolbar.editHistoryRemote=Search(Edit) shared information\r\n"
                    + "BottomToolbar.genmoveStopHint=In genmove mode,need wait a move afrer paused.\r\n"
                    + "BottomToolbar.gotomove=Goto\r\n"
                    + "BottomToolbar.heatMap=RawNet\r\n"
                    + "BottomToolbar.kataEstimate=KataEstimate\r\n"
                    + "BottomToolbar.linkHistoryFail=open failed\r\n"
                    + "BottomToolbar.linkHistoryHint=history is empty\r\n"
                    + "BottomToolbar.liveButton=Sync\r\n"
                    + "BottomToolbar.move=MoveNumber\r\n"
                    + "BottomToolbar.moveRank=MoveRank\r\n"
                    + "BottomToolbar.openfile=Open\r\n"
                    + "BottomToolbar.refresh=Refresh\r\n"
                    + "BottomToolbar.savefile=Save\r\n"
                    + "BottomToolbar.setMain=SetAsMain\r\n"
                    + "BottomToolbar.share=Share\r\n"
                    + "BottomToolbar.shareBatchSgf=Batch share games\r\n"
                    + "BottomToolbar.shareCurSgf=Share current game\r\n"
                    + "BottomToolbar.shareHistory=Share history (local)\r\n"
                    + "BottomToolbar.shareHistoryRemote=Search public game records \r\n"
                    + "BottomToolbar.stopAutoAnaHint=Auto analyze complete.\r\n"
                    + "BottomToolbar.syncBoard=Board synchronization tool\r\n"
                    + "BottomToolbar.syncBoardJava=Board synchronization tool(Simplified)\r\n"
                    + "BottomToolbar.tryPlay=Try\r\n"
                    + "BottomToolbar.yikeLive=Yike live\r\n"
                    + "BottomToolbar.yikeRoom=Yike room\r\n"
                    + "Byoyomi.byoyomi=Byoyomi \r\n"
                    + "Byoyomi.emptyTimeHint=Time and byoyomi can't be none at same time\\!\r\n"
                    + "Byoyomi.newGame.byoyomi=byoyomi(sec)\r\n"
                    + "Byoyomi.newGame.byoyomiTimes=times\r\n"
                    + "Byoyomi.newGame.limitMyTime=Limit My Time\r\n"
                    + "Byoyomi.newGame.saveTime=Save time(min)\r\n"
                    + "Byoyomi.none=None\r\n"
                    + "Byoyomi.time=Time \r\n"
                    + "Byoyomi.timeOutBlack=W+R,black time out\r\n"
                    + "Byoyomi.timeOutWhite=B+R,white time out\r\n"
                    + "CheckVersion.checkEveryDay=AlwaysCheck\r\n"
                    + "CheckVersion.copyDownload=copyDownload\r\n"
                    + "CheckVersion.copyFailed=Copy failed.Use Ctrl+C\r\n"
                    + "CheckVersion.copyQQgroup=copyQQgroup\r\n"
                    + "CheckVersion.copySuccess=Copy successfully\\!\r\n"
                    + "CheckVersion.currentVersion=CurrentVersion\\:\r\n"
                    + "CheckVersion.download=New version download\\:\r\n"
                    + "CheckVersion.download2=Can also download at QQ group file\\r\\nQQ group\\: 246284327(group3),867298807(group2 full),786173361(group1 full)\r\n"
                    + "CheckVersion.ignore=ignoreVersion\r\n"
                    + "CheckVersion.newVersion=New version\\:\r\n"
                    + "CheckVersion.newVersionHint=New version\\!Please update\r\n"
                    + "CheckVersion.newestVersion=NewestVersion\\:\r\n"
                    + "CheckVersion.noNewVersionHint=No newer version\r\n"
                    + "CheckVersion.titile=Check Version\r\n"
                    + "ChooseMoreEngine.column1=Idx\r\n"
                    + "ChooseMoreEngine.column2=name\r\n"
                    + "ChooseMoreEngine.column3=command\r\n"
                    + "ChooseMoreEngine.lblchooseStart=Auto load\\:\r\n"
                    + "ChooseMoreEngine.lblrdoDefault=Selected engine\r\n"
                    + "ChooseMoreEngine.lblrdoLast=Last exited engine\r\n"
                    + "ChooseMoreEngine.lblrdoMannul=Manually\r\n"
                    + "ChooseMoreEngine.lblrdoNone=No engine\r\n"
                    + "ChooseMoreEngine.newEngine=New engine\r\n"
                    + "ChooseMoreEngine.ok=Switch\r\n"
                    + "ChooseMoreEngine.selectHint=Please select an engine\r\n"
                    + "ChooseMoreEngine.title=Choose an engine(Double click\\=switch)\r\n"
                    + "ConfigDialog2.lblAdvanceTime=Advance Time Setting\r\n"
                    + "ConfigDialog2.lblBackgroundPonder=Background Ponder\r\n"
                    + "ConfigDialog2.lblCentiseconds.text=centiseconds\r\n"
                    + "ConfigDialog2.lblEngineFastSwitch=Fast Switch Engine\r\n"
                    + "ConfigDialog2.lblPvVisitsLimit=Show Variation Visits Minimum\r\n"
                    + "ConfigDialog2.lblShowPvVisits=Show Variation Visits Left\r\n"
                    + "ConfigDialog2.lblVariationRemoveDeadChain=Remove Dead Chain In Variation\r\n"
                    + "ConfigDialog2.no=No\r\n"
                    + "ConfigDialog2.rdoNoFastSwitch.text=No\r\n"
                    + "ConfigDialog2.yes=Yes\r\n"
                    + "ConfigDialog2.chkNoCapture.content=If checked gomoku,Lizzie will be not able to analyze Go game.\r\n"
                    + "ConfigDialog2.chkNoCapture.title=Gomoku?\r\n"
                    + "EditShareFrame.btnCancel=Cancel\r\n"
                    + "EditShareFrame.btnDelete=Delete\r\n"
                    + "EditShareFrame.btnEdit=Edit\r\n"
                    + "EditShareFrame.checkBoxPublic=Public\r\n"
                    + "EditShareFrame.deleteFail=Delete failed\r\n"
                    + "EditShareFrame.deleteHint1=If deleted,Can't open game record from website,confirm?\r\n"
                    + "EditShareFrame.deleteHint2=Confirm\r\n"
                    + "EditShareFrame.deleteSuccess=Delete successfully\r\n"
                    + "EditShareFrame.editFail=Edit failed\r\n"
                    + "EditShareFrame.editSucess=Edit succeeded\r\n"
                    + "EditShareFrame.labelBlack=Black\r\n"
                    + "EditShareFrame.labelNotice=Tip\\: Don't use symbol (like><|\\\\/., etc..)\r\n"
                    + "EditShareFrame.labelWhite=White\r\n"
                    + "EditShareFrame.labellabel=Label\r\n"
                    + "EditShareFrame.labelother=Other\r\n"
                    + "EditShareFrame.labeluploader=Uploader\r\n"
                    + "EditShareFrame.other=other\r\n"
                    + "EditShareFrame.title=Edit share infomation\r\n"
                    + "EngineManager.parseAdvcanceTimeSettingsFailed=Parse advanced time settings failed,can not send right left time to engine,engine game can still go on.\r\n"
                    + "EngineManager.engine=engine\r\n"
                    + "EngineManager.engineGameBlackSettingWrong=Black play settings wrong!\r\n"
                    + "EngineManager.engineGameWhiteSettingWrong=White play settings wrong!\r\n"
                    + "EngineManager.engineGameSameEngine=Can't choose same engine\r\n"
                    + "EngineManager.sameEngineHint=Can't choose same engine\r\n"
                    + "EngineManager.subEngine=(Sub)Engine\r\n"
                    + "EngineManager.engineGamePlaying=Playing\r\n"
                    + "EngineManager.doublePassFileName=_DoublePassGame\r\n"
                    + "EngineManager.outOfMoveFileName=_OutOfMoveGame\r\n"
                    + "EngineGameInfo.timeVisitsTips=Tips: Because of the delay and interval in receiving candidates,actually time will be smaller and visits maybe bigger.\r\n"
                    + "EngineGameInfo.notEnoughGames=not enough games\r\n"
                    + "EngineGameInfo.openingSGFindex=SgfOpenIndex_\r\n"
                    + "EngineGameInfo.rules=Rules\r\n"
                    + "EngineGameInfo.gameFinished=Game finished,\r\n"
                    + "EngineGameInfo.finishedByMoves=\\ out of moves\r\n"
                    + "EngineGameInfo.finishedByDoublePass=\\ double pass game\r\n"
                    + "EngineGameInfo.finishedWin=\\ win\r\n"
                    + "EngineGameInfo.exchange=\\ Exchange black/white: \r\n"
                    + "EngineGameInfo.maxMoves=\\ Max moves: \r\n"
                    + "EngineGameInfo.analyzeMode=AnalyzeMode\r\n"
                    + "EngineGameInfo.randomPlay1=\\ Random play: first \r\n"
                    + "EngineGameInfo.randomPlay2=\\ moves,winrate not less than best move \r\n"
                    + "EngineGameInfo.randomPlay3=,visits not less than best move \r\n"
                    + "EngineGameInfo.totalTime=\\  total time: \r\n"
                    + "EngineGameInfo.result.totalVisits=\\ total visits: \r\n"
                    + "EngineGameInfo.otherSettings=Ohter settings: \r\n"
                    + "EngineGameInfo.totalGames=\\ Max Games: \r\n"
                    + "EngineGameInfo.genmoveMode=GenmoveMode\r\n"
                    + "EngineGameInfo.komi=\\ Komi: \r\n"
                    + "EngineGameInfo.continueGame=\\ Continue game: \r\n"
                    + "EngineGameInfo.yes=yes\r\n"
                    + "EngineGameInfo.no=no\r\n"
                    + "EngineGameInfo.doublePassGame=Double pass games: \r\n"
                    + "EngineGameInfo.outOfMoveGame=Out of move games: \r\n"
                    + "EngineGameInfo.batchGameEndAndScore=Batch engine game finished,Score is \r\n"
                    + "EngineGameInfo.engineGameEndHintKifuPos=Game record saved : \r\n"
                    + "EngineGameInfo.oneStdev=One Approx Stand Error: \r\n"
                    + "EngineGameInfo.twoStdev=Two Approx Stand Error: \r\n"
                    + "EngineGameInfo.threeStdev=Three Approx Stand Error: \r\n"
                    + "EngineGameInfo.titleWinRate=winRate \r\n"
                    + "EngineGameInfo.sgfStartNumber=Use SGF open: yes    SFG count: \r\n"
                    + "EngineGameInfo.sgfStartOpen=Open\r\n"
                    + "EngineGameInfo.engine1=First engine\r\n"
                    + "EngineGameInfo.engine2=Second engine\r\n"
                    + "EngineGameInfo.allWins=All wins \r\n"
                    + "EngineGameInfo.sgfStartBlackWin=\\ black wins \r\n"
                    + "EngineGameInfo.sgfStartWhiteWin=\\ white wins \r\n"
                    + "EngineGameInfo.totalGameResults=Total games: \r\n"
                    + "EngineGameInfo.gameScore=\\ score: \r\n"
                    + "EngineGameInfo.gameWinrate=\\ win rate: \r\n"
                    + "EngineGameInfo.secondEngineElo=Second engine elo(First engine elo=0): \r\n"
                    + "EngineGameInfo.elo100Wr=100% win rate can't calculate elo\r\n"
                    + "EngineGameInfo.backgroundPonder=\\ Background ponder: \r\n"
                    + "EngineGameInfo.yes=yes\r\n"
                    + "EngineGameInfo.no=no\r\n"
                    + "EngineGameInfo.settingFirst=First engine settings: \r\n"
                    + "EngineGameInfo.settingSecond=Second engine settings: \r\n"
                    + "EngineGameInfo.time=Time: \r\n"
                    + "EngineGameInfo.command=Command: \r\n"
                    + "EngineGameInfo.totalVisits=\\ Total visits: \r\n"
                    + "EngineGameInfo.firstVisits=\\ BestMove visits: \r\n"
                    + "EngineGameInfo.resignThreshold=Resign threshold: Min move \r\n"
                    + "EngineGameInfo.resignThreshold2=,constant \r\n"
                    + "EngineGameInfo.resignThreshold3=\\ moves,winRate below \r\n"
                    + "EnginePkConfig.cancelButton=Cancel\r\n"
                    + "EnginePkConfig.chkAutosave=SaveGame\r\n"
                    + "EnginePkConfig.chkExchange=Exchange black/white\r\n"
                    + "EnginePkConfig.chkRandomMove=Random\\:first\r\n"
                    + "EnginePkConfig.chkRandomMoveVists=Visits above best *\r\n"
                    + "EnginePkConfig.chkSatartNum=StartNum(default\\:1)\r\n"
                    + "EnginePkConfig.chkSaveWinrate=SaveWinGraph\r\n"
                    + "EnginePkConfig.lblGameMAX=MaxMove\r\n"
                    + "EnginePkConfig.lblGameMIN=MinMove\r\n"
                    + "EnginePkConfig.lblRandomWinrate=,WRate above best -\r\n"
                    + "EnginePkConfig.lblnameSetting=Batch game folder name(oneTime)\\:\r\n"
                    + "EnginePkConfig.lblresignGenmove=In genmove mode,set visits caps, resign threshold etc in engine's parameters,mode can be changed in 'moreSettings'\r\n"
                    + "EnginePkConfig.lblresignSetting2=constant\r\n"
                    + "EnginePkConfig.lblresignSetting3=winRate below\r\n"
                    + "EnginePkConfig.lblresignSettingBlack=Black resign threshold\\:\r\n"
                    + "EnginePkConfig.lblresignSettingWhite=White resign threshold\\:\r\n"
                    + "EnginePkConfig.lblresignSettingConsistent=minMove\r\n"
                    + "EnginePkConfig.okButton=Confirm\r\n"
                    + "EnginePkConfig.rdoAna=AnalyzeMode\r\n"
                    + "EnginePkConfig.rdoGenmove=GenmoveMode\r\n"
                    + "EnginePkConfig.textAreaHint=Tip\\:\\r\\nIf out of moves or both passed,game will be saved but will not be recorded in score.\\r\\nIn GenmoveMode,engine will use genmove command,Please add --noponder(leela),disable ponder(katago).\\r\\nIn AnalyzeMode,engine will use lz-analyze or kata-analyze command,and Place the best move.\r\n"
                    + "EnginePkConfig.title=Engine game config\r\n"
                    + "EnginePkConfig.chkPreviousBestmovesOnlyFirstMove=Only best candidate\r\n"
                    + "EnginePkConfig.rdoLastMove=Previous move\r\n"
                    + "EnginePkConfig.rdoCurrentMove=Current move\r\n"
                    + "EnginePkConfig.lblChooseBestMoves=Display candidates:\r\n"
                    + "Config.deletePersistFile=Reset frame location succeed,please restart Lizzie!\r\n"
                    + "EstimateResults.Black=B\r\n"
                    + "EstimateResults.White=W\r\n"
                    + "EstimateResults.territoryPoints=territory\r\n"
                    + "EstimateResults.territoryCaptured=captured\r\n"
                    + "EstimateResults.territoryDifference=difference\r\n"
                    + "EstimateResults.areaPoints=areas\r\n"
                    + "EstimateResults.areaAlives=alives\r\n"
                    + "EstimateResults.areaSums=sums\r\n"
                    + "EstimateResults.area=\\ stones\r\n"
                    + "EstimateResults.autoEstimate=AutoEst\r\n"
                    + "EstimateResults.closeEstimate=HideEst\r\n"
                    + "EstimateResults.captures=captures\r\n"
                    + "EstimateResults.estimate=Estimate\r\n"
                    + "EstimateResults.lead=Lead\\: \r\n"
                    + "EstimateResults.points=\\ points\r\n"
                    + "EstimateResults.pts=pts\r\n"
                    + "EstimateResults.stopEstimate=StopEst\r\n"
                    + "EstimateResults.territoryMode=Territory\r\n"
                    + "EstimateResults.areaMode=Area\r\n"
                    + "EstimateResults.title=Estimate(without komi)\r\n"
                    + "FileFilterTest1.getDescription=weight file\r\n"
                    + "FileFilterTest2.getDescription=config file\r\n"
                    + "FirstUseSettings.lblLooks=Looks\r\n"
                    + "FirstUseSettings.rdoJavaLooks=Java\r\n"
                    + "FirstUseSettings.rdoSysLooks=System\r\n"
                    + "FirstUseSettings.looksHelperTitle=About Looks\r\n"
                    + "FirstUseSettings.looksHelperContent=You can try Java looks if some texts can't be displayed completely when using system looks.\r\n"
                    + "FirstUseSettings.chkLimitTime=Time(s)\r\n"
                    + "FirstUseSettings.chkLimitVisits=Visits\r\n"
                    + "FirstUseSettings.confirmHintNoSuggestionInfo=Please select at least one item in candidates info.\r\n"
                    + "FirstUseSettings.lblLanguage=Language(\\u8BED\\u8A00)\r\n"
                    + "FirstUseSettings.rdoLanguageChinese=\\u4E2D\\u6587\r\n"
                    + "FirstUseSettings.rdoLanguageEnglish=English\r\n"
                    + "FirstUseSettings.rdoLanguageKorean=\\uD55C\\uAD6D\\uC5B4\r\n"
                    + "FirstUseSettings.rdoLanguageJapanese=\\u65E5\\u672C\\u8A9E\r\n"
                    + "FirstUseSettings.lblSuggestionInfo=Candidates info\r\n"
                    + "FirstUseSettings.chkWinrate=WinRate\r\n"
                    + "FirstUseSettings.chkVisits=Visits\r\n"
                    + "FirstUseSettings.chkScoreLead=ScoreLead\r\n"
                    + "FirstUseSettings.btnCustomOrder=Custom display order\r\n"
                    + "FirstUseSettings.btnApply=Confirm\r\n"
                    + "FirstUseSettings.confirmHint=There are unselected options\r\n"
                    + "FirstUseSettings.confirmHint2=Wrong input format\r\n"
                    + "FirstUseSettings.defaultSettings=Load defaults\r\n"
                    + "FirstUseSettings.lblHint=Hint\\:Use mouse wheel to control game and variations forward / backward\r\n"
                    + "FirstUseSettings.lblLimitSuggestion=Limit max candidates\r\n"
                    + "FirstUseSettings.lblLimitVariation=Limit variation length\r\n"
                    + "FirstUseSettings.lblLizzieCache=Enable lizzie cache\r\n"
                    + "FirstUseSettings.lblMaxAnalyzeTime=Max Analyze Limit\r\n"
                    + "FirstUseSettings.lblMouseOverSubboard=When mouse over subBoard,subBoard\r\n"
                    + "FirstUseSettings.lblMouseOverSuggestion=When mouse over candidate,variation\r\n"
                    + "FirstUseSettings.lblNotice=Noitce\\:0 means no limit,you can change these settings on Menu-Settings-Initialize settings\r\n"
                    + "FirstUseSettings.lblShowScore=Show score on board as\r\n"
                    + "FirstUseSettings.lblWinratePerspective=WinRate(Score) perspective\r\n"
                    + "FirstUseSettings.lblShowPvVists=Show variation visits left(KataGo v1.60 or newer)\r\n"
                    + "FirstUseSettings.lizzieCacheDiscribe=When you go back,sometimes engine forgets previous calculation results and will start form 0 visits,if enable lizzie cache,lizzie will display previous results until engine's new calculation has bigger visits than previous.\r\n"
                    + "FirstUseSettings.lizzieCacheDiscribeTitle=Lizzie cache description\r\n"
                    + "FirstUseSettings.maxAnalyzeTimeDiscribe=If consistently analyze one move over max analyze time/visits,analysis will be interrupted automatic,press space or make a new move will continue analyzing.\r\n"
                    + "FirstUseSettings.maxAnalyzeTimeDiscribeTitile=Max analyze time description\r\n"
                    + "FirstUseSettings.rdoEveryPvVistits=All moves\r\n"
                    + "FirstUseSettings.rdoLastPvVistits=Last move\r\n"
                    + "FirstUseSettings.rdoLizzieCacheDisable=Disable\r\n"
                    + "FirstUseSettings.rdoLizzieCacheEnable=Enable\r\n"
                    + "FirstUseSettings.rdoMouseOverSubboardNoRefresh=Not refresh until mouse move out(or key G)\r\n"
                    + "FirstUseSettings.rdoMouseOverSubboardRefresh=Keep refresh\r\n"
                    + "FirstUseSettings.rdoMouseOverSuggestionNoRefresh=Not refresh until mouse move out(or key G)\r\n"
                    + "FirstUseSettings.rdoMouseOverSuggestionRefresh=Keep refresh\r\n"
                    + "FirstUseSettings.rdoNoPvVistits=None\r\n"
                    + "FirstUseSettings.rdoScoreOnBoardWithKomi=Score + komi\r\n"
                    + "FirstUseSettings.rdoScoreOnBoardJustScore=Just score\r\n"
                    + "FirstUseSettings.rdoAlwaysBlack=Always black\r\n"
                    + "FirstUseSettings.rdoAlternately=Alternately\r\n"
                    + "FirstUseSettings.title=Initialize settings\r\n"
                    + "GameInfo.me=Me\r\n"
                    + "GameInfo.untitled=Untitled\r\n"
                    + "GameInfoDialog.black=Black\r\n"
                    + "GameInfoDialog.handicap=Handicap\r\n"
                    + "GameInfoDialog.komi=Komi\r\n"
                    + "GameInfoDialog.okButton=OK\r\n"
                    + "GameInfoDialog.title=GameInfo\r\n"
                    + "GameInfoDialog.white=White\r\n"
                    + "GtpConsolePane.isEngineGame=Can not input command during engine game!\r\n"
                    + "GtpConsolePane.wrongPrevious=Can not undo!\r\n"
                    + "GtpConsolePane.wrongParameters=Wrong parameters!\r\n"
                    + "GtpConsolePane.noCommands=Can not get current engine's supported commands.\r\n"
                    + "GtpConsolePane.clear=Clear\r\n"
                    + "GtpConsolePane.commands=Cmds\r\n"
                    + "GtpConsolePane.send=Send\r\n"
                    + "GtpConsolePane.estimateEngine=EstimateEngine\r\n"
                    + "GtpConsolePane.title=Gtp Console\r\n"
                    + "IndependentMainBoard.title=Main board\r\n"
                    + "IndependentSubBoard.title=Sub board\r\n"
                    + "KataEstimate.errorHint=Load estimate engine failed,please check engine command\r\n"
                    + "KataEstimate.errorHint2=Estimate engine command error, or Did not find \"Zen.dll\"\\!\r\n"
                    + "leelaz.stopByLimitTitle=Ponder Stopped\r\n"
                    + "leelaz.stopByLimit=Ponder Paused by time or visits limit,Push spacebar to resume.\r\n"
                    + "leelaz.stopByLimit2=Click 'Settings-Configs-Engine Settings' menu to change limit conditions. \r\n"
                    + "Leelaz.openclPlatfromNotFound=Can not found OpenCl platform,Maybe need reinstall GPU driver.\r\n"
                    + "Leelaz.updateZenGtp=<html>ZenGTP engine is out-of-date,please update!<br />Download:<a color=\"blue\" href=\"https://github.com/yzyray/ZenGTP/releases\">&nbsp;here</a></html>\r\n"
                    + "Leelaz.kataGoPerformance=<html>First time of running KataGo is detected.<br />You may need to optimize the settings such as numSearchThreads.<br />Reference:<a color=\"blue\" href=\"https://github.com/lightvector/KataGo#tuning-for-performance\">&nbsp;here</a></html>\r\n"
                    + "Leelaz.engineEndUnormalHint=Engine process ended unintentionally for some reason.You may find more information in GTP console(hotkey e).\r\n"
                    + "Leelaz.engineStartNoExceptionMessage=(No message)\r\n"
                    + "Leelaz.analyzed=Analyzed\r\n"
                    + "Leelaz.autoAnalyzeComplete=Auto Analyze Complete,SGF saved in\\:\r\n"
                    + "Leelaz.batchAutoAnalyzeComplete=Batch Auto Analyze Complete,SGF saved in the same folder with original game record.\r\n"
                    + "Leelaz.black=black\r\n"
                    + "Leelaz.blackWin=B+R\r\n"
                    + "Leelaz.blackWinAiResign=AI Resign,Black Win\r\n"
                    + "Leelaz.encryption=encryption\r\n"
                    + "Leelaz.engineFailed=Failed to start the engine\r\n"
                    + "Leelaz.resign=Resign\\!\r\n"
                    + "Leelaz.result=result\r\n"
                    + "Leelaz.white=white\r\n"
                    + "Leelaz.whiteWin=W+R\r\n"
                    + "Leelaz.whiteWinAiResign=AI Resign,White Win\r\n"
                    + "Leelaz.win=Win\r\n"
                    + "Lizzie.defaultFontName=Dialog.plain\r\n"
                    + "Lizzie.save.error=Save config or persist file failed! Error message: \r\n"
                    + "Lizzie.save.path=Path: \r\n"
                    + "Lizzie.isChinese=no\r\n"
                    + "Lizzie.alwaysOnTopTitle=[Top]\r\n"
                    + "Lizzie.hint.restart=Please restart Lizzie to apply changes.\r\n"
                    + "Lizzie.hint.restartForPartChanges=Part of changes can't be applied before restarting Lizzie.\r\n"
                    + "Lizzie.askOnExit1=Save SGF?\r\n"
                    + "Lizzie.askOnExit2=Save SGF?\r\n"
                    + "Lizzie.engineFailed=Engine load failed,Running empty engine mode\r\n"
                    + "ConfigDialog2.lblVariationReplayInterval=Variation Replay Interval(ms)\r\n"
                    + "LizzieChangeMove.txtMoveNumber.error=Invalid number\\!\r\n"
                    + "LizzieConfig.about.lblLizzieInfo=<html><b>Lizzie</b> is a free and open-source Go graphical interface allowing the user to analyze games in real time using like KataGo or Leela Zero.<br /><br /><table><tr><td>Author:</td><td><a href=\\\"https://github.com/featurecat\\\">featurecat</a></td></tr><tr><td>Source code:</td><td><a href=\\\"https://github.com/featurecat/lizzie\\\">https://github.com/featurecat/lizzie</a></td></tr><tr><td>LICENSE:</td><td><a href=\\\"https://github.com/featurecat/lizzie/blob/master/LICENSE.txt\\\">GPL-3.0</a></td></tr><tr><td>Contributors:</td><td><a href=\\\"https://github.com/featurecat/lizzie/graphs/contributors\\\">zsalch cngoodboy kaorahi bittsitt dfannius OlivierBlanvillain yzyray toomasr TFiFiE</a></td></tr><tr><td></td><td><a href=\\\"https://github.com/featurecat/lizzie/graphs/contributors\\\">apetresc aerisnju kuba97531 bvandenbon typohh typohh Ka-zam alreadydone odCat</a></td></tr></td></tr><tr><td></td><td><a href=\\\"https://github.com/featurecat/lizzie/graphs/contributors\\\">inohiroki ParmuzinAlexander gjm11 tomasz-warniello rexl2018 Yi-Kai infinity0 objt-ba pliuphys ygrek</a></td></tr></table></html>\r\n"
                    + "LizzieConfig.about.lblOriginTitle=<html><b>Modify information</b></html>\r\n"
                    + "LizzieConfig.about.lblOriginLizzieInfo1=<html>This is a personal modified Lizzie<br /><br /><table><tr><td>Version:</td><td>\r\n"
                    + "LizzieConfig.about.lblOriginLizzieInfo2=</td></tr><tr><td>Modified by:</td><td><a href=\\\"https://github.com/yzyray\\\">yzyray</a></td></tr><tr><td>Source code:</td><td><a href=\\\"https://github.com/yzyray/lizzieyzy\\\">https://github.com/yzyray/lizzieyzy</a></td></tr><tr><td>BUG or Advice:</td><td><a href=\\\"https://github.com/yzyray/lizzieyzy/issues\">click to report issues</a></td></tr></table></html>\r\n"
                    + "LizzieConfig.lblLogGtpToFile=Log GTP To File\r\n"
                    + "LizzieConfig.lblLogGtpToFile.tooltips=Need restart,Log file is 'LastGtpLogs.txt' in Lizzie's folder.\r\n"
                    + "LizzieConfig.lblLogConsoleToFile=Log Console To Files\r\n"
                    + "LizzieConfig.lblLogConsoleToFile.tooltips=Need restart,Log files is 'LastConsoleLogs.txt' and 'LastErrorLogs.txt' in Lizzie's folder.\r\n"
                    + "LizzieConfig.lblRightClickFunction=Right Click Function\r\n"
                    + "LizzieConfig.rdoRightClickMenu=Menu\r\n"
                    + "LizzieConfig.rdoRightClickBack=Previous\r\n"
                    + "LizzieConfig.lblLoadKomi=Load Komi From SGF\r\n"
                    + "LizzieConfig.lblKifuLoadLast=Jump To End After Load Game\r\n"
                    + "LizzieConfig.lblLoadEstimate=PreLoad Estimate Engine\r\n"
                    + "LizzieConfig.lblEstimateEngine=Estimate Engine\r\n"
                    + "LizzieConfig.lblEnableDragStone=Drag Stone\r\n"
                    + "LizzieConfig.lblEnableClickReview=Click Stone Review\r\n"
                    + "LizzieConfig.visits=V\r\n"
                    + "LizzieConfig.SuggestionMoveColorConcentration1=Focus\r\n"
                    + "LizzieConfig.SuggestionMoveColorConcentration2=Normal\r\n"
                    + "LizzieConfig.SuggestionMoveColorConcentration3=Scatter\r\n"
                    + "LizzieConfig.aboutLizzieCache=Sometimes,when we go back to a previous board status,engine forgets previous calculation and start from 0,Lizzie can remember it and not update calculation result until current calculation Visits lager than previous.  \r\n"
                    + "LizzieConfig.aboutLizzieCacheTitle=About Lizzie Cache\r\n"
                    + "LizzieConfig.boardSize=Board Size\r\n"
                    + "LizzieConfig.button.add=Add\r\n"
                    + "LizzieConfig.button.cancel=Cancel\r\n"
                    + "LizzieConfig.button.ok=OK\r\n"
                    + "LizzieConfig.button.remove=Remove\r\n"
                    + "LizzieConfig.button.reset=Reset\r\n"
                    + "LizzieConfig.chkCheckEngineAlive=Auto Check Engine Alive\r\n"
                    + "LizzieConfig.chkShowIndependentSubBoard0=None\r\n"
                    + "LizzieConfig.chkShowIndependentSubBoard1=Independent\r\n"
                    + "LizzieConfig.chkShowIndependentSubBoard2=Extra\r\n"
                    + "LizzieConfig.chkShowWhiteSuggWhite1=None\r\n"
                    + "LizzieConfig.chkShowWhiteSuggWhite2=Move\r\n"
                    + "LizzieConfig.chkShowWhiteSuggWhite3=Order\r\n"
                    + "LizzieConfig.chkShowWhiteSuggWhite4=both\r\n"
                    + "LizzieConfig.lblAlwaysLogGtpInfo=Print GTP Info\r\n"
                    + "LizzieConfig.lblAlwaysOnTop=Always OnTop\r\n"
                    + "LizzieConfig.lblShowMoveRank=Move Rank Mark\r\n"
                    + "LizzieConfig.lblNextMoveHint=Next Move Hint\r\n"
                    + "LizzieConfig.comboMoveHint.none=None\r\n"
                    + "LizzieConfig.comboMoveHint.circle=Circle\r\n"
                    + "LizzieConfig.comboMoveHint.winrate=WinRate\r\n"
                    + "LizzieConfig.lblAlwaysShowBlackWinrate=Always Show Black WinRate/Score\r\n"
                    + "LizzieConfig.lblAppendWinrateToComment=Append Candidate Info To Comment\r\n"
                    + "LizzieConfig.lblBoardPositionProportion=Board Position Proportion\r\n"
                    + "LizzieConfig.lblEnableDoubleClickFindMove=Double Click Find Move\r\n"
                    + "LizzieConfig.lblEngineSettings=Engine Settings\\:\r\n"
                    + "LizzieConfig.lblIndepentMainBoard=Show Independent MainBoard\r\n"
                    + "LizzieConfig.lblIndepentSubBoard=Show Independent SubBoard\r\n"
                    + "LizzieConfig.lblMaxValueReverseColor=Reverse Color For Max Value\r\n"
                    + "LizzieConfig.lblMouseMoveRect=Show Rect On Mouse Move\r\n"
                    + "LizzieConfig.lblMoveNumInBracnh=MoveNum In Branch\r\n"
                    + "LizzieConfig.lblNotRereshVairationsOnMouseOver=Variation Not Refresh On Mouse Over\r\n"
                    + "LizzieConfig.lblNotShowMinPlayoutRatio=Min Playout Ratio for Stats\r\n"
                    + "LizzieConfig.lblNoticeLimit=Tip\\: 0 means no limit\r\n"
                    + "LizzieConfig.lblOtherSettings=Other Settings\\:\r\n"
                    + "LizzieConfig.lblShowAllMove=Always Show MoveNum In Branch\r\n"
                    + "LizzieConfig.lblShowCircleForOutOfLimit=Show Circle For Out Of Limit\r\n"
                    + "LizzieConfig.lblShowQuickLinks=Show Quick Links\r\n"
                    + "LizzieConfig.lblShowIndependentHawkEye=Show Independent Hawk Eye\r\n"
                    + "LizzieConfig.lblShowIndependentMoveList=Show Independent Move List\r\n"
                    + "LizzieConfig.lblShowMoveList=Show Candidates List\r\n"
                    + "LizzieConfig.lblShowMoveNumInVariationPane=Show MoveNum In Variation Pane\r\n"
                    + "LizzieConfig.lblShowStatus=Show Status\r\n"
                    + "LizzieConfig.lblShowSuggestionMoveOrder=Show Candidate Order\r\n"
                    + "LizzieConfig.lblShowTitleWinInfo=Show WinRate Info On Title\r\n"
                    + "LizzieConfig.lblShowVariationsOnMouse=Show Variation On Mouse Over\r\n"
                    + "LizzieConfig.lblShowWhiteSuggestionWhite=Use White Color On White turn\r\n"
                    + "LizzieConfig.lblSubBoardNotRefreshOnMouseOver=Sub Board Not Refresh On MouseOver\r\n"
                    + "LizzieConfig.lblSuggestionMoveAndWinrateSettings=Candidate And WinRate Settings\\:\r\n"
                    + "LizzieConfig.lblSuggestionMoveColorConcentration=Candidate Color Concentration\r\n"
                    + "LizzieConfig.lblViewSettings=View And Panel Settings\\:\r\n"
                    + "LizzieConfig.lblWinratePerspective=WinRateGraph Perspective\r\n"
                    + "LizzieConfig.lizzie.contributors=<html><table><tr><td><a href\\=\"https\\://github.com/cngoodboy\">cngoodboy</a></td><td><a href\\=\"https\\://github.com/kaorahi\">kaorahi</a></td><td><a href\\=\"https\\://github.com/zsalch\">zsalch</a></td></tr><tr><td><a href\\=\"https\\://github.com/bittsitt\">bittsitt</a></td><td><a href\\=\"https\\://github.com/OlivierBlanvillain\">OlivierBlanvillain</a></td><td><a href\\=\"https\\://github.com/dfannius\">dfannius</a></td></tr><tr><td><a href\\=\"https\\://github.com/toomasr\">toomasr</a></td><td><a href\\=\"https\\://github.com/apetresc\">apetresc</a></td><td><a href\\=\"https\\://github.com/TFiFiE\">TFiFiE</a></td></tr><tr><td><a href\\=\"https\\://github.com/aerisnju\">aerisnju</a></td><td><a href\\=\"https\\://github.com/kuba97531\">kuba97531</a></td><td><a href\\=\"https\\://github.com/bvandenbon\">bvandenbon</a></td></tr><tr><td><a href\\=\"https\\://github.com/Ka-zam\">Ka-zam</a></td><td><a href\\=\"https\\://github.com/typohh\">typohh</a></td><td><a href\\=\"https\\://github.com/alreadydone\">alreadydone</a></td></tr><tr><td><a href\\=\"https\\://github.com/odCat\">odCat</a></td><td><a href\\=\"https\\://github.com/tomasz-warniello\">tomasz-warniello</a></td><td><a href\\=\"https\\://github.com/inohiroki\">inohiroki</a></td></tr><tr><td><a href\\=\"https\\://github.com/ParmuzinAlexander\">ParmuzinAlexander</a></td><td><a href\\=\"https\\://github.com/ygrek\">ygrek</a></td><td><a href\\=\"https\\://github.com/pliuphys\">pliuphys</a></td></tr><tr><td><a href\\=\"https\\://github.com/infinity0\">infinity0</a></td><td><a href\\=\"https\\://github.com/yzyray\">yzyray</a></td><td></td></tr></html>\r\n"
                    + "LizzieConfig.lizzie.contributorsTitle=<html><a href\\=\"https\\://github.com/featurecat/lizzie/graphs/contributors\"><b>Contributors</b></a></html>\r\n"
                    + "LizzieConfig.lizzie.info=<html><b>Lizzie</b> is a free and open-source Go graphical interface allowing the user to analyze games in real time using like Leela Zero.<br /><br /><table><tr><td>Author\\:</td><td><a href\\=\"https\\://github.com/featurecat\">featurecat</a></td></tr><tr><td>Source code\\:</td><td><a href\\=\"https\\://github.com/featurecat/lizzie\">https\\://github.com/featurecat/lizzie</a></td></tr><tr><td>LICENSE\\:</td><td><a href\\=\"https\\://github.com/featurecat/lizzie/blob/master/LICENSE.txt\">GPL-3.0</a></td></tr></table></html>\r\n"
                    + "LizzieConfig.lizzieCache=Lizzie Cache\r\n"
                    + "LizzieConfig.lblStopAtEmpty=Stop Ponder On Empty Board\r\n"
                    + "LizzieConfig.prompt.selectEngine=Please select a engine\r\n"
                    + "LizzieConfig.prompt.selectikatago=Please select ikatago executable file\r\n"
                    + "LizzieConfig.prompt.selectWeight=Please select a weight file\r\n"
                    + "LizzieConfig.rdoBranchMoveContinue=Continue\r\n"
                    + "LizzieConfig.rdoBranchMoveOne=From 1\r\n"
                    + "LizzieConfig.rdoNoShowMoveRect=No\r\n"
                    + "LizzieConfig.rdoShowMoveRect=Yes\r\n"
                    + "LizzieConfig.rdoShowMoveRectOnPlay=Only In Game\r\n"
                    + "LizzieConfig.rdoShowWinrateBlack=Black\r\n"
                    + "LizzieConfig.rdoShowWinrateBoth=Both\r\n"
                    + "LizzieConfig.showBlueRing=Show Blue Ring On BestMove\r\n"
                    + "LizzieConfig.showNameInboard=Show Name In Board\r\n"
                    + "LizzieConfig.title.chkPureBackground=Pure Background\r\n"
                    + "LizzieConfig.title.chkPureBoard=Pure Board\r\n"
                    + "LizzieConfig.title.chkPureStone=Pure Stone\r\n"
                    + "LizzieConfig.title.SSHanalyzeUpdateInterval=Remote Analyze Interval\r\n"
                    + "LizzieConfig.title.about=About\r\n"
                    + "LizzieConfig.title.analyzeUpdateInterval=Analyze Update Interval\r\n"
                    + "LizzieConfig.title.appendWinrateToComment=Append Candidate Info To Comment\r\n"
                    + "LizzieConfig.title.avoidKeepVariations=Avoid Keep Variations\r\n"
                    + "LizzieConfig.title.backgroundPath=Background Path\r\n"
                    + "LizzieConfig.title.blackStonePath=Black Stone Path\r\n"
                    + "LizzieConfig.title.blunderBarColor=Blunder Bar Color\r\n"
                    + "LizzieConfig.title.blunderColor=Color\r\n"
                    + "LizzieConfig.title.blunderNodes=Blunder Nodes\r\n"
                    + "LizzieConfig.title.blunderThresholds=WinDiff Threshold\r\n"
                    + "LizzieConfig.chkUseScoreDiff=Also consider ScoreDiff,threshold as\r\n"
                    + "LizzieConfig.lblUseScoreDiffPercent=% of winDiff\r\n"
                    + "LizzieConfig.title.boardPath=Board Path\r\n"
                    + "LizzieConfig.title.boardPositionProportion=Board Position Proportion\r\n"
                    + "LizzieConfig.title.boardSize=Board Size\r\n"
                    + "LizzieConfig.title.centisecond=Centiseconds\r\n"
                    + "LizzieConfig.title.colorByWinrateInsteadOfVisits=Color by WinRate Instead of Visits\r\n"
                    + "LizzieConfig.title.commentBackgroundColor=Comment Background Color\r\n"
                    + "LizzieConfig.title.commentFontColor=Comment Font Color\r\n"
                    + "LizzieConfig.title.commentFontSize=Comment Font Size\r\n"
                    + "LizzieConfig.title.commentNodeColor=Comment Node Color\r\n"
                    + "LizzieConfig.title.config=Config(Shift+X)\r\n"
                    + "LizzieConfig.title.defaultTheme=Default\r\n"
                    + "LizzieConfig.title.dynamicWinrateGraphWidth=Dynamic WinRate Graph Width\r\n"
                    + "LizzieConfig.title.engine=Engine\r\n"
                    + "LizzieConfig.title.fontName=Visits Font Name\r\n"
                    + "LizzieConfig.title.gtpConsoleStyle=Gtp Console Style\r\n"
                    + "LizzieConfig.title.holdBestMovesToSgf=Hold Best Moves To Sgf\r\n"
                    + "LizzieConfig.title.lblManAiGameTime=Man Vs Ai Game Ai Time Per Move\r\n"
                    + "LizzieConfig.title.limitBestMoveNum=Limit Max Candidates\r\n"
                    + "LizzieConfig.title.limitBranchLength=Limit Branch Length\r\n"
                    + "LizzieConfig.title.maxAnalyzeTime=Max Analyze\r\n"
                    + "LizzieConfig.title.maxGameThinkingTime=Max Game Thinking Time\r\n"
                    + "LizzieConfig.title.maxSuggestionmoves=Limit Max Candidates\r\n"
                    + "LizzieConfig.title.minPlayoutRatioForStats=Min Playout Ratio for Stats\r\n"
                    + "LizzieConfig.title.minimumBlunderBarWidth=Minimum Blunder Bar Width\r\n"
                    + "LizzieConfig.title.minutes=Minutes\r\n"
                    + "LizzieConfig.title.moves=moves\r\n"
                    + "LizzieConfig.title.panelUI=Panel UI\r\n"
                    + "LizzieConfig.title.parameter=Parameter\r\n"
                    + "LizzieConfig.title.parameterConfig=Parameter Config\r\n"
                    + "LizzieConfig.title.parameterList=Parameter List\r\n"
                    + "LizzieConfig.title.preload=Preload\r\n"
                    + "LizzieConfig.title.printEngineLog=Print Engine Log\r\n"
                    + "LizzieConfig.title.scoreMeanLineColor=ScoreMean Line Color\r\n"
                    + "LizzieConfig.title.seconds=Seconds\r\n"
                    + "LizzieConfig.title.shadowSize=Stone Shadow Size\r\n"
                    + "LizzieConfig.title.showBestMovesByHold=Show Best Moves By Hold\r\n"
                    + "LizzieConfig.title.showBlunderBar=Show Blunder Bar\r\n"
                    + "LizzieConfig.title.showCaptured=Show Captured\r\n"
                    + "LizzieConfig.title.showComment=Show Comment\r\n"
                    + "LizzieConfig.title.showCommentNodeColor=Show Comment Node Color\r\n"
                    + "LizzieConfig.title.showCoordinates=Show Coordinates\r\n"
                    + "LizzieConfig.title.showLcbWinrate=Display WinRate As\r\n"
                    + "LizzieConfig.title.showMoveNumber=Show Move Number\r\n"
                    + "LizzieConfig.title.showMoveNumberAll=All\r\n"
                    + "LizzieConfig.title.showMoveNumberLast=Last\r\n"
                    + "LizzieConfig.title.showMoveNumberNo=None\r\n"
                    + "LizzieConfig.title.showPlayoutsInSuggestion=Visits\r\n"
                    + "LizzieConfig.title.showScoremeanInSuggestion=ScoreLead\r\n"
                    + "LizzieConfig.title.showSubBoard=Show Sub Board\r\n"
                    + "LizzieConfig.title.showVariationGraph=Show Variation\r\n"
                    + "LizzieConfig.title.showWinrate=Show WinRate\r\n"
                    + "LizzieConfig.title.showWinrateInSuggestion=WinRate\r\n"
                    + "LizzieConfig.title.stoneIndicatorCircle=Circle\r\n"
                    + "LizzieConfig.title.stoneIndicatorNo=No\r\n"
                    + "LizzieConfig.title.stoneIndicatorSolid=Solid\r\n"
                    + "LizzieConfig.title.stoneIndicatorType=Stone Indicator Type\r\n"
                    + "LizzieConfig.title.suggestionMoveInfo=Move Info\r\n"
                    + "LizzieConfig.title.theme=Theme\r\n"
                    + "LizzieConfig.title.ui=UI\r\n"
                    + "LizzieConfig.title.uiFontName=UI Font Name\r\n"
                    + "LizzieConfig.title.whiteStonePath=White Stone Path\r\n"
                    + "LizzieConfig.title.winrateFontName=WinRate Font Name\r\n"
                    + "LizzieConfig.title.winrateLineColor=WinRate Line Color\r\n"
                    + "LizzieConfig.title.winrateMissLineColor=WinRate Miss Line Color\r\n"
                    + "LizzieConfig.title.winrateStrokeWidth=WinRate Stroke Width\r\n"
                    + "BlunderTabel.black=Black\r\n"
                    + "BlunderTabel.white=White\r\n"
                    + "BlunderTabel.coords=Coords\r\n"
                    + "BlunderTabel.winRate=WinRate\r\n"
                    + "BlunderTabel.score=Score\r\n"
                    + "LizzieFrame.passInGameTip=If the game ended,use \"Game-Final score\" to calculate game result.\r\n"
                    + "LizzieFrame.sendTimes.kataGoTimeMismatch=Engine time settings is KataGo type,but current engine is not KataGo,time control maybe failed.\r\n"
                    + "LizzieFrame.speedUnit=visits/s\r\n"
                    + "LizzieFrame.aboutAnalyzeGenmoveInfo=Genmove mode use genmove command and engine will play move itself.\\r\\nAnalyze mode use lz-analyze/kata-analyze command to get candidates move and Lizzie will play the best move.\\r\\nIn genmove mode you can only change play condition by engine configs,in analyze mode Lizzie can control play condition such as total visits/first move visits/times.\r\n"
                    + "LizzieFrame.aboutAnalyzeGenmoveInfoTitle=About Genmove/Analyze mode\r\n"
                    + "LizzieFrame.chkComment=Comment\r\n"
                    + "LizzieFrame.chkBlunder=Blunder\r\n"
                    + "LizzieFrame.chkOnlyAfter=Next\r\n"
                    + "LizzieFrame.setThreshold=Threshold\r\n"
                    + "LizzieFrame.close=Close\r\n"
                    + "LizzieFrame.confirm=Confirm\r\n"
                    + "LizzieFrame.cancel=Cancel\r\n"
                    + "LizzieFrame.confirmNewBoard=This will clear the board and all analysis, continue?\r\n"
                    + "LizzieFrame.confirmNewBoardTitle=New Board?\r\n"
                    + "LizzieFrame.noNoticeAgain=Do not show again (reopen in Menu-View-Main panel settings)\r\n"
                    + "LizzieFrame.ifReplaceFile=Replace file \\\"\r\n"
                    + "LizzieFrame.replaceFileNotice=Tip: If you do not want replace file,use \\\"Menu-File-Save as\\\"\r\n"
                    + "LizzieFrame.replaceFileTitle=Replace file?\r\n"
                    + "LizzieFrame.closeCommentBarTitle=Close Control Bar?\r\n"
                    + "LizzieFrame.closeCommentBar=You can reopen it from \"Menu-View-Main panel settings-Show comment control bar\"\r\n"
                    + "LizzieFrame.currentRules.chinese=Chinese\r\n"
                    + "LizzieFrame.currentRules.chn-ancient=ChnAncient\r\n"
                    + "LizzieFrame.currentRules.japanese=Japanese\r\n"
                    + "LizzieFrame.currentRules.tromp-taylor=TrompTaylor\r\n"
                    + "LizzieFrame.currentRules.others=Others\r\n"
                    + "LizzieFrame.saveSubBoardHint=Currently not showing sub board!\r\n"
                    + "LizzieFrame.lizzieError=Lizzie - Error!\r\n"
                    + "LizzieFrame.saveImageErrorHint1=Failed to save image\r\n"
                    + "LizzieFrame.saveImageErrorHint2=Unsupported image format?\r\n"
                    + "LizzieFrame.AIscore=Accuracy\\: \r\n"
                    + "LizzieFrame.result=Result: \r\n"
                    + "LizzieFrame.backToHall=BackToHall\r\n"
                    + "LizzieFrame.black=B\\:\r\n"
                    + "LizzieFrame.chooseKifu=Choose game record\r\n"
                    + "LizzieFrame.chooseOpeningSgf=Choose opening sgf\r\n"
                    + "LizzieFrame.commands.key123456789=1-9|show Candidate 1-9 variations\r\n"
                    + "LizzieFrame.commands.keyA=a|run automatic analysis of game\r\n"
                    + "LizzieFrame.commands.keyAltC=ctrl-c|copy SGF to clipboard\r\n"
                    + "LizzieFrame.commands.keyJ=J|toggle(show difference/show circle/hide) next move hint\r\n"
                    + "LizzieFrame.commands.keyAltV=ctrl-v|paste SGF from clipboard\r\n"
                    + "LizzieFrame.commands.keyB=b|back to main trunk\r\n"
                    + "LizzieFrame.commands.keyG=G|refresh variation when mouse over\r\n"
                    + "LizzieFrame.commands.keyBracket=][|adjust main board position\r\n"
                    + "LizzieFrame.commands.keyC=c|toggle coordinates\r\n"
                    + "LizzieFrame.commands.keyComma=, or alt-, or wheel press|play best move/play best branch\r\n"
                    + "LizzieFrame.commands.keyControl=ctrl+up/down|undo/redo 10 moves\r\n"
                    + "LizzieFrame.commands.keyCtrlT=ctrl-t|toggle comment node color display\r\n"
                    + "LizzieFrame.commands.keyCtrlW=ctrl-w|toggle large winRate graph\r\n"
                    + "LizzieFrame.commands.keyD=d|show/hide dynamic komi\r\n"
                    + "LizzieFrame.commands.keyDelete=delete/backspace|delete move/branch\r\n"
                    + "LizzieFrame.commands.keyDownArrow=down arrow|redo\r\n"
                    + "LizzieFrame.commands.keyE=e|toggle evaluation coloring\r\n"
                    + "LizzieFrame.commands.keyEnd=end|go to end\r\n"
                    + "LizzieFrame.commands.keyEnter=enter or alt+enter|continue game with AI\r\n"
                    + "LizzieFrame.commands.keyF=f|toggle show AI candidates on board\r\n"
                    + "LizzieFrame.commands.keyShiftG=shift+g|toggle variation graph\r\n"
                    + "LizzieFrame.commands.keyHandY=h or y|toggle show raw net analyze\r\n"
                    + "LizzieFrame.commands.keyHome=home|go to start\r\n"
                    + "LizzieFrame.commands.keyI=i|edit game info\r\n"
                    + "LizzieFrame.commands.keyM=m|show/hide move number\r\n"
                    + "LizzieFrame.commands.keyN=n or alt+n|start game against AI\r\n"
                    + "LizzieFrame.commands.keyO=o|open SGF\r\n"
                    + "LizzieFrame.commands.keyP=p|pass\r\n"
                    + "LizzieFrame.commands.keyR=r|replay mouse over branch\r\n"
                    + "LizzieFrame.commands.keyS=s|save SGF\r\n"
                    + "LizzieFrame.commands.keyShiftF=shift+f|toggle show AI candidate's variations when mouse over\r\n"
                    + "LizzieFrame.commands.keyShiftZ=shift+z|toggle candidates etc.\r\n"
                    + "LizzieFrame.commands.keySlash=/ or .(keyboard point)|estimate score\r\n"
                    + "LizzieFrame.commands.keySpace=space|toggle pondering\r\n"
                    + "LizzieFrame.commands.keyY=y|show hawk-eye\r\n"
                    + "LizzieFrame.commands.keyU=u|show AI candidates list\r\n"
                    + "LizzieFrame.commands.keyUpDownArrow=up/down arrow|undo/redo\r\n"
                    + "LizzieFrame.commands.keyV=v|toggle try playing\r\n"
                    + "LizzieFrame.commands.keyW=alt+w|toggle winRate display\r\n"
                    + "LizzieFrame.commands.keyAltZ=alt+z|toggle show sub board\r\n"
                    + "LizzieFrame.commands.keyZ=z|temporary hide candidates(keep holding down)\r\n"
                    + "LizzieFrame.commands.mousePointSub=mouse on subBoard|right/left click switch variation,wheel control moves\r\n"
                    + "LizzieFrame.commands.mouseWheelScroll=scrollwheel|undo/redo\r\n"
                    + "LizzieFrame.commands.rightClick=right click|undo\r\n"
                    + "LizzieFrame.deleteMoves=This will delete all moves and branches after this move\r\n"
                    + "LizzieFrame.delete=Delete\r\n"
                    + "LizzieFrame.display.dynamic-komi=dyn. komi\\:\r\n"
                    + "LizzieFrame.display.lastMove=Last move\r\n"
                    + "LizzieFrame.display.leelaz-missing=Did not find Enigne, update config.txt or download from Leela Zero homepage\r\n"
                    + "LizzieFrame.display.loading=Engine is loading...\r\n"
                    + "LizzieFrame.display.network-missing=Did not find network weights.\\nUpdate config.txt (network-file) or download from Leela Zero homepage\r\n"
                    + "LizzieFrame.display.off=off\r\n"
                    + "LizzieFrame.display.on=on\r\n"
                    + "LizzieFrame.display.pondering=Pondering\r\n"
                    + "LizzieFrame.display.space=\\  Hotkey\\=Space\r\n"
                    + "LizzieFrame.display.tuning=Engine is tuning, may take a few minutes.\r\n"
                    + "LizzieFrame.display.down=Engine is down.\r\n"
                    + "LizzieFrame.captures=Captures\\: \r\n"
                    + "SetEstimateParam.btnGenerate=Generate\r\n"
                    + "LizzieFrame.engineGameStopFirstHint=Please wait current engine game finish,or stop it manually.\r\n"
                    + "LizzieFrame.fileExists=The file already exists, do you want to replace it?\r\n"
                    + "LizzieFrame.introduction=Manual\r\n"
                    + "LizzieFrame.komi=komi\\:\r\n"
                    + "LizzieFrame.listColumn2=WinRate\r\n"
                    + "LizzieFrame.listColumn4=Percents\r\n"
                    + "LizzieFrame.loading=Loading\r\n"
                    + "LizzieFrame.mainEngine=MainEngine\\:\r\n"
                    + "LizzieFrame.noEngineText=Empty\r\n"
                    + "LizzieFrame.weightText.contributing=Contributing\r\n"
                    + "LizzieFrame.onLoad=Load\r\n"
                    + "LizzieFrame.points=Points\\: \r\n"
                    + "LizzieFrame.prompt.failedToOpenFile=Failed to open file.\r\n"
                    + "LizzieFrame.prompt.failedToSaveFile=Failed to save file.\r\n"
                    + "LizzieFrame.prompt.sgfExists=The SGF file already exists, do you want to replace it?\r\n"
                    + "LizzieFrame.prompt.showControlsHint=hold x \\= view controls\r\n"
                    + "LizzieFrame.prompt.switching=switching...\r\n"
                    + "LizzieFrame.pts=pts\r\n"
                    + "LizzieFrame.recordExists=Record exists,replace it?\r\n"
                    + "LizzieFrame.refresh=Refresh\r\n"
                    + "LizzieFrame.ruleWarning=Can't get current engine's rules\r\n"
                    + "LizzieFrame.saveAndLoad.deleteAllWarining=Clear all saves?\r\n"
                    + "LizzieFrame.saveAndLoad.btnDeleteAll=Clear\r\n"
                    + "LizzieFrame.saveAndLoad.autoRec=AutoRec\r\n"
                    + "LizzieFrame.saveAndLoad.chkAutoResume=Auto Resume\r\n"
                    + "LizzieFrame.saveAndLoad.chkAutoSaveOnExit=Auto Save\r\n"
                    + "LizzieFrame.saveAndLoad.chkZoomImage=Zoom On MouseOver\r\n"
                    + "LizzieFrame.saveAndLoad.close=Close\r\n"
                    + "LizzieFrame.saveAndLoad.del=Del\r\n"
                    + "LizzieFrame.saveAndLoad.exitRecord=Auto Save\r\n"
                    + "LizzieFrame.saveAndLoad.load=Load\r\n"
                    + "LizzieFrame.saveAndLoad.newRecord=New record\r\n"
                    + "LizzieFrame.saveAndLoad.reName=Rename\r\n"
                    + "LizzieFrame.saveAndLoad.rec=Rec\r\n"
                    + "LizzieFrame.saveAndLoad.save=Save\r\n"
                    + "LizzieFrame.saveFileFailed=Save failed\\!\r\n"
                    + "LizzieFrame.scoreLeadWithKomi=Lead\\: \r\n"
                    + "LizzieFrame.scoreLeadJustScore=Lead\\: \r\n"
                    + "LizzieFrame.scoreStdev=stdev\\:\r\n"
                    + "LizzieFrame.setParamNoEngineHint=Engine is not loaded\r\n"
                    + "LizzieFrame.setParamsWarning=Current engine is not leela/sai,May not working\r\n"
                    + "LizzieFrame.stage=stage\\:\r\n"
                    + "LizzieFrame.startContinueGame=Continue game against AI.\r\n"
                    + "LizzieFrame.stopSync=Stop Sync\r\n"
                    + "LizzieFrame.subEngine=SubEngine\\:\r\n"
                    + "LizzieFrame.thinking=thinking\r\n"
                    + "LizzieFrame.title=Lizzie\r\n"
                    + "LizzieFrame.tryTitle=TryPlaying...\r\n"
                    + "LizzieFrame.url=url\r\n"
                    + "LizzieFrame.visits=visits\\:\r\n"
                    + "LizzieFrame.waitEngineLoadingHint=Please wait,engine is loading.\r\n"
                    + "LizzieFrame.warning=warning\r\n"
                    + "LizzieFrame.white=W\\:\r\n"
                    + "LizzieFrame.winrate=winRate\\:\r\n"
                    + "Mannul.blackResign=BlackResign\r\n"
                    + "Mannul.manualOne.disable=DisableManualPlace\r\n"
                    + "Mannul.manualOne.enable=EnableManualPlace\r\n"
                    + "Mannul.playNow=Play now\r\n"
                    + "Mannul.title=Manual intervention\r\n"
                    + "Mannul.whiteResign=WhiteResign\r\n"
                    + "Menu.variationPaneSettings=Variation pane settings\r\n"
                    + "Menu.showScrollVariation=Use scroll pane to expand width\r\n"
                    + "Menu.maxTreeWidth=Set max width\r\n"
                    + "Menu.ignoreOutOfWidth=Still use scroll pane when out of width(Can't display out region)\r\n"
                    + "Menu.maxTreeWidthHint=If feels laggy,decrease max width\r\n"
                    + "Menu.lblMaxTreeWidth=Max Width(Default 10000):\r\n"
                    + "Menu.setMaxTreeWidthTitle=Set Max With\r\n"
                    + "Menu.customLayout=Custome layout \r\n"
                    + "Menu.customLayoutSet=Set\r\n"
                    + "Menu.mainPanelSettings=Main panel settings\r\n"
                    + "Menu.showNewBoardConfirmDialog=Show confirm dialog in creating new board\r\n"
                    + "Menu.showReplaceFileConfirmDialog=Show overwrite confirm dialog in saving file\r\n"
                    + "Menu.bottomToolBar=Bottom tool bar\r\n"
                    + "Menu.flashAnalyzeSettings=Flash analyze settings\r\n"
                    + "Menu.showWinRateOrScoreLeadLine=Show winRate or scoreLead line\r\n"
                    + "Menu.showWinRateOrScoreLeadLine.winrate=win rate\r\n"
                    + "Menu.showWinRateOrScoreLeadLine.scoreLead=Score lead\r\n"
                    + "Menu.showWinRateOrScoreLeadLine.both=Both\r\n"
                    + "Menu.kataEstimate=Show kata estimate\r\n"
                    + "Menu.kataEstimateClose=Close\r\n"
                    + "Menu.kataEstimateCloseView=Hidden\r\n"
                    + "Menu.kataEstimateOnMainBoard=On main board\r\n"
                    + "Menu.kataEstimateOnSubBoard=On sub board\r\n"
                    + "Menu.kataEstimateOnBothBoard=On both board\r\n"
                    + "Menu.independentBoardMode=Independent board mode\r\n"
                    + "Menu.indepBothBoard=Independent both board (Alt+7)\r\n"
                    + "Menu.indepMainBoard=Independent main board\r\n"
                    + "Menu.indepSubBoard=Independent sub board\r\n"
                    + "Menu.indepExtraBoard=Independent extra board\r\n"
                    + "Menu.kataScolreLead=Show score lead\r\n"
                    + "Menu.leadWithKomi=Just score\r\n"
                    + "Menu.leadJustScore=Score+komi\r\n"
                    + "Menu.kataScoreLeadPerspective=Score lead perspective\r\n"
                    + "Menu.scoreLeadPerspectiveBlack=Always black\r\n"
                    + "Menu.scoreLeadPerspectiveAlternately=Alternately\r\n"
                    + "Menu.showScoreLeadOnWinrateGraph=Show score lead on winRate graph\r\n"
                    + "Menu.subBoardShowVar=Variation\r\n"
                    + "Menu.subBoardShowRaw=Only board and stone\r\n"
                    + "Menu.showHeat=Heatmap\r\n"
                    + "Menu.showHeatAfterCalc=Heatmap after calculation\r\n"
                    + "Menu.notShowHeat=No heatmap\r\n"
                    + "Menu.kataSettings=KataGo settings\r\n"
                    + "Menu.defaultView=Default mode (Alt+1)\r\n"
                    + "Menu.classicView=Classic mode (Alt+2)\r\n"
                    + "Menu.minView=Simple mode (Alt+3) \r\n"
                    + "Menu.extraMode3=Thinking mode (Alt+4)\r\n"
                    + "Menu.extraMode1=MultiView mode (Alt+5)\r\n"
                    + "Menu.extraMode2=DoubleEngine mode (Alt+6)\r\n"
                    + "Menu.SuggestionList=Independent candidates list (U)\r\n"
                    + "Menu.Suggestions=Candidates info\r\n"
                    + "Menu.allMoveNum=All\r\n"
                    + "Menu.alwaysOnTop=Always on top (Ctrl+Z)\r\n"
                    + "Menu.alwaysShowBlackWinrate=Always show black winRate/score\r\n"
                    + "Menu.analyze=Analyze\r\n"
                    + "Menu.anyMoveNum=Custom\r\n"
                    + "Menu.appendWinrateToComment=Append candidate info to comment\r\n"
                    + "Menu.variationPane=Variation panel (Shift+G)\r\n"
                    + "Menu.btnAnalyze.toolTipText=Auto analyze (A)\r\n"
                    + "Menu.btnSetMain.toolTipText=Set as main (L)\r\n"
                    + "Menu.btnBackMain.toolTipText=Back to main (B)\r\n"
                    + "Menu.btnAnalyze.btnFlashAnalyze=Flash analyze\r\n"
                    + "Menu.flashAnalyzeAllGame=Flash analyze (All game) (Ctrl+B)\r\n"
                    + "Menu.flashAnalyzePartGame=Flash analyze (Part game)\r\n"
                    + "Menu.flashAnalyzeAllBranches=Flash analyze (All branches)\r\n"
                    + "Menu.btnHawkeye.toolTipText=Hawk eye (Y)\r\n"
                    + "Menu.btnMarkup.toolTipText=Markup Tools\r\n"
                    + "Menu.btnMarkupClear.toolTipText=Clear Markups\r\n"
                    + "Menu.btnOpen.toolTipText=Open file (O)\r\n"
                    + "Menu.btnChangeTurn.toolTipText=Exchange move turn\r\n"
                    + "Menu.btnSave.toolTipText=Save (Ctrl+S)\r\n"
                    + "Menu.btnNewFile.toolTipText=New empty board\r\n"
                    + "Menu.commitPane=Comment panel (Alt+T)\r\n"
                    + "Menu.coordsMenu=Coordinates (C)\r\n"
                    + "Menu.copyBoardScreen=Copy Board Screen shot (Shift+C)\r\n"
                    + "Menu.copySgf=Copy SGF (Ctrl+C)\r\n"
                    + "Menu.copySubBoardScreen=Copy SubBoard Screen shot (Alt+C)\r\n"
                    + "Menu.topToolBar=Top tool bar\r\n"
                    + "Menu.topToolBarSeparated=Separated\r\n"
                    + "Menu.topToolBarCombined=Combined in menu\r\n"
                    + "Menu.autoWrap=Auto wrap (Separated only)\r\n"
                    + "Menu.toolbar=Tool bar\r\n"
                    + "Menu.edit=Edit\r\n"
                    + "Menu.exit=Exit\r\n"
                    + "Menu.fileMenu=File\r\n"
                    + "Menu.forceExit=Force exit\r\n"
                    + "Menu.game=Game\r\n"
                    + "Menu.game.continueLadder=Continue ladder\r\n"
                    + "Menu.game.continueLadderFail=Play at least %d ladder moves successively before auto-continuation.\r\n"
                    + "Menu.gtpPanel=Gtp panel (E)\r\n"
                    + "Menu.hawkEye=Hawk eye (Y)\r\n"
                    + "Menu.tsumeGo=Tsumego frame (Shift+E)\r\n"
                    + "Menu.captureTsumeGo=Capture tsumego (Shift+T)\r\n"
                    + "Menu.heatMapSettings=Heatmap\r\n"
                    + "Menu.independentMainBoard=Independent main board (Alt+Q)\r\n"
                    + "Menu.independentMainBoard2=Independent main board (with origin board)\r\n"
                    + "Menu.independentSubBoard=Independent sub board (Alt+A)\r\n"
                    + "Menu.informationPane=Information panel\r\n"
                    + "Menu.largeSubBoard=Large sub board (Ctrl+F)\r\n"
                    + "Menu.largeWinrateGraph=Large winRate graph (Ctrl+W)\r\n"
                    + "Menu.lastFiveMoveNum=Last five\r\n"
                    + "Menu.lastOneMoveNum=Last one\r\n"
                    + "Menu.lastTenMoveNum=Last ten\r\n"
                    + "Menu.playSound=Play sound\r\n"
                    + "Menu.notPlaySoundInSync=Not play sound during sync\r\n"
                    + "Menu.lblPDA=PDA\r\n"
                    + "Menu.lblWRN=WRN\r\n"
                    + "Menu.leftMove=Move to left ([)\r\n"
                    + "Menu.listPane=Candidates list panel (Alt+G)\r\n"
                    + "Menu.loadKomi=Load komi form SGF game record\r\n"
                    + "Menu.mainBoardPos=Main board/frame position\r\n"
                    + "Menu.moveMenu=Move number (M)\r\n"
                    + "Menu.moveNumberInBranchTips=Only affect on new played branches.\r\n"
                    + "Menu.moveNumberAlwaysFromOne=Always start from 1 \r\n"
                    + "Menu.moveNumberInBracnhFromOne=From 1 in branch \r\n"
                    + "Menu.moveNumberInBracnhFromOneContinue=Continue in branch \r\n"
                    + "Menu.noEngine=Empty Engine\r\n"
                    + "Menu.noMoveNum=None\r\n"
                    + "Menu.noRefreshOnMouse=Variation not refresh on mouse over\r\n"
                    + "Menu.open=Open (O)\r\n"
                    + "Menu.openRecent=Open recent\r\n"
                    + "Menu.openUrl=Open from online (Q)\r\n"
                    + "Menu.help=Help\r\n"
                    + "Menu.panel=Panel\r\n"
                    + "Menu.pasteSgf=Paste SGF (Ctrl+V)\r\n"
                    + "Menu.visits=Visits\r\n"
                    + "Menu.rightMove=Move to right (])\r\n"
                    + "Menu.save=Save (Ctrl+S)\r\n"
                    + "Menu.saveAs=Save as (S)\r\n"
                    + "Menu.saveAndLoad=Save and load\r\n"
                    + "Menu.saveBranchRaw=Save raw branch (Crtl+Alt+S)\r\n"
                    + "Menu.saveCommentRaw=Save raw game record with comment\r\n"
                    + "Menu.saveMore=More save\r\n"
                    + "Menu.saveRaw=Save raw game record (Crtl+Shift+S)\r\n"
                    + "Menu.saveMainBoardScreen=Save main board screen shot (Alt+S)\r\n"
                    + "Menu.saveSubBoardScreen=Save sub board screen shot\r\n"
                    + "Menu.saveWinrate=Save winRatePane screen shot (Shift+S)\r\n"
                    + "Menu.showScoreAsDiff=Show score as difference\r\n"
                    + "Menu.scoreLead=Score lead\r\n"
                    + "Menu.score=Score\r\n"
                    + "Menu.setReplayInterval=Set variation replay interval\r\n"
                    + "Menu.settings=Settings\r\n"
                    + "Menu.share=Share\r\n"
                    + "Menu.showAllMoveNumberInBranch=Show all move numbers in branch (Ctrl+M)\r\n"
                    + "Menu.showBlunderBar=Show blunder bar\r\n"
                    + "Menu.showMaxValueReverse=Reverse color for max value\r\n"
                    + "Menu.showMoveNumberOnVariationPane=Show move number on variation pane\r\n"
                    + "Menu.showNameInBoard=Show name in board\r\n"
                    + "Menu.showSuggestionOrder=Show candidate order\r\n"
                    + "Menu.showVariationOnMouse=Show variation on mouse over\r\n"
                    + "Menu.showWhiteSuggestWhite=Use white color for white turn\r\n"
                    + "Menu.statusPanel=Status panel\r\n"
                    + "Menu.subBoard=Sub board (Alt+Z)\r\n"
                    + "Menu.subBoardSettings=Sub board settings\r\n"
                    + "Menu.sync=Sync\r\n"
                    + "Menu.viewMenu=View\r\n"
                    + "Menu.winrate=WinRate\r\n"
                    + "Menu.winrateGraph=WinRate graph (Alt+W)\r\n"
                    + "Menu.winrateGraphSettings=WinRate graph settings\r\n"
                    + "Menu.winrateMode0=Black perspective\r\n"
                    + "Menu.winrateMode1=Both perspective\r\n"
                    + "Menu.customInfoOrdr=Custom display order\r\n"
                    + "Menu.setCandidatesDelay=Set display delay\r\n"
                    + "Message.title=Information\r\n"
                    + "MoreEngines.ikatagoUserName=UserName:\r\n"
                    + "MoreEngines.ikatagoUserNameTitle=Please input username\r\n"
                    + "MoreEngines.ikatagoPassWord=Password:\r\n"
                    + "MoreEngines.ikatagoPassWordTitle=Please input password\r\n"
                    + "MoreEngines.choosePlatform=Choose platform\r\n"
                    + "MoreEngines.choosePlatformTitle=Please choose platform\r\n"
                    + "MoreEngines.otherPlatform=Other\r\n"
                    + "MoreEngines.aboutRemoteEngine=If you selected remote option,Lizzie will use the SSH2(Default port 22) protocol to call the engine on the remote server, and it support two login ways: password or keygen.\\r\\nIf you are intended to use ikatago,ignore this function and just use generate button to get right command. \r\n"
                    + "MoreEngines.aboutRemoteEngineTitle=Use remote engine\r\n"
                    + "MoreEngines.chkRemoteEngine=Remote\r\n"
                    + "MoreEngines.lblPort=Port\r\n"
                    + "MoreEngines.rdoUserName=Name\r\n"
                    + "MoreEngines.lblPassword=PW\r\n"
                    + "MoreEngines.rdoKeygen=Keygen\r\n"
                    + "MoreEngines.scanKeygen=Scan\r\n"
                    + "MoreEngines.add=Add\r\n"
                    + "MoreEngines.btnEncrypt=EncryptCommand\r\n"
                    + "MoreEngines.cancel=Cancel\r\n"
                    + "MoreEngines.chooseConfig=Please select a config file\r\n"
                    + "MoreEngines.chooseType=Choose engine type\\r\\n\r\n"
                    + "MoreEngines.chooseTypeTitle=Please choose engine type\r\n"
                    + "MoreEngines.column0=Index\r\n"
                    + "MoreEngines.column1=Name\r\n"
                    + "MoreEngines.column2=Command\r\n"
                    + "MoreEngines.column3=Preload\r\n"
                    + "MoreEngines.column4=W\r\n"
                    + "MoreEngines.column5=H\r\n"
                    + "MoreEngines.column6=Komi\r\n"
                    + "MoreEngines.column7=Default\r\n"
                    + "MoreEngines.delete=Delete\r\n"
                    + "MoreEngines.deleteHint=Delete\r\n"
                    + "MoreEngines.deleteHint2=Cancel\r\n"
                    + "MoreEngines.deleteHint3=Current engine command is empty,delete it?\r\n"
                    + "MoreEngines.deleteHint4=Delete?\r\n"
                    + "MoreEngines.deleteHint5=Confirm delete?\r\n"
                    + "MoreEngines.deleteHint6=Delete?\r\n"
                    + "MoreEngines.editEngine=Edit engine\r\n"
                    + "MoreEngines.engineName=Select a row to edit engine\r\n"
                    + "MoreEngines.exit=Exit\r\n"
                    + "MoreEngines.lblCommand=Command\r\n"
                    + "MoreEngines.lblHeight=H\r\n"
                    + "MoreEngines.lblKomi=Komi\r\n"
                    + "MoreEngines.lblName=Name\\:\r\n"
                    + "MoreEngines.lblWidth=W\r\n"
                    + "MoreEngines.lbldefault=Default\r\n"
                    + "MoreEngines.lblpreload=PreLoad\r\n"
                    + "MoreEngines.lblrdoDefault=Default engine\r\n"
                    + "MoreEngines.moveDown=Down\r\n"
                    + "MoreEngines.moveDown5=Down5\r\n"
                    + "MoreEngines.moveFirst=First\r\n"
                    + "MoreEngines.moveLast=Last\r\n"
                    + "MoreEngines.moveUp=Up\r\n"
                    + "MoreEngines.moveUp5=Up5\r\n"
                    + "MoreEngines.newEngine=New engine\r\n"
                    + "MoreEngines.no=no\r\n"
                    + "MoreEngines.other=Other\r\n"
                    + "MoreEngines.ikatago=ikatago(remote engine)\r\n"
                    + "MoreEngines.save=Save\r\n"
                    + "MoreEngines.saveHint=Save\r\n"
                    + "MoreEngines.saveHint2=Cancel\r\n"
                    + "MoreEngines.saveHint3=Current engine has been modified,Save it?\r\n"
                    + "MoreEngines.saveHint4=Save?\r\n"
                    + "MoreEngines.scan=Generate\r\n"
                    + "MoreEngines.title=Engine Management\r\n"
                    + "MoreEngines.yes=yes\r\n"
                    + "Movelistframe.keyPanel.accuracy=Accuracy\r\n"
                    + "Movelistframe.keyPanel.accuracy.tip=Based on visits,stand for the difference between actual game and AI candidates,see details in [help-manual-hawk eye].\r\n"
                    + "Movelistframe.keyPanel.match=Match\r\n"
                    + "Movelistframe.keyPanel.match.tip=You can set match condition at bottom in WinRateMatch tab.\r\n"
                    + "Movelistframe.keyPanel.matchBestMove=BestMove%\r\n"
                    + "Movelistframe.keyPanel.avgWinLoss=AvgWinLoss\r\n"
                    + "Movelistframe.keyPanel.avgScoreLoss=AvgScoreLoss\r\n"
                    + "Movelistframe.statisticsPanel=Statistics\r\n"
                    + "Movelistframe.accuracyAndMatch.tip=In Statistics tab,mouse over Accuracy and Match will show tip.\r\n"
                    + "Movelistframe.chkCustom=Other\r\n"
                    + "Movelistframe.winLoss=WinRate Loss\r\n"
                    + "Movelistframe.scoreLoss=Score Loss\r\n"
                    + "Movelistframe.btnSetTabelThresold=List Filter\r\n"
                    + "Movelistframe.btnSetStatisticsThresold=Statistics THR\r\n"
                    + "Movelistframe.settingsToolTip=What's accuracy?\r\n"
                    + "Movelistframe.engineGameHint=Engine game must be auto analyzed first\r\n"
                    + "Movelistframe.black=Black\r\n"
                    + "Movelistframe.white=White\r\n"
                    + "Movelistframe.minTableColumnBlack=Black\r\n"
                    + "Movelistframe.minTableColumnWhite=White\r\n"
                    + "Movelistframe.minTableColumnCoords=Coords\r\n"
                    + "Movelistframe.minTableColumnWinDiff=WinDiff\r\n"
                    + "Movelistframe.minTableColumnScoreDiff=ScoreDiff\r\n"
                    + "Movelistframe.minTableColumnAiScore=Accuracy\r\n"
                    + "Movelistframe.tableColumnColor=Color\r\n"
                    + "Movelistframe.tableColumnMoveNum=MoveNum\r\n"
                    + "Movelistframe.tableColumnThisWin=WinRate\r\n"
                    + "Movelistframe.tableColumnBestWin=BestWin\r\n"
                    + "Movelistframe.tableColumnThisScore=Score\r\n"
                    + "Movelistframe.tableColumnBestScore=BestScore\r\n"
                    + "Movelistframe.tableColumnPlayouts=Visits\r\n"
                    + "Movelistframe.tableColumnNextPlayouts=NextVisits\r\n"
                    + "Movelistframe.all=All\r\n"
                    + "Movelistframe.open=Opening\r\n"
                    + "Movelistframe.middle=Middle\r\n"
                    + "Movelistframe.end=End\r\n"
                    + "Movelistframe.toEnd=-end\r\n"
                    + "Movelistframe.blackOpen=B: Opening(<\r\n"
                    + "Movelistframe.whiteOpen=W: Opening(<\r\n"
                    + "Movelistframe.moves=moves,\r\n"
                    + "Movelistframe.movesAnd>=\\ moves,>\r\n"
                    + "Movelistframe.movesAndMiddle=\\ moves) Middle(<\r\n"
                    + "Movelistframe.movesAndEnd=\\ moves) End(<\r\n"
                    + "Movelistframe.movesAndRightBrackets=\\ moves)\r\n"
                    + "Movelistframe.pts=pts \r\n"
                    + "Movelistframe.blackDifference=B Loss:\r\n"
                    + "Movelistframe.whiteDifference=W Loss:\r\n"
                    + "Movelistframe.allWinrate=\\ All(winRate \r\n"
                    + "Movelistframe.percentAndScore=% score \r\n"
                    + "Movelistframe.openAndWinrate=) Opening(winRate \r\n"
                    + "Movelistframe.middleAndWinrate=) Middle(winRate \r\n"
                    + "Movelistframe.endAndWinrate=) End(winRate \r\n"
                    + "Movelistframe.blackAIScore=B: Accuracy \r\n"
                    + "Movelistframe.whiteAIScore=W: Accuracy \r\n"
                    + "Movelistframe.blackMatch=B: Match \r\n"
                    + "Movelistframe.AIscore=\\ Accuracy \r\n"
                    + "Movelistframe.avgDifference=\\ AvgLoss win/score \r\n"
                    + "Movelistframe.whiteMatch=W: Match \r\n"
                    + "Movelistframe.allmoves=\\  All moves \r\n"
                    + "Movelistframe.analyzed=\\ analyzed \r\n"
                    + "Movelistframe.blackMatchLabel=BlackMatch\r\n"
                    + "Movelistframe.whiteMatchLabel=WhiteMatch\r\n"
                    + "Movelistframe.analyzedLabel=Analyzed\r\n"
                    + "Movelistframe.unanalyzedLabel=Unanalyzed\r\n"
                    + "Movelistframe.scoreLeadLabel=ScoreLead\r\n"
                    + "Movelistframe.blackAnalyzed=B: Analyzed \r\n"
                    + "Movelistframe.movedAndAverage=\\ moves average \r\n"
                    + "Movelistframe.percentAndStdev=% stdev \r\n"
                    + "Movelistframe.above=(above \r\n"
                    + "Movelistframe.rightBrackets=)\r\n"
                    + "Movelistframe.whiteAnalyzed=W: Analyzed \r\n"
                    + "Movelistframe.scoreAndStdev=\\ score stdev \r\n"
                    + "Movelistframe.scorePoints=pts:\r\n"
                    + "Movelistframe.stdev=\\ stdev  \r\n"
                    + "Movelistframe.checkBlackFilter=B\r\n"
                    + "Movelistframe.checkWhiteFilter=W\r\n"
                    + "Movelistframe.lblDropWinrate=WinRate Difference\r\n"
                    + "Movelistframe.lblDropScore=Score Difference\r\n"
                    + "Movelistframe.lblPlayouts=Visits(Both previous and next)\r\n"
                    + "Movelistframe.lblShowBranch=Branch\r\n"
                    + "Movelistframe.lblShowBranchItemMain=Main\r\n"
                    + "Movelistframe.lblShowBranchItemCurrent=Current\r\n"
                    + "Movelistframe.current=Current\r\n"
                    + "Movelistframe.chkTopCurrentMove=Last Move On Top(Detail List)\r\n"
                    + "Movelistframe.showGraph=ShowGraph\r\n"
                    + "Movelistframe.hideGraph=HideGraph\r\n"
                    + "Movelistframe.columnHead.PreivousWinRate=PreviousWinRate\r\n"
                    + "Movelistframe.title=HawkEye\r\n"
                    + "Movelistframe.titleMain=(Main)\r\n"
                    + "Movelistframe.titleSub=(Sub)\r\n"
                    + "Movelistframe.titleHint=,Y-show/hide,Q-always on top\r\n"
                    + "Movelistframe.topPanle.simpleList=Simple List\r\n"
                    + "Movelistframe.topPanle.detailList=Detail List\r\n"
                    + "Movelistframe.winrateMatch=WinRateMatch\r\n"
                    + "Movelistframe.accuracyLine=AccuracyTrend\r\n"
                    + "Movelistframe.stageScore=StageAccuracy\r\n"
                    + "Movelistframe.winrateDifference=WinRateLossGraph\r\n"
                    + "Movelistframe.scoreDifference=ScoreLossGraph\r\n"
                    + "Movelistframe.winrateStatistics=WinLossStatistic\r\n"
                    + "Movelistframe.scoreStatistics=ScoreLossStatistic\r\n"
                    + "Movelistframe.winrateBigMistake=BigBlunder(Win)\r\n"
                    + "Movelistframe.scoreBigMistake=BigBlunder(Score)\r\n"
                    + "Movelistframe.lblMatchConfig1=Match condition: First\r\n"
                    + "Movelistframe.lblMatchConfig2=candidates and visits not below max *\r\n"
                    + "Movelistframe.lblMatchConfig4=From move\r\n"
                    + "Movelistframe.lblMatchConfig5=to\r\n"
                    + "Movelistframe.lblDiffConfig1=WinRate loss threshold: First:\r\n"
                    + "Movelistframe.lblDiffConfigScore=Score  loss threshold:   First:\r\n"
                    + "Movelistframe.lblDiffConfig2=Second:\r\n"
                    + "Movelistframe.openingEndMove=Opening End\r\n"
                    + "Movelistframe.middleEndMove=Middle End\r\n"
                    + "MovenumberDialog.lblChangeTo=Display last\\:\r\n"
                    + "MovenumberDialog.okButton=Confirm\r\n"
                    + "MovenumberDialog.title=Move number \r\n"
                    + "NewAnaGameDialog.lblDisableWRNInGame=Disable analysisWideRootNoise in Game\r\n"
                    + "NewAnaGameDialog.wrongAiMoveSettings=Wrong AI move settings!\r\n"
                    + "NewAnaGameDialog.lblAutoSave=Auto Save Game Record\r\n"
                    + "NewAnaGameDialog.chkAutoSaveDirectory=Lizzie directory\r\n"
                    + "NewAnaGameDialog.chkAutoSaveFolder=MyGames folder\r\n"
                    + "NewAnaGameDialog.btnRandomStart=Set random\r\n"
                    + "NewAnaGameDialog.chkUsePlayMode=Enter Play Mode(No winRate,variation etc)\r\n"
                    + "NewAnaGameDialog.chooseEngine=Choose Engine\r\n"
                    + "NewAnaGameDialog.firstVisits=AI Max Visits for BestMove\r\n"
                    + "NewAnaGameDialog.handicap=Handicap(19x19 only)\r\n"
                    + "NewAnaGameDialog.lblAiUsePureNet=AI Use Raw Net Only\r\n"
                    + "NewAnaGameDialog.me=Me\r\n"
                    + "NewAnaGameDialog.moveTime=AI Time Per Move(Seconds)\r\n"
                    + "NewAnaGameDialog.moveVisits=AI Max Visits Per Move\r\n"
                    + "NewAnaGameDialog.noEngineHint=Please choose engine\\!\r\n"
                    + "NewAnaGameDialog.okButton=Confirm\r\n"
                    + "NewAnaGameDialog.ponder=AI Ponder(At my turn)\r\n"
                    + "NewAnaGameDialog.random=AI Random Opening\r\n"
                    + "NewAnaGameDialog.resign=AI Resign Condition\r\n"
                    + "NewAnaGameDialog.resign0=After move\r\n"
                    + "NewAnaGameDialog.resign1=,constant\r\n"
                    + "NewAnaGameDialog.resign2=moves,winrateBelow\r\n"
                    + "NewAnaGameDialog.showBlack=Show Black Candidates\r\n"
                    + "NewAnaGameDialog.showWhite=Show White Candidates\r\n"
                    + "NewAnaGameDialog.title=New Game(Analyze mode)\r\n"
                    + "NewEngineGameDialog.multiSgfNotBatch=Multiple SGF opening without batch games,only one SGF will be used!\r\n"
                    + "NewEngineGameDialog.btnConfig=MoreSettings\r\n"
                    + "NewEngineGameDialog.btnSGFstart=MultiSGF\r\n"
                    + "NewEngineGameDialog.cbxRandomSgf1=inOrder\r\n"
                    + "NewEngineGameDialog.cbxRandomSgf2=random\r\n"
                    + "NewEngineGameDialog.checkBoxAllowPonder=Allow ponder(don't check if only one machine)\r\n"
                    + "NewEngineGameDialog.handicap=Handicap(19x19 only)\r\n"
                    + "NewEngineGameDialog.komi=Komi\r\n"
                    + "NewEngineGameDialog.lblAdvanceTime=Advance time setting\r\n"
                    + "NewEngineGameDialog.lblB=Black Settings\r\n"
                    + "NewEngineGameDialog.lblBatchGame=Batch game\r\n"
                    + "NewEngineGameDialog.lblContinue=Proceed current board\r\n"
                    + "NewEngineGameDialog.lblFirstPlayout=BestMove Visits\r\n"
                    + "NewEngineGameDialog.lblPlayout=Total Visits\r\n"
                    + "NewEngineGameDialog.lblTime=Time(s)\r\n"
                    + "NewEngineGameDialog.lblW=White Settings\r\n"
                    + "NewEngineGameDialog.lblengine=Choose engine\r\n"
                    + "NewEngineGameDialog.lblsgf=Load sgf opening\r\n"
                    + "NewEngineGameDialog.message=If check \"Loading sgf opening\",please choose at least one sgf\r\n"
                    + "NewEngineGameDialog.okButton=Confirm\r\n"
                    + "NewEngineGameDialog.title=Engine game\r\n"
                    + "NewGameDialog.kataTime=KataGo Time Settings\r\n"
                    + "NewGameDialog.kataTime.byoyomi=Byoyomi\r\n"
                    + "NewGameDialog.kataTime.fisher=Fisher\r\n"
                    + "NewGameDialog.kataTime.absolute=Absolute\r\n"
                    + "NewGameDialog.kataTime.increment=Increment(sec)\r\n"
                    + "NewGameDialog.useFreeHandicap=Enable free handicap(KataGo only)\r\n"
                    + "NewGameDialog.Black=Black\r\n"
                    + "NewGameDialog.ContinuePlay=Continue on Current Game\r\n"
                    + "NewGameDialog.Handicap=Handicap\r\n"
                    + "NewGameDialog.Komi=Komi\r\n"
                    + "NewGameDialog.White=White\r\n"
                    + "NewGameDialog.chkPonder=AI Ponder\r\n"
                    + "NewGameDialog.chkPonderDescribe=(Ponder at my turn,may need extra set in engine config file)\r\n"
                    + "NewGameDialog.chkUsePlayMode=Enter Play Mode\r\n"
                    + "NewGameDialog.chkUsePlayModeDescribe=(No winRate,variation etc)\r\n"
                    + "NewGameDialog.chooseBlackWhite=Choose My Side\r\n"
                    + "NewGameDialog.lblAdvanceTime=Advance Time Settings\r\n"
                    + "NewGameDialog.noTime=Don't Use Time Settings\r\n"
                    + "NewGameDialog.okButton=Confirm\r\n"
                    + "NewGameDialog.playBlack=Play Black\r\n"
                    + "NewGameDialog.playWhite=Play White\r\n"
                    + "NewGameDialog.time=AI Time Per Move(Seconds)\r\n"
                    + "NewGameDialog.title=New Game(Genmove mode)\r\n"
                    + "OnlineDialog.button.cancel=Cancel\r\n"
                    + "OnlineDialog.button.interrupt=Interrupt\r\n"
                    + "OnlineDialog.button.ok=OK\r\n"
                    + "OnlineDialog.lblError.text=Invalid url.\r\n"
                    + "OnlineDialog.lblPrompt1.text=Please input an available url.\r\n"
                    + "OnlineDialog.lblPrompt2.text=Support yikeLive,example\\:\r\n"
                    + "OnlineDialog.title.config=Online\r\n"
                    + "OnlineDialog.title.refresh=Refresh\r\n"
                    + "OnlineDialog.title.refreshTime=Second\r\n"
                    + "OnlineDialog.title.url=URL\r\n"
                    + "OtherPrograms.editEngine=Edit\r\n"
                    + "OtherPrograms.engineName=Click a row to edit\r\n"
                    + "OtherPrograms.program=Program\r\n"
                    + "OtherPrograms.selectProgram=Select program\r\n"
                    + "OtherPrograms.title=FastLink Manage\r\n"
                    + "PrivateKifuSearch.btnSearch=Search\r\n"
                    + "PrivateKifuSearch.copy=Copy\r\n"
                    + "PrivateKifuSearch.copySuccess=Copy successfully\\!\r\n"
                    + "PrivateKifuSearch.edit=Edit\r\n"
                    + "PrivateKifuSearch.labelAnalyzed=Analyzed(>)\\:\r\n"
                    + "PrivateKifuSearch.labelName=Name\\:\r\n"
                    + "PrivateKifuSearch.labelBlack=Black\\:\r\n"
                    + "PrivateKifuSearch.labelDateEnd=EndDate\\:\r\n"
                    + "PrivateKifuSearch.labelDateStart=StartDate\\:\r\n"
                    + "PrivateKifuSearch.labelLabel=Label\\:\r\n"
                    + "PrivateKifuSearch.labelMove=Move(>)\\:\r\n"
                    + "PrivateKifuSearch.labelOther=Other\\:\r\n"
                    + "PrivateKifuSearch.labelScore=Accuracy(>)\\:\r\n"
                    + "PrivateKifuSearch.labelUploader=Uploader\\:\r\n"
                    + "PrivateKifuSearch.labelWhite=White\\:\r\n"
                    + "PrivateKifuSearch.open=Open\r\n"
                    + "PrivateKifuSearch.sql.AIGame=AI game\r\n"
                    + "PrivateKifuSearch.sql.analyzed=analyzed\r\n"
                    + "PrivateKifuSearch.sql.black=black\r\n"
                    + "PrivateKifuSearch.sql.blackScore=blackAccuracy\r\n"
                    + "PrivateKifuSearch.sql.copy=copy\r\n"
                    + "PrivateKifuSearch.sql.edit=edit\r\n"
                    + "PrivateKifuSearch.sql.fileName=fileName\r\n"
                    + "PrivateKifuSearch.sql.index=idx\r\n"
                    + "PrivateKifuSearch.sql.isPublic=isPublic\r\n"
                    + "PrivateKifuSearch.sql.label=label\r\n"
                    + "PrivateKifuSearch.sql.lackInfo=lack of info\r\n"
                    + "PrivateKifuSearch.sql.move=move\r\n"
                    + "PrivateKifuSearch.sql.open=open\r\n"
                    + "PrivateKifuSearch.sql.otherInfo=otherInfo\r\n"
                    + "PrivateKifuSearch.sql.private=private\r\n"
                    + "PrivateKifuSearch.sql.public=public\r\n"
                    + "PrivateKifuSearch.sql.uploadTime=uploadTime\r\n"
                    + "PrivateKifuSearch.sql.uploader=uploader\r\n"
                    + "PrivateKifuSearch.sql.url=url\r\n"
                    + "PrivateKifuSearch.sql.view=viewInLizzie\r\n"
                    + "PrivateKifuSearch.sql.white=white\r\n"
                    + "PrivateKifuSearch.sql.whiteScore=whiteAccuracy\r\n"
                    + "PrivateKifuSearch.title=Search (edit) share information\r\n"
                    + "PublicKifuSearch.title=Search public game records\r\n"
                    + "PublicKifuSearch.view=View\r\n"
                    + "ReadBoard.language=en\r\n"
                    + "#Current support language for Board sync tool:cn en jp kr(last two is same as en,not translated)\r\n"
                    + "ReadBoard.loadFailed=Load board synchronization tool failed,please check ' readboard ' folder and make sure installed ' .NET Framework 4.0 ' !\\r\\nYou can download here: https://aistudio.baidu.com/aistudio/datasetdetail/116865(readboard folder in packages is the newest version)\r\n"
                    + "ReadBoard.port=Port\r\n"
                    + "ReadBoard.portUsed=is used\r\n"
                    + "ReadBoard.versionCheckFaied=Board synchronization tool version is out-of-date,please update !\\r\\nDownload: https://aistudio.baidu.com/aistudio/datasetdetail/116865(readboard folder in packages is the newest version)\r\n"
                    + "RightClickMenu.findMove=Find move\r\n"
                    + "RightClickMenu.addSuggestionAsBranch=Add as branch\r\n"
                    + "RightClickMenu.addblack=Add black\r\n"
                    + "RightClickMenu.addwhite=Add white\r\n"
                    + "RightClickMenu.allow=Allow\r\n"
                    + "RightClickMenu.allow2=Add allow\r\n"
                    + "RightClickMenu.allow3=Remove allow\r\n"
                    + "RightClickMenu.avoid=Avoid\r\n"
                    + "RightClickMenu.avoid2=Keep in allow/avoid\r\n"
                    + "RightClickMenu.previousMove=Previous move\r\n"
                    + "RightClickMenu.cancelavoid=Cancel avoid/allow\r\n"
                    + "RightClickMenu.cleanedittemp=Clear edit cache\r\n"
                    + "RightClickMenu.cleanupedit=Undo\r\n"
                    + "RightClickMenu.clearPriority=Clear priority\r\n"
                    + "RightClickMenu.priority=Increase priority\r\n"
                    + "RightClickMenu.reedit=Redo\r\n"
                    + "RightClickMenu.regretOne=Regret move\r\n"
                    + "RightClickMenu2.findStone=Jump to move\r\n"
                    + "RightClickMenu2.deleteone=Delete\r\n"
                    + "RightClickMenu2.moveStone=Move\r\n"
                    + "RightClickMenu2.switchone=Exchange\r\n"
                    + "RightClickMenu2.review=Review\r\n"
                    + "SetAiTimes.lblKataTime=KataGo time\\:\r\n"
                    + "SetAiTimes.analyzeMode=AnalyzeMode\r\n"
                    + "SetAiTimes.cancel=Cancel\r\n"
                    + "SetAiTimes.lblAdvTime=Advance time\\:\r\n"
                    + "SetAiTimes.lblFirstPo=First Visits(optional)\\:\r\n"
                    + "SetAiTimes.lblGenmoveMode=GenmoveMode\r\n"
                    + "SetAiTimes.lblGenmoveTime=Time(s)\\:\r\n"
                    + "SetAiTimes.lblPo=Visits(optional)\\:\r\n"
                    + "SetAiTimes.lblPonder=Enable AI ponder\r\n"
                    + "SetAiTimes.lblTime=Time(s)\\:\r\n"
                    + "SetAiTimes.okButton=Confirm\r\n"
                    + "SetAiTimes.rdoNoPonder=No\r\n"
                    + "SetAiTimes.rdoPonder=Yes\r\n"
                    + "SetAiTimes.title=AI time control settings\r\n"
                    + "SetFrameFontSize.lblSetSize=Set global font size(12-20)\\:\r\n"
                    + "SetFrameFontSize.okButton=Confirm\r\n"
                    + "SetFrameFontSize.successHint=Set successfully,Please restart Lizzie\\!\r\n"
                    + "SetFrameFontSize.title=Set frame font size\r\n"
                    + "SetAnaGameRandomStart.apply=Apply\r\n"
                    + "SetAnaGameRandomStart.cancel=Cancel\r\n"
                    + "SetAnaGameRandomStart.chkEnable=Enable\r\n"
                    + "SetAnaGameRandomStart.lblFirst.text=First\r\n"
                    + "SetAnaGameRandomStart.lblMove.text=Move\r\n"
                    + "SetAnaGameRandomStart.lblVisits.text=Visits not lower than bestMove *\r\n"
                    + "SetAnaGameRandomStart.lblWinrate.text=WinRate not lower than bestMove -\r\n"
                    + "SetAnaGameRandomStart.title=Set Random\r\n"
                    + "SetAnaGameRandomStart.wrongFormat=Wrong format\r\n"
                    + "SetBoardSize.okButton=Confirm\r\n"
                    + "SetBoardSize.rdo13=13x13\r\n"
                    + "SetBoardSize.rdo15=15x15\r\n"
                    + "SetBoardSize.rdo19=19x19\r\n"
                    + "SetBoardSize.rdo9=9x9\r\n"
                    + "SetBoardSize.rdoOther=Other\r\n"
                    + "SetBoardSize.title=Set board size\r\n"
                    + "SetBoardSyncTime.lblSetInterval=Set sync interval(ms)\\:\r\n"
                    + "SetBoardSyncTime.okButton=Confirm\r\n"
                    + "SetBoardSyncTime.title=Set sync interval\r\n"
                    + "SetEstimateParam.lblEstmateEngine=Estimate engine\\:\r\n"
                    + "SetEstimateParam.lblHint=Tip\\: KataGo's estimate roughly result judging by threshold,if you want an better result,load katago engine and use Kata estimate\r\n"
                    + "SetEstimateParam.lblKatago=KataGo engine command(at least v1.33)\\:\r\n"
                    + "SetEstimateParam.lblThreshold=Threshold(KataGo)\\:\r\n"
                    + "SetEstimateParam.lblZenEngineCommand=Zen engine command(make sure zen.dll is along with engine)\\:\r\n"
                    + "SetEstimateParam.okButton=OK\r\n"
                    + "SetEstimateParam.rdoKataGo=KataGo\r\n"
                    + "SetEstimateParam.rdoZen=Zen\r\n"
                    + "SetEstimateParam.title=Set estimate engine\r\n"
                    + "AnalysisSettings.lblHint2=<html>Flash analysis use <font color=\"red\">KataGo analysis engine</font>,different from gtp engine,click <b><a href=\"https://github.com/lightvector/KataGo/blob/master/docs/Analysis_Engine.md\"><font color=\"blue\">here</font></a></b> to get more information.</html>\r\n"
                    + "SetKataEngines.lblPdaEngineUseMenu=PDA engine must set this value in menu.\r\n"
                    + "SetKataEngines.lblNumSearchThreads=numSearchThreads  (The number of CPU threads to use):\r\n"
                    + "SetKataEngines.Hint2=<html>Modified may <font color\\=\"red\">affect strength</font>,click <b><a href\\=\"https://github.com/lightvector/KataGo/blob/20d34784703c5b4000643d3ccc43bb37d418f3b5/cpp/configs/gtp_example.cfg#L76\"><font color\\=\"blue\">here</font></a></b> to get more information.</html>\r\n"
                    + "SetKataEngines.btnApply=Confirm\r\n"
                    + "SetKataEngines.btnCancel=Cancel\r\n"
                    + "SetKataEngines.lblAutoLoad=Auto Load\r\n"
                    + "SetKataEngines.lblHint=Tip\\: Need at least KataGo v1.4 engine\r\n"
                    + "SetKataEngines.lblPDA=playoutDoublingAdvantage  (default 0.0,range -3.0 to 3.0)\\:\r\n"
                    + "SetKataEngines.lblRPT=rootPolicyTemperature  (default 1.0,range 0.01 to 100.0)\\:\r\n"
                    + "SetKataEngines.lblShowInMenu=Set In Toolbar\r\n"
                    + "SetKataEngines.lblWRN=analysisWideRootNoise  (default 0.04,range 0.0 to 2.0)\\:\r\n"
                    + "SetKataEngines.title=Set KataGo engine parameters\r\n"
                    + "SetKataPDA.lblTip=Tip\\: PDA will be reset by handicaps after dynamic ratio changed.\r\n"
                    + "SetKataPDA.btnApply=Apply\r\n"
                    + "SetKataPDA.btnCancel=Cancel\r\n"
                    + "SetKataPDA.chkAutoPDA=Auto load this settings(every PDA engine)\r\n"
                    + "SetKataPDA.chkDymPda=Dynamic\r\n"
                    + "SetKataPDA.chkNoPDA=Disable\r\n"
                    + "SetKataPDA.chkStaticPda=Static\r\n"
                    + "SetKataPDA.lblCurPDA=Current PDA\\:\r\n"
                    + "SetKataPDA.lblDymRatio=Ratio\r\n"
                    + "SetKataPDA.lblpdakatagopdapda=Tip\\: PDA is a parameter preferring to play aggressively or defensively\r\n"
                    + "SetKataPDA.title=KataGo PDA parameter settings\r\n"
                    + "SetKataPDA.wrongParameter=Wrong parameter\r\n"
                    + "SetKataRules.btnApply=Apply\r\n"
                    + "SetKataRules.btnCancel=Cancel\r\n"
                    + "SetKataRules.btnChnOldRule=ChnAncientRules\r\n"
                    + "SetKataRules.btnChnRule=ChinaRules\r\n"
                    + "SetKataRules.btnJpnRule=JapanRules\r\n"
                    + "SetKataRules.btnTTRule=Tromp-TaylorRules\r\n"
                    + "SetKataRules.chkbxAutoLoadRules=Auto load this rules\r\n"
                    + "SetKataRules.lblClassicRules=ClassicRules\\:\r\n"
                    + "SetKataRules.lblHasButton=HasButton\\:\r\n"
                    + "SetKataRules.lblKoRule=KoRules\\:\r\n"
                    + "SetKataRules.lblMultiStoneSuicide=MultiStoneSuicide\\:\r\n"
                    + "SetKataRules.lblScoringRule=Scoring\\:\r\n"
                    + "SetKataRules.lblTaxRule=TaxRule\\:\r\n"
                    + "SetKataRules.lblWhiteHandicapBonus=WhiteHandicapBonus\\:\r\n"
                    + "SetKataRules.notKataGoHint=Current engine isn't KataGo,rule settings maybe failed\r\n"
                    + "SetKataRules.rdoAllTax=All\r\n"
                    + "SetKataRules.rdoArea=Area\r\n"
                    + "SetKataRules.rdoButtonGo=Yes\r\n"
                    + "SetKataRules.rdoHandicapKomiN=N\r\n"
                    + "SetKataRules.rdoHandicapKomiN1=N-1\r\n"
                    + "SetKataRules.rdoNoButtonGo=No\r\n"
                    + "SetKataRules.rdoNoHandicapKomi=0\r\n"
                    + "SetKataRules.rdoNoSuicide=Forbidden\r\n"
                    + "SetKataRules.rdoNoTax=None\r\n"
                    + "SetKataRules.rdoPositionKo=Position\r\n"
                    + "SetKataRules.rdoSeKiTax=SeKi\r\n"
                    + "SetKataRules.rdoSimpleKo=Simple\r\n"
                    + "SetKataRules.rdoSituationalKo=Situational\r\n"
                    + "SetKataRules.rdoSuicide=Allow\r\n"
                    + "SetKataRules.rdoTerritory=Territory\r\n"
                    + "SetKataRules.title=KataGo rule settings(at least v1.3)\r\n"
                    + "SetKomi.title=Set komi\r\n"
                    + "SetLeelaEngines.btnApply=Confirm\r\n"
                    + "SetLeelaEngines.btnCancel=Cancel\r\n"
                    + "SetLeelaEngines.lblAutoLoad=Auto load\r\n"
                    + "SetLeelaEngines.lblHint=Tip\\: Need Leela/Sai engine\r\n"
                    + "SetLeelaEngines.lblLagbuffer=GTP lagbuffer  (default 1,range 0 to 3000)\\:\r\n"
                    + "SetLeelaEngines.lblRam=Max ram limit  (MiB,default 2048,range 128 to 131072)\\:\r\n"
                    + "SetLeelaEngines.lblResign=Resign winRate  (%,default -1,range -1 to 30)\\:\r\n"
                    + "SetLeelaEngines.lblVisits=Max visits limit  (range 0 to 1000000000,0\\=no limit)\\:\r\n"
                    + "SetLeelaEngines.title=Set Leela/Sai engine parameters\r\n"
                    + "SetMatchAiPara.lblAi=Accuracy based on the difference from each move you played and candidates\r\n"
                    + "SetMatchAiPara.lblCalculation=Formula\\:\r\n"
                    + "SetMatchAiPara.title=About accuracy\r\n"
                    + "SetReplayTime.lblInterval=Interval(ms)\\:\r\n"
                    + "SetReplayTime.okButton=OK\r\n"
                    + "SetReplayTime.title=Set variation replay interval(hotKey R on mouse over)\r\n"
                    + "SetShareLabel.label_0=Label1\\:\r\n"
                    + "SetShareLabel.label_1=Label2\\:\r\n"
                    + "SetShareLabel.label_2=Label3\\:\r\n"
                    + "SetShareLabel.label_3=Label4\\:\r\n"
                    + "SetShareLabel.label_4=Label5\\:\r\n"
                    + "SetShareLabel.saveButton=Save\r\n"
                    + "SetShareLabel.title=Set Labels\r\n"
                    + "loadEngine.column0=Index\r\n"
                    + "loadEngine.column1=Name\r\n"
                    + "loadEngine.column2=Command\r\n"
                    + "loadEngine.column3=PreLoad\r\n"
                    + "loadEngine.column4=W\r\n"
                    + "loadEngine.column5=H\r\n"
                    + "loadEngine.column6=Komi\r\n"
                    + "loadEngine.column7=Def\r\n"
                    + "loadEngine.exit=Exit\r\n"
                    + "loadEngine.no=no\r\n"
                    + "loadEngine.noEngine=No Engine\r\n"
                    + "loadEngine.ok=Load\r\n"
                    + "loadEngine.title=Choose engine(Double click)\r\n"
                    + "loadEngine.yes=yes\r\n"
                    + "loggin.btnLoggin=signUp/login\r\n"
                    + "loggin.noSpace=Failed,Do not use space in user name.\r\n"
                    + "loggin.checkBoxShow=show\r\n"
                    + "loggin.connectFailed=Connect failed...try again or download new version\\:\r\n"
                    + "loggin.labelPasswd=PassWd\\:\r\n"
                    + "loggin.labelUser=User(no symbol)\\:\r\n"
                    + "loggin.logginFailed=Login failed wrong password, or user was occupied.\r\n"
                    + "loggin.logginFailed2=Login failed don't use symbol\r\n"
                    + "loggin.logginInfo=Login information\r\n"
                    + "loggin.logginSucceed=Login succeed\r\n"
                    + "loggin.signUpSucceed=Sign up and login succeed\r\n"
                    + "loggin.title=Login\r\n"
                    + "menu.frameFontSize=Frame font size\r\n"
                    + "menu.frameFontSizeSmall=Small\r\n"
                    + "menu.frameFontSizeMiddle=Middle\r\n"
                    + "menu.frameFontSizeBig=Big\r\n"
                    + "menu.frameLooks=Frame looks\r\n"
                    + "menu.frameLooksSystem=System\r\n"
                    + "menu.frameLooksJava=Java\r\n"
                    + "menu.language=Language\r\n"
                    + "menu.language.default=Default"),
            2);
      }
    }
    if (line.startsWith("error")) {
      Lizzie.gtpConsole.addLineReadBoard(line + (usePipe ? "" : "\n"));
    }
    if (line.startsWith("end")) {
      if (!isSyncing) syncBoardStones(false);
      tempcount = new ArrayList<Integer>();
    }
    if (line.startsWith("clear")) {
      Lizzie.board.clear(false);
      Lizzie.frame.refresh();
    }
    if (line.startsWith("start")) {
      String[] params = line.trim().split(" ");
      if (params.length >= 3) {
        int boardWidth = Integer.parseInt(params[1]);
        int boardHeight = Integer.parseInt(params[2]);
        if (boardWidth != Board.boardWidth || boardHeight != Board.boardHeight) {
          Lizzie.board.reopen(boardWidth, boardHeight);
        } else {
          Lizzie.board.clear(false);
        }
      } else {
        Lizzie.board.clear(false);
      }
    }
    if (line.startsWith("sync")) {
      Lizzie.frame.syncBoard = true;
      if (!Lizzie.leelaz.isPondering()) Lizzie.leelaz.togglePonder();
    }
    if (line.startsWith("both")) {
      Lizzie.frame.bothSync = true;
      if (Lizzie.frame.floatBoard != null && Lizzie.frame.floatBoard.isVisible())
        Lizzie.frame.floatBoard.setEditButton();
    }
    if (line.startsWith("noboth")) {
      Lizzie.frame.bothSync = false;
      if (Lizzie.frame.floatBoard != null && Lizzie.frame.floatBoard.isVisible())
        Lizzie.frame.floatBoard.setEditButton();
    }
    if (line.startsWith("stopAutoPlay")) {
      LizzieFrame.toolbar.chkAutoPlay.setSelected(false);
      LizzieFrame.toolbar.isAutoPlay = false;
    }
    if (line.startsWith("endsync")) {
      noMsg = true;
      Lizzie.frame.syncBoard = false;
      if (Lizzie.frame.isAnaPlayingAgainstLeelaz) {
        Lizzie.frame.stopAiPlayingAndPolicy();
      }
      showInBoard = false;
      if (Lizzie.frame.floatBoard != null) {
        Lizzie.frame.floatBoard.setVisible(false);
      }
    }
    if (line.startsWith("stopsync")) {
      Lizzie.frame.syncBoard = false;
      if (Lizzie.frame.isAnaPlayingAgainstLeelaz) {
        Lizzie.frame.stopAiPlayingAndPolicy();
      }
      Lizzie.leelaz.nameCmd();
      showInBoard = false;
      if (Lizzie.frame.floatBoard != null) {
        Lizzie.frame.floatBoard.setVisible(false);
      }
    }
    if (line.startsWith("play")) {
      String[] params = line.trim().split(">");
      if (params.length == 3) {
        String[] playParams = params[2].trim().split(" ");
        int playouts = Integer.parseInt(playParams[1]);
        int firstPlayouts = Integer.parseInt(playParams[2]);
        int time = Integer.parseInt(playParams[0]);
        if (time > 0) {
          LizzieFrame.toolbar.txtAutoPlayTime.setText(String.valueOf(time));
          LizzieFrame.toolbar.chkAutoPlayTime.setSelected(true);
        } else {
          LizzieFrame.toolbar.txtAutoPlayTime.setText(
              String.valueOf(Lizzie.config.leelazConfig.getInt("max-game-thinking-time-seconds")));
          LizzieFrame.toolbar.chkAutoPlayTime.setSelected(true);
        }
        if (playouts > 0) {
          LizzieFrame.toolbar.txtAutoPlayPlayouts.setText(String.valueOf(playouts));
          LizzieFrame.toolbar.chkAutoPlayPlayouts.setSelected(true);
        } else LizzieFrame.toolbar.chkAutoPlayPlayouts.setSelected(false);
        if (firstPlayouts > 0) {
          LizzieFrame.toolbar.txtAutoPlayFirstPlayouts.setText(String.valueOf(firstPlayouts));
          LizzieFrame.toolbar.chkAutoPlayFirstPlayouts.setSelected(true);
        } else LizzieFrame.toolbar.chkAutoPlayFirstPlayouts.setSelected(false);
        if (params[1].equals("black")) {
          LizzieFrame.toolbar.chkAutoPlayBlack.setSelected(true);
          LizzieFrame.toolbar.chkAutoPlayWhite.setSelected(false);
          LizzieFrame.toolbar.chkAutoPlay.setSelected(true);
          LizzieFrame.toolbar.setChkShowBlack(true);
          LizzieFrame.toolbar.setChkShowWhite(true);
          Lizzie.config.UsePureNetInGame = false;
          Lizzie.frame.isAnaPlayingAgainstLeelaz = true;
          LizzieFrame.toolbar.isAutoPlay = true;
          Lizzie.frame.clearWRNforGame(false);
        } else if (params[1].equals("white")) {
          LizzieFrame.toolbar.chkAutoPlayBlack.setSelected(false);
          LizzieFrame.toolbar.chkAutoPlayWhite.setSelected(true);
          LizzieFrame.toolbar.chkAutoPlay.setSelected(true);
          LizzieFrame.toolbar.setChkShowBlack(true);
          LizzieFrame.toolbar.setChkShowWhite(true);
          Lizzie.config.UsePureNetInGame = false;
          Lizzie.frame.isAnaPlayingAgainstLeelaz = true;
          LizzieFrame.toolbar.isAutoPlay = true;
          Lizzie.frame.clearWRNforGame(false);
        }
        Lizzie.leelaz.ponder();
      }
    }
    if (line.startsWith("pass")) {
      Lizzie.board.changeNextTurn();
    }
    if (line.startsWith("firstchanged")) {
      String[] params = line.trim().split(" ");
      if (params.length == 2) {
        int firstPlayouts = Integer.parseInt(params[1]);
        if (firstPlayouts > 0) {
          LizzieFrame.toolbar.txtAutoPlayFirstPlayouts.setText(String.valueOf(firstPlayouts));
          LizzieFrame.toolbar.chkAutoPlayFirstPlayouts.setSelected(true);
        } else LizzieFrame.toolbar.chkAutoPlayFirstPlayouts.setSelected(false);
      }
    }
    if (line.startsWith("playoutschanged")) {
      String[] params = line.trim().split(" ");
      if (params.length == 2) {
        int playouts = Integer.parseInt(params[1]);
        if (playouts > 0) {
          LizzieFrame.toolbar.txtAutoPlayPlayouts.setText(String.valueOf(playouts));
          LizzieFrame.toolbar.chkAutoPlayPlayouts.setSelected(true);
        } else LizzieFrame.toolbar.chkAutoPlayPlayouts.setSelected(false);
      }
    }
    if (line.startsWith("timechanged")) {
      String[] params = line.trim().split(" ");
      if (params.length == 2) {
        int time = Integer.parseInt(params[1]);
        if (time > 0) {
          LizzieFrame.toolbar.txtAutoPlayTime.setText(String.valueOf(time));
          LizzieFrame.toolbar.chkAutoPlayTime.setSelected(true);
        } else {
          LizzieFrame.toolbar.txtAutoPlayTime.setText(
              String.valueOf(Lizzie.config.leelazConfig.getInt("max-game-thinking-time-seconds")));
          LizzieFrame.toolbar.chkAutoPlayTime.setSelected(true);
        }
      }
    }

    if (line.startsWith("noponder")) {
      if (Lizzie.frame.isPlayingAgainstLeelaz) {
        Lizzie.frame.isPlayingAgainstLeelaz = false;
        Lizzie.leelaz.isThinking = false;
      }
      if (Lizzie.frame.isAnaPlayingAgainstLeelaz) {
        Lizzie.frame.stopAiPlayingAndPolicy();
      }
      Lizzie.leelaz.togglePonder();
    }
    if (line.startsWith("noinboard")) {
      if (Lizzie.frame.floatBoard != null && Lizzie.frame.floatBoard.isVisible()) {
        Lizzie.frame.floatBoard.setVisible(false);
      }
    }
    if (line.startsWith("inboard")) {
      //	Lizzie.gtpConsole.addLine(line);
      if (hideFromPlace) return;
      showInBoard = true;
      String[] params = line.trim().split(" ");
      if (params.length == 6) {
        if (params[5].startsWith("99")) {
          String[] param = params[5].split("_");
          float factor = Float.parseFloat(param[1]);
          if (Lizzie.frame.floatBoard == null) {
            Lizzie.frame.floatBoard =
                new FloatBoard(
                    (int) Math.ceil(Integer.parseInt(params[1]) * factor),
                    (int) Math.ceil(Integer.parseInt(params[2]) * factor),
                    (int) Math.ceil(Integer.parseInt(params[3]) * factor),
                    (int) Math.ceil(Integer.parseInt(params[4]) * factor),
                    Integer.parseInt(param[2]),
                    true);
            // Lizzie.frame.floatBoard.setFactor(factor);
          } else {
            Lizzie.frame.floatBoard.setPos(
                (int) Math.ceil(Integer.parseInt(params[1]) * factor),
                (int) Math.ceil(Integer.parseInt(params[2]) * factor),
                (int) Math.ceil(Integer.parseInt(params[3]) * factor),
                (int) Math.ceil(Integer.parseInt(params[4]) * factor),
                Integer.parseInt(param[2]));
            //   Lizzie.frame.floatBoard.setFactor(factor);
          }
        } else {
          if (Lizzie.frame.floatBoard == null) {
            Lizzie.frame.floatBoard =
                new FloatBoard(
                    Integer.parseInt(params[1]),
                    Integer.parseInt(params[2]),
                    Integer.parseInt(params[3]),
                    Integer.parseInt(params[4]),
                    Integer.parseInt(params[5]),
                    false);
            Lizzie.frame.floatBoard.setBoardType();
          } else {
            Lizzie.frame.floatBoard.setPos(
                Integer.parseInt(params[1]),
                Integer.parseInt(params[2]),
                Integer.parseInt(params[3]),
                Integer.parseInt(params[4]),
                Integer.parseInt(params[5]));
          }
        }
      }
    }
    if (line.startsWith("notinboard")) {
      showInBoard = false;
      if (Lizzie.frame.floatBoard != null) {
        Lizzie.frame.floatBoard.setVisible(false);
      }
    }
    if (line.startsWith("foreFoxWithInBoard")) {
      hideFloadBoardBeforePlace = true;
    }
    if (line.startsWith("notForeFoxWithInBoard")) {
      hideFloadBoardBeforePlace = false;
    }
    if (line.startsWith("placeComplete")) {
      if (hideFloadBoardBeforePlace && hideFromPlace) {
        hideFromPlace = false;
        if (Lizzie.frame.floatBoard != null) Lizzie.frame.floatBoard.setVisible(true);
      }
    }
  }

  private void syncBoardStones(boolean isSecondTime) {
    //    if (!this.javaReadBoard && !isSecondTime) {
    //      long thisTime = System.currentTimeMillis();
    //      if (thisTime - startSyncTime < Lizzie.config.readBoardArg2 / 2) return;
    //      startSyncTime = thisTime;
    //    }
    if (tempcount.size() > Board.boardWidth * Board.boardHeight) {
      tempcount = new ArrayList<Integer>();
      return;
    }
    isSyncing = true;
    boolean needReSync = false;
    boolean played = false;
    boolean holdLastMove = false;
    int lastX = 0;
    int lastY = 0;
    int playedMove = 0;
    boolean isLastBlack = false;
    BoardHistoryNode node = Lizzie.board.getHistory().getCurrentHistoryNode();
    BoardHistoryNode node2 = Lizzie.board.getHistory().getMainEnd();
    Stone[] stones = Lizzie.board.getHistory().getMainEnd().getData().stones;

    boolean needRefresh = false;
    for (int i = 0; i < tempcount.size(); i++) {
      int m = tempcount.get(i);
      int y = i / Board.boardWidth;
      int x = i % Board.boardWidth;
      if (((holdLastMove && m == 3) || m == 1) && !stones[Board.getIndex(x, y)].isBlack()) {
        if (stones[Board.getIndex(x, y)].isWhite()) {
          Lizzie.board.clear(false);
          needReSync = true;
          needRefresh = true;
          break;
        }
        if (!played) {
          Lizzie.board.moveToAnyPosition(node2);
        }
        Lizzie.board.placeForSync(x, y, Stone.BLACK, true);
        if (node2.variations.size() > 0 && node2.variations.get(0).isEndDummay()) {
          node2.variations.add(0, node2.variations.get(node2.variations.size() - 1));
          node2.variations.remove(1);
          node2.variations.remove(node2.variations.size() - 1);
        }
        played = true;
        playedMove = playedMove + 1;
      }
      if (((holdLastMove && m == 4) || m == 2) && !stones[Board.getIndex(x, y)].isWhite()) {
        if (stones[Board.getIndex(x, y)].isBlack()) {
          Lizzie.board.clear(false);
          needReSync = true;
          needRefresh = true;
          break;
        }

        if (!played) {
          Lizzie.board.moveToAnyPosition(node2);
        }
        Lizzie.board.placeForSync(x, y, Stone.WHITE, true);
        if (node2.variations.size() > 0 && node2.variations.get(0).isEndDummay()) {
          node2.variations.add(0, node2.variations.get(node2.variations.size() - 1));
          node2.variations.remove(1);
          node2.variations.remove(node2.variations.size() - 1);
        }
        played = true;
        playedMove = playedMove + 1;
      }

      if (!holdLastMove && m == 3 && !stones[Board.getIndex(x, y)].isBlack()) {
        if (stones[Board.getIndex(x, y)].isWhite()) {
          Lizzie.board.clear(false);
          needReSync = true;
          needRefresh = true;
          break;
        }
        holdLastMove = true;
        lastX = x;
        lastY = y;
        isLastBlack = true;
      }
      if (!holdLastMove && m == 4 && !stones[Board.getIndex(x, y)].isWhite()) {
        if (stones[Board.getIndex(x, y)].isBlack()) {
          Lizzie.board.clear(false);
          needReSync = true;
          needRefresh = true;
          break;
        }
        holdLastMove = true;
        lastX = x;
        lastY = y;
        isLastBlack = false;
      }
    }
    if (firstSync) {
      Lizzie.board.hasStartStone = true;
      Lizzie.board.addStartListAll();
      Lizzie.board.flatten();
    }
    // 落最后一步
    if (holdLastMove && !needReSync) {
      if (!played) {
        Lizzie.board.moveToAnyPosition(node2);
      }
      Lizzie.board.placeForSync(lastX, lastY, isLastBlack ? Stone.BLACK : Stone.WHITE, true);
      if (node2.variations.size() > 0 && node2.variations.get(0).isEndDummay()) {
        node2.variations.add(0, node2.variations.get(node2.variations.size() - 1));
        node2.variations.remove(1);
        node2.variations.remove(node2.variations.size() - 1);
      }
      played = true;
      if (Lizzie.config.alwaysSyncBoardStat || showInBoard) Lizzie.frame.lastMove();
    }
    stones = Lizzie.board.getHistory().getMainEnd().getData().stones;
    if ((Lizzie.config.alwaysSyncBoardStat) || showInBoard) {
      for (int i = 0; i < tempcount.size(); i++) {
        int m = tempcount.get(i);
        int y = i / Board.boardWidth;
        int x = i % Board.boardWidth;
        if (isStoneDiff(m, stones, x, y)) {
          needReSync = true;
          break;
        }
      }
    }
    if (!Lizzie.frame.bothSync && !needReSync) {
      if (played
          && !Lizzie.config.alwaysGotoLastOnLive
          && !(showInBoard
              && Lizzie.frame.floatBoard != null
              && !Lizzie.frame.floatBoard.hideSuggestion)
          && Lizzie.board.getHistory().getCurrentHistoryNode().previous().isPresent()
          && node != node2) {
        Lizzie.board.moveToAnyPosition(node);
      }
    }
    if (editMode) Lizzie.board.moveToAnyPosition(node);
    if (played) Lizzie.frame.renderVarTree(0, 0, false, false);
    if (needReSync && !isSecondTime) {
      Lizzie.board.clear(false);
      syncBoardStones(true);
    }
    if (played || needRefresh) {
      Lizzie.frame.refresh();
    }
    if (firstSync) {
      firstSync = false;
      Lizzie.board.previousMove(true);
      new Thread() {
        public void run() {
          try {
            Thread.sleep(500);
          } catch (InterruptedException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
          }
          Lizzie.frame.lastMove();
        }
      }.start();
    }
    if (Lizzie.frame.isPlayingAgainstLeelaz && needGenmove) {
      if (!Lizzie.board.getHistory().isBlacksTurn() && Lizzie.frame.playerIsBlack) {
        Lizzie.leelaz.genmove("W");
        needGenmove = false;
      } else if (!Lizzie.frame.playerIsBlack) {
        Lizzie.leelaz.genmove("B");
        needGenmove = false;
      }
    }
    isSyncing = false;
    //	    if (played && Lizzie.config.alwaysGotoLastOnLive) {
    //	      int moveNumber = Lizzie.board.getHistory().getMainEnd().getData().moveNumber;
    //	      Lizzie.board.goToMoveNumberBeyondBranch(moveNumber);
    //	      Lizzie.frame.refresh();
    //	    }
  }

  private boolean isStoneDiff(int m, Stone[] stones, int x, int y) {
    // TODO Auto-generated method stub
    Stone stone = stones[Board.getIndex(x, y)];
    if (m == 0 && stone != Stone.EMPTY) {
      if (Lizzie.frame.bothSync && lastMovePlayByLizzie) {
        BoardHistoryNode curNode = Lizzie.board.getHistory().getMainEnd();
        if (curNode.getData().lastMove.isPresent()) {
          int[] lastCoords = curNode.getData().lastMove.get();
          if (lastCoords[0] == x && lastCoords[1] == y) {
            return false;
          }
        }
      }
      return true;
    }
    if ((m == 1 || m == 3) && !stone.isBlack()) {
      return true;
    }
    if ((m == 2 || m == 4) && !stone.isWhite()) {
      return true;
    }
    return false;
  }

  public void shutdown() {
    noMsg = true;
    Lizzie.frame.syncBoard = false;
    Lizzie.frame.bothSync = false;
    this.sendCommand("quit");
    if (usePipe) {
      try {
        s.close();
        socket.close();
      } catch (Exception e) {
      }
    }
  }

  public void sendCommandTo(String command) {
    // if (Lizzie.gtpConsole.isVisible() || Lizzie.config.alwaysGtp)
    // Lizzie.gtpConsole.addReadBoardCommand(command);
    try {
      outputStream.write((command + "\n").getBytes());
      outputStream.flush();
    } catch (IOException e) {
      // e.printStackTrace();
    }
  }

  public void sendCommand(String command) {
    if (command.startsWith("place")) {
      if (hideFloadBoardBeforePlace && Lizzie.frame.floatBoard != null) {
        Lizzie.frame.floatBoard.setVisible(false);
        hideFromPlace = true;
      }
      lastMovePlayByLizzie = true;
      if (Lizzie.frame.isPlayingAgainstLeelaz) needGenmove = true;
    }
    if (usePipe) {
      sendCommandTo(command);
    } else if (readBoardStream != null) readBoardStream.sendCommand(command);
  }

  public void sendLossFocus() {
    // TODO Auto-generated method stub
    if (!Lizzie.config.readBoardGetFocus) return;
    sendCommand("loss");
  }

  public void checkVersion() {
    sendCommand("version");
  }

  // public void sendStopInBoard() {
  //	// TODO Auto-generated method stub
  //	 sendCommand("notinboard");
  // }
}
