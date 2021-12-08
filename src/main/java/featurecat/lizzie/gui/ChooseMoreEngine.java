package featurecat.lizzie.gui;

import featurecat.lizzie.Config;
import featurecat.lizzie.Lizzie;
import featurecat.lizzie.util.Utils;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;
import java.util.ResourceBundle;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;

@SuppressWarnings("serial")
public class ChooseMoreEngine extends JPanel {
  public static Config config;
  public TableModel dataModel;
  JPanel tablepanel;
  PanelWithToolTips selectpanel = new PanelWithToolTips();

  JScrollPane scrollpane;
  public static JTable table;
  public static JLabel checkBlacktxt;
  public static JLabel checkWhitetxt;
  Font headFont;
  Font winrateFont;
  static JDialog engch;
  static int engineMenuIndex;
  Timer timer;
  int sortnum = 3;
  public static int selectedorder = -1;
  boolean issorted = false;
  // JSpinner dropwinratechooser = new JSpinner(new SpinnerNumberModel(1, 0, 99,
  // 1));
  // JSpinner playoutschooser = new JSpinner(new SpinnerNumberModel(100, 0, 99999,
  // 100));
  // JCheckBox checkBlack = new JCheckBox();
  // JCheckBox checkWhite = new JCheckBox();
  // JTextArea command;
  // JTextField txtName;
  // JLabel engineName;
  // JCheckBox preload;
  // JTextField txtWidth;
  // JTextField txtHeight;
  // JTextField txtKomi;
  //
  // JButton scan;
  // JButton delete;
  JButton ok;
  // JButton noEngine;
  // JButton exit;
  // JCheckBox chkdefault;
  int curIndex = -1;

  public String enginePath = "";
  public String weightPath = "";
  public String commandHelp = "";
  private static final ResourceBundle resourceBundle = Lizzie.resourceBundle;

