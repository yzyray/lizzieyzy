package featurecat.lizzie.gui;

import featurecat.lizzie.Config;
import featurecat.lizzie.Lizzie;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ResourceBundle;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JRadioButton;
import javax.swing.JTextArea;

public class EnginePkConfig extends JDialog {
  private final ResourceBundle resourceBundle = Lizzie.resourceBundle;
  JFontTextField txtresignSettingBlackMinMove;
  JFontTextField txtresignSettingBlack;
  JFontTextField txtresignSettingBlack2;
  JFontTextField txtresignSettingWhiteMinMove;
  JFontTextField txtresignSettingWhite;
  JFontTextField txtresignSettingWhite2;
  JFontTextField txtnameSetting;
  JFontTextField txtGameMAX;
  // JFontCheckBox chkGenmove;
  JRadioButton rdoGenmove;
  JRadioButton rdoAna;
  JRadioButton rdoCurrentMove;
  JRadioButton rdoLastMove;

  JFontCheckBox chkPreviousBestmovesOnlyFirstMove;
  JFontCheckBox chkAutosave;
  JFontCheckBox chkExchange;
  JFontCheckBox chkGameMAX;
  JFontCheckBox chkRandomMove;
  JFontCheckBox chkRandomMoveVists;
  JFontCheckBox chkSaveWinrate;
  JFontCheckBox chkSatartNum;

  JFontTextField txtRandomMove;
  JFontTextField txtRandomDiffWinrate;
  JFontTextField txtRandomMoveVists;
  private JFontCheckBox chkPkPonder;
  private JFontTextField txtStartNum;

