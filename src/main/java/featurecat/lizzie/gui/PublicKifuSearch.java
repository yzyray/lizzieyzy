package featurecat.lizzie.gui;

import featurecat.lizzie.Lizzie;
import featurecat.lizzie.util.Utils;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Desktop;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import javax.imageio.ImageIO;
import javax.swing.AbstractCellEditor;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import org.jdesktop.swingx.JXDatePicker;
import org.json.JSONArray;

public class PublicKifuSearch extends JFrame {
  private final ResourceBundle resourceBundle = Lizzie.resourceBundle;
  private DefaultTableModel model;
  private JTable table;
  private final JPanel panel = new JPanel();
  private final JPanel panel_1 = new JPanel();
  private JTextField txtBlackOrWhite;
  private JTextField txtUploader;
  private JTextField txtLabel;
  private JTextField txtWhite;
  private JTextField txtBlack;
  JLabel lbl_tab;
  private JTextField txtOtherInfo;
  int tabNumber = 1;
  int numbersPerTab = 25;
  int curTabNumber = 1;
  ArrayList<String[]> rows;
  private JTextField txtBScore;
  private JTextField txtWScore;
  private JTextField txtAllMove;
  private JTextField txtAnalyzedMove;
  JXDatePicker datepick;
  JXDatePicker datepick2;

