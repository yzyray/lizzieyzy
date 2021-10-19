package featurecat.lizzie.gui;

import featurecat.lizzie.Lizzie;
import featurecat.lizzie.analysis.GetFoxRequest;
import featurecat.lizzie.rules.SGFParser;
import featurecat.lizzie.util.Utils;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.AbstractCellEditor;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class FoxKifuDownload extends JFrame {
  private DefaultTableModel model;
  private JTable table;
  private JTextField txtUserName;
  public GetFoxRequest foxReq;
  private List<KifuInfo> foxKifuInfos;
  private int myUid;
  private int tabNumber = 1;
  private int numbersPerTab = 25;
  private int curTabNumber = 1;
  private boolean isComplete=false;
  JLabel lblTab;
  private ArrayList<String[]> rows;

  public FoxKifuDownload() {
    setSize(new Dimension(1000, 550));
    setTitle("棋谱查询");

    try {
      this.setIconImage(ImageIO.read(MoreEngines.class.getResourceAsStream("/assets/logo.png")));
    } catch (IOException e1) {
      // TODO Auto-generated catch block
      e1.printStackTrace();
    }
    setAlwaysOnTop(Lizzie.frame.isAlwaysOnTop());
    setLocationRelativeTo(Lizzie.frame);

    JPanel panel = new JPanel();
    getContentPane().add(panel, BorderLayout.NORTH);

    JLabel lblUserName = new JFontLabel("用户名:");
    panel.add(lblUserName);

    txtUserName = new JFontTextField();
    panel.add(txtUserName);
    txtUserName.setColumns(10);
    txtUserName.addKeyListener(
        new KeyAdapter() {
          public void keyPressed(KeyEvent e) {
            if (e.getKeyCode() == KeyEvent.VK_ENTER) {
              getFoxKifus();
            }
          }
        });

    JButton btnSearch = new JFontButton("搜索");
    btnSearch.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            getFoxKifus();
          }
        });
    panel.add(btnSearch);

    JPanel buttonPane = new JPanel();

    table = new JTable();
    TableCellRenderer tcr = new ColorTableCellRenderer();
    table.setDefaultRenderer(Object.class, tcr);
    JScrollPane scrollPane = new JScrollPane(table);
    getContentPane().add(scrollPane, BorderLayout.CENTER);
    getContentPane().add(buttonPane, BorderLayout.SOUTH);

    JButton btnFirst = new JButton("|<");
    btnFirst.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            if (rows == null) return;
            curTabNumber = 1;
            while (model.getRowCount() > 0) {
              model.removeRow(model.getRowCount() - 1);
            }
            for (int i = (curTabNumber - 1) * numbersPerTab;
                i < numbersPerTab && i < rows.size();
                i++) {
              model.addRow(rows.get(i));
            }
            setLblTab(1);
            //lblTab.setText(curTabNumber + "/" + tabNumber);
          }
        });
    buttonPane.add(btnFirst);

    JButton btnPrevious = new JButton("<");
    btnPrevious.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            if (rows == null) return;
            if (curTabNumber == 1) return;
            curTabNumber = curTabNumber - 1;
            while (model.getRowCount() > 0) {
              model.removeRow(model.getRowCount() - 1);
            }
            for (int i = (curTabNumber - 1) * numbersPerTab;
                i < curTabNumber * numbersPerTab && i < rows.size();
                i++) {
              model.addRow(rows.get(i));
            }
            setLblTab(curTabNumber);
            //lblTab.setText(curTabNumber + "/" + tabNumber);
          }
        });
    buttonPane.add(btnPrevious);

    JButton btnNext = new JButton(">");
    btnNext.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            if (rows == null) return;
            if (curTabNumber == tabNumber) return;
            curTabNumber = curTabNumber + 1;
            while (model.getRowCount() > 0) {
              model.removeRow(model.getRowCount() - 1);
            }
            for (int i = (curTabNumber - 1) * numbersPerTab;
                i < curTabNumber * numbersPerTab && i < rows.size();
                i++) {
              model.addRow(rows.get(i));
            }
            setLblTab(curTabNumber);
            maybeGetNextPage();
           // lblTab.setText(curTabNumber + "/" + tabNumber);
          }
        });
    buttonPane.add(btnNext);

    JButton btnLast = new JButton(">|");
    btnLast.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            if (rows == null) return;
            if (curTabNumber == tabNumber) return;
            curTabNumber = tabNumber;
            while (model.getRowCount() > 0) {
              model.removeRow(model.getRowCount() - 1);
            }
            for (int i = (curTabNumber - 1) * numbersPerTab;
                i < curTabNumber * numbersPerTab && i < rows.size();
                i++) {
              model.addRow(rows.get(i));
            }
            setLblTab(curTabNumber);
            maybeGetNextPage();
            //lblTab.setText(curTabNumber + "/" + tabNumber);
          }
        });
    buttonPane.add(btnLast);

    lblTab = new JLabel("1/1");
    buttonPane.add(lblTab);
  }

  private void maybeGetNextPage() {
	// TODO Auto-generated method stub
	if(this.curTabNumber==this.tabNumber) {
		this.foxReq.sendCommand("uid "+this.myUid+" "+this.foxKifuInfos.get(foxKifuInfos.size()-1).chessid);
	}
}

