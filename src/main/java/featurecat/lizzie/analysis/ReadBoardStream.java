package featurecat.lizzie.analysis;

import featurecat.lizzie.Lizzie;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import org.json.JSONException;

public class ReadBoardStream extends Thread {

  private Socket socket = null;
  private BufferedReader in;
  private BufferedOutputStream out;

  public ReadBoardStream(Socket s) {
    socket = s;
    try {
      in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
      out = new BufferedOutputStream(socket.getOutputStream());
      start();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Override
  public void run() {
    String line;
    try {
      while ((line = in.readLine()) != null) {
        //  System.out.println(line);
        Lizzie.frame.readBoard.parseLine(line);
        if (line.equals("ready"))
          if (!Lizzie.frame.readBoard.isLoaded) {
            Lizzie.frame.readBoard.isLoaded = true;
            checkVersion();
          }
      }
    } catch (NumberFormatException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (JSONException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      // e.printStackTrace();
    }
  }

  public void sendCommand(String command) {
    try {
      out.write((command + "\n").getBytes());
      out.flush();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      //  e.printStackTrace();
    }
  }

  public void checkVersion() {
    sendCommand("version");
  }
}
