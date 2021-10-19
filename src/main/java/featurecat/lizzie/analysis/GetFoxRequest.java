package featurecat.lizzie.analysis;

import featurecat.lizzie.gui.FoxKifuDownload;
import featurecat.lizzie.util.Utils;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import org.jdesktop.swingx.util.OS;

public class GetFoxRequest {
  private Process process;
  private InputStreamReader inputStream;
  private OutputStreamWriter outputStream;
  private ScheduledExecutorService executor;
  private FoxKifuDownload foxKifuDownload;

  public GetFoxRequest(FoxKifuDownload foxKifuDownload) {
    this.foxKifuDownload = foxKifuDownload;
    File foxFile = new File("foxReq" + File.separator + "foxRequestQ.jar");
    if (!foxFile.exists()) {
      Utils.copyFoxReq();
    }
    String jarString = " -jar -Dfile.encoding=utf-8 foxReq" + File.separator + "foxRequestQ.jar";
    try {
      if (OS.isWindows()) {
        boolean success = false;
        String java64Path = "jre\\java11\\bin\\java.exe";
        File java64 = new File(java64Path);
        if (java64.exists()) {
          try {
            process = Runtime.getRuntime().exec(java64Path + jarString);
            success = true;
          } catch (Exception e) {
            success = false;
            e.printStackTrace();
          }
        }
        if (!success) {
          String java32Path = "jre\\java8_32\\bin\\java.exe";
          File java32 = new File(java32Path);
          if (java32.exists()) {
            try {
              process = Runtime.getRuntime().exec(java32 + jarString);
              success = true;
            } catch (Exception e) {
              success = false;
              e.printStackTrace();
            }
          }
        }
        if (!success) {
          process = Runtime.getRuntime().exec("java" + jarString);
        }
      } else {
        process = Runtime.getRuntime().exec("java" + jarString);
      }
      inputStream = new InputStreamReader(process.getInputStream(), "UTF-8");
      outputStream = new OutputStreamWriter(process.getOutputStream(), "UTF-8");
      executor = Executors.newSingleThreadScheduledExecutor();
      executor.execute(this::read);
    } catch (Exception e) {
      Utils.showMsg(e.getLocalizedMessage());
    }
  }

  private void read() {
    try {
      int c;
      StringBuilder line = new StringBuilder();
      while ((c = inputStream.read()) != -1) {
        line.append((char) c);
        if ((c == '\n')) {
          try {
            parseLine(line.toString());
          } catch (Exception ex) {
            ex.printStackTrace();
          }
          line = new StringBuilder();
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private void parseLine(String string) {
    //    System.out.println(string);
    //    Lizzie.gtpConsole.addLine(string);
    if (!string.equals("\n") && !string.equals("\r\n")) foxKifuDownload.receiveResult(string);
  }

  public void sendCommand(String command) {
    try {
      outputStream.write((command + "\n"));
      outputStream.flush();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
