package featurecat.lizzie.analysis;

import java.util.ArrayList;
import org.json.JSONObject;

public class ContributeGameInfo {
  public String gameId;
  public String blackPlayer;
  public String whitePlayer;
  public int sizeX;
  public int sizeY;
  public double komi;
  public JSONObject rules;
  public ArrayList<ContributeMoveInfo> initMoveList;
  public ArrayList<ContributeMoveInfo> moveList;
  public boolean isWatching;
  public boolean complete = false;
  public String gameResult = "";
  public boolean isMatchGame = false;
}
