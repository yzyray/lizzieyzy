package featurecat.lizzie.gui;

import featurecat.lizzie.Config;
import featurecat.lizzie.Lizzie;
import featurecat.lizzie.gui.LizzieFrame.HtmlKit;
import java.awt.Color;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.Document;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.StyleSheet;

public class SetKataEngines extends JDialog {
  private JFontTextField txtPDA;
  private JCheckBox chkEditPDA;
  private JCheckBox chkEditThreads;
  private JCheckBox chkAutoLoadThreads;
  private JCheckBox chkAutoLoadPDA;
  private JFontLabel lblWRN;
  private JCheckBox chkEditWRN;
  private JCheckBox chkAutoLoadWRN;
  private JFontTextField txtWRN;
  private JFontLabel lblRPT;
  private JCheckBox chkEditRPT;
  private JCheckBox chkWRNInMenu;
  private JCheckBox chkPDAInMenu;
  private JFontTextField txtRPT;
  private JCheckBox chkAutoRPT;
  private JFontLabel lblHint;
  private JTextField txtThreads;

  public SetKataEngines() {
    // this.setModal(true);
    // setType(Type.POPUP);
    boolean isPdaEngine = Lizzie.leelaz.isKataGoPda;
    setResizable(false);
    setTitle(Lizzie.resourceBundle.getString("SetKataEngines.title")); // ("设置KataGo引擎高级参数");
    setAlwaysOnTop(true);
    setLocationRelativeTo(Lizzie.frame);
    getContentPane().setLayout(null);

    JFontButton btnCancel =
        new JFontButton(Lizzie.resourceBundle.getString("SetKataEngines.btnCancel")); // ("取消");
    btnCancel.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            setVisible(false);
          }
        });
    btnCancel.setBounds(
        Lizzie.config.isFrameFontSmall() ? 376 : (Lizzie.config.isFrameFontMiddle() ? 440 : 540),
        173,
        93,
        25);
    getContentPane().add(btnCancel);

    JFontButton btnApply =
        new JFontButton(Lizzie.resourceBundle.getString("SetKataEngines.btnApply")); // ("确定");
    btnApply.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {

            Lizzie.config.showWRNInMenu = chkWRNInMenu.isSelected();
            Lizzie.config.uiConfig.put("show-wrn-in-menu", Lizzie.config.showWRNInMenu);

            Lizzie.config.showPDAInMenu = chkPDAInMenu.isSelected();
            Lizzie.config.uiConfig.put("show-pda-in-menu", Lizzie.config.showPDAInMenu);

            if (!isPdaEngine) {
              Lizzie.config.chkKataEnginePDA = chkEditPDA.isSelected();
              Lizzie.config.autoLoadKataEnginePDA = chkAutoLoadPDA.isSelected();
              Lizzie.config.txtKataEnginePDA = txtPDA.getText();
              if (Lizzie.config.chkKataEnginePDA) {
                Lizzie.leelaz.setPda(Lizzie.config.txtKataEnginePDA);
                if (Lizzie.config.autoLoadKataEnginePDA) {
                  Lizzie.config.autoLoadTxtKataEnginePDA = Lizzie.config.txtKataEnginePDA;
                  Lizzie.config.uiConfig.put(
                      "auto-load-txt-kata-engine-pda", Lizzie.config.autoLoadTxtKataEnginePDA);
                }
              } else Lizzie.leelaz.setPda("0");
              Lizzie.frame.menu.setUseGfPda(
                  Lizzie.config.chkKataEnginePDA, Lizzie.config.txtKataEnginePDA);
            }

            //  if (!Lizzie.config.showWRNInMenu) {
            Lizzie.config.chkKataEngineWRN = chkEditWRN.isSelected();
            Lizzie.config.autoLoadKataEngineWRN = chkAutoLoadWRN.isSelected();
            Lizzie.config.txtKataEngineWRN = txtWRN.getText();
            if (Lizzie.config.chkKataEngineWRN) {
              Lizzie.leelaz.sendCommand(
                  "kata-set-param analysisWideRootNoise " + Lizzie.config.txtKataEngineWRN);
              Lizzie.board.clearbestmovesafter(Lizzie.board.getHistory().getStart());
              if (Lizzie.config.autoLoadKataEngineWRN) {
                Lizzie.config.autoLoadTxtKataEngineWRN = Lizzie.config.txtKataEngineWRN;
                Lizzie.config.uiConfig.put(
                    "auto-load-txt-kata-engine-wrn", Lizzie.config.autoLoadTxtKataEngineWRN);
              }

            } else Lizzie.leelaz.sendCommand("kata-set-param analysisWideRootNoise 0");
            Lizzie.frame.menu.setUseWrn(
                Lizzie.config.chkKataEngineWRN, Lizzie.config.txtKataEngineWRN);
            //  }

            Lizzie.config.chkKataEngineThreads = chkEditThreads.isSelected();
            Lizzie.config.autoLoadKataEngineThreads = chkAutoLoadThreads.isSelected();
            Lizzie.config.txtKataEngineThreads = txtThreads.getText();
            Lizzie.config.uiConfig.put(
                "txt-kata-engine-threads", Lizzie.config.txtKataEngineThreads);
            Lizzie.config.uiConfig.put(
                "autoload-kata-engine-threads", Lizzie.config.autoLoadKataEngineThreads);

            if (Lizzie.config.chkKataEngineThreads) {
              Lizzie.leelaz.sendCommand(
                  "kata-set-param numSearchThreads " + Lizzie.config.txtKataEngineThreads);
            }
            //            Lizzie.config.chkKataEngineRPT = chkEditRPT.isSelected();
            //            Lizzie.config.autoLoadKataEngineRPT = chkAutoRPT.isSelected();
            //            Lizzie.config.txtKataEngineRPT = txtRPT.getText();

            //            if (Lizzie.config.chkKataEngineRPT)
            //              Lizzie.leelaz.sendCommand(
            //                  "kata-set-param rootPolicyTemperature " +
            // Lizzie.config.txtKataEngineRPT);
            //            else Lizzie.leelaz.sendCommand("kata-set-param rootPolicyTemperature
            // 1.0");
            // Lizzie.config.uiConfig.put("chk-kata-engine-pda", Lizzie.config.chkKataEnginePDA  );
            Lizzie.config.uiConfig.put(
                "autoload-kata-engine-pda", Lizzie.config.autoLoadKataEnginePDA);
            Lizzie.config.uiConfig.put("txt-kata-engine-pda", Lizzie.config.txtKataEnginePDA);

            //  Lizzie.config.uiConfig.put("chk-kata-engine-rpt", Lizzie.config.chkKataEngineRPT  );
            //            Lizzie.config.uiConfig.put(
            //                "autoload-kata-engine-rpt", Lizzie.config.autoLoadKataEngineRPT);
            //            Lizzie.config.uiConfig.put("txt-kata-engine-rpt",
            // Lizzie.config.txtKataEngineRPT);

            //   Lizzie.config.uiConfig.put("chk-kata-engine-wrn", Lizzie.config.chkKataEngineWRN
            // );
            Lizzie.config.uiConfig.put(
                "autoload-kata-engine-wrn", Lizzie.config.autoLoadKataEngineWRN);
            Lizzie.config.uiConfig.put("txt-kata-engine-wrn", Lizzie.config.txtKataEngineWRN);
            setVisible(false);
            Lizzie.frame.menu.updateMenuStatusForEngine();
            if (Lizzie.leelaz.isPondering()) Lizzie.leelaz.ponder();
          }
        });
    btnApply.setBounds(
        Lizzie.config.isFrameFontSmall() ? 278 : (Lizzie.config.isFrameFontMiddle() ? 340 : 440),
        173,
        93,
        25);
    getContentPane().add(btnApply);

    JFontLabel lblPDA =
        new JFontLabel(Lizzie.resourceBundle.getString("SetKataEngines.lblPDA")); // ("激进/保守程度
    // (playoutDoublingAdvantage,默认0.0,有效范围-3.0到3.0):");
    lblPDA.setBounds(10, 24, 700, 25);
    getContentPane().add(lblPDA);

    txtPDA = new JFontTextField();
    txtPDA.setDocument(new DoubleDocument());
    txtPDA.setBounds(
        Lizzie.config.isFrameFontSmall() ? 482 : (Lizzie.config.isFrameFontMiddle() ? 591 : 711),
        24,
        133,
        24);
    getContentPane().add(txtPDA);

    Document dt4 = txtPDA.getDocument();
    dt4.addDocumentListener(
        new DocumentListener() {
          public void insertUpdate(DocumentEvent e) {
            double pda = 0;
            boolean error = false;
            try {
              pda = Double.parseDouble(txtPDA.getText());
            } catch (NumberFormatException s) {
              // TODO Auto-generated catch block
              error = true;
            }
            if (error || pda > 3 || pda < -3) {
              txtPDA.setBackground(Color.RED);
            } else txtPDA.setBackground(Color.WHITE);
          }

          public void removeUpdate(DocumentEvent e) {
            double pda = 0;
            boolean error = false;
            try {
              pda = Double.parseDouble(txtPDA.getText());
            } catch (NumberFormatException s) {
              // TODO Auto-generated catch block
              error = true;
            }
            if (error || pda > 3 || pda < -3) {
              txtPDA.setBackground(Color.RED);
            } else txtPDA.setBackground(Color.WHITE);
          }

          public void changedUpdate(DocumentEvent e) {
            double pda = 0;
            boolean error = false;
            try {
              pda = Double.parseDouble(txtPDA.getText());
            } catch (NumberFormatException s) {
              // TODO Auto-generated catch block
              error = true;
            }
            if (error || pda > 3 || pda < -3) {
              txtPDA.setBackground(Color.RED);
            } else txtPDA.setBackground(Color.WHITE);
          }
        });

    JFontLabel lblAutoLoad =
        new JFontLabel(Lizzie.resourceBundle.getString("SetKataEngines.lblAutoLoad")); // ("自动加载");
    lblAutoLoad.setBounds(
        Lizzie.config.isFrameFontSmall() ? 626 : (Lizzie.config.isFrameFontMiddle() ? 735 : 855),
        3,
        100,
        20);
    getContentPane().add(lblAutoLoad);

    chkEditPDA = new JCheckBox();
    chkEditPDA.setBounds(
        Lizzie.config.isFrameFontSmall() ? 451 : (Lizzie.config.isFrameFontMiddle() ? 560 : 680),
        25,
        25,
        23);
    getContentPane().add(chkEditPDA);

    chkEditPDA.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            // TODO Auto-generated method stub
            if (chkEditPDA.isSelected()) {
              chkAutoLoadPDA.setEnabled(true);
              txtPDA.setEnabled(true);
            } else {
              chkAutoLoadPDA.setSelected(false);
              chkAutoLoadPDA.setEnabled(false);
              txtPDA.setEnabled(false);
            }
          }
        });

    chkAutoLoadPDA = new JCheckBox();
    chkAutoLoadPDA.setBounds(
        Lizzie.config.isFrameFontSmall() ? 640 : (Lizzie.config.isFrameFontMiddle() ? 756 : 885),
        25,
        25,
        23);
    getContentPane().add(chkAutoLoadPDA);

    lblWRN = new JFontLabel(Lizzie.resourceBundle.getString("SetKataEngines.lblWRN")); // ("分析广度拓展
    // (analysisWideRootNoise,默认0.0,有效范围0.0到2.0):");
    lblWRN.setBounds(10, 56, 700, 25);
    getContentPane().add(lblWRN);

    chkEditWRN = new JCheckBox();
    chkEditWRN.setBounds(
        Lizzie.config.isFrameFontSmall() ? 451 : (Lizzie.config.isFrameFontMiddle() ? 560 : 680),
        57,
        25,
        23);
    getContentPane().add(chkEditWRN);
    chkEditWRN.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            // TODO Auto-generated method stub
            if (chkEditWRN.isSelected()) {
              txtWRN.setEnabled(true);
              chkAutoLoadWRN.setEnabled(true);
            } else {
              txtWRN.setEnabled(false);
              chkAutoLoadWRN.setSelected(false);
              chkAutoLoadWRN.setEnabled(false);
            }
          }
        });

    chkAutoLoadWRN = new JCheckBox();
    chkAutoLoadWRN.setBounds(
        Lizzie.config.isFrameFontSmall() ? 640 : (Lizzie.config.isFrameFontMiddle() ? 756 : 885),
        57,
        25,
        23);
    getContentPane().add(chkAutoLoadWRN);

    txtWRN = new JFontTextField();
    txtWRN.setDocument(new DoubleDocument());
    // txtWRN.setColumns(10);
    txtWRN.setBounds(
        Lizzie.config.isFrameFontSmall() ? 482 : (Lizzie.config.isFrameFontMiddle() ? 591 : 711),
        57,
        133,
        24);
    getContentPane().add(txtWRN);

    Document dt3 = txtWRN.getDocument();
    dt3.addDocumentListener(
        new DocumentListener() {
          public void insertUpdate(DocumentEvent e) {
            double wrn = 0;
            boolean error = false;
            try {
              wrn = Double.parseDouble(txtWRN.getText());
            } catch (NumberFormatException s) {
              // TODO Auto-generated catch block
              error = true;
            }
            if (error || wrn > 2 || wrn < 0) {
              txtWRN.setBackground(Color.RED);
            } else txtWRN.setBackground(Color.WHITE);
          }

          public void removeUpdate(DocumentEvent e) {
            double wrn = 0;
            boolean error = false;
            try {
              wrn = Double.parseDouble(txtWRN.getText());
            } catch (NumberFormatException s) {
              // TODO Auto-generated catch block
              error = true;
            }
            if (error || wrn > 2 || wrn < 0) {
              txtWRN.setBackground(Color.RED);
            } else txtWRN.setBackground(Color.WHITE);
          }

          public void changedUpdate(DocumentEvent e) {
            double wrn = 0;
            boolean error = false;
            try {
              wrn = Double.parseDouble(txtWRN.getText());
            } catch (NumberFormatException s) {
              // TODO Auto-generated catch block
              error = true;
            }
            if (error || wrn > 2 || wrn < 0) {
              txtWRN.setBackground(Color.RED);
            } else txtWRN.setBackground(Color.WHITE);
          }
        });

    lblRPT =
        new JFontLabel(
            Lizzie.resourceBundle.getString(
                "SetKataEngines.lblRPT")); // ("根策略温度      (rootPolicyTemperature,默认1.0,
    // 有效范围0.01到100.0):");
    lblRPT.setBounds(10, 88, 700, 25);
    //  getContentPane().add(lblRPT);

    chkEditRPT = new JCheckBox();
    chkEditRPT.setBounds(
        Lizzie.config.isFrameFontSmall() ? 451 : (Lizzie.config.isFrameFontMiddle() ? 560 : 680),
        89,
        25,
        23);
    // getContentPane().add(chkEditRPT);

    chkEditRPT.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            // TODO Auto-generated method stub
            if (chkEditRPT.isSelected()) {
              txtRPT.setEnabled(true);
              chkAutoRPT.setEnabled(true);
            } else {
              chkAutoRPT.setSelected(false);
              txtRPT.setEnabled(false);
              chkAutoRPT.setEnabled(false);
            }
          }
        });

    txtRPT = new JFontTextField();
    txtRPT.setColumns(10);
    txtRPT.setBounds(
        Lizzie.config.isFrameFontSmall() ? 482 : (Lizzie.config.isFrameFontMiddle() ? 591 : 711),
        89,
        133,
        24);
    // getContentPane().add(txtRPT);

    Document dt2 = txtRPT.getDocument();
    dt2.addDocumentListener(
        new DocumentListener() {
          public void insertUpdate(DocumentEvent e) {
            double rpt = 0;
            boolean error = false;
            try {
              rpt = Double.parseDouble(txtRPT.getText());
            } catch (NumberFormatException s) {
              // TODO Auto-generated catch block
              error = true;
            }
            if (error || rpt > 100 || rpt < 0.01) {
              txtRPT.setBackground(Color.RED);
            } else txtRPT.setBackground(Color.WHITE);
          }

          public void removeUpdate(DocumentEvent e) {
            double rpt = 0;
            boolean error = false;
            try {
              rpt = Double.parseDouble(txtRPT.getText());
            } catch (NumberFormatException s) {
              // TODO Auto-generated catch block
              error = true;
            }
            if (error || rpt > 100 || rpt < 0.01) {
              txtRPT.setBackground(Color.RED);
            } else txtRPT.setBackground(Color.WHITE);
          }

          public void changedUpdate(DocumentEvent e) {
            double rpt = 0;
            boolean error = false;
            try {
              rpt = Double.parseDouble(txtRPT.getText());
            } catch (NumberFormatException s) {
              // TODO Auto-generated catch block
              error = true;
            }
            if (error || rpt > 100 || rpt < 0.01) {
              txtRPT.setBackground(Color.RED);
            } else txtRPT.setBackground(Color.WHITE);
          }
        });

    chkAutoRPT = new JCheckBox();
    chkAutoRPT.setBounds(
        Lizzie.config.isFrameFontSmall() ? 640 : (Lizzie.config.isFrameFontMiddle() ? 756 : 885),
        89,
        25,
        23);
    // getContentPane().add(chkAutoRPT);

    lblHint =
        new JFontLabel(
            Lizzie.resourceBundle.getString(
                "SetKataEngines.lblHint")); // ("注:需要至少KataGoV1.4引擎,勾选自动加载则在所有KataGo引擎启动后自动加载勾选的选项");
    lblHint.setBounds(10, 123, 1100, 25);
    getContentPane().add(lblHint);

    LinkLabel lblHint2 = new LinkLabel(Lizzie.resourceBundle.getString("SetKataEngines.Hint2"));
    lblHint2.setBounds(7, 145, 559, 28);
    getContentPane().add(lblHint2);

    setSize(983, 342);
    // Lizzie.setFrameSize(this, 770, 230);
    Lizzie.setFrameSize(
        this,
        Lizzie.config.isFrameFontSmall() ? 790 : (Lizzie.config.isFrameFontMiddle() ? 940 : 1120),
        230);
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

    // chkEditRPT.setSelected(Lizzie.config.chkKataEngineRPT);
    // chkAutoRPT.setSelected(Lizzie.config.autoLoadKataEngineRPT);

    JFontLabel lblShowInMenu =
        new JFontLabel(Lizzie.resourceBundle.getString("SetKataEngines.lblShowInMenu"));
    lblShowInMenu.setBounds(
        (Lizzie.config.isChinese ? 0 : Lizzie.config.isFrameFontSmall() ? 5 : 10)
            + (Lizzie.config.isFrameFontSmall()
                ? 688
                : (Lizzie.config.isFrameFontMiddle() ? 807 : 947)),
        3,
        161,
        20);
    getContentPane().add(lblShowInMenu);

    chkWRNInMenu = new JCheckBox("");
    chkWRNInMenu.setBounds(
        Lizzie.config.isFrameFontSmall() ? 718 : (Lizzie.config.isFrameFontMiddle() ? 854 : 1008),
        57,
        25,
        23);
    getContentPane().add(chkWRNInMenu);
    //    chkWRNInMenu.addActionListener(
    //        new ActionListener() {
    //          public void actionPerformed(ActionEvent e) {
    //            if (Lizzie.frame.isPlayingAgainstLeelaz
    //                || (Lizzie.engineManager.isEngineGame
    //                    && Lizzie.engineManager.engineGameInfo.isGenmove)) return;
    //            if (chkWRNInMenu.isSelected()) {
    //              txtWRN.setEnabled(false);
    //              chkAutoLoadWRN.setEnabled(false);
    //              chkEditWRN.setEnabled(false);
    //            } else {
    //              chkEditWRN.setEnabled(true);
    //              if (chkEditWRN.isSelected()) {
    //                txtWRN.setEnabled(true);
    //                chkAutoLoadWRN.setEnabled(true);
    //              }
    //            }
    //          }
    //        });

    chkPDAInMenu = new JCheckBox("");
    chkPDAInMenu.setBounds(
        Lizzie.config.isFrameFontSmall() ? 718 : (Lizzie.config.isFrameFontMiddle() ? 854 : 1008),
        25,
        25,
        23);
    getContentPane().add(chkPDAInMenu);

    //    chkPDAInMenu.addActionListener(
    //        new ActionListener() {
    //          public void actionPerformed(ActionEvent e) {
    //            if (chkPDAInMenu.isSelected()) {
    //              txtPDA.setEnabled(false);
    //              chkAutoLoadPDA.setEnabled(false);
    //              chkEditPDA.setEnabled(false);
    //            } else {
    //
    //              chkEditPDA.setEnabled(true);
    //              if (chkEditPDA.isSelected()) {
    //                txtPDA.setEnabled(true);
    //                chkAutoLoadPDA.setEnabled(true);
    //              }
    //            }
    //          }
    //        });

    chkEditPDA.setSelected(Lizzie.config.chkKataEnginePDA);
    chkAutoLoadPDA.setSelected(Lizzie.config.autoLoadKataEnginePDA);
    //   if (Lizzie.config.chkKataEnginePDA || Lizzie.config.autoLoadKataEnginePDA)
    txtPDA.setText(Lizzie.config.txtKataEnginePDA);

    JLabel lblNumSearchThreads =
        new JFontLabel(Lizzie.resourceBundle.getString("SetKataEngines.lblNumSearchThreads"));
    lblNumSearchThreads.setBounds(10, 88, 664, 25);
    getContentPane().add(lblNumSearchThreads);

    chkEditThreads = new JCheckBox();
    chkEditThreads.setBounds(
        Lizzie.config.isFrameFontSmall() ? 451 : (Lizzie.config.isFrameFontMiddle() ? 560 : 680),
        87,
        25,
        23);
    getContentPane().add(chkEditThreads);

    chkEditThreads.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            // TODO Auto-generated method stub
            if (chkEditThreads.isSelected()) {
              txtThreads.setEnabled(true);
              chkAutoLoadThreads.setEnabled(true);
            } else {
              chkAutoLoadThreads.setSelected(false);
              txtThreads.setEnabled(false);
              chkAutoLoadThreads.setEnabled(false);
            }
          }
        });

    txtThreads = new JFontTextField();
    txtThreads.setDocument(new IntDocument());
    txtThreads.setBounds(
        Lizzie.config.isFrameFontSmall() ? 482 : (Lizzie.config.isFrameFontMiddle() ? 591 : 711),
        87,
        133,
        24);
    getContentPane().add(txtThreads);

    chkAutoLoadThreads = new JCheckBox();
    chkAutoLoadThreads.setBounds(
        Lizzie.config.isFrameFontSmall() ? 640 : (Lizzie.config.isFrameFontMiddle() ? 756 : 885),
        87,
        25,
        23);
    getContentPane().add(chkAutoLoadThreads);

    if (!Lizzie.config.chkKataEnginePDA) {
      txtPDA.setEnabled(false);
      chkAutoLoadPDA.setEnabled(false);
    }
    if (Lizzie.config.autoLoadKataEnginePDA) {
      txtPDA.setEnabled(true);
      chkAutoLoadPDA.setEnabled(true);
      chkEditPDA.setSelected(true);
    }

    chkEditWRN.setSelected(Lizzie.config.chkKataEngineWRN);
    chkAutoLoadWRN.setSelected(Lizzie.config.autoLoadKataEngineWRN);
    //  if (Lizzie.config.chkKataEngineWRN || Lizzie.config.autoLoadKataEngineWRN)
    txtWRN.setText(Lizzie.config.txtKataEngineWRN);

    if (!Lizzie.config.chkKataEngineWRN) {
      txtWRN.setEnabled(false);
      chkAutoLoadWRN.setEnabled(false);
    }
    if (Lizzie.config.autoLoadKataEngineWRN) {
      txtWRN.setEnabled(true);
      chkAutoLoadWRN.setEnabled(true);
      chkEditWRN.setSelected(true);
    }

    if (Lizzie.config.showWRNInMenu) {
      chkWRNInMenu.setSelected(true);
      // txtWRN.setEnabled(false);
      //   chkAutoLoadWRN.setEnabled(false);
      //   chkEditWRN.setEnabled(false);
    } else {
      chkWRNInMenu.setSelected(false);
    }

    if (Lizzie.config.showPDAInMenu) {
      chkPDAInMenu.setSelected(true);
      //    txtPDA.setEnabled(false);
      //    chkAutoLoadPDA.setEnabled(false);
      //    chkEditPDA.setEnabled(false);
    } else {
      chkPDAInMenu.setSelected(false);
    }

    if (Lizzie.config.chkKataEngineThreads) {
      chkEditThreads.setSelected(true);
      txtThreads.setEnabled(true);
      chkAutoLoadThreads.setEnabled(true);
      txtThreads.setText(Lizzie.config.txtKataEngineThreads);
    } else {
      chkEditThreads.setSelected(false);
      txtThreads.setEnabled(false);
      chkAutoLoadThreads.setEnabled(false);
      chkAutoLoadThreads.setSelected(false);
    }
    if (Lizzie.config.autoLoadKataEngineThreads) {
      chkEditThreads.setSelected(true);
      txtThreads.setEnabled(true);
      chkAutoLoadThreads.setEnabled(true);
      chkAutoLoadThreads.setSelected(true);
      txtThreads.setText(Lizzie.config.txtKataEngineThreads);
    } else chkAutoLoadThreads.setSelected(false);

    //   if (Lizzie.config.chkKataEngineRPT || Lizzie.config.autoLoadKataEngineRPT)
    //  txtRPT.setText(Lizzie.config.txtKataEngineRPT);

    //    if (!Lizzie.config.chkKataEngineRPT) {
    //      txtRPT.setEnabled(false);
    //      chkAutoRPT.setEnabled(false);
    //    }
    //    if (Lizzie.config.autoLoadKataEngineRPT) {
    //      txtRPT.setEnabled(true);
    //      chkAutoRPT.setEnabled(true);
    //      chkEditRPT.setSelected(true);
    //    }
    if (Lizzie.frame.isPlayingAgainstLeelaz
        || (Lizzie.engineManager.isEngineGame && Lizzie.engineManager.engineGameInfo.isGenmove)) {
      txtWRN.setEnabled(false);
      chkEditWRN.setEnabled(false);
    }

    if (isPdaEngine) {
      txtPDA.setVisible(false);
      this.chkAutoLoadPDA.setVisible(false);
      this.chkEditPDA.setVisible(false);
      this.chkPDAInMenu.setVisible(false);

      JFontLabel lblPdaEngineUseMenu =
          new JFontLabel(Lizzie.resourceBundle.getString("SetKataEngines.lblPdaEngineUseMenu"));
      getContentPane().add(lblPdaEngineUseMenu);
      lblPdaEngineUseMenu.setBounds(
          Lizzie.config.isFrameFontSmall() ? 451 : (Lizzie.config.isFrameFontMiddle() ? 560 : 680),
          25,
          400,
          23);
    }
  }

  private class LinkLabel extends JTextPane {
    public LinkLabel(String text) {
      super();

      HTMLDocument htmlDoc;
      HtmlKit htmlKit;
      StyleSheet htmlStyle;

      htmlKit = new HtmlKit();
      htmlDoc = (HTMLDocument) htmlKit.createDefaultDocument();
      htmlStyle = htmlKit.getStyleSheet();
      String style =
          "body {background:"
              + String.format(
                  "%02x%02x%02x",
                  Lizzie.config.commentBackgroundColor.getRed(),
                  Lizzie.config.commentBackgroundColor.getGreen(),
                  Lizzie.config.commentBackgroundColor.getBlue())
              + "; color:#"
              + String.format(
                  "%02x%02x%02x",
                  Lizzie.config.commentFontColor.getRed(),
                  Lizzie.config.commentFontColor.getGreen(),
                  Lizzie.config.commentFontColor.getBlue())
              + "; font-family:"
              + Lizzie.config.fontName
              + ", Consolas, Menlo, Monaco, 'Ubuntu Mono', monospace;"
              + ("font-size:" + Lizzie.config.frameFontSize)
              + "}";
      htmlStyle.addRule(style);
      setFont(new Font(Config.sysDefaultFontName, Font.PLAIN, Config.frameFontSize));
      setEditorKit(htmlKit);
      setDocument(htmlDoc);
      setText(text);
      setEditable(false);
      setOpaque(false);
      putClientProperty(JEditorPane.HONOR_DISPLAY_PROPERTIES, Boolean.TRUE);
      addHyperlinkListener(
          new HyperlinkListener() {
            public void hyperlinkUpdate(HyperlinkEvent e) {
              if (HyperlinkEvent.EventType.ACTIVATED.equals(e.getEventType())) {
                if (Desktop.isDesktopSupported()) {
                  try {
                    Desktop.getDesktop().browse(e.getURL().toURI());
                  } catch (Exception ex) {
                  }
                }
              }
            }
          });
    }
  }
}