private void getFoxKifus() {
    // TODO Auto-generated method stub
    foxReq = new GetFoxRequest(this);
    rows = new ArrayList<String[]>();
    foxReq.sendCommand("user_name " + txtUserName.getText());
  }

  public void receiveResult(String string) {
    // TODO Auto-generated method stub
    try {
      //	string="{\"result\":0,\"resultstr\":\"\",\"uin\":0,\"ret\":0,\"srcuid\":0,\"dstuid\":0,\"type\":4,\"lastCode\":0,\"searchkey\":\"\",\"chesslist\":[{\"chessid\":\"1618556331030021043\",\"blackuid\":28336178,\"blacknick\":\"V283361785\",\"blackenname\":\"V283361785\",\"blackdan\":24,\"blackcountry\":86,\"whiteuid\":13041,\"whitenick\":\"0小肥羊0\",\"whiteenname\":\"yzyray\",\"whitedan\":24,\"whitecountry\":86,\"title\":\"\",\"gamestarttime\":1618556331,\"gameendtime\":1618557552,\"winner\":2,\"point\":-1,\"reason\":3,\"movenum\":256,\"boardsize\":19,\"handicap\":0,\"firstcolor\":1,\"komi\":375,\"clienttype\":2,\"commenttype\":0,\"additionalrule\":0,\"rule\":1,\"blackocc\":0,\"whiteocc\":0,\"starttime\":\"2021-04-16 14:58:51\",\"endtime\":\"2021-04-16 15:19:12\",\"introduction\":\"\",\"gametype\":1,\"favorite\":0},{\"chessid\":\"1618555056030001393\",\"blackuid\":13041,\"blacknick\":\"0小肥羊0\",\"blackenname\":\"yzyray\",\"blackdan\":24,\"blackcountry\":86,\"whiteuid\":7559043,\"whitenick\":\"火速王者\",\"whiteenname\":\"火速王者\",\"whitedan\":24,\"whitecountry\":86,\"title\":\"\",\"gamestarttime\":1618555056,\"gameendtime\":1618556244,\"winner\":1,\"point\":-1,\"reason\":3,\"movenum\":211,\"boardsize\":19,\"handicap\":0,\"firstcolor\":1,\"komi\":375,\"clienttype\":2,\"commenttype\":0,\"additionalrule\":0,\"rule\":1,\"blackocc\":0,\"whiteocc\":0,\"starttime\":\"2021-04-16 14:37:36\",\"endtime\":\"2021-04-16 14:57:24\",\"introduction\":\"\",\"gametype\":1,\"favorite\":0},{\"chessid\":\"1618553326030002960\",\"blackuid\":21658341,\"blacknick\":\"金老师6180\",\"blackenname\":\"金老师6180\",\"blackdan\":24,\"blackcountry\":86,\"whiteuid\":13041,\"whitenick\":\"0小肥羊0\",\"whiteenname\":\"yzyray\",\"whitedan\":24,\"whitecountry\":86,\"title\":\"\",\"gamestarttime\":1618553326,\"gameendtime\":1618554986,\"winner\":1,\"point\":-1,\"reason\":3,\"movenum\":221,\"boardsize\":19,\"handicap\":0,\"firstcolor\":1,\"komi\":375,\"clienttype\":2,\"commenttype\":0,\"additionalrule\":0,\"rule\":1,\"blackocc\":0,\"whiteocc\":0,\"starttime\":\"2021-04-16 14:08:46\",\"endtime\":\"2021-04-16 14:36:26\",\"introduction\":\"\",\"gametype\":1,\"favorite\":0},{\"chessid\":\"1618552169030041760\",\"blackuid\":13041,\"blacknick\":\"0小肥羊0\",\"blackenname\":\"yzyray\",\"blackdan\":24,\"blackcountry\":86,\"whiteuid\":308324,\"whitenick\":\"dazhua\",\"whiteenname\":\"dazhua\",\"whitedan\":24,\"whitecountry\":86,\"title\":\"\",\"gamestarttime\":1618552169,\"gameendtime\":1618553092,\"winner\":2,\"point\":-1,\"reason\":3,\"movenum\":123,\"boardsize\":19,\"handicap\":0,\"firstcolor\":1,\"komi\":375,\"clienttype\":2,\"commenttype\":0,\"additionalrule\":0,\"rule\":1,\"blackocc\":0,\"whiteocc\":0,\"starttime\":\"2021-04-16 13:49:29\",\"endtime\":\"2021-04-16 14:04:52\",\"introduction\":\"\",\"gametype\":1,\"favorite\":0},{\"chessid\":\"1618043705030011393\",\"blackuid\":13041,\"blacknick\":\"0小肥羊0\",\"blackenname\":\"yzyray\",\"blackdan\":24,\"blackcountry\":86,\"whiteuid\":24611925,\"whitenick\":\"和路雪63\",\"whiteenname\":\"和路雪63\",\"whitedan\":24,\"whitecountry\":86,\"title\":\"\",\"gamestarttime\":1618043705,\"gameendtime\":1618046331,\"winner\":1,\"point\":-2,\"reason\":2,\"movenum\":221,\"boardsize\":19,\"handicap\":0,\"firstcolor\":1,\"komi\":375,\"clienttype\":2,\"commenttype\":0,\"additionalrule\":0,\"rule\":1,\"blackocc\":0,\"whiteocc\":0,\"starttime\":\"2021-04-10 16:35:05\",\"endtime\":\"2021-04-10 17:18:51\",\"introduction\":\"\",\"gametype\":1,\"favorite\":0},{\"chessid\":\"1618038603030021119\",\"blackuid\":13041,\"blacknick\":\"0小肥羊0\",\"blackenname\":\"yzyray\",\"blackdan\":24,\"blackcountry\":86,\"whiteuid\":7688993,\"whitenick\":\"7225yc\",\"whiteenname\":\"7225yc\",\"whitedan\":24,\"whitecountry\":86,\"title\":\"\",\"gamestarttime\":1618038603,\"gameendtime\":1618040709,\"winner\":1,\"point\":-1,\"reason\":3,\"movenum\":203,\"boardsize\":19,\"handicap\":0,\"firstcolor\":1,\"komi\":375,\"clienttype\":2,\"commenttype\":0,\"additionalrule\":0,\"rule\":1,\"blackocc\":0,\"whiteocc\":0,\"starttime\":\"2021-04-10 15:10:03\",\"endtime\":\"2021-04-10 15:45:09\",\"introduction\":\"\",\"gametype\":1,\"favorite\":0},{\"chessid\":\"1618035399030032304\",\"blackuid\":13041,\"blacknick\":\"0小肥羊0\",\"blackenname\":\"yzyray\",\"blackdan\":24,\"blackcountry\":86,\"whiteuid\":1174686,\"whitenick\":\"静夜思7772\",\"whiteenname\":\"静夜思7772\",\"whitedan\":24,\"whitecountry\":86,\"title\":\"\",\"gamestarttime\":1618035399,\"gameendtime\":1618036085,\"winner\":1,\"point\":-2,\"reason\":2,\"movenum\":83,\"boardsize\":19,\"handicap\":0,\"firstcolor\":1,\"komi\":375,\"clienttype\":2,\"commenttype\":0,\"additionalrule\":0,\"rule\":1,\"blackocc\":0,\"whiteocc\":0,\"starttime\":\"2021-04-10 14:16:39\",\"endtime\":\"2021-04-10 14:28:05\",\"introduction\":\"\",\"gametype\":1,\"favorite\":0},{\"chessid\":\"1618031704030053734\",\"blackuid\":20154746,\"blacknick\":\"似狗非狗\",\"blackenname\":\"似狗非狗\",\"blackdan\":24,\"blackcountry\":86,\"whiteuid\":13041,\"whitenick\":\"0小肥羊0\",\"whiteenname\":\"yzyray\",\"whitedan\":24,\"whitecountry\":86,\"title\":\"\",\"gamestarttime\":1618031704,\"gameendtime\":1618034480,\"winner\":2,\"point\":-2,\"reason\":2,\"movenum\":280,\"boardsize\":19,\"handicap\":0,\"firstcolor\":1,\"komi\":375,\"clienttype\":2,\"commenttype\":0,\"additionalrule\":0,\"rule\":1,\"blackocc\":0,\"whiteocc\":0,\"starttime\":\"2021-04-10 13:15:04\",\"endtime\":\"2021-04-10 14:01:20\",\"introduction\":\"\",\"gametype\":1,\"favorite\":0}]}";
      JSONObject jsonOjbect = new JSONObject(string);
      if (jsonOjbect.has("uid")) {
        myUid = jsonOjbect.getInt("uid");
        foxReq.sendCommand("uid " + myUid);
      }
      if (jsonOjbect.has("chesslist")) {
        JSONArray jsonArray = jsonOjbect.getJSONArray("chesslist");
        if(jsonArray.len)
        foxKifuInfos = new ArrayList<KifuInfo>();
        isComplete=jsonArray.length()<100;
        for (int i = 0; i < jsonArray.length(); i++) {
          JSONObject jsonObject = jsonArray.getJSONObject(i);
          KifuInfo kifuInfo = new KifuInfo();
          kifuInfo.index = i + 1;
          kifuInfo.playTime = jsonObject.getString("starttime");
          kifuInfo.blackName =
              jsonObject.optString("blacknick", jsonObject.getString("blackenname"));
          int bRank = jsonObject.getInt("blackdan") - 17;
          kifuInfo.blackRank = bRank > 0 ? bRank + "段" : (Math.abs(bRank) + 1) + "级";
          kifuInfo.whiteName =
              jsonObject.optString("whitenick", jsonObject.getString("whiteenname"));
          int wRank = jsonObject.getInt("whitedan") - 17;
          kifuInfo.whiteRank = wRank > 0 ? wRank + "段" : (Math.abs(wRank) + 1) + "级";
          kifuInfo.chessid = jsonObject.getString("chessid");
          // kifuInfo.result=
          kifuInfo.totalMoves = jsonObject.getInt("movenum");
          String result = "";
          int winner = jsonObject.getInt("winner");
          int point = jsonObject.getInt("point");
          int rule = jsonObject.getInt("rule");
          if (winner == 1 || winner == 2) {
            if (winner == 1) {
              result = "黑";
              if (jsonObject.getInt("blackuid") == myUid) kifuInfo.isWin = true;
              else kifuInfo.isWin = false;
            } else {
              result = "白";
              if (jsonObject.getInt("whiteuid") == myUid) kifuInfo.isWin = true;
              else kifuInfo.isWin = false;
            }
            if (point < 0) {
              if (point == -1) result += "中盘胜";
              else if (point == -2) result += "超时胜";
              else result += "胜";
            } else {
              String unit = "";
              if (rule == 1) unit = "子";
              if (rule == 0) unit = "目";
              result += "胜" + point / 100f + unit;
            }
          } else result = "其他";
          kifuInfo.result = result;
          foxKifuInfos.add(kifuInfo);
        }
        if (foxKifuInfos.size() >= 1) {
          model = new DefaultTableModel();
          table.setModel(model);
          model.addColumn("序号");
          model.addColumn("时间");
          model.addColumn("黑方");
          model.addColumn("等级");
          model.addColumn("白方");
          model.addColumn("等级");
          model.addColumn("结果");
          model.addColumn("手数");
          model.addColumn("打开");
          model.addColumn("");
          model.addColumn("");
          table.getColumnModel().getColumn(0).setPreferredWidth(35);
          table.getColumnModel().getColumn(1).setPreferredWidth(130);
          table.getColumnModel().getColumn(2).setPreferredWidth(70);
          table.getColumnModel().getColumn(3).setPreferredWidth(40);
          table.getColumnModel().getColumn(4).setPreferredWidth(70);
          table.getColumnModel().getColumn(5).setPreferredWidth(40);
          table.getColumnModel().getColumn(6).setPreferredWidth(70);
          table.getColumnModel().getColumn(7).setPreferredWidth(50);
          table.getColumnModel().getColumn(8).setPreferredWidth(40);
          table.getColumnModel().getColumn(8).setCellEditor(new MyButtonOpenFoxKifuEditor());
          table.getColumnModel().getColumn(8).setCellRenderer(new MyButtonOpenFoxKifu());
          table.getColumnModel().getColumn(9).setPreferredWidth(0);
          table.getColumnModel().getColumn(10).setPreferredWidth(0);

          hideColumn(9);
          hideColumn(10);
          
          for (int i = 0; i < foxKifuInfos.size(); i++) {
            KifuInfo info = foxKifuInfos.get(i);
            String[] rowParams = {
              String.valueOf(info.index),
              info.playTime,
              info.blackName,
              info.blackRank,
              info.whiteName,
              info.whiteRank,
              info.result,
              String.valueOf(info.totalMoves),
              "",
              String.valueOf(info.isWin),
              info.chessid
            };
            rows.add(rowParams);
          }
          tabNumber = (int) Math.ceil(rows.size() / (double) numbersPerTab);
          curTabNumber = 1;
          for (int i = 0; i < numbersPerTab && i < rows.size(); i++) {
            model.addRow(rows.get(i));
          }
          setLblTab(1);          
          table.updateUI();
        }
      }
      if (jsonOjbect.has("chess")) {
        {
          String kifu = jsonOjbect.getString("chess");
          SGFParser.loadFromString(kifu);
          Lizzie.board.setMovelistAll();
          if (Lizzie.leelaz.isPondering()) Lizzie.leelaz.ponder();
          Lizzie.frame.refresh();
          setExtendedState(JFrame.ICONIFIED);
        }
      }
    } catch (JSONException e1) {
      // TODO Auto-generated catch block
      e1.printStackTrace();
      Utils.showHtmlMessage("获取棋谱失败", "消息: " + string);
    }
  }

  private void setLblTab(int i) {
	// TODO Auto-generated method stub
	  lblTab.setText(i+"/" + tabNumber + (this.isComplete ? "" : "..."));
}

