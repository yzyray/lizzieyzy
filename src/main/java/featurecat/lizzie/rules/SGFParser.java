package featurecat.lizzie.rules;

import static java.util.Arrays.asList;

import featurecat.lizzie.Lizzie;
import featurecat.lizzie.analysis.EngineManager;
import featurecat.lizzie.analysis.GameInfo;
import featurecat.lizzie.analysis.MoveData;
import featurecat.lizzie.gui.LizzieFrame;
import featurecat.lizzie.util.EncodingDetector;
import featurecat.lizzie.util.Utils;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

public class SGFParser {
  private static final SimpleDateFormat SGF_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
  private static final String alphabet = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
  private static final String[] listProps =
      new String[] {"LB", "CR", "SQ", "MA", "TR", "AB", "AW", "AE"};
  private static final String[] markupProps = new String[] {"LB", "CR", "SQ", "MA", "TR"};
  private static String[] lines;
  private static String[] line1;
  private static boolean islzFirst = false;
  private static boolean islzFirst2 = true;
  private static boolean islzloaded = false;

  private static boolean islz2First = false;
  private static boolean islz2First2 = true;
  private static boolean islz2loaded = false;

  private static boolean isExtraMode2 = false;
  // static boolean oriEmpty = false;

  public static boolean load(String filename, boolean showHint) throws IOException {
    // Clear the board
    islzFirst = false;
    islzFirst2 = true;
    islzloaded = false;
    isExtraMode2 = false;
    Lizzie.board.isLoadingFile = true;
    Lizzie.board.clear(false);
    File file = new File(filename);
    if (!file.exists() || !file.canRead()) {
      return false;
    }

    String encoding = EncodingDetector.detect(filename);
    FileInputStream fp = new FileInputStream(file);
    if (encoding == "WINDOWS-1252") encoding = "GB18030";
    InputStreamReader reader = new InputStreamReader(fp, encoding);
    StringBuilder builder = new StringBuilder();
    while (reader.ready()) {
      builder.append((char) reader.read());
    }
    reader.close();
    fp.close();
    String value = builder.toString();
    if (value.isEmpty()) {
      Lizzie.board.isLoadingFile = false;
      return false;
    }

    boolean returnValue = parse(value);
    Lizzie.board.isLoadingFile = false;
    if (Lizzie.board.hasStartStone) {
      int lenth2 = Lizzie.board.startStonelist.size();
      for (int i = 0; i < lenth2; i++) {
        Movelist move = Lizzie.board.startStonelist.get(lenth2 - 1 - i);
        String color = move.isblack ? "b" : "w";
        if (!move.ispass) {
          Lizzie.leelaz.sendCommand(
              "play " + color + " " + Board.convertCoordinatesToName(move.x, move.y));
        }
      }
    }
    if (isExtraMode2 && LizzieFrame.extraMode != 2 && !Lizzie.config.isAutoAna && showHint) {
      SwingUtilities.invokeLater(
          new Runnable() {
            public void run() {
              if (Lizzie.config.loadSgfLast) while (Lizzie.board.nextMove(false)) ;
              Lizzie.board.clearAfterMove();
              if (showHint) Lizzie.frame.refresh();
              int ret =
                  JOptionPane.showConfirmDialog(
                      Lizzie.frame,
                      Lizzie.resourceBundle.getString("SGFParse.doubleEngineHint"),
                      Lizzie.resourceBundle.getString("SGFParse.doubleEngineHintTitle"),
                      JOptionPane.OK_CANCEL_OPTION);
              if (ret == JOptionPane.OK_OPTION) {
                Lizzie.config.toggleExtraMode(2);
              }
            }
          });
    }
    return returnValue;
  }

  public static boolean loadFromString(String sgfString) {
    // Clear the board
    Lizzie.board.clear(false);
    isExtraMode2 = false;
    Lizzie.board.isLoadingFile = true;
    boolean result = parse(sgfString);
    if (Lizzie.board.hasStartStone) {
      int lenth2 = Lizzie.board.startStonelist.size();
      for (int i = 0; i < lenth2; i++) {
        Movelist move = Lizzie.board.startStonelist.get(lenth2 - 1 - i);
        String color = move.isblack ? "b" : "w";
        if (!move.ispass) {
          Lizzie.leelaz.sendCommand(
              "play " + color + " " + Board.convertCoordinatesToName(move.x, move.y));
        }
      }
    }
    Lizzie.board.isLoadingFile = false;
    if (isExtraMode2 && LizzieFrame.extraMode != 2 && !Lizzie.config.isAutoAna) {
      int ret =
          JOptionPane.showConfirmDialog(
              Lizzie.frame,
              Lizzie.resourceBundle.getString("SGFParse.doubleEngineHint"),
              Lizzie.resourceBundle.getString("SGFParse.doubleEngineHintTitle"),
              JOptionPane.OK_CANCEL_OPTION);
      if (ret == JOptionPane.OK_OPTION) {
        Lizzie.config.toggleExtraMode(2);
      }
    }
    return result;
  }

  public static boolean loadFromStringforedit(String sgfString) {
    // Clear the board
    Lizzie.board.clearforedit();
    Lizzie.board.hasStartStone = false;
    Lizzie.board.startStonelist = new ArrayList<Movelist>();
    Lizzie.board.isLoadingFile = true;
    boolean result = parse(sgfString);
    Lizzie.board.isLoadingFile = false;
    return result;
  }

  public static String passPos() {
    return (Board.boardWidth <= 51 && Board.boardHeight <= 51)
        ? String.format(
            "%c%c", alphabet.charAt(Board.boardWidth), alphabet.charAt(Board.boardHeight))
        : "";
  }

  public static boolean isPassPos(String pos) {
    // TODO
    String passPos = passPos();
    return pos.isEmpty() || passPos.equals(pos);
  }

  public static int[] convertSgfPosToCoord(String pos) {
    if (pos.length() < 2) return null;
    if (isPassPos(pos)) return null;
    //  int[]
    int[] ret = new int[2];
    ret[0] = alphabet.indexOf(pos.charAt(0));
    ret[1] = alphabet.indexOf(pos.charAt(1));
    return ret;
  }

  private static void saveLz(String[] liness, String[] line1s) {
    lines = liness;
    line1 = line1s;
    islzloaded = true;
  }

  private static void saveLz2(String[] liness, String[] line1s) {
    islz2loaded = true;
  }

  private static void loadLz() {
    String line2 = "";
    if (lines.length > 1) {
      line2 = lines[1];
    }
    //  String versionNumber = line1[0];
    String engname = line1[0];
    Lizzie.board.getData().engineName = engname;
    Lizzie.board.getData().winrate = 100 - Double.parseDouble(line1[1]);
    int numPlayouts =
        Integer.parseInt(
            line1[2].replaceAll("k", "00").replaceAll("m", "00000").replaceAll("[^0-9]", ""));
    if (line1.length >= 4) {
      double scoreMean = Double.parseDouble(line1[3]);
      Lizzie.board.getData().setScoreMean(scoreMean);
      Lizzie.board.getData().isKataData = true;
      if (line1.length >= 5) {
        double scoreStdev = Double.parseDouble(line1[4]);
        Lizzie.board.getData().scoreStdev = scoreStdev;
      }
      if (line1.length >= 6) {
        double pda = Double.parseDouble(line1[5]);
        Lizzie.board.getData().pda = pda;
        // Lizzie.board.getData().isPDA = true;
      }
    }
    if (numPlayouts > 0 && !line2.isEmpty()) {
      parseInfofromfile(line2);
    }
    islzloaded = false;
  }

