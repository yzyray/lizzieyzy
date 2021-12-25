package featurecat.lizzie;

import featurecat.lizzie.analysis.AnalysisEngine;
import featurecat.lizzie.analysis.EngineManager;
import featurecat.lizzie.analysis.KataEstimate;
import featurecat.lizzie.analysis.Leelaz;
import featurecat.lizzie.gui.AwareScaled;
import featurecat.lizzie.gui.FirstUseSettings;
import featurecat.lizzie.gui.GtpConsolePane;
import featurecat.lizzie.gui.LizzieFrame;
import featurecat.lizzie.gui.LoadEngine;
import featurecat.lizzie.gui.Message;
import featurecat.lizzie.gui.SocketCheckVersion;
import featurecat.lizzie.rules.Board;
import featurecat.lizzie.util.MultiOutputStream;
import featurecat.lizzie.util.Utils;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Window;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.InetAddress;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.ResourceBundle;
import javax.swing.*;
import javax.swing.plaf.FontUIResource;
import org.jdesktop.swingx.util.OS;
import org.json.JSONException;

/** Main class. */
public class Lizzie {
  public static ResourceBundle resourceBundle = ResourceBundle.getBundle("l10n.DisplayStrings");
  public static Config config;
  public static GtpConsolePane gtpConsole;
  public static LizzieFrame frame;
  public static JDialog loadEngine;
  public static FirstUseSettings firstUseSettings;
  public static Board board;
  public static Leelaz leelaz;
  public static Leelaz leelaz2;
  public static String lizzieVersion = "yzy2.4.6.1";
  public static String checkVersion = "211117";
  public static boolean readMode = false;
  private static String[] mainArgs;
  public static EngineManager engineManager;
  public static int javaVersion = 8;
  public static Float javaScaleFactor = 1.0f;
  public static String javaVersionString = "";
  public static Float sysScaleFactor =
      OS.isWindows() ? (java.awt.Toolkit.getDefaultToolkit().getScreenResolution() / 96.0f) : 1.0f;

