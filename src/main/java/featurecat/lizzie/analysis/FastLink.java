package featurecat.lizzie.analysis;

import featurecat.lizzie.gui.Message;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class FastLink {

  public void startProgram(String command) {
    List<String> commands = splitCommand(command);
    ProcessBuilder processBuilder = new ProcessBuilder(commands);
    // Commented for remote ssh
    // processBuilder.directory(startfolder);
    // processBuilder.redirectErrorStream(true);
    try {
      Process process = processBuilder.start();
    } catch (Exception e) {
      Message msg = new Message();
      msg.setMessage("运行失败! ");
      // msg.setVisible(true);
    }
  }

  private static enum Params {
    NORMAL,
    QUOTE,
    DOUBLE_QUOTE
  }

  public List<String> splitCommand(String commandLine) {
    if (commandLine == null || commandLine.length() == 0) {
      return new ArrayList<String>();
    }

    final ArrayList<String> commandList = new ArrayList<String>();
    final StringBuilder param = new StringBuilder();
    final StringTokenizer tokens = new StringTokenizer(commandLine, " '\"", true);
    boolean lastTokenQuoted = false;
    Params state = Params.NORMAL;

    while (tokens.hasMoreTokens()) {
      String nextToken = tokens.nextToken();
      switch (state) {
        case QUOTE:
          if ("'".equals(nextToken)) {
            state = Params.NORMAL;
            lastTokenQuoted = true;
          } else {
            param.append(nextToken);
          }
          break;
        case DOUBLE_QUOTE:
          if ("\"".equals(nextToken)) {
            state = Params.NORMAL;
            lastTokenQuoted = true;
          } else {
            param.append(nextToken);
          }
          break;
        default:
          if ("'".equals(nextToken)) {
            state = Params.QUOTE;
          } else if ("\"".equals(nextToken)) {
            state = Params.DOUBLE_QUOTE;
          } else if (" ".equals(nextToken)) {
            if (lastTokenQuoted || param.length() != 0) {
              commandList.add(param.toString());
              param.delete(0, param.length());
            }
          } else {
            param.append(nextToken);
          }
          lastTokenQuoted = false;
          break;
      }
    }
    if (lastTokenQuoted || param.length() != 0) {
      commandList.add(param.toString());
    }
    return commandList;
  }
}
