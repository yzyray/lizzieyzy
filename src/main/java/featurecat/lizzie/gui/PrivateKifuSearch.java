package featurecat.lizzie.gui;

import featurecat.lizzie.Lizzie;
import featurecat.lizzie.util.Utils;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
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

public class PrivateKifuSearch extends JFrame {
  private final ResourceBundle resourceBundle =
      Lizzie.config.useLanguage == 0
          ? ResourceBundle.getBundle("l10n.DisplayStrings")
          : (Lizzie.config.useLanguage == 1
              ? ResourceBundle.getBundle("l10n.DisplayStrings", new Locale("zh", "CN"))
              : ResourceBundle.getBundle("l10n.DisplayStrings", new Locale("en", "US")));
  //  private static Connection con = null;
  //  private static Statement sql = null;
  //  private static ResultSet rs = null;
  private DefaultTableModel model;
  private JTable table;
  private final JPanel panel = new JPanel();
  private final JPanel panel_1 = new JPanel();
  private JTextField txtUploader;
  private JTextField txtLabel;
  private JTextField txtWhite;
  private JTextField txtBlack;
  JLabel lbl_tab;
  private JTextField txtOtherInfo;
  int tabNumber = 1;
  int numbersPerTab = 25;
  int curTabNumber = 1;
  String[] row;
  ArrayList<String[]> rows;
  private JTextField txtBScore;
  private JTextField txtWScore;
  private JTextField txtAllMove;
  private JTextField txtAnalyzedMove;
  JXDatePicker datepick;
  JXDatePicker datepick2;
  JButton button;

