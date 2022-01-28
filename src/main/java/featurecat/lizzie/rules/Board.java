package featurecat.lizzie.rules;

import static java.lang.Math.min;
import static java.util.Collections.singletonList;

import featurecat.lizzie.Lizzie;
import featurecat.lizzie.analysis.EngineManager;
import featurecat.lizzie.analysis.GameInfo;
import featurecat.lizzie.analysis.Leelaz;
import featurecat.lizzie.analysis.MoveData;
import featurecat.lizzie.gui.LizzieFrame;
import featurecat.lizzie.gui.Message;
import featurecat.lizzie.gui.ScoreResult;
import featurecat.lizzie.util.Utils;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Stack;
import java.util.function.BiConsumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.swing.*;

public class Board {
  public static int boardHeight = 19;
  public static int boardWidth = 19;
  public int insertoricurrentMoveNumber = 0;
  public ArrayList<Integer> insertorimove = new ArrayList<Integer>();
  public ArrayList<Boolean> insertoriisblack = new ArrayList<Boolean>();

  public ArrayList<Movelist> tempmovelistForGenMoveGame;
  public ArrayList<Movelist> tempmovelist;
  public ArrayList<Movelist> tempmovelist2;
  public ArrayList<Movelist> tempallmovelist;
  public ArrayList<Movelistwr> movelistwr = new ArrayList<Movelistwr>();

  private static final String alphabet = "ABCDEFGHJKLMNOPQRSTUVWXYZ";
  private static final String alphabetWithI = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
  private BoardHistoryList history;
  // private boolean scoreMode;
  private boolean analysisMode;
  public String boardstatbeforeedit = "";
  public String boardstatafteredit = "";
  public boolean isLoadingFile = false;
  public boolean isPkBoard = false;
  public boolean isGameBoard = false;
  public boolean isPkBoardKataB = false;
  public boolean isPkBoardKataW = false;
  public boolean isKataBoard = false;
  public boolean hasStartStone = false;
  public ArrayList<Movelist> startStonelist = new ArrayList<Movelist>();

  private boolean forceRefresh;
  private boolean forceRefresh2;
  public boolean hasBestHeatMove = false;
  public int bestHeatMoveX;
  public int bestHeatMoveY;
  private ArrayList<Movelist> tempMovelistForSpin;
  public GroupInfo boardGroupInfo;
  private boolean hasBigBranch = false;
  public boolean isExtremlySmallBoard = false;
  private boolean neverPassedInGame = true;

  public boolean isMouseOnStone = false;
  private boolean preMouseOnStone = false;
  public BoardHistoryNode mouseOnNode;
  public int[] mouseOnStoneCoords = LizzieFrame.outOfBoundCoordinate;

  public Board() {
    initialize(false);
  }

  /** Initialize the board completely */
  private void initialize(boolean isEngineGame) {
    LizzieFrame.fileNameTitle = "";
    LizzieFrame.curFile = null;
    // scoreMode = false;
    isGameBoard = false;
    neverPassedInGame = true;
    analysisMode = false;
    Optional.empty();
    forceRefresh = false;
    forceRefresh2 = false;
    hasBigBranch = false;
    history = new BoardHistoryList(BoardData.empty(boardWidth, boardHeight));
    if (isEngineGame) {
      Lizzie.board
          .getHistory()
          .getGameInfo()
          .setKomi(Lizzie.board.getHistory().getGameInfo().getKomi());
    } else {
      if (LizzieFrame.boardRenderer != null) LizzieFrame.boardRenderer.clearAfterMove();
      if (LizzieFrame.boardRenderer2 != null) LizzieFrame.boardRenderer2.clearAfterMove();
      LizzieFrame.forceRecreate = true;
    }
    if (boardWidth < 4) isExtremlySmallBoard = true;
    else isExtremlySmallBoard = false;
    Lizzie.leelaz.clearPonderLimit();
  }

  /**
   * Calculates the array index of a stone stored at (x, y)
   *
   * @param x the x coordinate
   * @param y the y coordinate
   * @return the array index
   */
  public static int getIndex(int x, int y) {
    return x * Board.boardHeight + y;
  }

  public static int[] getCoord(int index) {
    //    int y = index / Board.boardWidth;
    //    int x = index % Board.boardWidth;
    //    return new int[] {x, y};
    int y = index % Board.boardHeight;
    int x = (index - y) / Board.boardHeight;
    return new int[] {x, y};
  }

  public int[] getCoordKataGo(int index) {
    int x = index % Board.boardWidth;
    int y = (index - x) / Board.boardWidth;
    return new int[] {x, y};
  }

  /**
   * Converts a named coordinate eg C16, T5, K10, etc to an x and y coordinate
   *
   * @param namedCoordinate a capitalized version of the named coordinate. Must be a valid 19x19 Go
   *     coordinate, without I
   * @return an optional array of coordinates, empty for pass and resign
   */
  public static Optional<int[]> asCoordinates(String namedCoordinate) {
    return asCoordinates(namedCoordinate, boardHeight);
  }

  public static Optional<int[]> asCoordinates(String namedCoordinate, int boardHeight) {
    namedCoordinate = namedCoordinate.trim();
    if (namedCoordinate.equalsIgnoreCase("pass") || namedCoordinate.equalsIgnoreCase("resign")) {
      return Optional.empty();
    }
    // coordinates take the form C16 A19 Q5 K10 etc. I is not used.
    String reg = "([A-HJ-Z]+)(\\d+)";
    Pattern p = Pattern.compile(reg);
    Matcher m = p.matcher(namedCoordinate);
    if (m.find() && m.groupCount() == 2) {
      String xCoords = m.group(1);
      int x =
          xCoords.length() == 2
              ? (asDigit(xCoords.substring(0, 1)) + 1) * 25 + asDigit(xCoords.substring(1, 2))
              : asDigit(xCoords);
      int y = boardHeight - Integer.parseInt(m.group(2));
      if (y < 0)
        for (int i = 1; i < m.group(2).length(); i++) {
          y = boardHeight - Integer.parseInt(m.group(2).substring(0, m.group(2).length() - i));
          if (y >= 0) break;
        }
      return Optional.of(new int[] {x, y});
    } else {
      reg = "\\(([\\d]+),([\\d]+)\\)";
      p = Pattern.compile(reg);
      m = p.matcher(namedCoordinate);
      if (m.find() && m.groupCount() == 2) {
        int x = Integer.parseInt(m.group(1));
        int y = Integer.parseInt(m.group(2)); // boardHeight - Integer.parseInt(m.group(2)) - 1;
        return Optional.of(new int[] {x, y});
      } else {
        return Optional.empty();
      }
    }
  }

  public static int asDigit(String name) {
    // coordinates take the form C16 A19 Q5 K10 etc. I is not used.
    int base = alphabet.length();
    char names[] = name.toCharArray();
    int length = names.length;
    if (length > 0) {
      int x = 0;
      for (int i = length - 1; i >= 0; i--) {
        int index = alphabet.indexOf(names[i]);
        if (index == -1) {
          return index;
        }
        x += index * Math.pow(base, length - i - 1);
      }
      return x;
    } else {
      return -1;
    }
  }

  public static String asName(int c) {
    String alphabetString =
        Lizzie.config.useIinCoordsName || Lizzie.config.useFoxStyleCoords
            ? alphabetWithI
            : alphabet;
    if (boardWidth
        > (Lizzie.config.useIinCoordsName || Lizzie.config.useFoxStyleCoords ? 26 : 25)) {
      return String.valueOf(c + 1);
    }
    StringBuilder name = new StringBuilder();
    int base = alphabetString.length();
    int n = c;
    ArrayDeque<Integer> ad = new ArrayDeque<Integer>();
    if (n > 0) {
      while (n > 0) {
        ad.addFirst(n < 25 && c >= 25 ? n % base - 1 : n % base);
        n /= base;
      }
    } else {
      ad.addFirst(n);
    }
    ad.forEach(i -> name.append(alphabetString.charAt(i)));
    return name.toString();
  }

  public static String coordsAsName(int c) {
    StringBuilder name = new StringBuilder();
    int base = alphabet.length();
    int n = c;
    ArrayDeque<Integer> ad = new ArrayDeque<Integer>();
    if (n > 0) {
      while (n > 0) {
        ad.addFirst(n < 25 && c >= 25 ? n % base - 1 : n % base);
        n /= base;
      }
    } else {
      ad.addFirst(n);
    }
    ad.forEach(i -> name.append(alphabet.charAt(i)));
    return name.toString();
  }

  /**
   * Converts a x and y coordinate to a named coordinate eg C16, T5, K10, etc
   *
   * @param x x coordinate -- must be valid
   * @param y y coordinate -- must be valid
   * @return a string representing the coordinate
   */
  public static String convertCoordinatesToName(int x, int y) {
    // coordinates take the form C16 A19 Q5 K10 etc. I is not used.
    if (boardWidth > 25 || boardHeight > 25) {
      return String.format(Locale.ENGLISH, "(%d,%d)", x, y); // boardHeight - y - 1);
    } else {
      return coordsAsName(x) + (boardHeight - y);
    }
  }

  public static int[] convertNameToCoordinates(String name, int boardHeight) {
    // coordinates take the form C16 A19 Q5 K10 etc. I is not used.
    Optional<int[]> coords = asCoordinates(name, boardHeight);
    if (coords.isPresent()) return coords.get();
    else return LizzieFrame.outOfBoundCoordinate;
  }

  public static int[] convertNameToCoordinates(String name) {
    // coordinates take the form C16 A19 Q5 K10 etc. I is not used.
    Optional<int[]> coords = asCoordinates(name);
    if (coords.isPresent()) return coords.get();
    else return LizzieFrame.outOfBoundCoordinate;
    //    if (boardWidth > 25 || boardHeight > 25) {
    //      int coords[] = new int[2];
    //
    //      int x = Integer.parseInt(name.replaceAll("\\(|\\)", "").split(",")[0]);
    //      int y = Integer.parseInt(name.replaceAll("\\(|\\)", "").split(",")[1]);
    //      coords[0] = x;
    //      coords[1] = y;
    //      return coords; // boardHeight - y - 1);
    //    } else {
    //      char i = name.charAt(0);
    //      int x;
    //      if (i > 73) x = i - 66;
    //      else x = i - 65;
    //      int y = boardHeight - Integer.parseInt(name.substring(1));
    //      int coords[] = new int[2];
    //      coords[0] = x;
    //      coords[1] = y;
    //      return coords;
    //    }
  }

  /**
   * Checks if a coordinate is valid
   *
   * @param x x coordinate
   * @param y y coordinate
   * @return whether or not this coordinate is part of the board
   */
  public static boolean isValid(int x, int y) {
    return x >= 0 && x < boardWidth && y >= 0 && y < boardHeight;
  }

  public static boolean isValid(int[] c) {
    return c != null && c.length == 2 && isValid(c[0], c[1]);
  }

  public void analyzeAllDiffNodes(ArrayList<BoardHistoryNode> nodeList) {
    for (BoardHistoryNode node : nodeList) {
      moveToAnyPosition(node);
      clearAfterMove();
      while (!node.diffAnalyzed) {
        try {
          if (Lizzie.config.isAutoAna) {
            Thread.sleep(50);
          } else {
            return;
          }
        } catch (InterruptedException e) {
          // TODO Auto-generated catch block
          // e.printStackTrace();
          return;
        }
      }
    }
    LizzieFrame.toolbar.stopAutoAna(false, false);
  }

  public void analyzeAllNodesAfter(BoardHistoryNode node) {
    // 待完成
    moveToAnyPosition(node);
    clearAfterMove();
    while (!node.analyzed) {
      try {
        if (Lizzie.config.isAutoAna) {
          Thread.sleep(50);
        } else {
          return;
        }
      } catch (InterruptedException e) {
        // TODO Auto-generated catch block
        // e.printStackTrace();
        return;
      }
    }
    if (Lizzie.board.getHistory().getCurrentHistoryNode().isMainTrunk()) {
      if (Lizzie.config.autoAnaEndMove != -1) {
        if (Lizzie.config.autoAnaEndMove < Lizzie.board.getHistory().getData().moveNumber) {
          LizzieFrame.toolbar.stopAutoAna(true, false);
          return;
        }
      }
      if (!node.next().isPresent()) {
        LizzieFrame.toolbar.stopAutoAna(true, false);
        return;
      }
    }
    if (node.numberOfChildren() > 1) {
      // Variation
      List<BoardHistoryNode> subNodes = node.getVariations();
      for (int i = subNodes.size() - 1; i >= 0; i--) {
        analyzeAllNodesAfter(subNodes.get(i));
      }
    } else if (node.numberOfChildren() == 1) {
      analyzeAllNodesAfter(node.next().orElse(null));
    }
  }

  public void clearAnalyzeStatusAfter(BoardHistoryNode node) {
    Stack<BoardHistoryNode> stack = new Stack<>();
    stack.push(node);
    while (!stack.isEmpty()) {
      BoardHistoryNode cur = stack.pop();
      cur.analyzed = false;
      if (cur.numberOfChildren() >= 1) {
        for (int i = cur.numberOfChildren() - 1; i >= 0; i--)
          stack.push(cur.getVariations().get(i));
      }
    }
  }

  public void clearDiffAnalyzeStatusAfter(ArrayList<BoardHistoryNode> diffList) {
    for (BoardHistoryNode node : diffList) node.diffAnalyzed = false;
  }

  public void clearBestMovesAfterForFirstEngine(BoardHistoryNode node) {
    {
      Stack<BoardHistoryNode> stack = new Stack<>();
      stack.push(node);
      while (!stack.isEmpty()) {
        BoardHistoryNode cur = stack.pop();
        if (cur.getData().getPlayouts() > 0) {
          cur.getData().isChanged = true;
        }
        if (cur.numberOfChildren() >= 1) {
          for (int i = cur.numberOfChildren() - 1; i >= 0; i--)
            stack.push(cur.getVariations().get(i));
        }
      }
    }
  }

  public void clearBestMovesAfter(BoardHistoryNode node) {
    clearBestMovesAfterForFirstEngine(node);
    if (Lizzie.config.isDoubleEngineMode()) clearBestMovesAfterForSecondEngine(node);
  }

  public void clearBestMovesInfomationAfter(BoardHistoryNode node) {
    Stack<BoardHistoryNode> stack = new Stack<>();
    stack.push(node);
    while (!stack.isEmpty()) {
      BoardHistoryNode cur = stack.pop();
      if (cur.getData().getPlayouts() > 0) {
        cur.getData().bestMoves = new ArrayList<>();
        cur.getData().winrate = 50;
        cur.getData().setPlayouts(0);
        cur.getData().scoreMean = 0;
        cur.nodeInfo = new NodeInfo();
      }
      if (cur.numberOfChildren() >= 1) {
        for (int i = cur.numberOfChildren() - 1; i >= 0; i--)
          stack.push(cur.getVariations().get(i));
      }
    }
  }

  public void clearBestMovesInfomationAfter2(BoardHistoryNode node) {
    Stack<BoardHistoryNode> stack = new Stack<>();
    stack.push(node);
    while (!stack.isEmpty()) {
      BoardHistoryNode cur = stack.pop();
      if (cur.getData().getPlayouts2() > 0) {
        cur.getData().bestMoves2 = new ArrayList<>();
        cur.getData().winrate2 = 50;
        cur.getData().setPlayouts2(0);
        cur.getData().scoreMean2 = 0;
        cur.nodeInfo2 = new NodeInfo();
      }
      if (cur.numberOfChildren() >= 1) {
        for (int i = cur.numberOfChildren() - 1; i >= 0; i--)
          stack.push(cur.getVariations().get(i));
      }
    }
  }

  public void clearBestMovesInfomation(BoardHistoryNode node) {
    if (node.getData().getPlayouts() > 0) node.getData().bestMoves = new ArrayList<>();
    node.getData().winrate = 50;
    node.getData().setPlayouts(0);
    node.getData().scoreMean = 0;
    node.nodeInfo = new NodeInfo();
  }

  public void clearbestmovesInfomation2(BoardHistoryNode node) {
    if (node.getData().getPlayouts2() > 0) node.getData().bestMoves2 = new ArrayList<>();
    node.getData().winrate2 = 50;
    node.getData().setPlayouts2(0);
    node.getData().scoreMean2 = 0;
    node.nodeInfo2 = new NodeInfo();
  }

  public void clearNodeInfo(BoardHistoryNode node) {
    Stack<BoardHistoryNode> stack = new Stack<>();
    stack.push(node);
    while (!stack.isEmpty()) {
      BoardHistoryNode cur = stack.pop();
      if (Lizzie.config.isDoubleEngineMode()) {}
      if (cur.numberOfChildren() >= 1) {
        for (int i = cur.numberOfChildren() - 1; i >= 0; i--)
          stack.push(cur.getVariations().get(i));
      }
    }
  }

  public void resetbestmoves(BoardHistoryNode node) {
    // if (node.getData().moveNumber <= movenumber) {
    if (node.getData().getPlayouts() > 0) node.getData().tryToClearBestMoves();
    // }
    if (node.numberOfChildren() > 1) {
      // Variation
      for (BoardHistoryNode sub : node.getVariations()) {
        resetbestmoves(sub);
      }
    } else if (node.numberOfChildren() == 1) {
      resetbestmoves(node.next().orElse(null));
    }
  }

  public void clearBestMovesAfterForSecondEngine(BoardHistoryNode node) {
    Stack<BoardHistoryNode> stack = new Stack<>();
    stack.push(node);
    while (!stack.isEmpty()) {
      BoardHistoryNode cur = stack.pop();
      if (cur.getData().getPlayouts2() > 0) {
        cur.getData().isChanged2 = true;
      }
      if (cur.numberOfChildren() >= 1) {
        for (int i = cur.numberOfChildren() - 1; i >= 0; i--)
          stack.push(cur.getVariations().get(i));
      }
    }
  }

  public void clearbestmoves() {
    if (history.getCurrentHistoryNode().getData().getPlayouts() > 0)
      history.getCurrentHistoryNode().getData().isChanged = true;
    if (Lizzie.config.isDoubleEngineMode()) clearbestmoves2();
  }

  public void clearbestmoves2() {
    if (history.getCurrentHistoryNode().getData().getPlayouts2() > 0)
      history.getCurrentHistoryNode().getData().isChanged2 = true;
  }

  public void savelistforswitch() {
    tempmovelist = getMoveList();
  }

  public void savelist(int movenumber) {
    tempmovelist = getMoveList();
    int length = tempmovelist.size() - movenumber;
    for (int i = 0; i < length; i++) {
      tempmovelist.remove(0);
    }
  }

  public ArrayList<Movelist> savelistforeditmode() {
    if (boardstatbeforeedit == "") {
      try {
        boardstatbeforeedit = SGFParser.saveToString(false);
      } catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
      tempmovelist = getmovelistWithOutStartStone();
    }
    tempallmovelist = getallmovelist();
    boardstatafteredit = "";
    tempmovelist2 = new ArrayList<Movelist>();
    return tempmovelist;
  }

  public void cleanedittemp() {
    boardstatbeforeedit = "";
    boardstatafteredit = "";
    tempmovelist2 = new ArrayList<Movelist>();
    tempmovelist = new ArrayList<Movelist>();
  }

  public void clearEditStuff() {
    boardstatafteredit = "";
    boardstatbeforeedit = "";
    tempmovelist.clear();
    tempmovelist2.clear();
  }

  public void cleanedit() {
    if (boardstatbeforeedit != "") {
      try {
        boardstatafteredit = SGFParser.saveToString(false);
      } catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
      tempmovelist2 = getMoveList();
    }
    SGFParser.loadFromStringforedit(boardstatbeforeedit);
    setmovelistForEditClean(tempmovelist);
    boardstatbeforeedit = "";
    tempmovelist = new ArrayList<Movelist>();
    return;
  }

  public void reedit() {
    if (boardstatafteredit != "") {
      try {
        boardstatbeforeedit = SGFParser.saveToString(false);
      } catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
      tempmovelist = getMoveList();
    }
    SGFParser.loadFromStringforedit(boardstatafteredit);
    setmovelistForEditClean(tempmovelist2);
    boardstatafteredit = "";
    tempmovelist2.clear();
    return;
  }

  public void resetlistforeditmode() {
    setMoveList(tempmovelist, false, false);
  }

  public void setlistforeditmode1() {
    tempmovelist = getMoveList();
  }

