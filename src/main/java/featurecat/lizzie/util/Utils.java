package featurecat.lizzie.util;

import static java.lang.Math.round;

import featurecat.lizzie.Lizzie;
import featurecat.lizzie.gui.EngineData;
import featurecat.lizzie.gui.Message;
import featurecat.lizzie.gui.RemoteEngineData;
import featurecat.lizzie.rules.BoardHistoryNode;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.StringTokenizer;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.SourceDataLine;
import javax.swing.Box;
import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.TableModel;
import org.json.JSONArray;
import org.json.JSONObject;

public class Utils {

  public static String aesKey = "iyekeeaysueeaesk";
  public static String iv = "s6st73f41adc4c5d";
  public static String aesKey2 = "iyekeeay2ueeaesk";
  public static String iv2 = "s6st73f49adc4c5d";
  public static String pwd = java.io.File.separator;
  private static int msemaphoretryroom = 1;
  private static boolean alertedNoByoyomiSoundFile = false;

  public static void addFiller(JComponent component, int width, int height) {
    Dimension FILLER_DIMENSION = new Dimension(width, height);
    Box.Filler filler = new Box.Filler(FILLER_DIMENSION, FILLER_DIMENSION, FILLER_DIMENSION);
    filler.setAlignmentX(Component.LEFT_ALIGNMENT);
    component.add(filler);
  }

  public static String doDecrypt(String str) {
    String de_aes = Base64AesDecipher.decryptAES(str, aesKey, iv);
    String de_base64 = Base64AesDecipher.DecipherBase64(de_aes);
    return de_base64;
  }

  public static String doEncrypt(String str) {
    String en_base64 = Base64AesEncrypt.encryptBASE64(str);
    String en_aes = Base64AesEncrypt.encryptAES(en_base64, aesKey, iv);
    return en_aes;
  }

  public static String doDecrypt2(String str) {
    String de_aes = Base64AesDecipher.decryptAES(str, aesKey2, iv2);
    String de_base64 = Base64AesDecipher.DecipherBase64(de_aes);
    return de_base64;
  }

  public static String doEncrypt2(String str) {
    String en_base64 = Base64AesEncrypt.encryptBASE64(str);
    String en_aes = Base64AesEncrypt.encryptAES(en_base64, aesKey2, iv2);
    return en_aes;
  }

  public static boolean isWindows() {
    String osName = System.getProperty("os.name", "generic").toLowerCase(Locale.ENGLISH);
    return osName != null && !osName.contains("darwin") && osName.contains("win");
  }

  private static enum ParamState {
    NORMAL,
    QUOTE,
    DOUBLE_QUOTE
  }

