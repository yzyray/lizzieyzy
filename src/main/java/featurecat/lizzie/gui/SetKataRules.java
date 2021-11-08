package featurecat.lizzie.gui;

import featurecat.lizzie.Lizzie;
import featurecat.lizzie.util.Utils;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.ResourceBundle;
import javax.imageio.ImageIO;
import javax.swing.ButtonGroup;
import javax.swing.JDialog;
import org.json.JSONObject;

public class SetKataRules extends JDialog {
  // private JDialog thisFrame = this;
  private ResourceBundle resourceBundle = Lizzie.resourceBundle;
  JFontRadioButton rdoArea;
  JFontRadioButton rdoTerritory;
  JFontRadioButton rdoSimpleKo;
  JFontRadioButton rdoPositionKo;
  JFontRadioButton rdoSituationalKo;
  JFontRadioButton rdoSuicide;
  JFontRadioButton rdoNoSuicide;
  JFontRadioButton rdoNoTax;
  JFontRadioButton rdoSeKiTax;
  JFontRadioButton rdoAllTax;
  JFontRadioButton rdoNoHandicapKomi;
  JFontRadioButton rdoHandicapKomiN;
  JFontRadioButton rdoHandicapKomiN1;
  JFontCheckBox chkbxAutoLoadRules;
  JSONObject jo;
  // private JFontLabel lblNewLabel_3;
  private JFontRadioButton rdoButtonGo;
  private JFontRadioButton rdoNoButtonGo;

