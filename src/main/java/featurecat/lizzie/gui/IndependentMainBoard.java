package featurecat.lizzie.gui;

import static java.awt.image.BufferedImage.TYPE_INT_ARGB;
import static java.lang.Math.max;
import static java.lang.Math.min;

import featurecat.lizzie.Config;
import featurecat.lizzie.Lizzie;
import featurecat.lizzie.analysis.EngineManager;
import featurecat.lizzie.analysis.MoveData;
import featurecat.lizzie.rules.Board;
import featurecat.lizzie.rules.BoardData;
import featurecat.lizzie.rules.SGFParser;
import featurecat.lizzie.rules.Stone;
import featurecat.lizzie.util.Utils;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import org.json.JSONArray;

public class IndependentMainBoard extends JFrame {

  /** */
  private static final long serialVersionUID = 1L;

  private boolean top = false;
  private boolean down = false;
  private boolean left = false;
  private boolean right = false;
  private boolean isLocked = Lizzie.config.independentMainBoardLocked;
  public boolean isMouseOver = false;
  private boolean isReplayVariation = false;
  private boolean isShowingRect = false;
  private JButton lockUnlock;
  private JButton btnClose;
  public int[] mouseOverCoordinate = LizzieFrame.outOfBoundCoordinate;
  public Optional<List<String>> variationOpt;
  private int curSuggestionMoveOrderByNumber = -1;
  private Stone draggedstone;
  private int[] startcoords = new int[2];
  private int[] draggedCoords;
  private boolean tempDrag = false;
  public boolean Draggedmode = false;
  private int selectX1;
  private int selectY1;
  public int selectCoordsX1;
  public int selectCoordsY1;
  public int selectCoordsX2;
  public int selectCoordsY2;
  public BufferedImage cachedImage;
  private JButton topUntop;
  // private boolean drag = false;
  // private Point lastPoint = null;
  // private Point draggingAnchor = null;
  public BoardRenderer boardRenderer;
  private JLayeredPane allPanel;
  private JPanel mainPanel;
  private int lastLabel;
  private boolean hasMarkup;
  private String markupKey;
  private String markupValue;

  public IndependentMainBoard() {
    // super(owner);
    Point origin = new Point();
    InputIndependentMainBoard input = new InputIndependentMainBoard(); // VK_R要重做
    setUndecorated(true);
    setTitle(Lizzie.resourceBundle.getString("IndependentMainBoard.title"));
    setAlwaysOnTop(Lizzie.config.independentMainBoardTop);
    try {
      this.setIconImage(ImageIO.read(getClass().getResourceAsStream("/assets/logo.png")));
    } catch (IOException e) {
      e.printStackTrace();
    }
    setResizable(true);
    boardRenderer = new BoardRenderer(true);
    allPanel = new JLayeredPane();
    mainPanel =
        new JPanel() {
          @Override
          public void paintComponent(Graphics g) {
            if (Config.isScaled) {
              Graphics2D g1 = (Graphics2D) g;
              g1.scale(1.0 / Lizzie.javaScaleFactor, 1.0 / Lizzie.javaScaleFactor);
            }
            paintMianPanel(g);
          }
        };
    addKeyListener(input);
    //    addMouseListener(input);
    //    addMouseWheelListener(input);
    mainPanel.enableInputMethods(false);
    //  getContentPane().setLayout(null);

    // setBounds(600, 140, 220, 220);

    ImageIcon lock;
    lock = new ImageIcon();
    try {
      lock.setImage(ImageIO.read(getClass().getResourceAsStream("/assets/Locked.png")));
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

    ImageIcon unLock;
    unLock = new ImageIcon();
    try {
      unLock.setImage(ImageIO.read(getClass().getResourceAsStream("/assets/Unlocked.png")));
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    lockUnlock = new JButton();

    if (isLocked) lockUnlock.setIcon(lock);
    else lockUnlock.setIcon(unLock);
    lockUnlock.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            isLocked = !isLocked;
            Lizzie.config.independentMainBoardLocked = isLocked;
            Lizzie.config.uiConfig.put(
                "independent-mainboard-locked", Lizzie.config.independentMainBoardLocked);
            if (isLocked) {
              lockUnlock.setIcon(lock);
              if (top || down || left || right) {
                setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                top = false;
                down = false;
                left = false;
                right = false;
              }
            } else lockUnlock.setIcon(unLock);
          }
        });
    lockUnlock.setFocusable(false);
    lockUnlock.setBounds(0, 38, 19, 19);

    lockUnlock.addMouseListener(
        new MouseAdapter() {
          public void mouseExited(MouseEvent e) {
            btnClose.setVisible(false);
            lockUnlock.setVisible(false);
            topUntop.setVisible(false);
          }

          public void mouseEntered(MouseEvent e) {
            btnClose.setVisible(true);
            lockUnlock.setVisible(true);
            topUntop.setVisible(true);
          }
        });

