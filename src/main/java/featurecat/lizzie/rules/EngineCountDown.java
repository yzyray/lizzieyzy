package featurecat.lizzie.rules;

import featurecat.lizzie.Lizzie;
import featurecat.lizzie.analysis.Leelaz;

public class EngineCountDown {
  public boolean isPlayBlack;

  private int countDownMoves;
  private int countDownTimes;

  private int currentCountDownMoves;
  private int currentCountDownTimes;

  private int MainSeconds;
  private int countDownSeconds;

  private int currentMainSeconds;
  private int currentCountDownSeconds;

  private float MainSecondsF;
  private float countDownSecondsF;

  private float currentMainSecondsF;
  private float currentCountDownSecondsF;

  private float fischerIcrementSeconds;
  private float MainSecondsLimit;
  private float MaxSecondsPerMove;

  private Leelaz engine;
  private String color;
  private int tempTimes;
  private TimeType type;

  enum TimeType {
    Canadian_byoyomi,
    kata_None,
    kata_Absolute,
    kata_Canadian_byoyomi,
    kata_Traditional_byoyomi,
    kata_Fisher,
    kata_Fisher_capped,
  }

  public boolean setEngineCountDown(String line, Leelaz engine) {
    line = line.toLowerCase();
    String[] params = line.split(" ");
    int paramsLength = params.length;
    if (paramsLength >= 2) {
      if (params[0].equals("time_settings") && paramsLength == 4) {
        try {
          MainSeconds = Integer.parseInt(params[1]) * 60;
          countDownSeconds = Integer.parseInt(params[2]);
          countDownMoves = Integer.parseInt(params[3]);
          this.engine = engine;
          this.type = TimeType.Canadian_byoyomi;
          return true;
        } catch (Exception e) {
          e.printStackTrace();
          return false;
        }
      } else {
        if (params[0].equals("kata-time_settings")) {
          if (params[1].equals("none") && paramsLength == 2) {
            this.engine = engine;
            this.type = TimeType.kata_None;
            return true;
          }

          if (params[1].equals("absolute") && paramsLength == 3) {
            try {
              MainSecondsF = Float.parseFloat(params[2]) * 60f;
              this.engine = engine;
              this.type = TimeType.kata_Absolute;
              return true;
            } catch (Exception e) {
              e.printStackTrace();
              return false;
            }
          }

          if (params[1].equals("canadian") && paramsLength == 5) {
            try {
              MainSecondsF = Float.parseFloat(params[2]) * 60f;
              countDownSecondsF = Float.parseFloat(params[3]);
              countDownMoves = Integer.parseInt(params[4]);
              this.engine = engine;
              this.type = TimeType.kata_Canadian_byoyomi;
              return true;
            } catch (Exception e) {
              e.printStackTrace();
              return false;
            }
          }

          if (params[1].equals("byoyomi") && paramsLength == 5) {
            try {
              MainSecondsF = Float.parseFloat(params[2]) * 60f;
              countDownSecondsF = Float.parseFloat(params[3]);
              countDownTimes = Integer.parseInt(params[4]);
              this.engine = engine;
              this.type = TimeType.kata_Traditional_byoyomi;
              return true;
            } catch (Exception e) {
              e.printStackTrace();
              return false;
            }
          }

          if (params[1].equals("fischer") && paramsLength == 4) {
            try {
              MainSecondsF = Float.parseFloat(params[2]) * 60f;
              fischerIcrementSeconds = Float.parseFloat(params[3]);
              this.engine = engine;
              this.type = TimeType.kata_Fisher;
              return true;
            } catch (Exception e) {
              e.printStackTrace();
              return false;
            }
          }

          if (params[1].equals("fischer-capped") && paramsLength == 6) {
            try {
              MainSecondsF = Float.parseFloat(params[2]) * 60f;
              fischerIcrementSeconds = Float.parseFloat(params[3]);
              MainSecondsLimit = Float.parseFloat(params[4]);
              MaxSecondsPerMove = Float.parseFloat(params[5]);
              this.engine = engine;
              this.type = TimeType.kata_Fisher_capped;
              return true;
            } catch (Exception e) {
              e.printStackTrace();
              return false;
            }
          }
        }
      }
    }
    return false;
  }

  public void initialize(boolean isPlayBlack) {
    this.isPlayBlack = isPlayBlack;
    color = isPlayBlack ? "B" : "W";
    if (type == TimeType.Canadian_byoyomi) {
      currentMainSeconds = MainSeconds;
      currentCountDownSeconds = countDownSeconds;
      currentCountDownMoves = countDownMoves;
    } else if (type == TimeType.kata_Canadian_byoyomi) {
      currentMainSecondsF = MainSecondsF;
      currentCountDownSecondsF = countDownSecondsF;
      currentCountDownMoves = countDownMoves;
    } else if (type == TimeType.kata_Traditional_byoyomi) {
      currentMainSecondsF = MainSecondsF;
      currentCountDownSecondsF = countDownSecondsF;
      currentCountDownTimes = countDownTimes;
    }
  }

  public void sendTimeLeft() {
    if (type == TimeType.Canadian_byoyomi) {
      if (currentMainSeconds <= 0) {
        currentCountDownMoves--;
        if (currentCountDownMoves <= 0) {
          currentCountDownMoves = countDownMoves;
          currentCountDownSeconds = countDownSeconds;
        }
        engine.timeLeft(color, currentCountDownSeconds, currentCountDownMoves);
      } else {
        engine.timeLeft(color, currentMainSeconds, 0);
      }
    } else if (type == TimeType.kata_Canadian_byoyomi) {
      if (currentMainSecondsF <= 0) {
        currentCountDownMoves--;
        if (currentCountDownMoves <= 0) {
          currentCountDownMoves = countDownMoves;
          currentCountDownSecondsF = countDownSecondsF;
        }
        engine.timeLeft(
            color, String.format("%.2f", currentCountDownSecondsF), currentCountDownMoves);
      } else {
        engine.timeLeft(color, String.format("%.2f", currentMainSecondsF), 0);
      }
    }
  }

  public void countDownCentiseconds() {
    if (type == TimeType.kata_None) {
      Lizzie.engineManager.stopCountDown();
      return;
    } else if (type == TimeType.Canadian_byoyomi) {
      tempTimes++;
      if (tempTimes >= 100) {
        tempTimes = 0;
        if (currentMainSeconds > 0) {
          currentMainSeconds--;
        } else if (currentCountDownSeconds > 0) {
          currentCountDownSeconds--;
        }
      }
    } else if (type == TimeType.kata_Canadian_byoyomi) {
      if (currentMainSecondsF > 0) {
        currentMainSecondsF = currentMainSecondsF - 0.01F;
      } else if (currentCountDownSecondsF > 0) {
        currentCountDownSecondsF = currentCountDownSecondsF - 0.01F;
      }
    }
  }
}
