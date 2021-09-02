package featurecat.lizzie.gui;

import featurecat.lizzie.Lizzie;
import java.awt.BorderLayout;
import java.io.IOException;
import java.text.NumberFormat;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class SetMatchAiPara extends JDialog {
  public SetMatchAiPara() {
    // setType(Type.POPUP);
    setTitle(Lizzie.resourceBundle.getString("SetMatchAiPara.title")); // ("AI评分设置");
    setAlwaysOnTop(true);
    // setBounds(0, 0, 810, 255);
    setResizable(false);
    Lizzie.setFrameSize(this, 810, 215);
    getContentPane().setLayout(new BorderLayout());
    JPanel buttonPane = new JPanel();
    getContentPane().add(buttonPane, BorderLayout.CENTER);
    buttonPane.setLayout(null);
    NumberFormat nf = NumberFormat.getIntegerInstance();
    nf.setGroupingUsed(false);

    JLabel lblAi =
        new JLabel(
            Lizzie.resourceBundle.getString(
                "SetMatchAiPara.lblAi")); // ("AI评分基于每一手所吻合点的计算量,最高100分最低0分,每一手计算量越接近当手计算量最高值的点,则总评越接近100");
    lblAi.setBounds(10, 10, 762, 29);
    buttonPane.add(lblAi);

    //    JButton btnConfirm =
    //        new JButton(Lizzie.resourceBundle.getString("SetMatchAiPara.btnConfirm")); // ("确定");
    //    btnConfirm.addActionListener(
    //        new ActionListener() {
    //          public void actionPerformed(ActionEvent e) {
    //
    //            if (applyChange()) {
    //              setVisible(false);
    //              Lizzie.board.clearNodeInfo(Lizzie.board.getHistory().getStart());
    //              Lizzie.board.setMovelistAll();
    //              Lizzie.frame.refresh();
    //            }
    //          }
    //        });
    //    btnConfirm.setBounds(329, 177, 93, 29);
    //    buttonPane.add(btnConfirm);

    //    txtMatchAiTemprature = new JTextField();
    //    txtMatchAiTemprature.setBounds(Lizzie.config.isChinese ? 75 : 118, 151, 41, 18);
    //    buttonPane.add(txtMatchAiTemprature);
    //    txtMatchAiTemprature.setText(Lizzie.config.matchAiTemperature + "");

    //    JLabel lblMatchTemprature =
    //        new JLabel(
    //            Lizzie.resourceBundle.getString("SetMatchAiPara.lblMatchTemprature")); //
    // ("设置吻合温度:");
    //    lblMatchTemprature.setBounds(10, 153, 125, 15);
    //    buttonPane.add(lblMatchTemprature);

    JLabel lblCalculation =
        new JLabel(Lizzie.resourceBundle.getString("SetMatchAiPara.lblCalculation")); // ("计算方式:");
    lblCalculation.setBounds(10, 50, 93, 15);
    buttonPane.add(lblCalculation);

    ImageIcon iconForm = new ImageIcon();
    if (Lizzie.config.isChinese) {
      try {
        iconForm.setImage(
            ImageIO.read(AnalysisFrame.class.getResourceAsStream("/assets/matchform.png"))
                .getScaledInstance(708, 96, java.awt.Image.SCALE_SMOOTH));
      } catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    } else {
      try {
        iconForm.setImage(
            ImageIO.read(AnalysisFrame.class.getResourceAsStream("/assets/matchformeng.png"))
                .getScaledInstance(708, 96, java.awt.Image.SCALE_SMOOTH));
      } catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }

    JLabel lblNewLabel = new JLabel(iconForm);
    lblNewLabel.setBounds(76, 45, 708, 96);
    buttonPane.add(lblNewLabel);
    try {
      this.setIconImage(ImageIO.read(MoreEngines.class.getResourceAsStream("/assets/logo.png")));
    } catch (IOException e) {
      e.printStackTrace();
    }
    setLocationRelativeTo(getOwner());
  }

  //  private class DigitOnlyFilter extends DocumentFilter {
  //    @Override
  //    public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr)
  //        throws BadLocationException {
  //      String newStr = string != null ? string.replaceAll("\\D++", "") : "";
  //      if (!newStr.isEmpty()) {
  //        fb.insertString(offset, newStr, attr);
  //      }
  //    }
  //  }

  //  private boolean applyChange() {
  //    boolean error = false;
  //    double matchAiTemperature = Lizzie.config.matchAiTemperature;
  //    try {
  //      matchAiTemperature = Double.parseDouble(this.txtMatchAiTemprature.getText());
  //    } catch (Exception e) {
  //      error = true;
  //      txtMatchAiTemprature.setBackground(Color.RED);
  //    }
  //    Lizzie.config.matchAiTemperature = matchAiTemperature;
  //    Lizzie.config.uiConfig.put("match-ai-temperature", Lizzie.config.matchAiTemperature);
  //    return !error;
  //  }
}
