package featurecat.lizzie.gui;

import featurecat.lizzie.Config;
import featurecat.lizzie.Lizzie;
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
import java.util.Optional;
import java.util.ResourceBundle;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;
import org.json.JSONArray;

public class OtherPrograms extends JPanel {
  public static Config config;
  public TableModel dataModel;
  JPanel tablepanel;
  JPanel selectpanel = new JPanel();

  JScrollPane scrollpane;
  public static JTable table;
  // public static JLabel checkBlacktxt;
  // public static JLabel checkWhitetxt;
  Font headFont;
  Font winrateFont;
  static JDialog engjf;
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
  JTextArea command;
  JTextField txtName;
  JLabel engineName;

  JButton scan;
  JButton delete;
  JButton save;
  JButton cancel;
  JButton exit;
  JButton moveUp;
  JButton moveDown;
  JButton moveFirst;
  int curIndex = -1;

  public String enginePath = "";

  public String engineSacnName = "";
  public String weightPath = "";
  public String commandHelp = "";
  private final ResourceBundle resourceBundle =
      Lizzie.config.useLanguage == 0
          ? ResourceBundle.getBundle("l10n.DisplayStrings")
          : (Lizzie.config.useLanguage == 1
              ? ResourceBundle.getBundle("l10n.DisplayStrings", new Locale("zh", "CN"))
              : ResourceBundle.getBundle("l10n.DisplayStrings", new Locale("en", "US")));
  private String osName;

