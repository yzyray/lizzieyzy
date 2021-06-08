package featurecat.lizzie.analysis;

import featurecat.lizzie.Lizzie;
import featurecat.lizzie.rules.Board;
import featurecat.lizzie.rules.BoardData;
import featurecat.lizzie.rules.Stone;
import java.util.List;
import java.util.Optional;

public class Branch {
  public BoardData data;
  //  public int branchLength;
  // 待完成
  //  public int pvVisits = -1;
  public boolean[] isNewStone;
  public int[] pvVisitsList;
  public int length;

  public Branch(
      Board board,
      List<String> variation,
      // 待完成
      List<String> pvVisits,
      int length,
      boolean fromSubboard,
      boolean blackToPlay,
      Stone[] stonesTemp) {
    int[] moveNumberList = new int[Board.boardWidth * Board.boardHeight];
    isNewStone = new boolean[Board.boardWidth * Board.boardHeight];
    pvVisitsList = new int[Board.boardWidth * Board.boardHeight];
    this.length = Math.min(variation.size(), length);
    int moveNumber = 0;
    double winrate = 0.0;
    int playouts = 0;
    //  branchLength = variation.size();
    if (fromSubboard) {
      this.data =
          new BoardData(
              stonesTemp.clone(), // stonesTemp
              board.getLastMove(),
              board.getData().lastMoveColor,
              blackToPlay,
              board.getData().zobrist.clone(),
              moveNumber,
              moveNumberList,
              board.getData().blackCaptures,
              board.getData().whiteCaptures,
              winrate,
              playouts);
    } else {
      if (stonesTemp != null)
        this.data =
            new BoardData(
                stonesTemp.clone(),
                board.getHistory().getCurrentHistoryNode().previous().get().getData().lastMove,
                board.getHistory().getCurrentHistoryNode().previous().get().getData().lastMoveColor,
                board.getHistory().getCurrentHistoryNode().previous().get().getData().blackToPlay,
                board
                    .getHistory()
                    .getCurrentHistoryNode()
                    .previous()
                    .get()
                    .getData()
                    .zobrist
                    .clone(),
                moveNumber,
                moveNumberList,
                board.getHistory().getCurrentHistoryNode().previous().get().getData().blackCaptures,
                board.getHistory().getCurrentHistoryNode().previous().get().getData().whiteCaptures,
                winrate,
                playouts);
      else
        this.data =
            new BoardData(
                board.getStones().clone(),
                board.getLastMove(),
                board.getData().lastMoveColor,
                board.getData().blackToPlay,
                board.getData().zobrist.clone(),
                moveNumber,
                moveNumberList,
                board.getData().blackCaptures,
                board.getData().whiteCaptures,
                winrate,
                playouts);
    }

    for (int i = 0; i < variation.size() && i < length; i++) {
      Optional<int[]> coordOpt = Board.asCoordinates(variation.get(i));
      if (!coordOpt.isPresent() || !Board.isValid(coordOpt.get()[0], coordOpt.get()[1])) {
        break;
      }
      int[] coord = coordOpt.get();

      int x = coord[0];
      int y = coord[1];
      data.lastMove = coordOpt;
      data.stones[Board.getIndex(coord[0], coord[1])] =
          data.blackToPlay ? Stone.BLACK : Stone.WHITE;
      isNewStone[Board.getIndex(coord[0], coord[1])] = true;
      if (Lizzie.frame.floatBoard == null || !Lizzie.frame.floatBoard.isVisible()) {
        if (Lizzie.config.removeDeadChainInVariation
            && !Lizzie.config.noCapture) { // 待完成增加选项 变化图中是否提子?
          Board.removeDeadChainForBranch(
              x + 1, y, data.blackToPlay ? Stone.WHITE : Stone.BLACK, data.stones);
          Board.removeDeadChainForBranch(
              x, y + 1, data.blackToPlay ? Stone.WHITE : Stone.BLACK, data.stones);
          Board.removeDeadChainForBranch(
              x - 1, y, data.blackToPlay ? Stone.WHITE : Stone.BLACK, data.stones);
          Board.removeDeadChainForBranch(
              x, y - 1, data.blackToPlay ? Stone.WHITE : Stone.BLACK, data.stones);
        }
      }
      data.moveNumberList[Board.getIndex(coord[0], coord[1])] =
          i + 1; // 待完成,pvVisits也类似保存,选项可选 pvVisits显示全部/最后一手/不显示

      data.lastMoveColor = data.blackToPlay ? Stone.WHITE : Stone.BLACK;
      data.blackToPlay = !data.blackToPlay;
      // 待完成,增加是否显示pvvisits的判断
      //  if (i == variation.size() - 1 || i == length - 1) {
      if (Lizzie.config.showPvVisitsAllMove || Lizzie.config.showPvVisitsLastMove) {
        if (pvVisits != null && pvVisits.size() == variation.size())
          try {
            //  this.pvVisits = Integer.parseInt(pvVisits.get(i));
            pvVisitsList[Board.getIndex(coord[0], coord[1])] = Integer.parseInt(pvVisits.get(i));
          } catch (NumberFormatException e) {
            e.printStackTrace();
          }
      }
    }
    //  }
  }
}
