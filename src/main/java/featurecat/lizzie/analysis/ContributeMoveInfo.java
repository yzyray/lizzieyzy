package featurecat.lizzie.analysis;

import java.util.ArrayList;
import java.util.List;

public class ContributeMoveInfo {
  public boolean isBlack;
  public boolean isPass;
  public int pos[] = {-1, -1};
  public List<MoveData> candidates;
  public boolean hasOwnership = false;
  public ArrayList<Double> ownershipArray;
}
