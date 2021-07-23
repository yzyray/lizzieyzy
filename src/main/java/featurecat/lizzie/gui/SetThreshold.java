package featurecat.lizzie.gui;

import featurecat.lizzie.Config;
import featurecat.lizzie.Lizzie;
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class SetThreshold extends JDialog {
  private JPanel dialogPane = new JPanel();
  private JPanel contentPanel = new JPanel();
  private JPanel buttonBar = new JPanel();
  private JSpinner dropWinRateChooser = new JSpinner(new SpinnerNumberModel(0, 0, 99, 1));
  private JSpinner dropScoreMeanChooser = new JSpinner(new SpinnerNumberModel(0, 0, 99, 1));
  private JSpinner playoutsChooser = new JSpinner(new SpinnerNumberModel(0, 0, 999999, 100));
  private JCheckBox chkTopCurrentMove;

  private JButton btnConfirm;
  private JButton btnCancel;
  private boolean fromMoveList;

  private int originMoveListWinrateThreshold = Lizzie.config.moveListWinrateThreshold;
  private int originMoveListScoreThreshold = Lizzie.config.moveListScoreThreshold;
  private int originMoveListVisitsThreshold = Lizzie.config.moveListVisitsThreshold;
  private boolean originMoveListTopCurNode = Lizzie.config.moveListTopCurNode;

  public SetThreshold(Window owner, boolean fromMoveList) {
    super(owner);
    this.fromMoveList = fromMoveList;
    initComponents();
    try {
      this.setIconImage(ImageIO.read(AnalysisFrame.class.getResourceAsStream("/assets/logo.png")));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private void initComponents() {
    setMinimumSize(new Dimension(100, 100));
    setResizable(false);
    setTitle(Lizzie.resourceBundle.getString("SetThreshold.title"));

    Container contentPane = getContentPane();
    contentPane.setLayout(new BorderLayout());

    initDialogPane(contentPane);

    pack();
    setLocationRelativeTo(getOwner());
  }

  private void initDialogPane(Container contentPane) {
    dialogPane.setBorder(new EmptyBorder(12, 12, 12, 12));
    dialogPane.setLayout(new BorderLayout());

    initContentPanel();
    initButtonBar();

    contentPane.add(dialogPane, BorderLayout.CENTER);
  }

  private void initContentPanel() {
    GridLayout gridLayout = new GridLayout(fromMoveList ? 4 : 3, 2, 4, 4);
    dropWinRateChooser.setFont(new Font("", Font.PLAIN, Config.frameFontSize));
    dropScoreMeanChooser.setFont(new Font("", Font.PLAIN, Config.frameFontSize));
    playoutsChooser.setFont(new Font("", Font.PLAIN, Config.frameFontSize));
    if (fromMoveList) {
      dropWinRateChooser.setValue(Lizzie.config.moveListWinrateThreshold);
      dropScoreMeanChooser.setValue(Lizzie.config.moveListScoreThreshold);
      playoutsChooser.setValue(Lizzie.config.moveListVisitsThreshold);
    } else {
      dropWinRateChooser.setValue(Lizzie.config.blunderWinThreshold);
      dropScoreMeanChooser.setValue(Lizzie.config.blunderScoreThreshold);
      playoutsChooser.setValue(Lizzie.config.blunderPlayoutsThreshold);
    }
    contentPanel.setLayout(gridLayout);
    contentPanel.add(
        new JFontLabel(Lizzie.resourceBundle.getString("Movelistframe.lblDropWinrate")));
    contentPanel.add(dropWinRateChooser);
    contentPanel.add(new JFontLabel(Lizzie.resourceBundle.getString("Movelistframe.lblDropScore")));
    contentPanel.add(dropScoreMeanChooser);
    contentPanel.add(new JFontLabel(Lizzie.resourceBundle.getString("Movelistframe.lblPlayouts")));
    contentPanel.add(playoutsChooser);
    if (fromMoveList) {
      chkTopCurrentMove = new JCheckBox();
      chkTopCurrentMove.setSelected(Lizzie.config.moveListTopCurNode);
      contentPanel.add(
          new JFontLabel(Lizzie.resourceBundle.getString("Movelistframe.chkTopCurrentMove")));
      contentPanel.add(chkTopCurrentMove);
      dropWinRateChooser.addChangeListener(
          new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
              Lizzie.config.saveThreshold(
                  (int) dropWinRateChooser.getValue(),
                  (int) dropScoreMeanChooser.getValue(),
                  (int) playoutsChooser.getValue(),
                  chkTopCurrentMove.isSelected());
            }
          });
      dropScoreMeanChooser.addChangeListener(
          new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
              Lizzie.config.saveThreshold(
                  (int) dropWinRateChooser.getValue(),
                  (int) dropScoreMeanChooser.getValue(),
                  (int) playoutsChooser.getValue(),
                  chkTopCurrentMove.isSelected());
            }
          });
      playoutsChooser.addChangeListener(
          new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
              Lizzie.config.saveThreshold(
                  (int) dropWinRateChooser.getValue(),
                  (int) dropScoreMeanChooser.getValue(),
                  (int) playoutsChooser.getValue(),
                  chkTopCurrentMove.isSelected());
            }
          });
      chkTopCurrentMove.addActionListener(
          new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
              Lizzie.config.saveThreshold(
                  (int) dropWinRateChooser.getValue(),
                  (int) dropScoreMeanChooser.getValue(),
                  (int) playoutsChooser.getValue(),
                  chkTopCurrentMove.isSelected());
            }
          });
    }
    dialogPane.add(contentPanel, BorderLayout.CENTER);
  }

  private void initButtonBar() {
    buttonBar.setLayout(new GridBagLayout());
    buttonBar.setBorder(new EmptyBorder(12, 0, 0, 0));
    btnConfirm = new JFontButton(Lizzie.resourceBundle.getString("SetKataEngines.btnApply"));
    GridBagConstraints gbc_button = new GridBagConstraints();
    gbc_button.anchor = GridBagConstraints.EAST;
    gbc_button.insets = new Insets(0, 0, 0, 5);
    gbc_button.gridx = 0;
    gbc_button.gridy = 0;
    buttonBar.add(btnConfirm, gbc_button);
    btnConfirm.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            if (!fromMoveList) {
              Lizzie.config.saveThreshold(
                  (int) dropWinRateChooser.getValue(),
                  (int) dropScoreMeanChooser.getValue(),
                  (int) playoutsChooser.getValue());
            }
            setVisible(false);
          }
        });

    dialogPane.add(buttonBar, BorderLayout.SOUTH);

    btnCancel = new JFontButton(Lizzie.resourceBundle.getString("SetKataEngines.btnCancel"));
    GridBagConstraints gbc_button_1 = new GridBagConstraints();
    gbc_button_1.anchor = GridBagConstraints.EAST;
    gbc_button_1.gridx = 1;
    gbc_button_1.gridy = 0;
    buttonBar.add(btnCancel, gbc_button_1);
    btnCancel.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            if (fromMoveList) {
              Lizzie.config.saveThreshold(
                  originMoveListWinrateThreshold,
                  originMoveListScoreThreshold,
                  originMoveListVisitsThreshold,
                  originMoveListTopCurNode);
            }
            setVisible(false);
          }
        });
  }
}