  public PrivateKifuSearch() {
    //		 this.setModal(true);
    // setType(Type.POPUP);
    setTitle(resourceBundle.getString("PrivateKifuSearch.title")); // ("修改共享棋谱信息");
    setAlwaysOnTop(Lizzie.frame.isAlwaysOnTop());
    //		    setResizable(false);
    Lizzie.setFrameSize(this, 1225, 569);
    // setSize(1125, 569);
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

    table = new JTable();
    model = new DefaultTableModel();
    getContentPane().setLayout(new BorderLayout(0, 0));
    getContentPane().add(panel, BorderLayout.NORTH);
    String DefaultFormat = "yyyy-MM-dd";
    Date date = new Date();
    date = getDateBefore(date, 60);
    Date date2 = new Date();
    GridBagLayout gbl_panel = new GridBagLayout();
    gbl_panel.columnWidths = new int[] {56, 82, 56, 82, 56, 82, 56, 82, 56, 82, 56, 82, 0};
    gbl_panel.rowHeights = new int[] {23, 23, 0};
    gbl_panel.columnWeights =
        new double[] {0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
    gbl_panel.rowWeights = new double[] {0.0, 0.0, Double.MIN_VALUE};
    panel.setLayout(gbl_panel);
    JLabel label_5 =
        new JLabel(resourceBundle.getString("PrivateKifuSearch.labelBlack")); // ("黑方:");
    GridBagConstraints gbc_label_5 = new GridBagConstraints();
    gbc_label_5.fill = GridBagConstraints.VERTICAL;
    gbc_label_5.insets = new Insets(0, 0, 5, 5);
    gbc_label_5.gridx = 0;
    gbc_label_5.gridy = 0;
    panel.add(label_5, gbc_label_5);

    txtBlack = new JTextField();
    GridBagConstraints gbc_txtBlack = new GridBagConstraints();
    gbc_txtBlack.fill = GridBagConstraints.BOTH;
    gbc_txtBlack.insets = new Insets(0, 0, 5, 5);
    gbc_txtBlack.gridx = 1;
    gbc_txtBlack.gridy = 0;
    panel.add(txtBlack, gbc_txtBlack);
    txtBlack.setColumns(4);

    JLabel label_7 =
        new JLabel(resourceBundle.getString("PrivateKifuSearch.labelScore")); // ("评分(大于):");
    GridBagConstraints gbc_label_7 = new GridBagConstraints();
    gbc_label_7.fill = GridBagConstraints.VERTICAL;
    gbc_label_7.insets = new Insets(0, 0, 5, 5);
    gbc_label_7.gridx = 2;
    gbc_label_7.gridy = 0;
    panel.add(label_7, gbc_label_7);

    txtBScore = new JTextField();
    GridBagConstraints gbc_txtBScore = new GridBagConstraints();
    gbc_txtBScore.fill = GridBagConstraints.BOTH;
    gbc_txtBScore.insets = new Insets(0, 0, 5, 5);
    gbc_txtBScore.gridx = 3;
    gbc_txtBScore.gridy = 0;
    panel.add(txtBScore, gbc_txtBScore);
    txtBScore.setColumns(3);

    JLabel label_4 =
        new JLabel(resourceBundle.getString("PrivateKifuSearch.labelWhite")); // ("白方:");
    GridBagConstraints gbc_label_4 = new GridBagConstraints();
    gbc_label_4.fill = GridBagConstraints.VERTICAL;
    gbc_label_4.insets = new Insets(0, 0, 5, 5);
    gbc_label_4.gridx = 4;
    gbc_label_4.gridy = 0;
    panel.add(label_4, gbc_label_4);

    txtWhite = new JTextField();
    GridBagConstraints gbc_txtWhite = new GridBagConstraints();
    gbc_txtWhite.fill = GridBagConstraints.BOTH;
    gbc_txtWhite.insets = new Insets(0, 0, 5, 5);
    gbc_txtWhite.gridx = 5;
    gbc_txtWhite.gridy = 0;
    panel.add(txtWhite, gbc_txtWhite);
    txtWhite.setColumns(4);

    JLabel label_8 =
        new JLabel(resourceBundle.getString("PrivateKifuSearch.labelScore")); // ("评分(大于):");
    GridBagConstraints gbc_label_8 = new GridBagConstraints();
    gbc_label_8.fill = GridBagConstraints.VERTICAL;
    gbc_label_8.insets = new Insets(0, 0, 5, 5);
    gbc_label_8.gridx = 6;
    gbc_label_8.gridy = 0;
    panel.add(label_8, gbc_label_8);

    txtWScore = new JTextField();
    GridBagConstraints gbc_txtWScore = new GridBagConstraints();
    gbc_txtWScore.fill = GridBagConstraints.BOTH;
    gbc_txtWScore.insets = new Insets(0, 0, 5, 5);
    gbc_txtWScore.gridx = 7;
    gbc_txtWScore.gridy = 0;
    panel.add(txtWScore, gbc_txtWScore);
    txtWScore.setColumns(3);

    JLabel label_10 =
        new JLabel(resourceBundle.getString("PrivateKifuSearch.labelMove")); // ("手数(大于):");
    GridBagConstraints gbc_label_10 = new GridBagConstraints();
    gbc_label_10.fill = GridBagConstraints.VERTICAL;
    gbc_label_10.insets = new Insets(0, 0, 5, 5);
    gbc_label_10.gridx = 8;
    gbc_label_10.gridy = 0;
    panel.add(label_10, gbc_label_10);

    txtAllMove = new JTextField();
    GridBagConstraints gbc_txtAllMove = new GridBagConstraints();
    gbc_txtAllMove.fill = GridBagConstraints.BOTH;
    gbc_txtAllMove.insets = new Insets(0, 0, 5, 5);
    gbc_txtAllMove.gridx = 9;
    gbc_txtAllMove.gridy = 0;
    panel.add(txtAllMove, gbc_txtAllMove);
    txtAllMove.setColumns(3);

    JLabel label_9 =
        new JLabel(resourceBundle.getString("PrivateKifuSearch.labelAnalyzed")); // ("已分析(大于):");
    GridBagConstraints gbc_label_9 = new GridBagConstraints();
    gbc_label_9.fill = GridBagConstraints.VERTICAL;
    gbc_label_9.insets = new Insets(0, 0, 5, 5);
    gbc_label_9.gridx = 10;
    gbc_label_9.gridy = 0;
    panel.add(label_9, gbc_label_9);

    txtAnalyzedMove = new JTextField();
    GridBagConstraints gbc_txtAnalyzedMove = new GridBagConstraints();
    gbc_txtAnalyzedMove.fill = GridBagConstraints.BOTH;
    gbc_txtAnalyzedMove.insets = new Insets(0, 0, 5, 0);
    gbc_txtAnalyzedMove.gridx = 11;
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
    txtUploader.setText(Lizzie.config.uploadUser);
    txtUploader.setEditable(false);

    JLabel label_3 =
        new JLabel(resourceBundle.getString("PrivateKifuSearch.labelLabel")); // ("标签:");
    GridBagConstraints gbc_label_3 = new GridBagConstraints();
    gbc_label_3.fill = GridBagConstraints.VERTICAL;
    gbc_label_3.insets = new Insets(0, 0, 0, 5);
    gbc_label_3.gridx = 2;
    gbc_label_3.gridy = 1;
    panel.add(label_3, gbc_label_3);

    button = new JButton(resourceBundle.getString("PrivateKifuSearch.btnSearch")); // ("查询");
    button.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {

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
                "select ID,black "
                    + resourceBundle.getString("PrivateKifuSearch.sql.black")
                    + ",white "
                    + resourceBundle.getString("PrivateKifuSearch.sql.white")
                    + ",case when bscore>=0"
                    + " and bscore<=100 then bscore when bscore>100 then '"
                    + resourceBundle.getString("PrivateKifuSearch.sql.AIGame")
                    + "' when bsc"
                    + "ore =-1then '"
                    + resourceBundle.getString("PrivateKifuSearch.sql.lackInfo")
                    + "' ELSE  '' end as "
                    + resourceBundle.getString("PrivateKifuSearch.sql.blackScore")
                    + ",case when wscore"
                    + ">=0 and wscore<=100 then wscore when wscore>100 then '"
                    + resourceBundle.getString("PrivateKifuSearch.sql.AIGame")
                    + "' when w"
                    + "score =-1then '"
                    + resourceBundle.getString("PrivateKifuSearch.sql.lackInfo")
                    + "' ELSE  '' end as "
                    + resourceBundle.getString("PrivateKifuSearch.sql.whiteScore")
                    + ",case when analyzedmove>0"
                    + " then analyzedmove else '' end as "
                    + resourceBundle.getString("PrivateKifuSearch.sql.analyzed")
                    + ",case when allmove>0 then allm"
                    + "ove else '' end as  "
                    + resourceBundle.getString("PrivateKifuSearch.sql.move")
                    + ",uploader "
                    + resourceBundle.getString("PrivateKifuSearch.sql.uploader")
                    + ",label "
                    + resourceBundle.getString("PrivateKifuSearch.sql.label")
                    + ",otherinfo "
                    + resourceBundle.getString("PrivateKifuSearch.sql.otherInfo")
                    + ","
                    + "case  isdelete when '2' then '"
                    + resourceBundle.getString("PrivateKifuSearch.sql.private")
                    + "' when '0' then '"
                    + resourceBundle.getString("PrivateKifuSearch.sql.public")
                    + "' end as "
                    + resourceBundle.getString("PrivateKifuSearch.sql.isPublic")
                    + ",crea"
                    + "tetime "
                    + resourceBundle.getString("PrivateKifuSearch.sql.uploadTime")
                    + ",filename as "
                    + resourceBundle.getString("PrivateKifuSearch.sql.fileName")
                    + ",url "
                    + resourceBundle.getString("PrivateKifuSearch.sql.url")
                    + ",''as  "
                    + resourceBundle.getString("PrivateKifuSearch.sql.edit")
                    + ",'' as"
                    + " "
                    + resourceBundle.getString("PrivateKifuSearch.sql.copy")
                    + ",'' as "
                    + resourceBundle.getString("PrivateKifuSearch.sql.view")
                    + ",'' as "
                    + resourceBundle.getString("PrivateKifuSearch.sql.open")
                    + " from public_search where  createtime between to_date('"
                    + sdf.format(datepick.getDate())
                    + "','yyyy-mm-dd') and to_date('"
                    + sdf.format(getDateBefore(datepick2.getDate(), -1))
                    + "','yyyy-mm-dd')"
                    + "and isdelete in (0,2)  and black like '%"
                    + txtBlack.getText()
                    + "%' and white like '%"
                    + txtWhite.getText()
                    + "%' and uploader = '"
                    + txtUploader.getText()
                    + "' and otherinfo like '%"
                    + txtOtherInfo.getText()
                    + "%' and label like'%"
                    + txtLabel.getText()
                    + "%' "
                    + searchScoreAndMove
                    + " order by createtime desc";
            SocketKifuSearch socketKifuSearch = new SocketKifuSearch();
            List<String> sqlResult = socketKifuSearch.SocketKifuSearch(sqlText);
            // 获得列数
            if (sqlResult != null) {
              String[] params = sqlResult.get(0).split(">->");
              if (model.getColumnCount() == 0) {
                for (int i = 0; i < params.length; i++) {
                  model.addColumn(params[i]);
                }

                table.getColumnModel().getColumn(0).setPreferredWidth(30);
                table.getColumnModel().getColumn(1).setPreferredWidth(70);
                table.getColumnModel().getColumn(2).setPreferredWidth(70);
                table.getColumnModel().getColumn(3).setPreferredWidth(50);
                table.getColumnModel().getColumn(4).setPreferredWidth(50);
                table.getColumnModel().getColumn(5).setPreferredWidth(40);
                table.getColumnModel().getColumn(6).setPreferredWidth(40);
                table.getColumnModel().getColumn(7).setPreferredWidth(40);
                table.getColumnModel().getColumn(8).setPreferredWidth(80);
                table.getColumnModel().getColumn(9).setPreferredWidth(160);
                table.getColumnModel().getColumn(10).setPreferredWidth(60);
                table.getColumnModel().getColumn(11).setPreferredWidth(70);
                table.getColumnModel().getColumn(12).setPreferredWidth(60);
                table.getColumnModel().getColumn(13).setPreferredWidth(50);
                table.getColumnModel().getColumn(14).setPreferredWidth(50);
                table.getColumnModel().getColumn(15).setPreferredWidth(50);
                table.getColumnModel().getColumn(16).setPreferredWidth(50);

                table.getColumnModel().getColumn(14).setCellEditor(new MyButtonEditEditor());
                table.getColumnModel().getColumn(14).setCellRenderer(new MyButtonEdit());
                table.getColumnModel().getColumn(15).setCellEditor(new MyButtonCopyPriEditor());
                table.getColumnModel().getColumn(15).setCellRenderer(new MyButtonCopyPri());
                table.getColumnModel().getColumn(16).setCellEditor(new MyButtonOpenPriEditor());
                table.getColumnModel().getColumn(16).setCellRenderer(new MyButtonOpenPri());
                table.getColumnModel().getColumn(17).setCellEditor(new MyButtonOpenHtmlPriEditor());
                table.getColumnModel().getColumn(17).setCellRenderer(new MyButtonOpenHtmlPri());
              }
              rows = new ArrayList<String[]>();
              for (int i = 1; i < sqlResult.size(); i++) {
                String[] rowParams = sqlResult.get(i).split(">->");
                row = new String[rowParams.length];
                // 将查询到的每行数据赋入数组内
                for (int n = 0; n < rowParams.length; n++) row[n] = rowParams[n];
                // 增加一行

                rows.add(row);
              }

              // model.addRow(row);
              while (model.getRowCount() > 0) {
                model.removeRow(model.getRowCount() - 1);
              }
              tabNumber = rows.size() / numbersPerTab + 1;
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
    table.setModel(model);
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

  public void searchToday() {
    Date date = new Date();
    datepick.setDate(date);
    datepick2.setDate(date);
    button.doClick();
  }

  public static Date getDateBefore(Date d, int day) {
    Calendar now = Calendar.getInstance();
    now.setTime(d);
    now.set(Calendar.DATE, now.get(Calendar.DATE) - day);
    return now.getTime();
  }
}

class MyButtonCopyPri implements TableCellRenderer {
  private JPanel panel;
  private JButton button;

  public MyButtonCopyPri() {
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
    button.setText(Lizzie.resourceBundle.getString("PrivateKifuSearch.copy")); // ("复制");
    return panel;
  }
}

class MyButtonCopyPriEditor extends AbstractCellEditor implements TableCellEditor {
  private JPanel panel;
  private JButton button;
  private String URL = "";

  public MyButtonCopyPriEditor() {
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
            //          Message msg = new Message();
            //            msg.setMessage("复制成功!");
            //            msg.setVisible(true);
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
    button.setText(Lizzie.resourceBundle.getString("PrivateKifuSearch.copy")); // ("复制");
    URL = table.getValueAt(row, 13).toString();
    return panel;
  }

  @Override
  public Object getCellEditorValue() {
    // TODO Auto-generated method stub
    return null;
  }
}

class MyButtonEdit implements TableCellRenderer {
  private JPanel panel;
  private JButton button;

  public MyButtonEdit() {
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
    button.setText(Lizzie.resourceBundle.getString("PrivateKifuSearch.edit")); // ("修改");
    return panel;
  }
}

class MyButtonEditEditor extends AbstractCellEditor implements TableCellEditor {
  private JPanel panel;
  private JButton button;
  private String ID = "";
  private String black = "";
  private String white = "";
  private String label = "";
  private String otherInfo = "";
  private String fileName = "";
  private boolean isPublic = true;

  public MyButtonEditEditor() {
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
            EditShareFrame editShareFrame =
                new EditShareFrame(ID, black, white, label, otherInfo, fileName, isPublic);
            editShareFrame.setVisible(true);
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
    button.setText(Lizzie.resourceBundle.getString("PrivateKifuSearch.edit")); // ("修改");
    ID = table.getValueAt(row, 0).toString();
    black = table.getValueAt(row, 1).toString();
    white = table.getValueAt(row, 2).toString();
    label = table.getValueAt(row, 8).toString();
    otherInfo = table.getValueAt(row, 9).toString();
    fileName = table.getValueAt(row, 12).toString();
    if (table
        .getValueAt(row, 10)
        .toString()
        .equals(Lizzie.resourceBundle.getString("PrivateKifuSearch.sql.private"))) isPublic = false;
    else isPublic = true;
    return panel;
  }

  @Override
  public Object getCellEditorValue() {
    // TODO Auto-generated method stub
    return null;
  }
}

class MyButtonOpenHtmlPri implements TableCellRenderer {
  private JPanel panel;
  private JButton button;

  public MyButtonOpenHtmlPri() {
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
    button.setText(Lizzie.resourceBundle.getString("PrivateKifuSearch.open")); // ("打开");
    return panel;
  }
}

class MyButtonOpenHtmlPriEditor extends AbstractCellEditor implements TableCellEditor {
  private JPanel panel;
  private JButton button;
  private String URL = "";

  public MyButtonOpenHtmlPriEditor() {
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
    button.setText(Lizzie.resourceBundle.getString("PrivateKifuSearch.open")); // ("打开");
    URL = table.getValueAt(row, 13).toString();
    return panel;
  }

  @Override
  public Object getCellEditorValue() {
    // TODO Auto-generated method stub
    return null;
  }
}

class MyButtonOpenPri implements TableCellRenderer {
  private JPanel panel;
  private JButton button;

  public MyButtonOpenPri() {
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

class MyButtonOpenPriEditor extends AbstractCellEditor implements TableCellEditor {
  private JPanel panel;
  private JButton button;
  private String URL = "";

  public MyButtonOpenPriEditor() {
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
    URL = table.getValueAt(row, 13).toString();
    return panel;
  }

  @Override
  public Object getCellEditorValue() {
    // TODO Auto-generated method stub
    return null;
  }
}