  public ChooseMoreEngine() {
    // super(new BorderLayout());

    (new File("")).getAbsoluteFile().toPath();
    System.getProperty("os.name", "generic").toLowerCase(Locale.ENGLISH);
    this.setLayout(null);
    dataModel = getTableModel();
    table = new JTable(dataModel);
    selectpanel.setLayout(null);
    winrateFont =
        new Font(Lizzie.config.uiFontName, Font.PLAIN, Math.max(Config.frameFontSize, 14));
    headFont = new Font(Lizzie.config.uiFontName, Font.PLAIN, Math.max(Config.frameFontSize, 13));

    table.getTableHeader().setFont(headFont);
    table.setFont(winrateFont);
    table.getTableHeader().setReorderingAllowed(false);
    table.getTableHeader().setResizingAllowed(false);
    TableCellRenderer tcr = new ColorTableCellRenderer();
    table.setDefaultRenderer(Object.class, tcr);
    table.setRowHeight(20);

    tablepanel = new JPanel(new BorderLayout());
    tablepanel.setBounds(0, 0, 885, 660);
    this.add(tablepanel);
    selectpanel.setBounds(0, 660, 900, 30);
    this.add(selectpanel);
    scrollpane = new JScrollPane(table);

    tablepanel.add(scrollpane);
    table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    table.setFillsViewportHeight(true);
    table.getColumnModel().getColumn(0).setPreferredWidth(40);
    table.getColumnModel().getColumn(1).setPreferredWidth(250);
    table.getColumnModel().getColumn(2).setPreferredWidth(700);
    // boolean persisted = Lizzie.config.persistedUi != null;
    // if (persisted
    // && Lizzie.config.persistedUi.optJSONArray("badmoves-list-position") != null
    // && Lizzie.config.persistedUi.optJSONArray("badmoves-list-position").length()
    // == 12) {
    // JSONArray pos =
    // Lizzie.config.persistedUi.getJSONArray("badmoves-list-position");
    // // table.getColumnModel().getColumn(0).setPreferredWidth(pos.getInt(4));
    // // table.getColumnModel().getColumn(1).setPreferredWidth(pos.getInt(5));
    // // table.getColumnModel().getColumn(2).setPreferredWidth(pos.getInt(6));
    // // table.getColumnModel().getColumn(3).setPreferredWidth(pos.getInt(7));
    // }

    JTableHeader header = table.getTableHeader();
    table.setFont(winrateFont);
    table.setRowHeight(Config.menuHeight);
    table.getTableHeader().setFont(headFont);
    table
        .getTableHeader()
        .setPreferredSize(
            new Dimension(table.getColumnModel().getTotalColumnWidth(), Config.menuHeight));
    // dropwinratechooser.setValue(Lizzie.config.limitbadmoves);
    // playoutschooser.setValue(Lizzie.config.limitbadplayouts);
    // checkBlack.setSelected(true);
    // checkWhite.setSelected(true);

    ok = new JFontButton(resourceBundle.getString("ChooseMoreEngine.ok")); // "切换选中引擎"
    // noEngine = new JButton("不加载引擎");
    // exit = new JButton("退出");

    // noEngine.setFocusable(false);
    // noEngine.setMargin(new Insets(0, 0, 0, 0));
    // exit.setFocusable(false);
    // exit.setMargin(new Insets(0, 0, 0, 0));
    ok.setFocusable(false);
    ok.setMargin(new Insets(0, 0, 0, 0));

    ok.setBounds(784, 0, 100, 26);
    // noEngine.setBounds(800, 20, 80, 22);
    // exit.setBounds(800, 20, 80, 22);

    selectpanel.add(ok);
    // selectpanel.add(noEngine);
    // selectpanel.add(exit);

    ok.addActionListener(
        new ActionListener() {

          @Override
          public void actionPerformed(ActionEvent e) {
            engch.setVisible(false);
            if (curIndex < 0) {
              JOptionPane.showMessageDialog(
                  engch, resourceBundle.getString("ChooseMoreEngine.selectHint")); // "请先选择一个引擎 ");
              return;
            }
            if (engineMenuIndex == 1) Lizzie.engineManager.switchEngine(curIndex, true);
            if (engineMenuIndex == 2) Lizzie.engineManager.switchEngine(curIndex, false);
          }
        });

    table.addMouseListener(
        new MouseAdapter() {
          public void mouseClicked(MouseEvent e) {
            int row = table.rowAtPoint(e.getPoint());
            int col = table.columnAtPoint(e.getPoint());
            if (e.getClickCount() == 2) {
              if (row >= 0 && col >= 0) {
                try {
                  handleTableDoubleClick(row, col);
                } catch (Exception ex) {
                  ex.printStackTrace();
                }
              }
            } else {
              if (row >= 0 && col >= 0) {
                if (e.getButton() == MouseEvent.BUTTON3)
                  try {
                    handleTableDoubleClick(row, col);
                  } catch (Exception ex) {
                    ex.printStackTrace();
                  }
                else
                  try {
                    handleTableClick(row, col);
                  } catch (Exception ex) {
                    ex.printStackTrace();
                  }
              }
            }
          }
        });
    table.addKeyListener(
        new KeyAdapter() {
          public void keyPressed(KeyEvent e) {
            // if (e.getKeyCode() == KeyEvent.VK_B) {
            // Lizzie.frame.toggleBadMoves();
            // }
            // if (e.getKeyCode() == KeyEvent.VK_U) {
            // Lizzie.frame.toggleBestMoves();
            // }
            // if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            // if (Lizzie.frame.isPlayingAgainstLeelaz) {
            // Lizzie.frame.isPlayingAgainstLeelaz = false;
            // Lizzie.leelaz.isThinking = false;
            // }
            // Lizzie.leelaz.togglePonder();
            // }
            // if (e.getKeyCode() == KeyEvent.VK_Q) {
            // togglealwaysontop();
            // }
          }
        });

    header.addMouseListener(
        new MouseAdapter() {
          public void mouseReleased(MouseEvent e) {
            // int pick = header.columnAtPoint(e.getPoint());
            // sortnum = pick;
            // issorted = !issorted;
          }
        });
  }

