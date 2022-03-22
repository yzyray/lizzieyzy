package featurecat.lizzie.gui;

import static java.awt.image.BufferedImage.TYPE_INT_ARGB;

import featurecat.lizzie.Config;
import featurecat.lizzie.Lizzie;
import featurecat.lizzie.analysis.MoveData;
import featurecat.lizzie.rules.Board;
import featurecat.lizzie.util.Utils;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;

public class FloatBoard extends JDialog {

  /** */
  private static final long serialVersionUID = 1L;

  //  private boolean isLocked;
  private boolean isMouseOver = false;
  private boolean isReplayVariation = false;
  // private JButton lockUnlock;
  public int[] mouseOverCoordinate = LizzieFrame.outOfBoundCoordinate;
  public Optional<List<String>> variationOpt;
  private int curSuggestionMoveOrderByNumber = -1;
  public int selectCoordsX1;
  public int selectCoordsY1;
  public int selectCoordsX2;
  public int selectCoordsY2;
  private BufferedImage cachedImage;
  public FloatBoardRenderer boardRenderer;
  private JPanel mainPanel;
  private JLayeredPane allPanel;
  private ImageIcon toStop;
  private ImageIcon toPlay;
  private ImageIcon plus;
  private ImageIcon minus;
  private ImageIcon down;
  private ImageIcon up;
  private ImageIcon left;
  private ImageIcon right;
  private ImageIcon position;
  private JButton btnStopGo;
  private JButton btnHideShow;
  private JButton btnLeft;
  private JButton btnRight;
  private JButton btnUp;
  private JButton btnDown;
  private JButton btnShowPos;
  private int posX, posY, posWidth, posHeight;
  public boolean hideSuggestion = false;
  private int boardType; // 0=野狐 1=YC 2=新浪 >2其他
  private int cachedBoardType = -2;
  private boolean isScaled;
  private int extraX, extraY;
  private int tempX, tempY, tempWidth, tempHeight;
  private boolean showPosBtn = false;

