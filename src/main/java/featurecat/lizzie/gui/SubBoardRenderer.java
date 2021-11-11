package featurecat.lizzie.gui;

import static java.awt.RenderingHints.*;
import static java.awt.image.BufferedImage.TYPE_INT_ARGB;
import static java.lang.Math.max;
import static java.lang.Math.min;

import featurecat.lizzie.Config;
import featurecat.lizzie.Lizzie;
import featurecat.lizzie.analysis.Branch;
import featurecat.lizzie.analysis.Leelaz;
import featurecat.lizzie.analysis.MoveData;
import featurecat.lizzie.rules.Board;
import featurecat.lizzie.rules.BoardHistoryNode;
import featurecat.lizzie.rules.Stone;
import featurecat.lizzie.rules.Zobrist;
import java.awt.*;
import java.awt.font.TextAttribute;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.imageio.ImageIO;
import org.json.JSONException;
import org.json.JSONObject;

public class SubBoardRenderer {
  // Percentage of the boardLength to offset before drawing black lines
  private static final double MARGIN = 0.03;
  private static final double STARPOINT_DIAMETER = 0.015;
  private static final BufferedImage emptyImage = new BufferedImage(1, 1, TYPE_INT_ARGB);

  public int x, y;
  public int boardWidth, boardHeight;
  private JSONObject uiPersist;
  private int scaledMarginWidth, availableWidth, squareWidth, stoneRadius;
  private int scaledMarginHeight, availableHeight, squareHeight;
  public Optional<Branch> branchOpt = Optional.empty();

  List<String> variation;
  // boolean variationBlackToPlay;
  private Branch branch;
  // private boolean oldBlackToPlay;
  private List<MoveData> bestMoves;
  private ArrayList<Double> estimateArray;
  private ArrayList<Double> preEstimateArray;
  public int bestmovesNum = 0;

  TexturePaint paint;
  private BufferedImage cachedBackgroundImage = emptyImage;
  private boolean cachedBackgroundImageHasCoordinatesEnabled = false;
  private int cachedBoardWidth = 0, cachedBoardHeight = 0;
  private BufferedImage cachedStonesImage = emptyImage;
  // private BufferedImage cachedStonesImagedraged = emptyImage;
  private BufferedImage blockimage = emptyImage;
  private BufferedImage kataEstimateImage = emptyImage;
  private BufferedImage heatimage = emptyImage;

  private BufferedImage cachedBoardImage = emptyImage;
  private BufferedImage cachedWallpaperImage = emptyImage;
  // private BufferedImage cachedStonesShadowImage = emptyImage;
  // private BufferedImage cachedStonesShadowImagedraged = emptyImage;
  private Zobrist cachedZhash = new Zobrist(); // defaults to an empty board

  private BufferedImage cachedHeatImage = emptyImage;

  private BufferedImage branchStonesImage = emptyImage;
  //  private BufferedImage branchStonesShadowImage;

  public Optional<List<String>> variationOpt;

  // special values of displayedBranchLength
  public static final int SHOW_RAW_BOARD = -1;
  public static final int SHOW_NORMAL_BOARD = -2;

  public int displayedBranchLength = SHOW_NORMAL_BOARD;
  private int cachedDisplayedBranchLength = SHOW_RAW_BOARD;
  private boolean showingBranch = false;
  Stone[] stonesTemp;
  //  private boolean isMainBoard = false;
  public boolean variationBlackToPlay = false;
  public boolean wheeled = false;
  private int maxAlpha = 240;
  public boolean showHeat = Lizzie.config.showHeat;
  public boolean showHeatAfterCalc = Lizzie.config.showHeatAfterCalc;
  // private long heatTime;

  private int subOrder = 0;
  public boolean isMouseOver = false;
  //  public int stopRefresh = 0;//0=未点击 1=暂停 2=继续
  public boolean statChanged = false;

  //  public void toggleStopRefresh() {
  //	  switch (stopRefresh) {
  //	  case 0:
  //		  stopRefresh=2;
  //		  break;
  //	  case 1:
  //		  stopRefresh=0;
  //		  break;
  //	  case 2:
  //		  stopRefresh=1;
  //		  break;
  //	  }
  //  }

  public void setOrder(int order) {
    subOrder = order;
    bestmovesNum = order;
    if (order != 3) {
      showHeat = false;
      showHeatAfterCalc = false;
    }
  }

  public void clearAfterMove() {
    // stopRefresh = 0;
    if (!isMouseOver) {
      bestmovesNum = subOrder;
      setDisplayedBranchLength(-2);
    }
  }

  public void reDrawGobanAnyway() {
    cachedBoardWidth = -1;
  }

  public void reDrawBackgroundAnyway() {
    cachedWallpaperImage = emptyImage;
  }

  public SubBoardRenderer(boolean isMainBoard) {
    uiPersist = Lizzie.config.persisted.getJSONObject("ui-persist");
    try {
      maxAlpha = uiPersist.getInt("max-alpha");
    } catch (JSONException e) {
    }
    // this.isMainBoard = isMainBoard;
  }

  /** Draw a go board */
  public void draw(Graphics2D g) {
    drawGoban(g);
    if ((!showHeat && !Lizzie.config.subBoardRaw)
        || (Lizzie.config.isFourSubMode() && !showHeat && this != Lizzie.frame.subBoardRenderer4)) {
      drawPlay(g);
      if (!maybeCleanBranch()) {
        if (!isMouseOver || statChanged || wheeled) drawBranch();
      }
      if (wheeled) wheeled = false;
    }
    if (!isMouseOver) drawStones();
    if (showHeat) {
      drawLeelazSuggestions();
    }
    drawEstimate();
    renderImages(g);
    if (Lizzie.frame.isInPlayMode()) return;
    if (showingBranch && !showHeat) {
      drawMoveNumbers(g);
      return;
    }
  }

  private boolean maybeCleanBranch() {
    // TODO Auto-generated method stub
    if ((Lizzie.board.getHistory().isBlacksTurn() && !Lizzie.config.showBlackCandidates)
        || (!Lizzie.board.getHistory().isBlacksTurn() && !Lizzie.config.showWhiteCandidates)) {
      clearBranch();
      showingBranch = false;
      return true;
    }
    return false;
  }

  private boolean isShowingEstimate = false;

  private void drawEstimate() {
    boolean hasDraw = false;
    if (!Lizzie.frame.isInScoreMode
        && !Lizzie.frame.isCounting
        && !Lizzie.frame.isShowingHeatmap
        && Lizzie.config.showKataGoEstimate
        && Lizzie.config.showKataGoEstimateOnMainbord) {
      if (estimateArray != null) {
        if (Lizzie.config.showKataGoEstimateBySize) {
          drawKataEstimateBySize(estimateArray, false);
        } else {
          drawKataEstimateByTransparent(estimateArray, false);
        }
        hasDraw = true;
      } else if (preEstimateArray != null) {
        if (Lizzie.config.showKataGoEstimateBySize) {
          drawKataEstimateBySize(preEstimateArray, true);
        } else {
          drawKataEstimateByTransparent(preEstimateArray, true);
        }
        hasDraw = true;
      }
    }
    if (!hasDraw && isShowingEstimate) {
      removeKataEstimateImage();
    }
    isShowingEstimate = hasDraw;
  }

