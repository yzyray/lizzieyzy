package featurecat.lizzie.gui;

import featurecat.lizzie.Lizzie;
import featurecat.lizzie.util.Utils;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

public class SocketEditFile {
  SMessage msg;
  ShareMessage smsg;
  String successInfo = "";
  boolean recievedServer = false;
  Socket socket = null;

  public String SocketEditFile(
      boolean isDelete,
      String id,
      String black,
      String white,
      String label,
      String otherInfo,
      String fileName,
      String upLoader,
      boolean isPublic) {
    // TODO Auto-generated method stub

    BufferedReader br = null;
    PrintWriter pw = null;
    try {
      // 客户端socket指定服务器的地址和端口号121.36.229.204
      socket = new Socket("lizzieyzy.cn", 3075);
      System.out.println("Socket=" + socket);
      // 同服务器原理一样
      br = new BufferedReader(new InputStreamReader(socket.getInputStream(), "utf-8"));
      pw =
          new PrintWriter(
              new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), "utf-8")));
      sendContent("SktINFOStart", pw);
      String info = "";
      if (!isDelete)
        info =
            id
                + ">"
                + black
                + ">"
                + white
                + ">"
                + label
                + ">"
                + otherInfo
                + ">"
                + (isPublic ? "true" : "false");
      else info = id + ">" + upLoader + ">" + fileName;
      sendContent(info, pw);
      // sendContent("SktINFOEnd",pw);
      pw.println("SktINFOEnd");
      pw.flush();
      pw.println("SktEND");
      pw.flush();
      String str;
      boolean success = false;
      boolean errMsg = false;
      String err = "";

      Runnable runnable =
          new Runnable() {
            public void run() {
              try {
                Thread.sleep(5000);
                if (socket != null && !recievedServer)
                  try {
                    socket.close();
                    Utils.showMsg(Lizzie.resourceBundle.getString("Socket.connectFailed"));
                    //                    msg = new SMessage();
                    //                    msg.setMessage(
                    //
                    // "连接超时...请重试或下载最新版Lizzie,链接:https://pan.baidu.com/s/1q615GHD62F92mNZbTYfcxA");
                    //                    // msg.setVisible(true);
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
      while ((str = br.readLine()) != null) {
        recievedServer = true;
        System.out.println(str);
        Lizzie.gtpConsole.addLine(str);
        if (str.startsWith("success")) {
          successInfo = str;
          success = true;
        }
        if (str.startsWith("error")) {
          errMsg = true;
          err = str;
        }
      }
      if (success) {
        return successInfo;
      } else if (errMsg) {
        return err;
      }
    } catch (Exception e) {
      // e.printStackTrace();
    } finally {
      if (socket != null)
        try {
          System.out.println("close......");
          br.close();
          pw.close();
          socket.close();
        } catch (IOException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
    }
    return "error";
  }

  public void sendContent(String str, PrintWriter pw) {
    pw.println(Utils.doEncrypt(str));
    pw.flush();
  }
}
