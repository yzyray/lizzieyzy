package featurecat.lizzie.analysis;

import featurecat.lizzie.Lizzie;
import featurecat.lizzie.gui.EngineFailedMessage;
import featurecat.lizzie.gui.RemoteEngineData;
import featurecat.lizzie.rules.Board;
import featurecat.lizzie.rules.BoardHistoryNode;
import featurecat.lizzie.rules.SGFParser;
import featurecat.lizzie.rules.Stone;
import featurecat.lizzie.util.Utils;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import org.jdesktop.swingx.util.OS;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ContributeEngine {
  private ArrayList<ContributeGameInfo> contributeGames;
  private ArrayList<ContributeUnParseGameInfo> unParseGameInfos;
  private int watchingGameIndex = -1;
  private ContributeGameInfo currentWatchGame;

  private Process process;
  public boolean isNormalEnd = false;

  private BufferedReader inputStream;
  // private BufferedOutputStream outputStream;
  private BufferedReader errorStream;

  private String engineCommand;
  private ScheduledExecutorService executor;
  private ScheduledExecutorService executorErr;
  private List<String> commands;

  private boolean useJavaSSH = false;
  private ContributeSSHController javaSSH;
  private String ip;
  private String port;
  private String userName;
  private String password;
  private boolean useKeyGen;
  private String keyGenPath;
  public boolean javaSSHClosed;
  private String engineParentPath = "";

  public ContributeEngine() {
    if (Lizzie.config.contributeUseCommand) {
      engineCommand = Lizzie.config.contributeCommand;
      try {
        String katagoPath =
            Lizzie.config.contributeCommand.substring(
                0, Lizzie.config.contributeCommand.toLowerCase().lastIndexOf("katago"));
        engineParentPath = katagoPath.substring(0, getLastIndexOfFileSep(katagoPath));
      } catch (Exception e) {
        e.printStackTrace();
      }
    } else {
      engineCommand = Lizzie.config.contributeEnginePath + " contribute";
      try {
        engineParentPath =
            Lizzie.config.contributeEnginePath.substring(
                0, getLastIndexOfFileSep(Lizzie.config.contributeEnginePath));
      } catch (Exception e) {
        e.printStackTrace();
      }
      boolean useConfigFile = Lizzie.config.contributeConfigPath.trim().length() > 0;
      if (useConfigFile) engineCommand += "-config " + Lizzie.config.contributeConfigPath;
      engineCommand += " -override-config ";
      if (!useConfigFile) engineCommand += "\"serverUrl = https://katagotraining.org/\",";
      engineCommand += "\"username = " + Lizzie.config.contributeUserName + "\",";
      engineCommand += "\"password = " + Lizzie.config.contributePassword + "\",";
      engineCommand += "\"maxSimultaneousGames = " + Lizzie.config.contributeBatchGames + "\",";
      engineCommand +=
          "\"includeOwnership = "
              + (Lizzie.config.contributeShowEstimate ? "true" : "false")
              + "\",";
      engineCommand += "\"logGamesAsJson = true\",";
    }
    RemoteEngineData remoteData = Utils.getContributeRemoteEngineData();
    this.useJavaSSH = remoteData.useJavaSSH;
    this.ip = remoteData.ip;
    this.port = remoteData.port;
    this.userName = remoteData.userName;
    this.password = remoteData.password;
    this.useKeyGen = remoteData.useKeyGen;
    this.keyGenPath = remoteData.keyGenPath;

    contributeGames = new ArrayList<ContributeGameInfo>();
    unParseGameInfos = new ArrayList<ContributeUnParseGameInfo>();
    testLines();
    //   startEngine(engineCommand);
  }

  private void testLines() {
    // TODO Auto-generated method stub
    String line1 =
        "{\"blackPlayer\":\"kata1-b40c256-s10312780288-d2513725330\",\"boardXSize\":19,\"boardYSize\":9,\"gameId\":\"8C3A37A596C4A007\",\"initialPlayer\":\"B\",\"initialStones\":[],\"initialTurnNumber\":0,\"move\":[\"W\",\"R5\"],\"moveInfos\":[{\"lcb\":0.411789224,\"move\":\"D4\",\"order\":0,\"prior\":0.0290139392,\"pv\":[\"D4\",\"Q4\",\"C6\",\"J6\",\"O3\",\"P2\",\"Q6\"],\"scoreLead\":-0.650186005,\"scoreMean\":-0.650186005,\"scoreSelfplay\":-0.931910802,\"scoreStdev\":10.4362585,\"utility\":-0.206014112,\"utilityLcb\":-0.182342383,\"visits\":230,\"winrate\":0.403021917},{\"lcb\":0.411744719,\"move\":\"R5\",\"order\":1,\"prior\":0.134325117,\"pv\":[\"R5\",\"D6\",\"C4\",\"R3\",\"P6\",\"P7\",\"O6\",\"O7\",\"P3\",\"N6\",\"E4\",\"Q4\",\"Q6\"],\"scoreLead\":-0.553495982,\"scoreMean\":-0.553495982,\"scoreSelfplay\":-0.851568654,\"scoreStdev\":10.9440635,\"utility\":-0.200783077,\"utilityLcb\":-0.182019276,\"visits\":588,\"winrate\":0.404795163},{\"lcb\":0.417061671,\"move\":\"D6\",\"order\":2,\"prior\":0.0482043773,\"pv\":[\"D6\",\"Q4\",\"C4\",\"K6\",\"O3\",\"P3\",\"O4\",\"L4\",\"Q6\"],\"scoreLead\":-0.602743492,\"scoreMean\":-0.602743492,\"scoreSelfplay\":-0.857608953,\"scoreStdev\":10.45662,\"utility\":-0.196769669,\"utilityLcb\":-0.171458192,\"visits\":157,\"winrate\":0.40768705},{\"lcb\":0.415725371,\"move\":\"C6\",\"order\":3,\"prior\":0.0257119387,\"pv\":[\"C6\",\"Q4\",\"D4\",\"K6\",\"O3\",\"P3\",\"O4\",\"K4\",\"Q6\"],\"scoreLead\":-0.638090716,\"scoreMean\":-0.638090716,\"scoreSelfplay\":-0.928961733,\"scoreStdev\":10.4675564,\"utility\":-0.206303305,\"utilityLcb\":-0.171929346,\"visits\":109,\"winrate\":0.402994275},{\"lcb\":0.47650713,\"move\":\"Q4\",\"order\":4,\"prior\":0.201449767,\"pv\":[\"Q4\",\"C5\",\"C3\",\"E4\",\"C7\",\"E6\"],\"scoreLead\":-0.323213479,\"scoreMean\":-0.323213479,\"scoreSelfplay\":-0.381685305,\"scoreStdev\":10.6445797,\"utility\":-0.0952725359,\"utilityLcb\":-0.0328620182,\"visits\":88,\"winrate\":0.453392123},{\"lcb\":0.423977935,\"move\":\"C4\",\"order\":5,\"prior\":0.0300614852,\"pv\":[\"C4\",\"R4\",\"D6\",\"O4\",\"P5\",\"O5\",\"Q3\"],\"scoreLead\":-0.597638077,\"scoreMean\":-0.597638077,\"scoreSelfplay\":-0.845657616,\"scoreStdev\":10.5177968,\"utility\":-0.188855677,\"utilityLcb\":-0.150809594,\"visits\":64,\"winrate\":0.409886793},{\"lcb\":0.497314454,\"move\":\"Q6\",\"order\":6,\"prior\":0.0666930974,\"pv\":[\"Q6\",\"R6\",\"R5\",\"Q5\",\"P6\",\"R4\",\"S5\",\"R7\",\"R3\",\"P5\",\"Q4\",\"P4\"],\"scoreLead\":-0.286929013,\"scoreMean\":-0.286929013,\"scoreSelfplay\":-0.358514113,\"scoreStdev\":10.7750358,\"utility\":-0.0979585101,\"utilityLcb\":0.0239233498,\"visits\":31,\"winrate\":0.452173024},{\"lcb\":0.498741718,\"move\":\"R4\",\"order\":7,\"prior\":0.0856196657,\"pv\":[\"R4\",\"C5\",\"P4\",\"R5\",\"Q5\",\"R6\"],\"scoreLead\":-0.28005121,\"scoreMean\":-0.28005121,\"scoreSelfplay\":-0.293528942,\"scoreStdev\":10.7529068,\"utility\":-0.069568964,\"utilityLcb\":0.0309431154,\"visits\":30,\"winrate\":0.461515022},{\"lcb\":0.475134022,\"move\":\"Q5\",\"order\":8,\"prior\":0.0532590933,\"pv\":[\"Q5\",\"D4\",\"C6\",\"R3\",\"P4\",\"D6\"],\"scoreLead\":-0.255666691,\"scoreMean\":-0.255666691,\"scoreSelfplay\":-0.318861147,\"scoreStdev\":11.0440182,\"utility\":-0.0922397198,\"utilityLcb\":-0.0341443709,\"visits\":23,\"winrate\":0.453617226},{\"lcb\":0.548670732,\"move\":\"R6\",\"order\":9,\"prior\":0.024626622,\"pv\":[\"R6\",\"R7\",\"Q4\",\"C5\"],\"scoreLead\":-0.243182455,\"scoreMean\":-0.243182455,\"scoreSelfplay\":-0.272283341,\"scoreStdev\":10.7335879,\"utility\":-0.0599970753,\"utilityLcb\":0.180094624,\"visits\":9,\"winrate\":0.45974788},{\"lcb\":0.545597085,\"move\":\"Q3\",\"order\":10,\"prior\":0.010609298,\"pv\":[\"Q3\",\"R4\",\"R3\",\"C5\"],\"scoreLead\":-0.132223046,\"scoreMean\":-0.132223046,\"scoreSelfplay\":-0.0698449511,\"scoreStdev\":10.8037429,\"utility\":-0.0373098608,\"utilityLcb\":0.13319495,\"visits\":6,\"winrate\":0.482447155},{\"lcb\":0.683495884,\"move\":\"F7\",\"order\":11,\"prior\":0.019130867,\"pv\":[\"F7\",\"D4\",\"R5\",\"D7\",\"P6\"],\"scoreLead\":0.688630409,\"scoreMean\":0.688630409,\"scoreSelfplay\":1.15353439,\"scoreStdev\":11.0101674,\"utility\":0.243819887,\"utilityLcb\":0.442939319,\"visits\":8,\"winrate\":0.609747946},{\"lcb\":0.685383238,\"move\":\"D7\",\"order\":12,\"prior\":0.00838891789,\"pv\":[\"D7\",\"C5\",\"Q4\",\"E6\"],\"scoreLead\":-0.217710837,\"scoreMean\":-0.217710837,\"scoreSelfplay\":-0.145621068,\"scoreStdev\":11.2186914,\"utility\":-0.0245287812,\"utilityLcb\":0.528029637,\"visits\":6,\"winrate\":0.480731972},{\"lcb\":0.989269478,\"move\":\"E1\",\"order\":13,\"prior\":0.0346729904,\"pv\":[\"E1\",\"R4\",\"C5\",\"D3\"],\"scoreLead\":7.34834574,\"scoreMean\":7.34834574,\"scoreSelfplay\":8.94696042,\"scoreStdev\":10.6734595,\"utility\":1.09665507,\"utilityLcb\":1.12185733,\"visits\":11,\"winrate\":0.979935306},{\"lcb\":0.991047954,\"move\":\"C1\",\"order\":14,\"prior\":0.0324443989,\"pv\":[\"C1\",\"D6\",\"R5\",\"C4\"],\"scoreLead\":8.40236836,\"scoreMean\":8.40236836,\"scoreSelfplay\":9.73915419,\"scoreStdev\":10.9224114,\"utility\":1.112235,\"utilityLcb\":1.13446588,\"visits\":10,\"winrate\":0.982814294},{\"lcb\":0.857557258,\"move\":\"F6\",\"order\":15,\"prior\":0.0115300342,\"pv\":[\"F6\",\"D6\",\"D5\",\"C5\"],\"scoreLead\":0.589361392,\"scoreMean\":0.589361392,\"scoreSelfplay\":0.943937096,\"scoreStdev\":11.0909651,\"utility\":0.203414365,\"utilityLcb\":0.939636284,\"visits\":6,\"winrate\":0.584882473},{\"lcb\":0.708746818,\"move\":\"D5\",\"order\":16,\"prior\":0.0109346863,\"pv\":[\"D5\",\"Q4\",\"D7\",\"C3\"],\"scoreLead\":0.253332512,\"scoreMean\":0.253332512,\"scoreSelfplay\":0.552879405,\"scoreStdev\":10.5815886,\"utility\":0.123840903,\"utilityLcb\":0.548226371,\"visits\":6,\"winrate\":0.551567015},{\"lcb\":0.867199898,\"move\":\"E6\",\"order\":17,\"prior\":0.0104588727,\"pv\":[\"E6\",\"R4\",\"C6\",\"C5\"],\"scoreLead\":0.267000896,\"scoreMean\":0.267000896,\"scoreSelfplay\":0.466307902,\"scoreStdev\":10.7677772,\"utility\":0.102288958,\"utilityLcb\":0.988067714,\"visits\":6,\"winrate\":0.539133692},{\"lcb\":0.784902828,\"move\":\"C5\",\"order\":18,\"prior\":0.0072018099,\"pv\":[\"C5\",\"Q4\",\"R3\"],\"scoreLead\":0.0290078599,\"scoreMean\":0.0290078599,\"scoreSelfplay\":0.171683443,\"scoreStdev\":10.7090626,\"utility\":0.0134675073,\"utilityLcb\":0.757304249,\"visits\":5,\"winrate\":0.509407738},{\"lcb\":0.730142354,\"move\":\"D3\",\"order\":19,\"prior\":0.00708987005,\"pv\":[\"D3\",\"C5\",\"R5\",\"E4\"],\"scoreLead\":-0.109001339,\"scoreMean\":-0.109001339,\"scoreSelfplay\":-0.0802570694,\"scoreStdev\":11.3003429,\"utility\":-0.02000496,\"utilityLcb\":0.639927093,\"visits\":5,\"winrate\":0.485723075},{\"lcb\":1.01969357,\"move\":\"C3\",\"order\":20,\"prior\":0.00466456264,\"pv\":[\"C3\",\"R4\",\"D6\",\"M4\"],\"scoreLead\":-0.0648853183,\"scoreMean\":-0.0648853183,\"scoreSelfplay\":-0.014294669,\"scoreStdev\":10.7327458,\"utility\":-0.0312999471,\"utilityLcb\":1.39802245,\"visits\":4,\"winrate\":0.490314905},{\"lcb\":1.00030125,\"move\":\"B9\",\"order\":21,\"prior\":0.0178851597,\"pv\":[\"B9\",\"Q4\",\"D4\",\"D6\"],\"scoreLead\":8.17815362,\"scoreMean\":8.17815362,\"scoreSelfplay\":9.83664288,\"scoreStdev\":10.4774375,\"utility\":1.11919874,\"utilityLcb\":1.15826783,\"visits\":8,\"winrate\":0.985831217},{\"lcb\":1.01201546,\"move\":\"A1\",\"order\":22,\"prior\":0.0139514403,\"pv\":[\"A1\",\"R4\",\"C5\",\"D7\"],\"scoreLead\":12.8445213,\"scoreMean\":12.8445213,\"scoreSelfplay\":13.8159124,\"scoreStdev\":10.2421329,\"utility\":1.1860912,\"utilityLcb\":1.22953555,\"visits\":7,\"winrate\":0.995924956},{\"lcb\":1.14454411,\"move\":\"S3\",\"order\":23,\"prior\":0.00788325816,\"pv\":[\"S3\",\"C5\",\"Q5\",\"D3\"],\"scoreLead\":3.06221988,\"scoreMean\":3.06221988,\"scoreSelfplay\":4.22072177,\"scoreStdev\":10.2725252,\"utility\":0.745836892,\"utilityLcb\":1.58296396,\"visits\":5,\"winrate\":0.83449705},{\"lcb\":1.01957799,\"move\":\"N9\",\"order\":24,\"prior\":0.00534055522,\"pv\":[\"N9\",\"R4\",\"C5\"],\"scoreLead\":7.63270156,\"scoreMean\":7.63270156,\"scoreSelfplay\":9.57141199,\"scoreStdev\":10.8286577,\"utility\":1.10314059,\"utilityLcb\":1.20775197,\"visits\":5,\"winrate\":0.98083303},{\"lcb\":1.05973461,\"move\":\"E3\",\"order\":25,\"prior\":0.00458878744,\"pv\":[\"E3\",\"D4\",\"R5\",\"D3\"],\"scoreLead\":0.465860389,\"scoreMean\":0.465860389,\"scoreSelfplay\":0.815108046,\"scoreStdev\":11.2101031,\"utility\":0.169534853,\"utilityLcb\":1.4767668,\"visits\":4,\"winrate\":0.575574625},{\"lcb\":0.70522012,\"move\":\"E4\",\"order\":26,\"prior\":0.00432151696,\"pv\":[\"E4\",\"R4\",\"C5\",\"D7\"],\"scoreLead\":0.230140535,\"scoreMean\":0.230140535,\"scoreSelfplay\":0.398309641,\"scoreStdev\":10.8131842,\"utility\":0.0664454782,\"utilityLcb\":0.545058498,\"visits\":4,\"winrate\":0.527956039},{\"lcb\":1.79168151,\"move\":\"R7\",\"order\":27,\"prior\":0.00392124429,\"pv\":[\"R7\",\"R6\",\"Q6\",\"R5\"],\"scoreLead\":0.932768956,\"scoreMean\":0.932768956,\"scoreSelfplay\":1.43481706,\"scoreStdev\":11.1106747,\"utility\":0.330622824,\"utilityLcb\":3.4528328,\"visits\":4,\"winrate\":0.635307446},{\"lcb\":1.14324709,\"move\":\"H6\",\"order\":28,\"prior\":0.00374870747,\"pv\":[\"H6\",\"D6\",\"Q6\"],\"scoreLead\":0.726161616,\"scoreMean\":0.726161616,\"scoreSelfplay\":1.20980753,\"scoreStdev\":10.9718092,\"utility\":0.271036212,\"utilityLcb\":1.69170828,\"visits\":4,\"winrate\":0.617072249},{\"lcb\":1.4671992,\"move\":\"R3\",\"order\":29,\"prior\":0.003677045,\"pv\":[\"R3\",\"C5\",\"R5\",\"R6\"],\"scoreLead\":0.381004637,\"scoreMean\":0.381004637,\"scoreSelfplay\":0.706595272,\"scoreStdev\":10.6353614,\"utility\":0.178011429,\"utilityLcb\":2.6103516,\"visits\":4,\"winrate\":0.566332467},{\"lcb\":1.06649285,\"move\":\"J1\",\"order\":30,\"prior\":0.00342844101,\"pv\":[\"J1\",\"Q4\",\"C5\"],\"scoreLead\":7.52553252,\"scoreMean\":7.52553252,\"scoreSelfplay\":9.22547737,\"scoreStdev\":10.7566052,\"utility\":1.10082991,\"utilityLcb\":1.33087288,\"visits\":4,\"winrate\":0.981291748},{\"lcb\":1.10277092,\"move\":\"E7\",\"order\":31,\"prior\":0.00332882511,\"pv\":[\"E7\",\"D6\",\"R5\"],\"scoreLead\":0.486253712,\"scoreMean\":0.486253712,\"scoreSelfplay\":0.782620267,\"scoreStdev\":10.9309526,\"utility\":0.146243644,\"utilityLcb\":1.591438,\"visits\":4,\"winrate\":0.56751375},{\"lcb\":1.05817791,\"move\":\"N1\",\"order\":32,\"prior\":0.00332145044,\"pv\":[\"N1\",\"R4\",\"C5\"],\"scoreLead\":7.4301893,\"scoreMean\":7.4301893,\"scoreSelfplay\":9.10384444,\"scoreStdev\":10.5530632,\"utility\":1.10156621,\"utilityLcb\":1.30882331,\"visits\":4,\"winrate\":0.981416023},{\"lcb\":0.656774979,\"move\":\"P4\",\"order\":33,\"prior\":0.00317440601,\"pv\":[\"P4\",\"R4\",\"C5\"],\"scoreLead\":0.0257591402,\"scoreMean\":0.0257591402,\"scoreSelfplay\":0.187021919,\"scoreStdev\":10.8356343,\"utility\":0.0260629798,\"utilityLcb\":0.42669776,\"visits\":4,\"winrate\":0.508391727},{\"lcb\":1.19720234,\"move\":\"D9\",\"order\":34,\"prior\":0.00279444852,\"pv\":[\"D9\",\"D4\",\"R5\"],\"scoreLead\":7.9553229,\"scoreMean\":7.9553229,\"scoreSelfplay\":9.31566207,\"scoreStdev\":11.0087581,\"utility\":1.10080452,\"utilityLcb\":1.69448902,\"visits\":3,\"winrate\":0.977319193},{\"lcb\":2.22619636,\"move\":\"P6\",\"order\":35,\"prior\":0.0023864056,\"pv\":[\"P6\",\"Q6\",\"Q5\"],\"scoreLead\":0.217488219,\"scoreMean\":0.217488219,\"scoreSelfplay\":0.419253707,\"scoreStdev\":10.9991624,\"utility\":0.0567512239,\"utilityLcb\":4.63124154,\"visits\":3,\"winrate\":0.531940684},{\"lcb\":2.83083596,\"move\":\"P3\",\"order\":36,\"prior\":0.00223683054,\"pv\":[\"P3\",\"Q4\",\"Q3\"],\"scoreLead\":0.657111873,\"scoreMean\":0.657111873,\"scoreSelfplay\":1.11418939,\"scoreStdev\":10.802924,\"utility\":0.254461867,\"utilityLcb\":6.22865528,\"visits\":3,\"winrate\":0.618171737},{\"lcb\":2.74306932,\"move\":\"C7\",\"order\":37,\"prior\":0.00185300154,\"pv\":[\"C7\",\"R4\",\"D4\"],\"scoreLead\":-0.0765924628,\"scoreMean\":-0.0765924628,\"scoreSelfplay\":-0.00539542238,\"scoreStdev\":10.843286,\"utility\":-0.0324626312,\"utilityLcb\":6.0415366,\"visits\":3,\"winrate\":0.493439972},{\"lcb\":3.48962703,\"move\":\"P5\",\"order\":38,\"prior\":0.0018191929,\"pv\":[\"P5\",\"R6\",\"Q4\"],\"scoreLead\":0.580941742,\"scoreMean\":0.580941742,\"scoreSelfplay\":0.914878011,\"scoreStdev\":10.9063092,\"utility\":0.212427619,\"utilityLcb\":8.06117576,\"visits\":3,\"winrate\":0.58268327},{\"lcb\":1.06321632,\"move\":\"O4\",\"order\":39,\"prior\":0.00174291292,\"pv\":[\"O4\",\"Q4\",\"C5\"],\"scoreLead\":0.751428902,\"scoreMean\":0.751428902,\"scoreSelfplay\":1.2611988,\"scoreStdev\":10.8922402,\"utility\":0.262650895,\"utilityLcb\":1.45697341,\"visits\":3,\"winrate\":0.620874643},{\"lcb\":3.28014854,\"move\":\"P7\",\"order\":40,\"prior\":0.00163422746,\"pv\":[\"P7\",\"Q6\",\"Q4\"],\"scoreLead\":0.804274887,\"scoreMean\":0.804274887,\"scoreSelfplay\":1.2640867,\"scoreStdev\":11.2016852,\"utility\":0.231983944,\"utilityLcb\":7.41784509,\"visits\":3,\"winrate\":0.618718485},{\"lcb\":2.75538222,\"move\":\"S5\",\"order\":41,\"prior\":0.00157304609,\"pv\":[\"S5\",\"Q4\",\"Q6\"],\"scoreLead\":0.184106842,\"scoreMean\":0.184106842,\"scoreSelfplay\":0.392372032,\"scoreStdev\":11.1420681,\"utility\":0.0592689637,\"utilityLcb\":6.05508593,\"visits\":3,\"winrate\":0.534709265},{\"lcb\":2.24798476,\"move\":\"J5\",\"order\":42,\"prior\":0.00140976778,\"pv\":[\"J5\",\"R4\",\"C5\"],\"scoreLead\":0.986083448,\"scoreMean\":0.986083448,\"scoreSelfplay\":1.58700724,\"scoreStdev\":10.9742351,\"utility\":0.309998925,\"utilityLcb\":4.63111908,\"visits\":3,\"winrate\":0.647569885},{\"lcb\":2.50896071,\"move\":\"F4\",\"order\":43,\"prior\":0.00140474865,\"pv\":[\"F4\",\"D4\",\"D5\"],\"scoreLead\":0.473270585,\"scoreMean\":0.473270585,\"scoreSelfplay\":0.805915445,\"scoreStdev\":11.3874679,\"utility\":0.19276049,\"utilityLcb\":5.40849755,\"visits\":3,\"winrate\":0.577206244},{\"lcb\":1.97573832,\"move\":\"T6\",\"order\":44,\"prior\":0.00133441656,\"pv\":[\"T6\",\"R4\"],\"scoreLead\":8.51880383,\"scoreMean\":8.51880383,\"scoreSelfplay\":9.71942997,\"scoreStdev\":10.9176559,\"utility\":1.1055886,\"utilityLcb\":2.7,\"visits\":2,\"winrate\":0.975738324},{\"lcb\":1.61269385,\"move\":\"K7\",\"order\":45,\"prior\":0.0012307344,\"pv\":[\"K7\",\"D6\"],\"scoreLead\":0.733570457,\"scoreMean\":0.733570457,\"scoreSelfplay\":1.18255627,\"scoreStdev\":11.0078098,\"utility\":0.250778129,\"utilityLcb\":2.7,\"visits\":2,\"winrate\":0.612693846},{\"lcb\":1.63003151,\"move\":\"M4\",\"order\":46,\"prior\":0.00120710477,\"pv\":[\"M4\",\"Q4\"],\"scoreLead\":0.880556166,\"scoreMean\":0.880556166,\"scoreSelfplay\":1.37919503,\"scoreStdev\":11.0354306,\"utility\":0.290909089,\"utilityLcb\":2.7,\"visits\":2,\"winrate\":0.630031511}],\"moves\":[[\"B\",\"Q7\"]],\"policy\":[3.68049223e-06,6.19486809e-06,6.17808655e-06,7.53280256e-06,6.76957688e-06,6.2530039e-06,5.57087606e-06,5.43178385e-06,5.44878594e-06,5.33101138e-06,5.18671504e-06,4.88331943e-06,5.503176e-06,4.79964456e-06,6.92415006e-06,5.93341929e-06,6.00028579e-06,5.5779069e-06,3.61465936e-06,6.47524894e-06,8.90591582e-06,1.43061297e-05,1.73704739e-05,2.0353531e-05,2.03701802e-05,1.8454235e-05,1.95316697e-05,1.818481e-05,1.74701963e-05,1.63404948e-05,1.62228826e-05,1.43759335e-05,1.65142992e-05,1.65479323e-05,2.81290831e-05,1.25817269e-05,1.13347996e-05,5.92474498e-06,6.33737591e-06,1.46806733e-05,0.000418685318,0.00287835486,0.000241473594,0.000135134833,8.49927601e-05,7.74028595e-05,6.58567151e-05,5.8113932e-05,5.34883802e-05,5.28141827e-05,6.74455659e-05,9.5486459e-05,0.000132852365,-1.0,0.000356320641,1.42922945e-05,6.41094448e-06,7.84140957e-06,2.30247133e-05,0.020761786,0.028109258,0.00144666806,0.000239257875,0.000142932578,0.000161094154,0.000158830313,0.000141837474,0.00011548755,0.000100778328,9.42824699e-05,0.000144586971,0.00061661005,0.0846912935,0.0194646474,2.69568445e-05,7.06108403e-06,8.38872438e-06,6.62079983e-05,0.00315508828,0.00585722877,0.000105115978,4.47749699e-05,4.83613221e-05,5.04693053e-05,5.54018225e-05,6.3225787e-05,5.62960922e-05,5.60773915e-05,5.88750263e-05,7.70132756e-05,0.000412710826,0.0513954312,0.234016597,0.000332746509,7.55729434e-06,7.69504732e-06,2.16730787e-05,0.0211149827,0.024825383,0.00148430374,0.00021313419,0.000115678253,0.000123376027,0.000133412919,0.000144928723,0.000137480034,0.000136500283,0.000125662933,0.00038736788,0.000940401864,0.434608012,0.0456792526,3.62762148e-05,7.60201965e-06,6.52342487e-06,1.40266093e-05,0.000475721114,0.00308499625,0.000285054935,0.000121995923,7.67280217e-05,6.47715351e-05,6.33398158e-05,6.44611937e-05,6.2430292e-05,6.33967356e-05,7.20937096e-05,0.000116166899,0.000556111161,0.00554762734,0.00114871492,1.63716359e-05,6.58723548e-06,6.42113901e-06,9.03051568e-06,1.38964742e-05,1.66100399e-05,1.92428961e-05,1.86723519e-05,1.75766909e-05,1.83625543e-05,1.80208172e-05,1.79513172e-05,1.78844584e-05,1.92637672e-05,1.85263489e-05,2.13879266e-05,2.29779071e-05,1.81737832e-05,1.39264985e-05,8.94638106e-06,5.85803582e-06,3.48918775e-06,5.82501298e-06,6.31948387e-06,7.85129851e-06,7.30364445e-06,6.77833941e-06,6.1586743e-06,5.78034133e-06,5.77612082e-06,5.57314888e-06,5.58367356e-06,5.59687669e-06,6.31927469e-06,6.68231496e-06,7.61636602e-06,7.25307382e-06,6.42931445e-06,6.07232596e-06,3.47920923e-06,2.8246609e-06],\"rootInfo\":{\"currentPlayer\":\"W\",\"scoreLead\":-0.53151578,\"scoreSelfplay\":-0.77868669,\"scoreStdev\":10.7260298,\"symHash\":\"0499B09CD37ABD33642F45051A0DE53D\",\"thisHash\":\"92C0EF21BA77060BC7E12D443568C6ED\",\"utility\":-0.181789062,\"visits\":1500,\"winrate\":0.413611871},\"rules\":{\"friendlyPassOk\":false,\"hasButton\":false,\"ko\":\"SITUATIONAL\",\"komi\":7.0,\"scoring\":\"AREA\",\"suicide\":false,\"tax\":\"NONE\",\"whiteHandicapBonus\":\"0\"},\"turnNumber\":1,\"whitePlayer\":\"kata1-b40c256-s10312780288-d2513725330\"}";
    String line2 =
        "{\"blackPlayer\":\"kata1-b40c256-s10312780288-d2513725330\",\"boardXSize\":19,\"boardYSize\":9,\"gameId\":\"8C3A37A596C4A007\",\"initialPlayer\":\"B\",\"initialStones\":[],\"initialTurnNumber\":0,\"move\":[\"B\",\"D6\"],\"moveInfos\":[{\"lcb\":0.40430922,\"move\":\"D6\",\"order\":0,\"prior\":0.299313873,\"pv\":[\"D6\",\"C4\",\"R3\",\"P6\",\"P7\",\"O6\",\"O7\",\"P3\",\"N6\",\"E4\",\"Q4\",\"Q6\"],\"scoreLead\":-0.495215883,\"scoreMean\":-0.495215883,\"scoreSelfplay\":-0.771733555,\"scoreStdev\":10.888411,\"utility\":-0.176860482,\"utilityLcb\":-0.201750062,\"visits\":294,\"winrate\":0.413527583},{\"lcb\":0.38488863,\"move\":\"D4\",\"order\":1,\"prior\":0.157995388,\"pv\":[\"D4\",\"C6\",\"D6\",\"C5\",\"D5\",\"C4\",\"D3\",\"D7\",\"E7\",\"D8\",\"R3\",\"P6\"],\"scoreLead\":-0.550912032,\"scoreMean\":-0.550912032,\"scoreSelfplay\":-0.862622475,\"scoreStdev\":10.8472309,\"utility\":-0.201343993,\"utilityLcb\":-0.248843094,\"visits\":87,\"winrate\":0.40248089},{\"lcb\":0.363900116,\"move\":\"R3\",\"order\":2,\"prior\":0.158468455,\"pv\":[\"R3\",\"P6\",\"P7\",\"O6\",\"O7\",\"P3\",\"N6\",\"N5\",\"M5\",\"N4\"],\"scoreLead\":-0.679683422,\"scoreMean\":-0.679683422,\"scoreSelfplay\":-0.977677303,\"scoreStdev\":11.3402493,\"utility\":-0.213523779,\"utilityLcb\":-0.29791236,\"visits\":71,\"winrate\":0.395155146},{\"lcb\":0.369205987,\"move\":\"C5\",\"order\":3,\"prior\":0.115465984,\"pv\":[\"C5\",\"P6\",\"P7\",\"O6\",\"N7\",\"D7\",\"E6\",\"E7\",\"F6\",\"F7\"],\"scoreLead\":-0.614543578,\"scoreMean\":-0.614543578,\"scoreSelfplay\":-0.93271096,\"scoreStdev\":10.6112298,\"utility\":-0.217997433,\"utilityLcb\":-0.283184592,\"visits\":49,\"winrate\":0.393349379},{\"lcb\":0.373547848,\"move\":\"C4\",\"order\":4,\"prior\":0.101293474,\"pv\":[\"C4\",\"C6\",\"Q4\",\"Q5\",\"R4\",\"P5\"],\"scoreLead\":-0.589922925,\"scoreMean\":-0.589922925,\"scoreSelfplay\":-0.936224926,\"scoreStdev\":11.0797972,\"utility\":-0.217939369,\"utilityLcb\":-0.278057672,\"visits\":40,\"winrate\":0.395813886},{\"lcb\":0.341204695,\"move\":\"Q3\",\"order\":5,\"prior\":0.0598101988,\"pv\":[\"Q3\",\"P6\",\"P7\",\"N6\",\"D4\",\"P4\"],\"scoreLead\":-0.665714796,\"scoreMean\":-0.665714796,\"scoreSelfplay\":-1.0390871,\"scoreStdev\":11.1921646,\"utility\":-0.24274179,\"utilityLcb\":-0.354213415,\"visits\":18,\"winrate\":0.382490482},{\"lcb\":0.343211855,\"move\":\"C6\",\"order\":6,\"prior\":0.0455303639,\"pv\":[\"C6\",\"D4\",\"R3\",\"P6\",\"P7\"],\"scoreLead\":-0.667099158,\"scoreMean\":-0.667099158,\"scoreSelfplay\":-1.02398959,\"scoreStdev\":10.979125,\"utility\":-0.23703047,\"utilityLcb\":-0.35040018,\"visits\":15,\"winrate\":0.385200636},{\"lcb\":0.169199044,\"move\":\"Q4\",\"order\":7,\"prior\":0.0148545485,\"pv\":[\"Q4\",\"Q5\",\"R4\",\"P5\",\"O3\"],\"scoreLead\":-0.618364132,\"scoreMean\":-0.618364132,\"scoreSelfplay\":-0.923022821,\"scoreStdev\":11.3693728,\"utility\":-0.218683247,\"utilityLcb\":-0.829586356,\"visits\":7,\"winrate\":0.395459455},{\"lcb\":0.229880065,\"move\":\"D5\",\"order\":8,\"prior\":0.0234855115,\"pv\":[\"D5\",\"P6\",\"P7\",\"O6\",\"N7\"],\"scoreLead\":-0.676911399,\"scoreMean\":-0.676911399,\"scoreSelfplay\":-1.04005638,\"scoreStdev\":10.8330438,\"utility\":-0.251226057,\"utilityLcb\":-0.654536431,\"visits\":6,\"winrate\":0.379254278}],\"moves\":[[\"B\",\"Q7\"],[\"W\",\"R5\"]],\"policy\":[5.27834345e-06,8.05490072e-06,7.61023603e-06,9.10493418e-06,9.49054083e-06,9.52584105e-06,8.53246183e-06,7.73154716e-06,7.19018726e-06,7.05459388e-06,6.79296636e-06,7.12325982e-06,8.26317319e-06,8.30255067e-06,8.36955951e-06,7.73968441e-06,7.75984608e-06,7.8471694e-06,5.73375837e-06,8.67422841e-06,9.59446606e-06,1.50996857e-05,1.59348547e-05,2.10667113e-05,1.67811177e-05,1.78038972e-05,1.63726199e-05,1.65041474e-05,1.60512282e-05,1.77085949e-05,2.0485184e-05,1.70838048e-05,2.23362003e-05,1.45192462e-05,9.66876723e-06,1.22883266e-05,1.34133752e-05,8.21268895e-06,8.9739533e-06,1.663478e-05,0.00029328774,0.00116160396,0.000188701175,0.000107033302,7.02075704e-05,4.78474176e-05,3.77513206e-05,3.05024951e-05,3.26868139e-05,6.69980291e-05,0.000180818184,0.000173466964,3.37481833e-05,-1.0,1.46525181e-05,2.21979371e-05,8.73357021e-06,9.21489573e-06,2.64512073e-05,0.0455303639,0.299313873,0.000393132883,0.000126923725,8.82173772e-05,8.32403966e-05,7.91604689e-05,7.64192737e-05,8.25827738e-05,0.000182828386,0.000365952263,0.00247891317,0.00320674083,5.32338854e-05,0.000736318994,0.000148411666,9.61466412e-06,9.35299613e-06,0.000186729376,0.115465984,0.0234855115,0.000149698462,3.15897341e-05,3.43229476e-05,3.06773582e-05,3.67310458e-05,4.3511176e-05,4.99089365e-05,6.9135458e-05,0.000104177292,0.000136864124,0.000432573666,0.000336469035,-1.0,7.28535379e-05,8.87773058e-06,8.59451302e-06,4.81283168e-05,0.101293474,0.157995388,0.000789408979,0.000159302886,8.57992272e-05,7.92763603e-05,8.62359739e-05,9.93733047e-05,0.000118766809,0.000155808666,0.000202627518,0.000604291388,0.00200473098,0.0148545485,0.000403855491,3.24890643e-05,8.83565099e-06,7.96041331e-06,1.45273198e-05,0.000294199883,0.00210139086,0.000161375196,0.000108317232,5.73204416e-05,4.28449312e-05,4.33493515e-05,4.76824716e-05,6.5196029e-05,0.000105987376,0.00014614503,0.000258521235,0.0020072707,0.0598101988,0.158468455,3.18329403e-05,8.07251126e-06,8.79091112e-06,1.01223113e-05,1.41376004e-05,1.70376443e-05,2.02964966e-05,1.93110609e-05,1.75654404e-05,1.82274252e-05,1.76539615e-05,1.72361943e-05,1.83009124e-05,1.93709693e-05,2.17752749e-05,2.72335838e-05,7.13420231e-05,5.46664523e-05,3.46060733e-05,1.45519462e-05,8.08746063e-06,6.44848842e-06,8.67459312e-06,7.91439743e-06,9.23737753e-06,8.79800609e-06,8.37780863e-06,7.3646579e-06,6.98478425e-06,6.74225703e-06,6.81230586e-06,6.82173231e-06,7.16121986e-06,7.30634929e-06,8.70200256e-06,8.71502471e-06,8.463825e-06,7.8611165e-06,8.88081377e-06,6.3061475e-06,1.49050436e-06],\"rootInfo\":{\"currentPlayer\":\"B\",\"scoreLead\":-0.554711424,\"scoreSelfplay\":-0.853328632,\"scoreStdev\":10.9461484,\"symHash\":\"3E9126DFCF72AA080DD4177C1A4CAF38\",\"thisHash\":\"93824AF0CCD9BC34B23F84434984664B\",\"utility\":-0.195802246,\"visits\":588,\"winrate\":0.404615824},\"rules\":{\"friendlyPassOk\":false,\"hasButton\":false,\"ko\":\"SITUATIONAL\",\"komi\":7.0,\"scoring\":\"AREA\",\"suicide\":false,\"tax\":\"NONE\",\"whiteHandicapBonus\":\"0\"},\"turnNumber\":2,\"whitePlayer\":\"kata1-b40c256-s10312780288-d2513725330\"}";
    getJsonGameInfo(tryToGetJsonString(line1), contributeGames, unParseGameInfos);
    getJsonGameInfo(tryToGetJsonString(line2), contributeGames, unParseGameInfos);
    watchGame(0, true);
  }

  private void startEngine(String engineCommand2) {
    commands = Utils.splitCommand(engineCommand);
    if (this.useJavaSSH) {
      this.javaSSH = new ContributeSSHController(this, this.ip, this.port);
      boolean loginStatus = false;
      if (this.useKeyGen) {
        loginStatus =
            this.javaSSH
                .loginByFileKey(this.engineCommand, this.userName, new File(this.keyGenPath))
                .booleanValue();
      } else {
        loginStatus =
            this.javaSSH.login(this.engineCommand, this.userName, this.password).booleanValue();
      }
      if (loginStatus) {
        this.inputStream = new BufferedReader(new InputStreamReader(this.javaSSH.getStdout()));
        //    this.outputStream = new BufferedOutputStream(this.javaSSH.getStdin());
        this.errorStream = new BufferedReader(new InputStreamReader(this.javaSSH.getSterr()));
        javaSSHClosed = false;
      } else {
        javaSSHClosed = true;
        return;
      }
    } else {
      ProcessBuilder processBuilder = new ProcessBuilder(commands);
      processBuilder.redirectErrorStream(true);
      try {
        process = processBuilder.start();
      } catch (IOException e) {
        tryToDignostic(
            Lizzie.resourceBundle.getString("Leelaz.engineFailed")
                + ": "
                + e.getLocalizedMessage());
        process = null;
        return;
      }
      initializeStreams();
    }
    executor = Executors.newSingleThreadScheduledExecutor();
    executor.execute(this::read);
    executorErr = Executors.newSingleThreadScheduledExecutor();
    executorErr.execute(this::readError);
    isNormalEnd = false;
  }

  private void readError() {
    String line = "";

    try {
      while ((line = errorStream.readLine()) != null) {
        try {
          parseLineForError(line);
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private void read() {
    try {
      String line = "";
      while ((line = inputStream.readLine()) != null) {
        try {
          parseLine(line.toString());
        } catch (Exception ex) {
          ex.printStackTrace();
        }
      }
      // this line will be reached when engine shuts down
      if (this.useJavaSSH) javaSSHClosed = true;
      System.out.println("estimate process ended.");
      // Do no exit for switching weights
      // System.exit(-1);
    } catch (IOException e) {
    }
    if (this.useJavaSSH) javaSSHClosed = true;
    if (!isNormalEnd) {
      tryToDignostic(Lizzie.resourceBundle.getString("Leelaz.engineEndUnormalHint"));
    }
    process = null;
    shutdown();
    return;
  }

  private void parseLineForError(String line) {
    // TODO Auto-generated method stub

  }

  private void parseLine(String line) {
    // TODO Auto-generated method stub
    if (line.startsWith("{")) {
      // json game info
      if (getJsonGameInfo(tryToGetJsonString(line), contributeGames, unParseGameInfos)) {
        if (contributeGames != null) {
          int finishedGames = 0;
          int playingGames = 0;
          for (ContributeGameInfo game : contributeGames) { // 已完成10局,正在进行5局,共15局,正在观看第3局
            if (game.complete) finishedGames++;
            else playingGames++;
          }
          if (Lizzie.frame.contributeView != null)
            Lizzie.frame.contributeView.setGames(finishedGames, playingGames);
        }
      }
    } else if (line.contains("Finished game")) {
      // 2021-11-02 09:21:45+0800: Finished game 8 (training), uploaded sgf
      // katago_contribute/kata1/sgfs/kata1-b40c256-s10312780288-d2513725330/155A1E55A4145135.sgf
      // and training data
      // katago_contribute/kata1/tdata/kata1-b40c256-s10312780288-d2513725330/273F621AC4CF6ACF.npz
      // (8 rows)
      String params[] = line.split(" ");
      String sgfPath = "";
      for (int i = 0; i < params.length - 1; i++) {
        if (params[i].equals("sgf")) sgfPath = params[i + 1];
      }
      // katago_contribute/kata1/sgfs/kata1-b40c256-s10312780288-d2513725330/155A1E55A4145135.sgf
      if (sgfPath.length() > 0) {
        String gameId =
            sgfPath.substring(getLastIndexOfFileSep(sgfPath) + 1, sgfPath.lastIndexOf("."));
        if (contributeGames != null) {
          for (ContributeGameInfo game : contributeGames) {
            if (game.gameId.equals(gameId)) {
              game.complete = true;
              if (!useJavaSSH) {
                game.gameResult = SGFParser.getResult(engineParentPath + File.separator + sgfPath);
                if (game == currentWatchGame) setReultToView(game.gameResult);
              }
            }
          }
        }
      }
    }
  }

  private void setReultToView(String result) {
    if (Lizzie.frame.contributeView != null) Lizzie.frame.contributeView.setResult(result);
  }

  private int getLastIndexOfFileSep(String path) {
    return Math.max(path.lastIndexOf("/"), path.lastIndexOf("\\"));
  }

  private void normalQuit() {
    isNormalEnd = true;
    if (this.useJavaSSH) this.javaSSH.close();
    else this.process.destroyForcibly();
  }

  private void shutdown() {
    if (useJavaSSH) javaSSH.close();
    process.destroy();
  }

  private void initializeStreams() {
    inputStream = new BufferedReader(new InputStreamReader(process.getInputStream()));
    //    outputStream = new BufferedOutputStream(process.getOutputStream());
    errorStream = new BufferedReader(new InputStreamReader(process.getErrorStream()));
  }

  // public void test() {
  //	  String jsonTestMove1 =
  //
  // "{\"blackPlayer\":\"kata1-b40c256-s10312780288-d2513725330\",\"boardXSize\":19,\"boardYSize\":19,\"gameId\":\"279066229A41A62D\",\"initialPlayer\":\"W\",\"initialStones\":[[\"W\",\"D17\"],[\"W\",\"G17\"],[\"B\",\"H17\"],[\"W\",\"O17\"],[\"B\",\"C16\"],[\"W\",\"E16\"],[\"B\",\"G16\"],[\"B\",\"Q16\"],[\"W\",\"B15\"],[\"B\",\"C15\"],[\"B\",\"E15\"],[\"W\",\"B14\"],[\"W\",\"C14\"],[\"B\",\"D14\"],[\"B\",\"R14\"],[\"W\",\"A13\"],[\"W\",\"C13\"],[\"B\",\"D13\"],[\"W\",\"B12\"],[\"B\",\"C12\"],[\"W\",\"D12\"],[\"B\",\"B11\"],[\"B\",\"C11\"],[\"W\",\"D11\"],[\"W\",\"C10\"],[\"B\",\"M6\"],[\"B\",\"P4\"],[\"B\",\"Q4\"],[\"B\",\"R4\"],[\"W\",\"C3\"],[\"B\",\"N3\"],[\"W\",\"O3\"],[\"B\",\"P3\"],[\"W\",\"Q3\"],[\"W\",\"R3\"],[\"B\",\"O2\"],[\"W\",\"P2\"],[\"W\",\"R2\"],[\"W\",\"Q1\"]],\"initialTurnNumber\":41,\"move\":[\"B\",\"E3\"],\"moveInfos\":[{\"lcb\":0.575600009,\"move\":\"C17\",\"order\":0,\"prior\":0.265088469,\"pv\":[\"C17\",\"F18\",\"E3\",\"D5\",\"E5\",\"E6\",\"F5\",\"D4\",\"F6\"],\"scoreLead\":1.08615072,\"scoreMean\":1.08615072,\"scoreSelfplay\":1.72183899,\"scoreStdev\":16.3849241,\"utility\":0.180461722,\"utilityLcb\":0.124862682,\"visits\":84,\"winrate\":0.596192246},{\"lcb\":0.562160151,\"move\":\"E3\",\"order\":1,\"prior\":0.169679642,\"pv\":[\"E3\",\"D5\",\"E5\",\"E6\",\"F5\",\"F6\",\"G6\",\"G5\"],\"scoreLead\":1.02266215,\"scoreMean\":1.02266215,\"scoreSelfplay\":1.64333649,\"scoreStdev\":16.2331243,\"utility\":0.173880349,\"utilityLcb\":0.0972798356,\"visits\":48,\"winrate\":0.590530712},{\"lcb\":0.549030164,\"move\":\"D4\",\"order\":2,\"prior\":0.131525815,\"pv\":[\"D4\",\"D3\",\"F4\",\"E4\",\"E5\",\"E3\",\"B10\",\"C9\",\"B9\",\"C8\"],\"scoreLead\":0.979179367,\"scoreMean\":0.979179367,\"scoreSelfplay\":1.67010226,\"scoreStdev\":16.5875115,\"utility\":0.158598786,\"utilityLcb\":0.0565568453,\"visits\":30,\"winrate\":0.586823476},{\"lcb\":0.559852593,\"move\":\"F4\",\"order\":3,\"prior\":0.0420230627,\"pv\":[\"F4\",\"F12\",\"G12\",\"G11\",\"H11\",\"G13\"],\"scoreLead\":1.2060264,\"scoreMean\":1.2060264,\"scoreSelfplay\":1.94697895,\"scoreStdev\":16.4633322,\"utility\":0.211466356,\"utilityLcb\":0.085237853,\"visits\":30,\"winrate\":0.60660389},{\"lcb\":0.485358475,\"move\":\"B10\",\"order\":4,\"prior\":0.131278798,\"pv\":[\"B10\",\"C9\",\"C17\",\"F18\",\"F11\",\"E10\"],\"scoreLead\":0.66319796,\"scoreMean\":0.66319796,\"scoreSelfplay\":1.16149337,\"scoreStdev\":16.5298758,\"utility\":0.0856993078,\"utilityLcb\":-0.11990448,\"visits\":17,\"winrate\":0.561508026},{\"lcb\":0.526083141,\"move\":\"F3\",\"order\":5,\"prior\":0.0346966982,\"pv\":[\"F3\",\"D5\",\"F5\",\"F12\",\"C17\"],\"scoreLead\":1.07306611,\"scoreMean\":1.07306611,\"scoreSelfplay\":1.74286962,\"scoreStdev\":16.4714684,\"utility\":0.184024152,\"utilityLcb\":-0.0019910976,\"visits\":11,\"winrate\":0.594977678},{\"lcb\":0.505757676,\"move\":\"E4\",\"order\":6,\"prior\":0.0212336313,\"pv\":[\"E4\",\"F12\",\"G12\",\"G11\"],\"scoreLead\":1.16878547,\"scoreMean\":1.16878547,\"scoreSelfplay\":1.92860043,\"scoreStdev\":16.6304298,\"utility\":0.193384295,\"utilityLcb\":-0.0715703784,\"visits\":11,\"winrate\":0.603889036},{\"lcb\":0.452107705,\"move\":\"K17\",\"order\":7,\"prior\":0.0398792885,\"pv\":[\"K17\",\"F4\",\"G18\",\"F17\"],\"scoreLead\":0.940272769,\"scoreMean\":0.940272769,\"scoreSelfplay\":1.54148039,\"scoreStdev\":16.7809488,\"utility\":0.130361631,\"utilityLcb\":-0.206843859,\"visits\":8,\"winrate\":0.576998627},{\"lcb\":0.202285321,\"move\":\"G18\",\"order\":8,\"prior\":0.063391462,\"pv\":[\"G18\",\"F17\",\"K17\",\"J17\",\"J16\"],\"scoreLead\":0.65016347,\"scoreMean\":0.65016347,\"scoreSelfplay\":1.15165872,\"scoreStdev\":16.5944316,\"utility\":0.0637146234,\"utilityLcb\":-0.888338312,\"visits\":6,\"winrate\":0.554897519},{\"lcb\":-0.318763853,\"move\":\"F11\",\"order\":9,\"prior\":0.018193569,\"pv\":[\"F11\",\"D10\",\"E3\",\"D5\"],\"scoreLead\":0.816999339,\"scoreMean\":0.816999339,\"scoreSelfplay\":1.30824296,\"scoreStdev\":16.4787323,\"utility\":0.103293271,\"utilityLcb\":-2.29380311,\"visits\":4,\"winrate\":0.569049623}],\"moves\":[[\"W\",\"F16\"],[\"B\",\"F15\"],[\"W\",\"H18\"],[\"B\",\"H16\"],[\"W\",\"J18\"]],\"policy\":[3.85994463e-06,4.5898737e-06,4.1230328e-06,4.1340686e-06,3.89024854e-06,4.88592786e-06,4.87075658e-06,4.07845391e-06,4.18148738e-06,4.70350324e-06,4.9613509e-06,4.88806245e-06,5.17216176e-06,4.27576924e-06,5.69123904e-06,4.94482765e-06,4.27726627e-06,4.68825465e-06,3.63846289e-06,4.52725089e-06,5.79629841e-06,2.47403896e-05,1.21821013e-05,2.95791269e-05,4.2973028e-05,0.063391462,-1.0,-1.0,9.2931341e-06,1.00932029e-05,1.00695224e-05,1.34367829e-05,1.41410565e-05,3.96248288e-05,1.94925597e-05,7.68301561e-06,5.86631813e-06,4.08863798e-06,4.48944547e-06,6.55278791e-06,0.265088469,-1.0,6.08308892e-06,0.00682745129,-1.0,-1.0,6.31834264e-05,0.0398792885,7.57255912e-05,0.000103204919,5.20802423e-05,-1.0,0.000869563373,2.77736908e-05,1.10493183e-05,6.93228048e-06,4.34016283e-06,4.88897649e-06,4.87059187e-05,-1.0,0.000203838819,-1.0,-1.0,-1.0,-1.0,8.17873024e-06,0.000862720713,0.000297790539,0.000237493252,0.000162482786,0.000101167068,1.10115116e-05,-1.0,8.29272449e-06,6.64964773e-06,3.84621308e-06,5.34664878e-06,-1.0,-1.0,4.4293206e-06,-1.0,-1.0,6.11325459e-06,5.07707819e-06,1.14664963e-05,0.000183663258,0.000367481931,0.000117969481,7.47261874e-05,4.91936516e-05,1.43086745e-05,7.31502405e-06,6.01531019e-06,6.45410728e-06,4.48131823e-06,4.73648879e-06,-1.0,-1.0,-1.0,4.16598004e-06,5.03627143e-06,1.04504152e-05,7.37962473e-05,5.78533327e-05,0.000125230319,9.67909364e-05,8.29186683e-05,8.28005286e-05,6.83888284e-05,1.75997193e-05,9.06298101e-06,-1.0,6.09189556e-06,3.5617752e-06,-1.0,-1.0,-1.0,-1.0,5.44591785e-06,1.02903796e-05,3.18880338e-05,0.000107325039,7.113225e-05,8.77469356e-05,9.17615544e-05,9.52863265e-05,9.23635744e-05,7.43965356e-05,3.51344934e-05,1.66164082e-05,9.96564813e-06,1.25108854e-05,4.53750772e-06,4.2655015e-06,-1.0,-1.0,-1.0,0.0073341555,0.00175204407,0.00948126614,0.000131042398,8.07988836e-05,7.50454928e-05,8.06972384e-05,0.000100923025,0.00011368571,0.000103359758,7.4003372e-05,7.13320405e-05,5.76540078e-05,1.42911504e-05,4.03967442e-06,4.46020886e-06,-1.0,-1.0,-1.0,9.04234639e-06,0.018193569,0.00176781346,0.000115142684,8.31261132e-05,7.53808199e-05,8.65382681e-05,0.000115117378,0.000159817646,0.000227050725,0.000277144922,0.0013509019,0.00021546903,1.45389795e-05,4.89262857e-06,4.05051878e-06,0.131278798,-1.0,0.000884849229,3.29604409e-05,7.62444397e-05,0.000133397916,8.8314795e-05,7.35178619e-05,7.31431646e-05,8.56080005e-05,0.000108944281,0.000152322085,0.000182602787,0.000276273553,0.00112469622,8.08081386e-05,1.2261592e-05,4.3453565e-06,4.01940724e-06,6.3951411e-06,6.32985302e-06,6.7703063e-06,1.81219093e-05,6.01919819e-05,8.68133648e-05,7.95930318e-05,7.27273582e-05,6.06098874e-05,6.41841762e-05,8.02908398e-05,7.20960379e-05,6.81930542e-05,5.76106395e-05,6.65518965e-05,1.83695302e-05,1.09136663e-05,4.09284758e-06,4.90481261e-06,4.50668131e-06,1.15759976e-05,1.43027537e-05,5.89565389e-05,0.000106818268,0.000103223472,8.37199259e-05,6.10200477e-05,3.59001715e-05,3.20882464e-05,2.62034282e-05,1.69225623e-05,1.53496349e-05,1.33967978e-05,1.53864949e-05,1.46555967e-05,1.21258963e-05,3.6794811e-06,5.41604368e-06,5.82888197e-06,4.19811222e-05,8.52374142e-05,0.000180865842,0.000126891216,0.00011168301,8.57324339e-05,5.51622506e-05,2.12204268e-05,1.07294336e-05,9.28245208e-06,9.78310618e-06,1.25254501e-05,9.65422987e-06,1.04290393e-05,1.43549132e-05,1.37319521e-05,3.38108953e-06,6.53842426e-06,7.6958122e-06,0.000339520368,0.00093252107,0.000524876406,0.000162574244,0.000101481171,7.44364806e-05,3.64815169e-05,1.30520984e-05,7.53090717e-06,-1.0,8.01355145e-06,9.33879983e-06,7.75047738e-06,7.57547377e-06,9.80761797e-06,1.25568331e-05,3.32509171e-06,6.48456671e-06,9.95211449e-05,0.00698277028,0.0111793149,0.000193329935,0.000159281437,0.000111911068,5.44242139e-05,1.61774333e-05,1.16899437e-05,8.93352444e-06,7.24966549e-06,8.70071835e-06,9.20843195e-06,4.85412284e-06,4.77248113e-06,4.86344106e-06,1.34216834e-05,4.71998055e-06,5.63531876e-06,3.05125395e-05,0.0122732548,0.131525815,0.0212336313,0.0420230627,0.000434801826,7.08543303e-05,1.74374291e-05,1.22801175e-05,8.70473559e-06,8.3211562e-06,6.22907964e-06,7.25980135e-06,-1.0,-1.0,-1.0,2.35384286e-05,5.5998853e-06,4.8093807e-06,2.22835697e-05,-1.0,0.00585763529,0.169679642,0.0346966982,0.000204027194,4.35904331e-05,1.42364524e-05,1.19058059e-05,9.38749145e-06,5.21113179e-06,-1.0,-1.0,-1.0,-1.0,-1.0,0.000115337374,4.4784897e-06,4.89658669e-06,7.69366943e-06,1.73707213e-05,3.55397679e-05,0.000110495501,3.53127944e-05,1.32399246e-05,1.11204527e-05,8.87290844e-06,7.67869369e-06,6.31117291e-06,6.99956945e-06,5.04497712e-06,-1.0,-1.0,-1.0,-1.0,4.83677104e-06,4.54418569e-06,3.41088821e-06,5.22051278e-06,4.51955475e-06,5.09063011e-06,5.01708701e-06,5.20207004e-06,4.48636365e-06,3.72131308e-06,3.79954031e-06,3.97980375e-06,3.99402734e-06,4.53978873e-06,4.65084349e-06,1.82651547e-05,4.97769224e-06,-1.0,4.08508322e-06,6.35743982e-06,3.80894767e-06,4.19335629e-06],\"rootInfo\":{\"currentPlayer\":\"B\",\"scoreLead\":1.03658954,\"scoreSelfplay\":1.68420193,\"scoreStdev\":16.4279875,\"symHash\":\"13D325B9884B1C847FAA0CDDD1607E02\",\"thisHash\":\"A259B41B94FC114D43A7F388FF438B84\",\"utility\":0.170826714,\"visits\":250,\"winrate\":0.591778947},\"rules\":{\"friendlyPassOk\":false,\"hasButton\":true,\"ko\":\"POSITIONAL\",\"komi\":6.0,\"scoring\":\"AREA\",\"suicide\":true,\"tax\":\"NONE\",\"whiteHandicapBonus\":\"0\"},\"turnNumber\":5,\"whitePlayer\":\"kata1-b40c256-s10312780288-d2513725330\"}";
  //		    String jsonTestMove2 =
  //
  // "{\"blackPlayer\":\"kata1-b40c256-s10312780288-d2513725330\",\"boardXSize\":19,\"boardYSize\":19,\"gameId\":\"105844FE92B031D9\",\"initialPlayer\":\"B\",\"initialStones\":[],\"initialTurnNumber\":0,\"move\":[\"W\",\"D16\"],\"moveInfos\":[{\"lcb\":0.487189075,\"move\":\"D16\",\"order\":0,\"prior\":0.823609948,\"pv\":[\"D16\",\"J3\",\"R3\",\"S3\",\"R10\",\"Q10\",\"S11\",\"Q3\",\"P4\",\"Q2\",\"C10\",\"K4\",\"M5\",\"S8\",\"D17\",\"D11\"],\"scoreLead\":-0.297153029,\"scoreMean\":-0.297153029,\"scoreSelfplay\":-0.388022193,\"scoreStdev\":8.73023537,\"utility\":-0.0640903973,\"utilityLcb\":0.00170650349,\"visits\":601,\"winrate\":0.462819852},{\"lcb\":1.32457619,\"move\":\"D17\",\"order\":1,\"prior\":0.172933906,\"pv\":[\"D17\",\"Q10\",\"J3\",\"D16\"],\"scoreLead\":2.96939391,\"scoreMean\":2.96939391,\"scoreSelfplay\":3.64311456,\"scoreStdev\":9.51754688,\"utility\":0.703302145,\"utilityLcb\":2.08845723,\"visits\":7,\"winrate\":0.811555789}],\"moves\":[[\"B\",\"R4\"],[\"W\",\"Q16\"],[\"B\",\"C4\"],[\"W\",\"C16\"],[\"B\",\"F4\"],[\"W\",\"P3\"],[\"B\",\"Q5\"],[\"W\",\"M3\"],[\"B\",\"R17\"],[\"W\",\"Q17\"],[\"B\",\"J14\"],[\"W\",\"R16\"],[\"B\",\"O4\"],[\"W\",\"O3\"],[\"B\",\"R11\"],[\"W\",\"R9\"],[\"B\",\"S14\"],[\"W\",\"P9\"],[\"B\",\"O17\"],[\"W\",\"S15\"],[\"B\",\"E17\"],[\"W\",\"R6\"],[\"B\",\"C13\"],[\"W\",\"D15\"],[\"B\",\"E13\"],[\"W\",\"F14\"],[\"B\",\"H16\"],[\"W\",\"K17\"],[\"B\",\"C18\"],[\"W\",\"F13\"],[\"B\",\"E11\"],[\"W\",\"K15\"],[\"B\",\"O14\"],[\"W\",\"C14\"],[\"B\",\"B14\"],[\"W\",\"P13\"],[\"B\",\"F16\"],[\"W\",\"D12\"],[\"B\",\"D13\"],[\"W\",\"K14\"],[\"B\",\"J13\"],[\"W\",\"J15\"],[\"B\",\"H15\"],[\"W\",\"K13\"],[\"B\",\"J12\"],[\"W\",\"O13\"],[\"B\",\"P14\"],[\"W\",\"Q13\"],[\"B\",\"M17\"],[\"W\",\"N14\"],[\"B\",\"Q14\"],[\"W\",\"R13\"],[\"B\",\"N15\"],[\"W\",\"L18\"],[\"B\",\"P16\"],[\"W\",\"M14\"],[\"B\",\"P18\"],[\"W\",\"N18\"],[\"B\",\"Q18\"],[\"W\",\"S17\"],[\"B\",\"M18\"],[\"W\",\"B15\"],[\"B\",\"B13\"],[\"W\",\"G11\"],[\"B\",\"E12\"],[\"W\",\"J11\"],[\"B\",\"K12\"],[\"W\",\"B18\"],[\"B\",\"C17\"],[\"W\",\"G12\"],[\"B\",\"K11\"],[\"W\",\"R14\"],[\"B\",\"E15\"],[\"W\",\"N16\"],[\"B\",\"N17\"],[\"W\",\"O15\"],[\"B\",\"M15\"],[\"W\",\"M16\"],[\"B\",\"H17\"],[\"W\",\"J10\"],[\"B\",\"K10\"],[\"W\",\"J8\"],[\"B\",\"K9\"],[\"W\",\"G8\"],[\"B\",\"L7\"],[\"W\",\"E8\"],[\"B\",\"F9\"],[\"W\",\"F10\"],[\"B\",\"E9\"],[\"W\",\"G9\"],[\"B\",\"Q8\"],[\"W\",\"M19\"],[\"B\",\"L17\"],[\"W\",\"J17\"],[\"B\",\"B1\"],[\"W\",\"J18\"],[\"B\",\"F8\"],[\"W\",\"J4\"],[\"B\",\"F7\"],[\"W\",\"E10\"],[\"B\",\"D10\"],[\"W\",\"G6\"],[\"B\",\"H5\"],[\"W\",\"J6\"],[\"B\",\"J5\"],[\"W\",\"K6\"],[\"B\",\"H4\"],[\"W\",\"K5\"],[\"B\",\"Q9\"],[\"W\",\"P7\"],[\"B\",\"Q7\"],[\"W\",\"B17\"],[\"B\",\"Q6\"],[\"W\",\"E5\"],[\"B\",\"F6\"],[\"W\",\"F5\"],[\"B\",\"G5\"],[\"W\",\"C5\"],[\"B\",\"D5\"],[\"W\",\"D6\"],[\"B\",\"D4\"],[\"W\",\"E6\"],[\"B\",\"C6\"],[\"W\",\"C7\"],[\"B\",\"B5\"],[\"W\",\"D9\"],[\"B\",\"F11\"],[\"W\",\"G10\"],[\"B\",\"K18\"],[\"W\",\"K19\"],[\"B\",\"A2\"],[\"W\",\"D14\"],[\"B\",\"E4\"],[\"W\",\"D8\"],[\"B\",\"E14\"]],\"policy\":[5.9945437e-06,7.02433044e-06,1.02003205e-05,6.29290116e-06,5.9085678e-06,6.06618278e-06,5.64770244e-06,6.0159914e-06,5.29377439e-06,-1.0,4.47222783e-06,-1.0,3.82775897e-06,4.24283826e-06,4.59863122e-06,5.0811027e-06,4.98126246e-06,5.08181029e-06,5.09124538e-06,6.43821477e-06,-1.0,-1.0,0.000222201255,1.08129652e-05,6.18105105e-06,5.35424533e-06,5.99747727e-06,-1.0,5.05583239e-06,-1.0,-1.0,-1.0,2.912019e-06,-1.0,-1.0,4.8495458e-06,5.13844861e-06,5.09625352e-06,5.93991217e-06,-1.0,-1.0,0.172933906,-1.0,4.96925304e-06,5.25234782e-06,-1.0,-1.0,-1.0,-1.0,-1.0,-1.0,-1.0,4.56831322e-06,-1.0,-1.0,-1.0,4.95449876e-06,5.63159392e-06,6.54740188e-06,-1.0,0.823609948,2.2266729e-06,-1.0,5.33940693e-06,-1.0,4.41773909e-06,6.50407492e-06,4.57073611e-06,-1.0,-1.0,4.70758596e-06,-1.0,-1.0,-1.0,4.79143364e-06,5.15061583e-06,7.30552711e-06,-1.0,4.93948664e-06,-1.0,-1.0,5.055987e-06,7.40426412e-06,-1.0,-1.0,-1.0,4.32659499e-06,-1.0,-1.0,-1.0,5.13512714e-06,5.40147039e-06,4.9937953e-06,-1.0,5.50024151e-06,6.92287131e-06,-1.0,-1.0,-1.0,-1.0,-1.0,8.1776916e-06,0.000867421739,-1.0,-1.0,5.2377768e-06,-1.0,-1.0,-1.0,-1.0,-1.0,-1.0,-1.0,5.8634364e-06,5.84254894e-06,-1.0,-1.0,-1.0,-1.0,-1.0,6.75326191e-06,7.83736505e-06,-1.0,-1.0,4.77175672e-06,5.32693093e-06,5.88872081e-06,-1.0,-1.0,-1.0,-1.0,6.84995484e-06,5.94739458e-06,6.00766134e-06,5.06371725e-06,5.18800334e-06,-1.0,-1.0,6.10467214e-06,-1.0,6.48785044e-06,-1.0,-1.0,6.07560287e-06,6.43434123e-06,7.12635892e-06,6.559339e-06,6.55351823e-06,7.45819625e-06,9.47022545e-06,7.25987593e-06,6.2395543e-06,5.61419347e-06,6.05859168e-06,5.42676526e-06,6.69884821e-06,-1.0,-1.0,-1.0,5.3589556e-06,-1.0,-1.0,6.02061755e-06,7.84445456e-06,7.6645938e-06,9.02484862e-06,1.21333442e-05,1.48786212e-05,-1.0,2.4980005e-05,5.82571874e-06,5.60191393e-06,6.18757986e-06,7.90640843e-06,-1.0,-1.0,-1.0,-1.0,5.51697894e-06,-1.0,-1.0,5.94968105e-06,2.46804575e-05,6.22271618e-05,9.24186752e-05,4.87290927e-05,0.000165614372,8.43101006e-05,9.03502678e-06,5.24867755e-06,5.83383007e-06,7.22158575e-06,5.21582933e-06,-1.0,-1.0,-1.0,-1.0,5.69077702e-06,5.93699042e-06,-1.0,6.50586753e-06,4.0418221e-05,6.92876638e-05,1.9261548e-05,-1.0,-1.0,-1.0,1.12472335e-05,5.60195122e-06,5.77002265e-06,5.85097996e-06,5.43980741e-06,-1.0,-1.0,-1.0,-1.0,6.18164086e-06,-1.0,1.01094811e-05,8.85810277e-06,1.22405954e-05,1.50928327e-05,7.0991241e-06,7.75920307e-06,-1.0,7.35637559e-06,6.23622327e-06,5.58112788e-06,6.1578462e-06,5.75795139e-06,-1.0,5.06122569e-06,5.20325011e-06,-1.0,6.22323569e-06,5.04898071e-06,6.63777928e-06,6.97468658e-06,-1.0,1.36716844e-05,0.00010322345,7.68261179e-06,-1.0,-1.0,6.52720246e-06,6.72850501e-06,5.53986638e-06,5.731692e-06,6.52816152e-06,-1.0,-1.0,-1.0,-1.0,-1.0,5.17847775e-06,-1.0,-1.0,6.82168775e-06,2.9438721e-05,2.84897524e-05,4.59873008e-05,7.13722693e-06,-1.0,-1.0,6.54706491e-06,5.25422638e-06,5.36190009e-06,-1.0,-1.0,-1.0,-1.0,-1.0,-1.0,-1.0,-1.0,-1.0,7.08717107e-06,8.37986317e-06,7.57212092e-06,1.07441047e-05,7.28234272e-06,-1.0,9.12757332e-06,1.33047288e-05,5.33316279e-06,4.91791661e-06,4.63040897e-06,-1.0,-1.0,-1.0,-1.0,5.32437116e-06,-1.0,-1.0,6.8883819e-06,7.45053239e-06,7.81751623e-06,7.41282747e-06,-1.0,1.80053648e-05,8.02401883e-06,-1.0,2.78274802e-05,5.55912493e-06,4.98104828e-06,4.90685579e-06,4.26245515e-06,4.53675375e-06,4.75405568e-06,5.10445943e-06,7.55985457e-06,2.6133248e-05,6.590267e-05,6.95785184e-06,7.150697e-06,-1.0,6.20428455e-06,-1.0,-1.0,8.12475264e-06,5.72638928e-05,2.57403608e-05,5.53568316e-06,-1.0,5.18596562e-06,5.06871265e-06,5.24173947e-06,5.8621672e-06,6.19884941e-06,5.7975908e-06,6.12643771e-06,6.77430899e-06,6.01869442e-06,6.93743777e-06,6.09136032e-06,6.50722041e-06,5.97014832e-06,6.5576628e-06,8.21513368e-06,2.49097084e-05,9.09296796e-06,6.16545003e-06,-1.0,-1.0,4.83915574e-06,5.25914948e-06,5.74609703e-06,5.92123706e-06,6.11503765e-06,6.3341331e-06,5.78555046e-06,5.91391199e-06,5.66032531e-06,5.80015694e-06,5.73021634e-06,6.12010172e-06,6.13545944e-06,6.34796925e-06,5.81785707e-06,5.36176731e-06,5.5913778900000004e-06,1.06019877e-06],\"rootInfo\":{\"currentPlayer\":\"W\",\"scoreLead\":-0.265064736,\"scoreSelfplay\":-0.348400569,\"scoreStdev\":8.74696019,\"symHash\":\"0FF8B6E4FEEA7B5673ECCA456AFEB88F\",\"thisHash\":\"26A7CCB8ACFCDF6F9AF9F6F7C462CABD\",\"utility\":-0.0565347644,\"visits\":609,\"winrate\":0.466263851},\"rules\":{\"friendlyPassOk\":false,\"hasButton\":false,\"ko\":\"POSITIONAL\",\"komi\":-36.0,\"scoring\":\"TERRITORY\",\"suicide\":true,\"tax\":\"NONE\",\"whiteHandicapBonus\":\"0\"},\"turnNumber\":135,\"whitePlayer\":\"kata1-b40c256-s10312780288-d2513725330\"}";
  //		    String jsonTestMove3 =
  //
  // "{\"blackPlayer\":\"kata1-b40c256-s10312780288-d2513725330\",\"boardXSize\":19,\"boardYSize\":19,\"gameId\":\"105844FE92B031D9\",\"initialPlayer\":\"B\",\"initialStones\":[],\"initialTurnNumber\":0,\"move\":[\"B\",\"J3\"],\"moveInfos\":[{\"lcb\":0.430815853,\"move\":\"J3\",\"order\":0,\"prior\":0.16544427,\"pv\":[\"J3\",\"R3\",\"S3\",\"R10\",\"Q10\",\"S11\",\"R2\",\"C10\",\"N4\",\"L2\",\"K4\",\"Q3\",\"K2\",\"S2\",\"S1\"],\"scoreLead\":-0.40152266,\"scoreMean\":-0.40152266,\"scoreSelfplay\":-0.531906974,\"scoreStdev\":9.15213063,\"utility\":-0.0988364037,\"utilityLcb\":-0.141818664,\"visits\":1237,\"winrate\":0.446735208},{\"lcb\":0.231062937,\"move\":\"Q10\",\"order\":1,\"prior\":0.196953416,\"pv\":[\"Q10\",\"J3\",\"Q2\",\"G3\",\"H3\",\"H2\",\"G2\",\"F2\"],\"scoreLead\":-1.09391724,\"scoreMean\":-1.09391724,\"scoreSelfplay\":-1.47745694,\"scoreStdev\":7.93888159,\"utility\":-0.396421215,\"utilityLcb\":-0.609949342,\"visits\":33,\"winrate\":0.310147429},{\"lcb\":0.20198922,\"move\":\"S12\",\"order\":2,\"prior\":0.121686354,\"pv\":[\"S12\",\"J3\",\"Q2\",\"Q10\",\"R10\"],\"scoreLead\":-1.36503146,\"scoreMean\":-1.36503146,\"scoreSelfplay\":-1.8796747599999999,\"scoreStdev\":9.85625507,\"utility\":-0.388591878,\"utilityLcb\":-0.664436128,\"visits\":21,\"winrate\":0.304153757},{\"lcb\":0.238628132,\"move\":\"P11\",\"order\":3,\"prior\":0.0943727344,\"pv\":[\"P11\",\"J3\",\"Q2\",\"G3\",\"H3\"],\"scoreLead\":-1.26031673,\"scoreMean\":-1.26031673,\"scoreSelfplay\":-1.70147704,\"scoreStdev\":8.86598983,\"utility\":-0.348779638,\"utilityLcb\":-0.560317401,\"visits\":19,\"winrate\":0.316975451},{\"lcb\":0.132132912,\"move\":\"L13\",\"order\":4,\"prior\":0.0608975925,\"pv\":[\"L13\",\"L12\",\"J3\",\"R10\",\"Q10\",\"S11\"],\"scoreLead\":-1.27474619,\"scoreMean\":-1.27474619,\"scoreSelfplay\":-1.72575511,\"scoreStdev\":8.80601327,\"utility\":-0.390761022,\"utilityLcb\":-0.842904691,\"visits\":14,\"winrate\":0.29959353},{\"lcb\":0.0862741277,\"move\":\"L3\",\"order\":5,\"prior\":0.0299970843,\"pv\":[\"L3\",\"R2\",\"M2\",\"M4\"],\"scoreLead\":-1.65057393,\"scoreMean\":-1.65057393,\"scoreSelfplay\":-2.25918277,\"scoreStdev\":9.89471241,\"utility\":-0.475496636,\"utilityLcb\":-0.944879238,\"visits\":10,\"winrate\":0.260119536},{\"lcb\":-0.00278644284,\"move\":\"F18\",\"order\":6,\"prior\":0.0654440522,\"pv\":[\"F18\",\"J3\",\"P11\",\"R2\",\"S2\"],\"scoreLead\":-4.99380175,\"scoreMean\":-4.99380175,\"scoreSelfplay\":-5.69604028,\"scoreStdev\":7.64507169,\"utility\":-1.02520477,\"utilityLcb\":-1.10352483,\"visits\":15,\"winrate\":0.0262209853},{\"lcb\":0.00254441945,\"move\":\"H6\",\"order\":7,\"prior\":0.0339142047,\"pv\":[\"H6\",\"G7\",\"J3\",\"R10\",\"Q10\",\"S11\",\"Q2\",\"K2\"],\"scoreLead\":-1.99267087,\"scoreMean\":-1.99267087,\"scoreSelfplay\":-2.53085314,\"scoreStdev\":8.44001473,\"utility\":-0.573281825,\"utilityLcb\":-1.10250888,\"visits\":11,\"winrate\":0.198554439},{\"lcb\":0.0577722425,\"move\":\"P10\",\"order\":8,\"prior\":0.0251434483,\"pv\":[\"P10\",\"J3\",\"Q2\",\"G3\",\"H3\",\"H2\"],\"scoreLead\":-1.57010683,\"scoreMean\":-1.57010683,\"scoreSelfplay\":-2.08191748,\"scoreStdev\":8.19570706,\"utility\":-0.529040468,\"utilityLcb\":-1.04234351,\"visits\":9,\"winrate\":0.24788448},{\"lcb\":0.0132564879,\"move\":\"M4\",\"order\":9,\"prior\":0.024624208,\"pv\":[\"M4\",\"R2\",\"S2\",\"R3\"],\"scoreLead\":-2.36608871,\"scoreMean\":-2.36608871,\"scoreSelfplay\":-3.02687714,\"scoreStdev\":8.85534411,\"utility\":-0.679160803,\"utilityLcb\":-1.10622741,\"visits\":9,\"winrate\":0.171429304},{\"lcb\":0.106310693,\"move\":\"Q2\",\"order\":10,\"prior\":0.020450918,\"pv\":[\"Q2\",\"J3\",\"S12\",\"Q10\"],\"scoreLead\":-1.34188693,\"scoreMean\":-1.34188693,\"scoreSelfplay\":-1.82195361,\"scoreStdev\":8.77153394,\"utility\":-0.456464619,\"utilityLcb\":-0.938503696,\"visits\":8,\"winrate\":0.284843685},{\"lcb\":-0.394718983,\"move\":\"Q3\",\"order\":11,\"prior\":0.0105356546,\"pv\":[\"Q3\",\"R10\",\"Q10\",\"S11\",\"J3\"],\"scoreLead\":-1.24471872,\"scoreMean\":-1.24471872,\"scoreSelfplay\":-1.63594914,\"scoreStdev\":8.623612,\"utility\":-0.354045436,\"utilityLcb\":-2.2539779,\"visits\":6,\"winrate\":0.308959706},{\"lcb\":0.0313850881,\"move\":\"O16\",\"order\":12,\"prior\":0.0206256472,\"pv\":[\"O16\",\"L15\",\"S12\",\"J3\",\"Q2\"],\"scoreLead\":-2.20917735,\"scoreMean\":-2.20917735,\"scoreSelfplay\":-2.88182557,\"scoreStdev\":8.81665836,\"utility\":-0.685703611,\"utilityLcb\":-1.07465992,\"visits\":8,\"winrate\":0.174953436},{\"lcb\":0.107296433,\"move\":\"Q11\",\"order\":13,\"prior\":0.0113245174,\"pv\":[\"Q11\",\"J3\",\"Q2\",\"S11\",\"S12\"],\"scoreLead\":-2.1601662,\"scoreMean\":-2.1601662,\"scoreSelfplay\":-2.64215242,\"scoreStdev\":8.88134779,\"utility\":-0.643944865,\"utilityLcb\":-0.880309758,\"visits\":6,\"winrate\":0.194838986},{\"lcb\":0.0291352263,\"move\":\"O19\",\"order\":14,\"prior\":0.00878981221,\"pv\":[\"O19\",\"N19\",\"O18\",\"L19\"],\"scoreLead\":-1.6077363,\"scoreMean\":-1.6077363,\"scoreSelfplay\":-2.26305945,\"scoreStdev\":9.05480575,\"utility\":-0.521164619,\"utilityLcb\":-1.10542223,\"visits\":6,\"winrate\":0.245526933},{\"lcb\":-3.77968475,\"move\":\"L15\",\"order\":15,\"prior\":0.00293598371,\"pv\":[\"L15\",\"L16\",\"L13\"],\"scoreLead\":-0.743081013,\"scoreMean\":-0.743081013,\"scoreSelfplay\":-1.1183821,\"scoreStdev\":9.30451338,\"utility\":-0.221609508,\"utilityLcb\":-11.4894428,\"visits\":3,\"winrate\":0.393490901},{\"lcb\":-0.0145148407,\"move\":\"S6\",\"order\":16,\"prior\":0.0121592078,\"pv\":[\"S6\",\"J3\",\"P11\",\"G3\",\"N4\"],\"scoreLead\":-7.41746929,\"scoreMean\":-7.41746929,\"scoreSelfplay\":-7.83015589,\"scoreStdev\":8.21705661,\"utility\":-1.07034573,\"utilityLcb\":-1.14755182,\"visits\":7,\"winrate\":0.0140800068},{\"lcb\":-0.301655538,\"move\":\"K4\",\"order\":17,\"prior\":0.00955134444,\"pv\":[\"K4\",\"R2\",\"L3\"],\"scoreLead\":-2.19575556,\"scoreMean\":-2.19575556,\"scoreSelfplay\":-2.99395562,\"scoreStdev\":9.04520998,\"utility\":-0.661950557,\"utilityLcb\":-1.96368311,\"visits\":6,\"winrate\":0.180467628},{\"lcb\":-0.0389014874,\"move\":\"N4\",\"order\":18,\"prior\":0.00895216037,\"pv\":[\"N4\",\"R2\",\"P11\"],\"scoreLead\":-2.17355697,\"scoreMean\":-2.17355697,\"scoreSelfplay\":-2.76665872,\"scoreStdev\":8.61053892,\"utility\":-0.586956516,\"utilityLcb\":-1.21142662,\"visits\":6,\"winrate\":0.192383737},{\"lcb\":-0.135128379,\"move\":\"R10\",\"order\":19,\"prior\":0.00702199386,\"pv\":[\"R10\",\"J3\",\"Q2\",\"G3\",\"H3\"],\"scoreLead\":-2.06921413,\"scoreMean\":-2.06921413,\"scoreSelfplay\":-2.65888472,\"scoreStdev\":8.04512965,\"utility\":-0.705614502,\"utilityLcb\":-1.50173329,\"visits\":5,\"winrate\":0.15973043},{\"lcb\":-0.0571549593,\"move\":\"S7\",\"order\":20,\"prior\":0.00600847835,\"pv\":[\"S7\",\"J3\",\"H3\",\"C10\"],\"scoreLead\":-6.5768306,\"scoreMean\":-6.5768306,\"scoreSelfplay\":-7.23732991,\"scoreStdev\":8.52626624,\"utility\":-1.05069052,\"utilityLcb\":-1.25271483,\"visits\":5,\"winrate\":0.0176688585},{\"lcb\":-0.225669131,\"move\":\"C10\",\"order\":21,\"prior\":0.00546428934,\"pv\":[\"C10\",\"J3\",\"S12\"],\"scoreLead\":-1.85793102,\"scoreMean\":-1.85793102,\"scoreSelfplay\":-2.40616825,\"scoreStdev\":8.24258229,\"utility\":-0.635705101,\"utilityLcb\":-1.78426535,\"visits\":5,\"winrate\":0.199723553},{\"lcb\":-0.90933986,\"move\":\"J2\",\"order\":22,\"prior\":0.00504765101,\"pv\":[\"J2\",\"R10\",\"Q10\"],\"scoreLead\":-3.18632583,\"scoreMean\":-3.18632583,\"scoreSelfplay\":-4.05984775,\"scoreStdev\":8.59715962,\"utility\":-0.799194043,\"utilityLcb\":-3.52919379,\"visits\":4,\"winrate\":0.101771157},{\"lcb\":-0.962500156,\"move\":\"B7\",\"order\":23,\"prior\":0.00488622906,\"pv\":[\"B7\",\"C10\",\"J3\"],\"scoreLead\":-2.40918217,\"scoreMean\":-2.40918217,\"scoreSelfplay\":-3.13289893,\"scoreStdev\":9.29980225,\"utility\":-0.701548171,\"utilityLcb\":-3.77640339,\"visits\":4,\"winrate\":0.176335109},{\"lcb\":-0.858794755,\"move\":\"E7\",\"order\":24,\"prior\":0.00481157424,\"pv\":[\"E7\",\"D7\",\"Q10\",\"J3\"],\"scoreLead\":-3.16230214,\"scoreMean\":-3.16230214,\"scoreSelfplay\":-3.58654177,\"scoreStdev\":8.17545891,\"utility\":-0.82040485,\"utilityLcb\":-3.46104529,\"visits\":4,\"winrate\":0.118796428},{\"lcb\":-0.472943418,\"move\":\"S8\",\"order\":25,\"prior\":0.00444644829,\"pv\":[\"S8\",\"J3\",\"Q2\",\"G3\"],\"scoreLead\":-5.47796237,\"scoreMean\":-5.47796237,\"scoreSelfplay\":-5.81049049,\"scoreStdev\":8.19962311,\"utility\":-1.03365063,\"utilityLcb\":-2.3835636,\"visits\":4,\"winrate\":0.027024348},{\"lcb\":-0.122917483,\"move\":\"G19\",\"order\":26,\"prior\":0.00439604651,\"pv\":[\"G19\",\"J3\",\"S12\"],\"scoreLead\":-7.80047278,\"scoreMean\":-7.80047278,\"scoreSelfplay\":-8.15680159,\"scoreStdev\":8.62356379,\"utility\":-1.07426654,\"utilityLcb\":-1.44546883,\"visits\":4,\"winrate\":0.0145648461},{\"lcb\":-2.47524091,\"move\":\"A15\",\"order\":27,\"prior\":0.00347745488,\"pv\":[\"A15\",\"A16\",\"J3\"],\"scoreLead\":-1.93678915,\"scoreMean\":-1.93678915,\"scoreSelfplay\":-2.67851724,\"scoreStdev\":9.99030322,\"utility\":-0.503893546,\"utilityLcb\":-7.85358251,\"visits\":4,\"winrate\":0.246866117},{\"lcb\":-1.12855161,\"move\":\"N19\",\"order\":28,\"prior\":0.00327888969,\"pv\":[\"N19\",\"O19\",\"J3\",\"R10\"],\"scoreLead\":-1.92224544,\"scoreMean\":-1.92224544,\"scoreSelfplay\":-2.52115977,\"scoreStdev\":8.84672547,\"utility\":-0.599174436,\"utilityLcb\":-4.24542957,\"visits\":4,\"winrate\":0.221843192},{\"lcb\":-0.519132091,\"move\":\"O18\",\"order\":29,\"prior\":0.00326960161,\"pv\":[\"O18\",\"N19\",\"O19\",\"L19\"],\"scoreLead\":-1.64658424,\"scoreMean\":-1.64658424,\"scoreSelfplay\":-2.32867324,\"scoreStdev\":9.65454348,\"utility\":-0.52901175,\"utilityLcb\":-2.62384992,\"visits\":4,\"winrate\":0.256733898},{\"lcb\":-2.45769102,\"move\":\"C9\",\"order\":30,\"prior\":0.00242334302,\"pv\":[\"C9\",\"C10\",\"D11\"],\"scoreLead\":-2.95945382,\"scoreMean\":-2.95945382,\"scoreSelfplay\":-3.26577608,\"scoreStdev\":8.62485286,\"utility\":-0.738618406,\"utilityLcb\":-7.8103986,\"visits\":3,\"winrate\":0.161486831},{\"lcb\":-0.191132567,\"move\":\"C5\",\"order\":31,\"prior\":0.00233952166,\"pv\":[\"C5\",\"J3\"],\"scoreLead\":-10.6876146,\"scoreMean\":-10.6876146,\"scoreSelfplay\":-10.9745042,\"scoreStdev\":8.90956772,\"utility\":-1.1208001,\"utilityLcb\":-1.65100943,\"visits\":3,\"winrate\":0.00524125709},{\"lcb\":-2.17837534,\"move\":\"J16\",\"order\":32,\"prior\":0.00166551233,\"pv\":[\"J16\",\"K16\",\"J3\"],\"scoreLead\":-1.23019816,\"scoreMean\":-1.23019816,\"scoreSelfplay\":-1.69050344,\"scoreStdev\":9.4947255,\"utility\":-0.384864667,\"utilityLcb\":-7.14461571,\"visits\":3,\"winrate\":0.325236162},{\"lcb\":-1.59439626,\"move\":\"R2\",\"order\":33,\"prior\":0.0016535176,\"pv\":[\"R2\",\"R10\"],\"scoreLead\":-1.2299521,\"scoreMean\":-1.2299521,\"scoreSelfplay\":-1.64771248,\"scoreStdev\":9.08908578,\"utility\":-0.357780591,\"utilityLcb\":-5.52157896,\"visits\":3,\"winrate\":0.318121652},{\"lcb\":-0.906160594,\"move\":\"L14\",\"order\":34,\"prior\":0.00133472169,\"pv\":[\"L14\",\"M12\"],\"scoreLead\":-4.30955982,\"scoreMean\":-4.30955982,\"scoreSelfplay\":-5.00663948,\"scoreStdev\":9.21536709,\"utility\":-0.907098074,\"utilityLcb\":-2.7,\"visits\":2,\"winrate\":0.093839406},{\"lcb\":-0.948311004,\"move\":\"S18\",\"order\":35,\"prior\":0.00111473724,\"pv\":[\"S18\",\"J3\"],\"scoreLead\":-8.5282321,\"scoreMean\":-8.5282321,\"scoreSelfplay\":-7.09235668,\"scoreStdev\":11.70981,\"utility\":-0.955516177,\"utilityLcb\":-2.7,\"visits\":2,\"winrate\":0.0516889961},{\"lcb\":-0.98415416,\"move\":\"D18\",\"order\":36,\"prior\":0.00110456371,\"pv\":[\"D18\",\"J3\"],\"scoreLead\":-7.18082428,\"scoreMean\":-7.18082428,\"scoreSelfplay\":-7.46874523,\"scoreStdev\":8.36073379,\"utility\":-1.06131231,\"utilityLcb\":-2.7,\"visits\":2,\"winrate\":0.0158458399}],\"moves\":[[\"B\",\"R4\"],[\"W\",\"Q16\"],[\"B\",\"C4\"],[\"W\",\"C16\"],[\"B\",\"F4\"],[\"W\",\"P3\"],[\"B\",\"Q5\"],[\"W\",\"M3\"],[\"B\",\"R17\"],[\"W\",\"Q17\"],[\"B\",\"J14\"],[\"W\",\"R16\"],[\"B\",\"O4\"],[\"W\",\"O3\"],[\"B\",\"R11\"],[\"W\",\"R9\"],[\"B\",\"S14\"],[\"W\",\"P9\"],[\"B\",\"O17\"],[\"W\",\"S15\"],[\"B\",\"E17\"],[\"W\",\"R6\"],[\"B\",\"C13\"],[\"W\",\"D15\"],[\"B\",\"E13\"],[\"W\",\"F14\"],[\"B\",\"H16\"],[\"W\",\"K17\"],[\"B\",\"C18\"],[\"W\",\"F13\"],[\"B\",\"E11\"],[\"W\",\"K15\"],[\"B\",\"O14\"],[\"W\",\"C14\"],[\"B\",\"B14\"],[\"W\",\"P13\"],[\"B\",\"F16\"],[\"W\",\"D12\"],[\"B\",\"D13\"],[\"W\",\"K14\"],[\"B\",\"J13\"],[\"W\",\"J15\"],[\"B\",\"H15\"],[\"W\",\"K13\"],[\"B\",\"J12\"],[\"W\",\"O13\"],[\"B\",\"P14\"],[\"W\",\"Q13\"],[\"B\",\"M17\"],[\"W\",\"N14\"],[\"B\",\"Q14\"],[\"W\",\"R13\"],[\"B\",\"N15\"],[\"W\",\"L18\"],[\"B\",\"P16\"],[\"W\",\"M14\"],[\"B\",\"P18\"],[\"W\",\"N18\"],[\"B\",\"Q18\"],[\"W\",\"S17\"],[\"B\",\"M18\"],[\"W\",\"B15\"],[\"B\",\"B13\"],[\"W\",\"G11\"],[\"B\",\"E12\"],[\"W\",\"J11\"],[\"B\",\"K12\"],[\"W\",\"B18\"],[\"B\",\"C17\"],[\"W\",\"G12\"],[\"B\",\"K11\"],[\"W\",\"R14\"],[\"B\",\"E15\"],[\"W\",\"N16\"],[\"B\",\"N17\"],[\"W\",\"O15\"],[\"B\",\"M15\"],[\"W\",\"M16\"],[\"B\",\"H17\"],[\"W\",\"J10\"],[\"B\",\"K10\"],[\"W\",\"J8\"],[\"B\",\"K9\"],[\"W\",\"G8\"],[\"B\",\"L7\"],[\"W\",\"E8\"],[\"B\",\"F9\"],[\"W\",\"F10\"],[\"B\",\"E9\"],[\"W\",\"G9\"],[\"B\",\"Q8\"],[\"W\",\"M19\"],[\"B\",\"L17\"],[\"W\",\"J17\"],[\"B\",\"B1\"],[\"W\",\"J18\"],[\"B\",\"F8\"],[\"W\",\"J4\"],[\"B\",\"F7\"],[\"W\",\"E10\"],[\"B\",\"D10\"],[\"W\",\"G6\"],[\"B\",\"H5\"],[\"W\",\"J6\"],[\"B\",\"J5\"],[\"W\",\"K6\"],[\"B\",\"H4\"],[\"W\",\"K5\"],[\"B\",\"Q9\"],[\"W\",\"P7\"],[\"B\",\"Q7\"],[\"W\",\"B17\"],[\"B\",\"Q6\"],[\"W\",\"E5\"],[\"B\",\"F6\"],[\"W\",\"F5\"],[\"B\",\"G5\"],[\"W\",\"C5\"],[\"B\",\"D5\"],[\"W\",\"D6\"],[\"B\",\"D4\"],[\"W\",\"E6\"],[\"B\",\"C6\"],[\"W\",\"C7\"],[\"B\",\"B5\"],[\"W\",\"D9\"],[\"B\",\"F11\"],[\"W\",\"G10\"],[\"B\",\"K18\"],[\"W\",\"K19\"],[\"B\",\"A2\"],[\"W\",\"D14\"],[\"B\",\"E4\"],[\"W\",\"D8\"],[\"B\",\"E14\"],[\"W\",\"D16\"]],\"policy\":[7.83243377e-06,1.77625625e-05,1.0992484e-05,9.73477927e-06,9.87713884e-06,9.64389983e-06,1.04934861e-05,1.31532261e-05,9.93021877e-06,-1.0,-1.0,-1.0,0.00310152629,0.00182882103,7.11272951e-05,9.81367066e-06,9.71492682e-06,9.05295656e-06,8.52255562e-06,1.09122066e-05,-1.0,-1.0,1.02835002e-05,1.11228128e-05,1.36155568e-05,1.09138273e-05,1.53187539e-05,-1.0,-1.0,-1.0,-1.0,-1.0,0.0011462901,-1.0,-1.0,5.1082854e-05,0.000789459504,2.01228231e-05,1.21918811e-05,-1.0,-1.0,1.26436998e-05,-1.0,8.870682e-06,8.91536456e-06,-1.0,-1.0,-1.0,-1.0,-1.0,-1.0,-1.0,1.06584339e-05,-1.0,-1.0,-1.0,2.40012942e-05,3.34428551e-05,2.57357879e-05,-1.0,-1.0,1.0775203e-05,-1.0,8.9151963e-06,-1.0,0.00146587193,0.000879493775,6.87810971e-05,-1.0,-1.0,0.000134084112,-1.0,-1.0,-1.0,1.14897866e-05,8.91490708e-06,0.00108797452,-1.0,-1.0,-1.0,-1.0,1.09979519e-05,9.66834796e-06,-1.0,-1.0,-1.0,0.00235965941,-1.0,-1.0,-1.0,0.000101275982,9.21179344e-06,8.32469232e-06,-1.0,9.62107151e-06,1.06320986e-05,-1.0,-1.0,-1.0,-1.0,-1.0,1.21812382e-05,1.02682079e-05,-1.0,-1.0,1.56122333e-05,-1.0,-1.0,-1.0,-1.0,-1.0,-1.0,-1.0,9.20960247e-06,8.56168754e-06,-1.0,-1.0,-1.0,-1.0,-1.0,2.53017388e-05,9.61434398e-06,-1.0,-1.0,0.0401590541,0.000308173883,2.59902863e-05,-1.0,-1.0,-1.0,-1.0,1.40643242e-05,9.98983614e-06,8.39937638e-06,9.0132753e-06,9.17120269e-06,-1.0,-1.0,1.11792433e-05,-1.0,8.84654401e-06,-1.0,-1.0,1.58643525e-05,1.6804499e-05,2.01022267e-05,1.3061117e-05,1.26909954e-05,1.2153615e-05,4.01470752e-05,0.156847432,1.27078993e-05,9.3188246e-06,1.06987936e-05,1.03080438e-05,1.08296008e-05,-1.0,-1.0,-1.0,9.08318634e-06,-1.0,-1.0,9.79031756e-06,1.37749912e-05,3.28700262e-05,8.76759514e-05,0.114378795,0.00292732404,-1.0,0.00051989255,1.47416995e-05,1.00215411e-05,1.39033145e-05,0.00536277052,-1.0,-1.0,-1.0,-1.0,8.48823584e-06,-1.0,-1.0,9.82075562e-06,1.32182613e-05,1.80480092e-05,5.40119145e-05,0.0237949118,0.277655065,0.00722311623,2.41387243e-05,1.1326465e-05,1.02638442e-05,1.44036367e-05,0.000534986088,-1.0,-1.0,-1.0,-1.0,1.03084676e-05,1.03768634e-05,-1.0,1.00930683e-05,1.2605351e-05,1.4547245e-05,1.51780932e-05,-1.0,-1.0,-1.0,1.60523232e-05,1.0981249e-05,1.0328713e-05,1.42997105e-05,1.76634567e-05,-1.0,-1.0,-1.0,-1.0,1.28886395e-05,-1.0,1.23787186e-05,1.01755732e-05,1.20992718e-05,1.34933016e-05,1.23054488e-05,1.71229331e-05,-1.0,1.32805544e-05,1.33917611e-05,1.01164269e-05,1.03699967e-05,0.00423822692,-1.0,4.30628643e-05,0.0001782646,-1.0,1.71678348e-05,1.57753057e-05,1.35651189e-05,1.63208224e-05,-1.0,1.33333961e-05,1.56188944e-05,1.16863193e-05,-1.0,-1.0,1.11660429e-05,1.24207136e-05,9.95662504e-06,8.84754172e-06,9.56117765e-06,-1.0,-1.0,-1.0,-1.0,-1.0,0.0147764524,-1.0,-1.0,1.30202916e-05,2.59456137e-05,1.74985289e-05,1.47290793e-05,1.09913126e-05,-1.0,-1.0,1.14822751e-05,9.89991622e-06,8.50834203e-06,-1.0,8.10868187e-06,-1.0,-1.0,-1.0,-1.0,-1.0,-1.0,-1.0,1.37502011e-05,3.10223732e-05,1.500291e-05,1.41696701e-05,1.23746313e-05,-1.0,1.08083359e-05,1.28710872e-05,9.9911349e-06,8.15028579e-06,8.45170052e-06,-1.0,-1.0,-1.0,-1.0,8.8157949e-06,-1.0,-1.0,0.010167988,5.88638504e-05,0.029077854,0.00195801468,-1.0,1.79638573e-05,1.13154338e-05,-1.0,1.53079527e-05,1.16943647e-05,7.8643734e-06,8.36287472e-06,8.12938742e-06,8.22761922e-06,8.14132909e-06,8.75823662e-06,9.62451031e-06,1.80465177e-05,0.228943914,0.000249291566,0.0360464156,-1.0,6.0575876e-05,-1.0,-1.0,0.00921394303,5.12204424e-05,1.30760127e-05,1.04646597e-05,-1.0,8.11460359e-06,8.03502735e-06,8.19172965e-06,8.43107955e-06,9.40407972e-06,1.10729661e-05,1.63035784e-05,0.000144352933,6.6641398e-05,8.93634642e-05,0.000283885805,4.9857339e-05,1.52568609e-05,1.76765388e-05,0.0177478585,0.00139267778,1.75393725e-05,9.88513602e-06,6.55878466e-06,-1.0,7.65903133e-06,7.9317806e-06,8.12635517e-06,8.64910453e-06,9.42229053e-06,1.11707923e-05,1.16097608e-05,1.11376103e-05,1.0890808e-05,1.10326837e-05,1.08700724e-05,1.03793518e-05,1.07799342e-05,1.13841716e-05,1.29203436e-05,1.07284304e-05,8.2147908e-06,5.42439648e-06],\"rootInfo\":{\"currentPlayer\":\"B\",\"scoreLead\":-0.481219132,\"scoreSelfplay\":-0.637727802,\"scoreStdev\":9.1332818,\"symHash\":\"05AB2CAD7CBE4C45D3F96ED5294FD1BA\",\"thisHash\":\"C3768C6E6B32760FD991176BCE1F7641\",\"utility\":-0.124391587,\"visits\":1500,\"winrate\":0.434396218},\"rules\":{\"friendlyPassOk\":false,\"hasButton\":false,\"ko\":\"POSITIONAL\",\"komi\":-36.0,\"scoring\":\"TERRITORY\",\"suicide\":true,\"tax\":\"NONE\",\"whiteHandicapBonus\":\"0\"},\"turnNumber\":136,\"whitePlayer\":\"kata1-b40c256-s10312780288-d2513725330\"}";
  //		    contributeGames = new ArrayList<ContributeGameInfo>();
  //		    unParseGameInfos = new ArrayList<ContributeUnParseGameInfo>();
  //		    testGetJsonGameInfo(tryToGetJsonString(jsonTestMove1), contributeGames, unParseGameInfos);
  //		    testGetJsonGameInfo(tryToGetJsonString(jsonTestMove2), contributeGames, unParseGameInfos);
  //		    testGetJsonGameInfo(tryToGetJsonString(jsonTestMove3), contributeGames, unParseGameInfos);
  //		    watchGame(0, true);
  //  }

  private JSONObject tryToGetJsonString(String input) {
    JSONObject json = null;
    try {
      json = new JSONObject(input);
    } catch (JSONException e) {
      e.printStackTrace();
    }

    return json;
  }

  public boolean getJsonGameInfo(
      JSONObject jsonInfo,
      ArrayList<ContributeGameInfo> games,
      ArrayList<ContributeUnParseGameInfo> unParseInfos) {
    if (jsonInfo == null) return false;
    try {
      String gameId = jsonInfo.getString("gameId");
      ContributeGameInfo currentGame = null;
      boolean isExistGame = false;
      if (!games.isEmpty()) {
        for (ContributeGameInfo game : games) {
          if (game.gameId.equals(gameId)) {
            currentGame = game;
            isExistGame = true;
            break;
          }
        }
      }
      if (isExistGame) {
        if (tryToParseJsonGame(currentGame, false, jsonInfo, unParseInfos))
          tryToUseUnParseGameInfos(currentGame, unParseInfos);
      } else {
        currentGame = new ContributeGameInfo();
        if (tryToParseJsonGame(currentGame, true, jsonInfo, unParseInfos))
          tryToUseUnParseGameInfos(currentGame, unParseInfos);
        games.add(currentGame);
        if (watchingGameIndex == -1) watchGame(0, Lizzie.config.contributeWatchAlwaysLastMove);
      }
    } catch (Exception e) {
      e.printStackTrace();
      return false;
    }
    return true;
  }

  private void tryToUseUnParseGameInfos(
      ContributeGameInfo currentGame, ArrayList<ContributeUnParseGameInfo> unParseInfos) {
    ContributeUnParseGameInfo unUsedParseInfo = null;
    if (!unParseInfos.isEmpty()) {
      for (ContributeUnParseGameInfo unParseInfo : unParseInfos) {
        if (unParseInfo.gameId.equals(currentGame.gameId)) {
          unUsedParseInfo = unParseInfo;
          break;
        }
      }
    }
    if (unUsedParseInfo == null) return;
    if (tryToParseJsonGame(currentGame, false, unUsedParseInfo.gameInfo, unParseInfos)) {
      unParseInfos.remove(unUsedParseInfo);
      tryToUseUnParseGameInfos(currentGame, unParseInfos);
    }
  }

  private boolean tryToParseJsonGame(
      ContributeGameInfo currentGame,
      boolean newGame,
      JSONObject jsonInfo,
      ArrayList<ContributeUnParseGameInfo> unParseInfos) {
    boolean success = false;
    // 从jsonInfo中解析已有的一局,更新currentGame,如果isWatching,更新到界面上
    if (newGame) {
      currentGame.gameId = jsonInfo.getString("gameId");
      currentGame.blackPlayer = jsonInfo.getString("blackPlayer");
      currentGame.whitePlayer = jsonInfo.getString("whitePlayer");
      currentGame.sizeX = jsonInfo.getInt("boardXSize");
      currentGame.sizeY = jsonInfo.getInt("boardYSize");
      currentGame.rules = jsonInfo.getJSONObject("rules");
      currentGame.komi = currentGame.rules.getDouble("komi");
    }
    JSONArray initStones = jsonInfo.getJSONArray("initialStones");
    if (initStones.length() > 0) {
      ArrayList<ContributeMoveInfo> initStoneList = new ArrayList<ContributeMoveInfo>();
      for (int i = 0; i < initStones.length(); i++) {
        ContributeMoveInfo move = new ContributeMoveInfo();
        List<Object> moveInfo = initStones.getJSONArray(i).toList();
        move.isBlack = moveInfo.get(0).toString().equals("B");
        move.pos = Board.convertNameToCoordinates(moveInfo.get(1).toString(), currentGame.sizeY);
        move.isPass = move.pos[0] < 0;
        initStoneList.add(move);
      }
      currentGame.initMoveList = initStoneList;
    }
    JSONArray historyMoves = jsonInfo.getJSONArray("moves");
    if (historyMoves.length() > 0) {
      ArrayList<ContributeMoveInfo> historyMoveList = new ArrayList<ContributeMoveInfo>();
      for (int i = 0; i < historyMoves.length(); i++) {
        ContributeMoveInfo move = new ContributeMoveInfo();
        List<Object> moveInfo = historyMoves.getJSONArray(i).toList();
        move.isBlack = moveInfo.get(0).toString().equals("B");
        move.pos = Board.convertNameToCoordinates(moveInfo.get(1).toString(), currentGame.sizeY);
        move.isPass = move.pos[0] < 0;
        historyMoveList.add(move);
      }
      if (newGame) {
        currentGame.moveList = historyMoveList;
      }
      if (newGame || (compareMoveList(historyMoveList, currentGame.moveList))) {
        // 成功,获取最后一手和bestmoves
        ContributeMoveInfo move = new ContributeMoveInfo();
        List<Object> lastMove = jsonInfo.getJSONArray("move").toList();
        move.isBlack = lastMove.get(0).toString().equals("B");
        move.pos = Board.convertNameToCoordinates(lastMove.get(1).toString(), currentGame.sizeY);
        move.isPass = move.pos[0] < 0;
        JSONArray moveInfos = jsonInfo.getJSONArray("moveInfos");
        move.candidates = Utils.getBestMovesFromJsonArray(moveInfos);
        currentGame.moveList.add(move);
      } else {
        ContributeUnParseGameInfo unParseInfo = new ContributeUnParseGameInfo();
        unParseInfo.gameId = jsonInfo.getString("gameId");
        unParseInfo.gameInfo = jsonInfo;
        unParseInfos.add(unParseInfo);
        success = false;
      }
    }
    return success;
  }

  private boolean compareMoveList(
      ArrayList<ContributeMoveInfo> list1, ArrayList<ContributeMoveInfo> list2) {
    if (list1 == null && list2 == null) return true;
    if (list1 == null && list2 != null || list1 != null && list2 == null) return false;
    if (list1.size() != list2.size()) return false;
    for (int i = 0; i < list1.size(); i++) {
      ContributeMoveInfo move1 = list1.get(i);
      ContributeMoveInfo move2 = list2.get(i);
      if (move1.isBlack != move2.isBlack
          || move1.isPass != move2.isPass
          || move1.pos[0] != move2.pos[0]
          || move1.pos[1] != move2.pos[1]) return false;
    }
    return true;
  }

  public void watchGame(int index, boolean loadToLast) {
    int currentMoveNumber = Lizzie.board.getHistory().getCurrentHistoryNode().getData().moveNumber;
    boolean changedGame = false;
    ContributeGameInfo watchGame = contributeGames.get(index);
    ArrayList<ContributeMoveInfo> remainList = new ArrayList<ContributeMoveInfo>();
    if (currentWatchGame == watchGame
        && isContributeGameAndCurrentBoardSame(watchGame, remainList)) {
      if (remainList != null && remainList.size() > 0) setContributeMoveList(remainList);
      else if (currentWatchGame.complete) {
        // 判断是否跳转下一局,watchingGameIndex
        watchGame(index + 1, loadToLast);
        return;
      }
    } else {
      if (Lizzie.config.contributeWatchSkipNone19
          && (watchGame.sizeX != 19 || watchGame.sizeY != 19)) {
        watchGame(index + 1, loadToLast);
        return;
      }
      changedGame = true;
      Lizzie.board.clear(false);
      currentWatchGame = watchGame;
      watchingGameIndex = index;
      if (Lizzie.frame.contributeView != null)
        Lizzie.frame.contributeView.setWathGameIndex(watchingGameIndex);
      Lizzie.board.reopen(currentWatchGame.sizeX, currentWatchGame.sizeY);
      Lizzie.board
          .getHistory()
          .getGameInfo()
          .setPlayerBlack(currentWatchGame.blackPlayer.replaceAll(" ", ""));
      Lizzie.board
          .getHistory()
          .getGameInfo()
          .setPlayerWhite(currentWatchGame.whitePlayer.replaceAll(" ", ""));
      Lizzie.board.getHistory().getGameInfo().setKomi(currentWatchGame.komi);
      if (currentWatchGame.complete) {
        Lizzie.board.getHistory().getGameInfo().setResult(currentWatchGame.gameResult);
        setReultToView(currentWatchGame.gameResult);
      }
      if (currentWatchGame.initMoveList != null && currentWatchGame.initMoveList.size() > 0)
        setContributeMoveList(currentWatchGame.initMoveList);
      if (currentWatchGame.moveList != null && currentWatchGame.moveList.size() > 0)
        setContributeMoveList(currentWatchGame.moveList);
    }
    if (!changedGame && !loadToLast) {
      //      if (Lizzie.board.getHistory().getCurrentHistoryNode().getData().moveNumber -
      // currentMoveNumber
      //          > 1)
      Lizzie.board.goToMoveNumber(currentMoveNumber);
    } else if (loadToLast) {
      while (Lizzie.board.nextMove(false)) ;
    }
    Lizzie.frame.refresh();
    // 切换到不同局,同步ContributeGameInfo到界面上(如是同一局直接返回),或直接跳倒数第二手
    // 还是显示前一手模式+跳到最后一手?
    // watchingGameIndex
  }

  private void setContributeMoveList(ArrayList<ContributeMoveInfo> remainList) {
    while (Lizzie.board.nextMove(false)) ;
    for (ContributeMoveInfo move : remainList) {
      // 把所有move place进去 再设置bestmoves进去
      if (move.candidates != null)
        Lizzie.board
            .getData()
            .tryToSetBestMoves(
                move.candidates,
                Lizzie.board.getHistory().getCurrentTurnPlayerShortName(),
                false,
                MoveData.getPlayouts(move.candidates));
      if (move.isPass) Lizzie.board.getHistory().pass(move.isBlack ? Stone.BLACK : Stone.WHITE);
      else
        Lizzie.board
            .getHistory()
            .place(move.pos[0], move.pos[1], move.isBlack ? Stone.BLACK : Stone.WHITE);
    }
  }

  private boolean isContributeGameAndCurrentBoardSame(
      ContributeGameInfo watchGame, ArrayList<ContributeMoveInfo> remainList) {
    boolean isSame = true;
    BoardHistoryNode startNode = Lizzie.board.getHistory().getStart();
    while (startNode.next().isPresent() && !startNode.getData().lastMove.isPresent())
      startNode = startNode.next().get();
    if (watchGame.initMoveList != null && watchGame.initMoveList.size() > 0) {
      isSame = compareListAndNode(watchGame.initMoveList, startNode, remainList);
    }
    if (watchGame.moveList != null && watchGame.moveList.size() > 0) {
      isSame = compareListAndNode(watchGame.moveList, startNode, remainList);
    }
    return isSame;
  }

  private boolean compareListAndNode(
      ArrayList<ContributeMoveInfo> list,
      BoardHistoryNode node,
      ArrayList<ContributeMoveInfo> remainList) {
    boolean started = false;

    for (int i = 0; i < list.size(); i++) {
      ContributeMoveInfo move = list.get(i);
      if (started || !move.isPass) {
        started = true;
        if (node.getData().lastMove.isPresent()) {
          if (node.getData().lastMove.get()[0] != move.pos[0]
              || node.getData().lastMove.get()[1] != move.pos[1]) return false;
          if (node.next().isPresent()) node = node.next().get();
          else {
            getRemainList(list, remainList, i + 1);
            return true;
          }

        } else {
          if (move.isPass) {
            if (node.next().isPresent()) node = node.next().get();
            else return true;
          } else {
            getRemainList(list, remainList, i + 1);
            return true;
          }
        }
      }
    }
    return true;
  }

  private void getRemainList(
      ArrayList<ContributeMoveInfo> list, ArrayList<ContributeMoveInfo> remainList, int i) {
    for (; i < list.size(); i++) {
      remainList.add(list.get(i));
    }
  }

  public void tryToDignostic(String message) {
    EngineFailedMessage engineFailedMessage =
        new EngineFailedMessage(
            commands, engineCommand, message, !useJavaSSH && OS.isWindows(), false);
    engineFailedMessage.setModal(true);
    engineFailedMessage.setVisible(true);
  }
}
