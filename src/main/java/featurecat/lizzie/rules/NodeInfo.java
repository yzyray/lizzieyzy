package featurecat.lizzie.rules;

public class NodeInfo {
  public boolean analyzed;
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
  public BoardHistoryNode nextNode;
}
