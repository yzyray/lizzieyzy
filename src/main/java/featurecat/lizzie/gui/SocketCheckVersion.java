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
import java.text.SimpleDateFormat;
import java.util.Date;

public class SocketCheckVersion {
  SMessage msg;
  ShareMessage smsg;
  String link = "";
  boolean recievedServer = false;
  Socket socket = null;

  public void SocketCheckVersion(boolean isAutoCheck) {
    // TODO Auto-generated method stub
    String date = new SimpleDateFormat("yyyyMMdd").format(new Date());
    Lizzie.config.uiConfig.put("auto-check-date", date);
    BufferedReader br = null;
    PrintWriter pw = null;
    try {
      // 客户端socket指定服务器的地址和端口号121.36.229.204
      socket = new Socket("lizzieyzy.cn", 3045);
      // System.out.println("Socket=" + socket);
      // 同服务器原理一样
      br = new BufferedReader(new InputStreamReader(socket.getInputStream(), "utf-8"));
      pw =
          new PrintWriter(
              new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), "utf-8")));

      sendContent("SktCheckVersion", pw);
      // pw.flush();
      pw.println(Lizzie.checkVersion);
      pw.flush();
      pw.println("SktEND");
      pw.flush();
      //  sendContent("SktEND",pw);

      String str;
      boolean success = false;
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
                    //                    msg = new SMessage();
                    //                    msg.setMessage(
                    //
                    // "检查更新失败...请重试或下载最新版Lizzie,链接:https://pan.baidu.com/s/1q615GHD62F92mNZbTYfcxA");
                    // msg.setVisible(true);
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
      while ((str = br.readLine()) != null) {
        recievedServer = true;
        // System.out.println(str);
        //   Lizzie.gtpConsole.addLine(str);
        if (str.startsWith("http")) {
          link = str;
          success = true;
        }
        if (str.startsWith("error")) {
          errMsg = true;
          err = str;
        }
        if (str.startsWith("version")) {
          String[] params = str.split(">");
          if (params.length == 3) {
            String remoteVersion = params[1];
            String remoteVersionDis = params[2];
            if (Integer.parseInt(remoteVersion) <= Integer.parseInt(Lizzie.checkVersion)
                || Integer.parseInt(remoteVersion) <= Lizzie.config.ignoreVersion) {
              if (!isAutoCheck) {
                CheckVersion checkVersion =
                    new CheckVersion(false, remoteVersion, remoteVersionDis);
                checkVersion.setVisible(true);
              }
            } else {
              CheckVersion checkVersion = new CheckVersion(true, remoteVersion, remoteVersionDis);
              checkVersion.setVisible(true);
            }
          } else {
            if (!isAutoCheck) {
              //              msg = new SMessage();
              //              msg.setMessage(
              //
              // "检查更新失败,请重试或下载最新版Lizzie,链接:https://pan.baidu.com/s/1q615GHD62F92mNZbTYfcxA");
              Utils.showMsg(Lizzie.resourceBundle.getString("Socket.connectFailed"));
            }
            // msg.setVisible(true);
          }
        }
      }
      if (success) {
        smsg = new ShareMessage();
        smsg.setMessage(link);
        smsg.setVisible(true);
      } else if (errMsg) {
        if (!isAutoCheck) {
          msg = new SMessage();
          msg.setMessage(err);
        }
        // msg.setVisible(true);
      }
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      if (socket != null)
        try {
          System.out.println("checkversion complete....");
          // Lizzie.gtpConsole.addLine("checkversion complete...." + "\n");
          br.close();
          pw.close();
          socket.close();
        } catch (IOException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
      else {
        // System.out.println("连接失败...");
        //  Lizzie.gtpConsole.addLine("检查更新失败..." + "\n");
        if (!isAutoCheck) {
          //          msg = new SMessage();
          //          msg.setMessage(
          //
          // "检查更新失败...请重试或下载最新版Lizzie,链接:https://pan.baidu.com/s/1q615GHD62F92mNZbTYfcxA");
          Utils.showMsg(Lizzie.resourceBundle.getString("Socket.connectFailed"));
        }
        //  msg.setVisible(true);
      }
    }
  }

  public void sendContent(String str, PrintWriter pw) {
    pw.println(Utils.doEncrypt(str));
    pw.flush();
  }
}