  public static void exportTable(JTable table, String file) throws IOException {
    TableModel model = table.getModel();
    BufferedWriter bWriter =
        new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "x-UTF-16LE-BOM"));
    for (int i = 0; i < model.getColumnCount(); i++) {
      bWriter.write(model.getColumnName(i));
      bWriter.write("\t");
    }
    bWriter.newLine();
    for (int i = 0; i < model.getRowCount(); i++) {
      for (int j = 0; j < model.getColumnCount(); j++) {
        bWriter.write(model.getValueAt(i, j).toString());
        bWriter.write("\t");
      }
      bWriter.newLine();
    }
    bWriter.close();
    System.out.println("write out to: " + file);
  }

  public static int zoomIn(int pos) {
    if (Lizzie.config.isScaled) return (int) Math.round(pos / Lizzie.javaScaleFactor);
    else return pos;
  }

  public static int zoomOut(int pos) {
    if (Lizzie.config.isScaled) return (int) Math.round(pos * Lizzie.javaScaleFactor);
    else return pos;
  }

  public static List<String> splitCommand(String commandLine) {
    if (commandLine == null || commandLine.length() == 0) {
      return new ArrayList<String>();
    }

    final ArrayList<String> commandList = new ArrayList<String>();
    final StringBuilder param = new StringBuilder();
    final StringTokenizer tokens = new StringTokenizer(commandLine, " '\"", true);
    boolean lastTokenQuoted = false;
    ParamState state = ParamState.NORMAL;
    while (tokens.hasMoreTokens()) {
      String nextToken = tokens.nextToken();
      switch (state) {
        case QUOTE:
          if ("'".equals(nextToken)) {
            state = ParamState.NORMAL;
            lastTokenQuoted = true;
          } else {
            param.append(nextToken);
          }
          break;
        case DOUBLE_QUOTE:
          if ("\"".equals(nextToken)) {
            state = ParamState.NORMAL;
            lastTokenQuoted = true;
          } else {
            param.append(nextToken);
          }
          break;
        default:
          if ("'".equals(nextToken)) {
            state = ParamState.QUOTE;
          } else if ("\"".equals(nextToken)) {
            state = ParamState.DOUBLE_QUOTE;
          } else if (" ".equals(nextToken)) {
            if (lastTokenQuoted || param.length() != 0) {
              if (commandList.isEmpty()) commandList.add(param.toString().trim());
              else commandList.add(param.toString());
              param.delete(0, param.length());
            }
          } else {
            param.append(nextToken);
          }
          lastTokenQuoted = false;
          break;
      }
    }
    if (lastTokenQuoted || param.length() != 0) {
      commandList.add(param.toString());
    }
    return commandList;
  }

  public static RemoteEngineData getEstimateEngineRemoteEngineData() {
    RemoteEngineData remoteData = new RemoteEngineData();
    Optional<JSONObject> remoteEngineInfoOpt =
        Optional.ofNullable(Lizzie.config.leelazConfig.optJSONObject("estimate-engine-ssh-info"));
    if (remoteEngineInfoOpt.isPresent()) {
      JSONObject remoteEngineInfo = remoteEngineInfoOpt.get();
      remoteData.useJavaSSH = remoteEngineInfo.optBoolean("useJavaSSH", false);
      remoteData.ip = remoteEngineInfo.optString("ip", "");
      remoteData.port = remoteEngineInfo.optString("port", "");
      remoteData.userName = remoteEngineInfo.optString("userName", "");
      remoteData.useKeyGen = remoteEngineInfo.optBoolean("useKeyGen", false);
      remoteData.password = remoteEngineInfo.optString("password", "");
      remoteData.keyGenPath = remoteEngineInfo.optString("keyGenPath", "");
    }
    return remoteData;
  }

  public static void saveEstimateEngineRemoteEngineData(RemoteEngineData remoteEngineData) {
    JSONObject remoteEngineInfo = new JSONObject();
    remoteEngineInfo.put("useJavaSSH", remoteEngineData.useJavaSSH);
    remoteEngineInfo.put("ip", remoteEngineData.ip);
    remoteEngineInfo.put("port", remoteEngineData.port);
    remoteEngineInfo.put("userName", remoteEngineData.userName);
    remoteEngineInfo.put("password", remoteEngineData.password);
    remoteEngineInfo.put("useKeyGen", remoteEngineData.useKeyGen);
    remoteEngineInfo.put("keyGenPath", remoteEngineData.keyGenPath);
    Lizzie.config.leelazConfig.put("estimate-engine-ssh-info", remoteEngineInfo);
    try {
      Lizzie.config.save();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public static RemoteEngineData getAnalysisEngineRemoteEngineData() {
    RemoteEngineData remoteData = new RemoteEngineData();
    Optional<JSONObject> remoteEngineInfoOpt =
        Optional.ofNullable(Lizzie.config.leelazConfig.optJSONObject("analysis-engine-ssh-info"));
    if (remoteEngineInfoOpt.isPresent()) {
      JSONObject remoteEngineInfo = remoteEngineInfoOpt.get();
      remoteData.useJavaSSH = remoteEngineInfo.optBoolean("useJavaSSH", false);
      remoteData.ip = remoteEngineInfo.optString("ip", "");
      remoteData.port = remoteEngineInfo.optString("port", "");
      remoteData.userName = remoteEngineInfo.optString("userName", "");
      remoteData.useKeyGen = remoteEngineInfo.optBoolean("useKeyGen", false);
      remoteData.password = remoteEngineInfo.optString("password", "");
      remoteData.keyGenPath = remoteEngineInfo.optString("keyGenPath", "");
    }
    return remoteData;
  }

  public static void saveAnalysisEngineRemoteEngineData(RemoteEngineData remoteEngineData) {
    JSONObject remoteEngineInfo = new JSONObject();
    remoteEngineInfo.put("useJavaSSH", remoteEngineData.useJavaSSH);
    remoteEngineInfo.put("ip", remoteEngineData.ip);
    remoteEngineInfo.put("port", remoteEngineData.port);
    remoteEngineInfo.put("userName", remoteEngineData.userName);
    remoteEngineInfo.put("password", remoteEngineData.password);
    remoteEngineInfo.put("useKeyGen", remoteEngineData.useKeyGen);
    remoteEngineInfo.put("keyGenPath", remoteEngineData.keyGenPath);
    Lizzie.config.leelazConfig.put("analysis-engine-ssh-info", remoteEngineInfo);
    try {
      Lizzie.config.save();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public static ArrayList<EngineData> getEngineData() {
    ArrayList<EngineData> engineData = new ArrayList<>();
    Optional<JSONArray> engineOpt =
        Optional.ofNullable(Lizzie.config.leelazConfig.optJSONArray("engine-settings-list"));
    if (engineOpt.isPresent()) {
      JSONArray engineJSArray = engineOpt.get();
      for (int i = 0; i < engineJSArray.length(); i++) {
        EngineData engineDt = new EngineData();
        JSONObject engineInfo = engineJSArray.getJSONObject(i);
        engineDt.index = i;
        engineDt.commands = engineInfo.optString("command", "");
        engineDt.name = engineInfo.optString("name", "");
        engineDt.preload = engineInfo.optBoolean("preload", false);
        engineDt.komi = engineInfo.optFloat("komi", 7.5F);
        engineDt.width = engineInfo.optInt("width", 19);
        engineDt.height = engineInfo.optInt("height", 19);
        engineDt.isDefault = engineInfo.optBoolean("isDefault", false);
        engineDt.useJavaSSH = engineInfo.optBoolean("useJavaSSH", false);
        engineDt.useKeyGen = engineInfo.optBoolean("useKeyGen", false);
        engineDt.keyGenPath = engineInfo.optString("keyGenPath", "");
        engineDt.ip = engineInfo.optString("ip", "");
        engineDt.port = engineInfo.optString("port", "");
        engineDt.userName = engineInfo.optString("userName", "");
        engineDt.password = engineInfo.optString("password", "");
        engineDt.initialCommand = engineInfo.optString("initialCommand", "");
        engineData.add(engineDt);
      }
    } else {
      engineData = getEngineDataOld();
      Lizzie.config.leelazConfig.remove("engine-command");
      Lizzie.config.leelazConfig.remove("engine-command-list");
      Lizzie.config.leelazConfig.remove("engine-name-list");
      Lizzie.config.leelazConfig.remove("engine-preload-list");
      Lizzie.config.leelazConfig.remove("engine-width-list");
      Lizzie.config.leelazConfig.remove("engine-height-list");
      Lizzie.config.leelazConfig.remove("engine-komi-list");
      saveEngineSettings(engineData);
    }
    return engineData;
  }

  public static void saveEngineSettings(ArrayList<EngineData> engineData) {
    JSONArray engineDate = new JSONArray();
    for (int i = 0; i < engineData.size(); i++) {
      JSONObject engineInfo = new JSONObject();
      EngineData engineDt = engineData.get(i);
      engineInfo.put("command", engineDt.commands);
      engineInfo.put("name", engineDt.name);
      engineInfo.put("preload", engineDt.preload);
      engineInfo.put("komi", engineDt.komi);
      engineInfo.put("width", engineDt.width);
      engineInfo.put("height", engineDt.height);
      engineInfo.put("isDefault", engineDt.isDefault);
      engineInfo.put("useJavaSSH", engineDt.useJavaSSH);
      engineInfo.put("ip", engineDt.ip);
      engineInfo.put("port", engineDt.port);
      engineInfo.put("userName", engineDt.userName);
      engineInfo.put("password", engineDt.password);
      engineInfo.put("useKeyGen", engineDt.useKeyGen);
      engineInfo.put("keyGenPath", engineDt.keyGenPath);
      engineInfo.put("initialCommand", engineDt.initialCommand);
      engineDate.put(engineInfo);
    }
    Lizzie.config.leelazConfig.put("engine-settings-list", engineDate);
    try {
      Lizzie.config.save();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public static ArrayList<EngineData> getEngineDataOld() {
    ArrayList<EngineData> engineData = new ArrayList<EngineData>();
    Optional<JSONArray> enginesCommandOpt =
        Optional.ofNullable(Lizzie.config.leelazConfig.optJSONArray("engine-command-list"));
    Optional<JSONArray> enginesNameOpt =
        Optional.ofNullable(Lizzie.config.leelazConfig.optJSONArray("engine-name-list"));
    Optional<JSONArray> enginesPreloadOpt =
        Optional.ofNullable(Lizzie.config.leelazConfig.optJSONArray("engine-preload-list"));

    Optional<JSONArray> enginesWidthOpt =
        Optional.ofNullable(Lizzie.config.leelazConfig.optJSONArray("engine-width-list"));

    Optional<JSONArray> enginesHeightOpt =
        Optional.ofNullable(Lizzie.config.leelazConfig.optJSONArray("engine-height-list"));
    Optional<JSONArray> enginesKomiOpt =
        Optional.ofNullable(Lizzie.config.leelazConfig.optJSONArray("engine-komi-list"));

    int defaultEngine = Lizzie.config.uiConfig.optInt("default-engine", -1);

    for (int i = 0;
        i < (enginesCommandOpt.isPresent() ? enginesCommandOpt.get().length() + 1 : 0);
        i++) {
      if (i == 0) {
        String engineCommand = Lizzie.config.leelazConfig.getString("engine-command");
        int width = enginesWidthOpt.isPresent() ? enginesWidthOpt.get().optInt(i, 19) : 19;
        int height = enginesHeightOpt.isPresent() ? enginesHeightOpt.get().optInt(i, 19) : 19;
        String name = enginesNameOpt.isPresent() ? enginesNameOpt.get().optString(i, "") : "";
        float komi =
            enginesKomiOpt.isPresent()
                ? enginesKomiOpt.get().optFloat(i, (float) 7.5)
                : (float) 7.5;
        boolean preload =
            enginesPreloadOpt.isPresent() ? enginesPreloadOpt.get().optBoolean(i, false) : false;
        EngineData enginedt = new EngineData();
        enginedt.commands = engineCommand;
        enginedt.name = name;
        enginedt.preload = preload;
        enginedt.index = i;
        enginedt.width = width;
        enginedt.height = height;
        enginedt.komi = komi;
        if (defaultEngine == i) enginedt.isDefault = true;
        else enginedt.isDefault = false;
        engineData.add(enginedt);
      } else {
        String commands =
            enginesCommandOpt.isPresent() ? enginesCommandOpt.get().optString(i - 1, "") : "";
        if (!commands.equals("")) {
          int width = enginesWidthOpt.isPresent() ? enginesWidthOpt.get().optInt(i, 19) : 19;
          int height = enginesHeightOpt.isPresent() ? enginesHeightOpt.get().optInt(i, 19) : 19;
          String name = enginesNameOpt.isPresent() ? enginesNameOpt.get().optString(i, "") : "";
          float komi =
              enginesKomiOpt.isPresent()
                  ? enginesKomiOpt.get().optFloat(i, (float) 7.5)
                  : (float) 7.5;
          boolean preload =
              enginesPreloadOpt.isPresent() ? enginesPreloadOpt.get().optBoolean(i, false) : false;
          EngineData enginedt = new EngineData();
          enginedt.commands = commands;
          enginedt.name = name;
          enginedt.preload = preload;
          enginedt.index = i;
          enginedt.width = width;
          enginedt.height = height;
          enginedt.komi = komi;
          if (defaultEngine == i) enginedt.isDefault = true;
          else enginedt.isDefault = false;
          engineData.add(enginedt);
        }
      }
    }
    return engineData;
  }

  public static boolean isBlank(String str) {
    return str == null || str.trim().isEmpty();
  }

  public static Float parseTextToFloat(JTextField text, Float defaultValue) {
    try {
      return Float.valueOf(Float.parseFloat(text.getText().trim()));
    } catch (NumberFormatException ex) {
      return defaultValue;
    }
  }

  public static int parseTextToInt(JTextField text, int defaultValue) {
    try {
      return Integer.parseInt(text.getText().trim());
    } catch (NumberFormatException ex) {
      return defaultValue;
    }
  }

  public static Long parseTextToLong(JTextField text, Long defaultValue) {
    try {
      return Long.parseLong(text.getText().trim());
    } catch (NumberFormatException ex) {
      return defaultValue;
    }
  }

  public static Double parseTextToDouble(JTextField text, Double defaultValue) {
    try {
      return Double.parseDouble(text.getText().trim());
    } catch (NumberFormatException ex) {
      return defaultValue;
    }
  }

  public static void showMsg(String message) {
    Message msg = new Message();
    msg.setMessage(message);
    //  msg.setVisible(true);
  }

  public static void showMsgNoModal(String message) {
    Message msg = new Message();
    msg.setMessageNoModal(message);
    //  msg.setVisible(true);
  }

  public static void showMsgNoModalForTime(String message, int seconds) {
    Message msg = new Message();
    msg.setMessageNoModal(message, seconds);
    //  msg.setVisible(true);
  }

  /**
   * @return a shorter, rounded string version of playouts. e.g. 345 -> 345, 1265 -> 1.3k, 44556 ->
   *     45k, 133523 -> 134k, 1234567 -> 1.2m
   */
  public static String getPlayoutsString(int playouts) {
    if (playouts >= 1_000_000) {
      double playoutsDouble = (double) playouts / 100_000; // 1234567 -> 12.34567
      return round(playoutsDouble) / 10.0 + "m";
    } else if (playouts >= 10_000) {
      double playoutsDouble = (double) playouts / 1_000; // 13265 -> 13.265
      return round(playoutsDouble) + "k";
    } else if (playouts >= 1_000) {
      double playoutsDouble = (double) playouts / 100; // 1265 -> 12.65
      return round(playoutsDouble) / 10.0 + "k";
    } else {
      return String.valueOf(playouts);
    }
  }

  public static Double txtFieldDoubleValue(JTextField txt) {
    if (txt.getText().trim().isEmpty()) {
      return 0.0;
    } else {
      return new Double(txt.getText().trim());
    }
  }

  /**
   * Truncate text that is too long for the given width
   *
   * @param line
   * @param fm
   * @param fitWidth
   * @return fitted
   */
  public static String truncateStringByWidth(String line, FontMetrics fm, int fitWidth) {
    if (line.isEmpty()) {
      return "";
    }
    int width = fm.stringWidth(line);
    if (width > fitWidth) {
      int guess = line.length() * fitWidth / width;
      String before = line.substring(0, guess).trim();
      width = fm.stringWidth(before);
      if (width > fitWidth) {
        int diff = width - fitWidth;
        int i = 0;
        for (; (diff > 0 && i < 5); i++) {
          diff = diff - fm.stringWidth(line.substring(guess - i - 1, guess - i));
        }
        return line.substring(0, guess - i).trim();
      } else {
        return before;
      }
    } else {
      return line;
    }
  }

  //  public static double lastWinrateDiff(BoardHistoryNode node) {
  //
  //    // Last winrate
  //    Optional<BoardData> lastNode = node.previous().flatMap(n -> Optional.of(n.getData()));
  //    boolean validLastWinrate = lastNode.map(d -> d.getPlayouts() > 0).orElse(false);
  //    double lastWR = validLastWinrate ? lastNode.get().winrate : 50;
  //
  //    // Current winrate
  //    BoardData data = node.getData();
  //    boolean validWinrate = false;
  //    double curWR = 50;
  //    if (data == Lizzie.board.getHistory().getData()) {
  //      Leelaz.WinrateStats stats = Lizzie.leelaz.getWinrateStats();
  //      curWR = stats.maxWinrate;
  //      validWinrate = (stats.totalPlayouts > 0);
  //      if (Lizzie.frame.isPlayingAgainstLeelaz
  //          && Lizzie.frame.playerIsBlack == !Lizzie.board.getHistory().getData().blackToPlay) {
  //        validWinrate = false;
  //      }
  //    } else {
  //      validWinrate = (data.getPlayouts() > 0);
  //      curWR = validWinrate ? data.winrate : 100 - lastWR;
  //    }
  //
  //    // Last move difference winrate
  //    if (validLastWinrate && validWinrate) {
  //      return 100 - lastWR - curWR;
  //    } else {
  //      return 0;
  //    }
  //  }

  //  public static Color getBlunderNodeColor(BoardHistoryNode node) {
  //    if (Lizzie.config.nodeColorMode == 1 && node.getData().blackToPlay
  //        || Lizzie.config.nodeColorMode == 2 && !node.getData().blackToPlay) {
  //      return Color.WHITE;
  //    }
  //    double diffWinrate = lastWinrateDiff(node);
  //    Optional<Double> st =
  //        diffWinrate >= 0
  //            ? Lizzie.config.blunderWinrateThresholds.flatMap(
  //                l -> l.stream().filter(t -> (t > 0 && t <= diffWinrate)).reduce((f, s) -> s))
  //            : Lizzie.config.blunderWinrateThresholds.flatMap(
  //                l -> l.stream().filter(t -> (t < 0 && t >= diffWinrate)).reduce((f, s) -> f));
  //    if (st.isPresent()) {
  //      return Lizzie.config.blunderNodeColors.map(m -> m.get(st.get())).get();
  //    } else {
  //      return Color.WHITE;
  //    }
  //  }

  public static Integer txtFieldValue(JTextField txt) {
    if (txt.getText().trim().isEmpty()
        || txt.getText().trim().length() >= String.valueOf(Integer.MAX_VALUE).length()) {
      return 0;
    } else {
      return Integer.parseInt(txt.getText().trim());
    }
  }

  public static int intOfMap(Map map, String key) {
    if (map == null) {
      return 0;
    }
    List s = (List<String>) map.get(key);
    if (s == null || s.size() <= 0) {
      return 0;
    }
    try {
      return Integer.parseInt((String) s.get(0));
    } catch (NumberFormatException e) {
      return 0;
    }
  }

  public static String stringOfMap(Map map, String key) {
    if (map == null) {
      return "";
    }
    List s = (List<String>) map.get(key);
    if (s == null || s.size() <= 0) {
      return "";
    }
    try {
      return (String) s.get(0);
    } catch (NumberFormatException e) {
      return "";
    }
  }

  public static void playVoiceFile() {
    if (Lizzie.config.notPlaySoundInSync && (Lizzie.frame.urlSgf || Lizzie.frame.syncBoard)) return;
    Runnable runnable =
        new Runnable() {
          public void run() {
            if (msemaphoretryroom < 0) {
              return;
            }
            msemaphoretryroom--;
            try {
              BoardHistoryNode node = Lizzie.board.getHistory().getCurrentHistoryNode();
              if (node.previous().isPresent()) {
                if (node.getData().blackCaptures > node.previous().get().getData().blackCaptures) {
                  if (node.getData().blackCaptures - node.previous().get().getData().blackCaptures
                      >= 3) playVoice(pwd + "sound" + pwd + "deadStoneMore.wav", false);
                  else playVoice(pwd + "sound" + pwd + "deadStone.wav", false);
                } else {
                  if (node.getData().whiteCaptures
                      > node.previous().get().getData().whiteCaptures) {
                    if (node.getData().whiteCaptures - node.previous().get().getData().whiteCaptures
                        >= 3) playVoice(pwd + "sound" + pwd + "deadStoneMore.wav", false);
                    else playVoice(pwd + "sound" + pwd + "deadStone.wav", false);
                  } else playVoice(pwd + "sound" + pwd + "Stone.wav", false);
                }
              } else {
                playVoice("\\sound\\Stone.wav", false);
              }
            } catch (Exception e) {
              // TODO Auto-generated catch block
              e.printStackTrace();
            }
            msemaphoretryroom++;
          }
        };
    Thread thread = new Thread(runnable);
    thread.start();
  }

  public static void playByoyomi(int seconds) {
    try {
      playVoice(pwd + "sound" + pwd + seconds + ".wav", true);
    } catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  private static void playVoice(String wav, boolean isByoyomi) throws Exception {
    File file = new File("");
    String courseFile = "";
    try {
      courseFile = file.getCanonicalPath();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    String filePath = courseFile + wav;
    if (!filePath.equals("")) {
      // Get audio input stream
      AudioInputStream audioInputStream = null;
      try {
        audioInputStream = AudioSystem.getAudioInputStream(new File(filePath));
      } catch (Exception e) {
        if (isByoyomi) {
          if (!alertedNoByoyomiSoundFile) {
            alertedNoByoyomiSoundFile = true;
            showMsg(Lizzie.resourceBundle.getString("Utils.noSoundFile") + wav + "\"");
          }
        } else {
          showMsg(Lizzie.resourceBundle.getString("Utils.noSoundFile") + wav + "\"");
          Lizzie.config.playSound = false;
          Lizzie.config.uiConfig.put("play-sound", Lizzie.config.playSound);
        }
        return;
      }
      //      Clip clip = AudioSystem.getClip();
      //      clip.open(audioInputStream);
      //      FloatControl gainControl = (FloatControl)
      // clip.getControl(FloatControl.Type.MASTER_GAIN);
      //      gainControl.setValue(-15.0f); // Reduce volume by 20 decibels.
      //      clip.start();
      // Get audio coding object
      AudioFormat audioFormat = audioInputStream.getFormat();
      // Set data entry
      DataLine.Info dataLineInfo =
          new DataLine.Info(SourceDataLine.class, audioFormat, AudioSystem.NOT_SPECIFIED);
      SourceDataLine sourceDataLine = (SourceDataLine) AudioSystem.getLine(dataLineInfo);
      sourceDataLine.open(audioFormat);
      sourceDataLine.start();
      // Read from the data sent to the mixer input stream
      int count;
      byte tempBuffer[] = new byte[1024];
      while ((count = audioInputStream.read(tempBuffer, 0, tempBuffer.length)) != -1) {
        if (count > 0) {
          sourceDataLine.write(tempBuffer, 0, count);
        }
      }
      // Empty the data buffer, and close the input
      sourceDataLine.drain();
      sourceDataLine.close();
    }
  }

  private static String getCurrentDirPath() {
    URL url = Utils.class.getProtectionDomain().getCodeSource().getLocation();
    String path = url.getPath();
    if (path.startsWith("file:")) {
      path = path.replace("file:", "");
    }
    if (path.contains(".jar!/")) {
      path = path.substring(0, path.indexOf(".jar!/") + 4);
    }

    File file = new File(path);
    path = file.getParentFile().getAbsolutePath();
    return path;
  }

  private static Path getDistFile(String path, String newFolderName) throws IOException {
    String currentRealPath = "";
    File file = new File("");
    currentRealPath = file.getCanonicalPath();
    Path dist =
        Paths.get(
            currentRealPath
                + File.separator
                + newFolderName
                + File.separator
                + path.substring(path.lastIndexOf("/") + 1));
    Path parent = dist.getParent();
    if (parent != null) {
      Files.createDirectories(parent);
    }
    Files.deleteIfExists(dist);
    return dist;
  }

  public static void copy(String resource, String newFolderName) throws IOException {
    InputStream in = Utils.class.getResourceAsStream(resource);
    Path dist = getDistFile(resource, newFolderName);
    Files.copy(in, dist);
    in.close();
  }

  public static void addNewThemeAs(String themeName) {
    // TODO Auto-generated method stub
    try {
      copy("/assets/newtheme/black.png", "theme" + File.separator + themeName);
      copy("/assets/newtheme/white.png", "theme" + File.separator + themeName);
      copy("/assets/newtheme/board.png", "theme" + File.separator + themeName);
      copy("/assets/newtheme/background.jpg", "theme" + File.separator + themeName);
      copy("/assets/newtheme/theme.txt", "theme" + File.separator + themeName);
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  public static boolean deleteDir(File dir) {
    if (dir.isDirectory()) {
      String[] children = dir.list();
      for (int i = 0; i < children.length; i++) {
        boolean success = deleteDir(new File(dir, children[i]));
        if (!success) {
          return false;
        }
      }
    }
    if (dir.delete()) {
      return true;
    } else {
      return false;
    }
  }
}