  private static boolean parse(String value) {
    // Drop anything outside "(;...)"
    boolean oriPlaySound = Lizzie.config.playSound;
    Lizzie.config.playSound = false;
    final Pattern SGF_PATTERN = Pattern.compile("(?s).*?(\\(\\s*;{0,1}.*\\))(?s).*?");
    Matcher sgfMatcher = SGF_PATTERN.matcher(value);
    if (sgfMatcher.matches()) {
      value = sgfMatcher.group(1);
    } else {
      value = "(;" + value.substring(1);
      Matcher sgfMatcher2 = SGF_PATTERN.matcher(value);
      if (sgfMatcher2.matches()) {
        value = sgfMatcher2.group(1);
      } else {
        return false;
      }
    }

    // Determine the SZ property
    Pattern szPattern = Pattern.compile("(?s).*?SZ\\[([\\d:]+)\\](?s).*");
    Matcher szMatcher = szPattern.matcher(value);
    if (szMatcher.matches()) {
      String sizeStr = szMatcher.group(1);
      Pattern sizePattern = Pattern.compile("([\\d]+):([\\d]+)");
      Matcher sizeMatcher = sizePattern.matcher(sizeStr);
      if (sizeMatcher.matches()) {
        Lizzie.board.reopen(
            Integer.parseInt(sizeMatcher.group(1)), Integer.parseInt(sizeMatcher.group(2)));
      } else {
        int boardSize = Integer.parseInt(sizeStr);
        Lizzie.board.reopen(boardSize, boardSize);
      }
    } else {
      Lizzie.board.reopen(19, 19);
    }

    int subTreeDepth = 0;
    // Save the variation step count
    Map<Integer, BoardHistoryNode> subTreeStepMap = new HashMap<Integer, BoardHistoryNode>();
    // Comment of the game head
    String headComment = "";
    // Game properties
    Map<String, String> gameProperties = new HashMap<String, String>();
    Map<String, String> pendingProps = new HashMap<String, String>();
    boolean inTag = false,
        isMultiGo = false,
        escaping = false,
        moveStart = false,
        startNewBranch = true;
    boolean inProp = false;

    String tag = "";
    StringBuilder tagBuilder = new StringBuilder();
    StringBuilder tagContentBuilder = new StringBuilder();
    // MultiGo 's branch: (Main Branch (Main Branch) (Branch) )
    // Other 's branch: (Main Branch (Branch) Main Branch)
    if (value.matches("(?s).*\\)\\s*\\)")) {
      isMultiGo = true;
    }

    String blackPlayer = "", whitePlayer = "";
    String result = "";
    // Support unicode characters (UTF-8)
    int len = value.length();
    boolean shouldProcessDummy = false;
    for (int i = 0; i < len; i++) {
      char c = value.charAt(i);
      if (escaping) {
        // Any char following "\" is inserted verbatim
        // (ref) "3.2. Text" in https://www.red-bean.com/sgf/sgf4.html
        tagContentBuilder.append(c == 'n' ? "\n" : c);
        escaping = false;
        continue;
      }
      switch (c) {
        case '(':
          if (!inTag) {
            subTreeDepth += 1;
            // Initialize the step count
            subTreeStepMap.put(subTreeDepth, Lizzie.board.getHistory().getCurrentHistoryNode());
            startNewBranch = true;
            pendingProps = new HashMap<String, String>();
          } else {
            if (i > 0) {
              // Allow the comment tag includes '('
              tagContentBuilder.append(c);
            }
          }
          break;
        case ')':
          if (!inTag) {
            if (isMultiGo) {
              // Restore to the variation node
              // int varStep = subTreeStepMap.get(subTreeDepth);
              BoardHistoryNode node = subTreeStepMap.get(subTreeDepth);
              //  for (int s = 0; s < varStep; s++)
              while (Lizzie.board.getHistory().getCurrentHistoryNode() != node) {
                if (!Lizzie.board.getHistory().previous().isPresent()) break;
              }
              //  System.out.println(subTreeDepth+" | "+varStep);
            }
            subTreeDepth -= 1;
          } else {
            // Allow the comment tag includes '('
            tagContentBuilder.append(c);
          }
          break;
        case '[':
          if (!inProp) {
            inProp = true;
            if (subTreeDepth > 1 && !isMultiGo) {
              break;
            }
            inTag = true;
            String tagTemp = tagBuilder.toString();
            if (!tagTemp.isEmpty()) {
              // Ignore small letters in tags for the long format Smart-Go file.
              // (ex) "PlayerBlack" ==> "PB"
              // It is the default format of mgt, an old SGF tool.
              // (Mgt is still supported in Debian and Ubuntu.)
              tag = tagTemp.replaceAll("[a-z]", "");
            }
            tagContentBuilder = new StringBuilder();
          } else {
            tagContentBuilder.append(c);
          }
          break;
        case ']':
          if (subTreeDepth > 1 && !isMultiGo) {
            break;
          }
          inTag = false;
          inProp = false;
          tagBuilder = new StringBuilder();
          String tagContent = tagContentBuilder.toString();
          // We got tag, we can parse this tag now.
          if (tag.equals("DD")) {
            if (tagContent.equals("true")) {
              shouldProcessDummy = true;
            }
          } else if (tag.equals("B") || tag.equals("W")) {

            moveStart = true;
            startNewBranch = true;
            int[] move = convertSgfPosToCoord(tagContent);
            // Save the step count
            //  subTreeStepMap.put(subTreeDepth, subTreeStepMap.get(subTreeDepth) + 1);
            Stone color = tag.equals("B") ? Stone.BLACK : Stone.WHITE;
            boolean newBranch =
                Lizzie.board
                    .getHistory()
                    .getCurrentHistoryNode()
                    .hasVariations(); // (subTreeStepMap.get(subTreeDepth) == 1);
            if (move == null) {
              if (shouldProcessDummy) {
                shouldProcessDummy = false;
                Stone colors =
                    Lizzie.board.getHistory().getLastMoveColor().isBlack()
                        ? Stone.WHITE
                        : Stone.BLACK;
                Lizzie.board.getHistory().pass(colors, false, false);
                Lizzie.board.getHistory().previous();
                Lizzie.board.getHistory().pass(color, newBranch, false);
                Lizzie.board
                        .getHistory()
                        .getCurrentHistoryNode()
                        .previous()
                        .get()
                        .next()
                        .get()
                        .getData()
                        .dummy =
                    true;
              } else Lizzie.board.getHistory().pass(color, newBranch, false);
            } else {
              if (shouldProcessDummy) {
                shouldProcessDummy = false;
                Stone colors =
                    Lizzie.board.getHistory().getLastMoveColor().isBlack()
                        ? Stone.WHITE
                        : Stone.BLACK;
                Lizzie.board.getHistory().pass(colors, false, false);
                Lizzie.board.getHistory().previous();
                Lizzie.board.getHistory().place(move[0], move[1], color, newBranch);
                Lizzie.board
                        .getHistory()
                        .getCurrentHistoryNode()
                        .previous()
                        .get()
                        .next()
                        .get()
                        .getData()
                        .dummy =
                    true;
              } else Lizzie.board.getHistory().place(move[0], move[1], color, newBranch);
            }
            if (newBranch) {
              processPendingPros(Lizzie.board.getHistory(), pendingProps);
            }
            if (islzFirst) {
              if (islzloaded) {
                loadLz();
              }
            }
            if (islz2First) {
              if (islz2loaded) {
                loadLz();
              }
            }
          } else if (tag.equals("C")) {
            // Support comment
            if (!moveStart) {
              headComment = tagContent;
            } else {
              Lizzie.board.comment(tagContent);
            }
          } else if (tag.equals("LZ")) {
            // Content contains data for Lizzie to read
            if (islzFirst2) {
              if (Lizzie.board.getData().moveNumber < 1) {
                islzFirst = true;
              }
              islzFirst2 = false;
            }
            if (islzFirst) {
              String[] lines = tagContent.split("\n");
              String[] line1 = lines[0].split(" ");
              saveLz(lines, line1);
            } else {
              String[] lines = tagContent.split("\n");
              String[] line1 = lines[0].split(" ");
              String line2 = "";
              if (lines.length > 1) {
                line2 = lines[1];
              }
              String engineName = line1[0];
              Lizzie.board.getData().engineName = engineName;
              Lizzie.board.getData().winrate = 100 - Double.parseDouble(line1[1]);
              int numPlayouts = 0;
              try {
                numPlayouts =
                    Integer.parseInt(
                        line1[2]
                            .replaceAll("k", "00")
                            .replaceAll("m", "00000")
                            .replaceAll("[^0-9]", ""));
              } catch (Exception e) {
                e.printStackTrace();
                Lizzie.board.isLoadingFile = false;
              }
              if (line1.length >= 4) {
                double scoreMean = Double.parseDouble(line1[3]);
                Lizzie.board.getData().setScoreMean(scoreMean);
                Lizzie.board.getData().isKataData = true;
                if (line1.length >= 5) {
                  double scoreStdev = Double.parseDouble(line1[4]);
                  Lizzie.board.getData().scoreStdev = scoreStdev;
                }
                if (line1.length >= 6) {
                  double pda = Double.parseDouble(line1[5]);
                  Lizzie.board.getData().pda = pda;
                  //  Lizzie.board.getData().isPDA = true;
                }
              }
              if (numPlayouts > 0 && !line2.isEmpty()) {
                parseInfofromfile(line2);
              }
            }
          } else if (tag.equals("LZ2")) {
            // Content contains data for Lizzie to read
            isExtraMode2 = true;
            if (islz2First2) {
              if (Lizzie.board.getData().moveNumber < 1) {
                islz2First = true;
              }
              islz2First2 = false;
            }
            if (islz2First) {
              String[] lines = tagContent.split("\n");
              String[] line1 = lines[0].split(" ");
              saveLz2(lines, line1);
            } else {
              String[] lines = tagContent.split("\n");
              String[] line1 = lines[0].split(" ");
              String line2 = "";
              if (lines.length > 1) {
                line2 = lines[1];
              }
              String engname = line1[0];
              Lizzie.board.getData().engineName2 = engname;
              Lizzie.board.getData().winrate2 = 100 - Double.parseDouble(line1[1]);
              int numPlayouts =
                  Integer.parseInt(
                      line1[2]
                          .replaceAll("k", "00")
                          .replaceAll("m", "00000")
                          .replaceAll("[^0-9]", ""));
              if (line1.length >= 4) {
                double scoreMean = Double.parseDouble(line1[3]);
                Lizzie.board.getData().setScoreMean2(scoreMean);
                Lizzie.board.getData().isKataData2 = true;
                if (line1.length >= 5) {
                  double scoreStdev = Double.parseDouble(line1[4]);
                  Lizzie.board.getData().scoreStdev2 = scoreStdev;
                }
                if (line1.length >= 6) {
                  double pda = Double.parseDouble(line1[5]);
                  Lizzie.board.getData().pda2 = pda;
                  //   Lizzie.board.getData().isPDA2 = true;
                }
              }
              if (numPlayouts > 0 && !line2.isEmpty()) {
                parseInfofromfile2(line2);
              }
            }
          } else if (tag.equals("LZOP")) {
            // Content contains data for Lizzie to read
            if (!Lizzie.board.getHistory().getCurrentHistoryNode().previous().isPresent()) {
              String[] lines = tagContent.split("\n");
              String[] line1 = lines[0].split(" ");
              String line2 = "";
              if (lines.length > 1) {
                line2 = lines[1];
              }
              String engineName = line1[0];
              Lizzie.board.getData().engineName = engineName;
              Lizzie.board.getData().winrate = 100 - Double.parseDouble(line1[1]);
              int numPlayouts = 0;
              try {
                numPlayouts =
                    Integer.parseInt(
                        line1[2]
                            .replaceAll("k", "00")
                            .replaceAll("m", "00000")
                            .replaceAll("[^0-9]", ""));
              } catch (Exception e) {
                e.printStackTrace();
                Lizzie.board.isLoadingFile = false;
              }
              if (line1.length >= 4) {
                double scoreMean = Double.parseDouble(line1[3]);
                Lizzie.board.getData().setScoreMean(scoreMean);
                Lizzie.board.getData().isKataData = true;
                if (line1.length >= 5) {
                  double scoreStdev = Double.parseDouble(line1[4]);
                  Lizzie.board.getData().scoreStdev = scoreStdev;
                }
                if (line1.length >= 6) {
                  double pda = Double.parseDouble(line1[5]);
                  Lizzie.board.getData().pda = pda;
                  //  Lizzie.board.getData().isPDA = true;
                }
              }
              if (numPlayouts > 0 && !line2.isEmpty()) {
                parseInfofromfile(line2);
              }
            }
          } else if (tag.equals("LZOP2")) {
            // Content contains data for Lizzie to read
            if (!Lizzie.board.getHistory().getCurrentHistoryNode().previous().isPresent()) {
              {
                String[] lines = tagContent.split("\n");
                String[] line1 = lines[0].split(" ");
                String line2 = "";
                if (lines.length > 1) {
                  line2 = lines[1];
                }
                String engname = line1[0];
                Lizzie.board.getData().engineName2 = engname;
                Lizzie.board.getData().winrate2 = 100 - Double.parseDouble(line1[1]);
                int numPlayouts =
                    Integer.parseInt(
                        line1[2]
                            .replaceAll("k", "00")
                            .replaceAll("m", "00000")
                            .replaceAll("[^0-9]", ""));
                if (line1.length >= 4) {
                  double scoreMean = Double.parseDouble(line1[3]);
                  Lizzie.board.getData().setScoreMean2(scoreMean);
                  Lizzie.board.getData().isKataData2 = true;
                  if (line1.length >= 5) {
                    double scoreStdev = Double.parseDouble(line1[4]);
                    Lizzie.board.getData().scoreStdev2 = scoreStdev;
                  }
                  if (line1.length >= 6) {
                    double pda = Double.parseDouble(line1[5]);
                    Lizzie.board.getData().pda2 = pda;
                    //   Lizzie.board.getData().isPDA2 = true;
                  }
                }
                if (numPlayouts > 0 && !line2.isEmpty()) {
                  parseInfofromfile2(line2);
                }
              }
            }
          } else if (tag.equals("AB") || tag.equals("AW")) {
            int[] move = convertSgfPosToCoord(tagContent);
            Stone color = tag.equals("AB") ? Stone.BLACK : Stone.WHITE;
            if (moveStart) {
              Lizzie.board.addNodeProperty(tag, tagContent);
              if (startNewBranch) {
                //   subTreeStepMap.put(subTreeDepth, subTreeStepMap.get(subTreeDepth) + 1);
                boolean newBranch = true;
                Lizzie.board
                    .getHistory()
                    .pass(Lizzie.board.getHistory().getLastMoveColor(), newBranch, true);
                if (newBranch) {
                  processPendingPros(Lizzie.board.getHistory(), pendingProps);
                }
                startNewBranch = false;
              }
              Lizzie.board.addNodeProperty(tag, tagContent);
              if (move != null) {
                Lizzie.board.getHistory().addExtraStone(move[0], move[1], color);
              }
            } else {
              if (move == null) {
                Lizzie.board.getHistory().pass(color);
              } else {
                Lizzie.board.getHistory().place(move[0], move[1], color);
              }
              if (!moveStart) {
                Lizzie.board.hasStartStone = true;
                Lizzie.board.addStartList();
                Lizzie.board.getHistory().flatten();
              }
            }
          } else if (tag.equals("PB")) {
            blackPlayer = tagContent;
          } else if (tag.equals("PW")) {
            whitePlayer = tagContent;
          } else if (tag.equals("RE")) {
            result = tagContent;
          } else if (tag.equals("DZ")) {
            if (tagContent.equals("Y")) {
              Lizzie.board.isPkBoard = true;
            }
            if (tagContent.equals("KB")) {
              Lizzie.board.isPkBoard = true;
              Lizzie.board.isKataBoard = true;
              Lizzie.board.isPkBoardKataB = true;
            }
            if (tagContent.equals("KW")) {
              Lizzie.board.isPkBoard = true;
              Lizzie.board.isKataBoard = true;
              Lizzie.board.isPkBoardKataW = true;
            }
            if (tagContent.equals("G")) {
              Lizzie.board.isKataBoard = true;
            }
          } else if (tag.equals("KM")) {
            if (Lizzie.config.readKomi) {
              try {
                if (!tagContent.trim().isEmpty()) {
                  Double komi = Double.parseDouble(tagContent);
                  if (komi >= 200) {
                    komi = komi / 100;
                    if (komi == 3.5) komi = 7.0;
                  }
                  if (komi.toString().endsWith(".75") || komi.toString().endsWith(".25"))
                    komi = komi * 2;
                  if (Math.abs(komi) < Board.boardWidth * Board.boardHeight) {
                    Lizzie.board.getHistory().getGameInfo().setKomi(komi);
                    Lizzie.board.getHistory().getGameInfo().changeKomi();
                    if (EngineManager.currentEngineNo >= 0) {
                      Lizzie.leelaz.sendCommand("komi " + komi);
                    }
                  }
                }
              } catch (NumberFormatException e) {
                e.printStackTrace();
                Lizzie.board.isLoadingFile = false;
              }
            }
          } else {
            if (moveStart) {
              // Other SGF node properties
              if ("AE".equals(tag)) {
                // remove a stone
                if (startNewBranch) {
                  // Save the step count
                  //    subTreeStepMap.put(subTreeDepth, subTreeStepMap.get(subTreeDepth) + 1);
                  Stone color =
                      Lizzie.board.getHistory().getLastMoveColor() == Stone.WHITE
                          ? Stone.BLACK
                          : Stone.WHITE;
                  boolean newBranch =
                      Lizzie.board.getHistory().getCurrentHistoryNode().hasVariations();
                  //  Lizzie.board.pass(color, newBranch, true);
                  Lizzie.board.getHistory().pass(color, newBranch, true);
                  if (newBranch) {
                    processPendingPros(Lizzie.board.getHistory(), pendingProps);
                  }
                  startNewBranch = false;
                }
                Lizzie.board.addNodeProperty(tag, tagContent);
                int[] move = convertSgfPosToCoord(tagContent);
                if (move != null) {
                  Lizzie.board.removeStone(
                      move[0], move[1], tag.equals("AB") ? Stone.BLACK : Stone.WHITE);
                }
              } else if (!"FIT".equals(tag)) {
                boolean firstProp = (subTreeStepMap.get(subTreeDepth).hasVariations());
                if (firstProp) {
                  addProperty(pendingProps, tag, tagContent);
                } else {
                  Lizzie.board.addNodeProperty(tag, tagContent);
                }
              }
            } else {
              if ("N".equals(tag) && headComment.isEmpty()) headComment = tagContent;
              else addProperty(gameProperties, tag, tagContent);
            }
          }
          break;
        case ';':
          if (inProp) {
            // support C[a;b;c;]
            tagContentBuilder.append(c);
          }
          break;
        default:
          if (subTreeDepth > 1 && !isMultiGo) {
            break;
          }
          if (inTag) {
            if (c == '\\') {
              escaping = true;
              continue;
            }
            tagContentBuilder.append(c);
          } else {
            if (c != '\n' && c != '\r' && c != '\t' && c != ' ') {
              tagBuilder.append(c);
            }
          }
      }
    }

    Lizzie.frame.setPlayers(whitePlayer, blackPlayer);
    Lizzie.frame.setResult(result);
    GameInfo gameInfo = Lizzie.board.getHistory().getGameInfo();
    gameInfo.setPlayerBlack(blackPlayer);
    gameInfo.setPlayerWhite(whitePlayer);
    gameInfo.setResult(result);
    // Rewind to game start
    while (Lizzie.board.previousMove(false)) ;
    // Set AW/AB Comment
    if (!headComment.isEmpty()) {
      Lizzie.board.comment(headComment);
    }
    if (gameProperties.size() > 0) {
      Lizzie.board.addNodeProperties(gameProperties);
    }
    Lizzie.config.playSound = oriPlaySound;
    return true;
  }

  public static String saveToString(boolean forUpload) throws IOException {
    try (StringWriter writer = new StringWriter()) {
      saveToStream(Lizzie.board, writer, forUpload, false);
      return writer.toString();
    }
  }

  public static void appendGameTimeAndPlayouts() {
    BoardHistoryNode node = Lizzie.board.getHistory().getStart();
    long blackPlayouts = 0;
    long whitePlayouts = 0;
    while (node.next().isPresent()) {
      if (node.getData().lastMove.isPresent() && node.getData().lastMoveColor.equals(Stone.WHITE)) {
        blackPlayouts += node.getData().getPlayouts();
      }
      if (node.getData().lastMove.isPresent() && node.getData().lastMoveColor.equals(Stone.BLACK)) {
        whitePlayouts += node.getData().getPlayouts();
      }
      node = node.next().get();
    }
    if (node.getData().lastMove.isPresent() && node.getData().lastMoveColor.equals(Stone.WHITE)) {
      blackPlayouts += node.getData().getPlayouts();
    }
    if (node.getData().lastMove.isPresent() && node.getData().lastMoveColor.equals(Stone.BLACK)) {
      whitePlayouts += node.getData().getPlayouts();
    }
    node = Lizzie.board.getHistory().getStart();
    if (Lizzie.config.chkEngineSgfStart)
      node.getData().comment =
          node.getData().comment
              + Lizzie.resourceBundle.getString("SGFParse.startGameSgf")
              + LizzieFrame.toolbar.currentEnginePkSgfNum
              + "\n";
    node.getData().comment +=
        Lizzie.resourceBundle.getString("SGFParse.blackTotalTime")
            + +Lizzie.engineManager.engineList.get(EngineManager.engineGameInfo.blackEngineIndex)
                    .pkMoveTimeGame
                / (float) 1000
            + Lizzie.resourceBundle.getString("SGFParse.seconds")
            + "\n"
            + Lizzie.resourceBundle.getString("SGFParse.totalVisits")
            + blackPlayouts;

    node.getData().comment +=
        "\n"
            + Lizzie.resourceBundle.getString("SGFParse.whiteTotalTime")
            + Lizzie.engineManager.engineList.get(EngineManager.engineGameInfo.whiteEngineIndex)
                    .pkMoveTimeGame
                / (float) 1000
            + Lizzie.resourceBundle.getString("SGFParse.seconds")
            + "\n"
            + Lizzie.resourceBundle.getString("SGFParse.totalVisits")
            + +whitePlayouts;
    if (EngineManager.engineGameInfo.firstEngineIndex
        == EngineManager.engineGameInfo.blackEngineIndex) {
      EngineManager.engineGameInfo.firstEngineTotlePlayouts += blackPlayouts;
      EngineManager.engineGameInfo.secondEngineTotlePlayouts += whitePlayouts;

    } else {
      EngineManager.engineGameInfo.firstEngineTotlePlayouts += whitePlayouts;
      EngineManager.engineGameInfo.secondEngineTotlePlayouts += blackPlayouts;
    }
    EngineManager.engineGameInfo.firstEngineTotleTime +=
        Lizzie.engineManager.engineList.get(EngineManager.engineGameInfo.firstEngineIndex)
            .pkMoveTimeGame;
    EngineManager.engineGameInfo.secondEngineTotleTime +=
        Lizzie.engineManager.engineList.get(EngineManager.engineGameInfo.secondEngineIndex)
            .pkMoveTimeGame;
  }