  /** Launches the game window, and runs the game. */
  public static void main(String[] args) throws IOException {
    mainArgs = args;
    config = new Config();
    try {
      LizzieFrame.uiFont = new Font("SansSerif", Font.TRUETYPE_FONT, 12);
      LizzieFrame.winrateFont =
          Font.createFont(
              Font.TRUETYPE_FONT,
              Thread.currentThread()
                  .getContextClassLoader()
                  .getResourceAsStream("fonts/OpenSans-Semibold.ttf"));
    } catch (IOException | FontFormatException e) {
      e.printStackTrace();
    }
    if (config.logConsoleToFile) {
      PrintStream oldPrintStream = System.out;
      FileOutputStream bos =
          new FileOutputStream("LastConsoleLogs_" + lizzieVersion + ".txt", true);
      MultiOutputStream multi = new MultiOutputStream(new PrintStream(bos), oldPrintStream);
      System.setOut(new PrintStream(multi));

      PrintStream oldErrorPrintStream = System.err;
      FileOutputStream bosError =
          new FileOutputStream("LastErrorLogs_" + lizzieVersion + ".txt", true);
      MultiOutputStream multiError =
          new MultiOutputStream(new PrintStream(bosError), oldErrorPrintStream);
      System.setErr(new PrintStream(multiError));
      String sf = new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss").format(new Date());
      bos.write((sf + "\n").getBytes());
      bosError.write((sf + "\n").getBytes());
    }
    // -Dsun.java2d.uiScale.enabled=false
    // -Dsun.java2d.win.uiScaleX=1.25 -Dsun.java2d.win.uiScaleY=1.25
    // -Dsun.java2d.win.uiScaleX=125% -Dsun.java2d.win.uiScaleY=125%
    // -Dsun.java2d.win.uiScaleX=120dpi -Dsun.java2d.win.uiScaleY=120dpi
    //  System.out.println(System.getProperty("sun.java2d.win.uiScaleX"));
    // System.setProperty("sun.java2d.uiScale.enabled", "false");
    javaVersionString = System.getProperty("java.version");
    try {
      javaVersion =
          Math.max(
              8, Integer.parseInt(javaVersionString.substring(0, javaVersionString.indexOf('.'))));
    } catch (Exception e) {
    }
    System.out.println("java version:" + javaVersionString);
    leelaz = new Leelaz("");

    AwareScaled awareScaled = new AwareScaled();
    awareScaled.setVisible(true);
    setLookAndFeel();
    String hostName = InetAddress.getLocalHost().getHostName();
    if (config.firstTimeLoad || !hostName.equals(config.hostName)) {
      if (!config.hostName.equals("")) config.deletePersist(false);
      resetAllHints();
      config.hostName = hostName;
      config.uiConfig.put("host-name", config.hostName);
      config.isChinese = (resourceBundle.getString("Lizzie.isChinese")).equals("yes");
      openFirstUseSettings(true);
    }
    while (Lizzie.config.needReopenFirstUseSettings) {
      if (config.useLanguage == 1)
        resourceBundle = ResourceBundle.getBundle("l10n.DisplayStrings", new Locale("zh", "CN"));
      else if (config.useLanguage == 2)
        resourceBundle = ResourceBundle.getBundle("l10n.DisplayStrings", new Locale("en", "US"));
      else if (config.useLanguage == 3)
        resourceBundle = ResourceBundle.getBundle("l10n.DisplayStrings", new Locale("ko"));
      else if (config.useLanguage == 4)
        resourceBundle = ResourceBundle.getBundle("l10n.DisplayStrings", new Locale("ja", "JP"));
      config.isChinese = (resourceBundle.getString("Lizzie.isChinese")).equals("yes");
      FirstUseSettings firstUseSettings = new FirstUseSettings(true);
      firstUseSettings.setVisible(true);
    }
    if (config.useLanguage == 1)
      resourceBundle = ResourceBundle.getBundle("l10n.DisplayStrings", new Locale("zh", "CN"));
    else if (config.useLanguage == 2)
      resourceBundle = ResourceBundle.getBundle("l10n.DisplayStrings", new Locale("en", "US"));
    else if (config.useLanguage == 3)
      resourceBundle = ResourceBundle.getBundle("l10n.DisplayStrings", new Locale("ko"));
    else if (config.useLanguage == 4)
      resourceBundle = ResourceBundle.getBundle("l10n.DisplayStrings", new Locale("ja", "JP"));
    config.isChinese = (resourceBundle.getString("Lizzie.isChinese")).equals("yes");
    if (config.theme.uiFontName() != null) config.uiFontName = config.theme.uiFontName();
    if (Lizzie.config.uiFontName != null
        && !(Lizzie.config.uiFontName.equals("Lizzie默认")
            || Lizzie.config.uiFontName.equals("Lizzie Default"))) {
      LizzieFrame.uiFont = new Font(Lizzie.config.uiFontName, Font.PLAIN, 12);
    }
    LizzieFrame.playoutsFont = new Font(Lizzie.config.fontName, Font.PLAIN, 12);
    if (Lizzie.config.winrateFontName != null
        && !(Lizzie.config.winrateFontName.equals("Lizzie默认")
            || Lizzie.config.winrateFontName.equals("Lizzie Default"))) {
      LizzieFrame.winrateFont = new Font(Lizzie.config.winrateFontName, Font.BOLD, 12);
    }
    config.shareLabel1 =
        config.uiConfig.optString(
            "share-label-1", resourceBundle.getString("ShareFrame.shareLabel1"));
    config.shareLabel2 =
        config.uiConfig.optString(
            "share-label-2", resourceBundle.getString("ShareFrame.shareLabel2"));
    config.shareLabel3 =
        config.uiConfig.optString(
            "share-label-3", resourceBundle.getString("ShareFrame.shareLabel3"));
    if (Lizzie.config.uiConfig.optBoolean("autoload-default", false)) {
      start(-1, true);
    } else if (Lizzie.config.uiConfig.optBoolean("autoload-last", false)) {
      int lastEngine = Lizzie.config.uiConfig.optInt("last-engine", -1);
      start(lastEngine, false);
    } else if (Lizzie.config.uiConfig.optBoolean("autoload-empty", false)) {
      start(-1, false);
    } else {
      if (mainArgs.length == 1) {
        if (mainArgs[0].equals("read")) {
          readMode = true;
          config.showStatus = false;
          start(-1, false);
          return;
        }
      }
      if (Utils.getEngineData().isEmpty()) {
        start(-1, false);
      } else {
        loadEngine = LoadEngine.createDialog();
        loadEngine.setVisible(true);
      }
    }
    if (Lizzie.config.autoReplayBranch) frame.autoReplayBranch();
  }

  public static void setFrameSize(Window frame, int width, int height) {
    if (javaVersion > 8)
      frame.setSize(
          (int) (width - 20 + (Config.isScaled ? 1.0 : Math.sqrt(Lizzie.sysScaleFactor)) * 30),
          (int) (height - 20 + (Config.isScaled ? 1.0 : Lizzie.sysScaleFactor) * 30));
    else
      frame.setSize(
          (int) (width - 20 + (Config.isScaled ? 1.0 : Math.sqrt(Lizzie.sysScaleFactor)) * 20),
          (int) (height - 20 + (Config.isScaled ? 1.0 : Lizzie.sysScaleFactor) * 25));
  }

  public static void openFirstUseSettings(boolean isOnload) {
    firstUseSettings = new FirstUseSettings(isOnload);
    firstUseSettings.setVisible(true);
  }

