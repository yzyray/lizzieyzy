package featurecat.lizzie.gui;

import static java.awt.image.BufferedImage.TYPE_INT_ARGB;

import featurecat.lizzie.Lizzie;
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
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import org.json.JSONArray;

public class IndependentSubBoard extends JFrame {

  /** */
  private static final long serialVersionUID = 1L;

  private boolean top = false;
  private boolean down = false;
  private boolean left = false;
  private boolean right = false;
  private boolean isLocked = Lizzie.config.independentSubBoardLocked;
  private JButton lockUnlock;
  private JButton btnClose;
  private JButton topUntop;
  public BufferedImage cachedImage;
  // private boolean drag = false;
  // private Point lastPoint = null;
  // private Point draggingAnchor = null;
  public SubBoardRenderer subBoardRenderer;
  private JPanel mainPanel;
  private JLayeredPane allPanel;

  public IndependentSubBoard() {
    // super(owner);
    Point origin = new Point();
    InputIndependentSubboard input = new InputIndependentSubboard();
    setUndecorated(true);
    setTitle(Lizzie.resourceBundle.getString("IndependentSubBoard.title"));
    setAlwaysOnTop(Lizzie.config.independentSubBoardTop);
    try {
      this.setIconImage(ImageIO.read(AnalysisFrame.class.getResourceAsStream("/assets/logo.png")));
    } catch (IOException e) {
      e.printStackTrace();
    }
    setResizable(true);
    subBoardRenderer = new SubBoardRenderer(false);
    allPanel = new JLayeredPane();
    mainPanel =
        new JPanel(true) {
          @Override
          protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            paintMianPanel(g);
          }
        };
    addKeyListener(input);
    addMouseListener(input);
    addMouseWheelListener(input);
    mainPanel.enableInputMethods(false);
    getContentPane().add(mainPanel);
    // setBounds(600, 140, 220, 220);

    boolean persisted = Lizzie.config.persistedUi != null;
    if (persisted
        && Lizzie.config.persistedUi.optJSONArray("independent-sub-board") != null
        && Lizzie.config.persistedUi.optJSONArray("independent-sub-board").length() == 4) {
      JSONArray pos = Lizzie.config.persistedUi.getJSONArray("independent-sub-board");
      setBounds(pos.getInt(0), pos.getInt(1), pos.getInt(2), pos.getInt(3));
    } else {
      Dimension screensize = Toolkit.getDefaultToolkit().getScreenSize();
      setBounds(0, (int) screensize.getHeight() / 2 - 150, 300, 300);
    }