  public static void appendAiScoreBlunder() {
    int analyzedBlack = 0;
    int analyzedWhite = 0;
    double blackValue = 0;
    double whiteValue = 0;
    List<BlunderMoves> blackDiffWinrate = new ArrayList<BlunderMoves>();
    List<BlunderMoves> whiteDiffWinrate = new ArrayList<BlunderMoves>();
    BoardHistoryNode node = Lizzie.board.getHistory().getEnd();
    while (node.previous().isPresent()) {
      node = node.previous().get();
      NodeInfo nodeInfo = node.nodeInfoMain;
      if (nodeInfo.analyzed) {
        if (nodeInfo.isBlack) {
          blackValue = blackValue + nodeInfo.percentsMatch;
          // + Math.pow(nodeInfo.percentsMatch, (double) 1 / Lizzie.config.matchAiTemperature);
          analyzedBlack = analyzedBlack + 1;
          blackDiffWinrate.add(new BlunderMoves(nodeInfo.moveNum, nodeInfo.diffWinrate));
        } else {
          whiteValue = whiteValue + nodeInfo.percentsMatch;
          // + Math.pow(nodeInfo.percentsMatch, (double) 1 / Lizzie.config.matchAiTemperature);
          analyzedWhite = analyzedWhite + 1;
          whiteDiffWinrate.add(new BlunderMoves(nodeInfo.moveNum, nodeInfo.diffWinrate));
        }
      }
    }

    BoardHistoryNode startNode = Lizzie.board.getHistory().getStart();
    if (analyzedBlack >= 10) {
      String bAiScore = String.format(Locale.ENGLISH, "%.1f", blackValue * 100 / analyzedBlack);
      String infoString =
          Lizzie.resourceBundle.getString("SGFParse.blackAiScore")
              + bAiScore
              + "\n"
              + Lizzie.resourceBundle.getString("SGFParse.blackTop10");
      Collections.sort(blackDiffWinrate);
      for (int i = 0; i < 10; i++) {
        if (i < 9) infoString = infoString + " " + blackDiffWinrate.get(i).toString() + ",";
        else infoString = infoString + " " + blackDiffWinrate.get(i).toString();
      }
      if (startNode.getData().comment.equals("")) startNode.getData().comment = infoString;
      else startNode.getData().comment += "\n" + infoString;
    }
    if (analyzedWhite >= 10) {
      String wAiScore = String.format(Locale.ENGLISH, "%.1f", whiteValue * 100 / analyzedWhite);
      String infoString =
          Lizzie.resourceBundle.getString("SGFParse.whiteAiScore")
              + wAiScore
              + "\n"
              + Lizzie.resourceBundle.getString("SGFParse.whiteTop10");
      Collections.sort(whiteDiffWinrate);
      for (int i = 0; i < 10; i++) {
        if (i < 9) infoString = infoString + " " + whiteDiffWinrate.get(i).toString() + ",";
        else infoString = infoString + " " + whiteDiffWinrate.get(i).toString();
      }
      if (startNode.getData().comment.equals("")) startNode.getData().comment = infoString;
      else startNode.getData().comment += "\n" + infoString;
    }
  }
  //
  //  public static void appendTimeAndPlayouts() {
  //    appendGameTime();
  //    appendGamePlayouts();
  //  }

  public static void save(Board board, String filename) throws IOException {
    save(board, filename, false);
  }

  public static void save(Board board, String filename, boolean isAutoSave) throws IOException {
    try (Writer writer = new OutputStreamWriter(new FileOutputStream(filename), "utf-8")) {
      saveToStream(board, writer, false, isAutoSave);
    }
  }

  private static void saveToStream(
      Board board, Writer writer, boolean forUpload, boolean fromAutoSave) throws IOException {
    // collect game info

    BoardHistoryList history = board.getHistory().shallowCopy();
    GameInfo gameInfo = history.getGameInfo();
    String playerB = gameInfo.getPlayerBlack();
    String playerW = gameInfo.getPlayerWhite();
    String result = gameInfo.getResult();
    Double komi = gameInfo.getKomi();
    Integer handicap = gameInfo.getHandicap();
    String date = SGF_DATE_FORMAT.format(gameInfo.getDate());

    // add SGF header
    StringBuilder builder = new StringBuilder("(;");
    StringBuilder generalProps = new StringBuilder("");
    if (handicap != 0) generalProps.append(String.format("HA[%s]", handicap));
    if (LizzieFrame.isSavingRaw) {
      generalProps.append(
          String.format(
              "KM[%s]PW[%s]PB[%s]DT[%s]RE[%s]SZ[%s]CA[UTF-8]",
              komi,
              playerW,
              playerB,
              date,
              result,
              Board.boardWidth
                  + (Board.boardWidth != Board.boardHeight ? ":" + Board.boardHeight : "")));
    } else {
      if (EngineManager.isEngineGame || EngineManager.isSaveingEngineSGF) {
        Lizzie.board.updateWinrate();
        SGFParser.appendTime();
        if (Lizzie.engineManager.engineList.get(EngineManager.engineGameInfo.blackEngineIndex)
            .isKatago) {
          String rules = "";
          boolean usingSpecificRues = false;
          switch (Lizzie.engineManager.engineList.get(EngineManager.engineGameInfo.blackEngineIndex)
              .usingSpecificRules) {
            case 1:
              rules = Lizzie.resourceBundle.getString("LizzieFrame.currentRules.chinese");
              usingSpecificRues = true;
              break;
            case 2:
              rules = Lizzie.resourceBundle.getString("LizzieFrame.currentRules.chn-ancient");
              usingSpecificRues = true;
              break;
            case 3:
              rules = Lizzie.resourceBundle.getString("LizzieFrame.currentRules.japanese");
              usingSpecificRues = true;
              break;
            case 4:
              rules = Lizzie.resourceBundle.getString("LizzieFrame.currentRules.tromp-taylor");
              usingSpecificRues = true;
              break;
            case 5:
              rules = Lizzie.resourceBundle.getString("LizzieFrame.currentRules.others");
              usingSpecificRues = true;
              break;
          }

          if (usingSpecificRues) {
            if (Lizzie.board.getHistory().getStart().getData().comment.equals(""))
              Lizzie.board.getHistory().getStart().getData().comment +=
                  Lizzie.resourceBundle.getString("SGFParse.blackRules") + rules;
            else
              Lizzie.board.getHistory().getStart().getData().comment +=
                  "\n" + Lizzie.resourceBundle.getString("SGFParse.blackRules") + rules;
          }
        }
        if (Lizzie.engineManager.engineList.get(EngineManager.engineGameInfo.whiteEngineIndex)
            .isKatago) {
          String rules = "";
          boolean usingSpecificRues = false;
          switch (Lizzie.engineManager.engineList.get(EngineManager.engineGameInfo.whiteEngineIndex)
              .usingSpecificRules) {
            case 1:
              rules = Lizzie.resourceBundle.getString("LizzieFrame.currentRules.chinese");
              usingSpecificRues = true;
              break;
            case 2:
              rules = Lizzie.resourceBundle.getString("LizzieFrame.currentRules.chn-ancient");
              usingSpecificRues = true;
              break;
            case 3:
              rules = Lizzie.resourceBundle.getString("LizzieFrame.currentRules.japanese");
              usingSpecificRues = true;
              break;
            case 4:
              rules = Lizzie.resourceBundle.getString("LizzieFrame.currentRules.tromp-taylor");
              usingSpecificRues = true;
              break;
            case 5:
              rules = Lizzie.resourceBundle.getString("LizzieFrame.currentRules.others");
              usingSpecificRues = true;
              break;
          }

          if (usingSpecificRues) {
            if (Lizzie.board.getHistory().getStart().getData().comment.equals(""))
              Lizzie.board.getHistory().getStart().getData().comment +=
                  Lizzie.resourceBundle.getString("SGFParse.whiteRules") + rules;
            else
              Lizzie.board.getHistory().getStart().getData().comment +=
                  "\n" + Lizzie.resourceBundle.getString("SGFParse.whiteRules") + rules;
          }
        }
      } else {
        if (Lizzie.leelaz.isKatago && !fromAutoSave) {
          String rules = "";
          boolean usingSpecificRues = false;
          if (Lizzie.leelaz.isKatago) {
            switch (Lizzie.leelaz.usingSpecificRules) {
              case 1:
                rules = Lizzie.resourceBundle.getString("LizzieFrame.currentRules.chinese");
                usingSpecificRues = true;
                break;
              case 2:
                rules = Lizzie.resourceBundle.getString("LizzieFrame.currentRules.chn-ancient");
                usingSpecificRues = true;
                break;
              case 3:
                rules = Lizzie.resourceBundle.getString("LizzieFrame.currentRules.japanese");
                usingSpecificRues = true;
                break;
              case 4:
                rules = Lizzie.resourceBundle.getString("LizzieFrame.currentRules.tromp-taylor");
                usingSpecificRues = true;
                break;
              case 5:
                rules = Lizzie.resourceBundle.getString("LizzieFrame.currentRules.others");
                usingSpecificRues = true;
                break;
            }
          }
          if (usingSpecificRues) {
            if (Lizzie.board.getHistory().getStart().getData().comment.equals(""))
              Lizzie.board.getHistory().getStart().getData().comment +=
                  Lizzie.resourceBundle.getString("SGFParse.rules") + rules;
            else if (!Lizzie.board
                .getHistory()
                .getStart()
                .getData()
                .comment
                .contains(Lizzie.resourceBundle.getString("SGFParse.rules")))
              Lizzie.board.getHistory().getStart().getData().comment =
                  Lizzie.resourceBundle.getString("SGFParse.rules")
                      + rules
                      + "\n\n"
                      + Lizzie.board.getHistory().getStart().getData().comment;
            else {
              String oldComment = Lizzie.board.getHistory().getStart().getData().comment;
              int leftIndex =
                  oldComment.indexOf(
                      "\n", oldComment.indexOf(Lizzie.resourceBundle.getString("SGFParse.rules")));
              Lizzie.board.getHistory().getStart().getData().comment =
                  oldComment.substring(
                          0, oldComment.indexOf(Lizzie.resourceBundle.getString("SGFParse.rules")))
                      + Lizzie.resourceBundle.getString("SGFParse.rules")
                      + rules
                      + (leftIndex > 0 ? oldComment.substring(leftIndex) : "");
            }
          }
        }
      }

      if (EngineManager.isEngineGame || Lizzie.board.isPkBoard) {
        if (Lizzie.engineManager.engineList.get(EngineManager.engineGameInfo.whiteEngineIndex)
                .isKatago
            || Lizzie.engineManager.engineList.get(EngineManager.engineGameInfo.whiteEngineIndex)
                .isSai
            || Lizzie.board.isPkBoardKataW)
          generalProps.append(
              String.format(
                  "KM[%s]PW[%s]PB[%s]DT[%s]DZ[KW]AP[Lizzie: %s]RE[%s]SZ[%s]CA[UTF-8]",
                  komi,
                  playerW,
                  playerB,
                  date,
                  Lizzie.lizzieVersion,
                  result,
                  Board.boardWidth
                      + (Board.boardWidth != Board.boardHeight ? ":" + Board.boardHeight : "")));
        else if (Lizzie.engineManager.engineList.get(EngineManager.engineGameInfo.blackEngineIndex)
                .isKatago
            || Lizzie.engineManager.engineList.get(EngineManager.engineGameInfo.blackEngineIndex)
                .isSai
            || Lizzie.board.isPkBoardKataB)
          generalProps.append(
              String.format(
                  "KM[%s]PW[%s]PB[%s]DT[%s]DZ[KB]AP[Lizzie: %s]RE[%s]SZ[%s]CA[UTF-8]",
                  komi,
                  playerW,
                  playerB,
                  date,
                  Lizzie.lizzieVersion,
                  result,
                  Board.boardWidth
                      + (Board.boardWidth != Board.boardHeight ? ":" + Board.boardHeight : "")));
        else
          generalProps.append(
              String.format(
                  "KM[%s]PW[%s]PB[%s]DT[%s]DZ[Y]AP[Lizzie: %s]RE[%s]SZ[%s]CA[UTF-8]",
                  komi,
                  playerW,
                  playerB,
                  date,
                  Lizzie.lizzieVersion,
                  result,
                  Board.boardWidth
                      + (Board.boardWidth != Board.boardHeight ? ":" + Board.boardHeight : "")));
      } else {
        if (Lizzie.leelaz.isKatago || Lizzie.board.isKataBoard)
          generalProps.append(
              String.format(
                  "KM[%s]PW[%s]PB[%s]DT[%s]DZ[G]AP[Lizzie: %s]RE[%s]SZ[%s]CA[UTF-8]",
                  komi,
                  playerW,
                  playerB,
                  date,
                  Lizzie.lizzieVersion,
                  result,
                  Board.boardWidth
                      + (Board.boardWidth != Board.boardHeight ? ":" + Board.boardHeight : "")));
        else
          generalProps.append(
              String.format(
                  "KM[%s]PW[%s]PB[%s]DT[%s]AP[Lizzie: %s]RE[%s]SZ[%s]CA[UTF-8]",
                  komi,
                  playerW,
                  playerB,
                  date,
                  Lizzie.lizzieVersion,
                  result,
                  Board.boardWidth
                      + (Board.boardWidth != Board.boardHeight ? ":" + Board.boardHeight : "")));
      }
    }
    // To append the winrate to the comment of sgf we might need to update the
    // Winrate
    // if (Lizzie.config.appendWinrateToComment) {
    // Lizzie.board.updateWinrate();
    // }

    // move to the first move
    history.toStart();

    // Game properties
    history.getData().addProperties(generalProps.toString());
    builder.append(history.getData().propertiesString());

    // add handicap stones to SGF
    if (handicap != 0) {
      builder.append("AB");
      Stone[] stones = history.getStones();
      for (int i = 0; i < stones.length; i++) {
        Stone stone = stones[i];
        if (stone.isBlack()) {
          // i = x * Board.BOARD_SIZE + y;
          builder.append(String.format("[%s]", asCoord(i)));
        }
      }
    } else {
      // Process the AW/AB stone
      Stone[] stones = history.getStones();
      StringBuilder abStone = new StringBuilder();
      StringBuilder awStone = new StringBuilder();
      for (int i = 0; i < stones.length; i++) {
        Stone stone = stones[i];
        if (stone.isBlack() || stone.isWhite()) {
          if (stone.isBlack()) {
            abStone.append(String.format("[%s]", asCoord(i)));
          } else {
            awStone.append(String.format("[%s]", asCoord(i)));
          }
        }
      }
      if (abStone.length() > 0) {
        builder.append("AB").append(abStone);
      }
      if (awStone.length() > 0) {
        builder.append("AW").append(awStone);
      }
    }

    // The AW/AB Comment
    if (!history.getData().comment.isEmpty()) {
      builder.append(String.format("C[%s]", Escaping(history.getData().comment)));
    }
    BoardHistoryNode curNode = history.getCurrentHistoryNode();
    try {
      if (curNode.getData().getPlayouts() > 0)
        builder.append(String.format("LZOP[%s]", formatNodeData(curNode)));
      if (LizzieFrame.extraMode == 2 && curNode.getData().getPlayouts2() > 0)
        builder.append(String.format("LZOP2[%s]", formatNodeData2(curNode)));
      if (!EngineManager.isEngineGame && !Lizzie.board.isPkBoard) {
        BoardData data = curNode.getData();
        if (Lizzie.board.isGameBoard) {
          if (data.getPlayouts() > 0 && curNode.next().isPresent())
            curNode.next().get().getData().comment = formatCommentForGame(curNode);
          else if (curNode.next().isPresent()) curNode.next().get().getData().comment = "";
        }
      }
    } catch (Exception e) {
      Lizzie.board.isLoadingFile = false;
    }
    // replay moves, and convert them to tags.
    // * format: ";B[xy]" or ";W[xy]"
    // * with 'xy' = coordinates ; or 'tt' for pass.

    // Write variation tree
    builder.append(generateNode(board, curNode, forUpload));

    // close file
    builder.append(')');
    writer.append(builder.toString());
  }

