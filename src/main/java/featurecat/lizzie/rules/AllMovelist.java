package featurecat.lizzie.rules;

import featurecat.lizzie.Lizzie;
import java.util.ArrayList;

public class AllMovelist {
  public boolean ispass;
  public int x;
  public int y;
  public boolean isblack;
  public AllMovelist previous;
  public boolean currentPosition = false;
  public String comment;

  public ArrayList<AllMovelist> variations = new ArrayList<AllMovelist>();
  //  public boolean isMain() {
  //	  AllMovelist node = this;
  //	    while (node.previous!=null) {
  //	      AllMovelist pre = node.previous;
  //	      if (!pre.variations.isEmpty() && pre.variations.get(0) != node) {
  //	        return false;
  //	      }
  //	      node = pre;
  //	    }
  //	    return true;
  //  }
  public void playNode() {
    AllMovelist node = this;
    if (node.ispass)
      Lizzie.board
          .getHistory()
          .pass(Lizzie.board.getHistory().isBlacksTurn() ? Stone.BLACK : Stone.WHITE, false, false);
    else Lizzie.board.getHistory().place(node.x, node.y, node.isblack ? Stone.BLACK : Stone.WHITE);
  }
}