  private void drawPlay(Graphics2D g) {
    // TODO Auto-generated method stub
    // if (Lizzie.frame.extraMode == 1) {
    g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
    g.setRenderingHint(KEY_ANTIALIASING, VALUE_ANTIALIAS_ON);
    g.setColor(Color.BLACK);
    //      if (stopRefresh==1) {
    //        g.fillRect(
    //            x + boardWidth - stoneRadius * 7/2,
    //            y + boardHeight - scaledMarginHeight * 9 / 10,
    //            scaledMarginHeight * 8 / 10,
    //            scaledMarginHeight * 8 / 10);
    //      }
    //
    //      else if (stopRefresh==2) {
    //    	  int[] xPoints = {
    //    	          x + boardWidth - stoneRadius * 7/2,
    //    	          x + boardWidth - stoneRadius * 7/2,
    //    	          x + boardWidth - stoneRadius * 5/2
    //    	        };
    //    	        int[] yPoints = {
    //    	          y + boardHeight - 1,
    //    	          y + boardHeight - scaledMarginHeight + 1,
    //    	          y + boardHeight - scaledMarginHeight / 2
    //    	        };
    //    	        g.fillPolygon(xPoints, yPoints, 3);
    //        }
    //      else
    if (isMouseOver) {
      g.fillRect(
          x + boardWidth - stoneRadius * 7 / 2,
          y + boardHeight - scaledMarginHeight * 9 / 10,
          scaledMarginHeight * 3 / 10,
          scaledMarginHeight * 8 / 10);
      g.fillRect(
          x + boardWidth + scaledMarginHeight * 5 / 10 - stoneRadius * 7 / 2,
          y + boardHeight - scaledMarginHeight * 9 / 10,
          scaledMarginHeight * 3 / 10,
          scaledMarginHeight * 8 / 10);
    } else {
      int[] xPoints = {
        x + boardWidth - stoneRadius * 7 / 2,
        x + boardWidth - stoneRadius * 7 / 2,
        x + boardWidth - stoneRadius * 5 / 2
      };
      int[] yPoints = {
        y + boardHeight - 1,
        y + boardHeight - scaledMarginHeight + 1,
        y + boardHeight - scaledMarginHeight / 2
      };
      g.fillPolygon(xPoints, yPoints, 3);
    }
    // }
  }

  public void clearBranch() {
    branchStonesImage = new BufferedImage(boardWidth, boardHeight, TYPE_INT_ARGB);
  }
  /**
   * Return the best move of Leelaz's suggestions
   *
   * @return the optional coordinate name of the best move
   */
  public Optional<String> bestMoveCoordinateName() {
    return bestMoves.isEmpty() ? Optional.empty() : Optional.of(bestMoves.get(0).coordinate);
  }

  /** Calculate good values for boardLength, scaledMargin, availableLength, and squareLength */
  public static int[] availableLength(int boardWidth, int boardHeight, boolean showCoordinates) {
    int[] calculatedPixelMargins = calculatePixelMargins(boardWidth, boardHeight, showCoordinates);
    return (calculatedPixelMargins != null && calculatedPixelMargins.length >= 6)
        ? calculatedPixelMargins
        : new int[] {boardWidth, 0, boardWidth, boardHeight, 0, boardHeight};
  }

  /** Calculate good values for boardLength, scaledMargin, availableLength, and squareLength */
  public void setupSizeParameters() {
    int boardWidth0 = boardWidth;
    int boardHeight0 = boardHeight;

    int[] calculatedPixelMargins = calculatePixelMargins();
    boardWidth = calculatedPixelMargins[0];
    scaledMarginWidth = calculatedPixelMargins[1];
    availableWidth = calculatedPixelMargins[2];
    boardHeight = calculatedPixelMargins[3];
    scaledMarginHeight = calculatedPixelMargins[4];
    availableHeight = calculatedPixelMargins[5];

    squareWidth = calculateSquareWidth(availableWidth);
    squareHeight = calculateSquareHeight(availableHeight);
    if (squareWidth > squareHeight) {
      squareWidth = squareHeight;
      int newWidth = squareWidth * (Board.boardWidth - 1) + 1;
      int diff = availableWidth - newWidth;
      availableWidth = newWidth;
      boardWidth -= diff + (scaledMarginWidth - scaledMarginHeight) * 2;
      scaledMarginWidth = scaledMarginHeight;
    } else if (squareWidth < squareHeight) {
      squareHeight = squareWidth;
      int newHeight = squareHeight * (Board.boardHeight - 1) + 1;
      int diff = availableHeight - newHeight;
      availableHeight = newHeight;
      boardHeight -= diff + (scaledMarginHeight - scaledMarginWidth) * 2;
      scaledMarginHeight = scaledMarginWidth;
    }
    stoneRadius = max(squareWidth, squareHeight) < 4 ? 1 : max(squareWidth, squareHeight) / 2 - 1;

    // re-center board
    setLocation(x + (boardWidth0 - boardWidth) / 2, y + (boardHeight0 - boardHeight) / 2);
  }

  /**
   * Draw the green background and go board with lines. We cache the image for a performance boost.
   */
  private void drawGoban(Graphics2D g0) {

    int width = boardWidth;
    int height = boardHeight;

    // Draw the cached background image if frame size changes
    if (cachedBackgroundImage.getWidth() != width
        || cachedBackgroundImage.getHeight() != height
        || cachedBoardWidth != boardWidth
        || cachedBoardHeight != boardHeight
        //        || cachedX != x
        //        || cachedY != y
        || cachedBackgroundImageHasCoordinatesEnabled != showCoordinates()
        || Lizzie.board.isForceRefresh2()) {

      cachedBoardWidth = boardWidth;
      cachedBoardHeight = boardHeight;
      Lizzie.board.setForceRefresh2(false);

      cachedBackgroundImage = new BufferedImage(width, height, TYPE_INT_ARGB);
      Graphics2D g = cachedBackgroundImage.createGraphics();
      g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

      // Draw the wooden background
      drawWoodenBoard(g);

      // Draw the lines
      g.setColor(Color.BLACK);
      for (int i = 0; i < Board.boardHeight; i++) {
        // g.setStroke(new BasicStroke(stoneRadius / 15f));

        g.drawLine(
            scaledMarginWidth,
            scaledMarginHeight + squareHeight * i,
            scaledMarginWidth + availableWidth - 1,
            scaledMarginHeight + squareHeight * i);
      }

      for (int i = 0; i < Board.boardWidth; i++) {
        // g.setStroke(new BasicStroke(stoneRadius / 15f));

        g.drawLine(
            scaledMarginWidth + squareWidth * i,
            scaledMarginHeight,
            scaledMarginWidth + squareWidth * i,
            scaledMarginHeight + availableHeight - 1);
      }

      // Draw the star points
      drawStarPoints(g);

      // Draw coordinates if enabled
      if (showCoordinates()) {
        g.setColor(Color.BLACK);
        for (int i = 0; i < Board.boardWidth; i++) {
          drawString(
              g,
              scaledMarginWidth + squareWidth * i,
              scaledMarginHeight / 3,
              LizzieFrame.uiFont,
              Board.asName(i),
              stoneRadius * 4 / 5,
              stoneRadius);
          drawString(
              g,
              scaledMarginWidth + squareWidth * i,
              scaledMarginHeight / 3 + boardHeight,
              LizzieFrame.uiFont,
              Board.asName(i),
              stoneRadius * 4 / 5,
              stoneRadius);
        }
        for (int i = 0; i < Board.boardHeight; i++) {
          drawString(
              g,
              scaledMarginWidth / 3,
              scaledMarginHeight + squareHeight * i,
              LizzieFrame.uiFont,
              "" + (Board.boardHeight <= 25 ? (Board.boardHeight - i) : (i + 1)),
              stoneRadius * 4 / 5,
              stoneRadius);
          drawString(
              g,
              scaledMarginWidth / 3 + boardWidth,
              scaledMarginHeight + squareHeight * i,
              LizzieFrame.uiFont,
              "" + (Board.boardHeight <= 25 ? (Board.boardHeight - i) : (i + 1)),
              stoneRadius * 4 / 5,
              stoneRadius);
        }
      }
      g.dispose();
    }

    g0.setRenderingHint(KEY_ANTIALIASING, VALUE_ANTIALIAS_OFF);
    g0.drawImage(cachedBackgroundImage, x, y, null);
    //  cachedX = x;
    //  cachedY = y;
  }

