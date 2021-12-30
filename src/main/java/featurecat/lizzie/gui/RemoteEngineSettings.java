package featurecat.lizzie.gui;

import featurecat.lizzie.Lizzie;
import featurecat.lizzie.util.Utils;
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FileDialog;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.border.EmptyBorder;

public class RemoteEngineSettings extends JDialog {
  private JPanel dialogPane = new JPanel();
  private JPanel contentPanel = new JPanel();
  private JPanel buttonBar = new JPanel();
  private JButton okButton = new JButton();
  private JFontTextField txtIP;
  private JFontTextField txtPort;
  private JFontTextField txtUserName;
  private JFontCheckBox chkUsePassword;
  private JPasswordField txtPassword;
  private JFontCheckBox chkUseKeyGen;
  private String keyGenPath = "";
  private JDialog thisDialog = this;
  private boolean isAnalysisEngine;
  private boolean isContributeEngine;

  public RemoteEngineSettings(JDialog owner, boolean isAnalysisEngine, boolean isContributeEngine) {
    super(owner);
    this.isAnalysisEngine = isAnalysisEngine;
    this.isContributeEngine = isContributeEngine;
    setResizable(false);
    setTitle(Lizzie.resourceBundle.getString("RemoteEngineSettings.title"));
    try {
      this.setIconImage(ImageIO.read(MoreEngines.class.getResourceAsStream("/assets/logo.png")));
    } catch (IOException e) {
      e.printStackTrace();
    }
    Container contentPane = getContentPane();
    contentPane.setLayout(new BorderLayout());
    initDialogPane(contentPane);
    pack();
    this.setLocationRelativeTo(owner);
  }

  private void initDialogPane(Container contentPane) {
    dialogPane.setBorder(new EmptyBorder(12, 12, 12, 12));
    dialogPane.setLayout(new BorderLayout());
    initContentPanel();
    initButtonBar();
    contentPane.add(dialogPane, BorderLayout.CENTER);
  }

