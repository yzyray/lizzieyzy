package featurecat.lizzie.gui;

import featurecat.lizzie.Lizzie;
import featurecat.lizzie.rules.SGFParser;
import featurecat.lizzie.util.Utils;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

public class SocketGetFile {
  SMessage msg;
  ShareMessage smsg;
  String link = "";
  boolean recievedServer = false;
  Socket socket = null;

  public void SocketGetFile(String name, String date, String file) {
    // TODO Auto-generated method stub

    BufferedReader br = null;
    PrintWriter pw = null;
    try {
      // 客户端socket指定服务器的地址和端口号121.36.229.204
      socket = new Socket("lizzieyzy.cn", 3105);
      // System.out.println("Socket=" + socket);
      // 同服务器原理一样
      br = new BufferedReader(new InputStreamReader(socket.getInputStream(), "utf-8"));
      pw =
          new PrintWriter(
              new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), "utf-8")));
      sendContent("SktINFOStart", pw);
      pw.println(name + ">" + date + ">" + file);
      pw.flush();
      pw.println("SktEND");
      pw.flush();
      //  sendContent("SktEND",pw);

      String str;
      boolean errMsg = false;
      String err = "";

      Runnable runnable =
          new Runnable() {
            public void run() {
              try {
                Thread.sleep(15000);
                if (socket != null && !recievedServer)
                  try {
                    socket.close();
                    // msg = new SMessage();
                    //    msg.setMessage(
                    //
                    // "连接失败...请重试或下载最新版Lizzie,链接:https://pan.baidu.com/s/1q615GHD62F92mNZbTYfcxA");
                    //     msg.setVisible(true);
                    Utils.showMsg(Lizzie.resourceBundle.getString("Socket.connectFailed"));
                  } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                  }
              } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
              }
            }
          };
      Thread th = new Thread(runnable);
      th.start();
      String sgfString = "";
      while ((str = br.readLine()) != null) {
        recievedServer = true;
        sgfString += str + "\n";
        if (str.startsWith("getFileEnd")) {
          SGFParser.loadFromString(sgfString);
          Lizzie.board.setMovelistAll();
          Lizzie.frame.setVisible(true);
          Lizzie.frame.refresh();
        }
        if (str.startsWith("errorFileInfo")) {
          errMsg = true;
          err = str.substring(13);
        }
      }
      if (errMsg) {
        Utils.showMsg(err);
      }

    } catch (Exception e) {
      // e.printStackTrace();
    } finally {
      if (socket != null)
        try {
          // System.out.println("close......");
          br.close();
          pw.close();
          socket.close();
        } catch (IOException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
      else {
        // System.out.println("连接失败...");
        //  Lizzie.gtpConsole.addLine("连接失败..." + "\n");
        Utils.showMsg(Lizzie.resourceBundle.getString("Socket.connectFailed"));
        // msg = new SMessage();
        // msg.setMessage("连接失败...请重试或下载最新版Lizzie,链接:https://pan.baidu.com/s/1q615GHD62F92mNZbTYfcxA");

        //  msg.setVisible(true);
      }
    }
  }

  public void sendContent(String str, PrintWriter pw) {
    pw.println(Utils.doEncrypt(str));
    pw.flush();
  }
}
