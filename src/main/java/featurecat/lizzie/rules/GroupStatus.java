package featurecat.lizzie.rules;

public class GroupStatus {
  public int value = 0; // 0=空 1=黑 2=白
  public boolean isMarkedEmpty = false;
  public boolean isMarkedDead = false;
  public boolean hasCalculated = false;
  public int gourpIndex = -1;
  // public int nextValue = -1; // <0=无 1=黑 2=白 3=黑+白
}
