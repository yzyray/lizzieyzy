package featurecat.lizzie.gui;

import static java.awt.event.KeyEvent.*;

import featurecat.lizzie.Lizzie;
import featurecat.lizzie.analysis.EngineManager;
import featurecat.lizzie.rules.Tsumego;
import featurecat.lizzie.util.Utils;
import java.awt.event.*;
import javax.swing.SwingUtilities;

public class Input implements MouseListener, KeyListener, MouseWheelListener, MouseMotionListener {
  // public static boolean isinsertmode = false;
  public static boolean tempDrag = false;
  public static boolean Draggedmode = false;
  public static int insert = 0;
  public static boolean selectMode = false;

  @Override
  public void mouseClicked(MouseEvent e) {
    if (e.isAltDown()
        && !SwingUtilities.isMiddleMouseButton(e)
        && (LizzieFrame.allowcoords != "" || LizzieFrame.avoidcoords != ""))
      LizzieFrame.menu.clearSelect.doClick();
  }

  @Override
  public void mousePressed(MouseEvent e) {
    if (Lizzie.frame.isInScoreMode) {
      if (e.getButton() == MouseEvent.BUTTON1)
        Lizzie.frame.leftClickInScoreMode(Utils.zoomOut(e.getX()), Utils.zoomOut(e.getY()));
      return;
    }
    if (tempDrag) {
      Lizzie.frame.DraggedReleased(Utils.zoomOut(e.getX()), Utils.zoomOut(e.getY()));
      tempDrag = false;
    } else {
      if (Lizzie.frame.isShowingRightMenu) return;
      if (!SwingUtilities.isMiddleMouseButton(e) && (selectMode || e.isAltDown())) {
        Lizzie.frame.selectPressed(Utils.zoomOut(e.getX()), Utils.zoomOut(e.getY()), e.isAltDown());
        return;
      }
      Lizzie.frame.processCommentMousePressed(e);
      //      if (Lizzie.frame.processCommentMousePressed(e)) {
      //        return;
      //      }
      if (Lizzie.frame.processPressOnSub(e)) {
        Lizzie.frame.refresh();
        return;
      }
      if (EngineManager.isEngineGame) {
        if (e.getButton() == MouseEvent.BUTTON1)
          Lizzie.frame.onClickedForManul(Utils.zoomOut(e.getX()), Utils.zoomOut(e.getY()));
        return;
      }
      if (Lizzie.config.isFloatBoardMode()) {
        Lizzie.frame.onClickedWinrateOnly(Utils.zoomOut(e.getX()), Utils.zoomOut(e.getY()));
        return;
      }
      if (e.getButton() == MouseEvent.BUTTON1) // left click
      {
        if (e.getClickCount() == 2
            && !Lizzie.frame.isTrying
            && !Lizzie.frame.isPlayingAgainstLeelaz
            && !Lizzie.frame.isAnaPlayingAgainstLeelaz
            && Lizzie.config.allowDoubleClick) { // TODO: Maybe need to delay check
          Lizzie.frame.onDoubleClicked(Utils.zoomOut(e.getX()), Utils.zoomOut(e.getY()));
        } else {
          if (Lizzie.frame.tryToMarkup(Utils.zoomOut(e.getX()), Utils.zoomOut(e.getY()))) return;
          if (insert == 0) {
            Lizzie.frame.onClicked(Utils.zoomOut(e.getX()), Utils.zoomOut(e.getY()));
          } else if (insert == 1) {
            if (Lizzie.frame.iscoordsempty(Utils.zoomOut(e.getX()), Utils.zoomOut(e.getY()))) {
              int[] coords =
                  Lizzie.frame.convertmousexytocoords(
                      Utils.zoomOut(e.getX()), Utils.zoomOut(e.getY()));
              Lizzie.frame.insertMove(coords, true);
            }
          } else if (insert == 2) {
            if (Lizzie.frame.iscoordsempty(Utils.zoomOut(e.getX()), Utils.zoomOut(e.getY()))) {
              int[] coords =
                  Lizzie.frame.convertmousexytocoords(
                      Utils.zoomOut(e.getX()), Utils.zoomOut(e.getY()));
              Lizzie.frame.insertMove(coords, false);
            }
          }
        }

      } else if (e.getButton() == MouseEvent.BUTTON3) // right click
      {
        if (Lizzie.frame.onClickedRight(Utils.zoomOut(e.getX()), Utils.zoomOut(e.getY()))) return;
        if (!Lizzie.frame.openRightClickMenu(Utils.zoomOut(e.getX()), Utils.zoomOut(e.getY())))
          Lizzie.frame.undoForRightClick();
      }
    }
    // Lizzie.frame.toolbar.setTxtUnfocuse();
  }