  class ColorTableCellRenderer extends DefaultTableCellRenderer {
    DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();

    public Component getTableCellRendererComponent(
        JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
      if (column == 2) {
        JLabel label =
            (JLabel)
                super.getTableCellRendererComponent(table, value, isSelected, false, row, column);
        label.setToolTipText(value.toString());
        return label;
      }
      return renderer.getTableCellRendererComponent(table, value, isSelected, false, row, column);
    }
  }

  private void handleTableClick(int row, int col) {

    curIndex = Integer.parseInt(table.getModel().getValueAt(row, 0).toString()) - 1;
  }

  private void handleTableDoubleClick(int row, int col) {
    engch.setVisible(false);
    curIndex = Integer.parseInt(table.getModel().getValueAt(row, 0).toString()) - 1;
    //  Lizzie.config.uiConfig.put("default-engine", curIndex);
    if (engineMenuIndex == 1) Lizzie.engineManager.switchEngine(curIndex, true);
    if (engineMenuIndex == 2) Lizzie.engineManager.switchEngine(curIndex, false);
  }

  //  public ArrayList<EngineData> getEngineData() {
  //    ArrayList<EngineData> engineData = new ArrayList<EngineData>();
  //    Optional<JSONArray> enginesCommandOpt =
  //        Optional.ofNullable(Lizzie.config.leelazConfig.optJSONArray("engine-command-list"));
  //    Optional<JSONArray> enginesNameOpt =
  //        Optional.ofNullable(Lizzie.config.leelazConfig.optJSONArray("engine-name-list"));
  //    Optional<JSONArray> enginesPreloadOpt =
  //        Optional.ofNullable(Lizzie.config.leelazConfig.optJSONArray("engine-preload-list"));
  //
  //    Optional<JSONArray> enginesWidthOpt =
  //        Optional.ofNullable(Lizzie.config.leelazConfig.optJSONArray("engine-width-list"));
  //
  //    Optional<JSONArray> enginesHeightOpt =
  //        Optional.ofNullable(Lizzie.config.leelazConfig.optJSONArray("engine-height-list"));
  //    Optional<JSONArray> enginesKomiOpt =
  //        Optional.ofNullable(Lizzie.config.leelazConfig.optJSONArray("engine-komi-list"));
  //
  //    int defaultEngine = Lizzie.config.uiConfig.optInt("default-engine", -1);
  //
  //    for (int i = 0;
  //        i < (enginesCommandOpt.isPresent() ? enginesCommandOpt.get().length() + 1 : 0);
  //        i++) {
  //      if (i == 0) {
  //        String engineCommand = Lizzie.config.leelazConfig.getString("engine-command");
  //        int width = enginesWidthOpt.isPresent() ? enginesWidthOpt.get().optInt(i, 19) : 19;
  //        int height = enginesHeightOpt.isPresent() ? enginesHeightOpt.get().optInt(i, 19) : 19;
  //        String name = enginesNameOpt.isPresent() ? enginesNameOpt.get().optString(i, "") : "";
  //        float komi =
  //            enginesKomiOpt.isPresent()
  //                ? enginesKomiOpt.get().optFloat(i, (float) 7.5)
  //                : (float) 7.5;
  //        boolean preload =
  //            enginesPreloadOpt.isPresent() ? enginesPreloadOpt.get().optBoolean(i, false) :
  // false;
  //        EngineData enginedt = new EngineData();
  //        enginedt.commands = engineCommand;
  //        enginedt.name = name;
  //        enginedt.preload = preload;
  //        enginedt.index = i;
  //        enginedt.width = width;
  //        enginedt.height = height;
  //        enginedt.komi = komi;
  //        if (defaultEngine == i) enginedt.isDefault = true;
  //        else enginedt.isDefault = false;
  //        engineData.add(enginedt);
  //      } else {
  //        String commands =
  //            enginesCommandOpt.isPresent() ? enginesCommandOpt.get().optString(i - 1, "") : "";
  //        if (!commands.equals("")) {
  //          int width = enginesWidthOpt.isPresent() ? enginesWidthOpt.get().optInt(i, 19) : 19;
  //          int height = enginesHeightOpt.isPresent() ? enginesHeightOpt.get().optInt(i, 19) : 19;
  //          String name = enginesNameOpt.isPresent() ? enginesNameOpt.get().optString(i, "") : "";
  //          float komi =
  //              enginesKomiOpt.isPresent()
  //                  ? enginesKomiOpt.get().optFloat(i, (float) 7.5)
  //                  : (float) 7.5;
  //          boolean preload =
  //              enginesPreloadOpt.isPresent() ? enginesPreloadOpt.get().optBoolean(i, false) :
  // false;
  //          EngineData enginedt = new EngineData();
  //          enginedt.commands = commands;
  //          enginedt.name = name;
  //          enginedt.preload = preload;
  //          enginedt.index = i;
  //          enginedt.width = width;
  //          enginedt.height = height;
  //          enginedt.komi = komi;
  //          if (defaultEngine == i) enginedt.isDefault = true;
  //          else enginedt.isDefault = false;
  //          engineData.add(enginedt);
  //        }
  //      }
  //    }
  //    return engineData;
  //  }