  /**
   * Draws the star points on the board, according to board size
   *
   * @param g graphics2d object to draw
   */
  private void drawStarPoints(Graphics2D g) {
    if (Board.boardWidth == 19 && Board.boardHeight == 19) {
      drawStarPoints0(3, 3, 6, false, g);
    } else if (Board.boardWidth == 13 && Board.boardHeight == 13) {
      drawStarPoints0(2, 3, 6, true, g);
    } else if (Board.boardWidth == 9 && Board.boardHeight == 9) {
      drawStarPoints0(2, 2, 4, true, g);
    } else if (Board.boardWidth == 7 && Board.boardHeight == 7) {
      drawStarPoints0(2, 2, 2, true, g);
    } else if (Board.boardWidth == 5 && Board.boardHeight == 5) {
      drawStarPoints0(0, 0, 2, true, g);
    }
  }

  private void drawStarPoints0(
      int nStarpoints, int edgeOffset, int gridDistance, boolean center, Graphics2D g) {
    g.setRenderingHint(KEY_ANTIALIASING, VALUE_ANTIALIAS_ON);
    int starPointRadius = (int) (STARPOINT_DIAMETER * min(boardWidth, boardHeight)) / 2;
    for (int i = 0; i < nStarpoints; i++) {
      for (int j = 0; j < nStarpoints; j++) {
        int centerX = scaledMarginWidth + squareWidth * (edgeOffset + gridDistance * i);
        int centerY = scaledMarginHeight + squareHeight * (edgeOffset + gridDistance * j);
        fillCircle(g, centerX, centerY, starPointRadius);
      }
    }

    if (center) {
      int centerX = scaledMarginWidth + squareWidth * gridDistance;
      int centerY = scaledMarginHeight + squareHeight * gridDistance;
      fillCircle(g, centerX, centerY, starPointRadius);
    }
  }

  public void removeKataEstimateImage() {
    try {
      kataEstimateImage = new BufferedImage(boardWidth, boardHeight, TYPE_INT_ARGB);
    } catch (Exception ex) {
    }
  }

  public boolean shouldShowCountBlockBelow() {
    Leelaz leelaz = Lizzie.leelaz;
    if (leelaz.isKatago && leelaz.iskataHeatmapShowOwner) {
      return Lizzie.config.showPureEstimateBigBelow;
    }
    return Lizzie.config.showKataGoEstimateBigBelow;
  }

  public boolean shouldShowCountBlockBig() {
    Leelaz leelaz = Lizzie.leelaz;
    if (leelaz.isKatago && leelaz.iskataHeatmapShowOwner) {
      return Lizzie.config.showPureEstimateBigBelow;
    }
    return Lizzie.config.showKataGoEstimateBigBelow;
  }

  public void drawKataEstimateByTransparent(ArrayList<Double> tempcount, boolean reverse) {
    BufferedImage newEstimateImage = new BufferedImage(boardWidth, boardHeight, TYPE_INT_ARGB);
    Graphics2D g = newEstimateImage.createGraphics();
    boolean blackToPlay = Lizzie.board.getHistory().isBlacksTurn();
    if (reverse) blackToPlay = !blackToPlay;
    for (int i = 0; i < tempcount.size(); i++) {
      if ((tempcount.get(i) > 0 && blackToPlay) || (tempcount.get(i) < 0 && !blackToPlay)) {
        int y = i / Board.boardWidth;
        int x = i % Board.boardWidth;
        int stoneX = scaledMarginWidth + squareWidth * x;
        int stoneY = scaledMarginHeight + squareHeight * y;
        // g.setColor(Color.BLACK);

        int alpha =
            shouldShowCountBlockBig()
                ? (int) (tempcount.get(i) * 105)
                : (int) (tempcount.get(i) * 255);
        Color cl = new Color(0, 0, 0, Math.abs(alpha));
        if (!shouldShowCountBlockBig()
            && Lizzie.board.getHistory().getStones()[Board.getIndex(x, y)].isBlack()) {
          Color cl2 =
              new Color(
                  127 - (Math.abs(alpha) - 1) / 2,
                  127 - (Math.abs(alpha) - 1) / 2,
                  127 - (Math.abs(alpha) - 1) / 2,
                  255);
          g.setColor(cl2);
        } else g.setColor(cl);
        if (shouldShowCountBlockBig())
          g.fillRect(
              stoneX - squareWidth * 5 / 10,
              stoneY - squareWidth * 5 / 10,
              squareWidth,
              squareWidth);
        else
          g.fillRect(
              stoneX - squareWidth / 4, stoneY - squareWidth / 4, squareWidth / 2, squareWidth / 2);
      }
      if ((tempcount.get(i) < 0 && blackToPlay) || (tempcount.get(i) > 0 && !blackToPlay)) {
        int y = i / Board.boardWidth;
        int x = i % Board.boardWidth;
        int stoneX = scaledMarginWidth + squareWidth * x;
        int stoneY = scaledMarginHeight + squareHeight * y;
        int alpha =
            shouldShowCountBlockBig()
                ? (int) (tempcount.get(i) * 165)
                : (int) (tempcount.get(i) * 255);
        Color cl = new Color(255, 255, 255, Math.abs(alpha));
        g.setColor(cl);
        if (shouldShowCountBlockBig())
          g.fillRect(
              stoneX - squareWidth * 5 / 10,
              stoneY - squareWidth * 5 / 10,
              squareWidth,
              squareWidth);
        else
          g.fillRect(
              stoneX - squareWidth / 4, stoneY - squareWidth / 4, squareWidth / 2, squareWidth / 2);
      }
    }
    kataEstimateImage = newEstimateImage;
    g.dispose();
  }

  private double convertLength(double length) {
    double lengthab = Math.abs(length);
    if (lengthab > 0.2) {
      lengthab = lengthab * 6 / 10;
      return lengthab;
    } else {
      return 0;
    }
  }

  public void drawKataEstimateBySize(ArrayList<Double> tempcount, boolean reverse) {
    BufferedImage newEstimateImage = new BufferedImage(boardWidth, boardHeight, TYPE_INT_ARGB);
    Graphics2D g = newEstimateImage.createGraphics();
    boolean blackToPlay = Lizzie.board.getHistory().isBlacksTurn();
    if (reverse) blackToPlay = !blackToPlay;
    for (int i = 0; i < tempcount.size(); i++) {
      if ((tempcount.get(i) > 0 && blackToPlay) || (tempcount.get(i) < 0 && !blackToPlay)) {
        int y = i / Board.boardWidth;
        int x = i % Board.boardWidth;
        int stoneX = scaledMarginWidth + squareWidth * x;
        int stoneY = scaledMarginHeight + squareHeight * y;
        Color cl = new Color(0, 0, 0, 180);
        g.setColor(cl);
        int length = (int) (convertLength(tempcount.get(i)) * squareWidth);
        if (length > 0) g.fillRect(stoneX - length / 2, stoneY - length / 2, length, length);
      }
      if ((tempcount.get(i) < 0 && Lizzie.board.getHistory().isBlacksTurn())
          || (tempcount.get(i) > 0 && !Lizzie.board.getHistory().isBlacksTurn())) {
        int y = i / Board.boardWidth;
        int x = i % Board.boardWidth;
        int stoneX = scaledMarginWidth + squareWidth * x;
        int stoneY = scaledMarginHeight + squareHeight * y;
        int length = (int) (convertLength(tempcount.get(i)) * squareWidth);

        Color cl = new Color(255, 255, 255, 180);
        g.setColor(cl);
        if (length > 0) g.fillRect(stoneX - length / 2, stoneY - length / 2, length, length);
      }
    }
    kataEstimateImage = newEstimateImage;
    g.dispose();
  }

