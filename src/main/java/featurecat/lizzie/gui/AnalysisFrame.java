package featurecat.lizzie.gui;

import featurecat.lizzie.Config;
import featurecat.lizzie.Lizzie;
import featurecat.lizzie.analysis.EngineManager;
import featurecat.lizzie.analysis.Leelaz;
import featurecat.lizzie.analysis.MoveData;
import featurecat.lizzie.rules.Board;
import featurecat.lizzie.rules.BoardHistoryNode;
import featurecat.lizzie.util.Utils;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowStateListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;
import org.json.JSONArray;

@SuppressWarnings("serial")
public class AnalysisFrame extends JFrame {
  private final ResourceBundle resourceBundle =
      Lizzie.config.useLanguage == 0
          ? ResourceBundle.getBundle("l10n.DisplayStrings")
          : (Lizzie.config.useLanguage == 1
              ? ResourceBundle.getBundle("l10n.DisplayStrings", new Locale("zh", "CN"))
              : ResourceBundle.getBundle("l10n.DisplayStrings", new Locale("en", "US")));
  TableModel dataModel;
  JScrollPane scrollpane;
  JPanel topPanel;
  JPanel bottomPanel;
  public JTable table;
  Timer timer;
  int sortnum = -1;
  public int selectedorder = -1;
  public int clickOrder = -1;
  int currentRow = -1;
  Font winrateFont;
  Font headFont;
  int index;
  private String oriTitle = "";

