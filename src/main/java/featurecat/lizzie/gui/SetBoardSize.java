package featurecat.lizzie.gui;

import featurecat.lizzie.Config;
import featurecat.lizzie.Lizzie;
import featurecat.lizzie.rules.Board;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.ResourceBundle;
import javax.imageio.ImageIO;
import javax.swing.Action;
import javax.swing.ButtonGroup;
import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import javax.swing.text.InternationalFormatter;

public class SetBoardSize extends JDialog {
  private JFormattedTextField width;
  private JFormattedTextField height;
  private final ResourceBundle resourceBundle = Lizzie.resourceBundle;
  private int widthNumber;
  private int heightNumber;
  // private static JTextField defaultText = new JTextField();
  private JFontRadioButton rdo19;
  private JFontRadioButton rdo15;
  private JFontRadioButton rdo13;
  private JFontRadioButton rdo9;
  private JFontRadioButton rdoOther;

  public SetBoardSize() {
    // setType(Type.POPUP);
    setModal(true);
    setTitle(resourceBundle.getString("SetBoardSize.title")); // ("设置棋盘大小");
    setAlwaysOnTop(Lizzie.frame.isAlwaysOnTop());
    //   setBounds(0, 0, 428, 103);
    setResizable(false);
    Lizzie.setFrameSize(
        this,
        Lizzie.config.isFrameFontSmall() ? 428 : (Lizzie.config.isFrameFontMiddle() ? 450 : 480),
        103);
    getContentPane().setLayout(new BorderLayout());
    JPanel buttonPane = new JPanel();
    getContentPane().add(buttonPane, BorderLayout.CENTER);
    JFontButton okButton =
        new JFontButton(resourceBundle.getString("SetBoardSize.okButton")); // ("确定");
    okButton.setBounds(161, 38, 95, 25);
    okButton.setFocusable(false);
    okButton.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            if (rdoOther.isSelected()) if (!checkMove()) return;
            setVisible(false);
            applyChange();
          }
        });
    buttonPane.setLayout(null);
    okButton.setActionCommand("OK");
    buttonPane.add(okButton);
    getRootPane().setDefaultButton(okButton);
    NumberFormat nf = NumberFormat.getIntegerInstance();
    nf.setGroupingUsed(false);

    width =
        new JFormattedTextField(
            new InternationalFormatter(nf) {
              protected DocumentFilter getDocumentFilter() {
                return filter;
              }

              private DocumentFilter filter = new DigitOnlyFilter();
            });
    width.setFont(new Font(Config.sysDefaultFontName, Font.PLAIN, Config.frameFontSize));
    buttonPane.add(width);
    width.setColumns(3);

    JFontLabel lblHeight = new JFontLabel("x");
    buttonPane.add(lblHeight);
    lblHeight.setHorizontalAlignment(SwingConstants.LEFT);

    height =
        new JFormattedTextField(
            new InternationalFormatter(nf) {
              protected DocumentFilter getDocumentFilter() {
                return filter;
              }

              private DocumentFilter filter = new DigitOnlyFilter();
            });

    height.setFont(new Font(Config.sysDefaultFontName, Font.PLAIN, Config.frameFontSize));
    buttonPane.add(height);
    height.setColumns(3);

    width.setBounds(
        Lizzie.config.isFrameFontSmall() ? 330 : (Lizzie.config.isFrameFontMiddle() ? 355 : 380),
        7,
        Lizzie.config.isFrameFontSmall() ? 30 : (Lizzie.config.isFrameFontMiddle() ? 33 : 36),
        20);
    lblHeight.setBounds(
        Lizzie.config.isFrameFontSmall() ? 363 : (Lizzie.config.isFrameFontMiddle() ? 390 : 419),
        6,
        18,
        20);
    height.setBounds(
        Lizzie.config.isFrameFontSmall() ? 372 : (Lizzie.config.isFrameFontMiddle() ? 400 : 431),
        7,
        Lizzie.config.isFrameFontSmall() ? 30 : (Lizzie.config.isFrameFontMiddle() ? 33 : 36),
        20);

    rdo19 = new JFontRadioButton(resourceBundle.getString("SetBoardSize.rdo19")); // $NON-NLS-1$
    rdo19.setBounds(
        6,
        6,
        Lizzie.config.isFrameFontSmall() ? 72 : (Lizzie.config.isFrameFontMiddle() ? 77 : 82),
        23);
    buttonPane.add(rdo19);

    rdo15 = new JFontRadioButton(resourceBundle.getString("SetBoardSize.rdo15")); // $NON-NLS-1$
    rdo15.setBounds(
        Lizzie.config.isFrameFontSmall() ? 76 : (Lizzie.config.isFrameFontMiddle() ? 81 : 86),
        6,
        Lizzie.config.isFrameFontSmall() ? 72 : (Lizzie.config.isFrameFontMiddle() ? 77 : 82),
        23);
    buttonPane.add(rdo15);

    rdo13 = new JFontRadioButton(resourceBundle.getString("SetBoardSize.rdo13")); // $NON-NLS-1$
    rdo13.setBounds(
        Lizzie.config.isFrameFontSmall() ? 146 : (Lizzie.config.isFrameFontMiddle() ? 156 : 166),
        6,
        Lizzie.config.isFrameFontSmall() ? 72 : (Lizzie.config.isFrameFontMiddle() ? 77 : 82),
        23);
    buttonPane.add(rdo13);

    rdo9 = new JFontRadioButton(resourceBundle.getString("SetBoardSize.rdo9")); // $NON-NLS-1$
    rdo9.setBounds(
        Lizzie.config.isFrameFontSmall() ? 216 : (Lizzie.config.isFrameFontMiddle() ? 231 : 246),
        6,
        Lizzie.config.isFrameFontSmall() ? 56 : (Lizzie.config.isFrameFontMiddle() ? 61 : 66),
        23);
    buttonPane.add(rdo9);

    rdoOther =
        new JFontRadioButton(resourceBundle.getString("SetBoardSize.rdoOther")); // $NON-NLS-1$
    rdoOther.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            width.setEnabled(rdoOther.isSelected());
            height.setEnabled(rdoOther.isSelected());
          }
        });
    rdoOther.setBounds(
        Lizzie.config.isFrameFontSmall() ? 274 : (Lizzie.config.isFrameFontMiddle() ? 294 : 314),
        6,
        Lizzie.config.isFrameFontSmall() ? 55 : (Lizzie.config.isFrameFontMiddle() ? 60 : 65),
        23);
    buttonPane.add(rdoOther);
    rdo9.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            width.setEnabled(rdoOther.isSelected());
            height.setEnabled(rdoOther.isSelected());
          }
        });
    rdo13.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            width.setEnabled(rdoOther.isSelected());
            height.setEnabled(rdoOther.isSelected());
          }
        });
    rdo15.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            width.setEnabled(rdoOther.isSelected());
            height.setEnabled(rdoOther.isSelected());
          }
        });
    rdo19.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            width.setEnabled(rdoOther.isSelected());
            height.setEnabled(rdoOther.isSelected());
          }
        });
    rdo19.setFocusable(false);
    rdo15.setFocusable(false);
    rdo13.setFocusable(false);
    rdo9.setFocusable(false);
    rdoOther.setFocusable(false);
    if (Board.boardHeight == 19 && Board.boardWidth == 19) rdo19.setSelected(true);
    else if (Board.boardHeight == 15 && Board.boardWidth == 15) rdo15.setSelected(true);
    else if (Board.boardHeight == 13 && Board.boardWidth == 13) rdo13.setSelected(true);
    else if (Board.boardHeight == 9 && Board.boardWidth == 9) rdo9.setSelected(true);
    else {
      rdoOther.setSelected(true);
      Lizzie.config.otherSizeWidth = Board.hexWidth;
      Lizzie.config.otherSizeHeight = Board.hexHeight;
    }
    if (!rdoOther.isSelected()) {
      width.setEnabled(false);
      height.setEnabled(false);
    }
    width.setText(String.valueOf(Lizzie.config.otherSizeWidth));
    height.setText(String.valueOf(Lizzie.config.otherSizeHeight));

    ButtonGroup group = new ButtonGroup();
    group.add(rdo19);
    group.add(rdo15);
    group.add(rdo13);
    group.add(rdo9);
    group.add(rdoOther);

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
    setLocationRelativeTo(getOwner());
  }

  private class DigitOnlyFilter extends DocumentFilter {
    @Override
    public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr)
        throws BadLocationException {
      String newStr = string != null ? string.replaceAll("\\D++", "") : "";
      if (!newStr.isEmpty()) {
        fb.insertString(offset, newStr, attr);
      }
    }
  }

  private void applyChange() {
    if (rdo19.isSelected()) Lizzie.board.reopen(19, 19);
    else if (rdo15.isSelected()) Lizzie.board.reopen(15, 15);
    else if (rdo13.isSelected()) Lizzie.board.reopen(13, 13);
    else if (rdo9.isSelected()) Lizzie.board.reopen(9, 9);
    else {
      Lizzie.board.reopen(widthNumber, heightNumber);
      Lizzie.config.saveOtherBoardSize(widthNumber, heightNumber);
    }
  }

  private Integer txtFieldValue(JTextField txt) {
    if (txt.getText().trim().isEmpty()
        || txt.getText().trim().length() >= String.valueOf(Integer.MAX_VALUE).length()) {
      return 0;
    } else {
      return Integer.parseInt(txt.getText().trim());
    }
  }

  private boolean checkMove() {
    boolean error = false;
    try {
      widthNumber = txtFieldValue(width);
      heightNumber = txtFieldValue(height);
    } catch (NumberFormatException e) {
      error = true;
      e.printStackTrace();
    }
    // changePosition = getChangeToType();
    // Color c = defaultText.getBackground();
    if (widthNumber < 2 || heightNumber < 2 || error) {
      width.setToolTipText(resourceBundle.getString("LizzieChangeMove.txtMoveNumber.error"));
      height.setToolTipText(resourceBundle.getString("LizzieChangeMove.txtMoveNumber.error"));
      Action action = width.getActionMap().get("postTip");
      Action action2 = height.getActionMap().get("postTip");
      if (action != null) {
        ActionEvent ae =
            new ActionEvent(
                height,
                ActionEvent.ACTION_PERFORMED,
                "postTip",
                EventQueue.getMostRecentEventTime(),
                0);
        action.actionPerformed(ae);
        ActionEvent ae2 =
            new ActionEvent(
                width,
                ActionEvent.ACTION_PERFORMED,
                "postTip",
                EventQueue.getMostRecentEventTime(),
                0);
        action2.actionPerformed(ae2);
      }
      width.setBackground(Color.red);
      height.setBackground(Color.red);
      return false;
    }
    return true;
  }
}
