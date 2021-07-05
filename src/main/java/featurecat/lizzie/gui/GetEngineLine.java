package featurecat.lizzie.gui;

import featurecat.lizzie.Lizzie;
import featurecat.lizzie.util.Utils;
import java.awt.Component;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

public class GetEngineLine {
  public String enginePath = "";
  public String weightPath = "";
  public String configPath = "";
  public String commandHelp = "";
  private BufferedInputStream inputStream;
  private Path curPath;
  private final ResourceBundle resourceBundle = Lizzie.resourceBundle;
  private EngineParameter ep;

  public GetEngineLine() {
    curPath = (new File("")).getAbsoluteFile().toPath();
  }

  public String getEngineLine(
      Component parentComponent, boolean isKataGoOnly, boolean isAnalysisEngine) {
    boolean isKataGo = false;
    boolean isLeela = false;
    boolean isIkatago = false;
    if (isKataGoOnly) {
      isKataGo = true;
    } else {
      Object[] options = {
        "KataGo",
        "LeelaZero",
        resourceBundle.getString("MoreEngines.ikatago"),
        resourceBundle.getString("MoreEngines.other")
      };

      int response =
          JOptionPane.showOptionDialog(
              parentComponent,
              resourceBundle.getString("MoreEngines.chooseTypeTitle"),
              resourceBundle.getString("MoreEngines.chooseType"),
              JOptionPane.YES_OPTION,
              JOptionPane.QUESTION_MESSAGE,
              null,
              options,
              options[0]);
      if (response == -1) {
        return "";
      } else if (response == 0) {
        isKataGo = true;
      } else if (response == 1) {
        isLeela = true;
      } else if (response == 2) {
        isIkatago = true;
      }
    }
    //	  else if(response==2)
    //	  {
    //	  }
    String engineLine = "";
    File engineFile = null;
    File weightFile = null;
    File configFile = null;
    JFileChooser chooser = new JFileChooser(".");
    if (Utils.isWindows()) {
      FileNameExtensionFilter filter =
          new FileNameExtensionFilter(
              isIkatago ? "ikatago" : resourceBundle.getString("LizzieConfig.title.engine"),
              "exe",
              "bat");
      chooser.setFileFilter(filter);
    } else {
      // setVisible(false);
    }
    chooser.setMultiSelectionEnabled(false);
    chooser.setDialogTitle(
        isIkatago
            ? resourceBundle.getString("LizzieConfig.prompt.selectikatago")
            : resourceBundle.getString("LizzieConfig.prompt.selectEngine"));
    int result = chooser.showOpenDialog(parentComponent);
    if (result == JFileChooser.APPROVE_OPTION) {
      engineFile = chooser.getSelectedFile();
      if (engineFile != null) {
        enginePath = relativizePath(engineFile.toPath());
        if (isIkatago) {
          boolean isColab = false;
          Object[] options = {"Colab", resourceBundle.getString("MoreEngines.otherPlatform")};

          int response =
              JOptionPane.showOptionDialog(
                  parentComponent,
                  resourceBundle.getString("MoreEngines.choosePlatformTitle"),
                  resourceBundle.getString("MoreEngines.choosePlatform"),
                  JOptionPane.YES_OPTION,
                  JOptionPane.QUESTION_MESSAGE,
                  null,
                  options,
                  options[0]);
          if (response == -1) {
            return "";
          } else if (response == 0) {
            isColab = true;
          }
          String userName =
              JOptionPane.showInputDialog(
                  parentComponent,
                  resourceBundle.getString("MoreEngines.ikatagoUserName"),
                  resourceBundle.getString("MoreEngines.ikatagoUserNameTitle"),
                  JOptionPane.INFORMATION_MESSAGE);
          String password =
              JOptionPane.showInputDialog(
                  parentComponent,
                  resourceBundle.getString("MoreEngines.ikatagoPassWord"),
                  resourceBundle.getString("MoreEngines.ikatagoPassWordTitle"),
                  JOptionPane.INFORMATION_MESSAGE);
          return enginePath
              + " --platform "
              + (isColab ? "colab" : "all")
              + " --username "
              + userName
              + " --password "
              + password;
        }
        getCommandHelp();
        if (!isKataGo && !isLeela && !isIkatago) {
          ep = new EngineParameter(enginePath, weightPath, commandHelp, false, "", false, true);
          ep.setVisible(true);
          if (!ep.commandLine.isEmpty()) {
            engineLine = ep.commandLine;
          }
          return engineLine;
        }
        JFileChooser chooserw = new JFileChooser(".");
        FileFilterTest1 fileFilter = new FileFilterTest1();
        chooserw.setFileFilter(fileFilter);
        chooserw.setMultiSelectionEnabled(false);
        chooserw.setDialogTitle(resourceBundle.getString("LizzieConfig.prompt.selectWeight"));
        result = chooserw.showOpenDialog(parentComponent);
        if (result == JFileChooser.APPROVE_OPTION) {
          weightFile = chooserw.getSelectedFile();
          if (weightFile != null) {
            weightPath = relativizePath(weightFile.toPath());
            if (isKataGo) {
              JFileChooser chooseConfig = new JFileChooser(".");
              FileFilterTest2 fileFilter2 = new FileFilterTest2();
              chooseConfig.setFileFilter(fileFilter2);
              chooseConfig.setMultiSelectionEnabled(false);
              chooseConfig.setDialogTitle(resourceBundle.getString("MoreEngines.chooseConfig"));
              result = chooseConfig.showOpenDialog(parentComponent);
              if (result == JFileChooser.APPROVE_OPTION) {
                configFile = chooseConfig.getSelectedFile();
                configPath = relativizePath(configFile.toPath());
              }
              ep =
                  new EngineParameter(
                      enginePath,
                      weightPath,
                      commandHelp,
                      true,
                      configPath,
                      isAnalysisEngine,
                      false);
              ep.setVisible(true);
              if (!ep.commandLine.isEmpty()) {
                engineLine = ep.commandLine;
              }
            } else {
              ep =
                  new EngineParameter(enginePath, weightPath, commandHelp, false, "", false, false);
              ep.setVisible(true);
              if (!ep.commandLine.isEmpty()) {
                engineLine = ep.commandLine;
              }
            }
          }
        }
      }
    }
    return engineLine;
  }

  private String relativizePath(Path path) {
    Path relatPath;
    if (path.startsWith(curPath)) {
      relatPath = curPath.relativize(path);
    } else {
      relatPath = path;
    }
    return "\"" + relatPath.toString() + "\"";
  }

  private void getCommandHelp() {

    List<String> commands = new ArrayList<String>();
    commands.add(enginePath);
    commands.add("-h");

    ProcessBuilder processBuilder = new ProcessBuilder(commands);
    processBuilder.directory();
    processBuilder.redirectErrorStream(true);
    try {
      Process process = processBuilder.start();
      inputStream = new BufferedInputStream(process.getInputStream());
      ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
      executor.execute(this::read);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private void read() {
    try {
      int c;
      StringBuilder line = new StringBuilder();
      while ((c = inputStream.read()) != -1) {
        line.append((char) c);
      }
      commandHelp = line.toString();
      if (ep != null) ep.txtParams.setText(commandHelp);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
