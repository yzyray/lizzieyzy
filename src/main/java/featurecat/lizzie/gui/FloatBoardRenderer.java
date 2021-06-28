package featurecat.lizzie.gui;

import static java.awt.RenderingHints.*;
import static java.awt.image.BufferedImage.TYPE_INT_ARGB;
import static java.lang.Math.log;
import static java.lang.Math.max;
import static java.lang.Math.min;
import static java.lang.Math.round;

import featurecat.lizzie.Lizzie;
import featurecat.lizzie.analysis.Branch;
import featurecat.lizzie.analysis.Leelaz;
import featurecat.lizzie.analysis.MoveData;
import featurecat.lizzie.rules.Board;
import featurecat.lizzie.rules.BoardData;
import featurecat.lizzie.rules.BoardHistoryNode;
import featurecat.lizzie.rules.SGFParser;
import featurecat.lizzie.rules.Stone;
import featurecat.lizzie.rules.Zobrist;
import featurecat.lizzie.util.Utils;
import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.font.TextAttribute;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.concurrent.CountDownLatch;

public class FloatBoardRenderer {
  // Percentage of the boardLength to offset before drawing black lines
  private static final double MARGIN = 0.03;
  private final ResourceBundle resourceBundle =
      Lizzie.config.useLanguage == 0
          ? ResourceBundle.getBundle("l10n.DisplayStrings")
          : (Lizzie.config.useLanguage == 1
              ? ResourceBundle.getBundle("l10n.DisplayStrings", new Locale("zh", "CN"))
              : ResourceBundle.getBundle("l10n.DisplayStrings", new Locale("en", "US")));
  // private static final double MARGIN_WITH_COORDINATES = 0.06;
  private static final double STARPOINT_DIAMETER = 0.015;
  private static final BufferedImage emptyImage = new BufferedImage(1, 1, TYPE_INT_ARGB);

  public int boardType = -1;
  private int x, y;
  public int boardWidth = 1, boardHeight = 1;
  // private int shadowRadius;
  public boolean emptyName = true;
  public boolean changedName = false;

  private int scaledMarginWidth, availableWidth, squareWidth, stoneRadius;
  private int scaledMarginHeight, availableHeight, squareHeight;
  public Optional<Branch> branchOpt = Optional.empty();
  private List<MoveData> bestMoves = new ArrayList<MoveData>();
  private MoveData mouseOverTemp = new MoveData();
  private BoardHistoryNode mouseOverTempNode;

  private int mouseOverOrder = -1;
  private boolean isMouseOverStoneBlack;

  private boolean isShowingBranch = false;
  private String mouseOverCoords = "";
  private Branch branch;

  private boolean cachedBackgroundImageHasCoordinatesEnabled = false;
  private int cachedBoardWidth = 0, cachedBoardHeight = 0;
  private BufferedImage cachedStonesImage = emptyImage;
  private BufferedImage cachedStonesImagedraged = emptyImage;
  private BufferedImage blockimage = emptyImage;
  private BufferedImage selectImage = emptyImage;
  private BufferedImage unImportantSugg = emptyImage;
  private boolean unImportantCleared = false;
  // private BufferedImage importantSugg = emptyImage;
  private ArrayList<BufferedImage> cachedSelectImage = new ArrayList<BufferedImage>();
  private boolean hasBlockimage = false;
  private BufferedImage kataEstimateImage = emptyImage;
  private BufferedImage estimateImage = emptyImage;

  private BufferedImage cachedBoardImage = emptyImage;
  private BufferedImage cachedWallpaperImage = emptyImage;
  private BufferedImage cachedStonesShadowImage = emptyImage;
  private BufferedImage cachedStonesShadowImagedraged = emptyImage;
  private Zobrist cachedZhash = new Zobrist(); // defaults to an empty board

  private BufferedImage cachedBlackStoneImage = emptyImage;
  private BufferedImage cachedWhiteStoneImage = emptyImage;

  private BufferedImage branchStonesImage = emptyImage;
  private BufferedImage branchStonesShadowImage;

  // private boolean lastInScoreMode = false;

  public Optional<List<String>> variationOpt;

  // special values of displayedBranchLength
  public static final int SHOW_RAW_BOARD = -1;
  public static final int SHOW_NORMAL_BOARD = -2;

  private int displayedBranchLength = SHOW_NORMAL_BOARD;
  private int cachedDisplayedBranchLength = SHOW_RAW_BOARD;
  public boolean[] hasDrawBackground =
      new boolean[Lizzie.board.boardHeight * Lizzie.board.boardWidth];
  private boolean showingBranch = false;
  private boolean isFancyBoard = true;
  private Color noFancyColor;
  // private boolean isMainBoard = false;
  // public boolean reverseBestmoves = false;
  private int maxAlpha = 240;
  // int number = 1;
  TexturePaint paint;
  List<String> variation;
  List<String> cachedVariation = new ArrayList<String>();
  private int cachedDisplayedBranchLengthFroBranch;
  List<String> pvVistis;
  private int drawUnimportantSuggCount = 100;
  // public int boardIndex = 0;

  public FloatBoardRenderer() {}

  /** Draw a go board */
  public void draw(Graphics2D g) {
    if (paint == null) {
      gainPaint();
    }
    if (Lizzie.frame.isShowingHeatmap
        && !Lizzie.frame.isAnaPlayingAgainstLeelaz
        && !Lizzie.leelaz.isZen) drawRawWinrate(g);
    else if (Lizzie.frame.isShowingPolicy && !Lizzie.leelaz.isKatago && !Lizzie.leelaz.isZen)
      drawRawWinrate(g);
    else if (Lizzie.config.showNameInBoard) drawName(g);

    drawBranch();
    renderImages(g);
    if (isShowingBranch) drawMoveNumbers(g);
    g.setRenderingHint(KEY_ANTIALIASING, VALUE_ANTIALIAS_ON);
    if (!isShowingRawBoard()) {
      if (Lizzie.config.showBestMovesNow()) {
        if ((Lizzie.board.getHistory().isBlacksTurn()
                && Lizzie.frame.toolbar.chkShowBlack.isSelected())
            || (!Lizzie.board.getHistory().isBlacksTurn()
                && Lizzie.frame.toolbar.chkShowWhite.isSelected())) {
          if (!Lizzie.frame.isShowingHeatmap && !Lizzie.frame.isShowingPolicy) {
            drawUnimportantSuggCount = drawUnimportantSuggCount + 1;
            if (drawUnimportantSuggCount > 100 / getInterval()) {
              drawLeelazSuggestionsUnimportant();
              drawUnimportantSuggCount = 0;
            }
            renderImagesUnimportant(g);
          }
          drawLeelazSuggestions(g);
        }
      }
    }
  }

  private void gainPaint() {
    cachedBoardImage = Lizzie.config.theme.board();
    paint =
        new TexturePaint(
            cachedBoardImage,
            new Rectangle(0, 0, cachedBoardImage.getWidth(), cachedBoardImage.getHeight()));
  }

