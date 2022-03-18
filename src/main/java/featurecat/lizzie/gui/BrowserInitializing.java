package featurecat.lizzie.gui;

import featurecat.lizzie.Config;
import featurecat.lizzie.Lizzie;
import featurecat.lizzie.util.Utils;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Window;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.PrintStream;
import java.io.Reader;
import java.nio.charset.Charset;
import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;

public class BrowserInitializing extends JDialog {

  public BrowserInitializing(Window owner) {
    super(owner);
    this.setModal(false);
    this.setResizable(false);
    this.setMinimumSize(new Dimension(Utils.zoomOut(450), Utils.zoomOut(250)));
    this.setTitle(Lizzie.resourceBundle.getString("Message.title"));
    JLabel tip = new JFontLabel(Lizzie.resourceBundle.getString("BrowserFrame.initializing"));
    tip.setBorder(new EmptyBorder(2, 2, 2, 2));
    tip.setHorizontalAlignment(JLabel.LEFT);
    this.add(tip, BorderLayout.NORTH);
    JFontTextArea msg = new JFontTextArea();
    msg.setFont(new Font(Config.sysDefaultFontName, Font.PLAIN, Config.frameFontSize));
    msg.setBorder(BorderFactory.createEmptyBorder());
    msg.setEditable(false);
    msg.setBackground(Color.BLACK);
    msg.setForeground(Color.RED);
    JScrollPane scrollPane = new JScrollPane();
    scrollPane.setBorder(new EmptyBorder(2, 2, 2, 2));
    scrollPane.setViewportView(msg);
    this.add(scrollPane, BorderLayout.CENTER);
    PrintStream printStream;
    try {
      printStream = new PrintStream(new MyOutputStream(msg), true, Charset.defaultCharset().name());
      System.setErr(printStream);
    } catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    pack();
    setLocationRelativeTo(owner);
  }
}

class MyOutputStream extends OutputStream {

  private PipedOutputStream out = new PipedOutputStream();
  private Reader reader;
  private JFontTextArea msg;

  public MyOutputStream(JFontTextArea text) throws IOException {
    PipedInputStream in = new PipedInputStream(out);
    reader = new InputStreamReader(in, Charset.defaultCharset());
    this.msg = text;
  }

  public void write(int i) throws IOException {
    out.write(i);
  }

  public void write(byte[] bytes, int i, int i1) throws IOException {
    out.write(bytes, i, i1);
  }

  public void flush() throws IOException {
    if (reader.ready()) {
      char[] chars = new char[1024];
      int n = reader.read(chars);

      String txt = new String(chars, 0, n);
      msg.append(txt);
      msg.setCaretPosition(msg.getDocument().getLength());
    }
  }
}
