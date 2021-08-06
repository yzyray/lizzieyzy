package featurecat.lizzie.rules;

import featurecat.lizzie.Lizzie;
import featurecat.lizzie.analysis.EngineManager;
import featurecat.lizzie.analysis.Leelaz;
import featurecat.lizzie.util.Utils;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/** Node structure for the board history / sgf tree */
public class BoardHistoryNode {
  private Optional<BoardHistoryNode> previous;
  public ArrayList<BoardHistoryNode> variations;
  public NodeInfo nodeInfo;
  public NodeInfo nodeInfoMain;
  public NodeInfo nodeInfo2;
  public NodeInfo nodeInfoMain2;
  public boolean analyzed = false;
  public boolean diffAnalyzed = false;
  public boolean isBest = false;
  public ArrayList<ExtraStones> extraStones;

  private BoardData data;

  // Save the children for restore to branch
  private int fromBackChildren;

  /** Initializes a new list node */
  public BoardHistoryNode(BoardData data) {
    previous = Optional.empty();
    variations = new ArrayList<BoardHistoryNode>();
    nodeInfo = new NodeInfo();
    nodeInfoMain = new NodeInfo();
    nodeInfo2 = new NodeInfo();
    nodeInfoMain2 = new NodeInfo();
    this.data = data;
  }

  /** Remove all subsequent nodes. */
  public void clear() {
    variations.clear();
  }

  public void addExtraStones(int x, int y, boolean isBlack) {
    if (extraStones == null) extraStones = new ArrayList<ExtraStones>();
    ExtraStones stone = new ExtraStones();
    stone.x = x;
    stone.y = y;
    stone.isBlack = isBlack;
    extraStones.add(stone);
  }

  public void undoExtraStones() {
    if (extraStones == null || Lizzie.board.isLoadingFile) return;
    for (ExtraStones stone : extraStones) {
      Lizzie.leelaz.undo();
    }
  }

  public void placeExtraStones() {
    if (extraStones == null || Lizzie.board.isLoadingFile) return;
    for (ExtraStones stone : extraStones) {
      Lizzie.leelaz.playMove(
          stone.isBlack ? Stone.BLACK : Stone.WHITE,
          Lizzie.board.convertCoordinatesToName(stone.x, stone.y));
    }
  }

  /**
   * Sets up for a new node. Overwrites future history.
   *
   * @param node the node following this one
   * @return the node that was just set
   */
  public BoardHistoryNode add(BoardHistoryNode node) {
    variations.clear();
    variations.add(node);
    node.previous = Optional.of(this);

    return node;
  }

  /**
   * If we already have a next node with the same BoardData, move to it, otherwise add it and move
   * to it.
   *
   * @param data the node following this one
   * @return the node that was just set
   */
  public BoardHistoryNode addOrGoto(BoardData data) {
    return addOrGoto(data, false);
  }

  public BoardHistoryNode addOrGoto(BoardData data, boolean newBranch) {
    return addOrGoto(data, newBranch, false, false);
  }

  /**
   * If we already have a next node with the same BoardData, move to it, otherwise add it and move
   * to it.
   *
   * @param data the node following this one
   * @param newBranch add a new branch
   * @return the node that was just set *
   */
  public static double lastWinrateDiff(BoardHistoryNode node) {

    // Last winrate
    Optional<BoardData> lastNode = node.previous().flatMap(n -> Optional.of(n.getData()));
    boolean validLastWinrate = lastNode.map(d -> d.getPlayouts() > 0).orElse(false);
    double lastWR = validLastWinrate ? lastNode.get().winrate : 50;

    // Current winrate
    BoardData data = node.getData();
    boolean validWinrate = false;
    double curWR = 50;
    if (data == Lizzie.board.getHistory().getData()) {
      Leelaz.WinrateStats stats = Lizzie.leelaz.getWinrateStats();
      curWR = stats.maxWinrate;
      validWinrate = (stats.totalPlayouts > 0);
      if (Lizzie.frame.isPlayingAgainstLeelaz
          && Lizzie.frame.playerIsBlack == !Lizzie.board.getHistory().getData().blackToPlay) {
        validWinrate = false;
      }
    } else {
      validWinrate = (data.getPlayouts() > 0);
      curWR = validWinrate ? data.winrate : 100 - lastWR;
    }

    // Last move difference winrate
    if (validLastWinrate && validWinrate) {
      return 100 - lastWR - curWR;
    } else {
      return 0;
    }
  }

