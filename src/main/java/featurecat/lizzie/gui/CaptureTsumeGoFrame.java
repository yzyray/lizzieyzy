package featurecat.lizzie.gui;

import featurecat.lizzie.Lizzie;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

public class CaptureTsumeGoFrame extends JFrame {
  public CaptureTsumeGoFrame() {
    setTitle(Lizzie.resourceBundle.getString("CaptureTsumeGoFrame.title"));
    JPanel contentPane = new JPanel();
    getContentPane().add(contentPane, BorderLayout.CENTER);
    contentPane.setBorder(new EmptyBorder(6, 15, 8, 15));
    contentPane.setLayout(new GridLayout(3, 2, 5, 5));

    JFontLabel lblStep1 =
        new JFontLabel(Lizzie.resourceBundle.getString("CaptureTsumeGoFrame.lblStep1"));
    contentPane.add(lblStep1);

    JLabel capture1 = new JLabel();
    contentPane.add(capture1);

    JFontLabel lblStep2 =
        new JFontLabel(Lizzie.resourceBundle.getString("CaptureTsumeGoFrame.lblStep2"));
    contentPane.add(lblStep2);

    JLabel capture2 = new JLabel();
    contentPane.add(capture2);

    JFontLabel lblStep3 =
        new JFontLabel(Lizzie.resourceBundle.getString("CaptureTsumeGoFrame.lblStep3"));
    contentPane.add(lblStep3);

    JLabel capture3 = new JLabel();
    contentPane.add(capture3);

    JPanel buttonPane = new JPanel();
    buttonPane.setBorder(new EmptyBorder(6, 6, 6, 6));
    getContentPane().add(buttonPane, BorderLayout.SOUTH);
    buttonPane.setLayout(new BorderLayout(0, 0));

    JFontButton btnStart =
        new JFontButton(Lizzie.resourceBundle.getString("CaptureTsumeGoFrame.btnStart"));
    btnStart.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            setVisible(false);
            Lizzie.frame.startCaptureTsumeGo();
          }
        });
    btnStart.setFocusable(false);
    buttonPane.add(btnStart, BorderLayout.EAST);

    JFontButton btnSettings =
        new JFontButton(Lizzie.resourceBundle.getString("CaptureTsumeGoFrame.btnSettings"));
    btnSettings.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            openTsumegoSettings();
          }
        });

    btnSettings.setFocusable(false);
    buttonPane.add(btnSettings, BorderLayout.WEST);

    JLabel lblTip = new JLabel(Lizzie.resourceBundle.getString("CaptureTsumeGoFrame.lblTip"));
    lblTip.setHorizontalAlignment(SwingConstants.CENTER);
    buttonPane.add(lblTip, BorderLayout.CENTER);

    try {
      capture1.setIcon(
          new ImageIcon(ImageIO.read(getClass().getResourceAsStream("/assets/capture1.png"))));
      capture2.setIcon(
          new ImageIcon(ImageIO.read(getClass().getResourceAsStream("/assets/capture2.png"))));
      capture3.setIcon(
          new ImageIcon(ImageIO.read(getClass().getResourceAsStream("/assets/capture3.png"))));
    } catch (IOException e1) {
      // TODO Auto-generated catch block
      e1.printStackTrace();
    }

    addKeyListener(
        new KeyAdapter() {
          public void keyPressed(KeyEvent e) {
            if (e.getKeyCode() == KeyEvent.VK_T) {
              if (e.isShiftDown()) {
                Lizzie.frame.startCaptureTsumeGo();
              }
            }
          }
        });
    pack();
    setAlwaysOnTop(true);
    try {
      setIconImage(ImageIO.read(getClass().getResourceAsStream("/assets/logo.png")));
    } catch (IOException e) {
      e.printStackTrace();
    }
    setLocation(20, 20);
  }

  private void openTsumegoSettings() {
    CaptureTsumeGoSettings captureTsumeGoSettings = new CaptureTsumeGoSettings(this);
    captureTsumeGoSettings.setVisible(true);
  }
}
