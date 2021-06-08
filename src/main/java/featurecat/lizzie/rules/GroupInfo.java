package featurecat.lizzie.rules;

import featurecat.lizzie.gui.ScoreResult;

public class GroupInfo {
  public GroupStatus[][] groupStatus;
  Stone[] oriStones;
  // public GroupStatus[][] markedStatus;
  public int maxGoupIndex = -1;
  public boolean groupHasNextB = false;
  public boolean groupHasNextW = false;
  public ScoreResult scoreResult;
}
