package featurecat.lizzie.analysis;

import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.Session;
import featurecat.lizzie.Lizzie;
import featurecat.lizzie.gui.LizzieFrame;
import featurecat.lizzie.util.Utils;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.json.JSONException;

public class SSHController {
  private Connection conn;
  private Session session;
  private RemoteConnect newConnect;
  private Leelaz owner;

  public SSHController(Leelaz owner, String ip, String port) {
    this.owner = owner;
    this.newConnect = new RemoteConnect();
    this.newConnect.setIp(ip);
    this.newConnect.setPort(port);
  }

  public Boolean login(String command, String userName, String password) {
    boolean flag = false;
    try {
      this.conn = new Connection(this.newConnect.getIp(), this.newConnect.getPort());
      this.conn.connect(null, 3000, 3000);
      flag = this.conn.authenticateWithPassword(userName, Utils.doDecrypt(password));
      if (flag) {
        this.session = this.conn.openSession();
        this.session.execCommand(command);
      } else {
        owner.isLoaded = false;
        Utils.showMsg(Lizzie.resourceBundle.getString("SSHController.connectFailed"));
        LizzieFrame.openMoreEngineDialog();
        this.conn.close();
      }
    } catch (IOException e) {
      owner.isLoaded = false;
      e.printStackTrace();
      String err = e.getLocalizedMessage();
      try {
        this.owner.tryToDignostic(
            String.valueOf(Lizzie.resourceBundle.getString("SSHController.engineFailed"))
                + ": "
                + ((err == null)
                    ? Lizzie.resourceBundle.getString("Leelaz.engineStartNoExceptionMessage")
                    : err),
            true);
        LizzieFrame.openMoreEngineDialog();
      } catch (JSONException e1) {
        e1.printStackTrace();
      }
    }
    return Boolean.valueOf(flag);
  }

  public Boolean loginByFileKey(String command, String userName, File keyFile) {
    boolean flag = false;
    try {
      this.conn = new Connection(this.newConnect.getIp());
      this.conn.connect(null, 3000, 3000);
      flag = this.conn.authenticateWithPublicKey(userName, keyFile, null);
      if (flag) {
        this.session = this.conn.openSession();
        this.session.execCommand(command);
      } else {
        owner.isLoaded = false;
        Utils.showMsg(Lizzie.resourceBundle.getString("SSHController.connectFailed"));
        LizzieFrame.openMoreEngineDialog();
        this.conn.close();
      }
    } catch (Exception e) {
      owner.isLoaded = false;
      e.printStackTrace();
      String err = e.getLocalizedMessage();
      try {
        this.owner.tryToDignostic(
            String.valueOf(Lizzie.resourceBundle.getString("SSHController.engineFailed"))
                + ": "
                + ((err == null)
                    ? Lizzie.resourceBundle.getString("Leelaz.engineStartNoExceptionMessage")
                    : err),
            true);
        LizzieFrame.openMoreEngineDialog();
      } catch (JSONException e1) {
        e1.printStackTrace();
      }
    }
    return Boolean.valueOf(flag);
  }

  public void close() {
    this.session.close();
    this.conn.close();
  }

  public InputStream getStdout() {
    return this.session.getStdout();
  }

  public InputStream getSterr() {
    return this.session.getStderr();
  }

  public OutputStream getStdin() {
    return this.session.getStdin();
  }
}
