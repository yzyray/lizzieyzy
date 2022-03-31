package featurecat.lizzie.rules;

import featurecat.lizzie.Lizzie;
import java.util.ArrayList;
import java.util.List;

public class Tsumego {
  private int wallGap = 2;
  private int leftIndex;
  private int rightIndex;
  private int topIndex;
  private int bottomIndex;
  private Stone side;
  private Stone otherSide;
  private static final int NO_NEED = -1;

  public Stone getCoverSideAndIndex() {
    Stone[] stones = Lizzie.board.getStones();
    int whiteCount = 0,
        blackCount = 0,
        whiteWidthGap = 0,
        whiteHeightGap = 0,
        blackWidthGap = 0,
        blackHeightGap = 0;
    int leftIndex = Board.boardWidth - 1,
        rightIndex = 0,
        topIndex = Board.boardHeight - 1,
        bottomtIndex = 0;
    for (int i = 0; i < Board.boardWidth; i++) {
      for (int j = 0; j < Board.boardHeight; j++) {
        Stone stone = stones[Board.getIndex(i, j)];
        if (stone == Stone.BLACK) {
          blackCount++;
          blackWidthGap += Math.max(i, Board.boardWidth - i - 1);
          blackHeightGap += Math.max(j, Board.boardHeight - j - 1);
          if (i < leftIndex) leftIndex = i;
          if (i > rightIndex) rightIndex = i;
          if (j < topIndex) topIndex = j;
          if (j > bottomtIndex) bottomtIndex = j;
        } else if (stone == Stone.WHITE) {
          whiteCount++;
          whiteWidthGap += Math.max(i, Board.boardWidth - i - 1);
          whiteHeightGap += Math.max(j, Board.boardHeight - j - 1);
          if (i < leftIndex) leftIndex = i;
          if (i > rightIndex) rightIndex = i;
          if (j < topIndex) topIndex = j;
          if (j > bottomtIndex) bottomtIndex = j;
        }
      }
    }
    double blackAvgWidthGap = blackWidthGap / (double) blackCount;
    double blackAvgHeightGap = blackHeightGap / (double) blackCount;
    double whiteAvgWidthGap = whiteWidthGap / (double) whiteCount;
    double whiteAvgHeightGap = whiteHeightGap / (double) whiteCount;
    if (blackAvgWidthGap < whiteAvgWidthGap && blackAvgHeightGap < whiteAvgHeightGap) {
      side = Stone.BLACK;
    }
    if (blackAvgWidthGap <= whiteAvgWidthGap && blackAvgHeightGap < whiteAvgHeightGap) {
      side = Stone.BLACK;
    }
    if (blackAvgWidthGap < whiteAvgWidthGap && blackAvgHeightGap <= whiteAvgHeightGap) {
      side = Stone.BLACK;
    }
    if (blackAvgWidthGap > whiteAvgWidthGap && blackAvgHeightGap > whiteAvgHeightGap) {
      side = Stone.WHITE;
    }
    if (blackAvgWidthGap >= whiteAvgWidthGap && blackAvgHeightGap > whiteAvgHeightGap) {
      side = Stone.WHITE;
    }
    if (blackAvgWidthGap > whiteAvgWidthGap && blackAvgHeightGap >= whiteAvgHeightGap) {
      side = Stone.WHITE;
    }
    if (blackCount >= whiteCount) side = Stone.BLACK;
    else side = Stone.WHITE;
    if (leftIndex - wallGap > 0) this.leftIndex = leftIndex - wallGap;
    else this.leftIndex = NO_NEED;
    if (rightIndex + wallGap < Board.boardWidth - 1) this.rightIndex = rightIndex + wallGap;
    else this.topIndex = NO_NEED;
    if (topIndex - wallGap > 0) this.topIndex = topIndex - wallGap;
    else this.topIndex = NO_NEED;
    if (bottomtIndex + wallGap < Board.boardHeight - 1) this.bottomIndex = bottomtIndex + wallGap;
    else this.bottomIndex = NO_NEED;
    if (side == Stone.BLACK) otherSide = Stone.WHITE;
    else otherSide = Stone.BLACK;
    return side;
  }

  private void addStone(
      Stone[] stones,
      Zobrist zobrist,
      int x,
      int y,
      Stone color,
      List<extraMoveForTsumego> extraStones) {
    stones[Board.getIndex(x, y)] = color;
    zobrist.toggleStone(x, y, color);
    extraMoveForTsumego stone = new extraMoveForTsumego();
    stone.x = x;
    stone.y = y;
    stone.color = color;
    extraStones.add(stone);
  }