  public void drawcountblock(ArrayList<Integer> tempcount) {
    kataEstimateImage = new BufferedImage(boardWidth, boardHeight, TYPE_INT_ARGB);
    Graphics2D g = kataEstimateImage.createGraphics();
    for (int i = 0; i < tempcount.size(); i++) {
      if (tempcount.get(i) > 0) {
        int y = i / Board.boardWidth;
        int x = i % Board.boardWidth;
        int stoneX = scaledMarginWidth + squareWidth * x;
        int stoneY = scaledMarginHeight + squareHeight * y;
        g.setColor(Color.BLACK);
        g.fillRect(stoneX - stoneRadius / 2, stoneY - stoneRadius / 2, stoneRadius, stoneRadius);
      }
      if (tempcount.get(i) < 0) {
        int y = i / Board.boardWidth;
        int x = i % Board.boardWidth;
        int stoneX = scaledMarginWidth + squareWidth * x;
        int stoneY = scaledMarginHeight + squareHeight * y;
        g.setColor(Color.WHITE);
        g.fillRect(stoneX - stoneRadius / 2, stoneY - stoneRadius / 2, stoneRadius, stoneRadius);
      }
    }
    g.dispose();
  }

  public void removeblock() {
    blockimage = new BufferedImage(boardWidth, boardHeight, TYPE_INT_ARGB);
  }

  public void removeHeat() {
    heatimage = new BufferedImage(boardWidth, boardHeight, TYPE_INT_ARGB);
  }

  public void drawmoveblock(int x, int y, boolean isblack) {
    blockimage = new BufferedImage(boardWidth, boardHeight, TYPE_INT_ARGB);
    Stone[] stones = Lizzie.board.getStones();
    if (stones[Board.getIndex(x, y)].isBlack() || stones[Board.getIndex(x, y)].isWhite()) {
      return;
    }
    Graphics2D g = blockimage.createGraphics();
    int stoneX = scaledMarginWidth + squareWidth * x;
    int stoneY = scaledMarginHeight + squareHeight * y;
    g.setColor(isblack ? Color.BLACK : Color.WHITE);
    g.fillRect(stoneX - stoneRadius / 2, stoneY - stoneRadius / 2, stoneRadius, stoneRadius);
  }

  //  public void drawbadstone(int x, int y, Stone stone) {
  //    cachedStonesImagedraged = new BufferedImage(boardWidth, boardHeight, TYPE_INT_ARGB);
  //    cachedStonesShadowImagedraged = new BufferedImage(boardWidth, boardHeight, TYPE_INT_ARGB);
  //    Graphics2D g = cachedStonesImagedraged.createGraphics();
  //    Graphics2D gShadow = cachedStonesShadowImagedraged.createGraphics();
  //    gShadow.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
  //    // gShadow.setRenderingHint(KEY_ANTIALIASING, VALUE_ANTIALIAS_ON);
  //    // g.setRenderingHint(RenderingHints.KEY_RENDERING,
  //    // RenderingHints.VALUE_RENDER_QUALITY);
  //    g.setRenderingHint(KEY_ANTIALIASING, VALUE_ANTIALIAS_ON);
  //
  //    // gShadow.setRenderingHint(RenderingHints.KEY_RENDERING,
  //    // RenderingHints.VALUE_RENDER_QUALITY);
  //    // gShadow.setRenderingHint(KEY_ANTIALIASING, VALUE_ANTIALIAS_ON);
  //    int stoneX = scaledMarginWidth + squareWidth * x;
  //    int stoneY = scaledMarginHeight + squareHeight * y;
  //    g.setColor(Color.magenta);
  //    drawCircle3(g, stoneX, stoneY, stoneRadius * 7 / 6);
  //    g.dispose();
  //  }

  /** Draw the stones. We cache the image for a performance boost. */
  public void drawStones() {
    if (Lizzie.board == null) return;
    // draw a new image if frame size changes or board state changes
    if (cachedStonesImage.getWidth() != boardWidth
        || cachedStonesImage.getHeight() != boardHeight
        || cachedDisplayedBranchLength != displayedBranchLength
        || cachedBackgroundImageHasCoordinatesEnabled != showCoordinates()
        || !cachedZhash.equals(Lizzie.board.getData().zobrist)) {

      cachedStonesImage = new BufferedImage(boardWidth, boardHeight, TYPE_INT_ARGB);
      //  cachedStonesShadowImage = new BufferedImage(boardWidth, boardHeight, TYPE_INT_ARGB);
      Graphics2D g = cachedStonesImage.createGraphics();
      g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
      // Graphics2D gShadow = cachedStonesShadowImage.createGraphics();
      // gShadow.setRenderingHint(RenderingHints.KEY_RENDERING,
      // RenderingHints.VALUE_RENDER_QUALITY);

      // we need antialiasing to make the stones pretty. Java is a bit slow at
      // antialiasing; that's
      // why we want the cache
      g.setRenderingHint(KEY_ANTIALIASING, VALUE_ANTIALIAS_ON);
      // gShadow.setRenderingHint(KEY_ANTIALIASING, VALUE_ANTIALIAS_ON);

      for (int i = 0; i < Board.boardWidth; i++) {
        for (int j = 0; j < Board.boardHeight; j++) {
          int stoneX = scaledMarginWidth + squareWidth * i;
          int stoneY = scaledMarginHeight + squareHeight * j;
          drawStone(g, stoneX, stoneY, (Lizzie.board.getStones())[Board.getIndex(i, j)], i, j);
        }
      }

      cachedZhash = Lizzie.board.getData().zobrist.clone();
      cachedDisplayedBranchLength = displayedBranchLength;
      cachedBackgroundImageHasCoordinatesEnabled = showCoordinates();
      g.dispose();
      // gShadow.dispose();
    }
  }

  /*
   * Draw a white/black dot on territory and captured stones. Dame is drawn as red
   * dot.
   */
  //  private void drawScore(Graphics2D go) {
  //    Graphics2D g = cachedStonesImage.createGraphics();
  //    g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
  //    Stone scorestones[] = Lizzie.board.scoreStones();
  //    int scoreRadius = stoneRadius / 4;
  //    for (int i = 0; i < Board.boardWidth; i++) {
  //      for (int j = 0; j < Board.boardHeight; j++) {
  //        int stoneX = scaledMarginWidth + squareWidth * i;
  //        int stoneY = scaledMarginHeight + squareHeight * j;
  //        switch (scorestones[Board.getIndex(i, j)]) {
  //          case WHITE_POINT:
  //          case BLACK_CAPTURED:
  //            g.setColor(Color.white);
  //            fillCircle(g, stoneX, stoneY, scoreRadius);
  //            break;
  //          case BLACK_POINT:
  //          case WHITE_CAPTURED:
  //            g.setColor(Color.black);
  //            fillCircle(g, stoneX, stoneY, scoreRadius);
  //            break;
  //          case DAME:
  //            g.setColor(Color.red);
  //            fillCircle(g, stoneX, stoneY, scoreRadius);
  //            break;
  //        }
  //      }
  //    }
  //    g.dispose();
  //  }

  //  private Optional<MoveData> getBestMove2() {
  //    if (!bestMoves.isEmpty()) {
  //      if (bestMoves.size() < this.bestmovesNum + 1) bestmovesNum = bestMoves.size() - 1;
  //      return Optional.of(bestMoves.get(bestmovesNum));
  //    }
  //    return Optional.empty();
  //  }

