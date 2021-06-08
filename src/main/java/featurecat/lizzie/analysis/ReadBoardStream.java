package featurecat.lizzie.analysis;

import featurecat.lizzie.Lizzie;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import org.json.JSONException;

public class ReadBoardStream extends Thread {

  private Socket socket = null;
  private BufferedReader br = null;
  private PrintWriter pw = null;
  private ArrayList<Integer> tempcount = new ArrayList<Integer>();

  public ReadBoardStream(Socket s) {
    socket = s;
    try {
      br = new BufferedReader(new InputStreamReader(socket.getInputStream(), "utf-8"));
      pw =
          new PrintWriter(
              new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), "utf-8")), true);
      start();
    } catch (Exception e) {

      e.printStackTrace();
    }
  }

  @Override
  public void run() {
    String line;
    try {
      while ((line = br.readLine()) != null) {
        //  System.out.println(line);
        Lizzie.frame.readBoard.parseLine(line, true);
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
    pw.println(command);
    pw.flush();
  }
}