  @Override
  public void mouseReleased(MouseEvent e) {
    if (selectMode || (e.isAltDown() && e.getButton() != MouseEvent.BUTTON2)) {
      Lizzie.frame.selectReleased(Utils.zoomOut(e.getX()), Utils.zoomOut(e.getY()));
      return;
    }
    if (Draggedmode
        && !Lizzie.frame.isTrying
        && !LizzieFrame.urlSgf
        && !Lizzie.frame.isPlayingAgainstLeelaz
        && !Lizzie.frame.isAnaPlayingAgainstLeelaz
        && Lizzie.config.allowDrag) {
      Lizzie.frame.DraggedReleased(Utils.zoomOut(e.getX()), Utils.zoomOut(e.getY()));
      return;
    }
    if (SwingUtilities.isMiddleMouseButton(e)) {
      // if (Lizzie.frame.syncBoard) return;
      if (Lizzie.frame.isShowingRightMenu) return;
      Lizzie.frame.playCurrentVariation();
    }
  }

  @Override
  public void mouseEntered(MouseEvent e) {
    Lizzie.frame.stopTemporaryBoardMaybe();
  }

  @Override
  public void mouseExited(MouseEvent e) {
    if (!Lizzie.frame.isShowingRightMenu) Lizzie.frame.onMouseExited();
  }

  @Override
  public void mouseDragged(MouseEvent e) {
    if (Lizzie.frame.isPlayingAgainstLeelaz || Lizzie.frame.isContributing) return;
    if (!SwingUtilities.isMiddleMouseButton(e) && (selectMode || e.isAltDown())) {
      Lizzie.frame.selectDragged(Utils.zoomOut(e.getX()), Utils.zoomOut(e.getY()));
      return;
    }
    if (Draggedmode
        && !Lizzie.frame.isTrying
        && !LizzieFrame.urlSgf
        && !Lizzie.frame.isPlayingAgainstLeelaz
        && !Lizzie.frame.isAnaPlayingAgainstLeelaz
        && Lizzie.config.allowDrag) {
      Lizzie.frame.DraggedDragged(Utils.zoomOut(e.getX()), Utils.zoomOut(e.getY()));
      return;
    }
    Lizzie.frame.onMouseDragged(Utils.zoomOut(e.getX()), Utils.zoomOut(e.getY()));
  }

  @Override
  public void mouseMoved(MouseEvent e) {
    if (tempDrag) Lizzie.frame.DraggedDragged(Utils.zoomOut(e.getX()), Utils.zoomOut(e.getY()));
    else {
      if (selectMode || e.isAltDown()) {
        return;
      }
      if (Draggedmode
          && !Lizzie.frame.isTrying
          && !LizzieFrame.urlSgf
          && !Lizzie.frame.isPlayingAgainstLeelaz
          && !Lizzie.frame.isAnaPlayingAgainstLeelaz
          && Lizzie.config.allowDrag) {
        Lizzie.frame.DraggedMoved(Utils.zoomOut(e.getX()), Utils.zoomOut(e.getY()));
        return;
      }
      if (!Lizzie.frame.onMouseMoved(Utils.zoomOut(e.getX()), Utils.zoomOut(e.getY())))
        Lizzie.board.clearPressStoneInfo(null);
      // Lizzie.frame.processCommentMouseOverd(e);
    }
  }

  @Override
  public void keyTyped(KeyEvent e) {}

  public static void undo() {
    LizzieFrame.undo(1);
  }

  private void undoToChildOfPreviousWithVariation() {
    // Undo until the position just after the junction position.
    // If we are already on such a position, we go to
    // the junction position for convenience.
    // Use cases:
    // [Delete branch] Call this function and then deleteMove.
    // [Go to junction] Call this function twice.
    if (!Lizzie.board.undoToChildOfPreviousWithVariation()) Lizzie.board.previousMove(true);
  }

