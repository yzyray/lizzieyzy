package featurecat.lizzie.gui;

import featurecat.lizzie.Lizzie;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;
import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;

public class Manual extends JDialog {
  boolean isMannul = false;
  private final ResourceBundle resourceBundle =
      Lizzie.config.useLanguage == 0
          ? ResourceBundle.getBundle("l10n.DisplayStrings")
          : (Lizzie.config.useLanguage == 1
              ? ResourceBundle.getBundle("l10n.DisplayStrings", new Locale("zh", "CN"))
              : ResourceBundle.getBundle("l10n.DisplayStrings", new Locale("en", "US")));

  public Manual() {
    //  setType(Type.POPUP);
    setTitle(resourceBundle.getString("Mannul.title")); // ("人工干预");
    setAlwaysOnTop(true);
    // setBounds(0, 0, 194, 168);
    Lizzie.setFrameSize(this, 189, 165);
    getContentPane().setLayout(new BorderLayout());
    JPanel buttonPane = new JPanel();
    getContentPane().add(buttonPane, BorderLayout.CENTER);
    buttonPane.setLayout(null);
    JButton playNow = new JButton(resourceBundle.getString("Mannul.playNow")); // ("立即落子");
    playNow.setBounds(10, 5, 158, 29);

    playNow.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            if (Lizzie.board.getHistory().isBlacksTurn())
              Lizzie.engineManager.engineList.get(
                          Lizzie.engineManager.engineGameInfo.blackEngineIndex)
                      .playNow =
                  true;
            else
              Lizzie.engineManager.engineList.get(
                          Lizzie.engineManager.engineGameInfo.whiteEngineIndex)
                      .playNow =
                  true;
          }
        });

    buttonPane.add(playNow);
    JButton manualOne =
        new JButton(resourceBundle.getString("Mannul.manualOne.enable")); // ("允许人工落子");
    manualOne.setBounds(10, 35, 158, 29);
    manualOne.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            if (!isMannul) {
              Lizzie.frame.addInput(false);
              manualOne.setText(
                  resourceBundle.getString("Mannul.manualOne.disable")); // ("关闭人工落子");
            } else {
              Lizzie.frame.removeInput(false);
              manualOne.setText(resourceBundle.getString("Mannul.manualOne.enable")); // ("允许人工落子");
            }
            isMannul = !isMannul;
          }
        });
    buttonPane.add(manualOne);

    JButton blackResign = new JButton(resourceBundle.getString("Mannul.blackResign")); // ("黑认输");
    blackResign.setBounds(10, 65, 158, 29);
    blackResign.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            Lizzie.engineManager.engineList.get(
                        Lizzie.engineManager.engineGameInfo.blackEngineIndex)
                    .resigned =
                true;
            //            Lizzie.engineManager.engineList.get(
            //                        Lizzie.engineManager.engineGameInfo.blackEngineIndex)
            //                    .isManualB =
            //                true;
            //            if (!Lizzie.engineManager.engineGameInfo.isGenmove) {
            //              Lizzie.engineManager.engineList.get(
            //                          Lizzie.engineManager.engineGameInfo.blackEngineIndex)
            //                      .played =
            //                  true;
            //              Lizzie.engineManager
            //                  .engineList
            //                  .get(Lizzie.engineManager.engineGameInfo.blackEngineIndex)
            //                  .nameCmd();
            //            }
          }
        });
    buttonPane.add(blackResign);

    JButton whiteResign = new JButton(resourceBundle.getString("Mannul.whiteResign")); // ("白认输");
    whiteResign.setBounds(10, 95, 158, 29);
    whiteResign.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            Lizzie.engineManager.engineList.get(
                        Lizzie.engineManager.engineGameInfo.whiteEngineIndex)
                    .resigned =
                true;
            //            Lizzie.engineManager.engineList.get(
            //                        Lizzie.engineManager.engineGameInfo.whiteEngineIndex)
            //                    .isManualW =
            //                true;
            //            if (!Lizzie.engineManager.engineGameInfo.isGenmove) {
            //              Lizzie.engineManager.engineList.get(
            //                          Lizzie.engineManager.engineGameInfo.whiteEngineIndex)
            //                      .played =
            //                  true;
            //              Lizzie.engineManager
            //                  .engineList
            //                  .get(Lizzie.engineManager.engineGameInfo.whiteEngineIndex)
            //                  .nameCmd();
            //            }
          }
        });
    buttonPane.add(whiteResign);

    try {
      this.setIconImage(ImageIO.read(MoreEngines.class.getResourceAsStream("/assets/logo.png")));
    } catch (IOException e) {
      e.printStackTrace();
    }
    if (Lizzie.engineManager.engineGameInfo.isGenmove) {
      manualOne.setEnabled(false);
      playNow.setEnabled(false);
    }
    setLocationRelativeTo(getOwner());
  }
}