  public AbstractTableModel getTableModel() {

    return new AbstractTableModel() {
      public int getColumnCount() {

        return 3;
      }

      public int getRowCount() {
        ArrayList<EngineData> EngineDatas = Utils.getEngineData();
        return EngineDatas.size();
      }

      public String getColumnName(int column) {

        if (column == 0) return resourceBundle.getString("ChooseMoreEngine.column1"); // "序号";
        if (column == 1) return resourceBundle.getString("ChooseMoreEngine.column2"); // "名称";
        if (column == 2) return resourceBundle.getString("ChooseMoreEngine.column3"); // "命令行";

        return "";
      }

      public Object getValueAt(int row, int col) {
        ArrayList<EngineData> EngineDatas = Utils.getEngineData();
        if (row > (EngineDatas.size() - 1)) {
          if (col == 0) return row + 1;
          return "";
        }
        EngineData data = EngineDatas.get(row);

        if (col != 0 && data.commands.equals("")) {
          return "";
        }

        switch (col) {
          case 0:
            return data.index + 1;
          case 1:
            return data.name;
          case 2:
            return data.commands;
          default:
            return "";
        }
      }
    };
  }

  public static JDialog createDialog(int index) {
    // Create and set up the window.
    engch = new JDialog();
    engch.setTitle(resourceBundle.getString("ChooseMoreEngine.title")); // "选择要切换的引擎(双击直接切换)");
    engineMenuIndex = index;
    engch.addWindowListener(
        new WindowAdapter() {
          public void windowClosing(WindowEvent e) {
            engch.setVisible(false);
          }
        });

    final ChooseMoreEngine newContentPane = new ChooseMoreEngine();
    newContentPane.setOpaque(true); // content panes must be opaque
    engch.setContentPane(newContentPane);
    // Display the window.
    // jf.setSize(521, 320);

    // boolean persisted = Lizzie.config.persistedUi != null;

    // engch.setBounds(50, 50, 891, 720);
    Lizzie.setFrameSize(engch, 891, 715);
    engch.setResizable(false);
    try {
      engch.setIconImage(ImageIO.read(LoadEngine.class.getResourceAsStream("/assets/logo.png")));
    } catch (IOException e) {
      e.printStackTrace();
    }
    engch.setAlwaysOnTop(true);
    engch.setLocationRelativeTo(engch.getOwner());
    // jf.setResizable(false);
    return engch;
  }
}
