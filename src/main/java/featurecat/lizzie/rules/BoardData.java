package featurecat.lizzie.rules;

import featurecat.lizzie.Lizzie;
import featurecat.lizzie.analysis.EngineManager;
import featurecat.lizzie.analysis.MoveData;
import featurecat.lizzie.gui.LizzieFrame;
import java.util.*;

public class BoardData {
  public int moveNumber;
  public int moveMNNumber;
  public Optional<int[]> lastMove;
  public int[] moveNumberList;
  public boolean blackToPlay;
  public boolean dummy;
  // added for change bestmoves when playouts is not increased

  public Stone lastMoveColor;
  public Stone[] stones;
  public Zobrist zobrist;
  public boolean verify;

  public double winrate;
  public double winrate2;
  private int playouts;
  private int playouts2;
  public double scoreMean;
  public double scoreMean2;
  public double scoreStdev;
  public double scoreStdev2;
  // public double scoreMeanBoard;
  // public double scoreMeanBoard2;
  public List<MoveData> bestMoves;
  public List<MoveData> bestMovesOutOfRange;
  public List<MoveData> bestMoves2;
  public List<MoveData> bestMoves2OutOfRange;
  public int blackCaptures;
  public int whiteCaptures;
  public boolean isChanged = false;
  public boolean isChanged2 = false;
  public String comment = "";
  // public String comment2 = "";
  public String engineName = "";
  public String engineName2 = "";
  public boolean isSaiData;
  public boolean isSaiData2;
  public boolean isKataData;
  public boolean isKataData2;
  //  public boolean isPDA;
  //  public boolean isPDA2;
  public double pda = 0;
  public double pda2 = 0;
  public double komi = -999;
  public double wrn = 0;
  //	public boolean commented=true;
  //	public boolean commented2=true;

  // Node properties
  private Map<String, String> properties = new HashMap<String, String>();

  public BoardData(
      Stone[] stones,
      Optional<int[]> lastMove,
      Stone lastMoveColor,
      boolean blackToPlay,
      Zobrist zobrist,
      int moveNumber,
      int[] moveNumberList,
      int blackCaptures,
      int whiteCaptures,
      double winrate,
      int playouts) {
    this.moveMNNumber = -1;
    this.moveNumber = moveNumber;
    this.lastMove = lastMove;
    this.moveNumberList = moveNumberList;
    this.blackToPlay = blackToPlay;
    this.dummy = false;
    this.lastMoveColor = lastMoveColor;
    this.stones = stones;
    this.zobrist = zobrist;
    this.verify = false;

    this.winrate = winrate;
    this.playouts = playouts;
    this.blackCaptures = blackCaptures;
    this.whiteCaptures = whiteCaptures;
    this.bestMoves = new ArrayList<>();
    this.bestMoves2 = new ArrayList<>();
  }

  public double getKomi() {
    if (komi != -999) return komi;
    else return Lizzie.board.getHistory().getGameInfo().getKomi();
  }

  public static BoardData empty(int width, int height) {
    Stone[] stones = new Stone[width * height];
    for (int i = 0; i < stones.length; i++) {
      stones[i] = Stone.EMPTY;
    }

    int[] boardArray = new int[width * height];
    return new BoardData(
        stones, Optional.empty(), Stone.EMPTY, true, new Zobrist(), 0, boardArray, 0, 0, 50, 0);
  }

  /**
   * Add a key and value
   *
   * @param key
   * @param value
   */
  public void addProperty(String key, String value) {
    SGFParser.addProperty(properties, key, value);
    if ("N".equals(key) && comment.isEmpty()) {
      comment = value;
    } else if ("MN".equals(key)) {
      moveMNNumber = Integer.parseInt(getOrDefault("MN", "-1"));
    }
  }

  /**
   * Get a value with key
   *
   * @param key
   * @return
   */
  public String getProperty(String key) {
    return properties.get(key);
  }