  /** Generate node with variations */
  public static void appendComment() {
    // if (!Lizzie.config.showComment) return;
    // if (!Lizzie.leelaz.isLoaded()) return;
    if (EngineManager.isEngineGame || EngineManager.isSaveingEngineSGF) {
      if (Lizzie.board.getHistory().getCurrentHistoryNode().getData().getPlayouts() > 0) {
        Lizzie.board.getHistory().getData().comment =
            formatCommentPk(Lizzie.board.getHistory().getCurrentHistoryNode());
      }
    } else if (Lizzie.board.getHistory().getCurrentHistoryNode().getData().getPlayouts() > 0) {
      if (Lizzie.board.isGameBoard) {
        Lizzie.board.getHistory().getData().comment =
            formatCommentForGame(Lizzie.board.getHistory().getCurrentHistoryNode());
      } else {
        Lizzie.board.getHistory().getData().comment =
            formatComment(Lizzie.board.getHistory().getCurrentHistoryNode());
      }
    }
  }

  public static void appendTime() {
    BoardHistoryNode node = Lizzie.board.getHistory().getCurrentHistoryNode();
    if (node.getData().moveNumber >= 3 && node.getData().getPlayouts() > 0) {
      if (node.getData().blackToPlay)
        node.getData().comment +=
            "\n"
                + Lizzie.resourceBundle.getString("SGFParse.moveTime")
                + Lizzie.engineManager.engineList.get(EngineManager.engineGameInfo.blackEngineIndex)
                        .pkMoveTime
                    / 1000f
                + Lizzie.resourceBundle.getString("SGFParse.seconds");
      else
        node.getData().comment +=
            "\n"
                + Lizzie.resourceBundle.getString("SGFParse.moveTime")
                + Lizzie.engineManager.engineList.get(EngineManager.engineGameInfo.whiteEngineIndex)
                        .pkMoveTime
                    / 1000f
                + Lizzie.resourceBundle.getString("SGFParse.seconds");
    }
  }
  //
  //  public static void appendCommentForPk() {
  //	  appendComment();
  //  }

  private static double getMatchValue(BoardHistoryNode node) {
    if (!node.isMainTrunk()) return 0;
    boolean isBlack = node.getData().blackToPlay;
    double matchValue = 0;
    int analyzed = 0;
    while (node.previous().isPresent()) {
      node = node.previous().get();
      NodeInfo nodeInfo = node.nodeInfoMain;
      if (node.getData().moveNumber <= Lizzie.config.matchAiLastMove
          && (node.getData().moveNumber + 1) > Lizzie.config.matchAiFirstMove) {
        if (nodeInfo.analyzed) {
          if (node.getData().blackToPlay && isBlack) {
            matchValue = matchValue + nodeInfo.percentsMatch;
            analyzed++;
          } else if (!node.getData().blackToPlay && !isBlack) {
            matchValue = matchValue + nodeInfo.percentsMatch;
            analyzed++;
          }
        }
      }
    }
    if (analyzed == 0) return 0;
    return matchValue * 100 / analyzed;
  }

  private static String generateNode(Board board, BoardHistoryNode node, boolean forUpload)
      throws IOException {
    StringBuilder builder = new StringBuilder("");

    if (node != null) {

      BoardData data = node.getData();
      String stone = "";
      if (Stone.BLACK.equals(data.lastMoveColor) || Stone.WHITE.equals(data.lastMoveColor)) {

        if (Stone.BLACK.equals(data.lastMoveColor)) stone = "B";
        else if (Stone.WHITE.equals(data.lastMoveColor)) stone = "W";

        builder.append(";");

        if (!data.dummy) {
          builder.append(
              String.format(
                  "%s[%s]",
                  stone, data.lastMove.isPresent() ? asCoord(data.lastMove.get()) : passPos()));
        }

        // Node properties
        builder.append(data.propertiesString());

        if (forUpload) {
          builder.append(String.format("FIT[%s]", getMatchValue(node)));
        }

        if (!LizzieFrame.isSavingRaw) {
          if (Lizzie.config.appendWinrateToComment) {
            // Append the winrate to the comment of sgf
            if (!EngineManager.isEngineGame && !Lizzie.board.isPkBoard) {
              if (Lizzie.board.isGameBoard) {
                if (data.getPlayouts() > 0 && node.next().isPresent())
                  node.next().get().getData().comment = formatCommentForGame(node);
                else if (node.next().isPresent()) node.next().get().getData().comment = "";
              } else {
                if (data.getPlayouts() > 0) data.comment = formatComment(node);
                if (LizzieFrame.extraMode == 2 && data.getPlayouts2() > 0) {
                  data.comment = formatComment2(node);
                  //  if (data.comment != "") data.comment += "\n" + data.comment2;
                  //  else data.comment = data.comment2;
                }
              }
            }
          } else if (!EngineManager.isEngineGame && !Lizzie.board.isPkBoard) {
            if (Lizzie.board.isGameBoard) {
              if (data.getPlayouts() > 0 && node.next().isPresent())
                node.next().get().getData().comment = formatCommentForGame(node);
              else if (node.next().isPresent()) node.next().get().getData().comment = "";
            }
          }
          String curComment =
              EngineManager.isSaveingEngineSGF && node.previous().isPresent()
                  ? node.previous().get().getData().comment
                  : data.comment;
          if (EngineManager.isSaveingEngineSGF) {
            if (node.previous().isPresent() && !node.previous().get().previous().isPresent())
              curComment = formatCommentPk(node.previous().get());
          }
          //  if (data.komi > -999) curComment = data.comment + "\n" + "贴目: " + data.komi;
          //  if (Lizzie.board.getHistory().getData().pda != 0) curComment += " PDA: " + data.pda;
          // Write the comment
          if (!data.comment.isEmpty()) {
            builder.append(String.format("C[%s]", Escaping(curComment)));
          }

          // Add LZ specific data to restore on next load
          try {
            if (node.getData().getPlayouts() > 0)
              builder.append(String.format("LZ[%s]", formatNodeData(node)));
            if (LizzieFrame.extraMode == 2 && node.getData().getPlayouts2() > 0)
              builder.append(String.format("LZ2[%s]", formatNodeData2(node)));
          } catch (Exception e) {
            Lizzie.board.isLoadingFile = false;
          }
        } else if (LizzieFrame.isSavingRawComment) {
          // Append the winrate to the comment of sgf
          if (!EngineManager.isEngineGame && !Lizzie.board.isPkBoard) {
            if (data.getPlayouts() > 0) data.comment = formatComment(node);
            if (LizzieFrame.extraMode == 2 && data.getPlayouts2() > 0) {
              data.comment = formatComment2(node);
              //  if (data.comment != "") data.comment += "\n" + data.comment2;
              //  else data.comment = data.comment2;
            }
          }
          String curComment = data.comment;
          //  if (data.komi > -999) curComment = data.comment + "\n" + "贴目: " + data.komi;
          //    if (Lizzie.board.getHistory().getData().pda != 0) curComment += " PDA: " + data.pda;
          // Write the comment
          if (!data.comment.isEmpty()) {
            builder.append(String.format("C[%s]", Escaping(curComment)));
          }
        }
      }
      if (node.numberOfChildren() > 1) {
        // Variation
        for (BoardHistoryNode sub : node.getVariations()) {
          builder.append("(");
          builder.append(generateNode(board, sub, forUpload));
          builder.append(")");
        }
      } else if (node.numberOfChildren() == 1) {
        builder.append(generateNode(board, node.next().orElse(null), forUpload));
      } else {
        if (node.isEndDummay()) {
          builder.append(";DD[true]");
        }
        return builder.toString();
      }
    }

    return builder.toString();
  }

  /**
   * Format Comment with following format: Move <Move number> <Winrate> (<Last Move Rate
   * Difference>) (<Weight name> / <Playouts>)
   */
  public static String formatComment(BoardHistoryNode node) {
    //    if (node.getData().commented) return node.getData().comment;
    //    node.getData().commented = true;
    BoardData data = node.getData();
    String engine = node.getData().engineName;
    engine = engine.replaceAll(" ", "");
    String playouts = Utils.getPlayoutsString(data.getPlayouts());

    // Last winrate
    Optional<BoardData> lastNode = node.previous().flatMap(n -> Optional.of(n.getData()));
    boolean validLastWinrate = lastNode.isPresent();

    double lastWR = validLastWinrate ? lastNode.get().getWinrate() : 50;
    // Current winrate
    boolean validWinrate = (data.getPlayouts() > 0);
    double curWR;
    if (Lizzie.config.winrateAlwaysBlack) {
      curWR = validWinrate ? data.getWinrate() : lastWR;
    } else {
      curWR = validWinrate ? data.getWinrate() : 100 - lastWR;
    }

    // Last move difference winrate
    String lastMoveDiff = "";
    if (validLastWinrate && validWinrate) {
      //      if (Lizzie.config.handicapInsteadOfWinrate) {
      //        double currHandicapedWR = Leelaz.winrateToHandicap(100 - curWR);
      //        double lastHandicapedWR = Leelaz.winrateToHandicap(lastWR);
      //        lastMoveDiff = String.format(": %.2f", currHandicapedWR - lastHandicapedWR);
      //      } else {
      double diff;
      if (Lizzie.config.winrateAlwaysBlack) {
        diff = lastWR - curWR;
      } else {
        diff = 100 - lastWR - curWR;
      }
      lastMoveDiff = String.format("(%s%.1f%%)", diff > 0 ? "+" : "-", Math.abs(diff));
      // }
    }
    // if (Lizzie.engineManager.isEngineGame && node.moveNumberOfNode() <= 3) {
    // lastMoveDiff = "";
    // }
    //    String wf = "%s棋 胜率: %s %s\n(%s / %s 计算量)";
    boolean blackWinrate = !data.blackToPlay || Lizzie.config.winrateAlwaysBlack;
    String nc = "";
    //        String.format(
    //            wf,
    //           blackWinrate ? "黑" : "白",
    //            String.format(Locale.ENGLISH,"%.1f%%", 100 - curWR),
    //            lastMoveDiff,
    //            engine,
    //            playouts);

    if (data.isKataData) {
      String diffScore = "";
      if (validLastWinrate && validWinrate) {
        if (node.previous().get().getData().getPlayouts() > 0) {
          double diff = -data.scoreMean - node.previous().get().getData().scoreMean;
          if (Lizzie.config.winrateAlwaysBlack && Lizzie.board.getHistory().isBlacksTurn())
            diff = -diff;
          diffScore = String.format("(%s%.1f)", diff > 0 ? "+" : "-", Math.abs(diff));
        }
      }
      if (data.isSaiData) {
        double score = data.scoreMean;
        if (data.blackToPlay) {
          if (Lizzie.config.showKataGoBoardScoreMean)
            score = score + Lizzie.board.getHistory().getGameInfo().getKomi();
          else score = -score;
        } else {
          if (Lizzie.config.showKataGoBoardScoreMean)
            score = score - Lizzie.board.getHistory().getGameInfo().getKomi();
          else score = -score;
        }
        String wf =
            "%s "
                + Lizzie.resourceBundle.getString("SGFParse.winrate")
                + " %s %s\n"
                + (Lizzie.config.showKataGoBoardScoreMean
                    ? Lizzie.resourceBundle.getString("SGFParse.leadBoard")
                    : Lizzie.resourceBundle.getString("SGFParse.leadScore"))
                + " %s %s\n(%s / %s "
                + Lizzie.resourceBundle.getString("SGFParse.playouts")
                + ")\n"
                + Lizzie.resourceBundle.getString("SGFParse.komi")
                + " %s";
        nc =
            String.format(
                wf,
                blackWinrate
                    ? Lizzie.resourceBundle.getString("SGFParse.black")
                    : Lizzie.resourceBundle.getString("SGFParse.white"),
                String.format(Locale.ENGLISH, "%.1f%%", 100 - curWR),
                lastMoveDiff,
                String.format(Locale.ENGLISH, "%.1f", score),
                diffScore,
                engine,
                playouts,
                String.format(Locale.ENGLISH, "%.1f", data.getKomi()));
      } else {
        double score = node.getData().scoreMean;
        if (data.blackToPlay) {
          if (Lizzie.config.showKataGoBoardScoreMean)
            score = score + Lizzie.board.getHistory().getGameInfo().getKomi();
          else score = -score;
        } else {
          if (Lizzie.config.showKataGoBoardScoreMean)
            score = score - Lizzie.board.getHistory().getGameInfo().getKomi();
          else score = -score;
        }
        String wf =
            "%s "
                + Lizzie.resourceBundle.getString("SGFParse.winrate")
                + " %s %s\n"
                + (Lizzie.config.showKataGoBoardScoreMean
                    ? Lizzie.resourceBundle.getString("SGFParse.leadBoard")
                    : Lizzie.resourceBundle.getString("SGFParse.leadScore"))
                + " %s %s "
                + Lizzie.resourceBundle.getString("SGFParse.stdev")
                + " %s\n(%s / %s "
                + Lizzie.resourceBundle.getString("SGFParse.playouts")
                + ")\n"
                + Lizzie.resourceBundle.getString("SGFParse.komi")
                + " %s";
        double scoreStdev = 0;
        try {
          if (!data.bestMoves.isEmpty()) scoreStdev = node.getData().bestMoves.get(0).scoreStdev;
        } catch (Exception ex) {
          ex.printStackTrace();
        }
        nc =
            String.format(
                wf,
                blackWinrate
                    ? Lizzie.resourceBundle.getString("SGFParse.black")
                    : Lizzie.resourceBundle.getString("SGFParse.white"),
                String.format(Locale.ENGLISH, "%.1f%%", 100 - curWR),
                lastMoveDiff,
                String.format(Locale.ENGLISH, "%.1f", score),
                diffScore,
                String.format(Locale.ENGLISH, "%.1f", scoreStdev),
                engine,
                playouts,
                data.pda != 0
                    ? String.format(Locale.ENGLISH, "%.1f", data.getKomi())
                        + " "
                        + Lizzie.resourceBundle.getString("SGFParse.pda")
                        + data.pda
                    : String.format(Locale.ENGLISH, "%.1f", data.getKomi()));
      }
    } else {
      String wf =
          "%s "
              + Lizzie.resourceBundle.getString("SGFParse.winrate")
              + " %s %s\n(%s / %s "
              + Lizzie.resourceBundle.getString("SGFParse.playouts")
              + ")\n"
              + Lizzie.resourceBundle.getString("SGFParse.komi")
              + " %s";

      nc =
          String.format(
              wf,
              blackWinrate
                  ? Lizzie.resourceBundle.getString("SGFParse.black")
                  : Lizzie.resourceBundle.getString("SGFParse.white"),
              String.format(Locale.ENGLISH, "%.1f%%", 100 - curWR),
              lastMoveDiff,
              engine,
              playouts,
              String.format(Locale.ENGLISH, "%.1f", data.getKomi()));
    }

    if (!data.comment.isEmpty()) {
      // [^\\(\\)/]*
      String wp = "";
      if (!data.isKataData) {
        wp =
            "("
                + Lizzie.resourceBundle.getString("SGFParse.black")
                + " |"
                + Lizzie.resourceBundle.getString("SGFParse.white")
                + " )"
                + Lizzie.resourceBundle.getString("SGFParse.winrate")
                + " [0-9\\.\\-]+%* \\(*[0-9.\\-+]*%*\\)*\n\\("
                + ".*"
                + " / [0-9\\.]*[kmKM]* "
                + Lizzie.resourceBundle.getString("SGFParse.playouts")
                + "\\)\\n"
                + Lizzie.resourceBundle.getString("SGFParse.komi")
                + ".*";
      } else {
        if (data.isSaiData)
          wp =
              "("
                  + Lizzie.resourceBundle.getString("SGFParse.black")
                  + " |"
                  + Lizzie.resourceBundle.getString("SGFParse.white")
                  + " )"
                  + Lizzie.resourceBundle.getString("SGFParse.winrate")
                  + " [0-9\\.\\-]+%* \\(*[0-9.\\-+]*%*\\)*\n"
                  + (Lizzie.config.showKataGoBoardScoreMean
                      ? Lizzie.resourceBundle.getString("SGFParse.leadBoard")
                      : Lizzie.resourceBundle.getString("SGFParse.leadScore"))
                  + " [0-9\\.\\-+]* \\(*[0-9.\\-+]*\\)*\n\\("
                  + ".*"
                  + " / [0-9\\.]*[kmKM]* "
                  + Lizzie.resourceBundle.getString("SGFParse.playouts")
                  + "\\)\\n"
                  + Lizzie.resourceBundle.getString("SGFParse.komi")
                  + ".*";
        else
          wp =
              "("
                  + Lizzie.resourceBundle.getString("SGFParse.black")
                  + " |"
                  + Lizzie.resourceBundle.getString("SGFParse.white")
                  + " )"
                  + Lizzie.resourceBundle.getString("SGFParse.winrate")
                  + " [0-9\\.\\-]+%* \\(*[0-9.\\-+]*%*\\)*\n"
                  + (Lizzie.config.showKataGoBoardScoreMean
                      ? Lizzie.resourceBundle.getString("SGFParse.leadBoard")
                      : Lizzie.resourceBundle.getString("SGFParse.leadScore"))
                  + " [0-9\\.\\-+]* \\(*[0-9.\\-+]*\\)* "
                  + Lizzie.resourceBundle.getString("SGFParse.stdev")
                  + " [0-9\\.\\-+]*\n\\("
                  + ".*"
                  + " / [0-9\\.]*[kmKM]* "
                  + Lizzie.resourceBundle.getString("SGFParse.playouts")
                  + "\\)\\n"
                  + Lizzie.resourceBundle.getString("SGFParse.komi")
                  + ".*";
      }
      // if (Lizzie.leelaz.isKatago) wp = wp + "\n.*";
      if (data.comment.matches("(?s).*" + wp + "(?s).*")) {
        nc = data.comment.replaceAll(wp, nc);
      } else {
        nc = String.format("%s\n\n%s", data.comment, nc);
      }
    }
    return nc;
  }

