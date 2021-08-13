package featurecat.lizzie.rules;

import featurecat.lizzie.analysis.Leelaz;

public class EngineCountDown {
  public int leftSeconds;
  public int countDownSeconds;
  public int countDownMoves;
  public int currentLeftSeconds;
  public int currentCountDownSeconds;
  public int currentCountDownMoves;
  public Leelaz engine;
  public boolean isPlayBlack;
  public String color;
  private int tempTimes;

  public void initialize(boolean isPlayBlack) {
    this.isPlayBlack = isPlayBlack;
    color = isPlayBlack ? "B" : "W";
    currentLeftSeconds = leftSeconds;
    currentCountDownSeconds = countDownSeconds;
    currentCountDownMoves = countDownMoves;
  }

  public void sendTimeLeft() {
    if (currentLeftSeconds <= 0) {
      currentCountDownMoves--;
      if (currentCountDownMoves <= 0) {
        currentCountDownMoves = countDownMoves;
        currentCountDownSeconds = countDownSeconds;
      }
      engine.timeLeft(color, currentCountDownSeconds, currentCountDownMoves);
    } else {
      engine.timeLeft(color, currentLeftSeconds, 0);
    }
  }

  public void tempCount() {
    tempTimes++;
    if (tempTimes >= 100) {
      tempTimes = 0;
      if (currentLeftSeconds > 0) {
        currentLeftSeconds--;
      } else if (currentCountDownSeconds > 0) {
        currentCountDownSeconds--;
      }
    }
  }
}