  /** Draw the 'ghost stones' which show a variationOpt Leelaz is thinking about */
  private void drawBranch() {
    showingBranch = false;
    BufferedImage newImage = new BufferedImage(boardWidth, boardHeight, TYPE_INT_ARGB);
    // branchStonesImage = new BufferedImage(boardWidth, boardHeight, TYPE_INT_ARGB);
    // branchStonesShadowImage = new BufferedImage(boardWidth, boardHeight, TYPE_INT_ARGB);
    branchOpt = Optional.empty();

    //    if (Lizzie.frame.isPlayingAgainstLeelaz) {
    //      branchStonesImage = newImage;
    //      return;
    //    }

    // Leela Zero isn't connected yet
    if (Lizzie.leelaz == null) {
      branchStonesImage = newImage;
      return;
    }
    // calculate best moves and branch
    //  if (Lizzie.frame.toolbar.isEnginePk && Lizzie.frame.toolbar.isGenmove) {
    // reverseBestmoves = false;
    if (!isMouseOver) {
      BoardHistoryNode bestMoveNode;
      if (!Lizzie.board.getHistory().getCurrentHistoryNode().getData().bestMoves.isEmpty()) {
        bestMoveNode = Lizzie.board.getHistory().getCurrentHistoryNode();
        bestMoves = bestMoveNode.getData().bestMoves;
        estimateArray = bestMoveNode.getData().estimateArray;
        variationBlackToPlay = bestMoveNode.getData().blackToPlay;
      } else {
        bestMoves = new ArrayList<MoveData>();
        estimateArray = null;
      }
      if (Lizzie.config.showKataGoEstimate
          && estimateArray == null
          && Lizzie.board.getHistory().getCurrentHistoryNode().previous().isPresent()) {
        preEstimateArray =
            Lizzie.board
                .getHistory()
                .getCurrentHistoryNode()
                .previous()
                .get()
                .getData()
                .estimateArray;
      } else preEstimateArray = null;
    }

    variationOpt = Optional.empty();

    Graphics2D g = (Graphics2D) newImage.getGraphics();
    g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
    //  Graphics2D gShadow = (Graphics2D) branchStonesShadowImage.getGraphics();
    //   gShadow.setRenderingHint(RenderingHints.KEY_RENDERING,
    // RenderingHints.VALUE_RENDER_QUALITY);

    Optional<MoveData> suggestedMove = getBestMove();

    if (!suggestedMove.isPresent()) {
      g.setColor(new Color(0, 0, 0, 255));
      g.setFont(new Font(Config.sysDefaultFontName, Font.BOLD, stoneRadius * 3 / 2));
      g.drawString(
          "" + (this.bestmovesNum + 1),
          boardWidth - stoneRadius * 9 / 5,
          boardHeight - stoneRadius * 1 / 5);

      branchStonesImage = newImage;
      return;
    }

    if (!isMouseOver || (statChanged && !wheeled)) {
      if (!isMouseOver) stonesTemp = Lizzie.board.getData().stones;
      if (suggestedMove.isPresent()) variation = suggestedMove.get().variation;
      //  variationBlackToPlay =
      //       Lizzie.board.getHistory().getCurrentHistoryNode().getData().blackToPlay;
      if (statChanged) {
        setDisplayedBranchLength(-2);
        statChanged = false;
      }
    }
    if (variation == null) {
      return;
    }
    //  if (!wheeled) oldBlackToPlay = Lizzie.board.getData().blackToPlay;
    // branch = null;
    branch =
        new Branch(
            Lizzie.board,
            variation,
            null,
            this.displayedBranchLength > 0 ? displayedBranchLength : 199,
            true,
            variationBlackToPlay,
            stonesTemp);
    //    mouseOverCoords = suggestedMove.get().coordinate;
    branchOpt = Optional.of(branch);
    variationOpt = Optional.of(variation);

    //
    //    List<String> variation = suggestedMove.get().variation;
    //    Branch branch = null;
    //    if (Lizzie.frame.toolbar.isEnginePk && Lizzie.frame.toolbar.isGenmove)
    //      branch =
    //          new Branch(
    //              Lizzie.board,
    //              variation,
    //              true,
    //              this.displayedBranchLength > 0 ? displayedBranchLength : 199);
    //    else
    //      branch =
    //          new Branch(
    //              Lizzie.board,
    //              variation,
    //              reverseBestmoves,
    //              this.displayedBranchLength > 0 ? displayedBranchLength : 199);
    //    branchOpt = Optional.of(branch);
    //    variationOpt = Optional.of(variation);
    showingBranch = true;

    g.setRenderingHint(KEY_ANTIALIASING, VALUE_ANTIALIAS_ON);

    for (int i = 0; i < Board.boardWidth; i++) {
      for (int j = 0; j < Board.boardHeight; j++) {
        // Display latest stone for ghost dead stone
        int index = Board.getIndex(i, j);
        Stone stone = branch.data.stones[index];
        if (stonesTemp != null && stonesTemp[index] != Stone.EMPTY) {
          if (stone == Stone.BLACK_CAPTURED || stone == Stone.WHITE_CAPTURED) {
            int stoneX = scaledMarginWidth + squareWidth * i;
            int stoneY = scaledMarginHeight + squareHeight * j;
            g.setPaint(paint);
            fillCircle(g, stoneX, stoneY, stoneRadius + 1);
            g.setColor(Color.BLACK);
            g.setStroke(new BasicStroke(1));
            g.drawLine(
                stoneX - (i == 0 ? 0 : (stoneRadius + 1)),
                stoneY,
                stoneX + (i == Board.boardWidth - 1 ? 0 : (stoneRadius + 1)),
                stoneY);
            g.drawLine(
                stoneX,
                stoneY - (j == 0 ? 0 : (stoneRadius + 1)),
                stoneX,
                stoneY + (j == Board.boardHeight - 1 ? 0 : (stoneRadius + 1)));
          }
          continue;
        }
        if (branch.data.moveNumberList[index] > maxBranchMoves()) continue;

        int stoneX = scaledMarginWidth + squareWidth * i;
        int stoneY = scaledMarginHeight + squareHeight * j;

        drawStone(g, stoneX, stoneY, stone.unGhosted(), i, j);
        // if (i == Lizzie.frame.suggestionclick[0] && j ==
        // Lizzie.frame.suggestionclick[1])
        // {
        // Optional<int[]> coords1 =
        // Board.asCoordinates(suggestedMove.get().coordinate);
        // if (coords1.isPresent()
        // && coords1.get()[0] == Lizzie.frame.suggestionclick[0]
        // && coords1.get()[1] == Lizzie.frame.suggestionclick[1]) {
        // g.setColor(Color.magenta);
        // drawCircle3(g, stoneX, stoneY, stoneRadius - 1);
        // }
        // }
      }
      g = (Graphics2D) newImage.getGraphics();
      g.setColor(new Color(0, 0, 0, 255));
      // g.setFont(new Font("幼圆", Font.BOLD, stoneRadius * 5 / 4));
      //  g.drawString("变化", boardWidth - stoneRadius * 14 / 3, boardWidth - stoneRadius * 2 / 7);
      g.setFont(new Font(Config.sysDefaultFontName, Font.BOLD, stoneRadius * 3 / 2));
      g.drawString(
          "" + (this.bestmovesNum + 1),
          boardWidth - stoneRadius * 9 / 5,
          boardHeight - stoneRadius * 1 / 5);
    }
    branchStonesImage = newImage;
    g.dispose();
    //   gShadow.dispose();
  }

  private Optional<MoveData> getBestMove() {
    if (!bestMoves.isEmpty()) {
      if (bestMoves.size() < subOrder + 1) return Optional.empty();
      if (bestMoves.size() < this.bestmovesNum + 1) bestmovesNum = bestMoves.size() - 1;
      return Optional.of(bestMoves.get(bestmovesNum));
    }
    return Optional.empty();
  }

  /** Render the shadows and stones in correct background-foreground order */
  private void renderImages(Graphics2D g) {
    g.setRenderingHint(KEY_ANTIALIASING, VALUE_ANTIALIAS_OFF);
    if ((Lizzie.config.showKataGoEstimate
            && Lizzie.config.showKataGoEstimateOnSubbord
            && Lizzie.config.showKataGoEstimateOnSubbord)
        || Lizzie.frame.isShowingHeatmap)
      if (shouldShowCountBlockBelow()) g.drawImage(kataEstimateImage, x, y, null);
    g.drawImage(cachedStonesImage, x, y, null);
    // g.drawImage(cachedStonesShadowImage, x, y, null);
    // g.drawImage(cachedStonesShadowImagedraged, x, y, null);
    //    if (Lizzie.config.showBranchNow()) {
    //      g.drawImage(branchStonesShadowImage, x, y, null);
    //    }

    // g.drawImage(cachedStonesImagedraged, x, y, null);
    g.drawImage(blockimage, x, y, null);
    if ((Lizzie.config.showKataGoEstimate
            && Lizzie.config.showKataGoEstimateOnSubbord
            && Lizzie.config.showKataGoEstimateOnSubbord)
        || Lizzie.frame.isShowingHeatmap)
      if (!shouldShowCountBlockBelow()) g.drawImage(kataEstimateImage, x, y, null);
    g.drawImage(heatimage, x, y, null);
    if (!Lizzie.frame.isInPlayMode() && !Lizzie.config.subBoardRaw && Lizzie.config.showBranchNow()
        || (Lizzie.config.isFourSubMode() && !showHeat && this != Lizzie.frame.subBoardRenderer4)) {
      g.drawImage(branchStonesImage, x, y, null);
    }
  }

