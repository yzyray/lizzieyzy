package featurecat.lizzie.rules;

import featurecat.lizzie.Lizzie;
import featurecat.lizzie.util.Utils;
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

  public Stone getCoverSideAndIndex(boolean forceSide, boolean forceBlack) {
    Stone[] stones = Lizzie.board.getStones();
    int whiteCount = 0, blackCount = 0;
    int leftIndex = Board.boardWidth - 1,
        rightIndex = 0,
        topIndex = Board.boardHeight - 1,
        bottomtIndex = 0;
    int blackLeft = 0,
        blackRight = Board.boardWidth - 1,
        blackTop = 0,
        blackBottom = Board.boardHeight - 1;
    int whiteLeft = 0,
        whiteRight = Board.boardWidth - 1,
        whiteTop = 0,
        whiteBottom = Board.boardHeight - 1;
    for (int i = 0; i < Board.boardWidth; i++) {
      for (int j = 0; j < Board.boardHeight; j++) {
        Stone stone = stones[Board.getIndex(i, j)];
        if (stone == Stone.BLACK) {
          blackCount++;
          if (i < blackLeft) blackLeft = i;
          if (i > blackRight) blackRight = i;
          if (j < blackTop) blackTop = j;
          if (j > blackBottom) blackBottom = j;

          if (i < leftIndex) leftIndex = i;
          if (i > rightIndex) rightIndex = i;
          if (j < topIndex) topIndex = j;
          if (j > bottomtIndex) bottomtIndex = j;
        } else if (stone == Stone.WHITE) {
          whiteCount++;

          if (i < whiteLeft) whiteLeft = i;
          if (i > whiteRight) whiteRight = i;
          if (j < whiteTop) whiteTop = j;
          if (j > whiteBottom) whiteBottom = j;

          if (i < leftIndex) leftIndex = i;
          if (i > rightIndex) rightIndex = i;
          if (j < topIndex) topIndex = j;
          if (j > bottomtIndex) bottomtIndex = j;
        }
      }
    }
    int blackCoverCount = 0;
    int whiteCoverCount = 0;
    if (blackLeft < whiteLeft) blackCoverCount++;
    if (blackRight > whiteRight) blackCoverCount++;
    if (blackTop < whiteTop) blackCoverCount++;
    if (blackBottom > whiteBottom) blackCoverCount++;

    if (blackLeft > whiteLeft) whiteCoverCount++;
    if (blackRight < whiteRight) whiteCoverCount++;
    if (blackTop > whiteTop) whiteCoverCount++;
    if (blackBottom < whiteBottom) whiteCoverCount++;
    // 看远的2-3边
    int minGap = 999;
    int minGapIndex = -1;
    if (leftIndex < minGap) {
      minGap = leftIndex;
      minGapIndex = 1;
    }
    if (Board.boardWidth - 1 - rightIndex < minGap) {
      minGap = Board.boardWidth - 1 - rightIndex;
      minGapIndex = 2;
    }
    if (topIndex < minGap) {
      minGap = rightIndex;
      minGapIndex = 3;
    }
    if (Board.boardHeight - 1 - bottomtIndex < minGap) {
      minGap = Board.boardHeight - 1 - bottomtIndex;
      minGapIndex = 4;
    }
    switch (minGapIndex) {
      case 1:
        if (blackLeft > whiteLeft) whiteCoverCount--;
        if (blackLeft < whiteLeft) blackCoverCount--;
        break;
      case 2:
        if (blackRight < whiteRight) whiteCoverCount--;
        if (blackRight > whiteRight) blackCoverCount--;
        break;
      case 3:
        if (blackTop > whiteTop) whiteCoverCount--;
        if (blackTop < whiteTop) blackCoverCount--;
        break;
      case 4:
        if (blackBottom < whiteBottom) whiteCoverCount--;
        if (blackBottom > whiteBottom) blackCoverCount--;
        break;
    }

    if (forceSide) {
      if (forceBlack) side = Stone.BLACK;
      else side = Stone.WHITE;
    } else {
      if (blackCoverCount < whiteCoverCount) {
        side = Stone.WHITE;
      } else if (blackCoverCount > whiteCoverCount) {
        side = Stone.BLACK;
      } else if (blackCount >= whiteCount) side = Stone.BLACK;
      else side = Stone.WHITE;
    }

    if (leftIndex - wallGap > 0) this.leftIndex = leftIndex - wallGap;
    else this.leftIndex = -1;
    if (rightIndex + wallGap < Board.boardWidth - 1) this.rightIndex = rightIndex + wallGap;
    else this.rightIndex = Board.boardWidth;
    if (topIndex - wallGap > 0) this.topIndex = topIndex - wallGap;
    else this.topIndex = -1;
    if (bottomtIndex + wallGap < Board.boardHeight - 1) this.bottomIndex = bottomtIndex + wallGap;
    else this.bottomIndex = Board.boardHeight;
    if (side == Stone.BLACK) otherSide = Stone.WHITE;
    else otherSide = Stone.BLACK;
    return side;
  }

  public void buildCoverWall(
      boolean addKoThreatSide,
      boolean addKoThreatOtherSide,
      boolean forceToPlay,
      boolean blackToPlay) {
    double komi = 7.5;
    if (rightIndex == Board.boardWidth
        && leftIndex == -1
        && topIndex == -1
        && bottomIndex == Board.boardHeight) return;
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
            Utils.addStone(stones, zobrist, i, j, side, extraStones);
            placed = true;
          }
        }
        if (!placed) {
          if (j == topIndex || j == bottomIndex) {
            if (i >= leftIndex && i <= rightIndex) {
              Utils.addStone(stones, zobrist, i, j, side, extraStones);
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
    int rightArea = (Board.boardWidth - leftIndex + 1) * Board.boardHeight;
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
    boolean noRoomForSideKo = false;
    boolean noRoomForOtherSideKo = false;
    boolean outOfHalf = false;
    int totalArea = 0;
    switch (minIndex) {
      case 1:
        if (minArea <= halfAreaWithKomi) {
          int leftSideIndex = halfAreaWithKomi / Board.boardHeight - 1;
          int remainStones = halfAreaWithKomi - (leftSideIndex + 1) * Board.boardHeight;
          for (int x = 0; x < Board.boardWidth; x++) {
            for (int y = 0; y < Board.boardHeight; y++) {
              if (x < leftSideIndex) {
                if (x <= rightIndex && x >= leftIndex) {
                  if (y < topIndex || y > bottomIndex) {
                    if ((y + x) % 2 == 0) Utils.addStone(stones, zobrist, x, y, side, extraStones);
                  }
                } else {
                  if ((y + x) % 2 == 0) Utils.addStone(stones, zobrist, x, y, side, extraStones);
                }
              }
              if (x == leftSideIndex) {
                Utils.addStone(stones, zobrist, x, y, side, extraStones);
              }
              if (remainStones > 0) {
                if (x == leftSideIndex + 1) {
                  if (y < remainStones) Utils.addStone(stones, zobrist, x, y, side, extraStones);
                  else Utils.addStone(stones, zobrist, x, y, otherSide, extraStones);
                }
                if (x == leftSideIndex + 2) {
                  Utils.addStone(stones, zobrist, x, y, otherSide, extraStones);
                }
                if (x > leftSideIndex + 2) {
                  if ((y + x) % 2 == 0)
                    Utils.addStone(stones, zobrist, x, y, otherSide, extraStones);
                }
              } else {
                if (x == leftSideIndex + 1) {
                  Utils.addStone(stones, zobrist, x, y, otherSide, extraStones);
                }
                if (x > leftSideIndex + 1) {
                  if ((y + x) % 2 == 0)
                    Utils.addStone(stones, zobrist, x, y, otherSide, extraStones);
                }
              }
            }
          }
          if (addKoThreatSide) {
            if (leftSideIndex < 4) {
              noRoomForSideKo = true;
            } else {
              int topRoom = topIndex;
              int bottomRoom = Board.boardHeight - bottomIndex - 1;
              if (topRoom >= 2) {
                for (int x = 0; x < Board.boardWidth; x++) {
                  for (int y = 0; y < Board.boardHeight; y++) {
                    if ((y == 0 && x == 0) || (y == 0 && x == 3) || (y == 1 && x == 3))
                      removeAndAddStone(stones, zobrist, x, y, Stone.EMPTY, extraStones);
                    if (y == 0 && x == 1)
                      removeAndAddStone(stones, zobrist, x, y, otherSide, extraStones);
                    if (y == 0 && x == 2)
                      removeAndAddStone(stones, zobrist, x, y, side, extraStones);
                    if (y == 1 && x >= 0 && x <= 2)
                      removeAndAddStone(stones, zobrist, x, y, otherSide, extraStones);
                  }
                }
              } else if (bottomRoom >= 2) {
                for (int x = 0; x < Board.boardWidth; x++) {
                  for (int y = 0; y < Board.boardHeight; y++) {
                    if ((y == Board.boardHeight - 1 && x == 0)
                        || (y == Board.boardHeight - 1 && x == 3)
                        || (y == Board.boardHeight - 2 && x == 3))
                      removeAndAddStone(stones, zobrist, x, y, Stone.EMPTY, extraStones);
                    if (y == Board.boardHeight - 1 && x == 1)
                      removeAndAddStone(stones, zobrist, x, y, otherSide, extraStones);
                    if (y == Board.boardHeight - 1 && x == 2)
                      removeAndAddStone(stones, zobrist, x, y, side, extraStones);
                    if (y == Board.boardHeight - 2 && x >= 0 && x <= 2)
                      removeAndAddStone(stones, zobrist, x, y, otherSide, extraStones);
                  }
                }
              } else noRoomForSideKo = true;
            }
          }
          if (addKoThreatOtherSide) {
            if (Board.boardWidth - leftSideIndex - 2 < 5) {
              noRoomForOtherSideKo = true;
            } else {
              for (int x = 0; x < Board.boardWidth; x++) {
                for (int y = 0; y < Board.boardHeight; y++) {
                  if ((y == 0 && x == Board.boardWidth - 1)
                      || (y == 0 && x == Board.boardWidth - 4)
                      || (y == 1 && x == Board.boardWidth - 4))
                    removeAndAddStone(stones, zobrist, x, y, Stone.EMPTY, extraStones);
                  if (y == 0 && x == Board.boardWidth - 2)
                    removeAndAddStone(stones, zobrist, x, y, side, extraStones);
                  if (y == 0 && x == Board.boardWidth - 3)
                    removeAndAddStone(stones, zobrist, x, y, otherSide, extraStones);
                  if (y == 1 && x >= Board.boardWidth - 3 && x <= Board.boardWidth - 1)
                    removeAndAddStone(stones, zobrist, x, y, side, extraStones);
                }
              }
            }
          }
        } else {
          outOfHalf = true;
          boolean topMin = false;
          int topRoom = topIndex;
          int bottomRoom = Board.boardHeight - bottomIndex - 1;
          if (topRoom <= bottomRoom) topMin = true;
          if (addKoThreatSide) {
            if (rightIndex < 4) {
              noRoomForSideKo = true;
            } else if (topRoom >= 2 && bottomRoom < 2) {
              topMin = true;
            } else if (topRoom < 2 && bottomRoom >= 2) topMin = false;
          }
          // 确定好用哪一边填充,topMin
          if (topMin) {
            totalArea =
                Math.min(Board.boardWidth, rightIndex + 1)
                    * Math.min(Board.boardHeight, (bottomIndex + 1));
            for (int x = 0; x < Board.boardWidth; x++) {
              for (int y = 0; y < Board.boardHeight; y++) {
                if (x < rightIndex) {
                  if (y < topIndex || (x < leftIndex && y <= bottomIndex)) {
                    if (y == bottomIndex) Utils.addStone(stones, zobrist, x, y, side, extraStones);
                    else if ((y + x) % 2 == 0)
                      Utils.addStone(stones, zobrist, x, y, side, extraStones);
                  }
                }
                if (x == rightIndex) {
                  if (y < topIndex) Utils.addStone(stones, zobrist, x, y, side, extraStones);
                }
                if (x <= rightIndex) {
                  if (y == bottomIndex + 1)
                    Utils.addStone(stones, zobrist, x, y, otherSide, extraStones);
                  if (y > bottomIndex + 1)
                    if ((y + x) % 2 == 0)
                      Utils.addStone(stones, zobrist, x, y, otherSide, extraStones);
                }
                if (x == rightIndex + 1) {
                  Utils.addStone(stones, zobrist, x, y, otherSide, extraStones);
                }
                if (x > rightIndex + 1) {
                  if ((y + x) % 2 == 0)
                    Utils.addStone(stones, zobrist, x, y, otherSide, extraStones);
                }
              }
            }
            if (addKoThreatSide) {
              if (rightIndex < 4) {
                noRoomForSideKo = true;
              } else {
                if (topIndex >= 2) {
                  for (int x = 0; x < Board.boardWidth; x++) {
                    for (int y = 0; y < Board.boardHeight; y++) {
                      if ((y == 0 && x == 0) || (y == 0 && x == 3) || (y == 1 && x == 3))
                        removeAndAddStone(stones, zobrist, x, y, Stone.EMPTY, extraStones);
                      if (y == 0 && x == 1)
                        removeAndAddStone(stones, zobrist, x, y, otherSide, extraStones);
                      if (y == 0 && x == 2)
                        removeAndAddStone(stones, zobrist, x, y, side, extraStones);
                      if (y == 1 && x >= 0 && x <= 2)
                        removeAndAddStone(stones, zobrist, x, y, otherSide, extraStones);
                    }
                  }
                } else noRoomForSideKo = true;
              }
            }
          } else {
            totalArea =
                Math.min(Board.boardWidth, rightIndex + 1)
                    * (Board.boardHeight - Math.max(0, topIndex));
            for (int x = 0; x < Board.boardWidth; x++) {
              for (int y = 0; y < Board.boardHeight; y++) {
                if (x < rightIndex) {
                  if (y > bottomIndex || (x < leftIndex && y >= topIndex)) {
                    if (y == topIndex) Utils.addStone(stones, zobrist, x, y, side, extraStones);
                    else if ((y + x) % 2 == 0)
                      Utils.addStone(stones, zobrist, x, y, side, extraStones);
                  }
                }
                if (x == rightIndex) {
                  if (y > bottomIndex) Utils.addStone(stones, zobrist, x, y, side, extraStones);
                }
                if (x <= rightIndex) {
                  if (y == topIndex - 1)
                    Utils.addStone(stones, zobrist, x, y, otherSide, extraStones);
                  if (y < topIndex - 1)
                    if ((y + x) % 2 == 0)
                      Utils.addStone(stones, zobrist, x, y, otherSide, extraStones);
                }
                if (x == rightIndex + 1) {
                  Utils.addStone(stones, zobrist, x, y, otherSide, extraStones);
                }
                if (x > rightIndex + 1) {
                  if ((y + x) % 2 == 0)
                    Utils.addStone(stones, zobrist, x, y, otherSide, extraStones);
                }
              }
            }
            if (addKoThreatSide) {
              if (rightIndex < 4) {
                noRoomForSideKo = true;
              } else if (Board.boardHeight - bottomIndex - 1 >= 2) {
                for (int x = 0; x < Board.boardWidth; x++) {
                  for (int y = 0; y < Board.boardHeight; y++) {
                    if ((y == Board.boardHeight - 1 && x == 0)
                        || (y == Board.boardHeight - 1 && x == 3)
                        || (y == Board.boardHeight - 2 && x == 3))
                      removeAndAddStone(stones, zobrist, x, y, Stone.EMPTY, extraStones);
                    if (y == Board.boardHeight - 1 && x == 1)
                      removeAndAddStone(stones, zobrist, x, y, otherSide, extraStones);
                    if (y == Board.boardHeight - 1 && x == 2)
                      removeAndAddStone(stones, zobrist, x, y, side, extraStones);
                    if (y == Board.boardHeight - 2 && x >= 0 && x <= 2)
                      removeAndAddStone(stones, zobrist, x, y, otherSide, extraStones);
                  }
                }
              } else noRoomForSideKo = true;
            }
          }
          if (addKoThreatOtherSide) {
            if (Board.boardWidth - rightIndex - 1 < 5) {
              noRoomForOtherSideKo = true;
            } else {
              for (int x = 0; x < Board.boardWidth; x++) {
                for (int y = 0; y < Board.boardHeight; y++) {
                  if ((y == 0 && x == Board.boardWidth - 1)
                      || (y == 0 && x == Board.boardWidth - 4)
                      || (y == 1 && x == Board.boardWidth - 4))
                    removeAndAddStone(stones, zobrist, x, y, Stone.EMPTY, extraStones);
                  if (y == 0 && x == Board.boardWidth - 2)
                    removeAndAddStone(stones, zobrist, x, y, side, extraStones);
                  if (y == 0 && x == Board.boardWidth - 3)
                    removeAndAddStone(stones, zobrist, x, y, otherSide, extraStones);
                  if (y == 1 && x >= Board.boardWidth - 3 && x <= Board.boardWidth - 1)
                    removeAndAddStone(stones, zobrist, x, y, side, extraStones);
                }
              }
            }
          }
        }
        break;
      case 2:
        if (minArea <= halfAreaWithKomi) {
          int rightSideIndex = halfAreaWithKomi / Board.boardHeight + 1;
          int remainStones =
              halfAreaWithKomi - (Board.boardWidth - rightSideIndex) * Board.boardHeight;
          for (int x = 0; x < Board.boardWidth; x++) {
            for (int y = 0; y < Board.boardHeight; y++) {
              if (x > rightSideIndex) {
                if (x >= leftIndex && x <= rightIndex) {
                  if (y < topIndex || y > bottomIndex) {
                    if ((y + x) % 2 == 0) Utils.addStone(stones, zobrist, x, y, side, extraStones);
                  }
                } else {
                  if ((y + x) % 2 == 0) Utils.addStone(stones, zobrist, x, y, side, extraStones);
                }
              }
              if (x == rightSideIndex) {
                Utils.addStone(stones, zobrist, x, y, side, extraStones);
              }
              if (remainStones > 0) {
                if (x == rightSideIndex - 1) {
                  if (y < remainStones) Utils.addStone(stones, zobrist, x, y, side, extraStones);
                  else Utils.addStone(stones, zobrist, x, y, otherSide, extraStones);
                }
                if (x == rightSideIndex - 2) {
                  Utils.addStone(stones, zobrist, x, y, otherSide, extraStones);
                }
                if (x < rightSideIndex) {
                  if ((y + x) % 2 == 0)
                    Utils.addStone(stones, zobrist, x, y, otherSide, extraStones);
                }
              } else {
                if (x == rightSideIndex - 1) {
                  Utils.addStone(stones, zobrist, x, y, otherSide, extraStones);
                }
                if (x > rightSideIndex - 1) {
                  if ((y + x) % 2 == 0)
                    Utils.addStone(stones, zobrist, x, y, otherSide, extraStones);
                }
              }
            }
          }
          if (addKoThreatSide) {
            if (Board.boardWidth - rightSideIndex - 1 < 4) {
              noRoomForSideKo = true;
            } else {
              int topRoom = topIndex;
              int bottomRoom = Board.boardHeight - bottomIndex - 1;
              if (topRoom >= 2) {
                for (int x = 0; x < Board.boardWidth; x++) {
                  for (int y = 0; y < Board.boardHeight; y++) {
                    if ((y == 0 && x == Board.boardWidth - 1)
                        || (y == 0 && x == Board.boardWidth - 4)
                        || (y == 1 && x == Board.boardWidth - 4))
                      removeAndAddStone(stones, zobrist, x, y, Stone.EMPTY, extraStones);
                    if (y == 0 && x == Board.boardWidth - 2)
                      removeAndAddStone(stones, zobrist, x, y, otherSide, extraStones);
                    if (y == 0 && x == Board.boardWidth - 3)
                      removeAndAddStone(stones, zobrist, x, y, side, extraStones);
                    if (y == 1 && x >= Board.boardWidth - 3 && x <= Board.boardWidth - 1)
                      removeAndAddStone(stones, zobrist, x, y, otherSide, extraStones);
                  }
                }
              } else if (bottomRoom >= 2) {
                for (int x = 0; x < Board.boardWidth; x++) {
                  for (int y = 0; y < Board.boardHeight; y++) {
                    if ((y == Board.boardHeight - 1 && x == Board.boardWidth - 1)
                        || (y == Board.boardHeight - 1 && x == Board.boardWidth - 4)
                        || (y == Board.boardHeight - 2 && x == Board.boardWidth - 4))
                      removeAndAddStone(stones, zobrist, x, y, Stone.EMPTY, extraStones);
                    if (y == Board.boardHeight - 1 && x == Board.boardWidth - 2)
                      removeAndAddStone(stones, zobrist, x, y, otherSide, extraStones);
                    if (y == Board.boardHeight - 1 && x == Board.boardWidth - 3)
                      removeAndAddStone(stones, zobrist, x, y, side, extraStones);
                    if (y == Board.boardHeight - 2
                        && x >= Board.boardWidth - 3
                        && x <= Board.boardWidth - 1)
                      removeAndAddStone(stones, zobrist, x, y, otherSide, extraStones);
                  }
                }
              } else noRoomForSideKo = true;
            }
          }
          if (addKoThreatOtherSide) {
            if (rightSideIndex - 1 < 5) {
              noRoomForOtherSideKo = true;
            } else {
              for (int x = 0; x < Board.boardWidth; x++) {
                for (int y = 0; y < Board.boardHeight; y++) {
                  if ((y == 0 && x == 0) || (y == 0 && x == 3) || (y == 1 && x == 3))
                    removeAndAddStone(stones, zobrist, x, y, Stone.EMPTY, extraStones);
                  if (y == 0 && x == 1) removeAndAddStone(stones, zobrist, x, y, side, extraStones);
                  if (y == 0 && x == 2)
                    removeAndAddStone(stones, zobrist, x, y, otherSide, extraStones);
                  if (y == 1 && x >= 0 && x <= 2)
                    removeAndAddStone(stones, zobrist, x, y, side, extraStones);
                }
              }
            }
          }
        } else {
          outOfHalf = true;
          boolean topMin = false;
          int topRoom = topIndex;
          int bottomRoom = Board.boardHeight - bottomIndex - 1;
          if (topRoom <= bottomRoom) topMin = true;
          if (addKoThreatSide) {
            if (Board.boardWidth - leftIndex - 1 < 4) {
              noRoomForSideKo = true;
            } else if (topRoom >= 2 && bottomRoom < 2) {
              topMin = true;
            } else if (topRoom < 2 && bottomRoom >= 2) {
              topMin = false;
            }
          }
          if (topMin) {
            totalArea =
                (Board.boardWidth - Math.max(0, leftIndex))
                    * Math.min(Board.boardHeight, (bottomIndex + 1));
            for (int x = 0; x < Board.boardWidth; x++) {
              for (int y = 0; y < Board.boardHeight; y++) {
                if (x > leftIndex) {
                  if (y < topIndex || (x > rightIndex && y <= bottomIndex)) {
                    if (y == bottomIndex) Utils.addStone(stones, zobrist, x, y, side, extraStones);
                    else if ((y + x) % 2 == 0)
                      Utils.addStone(stones, zobrist, x, y, side, extraStones);
                  }
                }
                if (x == leftIndex) {
                  if (y < topIndex) Utils.addStone(stones, zobrist, x, y, side, extraStones);
                }
                if (x >= leftIndex) {
                  if (y == bottomIndex + 1)
                    Utils.addStone(stones, zobrist, x, y, otherSide, extraStones);
                  if (y > bottomIndex + 1)
                    if ((y + x) % 2 == 0)
                      Utils.addStone(stones, zobrist, x, y, otherSide, extraStones);
                }
                if (x == leftIndex - 1) {
                  Utils.addStone(stones, zobrist, x, y, otherSide, extraStones);
                }
                if (x < leftIndex - 1) {
                  if ((y + x) % 2 == 0)
                    Utils.addStone(stones, zobrist, x, y, otherSide, extraStones);
                }
              }
            }
            if (addKoThreatSide) {
              if (Board.boardWidth - leftIndex - 1 < 4) {
                noRoomForSideKo = true;
              } else {
                if (topIndex >= 2) {
                  for (int x = 0; x < Board.boardWidth; x++) {
                    for (int y = 0; y < Board.boardHeight; y++) {
                      if ((y == 0 && x == Board.boardWidth - 1)
                          || (y == 0 && x == Board.boardWidth - 4)
                          || (y == 1 && x == Board.boardWidth - 4))
                        removeAndAddStone(stones, zobrist, x, y, Stone.EMPTY, extraStones);
                      if (y == 0 && x == Board.boardWidth - 2)
                        removeAndAddStone(stones, zobrist, x, y, otherSide, extraStones);
                      if (y == 0 && x == Board.boardWidth - 3)
                        removeAndAddStone(stones, zobrist, x, y, side, extraStones);
                      if (y == 1 && x >= Board.boardWidth - 3 && x <= Board.boardWidth - 1)
                        removeAndAddStone(stones, zobrist, x, y, otherSide, extraStones);
                    }
                  }
                } else noRoomForSideKo = true;
              }
            }
          } else {
            totalArea =
                (Board.boardWidth - Math.max(0, leftIndex))
                    * (Board.boardHeight - Math.max(0, topIndex));
            for (int x = 0; x < Board.boardWidth; x++) {
              for (int y = 0; y < Board.boardHeight; y++) {
                if (x > leftIndex) {
                  if (y > bottomIndex || (x > rightIndex && y >= topIndex)) {
                    if (y == topIndex) Utils.addStone(stones, zobrist, x, y, side, extraStones);
                    else if ((y + x) % 2 == 0)
                      Utils.addStone(stones, zobrist, x, y, side, extraStones);
                  }
                }
                if (x == leftIndex) {
                  if (y > bottomIndex) Utils.addStone(stones, zobrist, x, y, side, extraStones);
                }
                if (x >= leftIndex) {
                  if (y == topIndex - 1)
                    Utils.addStone(stones, zobrist, x, y, otherSide, extraStones);
                  if (y < topIndex - 1)
                    if ((y + x) % 2 == 0)
                      Utils.addStone(stones, zobrist, x, y, otherSide, extraStones);
                }
                if (x == leftIndex - 1) {
                  Utils.addStone(stones, zobrist, x, y, otherSide, extraStones);
                }
                if (x < leftIndex - 1) {
                  if ((y + x) % 2 == 0)
                    Utils.addStone(stones, zobrist, x, y, otherSide, extraStones);
                }
              }
            }
            if (addKoThreatSide) {
              if (Board.boardWidth - leftIndex - 1 < 4) {
                noRoomForSideKo = true;
              } else if (Board.boardHeight - bottomIndex - 1 >= 2) {
                for (int x = 0; x < Board.boardWidth; x++) {
                  for (int y = 0; y < Board.boardHeight; y++) {
                    if ((y == Board.boardHeight - 1 && x == Board.boardWidth - 1)
                        || (y == Board.boardHeight - 1 && x == Board.boardWidth - 4)
                        || (y == Board.boardHeight - 2 && x == Board.boardWidth - 4))
                      removeAndAddStone(stones, zobrist, x, y, Stone.EMPTY, extraStones);
                    if (y == Board.boardHeight - 1 && x == Board.boardWidth - 2)
                      removeAndAddStone(stones, zobrist, x, y, otherSide, extraStones);
                    if (y == Board.boardHeight - 1 && x == Board.boardWidth - 3)
                      removeAndAddStone(stones, zobrist, x, y, side, extraStones);
                    if (y == Board.boardHeight - 2
                        && x >= Board.boardWidth - 3
                        && x <= Board.boardWidth - 1)
                      removeAndAddStone(stones, zobrist, x, y, otherSide, extraStones);
                  }
                }
              } else noRoomForSideKo = true;
            }
          }
          if (addKoThreatOtherSide) {
            if (leftIndex < 5) {
              noRoomForOtherSideKo = true;
            } else {
              for (int x = 0; x < Board.boardWidth; x++) {
                for (int y = 0; y < Board.boardHeight; y++) {
                  if ((y == 0 && x == 0) || (y == 0 && x == 3) || (y == 1 && x == 3))
                    removeAndAddStone(stones, zobrist, x, y, Stone.EMPTY, extraStones);
                  if (y == 0 && x == 1) removeAndAddStone(stones, zobrist, x, y, side, extraStones);
                  if (y == 0 && x == 2)
                    removeAndAddStone(stones, zobrist, x, y, otherSide, extraStones);
                  if (y == 1 && x >= 0 && x <= 2)
                    removeAndAddStone(stones, zobrist, x, y, side, extraStones);
                }
              }
            }
          }
        }
        break;
      case 3:
        if (minArea <= halfAreaWithKomi) {
          int topSideIndex = halfAreaWithKomi / Board.boardWidth - 1;
          int remainStones = halfAreaWithKomi - (topSideIndex + 1) * Board.boardHeight;
          for (int x = 0; x < Board.boardWidth; x++) {
            for (int y = 0; y < Board.boardHeight; y++) {
              if (y < topSideIndex) {
                if (y <= bottomIndex) {
                  if (x < leftIndex || x > rightIndex || y < topIndex) {
                    if ((y + x) % 2 == 0) Utils.addStone(stones, zobrist, x, y, side, extraStones);
                  }
                } else {
                  if ((y + x) % 2 == 0) Utils.addStone(stones, zobrist, x, y, side, extraStones);
                }
              }
              if (y == topSideIndex) {
                Utils.addStone(stones, zobrist, x, y, side, extraStones);
              }
              if (remainStones > 0) {
                if (y == topSideIndex + 1) {
                  if (x < remainStones) Utils.addStone(stones, zobrist, x, y, side, extraStones);
                  else Utils.addStone(stones, zobrist, x, y, otherSide, extraStones);
                }
                if (y == topSideIndex + 2) {
                  Utils.addStone(stones, zobrist, x, y, otherSide, extraStones);
                }
                if (y > topSideIndex + 2) {
                  if ((y + x) % 2 == 0)
                    Utils.addStone(stones, zobrist, x, y, otherSide, extraStones);
                }
              } else {
                if (y == topSideIndex + 1) {
                  Utils.addStone(stones, zobrist, x, y, otherSide, extraStones);
                }
                if (y > topSideIndex + 1) {
                  if ((y + x) % 2 == 0)
                    Utils.addStone(stones, zobrist, x, y, otherSide, extraStones);
                }
              }
            }
          }
          if (addKoThreatSide) {
            if (topSideIndex < 4) {
              noRoomForSideKo = true;
            } else {
              int leftRoom = leftIndex;
              int rightRoom = Board.boardWidth - rightIndex - 1;
              if (leftRoom >= 2) {
                for (int x = 0; x < Board.boardWidth; x++) {
                  for (int y = 0; y < Board.boardHeight; y++) {
                    if ((x == 0 && y == 0) || (x == 0 && y == 3) || (x == 1 && y == 3))
                      removeAndAddStone(stones, zobrist, x, y, Stone.EMPTY, extraStones);
                    if (x == 0 && y == 1)
                      removeAndAddStone(stones, zobrist, x, y, otherSide, extraStones);
                    if (x == 0 && y == 2)
                      removeAndAddStone(stones, zobrist, x, y, side, extraStones);
                    if (x == 1 && y >= 0 && y <= 2)
                      removeAndAddStone(stones, zobrist, x, y, otherSide, extraStones);
                  }
                }
              } else if (rightRoom >= 2) {
                for (int x = 0; x < Board.boardWidth; x++) {
                  for (int y = 0; y < Board.boardHeight; y++) {
                    if ((x == Board.boardWidth - 1 && y == 0)
                        || (x == Board.boardWidth - 1 && y == 3)
                        || (x == Board.boardWidth - 2 && y == 3))
                      removeAndAddStone(stones, zobrist, x, y, Stone.EMPTY, extraStones);
                    if (x == Board.boardWidth - 1 && y == 1)
                      removeAndAddStone(stones, zobrist, x, y, otherSide, extraStones);
                    if (x == Board.boardWidth - 1 && y == 2)
                      removeAndAddStone(stones, zobrist, x, y, side, extraStones);
                    if (x == Board.boardWidth - 2 && y >= 0 && y <= 2)
                      removeAndAddStone(stones, zobrist, x, y, otherSide, extraStones);
                  }
                }
              } else noRoomForSideKo = true;
            }
          }
          if (addKoThreatOtherSide) {
            if (Board.boardHeight - topSideIndex - 2 < 5) {
              noRoomForOtherSideKo = true;
            } else {
              for (int x = 0; x < Board.boardWidth; x++) {
                for (int y = 0; y < Board.boardHeight; y++) {
                  if ((x == 0 && y == Board.boardHeight - 1)
                      || (x == 0 && y == Board.boardHeight - 4)
                      || (x == 1 && y == Board.boardHeight - 4))
                    removeAndAddStone(stones, zobrist, x, y, Stone.EMPTY, extraStones);
                  if (x == 0 && y == Board.boardHeight - 2)
                    removeAndAddStone(stones, zobrist, x, y, side, extraStones);
                  if (x == 0 && y == Board.boardHeight - 3)
                    removeAndAddStone(stones, zobrist, x, y, otherSide, extraStones);
                  if (x == 1 && y >= Board.boardHeight - 3 && y <= Board.boardHeight - 1)
                    removeAndAddStone(stones, zobrist, x, y, side, extraStones);
                }
              }
            }
          }
        } else {
          outOfHalf = true;
          boolean leftMin = false;
          int leftRoom = leftIndex;
          int rightRoom = Board.boardWidth - rightIndex - 1;
          if (leftRoom <= rightRoom) leftMin = true;
          if (addKoThreatSide) {
            if (bottomIndex < 4) {
              noRoomForSideKo = true;
            } else if (leftRoom >= 2 && rightRoom < 2) {
              leftMin = true;
            } else if (leftRoom < 2 && rightRoom >= 2) {
              leftMin = false;
            }
          }
          if (leftMin) {
            totalArea =
                Math.min(Board.boardWidth, rightIndex + 1)
                    * Math.min(Board.boardHeight, bottomIndex + 1);
            for (int x = 0; x < Board.boardWidth; x++) {
              for (int y = 0; y < Board.boardHeight; y++) {
                if (y < bottomIndex) {
                  if (x < leftIndex || (x <= rightIndex && y < topIndex)) {
                    if (x == rightIndex) Utils.addStone(stones, zobrist, x, y, side, extraStones);
                    else if ((y + x) % 2 == 0)
                      Utils.addStone(stones, zobrist, x, y, side, extraStones);
                  }
                }
                if (y == bottomIndex) {
                  if (x < leftIndex) Utils.addStone(stones, zobrist, x, y, side, extraStones);
                }
                if (y <= bottomIndex) {
                  if (x == rightIndex + 1)
                    Utils.addStone(stones, zobrist, x, y, otherSide, extraStones);
                  if (x > rightIndex + 1)
                    if ((y + x) % 2 == 0)
                      Utils.addStone(stones, zobrist, x, y, otherSide, extraStones);
                }
                if (y == bottomIndex + 1) {
                  Utils.addStone(stones, zobrist, x, y, otherSide, extraStones);
                }
                if (y > bottomIndex + 1) {
                  if ((y + x) % 2 == 0)
                    Utils.addStone(stones, zobrist, x, y, otherSide, extraStones);
                }
              }
            }
            if (addKoThreatSide) {
              if (bottomIndex < 4) {
                noRoomForSideKo = true;
              } else {
                if (leftIndex >= 2) {
                  for (int x = 0; x < Board.boardWidth; x++) {
                    for (int y = 0; y < Board.boardHeight; y++) {
                      if ((x == 0 && y == 0) || (x == 0 && y == 3) || (x == 1 && y == 3))
                        removeAndAddStone(stones, zobrist, x, y, Stone.EMPTY, extraStones);
                      if (x == 0 && y == 1)
                        removeAndAddStone(stones, zobrist, x, y, otherSide, extraStones);
                      if (x == 0 && y == 2)
                        removeAndAddStone(stones, zobrist, x, y, side, extraStones);
                      if (x == 1 && y >= 0 && y <= 2)
                        removeAndAddStone(stones, zobrist, x, y, otherSide, extraStones);
                    }
                  }
                } else noRoomForSideKo = true;
              }
            }
          } else {
            totalArea =
                (Board.boardWidth - Math.max(0, leftIndex - 1))
                    * Math.min(Board.boardHeight, bottomIndex + 1);
            for (int x = 0; x < Board.boardWidth; x++) {
              for (int y = 0; y < Board.boardHeight; y++) {
                if ((x > rightIndex && y < bottomIndex) || (x >= leftIndex && y < topIndex)) {
                  if (x == leftIndex) Utils.addStone(stones, zobrist, x, y, side, extraStones);
                  else if ((y + x) % 2 == 0)
                    Utils.addStone(stones, zobrist, x, y, side, extraStones);
                }
                if (y == bottomIndex) {
                  if (x > rightIndex) Utils.addStone(stones, zobrist, x, y, side, extraStones);
                }
                if (y <= bottomIndex) {
                  if (x == leftIndex - 1)
                    Utils.addStone(stones, zobrist, x, y, otherSide, extraStones);
                  if (x < leftIndex - 1)
                    if ((y + x) % 2 == 0)
                      Utils.addStone(stones, zobrist, x, y, otherSide, extraStones);
                }
                if (y == bottomIndex + 1) {
                  Utils.addStone(stones, zobrist, x, y, otherSide, extraStones);
                }
                if (y > bottomIndex + 1) {
                  if ((y + x) % 2 == 0)
                    Utils.addStone(stones, zobrist, x, y, otherSide, extraStones);
                }
              }
            }
            if (addKoThreatSide) {
              if (bottomIndex < 4) {
                noRoomForSideKo = true;
              } else if (Board.boardWidth - rightIndex - 1 >= 2) {
                for (int x = 0; x < Board.boardWidth; x++) {
                  for (int y = 0; y < Board.boardHeight; y++) {
                    if ((x == Board.boardWidth - 1 && y == 0)
                        || (x == Board.boardWidth - 1 && y == 3)
                        || (x == Board.boardWidth - 2 && y == 3))
                      removeAndAddStone(stones, zobrist, x, y, Stone.EMPTY, extraStones);
                    if (x == Board.boardWidth - 1 && y == 1)
                      removeAndAddStone(stones, zobrist, x, y, otherSide, extraStones);
                    if (x == Board.boardWidth - 1 && y == 2)
                      removeAndAddStone(stones, zobrist, x, y, side, extraStones);
                    if (x == Board.boardWidth - 2 && y >= 0 && y <= 2)
                      removeAndAddStone(stones, zobrist, x, y, otherSide, extraStones);
                  }
                }
              } else noRoomForSideKo = true;
            }
          }
          if (addKoThreatOtherSide) {
            if (Board.boardHeight - bottomIndex - 1 < 5) {
              noRoomForOtherSideKo = true;
            } else {
              for (int x = 0; x < Board.boardWidth; x++) {
                for (int y = 0; y < Board.boardHeight; y++) {
                  if ((x == 0 && y == Board.boardHeight - 1)
                      || (x == 0 && y == Board.boardHeight - 4)
                      || (x == 1 && y == Board.boardHeight - 4))
                    removeAndAddStone(stones, zobrist, x, y, Stone.EMPTY, extraStones);
                  if (x == 0 && y == Board.boardHeight - 2)
                    removeAndAddStone(stones, zobrist, x, y, side, extraStones);
                  if (x == 0 && y == Board.boardHeight - 3)
                    removeAndAddStone(stones, zobrist, x, y, otherSide, extraStones);
                  if (x == 1 && y >= Board.boardHeight - 3 && y <= Board.boardHeight - 1)
                    removeAndAddStone(stones, zobrist, x, y, side, extraStones);
                }
              }
            }
          }
        }
        break;
      case 4:
        if (minArea <= halfAreaWithKomi) {
          int bottomSideIndex = halfAreaWithKomi / Board.boardWidth + 1;
          int remainStones =
              halfAreaWithKomi - (Board.boardHeight - bottomSideIndex) * Board.boardHeight;
          for (int x = 0; x < Board.boardWidth; x++) {
            for (int y = 0; y < Board.boardHeight; y++) {
              if (y > bottomSideIndex) {
                if (y >= topIndex && y <= bottomIndex) {
                  if (x < leftIndex || x > rightIndex) {
                    if ((y + x) % 2 == 0) Utils.addStone(stones, zobrist, x, y, side, extraStones);
                  }
                } else {
                  if ((y + x) % 2 == 0) Utils.addStone(stones, zobrist, x, y, side, extraStones);
                }
              }
              if (y == bottomSideIndex) {
                Utils.addStone(stones, zobrist, x, y, side, extraStones);
              }
              if (remainStones > 0) {
                if (y == bottomSideIndex - 1) {
                  if (x < remainStones) Utils.addStone(stones, zobrist, x, y, side, extraStones);
                  else Utils.addStone(stones, zobrist, x, y, otherSide, extraStones);
                }
                if (y == bottomSideIndex - 2) {
                  Utils.addStone(stones, zobrist, x, y, otherSide, extraStones);
                }
                if (y < bottomSideIndex - 2) {
                  if ((y + x) % 2 == 0)
                    Utils.addStone(stones, zobrist, x, y, otherSide, extraStones);
                }
              } else {
                if (y == bottomSideIndex - 1) {
                  Utils.addStone(stones, zobrist, x, y, otherSide, extraStones);
                }
                if (y < bottomSideIndex - 1) {
                  if ((y + x) % 2 == 0)
                    Utils.addStone(stones, zobrist, x, y, otherSide, extraStones);
                }
              }
            }
          }
          if (addKoThreatSide) {
            if (Board.boardHeight - bottomSideIndex - 1 < 4) {
              noRoomForSideKo = true;
            } else {
              int leftRoom = leftIndex;
              int rightRoom = Board.boardWidth - rightIndex - 1;
              if (leftRoom >= 2) {
                for (int x = 0; x < Board.boardWidth; x++) {
                  for (int y = 0; y < Board.boardHeight; y++) {
                    if ((x == 0 && y == Board.boardHeight - 1)
                        || (x == 0 && y == Board.boardHeight - 4)
                        || (x == 1 && y == Board.boardHeight - 4))
                      removeAndAddStone(stones, zobrist, x, y, Stone.EMPTY, extraStones);
                    if (x == 0 && y == Board.boardHeight - 2)
                      removeAndAddStone(stones, zobrist, x, y, otherSide, extraStones);
                    if (x == 0 && y == Board.boardHeight - 3)
                      removeAndAddStone(stones, zobrist, x, y, side, extraStones);
                    if (x == 1 && y >= Board.boardHeight - 3 && y <= Board.boardHeight - 1)
                      removeAndAddStone(stones, zobrist, x, y, otherSide, extraStones);
                  }
                }
              } else if (rightRoom >= 2) {
                for (int x = 0; x < Board.boardWidth; x++) {
                  for (int y = 0; y < Board.boardHeight; y++) {
                    if ((x == Board.boardWidth - 1 && y == Board.boardHeight - 1)
                        || (x == Board.boardWidth - 1 && y == Board.boardHeight - 4)
                        || (x == Board.boardWidth - 2 && y == Board.boardHeight - 4))
                      removeAndAddStone(stones, zobrist, x, y, Stone.EMPTY, extraStones);
                    if (x == Board.boardWidth - 1 && y == Board.boardHeight - 2)
                      removeAndAddStone(stones, zobrist, x, y, otherSide, extraStones);
                    if (x == Board.boardWidth - 1 && y == Board.boardHeight - 3)
                      removeAndAddStone(stones, zobrist, x, y, side, extraStones);
                    if (x == Board.boardWidth - 2
                        && y >= Board.boardHeight - 3
                        && y <= Board.boardHeight - 1)
                      removeAndAddStone(stones, zobrist, x, y, otherSide, extraStones);
                  }
                }
              } else noRoomForSideKo = true;
            }
          }
          if (addKoThreatOtherSide) {
            if (bottomSideIndex - 1 < 5) {
              noRoomForOtherSideKo = true;
            } else {
              for (int x = 0; x < Board.boardWidth; x++) {
                for (int y = 0; y < Board.boardHeight; y++) {
                  if ((x == 0 && y == 0) || (x == 0 && y == 3) || (x == 1 && y == 3))
                    removeAndAddStone(stones, zobrist, x, y, Stone.EMPTY, extraStones);
                  if (x == 0 && y == 1) removeAndAddStone(stones, zobrist, x, y, side, extraStones);
                  if (x == 0 && y == 2)
                    removeAndAddStone(stones, zobrist, x, y, otherSide, extraStones);
                  if (x == 1 && y >= 0 && y <= 2)
                    removeAndAddStone(stones, zobrist, x, y, side, extraStones);
                }
              }
            }
          }
        } else {
          outOfHalf = true;
          boolean leftMin = false;
          int leftRoom = leftIndex;
          int rightRoom = Board.boardWidth - rightIndex - 1;
          if (leftRoom <= rightRoom) leftMin = true;
          if (addKoThreatSide) {
            if (bottomIndex < 4) {
              noRoomForSideKo = true;
            } else if (leftRoom >= 2 && rightRoom < 2) {
              leftMin = true;
            } else if (leftRoom < 2 && rightRoom >= 2) {
              leftMin = false;
            }
          }
          if (leftMin) {
            totalArea =
                Math.min(Board.boardWidth, rightIndex + 1)
                    * (Board.boardHeight - Math.max(0, topIndex));
            for (int x = 0; x < Board.boardWidth; x++) {
              for (int y = 0; y < Board.boardHeight; y++) {
                if (y > topIndex) {
                  if (x < leftIndex || (y >= bottomIndex && x <= rightIndex)) {
                    if (x == rightIndex) Utils.addStone(stones, zobrist, x, y, side, extraStones);
                    else if ((y + x) % 2 == 0)
                      Utils.addStone(stones, zobrist, x, y, side, extraStones);
                  }
                }
                if (y == topIndex) {
                  if (x < leftIndex) Utils.addStone(stones, zobrist, x, y, side, extraStones);
                }
                if (y >= topIndex) {
                  if (x == rightIndex + 1)
                    Utils.addStone(stones, zobrist, x, y, otherSide, extraStones);
                  if (x > rightIndex + 1)
                    if ((y + x) % 2 == 0)
                      Utils.addStone(stones, zobrist, x, y, otherSide, extraStones);
                }
                if (y == topIndex - 1) {
                  Utils.addStone(stones, zobrist, x, y, otherSide, extraStones);
                }
                if (y < topIndex - 1) {
                  if ((y + x) % 2 == 0)
                    Utils.addStone(stones, zobrist, x, y, otherSide, extraStones);
                }
              }
            }
            if (addKoThreatSide) {
              if (Board.boardHeight - topIndex - 1 < 4) {
                noRoomForSideKo = true;
              } else {
                if (leftIndex >= 2) {
                  for (int x = 0; x < Board.boardWidth; x++) {
                    for (int y = 0; y < Board.boardHeight; y++) {
                      if ((x == 0 && y == Board.boardHeight - 1)
                          || (x == 0 && y == Board.boardHeight - 4)
                          || (x == 1 && y == Board.boardHeight - 4))
                        removeAndAddStone(stones, zobrist, x, y, Stone.EMPTY, extraStones);
                      if (x == 0 && y == Board.boardHeight - 2)
                        removeAndAddStone(stones, zobrist, x, y, otherSide, extraStones);
                      if (x == 0 && y == Board.boardHeight - 3)
                        removeAndAddStone(stones, zobrist, x, y, side, extraStones);
                      if (x == 1 && y >= Board.boardHeight - 3 && y <= Board.boardHeight - 1)
                        removeAndAddStone(stones, zobrist, x, y, otherSide, extraStones);
                    }
                  }
                } else noRoomForSideKo = true;
              }
            }
          } else {
            totalArea =
                (Board.boardWidth - Math.max(0, leftIndex))
                    * (Board.boardHeight - Math.max(0, topIndex));
            for (int x = 0; x < Board.boardWidth; x++) {
              for (int y = 0; y < Board.boardHeight; y++) {
                if (y > topIndex) {
                  if (x > rightIndex || (y >= bottomIndex && x >= leftIndex)) {
                    if (x == leftIndex) Utils.addStone(stones, zobrist, x, y, side, extraStones);
                    else if ((y + x) % 2 == 0)
                      Utils.addStone(stones, zobrist, x, y, side, extraStones);
                  }
                }
                if (y == topIndex) {
                  if (x > rightIndex) Utils.addStone(stones, zobrist, x, y, side, extraStones);
                }
                if (y >= topIndex) {
                  if (x == leftIndex - 1)
                    Utils.addStone(stones, zobrist, x, y, otherSide, extraStones);
                  if (x < leftIndex - 1)
                    if ((y + x) % 2 == 0)
                      Utils.addStone(stones, zobrist, x, y, otherSide, extraStones);
                }
                if (y == topIndex - 1) {
                  Utils.addStone(stones, zobrist, x, y, otherSide, extraStones);
                }
                if (y < topIndex - 1) {
                  if ((y + x) % 2 == 0)
                    Utils.addStone(stones, zobrist, x, y, otherSide, extraStones);
                }
              }
            }
            if (addKoThreatSide) {
              if (Board.boardHeight - topIndex - 1 < 4) {
                noRoomForSideKo = true;
              } else if (Board.boardWidth - rightIndex - 1 >= 2) {
                for (int x = 0; x < Board.boardWidth; x++) {
                  for (int y = 0; y < Board.boardHeight; y++) {
                    if ((x == Board.boardWidth - 1 && y == Board.boardHeight - 1)
                        || (x == Board.boardWidth - 1 && y == Board.boardHeight - 4)
                        || (x == Board.boardWidth - 2 && y == Board.boardHeight - 4))
                      removeAndAddStone(stones, zobrist, x, y, Stone.EMPTY, extraStones);
                    if (x == Board.boardWidth - 1 && y == Board.boardHeight - 2)
                      removeAndAddStone(stones, zobrist, x, y, otherSide, extraStones);
                    if (x == Board.boardWidth - 1 && y == Board.boardHeight - 3)
                      removeAndAddStone(stones, zobrist, x, y, side, extraStones);
                    if (x == Board.boardWidth - 2
                        && y >= Board.boardHeight - 3
                        && y <= Board.boardHeight - 1)
                      removeAndAddStone(stones, zobrist, x, y, otherSide, extraStones);
                  }
                }
              } else noRoomForSideKo = true;
            }
          }
          if (addKoThreatOtherSide) {
            if (topIndex < 5) {
              noRoomForOtherSideKo = true;
            } else {
              for (int x = 0; x < Board.boardWidth; x++) {
                for (int y = 0; y < Board.boardHeight; y++) {
                  if ((x == 0 && y == 0) || (x == 0 && y == 3) || (x == 1 && y == 3))
                    removeAndAddStone(stones, zobrist, x, y, Stone.EMPTY, extraStones);
                  if (x == 0 && y == 1) removeAndAddStone(stones, zobrist, x, y, side, extraStones);
                  if (x == 0 && y == 2)
                    removeAndAddStone(stones, zobrist, x, y, otherSide, extraStones);
                  if (x == 1 && y >= 0 && y <= 2)
                    removeAndAddStone(stones, zobrist, x, y, side, extraStones);
                }
              }
            }
          }
        }
        break;
    }
    if (outOfHalf) {
      komi = 2 * (totalArea - Math.ceil((Board.boardHeight * Board.boardWidth) / 2.0));
    }
    if (side == Stone.WHITE) komi = -komi;
    komi = Math.min(150, komi);
    komi = Math.max(-150, komi);
    // add ko threat
    if (noRoomForOtherSideKo)
      Utils.showMsg(
          Lizzie.resourceBundle.getString("Tsumego.noRoomForKoThreat1")
              + " ["
              + (this.side == Stone.BLACK
                  ? Lizzie.resourceBundle.getString("Tsumego.noRoomForKoThreat.black")
                  : Lizzie.resourceBundle.getString("Tsumego.noRoomForKoThreat.white"))
              + "] "
              + Lizzie.resourceBundle.getString("Tsumego.noRoomForKoThreat2"));
    if (noRoomForSideKo)
      Utils.showMsg(
          Lizzie.resourceBundle.getString("Tsumego.noRoomForKoThreat1")
              + " ["
              + (this.side == Stone.BLACK
                  ? Lizzie.resourceBundle.getString("Tsumego.noRoomForKoThreat.white")
                  : Lizzie.resourceBundle.getString("Tsumego.noRoomForKoThreat.black"))
              + "] "
              + Lizzie.resourceBundle.getString("Tsumego.noRoomForKoThreat2"));
    boolean isBlackTurn = Lizzie.board.getHistory().isBlacksTurn();
    if (forceToPlay) isBlackTurn = blackToPlay;
    Lizzie.board.flattenWithCondition(stones, zobrist, isBlackTurn, extraStones, komi);
  }

  private void removeAndAddStone(
      Stone[] stones,
      Zobrist zobrist,
      int x,
      int y,
      Stone color,
      List<extraMoveForTsumego> extraStones) {
    if (color == Stone.EMPTY && stones[Board.getIndex(x, y)] == Stone.EMPTY) return;
    if (color != Stone.EMPTY && stones[Board.getIndex(x, y)] != Stone.EMPTY) {
      zobrist.toggleStone(x, y, stones[Board.getIndex(x, y)]);
    }
    stones[Board.getIndex(x, y)] = color;
    zobrist.toggleStone(x, y, color);
    if (extraStones != null)
      for (int i = 0; i < extraStones.size(); i++) {

        if (extraStones.get(i).x == x && extraStones.get(i).y == y) {
          extraStones.remove(i);
          break;
        }
      }
    if (color != Stone.EMPTY) {
      extraMoveForTsumego stone = new extraMoveForTsumego();
      stone.x = x;
      stone.y = y;
      stone.color = color;
      extraStones.add(stone);
    }
  }
}
