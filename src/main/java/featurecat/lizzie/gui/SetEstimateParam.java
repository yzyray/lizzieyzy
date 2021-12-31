package featurecat.lizzie.gui;

import featurecat.lizzie.Lizzie;
import featurecat.lizzie.util.Utils;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ResourceBundle;
import javax.imageio.ImageIO;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JRadioButton;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class SetEstimateParam extends JDialog {
  private JTextField txtThreshold;
  private JTextArea textAreaZen;
  private JRadioButton rdoKataGo;
  private JRadioButton rdoZen;
  private JDialog dialog = this;
  private final ResourceBundle resourceBundle = Lizzie.resourceBundle;

  public SetEstimateParam() {

    setResizable(false);
    setLocationRelativeTo(Lizzie.frame);
    //    this.setModal(true);
    getContentPane().setLayout(null);

    JLabel lblKatago =
        new JLabel(
            resourceBundle.getString(
                "SetEstimateParam.lblKatago")); // "KataGo 引擎加载命令行(需v1.33版本):");
    lblKatago.setBounds(10, 131, 262, 26);
    getContentPane().add(lblKatago);

    JTextArea textAreaCommand = new JFontTextArea();
    textAreaCommand.setBounds(10, 159, 464, 102);
    textAreaCommand.setLineWrap(true);
    getContentPane().add(textAreaCommand);

    JLabel lblThreshold =
        new JLabel(
            resourceBundle.getString("SetEstimateParam.lblThreshold")); // ("阈值(增大为严格,减小为宽松):");
    lblThreshold.setBounds(10, 271, 214, 15);
    getContentPane().add(lblThreshold);

    txtThreshold = new JTextField();
    txtThreshold.setBounds(234, 268, 66, 21);
    getContentPane().add(txtThreshold);
    txtThreshold.setColumns(10);

    JCheckBox chkRemoteEngine =
        new JCheckBox(Lizzie.resourceBundle.getString("MoreEngines.chkRemoteEngine"));
    chkRemoteEngine.setBounds(8, 30, 90, 23);
    getContentPane().add(chkRemoteEngine);

    JButton setRemoteEngine =
        new JButton(Lizzie.resourceBundle.getString("SetEstimateParam.setRemoteEngine"));
    setRemoteEngine.setMargin(new Insets(0, 0, 0, 0));
    setRemoteEngine.setBounds(105, 31, 80, 23);
    getContentPane().add(setRemoteEngine);

    chkRemoteEngine.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            setRemoteEngine.setEnabled(chkRemoteEngine.isSelected());
          }
        });

    setRemoteEngine.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            RemoteEngineSettings remoteEngineSettings =
                new RemoteEngineSettings(dialog, false, false);
            remoteEngineSettings.setVisible(true);
          }
        });
    chkRemoteEngine.setSelected(Utils.getEstimateEngineRemoteEngineData().useJavaSSH);
    setRemoteEngine.setEnabled(chkRemoteEngine.isSelected());

    JButton btnOk = new JButton(resourceBundle.getString("SetEstimateParam.okButton")); // ("确定");
    btnOk.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            RemoteEngineData remoteEngineData = Utils.getEstimateEngineRemoteEngineData();
            remoteEngineData.useJavaSSH = chkRemoteEngine.isSelected();
            Utils.saveEstimateEngineRemoteEngineData(remoteEngineData);
            Lizzie.config.useZenEstimate = rdoZen.isSelected();
            Lizzie.config.zenEstimateCommand = textAreaZen.getText();
            Lizzie.config.uiConfig.put("use-zen-estimate", Lizzie.config.useZenEstimate);
            Lizzie.config.uiConfig.put("use-estimate-command", Lizzie.config.zenEstimateCommand);
            Lizzie.config.estimateThreshold =
                Utils.parseTextToDouble(txtThreshold, Lizzie.config.estimateThreshold);
            Lizzie.config.estimateCommand = textAreaCommand.getText();
            Lizzie.config.uiConfig.put("estimate-command", Lizzie.config.estimateCommand);
            Lizzie.config.uiConfig.put("estimate-threshold", Lizzie.config.estimateThreshold);
            Lizzie.frame.restartZen();
            Lizzie.frame.countstones(false);
          }
        });
    btnOk.setBounds(391, 325, 93, 23);
    getContentPane().add(btnOk);
    setTitle(resourceBundle.getString("SetEstimateParam.title")); // ("设置");
    // setSize(507, 379);
    Lizzie.setFrameSize(this, 507, 389);
    textAreaCommand.setText(Lizzie.config.estimateCommand);
    txtThreshold.setText(String.valueOf(Lizzie.config.estimateThreshold));
    JTextArea lblHint = new JTextArea();
    lblHint.setBackground(this.getBackground());
    lblHint.setText(resourceBundle.getString("SetEstimateParam.lblHint")); // $NON-NLS-1$
    lblHint.setLineWrap(true);
    lblHint.setBounds(10, 288, 464, 50);
    getContentPane().add(lblHint);

    JLabel lblEstmateEngine =
        new JLabel(resourceBundle.getString("SetEstimateParam.lblEstmateEngine")); // $NON-NLS-1$
    lblEstmateEngine.setBounds(10, 10, 107, 15);
    getContentPane().add(lblEstmateEngine);

    rdoZen = new JRadioButton(resourceBundle.getString("SetEstimateParam.rdoZen")); // $NON-NLS-1$
    rdoZen.setBounds(129, 6, 121, 23);
    getContentPane().add(rdoZen);

    rdoKataGo =
        new JRadioButton(resourceBundle.getString("SetEstimateParam.rdoKataGo")); // $NON-NLS-1$
    rdoKataGo.setBounds(272, 6, 121, 23);
    getContentPane().add(rdoKataGo);

    ButtonGroup gp = new ButtonGroup();
    gp.add(rdoKataGo);
    gp.add(rdoZen);

    if (Lizzie.config.useZenEstimate) rdoZen.setSelected(true);
    else rdoKataGo.setSelected(true);

    JLabel lblZenEngineCommand =
        new JLabel(resourceBundle.getString("SetEstimateParam.lblZenEngineCommand")); // $NON-NLS-1$
    lblZenEngineCommand.setBounds(10, 59, 878, 15);
    getContentPane().add(lblZenEngineCommand);

    textAreaZen = new JFontTextArea();
    textAreaZen.setText((String) null);
    textAreaZen.setBounds(10, 76, 464, 55);
    getContentPane().add(textAreaZen);
    textAreaZen.setText(Lizzie.config.zenEstimateCommand);

    JButton btnGenerate =
        new JButton(resourceBundle.getString("SetEstimateParam.btnGenerate")); // "自动生成");
    btnGenerate.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            GetEngineLine getEngineLine = new GetEngineLine();
            String el = getEngineLine.getEngineLine(dialog, true, false, false, false);
            if (!el.isEmpty()) {
              textAreaCommand.setText(el);
            }
          }
        });
    btnGenerate.setBounds(272, 133, 93, 23);
    btnGenerate.setFocusable(false);
    getContentPane().add(btnGenerate);

    setAlwaysOnTop(true);
    try {
      this.setIconImage(ImageIO.read(MoreEngines.class.getResourceAsStream("/assets/logo.png")));

    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
