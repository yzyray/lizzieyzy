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
import javax.swing.JTextField;
import javax.swing.KeyStroke;

public class JFontTextField extends JTextField implements MouseListener {
  private JPopupMenu pop = null; // 弹出菜单
  private JMenuItem copy = null, paste = null, cut = null; // 三个功能菜单

  public JFontTextField() {
    super();
    this.setFont(new Font("", Font.PLAIN, Config.frameFontSize));
    init();
  }

  public JFontTextField(String text) {
    super();
    this.setText(text);
    this.setFont(new Font("", Font.PLAIN, Config.frameFontSize));
    init();
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
          public void actionPerformed(ActionEvent e) {
            action(e);
          }
        });
    cut.addActionListener(
        new ActionListener() {
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
