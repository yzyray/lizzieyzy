package featurecat.lizzie.theme;

import static java.io.File.separator;

import featurecat.lizzie.Lizzie;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.IntStream;
import javax.imageio.ImageIO;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

/** Theme Allow to load the external image & theme config */
public class Theme {
  BufferedImage blackStoneCached = null;
  BufferedImage whiteStoneCached = null;
  BufferedImage boardCached = null;
  BufferedImage backgroundCached = null;

  private String configFile = "theme.txt";
  public static String pathPrefix = "theme" + separator;
  public String path = null;
  public JSONObject config = new JSONObject();
  private JSONObject uiConfig = null;
  private Optional<List<Double>> blunderWinrateThresholds = Optional.empty();

  public Theme() {}

  public boolean getTheme(JSONObject uiConfig) {
    this.uiConfig = uiConfig;
    String themeName = uiConfig.optString("theme");
    this.path = Theme.pathPrefix + (themeName.isEmpty() ? "" : themeName + separator);
    File file = new File(this.path + this.configFile);
    boolean canReadFile = file.canRead();
    if (canReadFile) {
      FileInputStream fp;
      try {
        fp = new FileInputStream(file);
        config = new JSONObject(new JSONTokener(fp));
        fp.close();
      } catch (IOException e) {
      } catch (JSONException e) {
      }
    }
    return canReadFile;
  }

  private String getImagePathByKey(String key) {
    return config.optString(key);
  }

  public String backgroundPath() {
    return getImagePathByKey("background-image");
  }

  public int stoneIndicatorType() {
    String key = "stone-indicator-type";
    return config.optInt(key, uiConfig.optInt(key, 0));
  }

  public String blackStonePath() {
    return getImagePathByKey("black-stone-image");
  }

  public String whiteStonePath() {
    return getImagePathByKey("white-stone-image");
  }

  public String boardPath() {
    return getImagePathByKey("board-image");
  }

  public Color pureBoardColor() {
    return getColorByKey("pure-board-color", new Color(217, 152, 77));
  }

  public Color pureBackgroundColor() {
    return getColorByKey("pure-background-color", Color.GRAY);
  }

  public Color scoreMeanLineColor() {
    return getColorByKey("scoremean-line-color", new Color(255, 0, 255));
  }

  public Theme(String themeName) {
    this.uiConfig = Lizzie.config.uiConfig;
    this.path = Theme.pathPrefix + (themeName.isEmpty() ? "" : themeName + separator);
    File file = new File(this.path + this.configFile);
    if (file.canRead()) {
      FileInputStream fp;
      try {
        fp = new FileInputStream(file);
        config = new JSONObject(new JSONTokener(fp));
        fp.close();
      } catch (IOException e) {
      } catch (JSONException e) {
      }
    }
  }

  public String boardImageString() {
    return this.path + config.optString("oard-image", "board.png");
  }

  public String backgroundImageString() {
    return this.path + config.optString("background-image", "background.png");
  }

  public String whiteStoneImageString() {
    return this.path + config.optString("white-stone-image", "white.png");
  }

  public String blackStoneImageString() {
    return this.path + config.optString("black-stone-image", "black.png");
  }

  public BufferedImage blackStone() {
    if (blackStoneCached == null) {
      blackStoneCached = getImageByKey("black-stone-image", "black.png", "black0.png");
    }
    return blackStoneCached;
  }

  public BufferedImage whiteStone() {
    if (whiteStoneCached == null) {
      whiteStoneCached = getImageByKey("white-stone-image", "white.png", "white0.png");
    }
    return whiteStoneCached;
  }

  public BufferedImage board() {
    if (boardCached == null) {
      boardCached = getImageByKey("board-image", "board.png", "board.png");
    }
    return boardCached;
  }

  public BufferedImage background() {
    if (backgroundCached == null) {
      backgroundCached = getImageByKey("background-image", "background.png", "background.jpg");
    }
    return backgroundCached;
  }

