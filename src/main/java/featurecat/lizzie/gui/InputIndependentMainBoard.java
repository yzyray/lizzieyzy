package featurecat.lizzie.gui;

import static java.awt.event.KeyEvent.*;

import featurecat.lizzie.Lizzie;
import java.awt.event.*;

public class InputIndependentMainBoard implements KeyListener {

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

  //  private void startTemporaryBoard() {
  //    if (Lizzie.config.showBestMoves) {
  //      startRawBoard();
  //    } else {
  //      Lizzie.config.showBestMovesTemporarily = true;
  //    }
  //  }
  //
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
  //
  //  private void stopTemporaryBoard() {
  //    stopRawBoard();
  //    Lizzie.config.showBestMovesTemporarily = false;
  //  }

  //  private void toggleHints() {
  //    Lizzie.config.toggleShowBranch();
  //    Lizzie.config.showSubBoard =
  //        Lizzie.config.showNextMoves = Lizzie.config.showBestMoves = Lizzie.config.showBranch;
  //  }

  public static void nextBranch() {
    if (Lizzie.frame.isPlayingAgainstLeelaz || Lizzie.frame.isAnaPlayingAgainstLeelaz) return;
    Lizzie.board.nextBranch();
  }

  public static void previousBranch() {
    if (Lizzie.frame.isPlayingAgainstLeelaz || Lizzie.frame.isAnaPlayingAgainstLeelaz) return;
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
          if (Lizzie.frame.independentMainBoard.boardRenderer.isShowingBranch()) {
            Lizzie.frame.independentMainBoard.doBranch(-1);
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
          if (Lizzie.frame.independentMainBoard.boardRenderer.isShowingBranch()) {
            Lizzie.frame.independentMainBoard.doBranch(1);
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
              Lizzie.frame.saveSubBoardPicture();
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
            Lizzie.frame.flashAnalyzeGame(true);
          } else {
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
          Lizzie.board.clearbestmovesafter(Lizzie.board.getHistory().getStart());
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
          LizzieFrame.boardRenderer.removeKataEstimateImage();
          // Lizzie.frame.repaint();
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
        } else {
          Lizzie.frame.setRules();
        }
        // toggleShowDynamicKomi();

        break;

      case VK_R:
        // if (isinsertmode) {
        // return;
        // }
        Lizzie.frame.replayBranchIndependentMainBoard();
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
}
