package featurecat.lizzie.gui;

import featurecat.lizzie.Lizzie;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.JCheckBox;
import javax.swing.JDialog;

public class SetLeelaEngines extends JDialog {
  private JFontTextField txtMem;
  JCheckBox chkEditMem;
  JCheckBox chkAutoLoadMem;
  private JFontLabel lblVisits;
  private JCheckBox chkEditVisits;
  private JCheckBox chkAutoLoadVisits;
  private JFontTextField txtVisits;
  private JFontLabel lblLagbuffer;
  private JCheckBox chkEditLagbuffer;
  private JFontTextField txtLagbuffer;
  private JCheckBox chkAutoLoadLagbuffer;
  private JFontLabel lblResign;
  private JCheckBox chkEditResign;
  private JFontTextField txtResign;
  private JCheckBox chkAutoLoadResign;
  private JFontLabel lblHint;

  public SetLeelaEngines() {
    // this.setModal(true);
    // setType(Type.POPUP);
    setResizable(false);
    setTitle(Lizzie.resourceBundle.getString("SetLeelaEngines.title")); // ("设置Leela(Sai)引擎高级参数");
    setAlwaysOnTop(true);
    setLocationRelativeTo(Lizzie.frame);
    getContentPane().setLayout(null);

    JFontButton btnCancel =
        new JFontButton(Lizzie.resourceBundle.getString("SetLeelaEngines.btnCancel")); // ("取消");
    btnCancel.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            setVisible(false);
          }
        });
    btnCancel.setBounds(
        Lizzie.config.isFrameFontSmall() ? 285 : (Lizzie.config.isFrameFontMiddle() ? 370 : 460),
        183,
        93,
        25);
    getContentPane().add(btnCancel);

    JFontButton btnApply =
        new JFontButton(Lizzie.resourceBundle.getString("SetLeelaEngines.btnApply")); // ("确定");
    btnApply.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            Lizzie.config.chkLzsaiEngineMem = chkEditMem.isSelected();
            Lizzie.config.autoLoadLzsaiEngineMem = chkAutoLoadMem.isSelected();
            Lizzie.config.txtLzsaiEngineMem = txtMem.getText();

            Lizzie.config.chkLzsaiEngineVisits = chkEditVisits.isSelected();
            Lizzie.config.autoLoadLzsaiEngineVisits = chkAutoLoadVisits.isSelected();
            Lizzie.config.txtLzsaiEngineVisits = txtVisits.getText();

            Lizzie.config.chkLzsaiEngineLagbuffer = chkEditLagbuffer.isSelected();
            Lizzie.config.autoLoadLzsaiEngineLagbuffer = chkAutoLoadLagbuffer.isSelected();
            Lizzie.config.txtLzsaiEngineLagbuffer = txtLagbuffer.getText();

            Lizzie.config.chkLzsaiEngineResign = chkEditResign.isSelected();
            Lizzie.config.autoLoadLzsaiEngineResign = chkAutoLoadResign.isSelected();
            Lizzie.config.txtLzsaiEngineResign = txtResign.getText();

            if (Lizzie.config.chkLzsaiEngineMem)
              Lizzie.leelaz.sendCommand(
                  "lz-setoption name Maximum Memory Use (MiB) value "
                      + Lizzie.config.txtLzsaiEngineMem);

            if (Lizzie.config.chkLzsaiEngineVisits)
              Lizzie.leelaz.sendCommand(
                  "lz-setoption name Visits value " + Lizzie.config.txtLzsaiEngineVisits);

            if (Lizzie.config.chkLzsaiEngineLagbuffer)
              Lizzie.leelaz.sendCommand(
                  "lz-setoption name Lagbuffer value " + Lizzie.config.txtLzsaiEngineLagbuffer);

            if (Lizzie.config.chkLzsaiEngineResign)
              Lizzie.leelaz.sendCommand(
                  "lz-setoption name Resign Percentage value "
                      + Lizzie.config.txtLzsaiEngineResign);

            Lizzie.config.uiConfig.put("chk-lzsai-enginemem", Lizzie.config.chkLzsaiEngineMem);
            Lizzie.config.uiConfig.put(
                "autoload-Lzsai-enginemem", Lizzie.config.autoLoadLzsaiEngineMem);
            Lizzie.config.uiConfig.put("txt-lzsai-enginemem", Lizzie.config.txtLzsaiEngineMem);

            Lizzie.config.uiConfig.put(
                "chk-lzsai-enginevisits", Lizzie.config.chkLzsaiEngineVisits);
            Lizzie.config.uiConfig.put(
                "autoload-Lzsai-enginevisits", Lizzie.config.autoLoadLzsaiEngineVisits);
            Lizzie.config.uiConfig.put(
                "txt-lzsai-enginevisits", Lizzie.config.txtLzsaiEngineVisits);

            Lizzie.config.uiConfig.put(
                "chk-lzsai-enginelagbuffer", Lizzie.config.chkLzsaiEngineLagbuffer);
            Lizzie.config.uiConfig.put(
                "autoload-Lzsai-enginelagbuffer", Lizzie.config.autoLoadLzsaiEngineLagbuffer);
            Lizzie.config.uiConfig.put(
                "txt-lzsai-enginelagbuffer", Lizzie.config.txtLzsaiEngineLagbuffer);

            Lizzie.config.uiConfig.put(
                "chk-lzsai-engineresign", Lizzie.config.chkLzsaiEngineResign);
            Lizzie.config.uiConfig.put(
                "autoload-Lzsai-engineresign", Lizzie.config.autoLoadLzsaiEngineResign);
            Lizzie.config.uiConfig.put(
                "txt-lzsai-engineresign", Lizzie.config.txtLzsaiEngineResign);
            setVisible(false);
            if (Lizzie.leelaz.isPondering()) Lizzie.leelaz.ponder();
          }
        });
    btnApply.setBounds(
        Lizzie.config.isFrameFontSmall() ? 185 : (Lizzie.config.isFrameFontMiddle() ? 270 : 360),
        183,
        93,
        25);
    getContentPane().add(btnApply);

    JFontLabel lblRam =
        new JFontLabel(
            Lizzie.resourceBundle.getString(
                "SetLeelaEngines.lblRam")); // ("最大允许使用的内存(MiB)(默认2048 最小128 最大 131072):");
    lblRam.setBounds(10, 25, 704, 25);
    getContentPane().add(lblRam);

    txtMem = new JFontTextField();
    txtMem.setBounds(
        Lizzie.config.isFrameFontSmall() ? 371 : (Lizzie.config.isFrameFontMiddle() ? 481 : 591),
        26,
        133,
        24);
    getContentPane().add(txtMem);
    txtMem.setColumns(10);

    JFontLabel lblAutoLoad =
        new JFontLabel(Lizzie.resourceBundle.getString("SetLeelaEngines.lblAutoLoad")); // ("自动加载");
    lblAutoLoad.setBounds(
        Lizzie.config.isFrameFontSmall() ? 515 : (Lizzie.config.isFrameFontMiddle() ? 625 : 735),
        1,
        100,
        25);
    getContentPane().add(lblAutoLoad);

    chkEditMem = new JCheckBox();
    chkEditMem.setBounds(
        Lizzie.config.isFrameFontSmall() ? 340 : (Lizzie.config.isFrameFontMiddle() ? 450 : 560),
        26,
        25,
        23);
    getContentPane().add(chkEditMem);

    chkEditMem.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            // TODO Auto-generated method stub
            if (chkEditMem.isSelected()) {
              chkAutoLoadMem.setEnabled(true);
              txtMem.setEditable(true);
            } else {
              chkAutoLoadMem.setEnabled(false);
              txtMem.setEditable(false);
            }
          }
        });

    chkAutoLoadMem = new JCheckBox();
    chkAutoLoadMem.setBounds(
        Lizzie.config.isFrameFontSmall() ? 525 : (Lizzie.config.isFrameFontMiddle() ? 644 : 763),
        26,
        25,
        23);
    getContentPane().add(chkAutoLoadMem);

    lblVisits =
        new JFontLabel(
            Lizzie.resourceBundle.getString(
                "SetLeelaEngines.lblVisits")); // ("最大局面计算量(Visits)(最小0 最大1000000000 0为无限制):");
    lblVisits.setBounds(10, 55, 704, 25);
    getContentPane().add(lblVisits);

    chkEditVisits = new JCheckBox();
    chkEditVisits.setBounds(
        Lizzie.config.isFrameFontSmall() ? 340 : (Lizzie.config.isFrameFontMiddle() ? 450 : 560),
        56,
        25,
        23);
    getContentPane().add(chkEditVisits);
    chkEditVisits.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            // TODO Auto-generated method stub
            if (chkEditVisits.isSelected()) {
              txtVisits.setEditable(true);
              chkAutoLoadVisits.setEnabled(true);
            } else {
              txtVisits.setEditable(false);
              chkAutoLoadVisits.setEnabled(false);
            }
          }
        });

    chkAutoLoadVisits = new JCheckBox();
    chkAutoLoadVisits.setBounds(
        Lizzie.config.isFrameFontSmall() ? 525 : (Lizzie.config.isFrameFontMiddle() ? 644 : 763),
        56,
        25,
        23);
    getContentPane().add(chkAutoLoadVisits);

    txtVisits = new JFontTextField();
    txtVisits.setColumns(10);
    txtVisits.setBounds(
        Lizzie.config.isFrameFontSmall() ? 371 : (Lizzie.config.isFrameFontMiddle() ? 481 : 591),
        56,
        133,
        24);
    getContentPane().add(txtVisits);

    lblLagbuffer =
        new JFontLabel(
            Lizzie.resourceBundle.getString(
                "SetLeelaEngines.lblLagbuffer")); // ("GTP延迟时间(Lagbuffer)(默认1 最小0 最大3000):");
    lblLagbuffer.setBounds(10, 85, 704, 25);
    getContentPane().add(lblLagbuffer);

    chkEditLagbuffer = new JCheckBox();
    chkEditLagbuffer.setBounds(
        Lizzie.config.isFrameFontSmall() ? 340 : (Lizzie.config.isFrameFontMiddle() ? 450 : 560),
        86,
        25,
        23);
    getContentPane().add(chkEditLagbuffer);

    chkEditLagbuffer.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            // TODO Auto-generated method stub
            if (chkEditLagbuffer.isSelected()) {
              txtLagbuffer.setEditable(true);
              chkAutoLoadLagbuffer.setEnabled(true);
            } else {
              txtLagbuffer.setEditable(false);
              chkAutoLoadLagbuffer.setEnabled(false);
            }
          }
        });

    txtLagbuffer = new JFontTextField();
    txtLagbuffer.setColumns(10);
    txtLagbuffer.setBounds(
        Lizzie.config.isFrameFontSmall() ? 371 : (Lizzie.config.isFrameFontMiddle() ? 481 : 591),
        86,
        133,
        24);
    getContentPane().add(txtLagbuffer);

    chkAutoLoadLagbuffer = new JCheckBox();
    chkAutoLoadLagbuffer.setBounds(
        Lizzie.config.isFrameFontSmall() ? 525 : (Lizzie.config.isFrameFontMiddle() ? 644 : 763),
        86,
        25,
        23);
    getContentPane().add(chkAutoLoadLagbuffer);

    lblResign =
        new JFontLabel(
            Lizzie.resourceBundle.getString(
                "SetLeelaEngines.lblResign")); // ("认输胜率(默认-1 最小-1 最大30):");
    lblResign.setBounds(10, 115, 704, 25);
    getContentPane().add(lblResign);

    chkEditResign = new JCheckBox();
    chkEditResign.setBounds(
        Lizzie.config.isFrameFontSmall() ? 340 : (Lizzie.config.isFrameFontMiddle() ? 450 : 560),
        116,
        25,
        23);
    getContentPane().add(chkEditResign);
    chkEditResign.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            // TODO Auto-generated method stub
            if (chkEditResign.isSelected()) {
              txtResign.setEditable(true);
              chkAutoLoadResign.setEnabled(true);
            } else {
              txtResign.setEditable(false);
              chkAutoLoadResign.setEnabled(false);
            }
          }
        });

    txtResign = new JFontTextField();
    txtResign.setColumns(10);
    txtResign.setBounds(
        Lizzie.config.isFrameFontSmall() ? 371 : (Lizzie.config.isFrameFontMiddle() ? 481 : 591),
        116,
        133,
        24);
    getContentPane().add(txtResign);

    chkAutoLoadResign = new JCheckBox();
    chkAutoLoadResign.setBounds(
        Lizzie.config.isFrameFontSmall() ? 525 : (Lizzie.config.isFrameFontMiddle() ? 644 : 763),
        116,
        25,
        23);
    getContentPane().add(chkAutoLoadResign);

    lblHint =
        new JFontLabel(
            Lizzie.resourceBundle.getString(
                "SetLeelaEngines.lblHint")); // ("注:只有Leela或Sai引擎支持以上选项,勾选自动加载则在所有Leela和Sai引擎启动后自动加载勾选的选项");
    lblHint.setBounds(10, 145, 1210, 25);
    getContentPane().add(lblHint);
    //  if (Lizzie.config.autoLoadKataRules) {}
    //  setSize(1280, 254);
    Lizzie.setFrameSize(
        this,
        Lizzie.config.isFrameFontSmall() ? 580 : (Lizzie.config.isFrameFontMiddle() ? 750 : 930),
        244);
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

    chkEditMem.setSelected(Lizzie.config.chkLzsaiEngineMem);
    chkAutoLoadMem.setSelected(Lizzie.config.autoLoadLzsaiEngineMem);
    txtMem.setText(Lizzie.config.txtLzsaiEngineMem);
    if (!Lizzie.config.chkLzsaiEngineMem) {
      txtMem.setEditable(false);
      chkAutoLoadMem.setEnabled(false);
    }

    chkEditVisits.setSelected(Lizzie.config.chkLzsaiEngineVisits);
    chkAutoLoadVisits.setSelected(Lizzie.config.autoLoadLzsaiEngineVisits);
    txtVisits.setText(Lizzie.config.txtLzsaiEngineVisits);
    if (!Lizzie.config.chkLzsaiEngineVisits) {
      txtVisits.setEditable(false);
      chkAutoLoadVisits.setEnabled(false);
    }

    chkEditLagbuffer.setSelected(Lizzie.config.chkLzsaiEngineLagbuffer);
    chkAutoLoadLagbuffer.setSelected(Lizzie.config.autoLoadLzsaiEngineLagbuffer);
    txtLagbuffer.setText(Lizzie.config.txtLzsaiEngineLagbuffer);
    if (!Lizzie.config.chkLzsaiEngineLagbuffer) {
      txtLagbuffer.setEditable(false);
      chkAutoLoadLagbuffer.setEnabled(false);
    }

    chkEditResign.setSelected(Lizzie.config.chkLzsaiEngineResign);
    chkAutoLoadResign.setSelected(Lizzie.config.autoLoadLzsaiEngineResign);
    txtResign.setText(Lizzie.config.txtLzsaiEngineResign);
    if (!Lizzie.config.chkLzsaiEngineResign) {
      txtResign.setEditable(false);
      chkAutoLoadResign.setEnabled(false);
    }
  }
}
