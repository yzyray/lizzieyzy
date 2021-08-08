package featurecat.lizzie.analysis;

import featurecat.lizzie.Lizzie;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** Holds the data from Leelaz's pondering mode */
public class MoveData {
  public String coordinate;
  public int playouts;
  public double winrate;
  public List<String> variation;
  // 待完成
  public List<String> pvVisits;
  public double lcb;
  // public double oriwinrate;
  public double policy;
  // public int equalplayouts;
  public double scoreMean;
  public double scoreStdev;
  public boolean isKataData;
  public boolean isSaiData;
  public int order;
  public boolean isNextMove;
  public double bestWinrate;
  public double bestScoreMean;
  public boolean lastTimeUnlimited;
  public long lastTimeUnlimitedTime;
  public boolean isSymmetry = false;

  public MoveData() {}

  /**
   * Parses a leelaz ponder output line. For example:
   *
   * <p>0.16 0.15
   *
   * <p>info move R5 visits 38 winrate 5404 order 0 pv R5 Q5 R6 S4 Q10 C3 D3 C4 C6 C5 D5
   *
   * <p>0.17
   *
   * <p>info move Q16 visits 80 winrate 4405 prior 1828 lcb 4379 order 0 pv Q16 D4
   *
   * @param line line of ponder output
   */
  public static MoveData fromInfo(String line) throws ArrayIndexOutOfBoundsException {
    MoveData result = new MoveData();
    String[] data = line.trim().split(" ");
    // int k =
    // Lizzie.config.config.getJSONObject("leelaz").getInt("max-suggestion-moves");
    //    boolean islcb =
    //        (Lizzie.config.leelaversion >= 17 && Lizzie.config.showlcbwinrate &&
    // !Lizzie.leelaz.noLcb);
    // Todo: Proper tag parsing in case gtp protocol is extended(?)/changed
    for (int i = 0; i < data.length; i++) {
      String key = data[i];
      if (key.equals("pv")) {
        // Read variation to the end of line
        result.variation = new ArrayList<>(Arrays.asList(data));
        result.variation =
            result.variation.subList(
                i + 1,
                (Lizzie.config.limitBranchLength > 0
                        && data.length - i - 1 > Lizzie.config.limitBranchLength)
                    ? i + 1 + Lizzie.config.limitBranchLength
                    : data.length);
        // result.variation = result.variation.subList(i + 1, data.length);
        break;
      } else {
        String value = data[++i];
        if (key.equals("order")) {
          result.order = Integer.parseInt(value);
        }
        if (key.equals("move")) {
          result.coordinate = value;
        }
        if (key.equals("visits")) {
          result.playouts = Integer.parseInt(value);
        }
        if (key.equals("lcb")) {
          // LCB support
          result.lcb = Integer.parseInt(value) / 100.0;
          //          if (islcb) {
          //            result.winrate = Integer.parseInt(value) / 100.0;
          //          }
        }
        if (key.equals("prior")) {
          result.policy = Integer.parseInt(value) / 100.0;
        }

        if (key.equals("winrate")) {
          // support 0.16 0.15
          result.winrate = Integer.parseInt(value) / 100.0;
          // result.oriwinrate = result.winrate;
          //          if (!islcb) {
          //            result.winrate = Integer.parseInt(value) / 100.0;
          //          }
        }
      }
    }
    result.isKataData = false;
    result.isSaiData = false;
    return result;
  }

  //  Best:J3...   Nodes collected {8}: 800055 -> 569197 (0.00752s)
  //  public static MoveData fromInfoSpec(String line) throws ArrayIndexOutOfBoundsException {
  //    MoveData result = new MoveData();
  //    String[] data = line.trim().split("Best:");
  //    String[] data2 = data[1].split("\\.\\.\\.");
  //    result.coordinate = data2[0];
  //    result.winrate = 50.0;
  //    result.oriwinrate = 50.0;
  //    result.playouts = -999;
  //
  //    return result;
  //  }

