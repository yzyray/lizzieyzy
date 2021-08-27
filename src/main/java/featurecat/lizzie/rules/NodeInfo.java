package featurecat.lizzie.rules;

public class NodeInfo {
  public boolean analyzed;
  public boolean analyzedMatchValue;
  public boolean changed;
  public boolean changed2;
  public int[] coords;
  public int moveNum;
  public boolean isBlack;
  public double winrate;
  public double diffWinrate;
  public int playouts;
  public int previousPlayouts;
  public double scoreMeanDiff;
  public double scoreMeanBoard;
  public boolean isMatchAi;
  public double percentsMatch;
  public boolean isBest;
  public BoardHistoryNode nextNode;

  public double getScoreMeanDiff() {
    if (this.isBest) return 0;
    else return scoreMeanDiff;
  }

  public double getWinrateDiff() {
    if (this.isBest) return 0;
    else return diffWinrate;
  }
}
