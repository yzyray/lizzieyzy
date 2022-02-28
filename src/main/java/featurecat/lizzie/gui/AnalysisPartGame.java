package featurecat.lizzie.gui;

import featurecat.lizzie.Lizzie;
import featurecat.lizzie.util.Utils;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JDialog;

public class AnalysisPartGame extends JDialog {
  private JFontTextField txtStartMove;
  private JFontTextField txtEndMove;

  public AnalysisPartGame() {
    this.setModal(true);
    this.setAlwaysOnTop(Lizzie.frame.isAlwaysOnTop());
    setResizable(false);
    setTitle(Lizzie.resourceBundle.getString("AnalysisPartGame.title")); // ("闪电分析");
    // setSize(352, 119);
    Lizzie.setFrameSize(
        this,
        Lizzie.config.isFrameFontSmall() ? 345 : (Lizzie.config.isFrameFontMiddle() ? 450 : 550),
        119);
    getContentPane().setLayout(null);

    JFontButton btnStart =
        new JFontButton(Lizzie.resourceBundle.getString("AnalysisPartGame.btnStart")); // ("开始分析");
    btnStart.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            Lizzie.config.analysisStartMove =
                Utils.parseTextToInt(txtStartMove, Lizzie.config.analysisStartMove);
            Lizzie.config.analysisEndMove =
                Utils.parseTextToInt(txtEndMove, Lizzie.config.analysisEndMove);
            Lizzie.config.uiConfig.put("analysis-start-move", Lizzie.config.analysisStartMove);
            Lizzie.config.uiConfig.put("analysis-end-move", Lizzie.config.analysisEndMove);
            setVisible(false);
            Lizzie.frame.flashAnalyzeGame(false, false);
          }
        });
    btnStart.setBounds(
        124,
        60,
        Lizzie.config.isFrameFontSmall() ? 93 : (Lizzie.config.isFrameFontMiddle() ? 105 : 120),
        Lizzie.config.isFrameFontSmall() ? 23 : (Lizzie.config.isFrameFontMiddle() ? 26 : 30));
    getContentPane().add(btnStart);

    JFontLabel lblStartMove =
        new JFontLabel(Lizzie.resourceBundle.getString("AnalysisPartGame.startMove")); // ("开始手数:");
    lblStartMove.setBounds(
        10,
        7,
        Lizzie.config.isFrameFontSmall() ? 66 : (Lizzie.config.isFrameFontMiddle() ? 76 : 86),
        23);
    getContentPane().add(lblStartMove);

    txtStartMove = new JFontTextField();
    txtStartMove.setBounds(
        Lizzie.config.isFrameFontSmall() ? 80 : (Lizzie.config.isFrameFontMiddle() ? 90 : 100),
        7,
        66,
        23);
    getContentPane().add(txtStartMove);

    JFontLabel lblEndMove =
        new JFontLabel(Lizzie.resourceBundle.getString("AnalysisPartGame.endMove")); // ("结束手数:");
    lblEndMove.setBounds(
        190,
        7,
        Lizzie.config.isFrameFontSmall() ? 66 : (Lizzie.config.isFrameFontMiddle() ? 76 : 86),
        23);
    getContentPane().add(lblEndMove);

    txtEndMove = new JFontTextField();
    txtEndMove.setBounds(
        Lizzie.config.isFrameFontSmall() ? 260 : (Lizzie.config.isFrameFontMiddle() ? 270 : 280),
        7,
        66,
        23);
    getContentPane().add(txtEndMove);
    txtStartMove.setText(
        Lizzie.config.analysisStartMove > 0 ? String.valueOf(Lizzie.config.analysisStartMove) : "");
    txtEndMove.setText(
        Lizzie.config.analysisEndMove > 0 ? String.valueOf(Lizzie.config.analysisEndMove) : "");

    JFontLabel lblNotice =
        new JFontLabel(
            Lizzie.resourceBundle.getString(
                "AnalysisPartGame.lblNotice")); // ("注:手数为空代表从第一手开始分析/分析到最后一手为止");
    lblNotice.setBounds(10, 33, 601, 23);
    getContentPane().add(lblNotice);
    setLocationRelativeTo(Lizzie.frame != null ? Lizzie.frame : null);
  }
}