  public FloatBoard(int x, int y, int width, int height, int boardType, boolean isScaled) {
    tempX = x;
    tempY = y;
    tempWidth = width;
    tempHeight = height;
    this.boardType = boardType;
    // if (boardType > 2) showPosBtn = true;
    this.isScaled = isScaled;
    x = x - Utils.zoomIn(20) + extraX;
    y = y - Utils.zoomIn(20) + extraY;
    width = width + Utils.zoomIn(40);
    height = height + Utils.zoomIn(40);
    posX = x;
    posY = y;
    posWidth = width;
    posHeight = height;
    setTitle("FloatBoard");
    setAlwaysOnTop(true);
    try {
      this.setIconImage(ImageIO.read(getClass().getResourceAsStream("/assets/logo.png")));
    } catch (IOException e) {
      e.printStackTrace();
    }
    setBounds(Utils.zoomIn(x), Utils.zoomIn(y), Utils.zoomIn(width), Utils.zoomIn(height));

    setResizable(false);
    boardRenderer = new FloatBoardRenderer();
    setBoardType();
    mainPanel =
        new JPanel() {
          @Override
          public void paint(Graphics g) {
            super.paintComponent(g);
            ((Graphics2D) g).setComposite(AlphaComposite.getInstance(AlphaComposite.SRC));
            if (Config.isScaled) {
              Graphics2D g1 = (Graphics2D) g;
              g1.scale(1.0 / Lizzie.javaScaleFactor, 1.0 / Lizzie.javaScaleFactor);
            }
            paintMianPanel(g);
          }
        };
    mainPanel.enableInputMethods(false);
    mainPanel.setBounds(0, 0, Utils.zoomIn(width), Utils.zoomIn(height));

    //  mainPanel.setBackground(new Color(0, 0, 0, 0));

    toStop = new ImageIcon();
    toPlay = new ImageIcon();
    plus = new ImageIcon();
    minus = new ImageIcon();
    down = new ImageIcon();
    up = new ImageIcon();
    left = new ImageIcon();
    right = new ImageIcon();
    position = new ImageIcon();
    try {
      toStop.setImage(ImageIO.read(getClass().getResourceAsStream("/assets/tostop.png")));
      toPlay.setImage(ImageIO.read(getClass().getResourceAsStream("/assets/toplay.png")));
      plus.setImage(ImageIO.read(getClass().getResourceAsStream("/assets/plus.png")));
      minus.setImage(ImageIO.read(getClass().getResourceAsStream("/assets/minus.png")));
      down.setImage(ImageIO.read(getClass().getResourceAsStream("/assets/downFloat.png")));
      up.setImage(ImageIO.read(getClass().getResourceAsStream("/assets/upFloat.png")));
      left.setImage(ImageIO.read(getClass().getResourceAsStream("/assets/leftFloat.png")));
      right.setImage(ImageIO.read(getClass().getResourceAsStream("/assets/rightFloat.png")));
      position.setImage(ImageIO.read(getClass().getResourceAsStream("/assets/pos.png")));
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

    btnStopGo = new JButton();
    if (Lizzie.leelaz.isPondering()) btnStopGo.setIcon(toStop);
    else btnStopGo.setIcon(toPlay);
    btnStopGo.setContentAreaFilled(false);
    btnStopGo.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            Lizzie.leelaz.togglePonder();
          }
        });
    btnStopGo.setFocusable(false);
    btnStopGo.setVisible(true);

    btnHideShow = new JButton(minus);
    btnHideShow.setContentAreaFilled(false);
    btnHideShow.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            toggleHide();
          }
        });
    btnHideShow.setFocusable(false);
    btnHideShow.setVisible(true);
    btnStopGo.setPreferredSize(new Dimension(16, 16));
    btnHideShow.setPreferredSize(new Dimension(16, 16));
    btnStopGo.setBounds(
        getWidth() - 20 - Utils.zoomIn(Utils.zoomIn(20)),
        getHeight() - 18 - Utils.zoomIn(Utils.zoomIn(20)),
        19,
        19);
    btnHideShow.setBounds(
        getWidth() - 40 - Utils.zoomIn(Utils.zoomIn(20)),
        getHeight() - 18 - Utils.zoomIn(Utils.zoomIn(20)),
        19,
        19);

    btnLeft = new JButton(left);
    btnLeft.setContentAreaFilled(false);
    btnLeft.setMargin(new Insets(0, 0, 0, 0));
    btnLeft.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            extraX--;
            reSetPos();
          }
        });
    btnLeft.setFocusable(false);
    btnLeft.setPreferredSize(new Dimension(16, 16));

    btnRight = new JButton(right);
    btnRight.setContentAreaFilled(false);
    btnRight.setMargin(new Insets(0, 0, 0, 0));
    btnRight.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            extraX++;
            reSetPos();
          }
        });
    btnRight.setFocusable(false);
    btnRight.setPreferredSize(new Dimension(16, 16));

    btnUp = new JButton(up);
    btnUp.setContentAreaFilled(false);
    btnUp.setMargin(new Insets(0, 0, 0, 0));
    btnUp.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            extraY--;
            reSetPos();
          }
        });
    btnUp.setFocusable(false);
    btnUp.setPreferredSize(new Dimension(16, 16));

    btnDown = new JButton(down);
    btnDown.setContentAreaFilled(false);
    btnDown.setMargin(new Insets(0, 0, 0, 0));
    btnDown.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            extraY++;
            reSetPos();
          }
        });
    btnDown.setFocusable(false);
    btnDown.setPreferredSize(new Dimension(16, 16));

    btnShowPos = new JButton(position);
    btnShowPos.setContentAreaFilled(false);
    btnShowPos.setMargin(new Insets(0, 0, 0, 0));
    btnShowPos.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            showPosBtn = !showPosBtn;
            setButton();
          }
        });
    btnShowPos.setFocusable(false);
    btnShowPos.setPreferredSize(new Dimension(16, 16));

    setButton();

    allPanel = new JLayeredPane();
    allPanel.setLayout(null);
    getContentPane().add(allPanel);
    allPanel.add(btnShowPos, new Integer(200));
    allPanel.add(btnLeft, new Integer(200));
    allPanel.add(btnRight, new Integer(200));
    allPanel.add(btnUp, new Integer(200));
    allPanel.add(btnDown, new Integer(200));
    allPanel.add(btnHideShow, new Integer(200));
    allPanel.add(btnStopGo, new Integer(200));
    allPanel.add(mainPanel, new Integer(100));

    this.setUndecorated(true);
    this.setBackground(new Color(0, 0, 0, 0));
    this.setVisible(true);
    addKeyListener(
        new KeyAdapter() {
          public void keyPressed(KeyEvent e) {
            if (e.getKeyCode() == KeyEvent.VK_SLASH || e.getKeyCode() == KeyEvent.VK_DECIMAL) {
              if (Lizzie.frame.isCounting) {
                Lizzie.frame.clearKataEstimate();
                Lizzie.frame.isCounting = false;
                Lizzie.frame.estimateResults.setVisible(false);
              } else {
                Lizzie.frame.countstones(true);
              }
            }
            if (e.getKeyCode() == KeyEvent.VK_PERIOD)
              if (Lizzie.config.useShortcutKataEstimate) Lizzie.frame.toggleShowKataEstimate();
            if (e.getKeyCode() == KeyEvent.VK_H) Lizzie.leelaz.toggleHeatmap(false);
            if (e.getKeyCode() == KeyEvent.VK_T) Lizzie.frame.togglePolicy();
            if (e.getKeyCode() == KeyEvent.VK_UP) {
              if (boardRenderer.isShowingBranch()) {
                doBranch(-1);
              }
              refreshByLis();
            }
            if (e.getKeyCode() == KeyEvent.VK_DOWN) {
              if (boardRenderer.isShowingBranch()) {
                doBranch(1);
              }
              refreshByLis();
            }
            if (e.getKeyCode() == KeyEvent.VK_G) {
              tryToRefreshVariation();
            }
            if (e.getKeyCode() == KeyEvent.VK_F) {
              toggleHide();
            }
            if (e.getKeyCode() == KeyEvent.VK_SPACE) {
              Lizzie.frame.togglePonderMannul();
            }
          }
        });
    addMouseListener(
        new MouseAdapter() {
          public void mousePressed(MouseEvent e) {
            if (e.getButton() == MouseEvent.BUTTON1) // left click
            {
              onClicked(Utils.zoomOut(e.getX()), Utils.zoomOut(e.getY()));
            }
          }
        });

    addMouseListener(
        new MouseAdapter() {
          public void mouseExited(MouseEvent e) {
            //            btnClose.setVisible(false);
            //            lockUnlock.setVisible(false);
            //            topUntop.setVisible(false);
            mouseOverCoordinate = LizzieFrame.outOfBoundCoordinate;
            isMouseOver = false;
            clearMoved();
            refreshByLis();
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
              } else boardRenderer.incrementDisplayedBranchLength(1);
            } else if (e.getWheelRotation() < 0) {
              if (boardRenderer.isShowingBranch()) {
                doBranch(-1);
              } else boardRenderer.incrementDisplayedBranchLength(-1);
            }
            refreshByLis();
          }
        });

    addMouseMotionListener(
        new MouseAdapter() {
          @Override
          public void mouseMoved(MouseEvent e) {
            int x = Utils.zoomOut(e.getX());
            int y = Utils.zoomOut(e.getY());
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
                List<MoveData> bestMoves =
                    Lizzie.board.getHistory().getMainEnd().getData().bestMoves;
                if (!bestMoves.isEmpty())
                  for (int i = 0; i < bestMoves.size(); i++) {
                    Optional<int[]> bestCoords = Board.asCoordinates(bestMoves.get(i).coordinate);
                    if (bestCoords.isPresent()) {
                      if (bestCoords.get()[0] == curCoords[0]
                          && bestCoords.get()[1] == curCoords[1]) {
                        isCurMouseOver = true;
                      }
                    }
                  }
                if (isCurMouseOver) {
                  clearMoved();
                  needRepaint = true;
                  isMouseOver = true;
                  if (Lizzie.config.autoReplayBranch) {
                    Lizzie.frame.mouseOverChanged = true;
                    if (!Lizzie.config.autoReplayDisplayEntireVariationsFirst)
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
            } else {
              mouseOverCoordinate = LizzieFrame.outOfBoundCoordinate;
              if (isMouseOver) {
                isMouseOver = false;
                needRepaint = true;
                clearMoved();
                isMouseOver = false;
              }
            }
            if (needRepaint) refreshByLis();
          }
        });
  }

  private void toggleHide() {
    // TODO Auto-generated method stub
    hideSuggestion = !hideSuggestion;
    if (hideSuggestion) btnHideShow.setIcon(plus);
    else {
      btnHideShow.setIcon(minus);
      if (Lizzie.board.getHistory().getCurrentHistoryNode()
          != Lizzie.board.getHistory().getMainEnd())
        Lizzie.board.moveToAnyPosition(Lizzie.board.getHistory().getMainEnd());
    }
    refreshByLis();
  }

  private void tryToRefreshVariation() {
    boardRenderer.refreshVariation();
  }

  protected void reSetPos() {
    // TODO Auto-generated method stub
    this.setPos(tempX, tempY, tempWidth, tempHeight, boardType);
  }

  private void doBranch(int moveTo) {
    Lizzie.frame.readBoard.sendLossFocus();
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
    if (posWidth <= 40 || posHeight <= 40) return;
    cachedImage = new BufferedImage(posWidth, posHeight, TYPE_INT_ARGB);
    // TODO Auto-generated method stub
    if (!hideSuggestion) {
      Graphics2D g0 = (Graphics2D) cachedImage.getGraphics();
      boardRenderer.setLocation(Utils.zoomIn(20), Utils.zoomIn(20));
      boardRenderer.setBoardLength(posWidth - Utils.zoomIn(40), posHeight - Utils.zoomIn(40));
      boardRenderer.draw(g0);
      g0.dispose();
    }
    g.drawImage(cachedImage, 0, 0, null);
  }

  private void setDisplayedBranchLength(int n) {
    boardRenderer.setDisplayedBranchLength(n);
  }

  public void refresh() {
    mainPanel.repaint();
  }

  private void refreshByLis() {
    mainPanel.repaint();
  }

  public void replayBranch() {
    if (isReplayVariation) return;
    int replaySteps = boardRenderer.getReplayBranch();
    if (replaySteps <= 0) return; // Bad steps or no branch
    int oriBranchLength = boardRenderer.getDisplayedBranchLength();
    isReplayVariation = true;
    if (Lizzie.leelaz.isPondering()) Lizzie.leelaz.togglePonder();
    Runnable runnable =
        new Runnable() {
          public void run() {
            int secs = (int) (Lizzie.config.replayBranchIntervalSeconds * 1000);
            for (int i = 1; i < replaySteps + 1; i++) {
              if (!isReplayVariation) break;
              setDisplayedBranchLength(i);
              repaint();
              try {
                Thread.sleep(secs);
              } catch (InterruptedException e) {
                e.printStackTrace();
              }
            }
            boardRenderer.setDisplayedBranchLength(oriBranchLength);
            isReplayVariation = false;
            if (!Lizzie.leelaz.isPondering()) Lizzie.leelaz.togglePonder();
          }
        };
    Thread thread = new Thread(runnable);
    thread.start();
  }

  //
  //  private int[] convertmousexytocoords(int x, int y) {
  //    Optional<int[]> boardCoordinates = boardRenderer.convertScreenToCoordinates(x, y);
  //    if (boardCoordinates.isPresent()) {
  //      int[] coords = boardCoordinates.get();
  //      return coords;
  //    }
  //    return Lizzie.frame.outOfBoundCoordinate;
  //  }

  public void setMouseOverCoords(int index) {
    List<MoveData> bestMoves = Lizzie.board.getHistory().getData().bestMoves;
    if (bestMoves == null || bestMoves.isEmpty()) return;
    if (index >= bestMoves.size()) return;
    if (curSuggestionMoveOrderByNumber == index) {
      curSuggestionMoveOrderByNumber = -1;
      mouseOverCoordinate = LizzieFrame.outOfBoundCoordinate;
      return;
    }
    curSuggestionMoveOrderByNumber = index;
    mouseOverCoordinate =
        Board.convertNameToCoordinates(
            Lizzie.board.getHistory().getData().bestMoves.get(index).coordinate);
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
  //
  //  private boolean openRightClickMenu(int x, int y) {
  //    if (Lizzie.frame.clickOrder != -1) {
  //      Lizzie.frame.clickOrder = -1;
  //      Lizzie.frame.hasMoveOutOfList = false;
  //      Lizzie.frame.boardRenderer.startNormalBoard();
  //      Lizzie.frame.suggestionclick = Lizzie.frame.outOfBoundCoordinate;
  //      Lizzie.frame.mouseOverCoordinate = Lizzie.frame.outOfBoundCoordinate;
  //      Lizzie.frame.boardRenderer.clearBranch();
  //      Lizzie.frame.selectedorder = -1;
  //      Lizzie.frame.currentRow = -1;
  //      return true;
  //    }
  //    if (!Lizzie.config.showRightMenu && !isMouseOverSuggestions()) {
  //      return false;
  //    }
  //    Optional<int[]> boardCoordinates = boardRenderer.convertScreenToCoordinates(x, y);
  //
  //    if (!boardCoordinates.isPresent()) {
  //
  //      return false;
  //    }
  //    //    if (isPlayingAgainstLeelaz) {
  //    //
  //    //      return true;
  //    //    }
  //    //    if (Lizzie.leelaz.isPondering()) {
  //    //      Lizzie.leelaz.sendCommand("name");
  //    //    }
  //
  //    int[] coords = boardCoordinates.get();
  //
  //    if (Lizzie.board.getstonestat(coords) == Stone.BLACK
  //        || Lizzie.board.getstonestat(coords) == Stone.WHITE) {
  //      Lizzie.frame.RightClickMenu2.Store(x, y);
  //      Timer timer = new Timer();
  //      timer.schedule(
  //          new TimerTask() {
  //            public void run() {
  //              showmenu2(x, y);
  //              this.cancel();
  //            }
  //          },
  //          50);
  //      return true;
  //    } else {
  //      Lizzie.frame.RightClickMenu.Store(x, y);
  //      Timer timer = new Timer();
  //      timer.schedule(
  //          new TimerTask() {
  //            public void run() {
  //              showmenu(x, y);
  //              this.cancel();
  //            }
  //          },
  //          50);
  //    }
  //    return true;
  //  }
  //
  //  private void showmenu(int x, int y) {
  //    Lizzie.frame.RightClickMenu.show(mainPanel, x, y);
  //  }
  //
  //  private void showmenu2(int x, int y) {
  //    Lizzie.frame.RightClickMenu2.show(mainPanel, x, y);
  //  }

  private void onClicked(int x, int y) {
    // Check for board click
    Optional<int[]> boardCoordinates;

    boardCoordinates = boardRenderer.convertScreenToCoordinates(x, y);

    if (boardCoordinates.isPresent()) {
      // 增加判断是否为插入模式
      int[] coords = boardCoordinates.get();
      if (Lizzie.frame.bothSync) {
        Lizzie.board.place(coords[0], coords[1]);
      }
    }
    Lizzie.frame.refresh();
  }

  public boolean isMouseOver(int x, int y) {
    if ((Lizzie.board.getHistory().isBlacksTurn() && !Lizzie.config.showBlackCandidates)
        || (!Lizzie.board.getHistory().isBlacksTurn() && !Lizzie.config.showWhiteCandidates)) {
      return false;
    }
    if (Lizzie.config.showSuggestionVariations)
      return mouseOverCoordinate[0] == x && mouseOverCoordinate[1] == y;
    else return false;
  }

  private void setButton() {
    if (showPosBtn) {
      btnLeft.setVisible(true);
      btnRight.setVisible(true);
      btnUp.setVisible(true);
      btnDown.setVisible(true);
      btnStopGo.setBounds(
          getWidth() - 60 - Utils.zoomIn(Utils.zoomIn(20)),
          getHeight() - 18 - Utils.zoomIn(Utils.zoomIn(20)),
          19,
          19);
      btnHideShow.setBounds(
          getWidth() - 80 - Utils.zoomIn(Utils.zoomIn(20)),
          getHeight() - 18 - Utils.zoomIn(Utils.zoomIn(20)),
          19,
          19);
      btnLeft.setBounds(
          getWidth() - 38 - Utils.zoomIn(Utils.zoomIn(20)),
          getHeight() - 18 - Utils.zoomIn(Utils.zoomIn(20)),
          19,
          19);
      btnRight.setBounds(
          getWidth() - 19 - Utils.zoomIn(Utils.zoomIn(20)),
          getHeight() - 18 - Utils.zoomIn(Utils.zoomIn(20)),
          19,
          19);

      btnUp.setBounds(
          getWidth() - 19 - Utils.zoomIn(Utils.zoomIn(20)),
          getHeight() - 54 - Utils.zoomIn(Utils.zoomIn(20)),
          19,
          19);
      btnDown.setBounds(
          getWidth() - 19 - Utils.zoomIn(Utils.zoomIn(20)),
          getHeight() - 36 - Utils.zoomIn(Utils.zoomIn(20)),
          19,
          19);
      btnShowPos.setBounds(
          getWidth() - 19 - Utils.zoomIn(Utils.zoomIn(20)),
          getHeight() - 72 - Utils.zoomIn(Utils.zoomIn(20)),
          19,
          19);
    } else {
      btnLeft.setVisible(false);
      btnRight.setVisible(false);
      btnUp.setVisible(false);
      btnDown.setVisible(false);
      btnShowPos.setBounds(
          getWidth() - 19 - Utils.zoomIn(Utils.zoomIn(20)),
          getHeight() - 38 - Utils.zoomIn(Utils.zoomIn(20)),
          19,
          19);
      btnStopGo.setBounds(
          getWidth() - 20 - Utils.zoomIn(Utils.zoomIn(20)),
          getHeight() - 18 - Utils.zoomIn(Utils.zoomIn(20)),
          19,
          19);
      btnHideShow.setBounds(
          getWidth() - 40 - Utils.zoomIn(Utils.zoomIn(20)),
          getHeight() - 18 - Utils.zoomIn(Utils.zoomIn(20)),
          19,
          19);
    }
  }

  public void setPos(int x, int y, int width, int height, int boardType) {
    // TODO Auto-generated method stub
    if (width <= 0 || height <= 0) {
      setVisible(false);
      return;
    }
    tempX = x;
    tempY = y;
    tempWidth = width;
    tempHeight = height;
    //  if (cachedBoardType != boardType && boardType > 2)
    //  	showPosBtn = true;
    setBoardType();
    x = x - Utils.zoomIn(20) + extraX;
    y = y - Utils.zoomIn(20) + extraY;
    width = width + Utils.zoomIn(40);
    height = height + Utils.zoomIn(40);
    if (posX != x
        || posY != y
        || posWidth != width
        || posHeight != height
        || (cachedBoardType != boardType && boardType > 2)) {
      if (posWidth != width || posHeight != height) {
        extraX = 0;
        extraY = 0;
      }
      cachedBoardType = boardType;
      posX = x;
      posY = y;
      posWidth = width;
      posHeight = height;
      setBounds(Utils.zoomIn(x), Utils.zoomIn(y), Utils.zoomIn(width), Utils.zoomIn(height));
      mainPanel.setBounds(0, 0, Utils.zoomIn(width), Utils.zoomIn(height));
      setButton();
    }
    this.boardType = boardType;
    if (Lizzie.leelaz.isPondering()) btnStopGo.setIcon(toStop);
    else btnStopGo.setIcon(toPlay);
    if (!isVisible()) {
      setVisible(true);
      if (hideSuggestion) toggleHide();
    }
  }

  public void setBoardType() {
    // TODO Auto-generated method stub
    if (isScaled && boardType == 1) this.boardRenderer.boardType = -1;
    else this.boardRenderer.boardType = boardType;
  }

  public void setPonderState(boolean isPondering) {
    // TODO Auto-generated method stub
    if (isPondering) btnStopGo.setIcon(toStop);
    else btnStopGo.setIcon(toPlay);
  }
}