  public PublicKifuSearch() {
    //		 this.setModal(true);
    // setType(Type.POPUP);
    setTitle(resourceBundle.getString("PublicKifuSearch.title")); // "公开棋谱查询");
    setAlwaysOnTop(Lizzie.frame.isAlwaysOnTop());

    addWindowListener(
        new WindowAdapter() {
          public void windowClosing(WindowEvent e) {
            try {
              Lizzie.config.persist();
            } catch (IOException es) {
              // TODO Auto-generated catch block
            }
            setVisible(false);
          }
        });
    //		    setResizable(false);
    boolean formPersis = false;
    boolean persisted = Lizzie.config.persistedUi != null;
    if (persisted && Lizzie.config.persistedUi.optJSONArray("public-kifu-search") != null) {
      JSONArray pos = Lizzie.config.persistedUi.getJSONArray("public-kifu-search");

      if (pos.length() == 4) {
        setBounds(pos.getInt(0), pos.getInt(1), pos.getInt(2), pos.getInt(3));
        formPersis = true;
      }
    }
    if (!formPersis) {
      // setSize(995, 569);
      Lizzie.setFrameSize(this, 1100, 569);
      setLocationRelativeTo(Lizzie.frame);
    }
    try {
      this.setIconImage(ImageIO.read(MoreEngines.class.getResourceAsStream("/assets/logo.png")));
    } catch (IOException e1) {
      // TODO Auto-generated catch block
      e1.printStackTrace();
    }
    table = new JTable();
    getContentPane().setLayout(new BorderLayout(0, 0));
    getContentPane().add(panel, BorderLayout.NORTH);
    String DefaultFormat = "yyyy-MM-dd";
    Date date = new Date();
    date = getDateBefore(date, 3);
    Date date2 = new Date();
    GridBagLayout gbl_panel = new GridBagLayout();
    gbl_panel.columnWidths = new int[] {56, 82, 56, 82, 56, 82, 56, 82, 56, 82, 56, 82, 56, 82, 0};
    gbl_panel.rowHeights = new int[] {23, 23, 0};
    gbl_panel.columnWeights =
        new double[] {
          0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE
        };
    gbl_panel.rowWeights = new double[] {0.0, 0.0, Double.MIN_VALUE};
    panel.setLayout(gbl_panel);

    JLabel label_blackOrWhite = new JLabel(resourceBundle.getString("PrivateKifuSearch.labelName"));
    GridBagConstraints gbc_blackOrWhite = new GridBagConstraints();
    gbc_blackOrWhite.fill = GridBagConstraints.VERTICAL;
    gbc_blackOrWhite.insets = new Insets(0, 0, 5, 5);
    gbc_blackOrWhite.gridx = 0;
    gbc_blackOrWhite.gridy = 0;
    panel.add(label_blackOrWhite, gbc_blackOrWhite);

    txtBlackOrWhite = new JTextField();
    GridBagConstraints gbc_txtBlackOrWhite = new GridBagConstraints();
    gbc_txtBlackOrWhite.fill = GridBagConstraints.BOTH;
    gbc_txtBlackOrWhite.insets = new Insets(0, 0, 5, 5);
    gbc_txtBlackOrWhite.gridx = 1;
    gbc_txtBlackOrWhite.gridy = 0;
    panel.add(txtBlackOrWhite, gbc_txtBlackOrWhite);
    txtBlackOrWhite.setColumns(4);

    JLabel label_5 =
        new JLabel(resourceBundle.getString("PrivateKifuSearch.labelBlack")); // ("黑方:");
    GridBagConstraints gbc_label_5 = new GridBagConstraints();
    gbc_label_5.fill = GridBagConstraints.VERTICAL;
    gbc_label_5.insets = new Insets(0, 0, 5, 5);
    gbc_label_5.gridx = 2;
    gbc_label_5.gridy = 0;
    panel.add(label_5, gbc_label_5);

    txtBlack = new JTextField();
    GridBagConstraints gbc_txtBlack = new GridBagConstraints();
    gbc_txtBlack.fill = GridBagConstraints.BOTH;
    gbc_txtBlack.insets = new Insets(0, 0, 5, 5);
    gbc_txtBlack.gridx = 3;
    gbc_txtBlack.gridy = 0;
    panel.add(txtBlack, gbc_txtBlack);
    txtBlack.setColumns(4);

    JLabel label_7 =
        new JLabel(resourceBundle.getString("PrivateKifuSearch.labelScore")); // ("评分(大于):");
    GridBagConstraints gbc_label_7 = new GridBagConstraints();
    gbc_label_7.fill = GridBagConstraints.VERTICAL;
    gbc_label_7.insets = new Insets(0, 0, 5, 5);
    gbc_label_7.gridx = 4;
    gbc_label_7.gridy = 0;
    panel.add(label_7, gbc_label_7);

    txtBScore = new JTextField();
    GridBagConstraints gbc_txtBScore = new GridBagConstraints();
    gbc_txtBScore.fill = GridBagConstraints.BOTH;
    gbc_txtBScore.insets = new Insets(0, 0, 5, 5);
    gbc_txtBScore.gridx = 5;
    gbc_txtBScore.gridy = 0;
    panel.add(txtBScore, gbc_txtBScore);
    txtBScore.setColumns(3);

    JLabel label_4 =
        new JLabel(resourceBundle.getString("PrivateKifuSearch.labelWhite")); // ("白方:");
    GridBagConstraints gbc_label_4 = new GridBagConstraints();
    gbc_label_4.fill = GridBagConstraints.VERTICAL;
    gbc_label_4.insets = new Insets(0, 0, 5, 5);
    gbc_label_4.gridx = 6;
    gbc_label_4.gridy = 0;
    panel.add(label_4, gbc_label_4);

    txtWhite = new JTextField();
    GridBagConstraints gbc_txtWhite = new GridBagConstraints();
    gbc_txtWhite.fill = GridBagConstraints.BOTH;
    gbc_txtWhite.insets = new Insets(0, 0, 5, 5);
    gbc_txtWhite.gridx = 7;
    gbc_txtWhite.gridy = 0;
    panel.add(txtWhite, gbc_txtWhite);
    txtWhite.setColumns(4);

    JLabel label_8 =
        new JLabel(resourceBundle.getString("PrivateKifuSearch.labelScore")); // ("评分(大于):");
    GridBagConstraints gbc_label_8 = new GridBagConstraints();
    gbc_label_8.fill = GridBagConstraints.VERTICAL;
    gbc_label_8.insets = new Insets(0, 0, 5, 5);
    gbc_label_8.gridx = 8;
    gbc_label_8.gridy = 0;
    panel.add(label_8, gbc_label_8);

    txtWScore = new JTextField();
    GridBagConstraints gbc_txtWScore = new GridBagConstraints();
    gbc_txtWScore.fill = GridBagConstraints.BOTH;
    gbc_txtWScore.insets = new Insets(0, 0, 5, 5);
    gbc_txtWScore.gridx = 9;
    gbc_txtWScore.gridy = 0;
    panel.add(txtWScore, gbc_txtWScore);
    txtWScore.setColumns(3);

    JLabel label_10 =
        new JLabel(resourceBundle.getString("PrivateKifuSearch.labelMove")); // ("手数(大于):");
    GridBagConstraints gbc_label_10 = new GridBagConstraints();
    gbc_label_10.fill = GridBagConstraints.VERTICAL;
    gbc_label_10.insets = new Insets(0, 0, 5, 5);
    gbc_label_10.gridx = 10;
    gbc_label_10.gridy = 0;
    panel.add(label_10, gbc_label_10);

    txtAllMove = new JTextField();
    GridBagConstraints gbc_txtAllMove = new GridBagConstraints();
    gbc_txtAllMove.fill = GridBagConstraints.BOTH;
    gbc_txtAllMove.insets = new Insets(0, 0, 5, 5);
    gbc_txtAllMove.gridx = 11;
    gbc_txtAllMove.gridy = 0;
    panel.add(txtAllMove, gbc_txtAllMove);
    txtAllMove.setColumns(3);

    JLabel label_9 =
        new JLabel(resourceBundle.getString("PrivateKifuSearch.labelAnalyzed")); // ("已分析(大于):");
    GridBagConstraints gbc_label_9 = new GridBagConstraints();
    gbc_label_9.fill = GridBagConstraints.VERTICAL;
    gbc_label_9.insets = new Insets(0, 0, 5, 5);
    gbc_label_9.gridx = 12;
    gbc_label_9.gridy = 0;
    panel.add(label_9, gbc_label_9);

    txtAnalyzedMove = new JTextField();
    GridBagConstraints gbc_txtAnalyzedMove = new GridBagConstraints();
    gbc_txtAnalyzedMove.fill = GridBagConstraints.BOTH;
    gbc_txtAnalyzedMove.insets = new Insets(0, 0, 5, 0);
    gbc_txtAnalyzedMove.gridx = 13;
    gbc_txtAnalyzedMove.gridy = 0;
    panel.add(txtAnalyzedMove, gbc_txtAnalyzedMove);
    txtAnalyzedMove.setColumns(3);

    JLabel label =
        new JLabel(resourceBundle.getString("PrivateKifuSearch.labelUploader")); // ("上传者:");
    GridBagConstraints gbc_label = new GridBagConstraints();
    gbc_label.fill = GridBagConstraints.VERTICAL;
    gbc_label.insets = new Insets(0, 0, 0, 5);
    gbc_label.gridx = 0;
    gbc_label.gridy = 1;
    panel.add(label, gbc_label);

    txtUploader = new JTextField();
    GridBagConstraints gbc_txtUploader = new GridBagConstraints();
    gbc_txtUploader.fill = GridBagConstraints.BOTH;
    gbc_txtUploader.insets = new Insets(0, 0, 0, 5);
    gbc_txtUploader.gridx = 1;
    gbc_txtUploader.gridy = 1;
    panel.add(txtUploader, gbc_txtUploader);
    txtUploader.setColumns(4);

    JLabel label_3 =
        new JLabel(resourceBundle.getString("PrivateKifuSearch.labelLabel")); // ("标签:");
    GridBagConstraints gbc_label_3 = new GridBagConstraints();
    gbc_label_3.fill = GridBagConstraints.VERTICAL;
    gbc_label_3.insets = new Insets(0, 0, 0, 5);
    gbc_label_3.gridx = 2;
    gbc_label_3.gridy = 1;
    panel.add(label_3, gbc_label_3);

    JButton button =
        new JButton(resourceBundle.getString("PrivateKifuSearch.btnSearch")); // ("查询");
    button.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            //  try {
            //      con = dbConn("", "");
            //     if (con == null) {
            //       System.out.print("Database connection error");
            //      }
            //   sql = con.createStatement();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            double bScore = 0, wScore = 0;
            int allMove = 0, analyzedMove = 0;
            try {
              bScore = Double.parseDouble(txtBScore.getText());
            } catch (Exception a) {
            }
            try {
              wScore = Double.parseDouble(txtWScore.getText());
            } catch (Exception a) {
            }
            try {
              allMove = Integer.parseInt(txtAllMove.getText());
            } catch (Exception a) {
            }
            try {
              analyzedMove = Integer.parseInt(txtAnalyzedMove.getText());
            } catch (Exception a) {
            }
            String searchScoreAndMove = "";
            if (bScore > 0) {
              searchScoreAndMove = "and bscore>" + bScore;
            }
            if (wScore > 0) {
              searchScoreAndMove += "and wscore>" + wScore;
            }
            if (allMove > 0) {
              searchScoreAndMove += "and allMove>" + allMove;
            }
            if (analyzedMove > 0) {
              searchScoreAndMove += "and analyzedMove>" + analyzedMove;
            }
            String sqlText =
                "select  rownum as \""
                    + resourceBundle.getString("PrivateKifuSearch.sql.index")
                    + "\",a.* from (select black "
                    + "\""
                    + resourceBundle.getString("PrivateKifuSearch.sql.black")
                    + "\""
                    + ",white "
                    + "\""
                    + resourceBundle.getString("PrivateKifuSearch.sql.white")
                    + "\""
                    + ",case when bscore>=0"
                    + " and bscore<=100 then bscore when bscore>100 then '"
                    + resourceBundle.getString("PrivateKifuSearch.sql.AIGame")
                    + "' when bsc"
                    + "ore =-1 then '"
                    + resourceBundle.getString("PrivateKifuSearch.sql.lackInfo")
                    + "' ELSE  '' end as "
                    + "\""
                    + resourceBundle.getString("PrivateKifuSearch.sql.blackScore")
                    + "\""
                    + ",case when wscore"
                    + ">=0 and wscore<=100 then wscore when wscore>100 then '"
                    + resourceBundle.getString("PrivateKifuSearch.sql.AIGame")
                    + "' when w"
                    + "score =-1 then '"
                    + resourceBundle.getString("PrivateKifuSearch.sql.lackInfo")
                    + "' ELSE  '' end as "
                    + "\""
                    + resourceBundle.getString("PrivateKifuSearch.sql.whiteScore")
                    + "\""
                    + ",case when analyzedmove>0"
                    + " then analyzedmove else '' end as "
                    + "\""
                    + resourceBundle.getString("PrivateKifuSearch.sql.analyzed")
                    + "\""
                    + ",case when allmove>0 then allm"
                    + "ove else '' end as  "
                    + "\""
                    + resourceBundle.getString("PrivateKifuSearch.sql.move")
                    + "\""
                    + ",uploader "
                    + "\""
                    + resourceBundle.getString("PrivateKifuSearch.sql.uploader")
                    + "\""
                    + ",label "
                    + "\""
                    + resourceBundle.getString("PrivateKifuSearch.sql.label")
                    + "\""
                    + ",otherinfo "
                    + "\""
                    + resourceBundle.getString("PrivateKifuSearch.sql.otherInfo")
                    + "\""
                    + ",crea"
                    + "tetime "
                    + "\""
                    + resourceBundle.getString("PrivateKifuSearch.sql.uploadTime")
                    + "\""
                    + ",url "
                    + "\""
                    + resourceBundle.getString("PrivateKifuSearch.sql.url")
                    + "\""
                    + ",''as  "
                    + "\""
                    + resourceBundle.getString("PrivateKifuSearch.sql.copy")
                    + "\""
                    + ",'' as"
                    + " "
                    + "\""
                    + resourceBundle.getString("PrivateKifuSearch.sql.view")
                    + "\""
                    + ",'' as "
                    + "\""
                    + resourceBundle.getString("PrivateKifuSearch.sql.open")
                    + "\""
                    + " from public_search where  createtime between to_date('"
                    + sdf.format(datepick.getDate())
                    + "','yyyy-mm-dd') and to_date('"
                    + sdf.format(getDateBefore(datepick2.getDate(), -1))
                    + "','yyyy-mm-dd')"
                    + "and black like '%"
                    + txtBlack.getText()
                    + "%' and white like '%"
                    + txtWhite.getText()
                    + "%' and (white like '%"
                    + txtBlackOrWhite.getText()
                    + "%' or black like '%"
                    + txtBlackOrWhite.getText()
                    + "%') and uploader like '%"
                    + txtUploader.getText()
                    + "%' and otherinfo like '%"
                    + txtOtherInfo.getText()
                    + "%' and label like'%"
                    + txtLabel.getText()
                    + "%' "
                    + searchScoreAndMove
                    + " order by createtime desc)"
                    + "a";
            SocketKifuSearch socketKifuSearch = new SocketKifuSearch();
            List<String> sqlResult = socketKifuSearch.SocketKifuSearch(sqlText);
            //    rs = sql.executeQuery(sqlText);
            //    ResultSetMetaData rsmd = rs.getMetaData();
            // 获得列数
            if (sqlResult != null) {
              model = new DefaultTableModel();
              table.setModel(model);
              String[] params = sqlResult.get(0).split(">->");
              for (int i = 0; i < params.length; i++) {
                model.addColumn(params[i]);
              }
              table.getColumnModel().getColumn(0).setPreferredWidth(30);
              table.getColumnModel().getColumn(1).setPreferredWidth(80);
              table.getColumnModel().getColumn(2).setPreferredWidth(80);
              table.getColumnModel().getColumn(3).setPreferredWidth(50);
              table.getColumnModel().getColumn(4).setPreferredWidth(50);
              table.getColumnModel().getColumn(5).setPreferredWidth(40);
              table.getColumnModel().getColumn(6).setPreferredWidth(40);
              table.getColumnModel().getColumn(7).setPreferredWidth(60);
              table.getColumnModel().getColumn(8).setPreferredWidth(60);
              table.getColumnModel().getColumn(9).setPreferredWidth(160);
              table.getColumnModel().getColumn(10).setPreferredWidth(70);
              table.getColumnModel().getColumn(11).setPreferredWidth(60);
              table.getColumnModel().getColumn(12).setPreferredWidth(48);
              table.getColumnModel().getColumn(13).setPreferredWidth(48);
              table.getColumnModel().getColumn(14).setPreferredWidth(48);
              table.getColumnModel().getColumn(12).setCellEditor(new MyButtonCopyEditor());
              table.getColumnModel().getColumn(12).setCellRenderer(new MyButtonCopy());
              table.getColumnModel().getColumn(13).setCellEditor(new MyButtonOpenEditor());
              table.getColumnModel().getColumn(13).setCellRenderer(new MyButtonOpen());
              table.getColumnModel().getColumn(14).setCellEditor(new MyButtonOpenHtmlEditor());
              table.getColumnModel().getColumn(14).setCellRenderer(new MyButtonOpenHtml());

              rows = new ArrayList<String[]>();
              for (int i = 1; i < sqlResult.size(); i++) {
                String[] rowParams = sqlResult.get(i).split(">->");
                rows.add(rowParams);
              }
              tabNumber = (int) Math.ceil(rows.size() / (double) numbersPerTab);
              curTabNumber = 1;
              for (int i = 0; i < numbersPerTab && i < rows.size(); i++) {
                model.addRow(rows.get(i));
              }
              lbl_tab.setText("1/" + tabNumber);
              table.updateUI();
            }
          }
        });

    txtLabel = new JTextField();
    GridBagConstraints gbc_txtLabel = new GridBagConstraints();
    gbc_txtLabel.fill = GridBagConstraints.BOTH;
    gbc_txtLabel.insets = new Insets(0, 0, 0, 5);
    gbc_txtLabel.gridx = 3;
    gbc_txtLabel.gridy = 1;
    panel.add(txtLabel, gbc_txtLabel);
    txtLabel.setColumns(4);

    JLabel label_6 =
        new JLabel(resourceBundle.getString("PrivateKifuSearch.labelOther")); // ("其他:");
    GridBagConstraints gbc_label_6 = new GridBagConstraints();
    gbc_label_6.fill = GridBagConstraints.VERTICAL;
    gbc_label_6.insets = new Insets(0, 0, 0, 5);
    gbc_label_6.gridx = 4;
    gbc_label_6.gridy = 1;
    panel.add(label_6, gbc_label_6);

    txtOtherInfo = new JTextField();
    GridBagConstraints gbc_txtOtherInfo = new GridBagConstraints();
    gbc_txtOtherInfo.fill = GridBagConstraints.BOTH;
    gbc_txtOtherInfo.insets = new Insets(0, 0, 0, 5);
    gbc_txtOtherInfo.gridx = 5;
    gbc_txtOtherInfo.gridy = 1;
    panel.add(txtOtherInfo, gbc_txtOtherInfo);
    txtOtherInfo.setColumns(4);

    JLabel label_2 =
        new JLabel(resourceBundle.getString("PrivateKifuSearch.labelDateStart")); // ("开始日期:");
    GridBagConstraints gbc_label_2 = new GridBagConstraints();
    gbc_label_2.fill = GridBagConstraints.VERTICAL;
    gbc_label_2.insets = new Insets(0, 0, 0, 5);
    gbc_label_2.gridx = 6;
    gbc_label_2.gridy = 1;
    panel.add(label_2, gbc_label_2);
    datepick = new JXDatePicker();
    datepick.setFormats(DefaultFormat);
    datepick.setDate(date);
    GridBagConstraints gbc_datepick = new GridBagConstraints();
    gbc_datepick.fill = GridBagConstraints.BOTH;
    gbc_datepick.insets = new Insets(0, 0, 0, 5);
    gbc_datepick.gridx = 7;
    gbc_datepick.gridy = 1;
    panel.add(datepick, gbc_datepick);

    JLabel label_1 =
        new JLabel(resourceBundle.getString("PrivateKifuSearch.labelDateEnd")); // ("结束日期:");
    GridBagConstraints gbc_label_1 = new GridBagConstraints();
    gbc_label_1.fill = GridBagConstraints.VERTICAL;
    gbc_label_1.insets = new Insets(0, 0, 0, 5);
    gbc_label_1.gridx = 8;
    gbc_label_1.gridy = 1;
    panel.add(label_1, gbc_label_1);

    datepick2 = new JXDatePicker();
    datepick2.setFormats(DefaultFormat);
    datepick2.setDate(date2);
    GridBagConstraints gbc_datepick2 = new GridBagConstraints();
    gbc_datepick2.fill = GridBagConstraints.BOTH;
    gbc_datepick2.insets = new Insets(0, 0, 0, 5);
    gbc_datepick2.gridx = 9;
    gbc_datepick2.gridy = 1;
    panel.add(datepick2, gbc_datepick2);
    GridBagConstraints gbc_button = new GridBagConstraints();
    gbc_button.gridwidth = 2;
    gbc_button.fill = GridBagConstraints.VERTICAL;
    gbc_button.insets = new Insets(0, 0, 0, 5);
    gbc_button.gridx = 10;
    gbc_button.gridy = 1;
    panel.add(button, gbc_button);
    JScrollPane scrollPane = new JScrollPane(table);
    getContentPane().add(scrollPane, BorderLayout.CENTER);
    getContentPane().add(panel_1, BorderLayout.SOUTH);

    JButton btnFirst = new JButton("|<");
    btnFirst.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            if (rows == null) return;
            curTabNumber = 1;
            while (model.getRowCount() > 0) {
              model.removeRow(model.getRowCount() - 1);
            }
            for (int i = (curTabNumber - 1) * numbersPerTab;
                i < numbersPerTab && i < rows.size();
                i++) {
              model.addRow(rows.get(i));
            }
            lbl_tab.setText(curTabNumber + "/" + tabNumber);
          }
        });
    panel_1.add(btnFirst);

    JButton btnPrevious = new JButton("<");
    btnPrevious.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            if (rows == null) return;
            if (curTabNumber == 1) return;
            curTabNumber = curTabNumber - 1;
            while (model.getRowCount() > 0) {
              model.removeRow(model.getRowCount() - 1);
            }
            for (int i = (curTabNumber - 1) * numbersPerTab;
                i < curTabNumber * numbersPerTab && i < rows.size();
                i++) {
              model.addRow(rows.get(i));
            }
            lbl_tab.setText(curTabNumber + "/" + tabNumber);
          }
        });
    panel_1.add(btnPrevious);

    JButton btnNext = new JButton(">");
    btnNext.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            if (rows == null) return;
            if (curTabNumber == tabNumber) return;
            curTabNumber = curTabNumber + 1;
            while (model.getRowCount() > 0) {
              model.removeRow(model.getRowCount() - 1);
            }
            for (int i = (curTabNumber - 1) * numbersPerTab;
                i < curTabNumber * numbersPerTab && i < rows.size();
                i++) {
              model.addRow(rows.get(i));
            }
            lbl_tab.setText(curTabNumber + "/" + tabNumber);
          }
        });
    panel_1.add(btnNext);

    JButton btnLast = new JButton(">|");
    btnLast.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            if (rows == null) return;
            if (curTabNumber == tabNumber) return;
            curTabNumber = tabNumber;
            while (model.getRowCount() > 0) {
              model.removeRow(model.getRowCount() - 1);
            }
            for (int i = (curTabNumber - 1) * numbersPerTab;
                i < curTabNumber * numbersPerTab && i < rows.size();
                i++) {
              model.addRow(rows.get(i));
            }
            lbl_tab.setText(curTabNumber + "/" + tabNumber);
          }
        });
    panel_1.add(btnLast);

    lbl_tab = new JLabel("1/1");
    panel_1.add(lbl_tab);
  }

  public static Date getDateBefore(Date d, int day) {
    Calendar now = Calendar.getInstance();
    now.setTime(d);
    now.set(Calendar.DATE, now.get(Calendar.DATE) - day);
    return now.getTime();
  }

  //  public static Connection dbConn(String name, String pass) {
  //    Connection c = null;
  //    try {
  //      Class.forName("oracle.jdbc.driver.OracleDriver");
  //    } catch (ClassNotFoundException e) {
  //      e.printStackTrace();
  //    }
  //    try {
  //      c = DriverManager.getConnection("jdbc:oracle:thin:@ip:port:ORCL", name, pass);
  //
  //    } catch (SQLException e) {
  //      e.printStackTrace();
  //    }
  //    return c;
  //  }
}

