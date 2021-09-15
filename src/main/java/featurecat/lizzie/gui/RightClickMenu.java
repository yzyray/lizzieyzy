package featurecat.lizzie.gui;

import featurecat.lizzie.Lizzie;
import featurecat.lizzie.analysis.GameInfo;
import featurecat.lizzie.rules.Board;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JPopupMenu;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

public class RightClickMenu extends JPopupMenu {
  //	public static int mousex;
  //	public static int mousey;
  public static int[] coords;
  private final ResourceBundle resourceBundle =
      Lizzie.config.useLanguage == 0
          ? ResourceBundle.getBundle("l10n.DisplayStrings")
          : (Lizzie.config.useLanguage == 1
              ? ResourceBundle.getBundle("l10n.DisplayStrings", new Locale("zh", "CN"))
              : ResourceBundle.getBundle("l10n.DisplayStrings", new Locale("en", "US")));
  private JFontMenuItem findMove;
  private JFontMenuItem addblack;
  private JFontMenuItem addwhite;

  private JFontMenuItem priority;
  private JFontMenuItem clearPriority;
  private JFontMenuItem allow;
  private JFontMenuItem allow2;
  private JFontMenuItem avoid;
  private JFontMenuItem previousMove;
  private JFontMenuItem addSuggestionAsBranch;
  private JFontCheckBoxMenuItem avoid2;
  private JFontMenuItem cancelavoid;
  private JFontMenuItem reedit;
  private JFontMenuItem cleanupedit;
  private JFontMenuItem cleanedittemp;
  Separator sep1 = new Separator();
  Separator sep = new Separator();
  private int[] mouseOverCoordinateTemp;