  public void setlistforeditmode2() {
    setMoveList(tempmovelist, false, false);
  }

  public void setlist(ArrayList<Movelist> list) {
    setMoveList(list, false, false);
  }

  public void setlist() {
    setMoveList(tempmovelist, false, false);
  }

  public void setlistforswitch() {
    setMoveList(tempmovelist, false, false);
    tempmovelist.clear();
  }

  /**
   * Open board again when the SZ property is setup by sgf
   *
   * @param size
   */
  public void reopen(int width, int height) {
    width = (width >= 2) ? width : 19;
    height = (height >= 2) ? height : 19;

    if (width != boardWidth || height != boardHeight) {
      boardWidth = width;
      boardHeight = height;
      Zobrist.init();
      clear(false);
      Lizzie.leelaz.boardSize(boardWidth, boardHeight);
      Lizzie.leelaz.ponder();
      forceRefresh = true;
      forceRefresh2 = true;
      Lizzie.frame.redrawBoardrendererBackground();
      Lizzie.frame.refresh();
    }
  }

  public void reopenOnlyBoard(int width, int height) {
    width = (width >= 2) ? width : 19;
    height = (height >= 2) ? height : 19;

    if (width != boardWidth || height != boardHeight) {
      boardWidth = width;
      boardHeight = height;
      Zobrist.init();
      clear(false);
      //  Lizzie.leelaz.boardSize(boardWidth, boardHeight);
      //  Lizzie.leelaz.ponder();
      //  Lizzie.leelaz.setResponseUpToDate();
      forceRefresh = true;
      forceRefresh2 = true;
    }
  }

  public void open(int width, int height) {
    width = (width >= 2) ? width : 19;
    height = (height >= 2) ? height : 19;

    if (width != boardWidth || height != boardHeight) {
      boardWidth = width;
      boardHeight = height;
      Zobrist.init();
      // mvnumber = new int[boardHeight * boardWidth];
      clearHasDrawBackground();
      // Lizzie.leelaz.boardSize(boardWidth, boardHeight);
      forceRefresh = true;
      forceRefresh2 = true;
    }
  }

  public void clearHasDrawBackground() {
    LizzieFrame.boardRenderer.hasDrawBackground = new boolean[boardHeight * boardWidth];
    if (LizzieFrame.boardRenderer2 != null)
      LizzieFrame.boardRenderer2.hasDrawBackground = new boolean[boardHeight * boardWidth];
  }

  public boolean isForceRefresh() {
    return forceRefresh;
  }

  public boolean isForceRefresh2() {
    return forceRefresh2;
  }

  public void setForceRefresh(boolean forceRefresh) {
    this.forceRefresh = forceRefresh;
  }

  public void setForceRefresh2(boolean forceRefresh) {
    this.forceRefresh2 = forceRefresh;
  }

  /**
   * The comment. Thread safe
   *
   * @param comment the comment of stone
   */
  public void comment(String comment) {
    synchronized (this) {
      //      String[] params = comment.split("\n");
      //      comment = "";
      //      boolean first = true;
      //      for (int i = 0; i < params.length; i++) {
      //        if (!params[i].startsWith("贴目")) {
      //          if (first) {
      //            comment += params[i];
      //            first = false;
      //          } else comment += "\n" + params[i];
      //        }
      //      }
      history.getData().comment = comment;
    }
  }

  /**
   * Update the move number. Thread safe
   *
   * @param moveNumber the move number of stone
   */
  public void moveNumber(int moveNumber) {
    synchronized (this) {
      BoardData data = history.getData();
      if (data.lastMove.isPresent()) {
        int[] moveNumberList = history.getMoveNumberList();
        moveNumberList[Board.getIndex(data.lastMove.get()[0], data.lastMove.get()[1])] = moveNumber;
        Optional<BoardHistoryNode> node = history.getCurrentHistoryNode().previous();
        while (node.isPresent() && node.get().numberOfChildren() <= 1) {
          BoardData nodeData = node.get().getData();
          if (nodeData.lastMove.isPresent() && nodeData.moveNumber >= moveNumber) {
            moveNumber = (moveNumber > 1) ? moveNumber - 1 : 0;
            moveNumberList[Board.getIndex(nodeData.lastMove.get()[0], nodeData.lastMove.get()[1])] =
                moveNumber;
          }
          node = node.get().previous();
        }
      }
    }
  }

  /**
   * Add a stone to the board representation. Thread safe
   *
   * @param x x coordinate
   * @param y y coordinate
   * @param color the type of stone to place
   */
  public void addStone(int x, int y, Stone color) {
    synchronized (this) {
      if (!isValid(x, y) || history.getStones()[getIndex(x, y)] != Stone.EMPTY) return;

      Stone[] stones = history.getData().stones;
      Zobrist zobrist = history.getData().zobrist;

      // set the stone at (x, y) to color
      stones[getIndex(x, y)] = color;
      zobrist.toggleStone(x, y, color);

      Lizzie.frame.refresh();
    }
  }

  /**
   * Remove a stone from the board representation. Thread safe
   *
   * @param x x coordinate
   * @param y y coordinate
   * @param color the type of stone to place
   */
  public void removeStone(int x, int y, Stone color) {
    synchronized (this) {
      if (!isValid(x, y) || history.getStones()[getIndex(x, y)] == Stone.EMPTY) return;

      BoardData data = history.getData();
      Stone[] stones = data.stones;
      Zobrist zobrist = data.zobrist;

      // set the stone at (x, y) to empty
      Stone oriColor = stones[getIndex(x, y)];
      stones[getIndex(x, y)] = Stone.EMPTY;
      zobrist.toggleStone(x, y, oriColor);
      data.moveNumberList[Board.getIndex(x, y)] = 0;

      Lizzie.frame.refresh();
    }
  }

  /**
   * Add a key and value to node
   *
   * @param key
   * @param value
   */
  public void addNodeProperty(String key, String value) {
    synchronized (this) {
      history.getData().addProperty(key, value);
      if ("MN".equals(key)) {
        moveNumber(Integer.parseInt(value));
      }
    }
  }

  /**
   * Add a keys and values to node
   *
   * @param properties
   */
  public void addNodeProperties(Map<String, String> properties) {
    synchronized (this) {
      history.getData().addProperties(properties);
    }
  }

  /**
   * The pass. Thread safe
   *
   * @param color the type of pass
   */
  public void pass(Stone color) {
    pass(color, false, false, false);
  }

  public void pass(Stone color, boolean newBranch) {
    pass(color, newBranch, false, false);
  }

  public void pass(Stone color, boolean newBranch, boolean dummy) {
    pass(color, newBranch, dummy, false);
  }

  public void editmovelist(ArrayList<Movelist> movelist, int[] coords, int x, int y) {
    //   int lenth = movelist.size();
    //  if (Lizzie.board.hasStartStone) movenum += startStonelist.size();
    for (Movelist move : movelist) {
      if (move.x == coords[0] && move.y == coords[1]) {
        move.x = x;
        move.y = y;
        break;
      }
    }
    // movelist.get(lenth - movenum).x = x;
    // movelist.get(lenth - movenum).y = y;
  }

  public void editmovelistswitch(ArrayList<Movelist> movelist, int[] coords) {
    // if (Lizzie.board.hasStartStone) movenum += startStonelist.size();
    for (Movelist move : movelist) {
      if (move.x == coords[0] && move.y == coords[1]) {
        move.isblack = !move.isblack;
        break;
      }
    }
    //  movelist.get(lenth - movenum).isblack = !movelist.get(lenth - movenum).isblack;
  }

  public void editmovelistadd(
      ArrayList<Movelist> movelist, int movenum, int x, int y, boolean isblack) {
    int lenth = movelist.size();
    if (Lizzie.board.hasStartStone) movenum += startStonelist.size();
    Movelist mv = new Movelist();
    mv.isblack = isblack;
    mv.x = x;
    mv.y = y;
    mv.movenum = movenum + 1;
    movelist.add(lenth - movenum, mv);
  }

  public void editmovelistdelete(ArrayList<Movelist> movelist, int[] coords) {
    //   if (Lizzie.board.hasStartStone) movenum += startStonelist.size();
    for (Movelist move : movelist) {
      if (move.x == coords[0] && move.y == coords[1]) {
        movelist.remove(move);
        break;
      }
    }
    //  movelist.remove(lenth - movenum);
  }

  public synchronized void resetMoveList(ArrayList<Movelist> moveList) {
    setMoveList(moveList, false, false);
  }

  public synchronized void resetMoves() {
    ArrayList<Movelist> mv = Lizzie.board.getMoveList();
    setMoveList(mv, false, false);
  }

  public void setMoveList(ArrayList<Movelist> movelist, boolean forSpin, boolean noCommand) {
    boolean oriPlaySound = Lizzie.config.playSound;
    Lizzie.config.playSound = false;
    Lizzie.board.isLoadingFile = true;
    while (previousMove(false)) ;
    Lizzie.board.isLoadingFile = false;
    if (!forSpin) {
      if (Lizzie.board.hasStartStone) {
        Lizzie.board.hasStartStone = false;
        startStonelist = new ArrayList<Movelist>();
      }
    }
    int lenth = movelist.size();
    for (int i = 0; i < lenth; i++) {
      Movelist move = movelist.get(lenth - 1 - i);
      if (!move.ispass) {
        if (noCommand) {
          history.place(move.x, move.y, move.isblack ? Stone.BLACK : Stone.WHITE);
        } else {
          if (history.getStones()[getIndex(move.x, move.y)] != Stone.EMPTY)
            Lizzie.leelaz.playMove(
                move.isblack ? Stone.BLACK : Stone.WHITE, convertCoordinatesToName(move.x, move.y));
          else place(move.x, move.y, move.isblack ? Stone.BLACK : Stone.WHITE);
        }
      } else {
        if (noCommand) {
          history.pass(move.isblack ? Stone.BLACK : Stone.WHITE);
        } else {
          pass(move.isblack ? Stone.BLACK : Stone.WHITE);
        }
      }
    }
    Lizzie.config.playSound = oriPlaySound;
  }

  public void setMoveListWithFlatten(
      ArrayList<Movelist> movelist, int flattenNumber, boolean flattenBlackToPlay) {

    boolean oriPlaySound = Lizzie.config.playSound;
    Lizzie.config.playSound = false;
    while (previousMove(false)) ;
    int lenth = movelist.size();
    for (int i = 0; i < lenth; i++) {
      Movelist move = movelist.get(lenth - 1 - i);
      if (!move.ispass) {
        place(move.x, move.y, move.isblack ? Stone.BLACK : Stone.WHITE);
      } else if (i + 1 > flattenNumber) {
        pass(move.isblack ? Stone.BLACK : Stone.WHITE);
      }
      if (i + 1 == flattenNumber) {
        Lizzie.board.flatten();
        Lizzie.board.getHistory().getData().blackToPlay = flattenBlackToPlay;
      }
    }
    Lizzie.config.playSound = oriPlaySound;
  }

  public void setmovelistForEditClean(ArrayList<Movelist> movelist) {
    boolean oriPlaySound = Lizzie.config.playSound;
    Lizzie.config.playSound = false;
    while (previousMove(false)) ;
    int lenth = movelist.size();
    if (hasStartStone) {
      for (int i = 0; i < startStonelist.size(); i++) {
        Movelist move = startStonelist.get(i);
        Lizzie.leelaz.playMove(
            move.isblack ? Stone.BLACK : Stone.WHITE, convertCoordinatesToName(move.x, move.y));
      }
    }
    for (int i = 0; i < lenth; i++) {
      Movelist move = movelist.get(lenth - 1 - i);
      if (!move.ispass) {
        place(move.x, move.y, move.isblack ? Stone.BLACK : Stone.WHITE);
        //	        try {
        //	          mvnumber[getIndex(move.x, move.y)] = i + 1;
        //	        } catch (Exception ex) {
        //	        }
      } else {
        pass(move.isblack ? Stone.BLACK : Stone.WHITE, true, false, false);
      }
    }
    Lizzie.config.playSound = oriPlaySound;
  }

  public ArrayList<Movelist> getallmovelist() {
    ArrayList<Movelist> movelist = new ArrayList<Movelist>();
    // while (nextMove()) ;
    Optional<BoardHistoryNode> node = history.getEnd().now();
    Optional<int[]> passstep = Optional.empty();
    while (node.isPresent()) {
      Optional<int[]> lastMove = node.get().getData().lastMove;
      if (lastMove == passstep) {
        Movelist move = new Movelist();
        move.ispass = true;
        move.isblack = node.get().getData().lastMoveColor.isBlack();
        movelist.add(move);
      } else {
        if (lastMove.isPresent()) {
          int[] n = lastMove.get();
          Movelist move = new Movelist();
          move.x = n[0];
          move.y = n[1];
          move.ispass = false;
          move.isblack = node.get().getData().lastMoveColor.isBlack();
          move.movenum = node.get().getData().moveNumber;
          movelist.add(move);
        }
      }
      if (node.get().extraStones != null) {
        for (ExtraStones stone : node.get().extraStones) {
          Movelist move = new Movelist();
          move.x = stone.x;
          move.y = stone.y;
          move.ispass = false;
          move.isblack = stone.isBlack;
          move.movenum = -1;
          movelist.add(move);
        }
      }
      node = node.get().previous();
    }
    if (movelist.size() > 0) movelist.remove(movelist.size() - 1);
    if (hasStartStone) {
      for (Movelist mv : startStonelist) {
        movelist.add(mv);
      }
    }
    return movelist;
  }

  public void setStartListStone(int[] coordinates, boolean isBlack) {
    Movelist move = new Movelist();
    move.x = coordinates[0];
    move.y = coordinates[1];
    move.ispass = false;
    move.isblack = isBlack;
    move.movenum = startStonelist.size() + 1;
    startStonelist.add(move);
  }

  public void addStartList() {
    Optional<BoardHistoryNode> node = history.getCurrentHistoryNode().now();
    Optional<int[]> passstep = Optional.empty();
    if (node.isPresent()) {
      Optional<int[]> lastMove = node.get().getData().lastMove;
      if (lastMove == passstep) {
        Movelist move = new Movelist();
        move.ispass = true;
        move.isblack = node.get().getData().lastMoveColor.isBlack();
        startStonelist.add(move);
        node = node.get().previous();
      } else {
        if (lastMove.isPresent()) {

          int[] n = lastMove.get();
          Movelist move = new Movelist();
          move.x = n[0];
          move.y = n[1];
          move.ispass = false;
          move.isblack = node.get().getData().lastMoveColor.isBlack();
          move.movenum = node.get().getData().moveNumber;
          startStonelist.add(move);
        }
      }
    }
  }

  public void addStartListAll() {
    Optional<BoardHistoryNode> node = history.getCurrentHistoryNode().now();
    Optional<int[]> passstep = Optional.empty();
    while (node.isPresent()) {
      Optional<int[]> lastMove = node.get().getData().lastMove;
      if (lastMove == passstep) {
        node = node.get().previous();
      } else {
        if (lastMove.isPresent()) {

          int[] n = lastMove.get();
          Movelist move = new Movelist();
          move.x = n[0];
          move.y = n[1];
          move.ispass = false;
          move.isblack = node.get().getData().lastMoveColor.isBlack();
          move.movenum = node.get().getData().moveNumber;
          startStonelist.add(move);
        }
      }
      try {
        node = node.get().previous();
      } catch (Exception e) {
        break;
      }
    }
  }

  public synchronized void resendMoveToEngine(Leelaz leelaz) {
    ArrayList<Movelist> mv = getMoveList();
    leelaz.sendCommand("clear_board");
    Lizzie.board.restoreMoveNumber(mv, false, leelaz);
  }

  public ArrayList<Movelist> getMoveList() {
    ArrayList<Movelist> movelist = new ArrayList<Movelist>();
    BoardHistoryNode node = history.getCurrentHistoryNode();
    while (node.previous().isPresent()) {
      Optional<int[]> lastMove = node.getData().lastMove;
      if (!lastMove.isPresent()) {
        if (!node.getData().dummy) {
          Movelist move = new Movelist();
          move.ispass = true;
          move.isblack = node.getData().lastMoveColor.isBlack();
          movelist.add(move);
        }
      } else {
        int[] n = lastMove.get();
        Movelist move = new Movelist();
        move.x = n[0];
        move.y = n[1];
        move.ispass = false;
        move.isblack = node.getData().lastMoveColor.isBlack();
        move.movenum = node.getData().moveNumber;
        movelist.add(move);
      }
      if (node.extraStones != null) {
        for (ExtraStones stone : node.extraStones) {
          Movelist move = new Movelist();
          move.x = stone.x;
          move.y = stone.y;
          move.ispass = false;
          move.isblack = stone.isBlack;
          move.movenum = -1;
          movelist.add(move);
        }
      }
      node = node.previous().get();
    }
    if (hasStartStone) {
      for (Movelist mv : startStonelist) {
        movelist.add(mv);
      }
    }
    return movelist;
  }

  public ArrayList<Movelist> getmovelistForSaveLoad() {
    ArrayList<Movelist> movelist = new ArrayList<Movelist>();
    Optional<BoardHistoryNode> node = history.getCurrentHistoryNode().now();
    Optional<int[]> passstep = Optional.empty();
    try {
      if (node.get().topOfFatherBranch2().variations.get(0).getData().dummy)
        node = node.get().topOfFatherBranch2().previous();
    } catch (Exception e) {
    }
    while (node.isPresent()) {
      Optional<int[]> lastMove = node.get().getData().lastMove;
      if (lastMove == passstep) {
        Movelist move = new Movelist();
        move.ispass = true;
        move.isblack = node.get().getData().lastMoveColor.isBlack();
        movelist.add(move);
      } else {
        if (lastMove.isPresent()) {
          int[] n = lastMove.get();
          Movelist move = new Movelist();
          move.x = n[0];
          move.y = n[1];
          move.ispass = false;
          move.isblack = node.get().getData().lastMoveColor.isBlack();
          move.movenum = node.get().getData().moveNumber;
          movelist.add(move);
        }
      }
      if (node.get().extraStones != null) {
        for (ExtraStones stone : node.get().extraStones) {
          Movelist move = new Movelist();
          move.x = stone.x;
          move.y = stone.y;
          move.ispass = false;
          move.isblack = stone.isBlack;
          move.movenum = -1;
          movelist.add(move);
        }
      }
      node = node.get().previous();
    }
    if (movelist.size() > 0) movelist.remove(movelist.size() - 1);
    if (hasStartStone) {
      for (Movelist mv : startStonelist) {
        movelist.add(mv);
      }
    }
    return movelist;
  }

  public ArrayList<Movelist> getmovelistWithOutStartStone() {
    ArrayList<Movelist> movelist = new ArrayList<Movelist>();
    Optional<BoardHistoryNode> node = history.getCurrentHistoryNode().now();
    Optional<int[]> passstep = Optional.empty();
    while (node.isPresent()) {
      Optional<int[]> lastMove = node.get().getData().lastMove;
      if (lastMove == passstep) {
        Movelist move = new Movelist();
        move.ispass = true;
        move.isblack = node.get().getData().lastMoveColor.isBlack();
        movelist.add(move);
      } else {
        if (lastMove.isPresent()) {
          int[] n = lastMove.get();
          Movelist move = new Movelist();
          move.x = n[0];
          move.y = n[1];
          move.ispass = false;
          move.isblack = node.get().getData().lastMoveColor.isBlack();
          move.movenum = node.get().getData().moveNumber;
          movelist.add(move);
        }
      }
      if (node.get().extraStones != null) {
        for (ExtraStones stone : node.get().extraStones) {
          Movelist move = new Movelist();
          move.x = stone.x;
          move.y = stone.y;
          move.ispass = false;
          move.isblack = stone.isBlack;
          move.movenum = -1;
          movelist.add(move);
        }
      }
      node = node.get().previous();
    }
    if (movelist.size() > 0) movelist.remove(movelist.size() - 1);
    return movelist;
  }

