package featurecat.lizzie.gui;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import featurecat.lizzie.Lizzie;

public class CaptureTsumeGoFrame extends JFrame {
  public CaptureTsumeGoFrame() {
    setTitle("抓取死活题");
    JPanel contentPane = new JPanel();
    getContentPane().add(contentPane, BorderLayout.CENTER);
    contentPane.setBorder(new EmptyBorder(6, 15, 8, 15));
    contentPane.setLayout(new GridLayout(3, 2, 5, 5));

    JFontLabel lblStep1 = new JFontLabel("<html>步骤1:<br /><br />&nbsp&nbsp左键点击起始交叉点(0,0)</html>");
    contentPane.add(lblStep1);

    JLabel capture1 = new JLabel();
    contentPane.add(capture1);

    JFontLabel lblStep2 =
        new JFontLabel("<html>步骤2:<br /><br />&nbsp&nbsp左键点击第一个方格的交叉点(1,1)</html>");
    contentPane.add(lblStep2);

    JLabel capture2 = new JLabel();
    contentPane.add(capture2);

    JFontLabel lblStep3 = new JFontLabel("<html>步骤3:<br /><br />&nbsp&nbsp移动鼠标至蓝色方格准确覆盖整个死活题,<br />左键点击完成抓取</html>");
    contentPane.add(lblStep3);

    JLabel capture3 = new JLabel();
    contentPane.add(capture3);

    JPanel buttonPane = new JPanel();
    buttonPane.setBorder(new EmptyBorder(6, 6, 6, 6));
    getContentPane().add(buttonPane, BorderLayout.SOUTH);
    buttonPane.setLayout(new BorderLayout(0, 0));

    JFontButton btnStart = new JFontButton("开始抓取(Shift+T)");
    btnStart.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            setExtendedState(JFrame.ICONIFIED);
            Lizzie.frame.startCaptureTsumeGo();
          }
        });
    buttonPane.add(btnStart, BorderLayout.EAST);

    JFontButton btnSettings = new JFontButton("参数设置");
    btnSettings.addActionListener(new ActionListener() {
    	public void actionPerformed(ActionEvent e) {
    		
    	}
    });
    btnSettings.setFocusable(false);
    buttonPane.add(btnSettings, BorderLayout.WEST);

    JLabel lblNewLabel = new JLabel("注: 点击失误可按ESC取消");
    lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
    buttonPane.add(lblNewLabel, BorderLayout.CENTER);

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

    pack();
    setAlwaysOnTop(true);
    try {
      setIconImage(ImageIO.read(getClass().getResourceAsStream("/assets/logo.png")));
    } catch (IOException e) {
      e.printStackTrace();
    }
    setLocation(20, 20);
  }
}
