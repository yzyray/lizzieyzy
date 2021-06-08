package featurecat.lizzie.gui;

import featurecat.lizzie.Lizzie;
import featurecat.lizzie.util.Utils;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import org.json.JSONArray;

public class GtpConsolePane extends JDialog {
  private final ResourceBundle resourceBundle =
      Lizzie.config.useLanguage == 0
          ? ResourceBundle.getBundle("l10n.DisplayStrings")
          : (Lizzie.config.useLanguage == 1
              ? ResourceBundle.getBundle("l10n.DisplayStrings", new Locale("zh", "CN"))
              : ResourceBundle.getBundle("l10n.DisplayStrings", new Locale("en", "US")));

  // Display Comment
  // private HTMLDocument htmlDoc;
  // private HTMLEditorKit htmlKit;
  // private StyleSheet htmlStyle;
  private int scrollLength = 0;
  private JScrollPane scrollPane;
  public JIMSendTextPane console;
  public final JFontTextField txtCommand = new JFontTextField();
  public JButton send;
  private JFontLabel lblCommand = new JFontLabel();
  private JPanel pnlCommand = new JPanel();
  private ScheduledExecutorService executor;
  int checkCount = 0;
  private Font gtpFont;

  /** Creates a Gtp Console Window */
  public GtpConsolePane(Window owner) {
    super(owner);
    setTitle(resourceBundle.getString("GtpConsolePane.title"));
    try {
      gtpFont =
          Font.createFont(
              Font.TRUETYPE_FONT,
              Thread.currentThread()
                  .getContextClassLoader()
                  .getResourceAsStream("fonts/SourceCodePro-Regular.ttf"));

      gtpFont = gtpFont.deriveFont(Font.PLAIN, Lizzie.config.frameFontSize);
    } catch (IOException | FontFormatException e) {
      e.printStackTrace();
      gtpFont = new Font(Font.MONOSPACED, Font.PLAIN, Lizzie.config.frameFontSize);
    }

    boolean persisted =
        Lizzie.config.persistedUi != null
            && Lizzie.config.persistedUi.optJSONArray("gtp-console-position") != null
            && Lizzie.config.persistedUi.optJSONArray("gtp-console-position").length() == 4;
    if (persisted) {
      JSONArray pos = Lizzie.config.persistedUi.getJSONArray("gtp-console-position");
      this.setBounds(pos.getInt(0), pos.getInt(1), pos.getInt(2), pos.getInt(3));
      Dimension screensize = Toolkit.getDefaultToolkit().getScreenSize();
      int width = (int) screensize.getWidth();
      int height = (int) screensize.getHeight();
      if (pos.getInt(0) >= width || pos.getInt(1) >= height) this.setLocation(0, 0);
    } else {
      Dimension screensize = Toolkit.getDefaultToolkit().getScreenSize();
      setBounds((int) screensize.getWidth() - 400, (int) screensize.getHeight() - 700, 400, 650);
    }
    console = new JIMSendTextPane(false);
    //    console.addMouseListener(
    //        new MouseAdapter() {
    //          public void mouseClicked(MouseEvent e) {
    //            if (e.getButton() == MouseEvent.BUTTON3) {
    //              String text = console.getSelectedText();
    //              Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
    //              Transferable transferableString = new StringSelection(text);
    //              clipboard.setContents(transferableString, null);
    //              int length = console.getCaretPosition();
    //              console.setCaretPosition(length);
    //            }
    //          }
    //        });
    console.setBorder(BorderFactory.createEmptyBorder());
    console.setEditable(false);
    console.setBackground(Color.BLACK);
    console.setForeground(Color.LIGHT_GRAY);
    console.setFont(gtpFont);
    //   console.setEditorKit(htmlKit);
    //   console.setDocument(htmlDoc);
    scrollPane = new JScrollPane();
    scrollPane.setBorder(BorderFactory.createEmptyBorder());
    txtCommand.setBackground(Color.DARK_GRAY);
    txtCommand.setForeground(Color.WHITE);
    lblCommand.setFont(new Font("", Font.BOLD, 11));
    lblCommand.setOpaque(true);
    lblCommand.setBackground(Color.DARK_GRAY);
    lblCommand.setForeground(Color.WHITE);
    lblCommand.setText("GTP>");
    pnlCommand.setLayout(new BorderLayout(0, 0));
    pnlCommand.add(lblCommand, BorderLayout.WEST);

    pnlCommand.add(txtCommand);

    getContentPane()
        .addKeyListener(
            new KeyAdapter() {
              public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_E) {
                  Lizzie.frame.toggleGtpConsole();
                }
              }
            });
    console.addKeyListener(
        new KeyAdapter() {
          public void keyPressed(KeyEvent e) {
            if (e.getKeyCode() == KeyEvent.VK_E) {
              Lizzie.frame.toggleGtpConsole();
            }
          }
        });

    JPanel buttonPane = new JPanel();
    JButton clear = new JButton(resourceBundle.getString("GtpConsolePane.clear"));
    clear.setFocusable(false);
    clear.setFocusable(false);
    clear.setMargin(new Insets(0, 5, 0, 5));
    clear.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            console.setText("");
          }
        });

    JButton commands = new JButton(resourceBundle.getString("GtpConsolePane.commands"));
    commands.setFocusable(false);
    commands.setFocusable(false);
    commands.setMargin(new Insets(0, 0, 0, 0));
    commands.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            openCommands();
          }
        });
    // pnlCommand.add(clear, BorderLayout.EAST);

    send = new JButton(resourceBundle.getString("GtpConsolePane.send"));
    send.addActionListener(e -> postCommand(e));
    buttonPane.setLayout(new GridLayout(1, 3, 0, 0));
    send.setMargin(new Insets(0, 5, 0, 5));
    send.setFocusable(false);
    buttonPane.add(send);
    buttonPane.add(commands);
    buttonPane.add(clear);
    pnlCommand.add(buttonPane, BorderLayout.EAST);
    getContentPane().add(scrollPane, BorderLayout.CENTER);
    getContentPane().add(pnlCommand, BorderLayout.SOUTH);
    scrollPane.setViewportView(console);
    getRootPane().setBorder(BorderFactory.createEmptyBorder());
    // getRootPane().setWindowDecorationStyle(JRootPane.PLAIN_DIALOG);
    // setVisible(true);

    txtCommand.addActionListener(e -> postCommand(e));
    this.addWindowListener(
        new WindowAdapter() {
          public void windowClosing(WindowEvent e) {
            Lizzie.frame.toggleGtpConsole();
          }
        });
    executor = Executors.newSingleThreadScheduledExecutor();
    executor.execute(this::read);
  }

  public void openCommands() {
    // TODO Auto-generated method stub
    if (Lizzie.leelaz.commandLists.isEmpty()) {
      Utils.showMsg(resourceBundle.getString("GtpConsolePane.noCommands"));
      return;
    }
    FastCommands fastCommands = new FastCommands(this);
    fastCommands.setVisible(true);
  }

  public void setDocs(String str, Color col, boolean isCommand, int fontSize) {
    SimpleAttributeSet attrSet = new SimpleAttributeSet();
    StyleConstants.setForeground(attrSet, col);
    // 颜色
    if (isCommand) {
      StyleConstants.setFontFamily(attrSet, Lizzie.config.uiFontName);
    }
    StyleConstants.setFontSize(attrSet, fontSize);
    // 字体大小
    insert(str, attrSet);
  }

  public void insert(String str, AttributeSet attrSet) {
    Document doc = console.getDocument();
    //   str = "\n " + str;
    try {
      doc.insertString(doc.getLength(), str, attrSet);
    } catch (BadLocationException e) {
      System.out.println("BadLocationException:   " + e);
    }
  }

  private void read() {
    while (true) {
      try {
        Thread.sleep(300);
        if ((Lizzie.leelaz != null && !Lizzie.leelaz.isLoaded())
            || (Lizzie.engineManager.isPreEngineGame
                && (!Lizzie.engineManager
                        .engineList
                        .get(Lizzie.engineManager.engineGameInfo.whiteEngineIndex)
                        .isLoaded()
                    || !Lizzie.engineManager
                        .engineList
                        .get(Lizzie.engineManager.engineGameInfo.blackEngineIndex)
                        .isLoaded()))) Lizzie.frame.refresh();
      } catch (InterruptedException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
      checkCount++;
      if (checkCount > 100) {
        checkCount = 0;
        checkConsole();
      } else {
        // checkConsole();
        // format(commandTexts)
        //   addText(commandTexts);
        int length = console.getDocument().getLength();
        if (length != scrollLength) {
          scrollLength = length;
          console.setCaretPosition(scrollLength);
        }
        // commandTexts = "";
      }
    }
  }

  public void checkConsole() {
    if (console.getText().length() > 200000) {
      console.setText(
          console
              .getText()
              .substring(console.getText().length() - 70000, console.getText().length()));
      console.setCaretPosition(console.getDocument().getLength());
    }
  }

  public void addCommand(String command, int commandNumber, String engineName) {
    if (command == null || command.trim().length() == 0) {
      return;
    }
    setDocs(engineName + "> " + command + "\n", Color.WHITE, true, Lizzie.config.frameFontSize);
  }

  public void addEstimateCommand(String command, int commandNumber) {
    if (command == null || command.trim().length() == 0) {
      return;
    }
    setDocs(
        resourceBundle.getString("GtpConsolePane.estimateEngine") + "> " + command + "\n",
        Color.WHITE,
        true,
        Lizzie.config.frameFontSize);
    //   commandTexts+=formatZenCommand(command, commandNumber);
    // addText();
  }

  public void addReadBoardCommand(String command) {
    if (command == null || command.trim().length() == 0) {
      return;
    }
    setDocs("ReadBoard > " + command + "\n", Color.WHITE, true, Lizzie.config.frameFontSize);
    //   commandTexts += command + "\n";
    // commandTexts+=formatReadBoardCommand(command);
    // addText();
  }

  public void addLine(String line) {
    if (line == null || line.trim().length() == 0) {
      return;
    }
    setDocs(" " + line, Color.GREEN, false, Lizzie.config.frameFontSize);
  }

  public void addLineEstimate(String line) {
    if (line == null || line.trim().length() == 0) {
      return;
    }
    setDocs(" " + line, Color.GRAY, false, Lizzie.config.frameFontSize);
  }

  private void postCommand(ActionEvent e) {
    if (txtCommand.getText() == null || txtCommand.getText().trim().isEmpty()) {
      return;
    }
    String command = txtCommand.getText().trim();
    txtCommand.setText("");

    if (Lizzie.engineManager.isEngineGame) {
      this.setDocs(
          resourceBundle.getString("GtpConsolePane.isEngineGame") + "\r\n",
          new Color(255, 255, 0),
          false,
          Lizzie.config.frameFontSize);
      return;
    }

    if (Lizzie.leelaz != null) {
      if (command.toLowerCase().startsWith("genmove")
          || command.toLowerCase().startsWith("lz-genmove")
          || command.toLowerCase().startsWith("kata-genmove")
          || command.toLowerCase().startsWith("play")) {
        String cmdParams[] = command.split(" ");
        if (cmdParams.length >= 2) {
          String param1 = cmdParams[1].toLowerCase();
          boolean needPass = (Lizzie.board.getData().blackToPlay != "b".equals(param1));
          if (needPass) {
            Lizzie.board.pass();
          }
          boolean isAnalyze =
              command.toLowerCase().startsWith("lz-genmove")
                  || command.toLowerCase().startsWith("kata-genmove");
          if (command.toLowerCase().startsWith("genmove") || isAnalyze) {
            if (!Lizzie.leelaz.isThinking) {
              Lizzie.leelaz.time_settings();
              Lizzie.leelaz.isInputCommand = true;
              Lizzie.leelaz.genmove(param1, isAnalyze);
            }
          } else {
            if (cmdParams.length >= 3) {
              String param2 = cmdParams[2].toUpperCase();
              Lizzie.board.place(param2);
            }
          }
        } else {
          this.setDocs(
              resourceBundle.getString("GtpConsolePane.wrongParameters") + "\r\n",
              new Color(255, 255, 0),
              false,
              Lizzie.config.frameFontSize);
        }
      } else if ("clear_board".equals(command.toLowerCase())) {
        Lizzie.board.clear(false);
        Lizzie.frame.refresh();
      } else if ("heatmap".equals(command.toLowerCase())) {
        Lizzie.leelaz.toggleHeatmap(false);
      } else if (command.toLowerCase().startsWith("kata-raw")) {
        Lizzie.leelaz.setHeatmap();
        Lizzie.leelaz.sendCommand(command);
      } else if (command.toLowerCase().startsWith("boardsize")) {
        String cmdParams[] = command.split(" ");
        if (cmdParams.length >= 2) {
          int width = Integer.parseInt(cmdParams[1]);
          int height = width;
          if (cmdParams.length >= 3) {
            height = Integer.parseInt(cmdParams[2]);
          }
          Lizzie.board.reopen(width, height);
        } else {
          this.setDocs(
              resourceBundle.getString("GtpConsolePane.wrongParameters") + "\r\n",
              new Color(255, 255, 0),
              false,
              Lizzie.config.frameFontSize);
        }
      } else if (command.toLowerCase().startsWith("komi")) {
        String cmdParams[] = command.split(" ");
        if (cmdParams.length == 2) {
          Lizzie.board.getHistory().getGameInfo().setKomi(Double.parseDouble(cmdParams[1]));
          Lizzie.board.getHistory().getGameInfo().changeKomi();
          // Lizzie.frame.komi = cmdParams[1];
          if (Lizzie.frame.toolbar.setkomi != null)
            Lizzie.frame.toolbar.setkomi.textFieldKomi.setText(cmdParams[1]);
        }
        Lizzie.leelaz.sendCommand(command);
        Lizzie.board.clearbestmovesafter(Lizzie.board.getHistory().getStart());
        if (Lizzie.leelaz.isPondering()) {
          Lizzie.leelaz.ponder();
        }
      } else if ("undo".equals(command)) {
        if (!Lizzie.board.previousMove(true))
          this.setDocs(
              resourceBundle.getString("GtpConsolePane.wrongPrevious") + "\r\n",
              new Color(255, 255, 0),
              false,
              Lizzie.config.frameFontSize);
      } else if (command.startsWith("pda")
          || command.startsWith("dympdacap")
          || command.startsWith("getpda")
          || command.startsWith("getdympdacap")) {
        if (command.toLowerCase().startsWith("pda")) {
          String[] params = command.trim().split(" ");
          if (params.length == 2) {
            try {
              Double pda = Double.parseDouble(params[1]);
              Lizzie.leelaz.pda = pda;
              Lizzie.leelaz.isStaticPda = true;
              if (Lizzie.frame.extraMode == 2) Lizzie.leelaz2.pda = 0;
              if (Lizzie.frame.menu.setPda != null)
                Lizzie.frame.menu.setPda.curPDA.setText(pda + "");
              Lizzie.frame.menu.txtPDA.setText(pda + "");
            } catch (Exception es) {
            }
          }
        }
        if (command.toLowerCase().startsWith("dympdacap")) {
          Lizzie.leelaz.sendCommand("pda 0");
          Lizzie.frame.menu.txtPDA.setText("0.000");
          Lizzie.leelaz.isStaticPda = false;
          String[] params = command.trim().split(" ");
          if (params.length == 2) {
            try {
              double dymCap = Double.parseDouble(params[1]);
              Lizzie.leelaz.pdaCap = dymCap;
            } catch (Exception es) {
            }
          }
        }
        Lizzie.leelaz.sendCommand(command);
        if (Lizzie.leelaz.isPondering()) Lizzie.leelaz.ponder();
      } else if (command.startsWith("dympdacap")) {
        String[] params = command.trim().split(" ");
        if (params.length == 2) {
          try {
            Double pdaCap = Double.parseDouble(params[1]);
            Lizzie.leelaz.pdaCap = pdaCap;
            if (Lizzie.frame.menu.setPda != null)
              Lizzie.frame.menu.setPda.txtDymCap.setText(pdaCap + "");
          } catch (Exception es) {
          }
        }
        Lizzie.leelaz.sendCommand(command);
        if (Lizzie.leelaz.isPondering()) Lizzie.leelaz.ponder();
      } else {
        Lizzie.leelaz.sendCommand(command);
      }
    }
  }

  public void addErrorLine(String line) {
    // TODO Auto-generated method stub
    setDocs(line, new Color(255, 0, 0), false, Lizzie.config.frameFontSize);
  }

  public void setViewEnd() {
    // TODO Auto-generated method stub
    Color cl = console.getSelectionColor();
    console.setSelectionColor(new Color(0, 0, 0, 0));
    console.selectAll();
    console.setSelectionColor(cl);
    getContentPane().requestFocus();
  }
}
