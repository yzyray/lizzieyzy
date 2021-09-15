package featurecat.lizzie.gui;

import featurecat.lizzie.Config;
import featurecat.lizzie.Lizzie;
import featurecat.lizzie.util.Utils;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.Locale;
import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

public class ScoreResult extends JDialog {
  private JPanel dialogPane = new JPanel();
  private JPanel contentPanel = new JPanel();
  private JPanel buttonBar = new JPanel();
  private JFontLabel blackScore =
      new JFontLabel(Lizzie.resourceBundle.getString("ScoreResult.lblBlackScore"));
  private JFontLabel whiteScore =
      new JFontLabel(Lizzie.resourceBundle.getString("ScoreResult.lblWhiteScore"));
  private JFontLabel scoreResult = new JFontLabel();

  private JButton btnRecalculate;
  private JButton btnClose;
  private Color backColor = new Color(210, 210, 210);

  public ScoreResult(Window owner) {
    super(owner);
    initComponents();
    dialogPane.setBackground(backColor);
    contentPanel.setBackground(backColor);
    buttonBar.setBackground(backColor);
    addWindowListener(
        new WindowAdapter() {
          public void windowClosing(WindowEvent e) {
            setVisible(false);
            Lizzie.frame.endFinalScore();
          }
        });
    try {
      this.setIconImage(ImageIO.read(AnalysisFrame.class.getResourceAsStream("/assets/logo.png")));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private void initComponents() {
    setMinimumSize(new Dimension(100, 100));
    setResizable(false);
    setTitle(Lizzie.resourceBundle.getString("ScoreResult.title"));

    Container contentPane = getContentPane();
    contentPane.setLayout(new BorderLayout());

    initDialogPane(contentPane);

    pack();
    int width = this.getWidth();
    int height = this.getHeight();
    int frameX = Lizzie.frame.getX();
    int frameY = Lizzie.frame.getY();
    Dimension screensize = Toolkit.getDefaultToolkit().getScreenSize();
    int screenWidth = screensize.width;
    int screenHeight = screensize.height;
    int boardX = Lizzie.frame.boardX;
    int boardY =
        Lizzie.frame.boardY
            + Utils.zoomIn(Lizzie.frame.mainPanel.getY())
            + Config.menuHeight
            + Lizzie.frame.topPanel.getHeight();
    int boardLenght = Lizzie.frame.maxSize;
    if (Lizzie.config.isFloatBoardMode() && Lizzie.frame.independentMainBoard != null) {
      frameX = Lizzie.frame.independentMainBoard.getX();
      frameY = Lizzie.frame.independentMainBoard.getY();
      boardX = 0;
      boardY = 0;
      boardLenght = Lizzie.frame.independentMainBoard.getWidth();
    }
    if (frameX + boardX + boardLenght + width + 5 <= screenWidth)
      this.setLocation(
          frameX + boardX + boardLenght + 5,
          Math.min(frameY + boardY + boardLenght / 2 - height * 2 / 3, screenHeight - height));
    else if (frameX + boardX - width >= 0)
      this.setLocation(
          frameX + boardX - width,
          Math.min(frameY + boardY + boardLenght / 2 - height * 2 / 3, screenHeight - height));
    else if (frameY + boardY - height > 0)
      this.setLocation(frameX + boardX + boardLenght / 2 - width / 2, frameY + boardY - height);
    else if (frameY + boardY + boardLenght + height <= screenHeight)
      this.setLocation(
          frameX + boardX + boardLenght / 2 - width / 2, frameY + boardY + boardLenght);
    else
      setLocation(
          screenWidth - width,
          Math.min(frameY + boardY + boardLenght / 2 - height * 2 / 3, screenHeight - height));
  }

  private void initDialogPane(Container contentPane) {
    dialogPane.setBorder(new EmptyBorder(5, 12, 5, 12));
    dialogPane.setLayout(new BorderLayout());

    initContentPanel();
    initButtonBar();

    contentPane.add(dialogPane, BorderLayout.CENTER);
  }

  private void initContentPanel() {
    GridLayout gridLayout = new GridLayout(4, 1, 4, 4);
    contentPanel.setLayout(gridLayout);
    JLabel hint = new JFontLabel(Lizzie.resourceBundle.getString("ScoreResult.hint"));
    hint.setForeground(Color.BLUE);
    contentPanel.add(hint);
    contentPanel.add(blackScore);
    contentPanel.add(whiteScore);
    scoreResult.setOpaque(true);
    contentPanel.add(scoreResult);

    dialogPane.add(contentPanel, BorderLayout.CENTER);
  }

  private void initButtonBar() {
    buttonBar.setLayout(new GridBagLayout());
    buttonBar.setBorder(new EmptyBorder(5, 0, 0, 0));
    btnRecalculate = new JFontButton(Lizzie.resourceBundle.getString("ScoreResult.btnRecalculate"));
    btnRecalculate.setFocusable(false);
    GridBagConstraints gbc_button = new GridBagConstraints();
    gbc_button.anchor = GridBagConstraints.EAST;
    gbc_button.insets = new Insets(0, 0, 0, 0);
    gbc_button.gridx = 0;
    gbc_button.gridy = 0;
    buttonBar.add(btnRecalculate, gbc_button);
    btnRecalculate.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            setVisible(false);
            Lizzie.frame.startFinalScore();
          }
        });

    dialogPane.add(buttonBar, BorderLayout.SOUTH);

    btnClose = new JFontButton(Lizzie.resourceBundle.getString("ScoreResult.btnClose"));
    btnClose.setFocusable(false);
    btnClose.setPreferredSize(btnRecalculate.getPreferredSize());
    GridBagConstraints gbc_button_1 = new GridBagConstraints();
    gbc_button_1.anchor = GridBagConstraints.EAST;
    gbc_button_1.gridx = 1;
    gbc_button_1.gridy = 0;
    buttonBar.add(btnClose, gbc_button_1);
    btnClose.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            setVisible(false);
            Lizzie.frame.endFinalScore();
          }
        });
  }

  public void setScore(
      int blackAlive, int blackPoint, int whiteAlive, int whitePoint, double komi) {
    double blackAll = blackAlive + blackPoint;
    double whiteAll = whiteAlive + whitePoint + komi;
    blackScore.setText(
        Lizzie.resourceBundle.getString("ScoreResult.lblBlackScore")
            + blackAlive
            + "+"
            + blackPoint
            + "="
            + blackAll);
    whiteScore.setText(
        Lizzie.resourceBundle.getString("ScoreResult.lblWhiteScore")
            + whiteAlive
            + "+"
            + whitePoint
            + "+"
            + komi
            + "="
            + whiteAll);
    scoreResult.setText(
        (blackAll >= whiteAll
                ? Lizzie.resourceBundle.getString("ScoreResult.blackWin")
                : Lizzie.resourceBundle.getString("ScoreResult.whiteWin"))
            + String.format(Locale.ENGLISH, "%.1f", Math.abs(blackAll - whiteAll))
            + Lizzie.resourceBundle.getString("ScoreResult.points"));
    if (blackAll >= whiteAll) {
      scoreResult.setBackground(Color.BLACK);
      scoreResult.setForeground(Color.WHITE);
    } else {
      scoreResult.setBackground(Color.WHITE);
      scoreResult.setForeground(Color.BLACK);
    }
  }
}