private void hideColumn(int i) {
    // TODO Auto-generated method stub
    table.getColumnModel().getColumn(i).setWidth(0);
    table.getColumnModel().getColumn(i).setMaxWidth(0);
    table.getColumnModel().getColumn(i).setMinWidth(0);
    table.getTableHeader().getColumnModel().getColumn(i).setMaxWidth(0);
    table.getTableHeader().getColumnModel().getColumn(i).setMinWidth(0);
  }
}

class ColorTableCellRenderer extends DefaultTableCellRenderer {

  public Component getTableCellRendererComponent(
      JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
    setHorizontalAlignment(CENTER);
    if (table.getValueAt(row, 9).toString().equals("true")) {
      setForeground(Color.RED);
    } else setForeground(Color.GRAY);
    return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
  }
}

class MyButtonOpenFoxKifu implements TableCellRenderer {
  private JPanel panel;
  private JButton button;

  public MyButtonOpenFoxKifu() {
    initButton();
    initPanel();
    panel.add(button, BorderLayout.CENTER);
  }

  private void initButton() {
    button = new JButton();
    button.setMargin(new Insets(0, 0, 0, 0));
  }

  private void initPanel() {
    panel = new JPanel();
    panel.setLayout(new BorderLayout());
  }

  public Component getTableCellRendererComponent(
      JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
    button.setText(Lizzie.resourceBundle.getString("PrivateKifuSearch.open")); // ("打开");
    return panel;
  }
}