  public ArrayList<Movelist> getmovelist(Optional<BoardHistoryNode> node) {
    ArrayList<Movelist> movelist = new ArrayList<Movelist>();
    //  Optional<BoardHistoryNode> node = history.getCurrentHistoryNode().now();
    Optional<int[]> passstep = Optional.empty();
    while (node.isPresent()) {
      Optional<int[]> lastMove = node.get().getData().lastMove;
      if (lastMove == passstep) {
        Movelist move = new Movelist();
        move.ispass = true;
        move.isblack = node.get().getData().lastMoveColor.isBlack();
        movelist.add(move);
      } else {
        if (lastMove.isPresent()) {
          int[] n = lastMove.get();
          Movelist move = new Movelist();
          move.x = n[0];
          move.y = n[1];
          move.ispass = false;
          move.isblack = node.get().getData().lastMoveColor.isBlack();
          move.movenum = node.get().getData().moveNumber;
          movelist.add(move);
        }
      }
      if (node.get().extraStones != null) {
        for (ExtraStones stone : node.get().extraStones) {
          Movelist move = new Movelist();
          move.x = stone.x;
          move.y = stone.y;
          move.ispass = false;
          move.isblack = stone.isBlack;
          move.movenum = -1;
          movelist.add(move);
        }
      }
      node = node.get().previous();
    }
    if (movelist.size() > 0) movelist.remove(movelist.size() - 1);
    if (hasStartStone) {
      for (Movelist mv : startStonelist) {
        movelist.add(mv);
      }
    }
    return movelist;
  }

  public void playAllMovelist(AllMovelist listHead, int startMoveNumber) {
    if (listHead.variations.isEmpty()) return;
    ArrayList<BoardHistoryNode> tempHistoryNode = new ArrayList<BoardHistoryNode>();
    tempMovelistForSpin = new ArrayList<Movelist>();
    playMovelistAfter(listHead.variations.get(0), tempHistoryNode, true, startMoveNumber);
    if (tempMovelistForSpin.size() <= 0 && this.hasStartStone && this.startStonelist.size() > 0) {
      tempMovelistForSpin = getmovelist(history.getCurrentHistoryNode().now());
    }
    history.getStart();
    setMoveList(tempMovelistForSpin, true, false);
  }

  public void playMovelistAfter(
      AllMovelist listNode,
      ArrayList<BoardHistoryNode> tempHistoryNode,
      boolean firstTime,
      int startMoveNumber) {
    if (!firstTime) listNode.playNode();
    if (startMoveNumber > 0
        && getHistory().getCurrentHistoryNode().isMainTrunk()
        && getHistory().getMoveNumber() == startMoveNumber) {
      hasStartStone = true;
      startStonelist = new ArrayList<Movelist>();
      addStartListAll();
      if (startStonelist.get(startStonelist.size() - 1).ispass)
        startStonelist.remove(startStonelist.size() - 1);
      Lizzie.board.flatten();
      startMoveNumber = -1;
    }
    if (listNode.comment != null)
      history.getCurrentHistoryNode().getData().comment = listNode.comment;
    if (listNode.currentPosition) {
      tempMovelistForSpin = getmovelist(history.getCurrentHistoryNode().now());
    }
    if (listNode.variations.isEmpty() && !tempHistoryNode.isEmpty()) {
      history.setHead(tempHistoryNode.get(tempHistoryNode.size() - 1));
      tempHistoryNode.remove(tempHistoryNode.size() - 1);
    }
    if (listNode.variations.size() > 1) {
      for (int i = 0; i < listNode.variations.size() - 1; i++)
        tempHistoryNode.add(history.getCurrentHistoryNode());
      for (AllMovelist sub : listNode.variations) {
        playMovelistAfter(sub, tempHistoryNode, false, startMoveNumber);
      }
    } else if (listNode.variations.size() == 1) {
      playMovelistAfter(listNode.variations.get(0), tempHistoryNode, false, startMoveNumber);
    }
  }

  public AllMovelist getAllMovelist(int type) {
    boolean oriHasStartStone = hasStartStone;
    BoardHistoryNode node = history.getStart();
    AllMovelist listHead = new AllMovelist();
    ArrayList<AllMovelist> tempListNode = new ArrayList<AllMovelist>();
    addtoAllMovelistAfter(node, listHead, tempListNode, type);
    hasStartStone = oriHasStartStone;
    return listHead; // .variations.get(0);
  }

  public void addtoAllMovelistAfter(
      BoardHistoryNode node, AllMovelist listHead, ArrayList<AllMovelist> tempListNode, int type) {
    Stack<BoardHistoryNode> stack = new Stack<>();
    stack.push(node);
    while (!stack.isEmpty()) {
      BoardHistoryNode cur = stack.pop();
      AllMovelist listNode = addToList(cur, listHead, type);
      if (hasStartStone) {
        hasStartStone = false;
        for (int i = 0; i < startStonelist.size(); i++) {
          Movelist mv = startStonelist.get(i);
          if (!mv.ispass) {
            int[] lastCoords = {mv.x, mv.y};
            Optional<int[]> lastMove = Optional.of(lastCoords);
            listNode = addMoveToList(lastMove, listNode, type, mv.isblack, "", false);
          }
        }
      }
      if (!cur.next().isPresent() && !tempListNode.isEmpty()) {
        listHead = tempListNode.get(tempListNode.size() - 1);
        tempListNode.remove(tempListNode.size() - 1);
      } else listHead = listNode;
      if (cur.numberOfChildren() >= 1) {
        if (cur.numberOfChildren() > 1) tempListNode.add(listHead);
        for (int i = cur.numberOfChildren() - 1; i >= 0; i--)
          stack.push(cur.getVariations().get(i));
      }
    }
  }

  public AllMovelist addToList(BoardHistoryNode node, AllMovelist list, int type) {
    if (node.extraStones != null) {
      for (ExtraStones stone : node.extraStones) {
        int[] lastCoords = {stone.x, stone.y};
        Optional<int[]> lastMove = Optional.of(lastCoords);
        list = addMoveToList(lastMove, list, type, stone.isBlack, "", false);
      }
    }
    Optional<int[]> lastMove = node.getData().lastMove;
    if (!lastMove.isPresent() && node.getData().dummy) return list;
    boolean isBlack = node.getData().lastMoveColor.isBlack();
    String comment = node.getData().comment;
    boolean currentPosition = node == history.getCurrentHistoryNode();
    return addMoveToList(lastMove, list, type, isBlack, comment, currentPosition);
  }

  private AllMovelist addMoveToList(
      Optional<int[]> lastMove,
      AllMovelist list,
      int type,
      boolean isBlack,
      String comment,
      boolean currentPosition) {
    AllMovelist move = new AllMovelist();
    if (!lastMove.isPresent()) {
      move.ispass = true;
      move.previous = list;
      if (type == 6) move.isblack = !isBlack;
      else move.isblack = isBlack;
    } else {
      if (lastMove.isPresent()) {
        int[] n = lastMove.get();
        move.isblack = isBlack;
        switch (type) {
          case 0: // 不改变
            move.x = n[0];
            move.y = n[1];
            break;
          case 1: // 向右旋转
            move.x = boardWidth - 1 - n[1];
            move.y = n[0];
            break;
          case 2: // 向左旋转
            move.x = n[1];
            move.y = boardHeight - 1 - n[0];
            break;
          case 3: // 水平翻转
            move.x = boardWidth - 1 - n[0];
            move.y = n[1];
            break;
          case 4: // 垂直翻转
            move.x = n[0];
            move.y = boardHeight - 1 - n[1];
            break;
          case 6: // 交换黑白
            move.x = n[0];
            move.y = n[1];
            move.isblack = !isBlack;
            break;
          default: // 不改变
            move.x = -n[0];
            move.y = n[1];
        }
        //        move.x = boardWidth - 1 - n[0];
        //        move.y = n[1];
        move.ispass = false;
        move.previous = list;
      }
    }
    move.comment = comment;
    if (currentPosition) move.currentPosition = true;
    list.variations.add(move);
    return move;
  }

  public Stone getstonestat(int coords[]) {
    Stone stones[] = history.getData().stones.clone();
    return stones[getIndex(coords[0], coords[1])];
  }

  public int getmovenumber(int coords[]) {
    Stone stones[] = history.getData().stones.clone();
    if (!stones[getIndex(coords[0], coords[1])].isBlack()
        && !stones[getIndex(coords[0], coords[1])].isWhite()) {
      return -1;
    }
    int mvnumbers = -1;
    //    try {
    //      mvnumbers = mvnumber[getIndex(coords[0], coords[1])];
    //    } catch (Exception ex) {
    //    }
    return mvnumbers;
  }

  public void pass(Stone color, boolean newBranch, boolean dummy, boolean changeMove) {
    synchronized (this) {
      // check to see if this move is being replayed in history
      if (history.getNext().map(n -> !n.lastMove.isPresent()).orElse(false) && !newBranch) {
        // this is the next move in history. Just increment history so that we don't
        // erase the
        // redo's
        history.next();
        if (Lizzie.config.playSound) Utils.playVoiceFile();
        if (!EngineManager.isEngineGame) Lizzie.leelaz.playMove(color, "pass");

        if (Lizzie.frame.isPlayingAgainstLeelaz
            && Lizzie.frame.playerIsBlack != getData().blackToPlay)
          Lizzie.leelaz.genmove((history.isBlacksTurn() ? "b" : "w"));
        clearAfterMove();
        return;
      }

      Stone[] stones = history.getStones().clone();
      Zobrist zobrist = history.getZobrist();

      int moveNumber = history.getMoveNumber() + 1;
      int[] moveNumberList =
          newBranch && history.getNext(true).isPresent()
              ? new int[Board.boardWidth * Board.boardHeight]
              : history.getMoveNumberList().clone();

      // build the new game state
      BoardData newState =
          new BoardData(
              stones,
              Optional.empty(),
              color,
              color.equals(Stone.WHITE),
              zobrist,
              moveNumber,
              moveNumberList,
              history.getData().blackCaptures,
              history.getData().whiteCaptures,
              0,
              0);
      newState.dummy = dummy;
      history.addOrGoto(newState, newBranch);
      // update leelaz with pass
      if (!Lizzie.leelaz.isInputCommand && !EngineManager.isEngineGame)
        Lizzie.leelaz.playMove(color, "pass");

      if (Lizzie.frame.isPlayingAgainstLeelaz
          && Lizzie.frame.playerIsBlack != getData().blackToPlay)
        Lizzie.leelaz.genmove((history.isBlacksTurn() ? "b" : "w"));

      // update history with pass
      if (Lizzie.config.playSound) Utils.playVoiceFile();
      Lizzie.frame.refresh();
      if (Lizzie.frame.isPlayingAgainstLeelaz || Lizzie.frame.isAnaPlayingAgainstLeelaz) {
        if (Lizzie.frame.playerIsBlack != Lizzie.board.getHistory().isBlacksTurn()) {
          if (neverPassedInGame) {
            neverPassedInGame = false;
            Utils.showMsg(Lizzie.resourceBundle.getString("LizzieFrame.passInGameTip"));
          }
        }
      }
    }
  }

  /** overloaded method for pass(), chooses color in an alternating pattern */
  public void pass() {
    pass(history.isBlacksTurn() ? Stone.BLACK : Stone.WHITE);
  }

  /**
   * Places a stone onto the board representation. Thread safe
   *
   * @param x x coordinate
   * @param y y coordinate
   * @param color the type of stone to place
   */
  public void place(int x, int y, Stone color) {
    place(x, y, color, false);
  }

  public void place(int x, int y, Stone color, boolean newBranch) {
    place(x, y, color, newBranch, false, false);
  }

  public void placeForSync(int x, int y, Stone color, boolean newBranch) {
    place(x, y, color, newBranch, true, false);
    Lizzie.frame.readBoard.lastMovePlayByLizzie = false;
  }

  public void placeForManual(int x, int y) {
    placeForManual(x, y, history.isBlacksTurn() ? Stone.BLACK : Stone.WHITE);
  }

  public void placeForManual(int x, int y, Stone color) {
    place(x, y, color, false, false, true);
  }

  private void modifyStart() {
    Lizzie.leelaz.modifyStart();
    if (Lizzie.config.isDoubleEngineMode() && Lizzie.leelaz2 != null) Lizzie.leelaz2.modifyStart();
  }

  private void modifyEnd() {
    Lizzie.leelaz.setModifyEnd();
    if (Lizzie.config.isDoubleEngineMode() && Lizzie.leelaz2 != null) Lizzie.leelaz2.setModifyEnd();
  }

  public void place(
      int x, int y, Stone color, boolean newBranch, boolean forSync, boolean forManual) {
    boolean noCheckSuiKo = false;
    LizzieFrame.boardRenderer.removedrawmovestone();
    Lizzie.frame.suggestionclick = LizzieFrame.outOfBoundCoordinate;
    if (Lizzie.frame.isCounting) {
      Lizzie.frame.clearKataEstimate();
      Lizzie.frame.estimateResults.btnEstimate.setText(
          Lizzie.resourceBundle.getString("EstimateResults.estimate"));
      Lizzie.frame.estimateResults.iscounted = false;
      Lizzie.frame.isCounting = false;
    }
    synchronized (this) {
      if (!isValid(x, y) || (history.getStones()[getIndex(x, y)] != Stone.EMPTY && !newBranch))
        return;
      updateWinrate();
      if (EngineManager.isEngineGame) SGFParser.appendTime();
      // modifyStart();
      if (!forSync
          && !Lizzie.frame.bothSync
          && (LizzieFrame.urlSgf || Lizzie.frame.syncBoard)
          && Lizzie.board.getHistory().getCurrentHistoryNode()
              == Lizzie.board.getHistory().getMainEnd()) {
        //      newBranch = true;
        //      //  changeMove=true;
        boolean hasVairation = false;
        BoardHistoryNode node = Lizzie.board.getHistory().getCurrentHistoryNode();
        for (int i = 0; i < node.variations.size(); i++) {
          Optional<int[]> nodeCoords = node.variations.get(i).getData().lastMove;

          if (nodeCoords.isPresent()) {
            int[] coords = nodeCoords.get();
            if (coords[0] == x && coords[1] == y) {
              hasVairation = true;
              // changeMove=false;
            }
          }
        }
        if (!hasVairation) {
          boolean isEmpty = EngineManager.isEmpty;
          EngineManager.isEmpty = true;
          Lizzie.board.pass(color, false, true);
          Lizzie.board.previousMove(false);
          Lizzie.board.getHistory().place(x, y, color, true);
          noCheckSuiKo = true;
          EngineManager.isEmpty = isEmpty;
          Lizzie.leelaz.playMove(color, convertCoordinatesToName(x, y));
          // modifyEnd(false);
          clearAfterMove();
          return;
          // Lizzie.leelaz.playMove(color, convertCoordinatesToName(x, y));
        }
      }
      //      try {
      //        mvnumber[getIndex(x, y)] = history.getCurrentHistoryNode().getData().moveNumber + 1;
      //      } catch (Exception ex) {
      //      }
      double nextWinrate = -100;
      if (history.getData().winrate >= 0) nextWinrate = 100 - history.getData().winrate;

      // check to see if this coordinate is being replayed in history
      Optional<int[]> nextLast = history.getNext().flatMap(n -> n.lastMove);
      if (nextLast.isPresent()
          && nextLast.get()[0] == x
          && nextLast.get()[1] == y
          && !newBranch
          && Lizzie.frame.blackorwhite == 0) {
        // this is the next coordinate in history. Just increment history so that we
        // don't erase the
        // redo's
        history.next();
        updateIsBest();
        if (Lizzie.config.playSound) Utils.playVoiceFile();
        // should be opposite from the bottom case
        if (Lizzie.frame.isPlayingAgainstLeelaz
            && Lizzie.frame.playerIsBlack != getData().blackToPlay) {
          Lizzie.leelaz.playMove(color, convertCoordinatesToName(x, y));
          Lizzie.leelaz.genmove((Lizzie.board.getData().blackToPlay ? "b" : "w"));
        } else if (!Lizzie.frame.isPlayingAgainstLeelaz && !EngineManager.isEngineGame) {
          Lizzie.leelaz.playMove(color, convertCoordinatesToName(x, y));
        }
        //  modifyEnd(false);
        clearAfterMove();
        return;
      }
      // load a copy of the data at the current node of history
      Stone[] stones = history.getStones().clone();
      Zobrist zobrist = history.getZobrist();
      Optional<int[]> lastMove = Optional.of(new int[] {x, y});
      int moveNumber = history.getMoveNumber() + 1;
      int moveMNNumber =
          history.getMoveMNNumber() > -1 && !newBranch ? history.getMoveMNNumber() + 1 : -1;
      int[] moveNumberList =
          newBranch && history.getNext(true).isPresent()
              ? new int[Board.boardWidth * Board.boardHeight]
              : history.getMoveNumberList().clone();
      if (Lizzie.frame.isTrying) moveNumberList[Board.getIndex(x, y)] = -moveNumber;
      else moveNumberList[Board.getIndex(x, y)] = moveMNNumber > -1 ? moveMNNumber : moveNumber;

      // set the stone at (x, y) to color
      stones[getIndex(x, y)] = color;
      zobrist.toggleStone(x, y, color);

      // remove enemy stones
      int capturedStones = 0;
      int isSuicidal = 0;
      if (!Lizzie.config.noCapture) {
        capturedStones += removeDeadChain(x + 1, y, color.opposite(), stones, zobrist);
        capturedStones += removeDeadChain(x, y + 1, color.opposite(), stones, zobrist);
        capturedStones += removeDeadChain(x - 1, y, color.opposite(), stones, zobrist);
        capturedStones += removeDeadChain(x, y - 1, color.opposite(), stones, zobrist);

        // check to see if the player made a suicidal coordinate
        isSuicidal = removeDeadChain(x, y, color, stones, zobrist);
      }
      for (int i = 0; i < Board.boardWidth * Board.boardHeight; i++) {
        if (stones[i].equals(Stone.EMPTY)) {
          moveNumberList[i] = 0;
        }
      }

      int bc = history.getData().blackCaptures;
      int wc = history.getData().whiteCaptures;
      if (color.isBlack()) bc += capturedStones;
      else wc += capturedStones;
      BoardData newState =
          new BoardData(
              stones,
              lastMove,
              color,
              color.equals(Stone.WHITE),
              zobrist,
              moveNumber,
              moveNumberList,
              bc,
              wc,
              nextWinrate,
              0);
      newState.moveMNNumber = moveMNNumber;
      newState.dummy = false;
      // don't make this coordinate if it is suicidal or violates superko
      if (!noCheckSuiKo) {
        if (history.violatesKoRule(newState)) {
          // modifyEnd();
          return;
        }
        if (Lizzie.leelaz.canSuicidal) {
          if (isSuicidal == 1) {
            //   modifyEnd();
            return;
          }
        } else if (isSuicidal > 0) {
          //   modifyEnd();
          return;
        }
      }
      // update history with this coordinate
      // update leelaz with board position
      if (EngineManager.isEngineGame) {
        if (color.isBlack()) {
          if (Lizzie.engineManager.firstEngineCountDown != null
              && !Lizzie.engineManager.firstEngineCountDown.isPlayBlack)
            Lizzie.engineManager.firstEngineCountDown.sendTimeLeft(false);
          else if (Lizzie.engineManager.secondEngineCountDown != null
              && !Lizzie.engineManager.secondEngineCountDown.isPlayBlack)
            Lizzie.engineManager.secondEngineCountDown.sendTimeLeft(false);
        } else {
          if (Lizzie.engineManager.firstEngineCountDown != null
              && Lizzie.engineManager.firstEngineCountDown.isPlayBlack)
            Lizzie.engineManager.firstEngineCountDown.sendTimeLeft(false);
          else if (Lizzie.engineManager.secondEngineCountDown != null
              && Lizzie.engineManager.secondEngineCountDown.isPlayBlack)
            Lizzie.engineManager.secondEngineCountDown.sendTimeLeft(false);
        }
      }
      boolean needGenmove = false;
      if (forManual && !Lizzie.frame.isPlayingAgainstLeelaz && !Lizzie.leelaz.isInputCommand) {
        LizzieFrame.toolbar.isPkStop = true;
        String move = convertCoordinatesToName(x, y);
        if (getHistory().isBlacksTurn()) {
          Lizzie.leelaz =
              Lizzie.engineManager.engineList.get(EngineManager.engineGameInfo.whiteEngineIndex);
          Lizzie.engineManager
              .engineList
              .get(EngineManager.engineGameInfo.blackEngineIndex)
              .playMoveNoPonder(color, move);
          if (Lizzie.config.enginePkPonder) {
            Lizzie.engineManager
                .engineList
                .get(EngineManager.engineGameInfo.blackEngineIndex)
                .ponder(true, color.isWhite());
          }
        } else {
          Lizzie.leelaz =
              Lizzie.engineManager.engineList.get(EngineManager.engineGameInfo.blackEngineIndex);
          Lizzie.engineManager
              .engineList
              .get(EngineManager.engineGameInfo.whiteEngineIndex)
              .playMoveNoPonder(color, move);
          if (Lizzie.config.enginePkPonder) {
            Lizzie.engineManager
                .engineList
                .get(EngineManager.engineGameInfo.whiteEngineIndex)
                .ponder(true, color.isWhite());
          }
        }
        Lizzie.leelaz.playMovePonder(color.isBlack() ? "B" : "W", move);
        LizzieFrame.toolbar.isPkStop = false;
      } else if (Lizzie.frame.isPlayingAgainstLeelaz
          && Lizzie.frame.playerIsBlack == getData().blackToPlay) {
        if (Lizzie.engineManager.playingAgainstHumanEngineCountDown != null)
          Lizzie.engineManager.playingAgainstHumanEngineCountDown.sendTimeLeft(false);
        Lizzie.leelaz.playMove(color, convertCoordinatesToName(x, y), true, color.isWhite());
        needGenmove = true;
      } else if (!Lizzie.frame.isPlayingAgainstLeelaz
          && !Lizzie.leelaz.isInputCommand
          && !EngineManager.isEngineGame) {
        Lizzie.leelaz.playMove(color, convertCoordinatesToName(x, y), true, color.isWhite());
      }
      if (!forSync
          && Lizzie.frame.bothSync
          && Lizzie.frame.readBoard != null
          && Lizzie.frame.readBoard.process != null
          && Lizzie.frame.readBoard.process.isAlive()) {
        Lizzie.frame.readBoard.sendCommand("place " + x + " " + y);
      }
      history.addOrGoto(newState, newBranch);
      updateIsBest();
      if (needGenmove) Lizzie.leelaz.genmove((color.isWhite() ? "B" : "W"));
      //   modifyEnd(false);
      if (Lizzie.config.playSound) Utils.playVoiceFile();
      if (!forSync) Lizzie.frame.refresh();
    }
  }

