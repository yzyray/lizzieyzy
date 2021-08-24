package featurecat.lizzie.gui;

import featurecat.lizzie.Lizzie;
import featurecat.lizzie.rules.BoardHistoryNode;
import featurecat.lizzie.rules.NodeInfo;
import featurecat.lizzie.util.Utils;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.IOException;
import java.util.Locale;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JTextField;

public class ShareFrame extends JDialog {
  private JTextField txtBlack;
  private JTextField txtWhite;
  public JTextField txtUploader;
  private JTextField txtOtherInfo;
  private JTextField otherLabel;
  private JDialog shareFrame = this;
  int selectedLabelIndex = 0;
  JComboBox cboxLabel;
  ItemListener lis;
  JCheckBox chkPublic;

  public ShareFrame() {
    this.setModal(true);
    // setType(Type.POPUP);
    setResizable(false);
    setTitle(Lizzie.resourceBundle.getString("ShareFrame.title")); // "分享信息");
    setAlwaysOnTop(Lizzie.frame.isAlwaysOnTop());
    setLocationRelativeTo(Lizzie.frame);
    getContentPane().setLayout(null);

    JButton btnApply =
        new JButton(Lizzie.resourceBundle.getString("ShareFrame.btnApply")); // ("分享");
    btnApply.setBounds(193, 133, 69, 23);
    getContentPane().add(btnApply);
    btnApply.setMargin(new Insets(0, 0, 0, 0));

    JButton btnCancel =
        new JButton(Lizzie.resourceBundle.getString("ShareFrame.btnCancel")); // ("取消");
    btnCancel.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            setVisible(false);
          }
        });
    btnCancel.setBounds(272, 133, 69, 23);
    getContentPane().add(btnCancel);
    btnCancel.setMargin(new Insets(0, 0, 0, 0));

    txtBlack = new JTextField();
    txtBlack.setBounds(34, 10, 229, 23);
    getContentPane().add(txtBlack);
    txtBlack.setColumns(10);

    txtWhite = new JTextField();
    txtWhite.setBounds(315, 10, 211, 23);
    getContentPane().add(txtWhite);
    txtWhite.setColumns(10);
    txtBlack.setText(Lizzie.board.getHistory().getGameInfo().getPlayerBlack());
    txtWhite.setText(Lizzie.board.getHistory().getGameInfo().getPlayerWhite());

    JButton btnLogin =
        new JButton(Lizzie.resourceBundle.getString("ShareFrame.btnLogin")); // ("注册/登录");
    btnLogin.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            Loggin loggin = new Loggin(shareFrame, false);
            loggin.setVisible(true);
          }
        });
    btnLogin.setBounds(440, 44, 87, 23);
    getContentPane().add(btnLogin);
    btnLogin.setMargin(new Insets(0, 0, 0, 0));

    txtUploader = new JTextField();
    txtUploader.setBounds(325, 44, 105, 23);
    getContentPane().add(txtUploader);
    txtUploader.setColumns(10);
    txtUploader.setEnabled(false);
    if (!Lizzie.config.uploadUser.equals("") && !Lizzie.config.uploadPassWd.equals("")) {
      SocketLoggin login = new SocketLoggin();
      String result = login.SocketLoggin(Lizzie.config.uploadUser, Lizzie.config.uploadPassWd);
      if (result.startsWith("success")) {
        btnLogin.setText(Lizzie.resourceBundle.getString("ShareFrame.reLogin")); // ("重新登录");
        txtUploader.setText(Lizzie.config.uploadUser);
      }
    }

    txtOtherInfo = new JTextField();
    txtOtherInfo.setBounds(123, 77, 403, 23);
    getContentPane().add(txtOtherInfo);
    txtOtherInfo.setColumns(10);

    JLabel lblBlack = new JLabel(Lizzie.resourceBundle.getString("ShareFrame.lblBlack")); // ("黑:");
    lblBlack.setBounds(10, 13, 25, 15);
    getContentPane().add(lblBlack);

    JLabel lblWhite = new JLabel(Lizzie.resourceBundle.getString("ShareFrame.lblWhite")); // ("白:");
    lblWhite.setBounds(291, 14, 25, 15);
    getContentPane().add(lblWhite);

    JLabel lblUploader =
        new JLabel(Lizzie.resourceBundle.getString("ShareFrame.lblUploader")); // ("上传者:");
    lblUploader.setBounds(Lizzie.config.isChinese ? 272 : 267, 48, 87, 15);
    getContentPane().add(lblUploader);

    JLabel lblOther =
        new JLabel(Lizzie.resourceBundle.getString("ShareFrame.lblOther")); // ("其他信息:");
    lblOther.setBounds(66, 81, 61, 15);
    getContentPane().add(lblOther);

    JLabel lblLabel = new JLabel(Lizzie.resourceBundle.getString("ShareFrame.lblLabel")); // "标签:");
    lblLabel.setBounds(Lizzie.config.isChinese ? 10 : 5, 48, 37, 15);
    getContentPane().add(lblLabel);

    otherLabel = new JTextField();
    otherLabel.setBounds(165, 44, 97, 23);
    getContentPane().add(otherLabel);
    otherLabel.setEnabled(false);

    cboxLabel = new JComboBox();
    setShareLabelCombox();
    cboxLabel.setBounds(73, 45, 85, 21);
    getContentPane().add(cboxLabel);
    cboxLabel.addItemListener(
        new ItemListener() {
          public void itemStateChanged(ItemEvent e) {
            if (e.getStateChange() == ItemEvent.SELECTED) {
              selectedLabelIndex = cboxLabel.getSelectedIndex();
            }
            if (selectedLabelIndex == (cboxLabel.getItemCount() - 1)) otherLabel.setEnabled(true);
            else {
              otherLabel.setEnabled(false);
              otherLabel.setText("");
            }
          }
        });

    ImageIcon iconSettings = new ImageIcon();
    try {
      iconSettings.setImage(
          ImageIO.read(AnalysisFrame.class.getResourceAsStream("/assets/config.png")));
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

    JButton btnConfig = new JButton(iconSettings);
    btnConfig.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            SetShareLabel setShareLabel = new SetShareLabel(shareFrame);
            setShareLabel.setVisible(true);
          }
        });
    btnConfig.setBounds(42, 44, 27, 23);
    getContentPane().add(btnConfig);

    chkPublic = new JCheckBox(Lizzie.resourceBundle.getString("ShareFrame.chkPublic")); // ("公开");
    if (Lizzie.config.isChinese) chkPublic.setBounds(10, 77, 50, 23);
    else chkPublic.setBounds(5, 77, 62, 23);
    chkPublic.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            // TBD
            if (chkPublic.isSelected()) {
              Lizzie.config.sharePublic = true;
              Lizzie.config.uiConfig.put("share-public", true);
            } else {
              Lizzie.config.sharePublic = false;
              Lizzie.config.uiConfig.put("share-public", false);
            }
          }
        });
    chkPublic.setSelected(Lizzie.config.sharePublic);
    getContentPane().add(chkPublic);

    JLabel lblNotice =
        new JLabel(
            Lizzie.resourceBundle.getString(
                "ShareFrame.lblNotice")); // ("注:请勿使用包含符号(例如><|\\/.,等)的黑白名字,标签,上传者,其他信息");
    lblNotice.setBounds(10, 110, 516, 15);
    getContentPane().add(lblNotice);
    //    checkBox.setVisible(false);
    try {
      this.setIconImage(ImageIO.read(MoreEngines.class.getResourceAsStream("/assets/logo.png")));
    } catch (IOException e) {
      e.printStackTrace();
    }

    btnApply.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            String b, w, up, bScore, wScore, allMove;
            b = formatStr(txtBlack.getText());
            w = formatStr(txtWhite.getText());
            up = formatStr(txtUploader.getText());
            if (Lizzie.board.getHistory().isEmptyBoard()) {
              Utils.showMsg(
                  Lizzie.resourceBundle.getString("ShareFrame.emptyKifu")); // ("当前棋谱为空,请先打开棋谱后分享");
              // msg.setVisible(true);
              return;
            }
            if (up.equals("")) {
              Utils.showMsg(
                  Lizzie.resourceBundle.getString("ShareFrame.emptyUploader")); // ("上传者为空,请先登录");
              //   msg.setVisible(true);
              return;
            }

            int move = 0;
            int analyzedMove = 0;
            int analyzedBlack = 0;
            int analyzedWhite = 0;
            double blackValue = 0;
            double whiteValue = 0;

            BoardHistoryNode node = Lizzie.board.getHistory().getEnd();
            while (node.previous().isPresent()) {
              move++;
              if (!node.getData().bestMoves.isEmpty()) analyzedMove++;
              node = node.previous().get();
              NodeInfo nodeInfo = node.nodeInfo;
              if (nodeInfo.analyzed) {
                if (nodeInfo.isBlack) {
                  blackValue = blackValue + nodeInfo.percentsMatch;
                  //                          + Math.pow(
                  //                              nodeInfo.percentsMatch,
                  //                              (double) 1 / Lizzie.config.matchAiTemperature);
                  analyzedBlack = analyzedBlack + 1;

                } else {
                  whiteValue = whiteValue + nodeInfo.percentsMatch;
                  //                          + Math.pow(
                  //                              nodeInfo.percentsMatch,
                  //                              (double) 1 / Lizzie.config.matchAiTemperature);
                  analyzedWhite = analyzedWhite + 1;
                }
              }
            }
            if (Lizzie.board.isPkBoard) bScore = "101"; // AI对局
            else {
              if (analyzedBlack >= 20)
                bScore = String.format(Locale.ENGLISH, "%.1f", blackValue * 100 / analyzedBlack);
              else {
                bScore = "-1"; // 分析不足20手
              }
            }
            if (Lizzie.board.isPkBoard) wScore = "101";
            else {
              if (analyzedWhite >= 20)
                wScore = String.format(Locale.ENGLISH, "%.1f", whiteValue * 100 / analyzedWhite);
              else {
                wScore = "-1";
              }
            }
            // analyzedMove = (analyzedBlack + analyzedWhite) + "";
            allMove = move + "";
            String analyzedMoveStr = analyzedMove + "";
            Runnable runnable =
                new Runnable() {
                  public void run() {
                    SocketUpfile socket = new SocketUpfile();
                    socket.SocketUpfile(
                        b.replaceAll(">", ""),
                        w.replaceAll(">", ""),
                        up.replaceAll(">", ""),
                        getLabel().replaceAll(">", ""),
                        txtOtherInfo.getText().replaceAll(">", "").equals("")
                            ? " "
                            : txtOtherInfo.getText().replaceAll(">", ""),
                        Lizzie.config.sharePublic,
                        bScore,
                        wScore,
                        allMove,
                        analyzedMoveStr,
                        false);
                  }
                };
            Thread th = new Thread(runnable);
            th.start();
            setVisible(false);
          }
        });
    // setSize(558, 199);
    Lizzie.setFrameSize(this, 547, 192);
  }

  public void setShareLabelCombox() {
    cboxLabel.removeAllItems();

    // cboxLabel.removeItemListener(lis);
    if (!Lizzie.config.shareLabel1.equals("")) {
      cboxLabel.addItem(Lizzie.config.shareLabel1);
    }
    if (!Lizzie.config.shareLabel2.equals("")) {
      cboxLabel.addItem(Lizzie.config.shareLabel2);
    }
    if (!Lizzie.config.shareLabel3.equals("")) {
      cboxLabel.addItem(Lizzie.config.shareLabel3);
    }
    if (!Lizzie.config.shareLabel4.equals("")) {
      cboxLabel.addItem(Lizzie.config.shareLabel4);
    }
    if (!Lizzie.config.shareLabel5.equals("")) {
      cboxLabel.addItem(Lizzie.config.shareLabel5);
    }
    cboxLabel.addItem(Lizzie.resourceBundle.getString("ShareFrame.shareLabelOther")); // ("其他");
    otherLabel.setEnabled(false);
    otherLabel.setText("");
    // cboxLabel.addItemListener(lis);
  }

  public String getLabel() {
    if (otherLabel.isEnabled()) return otherLabel.getText();
    else {
      switch (selectedLabelIndex) {
        case 0:
          return Lizzie.config.shareLabel1;
        case 1:
          return Lizzie.config.shareLabel2;
        case 2:
          return Lizzie.config.shareLabel3;
        case 3:
          return Lizzie.config.shareLabel4;
        case 4:
          return Lizzie.config.shareLabel5;
      }
    }
    return "";
  }

  public String formatStr(String inputStr) {
    inputStr = inputStr.replaceAll("[/\\\\:*?|]", "");
    inputStr = inputStr.replaceAll("[\"<>]", "");
    return inputStr;
  }
}
