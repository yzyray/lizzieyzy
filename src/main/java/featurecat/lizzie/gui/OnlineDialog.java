package featurecat.lizzie.gui;

import featurecat.lizzie.Lizzie;
import featurecat.lizzie.rules.Board;
import featurecat.lizzie.rules.BoardData;
import featurecat.lizzie.rules.BoardHistoryList;
import featurecat.lizzie.rules.BoardHistoryNode;
import featurecat.lizzie.rules.SGFParser;
import featurecat.lizzie.rules.Stone;
import featurecat.lizzie.util.AjaxHttpRequest;
import featurecat.lizzie.util.Utils;
import io.socket.client.Ack;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.ByteBuffer;
import java.text.NumberFormat;
import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import javax.swing.text.InternationalFormatter;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class OnlineDialog extends JDialog {
  private final ResourceBundle resourceBundle = Lizzie.resourceBundle;
  //      Lizzie.config.useLanguage == 0
  //          ? ResourceBundle.getBundle("l10n.DisplayStrings")
  //          : (Lizzie.config.useLanguage == 1
  //              ? ResourceBundle.getBundle("l10n.DisplayStrings", new Locale("zh", "CN"))
  //              : ResourceBundle.getBundle("l10n.DisplayStrings", new Locale("en", "US")));
  private ScheduledExecutorService online = Executors.newScheduledThreadPool(1);
  private ScheduledFuture<?> schedule = null;
  private static WebSocketClient client;
  private Socket sio;
  private int type = 0;
  private JFormattedTextField txtRefreshTime;
  private JLabel lblError;
  private int refreshTime;
  private JTextField txtUrl;
  private String ajaxUrl = "";
  private Map queryMap = null;
  private String query = "";
  private String whitePlayer = "";
  private String blackPlayer = "";
  private long wuid = 0;
  private long buid = 0;
  private String wTime = "";
  private String bTime = "";
  private int seqs = 0;
  private boolean done = false;
  private BoardHistoryList history = null;
  private int boardSize = 19;
  private long userId = -1000000;
  private long roomId = 0;
  // static AjaxHttpRequest ajax;
  private boolean firstTime = true;
  static boolean isStoped = false;
  static boolean fromBrowser = false;
  static Timer timer;
  private boolean chineseFlag = false;
  private int chineseRule = 1;
  private boolean shouldMoveForward = false;
  private Map<Integer, Map<Integer, JSONObject>> branchs =
      new HashMap<Integer, Map<Integer, JSONObject>>();
  private Map<Integer, Map<Integer, JSONObject>> comments =
      new HashMap<Integer, Map<Integer, JSONObject>>();
  private byte[] b = {
    119, 115, 115, 58, 47, 47, 104, 108, 119, 115, 46, 104, 117, 97, 110, 108, 101, 46, 113, 113,
    46, 99, 111, 109, 47, 119, 113, 98, 114, 111, 97, 100, 99, 97, 115, 116, 108, 111, 116, 117, 115
  };
  private byte[] b2 = {
    119, 115, 58, 47, 47, 119, 115, 104, 97, 108, 108, 46, 104, 117, 97, 110, 108, 101, 46, 113,
    113, 46, 99, 111, 109, 47, 78, 101, 119, 69, 97, 103, 108, 101, 69, 121, 101, 76, 111, 116, 117,
    115
  };
  private byte[] b3 = {
    104, 116, 116, 112, 115, 58, 47, 47, 119, 101, 105, 113, 105, 46, 113, 113, 46, 99, 111, 109,
    47, 111, 112, 101, 110, 113, 105, 112, 117, 47, 103, 101, 116, 113, 105, 112, 117, 63, 99, 97,
    108, 108, 98, 97, 99, 107, 61, 106, 81, 117, 101, 114, 121, 49, 38, 103, 97, 109, 101, 99, 111,
    100, 101, 61
  };
  private byte[] b4 = {104, 117, 97, 110, 108, 101, 46, 113, 113, 46, 99, 111, 109};

  private byte[] c1 = {
    104, 116, 116, 112, 115, 58, 47, 47, 114, 116, 103, 97, 109, 101, 46, 121, 105, 107, 101, 119,
    101, 105, 113, 105, 46, 99, 111, 109
  };

  public OnlineDialog() {
    setTitle(resourceBundle.getString("OnlineDialog.title.config"));
    setModalityType(ModalityType.APPLICATION_MODAL);
    setAlwaysOnTop(Lizzie.frame.isAlwaysOnTop());
    // setType(Type.POPUP);
    // setBounds(100, 100, 790, 207);
    Lizzie.setFrameSize(this, 730, 172);
    this.setResizable(false);
    try {
      this.setIconImage(ImageIO.read(MoreEngines.class.getResourceAsStream("/assets/logo.png")));
    } catch (IOException e) {
      e.printStackTrace();
    }
    getContentPane().setLayout(new BorderLayout());
    JPanel buttonPane = new JPanel();
    getContentPane().add(buttonPane, BorderLayout.CENTER);
    JButton okButton = new JButton(resourceBundle.getString("OnlineDialog.button.ok"));
    okButton.setBounds(129, 105, 74, 29);
    okButton.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            applyChange();
          }
        });
    buttonPane.setLayout(null);
    okButton.setActionCommand("OK");
    buttonPane.add(okButton);
    getRootPane().setDefaultButton(okButton);

    JButton interruptButton =
        new JButton(resourceBundle.getString("OnlineDialog.button.interrupt"));
    interruptButton.setBounds(218, 105, 74, 29);
    interruptButton.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            stopSync();
          }
        });
    buttonPane.add(interruptButton);
    interruptButton.setMargin(new Insets(0, 0, 0, 0));

    JButton cancelButton = new JButton(resourceBundle.getString("OnlineDialog.button.cancel"));
    cancelButton.setBounds(307, 105, 74, 29);
    cancelButton.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            setVisible(false);
          }
        });
    cancelButton.setActionCommand("Cancel");
    buttonPane.add(cancelButton);

    JLabel lblUrl = new JLabel(resourceBundle.getString("OnlineDialog.title.url"));
    lblUrl.setBounds(10, 58, 56, 14);
    buttonPane.add(lblUrl);
    lblUrl.setHorizontalAlignment(SwingConstants.LEFT);

    NumberFormat nf = NumberFormat.getIntegerInstance();
    nf.setGroupingUsed(false);

    txtUrl = new JTextField();
    txtUrl.addFocusListener(
        new FocusAdapter() {
          @Override
          public void focusGained(FocusEvent e) {
            txtUrl.selectAll();
          }
        });
    txtUrl.setBounds(69, 55, 639, 20);
    buttonPane.add(txtUrl);
    txtUrl.setColumns(10);

    JLabel lblRefresh = new JLabel(resourceBundle.getString("OnlineDialog.title.refresh"));
    lblRefresh.setBounds(10, 80, 56, 14);
    buttonPane.add(lblRefresh);

    JLabel lblRefreshTime = new JLabel(resourceBundle.getString("OnlineDialog.title.refreshTime"));
    lblRefreshTime.setBounds(119, 80, 81, 14);
    buttonPane.add(lblRefreshTime);

    txtRefreshTime =
        new JFormattedTextField(
            new InternationalFormatter(nf) {
              protected DocumentFilter getDocumentFilter() {
                return filter;
              }

              private DocumentFilter filter = new DigitOnlyFilter();
            });
    txtRefreshTime.setBounds(69, 79, 36, 20);
    txtRefreshTime.setText("1");
    buttonPane.add(txtRefreshTime);
    txtRefreshTime.setColumns(10);
    JLabel lblPrompt1 = new JLabel(resourceBundle.getString("OnlineDialog.lblPrompt1.text"));
    lblPrompt1.setBounds(10, 6, 398, 14);
    buttonPane.add(lblPrompt1);

    JLabel lblPrompt2 =
        new JLabel(
            resourceBundle.getString("OnlineDialog.lblPrompt2.text")
                + "https://home.yikeweiqi.com/#/live/room/18328/1/15630642"); // 支持弈客直播，例如
    lblPrompt2.setBounds(10, 30, 475, 14);
    buttonPane.add(lblPrompt2);
    //    JLabel lblPrompt3 =
    //        new JLabel(
    //
    // "支持野狐(腾讯围棋)分享链接，例如:http://share.foxwq.com/index.html?gameid=369&showtype=1&showid=83&chessid=383699091456898&status=0&createtime=1559816204&title=%E9%9F%A9%E5%9B%BD%E5%9B%B4%E6%A3%8BTV%E6%9D%AF32%E5%BC%BA%E6%88%98&chatid=880&support=1");
    //    lblPrompt3.setBounds(10, 50, 755, 14);
    //    buttonPane.add(lblPrompt3);
    //
    //    JLabel lblPrompt4 =
    //        new JLabel(
    //
    // "或:http://huanle.qq.com/act/a20170110wq/index-share.html?srctype=2&svrid=2404&roomid=20&tableid=6354&gametag=S2404R20T6354t5D06F6ECS82&title=%E8%85%BE%E8%AE%AF%E5%9B%B4%E6%A3%8B%E5%9C%A8%E7%BA%BF%E5%AF%B9%E5%BC%88&uin=643324524&support=0");
    //    lblPrompt4.setBounds(10, 65, 755, 14);
    //    buttonPane.add(lblPrompt4);

    lblError = new JLabel(resourceBundle.getString("OnlineDialog.lblError.text"));
    lblError.setForeground(Color.RED);
    lblError.setBounds(171, 80, 316, 16);
    lblError.setVisible(false);
    buttonPane.add(lblError);

    txtUrl.selectAll();

    setLocationRelativeTo(getOwner());
    paste();
  }

  public void paste() {
    String pastContent =
        Optional.ofNullable(Toolkit.getDefaultToolkit().getSystemClipboard().getContents(null))
            .filter(cc -> cc.isDataFlavorSupported(DataFlavor.stringFlavor))
            .flatMap(
                cc -> {
                  try {
                    return Optional.of((String) cc.getTransferData(DataFlavor.stringFlavor));
                  } catch (UnsupportedFlavorException e) {
                    e.printStackTrace();
                  } catch (IOException e) {
                    e.printStackTrace();
                  }
                  return Optional.empty();
                })
            .orElse("");
    txtUrl.setText(pastContent);
  }

  private void applyChange() {
    //
    if (LizzieFrame.urlSgf) {
      if (client != null && client.isOpen()) {
        client.close();
        client = null;
      }
    }
    type = checkUrl();
    isStoped = false;
    chineseRule = 1;
    chineseFlag = false;
    firstTime = true;
    LizzieFrame.urlSgf = true;
    Lizzie.frame.setCommentPaneOrArea(false);
    if (type > 0) {
      error(false);
      setVisible(false);
      try {
        Lizzie.frame.setResult("");
        proc();
      } catch (IOException | URISyntaxException e) {
        e.printStackTrace();
      }
    } else {
      error(true);
    }
    Lizzie.frame.syncLiveBoardStat();
  }

  private class DigitOnlyFilter extends DocumentFilter {
    @Override
    public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr)
        throws BadLocationException {
      String newStr = string != null ? string.replaceAll("\\D++", "") : "";
      if (!newStr.isEmpty()) {
        fb.insertString(offset, newStr, attr);
      }
    }

    @Override
    public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs)
        throws BadLocationException {
      String newStr = text != null ? text.replaceAll("\\D++", "") : "";
      if (!newStr.isEmpty()) {
        fb.replace(offset, length, newStr, attrs);
      }
    }
  }

  private void error(boolean e) {
    if (!this.isVisible() && OnlineDialog.fromBrowser) return;
    if (!isStoped && (Lizzie.frame.browser == null || Lizzie.frame.browser.isDisposed())) {
      lblError.setVisible(e);
      setVisible(true);
    }
  }

  private int checkUrl() {
    String id = null;
    chineseRule = 1;
    chineseFlag = false;
    String url = txtUrl.getText().trim();
    if (url.endsWith("/0/0")) {
      url = url.substring(0, url.length() - 4);
    }
    Pattern up =
        Pattern.compile(
            "https*://(?s).*?([^\\./]+\\.[^\\./]+)/(?s).*?(live/[a-zA-Z]+/)([^/]+)/[0-9]+/([^/]+)[^\\n]*");
    Matcher um = up.matcher(url);
    if (um.matches() && um.groupCount() >= 4) {
      int type = 1;
      id = um.group(3);
      try {
        roomId = Long.parseLong(um.group(4));
      } catch (NumberFormatException e) {
        roomId = Long.parseLong(id);
        type = 2;
      }
      if (!Utils.isBlank(id) && roomId > 0) {
        ajaxUrl = "https://api." + um.group(1) + "/golive/dtl?id=" + id + "&flag=1";
        return type;
      }
    }
    up = Pattern.compile("https*://(?s).*?([^\\./]+\\.[^\\./]+)/(?s).*?(live/[a-zA-Z]+/)([^/]+)");
    um = up.matcher(url);
    if (um.matches() && um.groupCount() >= 3) {
      id = um.group(3);
      if (!Utils.isBlank(id)) {
        ajaxUrl = "https://api." + um.group(1) + "/golive/dtl?id=" + id;
        return 2;
      }
    }

    up =
        Pattern.compile(
            "https*://(?s).*?([^\\./]+\\.[^\\./]+)/(?s).*?(game/[a-zA-Z]+/)[0-9]+/([^/]+)");
    um = up.matcher(url);
    if (um.matches() && um.groupCount() >= 3) {
      roomId = Long.parseLong(um.group(3));
      if (roomId > 0) { // !Utils.isBlank(id)) {
        ajaxUrl = "https://api." + um.group(1) + "/golive/dtl?id=" + roomId;
        return 5;
      }
    }

    up =
        Pattern.compile(
            "https*://(?s).*?([^\\./]+\\.[^\\./]+)/(?s).*?(room=)([0-9]+)(&hall)(?s).*?");
    um = up.matcher(url);
    if (um.matches() && um.groupCount() >= 3) {
      roomId = Long.parseLong(um.group(3));
      if (roomId > 0) { // !Utils.isBlank(id)) {
        ajaxUrl = "https://api." + um.group(1) + "/golive/dtl?id=" + roomId;
        return 5;
      }
    }

    try {
      URI uri = new URI(url);
      queryMap = splitQuery(uri);
      if (queryMap != null) {
        if (queryMap.get("gameid") != null && queryMap.get("createtime") != null) {
          return 3;
        } else if (queryMap.get("gametag") != null && queryMap.get("uin") != null) {
          query = uri.getRawQuery();
          ajaxUrl =
              "http://wshall."
                  + new String(b4) // uri.getHost()
                  + "/wxnseed/Broadcast/RequestBroadcast?callback=jQuery1&"
                  + query;
          return 4;
        }
      }
    } catch (URISyntaxException e) {
      e.printStackTrace();
    }

    // Try
    ajaxUrl = url;
    return 99;
  }

  private void proc() throws IOException, URISyntaxException {
    refreshTime = Utils.txtFieldValue(txtRefreshTime);
    refreshTime = (refreshTime > 0 ? refreshTime : 10);
    // if (!online.isShutdown()) {
    // online.shutdown();
    // }
    if (schedule != null && !schedule.isCancelled() && !schedule.isDone()) {
      schedule.cancel(false);
    }
    done = false;
    history = null;
    Lizzie.board.clearForOnline();
    switch (type) {
      case 1:
      case 5:
        req2(true);
        break;
      case 2:
        refresh("(?s).*?(\\\"Content\\\":\\\")(.+)(\\\",\\\")(?s).*");
        break;
      case 3:
        req(true);
        break;
      case 4:
        req0();
        break;
      case 99:
        get();
        break;
      default:
        break;
    }
  }

  public void parseSgf(String data, String format, int num, boolean decode, boolean first) {
    JSONObject o = null;
    JSONObject live = null;
    try {
      o = new JSONObject(data);
      o = o.optJSONObject("Result");
      if (o != null) {
        live = o.optJSONObject("live");
      }
    } catch (JSONException e) {
    }
    String sgf = "";
    if (live != null) {
      sgf = live.optString("Content");
    }
    if (Utils.isBlank(sgf)) {
      if (!Utils.isBlank(format)) {
        Pattern sp = Pattern.compile(format);
        Matcher sm = sp.matcher(data);
        if (sm.matches() && sm.groupCount() >= num) {
          sgf = sm.group(num);
          if (decode) {
            sgf = URLDecoder.decode(sgf);
          }
        }
      } else {
        sgf = data;
      }
    }
    try {
      BoardHistoryList liveNode = SGFParser.parseSgf(sgf, first);
      if (liveNode != null) {
        blackPlayer = liveNode.getGameInfo().getPlayerBlack();
        whitePlayer = liveNode.getGameInfo().getPlayerWhite();
        double komi = liveNode.getGameInfo().getKomi();
        int handicap = liveNode.getGameInfo().getHandicap();
        if (live != null) {
          komi = live.optDouble("komi", komi);
          handicap = live.optInt("handicap", handicap);
          blackPlayer = live.optString("BlackPlayer", blackPlayer);
          whitePlayer = live.optString("WhitePlayer", whitePlayer);
        }
        int diffMove = Lizzie.board.getHistory().sync(liveNode);
        if (diffMove >= 0) {
          //     Lizzie.board.goToMoveNumberBeyondBranch(diffMove > 0 ? diffMove - 1 : 0);
          //    while (Lizzie.board.nextMove()) ;
        }
        if (Utils.isBlank(blackPlayer)) {
          Pattern spb =
              Pattern.compile("(?s).*?(\\\"BlackPlayer\\\":\\\")([^\"]+)(\\\",\\\")(?s).*");
          Matcher smb = spb.matcher(data);
          if (smb.matches() && smb.groupCount() >= 2) {
            blackPlayer = smb.group(2);
          }
        }
        if (Utils.isBlank(whitePlayer)) {
          Pattern spw =
              Pattern.compile("(?s).*?(\\\"WhitePlayer\\\":\\\")([^\\\"]+)(\\\",\\\")(?s).*");
          Matcher smw = spw.matcher(data);
          if (smw.matches() && smw.groupCount() >= 2) {
            whitePlayer = smw.group(2);
          }
        }
        if (first) {
          Lizzie.frame.setPlayers(whitePlayer, blackPlayer);
          Lizzie.board.getHistory().getGameInfo().setPlayerBlack(blackPlayer);
          Lizzie.board.getHistory().getGameInfo().setPlayerWhite(whitePlayer);
          if (Lizzie.config.readKomi) {
            Lizzie.board.getHistory().getGameInfo().setKomi(komi);
            Lizzie.leelaz.komi(komi);
          }
          firstTime = false;
        }
        if (live != null && "3".equals(live.optString("Status"))) {
          if (schedule != null && !schedule.isCancelled() && !schedule.isDone()) {
            schedule.cancel(false);
          }
          String result = live.optString("GameResult");
          if (!Utils.isBlank(result)) {
            Lizzie.board.getHistory().getData().comment =
                result + "\n" + Lizzie.board.getHistory().getData().comment;
            Lizzie.board.previousMove(false);
            Lizzie.board.nextMove(true);
          }
        }
      } else {
        error(true);
      }
    } catch (NullPointerException e) {
      error(true);
    }
  }

  public void get() throws IOException {

    URL url = new URL(ajaxUrl);
    HttpURLConnection con = (HttpURLConnection) url.openConnection();

    con.setRequestMethod("GET");
    con.setRequestProperty(
        "User-Agent",
        "Mozilla/5.0 (Linux; U; Android 2.3.6; zh-cn; GT-S5660 Build/GINGERBREAD) AppleWebKit/533.1 (KHTML, like Gecko) Version/4.0 Mobile Safari/533.1 MicroMessenger/4.5.255");

    con.getResponseCode();

    BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
    StringBuffer response = new StringBuffer();
    String line;
    while ((line = in.readLine()) != null) {
      response.append(line);
    }
    in.close();
    String sgf = response.toString();
    parseSgf(sgf, "", 0, false, true);
  }

  public void refresh(String format) throws IOException {
    refresh(format, 2, true, false);
  }

  public void refresh(String format, int num, boolean needSchedule, boolean decode)
      throws IOException {
    Map params = new HashMap();
    final AjaxHttpRequest ajax = new AjaxHttpRequest();

    ajax.setReadyStateChangeListener(
        new AjaxHttpRequest.ReadyStateChangeListener() {
          public void onReadyStateChange() {
            int readyState = ajax.getReadyState();
            if (readyState == AjaxHttpRequest.STATE_COMPLETE) {
              String sgf = ajax.getResponseText();
              parseSgf(sgf, format, num, decode, firstTime);
            }
          }
        });

    if (needSchedule && !isStoped && type == 101) { // 弈客暂时不需要刷新了
      timer =
          new Timer(
              refreshTime * 1000,
              new ActionListener() {
                public void actionPerformed(ActionEvent evt) {
                  if (!LizzieFrame.urlSgf) {
                    timer.stop();
                    ajax.abort();
                  } else {
                    try {
                      ajax.open("GET", ajaxUrl, true);
                      ajax.send(params);
                    } catch (IOException e) {
                      e.printStackTrace();
                    }
                  }
                }
              });
      timer.start();
    } else {
      try {
        ajax.open("GET", ajaxUrl, true);
        ajax.send(params);
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  public void getSgf(String chessid) {
    if (!Utils.isBlank(chessid)) {
      ajaxUrl = new String(b3) + chessid;
      try {
        refresh("(jQuery1\\(\\\")([^\\\"]+)(?s).*", 2, false, true);
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  public void req0() throws IOException {
    Map params = new HashMap();
    final AjaxHttpRequest ajax = new AjaxHttpRequest();

    ajax.setReadyStateChangeListener(
        new AjaxHttpRequest.ReadyStateChangeListener() {
          public void onReadyStateChange() {
            int readyState = ajax.getReadyState();
            if (readyState == AjaxHttpRequest.STATE_COMPLETE) {
              String format = "jQuery[^\\(]*\\(((?s).*?)\\)";
              Pattern sp = Pattern.compile(format);
              Matcher sm = sp.matcher(ajax.getResponseText());
              if (sm.matches() && sm.groupCount() == 1) {
                JSONObject o = new JSONObject(sm.group(1));
                if (0 == o.optInt("result") && 0 == o.optInt("ResultID")) {
                  chineseRule = 0;
                }
                List list = new ArrayList();
                list.add("369");
                queryMap.put("gameid", list);
                list = new ArrayList();
                list.add(o.optString("ShowType"));
                queryMap.put("showtype", list);
                list = new ArrayList();
                list.add(o.optString("ShowID"));
                queryMap.put("showid", list);
                list = new ArrayList();
                list.add(o.optString("CreateTime"));
                queryMap.put("createtime", list);

                try {
                  req(true);
                } catch (URISyntaxException e) {
                  e.printStackTrace();
                }
              }
            }
          }
        });

    try {
      ajax.open("GET", ajaxUrl, true);
      ajax.send(params);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private void reReq() {
    try {
      req(true);
    } catch (URISyntaxException e) {
      e.printStackTrace();
    }
  }

  private void req(boolean clear) throws URISyntaxException {
    seqs = 0;
    URI uri = new URI(new String(type == 3 ? b : b2));

    if (clear) Lizzie.board.clearForOnline();
    if (client != null && client.isOpen()) {
      client.close();
    }
    client =
        new WebSocketClient(uri) {

          public void onOpen(ServerHandshake arg0) {
            byte[] req1 =
                req1(
                    90,
                    ++seqs,
                    23406,
                    Utils.intOfMap(queryMap, "gameid"),
                    Utils.intOfMap(queryMap, "showtype"),
                    Utils.intOfMap(queryMap, "showid"),
                    Utils.intOfMap(queryMap, "createtime"));
            client.send(req1);
          }

          public void onMessage(String arg0) {
            // System.out.println("socket message" + arg0);
          }

          public void onError(Exception arg0) {
            // arg0.printStackTrace();
            // System.out.println("socket error");
          }

          public void onClose(int arg0, String arg1, boolean arg2) {
            // System.out.println("socket close:" + arg0 + ":" + arg1 + ":" + arg2);
          }

          public void onMessage(ByteBuffer bytes) {
            // System.out.println("socket message ByteBuffer" +
            // byteArrayToHexString(bytes.array()));
            if (!isStoped) parseReq(bytes);
          }
        };

    client.connect();
  }

  public byte[] req1(int len, int seq, int msgID, int gameId, int showType, int showId, int time) {
    ByteBuffer bytes = ByteBuffer.allocate(len);
    bytes.putShort((short) len);
    bytes.putShort((short) 1);
    bytes.putInt(seq);
    bytes.putShort((short) -4);
    bytes.putInt(50000);
    bytes.put((byte) 0);
    bytes.put((byte) 0);
    bytes.putShort((short) msgID);
    bytes.putShort((short) 0);
    bytes.putInt(1000);
    bytes.put((byte) 0);
    bytes.put((byte) 234);
    bytes.putShort((short) 0);
    bytes.putShort((short) 0);
    bytes.putShort((short) 60);
    bytes.putInt(0);
    bytes.putInt(gameId);
    bytes.putInt(showType);
    bytes.putInt(showId);
    bytes.putInt(0);
    bytes.putInt(-1);
    bytes.putInt(0);
    bytes.putShort((short) 0);
    bytes.putInt(3601);
    bytes.putInt(time);
    bytes.putInt(0);
    bytes.putInt(0);
    bytes.putInt(0);
    bytes.putInt(0);
    bytes.putInt(1);
    return bytes.array();
  }

  public byte[] req2(int len, int seq, int msgID, int gameId, int showType, int showId, int time) {
    ByteBuffer bytes = ByteBuffer.allocate(len);
    bytes.putShort((short) len);
    bytes.putShort((short) 1);
    bytes.putInt(seq);
    bytes.putShort((short) -4);
    bytes.putInt(50000);
    bytes.put((byte) 0);
    bytes.put((byte) 0);
    bytes.putShort((short) msgID);
    bytes.putShort((short) 0);
    bytes.putInt(1000);
    bytes.put((byte) 0);
    bytes.put((byte) 234);
    bytes.putShort((short) 0);
    bytes.putShort((short) 0);
    bytes.putShort((short) 24);
    bytes.putInt(0);
    bytes.putInt(gameId);
    bytes.putInt(showType);
    bytes.putInt(showId);
    bytes.putShort((short) 0);
    bytes.putInt(3601);
    return bytes.array();
  }

  public void parseReq(ByteBuffer res) {
    res.get();
    res.get();
    int msgID = res.getShort();
    // System.out.println("recv msgID:" + msgID);
    if (msgID == 23406) {
      res.getShort();
      res.getInt();
      res.get();
      res.get();
      res.getShort();
      res.getShort();
      res.getShort();

      int resultId = res.getInt();
      res.getInt();
      res.getInt();
      res.getInt();
      res.getInt();
      int showFragmentNum = res.getInt();
      List<Fragment> fragmentList = new ArrayList<Fragment>();
      if (showFragmentNum > 0) {
        for (int i = 0; i < showFragmentNum; i++) {
          int len = res.getShort();
          byte[] frag = new byte[len];
          res.get(frag, res.arrayOffset(), len);
          fragmentList.add(new Fragment(len, frag));
        }

        processFrag(fragmentList);
      }

      if (resultId == 23409 || resultId == 23412) {
        done = true;
        getSgf(Utils.stringOfMap(queryMap, "chessid"));
      }

      res.getInt();
      res.getInt();
      res.getInt();
      int transparentLen = res.getShort();
      // TODO
      if (transparentLen > 0) {
        // Transparent
      } else {
        res.get();
      }
      if (type == 3) {
        res.getInt();
        res.getInt();
        res.getInt();
      }

      if (!done && (schedule == null || schedule.isCancelled() || schedule.isDone())) {
        schedule =
            online.scheduleAtFixedRate(
                new Runnable() {
                  @Override
                  public void run() {
                    if (!LizzieFrame.urlSgf) {
                      online.shutdown();
                      schedule.cancel(true);
                      return;
                    }
                    if (client.isOpen()) {
                      byte[] req2 =
                          req2(
                              54,
                              ++seqs,
                              23413,
                              Utils.intOfMap(queryMap, "gameid"),
                              Utils.intOfMap(queryMap, "showtype"),
                              Utils.intOfMap(queryMap, "showid"),
                              Utils.intOfMap(queryMap, "createtime"));
                      client.send(req2);
                    } else {
                      schedule.cancel(false);
                      if (!done) {
                        reReq();
                      }
                    }
                  }
                },
                1,
                refreshTime,
                TimeUnit.SECONDS);
      }
    } else if (msgID == 23407) {
      res.getShort();
      res.getInt();
      res.get();
      res.get();
      res.getShort();
      res.getShort();
      res.getShort();

      res.getInt();
      res.getInt();
      res.getInt();
      res.getInt();
      int showFragmentNum = res.getInt();
      List<Fragment> fragmentList = new ArrayList<Fragment>();
      if (showFragmentNum > 0) {
        for (int i = 0; i < showFragmentNum; i++) {
          int len = res.getShort();
          byte[] frag = new byte[len];
          res.get(frag, res.arrayOffset(), len);
          fragmentList.add(new Fragment(len, frag));
        }
        processFrag(fragmentList);
      }

    } else if (msgID == 23413) {
      res.getShort();
      res.getInt();
      res.get();
      res.get();
      res.getShort();
      res.getShort();
      res.getShort();

      res.getInt();
      res.getInt();
      res.getInt();
      res.getInt();
      res.getInt();
      res.getInt();
      int tipsLen = res.getInt();
      if (tipsLen > 0) {
        for (int i = 0; i < tipsLen; i++) {
          int len = res.getShort();
          byte[] tips = new byte[len];
          res.get(tips, res.arrayOffset(), len);
          // TODO
        }
      }
      res.getInt();
      int transparentLen = res.getShort();
      // TODO
      if (transparentLen > 0) {
        // Transparent
      }
      if (type == 3) {
        res.getInt();
        res.getInt();
        res.getInt();
      }
    } else if (msgID == 23414) {
      res.getShort();
      res.getInt();
      res.get();
      res.get();
      res.getShort();
      res.getShort();
      res.getShort();

      res.getInt();
      res.getInt();
      res.getInt();
      res.getInt();
      res.getInt();
      int transparentDataLen = res.getShort();
      List<Fragment> fragmentList = new ArrayList<Fragment>();
      if (transparentDataLen > 0) {
        byte[] frag = new byte[transparentDataLen];
        res.get(frag, res.arrayOffset(), transparentDataLen);
        fragmentList.add(new Fragment(transparentDataLen, frag));
        processFrag(fragmentList);
      }
    }
  }

  private void processFrag(List<Fragment> fragmentList) {

    for (Fragment f : fragmentList) {
      if (f != null) {
        // System.out.println("Msg:" + f.type + ":" + (f.line != null ?
        // f.line.toString() :
        // ""));
        if (f.type == 20032) {
          int size = ((JSONObject) f.line.opt("AAA307")).optInt("AAA16");
          int handicap = ((JSONObject) f.line.opt("AAA307")).optInt("AAA4");
          if (size > 0) {
            boardSize = size;
            Lizzie.board.reopen(boardSize, boardSize);
            history = new BoardHistoryList(BoardData.empty(size, size)); // TODO boardSize
            JSONObject a309 = ((JSONObject) f.line.opt("AAA309"));
            blackPlayer =
                a309 == null
                    ? ""
                    : ("86".equals(a309.optString("AAA227"))
                        ? a309.optString("AAA225")
                        : a309.optString("AAA224"));
            JSONObject a308 = ((JSONObject) f.line.opt("AAA308"));
            whitePlayer =
                a308 == null
                    ? ""
                    : ("86".equals(a308.optString("AAA227"))
                        ? a308.optString("AAA225")
                        : a308.optString("AAA224"));
            Lizzie.frame.setPlayers(whitePlayer, blackPlayer);
            Lizzie.board.getHistory().getGameInfo().setPlayerBlack(blackPlayer);
            Lizzie.board.getHistory().getGameInfo().setPlayerWhite(whitePlayer);
            if (handicap > 1) Lizzie.board.getHistory().getGameInfo().setHandicap(handicap);

            if (size == 19) {
              switch (handicap) {
                case 2:
                  history.place(15, 3, Stone.BLACK, false, false);
                  history.place(3, 15, Stone.BLACK, false, false);
                  break;
                case 3:
                  history.place(3, 3, Stone.BLACK);
                  history.place(15, 3, Stone.BLACK);
                  history.place(3, 15, Stone.BLACK);
                  break;
                case 4:
                  history.place(3, 3, Stone.BLACK);
                  history.place(3, 15, Stone.BLACK);
                  history.place(15, 3, Stone.BLACK);
                  history.place(15, 15, Stone.BLACK);
                  break;
                case 5:
                  history.place(3, 3, Stone.BLACK);
                  history.place(3, 15, Stone.BLACK);
                  history.place(15, 3, Stone.BLACK);
                  history.place(15, 15, Stone.BLACK);
                  history.place(9, 9, Stone.BLACK);
                  break;
                case 6:
                  history.place(3, 3, Stone.BLACK);
                  history.place(3, 15, Stone.BLACK);
                  history.place(15, 3, Stone.BLACK);
                  history.place(15, 15, Stone.BLACK);
                  history.place(3, 9, Stone.BLACK);
                  history.place(15, 9, Stone.BLACK);
                  break;
                case 7:
                  history.place(3, 3, Stone.BLACK);
                  history.place(3, 15, Stone.BLACK);
                  history.place(15, 3, Stone.BLACK);
                  history.place(15, 15, Stone.BLACK);
                  history.place(3, 9, Stone.BLACK);
                  history.place(15, 9, Stone.BLACK);
                  history.place(9, 9, Stone.BLACK);
                  break;
                case 8:
                  history.place(3, 3, Stone.BLACK);
                  history.place(3, 15, Stone.BLACK);
                  history.place(15, 3, Stone.BLACK);
                  history.place(15, 15, Stone.BLACK);
                  history.place(9, 3, Stone.BLACK);
                  history.place(9, 15, Stone.BLACK);
                  history.place(3, 9, Stone.BLACK);
                  history.place(15, 9, Stone.BLACK);
                  break;
                case 9:
                  history.place(3, 3, Stone.BLACK);
                  history.place(3, 15, Stone.BLACK);
                  history.place(15, 3, Stone.BLACK);
                  history.place(15, 15, Stone.BLACK);
                  history.place(9, 3, Stone.BLACK);
                  history.place(9, 15, Stone.BLACK);
                  history.place(3, 9, Stone.BLACK);
                  history.place(15, 9, Stone.BLACK);
                  history.place(9, 9, Stone.BLACK);
                  break;
              }
            }
            double komi = Lizzie.board.getHistory().getGameInfo().getKomi();
            int a4 = ((JSONObject) f.line.opt("AAA307")).optInt("AAA4");
            int a5 = ((JSONObject) f.line.opt("AAA307")).optInt("AAA5");
            int a10 = ((JSONObject) f.line.opt("AAA307")).optInt("AAA10");
            if (0 == a4 && 0 == a5) {
              komi = 6.5;
            } else if (1 == a10 && 1 == chineseRule) {
              chineseFlag = true;
              komi = ((double) a5 / 100 * 2);
            } else {
              komi = ((double) a5 / 100);
            }
            if (Lizzie.config.readKomi) {
              Lizzie.board.getHistory().getGameInfo().setKomi(komi);
              Lizzie.leelaz.komi(komi);
            }
          } else {
            break;
          }
        } else if (f.type == 4116) {
          long tu = wuid;
          wuid = buid;
          buid = tu;
          String tt = wTime;
          wTime = bTime;
          bTime = tt;
          String t = whitePlayer;
          whitePlayer = blackPlayer;
          blackPlayer = t;
          Lizzie.frame.setPlayers(whitePlayer, blackPlayer);
          Lizzie.board.getHistory().getGameInfo().setPlayerBlack(blackPlayer);
          Lizzie.board.getHistory().getGameInfo().setPlayerWhite(whitePlayer);
        } else if (f.type == 7005) {
          long uid = f.line.optLong("AAA303");
          int handicap = Lizzie.board.getHistory().getGameInfo().getHandicap();
          int num = f.line.optInt("AAA102") + handicap;
          if (num == 0) {
            num = history.getData().moveNumber + 1;
          }
          //          Stone color;
          //          if (handicap > 0)
          //            color = history.getLastMoveColor() == Stone.WHITE ? Stone.BLACK :
          // Stone.WHITE;
          //          else color = (num % 2 != 0) ? Stone.BLACK : Stone.WHITE;
          //          //    Stone color = ((f.line.optInt("AAA158") & 3) == 1) ? Stone.WHITE :
          // Stone.BLACK;
          //          if (uid > 0) {
          //            if (Stone.BLACK.equals(color)) {
          //              buid = uid;
          //            } else {
          //              wuid = uid;
          //            }
          //          }
          Stone color = ((f.line.optInt("AAA158") & 3) == 1) ? Stone.WHITE : Stone.BLACK;
          if (uid > 0) {
            if (Stone.BLACK.equals(color)) {
              buid = uid;
            } else {
              wuid = uid;
            }
          }
          int index = f.line.optInt("AAA106");
          int[] coord = asCoord(Stone.BLACK.equals(color) ? index : index - 1024);
          boolean changeMove = false;

          if (num <= history.getMoveNumber()) {
            int cur = history.getMoveNumber();
            for (int i = num; i <= cur; i++) {
              BoardHistoryNode currentNode = history.getCurrentHistoryNode();
              boolean isSameMove = (i == cur && currentNode.getData().isSameCoord(coord));
              if (currentNode.previous().isPresent()) {
                BoardHistoryNode pre = currentNode.previous().get();
                history.previous();
                if (pre.numberOfChildren() <= 1 && !isSameMove) {
                  int idx = pre.indexOfNode(currentNode);
                  pre.deleteChild(idx);
                  changeMove = false;
                } else {
                  changeMove = true;
                }
              }
            }
          }

          if (coord == null || !Board.isValid(coord)) {
            history.pass(color, false, false);
          } else {
            history.place(coord[0], coord[1], color, false, changeMove);
          }
        } else if (f.type == 7045) {
          Stone color = history.getLastMoveColor() == Stone.WHITE ? Stone.BLACK : Stone.WHITE;
          history.pass(color, false, false);
        } else if (f.type == 7198) {
          long uid = f.line.optLong("AAA303");
          int time = f.line.optInt("AAA196");
          int readCount = f.line.optInt("AAA197");
          int readTime = f.line.optInt("AAA198");
          if (uid > 0) {
            if (uid == buid) {
              bTime =
                  String.format(
                      "%d:%02d %d %d", (int) (time / 60), (int) (time % 60), readCount, readTime);
            } else {
              wTime =
                  String.format(
                      "%d:%02d %d %d", (int) (time / 60), (int) (time % 60), readCount, readTime);
            }
          }
          // Lizzie.frame.updateBasicInfo(bTime, wTime);
        } else if (f.type == 8005) {
          int num = f.line.optInt("AAA72");
          String comment = f.line.optString("AAA37");
          if (num > 0 && !Utils.isBlank(comment)) {
            history.goToMoveNumber(num, false);
            history.getData().comment += comment + "\n";
            while (history.next(true).isPresent()) ;
          }
        } else if (f.type == 8185) {
          JSONObject branch = (JSONObject) f.line.opt("AAA79");
          if (branch != null) {
            int moveNum = branch.optInt("AAA20") - 1;
            if (moveNum > 0) {
              history.goToMoveNumber(moveNum, false);
              String branchCmt = branch.optString("AAA283");
              JSONArray branchMoves = branch.optJSONArray("AAA106");
              if (branchMoves != null && branchMoves.length() > 0) {
                if (history.getCurrentHistoryNode().numberOfChildren() == 0) {
                  // BoardData data = BoardData.empty(boardSize);
                  // data.moveMNNumber = history.getData().moveMNNumber + 1;
                  // data.moveNumber = history.getData().moveNumber + 1;
                  // history.getCurrentHistoryNode().addOrGoto(data);
                  Stone color =
                      history.getLastMoveColor() == Stone.WHITE ? Stone.BLACK : Stone.WHITE;
                  history.pass(color, false, true);
                  history.previous();
                }
                for (int i = 0; i < branchMoves.length(); i++) {
                  Stone color =
                      history.getLastMoveColor() == Stone.WHITE ? Stone.BLACK : Stone.WHITE;
                  int index = branchMoves.getInt(i);
                  int[] coord = asCoord(Stone.BLACK.equals(color) ? index : index - 1024);
                  history.place(coord[0], coord[1], color, i == 0);
                  if (i == 0) {
                    history.getData().comment += branchCmt + "\n";
                  }
                }
                history.toBranchTop();
                while (history.next(true).isPresent()) ;
              }
            }
          }
        } else if (f.type == 7185 || f.type == 7186) {
          done = true;
          if (schedule != null && !schedule.isCancelled() && !schedule.isDone()) {
            schedule.cancel(false);
          }
          if (client != null && client.isOpen()) {
            client.close();
          }
          String result = result(f.type, f.line);
          while (history.next().isPresent()) ;
          history.getEnd().getData().comment = result + "\n" + history.getEnd().getData().comment;
          Lizzie.frame.setResult(result);
          Lizzie.board.getHistory().getGameInfo().setResult(result);
        }
      }
    }
    if (history != null) {
      while (history.previous().isPresent()) ;
      int diffMove = Lizzie.board.getHistory().sync(history);
      if (diffMove >= 0) {
        //     Lizzie.board.goToMoveNumberBeyondBranch(diffMove > 0 ? diffMove - 1 : 0);
        //    while (Lizzie.board.nextMove()) {}
      }
      while (history.next(true).isPresent()) ;
    }
  }

  private String decimalToFraction(double e) {
    if (e == 0.0) return "";
    int c = 0;
    int b = 10;
    while (e != Math.floor(e)) {
      e *= b;
      c++;
    }
    b = (int) Math.pow(b, c);
    int nor = (int) e;
    int gcd = gcd(nor, b);

    return String.valueOf(nor / gcd) + "/" + String.valueOf(b / gcd);
  }

  private int gcd(int a, int b) {
    if (a == 0) {
      return b;
    }
    return gcd(b % a, a);
  }

  private String result(long type, JSONObject i) {
    String F = "";
    if (type == 7185) {
      if (i.optDouble("AAA167") > 0) {
        double w = i.optDouble("AAA167") / 100;
        if (1 == i.optInt("AAA166")) {
          int I = (int) w;
          double b = w - I;
          String C = decimalToFraction(b);
          F =
              chineseFlag
                  ? (0 != I ? "黑胜" + I + (Utils.isBlank(C) ? "" : "又" + C) + "子" : "黑胜" + C + "子")
                  : "黑胜" + w + "目";
        } else if (2 == i.optInt("AAA166")) {
          int E = (int) w;
          double d = w - E;
          String D = decimalToFraction(d);
          F =
              chineseFlag
                  ? (0 != E ? "白胜" + E + (Utils.isBlank(D) ? "" : "又" + D) + "子" : "白胜" + D + "子")
                  : "白胜" + w + "目";
        } else {
          F = "和棋";
        }
      } else {
        F = (1 == i.optInt("AAA166") ? "黑中盘胜" : (2 == i.optInt("AAA166") ? "白中盘胜" : "和棋"));
      }
    } else if (type == 7186) {
      F = "";
      if (i.optDouble("AAA167") > 0) {
        String[] w = String.valueOf(i.optDouble("AAA167") / 100).split(".");
        String w1 = w.length >= 2 ? "半" : "";
        F =
            (1 == i.optInt("AAA166")
                ? "黑" + w[0] + "目" + w1 + "胜"
                : (2 == i.optInt("AAA166") ? "白" + w[0] + "目" + w1 + "胜" : "和棋"));
      } else {
        F = (1 == i.optInt("AAA166") ? "黑中盘胜" : (2 == i.optInt("AAA166") ? "白中盘胜" : "和棋"));
      }
    }
    return F;
  }

  private int[] asCoord(int index) {
    int[] coord = new int[2];
    if (index >= 1024) {
      int i = index - 1024;
      coord[0] = i % 32;
      coord[1] = i / 32;
    }
    return coord;
  }

  private class Fragment {
    public long type;
    public JSONObject line;

    public Fragment(int len, byte[] frag) {
      Proto o = parseProto(frag);
      // System.out.println("type:" + o.type);
      // System.out.println("raw:" + byteArrayToHexString(o.raw));
      this.type = o.type;
      if (o.type == 20032) {
        line = decode52(ByteBuffer.wrap(o.raw));
      } else if (o.type == 4116) {
        line = decode53(ByteBuffer.wrap(o.raw));
      } else if (o.type == 7005) {
        line = decode20(ByteBuffer.wrap(o.raw));
      } else if (o.type == 8005) {
        line = decode7(ByteBuffer.wrap(o.raw));
      } else if (o.type == 7025) {
        // TODO AA23
        o.type = 0;
      } else if (o.type == 8185) {
        line = decode17(ByteBuffer.wrap(o.raw));
      } else if (o.type == 7185) {
        line = decode35(ByteBuffer.wrap(o.raw));
      } else if (o.type == 7198) {
        line = decode42(ByteBuffer.wrap(o.raw));
      }
    }

    private JSONObject decode52(ByteBuffer buf) {
      JSONObject m = new JSONObject();
      while (buf.position() < buf.array().length) {
        long t = uint32(buf);
        t = t >>> 3;
        if (t == 1) {
          m.put("AAA311", uint32(buf));
        } else if (t == 2) {
          m.put("AAA303", uint64(buf));
        } else if (t == 3) {
          m.put("AAA312", uint32(buf));
        } else if (t == 4) {
          m.put("AAA305", uint64(buf));
        } else if (t == 5) {
          m.put("AAA306", uint64(buf));
        } else if (t == 6) {
          int len = (int) uint32(buf);
          byte[] newB = new byte[len];
          buf.get(newB, 0, len);
          m.put("AAA307", decode1(ByteBuffer.wrap(newB)));
        } else if (t == 7) {
          int len = (int) uint32(buf);
          byte[] newB = new byte[len];
          buf.get(newB, 0, len);
          m.put("AAA308", decode48(ByteBuffer.wrap(newB)));
        } else if (t == 8) {
          int len = (int) uint32(buf);
          byte[] newB = new byte[len];
          buf.get(newB, 0, len);
          m.put("AAA309", decode48(ByteBuffer.wrap(newB)));
        } else if (t == 9) {
          m.put("AAA310", uint64(buf));
        } else {
          // TODO
          break;
          // skipType(buf, (int) (t & 7));
        }
      }
      return m;
    }

    private JSONObject decode1(ByteBuffer buf) {
      JSONObject m = new JSONObject();
      while (buf.position() < buf.array().length) {
        long tl = uint32(buf);
        int t = (int) (tl >>> 3);
        switch (t) {
          case 1:
            m.put("AAA1", uint64(buf));
            break;
          case 2:
            m.put("AAA2", uint64(buf));
            break;
          case 3:
            m.put("AAA3", uint32(buf));
            break;
          case 4:
            m.put("AAA4", uint32(buf));
            break;
          case 5:
            m.put("AAA5", uint32(buf));
            break;
          case 6:
            m.put("AAA6", uint32(buf));
            break;
          case 7:
            m.put("AAA7", uint32(buf));
            break;
          case 8:
            m.put("AAA8", uint32(buf));
            break;
          case 9:
            m.put("AAA9", uint32(buf));
            break;
          case 10:
            m.put("AAA10", uint32(buf));
            break;
          case 11:
            m.put("AAA11", uint32(buf));
            break;
          case 12:
            m.put("AAA12", uint32(buf));
            break;
          case 13:
            m.put("AAA13", uint32(buf));
            break;
          case 14:
            m.put("AAA14", uint32(buf));
            break;
          case 15:
            m.put("AAA15", uint32(buf));
            break;
          case 16:
            m.put("AAA16", uint32(buf));
            break;
          case 17:
            m.put("AAA17", uint32(buf));
            break;
          default:
            // skipType(buf, (int) (t & 7));
            break;
        }
      }
      return m;
    }

    private JSONObject decode48(ByteBuffer buf) {
      JSONObject m = new JSONObject();
      while (buf.position() < buf.array().length) {
        long tl = uint32(buf);
        int t = (int) (tl >>> 3);
        switch (t) {
          case 1:
            m.put("AAA224", readString(buf));
            break;
          case 2:
            m.put("AAA225", readString(buf));
            break;
          case 3:
            m.put("AAA226", uint32(buf));
            break;
          case 4:
            m.put("AAA227", uint32(buf));
            break;
          case 5:
            m.put("AAA228", uint32(buf));
            break;
          case 6:
            m.put("AAA234", uint32(buf));
            break;
          case 7:
            m.put("AAA248", uint32(buf));
            break;
          case 8:
            m.put("AAA249", uint32(buf));
            break;
          case 9:
            m.put("AAA250", uint64(buf));
            break;
          case 10:
            m.put("AAA251", readString(buf));
            break;
          default:
            // skipType(buf, (int) (t & 7));
            break;
        }
      }
      return m;
    }

    private JSONObject decode20(ByteBuffer buf) {
      JSONObject m = new JSONObject();
      while (buf.position() < buf.array().length) {
        long tl = uint32(buf);
        int t = (int) (tl >>> 3);
        switch (t) {
          case 1:
            m.put("AAA311", uint32(buf));
            break;
          case 2:
            m.put("AAA303", uint64(buf));
            break;
          case 3:
            m.put("AAA312", uint32(buf));
            break;
          case 4:
            m.put("AAA106", uint32(buf));
            break;
          case 5:
            m.put("AAA168", uint32(buf));
            break;
          case 6:
            m.put("AAA158", uint32(buf));
            break;
          case 7:
            m.put("AAA109", uint32(buf));
            break;
          case 8:
            m.put("AAA102", uint32(buf));
            break;
          default:
            // r.skipType(t&7)
            // break;
            return m;
        }
      }
      return m;
    }

    private JSONObject decode35(ByteBuffer buf) {
      JSONObject m = new JSONObject();
      while (buf.position() < buf.array().length) {
        long tl = uint32(buf);
        int t = (int) (tl >>> 3);
        switch (t) {
          case 1:
            m.put("AAA311", uint32(buf));
            break;
          case 2:
            m.put("AAA303", uint64(buf));
            break;
          case 3:
            m.put("AAA312", uint32(buf));
            break;
          case 4:
            m.put("AAA166", uint32(buf));
            break;
          case 5:
            m.put("AAA167", (int) uint32(buf));
            break;
          case 6:
            m.put("AAA168", uint32(buf));
            break;
          default:
            // r.skipType(t&7)
            // break;
            return m;
        }
      }
      return m;
    }

    private JSONObject decode53(ByteBuffer buf) {
      JSONObject m = new JSONObject();
      while (buf.position() < buf.array().length) {
        long tl = uint32(buf);
        int t = (int) (tl >>> 3);
        switch (t) {
          case 1:
            m.put("AAA311", uint32(buf));
            break;
          case 2:
            m.put("AAA312", uint32(buf));
            break;
          default:
            // r.skipType(t&7)
            // break;
            return m;
        }
      }
      return m;
    }

    private JSONObject decode7(ByteBuffer buf) {
      JSONObject m = new JSONObject();
      while (buf.position() < buf.array().length) {
        long tl = uint32(buf);
        int t = (int) (tl >>> 3);
        switch (t) {
          case 1:
            m.put("AAA311", uint32(buf));
            break;
          case 2:
            m.put("AAA303", uint64(buf));
            break;
          case 3:
            m.put("AAA312", uint32(buf));
            break;
          case 4:
            m.put("AAA72", uint32(buf));
            break;
          case 5:
            m.put("AAA37", readString(buf));
            break;
          case 6:
            m.put("AAA38", uint32(buf));
            break;
          default:
            // r.skipType(t&7)
            // break;
            return m;
        }
      }
      return m;
    }

    private JSONObject decode17(ByteBuffer buf) {
      JSONObject m = new JSONObject();
      while (buf.position() < buf.array().length) {
        long tl = uint32(buf);
        int t = (int) (tl >>> 3);
        switch (t) {
          case 1:
            m.put("AAA311", uint32(buf));
            break;
          case 2:
            m.put("AAA303", uint64(buf));
            break;
          case 3:
            m.put("AAA312", uint32(buf));
            break;
          case 4:
            m.put("AAA77", uint32(buf));
            break;
          case 5:
            m.put("AAA78", uint32(buf));
            break;
          case 6:
            int len = (int) uint32(buf);
            byte[] newB = new byte[len];
            buf.get(newB, 0, len);
            m.put("AAA79", decode2(ByteBuffer.wrap(newB)));
            break;
          case 7:
            // TODO AAA80
            break;
          default:
            // r.skipType(t&7)
            // break;
            return m;
        }
      }
      return m;
    }

    private JSONObject decode2(ByteBuffer buf) {
      JSONObject m = new JSONObject();
      while (buf.position() < buf.array().length) {
        long tl = uint32(buf);
        int t = (int) (tl >>> 3);
        switch (t) {
          case 1:
            if (m.optJSONArray("AAA106") == null) {
              m.put("AAA106", new JSONArray("[]"));
            }
            m.getJSONArray("AAA106").put(uint32(buf));
            break;
          case 2:
            m.put("AAA19", uint32(buf));
            break;
          case 3:
            m.put("AAA20", uint32(buf));
            break;
          case 4:
            m.put("AAA283", readString(buf));
            break;
          case 5:
            if (m.optJSONArray("AAA37") == null) {
              m.put("AAA37", new JSONArray("[]"));
            }
            m.getJSONArray("AAA37").put(readString(buf));
            break;
          default:
            // r.skipType(t&7)
            // break;
            return m;
        }
      }
      return m;
    }

    public JSONObject decode42(ByteBuffer buf) {
      JSONObject m = new JSONObject();
      while (buf.position() < buf.array().length) {
        long tl = uint32(buf);
        int t = (int) (tl >>> 3);
        switch (t) {
          case 1:
            m.put("AAA311", uint32(buf));
            break;
          case 2:
            m.put("AAA303", uint64(buf));
            break;
          case 3:
            m.put("AAA312", uint32(buf));
            break;
          case 4:
            m.put("AAA196", uint32(buf));
            break;
          case 5:
            m.put("AAA197", uint32(buf));
            break;
          case 6:
            m.put("AAA198", uint32(buf));
            break;
          default:
            // r.skipType(t&7)
            // break;
            return m;
        }
      }
      return m;
    }

    private long uint32(ByteBuffer buf) {
      long i = 0;
      long b = buf.get() & 0xFF;
      i = (127 & b) >>> 0;
      if (b < 128) return i;
      b = buf.get() & 0xFF;
      i = (i | (127 & b) << 7) >>> 0;
      if (b < 128) return i;
      b = buf.get() & 0xFF;
      i = (i | (127 & b) << 14) >>> 0;
      if (b < 128) return i;
      b = buf.get() & 0xFF;
      i = (i | (127 & b) << 21) >>> 0;
      if (b < 128) return i;
      b = buf.get() & 0xFF;
      i = (i | (15 & b) << 28) >>> 0;
      if (b < 128) return i;
      b = buf.get() & 0xFF;
      // TODO
      return i;
    }

    private long uint64(ByteBuffer buf) {
      Uint64 e = u(buf);
      if (e != null && ((e.hi >>> 31) != 0)) {
        long t = 1 + ~e.lo >>> 0;
        long o = ~e.hi >>> 0;
        if (t == 0) {
          o = o + 1 >>> 0;
          return -(t + 4294967296L * o);
        }
        return t;
      }
      return e.lo + 4294967296L * e.hi;
    }

    private Uint64 u(ByteBuffer buf) {
      int t = 0;
      Uint64 e = new Uint64();
      long b = 0;
      if (!(buf.array().length - buf.position() > 4)) {
        for (; t < 3; ++t) {
          if (buf.position() >= buf.array().length) return e;
          b = buf.get() & 0xFF;
          e.lo = (e.lo | (127 & b) << 7 * t) >>> 0;
          if (b < 128) return e;
        }
        b = buf.get() & 0xFF;
        e.lo = (e.lo | (127 & b) << 7 * t) >>> 0;
        return e;
      }
      for (; t < 4; ++t) {
        b = buf.get() & 0xFF;
        e.lo = (e.lo | (127 & b) << 7 * t) >>> 0;
        if (b < 128) return e;
      }
      b = buf.get() & 0xFF;
      e.lo = (e.lo | (127 & b) << 28) >>> 0;
      e.hi = (e.hi | (127 & b) >> 4) >>> 0;
      if (b < 128) return e;
      t = 0;
      if (buf.array().length - buf.position() > 4) {
        for (; t < 5; ++t) {
          b = buf.get() & 0xFF;
          e.hi = (e.hi | (127 & b) << 7 * t + 3) >>> 0;
          if (b < 128) return e;
        }
      } else
        for (; t < 5; ++t) {
          if (buf.position() >= buf.array().length) break;
          b = buf.get() & 0xFF;
          e.hi = (e.hi | (127 & b) << 7 * t + 3) >>> 0;
          if (b < 128) return e;
        }
      // TODO Error
      return e;
    }

    private class Uint64 {
      public long lo = 0;
      public long hi = 0;
    }

    private byte[] bytes(ByteBuffer buf) {
      long e = uint32(buf);
      long t = buf.position();
      long o = t + e;
      if (o > buf.array().length) return null;
      byte[] b = new byte[(int) e];
      for (int i = 0; i < e; i++) {
        b[i] = buf.get();
      }
      return b;
    }

    private String readString(ByteBuffer buf) {
      byte[] e = bytes(buf);
      if (e == null || e.length <= 0) return "";
      List<Long> s = new ArrayList();
      StringBuilder i = new StringBuilder();
      int t = 0;
      int o = e.length;
      long r;
      for (; t < o; ) {
        r = e[t++] & 0xFF;
        if (r < 128) {
          s.add(r);
        } else if (r > 191 && r < 224) {
          s.add((31 & r) << 6 | 63 & (e[t++] & 0xFF));
        } else if (r > 239 && r < 365) {
          r =
              ((7 & r) << 18
                      | (63 & (e[t++] & 0xFF)) << 12
                      | (63 & (e[t++] & 0xFF)) << 6
                      | 63 & (e[t++] & 0xFF))
                  - 65536;
          s.add(55296 + (r >> 10));
          s.add(56320 + (1023 & r));
        } else {
          s.add((15 & r) << 12 | (63 & (e[t++] & 0xFF)) << 6 | 63 & (e[t++] & 0xFF));
          // n > 8191;
          for (long l : s) {
            String str = fromCharCode((int) l);
            i.append(str);
          }
          s = new ArrayList();
        }
      }

      if (i.length() == 0 || s.size() > 0) {
        for (long l : s) {
          String str = fromCharCode((int) l);
          i.append(str);
        }
      }

      return i.toString();
    }

    private Proto parseProto(byte[] e) {
      int o = e.length;
      if (o <= 2) return null;
      int r = 0;
      long i = (long) (256 * (e[r] & 0xFF)) + (long) (e[r + 1] & 0xFF);
      r += 2;
      o -= 2;
      long s = 0;
      if (32768 == i) {
        if (o <= 4) return null;
        s =
            (long) (256 * (e[r] & 0xFF) * 256 * 256)
                + (long) (256 * (e[r + 1] & 0xFF) * 256)
                + (long) (256 * (e[r + 2] & 0xFF))
                + (long) ((e[r + 3] & 0xFF));
        r += 4;
        o -= 4;
      } else {
        s = i;
      }
      if (s < 7 || Integer.compareUnsigned((int) s, 0x80000000) >= 0) return null;
      if (o < s) return null;
      long n = (long) (256 * (e[r] & 0xFF)) + (long) (e[r + 1] & 0xFF);
      o -= 2;
      r += 2; // TODO
      o -= 1;
      byte[] a = new byte[e.length - 4 - r - 1];
      for (int p = ++r; p < e.length - 4; p++) a[p - r] = e[p];
      return new Proto(n, a);
    }
  }

  private class Proto {
    public long type;
    public byte[] raw;

    public Proto(long type, byte[] raw) {
      this.type = type;
      this.raw = raw;
      ByteBuffer.wrap(raw);
    }
  }

  public static String byteArrayToHexString(byte[] a) {
    if (a == null) return "null";
    int iMax = a.length - 1;
    if (iMax == -1) return "[]";

    StringBuilder b = new StringBuilder();
    b.append('[');
    for (int i = 0; ; i++) {
      b.append(String.format((a[i] & 0xFF) < 16 ? "0x0%X" : "0x%X", a[i]));
      if (i == iMax) return b.append(']').toString();
      b.append(", ");
    }
  }

  public static byte[] hexStringToByteArray(String s) {
    int len = s.length();
    byte[] data = new byte[len / 2];
    for (int i = 0; i < len; i += 2) {
      data[i / 2] =
          (byte) ((Character.digit(s.charAt(i), 16) << 4) + Character.digit(s.charAt(i + 1), 16));
    }
    return data;
  }

  public static String fromCharCode(int... codePoints) {
    return new String(codePoints, 0, codePoints.length);
  }

  public Map<String, List<String>> splitQuery(URI uri) {
    if (uri.getQuery() == null) {
      return Collections.emptyMap();
    }
    return Arrays.stream(uri.getQuery().split("&"))
        .map(this::splitQueryParameter)
        .collect(
            Collectors.groupingBy(
                SimpleImmutableEntry::getKey,
                LinkedHashMap::new,
                Collectors.mapping(Map.Entry::getValue, Collectors.toList())));
  }

  public SimpleImmutableEntry<String, String> splitQueryParameter(String it) {
    final int idx = it.indexOf("=");
    final String key = idx > 0 ? it.substring(0, idx) : it;
    final String value = idx > 0 && it.length() > idx + 1 ? it.substring(idx + 1) : null;
    return new SimpleImmutableEntry<>(key, value);
  }

  public void req2(boolean clear) throws URISyntaxException {
    if (clear) Lizzie.board.clearForOnline();
    if (sio != null) {
      sio.close();
    }
    seqs = 0;
    URI uri = new URI(new String(c1));
    sio = IO.socket(uri);
    sio.on(
            Socket.EVENT_CONNECT,
            new Emitter.Listener() {
              @Override
              public void call(Object... args) {
                // System.out.println("io:connect");
                login();
              }
            })
        .on(
            Socket.EVENT_MESSAGE,
            new Emitter.Listener() {
              @Override
              public void call(Object... args) {
                // System.out.println("io:message");
              }
            })
        .on(
            Socket.EVENT_DISCONNECT,
            new Emitter.Listener() {
              @Override
              public void call(Object... args) {
                // System.out.println("io:disconnect");
              }
            })
        .on(
            Socket.EVENT_ERROR,
            new Emitter.Listener() {
              @Override
              public void call(Object... args) {
                // System.out.println("io:error");
              }
            })
        .on(
            Socket.EVENT_PING,
            new Emitter.Listener() {
              @Override
              public void call(Object... args) {
                // System.out.println("io:ping");
              }
            })
        .on(
            Socket.EVENT_PONG,
            new Emitter.Listener() {
              @Override
              public void call(Object... args) {
                // System.out.println("io:pong");
              }
            })
        .on(
            Socket.EVENT_CONNECT_ERROR,
            new Emitter.Listener() {
              @Override
              public void call(Object... args) {
                // System.out.println("io:EVENT_CONNECT_ERROR");
              }
            })
        .on(
            Socket.EVENT_CONNECT_TIMEOUT,
            new Emitter.Listener() {
              @Override
              public void call(Object... args) {
                // System.out.println("io:EVENT_CONNECT_TIMEOUT");
              }
            })
        .on(
            Socket.EVENT_CONNECTING,
            new Emitter.Listener() {
              @Override
              public void call(Object... args) {
                // System.out.println("io:EVENT_CONNECTING");
              }
            })
        .on(
            Socket.EVENT_RECONNECT,
            new Emitter.Listener() {
              @Override
              public void call(Object... args) {
                // System.out.println("io:EVENT_RECONNECT");
              }
            })
        .on(
            Socket.EVENT_RECONNECT_ATTEMPT,
            new Emitter.Listener() {
              @Override
              public void call(Object... args) {
                // System.out.println("io:EVENT_RECONNECT_ATTEMPT");
              }
            })
        .on(
            Socket.EVENT_RECONNECT_FAILED,
            new Emitter.Listener() {
              @Override
              public void call(Object... args) {
                // System.out.println("io:EVENT_RECONNECT_FAILED");
              }
            })
        .on(
            Socket.EVENT_RECONNECT_ERROR,
            new Emitter.Listener() {
              @Override
              public void call(Object... args) {
                // System.out.println("io:EVENT_RECONNECT_ERROR");
              }
            })
        .on(
            Socket.EVENT_RECONNECTING,
            new Emitter.Listener() {
              @Override
              public void call(Object... args) {
                // System.out.println("io:EVENT_RECONNECTING");
              }
            })
        .on(
            "heartbeat",
            new Emitter.Listener() {
              @Override
              public void call(Object... args) {
                // System.out.println("io:heartbeat:" + strJson(args));
              }
            })
        .on(
            "userinfo",
            new Emitter.Listener() {
              @Override
              public void call(Object... args) {
                // System.out.println("io:userinfo:" + strJson(args));
                // System.out.println(
                // "io:userinfo:userid:"
                // + (args == null || args.length < 1
                // ? ""
                // : ((JSONObject) args[0]).opt("user_id").toString()));
                userId =
                    (args == null || args.length < 1
                        ? userId
                        : ((JSONObject) args[0]).optLong("user_id"));
                entry();
              }
            })
        .on(
            "init",
            new Emitter.Listener() {
              @Override
              public void call(Object... args) {
                // System.out.println("io:init:" + strJson(args));
                initData(args == null || args.length < 1 ? null : ((JSONObject) args[0]));
              }
            })
        .on(
            "move",
            new Emitter.Listener() {
              @Override
              public void call(Object... args) {
                // System.out.println("io:move:" + strJson(args));
                move(args == null || args.length < 1 ? null : (JSONObject) args[0]);
                sync();
                if (shouldMoveForward) {
                  shouldMoveForward = false;
                  Lizzie.frame.lastMove();
                }
              }
            })
        .on(
            "update_game",
            new Emitter.Listener() {
              @Override
              public void call(Object... args) {
                // System.out.println("io:update_game:" + strJson(args));
                updateGame(args == null || args.length < 1 ? null : (JSONObject) args[0]);
              }
            })
        .on(
            "move_delete",
            new Emitter.Listener() {
              @Override
              public void call(Object... args) {
                // System.out.println("io:move_delete:" + strJson(args));
                moveDelete();
                // sync();
              }
            })
        .on(
            "comments",
            new Emitter.Listener() {
              @Override
              public void call(Object... args) {
                // System.out.println("io:comments:" + strJson(args));
                procComments(args == null || args.length < 1 ? null : (JSONObject) args[0]);
                sync();
              }
            })
        .on(
            "notice",
            new Emitter.Listener() {
              @Override
              public void call(Object... args) {
                // System.out.println("io:notice:" + strJson(args));
              }
            });
    sio.connect();
  }

  private void login() {
    JSONObject data = new JSONObject();
    data.put("hall", "1");
    data.put("room", roomId);
    data.put("token", -1);
    data.put("user_id", userId);
    data.put("platform", 3);
    sendData(
        "login",
        data,
        new Ack() {
          @Override
          public void call(Object... args) {
            // entry();
          }
        });
  }

  private void entry() {
    JSONObject data = new JSONObject();
    data.put("hall", "1");
    data.put("room", roomId);
    data.put("platform", 3);
    data.put("user_id", userId);
    sendData(
        "entry_room",
        data,
        new Ack() {
          @Override
          public void call(Object... args) {
            channel();
          }
        });
  }

  private void initData(JSONObject data) {
    if (data == null) return;
    JSONObject info = data.optJSONObject("game_info");
    int size = info.optInt("boardSize", 19);
    boardSize = size;
    Lizzie.board.reopen(boardSize, boardSize);
    history = new BoardHistoryList(BoardData.empty(size, size)); // TODO boardSize
    blackPlayer = info.optString("blackName");
    whitePlayer = info.optString("whiteName");
    boolean isEnd = !Utils.isBlank(info.optString("resultDesc"));
    history = SGFParser.parseSgf(info.optString("sgf"), true);
    if (history != null) {
      double komi = info.optDouble("komi", history.getGameInfo().getKomi());
      int handicap = info.optInt("handicap", history.getGameInfo().getHandicap());
      if (Lizzie.config.readKomi) {
        Lizzie.board.getHistory().getGameInfo().setKomi(komi);
        Lizzie.leelaz.komi(komi);
      }
      Lizzie.board.getHistory().getGameInfo().setHandicap(handicap);
      int diffMove = Lizzie.board.getHistory().sync(history);
      if (diffMove >= 0) {
        //      Lizzie.board.goToMoveNumberBeyondBranch(diffMove > 0 ? diffMove - 1 : 0);
        //      while (Lizzie.board.nextMove()) ;
      }
      if ("3".equals(info.optString("status"))) {
        //   sio.close();
        String result = info.optString("resultDesc");
        if (!Utils.isBlank(result)) {
          Lizzie.board.getHistory().getEnd().getData().comment =
              result + "\n" + Lizzie.board.getHistory().getEnd().getData().comment;
          Lizzie.frame.setResult(result);
          Lizzie.board.getHistory().getGameInfo().setResult(result);
          Lizzie.board.previousMove(false);
          Lizzie.board.nextMove(true);
        }
      }
    }
    if (history == null) {
      //      error(true);
      sio.close();
      if (isEnd && type == 1) {
        try {
          refresh("(?s).*?(\\\"Content\\\":\\\")(.+)(\\\",\\\")(?s).*", 2, false, false);
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }
    Lizzie.frame.setPlayers(whitePlayer, blackPlayer);
    Lizzie.board.getHistory().getGameInfo().setPlayerBlack(blackPlayer);
    Lizzie.board.getHistory().getGameInfo().setPlayerWhite(whitePlayer);
    Lizzie.frame.renderVarTree(0, 0, false, true);
  }

  private void channel() {
    JSONObject data = new JSONObject();
    data.put("hall", "1");
    data.put("room", roomId);
    data.put("platform", 3);
    data.put("channel", "chat_1_" + roomId); // channel);
    sendData(
        "channel/add",
        data,
        new Ack() {
          @Override
          public void call(Object... args) {
            listen();
          }
        });
  }

  private void listen() {
    JSONObject data = new JSONObject();
    data.put("hall", "1");
    data.put("room", roomId);
    data.put("platform", 3);
    sendData(
        "comment/listen",
        data,
        new Ack() {
          @Override
          public void call(Object... args) {
            // System.out.println(
            // "listen callback:"
            // + (args == null || args.length <= 0 ? "null" :
            // args[0].toString()));
            listenBack(args == null || args.length <= 0 ? null : (JSONArray) args[0]);
          }
        });
  }

  private void listenBack(JSONArray data) {
    if (data == null) return;
    for (Object o : data) {
      JSONObject d = (JSONObject) o;
      switch (d.optInt("type", 0)) {
        case 1:
          addBranch(d);
          break;
        case 2:
          try {
            addComment(d, true);
          } catch (Exception ex) {
          }
          break;
      }
    }
    sync();
  }

  void sync() {
    while (history.previous().isPresent()) ;
    Lizzie.board.getHistory().sync(history);
  }

  private void procComments(JSONObject cb) {
    if (cb == null) return;
    String type = cb.optString("type", "");
    if ("add".equals(type) || "update".equals(type)) {
      // TODO
      JSONObject d = ((JSONObject) cb.opt("content"));
      switch (d.optInt("type", 0)) {
        case 1:
          addBranch(d);
          break;
        case 2:
          addComment(d, "add".equals(type));
          break;
      }
    }
  }

  private void addBranch(JSONObject branch) {
    if (branch == null) return;
    int move = branch.optInt("handsCount");
    int id = branch.optInt("id");
    Map<Integer, JSONObject> b = null;
    if (!branchs.containsKey(move)) {
      b = new HashMap<Integer, JSONObject>();
      branchs.put(move, b);
    } else {
      b = branchs.get(move);
    }
    if (!b.containsKey(id)) {
      b.put(id, branch);
      addBranch(move, branch.optString("content"));
    } else {
      // TODO update
    }
  }

  private int addBranch(int move, String sgf) {
    int subIndex = -1;
    if (!Utils.isBlank(sgf)) {
      if (move > 0) {
        history.goToMoveNumber(move, false);
        if (history.getCurrentHistoryNode().numberOfChildren() == 0) {
          Stone color = history.getLastMoveColor() == Stone.WHITE ? Stone.BLACK : Stone.WHITE;
          history.pass(color, false, true);
          history.previous();
        }
        subIndex = SGFParser.parseBranch(history, sgf);
        while (history.next(true).isPresent()) ;
      }
    }
    return subIndex;
  }

  private void addComment(JSONObject c, boolean add) {
    if (c == null) return;
    JSONObject extend = new JSONObject(c.optString("extend"));
    String member = extend == null ? "" : extend.optString("LiveMember");
    String content = c.optString("content");
    int move = c.optInt("handsCount");
    int id = c.optInt("id");
    Map<Integer, JSONObject> b = null;
    if (!comments.containsKey(move)) {
      b = new HashMap<Integer, JSONObject>();
      comments.put(move, b);
    } else {
      b = comments.get(move);
    }
    // if (!b.containsKey(id)) {
    b.put(id, c);
    // }
    addComment(move, Utils.isBlank(member) ? content : member + "：" + content, add);
  }

  private void addComment(int move, String comment, boolean add) {
    if (!Utils.isBlank(comment)) {
      history.goToMoveNumber(move, false);
      if (add) {
        history.getData().comment += comment + "\n";
      } else {
        history.getData().comment = comment + "\n";
      }
      while (history.next(true).isPresent()) ;
    }
  }

  private void move(JSONObject d) {
    if (d == null || d.opt("move") == null) return;
    JSONObject m = (JSONObject) d.get("move");
    int move = m.optInt("mcnt");
    if (move > 0) {
      int[] c = new int[2];
      c[0] = m.optInt("x");
      c[1] = m.optInt("y");
      boolean changeMove = false;
      while (history.next(true).isPresent()) ;
      Stone color = (m.optInt("c") == -1) ? Stone.WHITE : Stone.BLACK;
      //      Stone color =
      //          (move - history.getData().moveNumber) % 2 == 0
      //              ? history.getLastMoveColor()
      //              : (history.getLastMoveColor() == Stone.WHITE ? Stone.BLACK : Stone.WHITE);
      //      //    Stone color = (m.optInt("c") == -1) ? Stone.WHITE : Stone.BLACK;
      if (move <= history.getMoveNumber()) {
        int cur = history.getMoveNumber();
        for (int i = move; i <= cur; i++) {
          BoardHistoryNode currentNode = history.getCurrentHistoryNode();
          boolean isSameMove = (i == cur && currentNode.getData().isSameCoord(c));
          if (currentNode.previous().isPresent()) {
            BoardHistoryNode pre = currentNode.previous().get();
            history.previous();
            if (pre.numberOfChildren() <= 1 && !isSameMove) {
              int idx = pre.indexOfNode(currentNode);
              pre.deleteChild(idx);
              changeMove = false;
            } else {
              changeMove = true;
            }
          }
        }
      }

      if (c == null || !Board.isValid(c)) {
        history.pass(color, false, false);
      } else {

        history.place(c[0], c[1], color, false, changeMove);
      }

      sync();
    }
  }

  public void moveDelete() {
    if (LizzieFrame.urlSgf) {
      if (client != null && client.isOpen()) {
        client.close();
        client = null;
      }
    }
    type = checkUrl();
    isStoped = false;
    chineseRule = 1;
    chineseFlag = false;
    LizzieFrame.urlSgf = true;
    Lizzie.frame.setCommentPaneOrArea(false);
    if (type > 0) {
      error(false);
      setVisible(false);
      try {
        Lizzie.frame.setResult("");
        proc();
      } catch (IOException | URISyntaxException e) {
        e.printStackTrace();
      }
    } else {
      error(true);
    }
    Lizzie.frame.syncLiveBoardStat();
    //   sync();
    // Lizzie.board.moveToAnyPosition(Lizzie.board.getHistory().getMainEnd());
    // Lizzie.board.deleteMove();
    //  shouldMoveForward = true;
  }

  private void updateGame(JSONObject g) {
    if (g == null) return;
    int status = g.optInt("status");
    if (status == 3) {
      sio.close();
      String result = g.optString("resultDesc");
      if (!Utils.isBlank(result)) {
        while (Lizzie.board.getHistory().next().isPresent()) ;
        Lizzie.board.getHistory().getEnd().getData().comment =
            result + "\n" + Lizzie.board.getHistory().getEnd().getData().comment;
        Lizzie.frame.setResult(result);
        Lizzie.board.getHistory().getGameInfo().setResult(result);
        Lizzie.board.previousMove(false);
        Lizzie.board.nextMove(true);
      }
    }
  }

  private void sendData(String id, JSONObject data, final Ack ack) { // callback i
    if (data == null) data = new JSONObject();
    sio.emit(
        id,
        data,
        new Ack() {
          @Override
          public void call(Object... args) {
            Object t = null;
            if (args != null && args.length > 0) {
              JSONObject e = (JSONObject) args[0];
              switch ((int) e.get("code")) {
                case 0:
                  if (e.opt("data") instanceof JSONArray) {
                    t = (JSONArray) e.opt("data");
                  } else {
                    t = (JSONObject) e.opt("data");
                  }
                  break;
                case 1:
                case 2:
                  if (e.opt("message") instanceof JSONArray) {
                    t = (JSONArray) e.opt("message");
                  } else {
                    t = (JSONObject) e.opt("message");
                  }
                  break;
                case 10:
                  sio.disconnect();
              }
            }
            if (ack != null) {
              if (t == null) {
                ack.call();
              } else {
                ack.call(t);
              }
            }
          }
        });
  }

  public void stopSync() {
    LizzieFrame.urlSgf = false;
    Lizzie.frame.setCommentPaneOrArea(true);
    isStoped = true;
    //    if (client != null && client.isOpen()) {
    //      client.close();
    //      client = null;
    //    }
    txtUrl.setText("");
    checkUrl();
    //  type = 1;
    //    try {
    //      procNoClear();
    //    } catch (IOException e) {
    //      // TODO Auto-generated catch block
    //      e.printStackTrace();
    //    } catch (URISyntaxException e) {
    //      // TODO Auto-generated catch block
    //      e.printStackTrace();
    //    }
    if (sio != null) {
      sio.close();
    }
    setVisible(false);
    //  Lizzie.frame.onlineDialog.dispose();
  }

  public void applyChangeWeb(String url) {
    //
    isStoped = false;
    fromBrowser = true;
    firstTime = true;
    txtUrl.setText(url);
    type = checkUrl();
    LizzieFrame.urlSgf = true;
    Lizzie.frame.setCommentPaneOrArea(false);
    if (type > 0) {
      setVisible(false);
      try {
        Lizzie.frame.setResult("");
        proc();
      } catch (IOException | URISyntaxException e) {
        e.printStackTrace();
      }
    } else {
      // error(true);
    }
  }

  public static void main(String[] args) {
    EventQueue.invokeLater(
        () -> {
          try {
            OnlineDialog window = new OnlineDialog();
            window.setVisible(true);
          } catch (Exception e) {
            e.printStackTrace();
          }
        });
  }
}