  public AnalysisFrame(int engine) {
    index = engine;
    dataModel = getTableModel();
    if (Lizzie.config.isDoubleEngineMode()) {
      if (index == 1) oriTitle = resourceBundle.getString("AnalysisFrame.titleMain");
      else if (index == 2) oriTitle = resourceBundle.getString("AnalysisFrame.titleSub");
    } else oriTitle = resourceBundle.getString("AnalysisFrame.title");
    setTitle(oriTitle);
    setAlwaysOnTop(Lizzie.config.suggestionsalwaysontop || Lizzie.frame.isAlwaysOnTop());
    setTopTitle();
    // JDialog dialog = new JDialog(owner,
    // "单击显示紫圈(小棋盘显示变化),右键落子,双击显示后续变化图,快捷键U显示/关闭");
    addWindowListener(
        new WindowAdapter() {
          public void windowClosing(WindowEvent e) {
            Lizzie.frame.toggleBestMoves();
          }
        });

    // Create and set up the content pane.
    // final AnalysisFrame newContentPane = new AnalysisFrame();
    // newContentPane.setOpaque(true); // content panes must be opaque
    // setContentPane(newContentPane);
    // Display the window.
    // jfs.setSize(521, 285);

    try {
      setIconImage(ImageIO.read(getClass().getResourceAsStream("/assets/logo.png")));
    } catch (IOException e) {
      e.printStackTrace();
    }
    table = new JTable(dataModel);

    winrateFont = new Font("Microsoft YaHei", Font.BOLD, Math.max(Config.frameFontSize, 14));
    headFont = new Font("Microsoft YaHei", Font.PLAIN, Math.max(Config.frameFontSize, 13));

    table.getTableHeader().setFont(headFont);
    table.getTableHeader().setReorderingAllowed(false);
    table.setFont(winrateFont);
    table.setRowHeight(Config.menuHeight);
    TableCellRenderer tcr = new ColorTableCellRenderer();
    table.setDefaultRenderer(Object.class, tcr);

    ((DefaultTableCellRenderer) table.getTableHeader().getDefaultRenderer())
        .setHorizontalAlignment(JLabel.CENTER);
    scrollpane = new JScrollPane(table);
    topPanel = new JPanel(new BorderLayout());
    topPanel.add(scrollpane);

    bottomPanel =
        new JPanel(true) {
          @Override
          protected void paintComponent(Graphics g) {
            // super.paintComponent(g);
            paintBottomPanel(g, bottomPanel.getWidth(), bottomPanel.getHeight());
          }
        };
    bottomPanel.setLayout(null);

    JCheckBox checkList = new JCheckBox(resourceBundle.getString("AnalysisFrame.checkList"));
    bottomPanel.add(checkList);
    checkList.setBounds(370, 0, 50, 18);
    checkList.setFocusable(false);
    checkList.setMargin(new Insets(0, 0, 0, 0));
    checkList.setSelected(Lizzie.config.showBestMovesList);
    checkList.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            Lizzie.config.showBestMovesList = checkList.isSelected();
            Lizzie.frame.toggleBestMoves();
            Lizzie.frame.toggleBestMoves();
            Lizzie.config.uiConfig.put("show-bestmoves-list", Lizzie.config.showBestMovesList);
          }
        });

    JCheckBox checkGraph = new JCheckBox(resourceBundle.getString("AnalysisFrame.checkGraph"));
    bottomPanel.add(checkGraph);
    checkGraph.setBounds(420, 0, 65, 18);
    checkGraph.setFocusable(false);
    checkGraph.setMargin(new Insets(0, 0, 0, 0));
    checkGraph.setSelected(Lizzie.config.showBestMovesGraph);
    checkGraph.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            Lizzie.config.showBestMovesGraph = checkGraph.isSelected();
            Lizzie.frame.toggleBestMoves();
            Lizzie.frame.toggleBestMoves();
            Lizzie.config.uiConfig.put("show-bestmoves-graph", Lizzie.config.showBestMovesGraph);
          }
        });

    JCheckBox checkShowNext =
        new JCheckBox(resourceBundle.getString("AnalysisFrame.checkShowNext"));
    bottomPanel.add(checkShowNext);
    checkShowNext.setBounds(485, 0, 65, 18);
    checkShowNext.setFocusable(false);
    checkShowNext.setMargin(new Insets(0, 0, 0, 0));
    checkShowNext.setSelected(Lizzie.config.anaFrameShowNext);
    checkShowNext.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            Lizzie.config.anaFrameShowNext = checkShowNext.isSelected();
            Lizzie.frame.toggleBestMoves();
            Lizzie.frame.toggleBestMoves();
            Lizzie.config.uiConfig.put("anaframe-show-next", Lizzie.config.anaFrameShowNext);
          }
        });

    JCheckBox checkUseMouseMove =
        new JCheckBox(resourceBundle.getString("AnalysisFrame.checkUseMouseMove"));
    bottomPanel.add(checkUseMouseMove);
    checkUseMouseMove.setBounds(550, 0, 95, 18);
    checkUseMouseMove.setFocusable(false);
    checkUseMouseMove.setMargin(new Insets(0, 0, 0, 0));
    checkUseMouseMove.setSelected(Lizzie.config.anaFrameUseMouseMove);
    checkUseMouseMove.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            Lizzie.config.anaFrameUseMouseMove = checkUseMouseMove.isSelected();
            Lizzie.frame.toggleBestMoves();
            Lizzie.frame.toggleBestMoves();
            Lizzie.config.uiConfig.put(
                "anaframe-use-mousemove", Lizzie.config.anaFrameUseMouseMove);
          }
        });

    //    anaFrameShowNext = uiConfig.optBoolean("anaframe-show-next", true);
    // anaFrameUseMouseMove = uiConfig.optBoolean("anaframe-use-mousemove", true);

    this.getContentPane().setLayout(new BorderLayout());
    this.getContentPane().add(bottomPanel, BorderLayout.SOUTH);
    if (!Lizzie.config.showBestMovesList) {
    } else this.getContentPane().add(topPanel, BorderLayout.NORTH);

    this.addComponentListener(
        new ComponentAdapter() {
          public void componentResized(ComponentEvent e) {
            int height = getContentPane().getHeight();

            if (!Lizzie.config.showBestMovesList) {
              getContentPane().remove(topPanel);
              bottomPanel.setPreferredSize(new Dimension(getWidth(), height));
            } else if (Lizzie.config.showBestMovesGraph) {
              topPanel.setPreferredSize(new Dimension(getWidth(), height / 2));
              bottomPanel.setPreferredSize(new Dimension(getWidth(), height / 2));
            } else {
              topPanel.setPreferredSize(new Dimension(getWidth(), height - 22));
              bottomPanel.setPreferredSize(new Dimension(getWidth(), 22));
            }
          }
        });

    this.addWindowStateListener(
        new WindowStateListener() {
          public void windowStateChanged(WindowEvent state) {
            int height = getHeight() - 40;
            if (!Lizzie.config.showBestMovesList) {
              getContentPane().remove(topPanel);
              bottomPanel.setPreferredSize(new Dimension(getWidth(), height));
            } else if (Lizzie.config.showBestMovesGraph) {
              topPanel.setPreferredSize(new Dimension(getWidth(), height / 2));
              bottomPanel.setPreferredSize(new Dimension(getWidth(), height / 2));
            } else {
              topPanel.setPreferredSize(new Dimension(getWidth(), height - 22));
              bottomPanel.setPreferredSize(new Dimension(getWidth(), 22));
            }
          }
        });

    timer =
        new Timer(
            100,
            new ActionListener() {
              public void actionPerformed(ActionEvent evt) {
                dataModel.getColumnCount();
                repaint();
                // table.validate();
                // table.updateUI();
              }
            });
    timer.start();

    table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    table.setFillsViewportHeight(true);
    table.getColumnModel().getColumn(0).setPreferredWidth(35);
    table.getColumnModel().getColumn(1).setPreferredWidth(20);
    table.getColumnModel().getColumn(2).setPreferredWidth(40);
    table.getColumnModel().getColumn(3).setPreferredWidth(70);
    table.getColumnModel().getColumn(4).setPreferredWidth(35);
    table.getColumnModel().getColumn(5).setPreferredWidth(50);
    table.getColumnModel().getColumn(6).setPreferredWidth(35);
    if (table.getColumnCount() == 9) {
      table.getColumnModel().getColumn(7).setPreferredWidth(70);
      table.getColumnModel().getColumn(8).setPreferredWidth(35);
    }
    boolean persisted = Lizzie.config.persistedUi != null;

    if (persisted) {

      if (table.getColumnCount() == 9) {
        if (Lizzie.config.persistedUi.optJSONArray("suggestions-list-position-9") != null
            && Lizzie.config.persistedUi.optJSONArray("suggestions-list-position-9").length()
                == 13) {
          JSONArray pos = Lizzie.config.persistedUi.getJSONArray("suggestions-list-position-9");
          table.getColumnModel().getColumn(0).setPreferredWidth(pos.getInt(4));
          table.getColumnModel().getColumn(1).setPreferredWidth(pos.getInt(5));
          table.getColumnModel().getColumn(2).setPreferredWidth(pos.getInt(6));
          table.getColumnModel().getColumn(3).setPreferredWidth(pos.getInt(7));
          table.getColumnModel().getColumn(4).setPreferredWidth(pos.getInt(8));
          table.getColumnModel().getColumn(5).setPreferredWidth(pos.getInt(9));
          table.getColumnModel().getColumn(6).setPreferredWidth(pos.getInt(10));
          table.getColumnModel().getColumn(7).setPreferredWidth(pos.getInt(11));
          table.getColumnModel().getColumn(8).setPreferredWidth(pos.getInt(12));
          setBounds(pos.getInt(0), pos.getInt(1), pos.getInt(2), pos.getInt(3));
        } else {
          setBounds(-9, 550, 650, 320);
        }
      } else if (Lizzie.config.persistedUi.optJSONArray("suggestions-list-position-7") != null
          && Lizzie.config.persistedUi.optJSONArray("suggestions-list-position-7").length() == 11) {
        JSONArray pos = Lizzie.config.persistedUi.getJSONArray("suggestions-list-position-7");
        table.getColumnModel().getColumn(0).setPreferredWidth(pos.getInt(4));
        table.getColumnModel().getColumn(1).setPreferredWidth(pos.getInt(5));
        table.getColumnModel().getColumn(2).setPreferredWidth(pos.getInt(6));
        table.getColumnModel().getColumn(3).setPreferredWidth(pos.getInt(7));
        table.getColumnModel().getColumn(4).setPreferredWidth(pos.getInt(8));
        table.getColumnModel().getColumn(5).setPreferredWidth(pos.getInt(9));
        table.getColumnModel().getColumn(6).setPreferredWidth(pos.getInt(10));
        setBounds(pos.getInt(0), pos.getInt(1), pos.getInt(2), pos.getInt(3));
        Dimension screensize = Toolkit.getDefaultToolkit().getScreenSize();
        int widths = (int) screensize.getWidth();
        int heights = (int) screensize.getHeight();
        if (pos.getInt(0) >= widths || pos.getInt(1) >= heights) this.setLocation(0, 0);
      } else {
        setBounds(-9, 480, 650, 320);
      }
    } else {
      setBounds(-9, 480, 650, 320);
    }

    if (!Lizzie.config.showBestMovesList) {
      bottomPanel.setPreferredSize(new Dimension(this.getWidth(), this.getHeight() - 40));
    }
    setVisible(false);
    setVisible(true);
    if (Lizzie.config.isDoubleEngineMode()) {
      if (index == 2) {
        if (Lizzie.frame.analysisFrame != null)
          this.setLocation(
              Lizzie.frame.analysisFrame.getX() + Lizzie.frame.analysisFrame.getWidth(),
              Lizzie.frame.analysisFrame.getY());
      }
    }
    JTableHeader header = table.getTableHeader();

    table.addMouseMotionListener(
        new MouseMotionListener() {
          @Override
          public void mouseDragged(MouseEvent e) {
            // TODO Auto-generated method stub
            mouseMoved(e);
          }

          @Override
          public void mouseMoved(MouseEvent e) {
            // TODO Auto-generated method stub
            if (!Lizzie.config.anaFrameUseMouseMove || clickOrder != -1) return;
            Point p = e.getPoint();
            int row = table.rowAtPoint(p);
            if (row < 0) {
              if (Lizzie.frame.suggestionclick != LizzieFrame.outOfBoundCoordinate) {
                LizzieFrame.boardRenderer.startNormalBoard();
                LizzieFrame.boardRenderer.clearBranch();
                Lizzie.frame.suggestionclick = LizzieFrame.outOfBoundCoordinate;
                Lizzie.frame.mouseOverCoordinate = LizzieFrame.outOfBoundCoordinate;
                selectedorder = -1;
                currentRow = -1;
                clickOrder = -1;
                Lizzie.frame.refresh();
              }
              return;
            }
            if (table.getValueAt(row, 1).toString().startsWith("pass")) return;
            if (selectedorder >= 0
                && Board.convertNameToCoordinates(table.getValueAt(row, 1).toString())[0]
                    == Lizzie.frame.suggestionclick[0]
                && Board.convertNameToCoordinates(table.getValueAt(row, 1).toString())[1]
                    == Lizzie.frame.suggestionclick[1]) {
            } else {
              LizzieFrame.boardRenderer.startNormalBoard();
              selectedorder = row;
              currentRow = row;
              int[] coords = Board.convertNameToCoordinates(table.getValueAt(row, 1).toString());
              Lizzie.frame.mouseOverCoordinate = coords;
              Lizzie.frame.suggestionclick = coords;
              Lizzie.frame.refresh();
            }
          }
        });

    table.addMouseWheelListener(
        new MouseWheelListener() {
          @Override
          public void mouseWheelMoved(MouseWheelEvent e) {
            // TODO Auto-generated method stub
            if (clickOrder != -1) {
              if (e.getWheelRotation() > 0) {
                Lizzie.frame.doBranch(1);
              } else if (e.getWheelRotation() < 0) {
                Lizzie.frame.doBranch(-1);
              }
              Lizzie.frame.refresh();
            } else {
              scrollpane.dispatchEvent(e);
            }
          }
        });
    table.addMouseListener(
        new MouseAdapter() {
          public void mouseExited(MouseEvent e) {
            if (!Lizzie.config.anaFrameUseMouseMove || clickOrder != -1) return;
            if (Lizzie.frame.suggestionclick != LizzieFrame.outOfBoundCoordinate) {
              LizzieFrame.boardRenderer.startNormalBoard();
              LizzieFrame.boardRenderer.clearBranch();
              Lizzie.frame.suggestionclick = LizzieFrame.outOfBoundCoordinate;
              Lizzie.frame.mouseOverCoordinate = LizzieFrame.outOfBoundCoordinate;
              selectedorder = -1;
              currentRow = -1;
              Lizzie.frame.refresh();
            }
          }

          public void mouseClicked(MouseEvent e) {
            int row = table.rowAtPoint(e.getPoint());
            int col = table.columnAtPoint(e.getPoint());
            //            if (e.getClickCount() == 2) {
            //              if (row >= 0 && col >= 0) {
            //                try {
            //                  handleTableDoubleClick(row, col);
            //                } catch (Exception ex) {
            //                  ex.printStackTrace();
            //                }
            //              }
            //            } else {

            if (row >= 0 && col >= 0) {
              if (e.getButton() == MouseEvent.BUTTON3) {
                try {
                  handleTableRightClick(row, col);
                } catch (Exception ex) {
                  ex.printStackTrace();
                }
              } else
                try {
                  handleTableClick(row, col);
                } catch (Exception ex) {
                  ex.printStackTrace();
                }
            }
          }
          //    }
        });
    table.addKeyListener(
        new KeyAdapter() {
          public void keyPressed(KeyEvent e) {
            if (e.getKeyCode() == KeyEvent.VK_U) {
              Lizzie.frame.toggleBestMoves();
            }
            if (e.getKeyCode() == KeyEvent.VK_UP) {
              Lizzie.board.previousMove(true);
            }
            if (e.getKeyCode() == KeyEvent.VK_DOWN) {
              Lizzie.board.nextMove(true);
            }
            if (e.getKeyCode() == KeyEvent.VK_Y) {
              Lizzie.frame.toggleBadMoves();
            }
            if (e.getKeyCode() == KeyEvent.VK_SPACE) {
              Lizzie.frame.togglePonderMannul();
            }

            if (e.getKeyCode() == KeyEvent.VK_Q) {
              Lizzie.frame.toggleAnalysisFrameAlwaysontop();
            }
          }
        });
    addKeyListener(
        new KeyAdapter() {
          public void keyPressed(KeyEvent e) {
            if (e.getKeyCode() == KeyEvent.VK_U) {
              Lizzie.frame.toggleBestMoves();
            }
            if (e.getKeyCode() == KeyEvent.VK_Y) {
              Lizzie.frame.toggleBadMoves();
            }
            if (e.getKeyCode() == KeyEvent.VK_SPACE) {
              Lizzie.frame.togglePonderMannul();
            }

            if (e.getKeyCode() == KeyEvent.VK_Q) {
              Lizzie.frame.toggleAnalysisFrameAlwaysontop();
            }
          }
        });

    header.addMouseListener(
        new MouseAdapter() {
          public void mouseReleased(MouseEvent e) {
            int pick = header.columnAtPoint(e.getPoint());
            sortnum = pick;
          }
        });
  }

  public void setTopTitle() {
    if (this.isAlwaysOnTop())
      setTitle(Lizzie.resourceBundle.getString("Lizzie.alwaysOnTopTitle") + oriTitle);
    else setTitle(oriTitle);
  }

  private void paintBottomPanel(Graphics g0, int width, int height) {
    // TODO Auto-generated method stub
    int minHeight = 22;
    int trueHeight = height - minHeight;
    int totalPlayouts = 0;
    int maxPlayouts = 0;
    double stable = 0;
    List<MoveData> bestMoves = null;
    if (index == 1) bestMoves = Lizzie.board.getData().bestMoves;
    else if (index == 2) bestMoves = Lizzie.board.getData().bestMoves2;
    if (bestMoves == null || bestMoves.isEmpty()) return;
    Graphics2D g = (Graphics2D) g0;
    g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    for (MoveData move : bestMoves) {
      totalPlayouts += move.playouts;
      if (move.playouts > maxPlayouts) maxPlayouts = move.playouts;
    }
    int length = bestMoves.size();
    for (int i = 0; i < 11; i++) {
      if (i < length)
        stable +=
            (maxPlayouts - bestMoves.get(i).playouts) * (maxPlayouts - bestMoves.get(i).playouts);
      else stable += maxPlayouts * maxPlayouts;
    }
    stable = stable / 10 / totalPlayouts / totalPlayouts;
    stable = Math.pow(stable, 0.5) * 100;
    g.setColor(Color.BLACK);
    g.setFont(new Font(Lizzie.config.uiFontName, Font.PLAIN, 13));
    g.drawString(
        resourceBundle.getString("AnalysisFrame.totalVisits")
            + Utils.getPlayoutsString(totalPlayouts)
            + " "
            + resourceBundle.getString("AnalysisFrame.maxVisits")
            + Utils.getPlayoutsString(maxPlayouts)
            + " "
            + resourceBundle.getString("AnalysisFrame.concentration")
            + String.format(Locale.ENGLISH, "%.2f", stable)
            + "%",
        5,
        15);
    if (Lizzie.config.showBestMovesGraph)
    // 画横向柱状图,前X选点
    {
      g.setStroke(new BasicStroke(1));
      g.drawLine(0, 20, width, 20);
      int nums = trueHeight / 20;
      for (int i = 0; i < nums; i++) {
        g.drawString(String.valueOf(i + 1), 3, minHeight + i * 20 + 15);
        g.setColor(Color.DARK_GRAY);
        if (i < length) {
          double percents = (double) bestMoves.get(i).playouts / maxPlayouts;
          g.fillRect(20, minHeight + i * 20 + 2, (int) ((width - 80) * percents), 16);
          g.drawString(
              String.format(
                      Locale.ENGLISH,
                      "%.2f",
                      (double) bestMoves.get(i).playouts * 100 / totalPlayouts)
                  + "%",
              26 + (int) ((width - 80) * percents),
              minHeight + i * 20 + 15);
        }
      }
    }
    //    if (Config.isScaled) {
    //      Graphics2D g1 = (Graphics2D) g0;
    //      g1.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
    //      g1.setRenderingHint(KEY_ANTIALIASING, VALUE_ANTIALIAS_ON);
    //      g1.drawImage(cachedImage, 0, 0, null);
    //    } else g0.drawImage(cachedImage, 0, 0, null);
    //    g.dispose();
  }

  class ColorTableCellRenderer extends DefaultTableCellRenderer {
    Object mainValue;
    boolean isPlayoutPercents = false;
    boolean isSelect = false;
    boolean isChanged = false;
    boolean isNextMove;
    double diff = 0;
    double scoreDiff = 0;

    public Component getTableCellRendererComponent(
        JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
      // if(row%2 == 0){
      setHorizontalAlignment(CENTER);
      if (column == 5) {
        isPlayoutPercents = true;
        mainValue = value;
      } else {
        isPlayoutPercents = false;
      }
      if (table.getValueAt(row, 0).toString().length() > 3) {
        isNextMove = true;
        String winrate = table.getValueAt(row, 3).toString();
        if (winrate.contains("("))
          diff = Double.parseDouble(winrate.substring(1, winrate.indexOf("(")));
        else diff = 0;
        if (table.getColumnCount() > 7) {
          String score = table.getValueAt(row, 7).toString();
          if (score.contains("("))
            scoreDiff = Double.parseDouble(score.substring(1, score.indexOf("(")));
          else scoreDiff = 0;
        } else scoreDiff = 0;
      } else isNextMove = false;

      String coordsName = table.getValueAt(row, 1).toString();
      int[] coords = new int[] {-2, -2};
      if (!coordsName.startsWith("pas") && coordsName.length() > 1) {
        coords = Board.convertNameToCoordinates(coordsName);
      }
      if (coords[0] == Lizzie.frame.suggestionclick[0]
          && coords[1] == Lizzie.frame.suggestionclick[1]) {
        if (selectedorder >= 0 && selectedorder != row) {
          currentRow = row;
          // selectedorder = -1;
          isChanged = true;
          // setForeground(Color.RED);
        } else {
          isChanged = false;
        }
        isSelect = true;
        // setBackground(new Color(238, 221, 130));
        return super.getTableCellRendererComponent(table, value, false, false, row, column);

      } else {
        isSelect = false;
        isChanged = false;
        return super.getTableCellRendererComponent(table, value, false, false, row, column);
      }
    }

    public void paintComponent(Graphics g) {

      if (isPlayoutPercents) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setColor(Color.LIGHT_GRAY);
        g2.fillRect(
            0,
            0,
            (int) (getWidth() * (Double.parseDouble(mainValue.toString()) / 100)),
            getHeight());

      } else {
        if (isSelect) {
          setForeground(Color.BLUE);
          setBackground(new Color(0, 0, 0, 35));
        }
        if (isChanged) {
          setForeground(Color.RED);
        }
        if (isNextMove) {
          if (isSelect) {
            if (diff <= Lizzie.config.winLossThreshold5
                || scoreDiff <= Lizzie.config.scoreLossThreshold5)
              setBackground(new Color(85, 25, 80, 120));
            else if (diff <= Lizzie.config.winLossThreshold4
                || scoreDiff <= Lizzie.config.scoreLossThreshold4)
              setBackground(new Color(208, 16, 19, 100));
            else if (diff <= Lizzie.config.winLossThreshold3
                || scoreDiff <= Lizzie.config.scoreLossThreshold3)
              setBackground(new Color(200, 140, 50, 100));
            else if (diff <= Lizzie.config.winLossThreshold2
                || scoreDiff <= Lizzie.config.scoreLossThreshold2)
              setBackground(new Color(180, 180, 0, 100));
            else if (diff <= Lizzie.config.winLossThreshold1
                || scoreDiff <= Lizzie.config.scoreLossThreshold1)
              setBackground(new Color(140, 202, 34, 100));
            else setBackground(new Color(0, 180, 0, 100));
          } else {
            if (diff <= Lizzie.config.winLossThreshold5
                || scoreDiff <= Lizzie.config.scoreLossThreshold5)
              setBackground(new Color(85, 25, 80, 70));
            else if (diff <= Lizzie.config.winLossThreshold4
                || scoreDiff <= Lizzie.config.scoreLossThreshold4)
              setBackground(new Color(208, 16, 19, 50));
            else if (diff <= Lizzie.config.winLossThreshold3
                || scoreDiff <= Lizzie.config.scoreLossThreshold3)
              setBackground(new Color(200, 140, 50, 50));
            else if (diff <= Lizzie.config.winLossThreshold2
                || scoreDiff <= Lizzie.config.scoreLossThreshold2)
              setBackground(new Color(180, 180, 0, 50));
            else if (diff <= Lizzie.config.winLossThreshold1
                || scoreDiff <= Lizzie.config.scoreLossThreshold1)
              setBackground(new Color(140, 202, 34, 50));
            else setBackground(new Color(0, 180, 0, 60));
          }
        } else if (!isSelect && !isChanged) {
          setForeground(Color.BLACK);
          setBackground(Color.WHITE);
        }
      }
      super.paintComponent(g);
    }
  }

  private void handleTableClick(int row, int col) {
    LizzieFrame.boardRenderer.startNormalBoard();
    if (table.getValueAt(row, 1).toString().startsWith("pass")) return;
    if (clickOrder != -1
        && selectedorder >= 0
        && Board.convertNameToCoordinates(table.getValueAt(row, 1).toString())[0]
            == Lizzie.frame.suggestionclick[0]
        && Board.convertNameToCoordinates(table.getValueAt(row, 1).toString())[1]
            == Lizzie.frame.suggestionclick[1]) {
      Lizzie.frame.suggestionclick = LizzieFrame.outOfBoundCoordinate;
      Lizzie.frame.mouseOverCoordinate = LizzieFrame.outOfBoundCoordinate;
      LizzieFrame.boardRenderer.clearBranch();
      selectedorder = -1;
      clickOrder = -1;
      currentRow = -1;
      Lizzie.frame.refresh();
    } else {

      clickOrder = row;
      selectedorder = row;
      currentRow = row;
      int[] coords = Board.convertNameToCoordinates(table.getValueAt(row, 1).toString());
      Lizzie.frame.mouseOverCoordinate = coords;
      Lizzie.frame.suggestionclick = coords;
      Lizzie.frame.refresh();
    }
  }

  private void handleTableRightClick(int row, int col) {
    if (table.getValueAt(row, 1).toString().startsWith("pass")) return;
    if (selectedorder != row) {
      int[] coords = Board.convertNameToCoordinates(table.getValueAt(row, 1).toString());
      Lizzie.frame.suggestionclick = coords;
      Lizzie.frame.mouseOverCoordinate = LizzieFrame.outOfBoundCoordinate;
      Lizzie.frame.refresh();
      selectedorder = row;
    } else {
      Lizzie.frame.suggestionclick = LizzieFrame.outOfBoundCoordinate;
      Lizzie.frame.refresh();
      selectedorder = -1;
    }
  }

  public AbstractTableModel getTableModel() {
    return new AbstractTableModel() {
      ArrayList<MoveData> data2 = new ArrayList<MoveData>();
      List<MoveData> bestMoves;

      public int getColumnCount() {
        Leelaz leelaz = null;
        if (index == 1) {
          leelaz = Lizzie.leelaz;
        } else if (index == 2) {
          leelaz = Lizzie.leelaz2;
        }
        if (leelaz != null && (leelaz.isKatago || leelaz.isSai)) return 9;
        else if (index == 1
            ? Lizzie.board.isContainsKataData()
            : Lizzie.board.isContainsKataData2()) return 9;
        else return 7;
      }

      public int getRowCount() {
        data2 = new ArrayList<MoveData>();
        if (index == 1) {
          if (EngineManager.isEngineGame && Lizzie.config.showPreviousBestmovesInEngineGame) {
            if (Lizzie.board.getHistory().getCurrentHistoryNode().previous().isPresent())
              if ((bestMoves = Lizzie.leelaz.getBestMoves()).isEmpty())
                bestMoves =
                    Lizzie.board
                        .getHistory()
                        .getCurrentHistoryNode()
                        .previous()
                        .get()
                        .getData()
                        .bestMoves;

          } else bestMoves = Lizzie.board.getHistory().getCurrentHistoryNode().getData().bestMoves;
          if (bestMoves != null)
            for (int i = 0; i < bestMoves.size(); i++) {
              // if (!Lizzie.board.getData().bestMoves.get(i).coordinate.contains("ass"))
              data2.add(bestMoves.get(i));
            }
        } else if (index == 2) {
          if (Lizzie.board.getData().bestMoves2 != null) {
            for (int i = 0; i < Lizzie.board.getData().bestMoves2.size(); i++) {
              // if (!Lizzie.board.getData().bestMoves2.get(i).coordinate.contains("ass"))
              data2.add(Lizzie.board.getData().bestMoves2.get(i));
            }
          }
        }
        if (Lizzie.config.anaFrameShowNext
            && Lizzie.board.getHistory().getCurrentHistoryNode().next().isPresent()) {
          BoardHistoryNode next = Lizzie.board.getHistory().getCurrentHistoryNode().next().get();
          if (next.getData().lastMove.isPresent()) {
            int[] coords = next.getData().lastMove.get();
            boolean hasData = false;
            for (MoveData move : data2) {
              if (Board.convertNameToCoordinates(move.coordinate)[0] == coords[0]
                  && Board.convertNameToCoordinates(move.coordinate)[1] == coords[1]) {
                if (move.order == 0) {
                  move.winrate = data2.get(0).winrate;
                  move.isNextMove = true;
                  move.bestWinrate = data2.get(0).winrate;
                  move.bestScoreMean = data2.get(0).scoreMean;
                } else {
                  if (index == 1) {
                    if (data2.size() > 0
                        && !hasData
                        && !next.getData().bestMoves.isEmpty()
                        && next.getData().getPlayouts() > move.playouts) {
                      MoveData curMove = new MoveData();
                      curMove.playouts = next.getData().getPlayouts();
                      curMove.coordinate = Board.convertCoordinatesToName(coords[0], coords[1]);
                      curMove.winrate = 100.0 - next.getData().winrate;
                      curMove.policy = 0;
                      curMove.scoreMean = -next.getData().scoreMean;
                      curMove.scoreStdev = 0;
                      curMove.order = move.order;
                      curMove.isNextMove = true;
                      curMove.lcb = 0;
                      curMove.bestWinrate = data2.get(0).winrate;
                      curMove.bestScoreMean = data2.get(0).scoreMean;
                      data2.add(0, curMove);
                      hasData = true;
                      break;
                    }
                  } else {
                    if (data2.size() > 0
                        && !hasData
                        && !next.getData().bestMoves2.isEmpty()
                        && next.getData().getPlayouts2() > move.playouts) {
                      MoveData curMove = new MoveData();
                      curMove.playouts = next.getData().getPlayouts2();
                      curMove.coordinate = Board.convertCoordinatesToName(coords[0], coords[1]);
                      curMove.winrate = 100.0 - next.getData().winrate2;
                      curMove.policy = 0;
                      curMove.scoreMean = -next.getData().scoreMean2;
                      curMove.scoreStdev = 0;
                      curMove.order = move.order;
                      curMove.isNextMove = true;
                      curMove.lcb = 0;
                      curMove.bestWinrate = data2.get(0).winrate;
                      curMove.bestScoreMean = data2.get(0).scoreMean;
                      data2.add(0, curMove);
                      hasData = true;
                      break;
                    }
                  }
                  MoveData curMove = new MoveData();
                  curMove.order = move.order;
                  curMove.playouts = move.playouts;
                  curMove.coordinate = move.coordinate;
                  curMove.winrate = move.winrate;
                  curMove.policy = move.policy;
                  curMove.scoreMean = move.scoreMean;
                  curMove.scoreStdev = move.scoreStdev;
                  curMove.order = move.order;
                  curMove.isNextMove = true;
                  curMove.lcb = move.lcb;
                  curMove.bestWinrate = data2.get(0).winrate;
                  curMove.bestScoreMean = data2.get(0).scoreMean;
                  data2.add(0, curMove);
                }
                hasData = true;
                break;
              }
            }
            if (index == 1) {
              if (data2.size() > 0 && !hasData && !next.getData().bestMoves.isEmpty()) {
                MoveData curMove = new MoveData();
                curMove.playouts = 0;
                curMove.coordinate = Board.convertCoordinatesToName(coords[0], coords[1]);
                curMove.winrate = 100.0 - next.getData().winrate;
                curMove.policy = 0;
                curMove.scoreMean = -next.getData().scoreMean;
                curMove.scoreStdev = 0;
                curMove.order = -100;
                curMove.isNextMove = true;
                curMove.lcb = 0;
                curMove.bestWinrate = data2.get(0).winrate;
                curMove.bestScoreMean = data2.get(0).scoreMean;
                data2.add(0, curMove);
              }
            } else {
              if (data2.size() > 0 && !hasData && !next.getData().bestMoves2.isEmpty()) {
                MoveData curMove = new MoveData();
                curMove.playouts = 0;
                curMove.coordinate = Board.convertCoordinatesToName(coords[0], coords[1]);
                curMove.winrate = 100.0 - next.getData().winrate2;
                curMove.policy = 0;
                curMove.scoreMean = -next.getData().scoreMean2;
                curMove.scoreStdev = 0;
                curMove.order = -100;
                curMove.isNextMove = true;
                curMove.lcb = 0;
                curMove.bestWinrate = data2.get(0).winrate;
                curMove.bestScoreMean = data2.get(0).scoreMean;
                data2.add(0, curMove);
              }
            }
          }
        }

        if (Lizzie.frame.isInPlayMode()) return 0;

        return data2.size();
      }

      public String getColumnName(int column) {
        if (column == 0) return resourceBundle.getString("AnalysisFrame.column1"); // "序号";
        if (column == 1) return resourceBundle.getString("AnalysisFrame.column2"); // "坐标";
        if (column == 2) return resourceBundle.getString("AnalysisFrame.column3"); // "Lcb(%)";
        if (column == 3) return resourceBundle.getString("AnalysisFrame.column4"); // "胜率(%)";
        if (column == 4) return resourceBundle.getString("AnalysisFrame.column5"); // "计算量";
        if (column == 5) return resourceBundle.getString("AnalysisFrame.column6"); // "占比(%)";
        if (column == 6) return resourceBundle.getString("AnalysisFrame.column7"); // "策略网络(%)";
        if (column == 7) return resourceBundle.getString("AnalysisFrame.column8"); // "目差";
        if (column == 8) return resourceBundle.getString("AnalysisFrame.column9"); // "局面复杂度";
        return "";
      }

      public Object getValueAt(int row, int col) {

        // Collections.sort(data2) ;
        Collections.sort(
            data2,
            new Comparator<MoveData>() {

              @Override
              public int compare(MoveData s1, MoveData s2) {
                // 降序
                //            	  if (sortnum == 0) {
                //                      if (s1.order > s2.order) return 1;
                //                      if (s1.order < s2.order) return -1;
                //                    }
                if (sortnum == 2) {
                  if (s1.lcb < s2.lcb) return 1;
                  if (s1.lcb > s2.lcb) return -1;
                }
                if (sortnum == 3) {
                  if (s1.winrate < s2.winrate) return 1;
                  if (s1.winrate > s2.winrate) return -1;
                }
                if (sortnum == 4) {
                  if (s1.playouts < s2.playouts) return 1;
                  if (s1.playouts > s2.playouts) return -1;
                }
                if (sortnum == 5) {
                  if (s1.playouts < s2.playouts) return 1;
                  if (s1.playouts > s2.playouts) return -1;
                }
                if (sortnum == 6) {
                  if (s1.policy < s2.policy) return 1;
                  if (s1.policy > s2.policy) return -1;
                }
                if (sortnum == 7) {
                  if (s1.scoreMean < s2.scoreMean) return 1;
                  if (s1.scoreMean > s2.scoreMean) return -1;
                }
                if (sortnum == 8) {
                  if (s1.scoreStdev < s2.scoreStdev) return 1;
                  if (s1.scoreStdev > s2.scoreStdev) return -1;
                }
                return 0;
              }
            });

        // featurecat.lizzie.analysis.MoveDataSorter MoveDataSorter = new
        // MoveDataSorter(data2);
        // ArrayList sortedMoveData = MoveDataSorter.getSortedMoveDataByPolicy();
        //   double maxlcb = 0;
        int totalPlayouts = 0;
        for (MoveData move : data2) {
          totalPlayouts = totalPlayouts + move.playouts;
        }
        MoveData data = data2.get(row);
        switch (col) {
          case 0:
            if (data.order == -100)
              return "\n" + resourceBundle.getString("AnalysisFrame.actual") + "\n";
            else if (data.isNextMove)
              return data.order + 1 + "(" + resourceBundle.getString("AnalysisFrame.actual") + ")";
            else return data.order + 1;
          case 1:
            //
            // if(Lizzie.board.convertNameToCoordinates(data.coordinate)[0]==Lizzie.frame.suggestionclick[0]&&Lizzie.board.convertNameToCoordinates(data.coordinate)[1]==Lizzie.frame.suggestionclick[1])
            // {return "*"+data.coordinate;}
            // else
            return data.coordinate;
          case 2:
            return String.format(Locale.ENGLISH, "%.1f", data.lcb);
          case 3:
            if (data.isNextMove) {
              if (data.order != 0) {
                double diff = data.winrate - data.bestWinrate;
                return (diff > 0 ? "↑" : "↓")
                    + String.format(Locale.ENGLISH, "%.1f", diff)
                    + "("
                    + String.format(Locale.ENGLISH, "%.1f", data.winrate)
                    + ")";
              }
            }
            return String.format(Locale.ENGLISH, "%.1f", data.winrate);
          case 4:
            if (data.order == -100) return resourceBundle.getString("AnalysisFrame.exclude");
            else return Utils.getPlayoutsString(data.playouts);
          case 5:
            return String.format(
                Locale.ENGLISH, "%.1f", (double) data.playouts * 100 / totalPlayouts);
          case 6:
            return String.format(Locale.ENGLISH, "%.2f", data.policy);
          case 7:
            double score = data.scoreMean;
            if (EngineManager.isEngineGame && EngineManager.engineGameInfo.isGenmove) {
              if (!Lizzie.board.getHistory().isBlacksTurn()) {
                if (Lizzie.config.showKataGoBoardScoreMean) {
                  score = score + Lizzie.board.getHistory().getGameInfo().getKomi();
                }
              } else {
                if (Lizzie.config.showKataGoBoardScoreMean) {
                  score = score - Lizzie.board.getHistory().getGameInfo().getKomi();
                }
              }
            } else {
              if (Lizzie.board.getHistory().isBlacksTurn()) {
                if (Lizzie.config.showKataGoBoardScoreMean) {
                  score = score + Lizzie.board.getHistory().getGameInfo().getKomi();
                }
              } else {
                if (Lizzie.config.showKataGoBoardScoreMean) {
                  score = score - Lizzie.board.getHistory().getGameInfo().getKomi();
                }
              }
            }
            if (data.isNextMove && data.order != 0) {
              double diff = data.scoreMean - data.bestScoreMean;
              return (diff > 0 ? "↑" : "↓")
                  + String.format(Locale.ENGLISH, "%.1f", diff)
                  + "("
                  + String.format(Locale.ENGLISH, "%.1f", score)
                  + ")";
            } else return String.format(Locale.ENGLISH, "%.1f", score);
          case 8:
            return String.format(Locale.ENGLISH, "%.1f", data.scoreStdev);
          default:
            return "";
        }
      }
    };
  }
}