  public int getCurrentMovenumber() {
    return history.getCurrentHistoryNode().getData().moveNumber;
  }

  public int getMovenumberInBranch(int index) {
    return history.getCurrentHistoryNode().getData().moveNumberList[index];
  }

  /**
   * overloaded method for place(), chooses color in an alternating pattern
   *
   * @param x x coordinate
   * @param y y coordinate
   */
  public void place(int x, int y) {
    place(x, y, history.isBlacksTurn() ? Stone.BLACK : Stone.WHITE);
  }

  /**
   * overloaded method for place. To be used by the LeelaZ engine. Color is then assumed to be
   * alternating
   *
   * @param namedCoordinate the coordinate to place a stone,
   */
  public void place(String namedCoordinate) {
    Optional<int[]> coords = asCoordinates(namedCoordinate);
    if (coords.isPresent()) {
      place(coords.get()[0], coords.get()[1]);
    } else {
      pass(history.isBlacksTurn() ? Stone.BLACK : Stone.WHITE);
    }
  }

  public boolean maybePlace(String namedCoordinate) {
    Optional<int[]> coords = asCoordinates(namedCoordinate);
    if (coords.isPresent()) {
      place(coords.get()[0], coords.get()[1]);
      return true;
    } else {
      return false;
    }
  }
  /** for handicap */
  public void flatten() {
    Stone[] stones = history.getStones();
    boolean blackToPlay = history.isBlacksTurn();
    Zobrist zobrist = history.getZobrist().clone();
    BoardHistoryList oldHistory = history;
    history =
        new BoardHistoryList(
            new BoardData(
                stones,
                Optional.empty(),
                Stone.EMPTY,
                blackToPlay,
                zobrist,
                0,
                new int[boardWidth * boardHeight],
                0,
                0,
                0.0,
                0));
    history.setGameInfo(oldHistory.getGameInfo());
  }

  /**
   * Removes a chain if it has no liberties
   *
   * @param x x coordinate -- needn't be valid
   * @param y y coordinate -- needn't be valid
   * @param color the color of the chain to remove
   * @param stones the stones array to modify
   * @param zobrist the zobrist object to modify
   * @return number of removed stones
   */
  public static int removeDeadChain(int x, int y, Stone color, Stone[] stones, Zobrist zobrist) {
    if (!isValid(x, y) || stones[getIndex(x, y)] != color) return 0;

    boolean hasLiberties = hasLibertiesHelper(x, y, color, stones);

    // either remove stones or reset what hasLibertiesHelper does to the board
    return cleanupHasLibertiesHelper(x, y, color.recursed(), stones, zobrist, !hasLiberties);
  }

  public static void removeDeadChainForBranch(int x, int y, Stone color, Stone[] stones) {
    if (!isValid(x, y) || stones[getIndex(x, y)] != color) return;

    boolean hasLiberties = hasLibertiesHelperForBracnh(x, y, color, stones);

    // either remove stones or reset what hasLibertiesHelper does to the board
    cleanupHasLibertiesHelperForBranch(x, y, color.recursed(), stones, !hasLiberties);
  }

  /**
   * Recursively determines if a chain has liberties. Alters the state of stones, so it must be
   * counteracted
   *
   * @param x x coordinate -- needn't be valid
   * @param y y coordinate -- needn't be valid
   * @param color the color of the chain to be investigated
   * @param stones the stones array to modify
   * @return whether or not this chain has liberties
   */
  private static boolean hasLibertiesHelper(int x, int y, Stone color, Stone[] stones) {
    if (!isValid(x, y)) return false;

    if (stones[getIndex(x, y)] == Stone.EMPTY) return true; // a liberty was found
    else if (stones[getIndex(x, y)] != color)
      return false; // we are either neighboring an enemy stone, or one we've already recursed on

    // set this index to be the recursed color to keep track of where we've already
    // searched
    stones[getIndex(x, y)] = color.recursed();

    // set removeDeadChain to true if any recursive calls return true. Recurse in
    // all 4 directions
    boolean hasLiberties =
        hasLibertiesHelper(x + 1, y, color, stones)
            || hasLibertiesHelper(x, y + 1, color, stones)
            || hasLibertiesHelper(x - 1, y, color, stones)
            || hasLibertiesHelper(x, y - 1, color, stones);

    return hasLiberties;
  }

  private static boolean hasLibertiesHelperForBracnh(int x, int y, Stone color, Stone[] stones) {
    if (!isValid(x, y)) return false;

    if (stones[getIndex(x, y)].isEmpty()) return true; // a liberty was found
    else if (stones[getIndex(x, y)] != color)
      return false; // we are either neighboring an enemy stone, or one we've already recursed on

    // set this index to be the recursed color to keep track of where we've already
    // searched
    stones[getIndex(x, y)] = color.recursed();

    // set removeDeadChain to true if any recursive calls return true. Recurse in
    // all 4 directions
    boolean hasLiberties =
        hasLibertiesHelperForBracnh(x + 1, y, color, stones)
            || hasLibertiesHelperForBracnh(x, y + 1, color, stones)
            || hasLibertiesHelperForBracnh(x - 1, y, color, stones)
            || hasLibertiesHelperForBracnh(x, y - 1, color, stones);

    return hasLiberties;
  }

  /**
   * cleans up what hasLibertyHelper does to the board state
   *
   * @param x x coordinate -- needn't be valid
   * @param y y coordinate -- needn't be valid
   * @param color color to clean up. Must be a recursed stone type
   * @param stones the stones array to modify
   * @param zobrist the zobrist object to modify
   * @param removeStones if true, we will remove all these stones. otherwise, we will set them to
   *     their unrecursed version
   * @return number of removed stones
   */
  private static int cleanupHasLibertiesHelper(
      int x, int y, Stone color, Stone[] stones, Zobrist zobrist, boolean removeStones) {
    int removed = 0;
    if (!isValid(x, y) || stones[getIndex(x, y)] != color) return 0;

    stones[getIndex(x, y)] = removeStones ? Stone.EMPTY : color.unrecursed();
    if (removeStones) {
      zobrist.toggleStone(x, y, color.unrecursed());
      removed = 1;
    }

    // use the flood fill algorithm to replace all adjacent recursed stones
    removed += cleanupHasLibertiesHelper(x + 1, y, color, stones, zobrist, removeStones);
    removed += cleanupHasLibertiesHelper(x, y + 1, color, stones, zobrist, removeStones);
    removed += cleanupHasLibertiesHelper(x - 1, y, color, stones, zobrist, removeStones);
    removed += cleanupHasLibertiesHelper(x, y - 1, color, stones, zobrist, removeStones);
    return removed;
  }

  private static void cleanupHasLibertiesHelperForBranch(
      int x, int y, Stone color, Stone[] stones, boolean removeStones) {
    //   int removed = 0;
    if (!isValid(x, y) || stones[getIndex(x, y)] != color) return;

    stones[getIndex(x, y)] =
        removeStones
            ? color == Stone.BLACK_RECURSED ? Stone.BLACK_CAPTURED : Stone.WHITE_CAPTURED
            : color.unrecursed();

    // use the flood fill algorithm to replace all adjacent recursed stones
    cleanupHasLibertiesHelperForBranch(x + 1, y, color, stones, removeStones);
    cleanupHasLibertiesHelperForBranch(x, y + 1, color, stones, removeStones);
    cleanupHasLibertiesHelperForBranch(x - 1, y, color, stones, removeStones);
    cleanupHasLibertiesHelperForBranch(x, y - 1, color, stones, removeStones);
  }

  /**
   * Get current board state
   *
   * @return the stones array corresponding to the current board state
   */
  public Stone[] getStones() {
    return history.getStones();
  }

  /**
   * Shows where to mark the last coordinate
   *
   * @return the last played stone, if any, Optional.empty otherwise
   */
  public Optional<int[]> getLastMove() {
    return history.getLastMove();
  }

  /**
   * Gets the move played in this position
   *
   * @return the next move, if any, Optional.empty otherwise
   */
  public Optional<int[]> getNextMove() {
    return history.getNextMove();
  }

  public int moveNumberByCoord(int[] coord) {
    int moveNumber = 0;
    if (Board.isValid(coord)) {
      int index = Board.getIndex(coord[0], coord[1]);
      if (Lizzie.board.getHistory().getStones()[index] != Stone.EMPTY) {
        BoardHistoryNode cur = Lizzie.board.getHistory().getCurrentHistoryNode();
        moveNumber = cur.getData().moveNumberList[index];
        if (!cur.isMainTrunk()) {
          if (moveNumber > 0) {
            moveNumber = cur.getData().moveNumber - cur.getData().moveMNNumber + moveNumber;
          } else {
            BoardHistoryNode p = cur.firstParentWithVariations().orElse(cur);
            while (p != cur && moveNumber == 0) {
              moveNumber = p.getData().moveNumberList[index];
              if (moveNumber > 0) {
                BoardHistoryNode topOfTop = p.firstParentWithVariations().orElse(p);
                if (topOfTop != p) {
                  moveNumber = p.getData().moveNumber - p.getData().moveMNNumber + moveNumber;
                }
              } else {
                cur = p;
                p = cur.firstParentWithVariations().orElse(cur);
              }
            }
          }
        } else if (cur.getData().moveMNNumber > 0)
          return moveNumber + cur.getData().moveNumber - cur.getData().moveMNNumber;
      }
    }
    return moveNumber;
  }

  public int moveNumberByXY(int x, int y) {
    int moveNumber = -1;
    int coord[] = {x, y};
    if (Board.isValid(coord)) {
      int index = Board.getIndex(coord[0], coord[1]);
      if (Lizzie.board.getHistory().getStones()[index] != Stone.EMPTY) {
        BoardHistoryNode cur = Lizzie.board.getHistory().getCurrentHistoryNode();
        moveNumber = cur.getData().moveNumberList[index];
        if (!cur.isMainTrunk()) {
          if (moveNumber > 0) {
            moveNumber = cur.getData().moveNumber - cur.getData().moveMNNumber + moveNumber;
          } else {
            BoardHistoryNode p = cur.firstParentWithVariations().orElse(cur);
            while (p != cur && moveNumber == 0) {
              moveNumber = p.getData().moveNumberList[index];
              if (moveNumber > 0) {
                BoardHistoryNode topOfTop = p.firstParentWithVariations().orElse(p);
                if (topOfTop != p) {
                  moveNumber = p.getData().moveNumber - p.getData().moveMNNumber + moveNumber;
                }
              } else {
                cur = p;
                p = cur.firstParentWithVariations().orElse(cur);
              }
            }
          }
        } else if (cur.getData().moveMNNumber > 0)
          return moveNumber + cur.getData().moveNumber - cur.getData().moveMNNumber;
      }
    }
    return moveNumber;
  }

  /**
   * Gets current board move number
   *
   * @return the int array corresponding to the current board move number
   */
  public int[] getMoveNumberList() {
    return history.getMoveNumberList();
  }

  private Thread ShowCandidateSchedule;

  public void clearAfterMove() {
    Lizzie.leelaz.clearPonderLimit();
    if (!Lizzie.leelaz.isPondering()) Lizzie.frame.clearKataEstimate();
    if (Lizzie.frame.priorityMoveCoords.size() > 0) Lizzie.frame.priorityMoveCoords.clear();
    if (isLoadingFile) return;
    Lizzie.frame.clickbadmove = LizzieFrame.outOfBoundCoordinate;
    if (Lizzie.config.showMouseOverWinrateGraph
        && Lizzie.config.showWinrateGraph
        && LizzieFrame.winrateGraph.mouseOverNode != null) {
      LizzieFrame.winrateGraph.clearMouseOverNode();
    }
    if (Lizzie.frame.clickOrder != -1) {
      Lizzie.frame.clickOrder = -1;
      // Lizzie.frame.boardRenderer.startNormalBoard();
      Lizzie.frame.suggestionclick = LizzieFrame.outOfBoundCoordinate;
      Lizzie.frame.mouseOverCoordinate = LizzieFrame.outOfBoundCoordinate;
      // Lizzie.frame.boardRenderer.clearBranch();

      Lizzie.frame.selectedorder = -1;
      Lizzie.frame.currentRow = -1;
    }
    if (LizzieFrame.toolbar.chkAutoSub.isSelected()) {
      LizzieFrame.toolbar.displayedSubBoardBranchLength = 1;
      LizzieFrame.subBoardRenderer.setDisplayedBranchLength(1);
      LizzieFrame.subBoardRenderer.wheeled = false;
    } else {
      LizzieFrame.subBoardRenderer.clearAfterMove();
    }

    //  Lizzie.frame.subBoardRenderer.bestmovesNum = 0;
    LizzieFrame.subBoardRenderer.clearAfterMove();
    if (Lizzie.config.isFourSubMode()) {
      Lizzie.frame.subBoardRenderer2.clearAfterMove();
      Lizzie.frame.subBoardRenderer3.clearAfterMove();
      Lizzie.frame.subBoardRenderer4.clearAfterMove();
    }
    LizzieFrame.boardRenderer.removedrawmovestone();
    if (Lizzie.config.isDoubleEngineMode()) {
      LizzieFrame.boardRenderer2.removedrawmovestone();
    }
    Lizzie.frame.suggestionclick = LizzieFrame.outOfBoundCoordinate;
    if (Lizzie.frame.analysisFrame != null && Lizzie.frame.analysisFrame.isVisible()) {
      Lizzie.frame.analysisFrame.selectedorder = -1;
      Lizzie.frame.analysisFrame.clickOrder = -1;
    }
    if (Lizzie.frame.analysisFrame2 != null && Lizzie.frame.analysisFrame2.isVisible()) {
      Lizzie.frame.analysisFrame2.selectedorder = -1;
      Lizzie.frame.analysisFrame2.clickOrder = -1;
    }
    if (Lizzie.frame.isCounting) {
      Lizzie.frame.clearKataEstimate();
      Lizzie.frame.estimateResults.btnEstimate.setText(
          Lizzie.resourceBundle.getString("EstimateResults.estimate"));
      Lizzie.frame.estimateResults.iscounted = false;
      Lizzie.frame.isCounting = false;
    }
    // Lizzie.frame.isShowingHeatmap = false;
    if (Lizzie.frame.independentMainBoard != null) {
      Lizzie.frame.independentMainBoard.mouseOverCoordinate = LizzieFrame.outOfBoundCoordinate;
      // Lizzie.frame.independentMainBoard.boardRenderer.startNormalBoard();
      // Lizzie.frame.independentMainBoard.boardRenderer.clearBranch();
      Lizzie.frame.independentMainBoard.boardRenderer.clearAfterMove();
      Lizzie.frame.independentMainBoard.boardRenderer.removedrawmovestone();
    }
    if (Lizzie.frame.floatBoard != null) {
      Lizzie.frame.floatBoard.mouseOverCoordinate = LizzieFrame.outOfBoundCoordinate;
      // Lizzie.frame.floatBoard.boardRenderer.startNormalBoard();
      // Lizzie.frame.floatBoard.boardRenderer.clearBranch();
      Lizzie.frame.floatBoard.boardRenderer.clearSuggestionImage();
      Lizzie.frame.floatBoard.boardRenderer.removedrawmovestone();
    }
    if (Lizzie.frame.independentSubBoard != null) {
      Lizzie.frame.independentSubBoard.subBoardRenderer.clearAfterMove();

      if (LizzieFrame.toolbar.chkAutoSub.isSelected()) {
        Lizzie.frame.independentSubBoard.subBoardRenderer.setDisplayedBranchLength(1);
        Lizzie.frame.independentSubBoard.subBoardRenderer.wheeled = false;
      } else {
        Lizzie.frame.independentSubBoard.subBoardRenderer.clearAfterMove();
      }
    }

    LizzieFrame.boardRenderer.clearAfterMove();
    if (Lizzie.config.isDoubleEngineMode()) {
      LizzieFrame.boardRenderer2.clearAfterMove();
    }
    handleCandidatesDelay();
    Lizzie.frame.doCommentAfterMove();
  }

  public void handleCandidatesDelay() {
    // TODO Auto-generated method stub
    if (ShowCandidateSchedule != null) ShowCandidateSchedule.interrupt();
    if (Lizzie.config.delayShowCandidates) {
      Lizzie.frame.hideCandidates();
      if (Lizzie.config.delayCandidatesSeconds > 0) {
        Runnable runnable =
            new Runnable() {
              public void run() {
                BoardHistoryNode node = Lizzie.board.getHistory().getCurrentHistoryNode();
                try {
                  Thread.sleep((int) (Lizzie.config.delayCandidatesSeconds * 1000));
                  if (node == Lizzie.board.getHistory().getCurrentHistoryNode())
                    Lizzie.frame.showCandidates();
                } catch (InterruptedException e) {
                  return;
                }
              }
            };
        ShowCandidateSchedule = new Thread(runnable);
        ShowCandidateSchedule.start();
      }
    }
  }

  /** Goes to the next coordinate, thread safe */
  public boolean nextMove(boolean needRefresh) {
    // canGetBestMoves = false;
    synchronized (this) {
      modifyStart();
      updateWinrate();
      Optional<BoardData> data = history.getNext();
      if (data.isPresent()) {
        if (Lizzie.config.playSound) Utils.playVoiceFile();
        // update leelaz board position, before updating to next node
        Optional<int[]> lastMoveOpt = data.get().lastMove;
        if (lastMoveOpt.isPresent()) {
          int[] lastMove = lastMoveOpt.get();
          String name = convertCoordinatesToName(lastMove[0], lastMove[1]);
          Lizzie.leelaz.playMove(data.get().lastMoveColor, name, true, data.get().blackToPlay);
        } else {
          Lizzie.leelaz.playMove(data.get().lastMoveColor, "pass", true, data.get().blackToPlay);
        }
        history.next();
        updateIsBest();
        if (needRefresh) {
          clearAfterMove();
          Lizzie.frame.refresh();
        }
        return true;
      } // else modifyEnd();
      // canGetBestMoves = true;
      modifyEnd();
      return false;
    }
  }

  /**
   * Goes to the next coordinate, thread safe
   *
   * @param fromBackChildren by back children branch
   * @return true when has next variation
   */
  public boolean nextMove(int fromBackChildren) {
    synchronized (this) {
      return nextVariation(fromBackChildren);
    }
  }