    ImageIcon closeIcon;
    closeIcon = new ImageIcon();
    try {
      closeIcon.setImage(ImageIO.read(getClass().getResourceAsStream("/assets/close.png")));
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

    addWindowListener(
        new WindowAdapter() {
          public void windowClosing(WindowEvent e) {
            Lizzie.frame.toggleIndependentMainBoard();
            if (Lizzie.config.isFloatBoardMode()) Lizzie.frame.defaultMode();
          }
        });

    btnClose = new JButton(closeIcon);
    btnClose.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            Lizzie.frame.toggleIndependentMainBoard();
            if (Lizzie.config.isFloatBoardMode()) Lizzie.frame.defaultMode();
          }
        });
    btnClose.setFocusable(false);
    btnClose.setBounds(0, 0, 19, 19);
    btnClose.addMouseListener(
        new MouseAdapter() {
          public void mouseExited(MouseEvent e) {
            btnClose.setVisible(false);
            lockUnlock.setVisible(false);
            topUntop.setVisible(false);
          }

          public void mouseEntered(MouseEvent e) {
            btnClose.setVisible(true);
            lockUnlock.setVisible(true);
            topUntop.setVisible(true);
          }
        });
    ImageIcon topIcon;
    topIcon = new ImageIcon();
    try {

      topIcon.setImage(ImageIO.read(getClass().getResourceAsStream("/assets/top.png")));
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

    ImageIcon btm;
    btm = new ImageIcon();
    try {
      btm.setImage(ImageIO.read(getClass().getResourceAsStream("/assets/btm.png")));
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

    topUntop = new JButton();
    if (this.isAlwaysOnTop()) topUntop.setIcon(btm);
    else topUntop.setIcon(topIcon);

    topUntop.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            setAlwaysOnTop(!isAlwaysOnTop());
            Lizzie.config.independentMainBoardTop = isAlwaysOnTop();
            Lizzie.config.uiConfig.put(
                "independent-main-board-top", Lizzie.config.independentMainBoardTop);
            if (isAlwaysOnTop()) topUntop.setIcon(btm);
            else topUntop.setIcon(topIcon);
          }
        });
    topUntop.addMouseListener(
        new MouseAdapter() {
          public void mouseExited(MouseEvent e) {
            btnClose.setVisible(false);
            lockUnlock.setVisible(false);
            topUntop.setVisible(false);
          }

          public void mouseEntered(MouseEvent e) {
            btnClose.setVisible(true);
            lockUnlock.setVisible(true);
            topUntop.setVisible(true);
          }
        });

    topUntop.setBounds(0, 19, 19, 19);
    topUntop.setFocusable(false);
    topUntop.setMargin(new Insets(0, -1, 0, 0));

    btnClose.setVisible(false);
    lockUnlock.setVisible(false);
    topUntop.setVisible(false);
    // allPanel.setLayout(null);
    getContentPane().add(allPanel);

    allPanel.add(topUntop, new Integer(200));
    allPanel.add(lockUnlock, new Integer(200));
    allPanel.add(btnClose, new Integer(200));
    allPanel.add(mainPanel, new Integer(100));

    pack();
    boolean persisted = Lizzie.config.persistedUi != null;
    if (persisted
        && Lizzie.config.persistedUi.optJSONArray("independent-main-board") != null
        && Lizzie.config.persistedUi.optJSONArray("independent-main-board").length() == 4) {
      JSONArray pos = Lizzie.config.persistedUi.getJSONArray("independent-main-board");
      setBounds(pos.getInt(0), pos.getInt(1), pos.getInt(2), pos.getInt(3));
    } else {
      Dimension screensize = Toolkit.getDefaultToolkit().getScreenSize();
      setBounds(
          (int) screensize.getWidth() - 600, (int) screensize.getHeight() / 2 - 300, 600, 600);
    }
    addComponentListener(
        new ComponentAdapter() {
          @Override
          public void componentResized(ComponentEvent e) {
            mainPanel.setBounds(0, 0, getWidth(), getHeight());
            mainPanel.repaint();
          }
        });
    addMouseListener(
        new MouseAdapter() {
          public void mouseClicked(MouseEvent e) {
            if (e.isAltDown()
                && !SwingUtilities.isMiddleMouseButton(e)
                && (LizzieFrame.allowcoords != "" || LizzieFrame.avoidcoords != ""))
              LizzieFrame.menu.clearSelect.doClick();
          }

          public void mousePressed(MouseEvent e) {
            origin.x = e.getX();
            origin.y = e.getY();

            if (tempDrag) {
              DraggedReleased(Utils.zoomOut(e.getX()), Utils.zoomOut(e.getY()));
              tempDrag = false;
            } else {
              if (Lizzie.frame.isInScoreMode) {
                if (e.getButton() == MouseEvent.BUTTON1)
                  Lizzie.frame.leftClickInScoreMode(
                      Utils.zoomOut(e.getX()), Utils.zoomOut(e.getY()));
                return;
              }
              if (!SwingUtilities.isMiddleMouseButton(e) && (Input.selectMode || e.isAltDown())) {
                selectPressed(Utils.zoomOut(e.getX()), Utils.zoomOut(e.getY()), e.isAltDown());
                return;
              }
              if (Lizzie.frame.isShowingRightMenu) return;
              if (EngineManager.isEngineGame) {
                if (e.getButton() == MouseEvent.BUTTON1)
                  onClickedForManul(Utils.zoomOut(e.getX()), Utils.zoomOut(e.getY()));
                return;
              }
              if (e.getButton() == MouseEvent.BUTTON1) // left click
              {
                if (e.getClickCount() == 2
                    && !Lizzie.frame.isTrying
                    && !Lizzie.frame.isPlayingAgainstLeelaz
                    && !Lizzie.frame.isAnaPlayingAgainstLeelaz
                    && Lizzie.config.allowDoubleClick) { // TODO: Maybe need to delay check
                  onDoubleClicked(Utils.zoomOut(e.getX()), Utils.zoomOut(e.getY()));
                } else {
                  if (tryToMarkup(Utils.zoomOut(e.getX()), Utils.zoomOut(e.getY()))) return;
                  if (Input.insert == 0) {
                    onClicked(Utils.zoomOut(e.getX()), Utils.zoomOut(e.getY()));
                  } else if (Input.insert == 1) {
                    if (iscoordsempty(Utils.zoomOut(e.getX()), Utils.zoomOut(e.getY()))) {
                      int[] coords =
                          convertmousexytocoords(Utils.zoomOut(e.getX()), Utils.zoomOut(e.getY()));
                      Lizzie.frame.insertMove(coords, true);
                    }
                  } else if (Input.insert == 2) {
                    if (iscoordsempty(Utils.zoomOut(e.getX()), Utils.zoomOut(e.getY()))) {

                      int[] coords =
                          convertmousexytocoords(Utils.zoomOut(e.getX()), Utils.zoomOut(e.getY()));
                      Lizzie.frame.insertMove(coords, false);
                    }
                  }
                }

              } else if (e.getButton() == MouseEvent.BUTTON3) // right click
              {
                if (onClickedRight(Utils.zoomOut(e.getX()), Utils.zoomOut(e.getY()))) return;
                if (!openRightClickMenu(Utils.zoomOut(e.getX()), Utils.zoomOut(e.getY())))
                  Lizzie.frame.undoForRightClick();
              }
            }
          }
        });
    addMouseMotionListener(
        new MouseMotionAdapter() {
          public void mouseDragged(MouseEvent e) {
            if (Lizzie.frame.isPlayingAgainstLeelaz) return;
            if (!SwingUtilities.isMiddleMouseButton(e) && (Input.selectMode || e.isAltDown())) {
              selectDragged(Utils.zoomOut(e.getX()), Utils.zoomOut(e.getY()));
              return;
            }
            if (Draggedmode
                && !Lizzie.frame.isTrying
                && !LizzieFrame.urlSgf
                && !Lizzie.frame.isPlayingAgainstLeelaz
                && !Lizzie.frame.isAnaPlayingAgainstLeelaz
                && Lizzie.config.allowDrag) {
              DraggedDragged(Utils.zoomOut(e.getX()), Utils.zoomOut(e.getY()));
              return;
            }
            if (isLocked) return;
            Dimension dimension = getSize();
            if (top) {
              dimension.setSize(dimension.getHeight() - e.getY(), dimension.getHeight() - e.getY());
              setSize(dimension);
              setLocation(getLocationOnScreen().x, getLocationOnScreen().y + e.getY());
            } else if (down) {
              dimension.setSize(e.getY(), e.getY());
              setSize(dimension);
            } else if (left) {
              dimension.setSize(dimension.getWidth() - e.getX(), dimension.getWidth() - e.getX());
              setSize(dimension);
              setLocation(getLocationOnScreen().x + e.getX(), getLocationOnScreen().y);
            } else if (right) {
              dimension.setSize(e.getX(), e.getX());
              setSize(dimension);
            } else {
              // setLocation(e.getLocationOnScreen().x - draggingAnchor.x, e.getLocationOnScreen().y
              // - draggingAnchor.y);
              if (LizzieFrame.canGoAfterload) {
                Point p = getLocation();
                setLocation(p.x + e.getX() - origin.x, p.y + e.getY() - origin.y);
              }
            }
          }
        });

    addMouseListener(
        new MouseAdapter() {
          public void mouseExited(MouseEvent e) {
            btnClose.setVisible(false);
            lockUnlock.setVisible(false);
            topUntop.setVisible(false);
            Lizzie.board.clearPressStoneInfo(null);
            if (!Lizzie.frame.isShowingRightMenu) {
              mouseOverCoordinate = LizzieFrame.outOfBoundCoordinate;
              isMouseOver = false;
              Draggedmode = false;
              clearMoved();
            }
            if (draggedstone != Stone.EMPTY) {
              draggedstone = Stone.EMPTY;
              boardRenderer.removedrawmovestone();
              refresh();
              return;
            }
          }

          public void mouseEntered(MouseEvent e) {
            if (Lizzie.frame.isInTemporaryBoard) {
              Lizzie.frame.stopTemporaryBoardMaybe();
              Lizzie.frame.refresh();
            }
            btnClose.setVisible(true);
            lockUnlock.setVisible(true);
            topUntop.setVisible(true);
          }
        });

    addMouseWheelListener(
        new MouseWheelListener() {
          @Override
          public void mouseWheelMoved(MouseWheelEvent e) {
            // TODO Auto-generated method stub

            if (e.getWheelRotation() > 0) {
              if (boardRenderer.isShowingBranch()) {
                doBranch(1);
                refresh();
              } else {
                Input.redo();
              }
            } else if (e.getWheelRotation() < 0) {
              if (boardRenderer.isShowingBranch()) {
                doBranch(-1);
                refresh();
              } else {
                Input.undo();
              }
            }
          }
        });

    addMouseListener(
        new MouseAdapter() {
          public void mouseReleased(MouseEvent e) {
            if (Input.selectMode || (e.isAltDown() && e.getButton() != MouseEvent.BUTTON2)) {
              selectReleased(Utils.zoomOut(e.getX()), Utils.zoomOut(e.getY()));
              return;
            }
            if (Draggedmode
                && !Lizzie.frame.isTrying
                && !LizzieFrame.urlSgf
                && !Lizzie.frame.isPlayingAgainstLeelaz
                && !Lizzie.frame.isAnaPlayingAgainstLeelaz
                && Lizzie.config.allowDrag) {
              DraggedReleased(Utils.zoomOut(e.getX()), Utils.zoomOut(e.getY()));
              return;
            }
            if (SwingUtilities.isMiddleMouseButton(e)) {
              // if (Lizzie.frame.syncBoard) return;
              if (Lizzie.frame.isShowingRightMenu) return;
              Lizzie.frame.playCurrentVariation();
            }
          }
        });

    addMouseMotionListener(
        new MouseAdapter() {
          @Override
          public void mouseMoved(MouseEvent e) {
            int x = Utils.zoomOut(e.getX());
            int y = Utils.zoomOut(e.getY());
            if (tempDrag) DraggedDragged(Utils.zoomOut(e.getX()), Utils.zoomOut(e.getY()));
            else {
              if (Input.selectMode) {
                Lizzie.board.clearPressStoneInfo(null);
                return;
              }
              if (Lizzie.frame.RightClickMenu.isVisible()
                  || Lizzie.frame.RightClickMenu2.isVisible()) {
                Lizzie.board.clearPressStoneInfo(null);
                return;
              }
              if (Draggedmode
                  && !Lizzie.frame.isTrying
                  && !LizzieFrame.urlSgf
                  && !Lizzie.frame.isPlayingAgainstLeelaz
                  && !Lizzie.frame.isAnaPlayingAgainstLeelaz
                  && Lizzie.config.allowDrag) {
                DraggedMoved(x, y);
                Lizzie.board.clearPressStoneInfo(null);
                return;
              }
              boolean needRepaint = false;
              Optional<int[]> coords = boardRenderer.convertScreenToCoordinates(x, y);
              if (coords.isPresent()) {
                int[] curCoords = coords.get();
                boolean isCoordsChanged = false;
                if (mouseOverCoordinate[0] != curCoords[0]
                    || mouseOverCoordinate[1] != curCoords[1]) {
                  isCoordsChanged = true;
                  mouseOverCoordinate = curCoords;
                }
                if (isCoordsChanged) {
                  boolean isCurMouseOver = false;
                  if (Lizzie.config.showNextMoveBlunder) {
                    if (Lizzie.board.getHistory().getCurrentHistoryNode().next().isPresent()) {
                      BoardData nextData =
                          Lizzie.board.getHistory().getCurrentHistoryNode().next().get().getData();
                      if (nextData.getPlayouts() > 0)
                        if (nextData.lastMove.isPresent())
                          if (nextData.lastMove.get()[0] == curCoords[0]
                              && nextData.lastMove.get()[1] == curCoords[1]) {
                            isCurMouseOver = true;
                          }
                    }
                  }
                  List<MoveData> bestMoves = Lizzie.frame.getBestMoves();
                  if (!bestMoves.isEmpty())
                    for (int i = 0; i < bestMoves.size(); i++) {
                      Optional<int[]> bestCoords = Board.asCoordinates(bestMoves.get(i).coordinate);
                      if (bestCoords.isPresent()) {
                        if (bestCoords.get()[0] == curCoords[0]
                            && bestCoords.get()[1] == curCoords[1]) {
                          isCurMouseOver = true;
                          break;
                        }
                      }
                    }
                  if (isCurMouseOver) {
                    clearMoved();
                    needRepaint = true;
                    isMouseOver = true;
                    if (Lizzie.config.autoReplayBranch) {
                      Lizzie.frame.mouseOverChanged = true;
                      boardRenderer.setDisplayedBranchLength(1);
                    }
                  } else {
                    if (isMouseOver) {
                      needRepaint = true;
                    }
                    clearMoved();
                    isMouseOver = false;
                  }
                }
                if (Lizzie.frame.shouldShowRect()) {
                  isShowingRect = true;
                  needRepaint = true;
                  boardRenderer.drawmoveblock(
                      curCoords[0], curCoords[1], Lizzie.board.getHistory().isBlacksTurn());
                }
                Lizzie.board.clearPressStoneInfo(curCoords);
              } else {
                if (isMouseOver) {
                  mouseOverCoordinate = LizzieFrame.outOfBoundCoordinate;
                  isMouseOver = false;
                  needRepaint = true;
                  clearMoved();
                  isMouseOver = false;
                }
                if (Lizzie.frame.shouldShowRect()) {
                  if (isShowingRect) {
                    needRepaint = true;
                    boardRenderer.removeblock();
                    isShowingRect = false;
                  }
                }
                Lizzie.board.clearPressStoneInfo(null);
              }
              if (needRepaint) refresh();

              if (isLocked) {
                return;
              }
              if (e.getPoint().getY() <= 6) {
                setCursor(Cursor.getPredefinedCursor(Cursor.N_RESIZE_CURSOR));
                top = true;
              } else if (Math.abs(e.getPoint().getY() - getSize().getHeight()) <= 7) {
                setCursor(Cursor.getPredefinedCursor(Cursor.N_RESIZE_CURSOR));
                down = true;
              } else if (e.getPoint().getX() <= 6) {
                setCursor(Cursor.getPredefinedCursor(Cursor.E_RESIZE_CURSOR));
                left = true;
              } else if (Math.abs(e.getPoint().getX() - getSize().getWidth()) <= 7) {
                setCursor(Cursor.getPredefinedCursor(Cursor.E_RESIZE_CURSOR));
                right = true;
              } else {
                setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                // draggingAnchor = new Point(Utils.zoomOut(e.getX()) + getX(),
                // Utils.zoomOut(e.getY()) + getY());
                top = false;
                down = false;
                left = false;
                right = false;
                //  drag = true;
              }
            }
          }
        });
  }

  protected void paintButton(Graphics g) {
    // TODO Auto-generated method stub
    Graphics2D g1 = (Graphics2D) g;
    final AffineTransform t = g1.getTransform();
    t.setToScale(1, 1);
    g1.setTransform(t);
  }

  public void doBranch(int moveTo) {
    if (moveTo > 0) {
      if (boardRenderer.isShowingNormalBoard()) {
        setDisplayedBranchLength(2);
      } else if (boardRenderer.isShowingUnImportantBoard()) {
        setDisplayedBranchLength(2);
      } else {
        if (boardRenderer.getReplayBranch() > boardRenderer.getDisplayedBranchLength()) {
          boardRenderer.incrementDisplayedBranchLength(1);
        }
      }
    } else {
      if (boardRenderer.isShowingNormalBoard()) {
        setDisplayedBranchLength(boardRenderer.getBranchLength() - 1);
      } else {
        if (boardRenderer.getDisplayedBranchLength() > 1) {
          boardRenderer.incrementDisplayedBranchLength(-1);
        }
      }
    }
  }

  public void clearMoved() {
    isReplayVariation = false;
    isMouseOver = false;
    boardRenderer.startNormalBoard();
    boardRenderer.clearBranch();
    boardRenderer.notShowingBranch();
  }

  private void paintMianPanel(Graphics g) {
    int width = Utils.zoomOut(mainPanel.getWidth());
    int height = Utils.zoomOut(mainPanel.getHeight());
    BufferedImage cachedImage = new BufferedImage(width, height, TYPE_INT_ARGB);
    // TODO Auto-generated method stub
    Graphics2D g0 = (Graphics2D) cachedImage.getGraphics();

    boardRenderer.setLocation(0, 0);
    boardRenderer.setBoardLength(width, height);
    boardRenderer.draw(g0);
    g0.dispose();
    this.cachedImage = cachedImage;
    g.drawImage(this.cachedImage, 0, 0, null);
  }

  private void setDisplayedBranchLength(int n) {
    boardRenderer.setDisplayedBranchLength(n);
  }

  public void refresh() {
    repaint();
  }

  public void replayBranch() {
    if (isReplayVariation || Lizzie.config.autoReplayBranch) return;
    int replaySteps = boardRenderer.getReplayBranch();
    if (replaySteps <= 0) return; // Bad steps or no branch
    int oriBranchLength = boardRenderer.getDisplayedBranchLength();
    isReplayVariation = true;
    final boolean oriPonder = Lizzie.leelaz.isPondering();
    if (!Lizzie.config.noRefreshOnMouseMove && Lizzie.leelaz.isPondering())
      Lizzie.leelaz.togglePonder();
    Runnable runnable =
        new Runnable() {
          public void run() {
            int secs = (int) (Lizzie.config.replayBranchIntervalSeconds * 1000);
            for (int i = 1; i < replaySteps + 1; i++) {
              if (!isReplayVariation) break;
              setDisplayedBranchLength(i + 1);
              repaint();
              try {
                Thread.sleep(secs);
              } catch (InterruptedException e) {
                e.printStackTrace();
              }
            }
            boardRenderer.setDisplayedBranchLength(oriBranchLength);
            isReplayVariation = false;
            if (!Lizzie.config.noRefreshOnMouseMove && oriPonder && !Lizzie.leelaz.isPondering())
              Lizzie.leelaz.togglePonder();
          }
        };
    Thread thread = new Thread(runnable);
    thread.start();
  }

  private void onClickedForManul(int x, int y) {
    Optional<int[]> boardCoordinates = boardRenderer.convertScreenToCoordinates(x, y);
    if (boardCoordinates.isPresent()) {
      int[] coords = boardCoordinates.get();
      if (Lizzie.frame.blackorwhite == 0) Lizzie.board.placeForManual(coords[0], coords[1]);
      if (Lizzie.frame.blackorwhite == 1)
        Lizzie.board.placeForManual(coords[0], coords[1], Stone.BLACK);
      if (Lizzie.frame.blackorwhite == 2)
        Lizzie.board.placeForManual(coords[0], coords[1], Stone.WHITE);
    }
  }

  private boolean iscoordsempty(int x, int y) {
    Optional<int[]> boardCoordinates = boardRenderer.convertScreenToCoordinates(x, y);
    if (boardCoordinates.isPresent()) {
      return Lizzie.board.iscoordsempty(boardCoordinates.get()[0], boardCoordinates.get()[1]);
    }
    return false;
  }

  private void onDoubleClicked(int x, int y) {
    Optional<int[]> boardCoordinates = boardRenderer.convertScreenToCoordinates(x, y);
    if (boardCoordinates.isPresent()) {
      int[] coords = boardCoordinates.get();
      if (!Lizzie.frame.isPlayingAgainstLeelaz) {
        int moveNumber = Lizzie.board.moveNumberByCoord(coords);
        if (moveNumber > 0) {
          Lizzie.board.goToMoveNumberBeyondBranch(moveNumber);
        }
      }
    }
  }

  public void setDragStartInfo(int[] coords, boolean fromRightClick) {
    startcoords[0] = coords[0];
    startcoords[1] = coords[1];
    draggedstone = Lizzie.board.getstonestat(coords);
    if (draggedstone == Stone.BLACK || draggedstone == Stone.WHITE) {
      draggedCoords = coords;
      if (fromRightClick) tempDrag = true;
      else Draggedmode = true;
    }
  }

  private void onClicked(int x, int y) {
    if (Lizzie.frame.isContributing) return;
    // Check for board click
    Optional<int[]> boardCoordinates;
    boardCoordinates = boardRenderer.convertScreenToCoordinates(x, y);
    if (boardCoordinates.isPresent()) {
      int[] coords = boardCoordinates.get();
      if (Lizzie.board.hasStoneAt(coords)) {
        Lizzie.board.setPressStoneInfo(coords, false);
      }
      if (Lizzie.frame.bothSync) {
        if (Lizzie.frame.blackorwhite == 0) Lizzie.board.place(coords[0], coords[1]);
        if (Lizzie.frame.blackorwhite == 1) Lizzie.board.place(coords[0], coords[1], Stone.BLACK);
        if (Lizzie.frame.blackorwhite == 2) Lizzie.board.place(coords[0], coords[1], Stone.WHITE);
      } else if (Lizzie.config.allowDrag) {
        startcoords[0] = coords[0];
        startcoords[1] = coords[1];
        draggedstone = Lizzie.board.getstonestat(coords);
        if (draggedstone == Stone.BLACK || draggedstone == Stone.WHITE) {
          draggedCoords = coords;
          Draggedmode = true;
        }
      }
      //  if (Lizzie.board.inAnalysisMode()) Lizzie.board.toggleAnalysis();
      if (!Lizzie.frame.isPlayingAgainstLeelaz
          || (Lizzie.frame.playerIsBlack == Lizzie.board.getData().blackToPlay)) {
        if (!Lizzie.frame.isAnaPlayingAgainstLeelaz
            || !LizzieFrame.toolbar.chkAutoPlayBlack.isSelected()
                == Lizzie.board.getData().blackToPlay) {
          if (Lizzie.frame.isPlayingAgainstLeelaz || Lizzie.frame.isAnaPlayingAgainstLeelaz) {
            if (Lizzie.leelaz.isGamePaused) return;
            if (Lizzie.leelaz.isLoaded() && !EngineManager.isEmpty)
              Lizzie.board.place(coords[0], coords[1]);
            else
              Utils.showMsg(
                  Lizzie.resourceBundle.getString(
                      "LizzieFrame.waitEngineLoadingHint")); // ("请等待引擎加载完毕");
            if (Lizzie.config.showrect == 1) boardRenderer.removeblock();
          } else {
            if (Lizzie.frame.blackorwhite == 0) Lizzie.board.place(coords[0], coords[1]);
            if (Lizzie.frame.blackorwhite == 1)
              Lizzie.board.place(coords[0], coords[1], Stone.BLACK);
            if (Lizzie.frame.blackorwhite == 2)
              Lizzie.board.place(coords[0], coords[1], Stone.WHITE);
          }
        }
      }
    }
    Lizzie.frame.refresh();
  }

  private int[] convertmousexytocoords(int x, int y) {
    Optional<int[]> boardCoordinates = boardRenderer.convertScreenToCoordinates(x, y);
    if (boardCoordinates.isPresent()) {
      int[] coords = boardCoordinates.get();
      return coords;
    }
    return LizzieFrame.outOfBoundCoordinate;
  }

  public void setMouseOverCoords(int index) {
    List<MoveData> bestMoves = Lizzie.board.getHistory().getData().bestMoves;
    if (bestMoves == null || bestMoves.isEmpty()) return;
    if (index >= bestMoves.size()) return;
    if (curSuggestionMoveOrderByNumber == index) {
      curSuggestionMoveOrderByNumber = -1;
      clearMoved();
      mouseOverCoordinate = LizzieFrame.outOfBoundCoordinate;
      return;
    }
    curSuggestionMoveOrderByNumber = index;
    mouseOverCoordinate =
        Board.convertNameToCoordinates(
            Lizzie.board.getHistory().getData().bestMoves.get(index).coordinate);
  }

  private void DraggedReleased(int x, int y) {
    Lizzie.frame.DraggedReleased(x, y, boardRenderer, draggedstone, Draggedmode, draggedCoords);
  }

  private void DraggedDragged(int x, int y) {
    if (draggedstone != Stone.EMPTY) {
      Optional<int[]> boardCoordinates = boardRenderer.convertScreenToCoordinates(x, y);
      if (boardCoordinates.isPresent()) {
        int[] coords = boardCoordinates.get();
        boardRenderer.drawmovestone(coords[0], coords[1], draggedstone);
        refresh();
      }
    }
  }

  private void DraggedMoved(int x, int y) {
    if (Lizzie.frame.RightClickMenu.isVisible() || Lizzie.frame.RightClickMenu2.isVisible()) {
      return;
    }

    refresh();
  }

  private boolean isMouseOver2(int x, int y) {

    return mouseOverCoordinate[0] == x && mouseOverCoordinate[1] == y;
  }

  public boolean isMouseOverSuggestions() {
    List<MoveData> bestMoves = Lizzie.board.getHistory().getData().bestMoves;
    for (int i = 0; i < bestMoves.size(); i++) {
      Optional<int[]> c = Board.asCoordinates(bestMoves.get(i).coordinate);
      if (c.isPresent()) {
        if (isMouseOver2(c.get()[0], c.get()[1])) {
          List<String> variation = bestMoves.get(i).variation;
          variationOpt = Optional.of(variation);
          return true;
        }
      }
    }
    return false;
  }

  private boolean openRightClickMenu(int x, int y) {
    if (Lizzie.frame.clickOrder != -1) {
      Lizzie.frame.clickOrder = -1;
      LizzieFrame.boardRenderer.startNormalBoard();
      Lizzie.frame.suggestionclick = LizzieFrame.outOfBoundCoordinate;
      Lizzie.frame.mouseOverCoordinate = LizzieFrame.outOfBoundCoordinate;
      LizzieFrame.boardRenderer.clearBranch();
      Lizzie.frame.selectedorder = -1;
      Lizzie.frame.currentRow = -1;
      return true;
    }
    if (!Lizzie.config.showRightMenu && !isMouseOverSuggestions()) {
      return false;
    }
    Optional<int[]> boardCoordinates = boardRenderer.convertScreenToCoordinates(x, y);

    if (!boardCoordinates.isPresent()) {

      return false;
    }
    //    if (isPlayingAgainstLeelaz) {
    //
    //      return true;
    //    }
    //    if (Lizzie.leelaz.isPondering()) {
    //      Lizzie.leelaz.sendCommand("name");
    //    }

    int[] coords = boardCoordinates.get();

    if (Lizzie.board.getstonestat(coords) == Stone.BLACK
        || Lizzie.board.getstonestat(coords) == Stone.WHITE) {
      // Lizzie.frame.RightClickMenu2.Store(x, y);
      showmenu2(x, y, coords);
      //      Timer timer = new Timer();
      //      timer.schedule(
      //          new TimerTask() {
      //            public void run() {
      //              showmenu2(x, y, coords);
      //              this.cancel();
      //            }
      //          },
      //          50);
      return true;
    } else {
      //  Lizzie.frame.RightClickMenu.Store(x, y);
      showmenu(x, y, coords);
      //      Timer timer = new Timer();
      //      timer.schedule(
      //          new TimerTask() {
      //            public void run() {
      //              showmenu(x, y, coords);
      //              this.cancel();
      //            }
      //          },
      //          50);
    }
    return true;
  }

  private void showmenu(int x, int y, int[] coords) {
    Lizzie.frame.RightClickMenu.setCoords(coords);
    Lizzie.frame.RightClickMenu.show(mainPanel, Utils.zoomIn(x), Utils.zoomIn(y));
  }

  private void showmenu2(int x, int y, int[] coords) {
    Lizzie.frame.RightClickMenu2.setCoords(coords);
    Lizzie.frame.RightClickMenu2.setFromIndependent(true);
    Lizzie.frame.RightClickMenu2.show(mainPanel, Utils.zoomIn(x), Utils.zoomIn(y));
  }

  private void selectPressed(int x, int y, boolean isFromAlt) {
    selectX1 = x;
    selectY1 = y;
    if (isFromAlt) {
      Lizzie.frame.selectForceAllow = true;
      Lizzie.frame.isKeepingForce = true;
    }
  }

  private void selectDragged(int x, int y) {
    if (selectX1 > 0 && selectY1 > 0)
      boardRenderer.drawSelectedRect(selectX1, selectY1, x, y, Lizzie.frame.selectForceAllow);
    else boardRenderer.removeSelectedRect();
    refresh();
  }

  public void selectReleased(int x, int y) {
    if (selectX1 > 0 && selectY1 > 0) {
      Optional<int[]> boardCoordinates =
          boardRenderer.convertScreenToCoordinatesForSelect(
              min(selectX1, x), max(selectX1, x), min(selectY1, y), max(selectY1, y));
      if (boardCoordinates.isPresent()) {
        //     selectX2 = x;
        //     selectY2 = y;
        int[] coords = boardCoordinates.get();
        selectCoordsX1 = coords[0];
        selectCoordsY1 = coords[1];
        selectCoordsX2 = coords[2];
        selectCoordsY2 = coords[3];

        selectForceAllowAvoid();
        if (Lizzie.frame.selectForceAllow)
          boardRenderer.drawAllSelectedRectByCoords(
              Lizzie.frame.selectForceAllow, LizzieFrame.allowcoords);
        else
          boardRenderer.drawAllSelectedRectByCoords(
              Lizzie.frame.selectForceAllow, LizzieFrame.avoidcoords);
        Lizzie.board.clearBestMovesAfter(Lizzie.board.getHistory().getStart());
        repaint();
      } else {
        selectCoordsX2 = -1;
        selectCoordsY2 = -1;
      }
    }
  }

  public void selectForceAllowAvoid() {
    //  Lizzie.board.convertCoordinatesToName(coords[0], coords[1]);
    int minX = min(selectCoordsX1, selectCoordsX2);
    int minY = min(selectCoordsY1, selectCoordsY2);
    int xCounts = Math.abs(selectCoordsX1 - selectCoordsX2);
    int yCounts = Math.abs(selectCoordsY1 - selectCoordsY2);
    //    featurecat.lizzie.gui.RightClickMenu.kataAllowTopLeft =
    //        Lizzie.board.convertCoordinatesToName(minX, minY);
    //    featurecat.lizzie.gui.RightClickMenu.kataAllowBottomRight =
    //        Lizzie.board.convertCoordinatesToName(minX + xCounts, minY + yCounts);
    for (int i = 0; i <= xCounts; i++) {
      for (int j = 0; j <= yCounts; j++) {
        int x = minX + i;
        int y = minY + j;
        String coordsName = Board.convertCoordinatesToName(x, y);
        if (Lizzie.frame.selectForceAllow) {
          if (LizzieFrame.allowcoords != "") {
            LizzieFrame.allowcoords = LizzieFrame.allowcoords + "," + coordsName;
          } else {
            LizzieFrame.allowcoords = coordsName;
          }
        } else {
          if (LizzieFrame.avoidcoords != "") {
            LizzieFrame.avoidcoords = LizzieFrame.avoidcoords + "," + coordsName;
          } else {
            LizzieFrame.avoidcoords = coordsName;
          }
        }
      }
    }
    if (Lizzie.frame.selectForceAllow) {
      LizzieFrame.avoidcoords = "";
      Lizzie.leelaz.analyzeAvoid("allow", LizzieFrame.allowcoords, 50);
    } else {
      LizzieFrame.allowcoords = "";
      Lizzie.leelaz.analyzeAvoid("avoid", LizzieFrame.avoidcoords, 50);
    }
    Input.selectMode = false;
    LizzieFrame.menu.clearAllowAvoidButtonState();
  }

  private boolean tryToMarkup(int x, int y) {
    // TODO Auto-generated method stub
    if (Lizzie.frame.isMarkuping) {
      Optional<int[]> boardCoordinates;
      boardCoordinates = boardRenderer.convertScreenToCoordinates(x, y);
      if (boardCoordinates.isPresent()) {
        int[] coords = boardCoordinates.get();
        BoardData data = Lizzie.board.getHistory().getData();
        lastLabel = 'A' - 1;
        hasMarkup = false;
        data.getProperties()
            .forEach(
                (key, value) -> {
                  if (SGFParser.isListProperty(key)) {
                    String[] labels = value.split(",");
                    for (String label : labels) {
                      String[] moves = label.split(":");
                      int[] move = SGFParser.convertSgfPosToCoord(moves[0]);
                      if (move != null && (move[0] == coords[0] && move[1] == coords[1])) {
                        hasMarkup = true;
                        break;
                      }
                      if (Lizzie.frame.markupType == 1) {
                        if ("LB".equals(key) && moves.length > 1) {
                          // Label
                          if (moves[1].charAt(0) > lastLabel) lastLabel = moves[1].charAt(0);
                        }
                      }
                    }
                  }
                });
        if (hasMarkup) {
          tryToRemoveMarkup(x, y);
          return true;
        }
        if (Lizzie.frame.markupType == 1) {
          lastLabel = lastLabel + 1;
          if (lastLabel >= 91 && lastLabel <= 96) lastLabel = 97;
          String value = SGFParser.asCoord(coords) + ":" + ((char) lastLabel);
          data.getProperties().merge("LB", value, (old, val) -> old + "," + val);
        } else if (Lizzie.frame.markupType == 2) {
          String value = SGFParser.asCoord(coords);
          data.getProperties().merge("CR", value, (old, val) -> old + "," + val);
        } else if (Lizzie.frame.markupType == 3) {
          String value = SGFParser.asCoord(coords);
          data.getProperties().merge("MA", value, (old, val) -> old + "," + val);
        } else if (Lizzie.frame.markupType == 4) {
          String value = SGFParser.asCoord(coords);
          data.getProperties().merge("SQ", value, (old, val) -> old + "," + val);
        } else if (Lizzie.frame.markupType == 5) {
          String value = SGFParser.asCoord(coords);
          data.getProperties().merge("TR", value, (old, val) -> old + "," + val);
        }
        refresh();
        return true;
      } else return false;
    } else return false;
  }

  private boolean tryToRemoveMarkup(int x, int y) {
    // TODO Auto-generated method stub
    if (Lizzie.frame.isMarkuping) {
      Optional<int[]> boardCoordinates;
      boardCoordinates = boardRenderer.convertScreenToCoordinates(x, y);
      if (boardCoordinates.isPresent()) {
        int[] coords = boardCoordinates.get();
        BoardData data = Lizzie.board.getHistory().getData();
        data.getProperties()
            .forEach(
                (key, value) -> {
                  if (SGFParser.isListProperty(key)) {
                    String[] labels = value.split(",");
                    for (String label : labels) {
                      String[] moves = label.split(":");
                      int[] move = SGFParser.convertSgfPosToCoord(moves[0]);
                      if (move != null && (move[0] == coords[0] && move[1] == coords[1])) {
                        markupKey = key;
                        markupValue = value;
                      }
                    }
                  }
                });
        String newValue = "";
        String[] labels = markupValue.split(",");
        for (String label : labels) {
          String[] moves = label.split(":");
          int[] move = SGFParser.convertSgfPosToCoord(moves[0]);
          if (move != null && (move[0] != coords[0] || move[1] != coords[1])) {
            newValue += label + ",";
          }
        }
        if (newValue.endsWith(",")) newValue = newValue.substring(0, newValue.length() - 1);
        data.getProperties().replace(markupKey, newValue);
        refresh();
        return true;
      } else return false;
    } else return false;
  }

  public boolean onClickedRight(int x, int y) {
    if (Lizzie.frame.blackorwhite == 0) return false;
    Optional<int[]> boardCoordinates;
    boardCoordinates = boardRenderer.convertScreenToCoordinates(x, y);

    if (boardCoordinates.isPresent()) {
      int[] coords = boardCoordinates.get();
      if (!Lizzie.frame.isPlayingAgainstLeelaz && !Lizzie.frame.isAnaPlayingAgainstLeelaz) {
        if (Lizzie.board.getHistory().getStones()[Board.getIndex(coords[0], coords[1])]
            != Stone.EMPTY) {
          showmenu2(x, y, coords);
        } else {
          if (Lizzie.frame.blackorwhite == 1) Lizzie.board.place(coords[0], coords[1], Stone.WHITE);
          if (Lizzie.frame.blackorwhite == 2) Lizzie.board.place(coords[0], coords[1], Stone.BLACK);
        }
        return true;
      }
    }
    return false;
  }

  public boolean isMouseOver(int x, int y) {
    if (!Lizzie.config.showBlackCandidates && !Lizzie.config.showWhiteCandidates) {
      return false;
    }
    if (Lizzie.config.showSuggestionVariations)
      return mouseOverCoordinate[0] == x && mouseOverCoordinate[1] == y;
    else return false;
  }
}