class MyButtonCopy implements TableCellRenderer {
  private JPanel panel;
  private JButton button;

  public MyButtonCopy() {
    initButton();
    initPanel();
    panel.add(button, BorderLayout.CENTER);
  }

  private void initButton() {
    button = new JButton();
    button.setMargin(new Insets(0, 0, 0, 0));
  }

  private void initPanel() {
    panel = new JPanel();
    panel.setLayout(new BorderLayout());
  }

  public Component getTableCellRendererComponent(
      JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
    button.setText(Lizzie.resourceBundle.getString("PrivateKifuSearch.copy"));
    return panel;
  }
}

class MyButtonCopyEditor extends AbstractCellEditor implements TableCellEditor {
  private JPanel panel;
  private JButton button;
  private String URL = "";

  public MyButtonCopyEditor() {
    initButton();
    initPanel();
    panel.add(this.button, BorderLayout.CENTER);
  }

  private void initButton() {
    button = new JButton();
    button.setMargin(new Insets(0, 0, 0, 0));
    button.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {

            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            Transferable transferableString = new StringSelection(URL);
            clipboard.setContents(transferableString, null);
            Utils.showMsg(Lizzie.resourceBundle.getString("PrivateKifuSearch.copySuccess")); // );
          }
        });
  }

  private void initPanel() {
    panel = new JPanel();
    panel.setLayout(new BorderLayout());
  }

  @Override
  public Component getTableCellEditorComponent(
      JTable table, Object value, boolean isSelected, int row, int column) {
    button.setText(Lizzie.resourceBundle.getString("PrivateKifuSearch.copy"));
    URL = table.getValueAt(row, 11).toString();
    return panel;
  }

  @Override
  public Object getCellEditorValue() {
    // TODO Auto-generated method stub
    return null;
  }
}