  /** Save the move number for restore If in the branch, save the back routing from children */
  public void saveMoveNumber() {
    BoardHistoryNode currentNode = history.getCurrentHistoryNode();
    int curMoveNum = currentNode.getData().moveNumber;
    if (curMoveNum > 0) {
      if (!currentNode.isMainTrunk()) {
        // If in branch, save the back routing from children
        saveBackRouting(currentNode);
      }
      goToMoveNumber(0);
    }
    Optional.of(currentNode);
  }

  /** Save the back routing from children */
  public void saveBackRouting(BoardHistoryNode node) {
    Optional<BoardHistoryNode> prev = node.previous();
    prev.ifPresent(
        n -> {
          n.setFromBackChildren(n.getVariations().indexOf(node));
          saveBackRouting(n);
        });
  }

  public void restoreMoveNumber(ArrayList<Movelist> mv, boolean isEngineGame, Leelaz engine) {
    int lenth = mv.size();
    for (int i = 0; i < lenth; i++) {
      Movelist move = mv.get(lenth - 1 - i);
      String color = move.isblack ? "B" : "W";
      if (move.ispass) {
        if (i > 0) engine.sendCommand("play " + color + " pass");
        else if (getHistory().getStart().next().isPresent()
            && !getHistory().getStart().next().get().getData().lastMove.isPresent())
          engine.sendCommand("play " + color + " pass");
      } else {
        engine.sendCommand("play " + color + " " + convertCoordinatesToName(move.x, move.y));
      }
    }
    Lizzie.initializeAfterVersionCheck(isEngineGame, engine);
  }

  /** Go to move number by back routing from children when in branch */
  public void goToMoveNumberByBackChildren(int moveNumber) {
    int delta = moveNumber - history.getMoveNumber();
    for (int i = 0; i < Math.abs(delta); i++) {
      BoardHistoryNode currentNode = history.getCurrentHistoryNode();
      if (currentNode.hasVariations() && delta > 0) {
        nextMove(currentNode.getFromBackChildren());
      } else {
        if (!(delta > 0 ? nextMove(false) : previousMove(false))) {
          break;
        }
      }
    }
    clearAfterMove();
    Lizzie.frame.refresh();
  }

  public boolean goToMoveNumber(int moveNumber) {
    return goToMoveNumberHelper(moveNumber, false);
  }

  public boolean goToMoveNumberWithinBranch(int moveNumber) {
    return goToMoveNumberHelper(moveNumber, true);
  }

  public boolean goToMoveNumberBeyondBranch(int moveNumber) {
    // Go to main trunk if current branch is shorter than moveNumber.
    if (moveNumber > history.currentBranchLength() && moveNumber <= history.mainTrunkLength()) {
      goToMoveNumber(0);
    }
    return goToMoveNumber(moveNumber);
  }

  public boolean goToMoveNumberHelper(int moveNumber, boolean withinBranch) {
    if (EngineManager.isEngineGame) return false;
    if (Lizzie.config.noRefreshOnMouseMove) {
      LizzieFrame.boardRenderer.clearBranch();
      if (Lizzie.config.isDoubleEngineMode()) LizzieFrame.boardRenderer2.clearBranch();
    }
    int delta = moveNumber - history.getMoveNumber();
    boolean moved = false;
    for (int i = 0; i < Math.abs(delta); i++) {
      if (withinBranch && delta < 0) {
        BoardHistoryNode currentNode = history.getCurrentHistoryNode();
        if (!currentNode.isFirstChild()) {
          break;
        }
      }
      if (!(delta > 0 ? nextMove(false) : previousMove(false))) {
        break;
      }
      if (!moved) {
        moved = true;
      }
    }
    if (moved) {
      clearAfterMove();
      Lizzie.frame.refresh();
    }
    return moved;
  }

  /** Goes to the next variation, thread safe */
  public boolean nextVariation(int idx) {
    synchronized (this) {
      modifyStart();
      updateWinrate();
      // Don't update winrate here as this is usually called when jumping between
      // variations
      if (history.nextVariation(idx).isPresent()) {
        // Update leelaz board position, before updating to next node
        updateIsBest();
        Optional<int[]> lastMoveOpt = history.getData().lastMove;
        // history.getCurrentHistoryNode().placeExtraStones();
        if (lastMoveOpt.isPresent()) {
          int[] lastMove = lastMoveOpt.get();
          String name = convertCoordinatesToName(lastMove[0], lastMove[1]);
          Lizzie.leelaz.playMove(history.getLastMoveColor(), name);
        } else {
          Lizzie.leelaz.playMove(history.getLastMoveColor(), "pass");
        }
        modifyEnd();
        Lizzie.frame.refresh();
        // Lizzie.board.modifyEnd(false);
        return true;
      }
      modifyEnd();
      return false;
    }
  }

  /**
   * Returns all the nodes at the given depth in the history tree, always including a node from the
   * main variation (possibly less deep that the given depth).
   *
   * @return the list of candidate nodes
   */
  private List<BoardHistoryNode> branchCandidates(BoardHistoryNode node) {
    int targetDepth = node.getData().moveNumber;
    Stream<BoardHistoryNode> nodes = singletonList(history.root()).stream();
    for (int i = 0; i < targetDepth; i++) {
      nodes = nodes.flatMap(n -> n.getVariations().stream());
    }
    LinkedList<BoardHistoryNode> result = nodes.collect(Collectors.toCollection(LinkedList::new));

    if (result.isEmpty() || !result.get(0).isMainTrunk()) {
      BoardHistoryNode endOfMainTrunk = history.root();
      while (endOfMainTrunk.next().isPresent()) {
        endOfMainTrunk = endOfMainTrunk.next().get();
      }
      result.addFirst(endOfMainTrunk);
      return result;
    } else {
      return result;
    }
  }

  /**
   * Moves to next variation (variation to the right) if possible. The variation must have a move
   * with the same move number as the current move in it.
   *
   * @return true if there exist a target variation
   */
  public boolean nextBranch() {
    synchronized (this) {
      BoardHistoryNode currentNode = history.getCurrentHistoryNode();
      Optional<BoardHistoryNode> targetNode = Optional.empty();
      boolean foundIt = false;
      for (BoardHistoryNode candidate : branchCandidates(currentNode)) {
        if (foundIt) {
          targetNode = Optional.of(candidate);
          break;
        } else if (candidate == currentNode) {
          foundIt = true;
        }
      }
      if (targetNode.isPresent()) {
        moveToAnyPosition(targetNode.get());
      }
      return targetNode.isPresent();
    }
  }

  /**
   * Moves to previous variation (variation to the left) if possible, or back to main trunk To move
   * to another variation, the variation must have the same number of moves in it.
   *
   * <p>Note: This method will always move back to main trunk, even if variation has more moves than
   * main trunk (if this case it will move to the last move in the trunk).
   *
   * @return true if there exist a target variation
   */
  public boolean previousBranch() {
    synchronized (this) {
      BoardHistoryNode currentNode = history.getCurrentHistoryNode();
      Optional<BoardHistoryNode> targetNode = Optional.empty();
      for (BoardHistoryNode candidate : branchCandidates(currentNode)) {
        if (candidate == currentNode) {
          break;
        } else {
          targetNode = Optional.of(candidate);
        }
      }
      if (targetNode.isPresent()) {
        moveToAnyPosition(targetNode.get());
      }
      return targetNode.isPresent();
    }
  }

  /**
   * Jump anywhere in the board history tree.
   *
   * @param targetNode history node to be located
   * @return void
   */
  public void moveToAnyPosition(BoardHistoryNode targetNode) {
    if (EngineManager.isEngineGame) return;
    List<Integer> targetParents = new ArrayList<Integer>();
    List<Integer> sourceParents = new ArrayList<Integer>();

    BiConsumer<BoardHistoryNode, List<Integer>> populateParent =
        (node, parentList) -> {
          Optional<BoardHistoryNode> prevNode = node.previous();
          while (prevNode.isPresent()) {
            BoardHistoryNode p = prevNode.get();
            for (int m = 0; m < p.numberOfChildren(); m++) {
              if (p.getVariation(m).get() == node) {
                parentList.add(m);
              }
            }
            node = p;
            prevNode = p.previous();
          }
        };

    // Compute the path from the current node to the root
    populateParent.accept(history.getCurrentHistoryNode(), sourceParents);

    // Compute the path from the target node to the root
    populateParent.accept(targetNode, targetParents);

    // Compute the distance from source to the deepest common answer
    int targetDepth = targetParents.size();
    int sourceDepth = sourceParents.size();
    int maxDepth = min(targetParents.size(), sourceParents.size());
    int depth;
    for (depth = 0; depth < maxDepth; depth++) {
      int sourceParent = sourceParents.get(sourceDepth - depth - 1);
      int targetParent = targetParents.get(targetDepth - depth - 1);
      if (sourceParent != targetParent) {
        break;
      }
    }

    // Move all the way up to the deepest common ansestor
    for (int m = 0; m < sourceDepth - depth; m++) {
      previousMove(false);
    }

    // Then all the way down to the target
    for (int m = targetDepth - depth; m > 0; m--) {
      nextVariation(targetParents.get(m - 1));
    }
  }

  public void moveBranchUp() {
    synchronized (this) {
      history.getCurrentHistoryNode().topOfBranch().moveUp();
    }
  }

  public void moveBranchDown() {
    synchronized (this) {
      history.getCurrentHistoryNode().topOfBranch().moveDown();
    }
  }

  public void deleteMove() {
    synchronized (this) {
      BoardHistoryNode currentNode = history.getCurrentHistoryNode();
      if (currentNode.next(true).isPresent()) {
        // Will delete more than one move, ask for confirmation
        int ret =
            JOptionPane.showConfirmDialog(
                Lizzie.frame,
                Lizzie.resourceBundle.getString("LizzieFrame.deleteMoves"),
                Lizzie.resourceBundle.getString("LizzieFrame.delete"),
                JOptionPane.OK_CANCEL_OPTION);
        if (ret != JOptionPane.OK_OPTION) {
          return;
        }
      }
      if (currentNode.previous().isPresent()) {
        BoardHistoryNode pre = currentNode.previous().get();
        previousMove(true);
        int idx = pre.indexOfNode(currentNode);
        pre.deleteChild(idx);
        if (currentNode.isMainTrunk()) {
          for (int i = 0; i < this.movelistwr.size(); i++) {
            if (movelistwr.get(i).movenum == currentNode.getData().moveNumber) {
              for (int j = i; j < this.movelistwr.size(); j++) {
                movelistwr.get(j).isdelete = true;
              }
              break;
            }
          }
        }
        Lizzie.board.clearNodeInfo(Lizzie.board.getHistory().getStart());
        Lizzie.board.setMovelistAll();
      } else {
        deleteMoveNoHintAfter();
        // clear(false); // Clear the board if we're at the top
        if (Lizzie.leelaz.isPondering()) Lizzie.leelaz.ponder();
      }
    }
    // LizzieFrame.forceRecreate = true;
  }

  public void deleteMoveNoHintAfter() {
    if (!history.getCurrentHistoryNode().next().isPresent()) return;
    synchronized (this) {
      BoardHistoryNode currentNode = history.getCurrentHistoryNode().next().get();
      if (currentNode.previous().isPresent()) {
        BoardHistoryNode pre = currentNode.previous().get();
        // previousMove();
        int idx = pre.indexOfNode(currentNode);
        pre.deleteChild(idx);
        if (currentNode.isMainTrunk()) {
          for (int i = 0; i < this.movelistwr.size(); i++) {
            if (movelistwr.get(i).movenum == currentNode.getData().moveNumber) {
              for (int j = i; j < this.movelistwr.size(); j++) {
                movelistwr.get(j).isdelete = true;
              }
              break;
            }
          }
        }
        // Lizzie.board.clearNodeInfo(Lizzie.board.getHistory().getStart());
        // Lizzie.board.setMovelistAll();
      } else {
        clear(false); // Clear the board if we're at the top
        if (Lizzie.leelaz.isPondering()) Lizzie.leelaz.ponder();
      }
    }
    // LizzieFrame.forceRecreate = true;
  }

  public void deleteMoveNoHint() {
    synchronized (this) {
      BoardHistoryNode currentNode = history.getCurrentHistoryNode();
      if (currentNode.previous().isPresent()) {
        BoardHistoryNode pre = currentNode.previous().get();
        previousMove(true);
        int idx = pre.indexOfNode(currentNode);
        pre.deleteChild(idx);
        if (currentNode.isMainTrunk()) {
          for (int i = 0; i < this.movelistwr.size(); i++) {
            if (movelistwr.get(i).movenum == currentNode.getData().moveNumber) {
              for (int j = i; j < this.movelistwr.size(); j++) {
                movelistwr.get(j).isdelete = true;
              }
              break;
            }
          }
        }
        Lizzie.board.clearNodeInfo(Lizzie.board.getHistory().getStart());
        Lizzie.board.setMovelistAll();
      } else {
        clear(false); // Clear the board if we're at the top
        if (Lizzie.leelaz.isPondering()) Lizzie.leelaz.ponder();
      }
    }
    // LizzieFrame.forceRecreate = true;
  }

  public void deleteBranch() {
    int originalMoveNumber = history.getMoveNumber();
    undoToChildOfPreviousWithVariation();
    int moveNumberBeforeOperation = history.getMoveNumber();
    deleteMove();
    boolean canceled = (history.getMoveNumber() == moveNumberBeforeOperation);
    if (canceled) {
      goToMoveNumber(originalMoveNumber);
    }
    Lizzie.board.clearNodeInfo(Lizzie.board.getHistory().getStart());
    Lizzie.board.setMovelistAll();
  }

  public BoardData getData() {
    return history.getData();
  }

  public BoardHistoryList getHistory() {
    return history;
  }

  public void setHistory(BoardHistoryList newList) {
    history = newList;
  }

  public boolean setAsMainBranch() {
    if (history.getCurrentHistoryNode().isMainTrunk()) {
      Lizzie.board.clearNodeInfo(Lizzie.board.getHistory().getStart());
      setMovelistAll();
      return false;
    }
    BoardHistoryNode topNode = history.getCurrentHistoryNode().topOfFatherBranch();
    BoardHistoryNode mainNode = history.getCurrentHistoryNode().topOfFatherBranch2();
    BoardHistoryNode oldFirstVar = mainNode.variations.get(0);
    for (int i = 0; i < mainNode.variations.size(); i++) {
      if (mainNode.variations.get(i) == topNode) {
        mainNode.variations.remove(i);
        mainNode.variations.add(i, oldFirstVar);
        mainNode.variations.remove(0);
        mainNode.variations.add(0, topNode);
        oldFirstVar.resetMoveNumberList();
        topNode.resetMoveNumberList();
        return true;
      }
    }
    return false;
  }

  public void copyNodeInfoToMain(BoardHistoryNode node) {
    node.nodeInfoMain.analyzed = node.nodeInfo.analyzed;
    node.nodeInfoMain.coords = node.nodeInfo.coords;
    node.nodeInfoMain.moveNum = node.nodeInfo.moveNum;
    node.nodeInfoMain.isBlack = node.nodeInfo.isBlack;
    node.nodeInfoMain.winrate = node.nodeInfo.winrate;
    node.nodeInfoMain.diffWinrate = node.nodeInfo.diffWinrate;
    node.nodeInfoMain.playouts = node.nodeInfo.playouts;
    node.nodeInfoMain.previousPlayouts = node.nodeInfo.previousPlayouts;
    node.nodeInfoMain.scoreMeanDiff = node.nodeInfo.scoreMeanDiff;
    node.nodeInfoMain.scoreLead = node.nodeInfo.scoreLead;
    node.nodeInfoMain.isMatchAi = node.nodeInfo.isMatchAi;
  }

  public void clearBoardStat() {
    isPkBoard = false;
    isPkBoardKataB = false;
    isPkBoardKataW = false;
    isKataBoard = false;
    clearBestMovesAfter(history.getStart());
  }

  public void clearPkBoardStat() {
    isPkBoard = false;
    isPkBoardKataB = false;
    isPkBoardKataW = false;
    isKataBoard = false;
  }

  /** Clears all history and starts over from empty board. */

  //  public void clearforpk() {
  //	    Lizzie.frame.winrateGraph.maxcoreMean = 15;
  //	    hasStartStone = false;
  //	    startStonelist = new ArrayList<Movelist>();
  //	    Lizzie.frame.resetTitle();
  //	    isKataBoard = false;
  //	    movelistwr.clear();
  //	    Lizzie.frame.boardRenderer.removecountblock();
  //	    initializeForPk();
  //	  }
  public void clear(boolean isEngineGame) {
    LizzieFrame.winrateGraph.resetMaxScoreLead();
    if (Lizzie.frame.readBoard != null) {
      Lizzie.frame.readBoard.firstSync = true;
    }
    double komi = history.getGameInfo().getKomi();
    isPkBoardKataB = false;
    isPkBoardKataW = false;
    Lizzie.frame.resetTitle();
    hasStartStone = false;
    startStonelist = new ArrayList<Movelist>();
    movelistwr.clear();
    initialize(isEngineGame);
    isKataBoard = false;
    if (!isEngineGame) {
      cleanedittemp();
      isPkBoard = false;
      Lizzie.leelaz.clear();
      if (Lizzie.frame.readBoard != null
          && Lizzie.frame.readBoard.process != null
          && Lizzie.frame.readBoard.process.isAlive()) {
        Lizzie.board.getHistory().getGameInfo().resetAllNoKomi();
        Lizzie.leelaz.komi(komi);
      } else {
        komi = Lizzie.leelaz.orikomi;
        Lizzie.board.getHistory().getGameInfo().resetAllNoKomi();
        Lizzie.leelaz.komi(komi);
      }
      if (LizzieFrame.urlSgf) {
        if (LizzieFrame.onlineDialog != null) {
          LizzieFrame.onlineDialog.stopSync();
        }
      }
    } else Lizzie.board.getHistory().getGameInfo().setKomi(komi);
    Lizzie.frame.clearKataEstimate();
  }

  public void clearForOnline() {
    if (Lizzie.frame.readBoard != null && Lizzie.frame.syncBoard) {
      Lizzie.frame.readBoard.firstSync = true;
    }
    Lizzie.frame.resetTitle();
    LizzieFrame.winrateGraph.resetMaxScoreLead();
    hasStartStone = false;
    startStonelist = new ArrayList<Movelist>();
    movelistwr.clear();
    cleanedittemp();
    initialize(false);
    isPkBoard = false;
    isPkBoardKataB = false;
    isPkBoardKataW = false;
    isKataBoard = false;
    Lizzie.leelaz.komi = Lizzie.leelaz.orikomi;
    Lizzie.leelaz.clear();
    Lizzie.leelaz.sendCommand("komi " + Lizzie.leelaz.orikomi);
    LizzieFrame.menu.txtKomi.setText(String.valueOf(Lizzie.leelaz.orikomi));
    Lizzie.board.getHistory().getGameInfo().resetAllNoKomi();
    Lizzie.board.getHistory().getGameInfo().setKomi(Lizzie.leelaz.orikomi);
    Lizzie.frame.clearKataEstimate();
  }

  public void clearforedit() {
    initialize(false);
    Lizzie.leelaz.clear();
  }

  /** Goes to the previous coordinate, thread safe */
  public boolean previousMove(boolean needRefresh) {
    synchronized (this) {
      modifyStart();
      boolean isPass = false;
      if (history.getCurrentHistoryNode().next().isPresent())
        updateIsBest(history.getCurrentHistoryNode().next().get());
      if (!history.getLastMove().isPresent()) isPass = true;
      if (history.getPrevious().isPresent()) {
        if (!Lizzie.board.isLoadingFile) {
          boolean nopass = false;
          if (!Lizzie.leelaz.isKatago || Lizzie.leelaz.isSai) {
            if (isPass
                && !history.getLastMove().isPresent()
                && history.getCurrentHistoryNode().previous().isPresent()) nopass = true;
          }
          if (!nopass) Lizzie.leelaz.undo(true, history.getPrevious().get().blackToPlay);
          else modifyEnd();
        }
        history.previous();
        if (needRefresh) {
          clearAfterMove();
          Lizzie.frame.refresh();
        }
        updateMovelistNext(Lizzie.board.getHistory().getCurrentHistoryNode());
        return true;
      }
      modifyEnd();
      return false;
    }
  }