  public static void start(int index, boolean loadDefault) {
    board = new Board();
    frame = new LizzieFrame();
    LizzieFrame.menu.doubleMenu(true);
    frame.reSetLoc();
    frame.showMainPanel();
    frame.addResizeLis();
    SwingUtilities.invokeLater(
        new Thread() {
          public void run() {
            if (mainArgs.length == 1) {
              if (!mainArgs[0].equals("read")) {
                File file = new File(mainArgs[0]);
                frame.loadFile(file, true, true);
                LizzieFrame.curFile = file;
              }
            } else if (config.autoResume) {
              frame.resumeFile();
            }
            Lizzie.frame.setMainPanelFocus();
          }
        });
    gtpConsole = new GtpConsolePane(frame);
    gtpConsole.setVisible(config.persistedUi.optBoolean("gtp-console-opened", false));
    frame.setVisible(true);
    SwingUtilities.invokeLater(
        new Thread() {
          public void run() {
            if (config.isShowingIndependentMain) frame.openIndependentMainBoard();
            if (config.isShowingIndependentSub) frame.openIndependentSubBoard();
            try {
              Thread.sleep(60);
            } catch (InterruptedException e2) {
              // TODO Auto-generated catch block
              e2.printStackTrace();
            }
            try {
              Lizzie.engineManager = new EngineManager(Lizzie.config, index, loadDefault);
            } catch (Exception e) {
              try {
                Message msg = new Message();
                msg.setMessage(resourceBundle.getString("Lizzie.engineFailed"));
                //  msg.setVisible(true);
                Lizzie.engineManager = new EngineManager(Lizzie.config, -1, false);
                //  frame.refresh();
              } catch (JSONException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
              } catch (IOException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
              }
            }
            if (Lizzie.config.saveBoardConfig.optInt("save-auto-game-index2", -1) == -5) {
              Lizzie.config.saveBoardConfig.put("save-auto-game-index1", 1);
              File file = new File("save\\autoGame1.bmp");
              if (file.exists() && file.isFile()) file.delete();
              File file2 = new File("save\\autoGame1.sgf");
              if (file2.exists() && file2.isFile()) file2.delete();
              File oldfile = new File("save\\autoGame2.bmp");
              File newfile = new File("save\\autoGame1.bmp");
              if (oldfile.exists()) {
                oldfile.renameTo(newfile);
              }
              File oldfile2 = new File("save\\autoGame2.sgf");
              File newfile2 = new File("save\\autoGame1.sgf");
              if (oldfile2.exists()) {
                oldfile2.renameTo(newfile2);
              }
            }

            Runnable runnable2 =
                new Runnable() {
                  public void run() {
                    if (Lizzie.config.loadEstimateEngine) {
                      try {
                        frame.zen = new KataEstimate(true);
                      } catch (IOException e1) {
                        e1.printStackTrace();
                      }
                    }
                    if (Lizzie.config.analysisEnginePreLoad) {
                      try {
                        frame.analysisEngine = new AnalysisEngine(true);
                      } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                      }
                    }
                    if (config.autoCheckVersion) {
                      String date = new SimpleDateFormat("yyyyMMdd").format(new Date());
                      if (!config.autoCheckDate.equals(date)) {
                        SocketCheckVersion socketCheckVersion = new SocketCheckVersion();
                        socketCheckVersion.SocketCheckVersion(true);
                      }
                    }
                  }
                };
            Thread thread2 = new Thread(runnable2);
            thread2.start();
          }
        });
  }

  public static void setLookAndFeel() {
    try {
      if (System.getProperty("os.name").contains("Mac")) {
        if (config.useJavaLooks) setUIFont(new javax.swing.plaf.FontUIResource("", Font.PLAIN, 12));
        else System.setProperty("apple.laf.useScreenMenuBar", "true");
      } else {
        setUIFont(new javax.swing.plaf.FontUIResource("", Font.PLAIN, 12));
      }
      UIManager.put(
          "OptionPane.buttonFont",
          new FontUIResource(
              new Font(Config.sysDefaultFontName, Font.PLAIN, Config.frameFontSize)));
      UIManager.put(
          "OptionPane.messageFont",
          new FontUIResource(
              new Font(Config.sysDefaultFontName, Font.PLAIN, Config.frameFontSize)));
      if (config.useJavaLooks) {
        String lookAndFeel = UIManager.getCrossPlatformLookAndFeelClassName();
        UIManager.setLookAndFeel(lookAndFeel);
      } else {
        // String lookAndFeel = "com.sun.java.swing.plaf.windows.WindowsClassicLookAndFeel";
        String lookAndFeel = UIManager.getSystemLookAndFeelClassName();
        UIManager.setLookAndFeel(lookAndFeel);
      }
    } catch (IllegalAccessException e) {
      e.printStackTrace();
    } catch (ClassNotFoundException e) {
      e.printStackTrace();
    } catch (InstantiationException e) {
      e.printStackTrace();
    } catch (UnsupportedLookAndFeelException e) {
      e.printStackTrace();
    }
  }

  public static void resetLookAndFeel() {
    try {
      if (config.useJavaLooks) {
        String lookAndFeel = UIManager.getCrossPlatformLookAndFeelClassName();
        UIManager.setLookAndFeel(lookAndFeel);
      } else {
        String lookAndFeel = UIManager.getSystemLookAndFeelClassName();
        UIManager.setLookAndFeel(lookAndFeel);
      }
    } catch (IllegalAccessException e) {
      e.printStackTrace();
    } catch (ClassNotFoundException e) {
      e.printStackTrace();
    } catch (InstantiationException e) {
      e.printStackTrace();
    } catch (UnsupportedLookAndFeelException e) {
      e.printStackTrace();
    }
  }

  public static void setUIFont(javax.swing.plaf.FontUIResource f) {
    java.util.Enumeration keys = UIManager.getDefaults().keys();
    while (keys.hasMoreElements()) {
      Object key = keys.nextElement();
      Object value = UIManager.get(key);
      if (value instanceof javax.swing.plaf.FontUIResource) UIManager.put(key, f);
    }
  }

  public static void initializeAfterVersionCheck(boolean isEngineGame, Leelaz engine) {
    engine.canRestoreDymPda = true;
    if (engineManager.isEngineGame()) {
      if (engineManager.engineList.get(EngineManager.engineGameInfo.firstEngineIndex).isKataGoPda
          || engineManager.engineList.get(EngineManager.engineGameInfo.secondEngineIndex)
              .isKataGoPda) LizzieFrame.menu.showPda(true);
      else LizzieFrame.menu.showPda(false);
    } else {
      LizzieFrame.sendAiTime(false, engine, false);
      LizzieFrame.menu.showPda(Lizzie.leelaz.isKataGoPda);
    }
    if (engine != leelaz) return;

    if (!isEngineGame && !frame.isPlayingAgainstLeelaz) {
      if (Lizzie.config.notStartPondering) {
        leelaz.notPondering();
        leelaz.setResponseUpToDate();
        Lizzie.config.notStartPondering = false;
      } else {
        leelaz.ponder();
        leelaz.setResponseUpToDate();
      }
    }
    SwingUtilities.invokeLater(
        new Thread() {
          public void run() {
            LizzieFrame.menu.updateMenuStatusForEngine();
            if (!Lizzie.frame.syncBoard) Lizzie.frame.reSetLoc();
            LizzieFrame.toolbar.reSetButtonLocation();
            if (!isEngineGame) {
              if (Lizzie.frame.resetMovelistFrameandAnalysisFrame()) frame.setVisible(true);
            }
          }
        });
  }

  public static void shutdown() {
    //    if (config.config.getJSONObject("ui").getBoolean("confirm-exit")) {
    //      int ret =
    //          JOptionPane.showConfirmDialog(
    //              Lizzie.frame,
    //              resourceBundle.getString("Lizzie.askOnExit1"),
    //              resourceBundle.getString("Lizzie.askOnExit2"),
    //              JOptionPane.OK_CANCEL_OPTION);
    //      if (ret == JOptionPane.OK_OPTION) {
    //        frame.saveFile(false);
    //      }
    //    }
    if (config.autoSaveOnExit) frame.saveAutoGame(1);
    if (Lizzie.config.uiConfig.optBoolean("autoload-last", false)) {
      Lizzie.config.uiConfig.put("last-engine", EngineManager.currentEngineNo);
    }
    try {
      config.persist();
      config.save();
    } catch (Exception e) {
      Utils.showMsg(resourceBundle.getString("Lizzie.save.error") + e.getLocalizedMessage());
      e.printStackTrace();
      config.deletePersist(false);
    }
    try {
      if (engineManager != null) engineManager.forceKillAllEngines();
    } catch (Exception e) {
      e.printStackTrace();
    }
    if (frame.readBoard != null)
      try {
        frame.readBoard.shutdown();
      } catch (Exception e) {
        e.printStackTrace();
      }
    try {
      frame.shutdownClockHelper();
    } catch (Exception e) {
      e.printStackTrace();
    }
    Lizzie.frame.destroyEstimateEngine();
    Lizzie.frame.destroyAnalysisEngine();
    System.exit(0);
  }

  public static void resetAllHints() {
    config.allowCloseCommentControlHint = true;
    config.showReplaceFileHint = true;
    config.firstLoadKataGo = true;
    config.uiConfig.put("first-load-katago", config.firstLoadKataGo);
    config.uiConfig.put("show-replace-file-hint", config.showReplaceFileHint);
    config.uiConfig.put("allow-close-comment-control-hint", config.allowCloseCommentControlHint);
  }
}
