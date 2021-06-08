package featurecat.lizzie.rules;

import java.util.ArrayList;
import java.util.Optional;

public class MoveLinkedList {
  public boolean isPass;
  public int x;
  public int y;
  public int moveNum;
  public boolean isBlack;
  public boolean needSkip;
  public Optional<MoveLinkedList> previous;
  public ArrayList<MoveLinkedList> variations;

  public MoveLinkedList() {
    previous = Optional.empty();
    variations = new ArrayList<MoveLinkedList>();
  }
}