  public boolean undoToChildOfPreviousWithVariation() {
    BoardHistoryNode start = history.getCurrentHistoryNode();
    Optional<BoardHistoryNode> goal = start.findChildOfPreviousWithVariation();
    if (!goal.isPresent() || start == goal.get()) return false;
    boolean moved = false;
    while (history.getCurrentHistoryNode() != goal.get() && previousMove(false)) {
      if (!moved) moved = true;
    }
    if (moved) {
      Lizzie.board.clearAfterMove();
      Lizzie.frame.refresh();
    }
    return true;
  }

  public boolean inAnalysisMode() {
    return analysisMode;
  }
  //
  //  public boolean inScoreMode() {
  //    return scoreMode;
  //  }

  public void autosave() {
    if (autosaveToMemory()) {
      try {
        Lizzie.config.persist();
      } catch (IOException err) {
      }
    }
  }

  public boolean autosaveToMemory() {
    try {
      String sgf = SGFParser.saveToString(false);
      if (sgf.equals(Lizzie.config.persisted.getString("autosave"))) {
        return false;
      }
      Lizzie.config.persisted.put("autosave", sgf);
    } catch (Exception err) { // IOException or JSONException
      return false;
    }
    return true;
  }

  //  public void resumePreviousGame() {
  //    try {
  //
  //      SGFParser.loadFromString(Lizzie.config.persisted.getString("autosave"));
  //      while (nextMove()) ;
  //      Lizzie.board.setMovelistAll();
  //      Lizzie.frame.resetMovelistFrameandAnalysisFrame();
  //      Lizzie.frame.setVisible(true);
  //    } catch (JSONException err) {
  //    }
  //  }

  public boolean isContainsKataData() {
    BoardHistoryNode node = getHistory().getStart();
    while (node.next().isPresent()) {
      if (node.getData().isKataData) return true;
      else node = node.next().get();
    }
    return false;
  }

  public boolean isContainsKataData2() {
    BoardHistoryNode node = getHistory().getStart();
    while (node.next().isPresent()) {
      if (node.getData().isKataData2) return true;
      else node = node.next().get();
    }
    return false;
  }

  //  public double lastWinrateDiff2(BoardHistoryNode node) {
  //    if (Lizzie.board.isPkBoard) {
  //      if (node.previous().isPresent()
  //          && node.previous().get().previous().isPresent()
  //          && !node.previous().get().previous().get().getData().bestMoves.isEmpty()) {
  //        return (node.previous().get().previous().get().getData().bestMoves.get(0).winrate
  //            - Lizzie.board.getData().bestMoves.get(0).winrate);
  //      }
  //    } else {
  //      // Last winrate
  //      Optional<BoardData> lastNode = node.previous().flatMap(n -> Optional.of(n.getData()));
  //      boolean validLastWinrate = lastNode.map(d -> d.getPlayouts() > 0).orElse(false);
  //      while (!validLastWinrate && node.previous().isPresent()) {
  //        node = node.previous().get();
  //        lastNode = node.previous().flatMap(n -> Optional.of(n.getData()));
  //        validLastWinrate = lastNode.map(d -> d.getPlayouts() > 0).orElse(false);
  //      }
  //      if (!node.previous().isPresent()) {
  //        return 0;
  //      }
  //      double lastWR = lastNode.get().bestMoves.get(0).winrate;
  //      if (lastNode.get().blackToPlay == node.getData().blackToPlay) {
  //        return lastWR - Lizzie.board.getData().bestMoves.get(0).winrate;
  //      } else {
  //        return (100 - lastWR) - Lizzie.board.getData().bestMoves.get(0).winrate;
  //      }
  //    }
  //    return 0;
  //  }

  //  public double lastScoreMeanDiff2(BoardHistoryNode node) {
  //    if (Lizzie.board.isPkBoard) {
  //      if (node.previous().isPresent()
  //          && node.previous().get().previous().isPresent()
  //          && !node.previous().get().previous().get().getData().bestMoves.isEmpty()) {
  //        return (node.previous().get().previous().get().getData().bestMoves.get(0).scoreMean
  //            - Lizzie.board.getData().bestMoves.get(0).scoreMean);
  //      }
  //    } else {
  //      // Last winrate
  //      Optional<BoardData> lastNode = node.previous().flatMap(n -> Optional.of(n.getData()));
  //      boolean validLastWinrate = lastNode.map(d -> d.getPlayouts() > 0).orElse(false);
  //      while (!validLastWinrate && node.previous().isPresent()) {
  //        node = node.previous().get();
  //        lastNode = node.previous().flatMap(n -> Optional.of(n.getData()));
  //        validLastWinrate = lastNode.map(d -> d.getPlayouts() > 0).orElse(false);
  //      }
  //      if (!node.previous().isPresent()) {
  //        return 0;
  //      }
  //      double lastWR = lastNode.get().bestMoves.get(0).scoreMean;
  //      if (lastNode.get().blackToPlay == node.getData().blackToPlay) {
  //        return lastWR - Lizzie.board.getData().bestMoves.get(0).scoreMean;
  //      } else {
  //        return (-lastWR) - Lizzie.board.getData().bestMoves.get(0).scoreMean;
  //      }
  //    }
  //    return 0;
  //  }

  public double lastWinrateDiff(BoardHistoryNode node) {
    if (Lizzie.board.isPkBoard) {
      if (node.previous().isPresent()
          && node.previous().get().previous().isPresent()
          && node.previous().get().previous().get().getData().getPlayouts() > 0) {
        return (node.previous().get().previous().get().getData().winrate - node.getData().winrate);
      }
    } else {
      Optional<BoardData> lastNode = node.previous().flatMap(n -> Optional.of(n.getData()));
      boolean validLastWinrate = lastNode.map(d -> d.getPlayouts() > 0).orElse(false);
      while (!validLastWinrate && node.previous().isPresent()) {
        node = node.previous().get();
        lastNode = node.previous().flatMap(n -> Optional.of(n.getData()));
        validLastWinrate = lastNode.map(d -> d.getPlayouts() > 0).orElse(false);
      }
      if (!node.previous().isPresent()) {
        return 0;
      }
      double lastWR = lastNode.get().winrate;
      if (lastNode.get().blackToPlay == node.getData().blackToPlay) {
        return lastWR - node.getData().winrate;
      } else {
        return (100 - lastWR) - node.getData().winrate;
      }
    }
    return 0;
  }

  public double lastWinrateDiff2(BoardHistoryNode node) {
    if (Lizzie.board.isPkBoard) {
      if (node.previous().isPresent()
          && node.previous().get().previous().isPresent()
          && node.previous().get().previous().get().getData().getPlayouts2() > 0) {
        return (node.previous().get().previous().get().getData().winrate2
            - node.getData().winrate2);
      }
    } else {
      Optional<BoardData> lastNode = node.previous().flatMap(n -> Optional.of(n.getData()));
      boolean validLastWinrate = lastNode.map(d -> d.getPlayouts2() > 0).orElse(false);
      while (!validLastWinrate && node.previous().isPresent()) {
        node = node.previous().get();
        lastNode = node.previous().flatMap(n -> Optional.of(n.getData()));
        validLastWinrate = lastNode.map(d -> d.getPlayouts2() > 0).orElse(false);
      }
      if (!node.previous().isPresent()) {
        return 0;
      }
      double lastWR = lastNode.get().winrate2;
      if (lastNode.get().blackToPlay == node.getData().blackToPlay) {
        return lastWR - node.getData().winrate2;
      } else {
        return (100 - lastWR) - node.getData().winrate2;
      }
    }
    return 0;
  }

  public double lastScoreMeanDiff(BoardHistoryNode node) {
    if (Lizzie.board.isPkBoard) {
      if (node.previous().isPresent()
          && node.previous().get().previous().isPresent()
          && node.previous().get().previous().get().getData().getPlayouts() > 0) {
        return (node.previous().get().previous().get().getData().scoreMean
            - node.getData().scoreMean);
      }
    } else {
      Optional<BoardData> lastNode = node.previous().flatMap(n -> Optional.of(n.getData()));
      boolean validLastWinrate = lastNode.map(d -> d.getPlayouts() > 0).orElse(false);
      while (!validLastWinrate && node.previous().isPresent()) {
        node = node.previous().get();
        lastNode = node.previous().flatMap(n -> Optional.of(n.getData()));
        validLastWinrate = lastNode.map(d -> d.getPlayouts() > 0).orElse(false);
      }
      if (!node.previous().isPresent()) {
        return 0;
      }

      {
        double lastWR = lastNode.get().scoreMean;
        if (lastNode.get().blackToPlay == node.getData().blackToPlay) {
          return lastWR - node.getData().scoreMean;
        } else {
          return (-lastWR) - node.getData().scoreMean;
        }
      }
    }
    return 0;
  }

  public double lastScoreMeanDiff2(BoardHistoryNode node) {
    if (Lizzie.board.isPkBoard) {
      if (node.previous().isPresent()
          && node.previous().get().previous().isPresent()
          && node.previous().get().previous().get().getData().getPlayouts2() > 0) {
        return (node.previous().get().previous().get().getData().scoreMean2
            - node.getData().scoreMean2);
      }
    } else {
      Optional<BoardData> lastNode = node.previous().flatMap(n -> Optional.of(n.getData()));
      boolean validLastWinrate = lastNode.map(d -> d.getPlayouts2() > 0).orElse(false);
      while (!validLastWinrate && node.previous().isPresent()) {
        node = node.previous().get();
        lastNode = node.previous().flatMap(n -> Optional.of(n.getData()));
        validLastWinrate = lastNode.map(d -> d.getPlayouts2() > 0).orElse(false);
      }
      if (!node.previous().isPresent()) {
        return 0;
      }

      {
        double lastWR = lastNode.get().scoreMean2;
        if (lastNode.get().blackToPlay == node.getData().blackToPlay) {
          return lastWR - node.getData().scoreMean2;
        } else {
          return (-lastWR) - node.getData().scoreMean2;
        }
      }
    }
    return 0;
  }

  public void setMovelistAll() {
    Runnable runnable =
        new Runnable() {
          public void run() {
            BoardHistoryNode node = Lizzie.board.getHistory().getStart();
            updateMovelist(node);
            while (node.next().isPresent()) {
              node = node.next().get();
              updateMovelist(node);
              updateIsBest(node);
            }
          }
        };
    Thread thread = new Thread(runnable);
    thread.start();
  }

  public void setMovelistAll2() {
    Runnable runnable =
        new Runnable() {
          public void run() {
            BoardHistoryNode node = Lizzie.board.getHistory().getStart();
            updateMovelist2(node);
            while (node.next().isPresent()) {
              node = node.next().get();
              updateMovelist2(node);
            }
          }
        };
    Thread thread = new Thread(runnable);
    thread.start();
  }

  public void updateMovelist(BoardHistoryNode node) {
    if (!node.previous().isPresent()) {
      return;
    }
    if (Lizzie.config.isDoubleEngineMode()) {
      updateMovelist2(node);
    }
    BoardHistoryNode previousNode = node.previous().get();
    int movenumer = node.getData().moveNumber;
    int playouts = node.getData().getPlayouts();
    if (((playouts != previousNode.nodeInfo.playouts
                || node.previous().get().getData().getPlayouts()
                    != previousNode.nodeInfo.previousPlayouts)
            && previousNode.getData().winrate >= 0)
        || previousNode.getData().playoutsChanged) {
      double winrateDiff = lastWinrateDiff(node);
      if (Lizzie.board.isPkBoard && playouts > 0) {
        if (node.isMainTrunk() && node.previous().get().isMainTrunk()) {
          if (node.getData().lastMove.isPresent()
              && previousNode.previous().isPresent()
              && previousNode.getData().lastMove.isPresent()) {
            int[] coords = node.getData().lastMove.get();
            boolean isblack = !node.getData().blackToPlay;
            int previousplayouts = 0;
            previousplayouts = previousNode.previous().get().getData().getPlayouts();
            previousNode.nodeInfo.analyzed = previousplayouts > 0;
            node.nodeInfo.diffWinrate = winrateDiff;
            previousNode.nodeInfo.winrate = 100 - node.previous().get().getData().winrate;
            previousNode.nodeInfo.coords = coords;
            previousNode.nodeInfo.isBlack = isblack;
            previousNode.nodeInfo.playouts = playouts;
            previousNode.nodeInfo.moveNum = movenumer;
            previousNode.nodeInfo.previousPlayouts = previousplayouts;
            if (node.getData().isKataData) {
              node.nodeInfo.scoreMeanDiff = lastScoreMeanDiff(node);
              previousNode.nodeInfo.scoreLead = node.getData().scoreMean;
            }
            previousNode.nodeInfoMain = previousNode.nodeInfo;
          }
        }
      } else {
        if (node.getData().lastMove != null && node.getData().lastMove.isPresent()) {
          MatchAiInfo info =
              isMatchAi(node, Lizzie.config.matchAiMoves, Lizzie.config.matchAiPercentsPlayouts);
          double percentsMatch = info.precents;
          boolean isBest = info.isBest;
          boolean isMatchAi = info.isMatch;
          node.getData().lastMoveMatchCandidteNo = info.matchCandidteNo;
          int[] coords = node.getData().lastMove.get();
          boolean isblack = !node.getData().blackToPlay;
          int previousplayouts = 0;

          previousplayouts = previousNode.getData().getPlayouts();
          previousNode.nodeInfo.analyzed = previousplayouts > 0 && playouts > 0;
          previousNode.nodeInfo.analyzedMatchValue = previousplayouts > 0;
          previousNode.nodeInfo.isBest = isBest;
          if (previousNode.nodeInfo.analyzed) {
            previousNode.nodeInfo.diffWinrate = winrateDiff;
            if (node.getData().isKataData) {
              previousNode.nodeInfo.scoreMeanDiff = lastScoreMeanDiff(node);
              previousNode.nodeInfo.scoreLead = node.getData().scoreMean;
            }
            previousNode.nodeInfo.winrate = 100 - node.getData().winrate;
          }
          previousNode.nodeInfo.coords = coords;
          previousNode.nodeInfo.isBlack = isblack;
          previousNode.nodeInfo.playouts = playouts;
          previousNode.nodeInfo.moveNum = movenumer;
          previousNode.nodeInfo.previousPlayouts = previousplayouts;
          previousNode.nodeInfo.isMatchAi = isMatchAi;
          previousNode.nodeInfo.percentsMatch = percentsMatch;
          if (node.isMainTrunk() && node.previous().get().isMainTrunk()) {
            previousNode.nodeInfoMain.analyzed = previousplayouts > 0 && playouts > 0;
            previousNode.nodeInfoMain.analyzedMatchValue = previousplayouts > 0;
            if (previousNode.nodeInfoMain.analyzed) {
              previousNode.nodeInfoMain.diffWinrate = winrateDiff;
              if (node.getData().isKataData) {
                previousNode.nodeInfoMain.scoreMeanDiff = lastScoreMeanDiff(node);
                previousNode.nodeInfoMain.scoreLead = node.getData().scoreMean;
              }
              previousNode.nodeInfoMain.winrate = 100 - node.getData().winrate;
            }
            previousNode.nodeInfoMain.isBest = isBest;
            previousNode.nodeInfoMain.coords = coords;
            previousNode.nodeInfoMain.isBlack = isblack;
            previousNode.nodeInfoMain.playouts = playouts;
            previousNode.nodeInfoMain.moveNum = movenumer;
            previousNode.nodeInfoMain.previousPlayouts = previousplayouts;
            previousNode.nodeInfoMain.isMatchAi = isMatchAi;
            previousNode.nodeInfoMain.percentsMatch = percentsMatch;
          }
        }
      }
      previousNode.getData().playoutsChanged = false;
    }
  }

  public void updateMovelist2(BoardHistoryNode node) {
    if (!node.previous().isPresent()) {
      return;
    }
    BoardHistoryNode previousNode = node.previous().get();
    int movenumer = node.getData().moveNumber;
    int playouts = node.getData().getPlayouts2();
    if ((playouts != previousNode.nodeInfo2.playouts
            || node.previous().get().getData().getPlayouts2()
                != previousNode.nodeInfo2.previousPlayouts)
        && previousNode.getData().winrate2 >= 0) {
      double winrateDiff = lastWinrateDiff2(node);
      if (Lizzie.board.isPkBoard) {
        if (node.getData().lastMove.isPresent()
            && previousNode.previous().isPresent()
            && previousNode.getData().lastMove.isPresent()) {
          int[] coords = node.getData().lastMove.get();
          boolean isblack = !node.getData().blackToPlay;
          int previousplayouts = 0;
          previousplayouts = previousNode.previous().get().getData().getPlayouts2();
          previousNode.nodeInfo2.analyzed = previousplayouts > 0;
          node.nodeInfo2.diffWinrate = winrateDiff;
          previousNode.nodeInfo2.winrate = 100 - node.previous().get().getData().winrate2;
          previousNode.nodeInfo2.coords = coords;
          previousNode.nodeInfo2.isBlack = isblack;
          previousNode.nodeInfo2.playouts = playouts;
          previousNode.nodeInfo2.moveNum = movenumer;
          previousNode.nodeInfo2.previousPlayouts = previousplayouts;
          if (node.getData().isKataData2) {
            previousNode.nodeInfo2.scoreMeanDiff = lastScoreMeanDiff2(node);
            previousNode.nodeInfo2.scoreLead = node.getData().scoreMean2;
          }
        }
      } else {
        if (node.getData().lastMove != null && node.getData().lastMove.isPresent()) {
          MatchAiInfo info =
              isMatchAi2(node, Lizzie.config.matchAiMoves, Lizzie.config.matchAiPercentsPlayouts);
          double percentsMatch = info.precents;
          boolean isBest = info.isBest;
          boolean isMatchAi = info.isMatch;
          int[] coords = node.getData().lastMove.get();
          boolean isblack = !node.getData().blackToPlay;
          int previousplayouts = 0;
          previousplayouts = previousNode.getData().getPlayouts2();
          previousNode.nodeInfo2.analyzed = previousplayouts > 0 && playouts > 0;
          previousNode.nodeInfo2.analyzedMatchValue = previousplayouts > 0;
          if (previousNode.nodeInfo2.analyzed) {
            previousNode.nodeInfo2.diffWinrate = winrateDiff;
            if (node.getData().isKataData2) {
              previousNode.nodeInfo2.scoreMeanDiff = lastScoreMeanDiff2(node);
              previousNode.nodeInfo2.scoreLead = node.getData().scoreMean2;
            }
            previousNode.nodeInfo2.winrate = 100 - node.getData().winrate2;
          }
          previousNode.nodeInfo2.isBest = isBest;
          previousNode.nodeInfo2.coords = coords;
          previousNode.nodeInfo2.isBlack = isblack;
          previousNode.nodeInfo2.playouts = playouts;
          previousNode.nodeInfo2.moveNum = movenumer;
          previousNode.nodeInfo2.previousPlayouts = previousplayouts;
          previousNode.nodeInfo2.isMatchAi = isMatchAi;
          previousNode.nodeInfo2.percentsMatch = percentsMatch;

          if (node.isMainTrunk()) {
            previousNode.nodeInfoMain2.analyzed = previousplayouts > 0 && playouts > 0;
            previousNode.nodeInfoMain2.analyzedMatchValue = previousplayouts > 0;
            if (previousNode.nodeInfoMain2.analyzed) {
              previousNode.nodeInfoMain2.diffWinrate = winrateDiff;
              if (node.getData().isKataData2) {
                previousNode.nodeInfoMain2.scoreMeanDiff = lastScoreMeanDiff2(node);
                previousNode.nodeInfoMain2.scoreLead = node.getData().scoreMean2;
              }
              previousNode.nodeInfoMain2.winrate = 100 - node.getData().winrate2;
            }
            previousNode.nodeInfoMain2.isBest = isBest;
            previousNode.nodeInfoMain2.coords = coords;
            previousNode.nodeInfoMain2.isBlack = isblack;
            previousNode.nodeInfoMain2.playouts = playouts;
            previousNode.nodeInfoMain2.moveNum = movenumer;
            previousNode.nodeInfoMain2.previousPlayouts = previousplayouts;
            previousNode.nodeInfoMain2.isMatchAi = isMatchAi;
            previousNode.nodeInfoMain2.percentsMatch = percentsMatch;
          }
        }
      }
    }
  }

