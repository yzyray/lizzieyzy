package featurecat.lizzie.gui;

import featurecat.lizzie.Config;
import featurecat.lizzie.Lizzie;
import featurecat.lizzie.gui.LizzieFrame.HtmlKit;
import featurecat.lizzie.util.Utils;
import java.awt.Desktop;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.StyleSheet;

public class AnalysisSettings extends JDialog {
  private JTextField txtMaxVisits;
  private JRadioButton rdoUseCurrentRules;
  private JRadioButton rdoUseSpecificRules;
  private JFontTextArea engineCmd;
  private JFontCheckBox chkPreLoad;
  private JFontCheckBox chkAlwaysOverride;
  private JFontCheckBox chkAutoExit;
  private JDialog dialog = this;
  private JFontCheckBox chkUseJavaSSH;

  public AnalysisSettings(boolean isDuringAnalyze, boolean fromError) {
    this.setModal(true);
    this.setAlwaysOnTop(Lizzie.frame.isAlwaysOnTop());
    setResizable(false);
    setTitle(Lizzie.resourceBundle.getString("AnalysisSettings.title")); // ("闪电分析设置");
    // setSize(609, 367);
    Lizzie.setFrameSize(this, 592, 385);
    getContentPane().setLayout(null);

    JLabel lblEngineCmd =
        new JFontLabel(
            Lizzie.resourceBundle.getString("AnalysisSettings.lblEngineCmd")); // ("分析引擎命令:");
    lblEngineCmd.setBounds(10, 1, 169, 22);
    getContentPane().add(lblEngineCmd);

    engineCmd = new JFontTextArea();
    engineCmd.setBounds(10, 26, 566, 130);
    engineCmd.setFont(new Font(Config.sysDefaultFontName, Font.PLAIN, Config.frameFontSize));
    getContentPane().add(engineCmd);

    JLabel example =
        new JLabel(
            Lizzie.resourceBundle.getString(
                "AnalysisSettings.example")); // ("例:katago analysis -model model.bin.gz -config
    // analysis.cfg -quit-without-waiting");
    example.setBounds(10, 158, 567, 20);
    getContentPane().add(example);

    JLabel lblMaxVisits =
        new JFontLabel(
            Lizzie.resourceBundle.getString("AnalysisSettings.lblMaxVisits")); // ("单步计算量:");
    lblMaxVisits.setBounds(10, 207, 136, 20);
    getContentPane().add(lblMaxVisits);

    txtMaxVisits = new JFontTextField();
    txtMaxVisits.setBounds(
        Lizzie.config.isFrameFontSmall() ? 80 : (Lizzie.config.isFrameFontMiddle() ? 100 : 120),
        205,
        66,
        24);
    getContentPane().add(txtMaxVisits);

    JLabel lblRules =
        new JFontLabel(Lizzie.resourceBundle.getString("AnalysisSettings.lblRules")); // ("规则:");
    lblRules.setBounds(10, 232, 54, 20);
    getContentPane().add(lblRules);

    rdoUseCurrentRules =
        new JFontRadioButton(
            Lizzie.resourceBundle.getString(
                "AnalysisSettings.rdoUseCurrentRules")); // ("使用当前引擎规则(未指定规则将使用中国规则)");
    rdoUseCurrentRules.setBounds(59, 231, 510, 23);
    getContentPane().add(rdoUseCurrentRules);

    rdoUseSpecificRules =
        new JFontRadioButton(
            Lizzie.resourceBundle.getString("AnalysisSettings.rdoUseSpecificRules")); // ("使用指定规则");
    rdoUseSpecificRules.setBounds(
        59,
        256,
        Lizzie.config.isFrameFontSmall() ? 131 : (Lizzie.config.isFrameFontMiddle() ? 131 : 150),
        23);
    getContentPane().add(rdoUseSpecificRules);

    ButtonGroup group = new ButtonGroup();
    group.add(rdoUseSpecificRules);
    group.add(rdoUseCurrentRules);

    chkAlwaysOverride =
        new JFontCheckBox(
            Lizzie.resourceBundle.getString(
                "AnalysisSettings.chkAlwaysOverride")); // ("总是覆盖已有分析结果");
    chkAlwaysOverride.setBounds(10, 281, 370, 23);
    getContentPane().add(chkAlwaysOverride);

    chkPreLoad =
        new JFontCheckBox(
            Lizzie.resourceBundle.getString("AnalysisSettings.chkPreLoad")); // ("启动Lizzie时预加载引擎");
    chkPreLoad.setBounds(10, 306, 304, 23);
    getContentPane().add(chkPreLoad);

    chkAutoExit =
        new JFontCheckBox(
            Lizzie.resourceBundle.getString("AnalysisSettings.chkAutoExit")); // ("分析完毕后关闭引擎");
    chkAutoExit.setBounds(10, 331, 304, 23);
    getContentPane().add(chkAutoExit);

    JButton btnSetRules =
        new JFontButton(
            Lizzie.resourceBundle.getString("AnalysisSettings.btnSetRules")); // ("设置规则");
    btnSetRules.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            SetAnalysisRules setAnalysisRules = new SetAnalysisRules();
            setAnalysisRules.setVisible(true);
          }
        });
    btnSetRules.setMargin(new Insets(0, 0, 0, 0));
    btnSetRules.setBounds(
        Lizzie.config.isFrameFontSmall() ? 191 : (Lizzie.config.isFrameFontMiddle() ? 191 : 211),
        256,
        Lizzie.config.isFrameFontSmall() ? 93 : (Lizzie.config.isFrameFontMiddle() ? 105 : 121),
        23);
    getContentPane().add(btnSetRules);

    JButton btnConfirmAndRedo =
        new JFontButton(
            Lizzie.resourceBundle.getString("AnalysisSettings.btnConfirmAndRedo")); // ("确定并重新计算");
    btnConfirmAndRedo.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            {
              Lizzie.frame.analysisEngine.waitFrame.setVisible(false);
              saveConfig();
              setVisible(false);
              Lizzie.frame.destroyAnalysisEngine();
              Lizzie.frame.flashAnalyzeGame(Lizzie.config.analysisRecentIsPartGame);
            }
          }
        });
    btnConfirmAndRedo.setMargin(new Insets(0, 0, 0, 0));
    btnConfirmAndRedo.setBounds(
        Lizzie.config.isFrameFontSmall() ? 375 : (Lizzie.config.isFrameFontMiddle() ? 355 : 325),
        321,
        Lizzie.config.isFrameFontSmall() ? 99 : (Lizzie.config.isFrameFontMiddle() ? 120 : 150),
        31);
    btnConfirmAndRedo.setVisible(isDuringAnalyze);
    getContentPane().add(btnConfirmAndRedo);

    JButton btnConfirm =
        new JFontButton(Lizzie.resourceBundle.getString("AnalysisSettings.btnConfirm")); // ("确定");
    btnConfirm.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            if (fromError) {
              Lizzie.frame.analysisEngine.waitFrame.setVisible(false);
              saveConfig();
              setVisible(false);
              Lizzie.frame.destroyAnalysisEngine();
              Lizzie.frame.flashAnalyzeGame(Lizzie.config.analysisRecentIsPartGame);
            } else {
              saveConfig();
              setVisible(false);
            }
          }
        });
    btnConfirm.setBounds(484, 321, 93, 31);
    getContentPane().add(btnConfirm);

    LinkLabel lblHint2 =
        new LinkLabel(Lizzie.resourceBundle.getString("AnalysisSettings.lblHint2"));
    lblHint2.setBounds(7, 177, 633, 20);
    getContentPane().add(lblHint2);

    txtMaxVisits.setText(
        (Lizzie.frame.isBatchAnalysisMode
                ? Lizzie.config.batchAnalysisPlayouts
                : Lizzie.config.analysisMaxVisits)
            + "");
    engineCmd.setText(Lizzie.config.analysisEngineCommand);

    if (Lizzie.config.analysisUseCurrentRules) rdoUseCurrentRules.setSelected(true);
    else rdoUseSpecificRules.setSelected(true);

    chkAutoExit.setSelected(Lizzie.config.analysisAutoQuit);
    chkPreLoad.setSelected(Lizzie.config.analysisEnginePreLoad);
    chkAlwaysOverride.setSelected(Lizzie.config.analysisAlwaysOverride);

    JButton btnGenerate =
        new JFontButton(
            Lizzie.resourceBundle.getString("SetEstimateParam.btnGenerate")); // "自动生成");
    btnGenerate.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            GetEngineLine getEngineLine = new GetEngineLine();
            String el = getEngineLine.getEngineLine(dialog, true, true);
            if (!el.isEmpty()) {
              engineCmd.setText(el);
            }
          }
        });
    btnGenerate.setMargin(new Insets(0, 0, 0, 0));
    btnGenerate.setBounds(
        Lizzie.config.isFrameFontSmall() ? 93 : (Lizzie.config.isFrameFontMiddle() ? 110 : 140),
        1,
        Lizzie.config.isFrameFontSmall() ? 80 : (Lizzie.config.isFrameFontMiddle() ? 95 : 110),
        23);
    btnGenerate.setFocusable(false);
    getContentPane().add(btnGenerate);

    chkUseJavaSSH =
        new JFontCheckBox(Lizzie.resourceBundle.getString("MoreEngines.chkRemoteEngine"));
    JFontButton setRemoteEngine =
        new JFontButton(Lizzie.resourceBundle.getString("SetEstimateParam.setRemoteEngine"));
    setRemoteEngine.setMargin(new Insets(0, 0, 0, 0));
    chkUseJavaSSH.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            setRemoteEngine.setEnabled(chkUseJavaSSH.isSelected());
          }
        });
    setRemoteEngine.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            RemoteEngineSettings remoteEngineSettings = new RemoteEngineSettings(dialog, true);
            remoteEngineSettings.setVisible(true);
          }
        });
    chkUseJavaSSH.setSelected(Utils.getAnalysisEngineRemoteEngineData().useJavaSSH);
    setRemoteEngine.setEnabled(chkUseJavaSSH.isSelected());

    chkUseJavaSSH.setBounds(
        Lizzie.config.isFrameFontSmall() ? 200 : (Lizzie.config.isFrameFontMiddle() ? 230 : 260),
        1,
        Lizzie.config.isFrameFontSmall() ? 80 : (Lizzie.config.isFrameFontMiddle() ? 90 : 110),
        22);
    setRemoteEngine.setBounds(
        Lizzie.config.isFrameFontSmall() ? 280 : (Lizzie.config.isFrameFontMiddle() ? 320 : 370),
        1,
        Lizzie.config.isFrameFontSmall() ? 80 : (Lizzie.config.isFrameFontMiddle() ? 80 : 80),
        23);

    getContentPane().add(chkUseJavaSSH);
    getContentPane().add(setRemoteEngine);
    setLocationRelativeTo(Lizzie.frame != null ? Lizzie.frame : null);
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
      // setFont(new Font(Config.sysDefaultFontName, Font.PLAIN, Config.frameFontSize));
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

  public void saveConfig() {
    RemoteEngineData remoteEngineData = Utils.getAnalysisEngineRemoteEngineData();
    remoteEngineData.useJavaSSH = chkUseJavaSSH.isSelected();
    Utils.saveAnalysisEngineRemoteEngineData(remoteEngineData);
    Lizzie.config.analysisEngineCommand = engineCmd.getText().trim();
    if (Lizzie.frame.isBatchAnalysisMode) {
      Lizzie.config.batchAnalysisPlayouts =
          Utils.parseTextToInt(txtMaxVisits, Lizzie.config.batchAnalysisPlayouts);
      Lizzie.config.uiConfig.put("batch-analysis-playouts", Lizzie.config.batchAnalysisPlayouts);
    } else {
      Lizzie.config.analysisMaxVisits =
          Utils.parseTextToInt(txtMaxVisits, Lizzie.config.analysisMaxVisits);
      Lizzie.config.uiConfig.put("analysis-max-visits", Lizzie.config.analysisMaxVisits);
    }
    //    if (Lizzie.config.analysisMaxVisits == 1)
    //      Utils.showMsg(
    //          Lizzie.resourceBundle.getString(
    //              "AnalysisSettings.maxVisits1Hint")); // ("单步计算量最小为2,当前设置为1,将自动调整为2");
    if (Lizzie.config.analysisMaxVisits <= 1) Lizzie.config.analysisMaxVisits = 1;
    Lizzie.config.analysisAutoQuit = chkAutoExit.isSelected();
    Lizzie.config.analysisEnginePreLoad = chkPreLoad.isSelected();
    Lizzie.config.analysisAlwaysOverride = chkAlwaysOverride.isSelected();
    Lizzie.config.analysisUseCurrentRules = rdoUseCurrentRules.isSelected();
    Lizzie.config.uiConfig.put("analysis-auto-quit", Lizzie.config.analysisAutoQuit);
    Lizzie.config.uiConfig.put("analysis-engine-preload", Lizzie.config.analysisEnginePreLoad);
    Lizzie.config.uiConfig.put("analysis-always-override", Lizzie.config.analysisAlwaysOverride);
    Lizzie.config.uiConfig.put("analysis-use-current-rules", Lizzie.config.analysisUseCurrentRules);
    Lizzie.config.uiConfig.put("analysis-engine-command", Lizzie.config.analysisEngineCommand);
  }
}