  /** Draw move numbers and/or mark the last played move */
  private void drawMoveNumbers(Graphics2D g) {
    g.setRenderingHint(KEY_ANTIALIASING, VALUE_ANTIALIAS_ON);
    Board board = Lizzie.board;
    Optional<int[]> lastMoveOpt = branchOpt.map(b -> b.data.lastMove).orElse(board.getLastMove());

    if (!lastMoveOpt.isPresent() && board.getData().moveNumber != 0 && !board.getData().dummy) {
      g.setColor(
          board.getData().blackToPlay ? new Color(255, 255, 255, 150) : new Color(0, 0, 0, 150));
      g.fillOval(
          x + boardWidth / 2 - 4 * stoneRadius,
          y + boardHeight / 2 - 4 * stoneRadius,
          stoneRadius * 8,
          stoneRadius * 8);
      g.setColor(
          board.getData().blackToPlay ? new Color(0, 0, 0, 255) : new Color(255, 255, 255, 255));
      drawString(
          g,
          x + boardWidth / 2,
          y + boardHeight / 2,
          LizzieFrame.uiFont,
          Lizzie.resourceBundle.getString("BoardRenderer.pass"),
          stoneRadius * 4,
          stoneRadius * 6);
    }
    //    if (Lizzie.config.allowMoveNumber == 0 && !branchOpt.isPresent()) {
    //      if (lastMoveOpt.isPresent()) {
    //        int[] lastMove = lastMoveOpt.get();
    //
    //        // Mark the last coordinate
    //        int lastMoveMarkerRadius = stoneRadius / 2;
    //        int stoneX = x + scaledMarginWidth + squareWidth * lastMove[0];
    //        int stoneY = y + scaledMarginHeight + squareHeight * lastMove[1];
    //
    //        // Set color to the opposite color of whatever is on the board
    //        boolean isWhite = board.getStones()[Board.getIndex(lastMove[0],
    // lastMove[1])].isWhite();
    //        // g.setColor(Lizzie.board.getData().blackToPlay ? Color.BLACK : Color.WHITE);
    //        g.setColor(Color.red);
    //
    //        if (Lizzie.config.solidStoneIndicator) {
    //          // Use a solid circle instead of
    //          fillCircle(g, stoneX, stoneY, (int) (lastMoveMarkerRadius * 0.65));
    //        } else {
    //          // fillCircle(g, stoneX, stoneY, (int) (lastMoveMarkerRadius * 0.70));
    //          drawCircle2(g, stoneX, stoneY, lastMoveMarkerRadius);
    //          // 需要恢复的
    //        }
    //      }
    //
    //      return;
    //    }
    if (!branchOpt.isPresent()) return;
    int[] moveNumberList =
        branchOpt.map(b -> b.data.moveNumberList).orElse(board.getMoveNumberList());

    // Allow to display only last move number
    int lastMoveNumber =
        branchOpt
            .map(b -> b.data.moveNumber)
            .orElse(Arrays.stream(moveNumberList).max().getAsInt());

    for (int i = 0; i < Board.boardWidth; i++) {
      for (int j = 0; j < Board.boardHeight; j++) {
        int stoneX = x + scaledMarginWidth + squareWidth * i;
        int stoneY = y + scaledMarginHeight + squareHeight * j;
        int here = Board.getIndex(i, j);

        // Allow to display only last move number
        if (Lizzie.config.allowMoveNumber > -1
            && lastMoveNumber - moveNumberList[here] >= Lizzie.config.allowMoveNumber) {
          continue;
        }

        Stone stoneHere = branchOpt.map(b -> b.data.stones[here]).orElse(board.getStones()[here]);

        // don't write the move number if either: the move number is 0, or there will
        // already be
        // playout information written
        if (moveNumberList[Board.getIndex(i, j)] > 0) {
          boolean reverse = (moveNumberList[Board.getIndex(i, j)] > maxBranchMoves());

          if (displayedBranchLength > 0
              && (lastMoveOpt.isPresent()
                  && lastMoveOpt.get()[0] == i
                  && lastMoveOpt.get()[1] == j)) {
            if (reverse) continue;
            g.setColor(Color.RED);
          } else {
            if (reverse) continue;
            g.setColor(stoneHere.isBlack() ^ reverse ? Color.WHITE : Color.BLACK);
            if (displayedBranchLength <= 0) {
              Optional<MoveData> suggestedMove = getBestMove();
              if (suggestedMove.isPresent()) {
                int[] coords = Board.convertNameToCoordinates(suggestedMove.get().coordinate);
                if (coords[0] == i && coords[1] == j) g.setColor(Color.RED);
              }
            }
          }
          String moveNumberString = String.valueOf(moveNumberList[Board.getIndex(i, j)]);

          drawString(
              g,
              stoneX,
              //              moveNumberList[Board.getIndex(i, j)] > 9 && stoneRadius <= 12
              //                  ? stoneY + stoneRadius / 10:
              stoneY,
              LizzieFrame.uiFont,
              moveNumberString,
              (float) (stoneRadius * 1.8),
              (float) (stoneRadius * 1.7));
        }
      }
    }
  }

  /**
   * Draw all of Leelaz's suggestions as colored stones with winrate/playout statistics overlayed
   */
  private void drawLeelazSuggestions() {

    heatimage = new BufferedImage(boardWidth, boardHeight, TYPE_INT_ARGB);
    Graphics2D g = heatimage.createGraphics();

    bestMoves = Lizzie.board.getHistory().getData().bestMoves;
    if (bestMoves != null && !bestMoves.isEmpty()) {

      // Collections.sort(bestMoves);

      int maxPlayouts = 0;
      double maxPolicy = 0;
      //        double maxWinrate = 0;
      //        double minWinrate = 100.0;
      List<MoveData> tempbest1 = new ArrayList<MoveData>();
      List<MoveData> tempbest2 = new ArrayList<MoveData>();
      for (int i = 0; i < bestMoves.size(); i++) {
        tempbest1.add(bestMoves.get(i)); // 开始复制一个list的内容到另外一个list
        tempbest2.add(bestMoves.get(i));
      }
      Collections.sort(
          tempbest1,
          new Comparator<MoveData>() {

            @Override
            public int compare(MoveData s1, MoveData s2) {
              // 降序
              if (s1.lcb < s2.lcb) return 1;
              if (s1.lcb > s2.lcb) return -1;
              else return 0;
            }
          });

      Collections.sort(
          tempbest2,
          new Comparator<MoveData>() {

            @Override
            public int compare(MoveData s1, MoveData s2) {
              // 降序
              if (s1.playouts < s2.playouts) return 1;
              if (s1.playouts > s2.playouts) return -1;
              else return 0;
            }
          });
      if (Lizzie.config.leelaversion >= 17 && Lizzie.config.showlcbcolor) {
        for (int i = 0; i < tempbest1.size(); i++) {
          tempbest1.get(i).playouts = tempbest2.get(i).playouts;
        }
      }
      for (MoveData move : bestMoves) {
        if (move.playouts > maxPlayouts) maxPlayouts = move.playouts;

        if (move.policy > maxPolicy) maxPolicy = move.policy;
        //          if (move.winrate < minWinrate) minWinrate = move.winrate;
      }

      for (int i = 0; i < Board.boardWidth; i++) {
        for (int j = 0; j < Board.boardHeight; j++) {
          Optional<MoveData> moveOpt = Optional.empty();

          // This is inefficient but it looks better with shadows

          for (MoveData m : tempbest1) {
            Optional<int[]> coord = Board.asCoordinates(m.coordinate);
            if (coord.isPresent()) {
              int[] c = coord.get();
              if (c[0] == i && c[1] == j) {
                moveOpt = Optional.of(m);
                break;
              }
            }
          }

          if (!moveOpt.isPresent()) {
            continue;
          }
          MoveData move = moveOpt.get();

          Optional<int[]> coordsOpt = Board.asCoordinates(move.coordinate);
          if (!coordsOpt.isPresent()) {
            continue;
          }
          int[] coords = coordsOpt.get();

          int suggestionX = scaledMarginWidth + squareWidth * coords[0];
          int suggestionY = scaledMarginHeight + squareHeight * coords[1];

          int length = 0;
          if (showHeatAfterCalc)
            length = (int) ((move.playouts * 2.7f * squareWidth) / maxPlayouts);
          else length = (int) ((move.policy * 2.7f * squareWidth) / maxPolicy);
          drawHeat(g, suggestionX - length / 2, suggestionY - length / 2, length);
        }
      }
    }
    g.dispose();
    //   }
  }