  public BoardHistoryNode addOrGoto(
      BoardData data, boolean newBranch, boolean changeMove, boolean clearAfterMove) {
    if (!Lizzie.board.isLoadingFile
        && Lizzie.leelaz != null
        && !EngineManager.isEngineGame
        && clearAfterMove) {
      Lizzie.leelaz.clearBestMoves();
    }
    Optional<BoardHistoryNode> next = next(true);
    boolean nextDummy = next.isPresent() && next.get().isEndDummay();
    if (!newBranch && nextDummy) {
      changeMove = true;
    }
    if (!newBranch) {
      for (int i = 0; i < variations.size(); i++) {
        if (variations.get(i).data.zobrist.equals(data.zobrist)) {
          // if (i != 0) {
          // // Swap selected next to foremost
          // BoardHistoryNode currentNext = variations.get(i);
          // variations.set(i, variations.get(0));
          // variations.set(0, currentNext);
          // }
          if (i != 0 && changeMove) {
            break;
          }
          if (Lizzie.config.playSound) Utils.playVoiceFile();
          if (clearAfterMove) Lizzie.board.clearAfterMove();
          return variations.get(i);
        }
      }
    }

    if (!this.previous.isPresent()) {
      data.moveMNNumber = 1;
    }
    if (Lizzie.config.newMoveNumberInBranch && !variations.isEmpty() && !changeMove) {
      if (!newBranch) {
        data.moveNumberList = new int[Board.boardWidth * Board.boardHeight];
        data.moveMNNumber = -1;
      }
      if (data.moveMNNumber == -1) {
        data.moveMNNumber = data.dummy ? 0 : 1;
      }
      data.lastMove.ifPresent(
          m -> data.moveNumberList[Board.getIndex(m[0], m[1])] = data.moveMNNumber);
    }
    BoardHistoryNode node = new BoardHistoryNode(data);
    if (changeMove) {
      //   Optional<BoardHistoryNode> next = next(true);
      next.ifPresent(
          n -> {
            node.variations = n.variations;
            n.previous = null;
            n.variations.stream().forEach(v -> v.previous = Optional.of(node));
            variations.set(0, node);
          });

    } else {
      // Add node
      if (variations.size() == 0) {
        if (this.data.blackToPlay != node.getData().lastMoveColor.isBlack()) {
          this.data.blackToPlay = node.getData().lastMoveColor.isBlack();
        }
      }
      variations.add(node);
    }
    node.previous = Optional.of(this);
    if (Lizzie.config.playSound) Utils.playVoiceFile();
    if (clearAfterMove) Lizzie.board.clearAfterMove();
    return node;
  }

  /** @return data stored on this node */
  public BoardData getData() {
    return data;
  }

  /** @return variations for display */
  public List<BoardHistoryNode> getVariations() {
    return variations;
  }

  public Optional<BoardHistoryNode> previous() {
    return previous;
  }

  public Optional<BoardHistoryNode> next() {
    return next(false);
  }

  public Optional<BoardHistoryNode> next(boolean includeDummy) {
    return variations.isEmpty() || (!includeDummy && variations.get(0).isEndDummay())
        ? Optional.empty()
        : Optional.of(variations.get(0));
  }

  public boolean isEndDummay() {
    return this.data.dummy && variations.isEmpty();
  }

  public Optional<BoardHistoryNode> now() {
    return Optional.of(this);
  }

  public BoardHistoryNode topOfBranch() {
    BoardHistoryNode top = this;
    while (top.previous.isPresent() && top.previous.get().variations.size() == 1) {
      top = top.previous.get();
    }
    return top;
  }

  public BoardHistoryNode topOfFatherBranch() {
    BoardHistoryNode top = this;
    while (top.previous.isPresent() && !top.previous.get().isMainTrunk()) {
      top = top.previous.get();
    }
    return top;
  }

