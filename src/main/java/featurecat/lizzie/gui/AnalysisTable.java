package featurecat.lizzie.gui;

import featurecat.lizzie.Lizzie;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FileDialog;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javax.imageio.ImageIO;
import javax.swing.AbstractCellEditor;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import org.json.JSONObject;

public class AnalysisTable {
  private final ResourceBundle resourceBundle = Lizzie.resourceBundle;
  public JFrame frame;
  public JButton stopGo;
  public JButton stopStart;
  private JTable table;
  public int changeRow;

  //    private Object[][] data = {
  //            {1, 2, 3},
  //            {4, 5, 6},
  //            {7, 8, 9}};
  //

  public AnalysisTable() {
    frame = new JFrame();
    frame.setTitle(resourceBundle.getString("AnalysisTable.title"));
    //  frame.setBounds(0, 0, 620, 320);
    Lizzie.setFrameSize(frame, 617, 317);
    frame.setResizable(false);
    try {
      frame.setIconImage(ImageIO.read(getClass().getResourceAsStream("/assets/logo.png")));
    } catch (IOException e) {
      e.printStackTrace();
    }
    frame.setLocationRelativeTo(Lizzie.frame);
    // frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.getContentPane().setLayout(null);
    stopGo = new JButton();
    stopGo.setText(resourceBundle.getString("AnalysisTable.stopGo"));
    //    if (!Lizzie.frame.toolbar.isAutoAna) {
    //      stopGo.setText("继续");
    //    }
    stopGo.setBounds(10, 2, 100, 25);
    frame.getContentPane().add(stopGo);
    stopGo.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            Lizzie.leelaz.togglePonder();
          }
        });

    stopStart = new JButton();
    stopStart.setText(resourceBundle.getString("AnalysisTable.stopStart"));
    stopStart.setBounds(110, 2, 100, 25);
    frame.getContentPane().add(stopStart);
    stopStart.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            if (Lizzie.config.isAutoAna) {
              LizzieFrame.toolbar.stopAutoAna(true, true);
              Lizzie.frame.isBatchAna = false;
            } else {
              verifyCurrentKifu();
              Lizzie.frame.isBatchAna = true;
              StartAnaDialog newgame = new StartAnaDialog(false, Lizzie.frame);
              newgame.setVisible(true);
            }
          }
        });

    JButton stopStartAnalysisMode =
        new JButton(resourceBundle.getString("AnalysisTable.stopStartAnalysisMode"));
    stopStartAnalysisMode.setMargin(new Insets(0, 0, 0, 0));
    stopStartAnalysisMode.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            if (Lizzie.frame.isBatchAnalysisMode && Lizzie.frame.analysisEngine != null) {
              Lizzie.frame.analysisEngine.waitFrame.setVisible(false);
              Lizzie.frame.destroyAnalysisEngine();
              Lizzie.frame.isBatchAnalysisMode = false;
            } else {
              verifyCurrentKifu();
              Lizzie.frame.isBatchAna = true;
              StartAnaDialog newgame = new StartAnaDialog(true, Lizzie.frame);
              newgame.setVisible(true);
            }
          }
        });
    stopStartAnalysisMode.setBounds(210, 2, 130, 25);
    frame.getContentPane().add(stopStartAnalysisMode);
    JButton addFile = new JButton(resourceBundle.getString("AnalysisTable.addFile"));
    addFile.setBounds(340, 2, 100, 25);
    frame.getContentPane().add(addFile);
    addFile.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            //            if (!Lizzie.frame.isBatchAna) {
            //              Lizzie.frame.openFileWithAna();
            //              return;
            //            }
            JSONObject filesystem = Lizzie.config.persisted.getJSONObject("filesystem");
            FileDialog fileDialog =
                new FileDialog(Lizzie.frame, resourceBundle.getString("AnalysisTable.fileDialog"));

            fileDialog.setLocationRelativeTo(Lizzie.frame);
            fileDialog.setDirectory(filesystem.getString("last-folder"));
            fileDialog.setFile("*.sgf;*.gib;*.SGF;*.GIB");

            fileDialog.setMultipleMode(true);
            fileDialog.setMode(0);
            fileDialog.setVisible(true);

            File[] files = fileDialog.getFiles();

            if (files.length > 0) {
              Lizzie.frame.isBatchAna = true;
              for (int i = 0; i < files.length; i++) {
                Lizzie.frame.Batchfiles.add(files[i]);
              }
              Lizzie.frame.analysisTable.refreshTable();
            }
          }
        });

    JButton clearAllFiles = new JButton(resourceBundle.getString("AnalysisTable.clearAllFiles"));
    clearAllFiles.setBounds(440, 2, 100, 25);
    frame.getContentPane().add(clearAllFiles);
    clearAllFiles.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            Lizzie.frame.isBatchAna = false;
            Lizzie.frame.BatchAnaNum = 0;
            Lizzie.frame.Batchfiles = new ArrayList<File>();
            Lizzie.frame.analysisTable.refreshTable();
          }
        });

    JPanel panel = new JPanel();
    panel.setBounds(10, 30, 614, 282);
    frame.getContentPane().add(panel);
    panel.setLayout(null);

    JScrollPane scrollPane = new JScrollPane();
    scrollPane.setBounds(0, 0, 584, 242);
    panel.add(scrollPane);

    table = new JTable();
    scrollPane.setViewportView(table);

    table.setModel(
        new DefaultTableModel() {
          @Override
          public Object getValueAt(int row, int column) {
            switch (column) {
              case 0:
                if (row == 0) return resourceBundle.getString("AnalysisTable.current");
                else return row + 1;
              case 1:
                return Lizzie.frame.Batchfiles.get(row + Lizzie.frame.BatchAnaNum).getName();
            }

            return "";
          }

          @Override
          public int getRowCount() {
            if (Lizzie.frame.Batchfiles != null)
              return Lizzie.frame.Batchfiles.size() - Lizzie.frame.BatchAnaNum;
            else return 0;
          }

          @Override
          public String getColumnName(int column) {
            if (column == 0) return resourceBundle.getString("AnalysisTable.column1");
            if (column == 1) return resourceBundle.getString("AnalysisTable.column2");
            if (column == 2) return resourceBundle.getString("AnalysisTable.prior");
            if (column == 3) return resourceBundle.getString("AnalysisTable.up");
            if (column == 4) return resourceBundle.getString("AnalysisTable.down");
            if (column == 5) return resourceBundle.getString("AnalysisTable.delete");
            return "";
          }

          @Override
          public int getColumnCount() {
            return 6;
          }

          @Override
          public void setValueAt(Object aValue, int row, int column) {
            // data[row][column] = aValue;
            //   fireTableCellUpdated(row, column);
          }

          @Override
          public boolean isCellEditable(int row, int column) {
            if (row == 0) return false;
            if (column == 2 || column == 3 || column == 4 || column == 5) {
              return true;
            } else {
              return false;
            }
          }
        });

    table.getColumnModel().getColumn(2).setCellEditor(new MyButtonFirstEditor());

    table.getColumnModel().getColumn(2).setCellRenderer(new MyButtonFirst());

    table.getColumnModel().getColumn(3).setCellEditor(new MyButtonUpEditor());

    table.getColumnModel().getColumn(3).setCellRenderer(new MyButtonUp());

    table.getColumnModel().getColumn(4).setCellEditor(new MyButtonDownEditor());

    table.getColumnModel().getColumn(4).setCellRenderer(new MyButtonDown());

    table.getColumnModel().getColumn(5).setCellEditor(new MyButtonDeleteEditor());

    table.getColumnModel().getColumn(5).setCellRenderer(new MyButtonDelete());
    table.setRowSelectionAllowed(false);
    table.getColumnModel().getColumn(0).setPreferredWidth(40);
    table.getColumnModel().getColumn(1).setPreferredWidth(300);
    table.getColumnModel().getColumn(2).setPreferredWidth(40);
    table.getColumnModel().getColumn(3).setPreferredWidth(40);
    table.getColumnModel().getColumn(4).setPreferredWidth(40);
    table.getColumnModel().getColumn(5).setPreferredWidth(40);
    resetAnalysisMode();
  }

  protected void verifyCurrentKifu() {
    // TODO Auto-generated method stub
    String firstFileName = Lizzie.frame.Batchfiles.get(Lizzie.frame.BatchAnaNum).getName();
    if (!LizzieFrame.fileNameTitle.equals(firstFileName)) {
      Lizzie.frame.loadFile(Lizzie.frame.Batchfiles.get(Lizzie.frame.BatchAnaNum), false, false);
    }
  }

  public void refreshTable() {
    // table.updateUI();
    table.revalidate();
    table.repaint();
  }

  public void resetAnalysisMode() {
    // TODO Auto-generated method stub
    if (Lizzie.frame.isBatchAnalysisMode) {
      stopGo.setEnabled(false);
      stopStart.setEnabled(false);
    } else {
      stopGo.setEnabled(true);
      stopStart.setEnabled(true);
    }
  }
}

