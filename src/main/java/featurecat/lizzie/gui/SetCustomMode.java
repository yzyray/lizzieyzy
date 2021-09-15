package featurecat.lizzie.gui;

import featurecat.lizzie.ExtraMode;
import featurecat.lizzie.Lizzie;
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class SetCustomMode extends JDialog {
  private JPanel dialogPane = new JPanel();
  private JPanel contentPanel = new JPanel();
  private JPanel buttonBar = new JPanel();
  private JSlider sldBoardPositionProportion;
  private JCheckBox subBoard;
  private JCheckBox winratePanel;
  private JCheckBox commentPanel;
  private JCheckBox variationPanel;
  private JCheckBox listPanel;
  private JCheckBox informationPanel;
  private JCheckBox statusPanel;
  private JCheckBox bigSubBoard;
  private JCheckBox bigWinrate;
  private JCheckBox floatMainBoard;
  private JCheckBox floatSubBoard;

  private JButton btnConfirm;
  private JButton btnCancel;

  private int index;
  private boolean isSetCustom;
  private boolean oriPondering;

  public SetCustomMode(int index, boolean isSetCustom) {
    //  super(owner);
    this.index = index;
    this.isSetCustom = isSetCustom;
    oriPondering = Lizzie.leelaz.isPondering();
    if (Lizzie.leelaz.isPondering()) Lizzie.leelaz.togglePonder();
    this.setAlwaysOnTop(true);
    this.saveTempConfig();
    if (isSetCustom) Lizzie.config.loadCustomLayout(index);
    initComponents();

    addWindowListener(
        new WindowAdapter() {
          public void windowClosing(WindowEvent e) {
            restoreTempConfig();
            setVisible(false);
            if (oriPondering) Lizzie.leelaz.ponder();
          }
        });
    try {
      this.setIconImage(ImageIO.read(AnalysisFrame.class.getResourceAsStream("/assets/logo.png")));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private boolean showSubBoard;
  private boolean showWinratePanel;
  private boolean showCommentPanel;
  private boolean showVariationPanel;
  private boolean showListPanel;
  private boolean showInformationPanel;
  private boolean showStatus;
  private boolean showBigSubBoard;
  private boolean showBigWinrate;
  private int boardPositionProportion;
  private boolean showFloatMainBoard;
  private boolean showFloatSubBoard;
  private boolean isFloatMode;

  private void saveTempConfig() {
    showSubBoard = Lizzie.config.showSubBoard;
    showWinratePanel = Lizzie.config.showWinrateGraph;
    showCommentPanel = Lizzie.config.showComment;
    showVariationPanel = Lizzie.config.showVariationGraph;
    showListPanel = Lizzie.config.showListPane();
    showInformationPanel = Lizzie.config.showCaptured;
    showStatus = Lizzie.config.showStatus;
    showBigSubBoard = Lizzie.config.showLargeSubBoard();
    showBigWinrate = Lizzie.config.showLargeWinrate();
    boardPositionProportion = Lizzie.frame.BoardPositionProportion;
    showFloatMainBoard =
        Lizzie.frame.independentMainBoard != null && Lizzie.frame.independentMainBoard.isVisible();
    showFloatSubBoard =
        Lizzie.frame.independentSubBoard != null && Lizzie.frame.independentSubBoard.isVisible();
    isFloatMode = Lizzie.config.isFloatBoardMode();
  }

  private void restoreTempConfig() {
    Lizzie.config.showSubBoard = showSubBoard;
    Lizzie.config.showWinrateGraph = showWinratePanel;
    Lizzie.config.showComment = showCommentPanel;
    Lizzie.config.showVariationGraph = showVariationPanel;
    Lizzie.config.showCaptured = showInformationPanel;
    Lizzie.config.showStatus = showStatus;
    Lizzie.config.largeSubBoard = showBigSubBoard;
    Lizzie.config.largeWinrateGraph = showBigWinrate;
    Lizzie.frame.BoardPositionProportion = boardPositionProportion;
    Lizzie.frame.setVarTreeVisible(Lizzie.config.showVariationGraph);
    if (isFloatMode) Lizzie.config.extraMode = ExtraMode.Float_Board;
    else Lizzie.config.extraMode = ExtraMode.Normal;
    if (showFloatMainBoard) {
      if (Lizzie.frame.independentMainBoard == null
          || !Lizzie.frame.independentMainBoard.isVisible())
        Lizzie.frame.toggleIndependentMainBoard();
    } else {
      if (Lizzie.frame.independentMainBoard != null
          && Lizzie.frame.independentMainBoard.isVisible())
        Lizzie.frame.toggleIndependentMainBoard();
    }
    if (showFloatSubBoard) {
      if (Lizzie.frame.independentSubBoard == null || !Lizzie.frame.independentSubBoard.isVisible())
        Lizzie.frame.toggleIndependentSubBoard();
    } else {
      if (Lizzie.frame.independentSubBoard != null && Lizzie.frame.independentSubBoard.isVisible())
        Lizzie.frame.toggleIndependentSubBoard();
    }
    if (showListPanel && !Lizzie.config.showListPane()) Lizzie.config.toggleShowListPane();
    if (!showListPanel && Lizzie.config.showListPane()) Lizzie.config.toggleShowListPane();
    Lizzie.frame.refreshContainer();
    Lizzie.frame.repaint();
  }

  private void initComponents() {
    setMinimumSize(new Dimension(100, 100));
    setResizable(false);
    setTitle(
        isSetCustom
            ? Lizzie.resourceBundle.getString("SetCustomMode.title") + index
            : Lizzie.resourceBundle.getString("VisualizedPanelSettings.title"));

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
    GridLayout gridLayout = new GridLayout(12, 2, 45, 4);
    contentPanel.setLayout(gridLayout);

    subBoard = new JCheckBox();
    subBoard.setSelected(Lizzie.config.showSubBoard);
    subBoard.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            Lizzie.config.showSubBoard = subBoard.isSelected();
            Lizzie.frame.repaint();
            if (!Lizzie.config.showSubBoard) {
              bigSubBoard.setSelected(false);
            }
          }
        });
    contentPanel.add(new JFontLabel(Lizzie.resourceBundle.getString("SetCustomMode.subBoard")));
    contentPanel.add(subBoard);

    winratePanel = new JCheckBox();
    winratePanel.setSelected(Lizzie.config.showWinrateGraph);
    winratePanel.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            Lizzie.config.showWinrateGraph = winratePanel.isSelected();
            Lizzie.frame.refreshContainer();
            Lizzie.frame.repaint();
            if (!Lizzie.config.showWinrateGraph) {
              bigWinrate.setSelected(false);
              if (!bigSubBoard.isSelected()) sldBoardPositionProportion.setEnabled(true);
            }
          }
        });
    contentPanel.add(new JFontLabel(Lizzie.resourceBundle.getString("SetCustomMode.winratePanel")));
    contentPanel.add(winratePanel);

    commentPanel = new JCheckBox();
    commentPanel.setSelected(Lizzie.config.showComment);
    commentPanel.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            Lizzie.config.showComment = commentPanel.isSelected();
            if (Lizzie.config.showComment) Lizzie.frame.setCommentPaneContent();
            else {
              Lizzie.frame.commentScrollPane.setVisible(false);
              Lizzie.frame.blunderContentPane.setVisible(false);
            }
            Lizzie.frame.refreshContainer();
            Lizzie.frame.repaint();
          }
        });
    contentPanel.add(new JFontLabel(Lizzie.resourceBundle.getString("SetCustomMode.commentPanel")));
    contentPanel.add(commentPanel);

    variationPanel = new JCheckBox();
    variationPanel.setSelected(Lizzie.config.showVariationGraph);
    variationPanel.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            Lizzie.config.showVariationGraph = variationPanel.isSelected();
            Lizzie.frame.refreshContainer();
            Lizzie.frame.repaint();
          }
        });
    contentPanel.add(
        new JFontLabel(Lizzie.resourceBundle.getString("SetCustomMode.variationPanel")));
    contentPanel.add(variationPanel);

    listPanel = new JCheckBox();
    listPanel.setSelected(Lizzie.config.showListPane);
    listPanel.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            if (Lizzie.config.showListPane() != listPanel.isSelected())
              Lizzie.config.toggleShowListPane();
            Lizzie.frame.refreshContainer();
            Lizzie.frame.repaint();
            if (Lizzie.config.showListPane()) {
              bigWinrate.setSelected(false);
              if (!bigSubBoard.isSelected()) sldBoardPositionProportion.setEnabled(true);
            }
          }
        });
    contentPanel.add(new JFontLabel(Lizzie.resourceBundle.getString("SetCustomMode.listPanel")));
    contentPanel.add(listPanel);

    informationPanel = new JCheckBox();
    informationPanel.setSelected(Lizzie.config.showCaptured);
    informationPanel.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            Lizzie.config.showCaptured = informationPanel.isSelected();
            Lizzie.frame.refreshContainer();
            Lizzie.frame.repaint();
          }
        });
    contentPanel.add(
        new JFontLabel(Lizzie.resourceBundle.getString("SetCustomMode.informationPanel")));
    contentPanel.add(informationPanel);

    statusPanel = new JCheckBox();
    statusPanel.setSelected(Lizzie.config.showStatus);
    statusPanel.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            Lizzie.config.showStatus = statusPanel.isSelected();
            Lizzie.frame.refreshContainer();
            Lizzie.frame.repaint();
          }
        });
    contentPanel.add(new JFontLabel(Lizzie.resourceBundle.getString("SetCustomMode.statusPanel")));
    contentPanel.add(statusPanel);

    bigSubBoard = new JCheckBox();
    bigSubBoard.setSelected(Lizzie.config.showLargeSubBoard());
    bigSubBoard.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            Lizzie.config.largeSubBoard = bigSubBoard.isSelected();
            Lizzie.frame.refreshContainer();
            Lizzie.frame.repaint();
            if (Lizzie.config.largeSubBoard) {
              Lizzie.config.showSubBoard = true;
              bigWinrate.setSelected(false);
              subBoard.setSelected(true);
              sldBoardPositionProportion.setEnabled(false);
            } else if (!bigWinrate.isSelected()) sldBoardPositionProportion.setEnabled(true);
          }
        });
    contentPanel.add(new JFontLabel(Lizzie.resourceBundle.getString("SetCustomMode.bigSubBoard")));
    contentPanel.add(bigSubBoard);

    bigWinrate = new JCheckBox();
    bigWinrate.setSelected(Lizzie.config.showLargeWinrate());
    bigWinrate.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            Lizzie.config.largeWinrateGraph = bigWinrate.isSelected();
            if (Lizzie.config.largeWinrateGraph) {
              Lizzie.config.largeSubBoard = false;
              Lizzie.config.showWinrateGraph = true;
              bigSubBoard.setSelected(false);
              listPanel.setSelected(false);
              Lizzie.config.showListPane = false;
              Lizzie.frame.setHideListScrollpane(false);
              winratePanel.setSelected(true);
              sldBoardPositionProportion.setEnabled(false);

            } else if (!bigWinrate.isSelected()) sldBoardPositionProportion.setEnabled(true);
            Lizzie.frame.refreshContainer();
            Lizzie.frame.repaint();
          }
        });
    contentPanel.add(new JFontLabel(Lizzie.resourceBundle.getString("SetCustomMode.bigWinrate")));
    contentPanel.add(bigWinrate);

    sldBoardPositionProportion = new JSlider();
    sldBoardPositionProportion.setPreferredSize(
        new Dimension(50, sldBoardPositionProportion.getHeight()));
    sldBoardPositionProportion.setPaintTicks(true);
    sldBoardPositionProportion.setSnapToTicks(true);
    sldBoardPositionProportion.addChangeListener(
        new ChangeListener() {
          public void stateChanged(ChangeEvent e) {
            if (Lizzie.frame.BoardPositionProportion != sldBoardPositionProportion.getValue()) {
              Lizzie.frame.BoardPositionProportion = sldBoardPositionProportion.getValue();
              Lizzie.frame.refreshContainer();
              Lizzie.frame.repaint();
            }
          }
        });
    sldBoardPositionProportion.setValue(Lizzie.frame.BoardPositionProportion);
    sldBoardPositionProportion.setMaximum(8);
    contentPanel.add(
        new JFontLabel(
            Lizzie.resourceBundle.getString("SetCustomMode.sldBoardPositionProportion")));
    contentPanel.add(sldBoardPositionProportion);

    floatMainBoard = new JCheckBox();
    floatMainBoard.setSelected(Lizzie.config.isFloatBoardMode());
    floatMainBoard.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            if (floatMainBoard.isSelected()) {
              Lizzie.config.extraMode = ExtraMode.Float_Board;
              bigWinrate.setSelected(false);
              bigWinrate.setEnabled(false);
              bigSubBoard.setSelected(false);
              bigSubBoard.setEnabled(false);
              if (Lizzie.frame.independentMainBoard == null
                  || !Lizzie.frame.independentMainBoard.isVisible())
                Lizzie.frame.toggleIndependentMainBoard();
            } else {
              Lizzie.config.extraMode = ExtraMode.Normal;
              if (Lizzie.frame.independentMainBoard != null
                  && Lizzie.frame.independentMainBoard.isVisible())
                Lizzie.frame.toggleIndependentMainBoard();
              bigWinrate.setEnabled(true);
              bigSubBoard.setEnabled(true);
            }
            Lizzie.frame.refreshContainer();
            Lizzie.frame.repaint();
            setVisible(true);
          }
        });
    contentPanel.add(
        new JFontLabel(Lizzie.resourceBundle.getString("SetCustomMode.floatMainBoard")));
    contentPanel.add(floatMainBoard);

    if (floatMainBoard.isSelected()) {
      bigWinrate.setSelected(false);
      bigWinrate.setEnabled(false);
      bigSubBoard.setSelected(false);
      bigSubBoard.setEnabled(false);
    }

    floatSubBoard = new JCheckBox();
    floatSubBoard.setSelected(
        Lizzie.frame.independentSubBoard != null && Lizzie.frame.independentSubBoard.isVisible());
    floatSubBoard.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            if (floatSubBoard.isSelected()) {
              subBoard.setSelected(false);
              subBoard.setEnabled(false);
              Lizzie.config.showSubBoard = false;
              if (Lizzie.frame.independentSubBoard == null
                  || !Lizzie.frame.independentSubBoard.isVisible())
                Lizzie.frame.toggleIndependentSubBoard();
            } else {
              subBoard.setEnabled(true);
              subBoard.setSelected(true);
              Lizzie.config.showSubBoard = true;
              if (Lizzie.frame.independentSubBoard != null
                  && Lizzie.frame.independentSubBoard.isVisible())
                Lizzie.frame.toggleIndependentSubBoard();
            }
            Lizzie.frame.refreshContainer();
            Lizzie.frame.repaint();
            setVisible(true);
          }
        });
    contentPanel.add(
        new JFontLabel(Lizzie.resourceBundle.getString("SetCustomMode.floatSubBoard")));
    contentPanel.add(floatSubBoard);
    if (floatSubBoard.isSelected()) {
      subBoard.setSelected(false);
      subBoard.setEnabled(false);
    }

    if (Lizzie.config.showListPane()) {
      bigWinrate.setSelected(false);
    }
    if (Lizzie.config.showLargeWinrate()) {
      listPanel.setSelected(false);
    }
    if (Lizzie.config.showLargeSubBoard() || Lizzie.config.showLargeWinrate())
      sldBoardPositionProportion.setEnabled(false);

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
            Lizzie.config.savePanelConfig();
            if (isSetCustom) Lizzie.config.saveCustomMode(index);
            setVisible(false);
            if (oriPondering) Lizzie.leelaz.ponder();
            Lizzie.frame.refreshContainer();
            Lizzie.frame.repaint();
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
            restoreTempConfig();
            if (oriPondering) Lizzie.leelaz.ponder();
            setVisible(false);
          }
        });
  }
}