  public void buildCoverWall() {
    List<extraMoveForTsumego> extraStones = new ArrayList<extraMoveForTsumego>();
    Zobrist zobrist = Lizzie.board.getHistory().getZobrist();
    Stone[] curStones = Lizzie.board.getStones();
    Stone[] stones = new Stone[curStones.length];
    for (int i = 0; i < curStones.length; i++) {
      stones[i] = curStones[i];
    }

    for (int i = 0; i < Board.boardWidth; i++) {
      for (int j = 0; j < Board.boardHeight; j++) {
        boolean placed = false;
        if (i == leftIndex || i == rightIndex) {
          if (j >= topIndex && j <= bottomIndex) {
            addStone(stones, zobrist, i, j, side, extraStones);
            placed = true;
          }
        }
        if (!placed) {
          if (j == topIndex || j == bottomIndex) {
            if (i >= leftIndex && i <= rightIndex) {
              addStone(stones, zobrist, i, j, side, extraStones);
            }
          }
        }
      }
    }

    //    int leftGap=leftIndex+1;
    //    int rightGap=Board.boardWidth-rightIndex-1;
    //    int topGap=topIndex+1;
    //    int bottomGap=Board.boardHeight-bottomtIndex-1;
    //    int minGap=999;
    //    if(leftGap<minGap)
    //    	minGap=leftGap;
    //    if(rightGap<minGap)
    //    	minGap=rightGap;
    //    if(topGap<minGap)
    //    	minGap=topGap;
    //    if(bottomGap<minGap)
    //    	minGap=bottomGap;
    // fill board
    int minIndex = -1;
    int leftArea = (rightIndex + 1) * Board.boardHeight;
    int rightArea = (Board.boardWidth - leftIndex - 1) * Board.boardHeight;
    int topArea = (bottomIndex + 1) * Board.boardWidth;
    int bottomArea = (Board.boardHeight - topIndex + 1) * Board.boardWidth;
    int minArea = 99999;
    if (leftArea < minArea) {
      minArea = leftArea;
      minIndex = 1;
    }
    if (rightArea < minArea) {
      minArea = rightArea;
      minIndex = 2;
    }
    if (topArea < minArea) {
      minArea = topArea;
      minIndex = 3;
    }
    if (bottomArea < minArea) {
      minArea = bottomArea;
      minIndex = 4;
    }
    int halfAreaWithKomi = (int) Math.ceil((Board.boardHeight * Board.boardWidth) / 2.0 + 4.0);
    switch (minIndex) {
      case 1:
        if (minArea <= halfAreaWithKomi) {
          int leftSideIndex = halfAreaWithKomi / Board.boardHeight - 1;
          int remainStones = halfAreaWithKomi - (leftSideIndex + 1) * Board.boardHeight;
          for (int x = 0; x < Board.boardWidth; x++) {
            for (int y = 0; y < Board.boardHeight; y++) {
              if (x < leftSideIndex) {
                if (x <= rightIndex) {
                  if (y < topIndex || y > bottomIndex) {
                    if ((y + x) % 2 == 0) addStone(stones, zobrist, x, y, side, extraStones);
                  }
                } else {
                  if ((y + x) % 2 == 0) addStone(stones, zobrist, x, y, side, extraStones);
                }
              }
              if (x == leftSideIndex) {
                addStone(stones, zobrist, x, y, side, extraStones);
              }
              if (remainStones > 0) {
                if (x == leftSideIndex + 1) {
                  if (y < remainStones) addStone(stones, zobrist, x, y, side, extraStones);
                  else addStone(stones, zobrist, x, y, otherSide, extraStones);
                }
                if (x == leftSideIndex + 2) {
                  addStone(stones, zobrist, x, y, otherSide, extraStones);
                }
                if (x > leftSideIndex + 2) {
                  if ((y + x) % 2 == 0) addStone(stones, zobrist, x, y, otherSide, extraStones);
                }
              } else {
                if (x == leftSideIndex + 1) {
                  addStone(stones, zobrist, x, y, otherSide, extraStones);
                }
                if (x > leftSideIndex + 1) {
                  if ((y + x) % 2 == 0) addStone(stones, zobrist, x, y, otherSide, extraStones);
                }
              }
            }
          }
        } else {

        }
        break;
      case 2:
        break;
      case 3:
        break;
      case 4:
        break;
    }

    // add ko threat

    Lizzie.board.flattenWithCondition(stones, zobrist, side == Stone.BLACK, extraStones);
  }
}