  public EnginePkConfig(boolean formToolbar) {
    // setType(Type.POPUP);
    setModal(true);
    setTitle(resourceBundle.getString("EnginePkConfig.title")); // ("引擎对局设置");
    //  setBounds(0, 0, 530, 293);
    setSize(815, 480);
    Lizzie.setFrameSize(
        this,
        Lizzie.config.isFrameFontSmall() ? 515 : (Lizzie.config.isFrameFontMiddle() ? 620 : 750),
        Lizzie.config.isFrameFontSmall()
            ? (formToolbar ? 335 : 305)
            : (Lizzie.config.isFrameFontMiddle() ? 325 : 350));
    setResizable(false);
    setAlwaysOnTop(Lizzie.frame.isAlwaysOnTop());
    getContentPane().setLayout(null);
    setLocationRelativeTo(getOwner());
    JFontLabel lblresignSettingBlack =
        new JFontLabel(
            resourceBundle.getString("EnginePkConfig.lblresignSettingBlack")); // ("认输阈值:连续");
    JFontLabel lblresignSettingBlack2 =
        new JFontLabel(resourceBundle.getString("EnginePkConfig.lblresignSetting2")); // ("手胜率低于");
    JFontLabel lblresignSettingBlack3 =
        new JFontLabel(resourceBundle.getString("EnginePkConfig.lblresignSetting3"));
    JFontLabel lblresignSettingBlack4 = new JFontLabel("%");

    txtresignSettingBlack = new JFontTextField();
    txtresignSettingBlack.setDocument(new IntDocument());
    txtresignSettingBlack2 = new JFontTextField();
    txtresignSettingBlack2.setDocument(new DoubleDocument());
    txtresignSettingBlackMinMove = new JFontTextField();
    txtresignSettingBlackMinMove.setDocument(new IntDocument());

    lblresignSettingBlack3.setBounds(308, 92, 82, 25);
    lblresignSettingBlack.setBounds(10, 92, 197, 25);
    lblresignSettingBlack2.setBounds(230, 92, 57, 25);
    lblresignSettingBlack4.setBounds(431, 92, 15, 25);
    txtresignSettingBlackMinMove.setBounds(197, 96, 30, 18);
    txtresignSettingBlack.setBounds(282, 96, 20, 18);
    txtresignSettingBlack2.setBounds(389, 96, 35, 18);

    JFontLabel lblresignSettingWhite =
        new JFontLabel(resourceBundle.getString("EnginePkConfig.lblresignSettingWhite"));
    JFontLabel lblresignSettingWhite2 =
        new JFontLabel(resourceBundle.getString("EnginePkConfig.lblresignSetting2"));
    JFontLabel lblresignSettingWhite3 =
        new JFontLabel(resourceBundle.getString("EnginePkConfig.lblresignSetting3"));
    JFontLabel lblresignSettingWhite4 = new JFontLabel("%");

    txtresignSettingWhite = new JFontTextField();
    txtresignSettingWhite.setDocument(new IntDocument());
    txtresignSettingWhite2 = new JFontTextField();
    txtresignSettingWhite2.setDocument(new DoubleDocument());
    txtresignSettingWhiteMinMove = new JFontTextField();
    txtresignSettingWhiteMinMove.setDocument(new IntDocument());

    lblresignSettingWhite.setBounds(10, 111, 197, 25);
    lblresignSettingWhite3.setBounds(308, 111, 82, 25);
    lblresignSettingWhite4.setBounds(431, 111, 15, 25);
    lblresignSettingWhite2.setBounds(230, 111, 57, 25);

    txtresignSettingWhiteMinMove.setBounds(197, 115, 30, 18);
    txtresignSettingWhite.setBounds(282, 115, 20, 18);
    txtresignSettingWhite2.setBounds(389, 115, 35, 18);

    if (formToolbar) {
      getContentPane().add(lblresignSettingBlack);
      getContentPane().add(lblresignSettingBlack2);
      getContentPane().add(txtresignSettingBlack);
      getContentPane().add(txtresignSettingBlack2);
      getContentPane().add(lblresignSettingBlack4);
      getContentPane().add(lblresignSettingBlack3);
      getContentPane().add(txtresignSettingBlackMinMove);
      getContentPane().add(txtresignSettingWhiteMinMove);
      getContentPane().add(txtresignSettingWhite);
      getContentPane().add(txtresignSettingWhite2);
      getContentPane().add(lblresignSettingWhite);
      getContentPane().add(lblresignSettingWhite2);
      getContentPane().add(lblresignSettingWhite3);
      getContentPane().add(lblresignSettingWhite4);
    }

    txtresignSettingBlack.setText(Lizzie.config.firstEngineResignMoveCounts + "");
    txtresignSettingBlack2.setText(Lizzie.config.firstEngineResignWinrate + "");
    txtresignSettingBlackMinMove.setText(Lizzie.config.firstEngineMinMove + "");

    txtresignSettingWhite.setText(Lizzie.config.secondEngineResignMoveCounts + "");
    txtresignSettingWhite2.setText(Lizzie.config.secondEngineResignWinrate + "");
    txtresignSettingWhiteMinMove.setText(Lizzie.config.secondEngineMinMove + "");

    chkExchange =
        new JFontCheckBox(resourceBundle.getString("EnginePkConfig.chkExchange")); // ("交换黑白");
    getContentPane().add(chkExchange);
    chkExchange.setBounds(7, 28, 145, 18);

    chkRandomMove =
        new JFontCheckBox(resourceBundle.getString("EnginePkConfig.chkRandomMove")); // ("随机落子:前");
    getContentPane().add(chkRandomMove);
    chkRandomMove.setBounds(
        6,
        53,
        Lizzie.config.isFrameFontSmall() ? 98 : (Lizzie.config.isFrameFontMiddle() ? 110 : 140),
        20);

    txtRandomMove = new JFontTextField();
    txtRandomMove.setDocument(new IntDocument());
    getContentPane().add(txtRandomMove);
    txtRandomMove.setBounds(
        Lizzie.config.isFrameFontSmall() ? 104 : (Lizzie.config.isFrameFontMiddle() ? 120 : 150),
        Lizzie.config.isFrameFontSmall() ? 55 : (Lizzie.config.isFrameFontMiddle() ? 54 : 53),
        Lizzie.config.isFrameFontSmall() ? 30 : (Lizzie.config.isFrameFontMiddle() ? 35 : 40),
        Lizzie.config.isFrameFontSmall() ? 18 : (Lizzie.config.isFrameFontMiddle() ? 20 : 22));

    JFontLabel lblRandomWinrate =
        new JFontLabel(
            resourceBundle.getString("EnginePkConfig.lblRandomWinrate")); // ("手,胜率不低于首位");
    getContentPane().add(lblRandomWinrate);
    lblRandomWinrate.setBounds(
        Lizzie.config.isFrameFontSmall() ? 142 : (Lizzie.config.isFrameFontMiddle() ? 165 : 193),
        53,
        193,
        20);

    chkRandomMoveVists =
        new JFontCheckBox(
            resourceBundle.getString("EnginePkConfig.chkRandomMoveVists")); // ("且计算量不低于最高值");
    chkRandomMoveVists.setBounds(
        Lizzie.config.isFrameFontSmall() ? 299 : (Lizzie.config.isFrameFontMiddle() ? 352 : 425),
        51,
        Lizzie.config.isFrameFontSmall() ? 145 : (Lizzie.config.isFrameFontMiddle() ? 185 : 230),
        24);
    getContentPane().add(chkRandomMoveVists);

    txtRandomMoveVists = new JFontTextField();
    txtRandomMoveVists.setDocument(new DoubleDocument());
    txtRandomMoveVists.setBounds(
        Lizzie.config.isFrameFontSmall() ? 444 : (Lizzie.config.isFrameFontMiddle() ? 540 : 660),
        Lizzie.config.isFrameFontSmall() ? 55 : (Lizzie.config.isFrameFontMiddle() ? 54 : 53),
        Lizzie.config.isFrameFontSmall() ? 35 : (Lizzie.config.isFrameFontMiddle() ? 42 : 49),
        Lizzie.config.isFrameFontSmall() ? 18 : (Lizzie.config.isFrameFontMiddle() ? 20 : 22));
    getContentPane().add(txtRandomMoveVists);

    txtRandomDiffWinrate = new JFontTextField();
    txtRandomDiffWinrate.setDocument(new DoubleDocument());
    getContentPane().add(txtRandomDiffWinrate);
    txtRandomDiffWinrate.setBounds(
        Lizzie.config.isFrameFontSmall() ? 255 : (Lizzie.config.isFrameFontMiddle() ? 300 : 365),
        Lizzie.config.isFrameFontSmall() ? 55 : (Lizzie.config.isFrameFontMiddle() ? 54 : 53),
        Lizzie.config.isFrameFontSmall() ? 25 : (Lizzie.config.isFrameFontMiddle() ? 30 : 35),
        Lizzie.config.isFrameFontSmall() ? 18 : (Lizzie.config.isFrameFontMiddle() ? 20 : 22));

    JFontLabel lblRandomWinrate2 = new JFontLabel("%");
    getContentPane().add(lblRandomWinrate2);
    lblRandomWinrate2.setBounds(
        Lizzie.config.isFrameFontSmall() ? 282 : (Lizzie.config.isFrameFontMiddle() ? 332 : 401),
        54,
        35,
        20);

    JFontLabel lblnameSetting =
        new JFontLabel(
            resourceBundle.getString("EnginePkConfig.lblnameSetting")); // ("多盘对战棋谱保存文件夹名(一次有效):");
    txtnameSetting = new JFontTextField();
    getContentPane().add(lblnameSetting);
    getContentPane().add(txtnameSetting);
    lblnameSetting.setBounds(10, formToolbar ? 73 : 78, 454, 25);
    txtnameSetting.setBounds(
        Lizzie.config.isFrameFontSmall() ? 222 : (Lizzie.config.isFrameFontMiddle() ? 285 : 355),
        Lizzie.config.isFrameFontSmall()
            ? (formToolbar ? 77 : 82)
            : (Lizzie.config.isFrameFontMiddle() ? 81 : 80),
        100,
        Lizzie.config.isFrameFontSmall() ? 18 : (Lizzie.config.isFrameFontMiddle() ? 20 : 22));

    chkSatartNum =
        new JFontCheckBox(
            resourceBundle.getString("EnginePkConfig.chkSatartNum")); // ("开始序号(默认为1)");
    chkSatartNum.setBounds(
        Lizzie.config.isFrameFontSmall() ? 322 : (Lizzie.config.isFrameFontMiddle() ? 385 : 455),
        formToolbar ? 75 : 79,
        Lizzie.config.isFrameFontSmall() ? 142 : (Lizzie.config.isFrameFontMiddle() ? 160 : 194),
        formToolbar ? 20 : 23);
    getContentPane().add(chkSatartNum);

    txtStartNum = new JFontTextField();
    txtStartNum.setDocument(new IntDocument());
    txtStartNum.setBounds(
        Lizzie.config.isFrameFontSmall() ? 465 : (Lizzie.config.isFrameFontMiddle() ? 547 : 650),
        Lizzie.config.isFrameFontSmall()
            ? (formToolbar ? 77 : 82)
            : (Lizzie.config.isFrameFontMiddle() ? 81 : 80),
        Lizzie.config.isFrameFontSmall() ? 30 : (Lizzie.config.isFrameFontMiddle() ? 35 : 40),
        Lizzie.config.isFrameFontSmall() ? 18 : (Lizzie.config.isFrameFontMiddle() ? 20 : 22));
    getContentPane().add(txtStartNum);
    // txtStartNum.setColumns(10);

    chkSatartNum.setSelected(Lizzie.config.chkPkStartNum);
    txtStartNum.setText(Lizzie.config.pkStartNum + "");

    rdoGenmove =
        new JFontRadioButton(
            resourceBundle.getString("EnginePkConfig.rdoGenmove")); // ("genmove模式对战");
    rdoAna = new JFontRadioButton(resourceBundle.getString("EnginePkConfig.rdoAna")); // ("分析模式对战");
    rdoAna.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            setTextEnable(true);
          }
        });

    rdoGenmove.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            setTextEnable(false);
          }
        });
    rdoAna.setFocusable(false);
    rdoGenmove.setFocusable(false);

    ButtonGroup wrgroup = new ButtonGroup();
    wrgroup.add(rdoGenmove);
    wrgroup.add(rdoAna);

    getContentPane().add(rdoGenmove);
    getContentPane().add(rdoAna);

    rdoGenmove.setBounds(7, 6, 164, 20);
    rdoAna.setBounds(
        172,
        6,
        Lizzie.config.isChinese
            ? Lizzie.config.isFrameFontSmall() ? 80 : (Lizzie.config.isFrameFontMiddle() ? 95 : 110)
            : Lizzie.config.isFrameFontSmall()
                ? 100
                : (Lizzie.config.isFrameFontMiddle() ? 120 : 143),
        20);

    JButton aboutAnalyzeGame = new JFontButton("?");
    aboutAnalyzeGame.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            Lizzie.frame.showAnalyzeGenmoveInfo();
          }
        });
    aboutAnalyzeGame.setFocusable(false);
    aboutAnalyzeGame.setMargin(new Insets(0, 0, 0, 0));
    aboutAnalyzeGame.setBounds(
        Lizzie.config.isChinese
            ? Lizzie.config.isFrameFontSmall()
                ? 255
                : (Lizzie.config.isFrameFontMiddle() ? 270 : 285)
            : Lizzie.config.isFrameFontSmall()
                ? 275
                : (Lizzie.config.isFrameFontMiddle() ? 295 : 318),
        Lizzie.config.isFrameFontSmall() ? 6 : (Lizzie.config.isFrameFontMiddle() ? 3 : 1),
        Lizzie.config.frameFontSize > 16 ? Lizzie.config.menuHeight - 5 : Lizzie.config.menuHeight,
        Lizzie.config.frameFontSize > 16 ? Lizzie.config.menuHeight - 5 : Lizzie.config.menuHeight);
    getContentPane().add(aboutAnalyzeGame);

    chkGameMAX = new JFontCheckBox(resourceBundle.getString("EnginePkConfig.lblGameMAX"));
    txtGameMAX = new JFontTextField();
    txtGameMAX.setDocument(new IntDocument());
    getContentPane().add(chkGameMAX);
    getContentPane().add(txtGameMAX);

    chkGameMAX.setBounds(
        154,
        28,
        Lizzie.config.isFrameFontSmall() ? 75 : (Lizzie.config.isFrameFontMiddle() ? 90 : 105),
        20);
    txtGameMAX.setBounds(
        Lizzie.config.isFrameFontSmall() ? 229 : (Lizzie.config.isFrameFontMiddle() ? 244 : 259),
        Lizzie.config.isFrameFontSmall() ? 30 : (Lizzie.config.isFrameFontMiddle() ? 29 : 27),
        Lizzie.config.isFrameFontSmall() ? 40 : (Lizzie.config.isFrameFontMiddle() ? 45 : 50),
        Lizzie.config.isFrameFontSmall() ? 18 : (Lizzie.config.isFrameFontMiddle() ? 20 : 22));

    chkAutosave =
        new JFontCheckBox(resourceBundle.getString("EnginePkConfig.chkAutosave")); // ("自动保存棋谱");
    // JFontLabel lblAutosave = new JFontLabel("自动保存棋谱");
    getContentPane().add(chkAutosave);
    // add(lblAutosave);
    chkAutosave.setBounds(
        Lizzie.config.isFrameFontSmall() ? 288 : (Lizzie.config.isFrameFontMiddle() ? 310 : 330),
        28,
        Lizzie.config.isFrameFontSmall() ? 100 : (Lizzie.config.isFrameFontMiddle() ? 130 : 160),
        20);
    // lblAutosave.setBounds(242, 65, 100, 18);

    chkSaveWinrate =
        new JFontCheckBox(resourceBundle.getString("EnginePkConfig.chkSaveWinrate")); // ("保存胜率截图");
    getContentPane().add(chkSaveWinrate);
    chkSaveWinrate.setBounds(
        Lizzie.config.isFrameFontSmall() ? 387 : (Lizzie.config.isFrameFontMiddle() ? 446 : 495),
        28,
        178,
        20);

    JFontLabel lblChooseBestMoves =
        new JFontLabel(resourceBundle.getString("EnginePkConfig.lblChooseBestMoves")); // 选点显示为:
    getContentPane().add(lblChooseBestMoves);
    lblChooseBestMoves.setBounds(10, formToolbar ? 133 : 105, 178, 25);
    rdoCurrentMove =
        new JFontRadioButton(resourceBundle.getString("EnginePkConfig.rdoCurrentMove"));
    rdoLastMove = new JFontRadioButton(resourceBundle.getString("EnginePkConfig.rdoLastMove"));
    ButtonGroup groupBestmoves = new ButtonGroup();
    groupBestmoves.add(rdoCurrentMove);
    groupBestmoves.add(rdoLastMove);
    getContentPane().add(rdoLastMove);
    getContentPane().add(rdoCurrentMove);
    rdoCurrentMove.setBounds(130, formToolbar ? 133 : 105, 108, 25);
    if (Lizzie.config.isChinese)
      rdoLastMove.setBounds(
          235,
          formToolbar ? 133 : 105,
          Lizzie.config.isFrameFontSmall() ? 65 : (Lizzie.config.isFrameFontMiddle() ? 75 : 90),
          25);
    else
      rdoLastMove.setBounds(
          235,
          formToolbar ? 133 : 105,
          Lizzie.config.isFrameFontSmall() ? 110 : (Lizzie.config.isFrameFontMiddle() ? 125 : 150),
          25);
    if (Lizzie.config.showPreviousBestmovesInEngineGame) rdoLastMove.setSelected(true);
    else rdoCurrentMove.setSelected(true);
    rdoLastMove.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            chkPreviousBestmovesOnlyFirstMove.setEnabled(rdoLastMove.isSelected());
          }
        });
    rdoCurrentMove.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            chkPreviousBestmovesOnlyFirstMove.setEnabled(rdoLastMove.isSelected());
          }
        });

    chkPreviousBestmovesOnlyFirstMove =
        new JFontCheckBox(
            resourceBundle.getString("EnginePkConfig.chkPreviousBestmovesOnlyFirstMove"));
    chkPreviousBestmovesOnlyFirstMove.setSelected(Lizzie.config.showPreviousBestmovesOnlyFirstMove);
    chkPreviousBestmovesOnlyFirstMove.setEnabled(rdoLastMove.isSelected());
    if (Lizzie.config.isChinese)
      chkPreviousBestmovesOnlyFirstMove.setBounds(
          Lizzie.config.isFrameFontSmall() ? 305 : (Lizzie.config.isFrameFontMiddle() ? 315 : 330),
          formToolbar ? 133 : 105,
          208,
          25);
    else
      chkPreviousBestmovesOnlyFirstMove.setBounds(
          Lizzie.config.isFrameFontSmall() ? 345 : (Lizzie.config.isFrameFontMiddle() ? 370 : 395),
          formToolbar ? 133 : 105,
          208,
          25);
    getContentPane().add(chkPreviousBestmovesOnlyFirstMove);

    JTextArea textAreaHint = new JTextArea();
    textAreaHint.setFont(new Font("", Font.PLAIN, Config.frameFontSize));
    textAreaHint.setLineWrap(true);
    textAreaHint.setText(resourceBundle.getString("EnginePkConfig.textAreaHint"));
    textAreaHint.setBackground(this.getBackground());
    textAreaHint.setBounds(
        7,
        (formToolbar ? 158 : 133),
        Lizzie.config.isFrameFontSmall() ? 491 : (Lizzie.config.isFrameFontMiddle() ? 580 : 700),
        Lizzie.config.isFrameFontSmall() ? 107 : (Lizzie.config.isFrameFontMiddle() ? 125 : 145));
    textAreaHint.setEditable(false);
    getContentPane().add(textAreaHint);

    JFontButton okButton = new JFontButton(resourceBundle.getString("EnginePkConfig.okButton"));
    JFontButton cancelButton =
        new JFontButton(resourceBundle.getString("EnginePkConfig.cancelButton"));
    getContentPane().add(okButton);
    getContentPane().add(cancelButton);
    okButton.setMargin(new Insets(0, 0, 0, 0));
    cancelButton.setMargin(new Insets(0, 0, 0, 0));
    okButton.setBounds(
        175,
        Lizzie.config.isFrameFontSmall()
            ? (formToolbar ? 271 : 241)
            : (Lizzie.config.isFrameFontMiddle() ? 260 : 280),
        85,
        25);
    cancelButton.setBounds(
        266,
        Lizzie.config.isFrameFontSmall()
            ? (formToolbar ? 271 : 241)
            : (Lizzie.config.isFrameFontMiddle() ? 260 : 280),
        85,
        25);

    okButton.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            applyChange();
          }
        });

    cancelButton.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            setVisible(false);
          }
        });
    //    txtresignSettingBlack.setText(Lizzie.frame.toolbar.pkResignMoveCounts + "");
    //    txtresignSettingBlack2.setText(String.valueOf(Lizzie.frame.toolbar.pkResginWinrate));
    if (Lizzie.engineManager.engineGameInfo != null
        && Lizzie.engineManager.engineGameInfo.batchGameName != null)
      txtnameSetting.setText(Lizzie.frame.toolbar.batchPkNameToolbar);
    if (Lizzie.frame.toolbar.AutosavePk) {
      chkAutosave.setSelected(true);
    }
    if (Lizzie.frame.toolbar.isGenmoveToolbar) {
      rdoGenmove.setSelected(true);
      setTextEnable(false);
    } else {
      rdoAna.setSelected(true);
      setTextEnable(true);
    }
    if (Lizzie.frame.toolbar.exChangeToolbar) {
      chkExchange.setSelected(true);
    }
    if (Lizzie.frame.toolbar.checkGameMaxMove) {
      chkGameMAX.setSelected(true);
    }
    txtGameMAX.setText(Lizzie.frame.toolbar.maxGanmeMove + "");

    //    if (Lizzie.frame.toolbar.checkGameMinMove) {
    //      chkGameMIN.setSelected(true);
    //    }

    if (Lizzie.frame.toolbar.isRandomMove) {
      chkRandomMove.setSelected(true);
    }
    if (Lizzie.frame.toolbar.randomMove > 0)
      txtRandomMove.setText(Lizzie.frame.toolbar.randomMove + "");
    txtRandomDiffWinrate.setText(Lizzie.frame.toolbar.randomDiffWinrate + "");

    JFontLabel label = new JFontLabel("%"); // (第一选点永不排除)
    label.setBounds(
        Lizzie.config.isFrameFontSmall() ? 481 : (Lizzie.config.isFrameFontMiddle() ? 585 : 710),
        54,
        25,
        20);
    getContentPane().add(label);
    if (Lizzie.frame.toolbar.enginePkSaveWinrate) chkSaveWinrate.setSelected(true);

    chkRandomMoveVists.setSelected(Lizzie.config.checkRandomVisits);
    txtRandomMoveVists.setText(Lizzie.config.percentsRandomVisits + "");

    chkPkPonder = new JFontCheckBox("后台计算");
    chkPkPonder.setBounds(428, 6, 77, 23);
    if (formToolbar) {
      getContentPane().add(chkPkPonder);
    }
    // if (formToolbar)
    chkPkPonder.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            if (chkPkPonder.isSelected()) Lizzie.config.enginePkPonder = true;
            else Lizzie.config.enginePkPonder = false;
            Lizzie.config.uiConfig.put("engine-pk-ponder", Lizzie.config.enginePkPonder);
          }
        });
    chkPkPonder.setSelected(Lizzie.config.enginePkPonder);
  }

  private void setTextEnable(boolean status) {
    txtresignSettingBlack.setEnabled(status);
    txtresignSettingBlack2.setEnabled(status);
    txtresignSettingBlackMinMove.setEnabled(status);
    txtresignSettingWhite.setEnabled(status);
    txtresignSettingWhite2.setEnabled(status);
    txtresignSettingWhiteMinMove.setEnabled(status);
    chkRandomMove.setEnabled(status);
    txtRandomMove.setEnabled(status);
    txtRandomDiffWinrate.setEnabled(status);
    chkRandomMove.setEnabled(status);
    txtRandomDiffWinrate.setEnabled(status);
    txtRandomMove.setEnabled(status);
    txtRandomMoveVists.setEnabled(status);
    chkRandomMoveVists.setEnabled(status);
  }

  private void applyChange() {
    try {
      Lizzie.config.pkStartNum = Integer.parseInt(txtStartNum.getText());
    } catch (NumberFormatException err) {
    }
    Lizzie.config.chkPkStartNum = chkSatartNum.isSelected();
    // Lizzie.config.uiConfig.put("chkpk-start-num", Lizzie.config.chkPkStartNum);
    // Lizzie.config.uiConfig.put("pk-start-num", Lizzie.config.pkStartNum);

    try {
      Lizzie.config.firstEngineResignMoveCounts = Integer.parseInt(txtresignSettingBlack.getText());
    } catch (NumberFormatException err) {
    }
    try {
      Lizzie.config.firstEngineResignWinrate = Double.parseDouble(txtresignSettingBlack2.getText());
    } catch (NumberFormatException err) {
    }
    try {
      Lizzie.config.firstEngineMinMove = Integer.parseInt(txtresignSettingBlackMinMove.getText());
    } catch (NumberFormatException err) {
    }

    try {
      Lizzie.config.secondEngineResignMoveCounts =
          Integer.parseInt(txtresignSettingWhite.getText());
    } catch (NumberFormatException err) {
    }
    try {
      Lizzie.config.secondEngineResignWinrate =
          Double.parseDouble(txtresignSettingWhite2.getText());
    } catch (NumberFormatException err) {
    }
    try {
      Lizzie.config.secondEngineMinMove = Integer.parseInt(txtresignSettingWhiteMinMove.getText());
    } catch (NumberFormatException err) {
    }
    Lizzie.config.showPreviousBestmovesInEngineGame = rdoLastMove.isSelected();
    Lizzie.config.showPreviousBestmovesOnlyFirstMove =
        chkPreviousBestmovesOnlyFirstMove.isSelected();

    Lizzie.config.uiConfig.put(
        "show-previous-bestmoves-only-first-move",
        Lizzie.config.showPreviousBestmovesOnlyFirstMove);
    Lizzie.config.uiConfig.put(
        "show-previous-bestmoves-in-enginegame", Lizzie.config.showPreviousBestmovesInEngineGame);
    Lizzie.config.uiConfig.put(
        "first-engine-resign-move-counts", Lizzie.config.firstEngineResignMoveCounts);
    Lizzie.config.uiConfig.put(
        "first-engine-resign-winrate", Lizzie.config.firstEngineResignWinrate);
    Lizzie.config.uiConfig.put("first-engine-min-move", Lizzie.config.firstEngineMinMove);

    Lizzie.config.uiConfig.put(
        "second-engine-resign-move-counts", Lizzie.config.secondEngineResignMoveCounts);
    Lizzie.config.uiConfig.put(
        "second-engine-resign-winrate", Lizzie.config.secondEngineResignWinrate);
    Lizzie.config.uiConfig.put("second-engine-min-move", Lizzie.config.secondEngineMinMove);

    Lizzie.frame.toolbar.AutosavePk = chkAutosave.isSelected();
    Lizzie.frame.toolbar.isGenmoveToolbar = rdoGenmove.isSelected();
    Lizzie.frame.toolbar.batchPkNameToolbar = txtnameSetting.getText();
    Lizzie.frame.toolbar.exChangeToolbar = chkExchange.isSelected();
    Lizzie.frame.toolbar.isRandomMove = chkRandomMove.isSelected();
    Lizzie.frame.toolbar.enginePkSaveWinrate = chkSaveWinrate.isSelected();
    try {
      Lizzie.frame.toolbar.randomMove = Integer.parseInt(txtRandomMove.getText().trim());
    } catch (NumberFormatException err) {
    }
    try {
      Lizzie.frame.toolbar.randomDiffWinrate =
          Double.parseDouble(txtRandomDiffWinrate.getText().trim());
    } catch (NumberFormatException err) {
    }
    Lizzie.config.checkRandomVisits = chkRandomMoveVists.isSelected();
    try {
      Lizzie.config.percentsRandomVisits = Double.parseDouble(txtRandomMoveVists.getText().trim());
    } catch (NumberFormatException err) {
    }
    Lizzie.config.uiConfig.put("check-random-visits", Lizzie.config.checkRandomVisits);
    Lizzie.config.uiConfig.put("percents-random-visits", Lizzie.config.percentsRandomVisits);
    Lizzie.frame.toolbar.setGenmove();

    Lizzie.frame.toolbar.checkGameMaxMove = chkGameMAX.isSelected();
    try {
      Lizzie.frame.toolbar.maxGanmeMove = Integer.parseInt(txtGameMAX.getText().trim());
    } catch (NumberFormatException err) {
    }
    //    Lizzie.frame.toolbar.checkGameMinMove = chkGameMIN.isSelected();
    //    try {
    //      Lizzie.frame.toolbar.minGanmeMove = Integer.parseInt(txtGameMIN.getText().trim());
    //    } catch (NumberFormatException err) {
    //    }
    setVisible(false);
  }
}