  public BoardHistoryNode topOfFatherBranch2() {
    BoardHistoryNode top = this;
    while (top.previous.isPresent() && !top.previous.get().isMainTrunk()) {
      top = top.previous.get();
    }
    top = top.previous.get();
    return top;
  }

  public int numberOfChildren() {
    if (variations == null) return 0;
    else return variations.size();
  }

  public boolean hasVariations() {
    return numberOfChildren() > 1;
  }

  public boolean isFirstChild() {
    return previous.flatMap(p -> p.next().map(n -> n == this)).orElse(false);
  }

  public Optional<BoardHistoryNode> getVariation(int idx) {
    if (variations.size() <= idx) {
      return Optional.empty();
    } else {
      return Optional.of(variations.get(idx));
    }
  }

  public void moveUp() {
    previous.ifPresent(p -> p.moveChildUp(this));
  }

  public void moveDown() {
    previous.ifPresent(p -> p.moveChildDown(this));
  }

  public void moveChildUp(BoardHistoryNode child) {
    for (int i = 1; i < variations.size(); i++) {
      if (variations.get(i).data.zobrist.equals(child.data.zobrist)) {
        BoardHistoryNode tmp = variations.get(i - 1);
        variations.set(i - 1, child);
        variations.set(i, tmp);
        if ((i - 1) == 0) {
          child.swapMoveNumberList(tmp);
        }
        return;
      }
    }
  }

  public void moveChildDown(BoardHistoryNode child) {
    for (int i = 0; i < variations.size() - 1; i++) {
      if (variations.get(i).data.zobrist.equals(child.data.zobrist)) {
        BoardHistoryNode tmp = variations.get(i + 1);
        variations.set(i + 1, child);
        variations.set(i, tmp);
        if (i == 0) {
          tmp.swapMoveNumberList(child);
        }
        return;
      }
    }
  }

  public void swapMoveNumberList(BoardHistoryNode child) {
    int childStart = child.getData().moveMNNumber;
    child.resetMoveNumberListBranch(this.getData().moveMNNumber);
    this.resetMoveNumberListBranch(childStart);
  }

  public void resetMoveNumberListBranch(int start) {
    this.resetMoveNumberList(start);
    BoardHistoryNode node = this;
    while (node.next().isPresent()) {
      node = node.next().get();
      start++;
      node.resetMoveNumberList(start);
    }
  }

  public void resetMoveNumberList(int start) {
    BoardData data = this.getData();
    int[] moveNumberList = data.moveNumberList;
    data.moveMNNumber = start;
    if (data.lastMove.isPresent() && !data.dummy) {
      int[] move = data.lastMove.get();
      moveNumberList[Board.getIndex(move[0], move[1])] = start;
    }
    Optional<BoardHistoryNode> node = this.previous();
    int moveNumber = start;
    while (node.isPresent()) {
      BoardData nodeData = node.get().getData();
      if (nodeData.lastMove.isPresent()) {
        int[] move = nodeData.lastMove.get();
        moveNumber = (moveNumber > 1) ? moveNumber - 1 : 0;
        moveNumberList[Board.getIndex(move[0], move[1])] = moveNumber;
      }
      node = node.get().previous();
    }
  }

  public void deleteChild(int idx) {
    if (idx < numberOfChildren()) {
      variations.remove(idx);
    }
  }

  /** @param fromBackChildren the fromBackChildren to set */
  public void setFromBackChildren(int fromBackChildren) {
    this.fromBackChildren = fromBackChildren;
  }

  /** @return the fromBackChildren */
  public int getFromBackChildren() {
    return fromBackChildren;
  }

  /**
   * Returns the number of moves in a tree (only the left-most (trunk) variation)
   *
   * @return number of moves in a tree
   */
  public int getDepth() {
    BoardHistoryNode node = this;
    int c = 0;
    while (node.next().isPresent()) {
      c++;
      node = node.next().get();
    }
    return c;
  }

  /**
   * Given some depth, returns the child at the given depth in the main trunk. If the main variation
   * is too short, silently stops early.
   *
   * @return the child at the given depth
   */
  public BoardHistoryNode childAtDepth(int depth) {
    BoardHistoryNode node = this;
    for (int i = 0; i < depth; i++) {
      Optional<BoardHistoryNode> next = node.next();
      if (next.isPresent()) {
        node = next.get();
      } else {
        break;
      }
    }
    return node;
  }

