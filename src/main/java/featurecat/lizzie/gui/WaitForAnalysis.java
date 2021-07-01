package featurecat.lizzie.gui;

import featurecat.lizzie.Lizzie;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

public class WaitForAnalysis extends JDialog {
  private JLabel lblAnalsisProgress;

  public WaitForAnalysis() {
    this.setModal(true);
    setResizable(false);
    this.setAlwaysOnTop(Lizzie.frame.isAlwaysOnTop());
    setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
    setTitle(Lizzie.resourceBundle.getString("WaitForAnalysis.title")); // ("分析中,请等待");
    // setSize(378, 93);
    Lizzie.setFrameSize(this, 378, 93);
    getContentPane().setLayout(null);

    lblAnalsisProgress =
        new JFontLabel(
            Lizzie.resourceBundle.getString(
                "WaitForAnalysis.lblAnalsisProgress")); // ("进度: 引擎加载中...");
    lblAnalsisProgress.setHorizontalAlignment(SwingConstants.CENTER);
    lblAnalsisProgress.setBounds(10, 2, 335, 25);
    getContentPane().add(lblAnalsisProgress);

    //    JLabel lblNotice = new JLabel("注: 如分析速度过慢,可在设置中降低每步计算量");
    //    lblNotice.setBounds(10, 30, 289, 15);
    //    getContentPane().add(lblNotice);

    JButton btnSettings =
        new JButton(Lizzie.resourceBundle.getString("WaitForAnalysis.btnSettings")); // ("设置");
    btnSettings.setFocusable(false);
    btnSettings.setMargin(new Insets(0, 0, 0, 0));
    btnSettings.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            AnalysisSettings analysisSettings = new AnalysisSettings(true, false);
            analysisSettings.setVisible(true);
          }
        });
    btnSettings.setBounds(225, 29, 73, 22);
    getContentPane().add(btnSettings);

    JButton btnHide =
        new JButton(Lizzie.resourceBundle.getString("WaitForAnalysis.btnHide")); // ("隐藏界面");
    btnHide.setFocusable(false);
    btnHide.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            setVisible(false);
          }
        });
    btnHide.setMargin(new Insets(0, 0, 0, 0));
    btnHide.setBounds(148, 29, 73, 22);
    getContentPane().add(btnHide);

    JButton btnCancel =
        new JButton(Lizzie.resourceBundle.getString("WaitForAnalysis.btnCancel")); // ("取消分析");
    btnCancel.setFocusable(false);
    btnCancel.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            Lizzie.frame.isBatchAnalysisMode = false;
            Lizzie.frame.destroyAnalysisEngine();
            setVisible(false);
          }
        });
    btnCancel.setMargin(new Insets(0, 0, 0, 0));
    btnCancel.setBounds(71, 29, 73, 22);
    getContentPane().add(btnCancel);
  }

  public void setProgress(int curMove, int allMove) {
    if (curMove == allMove) {
      setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
      lblAnalsisProgress.setText(
          Lizzie.resourceBundle.getString("AnalysisEngine.analyzeComplete") + "!");
      setTitle(Lizzie.resourceBundle.getString("AnalysisEngine.analyzeComplete"));
      if (Lizzie.frame.isBatchAnalysisMode) {
        setVisible(false);
        new Thread() {
          public void run() {
            try {
              Thread.sleep(300);
            } catch (InterruptedException e) {
              // TODO Auto-generated catch block
              e.printStackTrace();
            }
            Lizzie.frame.flashAutoAnaSaveAndLoad();
          }
        }.start();
      } else {
        new Thread() {
          public void run() {
            try {
              Thread.sleep(1000);
            } catch (InterruptedException e) {
              // TODO Auto-generated catch block
              e.printStackTrace();
            }
            setVisible(false);
          }
        }.start();
      }
    } else {
      lblAnalsisProgress.setText(
          Lizzie.resourceBundle.getString("WaitForAnalysis.progress") + curMove + "/" + allMove);
    }
  }
}
