package featurecat.lizzie.analysis;

import featurecat.lizzie.Lizzie;
import featurecat.lizzie.gui.SgfWinLossList;
import featurecat.lizzie.rules.Movelist;
import java.util.ArrayList;

public class EngineGameInfo {
  public ArrayList<SgfWinLossList> engineGameSgfWinLoss;
  public boolean isGenmove;
  public boolean isContinueGame;
  public ArrayList<Movelist> continueGameList;
  public int blackEngineIndex; // 当前黑白方
  public int whiteEngineIndex;
  public int firstEngineIndex; // 第一第二引擎
  public int secondEngineIndex;
  public boolean isBatchGame; // 是否批量对局
  public int batchNumber;
  public int batchNumberCurrent;
  public boolean isExchange; // 是否交换黑白

  public int firstEngineWinAsBlack;
  public int firstEngineWinAsWhite;
  public int firstEngineTotlePlayouts;
  public int firstEngineTotleTime;

  public int secondEngineWinAsBlack;
  public int secondEngineWinAsWhite;
  public int secondEngineTotlePlayouts;
  public int secondEngineTotleTime;

  public int timeBlack;
  public int timeWhite;
  public int playoutsBlack;
  public int playoutsWhite;
  public int firstPlayoutsBlack;
  public int firstPlayoutsWhite;

  public int timeFirstEngine;
  public int timeSecondEngine;
  public int playoutsFirstEngine;
  public int playoutsSecondEngine;
  public int firstPlayoutsFirstEngine;
  public int firstPlayoutsSecondEngine;

  public String batchGameName;
  public String SF;

  public int doublePassGame;
  public int maxMoveGame;
  public String settingAll, settingFirst, settingSecond;
  public String resultOther, resultFirst, resultSecond;

  public int blackMinMove;
  public int blackResignMoveCounts;
  public Double blackResignWinrate;

  public int whiteMinMove;
  public int whiteResignMoveCounts;
  public Double whiteResignWinrate;

  public int getFirstEngineWins() {
    return firstEngineWinAsBlack + firstEngineWinAsWhite;
  }

  public int getSecondEngineWins() {
    return secondEngineWinAsBlack + secondEngineWinAsWhite;
  }

  public void exChangeBlackWhite() {

    int temp = blackEngineIndex;
    blackEngineIndex = whiteEngineIndex;
    whiteEngineIndex = temp;
    Lizzie.frame.toolbar.enginePkBlack.setEnabled(true);
    Lizzie.frame.toolbar.enginePkWhite.setEnabled(true);
    Lizzie.frame.toolbar.enginePkBlack.setSelectedIndex(blackEngineIndex);
    Lizzie.frame.toolbar.enginePkWhite.setSelectedIndex(whiteEngineIndex);
    Lizzie.frame.toolbar.enginePkBlack.setEnabled(false);
    Lizzie.frame.toolbar.enginePkWhite.setEnabled(false);
    if (firstEngineIndex == blackEngineIndex)
      Lizzie.frame.toolbar.lblenginePkResult.setText(
          getFirstEngineWins() + ":" + getSecondEngineWins());
    else
      Lizzie.frame.toolbar.lblenginePkResult.setText(
          getSecondEngineWins() + ":" + getFirstEngineWins());

    String temp1 = Lizzie.frame.toolbar.txtenginePkFirstPlayputs.getText();
    Lizzie.frame.toolbar.txtenginePkFirstPlayputs.setText(
        Lizzie.frame.toolbar.txtenginePkFirstPlayputsWhite.getText());
    Lizzie.frame.toolbar.txtenginePkFirstPlayputsWhite.setText(temp1);
    temp1 = Lizzie.frame.toolbar.txtenginePkPlayputs.getText();
    Lizzie.frame.toolbar.txtenginePkPlayputs.setText(
        Lizzie.frame.toolbar.txtenginePkPlayputsWhite.getText());
    Lizzie.frame.toolbar.txtenginePkPlayputsWhite.setText(temp1);
    temp1 = Lizzie.frame.toolbar.txtenginePkTime.getText();
    Lizzie.frame.toolbar.txtenginePkTime.setText(
        Lizzie.frame.toolbar.txtenginePkTimeWhite.getText());
    Lizzie.frame.toolbar.txtenginePkTimeWhite.setText(temp1);

    int temp2 = timeFirstEngine;
    timeFirstEngine = timeSecondEngine;
    timeSecondEngine = temp2;

    temp2 = playoutsBlack;
    playoutsBlack = playoutsWhite;
    playoutsWhite = temp2;

    temp2 = firstPlayoutsBlack;
    firstPlayoutsBlack = firstPlayoutsWhite;
    firstPlayoutsWhite = temp2;

    temp2 = blackMinMove;
    blackMinMove = whiteMinMove;
    whiteMinMove = temp2;

    temp2 = blackResignMoveCounts;
    blackResignMoveCounts = whiteResignMoveCounts;
    whiteResignMoveCounts = temp2;

    Double temp3 = blackResignWinrate;
    blackResignWinrate = whiteResignWinrate;
    whiteResignWinrate = temp3;
  }
}