  public RightClickMenu() {

    PopupMenuListener listener =
        new PopupMenuListener() {
          public void popupMenuCanceled(PopupMenuEvent e) {}

          public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
            //				if (Lizzie.leelaz.isPondering() && isforcing) {
            //					if (isallow) {
            //						Lizzie.leelaz.analyzeAvoid("allow", Lizzie.board.getcurrentturn(), allowcoords,
            // 1);
            //					} else {
            //						Lizzie.leelaz.analyzeAvoid("avoid",  avoidcoords, 60);
            //					}
            //				}
            //				if (Lizzie.leelaz.isPondering() && !isforcing) {
            //					Lizzie.leelaz.ponder();
            //				}
            if (Lizzie.frame.isMouseOver) {
              Lizzie.frame.mouseOverCoordinate = LizzieFrame.outOfBoundCoordinate;
              Lizzie.frame.isMouseOver = false;
              Lizzie.frame.clearMoved();
            }
            if (Lizzie.frame.independentMainBoard != null
                && Lizzie.frame.independentMainBoard.isMouseOver) {
              Lizzie.frame.independentMainBoard.mouseOverCoordinate =
                  LizzieFrame.outOfBoundCoordinate;
              Lizzie.frame.independentMainBoard.isMouseOver = false;
              Lizzie.frame.independentMainBoard.clearMoved();
            }
            Timer timer = new Timer();
            timer.schedule(
                new TimerTask() {
                  public void run() {
                    Lizzie.frame.isShowingRightMenu = false;
                    this.cancel();
                  }
                },
                200);
          }

          public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
            if (Lizzie.config.isFloatBoardMode())
              mouseOverCoordinateTemp = Lizzie.frame.independentMainBoard.mouseOverCoordinate;
            else mouseOverCoordinateTemp = Lizzie.frame.mouseOverCoordinate;
            Lizzie.frame.isShowingRightMenu = true;
            if (Lizzie.frame.independentMainBoard != null) {
              if ((Lizzie.frame.independentMainBoard.isMouseOver
                      || Lizzie.frame.isMouseOver
                      || Lizzie.frame.independentMainBoard.boardRenderer.isShowingBranch())
                  && !Lizzie.frame.isAnaPlayingAgainstLeelaz) {
                addSuggestionAsBranch.setVisible(true);
              } else {
                addSuggestionAsBranch.setVisible(false);
              }
            } else if ((Lizzie.frame.isMouseOver || LizzieFrame.boardRenderer.isShowingBranch())
                && !Lizzie.frame.isAnaPlayingAgainstLeelaz) {
              addSuggestionAsBranch.setVisible(true);
            } else {
              addSuggestionAsBranch.setVisible(false);
            }

            if (Lizzie.board.boardstatbeforeedit == "") {
              cleanupedit.setVisible(false);
              if (Lizzie.board.boardstatafteredit == "") {
                cleanedittemp.setVisible(false);
              }
            } else {
              cleanupedit.setVisible(true);
              cleanedittemp.setVisible(true);
            }
            if (Lizzie.board.boardstatafteredit == "") {
              reedit.setVisible(false);
            } else {
              reedit.setVisible(true);
              cleanedittemp.setVisible(true);
            }
            if (Lizzie.frame.isPlayingAgainstLeelaz || Lizzie.frame.isAnaPlayingAgainstLeelaz) {
              priority.setVisible(false);
              clearPriority.setVisible(false);
              findMove.setVisible(false);
              addblack.setVisible(false);
              addwhite.setVisible(false);
              allow.setVisible(false);
              allow2.setVisible(false);
              avoid.setVisible(false);
              avoid2.setVisible(false);
              cancelavoid.setVisible(false);
              sep.setVisible(false);
              sep1.setVisible(false);
              previousMove.setText(resourceBundle.getString("RightClickMenu.regretOne")); // ("悔棋");
            } else {
              previousMove.setText(
                  resourceBundle.getString("RightClickMenu.previousMove")); // ("回退一手");
              findMove.setVisible(true);
              addblack.setVisible(true);
              addwhite.setVisible(true);
              if (Lizzie.leelaz.isKatagoCustom) {
                priority.setVisible(true);
                clearPriority.setVisible(true);
                sep1.setVisible(true);
              } else {
                priority.setVisible(false);
                clearPriority.setVisible(false);
                sep1.setVisible(false);
              }
              sep.setVisible(true);
              allow.setVisible(true);
              avoid.setVisible(true);
              avoid2.setVisible(true);
              if (LizzieFrame.allowcoords != "") {
                allow2.setVisible(true);
                if (LizzieFrame.avoidcoords != "") {
                  cancelavoid.setVisible(true);
                }
              } else {
                allow2.setVisible(false);
              }
            }
          }
          //	}
        };

    this.addPopupMenuListener(listener);
    ImageIcon iconBack = new ImageIcon();
    ImageIcon iconBlack = new ImageIcon();
    ImageIcon iconWhite = new ImageIcon();
    ImageIcon iconRecycle = new ImageIcon();
    ImageIcon iconForbidPoint = new ImageIcon();
    ImageIcon iconSetPoint = new ImageIcon();
    ImageIcon iconUppoint = new ImageIcon();
    ImageIcon iconForward = new ImageIcon();
    ImageIcon iconAddPoint = new ImageIcon();
    ImageIcon iconSearch = new ImageIcon();
    try {
      iconForward.setImage(
          ImageIO.read(AnalysisFrame.class.getResourceAsStream("/assets/right.png")));
      iconBack.setImage(ImageIO.read(AnalysisFrame.class.getResourceAsStream("/assets/left.png")));
      iconBlack.setImage(
          ImageIO.read(AnalysisFrame.class.getResourceAsStream("/assets/smallblack1.png")));
      iconWhite.setImage(
          ImageIO.read(AnalysisFrame.class.getResourceAsStream("/assets/smallwhite.png")));
      iconRecycle.setImage(
          ImageIO.read(AnalysisFrame.class.getResourceAsStream("/assets/recycle.png")));
      iconForbidPoint.setImage(
          ImageIO.read(AnalysisFrame.class.getResourceAsStream("/assets/forbidpoint.png")));
      iconSetPoint.setImage(
          ImageIO.read(AnalysisFrame.class.getResourceAsStream("/assets/setpoint.png")));
      iconUppoint.setImage(
          ImageIO.read(AnalysisFrame.class.getResourceAsStream("/assets/uppoint.png")));
      iconAddPoint.setImage(
          ImageIO.read(AnalysisFrame.class.getResourceAsStream("/assets/addpoint.png")));
      iconSearch.setImage(
          ImageIO.read(AnalysisFrame.class.getResourceAsStream("/assets/search.png")));
    } catch (IOException e1) {
      // TODO Auto-generated catch block
      e1.printStackTrace();
    }
    // insertmode = new JFontMenuItem("进入插入棋子模式");
    // quitinsert = new JFontMenuItem("退出插入棋子模式");
    findMove = new JFontMenuItem(resourceBundle.getString("RightClickMenu.findMove"));
    findMove.setIcon(iconSearch);
    priority = new JFontMenuItem(resourceBundle.getString("RightClickMenu.priority"));
    priority.setIcon(iconUppoint);
    clearPriority = new JFontMenuItem(resourceBundle.getString("RightClickMenu.clearPriority"));
    clearPriority.setIcon(iconRecycle);
    addSuggestionAsBranch =
        new JFontMenuItem(
            resourceBundle.getString("RightClickMenu.addSuggestionAsBranch")); // ("将变化图添加为分支");
    addblack = new JFontMenuItem(resourceBundle.getString("RightClickMenu.addblack")); // ("插入黑子");
    addblack.setIcon(iconBlack);
    addwhite = new JFontMenuItem(resourceBundle.getString("RightClickMenu.addwhite")); // ("插入白子");
    addwhite.setIcon(iconWhite);
    // deleteone = new JFontMenuItem("更改棋子位置");
    allow = new JFontMenuItem(resourceBundle.getString("RightClickMenu.allow")); // ("只分析此点");
    allow.setIcon(iconSetPoint);
    allow2 = new JFontMenuItem(resourceBundle.getString("RightClickMenu.allow2")); // ("增加分析此点");
    allow2.setIcon(iconAddPoint);
    avoid = new JFontMenuItem(resourceBundle.getString("RightClickMenu.avoid")); // ("不分析此点");
    avoid.setIcon(iconForbidPoint);
    avoid2 =
        new JFontCheckBoxMenuItem(
            resourceBundle.getString("RightClickMenu.avoid2")); // ("持续分析/不分析");
    cancelavoid =
        new JFontMenuItem(resourceBundle.getString("RightClickMenu.cancelavoid")); // ("清除分析与不分析");
    cancelavoid.setIcon(iconRecycle);
    cleanedittemp =
        new JFontMenuItem(resourceBundle.getString("RightClickMenu.cleanedittemp")); // ("清除编辑缓存");
    cleanedittemp.setIcon(iconRecycle);
    // test=new JFontMenuItem("测试删除棋子");
    // test2=new JFontMenuItem("测试恢复棋盘状态");
    reedit = new JFontMenuItem(resourceBundle.getString("RightClickMenu.reedit")); // ("恢复到编辑前");
    reedit.setIcon(iconForward);
    cleanupedit =
        new JFontMenuItem(resourceBundle.getString("RightClickMenu.cleanupedit")); // ("恢复到编辑后");
    cleanupedit.setIcon(iconBack);
    previousMove =
        new JFontMenuItem(resourceBundle.getString("RightClickMenu.previousMove")); // ("回退一手");
    previousMove.setIcon(iconBack);
    // this.add(addblack);
    // this.add(addwhite);
    this.add(allow);
    this.add(allow2);
    this.add(avoid);
    this.add(cancelavoid);
    this.add(avoid2);
    this.add(sep);
    this.add(priority);
    this.add(clearPriority);
    this.add(sep1);
    this.add(previousMove);
    this.add(findMove);
    this.add(addblack);
    this.add(addwhite);
    this.add(cleanedittemp);
    this.add(reedit);
    this.add(cleanupedit);
    this.add(addSuggestionAsBranch);

    addMouseListener(
        new MouseAdapter() {
          public void mouseExited(MouseEvent e) {
            //	                Lizzie.frame.mouseOverCoordinate = LizzieFrame.outOfBoundCoordinate;
            //	                Lizzie.frame.isMouseOver = false;
            //	                Lizzie.frame.clearMoved();
          }
        });
    priority.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            if (!Lizzie.leelaz.isPondering()) Lizzie.leelaz.togglePonder();
            String coord = Board.convertCoordinatesToName(coords[0], coords[1]);
            Lizzie.frame.priorityMoveCoords.add(coord);
            Lizzie.leelaz.sendCommand("setmaxpolicy " + coord + " 1.1");
          }
        });

    clearPriority.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            Lizzie.leelaz.sendCommand("clearpolicy");
            Lizzie.frame.priorityMoveCoords.clear();
          }
        });

    previousMove.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            Lizzie.frame.undoForRightClick();
          }
        });

    addSuggestionAsBranch.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            if (Lizzie.config.isFloatBoardMode())
              Lizzie.frame.independentMainBoard.mouseOverCoordinate = mouseOverCoordinateTemp;
            else Lizzie.frame.mouseOverCoordinate = mouseOverCoordinateTemp;
            Lizzie.frame.addSuggestionAsBranch();
            if (Lizzie.config.isFloatBoardMode())
              Lizzie.frame.independentMainBoard.mouseOverCoordinate =
                  LizzieFrame.outOfBoundCoordinate;
            else Lizzie.frame.mouseOverCoordinate = LizzieFrame.outOfBoundCoordinate;
          }
        });

    cleanedittemp.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            cleanedittemp();
          }
        });

    reedit.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            GameInfo gameInfo = Lizzie.board.getHistory().getGameInfo();
            reedit();
            Lizzie.board.getHistory().setGameInfo(gameInfo);
            Lizzie.frame.refresh();
          }
        });

    cleanupedit.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            GameInfo gameInfo = Lizzie.board.getHistory().getGameInfo();
            cleanupedit();
            Lizzie.board.getHistory().setGameInfo(gameInfo);
            Lizzie.frame.refresh();
          }
        });

    allow2.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            allow2();
          }
        });

    findMove.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            Lizzie.board.findMove(coords);
            Lizzie.frame.refresh();
          }
        });

    addblack.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            GameInfo gameInfo = Lizzie.board.getHistory().getGameInfo();
            addblack();
            Lizzie.board.getHistory().setGameInfo(gameInfo);
          }
        });

    addwhite.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            GameInfo gameInfo = Lizzie.board.getHistory().getGameInfo();
            addwhite();
            Lizzie.board.getHistory().setGameInfo(gameInfo);
          }
        });

    allow.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            allow();
          }
        });
    avoid.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            avoid();
          }
        });
    avoid2.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            avoid2();
          }
        });
    cancelavoid.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            cancelavoid();
          }
        });
  }

  private void cleanupedit() {
    Lizzie.board.cleanedit();
  }

  private void cleanedittemp() {
    Lizzie.board.cleanedittemp();
  }

  private void reedit() {
    Lizzie.board.reedit();
  }

  private void addblack() {
    if (Lizzie.board.iscoordsempty(coords[0], coords[1])) {
      Lizzie.frame.insertMove(coords, true);
    }
  }

  private void cancelavoid() {
    LizzieFrame.allowcoords = "";
    LizzieFrame.avoidcoords = "";
    // move = 0;
    Lizzie.leelaz.ponder();
    Lizzie.board.clearbestmovesafter(Lizzie.board.getHistory().getStart());
    LizzieFrame.boardRenderer.removeSelectedRect();
    Lizzie.frame.refresh();
  }

  private void addwhite() {
    if (Lizzie.board.iscoordsempty(coords[0], coords[1])) {
      Lizzie.frame.insertMove(coords, false);
    }
  }

  private void allow() {
    if (Lizzie.board.iscoordsempty(coords[0], coords[1])) {
      LizzieFrame.allowcoords = Board.convertCoordinatesToName(coords[0], coords[1]);
    }
    LizzieFrame.isforcing = true;
    LizzieFrame.isallow = true;
    LizzieFrame.isTempForcing = true;
    LizzieFrame.avoidcoords = "";
    Lizzie.leelaz.Pondering();
    Lizzie.leelaz.analyzeAvoid("allow", LizzieFrame.allowcoords, 1);
    Lizzie.board.clearbestmovesafter(Lizzie.board.getHistory().getStart());

    LizzieFrame.boardRenderer.drawAllSelectedRectByCoords(true, LizzieFrame.allowcoords);
    Lizzie.frame.refresh();
  }

  private void allow2() {
    if (Lizzie.board.iscoordsempty(coords[0], coords[1])) {
      if (LizzieFrame.allowcoords != "") {
        LizzieFrame.allowcoords += "," + Board.convertCoordinatesToName(coords[0], coords[1]);
      } else {
        LizzieFrame.allowcoords = Board.convertCoordinatesToName(coords[0], coords[1]);
      }
    }
    LizzieFrame.isforcing = true;
    LizzieFrame.isallow = true;
    LizzieFrame.avoidcoords = "";
    Lizzie.leelaz.Pondering();
    Lizzie.leelaz.analyzeAvoid("allow", LizzieFrame.allowcoords, 1);
    Lizzie.board.clearbestmovesafter(Lizzie.board.getHistory().getStart());
    LizzieFrame.boardRenderer.drawAllSelectedRectByCoords(true, LizzieFrame.allowcoords);
    Lizzie.frame.refresh();
  }

  private void avoid() {
    LizzieFrame.isTempForcing = true;
    LizzieFrame.isforcing = true;
    LizzieFrame.isallow = false;
    if (Lizzie.board.iscoordsempty(coords[0], coords[1])) {
      if (LizzieFrame.avoidcoords != "") {
        LizzieFrame.avoidcoords += "," + Board.convertCoordinatesToName(coords[0], coords[1]);
      } else {
        LizzieFrame.avoidcoords = Board.convertCoordinatesToName(coords[0], coords[1]);
      }
    }
    Lizzie.leelaz.analyzeAvoid("avoid", LizzieFrame.avoidcoords, Lizzie.config.selectAvoidMoves);
    Lizzie.board.clearbestmovesafter(Lizzie.board.getHistory().getStart());
    LizzieFrame.boardRenderer.drawAllSelectedRectByCoords(false, LizzieFrame.avoidcoords);
    Lizzie.frame.repaint();
  }

  private void avoid2() {
    LizzieFrame.isKeepForcing = !LizzieFrame.isKeepForcing;
  }

  public void setCoords(int[] coords) {
    RightClickMenu.coords = coords;
  }
}