  public OtherPrograms() {
    // super(new BorderLayout());

    (new File("")).getAbsoluteFile().toPath();
    osName = System.getProperty("os.name", "generic").toLowerCase(Locale.ENGLISH);
    this.setLayout(null);
    dataModel = getTableModel();
    table = new JTable(dataModel);
    selectpanel.setLayout(null);
    winrateFont = new Font("Microsoft YaHei", Font.PLAIN, Math.max(Config.frameFontSize, 14));
    headFont = new Font("Microsoft YaHei", Font.PLAIN, Math.max(Config.frameFontSize, 13));

    table.getTableHeader().setFont(headFont);
    table.setFont(winrateFont);
    table.getTableHeader().setReorderingAllowed(false);
    table.getTableHeader().setResizingAllowed(false);
    TableCellRenderer tcr = new ColorTableCellRenderer();
    table.setDefaultRenderer(Object.class, tcr);
    table.setRowHeight(20);

    tablepanel = new JPanel(new BorderLayout());
    tablepanel.setBounds(0, 330, 885, 432);
    this.add(tablepanel, BorderLayout.SOUTH);
    selectpanel.setBounds(0, 0, 900, 330);
    this.add(selectpanel, BorderLayout.NORTH);
    scrollpane = new JScrollPane(table);

    tablepanel.add(scrollpane);
    table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    table.setFillsViewportHeight(true);
    table.getColumnModel().getColumn(0).setPreferredWidth(30);
    table.getColumnModel().getColumn(1).setPreferredWidth(200);
    table.getColumnModel().getColumn(2).setPreferredWidth(400);
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
    table.setRowHeight(Config.menuHeight);
    table.getTableHeader().setFont(headFont);
    table
        .getTableHeader()
        .setPreferredSize(
            new Dimension(table.getColumnModel().getTotalColumnWidth(), Config.menuHeight));
    table.setFont(winrateFont);
    JTableHeader header = table.getTableHeader();

    // dropwinratechooser.setValue(Lizzie.config.limitbadmoves);
    // playoutschooser.setValue(Lizzie.config.limitbadplayouts);
    // checkBlack.setSelected(true);
    // checkWhite.setSelected(true);

    engineName = new JLabel(resourceBundle.getString("OtherPrograms.engineName"));
    // = new JLabel("单击选中列表中的启动项进行设置");
    engineName.setFont(new Font("Microsoft YaHei", Font.PLAIN, 14));
    JLabel lblname =
        new JLabel(resourceBundle.getString("MoreEngines.lblName")); // new JLabel("名称：");
    txtName = new JTextField();
    command = new JFontTextArea(5, 80);
    command.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
    txtName.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
    txtName.setFont(new Font(Config.sysDefaultFontName, Font.PLAIN, Config.frameFontSize));
    command.setFont(new Font(Config.sysDefaultFontName, Font.PLAIN, Config.frameFontSize));
    JLabel lblCommand = new JLabel(resourceBundle.getString("MoreEngines.lblCommand"));

    save = new JButton(resourceBundle.getString("MoreEngines.save")); // ("保存");
    cancel = new JButton(resourceBundle.getString("MoreEngines.cancel")); // ("取消");
    exit = new JButton(resourceBundle.getString("MoreEngines.exit")); // ("退出");
    delete = new JButton(resourceBundle.getString("MoreEngines.delete")); // ("删除");
    scan = new JButton(resourceBundle.getString("MoreEngines.scan")); // ("浏览");
    moveUp = new JButton(resourceBundle.getString("MoreEngines.moveUp")); // ("上移");
    moveDown = new JButton(resourceBundle.getString("MoreEngines.moveDown")); // ("下移");
    moveFirst = new JButton(resourceBundle.getString("MoreEngines.moveFirst")); // ("置顶");

    moveUp.setFocusable(false);
    moveUp.setMargin(new Insets(0, 0, 0, 0));
    moveDown.setFocusable(false);
    moveDown.setMargin(new Insets(0, 0, 0, 0));
    moveFirst.setFocusable(false);
    moveFirst.setMargin(new Insets(0, 0, 0, 0));
    scan.setFocusable(false);
    scan.setMargin(new Insets(0, 0, 0, 0));
    save.setFocusable(false);
    save.setMargin(new Insets(0, 0, 0, 0));
    cancel.setFocusable(false);
    cancel.setMargin(new Insets(0, 0, 0, 0));
    exit.setFocusable(false);
    exit.setMargin(new Insets(0, 0, 0, 0));
    delete.setFocusable(false);
    delete.setMargin(new Insets(0, 0, 0, 0));

    engineName.setBounds(5, 5, 700, 20);
    txtName.setBounds(72, 35, 778, 20);
    lblname.setBounds(5, 35, 45, 20);
    lblCommand.setBounds(5, 65, 68, 20);
    scan.setBounds(5, 85, 57, 20);
    command.setBounds(72, 65, 778, 200);

    moveUp.setBounds(450, 270, 40, 22);
    moveDown.setBounds(500, 270, 40, 22);
    moveFirst.setBounds(550, 270, 40, 22);
    save.setBounds(620, 270, 50, 22);
    cancel.setBounds(680, 270, 50, 22);
    delete.setBounds(740, 270, 50, 22);
    exit.setBounds(800, 270, 50, 22);
    // checkBlacktxt = new JLabel("黑:");
    // checkWhitetxt = new JLabel("白:");
    // JLabel dropwinratechoosertxt = new JLabel("胜率波动筛选:");
    // JLabel playoutschoosertxt = new JLabel("前后计算量筛选:");

    engineName.setEnabled(false);
    txtName.setEnabled(false);
    command.setEnabled(false);
    delete.setEnabled(false);
    moveUp.setEnabled(false);
    moveDown.setEnabled(false);
    moveFirst.setEnabled(false);

    cancel.setEnabled(false);
    scan.setEnabled(false);
    selectpanel.add(engineName);
    selectpanel.add(lblname);
    selectpanel.add(txtName);
    selectpanel.add(command);
    selectpanel.add(lblCommand);

    selectpanel.add(scan);
    selectpanel.add(save);
    selectpanel.add(cancel);
    selectpanel.add(exit);
    selectpanel.add(moveUp);
    selectpanel.add(moveDown);
    selectpanel.add(delete);
    selectpanel.add(moveFirst);

    scan.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            // TODO Auto-generated method stub
            String el = getEngineLine();
            if (!el.isEmpty()) {
              command.setText(el);
              txtName.setText(engineSacnName);
            }
          }
        });
    delete.addActionListener(
        new ActionListener() {

          @Override
          public void actionPerformed(ActionEvent e) {
            // TODO Auto-generated method stub
            command.setText("");
            saveEngineConfig();
            command.setText("");
            engineName.setText(resourceBundle.getString("MoreEngines.lblCommand"));
            txtName.setText("");
            engineName.setEnabled(false);
            txtName.setEnabled(false);
            command.setEnabled(false);
            moveUp.setEnabled(false);
            moveDown.setEnabled(false);
            moveFirst.setEnabled(false);
            delete.setEnabled(false);
            scan.setEnabled(false);
            cancel.setEnabled(false);
            curIndex = -1;
            table.validate();
            table.updateUI();
            table.getSelectionModel().clearSelection();
          }
        });
    exit.addActionListener(
        new ActionListener() {

          @Override
          public void actionPerformed(ActionEvent e) {
            // TODO Auto-generated method stub
            engjf.setVisible(false);
          }
        });
    cancel.addActionListener(
        new ActionListener() {

          @Override
          public void actionPerformed(ActionEvent e) {
            // TODO Auto-generated method stub
            command.setText("");
            engineName.setText(resourceBundle.getString("MoreEngines.lblCommand"));
            txtName.setText("");
            curIndex = -1;
            engineName.setEnabled(false);
            txtName.setEnabled(false);
            command.setEnabled(false);
            delete.setEnabled(false);
            scan.setEnabled(false);
            cancel.setEnabled(false);
            moveUp.setEnabled(false);
            moveFirst.setEnabled(false);
            moveDown.setEnabled(false);
            table.getSelectionModel().clearSelection();
          }
        });
    save.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            if (curIndex >= 0) saveEngineConfig();
            table.validate();
            table.updateUI();
          }
        });
    table.addMouseListener(
        new MouseAdapter() {
          public void mouseClicked(MouseEvent e) {
            int row = table.rowAtPoint(e.getPoint());
            int col = table.columnAtPoint(e.getPoint());

            handleTableClick(row, col);
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

    moveFirst.addActionListener(
        new ActionListener() {

          @Override
          public void actionPerformed(ActionEvent e) {

            ArrayList<ProgramData> programData = getProgramData();
            if (curIndex < 1 || curIndex > programData.size() - 1) return;
            ProgramData enginedt = programData.get(curIndex);
            programData.remove(curIndex);
            programData.add(0, enginedt);

            JSONArray commands = new JSONArray();
            JSONArray names = new JSONArray();
            for (int i = 0; i < programData.size(); i++) {
              ProgramData proDt = programData.get(i);
              commands.put(proDt.commands.trim());
              names.put(proDt.name);
            }
            Lizzie.config.leelazConfig.put("program-command-list", commands);
            Lizzie.config.leelazConfig.put("program-name-list", names);
            LizzieFrame.menu.updateFastLinks();
            table.validate();
            table.updateUI();
            curIndex = 0;
          }
        });

    moveUp.addActionListener(
        new ActionListener() {

          @Override
          public void actionPerformed(ActionEvent e) {

            ArrayList<ProgramData> programData = getProgramData();
            if (curIndex < 1 || curIndex > programData.size() - 1) return;
            ProgramData enginedt = programData.get(curIndex);
            programData.remove(curIndex);
            programData.add(curIndex - 1, enginedt);

            JSONArray commands = new JSONArray();
            JSONArray names = new JSONArray();
            for (int i = 0; i < programData.size(); i++) {
              ProgramData proDt = programData.get(i);
              commands.put(proDt.commands.trim());
              names.put(proDt.name);
            }
            Lizzie.config.leelazConfig.put("program-command-list", commands);
            Lizzie.config.leelazConfig.put("program-name-list", names);
            LizzieFrame.menu.updateFastLinks();
            table.validate();
            table.updateUI();
            curIndex = curIndex - 1;
          }
        });
    moveDown.addActionListener(
        new ActionListener() {

          @Override
          public void actionPerformed(ActionEvent e) {
            // TODO Auto-generated method stub

            ArrayList<ProgramData> programData = getProgramData();
            if (curIndex < 0 || curIndex > programData.size() - 2) return;
            ProgramData enginedt = programData.get(curIndex);
            programData.remove(curIndex);
            programData.add(curIndex + 1, enginedt);

            JSONArray commands = new JSONArray();
            JSONArray names = new JSONArray();
            for (int i = 0; i < programData.size(); i++) {
              ProgramData proDt = programData.get(i);

              commands.put(proDt.commands.trim());
              names.put(proDt.name);
            }
            Lizzie.config.leelazConfig.put("program-command-list", commands);
            Lizzie.config.leelazConfig.put("program-name-list", names);
            LizzieFrame.menu.updateFastLinks();
            table.validate();
            table.updateUI();
            curIndex = curIndex + 1;
          }
        });
  }

  class ColorTableCellRenderer extends DefaultTableCellRenderer {
    DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();

    public Component getTableCellRendererComponent(
        JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {

      // if (Lizzie.board.convertNameToCoordinates(table.getValueAt(row,
      // 2).toString())[0]
      // == Lizzie.frame.clickbadmove[0]
      // && Lizzie.board.convertNameToCoordinates(table.getValueAt(row,
      // 2).toString())[1]
      // == Lizzie.frame.clickbadmove[1]) {

      // Color hsbColor =
      // Color.getHSBColor(
      // Color.RGBtoHSB(238, 221, 130, null)[0],
      // Color.RGBtoHSB(238, 221, 130, null)[1],
      // Color.RGBtoHSB(238, 221, 130, null)[2]);
      // setBackground(hsbColor);
      // if (Math.abs(Float.parseFloat(table.getValueAt(row, 3).toString())) >= 5
      // && Math.abs(Float.parseFloat(table.getValueAt(row, 3).toString())) <= 10) {
      // Color hsbColor2 =
      // Color.getHSBColor(
      // Color.RGBtoHSB(255, 153, 18, null)[0],
      // Color.RGBtoHSB(255, 153, 18, null)[1],
      // Color.RGBtoHSB(255, 153, 18, null)[2]);
      // setForeground(hsbColor2);
      // } else if (Math.abs(Float.parseFloat(table.getValueAt(row, 3).toString())) >
      // 10) {
      // setForeground(Color.RED);
      // } else {
      // setForeground(Color.BLACK);
      // }
      // return super.getTableCellRendererComponent(table, value, isSelected, false,
      // row,
      // column);
      // }
      // if (Math.abs(Float.parseFloat(table.getValueAt(row, 3).toString())) >= 5
      // && Math.abs(Float.parseFloat(table.getValueAt(row, 3).toString())) <= 10) {
      // Color hsbColor =
      // Color.getHSBColor(
      // Color.RGBtoHSB(255, 153, 18, null)[0],
      // Color.RGBtoHSB(255, 153, 18, null)[1],
      // Color.RGBtoHSB(255, 153, 18, null)[2]);
      // setBackground(Color.WHITE);
      // setForeground(hsbColor);
      // return super.getTableCellRendererComponent(table, value, isSelected, false,
      // row,
      // column);
      // }
      // if (Math.abs(Float.parseFloat(table.getValueAt(row, 3).toString())) > 10) {
      // setBackground(Color.WHITE);
      // setForeground(Color.RED);
      // return super.getTableCellRendererComponent(table, value, isSelected, false,
      // row,
      // column);
      // } else
      if (row == curIndex) {
        return renderer.getTableCellRendererComponent(table, value, true, false, row, column);
      } else {
        return renderer.getTableCellRendererComponent(table, value, false, false, row, column);
      }
    }
  }

  public boolean isWindows() {
    return osName != null && !osName.contains("darwin") && osName.contains("win");
  }

  private String getEngineLine() {
    String engineLine = "";
    File engineFile = null;
    JFileChooser chooser = new JFileChooser(".");
    if (isWindows()) {
      FileNameExtensionFilter filter =
          new FileNameExtensionFilter(
              resourceBundle.getString("OtherPrograms.program"), "jar", "exe", "bat");
      chooser.setFileFilter(filter);
    } else {
      setVisible(false);
    }
    chooser.setMultiSelectionEnabled(false);
    chooser.setDialogTitle(resourceBundle.getString("OtherPrograms.selectProgram")); // ("选择应用程序");
    int result = chooser.showOpenDialog(this);
    if (result == JFileChooser.APPROVE_OPTION) {
      engineFile = chooser.getSelectedFile();
      if (engineFile != null) {
        enginePath = engineFile.getAbsolutePath();
        engineSacnName = engineFile.getName();
        return enginePath;
      }
    }
    return engineLine;
  }

  private void handleTableClick(int row, int col) {
    // if (selectedorder != row) {
    // int[] coords = Lizzie.board.convertNameToCoordinates(table.getValueAt(row,
    // 2).toString());
    // Lizzie.frame.clickbadmove = coords;
    // Lizzie.frame.boardRenderer.drawbadstone(coords[0], coords[1], Stone.BLACK);
    // Lizzie.frame.repaint();
    // selectedorder = row;
    // } else {
    // Lizzie.frame.clickbadmove = Lizzie.frame.outOfBoundCoordinate;
    // Lizzie.frame.boardRenderer.removedrawmovestone();
    // Lizzie.frame.repaint();
    // selectedorder = -1;
    // table.clearSelection();
    // }
    command.setText(table.getModel().getValueAt(row, 2).toString());
    engineName.setText(
        resourceBundle.getString("OtherPrograms.editEngine")
            + table.getModel().getValueAt(row, 0).toString());
    txtName.setText(table.getModel().getValueAt(row, 1).toString());
    curIndex = Integer.parseInt(table.getModel().getValueAt(row, 0).toString()) - 1;
    engineName.setEnabled(true);
    txtName.setEnabled(true);
    command.setEnabled(true);
    delete.setEnabled(true);
    save.setEnabled(true);
    cancel.setEnabled(true);
    scan.setEnabled(true);
    moveUp.setEnabled(true);
    moveFirst.setEnabled(true);
    moveDown.setEnabled(true);
    table.validate();
    table.updateUI();
  }

  private void saveEngineConfig() {
    ArrayList<ProgramData> programData = getProgramData();
    ProgramData programDt = new ProgramData();
    programDt.index = curIndex;
    programDt.commands = this.command.getText();
    programDt.name = this.txtName.getText();

    if (curIndex + 1 > programData.size()) {
      programData.add(programDt);
    } else {
      programData.remove(curIndex);
      programData.add(curIndex, programDt);
    }
    JSONArray commands = new JSONArray();
    JSONArray names = new JSONArray();
    for (int i = 0; i < programData.size(); i++) {
      ProgramData proDt = programData.get(i);
      if (!commands.equals("")) {
        commands.put(proDt.commands.trim());
        names.put(proDt.name);
      }
    }
    Lizzie.config.leelazConfig.put("program-command-list", commands);
    Lizzie.config.leelazConfig.put("program-name-list", names);
    LizzieFrame.menu.updateFastLinks();
  }

  public ArrayList<ProgramData> getProgramData() {
    ArrayList<ProgramData> ProgramData = new ArrayList<ProgramData>();
    Optional<JSONArray> enginesCommandOpt =
        Optional.ofNullable(Lizzie.config.leelazConfig.optJSONArray("program-command-list"));
    Optional<JSONArray> enginesNameOpt =
        Optional.ofNullable(Lizzie.config.leelazConfig.optJSONArray("program-name-list"));

    for (int i = 0;
        i < (enginesCommandOpt.isPresent() ? enginesCommandOpt.get().length() : 0);
        i++) {
      String commands = enginesCommandOpt.get().getString(i);
      if (!commands.equals("")) {
        String name = enginesNameOpt.isPresent() ? enginesNameOpt.get().optString(i, "") : "";
        ProgramData programDt = new ProgramData();
        programDt.commands = commands;
        programDt.name = name;
        programDt.index = i;
        ProgramData.add(programDt);
      }
    }
    return ProgramData;
  }

  public AbstractTableModel getTableModel() {

    return new AbstractTableModel() {
      public int getColumnCount() {

        return 3;
      }

      public int getRowCount() {

        return 30;
      }

      public String getColumnName(int column) {

        if (column == 0) return resourceBundle.getString("MoreEngines.column0");
        if (column == 1) return resourceBundle.getString("MoreEngines.column1");
        if (column == 2) return resourceBundle.getString("MoreEngines.column2");

        return "";
      }

      public Object getValueAt(int row, int col) {
        ArrayList<ProgramData> ProgramDatas = getProgramData();
        if (row > (ProgramDatas.size() - 1)) {
          if (col == 0) return row + 1;
          return "";
        }
        ProgramData data = ProgramDatas.get(row);

        if (col != 0 && data.commands.equals("")) {
          return "";
        }

        switch (col) {
          case 0:
            return row + 1;
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

  public static JDialog createDialog() {
    // Create and set up the window.
    engjf = new JDialog();
    engjf.setTitle(Lizzie.resourceBundle.getString("OtherPrograms.title")); // ("快速启动设置");

    engjf.addWindowListener(
        new WindowAdapter() {
          public void windowClosing(WindowEvent e) {
            engjf.setVisible(false);
          }
        });

    final OtherPrograms newContentPane = new OtherPrograms();
    newContentPane.setOpaque(true); // content panes must be opaque
    engjf.setContentPane(newContentPane);
    // Display the window.
    // jf.setSize(521, 320);

    // boolean persisted = Lizzie.config.persistedUi != null;
    // if (persisted
    // && Lizzie.config.persistedUi.optJSONArray("badmoves-list-position") != null
    // && Lizzie.config.persistedUi.optJSONArray("badmoves-list-position").length()
    // >= 4) {
    // JSONArray pos =
    // Lizzie.config.persistedUi.getJSONArray("badmoves-list-position");
    // // jf.setBounds(pos.getInt(0), pos.getInt(1), pos.getInt(2), pos.getInt(3));
    // engjf.setBounds(50, 50, 900, 800);
    // } else {
    // engjf.setBounds(50, 50, 900, 800);
    Lizzie.setFrameSize(engjf, 891, 791);
    engjf.setResizable(false);
    // }
    try {
      engjf.setIconImage(ImageIO.read(OtherPrograms.class.getResourceAsStream("/assets/logo.png")));
    } catch (IOException e) {
      e.printStackTrace();
    }
    engjf.setAlwaysOnTop(Lizzie.frame.isAlwaysOnTop());
    engjf.setLocationRelativeTo(engjf.getOwner());
    // jf.setResizable(false);
    return engjf;
  }
}