  /**
   * Get a value with key, or the default if there is no such key
   *
   * @param key
   * @param defaultValue
   * @return
   */
  public String getOrDefault(String key, String defaultValue) {
    return SGFParser.getOrDefault(properties, key, defaultValue);
  }

  /**
   * Get the properties
   *
   * @return
   */
  public Map<String, String> getProperties() {
    return properties;
  }

  public void setProperties(Map<String, String> properties) {
    this.properties = properties;
  }

  /**
   * Add the properties
   *
   * @return
   */
  public void addProperties(Map<String, String> addProps) {
    SGFParser.addProperties(this.properties, addProps);
  }

  /**
   * Add the properties from string
   *
   * @return
   */
  public void addProperties(String propsStr) {
    SGFParser.addProperties(properties, propsStr);
  }

  /**
   * Get properties string
   *
   * @return
   */
  public String propertiesString() {
    return SGFParser.propertiesString(properties);
  }

  public double getWinrate() {
    if (!blackToPlay || !Lizzie.config.winrateAlwaysBlack) {
      return winrate;
    } else {
      return 100 - winrate;
    }
  }

  public double getWinrate2() {
    if (!blackToPlay || !Lizzie.config.winrateAlwaysBlack) {
      return winrate2;
    } else {
      return 100 - winrate2;
    }
  }

  public void tryToSetBestMoves(
      List<MoveData> moves, String engName, boolean isFromLeelaz, int totalplayouts) {
    if (Lizzie.config.enableLizzieCache
        && !Lizzie.config.isAutoAna
        && !EngineManager.isEngineGame) {
      if (!(totalplayouts > playouts || isChanged || pda != Lizzie.leelaz.pda)) {
        return;
      }
    }
    // added for change bestmoves when playouts is not increased
    if (totalplayouts < playouts) isChanged = false;
    setPlayouts(totalplayouts);
    winrate = moves.get(0).winrate;
    if (moves.get(0).isKataData) {
      scoreMean = moves.get(0).scoreMean;
      scoreStdev = moves.get(0).scoreStdev;
      if (isFromLeelaz) {
        Lizzie.leelaz.scoreMean = moves.get(0).scoreMean;
        Lizzie.leelaz.scoreStdev = moves.get(0).scoreStdev;
      }
      isKataData = true;
    } else isKataData = false;
    isSaiData = moves.get(0).isSaiData;
    engineName = engName;
    komi = Lizzie.board.getHistory().getGameInfo().getKomi();
    if (isFromLeelaz) {
      if (Lizzie.leelaz.isDymPda || Lizzie.leelaz.pda != 0) {
        pda = Lizzie.leelaz.pda;
      } else pda = 0;
    }
    if (!(EngineManager.isEngineGame && EngineManager.engineGameInfo.isGenmove))
      wrn = Lizzie.leelaz.wrn;
    // 排序
    Collections.sort(
        moves,
        new Comparator<MoveData>() {

          @Override
          public int compare(MoveData s1, MoveData s2) {
            // 降序
            if (s1.order < s2.order) return -1;
            if (s1.order > s2.order) return 1;
            return 0;
          }
        });

    tryToLimitMoves(moves, bestMoves, true);
    bestMoves = moves;
  }