  private void drawWoodenBoard(Graphics2D g) {
    if (Lizzie.config.usePureBoard) {
      // simple version
      g.setRenderingHint(KEY_ANTIALIASING, VALUE_ANTIALIAS_OFF);
      g.setColor(Lizzie.config.pureBoardColor);
      g.fillRect(0, 0, boardWidth, boardHeight);
    } else {
      // fancy version
      if (cachedBoardImage == emptyImage) {
        cachedBoardImage = Lizzie.config.theme.board();
      }
      drawTextureImage(g, cachedBoardImage, 0, 0, boardWidth, boardHeight);
      g.setStroke(new BasicStroke(1));
    }
  }

  /**
   * Calculates the lengths and pixel margins from a given boardLength.
   *
   * @param boardLength go board's length in pixels; must be boardLength >= BOARD_SIZE - 1
   * @return an array containing the three outputs: new boardLength, scaledMargin, availableLength
   */
  private static int[] calculatePixelMargins(
      int boardWidth, int boardHeight, boolean showCoordinates) {
    // boardLength -= boardLength*MARGIN/3; // account for the shadows we will draw
    // around the edge
    // of the board
    // if (boardLength < Board.BOARD_SIZE - 1)
    // throw new IllegalArgumentException("boardLength may not be less than " +
    // (Board.BOARD_SIZE - 1) + ", but was " + boardLength);

    int scaledMarginWidth;
    int availableWidth;
    int scaledMarginHeight;
    int availableHeight;
    if (Board.boardWidth == Board.boardHeight) {
      boardWidth = min(boardWidth, boardHeight);
    }

    // decrease boardLength until the availableLength will result in square board
    // intersections
    double marginWidth = 0.035 / Board.boardWidth * 19.0;
    boardWidth++;
    do {
      boardWidth--;
      scaledMarginWidth = (int) (marginWidth * boardWidth);
      availableWidth = boardWidth - 2 * scaledMarginWidth;
    } while (!((availableWidth - 1) % (Board.boardWidth - 1) == 0));
    // this will be true if BOARD_SIZE - 1 square intersections, plus one line, will
    // fit
    int squareWidth = 0;
    int squareHeight = 0;
    if (Board.boardWidth != Board.boardHeight) {

      double marginHeight = 0.035 / Board.boardHeight * 19.0;
      boardHeight++;
      do {
        boardHeight--;
        scaledMarginHeight = (int) (marginHeight * boardHeight);
        availableHeight = boardHeight - 2 * scaledMarginHeight;
      } while (!((availableHeight - 1) % (Board.boardHeight - 1) == 0));
      squareWidth = calculateSquareWidth(availableWidth);
      squareHeight = calculateSquareHeight(availableHeight);
      if (squareWidth > squareHeight) {
        squareWidth = squareHeight;
        int newWidth = squareWidth * (Board.boardWidth - 1) + 1;
        int diff = availableWidth - newWidth;
        availableWidth = newWidth;
        boardWidth -= diff + (scaledMarginWidth - scaledMarginHeight) * 2;
        scaledMarginWidth = scaledMarginHeight;
      } else if (squareWidth < squareHeight) {
        squareHeight = squareWidth;
        int newHeight = squareHeight * (Board.boardHeight - 1) + 1;
        int diff = availableHeight - newHeight;
        availableHeight = newHeight;
        boardHeight -= diff + (scaledMarginHeight - scaledMarginWidth) * 2;
        scaledMarginHeight = scaledMarginWidth;
      }
    } else {
      boardHeight = boardWidth;
      scaledMarginHeight = scaledMarginWidth;
      availableHeight = availableWidth;
    }
    return new int[] {
      boardWidth,
      scaledMarginWidth,
      availableWidth,
      boardHeight,
      scaledMarginHeight,
      availableHeight
    };
  }

  /** Draws a stone centered at (centerX, centerY) */
  private void drawStone(Graphics2D g, int centerX, int centerY, Stone color, int x, int y) {
    // g.setRenderingHint(KEY_ALPHA_INTERPOLATION,
    // VALUE_ALPHA_INTERPOLATION_QUALITY);
    g.setRenderingHint(KEY_INTERPOLATION, VALUE_INTERPOLATION_BILINEAR);
    g.setRenderingHint(KEY_ANTIALIASING, VALUE_ANTIALIAS_ON);

    if (color.isBlack() || color.isWhite()) {
      boolean isBlack = color.isBlack();
      // if (uiConfig.getBoolean("fancy-stones")) {
      // 需要恢复的
      g.setColor(isBlack ? Color.BLACK : Color.WHITE);
      fillCircle(g, centerX, centerY, stoneRadius);
      if (!isBlack) {
        g.setColor(Color.BLACK);
        drawCircle(g, centerX, centerY, stoneRadius);
      }
    }
  }

  private void drawHeat(Graphics2D g, int x, int y, int length) {
    // g.setRenderingHint(KEY_ALPHA_INTERPOLATION,
    // VALUE_ALPHA_INTERPOLATION_QUALITY);
    // g.setRenderingHint(KEY_INTERPOLATION, VALUE_INTERPOLATION_BILINEAR);
    // g.setRenderingHint(KEY_ANTIALIASING, VALUE_ANTIALIAS_ON);

    // if (uiConfig.getBoolean("fancy-stones")) {
    // 需要恢复的

    // drawShadow(gShadow, centerX, centerY, isGhost);
    g.drawImage(getHeatStone(squareWidth * 3), x, y, length, length, null);
  }

  private BufferedImage getHeatStone(int size) {
    BufferedImage stoneImage = cachedHeatImage;
    if (stoneImage.getWidth() != size || stoneImage.getHeight() != size) {
      stoneImage = new BufferedImage(size, size, TYPE_INT_ARGB);
      Image img = null;
      try {
        img = ImageIO.read(getClass().getResourceAsStream("/assets/heatmap.png"));
      } catch (IOException e2) {
        e2.printStackTrace();
      }
      Graphics2D g2 = stoneImage.createGraphics();
      g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
      g2.drawImage(img.getScaledInstance(size, size, java.awt.Image.SCALE_SMOOTH), 0, 0, null);
      g2.dispose();
      cachedHeatImage = stoneImage;
    }
    return stoneImage;
  }

  public BufferedImage getWallpaper() {
    if (cachedWallpaperImage == emptyImage) {
      cachedWallpaperImage = Lizzie.config.theme.background();
    }
    return cachedWallpaperImage;
  }

  /** Draw texture image */
  public void drawTextureImage(
      Graphics2D g, BufferedImage img, int x, int y, int width, int height) {
    paint = new TexturePaint(img, new Rectangle(0, 0, img.getWidth(), img.getHeight()));
    g.setPaint(paint);
    g.fill(new Rectangle(x, y, width, height));
  }