  private int getInterval() {
    if (Lizzie.leelaz != null) return Lizzie.leelaz.getInterval();
    else return Lizzie.config.analyzeUpdateIntervalCentisec;
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
  public int[] availableLength(int boardWidth, int boardHeight, boolean showCoordinates) {
    int[] calculatedPixelMargins = calculatePixelMargins(boardWidth, boardHeight);
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

  private void drawRawWinrate(Graphics2D g0) {
    g0.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    // Font font = new Font(Lizzie.config.fontName, Font.PLAIN, (int) (Math.min(28,
    // this.scaledMarginHeight*63/100)));
    //   g0.setFont(font);
    String wr = "";
    if (Lizzie.leelaz.isKatago)
      wr =
          resourceBundle.getString("BoardRenderer.pureNetWhiteWinrate") // "纯网络:白胜率 "
              + String.format("%.1f", Lizzie.leelaz.heatwinrate * 100)
              + " "
              + resourceBundle.getString("BoardRenderer.whiteScore") // " 白目差 "
              + String.format("%.1f", Lizzie.leelaz.heatScore)
              + " "
              + resourceBundle.getString("BoardRenderer.symmetry") // " 对称类型 "
              + Lizzie.leelaz.symmetry;
    else if (Lizzie.leelaz.heatwinrate >= 0) {
      wr =
          resourceBundle.getString("BoardRenderer.pureNetWinrate")
              + String.format("%.1f", Lizzie.leelaz.heatwinrate * 100);
    } else {
      wr = resourceBundle.getString("BoardRenderer.noPureNetWinrate");
    }
    // "未计算胜率:"
    if (emptyName) {
      emptyName = false;
      changedName = true;
      Lizzie.frame.refresh();
    }
    emptyName = false;

    // int lengthWr = g0.getFontMetrics().stringWidth(wr);
    g0.setColor(Color.BLACK);
    //  String regex = "[\u4e00-\u9fa5]";
    drawStringBold(
        g0,
        x + boardWidth / 2,
        y - scaledMarginHeight * 13 / 40 + boardHeight,
        new Font(Lizzie.config.fontName, Font.PLAIN, 16),
        wr,
        scaledMarginHeight / 2,
        boardWidth);
  }

  private void drawName(Graphics2D g0) {
    g0.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    Font font =
        new Font(
            "Microsoft YaHei",
            Font.PLAIN,
            (int) (Math.min(28, this.scaledMarginHeight * 53 / 100)));
    g0.setFont(font);
    String black = Lizzie.board.getHistory().getGameInfo().getPlayerBlack();
    String white = Lizzie.board.getHistory().getGameInfo().getPlayerWhite();

    int lengthB = g0.getFontMetrics().stringWidth(black);
    int height = g0.getFontMetrics().getHeight();
    int lengthW = g0.getFontMetrics().stringWidth(white);
    while (lengthB > squareWidth * (Board.boardWidth * 0.43)
        || lengthW > squareWidth * (Board.boardWidth * 0.43)) {
      if (lengthB > squareWidth * (Board.boardWidth * 0.43))
        black =
            black.substring(
                0,
                black.length()
                    - (Math.max(
                        1,
                        (lengthB - squareWidth * (Board.boardWidth / 2))
                            / (lengthB / black.length()))));
      if (lengthW > squareWidth * (Board.boardWidth * 0.43))
        white =
            white.substring(
                0,
                white.length()
                    - (Math.max(
                        1,
                        (lengthW - squareWidth * (Board.boardWidth / 2))
                            / (lengthW / white.length()))));
      lengthB = g0.getFontMetrics().stringWidth(black);
      lengthW = g0.getFontMetrics().stringWidth(white);
    }
    if (Lizzie.engineManager.isEngineGame && Lizzie.engineManager.engineGameInfo.isBatchGame) {
      if (Lizzie.engineManager.engineGameInfo.firstEngineIndex
          == Lizzie.engineManager.engineGameInfo.blackEngineIndex) {
        black = black + " " + Lizzie.engineManager.engineGameInfo.getFirstEngineWins();
        white = Lizzie.engineManager.engineGameInfo.getSecondEngineWins() + " " + white;
      } else {
        black = black + " " + Lizzie.engineManager.engineGameInfo.getSecondEngineWins();
        white = Lizzie.engineManager.engineGameInfo.getFirstEngineWins() + " " + white;
      }
    }
    lengthB = g0.getFontMetrics().stringWidth(black);
    lengthW = g0.getFontMetrics().stringWidth(white);
    if (black.equals("") && white.contentEquals("")) {
      if (!emptyName) {
        emptyName = true;
        changedName = true;
      }
      return;
    }
    if (emptyName) {
      emptyName = false;
      changedName = true;
      Lizzie.frame.refresh();
    }
    emptyName = false;
    if (Lizzie.board.getHistory().isBlacksTurn()) {
      g0.setColor(Color.WHITE);
      g0.fillOval(
          x + boardWidth / 2 - stoneRadius * 3 / 10,
          y - scaledMarginHeight + stoneRadius + boardHeight,
          stoneRadius * 5 / 4,
          stoneRadius * 5 / 4);

      g0.setColor(Color.BLACK);
      g0.fillOval(
          x + boardWidth / 2 - stoneRadius * 9 / 10,
          y - scaledMarginHeight + stoneRadius + boardHeight,
          stoneRadius * 5 / 4,
          stoneRadius * 5 / 4);
    } else {
      g0.setColor(Color.BLACK);
      g0.fillOval(
          x + boardWidth / 2 - stoneRadius * 9 / 10,
          y - scaledMarginHeight + stoneRadius + boardHeight,
          stoneRadius * 5 / 4,
          stoneRadius * 5 / 4);
      g0.setColor(Color.WHITE);
      g0.fillOval(
          x + boardWidth / 2 - stoneRadius * 3 / 10,
          y - scaledMarginHeight + stoneRadius + boardHeight,
          stoneRadius * 5 / 4,
          stoneRadius * 5 / 4);
    }
    //  g0.setColor(Color.BLACK);

    // set maximum size of font

    //  String regex = "[\u4e00-\u9fa5]";
    //    Font f =
    //        drawStringBoldGetFont(
    //            g0,
    //            x + boardWidth / 2 - lengthB / 2 - stoneRadius * 4 / 3,
    //            y - scaledMarginHeight + boardHeight + stoneRadius * 8 / 5,
    //            new Font(Lizzie.config.uiFontName, Font.PLAIN, 16),
    //            black,
    //            stoneRadius * 5 / 4,
    //            lengthB);

    FontRenderContext frcb = g0.getFontRenderContext();
    if (black.length() > 0) {
      TextLayout tlb = new TextLayout(black, font, frcb);

      float swb = (float) tlb.getBounds().getWidth();
      float shb = (float) tlb.getBounds().getHeight();
      Shape shab =
          tlb.getOutline(
              AffineTransform.getTranslateInstance(
                  x + boardWidth / 2 - lengthB - scaledMarginWidth * 3 / 5,
                  y - scaledMarginHeight + boardHeight + stoneRadius * 13 / 8 + (height + 2) / 4));
      g0.setColor(Color.BLACK);
      g0.fill(shab);
    }

    g0.setColor(Color.WHITE);

    FontRenderContext frc = g0.getFontRenderContext();
    if (white.length() > 0) {
      TextLayout tl = new TextLayout(white, font, frc);

      float sw = (float) tl.getBounds().getWidth();
      float sh = (float) tl.getBounds().getHeight();
      Shape sha =
          tl.getOutline(
              AffineTransform.getTranslateInstance(
                  x + boardWidth / 2 + scaledMarginWidth * 3 / 5 + 1,
                  y - scaledMarginHeight + boardHeight + stoneRadius * 13 / 8 + (height + 2) / 4));
      // double a =sha.getBounds().getHeight();
      g0.setColor(Color.BLACK);
      g0.setStroke(new BasicStroke(2));
      g0.draw(sha);
      g0.setColor(Color.WHITE);
      g0.fill(sha);
    }
  }
  /**
   * Draw the green background and go board with lines. We cache the image for a performance boost.
   */
  //  private void drawGoban(Graphics2D g0) {
  //    int width = boardWidth;
  //    int height = boardHeight;
  //    // Draw the cached background image if frame size changes
  //    if (cachedBackgroundImage.getWidth() != width
  //        || cachedBackgroundImage.getHeight() != height
  //        || cachedBoardWidth != boardWidth
  //        || cachedBoardHeight != boardHeight
  //        // || cachedX != x
  //        // || cachedY != y
  //        || cachedBackgroundImageHasCoordinatesEnabled != showCoordinates()
  //        || changedName
  //        || Lizzie.board.isForceRefresh()) {
  //      clearSuggestionImage();
  //      changedName = false;
  //      cachedBoardWidth = boardWidth;
  //      cachedBoardHeight = boardHeight;
  //      Lizzie.board.setForceRefresh(false);
  //
  //      cachedBackgroundImage = new BufferedImage(width, height, TYPE_INT_ARGB);
  //      Graphics2D g = cachedBackgroundImage.createGraphics();
  //      g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
  //
  //      // Draw the wooden background
  //      drawWoodenBoard(g);
  //      //      g.setColor(new Color(0,0,0,52));
  //      //      g.fillRect(x, y+stoneRadius/5, stoneRadius/5, boardHeight-stoneRadius/5);
  //      //      g.fillRect(x, y, boardWidth-stoneRadius/5,  stoneRadius/5);
  //      //      g.fillRect(x+boardWidth-stoneRadius/5, y, stoneRadius/5,
  // boardHeight-stoneRadius/5);
  //      //      g.fillRect(x+stoneRadius/5, y+boardHeight-stoneRadius/5, boardWidth,
  // stoneRadius/5);
  //      // Draw the lines
  //      g.setColor(Color.BLACK);
  //      for (int i = 0; i < Board.boardHeight; i++) {
  //        // g.setStroke(new BasicStroke(stoneRadius / 15f));
  //        if (i == 0 || i == Board.boardHeight - 1) {
  //          g.setStroke(new BasicStroke(stoneRadius / 10f));
  //          g.drawLine(
  //              scaledMarginWidth,
  //              scaledMarginHeight + squareHeight * i,
  //              scaledMarginWidth + availableWidth - 1,
  //              scaledMarginHeight + squareHeight * i);
  //        }
  //        g.setStroke(new BasicStroke(1f));
  //        g.drawLine(
  //            scaledMarginWidth,
  //            scaledMarginHeight + squareHeight * i,
  //            scaledMarginWidth + availableWidth - 1,
  //            scaledMarginHeight + squareHeight * i);
  //      }
  //      for (int i = 0; i < Board.boardWidth; i++) {
  //        // g.setStroke(new BasicStroke(stoneRadius / 15f));
  //        if (i == 0 || i == Board.boardWidth - 1) {
  //          g.setStroke(new BasicStroke(stoneRadius / 10f));
  //          g.drawLine(
  //              scaledMarginWidth + squareWidth * i,
  //              scaledMarginHeight,
  //              scaledMarginWidth + squareWidth * i,
  //              scaledMarginHeight + availableHeight - 1);
  //        }
  //        g.setStroke(new BasicStroke(1f));
  //        g.drawLine(
  //            scaledMarginWidth + squareWidth * i,
  //            scaledMarginHeight,
  //            scaledMarginWidth + squareWidth * i,
  //            scaledMarginHeight + availableHeight - 1);
  //      }
  //
  //      // Draw the star points
  //      drawStarPoints(g);
  //
  //      // Draw coordinates if enabled
  //      if (showCoordinates()) {
  //        g.setColor(Color.BLACK);
  //        for (int i = 0; i < Board.boardWidth; i++) {
  //          drawString(
  //              g,
  //              scaledMarginWidth + squareWidth * i,
  //              scaledMarginHeight * 4 / 10,
  //              Lizzie.frame.uiFont,
  //              Board.asName(i),
  //              stoneRadius * 4 / 5,
  //              stoneRadius);
  //          if ((!Lizzie.config.showNameInBoard
  //                  || (Lizzie.board != null
  //                      && (Lizzie.board.getHistory().getGameInfo().getPlayerWhite().equals("")
  //                          &&
  // Lizzie.board.getHistory().getGameInfo().getPlayerBlack().equals(""))))
  //              && (!Lizzie.frame.isShowingHeatmap || Lizzie.leelaz.isZen)
  //              && (!Lizzie.frame.isShowingPolicy || Lizzie.leelaz.isKatago ||
  // Lizzie.leelaz.isZen))
  //            drawString(
  //                g,
  //                scaledMarginWidth + squareWidth * i,
  //                -scaledMarginHeight * 4 / 10 + boardHeight,
  //                Lizzie.frame.uiFont,
  //                Board.asName(i),
  //                stoneRadius * 4 / 5,
  //                stoneRadius);
  //        }
  //        for (int i = 0; i < Board.boardHeight; i++) {
  //          drawString(
  //              g,
  //              scaledMarginWidth * 4 / 10,
  //              scaledMarginHeight + squareHeight * i,
  //              Lizzie.frame.uiFont,
  //              "" + (Board.boardHeight <= 25 ? (Board.boardHeight - i) : (i + 1)),
  //              stoneRadius * 4 / 5,
  //              stoneRadius);
  //          drawString(
  //              g,
  //              -scaledMarginWidth * 4 / 10 + boardWidth,
  //              scaledMarginHeight + squareHeight * i,
  //              Lizzie.frame.uiFont,
  //              "" + (Board.boardHeight <= 25 ? (Board.boardHeight - i) : (i + 1)),
  //              stoneRadius * 4 / 5,
  //              stoneRadius);
  //        }
  //      }
  //      g.dispose();
  //    }
  //
  //    g0.setRenderingHint(KEY_ANTIALIASING, VALUE_ANTIALIAS_OFF);
  //    g0.drawImage(cachedBackgroundImage, x, y, null);
  //    // cachedX = x;
  //    //  cachedY = y;
  //  }

  /**
   * Draws the star points on the board, according to board size
   *
   * @param g graphics2d object to draw
   */
  private void drawStarPoints(Graphics2D g) {
    if (Board.boardWidth == 19 && Board.boardHeight == 19) {
      drawStarPoints0(3, 3, 6, false, g);
    } else if (Board.boardWidth == 15 && Board.boardHeight == 15) {
      drawStarPoints0(2, 3, 8, true, g);
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
      int centerX = scaledMarginWidth + squareWidth * (Board.boardWidth / 2);
      int centerY = scaledMarginHeight + squareHeight * (Board.boardHeight / 2);
      fillCircle(g, centerX, centerY, starPointRadius);
    }
  }

  public void removedrawmovestone() {
    cachedStonesImagedraged = new BufferedImage(boardWidth, boardHeight, TYPE_INT_ARGB);
    cachedStonesShadowImagedraged = new BufferedImage(boardWidth, boardHeight, TYPE_INT_ARGB);
  }

  public void drawmovestone(int x, int y, Stone stone) {
    cachedStonesImagedraged = new BufferedImage(boardWidth, boardHeight, TYPE_INT_ARGB);
    cachedStonesShadowImagedraged = new BufferedImage(boardWidth, boardHeight, TYPE_INT_ARGB);
    Graphics2D g = cachedStonesImagedraged.createGraphics();
    Graphics2D gShadow = cachedStonesShadowImagedraged.createGraphics();
    gShadow.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
    g.setRenderingHint(KEY_ANTIALIASING, VALUE_ANTIALIAS_ON);
    int stoneX = scaledMarginWidth + squareWidth * x;
    int stoneY = scaledMarginHeight + squareHeight * y;
    if (Lizzie.config.usePureStone) drawStoneSimple(g, gShadow, stoneX, stoneY, stone, x, y);
    else drawStone(g, gShadow, stoneX, stoneY, stone, x, y);
    g.dispose();
  }

  public void drawSelectedRect(int x1, int y1, int x2, int y2, boolean isAllow) {
    selectImage = new BufferedImage(boardWidth, boardHeight, TYPE_INT_ARGB);
    Graphics2D g = selectImage.createGraphics();
    g.setRenderingHint(KEY_ANTIALIASING, VALUE_ANTIALIAS_ON);
    if (isAllow) g.setColor(new Color(0, 0, 120, 45));
    else g.setColor(new Color(120, 0, 0, 45));
    g.fillRect(min(x1 - x, x2 - x), min(y1 - y, y2 - y), Math.abs(x2 - x1), Math.abs(y2 - y1));
    g.dispose();
  }

  public void addSelectedRect(int x1, int y1, int x2, int y2, boolean isAllow) {
    this.selectImage = new BufferedImage(boardWidth, boardHeight, TYPE_INT_ARGB);
    BufferedImage selectImage = new BufferedImage(boardWidth, boardHeight, TYPE_INT_ARGB);
    Graphics2D g = selectImage.createGraphics();
    g.setRenderingHint(KEY_ANTIALIASING, VALUE_ANTIALIAS_ON);
    if (isAllow) g.setColor(new Color(0, 0, 120, 45));
    else g.setColor(new Color(120, 0, 0, 45));
    int startX = scaledMarginWidth + squareWidth * min(x1, x2) - squareWidth / 2;
    int startY = scaledMarginHeight + squareHeight * min(y1, y2) - squareHeight / 2;
    // int endX = scaledMarginWidth + squareWidth * max(x1,x2);
    // int endY = scaledMarginHeight + squareHeight * max(y1,y2);
    g.fillRect(
        startX,
        startY,
        squareWidth * (Math.abs(x2 - x1) + 1),
        squareHeight * (Math.abs(y2 - y1) + 1));
    cachedSelectImage.add(selectImage);
    g.dispose();
  }

  public void removeSelectedRect() {
    cachedSelectImage = new ArrayList<BufferedImage>();
    selectImage = new BufferedImage(boardWidth, boardHeight, TYPE_INT_ARGB);
  }

  public void removecountblock() {
    kataEstimateImage = new BufferedImage(boardWidth, boardHeight, TYPE_INT_ARGB);
  }

  public void removeEstimateImage() {
    estimateImage = new BufferedImage(boardWidth, boardHeight, TYPE_INT_ARGB);
  }

  public static int roundToInt(double number) {
    return (int) round(number);
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

  public void drawKataEstimateByTransparent(ArrayList<Double> tempcount) {
    BufferedImage newEstimateImage = new BufferedImage(boardWidth, boardHeight, TYPE_INT_ARGB);
    Graphics2D g = newEstimateImage.createGraphics();
    Leelaz leelaz = Lizzie.leelaz;
    for (int i = 0; i < tempcount.size(); i++) {
      if ((tempcount.get(i) > 0 && Lizzie.board.getHistory().isBlacksTurn())
          || (tempcount.get(i) < 0 && !Lizzie.board.getHistory().isBlacksTurn())) {
        int y = i / Lizzie.board.boardWidth;
        int x = i % Lizzie.board.boardWidth;
        int stoneX = scaledMarginWidth + squareWidth * x;
        int stoneY = scaledMarginHeight + squareHeight * y;
        // g.setColor(Color.BLACK);

        int alpha =
            shouldShowCountBlockBig()
                ? (int) (tempcount.get(i) * 105)
                : (int) (tempcount.get(i) * 255);
        Color cl = new Color(0, 0, 0, Math.abs(alpha));
        if (!shouldShowCountBlockBig()
            && Lizzie.board.getHistory().getStones()[Lizzie.board.getIndex(x, y)].isBlack()) {
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
              stoneX - squareWidth * 3 / 10,
              stoneY - squareWidth * 3 / 10,
              squareWidth * 6 / 10,
              squareWidth * 6 / 10);
      }
      if ((tempcount.get(i) < 0 && Lizzie.board.getHistory().isBlacksTurn())
          || (tempcount.get(i) > 0 && !Lizzie.board.getHistory().isBlacksTurn())) {
        int y = i / Lizzie.board.boardWidth;
        int x = i % Lizzie.board.boardWidth;
        int stoneX = scaledMarginWidth + squareWidth * x;
        int stoneY = scaledMarginHeight + squareHeight * y;
        int alpha =
            (Lizzie.config.showKataGoEstimateBigBelow
                    || (leelaz.isKatago
                        && leelaz.iskataHeatmapShowOwner
                        && Lizzie.config.showPureEstimateBigBelow))
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
              stoneX - squareWidth * 3 / 10,
              stoneY - squareWidth * 3 / 10,
              squareWidth * 6 / 10,
              squareWidth * 6 / 10);
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

  public void drawKataEstimateBySize(ArrayList<Double> tempcount) {
    BufferedImage newEstimateImage = new BufferedImage(boardWidth, boardHeight, TYPE_INT_ARGB);
    Graphics2D g = newEstimateImage.createGraphics();
    for (int i = 0; i < tempcount.size(); i++) {
      if ((tempcount.get(i) > 0 && Lizzie.board.getHistory().isBlacksTurn())
          || (tempcount.get(i) < 0 && !Lizzie.board.getHistory().isBlacksTurn())) {
        int y = i / Lizzie.board.boardWidth;
        int x = i % Lizzie.board.boardWidth;
        int stoneX = scaledMarginWidth + squareWidth * x;
        int stoneY = scaledMarginHeight + squareHeight * y;
        Color cl = new Color(0, 0, 0, 180);
        g.setColor(cl);
        int length = (int) (convertLength(tempcount.get(i)) * squareWidth);
        if (length > 0) g.fillRect(stoneX - length / 2, stoneY - length / 2, length, length);
      }
      if ((tempcount.get(i) < 0 && Lizzie.board.getHistory().isBlacksTurn())
          || (tempcount.get(i) > 0 && !Lizzie.board.getHistory().isBlacksTurn())) {
        int y = i / Lizzie.board.boardWidth;
        int x = i % Lizzie.board.boardWidth;
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

  public void drawEstimateImage(ArrayList<Double> tempcount) {
    estimateImage = new BufferedImage(boardWidth, boardHeight, TYPE_INT_ARGB);
    Graphics2D g = estimateImage.createGraphics();
    for (int i = 0; i < tempcount.size(); i++) {
      if (tempcount.get(i) > 0) {
        int y = i / Lizzie.board.boardWidth;
        int x = i % Lizzie.board.boardWidth;
        int stoneX = scaledMarginWidth + squareWidth * x;
        int stoneY = scaledMarginHeight + squareHeight * y;
        g.setColor(Color.BLACK);
        g.fillRect(
            stoneX - squareWidth / 4, stoneY - squareWidth / 4, squareWidth / 2, squareWidth / 2);
      }
      if (tempcount.get(i) < 0) {
        int y = i / Lizzie.board.boardWidth;
        int x = i % Lizzie.board.boardWidth;
        int stoneX = scaledMarginWidth + squareWidth * x;
        int stoneY = scaledMarginHeight + squareHeight * y;
        g.setColor(Color.WHITE);
        g.fillRect(
            stoneX - squareWidth / 4, stoneY - squareWidth / 4, squareWidth / 2, squareWidth / 2);
      }
    }
    g.dispose();
  }

  public void removeblock() {
    if (hasBlockimage) {
      blockimage = new BufferedImage(boardWidth, boardHeight, TYPE_INT_ARGB);
      hasBlockimage = false;
    }
  }

  public void drawmoveblock(int x, int y, boolean isblack) {
    if (boardWidth == 0 || boardHeight == 0) return;
    blockimage = new BufferedImage(boardWidth, boardHeight, TYPE_INT_ARGB);
    Stone[] stones = Lizzie.board.getStones();
    if (stones[Lizzie.board.getIndex(x, y)].isBlack()
        || stones[Lizzie.board.getIndex(x, y)].isWhite()) {
      return;
    }
    Graphics2D g = blockimage.createGraphics();
    int stoneX = scaledMarginWidth + squareWidth * x;
    int stoneY = scaledMarginHeight + squareHeight * y;
    g.setColor(isblack ? Color.BLACK : Color.WHITE);
    g.fillRect(
        stoneX - squareWidth * 23 / 100,
        stoneY - squareWidth * 23 / 100,
        squareWidth * 46 / 100,
        squareWidth * 46 / 100);
    hasBlockimage = true;
    g.dispose();
  }

  /** Draw the stones. We cache the image for a performance boost. */
  public void drawStones() {
    if (Lizzie.board == null) return;

    // draw a new image if frame size changes or board state changes
    if (cachedStonesImage.getWidth() != boardWidth
        || cachedStonesImage.getHeight() != boardHeight
        || cachedDisplayedBranchLength != displayedBranchLength
        || cachedBackgroundImageHasCoordinatesEnabled != showCoordinates()
        || !cachedZhash.equals(Lizzie.board.getData().zobrist)) {
      cachedZhash = Lizzie.board.getData().zobrist.clone();
      cachedStonesImage = new BufferedImage(boardWidth, boardHeight, TYPE_INT_ARGB);
      cachedStonesShadowImage = new BufferedImage(boardWidth, boardHeight, TYPE_INT_ARGB);
      Graphics2D g = cachedStonesImage.createGraphics();
      g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
      Graphics2D gShadow = cachedStonesShadowImage.createGraphics();
      gShadow.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

      // we need antialiasing to make the stones pretty. Java is a bit slow at
      // antialiasing; that's
      // why we want the cache
      g.setRenderingHint(KEY_ANTIALIASING, VALUE_ANTIALIAS_ON);
      gShadow.setRenderingHint(KEY_ANTIALIASING, VALUE_ANTIALIAS_ON);
      drawShadowCache();
      if (Lizzie.config.usePureStone) {
        for (int i = 0; i < Board.boardWidth; i++) {
          for (int j = 0; j < Board.boardHeight; j++) {
            int stoneX = scaledMarginWidth + squareWidth * i;
            int stoneY = scaledMarginHeight + squareHeight * j;
            drawStoneSimple(
                g, gShadow, stoneX, stoneY, Lizzie.board.getStones()[Board.getIndex(i, j)], i, j);
          }
        }
      } else {
        final CountDownLatch latch = new CountDownLatch(Board.boardWidth);
        for (int i = 0; i < Board.boardWidth; i++) {
          final Integer threadI = i;
          new Thread() {
            public void run() {
              for (int j = 0; j < Board.boardHeight; j++) {
                int stoneX = scaledMarginWidth + squareWidth * threadI;
                int stoneY = scaledMarginHeight + squareHeight * j;
                drawStone(
                    g,
                    gShadow,
                    stoneX,
                    stoneY,
                    Lizzie.board.getStones()[Board.getIndex(threadI, j)],
                    threadI,
                    j);
              }
              latch.countDown();
            }
          }.start();
        }
        try {
          latch.await();
        } catch (InterruptedException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
      }
      cachedDisplayedBranchLength = displayedBranchLength;
      cachedBackgroundImageHasCoordinatesEnabled = showCoordinates();
      g.dispose();
      gShadow.dispose();
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

  /** Draw the 'ghost stones' which show a variationOpt Leelaz is thinking about */
  private void drawBranch() {
    if (cachedBoardWidth != boardWidth || cachedBoardHeight != boardHeight) {
      cachedBoardWidth = boardWidth;
      cachedBoardHeight = boardHeight;
      cachedShadow = null;
      cachedGhostShadow2 = null;
    }
    showingBranch = false;
    branchOpt = Optional.empty();
    bestMoves = Lizzie.board.getHistory().getCurrentHistoryNode().getData().bestMoves;
    if ((Lizzie.board.getHistory().isBlacksTurn()
            && !Lizzie.frame.toolbar.chkShowBlack.isSelected())
        || (!Lizzie.board.getHistory().isBlacksTurn()
            && !Lizzie.frame.toolbar.chkShowWhite.isSelected())) return;
    variationOpt = Optional.empty();

    if ((isShowingRawBoard() || !Lizzie.config.showBranchNow())) {
      return;
    }

    Optional<MoveData> suggestedMove = mouseOveredMove();

    if (!suggestedMove.isPresent()
        || Lizzie.frame.isShowingPolicy
        || Lizzie.frame.isShowingHeatmap) {
      mouseOverTemp = null;
      return;
    }
    boolean notChangedMouseOverMove =
        mouseOverTemp != null
            && mouseOverTempNode == Lizzie.board.getHistory().getCurrentHistoryNode()
            && mouseOverTemp.coordinate.equals(suggestedMove.get().coordinate);
    mouseOverTemp = suggestedMove.get();
    mouseOverTempNode = Lizzie.board.getHistory().getCurrentHistoryNode();
    int maxPlayouts = 0;
    for (MoveData move : bestMoves) {
      if (move.playouts > maxPlayouts) maxPlayouts = move.playouts;
    }
    float percentPlayouts = (float) suggestedMove.get().playouts / maxPlayouts;
    //        (Lizzie.config.leelaversion >= 17 && Lizzie.config.showlcbcolor &&
    // !Lizzie.leelaz.noLcb)
    //            ? (float) max(suggestedMove.get().playouts, suggestedMove.get().equalplayouts)
    //                / maxPlayouts
    //            :
    if (notChangedMouseOverMove) {
      if (displayedBranchLength == 1) if (!Lizzie.config.autoReplayBranch) return;
    } else {
      if (!(Lizzie.config.autoReplayBranch
          && Lizzie.config.autoReplayDisplayEntireVariationsFirst)) {
        if (displayedBranchLength < 2
            && (percentPlayouts <= Lizzie.config.minPlayoutRatioForStats
                || Lizzie.config.autoReplayBranch)) {
          displayedBranchLength = 1;
          if (!Lizzie.config.autoReplayBranch) return;
        }
        if (displayedBranchLength < 2
            && Lizzie.config.limitMaxSuggestion > 0
            && mouseOverOrder > Lizzie.config.limitMaxSuggestion) {
          displayedBranchLength = 1;
          if (!Lizzie.config.autoReplayBranch) return;
        }
      }
    }
    if (displayedBranchLength == 1 && !Lizzie.config.autoReplayBranch) displayedBranchLength = -2;
    // List<String>
    if (!Lizzie.config.noRefreshOnMouseMove
        || (!isShowingBranch || !mouseOverCoords.equals(suggestedMove.get().coordinate))) {
      variation = suggestedMove.get().variation;
      pvVistis = suggestedMove.get().pvVisits;
    }
    branch = null;
    //    if (Lizzie.engineManager.isEngineGame && Lizzie.engineManager.engineGameInfo.isGenmove)
    //      branch =
    //          new Branch(
    //              Lizzie.board,
    //              variation,
    //              pvVistis,
    //              true,
    //              this.displayedBranchLength > 0 ? displayedBranchLength : 199,
    //              false,
    //              false,
    //              null);
    //    else
    branch =
        new Branch(
            Lizzie.board,
            variation,
            pvVistis,
            this.displayedBranchLength > 0 ? displayedBranchLength : 199,
            false,
            false,
            null);
    mouseOverCoords = suggestedMove.get().coordinate;
    branchOpt = Optional.of(branch);
    variationOpt = Optional.of(variation);
    showingBranch = true;
    isShowingBranch = true;

    if (Lizzie.config.noRefreshOnMouseMove) {
      if (variation == cachedVariation
          && displayedBranchLength == cachedDisplayedBranchLengthFroBranch) return;
    } else {
      if (compareVariationListEquals(variation, cachedVariation)
          && displayedBranchLength == cachedDisplayedBranchLengthFroBranch) return;
    }
    cachedVariation = variation;
    cachedDisplayedBranchLengthFroBranch = displayedBranchLength;

    BufferedImage tempBranchStonesImage = new BufferedImage(boardWidth, boardHeight, TYPE_INT_ARGB);
    BufferedImage tempBranchStonesShadowImage =
        new BufferedImage(boardWidth, boardHeight, TYPE_INT_ARGB);

    Graphics2D g = (Graphics2D) tempBranchStonesImage.getGraphics();
    g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
    Graphics2D gShadow = (Graphics2D) tempBranchStonesShadowImage.getGraphics();
    gShadow.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

    g.setRenderingHint(KEY_ANTIALIASING, VALUE_ANTIALIAS_ON);
    drawShadowCache();
    final CountDownLatch latch = new CountDownLatch(Board.boardWidth);
    if (Lizzie.config.usePureStone) {
      for (int i = 0; i < Board.boardWidth; i++) {
        for (int j = 0; j < Board.boardHeight; j++) {
          // Display latest stone for ghost dead stone
          int index = Board.getIndex(i, j);
          Stone stone = branch.data.stones[index];
          if (Lizzie.board.getData().stones[index] != Stone.EMPTY && !branch.isNewStone[index])
            continue;
          if (branch.data.moveNumberList[index] > maxBranchMoves(false)) continue;

          int stoneX = scaledMarginWidth + squareWidth * i;
          int stoneY = scaledMarginHeight + squareHeight * j;
          drawStoneSimple(g, gShadow, stoneX, stoneY, stone, i, j);
          if (i == Lizzie.frame.floatBoard.mouseOverCoordinate[0]
              && j == Lizzie.frame.floatBoard.mouseOverCoordinate[1])
            isMouseOverStoneBlack = stone.isBlack();
        }
      }
    } else {
      for (int i = 0; i < Board.boardWidth; i++) {
        final int threadI = i;
        new Thread() {
          public void run() {
            for (int j = 0; j < Board.boardHeight; j++) {
              // Display latest stone for ghost dead stone
              int index = Board.getIndex(threadI, j);
              Stone stone = branch.data.stones[index];
              //     boolean isGhost = (stone == Stone.BLACK_GHOST || stone == Stone.WHITE_GHOST);
              if (Lizzie.board.getData().stones[index] != Stone.EMPTY && !branch.isNewStone[index])
                continue;
              if (branch.data.moveNumberList[index] > maxBranchMoves(false)) continue;

              int stoneX = scaledMarginWidth + squareWidth * threadI;
              int stoneY = scaledMarginHeight + squareHeight * j;
              // if(i==11&&j==6)
              // if (stone != Stone.BLACK_CAPTURED && stone != Stone.WHITE_CAPTURED)
              drawStone(g, gShadow, stoneX, stoneY, stone, threadI, j);
              if (threadI == Lizzie.frame.floatBoard.mouseOverCoordinate[0]
                  && j == Lizzie.frame.floatBoard.mouseOverCoordinate[1])
                isMouseOverStoneBlack = stone.isBlack();
            }
            latch.countDown();
          }
        }.start();
      }
      try {
        latch.await();
      } catch (InterruptedException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }
    g.dispose();
    gShadow.dispose();
    branchStonesImage = tempBranchStonesImage;
    branchStonesShadowImage = tempBranchStonesShadowImage;
  }

  private boolean compareVariationListEquals(List<String> variation, List<String> variation2) {
    if (variation.size() != variation2.size()) return false;
    else
      for (int i = 0; i < variation.size(); i++) {
        if (!variation.get(i).equals(variation2.get(i))) return false;
      }
    return true;
  }

  private Optional<MoveData> mouseOveredMove() {
    if (bestMoves != null && !bestMoves.isEmpty())
      for (int i = 0; i < bestMoves.size(); i++) {
        Optional<int[]> coords = Board.asCoordinates(bestMoves.get(i).coordinate);
        if (coords.isPresent()) {
          if (Lizzie.frame.isMouseOverFloatBoard(coords.get()[0], coords.get()[1])) {
            mouseOverOrder = i + 1;
            return Optional.of(bestMoves.get(i));
          }
        }
      }
    if (mouseOverTemp != null
        && mouseOverTempNode == Lizzie.board.getHistory().getCurrentHistoryNode()) {
      boolean needAddback = false;
      Optional<int[]> coords = Board.asCoordinates(mouseOverTemp.coordinate);
      if (coords.isPresent()) {
        if (Lizzie.frame.isMouseOverFloatBoard(coords.get()[0], coords.get()[1])) {
          needAddback = true;
        }
        if (needAddback) {
          mouseOverTemp.order = Math.max(bestMoves.size(), 9);
          bestMoves.add(mouseOverTemp);
          return Optional.of(mouseOverTemp);
        }
      }
    }
    mouseOverOrder = -1;
    return Optional.empty();
  }

  /** Render the shadows and stones in correct background-foreground order */
  private void renderImages(Graphics2D g) {
    g.setRenderingHint(KEY_ANTIALIASING, VALUE_ANTIALIAS_OFF);
    if ((Lizzie.config.showKataGoEstimate && !Lizzie.config.isHiddenKataEstimate)
        || Lizzie.frame.isShowingHeatmap)
      if (shouldShowCountBlockBelow()) g.drawImage(kataEstimateImage, x, y, null);
    if (isShowingBranch) {
      if (!Lizzie.config.removeDeadChainInVariation) {
        g.drawImage(cachedStonesShadowImage, x, y, null);
        g.drawImage(cachedStonesImage, x, y, null);
      } else if (displayedBranchLength == 1 && !Lizzie.config.autoReplayBranch) {
        g.drawImage(cachedStonesShadowImage, x, y, null);
        g.drawImage(cachedStonesImage, x, y, null);
      }
      if (displayedBranchLength != 1 || Lizzie.config.autoReplayBranch) {
        g.drawImage(branchStonesShadowImage, x, y, null);
        g.drawImage(branchStonesImage, x, y, null);
      }
    } else {
      g.drawImage(cachedStonesShadowImage, x, y, null);
      g.drawImage(cachedStonesImage, x, y, null);
    }
    g.drawImage(blockimage, x, y, null);
    if ((Lizzie.config.showKataGoEstimate && !Lizzie.config.isHiddenKataEstimate)
        || Lizzie.frame.isShowingHeatmap)
      if (!shouldShowCountBlockBelow()) g.drawImage(kataEstimateImage, x, y, null);
    if (Lizzie.frame.isCounting || Lizzie.frame.isAutocounting)
      g.drawImage(estimateImage, x, y, null);
    g.drawImage(selectImage, x, y, null);
    if (!cachedSelectImage.isEmpty()) {
      for (int i = 0; i < cachedSelectImage.size(); i++) {
        g.drawImage(cachedSelectImage.get(i), x, y, null);
      }
    }
    //  if (!branchOpt.isPresent()) {
    //    g.drawImage(unImportantSugg, x, y, null);
    //  g.drawImage(importantSugg, x, y, null);
    //  }
  }

  private void renderImagesUnimportant(Graphics2D g) {
    if (Lizzie.frame.isShowingPolicy || Lizzie.frame.isShowingHeatmap) return;
    if (!branchOpt.isPresent()) {
      g.drawImage(unImportantSugg, x, y, null);
    }
  }

  /** Draw move numbers and/or mark the last played move */
  private void drawMoveNumbers(Graphics2D g) {
    g.setRenderingHint(KEY_ANTIALIASING, VALUE_ANTIALIAS_ON);
    Board board = Lizzie.board;
    Optional<int[]> lastMoveOpt = branchOpt.map(b -> b.data.lastMove).orElse(board.getLastMove());

    if (!lastMoveOpt.isPresent() && board.getData().moveNumber != 0) {
      g.setColor(
          board.getData().blackToPlay ? new Color(255, 255, 255, 50) : new Color(0, 0, 0, 50));
      g.fillOval(
          x + boardWidth / 2 - 2 * stoneRadius,
          y + boardHeight / 2 - 2 * stoneRadius,
          stoneRadius * 4,
          stoneRadius * 4);
      g.setColor(
          board.getData().blackToPlay ? new Color(0, 0, 0, 180) : new Color(255, 255, 255, 180));
      drawString(
          g,
          x + boardWidth / 2,
          y + boardHeight / 2,
          LizzieFrame.winrateFont,
          "pass",
          stoneRadius * 2,
          stoneRadius * 3);
    }
    if (Lizzie.config.showMoveAllInBranch
        && !Lizzie.board.getHistory().getCurrentHistoryNode().isMainTrunk()) {
    } else if (!branchOpt.isPresent()) {
      return;
    }

    int[] moveNumberList;
    //    if (Lizzie.engineManager.isEngineGame && Lizzie.engineManager.engineGameInfo.isGenmove) {
    //      if (isShowingBranch)
    //        moveNumberList =
    //            branchOpt.map(b -> b.data.moveNumberList).orElse(board.getMoveNumberList());
    //      else moveNumberList = board.getMoveNumberList();
    //    } else
    moveNumberList = branchOpt.map(b -> b.data.moveNumberList).orElse(board.getMoveNumberList());

    // Allow to display only last move number
    int lastMoveNumber =
        branchOpt
            .map(b -> b.data.moveNumber)
            .orElse(Arrays.stream(moveNumberList).max().getAsInt());

    //    if (Lizzie.config.showMoveAllInBranch
    //        && !Lizzie.config.showMoveNumberFromOne
    //        && !Lizzie.frame.isTrying
    //        && !branchOpt.isPresent()
    //        && !Lizzie.engineManager.isEngineGame
    //        && !Lizzie.board.getHistory().getMainEndWithPass().isEndDummay()) {
    //      for (int i = 0; i < Board.boardWidth; i++) {
    //        for (int j = 0; j < Board.boardHeight; j++) {
    //          int stoneX = x + scaledMarginWidth + squareWidth * i;
    //          int stoneY = y + scaledMarginHeight + squareHeight * j;
    //          int here = Board.getIndex(i, j);
    //
    //          if ((Lizzie.config.allowMoveNumber > -1
    //              && lastMoveNumber - moveNumberList[here] >= Lizzie.config.allowMoveNumber)) {
    //            continue;
    //          }
    //          Stone stoneHere = branchOpt.map(b ->
    // b.data.stones[here]).orElse(board.getStones()[here]);
    //
    //          if ((lastMoveOpt.isPresent() && lastMoveOpt.get()[0] == i && lastMoveOpt.get()[1] ==
    // j)) {
    //            if (Lizzie.engineManager.isEngineGame &&
    // Lizzie.engineManager.engineGameInfo.isGenmove) {
    //              continue;
    //            }
    //            g.setColor(Color.RED.brighter());
    //            drawPolygonSmall(g, stoneX, stoneY, stoneRadius);
    //          } else {
    //            g.setColor(stoneHere.isBlack() ? Color.WHITE : Color.BLACK);
    //          }
    //
    //          int moveNumber = Lizzie.board.moveNumberByXY(i, j);
    //          if (moveNumber >= 100) {
    //            drawString(
    //                g,
    //                stoneX,
    //                stoneY,
    //                LizzieFrame.uiFont,
    //                moveNumber + "",
    //                (float) (stoneRadius * 1.4),
    //                (int) (stoneRadius * 1.85));
    //          } else if (moveNumber > 0) {
    //            drawString(
    //                g,
    //                stoneX,
    //                stoneY,
    //                LizzieFrame.uiFont,
    //                moveNumber + "",
    //                (float) (stoneRadius * 1.4),
    //                (int) (stoneRadius * 1.4));
    //          }
    //        }
    //      }
    //      return;
    //    }

    for (int i = 0; i < Board.boardWidth; i++) {
      for (int j = 0; j < Board.boardHeight; j++) {
        int stoneX = x + scaledMarginWidth + squareWidth * i;
        int stoneY = y + scaledMarginHeight + squareHeight * j;
        int here = Board.getIndex(i, j);

        // Allow to display only last move number
        if (Lizzie.config.showMoveAllInBranch
            && !Lizzie.board.getHistory().getCurrentHistoryNode().isMainTrunk()) {
        } else {
          if (!Lizzie.frame.isTrying && !Lizzie.engineManager.isEngineGame) {
            if ((Lizzie.config.allowMoveNumber > -1
                && lastMoveNumber - moveNumberList[here] >= Lizzie.config.allowMoveNumber)) {
              continue;
            }
          }
          if (Lizzie.engineManager.isEngineGame) {
            if ((Lizzie.config.allowMoveNumber > -1
                && lastMoveNumber - moveNumberList[here]
                    >= max(Lizzie.config.allowMoveNumber, 1))) {
              continue;
            }
          }
        }
        Stone stoneHere = branchOpt.map(b -> b.data.stones[here]).orElse(board.getStones()[here]);
        int mvNum = moveNumberList[Board.getIndex(i, j)];
        // don't write the move number if either: the move number is 0, or there will
        // already be
        // playout information written
        if ((mvNum > 0 || Lizzie.frame.isTrying && mvNum < 0)
            && (!branchOpt.isPresent() || !Lizzie.frame.floatBoard.isMouseOver(i, j))) {
          boolean isShowingPvVists = false;
          boolean reverse = (moveNumberList[Board.getIndex(i, j)] > maxBranchMoves(true));
          if ((lastMoveOpt.isPresent() && lastMoveOpt.get()[0] == i && lastMoveOpt.get()[1] == j)) {

            if (isShowingBranch
                && Lizzie.config.showPvVisitsLastMove
                && branch.pvVisitsList[here] > Lizzie.config.pvVisitsLimit) {
              int pvVisits = branch.pvVisitsList[here];
              g.setColor(Color.ORANGE);
              if (pvVisits >= 1000)
                g.fillRect(
                    (int) (stoneX - squareWidth * 0.43),
                    (int) (stoneY - squareWidth * 0.5),
                    (int) (squareWidth * 0.93),
                    (int) round(squareWidth * 0.33));
              else if (pvVisits < 10)
                g.fillRect(
                    (int) (stoneX - squareWidth * 0.43),
                    (int) (stoneY - squareWidth * 0.5),
                    (int) (squareWidth * 0.9),
                    (int) round(squareWidth * 0.33));
              else
                g.fillRect(
                    (int) (stoneX - squareWidth * 0.43),
                    (int) (stoneY - squareWidth * 0.5),
                    (int) (squareWidth * 0.9),
                    (int) round(squareWidth * 0.33));
              g.setColor(Color.BLACK);
              drawString(
                  g,
                  (int) (stoneX + squareWidth * 0.1),
                  (int) (stoneY - squareWidth * 0.2),
                  LizzieFrame.uiFont,
                  Font.PLAIN,
                  Lizzie.frame.getPlayoutsString(pvVisits),
                  (float) (squareWidth * 0.33),
                  squareWidth * 0.8,
                  1);
              g.setColor(Color.RED.brighter());
              drawPolygonSmallPv(g, stoneX, stoneY, squareWidth);

              drawString(
                  g,
                  stoneX,
                  (int) (stoneY + squareWidth * 0.12),
                  LizzieFrame.uiFont,
                  mvNum + "",
                  (float) (stoneRadius * 1.3),
                  (int) (stoneRadius * 1.4));

              continue;
            } else {
              g.setColor(Color.RED.brighter());
              drawPolygonSmall(g, stoneX, stoneY, stoneRadius);
              //              if (Lizzie.engineManager.isEngineGame
              //                  && Lizzie.engineManager.engineGameInfo.isGenmove
              //                  && !isShowingBranch) {
              //                continue;
              //              }
            }
          }
          // Draw white letters on black stones nomally.
          // But use black letters for showing black moves without stones.
          else {
            if (reverse) continue;
            //            if(stoneHere==Stone.BLACK_CAPTURED||stoneHere==Stone.WHITE_CAPTURED)
            //            	 g.setColor(stoneHere==Stone.WHITE_CAPTURED? Color.WHITE : Color.BLACK);
            //            else

            if (isShowingBranch
                && Lizzie.config.showPvVisitsAllMove
                && branch.pvVisitsList[here] > Lizzie.config.pvVisitsLimit) {
              int pvVisits = branch.pvVisitsList[here];
              isShowingPvVists = true;
              g.setColor(Color.ORANGE);
              if (pvVisits >= 1000)
                g.fillRect(
                    (int) (stoneX - squareWidth * 0.3),
                    (int) (stoneY - squareWidth * 0.5),
                    (int) (squareWidth * 0.8),
                    (int) round(squareWidth * 0.33));
              else if (pvVisits < 10)
                g.fillRect(
                    (int) (stoneX - squareWidth * 0.25),
                    (int) (stoneY - squareWidth * 0.5),
                    (int) (squareWidth * 0.72),
                    (int) round(squareWidth * 0.33));
              else
                g.fillRect(
                    (int) (stoneX - squareWidth * 0.25),
                    (int) (stoneY - squareWidth * 0.5),
                    (int) (squareWidth * 0.72),
                    (int) round(squareWidth * 0.33));
              g.setColor(Color.BLACK);
              drawString(
                  g,
                  (int) (stoneX + squareWidth * 0.1),
                  (int) (stoneY - squareWidth * 0.2),
                  LizzieFrame.uiFont,
                  Font.PLAIN,
                  Lizzie.frame.getPlayoutsString(pvVisits),
                  (float) (squareWidth * 0.33),
                  squareWidth * 0.8,
                  1);
            }
            g.setColor(stoneHere.isBlackColor() ? Color.WHITE : Color.BLACK);
          }
          String moveNumberString = mvNum + "";
          if (Lizzie.config.showMoveNumberFromOne && Lizzie.config.allowMoveNumber > 0) {
            if (lastMoveNumber > Lizzie.config.allowMoveNumber)
              moveNumberString = mvNum - (lastMoveNumber - Lizzie.config.allowMoveNumber) + "";
          }
          if (isShowingPvVists) {
            if (Lizzie.frame.isTrying) {
              if (mvNum < 0) {
                moveNumberString = -mvNum + "";
                drawString(
                    g,
                    stoneX,
                    (int) (stoneY + squareWidth * 0.12),
                    LizzieFrame.uiFont,
                    moveNumberString,
                    (float) (stoneRadius * 1.3),
                    (int) (stoneRadius * 1.4));
              }

            } else if (mvNum >= 100) {
              drawString(
                  g,
                  stoneX,
                  (int) (stoneY + squareWidth * 0.12),
                  LizzieFrame.uiFont,
                  moveNumberString,
                  (float) (stoneRadius * 1.3),
                  (int) (stoneRadius * 1.85));
            } else {
              drawString(
                  g,
                  stoneX,
                  (int) (stoneY + squareWidth * 0.12),
                  LizzieFrame.uiFont,
                  moveNumberString,
                  (float) (stoneRadius * 1.3),
                  (int) (stoneRadius * 1.4));
            }
          } else {
            if (Lizzie.frame.isTrying) {
              if (mvNum < 0) {
                moveNumberString = -mvNum + "";
                drawString(
                    g,
                    stoneX,
                    stoneY,
                    LizzieFrame.uiFont,
                    moveNumberString,
                    (float) (stoneRadius * 1.4),
                    (int) (stoneRadius * 1.4));
              }

            } else if (mvNum >= 100) {
              drawString(
                  g,
                  stoneX,
                  stoneY,
                  LizzieFrame.uiFont,
                  moveNumberString,
                  (float) (stoneRadius * 1.4),
                  (int) (stoneRadius * 1.85));
            } else {
              drawString(
                  g,
                  stoneX,
                  stoneY,
                  LizzieFrame.uiFont,
                  moveNumberString,
                  (float) (stoneRadius * 1.4),
                  (int) (stoneRadius * 1.4));
            }
          }
        }
      }
    }
  }

  /**
   * Draw all of Leelaz's suggestions as colored stones with winrate/playout statistics overlayed
   */
  private void drawLeelazSuggestions(Graphics2D g) {
    //  g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED);
    int minAlpha = 20;
    int minAlphaP = 32;
    // float winrateHueFactor = 0.9f;
    float alphaFactor = 5.0f;
    float redHue = Color.RGBtoHSB(2, 0, 0, null)[0];
    float greenHue = Color.RGBtoHSB(0, 255, 0, null)[0];
    float cyanHue = Lizzie.config.bestMoveColor;
    if (Lizzie.frame.isShowingHeatmap) {
      int maxPolicy = 0;
      //    int minPolicy = 0;
      Leelaz leelaz = Lizzie.leelaz;
      ArrayList<Integer> heatcount = leelaz.heatcount;
      for (Integer heat : heatcount) {
        if (heat > maxPolicy) maxPolicy = heat;
      }
      for (int i = 0; i < heatcount.size(); i++) {
        if (heatcount.get(i) > 0) {
          int y1 = i / Lizzie.board.boardWidth;
          int x1 = i % Lizzie.board.boardWidth;
          int suggestionX = x + scaledMarginWidth + squareWidth * x1;
          int suggestionY = y + scaledMarginHeight + squareHeight * y1;
          double percent = ((double) heatcount.get(i)) / maxPolicy;

          // g.setColor(Color.BLACK);
          // g.fillRect(stoneX - stoneRadius / 2, stoneY - stoneRadius / 2, stoneRadius,
          // stoneRadius);

          float hue;
          if (heatcount.get(i) == maxPolicy) {
            hue = cyanHue;
            Lizzie.board.hasBestHeatMove = true;
            Lizzie.board.bestHeatMoveX = x1;
            Lizzie.board.bestHeatMoveY = y1;
            if (Lizzie.frame.isAnaPlayingAgainstLeelaz && Lizzie.config.UsePureNetInGame) {
              if ((Lizzie.frame.playerIsBlack && !Lizzie.board.getHistory().isBlacksTurn())
                  || (!Lizzie.frame.playerIsBlack && Lizzie.board.getHistory().isBlacksTurn())) {
                if (Lizzie.frame.isAnaPlayingAgainstLeelaz && !Lizzie.frame.bothSync) {
                  if (Lizzie.board.getHistory().getMoveNumber()
                      >= Lizzie.config.anaGameResignStartMove) {
                    double winrate = Lizzie.leelaz.heatwinrate * 100;
                    if (Lizzie.leelaz.isKatago && !Lizzie.frame.playerIsBlack)
                      winrate = 100 - winrate;
                    if (winrate < Lizzie.config.anaGameResignPercent) {
                      Lizzie.leelaz.anaGameResignCount++;
                    } else Lizzie.leelaz.anaGameResignCount = 0;
                  }
                  if (Lizzie.leelaz.anaGameResignCount >= Lizzie.config.anaGameResignMove) {
                    Lizzie.frame.togglePonderMannul();
                    Utils.showMsg(
                        Lizzie.leelaz.oriEnginename
                            + " "
                            + resourceBundle.getString("Leelaz.resign"));
                    return;
                  }
                }
                Lizzie.board.playBestHeatMove();
                return;
              }
            }
          } else {
            if (Lizzie.frame.isAnaPlayingAgainstLeelaz && Lizzie.config.UsePureNetInGame) {
              continue;
            }
            double fraction;

            fraction = percent;

            hue = redHue + (greenHue - redHue) * (float) fraction;
          }

          float saturation = 1.0f;
          float brightness = 0.85f;
          float alpha =
              minAlphaP + (maxAlpha - minAlphaP) * max(0, (float) log(percent) / alphaFactor + 1);

          Color hsbColor = Color.getHSBColor(hue, saturation, brightness);
          Color color =
              new Color(hsbColor.getRed(), hsbColor.getGreen(), hsbColor.getBlue(), (int) alpha);
          if (!branchOpt.isPresent()) {
            if (!leelaz.iskataHeatmapShowOwner || !leelaz.isKatago) {
              drawShadiwCache2();
              drawShadow2(g, suggestionX, suggestionY, alpha / 255.0f);
              g.setColor(color);
              fillCircle(g, suggestionX, suggestionY, stoneRadius);
            }
            String text = String.format("%.1f", ((double) heatcount.get(i)) / 10);
            if (!leelaz.iskataHeatmapShowOwner || !leelaz.isKatago) g.setColor(Color.WHITE);
            else {
              if (hue == cyanHue) g.setColor(new Color(255, 0, 0));
              else if (percent >= 0.3) g.setColor(new Color(0, 120, 255));
              else g.setColor(new Color(0, 235, 0));
            }
            if (leelaz.isKatago && leelaz.iskataHeatmapShowOwner) {
              if (percent < 0.3) {
                drawString(
                    g,
                    suggestionX,
                    suggestionY,
                    LizzieFrame.winrateFont,
                    Font.PLAIN,
                    text,
                    stoneRadius * 4 / 5,
                    stoneRadius * 1.55,
                    0);
              } else
                drawString(
                    g,
                    suggestionX,
                    suggestionY,
                    LizzieFrame.winrateFont,
                    Font.PLAIN,
                    text,
                    stoneRadius,
                    stoneRadius * 1.9,
                    0);
            } else
              drawString(
                  g,
                  suggestionX,
                  suggestionY,
                  LizzieFrame.winrateFont,
                  Font.PLAIN,
                  text,
                  stoneRadius,
                  stoneRadius * 1.9,
                  0);
          }
        }
      }
      return;
    }

    if (Lizzie.frame.isShowingPolicy) {

      if (bestMoves.isEmpty()) return;
      Double maxPolicy = 0.0;
      // int minPolicy = 0;
      for (int n = 0; n < bestMoves.size(); n++) {
        if (bestMoves.get(n).policy > maxPolicy) maxPolicy = bestMoves.get(n).policy;
      }
      for (int i = 0; i < bestMoves.size(); i++) {
        MoveData bestmove = bestMoves.get(i);
        int y1 = 0;
        int x1 = 0;
        Optional<int[]> coord = Board.asCoordinates(bestmove.coordinate);
        if (coord.isPresent()) {
          x1 = coord.get()[0];
          y1 = coord.get()[1];

          int suggestionX = x + scaledMarginWidth + squareWidth * x1;
          int suggestionY = y + scaledMarginHeight + squareHeight * y1;
          double percent = bestmove.policy / maxPolicy;

          float hue;
          if (bestmove.policy == maxPolicy) {
            hue = cyanHue;
          } else {
            double fraction;

            fraction = percent;

            hue = redHue + (greenHue - redHue) * (float) fraction;
          }

          float saturation = 1.0f;
          float brightness = 0.85f;
          float alpha =
              minAlphaP + (maxAlpha - minAlphaP) * max(0, (float) log(percent) / alphaFactor + 1);

          Color hsbColor = Color.getHSBColor(hue, saturation, brightness);
          Color color =
              new Color(hsbColor.getRed(), hsbColor.getGreen(), hsbColor.getBlue(), (int) alpha);
          if (!branchOpt.isPresent()) {
            drawShadiwCache2();
            drawShadow2(g, suggestionX, suggestionY, alpha / 255.0f);
            g.setColor(color);
            fillCircle(g, suggestionX, suggestionY, stoneRadius);

            String text = String.format("%.1f", ((double) bestMoves.get(i).policy));
            g.setColor(Color.WHITE);
            drawString(
                g,
                suggestionX,
                suggestionY,
                LizzieFrame.winrateFont,
                Font.PLAIN,
                text,
                stoneRadius,
                stoneRadius * 1.9,
                0);
          }
        }
      }

    } else {
      if (bestMoves != null && !bestMoves.isEmpty()) {

        // Collections.sort(bestMoves);

        int maxPlayouts = 0;
        double maxWinrate = 0;
        double minWinrate = 100.0;
        double maxScoreMean = -300;
        for (MoveData move : bestMoves) {
          if (move.playouts > maxPlayouts) maxPlayouts = move.playouts;
          if (move.winrate > maxWinrate) maxWinrate = move.winrate;
          if (move.winrate < minWinrate) minWinrate = move.winrate;
          if (move.isKataData && Lizzie.config.showScoremeanInSuggestion) {
            if (move.scoreMean > maxScoreMean) maxScoreMean = move.scoreMean;
          }
        }

        for (int i = bestMoves.size() - 1; i >= 0; i--) {
          MoveData move = bestMoves.get(i);
          // number = i + 1;

          boolean isBestMove = bestMoves.get(0) == move;
          boolean hasMaxWinrate = move.winrate == maxWinrate;
          boolean flipWinrate =
              Lizzie.config.winrateAlwaysBlack && !Lizzie.board.getData().blackToPlay;

          if (move.playouts == 0) {
            continue; // This actually can happen
          }
          double fraction = 0;
          float percentPlayouts = (float) move.playouts / maxPlayouts;
          //              (Lizzie.config.leelaversion >= 17
          //                      && Lizzie.config.showlcbcolor
          //                      && !Lizzie.leelaz.noLcb
          //                      &&
          // Lizzie.board.getHistory().getCurrentHistoryNode().getData().winrate < 95
          //                      &&
          // Lizzie.board.getHistory().getCurrentHistoryNode().getData().winrate > 5)
          //                  ? (float) max(move.playouts, move.equalplayouts) / maxPlayouts
          //                  :
          //            double percentWinrate =
          //                Math.min(
          //                    1,
          //                    Math.max(0.01, move.winrate - minWinrate)
          //                        / Math.max(0.01, maxWinrate - minWinrate));

          Optional<int[]> coordsOpt = Board.asCoordinates(move.coordinate);
          if (!coordsOpt.isPresent()) {
            //	Lizzie.board.getHistory().getCurrentHistoryNode().getData().comment+=move.coordinate+"计算量:"+move.playouts+"";
            continue;
          }
          int[] coords = coordsOpt.get();

          int suggestionX = x + scaledMarginWidth + squareWidth * coords[0];
          int suggestionY = y + scaledMarginHeight + squareHeight * coords[1];
          boolean isMouseOver =
              Lizzie.frame.floatBoard != null
                  && Lizzie.frame.floatBoard.isMouseOver(coords[0], coords[1]);
          if ((Lizzie.config.limitMaxSuggestion > 0
                  && move.order + 1 > Lizzie.config.limitMaxSuggestion)
              && !isMouseOver) {
            if (hasDrawBackground[Lizzie.board.getIndex(coords[0], coords[1])]) continue;
          }
          if (percentPlayouts <= Lizzie.config.minPlayoutRatioForStats
              && !Lizzie.config.showNoSuggCircle
              && !isMouseOver) {
            if (hasDrawBackground[Lizzie.board.getIndex(coords[0], coords[1])]) continue;
          }
          float hue;
          boolean hue2;
          if (isBestMove) {
            hue = cyanHue;
            hue2 = true;
          } else {
            fraction = percentPlayouts;
            fraction = Math.pow(fraction, (double) 1 / Lizzie.config.suggestionColorRatio);
            // }
            hue2 = fraction > 0.375 ? true : false;
            hue = redHue + (greenHue - redHue) * (float) fraction;
          }

          float saturation = 1.0f;
          float brightness = 0.85f;
          float alpha;
          if (percentPlayouts < 0.05 && !isBestMove)
            alpha =
                minAlpha
                    + (maxAlpha - minAlpha)
                        * max(0, (float) log(percentPlayouts) / alphaFactor + 1);
          else
            alpha = 32 + (maxAlpha - 32) * max(0, (float) log(percentPlayouts) / alphaFactor + 1);

          Color hsbColor = Color.getHSBColor(hue, saturation, brightness);
          Color color =
              new Color(hsbColor.getRed(), hsbColor.getGreen(), hsbColor.getBlue(), (int) alpha);
          if (!branchOpt.isPresent() || isMouseOver) {
            if (Lizzie.config.showSuggestionOrder && move.order == 0) {
              boolean blackToPlay = Lizzie.board.getData().blackToPlay;
              drawStringForOrder(
                  g,
                  (int) round(suggestionX + squareWidth * 0.43) + 1,
                  (int) round(suggestionY - squareWidth * 0.358) - 1,
                  LizzieFrame.winrateFont,
                  Font.PLAIN,
                  "1",
                  squareWidth * 0.36f,
                  squareWidth * 0.39,
                  1,
                  blackToPlay);
            }
          }
          if (!branchOpt.isPresent()) {
            {
              if (isFancyBoard) {
                g.setPaint(paint);
                Composite comp = g.getComposite();
                fillCircle(g, suggestionX, suggestionY, stoneRadius);
                g.setComposite(comp);
              } else {
                g.setColor(noFancyColor);
                Composite comp = g.getComposite();
                fillCircle(g, suggestionX, suggestionY, stoneRadius);
                g.setComposite(comp);
              }
              g.setColor(color.GRAY);
              if (percentPlayouts <= Lizzie.config.minPlayoutRatioForStats)
                drawCircleMin(g, suggestionX, suggestionY, stoneRadius + 1, 26.5f);
              else drawCircle(g, suggestionX, suggestionY, stoneRadius + 1, 26.5f);
              g.setColor(color);
              if (isBestMove) fillCircleBest(g, suggestionX, suggestionY, stoneRadius);
              else fillCircle(g, suggestionX, suggestionY, stoneRadius);
              if (isBestMove) {
                if (Lizzie.config.showBlueRing) {
                  g.setColor(color.BLUE.brighter());
                  drawCircleBest(g, suggestionX, suggestionY, stoneRadius + 1, 15f);
                } else {
                  g.setColor(color.GRAY);
                  drawCircle(g, suggestionX, suggestionY, stoneRadius + 1, 26.5f);
                }
              }
            }
          }
          if (percentPlayouts <= Lizzie.config.minPlayoutRatioForStats && !isMouseOver) {
            continue;
          }
          if (!branchOpt.isPresent() || isMouseOver) {
            double roundedWinrate = round(move.winrate * 10) / 10.0;
            if (flipWinrate) {
              roundedWinrate = 100.0 - roundedWinrate;
            }

            //  g.setColor(Color.BLACK);

            String text;
            // if (Lizzie.config.handicapInsteadOfWinrate) {
            //  text = String.format("%.2f", Lizzie.leelaz.winrateToHandicap(move.winrate));
            // } else {
            text = String.format("%.1f", roundedWinrate);
            // }
            if (Lizzie.config.showSuggestionOrder && move.order < 9 && move.order > 0) {
              boolean blackToPlay = Lizzie.board.getData().blackToPlay;
              drawStringForOrder(
                  g,
                  (int) round(suggestionX + squareWidth * 0.43),
                  (int) round(suggestionY - squareWidth * 0.358),
                  LizzieFrame.winrateFont,
                  Font.PLAIN,
                  move.order + 1 + "",
                  squareWidth * 0.36f,
                  squareWidth * 0.39,
                  1,
                  blackToPlay);
            }

            if (!(Lizzie.config.limitMaxSuggestion > 0
                    && move.order + 1 > Lizzie.config.limitMaxSuggestion)
                || isMouseOver) {
              // number++;
              if (isMouseOver && isShowingBranch) {
                // Color oriColor = g.getColor();
                g.setColor(Color.RED.brighter());
                drawCircle(g, suggestionX, suggestionY, stoneRadius + 1, 11f);
                // g.setColor(oriColor);
              }

              if (Lizzie.config.whiteSuggestionWhite) {
                //                if (Lizzie.engineManager.isEngineGame
                //                    && Lizzie.engineManager.engineGameInfo.isGenmove) {
                //                  if (Lizzie.board.getHistory().isBlacksTurn())
                // g.setColor(Color.WHITE);
                //                  else g.setColor(Color.BLACK);
                //                } else
                {
                  if (Lizzie.board.getHistory().isBlacksTurn()) g.setColor(Color.BLACK);
                  else g.setColor(Color.WHITE);
                }
              } else g.setColor(Color.BLACK);
              if (branchOpt.isPresent()) { // 待修改
                //                if (Lizzie.engineManager.isEngineGame
                //                    && Lizzie.engineManager.engineGameInfo.isGenmove) {
                //                  if (isMouseOverStoneBlack) g.setColor(Color.BLACK);
                //                  else g.setColor(Color.WHITE);
                //                } else
                if (isMouseOverStoneBlack) g.setColor(Color.WHITE);
                else g.setColor(Color.BLACK);
              }
              //      boolean isGenmoveBest = false;
              //              if (Lizzie.engineManager.isEngineGame
              //                  && Lizzie.engineManager.engineGameInfo.isGenmove) {
              //                Optional<int[]> lastMoveOpt = Lizzie.board.getLastMove();
              //                if ((lastMoveOpt.isPresent()
              //                    && lastMoveOpt.get()[0] == coords[0]
              //                    && lastMoveOpt.get()[1] == coords[1])) {
              //                  if (Lizzie.board.getData().blackToPlay) g.setColor(Color.BLACK);
              //                  else g.setColor(Color.WHITE);
              //                  isGenmoveBest = true;
              //                }
              //              }

              Color maxColor;
              if (isBestMove) maxColor = Lizzie.config.bestColor;
              else maxColor = hue2 ? Color.RED.brighter() : Color.CYAN;
              boolean showWinrate = Lizzie.config.showWinrateInSuggestion;
              boolean showPlayouts = Lizzie.config.showPlayoutsInSuggestion;
              boolean showScoreLead = move.isKataData && Lizzie.config.showScoremeanInSuggestion;
              boolean canShowMaxColor = Lizzie.config.showSuggestionMaxRed && !isMouseOver;
              Color oriColor = g.getColor();
              if (showScoreLead && showPlayouts && showWinrate) {
                double score = move.scoreMean;
                if (Lizzie.board.getHistory().isBlacksTurn()) {
                  if (Lizzie.config.showKataGoBoardScoreMean) {
                    score = score + Lizzie.board.getHistory().getGameInfo().getKomi();
                  }
                } else {
                  if (Lizzie.config.showKataGoBoardScoreMean) {
                    score = score - Lizzie.board.getHistory().getGameInfo().getKomi();
                  }
                  if (Lizzie.config.winrateAlwaysBlack) {
                    score = -score;
                  }
                }
                boolean shouldShowMaxColorWinrate = canShowMaxColor && hasMaxWinrate;
                boolean shouldShowMaxColorPlayouts =
                    canShowMaxColor && move.playouts == maxPlayouts;
                boolean shouldShowMaxColorScoreLead =
                    canShowMaxColor && move.scoreMean == maxScoreMean;
                String winrateText = String.format("%.1f", roundedWinrate);
                String playoutsText = Lizzie.frame.getPlayoutsString(move.playouts);
                String scoreLeadText = String.format("%.1f", score);
                if (Lizzie.config.useDefaultInfoRowOrder) {
                  if (shouldShowMaxColorWinrate) g.setColor(maxColor);
                  if (roundedWinrate < 10)
                    drawStringFor3row(
                        g,
                        suggestionX,
                        suggestionY - (int) round(squareWidth * 0.127),
                        LizzieFrame.winrateFont,
                        Font.PLAIN,
                        winrateText,
                        squareWidth * 0.36f,
                        squareWidth * 0.67);
                  else
                    drawStringFor3row(
                        g,
                        suggestionX,
                        suggestionY - (int) round(squareWidth * 0.125),
                        LizzieFrame.winrateFont,
                        Font.PLAIN,
                        winrateText,
                        squareWidth * 0.35f,
                        squareWidth * 0.67);
                  if (shouldShowMaxColorWinrate) g.setColor(oriColor);
                  if (shouldShowMaxColorPlayouts) g.setColor(maxColor);
                  if (move.playouts >= 1000) {
                    drawStringFor3row(
                        g,
                        suggestionX,
                        suggestionY + (int) round(squareWidth * 0.18),
                        LizzieFrame.playoutsFont,
                        Font.PLAIN,
                        playoutsText,
                        squareWidth * 0.34f,
                        stoneRadius * 1.8);
                  } else {
                    drawStringFor3row(
                        g,
                        suggestionX,
                        suggestionY + (int) round(squareWidth * 0.18),
                        LizzieFrame.playoutsFont,
                        Font.PLAIN,
                        playoutsText,
                        squareWidth * 0.34f,
                        stoneRadius * 1.3);
                  }
                  if (shouldShowMaxColorPlayouts) g.setColor(oriColor);
                  if (shouldShowMaxColorScoreLead) g.setColor(maxColor);
                  drawStringFor3row(
                      g,
                      suggestionX,
                      suggestionY + (int) round(squareWidth * 0.435),
                      LizzieFrame.winrateFont,
                      Font.PLAIN,
                      scoreLeadText,
                      availableWidth * 0.273f / (Board.boardWidth - 1),
                      stoneRadius * 1.6);
                  if (shouldShowMaxColorScoreLead) g.setColor(oriColor);
                } else {
                  String rowText1 = getSuggestionInfoRow1(winrateText, playoutsText, scoreLeadText);
                  String rowText2 = getSuggestionInfoRow2(winrateText, playoutsText, scoreLeadText);
                  String rowText3 = getSuggestionInfoRow3(winrateText, playoutsText, scoreLeadText);
                  boolean shouldShowMaxColorRow1 =
                      (shouldShowMaxColorWinrate && rowText1.equals(winrateText))
                          || (shouldShowMaxColorPlayouts && rowText1.equals(playoutsText))
                          || (shouldShowMaxColorScoreLead && rowText1.equals(scoreLeadText));
                  boolean shouldShowMaxColorRow2 =
                      (shouldShowMaxColorWinrate && rowText2.equals(winrateText))
                          || (shouldShowMaxColorPlayouts && rowText2.equals(playoutsText))
                          || (shouldShowMaxColorScoreLead && rowText2.equals(scoreLeadText));
                  boolean shouldShowMaxColorRow3 =
                      (shouldShowMaxColorWinrate && rowText3.equals(winrateText))
                          || (shouldShowMaxColorPlayouts && rowText3.equals(playoutsText))
                          || (shouldShowMaxColorScoreLead && rowText3.equals(scoreLeadText));
                  if (shouldShowMaxColorRow1) g.setColor(maxColor);
                  drawStringFor3row(
                      g,
                      suggestionX,
                      suggestionY - (int) round(squareWidth * 0.125),
                      Lizzie.config.suggestionInfoPlayouts == 1
                          ? LizzieFrame.playoutsFont
                          : LizzieFrame.winrateFont,
                      Font.PLAIN,
                      rowText1,
                      squareWidth * 0.35f,
                      squareWidth * 0.67);
                  if (shouldShowMaxColorRow1) g.setColor(oriColor);
                  if (shouldShowMaxColorRow2) g.setColor(maxColor);
                  drawStringFor3row(
                      g,
                      suggestionX,
                      suggestionY + (int) round(squareWidth * 0.18),
                      Lizzie.config.suggestionInfoPlayouts == 2
                          ? LizzieFrame.playoutsFont
                          : LizzieFrame.winrateFont,
                      Font.PLAIN,
                      rowText2,
                      squareWidth * 0.32f,
                      stoneRadius * 1.8);
                  if (shouldShowMaxColorRow2) g.setColor(oriColor);
                  if (shouldShowMaxColorRow3) g.setColor(maxColor);
                  drawStringFor3row(
                      g,
                      suggestionX,
                      suggestionY + (int) round(squareWidth * 0.435),
                      Lizzie.config.suggestionInfoPlayouts == 3
                          ? LizzieFrame.playoutsFont
                          : LizzieFrame.winrateFont,
                      Font.PLAIN,
                      rowText3,
                      availableWidth * 0.273f / (Board.boardWidth - 1),
                      stoneRadius * 1.6);
                  if (shouldShowMaxColorRow3) g.setColor(oriColor);
                }
              } else if (showWinrate && showPlayouts) {
                String winrateText = String.format("%.1f", roundedWinrate);
                String playoutsText = Lizzie.frame.getPlayoutsString(move.playouts);
                boolean shouldShowMaxColorWinrate = canShowMaxColor && hasMaxWinrate;
                boolean shouldShowMaxColorPlayouts =
                    canShowMaxColor && move.playouts == maxPlayouts;
                if (Lizzie.config.useDefaultInfoRowOrder
                    || Lizzie.config.suggestionInfoWinrate < Lizzie.config.suggestionInfoPlayouts) {
                  if (shouldShowMaxColorWinrate) g.setColor(maxColor);
                  if (roundedWinrate < 10) {
                    drawString(
                        g,
                        suggestionX,
                        suggestionY - squareWidth / 15,
                        LizzieFrame.winrateFont,
                        Font.PLAIN,
                        winrateText,
                        stoneRadius,
                        squareWidth * 0.57,
                        1);
                  } else {
                    drawString(
                        g,
                        suggestionX,
                        suggestionY - squareWidth / 16,
                        LizzieFrame.winrateFont,
                        Font.PLAIN,
                        winrateText,
                        stoneRadius,
                        squareWidth * 0.735,
                        1);
                  }
                  if (shouldShowMaxColorWinrate) g.setColor(oriColor);
                  if (shouldShowMaxColorPlayouts) g.setColor(maxColor);
                  //    if (move.playouts >= 1000) {

                  drawString(
                      g,
                      suggestionX,
                      suggestionY + stoneRadius * 15 / 35,
                      LizzieFrame.playoutsFont,
                      playoutsText,
                      stoneRadius * 0.77f,
                      stoneRadius * 1.8);
                  //                  } else {
                  //                    drawString(
                  //                        g,
                  //                        suggestionX,
                  //                        suggestionY + stoneRadius * 16 / 35,
                  //                        LizzieFrame.playoutsFont,
                  //                        playoutsText,
                  //                        stoneRadius * 0.8f,
                  //                        stoneRadius * 1.4);
                  //                  }
                  if (shouldShowMaxColorPlayouts) g.setColor(oriColor);
                } else {
                  if (shouldShowMaxColorWinrate) g.setColor(maxColor);
                  drawString(
                      g,
                      suggestionX,
                      suggestionY + stoneRadius * 15 / 35,
                      LizzieFrame.winrateFont,
                      winrateText,
                      stoneRadius * 0.77f,
                      stoneRadius * 1.8);

                  if (shouldShowMaxColorWinrate) g.setColor(oriColor);
                  if (shouldShowMaxColorPlayouts) g.setColor(maxColor);
                  drawString(
                      g,
                      suggestionX,
                      suggestionY - squareWidth / 15,
                      LizzieFrame.playoutsFont,
                      Font.PLAIN,
                      playoutsText,
                      stoneRadius * 0.77f,
                      stoneRadius * 1.8,
                      1);
                  if (shouldShowMaxColorPlayouts) g.setColor(oriColor);
                }
              } else if (showWinrate && showScoreLead) {
                boolean shouldShowMaxColorWinrate = canShowMaxColor && hasMaxWinrate;
                boolean shouldShowMaxColorScoreLead =
                    canShowMaxColor && move.scoreMean == maxScoreMean;
                double score = move.scoreMean;
                if (Lizzie.board.getHistory().isBlacksTurn()) {
                  if (Lizzie.config.showKataGoBoardScoreMean) {
                    score = score + Lizzie.board.getHistory().getGameInfo().getKomi();
                  }
                } else {
                  if (Lizzie.config.showKataGoBoardScoreMean) {
                    score = score - Lizzie.board.getHistory().getGameInfo().getKomi();
                  }
                  if (Lizzie.config.winrateAlwaysBlack) {
                    score = -score;
                  }
                }
                String winrateText = String.format("%.1f", roundedWinrate);
                String scoreLeadText = String.format("%.1f", score);
                if (Lizzie.config.useDefaultInfoRowOrder
                    || Lizzie.config.suggestionInfoWinrate
                        < Lizzie.config.suggestionInfoScoreLead) {
                  if (shouldShowMaxColorWinrate) g.setColor(maxColor);
                  if (roundedWinrate < 10) {
                    drawString(
                        g,
                        suggestionX,
                        suggestionY - squareWidth / 15,
                        LizzieFrame.winrateFont,
                        Font.PLAIN,
                        winrateText,
                        stoneRadius,
                        squareWidth * 0.57,
                        1);
                  } else {
                    drawString(
                        g,
                        suggestionX,
                        suggestionY - squareWidth / 16,
                        LizzieFrame.winrateFont,
                        Font.PLAIN,
                        winrateText,
                        stoneRadius,
                        squareWidth * 0.735,
                        1);
                  }
                  if (shouldShowMaxColorWinrate) g.setColor(oriColor);
                  if (shouldShowMaxColorScoreLead) g.setColor(maxColor);
                  drawString(
                      g,
                      suggestionX,
                      suggestionY + stoneRadius * 4 / 9,
                      LizzieFrame.winrateFont,
                      scoreLeadText,
                      stoneRadius * 0.75f,
                      stoneRadius * 1.6);
                  if (shouldShowMaxColorScoreLead) g.setColor(oriColor);
                } else {
                  if (shouldShowMaxColorWinrate) g.setColor(maxColor);
                  drawString(
                      g,
                      suggestionX,
                      suggestionY + stoneRadius * 15 / 35,
                      LizzieFrame.winrateFont,
                      winrateText,
                      stoneRadius * 0.77f,
                      stoneRadius * 1.8);

                  if (shouldShowMaxColorWinrate) g.setColor(oriColor);
                  if (shouldShowMaxColorScoreLead) g.setColor(maxColor);
                  drawString(
                      g,
                      suggestionX,
                      suggestionY - squareWidth / 16,
                      LizzieFrame.winrateFont,
                      Font.PLAIN,
                      scoreLeadText,
                      stoneRadius * 0.88f,
                      squareWidth * 0.735,
                      1);
                  if (shouldShowMaxColorScoreLead) g.setColor(oriColor);
                }
              } else if (showPlayouts && showScoreLead) {
                boolean shouldShowMaxColorPlayouts =
                    canShowMaxColor && move.playouts == maxPlayouts;
                boolean shouldShowMaxColorScoreLead =
                    canShowMaxColor && move.scoreMean == maxScoreMean;
                double score = move.scoreMean;
                if (Lizzie.board.getHistory().isBlacksTurn()) {
                  if (Lizzie.config.showKataGoBoardScoreMean) {
                    score = score + Lizzie.board.getHistory().getGameInfo().getKomi();
                  }
                } else {
                  if (Lizzie.config.showKataGoBoardScoreMean) {
                    score = score - Lizzie.board.getHistory().getGameInfo().getKomi();
                  }
                  if (Lizzie.config.winrateAlwaysBlack) {
                    score = -score;
                  }
                }
                String playoutsText = Lizzie.frame.getPlayoutsString(move.playouts);
                String scoreLeadText = String.format("%.1f", score);
                if (Lizzie.config.useDefaultInfoRowOrder
                    || Lizzie.config.suggestionInfoPlayouts
                        < Lizzie.config.suggestionInfoScoreLead) {
                  if (shouldShowMaxColorPlayouts) g.setColor(maxColor);
                  drawString(
                      g,
                      suggestionX,
                      suggestionY - stoneRadius * 1 / 15,
                      LizzieFrame.playoutsFont,
                      Font.PLAIN,
                      playoutsText,
                      stoneRadius * 0.82f,
                      stoneRadius * 1.73,
                      1);
                  if (shouldShowMaxColorPlayouts) g.setColor(oriColor);
                  if (shouldShowMaxColorScoreLead) g.setColor(maxColor);
                  drawString(
                      g,
                      suggestionX,
                      suggestionY + stoneRadius * 4 / 9,
                      LizzieFrame.winrateFont,
                      scoreLeadText,
                      stoneRadius * 0.75f,
                      stoneRadius * 1.6);
                  if (shouldShowMaxColorScoreLead) g.setColor(oriColor);
                } else {
                  if (shouldShowMaxColorPlayouts) g.setColor(maxColor);
                  // if (move.playouts >= 1000) {
                  drawString(
                      g,
                      suggestionX,
                      suggestionY + stoneRadius * 15 / 35,
                      LizzieFrame.playoutsFont,
                      playoutsText,
                      stoneRadius * 0.77f,
                      stoneRadius * 1.8);
                  //                  } else {
                  //                    drawString(
                  //                        g,
                  //                        suggestionX,
                  //                        suggestionY + stoneRadius * 16 / 35,
                  //                        LizzieFrame.playoutsFont,
                  //                        playoutsText,
                  //                        stoneRadius * 0.8f,
                  //                        stoneRadius * 1.4);
                  //                  }
                  if (shouldShowMaxColorPlayouts) g.setColor(oriColor);
                  if (shouldShowMaxColorScoreLead) g.setColor(maxColor);
                  drawString(
                      g,
                      suggestionX,
                      suggestionY - squareWidth / 16,
                      LizzieFrame.winrateFont,
                      Font.PLAIN,
                      scoreLeadText,
                      stoneRadius * 0.88f,
                      squareWidth * 0.735,
                      1);
                  if (shouldShowMaxColorScoreLead) g.setColor(oriColor);
                }

              } else if (showWinrate) {
                boolean shouldShowMaxColorWinrate = canShowMaxColor && hasMaxWinrate;
                if (shouldShowMaxColorWinrate) g.setColor(maxColor);
                if (roundedWinrate < 10) {
                  drawString(
                      g,
                      suggestionX,
                      suggestionY,
                      LizzieFrame.winrateFont,
                      String.format("%.1f", roundedWinrate),
                      squareWidth * 0.46f,
                      stoneRadius * 1.9);
                } else {
                  drawString(
                      g,
                      suggestionX,
                      suggestionY,
                      LizzieFrame.winrateFont,
                      String.format("%.1f", roundedWinrate),
                      squareWidth * 0.46f,
                      stoneRadius * 1.9);
                }
                if (shouldShowMaxColorWinrate) g.setColor(oriColor);
              } else if (showPlayouts) {
                boolean shouldShowMaxColorPlayouts =
                    canShowMaxColor && move.playouts == maxPlayouts;
                if (shouldShowMaxColorPlayouts) g.setColor(maxColor);
                drawString(
                    g,
                    suggestionX,
                    suggestionY,
                    LizzieFrame.playoutsFont,
                    Lizzie.frame.getPlayoutsString(move.playouts),
                    stoneRadius,
                    stoneRadius * 1.9);
                if (shouldShowMaxColorPlayouts) g.setColor(oriColor);
              } else if (showScoreLead) {
                double score = move.scoreMean;
                if (Lizzie.board.getHistory().isBlacksTurn()) {
                  if (Lizzie.config.showKataGoBoardScoreMean) {
                    score = score + Lizzie.board.getHistory().getGameInfo().getKomi();
                  }
                } else {
                  if (Lizzie.config.showKataGoBoardScoreMean) {
                    score = score - Lizzie.board.getHistory().getGameInfo().getKomi();
                  }
                  if (Lizzie.config.winrateAlwaysBlack) {
                    score = -score;
                  }
                }
                boolean shouldShowMaxColorScoreLead =
                    canShowMaxColor && move.scoreMean == maxScoreMean;
                if (shouldShowMaxColorScoreLead) g.setColor(maxColor);
                drawString(
                    g,
                    suggestionX,
                    suggestionY,
                    LizzieFrame.winrateFont,
                    String.format("%.1f", score),
                    stoneRadius,
                    stoneRadius * 1.7);
                if (shouldShowMaxColorScoreLead) g.setColor(oriColor);
              }
            }
          }
          // }
        }
      } else {
        clearSuggestionImage();
      }
    }
  }

  public void clearSuggestionImage() {
    if (!unImportantCleared) {
      unImportantSugg = new BufferedImage(boardWidth, boardHeight, TYPE_INT_ARGB);
      unImportantCleared = true;
      drawUnimportantSuggCount = 100;
    }
    hasDrawBackground = new boolean[Lizzie.board.boardHeight * Lizzie.board.boardWidth];
    clearBranch();
  }

  private void drawLeelazSuggestionsUnimportant() {
    BufferedImage newUnImportantSugg = new BufferedImage(boardWidth, boardHeight, TYPE_INT_ARGB);
    Graphics2D g = newUnImportantSugg.createGraphics();
    g.setRenderingHint(KEY_ANTIALIASING, VALUE_ANTIALIAS_ON);
    int minAlpha = 20;
    // float winrateHueFactor = 0.9f;
    float alphaFactor = 5.0f;
    float redHue = Color.RGBtoHSB(2, 0, 0, null)[0];
    float greenHue = Color.RGBtoHSB(0, 255, 0, null)[0];
    float cyanHue = Lizzie.config.bestMoveColor;
    if (bestMoves != null && !bestMoves.isEmpty()) {

      // Collections.sort(bestMoves);

      int maxPlayouts = 0;
      double maxWinrate = 0;
      //      double minWinrate = 100.0;
      //      double maxScoreMean = -300;
      //      List<MoveData> tempbest1 = new ArrayList();
      //      List<MoveData> tempbest2 = new ArrayList();
      //      for (int i = 0; i < bestMoves.size(); i++) {
      //        tempbest1.add(bestMoves.get(i)); // 开始复制一个list的内容到另外一个list
      //        tempbest2.add(bestMoves.get(i));
      //      }
      //      Collections.sort(
      //          tempbest1,
      //          new Comparator<MoveData>() {
      //
      //            @Override
      //            public int compare(MoveData s1, MoveData s2) {
      //              // 降序
      //              if (s1.lcb < s2.lcb) return 1;
      //              if (s1.lcb > s2.lcb) return -1;
      //              else return 0;
      //            }
      //          });
      //
      //      Collections.sort(
      //          tempbest2,
      //          new Comparator<MoveData>() {
      //
      //            @Override
      //            public int compare(MoveData s1, MoveData s2) {
      //              // 降序
      //              if (s1.playouts < s2.playouts) return 1;
      //              if (s1.playouts > s2.playouts) return -1;
      //              else return 0;
      //            }
      //          });
      //      if (Lizzie.config.leelaversion >= 17 && Lizzie.config.showlcbcolor &&
      // !Lizzie.leelaz.noLcb) {
      //        for (int i = 0; i < tempbest1.size(); i++) {
      //          tempbest1.get(i).equalplayouts = tempbest2.get(i).playouts;
      //        }
      //      }
      for (MoveData move : bestMoves) {
        if (move.playouts > maxPlayouts) maxPlayouts = move.playouts;
        if (move.winrate > maxWinrate) maxWinrate = move.winrate;
        //     if (move.winrate < minWinrate) minWinrate = move.winrate;

      }

      for (int i = bestMoves.size() - 1; i >= 0; i--) {
        MoveData move = bestMoves.get(i);
        boolean isBestMove = bestMoves.get(0) == move;
        // boolean hasMaxWinrate = move.winrate == maxWinrate;

        if (move.playouts == 0) {
          continue; // This actually can happen
        }

        float percentPlayouts = (float) move.playouts / maxPlayouts;
        if (!branchOpt.isPresent()) {

          Optional<int[]> coordsOpt = Board.asCoordinates(move.coordinate);
          if (!coordsOpt.isPresent()) {
            continue;
          }
          int[] coords = coordsOpt.get();

          int suggestionX = scaledMarginWidth + squareWidth * coords[0];
          int suggestionY = scaledMarginHeight + squareHeight * coords[1];
          if (!((Lizzie.config.limitMaxSuggestion > 0
                  && move.order + 1 > Lizzie.config.limitMaxSuggestion)
              || move.order + 1 > 25)) {
            hasDrawBackground[Lizzie.board.getIndex(coords[0], coords[1])] = false;
            continue;
          }
          if (percentPlayouts <= Lizzie.config.minPlayoutRatioForStats
              && !Lizzie.config.showNoSuggCircle) {
            hasDrawBackground[Lizzie.board.getIndex(coords[0], coords[1])] = false;
            continue;
          }
          hasDrawBackground[Lizzie.board.getIndex(coords[0], coords[1])] = true;

          float hue;
          //  boolean hue2;
          if (isBestMove) {
            hue = cyanHue;
            //    hue2 = true;
          } else {
            double fraction = percentPlayouts;
            // Correction to make differences between colors more perceptually linear
            fraction = percentPlayouts;
            // Correction to make differences between colors more perceptually linear
            // fraction *= 2;
            // if (fraction < 0.1) { // red to yellow
            //   fraction = Math.cbrt(fraction * fraction) / 2;
            // }

            // } else { // yellow to green
            //  fraction = 1 - Math.sqrt(2 - fraction) / 2;
            fraction =
                Math.pow(fraction, (double) 1 / Lizzie.config.suggestionColorRatio); // 提升好选点温度
            //  }
            //    hue2 = percentPlayouts > 0.4 ? true : false;
            hue = redHue + (greenHue - redHue) * (float) fraction;
            // else hue = redHue + (greenHue - redHue) * (float) (percentPlayouts * 0.8 + 0.2);
          }

          float saturation = 1.0f;
          float brightness = 0.85f;
          float alpha;
          //            if (!(Lizzie.config.limitMaxSuggestion > 0
          //                    && number > Lizzie.config.limitMaxSuggestion)) {
          //            	  alpha =
          //            			  90
          //                             + (maxAlpha - 90)
          //                                 * max(
          //                                     0,
          //                                     (float)
          //                                                 log(percentPlayouts)
          //                                             / alphaFactor
          //                                         + 1);
          //            }
          //            else
          if (percentPlayouts < 0.05)
            alpha =
                minAlpha
                    + (maxAlpha - minAlpha)
                        * max(0, (float) log(percentPlayouts) / alphaFactor + 1);
          else
            alpha = 32 + (maxAlpha - 32) * max(0, (float) log(percentPlayouts) / alphaFactor + 1);

          Color hsbColor = Color.getHSBColor(hue, saturation, brightness);
          Color color =
              new Color(hsbColor.getRed(), hsbColor.getGreen(), hsbColor.getBlue(), (int) alpha);
          if (!branchOpt.isPresent()) {
            // drawShadow2(g, suggestionX, suggestionY, true, alpha / 255.0f);
            //    g.setColor(new Color(110, 110, 110, 35));
            // drawCircle(g, suggestionX, suggestionY, stoneRadius + 1, 31.5f);
            //            g.drawOval(
            //                suggestionX - stoneRadius - 1,
            //                suggestionY - stoneRadius - 1,
            //                2 * stoneRadius + 2,
            //                2 * stoneRadius + 2);
            //   g.setColor(color);

            g.setColor(new Color(155, 155, 155));
            // g.setColor(color.LIGHT_GRAY);
            drawCircleMin(g, suggestionX, suggestionY, stoneRadius + 1, 28.5f);
            if (isFancyBoard) {
              g.setPaint(paint);
              Composite comp = g.getComposite();
              fillCircle(g, suggestionX, suggestionY, stoneRadius);
              g.setComposite(comp);
            } else {
              g.setColor(noFancyColor);
              Composite comp = g.getComposite();
              fillCircle(g, suggestionX, suggestionY, stoneRadius);
              g.setComposite(comp);
            }
            g.setColor(color);
            fillCircle(g, suggestionX, suggestionY, stoneRadius);
          }
        }
      }
      unImportantCleared = false;
    }
    if (!branchOpt.isPresent()) unImportantSugg = newUnImportantSugg;
    g.dispose();
  }

  /**
   * Calculates the lengths and pixel margins from a given boardLength.
   *
   * @param boardLength go board's length in pixels; must be boardLength >= BOARD_SIZE - 1
   * @return an array containing the three outputs: new boardLength, scaledMargin, availableLength
   */
  private int[] calculatePixelMargins(int boardWidth, int boardHeight) {
    // boardLength -= boardLength*MARGIN/3; // account for the shadows we will draw
    // around the edge
    // of the board
    // if (boardLength < Board.BOARD_SIZE - 1)
    // throw new IllegalArgumentException("boardLength may not be less than " +
    // (Board.BOARD_SIZE - 1) + ", but was " + boardLength);
    //	  if (Board.boardWidth == Board.boardHeight) {
    //	      boardWidth = min(boardWidth, boardHeight);
    //	    }

    int availableWidth = boardWidth * 18 / 19;
    int availableHeight = boardHeight * 18 / 19;
    int scaledMarginWidth = (boardWidth + 20 + (boardType == 0 || boardType == 1 ? 38 : 0)) / 38;
    int scaledMarginHeight = (boardHeight + 20 + (boardType == 0 || boardType == 1 ? 38 : 0)) / 38;
    // decrease boardLength until the availableLength will result in square board
    // intersections
    //    if (Board.boardWidth != Board.boardHeight) {
    //
    //    } else {
    //      boardHeight = boardWidth;
    //      availableHeight = availableWidth;
    //    }
    return new int[] {
      boardWidth,
      scaledMarginWidth,
      availableWidth,
      boardHeight,
      scaledMarginHeight,
      availableHeight
    };
  }

  private BufferedImage cachedGhostShadow2 = null;
  private int cachedR2;
  private int cachedShadowSize2;
  private int cachedStoneCenter2;

  private void drawShadiwCache2() {
    if (!Lizzie.config.showStoneShadow) return;
    if (cachedGhostShadow2 == null) {
      cachedR2 = stoneRadius * 70 / 100;
      cachedShadowSize2 = (int) (cachedR2 * 0.2) == 0 ? 1 : (int) (cachedR2 * 0.2);
      final int width = 2 * (stoneRadius + cachedShadowSize2) + cachedShadowSize2;
      cachedGhostShadow2 = new BufferedImage(width, width, TYPE_INT_ARGB);
      Paint TOP_GRADIENT_PAINT;
      {
        Graphics2D g = (Graphics2D) cachedGhostShadow2.getGraphics();
        TOP_GRADIENT_PAINT =
            new RadialGradientPaint(
                new Point2D.Float(cachedStoneCenter2, cachedStoneCenter2),
                stoneRadius + cachedShadowSize2,
                new float[] {
                  ((float) stoneRadius / (stoneRadius + cachedShadowSize2)) - 0.0001f,
                  ((float) stoneRadius / (stoneRadius + cachedShadowSize2)),
                  1.0f
                },
                new Color[] {
                  new Color(0, 0, 0, 0), new Color(50, 50, 50, 40), new Color(0, 0, 0, 0)
                });
        Paint originalPaint = g.getPaint();
        g.setPaint(TOP_GRADIENT_PAINT);
        fillCircle(g, cachedStoneCenter2, cachedStoneCenter2, stoneRadius + cachedShadowSize2);
        g.setPaint(originalPaint);
      }
    }
  }

  private void drawShadow2(Graphics2D g1, int centerX, int centerY, float shadowStrength) {
    if (!Lizzie.config.showStoneShadow) return;
    g1.drawImage(
        cachedGhostShadow2, centerX - cachedStoneCenter2, centerY - cachedStoneCenter2, null);
  }

  private BufferedImage cachedShadow = null;
  private int cachedR;
  private int cachedShadowSize;
  private int cachedStoneCenter;

  private void drawShadowCache() {
    if (!Lizzie.config.showStoneShadow) return;
    if (cachedShadow == null) {
      cachedR = stoneRadius * Lizzie.config.shadowSize / 100;
      cachedShadowSize = (int) (cachedR * 0.2) == 0 ? 1 : (int) (cachedR * 0.2);
      cachedStoneCenter = stoneRadius + cachedShadowSize;

      final int fartherShadowSize = (int) (cachedR * 0.17) == 0 ? 1 : (int) (cachedR * 0.17);
      final int width = 2 * (stoneRadius + cachedShadowSize) + cachedShadowSize;

      cachedShadow = new BufferedImage(width, width, TYPE_INT_ARGB);

      Paint TOP_GRADIENT_PAINT;
      Paint LOWER_RIGHT_GRADIENT_PAINT;

      {
        Graphics2D g = (Graphics2D) cachedShadow.getGraphics();
        TOP_GRADIENT_PAINT =
            new RadialGradientPaint(
                new Point2D.Float(cachedStoneCenter, cachedStoneCenter),
                stoneRadius + cachedShadowSize,
                new float[] {0.3f, 1.0f},
                new Color[] {new Color(50, 50, 50, 150), new Color(0, 0, 0, 0)});
        LOWER_RIGHT_GRADIENT_PAINT =
            new RadialGradientPaint(
                new Point2D.Float(
                    cachedStoneCenter + cachedShadowSize, cachedStoneCenter + cachedShadowSize),
                stoneRadius + fartherShadowSize,
                new float[] {0.6f, 1.0f},
                new Color[] {new Color(0, 0, 0, 140), new Color(0, 0, 0, 0)});
        Paint originalPaint = g.getPaint();

        g.setPaint(TOP_GRADIENT_PAINT);
        fillCircle(g, cachedStoneCenter, cachedStoneCenter, stoneRadius + cachedShadowSize);
        g.setPaint(LOWER_RIGHT_GRADIENT_PAINT);
        fillCircle(
            g,
            cachedStoneCenter + cachedShadowSize,
            cachedStoneCenter + cachedShadowSize,
            stoneRadius + fartherShadowSize);
        g.setPaint(originalPaint);
      }
    }
  }

  private void drawShadow(Graphics2D g1, int centerX, int centerY) {
    if (!Lizzie.config.showStoneShadow) return;
    g1.drawImage(cachedShadow, centerX - cachedStoneCenter, centerY - cachedStoneCenter, null);
  }

  /** Draws a stone centered at (centerX, centerY) */
  private void drawStone(
      Graphics2D g, Graphics2D gShadow, int centerX, int centerY, Stone color, int x, int y) {
    g.setRenderingHint(KEY_INTERPOLATION, VALUE_INTERPOLATION_BILINEAR);
    g.setRenderingHint(KEY_ANTIALIASING, VALUE_ANTIALIAS_ON);

    if (color.needDrawBlack() || color.needDrawWhite()) {
      boolean isBlack = color.isBlack();
      drawShadow(gShadow, centerX, centerY);
      int size = stoneRadius * 2 + 1;
      g.drawImage(
          getScaleStone(isBlack, size),
          centerX - stoneRadius,
          centerY - stoneRadius,
          size,
          size,
          null);
    }
  }

  private void drawStoneSimple(
      Graphics2D g, Graphics2D gShadow, int centerX, int centerY, Stone color, int x, int y) {
    g.setRenderingHint(KEY_INTERPOLATION, VALUE_INTERPOLATION_BILINEAR);
    g.setRenderingHint(KEY_ANTIALIASING, VALUE_ANTIALIAS_ON);
    if (color.needDrawBlack() || color.needDrawWhite()) {
      boolean isBlack = color.isBlack();
      drawShadow(gShadow, centerX, centerY);
      g.setColor(isBlack ? Color.BLACK : Color.WHITE);
      fillCircle(g, centerX, centerY, stoneRadius);
      if (!isBlack) {
        g.setColor(Color.BLACK);
        g.setStroke(new BasicStroke(Math.max(stoneRadius / 16f, 1f)));
        drawCircle(g, centerX, centerY, stoneRadius);
      }
    }
  }

  /** Get scaled stone, if cached then return cached */
  private BufferedImage getScaleStone(boolean isBlack, int size) {
    BufferedImage stoneImage = isBlack ? cachedBlackStoneImage : cachedWhiteStoneImage;
    if (stoneImage.getWidth() != size || stoneImage.getHeight() != size) {
      stoneImage = new BufferedImage(size, size, TYPE_INT_ARGB);
      Image img = isBlack ? Lizzie.config.theme.blackStone() : Lizzie.config.theme.whiteStone();
      Graphics2D g2 = stoneImage.createGraphics();
      g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
      g2.drawImage(img.getScaledInstance(size, size, java.awt.Image.SCALE_SMOOTH), 0, 0, null);
      g2.dispose();
      if (isBlack) {
        cachedBlackStoneImage = stoneImage;
      } else {
        cachedWhiteStoneImage = stoneImage;
      }
    }
    return stoneImage;
  }

  public BufferedImage getWallpaper() {
    if (cachedWallpaperImage == emptyImage) {
      cachedWallpaperImage = Lizzie.config.theme.background();
    }
    return cachedWallpaperImage;
  }

  /**
   * Draw scale smooth image, enhanced display quality (Not use, for future) This function use the
   * traditional Image.getScaledInstance() method to provide the nice quality, but the performance
   * is poor. Recommended for use in a few drawings
   */
  // public void drawScaleSmoothImage(Graphics2D g, BufferedImage img, int x, int
  // y, int width,
  // int height, ImageObserver observer) {
  // BufferedImage newstone = new BufferedImage(width, height, TYPE_INT_ARGB);
  // Graphics2D g2 = newstone.createGraphics();
  // g2.drawImage(img.getScaledInstance(width, height,
  // java.awt.Image.SCALE_SMOOTH), 0, 0,
  // observer);
  // g2.dispose();
  // g.drawImage(newstone, x, y, width, height, observer);
  // }

  /**
   * Draw scale smooth image, enhanced display quality (Not use, for future) This functions use a
   * multi-step approach to prevent the information loss and produces a much higher quality that is
   * close to the Image.getScaledInstance() and faster than Image.getScaledInstance() method.
   */
  // public void drawScaleImage(Graphics2D g, BufferedImage img, int x, int y, int
  // width, int
  // height, ImageObserver observer) {
  // BufferedImage newstone = (BufferedImage)img;
  // int w = img.getWidth();
  // int h = img.getHeight();
  // do {
  // if (w > width) {
  // w /= 2;
  // if (w < width) {
  // w = width;
  // }
  // }
  // if (h > height) {
  // h /= 2;
  // if (h < height) {
  // h = height;
  // }
  // }
  // BufferedImage tmp = new BufferedImage(w, h, TYPE_INT_ARGB);
  // Graphics2D g2 = tmp.createGraphics();
  // g2.setRenderingHint(KEY_INTERPOLATION,
  // VALUE_INTERPOLATION_BICUBIC);
  // g2.drawImage(newstone, 0, 0, w, h, null);
  // g2.dispose();
  // newstone = tmp;
  // }
  // while (w != width || h != height);
  // g.drawImage(newstone, x, y, width, height, observer);
  // }

  /** Draw texture image */
  public void drawTextureImage(
      Graphics2D g, BufferedImage img, int x, int y, int width, int height, boolean createPaint) {
    if (createPaint) {
      paint = new TexturePaint(img, new Rectangle(0, 0, img.getWidth(), img.getHeight()));
      g.setPaint(paint);
    } else g.setPaint(new TexturePaint(img, new Rectangle(0, 0, img.getWidth(), img.getHeight())));
    g.fill(new Rectangle(x, y, width, height));
  }

  /**
   * Draw stone Markups
   *
   * @param g
   */
  private void drawStoneMarkup(Graphics2D g) {

    BoardData data = Lizzie.board.getHistory().getData();

    data.getProperties()
        .forEach(
            (key, value) -> {
              if (SGFParser.isListProperty(key)) {
                String[] labels = value.split(",");
                for (String label : labels) {
                  String[] moves = label.split(":");
                  int[] move = SGFParser.convertSgfPosToCoord(moves[0]);
                  if (move != null) {
                    Optional<int[]> lastMove =
                        branchOpt.map(b -> b.data.lastMove).orElse(Lizzie.board.getLastMove());
                    if (lastMove.map(m -> !Arrays.equals(move, m)).orElse(true)) {

                      if (Lizzie.frame.floatBoard.isMouseOver(move[0], move[1])) continue;
                      int moveX = x + scaledMarginWidth + squareWidth * move[0];
                      int moveY = y + scaledMarginHeight + squareHeight * move[1];
                      g.setColor(
                          Lizzie.board.getStones()[Board.getIndex(move[0], move[1])].isBlack()
                              ? Color.WHITE
                              : Color.BLACK);
                      g.setStroke(new BasicStroke(2));
                      if ("LB".equals(key) && moves.length > 1) {
                        // Label
                        double labelRadius = stoneRadius * 1.4;
                        drawString(
                            g,
                            moveX,
                            moveY,
                            LizzieFrame.uiFont,
                            moves[1],
                            (float) labelRadius,
                            labelRadius);
                      } else if ("TR".equals(key)) {
                        drawTriangle(g, moveX, moveY, (stoneRadius + 1) * 2 / 3);
                      } else if ("SQ".equals(key)) {
                        drawSquare(g, moveX, moveY, (stoneRadius + 1) / 2);
                      } else if ("CR".equals(key)) {
                        drawCircle(g, moveX, moveY, stoneRadius * 2 / 3);
                      } else if ("MA".equals(key)) {
                        drawMarkX(g, moveX, moveY, (stoneRadius + 1) / 2);
                      }
                    }
                  }
                }
              }
            });
  }

  /** Draws the triangle of a circle centered at (centerX, centerY) with radius $radius$ */
  private void drawTriangle(Graphics2D g, int centerX, int centerY, int radius) {
    int offset = (int) (3.0 / 2.0 * radius / Math.sqrt(3.0));
    int x[] = {centerX, centerX - offset, centerX + offset};
    int y[] = {centerY - radius, centerY + radius / 2, centerY + radius / 2};
    g.drawPolygon(x, y, 3);
  }

  /** Draws the square of a circle centered at (centerX, centerY) with radius $radius$ */
  private void drawSquare(Graphics2D g, int centerX, int centerY, int radius) {
    g.drawRect(centerX - radius, centerY - radius, radius * 2, radius * 2);
  }

  /** Draws the mark(X) of a circle centered at (centerX, centerY) with radius $radius$ */
  private void drawMarkX(Graphics2D g, int centerX, int centerY, int radius) {
    g.drawLine(centerX - radius, centerY - radius, centerX + radius, centerY + radius);
    g.drawLine(centerX - radius, centerY + radius, centerX + radius, centerY - radius);
  }

  /** Fills in a circle centered at (centerX, centerY) with radius $radius$ */
  private void fillCircle(Graphics2D g, int centerX, int centerY, int radius) {
    g.fillOval(
        centerX - radius - (Lizzie.config.isScaled ? 1 : 0),
        centerY - radius - (Lizzie.config.isScaled ? 1 : 0),
        2 * radius + 1 + (Lizzie.config.isScaled ? 2 : 0),
        2 * radius + 1 + (Lizzie.config.isScaled ? 2 : 0));
  }

  private void fillCircleBest(Graphics2D g, int centerX, int centerY, int radius) {
    g.fillOval(centerX - radius - 1, centerY - radius - 1, 2 * radius + 3, 2 * radius + 3);
  }

  /** Draws the outline of a circle centered at (centerX, centerY) with radius $radius$ */
  private void drawCircle(Graphics2D g, int centerX, int centerY, int radius) {
    // g.setStroke(new BasicStroke(radius / 11.5f));
    g.drawOval(centerX - radius, centerY - radius, 2 * radius, 2 * radius);
  }

  private void drawCircleBest(Graphics2D g, int centerX, int centerY, int radius, float f) {
    g.setStroke(new BasicStroke(radius / f));
    g.drawOval(
        centerX - radius - 1 + (Lizzie.config.isScaled ? 1 : 0),
        centerY - radius - 1 + (Lizzie.config.isScaled ? 1 : 0),
        2 * radius + 2 + (Lizzie.config.isScaled ? -1 : 0),
        2 * radius + 2 + (Lizzie.config.isScaled ? -1 : 0));
  }

  private void drawCircle(Graphics2D g, int centerX, int centerY, int radius, float f) {
    g.setStroke(new BasicStroke(radius / f));
    g.drawOval(
        centerX - radius,
        centerY - radius,
        2 * radius + (Lizzie.config.isScaled ? 1 : 0),
        2 * radius + (Lizzie.config.isScaled ? 1 : 0));
  }

  private void drawCircleMin(Graphics2D g, int centerX, int centerY, int radius, float f) {
    g.setStroke(new BasicStroke(Math.min(radius / f, 0.5f)));
    g.drawOval(centerX - radius, centerY - radius, 2 * radius, 2 * radius);
  }
  //  private void drawCircle2(Graphics2D g, int centerX, int centerY, int radius) {
  //
  //    g.setStroke(new BasicStroke(radius / 26.5f));
  //
  //    g.drawOval(centerX - radius, centerY - radius, 2 * radius, 2 * radius);
  //  }

  // private void drawCircle4(Graphics2D g, int centerX, int centerY, int radius)
  // {
  // g.setStroke(new BasicStroke(1f));
  // g.drawOval(centerX - radius, centerY - radius, 2 * radius, 2 * radius);
  // }

  //  private void drawCircle3(Graphics2D g, int centerX, int centerY, int radius) {
  //    g.setStroke(new BasicStroke(radius / 5f));
  //    g.drawOval(centerX - radius, centerY - radius, 2 * radius, 2 * radius);
  //  }
  //
  //  private void drawCircle4(Graphics2D g, int centerX, int centerY, int radius) {
  //    g.setStroke(new BasicStroke(radius / 11f));
  //    g.drawOval(centerX - radius, centerY - radius, 2 * radius, 2 * radius);
  //  }

  private void drawPolygon(Graphics2D g, int centerX, int centerY, int radius) {
    int[] xPoints = {centerX, centerX - (11 * radius / 11), centerX + (11 * radius / 11)};
    int[] yPoints = {
      centerY - (10 * radius / 11), centerY + (8 * radius / 11), centerY + (8 * radius / 11)
    };
    g.fillPolygon(xPoints, yPoints, 3);
  }

  private void drawPolygonSmall(Graphics2D g, int centerX, int centerY, int radius) {
    int[] xPoints = {
      centerX - radius * 16 / 15, centerX - radius * 16 / 15, centerX - radius * 4 / 11
    };
    int[] yPoints = {
      centerY - radius * 16 / 15, centerY - radius * 4 / 11, centerY - radius * 16 / 15
    };
    g.fillPolygon(xPoints, yPoints, 3);
  }

  private void drawPolygonSmallPv(Graphics2D g, int centerX, int centerY, int radius) {
    int[] xPoints = {
      centerX - radius * 9 / 20,
      centerX - radius * 9 / 20,
      centerX - radius * 17 / 40,
      centerX - radius * 11 / 40
    };
    int[] yPoints = {
      (int) (centerY - radius * 0.5),
      (int) (centerY - radius * 0.18),
      (int) (centerY - radius * 0.18),
      (int) (centerY - radius * 0.5)
    };
    g.fillPolygon(xPoints, yPoints, 4);
  }

  /**
   * Draws a string centered at (x, y) of font $fontString$, whose contents are $string$. The
   * maximum/default fontsize will be $maximumFontHeight$, and the length of the drawn string will
   * be at most maximumFontWidth. The resulting actual size depends on the length of $string$.
   * aboveOrBelow is a param that lets you set: aboveOrBelow = -1 -> y is the top of the string
   * aboveOrBelow = 0 -> y is the vertical center of the string aboveOrBelow = 1 -> y is the bottom
   * of the string
   */
  private void drawStringFor3row(
      Graphics2D g,
      int x,
      int y,
      Font fontBase,
      int style,
      String string,
      float maximumFontHeight,
      double maximumFontWidth) {

    Font font = makeFont(fontBase, style);

    // set maximum size of font
    FontMetrics fm = g.getFontMetrics(font);
    font = font.deriveFont((float) (font.getSize2D() * maximumFontWidth / fm.stringWidth(string)));
    font = font.deriveFont(min(maximumFontHeight, font.getSize()));
    g.setFont(font);
    fm = g.getFontMetrics(font);
    g.drawString(string, x - fm.stringWidth(string) / 2, y);
  }

  private Font drawString(
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
    //    if(font.getSize()<15)
    //    	font=new Font(font.getName(),Font.BOLD,font.getSize());
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
    g.drawString(string, x - fm.stringWidth(string) / 2, y + height / 2 + verticalOffset);
    return font;
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

  private void drawStringBold(
      Graphics2D g,
      int x,
      int y,
      Font fontBase,
      String string,
      float maximumFontHeight,
      double maximumFontWidth) {
    drawString(g, x, y, fontBase, Font.BOLD, string, maximumFontHeight, maximumFontWidth, 0);
  }

  /** @return a font with kerning enabled */
  private Font makeFont(Font fontBase, int style) {
    Font font = fontBase.deriveFont(style, 100);
    Map<TextAttribute, Object> atts = new HashMap<>();
    atts.put(TextAttribute.KERNING, TextAttribute.KERNING_ON);
    return font.deriveFont(atts);
  }

  private int[] calculatePixelMargins() {
    return calculatePixelMargins(boardWidth, boardHeight);
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
    this.boardWidth = boardWidth;
    this.boardHeight = boardHeight;
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
    int marginWidth;
    int marginHeight;
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
    if (Board.isValid(x, y)) return Optional.of(new int[] {x, y});
    else return Optional.empty();
  }

  public Optional<int[]> convertScreenToCoordinatesForSelect(int x, int y) {
    int marginWidth; // the pixel width of the margins
    //  int boardWidthWithoutMargins; // the pixel width of the game board without margins
    int marginHeight; // the pixel height of the margins
    //   int boardHeightWithoutMargins; // the pixel height of the game board without margins

    // calculate a good set of boardLength, scaledMargin, and
    // boardLengthWithoutMargins to use
    // int[] calculatedPixelMargins = calculatePixelMargins();
    // setBoardLength(calculatedPixelMargins[0], calculatedPixelMargins[3]);
    marginWidth = this.scaledMarginWidth;
    marginHeight = this.scaledMarginHeight;
    if (x > this.x + boardWidth - marginHeight) x = this.x + boardWidth - marginHeight;
    if (x < this.x + marginHeight) x = this.x + marginHeight;
    if (y > this.y + boardHeight - marginWidth) y = this.y + boardHeight - marginWidth;
    if (y < this.y + marginWidth) y = this.y + marginWidth;
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

  public Optional<int[]> convertScreenToCoordinatesForSelect(int x1, int x2, int y1, int y2) {
    int marginWidth; // the pixel width of the margins
    //  int boardWidthWithoutMargins; // the pixel width of the game board without margins
    int marginHeight; // the pixel height of the margins
    //   int boardHeightWithoutMargins; // the pixel height of the game board without margins

    // calculate a good set of boardLength, scaledMargin, and
    // boardLengthWithoutMargins to use
    // int[] calculatedPixelMargins = calculatePixelMargins();
    // setBoardLength(calculatedPixelMargins[0], calculatedPixelMargins[3]);
    marginWidth = this.scaledMarginWidth;
    marginHeight = this.scaledMarginHeight;
    if (x1 > this.x + boardWidth - marginHeight) x1 = this.x + boardWidth - marginHeight;
    if (x1 < this.x + marginHeight) x1 = this.x + marginHeight - 1;
    if (y1 > this.y + boardHeight - marginWidth) y1 = this.y + boardHeight - marginWidth;
    if (y1 < this.y + marginWidth) y1 = this.y + marginWidth - 1;
    if (x2 > this.x + boardWidth - marginHeight) x2 = this.x + boardWidth - marginHeight;
    if (x2 < this.x + marginHeight) x2 = this.x + marginHeight - 1;
    if (y2 > this.y + boardHeight - marginWidth) y2 = this.y + boardHeight - marginWidth;
    if (y2 < this.y + marginWidth) y2 = this.y + marginWidth - 1;
    // transform the pixel coordinates to board coordinates
    x1 = squareWidth == 0 ? 0 : Math.floorDiv(x1 - this.x - marginWidth + squareWidth, squareWidth);
    y1 =
        squareHeight == 0
            ? 0
            : Math.floorDiv(y1 - this.y - marginHeight + squareHeight, squareHeight);
    x2 = squareWidth == 0 ? 0 : Math.floorDiv(x2 - this.x - marginWidth, squareWidth);
    y2 = squareHeight == 0 ? 0 : Math.floorDiv(y2 - this.y - marginHeight, squareHeight);

    // return these values if they are valid board coordinates
    return Board.isValid(x1, y1) && Board.isValid(x2, y2)
        ? Optional.of(new int[] {x1, y1, x2, y2})
        : Optional.empty();
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

    // re-center board
    // setLocation(x + (boardWidth0 - boardWidth) / 2, y + (boardHeight0 -
    // boardHeight) / 2);
  }

  private static int calculateSquareWidth(int availableWidth) {
    return availableWidth / (Board.boardWidth - 1);
  }

  private static int calculateSquareHeight(int availableHeight) {
    return availableHeight / (Board.boardHeight - 1);
  }

  private boolean isShowingRawBoard() {
    return (displayedBranchLength == SHOW_RAW_BOARD || displayedBranchLength == 0);
  }

  private int maxBranchMoves(boolean forDrawMove) {
    if (forDrawMove && displayedBranchLength == 1) {
      return 999;
    }
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
    return isShowingBranch;
  }

  public void notShowingBranch() {
    showingBranch = false;
  }

  public void setDisplayedBranchLength(int n) {
    displayedBranchLength = n;
  }

  public int getDisplayedBranchLength() {
    return displayedBranchLength;
  }

  public int getReplayBranch() {

    return mouseOveredMove().isPresent() ? mouseOveredMove().get().variation.size() : 0;
  }

  public int getBranchLength() {

    return branchOpt.isPresent() ? branchOpt.get().length : 0;
  }

  public boolean incrementDisplayedBranchLength(int n) {
    if (isShowingBranch && displayedBranchLength == SHOW_NORMAL_BOARD) {
      displayedBranchLength = 2;
      return true;
    }
    switch (displayedBranchLength) {
      case 1:
        if (Lizzie.config.autoReplayBranch) displayedBranchLength = 2;
        else if (!isShowingBranch && n == 1) displayedBranchLength = 256;
        else if (n == 1) displayedBranchLength = 2;
        return true;
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

  public void startNormalBoard() {
    setDisplayedBranchLength(SHOW_NORMAL_BOARD);
    // branchOpt = Optional.empty();
  }

  public void clearBranch() {
    isShowingBranch = false;
    showingBranch = false;
  }

  public boolean isInside(int x1, int y1) {
    return x <= x1 && x1 < x + boardWidth && y <= y1 && y1 < y + boardHeight;
  }

  private boolean showCoordinates() {
    return Lizzie.config.showCoordinates;
  }

  public boolean isShowingNormalBoard() {
    return displayedBranchLength == SHOW_NORMAL_BOARD;
  }

  public boolean isShowingUnImportantBoard() {
    return displayedBranchLength == 1 || displayedBranchLength == 256;
  }

  private String getSuggestionInfoRow1(String winrate, String playouts, String scoreLead) {
    if (Lizzie.config.suggestionInfoWinrate == 1) return winrate;
    else if (Lizzie.config.suggestionInfoPlayouts == 1) return playouts;
    else if (Lizzie.config.suggestionInfoScoreLead == 1) return scoreLead;
    return winrate;
  }

  private String getSuggestionInfoRow2(String winrate, String playouts, String scoreLead) {
    if (Lizzie.config.suggestionInfoPlayouts == 2) return playouts;
    else if (Lizzie.config.suggestionInfoWinrate == 2) return winrate;
    else if (Lizzie.config.suggestionInfoScoreLead == 2) return scoreLead;
    return playouts;
  }

  private String getSuggestionInfoRow3(String winrate, String playouts, String scoreLead) {
    if (Lizzie.config.suggestionInfoScoreLead == 3) return scoreLead;
    else if (Lizzie.config.suggestionInfoWinrate == 3) return winrate;
    else if (Lizzie.config.suggestionInfoPlayouts == 3) return playouts;
    return scoreLead;
  }

  private Font drawStringForOrder(
      Graphics2D g,
      int x,
      int y,
      Font fontBase,
      int style,
      String string,
      float maximumFontHeight,
      double maximumFontWidth,
      int aboveOrBelow,
      boolean blackToPlay) {

    Font font = makeFont(fontBase, style);

    // set maximum size of font
    FontMetrics fm = g.getFontMetrics(font);
    font = font.deriveFont((float) (font.getSize2D() * maximumFontWidth / fm.stringWidth(string)));
    font = font.deriveFont(min(maximumFontHeight, font.getSize()));
    //    if(font.getSize()<15)
    //    	font=new Font(font.getName(),Font.BOLD,font.getSize());
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
    int x1 = x - fm.stringWidth(string) / 2;
    int y1 = y + height / 2 + verticalOffset;
    int width = fm.stringWidth(string);
    g.setColor(
        Lizzie.config.whiteSuggestionOrderWhite && !blackToPlay
            ? new Color(155, 118, 36)
            : Color.ORANGE);
    g.fillRect(x1, y1 - height, width, height + Math.max(1, height / 12));
    g.setColor(Lizzie.config.whiteSuggestionOrderWhite && !blackToPlay ? Color.WHITE : Color.BLACK);
    g.drawString(string, x1, y1);
    return font;
  }

  public void addSuggestionAsBranch() {
    mouseOveredMove()
        .ifPresent(
            m -> {
              if (m.variation.size() > 0) {
                if (Lizzie.board.getHistory().getCurrentHistoryNode().numberOfChildren() == 0) {
                  Stone color =
                      Lizzie.board.getHistory().isBlacksTurn() ? Stone.BLACK : Stone.WHITE;

                  Lizzie.board.getHistory().pass(color, false, true);
                  Lizzie.board.getHistory().previous();
                }
                for (int i = 0; i < m.variation.size(); i++) {
                  Stone color =
                      Lizzie.board.getHistory().isBlacksTurn() ? Stone.BLACK : Stone.WHITE;
                  Optional<int[]> coordOpt = Board.asCoordinates(m.variation.get(i));
                  if (!coordOpt.isPresent()
                      || !Board.isValid(coordOpt.get()[0], coordOpt.get()[1])) {
                    break;
                  }
                  int[] coord = coordOpt.get();
                  Lizzie.board.getHistory().place(coord[0], coord[1], color, i == 0);
                }
                Lizzie.board.getHistory().toBranchTop();
                Lizzie.frame.reRenderTree();
                Lizzie.frame.refresh();
              }
            });
  }
}