  private void tryToLimitMoves(List<MoveData> moves, List<MoveData> lastMoves, boolean isMain) {
    // TODO Auto-generated method stub
    List<MoveData> outOfRangeMoves = new ArrayList<>();
    if (Lizzie.config.limitMaxSuggestion > 0
        && !Lizzie.config.showNoSuggCircle
        && (moves.size() > Lizzie.config.limitMaxSuggestion)) {
      for (int n = Lizzie.config.limitMaxSuggestion; n < moves.size(); n++) {
        MoveData move = moves.get(n);
        boolean needSkip = false;
        int absoluteMaxSuggestionOrder = Lizzie.config.limitMaxSuggestion + 1;
        if (move.order < absoluteMaxSuggestionOrder) {
          for (int s = 0; s < absoluteMaxSuggestionOrder && s < lastMoves.size(); s++) {
            MoveData lastBestMove = lastMoves.get(s);
            if (s >= Lizzie.config.limitMaxSuggestion) {
              if (!lastBestMove.lastTimeUnlimited) continue;
            }
            if (move.coordinate.equals(lastBestMove.coordinate)) {
              move.lastTimeUnlimited = true;
              if (move.playouts > lastBestMove.playouts || !lastBestMove.lastTimeUnlimited) {
                move.lastTimeUnlimitedTime = System.currentTimeMillis();
                needSkip = true;
              } else if (System.currentTimeMillis() - lastBestMove.lastTimeUnlimitedTime < 3000) {
                move.lastTimeUnlimitedTime = lastBestMove.lastTimeUnlimitedTime;
                needSkip = true;
              }
              continue;
            }
          }
        }
        if (Lizzie.frame.priorityMoveCoords.size() > 0 && !needSkip) {
          for (String coords : Lizzie.frame.priorityMoveCoords) {
            if (move.coordinate.equals(coords)) {
              needSkip = true;
              continue;
            }
          }
        }
        if (!needSkip) {
          outOfRangeMoves.add(move);
          moves.remove(move);
          n--;
        }
      }
      if (isMain) bestMovesOutOfRange = outOfRangeMoves;
      else bestMoves2OutOfRange = outOfRangeMoves;
    }
  }

  public void tryToSetBestMoves2(
      List<MoveData> moves, String engName, boolean isFromLeelaz, int totalplayouts) {
    if (Lizzie.config.enableLizzieCache && !Lizzie.config.isAutoAna) {
      if (!(totalplayouts > playouts2
          || isChanged2
          || pda != Lizzie.leelaz.pda)) { // ||Lizzie.frame.urlSgf
        return;
      }
    }
    if (totalplayouts < playouts2) isChanged2 = false;
    setPlayouts2(totalplayouts);
    winrate2 = moves.get(0).winrate;
    if (moves.get(0).isKataData) {
      scoreMean2 = moves.get(0).scoreMean;
      scoreStdev2 = moves.get(0).scoreStdev;
      if (Lizzie.leelaz2 != null && isFromLeelaz) {
        Lizzie.leelaz2.scoreMean = moves.get(0).scoreMean;
        Lizzie.leelaz2.scoreStdev = moves.get(0).scoreStdev;
      }
      isKataData2 = true;
    } else isKataData2 = false;
    isSaiData2 = moves.get(0).isSaiData;
    engineName2 = engName;
    if (isFromLeelaz) {
      if (Lizzie.leelaz2 != null && (Lizzie.leelaz2.isDymPda || Lizzie.leelaz2.pda != 0)) {
        pda2 = Lizzie.leelaz2.pda;
      } else pda2 = 0;
    }
    Collections.sort(
        moves,
        new Comparator<MoveData>() {

          @Override
          public int compare(MoveData s1, MoveData s2) {
            // 降序
            if (s1.order < s2.order) return -1;
            if (s1.order > s2.order) return 1;
            return 0;
          }
        });
    tryToLimitMoves(moves, bestMoves2, false);
    bestMoves2 = moves;
  }

  public static double getWinrateFromBestMoves(List<MoveData> bestMoves) {
    // return the weighted average winrate of bestMoves
    double winrate = 0;
    try {
      winrate = bestMoves.get(0).winrate;
    } catch (Exception e) {
    }
    return winrate;
    //    return bestMoves
    //        .stream()
    //        .mapToDouble(move -> move.winrate * move.playouts / MoveData.getPlayouts(bestMoves))
    //        .sum();
  }

  public static double getScoreLeadFromBestMoves(List<MoveData> bestMoves) {
    // return the weighted average winrate of bestMoves
    double scoreLead = 0;
    try {
      scoreLead = bestMoves.get(0).scoreMean;
    } catch (Exception e) {
    }
    return scoreLead;
  }