  private void undoToFirstParentWithVariations() {
    if (Lizzie.board.undoToChildOfPreviousWithVariation()) {
      Lizzie.board.previousMove(true);
    }
  }

  private void goCommentNode(boolean moveForward) {
    if (moveForward) {
      LizzieFrame.redo(Lizzie.board.getHistory().getCurrentHistoryNode().goToNextNodeWithComment());
    } else {
      LizzieFrame.undo(
          Lizzie.board.getHistory().getCurrentHistoryNode().goToPreviousNodeWithComment());
    }
  }

  public static void redo() {
    LizzieFrame.redo(1);
  }

  //  private void startRawBoard() {
  //    if (!Lizzie.config.showRawBoard) {
  //      Lizzie.frame.startRawBoard();
  //    }
  //    Lizzie.config.showRawBoard = true;
  //  }
  //
  //  private void stopRawBoard() {
  //    Lizzie.frame.stopRawBoard();
  //    Lizzie.config.showRawBoard = false;
  //  }

  //  private void toggleHints() {
  //    Lizzie.config.toggleShowBranch();
  //    Lizzie.config.showSubBoard =
  //        Lizzie.config.showNextMoves = Lizzie.config.showBestMoves = Lizzie.config.showBranch;
  //  }

  public static void nextBranch() {
    if (Lizzie.frame.isPlayingAgainstLeelaz || Lizzie.frame.isAnaPlayingAgainstLeelaz) {
      return;
      // Lizzie.frame.isPlayingAgainstLeelaz = false;
    }
    Lizzie.board.nextBranch();
  }

  public static void previousBranch() {
    if (Lizzie.frame.isPlayingAgainstLeelaz || Lizzie.frame.isAnaPlayingAgainstLeelaz) {
      return;
      // Lizzie.frame.isPlayingAgainstLeelaz = false;
    }
    Lizzie.board.previousBranch();
  }

  private void moveBranchUp() {
    Lizzie.board.moveBranchUp();
    Lizzie.frame.renderVarTree(0, 0, false, false);
  }

  private void moveBranchDown() {
    Lizzie.board.moveBranchDown();
    Lizzie.frame.renderVarTree(0, 0, false, false);
  }

  private void deleteMove() {
    Lizzie.board.deleteMove();
  }

  private void deleteBranch() {
    Lizzie.board.deleteBranch();
  }

  private boolean controlIsPressed(KeyEvent e) {
    boolean mac = System.getProperty("os.name", "").toUpperCase().startsWith("MAC");
    return e.isControlDown() || (mac && e.isMetaDown());
  }

  //  private void toggleShowDynamicKomi() {
  //    Lizzie.config.showDynamicKomi = !Lizzie.config.showDynamicKomi;
  //  }

