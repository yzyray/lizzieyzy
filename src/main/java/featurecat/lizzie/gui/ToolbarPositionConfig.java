package featurecat.lizzie.gui;

import featurecat.lizzie.Lizzie;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;

public class ToolbarPositionConfig extends JDialog {

  public ToolbarPositionConfig() {
    // setType(Type.POPUP);
    setTitle("显示顺序设置");
    this.setModal(true);
    // setBounds(0, 0, 150, 140);
    Lizzie.setFrameSize(this, 160, 137);
    setResizable(false);
    setAlwaysOnTop(Lizzie.frame.isAlwaysOnTop());
    getContentPane().setLayout(null);
    setLocationRelativeTo(getOwner());
    JComboBox anaPanel = new JComboBox();
    JComboBox autoPlayPanel = new JComboBox();
    JComboBox enginePkPanel = new JComboBox();
    anaPanel.addItem("1");
    anaPanel.addItem("2");
    anaPanel.addItem("3");
    autoPlayPanel.addItem("1");
    autoPlayPanel.addItem("2");
    autoPlayPanel.addItem("3");
    enginePkPanel.addItem("1");
    enginePkPanel.addItem("2");
    enginePkPanel.addItem("3");
    JLabel lblanaPanel = new JLabel("自动分析面板:");
    JLabel lblautoPlayPanel = new JLabel("自动落子面板:");
    JLabel lblenginePkPanel = new JLabel("引擎对战面板:");
    getContentPane().add(lblanaPanel);
    getContentPane().add(lblautoPlayPanel);
    getContentPane().add(lblenginePkPanel);
    getContentPane().add(anaPanel);
    getContentPane().add(autoPlayPanel);
    getContentPane().add(enginePkPanel);
    lblanaPanel.setBounds(10, 3, 110, 25);
    anaPanel.setBounds(100, 7, 40, 18);

    lblenginePkPanel.setBounds(10, 23, 110, 25);
    enginePkPanel.setBounds(100, 27, 40, 18);

    lblautoPlayPanel.setBounds(10, 43, 110, 25);
    autoPlayPanel.setBounds(100, 47, 40, 18);
    JButton okButton = new JButton("确认");
    JButton cancelButton = new JButton("取消");
    okButton.setMargin(new Insets(0, 0, 0, 0));
    cancelButton.setMargin(new Insets(0, 0, 0, 0));
    okButton.setBounds(10, 73, 60, 25);
    cancelButton.setBounds(85, 73, 60, 25);
    getContentPane().add(okButton);
    getContentPane().add(cancelButton);
    anaPanel.setSelectedIndex(LizzieFrame.toolbar.anaPanelOrder);
    enginePkPanel.setSelectedIndex(LizzieFrame.toolbar.enginePkOrder);
    autoPlayPanel.setSelectedIndex(LizzieFrame.toolbar.autoPlayOrder);
    okButton.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            LizzieFrame.toolbar.anaPanelOrder = anaPanel.getSelectedIndex();
            LizzieFrame.toolbar.enginePkOrder = enginePkPanel.getSelectedIndex();
            LizzieFrame.toolbar.autoPlayOrder = autoPlayPanel.getSelectedIndex();
            LizzieFrame.toolbar.setOrder();
            setVisible(false);
          }
        });
    cancelButton.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            setVisible(false);
          }
        });
  }
}