  public SetKataRules() {
    // this.setModal(true);
    // setType(Type.POPUP);
    setResizable(false);
    setTitle(resourceBundle.getString("SetKataRules.title")); // ("KataGo规则设置(仅支持KataGo v1.3引擎)");
    setAlwaysOnTop(true);
    setLocationRelativeTo(Lizzie.frame);
    getContentPane().setLayout(null);

    this.addWindowListener(
        new WindowAdapter() {
          public void windowClosing(WindowEvent e) {
            if (Lizzie.leelaz.isPondering()) Lizzie.leelaz.ponder();
            setVisible(false);
          }
        });

    JFontLabel lblScoringRule =
        new JFontLabel(resourceBundle.getString("SetKataRules.lblScoringRule")); // ("胜负判断:");
    lblScoringRule.setBounds(21, 48, 146, 27);
    getContentPane().add(lblScoringRule);

    rdoTerritory =
        new JFontRadioButton(resourceBundle.getString("SetKataRules.rdoTerritory")); // ("点目");
    rdoTerritory.setBounds(280, 50, 119, 23);
    getContentPane().add(rdoTerritory);

    rdoArea = new JFontRadioButton(resourceBundle.getString("SetKataRules.rdoArea")); // ("数子");
    rdoArea.setBounds(173, 50, 105, 23);
    getContentPane().add(rdoArea);

    rdoTerritory.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            rdoNoButtonGo.setEnabled(false);
            rdoButtonGo.setEnabled(false);
          }
        });

    rdoArea.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            rdoNoButtonGo.setEnabled(true);
            rdoButtonGo.setEnabled(true);
          }
        });

    ButtonGroup group1 = new ButtonGroup();
    group1.add(rdoArea);
    group1.add(rdoTerritory);

    JFontLabel lblKoRule =
        new JFontLabel(resourceBundle.getString("SetKataRules.lblKoRule")); // ("打劫规则:");
    lblKoRule.setBounds(21, 85, 129, 27);
    getContentPane().add(lblKoRule);

    rdoSimpleKo =
        new JFontRadioButton(resourceBundle.getString("SetKataRules.rdoSimpleKo")); // ("不禁全同");
    rdoSimpleKo.setBounds(173, 87, 105, 23);
    getContentPane().add(rdoSimpleKo);

    rdoPositionKo =
        new JFontRadioButton(resourceBundle.getString("SetKataRules.rdoPositionKo")); // ("严格禁全同");
    rdoPositionKo.setBounds(
        280,
        87,
        Lizzie.config.isFrameFontSmall() ? 119 : (Lizzie.config.isFrameFontMiddle() ? 119 : 130),
        23);
    getContentPane().add(rdoPositionKo);

    rdoSituationalKo =
        new JFontRadioButton(
            resourceBundle.getString("SetKataRules.rdoSituationalKo")); // ("同一色落子后禁全同");
    rdoSituationalKo.setBounds(
        Lizzie.config.isFrameFontSmall()
            ? Lizzie.config.isFrameFontSmall()
                ? 401
                : (Lizzie.config.isFrameFontMiddle() ? 401 : 450)
            : (Lizzie.config.isFrameFontMiddle()
                ? Lizzie.config.isFrameFontSmall()
                    ? 401
                    : (Lizzie.config.isFrameFontMiddle() ? 401 : 450)
                : 450),
        87,
        295,
        23);
    getContentPane().add(rdoSituationalKo);

    ButtonGroup group2 = new ButtonGroup();
    group2.add(rdoSimpleKo);
    group2.add(rdoPositionKo);
    group2.add(rdoSituationalKo);

    JFontLabel lblMultiStoneSuicide =
        new JFontLabel(
            resourceBundle.getString("SetKataRules.lblMultiStoneSuicide")); // ("允许棋块自杀:");
    lblMultiStoneSuicide.setBounds(21, 122, 146, 27);
    getContentPane().add(lblMultiStoneSuicide);

    rdoSuicide =
        new JFontRadioButton(resourceBundle.getString("SetKataRules.rdoSuicide")); // ("是");
    rdoSuicide.setBounds(173, 124, 105, 23);
    getContentPane().add(rdoSuicide);

    rdoNoSuicide =
        new JFontRadioButton(resourceBundle.getString("SetKataRules.rdoNoSuicide")); // ("否");
    rdoNoSuicide.setBounds(280, 124, 119, 23);
    getContentPane().add(rdoNoSuicide);

    ButtonGroup group3 = new ButtonGroup();
    group3.add(rdoSuicide);
    group3.add(rdoNoSuicide);

    JFontLabel lblTaxRule =
        new JFontLabel(resourceBundle.getString("SetKataRules.lblTaxRule")); // ("还棋头:");
    lblTaxRule.setBounds(21, 159, 146, 27);
    getContentPane().add(lblTaxRule);

    rdoNoTax = new JFontRadioButton(resourceBundle.getString("SetKataRules.rdoNoTax")); // ("无");
    rdoNoTax.setBounds(173, 161, 84, 23);
    getContentPane().add(rdoNoTax);

    rdoSeKiTax =
        new JFontRadioButton(resourceBundle.getString("SetKataRules.rdoSeKiTax")); // ("仅双活棋块");
    rdoSeKiTax.setBounds(
        280,
        161,
        Lizzie.config.isFrameFontSmall() ? 119 : (Lizzie.config.isFrameFontMiddle() ? 119 : 130),
        23);
    getContentPane().add(rdoSeKiTax);

    rdoAllTax =
        new JFontRadioButton(resourceBundle.getString("SetKataRules.rdoAllTax")); // ("每一块棋");
    rdoAllTax.setBounds(
        Lizzie.config.isFrameFontSmall() ? 401 : (Lizzie.config.isFrameFontMiddle() ? 401 : 450),
        161,
        143,
        23);
    getContentPane().add(rdoAllTax);

    ButtonGroup group4 = new ButtonGroup();
    group4.add(rdoNoTax);
    group4.add(rdoSeKiTax);
    group4.add(rdoAllTax);

    JFontLabel lblWhiteHandicapBonus =
        new JFontLabel(
            resourceBundle.getString("SetKataRules.lblWhiteHandicapBonus")); // ("让子贴还(让N子):");
    lblWhiteHandicapBonus.setBounds(21, 196, 153, 27);
    getContentPane().add(lblWhiteHandicapBonus);

    rdoNoHandicapKomi =
        new JFontRadioButton(
            resourceBundle.getString("SetKataRules.rdoNoHandicapKomi")); // ("不贴还");
    rdoNoHandicapKomi.setBounds(173, 198, 105, 23);
    getContentPane().add(rdoNoHandicapKomi);

    rdoHandicapKomiN =
        new JFontRadioButton(
            resourceBundle.getString("SetKataRules.rdoHandicapKomiN")); // ("贴还N目");
    rdoHandicapKomiN.setBounds(280, 198, 119, 23);
    getContentPane().add(rdoHandicapKomiN);

    rdoHandicapKomiN1 =
        new JFontRadioButton(
            resourceBundle.getString("SetKataRules.rdoHandicapKomiN1")); // ("贴还N-1目");
    rdoHandicapKomiN1.setBounds(
        Lizzie.config.isFrameFontSmall() ? 401 : (Lizzie.config.isFrameFontMiddle() ? 401 : 450),
        198,
        143,
        23);
    getContentPane().add(rdoHandicapKomiN1);

    ButtonGroup group5 = new ButtonGroup();
    group5.add(rdoNoHandicapKomi);
    group5.add(rdoHandicapKomiN);
    group5.add(rdoHandicapKomiN1);

    JFontLabel lblClassicRules =
        new JFontLabel(resourceBundle.getString("SetKataRules.lblClassicRules")); // ("典型规则:");
    lblClassicRules.setBounds(10, 9, 91, 27);
    getContentPane().add(lblClassicRules);

    JFontButton btnChnRule =
        new JFontButton(resourceBundle.getString("SetKataRules.btnChnRule")); // ("中国规则");
    btnChnRule.setMargin(new Insets(0, 0, 0, 0));
    btnChnRule.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            rdoArea.setSelected(true);
            rdoNoTax.setSelected(true);
            rdoHandicapKomiN.setSelected(true);
            rdoSimpleKo.setSelected(true);
            rdoNoSuicide.setSelected(true);
            rdoNoButtonGo.setSelected(true);
            rdoNoButtonGo.setEnabled(true);
            rdoButtonGo.setEnabled(true);
          }
        });
    btnChnRule.setBounds(99, 11, 93, 23);
    getContentPane().add(btnChnRule);

    JFontButton btnJpnRule =
        new JFontButton(resourceBundle.getString("SetKataRules.btnJpnRule")); // ("日本规则");
    btnJpnRule.setMargin(new Insets(0, 0, 0, 0));
    btnJpnRule.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            rdoTerritory.setSelected(true);
            rdoSeKiTax.setSelected(true);
            rdoNoHandicapKomi.setSelected(true);
            rdoSimpleKo.setSelected(true);
            rdoNoSuicide.setSelected(true);
            rdoNoButtonGo.setSelected(true);
            rdoNoButtonGo.setEnabled(false);
            rdoButtonGo.setEnabled(false);
          }
        });
    btnJpnRule.setBounds(202, 11, 93, 23);
    getContentPane().add(btnJpnRule);

    JFontButton btnTTRule =
        new JFontButton(resourceBundle.getString("SetKataRules.btnTTRule")); // ("Tromp-Taylor规则");
    btnTTRule.setMargin(new Insets(0, 0, 0, 0));
    btnTTRule.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            rdoArea.setSelected(true);
            rdoNoTax.setSelected(true);
            rdoHandicapKomiN.setSelected(true);
            rdoPositionKo.setSelected(true);
            rdoSuicide.setSelected(true);
            rdoNoButtonGo.setSelected(true);
            rdoNoButtonGo.setEnabled(true);
            rdoButtonGo.setEnabled(true);
          }
        });
    btnChnRule.setFocusable(false);
    btnTTRule.setFocusable(false);
    btnJpnRule.setFocusable(false);

    btnTTRule.setBounds(
        416,
        11,
        Lizzie.config.isFrameFontSmall() ? 142 : (Lizzie.config.isFrameFontMiddle() ? 142 : 180),
        23);
    getContentPane().add(btnTTRule);

    JFontButton btnCancel =
        new JFontButton(resourceBundle.getString("SetKataRules.btnCancel")); // ("取消");
    btnCancel.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            if (Lizzie.leelaz.isPondering()) Lizzie.leelaz.ponder();
            setVisible(false);
          }
        });
    btnCancel.setBounds(292, 293, 93, 23);
    getContentPane().add(btnCancel);

    JFontButton btnApply =
        new JFontButton(resourceBundle.getString("SetKataRules.btnApply")); // ("确定");
    btnApply.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            Lizzie.board.clearBestMovesAfter(Lizzie.board.getHistory().getStart());
            if (jo == null) {
              jo = new JSONObject();
            }
            if (rdoArea.isSelected()) jo.put("scoring", "AREA");
            if (rdoTerritory.isSelected()) jo.put("scoring", "TERRITORY");

            if (rdoSimpleKo.isSelected()) jo.put("ko", "SIMPLE");
            if (rdoPositionKo.isSelected()) jo.put("ko", "POSITIONAL");
            if (rdoSituationalKo.isSelected()) jo.put("ko", "SITUATIONAL");

            if (rdoSuicide.isSelected()) jo.put("suicide", true);
            if (rdoNoSuicide.isSelected()) jo.put("suicide", false);

            if (rdoNoTax.isSelected()) jo.put("tax", "NONE");
            if (rdoSeKiTax.isSelected()) jo.put("tax", "SEKI");
            if (rdoAllTax.isSelected()) jo.put("tax", "ALL");

            if (rdoNoHandicapKomi.isSelected()) jo.put("whiteHandicapBonus", "0");
            if (rdoHandicapKomiN.isSelected()) jo.put("whiteHandicapBonus", "N");
            if (rdoHandicapKomiN1.isSelected()) jo.put("whiteHandicapBonus", "N-1");

            if (rdoButtonGo.isSelected()) jo.put("hasButton", true);
            if (rdoNoButtonGo.isSelected()) jo.put("hasButton", false);

            int oriRules = Lizzie.leelaz.usingSpecificRules;
            if (jo.optString("scoring", "").contentEquals("AREA")
                && jo.optString("ko", "").contentEquals("POSITIONAL")
                && jo.optBoolean("suicide", false)
                && jo.optString("tax", "").contentEquals("NONE")
                && jo.optString("whiteHandicapBonus", "").contentEquals("N")
                && !jo.optBoolean("hasButton", true)) {
              Lizzie.leelaz.usingSpecificRules = 4; // tt规则
            } else if (jo.optString("scoring", "").contentEquals("AREA")
                && jo.optString("tax", "").contentEquals("NONE")
                && !jo.optBoolean("hasButton", true)) {
              Lizzie.leelaz.usingSpecificRules = 1; // 中国规则
            } else if (jo.optString("scoring", "").contentEquals("AREA")
                && jo.optString("tax", "").contentEquals("ALL")
                && !jo.optBoolean("hasButton", true)) {
              Lizzie.leelaz.usingSpecificRules = 2; // 中古规则
            } else if (jo.optString("scoring", "").contentEquals("TERRITORY")
                && jo.optString("tax", "").contentEquals("SEKI")) {
              Lizzie.leelaz.usingSpecificRules = 3; // 日本规则
            } else if (jo.optString("scoring", "").contentEquals("AREA")
                || jo.optString("scoring", "").contentEquals("TERRITORY")) {
              Lizzie.leelaz.usingSpecificRules = 5; // 其他规则
            }
            if (Lizzie.leelaz.usingSpecificRules != oriRules) Lizzie.frame.refresh();
            Lizzie.leelaz.sendCommand("kata-set-rules " + jo.toString());

            if (chkbxAutoLoadRules.isSelected()) {
              Lizzie.config.autoLoadKataRules = true;
              Lizzie.config.kataRules = jo.toString();
              Lizzie.config.uiConfig.put("kata-rules", Lizzie.config.kataRules);
              Lizzie.config.uiConfig.put("auto-load-kata-rules", true);
            } else {
              Lizzie.config.autoLoadKataRules = false;
              Lizzie.config.uiConfig.put("auto-load-kata-rules", false);
            }
            Lizzie.leelaz.getParameterScadule(false);
            Lizzie.leelaz.sendCommand("kata-get-rules");
            if (Lizzie.leelaz.isPondering()) Lizzie.leelaz.ponder();
            setVisible(false);
          }
        });
    btnApply.setBounds(184, 293, 93, 23);
    getContentPane().add(btnApply);

    JFontButton btnChnOldRule =
        new JFontButton(resourceBundle.getString("SetKataRules.btnChnOldRule")); // ("中国古棋");
    btnChnOldRule.setMargin(new Insets(0, 0, 0, 0));
    btnChnOldRule.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            rdoArea.setSelected(true);
            rdoAllTax.setSelected(true);
            rdoHandicapKomiN.setSelected(true);
            rdoSimpleKo.setSelected(true);
            rdoSuicide.setSelected(false);
            rdoNoButtonGo.setEnabled(true);
            rdoButtonGo.setEnabled(true);
            rdoNoButtonGo.setSelected(true);
          }
        });
    btnChnOldRule.setBounds(305, 11, 101, 23);
    getContentPane().add(btnChnOldRule);
    btnChnOldRule.setFocusable(false);
    chkbxAutoLoadRules =
        new JFontCheckBox(
            resourceBundle.getString(
                "SetKataRules.chkbxAutoLoadRules")); // ("自动设置为此规则(勾选则所有Katago引擎启动时自动设置为此规则,仅支持KataGo v1.3引擎)");
    chkbxAutoLoadRules.setBounds(18, 265, 897, 23);
    getContentPane().add(chkbxAutoLoadRules);
    if (Lizzie.config.autoLoadKataRules) {
      chkbxAutoLoadRules.setSelected(true);
    }

    JFontLabel lblHasButton =
        new JFontLabel(
            resourceBundle.getString("SetKataRules.lblHasButton")); // ("数子规则下收后方贴还0.5目");
    lblHasButton.setBounds(21, 233, 256, 27);
    getContentPane().add(lblHasButton);

    rdoButtonGo =
        new JFontRadioButton(resourceBundle.getString("SetKataRules.rdoButtonGo")); // ("是");
    rdoButtonGo.setBounds(280, 235, 105, 23);
    getContentPane().add(rdoButtonGo);

    rdoNoButtonGo =
        new JFontRadioButton(resourceBundle.getString("SetKataRules.rdoNoButtonGo")); // ("否");
    rdoNoButtonGo.setBounds(
        Lizzie.config.isFrameFontSmall() ? 401 : (Lizzie.config.isFrameFontMiddle() ? 401 : 450),
        235,
        143,
        23);

    ButtonGroup group6 = new ButtonGroup();
    group6.add(rdoButtonGo);
    group6.add(rdoNoButtonGo);
    getContentPane().add(rdoNoButtonGo);
    setSize(592, 361);
    Lizzie.setFrameSize(
        this,
        Lizzie.config.isFrameFontSmall() ? 592 : (Lizzie.config.isFrameFontMiddle() ? 720 : 890),
        361);
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
    if (!Lizzie.leelaz.isKatago) {
      //      Message msg = new Message();
      //      msg.setMessage("当前引擎不是KataGo引擎(或未加载完成),可能无法修改规则");
      //      msg.setVisible(true);
      Utils.showMsg(resourceBundle.getString("SetKataRules.notKataGoHint"));
    }
    Lizzie.leelaz.getRcentLine = true;
    Lizzie.leelaz.nameCmd();
    Lizzie.leelaz.sendCommand("kata-get-rules");
  }

  public boolean getRules() {
    if (Lizzie.leelaz.recentRulesLine.equals("")) {
      return false;
    } else {
      String line = Lizzie.leelaz.recentRulesLine;
      jo = new JSONObject(new String(line.substring(2)));
      // Lizzie.leelaz.usingSpecificRules=
      if (jo.optBoolean("hasButton", false)) rdoButtonGo.setSelected(true);
      else rdoNoButtonGo.setSelected(true);
      if (jo.optBoolean("suicide", false)) rdoSuicide.setSelected(true);
      else rdoNoSuicide.setSelected(true);
      if (jo.optString("ko", "").contentEquals("POSITIONAL")) rdoPositionKo.setSelected(true);
      if (jo.optString("ko", "").contentEquals("SIMPLE")) rdoSimpleKo.setSelected(true);
      if (jo.optString("ko", "").contentEquals("SITUATIONAL")) rdoSituationalKo.setSelected(true);

      if (jo.optString("scoring", "").contentEquals("AREA")) {
        rdoArea.setSelected(true);
        rdoNoButtonGo.setEnabled(true);
        rdoButtonGo.setEnabled(true);
      }
      if (jo.optString("scoring", "").contentEquals("TERRITORY")) {
        rdoTerritory.setSelected(true);
        rdoNoButtonGo.setEnabled(false);
        rdoButtonGo.setEnabled(false);
      }

      if (jo.optString("whiteHandicapBonus", "").contentEquals("0"))
        rdoNoHandicapKomi.setSelected(true);
      if (jo.optString("whiteHandicapBonus", "").contentEquals("N"))
        rdoHandicapKomiN.setSelected(true);
      if (jo.optString("whiteHandicapBonus", "").contentEquals("N-1"))
        rdoHandicapKomiN1.setSelected(true);

      if (jo.optString("tax", "").contentEquals("NONE")) rdoNoTax.setSelected(true);
      if (jo.optString("tax", "").contentEquals("ALL")) rdoAllTax.setSelected(true);
      if (jo.optString("tax", "").contentEquals("SEKI")) rdoSeKiTax.setSelected(true);

      return true;
    }
  }
}
