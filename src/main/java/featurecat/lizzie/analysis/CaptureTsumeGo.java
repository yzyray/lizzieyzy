package featurecat.lizzie.analysis;

import featurecat.lizzie.Lizzie;
import featurecat.lizzie.rules.Board;
import featurecat.lizzie.rules.Stone;
import featurecat.lizzie.rules.Zobrist;
import featurecat.lizzie.rules.extraMoveForTsumego;
import featurecat.lizzie.util.Utils;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import javax.swing.JFrame;
import org.jdesktop.swingx.util.OS;

public class CaptureTsumeGo {
  private Process process;
  private BufferedReader inputStream;
  // private BufferedOutputStream outputStream;
  private BufferedReader errorStream;
  private ScheduledExecutorService executor;
  private ScheduledExecutorService executorErr;

  public CaptureTsumeGo() {
    if (start()) {
      initializeStreams();
      executor = Executors.newSingleThreadScheduledExecutor();
      executor.execute(this::read);
      executorErr = Executors.newSingleThreadScheduledExecutor();
      executorErr.execute(this::readError);
    }
  }

  private boolean start() {
    String jarName = "CaptureTsumeGo.jar";
    String params =
        " "
            + Lizzie.config.captureBlackOffset
            + " "
            + Lizzie.config.captureBlackPercent
            + " "
            + Lizzie.config.captureWhiteOffset
            + " "
            + Lizzie.config.captureWhitePercent
            + " "
            + Lizzie.config.captureGrayOffset;
    File jarFile = new File("captureTsumeGo" + File.separator + jarName);
    if (!jarFile.exists()) Utils.copyCaptureTsumeGo();
    boolean success = false;
    try {
      if (OS.isWindows()) {
        String java64Path = "jre\\java11\\bin\\java.exe";
        File java64 = new File(java64Path);

        if (java64.exists()) {
          try {
            process =
                Runtime.getRuntime()
                    .exec(java64Path + " -jar captureTsumeGo" + File.separator + jarName + params);
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
              process =
                  Runtime.getRuntime()
                      .exec(java32 + " -jar captureTsumeGo" + File.separator + jarName + params);
              success = true;
            } catch (Exception e) {
              success = false;
              e.printStackTrace();
            }
          }
        }
        if (!success) {
          process =
              Runtime.getRuntime()
                  .exec("java -jar captureTsumeGo" + File.separator + jarName + params);
          success = true;
        }
      } else {
        process =
            Runtime.getRuntime()
                .exec("java -jar captureTsumeGo" + File.separator + jarName + params);
        success = true;
      }
    } catch (Exception e) {
      success = false;
      Utils.showMsg(e.getLocalizedMessage());
    }
    return success;
  }

  private void initializeStreams() {
    inputStream = new BufferedReader(new InputStreamReader(process.getInputStream()));
    // outputStream = new BufferedOutputStream(process.getOutputStream());
    errorStream = new BufferedReader(new InputStreamReader(process.getErrorStream()));
  }

  private void read() {
    try {
      String line = "";
      // while ((c = inputStream.read()) != -1) {
      while ((line = inputStream.readLine()) != null) {
        try {
          parseLine(line.toString());
        } catch (Exception ex) {
          ex.printStackTrace();
        }
      }
      // this line will be reached when engine shuts down
      System.out.println("Capture process ended.");
      // Do no exit for switching weights
      // System.exit(-1);
    } catch (IOException e) {
    }

    process = null;
    shutdown();
    return;
  }

  private void readError() {
    String line = "";
    try {
      while ((line = errorStream.readLine()) != null) {
        try {
          Lizzie.gtpConsole.addErrorLine(line + "\n");
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  private int direction = -1;
  private int bw = -1;
  private int bh = -1;
  private ArrayList<int[]> stoneData;

  private void parseLine(String line) {
    // Lizzie.gtpConsole.addLine(line + "\n");
    if (line.equals("esc")) {
      Lizzie.frame.setExtendedState(JFrame.NORMAL);
      Lizzie.frame.openCaptureTsumego();
    }
    if (line.startsWith("dx")) {
      String[] params = line.split(" ");
      if (params.length == 6) {
        direction = Integer.parseInt(params[1]);
        bw = Integer.parseInt(params[3]);
        bh = Integer.parseInt(params[5]);
        stoneData = new ArrayList<int[]>();
      }
    }
    if (line.startsWith("#")) {
      String[] params = line.substring(2).split(" ");
      int[] data = new int[params.length];
      for (int i = 0; i < params.length; i++) {
        data[i] = Integer.parseInt(params[i]);
      }
      stoneData.add(data);
    }
    if (line.equals("end")) {
      processStoneData(stoneData, direction, bw, bh);
      Lizzie.frame.setExtendedState(JFrame.NORMAL);
      Lizzie.frame.openTsumego();
    }
  }

  private void processStoneData(ArrayList<int[]> stoneData, int direction, int bw, int bh) {
    if (Board.boardWidth < bw || Board.boardHeight < bh) Lizzie.board.reopen(bw, bw);
    else Lizzie.board.clear(false);
    Stone[] curStones = Lizzie.board.getStones();
    Stone[] stones = new Stone[curStones.length];
    for (int i = 0; i < curStones.length; i++) {
      stones[i] = curStones[i];
    }
    Zobrist zobrist = Lizzie.board.getHistory().getZobrist();
    List<extraMoveForTsumego> extraStones = new ArrayList<extraMoveForTsumego>();
    for (int y = 0; y < stoneData.size(); y++) {
      int[] value = stoneData.get(y);
      for (int x = 0; x < value.length; x++) {
        int data = value[x];
        Stone stone = Stone.EMPTY;
        if (data == 1) stone = Stone.BLACK;
        else if (data == 2) stone = Stone.WHITE;
        if (stone != Stone.EMPTY) {
          switch (direction) {
            case 1: // 左上角
              Utils.addStone(stones, zobrist, x, y, stone, extraStones);
              break;
            case 2: // 右上角
              Utils.addStone(stones, zobrist, Board.boardWidth - (bw - x), y, stone, extraStones);
              break;
            case 3: // 左下角
              Utils.addStone(stones, zobrist, x, Board.boardHeight - (bh - y), stone, extraStones);
              break;
            case 4: // 右下角
              Utils.addStone(
                  stones,
                  zobrist,
                  Board.boardWidth - (bw - x),
                  Board.boardHeight - (bh - y),
                  stone,
                  extraStones);
              break;
          }
        }
      }
    }
    Lizzie.board.flattenWithCondition(stones, zobrist, true, extraStones);
    Lizzie.frame.refresh();
  }

  public void shutdown() {
    process.destroy();
  }
}