  private static String formatCommentForGame(BoardHistoryNode node) {
    //    if (node.getData().commented) return node.getData().comment;
    //    node.getData().commented = true;

    BoardData data = node.getData();
    String engine = node.getData().engineName;
    engine = engine.replaceAll(" ", "");
    String playouts = Utils.getPlayoutsString(data.getPlayouts());

    // Last winrate
    Optional<BoardData> lastNode = node.previous().flatMap(n -> Optional.of(n.getData()));
    boolean validLastWinrate = lastNode.isPresent();

    double lastWR = validLastWinrate ? lastNode.get().winrate : 50;
    // Current winrate
    boolean validWinrate = (data.getPlayouts() > 0);
    double curWR;
    //  if (Lizzie.config.winrateAlwaysBlack) {
    curWR = validWinrate ? data.winrate : lastWR;
    //  } else {
    //    curWR = validWinrate ? data.getWinrate() : 100 - lastWR;
    //  }

    // Last move difference winrate
    String lastMoveDiff = "";
    if (validLastWinrate && validWinrate) {
      //      if (Lizzie.config.handicapInsteadOfWinrate) {
      //        double currHandicapedWR = Leelaz.winrateToHandicap(100 - curWR);
      //        double lastHandicapedWR = Leelaz.winrateToHandicap(lastWR);
      //        lastMoveDiff = String.format(": %.2f", currHandicapedWR - lastHandicapedWR);
      //      } else {
      double diff;
      //  if (Lizzie.config.winrateAlwaysBlack) {
      //      diff = lastWR - curWR;
      //   } else {
      diff = 100 - lastWR - curWR;
      //   }
      lastMoveDiff = String.format("(%s%.1f%%)", diff < 0 ? "+" : "-", Math.abs(diff));
      // }
    }
    // if (Lizzie.engineManager.isEngineGame && node.moveNumberOfNode() <= 3) {
    // lastMoveDiff = "";
    // }
    //    String wf = "%s棋 胜率: %s %s\n(%s / %s 计算量)";
    boolean blackWinrate = !data.blackToPlay;
    String nc = "";
    //        String.format(
    //            wf,
    //           blackWinrate ? "黑" : "白",
    //            String.format(Locale.ENGLISH,"%.1f%%", 100 - curWR),
    //            lastMoveDiff,
    //            engine,
    //            playouts);

    if (data.isKataData) {
      String diffScore = "";
      // if (validLastWinrate && validWinrate) {
      if (node.previous().isPresent()) {
        if (node.previous().get().getData().getPlayouts() > 0) {
          double diff = -data.scoreMean - node.previous().get().getData().scoreMean;
          if (Lizzie.config.winrateAlwaysBlack && Lizzie.board.getHistory().isBlacksTurn())
            diff = -diff;
          // if (!blackWinrate) diff = -diff;
          diffScore = String.format("(%s%.1f)", diff > 0 ? "+" : "-", Math.abs(diff));
        }
        //  }
        else if (node.previous().get().previous().isPresent()
            && node.previous().get().previous().get().getData().getPlayouts() > 0) {
          double diff =
              -data.scoreMean + node.previous().get().previous().get().getData().scoreMean;
          if (!blackWinrate) diff = -diff;
          diffScore = String.format("(%s%.1f)", diff > 0 ? "+" : "-", Math.abs(diff));
        }
      }
      if (data.isSaiData) {
        double score = data.scoreMean;
        if (data.blackToPlay) {
          if (Lizzie.config.showKataGoBoardScoreMean)
            score = score + Lizzie.board.getHistory().getGameInfo().getKomi();
        } else {
          if (Lizzie.config.showKataGoBoardScoreMean)
            score = score - Lizzie.board.getHistory().getGameInfo().getKomi();
        }
        String wf =
            "%s "
                + Lizzie.resourceBundle.getString("SGFParse.winrate")
                + " %s %s\n"
                + (Lizzie.config.showKataGoBoardScoreMean
                    ? Lizzie.resourceBundle.getString("SGFParse.leadBoard")
                    : Lizzie.resourceBundle.getString("SGFParse.leadScore"))
                + " %s %s\n(%s / %s "
                + Lizzie.resourceBundle.getString("SGFParse.playouts")
                + ")\n"
                + Lizzie.resourceBundle.getString("SGFParse.komi")
                + " %s";
        nc =
            String.format(
                wf,
                blackWinrate
                    ? Lizzie.resourceBundle.getString("SGFParse.white")
                    : Lizzie.resourceBundle.getString("SGFParse.black"),
                String.format(Locale.ENGLISH, "%.1f%%", curWR),
                lastMoveDiff,
                String.format(Locale.ENGLISH, "%.1f", score),
                diffScore,
                engine,
                playouts,
                String.format(Locale.ENGLISH, "%.1f", data.getKomi()));
      } else {
        double score = node.getData().scoreMean;
        if (data.blackToPlay) {
          if (Lizzie.config.showKataGoBoardScoreMean)
            score = score + Lizzie.board.getHistory().getGameInfo().getKomi();
        } else {
          if (Lizzie.config.showKataGoBoardScoreMean)
            score = score - Lizzie.board.getHistory().getGameInfo().getKomi();
        }
        String wf =
            "%s "
                + Lizzie.resourceBundle.getString("SGFParse.winrate")
                + " %s %s\n"
                + (Lizzie.config.showKataGoBoardScoreMean
                    ? Lizzie.resourceBundle.getString("SGFParse.leadBoard")
                    : Lizzie.resourceBundle.getString("SGFParse.leadScore"))
                + " %s %s "
                + Lizzie.resourceBundle.getString("SGFParse.stdev")
                + " %s\n(%s / %s "
                + Lizzie.resourceBundle.getString("SGFParse.playouts")
                + ")\n"
                + Lizzie.resourceBundle.getString("SGFParse.komi")
                + " %s";
        double scoreStdev = 0;
        try {
          if (!data.bestMoves.isEmpty()) scoreStdev = node.getData().bestMoves.get(0).scoreStdev;
        } catch (Exception ex) {
          ex.printStackTrace();
        }
        nc =
            String.format(
                wf,
                blackWinrate
                    ? Lizzie.resourceBundle.getString("SGFParse.white")
                    : Lizzie.resourceBundle.getString("SGFParse.black"),
                String.format(Locale.ENGLISH, "%.1f%%", curWR),
                lastMoveDiff,
                String.format(Locale.ENGLISH, "%.1f", score),
                diffScore,
                String.format(Locale.ENGLISH, "%.1f", scoreStdev),
                engine,
                playouts,
                data.pda != 0
                    ? String.format(Locale.ENGLISH, "%.1f", data.getKomi())
                        + " "
                        + Lizzie.resourceBundle.getString("SGFParse.pda")
                        + data.pda
                    : String.format(Locale.ENGLISH, "%.1f", data.getKomi()));
      }
    } else {
      String wf =
          "%s "
              + Lizzie.resourceBundle.getString("SGFParse.winrate")
              + " %s %s\n(%s / %s "
              + Lizzie.resourceBundle.getString("SGFParse.playouts")
              + ")\n"
              + Lizzie.resourceBundle.getString("SGFParse.komi")
              + " %s";

      nc =
          String.format(
              wf,
              blackWinrate
                  ? Lizzie.resourceBundle.getString("SGFParse.white")
                  : Lizzie.resourceBundle.getString("SGFParse.black"),
              String.format(Locale.ENGLISH, "%.1f%%", curWR),
              lastMoveDiff,
              engine,
              playouts,
              String.format(Locale.ENGLISH, "%.1f", data.getKomi()));
    }
    return nc;
  }

