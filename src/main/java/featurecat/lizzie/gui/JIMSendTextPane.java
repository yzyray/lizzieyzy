package featurecat.lizzie.gui;

import featurecat.lizzie.Config;
import featurecat.lizzie.Lizzie;
import java.awt.Font;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTextPane;
import javax.swing.KeyStroke;
import javax.swing.text.AbstractDocument;
import javax.swing.text.BoxView;
import javax.swing.text.ComponentView;
import javax.swing.text.Element;
import javax.swing.text.IconView;
import javax.swing.text.LabelView;
import javax.swing.text.ParagraphView;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledEditorKit;
import javax.swing.text.View;
import javax.swing.text.ViewFactory;

/**
 * 该类是真正实现超长单词都能自动换行的 JTextPane 的子类 Java 7 以下版本的 JTextPane 本身都能实现自动换行，对 超长单词都能有效，但从 Java 7
 * 开始读超长单词就不能自动 换行，导致 JTextPane 的实际宽度变大，使得滚动条出现。 下面的方法是对这个 bug 的较好修复。
 *
 * <p>Created by dolphin on 15-2-3.
 */
public class JIMSendTextPane extends JTextPane implements MouseListener {

  private JPopupMenu pop = null; // 弹出菜单
  private JMenuItem copy = null, paste = null, cut = null; // 三个功能菜单
  // 内部类
  // 以下内部类全都用于实现自动强制折行

  public class WarpEditorKit extends StyledEditorKit {
    /** @Fields serialVersionUID : TODO */
    private static final long serialVersionUID = 1L;

    private ViewFactory defaultFactory = new WarpColumnFactory();

    @Override
    public ViewFactory getViewFactory() {
      return defaultFactory;
    }

    private class WarpColumnFactory implements ViewFactory {

      public View create(Element elem) {
        String kind = elem.getName();
        if (kind != null) {
          if (kind.equals(AbstractDocument.ContentElementName)) {
            return new WarpLabelView(elem);
          } else if (kind.equals(AbstractDocument.ParagraphElementName)) {
            return new ParagraphView(elem);
          } else if (kind.equals(AbstractDocument.SectionElementName)) {
            return new BoxView(elem, View.Y_AXIS);
          } else if (kind.equals(StyleConstants.ComponentElementName)) {
            return new ComponentView(elem);
          } else if (kind.equals(StyleConstants.IconElementName)) {
            return new IconView(elem);
          }
        }

        // default to text display
        return new LabelView(elem);
      }
    }

    private class WarpLabelView extends LabelView {

      public WarpLabelView(Element elem) {
        super(elem);
      }

      @Override
      public float getMinimumSpan(int axis) {
        switch (axis) {
          case View.X_AXIS:
            return 0;
          case View.Y_AXIS:
            return super.getMinimumSpan(axis);
          default:
            throw new IllegalArgumentException("Invalid axis: " + axis);
        }
      }
    }
  }

  // 本类

  // 构造函数
  public JIMSendTextPane(boolean isComment) {
    super();
    init();
    if (isComment)
      this.setFont(
          new Font(
              Lizzie.config.uiFontName,
              Font.PLAIN,
              Lizzie.config.commentFontSize > 0
                  ? Lizzie.config.commentFontSize
                  : Config.frameFontSize));
    else this.setFont(new Font(Config.sysDefaultFontName, Font.PLAIN, Config.frameFontSize));
    this.setEditorKit(new WarpEditorKit());
  }

  public void resetEditorKid() {
    this.setEditorKit(new WarpEditorKit());
  }

  private void init() {
    this.addMouseListener(this);
    pop = new JPopupMenu();
    pop.add(copy = new JFontMenuItem(Lizzie.resourceBundle.getString("JTextPane.copy")));
    pop.add(paste = new JFontMenuItem(Lizzie.resourceBundle.getString("JTextPane.paste")));
    pop.add(cut = new JFontMenuItem(Lizzie.resourceBundle.getString("JTextPane.cut")));
    copy.setAccelerator(KeyStroke.getKeyStroke('C', InputEvent.CTRL_MASK));
    paste.setAccelerator(KeyStroke.getKeyStroke('V', InputEvent.CTRL_MASK));
    cut.setAccelerator(KeyStroke.getKeyStroke('X', InputEvent.CTRL_MASK));
    copy.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            action(e);
          }
        });

    paste.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            action(e);
          }
        });
    cut.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            action(e);
          }
        });
    this.add(pop);
  }

  /**
   * 菜单动作
   *
   * @param e
   */
  public void action(ActionEvent e) {
    String str = e.getActionCommand();
    if (str.equals(copy.getText())) { // 复制
      this.copy();
    } else if (str.equals(paste.getText())) { // 粘贴
      this.paste();
    } else if (str.equals(cut.getText())) { // 剪切
      this.cut();
    }
  }

  public JPopupMenu getPop() {
    return pop;
  }

  public void setPop(JPopupMenu pop) {
    this.pop = pop;
  }

  /**
   * 剪切板中是否有文本数据可供粘贴
   *
   * @return true为有文本数据
   */
  public boolean isClipboardString() {
    boolean b = false;
    Clipboard clipboard = this.getToolkit().getSystemClipboard();
    Transferable content = clipboard.getContents(this);
    try {
      if (content.getTransferData(DataFlavor.stringFlavor) instanceof String) {
        b = true;
      }
    } catch (Exception e) {
    }
    return b;
  }

  /**
   * 文本组件中是否具备复制的条件
   *
   * @return true为具备
   */
  public boolean isCanCopy() {
    boolean b = false;
    int start = this.getSelectionStart();
    int end = this.getSelectionEnd();
    if (start != end) b = true;
    return b;
  }

  @Override
  public void mouseClicked(MouseEvent e) {
    // TODO Auto-generated method stub

  }

  @Override
  public void mousePressed(MouseEvent e) {
    // TODO Auto-generated method stub
    if (e.getButton() == MouseEvent.BUTTON3) {
      copy.setEnabled(isCanCopy());
      paste.setEnabled(isClipboardString());
      cut.setEnabled(isCanCopy());
      pop.show(this, e.getX(), e.getY());
    }
  }

  @Override
  public void mouseReleased(MouseEvent e) {
    // TODO Auto-generated method stub

  }

  @Override
  public void mouseEntered(MouseEvent e) {
    // TODO Auto-generated method stub

  }

  @Override
  public void mouseExited(MouseEvent e) {
    // TODO Auto-generated method stub

  }
}