  @Override
  public void keyPressed(KeyEvent e) {
    // If any controls key is pressed, let's disable analysis mode.
    // This is probably the user attempting to exit analysis mode.
    //  int a = e.getKeyCode();
    switch (e.getKeyCode()) {
      case VK_E:
        if (controlIsPressed(e)) {
          Lizzie.frame.shareSGF();
        } else if (e.isAltDown()) {
          Lizzie.frame.startEngineGameDialog();
        } else Lizzie.frame.toggleGtpConsole();
        break;
      case VK_RIGHT:
        // if (isinsertmode) {
        // return;
        // }
        if (e.isShiftDown()) {
          if (controlIsPressed(e) && e.isAltDown()) {
            Lizzie.board.exchangeBlackWhite();
          }
          moveBranchDown();
        } else {
          if (controlIsPressed(e) && e.isAltDown()) {
            Lizzie.board.SpinAndMirror(1);
          } else nextBranch();
        }
        break;

      case VK_LEFT:
        // if (isinsertmode) {
        // return;
        // }
        if (e.isShiftDown()) {

          moveBranchUp();
        } else if (controlIsPressed(e)) {
          if (e.isAltDown()) {
            Lizzie.board.SpinAndMirror(2);
          } else undoToFirstParentWithVariations();
        } else {

          previousBranch();
        }
        break;
      case VK_U:
        Lizzie.frame.toggleBestMoves();
        break;
      case VK_UP:
        // if (isinsertmode) {
        // return;
        // }
        if (controlIsPressed(e) && e.isShiftDown()) {
          goCommentNode(false);
        } else if (e.isShiftDown()) {
          undoToChildOfPreviousWithVariation();
        } else if (controlIsPressed(e)) {
          if (e.isAltDown()) {
            Lizzie.board.SpinAndMirror(3);
          } else LizzieFrame.undoNoRefresh(10);
        } else {
          if (LizzieFrame.boardRenderer.isShowingBranch()) {
            Lizzie.frame.doBranch(-1);
          } else {
            LizzieFrame.undoNoRefresh(1);
          }
        }
        break;

      case VK_PAGE_DOWN:
        if (LizzieFrame.boardRenderer.isShowingBranch()) {
          Lizzie.frame.doBranch(1);
        } else {
          // Lizzie.frame.noautocounting();
          LizzieFrame.redo(10);
        }

        break;

      case VK_DOWN:
        // if (isinsertmode) {
        // return;
        // }
        if (controlIsPressed(e) && e.isShiftDown()) {
          goCommentNode(true);
        } else if (controlIsPressed(e)) {
          // Lizzie.frame.noautocounting();
          if (e.isAltDown()) {
            Lizzie.board.SpinAndMirror(4);
          } else LizzieFrame.redoNoRefresh(10);
        } else {
          if (LizzieFrame.boardRenderer.isShowingBranch()) {
            Lizzie.frame.doBranch(1);
          } else {
            LizzieFrame.redoNoRefresh(1);
          }
        }
        break;

      case VK_N:
        if (e.isAltDown() && !Lizzie.leelaz.noAnalyze) {
          Lizzie.frame.startAnalyzeGameDialog();
        } else {
          Lizzie.frame.startNewGame();
        }
        break;
      case VK_SPACE:
        Lizzie.frame.togglePonderMannul();
        break;

      case VK_L:
        // if (e.isAltDown()) {
        //  Lizzie.config.toggleShowLcbWinrate();
        // Lizzie.frame.setButtomBtnFont();
        // } else
        Lizzie.frame.setAsMain();
        break;

      case VK_P:
        Lizzie.board.pass();
        break;

      case VK_COMMA:
        if (e.isAltDown()) {
          Lizzie.frame.genmove();
        } else if (!Lizzie.config.showSuggestionVariations) {
          if (Lizzie.frame.isMouseOver) Lizzie.frame.playCurrentVariation();
          else Lizzie.frame.playBestMove();
        } else {
          if (!Lizzie.frame.playCurrentVariation()) Lizzie.frame.playBestMove();
        }
        break;

      case VK_M:
        if (controlIsPressed(e)) {
          Lizzie.config.toggleShowMoveAllInBranch();
        } else if (e.isAltDown()) {
          Lizzie.config.toggleShowMoveRankMark();
        } else {
          Lizzie.config.toggleShowMoveNumber();
        }
        break;

      case VK_J:
        Lizzie.config.toggleShowNextMoves();
        break;

      case VK_F:
        if (e.isShiftDown()) {
          Lizzie.config.toggleShowSuggestionVariations();
        } else {
          if (controlIsPressed(e)) Lizzie.config.toggleLargeSubBoard();
          else {
            Lizzie.frame.toggleShowCandidates();
          }
        }
        //  Lizzie.frame.refresh();
        break;

      case VK_H:
        // Lizzie.config.toggleHandicapInsteadOfWinrate();
        //        if (e.isAltDown()) {
        //          Lizzie.config.showHeat = !Lizzie.config.showHeat;
        //          Lizzie.frame.subBoardRenderer.showHeat = Lizzie.config.showHeat;
        //          Lizzie.frame.subBoardRenderer.clearBranch();
        //          Lizzie.frame.subBoardRenderer.removeHeat();
        //        } else
        Lizzie.leelaz.toggleHeatmap(false);
        break;

      case VK_PAGE_UP:
        if (LizzieFrame.boardRenderer.isShowingBranch()) {
          Lizzie.frame.doBranch(-1);
        } else {
          // Lizzie.frame.noautocounting();
          LizzieFrame.undo(10);
        }

        break;

      case VK_I:
        // stop the ponder
        // if (Lizzie.leelaz.isPondering()) Lizzie.leelaz.togglePonder();
        if (e.isControlDown()) {
          SetBoardSize st = new SetBoardSize();
          st.setVisible(true);
        } else LizzieFrame.editGameInfo();
        break;

      case VK_S:
        if (e.isControlDown() && e.isShiftDown()) {
          LizzieFrame.saveFile(true);
        } else if (e.isControlDown() && e.isAltDown()) {
          LizzieFrame.saveCurrentBranch();
        } else if (e.isShiftDown()) {
          Lizzie.frame.saveImage(
              Lizzie.frame.statx,
              Lizzie.frame.staty,
              (int) (Lizzie.frame.grw * 1.03),
              Lizzie.frame.grh + Lizzie.frame.stath);
        } else {
          if (e.isAltDown()) {
            Lizzie.frame.saveMainBoardPicture();
          } else {
            if (e.isControlDown()) {
              // Lizzie.frame.saveSubBoardPicture();
              Lizzie.frame.saveOriFile();
            } else LizzieFrame.saveFile(false);
          }
        }
        break;

      case VK_O:
        if (e.isControlDown()) {
          Lizzie.frame.openFileWithAna(false);
        } else if (e.isAltDown()) {
          Lizzie.frame.openBoardSync();
        } else if (e.isShiftDown()) {
          Lizzie.frame.bowser(
              "https://home.yikeweiqi.com/#/live",
              Lizzie.resourceBundle.getString("BottomToolbar.yikeLive"),
              true);
        } else {
          Lizzie.frame.openFile();
        }
        break;

      case VK_V:
        // Lizzie.frame.getBowserUrl();
        if (controlIsPressed(e)) {
          // if (isinsertmode) {
          // return;
          // }
          Lizzie.frame.pasteSgf();
        } else if (e.isAltDown()) {
          Lizzie.config.showSuggestionVariations = !Lizzie.config.showSuggestionVariations;
          Lizzie.config.uiConfig.put(
              "show-suggestion-variations", Lizzie.config.showSuggestionVariations);
        } else {
          Lizzie.frame.tryPlay(false);
        }
        break;

      case VK_HOME:
        // Lizzie.frame.noautocounting();
        // if (isinsertmode) {
        // return;
        // }
        if (controlIsPressed(e)) {
          Lizzie.board.clear(false);
          if (Lizzie.leelaz.isPondering()) {
            Lizzie.leelaz.ponder();
          }
        } else {
          Lizzie.frame.firstMove();
        }
        break;

      case VK_END:
        // Lizzie.frame.noautocounting();
        // if (isinsertmode) {
        // return;
        // }
        Lizzie.frame.lastMove();
        break;

      case VK_X:
        if (e.isShiftDown()) {
          Lizzie.frame.openConfigDialog2(0);
        }
        //        else if (controlIsPressed(e)) {
        //          Lizzie.frame.openConfigDialog();
        //        }
        else if (e.isAltDown()) {
          LizzieFrame.openMoreEngineDialog();
        } else {
          if (!Lizzie.frame.showControls) {
            //             if (Lizzie.leelaz.isPondering()) {
            //             wasPonderingWhenControlsShown = true;
            //             Lizzie.leelaz.togglePonder();
            //             } else {
            //             wasPonderingWhenControlsShown = false;
            //             }
            Lizzie.frame.setVarTreeVisible(false);
            if (Lizzie.frame.listScrollpane.isVisible())
              Lizzie.frame.listScrollpane.setVisible(false);
            if (Lizzie.frame.commentScrollPane.isVisible())
              Lizzie.frame.commentScrollPane.setVisible(false);
            if (Lizzie.frame.blunderContentPane.isVisible())
              Lizzie.frame.blunderContentPane.setVisible(false);
            Lizzie.frame.drawControls();
            // Lizzie.frame.showControls = true;
          }
        }
        break;

      case VK_W:
        if (controlIsPressed(e)) {
          Lizzie.config.toggleLargeWinrate();
        } else if (e.isAltDown()) Lizzie.config.toggleShowWinrate();
        break;

      case VK_G:
        if (e.isAltDown()) {
          Lizzie.config.toggleShowListPane();
        } else if (e.isShiftDown()) Lizzie.config.toggleShowVariationGraph();
        else Lizzie.frame.tryToRefreshVariation();
        break;

      case VK_T:
        if (controlIsPressed(e)) {
          Lizzie.config.toggleShowCommentNodeColor();
        } else if (e.isAltDown()) {
          Lizzie.config.toggleShowComment();
        }
        //        else if (e.isShiftDown()) {
        //                }
        else {
          Lizzie.frame.togglePolicy();
        }
        break;

      case VK_Y:
        //  Lizzie.config.toggleNodeColorMode();
        Lizzie.frame.toggleBadMoves();
        break;

      case VK_C:
        if (e.isAltDown()) {
          Lizzie.frame.copySubBoard();
        } else if (e.isShiftDown()) {
          Lizzie.frame.saveMainBoardToClipboard();
        } else if (controlIsPressed(e)) {
          Lizzie.frame.copySgf();
        } else {
          Lizzie.config.toggleCoordinates();
        }
        break;

      case VK_ENTER:
        if (e.isAltDown()) {
          Lizzie.frame.continueAiPlaying(false, true, true, true);
        } else {
          Lizzie.frame.continueAiPlaying(true, true, true, true);
        }
        break;

      case VK_B:
        if (e.isAltDown()) {
          Lizzie.frame.shareSGF();
        } else {
          if (e.isControlDown()) {
            Lizzie.frame.flashAnalyzeGame(true, false);
          } else {
            Tsumego tsumego = new Tsumego();
            tsumego.getCoverSideAndIndex();
            tsumego.buildCoverWall();
            Lizzie.frame.moveToMainTrunk();
          }
        }
        break;
      case VK_DELETE:
      case VK_BACK_SPACE:
        // if (isinsertmode) {
        // return;
        // }
        if (e.isAltDown()) {
          Lizzie.board.clearBestMovesAfter(Lizzie.board.getHistory().getStart());
        } else if (e.isShiftDown()) {
          deleteBranch();
        } else {
          deleteMove();
        }
        break;

      case VK_Z:
        if (e.isControlDown()) {
          Lizzie.frame.toggleAlwaysOntop();
        } else if (e.isAltDown()) Lizzie.config.toggleShowSubBoard();
        // }
        else {
          Lizzie.frame.startTemporaryBoard();
        }
        break;
      case VK_Q:
        // Lizzie.frame.toggleAlwaysOntop();
        if (controlIsPressed(e)) {
          Lizzie.frame.toggleScoreMode();
        } else if (e.isAltDown()) {
          Lizzie.frame.toggleIndependentMainBoard();
        } else Lizzie.frame.openOnlineDialog();
        break;

      case VK_A:
        if (controlIsPressed(e)) {
          AutoPlay autoPlay = new AutoPlay();
          autoPlay.setVisible(true);
        } else if (e.isAltDown()) {
          if (!Lizzie.frame.syncBoard) Lizzie.frame.toggleIndependentSubBoard();
        } else if (e.isShiftDown()) {
          //  Lizzie.board.clearbestmovesafter(Lizzie.board.getHistory().getStart());
          Lizzie.board.clearBoardStat();
        } else {
          StartAnaDialog newgame = new StartAnaDialog(false, Lizzie.frame);
          newgame.setVisible(true);
          if (newgame.isCancelled()) {
            LizzieFrame.toolbar.resetAutoAna();
          }
        }
        break;
        // this is copyed from https://github.com/zsalch/lizzie/tree/n_avoiddialog
      case VK_DECIMAL:
      case VK_SLASH:
        if (Lizzie.frame.isCounting) {
          Lizzie.frame.clearKataEstimate();
          Lizzie.frame.isCounting = false;
          Lizzie.frame.estimateResults.setVisible(false);
        } else {
          Lizzie.frame.countstones(true);
        }
        break;
      case VK_PERIOD:
        if (Lizzie.config.useShortcutKataEstimate) Lizzie.frame.toggleShowKataEstimate();
        // if (!Lizzie.board.getHistory().getNext().isPresent()) {
        // Lizzie.board.setScoreMode(!Lizzie.board.inScoreMode());
        // }
        break;

      case VK_D:
        if (e.isAltDown()) {
          Lizzie.frame.setLzSaiEngine();
        } else if (e.isShiftDown()) {
          Lizzie.frame.setRules();
        }
        // toggleShowDynamicKomi();

        break;

      case VK_R:
        // if (isinsertmode) {
        // return;
        // }
        Lizzie.frame.replayBranch();
        break;

      case VK_OPEN_BRACKET:
        if (Lizzie.frame.BoardPositionProportion > 0) {
          Lizzie.frame.BoardPositionProportion--;
          Lizzie.frame.refreshContainer();
        }
        break;

      case VK_CLOSE_BRACKET:
        if (Lizzie.frame.BoardPositionProportion < 8) {
          Lizzie.frame.BoardPositionProportion++;
          Lizzie.frame.refreshContainer();
        }
        break;

      case VK_K:
        if (e.isAltDown()) {
          Lizzie.config.toggleEvaluationColoring();
        }
        break;
        // Use Ctrl+Num to switching multiple engine

      case VK_1:
        if (e.isAltDown()) {
          Lizzie.frame.defaultMode();
          break;
        }
      case VK_2:
        if (e.isAltDown()) {
          Lizzie.frame.classicMode();
          break;
        }
      case VK_3:
        if (e.isAltDown()) {
          Lizzie.frame.minMode();
          break;
        }
      case VK_4:
        if (e.isAltDown()) {
          Lizzie.config.toggleExtraMode(3);
          break;
        }
      case VK_5:
        if (e.isAltDown()) {
          Lizzie.config.toggleExtraMode(1);
          break;
        }
      case VK_6:
        if (e.isAltDown()) {
          Lizzie.config.toggleExtraMode(2);
          break;
        }
      case VK_7:
        if (e.isAltDown()) {
          Lizzie.frame.independentBoardMode(false);
          break;
        }
      case VK_8:
        if (e.isAltDown()) {
          Lizzie.frame.switchToCustomMode(1);
          break;
        }
      case VK_9:
        if (controlIsPressed(e)) {
          Lizzie.engineManager.switchEngine(e.getKeyCode() - VK_1, true);
        } else if (e.isAltDown()) {
          Lizzie.frame.switchToCustomMode(2);
        } else Lizzie.frame.setMouseOverCoords(e.getKeyCode() - VK_1);
        break;
      case VK_0:
        if (controlIsPressed(e)) {
          Lizzie.engineManager.switchEngine(9, true);
        }
        break;
    }

    Lizzie.frame.refresh();
  }

