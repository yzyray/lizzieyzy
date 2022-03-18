package featurecat.lizzie.gui;

import featurecat.lizzie.Lizzie;
import featurecat.lizzie.util.MultiOutputStream;
import java.awt.*;
import java.awt.event.*;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import javax.imageio.ImageIO;
import javax.swing.*;
import me.friwi.jcefmaven.*;
import org.cef.CefApp;
import org.cef.CefApp.CefAppState;
import org.cef.CefClient;
import org.cef.CefSettings.LogSeverity;
import org.cef.browser.CefBrowser;
import org.cef.browser.CefFrame;
import org.cef.browser.CefMessageRouter;
import org.cef.handler.CefDisplayHandlerAdapter;
import org.cef.handler.CefFocusHandlerAdapter;
import org.cef.handler.CefLifeSpanHandlerAdapter;

public class BrowserFrame extends JFrame {
  private static final long serialVersionUID = -5570653778104813836L;
  private final JTextField address_;
  private final CefApp cefApp_;
  private final CefClient client_;
  private final CefBrowser browser_;
  private final Component browerUI_;
  private boolean browserFocus_ = true;
  private JToolBar toolbar;
  private boolean isYike;
  /**
   * To display a simple browser window, it suffices completely to create an instance of the class
   * CefBrowser and to assign its UI component to your application (e.g. to your content pane). But
   * to be more verbose, this CTOR keeps an instance of each object on the way to the browser UI.
   */
  public BrowserFrame(String startURL, String title, boolean yike)
      throws UnsupportedPlatformException, CefInitializationException, IOException,
          InterruptedException {
    // (0) Initialize CEF using the maven loader
    this.isYike = yike;
    this.setTitle(title);
    try {
      setIconImage(ImageIO.read(getClass().getResourceAsStream("/assets/logo.png")));
    } catch (IOException e) {
      e.printStackTrace();
    }
    CefAppBuilder builder = new CefAppBuilder();
    // windowless_rendering_enabled must be set to false if not wanted.
    builder.getCefSettings().windowless_rendering_enabled = false;
    builder.getCefSettings().log_severity = LogSeverity.LOGSEVERITY_DISABLE;
    // USE builder.setAppHandler INSTEAD OF CefApp.addAppHandler!
    // Fixes compatibility issues with MacOSX
    builder.setAppHandler(
        new MavenCefAppHandlerAdapter() {
          @Override
          public void stateHasChanged(org.cef.CefApp.CefAppState state) {
            // Shutdown the app if the native CEF part is terminated
            if (state == CefAppState.TERMINATED) setVisible(false);
          }
        });

    // (1) The entry point to JCEF is always the class CefApp. There is only one
    //     instance per application and therefore you have to call the method
    //     "getInstance()" instead of a CTOR.
    //
    //     CefApp is responsible for the global CEF context. It loads all
    //     required native libraries, initializes CEF accordingly, starts a
    //     background task to handle CEF's message loop and takes care of
    //     shutting down CEF after disposing it.
    //
    //     WHEN WORKING WITH MAVEN: Use the builder.build() method to
    //     build the CefApp on first run and fetch the instance on all consecutive
    //     runs. This method is thread-safe and will always return a valid app
    //     instance.
    if (!Lizzie.config.browserInitiazed)
      Lizzie.frame.browserInitializing = new BrowserInitializing(Lizzie.frame);
    new Thread() {
      public void run() {
        try {
          Thread.sleep(500);
          if (!Lizzie.config.browserInitiazed) Lizzie.frame.browserInitializing.setVisible(true);
          else Lizzie.frame.browserInitializing.dispose();
        } catch (InterruptedException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
      }
    }.start();
    cefApp_ = builder.build();
    Lizzie.config.browserInitiazed = true;
    if (Lizzie.frame.browserInitializing != null && Lizzie.frame.browserInitializing.isVisible()) {
      Lizzie.frame.browserInitializing.setVisible(false);
      Lizzie.frame.browserInitializing.dispose();
    }
    if (Lizzie.config.logConsoleToFile) {
      PrintStream oldErrorPrintStream = System.err;
      FileOutputStream bosError =
          new FileOutputStream("LastErrorLogs_" + Lizzie.lizzieVersion + ".txt", true);
      MultiOutputStream multiError =
          new MultiOutputStream(new PrintStream(bosError), oldErrorPrintStream);
      System.setErr(new PrintStream(multiError));
    }
    // (2) JCEF can handle one to many browser instances simultaneous. These
    //     browser instances are logically grouped together by an instance of
    //     the class CefClient. In your application you can create one to many
    //     instances of CefClient with one to many CefBrowser instances per
    //     client. To get an instance of CefClient you have to use the method
    //     "createClient()" of your CefApp instance. Calling an CTOR of
    //     CefClient is not supported.
    //
    //     CefClient is a connector to all possible events which come from the
    //     CefBrowser instances. Those events could be simple things like the
    //     change of the browser title or more complex ones like context menu
    //     events. By assigning handlers to CefClient you can control the
    //     behavior of the browser. See tests.detailed.MainFrame for an example
    //     of how to use these handlers.
    client_ = cefApp_.createClient();

    // (3) Create a simple message router to receive messages from CEF.
    CefMessageRouter msgRouter = CefMessageRouter.create();
    client_.addMessageRouter(msgRouter);

    // (4) One CefBrowser instance is responsible to control what you'll see on
    //     the UI component of the instance. It can be displayed off-screen
    //     rendered or windowed rendered. To get an instance of CefBrowser you
    //     have to call the method "createBrowser()" of your CefClient
    //     instances.
    //
    //     CefBrowser has methods like "goBack()", "goForward()", "loadURL()",
    //     and many more which are used to control the behavior of the displayed
    //     content. The UI is held within a UI-Compontent which can be accessed
    //     by calling the method "getUIComponent()" on the instance of CefBrowser.
    //     The UI component is inherited from a java.awt.Component and therefore
    //     it can be embedded into any AWT UI.
    browser_ = client_.createBrowser(startURL, false, false);
    browerUI_ = browser_.getUIComponent();

    // (5) For this minimal browser, we need only a text field to enter an URL
    //     we want to navigate to and a CefBrowser window to display the content
    //     of the URL. To respond to the input of the user, we're registering an
    //     anonymous ActionListener. This listener is performed each time the
    //     user presses the "ENTER" key within the address field.
    //     If this happens, the entered value is passed to the CefBrowser
    //     instance to be loaded as URL.
    address_ = new JTextField(startURL);
    address_.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            browser_.loadURL(address_.getText());
          }
        });

    address_.addKeyListener(
        new KeyAdapter() {
          public void keyPressed(KeyEvent e) {
            if (e.getKeyChar() == KeyEvent.VK_ENTER) // 按回车键执行相应操作;
            {
              browser_.loadURL(address_.getText());
              if (isYike) Lizzie.frame.syncOnline(address_.getText());
            }
          }
        });

    // Update the address field when the browser URL changes.
    client_.addDisplayHandler(
        new CefDisplayHandlerAdapter() {
          @Override
          public void onAddressChange(CefBrowser browser, CefFrame frame, String url) {
            address_.setText(url);
          }
        });

    // Clear focus from the browser when the address field gains focus.
    address_.addFocusListener(
        new FocusAdapter() {
          @Override
          public void focusGained(FocusEvent e) {
            if (!browserFocus_) return;
            browserFocus_ = false;
            KeyboardFocusManager.getCurrentKeyboardFocusManager().clearGlobalFocusOwner();
            address_.requestFocus();
          }
        });

    // Clear focus from the address field when the browser gains focus.
    client_.addFocusHandler(
        new CefFocusHandlerAdapter() {
          @Override
          public void onGotFocus(CefBrowser browser) {
            if (browserFocus_) return;
            browserFocus_ = true;
            KeyboardFocusManager.getCurrentKeyboardFocusManager().clearGlobalFocusOwner();
            browser.setFocus(true);
          }

          @Override
          public void onTakeFocus(CefBrowser browser, boolean next) {
            browserFocus_ = false;
          }
        });

    // 以下为处理无法打开新链接的问题
    client_.addLifeSpanHandler(
        new CefLifeSpanHandlerAdapter() {

          @Override
          public boolean onBeforePopup(
              CefBrowser browser, CefFrame frame, String target_url, String target_frame_name) {
            if (isYike && !browser.getURL().equals(target_url)) {
              Lizzie.frame.syncOnline(target_url);
            }
            browser_.loadURL(target_url);
            // 返回true表示取消弹出窗口
            return true;
          }
        });

    // (6) All UI components are assigned to the default content pane of this
    //     JFrame and afterwards the frame is made visible to the user.
    toolbar = new JToolBar();
    toolbar.setBorderPainted(false);
    toolbar.setFloatable(false);
    // toolbarPanel.setLayout(new BorderLayout(0, 0));

    ImageIcon iconLeft = new ImageIcon();
    try {
      iconLeft.setImage(ImageIO.read(getClass().getResourceAsStream("/assets/left.png")));
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    JButton back = new JButton(iconLeft);
    back.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            if (browser_.canGoBack()) browser_.goBack();
          }
        });
    back.setFocusable(false);

    JButton load = new JButton(Lizzie.resourceBundle.getString("LizzieFrame.onLoad")); // ("加载");
    load.setFocusable(false);
    load.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            // TBD未完成
            browser_.loadURL(address_.getText());
            if (isYike) Lizzie.frame.syncOnline(address_.getText());
          }
        });

    JButton stop =
        new JButton(Lizzie.resourceBundle.getString("LizzieFrame.stopSync")); // ("停止同步");
    stop.setFocusable(false);
    stop.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            // TBD
            if (LizzieFrame.onlineDialog != null) {
              LizzieFrame.onlineDialog.stopSync();
            }
          }
        });
    toolbar.add(back);
    toolbar.add(address_);
    toolbar.add(load);
    toolbar.add(stop);

    JPanel view = new JPanel();
    view.setLayout(null);
    view.add(browerUI_);

    view.addComponentListener(
        new ComponentAdapter() {
          @Override
          public void componentResized(ComponentEvent e) {
            view.revalidate();
            browerUI_.setBounds(0, 0, view.getWidth(), view.getHeight());
          }
        });

    getContentPane()
        .add(toolbar, BorderLayout.PAGE_START); // .add(toolbarPanel, BorderLayout.NORTH);
    toolbar.setVisible(isYike);
    getContentPane().add(view, BorderLayout.CENTER);
    getRootPane().setBorder(BorderFactory.createEmptyBorder());
    pack();
    setSize(Lizzie.frame.bowserWidth, Lizzie.frame.bowserHeight);
    setLocation(Lizzie.frame.bowserX, Lizzie.frame.bowserY);
    setFrameSize();
    setVisible(true);

    // (7) To take care of shutting down CEF accordingly, it's important to call
    //     the method "dispose()" of the CefApp instance if the Java
    //     application will be closed. Otherwise you'll get asserts from CEF.
    addWindowListener(
        new WindowAdapter() {
          @Override
          public void windowClosing(WindowEvent e) {
            if (isYike) {
              Lizzie.frame.bowserX = getX();
              Lizzie.frame.bowserY = getY();
              Lizzie.frame.bowserWidth = getWidth();
              Lizzie.frame.bowserHeight = getHeight();
            }
            setVisible(false);
          }
        });
  }

  private void setFrameSize() {
    // TODO Auto-generated method stub
    if (isYike) {
      setSize(Lizzie.frame.bowserWidth, Lizzie.frame.bowserHeight);
      setLocation(Lizzie.frame.bowserX, Lizzie.frame.bowserY);
    } else setExtendedState(Frame.MAXIMIZED_BOTH);
  }

  public void openURL(String url, String title, boolean yike) {
    // TODO Auto-generated method stub
    SwingUtilities.invokeLater(
        new Runnable() {
          public void run() {
            isYike = yike;
            browser_.loadURL(url);
            setTitle(title);
            toolbar.setVisible(isYike);
            setFrameSize();
            setVisible(true);
          }
        });
  }
}