  private void updateMovelistNext(BoardHistoryNode node) {
    if (!(node.next().isPresent() && node.next().get().next().isPresent())) {
      updateMovelist(node);
      return;
    }
    BoardHistoryNode nextnextNode = node.next().get().next().get();
    updateMovelist(nextnextNode);
  }

  class MatchAiInfo {
    boolean isBest;
    double precents;
    int matchCandidteNo;
    boolean isMatch;
  }

  private MatchAiInfo isMatchAi(BoardHistoryNode node, int bestNums, double percentPlayouts) {
    BoardData preNodeData = node.previous().get().getData();
    MatchAiInfo info = new MatchAiInfo();
    boolean hasPut = false;
    if (preNodeData.bestMoves.isEmpty()) {
      info.isMatch = false;
      hasPut = true;
    }
    double maxPlayouts = 0;
    for (MoveData move : preNodeData.bestMoves) {
      if (move.playouts > maxPlayouts) maxPlayouts = move.playouts;
    }

    for (int i = 0; i < preNodeData.bestMoves.size(); i++) {
      if (node.getData().lastMove.isPresent()) {
        int[] lastMoveCoords = node.getData().lastMove.get();

        Optional<int[]> coord = Board.asCoordinates(preNodeData.bestMoves.get(i).coordinate);
        if (coord.isPresent()) {
          int[] c = coord.get();
          if (c[0] == lastMoveCoords[0] && c[1] == lastMoveCoords[1]) {
            if ((preNodeData.bestMoves.get(i).playouts / maxPlayouts) * 100 >= percentPlayouts
                && i < bestNums) {
              if (i == 0) info.isBest = true;
              info.isMatch = true;
              hasPut = true;
            }
            if (i == 0) info.precents = 1;
            else info.precents = preNodeData.bestMoves.get(i).playouts / maxPlayouts;
            info.matchCandidteNo = i + 1;
          }
        }
      }
    }
    if (!hasPut) info.isMatch = false;
    return info;
  }

  private MatchAiInfo isMatchAi2(BoardHistoryNode node, int bestNums, double percentPlayouts) {
    BoardData preNodeData = node.previous().get().getData();
    MatchAiInfo info = new MatchAiInfo();
    boolean hasPut = false;
    if (preNodeData.bestMoves2.isEmpty()) {
      info.isMatch = false;
      hasPut = true;
      // return false;
    }
    double maxPlayouts = 0;
    for (MoveData move : preNodeData.bestMoves2) {
      if (move.playouts > maxPlayouts) maxPlayouts = move.playouts;
    }

    for (int i = 0; i < preNodeData.bestMoves2.size(); i++) {
      if (node.getData().lastMove.isPresent()) {
        int[] lastMoveCoords = node.getData().lastMove.get();

        Optional<int[]> coord = Board.asCoordinates(preNodeData.bestMoves2.get(i).coordinate);
        if (coord.isPresent()) {
          int[] c = coord.get();
          if (c[0] == lastMoveCoords[0] && c[1] == lastMoveCoords[1]) {
            if ((preNodeData.bestMoves2.get(i).playouts / maxPlayouts) * 100 >= percentPlayouts
                && i < bestNums) {
              if (i == 0) info.isBest = true;
              info.isMatch = true;
              hasPut = true;
            }
            if (i == 0) info.precents = 1;
            else info.precents = preNodeData.bestMoves2.get(i).playouts / maxPlayouts;
          }
        }
      }
    }
    if (!hasPut) info.isMatch = false;
    return info;
  }

  public void updateIsBest(BoardHistoryNode node) {
    if (node.previous().isPresent()
        && node.previous().get().getData().getPlayouts() > 0
        && node.getData().lastMove.isPresent()) {
      int[] coords = node.getData().lastMove.get();
      try {
        int[] bestCoords =
            Board.convertNameToCoordinates(
                node.previous().get().getData().bestMoves.get(0).coordinate);
        if (bestCoords[0] == coords[0] && bestCoords[1] == coords[1]) node.isBest = true;
        else node.isBest = false;
      } catch (Exception e) {
        e.printStackTrace();
      }
    } else node.isBest = false;
  }

  public void updateIsBest() {
    BoardHistoryNode node = history.getCurrentHistoryNode();
    updateIsBest(node);
  }

  public void updateWinrate() {
    updateMovelist(history.getCurrentHistoryNode());
    if ((Lizzie.leelaz.isPondering() && !isLoadingFile) || EngineManager.isEngineGame) {
      updateComment();
    }
  }

  public void updateComment() {
    if ((Lizzie.config.appendWinrateToComment && !LizzieFrame.urlSgf) || EngineManager.isEngineGame)
      // Append the winrate to the comment
      SGFParser.appendComment();
  }

  public void setKomi(double komi) {
    getHistory().getGameInfo().setKomi(komi);
    Lizzie.leelaz.komi(komi);
  }

  public boolean iscoordsempty(int x, int y) {
    if (history.getStones()[getIndex(x, y)] != Stone.EMPTY) {
      return false;
    }
    return true;
  }

  public int getMaxMoveNumber() {
    // TODO Auto-generated method stub
    return history.mainTrunkLength();
  }

  public void playBestHeatMove() {
    if (hasBestHeatMove) {
      place(bestHeatMoveX, bestHeatMoveY);
      clearBestHeatMove();
    }
  }

  public void clearBestHeatMove() {
    hasBestHeatMove = false;
    bestHeatMoveX = -1;
    bestHeatMoveY = -1;
  }

  //  public int getAllExtraStones(BoardHistoryNode node) {
  //    int extraStones = node.extraStones == null ? 0 : node.extraStones.size();
  //    while (node.previous().isPresent()) {
  //      node = node.previous().get();
  //      extraStones += node.extraStones == null ? 0 : node.extraStones.size();
  //    }
  //    return extraStones;
  //  }

  public void exchangeBlackWhite() {
    AllMovelist listHead = Lizzie.board.getAllMovelist(6);
    double komi = Lizzie.board.getHistory().getGameInfo().getKomi();
    int startMoveNumber = 0;
    if (hasStartStone) startMoveNumber += startStonelist.size();
    Lizzie.board.clear(false);
    Lizzie.board.playAllMovelist(listHead, startMoveNumber);
    Lizzie.leelaz.komi(komi);
    Lizzie.frame.refresh();
  }

  public void SpinAndMirror(int type) {
    if (Board.boardWidth != Board.boardHeight && type != 3 && type != 4) {
      Message msg = new Message();
      msg.setMessage(
          Lizzie.resourceBundle.getString("SpinAndMirror.noneSquareError")); // "非正方形棋盘不能旋转");
      return;
    }
    AllMovelist listHead = Lizzie.board.getAllMovelist(type);
    double komi = Lizzie.board.getHistory().getGameInfo().getKomi();
    int startMoveNumber = 0;
    if (hasStartStone) startMoveNumber += startStonelist.size();
    Lizzie.board.clear(false);
    Lizzie.board.playAllMovelist(listHead, startMoveNumber);
    Lizzie.leelaz.komi(komi);
    Lizzie.frame.refresh();
  }

  public void gotoAnyMoveByCoords(int[] coords) {
    BoardHistoryNode node = history.getCurrentHistoryNode();
    if (node.getData().lastMove.isPresent()
        && node.getData().lastMove.get()[0] == coords[0]
        && node.getData().lastMove.get()[1] == coords[1]) return;
    while (node.previous().isPresent()) {
      node = node.previous().get();
      if (node.getData().lastMove.isPresent()
          && node.getData().lastMove.get()[0] == coords[0]
          && node.getData().lastMove.get()[1] == coords[1]) {
        moveToAnyPosition(node);
        return;
      }
    }
    node = history.getCurrentHistoryNode();
    while (node.next().isPresent()) {
      node = node.next().get();
      if (node.getData().lastMove.isPresent()
          && node.getData().lastMove.get()[0] == coords[0]
          && node.getData().lastMove.get()[1] == coords[1]) {
        moveToAnyPosition(node);
        return;
      }
    }
    if (node.getData().lastMove.isPresent()
        && node.getData().lastMove.get()[0] == coords[0]
        && node.getData().lastMove.get()[1] == coords[1]) {
      moveToAnyPosition(node);
      return;
    }
  }

  public MoveLinkedList getMoveLinkedListAfter(BoardHistoryNode node) {
    // TODO Auto-generated method stub
    MoveLinkedList head = new MoveLinkedList();
    getMoveLinkedListAfterHelper(node, head);
    if (head.variations.size() > 0) return head.variations.get(0);
    else return null;
  }

  public void getMoveLinkedListAfterHelper(BoardHistoryNode node, MoveLinkedList head) {
    Stack<BoardHistoryNode> stack = new Stack<>();
    stack.push(node);
    while (!stack.isEmpty()) {
      BoardHistoryNode cur = stack.pop();
      if (cur.extraStones != null) {
        for (int i = cur.extraStones.size() - 1; i >= 0; i--) {
          ExtraStones stone = cur.extraStones.get(i);
          int[] lastCoords = {stone.x, stone.y};
          Optional<int[]> lastMove = Optional.of(lastCoords);
          head = addMoveToLinedList(head, lastMove, stone.isBlack, false);
        }
      }
      Optional<int[]> lastMove = cur.getData().lastMove;
      if (lastMove.isPresent() || !cur.getData().dummy)
        head =
            addMoveToLinedList(
                head, lastMove, cur.getData().lastMoveColor.isBlack(), !cur.previous().isPresent());
      if (cur.numberOfChildren() >= 1) {
        for (int i = cur.numberOfChildren() - 1; i >= 0; i--)
          stack.push(cur.getVariations().get(i));
      }
    }
  }

  public void placeLinkedList(
      MoveLinkedList move, BoardHistoryNode node, boolean isFirst, int index) {
    // TODO Auto-generated method stub
    if (node != null) {
      while (getHistory().getCurrentHistoryNode() != node) Lizzie.board.previousMove(false);
    }
    if (!move.needSkip) {
      if (!move.isPass) {
        place(move.x, move.y, move.isBlack ? Stone.BLACK : Stone.WHITE);
      } else {
        pass(move.isBlack ? Stone.BLACK : Stone.WHITE);
      }
    }
    if (isFirst && index >= 0 && !move.needSkip) {
      BoardHistoryNode thisNode = getHistory().getCurrentHistoryNode();
      BoardHistoryNode preivousNode = thisNode.previous().get();
      for (int i = 0; i < preivousNode.numberOfChildren(); i++) {
        if (preivousNode.variations.get(i) == thisNode) {
          preivousNode.variations.remove(i);
          preivousNode.variations.add(index, thisNode);
          break;
        }
      }
    }
    int variationsSize = move.variations.size();
    if (variationsSize > 1) {
      // Variation
      BoardHistoryNode curNode = getHistory().getCurrentHistoryNode();
      for (int i = 0; i < variationsSize; i++) {
        MoveLinkedList sub = move.variations.get(i);
        if (i == 0) placeLinkedList(sub, null, false, 0);
        else placeLinkedList(sub, curNode, false, 0);
      }
    } else if (variationsSize == 1) {
      placeLinkedList(move.variations.get(0), null, false, 0);
    }
  }

  public BoardHistoryNode getBoardHistoryNodeByCoords(int[] coords) {
    // TODO Auto-generated method stub
    BoardHistoryNode node = history.getCurrentHistoryNode();
    if (node.getData().lastMove.isPresent()
        && node.getData().lastMove.get()[0] == coords[0]
        && node.getData().lastMove.get()[1] == coords[1]) return node;
    while (node.previous().isPresent()) {
      node = node.previous().get();
      if (node.getData().lastMove.isPresent()
          && node.getData().lastMove.get()[0] == coords[0]
          && node.getData().lastMove.get()[1] == coords[1]) {
        return node;
      }
    }
    node = history.getCurrentHistoryNode();
    while (node.next().isPresent()) {
      node = node.next().get();
      if (node.getData().lastMove.isPresent()
          && node.getData().lastMove.get()[0] == coords[0]
          && node.getData().lastMove.get()[1] == coords[1]) {
        return node;
      }
    }
    if (node.getData().lastMove.isPresent()
        && node.getData().lastMove.get()[0] == coords[0]
        && node.getData().lastMove.get()[1] == coords[1]) {
      return node;
    }
    return node;
  }

  private MoveLinkedList addMoveToLinedList(
      MoveLinkedList head, Optional<int[]> lastMove, boolean isBlack, boolean needSkip) {
    MoveLinkedList move = new MoveLinkedList();
    if (lastMove.isPresent()) {
      int[] n = lastMove.get();
      move.x = n[0];
      move.y = n[1];
      move.isPass = false;
      move.isBlack = isBlack;
      move.moveNum = head.moveNum + 1;
    } else {
      move.needSkip = needSkip;
      move.isPass = true;
      move.moveNum = head.moveNum + 1;
      move.isBlack = isBlack;
    }
    head.variations.add(move);
    move.previous = Optional.of(head);
    return move;
  }

  public MoveLinkedList getMainMoveLinkedListBetween(
      BoardHistoryNode startNode, BoardHistoryNode endNode) {
    // TODO Auto-generated method stub
    MoveLinkedList head = new MoveLinkedList();
    MoveLinkedList returnHead = head;
    boolean needAddFirstNode = true;
    do {
      if (endNode.extraStones != null) {
        for (ExtraStones stone : endNode.extraStones) {
          int[] lastCoords = {stone.x, stone.y};
          Optional<int[]> lastMove = Optional.of(lastCoords);
          head = addMoveToLinedList(head, lastMove, stone.isBlack, false);
        }
      }
      Optional<int[]> lastMove = endNode.getData().lastMove;
      if (lastMove.isPresent() || !endNode.getData().dummy)
        head = addMoveToLinedList(head, lastMove, endNode.getData().lastMoveColor.isBlack(), false);
      if (startNode == endNode) {
        needAddFirstNode = false;
        break;
      }
      if (endNode.previous().isPresent()) endNode = endNode.previous().get();
    } while (endNode.previous().isPresent());
    if (needAddFirstNode) {
      if (endNode.extraStones != null) {
        for (ExtraStones stone : endNode.extraStones) {
          int[] lastCoords = {stone.x, stone.y};
          Optional<int[]> lastMove = Optional.of(lastCoords);
          head = addMoveToLinedList(head, lastMove, stone.isBlack, false);
        }
      }
      Optional<int[]> lastMove = endNode.getData().lastMove;
      if (lastMove.isPresent() || !endNode.getData().dummy)
        head = addMoveToLinedList(head, lastMove, endNode.getData().lastMoveColor.isBlack(), false);
    }
    if (returnHead.variations.size() > 0) return returnHead.variations.get(0);
    else return null;
  }

  public void placeLinkedListReverse(MoveLinkedList move) {
    // TODO Auto-generated method stub
    while (move.previous.isPresent()) {
      if (!move.needSkip) {
        if (!move.isPass) {
          place(move.x, move.y, move.isBlack ? Stone.BLACK : Stone.WHITE);
        } else {
          pass(move.isBlack ? Stone.BLACK : Stone.WHITE);
        }
      }
      move = move.previous.get();
    }
  }

  public void setMoveListWithFlattenExit(
      ArrayList<Movelist> movelist, int flattenNumber, boolean flattenBlackToPlay) {
    while (previousMove(false)) ;
    int lenth = movelist.size();
    for (int i = 0; i < lenth; i++) {
      Movelist move = movelist.get(lenth - 1 - i);
      if (!move.ispass) {
        place(move.x, move.y, move.isblack ? Stone.BLACK : Stone.WHITE);
      }
      if (i + 1 == flattenNumber) {
        // addStartList();
        //    Lizzie.board.hasStartStone=true;
        if (Lizzie.board.hasStartStone) {
          startStonelist = new ArrayList<Movelist>();
          addStartListAll();
        }
        flatten();
        getHistory().getData().blackToPlay = flattenBlackToPlay;
        return;
      }
    }
  }

  public void editMove(int[] coords, boolean isSwitch, boolean isDelete) {
    GameInfo gameInfo = Lizzie.board.getHistory().getGameInfo();
    boolean oriPlaySound = Lizzie.config.playSound;
    Lizzie.config.playSound = false;
    Lizzie.board.savelistforeditmode();
    int moveNumber = Lizzie.board.moveNumberByCoord(coords);
    if (moveNumber > 0) {
      MoveLinkedList reStoreMainListHead =
          Lizzie.board.getMainMoveLinkedListBetween(
              Lizzie.board.getBoardHistoryNodeByCoords(coords),
              Lizzie.board.getHistory().getCurrentHistoryNode());
      if (reStoreMainListHead != null) {
        while (reStoreMainListHead.variations.size() > 0)
          reStoreMainListHead = reStoreMainListHead.variations.get(0);
        if (isSwitch) {
          reStoreMainListHead.isBlack = !reStoreMainListHead.isBlack;
        } else if (isDelete) {
          reStoreMainListHead.needSkip = true;
        }
      }
      Lizzie.board.gotoAnyMoveByCoords(coords);
      int index =
          Lizzie.board.getHistory().getCurrentHistoryNode().previous().isPresent()
              ? Lizzie.board
                  .getHistory()
                  .getCurrentHistoryNode()
                  .previous()
                  .get()
                  .findIndexOfNode(Lizzie.board.getHistory().getCurrentHistoryNode())
              : -1;
      MoveLinkedList listHead =
          Lizzie.board.getMoveLinkedListAfter(Lizzie.board.getHistory().getCurrentHistoryNode());
      if (listHead == null) {
        Lizzie.board.deleteMove();
        if (isSwitch) {
          Lizzie.board.place(
              coords[0],
              coords[1],
              Lizzie.board.getHistory().isBlacksTurn() ? Stone.WHITE : Stone.BLACK);
        }
      } else {
        Lizzie.board.deleteMoveNoHint();
        if (isSwitch) {
          listHead.isBlack = !listHead.isBlack;
        } else if (isDelete) {
          listHead.needSkip = true;
        }
        Lizzie.board.placeLinkedList(listHead, null, true, index);
        // 返回原点
        Lizzie.board.gotoAnyMoveByCoords(coords);
        if (reStoreMainListHead != null) Lizzie.board.placeLinkedListReverse(reStoreMainListHead);
      }
    } else {
      MoveLinkedList reStoreMainListHead =
          Lizzie.board.getMainMoveLinkedListBetween(
              Lizzie.board.getHistory().getStart(),
              Lizzie.board.getHistory().getCurrentHistoryNode());
      if (reStoreMainListHead != null) {
        while (reStoreMainListHead.variations.size() > 0)
          reStoreMainListHead = reStoreMainListHead.variations.get(0);
        if (reStoreMainListHead.isPass && reStoreMainListHead.previous.isPresent())
          reStoreMainListHead = reStoreMainListHead.previous.get();
      }
      while (Lizzie.board.previousMove(false)) ;
      MoveLinkedList listHead =
          Lizzie.board.getMoveLinkedListAfter(Lizzie.board.getHistory().getCurrentHistoryNode());
      if (listHead == null) {
        int startMoveNumber = 0;
        boolean blackToPlay = Lizzie.board.getHistory().getStart().getData().blackToPlay;
        if (Lizzie.board.hasStartStone) startMoveNumber += Lizzie.board.startStonelist.size();
        if (isSwitch) Lizzie.board.editmovelistswitch(Lizzie.board.tempallmovelist, coords);
        else if (isDelete) Lizzie.board.editmovelistdelete(Lizzie.board.tempallmovelist, coords);
        Lizzie.board.clearforedit();
        Lizzie.board.setMoveListWithFlattenExit(
            Lizzie.board.tempallmovelist, startMoveNumber - (isDelete ? 1 : 0), blackToPlay);
      } else {
        int startMoveNumber = 0;
        boolean blackToPlay = Lizzie.board.getHistory().getStart().getData().blackToPlay;
        if (Lizzie.board.hasStartStone) startMoveNumber += Lizzie.board.startStonelist.size();
        if (isSwitch) Lizzie.board.editmovelistswitch(Lizzie.board.tempallmovelist, coords);
        else if (isDelete) Lizzie.board.editmovelistdelete(Lizzie.board.tempallmovelist, coords);
        Lizzie.board.clearforedit();
        Lizzie.board.setMoveListWithFlattenExit(
            Lizzie.board.tempallmovelist, startMoveNumber - (isDelete ? 1 : 0), blackToPlay);
        listHead.needSkip = true;
        Lizzie.board.placeLinkedList(listHead, null, false, -1);
        // 返回原点
        while (Lizzie.board.previousMove(false)) ;
        if (reStoreMainListHead != null) Lizzie.board.placeLinkedListReverse(reStoreMainListHead);
      }
    }
    Lizzie.config.playSound = oriPlaySound;
    Lizzie.board.getHistory().setGameInfo(gameInfo);
  }