  private void initContentPanel() {
    JFontButton scanKeyGen =
        new JFontButton(Lizzie.resourceBundle.getString("RemoteEngineSettings.scanKeyGen"));
    txtIP = new JFontTextField();
    txtIP.setColumns(10);
    txtPort = new JFontTextField();
    txtPort.setColumns(10);
    txtUserName = new JFontTextField();
    txtUserName.setColumns(10);
    chkUsePassword =
        new JFontCheckBox(Lizzie.resourceBundle.getString("RemoteEngineSettings.chkUsePassword"));
    txtPassword = new JPasswordField();
    txtPassword.setColumns(10);
    chkUseKeyGen =
        new JFontCheckBox(Lizzie.resourceBundle.getString("RemoteEngineSettings.chkUseKeyGen"));
    RemoteEngineData remoteEngineData;
    if (isAnalysisEngine) {
      remoteEngineData = Utils.getAnalysisEngineRemoteEngineData();
    } else if (isContributeEngine) {
      remoteEngineData = Utils.getContributeRemoteEngineData();
    } else {
      remoteEngineData = Utils.getEstimateEngineRemoteEngineData();
    }
    txtIP.setText(remoteEngineData.ip);
    txtPort.setText(remoteEngineData.port);
    txtUserName.setText(remoteEngineData.userName);
    txtPassword.setText(Utils.doDecrypt(remoteEngineData.password));
    chkUsePassword.setSelected(!remoteEngineData.useKeyGen);
    chkUseKeyGen.setSelected(remoteEngineData.useKeyGen);
    if (chkUsePassword.isSelected()) {
      scanKeyGen.setEnabled(false);
      txtPassword.setEnabled(true);
    } else {
      scanKeyGen.setEnabled(true);
      txtPassword.setEnabled(false);
    }
    keyGenPath = remoteEngineData.keyGenPath;
    chkUseKeyGen.setToolTipText(keyGenPath);
    scanKeyGen.setToolTipText(keyGenPath);
    scanKeyGen.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            FileDialog fileDialog =
                new FileDialog(
                    thisDialog, Lizzie.resourceBundle.getString("MoreEngines.chooseKeygen"));
            fileDialog.setLocationRelativeTo(MoreEngines.engjf);
            fileDialog.setAlwaysOnTop(true);
            fileDialog.setModal(true);
            fileDialog.setMultipleMode(false);
            fileDialog.setMode(0);
            fileDialog.setVisible(true);
            File[] file = fileDialog.getFiles();
            if (file.length > 0) keyGenPath = file[0].getAbsolutePath();
            chkUseKeyGen.setToolTipText(keyGenPath);
            scanKeyGen.setToolTipText(keyGenPath);
          }
        });
    chkUsePassword.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            if (chkUsePassword.isSelected()) {
              chkUseKeyGen.setSelected(false);
              scanKeyGen.setEnabled(false);
              txtPassword.setEnabled(true);
            } else {
              chkUseKeyGen.setSelected(true);
              scanKeyGen.setEnabled(true);
              txtPassword.setEnabled(false);
            }
          }
        });
    chkUseKeyGen.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            if (chkUseKeyGen.isSelected()) {
              chkUsePassword.setSelected(false);
              scanKeyGen.setEnabled(true);
              txtPassword.setEnabled(false);
            } else {
              chkUsePassword.setSelected(true);
              scanKeyGen.setEnabled(false);
              txtPassword.setEnabled(true);
            }
          }
        });
    GridLayout gridLayout = new GridLayout(5, 2, 4, 4);
    contentPanel.setLayout(gridLayout);
    contentPanel.add(new JFontLabel(Lizzie.resourceBundle.getString("RemoteEngineSettings.lblIP")));
    contentPanel.add(txtIP);
    contentPanel.add(
        new JFontLabel(Lizzie.resourceBundle.getString("RemoteEngineSettings.lblProt")));
    contentPanel.add(txtPort);
    contentPanel.add(
        new JFontLabel(Lizzie.resourceBundle.getString("RemoteEngineSettings.lblUserName")));
    contentPanel.add(txtUserName);
    contentPanel.add(chkUsePassword);
    contentPanel.add(txtPassword);
    contentPanel.add(chkUseKeyGen);
    contentPanel.add(scanKeyGen);
    dialogPane.add(contentPanel, BorderLayout.CENTER);
  }

  private void initButtonBar() {
    buttonBar.setBorder(new EmptyBorder(12, 0, 0, 0));
    buttonBar.setLayout(new GridBagLayout());
    ((GridBagLayout) buttonBar.getLayout()).columnWidths = new int[] {0, 80};
    ((GridBagLayout) buttonBar.getLayout()).columnWeights = new double[] {1.0, 0.0};
    okButton.setText("确定");
    okButton.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            RemoteEngineData remoteEngineData = new RemoteEngineData();
            remoteEngineData.useJavaSSH = true;
            remoteEngineData.ip = txtIP.getText();
            remoteEngineData.port = txtPort.getText();
            remoteEngineData.userName = txtUserName.getText();
            remoteEngineData.password = Utils.doEncrypt(new String(txtPassword.getPassword()));
            remoteEngineData.keyGenPath = keyGenPath;
            remoteEngineData.useKeyGen = chkUseKeyGen.isSelected();
            if (isAnalysisEngine) Utils.saveAnalysisEngineRemoteEngineData(remoteEngineData);
            else if (isContributeEngine) Utils.saveContributeRemoteEngineData(remoteEngineData);
            else Utils.saveEstimateEngineRemoteEngineData(remoteEngineData);
            setVisible(false);
          }
        });
    int center = GridBagConstraints.CENTER;
    int both = GridBagConstraints.BOTH;
    buttonBar.add(
        okButton,
        new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, center, both, new Insets(0, 0, 0, 0), 0, 0));
    dialogPane.add(buttonBar, BorderLayout.SOUTH);
  }
}