class MyButtonOpen implements TableCellRenderer {
  private JPanel panel;
  private JButton button;

  public MyButtonOpen() {
    initButton();
    initPanel();
    panel.add(button, BorderLayout.CENTER);
  }

  private void initButton() {
    button = new JButton();
    button.setMargin(new Insets(0, 0, 0, 0));
  }

  private void initPanel() {
    panel = new JPanel();
    panel.setLayout(new BorderLayout());
  }

  public Component getTableCellRendererComponent(
      JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
    button.setText(Lizzie.resourceBundle.getString("PublicKifuSearch.view"));
    return panel;
  }
}

class MyButtonOpenEditor extends AbstractCellEditor implements TableCellEditor {
  private JPanel panel;
  private JButton button;
  private String URL = "";

  public MyButtonOpenEditor() {
    initButton();
    initPanel();
    panel.add(this.button, BorderLayout.CENTER);
  }

  private void initButton() {
    button = new JButton();
    button.setMargin(new Insets(0, 0, 0, 0));
    button.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            // Lizzie.frame.bowser(URL, "Lizzie Player", false);
            // http://lizzieyzy.cn/?qp=%2F%E5%80%94%E5%BC%BA%E7%9A%84%E7%B4%AB%E8%97%A4%2F20200603%2F20200603130156

            try {
              String folder = java.net.URLDecoder.decode(URL, "utf-8");
              // http://lizzieyzy.cn/?qp=/倔强的紫藤/20200603/20200603130156
              String[] params = folder.trim().split("/");
              if (params.length > 3) {
                String name = params[(params.length - 3)];
                String date = params[(params.length - 2)];
                String file = params[(params.length - 1)];
                SocketGetFile socketGetFile = new SocketGetFile();
                socketGetFile.SocketGetFile(name, date, file);
              }
            } catch (UnsupportedEncodingException e1) {
              // TODO Auto-generated catch block
              e1.printStackTrace();
            }
          }
        });
  }

  private void initPanel() {
    panel = new JPanel();
    panel.setLayout(new BorderLayout());
  }

  @Override
  public Component getTableCellEditorComponent(
      JTable table, Object value, boolean isSelected, int row, int column) {
    button.setText(Lizzie.resourceBundle.getString("PublicKifuSearch.view"));
    URL = table.getValueAt(row, 11).toString();
    return panel;
  }

  @Override
  public Object getCellEditorValue() {
    // TODO Auto-generated method stub
    return null;
  }
}