  /**
   * Check if there is a branch that is at least depth deep (at least depth moves)
   *
   * @return true if it is deep enough, false otherwise
   */
  public boolean hasDepth(int depth) {
    BoardHistoryNode node = this;
    int c = 0;
    while (node.next().isPresent()) {
      if (node.hasVariations()) {
        int variationDepth = depth - c - 1;
        return node.getVariations().stream().anyMatch(v -> v.hasDepth(variationDepth));
      } else {
        node = node.next().get();
        c++;
        if (c >= depth) {
          return true;
        }
      }
    }
    return false;
  }

  /**
   * Find top of variation (the first move that is on the main trunk)
   *
   * @return top of variation, if on main trunk, return start move
   */
  public BoardHistoryNode findTop() {
    BoardHistoryNode start = this;
    BoardHistoryNode top = start;
    while (start.previous().isPresent()) {
      BoardHistoryNode pre = start.previous().get();
      if (pre.next().isPresent() && pre.next().get() != start) {
        top = pre;
      }
      start = pre;
    }
    return top;
  }

  /**
   * Finds the first parent with variations.
   *
   * @return the first parent with variations, if any, Optional.empty otherwise
   */
  public Optional<BoardHistoryNode> firstParentWithVariations() {
    return this.findChildOfPreviousWithVariation().flatMap(v -> v.previous());
  }

  /**
   * Find first move with variations in tree above node.
   *
   * @return The child (in the current variation) of the first node with variations
   */
  public Optional<BoardHistoryNode> findChildOfPreviousWithVariation() {
    BoardHistoryNode node = this;
    while (node.previous().isPresent()) {
      BoardHistoryNode pre = node.previous().get();
      if (pre.hasVariations()) {
        return Optional.of(node);
      }
      node = pre;
    }
    return Optional.empty();
  }

  /**
   * Given a child node, find the index of that child node in its parent
   *
   * @return index of child node, -1 if child node not a child of parent
   */
  public int indexOfNode(BoardHistoryNode childNode) {
    if (!next(true).isPresent()) {
      return -1;
    }
    for (int i = 0; i < numberOfChildren(); i++) {
      if (getVariation(i).map(v -> v == childNode).orElse(false)) {
        return i;
      }
    }
    return -1;
  }

  /**
   * Given a child node, find the index of that child node in its parent
   *
   * @return index of child node, -1 if child node not a child of parent
   */
  public int findIndexOfNode(BoardHistoryNode childNode) {
    if (!next().isPresent()) {
      return -1;
    }
    for (int i = 0; i < numberOfChildren(); i++) {
      Optional<BoardHistoryNode> node = getVariation(i);
      while (node.isPresent()) {
        if (node.map(n -> n == childNode).orElse(false)) {
          return i;
        }
        node = node.get().next();
      }
    }
    return -1;
  }

  /**
   * Given a child node, find the depth of that child node in its parent
   *
   * @return depth of child node, 0 if child node not a child of parent
   */
  public int depthOfNode(BoardHistoryNode childNode) {
    if (!next().isPresent()) {
      return 0;
    }
    for (int i = 0; i < numberOfChildren(); i++) {
      Optional<BoardHistoryNode> node = getVariation(i);
      int move = 1;
      while (node.isPresent()) {
        if (node.map(n -> n == childNode).orElse(false)) {
          return move;
        }
        move++;
        node = node.get().next();
      }
    }
    return 0;
  }

  /**
   * The move number of that node in its branch
   *
   * @return move number of node, 0 if node not a child of branch
   */
  public int moveNumberInBranch() {
    Optional<BoardHistoryNode> top = firstParentWithVariations();
    return top.isPresent() ? top.get().moveNumberOfNode() + top.get().depthOfNode(this) : 0;
  }

  /**
   * The move number of that node
   *
   * @return move number of node
   */
  public int moveNumberOfNode() {
    return isMainTrunk() ? getData().moveNumber : moveNumberInBranch();
  }