class MyButtonFirst implements TableCellRenderer {
  private JPanel panel;
  private JButton button;

  public MyButtonFirst() {
    initButton();
    initPanel();
    panel.add(button, BorderLayout.CENTER);
  }

  private void initButton() {
    button = new JButton();
    button.setFocusable(false);
    button.setMargin(new Insets(0, 0, 0, 0));
  }

  private void initPanel() {
    panel = new JPanel();
    panel.setLayout(new BorderLayout());
  }

  public Component getTableCellRendererComponent(
      JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
    button.setText(Lizzie.resourceBundle.getString("AnalysisTable.prior"));
    if (row == 0 || row == 1) button.setEnabled(false);
    else button.setEnabled(true);
    return panel;
  }
}

class MyButtonFirstEditor extends AbstractCellEditor implements TableCellEditor {
  private JPanel panel;
  private JButton button;

  public MyButtonFirstEditor() {
    initButton();
    initPanel();
    panel.add(this.button, BorderLayout.CENTER);
  }

  private void initButton() {
    button = new JButton();
    button.setFocusable(false);
    button.setMargin(new Insets(0, 0, 0, 0));
    button.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            if (Lizzie.frame.analysisTable.changeRow + Lizzie.frame.BatchAnaNum == 0) return;
            File file =
                Lizzie.frame.Batchfiles.get(
                    Lizzie.frame.analysisTable.changeRow + Lizzie.frame.BatchAnaNum);
            Lizzie.frame.Batchfiles.remove(
                Lizzie.frame.analysisTable.changeRow + Lizzie.frame.BatchAnaNum);
            Lizzie.frame.Batchfiles.add(Lizzie.frame.BatchAnaNum + 1, file);
            Lizzie.frame.analysisTable.refreshTable();
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
    button.setText(Lizzie.resourceBundle.getString("AnalysisTable.prior"));
    Lizzie.frame.analysisTable.changeRow = row;
    if (row == 0 || row == 1) button.setEnabled(false);
    else button.setEnabled(true);
    return panel;
  }

