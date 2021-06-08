package featurecat.lizzie.gui;

import featurecat.lizzie.Lizzie;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.util.Locale;
import java.util.ResourceBundle;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.border.EmptyBorder;

public class EngineParameter extends JDialog {

  public String enginePath = "";
  public String weightPath = "";
  public String parameters = "";
  public String commandLine = "";

  public JTextPane txtParams;

  private final JPanel contentPanel = new JPanel();
  private JTextField txtCommandLine;
  private JTextField txtParameter;
  private Color oriColor;
  private final ResourceBundle resourceBundle =
      Lizzie.config.useLanguage == 0
          ? ResourceBundle.getBundle("l10n.DisplayStrings")
          : (Lizzie.config.useLanguage == 1
              ? ResourceBundle.getBundle("l10n.DisplayStrings", new Locale("zh", "CN"))
              : ResourceBundle.getBundle("l10n.DisplayStrings", new Locale("en", "US")));

  /** Create the dialog. */
  public EngineParameter(
      String enginePath,
      String weightPath,
      String helpCommand,
      boolean isKataGo,
      String configPath,
      boolean isAnalysisEngine,
      boolean isOther) {
    setTitle(resourceBundle.getString("LizzieConfig.title.parameterConfig"));
    setModalExclusionType(ModalExclusionType.APPLICATION_EXCLUDE);
    setModal(true);
    // setType(Type.POPUP);
    setAlwaysOnTop(true);
    setModalityType(ModalityType.APPLICATION_MODAL);
    setBounds(100, 100, 680, 660);
    Lizzie.setFrameSize(this, 680, 660);
    getContentPane().setLayout(new BorderLayout());
    contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
    getContentPane().add(contentPanel, BorderLayout.CENTER);
    contentPanel.setLayout(null);
    JLabel lblEngneCommand = new JLabel(resourceBundle.getString("LizzieConfig.title.engine"));
    lblEngneCommand.setBounds(6, 17, 83, 16);
    contentPanel.add(lblEngneCommand);
    txtCommandLine = new JTextField();
    txtCommandLine.setEditable(false);
    txtCommandLine.setBounds(89, 12, 565, 26);
    if (isKataGo) txtCommandLine.setText(enginePath);
    else txtCommandLine.setText(enginePath);
    contentPanel.add(txtCommandLine);
    txtCommandLine.setColumns(10);
    JLabel lblParameter = new JLabel(resourceBundle.getString("LizzieConfig.title.parameter"));
    lblParameter.setBounds(6, 45, 83, 16);
    contentPanel.add(lblParameter);
    txtParameter = new JTextField();
    txtParameter.addFocusListener(
        new FocusAdapter() {
          @Override
          public void focusLost(FocusEvent e) {
            if (!txtParameter.getText().isEmpty()) {
              txtParameter.setBackground(oriColor);
            }
          }
        });
    txtParameter.setColumns(10);
    txtParameter.setBounds(89, 44, 565, 26);
    if (!isOther) {
      if (isKataGo)
        txtParameter.setText(
            (isAnalysisEngine ? " analysis -model " : " gtp -model ")
                + weightPath
                + " -config "
                + configPath
                + (isAnalysisEngine ? " -quit-without-waiting" : ""));
      else txtParameter.setText(" --weights " + weightPath + " -g --lagbuffer 0 ");
    }
    oriColor = txtParameter.getBackground();
    contentPanel.add(txtParameter);

    JScrollPane scrollPane = new JScrollPane();
    scrollPane.setBounds(6, 110, 648, 478);
    contentPanel.add(scrollPane);
    Font font = new Font("Consolas", Font.PLAIN, 12);
    scrollPane.setVerticalScrollBarPolicy(
        javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);

    txtParams = new JTextPane();
    scrollPane.setViewportView(txtParams);
    txtParams.setFont(font);
    txtParams.setText(helpCommand);
    txtParams.setEditable(false);

    JLabel lblParameterList =
        new JLabel(resourceBundle.getString("LizzieConfig.title.parameterList"));
    lblParameterList.setBounds(6, 81, 114, 16);
    contentPanel.add(lblParameterList);
    JPanel buttonPane = new JPanel();
    buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
    getContentPane().add(buttonPane, BorderLayout.SOUTH);
    JButton okButton = new JButton(resourceBundle.getString("LizzieConfig.button.ok"));
    okButton.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            if (txtParameter.getText().isEmpty()) {
              txtParameter.setBackground(Color.RED);
            } else {
              parameters = txtParameter.getText().trim();
              commandLine = txtCommandLine.getText() + " " + parameters;
              setVisible(false);
            }
          }
        });
    okButton.setActionCommand("OK");
    buttonPane.add(okButton);
    getRootPane().setDefaultButton(okButton);
    JButton cancelButton = new JButton(resourceBundle.getString("LizzieConfig.button.cancel"));
    cancelButton.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            setVisible(false);
          }
        });
    cancelButton.setActionCommand("Cancel");
    buttonPane.add(cancelButton);
    //  setLocationRelativeTo(getOwner());
  }
}