  public static MoveData fromInfoSai(String line) throws ArrayIndexOutOfBoundsException {
    MoveData result = new MoveData();
    String[] data = line.trim().split(" ");
    // int k =
    // Lizzie.config.config.getJSONObject("leelaz").getInt("max-suggestion-moves");
    //    boolean islcb =
    //        (Lizzie.config.leelaversion >= 17 && Lizzie.config.showlcbwinrate &&
    // !Lizzie.leelaz.noLcb);
    // Todo: Proper tag parsing in case gtp protocol is extended(?)/changed
    for (int i = 0; i < data.length; i++) {
      String key = data[i];
      if (key.equals("pv")) {
        // Read variation to the end of line
        result.variation = new ArrayList<>(Arrays.asList(data));
        result.variation =
            result.variation.subList(
                i + 1,
                (Lizzie.config.limitBranchLength > 0
                        && data.length - i - 1 > Lizzie.config.limitBranchLength)
                    ? i + 1 + Lizzie.config.limitBranchLength
                    : data.length);
        // result.variation = result.variation.subList(i + 1, data.length);
        break;
      } else {
        String value = data[++i];
        if (key.equals("order")) {
          result.order = Integer.parseInt(value);
        }
        if (key.equals("move")) {
          result.coordinate = value;
        }
        if (key.equals("visits")) {
          result.playouts = Integer.parseInt(value);
        }
        if (key.equals("lcb")) {
          // LCB support
          result.lcb = Integer.parseInt(value) / 100.0;
          //          if (islcb) {
          //            result.winrate = Integer.parseInt(value) / 100.0;
          //          }
        }
        if (key.equals("prior")) {
          result.policy = Integer.parseInt(value) / 100.0;
        }
        if (key.equals("areas")) {
          result.scoreMean =
              Lizzie.board.getHistory().isBlacksTurn()
                  ? result.scoreMean = Integer.parseInt(value) / 10000.0
                  : -Integer.parseInt(value) / 10000.0;
        }
        if (key.equals("winrate")) {
          // support 0.16 0.15
          result.winrate = Integer.parseInt(value) / 100.0;
          // result.oriwinrate = result.winrate;
          //          if (!islcb) {
          //            result.winrate = Integer.parseInt(value) / 100.0;
          //          }
        }
      }
    }
    result.isKataData = true;
    result.isSaiData = true;
    return result;
  }

