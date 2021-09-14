package featurecat.lizzie.analysis;

import featurecat.lizzie.gui.Message;
import featurecat.lizzie.util.Utils;
import java.util.List;

public class FastLink {

  public void startProgram(String command) {
    List<String> commands = Utils.splitCommand(command);
    ProcessBuilder processBuilder = new ProcessBuilder(commands);
    try {
      Process process = processBuilder.start();
    } catch (Exception e) {
      Message msg = new Message();
      msg.setMessage("运行失败! ");
      // msg.setVisible(true);
    }
  }
}