  /** Use custom font for general text */
  public String fontName() {
    String key = "font-name";
    //    String name = config.optString(key, uiConfig.optString(key, "微软雅黑"));
    //    if (name.equals("Lizzie默认")) name = "微软雅黑";
    return config.optString(key, uiConfig.optString(key, null));
  }

  /** Use custom font for the UI */
  public String uiFontName() {
    String key = "ui-font-name";
    String name = config.optString(key, uiConfig.optString(key, "Microsoft YaHei")); // 微软雅黑
    if (name.equals("Lizzie默认") || name.equals("Lizzie Default")) name = "Microsoft YaHei"; // 微软雅黑
    return name;
  }

  /** Use custom font for the Leela Zero winrate on the stone */
  public String winrateFontName() {
    String key = "winrate-font-name";
    return config.optString(key, uiConfig.optString(key, null));
  }

  /** Show the node with the comment color */
  public boolean showCommentNodeColor(boolean onLoad) {
    String key = "show-comment-node-color";
    if (onLoad) return config.optBoolean(key, uiConfig.optBoolean(key, true));
    else return config.optBoolean(key, true);
  }

  public boolean usePureStone(boolean onLoad) {
    String key = "use-pure-stone";
    if (onLoad) return config.optBoolean(key, uiConfig.optBoolean(key, false));
    else return config.optBoolean(key, false);
  }

  public boolean usePureBackground(boolean onLoad) {
    String key = "use-pure-background";
    if (onLoad) return config.optBoolean(key, uiConfig.optBoolean(key, false));
    else return config.optBoolean(key, false);
  }

  public boolean usePureBoard(boolean onLoad) {
    String key = "use-pure-board";
    if (onLoad) return config.optBoolean(key, uiConfig.optBoolean(key, false));
    else return config.optBoolean(key, false);
  }

  public boolean useScoreDiffInVariationTree(boolean onLoad) {
    String key = "use-scorediff-in-variation-tree";
    if (onLoad) return config.optBoolean(key, uiConfig.optBoolean(key, true));
    else return config.optBoolean(key, true);
  }

  public double scoreDiffInVariationTreeFactor(boolean onLoad) {
    String key = "scorediff-in-variation-tree-factor";
    if (onLoad) return config.optDouble(key, uiConfig.optDouble(key, 0.5));
    else return config.optDouble(key, 0.5);
  }

  public boolean showStoneShadow(boolean onLoad) {
    String key = "show-stone-shadow";
    if (onLoad) return config.optBoolean(key, uiConfig.optBoolean(key, true));
    else return config.optBoolean(key, true);
  }

  /** The size of the shadow */
  public int shadowSize() {
    return getIntByKey("shadow-size", 85);
  }

  /** The stroke width of the winrate line */
  public float winrateStrokeWidth() {
    return config.optFloat("winrate-stroke-width", uiConfig.optFloat("winrate-stroke-width", 1.7f));
  }

  /** The minimum width of the blunder bar */
  public int minimumBlunderBarWidth() {
    return getIntByKey("minimum-blunder-bar-width", 3);
  }

  /** The font size of the comment */
  public int commentFontSize() {
    return getIntByKey("comment-font-size", 0);
  }

  public int backgroundFilter() {
    return getIntByKey("background-filter", 20);
  }
  /** The size of the shadow */
  public int nodeColorMode() {
    return getIntByKey("node-color-mode", 0);
  }

  /**
   * The background color of the comment panel
   *
   * @return
   */
  public Color commentBackgroundColor() {
    return getColorByKey("comment-background-color", new Color(0, 0, 0, 200));
  }

  /** The font color of the comment */
  public Color commentFontColor() {
    return getColorByKey("comment-font-color", Color.WHITE);
  }

  /** The color of the node with the comment */
  public Color commentNodeColor() {
    return getColorByKey("comment-node-color", Color.BLUE.brighter());
  }

  /** The color of the winrate line */
  public Color winrateLineColor() {
    return getColorByKey("winrate-line-color", Color.GREEN);
  }

  /** The color of the line that missed the winrate */
  public Color winrateMissLineColor() {
    return getColorByKey("winrate-miss-line-color", Color.blue.darker());
  }