class MyButtonOpenFoxKifuEditor extends AbstractCellEditor implements TableCellEditor {
  private JPanel panel;
  private JButton button;
  private String chessid = "";

  public MyButtonOpenFoxKifuEditor() {
    initButton();
    initPanel();
    panel.add(this.button, BorderLayout.CENTER);
  }

  private void initButton() {
    button = new JButton();
    button.setMargin(new Insets(0, 0, 0, 0));
    button.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            Lizzie.frame.foxKifuDownload.foxReq.sendCommand("chessid " + chessid);
          }
        });
  }

  private void initPanel() {
    panel = new JPanel();
    panel.setLayout(new BorderLayout());
  }

  @Override
  public Component getTableCellEditorComponent(
      JTable table, Object value, boolean isSelected, int row, int column) {
    button.setText(Lizzie.resourceBundle.getString("PrivateKifuSearch.open"));
    chessid = table.getValueAt(row, 10).toString();
    return panel;
  }

  @Override
  public Object getCellEditorValue() {
    // TODO Auto-generated method stub
    return null;
  }
}

class KifuInfo {
  int index;
  String playTime;
  String blackName;
  String blackRank;
  String whiteName;
  String whiteRank;
  String result;
  int totalMoves;
  String chessid;
  boolean isWin;
}