  @Override
  public Object getCellEditorValue() {
    // TODO Auto-generated method stub
    return null;
  }
}

class MyButtonUp implements TableCellRenderer {
  private JPanel panel;
  private JButton button;

  public MyButtonUp() {
    initButton();
    initPanel();
    panel.add(button, BorderLayout.CENTER);
  }

  private void initButton() {
    button = new JButton();
    button.setFocusable(false);
    button.setMargin(new Insets(0, 0, 0, 0));
  }

  private void initPanel() {
    panel = new JPanel();
    panel.setLayout(new BorderLayout());
  }

  public Component getTableCellRendererComponent(
      JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
    button.setText(Lizzie.resourceBundle.getString("AnalysisTable.up"));
    if (row == 0 || row == 1) button.setEnabled(false);
    else button.setEnabled(true);
    return panel;
  }
}

class MyButtonUpEditor extends AbstractCellEditor implements TableCellEditor {
  private JPanel panel;
  private JButton button;

  public MyButtonUpEditor() {
    initButton();
    initPanel();
    panel.add(this.button, BorderLayout.CENTER);
  }

  private void initButton() {
    button = new JButton();
    button.setFocusable(false);
    button.setMargin(new Insets(0, 0, 0, 0));
    button.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            if (Lizzie.frame.analysisTable.changeRow <= 1) return;
            File file =
                Lizzie.frame.Batchfiles.get(
                    Lizzie.frame.analysisTable.changeRow + Lizzie.frame.BatchAnaNum);
            Lizzie.frame.Batchfiles.remove(
                Lizzie.frame.analysisTable.changeRow + Lizzie.frame.BatchAnaNum);
            Lizzie.frame.Batchfiles.add(
                Lizzie.frame.analysisTable.changeRow + Lizzie.frame.BatchAnaNum - 1, file);
            Lizzie.frame.analysisTable.refreshTable();
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
    button.setText(Lizzie.resourceBundle.getString("AnalysisTable.up"));
    Lizzie.frame.analysisTable.changeRow = row;
    if (row == 0 || row == 1) button.setEnabled(false);
    else button.setEnabled(true);
    return panel;
  }

