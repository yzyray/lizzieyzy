package featurecat.lizzie.gui;

import featurecat.lizzie.Lizzie;
import featurecat.lizzie.gui.LizzieFrame.HtmlKit;
import java.awt.Desktop;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JTextPane;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.StyleSheet;

public class HtmlMessage extends JDialog {

  public HtmlMessage(String title, String content) {
    this.setModal(true);
    this.setResizable(false);
    // setType(Type.POPUP);
    setTitle(title);
    setAlwaysOnTop(true);
    JTextPane lblMessage = new JTextPane();
    lblMessage.setBackground(this.getBackground());
    HTMLDocument htmlDoc;
    HtmlKit htmlKit;
    StyleSheet htmlStyle;

    htmlKit = new HtmlKit();
    htmlDoc = (HTMLDocument) htmlKit.createDefaultDocument();
    htmlStyle = htmlKit.getStyleSheet();
    String style =
        "body {background:"
            + String.format(
                "%02x%02x%02x",
                Lizzie.config.commentBackgroundColor.getRed(),
                Lizzie.config.commentBackgroundColor.getGreen(),
                Lizzie.config.commentBackgroundColor.getBlue())
            + "; color:#"
            + String.format(
                "%02x%02x%02x",
                Lizzie.config.commentFontColor.getRed(),
                Lizzie.config.commentFontColor.getGreen(),
                Lizzie.config.commentFontColor.getBlue())
            + "; font-family:"
            + Lizzie.config.fontName
            + ", Consolas, Menlo, Monaco, 'Ubuntu Mono', monospace;"
            + (Lizzie.config.commentFontSize > 0
                ? "font-size:" + Lizzie.config.commentFontSize
                : "")
            + "}";
    htmlStyle.addRule(style);

    lblMessage.setEditorKit(htmlKit);
    lblMessage.setDocument(htmlDoc);
    lblMessage.setText(content);
    lblMessage.setEditable(false);
    lblMessage.setOpaque(false);
    lblMessage.putClientProperty(JEditorPane.HONOR_DISPLAY_PROPERTIES, Boolean.TRUE);
    lblMessage.addHyperlinkListener(
        new HyperlinkListener() {
          public void hyperlinkUpdate(HyperlinkEvent e) {
            if (HyperlinkEvent.EventType.ACTIVATED.equals(e.getEventType())) {
              if (Desktop.isDesktopSupported()) {
                try {
                  Desktop.getDesktop().browse(e.getURL().toURI());
                } catch (Exception ex) {
                }
              }
            }
          }
        });
    this.add(lblMessage);
    try {
      this.setIconImage(ImageIO.read(MoreEngines.class.getResourceAsStream("/assets/logo.png")));
    } catch (IOException e) {
      e.printStackTrace();
    }
    pack();
    setLocationRelativeTo(null);
  }
}
