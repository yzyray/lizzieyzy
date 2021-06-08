package featurecat.lizzie.gui;

import featurecat.lizzie.Lizzie;
import featurecat.lizzie.rules.BoardHistoryNode;
import featurecat.lizzie.rules.NodeInfo;
import java.awt.FileDialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JTextField;
import org.json.JSONObject;

public class BatchShareFrame extends JDialog {
  public JTextField txtUploader;
  private JTextField txtOtherInfo;
  private JTextField otherLabel;
  private JDialog shareFrame = this;
  int selectedLabelIndex = 0;
  JComboBox cboxLabel;
  ItemListener lis;
  JCheckBox checkBox;

  public BatchShareFrame() {
    this.setModal(true);
    // setType(Type.POPUP);
    setResizable(false);
    setTitle("批量分享");
    setAlwaysOnTop(Lizzie.frame.isAlwaysOnTop());
    setLocationRelativeTo(Lizzie.frame);
    getContentPane().setLayout(null);

    JButton btnApply = new JButton("选择棋谱并分享");
    btnApply.setBounds(151, 108, 117, 23);
    getContentPane().add(btnApply);

    JButton btnCancel = new JButton("取消");
    btnCancel.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            setVisible(false);
          }
        });
    btnCancel.setBounds(285, 108, 117, 23);
    getContentPane().add(btnCancel);

    JButton btnLoggin = new JButton("注册/登录");
    btnLoggin.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            Loggin loggin = new Loggin(shareFrame, false);
            loggin.setVisible(true);
          }
        });
    btnLoggin.setBounds(440, 10, 87, 23);
    getContentPane().add(btnLoggin);

    txtUploader = new JTextField();
    txtUploader.setBounds(325, 10, 105, 23);
    getContentPane().add(txtUploader);
    txtUploader.setColumns(10);
    txtUploader.setEnabled(false);
    if (!Lizzie.config.uploadUser.equals("") && !Lizzie.config.uploadPassWd.equals("")) {
      SocketLoggin login = new SocketLoggin();
      String result = login.SocketLoggin(Lizzie.config.uploadUser, Lizzie.config.uploadPassWd);
      if (result.startsWith("success")) {
        btnLoggin.setText("重新登录");
        txtUploader.setText(Lizzie.config.uploadUser);
      }
    }

    txtOtherInfo = new JTextField();
    txtOtherInfo.setBounds(123, 43, 403, 23);
    getContentPane().add(txtOtherInfo);
    txtOtherInfo.setColumns(10);

    JLabel label_2 = new JLabel("上传者:");
    label_2.setBounds(272, 14, 46, 15);
    getContentPane().add(label_2);

    JLabel label_3 = new JLabel("其他信息:");
    label_3.setBounds(66, 47, 61, 15);
    getContentPane().add(label_3);

    JLabel label_4 = new JLabel("标签:");
    label_4.setBounds(10, 14, 37, 15);
    getContentPane().add(label_4);

    otherLabel = new JTextField();
    otherLabel.setBounds(165, 10, 97, 23);
    getContentPane().add(otherLabel);
    otherLabel.setEnabled(false);

    cboxLabel = new JComboBox();
    setShareLabelCombox();
    cboxLabel.setBounds(73, 11, 85, 21);
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
    btnConfig.setBounds(42, 10, 27, 23);
    getContentPane().add(btnConfig);

    checkBox = new JCheckBox("公开");
    checkBox.setBounds(10, 43, 50, 23);
    checkBox.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            // TBD
            if (checkBox.isSelected()) {
              Lizzie.config.sharePublic = true;
              Lizzie.config.uiConfig.put("share-public", true);
            } else {
              Lizzie.config.sharePublic = false;
              Lizzie.config.uiConfig.put("share-public", false);
            }
          }
        });
    checkBox.setSelected(Lizzie.config.sharePublic);
    getContentPane().add(checkBox);

    JLabel label_5 = new JLabel("注:请勿使用包含符号(例如><|\\/.,等)标签,上传者,其他信息,黑白名字将会根据棋谱内信息上传");
    label_5.setBounds(10, 76, 516, 15);
    getContentPane().add(label_5);
    //    checkBox.setVisible(false);
    try {
      this.setIconImage(ImageIO.read(MoreEngines.class.getResourceAsStream("/assets/logo.png")));
    } catch (IOException e) {
      e.printStackTrace();
    }

    btnApply.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            String up = formatStr(txtUploader.getText());
            if (up.equals("")) {
              Message msg = new Message();
              msg.setMessage("上传者为空,请先登录");
              //   msg.setVisible(true);
              return;
            }
            Lizzie.frame.setAlwaysOnTop(false);
            setAlwaysOnTop(false);
            JSONObject filesystem = Lizzie.config.persisted.getJSONObject("filesystem");
            FileDialog fileDialog =
                new FileDialog(
                    Lizzie.frame,
                    Lizzie.resourceBundle.getString("LizzieFrame.chooseKifu")); // "选择棋谱");
            fileDialog.setLocationRelativeTo(Lizzie.frame);
            fileDialog.setAlwaysOnTop(true);
            fileDialog.setDirectory(filesystem.getString("last-folder"));
            fileDialog.setFile("*.sgf;*.gib;*.SGF;*.GIB");

            fileDialog.setMultipleMode(true);
            fileDialog.setMode(0);
            fileDialog.setVisible(true);

            File[] files = fileDialog.getFiles();

            for (int i = 0; i < files.length; i++) {
              Lizzie.frame.loadFile(files[i], false, false);
              upFile(files[i].getName());
            }
            Lizzie.frame.setAlwaysOnTop(Lizzie.config.mainsalwaysontop);
            setAlwaysOnTop(Lizzie.config.mainsalwaysontop);
            if (files.length > 0) {
              setVisible(false);
              PrivateKifuSearch search = new PrivateKifuSearch();
              search.setVisible(true);
              search.searchToday();
            }
          }
        });
    //   setSize(558, 170);
    Lizzie.setFrameSize(this, 558, 170);
  }

  public void upFile(String fileName) {
    String b, w, up, bScore, wScore, allMove;
    b = Lizzie.board.getHistory().getGameInfo().getPlayerBlack();
    w = Lizzie.board.getHistory().getGameInfo().getPlayerWhite();
    up = formatStr(txtUploader.getText());
    if (Lizzie.board.getHistory().isEmptyBoard()) {
      //   Message msg = new Message();
      //   msg.setMessage("当前棋谱为空,请先打开棋谱后分享");
      // msg.setVisible(true);
      return;
    }
    if (up.equals("")) {
      //   Message msg = new Message();
      //   msg.setMessage("上传者为空,请先登录");
      // msg.setVisible(true);
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
          // + Math.pow(nodeInfo.percentsMatch, (double) 1 / Lizzie.config.matchAiTemperature);
          analyzedBlack = analyzedBlack + 1;

        } else {
          whiteValue = whiteValue + nodeInfo.percentsMatch;
          //   + Math.pow(nodeInfo.percentsMatch, (double) 1 / Lizzie.config.matchAiTemperature);
          analyzedWhite = analyzedWhite + 1;
        }
      }
    }
    if (Lizzie.board.isPkBoard) bScore = "101"; // AI对局
    else {
      if (analyzedBlack >= 20) bScore = String.format("%.1f", blackValue * 100 / analyzedBlack);
      else {
        bScore = "-1"; // 分析不足20手
      }
    }
    if (Lizzie.board.isPkBoard) wScore = "101";
    else {
      if (analyzedWhite >= 20) wScore = String.format("%.1f", whiteValue * 100 / analyzedWhite);
      else {
        wScore = "-1";
      }
    }
    // analyzedMove = (analyzedBlack + analyzedWhite) + "";
    allMove = move + "";
    String analyzedMoveStr = analyzedMove + "";
    SocketUpfile socket = new SocketUpfile();
    socket.SocketUpfile(
        b.replaceAll(">", ""),
        w.replaceAll(">", ""),
        up.replaceAll(">", ""),
        getLabel().replaceAll(">", ""),
        txtOtherInfo.getText().replaceAll(">", "").equals("")
            ? fileName
            : txtOtherInfo.getText().replaceAll(">", "") + " " + fileName,
        Lizzie.config.sharePublic,
        bScore,
        wScore,
        allMove,
        analyzedMoveStr,
        true);
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
    cboxLabel.addItem("其他");
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