  public static String formatCommentPk(BoardHistoryNode node) {
    if (!EngineManager.isSaveingEngineSGF && !node.previous().isPresent()) return "";
    BoardData data = node.getData();
    String engine = node.getData().engineName;
    engine = engine.replaceAll(" ", "");
    String playouts = Utils.getPlayoutsString(data.getPlayouts());
    // Last winrate
    Optional<BoardData> lastNode;
    if (node.previous().isPresent())
      lastNode = node.previous().get().previous().flatMap(n -> Optional.of(n.getData()));
    else lastNode = Optional.empty();
    boolean validLastWinrate = lastNode.isPresent();
    double lastWR = validLastWinrate ? lastNode.get().winrate : 50;
    boolean validWinrate = (data.getPlayouts() > 0);
    double curWR;
    curWR = validWinrate ? data.winrate : 100 - lastWR;
    String lastMoveDiff = "";
    if (validLastWinrate && validWinrate) {
      double diff = curWR - lastWR;
      lastMoveDiff = String.format("(%s%.1f%%)", diff > 0 ? "+" : "-", Math.abs(diff));
    }
    boolean blackWinrate = data.blackToPlay;
    String nc = "";
    if (data.isKataData) {
      String diffScore = "";
      if (validLastWinrate && validWinrate) {
        if (node.previous().get().previous().get().getData().getPlayouts() > 0) {
          double diff = data.scoreMean - node.previous().get().previous().get().getData().scoreMean;
          diffScore = String.format("(%s%.1f)", diff > 0 ? "+" : "-", Math.abs(diff));
        }
      }
      if (data.isSaiData) {
        double score = data.scoreMean;
        if (data.blackToPlay) {
          if (Lizzie.config.showKataGoBoardScoreMean)
            score = score + Lizzie.board.getHistory().getGameInfo().getKomi();
        } else {
          if (Lizzie.config.showKataGoBoardScoreMean)
            score = score - Lizzie.board.getHistory().getGameInfo().getKomi();
        }
        String wf =
            "%s "
                + Lizzie.resourceBundle.getString("SGFParse.winrate")
                + " %s %s\n"
                + (Lizzie.config.showKataGoBoardScoreMean
                    ? Lizzie.resourceBundle.getString("SGFParse.leadBoard")
                    : Lizzie.resourceBundle.getString("SGFParse.leadScore"))
                + " %s %s\n(%s / %s "
                + Lizzie.resourceBundle.getString("SGFParse.playouts")
                + ")\n"
                + Lizzie.resourceBundle.getString("SGFParse.komi")
                + " %s";
        nc =
            String.format(
                wf,
                blackWinrate
                    ? Lizzie.resourceBundle.getString("SGFParse.black")
                    : Lizzie.resourceBundle.getString("SGFParse.white"),
                String.format(Locale.ENGLISH, "%.1f%%", curWR),
                lastMoveDiff,
                String.format(Locale.ENGLISH, "%.1f", score),
                diffScore,
                engine,
                playouts,
                String.format(Locale.ENGLISH, "%.1f", data.getKomi()));
      } else {
        double score = node.getData().scoreMean;
        if (data.blackToPlay) {
          if (Lizzie.config.showKataGoBoardScoreMean)
            score = score + Lizzie.board.getHistory().getGameInfo().getKomi();
        } else {
          if (Lizzie.config.showKataGoBoardScoreMean)
            score = score - Lizzie.board.getHistory().getGameInfo().getKomi();
        }
        String wf =
            "%s "
                + Lizzie.resourceBundle.getString("SGFParse.winrate")
                + " %s %s\n"
                + (Lizzie.config.showKataGoBoardScoreMean
                    ? Lizzie.resourceBundle.getString("SGFParse.leadBoard")
                    : Lizzie.resourceBundle.getString("SGFParse.leadScore"))
                + " %s %s "
                + Lizzie.resourceBundle.getString("SGFParse.stdev")
                + " %s\n(%s / %s "
                + Lizzie.resourceBundle.getString("SGFParse.playouts")
                + ")\n"
                + Lizzie.resourceBundle.getString("SGFParse.komi")
                + " %s";
        double scoreStdev = 0;
        try {
          if (!data.bestMoves.isEmpty()) scoreStdev = node.getData().bestMoves.get(0).scoreStdev;
        } catch (Exception ex) {
          ex.printStackTrace();
        }
        nc =
            String.format(
                wf,
                blackWinrate
                    ? Lizzie.resourceBundle.getString("SGFParse.black")
                    : Lizzie.resourceBundle.getString("SGFParse.white"),
                String.format(Locale.ENGLISH, "%.1f%%", curWR),
                lastMoveDiff,
                String.format(Locale.ENGLISH, "%.1f", score),
                diffScore,
                String.format(Locale.ENGLISH, "%.1f", scoreStdev),
                engine,
                playouts,
                String.format(Locale.ENGLISH, "%.1f", data.getKomi())
                    + getPdaWrnString(data.pda, data.wrn));
      }
    } else {
      String wf =
          "%s "
              + Lizzie.resourceBundle.getString("SGFParse.winrate")
              + " %s %s\n(%s / %s "
              + Lizzie.resourceBundle.getString("SGFParse.playouts")
              + ")\n"
              + Lizzie.resourceBundle.getString("SGFParse.komi")
              + " %s";

      nc =
          String.format(
              wf,
              blackWinrate
                  ? Lizzie.resourceBundle.getString("SGFParse.black")
                  : Lizzie.resourceBundle.getString("SGFParse.white"),
              String.format(Locale.ENGLISH, "%.1f%%", curWR),
              lastMoveDiff,
              engine,
              playouts,
              String.format(Locale.ENGLISH, "%.1f", data.getKomi()));
    }

    if (!data.comment.isEmpty() && !EngineManager.isSaveingEngineSGF) {
      // [^\\(\\)/]*
      String wp = "";
      if (!data.isKataData) {
        wp =
            "("
                + Lizzie.resourceBundle.getString("SGFParse.black")
                + " |"
                + Lizzie.resourceBundle.getString("SGFParse.white")
                + " )"
                + Lizzie.resourceBundle.getString("SGFParse.winrate")
                + " [0-9\\.\\-]+%* \\(*[0-9.\\-+]*%*\\)*\n\\("
                + ".*"
                + " / [0-9\\.]*[kmKM]* "
                + Lizzie.resourceBundle.getString("SGFParse.playouts")
                + "\\)\\n"
                + Lizzie.resourceBundle.getString("SGFParse.komi")
                + ".*";
      } else {
        if (data.isSaiData)
          wp =
              "("
                  + Lizzie.resourceBundle.getString("SGFParse.black")
                  + " |"
                  + Lizzie.resourceBundle.getString("SGFParse.white")
                  + " )"
                  + Lizzie.resourceBundle.getString("SGFParse.winrate")
                  + " [0-9\\.\\-]+%* \\(*[0-9.\\-+]*%*\\)*\n"
                  + (Lizzie.config.showKataGoBoardScoreMean
                      ? Lizzie.resourceBundle.getString("SGFParse.leadBoard")
                      : Lizzie.resourceBundle.getString("SGFParse.leadScore"))
                  + " [0-9\\.\\-+]* \\(*[0-9.\\-+]*\\)*\n\\("
                  + ".*"
                  + " / [0-9\\.]*[kmKM]* "
                  + Lizzie.resourceBundle.getString("SGFParse.playouts")
                  + "\\)\\n"
                  + Lizzie.resourceBundle.getString("SGFParse.komi")
                  + ".*";
        else
          wp =
              "("
                  + Lizzie.resourceBundle.getString("SGFParse.black")
                  + " |"
                  + Lizzie.resourceBundle.getString("SGFParse.white")
                  + " )"
                  + Lizzie.resourceBundle.getString("SGFParse.winrate")
                  + " [0-9\\.\\-]+%* \\(*[0-9.\\-+]*%*\\)*\n"
                  + (Lizzie.config.showKataGoBoardScoreMean
                      ? Lizzie.resourceBundle.getString("SGFParse.leadBoard")
                      : Lizzie.resourceBundle.getString("SGFParse.leadScore"))
                  + " [0-9\\.\\-+]* \\(*[0-9.\\-+]*\\)* "
                  + Lizzie.resourceBundle.getString("SGFParse.stdev")
                  + " [0-9\\.\\-+]*\n\\("
                  + ".*"
                  + " / [0-9\\.]*[kmKM]* "
                  + Lizzie.resourceBundle.getString("SGFParse.playouts")
                  + "\\)\\n"
                  + Lizzie.resourceBundle.getString("SGFParse.komi")
                  + ".*";
      }
      // if (Lizzie.leelaz.isKatago) wp = wp + "\n.*";
      if (data.comment.matches("(?s).*" + wp + "(?s).*")) {
        nc = data.comment.replaceAll(wp, nc);
      } else {
        nc = String.format("%s\n\n%s", data.comment, nc);
      }
    }
    return nc;
  }

  private static String getPdaWrnString(double pda, double wrn) {
    // TODO Auto-generated method stub
    String line = pda != 0 ? " " + Lizzie.resourceBundle.getString("SGFParse.pda") + pda : "";
    line += wrn != 0 ? " " + Lizzie.resourceBundle.getString("SGFParse.wrn") + wrn : "";
    return line;
  }

  private static String formatComment2(BoardHistoryNode node) {
    //    if (node.getData().commented2) return node.getData().comment;
    //    node.getData().commented2 = true;
    BoardData data = node.getData();
    String engine = node.getData().engineName2;
    engine = engine.replaceAll(" ", "");
    String playouts = Utils.getPlayoutsString(data.getPlayouts2());

    Optional<BoardData> lastNode = node.previous().flatMap(n -> Optional.of(n.getData()));
    boolean validLastWinrate = lastNode.isPresent();

    double lastWR = validLastWinrate ? lastNode.get().getWinrate2() : 50;
    if (EngineManager.isEngineGame && node.moveNumberOfNode() > 2) {
      lastWR = 100 - lastWR;
    }
    boolean validWinrate = (data.getPlayouts2() > 0);
    double curWR;
    if (Lizzie.config.winrateAlwaysBlack) {
      curWR = validWinrate ? data.getWinrate2() : lastWR;
    } else {
      curWR = validWinrate ? data.getWinrate2() : 100 - lastWR;
    }

    String lastMoveDiff = "";
    if (validLastWinrate && validWinrate) {
      //	      if (Lizzie.config.handicapInsteadOfWinrate) {
      //	        double currHandicapedWR = Leelaz.winrateToHandicap(100 - curWR);
      //	        double lastHandicapedWR = Leelaz.winrateToHandicap(lastWR);
      //	        lastMoveDiff = String.format(": %.2f", currHandicapedWR - lastHandicapedWR);
      //	      } else {
      double diff;
      if (Lizzie.config.winrateAlwaysBlack) {
        diff = lastWR - curWR;
      } else {
        diff = 100 - lastWR - curWR;
      }
      lastMoveDiff = String.format("(%s%.1f%%)", diff > 0 ? "+" : "-", Math.abs(diff));
      //  }
    }
    boolean blackWinrate = !data.blackToPlay || Lizzie.config.winrateAlwaysBlack;
    String nc = "";

    if (data.isKataData2) {
      String diffScore = "";
      if (validLastWinrate && validWinrate) {
        if (node.previous().get().getData().getPlayouts() > 0) {
          double diff = data.scoreMean + node.previous().get().getData().scoreMean;
          if (Lizzie.config.winrateAlwaysBlack && Lizzie.board.getHistory().isBlacksTurn())
            diff = -diff;
          diffScore = String.format("(%s%.1f)", diff > 0 ? "+" : "-", Math.abs(diff));
        }
      }
      if (data.isSaiData2) {
        double score = data.scoreMean2;
        if (data.blackToPlay) {
          if (Lizzie.config.showKataGoBoardScoreMean)
            score = score + Lizzie.board.getHistory().getGameInfo().getKomi();
          else score = -score;
        } else {
          if (Lizzie.config.showKataGoBoardScoreMean)
            score = score - Lizzie.board.getHistory().getGameInfo().getKomi();
          else score = -score;
        }
        String wf =
            "%s "
                + Lizzie.resourceBundle.getString("SGFParse.winrate")
                + " %s %s\n"
                + (Lizzie.config.showKataGoBoardScoreMean
                    ? Lizzie.resourceBundle.getString("SGFParse.leadBoard")
                    : Lizzie.resourceBundle.getString("SGFParse.leadScore"))
                + " %s %s\n(%s / %s "
                + Lizzie.resourceBundle.getString("SGFParse.playouts")
                + ")\n"
                + Lizzie.resourceBundle.getString("SGFParse.komi")
                + " %s";
        nc =
            String.format(
                wf,
                blackWinrate
                    ? Lizzie.resourceBundle.getString("SGFParse.black")
                    : Lizzie.resourceBundle.getString("SGFParse.white"),
                String.format(Locale.ENGLISH, "%.1f%%", 100 - curWR),
                lastMoveDiff,
                String.format(Locale.ENGLISH, "%.1f", score),
                diffScore,
                engine,
                playouts,
                String.format(Locale.ENGLISH, "%.1f", data.getKomi()));
      } else {
        double score = data.scoreMean2;
        if (data.blackToPlay) {
          if (Lizzie.config.showKataGoBoardScoreMean)
            score = score + Lizzie.board.getHistory().getGameInfo().getKomi();
          else score = -score;
        } else {
          if (Lizzie.config.showKataGoBoardScoreMean)
            score = score - Lizzie.board.getHistory().getGameInfo().getKomi();
          else score = -score;
        }
        String wf =
            "%s "
                + Lizzie.resourceBundle.getString("SGFParse.winrate")
                + " %s %s\n"
                + (Lizzie.config.showKataGoBoardScoreMean
                    ? Lizzie.resourceBundle.getString("SGFParse.leadBoard")
                    : Lizzie.resourceBundle.getString("SGFParse.leadScore"))
                + " %s %s "
                + Lizzie.resourceBundle.getString("SGFParse.stdev")
                + " %s\n(%s / %s "
                + Lizzie.resourceBundle.getString("SGFParse.playouts")
                + ")\n"
                + Lizzie.resourceBundle.getString("SGFParse.komi")
                + " %s";
        double scoreStdev = 0;
        try {
          if (!data.bestMoves2.isEmpty()) scoreStdev = data.bestMoves2.get(0).scoreStdev;
        } catch (Exception ex) {
          ex.printStackTrace();
        }
        nc =
            String.format(
                wf,
                blackWinrate
                    ? Lizzie.resourceBundle.getString("SGFParse.black")
                    : Lizzie.resourceBundle.getString("SGFParse.white"),
                String.format(Locale.ENGLISH, "%.1f%%", 100 - curWR),
                lastMoveDiff,
                String.format(Locale.ENGLISH, "%.1f", score),
                diffScore,
                String.format(Locale.ENGLISH, "%.1f", scoreStdev),
                engine,
                playouts,
                data.pda2 != 0
                    ? String.format(Locale.ENGLISH, "%.1f", data.getKomi())
                        + " "
                        + Lizzie.resourceBundle.getString("SGFParse.pda")
                        + data.pda2
                    : String.format(Locale.ENGLISH, "%.1f", data.getKomi()));
      }
    } else {
      String wf =
          "%s "
              + Lizzie.resourceBundle.getString("SGFParse.winrate")
              + " %s %s\n(%s / %s "
              + Lizzie.resourceBundle.getString("SGFParse.playouts")
              + ")\n"
              + Lizzie.resourceBundle.getString("SGFParse.komi")
              + " %s";

      nc =
          String.format(
              wf,
              blackWinrate
                  ? Lizzie.resourceBundle.getString("SGFParse.black")
                  : Lizzie.resourceBundle.getString("SGFParse.white"),
              String.format(Locale.ENGLISH, "%.1f%%", 100 - curWR),
              lastMoveDiff,
              engine,
              playouts,
              String.format(Locale.ENGLISH, "%.1f", data.getKomi()));
    }

    if (!data.comment.isEmpty()) {
      // [^\\(\\)/]*
      String wp = "";
      if (!data.isKataData) {
        wp =
            "("
                + Lizzie.resourceBundle.getString("SGFParse.black")
                + " |"
                + Lizzie.resourceBundle.getString("SGFParse.white")
                + " )"
                + Lizzie.resourceBundle.getString("SGFParse.winrate")
                + " [0-9\\.\\-]+%* \\(*[0-9.\\-+]*%*\\)*\n\\("
                + ".*"
                + " / [0-9\\.]*[kmKM]* "
                + Lizzie.resourceBundle.getString("SGFParse.playouts")
                + "\\)\\n"
                + Lizzie.resourceBundle.getString("SGFParse.komi")
                + ".*";
      } else {
        if (data.isSaiData)
          wp =
              "("
                  + Lizzie.resourceBundle.getString("SGFParse.black")
                  + " |"
                  + Lizzie.resourceBundle.getString("SGFParse.white")
                  + " )"
                  + Lizzie.resourceBundle.getString("SGFParse.winrate")
                  + " [0-9\\.\\-]+%* \\(*[0-9.\\-+]*%*\\)*\n"
                  + (Lizzie.config.showKataGoBoardScoreMean
                      ? Lizzie.resourceBundle.getString("SGFParse.leadBoard")
                      : Lizzie.resourceBundle.getString("SGFParse.leadScore"))
                  + " [0-9\\.\\-+]* \\(*[0-9.\\-+]*\\)*\n\\("
                  + ".*"
                  + " / [0-9\\.]*[kmKM]* "
                  + Lizzie.resourceBundle.getString("SGFParse.playouts")
                  + "\\)\\n"
                  + Lizzie.resourceBundle.getString("SGFParse.komi")
                  + ".*";
        else
          wp =
              "("
                  + Lizzie.resourceBundle.getString("SGFParse.black")
                  + " |"
                  + Lizzie.resourceBundle.getString("SGFParse.white")
                  + " )"
                  + Lizzie.resourceBundle.getString("SGFParse.winrate")
                  + " [0-9\\.\\-]+%* \\(*[0-9.\\-+]*%*\\)*\n"
                  + (Lizzie.config.showKataGoBoardScoreMean
                      ? Lizzie.resourceBundle.getString("SGFParse.leadBoard")
                      : Lizzie.resourceBundle.getString("SGFParse.leadScore"))
                  + " [0-9\\.\\-+]* \\(*[0-9.\\-+]*\\)* "
                  + Lizzie.resourceBundle.getString("SGFParse.stdev")
                  + " [0-9\\.\\-+]*\n\\("
                  + ".*"
                  + " / [0-9\\.]*[kmKM]* "
                  + Lizzie.resourceBundle.getString("SGFParse.playouts")
                  + "\\)\\n"
                  + Lizzie.resourceBundle.getString("SGFParse.komi")
                  + ".*";
      }
      // if (Lizzie.leelaz.isKatago) wp = wp + "\n.*";
      if (data.comment.matches("(?s).*" + wp + "(?s).*")) {
        nc = data.comment.replaceAll(wp, nc);
      } else {
        nc = String.format("%s\n\n%s", data.comment, nc);
      }
    }
    return nc;
  }