    ImageIcon lock;
    lock = new ImageIcon();
    try {
      lock.setImage(ImageIO.read(AnalysisFrame.class.getResourceAsStream("/assets/Locked.png")));
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

    ImageIcon unLock;
    unLock = new ImageIcon();
    try {
      unLock.setImage(
          ImageIO.read(AnalysisFrame.class.getResourceAsStream("/assets/Unlocked.png")));
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
            Lizzie.config.independentSubBoardLocked = isLocked;
            Lizzie.config.uiConfig.put(
                "independent-subboard-locked", Lizzie.config.independentSubBoardLocked);
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
            lockUnlock.setVisible(false);
            btnClose.setVisible(false);
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
      closeIcon.setImage(
          ImageIO.read(AnalysisFrame.class.getResourceAsStream("/assets/close.png")));
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

    addWindowListener(
        new WindowAdapter() {
          public void windowClosing(WindowEvent e) {
            Lizzie.frame.toggleIndependentSubBoard();
            if (!Lizzie.config.showSubBoard) {
              if (Lizzie.frame.extraMode == 8) Lizzie.config.toggleShowSubBoard();
            }
          }
        });

    btnClose = new JButton(closeIcon);
    btnClose.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            Lizzie.frame.toggleIndependentSubBoard();
            if (!Lizzie.config.showSubBoard) {
              if (Lizzie.frame.extraMode == 8) Lizzie.config.toggleShowSubBoard();
            }
          }
        });
    btnClose.setFocusable(false);
    btnClose.setBounds(0, 0, 19, 19);
    btnClose.setVisible(false);
    btnClose.addMouseListener(
        new MouseAdapter() {
          public void mouseExited(MouseEvent e) {
            lockUnlock.setVisible(false);
            btnClose.setVisible(false);
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

      topIcon.setImage(ImageIO.read(AnalysisFrame.class.getResourceAsStream("/assets/top.png")));
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

    ImageIcon btm;
    btm = new ImageIcon();
    try {
      btm.setImage(ImageIO.read(AnalysisFrame.class.getResourceAsStream("/assets/btm.png")));
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
            Lizzie.config.independentSubBoardTop = isAlwaysOnTop();
            Lizzie.config.uiConfig.put(
                "independent-sub-board-top", Lizzie.config.independentSubBoardTop);
            if (isAlwaysOnTop()) topUntop.setIcon(btm);
            else topUntop.setIcon(topIcon);
          }
        });
    topUntop.addMouseListener(
        new MouseAdapter() {
          public void mouseExited(MouseEvent e) {
            topUntop.setVisible(false);
            lockUnlock.setVisible(false);
            btnClose.setVisible(false);
          }

          public void mouseEntered(MouseEvent e) {
            topUntop.setVisible(true);
            lockUnlock.setVisible(true);
            btnClose.setVisible(true);
          }
        });
    topUntop.setVisible(false);
    topUntop.setBounds(0, 19, 19, 19);
    topUntop.setFocusable(false);
    topUntop.setMargin(new Insets(0, -1, 0, 0));

    lockUnlock.setVisible(false);
    allPanel.setLayout(null);
    getContentPane().add(allPanel);

    allPanel.add(topUntop, new Integer(200));
    allPanel.add(lockUnlock, new Integer(200));
    allPanel.add(btnClose, new Integer(200));
    allPanel.add(mainPanel, new Integer(100));

    addComponentListener(
        new ComponentAdapter() {
          @Override
          public void componentResized(ComponentEvent e) {
            mainPanel.setBounds(0, 0, getWidth(), getHeight());
            subBoardRenderer.isMouseOver = false;
            mainPanel.repaint();
          }
        });
    addMouseListener(
        new MouseAdapter() {
          public void mousePressed(MouseEvent e) {
            origin.x = e.getX();
            origin.y = e.getY();
          }
        });
    addMouseMotionListener(
        new MouseMotionAdapter() {
          public void mouseDragged(MouseEvent e) {
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
              if (LizzieFrame.canGoAfterload) {
                Point p = getLocation();
                setLocation(p.x + e.getX() - origin.x, p.y + e.getY() - origin.y);
              }
            }
            if (subBoardRenderer.bestmovesNum >= 1) {
              subBoardRenderer.statChanged = true;
              subBoardRenderer.bestmovesNum--;
              refresh();
            }
          }
        });

    addMouseMotionListener(
        new MouseAdapter() {

          @Override
          public void mouseMoved(MouseEvent e) {
            if (isLocked) return;
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
              // draggingAnchor = new Point(e.getX() + getX(), e.getY() + getY());
              top = false;
              down = false;
              left = false;
              right = false;
              //  drag = true;
            }
          }
        });
  }

  private void paintMianPanel(Graphics g) {
    // TODO Auto-generated method stub
    int width = Utils.zoomOut(mainPanel.getWidth());
    int height = Utils.zoomOut(mainPanel.getHeight());
    BufferedImage cachedImage = new BufferedImage(width, height, TYPE_INT_ARGB);
    Graphics2D g0 = (Graphics2D) cachedImage.getGraphics();
    // g0.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    // g0.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
    subBoardRenderer.setLocation(0, 0);
    subBoardRenderer.setBoardLength(width, height);
    subBoardRenderer.setupSizeParameters();
    subBoardRenderer.draw(g0);
    g0.dispose();
    this.cachedImage = cachedImage;
    if (Lizzie.config.isScaled) {
      Graphics2D g1 = (Graphics2D) g;
      final AffineTransform t = g1.getTransform();
      t.setToScale(1, 1);
      g1.setTransform(t);
      g1.drawImage(cachedImage, 0, 0, null);
    } else {
      g.drawImage(cachedImage, 0, 0, null);
    }
  }

  public void refresh() {
    mainPanel.repaint();
  }

  public void processIndependentPressOnSub(MouseEvent e) {
    if (e.getButton() == MouseEvent.BUTTON1) {
      subBoardRenderer.statChanged = true;
      subBoardRenderer.bestmovesNum++;
      refresh();
    } else if (e.getButton() == MouseEvent.BUTTON3) {
      if (subBoardRenderer.bestmovesNum >= 1) {
        subBoardRenderer.statChanged = true;
        subBoardRenderer.bestmovesNum--;
        refresh();
      }
    }
  }

  public void doBranch(int moveTo) {
    if (subBoardRenderer.isShowingNormalBoard()) {
      subBoardRenderer.setDisplayedBranchLength(1);
      subBoardRenderer.wheeled = true;
    } else if (moveTo > 0) {
      {
        if (subBoardRenderer.getReplayBranch() > subBoardRenderer.getDisplayedBranchLength()) {
          subBoardRenderer.incrementDisplayedBranchLength(1);
          subBoardRenderer.wheeled = true;
        }
      }

    } else {
      if (subBoardRenderer.isShowingNormalBoard()) {
        subBoardRenderer.setDisplayedBranchLength(subBoardRenderer.getReplayBranch());
      } else {
        if (subBoardRenderer.getDisplayedBranchLength() > 1) {
          subBoardRenderer.incrementDisplayedBranchLength(-1);
          subBoardRenderer.wheeled = true;
        }
      }
    }
  }

  public void mouseEntered() {
    // TODO Auto-generated method stub
    lockUnlock.setVisible(true);
    topUntop.setVisible(true);
    btnClose.setVisible(true);
    if (Lizzie.config.noRefreshOnSub) {
      if (!subBoardRenderer.isMouseOver) refresh();
      subBoardRenderer.isMouseOver = true;
    }
  }

  public void mouseExited() {
    // TODO Auto-generated method stub
    topUntop.setVisible(false);
    lockUnlock.setVisible(false);
    btnClose.setVisible(false);
    if (Lizzie.config.noRefreshOnSub) {
      if (subBoardRenderer.isMouseOver) refresh();
      subBoardRenderer.isMouseOver = false;
    }
    subBoardRenderer.setDisplayedBranchLength(-2);
  }
}