  public String bestMovesToString() {
    StringBuilder sb = new StringBuilder();
    int i = 0;
    for (MoveData move : bestMoves) {
      i++;
      if (LizzieFrame.isShareing && i > 10) break;
      // eg: info move R5 visits 38 winrate 5404 pv R5 Q5 R6 S4 Q10 C3 D3 C4 C6 C5 D5
      sb.append("move ").append(move.coordinate);
      sb.append(" visits ").append(move.playouts);
      sb.append(" winrate ").append((int) (move.winrate * 100));
      sb.append(" prior ").append((int) (move.policy * 100));
      if (isKataData)
        sb.append(" scoreMean ").append(String.format(Locale.ENGLISH, "%.2f", move.scoreMean));
      sb.append(" pv ")
          .append(
              move.variation == null
                  ? ""
                  : move.variation.stream().reduce((a, b) -> a + " " + b).get());
      if (isKataData && move.pvVisits != null)
        sb.append(" pvVisits ").append(move.pvVisits.stream().reduce((a, b) -> a + " " + b).get());
      if (i < bestMoves.size())
        sb.append(" info "); // this order is just because of how the MoveData info parser works
    }
    return sb.toString();
  }

  public String bestMovesToString2() {
    StringBuilder sb = new StringBuilder();
    int i = 0;
    for (MoveData move : bestMoves2) {
      i++;
      if (LizzieFrame.isShareing && i > 10) break;
      // eg: info move R5 visits 38 winrate 5404 pv R5 Q5 R6 S4 Q10 C3 D3 C4 C6 C5 D5
      sb.append("move ").append(move.coordinate);
      sb.append(" visits ").append(move.playouts);
      sb.append(" winrate ").append((int) (move.winrate * 100));
      sb.append(" prior ").append((int) (move.policy * 100));
      if (isKataData2) sb.append(" scoreMean ").append(move.scoreMean);
      sb.append(" pv ").append(move.variation.stream().reduce((a, b) -> a + " " + b).get());
      sb.append(" info "); // this order is just because of how the MoveData info parser works
    }
    return sb.toString();
  }

  public void setPlayouts(int playouts) {
    // if (playouts > this.playouts || isChanged) {
    this.playouts = playouts;
    // }
  }

  public void setPlayouts2(int playouts) {
    // if (playouts > this.playouts || isChanged) {
    this.playouts2 = playouts;
    // }
  }

  public void setScoreMean(double scoreMean) {
    // if (playouts > this.playouts || isChanged) {
    this.scoreMean = scoreMean;
    // }
  }

  public void setScoreMean2(double scoreMean) {
    // if (playouts > this.playouts || isChanged) {
    this.scoreMean2 = scoreMean;
    // }
  }

  public void setPlayoutsForce(int playouts) {
    this.playouts = playouts;
  }

  public int getPlayouts() {
    return playouts;
  }

  public int getPlayouts2() {
    return playouts2;
  }

  public void sync(BoardData data) {
    this.moveMNNumber = data.moveMNNumber;
    this.moveNumber = data.moveNumber;
    this.lastMove = data.lastMove;
    this.moveNumberList = data.moveNumberList;
    this.blackToPlay = data.blackToPlay;
    this.dummy = data.dummy;
    this.lastMoveColor = data.lastMoveColor;
    this.stones = data.stones;
    this.zobrist = data.zobrist;
    this.verify = data.verify;
    this.blackCaptures = data.blackCaptures;
    this.whiteCaptures = data.whiteCaptures;
    this.comment = data.comment;
  }

  public BoardData clone() {
    BoardData data = BoardData.empty(19, 19);
    data.sync(this);
    return data;
  }

  public boolean isSameCoord(int[] coord) {
    if (coord == null || coord.length < 2 || !this.lastMove.isPresent()) {
      return false;
    }
    return this.lastMove.map(m -> (m[0] == coord[0] && m[1] == coord[1])).orElse(false);
  }

  public void tryToClearBestMoves() {
    bestMoves = new ArrayList<>();
    playouts = 0;
    if (Lizzie.leelaz.isKatago) {
      Lizzie.leelaz.scoreMean = 0;
      Lizzie.leelaz.scoreStdev = 0;
    }
  }
}
