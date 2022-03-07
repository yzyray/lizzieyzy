package featurecat.lizzie.gui;

import static java.awt.RenderingHints.*;
import static java.awt.image.BufferedImage.TYPE_INT_ARGB;
import static java.lang.Math.log;
import static java.lang.Math.max;
import static java.lang.Math.min;
import static java.lang.Math.round;

import featurecat.lizzie.Lizzie;
import featurecat.lizzie.analysis.Branch;
import featurecat.lizzie.analysis.EngineManager;
import featurecat.lizzie.analysis.Leelaz;
import featurecat.lizzie.analysis.MoveData;
import featurecat.lizzie.rules.Board;
import featurecat.lizzie.rules.BoardData;
import featurecat.lizzie.rules.BoardHistoryNode;
import featurecat.lizzie.rules.GroupInfo;
import featurecat.lizzie.rules.NodeInfo;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BoardRenderer {
  // Percentage of the boardLength to offset before drawing black lines
  // private static final double MARGIN = 0.03;
  private final ResourceBundle resourceBundle = Lizzie.resourceBundle;
  // private static final double MARGIN_WITH_COORDINATES = 0.06;
  private static final double STARPOINT_DIAMETER = 0.015;
  private static final BufferedImage emptyImage = new BufferedImage(1, 1, TYPE_INT_ARGB);

  private int x, y;
  public int boardWidth = 1, boardHeight = 1;
  public boolean emptyName = true;
  public boolean changedName = false;
  private boolean cachedShowCoords = false;

  // private JSONObject uiConfig, uiPersist;
  private int scaledMarginWidth, availableWidth, squareWidth, stoneRadius;
  private int scaledMarginHeight, availableHeight, squareHeight;
  public Optional<Branch> branchOpt = Optional.empty();
  private List<MoveData> bestMoves = new ArrayList<MoveData>();
  private ArrayList<Double> estimateArray;
  private ArrayList<Double> preEstimateArray;
  private List<int[]> nextCoords;
  private MoveData mouseOverTemp = new MoveData();
  private BoardHistoryNode mouseOverTempNode;

  private int mouseOverOrder = -1;
  private boolean isMouseOverStoneBlack;

  private boolean isShowingBranch = false;
  private String mouseOverCoords = "";
  private Branch branch;

  private BufferedImage cachedBackgroundImage = emptyImage;
  private boolean cachedIsBigMargin = false;
  private int cachedBoardWidth = 0, cachedBoardHeight = 0;
  private BufferedImage cachedStonesImage = emptyImage;
  private BufferedImage cachedStonesImagedraged = emptyImage;
  private BufferedImage blockimage = emptyImage;
  private BufferedImage selectImage = emptyImage;
  private BufferedImage selectImageAll = emptyImage;
  boolean needDrawSelectImage = false;
  boolean needDrawSelectImageAll = false;
  private BufferedImage unImportantSugg = emptyImage;
  private boolean unImportantCleared = false;
  // private BufferedImage importantSugg = emptyImage;
  // private ArrayList<BufferedImage> cachedSelectImage = new ArrayList<BufferedImage>();
  private boolean hasBlockimage = false;
  private BufferedImage kataEstimateImage = emptyImage;
  private BufferedImage estimateImage = emptyImage;

  private BufferedImage scoreImage = emptyImage;

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
  public boolean[] hasDrawBackground = new boolean[Board.boardHeight * Board.boardWidth];
  private boolean isFancyBoard = true;
  private Color noFancyColor;
  private boolean changedSize = false;
  private boolean isIndependBoard = false;
  //  public boolean reverseBestmoves = false;
  private int maxAlpha = 240;
  // int number = 1;
  TexturePaint paint;
  List<String> variation = new ArrayList<String>();
  List<String> cachedVariation = new ArrayList<String>();
  private int cachedDisplayedBranchLengthFroBranch;
  List<String> pvVistis;
  private int drawUnimportantSuggCount = 100;
  // private boolean drawBadMoveHint = false;
  public int boardIndex = 0;
  private boolean shouldIgnoreBestMove = false;
  private int ignoreBestMoveX = -1;
  private int ignoreBestMoveY = -1;
  private boolean isShowingNextMoveBlunder = false;
  private int nextMoveX = -2;
  private int nextMoveY = -2;
  private List<String> nextPv;
  private List<String> nextPvVisits;
  private int nextVisits;
  private int nextPvX = -1;
  private int nextPvY = -1;
  private boolean isMouseOverNextBlunder = false;
  private boolean showBlunderWinrate;
  private boolean showBlunderScore;
  private String nextBlunderWinrate;
  private String nextBlunderScore;

  public void setOrder(int index) {
    // TODO Auto-generated method stub
    boardIndex = index;
  }

  public BoardRenderer(boolean isIndependBoard) {
    this.isIndependBoard = isIndependBoard;
  }

  /** Draw a go board */
  public void draw(Graphics2D g) {
    //    if (Lizzie.config.isScaled) {
    //      drawNoSuggestion(g);
    //      return;
    //    }
    // setupSizeParameters();

    // Stopwatch timer = new Stopwatch();
    drawGoban(g);
    nextCoords = new ArrayList<int[]>();
    if (Board.boardWidth <= 3) {
      int oriStoneRadius = stoneRadius;
      stoneRadius = stoneRadius / 2;
      if (!Lizzie.config.isThinkingMode() || (Lizzie.config.isThinkingMode() && boardIndex != 2)) {
        if (Lizzie.frame.isShowingHeatmap
            && !Lizzie.frame.isAnaPlayingAgainstLeelaz
            && !Lizzie.leelaz.isZen) drawRawWinrate(g);
        else if (Lizzie.frame.isShowingPolicy
            && !Lizzie.leelaz.isKatago
            && !EngineManager.isEmpty
            && !Lizzie.leelaz.isZen) drawRawWinrate(g);
        //  else if (Lizzie.config.showNameInBoard) drawName(g);
      }
      stoneRadius = oriStoneRadius;
    } else {
      if (!Lizzie.config.isThinkingMode() || (Lizzie.config.isThinkingMode() && boardIndex != 2)) {
        if (Lizzie.frame.isShowingHeatmap
            && !Lizzie.frame.isAnaPlayingAgainstLeelaz
            && !Lizzie.leelaz.isZen) drawRawWinrate(g);
        else if (Lizzie.frame.isShowingPolicy
            && !Lizzie.leelaz.isKatago
            && !EngineManager.isEmpty
            && !Lizzie.leelaz.isZen) drawRawWinrate(g);
        else if (Lizzie.config.showNameInBoard) drawName(g);
      }
    }

    if (!Lizzie.frame.isInScoreMode) drawBranch();
    else isShowingBranch = false;

    if (!isShowingBranch) drawStones();
    drawEstimate();
    renderImages(g);

    if (Lizzie.config.allowMoveNumber == 0
        && !Lizzie.frame.isInPlayMode()
        && !EngineManager.isEngineGame
        && Lizzie.config.moveRankMarkLastMove >= 0) {
      squareWidth = squareWidth * 2;
      availableWidth = availableWidth * 2;
      drawMoveRankMark(g);
      availableWidth = availableWidth / 2;
      squareWidth = squareWidth / 2;
      if (isShowingBranch) drawMoveNumbers(g);
    } else drawMoveNumbers(g);

    if (Lizzie.frame.isInScoreMode) {
    } else {
      if (!Lizzie.config.isShowingMarkupTools) drawStoneMarkup(g);
      this.shouldIgnoreBestMove = false;
      if (!isMouseOverNextBlunder) isShowingNextMoveBlunder = false;
      if (!isShowingRawBoard()) {
        if (Lizzie.config.showNextMoves && !isShowingBranch) {
          drawNextMoves(g);
        }
        // timer.lap("movenumbers");
        if (Lizzie.config.showBestMovesNow()) {
          if ((Lizzie.board.getHistory().isBlacksTurn() && Lizzie.config.showBlackCandidates)
              || (!Lizzie.board.getHistory().isBlacksTurn() && Lizzie.config.showWhiteCandidates)) {
            if (!Lizzie.frame.isShowingHeatmap && !Lizzie.frame.isShowingPolicy) {
              drawUnimportantSuggCount = drawUnimportantSuggCount + 1;
              if (drawUnimportantSuggCount > 100 / getInterval()) {
                drawLeelazSuggestionsUnimportant();
                drawUnimportantSuggCount = 0;
              }
              renderImagesUnimportant(g);
            }
            squareWidth = squareWidth * 2;
            availableWidth = availableWidth * 2;
            drawLeelazSuggestions(g);
            availableWidth = availableWidth / 2;
            squareWidth = squareWidth / 2;
          } else if (Lizzie.frame.isAnaPlayingAgainstLeelaz && Lizzie.config.UsePureNetInGame) {

            if (Lizzie.frame.playerIsBlack && !Lizzie.board.getHistory().isBlacksTurn()) {
              squareWidth = squareWidth * 2;
              availableWidth = availableWidth * 2;
              drawLeelazSuggestions(g);
              availableWidth = availableWidth / 2;
              squareWidth = squareWidth / 2;
            } else if (!Lizzie.frame.playerIsBlack && Lizzie.board.getHistory().isBlacksTurn()) {
              squareWidth = squareWidth * 2;
              availableWidth = availableWidth * 2;
              drawLeelazSuggestions(g);
              availableWidth = availableWidth / 2;
              squareWidth = squareWidth / 2;
            }
          }
        } else if (Lizzie.frame.isAnaPlayingAgainstLeelaz && Lizzie.config.UsePureNetInGame) {
          if (Lizzie.frame.playerIsBlack && !Lizzie.board.getHistory().isBlacksTurn()) {
            squareWidth = squareWidth * 2;
            availableWidth = availableWidth * 2;
            drawLeelazSuggestions(g);
            availableWidth = availableWidth / 2;
            squareWidth = squareWidth / 2;
          } else if (!Lizzie.frame.playerIsBlack && Lizzie.board.getHistory().isBlacksTurn()) {
            squareWidth = squareWidth * 2;
            availableWidth = availableWidth * 2;
            drawLeelazSuggestions(g);
            availableWidth = availableWidth / 2;
            squareWidth = squareWidth / 2;
          }
        }
      }
      if (isMouseOverNextBlunder) drawNextBlunderFirstMove(g);
      if (Lizzie.config.isShowingMarkupTools) drawStoneMarkup(g);
      if (Lizzie.frame.clickbadmove != LizzieFrame.outOfBoundCoordinate)
        drawbadstone(Lizzie.frame.clickbadmove[0], Lizzie.frame.clickbadmove[1], g);
      g.drawImage(cachedStonesShadowImagedraged, x, y, null);
      g.drawImage(cachedStonesImagedraged, x, y, null);
      // timer.lap("leelaz");
    }
    // timer.print();
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
          drawKataEstimateBySize(estimateArray, shouldShowPreviousBestMoves());
        } else {
          drawKataEstimateByTransparent(estimateArray, shouldShowPreviousBestMoves(), false);
        }
        hasDraw = true;
      } else if (preEstimateArray != null) {
        if (Lizzie.config.showKataGoEstimateBySize) {
          drawKataEstimateBySize(preEstimateArray, true);
        } else {
          drawKataEstimateByTransparent(preEstimateArray, true, false);
        }
        hasDraw = true;
      }
    }
    if (!hasDraw && isShowingEstimate) {
      removeKataEstimateImage();
    }
    isShowingEstimate = hasDraw;
  }

  private void drawPass(Graphics2D g, Board board, Optional<int[]> lastMoveOpt) {
    if (!lastMoveOpt.isPresent() && board.getData().moveNumber != 0 && !board.getData().dummy) {
      g.setColor(
          board.getData().blackToPlay ? new Color(255, 255, 255, 80) : new Color(0, 0, 0, 80));
      g.fillOval(
          x + boardWidth / 2 - stoneRadius * 5 / 2,
          y + boardHeight / 2 - stoneRadius * 5 / 2,
          stoneRadius * 5,
          stoneRadius * 5);
      g.setColor(
          board.getData().blackToPlay ? new Color(0, 0, 0, 200) : new Color(255, 255, 255, 200));
      drawString(
          g,
          x + boardWidth / 2,
          y + boardHeight / 2,
          LizzieFrame.uiFont,
          resourceBundle.getString("BoardRenderer.pass"),
          stoneRadius * 3,
          stoneRadius * 9 / 2);
    }
  }

  private void drawMoveRankMark(Graphics2D g) {
    Board board = Lizzie.board;
    Optional<int[]> lastMoveOpt = branchOpt.map(b -> b.data.lastMove).orElse(board.getLastMove());
    g.setRenderingHint(KEY_ANTIALIASING, VALUE_ANTIALIAS_ON);
    drawPass(g, board, lastMoveOpt);
    BoardHistoryNode node = Lizzie.board.getHistory().getCurrentHistoryNode();
    int limit = Lizzie.config.moveRankMarkLastMove;
    boolean shouldLimit = limit > 0;
    int[] moveNumberList = node.getData().moveNumberList;
    boolean isSub = this.boardIndex == 1;
    int[] drawList = new int[Board.boardWidth * Board.boardHeight];
    while (node.previous().isPresent()) {
      if (shouldLimit) {
        limit--;
        if (limit < 0) break;
      }
      if (node.getData().lastMove.isPresent()) {
        int[] coords = node.getData().lastMove.get();
        int index = Board.getIndex(coords[0], coords[1]);
        if (branchOpt.isPresent()) {
          if (branchOpt.get().isNewStone[index] || branchOpt.get().data.stones[index].isEmpty()) {
            node = node.previous().get();
            continue;
          }
        }
        int moveNumber =
            Lizzie.frame.isTrying
                ? -node.getData().moveNumber
                : node.getData().moveMNNumber > -1
                    ? node.getData().moveMNNumber
                    : node.getData().moveNumber;
        if (node == Lizzie.board.getHistory().getCurrentHistoryNode()) {
          int markX = x + scaledMarginWidth + squareWidth / 2 * coords[0];
          int markY = y + scaledMarginHeight + squareHeight * coords[1];
          int playouts = isSub ? node.getData().getPlayouts2() : node.getData().getPlayouts();
          int playoutsPrevious =
              isSub
                  ? node.previous().get().getData().getPlayouts2()
                  : node.previous().get().getData().getPlayouts();
          if (playouts > 0 && playoutsPrevious > 0) {
            if (moveNumberList[index] == moveNumber) {
              if (node.isBest) drawMoveRankMarkCircle(g, markX, markY, stoneRadius, 0, 0, true);
              else
                drawMoveRankMarkCircle(
                    g,
                    markX,
                    markY,
                    stoneRadius,
                    Lizzie.config.useWinLossInMoveRank
                        ? (isSub
                            ? Lizzie.board.lastWinrateDiff2(node)
                            : Lizzie.board.lastWinrateDiff(node))
                        : 0,
                    Lizzie.config.useScoreLossInMoveRank
                        ? (isSub
                            ? Lizzie.board.lastScoreMeanDiff2(node)
                            : Lizzie.board.lastScoreMeanDiff(node))
                        : 0,
                    true);
              drawList[index] = 1;
            }
          }
          g.setColor(node.getData().lastMoveColor.isWhite() ? Color.BLACK : Color.WHITE);
          switch (Lizzie.config.stoneIndicatorType) {
            case 0:
              g.setStroke(new BasicStroke((int) Math.round(squareWidth * 0.22f) / 5f));
              drawPolygonCircle(g, markX, markY, stoneRadius);
              break;
            default:
              drawCircle(g, markX, markY, (int) Math.round(squareWidth * 0.22f), 5f);
              if (Lizzie.config.moveRankMarkLastMove > 1
                  || Lizzie.config.moveRankMarkLastMove == 0) {
                g.setColor(Color.RED);
                drawPolygonSmall(g, markX, markY, stoneRadius);
              }
              break;
          }
        } else {
          NodeInfo nodeInfo =
              isSub ? node.previous().get().nodeInfo2 : node.previous().get().nodeInfo;
          if (nodeInfo.analyzed && nodeInfo.previousPlayouts > 0) {
            if (moveNumberList[index] == moveNumber && drawList[index] != 1) {
              double winrateDiff =
                  Lizzie.config.useWinLossInMoveRank ? nodeInfo.getWinrateDiff() : 0;
              double scoreDiff =
                  Lizzie.config.useScoreLossInMoveRank ? nodeInfo.getScoreMeanDiff() : 0;
              int markX = x + scaledMarginWidth + squareWidth / 2 * coords[0];
              int markY = y + scaledMarginHeight + squareHeight * coords[1];
              drawMoveRankMarkCircle(g, markX, markY, stoneRadius, winrateDiff, scoreDiff, false);
              drawList[index] = 1;
            }
          }
        }
      }
      node = node.previous().get();
    }
  }

  private void drawMoveRankMarkCircle(
      Graphics2D g,
      int markX,
      int markY,
      int stoneRadius2,
      double winrateDiff,
      double scoreDiff,
      boolean isLastMove) {
    float radiusF = 0.1f;
    if (winrateDiff <= Lizzie.config.winLossThreshold5
        || scoreDiff <= Lizzie.config.scoreLossThreshold5) {
      g.setColor(new Color(155, 25, 150));
      radiusF = 0.19f;
    } else if (winrateDiff <= Lizzie.config.winLossThreshold4
        || scoreDiff <= Lizzie.config.scoreLossThreshold4) {
      g.setColor(new Color(208, 16, 19));
      radiusF = 0.1675f;
    } else if (winrateDiff <= Lizzie.config.winLossThreshold3
        || scoreDiff <= Lizzie.config.scoreLossThreshold3) {
      g.setColor(new Color(200, 140, 50));
      radiusF = 0.145f;
    } else if (winrateDiff <= Lizzie.config.winLossThreshold2
        || scoreDiff <= Lizzie.config.scoreLossThreshold2) {
      g.setColor(new Color(180, 180, 0));
      radiusF = 0.1225f;
    } else if (winrateDiff <= Lizzie.config.winLossThreshold1
        || scoreDiff <= Lizzie.config.scoreLossThreshold1) g.setColor(new Color(140, 202, 34));
    else g.setColor(new Color(0, 180, 0));

    if (isLastMove) {
      switch (Lizzie.config.stoneIndicatorType) {
        case 0:
          drawPolygon(g, markX, markY, stoneRadius);
          break;
        default:
          int radius = (int) Math.round(squareWidth * 0.22f);
          fillCircle(g, markX, markY, radius);
          break;
      }
    } else {
      int radius = (int) Math.round(squareWidth * radiusF);
      fillCircle(g, markX, markY, radius);
    }
  }

  private boolean shouldShowPreviousBestMoves() {
    return (EngineManager.isEngineGame && Lizzie.config.showPreviousBestmovesInEngineGame);
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
  //  public int[] availableLength(int boardWidth, int boardHeight, boolean showCoordinates) {
  //    int[] calculatedPixelMargins = calculatePixelMargins(boardWidth, boardHeight,
  // showCoordinates);
  //    return (calculatedPixelMargins != null && calculatedPixelMargins.length >= 6)
  //        ? calculatedPixelMargins
  //        : new int[] {boardWidth, 0, boardWidth, boardHeight, 0, boardHeight};
  //  }

  /** Calculate good values for boardLength, scaledMargin, availableLength, and squareLength */
  public void setupSizeParameters(boolean isBigMargin) {
    int boardWidth0 = boardWidth;
    int boardHeight0 = boardHeight;

    int[] calculatedPixelMargins = calculatePixelMargins(isBigMargin);
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
    stoneRadius = max(squareWidth, squareHeight) < 4 ? 1 : max(squareWidth, squareHeight) - 1;

    // re-center board
    setLocation(x + (boardWidth0 - boardWidth) / 2, y + (boardHeight0 - boardHeight) / 2);
  }

  private void drawRawWinrate(Graphics2D g0) {
    g0.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    // Font font = new Font(Lizzie.config.fontName, Font.PLAIN, (int) (Math.min(28,
    // this.scaledMarginHeight*63/100)));
    //   g0.setFont(font);
    String wr = "";
    Leelaz leelaz = this.boardIndex == 1 && Lizzie.leelaz2 != null ? Lizzie.leelaz2 : Lizzie.leelaz;
    if (leelaz.isKatago)
      wr =
          resourceBundle.getString("BoardRenderer.pureNetWhiteWinrate") // "纯网络:白胜率 "
              + String.format(Locale.ENGLISH, "%.1f", leelaz.heatwinrate * 100)
              + " "
              + resourceBundle.getString("BoardRenderer.whiteScore") // " 白目差 "
              + String.format(Locale.ENGLISH, "%.1f", leelaz.heatScore)
              + " "
              + resourceBundle.getString("BoardRenderer.symmetry") // " 对称类型 "
              + leelaz.symmetry;
    else if (leelaz.heatwinrate >= 0) {
      wr =
          resourceBundle.getString("BoardRenderer.pureNetWinrate")
              + String.format(Locale.ENGLISH, "%.1f", leelaz.heatwinrate * 100);
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

  private boolean isKoreanName(String input) {
    final Pattern p =
        Pattern.compile(
            "^[\\u1100-\\u11ff\\uac00-\\ud7af\\u3130–\\u318F\\u3200–\\u32FF\\uA960–\\uA97F\\uD7B0–\\uD7FF\\uFF00–\\uFFEF\\w\\s]+$");
    Matcher m = p.matcher(input);
    return m.matches();
  }

  private void drawName(Graphics2D g0) {
    String black = Lizzie.board.getHistory().getGameInfo().getPlayerBlack();
    String white = Lizzie.board.getHistory().getGameInfo().getPlayerWhite();
    if (black.length() == 0 && white.length() == 0) {
      if (!emptyName) {
        emptyName = true;
        changedName = true;
        Lizzie.frame.refresh();
      }
      return;
    }

    g0.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    boolean isKoreanBlack = isKoreanName(black);
    boolean isKoreanWhite = isKoreanName(white);
    g0.setFont(
        new Font(
            isKoreanWhite || isKoreanBlack ? "Malgun Gothic" : Lizzie.config.uiFontName,
            Font.PLAIN,
            (int) (Math.min(28, this.scaledMarginHeight * 53 / 100))));

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
    if (EngineManager.isEngineGame && EngineManager.engineGameInfo.isBatchGame) {
      if (EngineManager.engineGameInfo.firstEngineIndex
          == EngineManager.engineGameInfo.blackEngineIndex) {
        black = black + " " + EngineManager.engineGameInfo.getFirstEngineWins();
        white = EngineManager.engineGameInfo.getSecondEngineWins() + " " + white;
      } else {
        black = black + " " + EngineManager.engineGameInfo.getSecondEngineWins();
        white = EngineManager.engineGameInfo.getFirstEngineWins() + " " + white;
      }
    }
    lengthB = g0.getFontMetrics().stringWidth(black);
    lengthW = g0.getFontMetrics().stringWidth(white);

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

    if (black.length() > 0) {
      Font font =
          new Font(
              isKoreanBlack ? "Malgun Gothic" : Lizzie.config.uiFontName,
              Font.PLAIN,
              (int) (Math.min(28, this.scaledMarginHeight * 53 / 100)));
      g0.setFont(font);
      FontRenderContext frcb = g0.getFontRenderContext();

      TextLayout tlb = new TextLayout(black, font, frcb);

      Shape shab =
          tlb.getOutline(
              AffineTransform.getTranslateInstance(
                  x + boardWidth / 2 - lengthB - scaledMarginWidth * 3 / 5,
                  y - scaledMarginHeight + boardHeight + stoneRadius * 13 / 8 + (height + 2) / 4));
      g0.setColor(Color.BLACK);
      g0.fill(shab);
    }

    g0.setColor(Color.WHITE);

    if (white.length() > 0) {
      Font font =
          new Font(
              isKoreanWhite ? "Malgun Gothic" : Lizzie.config.uiFontName,
              Font.PLAIN,
              (int) (Math.min(28, this.scaledMarginHeight * 53 / 100)));
      g0.setFont(font);
      FontRenderContext frc = g0.getFontRenderContext();
      TextLayout tl = new TextLayout(white, font, frc);
      Shape sha =
          tl.getOutline(
              AffineTransform.getTranslateInstance(
                  x + boardWidth / 2 + scaledMarginWidth * 3 / 5 + 1,
                  y - scaledMarginHeight + boardHeight + stoneRadius * 13 / 8 + (height + 2) / 4));
      g0.setColor(Color.BLACK);
      g0.setStroke(new BasicStroke(2));
      g0.draw(sha);
      g0.setColor(Color.WHITE);
      g0.fill(sha);
    }
  }

  public void reDrawGobanAnyway() {
    cachedBoardImage = emptyImage;
    cachedBoardWidth = -1;
  }

  public void reDrawBackgroundAnyway() {
    cachedWallpaperImage = emptyImage;
  }

  public void reDrawStoneAnyway() {
    cachedIsBigMargin = !cachedIsBigMargin;
  }

  public void reCreateStoneImageAnyway() {
    cachedBlackStoneImage = emptyImage;
    cachedWhiteStoneImage = emptyImage;
    cachedIsBigMargin = !cachedIsBigMargin;
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
        || changedName
        || cachedShowCoords != showCoordinates()
        || Lizzie.board.isForceRefresh()) {
      changedSize = true;
      cachedShadow = null;
      cachedGhostShadow2 = null;
      cachedShowCoords = showCoordinates();
      clearAfterMove();
      changedName = false;
      cachedBoardWidth = boardWidth;
      cachedBoardHeight = boardHeight;
      Lizzie.board.setForceRefresh(false);

      cachedBackgroundImage = new BufferedImage(width, height, TYPE_INT_ARGB);
      Graphics2D g = cachedBackgroundImage.createGraphics();
      g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

      // Draw the wooden background
      drawWoodenBoard(g);
      //      g.setColor(new Color(0,0,0,52));
      //      g.fillRect(x, y+stoneRadius/5, stoneRadius/5, boardHeight-stoneRadius/5);
      //      g.fillRect(x, y, boardWidth-stoneRadius/5,  stoneRadius/5);
      //      g.fillRect(x+boardWidth-stoneRadius/5, y, stoneRadius/5, boardHeight-stoneRadius/5);
      //      g.fillRect(x+stoneRadius/5, y+boardHeight-stoneRadius/5, boardWidth,  stoneRadius/5);
      // Draw the lines
      g.setColor(Color.BLACK);
      BasicStroke borderStroke =
          new BasicStroke(
              Math.max(boardWidth > 560 ? 2f : 1f, Math.min(3.2f, (float) availableWidth / 481f)));
      BasicStroke normalStroke =
          new BasicStroke(Math.max(1f, Math.min(1.7f, (float) availableWidth / 1110f)));
      for (int i = 0; i < Board.hexHeight; i++) {
        // g.setStroke(new BasicStroke(stoneRadius / 15f));
        if (i == 0 || i == Board.hexHeight - 1) {
          g.setStroke(borderStroke);
          g.drawLine(
              scaledMarginWidth + squareWidth + squareWidth * i,
              scaledMarginHeight + squareHeight + squareHeight * 2 * i,
              scaledMarginWidth
                  + squareWidth
                  + squareWidth * i
                  + squareWidth * 2 * (Board.hexWidth - 1),
              scaledMarginHeight + squareHeight + squareHeight * 2 * i);
        } else {
          g.setStroke(normalStroke);
          g.drawLine(
              scaledMarginWidth + squareWidth + squareWidth * i,
              scaledMarginHeight + squareHeight + squareHeight * 2 * i,
              scaledMarginWidth
                  + squareWidth
                  + squareWidth * i
                  + squareWidth * 2 * (Board.hexWidth - 1),
              scaledMarginHeight + squareHeight + squareHeight * 2 * i);
        }
      }
      for (int i = 0; i < Board.hexWidth; i++) {
        // g.setStroke(new BasicStroke(stoneRadius / 15f));
        if (i == 0 || i == Board.hexWidth - 1) {
          g.setStroke(borderStroke);
          g.drawLine(
              scaledMarginWidth + squareWidth + squareWidth * 2 * i,
              scaledMarginHeight + squareHeight,
              scaledMarginWidth + squareWidth * 2 * i + squareWidth * Board.hexHeight,
              scaledMarginHeight + squareHeight + squareHeight * 2 * (Board.hexHeight - 1));
          if (i == Board.hexWidth - 1) {
            g.setStroke(normalStroke);
            g.drawLine(
                scaledMarginWidth + squareWidth + squareWidth * 2 * i,
                scaledMarginHeight + squareHeight,
                scaledMarginWidth
                    + squareWidth
                    + squareWidth * i
                    + (i > Board.hexHeight - 1 ? squareWidth * (i - (Board.hexHeight - 1)) : 0),
                scaledMarginHeight
                    + squareHeight
                    + squareHeight * 2 * Math.min(i, Board.hexHeight - 1));
          }
        } else {
          g.setStroke(normalStroke);
          g.drawLine(
              scaledMarginWidth + squareWidth + squareWidth * 2 * i,
              scaledMarginHeight + squareHeight,
              scaledMarginWidth + squareWidth * 2 * i + squareWidth * Board.hexHeight,
              scaledMarginHeight + squareHeight + squareHeight * 2 * (Board.hexHeight - 1));

          g.drawLine(
              scaledMarginWidth + squareWidth + squareWidth * 2 * i,
              scaledMarginHeight + squareHeight,
              scaledMarginWidth
                  + squareWidth
                  + squareWidth * i
                  + (i > Board.hexHeight - 1 ? squareWidth * (i - (Board.hexHeight - 1)) : 0),
              scaledMarginHeight
                  + squareHeight
                  + squareHeight * 2 * Math.min(i, Board.hexHeight - 1));
        }
      }
      g.setStroke(normalStroke);
      for (int i = Board.hexWidth; i < Board.hexWidth + Board.hexHeight - 1; i++) {
        int num = i + 1 - Board.hexWidth;
        g.drawLine(
            scaledMarginWidth + squareWidth + squareWidth * 2 * i - squareWidth * num,
            scaledMarginHeight + squareHeight + num * 2 * squareHeight,
            scaledMarginWidth
                + squareWidth
                + squareWidth * i
                + (i > Board.hexHeight - 1 ? squareWidth * (i - (Board.hexHeight - 1)) : 0),
            scaledMarginHeight
                + squareHeight
                + squareHeight * 2 * Math.min(i, Board.hexHeight - 1));
      }

      // Draw the star points
      //  drawStarPoints(g);

      // Draw coordinates if enabled
      if (showCoordinates()) {
        g.setColor(Color.BLACK);
        for (int i = 0; i < Board.boardWidth; i++) {
          if (i <= (Board.hexWidth - 1) * 2 + 1) {
            if (i % 2 == 1)
              drawString(
                  g,
                  scaledMarginWidth + squareWidth * i,
                  scaledMarginHeight * 4 / 10,
                  LizzieFrame.uiFont,
                  Board.asName((i - 1) / 2),
                  stoneRadius * 4 / 5,
                  stoneRadius);
          }
          if (i >= Board.hexHeight) {
            if (i % 2 == 1)
              drawString(
                  g,
                  scaledMarginWidth + squareWidth * i,
                  boardHeight - scaledMarginHeight * 4 / 10,
                  LizzieFrame.uiFont,
                  Board.asName((i - Board.hexHeight) / 2),
                  stoneRadius * 4 / 5,
                  stoneRadius);
          }
        }
        for (int i = 0; i < Board.boardHeight; i++) {
          if (i % 2 == 1) {
            drawString(
                g,
                scaledMarginWidth * 4 / 10,
                scaledMarginHeight + squareHeight * i,
                LizzieFrame.uiFont,
                String.valueOf((i + 1) / 2),
                stoneRadius * 4 / 5,
                stoneRadius);

            drawString(
                g,
                -scaledMarginWidth * 4 / 10 + boardWidth,
                scaledMarginHeight + squareHeight * i,
                LizzieFrame.uiFont,
                String.valueOf((i + 1) / 2),
                stoneRadius * 4 / 5,
                stoneRadius);
          }
        }
      }
      g.dispose();
      reDrawAllSelectedRect();
      if (Lizzie.frame.isInScoreMode)
        if (Lizzie.board.boardGroupInfo != null) this.drawScore(Lizzie.board.boardGroupInfo);
        else this.clearScore();
    }

    g0.setRenderingHint(KEY_ANTIALIASING, VALUE_ANTIALIAS_OFF);
    g0.drawImage(cachedBackgroundImage, x, y, null);
    // cachedX = x;
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
    if (Lizzie.config.isDoubleEngineMode()) {
      if (this == LizzieFrame.boardRenderer) LizzieFrame.boardRenderer2.removedrawmovestone();
    }
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
    if (Lizzie.config.usePureStone) drawStoneSimple(g, gShadow, stoneX, stoneY, stone);
    else drawStone(g, gShadow, stoneX, stoneY, stone);
    g.dispose();
  }

  public void drawSelectedRect(int x1, int y1, int x2, int y2, boolean isAllow) {
    needDrawSelectImage = true;
    selectImage = new BufferedImage(boardWidth, boardHeight, TYPE_INT_ARGB);
    Graphics2D g = selectImage.createGraphics();
    g.setRenderingHint(KEY_ANTIALIASING, VALUE_ANTIALIAS_ON);
    if (isAllow) g.setColor(new Color(0, 0, 120, 45));
    else g.setColor(new Color(120, 0, 0, 45));
    Optional<int[]> coordsOpt =
        convertScreenToCoordinatesForSelect(
            Math.min(x1, x2), Math.max(x1, x2), Math.min(y1, y2), Math.max(y1, y2));
    if (coordsOpt.isPresent()) {
      int[] coords = coordsOpt.get();
      g.fillRect(
          scaledMarginWidth + squareWidth * (coords[0] - 1) + squareWidth / 2,
          scaledMarginWidth + squareWidth * (coords[1] - 1) + squareWidth / 2,
          squareWidth * (coords[2] - coords[0] + 1),
          squareWidth * (coords[3] - coords[1] + 1));
    }
    g.dispose();
  }

  public void drawAllSelectedRectByCoords(boolean isAllow, String coordsName) {
    if (coordsName.equals("")) return;
    needDrawSelectImage = false;
    needDrawSelectImageAll = true;
    selectImage = emptyImage;
    selectImageAll = new BufferedImage(boardWidth, boardHeight, TYPE_INT_ARGB);
    Graphics2D g = selectImageAll.createGraphics();
    g.setRenderingHint(KEY_ANTIALIASING, VALUE_ANTIALIAS_ON);
    if (isAllow) {
      g.setColor(new Color(0, 0, 120, 45));
      String[] coords = coordsName.split(",");
      for (String coordName : coords) {
        int[] coordinates = Board.convertNameToCoordinates(coordName);
        int stoneX = scaledMarginWidth + squareWidth * coordinates[0];
        int stoneY = scaledMarginHeight + squareHeight * coordinates[1];
        g.fillRect(stoneX - squareWidth / 2, stoneY - squareWidth / 2, squareWidth, squareWidth);
      }
    } else {
      g.setColor(new Color(120, 0, 0, 45));
      String[] coords = coordsName.split(",");
      for (String coordName : coords) {
        int[] coordinates = Board.convertNameToCoordinates(coordName);
        int stoneX = scaledMarginWidth + squareWidth * coordinates[0];
        int stoneY = scaledMarginHeight + squareHeight * coordinates[1];
        g.fillRect(stoneX - squareWidth / 2, stoneY - squareWidth / 2, squareWidth, squareWidth);
      }
    }
    g.dispose();
  }

  private void reDrawAllSelectedRect() {
    if (Lizzie.frame.selectForceAllow)
      drawAllSelectedRectByCoords(Lizzie.frame.selectForceAllow, LizzieFrame.allowcoords);
    else drawAllSelectedRectByCoords(Lizzie.frame.selectForceAllow, LizzieFrame.avoidcoords);
  }

  public void removeSelectedRect() {
    selectImage = emptyImage;
    selectImageAll = emptyImage;
    needDrawSelectImage = false;
    needDrawSelectImageAll = false;
  }

  public void removeKataEstimateImage() {
    kataEstimateImage = new BufferedImage(boardWidth, boardHeight, TYPE_INT_ARGB);
  }

  public void removeEstimateImage() {
    estimateImage = new BufferedImage(boardWidth, boardHeight, TYPE_INT_ARGB);
  }

  public static int roundToInt(double number) {
    return (int) round(number);
  }

  public boolean shouldShowCountBlockBelow() {
    Leelaz leelaz = this.boardIndex == 1 && Lizzie.leelaz2 != null ? Lizzie.leelaz2 : Lizzie.leelaz;
    if (leelaz.isKatago && leelaz.iskataHeatmapShowOwner) {
      return Lizzie.config.showPureEstimateBigBelow;
    }
    return Lizzie.config.showKataGoEstimateBigBelow;
  }

  public boolean shouldShowCountBlockBig() {
    Leelaz leelaz = this.boardIndex == 1 && Lizzie.leelaz2 != null ? Lizzie.leelaz2 : Lizzie.leelaz;
    if (leelaz.isKatago && leelaz.iskataHeatmapShowOwner) {
      return Lizzie.config.showPureEstimateBigBelow;
    }
    return Lizzie.config.showKataGoEstimateBigBelow;
  }

  public void drawKataEstimateByTransparent(
      ArrayList<Double> estimateList, boolean reverse, boolean fromRawNet) {
    BufferedImage newEstimateImage = new BufferedImage(boardWidth, boardHeight, TYPE_INT_ARGB);
    Graphics2D g = newEstimateImage.createGraphics();
    boolean blackToPlay = Lizzie.board.getHistory().isBlacksTurn();
    boolean showBigSize = shouldShowCountBlockBig();
    if (reverse) blackToPlay = !blackToPlay;
    for (int i = 0; i < estimateList.size(); i++) {
      int[] c = Lizzie.board.getCoordKataGo(i);
      int x = c[0];
      int y = c[1];
      if ((estimateList.get(i) > 0 && blackToPlay) || (estimateList.get(i) < 0 && !blackToPlay)) {
        if (!showBigSize)
          if ((!fromRawNet && Lizzie.config.showKataGoEstimateNotOnlive)
              || (fromRawNet && Lizzie.config.showPureEstimateNotOnlive)) {
            if (Lizzie.board.getHistory().getData().stones[Board.getIndex(c[0], c[1])]
                == Stone.BLACK) continue;
          }
        int stoneX = scaledMarginWidth + squareWidth * x;
        int stoneY = scaledMarginHeight + squareHeight * y;
        // g.setColor(Color.BLACK);

        int alpha =
            showBigSize ? (int) (estimateList.get(i) * 105) : (int) (estimateList.get(i) * 255);
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
        if (showBigSize)
          g.fillRect(
              stoneX - squareWidth * 5 / 10,
              stoneY - squareWidth * 5 / 10,
              squareWidth,
              squareWidth);
        else if (Lizzie.config.showKataGoEstimateSmall) {
          int length = (int) Math.round(squareWidth / 8.0);
          g.fillRect(stoneX - length, stoneY - length, length * 2, length * 2);
        } else {
          g.fillRect(
              stoneX - squareWidth / 4, stoneY - squareWidth / 4, squareWidth / 2, squareWidth / 2);
        }
      }
      if ((estimateList.get(i) < 0 && blackToPlay) || (estimateList.get(i) > 0 && !blackToPlay)) {
        if (!showBigSize)
          if ((!fromRawNet && Lizzie.config.showKataGoEstimateNotOnlive)
              || (fromRawNet && Lizzie.config.showPureEstimateNotOnlive)) {
            if (Lizzie.board.getHistory().getData().stones[Board.getIndex(c[0], c[1])]
                == Stone.WHITE) continue;
          }
        int stoneX = scaledMarginWidth + squareWidth * x;
        int stoneY = scaledMarginHeight + squareHeight * y;
        int alpha =
            showBigSize ? (int) (estimateList.get(i) * 165) : (int) (estimateList.get(i) * 255);
        Color cl = new Color(255, 255, 255, Math.abs(alpha));
        g.setColor(cl);
        if (showBigSize)
          g.fillRect(
              stoneX - squareWidth * 5 / 10,
              stoneY - squareWidth * 5 / 10,
              squareWidth,
              squareWidth);
        else if (Lizzie.config.showKataGoEstimateSmall) {
          int length = (int) Math.round(squareWidth / 8.0);
          g.fillRect(stoneX - length, stoneY - length, length * 2, length * 2);
        } else {
          g.fillRect(
              stoneX - squareWidth / 4, stoneY - squareWidth / 4, squareWidth / 2, squareWidth / 2);
        }
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

  public void drawKataEstimateBySize(ArrayList<Double> estimateList, boolean reverse) {
    BufferedImage newEstimateImage = new BufferedImage(boardWidth, boardHeight, TYPE_INT_ARGB);
    Graphics2D g = newEstimateImage.createGraphics();
    boolean blackToPlay = Lizzie.board.getHistory().isBlacksTurn();
    if (reverse) blackToPlay = !blackToPlay;
    for (int i = 0; i < estimateList.size(); i++) {
      int[] c = Lizzie.board.getCoordKataGo(i);
      int x = c[0];
      int y = c[1];
      if ((estimateList.get(i) > 0 && blackToPlay) || (estimateList.get(i) < 0 && !blackToPlay)) {
        int stoneX = scaledMarginWidth + squareWidth * x;
        int stoneY = scaledMarginHeight + squareHeight * y;
        Color cl = new Color(0, 0, 0, 180);
        g.setColor(cl);
        int length = (int) (convertLength(estimateList.get(i)) * squareWidth);
        if (length > 0) g.fillRect(stoneX - length / 2, stoneY - length / 2, length, length);
      }
      if ((estimateList.get(i) < 0 && blackToPlay) || (estimateList.get(i) > 0 && !blackToPlay)) {
        int stoneX = scaledMarginWidth + squareWidth * x;
        int stoneY = scaledMarginHeight + squareHeight * y;
        int length = (int) (convertLength(estimateList.get(i)) * squareWidth);

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
        int y = i / Board.boardWidth;
        int x = i % Board.boardWidth;
        int stoneX = scaledMarginWidth + squareWidth * x;
        int stoneY = scaledMarginHeight + squareHeight * y;
        g.setColor(Color.BLACK);
        g.fillRect(
            stoneX - squareWidth / 4, stoneY - squareWidth / 4, squareWidth / 2, squareWidth / 2);
      }
      if (tempcount.get(i) < 0) {
        int y = i / Board.boardWidth;
        int x = i % Board.boardWidth;
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
    if (stones[Board.getIndex(x, y)].isBlack() || stones[Board.getIndex(x, y)].isWhite()) {
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

  public void drawbadstone(int x, int y, Graphics2D g) {
    int stoneX = this.x + scaledMarginWidth + squareWidth * x;
    int stoneY = this.y + scaledMarginHeight + squareHeight * y;
    g.setColor(Color.magenta);
    drawCircle(g, stoneX, stoneY, stoneRadius * 7 / 6, 11.5f);
    //   g.dispose();
  }

  /** Draw the stones. We cache the image for a performance boost. */
  public void drawStones() {
    // draw a new image if frame size changes or board state changes
    if (cachedStonesImage.getWidth() != boardWidth
        || cachedStonesImage.getHeight() != boardHeight
        || cachedDisplayedBranchLength != displayedBranchLength
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
                g, gShadow, stoneX, stoneY, Lizzie.board.getStones()[Board.getIndex(i, j)]);
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
                    Lizzie.board.getStones()[Board.getIndex(threadI, j)]);
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
      g.dispose();
      gShadow.dispose();
      // lastInScoreMode = false;
    }
    //   if (Lizzie.board.inScoreMode()) lastInScoreMode = true;
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
    branchOpt = Optional.empty();
    // calculate best moves and branch
    if (this.boardIndex == 1) {
      bestMoves = Lizzie.board.getHistory().getCurrentHistoryNode().getData().bestMoves2;
      estimateArray = Lizzie.board.getHistory().getCurrentHistoryNode().getData().estimateArray2;
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
                .estimateArray2;
      } else preEstimateArray = null;
    } else {
      if (shouldShowPreviousBestMoves()) {
        if (Lizzie.board.getHistory().getCurrentHistoryNode().previous().isPresent()) {
          BoardData preData =
              Lizzie.board.getHistory().getCurrentHistoryNode().previous().get().getData();
          if (Lizzie.config.showPreviousBestmovesOnlyFirstMove) {
            bestMoves = new ArrayList<MoveData>();
            BoardData thisData = Lizzie.board.getData();
            for (MoveData move : preData.bestMoves) {
              if (thisData.lastMove.isPresent()) {
                int[] coords = Board.convertNameToCoordinates(move.coordinate);
                int[] lastMove = thisData.lastMove.get();
                if (coords[0] == lastMove[0] && coords[1] == lastMove[1]) {
                  bestMoves.add(move);
                  break;
                }
              }
            }
          } else bestMoves = preData.bestMoves;
          estimateArray = preData.estimateArray;
        }
      } else {
        bestMoves = Lizzie.board.getHistory().getCurrentHistoryNode().getData().bestMoves;
        estimateArray = Lizzie.board.getHistory().getCurrentHistoryNode().getData().estimateArray;
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
    }

    //    if ((Lizzie.board.getHistory().isBlacksTurn()
    //            && !Lizzie.frame.toolbar.chkShowBlack.isSelected())
    //        || (!Lizzie.board.getHistory().isBlacksTurn()
    //            && !Lizzie.frame.toolbar.chkShowWhite.isSelected())) return;
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
    boolean needShow = false;
    if (Lizzie.frame.priorityMoveCoords.size() > 0) {
      for (String coords : Lizzie.frame.priorityMoveCoords) {
        if (coords.equals(suggestedMove.get().coordinate)) needShow = true;
      }
    }

    if (Lizzie.board.getHistory().getCurrentHistoryNode().next().isPresent()) {
      if (Lizzie.board
          .getHistory()
          .getCurrentHistoryNode()
          .next()
          .get()
          .getData()
          .lastMove
          .isPresent()) {
        int nextMove[] =
            Lizzie.board.getHistory().getCurrentHistoryNode().next().get().getData().lastMove.get();
        int coords[] = Board.convertNameToCoordinates(suggestedMove.get().coordinate);
        if (nextMove[0] == coords[0] && nextMove[1] == coords[1]) needShow = true;
      }
    }
    if (notChangedMouseOverMove) {
      if (displayedBranchLength == 1) {
        if (!Lizzie.config.autoReplayBranch) return;
      }
    } else {
      if (!(Lizzie.config.autoReplayBranch
          && Lizzie.config.autoReplayDisplayEntireVariationsFirst)) {
        if (displayedBranchLength < 2
            && ((percentPlayouts <= Lizzie.config.minPlayoutRatioForStats && !needShow)
                || Lizzie.config.autoReplayBranch)) {
          displayedBranchLength = 1;
          if (!Lizzie.config.autoReplayBranch) return;
        }
        if (displayedBranchLength < 2
            && Lizzie.config.limitMaxSuggestion > 0
            && mouseOverOrder > Lizzie.config.limitMaxSuggestion
            && !needShow
            && !suggestedMove.get().lastTimeUnlimited) {
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
    if (shouldShowPreviousBestMoves()) {
      if (Lizzie.board.getHistory().getCurrentHistoryNode().previous().isPresent())
        branch =
            new Branch(
                Lizzie.board,
                variation,
                pvVistis,
                this.displayedBranchLength > 0 ? displayedBranchLength : 199,
                false,
                false,
                Lizzie.board
                    .getHistory()
                    .getCurrentHistoryNode()
                    .previous()
                    .get()
                    .getData()
                    .stones);
      else return;
    } else {
      branch =
          new Branch(
              Lizzie.board,
              variation,
              pvVistis,
              this.displayedBranchLength > 0 ? displayedBranchLength : 199,
              false,
              false,
              null);
    }

    mouseOverCoords = suggestedMove.get().coordinate;
    branchOpt = Optional.of(branch);
    variationOpt = Optional.of(variation);
    isShowingBranch = true;
    if (!changedSize) {
      if (Lizzie.config.noRefreshOnMouseMove) {
        if (variation == cachedVariation
            && displayedBranchLength == cachedDisplayedBranchLengthFroBranch) return;
      } else {
        if (compareVariationListEquals(variation, cachedVariation)
            && displayedBranchLength == cachedDisplayedBranchLengthFroBranch) return;
      }
    } else changedSize = false;
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
    if (Lizzie.config.usePureStone) {
      for (int i = 0; i < Board.boardWidth; i++) {
        for (int j = 0; j < Board.boardHeight; j++) {
          // Display latest stone for ghost dead stone
          int index = Board.getIndex(i, j);
          Stone stone = branch.data.stones[index];
          if (!Lizzie.config.removeDeadChainInVariation && !shouldShowPreviousBestMoves())
            if (Lizzie.board.getData().stones[index] != Stone.EMPTY) continue;
          if (branch.data.moveNumberList[index] > maxBranchMoves(false)) continue;
          int stoneX = scaledMarginWidth + squareWidth * i;
          int stoneY = scaledMarginHeight + squareHeight * j;
          boolean isMouseOver = false;
          if (isIndependBoard) {
            if (i == Lizzie.frame.independentMainBoard.mouseOverCoordinate[0]
                && j == Lizzie.frame.independentMainBoard.mouseOverCoordinate[1])
              isMouseOver = true;
          } else {
            if (i == Lizzie.frame.mouseOverCoordinate[0]
                && j == Lizzie.frame.mouseOverCoordinate[1]) isMouseOver = true;
          }
          boolean isCaptured = (stone == Stone.BLACK_CAPTURED || stone == Stone.WHITE_CAPTURED);
          if (isCaptured) drawCapturedStone(g, stoneX, stoneY, stone, isMouseOver);
          else drawStoneSimple(g, gShadow, stoneX, stoneY, stone);
          if (isMouseOver) isMouseOverStoneBlack = stone.isBlackColor();
        }
      }
    } else {
      for (int i = 0; i < Board.boardWidth; i++) {
        for (int j = 0; j < Board.boardHeight; j++) {
          // Display latest stone for ghost dead stone
          int index = Board.getIndex(i, j);
          Stone stone = branch.data.stones[index];
          if (!Lizzie.config.removeDeadChainInVariation && !shouldShowPreviousBestMoves())
            if (Lizzie.board.getData().stones[index] != Stone.EMPTY) continue;
          if (branch.data.moveNumberList[index] > maxBranchMoves(false)) continue;
          int stoneX = scaledMarginWidth + squareWidth * i;
          int stoneY = scaledMarginHeight + squareHeight * j;
          boolean isCaptured = (stone == Stone.BLACK_CAPTURED || stone == Stone.WHITE_CAPTURED);
          boolean isMouseOver = false;
          if (isIndependBoard) {
            if (i == Lizzie.frame.independentMainBoard.mouseOverCoordinate[0]
                && j == Lizzie.frame.independentMainBoard.mouseOverCoordinate[1])
              isMouseOver = true;
          } else {
            if (i == Lizzie.frame.mouseOverCoordinate[0]
                && j == Lizzie.frame.mouseOverCoordinate[1]) isMouseOver = true;
          }
          if (isCaptured) drawCapturedStone(g, stoneX, stoneY, stone, isMouseOver);
          else drawStone(g, gShadow, stoneX, stoneY, stone);
          if (isMouseOver) isMouseOverStoneBlack = stone.isBlackColor();
        }
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
    isMouseOverNextBlunder = false;
    if (isShowingNextMoveBlunder) {
      int[] mouseOverCoords;
      if (isIndependBoard) {
        mouseOverCoords = Lizzie.frame.independentMainBoard.mouseOverCoordinate;
      } else {
        mouseOverCoords = Lizzie.frame.mouseOverCoordinate;
      }
      if (mouseOverCoords[0] == nextMoveX && mouseOverCoords[1] == nextMoveY) {
        mouseOverOrder = -1;
        MoveData move = new MoveData();
        move.coordinate = Board.convertCoordinatesToName(nextMoveX, nextMoveY);
        move.order = -1;
        move.variation = nextPv;
        move.pvVisits = nextPvVisits;
        isMouseOverNextBlunder = true;
        return Optional.of(move);
      }
    }
    if (bestMoves != null && !bestMoves.isEmpty())
      for (int i = 0; i < bestMoves.size(); i++) {
        Optional<int[]> coords = Board.asCoordinates(bestMoves.get(i).coordinate);
        if (coords.isPresent()) {
          if (isIndependBoard) {
            if (Lizzie.frame.isMouseOverIndependMainBoard(coords.get()[0], coords.get()[1])) {
              mouseOverOrder = i + 1;
              return Optional.of(bestMoves.get(i));
            }
          } else if (Lizzie.frame.isMouseOver(coords.get()[0], coords.get()[1])) {
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
        if (isIndependBoard) {
          if (Lizzie.frame.isMouseOverIndependMainBoard(coords.get()[0], coords.get()[1])) {
            needAddback = true;
          }
        } else if (Lizzie.frame.isMouseOver(coords.get()[0], coords.get()[1])) {
          needAddback = true;
        }
        if (needAddback) {
          List<MoveData> outOfRangeMoves =
              this.boardIndex == 1
                  ? Lizzie.board.getHistory().getCurrentHistoryNode().getData().bestMoves2OutOfRange
                  : Lizzie.board.getHistory().getCurrentHistoryNode().getData().bestMovesOutOfRange;
          if (outOfRangeMoves != null) {
            for (MoveData move : outOfRangeMoves) {
              if (move.coordinate.equals(mouseOverTemp.coordinate)) {
                bestMoves.add(move);
                return Optional.of(move);
              }
            }
          }
          mouseOverTemp.order = Math.max(bestMoves.size(), 9);
          bestMoves.add(mouseOverTemp);
          return Optional.of(mouseOverTemp);
        }
      }
    }
    mouseOverOrder = -1;
    if (!Lizzie.config.isDoubleEngineMode()) Lizzie.frame.clearMouseOverCoordinate(isIndependBoard);
    return Optional.empty();
  }

  /** Render the shadows and stones in correct background-foreground order */
  private void renderImages(Graphics2D g) {
    g.setRenderingHint(KEY_ANTIALIASING, VALUE_ANTIALIAS_OFF);
    if ((Lizzie.config.showKataGoEstimate && Lizzie.config.showKataGoEstimateOnMainbord)
        || Lizzie.frame.isShowingHeatmap)
      if (shouldShowCountBlockBelow()) g.drawImage(kataEstimateImage, x, y, null);
    if (isShowingBranch) {
      if (!Lizzie.config.removeDeadChainInVariation && !shouldShowPreviousBestMoves()) {
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
    if ((Lizzie.config.showKataGoEstimate && Lizzie.config.showKataGoEstimateOnMainbord)
        || Lizzie.frame.isShowingHeatmap)
      if (!shouldShowCountBlockBelow()) g.drawImage(kataEstimateImage, x, y, null);
    if (Lizzie.frame.isCounting || Lizzie.frame.isAutocounting)
      g.drawImage(estimateImage, x, y, null);
    if (Lizzie.frame.isKeepingForce || LizzieFrame.isKeepForcing || LizzieFrame.isTempForcing) {
      if (needDrawSelectImage) g.drawImage(selectImage, x, y, null);
      if (needDrawSelectImageAll) g.drawImage(selectImageAll, x, y, null);
    }
    if (Lizzie.frame.isInScoreMode) g.drawImage(scoreImage, x, y, null);
    //  if (!branchOpt.isPresent()) {
    //    g.drawImage(unImportantSugg, x, y, null);
    //  g.drawImage(importantSugg, x, y, null);
    //  }
  }

  private void renderImagesUnimportant(Graphics2D g) {
    if (!branchOpt.isPresent()) {
      g.drawImage(unImportantSugg, x, y, null);
    }
  }

  /** Draw move numbers and/or mark the last played move */
  private void drawMoveNumbers(Graphics2D g) {
    g.setRenderingHint(KEY_ANTIALIASING, VALUE_ANTIALIAS_ON);
    Board board = Lizzie.board;
    Optional<int[]> lastMoveOpt = branchOpt.map(b -> b.data.lastMove).orElse(board.getLastMove());
    drawPass(g, board, lastMoveOpt);
    boolean showAllinBranch = false;
    if (Lizzie.config.showMoveAllInBranch
        && !Lizzie.board.getHistory().getCurrentHistoryNode().isMainTrunk()) {
      showAllinBranch = true;
    } else if (Lizzie.config.allowMoveNumber == 0
        && !branchOpt.isPresent()
        && !Lizzie.frame.isTrying) {
      if (lastMoveOpt.isPresent()) {
        int[] lastMove = lastMoveOpt.get();

        // Mark the last coordinate
        int stoneX = x + scaledMarginWidth + squareWidth * lastMove[0];
        int stoneY = y + scaledMarginHeight + squareHeight * lastMove[1];

        // Set color to the opposite color of whatever is on the board
        // boolean isWhite = board.getStones()[Board.getIndex(lastMove[0], lastMove[1])].isWhite();
        if (shouldShowPreviousBestMoves()) {
          g.setColor(Color.red);
          drawPolygonSmall(g, stoneX, stoneY, stoneRadius);
        } else
          switch (Lizzie.config.stoneIndicatorType) {
            case 0:
              g.setColor(Color.red);
              // g.setColor(Lizzie.board.getData().blackToPlay ? Color.BLUE : Color.RED);
              drawPolygon(g, stoneX, stoneY, stoneRadius);
              break;
            case 1:
              g.setColor(
                  Lizzie.board.getData().lastMoveColor.isWhite() ? Color.BLACK : Color.WHITE);
              drawCircle(g, stoneX, stoneY, (int) Math.round(squareWidth * 2 * 0.22f), 5f);
              break;
            case 2:
              g.setColor(
                  Lizzie.board.getData().lastMoveColor.isWhite() ? Color.BLACK : Color.WHITE);
              fillCircle(g, stoneX, stoneY, (int) (stoneRadius * 0.325));
              break;
          }
      }

      return;
    }

    int[] moveNumberList;
    moveNumberList = branchOpt.map(b -> b.data.moveNumberList).orElse(board.getMoveNumberList());

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

        if (!showAllinBranch) {
          if (!Lizzie.frame.isTrying && !EngineManager.isEngineGame) {
            if ((Lizzie.config.allowMoveNumber > -1
                && lastMoveNumber - moveNumberList[here] >= Lizzie.config.allowMoveNumber)) {
              continue;
            }
          }
          if (EngineManager.isEngineGame) {
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
            && (!branchOpt.isPresent()
                || !(isIndependBoard
                    ? Lizzie.frame.independentMainBoard.isMouseOver(i, j)
                    : Lizzie.frame.isMouseOver(i, j)))) {
          boolean isShowingPvVists = false;
          boolean reverse = (moveNumberList[Board.getIndex(i, j)] > maxBranchMoves(true));
          if ((lastMoveOpt.isPresent() && lastMoveOpt.get()[0] == i && lastMoveOpt.get()[1] == j)) {
            if (isShowingBranch
                && Lizzie.config.showPvVisitsLastMove
                && branch.pvVisitsList[here] > Lizzie.config.pvVisitsLimit) {
              int pvVisits = branch.pvVisitsList[here];
              drawLastMovePvVisits(g, stoneX, stoneY, pvVisits, mvNum);
              continue;
            } else if (isMouseOverNextBlunder && (i == nextPvX && j == nextPvY)) {
              int pvVisits = nextVisits;
              isShowingPvVists = true;
              drawLastMovePvVisits(g, stoneX, stoneY, pvVisits, mvNum);
              continue;

            } else {
              g.setColor(Color.RED);
              drawPolygonSmall(g, stoneX, stoneY, stoneRadius);
            }
            if (shouldShowPreviousBestMoves() && !isShowingBranch) continue;
          }
          // Draw white letters on black stones nomally.
          // But use black letters for showing black moves without stones.
          else {
            if (reverse) continue;
            if (isShowingBranch
                && Lizzie.config.showPvVisitsAllMove
                && branch.pvVisitsList[here] > Lizzie.config.pvVisitsLimit) {
              int pvVisits = branch.pvVisitsList[here];
              isShowingPvVists = true;
              drawPvVisits(g, stoneX, stoneY, pvVisits, mvNum);
            } else if (isMouseOverNextBlunder) {
              if (i == nextPvX && j == nextPvY) {
                int pvVisits = nextVisits;
                isShowingPvVists = true;
                drawPvVisits(g, stoneX, stoneY, pvVisits, mvNum);
              }
            }
            g.setColor(stoneHere.isBlackColor() ? Color.WHITE : Color.BLACK);
          }
          String moveNumberString = String.valueOf(mvNum);
          if (!showAllinBranch
              && Lizzie.config.showMoveNumberFromOne
              && Lizzie.config.allowMoveNumber > 0) {
            if (lastMoveNumber > Lizzie.config.allowMoveNumber) {
              moveNumberString =
                  String.valueOf(mvNum - (lastMoveNumber - Lizzie.config.allowMoveNumber));
            }
          }
          if (isShowingPvVists) {
            if (Lizzie.frame.isTrying) {
              if (mvNum < 0) {
                moveNumberString = String.valueOf(-mvNum);
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
            if (Lizzie.frame.isTrying && !branchOpt.isPresent()) {
              if (mvNum < 0) {
                moveNumberString = String.valueOf(-mvNum);
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

  private void drawPvVisits(Graphics2D g, int stoneX, int stoneY, int pvVisits, int mvNum) {
    // TODO Auto-generated method stub
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
        Utils.getPlayoutsString(pvVisits),
        (float) (squareWidth * 0.33),
        squareWidth * 0.8,
        1);
  }

  private void drawLastMovePvVisits(
      Graphics2D g, int stoneX, int stoneY, int pvVisits, int moveNum) {
    // TODO Auto-generated method stub
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
        Utils.getPlayoutsString(pvVisits),
        (float) (squareWidth * 0.33),
        squareWidth * 0.8,
        1);
    g.setColor(Color.RED);
    drawPolygonSmallPv(g, stoneX, stoneY, squareWidth);

    if (!(shouldShowPreviousBestMoves() && !isShowingBranch))
      drawString(
          g,
          stoneX,
          (int) (stoneY + squareWidth * 0.12),
          LizzieFrame.uiFont,
          String.valueOf(moveNum),
          (float) (stoneRadius * 1.3),
          (int) (stoneRadius * 1.4));
  }

  /**
   * Draw all of Leelaz's suggestions as colored stones with winrate/playout statistics overlayed
   */
  private void drawLeelazSuggestions(Graphics2D g) {
    //  g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED);
    int minAlpha = 32;
    // float winrateHueFactor = 0.9f;
    float alphaFactor = 5.0f;
    float redHue = Color.RGBtoHSB(2, 0, 0, null)[0];
    float greenHue = Color.RGBtoHSB(0, 255, 0, null)[0];
    float cyanHue = Lizzie.config.bestMoveColor;
    if (Lizzie.frame.isShowingHeatmap) {
      int maxPolicy = 0;
      //    int minPolicy = 0;
      Leelaz leelaz =
          this.boardIndex == 1 && Lizzie.leelaz2 != null ? Lizzie.leelaz2 : Lizzie.leelaz;
      ArrayList<Integer> heatcount = leelaz.heatcount;
      for (Integer heat : heatcount) {
        if (heat > maxPolicy) maxPolicy = heat;
      }
      for (int i = 0; i < heatcount.size(); i++) {
        if (heatcount.get(i) > 0) {
          int y1 = i / Board.boardWidth;
          int x1 = i % Board.boardWidth;
          int suggestionX = x + scaledMarginWidth + squareWidth / 2 * x1;
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
              minAlpha + (maxAlpha - minAlpha) * max(0, (float) log(percent) / alphaFactor + 1);

          Color hsbColor = Color.getHSBColor(hue, saturation, brightness);
          Color color =
              new Color(hsbColor.getRed(), hsbColor.getGreen(), hsbColor.getBlue(), (int) alpha);
          if (!branchOpt.isPresent()) {
            if (!leelaz.iskataHeatmapShowOwner || !leelaz.isKatago) {
              drawShadiwCache2();
              drawShadow2(g, suggestionX, suggestionY);
              g.setColor(color);
              fillCircle(g, suggestionX, suggestionY, stoneRadius);
            }
            String text = String.format(Locale.ENGLISH, "%.1f", ((double) heatcount.get(i)) / 10);
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

          int suggestionX = x + scaledMarginWidth + squareWidth / 2 * x1;
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
              minAlpha + (maxAlpha - minAlpha) * max(0, (float) log(percent) / alphaFactor + 1);

          Color hsbColor = Color.getHSBColor(hue, saturation, brightness);
          Color color =
              new Color(hsbColor.getRed(), hsbColor.getGreen(), hsbColor.getBlue(), (int) alpha);
          if (!branchOpt.isPresent()) {
            drawShadiwCache2();
            drawShadow2(g, suggestionX, suggestionY);
            g.setColor(color);
            fillCircle(g, suggestionX, suggestionY, stoneRadius);

            String text = String.format(Locale.ENGLISH, "%.1f", ((double) bestMoves.get(i).policy));
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

          if (move.playouts == 0) {
            continue; // This actually can happen
          }
          Optional<int[]> coordsOpt = Board.asCoordinates(move.coordinate);
          if (!coordsOpt.isPresent()) {
            continue;
          }
          boolean blackToPlay = Lizzie.board.getData().blackToPlay;
          if (this.shouldIgnoreBestMove) {
            int[] coords = coordsOpt.get();
            if (coords[0] == this.ignoreBestMoveX && coords[1] == this.ignoreBestMoveY) {
              if (Lizzie.config.showSuggestionOrder && move.order < 9 && move.order > 0) {
                int suggestionX = x + scaledMarginWidth + squareWidth / 2 * coords[0];
                int suggestionY = y + scaledMarginHeight + squareHeight * coords[1];
                drawOrder(
                    g, suggestionX, suggestionY, move.order, Lizzie.board.getData().blackToPlay);
              }
              if (Lizzie.config.showSuggestionOrder && move.order == 0) {
                int suggestionX = x + scaledMarginWidth + squareWidth / 2 * coords[0];
                int suggestionY = y + scaledMarginHeight + squareHeight * coords[1];
                if (shouldShowPreviousBestMoves()) blackToPlay = !blackToPlay;
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
              continue;
            }
          }
          boolean isBestMove = bestMoves.get(0) == move;
          boolean hasMaxWinrate = move.winrate == maxWinrate;
          boolean flipWinrate =
              Lizzie.config.winrateAlwaysBlack && !Lizzie.board.getData().blackToPlay;
          double fraction = 0;
          float percentPlayouts = (float) move.playouts / maxPlayouts;
          int[] coords = coordsOpt.get();

          int suggestionX = x + scaledMarginWidth + squareWidth / 2 * coords[0];
          int suggestionY = y + scaledMarginHeight + squareHeight * coords[1];
          boolean isMouseOver =
              isIndependBoard
                  ? Lizzie.frame.independentMainBoard.isMouseOver(coords[0], coords[1])
                  : Lizzie.frame.isMouseOver(coords[0], coords[1]);
          boolean lackOfPlayouts = percentPlayouts <= Lizzie.config.minPlayoutRatioForStats;
          boolean outOfOrder =
              Lizzie.config.limitMaxSuggestion > 0
                  && move.order + 1 > Lizzie.config.limitMaxSuggestion
                  && !move.lastTimeUnlimited;
          boolean hasBackground =
              Board.getIndex(coords[0], coords[1]) < hasDrawBackground.length
                  ? hasDrawBackground[Board.getIndex(coords[0], coords[1])]
                  : false;
          if (outOfOrder && !isMouseOver && hasBackground) continue;

          float hue;
          // boolean hue2;
          if (isBestMove) {
            hue = cyanHue;
            //    hue2 = true;
          } else {
            fraction = percentPlayouts;
            fraction =
                Math.pow(fraction, (double) 1 / Lizzie.config.suggestionColorRatio); // 提升好选点温度
            //   hue2 = fraction > 0.375 ? true : false;
            hue = redHue + (greenHue - redHue) * (float) fraction;
          }

          float saturation = 1.0f;
          float brightness = 0.85f;
          float alpha;
          float alphaRatio = max(0, (float) log(percentPlayouts) / alphaFactor + 1);
          alpha = minAlpha + (maxAlpha - minAlpha) * alphaRatio;
          Color hsbColor = Color.getHSBColor(hue, saturation, brightness);
          Color color =
              new Color(hsbColor.getRed(), hsbColor.getGreen(), hsbColor.getBlue(), (int) alpha);
          boolean needShow = false;
          if (Lizzie.frame.priorityMoveCoords.size() > 0) {
            for (String coord : Lizzie.frame.priorityMoveCoords) {
              if (coord.equals(move.coordinate)) needShow = true;
            }
          }
          if (Lizzie.board.getHistory().getCurrentHistoryNode().next().isPresent())
            if (Lizzie.board
                .getHistory()
                .getCurrentHistoryNode()
                .next()
                .get()
                .getData()
                .lastMove
                .isPresent()) {
              int nextMove[] =
                  Lizzie.board
                      .getHistory()
                      .getCurrentHistoryNode()
                      .next()
                      .get()
                      .getData()
                      .lastMove
                      .get();
              if (nextMove[0] == coords[0] && nextMove[1] == coords[1]) needShow = true;
            }
          if (!branchOpt.isPresent() || isMouseOver) {
            if (Lizzie.config.showSuggestionOrder && move.order == 0) {

              if (shouldShowPreviousBestMoves()) blackToPlay = !blackToPlay;
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
          boolean needSkipNumbers =
              (outOfOrder || lackOfPlayouts) && !isMouseOver && !isBestMove && !needShow;
          if (!branchOpt.isPresent()) {
            if (shouldShowPreviousBestMoves()
                && (Lizzie.board.getStones()[Board.getIndex(coords[0], coords[1])].isBlack()
                    || Lizzie.board.getStones()[Board.getIndex(coords[0], coords[1])].isWhite())) {

            } else {
              if (!hasBackground || isMouseOver) {
                if (!needSkipNumbers) {
                  if (isFancyBoard) {
                    g.setPaint(paint);
                    Composite comp = g.getComposite();
                    if (percentPlayouts < 0.05 && !isMouseOver && !needShow) {
                      g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, 0.8f));
                    }
                    fillCircle(g, suggestionX, suggestionY, stoneRadius + 1);
                    g.setComposite(comp);
                  } else {
                    g.setColor(noFancyColor);
                    Composite comp = g.getComposite();
                    if (percentPlayouts < 0.05)
                      g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, 0.8f));
                    fillCircle(g, suggestionX, suggestionY, stoneRadius + 1);
                    g.setComposite(comp);
                  }
                }
                boolean isOnNext = isOnNext(coords);
                if (isBestMove) {
                  g.setColor(color);
                  fillCircle(g, suggestionX, suggestionY, stoneRadius + 1);
                  if (Lizzie.config.showBlueRing) {
                    g.setColor(new Color(0, 0, 255, isOnNext ? 170 : 255));
                    drawCircle(g, suggestionX, suggestionY, stoneRadius + 2, 15f);
                  } else if (!isOnNext) {
                    float alphaCircle = 48 + 48 * alphaRatio;
                    g.setColor(new Color(0, 0, 0, (int) alphaCircle));
                    drawCircle(g, suggestionX, suggestionY, stoneRadius + 1, 26.5f);
                  }
                } else {
                  g.setColor(color);
                  fillCircle(g, suggestionX, suggestionY, stoneRadius + 1);
                  if (!isOnNext) {
                    float alphaCircle = 48 + 48 * alphaRatio;
                    g.setColor(new Color(0, 0, 0, (int) alphaCircle));
                    drawCircle(g, suggestionX, suggestionY, stoneRadius + 1, 26.5f);
                  }
                }
              }
            }
          }
          if (needSkipNumbers) {
            continue;
          }
          if (isMouseOverNextBlunder && isMouseOver) {
            if (Lizzie.config.showSuggestionOrder && move.order < 9 && move.order > 0) {
              drawOrder(
                  g, suggestionX, suggestionY, move.order, Lizzie.board.getData().blackToPlay);
            }
          }
          if (!branchOpt.isPresent() || (isMouseOver && !isMouseOverNextBlunder)) {
            double roundedWinrate = round(move.winrate * 10) / 10.0;
            if (flipWinrate) {
              roundedWinrate = 100.0 - roundedWinrate;
            }

            if (Lizzie.config.showSuggestionOrder && move.order < 9 && move.order > 0) {
              drawOrder(
                  g, suggestionX, suggestionY, move.order, Lizzie.board.getData().blackToPlay);
            }

            if (isMouseOver && isShowingBranch) {
              // Color oriColor = g.getColor();
              g.setColor(Color.RED);
              drawCircle(g, suggestionX, suggestionY, stoneRadius + 1, 11f);
              // g.setColor(oriColor);
            }

            if (Lizzie.config.whiteSuggestionWhite) {
              {
                if (shouldShowPreviousBestMoves()) {
                  if (Lizzie.board.getHistory().isBlacksTurn()) g.setColor(Color.WHITE);
                  else g.setColor(Color.BLACK);
                } else {
                  if (Lizzie.board.getHistory().isBlacksTurn()) g.setColor(Color.BLACK);
                  else g.setColor(Color.WHITE);
                }
              }
            } else g.setColor(Color.BLACK);
            if (branchOpt.isPresent()) {
              if (isMouseOverStoneBlack) g.setColor(Color.WHITE);
              else g.setColor(Color.BLACK);
            }
            boolean isGenmoveBest = false;
            if (shouldShowPreviousBestMoves()) {
              Optional<int[]> lastMoveOpt = Lizzie.board.getLastMove();
              if ((lastMoveOpt.isPresent()
                  && lastMoveOpt.get()[0] == coords[0]
                  && lastMoveOpt.get()[1] == coords[1])) {
                if (Lizzie.board.getData().blackToPlay) g.setColor(Color.BLACK);
                else g.setColor(Color.WHITE);
                isGenmoveBest = true;
              }
            }
            Color maxColor;
            if (isBestMove) maxColor = Lizzie.config.bestColor;
            else maxColor = fraction > 0.375 ? Color.RED : new Color(100, 255, 235);

            boolean showWinrate = Lizzie.config.showWinrateInSuggestion;
            boolean showPlayouts = Lizzie.config.showPlayoutsInSuggestion;
            boolean showScoreLead = move.isKataData && Lizzie.config.showScoremeanInSuggestion;
            boolean canShowMaxColor = Lizzie.config.showSuggestionMaxRed && !isGenmoveBest;
            if (isMouseOver && displayedBranchLength != 1) canShowMaxColor = false;
            Color oriColor = g.getColor();
            if (showScoreLead && showPlayouts && showWinrate) {
              double score = move.scoreMean;
              if (Lizzie.board.getHistory().isBlacksTurn()) {
                if (Lizzie.config.showKataGoScoreLeadWithKomi) {
                  score = score + Lizzie.board.getHistory().getGameInfo().getKomi();
                }
              } else {
                if (Lizzie.config.showKataGoScoreLeadWithKomi) {
                  score = score - Lizzie.board.getHistory().getGameInfo().getKomi();
                }
                if (Lizzie.config.winrateAlwaysBlack) {
                  score = -score;
                }
              }
              boolean shouldShowMaxColorWinrate = canShowMaxColor && hasMaxWinrate;
              boolean shouldShowMaxColorPlayouts = canShowMaxColor && move.playouts == maxPlayouts;
              boolean shouldShowMaxColorScoreLead =
                  canShowMaxColor && move.scoreMean == maxScoreMean;
              String winrateText = String.format(Locale.ENGLISH, "%.1f", roundedWinrate);
              String playoutsText = Utils.getPlayoutsString(move.playouts);
              String scoreLeadText = Utils.convertScoreToString(score, maxScoreMean);
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
              String winrateText = String.format(Locale.ENGLISH, "%.1f", roundedWinrate);
              String playoutsText = Utils.getPlayoutsString(move.playouts);
              boolean shouldShowMaxColorWinrate = canShowMaxColor && hasMaxWinrate;
              boolean shouldShowMaxColorPlayouts = canShowMaxColor && move.playouts == maxPlayouts;
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
                drawString(
                    g,
                    suggestionX,
                    suggestionY + stoneRadius * 15 / 35,
                    LizzieFrame.playoutsFont,
                    playoutsText,
                    stoneRadius * 0.77f,
                    stoneRadius * 1.8);
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
                if (Lizzie.config.showKataGoScoreLeadWithKomi) {
                  score = score + Lizzie.board.getHistory().getGameInfo().getKomi();
                }
              } else {
                if (Lizzie.config.showKataGoScoreLeadWithKomi) {
                  score = score - Lizzie.board.getHistory().getGameInfo().getKomi();
                }
                if (Lizzie.config.winrateAlwaysBlack) {
                  score = -score;
                }
              }
              String winrateText = String.format(Locale.ENGLISH, "%.1f", roundedWinrate);
              String scoreLeadText = Utils.convertScoreToString(score, maxScoreMean);
              if (Lizzie.config.useDefaultInfoRowOrder
                  || Lizzie.config.suggestionInfoWinrate < Lizzie.config.suggestionInfoScoreLead) {
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
              boolean shouldShowMaxColorPlayouts = canShowMaxColor && move.playouts == maxPlayouts;
              boolean shouldShowMaxColorScoreLead =
                  canShowMaxColor && move.scoreMean == maxScoreMean;
              double score = move.scoreMean;
              if (Lizzie.board.getHistory().isBlacksTurn()) {
                if (Lizzie.config.showKataGoScoreLeadWithKomi) {
                  score = score + Lizzie.board.getHistory().getGameInfo().getKomi();
                }
              } else {
                if (Lizzie.config.showKataGoScoreLeadWithKomi) {
                  score = score - Lizzie.board.getHistory().getGameInfo().getKomi();
                }
                if (Lizzie.config.winrateAlwaysBlack) {
                  score = -score;
                }
              }
              String playoutsText = Utils.getPlayoutsString(move.playouts);
              String scoreLeadText = Utils.convertScoreToString(score, maxScoreMean);
              if (Lizzie.config.useDefaultInfoRowOrder
                  || Lizzie.config.suggestionInfoPlayouts < Lizzie.config.suggestionInfoScoreLead) {
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
                drawString(
                    g,
                    suggestionX,
                    suggestionY + stoneRadius * 15 / 35,
                    LizzieFrame.playoutsFont,
                    playoutsText,
                    stoneRadius * 0.77f,
                    stoneRadius * 1.8);
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
                    String.format(Locale.ENGLISH, "%.1f", roundedWinrate),
                    squareWidth * 0.46f,
                    stoneRadius * 1.9);
              } else {
                drawString(
                    g,
                    suggestionX,
                    suggestionY,
                    LizzieFrame.winrateFont,
                    String.format(Locale.ENGLISH, "%.1f", roundedWinrate),
                    squareWidth * 0.46f,
                    stoneRadius * 1.9);
              }
              if (shouldShowMaxColorWinrate) g.setColor(oriColor);
            } else if (showPlayouts) {
              boolean shouldShowMaxColorPlayouts = canShowMaxColor && move.playouts == maxPlayouts;
              if (shouldShowMaxColorPlayouts) g.setColor(maxColor);
              drawString(
                  g,
                  suggestionX,
                  suggestionY,
                  LizzieFrame.playoutsFont,
                  Utils.getPlayoutsString(move.playouts),
                  stoneRadius,
                  stoneRadius * 1.9);
              if (shouldShowMaxColorPlayouts) g.setColor(oriColor);
            } else if (showScoreLead) {
              double score = move.scoreMean;
              if (Lizzie.board.getHistory().isBlacksTurn()) {
                if (Lizzie.config.showKataGoScoreLeadWithKomi) {
                  score = score + Lizzie.board.getHistory().getGameInfo().getKomi();
                }
              } else {
                if (Lizzie.config.showKataGoScoreLeadWithKomi) {
                  score = score - Lizzie.board.getHistory().getGameInfo().getKomi();
                }
                if (Lizzie.config.winrateAlwaysBlack) {
                  score = -score;
                }
              }
              boolean shouldShowMaxColorScoreLead =
                  canShowMaxColor && move.scoreMean == maxScoreMean;
              String scoreLeadText = Utils.convertScoreToString(score, maxScoreMean);
              if (shouldShowMaxColorScoreLead) g.setColor(maxColor);
              drawString(
                  g,
                  suggestionX,
                  suggestionY,
                  LizzieFrame.winrateFont,
                  scoreLeadText,
                  stoneRadius,
                  stoneRadius * 1.7);
              if (shouldShowMaxColorScoreLead) g.setColor(oriColor);
            }
          }
        }
      } else {
        clearAfterMove();
      }
    }
  }

  private boolean isOnNext(int[] coord) {
    if (nextCoords.isEmpty()) return false;
    else {
      for (int[] nextCoord : nextCoords) {
        if (nextCoord[0] == coord[0] && nextCoord[1] == coord[1]) return true;
      }
    }
    return false;
  }

  private void drawOrder(Graphics2D g, int x, int y, int order, boolean blackToPlay) {
    // TODO Auto-generated method stub
    if (shouldShowPreviousBestMoves()) blackToPlay = !blackToPlay;
    drawStringForOrder(
        g,
        (int) round(x + squareWidth * 0.43),
        (int) round(y - squareWidth * 0.358),
        LizzieFrame.winrateFont,
        Font.PLAIN,
        String.valueOf(order + 1),
        squareWidth * 0.36f,
        squareWidth * 0.39,
        1,
        blackToPlay);
  }

  public void clearAfterMove() {
    if (!unImportantCleared) {
      unImportantSugg = new BufferedImage(boardWidth, boardHeight, TYPE_INT_ARGB);
      unImportantCleared = true;
    }
    nextMoveX = -2;
    nextMoveY = -2;
    isMouseOverNextBlunder = false;
    drawUnimportantSuggCount = 101;
    clearBranch();
  }

  private void drawLeelazSuggestionsUnimportant() {
    BufferedImage newUnImportantSugg = new BufferedImage(boardWidth, boardHeight, TYPE_INT_ARGB);
    hasDrawBackground = new boolean[Board.boardHeight * Board.boardWidth];
    Graphics2D g = newUnImportantSugg.createGraphics();
    g.setRenderingHint(KEY_ANTIALIASING, VALUE_ANTIALIAS_ON);
    int minAlpha = 32;
    float alphaFactor = 5.0f;
    float redHue = Color.RGBtoHSB(2, 0, 0, null)[0];
    float greenHue = Color.RGBtoHSB(0, 255, 0, null)[0];
    float cyanHue = Lizzie.config.bestMoveColor;
    if (bestMoves != null && !bestMoves.isEmpty()) {
      int maxPlayouts = 0;
      double maxWinrate = 0;
      for (MoveData move : bestMoves) {
        if (move.playouts > maxPlayouts) maxPlayouts = move.playouts;
        if (move.winrate > maxWinrate) maxWinrate = move.winrate;
      }
      for (int i = bestMoves.size() - 1; i >= 0; i--) {
        MoveData move = bestMoves.get(i);
        boolean isBestMove = bestMoves.get(0) == move;

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
          boolean outOfOrder =
              Lizzie.config.limitMaxSuggestion > 0
                  && move.order + 1 > Lizzie.config.limitMaxSuggestion
                  && !move.lastTimeUnlimited;
          if (!outOfOrder && move.order < 20) {
            continue;
          }
          if (Lizzie.frame.priorityMoveCoords.size() > 0) {
            boolean needSkip = false;
            for (String coords2 : Lizzie.frame.priorityMoveCoords) {
              if (coords2.equals(move.coordinate)) needSkip = true;
            }
            if (needSkip) {
              continue;
            }
          }
          if (Lizzie.board.getHistory().getCurrentHistoryNode().next().isPresent())
            if (Lizzie.board
                .getHistory()
                .getCurrentHistoryNode()
                .next()
                .get()
                .getData()
                .lastMove
                .isPresent()) {
              int nextMove[] =
                  Lizzie.board
                      .getHistory()
                      .getCurrentHistoryNode()
                      .next()
                      .get()
                      .getData()
                      .lastMove
                      .get();
              if (nextMove[0] == coords[0] && nextMove[1] == coords[1]) {
                continue;
              }
            }
          if (!Lizzie.config.showNoSuggCircle && outOfOrder && !move.lastTimeUnlimited) continue;
          if (Board.getIndex(coords[0], coords[1]) < hasDrawBackground.length)
            hasDrawBackground[Board.getIndex(coords[0], coords[1])] = true;
          float hue;
          if (isBestMove) {
            hue = cyanHue;
          } else {
            double fraction = percentPlayouts;
            fraction = percentPlayouts;
            fraction =
                Math.pow(fraction, (double) 1 / Lizzie.config.suggestionColorRatio); // 提升好选点温度
            hue = redHue + (greenHue - redHue) * (float) fraction;
          }

          float saturation = 1.0f;
          float brightness = 0.85f;
          float alpha;
          float alphaRatio = max(0, (float) log(percentPlayouts) / alphaFactor + 1);
          alpha = minAlpha + (maxAlpha - minAlpha) * alphaRatio;

          Color hsbColor = Color.getHSBColor(hue, saturation, brightness);
          Color color =
              new Color(hsbColor.getRed(), hsbColor.getGreen(), hsbColor.getBlue(), (int) alpha);
          if (!branchOpt.isPresent()) {
            //            if (isFancyBoard) {
            //              g.setPaint(paint);
            //              Composite comp = g.getComposite();
            //              if (percentPlayouts < 0.05) {
            //                g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC, 0.8f));
            //              }
            //              fillCircle(g, suggestionX, suggestionY, stoneRadius + 1);
            //              g.setComposite(comp);
            //            } else {
            //              g.setColor(noFancyColor);
            //              Composite comp = g.getComposite();
            //              if (percentPlayouts < 0.05)
            //                g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC, 0.8f));
            //              fillCircle(g, suggestionX, suggestionY, stoneRadius + 1);
            //              g.setComposite(comp);
            //            }
            g.setColor(color);
            fillCircle(g, suggestionX, suggestionY, stoneRadius + 1);
            float alphaCircle = 48 + 48 * alphaRatio;
            g.setColor(new Color(0, 0, 0, (int) alphaCircle));
            drawCircle(g, suggestionX, suggestionY, stoneRadius + 1, 26.5f);
          }
        }
      }
      unImportantCleared = false;
    }
    if (!branchOpt.isPresent()) unImportantSugg = newUnImportantSugg;
    g.dispose();
  }

  private void drawNextMoves(Graphics2D g) {
    List<BoardHistoryNode> nexts = Lizzie.board.getHistory().getNexts();
    Color color;
    if (nexts.size() > 0) {
      color = nexts.get(0).getData().lastMoveColor == Stone.BLACK ? Color.BLACK : Color.WHITE;
      g.setColor(color);
      for (int i = 0; i < nexts.size(); i++) {
        boolean first = (i == 0);
        nexts
            .get(i)
            .getData()
            .lastMove
            .ifPresent(
                nextMove -> {
                  nextCoords.add(nextMove);
                  int moveX = x + scaledMarginWidth + squareWidth * nextMove[0];
                  int moveY = y + scaledMarginHeight + squareHeight * nextMove[1];
                  if (first) {
                    boolean hasFillCircle = false;
                    if (Lizzie.config.showNextMoveBlunder
                        && !Lizzie.board.isPkBoard
                        && !Lizzie.frame.isShowingHeatmap
                        && !Lizzie.frame.isShowingPolicy) {
                      BoardData nextData = nexts.get(0).getData();
                      BoardData thisData = Lizzie.board.getHistory().getData();
                      boolean isMain = this.boardIndex != 1;
                      List<MoveData> thisBestMoves =
                          isMain ? thisData.bestMoves : thisData.bestMoves2;
                      List<MoveData> nextBestMoves =
                          isMain ? nextData.bestMoves : nextData.bestMoves2;
                      if (nextBestMoves != null
                          && nextBestMoves.size() > 0
                          && !(isMain ? nextData.isChanged : nextData.isChanged2)
                          && thisBestMoves != null
                          && thisBestMoves.size() > 0) {
                        if (notEnoughSuggestionAt(nextMove[0], nextMove[1], bestMoves)) {
                          isShowingNextMoveBlunder = true;
                          nextMoveX = nextMove[0];
                          nextMoveY = nextMove[1];

                          MoveData nextMoveData = nextBestMoves.get(0);
                          MoveData thisMoveData = thisBestMoves.get(0);
                          nextVisits = nextMoveData.playouts;
                          int[] nextCoords =
                              Board.convertNameToCoordinates(nextMoveData.coordinate);
                          nextPvX = nextCoords[0];
                          nextPvY = nextCoords[1];
                          nextPv = new ArrayList<String>();
                          for (String v : nextMoveData.variation) nextPv.add(v);
                          nextPv.add(0, Board.convertCoordinatesToName(nextMove[0], nextMove[1]));
                          nextPvVisits = new ArrayList<String>();
                          if (nextMoveData.pvVisits != null && !nextMoveData.pvVisits.isEmpty()) {
                            for (String v : nextMoveData.pvVisits) nextPvVisits.add(v);
                            nextPvVisits.add(0, String.valueOf(nextMoveData.playouts));
                          }
                          if (thisData.isKataData && nextData.isKataData) {
                            double winrateDiff =
                                thisMoveData.winrate - (100 - nextMoveData.winrate);
                            double scoreDiff = thisMoveData.scoreMean + nextMoveData.scoreMean;
                            if (winrateDiff >= 10 || scoreDiff >= 5) {
                              drawCircleBackground(g, moveX, moveY);
                              g.setColor(new Color(220, 80, 30, 85));
                              drawNextMoveBlunder(
                                  g,
                                  color,
                                  moveX,
                                  moveY,
                                  100 - nextMoveData.winrate,
                                  -nextMoveData.scoreMean,
                                  false);
                              hasFillCircle = true;
                            } else if (winrateDiff >= 1.5 || scoreDiff >= 1) {
                              g.setColor(new Color(220, 150, 30));
                              drawNextMoveBlunder(
                                  g,
                                  color,
                                  moveX,
                                  moveY,
                                  100 - nextMoveData.winrate,
                                  -nextMoveData.scoreMean,
                                  false);
                              hasFillCircle = true;
                            } else {
                              g.setColor(new Color(0, 220, 0));
                              drawNextMoveBlunder(
                                  g,
                                  color,
                                  moveX,
                                  moveY,
                                  100 - nextMoveData.winrate,
                                  -nextMoveData.scoreMean,
                                  true);
                              hasFillCircle = true;
                            }
                          } else {
                            double winrateDiff =
                                thisMoveData.winrate - (100 - nextMoveData.winrate);
                            if (winrateDiff >= 10) {
                              drawCircleBackground(g, moveX, moveY);
                              g.setColor(new Color(220, 80, 30, 85));
                              drawNextMoveBlunder(
                                  g, color, moveX, moveY, 100 - nextMoveData.winrate, false);
                              hasFillCircle = true;
                            } else if (winrateDiff >= 1) {
                              g.setColor(new Color(220, 150, 30));
                              drawNextMoveBlunder(
                                  g, color, moveX, moveY, 100 - nextMoveData.winrate, false);
                              hasFillCircle = true;
                            } else // if (winrateDiff <= -2)
                            {
                              g.setColor(new Color(0, 220, 0));
                              drawNextMoveBlunder(
                                  g, color, moveX, moveY, 100 - nextMoveData.winrate, true);
                              hasFillCircle = true;
                            }
                          }
                        }
                      }
                    }
                    if (!hasFillCircle) {
                      g.setStroke(new BasicStroke(Math.max(stoneRadius / 7f, 2f)));
                      drawCircle(g, moveX, moveY, stoneRadius + 2);
                    }
                  } else {
                    g.setStroke(new BasicStroke(Math.max(stoneRadius / 15f, 1f)));
                    drawCircle(
                        g, moveX, moveY, stoneRadius + 2); // Slightly outside best move circle
                  }
                });
      }
    }
  }

  private void drawCircleBackground(Graphics2D g, int moveX, int moveY) {
    // TODO Auto-generated method stub
    if (isFancyBoard) {
      g.setPaint(paint);
      Composite comp = g.getComposite();
      g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, 0.8f));
      fillCircle(g, moveX, moveY, stoneRadius + 1);
      g.setComposite(comp);
    } else {
      g.setColor(noFancyColor);
      Composite comp = g.getComposite();
      g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, 0.8f));
      fillCircle(g, moveX, moveY, stoneRadius + 1);
      g.setComposite(comp);
    }
  }

  private boolean notEnoughSuggestionAt(int coordX, int coordY, List<MoveData> bestMoves) {
    // TODO Auto-generated method stub
    if (bestMoves.isEmpty()) return true;
    if ((Lizzie.board.getHistory().isBlacksTurn() && Lizzie.config.showBlackCandidates)
        || (!Lizzie.board.getHistory().isBlacksTurn() && Lizzie.config.showWhiteCandidates)) {
      String coordsName = Board.convertCoordinatesToName(coordX, coordY);
      for (MoveData move : bestMoves) {
        if (move.coordinate.equals(coordsName)) {
          if (move.order > 0
              && move.playouts < Lizzie.config.minPlayoutsForNextMove
              && Lizzie.board
                      .getHistory()
                      .getCurrentHistoryNode()
                      .next()
                      .get()
                      .getData()
                      .getPlayouts()
                  >= Lizzie.config.minPlayoutsForNextMove) {
            shouldIgnoreBestMove = true;
            this.ignoreBestMoveX = coordX;
            this.ignoreBestMoveY = coordY;
            return true;
          } else return false;
        }
      }
    }
    return true;
  }

  private void drawNextBlunderFirstMove(Graphics2D g) {
    // TODO Auto-generated method stub
    int moveX = x + scaledMarginWidth + squareWidth * nextMoveX;
    int moveY = y + scaledMarginHeight + squareHeight * nextMoveY;
    if (Lizzie.config.usePureStone)
      drawStoneSimple(
          g, g, moveX, moveY, Lizzie.board.getHistory().isBlacksTurn() ? Stone.BLACK : Stone.WHITE);
    else
      drawStone(
          g, g, moveX, moveY, Lizzie.board.getHistory().isBlacksTurn() ? Stone.BLACK : Stone.WHITE);
    if (Lizzie.board.getHistory().isBlacksTurn()) g.setColor(Color.WHITE);
    else g.setColor(Color.BLACK);
    if (showBlunderWinrate && showBlunderScore) {
      if (Lizzie.config.suggestionInfoWinrate <= Lizzie.config.suggestionInfoScoreLead) {
        drawString(
            g,
            moveX,
            moveY,
            LizzieFrame.winrateFont,
            Font.PLAIN,
            nextBlunderWinrate,
            squareWidth * 0.39f,
            squareWidth * 0.815,
            1);
        drawString(
            g,
            moveX,
            moveY
                + (Float.parseFloat(nextBlunderScore) <= -10
                    ? (int) (squareWidth * 0.34)
                    : squareWidth * 11 / 30),
            LizzieFrame.winrateFont,
            Font.PLAIN,
            nextBlunderScore,
            squareWidth * 0.3f,
            squareWidth * 0.65,
            1);
      } else {
        drawString(
            g,
            moveX,
            moveY,
            LizzieFrame.winrateFont,
            Font.PLAIN,
            nextBlunderScore,
            squareWidth * 0.39f,
            squareWidth * 0.815,
            1);
        drawString(
            g,
            moveX,
            moveY + squareWidth * 11 / 30,
            LizzieFrame.winrateFont,
            Font.PLAIN,
            nextBlunderWinrate,
            squareWidth * 0.3f,
            squareWidth * 0.65,
            1);
      }
    } else if (showBlunderWinrate) {
      drawString(
          g,
          moveX,
          moveY,
          LizzieFrame.winrateFont,
          Font.PLAIN,
          nextBlunderWinrate,
          squareWidth * 0.4f,
          squareWidth * 0.835,
          0);
    } else if (showBlunderScore) {
      drawString(
          g,
          moveX,
          moveY,
          LizzieFrame.winrateFont,
          Font.PLAIN,
          nextBlunderScore,
          squareWidth * 0.4f,
          squareWidth * 0.835,
          0);
    }
  }

  private void drawNextMoveBlunder(
      Graphics2D g,
      Color color,
      int moveX,
      int moveY,
      double winrate,
      double score,
      boolean isGoodMove) {
    // TODO Auto-generated method stub
    fillCircle(g, moveX, moveY, stoneRadius + 1);
    g.setStroke(new BasicStroke(Math.max(stoneRadius / 7f, 2f)));
    g.setColor(color);
    drawCircle(g, moveX, moveY, stoneRadius + 2);
    if (isGoodMove) g.setColor(Color.BLACK);
    else g.setColor(Color.WHITE);
    if (Lizzie.config.winrateAlwaysBlack && !Lizzie.board.getData().blackToPlay)
      winrate = 100 - winrate;
    if (Lizzie.board.getHistory().isBlacksTurn()) {
      if (Lizzie.config.showKataGoScoreLeadWithKomi) {
        score = score + Lizzie.board.getHistory().getGameInfo().getKomi();
      }
    } else {
      if (Lizzie.config.showKataGoScoreLeadWithKomi) {
        score = score - Lizzie.board.getHistory().getGameInfo().getKomi();
      }
      if (Lizzie.config.winrateAlwaysBlack) {
        score = -score;
      }
    }
    if (Lizzie.config.showWinrateInSuggestion && Lizzie.config.showScoremeanInSuggestion) {
      nextBlunderWinrate = String.format(Locale.ENGLISH, "%.1f", winrate);
      nextBlunderScore = String.format(Locale.ENGLISH, "%.1f", score);
      showBlunderWinrate = true;
      showBlunderScore = true;
      if (Lizzie.config.suggestionInfoWinrate <= Lizzie.config.suggestionInfoScoreLead) {
        drawString(
            g,
            moveX,
            moveY,
            LizzieFrame.winrateFont,
            Font.PLAIN,
            nextBlunderWinrate,
            squareWidth * 0.39f,
            squareWidth * 0.815,
            1);
        drawString(
            g,
            moveX,
            moveY + (score <= -10 ? (int) (squareWidth * 0.34) : squareWidth * 11 / 30),
            LizzieFrame.winrateFont,
            Font.PLAIN,
            nextBlunderScore,
            squareWidth * 0.3f,
            squareWidth * 0.65,
            1);
      } else {
        drawString(
            g,
            moveX,
            moveY,
            LizzieFrame.winrateFont,
            Font.PLAIN,
            nextBlunderScore,
            squareWidth * 0.39f,
            squareWidth * 0.815,
            1);
        drawString(
            g,
            moveX,
            moveY + (int) (squareWidth * 0.34),
            LizzieFrame.winrateFont,
            Font.PLAIN,
            nextBlunderWinrate,
            squareWidth * 0.3f,
            squareWidth * 0.65,
            1);
      }
    } else if (Lizzie.config.showScoremeanInSuggestion) {
      showBlunderWinrate = false;
      showBlunderScore = true;
      nextBlunderScore = String.format(Locale.ENGLISH, "%.1f", score);
      drawString(
          g,
          moveX,
          moveY,
          LizzieFrame.winrateFont,
          Font.PLAIN,
          nextBlunderScore,
          squareWidth * 0.4f,
          squareWidth * 0.835,
          0);
    } else if (Lizzie.config.showWinrateInSuggestion) {
      nextBlunderWinrate = String.format(Locale.ENGLISH, "%.1f", winrate);
      showBlunderWinrate = true;
      showBlunderScore = false;
      drawString(
          g,
          moveX,
          moveY,
          LizzieFrame.winrateFont,
          Font.PLAIN,
          nextBlunderWinrate,
          squareWidth * 0.4f,
          squareWidth * 0.835,
          0);
    } else {
      showBlunderWinrate = false;
      showBlunderScore = false;
    }
    g.setColor(color);
  }

  private void drawNextMoveBlunder(
      Graphics2D g, Color color, int moveX, int moveY, double winrate, boolean isGoodMove) {
    // TODO Auto-generated method stub
    fillCircle(g, moveX, moveY, stoneRadius + 1);
    g.setStroke(new BasicStroke(Math.max(stoneRadius / 7f, 2f)));
    g.setColor(color);
    drawCircle(g, moveX, moveY, stoneRadius + 2);
    if (Lizzie.config.winrateAlwaysBlack && !Lizzie.board.getData().blackToPlay)
      winrate = 100 - winrate;
    if (Lizzie.config.showWinrateInSuggestion) {
      if (isGoodMove) g.setColor(Color.BLACK);
      else g.setColor(Color.WHITE);
      nextBlunderWinrate = String.format(Locale.ENGLISH, "%.1f", winrate);
      showBlunderWinrate = true;
      showBlunderScore = false;
      drawString(
          g,
          moveX,
          moveY,
          LizzieFrame.winrateFont,
          Font.PLAIN,
          nextBlunderWinrate,
          squareWidth * 0.4f,
          squareWidth * 0.835,
          0);
      g.setColor(color);
    } else {
      showBlunderWinrate = false;
      showBlunderScore = false;
    }
  }

  private void drawWoodenBoard(Graphics2D g) {
    if (Lizzie.config.usePureBoard) {
      // simple version
      isFancyBoard = false;
      g.setRenderingHint(KEY_ANTIALIASING, VALUE_ANTIALIAS_OFF);
      noFancyColor = Lizzie.config.pureBoardColor;
      g.setColor(noFancyColor);
      g.fillRect(0, 0, boardWidth, boardHeight);
    } else {
      // fancy version
      isFancyBoard = true;
      if (cachedBoardImage == emptyImage) {
        cachedBoardImage = Lizzie.config.theme.board();
      }

      drawTextureImage(g, cachedBoardImage, 0, 0, boardWidth, boardHeight, true);
      g.setStroke(new BasicStroke(1));
    }
  }

  private boolean isBigMargin() {
    if (Lizzie.board.isExtremlySmallBoard) return false;
    if ((Lizzie.config.showNameInBoard && !isEmptyName()) || Lizzie.config.showCoordinates)
      return true;
    if (Lizzie.frame.isShowingHeatmap
        && !Lizzie.frame.isAnaPlayingAgainstLeelaz
        && !Lizzie.leelaz.isZen) return true;
    else if (Lizzie.frame.isShowingPolicy
        && !Lizzie.leelaz.isKatago
        && !EngineManager.isEmpty
        && !Lizzie.leelaz.isZen) return true;
    return false;
  }

  private boolean isEmptyName() {
    return (Lizzie.board.getHistory().getGameInfo().getPlayerBlack().length() == 0
        && Lizzie.board.getHistory().getGameInfo().getPlayerWhite().length() == 0);
  }

  /**
   * Calculates the lengths and pixel margins from a given boardLength.
   *
   * @param boardLength go board's length in pixels; must be boardLength >= BOARD_SIZE - 1
   * @return an array containing the three outputs: new boardLength, scaledMargin, availableLength
   */
  private int[] calculatePixelMargins(
      int boardWidth, int boardHeight, boolean showCoordinates, boolean isBigMargin) {
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
    double marginWidth = (isBigMargin ? 0.055 : 0.03) / Board.boardWidth * 19.0;
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
      double marginHeight = (isBigMargin ? 0.055 : 0.03) / Board.boardHeight * 19.0;
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

  //  private void drawShadow(Graphics2D g, int centerX, int centerY, boolean isGhost) {
  //    drawShadow(g, centerX, centerY, isGhost, 1);
  //  }

  private BufferedImage cachedGhostShadow2 = null;
  private int cachedR2;
  private int cachedShadowSize2;
  private int cachedStoneCenter2;

  private void drawShadiwCache2() {
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

  private void drawShadow2(Graphics2D g1, int centerX, int centerY) {
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
  private void drawStone(Graphics2D g, Graphics2D gShadow, int centerX, int centerY, Stone color) {
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

  private void drawCapturedStone(
      Graphics2D g, int centerX, int centerY, Stone stone, boolean isMouseOver) {
    if (isMouseOver) {
      if (isFancyBoard) {
        g.setPaint(paint);
      } else {
        g.setColor(noFancyColor);
      }
      g.fillOval(
          centerX - stoneRadius, centerY - stoneRadius, 2 * stoneRadius + 1, 2 * stoneRadius + 1);
    }
    if (stone == Stone.BLACK_CAPTURED) g.setColor(new Color(0, 0, 0, isMouseOver ? 90 : 70));
    else g.setColor(new Color(255, 255, 255, isMouseOver ? 120 : 95));
    g.fillOval(
        centerX - stoneRadius, centerY - stoneRadius, 2 * stoneRadius + 1, 2 * stoneRadius + 1);
  }

  private void drawStoneSimple(
      Graphics2D g, Graphics2D gShadow, int centerX, int centerY, Stone color) {
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
    if (isShowingBranch) return;
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
                    if ((isIndependBoard
                            ? Lizzie.frame.independentMainBoard.isMouseOver(move[0], move[1])
                            : Lizzie.frame.isMouseOver(move[0], move[1]))
                        && isShowingBranch) continue;
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
              //     }
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
    g.fillOval(centerX - radius, centerY - radius, 2 * radius + 1, 2 * radius + 1);
  }

  //  private void fillCircleBest(Graphics2D g, int centerX, int centerY, int radius) {
  //    g.fillOval(centerX - radius - 1, centerY - radius - 1, 2 * radius + 3, 2 * radius + 3);
  //  }

  /** Draws the outline of a circle centered at (centerX, centerY) with radius $radius$ */
  private void drawCircle(Graphics2D g, int centerX, int centerY, int radius) {
    // g.setStroke(new BasicStroke(radius / 11.5f));
    g.drawOval(centerX - radius, centerY - radius, 2 * radius, 2 * radius);
  }

  private void drawCircle(Graphics2D g, int centerX, int centerY, int radius, float f) {
    g.setStroke(new BasicStroke(radius / f));
    g.drawOval(centerX - radius, centerY - radius, 2 * radius, 2 * radius);
  }

  private void drawPolygon(Graphics2D g, int centerX, int centerY, int radius) {
    int[] xPoints = {centerX, centerX - (radius / 2), centerX + (radius / 2)};
    int[] yPoints = {
      centerY - (10 * radius / 22), centerY + (8 * radius / 22), centerY + (8 * radius / 22)
    };
    g.fillPolygon(xPoints, yPoints, 3);
  }

  private void drawPolygonCircle(Graphics2D g, int centerX, int centerY, int radius) {
    int[] xPoints = {centerX, centerX - (radius / 2), centerX + (radius / 2)};
    int[] yPoints = {
      centerY - (10 * radius / 22), centerY + (8 * radius / 22), centerY + (8 * radius / 22)
    };
    g.drawPolygon(xPoints, yPoints, 3);
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
    // bounding box for debugging
    // g.drawRect(x-(int)maximumFontWidth/2, y - height/2 + verticalOffset,
    // (int)maximumFontWidth,
    // height+verticalOffset );
    g.drawString(string, x - fm.stringWidth(string) / 2, y + height / 2 + verticalOffset);
    return font;
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

  private int[] calculatePixelMargins(boolean isBigMargin) {
    return calculatePixelMargins(boardWidth, boardHeight, showCoordinates(), isBigMargin);
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
    boolean isBigMargin = isBigMargin();
    if (this.boardWidth != boardWidth || this.boardHeight != boardHeight) {
      this.boardWidth = boardWidth;
      this.boardHeight = boardHeight;
      setupSizeParameters(isBigMargin);
    } else if (isBigMargin != cachedIsBigMargin) {
      cachedIsBigMargin = isBigMargin;
      setupSizeParameters(isBigMargin);
    }
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
    //   int boardWidthWithoutMargins; // the pixel width of the game board without margins
    int marginHeight; // the pixel height of the margins
    //  int boardHeightWithoutMargins; // the pixel height of the game board without margins

    // calculate a good set of boardLength, scaledMargin, and
    // boardLengthWithoutMargins to use
    // int[] calculatedPixelMargins = calculatePixelMargins();
    // setBoardLength(calculatedPixelMargins[0], calculatedPixelMargins[3]);
    marginWidth = this.scaledMarginWidth;
    marginHeight = this.scaledMarginHeight;
    int oriX = x;
    int oriY = y;

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
    else {
      if (Lizzie.config.isDoubleEngineMode() && boardIndex == 0)
        return LizzieFrame.boardRenderer2.convertScreenToCoordinates(oriX, oriY);
      else return Optional.empty();
    }
    // return Board.isValid(x, y) ? Optional.of(new int[] {x, y}) : Optional.empty();
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
    int marginWidth = this.scaledMarginWidth;
    int marginHeight = this.scaledMarginHeight;
    if (x1 > this.x + boardWidth - marginHeight) x1 = this.x + boardWidth - marginHeight;
    if (x1 < this.x + marginHeight) x1 = this.x + marginHeight - 1;
    if (y1 > this.y + boardHeight - marginWidth) y1 = this.y + boardHeight - marginWidth;
    if (y1 < this.y + marginWidth) y1 = this.y + marginWidth - 1;
    if (x2 > this.x + boardWidth - marginHeight) x2 = this.x + boardWidth - marginHeight;
    if (x2 < this.x + marginHeight) x2 = this.x + marginHeight - 1;
    if (y2 > this.y + boardHeight - marginWidth) y2 = this.y + boardHeight - marginWidth;
    if (y2 < this.y + marginWidth) y2 = this.y + marginWidth - 1;
    // transform the pixel coordinates to board coordinates
    int x1Check = squareWidth == 0 ? 0 : Math.floorDiv(x1 - this.x - marginWidth, squareWidth);
    int y1Check = squareHeight == 0 ? 0 : Math.floorDiv(y1 - this.y - marginHeight, squareHeight);
    int x2Check = squareWidth == 0 ? 0 : Math.floorDiv(x2 - this.x - marginWidth, squareWidth);
    int y2Check = squareHeight == 0 ? 0 : Math.floorDiv(y2 - this.y - marginHeight, squareHeight);
    if (x1Check == x2Check || y1Check == y2Check) return Optional.empty();
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
    stoneRadius = max(squareWidth, squareHeight) < 4 ? 1 : max(squareWidth, squareHeight) - 1;
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
    if (Lizzie.config.isThinkingMode() && this.boardIndex == 2) return 999;
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

  public void notShowingBranch() {}

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

  public void addSuggestionAsBranch() {
    mouseOveredMove()
        .ifPresent(
            m -> {
              if (m.variation.size() > 0) {
                if (this.isMouseOverNextBlunder) {
                  Lizzie.board.getHistory().next();
                }
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
                if (this.isMouseOverNextBlunder) Lizzie.board.getHistory().previous();
                Lizzie.frame.reRenderTree();
                Lizzie.frame.refresh();
              }
            });
  }

  public void drawScore(GroupInfo boardGroupInfo) {
    BufferedImage newScoreImage = new BufferedImage(boardWidth, boardHeight, TYPE_INT_ARGB);
    Graphics2D g = newScoreImage.createGraphics();
    g.setRenderingHint(KEY_ANTIALIASING, VALUE_ANTIALIAS_ON);
    g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

    for (int j = 0; j < Board.boardHeight; j++) {
      for (int i = 0; i < Board.boardWidth; i++) {
        int stoneX = scaledMarginWidth + squareWidth * i;
        int stoneY = scaledMarginHeight + squareHeight * j;
        if (boardGroupInfo.groupStatus[i][j].value == 0
            || boardGroupInfo.groupStatus[i][j].isMarkedEmpty) {
          g.setColor(Color.RED);
          g.fillOval(
              stoneX - squareWidth * 2 / 10,
              stoneY - squareWidth * 2 / 10,
              squareWidth * 4 / 10,
              squareWidth * 4 / 10);
        } else {
          if (boardGroupInfo.groupStatus[i][j].value == 1) g.setColor(Color.BLACK);
          else if (boardGroupInfo.groupStatus[i][j].value == 2) g.setColor(Color.WHITE);
          g.fillRect(
              stoneX - squareWidth * 3 / 10,
              stoneY - squareWidth * 3 / 10,
              squareWidth * 6 / 10,
              squareWidth * 6 / 10);
        }
      }
    }
    scoreImage = newScoreImage;
    g.dispose();
  }

  public void clearScore() {
    scoreImage = new BufferedImage(boardWidth, boardHeight, TYPE_INT_ARGB);
  }

  public void refreshVariation() {
    if (isShowingBranch) {
      isShowingBranch = false;
      displayedBranchLength = SHOW_NORMAL_BOARD;
      Lizzie.frame.refresh();
    }
  }

  //  private Color reverseColor(Color color) {
  //    // System.out.println("color=="+color);
  //    int r = color.getRed();
  //    int g = color.getGreen();
  //    int b = color.getBlue();
  //    int r_ = 255 - r;
  //    int g_ = 255 - g;
  //    int b_ = 255 - b;
  //    Color newColor = new Color(r_, g_, b_);
  //    return newColor;
  //  }
}