  /** Format Comment with following format: <Winrate> <Playouts> */
  private static String formatNodeData(BoardHistoryNode node) {
    BoardData data = node.getData();

    // Playouts
    String playouts = Utils.getPlayoutsString(data.getPlayouts());

    // Last winrate
    Optional<BoardData> lastNode = node.previous().flatMap(n -> Optional.of(n.getData()));
    boolean validLastWinrate = lastNode.map(d -> d.getPlayouts() > 0).orElse(false);
    double lastWR = validLastWinrate ? lastNode.get().winrate : 50;

    // Current winrate
    boolean validWinrate = (data.getPlayouts() > 0);
    double curWR = validWinrate ? data.winrate : 100 - lastWR;
    String curWinrate = "";
    curWinrate = String.format(Locale.ENGLISH, "%.1f", 100 - curWR);

    if (data.isKataData) {
      if (data.pda != 0) {
        String wf = "%s %s %s %s %s %s\n%s";
        return String.format(
            wf,
            data.engineName,
            curWinrate,
            playouts,
            String.format(Locale.ENGLISH, "%.1f", data.scoreMean),
            String.format(Locale.ENGLISH, "%.1f", data.scoreStdev),
            data.pda,
            data.bestMovesToString());
      } else {
        String wf = "%s %s %s %s %s\n%s";
        return String.format(
            wf,
            data.engineName,
            curWinrate,
            playouts,
            String.format(Locale.ENGLISH, "%.1f", data.scoreMean),
            String.format(Locale.ENGLISH, "%.1f", data.scoreStdev),
            data.bestMovesToString());
      }
    }

    String wf = "%s %s %s\n%s";

    return String.format(
        wf, data.engineName.replaceAll(" ", ""), curWinrate, playouts, data.bestMovesToString());
  }

  private static String formatNodeData2(BoardHistoryNode node) {
    BoardData data = node.getData();

    // Playouts
    String playouts = Utils.getPlayoutsString(data.getPlayouts2());

    // Last winrate
    Optional<BoardData> lastNode = node.previous().flatMap(n -> Optional.of(n.getData()));
    boolean validLastWinrate = lastNode.map(d -> d.getPlayouts2() > 0).orElse(false);
    double lastWR = validLastWinrate ? lastNode.get().winrate2 : 50;

    // Current winrate
    boolean validWinrate = (data.getPlayouts2() > 0);
    double curWR = validWinrate ? data.winrate2 : 100 - lastWR;
    String curWinrate = "";
    curWinrate = String.format(Locale.ENGLISH, "%.1f", 100 - curWR);

    if (data.isKataData2) {
      if (data.pda != 0) {
        String wf = "%s %s %s %s %s %s\n%s";
        return String.format(
            wf,
            data.engineName2,
            curWinrate,
            playouts,
            String.format(Locale.ENGLISH, "%.1f", data.scoreMean2),
            String.format(Locale.ENGLISH, "%.1f", data.scoreStdev2),
            data.pda2,
            data.bestMovesToString2());
      } else {
        String wf = "%s %s %s %s %s\n%s";
        return String.format(
            wf,
            data.engineName2,
            curWinrate,
            playouts,
            String.format(Locale.ENGLISH, "%.1f", data.scoreMean2),
            String.format(Locale.ENGLISH, "%.1f", data.scoreStdev2),
            data.bestMovesToString2());
      }
    }

    String wf = "%s %s %s\n%s";

    return String.format(wf, data.engineName2, curWinrate, playouts, data.bestMovesToString2());
  }

  public static boolean isListProperty(String key) {
    return asList(listProps).contains(key);
  }

  public static boolean isMarkupProperty(String key) {
    return asList(markupProps).contains(key);
  }

  /**
   * Get a value with key, or the default if there is no such key
   *
   * @param key
   * @param defaultValue
   * @return
   */
  public static String getOrDefault(Map<String, String> props, String key, String defaultValue) {
    return props.getOrDefault(key, defaultValue);
  }

  /**
   * Add a key and value to the props
   *
   * @param key
   * @param value
   */
  public static void addProperty(Map<String, String> props, String key, String value) {
    if (SGFParser.isListProperty(key)) {
      // Label and add/remove stones
      props.merge(key, value, (old, val) -> old + "," + val);
    } else {
      props.put(key, value);
    }
  }

  /**
   * Add the properties by mutating the props
   *
   * @return
   */
  public static void addProperties(Map<String, String> props, Map<String, String> addProps) {
    addProps.forEach((key, value) -> addProperty(props, key, value));
  }

  /**
   * Add the properties from string
   *
   * @return
   */
  public static void addProperties(Map<String, String> props, String propsStr) {
    boolean inTag = false, escaping = false;
    String tag = "";
    StringBuilder tagBuilder = new StringBuilder();
    StringBuilder tagContentBuilder = new StringBuilder();

    for (int i = 0; i < propsStr.length(); i++) {
      char c = propsStr.charAt(i);
      if (escaping) {
        tagContentBuilder.append(c);
        escaping = false;
        continue;
      }
      switch (c) {
        case '(':
          if (inTag) {
            if (i > 0) {
              tagContentBuilder.append(c);
            }
          }
          break;
        case ';':
        case ')':
          if (inTag) {
            tagContentBuilder.append(c);
          }
          break;
        case '[':
          inTag = true;
          String tagTemp = tagBuilder.toString();
          if (!tagTemp.isEmpty()) {
            tag = tagTemp.replaceAll("[a-z]", "");
          }
          tagContentBuilder = new StringBuilder();
          break;
        case ']':
          inTag = false;
          tagBuilder = new StringBuilder();
          addProperty(props, tag, tagContentBuilder.toString());
          break;
        default:
          if (inTag) {
            if (c == '\\') {
              escaping = true;
              continue;
            }
            tagContentBuilder.append(c);
          } else {
            if (c != '\n' && c != '\r' && c != '\t' && c != ' ') {
              tagBuilder.append(c);
            }
          }
      }
    }
  }

  /**
   * Get properties string by the props
   *
   * @return
   */
  public static String propertiesString(Map<String, String> props) {
    StringBuilder sb = new StringBuilder();
    props.forEach((key, value) -> sb.append(nodeString(key, value)));
    return sb.toString();
  }

  /**
   * Get node string by the key and value
   *
   * @param key
   * @param value
   * @return
   */
  public static String nodeString(String key, String value) {
    StringBuilder sb = new StringBuilder();
    if (SGFParser.isListProperty(key)) {
      // Label and add/remove stones
      sb.append(key);
      String[] vals = value.split(",");
      for (String val : vals) {
        sb.append("[").append(val).append("]");
      }
    } else {
      sb.append(key).append("[").append(value).append("]");
    }
    return sb.toString();
  }

  private static void processPendingPros(BoardHistoryList history, Map<String, String> props) {
    props.forEach((key, value) -> history.addNodeProperty(key, value));
    props = new HashMap<String, String>();
  }

  public static String Escaping(String in) {
    String out = in.replaceAll("\\\\", "\\\\\\\\");
    return out.replaceAll("\\]", "\\\\]");
  }

  public static BoardHistoryList parseSgf(String value, boolean first) {
    BoardHistoryList history = null;

    // Drop anything outside "(;...)"
    final Pattern SGF_PATTERN = Pattern.compile("(?s).*?(\\(\\s*;{0,1}.*\\))(?s).*?");
    Matcher sgfMatcher = SGF_PATTERN.matcher(value);
    if (sgfMatcher.matches()) {
      value = sgfMatcher.group(1);
    } else {
      return history;
    }

    // Determine the SZ property
    Pattern szPattern = Pattern.compile("(?s).*?SZ\\[(\\d+)\\](?s).*");
    Matcher szMatcher = szPattern.matcher(value);
    int boardWidth = 19;
    int boardHeight = 19;
    if (szMatcher.matches()) {
      String sizeStr = szMatcher.group(1);
      Pattern sizePattern = Pattern.compile("([\\d]+):([\\d]+)");
      Matcher sizeMatcher = sizePattern.matcher(sizeStr);
      if (sizeMatcher.matches()) {
        boardWidth = Integer.parseInt(sizeMatcher.group(1));
        boardHeight = Integer.parseInt(sizeMatcher.group(2));
      } else {
        boardWidth = boardHeight = Integer.parseInt(sizeStr);
      }
    }
    history = new BoardHistoryList(BoardData.empty(boardWidth, boardHeight));

    parseValue(value, history, false, first);

    return history;
  }

  private static BoardHistoryList parseValue(
      String value, BoardHistoryList history, boolean isBranch, boolean firstTime) {

    int subTreeDepth = 0;
    // Save the variation step count
    Map<Integer, BoardHistoryNode> subTreeStepMap = new HashMap<Integer, BoardHistoryNode>();
    // Comment of the game head
    String headComment = "";
    // Game properties
    Map<String, String> gameProperties = new HashMap<String, String>();
    Map<String, String> pendingProps = new HashMap<String, String>();
    boolean inTag = false,
        isMultiGo = false,
        escaping = false,
        moveStart = false,
        addPassForMove = true;
    boolean inProp = false;
    String tag = "";
    StringBuilder tagBuilder = new StringBuilder();
    StringBuilder tagContentBuilder = new StringBuilder();
    // MultiGo 's branch: (Main Branch (Main Branch) (Branch) )
    // Other 's branch: (Main Branch (Branch) Main Branch)
    if (value.matches("(?s).*\\)\\s*\\)")) {
      isMultiGo = true;
    }
    if (isBranch) {
      subTreeDepth += 1;
      // Initialize the step count
      subTreeStepMap.put(subTreeDepth, history.getCurrentHistoryNode());
    }

    String blackPlayer = "", whitePlayer = "";

    // Support unicode characters (UTF-8)
    for (int i = 0; i < value.length(); i++) {
      char c = value.charAt(i);
      if (escaping) {
        // Any char following "\" is inserted verbatim
        // (ref) "3.2. Text" in https://www.red-bean.com/sgf/sgf4.html
        tagContentBuilder.append(c == 'n' ? "\n" : c);
        escaping = false;
        continue;
      }
      switch (c) {
        case '(':
          if (!inTag) {
            subTreeDepth += 1;
            // Initialize the step count
            subTreeStepMap.put(subTreeDepth, history.getCurrentHistoryNode());
            addPassForMove = true;
            pendingProps = new HashMap<String, String>();
          } else {
            if (i > 0) {
              // Allow the comment tag includes '('
              tagContentBuilder.append(c);
            }
          }
          break;
        case ')':
          if (!inTag) {
            if (isMultiGo) {
              // Restore to the variation node
              // int varStep = subTreeStepMap.get(subTreeDepth);
              //   for (int s = 0; s < varStep; s++)
              while (history.getCurrentHistoryNode() != subTreeStepMap.get(subTreeDepth)) {
                if (!history.previous().isPresent()) break;
              }
            }
            subTreeDepth -= 1;
          } else {
            // Allow the comment tag includes '('
            tagContentBuilder.append(c);
          }
          break;
        case '[':
          if (!inProp) {
            inProp = true;
            if (subTreeDepth > 1 && !isMultiGo) {
              break;
            }
            inTag = true;
            String tagTemp = tagBuilder.toString();
            if (!tagTemp.isEmpty()) {
              // Ignore small letters in tags for the long format Smart-Go file.
              // (ex) "PlayerBlack" ==> "PB"
              // It is the default format of mgt, an old SGF tool.
              // (Mgt is still supported in Debian and Ubuntu.)
              tag = tagTemp.replaceAll("[a-z]", "");
            }
            tagContentBuilder = new StringBuilder();
          } else {
            tagContentBuilder.append(c);
          }
          break;
        case ']':
          if (subTreeDepth > 1 && !isMultiGo) {
            break;
          }
          inTag = false;
          inProp = false;
          tagBuilder = new StringBuilder();
          String tagContent = tagContentBuilder.toString();
          // We got tag, we can parse this tag now.
          if (tag.equals("B") || tag.equals("W")) {
            moveStart = true;
            addPassForMove = true;
            int[] move = convertSgfPosToCoord(tagContent);
            // Save the step count
            //  subTreeStepMap.put(subTreeDepth, subTreeStepMap.get(subTreeDepth) + 1);
            Stone color = tag.equals("B") ? Stone.BLACK : Stone.WHITE;
            // boolean newBranch = (subTreeStepMap.get(subTreeDepth) == 1);
            boolean newBranch = history.getCurrentHistoryNode().hasVariations();
            if (move == null) {
              history.pass(color, newBranch, false);

            } else {
              history.place(move[0], move[1], color, newBranch);
            }
            if (newBranch) {
              processPendingPros(history, pendingProps);
            }
          } else if (tag.equals("C")) {
            // Support comment
            if (!moveStart) {
              headComment = tagContent;
            } else {
              history.getData().comment = tagContent;
            }
          } else if (tag.equals("LZ") && history == null) {
            // Content contains data for Lizzie to read
            String[] lines = tagContent.split("\n");
            String[] line1 = lines[0].split(" ");
            String line2 = "";
            if (lines.length > 1) {
              line2 = lines[1];
            }
            String engname = line1[0];
            Lizzie.board.getData().engineName = engname;
            Lizzie.board.getData().winrate = 100 - Double.parseDouble(line1[1]);
            int numPlayouts =
                Integer.parseInt(
                    line1[2]
                        .replaceAll("k", "000")
                        .replaceAll("m", "000000")
                        .replaceAll("[^0-9]", ""));
            if (numPlayouts > 0 && !line2.isEmpty()) {
              Lizzie.board.getData().bestMoves = Lizzie.leelaz.parseInfo(line2);
            }
          } else if (tag.equals("AB") || tag.equals("AW")) {
            int[] move = convertSgfPosToCoord(tagContent);
            Stone color = tag.equals("AB") ? Stone.BLACK : Stone.WHITE;
            if (moveStart) {
              // add to node properties
              history.addNodeProperty(tag, tagContent);
              if (addPassForMove) {
                // Save the step count
                //  subTreeStepMap.put(subTreeDepth, subTreeStepMap.get(subTreeDepth) + 1);
                boolean newBranch = history.getCurrentHistoryNode().hasVariations();
                history.pass(color, newBranch, true);
                if (newBranch) {
                  processPendingPros(history, pendingProps);
                }
                addPassForMove = false;
              }
              history.addNodeProperty(tag, tagContent);
              if (move != null) {
                history.addExtraStone(move[0], move[1], color);
              }
            } else {
              if (move == null) {
                history.pass(color);
              } else {
                history.place(move[0], move[1], color);
              }
              history.flatten();
            }
            Lizzie.leelaz.playMove(color, Board.convertCoordinatesToName(move[0], move[1]));
            if (!moveStart) {
              Lizzie.board.hasStartStone = true;
              if (color == Stone.BLACK) addStartList(true, move[0], move[1]);
              else addStartList(false, move[0], move[1]);
            }
          } else if (tag.equals("PB")) {
            blackPlayer = tagContent;
            history.getGameInfo().setPlayerBlack(blackPlayer);
          } else if (tag.equals("PW")) {
            whitePlayer = tagContent;
            history.getGameInfo().setPlayerWhite(whitePlayer);
          } else if (tag.equals("KM") && Lizzie.config.readKomi) {
            if (firstTime) {
              try {
                if (!tagContent.trim().isEmpty()) {
                  Double komi = Double.parseDouble(tagContent);
                  if (komi >= 200) {
                    komi = komi / 100;
                    if (komi == 3.5) komi = 7.0;
                  }
                  if (komi.toString().endsWith(".75") || komi.toString().endsWith(".25"))
                    komi = komi * 2;
                  if (Math.abs(komi) < Board.boardWidth * Board.boardHeight) {
                    Lizzie.leelaz.komi(komi);
                    history.getGameInfo().setKomi(komi);
                  }
                }
              } catch (NumberFormatException e) {
                e.printStackTrace();
              }
            }
          } else if (tag.equals("HA")) {
            try {
              if (tagContent.trim().isEmpty()) {
                tagContent = "0";
              }
              int handicap = Integer.parseInt(tagContent);
              history.getGameInfo().setHandicap(handicap);
            } catch (NumberFormatException e) {
              e.printStackTrace();
            }
          } else {
            if (moveStart) {
              // Other SGF node properties
              if ("AE".equals(tag)) {
                // remove a stone
                if (addPassForMove) {
                  // Save the step count
                  // subTreeStepMap.put(subTreeDepth, subTreeStepMap.get(subTreeDepth) + 1);
                  Stone color =
                      history.getLastMoveColor() == Stone.WHITE ? Stone.BLACK : Stone.WHITE;
                  boolean newBranch = history.getCurrentHistoryNode().hasVariations();
                  history.pass(color, newBranch, true);
                  if (newBranch) {
                    processPendingPros(history, pendingProps);
                  }
                  addPassForMove = false;
                }
                history.addNodeProperty(tag, tagContent);
                int[] move = convertSgfPosToCoord(tagContent);
                if (move != null) {
                  history.removeStone(
                      move[0], move[1], tag.equals("AB") ? Stone.BLACK : Stone.WHITE);
                }
              } else {
                boolean firstProp = (subTreeStepMap.get(subTreeDepth).hasVariations());
                if (firstProp) {
                  addProperty(pendingProps, tag, tagContent);
                } else {
                  history.addNodeProperty(tag, tagContent);
                }
              }
            } else {
              if ("N".equals(tag) && headComment.isEmpty()) headComment = tagContent;
              else addProperty(gameProperties, tag, tagContent);
            }
          }
          break;
        case ';':
          if (inProp) {
            // support C[a;b;c;]
            tagContentBuilder.append(c);
          }
          break;
        default:
          if (subTreeDepth > 1 && !isMultiGo) {
            break;
          }
          if (inTag) {
            if (c == '\\') {
              escaping = true;
              continue;
            }
            tagContentBuilder.append(c);
          } else {
            if (c != '\n' && c != '\r' && c != '\t' && c != ' ') {
              tagBuilder.append(c);
            }
          }
      }
    }

    if (isBranch) {
      history.toBranchTop();
    } else {
      Lizzie.frame.setPlayers(whitePlayer, blackPlayer);
      if (!Utils.isBlank(gameProperties.get("RE")) && Utils.isBlank(history.getData().comment)) {
        history.getData().comment = gameProperties.get("RE");
      }

      // Rewind to game start
      while (history.previous().isPresent()) ;

      // Set AW/AB Comment
      if (!headComment.isEmpty()) {
        history.getData().comment = headComment;
      }
      if (gameProperties.size() > 0) {
        history.getData().addProperties(gameProperties);
      }
    }
    return history;
  }

