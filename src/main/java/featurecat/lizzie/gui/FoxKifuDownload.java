package featurecat.lizzie.gui;

import featurecat.lizzie.Config;
import featurecat.lizzie.Lizzie;
import featurecat.lizzie.analysis.GetFoxRequest;
import featurecat.lizzie.rules.SGFParser;
import featurecat.lizzie.util.Utils;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.AbstractCellEditor;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
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
  private JScrollPane scrollPane;
  private JTextField txtUserName;
  public GetFoxRequest foxReq;
  private List<KifuInfo> foxKifuInfos;
  private int myUid;
  private String lastCode = "";
  private int tabNumber = 1;
  private int numbersPerTab = 25;
  private int curTabNumber = 1;
  private boolean isComplete = false;
  private boolean isSearching = false;
  JLabel lblTab;
  private ArrayList<String[]> rows;
  private boolean isSecondTimeReqEmpty = false;
  private boolean isRequestEmpty = false;

  public FoxKifuDownload() {
    Lizzie.setFrameSize(this, 950, 635);
    setTitle(Lizzie.resourceBundle.getString("FoxKifuDownload.title"));
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

    JLabel lblUserName =
        new JFontLabel(Lizzie.resourceBundle.getString("FoxKifuDownload.lblUserName"));
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

    JButton btnSearch =
        new JFontButton(Lizzie.resourceBundle.getString("FoxKifuDownload.btnSearch"));
    btnSearch.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            getFoxKifus();
          }
        });
    panel.add(btnSearch);

    JLabel lblAfterGet = new JFontLabel();
    lblAfterGet.setText(Lizzie.resourceBundle.getString("FoxKifuDownload.lblAfterGet"));
    panel.add(lblAfterGet);

    JComboBox<String> cbxAfterGet = new JFontComboBox<String>();
    panel.add(cbxAfterGet);
    cbxAfterGet.addItem(Lizzie.resourceBundle.getString("FoxKifuDownload.cbxAfterGet.min"));
    cbxAfterGet.addItem(Lizzie.resourceBundle.getString("FoxKifuDownload.cbxAfterGet.close"));
    cbxAfterGet.addItem(Lizzie.resourceBundle.getString("FoxKifuDownload.cbxAfterGet.none"));
    cbxAfterGet.addItemListener(
        new ItemListener() {
          public void itemStateChanged(final ItemEvent e) {
            int index = cbxAfterGet.getSelectedIndex();
            Lizzie.config.foxAfterGet = index;
            Lizzie.config.uiConfig.put("fox-after-get", index);
          }
        });
    cbxAfterGet.setSelectedIndex(Lizzie.config.foxAfterGet);

    JPanel buttonPane = new JPanel();

    table =
        new JTable() {
          public boolean isCellEditable(int row, int column) {
            if (column == 8) return true;
            else return false;
          }
        };
    table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    table.setFillsViewportHeight(true);
    ((DefaultTableCellRenderer) table.getTableHeader().getDefaultRenderer())
        .setHorizontalAlignment(JLabel.CENTER);
    table
        .getTableHeader()
        .setFont(new Font(Config.sysDefaultFontName, Font.PLAIN, Config.frameFontSize));
    table.setFont(new Font(Config.sysDefaultFontName, Font.PLAIN, Config.frameFontSize));
    table.setRowHeight(Config.menuHeight);
    table.addMouseListener(
        new MouseAdapter() {
          public void mouseClicked(MouseEvent e) {
            int row = table.rowAtPoint(e.getPoint());
            int col = table.columnAtPoint(e.getPoint());
            if (e.getClickCount() == 2) {
              if (row >= 0 && col >= 0) {
                foxReq.sendCommand("chessid " + table.getValueAt(row, 10).toString());
              }
            }
          }
        });
    TableCellRenderer tcr = new ColorTableCellRenderer();
    table.setDefaultRenderer(Object.class, tcr);
    scrollPane = new JScrollPane(table);
    getContentPane().add(scrollPane, BorderLayout.CENTER);
    getContentPane().add(buttonPane, BorderLayout.SOUTH);

    JButton btnFirst = new JFontButton("|<");
    btnFirst.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            if (rows == null) return;
            curTabNumber = 1;
            while (model.getRowCount() > 0) {
              model.removeRow(model.getRowCount() - 1);
            }
            for (int i = (curTabNumber - 1) * numbersPerTab;
                i < numbersPerTab && i < foxKifuInfos.size();
                i++) {
              model.addRow(rows.get(i));
            }
            setLblTab(1);
            isSecondTimeReqEmpty = false;
          }
        });
    buttonPane.add(btnFirst);

    JButton btnPrevious = new JFontButton("<");
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
                i < curTabNumber * numbersPerTab && i < foxKifuInfos.size();
                i++) {
              model.addRow(rows.get(i));
            }
            setLblTab(curTabNumber);
            isSecondTimeReqEmpty = false;
          }
        });
    buttonPane.add(btnPrevious);

    JButton btnNext = new JFontButton(">");
    btnNext.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            if (rows == null) return;
            if (curTabNumber == tabNumber) {
              maybeGetNextPage();
              return;
            }
            curTabNumber = curTabNumber + 1;
            while (model.getRowCount() > 0) {
              model.removeRow(model.getRowCount() - 1);
            }
            for (int i = (curTabNumber - 1) * numbersPerTab;
                i < curTabNumber * numbersPerTab && i < foxKifuInfos.size();
                i++) {
              model.addRow(rows.get(i));
            }
            setLblTab(curTabNumber);
            maybeGetNextPage();
          }
        });
    buttonPane.add(btnNext);

    JButton btnLast = new JFontButton(">|");
    btnLast.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            if (rows == null) return;
            if (curTabNumber == tabNumber) {
              maybeGetNextPage();
              return;
            }
            curTabNumber = tabNumber;
            while (model.getRowCount() > 0) {
              model.removeRow(model.getRowCount() - 1);
            }
            for (int i = (curTabNumber - 1) * numbersPerTab;
                i < curTabNumber * numbersPerTab && i < foxKifuInfos.size();
                i++) {
              model.addRow(rows.get(i));
            }
            setLblTab(curTabNumber);
            maybeGetNextPage();
          }
        });
    buttonPane.add(btnLast);

    lblTab = new JFontLabel("1/1");
    lblTab.setPreferredSize(new Dimension(Config.menuHeight * 3, Config.menuHeight));
    buttonPane.add(lblTab);
  }

  private void maybeGetNextPage() {
    // TODO Auto-generated method stub
    if (curTabNumber == tabNumber || tabNumber >= 4 && curTabNumber == tabNumber - 1) {
      String last = foxKifuInfos.get(foxKifuInfos.size() - 1).chessid;
      if (!lastCode.equals(last)) {
        lastCode = last;
        this.foxReq.sendCommand(
            "uid " + this.myUid + " " + foxKifuInfos.get(foxKifuInfos.size() - 1).chessid);
      } else {
        if (curTabNumber == tabNumber) {
          if (isSecondTimeReqEmpty)
            Utils.showMsg(Lizzie.resourceBundle.getString("FoxKifuDownload.noMoreKifu"), this);
          if (isRequestEmpty) {
            isSecondTimeReqEmpty = true;
          }
        }
      }
    }
  }

  private void getFoxKifus() {
    // TODO Auto-generated method stub
    if (txtUserName.getText().trim().isEmpty()) {
      Utils.showMsg(Lizzie.resourceBundle.getString("FoxKifuDownload.noUser"), this);
      return;
    }
    if (isSearching) {
      Utils.showMsg(Lizzie.resourceBundle.getString("FoxKifuDownload.waitLastSearch"), this);
      return;
    }
    isSearching = true;
    isSecondTimeReqEmpty = false;
    isRequestEmpty = false;
    foxReq = new GetFoxRequest(this);
    foxKifuInfos = new ArrayList<KifuInfo>();
    lastCode = "";
    foxReq.sendCommand("user_name " + txtUserName.getText());
  }

  public void receiveResult(String string) {
    // TODO Auto-generated method stub
    try {
      JSONObject jsonOjbect = new JSONObject(string);
      if (jsonOjbect.has("uid")) {
        myUid = jsonOjbect.getInt("uid");
        foxReq.sendCommand("uid " + myUid);
      }
      if (jsonOjbect.has("chesslist")) {
        isSearching = false;
        JSONArray jsonArray = jsonOjbect.getJSONArray("chesslist");
        int oldRows = foxKifuInfos.size();
        if (jsonArray.length() == 0) {
          if (oldRows > 0) {
            isComplete = true;
            isSecondTimeReqEmpty = true;
            isRequestEmpty = true;
          } else Utils.showMsg(Lizzie.resourceBundle.getString("FoxKifuDownload.noKifu"), this);
        }
        isComplete = jsonArray.length() < 100;
        for (int i = 0; i < jsonArray.length(); i++) {
          JSONObject jsonObject = jsonArray.getJSONObject(i);
          KifuInfo kifuInfo = new KifuInfo();
          kifuInfo.index = i + 1;
          kifuInfo.playTime = jsonObject.getString("starttime");
          kifuInfo.blackName =
              jsonObject.optString("blacknick", jsonObject.getString("blackenname"));
          int bRank = jsonObject.getInt("blackdan") - 17;
          kifuInfo.blackRank =
              bRank > 0
                  ? bRank + Lizzie.resourceBundle.getString("FoxKifuDownload.rank.dan")
                  : (Math.abs(bRank) + 1)
                      + Lizzie.resourceBundle.getString("FoxKifuDownload.rank.kyu");
          kifuInfo.whiteName =
              jsonObject.optString("whitenick", jsonObject.getString("whiteenname"));
          int wRank = jsonObject.getInt("whitedan") - 17;
          kifuInfo.whiteRank =
              wRank > 0
                  ? wRank + Lizzie.resourceBundle.getString("FoxKifuDownload.rank.dan")
                  : (Math.abs(wRank) + 1)
                      + Lizzie.resourceBundle.getString("FoxKifuDownload.rank.kyu");
          kifuInfo.chessid = jsonObject.getString("chessid");
          // kifuInfo.result=
          kifuInfo.totalMoves = jsonObject.getInt("movenum");
          String result = "";
          int winner = jsonObject.getInt("winner");
          int point = jsonObject.getInt("point");
          int rule = jsonObject.getInt("rule");
          if (winner == 1 || winner == 2) {
            if (winner == 1) {
              result = Lizzie.resourceBundle.getString("FoxKifuDownload.black");
              if (jsonObject.getInt("blackuid") == myUid) kifuInfo.isWin = true;
              else kifuInfo.isWin = false;
            } else {
              result = Lizzie.resourceBundle.getString("FoxKifuDownload.white");
              if (jsonObject.getInt("whiteuid") == myUid) kifuInfo.isWin = true;
              else kifuInfo.isWin = false;
            }
            if (point < 0) {
              if (point == -1)
                result += Lizzie.resourceBundle.getString("FoxKifuDownload.winByRes");
              else if (point == -2)
                result += Lizzie.resourceBundle.getString("FoxKifuDownload.winByTime");
              else result += Lizzie.resourceBundle.getString("FoxKifuDownload.win");
            } else {
              String unit = "";
              if (rule == 1) unit = Lizzie.resourceBundle.getString("FoxKifuDownload.stones");
              if (rule == 0) unit = Lizzie.resourceBundle.getString("FoxKifuDownload.points");
              result +=
                  (Lizzie.config.isChinese
                          ? Lizzie.resourceBundle.getString("FoxKifuDownload.win")
                          : "+")
                      + point / 100f
                      + unit;
            }
          } else result = Lizzie.resourceBundle.getString("FoxKifuDownload.other");
          kifuInfo.result = result;
          foxKifuInfos.add(kifuInfo);
        }
        rows = new ArrayList<String[]>();
        if (foxKifuInfos.size() >= 1) {
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
          if (oldRows <= 0) {
            model = new DefaultTableModel();
            model.addColumn(Lizzie.resourceBundle.getString("FoxKifuDownload.column.index"));
            model.addColumn(Lizzie.resourceBundle.getString("FoxKifuDownload.column.time"));
            model.addColumn(Lizzie.resourceBundle.getString("FoxKifuDownload.column.black"));
            model.addColumn(Lizzie.resourceBundle.getString("FoxKifuDownload.column.rank"));
            model.addColumn(Lizzie.resourceBundle.getString("FoxKifuDownload.column.white"));
            model.addColumn(Lizzie.resourceBundle.getString("FoxKifuDownload.column.rank"));
            model.addColumn(Lizzie.resourceBundle.getString("FoxKifuDownload.column.result"));
            model.addColumn(Lizzie.resourceBundle.getString("FoxKifuDownload.column.moves"));
            model.addColumn(Lizzie.resourceBundle.getString("FoxKifuDownload.column.open"));
            model.addColumn("");
            model.addColumn("");

            for (int i = 0; i < numbersPerTab && i < rows.size(); i++) {
              model.addRow(rows.get(i));
            }

            if (model.getRowCount() > 0) {
              table.setModel(model);
              table.getColumnModel().getColumn(0).setPreferredWidth(25);
              table.getColumnModel().getColumn(1).setPreferredWidth(190);
              table.getColumnModel().getColumn(2).setPreferredWidth(100);
              table.getColumnModel().getColumn(3).setPreferredWidth(40);
              table.getColumnModel().getColumn(4).setPreferredWidth(100);
              table.getColumnModel().getColumn(5).setPreferredWidth(35);
              table.getColumnModel().getColumn(6).setPreferredWidth(100);
              table.getColumnModel().getColumn(7).setPreferredWidth(50);
              table.getColumnModel().getColumn(8).setPreferredWidth(35);
              table.getColumnModel().getColumn(8).setCellEditor(new MyButtonOpenFoxKifuEditor());
              table.getColumnModel().getColumn(8).setCellRenderer(new MyButtonOpenFoxKifu());
              table.getColumnModel().getColumn(9).setPreferredWidth(0);
              table.getColumnModel().getColumn(10).setPreferredWidth(0);
              hideColumn(9);
              hideColumn(10);
              table.revalidate();
              if (Lizzie.config.isFrameFontSmall() && rows.size() >= 25) {
                scrollPane.setPreferredSize(
                    new Dimension(
                        scrollPane.getWidth(),
                        table.getTableHeader().getHeight() + Config.menuHeight * 25 + 2));
                pack();
              }
            }
            curTabNumber = 1;
          }
          setLblTab(curTabNumber);
        }
      }
      if (jsonOjbect.has("chess")) {
        {
          String kifu = jsonOjbect.getString("chess");
          boolean oriReadKomi = Lizzie.config.readKomi;
          Lizzie.config.readKomi = false;
          SGFParser.loadFromString(kifu);
          Lizzie.board.setMovelistAll();
          if (Lizzie.leelaz.isPondering()) Lizzie.leelaz.ponder();
          Lizzie.frame.refresh();
          Lizzie.config.readKomi = oriReadKomi;
          if (Lizzie.config.foxAfterGet == 0) setExtendedState(JFrame.ICONIFIED);
          else if (Lizzie.config.foxAfterGet == 1) setVisible(false);
        }
      }
    } catch (JSONException e1) {
      // TODO Auto-generated catch block
      e1.printStackTrace();
      Utils.showMsg(
          Lizzie.resourceBundle.getString("FoxKifuDownload.getKifuFailed") + string, this);
      isSearching = false;
    }
  }

  private void setLblTab(int i) {
    // TODO Auto-generated method stub
    lblTab.setText(i + "/" + tabNumber + (this.isComplete ? "" : "..."));
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
    return super.getTableCellRendererComponent(table, value, isSelected, false, row, column);
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
    button = new JFontButton();
    button.setMargin(new Insets(0, 0, 0, 0));
  }

  private void initPanel() {
    panel = new JPanel();
    panel.setLayout(new BorderLayout());
  }

  public Component getTableCellRendererComponent(
      JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
    button.setText(Lizzie.resourceBundle.getString("FoxKifuDownload.column.open"));
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
    button = new JFontButton();
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