class MyButtonOpenHtml implements TableCellRenderer {
  private JPanel panel;
  private JButton button;

  public MyButtonOpenHtml() {
    initButton();
    initPanel();
    panel.add(button, BorderLayout.CENTER);
  }

  private void initButton() {
    button = new JButton();
    button.setMargin(new Insets(0, 0, 0, 0));
  }

  private void initPanel() {
    panel = new JPanel();
    panel.setLayout(new BorderLayout());
  }

  public Component getTableCellRendererComponent(
      JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
    button.setText(Lizzie.resourceBundle.getString("PrivateKifuSearch.open"));
    return panel;
  }
}

class MyButtonOpenHtmlEditor extends AbstractCellEditor implements TableCellEditor {
  private JPanel panel;
  private JButton button;
  private String URL = "";

  public MyButtonOpenHtmlEditor() {
    initButton();
    initPanel();
    panel.add(this.button, BorderLayout.CENTER);
  }

  private void initButton() {
    button = new JButton();
    button.setMargin(new Insets(0, 0, 0, 0));
    button.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {

            Desktop desktop = Desktop.getDesktop();
            try {
              desktop.browse(new URI(URL));
            } catch (IOException e1) {
              // TODO Auto-generated catch block
              e1.printStackTrace();
            } catch (URISyntaxException e1) {
              // TODO Auto-generated catch block
              e1.printStackTrace();
            }
          }
        });
  }

  private void initPanel() {
    panel = new JPanel();
    panel.setLayout(new BorderLayout());
  }

  @Override
  public Component getTableCellEditorComponent(
      JTable table, Object value, boolean isSelected, int row, int column) {
    button.setText(Lizzie.resourceBundle.getString("PrivateKifuSearch.open"));
    URL = table.getValueAt(row, 11).toString();
    return panel;
  }

  @Override
  public Object getCellEditorValue() {
    // TODO Auto-generated method stub
    return null;
  }
}