  @Override
  public Object getCellEditorValue() {
    // TODO Auto-generated method stub
    return null;
  }
}

class MyButtonDown implements TableCellRenderer {
  private JPanel panel;
  private JButton button;

  public MyButtonDown() {
    initButton();
    initPanel();
    panel.add(button, BorderLayout.CENTER);
  }

  private void initButton() {
    button = new JButton();
    button.setFocusable(false);
  }

  private void initPanel() {
    panel = new JPanel();
    button.setMargin(new Insets(0, 0, 0, 0));
    panel.setLayout(new BorderLayout());
  }

  public Component getTableCellRendererComponent(
      JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
    button.setText(Lizzie.resourceBundle.getString("AnalysisTable.down"));
    if (row == 0) button.setEnabled(false);
    else button.setEnabled(true);
    return panel;
  }
}

class MyButtonDownEditor extends AbstractCellEditor implements TableCellEditor {
  private JPanel panel;
  private JButton button;

  public MyButtonDownEditor() {
    initButton();
    initPanel();
    panel.add(this.button, BorderLayout.CENTER);
  }

  private void initButton() {
    button = new JButton();
    button.setFocusable(false);
    button.setMargin(new Insets(0, 0, 0, 0));
    button.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            if (Lizzie.frame.analysisTable.changeRow + Lizzie.frame.BatchAnaNum == 0) return;
            if ((Lizzie.frame.analysisTable.changeRow + Lizzie.frame.BatchAnaNum)
                >= (Lizzie.frame.Batchfiles.size() - 1)) return;
            File file =
                Lizzie.frame.Batchfiles.get(
                    Lizzie.frame.analysisTable.changeRow + Lizzie.frame.BatchAnaNum);
            Lizzie.frame.Batchfiles.remove(
                Lizzie.frame.analysisTable.changeRow + Lizzie.frame.BatchAnaNum);
            Lizzie.frame.Batchfiles.add(
                Lizzie.frame.analysisTable.changeRow + Lizzie.frame.BatchAnaNum + 1, file);
            Lizzie.frame.analysisTable.refreshTable();
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
    button.setText(Lizzie.resourceBundle.getString("AnalysisTable.down"));
    Lizzie.frame.analysisTable.changeRow = row;
    if (row == 0) button.setEnabled(false);
    else button.setEnabled(true);
    return panel;
  }

  @Override
  public Object getCellEditorValue() {
    // TODO Auto-generated method stub
    return null;
  }
}

class MyButtonDelete implements TableCellRenderer {
  private JPanel panel;
  private JButton button;

  public MyButtonDelete() {
    initButton();
    initPanel();
    panel.add(button, BorderLayout.CENTER);
  }

  private void initButton() {
    button = new JButton();
    button.setFocusable(false);
    button.setMargin(new Insets(0, 0, 0, 0));
  }

  private void initPanel() {
    panel = new JPanel();
    panel.setLayout(new BorderLayout());
  }

  public Component getTableCellRendererComponent(
      JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
    button.setText(Lizzie.resourceBundle.getString("AnalysisTable.delete"));
    if (row == 0) button.setEnabled(false);
    else button.setEnabled(true);
    return panel;
  }
}

class MyButtonDeleteEditor extends AbstractCellEditor implements TableCellEditor {
  private JPanel panel;
  private JButton button;

  public MyButtonDeleteEditor() {
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
            if (Lizzie.frame.analysisTable.changeRow + Lizzie.frame.BatchAnaNum == 0) return;
            Lizzie.frame.Batchfiles.remove(
                Lizzie.frame.analysisTable.changeRow + Lizzie.frame.BatchAnaNum);
            Lizzie.frame.analysisTable.refreshTable();
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
    button.setText(Lizzie.resourceBundle.getString("AnalysisTable.delete"));
    Lizzie.frame.analysisTable.changeRow = row;
    if (row == 0) button.setEnabled(false);
    else button.setEnabled(true);
    return panel;
  }

  @Override
  public Object getCellEditorValue() {
    // TODO Auto-generated method stub
    return null;
  }
}