  public static void addStartList(boolean isBlack, int x, int y) {
    Movelist move = new Movelist();
    move.x = x;
    move.y = y;
    move.ispass = false;
    move.isblack = isBlack;
    move.movenum = Lizzie.board.startStonelist.size() + 1;
    Lizzie.board.startStonelist.add(move);
  }

  public static int parseBranch(BoardHistoryList history, String value) {
    int subTreeDepth = 0;
    // Save the variation step count
    Map<Integer, Integer> subTreeStepMap = new HashMap<Integer, Integer>();
    // Comment of the game head
    String headComment = "";
    // Game properties
    Map<String, String> gameProperties = new HashMap<String, String>();
    Map<String, String> pendingProps = new HashMap<String, String>();
    boolean inTag = false,
        isMultiGo = false,
        escaping = false,
        moveStart = false,
        addPassForMove = true;
    boolean inProp = false;
    String tag = "";
    StringBuilder tagBuilder = new StringBuilder();
    StringBuilder tagContentBuilder = new StringBuilder();
    // MultiGo 's branch: (Main Branch (Main Branch) (Branch) )
    // Other 's branch: (Main Branch (Branch) Main Branch)
    if (value.matches("(?s).*\\)\\s*\\)")) {
      isMultiGo = true;
    }
    subTreeDepth += 1;
    // Initialize the step count
    subTreeStepMap.put(subTreeDepth, 0);

    for (int i = 0; i < value.length(); i++) {
      char c = value.charAt(i);
      if (escaping) {
        tagContentBuilder.append(c == 'n' ? "\n" : c);
        escaping = false;
        continue;
      }
      switch (c) {
        case '(':
          if (!inTag) {
            subTreeDepth += 1;
            // Initialize the step count
            subTreeStepMap.put(subTreeDepth, 0);
            addPassForMove = true;
            pendingProps = new HashMap<String, String>();
          } else {
            if (i > 0) {
              // Allow the comment tag includes '('
              tagContentBuilder.append(c);
            }
          }
          break;
        case ')':
          if (!inTag) {
            if (isMultiGo) {
              // Restore to the variation node
              int varStep = subTreeStepMap.get(subTreeDepth);
              for (int s = 0; s < varStep; s++) {
                history.previous();
              }
            }
            subTreeDepth -= 1;
          } else {
            // Allow the comment tag includes '('
            tagContentBuilder.append(c);
          }
          break;
        case '[':
          if (!inProp) {
            inProp = true;
            if (subTreeDepth > 1 && !isMultiGo) {
              break;
            }
            inTag = true;
            String tagTemp = tagBuilder.toString();
            if (!tagTemp.isEmpty()) {
              // Ignore small letters in tags for the long format Smart-Go file.
              // (ex) "PlayerBlack" ==> "PB"
              // It is the default format of mgt, an old SGF tool.
              // (Mgt is still supported in Debian and Ubuntu.)
              tag = tagTemp.replaceAll("[a-z]", "");
            }
            tagContentBuilder = new StringBuilder();
          } else {
            tagContentBuilder.append(c);
          }
          break;
        case ']':
          if (subTreeDepth > 1 && !isMultiGo) {
            break;
          }
          inTag = false;
          inProp = false;
          tagBuilder = new StringBuilder();
          String tagContent = tagContentBuilder.toString();
          // We got tag, we can parse this tag now.
          if (tag.equals("B") || tag.equals("W")) {
            moveStart = true;
            addPassForMove = true;
            int[] move = convertSgfPosToCoord(tagContent);
            // Save the step count
            subTreeStepMap.put(subTreeDepth, subTreeStepMap.get(subTreeDepth) + 1);
            Stone color = tag.equals("B") ? Stone.BLACK : Stone.WHITE;
            boolean newBranch = (subTreeStepMap.get(subTreeDepth) == 1);
            if (move == null) {
              history.pass(color, newBranch, false);
            } else {
              history.place(move[0], move[1], color, newBranch);
            }
            if (newBranch) {
              processPendingPros(history, pendingProps);
            }
          } else if (tag.equals("C")) {
            // Support comment
            if (!moveStart) {
              headComment = tagContent;
            } else {
              history.getData().comment = tagContent;
            }
          } else if (tag.equals("AB") || tag.equals("AW")) {
            int[] move = convertSgfPosToCoord(tagContent);
            Stone color = tag.equals("AB") ? Stone.BLACK : Stone.WHITE;
            if (moveStart) {
              // add to node properties
              history.addNodeProperty(tag, tagContent);
              if (addPassForMove) {
                // Save the step count
                subTreeStepMap.put(subTreeDepth, subTreeStepMap.get(subTreeDepth) + 1);
                boolean newBranch = (subTreeStepMap.get(subTreeDepth) == 1);
                history.pass(color, newBranch, true);
                if (newBranch) {
                  processPendingPros(history, pendingProps);
                }
                addPassForMove = false;
              }
              history.addNodeProperty(tag, tagContent);
              if (move != null) {
                // history.addStone(move[0], move[1], color);
                history.place(move[0], move[1], color);
              }
            } else {
              if (move == null) {
                history.pass(color);
              } else {
                history.place(move[0], move[1], color);
              }
              // history.flatten();
            }
            Lizzie.leelaz.playMove(color, Board.convertCoordinatesToName(move[0], move[1]));
            if (!moveStart) {
              Lizzie.board.hasStartStone = true;
              if (color == Stone.BLACK) addStartList(true, move[0], move[1]);
              else addStartList(false, move[0], move[1]);
            }
          } else if (tag.equals("PB")) {
          } else if (tag.equals("PW")) {
          } else if (tag.equals("KM") && Lizzie.config.readKomi) {
            try {
              if (!tagContent.trim().isEmpty()) {
                Double komi = Double.parseDouble(tagContent);
                if (komi >= 200) {
                  komi = komi / 100;
                  if (komi == 3.5) komi = 7.0;
                }
                if (komi.toString().endsWith(".75") || komi.toString().endsWith(".25"))
                  komi = komi * 2;
                if (Math.abs(komi) < Board.boardWidth * Board.boardHeight) {
                  history.getGameInfo().setKomi(komi);
                  history.getGameInfo().changeKomi();
                  Lizzie.leelaz.komi(komi);
                }
              }
            } catch (NumberFormatException e) {
              e.printStackTrace();
            }
          } else {
            if (moveStart) {
              // Other SGF node properties
              if ("AE".equals(tag)) {
                // remove a stone
                if (addPassForMove) {
                  // Save the step count
                  subTreeStepMap.put(subTreeDepth, subTreeStepMap.get(subTreeDepth) + 1);
                  Stone color =
                      history.getLastMoveColor() == Stone.WHITE ? Stone.BLACK : Stone.WHITE;
                  boolean newBranch = (subTreeStepMap.get(subTreeDepth) == 1);
                  history.pass(color, newBranch, true);
                  if (newBranch) {
                    processPendingPros(history, pendingProps);
                  }
                  addPassForMove = false;
                }
                history.addNodeProperty(tag, tagContent);
                int[] move = convertSgfPosToCoord(tagContent);
                if (move != null) {
                  history.removeStone(
                      move[0], move[1], tag.equals("AB") ? Stone.BLACK : Stone.WHITE);
                }
              } else {
                boolean firstProp = (subTreeStepMap.get(subTreeDepth) == 0);
                if (firstProp) {
                  addProperty(pendingProps, tag, tagContent);
                } else {
                  history.addNodeProperty(tag, tagContent);
                }
              }
            } else {
              if ("N".equals(tag) && headComment.isEmpty()) headComment = tagContent;
              else addProperty(gameProperties, tag, tagContent);
            }
          }
          break;
        case ';':
          if (inProp) {
            // support C[a;b;c;]
            tagContentBuilder.append(c);
          }
          break;
        default:
          if (subTreeDepth > 1 && !isMultiGo) {
            break;
          }
          if (inTag) {
            if (c == '\\') {
              escaping = true;
              continue;
            }
            tagContentBuilder.append(c);
          } else {
            if (c != '\n' && c != '\r' && c != '\t' && c != ' ') {
              tagBuilder.append(c);
            }
          }
      }
    }
    history.toBranchTop();
    return history.getCurrentHistoryNode().numberOfChildren() - 1;
  }

  private static String asCoord(int i) {
    int[] cor = Board.getCoord(i);

    return asCoord(cor);
  }

  private static List<MoveData> parseInfofromfile(String line) {
    List<MoveData> bestMoves = new ArrayList<>();
    String[] variations = line.split(" info ");
    int k =
        (Lizzie.config.limitMaxSuggestion > 0 && !Lizzie.config.showNoSuggCircle
            ? Lizzie.config.limitMaxSuggestion
            : 361);
    for (String var : variations) {
      if (!var.trim().isEmpty()) {
        bestMoves.add(MoveData.fromInfofromfile(var, bestMoves));
        k = k - 1;
        if (k < 1) break;
      }
    }
    Lizzie.board.getData().tryToSetBestMoves(bestMoves, Lizzie.board.getData().engineName, false);
    return bestMoves;
  }

  private static List<MoveData> parseInfofromfile2(String line) {
    List<MoveData> bestMoves = new ArrayList<>();
    String[] variations = line.split(" info ");
    // int k =
    // Lizzie.config.config.getJSONObject("leelaz").getInt("max-suggestion-moves");
    for (String var : variations) {
      if (!var.trim().isEmpty()) {
        bestMoves.add(MoveData.fromInfofromfile(var, bestMoves));
        // k = k - 1;
        // if (k < 1) break;
      }
    }
    Lizzie.board.getData().tryToSetBestMoves2(bestMoves, Lizzie.board.getData().engineName2, false);
    return bestMoves;
  }

  public static String asCoord(int[] c) {
    char x = alphabet.charAt(c[0]);
    char y = alphabet.charAt(c[1]);

    return String.format("%c%c", x, y);
  }
}

class BlunderMoves implements Comparable<BlunderMoves> {

  public int moveNumber;
  public Double diffWinrate;

  public BlunderMoves(int moveNumber, Double diffWinrate) {
    this.moveNumber = moveNumber;
    this.diffWinrate = diffWinrate;
  }

  @Override
  public String toString() {
    return moveNumber
        + "("
        + (diffWinrate > 0 ? "+" : "-")
        + String.format(Locale.ENGLISH, "%.1f", Math.abs(diffWinrate))
        + "%)";
  }

  @Override
  public int compareTo(BlunderMoves move) {
    if (Math.abs(move.diffWinrate) > Math.abs(this.diffWinrate)) return 1;
    else return -1;
  }
}
