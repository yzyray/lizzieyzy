package featurecat.lizzie.gui;

import featurecat.lizzie.Lizzie;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

public class SetKomi extends JDialog {
  private double komi;
  public static final DecimalFormat FORMAT_KOMI = new DecimalFormat("#0.0");

  public JTextField textFieldKomi;

  public SetKomi() {
    // setType(Type.POPUP);
    setTitle(Lizzie.resourceBundle.getString("SetKomi.title")); // ("设置贴目");
    setAlwaysOnTop(true);
    // setBounds(0, 0, 252, 98);
    Lizzie.setFrameSize(this, 252, 98);

    getContentPane().setLayout(new BorderLayout());
    JPanel buttonPane = new JPanel();
    getContentPane().add(buttonPane, BorderLayout.CENTER);
    JButton okButton = new JButton("确定");
    okButton.setBounds(10, 31, 60, 23);

    okButton.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            applyChange();
          }
        });
    buttonPane.setLayout(null);
    okButton.setActionCommand("OK");
    buttonPane.add(okButton);

    JButton closeButton = new JButton("关闭");
    closeButton.setBounds(166, 31, 60, 23);

    closeButton.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            setVisible(false);
          }
        });
    buttonPane.add(closeButton);
    NumberFormat nf = NumberFormat.getIntegerInstance();
    nf.setGroupingUsed(false);

    JLabel lblChangeTo = new JLabel("设置贴目(负数为倒贴目)：");
    lblChangeTo.setBounds(20, 6, 150, 20);
    buttonPane.add(lblChangeTo);
    lblChangeTo.setHorizontalAlignment(SwingConstants.LEFT);

    textFieldKomi = new JFormattedTextField(FORMAT_KOMI);

    textFieldKomi.setBounds(167, 7, 50, 19);
    buttonPane.add(textFieldKomi);
    textFieldKomi.setText(FORMAT_KOMI.format(Lizzie.board.getHistory().getGameInfo().getKomi()));

    JButton button = new JButton("确定并关闭");
    button.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            applyChange();
            setVisible(false);
          }
        });
    button.setBounds(72, 31, 93, 23);
    buttonPane.add(button);
    try {
      this.setIconImage(ImageIO.read(MoreEngines.class.getResourceAsStream("/assets/logo.png")));
    } catch (IOException e1) {
      // TODO Auto-generated catch block
      e1.printStackTrace();
    }

    this.addWindowListener(
        new WindowAdapter() {
          public void windowActivated(WindowEvent event) // 窗口获得焦点，重新设置数据
              {
            textFieldKomi.setText(
                FORMAT_KOMI.format(Lizzie.board.getHistory().getGameInfo().getKomi()));
          }
        });
    setLocationRelativeTo(Lizzie.frame);
  }

  private void applyChange() {
    try {
      komi = FORMAT_KOMI.parse(textFieldKomi.getText()).doubleValue();
    } catch (ParseException e) {
      // TODO Auto-generated catch block
      return;
    }
    Lizzie.board.clearBestMovesAfter(Lizzie.board.getHistory().getStart());
    Lizzie.board.getHistory().getGameInfo().setKomi(komi);
    Lizzie.board.getHistory().getGameInfo().changeKomi();
    Lizzie.leelaz.sendCommand("komi " + komi);
    if (Lizzie.leelaz.isPondering()) Lizzie.leelaz.ponder();
  }
}
