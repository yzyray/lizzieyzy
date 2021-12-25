package featurecat.lizzie.gui;

import featurecat.lizzie.Lizzie;
import featurecat.lizzie.util.Utils;
import java.awt.Color;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;
import javax.imageio.ImageIO;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import org.jdesktop.swingx.util.OS;

public class FirstUseSettings extends JDialog {

  private JFontTextField txtLimitSuggestion;
  private JFontTextField txtLimitVariation;
  private JFontTextField txtMaxAnalyzeTime;
  private JFontTextField txtMaxAnalyzePlayouts;
  private JCheckBox chkPlayouts;
  private JCheckBox chkScoreLead;
  private JCheckBox chkWinrate;
  private JCheckBox chkLimitTime;
  private JCheckBox chkLimitPlayouts;
  private JDialog thisDialog = this;

  public FirstUseSettings(boolean firstTime) {
    this.setModal(true);
    // setType(Type.POPUP);

    setResizable(false);
    setTitle(Lizzie.resourceBundle.getString("FirstUseSettings.title")); // ("初始化设置");
    setAlwaysOnTop(true);
    setSize(1076, 538);
    Lizzie.setFrameSize(
        this,
        Lizzie.config.isFrameFontSmall() ? 656 : (Lizzie.config.isFrameFontMiddle() ? 785 : 955),
        524);
    setLocationRelativeTo(Lizzie.frame != null ? Lizzie.frame : null);

    PanelWithToolTips panel = new PanelWithToolTips();
    panel.setLayout(null);
    getContentPane().add(panel);

    ImageIcon iconSettings = new ImageIcon();
    try {
      iconSettings.setImage(ImageIO.read(getClass().getResourceAsStream("/assets/settings.png")));
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    JFontLabel lblMouseOverSuggestion =
        new JFontLabel(
            Lizzie.resourceBundle.getString(
                "FirstUseSettings.lblMouseOverSuggestion")); // ("鼠标悬停在选点上时,变化图:");
    lblMouseOverSuggestion.setBounds(10, 6, 334, 22);
    panel.add(lblMouseOverSuggestion);

    JFontRadioButton rdoMouseOverSuggestionRefresh =
        new JFontRadioButton(
            Lizzie.resourceBundle.getString(
                "FirstUseSettings.rdoMouseOverSuggestionRefresh")); // ("不断刷新");
    rdoMouseOverSuggestionRefresh.setBounds(
        Lizzie.config.isFrameFontSmall() ? 240 : (Lizzie.config.isFrameFontMiddle() ? 310 : 380),
        6,
        108,
        23);
    panel.add(rdoMouseOverSuggestionRefresh);

    JFontRadioButton rdoMouseOverSuggestionNoRefresh =
        new JFontRadioButton(
            Lizzie.resourceBundle.getString(
                "FirstUseSettings.rdoMouseOverSuggestionNoRefresh")); // ("不刷新(鼠标移开再次移回则刷新一次)");
    rdoMouseOverSuggestionNoRefresh.setBounds(
        Lizzie.config.isFrameFontSmall() ? 350 : (Lizzie.config.isFrameFontMiddle() ? 420 : 520),
        6,
        457,
        23);
    panel.add(rdoMouseOverSuggestionNoRefresh);

    ButtonGroup group1 = new ButtonGroup();
    group1.add(rdoMouseOverSuggestionRefresh);
    group1.add(rdoMouseOverSuggestionNoRefresh);

    JFontLabel lblMouseOverSubboard =
        new JFontLabel(
            Lizzie.resourceBundle.getString(
                "FirstUseSettings.lblMouseOverSubboard")); // ("鼠标悬停在小棋盘上时,小棋盘:");
    lblMouseOverSubboard.setBounds(10, 46, 344, 22);
    panel.add(lblMouseOverSubboard);

    JFontRadioButton rdoMouseOverSubboardRefresh =
        new JFontRadioButton(
            Lizzie.resourceBundle.getString(
                "FirstUseSettings.rdoMouseOverSubboardRefresh")); // ("不断刷新");
    rdoMouseOverSubboardRefresh.setBounds(
        Lizzie.config.isFrameFontSmall() ? 240 : (Lizzie.config.isFrameFontMiddle() ? 310 : 380),
        48,
        107,
        23);
    panel.add(rdoMouseOverSubboardRefresh);

    JFontRadioButton rdoMouseOverSubboardNoRefresh =
        new JFontRadioButton(
            Lizzie.resourceBundle.getString(
                "FirstUseSettings.rdoMouseOverSubboardNoRefresh")); // ("不刷新(鼠标移开再次移回则刷新一次)");
    rdoMouseOverSubboardNoRefresh.setBounds(
        Lizzie.config.isFrameFontSmall() ? 350 : (Lizzie.config.isFrameFontMiddle() ? 420 : 520),
        48,
        457,
        23);
    panel.add(rdoMouseOverSubboardNoRefresh);

    ButtonGroup group2 = new ButtonGroup();
    group2.add(rdoMouseOverSubboardRefresh);
    group2.add(rdoMouseOverSubboardNoRefresh);

    JFontLabel lblLanguage =
        new JFontLabel(Lizzie.resourceBundle.getString("FirstUseSettings.lblLanguage"));
    lblLanguage.setBounds(10, 326, 249, 22);
    panel.add(lblLanguage);

    JFontRadioButton rdoLanguageChinese =
        new JFontRadioButton(
            Lizzie.resourceBundle.getString("FirstUseSettings.rdoLanguageChinese"));
    rdoLanguageChinese.setBounds(
        Lizzie.config.isFrameFontSmall() ? 240 : (Lizzie.config.isFrameFontMiddle() ? 310 : 380),
        328,
        80,
        23);
    panel.add(rdoLanguageChinese);

    JFontRadioButton rdoLanguageEnglish =
        new JFontRadioButton(
            Lizzie.resourceBundle.getString("FirstUseSettings.rdoLanguageEnglish"));
    rdoLanguageEnglish.setBounds(
        Lizzie.config.isFrameFontSmall() ? 340 : (Lizzie.config.isFrameFontMiddle() ? 410 : 480),
        328,
        90,
        23);
    panel.add(rdoLanguageEnglish);

    JFontRadioButton rdoLanguageKorean =
        new JFontRadioButton(Lizzie.resourceBundle.getString("FirstUseSettings.rdoLanguageKorean"));
    rdoLanguageKorean.setBounds(
        Lizzie.config.isFrameFontSmall() ? 440 : (Lizzie.config.isFrameFontMiddle() ? 510 : 590),
        328,
        90,
        23);
    panel.add(rdoLanguageKorean);

    JFontRadioButton rdoLanguageJapanese =
        new JFontRadioButton(
            Lizzie.resourceBundle.getString("FirstUseSettings.rdoLanguageJapanese"));
    rdoLanguageJapanese.setBounds(
        Lizzie.config.isFrameFontSmall() ? 540 : (Lizzie.config.isFrameFontMiddle() ? 610 : 700),
        328,
        90,
        23);
    panel.add(rdoLanguageJapanese);

    ButtonGroup languageGroup = new ButtonGroup();
    languageGroup.add(rdoLanguageChinese);
    languageGroup.add(rdoLanguageEnglish);
    languageGroup.add(rdoLanguageKorean);
    languageGroup.add(rdoLanguageJapanese);

    JFontLabel lblLooks =
        new JFontLabel(Lizzie.resourceBundle.getString("FirstUseSettings.lblLooks")); // "界面样式");
    lblLooks.setBounds(10, 366, 249, 22);
    panel.add(lblLooks);

    JFontRadioButton rdoJavaLooks =
        new JFontRadioButton(
            Lizzie.resourceBundle.getString("FirstUseSettings.rdoJavaLooks")); // ("Java默认");
    rdoJavaLooks.setBounds(
        Lizzie.config.isFrameFontSmall() ? 350 : (Lizzie.config.isFrameFontMiddle() ? 420 : 520),
        368,
        140,
        23);
    panel.add(rdoJavaLooks);

    JFontRadioButton rdoSysLooks =
        new JFontRadioButton(
            Lizzie.resourceBundle.getString("FirstUseSettings.rdoSysLooks")); // ("系统默认");
    rdoSysLooks.setBounds(
        Lizzie.config.isFrameFontSmall() ? 240 : (Lizzie.config.isFrameFontMiddle() ? 310 : 380),
        368,
        114,
        23);
    panel.add(rdoSysLooks);

    ButtonGroup looksGroup = new ButtonGroup();
    looksGroup.add(rdoJavaLooks);
    looksGroup.add(rdoSysLooks);

    JFontButton btnLooksHelper = new JFontButton(iconSettings);
    if (Lizzie.config.isChinese)
      btnLooksHelper.setBounds(
          Lizzie.config.isFrameFontSmall() ? 66 : (Lizzie.config.isFrameFontMiddle() ? 85 : 100),
          366,
          24,
          23);
    else
      btnLooksHelper.setBounds(
          Lizzie.config.isFrameFontSmall() ? 90 : (Lizzie.config.isFrameFontMiddle() ? 95 : 100),
          366,
          24,
          23);
    btnLooksHelper.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            Discribe lizzieCacheDiscribe = new Discribe();
            lizzieCacheDiscribe.setInfo(
                Lizzie.resourceBundle.getString("FirstUseSettings.looksHelperContent"),
                Lizzie.resourceBundle.getString("FirstUseSettings.looksHelperTitle"),
                thisDialog); // ("如果系统默认样式部分按钮、选择框显示不全,则可尝试选择Java默认界面","界面样式说明");
          }
        });
    panel.add(btnLooksHelper);

    JFontLabel lblLimitSuggestion =
        new JFontLabel(
            Lizzie.resourceBundle.getString("FirstUseSettings.lblLimitSuggestion")); // ("限制选点个数:");
    lblLimitSuggestion.setBounds(10, 246, 337, 22);
    panel.add(lblLimitSuggestion);

    txtLimitSuggestion = new JFontTextField();
    txtLimitSuggestion.setBounds(
        Lizzie.config.isFrameFontSmall() ? 240 : (Lizzie.config.isFrameFontMiddle() ? 310 : 380),
        246,
        66,
        24);
    panel.add(txtLimitSuggestion);
    txtLimitSuggestion.setColumns(10);

    JFontLabel lblLimitVariation =
        new JFontLabel(
            Lizzie.resourceBundle.getString("FirstUseSettings.lblLimitVariation")); // ("限制变化图长度:");
    lblLimitVariation.setBounds(
        Lizzie.config.isFrameFontSmall() ? 355 : (Lizzie.config.isFrameFontMiddle() ? 425 : 525),
        246,
        236,
        22);
    panel.add(lblLimitVariation);

    txtLimitVariation = new JFontTextField();
    txtLimitVariation.setBounds(
        Lizzie.config.isFrameFontSmall() ? 525 : (Lizzie.config.isFrameFontMiddle() ? 645 : 785),
        246,
        66,
        24);
    panel.add(txtLimitVariation);
    txtLimitVariation.setColumns(10);

    JFontLabel lblScoreOnBoard =
        new JFontLabel(
            Lizzie.resourceBundle.getString("FirstUseSettings.lblShowScore")); // ("目差在选点上显示为:");
    lblScoreOnBoard.setBounds(10, 86, 337, 22);
    panel.add(lblScoreOnBoard);

    JFontRadioButton rdoScoreOnBoardWithKomi =
        new JFontRadioButton(
            Lizzie.resourceBundle.getString(
                "FirstUseSettings.rdoScoreOnBoardWithKomi")); // ("盘面目数差");
    rdoScoreOnBoardWithKomi.setBounds(
        Lizzie.config.isFrameFontSmall() ? 240 : (Lizzie.config.isFrameFontMiddle() ? 310 : 380),
        88,
        Lizzie.config.isFrameFontSmall() ? 114 : (Lizzie.config.isFrameFontMiddle() ? 114 : 140),
        23);
    panel.add(rdoScoreOnBoardWithKomi);

    JFontRadioButton rdoScoreOnBoardWithOutKomi =
        new JFontRadioButton(
            Lizzie.resourceBundle.getString(
                "FirstUseSettings.rdoScoreOnBoardJustScore")); // ("计算贴目后目数差");
    rdoScoreOnBoardWithOutKomi.setBounds(
        Lizzie.config.isFrameFontSmall() ? 350 : (Lizzie.config.isFrameFontMiddle() ? 420 : 520),
        88,
        422,
        23);
    panel.add(rdoScoreOnBoardWithOutKomi);

    ButtonGroup group3 = new ButtonGroup();
    group3.add(rdoScoreOnBoardWithKomi);
    group3.add(rdoScoreOnBoardWithOutKomi);

    JFontLabel lblWinratePerspective =
        new JFontLabel(
            Lizzie.resourceBundle.getString(
                "FirstUseSettings.lblWinratePerspective")); // ("目差在胜率图上显示为:");
    lblWinratePerspective.setBounds(10, 126, 337, 22);
    panel.add(lblWinratePerspective);

    JFontRadioButton rdoAlwaysBlack =
        new JFontRadioButton(
            Lizzie.resourceBundle.getString("FirstUseSettings.rdoAlwaysBlack")); // ("盘面目数差");
    rdoAlwaysBlack.setBounds(
        Lizzie.config.isFrameFontSmall() ? 240 : (Lizzie.config.isFrameFontMiddle() ? 310 : 380),
        128,
        Lizzie.config.isFrameFontSmall() ? 114 : (Lizzie.config.isFrameFontMiddle() ? 114 : 140),
        23);
    panel.add(rdoAlwaysBlack);

    JFontRadioButton rdoAlternately =
        new JFontRadioButton(
            Lizzie.resourceBundle.getString("FirstUseSettings.rdoAlternately")); // ("计算贴目后目数差");
    rdoAlternately.setBounds(
        Lizzie.config.isFrameFontSmall() ? 350 : (Lizzie.config.isFrameFontMiddle() ? 420 : 520),
        128,
        422,
        23);
    panel.add(rdoAlternately);

    ButtonGroup group4 = new ButtonGroup();
    group4.add(rdoAlwaysBlack);
    group4.add(rdoAlternately);

    JFontLabel lblLizzieCache =
        new JFontLabel(
            Lizzie.resourceBundle.getString(
                "FirstUseSettings.lblLizzieCache")); // ("是否启用Lizzie缓存");
    lblLizzieCache.setBounds(10, 166, 285, 22);
    panel.add(lblLizzieCache);

    JFontRadioButton rdoLizzieCacheEnable =
        new JFontRadioButton(
            Lizzie.resourceBundle.getString("FirstUseSettings.rdoLizzieCacheEnable")); // ("启用");
    rdoLizzieCacheEnable.setBounds(
        Lizzie.config.isFrameFontSmall() ? 240 : (Lizzie.config.isFrameFontMiddle() ? 310 : 380),
        168,
        107,
        23);
    panel.add(rdoLizzieCacheEnable);

    JFontRadioButton rdoLizzieCacheDisable =
        new JFontRadioButton(
            Lizzie.resourceBundle.getString("FirstUseSettings.rdoLizzieCacheDisable")); // ("不启用");
    rdoLizzieCacheDisable.setBounds(
        Lizzie.config.isFrameFontSmall() ? 350 : (Lizzie.config.isFrameFontMiddle() ? 420 : 520),
        168,
        121,
        23);
    panel.add(rdoLizzieCacheDisable);

    ButtonGroup group5 = new ButtonGroup();
    group5.add(rdoLizzieCacheEnable);
    group5.add(rdoLizzieCacheDisable);

    JFontButton btnLizzieCacheHelper = new JFontButton(iconSettings);
    btnLizzieCacheHelper.setBounds(
        Lizzie.config.isFrameFontSmall() ? 120 : (Lizzie.config.isFrameFontMiddle() ? 155 : 187),
        166,
        24,
        23);
    btnLizzieCacheHelper.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            Discribe lizzieCacheDiscribe = new Discribe();
            lizzieCacheDiscribe.setInfo(
                Lizzie.resourceBundle.getString(
                    "FirstUseSettings.lizzieCacheDiscribe") // "回退局面时,有时候引擎已经遗忘了之前的计算结果,会从0开始重新计算。\r\n如启用Lizzie缓存,则界面上依然显示之前的计算结果,直到新的计算结果总计算量超过以前的结算结果为止。"
                ,
                Lizzie.resourceBundle.getString("FirstUseSettings.lizzieCacheDiscribeTitle"),
                thisDialog); // "Lizzie缓存说明");
          }
        });
    panel.add(btnLizzieCacheHelper);

    JFontLabel lblMaxAnalyzeTime =
        new JFontLabel(
            Lizzie.resourceBundle.getString(
                "FirstUseSettings.lblMaxAnalyzeTime")); // "单步最大分析时间(秒)");
    lblMaxAnalyzeTime.setBounds(10, 286, 236, 22);
    panel.add(lblMaxAnalyzeTime);

    JFontButton btnlMaxAnalyzeTimeHelper = new JFontButton(iconSettings);
    btnlMaxAnalyzeTimeHelper.setBounds(
        Lizzie.config.isFrameFontSmall() ? 114 : (Lizzie.config.isFrameFontMiddle() ? 148 : 179),
        286,
        24,
        23);
    btnlMaxAnalyzeTimeHelper.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            Discribe MaxAnalyzeTimeHelp = new Discribe();
            MaxAnalyzeTimeHelp.setInfo(
                Lizzie.resourceBundle.getString("FirstUseSettings.maxAnalyzeTimeDiscribe"),
                Lizzie.resourceBundle.getString("FirstUseSettings.maxAnalyzeTimeDiscribeTitile"),
                thisDialog);
          }
        });
    panel.add(btnlMaxAnalyzeTimeHelper);

    chkLimitTime =
        new JFontCheckBox(Lizzie.resourceBundle.getString("FirstUseSettings.chkLimitTime"));
    panel.add(chkLimitTime);
    chkLimitTime.setBounds(
        Lizzie.config.isFrameFontSmall() ? 160 : (Lizzie.config.isFrameFontMiddle() ? 217 : 277),
        287,
        Lizzie.config.isFrameFontSmall() ? 70 : (Lizzie.config.isFrameFontMiddle() ? 83 : 99),
        20);

    txtMaxAnalyzeTime = new JFontTextField();
    txtMaxAnalyzeTime.setBounds(
        Lizzie.config.isFrameFontSmall() ? 240 : (Lizzie.config.isFrameFontMiddle() ? 310 : 380),
        286,
        66,
        24);
    panel.add(txtMaxAnalyzeTime);

    chkLimitPlayouts =
        new JFontCheckBox(Lizzie.resourceBundle.getString("FirstUseSettings.chkLimitVisits"));
    panel.add(chkLimitPlayouts);
    if (Lizzie.config.isChinese)
      chkLimitPlayouts.setBounds(
          Lizzie.config.isFrameFontSmall() ? 335 : (Lizzie.config.isFrameFontMiddle() ? 405 : 505),
          287,
          Lizzie.config.isFrameFontSmall() ? 70 : (Lizzie.config.isFrameFontMiddle() ? 80 : 92),
          20);
    else
      chkLimitPlayouts.setBounds(
          Lizzie.config.isFrameFontSmall() ? 335 : (Lizzie.config.isFrameFontMiddle() ? 405 : 505),
          287,
          Lizzie.config.isFrameFontSmall() ? 80 : (Lizzie.config.isFrameFontMiddle() ? 92 : 103),
          20);
    txtMaxAnalyzePlayouts = new JFontTextField();
    if (Lizzie.config.isChinese)
      txtMaxAnalyzePlayouts.setBounds(
          Lizzie.config.isFrameFontSmall() ? 407 : (Lizzie.config.isFrameFontMiddle() ? 491 : 598),
          286,
          66,
          24);
    else
      txtMaxAnalyzePlayouts.setBounds(
          Lizzie.config.isFrameFontSmall() ? 420 : (Lizzie.config.isFrameFontMiddle() ? 507 : 617),
          286,
          66,
          24);
    panel.add(txtMaxAnalyzePlayouts);

    chkLimitTime.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            txtMaxAnalyzeTime.setEnabled(chkLimitTime.isSelected());
          }
        });
    chkLimitPlayouts.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            txtMaxAnalyzePlayouts.setEnabled(chkLimitPlayouts.isSelected());
          }
        });

    JFontLabel lblNotice =
        new JFontLabel(
            Lizzie.resourceBundle.getString(
                "FirstUseSettings.lblNotice")); // ("注:0代表不限制,本次设置完成后,可在[菜单-设置-初始化设置]中修改这些选项");
    lblNotice.setBounds(10, 406, 867, 22);
    panel.add(lblNotice);

    JFontLabel lblHint =
        new JFontLabel(
            Lizzie.resourceBundle.getString(
                "FirstUseSettings.lblHint")); // "提示:可多使用鼠标滚轮控制棋局和变化图的进退");
    lblHint.setForeground(Color.RED);
    lblHint.setBounds(10, 431, 867, 22);
    panel.add(lblHint);

    JFontLabel lblSuggestionInfo =
        new JFontLabel(Lizzie.resourceBundle.getString("FirstUseSettings.lblSuggestionInfo"));
    lblSuggestionInfo.setBounds(10, 206, 285, 22);
    panel.add(lblSuggestionInfo);

    chkWinrate = new JFontCheckBox(Lizzie.resourceBundle.getString("FirstUseSettings.chkWinrate"));
    chkWinrate.setBounds(
        Lizzie.config.isFrameFontSmall() ? 240 : (Lizzie.config.isFrameFontMiddle() ? 310 : 380),
        208,
        95,
        23);
    panel.add(chkWinrate);

    chkPlayouts = new JFontCheckBox(Lizzie.resourceBundle.getString("FirstUseSettings.chkVisits"));
    chkPlayouts.setBounds(
        Lizzie.config.isFrameFontSmall() ? 350 : (Lizzie.config.isFrameFontMiddle() ? 420 : 520),
        208,
        95,
        23);
    panel.add(chkPlayouts);

    chkScoreLead =
        new JFontCheckBox(Lizzie.resourceBundle.getString("FirstUseSettings.chkScoreLead"));
    chkScoreLead.setBounds(
        Lizzie.config.isFrameFontSmall() ? 460 : (Lizzie.config.isFrameFontMiddle() ? 530 : 660),
        208,
        125,
        23);
    panel.add(chkScoreLead);

    JFontButton btnCustomOrder =
        new JFontButton(Lizzie.resourceBundle.getString("FirstUseSettings.btnCustomOrder"));
    btnCustomOrder.setMargin(new Insets(0, 0, 0, 0));
    if (Lizzie.config.isChinese)
      btnCustomOrder.setBounds(
          Lizzie.config.isFrameFontSmall() ? 73 : (Lizzie.config.isFrameFontMiddle() ? 85 : 105),
          205,
          Lizzie.config.isFrameFontSmall() ? 101 : (Lizzie.config.isFrameFontMiddle() ? 130 : 160),
          24);
    else
      btnCustomOrder.setBounds(
          Lizzie.config.isFrameFontSmall() ? 99 : (Lizzie.config.isFrameFontMiddle() ? 130 : 155),
          205,
          Lizzie.config.isFrameFontSmall() ? 131 : (Lizzie.config.isFrameFontMiddle() ? 160 : 200),
          24);
    btnCustomOrder.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            LizzieFrame.openSuggestionInfoCustom(thisDialog);
          }
        });
    panel.add(btnCustomOrder);

    setChkSuggestionInfo();

    JFontButton defaultSettings =
        new JFontButton(
            Lizzie.resourceBundle.getString("FirstUseSettings.defaultSettings")); // ("加载默认设置");
    defaultSettings.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            chkPlayouts.setSelected(true);
            chkScoreLead.setSelected(true);
            chkWinrate.setSelected(true);
            rdoLizzieCacheEnable.setSelected(true);
            Locale locale = Locale.getDefault();
            if (!rdoLanguageChinese.isSelected() && !rdoLanguageEnglish.isSelected()) {
              if (Lizzie.config.isChinese || locale.getLanguage().equals("zh"))
                rdoLanguageChinese.setSelected(true);
              else if (locale.getLanguage().equals("ko")) rdoLanguageKorean.setSelected(true);
              else if (locale.getLanguage().equals("ja")) rdoLanguageJapanese.setSelected(true);
              else rdoLanguageEnglish.setSelected(true);
            }
            rdoMouseOverSubboardNoRefresh.setSelected(true);
            rdoMouseOverSuggestionNoRefresh.setSelected(true);
            rdoScoreOnBoardWithOutKomi.setSelected(true);
            rdoAlternately.setSelected(true);
            if (OS.isWindows()) rdoSysLooks.setSelected(true);
            else rdoJavaLooks.setSelected(true);
            txtLimitSuggestion.setText("10");
            txtLimitVariation.setText("0");
            chkLimitTime.setSelected(true);
            txtMaxAnalyzeTime.setEnabled(true);
            txtMaxAnalyzeTime.setText("600");
            chkLimitPlayouts.setSelected(false);
            txtMaxAnalyzePlayouts.setEnabled(false);
            txtMaxAnalyzePlayouts.setText("");
          }
        });
    defaultSettings.setBounds(
        Lizzie.config.isFrameFontSmall() ? 209 : (Lizzie.config.isFrameFontMiddle() ? 235 : 260),
        461,
        Lizzie.config.isFrameFontSmall() ? 116 : (Lizzie.config.isFrameFontMiddle() ? 130 : 165),
        25);
    panel.add(defaultSettings);

    JFontButton btnApply =
        new JFontButton(Lizzie.resourceBundle.getString("FirstUseSettings.btnApply")); // ("确定");
    btnApply.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            if (firstTime
                && !chkPlayouts.isSelected()
                && !chkScoreLead.isSelected()
                && !chkWinrate.isSelected()) {
              Utils.showMsg(
                  Lizzie.resourceBundle.getString(
                      "FirstUseSettings.confirmHintNoSuggestionInfo")); // ("至少选择一项选点信息");
              return;
            }
            if (!rdoSysLooks.isSelected() && !rdoJavaLooks.isSelected()) {
              Utils.showMsg(
                  Lizzie.resourceBundle.getString("FirstUseSettings.confirmHint")); // ("有未选择的选项");
              return;
            }
            if (!rdoLizzieCacheDisable.isSelected() && !rdoLizzieCacheEnable.isSelected()) {
              Utils.showMsg(
                  Lizzie.resourceBundle.getString("FirstUseSettings.confirmHint")); // ("有未选择的选项");
              return;
            }
            if (!rdoMouseOverSubboardNoRefresh.isSelected()
                && !rdoMouseOverSubboardRefresh.isSelected()) {
              Utils.showMsg(
                  Lizzie.resourceBundle.getString("FirstUseSettings.confirmHint")); // ("有未选择的选项");
              return;
            }
            if (!rdoMouseOverSuggestionNoRefresh.isSelected()
                && !rdoMouseOverSuggestionRefresh.isSelected()) {
              Utils.showMsg(
                  Lizzie.resourceBundle.getString("FirstUseSettings.confirmHint")); // ("有未选择的选项");
              return;
            }
            if (!rdoScoreOnBoardWithOutKomi.isSelected() && !rdoScoreOnBoardWithKomi.isSelected()) {
              Utils.showMsg(
                  Lizzie.resourceBundle.getString("FirstUseSettings.confirmHint")); // ("有未选择的选项");
              return;
            }
            if (!rdoAlwaysBlack.isSelected() && !rdoAlternately.isSelected()) {
              Utils.showMsg(
                  Lizzie.resourceBundle.getString("FirstUseSettings.confirmHint")); // ("有未选择的选项");
              return;
            }
            if (!rdoLanguageChinese.isSelected()
                && !rdoLanguageEnglish.isSelected()
                && !rdoLanguageKorean.isSelected()
                && !rdoLanguageJapanese.isSelected()) {
              Utils.showMsg(
                  Lizzie.resourceBundle.getString("FirstUseSettings.confirmHint")); // ("有未选择的选项");
              return;
            }
            boolean shouldShowRestartHint = false;
            Lizzie.config.showWinrateInSuggestion = chkWinrate.isSelected();
            Lizzie.config.showPlayoutsInSuggestion = chkPlayouts.isSelected();
            Lizzie.config.showScoremeanInSuggestion = chkScoreLead.isSelected();
            Lizzie.config.uiConfig.put(
                "show-winrate-in-suggestion", Lizzie.config.showWinrateInSuggestion);
            Lizzie.config.uiConfig.put(
                "show-playouts-in-suggestion", Lizzie.config.showPlayoutsInSuggestion);
            Lizzie.config.uiConfig.put(
                "show-scoremean-in-suggestion", Lizzie.config.showScoremeanInSuggestion);
            if (Lizzie.frame != null && LizzieFrame.menu != null) {
              LizzieFrame.menu.chkShowWinrate.setSelected(Lizzie.config.showWinrateInSuggestion);
              LizzieFrame.menu.chkShowPlayouts.setSelected(Lizzie.config.showPlayoutsInSuggestion);
              LizzieFrame.menu.chkShowScore.setSelected(Lizzie.config.showScoremeanInSuggestion);
            }
            int limitVariation = 0;
            int limitSuggestion = 0;
            int maxAnalyzeTime = (int) (Lizzie.config.maxAnalyzeTimeMillis / 1000);
            long maxAnalyzePlayouts = Lizzie.config.limitPlayouts;
            try {
              limitVariation = Integer.parseInt(txtLimitVariation.getText());
              limitSuggestion = Integer.parseInt(txtLimitSuggestion.getText());
              if (chkLimitTime.isSelected())
                maxAnalyzeTime = Integer.parseInt(txtMaxAnalyzeTime.getText());
              if (chkLimitPlayouts.isSelected())
                maxAnalyzePlayouts = Long.parseLong(txtMaxAnalyzePlayouts.getText());
            } catch (Exception ex) {
              Utils.showMsg(Lizzie.resourceBundle.getString("FirstUseSettings.confirmHint2"));
              return;
            }
            try {
              if (!chkLimitTime.isSelected())
                maxAnalyzeTime = Integer.parseInt(txtMaxAnalyzeTime.getText());
              if (!chkLimitPlayouts.isSelected())
                maxAnalyzePlayouts = Long.parseLong(txtMaxAnalyzePlayouts.getText());
            } catch (Exception ex) {
            }

            if (rdoLanguageChinese.isSelected()) {
              Lizzie.config.useLanguage = 1;
              Lizzie.config.uiConfig.put("use-language", Lizzie.config.useLanguage);
            } else if (rdoLanguageEnglish.isSelected()) {
              Lizzie.config.useLanguage = 2;
              Lizzie.config.uiConfig.put("use-language", Lizzie.config.useLanguage);
            } else if (rdoLanguageKorean.isSelected()) {
              Lizzie.config.useLanguage = 3;
              Lizzie.config.uiConfig.put("use-language", Lizzie.config.useLanguage);
            } else if (rdoLanguageJapanese.isSelected()) {
              Lizzie.config.useLanguage = 4;
              Lizzie.config.uiConfig.put("use-language", Lizzie.config.useLanguage);
            }
            boolean oriUseJavaLooks = Lizzie.config.useJavaLooks;
            Lizzie.config.useJavaLooks = rdoJavaLooks.isSelected();
            Lizzie.config.uiConfig.put("use-java-looks", Lizzie.config.useJavaLooks);

            shouldShowRestartHint = oriUseJavaLooks ^ Lizzie.config.useJavaLooks;
            Lizzie.config.limitTime = chkLimitTime.isSelected();
            Lizzie.config.limitPlayout = chkLimitPlayouts.isSelected();
            Lizzie.config.uiConfig.put("limit-playout", Lizzie.config.limitPlayout);
            Lizzie.config.uiConfig.put("limit-time", Lizzie.config.limitTime);

            Lizzie.config.maxAnalyzeTimeMillis = 1000 * maxAnalyzeTime;
            if (Lizzie.config.maxAnalyzeTimeMillis <= 0) {
              Lizzie.config.maxAnalyzeTimeMillis = 9999 * 60 * 1000;
            }
            Lizzie.config.leelazConfig.putOpt(
                "max-analyze-time-seconds", Lizzie.config.maxAnalyzeTimeMillis / 1000);

            Lizzie.config.limitPlayouts = maxAnalyzePlayouts;
            Lizzie.config.uiConfig.put("limit-playouts", Lizzie.config.limitPlayouts);

            if (rdoLizzieCacheEnable.isSelected()) Lizzie.config.enableLizzieCache = true;
            else Lizzie.config.enableLizzieCache = false;
            Lizzie.config.leelazConfig.put("enable-lizzie-cache", Lizzie.config.enableLizzieCache);

            if (rdoMouseOverSuggestionNoRefresh.isSelected())
              Lizzie.config.noRefreshOnMouseMove = true;
            else Lizzie.config.noRefreshOnMouseMove = false;
            Lizzie.config.uiConfig.putOpt(
                "norefresh-onmouse-move", Lizzie.config.noRefreshOnMouseMove);

            if (rdoMouseOverSubboardNoRefresh.isSelected()) Lizzie.config.noRefreshOnSub = true;
            else Lizzie.config.noRefreshOnSub = false;
            Lizzie.config.uiConfig.put("no-refresh-on-sub", Lizzie.config.noRefreshOnSub);

            if (rdoScoreOnBoardWithKomi.isSelected())
              Lizzie.config.showKataGoScoreLeadWithKomi = true;
            else Lizzie.config.showKataGoScoreLeadWithKomi = false;
            Lizzie.config.uiConfig.put(
                "show-katago-score-lead-with-komi", Lizzie.config.showKataGoScoreLeadWithKomi);

            if (rdoAlwaysBlack.isSelected()) Lizzie.config.winrateAlwaysBlack = true;
            else Lizzie.config.winrateAlwaysBlack = false;
            Lizzie.config.uiConfig.put("win-rate-always-black", Lizzie.config.winrateAlwaysBlack);

            Lizzie.config.limitMaxSuggestion = limitSuggestion;
            Lizzie.config.leelazConfig.put(
                "limit-max-suggestion", Lizzie.config.limitMaxSuggestion);
            Lizzie.config.limitBranchLength = limitVariation;
            Lizzie.config.leelazConfig.put("limit-branch-length", Lizzie.config.limitBranchLength);
            Lizzie.config.firstTimeLoad = false;
            Lizzie.config.uiConfig.put("first-time-load", false);
            Lizzie.config.needReopenFirstUseSettings = false;
            setVisible(false);
            if (Lizzie.frame != null) LizzieFrame.menu.refreshLimitStatus(true);
            try {
              Lizzie.config.save();
            } catch (IOException e1) {
              // TODO Auto-generated catch block
              e1.printStackTrace();
            }
            if (!firstTime && Lizzie.leelaz.isPondering()) Lizzie.leelaz.ponder();
            dispose();
            if (!firstTime && shouldShowRestartHint) {
              Utils.showMsg(Lizzie.resourceBundle.getString("Lizzie.hint.restartForPartChanges"));
            }
            if (firstTime) Lizzie.resetLookAndFeel();
            else Lizzie.frame.refresh();
          }
        });
    btnApply.setBounds(
        Lizzie.config.isFrameFontSmall() ? 332 : (Lizzie.config.isFrameFontMiddle() ? 385 : 440),
        461,
        Lizzie.config.isFrameFontSmall() ? 116 : (Lizzie.config.isFrameFontMiddle() ? 130 : 165),
        25);
    panel.add(btnApply);

    if (!firstTime) {
      if (Lizzie.config.enableLizzieCache) rdoLizzieCacheEnable.setSelected(true);
      else rdoLizzieCacheDisable.setSelected(true);
      if (Lizzie.config.noRefreshOnMouseMove) rdoMouseOverSuggestionNoRefresh.setSelected(true);
      else rdoMouseOverSuggestionRefresh.setSelected(true);
      if (Lizzie.config.noRefreshOnSub) rdoMouseOverSubboardNoRefresh.setSelected(true);
      else rdoMouseOverSubboardRefresh.setSelected(true);
      if (Lizzie.config.showKataGoScoreLeadWithKomi) rdoScoreOnBoardWithKomi.setSelected(true);
      else rdoScoreOnBoardWithOutKomi.setSelected(true);
      if (Lizzie.config.winrateAlwaysBlack) rdoAlwaysBlack.setSelected(true);
      else rdoAlternately.setSelected(true);
      if (Lizzie.config.useJavaLooks) rdoJavaLooks.setSelected(true);
      else rdoSysLooks.setSelected(true);
      if (Lizzie.config.enableLizzieCache) rdoLizzieCacheEnable.setSelected(true);
      else rdoLizzieCacheDisable.setSelected(true);
      txtLimitVariation.setText(String.valueOf(Lizzie.config.limitBranchLength));
      txtLimitSuggestion.setText(String.valueOf(Lizzie.config.limitMaxSuggestion));
      txtMaxAnalyzeTime.setText(String.valueOf(Lizzie.config.maxAnalyzeTimeMillis / 1000));
      txtMaxAnalyzePlayouts.setText(String.valueOf(Lizzie.config.limitPlayouts));
      chkLimitPlayouts.setSelected(Lizzie.config.limitPlayout);
      chkLimitTime.setSelected(Lizzie.config.limitTime);
      txtMaxAnalyzeTime.setEnabled(Lizzie.config.limitTime);
      txtMaxAnalyzePlayouts.setEnabled(Lizzie.config.limitPlayout);
    } else {
      chkLimitPlayouts.setSelected(false);
      chkLimitTime.setSelected(false);
      txtMaxAnalyzeTime.setEnabled(false);
      txtMaxAnalyzePlayouts.setEnabled(false);
      addWindowListener(
          new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
              System.exit(0);
            }
          });
    }
    if (Lizzie.config.isChinese) rdoLanguageChinese.setSelected(true);
    else if (Lizzie.config.useLanguage == 3
        || Lizzie.config.useLanguage == 0 && Locale.getDefault().equals("ko"))
      rdoLanguageKorean.setSelected(true);
    else if (Lizzie.config.useLanguage == 4
        || Lizzie.config.useLanguage == 0 && Locale.getDefault().equals("ja"))
      rdoLanguageJapanese.setSelected(true);
    else rdoLanguageEnglish.setSelected(true);

    rdoLanguageChinese.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            if (firstTime) {
              Lizzie.config.needReopenFirstUseSettings = true;
              Lizzie.config.useLanguage = 1;
              Lizzie.config.uiConfig.put("use-language", Lizzie.config.useLanguage);
              setVisible(false);
            } else {
              Lizzie.config.useLanguage = 1;
              Lizzie.config.uiConfig.put("use-language", Lizzie.config.useLanguage);
              Lizzie.resourceBundle =
                  ResourceBundle.getBundle("l10n.DisplayStrings", new Locale("zh", "CN"));
              Lizzie.config.isChinese = true;
              setVisible(false);
              FirstUseSettings firstUseSettings = new FirstUseSettings(false);
              firstUseSettings.setVisible(true);
            }
          }
        });

    rdoLanguageEnglish.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            if (firstTime) {
              Lizzie.config.needReopenFirstUseSettings = true;
              Lizzie.config.useLanguage = 2;
              Lizzie.config.uiConfig.put("use-language", Lizzie.config.useLanguage);
              setVisible(false);
            } else {
              Lizzie.config.useLanguage = 2;
              Lizzie.config.uiConfig.put("use-language", Lizzie.config.useLanguage);
              Lizzie.resourceBundle =
                  ResourceBundle.getBundle("l10n.DisplayStrings", new Locale("en", "US"));
              Lizzie.config.isChinese = false;
              setVisible(false);
              FirstUseSettings firstUseSettings = new FirstUseSettings(false);
              firstUseSettings.setVisible(true);
            }
          }
        });

    rdoLanguageKorean.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            if (firstTime) {
              Lizzie.config.needReopenFirstUseSettings = true;
              Lizzie.config.useLanguage = 3;
              Lizzie.config.uiConfig.put("use-language", Lizzie.config.useLanguage);
              setVisible(false);
            } else {
              Lizzie.config.useLanguage = 3;
              Lizzie.config.uiConfig.put("use-language", Lizzie.config.useLanguage);
              Lizzie.resourceBundle =
                  ResourceBundle.getBundle("l10n.DisplayStrings", new Locale("ko"));
              Lizzie.config.isChinese = false;
              setVisible(false);
              FirstUseSettings firstUseSettings = new FirstUseSettings(false);
              firstUseSettings.setVisible(true);
            }
          }
        });

    rdoLanguageJapanese.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            if (firstTime) {
              Lizzie.config.needReopenFirstUseSettings = true;
              Lizzie.config.useLanguage = 4;
              Lizzie.config.uiConfig.put("use-language", Lizzie.config.useLanguage);
              setVisible(false);
            } else {
              Lizzie.config.useLanguage = 4;
              Lizzie.config.uiConfig.put("use-language", Lizzie.config.useLanguage);
              Lizzie.resourceBundle =
                  ResourceBundle.getBundle("l10n.DisplayStrings", new Locale("ja", "JP"));
              Lizzie.config.isChinese = false;
              setVisible(false);
              FirstUseSettings firstUseSettings = new FirstUseSettings(false);
              firstUseSettings.setVisible(true);
            }
          }
        });
  }

  public void setChkSuggestionInfo() {
    // TODO Auto-generated method stub
    chkWinrate.setSelected(Lizzie.config.showWinrateInSuggestion);
    chkPlayouts.setSelected(Lizzie.config.showPlayoutsInSuggestion);
    chkScoreLead.setSelected(Lizzie.config.showScoremeanInSuggestion);
  }
}