  /** Fills in a circle centered at (centerX, centerY) with radius $radius$ */
  private void fillCircle(Graphics2D g, int centerX, int centerY, int radius) {

    g.fillOval(centerX - radius, centerY - radius, 2 * radius + 1, 2 * radius + 1);
  }

  /** Draws the outline of a circle centered at (centerX, centerY) with radius $radius$ */
  private void drawCircle(Graphics2D g, int centerX, int centerY, int radius) {
    // g.setStroke(new BasicStroke(radius / 11.5f));
    g.drawOval(centerX - radius, centerY - radius, 2 * radius, 2 * radius);
  }

  /**
   * Draws a string centered at (x, y) of font $fontString$, whose contents are $string$. The
   * maximum/default fontsize will be $maximumFontHeight$, and the length of the drawn string will
   * be at most maximumFontWidth. The resulting actual size depends on the length of $string$.
   * aboveOrBelow is a param that lets you set: aboveOrBelow = -1 -> y is the top of the string
   * aboveOrBelow = 0 -> y is the vertical center of the string aboveOrBelow = 1 -> y is the bottom
   * of the string
   */
  private void drawString(
      Graphics2D g,
      int x,
      int y,
      Font fontBase,
      int style,
      String string,
      float maximumFontHeight,
      double maximumFontWidth,
      int aboveOrBelow) {

    Font font = makeFont(fontBase, style);

    // set maximum size of font
    FontMetrics fm = g.getFontMetrics(font);
    font = font.deriveFont((float) (font.getSize2D() * maximumFontWidth / fm.stringWidth(string)));
    font = font.deriveFont(min(maximumFontHeight, font.getSize()));
    g.setFont(font);
    fm = g.getFontMetrics(font);
    int height = fm.getAscent() - fm.getDescent();
    int verticalOffset;
    if (aboveOrBelow == -1) {
      verticalOffset = height / 2;
    } else if (aboveOrBelow == 1) {
      verticalOffset = -height / 2;
    } else {
      verticalOffset = 0;
    }

    // bounding box for debugging
    // g.drawRect(x-(int)maximumFontWidth/2, y - height/2 + verticalOffset,
    // (int)maximumFontWidth,
    // height+verticalOffset );
    g.drawString(string, x - fm.stringWidth(string) / 2, y + height / 2 + verticalOffset);
  }

  private void drawString(
      Graphics2D g,
      int x,
      int y,
      Font fontBase,
      String string,
      float maximumFontHeight,
      double maximumFontWidth) {
    drawString(g, x, y, fontBase, Font.PLAIN, string, maximumFontHeight, maximumFontWidth, 0);
  }

  /** @return a font with kerning enabled */
  private Font makeFont(Font fontBase, int style) {
    Font font = fontBase.deriveFont(style, 100);
    Map<TextAttribute, Object> atts = new HashMap<>();
    atts.put(TextAttribute.KERNING, TextAttribute.KERNING_ON);
    return font.deriveFont(atts);
  }

  private int[] calculatePixelMargins() {
    return calculatePixelMargins(boardWidth, boardHeight, showCoordinates());
  }

  /**
   * Set the location to render the board
   *
   * @param x x coordinate
   * @param y y coordinate
   */
  public void setLocation(int x, int y) {
    this.x = x;
    this.y = y;
  }

  public Point getLocation() {
    return new Point(x, y);
  }

  /**
   * Set the maximum boardLength to render the board
   *
   * @param boardLength the boardLength of the board
   */
  public void setBoardLength(int boardWidth, int boardHeight) {
    if (this.boardWidth != boardWidth || this.boardHeight != boardHeight) {
      this.boardWidth = boardWidth;
      this.boardHeight = boardHeight;
      setupSizeParameters();
    }
  }

  /**
   * @return the actual board length, including the shadows drawn at the edge of the wooden board
   */
  public int[] getActualBoardLength() {
    return new int[] {
      (int) (boardWidth * (1 + MARGIN / 3)), (int) (boardHeight * (1 + MARGIN / 3))
    };
  }

  /**
   * Converts a location on the screen to a location on the board
   *
   * @param x x pixel coordinate
   * @param y y pixel coordinate
   * @return if there is a valid coordinate, an array (x, y) where x and y are between 0 and
   *     BOARD_SIZE - 1. Otherwise, returns Optional.empty
   */
  public Optional<int[]> convertScreenToCoordinates(int x, int y) {
    int marginWidth; // the pixel width of the margins
    int marginHeight; // the pixel height of the margins
    // calculate a good set of boardLength, scaledMargin, and
    // boardLengthWithoutMargins to use
    // int[] calculatedPixelMargins = calculatePixelMargins();
    // setBoardLength(calculatedPixelMargins[0], calculatedPixelMargins[3]);
    marginWidth = this.scaledMarginWidth;
    marginHeight = this.scaledMarginHeight;

    // transform the pixel coordinates to board coordinates
    x =
        squareWidth == 0
            ? 0
            : Math.floorDiv(x - this.x - marginWidth + squareWidth / 2, squareWidth);
    y =
        squareHeight == 0
            ? 0
            : Math.floorDiv(y - this.y - marginHeight + squareHeight / 2, squareHeight);

    // return these values if they are valid board coordinates
    return Board.isValid(x, y) ? Optional.of(new int[] {x, y}) : Optional.empty();
  }

  /**
   * Calculate the boardLength of each intersection square
   *
   * @param availableLength the pixel board length of the game board without margins
   * @return the board length of each intersection square
   */
  public void setBoardParam(int[] param) {
    boardWidth = param[0];
    scaledMarginWidth = param[1];
    availableWidth = param[2];
    boardHeight = param[3];
    scaledMarginHeight = param[4];
    availableHeight = param[5];

    squareWidth = calculateSquareWidth(availableWidth);
    squareHeight = calculateSquareHeight(availableHeight);
    stoneRadius = max(squareWidth, squareHeight) < 4 ? 1 : max(squareWidth, squareHeight) / 2 - 1;
  }

  private static int calculateSquareWidth(int availableWidth) {
    return availableWidth / (Board.boardWidth - 1);
  }

  private static int calculateSquareHeight(int availableHeight) {
    return availableHeight / (Board.boardHeight - 1);
  }

  private int maxBranchMoves() {
    switch (displayedBranchLength) {
      case SHOW_NORMAL_BOARD:
        return Integer.MAX_VALUE;
      case SHOW_RAW_BOARD:
        return -1;
      default:
        return displayedBranchLength;
    }
  }

  public boolean isShowingBranch() {
    return showingBranch;
  }

  public void setDisplayedBranchLength(int n) {
    displayedBranchLength = n;
  }

  public int getDisplayedBranchLength() {
    return displayedBranchLength;
  }

  public int getReplayBranch() {
    return variationOpt.isPresent() ? variationOpt.get().size() : 0;
  }

  public boolean incrementDisplayedBranchLength(int n) {
    switch (displayedBranchLength) {
      case SHOW_NORMAL_BOARD:
      case SHOW_RAW_BOARD:
        return false;
      default:
        // force nonnegative
        displayedBranchLength = max(0, displayedBranchLength + n);
        if (variation != null) {
          displayedBranchLength = min(displayedBranchLength, variation.size() + 1);
        } else displayedBranchLength = 0;
        return true;
    }
  }

  public boolean isShowingNormalBoard() {
    return displayedBranchLength == SHOW_NORMAL_BOARD;
  }

  public boolean isInside(int x1, int y1) {
    return x <= x1 && x1 < x + boardWidth && y <= y1 && y1 < y + boardHeight;
  }

  private boolean showCoordinates() {
    return false;
    // return isMainBoard && Lizzie.config.showCoordinates;
  }

  public void increaseMaxAlpha(int k) {
    maxAlpha = min(maxAlpha + k, 255);
    uiPersist.put("max-alpha", maxAlpha);
  }
}