  public static MoveData fromInfoKatago(String line) throws ArrayIndexOutOfBoundsException {
    MoveData result = new MoveData();
    String[] data = line.trim().split(" ");
    //    boolean islcb =
    //        (Lizzie.config.leelaversion >= 17 && Lizzie.config.showlcbwinrate &&
    // !Lizzie.leelaz.noLcb);
    // Todo: Proper tag parsing in case gtp protocol is extended(?)/changed
    for (int i = 0; i < data.length; i++) {
      String key = data[i];
      if (key.equals("pv")) {
        int pvVisitsPos = -1;
        for (int s = i + 1; s < data.length; s++) {
          String subKey = data[s];
          if (subKey.equals("pvVisits")) {
            pvVisitsPos = s;
            result.pvVisits = new ArrayList<>(Arrays.asList(data));
            result.pvVisits = result.pvVisits.subList(s + 1, data.length);
            break;
          }
        }
        // Read variation to the end of line
        result.variation = new ArrayList<>(Arrays.asList(data));
        int length = pvVisitsPos > -1 ? pvVisitsPos : data.length;
        result.variation =
            result.variation.subList(
                i + 1,
                (Lizzie.config.limitBranchLength > 0
                        && length - i - 1 > Lizzie.config.limitBranchLength)
                    ? i + 1 + Lizzie.config.limitBranchLength
                    : length);
        if (result.pvVisits != null) {
          if (result.pvVisits.size() > result.variation.size())
            result.pvVisits = result.pvVisits.subList(0, result.variation.size());
        }
        // result.variation = result.variation.subList(i + 1, data.length);
        break;
      } else {
        String value = data[++i];
        if (key.equals("order")) {
          result.order = Integer.parseInt(value);
        }
        if (key.equals("move")) {
          result.coordinate = value;
        }
        if (key.equals("visits")) {
          result.playouts = Integer.parseInt(value);
        }
        if (key.equals("lcb")) {
          // LCB support
          result.lcb = Double.parseDouble(value) * 100;
          //          if (islcb) {
          //            result.winrate = Double.parseDouble(value) * 100;
          //          }
        }
        if (key.equals("prior")) {
          result.policy = Double.parseDouble(value) * 100;
        }
        if (key.equals("winrate")) {
          // support 0.16 0.15
          result.winrate = Double.parseDouble(value) * 100;
          // result.oriwinrate = result.winrate;
        }
        //        if (key.equals("scoreLead")) {
        //          result.scoreMean = Double.parseDouble(value);
        //        }
        if (key.equals("scoreMean")) {
          result.scoreMean = Double.parseDouble(value);
        }
        if (key.equals("scoreStdev")) {
          result.scoreStdev = Double.parseDouble(value);
        }
        if (key.equals("isSymmetryOf")) {
          result.isSymmetry = true;
        }
      }
    }
    // if (result.winrate < 0) result.winrate = result.oriwinrate;
    result.isKataData = true;
    result.isSaiData = false;
    return result;
  }

  public static MoveData fromInfofromfile(String line, List<MoveData> bestMoves)
      throws ArrayIndexOutOfBoundsException {
    MoveData result = new MoveData();
    String[] data = line.trim().split(" ");

    // Todo: Proper tag parsing in case gtp protocol is extended(?)/changed
    for (int i = 0; i < data.length; i++) {
      String key = data[i];
      //      if (key.equals("pv")) {
      //        // Read variation to the end of line
      //        result.variation = new ArrayList<>(Arrays.asList(data));
      //        result.variation = result.variation.subList(i + 1, data.length);
      //        break;
      //      }
      if (key.equals("pv")) {
        int pvVisitsPos = -1;
        for (int s = i + 1; s < data.length; s++) {
          String subKey = data[s];
          if (subKey.equals("pvVisits")) {
            pvVisitsPos = s;
            result.pvVisits = new ArrayList<>(Arrays.asList(data));
            result.pvVisits = result.pvVisits.subList(s + 1, data.length);
            break;
          }
        }
        // Read variation to the end of line
        result.variation = new ArrayList<>(Arrays.asList(data));
        int length = pvVisitsPos > -1 ? pvVisitsPos : data.length;
        result.variation =
            result.variation.subList(
                i + 1,
                (Lizzie.config.limitBranchLength > 0
                        && length - i - 1 > Lizzie.config.limitBranchLength)
                    ? i + 1 + Lizzie.config.limitBranchLength
                    : length);
        // result.variation = result.variation.subList(i + 1, data.length);
        break;
      } else {
        String value = data[++i];
        if (key.equals("move")) {
          result.coordinate = value;
        }
        if (key.equals("visits")) {
          result.playouts = Integer.parseInt(value);
        }
        if (key.equals("winrate")) {
          // support 0.16 0.15
          result.winrate = Integer.parseInt(value) / 100.0;
          // result.oriwinrate = result.winrate;
          result.lcb = result.winrate;
        }
        if (key.equals("prior")) {
          try {
            result.policy = Integer.parseInt(value) / 100.0;
          } catch (NumberFormatException err) {
            result.policy = Double.parseDouble(value);
          }
        }
        if (key.equals("scoreMean")) {
          // support 0.16 0.15
          result.scoreMean = Double.parseDouble(value);
          Lizzie.board.isKataBoard = true;
          result.isKataData = true;
        }
      }
    }
    result.order = bestMoves.size();
    return result;
  }