  public boolean isCurTrunk() {
    BoardHistoryNode node = this;
    BoardHistoryNode curNode = Lizzie.board.getHistory().getEnd();
    if (node == curNode) return true;
    while (curNode.previous().isPresent()) {
      curNode = curNode.previous().get();
      if (node == curNode) {
        return true;
      }
    }
    return false;
  }
  /**
   * Check if node is part of the main trunk (rightmost branch)
   *
   * @return true if node is part of main trunk, false otherwise
   */
  public boolean isMainTrunk() {
    BoardHistoryNode node = this;
    while (node.previous().isPresent()) {
      BoardHistoryNode pre = node.previous().get();
      if (pre.next(true).isPresent() && pre.next(true).get() != node) {
        return false;
      }
      node = pre;
    }
    return true;
  }

  /**
   * Go to the next node with the comment.
   *
   * @return the move count to the next node with comment, 0 otherwise
   */
  public int goToNextNodeWithComment() {
    BoardHistoryNode node = this;
    int moves = 0;
    while (node.next().isPresent()) {
      moves++;
      BoardHistoryNode next = node.next().get();
      if (!next.getData().comment.isEmpty()) {
        break;
      }
      node = next;
    }
    return moves;
  }

  /**
   * Go to the previous node with the comment.
   *
   * @return the move count to the previous node with comment, 0 otherwise
   */
  public int goToPreviousNodeWithComment() {
    BoardHistoryNode node = this;
    int moves = 0;
    while (node.previous().isPresent()) {
      moves++;
      BoardHistoryNode previous = node.previous().get();
      if (!previous.getData().comment.isEmpty()) {
        break;
      }
      node = previous;
    }
    return moves;
  }

  public void sync(BoardHistoryNode node) {
    if (node == null) return;

    BoardHistoryNode cur = this;
    // Compare
    while (node != null) {
      if (!cur.compare(node)) {
        BoardData sData = cur.getData();
        sData.sync(node.getData());
        if (sData.getPlayouts() > 0) sData.comment = SGFParser.formatComment(cur);
        if (node.numberOfChildren() > 0) {
          for (int i = 0; i < node.numberOfChildren(); i++) {
            if (node.getVariation(i).isPresent()) {
              if (cur.variations.size() <= i) {
                cur.addOrGoto(node.getVariation(i).get().getData().clone(), (i > 0));
              }
              if (i > 0) {
                cur.variations.get(i).sync(node.getVariation(i).get());
              }
            }
          }
        }
      }
      cur = cur.next(true).map(n -> n).orElse(null);
      node = node.next(true).map(n -> n).orElse(null);
    }
  }

  public boolean compare(BoardHistoryNode node) {
    BoardData sData = this.getData();
    BoardData dData = node.getData();

    boolean dMove =
        sData.lastMove.isPresent() && dData.lastMove.isPresent()
            || !sData.lastMove.isPresent() && !dData.lastMove.isPresent();
    if (dMove && sData.lastMove.isPresent()) {
      int[] sM = sData.lastMove.get();
      int[] dM = dData.lastMove.get();
      dMove =
          (sM != null
              && sM.length == 2
              && dM != null
              && dM.length == 2
              && sM[0] == dM[0]
              && sM[1] == dM[1]);
    }
    return dMove
        && sData.comment != null
        && sData.comment.equals(dData.comment)
        && this.numberOfChildren() == node.numberOfChildren();
  }

  public void resetMoveNumberList() {
    // TODO Auto-generated method stub
    if (!this.previous.isPresent() || !this.getData().lastMove.isPresent()) return;
    boolean isNewBranch = this.previous.get().variations.get(0) != this;
    this.getData().moveNumberList =
        isNewBranch
            ? new int[Board.boardWidth * Board.boardHeight]
            : this.previous.get().getData().moveNumberList.clone();

    this.getData()
            .moveNumberList[
            Board.getIndex(this.getData().lastMove.get()[0], this.getData().lastMove.get()[1])] =
        isNewBranch ? 1 : this.getData().moveNumber;
    if (this.numberOfChildren() >= 1) this.variations.get(0).resetMoveNumberList();
  }
}