  /** The color of the blunder bar */
  public Color blunderBarColor() {
    return getColorByKey("blunder-bar-color", new Color(255, 204, 255));
  }

  public Color bestMoveColor() {
    return getColorByKey("best-move-color", Color.CYAN);
  }

  //  public Color bestVarPanelColor() {
  //    return getColorByKey("var-panel-color", new Color(80, 80, 80));
  //  }

  /** The threshold list of the blunder winrate */
  public Optional<List<Double>> blunderWinrateThresholds() {
    String key = "blunder-winrate-thresholds";
    Optional<JSONArray> array = Optional.ofNullable(config.optJSONArray(key));
    if (!array.isPresent()) {
      array = Optional.ofNullable(uiConfig.optJSONArray(key));
    }
    array.ifPresent(
        m -> {
          blunderWinrateThresholds = Optional.of(new ArrayList<Double>());
          m.forEach(a -> blunderWinrateThresholds.get().add(new Double(a.toString())));
        });
    return blunderWinrateThresholds;
  }

  /** The color list of the blunder node */
  public Optional<Map<Double, Color>> blunderNodeColors() {
    Optional<Map<Double, Color>> map = Optional.of(new HashMap<Double, Color>());
    String key = "blunder-node-colors";
    Optional<JSONArray> array = Optional.ofNullable(config.optJSONArray(key));
    if (!array.isPresent()) {
      array = Optional.ofNullable(uiConfig.optJSONArray(key));
    }
    array.ifPresent(
        a -> {
          IntStream.range(0, a.length())
              .forEach(
                  i -> {
                    Color color = array2Color((JSONArray) a.get(i), null);
                    blunderWinrateThresholds.map(l -> l.get(i)).map(t -> map.get().put(t, color));
                  });
        });
    return map;
  }

  private Color getColorByKey(String key, Color defaultColor) {
    Color color = array2Color(config.optJSONArray(key), null);
    if (color == null) {
      color = array2Color(uiConfig.optJSONArray(key), defaultColor);
    }
    return color;
  }

  /** Convert option color array to Color */
  public static Color array2Color(JSONArray a, Color defaultColor) {
    if (a != null) {
      if (a.length() == 3) {
        return new Color(a.getInt(0), a.getInt(1), a.getInt(2));
      } else if (a.length() == 4) {
        return new Color(a.getInt(0), a.getInt(1), a.getInt(2), a.getInt(3));
      }
    }
    return defaultColor;
  }

  private BufferedImage getImageByKey(String key, String defaultValue, String defaultImg) {
    BufferedImage image = null;
    String p = this.path + config.optString(key, defaultValue);
    try {
      image = ImageIO.read(new File(p));
    } catch (IOException e) {
      try {
        p = Theme.pathPrefix + uiConfig.optString(key, defaultValue);
        image = ImageIO.read(new File(p));
      } catch (IOException e1) {
        try {
          image = ImageIO.read(getClass().getResourceAsStream("/assets/" + defaultImg));
        } catch (IOException e2) {
          e2.printStackTrace();
        }
      }
    }
    return image;
  }

  public void save() {
    try {
      File file = new File(this.path + this.configFile);
      file.createNewFile();

      FileOutputStream fp = new FileOutputStream(file);
      OutputStreamWriter writer = new OutputStreamWriter(fp);

      Iterator<String> keys = config.keys();
      while (keys.hasNext()) {
        String key = keys.next();
        Object value = config.get(key);
        if (value == null || (value instanceof String && ((String) value).trim().isEmpty())) {
          keys.remove();
        }
      }

      writer.write(config.toString(2));

      writer.close();
      fp.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public static JSONArray color2Array(Color c) {
    JSONArray a = new JSONArray("[]");
    if (c != null) {
      a.put(c.getRed());
      a.put(c.getGreen());
      a.put(c.getBlue());
      if (c.getAlpha() != 255) {
        a.put(c.getAlpha());
      }
    }
    return a;
  }

  private int getIntByKey(String key, int defaultValue) {
    return config.optInt(key, uiConfig.optInt(key, defaultValue));
  }
}