  public String moveListToString(ArrayList<Movelist> moveList) {
    if (moveList == null || moveList.isEmpty()) {
      return "";
    } else {
      String returnString = "";
      for (Movelist move : moveList) {
        if (move.ispass) returnString += "-1,-1," + (move.isblack ? "b" : "w") + "_";
        else returnString += move.x + "," + move.y + "," + (move.isblack ? "b" : "w") + "_";
      }
      return returnString.substring(0, returnString.length() - 1);
    }
  }

  public void playList(String moveList) {
    // TODO Auto-generated method stub
    boolean oriPlaySound = Lizzie.config.playSound;
    Lizzie.config.playSound = false;
    String[] moves = moveList.split("_");
    for (int i = moves.length - 1; i >= 0; i--) {
      String[] move = moves[i].split(",");
      int x = Integer.parseInt(move[0]);
      int y = Integer.parseInt(move[1]);
      if (x >= 0) {
        place(x, y, move[2].equals("b") ? Stone.BLACK : Stone.WHITE);
      } else {
        pass(move[2].equals("b") ? Stone.BLACK : Stone.WHITE);
      }
    }
    Lizzie.config.playSound = oriPlaySound;
  }

  public void findMove(int[] coords) {
    // TODO Auto-generated method stub
    BoardHistoryNode node = history.getCurrentHistoryNode();
    if (node.getData().lastMove.isPresent()
        && node.getData().lastMove.get()[0] == coords[0]
        && node.getData().lastMove.get()[1] == coords[1]) return;
    while (node.previous().isPresent()) {
      node = node.previous().get();
      if (node.getData().lastMove.isPresent()
          && node.getData().lastMove.get()[0] == coords[0]
          && node.getData().lastMove.get()[1] == coords[1]) {
        moveToAnyPosition(node);
        return;
      }
    }
    node = history.getCurrentHistoryNode();
    while (node.next().isPresent()) {
      node = node.next().get();
      if (node.getData().lastMove.isPresent()
          && node.getData().lastMove.get()[0] == coords[0]
          && node.getData().lastMove.get()[1] == coords[1]) {
        moveToAnyPosition(node);
        return;
      }
    }
    if (node.getData().lastMove.isPresent()
        && node.getData().lastMove.get()[0] == coords[0]
        && node.getData().lastMove.get()[1] == coords[1]) {
      moveToAnyPosition(node);
      return;
    }
    node = history.getStart();
    findMoveInAnyBranch(coords, node);
  }

  public void findMoveInAnyBranch(int[] coords, BoardHistoryNode node) {
    Stack<BoardHistoryNode> stack = new Stack<>();
    stack.push(node);
    while (!stack.isEmpty()) {
      BoardHistoryNode cur = stack.pop();
      if (cur.getData().lastMove.isPresent()
          && cur.getData().lastMove.get()[0] == coords[0]
          && cur.getData().lastMove.get()[1] == coords[1]) {
        moveToAnyPosition(cur);
        return;
      }
      if (cur.numberOfChildren() >= 1) {
        for (int i = cur.numberOfChildren() - 1; i >= 0; i--)
          stack.push(cur.getVariations().get(i));
      }
    }
  }

  public void showGroupResult() {
    Lizzie.frame.drawScore(boardGroupInfo);
    int blackAlive = 0, blackPoint = 0, whiteAlive = 0, whitePoint = 0;
    int blackCaptures = 0, whiteCaptures = 0;
    blackCaptures = Lizzie.board.getData().blackCaptures;
    whiteCaptures = Lizzie.board.getData().whiteCaptures;
    double komi = getHistory().getGameInfo().getKomi();
    for (int j = 0; j < boardHeight; j++) {
      for (int i = 0; i < boardWidth; i++) {
        if (!boardGroupInfo.groupStatus[i][j].isMarkedEmpty)
          if (boardGroupInfo.groupStatus[i][j].value == 1) {
            if (boardGroupInfo.oriStones[getIndex(i, j)] == Stone.BLACK) blackAlive++;
            else {
              if (boardGroupInfo.oriStones[getIndex(i, j)] == Stone.WHITE) blackCaptures++;
              blackPoint++;
            }
          } else if (boardGroupInfo.groupStatus[i][j].value == 2) {
            if (boardGroupInfo.oriStones[getIndex(i, j)] == Stone.WHITE) whiteAlive++;
            else {
              if (boardGroupInfo.oriStones[getIndex(i, j)] == Stone.BLACK) whiteCaptures++;
              whitePoint++;
            }
          }
      }
    }
    if (boardGroupInfo.scoreResult == null) {
      boardGroupInfo.scoreResult = new ScoreResult(Lizzie.frame);
      boardGroupInfo.scoreResult.setScore(
          blackAlive, blackPoint, whiteAlive, whitePoint, blackCaptures, whiteCaptures, komi);
      boardGroupInfo.scoreResult.setVisible(true);
    } else {
      boardGroupInfo.scoreResult.setScore(
          blackAlive, blackPoint, whiteAlive, whitePoint, blackCaptures, whiteCaptures, komi);
      boardGroupInfo.scoreResult.setVisible(true);
    }
  }

  public void toggleDeadStoneOrEmptyPoint(int coordX, int coordY) {
    for (int j = 0; j < boardHeight; j++) {
      for (int i = 0; i < boardWidth; i++) {
        boardGroupInfo.groupStatus[i][j].hasCalculated = false;
      }
    }
    if (boardGroupInfo == null) return;
    if (boardGroupInfo.oriStones[getIndex(coordX, coordY)] == Stone.EMPTY) {
      boardGroupInfo.groupStatus[coordX][coordY].isMarkedEmpty =
          !boardGroupInfo.groupStatus[coordX][coordY].isMarkedEmpty;
    } else {

      toggleDeadStone(
          coordX, coordY, boardGroupInfo, boardGroupInfo.oriStones[getIndex(coordX, coordY)]);
      //    , boardGroupInfo.groupStatus[coordX][coordY].isMarkedDead);
      for (int j = 0; j < boardHeight; j++) {
        for (int i = 0; i < boardWidth; i++) {
          boardGroupInfo.groupStatus[i][j].hasCalculated = false;
        }
      }
      reCalculateGroupInfo(boardGroupInfo);
    }
    showGroupResult();
  }

  private void toggleDeadStone(
      int i, int j, GroupInfo groupInfo, Stone oriStone) { // , boolean hasMarkedDead) {
    if (groupInfo.groupStatus[i][j].hasCalculated) return;
    if (groupInfo.oriStones[getIndex(i, j)] == oriStone)
      groupInfo.groupStatus[i][j].isMarkedDead = !groupInfo.groupStatus[i][j].isMarkedDead;
    //  else if (hasMarkedDead) return;
    else if (groupInfo.oriStones[getIndex(i, j)] != Stone.EMPTY) return;
    groupInfo.groupStatus[i][j].hasCalculated = true;
    if (i > 0) toggleDeadStone(i - 1, j, groupInfo, oriStone);
    if (j > 0) toggleDeadStone(i, j - 1, groupInfo, oriStone);
    if (i < boardWidth - 1) toggleDeadStone(i + 1, j, groupInfo, oriStone);
    if (j < boardHeight - 1) toggleDeadStone(i, j + 1, groupInfo, oriStone);
  }

  private void reCalculateGroupInfo(GroupInfo groupInfo) {
    Stone[] stones = groupInfo.oriStones;
    for (int j = 0; j < boardHeight; j++) {
      for (int i = 0; i < boardWidth; i++) {
        Stone stoneHere = stones[getIndex(i, j)];
        if (stoneHere == Stone.BLACK) {
          if (groupInfo.groupStatus[i][j].isMarkedDead) groupInfo.groupStatus[i][j].value = 2;
          else groupInfo.groupStatus[i][j].value = 1;
        } else if (stoneHere == Stone.WHITE) {
          if (groupInfo.groupStatus[i][j].isMarkedDead) groupInfo.groupStatus[i][j].value = 1;
          else groupInfo.groupStatus[i][j].value = 2;
        }
      }
    }
    for (int j = 0; j < boardHeight; j++) {
      for (int i = 0; i < boardWidth; i++) {
        Stone stoneHere = stones[getIndex(i, j)];
        if (stoneHere == Stone.EMPTY) {
          if (!groupInfo.groupStatus[i][j].hasCalculated) {
            calculateBlankGroupStart(i, j, groupInfo);
          }
        }
      }
    }
  }

  public void getGroupInfo() {
    if (boardGroupInfo != null)
      if (boardGroupInfo.scoreResult != null) boardGroupInfo.scoreResult.setVisible(false);
    Stone[] stones = getHistory().getData().stones;
    GroupInfo groupInfo = new GroupInfo();
    groupInfo.oriStones = stones;
    groupInfo.groupStatus = new GroupStatus[boardWidth][boardHeight];
    // groupInfo.markedStatus = new GroupStatus[boardWidth][boardHeight];
    for (int j = 0; j < boardHeight; j++) {
      for (int i = 0; i < boardWidth; i++) {
        groupInfo.groupStatus[i][j] = new GroupStatus();
        //  groupInfo.markedStatus[i][j] = new GroupStatus();
        Stone stoneHere = stones[getIndex(i, j)];
        if (stoneHere == Stone.BLACK) {
          groupInfo.groupStatus[i][j].value = 1;
        } else if (stoneHere == Stone.WHITE) {
          groupInfo.groupStatus[i][j].value = 2;
        }
      }
    }
    for (int j = 0; j < boardHeight; j++) {
      for (int i = 0; i < boardWidth; i++) {
        Stone stoneHere = stones[getIndex(i, j)];
        if (stoneHere == Stone.EMPTY) {
          if (!groupInfo.groupStatus[i][j].hasCalculated) {
            calculateBlankGroupStart(i, j, groupInfo);
          }
        }
      }
    }
    boardGroupInfo = groupInfo;
    showGroupResult();
  }

  private void calculateBlankGroupStart(int i, int j, GroupInfo groupInfo) {
    groupInfo.maxGoupIndex++;
    // System.out.println(groupInfo.maxGoupIndex);
    groupInfo.groupHasNextB = false;
    groupInfo.groupHasNextW = false;
    calculateBlankGroup(i, j, groupInfo);
    boolean shouldSetB = false;
    boolean shouldSetW = false;
    if (groupInfo.groupHasNextB && !groupInfo.groupHasNextW) shouldSetB = true;
    if (groupInfo.groupHasNextW && !groupInfo.groupHasNextB) shouldSetW = true;
    if (shouldSetB) {
      for (int m = 0; m < boardHeight; m++) {
        for (int n = 0; n < boardWidth; n++) {
          if (groupInfo.groupStatus[n][m].gourpIndex == groupInfo.maxGoupIndex)
            groupInfo.groupStatus[n][m].value = 1;
        }
      }
    } else if (shouldSetW) {
      for (int m = 0; m < boardHeight; m++) {
        for (int n = 0; n < boardWidth; n++) {
          if (groupInfo.groupStatus[n][m].gourpIndex == groupInfo.maxGoupIndex)
            groupInfo.groupStatus[n][m].value = 2;
        }
      }
    } else {
      for (int m = 0; m < boardHeight; m++) {
        for (int n = 0; n < boardWidth; n++) {
          if (groupInfo.groupStatus[n][m].gourpIndex == groupInfo.maxGoupIndex)
            groupInfo.groupStatus[n][m].value = 0;
        }
      }
    }
  }

  private void calculateBlankGroup(int i, int j, GroupInfo groupInfo) {
    if (groupInfo.groupStatus[i][j].hasCalculated
        || groupInfo.oriStones[getIndex(i, j)] != Stone.EMPTY) return;
    groupInfo.groupStatus[i][j].hasCalculated = true;
    groupInfo.groupStatus[i][j].gourpIndex = groupInfo.maxGoupIndex;
    boolean hasNextB = false;
    boolean hasNextW = false;
    if (i > 0) {
      Stone here = groupInfo.oriStones[getIndex(i - 1, j)];
      if (here == Stone.BLACK) {
        if (groupInfo.groupStatus[i - 1][j].isMarkedDead) hasNextW = true;
        else hasNextB = true;
      } else if (here == Stone.WHITE) {
        if (groupInfo.groupStatus[i - 1][j].isMarkedDead) hasNextB = true;
        else hasNextW = true;
      }
    }
    if (j > 0) {
      Stone here = groupInfo.oriStones[getIndex(i, j - 1)];
      if (here == Stone.BLACK) {
        if (groupInfo.groupStatus[i][j - 1].isMarkedDead) hasNextW = true;
        else hasNextB = true;
      } else if (here == Stone.WHITE) {
        if (groupInfo.groupStatus[i][j - 1].isMarkedDead) hasNextB = true;
        else hasNextW = true;
      }
    }
    if (i < boardWidth - 1) {
      Stone here = groupInfo.oriStones[getIndex(i + 1, j)];
      if (here == Stone.BLACK) {
        if (groupInfo.groupStatus[i + 1][j].isMarkedDead) hasNextW = true;
        else hasNextB = true;
      } else if (here == Stone.WHITE) {
        if (groupInfo.groupStatus[i + 1][j].isMarkedDead) hasNextB = true;
        else hasNextW = true;
      }
    }
    if (j < boardHeight - 1) {
      Stone here = groupInfo.oriStones[getIndex(i, j + 1)];
      if (here == Stone.BLACK) {
        if (groupInfo.groupStatus[i][j + 1].isMarkedDead) hasNextW = true;
        else hasNextB = true;
      } else if (here == Stone.WHITE) {
        if (groupInfo.groupStatus[i][j + 1].isMarkedDead) hasNextB = true;
        else hasNextW = true;
      }
    }
    if (hasNextB) groupInfo.groupHasNextB = true;
    if (hasNextW) groupInfo.groupHasNextW = true;
    if (i > 0) calculateBlankGroup(i - 1, j, groupInfo);
    if (j > 0) calculateBlankGroup(i, j - 1, groupInfo);
    if (i < boardWidth - 1) calculateBlankGroup(i + 1, j, groupInfo);
    if (j < boardHeight - 1) calculateBlankGroup(i, j + 1, groupInfo);
  }

  public void setBigBranch() {
    hasBigBranch = true;
  }

  public boolean hasBigBranch() {
    return hasBigBranch;
  }

  public void clearBigBranch() {
    hasBigBranch = false;
  }

  public void changeNextTurn() {
    // TODO Auto-generated method stub
    if (Lizzie.leelaz.canAddPlayer) {
      getHistory().getCurrentHistoryNode().getData().blackToPlay =
          !getHistory().getCurrentHistoryNode().getData().blackToPlay;
      clearbestmoves();
      if (Lizzie.leelaz.isPondering()) Lizzie.leelaz.ponder();
    } else {
      this.pass();
    }
  }

  public final int MINIMUM_LADDER_LENGTH_FOR_AUTO_CONTINUATION = 5;

  public int continueLadder() {
    int k;
    // Repeating continueLadderByOne() is inefficient. So what? :p
    for (k = 0; continueLadderByOne(); k++) ;
    Lizzie.frame.refresh();
    return k;
  }

  private boolean continueLadderByOne() {
    final int PERIOD = 4, CHECK_LENGTH = MINIMUM_LADDER_LENGTH_FOR_AUTO_CONTINUATION;
    BoardHistoryList copiedHistory = history.shallowCopy();
    int[][] pastMove = new int[CHECK_LENGTH][];
    int dx = 0, dy = 0;
    for (int k = 0; k < CHECK_LENGTH; k++) {
      Optional<int[]> lastMoveOpt = copiedHistory.getLastMove();
      if (!lastMoveOpt.isPresent()) return false;
      int[] move = pastMove[k] = lastMoveOpt.get();
      copiedHistory.previous();
      if (k < PERIOD) continue;
      // check repeated pattern
      int[] periodMove = pastMove[k - PERIOD];
      int deltaX = periodMove[0] - move[0], deltaY = periodMove[1] - move[1];
      if (k == PERIOD) { // first periodical move
        dx = deltaX;
        dy = deltaY;
      }
      boolean isRepeated = (deltaX == dx && deltaY == dy);
      boolean isDiagonal = (Math.abs(deltaX) == 1 && Math.abs(deltaY) == 1);
      if (!isRepeated || !isDiagonal) return false;
    }
    int[] myPeriodMove = pastMove[PERIOD - 1];
    int x = myPeriodMove[0] + dx, y = myPeriodMove[1] + dy;
    boolean continued = isValidEmpty(x, y) && isValidEmpty(x + dx, y) && isValidEmpty(x, y + dy);
    if (!continued) return false;
    place(x, y);
    return true;
  }

  public boolean isCoordsEmpty(int x, int y) {
    if (history.getStones()[getIndex(x, y)] != Stone.EMPTY) {
      return false;
    }
    return true;
  }

  private boolean isValidEmpty(int x, int y) {
    return isValid(x, y) && isCoordsEmpty(x, y);
  }

  public boolean isFirstWhiteNodeWithHandicap(BoardHistoryNode node) {
    // TODO Auto-generated method stub
    if (node.getData().lastMove.isPresent() && node.getData().lastMoveColor != Stone.WHITE) {
      return false;
    }
    int blackStones = 0;
    while (node.previous().isPresent()) {
      node = node.previous().get();
      if (node.getData().lastMove.isPresent())
        if (node.getData().lastMoveColor == Stone.WHITE) {
          return false;
        }
      if (node.getData().lastMoveColor == Stone.BLACK) blackStones++;
    }
    if (blackStones > 1) return true;
    else return false;
  }

  public boolean hasStoneAt(int[] coords) {
    if (history.getStones()[getIndex(coords[0], coords[1])] != Stone.EMPTY) return true;
    return false;
  }

  public void clearPressStoneInfo(int[] coords) {
    if (preMouseOnStone) {
      if (coords == null
          || coords[0] != mouseOnStoneCoords[0]
          || coords[1] != mouseOnStoneCoords[1]) {
        isMouseOnStone = false;
        preMouseOnStone = false;
        mouseOnStoneCoords = LizzieFrame.outOfBoundCoordinate;
        if (reviewThread != null) reviewThread.interrupt();
        Lizzie.frame.refresh();
      }
    }
  }

  public void setPressStoneInfo(int[] coords) {
    if (!Lizzie.config.enableClickReview) {
      return;
    }
    isMouseOnStone = false;
    preMouseOnStone = true;
    mouseOnStoneCoords = coords;
    mouseOnNode = null;
    Runnable runnable =
        new Runnable() {
          public void run() {
            try {
              Thread.sleep(50);
            } catch (InterruptedException e) {
              // TODO Auto-generated catch block
              e.printStackTrace();
            }
            if (preMouseOnStone) {
              isMouseOnStone = true;
              BoardHistoryNode node = getHistory().getCurrentHistoryNode();
              while (node.previous().isPresent()) {
                if (node.getData().lastMove.isPresent()) {
                  if (node.getData().lastMove.get()[0] == mouseOnStoneCoords[0]
                      && node.getData().lastMove.get()[1] == mouseOnStoneCoords[1]) {
                    mouseOnNode = node.previous().get();
                    break;
                  }
                }
                node = node.previous().get();
              }
              if (mouseOnNode != null) {
                isMouseOnStone = true;
                startReviewThread();
              } else {
                isMouseOnStone = false;
                mouseOnStoneCoords = LizzieFrame.outOfBoundCoordinate;
              }
            }
          }
        };
    Thread thread = new Thread(runnable);
    thread.start();
  }

  private Thread reviewThread;
  public int reviewLength;

  private void startReviewThread() {
    int secs = (int) (Lizzie.config.replayBranchIntervalSeconds * 1000);
    if (reviewThread != null) reviewThread.interrupt();
    Runnable runnable =
        new Runnable() {
          public void run() {
            reviewLength = 1;
            Lizzie.frame.refresh();
            while (isMouseOnStone) {
              try {
                Thread.sleep(secs);
              } catch (InterruptedException e) {
                return;
              }
              reviewLength++;
              Lizzie.frame.refresh();
            }
          }
        };
    reviewThread = new Thread(runnable);
    reviewThread.start();
  }
}
