package featurecat.lizzie.gui;

import featurecat.lizzie.Lizzie;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import org.json.JSONArray;

public class Controller extends JDialog {
  // JButton btnTry;

  public Controller(Window owner) {
    super(owner);
    setTitle(Lizzie.resourceBundle.getString("Controller.title"));
    JPanel basePanel = new JPanel();
    basePanel.setBorder(new EmptyBorder(5, 0, 5, 0));
    getContentPane().add(basePanel);
    basePanel.setLayout(new BorderLayout(5, 5));

    JPanel centerPanel = new JPanel();
    centerPanel.setBorder(new EmptyBorder(0, 5, 0, 5));
    basePanel.add(centerPanel, BorderLayout.CENTER);

    centerPanel.setLayout(new GridLayout(4, 2, 5, 5));

    JButton btnFirst = new JButton("|<");
    centerPanel.add(btnFirst);
    btnFirst.setMargin(new Insets(0, 0, 0, 0));
    btnFirst.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            Lizzie.frame.firstMove();
          }
        });

    JButton btnLast = new JButton(">|");
    btnLast.setMargin(new Insets(0, 0, 0, 0));
    centerPanel.add(btnLast);
    btnLast.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            Lizzie.frame.lastMove();
          }
        });

    JButton btnFastBack = new JButton("<<");
    btnFastBack.setMargin(new Insets(0, 0, 0, 0));
    centerPanel.add(btnFastBack);
    btnFastBack.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            LizzieFrame.undo(5);
          }
        });

    JButton btnFastForward = new JButton(">>");
    btnFastForward.setMargin(new Insets(0, 0, 0, 0));
    centerPanel.add(btnFastForward);
    btnFastForward.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            LizzieFrame.redo(5);
          }
        });

    JButton btnBack = new JButton("<");
    btnBack.setMargin(new Insets(0, 0, 0, 0));
    centerPanel.add(btnBack);
    btnBack.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            LizzieFrame.undo(1);
          }
        });

    JButton btnForward = new JButton(">");
    btnForward.setMargin(new Insets(0, 0, 0, 0));
    centerPanel.add(btnForward);
    btnForward.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            LizzieFrame.redo(1);
          }
        });

    JButton btnBackToMain =
        new JButton(Lizzie.resourceBundle.getString("Controller.btnBackToMain"));
    btnBackToMain.setMargin(new Insets(0, 0, 0, 0));
    centerPanel.add(btnBackToMain);

    JButton btnDrawPainting =
        new JButton(Lizzie.resourceBundle.getString("Controller.btnDrawPainting"));
    btnDrawPainting.setMargin(new Insets(0, 0, 0, 0));
    btnDrawPainting.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            Lizzie.frame.drawPainting();
          }
        });

    centerPanel.add(btnDrawPainting);
    btnBackToMain.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            Lizzie.frame.moveToMainTrunk();
          }
        });

    addComponentListener(
        new ComponentAdapter() {
          @Override
          public void componentResized(ComponentEvent e) {
            setBtnFont(btnFirst);
            setBtnFont(btnFastBack);
            setBtnFont(btnBack);
            setBtnFont(btnForward);
            setBtnFont(btnFastForward);
            setBtnFont(btnLast);
            setBtnFont(btnDrawPainting);
            setBtnFont(btnBackToMain);
          }
        });
    pack();
    boolean formPersis = false;
    boolean persisted = Lizzie.config.persistedUi != null;
    if (persisted && Lizzie.config.persistedUi.optJSONArray("ctrl-position") != null) {
      JSONArray pos = Lizzie.config.persistedUi.getJSONArray("ctrl-position");
      if (pos.length() == 4) {
        setBounds(pos.getInt(0), pos.getInt(1), pos.getInt(2), pos.getInt(3));
        formPersis = true;
      }
    }
    if (!formPersis) {
      Lizzie.setFrameSize(this, 500, 460);
      setLocationRelativeTo(Lizzie.frame);
    }
  }

  private void setBtnFont(JButton btn) {
    int width = btn.getWidth();
    int height = btn.getHeight();
    int number = btn.getText().length();
    float newSize = Math.min(width / number, height) / 2.0f;
    btn.setFont(btn.getFont().deriveFont(newSize));
  }
}
