package featurecat.lizzie.analysis;

public class RemoteConnect {
  private String ip;
  private int port;

  public String getIp() {
    return ip;
  }

  public int getPort() {
    return port;
  }

  public void setIp(String ip) {
    this.ip = ip;
  }

  public void setPort(String port) {
    try {
      this.port = Integer.parseInt(port);
    } catch (Exception e) {
      this.port = 22;
    }
  }
}
