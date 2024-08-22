package featurecat.lizzie.gui;

import featurecat.lizzie.Config;
import featurecat.lizzie.Lizzie;
import featurecat.lizzie.analysis.EngineManager;
import featurecat.lizzie.analysis.Leelaz;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;
import javax.imageio.ImageIO;
import javax.swing.ButtonGroup;
import javax.swing.JDialog;
import javax.swing.JLabel;

public class SetKataPDA extends JDialog {
  private final ResourceBundle resourceBundle = Lizzie.resourceBundle;
  private JFontTextField txtStaticCur;
  JFontCheckBox chkDymPda;
  JFontCheckBox chkStaticPda;
  JFontCheckBox chkNoPDA;
  JFontCheckBox chkAutoPDA;
  public JLabel curPDA;
  public JFontTextField txtDymCap;

  public SetKataPDA() {
    // this.setModal(true);
    // setType(Type.POPUP);
    setResizable(false);
    setTitle(
        resourceBundle.getString("SetKataPDA.title")); // ("KataGo 激进参数PDA设置(仅支持KataGo PDA引擎)");
    setAlwaysOnTop(true);
    setLocationRelativeTo(Lizzie.frame);
    getContentPane().setLayout(null);

    //    this.addWindowListener(
    //        new WindowAdapter() {
    //          public void windowClosing(WindowEvent e) {
    //
    //            if (Lizzie.leelaz.isPondering()) Lizzie.leelaz.ponder();
    //            setVisible(false);
    //          }
    //        });

    JFontButton btnCancel =
        new JFontButton(resourceBundle.getString("SetKataPDA.btnCancel")); // ("取消");
    btnCancel.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            setVisible(false);
          }
        });
    btnCancel.setBounds(
        Lizzie.config.isFrameFontSmall() ? 255 : (Lizzie.config.isFrameFontMiddle() ? 320 : 380),
        Lizzie.config.isChinese ? 205 : 153,
        80,
        25);
    getContentPane().add(btnCancel);

    JFontButton btnApply =
        new JFontButton(resourceBundle.getString("SetKataPDA.btnApply")); // ("确定");
    btnApply.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            Leelaz engine;
            if (EngineManager.isEngineGame) {
              if (Lizzie.engineManager.engineList.get(EngineManager.engineGameInfo.firstEngineIndex)
                  .isKataGoPda)
                engine =
                    Lizzie.engineManager.engineList.get(
                        EngineManager.engineGameInfo.firstEngineIndex);
              else
                engine =
                    Lizzie.engineManager.engineList.get(
                        EngineManager.engineGameInfo.secondEngineIndex);
            } else engine = Lizzie.leelaz;
            if (chkDymPda.isSelected()) {
              double dymCap;
              try {
                dymCap = Double.parseDouble(txtDymCap.getText().trim());
              } catch (Exception es) {
                Message msg = new Message();
                msg.setMessage(
                    resourceBundle.getString("SetKataPDA.wrongParameter")); // ("输入参数有误 ");
                msg.setVisible(true);
                return;
              }

              if (dymCap == 0) {
                Message msg = new Message();
                msg.setMessage(
                    resourceBundle.getString("SetKataPDA.wrongParameter")); // ("输入参数有误 ");
                msg.setVisible(true);
                return;
              }
              engine.pda = 0;
              engine.sendCommand("pda 0");
              LizzieFrame.menu.txtPDA.setText("0.000");
              engine.sendCommand("dympdacap " + dymCap);
              if (chkAutoPDA.isSelected()) Lizzie.config.AutoPDA = "dympdacap " + dymCap;
              engine.pdaCap = dymCap;
              engine.isStaticPda = false;

            } else if (chkStaticPda.isSelected()) {
              double staticCur;
              try {
                staticCur = Double.parseDouble(txtStaticCur.getText().trim());

              } catch (Exception es) {
                Message msg = new Message();
                msg.setMessage(
                    resourceBundle.getString("SetKataPDA.wrongParameter")); // ("输入参数有误 ");
                msg.setVisible(true);
                return;
              }
              engine.sendCommand("pda " + staticCur);
              if (chkAutoPDA.isSelected()) {
                Lizzie.config.AutoPDA = "pda " + staticCur;
              }
              engine.pda = staticCur;
              engine.isStaticPda = true;
              LizzieFrame.menu.txtPDA.setText(String.valueOf(staticCur));
            }
            if (chkNoPDA.isSelected()) {
              engine.sendCommand("pda 0");
              engine.pda = 0;
              if (Lizzie.config.isDoubleEngineMode()) Lizzie.leelaz2.pda = 0;
              Lizzie.config.AutoPDA = "pda 0";
              LizzieFrame.menu.txtPDA.setText("0");
              engine.isStaticPda = true;
            }

            Lizzie.config.chkDymPDA = chkDymPda.isSelected();
            Lizzie.config.chkStaticPDA = chkStaticPda.isSelected();
            Lizzie.config.chkAutoPDA = chkAutoPDA.isSelected();
            Lizzie.config.dymPDACap = txtDymCap.getText();
            Lizzie.config.staticPDAcur = txtStaticCur.getText();

            Lizzie.config.uiConfig.put("chk-dym-pda", Lizzie.config.chkDymPDA);
            Lizzie.config.uiConfig.put("chk-static-pda", Lizzie.config.chkStaticPDA);
            Lizzie.config.uiConfig.put("chk-auto-pda", Lizzie.config.chkAutoPDA);
            Lizzie.config.uiConfig.put("dym-pda-cap", Lizzie.config.dymPDACap);
            Lizzie.config.uiConfig.put("static-pda-cur", Lizzie.config.staticPDAcur);
            Lizzie.config.uiConfig.put("auto-pda", Lizzie.config.AutoPDA);
            if (Lizzie.leelaz.isPondering()) Lizzie.leelaz.ponder();
            setVisible(false);
          }
        });
    btnApply.setBounds(
        Lizzie.config.isFrameFontSmall() ? 167 : (Lizzie.config.isFrameFontMiddle() ? 225 : 290),
        Lizzie.config.isChinese ? 205 : 153,
        80,
        25);
    getContentPane().add(btnApply);

    chkDymPda = new JFontCheckBox(resourceBundle.getString("SetKataPDA.chkDymPda")); // ("动态PDA");
    chkDymPda.setBounds(
        6,
        45,
        Lizzie.config.isFrameFontSmall() ? 75 : (Lizzie.config.isFrameFontMiddle() ? 95 : 115),
        23);
    getContentPane().add(chkDymPda);

    chkStaticPda =
        new JFontCheckBox(resourceBundle.getString("SetKataPDA.chkStaticPda")); // ("固定PDA");
    chkStaticPda.setBounds(
        Lizzie.config.isFrameFontSmall() ? 243 : (Lizzie.config.isFrameFontMiddle() ? 287 : 330),
        45,
        Lizzie.config.isFrameFontSmall() ? 75 : (Lizzie.config.isFrameFontMiddle() ? 95 : 115),
        23);
    getContentPane().add(chkStaticPda);

    chkDymPda.setFocusable(false);
    chkStaticPda.setFocusable(false);

    txtStaticCur = new JFontTextField();
    txtStaticCur.setBounds(
        Lizzie.config.isFrameFontSmall() ? 323 : (Lizzie.config.isFrameFontMiddle() ? 390 : 445),
        44,
        66,
        24);
    getContentPane().add(txtStaticCur);
    txtStaticCur.setColumns(10);

    txtStaticCur.setEnabled(false);

    JFontLabel lblTip =
        new JFontLabel(
            resourceBundle.getString(
                "SetKataPDA.lblTip")); // "注:动态变化系数修改后,会自动按让子数计算PDA,并设为当前值,不推荐序盘以后修改");
    lblTip.setBounds(10, 72, 831, 23);
    getContentPane().add(lblTip);

    JLabel lblCurPDA = new JLabel(resourceBundle.getString("SetKataPDA.lblCurPDA")); // ("当前PDA:");
    lblCurPDA.setFont(new Font("Song", Font.PLAIN, Math.max(Config.frameFontSize, 14)));
    lblCurPDA.setBounds(10, 10, 210, 24);
    getContentPane().add(lblCurPDA);

    curPDA = new JLabel("0.0");
    curPDA.setFont(new Font("Song", Font.PLAIN, Math.max(Config.frameFontSize, 15)));
    curPDA.setBounds(
        Lizzie.config.isFrameFontSmall() ? 132 : (Lizzie.config.isFrameFontMiddle() ? 145 : 175),
        13,
        60,
        20);
    getContentPane().add(curPDA);
    curPDA.setText(String.format(Locale.ENGLISH, "%.3f", Lizzie.leelaz.pda));

    chkAutoPDA =
        new JFontCheckBox(
            resourceBundle.getString(
                "SetKataPDA.chkAutoPDA")); // ("自动加载此PDA设置(每次对局开始时,包括人机对局和引擎对局)");
    chkAutoPDA.setBounds(6, 96, 812, 23);
    getContentPane().add(chkAutoPDA);

    chkDymPda.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            txtDymCap.setEnabled(true);
            txtStaticCur.setEnabled(false);
          }
        });
    chkStaticPda.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            txtDymCap.setEnabled(false);
            txtStaticCur.setEnabled(true);
          }
        });

    chkAutoPDA.setFocusable(false);

    chkNoPDA = new JFontCheckBox(resourceBundle.getString("SetKataPDA.chkNoPDA")); // ("不启用PDA");
    chkNoPDA.setFocusable(false);
    chkNoPDA.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            txtStaticCur.setEnabled(false);
            txtDymCap.setEnabled(false);
          }
        });

    ButtonGroup bg = new ButtonGroup();
    bg.add(chkDymPda);
    bg.add(chkStaticPda);
    bg.add(chkNoPDA);

    chkNoPDA.setBounds(
        Lizzie.config.isFrameFontSmall() ? 406 : (Lizzie.config.isFrameFontMiddle() ? 480 : 540),
        45,
        171,
        23);

    getContentPane().add(chkNoPDA);

    JFontLabel lblDymRatio =
        new JFontLabel(resourceBundle.getString("SetKataPDA.lblDymRatio")); // ("动态PDA系数:");
    lblDymRatio.setBounds(
        Lizzie.config.isFrameFontSmall() ? 87 : (Lizzie.config.isFrameFontMiddle() ? 107 : 127),
        46,
        130,
        20);
    getContentPane().add(lblDymRatio);

    txtDymCap = new JFontTextField();
    txtDymCap.setBounds(
        Lizzie.config.isFrameFontSmall() ? 154 : (Lizzie.config.isFrameFontMiddle() ? 190 : 230),
        44,
        66,
        24);
    getContentPane().add(txtDymCap);
    txtDymCap.setEnabled(false);

    JFontLabel lblpdakatagopdapda =
        new JFontLabel(
            resourceBundle.getString(
                "SetKataPDA.lblpdakatagopdapda")); // ("注:PDA为KataGo控制行棋激进/消极程度的参数,分先一般不需开启,让子需要激进(PDA为正)");
    lblpdakatagopdapda.setForeground(Color.RED);
    lblpdakatagopdapda.setBounds(10, 125, 835, 23);
    getContentPane().add(lblpdakatagopdapda);

    JFontLabel lblpdapda = new JFontLabel("被让子需要消极(PDA为负,如默认为白视角,则黑方需设置PDA为正才是消极)");
    lblpdapda.setForeground(Color.RED);
    lblpdapda.setBounds(6, 150, 872, 23);
    if (Lizzie.config.isChinese) getContentPane().add(lblpdapda);

    if (!Lizzie.leelaz.isStaticPda) {
      chkDymPda.setSelected(true);
    } else if (Lizzie.leelaz.pda == 0) {
      chkNoPDA.setSelected(true);
    } else {
      chkStaticPda.setSelected(true);
    }

    chkAutoPDA.setSelected(Lizzie.config.chkAutoPDA);
    // if(!Lizzie.config.dymPDACap.equals(""))
    // txtDymCap.setText(Lizzie.config.dymPDACap);
    // else
    txtDymCap.setText(String.valueOf(Lizzie.leelaz.pdaCap));
    txtStaticCur.setText(String.valueOf(Lizzie.leelaz.pda));

    JFontLabel label = new JFontLabel("一般默认为白方视角,也可修改配置文件改为黑方视角,或无视角(轮谁下是谁的视角)");
    label.setForeground(Color.RED);
    label.setBounds(6, 174, 872, 23);
    if (Lizzie.config.isChinese) getContentPane().add(label);
    if (chkDymPda.isSelected()) {
      txtDymCap.setEnabled(true);
      txtStaticCur.setEnabled(false);
    } else if (chkStaticPda.isSelected()) {
      txtDymCap.setEnabled(false);
      txtStaticCur.setEnabled(true);
    } else {
      chkNoPDA.setSelected(true);
      txtDymCap.setEnabled(false);
      txtStaticCur.setEnabled(false);
    }

    // setSize(957, 467);
    Lizzie.setFrameSize(
        this,
        Lizzie.config.isFrameFontSmall() ? 517 : (Lizzie.config.isFrameFontMiddle() ? 680 : 840),
        Lizzie.config.isChinese ? 267 : 217);
    try {
      this.setIconImage(ImageIO.read(MoreEngines.class.getResourceAsStream("/assets/logo.png")));
      Dimension screensize = Toolkit.getDefaultToolkit().getScreenSize();
      int x = (int) screensize.getWidth() / 2 - this.getWidth() / 2;
      int y = (int) screensize.getHeight() / 2 - this.getHeight() / 2;
      setLocation(x, y);
    } catch (IOException e1) {
      // TODO Auto-generated catch block
      e1.printStackTrace();
    }
  }
}