  // private boolean wasPonderingWhenControlsShown = false;

  @Override
  public void keyReleased(KeyEvent e) {
    switch (e.getKeyCode()) {
      case VK_X:
        // if (wasPonderingWhenControlsShown) Lizzie.leelaz.togglePonder();
        Lizzie.frame.stopShowingControl();
        break;

      case VK_Z:
        Lizzie.frame.stopTemporaryBoard();
        Lizzie.frame.refresh();
        break;

      default:
    }
  }

  private long wheelWhen;

  @Override
  public void mouseWheelMoved(MouseWheelEvent e) {
    // if (isinsertmode) {
    // return;
    // }
    //    if (Lizzie.frame.processCommentMouseWheelMoved(e)) {
    //      return;
    //    }
    if (Lizzie.frame.processSubboardMouseWheelMoved(e)) {
      return;
    }
    if (e.getWhen() - wheelWhen > 0) {
      wheelWhen = e.getWhen();
      if (e.getWheelRotation() > 0) {
        if (LizzieFrame.boardRenderer.isShowingBranch()
            || (Lizzie.config.isDoubleEngineMode()
                && LizzieFrame.boardRenderer2.isShowingBranch())) {
          Lizzie.frame.doBranch(1);
          Lizzie.frame.refresh();
        } else {
          redo();
        }
      } else if (e.getWheelRotation() < 0) {
        if (LizzieFrame.boardRenderer.isShowingBranch()
            || (Lizzie.config.isDoubleEngineMode()
                && LizzieFrame.boardRenderer2.isShowingBranch())) {
          Lizzie.frame.doBranch(-1);
          Lizzie.frame.refresh();
        } else {
          undo();
        }
      }
    }
  }
}