  public static MoveData fromSummaryKata(String summary) {
    if (summary.contains("=")) {
      summary = summary.trim().split("=")[0] + summary.trim().split("=")[1];
    }
    summary = summary.substring(5);
    // boolean hasPda = summary.contains("PDA");
    String[] params = summary.trim().split("PV");
    MoveData result = new MoveData();
    if (params.length <= 2) {
      String[] params2 = params[0].trim().split(" ");
      if (params2.length >= 8) {
        result.isKataData = true;
        Lizzie.board.isKataBoard = true;
        result.playouts = Integer.parseInt(params2[1]);
        result.winrate = Double.parseDouble(params2[3].replace("%", ""));
        result.scoreMean = Double.parseDouble(params2[5]);
        result.scoreStdev = Double.parseDouble(params2[7]);
      }
      if (params.length == 2) {
        result.variation =
            Arrays.asList(params[1].trim().split(" ", Lizzie.config.limitBranchLength));
        result.coordinate = result.variation.get(0);
      } else {
        result.coordinate = "A1";
        result.variation = Arrays.asList("A1");
      }
    }

    //	      result.coordinate = match.group(1);
    //	      result.playouts = Integer.parseInt(match.group(2));
    //	      result.winrate = Double.parseDouble(match.group(Lizzie.config.showlcbwinrate ? 4 : 3));
    //	      result.variation = Arrays.asList(match.group(5).split(" ",
    // Lizzie.config.limitBranchLength));
    // result.variation = Arrays.asList(match.group(5).split(" "));
    return result;
  }

  /**
   * Parses a leelaz summary output line. For example:
   *
   * <p>0.15 0.16
   *
   * <p>P16 -> 4 (V: 50.94%) (N: 5.79%) PV: P16 N18 R5 Q5 D4 -> 1393 (V: 51.16%) (N: 58.90%) PV: D4
   * D17 Q4 C6 F3 C12 K17 O17 G17 F16 E18 G16 E17 E16 H17 D18 D16 E19 F17 D15 C16 B17 B16 C17
   *
   * <p>0.17
   *
   * <p>Q4 -> 4348 (V: 43.88%) (LCB: 43.81%) (N: 18.67%) PV: Q4 D16 D4 Q16 R14 R6 C1
   *
   * @param summary line of summary output
   */
  public static MoveData fromSummary(String summary) {
    Matcher match = summaryPattern.matcher(summary.trim());
    if (!match.matches()) {
      Matcher matchold = summaryPatternold.matcher(summary.trim());
      if (!matchold.matches()) {
        Lizzie.gtpConsole.addLine("Summary err");
        return null;
        // throw new IllegalArgumentException("Unexpected summary format: " + summary);
      } else {
        MoveData result = new MoveData();
        result.coordinate = matchold.group(1);
        result.playouts = Integer.parseInt(matchold.group(2));
        result.winrate = Double.parseDouble(matchold.group(3));
        result.variation =
            Arrays.asList(matchold.group(4).split(" ", Lizzie.config.limitBranchLength));
        // result.variation = Arrays.asList(matchold.group(4).split(" "));
        return result;
      }
    } else {
      MoveData result = new MoveData();
      result.coordinate = match.group(1);
      result.playouts = Integer.parseInt(match.group(2));
      result.winrate = Double.parseDouble(match.group(3));
      result.variation = Arrays.asList(match.group(5).split(" ", Lizzie.config.limitBranchLength));
      // result.variation = Arrays.asList(match.group(5).split(" "));
      return result;
    }
  }

  public static MoveData fromSummaryLeela0110(String summary) {

    // support 0.16 0.15
    Pattern oldPattern = summaryPatternLeela0110;
    Matcher matchold = oldPattern.matcher(summary.trim());
    if (!matchold.matches()) {
      throw new IllegalArgumentException("Unexpected summary format: " + summary);
    } else {
      MoveData result = new MoveData();
      result.coordinate = matchold.group(1);
      result.playouts = Integer.parseInt(matchold.group(2));
      result.winrate = Double.parseDouble(matchold.group(3));
      // result.oriwinrate = result.winrate;
      result.policy = Double.parseDouble(matchold.group(4));
      result.variation =
          Arrays.asList(matchold.group(5).split(" ", Lizzie.config.limitBranchLength));
      return result;
    }
  }

  public static MoveData fromSummarySai(String summary) {
    Matcher match = summaryPatternSai.matcher(summary.trim());
    if (match.matches()) {
      MoveData result = new MoveData();
      result.coordinate = match.group(1);
      result.playouts = Integer.parseInt(match.group(2));
      result.winrate = Double.parseDouble(match.group(3));
      result.scoreMean =
          Lizzie.board.getHistory().isBlacksTurn()
              ? Double.parseDouble(match.group(5))
              : -Double.parseDouble(match.group(5));
      result.variation = Arrays.asList(match.group(6).split(" ", Lizzie.config.limitBranchLength));
      result.isKataData = true;
      result.isSaiData = true;
      return result;
    } else {
      Lizzie.gtpConsole.addLine("Summary err");
      return null;
    }
  }
  // C15  ->      718, 47.87%, C15 O17 R14 Q18 R6 O3 M17 R17 P17 P18
  public static MoveData fromSummaryZen(String summary) {
    String[] params = summary.trim().split(",");

    if (params.length == 3) {
      MoveData result = new MoveData();
      String[] params1 = params[0].trim().split("->");
      if (params1.length == 2) {
        result.coordinate = params1[0].trim();
        result.playouts = Integer.parseInt(params1[1].trim());
      }
      String wr = params[1].trim();
      result.winrate = Double.parseDouble(wr.substring(0, wr.length() - 1));
      result.variation =
          Arrays.asList(params[2].trim().split(" ", Lizzie.config.limitBranchLength));
      return result;
    } else return null;
  }

  private static Pattern summaryPatternLeela0110 =
      Pattern.compile(
          "^ *(\\w\\d*) -> *(\\d+) \\(W: ([^%)]+)%\\).* \\(N: ([^%)]+)%\\) PV: (.+).*$");

  private static Pattern summaryPattern =
      Pattern.compile(
          "^ *(\\w\\d*) -> *(\\d+) \\(V: ([^%)]+)%\\) \\(LCB: ([^%)]+)%\\) \\([^\\)]+\\) PV: (.+).*$");
  private static Pattern summaryPatternold =
      Pattern.compile("^ *(\\w\\d*) -> *(\\d+) \\(V: ([^%)]+)%\\) \\([^\\)]+\\) PV: (.+).*$");

  private static Pattern summaryPatternSai =
      Pattern.compile(
          "^ *(\\w\\d*) -> *(\\d+) \\(V: ([^%)]+)%\\) \\(LCB: ([^%)]+)%\\) \\([^\\)]+\\) \\(A: ([^)]+)\\) PV: (.+).*$");

  // support 0.16 0.15
  private static Pattern summaryPatternhandicap =
      Pattern.compile("^ *(\\w\\d*) ->    *(\\d+) \\(V: ([^%)]+)%\\) \\([^\\)]+\\) PV: (.+).* $");

  public static int getPlayouts(List<MoveData> moves) {
    int playouts = 0;
    for (MoveData move : moves) {
      if (move.isSymmetry) continue;
      playouts += move.playouts;
    }
    return playouts;
  }

  public static Comparator policyComparator =
      new Comparator() {
        @Override
        public int compare(Object o1, Object o2) {
          MoveData e1 = (MoveData) o1;
          MoveData e2 = (MoveData) o2;
          if (e1.policy > e2.policy) return 1;
          if (e1.policy < e2.policy) return -1;
          else return 0;
        }
      };
}
