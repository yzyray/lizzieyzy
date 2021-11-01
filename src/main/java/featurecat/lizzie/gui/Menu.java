package featurecat.lizzie.gui;

import featurecat.lizzie.Config;
import featurecat.lizzie.Lizzie;
import featurecat.lizzie.analysis.EngineManager;
import featurecat.lizzie.analysis.FastLink;
import featurecat.lizzie.analysis.GameInfo;
import featurecat.lizzie.util.Utils;
import java.awt.Color;
import java.awt.Component;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.JToolBar.Separator;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.text.Document;
import org.jdesktop.swingx.util.OS;
import org.json.JSONArray;

public class Menu extends JMenuBar {
  final ButtonGroup buttonGroup = new ButtonGroup();
  private final ResourceBundle resourceBundle =
      Lizzie.config.useLanguage == 0
          ? ResourceBundle.getBundle("l10n.DisplayStrings")
          : (Lizzie.config.useLanguage == 1
              ? ResourceBundle.getBundle("l10n.DisplayStrings", new Locale("zh", "CN"))
              : ResourceBundle.getBundle("l10n.DisplayStrings", new Locale("en", "US")));
  public static ImageIcon icon;
  public static ImageIcon icon2;
  public static ImageIcon stop;
  public static ImageIcon ready;
  public static ImageIcon ready2;
  public static JFontMenuItem[] engine = new JFontMenuItem[21];
  public static JFontMenu engineMenu;
  public static JFontMenu shutdownEngine;
  public static JFontMenu quickLinks;
  JFontMenuItem shutdownAllEngine;
  JFontMenuItem shutdownCurrentEngine;
  JFontMenuItem restartCurrentEngine;
  JFontMenuItem shutdownOtherEngine;
  JFontMenuItem minPlayoutsForNextMove;

  public static JFontMenuItem[] engine2 = new JFontMenuItem[21];
  public static JFontMenu engineMenu2;
  JFontMenuItem shutdownCurrentEngine2;
  JFontMenuItem restartCurrentEngine2;

  ImageIcon iconUp;
  ImageIcon iconDown;
  public JFontLabel byoyomiTime;
  JFontButton pondering;
  JFontButton black;
  JFontButton btnDoubleMenu;
  Separator combinedSeparatorGameControl = new Separator();
  Separator combinedSeparatorPlaceStone = new Separator();
  Separator combinedSeparatorForce = new Separator();
  JFontButton white;
  JFontButton blackwhite;
  JFontButton playPass;
  ImageIcon iconblack;
  ImageIcon iconblack2;
  ImageIcon iconwhite;
  ImageIcon iconwhite2;
  ImageIcon iconbh;
  ImageIcon iconbh2;

  ImageIcon rankMarkOn;
  ImageIcon rankMarkOff;
  JFontButton btnRankMark;

  JFontButton selectAllow;
  JFontButton selectAvoid;
  JButton selectAllowMore;
  JButton selectAvoidMore;
  JFontButton clearSelect;
  Separator forceSep;

  JFontCheckBox chkShowBlack;
  JFontCheckBox chkShowWhite;
  JFontCheckBox chkAnalyzeBlack;
  JFontCheckBox chkAnalyzeWhite;
  JFontCheckBox chkShowPlayouts;
  JFontCheckBox chkShowWinrate;
  JFontCheckBox chkShowScore;
  JToolBar toolPanel;

  // private int doubleMenuOriWidth;
  private JFontButton doubleMenuNewGame;
  private JFontButton doubleMenuPauseGame;
  private JFontButton doubleMenuResign;
  private JFontButton doubleMenuStopGame;

  ImageIcon iconAllow;
  ImageIcon iconAvoid;
  ImageIcon iconAllow2;
  ImageIcon iconAvoid2;
  ImageIcon iconClear;
  ImageIcon iconplayPass;

  public JPanel komiPanel;
  private JToolBar.Separator sepForPdaWrn;
  JPanel komiContentPanel;
  JFontLabel lblKomiSpinner;
  public JTextField txtKomi;
  JFontLabel lblPDASpinner;
  boolean showPDA = false;
  int startPos;
  JFontButton btnKomiUp;
  JFontButton btnKomiDown;
  public JFontTextField txtPDA;
  JFontLabel lblCustomPda;
  JPanel customPDAMorePanel;
  SetKomi setkomi;
  public SetKataPDA setPda;
  JFontButton more2;
  JFontButton setRules;
  JFontButton setLzSaiParam;
  JFontButton setBoardSize;
  JFontButton saveLoad;
  JFontLabel lblWRN;
  JFontLabel lblWRNForDouble;
  public JFontTextField txtWRN;
  JFontCheckBox chkWRN;
  JFontLabel lblGfPDAForDouble;
  JFontCheckBox chkPDA;
  JFontLabel lblGfPDA;
  public JFontTextField txtGfPDA;

  private JFontTextField txtPlayOutsLimit;
  private JFontCheckBox chkPlayOut;
  private JFontTextField txtTimeLimit;
  private JFontCheckBox chkTime;
  private JFontMenu openRecent;
  private boolean ShouldIgnoreDtChange;

  public Menu() {
    setPreferredSize(new Dimension(100, Config.menuHeight)); // 中25 大30
    // headFont = new Font(Config.sysDefaultFontName, Font.PLAIN,
    // Math.max(Lizzie.config.allFontSize, 12)); // 中16 大20
    final JFontMenu fileMenu = new JFontMenu(resourceBundle.getString("Menu.fileMenu")); // ("文件");
    fileMenu.setForeground(Color.BLACK);
    // fileMenu.setFont(headFont);
    this.add(fileMenu);
    final JFontMenuItem open =
        new JFontMenuItem(resourceBundle.getString("Menu.open")); // ("打开棋谱(O)");
    open.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            Lizzie.frame.openFile();
          }
        });
    fileMenu.add(open);

    openRecent = new JFontMenu(resourceBundle.getString("Menu.openRecent"));
    fileMenu.add(openRecent);

    updateRecentFileMenu();

    final JFontMenuItem openUrl =
        new JFontMenuItem(resourceBundle.getString("Menu.openUrl")); // ("打开在线链接(Q)");
    openUrl.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            Lizzie.frame.openOnlineDialog();
          }
        });

    fileMenu.add(openUrl);
    fileMenu.addSeparator();

    final JFontMenuItem save =
        new JFontMenuItem(resourceBundle.getString("Menu.save")); // ("保存棋谱(S)");();
    save.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            Lizzie.frame.saveOriFile();
          }
        });
    fileMenu.add(save);

    final JFontMenuItem saveAs =
        new JFontMenuItem(resourceBundle.getString("Menu.saveAs")); // ("保存棋谱(S)");();
    saveAs.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            LizzieFrame.saveFile(false);
          }
        });
    fileMenu.add(saveAs);

    final JFontMenu saveMore =
        new JFontMenu(resourceBundle.getString("Menu.saveMore")); // (); //saveMore.setText("更多保存");
    fileMenu.add(saveMore);

    final JFontMenuItem saveRaw =
        new JFontMenuItem(
            resourceBundle.getString(
                "Menu.saveRaw")); // ();    saveRaw.setText("保存纯净棋谱(Crtl+Shift+S)");
    saveRaw.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            LizzieFrame.saveFile(true);
          }
        });
    saveMore.add(saveRaw);

    final JFontMenuItem saveCommentRaw =
        new JFontMenuItem(resourceBundle.getString("Menu.saveCommentRaw")); // ();
    // saveCommentRaw.setText("保存纯净棋谱(带评论)");
    saveCommentRaw.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            Lizzie.frame.saveRawFileComment();
          }
        });
    saveMore.add(saveCommentRaw);

    final JFontMenuItem saveBranchRaw =
        new JFontMenuItem(resourceBundle.getString("Menu.saveBranchRaw")); // ();
    // saveBranchRaw.setText("保存纯净分支(Crtl+Alt+S)");
    saveBranchRaw.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            LizzieFrame.saveCurrentBranch();
          }
        });
    saveMore.add(saveBranchRaw);

    final JFontMenuItem saveMainBoardScreen =
        new JFontMenuItem(resourceBundle.getString("Menu.saveMainBoardScreen"));
    saveMainBoardScreen.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            Lizzie.frame.saveMainBoardPicture();
          }
        });
    saveMore.add(saveMainBoardScreen);

    final JFontMenuItem saveSubBoardScreen =
        new JFontMenuItem(resourceBundle.getString("Menu.saveSubBoardScreen"));
    saveSubBoardScreen.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            Lizzie.frame.saveSubBoardPicture();
          }
        });
    saveMore.add(saveSubBoardScreen);

    final JFontMenuItem saveWinrate =
        new JFontMenuItem(resourceBundle.getString("Menu.saveWinrate")); // ();
    // saveWinrate.setText("保存胜率图(Shift+S)");
    saveWinrate.addActionListener(
        new ActionListener() {

          @Override
          public void actionPerformed(ActionEvent e) {
            Lizzie.frame.saveImage(
                Lizzie.frame.statx,
                Lizzie.frame.staty,
                (int) (Lizzie.frame.grw * 1.03),
                Lizzie.frame.grh + Lizzie.frame.stath);
          }
        });
    saveMore.add(saveWinrate);

    final JFontMenuItem saveAndLoad =
        new JFontMenuItem(resourceBundle.getString("Menu.saveAndLoad")); // ();
    // autoSave.setText("存档与读档");
    saveAndLoad.addActionListener(
        new ActionListener() {

          @Override
          public void actionPerformed(ActionEvent e) {
            // TODO Auto-generated method stub
            Lizzie.frame.showTempGamePanel();
          }
        });
    fileMenu.add(saveAndLoad);

    fileMenu.addSeparator();

    final JFontMenuItem copyBoardScreen =
        new JFontMenuItem(
            resourceBundle.getString("Menu.copyBoardScreen")); // ("复制主棋盘到剪贴板(Shift+C)");
    fileMenu.add(copyBoardScreen);
    copyBoardScreen.addActionListener(
        new ActionListener() {

          @Override
          public void actionPerformed(ActionEvent e) {
            // TODO Auto-generated method stub
            Lizzie.frame.saveMainBoardToClipboard();
          }
        });

    final JFontMenuItem copySubBoardScreen =
        new JFontMenuItem(
            resourceBundle.getString("Menu.copySubBoardScreen")); // ("复制小棋盘到剪贴板(Alt+C)");
    fileMenu.add(copySubBoardScreen);
    copySubBoardScreen.addActionListener(
        new ActionListener() {

          @Override
          public void actionPerformed(ActionEvent e) {
            // TODO Auto-generated method stub
            Lizzie.frame.copySubBoard();
          }
        });

    final JFontMenuItem copySgf =
        new JFontMenuItem(resourceBundle.getString("Menu.copySgf")); // ();
    // copySgf.setText("复制棋谱到剪贴板(Ctrl+C)");
    fileMenu.add(copySgf);
    copySgf.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            // TODO Auto-generated method stub
            Lizzie.frame.copySgf();
          }
        });

    final JFontMenuItem pasteSgf =
        new JFontMenuItem(resourceBundle.getString("Menu.pasteSgf")); // ();
    // pasteSgf.setText("从剪贴板粘贴棋谱(Ctrl+V)");
    fileMenu.add(pasteSgf);
    fileMenu.addSeparator();
    pasteSgf.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            // TODO Auto-generated method stub
            Lizzie.frame.pasteSgf();
          }
        });

    final JFontCheckBoxMenuItem loadKomi =
        new JFontCheckBoxMenuItem(resourceBundle.getString("Menu.loadKomi")); // ();
    // loadKomi.setText("自动加载棋谱中的贴目");
    loadKomi.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            // TODO Auto-generated method stub
            Lizzie.config.readKomi = !Lizzie.config.readKomi;
            Lizzie.config.uiConfig.put("read-komi", Lizzie.config.readKomi);
          }
        });
    fileMenu.add(loadKomi);

    //
    //    final JFontMenuItem resume = new JFontMenuItem(resourceBundle.getString("Menu.resume"));
    // // ();
    //    // resume.setText("恢复自动保存的棋谱");
    //    fileMenu.add(resume);
    //
    //    resume.addActionListener(
    //        new ActionListener() {
    //          @Override
    //          public void actionPerformed(ActionEvent e) {
    //            //  Lizzie.board.resumePreviousGame();
    //            Lizzie.frame.resumeFile();
    //          }
    //        });

    fileMenu.addSeparator();

    final JFontMenuItem forceExit =
        new JFontMenuItem(resourceBundle.getString("Menu.forceExit")); // ();
    // forceExit.setText("强制退出");
    forceExit.addActionListener(
        new ActionListener() {

          @Override
          public void actionPerformed(ActionEvent e) {
            // TODO Auto-generated method stub
            System.exit(0);
          }
        });
    fileMenu.add(forceExit);

    final JFontMenuItem exit = new JFontMenuItem(resourceBundle.getString("Menu.exit")); // ();
    // exit.setText("退出");
    exit.addActionListener(
        new ActionListener() {

          @Override
          public void actionPerformed(ActionEvent e) {
            // TODO Auto-generated method stub
            Lizzie.shutdown();
          }
        });
    fileMenu.add(exit);

    fileMenu.addMenuListener(
        new MenuListener() {

          public void menuSelected(MenuEvent e) {
            //            if (Lizzie.config.uiConfig.optInt("autosave-interval-seconds", -1) > 0)
            //              autoSave.setState(true);
            //            else autoSave.setState(false);
            if (Lizzie.config.readKomi) loadKomi.setState(true);
            else loadKomi.setState(false);
          }

          @Override
          public void menuDeselected(MenuEvent e) {
            // TODO Auto-generated method stub
          }

          @Override
          public void menuCanceled(MenuEvent e) {
            // TODO Auto-generated method stub

          }
        });

    final JFontMenu viewMenu = new JFontMenu(resourceBundle.getString("Menu.viewMenu")); // ("显示");
    this.add(viewMenu);
    viewMenu.setForeground(Color.BLACK);
    // viewMenu.setFont(headFont);
    final JFontMenu panel = new JFontMenu(resourceBundle.getString("Menu.panel")); // ("面板");
    viewMenu.add(panel);

    final JFontMenu toolBar = new JFontMenu(resourceBundle.getString("Menu.toolbar"));
    toolBar.setForeground(Color.BLACK);
    viewMenu.add(toolBar);

    final JFontMenu mainBoardPos =
        new JFontMenu(resourceBundle.getString("Menu.mainBoardPos")); // ("主棋盘位置");
    viewMenu.add(mainBoardPos);

    final JFontMenuItem leftMove =
        new JFontMenuItem(resourceBundle.getString("Menu.leftMove")); // ("左移 ([)");
    leftMove.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            // TODO Auto-generated method stub
            if (Lizzie.frame.BoardPositionProportion > 0) Lizzie.frame.BoardPositionProportion--;
            Lizzie.frame.refresh();
          }
        });
    mainBoardPos.add(leftMove);

    final JFontMenuItem rightMove =
        new JFontMenuItem(resourceBundle.getString("Menu.rightMove")); // ("右移 (])");
    rightMove.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            // TODO Auto-generated method stub
            if (Lizzie.frame.BoardPositionProportion < 8) Lizzie.frame.BoardPositionProportion++;
            Lizzie.frame.refresh();
          }
        });
    mainBoardPos.add(rightMove);

    final JFontCheckBoxMenuItem coordsMenu =
        new JFontCheckBoxMenuItem(resourceBundle.getString("Menu.coordsMenu")); // ("坐标(C)");
    coordsMenu.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            // TODO Auto-generated method stub
            Lizzie.config.toggleCoordinates();
            Lizzie.frame.refresh();
          }
        });
    viewMenu.add(coordsMenu);

    final JFontMenu moveMenu =
        new JFontMenu(resourceBundle.getString("Menu.moveMenu")); // ("手数(M)");
    viewMenu.add(moveMenu);

    final JFontMenu moveRankMenu =
        new JFontMenu(resourceBundle.getString("Menu.moveRankMenu")); // ("落子评价标记(Alt+M)");
    viewMenu.add(moveRankMenu);
    addRankMarkMenu(null, moveRankMenu);

    final JFontMenu Suggestions =
        new JFontMenu(resourceBundle.getString("Menu.Suggestions")); // ("选点");
    viewMenu.add(Suggestions);

    final JFontMenu nextMoveHint =
        new JFontMenu(resourceBundle.getString("Menu.nextMoveHint")); // 下一手
    viewMenu.add(nextMoveHint);

    final JFontMenuItem makeAutoPlay =
        new JFontMenuItem(resourceBundle.getString("Menu.makeAutoPlay")); // ("自动播放(Ctrl+A)");
    makeAutoPlay.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            AutoPlay autoPlay = new AutoPlay();
            autoPlay.setVisible(true);
          }
        });
    viewMenu.add(makeAutoPlay);

    final JFontCheckBoxMenuItem nextMoveHintNone =
        new JFontCheckBoxMenuItem(resourceBundle.getString("Menu.nextMoveHintNone")); // ("不显示");
    nextMoveHintNone.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            // TODO Auto-generated method stub
            Lizzie.config.setShowNextMoves(false, false);
          }
        });
    nextMoveHint.add(nextMoveHintNone);

    final JFontCheckBoxMenuItem nextMoveHintSimple =
        new JFontCheckBoxMenuItem(resourceBundle.getString("Menu.nextMoveHintSimple")); // ("不显示");
    nextMoveHintSimple.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            // TODO Auto-generated method stub
            Lizzie.config.setShowNextMoves(true, false);
          }
        });
    nextMoveHint.add(nextMoveHintSimple);

    final JFontCheckBoxMenuItem nextMoveHintInformation =
        new JFontCheckBoxMenuItem(resourceBundle.getString("Menu.nextMoveHintInformation"));
    nextMoveHintInformation.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            // TODO Auto-generated method stub
            Lizzie.config.setShowNextMoves(true, true);
          }
        });
    nextMoveHint.add(nextMoveHintInformation);

    minPlayoutsForNextMove =
        new JFontMenuItem(
            resourceBundle.getString("Menu.minPlayoutsForNextMove")
                + Lizzie.config.minPlayoutsForNextMove
                + ")");
    minPlayoutsForNextMove.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            SwingUtilities.invokeLater(
                new Runnable() {
                  public void run() {
                    String result =
                        JOptionPane.showInputDialog(
                            Lizzie.frame,
                            resourceBundle.getString("Menu.minPlayoutsForNextMoveMessage"),
                            Lizzie.config.minPlayoutsForNextMove);
                    if (result != null)
                      try {
                        int numbers = Integer.parseInt(result);
                        Lizzie.config.minPlayoutsForNextMove = numbers;
                        Lizzie.frame.refresh();
                        resetMinPlayoutsForNextMove();
                        Lizzie.config.uiConfig.put(
                            "min-playouts-for-next-move", Lizzie.config.minPlayoutsForNextMove);
                      } catch (NumberFormatException ex) {
                        Utils.showMsg(resourceBundle.getString("Menu.inputIntegerHint"));
                        return;
                      }
                  }
                });
          }
        });
    nextMoveHint.add(minPlayoutsForNextMove);

    final JFontMenuItem deletePersistFile =
        new JFontMenuItem(resourceBundle.getString("Menu.deletePersistFile")); // ("重置界面位置");
    deletePersistFile.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            Lizzie.config.deletePersist(true);
            Lizzie.resetAllHints();
          }
        });
    viewMenu.add(deletePersistFile);

    viewMenu.addSeparator();

    final JFontCheckBoxMenuItem noMoveNum =
        new JFontCheckBoxMenuItem(resourceBundle.getString("Menu.noMoveNum")); // ("不显示");
    noMoveNum.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            // TODO Auto-generated method stub
            Lizzie.config.setMoveNumber(0);
          }
        });
    moveMenu.add(noMoveNum);

    final JFontCheckBoxMenuItem lastOneMoveNum =
        new JFontCheckBoxMenuItem(resourceBundle.getString("Menu.lastOneMoveNum")); // ("最近1手");
    lastOneMoveNum.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            // TODO Auto-generated method stub
            Lizzie.config.setMoveNumber(1);
          }
        });
    moveMenu.add(lastOneMoveNum);

    final JFontCheckBoxMenuItem lastFiveMoveNum =
        new JFontCheckBoxMenuItem(resourceBundle.getString("Menu.lastFiveMoveNum")); // ("最近5手");
    lastFiveMoveNum.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            // TODO Auto-generated method stub
            Lizzie.config.setMoveNumber(5);
          }
        });
    moveMenu.add(lastFiveMoveNum);

    final JFontCheckBoxMenuItem lastTenMoveNum =
        new JFontCheckBoxMenuItem(resourceBundle.getString("Menu.lastTenMoveNum")); // ("最近10手");
    lastTenMoveNum.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            // TODO Auto-generated method stub
            Lizzie.config.setMoveNumber(10);
          }
        });
    moveMenu.add(lastTenMoveNum);

    final JFontCheckBoxMenuItem allMoveNum =
        new JFontCheckBoxMenuItem(resourceBundle.getString("Menu.allMoveNum")); // ("全部");
    allMoveNum.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            // TODO Auto-generated method stub
            Lizzie.config.setMoveNumber(-1);
          }
        });
    moveMenu.add(allMoveNum);

    final JFontCheckBoxMenuItem anyMoveNum =
        new JFontCheckBoxMenuItem(resourceBundle.getString("Menu.anyMoveNum")); // ("自定义");
    anyMoveNum.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            // TODO Auto-generated method stub
            MovenumberDialog mvDialog = new MovenumberDialog();
            mvDialog.setVisible(true);
          }
        });
    moveMenu.add(anyMoveNum);
    moveMenu.addSeparator();

    final JFontCheckBoxMenuItem moveNumberAlwaysFromOne =
        new JFontCheckBoxMenuItem(
            resourceBundle.getString("Menu.moveNumberAlwaysFromOne")); // ("总是从1开始显示");
    moveNumberAlwaysFromOne.addActionListener(
        new ActionListener() {

          @Override
          public void actionPerformed(ActionEvent e) {
            // TODO Auto-generated method stub
            Lizzie.config.showMoveNumberFromOne = !Lizzie.config.showMoveNumberFromOne;
            Lizzie.config.uiConfig.put("movenumber-from-one", Lizzie.config.showMoveNumberFromOne);
            Lizzie.config.showMoveAllInBranch = false;
            Lizzie.config.uiConfig.putOpt("show-moveall-inbranch", false);
            Lizzie.frame.refresh();
          }
        });
    moveMenu.add(moveNumberAlwaysFromOne);

    final JFontCheckBoxMenuItem moveNumberInBracnhFromOne =
        new JFontCheckBoxMenuItem(
            resourceBundle.getString("Menu.moveNumberInBracnhFromOne")); // ("分支内从1开始显示");
    moveNumberInBracnhFromOne.addActionListener(
        new ActionListener() {

          @Override
          public void actionPerformed(ActionEvent e) {
            // TODO Auto-generated method stub
            Lizzie.config.newMoveNumberInBranch = true;
            // Lizzie.frame.refresh();
            Lizzie.config.uiConfig.put(
                "new-move-number-in-branch", Lizzie.config.newMoveNumberInBranch);
          }
        });
    moveMenu.add(moveNumberInBracnhFromOne);

    final JFontCheckBoxMenuItem moveNumberInBracnhFromOneContinue =
        new JFontCheckBoxMenuItem(
            resourceBundle.getString("Menu.moveNumberInBracnhFromOneContinue")); // ("分支内继续总手数");
    moveNumberInBracnhFromOneContinue.addActionListener(
        new ActionListener() {

          @Override
          public void actionPerformed(ActionEvent e) {
            // TODO Auto-generated method stub
            // TODO Auto-generated method stub
            Lizzie.config.newMoveNumberInBranch = false;
            // Lizzie.frame.refresh();
            Lizzie.config.uiConfig.put(
                "new-move-number-in-branch", Lizzie.config.newMoveNumberInBranch);
          }
        });
    moveMenu.add(moveNumberInBracnhFromOneContinue);

    final JFontCheckBoxMenuItem showAllMoveNumberInBranch =
        new JFontCheckBoxMenuItem(
            resourceBundle.getString("Menu.showAllMoveNumberInBranch")); // ("分支内总是显示所有手数");
    showAllMoveNumberInBranch.addActionListener(
        new ActionListener() {

          @Override
          public void actionPerformed(ActionEvent e) {
            // TODO Auto-generated method stub
            Lizzie.config.toggleShowMoveAllInBranch();
            Lizzie.frame.refresh();
          }
        });
    moveMenu.add(showAllMoveNumberInBranch);

    final JFontCheckBoxMenuItem showMoveNumberOnVariationPane =
        new JFontCheckBoxMenuItem(
            resourceBundle.getString("Menu.showMoveNumberOnVariationPane")); // ("显示分支面板上的手数");
    showMoveNumberOnVariationPane.addActionListener(
        new ActionListener() {

          @Override
          public void actionPerformed(ActionEvent e) {
            // TODO Auto-generated method stub
            Lizzie.config.showVarMove = !Lizzie.config.showVarMove;
            Lizzie.config.uiConfig.put("show-var-move", Lizzie.config.showVarMove);
            if (Lizzie.config.showVariationGraph) Lizzie.frame.renderVarTree(0, 0, false, false);
          }
        });
    moveMenu.add(showMoveNumberOnVariationPane);

    final JFontCheckBoxMenuItem largeSubBoard =
        new JFontCheckBoxMenuItem(
            resourceBundle.getString("Menu.largeSubBoard")); // ("放大小棋盘(Shift+F)");
    largeSubBoard.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            Lizzie.config.toggleLargeSubBoard();
          }
        });

    final JFontCheckBoxMenuItem largeWinrateGraph =
        new JFontCheckBoxMenuItem(
            resourceBundle.getString("Menu.largeWinrateGraph")); // ("放大胜率图(Ctrl+W)");
    largeWinrateGraph.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            Lizzie.config.toggleLargeWinrate();
          }
        });

    final JFontCheckBoxMenuItem appendWinrateToComment =
        new JFontCheckBoxMenuItem(
            resourceBundle.getString("Menu.appendWinrateToComment")); // ("记录胜率到评论中");
    appendWinrateToComment.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            Lizzie.config.toggleappendWinrateToComment();
          }
        });

    final JFontCheckBoxMenuItem showNameInBoard =
        new JFontCheckBoxMenuItem(
            resourceBundle.getString("Menu.showNameInBoard")); // ("棋盘下方显示黑白名字");
    showNameInBoard.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            Lizzie.config.showNameInBoard = !Lizzie.config.showNameInBoard;
            Lizzie.config.uiConfig.put("show-name-in-board", Lizzie.config.showNameInBoard);
            Lizzie.board.setForceRefresh(true);
            Lizzie.frame.refresh();
          }
        });

    final JFontCheckBoxMenuItem showCommentConrolPane =
        new JFontCheckBoxMenuItem(resourceBundle.getString("Menu.showCommentConrolPane"));
    showCommentConrolPane.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            Lizzie.config.hideBlunderControlPane = !Lizzie.config.hideBlunderControlPane;
            Lizzie.config.uiConfig.put(
                "hide-blunder-table-control-pane", Lizzie.config.hideBlunderControlPane);
          }
        });

    final JFontCheckBoxMenuItem alwaysOnTop =
        new JFontCheckBoxMenuItem(
            resourceBundle.getString("Menu.alwaysOnTop")); // ("总在最前(Ctrl+Z)");
    alwaysOnTop.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            Lizzie.frame.toggleAlwaysOntop();
          }
        });

    final JFontMenu mainPanelSettings =
        new JFontMenu(resourceBundle.getString("Menu.mainPanelSettings")); // ("主界面设置");
    viewMenu.add(mainPanelSettings);

    mainPanelSettings.add(largeSubBoard);
    mainPanelSettings.add(largeWinrateGraph);
    mainPanelSettings.add(appendWinrateToComment);
    mainPanelSettings.add(showNameInBoard);
    mainPanelSettings.add(showCommentConrolPane);
    mainPanelSettings.add(alwaysOnTop);

    // viewMenu.addSeparator();

    final JFontCheckBoxMenuItem suggestion1 =
        new JFontCheckBoxMenuItem(resourceBundle.getString("Menu.winrate")); // ("胜率");
    suggestion1.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            Lizzie.config.showWinrateInSuggestion = !Lizzie.config.showWinrateInSuggestion;
            Lizzie.config.uiConfig.put(
                "show-winrate-in-suggestion", Lizzie.config.showWinrateInSuggestion);
            Lizzie.frame.refresh();
            refreshDoubleMoveInfoStatus();
          }
        });
    Suggestions.add(suggestion1);

    final JFontCheckBoxMenuItem suggestion2 =
        new JFontCheckBoxMenuItem(resourceBundle.getString("Menu.visits")); // ("计算量");
    suggestion2.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            Lizzie.config.showPlayoutsInSuggestion = !Lizzie.config.showPlayoutsInSuggestion;
            Lizzie.config.uiConfig.put(
                "show-playouts-in-suggestion", Lizzie.config.showPlayoutsInSuggestion);
            Lizzie.frame.refresh();
            refreshDoubleMoveInfoStatus();
          }
        });
    Suggestions.add(suggestion2);

    final JFontCheckBoxMenuItem suggestion3 =
        new JFontCheckBoxMenuItem(resourceBundle.getString("Menu.scoreLead")); // ("目差");
    suggestion3.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            Lizzie.config.showScoremeanInSuggestion = !Lizzie.config.showScoremeanInSuggestion;
            Lizzie.config.uiConfig.put(
                "show-scoremean-in-suggestion", Lizzie.config.showScoremeanInSuggestion);
            Lizzie.frame.refresh();
            refreshDoubleMoveInfoStatus();
          }
        });
    Suggestions.add(suggestion3);

    final JFontMenuItem customInfoOrdr =
        new JFontMenuItem(resourceBundle.getString("Menu.customInfoOrdr")); // ("目差");
    customInfoOrdr.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            LizzieFrame.openSuggestionInfoCustom(Lizzie.frame);
          }
        });
    Suggestions.add(customInfoOrdr);

    Suggestions.addSeparator();

    final JFontMenu winrate =
        new JFontMenu(resourceBundle.getString("Menu.winrateGraphSettings")); // ("胜率图设置");
    viewMenu.add(winrate);

    final JFontCheckBoxMenuItem subboard =
        new JFontCheckBoxMenuItem(resourceBundle.getString("Menu.subBoard")); // ("小棋盘(Z)");
    panel.add(subboard);
    subboard.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            Lizzie.config.toggleShowSubBoard();
          }
        });

    final JFontCheckBoxMenuItem winrateGraph =
        new JFontCheckBoxMenuItem(resourceBundle.getString("Menu.winrateGraph")); // ("胜率图(W)");
    panel.add(winrateGraph);
    winrateGraph.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            Lizzie.config.toggleShowWinrate();
          }
        });

    final JFontCheckBoxMenuItem commitPane =
        new JFontCheckBoxMenuItem(resourceBundle.getString("Menu.commitPane")); // ("评论面板(Alt+T)");
    panel.add(commitPane);
    commitPane.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            Lizzie.config.toggleShowComment();
          }
        });

    final JFontCheckBoxMenuItem variationPane =
        new JFontCheckBoxMenuItem(resourceBundle.getString("Menu.variationPane")); // ("分支面板(G)");
    panel.add(variationPane);
    variationPane.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            Lizzie.config.toggleShowVariationGraph();
          }
        });

    final JFontMenu variationPaneSettings =
        new JFontMenu(resourceBundle.getString("Menu.variationPaneSettings"));

    final JFontCheckBoxMenuItem showScrollVariation =
        new JFontCheckBoxMenuItem(resourceBundle.getString("Menu.showScrollVariation"));
    showScrollVariation.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            Lizzie.config.showScrollVariation = showScrollVariation.isSelected();
            Lizzie.config.uiConfig.put("show-scroll-variation", Lizzie.config.showScrollVariation);
            Lizzie.frame.repaint();
          }
        });

    final JFontMenuItem maxTreeWidth =
        new JFontMenuItem(resourceBundle.getString("Menu.maxTreeWidth"));
    maxTreeWidth.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            SwingUtilities.invokeLater(
                new Runnable() {
                  public void run() {
                    Box box = Box.createVerticalBox();
                    JFontLabel label =
                        new JFontLabel(resourceBundle.getString("Menu.maxTreeWidthHint"));
                    label.setAlignmentX(Component.LEFT_ALIGNMENT);
                    box.add(label);
                    Utils.addFiller(box, 5, 5);
                    Utils.addFiller(box, 5, 5);
                    JFontLabel label2 =
                        new JFontLabel(resourceBundle.getString("Menu.lblMaxTreeWidth"));
                    label2.setAlignmentX(Component.LEFT_ALIGNMENT);
                    box.add(label2);
                    JFontTextField input = new JFontTextField();
                    input.setAlignmentX(Component.LEFT_ALIGNMENT);
                    input.setDocument(new IntDocument());
                    input.setText(String.valueOf(Lizzie.config.maxTreeWidth));
                    box.add(input);
                    Object[] options = new Object[2];
                    options[0] = resourceBundle.getString("LizzieFrame.confirm");
                    options[1] = resourceBundle.getString("LizzieFrame.cancel");
                    Object defaultOption = resourceBundle.getString("LizzieFrame.confirm");
                    JOptionPane optionPane =
                        new JOptionPane(
                            box,
                            JOptionPane.QUESTION_MESSAGE,
                            JOptionPane.YES_NO_OPTION,
                            null,
                            options,
                            defaultOption);
                    JDialog dialog =
                        optionPane.createDialog(
                            Lizzie.frame, resourceBundle.getString("Menu.setMaxTreeWidthTitle"));
                    dialog.setVisible(true);
                    dialog.dispose();
                    if (optionPane.getValue() != null
                        && optionPane.getValue().equals(defaultOption)) {
                      Lizzie.config.maxTreeWidth =
                          Utils.parseTextToInt(input, Lizzie.config.maxTreeWidth);
                      Lizzie.config.uiConfig.put("max-tree-width", Lizzie.config.maxTreeWidth);
                      Lizzie.board.clearBigBranch();
                      LizzieFrame.forceRecreate = true;
                      Lizzie.frame.repaint();
                    }
                  }
                });
          }
        });

    final JFontCheckBoxMenuItem ignoreOutOfWidth =
        new JFontCheckBoxMenuItem(resourceBundle.getString("Menu.ignoreOutOfWidth"));
    ignoreOutOfWidth.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            Lizzie.config.ignoreOutOfWidth = ignoreOutOfWidth.isSelected();
            Lizzie.config.uiConfig.put("ignore-out-of-width", Lizzie.config.ignoreOutOfWidth);
            Lizzie.frame.repaint();
          }
        });

    variationPaneSettings.add(showScrollVariation);
    variationPaneSettings.add(maxTreeWidth);
    variationPaneSettings.add(ignoreOutOfWidth);

    panel.add(variationPaneSettings);

    final JFontCheckBoxMenuItem listPane =
        new JFontCheckBoxMenuItem(resourceBundle.getString("Menu.listPane")); // ("选点列表面板(Alt+G)");
    panel.add(listPane);
    listPane.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            Lizzie.config.toggleShowListPane();
          }
        });

    final JFontCheckBoxMenuItem informationPane =
        new JFontCheckBoxMenuItem(resourceBundle.getString("Menu.informationPane")); // ("信息面板");
    panel.add(informationPane);
    informationPane.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            Lizzie.config.toggleShowCaptured();
          }
        });

    final JFontCheckBoxMenuItem statusPanel =
        new JFontCheckBoxMenuItem(resourceBundle.getString("Menu.statusPanel")); // ("状态面板");
    panel.add(statusPanel);
    statusPanel.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            Lizzie.config.toggleShowStatus();
          }
        });

    final JFontCheckBoxMenuItem gtpPanel =
        new JFontCheckBoxMenuItem(resourceBundle.getString("Menu.gtpPanel")); // ("Gtp控制台(E)");
    panel.add(gtpPanel);
    gtpPanel.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            Lizzie.frame.toggleGtpConsole();
          }
        });

    panel.addSeparator();

    final JFontMenuItem visualizedPanelSettings =
        new JFontMenuItem(resourceBundle.getString("Menu.visualizedPanelSettings"));
    visualizedPanelSettings.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            Lizzie.frame.visualizedPanelSettings();
          }
        });
    panel.add(visualizedPanelSettings);

    panel.addSeparator();

    final JFontCheckBoxMenuItem independentMainBoard =
        new JFontCheckBoxMenuItem(
            resourceBundle.getString("Menu.independentMainBoard")); // ("浮动主棋盘(Alt+Q)");
    panel.add(independentMainBoard);
    independentMainBoard.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            Lizzie.frame.toggleOnlyIndependMainBoard();
          }
        });

    final JFontCheckBoxMenuItem independentMainBoard2 =
        new JFontCheckBoxMenuItem(
            resourceBundle.getString("Menu.independentMainBoard2")); // ("浮动主棋盘(保留原棋盘)");
    panel.add(independentMainBoard2);
    independentMainBoard2.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            Lizzie.frame.toggleShowIndependMainBoard();
          }
        });

    final JFontCheckBoxMenuItem independentSubBoard =
        new JFontCheckBoxMenuItem(
            resourceBundle.getString("Menu.independentSubBoard")); // ("浮动小棋盘(Alt+W)");
    panel.add(independentSubBoard);
    independentSubBoard.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            Lizzie.frame.toggleIndependentSubBoard();
          }
        });

    final JFontCheckBoxMenuItem hawkEye =
        new JFontCheckBoxMenuItem(resourceBundle.getString("Menu.hawkEye")); // ("超级鹰眼(T)");
    hawkEye.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            Lizzie.frame.toggleBadMoves();
          }
        });
    panel.add(hawkEye);

    final JFontCheckBoxMenuItem SuggestionList =
        new JFontCheckBoxMenuItem(
            resourceBundle.getString("Menu.SuggestionList")); // ("独立选点列表(U)");
    SuggestionList.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            Lizzie.frame.toggleBestMoves();
          }
        });
    panel.add(SuggestionList);

    final JFontCheckBoxMenuItem winrateMode1 =
        new JFontCheckBoxMenuItem(resourceBundle.getString("Menu.winrateMode1")); // ("双方视角");
    winrateMode1.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            LizzieFrame.winrateGraph.mode = 1;
            Lizzie.frame.repaint();
          }
        });
    winrate.add(winrateMode1);

    final JFontCheckBoxMenuItem winrateMode0 =
        new JFontCheckBoxMenuItem(resourceBundle.getString("Menu.winrateMode0")); // ("黑方视角");
    winrateMode0.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            LizzieFrame.winrateGraph.mode = 0;
            Lizzie.frame.repaint();
          }
        });
    winrate.add(winrateMode0);
    winrate.addSeparator();
    // 增加设置胜率曲线宽度

    final JFontCheckBoxMenuItem showSuggestionOrder =
        new JFontCheckBoxMenuItem(
            resourceBundle.getString("Menu.showSuggestionOrder")); // ("显示选点右上方角标");
    showSuggestionOrder.addActionListener(
        new ActionListener() {

          @Override
          public void actionPerformed(ActionEvent e) {
            Lizzie.config.showSuggestionOrder = !Lizzie.config.showSuggestionOrder;
            Lizzie.config.uiConfig.put("show-suggestion-order", Lizzie.config.showSuggestionOrder);
          }
        });
    Suggestions.add(showSuggestionOrder);

    final JFontCheckBoxMenuItem showMaxValueReverse = new JFontCheckBoxMenuItem();
    showMaxValueReverse.setText(
        resourceBundle.getString(
            "Menu.showMaxValueReverse")); // 最高胜率-计算量-目差 反色显示 Reverse color for max value
    showMaxValueReverse.addActionListener(
        new ActionListener() {

          @Override
          public void actionPerformed(ActionEvent e) {
            Lizzie.config.showSuggestionMaxRed = !Lizzie.config.showSuggestionMaxRed;
            Lizzie.config.uiConfig.put(
                "show-suggestion-maxred", Lizzie.config.showSuggestionMaxRed);
          }
        });
    Suggestions.add(showMaxValueReverse);

    final JFontCheckBoxMenuItem showWhiteSuggestWhite =
        new JFontCheckBoxMenuItem(
            resourceBundle.getString(
                "Menu.showWhiteSuggestWhite")); // 轮白下字体显示为白色 Use white color for white turn
    showWhiteSuggestWhite.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            Lizzie.config.whiteSuggestionWhite = !Lizzie.config.whiteSuggestionWhite;
            Lizzie.config.uiConfig.put(
                "white-suggestion-white", Lizzie.config.whiteSuggestionWhite);
          }
        });
    Suggestions.add(showWhiteSuggestWhite);
    Suggestions.addSeparator();

    final JFontCheckBoxMenuItem alwaysShowBlackWinrate =
        new JFontCheckBoxMenuItem(
            resourceBundle.getString(
                "Menu.alwaysShowBlackWinrate")); // 总是显示黑胜率 Always show black winrate
    alwaysShowBlackWinrate.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            Lizzie.config.winrateAlwaysBlack = alwaysShowBlackWinrate.isSelected();
            Lizzie.config.uiConfig.put("win-rate-always-black", Lizzie.config.winrateAlwaysBlack);
            Lizzie.frame.refresh();
          }
        });
    Suggestions.add(alwaysShowBlackWinrate);

    final JFontCheckBoxMenuItem showVariationOnMouse =
        new JFontCheckBoxMenuItem(
            resourceBundle.getString(
                "Menu.showVariationOnMouse")); // 鼠标悬停显示变化图 Show variation on mouse over
    showVariationOnMouse.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            Lizzie.config.showSuggestionVariations = !Lizzie.config.showSuggestionVariations;
            Lizzie.config.uiConfig.put(
                "show-suggestion-variations", Lizzie.config.showSuggestionVariations);
          }
        });
    Suggestions.add(showVariationOnMouse);

    final JFontCheckBoxMenuItem noRefreshOnMouse =
        new JFontCheckBoxMenuItem(
            resourceBundle.getString(
                "Menu.noRefreshOnMouse")); // 鼠标悬停显示变化图时,变化图不刷新 Variation not refresh on mouse over
    noRefreshOnMouse.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            Lizzie.config.noRefreshOnMouseMove = !Lizzie.config.noRefreshOnMouseMove;
            Lizzie.config.uiConfig.put(
                "norefresh-onmouse-move", Lizzie.config.noRefreshOnMouseMove);
          }
        });
    Suggestions.add(noRefreshOnMouse);

    final JFontCheckBoxMenuItem showBlunderBar =
        new JFontCheckBoxMenuItem(
            resourceBundle.getString("Menu.showBlunderBar")); // Show blunder bar 显示柱状失误条
    showBlunderBar.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            Lizzie.config.showBlunderBar = !Lizzie.config.showBlunderBar;
            Lizzie.config.uiConfig.put("show-blunder-bar", Lizzie.config.showBlunderBar);
            Lizzie.frame.refresh();
          }
        });
    winrate.add(showBlunderBar);

    final JFontCheckBoxMenuItem showScoreLeadLine =
        new JFontCheckBoxMenuItem(
            resourceBundle.getString("Menu.showScoreLeadLine")); // Show blunder bar 显示目差曲线
    showScoreLeadLine.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            Lizzie.config.showScoreLeadLine = !Lizzie.config.showScoreLeadLine;
            Lizzie.config.uiConfig.put("show-score-lead-line", Lizzie.config.showScoreLeadLine);
            Lizzie.frame.refresh();
          }
        });
    winrate.add(showScoreLeadLine);

    final JFontCheckBoxMenuItem showMouseOverWinrateGraph =
        new JFontCheckBoxMenuItem(
            resourceBundle.getString("Menu.showMouseOverWinrateGraph")); // Show blunder bar 显示目差曲线
    showMouseOverWinrateGraph.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            boolean oriShowMouseOverWinrateGraph = Lizzie.config.showMouseOverWinrateGraph;
            Lizzie.config.showMouseOverWinrateGraph = !Lizzie.config.showMouseOverWinrateGraph;
            Lizzie.config.uiConfig.put(
                "show-mouse-over-winrate-graph", Lizzie.config.showMouseOverWinrateGraph);
            if (oriShowMouseOverWinrateGraph && !Lizzie.config.showMouseOverWinrateGraph)
              Lizzie.frame.clearMouseOverWinrateGraph();
          }
        });
    winrate.add(showMouseOverWinrateGraph);

    final JFontMenuItem setReplayInterval =
        new JFontMenuItem(
            resourceBundle.getString(
                "Menu.setReplayInterval")); // 设置选点变化图回放间隔 Set variation replay interval
    setReplayInterval.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            SetReplayTime setReplayTime = new SetReplayTime();
            setReplayTime.setVisible(true);
          }
        });
    Suggestions.add(setReplayInterval);

    final JFontMenu subBoardSettings =
        new JFontMenu(
            resourceBundle.getString("Menu.subBoardSettings")); // 小棋盘设置 Sub board settings
    viewMenu.add(subBoardSettings);

    final JFontMenu heatMapSettings =
        new JFontMenu(resourceBundle.getString("Menu.heatMapSettings")); // Heatmap 热点图
    subBoardSettings.add(heatMapSettings);

    final JFontCheckBoxMenuItem subBoardShowVar =
        new JFontCheckBoxMenuItem(
            resourceBundle.getString("Menu.subBoardShowVar")); // 变化图 Variation
    subBoardSettings.add(subBoardShowVar);

    subBoardShowVar.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            // TODO Auto-generated method stub
            if (LizzieFrame.subBoardRenderer == null) return;
            Lizzie.config.showHeat = false;
            Lizzie.config.subBoardRaw = false;
            Lizzie.config.showHeatAfterCalc = false;
            if (Lizzie.config.isFourSubMode()) {
              Lizzie.frame.subBoardRenderer4.showHeat = Lizzie.config.showHeat;
              Lizzie.frame.subBoardRenderer4.showHeatAfterCalc = Lizzie.config.showHeatAfterCalc;
              Lizzie.frame.subBoardRenderer4.removeHeat();
            }
            LizzieFrame.subBoardRenderer.showHeat = Lizzie.config.showHeat;
            LizzieFrame.subBoardRenderer.showHeatAfterCalc = Lizzie.config.showHeatAfterCalc;
            LizzieFrame.subBoardRenderer.removeHeat();
            Lizzie.config.uiConfig.put("show-heat", false);
            Lizzie.config.uiConfig.put("subboard-raw", false);
            Lizzie.config.uiConfig.put("show-heat-aftercalc", false);
            Lizzie.frame.refresh();
          }
        });

    final JFontCheckBoxMenuItem subBoardShowRaw =
        new JFontCheckBoxMenuItem(
            resourceBundle.getString("Menu.subBoardShowRaw")); // 仅棋盘棋子 Only board and stone
    subBoardSettings.add(subBoardShowRaw);

    subBoardShowRaw.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            // TODO Auto-generated method stub
            if (LizzieFrame.subBoardRenderer == null) return;
            Lizzie.config.showHeat = false;
            Lizzie.config.subBoardRaw = true;
            Lizzie.config.showHeatAfterCalc = false;
            if (Lizzie.config.isFourSubMode()) {
              Lizzie.frame.subBoardRenderer4.showHeat = Lizzie.config.showHeat;
              Lizzie.frame.subBoardRenderer4.showHeatAfterCalc = Lizzie.config.showHeatAfterCalc;
              Lizzie.frame.subBoardRenderer4.removeHeat();
              Lizzie.frame.subBoardRenderer4.branchOpt = Optional.empty();
            } else LizzieFrame.subBoardRenderer.branchOpt = Optional.empty();
            LizzieFrame.subBoardRenderer.showHeat = Lizzie.config.showHeat;
            LizzieFrame.subBoardRenderer.showHeatAfterCalc = Lizzie.config.showHeatAfterCalc;
            LizzieFrame.subBoardRenderer.removeHeat();
            Lizzie.config.uiConfig.put("show-heat", false);
            Lizzie.config.uiConfig.put("subboard-raw", true);
            Lizzie.config.uiConfig.put("show-heat-aftercalc", false);
            Lizzie.frame.refresh();
          }
        });

    final JFontCheckBoxMenuItem showHeat =
        new JFontCheckBoxMenuItem(resourceBundle.getString("Menu.showHeat")); // Heatmap 第一感热点图"
    heatMapSettings.add(showHeat);

    showHeat.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            // TODO Auto-generated method stub
            if (LizzieFrame.subBoardRenderer == null) return;
            Lizzie.config.showHeat = true;
            Lizzie.config.showHeatAfterCalc = false;
            if (Lizzie.config.isFourSubMode()) {
              Lizzie.frame.subBoardRenderer4.showHeat = Lizzie.config.showHeat;
              Lizzie.frame.subBoardRenderer4.showHeatAfterCalc = Lizzie.config.showHeatAfterCalc;
              Lizzie.frame.subBoardRenderer4.clearBranch();
              Lizzie.frame.subBoardRenderer4.removeHeat();
            } else {
              LizzieFrame.subBoardRenderer.showHeat = Lizzie.config.showHeat;
              LizzieFrame.subBoardRenderer.showHeatAfterCalc = Lizzie.config.showHeatAfterCalc;
              LizzieFrame.subBoardRenderer.clearBranch();
              LizzieFrame.subBoardRenderer.removeHeat();
            }
            Lizzie.config.uiConfig.put("show-heat", true);
            Lizzie.config.uiConfig.put("show-heat-aftercalc", false);
            Lizzie.frame.refresh();
          }
        });

    final JFontCheckBoxMenuItem showHeatAfterCalc =
        new JFontCheckBoxMenuItem(
            resourceBundle.getString("Menu.showHeatAfterCalc")); // Heatmap after calculation 计算后热点图
    heatMapSettings.add(showHeatAfterCalc);

    showHeatAfterCalc.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            // TODO Auto-generated method stub
            if (LizzieFrame.subBoardRenderer == null) return;
            Lizzie.config.showHeat = true;
            Lizzie.config.showHeatAfterCalc = true;
            if (Lizzie.config.isFourSubMode()) {
              Lizzie.frame.subBoardRenderer4.showHeat = Lizzie.config.showHeat;
              Lizzie.frame.subBoardRenderer4.showHeatAfterCalc = Lizzie.config.showHeatAfterCalc;
              Lizzie.frame.subBoardRenderer4.clearBranch();
              Lizzie.frame.subBoardRenderer4.removeHeat();
            } else {
              LizzieFrame.subBoardRenderer.showHeat = Lizzie.config.showHeat;
              LizzieFrame.subBoardRenderer.showHeatAfterCalc = Lizzie.config.showHeatAfterCalc;
              LizzieFrame.subBoardRenderer.clearBranch();
              LizzieFrame.subBoardRenderer.removeHeat();
            }
            Lizzie.config.uiConfig.put("show-heat", true);
            Lizzie.config.uiConfig.put("show-heat-aftercalc", true);
            Lizzie.frame.refresh();
          }
        });

    final JFontCheckBoxMenuItem notShowHeat =
        new JFontCheckBoxMenuItem(
            resourceBundle.getString("Menu.notShowHeat")); // No heatmap 不显示热点图
    heatMapSettings.add(notShowHeat);

    notShowHeat.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            // TODO Auto-generated method stub
            if (LizzieFrame.subBoardRenderer == null) return;
            Lizzie.config.showHeat = false;
            Lizzie.config.subBoardRaw = false;
            Lizzie.config.showHeatAfterCalc = false;
            if (Lizzie.config.isFourSubMode()) {
              Lizzie.frame.subBoardRenderer4.showHeat = Lizzie.config.showHeat;
              Lizzie.frame.subBoardRenderer4.showHeatAfterCalc = Lizzie.config.showHeatAfterCalc;
              Lizzie.frame.subBoardRenderer4.removeHeat();
            }
            LizzieFrame.subBoardRenderer.showHeat = Lizzie.config.showHeat;
            LizzieFrame.subBoardRenderer.showHeatAfterCalc = Lizzie.config.showHeatAfterCalc;
            LizzieFrame.subBoardRenderer.removeHeat();
            Lizzie.config.uiConfig.put("show-heat", false);
            Lizzie.config.uiConfig.put("subboard-raw", false);
            Lizzie.config.uiConfig.put("show-heat-aftercalc", false);
          }
        });

    final JFontMenu kataSettings =
        new JFontMenu(resourceBundle.getString("Menu.kataSettings")); // KataGo相关设置
    viewMenu.add(kataSettings);
    viewMenu.addSeparator();

    final JFontMenuItem defaultView =
        new JFontMenuItem(
            resourceBundle.getString("Menu.defaultView")); // 默认模式(Alt+1) Default mode (Alt+1)
    viewMenu.add(defaultView);
    defaultView.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            Lizzie.frame.defaultMode();
          }
        });

    final JFontMenuItem classicView =
        new JFontMenuItem(resourceBundle.getString("Menu.classicView")); // ("经典模式(Alt+2)");
    viewMenu.add(classicView);
    classicView.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            Lizzie.frame.classicMode();
          }
        });
    final JFontMenuItem minView =
        new JFontMenuItem(resourceBundle.getString("Menu.minView")); // ("精简模式(Alt+3)");
    viewMenu.add(minView);
    minView.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            Lizzie.frame.minMode();
          }
        });
    viewMenu.addSeparator();

    final JFontCheckBoxMenuItem extraMode3 =
        new JFontCheckBoxMenuItem(resourceBundle.getString("Menu.extraMode3")); // ("思考模式(Alt+4)");
    viewMenu.add(extraMode3);

    extraMode3.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            // TODO Auto-generated method stub
            Lizzie.config.toggleExtraMode(3);
          }
        });

    final JFontCheckBoxMenuItem extraMode1 =
        new JFontCheckBoxMenuItem(resourceBundle.getString("Menu.extraMode1")); // ("四方图模式(Alt+5)");
    viewMenu.add(extraMode1);

    extraMode1.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            // TODO Auto-generated method stub
            Lizzie.config.toggleExtraMode(1);
          }
        });

    final JFontCheckBoxMenuItem extraMode2 =
        new JFontCheckBoxMenuItem(resourceBundle.getString("Menu.extraMode2")); // ("双引擎模式(Alt+6)");
    viewMenu.add(extraMode2);

    extraMode2.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            // TODO Auto-generated method stub
            Lizzie.config.toggleExtraMode(2);
          }
        });

    final JFontMenu independentBoardMode =
        new JFontMenu(resourceBundle.getString("Menu.independentBoardMode")); // 浮动棋盘模式
    viewMenu.add(independentBoardMode);

    final JFontMenuItem indepBothBoard =
        new JFontMenuItem(resourceBundle.getString("Menu.indepBothBoard")); // 浮动双棋盘(Alt+7)
    independentBoardMode.add(indepBothBoard);

    indepBothBoard.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            // TODO Auto-generated method stub
            Lizzie.frame.independentBoardMode(false);
          }
        });

    final JFontMenuItem indepMainBoard =
        new JFontMenuItem(resourceBundle.getString("Menu.indepMainBoard")); // 浮动主棋盘
    independentBoardMode.add(indepMainBoard);

    indepMainBoard.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            // TODO Auto-generated method stub
            Lizzie.frame.independentBoardMode(true);
            if (Lizzie.config.isShowingIndependentSub) Lizzie.frame.toggleIndependentSubBoard();
          }
        });

    final JFontMenuItem indepSubBoard =
        new JFontMenuItem(resourceBundle.getString("Menu.indepSubBoard")); // 浮动小棋盘
    independentBoardMode.add(indepSubBoard);

    indepSubBoard.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            // TODO Auto-generated method stub
            Lizzie.frame.defaultMode();
            Lizzie.config.toggleShowSubBoard();
            if (!Lizzie.config.isShowingIndependentSub) Lizzie.frame.toggleIndependentSubBoard();
            if (Lizzie.config.isShowingIndependentMain) Lizzie.frame.toggleIndependentMainBoard();
          }
        });

    final JFontMenuItem indepExtraBoard =
        new JFontMenuItem(resourceBundle.getString("Menu.indepExtraBoard")); // 额外浮动棋盘
    independentBoardMode.add(indepExtraBoard);

    indepExtraBoard.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            // TODO Auto-generated method stub
            Lizzie.frame.defaultMode();
            if (!Lizzie.config.isShowingIndependentSub) Lizzie.frame.toggleIndependentSubBoard();
            if (!Lizzie.config.isShowingIndependentMain) Lizzie.frame.toggleIndependentMainBoard();
          }
        });

    viewMenu.addSeparator();

    JFontMenuItem customeMode1 = new JFontMenuItem();
    customeMode1.setLayout(null);
    JFontLabel lblCustomMode1 =
        new JFontLabel(
            resourceBundle.getString("Menu.customLayout")
                + "1"
                + (Lizzie.config.isChinese ? "" : " ")
                + "(Alt+8)");
    JFontButton btnSetCustomMode1 =
        new JFontButton(resourceBundle.getString("Menu.customLayoutSet"));
    btnSetCustomMode1.setMargin(new Insets(0, 0, 0, 0));
    customeMode1.setPreferredSize(
        new Dimension(
            (Lizzie.config.useJavaLooks ? 20 : 37)
                + (Lizzie.config.isChinese
                    ? (Lizzie.config.isFrameFontSmall()
                        ? 153
                        : (Lizzie.config.isFrameFontMiddle() ? 195 : 245))
                    : (Lizzie.config.isFrameFontSmall()
                        ? 185
                        : (Lizzie.config.isFrameFontMiddle() ? 235 : 290))),
            (Lizzie.config.useJavaLooks
                ? (Lizzie.config.isFrameFontSmall()
                    ? 20
                    : (Lizzie.config.isFrameFontMiddle() ? 25 : 30))
                : (Lizzie.config.isFrameFontSmall()
                    ? 25
                    : (Lizzie.config.isFrameFontMiddle() ? 27 : 30)))));
    lblCustomMode1.setBounds(
        Lizzie.config.useJavaLooks ? 20 : 37,
        (Lizzie.config.useJavaLooks
            ? -1
            : (Lizzie.config.isFrameFontSmall()
                ? 2
                : (Lizzie.config.isFrameFontMiddle() ? 1 : -1))),
        320,
        Lizzie.config.isFrameFontSmall() ? 20 : (Lizzie.config.isFrameFontMiddle() ? 25 : 30));
    btnSetCustomMode1.setBounds(
        (Lizzie.config.useJavaLooks ? 20 : 37)
            + (Lizzie.config.isChinese
                ? (Lizzie.config.isFrameFontSmall()
                    ? 106
                    : (Lizzie.config.isFrameFontMiddle() ? 142 : 180))
                : (Lizzie.config.isFrameFontSmall()
                    ? 140
                    : (Lizzie.config.isFrameFontMiddle() ? 178 : 224))),
        (Lizzie.config.useJavaLooks
            ? 1
            : (Lizzie.config.isFrameFontSmall()
                ? 2
                : (Lizzie.config.isFrameFontMiddle() ? 1 : -1))),
        Lizzie.config.isFrameFontSmall() ? 40 : (Lizzie.config.isFrameFontMiddle() ? 50 : 60),
        Lizzie.config.isFrameFontSmall()
            ? (Lizzie.config.useJavaLooks ? 18 : 22)
            : (Lizzie.config.isFrameFontMiddle()
                ? (Lizzie.config.useJavaLooks ? 23 : 25)
                : (Lizzie.config.useJavaLooks ? 28 : 30)));
    customeMode1.add(lblCustomMode1);
    customeMode1.add(btnSetCustomMode1);
    customeMode1.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            Lizzie.frame.switchToCustomMode(1);
          }
        });
    btnSetCustomMode1.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            Lizzie.frame.setCustomMode(1);
          }
        });
    viewMenu.add(customeMode1);

    JFontMenuItem customeMode2 = new JFontMenuItem();
    customeMode2.setLayout(null);
    JFontLabel lblCustomMode2 =
        new JFontLabel(
            resourceBundle.getString("Menu.customLayout")
                + "2"
                + (Lizzie.config.isChinese ? "" : " ")
                + "(Alt+9)");
    JFontButton btnSetCustomMode2 =
        new JFontButton(resourceBundle.getString("Menu.customLayoutSet"));
    btnSetCustomMode2.setMargin(new Insets(0, 0, 0, 0));
    customeMode2.setPreferredSize(
        new Dimension(
            (Lizzie.config.useJavaLooks ? 20 : 37)
                + (Lizzie.config.isChinese
                    ? (Lizzie.config.isFrameFontSmall()
                        ? 153
                        : (Lizzie.config.isFrameFontMiddle() ? 195 : 245))
                    : (Lizzie.config.isFrameFontSmall()
                        ? 185
                        : (Lizzie.config.isFrameFontMiddle() ? 235 : 290))),
            (Lizzie.config.useJavaLooks
                ? (Lizzie.config.isFrameFontSmall()
                    ? 20
                    : (Lizzie.config.isFrameFontMiddle() ? 25 : 30))
                : (Lizzie.config.isFrameFontSmall()
                    ? 25
                    : (Lizzie.config.isFrameFontMiddle() ? 27 : 30)))));
    lblCustomMode2.setBounds(
        Lizzie.config.useJavaLooks ? 20 : 37,
        (Lizzie.config.useJavaLooks
            ? -1
            : (Lizzie.config.isFrameFontSmall()
                ? 2
                : (Lizzie.config.isFrameFontMiddle() ? 1 : -1))),
        320,
        Lizzie.config.isFrameFontSmall() ? 20 : (Lizzie.config.isFrameFontMiddle() ? 25 : 30));
    btnSetCustomMode2.setBounds(
        (Lizzie.config.useJavaLooks ? 20 : 37)
            + (Lizzie.config.isChinese
                ? (Lizzie.config.isFrameFontSmall()
                    ? 106
                    : (Lizzie.config.isFrameFontMiddle() ? 142 : 180))
                : (Lizzie.config.isFrameFontSmall()
                    ? 140
                    : (Lizzie.config.isFrameFontMiddle() ? 178 : 224))),
        (Lizzie.config.useJavaLooks
            ? 1
            : (Lizzie.config.isFrameFontSmall()
                ? 2
                : (Lizzie.config.isFrameFontMiddle() ? 1 : -1))),
        Lizzie.config.isFrameFontSmall() ? 40 : (Lizzie.config.isFrameFontMiddle() ? 50 : 60),
        Lizzie.config.isFrameFontSmall()
            ? (Lizzie.config.useJavaLooks ? 18 : 22)
            : (Lizzie.config.isFrameFontMiddle()
                ? (Lizzie.config.useJavaLooks ? 23 : 25)
                : (Lizzie.config.useJavaLooks ? 28 : 30)));
    customeMode2.add(lblCustomMode2);
    customeMode2.add(btnSetCustomMode2);
    customeMode2.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            Lizzie.frame.switchToCustomMode(2);
          }
        });
    btnSetCustomMode2.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            Lizzie.frame.setCustomMode(2);
          }
        });
    viewMenu.add(customeMode2);

    final JFontMenu kataScolreLead =
        new JFontMenu(resourceBundle.getString("Menu.kataScolreLead")); // 目差显示
    kataSettings.add(kataScolreLead);

    final JFontCheckBoxMenuItem leadWithKomi =
        new JFontCheckBoxMenuItem(resourceBundle.getString("Menu.leadWithKomi")); // 目差
    kataScolreLead.add(leadWithKomi);
    leadWithKomi.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            Lizzie.config.showKataGoBoardScoreMean = false;
            Lizzie.config.uiConfig.put(
                "show-katago-boardscoremean", Lizzie.config.showKataGoBoardScoreMean);
          }
        });

    final JFontCheckBoxMenuItem leadWithoutKomi =
        new JFontCheckBoxMenuItem(resourceBundle.getString("Menu.leadWithoutKomi")); // 盘面
    kataScolreLead.add(leadWithoutKomi);
    leadWithoutKomi.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            Lizzie.config.showKataGoBoardScoreMean = true;
            Lizzie.config.uiConfig.put(
                "show-katago-boardscoremean", Lizzie.config.showKataGoBoardScoreMean);
          }
        });

    //    final JFontMenu kataScoreLeadPerspective =
    //        new JFontMenu(
    //            resourceBundle.getString(
    //                "Menu.kataScoreLeadPerspective")); // 目差视角 Score lead perspective
    //    kataSettings.add(kataScoreLeadPerspective);
    //
    //    final JFontCheckBoxMenuItem scoreLeadPerspectiveBlack =
    //        new JFontCheckBoxMenuItem(
    //            resourceBundle.getString("Menu.scoreLeadPerspectiveBlack")); // 永远为黑视角
    //    kataScoreLeadPerspective.add(scoreLeadPerspectiveBlack);
    //    scoreLeadPerspectiveBlack.addActionListener(
    //        new ActionListener() {
    //          @Override
    //          public void actionPerformed(ActionEvent e) {
    //            Lizzie.config.kataGoScoreMeanAlwaysBlack = true;
    //            Lizzie.config.uiConfig.put(
    //                "katago-scoremean-alwaysblack", Lizzie.config.kataGoScoreMeanAlwaysBlack);
    //          }
    //        });
    //
    //    final JFontCheckBoxMenuItem scoreLeadPerspectiveAlternately =
    //        new JFontCheckBoxMenuItem(
    //            resourceBundle.getString("Menu.scoreLeadPerspectiveAlternately")); // 黑白交替视角
    //    kataScoreLeadPerspective.add(scoreLeadPerspectiveAlternately);
    //    scoreLeadPerspectiveAlternately.addActionListener(
    //        new ActionListener() {
    //          @Override
    //          public void actionPerformed(ActionEvent e) {
    //            Lizzie.config.kataGoScoreMeanAlwaysBlack = false;
    //            Lizzie.config.uiConfig.put(
    //                "katago-scoremean-alwaysblack", Lizzie.config.kataGoScoreMeanAlwaysBlack);
    //          }
    //        });

    final JFontMenu showScoreLeadOnWinrateGraph =
        new JFontMenu(resourceBundle.getString("Menu.showScoreLeadOnWinrateGraph")); // 目差在胜率图上显示
    kataSettings.add(showScoreLeadOnWinrateGraph);

    final JFontCheckBoxMenuItem scoreLeadOnGraphWithKomi =
        new JFontCheckBoxMenuItem(resourceBundle.getString("Menu.leadWithKomi")); // 目差
    showScoreLeadOnWinrateGraph.add(scoreLeadOnGraphWithKomi);
    scoreLeadOnGraphWithKomi.addActionListener(
        new ActionListener() {

          @Override
          public void actionPerformed(ActionEvent e) {
            Lizzie.config.scoreMeanWinrateGraphBoard = false;
            Lizzie.config.uiConfig.put(
                "scoremean-winrategraph-board", Lizzie.config.scoreMeanWinrateGraphBoard);
          }
        });

    final JFontCheckBoxMenuItem scoreLeadOnGraphWithoutKomi =
        new JFontCheckBoxMenuItem(resourceBundle.getString("Menu.leadWithoutKomi")); // 盘面
    showScoreLeadOnWinrateGraph.add(scoreLeadOnGraphWithoutKomi);
    scoreLeadOnGraphWithoutKomi.addActionListener(
        new ActionListener() {

          @Override
          public void actionPerformed(ActionEvent e) {
            Lizzie.config.scoreMeanWinrateGraphBoard = true;
            Lizzie.config.uiConfig.put(
                "scoremean-winrategraph-board", Lizzie.config.scoreMeanWinrateGraphBoard);
          }
        });

    final JFontMenu kataEstimate =
        new JFontMenu(resourceBundle.getString("Menu.kataEstimate")); // ("Kata评估显示");
    kataSettings.add(kataEstimate);

    final JFontCheckBoxMenuItem kataEstimateClose =
        new JFontCheckBoxMenuItem(resourceBundle.getString("Menu.kataEstimateClose")); // ("关闭");
    kataEstimate.add(kataEstimateClose);
    kataEstimateClose.addActionListener(
        new ActionListener() {

          @Override
          public void actionPerformed(ActionEvent e) {
            Lizzie.config.isHiddenKataEstimate = false;
            Lizzie.config.showKataGoEstimate = false;
            Lizzie.config.uiConfig.put("show-katago-estimate", Lizzie.config.showKataGoEstimate);
            Lizzie.frame.clearKataEstimate();
            Lizzie.leelaz.ponder();
          }
        });

    final JFontCheckBoxMenuItem kataEstimateCloseView =
        new JFontCheckBoxMenuItem(
            resourceBundle.getString("Menu.kataEstimateCloseView")); // ("隐藏");
    kataEstimate.add(kataEstimateCloseView);
    kataEstimateCloseView.addActionListener(
        new ActionListener() {

          @Override
          public void actionPerformed(ActionEvent e) {
            Lizzie.config.isHiddenKataEstimate = true;
            Lizzie.config.showKataGoEstimate = true;
            Lizzie.config.showKataGoEstimateOnMainbord = false;
            Lizzie.config.showKataGoEstimateOnSubbord = false;
            if (Lizzie.config.saveKataEstimateStatus) {
              Lizzie.config.uiConfig.put("show-katago-estimate", Lizzie.config.showKataGoEstimate);
              Lizzie.config.uiConfig.put(
                  "show-katago-estimate-onsubbord", Lizzie.config.showKataGoEstimateOnSubbord);
              Lizzie.config.uiConfig.put(
                  "show-katago-estimate-onmainboard", Lizzie.config.showKataGoEstimateOnMainbord);
              Lizzie.frame.clearKataEstimate();
            }
            Lizzie.leelaz.ponder();
          }
        });

    final JFontCheckBoxMenuItem kataEstimateOnMainBoard =
        new JFontCheckBoxMenuItem(
            resourceBundle.getString("Menu.kataEstimateOnMainBoard")); // ("显示在大棋盘上");
    kataEstimate.add(kataEstimateOnMainBoard);
    kataEstimateOnMainBoard.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            Lizzie.config.isHiddenKataEstimate = false;
            Lizzie.config.showKataGoEstimate = true;
            Lizzie.config.showKataGoEstimateOnMainbord = true;
            Lizzie.config.showKataGoEstimateOnSubbord = false;
            if (Lizzie.config.showSubBoard) LizzieFrame.subBoardRenderer.removecountblock();
            Lizzie.leelaz.ponder();
            if (Lizzie.config.saveKataEstimateStatus) {
              Lizzie.config.uiConfig.put("show-katago-estimate", Lizzie.config.showKataGoEstimate);
              Lizzie.config.uiConfig.put(
                  "show-katago-estimate-onsubbord", Lizzie.config.showKataGoEstimateOnSubbord);
              Lizzie.config.uiConfig.put(
                  "show-katago-estimate-onmainboard", Lizzie.config.showKataGoEstimateOnMainbord);
            }
          }
        });

    final JFontCheckBoxMenuItem kataEstimateOnSubBoard =
        new JFontCheckBoxMenuItem(
            resourceBundle.getString("Menu.kataEstimateOnSubBoard")); // ("显示在小棋盘上");
    kataEstimate.add(kataEstimateOnSubBoard);
    kataEstimateOnSubBoard.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            Lizzie.config.isHiddenKataEstimate = false;
            Lizzie.config.showKataGoEstimate = true;
            Lizzie.config.showKataGoEstimateOnMainbord = false;
            Lizzie.config.showKataGoEstimateOnSubbord = true;
            LizzieFrame.boardRenderer.removecountblock();
            if (Lizzie.frame.floatBoard != null)
              Lizzie.frame.floatBoard.boardRenderer.removecountblock();
            Lizzie.leelaz.ponder();
            if (Lizzie.config.saveKataEstimateStatus) {
              Lizzie.config.uiConfig.put("show-katago-estimate", Lizzie.config.showKataGoEstimate);
              Lizzie.config.uiConfig.put(
                  "show-katago-estimate-onsubbord", Lizzie.config.showKataGoEstimateOnSubbord);
              Lizzie.config.uiConfig.put(
                  "show-katago-estimate-onmainboard", Lizzie.config.showKataGoEstimateOnMainbord);
            }
          }
        });

    final JFontCheckBoxMenuItem kataEstimateOnBothBoard =
        new JFontCheckBoxMenuItem(
            resourceBundle.getString("Menu.kataEstimateOnBothBoard")); // ("显示在大小棋盘上");
    kataEstimate.add(kataEstimateOnBothBoard);
    kataEstimateOnBothBoard.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            Lizzie.config.isHiddenKataEstimate = false;
            Lizzie.config.showKataGoEstimate = true;
            Lizzie.config.showKataGoEstimateOnMainbord = true;
            Lizzie.config.showKataGoEstimateOnSubbord = true;
            Lizzie.leelaz.ponder();
            if (Lizzie.config.saveKataEstimateStatus) {
              Lizzie.config.uiConfig.put("show-katago-estimate", Lizzie.config.showKataGoEstimate);
              Lizzie.config.uiConfig.put(
                  "show-katago-estimate-onsubbord", Lizzie.config.showKataGoEstimateOnSubbord);
              Lizzie.config.uiConfig.put(
                  "show-katago-estimate-onmainboard", Lizzie.config.showKataGoEstimateOnMainbord);
            }
          }
        });

    final JFontCheckBoxMenuItem kataEstimateSaveState =
        new JFontCheckBoxMenuItem(
            resourceBundle.getString("Menu.kataEstimateSaveState")); // ("记忆Kata评估显示状态");
    kataEstimate.add(kataEstimateSaveState);
    kataEstimateSaveState.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            Lizzie.config.saveKataEstimateStatus = !Lizzie.config.saveKataEstimateStatus;
            Lizzie.config.uiConfig.put(
                "save-kata-estimate-status", Lizzie.config.saveKataEstimateStatus);
            if (!Lizzie.config.saveKataEstimateStatus) {
              Lizzie.config.uiConfig.put("show-katago-estimate", false);
              Lizzie.config.uiConfig.put("show-katago-estimate-onsubbord", false);
              Lizzie.config.uiConfig.put("show-katago-estimate-onmainboard", false);
            } else {
              Lizzie.config.uiConfig.put("show-katago-estimate", Lizzie.config.showKataGoEstimate);
              Lizzie.config.uiConfig.put(
                  "show-katago-estimate-onsubbord", Lizzie.config.showKataGoEstimateOnSubbord);
              Lizzie.config.uiConfig.put(
                  "show-katago-estimate-onmainboard", Lizzie.config.showKataGoEstimateOnMainbord);
            }
          }
        });
    kataEstimate.addSeparator();

    final JFontCheckBoxMenuItem kataEstimateByTransparent =
        new JFontCheckBoxMenuItem(
            resourceBundle.getString(
                "Menu.kataEstimateByTransparent")); // ("以方块透明度表示占有率");//Display occupancy by
    // transparent
    kataEstimate.add(kataEstimateByTransparent);
    kataEstimateByTransparent.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            Lizzie.config.showKataGoEstimateNormal = true;
            Lizzie.config.showKataGoEstimateBySize = false;
            Lizzie.config.showKataGoEstimateBigBelow = false;
            if (Lizzie.leelaz.isKatago && !Lizzie.config.showKataGoEstimate)
              Lizzie.frame.toggleShowKataEstimate();
            Lizzie.config.saveKataEstimateConfigs();
          }
        });

    final JFontCheckBoxMenuItem kataEstimateByBigSquare =
        new JFontCheckBoxMenuItem(
            resourceBundle.getString(
                "Menu.kataEstimateByBigSquare")); // ("以大面积方块表示占有率");//Display occupancy by big
    // square under stone
    kataEstimate.add(kataEstimateByBigSquare);
    kataEstimateByBigSquare.addActionListener(
        new ActionListener() {

          @Override
          public void actionPerformed(ActionEvent e) {
            Lizzie.config.showKataGoEstimateNormal = false;
            Lizzie.config.showKataGoEstimateBySize = false;
            Lizzie.config.showKataGoEstimateBigBelow = true;
            if (Lizzie.leelaz.isKatago && !Lizzie.config.showKataGoEstimate)
              Lizzie.frame.toggleShowKataEstimate();
            Lizzie.config.saveKataEstimateConfigs();
          }
        });

    final JFontCheckBoxMenuItem kataEstimateBySize =
        new JFontCheckBoxMenuItem(
            resourceBundle.getString(
                "Menu.kataEstimateBySize")); // ("以方块大小表示占有率");//Display occupancy by square size
    kataEstimate.add(kataEstimateBySize);
    kataEstimateBySize.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            Lizzie.config.showKataGoEstimateNormal = false;
            Lizzie.config.showKataGoEstimateBySize = true;
            Lizzie.config.showKataGoEstimateBigBelow = false;
            if (Lizzie.leelaz.isKatago && !Lizzie.config.showKataGoEstimate)
              Lizzie.frame.toggleShowKataEstimate();
            Lizzie.config.saveKataEstimateConfigs();
          }
        });

    final JFontMenu kataEstimateInPureNet =
        new JFontMenu(resourceBundle.getString("Menu.kataEstimateInPureNet"));
    kataEstimate.add(kataEstimateInPureNet);

    final JFontCheckBoxMenuItem pureEstimateByTransparent =
        new JFontCheckBoxMenuItem(
            resourceBundle.getString(
                "Menu.kataEstimateByTransparent")); // ("以方块透明度表示占有率");//Display occupancy by
    // transparent
    kataEstimateInPureNet.add(pureEstimateByTransparent);
    pureEstimateByTransparent.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            Lizzie.config.showPureEstimateNormal = true;
            Lizzie.config.showPureEstimateBySize = false;
            Lizzie.config.showPureEstimateBigBelow = false;
            if (Lizzie.leelaz.isheatmap) Lizzie.leelaz.ponder();
            Lizzie.config.saveKataEstimateConfigs();
          }
        });

    final JFontCheckBoxMenuItem pureEstimateByBigSquare =
        new JFontCheckBoxMenuItem(
            resourceBundle.getString(
                "Menu.kataEstimateByBigSquare")); // ("以大面积方块表示占有率");//Display occupancy by big
    // square under stone
    kataEstimateInPureNet.add(pureEstimateByBigSquare);
    pureEstimateByBigSquare.addActionListener(
        new ActionListener() {

          @Override
          public void actionPerformed(ActionEvent e) {
            Lizzie.config.showPureEstimateNormal = false;
            Lizzie.config.showPureEstimateBySize = false;
            Lizzie.config.showPureEstimateBigBelow = true;
            if (Lizzie.leelaz.isheatmap) Lizzie.leelaz.ponder();
            Lizzie.config.saveKataEstimateConfigs();
          }
        });

    final JFontCheckBoxMenuItem pureEstimateBySize =
        new JFontCheckBoxMenuItem(
            resourceBundle.getString(
                "Menu.kataEstimateBySize")); // ("以方块大小表示占有率");//Display occupancy by square size
    kataEstimateInPureNet.add(pureEstimateBySize);
    pureEstimateBySize.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            Lizzie.config.showPureEstimateNormal = false;
            Lizzie.config.showPureEstimateBySize = true;
            Lizzie.config.showPureEstimateBigBelow = false;
            if (Lizzie.leelaz.isheatmap) Lizzie.leelaz.ponder();
            Lizzie.config.saveKataEstimateConfigs();
          }
        });

    kataEstimate.addSeparator();

    final JFontCheckBoxMenuItem useKataEstimateShortcut =
        new JFontCheckBoxMenuItem(
            resourceBundle.getString("Menu.useKataEstimateShortcut")); // ("使用Kata评估快捷键(大键盘点)");
    kataEstimate.add(useKataEstimateShortcut);
    useKataEstimateShortcut.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            Lizzie.config.useShortcutKataEstimate = !Lizzie.config.useShortcutKataEstimate;

            Lizzie.config.uiConfig.put(
                "shortcut-kata-estimate", Lizzie.config.useShortcutKataEstimate);
          }
        });

    viewMenu.addMenuListener(
        new MenuListener() {

          public void menuSelected(MenuEvent e) {
            if (Lizzie.config.ignoreOutOfWidth) ignoreOutOfWidth.setState(true);
            else ignoreOutOfWidth.setState(false);
            if (Lizzie.config.showScrollVariation) {
              ignoreOutOfWidth.setEnabled(true);
              showScrollVariation.setState(true);
              maxTreeWidth.setEnabled(true);
            } else {
              ignoreOutOfWidth.setEnabled(false);
              showScrollVariation.setState(false);
              maxTreeWidth.setEnabled(false);
            }
            if (Lizzie.config.showNextMoves) {
              nextMoveHintNone.setState(false);
              if (Lizzie.config.showNextMoveBlunder) {
                nextMoveHintSimple.setState(false);
                nextMoveHintInformation.setState(true);
              } else {
                nextMoveHintSimple.setState(true);
                nextMoveHintInformation.setState(false);
              }
            } else {
              nextMoveHintNone.setState(true);
              nextMoveHintSimple.setState(false);
              nextMoveHintInformation.setState(false);
            }
            if (Lizzie.config.useShortcutKataEstimate) useKataEstimateShortcut.setSelected(true);
            else useKataEstimateShortcut.setState(false);
            if (Lizzie.config.saveKataEstimateStatus) kataEstimateSaveState.setSelected(true);
            else kataEstimateSaveState.setState(false);
            if (Lizzie.config.isFourSubMode()) extraMode1.setState(true);
            else extraMode1.setState(false);
            if (Lizzie.config.isDoubleEngineMode()) extraMode2.setState(true);
            else extraMode2.setState(false);
            if (Lizzie.config.isThinkingMode()) extraMode3.setState(true);
            else extraMode3.setState(false);
            if (Lizzie.config.showWinrateInSuggestion) suggestion1.setState(true);
            else suggestion1.setState(false);
            if (Lizzie.config.showPlayoutsInSuggestion) suggestion2.setState(true);
            else suggestion2.setState(false);
            if (Lizzie.config.showScoremeanInSuggestion) suggestion3.setState(true);
            else suggestion3.setState(false);
            if (Lizzie.config.showKataGoBoardScoreMean) {
              leadWithKomi.setState(false);
              leadWithoutKomi.setState(true);
            } else {
              leadWithKomi.setState(true);
              leadWithoutKomi.setState(false);
            }

            //            if (Lizzie.config.kataGoScoreMeanAlwaysBlack) {
            //              scoreLeadPerspectiveBlack.setState(true);
            //              scoreLeadPerspectiveAlternately.setState(false);
            //            } else {
            //              scoreLeadPerspectiveBlack.setState(false);
            //              scoreLeadPerspectiveAlternately.setState(true);
            //            }
            if (Lizzie.config.showKataGoEstimate) {
              kataEstimateClose.setState(false);
              if (Lizzie.config.showKataGoEstimateOnMainbord
                  && Lizzie.config.showKataGoEstimateOnSubbord) {
                kataEstimateOnBothBoard.setState(true);
                kataEstimateOnMainBoard.setState(false);
                kataEstimateOnSubBoard.setState(false);
                kataEstimateCloseView.setState(false);
              } else if (Lizzie.config.showKataGoEstimateOnMainbord) {
                kataEstimateOnMainBoard.setState(true);
                kataEstimateOnBothBoard.setState(false);
                kataEstimateOnSubBoard.setState(false);
                kataEstimateCloseView.setState(false);
              } else if (Lizzie.config.showKataGoEstimateOnSubbord) {
                kataEstimateOnSubBoard.setState(true);
                kataEstimateOnMainBoard.setState(false);
                kataEstimateOnBothBoard.setState(false);
                kataEstimateCloseView.setState(false);
              } else {
                kataEstimateOnSubBoard.setState(false);
                kataEstimateOnMainBoard.setState(false);
                kataEstimateOnBothBoard.setState(false);
                kataEstimateCloseView.setState(true);
              }
            } else {
              kataEstimateCloseView.setState(false);
              kataEstimateClose.setState(true);
              kataEstimateOnMainBoard.setState(false);
              kataEstimateOnSubBoard.setState(false);
              kataEstimateOnBothBoard.setState(false);
            }

            if (Lizzie.config.showKataGoEstimateBySize) kataEstimateBySize.setState(true);
            else kataEstimateBySize.setState(false);
            if (Lizzie.config.showKataGoEstimateBigBelow) kataEstimateByBigSquare.setState(true);
            else kataEstimateByBigSquare.setState(false);
            if (Lizzie.config.showKataGoEstimateNormal) kataEstimateByTransparent.setState(true);
            else kataEstimateByTransparent.setState(false);

            if (Lizzie.config.showPureEstimateBySize) pureEstimateBySize.setState(true);
            else pureEstimateBySize.setState(false);
            if (Lizzie.config.showPureEstimateBigBelow) pureEstimateByBigSquare.setState(true);
            else pureEstimateByBigSquare.setState(false);
            if (Lizzie.config.showPureEstimateNormal) pureEstimateByTransparent.setState(true);
            else pureEstimateByTransparent.setState(false);
            if (Lizzie.config.winrateAlwaysBlack) alwaysShowBlackWinrate.setState(true);
            else alwaysShowBlackWinrate.setState(false);
            if (Lizzie.config.showSuggestionVariations) showVariationOnMouse.setState(true);
            else showVariationOnMouse.setState(false);
            if (Lizzie.config.noRefreshOnMouseMove) noRefreshOnMouse.setState(true);
            else noRefreshOnMouse.setState(false);
            if (LizzieFrame.winrateGraph.mode == 1) {
              winrateMode1.setState(true);
              winrateMode0.setState(false);
            } else {
              winrateMode1.setState(false);
              winrateMode0.setState(true);
            }
            if (Lizzie.config.showBlunderBar) showBlunderBar.setState(true);
            else showBlunderBar.setState(false);
            if (Lizzie.config.showScoreLeadLine) showScoreLeadLine.setState(true);
            else showScoreLeadLine.setState(false);
            if (Lizzie.config.showMouseOverWinrateGraph) showMouseOverWinrateGraph.setState(true);
            else showMouseOverWinrateGraph.setState(false);
            if (Lizzie.config.showWinrateGraph && Lizzie.config.showLargeWinrate())
              largeWinrateGraph.setState(true);
            else largeWinrateGraph.setState(false);
            if (Lizzie.config.showSubBoard && Lizzie.config.showLargeSubBoard())
              largeSubBoard.setState(true);
            else largeSubBoard.setState(false);
            if (Lizzie.config.appendWinrateToComment) appendWinrateToComment.setState(true);
            else appendWinrateToComment.setState(false);
            if (Lizzie.config.uiConfig.optBoolean("mains-always-ontop", false))
              alwaysOnTop.setState(true);
            else alwaysOnTop.setState(false);
            if (!Lizzie.config.hideBlunderControlPane) showCommentConrolPane.setState(true);
            else showCommentConrolPane.setState(false);
            if (Lizzie.config.showSubBoard) subboard.setState(true);
            else subboard.setState(false);
            if (Lizzie.config.showWinrateGraph) winrateGraph.setState(true);
            else winrateGraph.setState(false);
            if (Lizzie.config.showComment) commitPane.setState(true);
            else commitPane.setState(false);
            if (Lizzie.config.showVariationGraph) variationPane.setState(true);
            else variationPane.setState(false);
            if (Lizzie.config.showCaptured) informationPane.setState(true);
            else informationPane.setState(false);
            if (Lizzie.config.showListPane()) listPane.setState(true);
            else listPane.setState(false);
            if (Lizzie.config.showStatus) statusPanel.setState(true);
            else statusPanel.setState(false);
            if (Lizzie.gtpConsole.isVisible()) gtpPanel.setState(true);
            else gtpPanel.setState(false);
            if (Lizzie.config.uiConfig.optBoolean("show-suggestions-frame", false))
              SuggestionList.setState(true);
            else SuggestionList.setState(false);
            if (Lizzie.config.uiConfig.optBoolean("show-badmoves-frame", false))
              hawkEye.setState(true);
            else hawkEye.setState(false);
            if (Lizzie.config.isShowingIndependentMain) {
              if (Lizzie.config.isFloatBoardMode()) {
                independentMainBoard.setSelected(true);
                independentMainBoard2.setSelected(false);
              } else {
                independentMainBoard2.setSelected(true);
                independentMainBoard.setSelected(false);
              }
            } else {
              independentMainBoard.setSelected(false);
              independentMainBoard2.setSelected(false);
            }
            if (Lizzie.config.isShowingIndependentSub) independentSubBoard.setSelected(true);
            else independentSubBoard.setSelected(false);
            if (Lizzie.config.showCoordinates) coordsMenu.setState(true);
            else coordsMenu.setState(false);
            switch (Lizzie.config.allowMoveNumber) {
              case 0:
                noMoveNum.setState(true);
                lastOneMoveNum.setState(false);
                lastFiveMoveNum.setState(false);
                lastTenMoveNum.setState(false);
                allMoveNum.setState(false);
                anyMoveNum.setState(false);
                break;
              case 1:
                noMoveNum.setState(false);
                lastOneMoveNum.setState(true);
                lastFiveMoveNum.setState(false);
                lastTenMoveNum.setState(false);
                allMoveNum.setState(false);
                anyMoveNum.setState(false);
                break;
              case 5:
                noMoveNum.setState(false);
                lastOneMoveNum.setState(false);
                lastFiveMoveNum.setState(true);
                lastTenMoveNum.setState(false);
                allMoveNum.setState(false);
                anyMoveNum.setState(false);
                break;
              case 10:
                noMoveNum.setState(false);
                lastOneMoveNum.setState(false);
                lastFiveMoveNum.setState(false);
                lastTenMoveNum.setState(true);
                allMoveNum.setState(false);
                anyMoveNum.setState(false);
                break;
              case -1:
                noMoveNum.setState(false);
                lastOneMoveNum.setState(false);
                lastFiveMoveNum.setState(false);
                lastTenMoveNum.setState(false);
                allMoveNum.setState(true);
                anyMoveNum.setState(false);
                break;
              default:
                noMoveNum.setState(false);
                lastOneMoveNum.setState(false);
                lastFiveMoveNum.setState(false);
                lastTenMoveNum.setState(false);
                allMoveNum.setState(false);
                anyMoveNum.setState(true);
            }
            if (Lizzie.config.showMoveNumberFromOne) moveNumberAlwaysFromOne.setState(true);
            else moveNumberAlwaysFromOne.setState(false);

            if (Lizzie.config.showVarMove) showMoveNumberOnVariationPane.setState(true);
            else showMoveNumberOnVariationPane.setState(false);
            if (Lizzie.config.newMoveNumberInBranch) {
              moveNumberInBracnhFromOne.setState(true);
              moveNumberInBracnhFromOneContinue.setState(false);
            } else {
              moveNumberInBracnhFromOne.setState(false);
              moveNumberInBracnhFromOneContinue.setState(true);
            }

            if (Lizzie.config.showMoveAllInBranch) showAllMoveNumberInBranch.setState(true);
            else showAllMoveNumberInBranch.setState(false);
            if (Lizzie.config.showSuggestionOrder) showSuggestionOrder.setState(true);
            else showSuggestionOrder.setState(false);
            if (Lizzie.config.showSuggestionMaxRed) showMaxValueReverse.setState(true);
            else showMaxValueReverse.setState(false);
            if (Lizzie.config.whiteSuggestionWhite) showWhiteSuggestWhite.setState(true);
            else showWhiteSuggestWhite.setState(false);
            if (Lizzie.config.scoreMeanWinrateGraphBoard) {
              scoreLeadOnGraphWithKomi.setState(false);
              scoreLeadOnGraphWithoutKomi.setState(true);
            } else {
              scoreLeadOnGraphWithKomi.setState(true);
              scoreLeadOnGraphWithoutKomi.setState(false);
            }

            if (Lizzie.config.showHeat) {
              if (Lizzie.config.showHeatAfterCalc) {
                showHeatAfterCalc.setState(true);
                showHeat.setState(false);
                notShowHeat.setState(false);
              } else {
                showHeat.setState(true);
                showHeatAfterCalc.setState(false);
                notShowHeat.setState(false);
              }
              subBoardShowVar.setSelected(false);
              subBoardShowRaw.setSelected(false);
            } else {
              notShowHeat.setState(true);
              showHeat.setState(false);
              showHeatAfterCalc.setState(false);
              if (Lizzie.config.subBoardRaw) {
                subBoardShowRaw.setSelected(true);
                subBoardShowVar.setSelected(false);
              } else {
                subBoardShowVar.setSelected(true);
                subBoardShowRaw.setSelected(false);
              }
            }
            if (Lizzie.config.showNameInBoard) showNameInBoard.setState(true);
            else showNameInBoard.setState(false);
          }

          @Override
          public void menuDeselected(MenuEvent e) {
            // TODO Auto-generated method stub
          }

          @Override
          public void menuCanceled(MenuEvent e) {
            // TODO Auto-generated method stub

          }
        });

    final JFontMenu gameMenu = new JFontMenu(resourceBundle.getString("Menu.game"));
    gameMenu.setForeground(Color.BLACK);
    //  gameMenu.setFont(headFont);
    this.add(gameMenu);

    final JFontMenu newGame = new JFontMenu(resourceBundle.getString("Menu.newGame")); // ("新对局");
    gameMenu.add(newGame);

    final JFontMenu continueGameAgainstAi =
        new JFontMenu(resourceBundle.getString("Menu.continueGameAgainstAi")); // ("人机续弈");
    gameMenu.add(continueGameAgainstAi);

    final JFontMenuItem newGenmoveGame =
        new JFontMenuItem(
            resourceBundle.getString("Menu.newGenmoveGame")); // ("人机对局(Genmove模式 Alt+N)");
    newGenmoveGame.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            Lizzie.frame.startNewGame();
          }
        });
    newGame.add(newGenmoveGame);

    JFontMenuItem newAnalyzeModeGame = new JFontMenuItem(); // ("人机对局(分析模式 N)");

    newAnalyzeModeGame.setLayout(null);
    if (Lizzie.config.isChinese)
      newAnalyzeModeGame.setPreferredSize(
          new Dimension(
              (Lizzie.config.useJavaLooks ? -31 : 0)
                  + (Lizzie.config.isFrameFontSmall()
                      ? 200
                      : (Lizzie.config.isFrameFontMiddle() ? 250 : 300)),
              (Lizzie.config.useJavaLooks
                  ? (Lizzie.config.isFrameFontSmall()
                      ? 20
                      : (Lizzie.config.isFrameFontMiddle() ? 25 : 30))
                  : (Lizzie.config.isFrameFontSmall()
                      ? 25
                      : (Lizzie.config.isFrameFontMiddle() ? 27 : 30)))));
    else
      newAnalyzeModeGame.setPreferredSize(
          new Dimension(
              (Lizzie.config.useJavaLooks ? -31 : 0)
                  + (Lizzie.config.isFrameFontSmall()
                      ? 253
                      : (Lizzie.config.isFrameFontMiddle() ? 310 : 380)),
              (Lizzie.config.useJavaLooks
                  ? (Lizzie.config.isFrameFontSmall()
                      ? 20
                      : (Lizzie.config.isFrameFontMiddle() ? 25 : 30))
                  : (Lizzie.config.isFrameFontSmall()
                      ? 25
                      : (Lizzie.config.isFrameFontMiddle() ? 27 : 30)))));
    JFontLabel lblAnalyzeGame = new JFontLabel(resourceBundle.getString("Menu.newAnalyzeModeGame"));
    lblAnalyzeGame.setBounds(
        Lizzie.config.useJavaLooks ? 6 : 37,
        (Lizzie.config.useJavaLooks
            ? -1
            : (Lizzie.config.isFrameFontSmall()
                ? 2
                : (Lizzie.config.isFrameFontMiddle() ? 1 : -1))),
        320,
        Lizzie.config.isFrameFontSmall() ? 20 : (Lizzie.config.isFrameFontMiddle() ? 25 : 30));
    JButton aboutAnalyzeGame = new JFontButton("?");
    aboutAnalyzeGame.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            Lizzie.frame.showAnalyzeGenmoveInfo();
          }
        });
    aboutAnalyzeGame.setFocusable(false);
    aboutAnalyzeGame.setMargin(new Insets(0, 0, 0, 0));
    if (Lizzie.config.isChinese)
      aboutAnalyzeGame.setBounds(
          (Lizzie.config.useJavaLooks ? -31 : 0)
              + (Lizzie.config.isFrameFontSmall()
                  ? 177
                  : (Lizzie.config.isFrameFontMiddle() ? 220 : 270)),
          (Lizzie.config.useJavaLooks
              ? 1
              : (Lizzie.config.isFrameFontSmall()
                  ? 3
                  : (Lizzie.config.isFrameFontMiddle() ? 2 : 1))),
          Config.menuHeight - 2,
          Config.menuHeight - 2);
    else
      aboutAnalyzeGame.setBounds(
          (Lizzie.config.useJavaLooks ? -31 : 0)
              + (Lizzie.config.isFrameFontSmall()
                  ? 230
                  : (Lizzie.config.isFrameFontMiddle() ? 280 : 350)),
          (Lizzie.config.useJavaLooks
              ? 1
              : (Lizzie.config.isFrameFontSmall()
                  ? 3
                  : (Lizzie.config.isFrameFontMiddle() ? 2 : 1))),
          Config.menuHeight - 2,
          Config.menuHeight - 2);
    newAnalyzeModeGame.add(aboutAnalyzeGame);
    newAnalyzeModeGame.add(lblAnalyzeGame);
    newAnalyzeModeGame.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            Lizzie.frame.startAnalyzeGameDialog();
          }
        });
    newGame.add(newAnalyzeModeGame);

    final JFontMenuItem newEngineGame =
        new JFontMenuItem(resourceBundle.getString("Menu.newEngineGame")); // ("引擎对局(Alt+E)");
    newEngineGame.addActionListener(
        new ActionListener() {

          @Override
          public void actionPerformed(ActionEvent e) {
            Lizzie.frame.startEngineGameDialog();
          }
        });
    newGame.add(newEngineGame);

    final JFontMenuItem continueGenmoveGameAsWhite =
        new JFontMenuItem(
            resourceBundle.getString(
                "Menu.continueGenmoveGameAsWhite")); // ("续弈[AI执黑](Genmove模式 Alt+回车)");
    continueGenmoveGameAsWhite.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            Lizzie.frame.continueAiPlaying(true, false, false, false);
          }
        });
    continueGameAgainstAi.add(continueGenmoveGameAsWhite);

    final JFontMenuItem continueGenmoveGameAsBlack =
        new JFontMenuItem(
            resourceBundle.getString(
                "Menu.continueGenmoveGameAsBlack")); // ("续弈[AI执白](Genmove模式 Alt+回车)");
    continueGenmoveGameAsBlack.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            Lizzie.frame.continueAiPlaying(true, false, true, false);
          }
        });
    continueGameAgainstAi.add(continueGenmoveGameAsBlack);

    final JFontMenuItem continueAnalyzeGameAsWhite =
        new JFontMenuItem(
            resourceBundle.getString("Menu.continueAnalyzeGameAsWhite")); // ("续弈[AI执黑](分析模式 回车)");

    continueAnalyzeGameAsWhite.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            if (Lizzie.leelaz.noAnalyze) Lizzie.frame.continueAiPlaying(true, false, false, false);
            else Lizzie.frame.continueAiPlaying(false, false, false, false);
          }
        });

    continueGameAgainstAi.add(continueAnalyzeGameAsWhite);

    final JFontMenuItem continueAnalyzeGameAsBlack = new JFontMenuItem();
    continueAnalyzeGameAsBlack.setText(
        resourceBundle.getString("Menu.continueAnalyzeGameAsBlack")); // ("续弈[AI执白](分析模式 回车)");

    continueAnalyzeGameAsBlack.addActionListener(
        new ActionListener() {

          @Override
          public void actionPerformed(ActionEvent e) {
            if (Lizzie.leelaz.noAnalyze) Lizzie.frame.continueAiPlaying(true, false, true, false);
            else Lizzie.frame.continueAiPlaying(false, false, true, false);
          }
        });

    continueGameAgainstAi.add(continueAnalyzeGameAsBlack);

    final JFontCheckBoxMenuItem scoreGame =
        new JFontCheckBoxMenuItem(resourceBundle.getString("Menu.scoreGame")); // ("终局数子");
    gameMenu.add(scoreGame);
    scoreGame.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            Lizzie.frame.toggleScoreMode();
          }
        });
    gameMenu.addSeparator();

    final JFontMenuItem setAiTime =
        new JFontMenuItem(resourceBundle.getString("Menu.setAiTime")); // ("修改AI用时");
    setAiTime.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            SetAiTimes st = new SetAiTimes(Lizzie.frame);
            st.setVisible(true);
          }
        });
    gameMenu.add(setAiTime);

    gameMenu.addSeparator();
    final JFontMenuItem breakGame =
        new JFontMenuItem(resourceBundle.getString("Menu.breakGame")); // ("终止人机对局(空格)");
    breakGame.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            Lizzie.frame.togglePonderMannul();
          }
        });
    gameMenu.add(breakGame);

    final JFontMenuItem breakEngineGame =
        new JFontMenuItem(resourceBundle.getString("Menu.breakEngineGame")); // ("终止引擎对局(ALT+R)");

    breakEngineGame.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            Lizzie.engineManager.stopEngineGame(-1, true);
          }
        });
    gameMenu.add(breakEngineGame);

    final JFontMenuItem pauseEngineGame =
        new JFontMenuItem(
            resourceBundle.getString("Menu.pauseEngineGame")); // ("暂停/继续引擎对局(ALT+T)");
    pauseEngineGame.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            LizzieFrame.toolbar.btnEnginePkStop.doClick();
          }
        });
    gameMenu.add(pauseEngineGame);

    final JFontMenuItem changeEngineGameNumbers =
        new JFontMenuItem(resourceBundle.getString("Menu.changeEngineGameNumbers"));
    changeEngineGameNumbers.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            SwingUtilities.invokeLater(
                new Runnable() {
                  public void run() {
                    String result =
                        JOptionPane.showInputDialog(
                            Lizzie.frame,
                            resourceBundle.getString("Menu.changeEngineGameNumbersMessage"),
                            resourceBundle.getString("Menu.changeEngineGameNumbersTitle"),
                            JOptionPane.INFORMATION_MESSAGE);
                    if (result != null)
                      try {
                        int numbers = Integer.parseInt(result);
                        EngineManager.engineGameInfo.batchNumber = numbers;
                      } catch (NumberFormatException ex) {
                        Utils.showMsg(resourceBundle.getString("Menu.inputIntegerHint"));
                        return;
                      }
                    LizzieFrame.toolbar.txtenginePkBatch.setText(
                        String.valueOf(EngineManager.engineGameInfo.batchNumber));
                  }
                });
          }
        });
    gameMenu.add(changeEngineGameNumbers);

    final JFontMenuItem intervention =
        new JFontMenuItem(resourceBundle.getString("Menu.intervention"));
    intervention.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            Manual manul = new Manual();
            manul.setVisible(true);
          }
        });
    gameMenu.add(intervention);
    gameMenu.addSeparator();

    final JFontMenuItem playBestMove =
        new JFontMenuItem(resourceBundle.getString("Menu.playBestMove")); // ("让AI落子最佳一手(逗号)");
    playBestMove.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            if (!Lizzie.frame.playCurrentVariation()) Lizzie.frame.playBestMove();
          }
        });
    gameMenu.add(playBestMove);

    gameMenu.addMenuListener(
        new MenuListener() {

          public void menuSelected(MenuEvent e) {
            if (Lizzie.frame.isInScoreMode) scoreGame.setState(true);
            else scoreGame.setState(false);
          }

          @Override
          public void menuDeselected(MenuEvent e) {
            // TODO Auto-generated method stub
          }

          @Override
          public void menuCanceled(MenuEvent e) {
            // TODO Auto-generated method stub

          }
        });

    final JFontMenuItem playPassMove =
        new JFontMenuItem(resourceBundle.getString("Menu.playPassMove")); // ("停一手(P)");
    playPassMove.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            Lizzie.board.pass();
          }
        });
    gameMenu.add(playPassMove);

    final JFontMenu analyzeMenu = new JFontMenu(resourceBundle.getString("Menu.analyze"));
    analyzeMenu.setForeground(Color.BLACK);
    // analyMenu.setFont(headFont);
    this.add(analyzeMenu);

    final JFontMenuItem togglePonder =
        new JFontMenuItem(resourceBundle.getString("Menu.togglePonder")); // ("开始/停止 分析(空格)");
    // aboutItem.setMnemonic('A');
    togglePonder.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            Lizzie.frame.togglePonderMannul();
          }
        });
    analyzeMenu.add(togglePonder);

    final JFontMenuItem hawkEye2 =
        new JFontMenuItem(resourceBundle.getString("Menu.hawkEye")); // ("超级鹰眼(T)");
    hawkEye2.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            Lizzie.frame.toggleBadMoves();
          }
        });
    analyzeMenu.add(hawkEye2);
    analyzeMenu.addSeparator();

    final JFontMenuItem autoAnalyze =
        new JFontMenuItem(resourceBundle.getString("Menu.autoAnalyze")); // ("自动分析(A)");
    // aboutItem.setMnemonic('A');
    autoAnalyze.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            StartAnaDialog newgame = new StartAnaDialog(false, Lizzie.frame);
            newgame.setVisible(true);
            if (newgame.isCancelled()) {
              LizzieFrame.toolbar.resetAutoAna();
              return;
            }
          }
        });
    analyzeMenu.add(autoAnalyze);

    final JFontMenuItem batchAnalyze =
        new JFontMenuItem(resourceBundle.getString("Menu.batchAnalyze")); // ("批量分析(Ctrl+O)");
    batchAnalyze.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            Lizzie.frame.openFileWithAna(false);
          }
        });
    analyzeMenu.add(batchAnalyze);

    final JFontMenuItem batchAnalysisMode =
        new JFontMenuItem(resourceBundle.getString("Menu.batchAnalysisMode")); // ("批量分析(闪电模式)");
    batchAnalysisMode.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            Lizzie.frame.openFileWithAna(true);
          }
        });
    analyzeMenu.add(batchAnalysisMode);

    final JFontMenuItem stopAutoAnalyze =
        new JFontMenuItem(resourceBundle.getString("Menu.stopAutoAnalyze")); // ("停止自动(批量)分析");
    analyzeMenu.add(stopAutoAnalyze);
    stopAutoAnalyze.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            LizzieFrame.toolbar.stopAutoAna(true, true);
          }
        });

    final JFontMenuItem batchAnalyzeTable =
        new JFontMenuItem(resourceBundle.getString("Menu.batchAnalyzeTable")); // ("批量分析进度表");
    analyzeMenu.add(batchAnalyzeTable);
    batchAnalyzeTable.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            Lizzie.frame.openAnalysisTable();
          }
        });

    analyzeMenu.addSeparator();

    final JFontMenuItem flashAnalyzeAllGame =
        new JFontMenuItem(resourceBundle.getString("Menu.flashAnalyzeAllGame"));
    analyzeMenu.add(flashAnalyzeAllGame);
    flashAnalyzeAllGame.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            Lizzie.frame.flashAnalyzeGame(true);
          }
        });

    final JFontMenuItem flashAnalyzePartGame =
        new JFontMenuItem(resourceBundle.getString("Menu.flashAnalyzePartGame"));
    analyzeMenu.add(flashAnalyzePartGame);
    flashAnalyzePartGame.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            Lizzie.frame.flashAnalyzePart();
          }
        });
    final JFontMenuItem flashAnalyzeSettings =
        new JFontMenuItem(resourceBundle.getString("Menu.flashAnalyzeSettings")); //
    analyzeMenu.add(flashAnalyzeSettings);
    flashAnalyzeSettings.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            Lizzie.frame.flashAnalyzeSettings();
          }
        });

    analyzeMenu.addSeparator();
    final JFontMenuItem showHeatmap =
        new JFontMenuItem(resourceBundle.getString("Menu.showHeatmap")); // ("纯网络(H)");
    showHeatmap.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            Lizzie.leelaz.toggleHeatmap(false);
          }
        });
    analyzeMenu.add(showHeatmap);

    final JFontCheckBoxMenuItem showPolicy =
        new JFontCheckBoxMenuItem(resourceBundle.getString("Menu.showPolicy")); // ("策略网络(Y)");
    showPolicy.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            Lizzie.frame.togglePolicy();
          }
        });
    analyzeMenu.add(showPolicy);

    final JFontMenuItem estimateStones =
        new JFontMenuItem(resourceBundle.getString("Menu.estimateStones")); // ("形势判断( / 或小键盘点)");
    // aboutItem.setMnemonic('A');
    estimateStones.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            Lizzie.frame.countstones(true);
          }
        });
    analyzeMenu.add(estimateStones);
    analyzeMenu.addSeparator();

    final JFontMenuItem clearAllLizzieCache =
        new JFontMenuItem(
            resourceBundle.getString("Menu.clearAllLizzieCache")); // ("清除Lizzie所有分析缓存(Shift+A)");
    clearAllLizzieCache.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            Lizzie.board.clearBoardStat();
          }
        });
    analyzeMenu.add(clearAllLizzieCache);

    final JFontMenuItem clearThisLizzieCache =
        new JFontMenuItem(resourceBundle.getString("Menu.clearThisLizzieCache")); // ("清除当前分析缓存");
    clearThisLizzieCache.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            Lizzie.board.clearbestmoves();
          }
        });
    analyzeMenu.add(clearThisLizzieCache);

    final JFontMenuItem clearAllLizzieBestmoves =
        new JFontMenuItem(
            resourceBundle.getString("Menu.clearAllLizzieBestmoves")); // ("清除所有分析信息");
    clearAllLizzieBestmoves.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            Lizzie.board.clearbestmovesInfomationAfter(Lizzie.board.getHistory().getStart());
            if (Lizzie.config.isDoubleEngineMode())
              Lizzie.board.clearbestmovesInfomationAfter2(Lizzie.board.getHistory().getStart());
            Lizzie.frame.refreshCurrentMove();
          }
        });
    analyzeMenu.add(clearAllLizzieBestmoves);

    final JFontMenuItem clearThisLizzieBestmoves =
        new JFontMenuItem(
            resourceBundle.getString("Menu.clearThisLizzieBestmoves")); // ("清除当前分析信息");
    clearThisLizzieBestmoves.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            Lizzie.board.clearbestmovesInfomation(
                Lizzie.board.getHistory().getCurrentHistoryNode());
            if (Lizzie.config.isDoubleEngineMode())
              Lizzie.board.clearbestmovesInfomation2(
                  Lizzie.board.getHistory().getCurrentHistoryNode());
            Lizzie.frame.refreshCurrentMove();
          }
        });
    analyzeMenu.add(clearThisLizzieBestmoves);

    analyzeMenu.addMenuListener(
        new MenuListener() {

          public void menuSelected(MenuEvent e) {
            if (Lizzie.frame.isShowingPolicy) showPolicy.setState(true);
            else showPolicy.setState(false);
          }

          @Override
          public void menuDeselected(MenuEvent e) {
            // TODO Auto-generated method stub
          }

          @Override
          public void menuCanceled(MenuEvent e) {
            // TODO Auto-generated method stub

          }
        });

    final JFontMenu editMenu = new JFontMenu(resourceBundle.getString("Menu.edit"));
    editMenu.setForeground(Color.BLACK);
    // editMenu.setFont(headFont);
    this.add(editMenu);
    iconblack2 = new ImageIcon();
    try {
      iconblack2.setImage(
          ImageIO.read(getClass().getResourceAsStream("/assets/smallblack2.png"))
              .getScaledInstance(
                  Config.menuIconSize / 2 * 3,
                  Config.menuIconSize / 2 * 3,
                  java.awt.Image.SCALE_SMOOTH));
      // ImageIO.read(getClass().getResourceAsStream("/assets/menu.png")));
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

    iconblack = new ImageIcon();
    try {
      iconblack.setImage(
          ImageIO.read(getClass().getResourceAsStream("/assets/smallblack1.png"))
              .getScaledInstance(
                  Config.menuIconSize, Config.menuIconSize, java.awt.Image.SCALE_SMOOTH));
      // ImageIO.read(getClass().getResourceAsStream("/assets/menu.png")));
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

    iconwhite2 = new ImageIcon();
    try {
      iconwhite2.setImage(
          ImageIO.read(getClass().getResourceAsStream("/assets/smallwhite4.png"))
              .getScaledInstance(
                  Config.menuIconSize / 2 * 3,
                  Config.menuIconSize / 2 * 3,
                  java.awt.Image.SCALE_SMOOTH));
      // ImageIO.read(getClass().getResourceAsStream("/assets/smallwhite.png"));
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

    iconwhite = new ImageIcon();
    try {
      iconwhite.setImage(
          ImageIO.read(getClass().getResourceAsStream("/assets/smallwhite.png"))
              .getScaledInstance(
                  Config.menuIconSize, Config.menuIconSize, java.awt.Image.SCALE_SMOOTH));
      // ImageIO.read(getClass().getResourceAsStream("/assets/smallwhite.png"));
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

    iconbh2 = new ImageIcon();
    try {
      // iconbh.setImage(ImageIO.read(getClass().getResourceAsStream("/assets/menu.png")));
      iconbh2.setImage(
          ImageIO.read(getClass().getResourceAsStream("/assets/hb2.png"))
              .getScaledInstance(
                  Config.menuIconSize + 8, Config.menuIconSize + 8, java.awt.Image.SCALE_SMOOTH));
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

    iconbh = new ImageIcon();
    try {
      // iconbh.setImage(ImageIO.read(getClass().getResourceAsStream("/assets/menu.png")));
      iconbh.setImage(
          ImageIO.read(getClass().getResourceAsStream("/assets/hb.png"))
              .getScaledInstance(
                  Config.menuIconSize + 8, Config.menuIconSize + 8, java.awt.Image.SCALE_SMOOTH));
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

    iconAllow = new ImageIcon();
    try {
      // iconbh.setImage(ImageIO.read(getClass().getResourceAsStream("/assets/menu.png")));
      iconAllow.setImage(
          ImageIO.read(getClass().getResourceAsStream("/assets/blueallow.png"))
              .getScaledInstance(
                  Config.menuIconSize, Config.menuIconSize, java.awt.Image.SCALE_SMOOTH));
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

    iconAvoid = new ImageIcon();
    try {
      // iconbh.setImage(ImageIO.read(getClass().getResourceAsStream("/assets/menu.png")));
      iconAvoid.setImage(
          ImageIO.read(getClass().getResourceAsStream("/assets/redavoid.png"))
              .getScaledInstance(
                  Config.menuIconSize, Config.menuIconSize, java.awt.Image.SCALE_SMOOTH));
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

    iconAllow2 = new ImageIcon();
    try {
      // iconbh.setImage(ImageIO.read(getClass().getResourceAsStream("/assets/menu.png")));
      iconAllow2.setImage(
          ImageIO.read(getClass().getResourceAsStream("/assets/blueallow2.png"))
              .getScaledInstance(
                  Config.menuIconSize, Config.menuIconSize, java.awt.Image.SCALE_SMOOTH));
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

    iconAvoid2 = new ImageIcon();
    try {
      // iconbh.setImage(ImageIO.read(getClass().getResourceAsStream("/assets/menu.png")));
      iconAvoid2.setImage(
          ImageIO.read(getClass().getResourceAsStream("/assets/redavoid2.png"))
              .getScaledInstance(
                  Config.menuIconSize, Config.menuIconSize, java.awt.Image.SCALE_SMOOTH));
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

    iconClear = new ImageIcon();
    try {
      // iconbh.setImage(ImageIO.read(getClass().getResourceAsStream("/assets/menu.png")));
      iconClear.setImage(
          ImageIO.read(getClass().getResourceAsStream("/assets/clear.png"))
              .getScaledInstance(
                  Config.menuIconSize, Config.menuIconSize, java.awt.Image.SCALE_SMOOTH));
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    iconplayPass = new ImageIcon();
    try {
      // iconbh.setImage(ImageIO.read(getClass().getResourceAsStream("/assets/menu.png")));
      iconplayPass.setImage(
          ImageIO.read(getClass().getResourceAsStream("/assets/playpass.png"))
              .getScaledInstance(
                  Config.menuIconSize, Config.menuIconSize, java.awt.Image.SCALE_SMOOTH));
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

    final JFontMenuItem addBlack =
        new JFontMenuItem(resourceBundle.getString("Menu.addBlack")); // ("添加黑子");
    addBlack.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            featurecat.lizzie.gui.Input.insert = 0;
            Lizzie.frame.blackorwhite = 1;
            black.setIcon(iconblack2);
            white.setIcon(iconwhite);
            blackwhite.setIcon(iconbh);
          }
        });
    editMenu.add(addBlack);
    addBlack.setIcon(iconblack);

    final JFontMenuItem addWhite =
        new JFontMenuItem(resourceBundle.getString("Menu.addWhite")); // ("添加白子");
    addWhite.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            featurecat.lizzie.gui.Input.insert = 0;
            Lizzie.frame.blackorwhite = 2;
            black.setIcon(iconblack);
            white.setIcon(iconwhite2);
            blackwhite.setIcon(iconbh);
          }
        });
    editMenu.add(addWhite);
    addWhite.setIcon(iconwhite);

    final JFontMenuItem alternatelyMoves =
        new JFontMenuItem(resourceBundle.getString("Menu.alternatelyMoves")); // ("交替落子");
    alternatelyMoves.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            featurecat.lizzie.gui.Input.insert = 0;
            Lizzie.frame.blackorwhite = 0;
            black.setIcon(iconblack);
            white.setIcon(iconwhite);
            blackwhite.setIcon(iconbh2);
          }
        });
    editMenu.add(alternatelyMoves);
    alternatelyMoves.setIcon(iconbh);

    final JFontCheckBoxMenuItem allowDoubleClick =
        new JFontCheckBoxMenuItem(resourceBundle.getString("Menu.allowDoubleClick")); // ("允许双击找子");
    allowDoubleClick.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            Lizzie.config.allowDoubleClick = !Lizzie.config.allowDoubleClick;
            Lizzie.config.uiConfig.put("allow-double-click", Lizzie.config.allowDoubleClick);
          }
        });
    editMenu.add(allowDoubleClick);

    final JFontCheckBoxMenuItem allowDrag =
        new JFontCheckBoxMenuItem(resourceBundle.getString("Menu.allowDrag")); // ("允许拖动棋子");
    allowDrag.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            Lizzie.config.allowDrag = !Lizzie.config.allowDrag;
            Lizzie.config.uiConfig.put("allow-drag", Lizzie.config.allowDrag);
          }
        });
    editMenu.add(allowDrag);

    editMenu.addSeparator();

    final JFontCheckBoxMenuItem insertBlack =
        new JFontCheckBoxMenuItem(resourceBundle.getString("Menu.insertBlack")); // ("插入黑子");
    insertBlack.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            if (featurecat.lizzie.gui.Input.insert == 1) featurecat.lizzie.gui.Input.insert = 0;
            else featurecat.lizzie.gui.Input.insert = 1;
          }
        });
    editMenu.add(insertBlack);

    final JFontCheckBoxMenuItem insertWhite =
        new JFontCheckBoxMenuItem(resourceBundle.getString("Menu.insertWhite")); // ("插入白子");
    insertWhite.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            if (featurecat.lizzie.gui.Input.insert == 2) featurecat.lizzie.gui.Input.insert = 0;
            else featurecat.lizzie.gui.Input.insert = 2;
          }
        });
    editMenu.add(insertWhite);

    editMenu.addSeparator();

    final JFontMenuItem clearBoard =
        new JFontMenuItem(resourceBundle.getString("Menu.clearBoard")); // ("清空棋盘(Ctrl+Home)");
    clearBoard.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            Lizzie.board.clear(false);
            Lizzie.frame.refresh();
          }
        });
    editMenu.add(clearBoard);

    final JFontMenuItem backToMainBranch =
        new JFontMenuItem(resourceBundle.getString("Menu.backToMainBranch")); // ("返回主分支(T)");
    backToMainBranch.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            Lizzie.frame.moveToMainTrunk();
          }
        });
    editMenu.add(backToMainBranch);

    final JFontMenuItem setAsMain =
        new JFontMenuItem(resourceBundle.getString("Menu.setAsMain")); // ("设为主分支(L)");
    editMenu.add(setAsMain);

    setAsMain.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            Lizzie.frame.setAsMain();
          }
        });
    editMenu.addSeparator();

    final JFontMenuItem jumpToFirst =
        new JFontMenuItem(resourceBundle.getString("Menu.jumpToFirst")); // ("跳转到最前(Home)");
    jumpToFirst.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            Lizzie.frame.firstMove();
          }
        });
    editMenu.add(jumpToFirst);

    final JFontMenuItem jumpToLast =
        new JFontMenuItem(resourceBundle.getString("Menu.jumpToLast")); // ("跳转到最后(End)");
    jumpToLast.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            Lizzie.frame.lastMove();
          }
        });
    editMenu.add(jumpToLast);

    final JFontMenuItem jumpToLeft =
        new JFontMenuItem(resourceBundle.getString("Menu.jumpToLeft")); // ("跳转到左分支(←)");
    jumpToLeft.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            Input.previousBranch();
          }
        });
    editMenu.add(jumpToLeft);

    final JFontMenuItem jumpToRight =
        new JFontMenuItem(resourceBundle.getString("Menu.jumpToRight")); // ("跳转到右分(→)");
    jumpToRight.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            Input.nextBranch();
          }
        });
    editMenu.add(jumpToRight);

    editMenu.addSeparator();

    final JFontMenuItem delete =
        new JFontMenuItem(resourceBundle.getString("Menu.delete")); // ("删除一手(Delete)");
    editMenu.add(delete);
    delete.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            Lizzie.board.deleteMove();
          }
        });

    final JFontMenuItem deleteBranch =
        new JFontMenuItem(resourceBundle.getString("Menu.deleteBranch")); // ("删除分支(Shift+Delete)");
    editMenu.add(deleteBranch);
    deleteBranch.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            Lizzie.board.deleteBranch();
          }
        });

    editMenu.addSeparator();

    final JFontMenuItem setInfo =
        new JFontMenuItem(resourceBundle.getString("Menu.setInfo")); // ("编辑棋局信息(I)");
    setInfo.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            LizzieFrame.editGameInfo();
          }
        });
    editMenu.add(setInfo);

    final JFontMenuItem setBoard =
        new JFontMenuItem(resourceBundle.getString("Menu.setBoard")); // ("设置棋盘大小(Ctrl+I)");
    setBoard.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            SetBoardSize setBoardSize = new SetBoardSize();
            setBoardSize.setVisible(true);
          }
        });
    editMenu.add(setBoard);

    editMenu.addSeparator();

    final JFontMenuItem exchange =
        new JFontMenuItem(resourceBundle.getString("Menu.exchange")); // ("交换黑白(Ctrl+Shift+Alt+右)");
    editMenu.add(exchange);
    exchange.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            Lizzie.board.exchangeBlackWhite();
          }
        });

    final JFontMenuItem spinRight =
        new JFontMenuItem(resourceBundle.getString("Menu.spinRight")); // ("向右旋转(Ctrl+Alt+右)");
    editMenu.add(spinRight);
    spinRight.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            Lizzie.board.SpinAndMirror(1);
          }
        });
    final JFontMenuItem spinLeft =
        new JFontMenuItem(resourceBundle.getString("Menu.spinLeft")); // ("向左旋转(Ctrl+Alt+左)");
    editMenu.add(spinLeft);
    spinLeft.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            Lizzie.board.SpinAndMirror(2);
          }
        });
    final JFontMenuItem mirrorVertical =
        new JFontMenuItem(resourceBundle.getString("Menu.mirrorVertical")); // ("水平翻转(Ctrl+Alt+上)");
    editMenu.add(mirrorVertical);
    mirrorVertical.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            Lizzie.board.SpinAndMirror(3);
          }
        });
    final JFontMenuItem mirrorHorizon =
        new JFontMenuItem(resourceBundle.getString("Menu.mirrorHorizon")); // ("垂直翻转(Ctrl+Alt+下)");
    editMenu.add(mirrorHorizon);
    mirrorHorizon.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            Lizzie.board.SpinAndMirror(4);
          }
        });

    //    if (!Lizzie.config.enableLizzieCache) {
    //      clearsave.setVisible(false);
    //      clearthis.setVisible(false);
    //    }

    editMenu.addMenuListener(
        new MenuListener() {

          public void menuSelected(MenuEvent e) {
            if (Lizzie.config.allowDrag) allowDrag.setState(true);
            else allowDrag.setState(false);
            if (Lizzie.config.allowDoubleClick) allowDoubleClick.setState(true);
            else allowDoubleClick.setState(false);
            if (featurecat.lizzie.gui.Input.insert == 0) {
              insertBlack.setState(false);
              insertWhite.setState(false);
            } else if (featurecat.lizzie.gui.Input.insert == 1) {
              insertBlack.setState(true);
              insertWhite.setState(false);
            } else if (featurecat.lizzie.gui.Input.insert == 2) {
              insertBlack.setState(false);
              insertWhite.setState(true);
            }
          }

          @Override
          public void menuDeselected(MenuEvent e) {
            // TODO Auto-generated method stub
          }

          @Override
          public void menuCanceled(MenuEvent e) {
            // TODO Auto-generated method stub

          }
        });

    final JFontMenu shareKifu = new JFontMenu(resourceBundle.getString("Menu.share"));
    shareKifu.setForeground(Color.BLACK);
    // shareKifu.setFont(headFont);
    this.add(shareKifu);

    final JFontMenuItem shareSearch =
        new JFontMenuItem(resourceBundle.getString("Menu.shareSearch")); // ("公开棋谱查询");
    shareKifu.add(shareSearch);

    shareSearch.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            Lizzie.frame.openPublicKifuSearch();
          }
        });

    final JFontMenuItem shareCurrentSgf =
        new JFontMenuItem(resourceBundle.getString("Menu.shareCurrentSgf")); // ("分享当前棋谱(Ctrl+E)");
    shareKifu.add(shareCurrentSgf);

    shareCurrentSgf.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            Lizzie.frame.shareSGF();
          }
        });

    final JFontMenuItem shareBatchSgf =
        new JFontMenuItem(resourceBundle.getString("Menu.shareBatchSgf")); // ("批量分享");
    shareKifu.add(shareBatchSgf);

    shareBatchSgf.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            Lizzie.frame.batchShareSGF();
          }
        });

    final JFontMenuItem shareEdit =
        new JFontMenuItem(resourceBundle.getString("Menu.shareEdit")); // ("查询(修改)已分享棋谱信息");
    shareKifu.add(shareEdit);

    shareEdit.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            Lizzie.frame.openPrivateKifuSearch();
          }
        });

    final JFontMenuItem shareHistoryLoacl =
        new JFontMenuItem(resourceBundle.getString("Menu.shareHistoryLoacl")); // ("历史记录(本地)");
    shareKifu.add(shareHistoryLoacl);

    shareHistoryLoacl.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            File file = new File("");
            String courseFile = "";
            try {
              courseFile = file.getCanonicalPath();
            } catch (IOException se) {
              // TODO Auto-generated catch block
            }
            try {
              File linkHistory = new File(courseFile + "\\" + "shareLinks.txt");
              if (!linkHistory.exists()) {
                Message msg = new Message();
                msg.setMessage(
                    resourceBundle.getString(
                        "Menu.shareHistoryLoaclHintEmpty")); // ("历史记录为空");//shareHistoryLoaclHintEmpty
                // msg.setVisible(true);
                return;
              }
              Desktop.getDesktop().open(linkHistory);
            } catch (IOException e1) {
              // TODO Auto-generated catch block
              Message msg = new Message();
              msg.setMessage(
                  resourceBundle.getString(
                      "Menu.shareHistoryLoaclHintOpenFailed")); // ("打开失败");//shareHistoryLoaclHintOpenFailed
            }
          }
        });

    final JFontMenu live = new JFontMenu(resourceBundle.getString("Menu.sync"));
    live.setForeground(Color.BLACK);
    // live.setFont(headFont);
    this.add(live);

    final JFontMenuItem yikeLive =
        new JFontMenuItem(resourceBundle.getString("Menu.yikeLive")); // ("弈客直播(Shift+O)");
    live.add(yikeLive);

    yikeLive.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            Lizzie.frame.bowser(
                "https://home.yikeweiqi.com/#/live",
                resourceBundle.getString("BottomToolbar.yikeLive"),
                true);
          }
        });

    final JFontMenuItem yikeRoom =
        new JFontMenuItem(resourceBundle.getString("Menu.yikeRoom")); // ("弈客大厅");
    live.add(yikeRoom);

    yikeRoom.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            Lizzie.frame.bowser(
                "https://home.yikeweiqi.com/#/game",
                resourceBundle.getString("BottomToolbar.yikeRoom"),
                true);
          }
        });

    final JFontMenuItem foxKifu =
        new JFontMenuItem(resourceBundle.getString("Menu.foxKifu")); // ("野狐(腾讯)棋谱");
    live.add(foxKifu);

    foxKifu.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            Lizzie.frame.openFoxReq();
          }
        });

    final JFontMenuItem readBoardJava =
        new JFontMenuItem(resourceBundle.getString("Menu.readBoardJava")); // ("棋盘同步工具");
    live.add(readBoardJava);
    readBoardJava.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            Lizzie.frame.openReadBoardJava();
          }
        });

    final JFontMenuItem readBoard =
        new JFontMenuItem(resourceBundle.getString("Menu.readBoard")); // ("棋盘识别工具(Alt+O)");
    if (OS.isWindows()) live.add(readBoard);

    readBoard.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            Lizzie.frame.openBoardSync();
          }
        });
    live.addSeparator();

    final JFontCheckBoxMenuItem EnableEnterYikeGame =
        new JFontCheckBoxMenuItem(
            resourceBundle.getString("Menu.EnableEnterYikeGame")); // //允许弈客直播/大厅进入对居室
    EnableEnterYikeGame.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            Lizzie.config.openHtmlOnLive = !Lizzie.config.openHtmlOnLive;
            Lizzie.config.uiConfig.put("open-html-onlive", Lizzie.config.openHtmlOnLive);
          }
        });
    live.add(EnableEnterYikeGame);

    final JFontCheckBoxMenuItem alwaysGoToLastMove =
        new JFontCheckBoxMenuItem(
            resourceBundle.getString("Menu.alwaysGoToLastMove")); // //有新的一手时总是跳转到最新一手
    alwaysGoToLastMove.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            Lizzie.config.alwaysGotoLastOnLive = !Lizzie.config.alwaysGotoLastOnLive;
            Lizzie.config.uiConfig.put(
                "always-gotolast-onlive", Lizzie.config.alwaysGotoLastOnLive);
          }
        });
    live.add(alwaysGoToLastMove);

    final JFontMenu readBoardSettings =
        new JFontMenu(resourceBundle.getString("Menu.readBoardSettings")); // ("识别工具选项");
    live.add(readBoardSettings);

    final JFontCheckBoxMenuItem alwaysKeepBoardStatSync =
        new JFontCheckBoxMenuItem(
            resourceBundle.getString("Menu.alwaysKeepBoardStatSync")); // ("总是保持棋盘一致(回退时可能破坏历史手顺)");
    alwaysKeepBoardStatSync.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            Lizzie.config.alwaysSyncBoardStat = !Lizzie.config.alwaysSyncBoardStat;
            Lizzie.config.uiConfig.put("always-sync-boardstat", Lizzie.config.alwaysSyncBoardStat);
          }
        });
    readBoardSettings.add(alwaysKeepBoardStatSync);

    final JFontCheckBoxMenuItem readBoardGetFocus =
        new JFontCheckBoxMenuItem(
            resourceBundle.getString("Menu.readBoardGetFocus")); // 滚轮控制变化图时自动获取焦点
    readBoardGetFocus.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            Lizzie.config.readBoardGetFocus = !Lizzie.config.readBoardGetFocus;
            Lizzie.config.uiConfig.put("read-board-get-focus", Lizzie.config.readBoardGetFocus);
          }
        });
    readBoardSettings.add(readBoardGetFocus);

    live.addMenuListener(
        new MenuListener() {

          public void menuSelected(MenuEvent e) {
            if (Lizzie.config.openHtmlOnLive) EnableEnterYikeGame.setState(true);
            else EnableEnterYikeGame.setState(false);
            if (Lizzie.config.alwaysGotoLastOnLive) alwaysGoToLastMove.setState(true);
            else alwaysGoToLastMove.setState(false);
            if (Lizzie.config.alwaysSyncBoardStat) alwaysKeepBoardStatSync.setState(true);
            else alwaysKeepBoardStatSync.setState(false);
            if (Lizzie.config.readBoardGetFocus) readBoardGetFocus.setState(true);
            else readBoardGetFocus.setState(false);
          }

          @Override
          public void menuDeselected(MenuEvent e) {
            // TODO Auto-generated method stub
          }

          @Override
          public void menuCanceled(MenuEvent e) {
            // TODO Auto-generated method stub

          }
        });

    final JMenu topToolBar = new JFontMenu(resourceBundle.getString("Menu.topToolBar"));

    final JFontCheckBoxMenuItem topToolBarSeparated =
        new JFontCheckBoxMenuItem(resourceBundle.getString("Menu.topToolBarSeparated"));
    topToolBarSeparated.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            Lizzie.config.showTopToolBar = true;
            Lizzie.config.showDoubleMenu = true;
            Lizzie.config.uiConfig.put("show-top-tool-bar", Lizzie.config.showTopToolBar);
            doAfterChangeToolarPos();
          }
        });
    topToolBar.add(topToolBarSeparated);

    final JFontCheckBoxMenuItem topToolBarCombined =
        new JFontCheckBoxMenuItem(resourceBundle.getString("Menu.topToolBarCombined"));
    topToolBarCombined.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            Lizzie.config.showTopToolBar = true;
            Lizzie.config.showDoubleMenu = false;
            Lizzie.config.uiConfig.put("show-top-tool-bar", Lizzie.config.showTopToolBar);
            doAfterChangeToolarPos();
            Lizzie.frame.reSetLoc();
          }
        });
    topToolBar.add(topToolBarCombined);

    final JFontCheckBoxMenuItem topToolBarInVisible =
        new JFontCheckBoxMenuItem(resourceBundle.getString("Menu.inVisible"));
    topToolBarInVisible.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            Lizzie.config.showTopToolBar = false;
            Lizzie.config.uiConfig.put("show-top-tool-bar", Lizzie.config.showTopToolBar);
            Lizzie.config.flashAnalyze =
                Lizzie.config.uiConfig.optBoolean(
                    "flash-analyze",
                    Lizzie.config.showDoubleMenu && Lizzie.config.showTopToolBar ? false : true);
            if (!Lizzie.config.showDoubleMenu) {
              Lizzie.config.showDoubleMenu = true;
              doAfterChangeToolarPos();
            } else {
              Lizzie.frame.reSetLoc();
            }
          }
        });
    topToolBar.add(topToolBarInVisible);

    final JFontCheckBoxMenuItem autoWrap =
        new JFontCheckBoxMenuItem(resourceBundle.getString("Menu.autoWrap"));
    autoWrap.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            Lizzie.config.autoWrapToolBar = !Lizzie.config.autoWrapToolBar;
            Lizzie.frame.topPanel.updateUI();
            Lizzie.frame.reSetLoc();
            Lizzie.config.uiConfig.put("auto-wrap-tool-bar", Lizzie.config.autoWrapToolBar);
          }
        });
    topToolBar.addSeparator();
    topToolBar.add(autoWrap);

    toolBar.add(topToolBar);

    topToolBar.addMenuListener(
        new MenuListener() {

          public void menuSelected(MenuEvent e) {
            if (Lizzie.config.showTopToolBar) {
              if (Lizzie.config.showDoubleMenu) {
                topToolBarCombined.setState(false);
                topToolBarInVisible.setState(false);
                topToolBarSeparated.setSelected(true);
              } else {
                topToolBarCombined.setState(true);
                topToolBarInVisible.setState(false);
                topToolBarSeparated.setSelected(false);
              }
            } else {
              topToolBarCombined.setState(false);
              topToolBarInVisible.setState(true);
              topToolBarSeparated.setSelected(false);
            }
            if (Lizzie.config.autoWrapToolBar) autoWrap.setState(true);
            else autoWrap.setState(false);
          }

          @Override
          public void menuDeselected(MenuEvent e) {
            // TODO Auto-generated method stub
          }

          @Override
          public void menuCanceled(MenuEvent e) {
            // TODO Auto-generated method stub

          }
        });

    final JMenu bottomToolBar =
        new JFontMenu(resourceBundle.getString("Menu.bottomToolBar")); // ("底部工具栏");
    toolBar.add(bottomToolBar);

    final JFontCheckBoxMenuItem bottomToolbarVisible =
        new JFontCheckBoxMenuItem(resourceBundle.getString("Menu.visible"));
    bottomToolBar.add(bottomToolbarVisible);

    bottomToolbarVisible.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            Lizzie.frame.toolbarHeight = 26;
            LizzieFrame.toolbar.setVisible(true);
            if (Lizzie.config.showDoubleMenu) doubleMenu(false);
            Lizzie.frame.reSetLoc();
          }
        });

    final JFontCheckBoxMenuItem bottomToolbarInVisible =
        new JFontCheckBoxMenuItem(resourceBundle.getString("Menu.inVisible"));
    bottomToolBar.add(bottomToolbarInVisible);

    bottomToolbarInVisible.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            Lizzie.frame.toolbarHeight = 0;
            LizzieFrame.toolbar.setVisible(false);
            if (Lizzie.config.showDoubleMenu) doubleMenu(false);
            Lizzie.frame.reSetLoc();
          }
        });

    final JMenu detailedBar = new JFontMenu("详细工具栏(过时的)(仅中文)"); // ("详细工具栏(过时的)(仅中文)");
    if (Lizzie.config.isChinese) bottomToolBar.add(detailedBar);

    final JFontCheckBoxMenuItem showDetailedBar = new JFontCheckBoxMenuItem("使用详细工具栏");
    detailedBar.add(showDetailedBar);

    showDetailedBar.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            if (Lizzie.frame.toolbarHeight != 70) {
              SwingUtilities.invokeLater(
                  new Runnable() {
                    public void run() {
                      int ret =
                          JOptionPane.showConfirmDialog(
                              Lizzie.frame,
                              "详细工具栏已过时,其中的内容可以在顶部/底部工具栏中找到,从详细工具栏发起的功能也已过时,例如自动分析不能分析所有分支,确定启用详细工具栏?",
                              "确定使用详细工具栏?",
                              JOptionPane.YES_NO_OPTION);
                      if (ret == JOptionPane.NO_OPTION) {
                        return;
                      }
                      Lizzie.frame.toolbarHeight = 70;
                      LizzieFrame.toolbar.setVisible(true);
                      Lizzie.frame.reSetLoc();
                    }
                  });
            } else {
              Lizzie.frame.toolbarHeight = 26;
              LizzieFrame.toolbar.setVisible(true);
              Lizzie.frame.reSetLoc();
            }
          }
        });

    final JFontCheckBoxMenuItem showDetailedMenu = new JFontCheckBoxMenuItem("显示拓展/隐藏详细工具栏按钮");
    detailedBar.add(showDetailedMenu);

    showDetailedMenu.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            Lizzie.config.showDetailedToolbarMenu = !Lizzie.config.showDetailedToolbarMenu;
            Lizzie.config.uiConfig.put(
                "show-detailed-toolbar-menu", Lizzie.config.showDetailedToolbarMenu);
            LizzieFrame.toolbar.restShowDetail();
          }
        });

    final JFontMenuItem detailedBarOrder = new JFontMenuItem("设置模块顺序");
    detailedBar.add(detailedBarOrder);

    detailedBarOrder.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            ToolbarPositionConfig toolbarPositionConfig = new ToolbarPositionConfig();
            toolbarPositionConfig.setVisible(true);
          }
        });

    bottomToolBar.addSeparator();
    bottomToolBar.addMenuListener(
        new MenuListener() {

          public void menuSelected(MenuEvent e) {
            if (Lizzie.frame.toolbarHeight == 0) {
              bottomToolbarInVisible.setState(true);
              bottomToolbarVisible.setState(false);
              showDetailedBar.setState(false);
            } else if (Lizzie.frame.toolbarHeight == 70) {
              showDetailedBar.setState(true);
              bottomToolbarInVisible.setState(false);
              bottomToolbarVisible.setState(false);
            } else {
              bottomToolbarInVisible.setState(false);
              bottomToolbarVisible.setState(true);
              showDetailedBar.setState(false);
            }
            if (Lizzie.config.showDetailedToolbarMenu) showDetailedMenu.setState(true);
            else showDetailedMenu.setState(false);
          }

          @Override
          public void menuDeselected(MenuEvent e) {
            // TODO Auto-generated method stub
          }

          @Override
          public void menuCanceled(MenuEvent e) {
            // TODO Auto-generated method stub

          }
        });

    final JFontMenu customTopToolBar =
        new JFontMenu(resourceBundle.getString("Menu.customToolbar"));
    topToolBar.add(customTopToolBar);

    final JFontCheckBoxMenuItem showDoubleMenuBtn =
        new JFontCheckBoxMenuItem(
            resourceBundle.getString("Menu.showDoubleMenuBtn")); // ("双层菜单按钮");
    customTopToolBar.add(showDoubleMenuBtn);

    showDoubleMenuBtn.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            Lizzie.config.showDoubleMenuBtn = !Lizzie.config.showDoubleMenuBtn;
            Lizzie.config.uiConfig.put("show-double-menu-btn", Lizzie.config.showDoubleMenuBtn);
            toggleShowDoubleMenuButton(Lizzie.config.showDoubleMenuBtn);
          }
        });

    final JFontCheckBoxMenuItem showBasicBtn =
        new JFontCheckBoxMenuItem(
            resourceBundle.getString("Menu.showBasicBtn")); // ("基本按钮(仅双层菜单时)");
    customTopToolBar.add(showBasicBtn);

    showBasicBtn.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            Lizzie.config.showBasicBtn = !Lizzie.config.showBasicBtn;
            Lizzie.config.uiConfig.put("show-basic-btn", Lizzie.config.showBasicBtn);
            doubleMenu(false);
          }
        });

    final JFontCheckBoxMenuItem playMoveTools =
        new JFontCheckBoxMenuItem(resourceBundle.getString("Menu.playMoveTools")); // ("落子工具");
    customTopToolBar.add(playMoveTools);

    playMoveTools.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            Lizzie.config.showEditbar = !Lizzie.config.showEditbar;
            Lizzie.config.uiConfig.put("show-edit-bar", Lizzie.config.showEditbar);
            toggleShowEditbar(Lizzie.config.showEditbar);
          }
        });

    final JFontCheckBoxMenuItem allowAvoidTools =
        new JFontCheckBoxMenuItem(resourceBundle.getString("Menu.allowAvoidTools")); // ("强制计算工具");
    customTopToolBar.add(allowAvoidTools);

    allowAvoidTools.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            Lizzie.config.showForceMenu = !Lizzie.config.showForceMenu;
            Lizzie.config.uiConfig.put("show-force-menu", Lizzie.config.showForceMenu);
            toggleShowForce(Lizzie.config.showForceMenu);
          }
        });

    final JFontCheckBoxMenuItem showGameController =
        new JFontCheckBoxMenuItem(resourceBundle.getString("Menu.showGameController")); // ("对局控制");
    customTopToolBar.add(showGameController);

    showGameController.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            Lizzie.config.showDoubleMenuGameControl = !Lizzie.config.showDoubleMenuGameControl;
            Lizzie.config.uiConfig.put(
                "show-double-menu-game-control", Lizzie.config.showDoubleMenuGameControl);
            // updateMenuAfterEngine(false);
            setKomiPanelExtra();
          }
        });

    final JFontCheckBoxMenuItem showTimeLimitController =
        new JFontCheckBoxMenuItem(
            resourceBundle.getString("Menu.showTimeLimitController")); // ("单步分析时限(秒)");
    customTopToolBar.add(showTimeLimitController);
    showTimeLimitController.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            Lizzie.config.showTimeControlInMenu = !Lizzie.config.showTimeControlInMenu;
            Lizzie.config.uiConfig.put(
                "show-time-control-in-menu", Lizzie.config.showTimeControlInMenu);
            setKomiPanelExtra();
          }
        });

    final JFontCheckBoxMenuItem showPlayoutLimitController =
        new JFontCheckBoxMenuItem(
            resourceBundle.getString("Menu.showPlayoutLimitController")); // ("单步分析时限(秒)");
    customTopToolBar.add(showPlayoutLimitController);
    showPlayoutLimitController.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            Lizzie.config.showPlayoutControlInMenu = !Lizzie.config.showPlayoutControlInMenu;
            Lizzie.config.uiConfig.put(
                "show-playout-control-in-menu", Lizzie.config.showPlayoutControlInMenu);
            setKomiPanelExtra();
          }
        });

    final JFontCheckBoxMenuItem showAnalyzeController =
        new JFontCheckBoxMenuItem(
            resourceBundle.getString("Menu.showAnalyzeController")); // ("选点分析控制");
    customTopToolBar.add(showAnalyzeController);

    showAnalyzeController.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            Lizzie.config.showAnalyzeController = !Lizzie.config.showAnalyzeController;
            Lizzie.config.uiConfig.put(
                "show-analyze-controller", Lizzie.config.showAnalyzeController);
            setKomiPanelExtra();
          }
        });

    final JFontCheckBoxMenuItem showCandidateController =
        new JFontCheckBoxMenuItem(
            resourceBundle.getString("Menu.showCandidateController")); // ("选点显示控制");
    customTopToolBar.add(showCandidateController);

    showCandidateController.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            Lizzie.config.showDoubleMenuVar = !Lizzie.config.showDoubleMenuVar;
            Lizzie.config.uiConfig.put("show-double-menu-var", Lizzie.config.showDoubleMenuVar);
            setKomiPanelExtra();
          }
        });

    final JFontCheckBoxMenuItem showCandidateInfoController =
        new JFontCheckBoxMenuItem(
            resourceBundle.getString("Menu.showCandidateInfoController")); // ("选点信息控制");
    customTopToolBar.add(showCandidateInfoController);

    showCandidateInfoController.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            Lizzie.config.showDoubleMenuMoveInfo = !Lizzie.config.showDoubleMenuMoveInfo;
            Lizzie.config.uiConfig.put(
                "show-double-menu-moveinfo", Lizzie.config.showDoubleMenuMoveInfo);
            setKomiPanelExtra();
          }
        });

    final JFontCheckBoxMenuItem showRuleMenu =
        new JFontCheckBoxMenuItem(resourceBundle.getString("Menu.showRuleMenu")); // ("规则按钮");
    customTopToolBar.add(showRuleMenu);

    showRuleMenu.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            Lizzie.config.showRuleMenu = !Lizzie.config.showRuleMenu;
            Lizzie.config.uiConfig.put("show-rule-menu", Lizzie.config.showRuleMenu);
            setKomiPanelExtra();
          }
        });

    final JFontCheckBoxMenuItem showParamMenu =
        new JFontCheckBoxMenuItem(resourceBundle.getString("Menu.showParamMenu")); // 参数按钮
    customTopToolBar.add(showParamMenu);

    showParamMenu.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            Lizzie.config.showParamMenu = !Lizzie.config.showParamMenu;
            Lizzie.config.uiConfig.put("show-param-menu", Lizzie.config.showParamMenu);
            setKomiPanelExtra();
          }
        });

    final JFontCheckBoxMenuItem showGobanMenu =
        new JFontCheckBoxMenuItem(resourceBundle.getString("Menu.showGobanMenu")); // ("棋盘按钮");
    customTopToolBar.add(showGobanMenu);

    showGobanMenu.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            Lizzie.config.showGobanMenu = !Lizzie.config.showGobanMenu;
            Lizzie.config.uiConfig.put("show-goban-menu", Lizzie.config.showGobanMenu);
            setKomiPanelExtra();
          }
        });

    final JFontCheckBoxMenuItem showSaveLoadMenu =
        new JFontCheckBoxMenuItem(resourceBundle.getString("Menu.showSaveLoadMenu")); // ("存档按钮");
    customTopToolBar.add(showSaveLoadMenu);

    showSaveLoadMenu.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            Lizzie.config.showSaveLoadMenu = !Lizzie.config.showSaveLoadMenu;
            Lizzie.config.uiConfig.put("show-saveload-menu", Lizzie.config.showSaveLoadMenu);
            setKomiPanelExtra();
          }
        });

    customTopToolBar.addMenuListener(
        new MenuListener() {

          public void menuSelected(MenuEvent e) {
            if (Lizzie.config.showEditbar) playMoveTools.setSelected(true);
            else playMoveTools.setSelected(false);
            if (Lizzie.config.showForceMenu) allowAvoidTools.setSelected(true);
            else allowAvoidTools.setSelected(false);
            if (Lizzie.config.showRuleMenu) showRuleMenu.setSelected(true);
            else showRuleMenu.setSelected(false);
            if (Lizzie.config.showParamMenu) showParamMenu.setSelected(true);
            else showParamMenu.setSelected(false);
            if (Lizzie.config.showGobanMenu) showGobanMenu.setSelected(true);
            else showGobanMenu.setSelected(false);
            if (Lizzie.config.showSaveLoadMenu) showSaveLoadMenu.setSelected(true);
            else showSaveLoadMenu.setSelected(false);
            if (Lizzie.config.showDoubleMenuBtn) showDoubleMenuBtn.setSelected(true);
            else showDoubleMenuBtn.setSelected(false);
            if (Lizzie.config.showDoubleMenuGameControl) showGameController.setSelected(true);
            else showGameController.setSelected(false);
            if (Lizzie.config.showDoubleMenuVar) showCandidateController.setSelected(true);
            else showCandidateController.setSelected(false);
            if (Lizzie.config.showTimeControlInMenu) showTimeLimitController.setState(true);
            else showTimeLimitController.setState(false);
            if (Lizzie.config.showPlayoutControlInMenu) showPlayoutLimitController.setState(true);
            else showPlayoutLimitController.setState(false);
            if (Lizzie.config.showDoubleMenuMoveInfo) showCandidateInfoController.setSelected(true);
            else showCandidateInfoController.setSelected(false);
            if (Lizzie.config.showBasicBtn) showBasicBtn.setSelected(true);
            else showBasicBtn.setSelected(false);
            if (Lizzie.config.showAnalyzeController) showAnalyzeController.setState(true);
            else showAnalyzeController.setState(false);
          }

          @Override
          public void menuDeselected(MenuEvent e) {
            // TODO Auto-generated method stub
          }

          @Override
          public void menuCanceled(MenuEvent e) {
            // TODO Auto-generated method stub

          }
        });

    final JFontMenu customToolbarItem =
        new JFontMenu(resourceBundle.getString("Menu.customToolbar")); // ("自定义工具栏按钮");
    bottomToolBar.add(customToolbarItem);

    final JFontCheckBoxMenuItem liveButton =
        new JFontCheckBoxMenuItem(resourceBundle.getString("BottomToolbar.liveButton")); // ("同步");
    customToolbarItem.add(liveButton);
    liveButton.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            Lizzie.config.liveButton = !Lizzie.config.liveButton;
            Lizzie.config.uiConfig.put("liveButton", Lizzie.config.liveButton);
            LizzieFrame.toolbar.reSetButtonLocation();
          }
        });

    final JFontCheckBoxMenuItem shareButton =
        new JFontCheckBoxMenuItem(resourceBundle.getString("BottomToolbar.share")); // ("分享");
    customToolbarItem.add(shareButton);
    shareButton.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            Lizzie.config.share = !Lizzie.config.share;
            Lizzie.config.uiConfig.put("share", Lizzie.config.share);
            LizzieFrame.toolbar.reSetButtonLocation();
          }
        });

    final JFontCheckBoxMenuItem kataEstimateButton =
        new JFontCheckBoxMenuItem(
            resourceBundle.getString("BottomToolbar.kataEstimate")); // "Kata评估");
    customToolbarItem.add(kataEstimateButton);
    kataEstimateButton.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            Lizzie.config.kataEstimate = !Lizzie.config.kataEstimate;
            Lizzie.config.uiConfig.put("kataEstimate", Lizzie.config.kataEstimate);
            LizzieFrame.toolbar.reSetButtonLocation();
          }
        });

    final JFontCheckBoxMenuItem flashAnalyze =
        new JFontCheckBoxMenuItem(resourceBundle.getString("BottomToolbar.flashAnalyze"));
    customToolbarItem.add(flashAnalyze);
    flashAnalyze.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            Lizzie.config.flashAnalyze = !Lizzie.config.flashAnalyze;
            Lizzie.config.uiConfig.put("flash-analyze", Lizzie.config.flashAnalyze);
            LizzieFrame.toolbar.reSetButtonLocation();
          }
        });

    final JFontCheckBoxMenuItem batchOpen =
        new JFontCheckBoxMenuItem(resourceBundle.getString("BottomToolbar.batchOpen")); // "批量分析");
    customToolbarItem.add(batchOpen);
    batchOpen.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            Lizzie.config.batchOpen = !Lizzie.config.batchOpen;
            Lizzie.config.uiConfig.put("batchOpen", Lizzie.config.batchOpen);
            LizzieFrame.toolbar.reSetButtonLocation();
          }
        });

    final JFontCheckBoxMenuItem openfile =
        new JFontCheckBoxMenuItem(resourceBundle.getString("BottomToolbar.openfile")); // "打开");
    customToolbarItem.add(openfile);
    openfile.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            Lizzie.config.openfile = !Lizzie.config.openfile;
            Lizzie.config.uiConfig.put("openfile", Lizzie.config.openfile);
            LizzieFrame.toolbar.reSetButtonLocation();
          }
        });

    final JFontCheckBoxMenuItem savefile =
        new JFontCheckBoxMenuItem(resourceBundle.getString("BottomToolbar.savefile")); // "保存");
    customToolbarItem.add(savefile);
    savefile.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            Lizzie.config.savefile = !Lizzie.config.savefile;
            Lizzie.config.uiConfig.put("savefile", Lizzie.config.savefile);
            LizzieFrame.toolbar.reSetButtonLocation();
          }
        });

    final JFontCheckBoxMenuItem countButton =
        new JFontCheckBoxMenuItem(
            resourceBundle.getString("BottomToolbar.countButton")); // "形势判断");
    customToolbarItem.add(countButton);
    countButton.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            Lizzie.config.countButton = !Lizzie.config.countButton;
            Lizzie.config.uiConfig.put("countButton", Lizzie.config.countButton);
            LizzieFrame.toolbar.reSetButtonLocation();
          }
        });

    final JFontCheckBoxMenuItem finalScore =
        new JFontCheckBoxMenuItem(resourceBundle.getString("BottomToolbar.finalScore")); // "形势判断");
    customToolbarItem.add(finalScore);
    finalScore.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            Lizzie.config.finalScore = !Lizzie.config.finalScore;
            Lizzie.config.uiConfig.put("finalScore", Lizzie.config.finalScore);
            LizzieFrame.toolbar.reSetButtonLocation();
          }
        });

    final JFontCheckBoxMenuItem heatMap =
        new JFontCheckBoxMenuItem(resourceBundle.getString("BottomToolbar.heatMap")); // "纯网络");
    customToolbarItem.add(heatMap);
    heatMap.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            Lizzie.config.heatMap = !Lizzie.config.heatMap;
            Lizzie.config.uiConfig.put("heatMap", Lizzie.config.heatMap);
            LizzieFrame.toolbar.reSetButtonLocation();
          }
        });

    final JFontCheckBoxMenuItem badMoves =
        new JFontCheckBoxMenuItem(
            Lizzie.resourceBundle.getString("BottomToolbar.badMoves")); // "超级鹰眼");
    customToolbarItem.add(badMoves);
    badMoves.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            Lizzie.config.badMoves = !Lizzie.config.badMoves;
            Lizzie.config.uiConfig.put("badMoves", Lizzie.config.badMoves);
            LizzieFrame.toolbar.reSetButtonLocation();
          }
        });

    final JFontCheckBoxMenuItem analyzeList =
        new JFontCheckBoxMenuItem(
            resourceBundle.getString("BottomToolbar.analyzeList")); // "选点列表");
    customToolbarItem.add(analyzeList);
    analyzeList.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            Lizzie.config.analyzeList = !Lizzie.config.analyzeList;
            Lizzie.config.uiConfig.put("analyze-list", Lizzie.config.analyzeList);
            LizzieFrame.toolbar.reSetButtonLocation();
          }
        });

    final JFontCheckBoxMenuItem refresh =
        new JFontCheckBoxMenuItem(resourceBundle.getString("BottomToolbar.refresh")); // "刷新");
    customToolbarItem.add(refresh);
    refresh.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            Lizzie.config.refresh = !Lizzie.config.refresh;
            Lizzie.config.uiConfig.put("refresh", Lizzie.config.refresh);
            LizzieFrame.toolbar.reSetButtonLocation();
          }
        });

    final JFontCheckBoxMenuItem analyse =
        new JFontCheckBoxMenuItem(resourceBundle.getString("BottomToolbar.analyse")); // "分析");
    customToolbarItem.add(analyse);
    analyse.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            Lizzie.config.analyse = !Lizzie.config.analyse;
            Lizzie.config.uiConfig.put("analyse", Lizzie.config.analyse);
            LizzieFrame.toolbar.reSetButtonLocation();
          }
        });

    final JFontCheckBoxMenuItem tryPlay =
        new JFontCheckBoxMenuItem(resourceBundle.getString("BottomToolbar.tryPlay")); // "试下");
    customToolbarItem.add(tryPlay);
    tryPlay.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            Lizzie.config.tryPlay = !Lizzie.config.tryPlay;
            Lizzie.config.uiConfig.put("tryPlay", Lizzie.config.tryPlay);
            LizzieFrame.toolbar.reSetButtonLocation();
          }
        });

    final JFontCheckBoxMenuItem setMainButton =
        new JFontCheckBoxMenuItem(resourceBundle.getString("BottomToolbar.setMain")); // "设为主分支");
    customToolbarItem.add(setMainButton);
    setMainButton.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            Lizzie.config.setMain = !Lizzie.config.setMain;
            Lizzie.config.uiConfig.put("setMain", Lizzie.config.setMain);
            LizzieFrame.toolbar.reSetButtonLocation();
          }
        });

    final JFontCheckBoxMenuItem backMain =
        new JFontCheckBoxMenuItem(resourceBundle.getString("BottomToolbar.backMain")); // "返回主分支");
    customToolbarItem.add(backMain);
    backMain.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            Lizzie.config.backMain = !Lizzie.config.backMain;
            Lizzie.config.uiConfig.put("backMain", Lizzie.config.backMain);
            LizzieFrame.toolbar.reSetButtonLocation();
          }
        });

    final JFontCheckBoxMenuItem clearButton =
        new JFontCheckBoxMenuItem(
            resourceBundle.getString("BottomToolbar.clearButton")); // "清空棋盘");
    customToolbarItem.add(clearButton);
    clearButton.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            Lizzie.config.clearButton = !Lizzie.config.clearButton;
            Lizzie.config.uiConfig.put("clearButton", Lizzie.config.clearButton);
            LizzieFrame.toolbar.reSetButtonLocation();
          }
        });

    final JFontCheckBoxMenuItem deleteMove =
        new JFontCheckBoxMenuItem(resourceBundle.getString("BottomToolbar.deleteMove")); // "删除");
    customToolbarItem.add(deleteMove);
    deleteMove.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            Lizzie.config.deleteMove = !Lizzie.config.deleteMove;
            Lizzie.config.uiConfig.put("deleteMove", Lizzie.config.deleteMove);
            LizzieFrame.toolbar.reSetButtonLocation();
          }
        });

    final JFontCheckBoxMenuItem moveRank =
        new JFontCheckBoxMenuItem(resourceBundle.getString("BottomToolbar.moveRank")); // "手数");
    customToolbarItem.add(moveRank);
    moveRank.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            Lizzie.config.moveRank = !Lizzie.config.moveRank;
            Lizzie.config.uiConfig.put("move-rank", Lizzie.config.moveRank);
            LizzieFrame.toolbar.reSetButtonLocation();
          }
        });

    final JFontCheckBoxMenuItem move =
        new JFontCheckBoxMenuItem(resourceBundle.getString("BottomToolbar.move")); // "手数");
    customToolbarItem.add(move);
    move.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            Lizzie.config.move = !Lizzie.config.move;
            Lizzie.config.uiConfig.put("move", Lizzie.config.move);
            LizzieFrame.toolbar.reSetButtonLocation();
          }
        });

    final JFontCheckBoxMenuItem coords =
        new JFontCheckBoxMenuItem(resourceBundle.getString("BottomToolbar.coords")); // "坐标");
    customToolbarItem.add(coords);
    coords.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            Lizzie.config.coords = !Lizzie.config.coords;
            Lizzie.config.uiConfig.put("coords", Lizzie.config.coords);
            LizzieFrame.toolbar.reSetButtonLocation();
          }
        });

    final JFontCheckBoxMenuItem autoPlay =
        new JFontCheckBoxMenuItem(resourceBundle.getString("BottomToolbar.autoPlay")); // "自动播放");
    customToolbarItem.add(autoPlay);
    autoPlay.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            Lizzie.config.autoPlay = !Lizzie.config.autoPlay;
            Lizzie.config.uiConfig.put("autoPlay", Lizzie.config.autoPlay);
            LizzieFrame.toolbar.reSetButtonLocation();
          }
        });

    customToolbarItem.addMenuListener(
        new MenuListener() {

          public void menuSelected(MenuEvent e) {
            if (Lizzie.config.share) shareButton.setState(true);
            else shareButton.setState(false);
            if (Lizzie.config.liveButton) liveButton.setState(true);
            else liveButton.setState(false);
            if (Lizzie.config.kataEstimate) kataEstimateButton.setState(true);
            else kataEstimateButton.setState(false);
            if (Lizzie.config.batchOpen) batchOpen.setState(true);
            else batchOpen.setState(false);
            if (Lizzie.config.openfile) openfile.setState(true);
            else openfile.setState(false);
            if (Lizzie.config.savefile) savefile.setState(true);
            else savefile.setState(false);
            if (Lizzie.config.analyzeList) analyzeList.setState(true);
            else analyzeList.setState(false);

            if (Lizzie.config.refresh) refresh.setState(true);
            else refresh.setState(false);
            if (Lizzie.config.analyse) analyse.setState(true);
            else analyse.setState(false);
            if (Lizzie.config.tryPlay) tryPlay.setState(true);
            else tryPlay.setState(false);
            if (Lizzie.config.setMain) setMainButton.setState(true);
            else setMainButton.setState(false);
            if (Lizzie.config.backMain) backMain.setState(true);
            else backMain.setState(false);
            if (Lizzie.config.flashAnalyze) flashAnalyze.setState(true);
            else flashAnalyze.setState(false);
            if (Lizzie.config.clearButton) clearButton.setState(true);
            else clearButton.setState(false);
            if (Lizzie.config.countButton) countButton.setState(true);
            else countButton.setState(false);
            if (Lizzie.config.finalScore) finalScore.setState(true);
            else finalScore.setState(false);
            if (Lizzie.config.heatMap) heatMap.setState(true);
            else heatMap.setState(false);
            if (Lizzie.config.badMoves) badMoves.setState(true);
            else badMoves.setState(false);
            if (Lizzie.config.move) move.setState(true);
            else move.setState(false);
            if (Lizzie.config.coords) coords.setState(true);
            else coords.setState(false);
            if (Lizzie.config.autoPlay) autoPlay.setState(true);
            else autoPlay.setState(false);
            if (Lizzie.config.deleteMove) deleteMove.setState(true);
            else deleteMove.setState(false);
            if (Lizzie.config.moveRank) moveRank.setState(true);
            else moveRank.setState(false);
          }

          @Override
          public void menuDeselected(MenuEvent e) {
            // TODO Auto-generated method stub
          }

          @Override
          public void menuCanceled(MenuEvent e) {
            // TODO Auto-generated method stub

          }
        });

    final JFontMenu helpMenu = new JFontMenu(resourceBundle.getString("Menu.other"));
    helpMenu.setForeground(Color.BLACK);
    // helpMenu.setFont(headFont);
    this.add(helpMenu);

    final JFontMenuItem checkUpdate =
        new JFontMenuItem(resourceBundle.getString("Menu.checkUpdate")); // ("检查更新");
    helpMenu.add(checkUpdate);

    checkUpdate.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            Runnable runnable =
                new Runnable() {
                  public void run() {
                    SocketCheckVersion socketCheckVersion = new SocketCheckVersion();
                    socketCheckVersion.SocketCheckVersion(false);
                  }
                };
            Thread thread = new Thread(runnable);
            thread.start();
          }
        });

    final JFontMenuItem introduction =
        new JFontMenuItem(resourceBundle.getString("Menu.introduction")); // ("简介");
    helpMenu.add(introduction);

    introduction.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            Lizzie.frame.openHelp();
          }
        });

    final JFontMenuItem about =
        new JFontMenuItem(resourceBundle.getString("Menu.about")); // ("关于");
    helpMenu.add(about);

    about.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            Lizzie.frame.openConfigDialog2(2);
          }
        });

    quickLinks = new JFontMenu(resourceBundle.getString("Menu.quickLinks")); // ("快速启动");
    quickLinks.setForeground(Color.BLACK);
    // quickLinks.setFont(headFont);
    this.add(quickLinks);
    updateFastLinks();

    final JFontMenu settings = new JFontMenu(resourceBundle.getString("Menu.settings"));
    settings.setForeground(Color.BLACK);
    // settings.setFont(headFont);
    this.add(settings);

    final JFontMenuItem engineConfig =
        new JFontMenuItem(resourceBundle.getString("Menu.engineConfig")); // ("引擎(Alt+X)");
    settings.add(engineConfig);
    engineConfig.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            LizzieFrame.openMoreEngineDialog();
          }
        });

    final JFontMenuItem engineRules =
        new JFontMenuItem(resourceBundle.getString("Menu.engineRules")); // ("实时修改引擎规则(KataGo)(D)");
    settings.add(engineRules);

    engineRules.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            Lizzie.frame.setRules();
          }
        });

    final JFontMenuItem engineParameters =
        new JFontMenuItem(
            resourceBundle.getString("Menu.engineParameters")); // ("实时修改引擎参数(Alt+D)");
    settings.add(engineParameters);

    engineParameters.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            Lizzie.frame.setLzSaiEngine();
          }
        });

    settings.addSeparator();

    final JFontMenuItem initSettings =
        new JFontMenuItem(resourceBundle.getString("Menu.initSettings")); // ("初始化设置");
    settings.add(initSettings);

    initSettings.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            Lizzie.openFirstUseSettings(false);
          }
        });

    final JFontMenuItem comprehensiveSettings =
        new JFontMenuItem(
            resourceBundle.getString("Menu.comprehensiveSettings")); // ("综合设置(Shift+X)");
    settings.add(comprehensiveSettings);

    comprehensiveSettings.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            Lizzie.frame.openConfigDialog2(0);
          }
        });

    final JFontMenuItem theme =
        new JFontMenuItem(resourceBundle.getString("Menu.theme")); // ("主题");
    settings.add(theme);

    theme.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            Lizzie.frame.openConfigDialog2(1);
          }
        });

    settings.addSeparator();

    final JFontMenu language = new JFontMenu(resourceBundle.getString("menu.language"));
    settings.add(language);

    final JFontCheckBoxMenuItem languageDefault =
        new JFontCheckBoxMenuItem(resourceBundle.getString("menu.language.default"));
    language.add(languageDefault);
    languageDefault.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            Lizzie.config.useLanguage = 0;
            Lizzie.config.uiConfig.put("use-language", Lizzie.config.useLanguage);
            Utils.showMsg(resourceBundle.getString("Lizzie.hint.restart"));
          }
        });

    final JFontCheckBoxMenuItem languageZH = new JFontCheckBoxMenuItem("中文");
    language.add(languageZH);
    languageZH.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            Lizzie.config.useLanguage = 1;
            Lizzie.config.uiConfig.put("use-language", Lizzie.config.useLanguage);
            Utils.showMsg("重新打开Lizzie后设置生效!");
          }
        });

    final JFontCheckBoxMenuItem languageEN = new JFontCheckBoxMenuItem("English");
    language.add(languageEN);
    languageEN.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            Lizzie.config.useLanguage = 2;
            Lizzie.config.uiConfig.put("use-language", Lizzie.config.useLanguage);
            Utils.showMsg("Please restart Lizzie to apply changes.");
          }
        });

    final JFontMenu frameFontSize =
        new JFontMenu(resourceBundle.getString("menu.frameFontSize")); // 界面字体大小
    settings.add(frameFontSize);

    final JFontCheckBoxMenuItem frameFontSizeSmall =
        new JFontCheckBoxMenuItem(resourceBundle.getString("menu.frameFontSizeSmall"));
    frameFontSize.add(frameFontSizeSmall);

    frameFontSizeSmall.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            Lizzie.frame.setFrameFontSize(0);
          }
        });

    final JFontCheckBoxMenuItem frameFontSizeMiddle =
        new JFontCheckBoxMenuItem(resourceBundle.getString("menu.frameFontSizeMiddle"));
    frameFontSize.add(frameFontSizeMiddle);

    frameFontSizeMiddle.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            Lizzie.frame.setFrameFontSize(1);
          }
        });

    final JFontCheckBoxMenuItem frameFontSizeBig =
        new JFontCheckBoxMenuItem(resourceBundle.getString("menu.frameFontSizeBig"));
    frameFontSize.add(frameFontSizeBig);

    frameFontSizeBig.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            Lizzie.frame.setFrameFontSize(2);
          }
        });

    final JFontCheckBoxMenuItem frameFontSizeOther =
        new JFontCheckBoxMenuItem(resourceBundle.getString("menu.frameFontSizeOther"));
    frameFontSize.add(frameFontSizeOther);

    frameFontSizeOther.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            SetFrameFontSize setFrameFontSize = new SetFrameFontSize();
            setFrameFontSize.setVisible(true);
          }
        });

    final JFontMenu frameLooks = new JFontMenu(resourceBundle.getString("menu.frameLooks")); // 界面外观
    settings.add(frameLooks);

    final JFontCheckBoxMenuItem frameLooksSystem =
        new JFontCheckBoxMenuItem(resourceBundle.getString("menu.frameLooksSystem"));
    frameLooks.add(frameLooksSystem);

    frameLooksSystem.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            Lizzie.config.useJavaLooks = false;
            Lizzie.config.uiConfig.put("use-java-looks", Lizzie.config.useJavaLooks);
            Utils.showMsg(resourceBundle.getString("Lizzie.hint.restart"));
          }
        });

    final JFontCheckBoxMenuItem frameLooksJava =
        new JFontCheckBoxMenuItem(resourceBundle.getString("menu.frameLooksJava"));
    frameLooks.add(frameLooksJava);

    frameLooksJava.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            Lizzie.config.useJavaLooks = true;
            Lizzie.config.uiConfig.put("use-java-looks", Lizzie.config.useJavaLooks);
            Utils.showMsg(resourceBundle.getString("Lizzie.hint.restart"));
          }
        });

    settings.addSeparator();

    final JFontCheckBoxMenuItem playSound =
        new JFontCheckBoxMenuItem(resourceBundle.getString("Menu.playSound"));
    settings.add(playSound);

    playSound.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            Lizzie.config.playSound = !Lizzie.config.playSound;
            Lizzie.config.uiConfig.put("play-sound", Lizzie.config.playSound);
          }
        });

    final JFontCheckBoxMenuItem notPlaySoundInSync =
        new JFontCheckBoxMenuItem(resourceBundle.getString("Menu.notPlaySoundInSync"));
    settings.add(notPlaySoundInSync);
    notPlaySoundInSync.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            Lizzie.config.notPlaySoundInSync = !Lizzie.config.notPlaySoundInSync;
            Lizzie.config.uiConfig.put("not-play-sound-insync", Lizzie.config.notPlaySoundInSync);
          }
        });

    settings.addMenuListener(
        new MenuListener() {

          public void menuSelected(MenuEvent e) {
            if (Lizzie.config.playSound) playSound.setState(true);
            else playSound.setState(false);
            if (Lizzie.config.notPlaySoundInSync) notPlaySoundInSync.setState(true);
            else notPlaySoundInSync.setState(false);
            if (Lizzie.config.useLanguage == 0) languageDefault.setState(true);
            else languageDefault.setState(false);
            if (Lizzie.config.useLanguage == 1) languageZH.setState(true);
            else languageZH.setState(false);
            if (Lizzie.config.useLanguage == 2) languageEN.setState(true);
            else languageEN.setState(false);
            if (Lizzie.config.useJavaLooks) {
              frameLooksJava.setState(true);
              frameLooksSystem.setSelected(false);
            } else {
              frameLooksJava.setState(false);
              frameLooksSystem.setSelected(true);
            }
            if (Config.frameFontSize == 12) {
              frameFontSizeSmall.setState(true);
              frameFontSizeMiddle.setState(false);
              frameFontSizeBig.setState(false);
              frameFontSizeOther.setState(false);
            } else if (Config.frameFontSize == 16) {
              frameFontSizeSmall.setState(false);
              frameFontSizeMiddle.setState(true);
              frameFontSizeBig.setState(false);
              frameFontSizeOther.setState(false);
            } else if (Config.frameFontSize == 20) {
              frameFontSizeSmall.setState(false);
              frameFontSizeMiddle.setState(false);
              frameFontSizeBig.setState(true);
              frameFontSizeOther.setState(false);
            } else {
              frameFontSizeSmall.setState(false);
              frameFontSizeMiddle.setState(false);
              frameFontSizeBig.setState(false);
              frameFontSizeOther.setState(true);
            }
          }

          @Override
          public void menuDeselected(MenuEvent e) {
            // TODO Auto-generated method stub
          }

          @Override
          public void menuCanceled(MenuEvent e) {
            // TODO Auto-generated method stub

          }
        });

    engineMenu = new JFontMenu(resourceBundle.getString("Menu.noEngine"));
    engineMenu.setForeground(Color.BLACK);
    // headFont = new Font(Config.sysDefaultFontName, Font.BOLD,
    // Math.max(Lizzie.config.frameFontSize, 15));
    engineMenu.setFont(
        new Font(Config.sysDefaultFontName, Font.BOLD, Math.max(Config.frameFontSize, 15)));
    this.add(engineMenu);

    engineMenu2 = new JFontMenu(resourceBundle.getString("Menu.noEngine"));
    engineMenu2.setForeground(Color.BLACK);
    engineMenu2.setFont(
        new Font(Config.sysDefaultFontName, Font.BOLD, Math.max(Config.frameFontSize, 15)));
    //   headFont = new Font(Config.sysDefaultFontName, Font.PLAIN,
    // Math.max(Lizzie.config.allFontSize, 12));
    this.add(engineMenu2);

    icon = new ImageIcon();
    try {
      icon.setImage(ImageIO.read(getClass().getResourceAsStream("/assets/playing.png")));
      // icon.setImage(ImageIO.read(getClass().getResourceAsStream("/assets/run.png")));
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

    ready = new ImageIcon();
    try {
      ready.setImage(ImageIO.read(getClass().getResourceAsStream("/assets/ready.png")));
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

    icon2 = new ImageIcon();
    try {
      icon2.setImage(ImageIO.read(getClass().getResourceAsStream("/assets/playing2.png")));
      // icon.setImage(ImageIO.read(getClass().getResourceAsStream("/assets/run.png")));
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

    ready2 = new ImageIcon();
    try {
      ready2.setImage(ImageIO.read(getClass().getResourceAsStream("/assets/ready2.png")));
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

    stop = new ImageIcon();
    try {
      stop.setImage(ImageIO.read(getClass().getResourceAsStream("/assets/stop.png")));
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

    updateEngineMenuone();
    ArrayList<EngineData> engineData = Utils.getEngineData();
    for (int i = 0; i < engineData.size(); i++) {
      EngineData engineDt = engineData.get(i);
      LizzieFrame.toolbar.enginePkBlack.addItem("[" + (i + 1) + "]" + engineDt.name);
      LizzieFrame.toolbar.enginePkWhite.addItem("[" + (i + 1) + "]" + engineDt.name);
    }

    engineMenu.addSeparator();

    restartCurrentEngine =
        new JFontMenuItem(resourceBundle.getString("Menu.restartCurrentEngine")); // ("重启当前引擎");
    restartCurrentEngine.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            Lizzie.engineManager.reStartEngine();
          }
        });
    engineMenu.add(restartCurrentEngine);

    shutdownEngine = new JFontMenu(resourceBundle.getString("Menu.shutdownEngine")); // ("关闭引擎");
    engineMenu.add(shutdownEngine);

    shutdownCurrentEngine =
        new JFontMenuItem(resourceBundle.getString("Menu.shutdownCurrentEngine")); // ("关闭当前引擎");
    shutdownCurrentEngine.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            Lizzie.engineManager.killThisEngines();
            engineMenu.setText(resourceBundle.getString("Menu.noEngine")); //
          }
        });
    shutdownEngine.add(shutdownCurrentEngine);

    shutdownOtherEngine =
        new JFontMenuItem(resourceBundle.getString("Menu.shutdownOtherEngine")); // ("关闭当前以外引擎");
    shutdownOtherEngine.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            try {
              Lizzie.engineManager.killOtherEngines();
            } catch (Exception ex) {
              ex.printStackTrace();
            }
            for (int i = 0; i < engine.length; i++) {
              engine[i].setIcon(null);
            }
            engine[Lizzie.leelaz.currentEngineN()].setIcon(icon);
          }
        });
    shutdownEngine.add(shutdownOtherEngine);

    shutdownAllEngine =
        new JFontMenuItem(resourceBundle.getString("Menu.shutdownAllEngine")); // ("关闭所有引擎");
    shutdownAllEngine.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            try {
              Lizzie.engineManager.killAllEngines();
            } catch (Exception ex) {
              ex.printStackTrace();
            }
            for (int i = 0; i < engine.length; i++) {
              engine[i].setIcon(null);
            }
          }
        });
    shutdownEngine.add(shutdownAllEngine);

    updateEngineMenuone2();
    engineMenu2.addSeparator();
    restartCurrentEngine2 =
        new JFontMenuItem(resourceBundle.getString("Menu.restartCurrentEngine")); // ("重启当前引擎");
    restartCurrentEngine2.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            Lizzie.engineManager.reStartEngine2();
          }
        });
    engineMenu2.add(restartCurrentEngine2);

    shutdownCurrentEngine2 =
        new JFontMenuItem(resourceBundle.getString("Menu.shutdownCurrentEngine")); // ("关闭当前引擎");
    shutdownCurrentEngine2.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            Lizzie.engineManager.killThisEngines2();
            engineMenu2.setText(resourceBundle.getString("Menu.noEngine"));
          }
        });
    engineMenu2.add(shutdownCurrentEngine2);
    // }
    komiPanel = new JPanel();
    komiPanel.setLayout(null);
    lblKomiSpinner = new JFontLabel(resourceBundle.getString("Menu.komi")); // ("贴目:");
    txtKomi = new JFontTextField();
    txtKomi.setDocument(new KomiDocument(true));
    txtKomi.setText(String.valueOf(Lizzie.board.getHistory().getGameInfo().getKomi()));
    txtKomi.setHorizontalAlignment(JFontTextField.RIGHT);
    // txtKomi.setFocusable(false);
    txtKomi.addKeyListener(
        new KeyAdapter() {
          public void keyReleased(KeyEvent e) {
            // if (e.getKeyChar() == KeyEvent.VK_ENTER) // 按回车键执行相应操作;
            // {
            try {
              if (txtKomi.getText().trim().equals("-") || txtKomi.getText().trim().equals(""))
                return;
              double oriKomi = Lizzie.board.getHistory().getGameInfo().getKomi();
              double newKomi = Double.parseDouble(txtKomi.getText());
              if (newKomi == oriKomi) return;
              if (EngineManager.isEngineGame) {
                Lizzie.engineManager
                    .engineList
                    .get(EngineManager.engineGameInfo.firstEngineIndex)
                    .sendCommand("komi " + (newKomi == 0.0 ? "0" : newKomi));
                Lizzie.engineManager
                    .engineList
                    .get(EngineManager.engineGameInfo.secondEngineIndex)
                    .sendCommand("komi " + (newKomi == 0.0 ? "0" : newKomi));
                if (Lizzie.leelaz.isPondering()) Lizzie.leelaz.ponder();
                Lizzie.board.getHistory().getGameInfo().setKomiNoMenu(newKomi);
              } else Lizzie.leelaz.komiNoMenu(newKomi);
              Lizzie.board.getHistory().getGameInfo().changeKomi();
              Lizzie.frame.refresh();
            } catch (Exception es) {
              txtKomi.setText(String.valueOf(Lizzie.board.getHistory().getGameInfo().getKomi()));
            }
            //  }
          }
        });

    txtKomi.addFocusListener(
        new FocusListener() {
          @Override
          public void focusGained(FocusEvent e) {
            // TODO Auto-generated method stub

          }

          @Override
          public void focusLost(FocusEvent e) {
            // TODO Auto-generated method stub
            txtKomi.setText(String.valueOf(Lizzie.board.getHistory().getGameInfo().getKomi()));
          }
        });

    txtKomi.addMouseListener(
        new MouseAdapter() {
          @Override
          public void mouseExited(MouseEvent e) {
            // TODO Auto-generated method stub
            try {
              double oriKomi = Lizzie.board.getHistory().getGameInfo().getKomi();
              double newKomi = Double.parseDouble(txtKomi.getText());
              if (newKomi == oriKomi) return;
              if (EngineManager.isEngineGame) {
                Lizzie.engineManager
                    .engineList
                    .get(EngineManager.engineGameInfo.firstEngineIndex)
                    .sendCommand("komi " + (newKomi == 0.0 ? "0" : newKomi));
                Lizzie.engineManager
                    .engineList
                    .get(EngineManager.engineGameInfo.secondEngineIndex)
                    .sendCommand("komi " + (newKomi == 0.0 ? "0" : newKomi));
                if (Lizzie.leelaz.isPondering()) Lizzie.leelaz.ponder();
                Lizzie.board.getHistory().getGameInfo().setKomiNoMenu(newKomi);
              } else Lizzie.leelaz.komiNoMenu(newKomi);
              Lizzie.board.getHistory().getGameInfo().changeKomi();
              Lizzie.frame.refresh();
            } catch (Exception es) {
              txtKomi.setText(String.valueOf(Lizzie.board.getHistory().getGameInfo().getKomi()));
              es.printStackTrace();
            }
          }
        });

    lblPDASpinner = new JFontLabel(resourceBundle.getString("Menu.lblPDA"));
    txtPDA = new JFontTextField();
    txtPDA.setHorizontalAlignment(JFontTextField.RIGHT);
    txtPDA.setFocusable(false);

    ImageIcon komiUp;
    komiUp = new ImageIcon();
    try {
      komiUp.setImage(ImageIO.read(getClass().getResourceAsStream("/assets/smallUp.png")));
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    btnKomiUp = new JFontButton(komiUp);
    btnKomiUp.setFocusable(false);

    ImageIcon komiDown;
    komiDown = new ImageIcon();
    try {
      komiDown.setImage(ImageIO.read(getClass().getResourceAsStream("/assets/smallDown.png")));
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    btnKomiDown = new JFontButton(komiDown);
    btnKomiDown.setFocusable(false);

    btnKomiUp.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            if (txtKomi.getText().trim().equals("")) return;
            double newKomi = Double.parseDouble(txtKomi.getText().trim()) + 0.5;
            if (EngineManager.isEngineGame) {
              Lizzie.engineManager
                  .engineList
                  .get(EngineManager.engineGameInfo.firstEngineIndex)
                  .sendCommand("komi " + (newKomi == 0.0 ? "0" : newKomi));
              Lizzie.engineManager
                  .engineList
                  .get(EngineManager.engineGameInfo.secondEngineIndex)
                  .sendCommand("komi " + (newKomi == 0.0 ? "0" : newKomi));
              if (Lizzie.leelaz.isPondering()) Lizzie.leelaz.ponder();
              Lizzie.board.getHistory().getGameInfo().setKomi(newKomi);
            } else Lizzie.leelaz.komi(newKomi);
            Lizzie.board.getHistory().getGameInfo().changeKomi();
            Lizzie.frame.refresh();
          }
        });

    btnKomiDown.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            if (txtKomi.getText().trim().equals("")) return;
            double newKomi = Double.parseDouble(txtKomi.getText().trim()) - 0.5;
            if (EngineManager.isEngineGame) {
              Lizzie.engineManager
                  .engineList
                  .get(EngineManager.engineGameInfo.firstEngineIndex)
                  .sendCommand("komi " + (newKomi == 0.0 ? "0" : newKomi));
              Lizzie.engineManager
                  .engineList
                  .get(EngineManager.engineGameInfo.secondEngineIndex)
                  .sendCommand("komi " + (newKomi == 0.0 ? "0" : newKomi));
              if (Lizzie.leelaz.isPondering()) Lizzie.leelaz.ponder();
              Lizzie.board.getHistory().getGameInfo().setKomi(newKomi);
            } else Lizzie.leelaz.komi(newKomi);
            Lizzie.board.getHistory().getGameInfo().changeKomi();
            Lizzie.frame.refresh();
          }
        });

    if (!Lizzie.config.shouldWidenCheckBox && Config.isScaled) {
      btnKomiUp.addMouseListener(
          new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
              btnKomiUp.setBounds(
                  Lizzie.config.isFrameFontSmall()
                      ? 67
                      : (Lizzie.config.isFrameFontMiddle() ? 80 : 97),
                  Lizzie.config.isFrameFontSmall()
                      ? 0
                      : (Lizzie.config.isFrameFontMiddle() ? 2 : 3),
                  13,
                  Lizzie.config.isFrameFontSmall()
                      ? 9
                      : (Lizzie.config.isFrameFontMiddle() ? 10 : 12));
              btnKomiUp.setBackground(new Color(158, 158, 158));
              btnKomiUp.setBorderPainted(false);
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
              btnKomiUp.setBounds(
                  Lizzie.config.isFrameFontSmall()
                      ? 66
                      : (Lizzie.config.isFrameFontMiddle() ? 79 : 96),
                  Lizzie.config.isFrameFontSmall()
                      ? -1
                      : (Lizzie.config.isFrameFontMiddle() ? 1 : 2),
                  15,
                  Lizzie.config.isFrameFontSmall()
                      ? 11
                      : (Lizzie.config.isFrameFontMiddle() ? 12 : 14));
              btnKomiUp.setBackground(komiPanel.getBackground());
              btnKomiUp.setBorderPainted(true);
            }
          });

      btnKomiDown.addMouseListener(
          new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
              btnKomiDown.setBounds(
                  Lizzie.config.isFrameFontSmall()
                      ? 67
                      : (Lizzie.config.isFrameFontMiddle() ? 80 : 97),
                  Lizzie.config.isFrameFontSmall()
                      ? 10
                      : (Lizzie.config.isFrameFontMiddle() ? 13 : 16),
                  13,
                  Lizzie.config.isFrameFontSmall()
                      ? 9
                      : (Lizzie.config.isFrameFontMiddle() ? 10 : 12));
              btnKomiDown.setBackground(new Color(158, 158, 158));
              btnKomiDown.setBorderPainted(false);
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
              btnKomiDown.setBounds(
                  Lizzie.config.isFrameFontSmall()
                      ? 66
                      : (Lizzie.config.isFrameFontMiddle() ? 79 : 96),
                  Lizzie.config.isFrameFontSmall()
                      ? 9
                      : (Lizzie.config.isFrameFontMiddle() ? 12 : 15),
                  15,
                  Lizzie.config.isFrameFontSmall()
                      ? 11
                      : (Lizzie.config.isFrameFontMiddle() ? 12 : 14));
              btnKomiDown.setBackground(komiPanel.getBackground());
              btnKomiDown.setBorderPainted(true);
            }
          });
    }

    more2 = new JFontButton("...");
    more2.setFocusable(false);
    more2.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            if (setPda == null) {
              setPda = new SetKataPDA();
              setPda.setVisible(true);
            } else {
              setPda.dispose();
              setPda = new SetKataPDA();
              setPda.setVisible(true);
            }
          }
        });
    setRules = new JFontButton(resourceBundle.getString("Menu.rulesBtn"));
    setRules.setFocusable(false);
    setRules.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            Lizzie.frame.setRules();
          }
        });

    setLzSaiParam = new JFontButton(resourceBundle.getString("Menu.paramsBtn"));
    setLzSaiParam.setFocusable(false);
    setLzSaiParam.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            Lizzie.frame.setLzSaiEngine();
          }
        });

    setBoardSize = new JFontButton(resourceBundle.getString("Menu.gobanBtn"));
    setBoardSize.setFocusable(false);
    setBoardSize.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            SetBoardSize setBoardSize = new SetBoardSize();
            setBoardSize.setVisible(true);
          }
        });

    saveLoad = new JFontButton(resourceBundle.getString("Menu.saveBtn"));
    saveLoad.setFocusable(false);
    saveLoad.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            Lizzie.frame.showTempGamePanel();
          }
        });

    btnKomiUp.setMargin(new Insets(0, 0, (Lizzie.config.useJavaLooks ? 3 : 2), 0));
    btnKomiDown.setMargin(new Insets((Lizzie.config.useJavaLooks ? 3 : 2), 0, 0, 0));

    // btnKomiUp.setBorder(BorderFactory.createEmptyBorder(0,0,0,0));
    //  btnKomiDown.setBorder(BorderFactory.createEmptyBorder(0,0,0,0));
    // more.setMargin(new Insets(0, 0, 4, 1));
    // more2.setMargin(new Insets(0, 0, 4, 1));
    // more.setBounds(Lizzie.config.isFrameFontSmall()?80:(Lizzie.config.isFrameFontMiddle()?86:92),
    // -1, 20, 20);

    chkWRN = new JFontCheckBox();
    chkWRN.setToolTipText(resourceBundle.getString("Menu.chkWRN.toolTopText"));
    chkWRN.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            Lizzie.board.clearbestmovesafter(Lizzie.board.getHistory().getStart());
            if (chkWRN.isSelected()) {
              txtWRN.setEnabled(true);
              double wrn = 0;
              try {
                wrn = Double.parseDouble(txtWRN.getText());
              } catch (NumberFormatException s) {
                // TODO Auto-generated catch block
              }
              Lizzie.config.txtKataEngineWRN = String.valueOf(wrn);
              if (EngineManager.isEngineGame) {
                Lizzie.engineManager
                    .engineList
                    .get(EngineManager.engineGameInfo.firstEngineIndex)
                    .sendCommand("kata-set-param analysisWideRootNoise " + wrn);
                Lizzie.engineManager
                    .engineList
                    .get(EngineManager.engineGameInfo.secondEngineIndex)
                    .sendCommand("kata-set-param analysisWideRootNoise " + wrn);
              } else Lizzie.leelaz.sendCommand("kata-set-param analysisWideRootNoise " + wrn);
              if (Lizzie.leelaz.isPondering()) Lizzie.leelaz.ponder();
            } else {
              txtWRN.setEnabled(false);
              if (EngineManager.isEngineGame) {
                Lizzie.engineManager
                    .engineList
                    .get(EngineManager.engineGameInfo.firstEngineIndex)
                    .sendCommand("kata-set-param analysisWideRootNoise 0");
                Lizzie.engineManager
                    .engineList
                    .get(EngineManager.engineGameInfo.secondEngineIndex)
                    .sendCommand("kata-set-param analysisWideRootNoise 0");
              } else Lizzie.leelaz.sendCommand("kata-set-param analysisWideRootNoise 0");
              if (Lizzie.leelaz.isPondering()) Lizzie.leelaz.ponder();
            }
            Lizzie.config.chkKataEngineWRN = chkWRN.isSelected();
          }
        });
    // chkWRN.setSelected(Lizzie.config.autoLoadKataEngineWRN);
    lblWRN = new JFontLabel(resourceBundle.getString("Menu.lblWRN")); // ("分析广度拓展");
    txtWRN = new JFontTextField();
    txtWRN.setToolTipText(resourceBundle.getString("Menu.chkWRN.toolTopText"));
    txtWRN.setDocument(new DoubleDocument());
    txtWRN.setEnabled(false);
    // if (!Lizzie.config.autoLoadKataEngineWRN) txtWRN.setEnabled(false);
    // txtWRN.setText(Lizzie.config.txtKataEngineWRN ));

    Document dt3 = txtWRN.getDocument();
    dt3.addDocumentListener(
        new DocumentListener() {
          public void insertUpdate(DocumentEvent e) {
            if (ShouldIgnoreDtChange) return;
            double wrn = 0;
            boolean error = false;
            try {
              wrn = Double.parseDouble(txtWRN.getText());
            } catch (NumberFormatException s) {
              // TODO Auto-generated catch block
              error = true;
            }
            if (error || wrn < 0 || wrn > 2) txtWRN.setBackground(Color.RED);
            else txtWRN.setBackground(Color.WHITE);
            Lizzie.config.txtKataEngineWRN = String.valueOf(wrn);
            if (EngineManager.isEngineGame || EngineManager.isPreEngineGame) {
              Lizzie.engineManager
                  .engineList
                  .get(EngineManager.engineGameInfo.firstEngineIndex)
                  .sendCommand("kata-set-param analysisWideRootNoise " + wrn);
              Lizzie.engineManager
                  .engineList
                  .get(EngineManager.engineGameInfo.secondEngineIndex)
                  .sendCommand("kata-set-param analysisWideRootNoise " + wrn);
            } else Lizzie.leelaz.sendCommand("kata-set-param analysisWideRootNoise " + wrn);
            Lizzie.board.clearbestmovesafter(Lizzie.board.getHistory().getStart());
            if (Lizzie.leelaz.isPondering()) Lizzie.leelaz.ponder();
            Lizzie.config.uiConfig.put("txt-kata-engine-wrn", Lizzie.config.txtKataEngineWRN);
          }

          public void removeUpdate(DocumentEvent e) {
            if (ShouldIgnoreDtChange) return;
            double wrn = 0;
            boolean error = false;
            try {
              wrn = Double.parseDouble(txtWRN.getText());
            } catch (NumberFormatException s) {
              // TODO Auto-generated catch block
              error = true;
            }
            if (error || wrn < 0 || wrn > 2) txtWRN.setBackground(Color.RED);
            else txtWRN.setBackground(Color.WHITE);
            Lizzie.config.txtKataEngineWRN = String.valueOf(wrn);
            if (EngineManager.isEngineGame || EngineManager.isPreEngineGame) {
              Lizzie.engineManager
                  .engineList
                  .get(EngineManager.engineGameInfo.firstEngineIndex)
                  .sendCommand("kata-set-param analysisWideRootNoise " + wrn);
              Lizzie.engineManager
                  .engineList
                  .get(EngineManager.engineGameInfo.secondEngineIndex)
                  .sendCommand("kata-set-param analysisWideRootNoise " + wrn);
            } else Lizzie.leelaz.sendCommand("kata-set-param analysisWideRootNoise " + wrn);
            Lizzie.board.clearbestmovesafter(Lizzie.board.getHistory().getStart());
            if (Lizzie.leelaz.isPondering()) Lizzie.leelaz.ponder();
            Lizzie.config.uiConfig.put("txt-kata-engine-wrn", Lizzie.config.txtKataEngineWRN);
          }

          public void changedUpdate(DocumentEvent e) {
            if (ShouldIgnoreDtChange) return;
            double wrn = 0;
            boolean error = false;
            try {
              wrn = Double.parseDouble(txtWRN.getText());
            } catch (NumberFormatException s) {
              // TODO Auto-generated catch block
              error = true;
            }
            if (error || wrn < 0 || wrn > 2) txtWRN.setBackground(Color.RED);
            else txtWRN.setBackground(Color.WHITE);
            Lizzie.config.txtKataEngineWRN = String.valueOf(wrn);
            if (EngineManager.isEngineGame || EngineManager.isPreEngineGame) {
              Lizzie.engineManager
                  .engineList
                  .get(EngineManager.engineGameInfo.firstEngineIndex)
                  .sendCommand("kata-set-param analysisWideRootNoise " + wrn);
              Lizzie.engineManager
                  .engineList
                  .get(EngineManager.engineGameInfo.secondEngineIndex)
                  .sendCommand("kata-set-param analysisWideRootNoise " + wrn);
            } else Lizzie.leelaz.sendCommand("kata-set-param analysisWideRootNoise " + wrn);
            Lizzie.board.clearbestmovesafter(Lizzie.board.getHistory().getStart());
            if (Lizzie.leelaz.isPondering()) Lizzie.leelaz.ponder();
            Lizzie.config.uiConfig.put("txt-kata-engine-wrn", Lizzie.config.txtKataEngineWRN);
          }
        });

    chkPDA = new JFontCheckBox();
    chkPDA.setToolTipText(resourceBundle.getString("Menu.chkPDA.toolTopText"));
    chkPDA.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            // Lizzie.board.clearbestmovesafter(Lizzie.board.getHistory().getStart());
            if (chkPDA.isSelected()) {
              txtGfPDA.setEnabled(true);
              double pda = 0;
              try {
                pda = Double.parseDouble(txtGfPDA.getText());
              } catch (NumberFormatException s) {
                // TODO Auto-generated catch block
              }
              Lizzie.config.txtKataEnginePDA = String.valueOf(pda);
              if (EngineManager.isEngineGame) {
                Lizzie.engineManager
                    .engineList
                    .get(EngineManager.engineGameInfo.firstEngineIndex)
                    .setPda(String.valueOf(pda));
                Lizzie.engineManager
                    .engineList
                    .get(EngineManager.engineGameInfo.secondEngineIndex)
                    .setPda(String.valueOf(pda));
              } else Lizzie.leelaz.setPda(String.valueOf(pda));
              if (Lizzie.leelaz.isPondering()) Lizzie.leelaz.ponder();
              Lizzie.config.uiConfig.put("txt-kata-engine-pda", Lizzie.config.txtKataEnginePDA);

            } else {
              txtGfPDA.setEnabled(false);
              if (EngineManager.isEngineGame) {
                Lizzie.engineManager
                    .engineList
                    .get(EngineManager.engineGameInfo.firstEngineIndex)
                    .setPda("0");
                Lizzie.engineManager
                    .engineList
                    .get(EngineManager.engineGameInfo.secondEngineIndex)
                    .setPda("0");
              } else Lizzie.leelaz.setPda("0");
              if (Lizzie.leelaz.isPondering()) Lizzie.leelaz.ponder();
            }
            Lizzie.config.chkKataEnginePDA = chkPDA.isSelected();
          }
        });

    lblGfPDA = new JFontLabel(resourceBundle.getString("Menu.lblPDA"));
    txtGfPDA = new JFontTextField();
    txtGfPDA.setEnabled(false);
    txtGfPDA.setToolTipText(resourceBundle.getString("Menu.chkPDA.toolTopText"));
    txtGfPDA.setDocument(new KomiDocument(false));

    Document dt4 = txtGfPDA.getDocument();
    dt4.addDocumentListener(
        new DocumentListener() {
          public void insertUpdate(DocumentEvent e) {
            if (ShouldIgnoreDtChange) return;
            double pda = 0;
            boolean error = false;
            try {
              pda = Double.parseDouble(txtGfPDA.getText());
            } catch (NumberFormatException s) {
              // TODO Auto-generated catch block
              error = true;
            }
            if (error || pda > 3 || pda < -3) {
              txtGfPDA.setBackground(Color.RED);
            } else txtGfPDA.setBackground(Color.WHITE);
            Lizzie.config.txtKataEnginePDA = String.valueOf(pda);
            if (EngineManager.isEngineGame || EngineManager.isPreEngineGame) {
              Lizzie.engineManager
                  .engineList
                  .get(EngineManager.engineGameInfo.firstEngineIndex)
                  .setPda(String.valueOf(pda));
              Lizzie.engineManager
                  .engineList
                  .get(EngineManager.engineGameInfo.secondEngineIndex)
                  .setPda(String.valueOf(pda));
            } else Lizzie.leelaz.setPda(String.valueOf(pda));
            //  Lizzie.board.clearbestmovesafter(Lizzie.board.getHistory().getStart());
            if (Lizzie.leelaz.isPondering()) Lizzie.leelaz.ponder();
            Lizzie.config.uiConfig.put("txt-kata-engine-pda", Lizzie.config.txtKataEnginePDA);
          }

          public void removeUpdate(DocumentEvent e) {
            if (ShouldIgnoreDtChange) return;
            double pda = 0;
            boolean error = false;
            try {
              pda = Double.parseDouble(txtGfPDA.getText());
            } catch (NumberFormatException s) {
              // TODO Auto-generated catch block
              error = true;
            }
            if (error || pda > 3 || pda < -3) {
              txtGfPDA.setBackground(Color.RED);
            } else txtGfPDA.setBackground(Color.WHITE);
            Lizzie.config.txtKataEnginePDA = String.valueOf(pda);
            if (EngineManager.isEngineGame || EngineManager.isPreEngineGame) {
              Lizzie.engineManager
                  .engineList
                  .get(EngineManager.engineGameInfo.firstEngineIndex)
                  .setPda(String.valueOf(pda));
              Lizzie.engineManager
                  .engineList
                  .get(EngineManager.engineGameInfo.secondEngineIndex)
                  .setPda(String.valueOf(pda));
            } else Lizzie.leelaz.setPda(String.valueOf(pda));
            //   Lizzie.board.clearbestmovesafter(Lizzie.board.getHistory().getStart());
            if (Lizzie.leelaz.isPondering()) Lizzie.leelaz.ponder();
            Lizzie.config.uiConfig.put("txt-kata-engine-pda", Lizzie.config.txtKataEnginePDA);
          }

          public void changedUpdate(DocumentEvent e) {
            if (ShouldIgnoreDtChange) return;
            double pda = 0;
            boolean error = false;
            try {
              pda = Double.parseDouble(txtGfPDA.getText());
            } catch (NumberFormatException s) {
              // TODO Auto-generated catch block
              error = true;
            }
            if (error || pda > 3 || pda < -3) {
              txtGfPDA.setBackground(Color.RED);
            } else txtGfPDA.setBackground(Color.WHITE);
            Lizzie.config.txtKataEnginePDA = String.valueOf(pda);
            if (EngineManager.isEngineGame || EngineManager.isPreEngineGame) {
              Lizzie.engineManager
                  .engineList
                  .get(EngineManager.engineGameInfo.firstEngineIndex)
                  .setPda(String.valueOf(pda));
              Lizzie.engineManager
                  .engineList
                  .get(EngineManager.engineGameInfo.secondEngineIndex)
                  .setPda(String.valueOf(pda));
            } else Lizzie.leelaz.setPda(String.valueOf(pda));
            //  Lizzie.board.clearbestmovesafter(Lizzie.board.getHistory().getStart());
            if (Lizzie.leelaz.isPondering()) Lizzie.leelaz.ponder();
            Lizzie.config.uiConfig.put("txt-kata-engine-pda", Lizzie.config.txtKataEnginePDA);
          }
        });

    showPda(false);
    //  updateMenuStatus();

    lblKomiSpinner.setBounds(
        1,
        Lizzie.config.isFrameFontSmall() ? 1 : (Lizzie.config.isFrameFontMiddle() ? 3 : 6),
        Lizzie.config.isFrameFontSmall() ? 35 : (Lizzie.config.isFrameFontMiddle() ? 40 : 47),
        18);
    txtKomi.setBounds(
        Lizzie.config.isFrameFontSmall() ? 31 : (Lizzie.config.isFrameFontMiddle() ? 39 : 49),
        Lizzie.config.isFrameFontSmall() ? 0 : (Lizzie.config.isFrameFontMiddle() ? 2 : 3),
        Lizzie.config.isFrameFontSmall() ? 35 : (Lizzie.config.isFrameFontMiddle() ? 40 : 47),
        Lizzie.config.isFrameFontSmall()
            ? (Lizzie.config.useJavaLooks ? 20 : 19)
            : (Lizzie.config.isFrameFontMiddle()
                ? (Lizzie.config.useJavaLooks ? 22 : 21)
                : (Lizzie.config.useJavaLooks ? 26 : 25)));
    btnKomiUp.setBounds(
        Lizzie.config.isFrameFontSmall() ? 66 : (Lizzie.config.isFrameFontMiddle() ? 79 : 96),
        Lizzie.config.isFrameFontSmall()
            ? (Lizzie.config.useJavaLooks ? 0 : -1)
            : (Lizzie.config.isFrameFontMiddle() ? 1 : 2),
        15,
        Lizzie.config.isFrameFontSmall()
            ? (Lizzie.config.useJavaLooks ? 10 : 11)
            : (Lizzie.config.isFrameFontMiddle() ? 12 : 14));
    btnKomiDown.setBounds(
        Lizzie.config.isFrameFontSmall() ? 66 : (Lizzie.config.isFrameFontMiddle() ? 79 : 96),
        Lizzie.config.isFrameFontSmall() ? 9 : (Lizzie.config.isFrameFontMiddle() ? 12 : 15),
        15,
        Lizzie.config.isFrameFontSmall()
            ? (Lizzie.config.useJavaLooks ? 10 : 11)
            : (Lizzie.config.isFrameFontMiddle() ? 12 : 14));

    komiPanel.add(chkPDA);
    komiPanel.add(lblGfPDA);
    komiPanel.add(txtGfPDA);
    komiPanel.add(chkWRN);
    komiPanel.add(lblWRN);
    komiPanel.add(txtWRN);
    komiPanel.add(btnKomiUp);
    komiPanel.add(btnKomiDown);
    //  komiPanel.add(more);
    komiPanel.add(lblPDASpinner);
    komiPanel.add(txtPDA);
    komiPanel.add(more2);
    //    komiPanel.add(setRules);
    //    komiPanel.add(setLzSaiParam);
    //    komiPanel.add(setBoardSize);
    //    komiPanel.add(saveLoad);
    komiPanel.add(lblKomiSpinner);
    komiPanel.add(txtKomi);

    txtTimeLimit = new JFontTextField();
    txtTimeLimit.setDocument(new IntDocument());
    chkTime = new JFontCheckBox(resourceBundle.getString("Menu.chkTime"));
    if (!chkTime.isPreferredSizeSet())
      chkTime.setPreferredSize(
          new Dimension(
              (int) chkTime.getPreferredSize().getWidth()
                  + (Lizzie.config.shouldWidenCheckBox ? 4 : 0),
              Config.menuHeight - 3));
    chkTime.setSelected(Lizzie.config.limitTime);
    chkTime.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            Lizzie.config.limitTime = chkTime.isSelected();
            Lizzie.config.uiConfig.put("limit-time", Lizzie.config.limitTime);
            txtTimeLimit.setEnabled(Lizzie.config.limitTime);
            reCalculateLeelazPonderingIfOutOfLimit();
          }
        });
    chkTime.setFocusable(false);

    txtTimeLimit.setEnabled(Lizzie.config.limitTime);
    txtTimeLimit.setMaximumSize(new Dimension(50, Config.menuHeight));
    txtTimeLimit.setPreferredSize(new Dimension(50, Config.menuHeight - 2));
    txtTimeLimit.setText(String.valueOf(Lizzie.config.maxAnalyzeTimeMillis / 1000));
    txtTimeLimit.setColumns(3);
    txtTimeLimit.addFocusListener(
        new FocusListener() {
          @Override
          public void focusGained(FocusEvent e) {
            // TODO Auto-generated method stub
          }

          @Override
          public void focusLost(FocusEvent e) {
            // TODO Auto-generated method stub
            Lizzie.config.maxAnalyzeTimeMillis =
                1000
                    * Utils.parseTextToLong(
                        txtTimeLimit, Lizzie.config.maxAnalyzeTimeMillis / 1000);
            Lizzie.config.leelazConfig.put(
                "max-analyze-time-seconds", Lizzie.config.maxAnalyzeTimeMillis / 1000);
            reCalculateLeelazPonderingIfOutOfLimit();
          }
        });

    txtPlayOutsLimit = new JFontTextField();
    txtPlayOutsLimit.setDocument(new IntDocument());
    chkPlayOut = new JFontCheckBox(resourceBundle.getString("Menu.chkPlayOut"));
    if (!chkPlayOut.isPreferredSizeSet())
      chkPlayOut.setPreferredSize(
          new Dimension(
              (int) chkPlayOut.getPreferredSize().getWidth()
                  + (Lizzie.config.shouldWidenCheckBox ? 4 : 0),
              Config.menuHeight - 3));
    chkPlayOut.setSelected(Lizzie.config.limitPlayout);
    chkPlayOut.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            Lizzie.config.limitPlayout = chkPlayOut.isSelected();
            Lizzie.config.uiConfig.put("limit-playout", Lizzie.config.limitPlayout);
            txtPlayOutsLimit.setEnabled(Lizzie.config.limitPlayout);
            reCalculateLeelazPonderingIfOutOfLimit();
          }
        });
    chkPlayOut.setFocusable(false);
    txtPlayOutsLimit.setEnabled(Lizzie.config.limitPlayout);
    txtPlayOutsLimit.setMaximumSize(new Dimension(80, Config.menuHeight));
    txtPlayOutsLimit.setPreferredSize(new Dimension(80, Config.menuHeight - 2));
    txtPlayOutsLimit.setText(String.valueOf(Lizzie.config.limitPlayouts));
    txtPlayOutsLimit.setColumns(4);
    txtPlayOutsLimit.addFocusListener(
        new FocusListener() {
          @Override
          public void focusGained(FocusEvent e) {
            // TODO Auto-generated method stub
          }

          @Override
          public void focusLost(FocusEvent e) {
            // TODO Auto-generated method stub
            Lizzie.config.limitPlayouts =
                Utils.parseTextToLong(txtPlayOutsLimit, Lizzie.config.limitPlayouts);
            Lizzie.config.leelazConfig.put("limit-playouts", Lizzie.config.limitPlayouts);
            reCalculateLeelazPonderingIfOutOfLimit();
          }
        });
    updateMenuAfterEngine(true);
    if (!Lizzie.config.showDoubleMenu) {
      updateMenuStatus();
    }
    if (Lizzie.readMode) {
      engineMenu2.setVisible(false);
      engineMenu.setVisible(false);
    }
  }

  public void updateRecentFileMenu() {
    // TODO Auto-generated method stub
    SwingUtilities.invokeLater(
        new Runnable() {
          public void run() {
            openRecent.removeAll();
            int j = 1;
            for (int i = Lizzie.config.recentFilePaths.size() - 1; i >= 0; i--) {
              String recentFilePath = Lizzie.config.recentFilePaths.get(i);
              File recentF = new File(recentFilePath);
              final JFontMenuItem recentFile = new JFontMenuItem(j + ": " + recentF.getName());
              recentFile.setToolTipText(recentFilePath);
              recentFile.addActionListener(
                  new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                      Lizzie.frame.loadFile(recentF, true, false);
                    }
                  });
              openRecent.add(recentFile);
              j++;
            }
          }
        });
  }

  private void resetMinPlayoutsForNextMove() {
    // TODO Auto-generated method stub
    minPlayoutsForNextMove.setText(
        resourceBundle.getString("Menu.minPlayoutsForNextMove")
            + Lizzie.config.minPlayoutsForNextMove
            + ")");
  }

  public void updateSingleMenu() {
    komiPanel.add(chkPDA);
    komiPanel.add(lblGfPDA);
    komiPanel.add(txtPDA);
    komiPanel.add(txtGfPDA);
    komiPanel.add(more2);
    komiPanel.add(txtWRN);
    komiPanel.add(chkWRN);
    komiPanel.add(lblWRN);
    setRules.setMargin(new Insets(0, 0, 0, 0));
    setLzSaiParam.setMargin(new Insets(0, 0, 0, 0));
    setBoardSize.setMargin(new Insets(0, 0, 0, 0));
    saveLoad.setMargin(new Insets(0, 0, 0, 0));
    startPos =
        Lizzie.config.isFrameFontSmall() ? 83 : (Lizzie.config.isFrameFontMiddle() ? 98 : 113);
    if (showPDA
        || (EngineManager.isEngineGame
            && (Lizzie.engineManager.engineList.get(EngineManager.engineGameInfo.firstEngineIndex)
                    .isKataGoPda
                || Lizzie.engineManager.engineList.get(
                        EngineManager.engineGameInfo.secondEngineIndex)
                    .isKataGoPda))) {
      if (Lizzie.config.isChinese) {
        lblPDASpinner.setBounds(
            startPos,
            Lizzie.config.isFrameFontSmall() ? 0 : (Lizzie.config.isFrameFontMiddle() ? 2 : 6),
            Lizzie.config.isFrameFontSmall() ? 40 : (Lizzie.config.isFrameFontMiddle() ? 55 : 70),
            17);
        startPos +=
            Lizzie.config.isFrameFontSmall() ? 41 : (Lizzie.config.isFrameFontMiddle() ? 56 : 69);
      } else {
        lblPDASpinner.setBounds(
            startPos,
            Lizzie.config.isFrameFontSmall() ? 0 : (Lizzie.config.isFrameFontMiddle() ? 2 : 6),
            Lizzie.config.isFrameFontSmall() ? 27 : (Lizzie.config.isFrameFontMiddle() ? 35 : 43),
            17);
        startPos +=
            Lizzie.config.isFrameFontSmall() ? 27 : (Lizzie.config.isFrameFontMiddle() ? 35 : 43);
      }

      lblPDASpinner.setVisible(true);
      txtPDA.setBounds(
          startPos,
          Lizzie.config.isFrameFontSmall() ? 0 : (Lizzie.config.isFrameFontMiddle() ? 1 : 3),
          Lizzie.config.isFrameFontSmall() ? 43 : (Lizzie.config.isFrameFontMiddle() ? 52 : 64),
          Lizzie.config.isFrameFontSmall() ? 18 : (Lizzie.config.isFrameFontMiddle() ? 21 : 23));
      txtPDA.setVisible(true);
      more2.setBounds(
          startPos
              + (Lizzie.config.isFrameFontSmall()
                  ? 43
                  : (Lizzie.config.isFrameFontMiddle() ? 52 : 64)),
          Lizzie.config.isFrameFontSmall() ? -1 : (Lizzie.config.isFrameFontMiddle() ? 0 : 2),
          Lizzie.config.isFrameFontSmall() ? 20 : (Lizzie.config.isFrameFontMiddle() ? 23 : 26),
          Lizzie.config.isFrameFontSmall() ? 20 : (Lizzie.config.isFrameFontMiddle() ? 23 : 25));
      more2.setVisible(true);
      startPos =
          startPos
              + (Lizzie.config.isFrameFontSmall()
                  ? 63
                  : (Lizzie.config.isFrameFontMiddle() ? 75 : 91));
      lblGfPDA.setVisible(false);
      txtGfPDA.setVisible(false);
      chkPDA.setVisible(false);
    } else {
      if (Lizzie.config.showPDAInMenu
          && !isEngineGame()
          && Lizzie.leelaz != null
          && Lizzie.leelaz.isKatago) {
        chkPDA.setBounds(
            startPos,
            Lizzie.config.isFrameFontSmall() ? 0 : (Lizzie.config.isFrameFontMiddle() ? 1 : 2),
            20,
            Lizzie.config.isFrameFontSmall()
                ? 17
                : (Lizzie.config.isFrameFontMiddle() ? 22 : 25)); // 20, 17);
        if (Lizzie.config.isChinese) {
          lblGfPDA.setBounds(
              startPos + 20,
              0,
              Lizzie.config.isFrameFontSmall() ? 40 : (Lizzie.config.isFrameFontMiddle() ? 50 : 60),
              Lizzie.config.isFrameFontSmall()
                  ? 17
                  : (Lizzie.config.isFrameFontMiddle() ? 22 : 29));
          startPos +=
              Lizzie.config.isFrameFontSmall() ? 40 : (Lizzie.config.isFrameFontMiddle() ? 50 : 60);
        } else {
          lblGfPDA.setBounds(
              startPos + 20,
              0,
              Lizzie.config.isFrameFontSmall() ? 27 : (Lizzie.config.isFrameFontMiddle() ? 35 : 43),
              Lizzie.config.isFrameFontSmall()
                  ? 17
                  : (Lizzie.config.isFrameFontMiddle() ? 22 : 29));
          startPos +=
              Lizzie.config.isFrameFontSmall() ? 27 : (Lizzie.config.isFrameFontMiddle() ? 35 : 43);
        }
        txtGfPDA.setBounds(
            startPos
                + (Lizzie.config.isFrameFontSmall()
                    ? 19
                    : (Lizzie.config.isFrameFontMiddle() ? 21 : 22)),
            Lizzie.config.isFrameFontSmall() ? 0 : (Lizzie.config.isFrameFontMiddle() ? 1 : 3),
            Lizzie.config.isFrameFontSmall() ? 33 : (Lizzie.config.isFrameFontMiddle() ? 36 : 42),
            Lizzie.config.isFrameFontSmall() ? 18 : (Lizzie.config.isFrameFontMiddle() ? 21 : 23));
        startPos +=
            Lizzie.config.isFrameFontSmall() ? 50 : (Lizzie.config.isFrameFontMiddle() ? 59 : 68);
        chkPDA.setVisible(true);
        lblGfPDA.setVisible(true);
        txtGfPDA.setVisible(true);
      } else {
        chkPDA.setVisible(false);
        lblGfPDA.setVisible(false);
        txtGfPDA.setVisible(false);
      }
    }

    if (Lizzie.config.showWRNInMenu
        && !isEngineGame()
        && Lizzie.leelaz != null
        && Lizzie.leelaz.isKatago) {
      chkWRN.setBounds(
          startPos,
          Lizzie.config.isFrameFontSmall() ? 0 : (Lizzie.config.isFrameFontMiddle() ? 1 : 2),
          20,
          Lizzie.config.isFrameFontSmall() ? 17 : (Lizzie.config.isFrameFontMiddle() ? 22 : 25));
      lblWRN.setBounds(
          startPos + 20,
          0,
          Lizzie.config.isFrameFontSmall() ? 30 : (Lizzie.config.isFrameFontMiddle() ? 38 : 46),
          Lizzie.config.isFrameFontSmall() ? 17 : (Lizzie.config.isFrameFontMiddle() ? 22 : 29));
      txtWRN.setBounds(
          startPos
              + (Lizzie.config.isFrameFontSmall()
                  ? 50
                  : (Lizzie.config.isFrameFontMiddle() ? 57 : 66)),
          Lizzie.config.isFrameFontSmall() ? 0 : (Lizzie.config.isFrameFontMiddle() ? 1 : 3),
          Lizzie.config.isFrameFontSmall() ? 33 : (Lizzie.config.isFrameFontMiddle() ? 38 : 46),
          Lizzie.config.isFrameFontSmall() ? 18 : (Lizzie.config.isFrameFontMiddle() ? 21 : 23));
      lblWRN.setVisible(true);
      txtWRN.setVisible(true);
      chkWRN.setVisible(true);
      startPos +=
          Lizzie.config.isFrameFontSmall() ? 82 : (Lizzie.config.isFrameFontMiddle() ? 97 : 117);
    } else {
      chkWRN.setVisible(false);
      lblWRN.setVisible(false);
      txtWRN.setVisible(false);
    }
    setKomiPanelExtra();
  }

  public void updateMenuStatusForEngine() {
    SwingUtilities.invokeLater(
        new Thread() {
          public void run() {
            if (Lizzie.config.showDoubleMenu && Lizzie.frame != null) {
              setPdaAndWrnByEngineForDouble();
              setBtnRankMark();
              return;
            }
            updateSingleMenu();
          }
        });
  }

  public void updateMenuStatus() {
    SwingUtilities.invokeLater(
        new Thread() {
          public void run() {
            if (Lizzie.config.showDoubleMenu && Lizzie.frame != null) {
              doubleMenu(false);
              return;
            }
            updateSingleMenu();
          }
        });
  }

  public void updateEngineMenuone() {
    for (int i = 0; i < engine.length; i++) {
      engine[i] = new JFontMenuItem();
      engineMenu.add(engine[i]);
      engine[i].setText("[" + (i + 1) + "]");
      engine[i].setVisible(false);
    }
    ArrayList<EngineData> engineData = Utils.getEngineData();
    for (int i = 0; i < engineData.size(); i++) {
      EngineData engineDt = engineData.get(i);
      if (i > (engine.length - 2)) {
        engine[i].setText(resourceBundle.getString("Menu.moreEngines")); // ("更多引擎...");
        engine[i].setVisible(true);
        engine[i].addActionListener(
            new ActionListener() {
              public void actionPerformed(ActionEvent e) {
                JDialog chooseMoreEngine;
                chooseMoreEngine = ChooseMoreEngine.createDialog(1);
                chooseMoreEngine.setVisible(true);
              }
            });
        return;
      } else {
        engine[i].setText("[" + (i + 1) + "] " + engineDt.name);
        engine[i].setToolTipText(engineDt.commands);
        engine[i].setVisible(true);
        engine[i].setToolTipText(engineDt.commands);
        int a = i;
        engine[i].addActionListener(
            new ActionListener() {
              public void actionPerformed(ActionEvent e) {
                Lizzie.engineManager.switchEngine(a, true);
              }
            });
      }
    }
  }

  public void setEngineMenuone2status(boolean status) {
    engineMenu2.setVisible(status);
    if (Lizzie.readMode) {
      engineMenu2.setVisible(false);
      engineMenu.setVisible(false);
    }
  }

  public void updateEngineMenuone2() {

    for (int i = 0; i < engine.length; i++) {
      engine2[i] = new JFontMenuItem();
      engineMenu2.add(engine2[i]);
      engine2[i].setText("[" + (i + 1) + "]");
      engine2[i].setVisible(false);
    }
    ArrayList<EngineData> engineData = Utils.getEngineData();
    for (int i = 0; i < engineData.size(); i++) {
      EngineData engineDt = engineData.get(i);
      if (i > (engine.length - 2)) {
        engine2[i].setText(resourceBundle.getString("Menu.moreEngines")); // ("更多引擎...");
        engine2[i].setVisible(true);
        engine2[i].addActionListener(
            new ActionListener() {
              public void actionPerformed(ActionEvent e) {
                JDialog chooseMoreEngine;
                chooseMoreEngine = ChooseMoreEngine.createDialog(2);
                chooseMoreEngine.setVisible(true);
              }
            });
        return;
      } else {
        engine2[i].setText("[" + (i + 1) + "] " + engineDt.name);
        engine2[i].setToolTipText(engineDt.commands);
        engine2[i].setVisible(true);
        int a = i;
        engine2[i].addActionListener(
            new ActionListener() {
              public void actionPerformed(ActionEvent e) {
                Lizzie.engineManager.switchEngine(a, false);
              }
            });
      }
    }
  }

  public void updateMenuAfterEngine(boolean first) {
    if (!first) {
      this.remove(black);
      this.remove(white);
      this.remove(blackwhite);
      this.remove(selectAllow);
      this.remove(selectAllowMore);
      this.remove(selectAvoid);
      this.remove(selectAvoidMore);
      this.remove(playPass);
      //  this.remove(clearSelect);
      this.remove(komiPanel);
      this.remove(btnDoubleMenu);
      this.remove(byoyomiTime);
      if (doubleMenuNewGame != null) remove(doubleMenuNewGame);
      if (doubleMenuPauseGame != null) remove(doubleMenuPauseGame);
      if (doubleMenuResign != null) remove(doubleMenuResign);
    }
    byoyomiTime = new JFontLabel();
    byoyomiTime.setFont(new Font(Config.sysDefaultFontName, Font.BOLD, 15));
    add(byoyomiTime);
    byoyomiTime.setVisible(LizzieFrame.isShowingByoTime);

    iconUp = new ImageIcon();
    try {
      iconUp.setImage(
          ImageIO.read(getClass().getResourceAsStream("/assets/up.png"))
              .getScaledInstance(
                  Config.menuIconSize, Config.menuIconSize, java.awt.Image.SCALE_SMOOTH));
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    iconDown = new ImageIcon();
    try {
      iconDown.setImage(
          ImageIO.read(getClass().getResourceAsStream("/assets/down.png"))
              .getScaledInstance(
                  Config.menuIconSize, Config.menuIconSize, java.awt.Image.SCALE_SMOOTH));
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    btnDoubleMenu = new JFontButton();
    btnDoubleMenu.setFocusable(false);
    if (Lizzie.config.showDoubleMenu) btnDoubleMenu.setIcon(iconUp);
    else btnDoubleMenu.setIcon(iconDown);
    btnDoubleMenu.setMargin(new Insets(0, 0, 0, 0));
    // btnDoubleMenu.setPreferredSize(new Dimension(20, 20));
    btnDoubleMenu.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            Lizzie.config.showDoubleMenu = !Lizzie.config.showDoubleMenu;
            doAfterChangeToolarPos();
            if (!Lizzie.config.showDoubleMenu) Lizzie.frame.reSetLoc();
          }
        });
    if (!Lizzie.config.showDoubleMenuBtn) btnDoubleMenu.setVisible(false);
    add(btnDoubleMenu);
    black = new JFontButton(iconblack);
    black.setPreferredSize(new Dimension(Config.menuHeight, Config.menuHeight));
    black.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            featurecat.lizzie.gui.Input.insert = 0;
            if (Lizzie.frame.blackorwhite != 1) {
              Lizzie.frame.blackorwhite = 1;
              black.setIcon(iconblack2);
              white.setIcon(iconwhite);
              blackwhite.setIcon(iconbh);
            } else {
              Lizzie.frame.blackorwhite = 0;
              black.setIcon(iconblack);
              white.setIcon(iconwhite);
              blackwhite.setIcon(iconbh);
            }
          }
        });
    black.setFocusable(false);
    black.setMargin(new Insets(0, 0, 0, 0));
    // this.add(black);
    black.setToolTipText(resourceBundle.getString("Menu.playBlackToolTipText"));

    white = new JFontButton(iconwhite);
    white.setPreferredSize(new Dimension(Config.menuHeight, Config.menuHeight));
    white.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            featurecat.lizzie.gui.Input.insert = 0;
            if (Lizzie.frame.blackorwhite != 2) {
              Lizzie.frame.blackorwhite = 2;
              black.setIcon(iconblack);
              white.setIcon(iconwhite2);
              blackwhite.setIcon(iconbh);
            } else {
              Lizzie.frame.blackorwhite = 0;
              black.setIcon(iconblack);
              white.setIcon(iconwhite);
              blackwhite.setIcon(iconbh);
            }
          }
        });
    white.setFocusable(false);
    white.setMargin(new Insets(0, 0, 0, 0));
    // this.add(white);
    white.setToolTipText(resourceBundle.getString("Menu.playWhiteToolTipText"));

    // black.setPreferredSize(new Dimension(25, 22));
    //  white.setPreferredSize(new Dimension(25, 22));
    blackwhite = new JFontButton(iconbh);
    blackwhite.addActionListener(
        new ActionListener() {

          public void actionPerformed(ActionEvent e) {
            featurecat.lizzie.gui.Input.insert = 0;
            if (blackwhite.getIcon() == iconbh) {
              Lizzie.frame.blackorwhite = 0;
              black.setIcon(iconblack);
              white.setIcon(iconwhite);
              blackwhite.setIcon(iconbh2);
            } else {
              black.setIcon(iconblack);
              white.setIcon(iconwhite);
              blackwhite.setIcon(iconbh);
            }
          }
        });
    blackwhite.setFocusable(false);
    blackwhite.setMargin(new Insets(0, 0, 0, 0));
    //  this.add(blackwhite);
    blackwhite.setPreferredSize(
        new Dimension(
            Lizzie.config.isFrameFontSmall() ? 22 : (Lizzie.config.isFrameFontMiddle() ? 25 : 28),
            Lizzie.config.isFrameFontSmall() ? 22 : (Lizzie.config.isFrameFontMiddle() ? 25 : 28)));
    blackwhite.setToolTipText(resourceBundle.getString("Menu.playAlternatelyToolTipText"));

    playPass = new JFontButton(iconplayPass);
    playPass.setMargin(new Insets(0, 0, 0, 0));
    playPass.setPreferredSize(
        new Dimension(
            Lizzie.config.isFrameFontSmall() ? 22 : (Lizzie.config.isFrameFontMiddle() ? 25 : 28),
            Lizzie.config.isFrameFontSmall() ? 22 : (Lizzie.config.isFrameFontMiddle() ? 25 : 28)));
    playPass.setToolTipText(resourceBundle.getString("Menu.playPassToolTipText"));
    playPass.setFocusable(false);
    playPass.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            Lizzie.board.pass();
          }
        });
    //  this.add(playPass);
    toggleShowEditbar(Lizzie.config.showEditbar);

    selectAllow = new JFontButton(iconAllow);
    selectAllow.addActionListener(
        new ActionListener() {

          public void actionPerformed(ActionEvent e) {
            selectAvoid.setIcon(iconAvoid);
            if (!featurecat.lizzie.gui.Input.selectMode) {
              featurecat.lizzie.gui.Input.selectMode = true;
              Lizzie.frame.selectForceAllow = true;
              selectAllow.setIcon(iconAllow2);
            } else {
              if (!Lizzie.frame.selectForceAllow) {
                Lizzie.frame.selectForceAllow = true;
                selectAllow.setIcon(iconAllow2);
              } else {
                featurecat.lizzie.gui.Input.selectMode = false;
                selectAllow.setIcon(iconAllow);
              }
            }
            //   Lizzie.frame.boardRenderer.removeSelectedRect();
            //   if (Lizzie.frame.independentMainBoard != null)
            //     Lizzie.frame.independentMainBoard.boardRenderer.removeSelectedRect();
            //    Lizzie.frame.refresh();
            //    featurecat.lizzie.gui.RightClickMenu.avoidcoords = "";
            //   featurecat.lizzie.gui.RightClickMenu.allowcoords = "";
            //    }
            Lizzie.frame.isKeepingForce = true;
          }
        });
    selectAllow.setFocusable(false);
    selectAllow.setMargin(new Insets(0, 0, 0, -1));
    selectAllow.setToolTipText(resourceBundle.getString("Menu.selectAllowToolTipText"));

    selectAvoid = new JFontButton(iconAvoid);
    selectAvoid.addActionListener(
        new ActionListener() {

          public void actionPerformed(ActionEvent e) {
            selectAllow.setIcon(iconAllow);
            if (!featurecat.lizzie.gui.Input.selectMode) {
              featurecat.lizzie.gui.Input.selectMode = true;
              Lizzie.frame.selectForceAllow = false;
              selectAvoid.setIcon(iconAvoid2);
            } else {
              if (Lizzie.frame.selectForceAllow) {
                Lizzie.frame.selectForceAllow = false;
                selectAvoid.setIcon(iconAvoid2);
              } else {
                featurecat.lizzie.gui.Input.selectMode = false;
                selectAvoid.setIcon(iconAvoid);
              }
            }
            //  Lizzie.frame.boardRenderer.removeSelectedRect();
            //   if (Lizzie.frame.independentMainBoard != null)
            //    Lizzie.frame.independentMainBoard.boardRenderer.removeSelectedRect();
            //  Lizzie.frame.refresh();
            //  featurecat.lizzie.gui.RightClickMenu.avoidcoords = "";
            //   featurecat.lizzie.gui.RightClickMenu.allowcoords = "";
            // }
            Lizzie.frame.isKeepingForce = true;
          }
        });
    selectAvoid.setFocusable(false);
    selectAvoid.setMargin(new Insets(0, 0, 0, -1));
    selectAvoid.setToolTipText(resourceBundle.getString("Menu.selectAvoidToolTipText"));

    ImageIcon horizonDown;
    horizonDown = new ImageIcon();
    try {
      horizonDown.setImage(
          ImageIO.read(getClass().getResourceAsStream("/assets/horizonDown.png"))
              .getScaledInstance(
                  Lizzie.config.isFrameFontSmall()
                      ? 12
                      : (Lizzie.config.isFrameFontMiddle() ? 15 : 18),
                  Lizzie.config.isFrameFontSmall()
                      ? 20
                      : (Lizzie.config.isFrameFontMiddle() ? 25 : 30),
                  java.awt.Image.SCALE_SMOOTH));
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

    JPopupMenu selectAllowPopup = new JPopupMenu();
    JFontCheckBoxMenuItem selectAllowCustomMove = new JFontCheckBoxMenuItem();
    JFontTextField txtLimitLengthAllow = new JFontTextField();
    txtLimitLengthAllow.setDocument(new IntDocument());
    txtLimitLengthAllow.setPreferredSize(
        new Dimension(
            50,
            Lizzie.config.isFrameFontSmall() ? 20 : (Lizzie.config.isFrameFontMiddle() ? 22 : 24)));
    selectAllowCustomMove.setLayout(null);
    JFontLabel customNumberAllow =
        new JFontLabel(
            resourceBundle.getString(
                "Menu.selectLimitCustomMoves")); // "Custom limit moves:");//自定义限制手数:
    customNumberAllow.setBounds(
        Lizzie.config.useJavaLooks ? 20 : 37,
        Lizzie.config.isFrameFontSmall() ? 1 : (Lizzie.config.isFrameFontMiddle() ? 1 : 3),
        180,
        25);
    selectAllowCustomMove.add(customNumberAllow);
    selectAllowCustomMove.add(txtLimitLengthAllow);
    if (Lizzie.config.isChinese) {
      txtLimitLengthAllow.setBounds(
          Lizzie.config.isFrameFontSmall() ? 126 : (Lizzie.config.isFrameFontMiddle() ? 156 : 188),
          Lizzie.config.isFrameFontSmall() ? 4 : (Lizzie.config.isFrameFontMiddle() ? 3 : 4),
          40,
          Lizzie.config.isFrameFontSmall() ? 20 : (Lizzie.config.isFrameFontMiddle() ? 22 : 24));
      selectAllowCustomMove.setPreferredSize(
          new Dimension(
              Lizzie.config.isFrameFontSmall()
                  ? 170
                  : (Lizzie.config.isFrameFontMiddle() ? 205 : 233),
              Lizzie.config.isFrameFontSmall()
                  ? 27
                  : (Lizzie.config.isFrameFontMiddle() ? 27 : 30)));
    } else {
      txtLimitLengthAllow.setBounds(
          Lizzie.config.isFrameFontSmall() ? 156 : (Lizzie.config.isFrameFontMiddle() ? 191 : 228),
          Lizzie.config.isFrameFontSmall() ? 4 : (Lizzie.config.isFrameFontMiddle() ? 3 : 4),
          40,
          Lizzie.config.isFrameFontSmall() ? 20 : (Lizzie.config.isFrameFontMiddle() ? 22 : 24));
      selectAllowCustomMove.setPreferredSize(
          new Dimension(
              Lizzie.config.isFrameFontSmall()
                  ? 200
                  : (Lizzie.config.isFrameFontMiddle() ? 240 : 273),
              Lizzie.config.isFrameFontSmall()
                  ? 27
                  : (Lizzie.config.isFrameFontMiddle() ? 27 : 30)));
    }

    JFontCheckBoxMenuItem selectAllowAllMove =
        new JFontCheckBoxMenuItem(
            resourceBundle.getString("Menu.selectLimitAllMoves")); // "Limit All moves");//限制整个搜索过程
    JFontCheckBoxMenuItem selectAllowOneMove =
        new JFontCheckBoxMenuItem(
            resourceBundle.getString("Menu.selectLimitOneMove")); // ("Limit first move");//仅限制第一手
    selectAllowPopup.add(selectAllowAllMove);
    selectAllowPopup.add(selectAllowOneMove);
    selectAllowPopup.add(selectAllowCustomMove);
    selectAllowPopup.setVisible(true);
    selectAllowPopup.setVisible(false);

    selectAllowPopup.addPopupMenuListener(
        new PopupMenuListener() {
          public void popupMenuCanceled(PopupMenuEvent e) {}

          public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
            Lizzie.config.selectAllowCustomMoves =
                Utils.parseTextToInt(txtLimitLengthAllow, Lizzie.config.selectAllowCustomMoves);
            Lizzie.config.uiConfig.put(
                "select-allow-custom-moves", Lizzie.config.selectAllowCustomMoves);
            if (selectAllowCustomMove.isSelected()) {
              Lizzie.config.selectAllowMoves = Lizzie.config.selectAllowCustomMoves;
              Lizzie.config.uiConfig.put("select-allow-moves", Lizzie.config.selectAllowMoves);
            }
            Runnable runnable =
                new Runnable() {
                  public void run() {
                    try {
                      Thread.sleep(100);
                    } catch (InterruptedException e) {
                      // TODO Auto-generated catch block
                      e.printStackTrace();
                    }
                    if (Lizzie.leelaz.isPondering()) Lizzie.leelaz.ponder();
                  }
                };
            Thread thread = new Thread(runnable);
            thread.start();
          }

          public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
            if (Lizzie.config.selectAllowMoves == 999) {
              selectAllowAllMove.setSelected(true);
              selectAllowOneMove.setSelected(false);
              selectAllowCustomMove.setSelected(false);
            } else if (Lizzie.config.selectAllowMoves == 1) {
              selectAllowAllMove.setSelected(false);
              selectAllowOneMove.setSelected(true);
              selectAllowCustomMove.setSelected(false);
            } else {
              selectAllowAllMove.setSelected(false);
              selectAllowOneMove.setSelected(false);
              selectAllowCustomMove.setSelected(true);
            }
            txtLimitLengthAllow.setText(String.valueOf(Lizzie.config.selectAllowCustomMoves));
          }
        });

    selectAllowAllMove.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            Lizzie.config.selectAllowMoves = 999;
            Lizzie.config.uiConfig.put("select-allow-moves", Lizzie.config.selectAllowMoves);
            try {
              Lizzie.config.save();
            } catch (IOException e1) {
              // TODO Auto-generated catch block
              e1.printStackTrace();
            }
          }
        });

    selectAllowOneMove.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            Lizzie.config.selectAllowMoves = 1;
            Lizzie.config.uiConfig.put("select-allow-moves", Lizzie.config.selectAllowMoves);
          }
        });

    selectAllowCustomMove.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            Lizzie.config.selectAllowCustomMoves =
                Utils.parseTextToInt(txtLimitLengthAllow, Lizzie.config.selectAllowCustomMoves);
            Lizzie.config.uiConfig.put(
                "select-allow-custom-moves", Lizzie.config.selectAllowCustomMoves);

            Lizzie.config.selectAllowMoves = Lizzie.config.selectAllowCustomMoves;
            Lizzie.config.uiConfig.put("select-allow-moves", Lizzie.config.selectAllowMoves);
          }
        });

    selectAllowMore = new JButton(horizonDown);
    selectAllowMore.setMargin(new Insets(0, 0, 0, 0));
    selectAllowMore.setFocusable(false);
    selectAllowMore.setPreferredSize(
        new Dimension(
            (Lizzie.config.isFrameFontSmall() ? 12 : (Lizzie.config.isFrameFontMiddle() ? 15 : 18)),
            (Lizzie.config.isFrameFontSmall()
                ? 20
                : (Lizzie.config.isFrameFontMiddle() ? 25 : 30))));
    selectAllowMore.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            selectAllowPopup.show(
                toolPanel,
                selectAllow.getX(),
                selectAllowMore.getY() + selectAllowMore.getHeight());
          }
        });

    JPopupMenu selectAvoidPopup = new JPopupMenu();
    JFontCheckBoxMenuItem selectAvoidCustomMove = new JFontCheckBoxMenuItem();
    JFontTextField txtLimitLengthAvoid = new JFontTextField();
    txtLimitLengthAllow.setDocument(new IntDocument());
    txtLimitLengthAvoid.setPreferredSize(
        new Dimension(
            50,
            Lizzie.config.isFrameFontSmall() ? 20 : (Lizzie.config.isFrameFontMiddle() ? 22 : 24)));
    selectAvoidCustomMove.setLayout(null);
    JFontLabel customNumberAvoid =
        new JFontLabel(
            resourceBundle.getString(
                "Menu.selectLimitCustomMoves")); // "Custom limit moves:");//自定义限制手数:
    customNumberAvoid.setBounds(
        Lizzie.config.useJavaLooks ? 20 : 37,
        Lizzie.config.isFrameFontSmall() ? 1 : (Lizzie.config.isFrameFontMiddle() ? 1 : 3),
        180,
        25);
    selectAvoidCustomMove.add(customNumberAvoid);
    selectAvoidCustomMove.add(txtLimitLengthAvoid);
    if (Lizzie.config.isChinese) {
      txtLimitLengthAvoid.setBounds(
          Lizzie.config.isFrameFontSmall() ? 126 : (Lizzie.config.isFrameFontMiddle() ? 156 : 188),
          Lizzie.config.isFrameFontSmall() ? 4 : (Lizzie.config.isFrameFontMiddle() ? 3 : 4),
          40,
          Lizzie.config.isFrameFontSmall() ? 20 : (Lizzie.config.isFrameFontMiddle() ? 22 : 24));
      selectAvoidCustomMove.setPreferredSize(
          new Dimension(
              Lizzie.config.isFrameFontSmall()
                  ? 170
                  : (Lizzie.config.isFrameFontMiddle() ? 205 : 233),
              Lizzie.config.isFrameFontSmall()
                  ? 27
                  : (Lizzie.config.isFrameFontMiddle() ? 27 : 30)));
    } else {
      txtLimitLengthAvoid.setBounds(
          Lizzie.config.isFrameFontSmall() ? 156 : (Lizzie.config.isFrameFontMiddle() ? 191 : 228),
          Lizzie.config.isFrameFontSmall() ? 4 : (Lizzie.config.isFrameFontMiddle() ? 3 : 4),
          40,
          Lizzie.config.isFrameFontSmall() ? 20 : (Lizzie.config.isFrameFontMiddle() ? 22 : 24));
      selectAvoidCustomMove.setPreferredSize(
          new Dimension(
              Lizzie.config.isFrameFontSmall()
                  ? 200
                  : (Lizzie.config.isFrameFontMiddle() ? 240 : 273),
              Lizzie.config.isFrameFontSmall()
                  ? 27
                  : (Lizzie.config.isFrameFontMiddle() ? 27 : 30)));
    }

    JFontCheckBoxMenuItem selectAvoidAllMove =
        new JFontCheckBoxMenuItem(
            resourceBundle.getString("Menu.selectLimitAllMoves")); // "Limit All moves");//限制整个搜索过程
    JFontCheckBoxMenuItem selectAvoidOneMove =
        new JFontCheckBoxMenuItem(
            resourceBundle.getString("Menu.selectLimitOneMove")); // ("Limit first move");//仅限制第一手
    selectAvoidPopup.add(selectAvoidAllMove);
    selectAvoidPopup.add(selectAvoidOneMove);
    selectAvoidPopup.add(selectAvoidCustomMove);
    selectAvoidPopup.setVisible(true);
    selectAvoidPopup.setVisible(false);

    selectAvoidPopup.addPopupMenuListener(
        new PopupMenuListener() {
          public void popupMenuCanceled(PopupMenuEvent e) {}

          public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
            Lizzie.config.selectAvoidCustomMoves =
                Utils.parseTextToInt(txtLimitLengthAvoid, Lizzie.config.selectAvoidCustomMoves);
            Lizzie.config.uiConfig.put(
                "select-avoid-custom-moves", Lizzie.config.selectAvoidCustomMoves);
            if (selectAvoidCustomMove.isSelected()) {
              Lizzie.config.selectAvoidMoves = Lizzie.config.selectAvoidCustomMoves;
              Lizzie.config.uiConfig.put("select-avoid-moves", Lizzie.config.selectAvoidMoves);
            }
            Runnable runnable =
                new Runnable() {
                  public void run() {
                    try {
                      Thread.sleep(100);
                    } catch (InterruptedException e) {
                      // TODO Auto-generated catch block
                      e.printStackTrace();
                    }
                    if (Lizzie.leelaz.isPondering()) Lizzie.leelaz.ponder();
                  }
                };
            Thread thread = new Thread(runnable);
            thread.start();
          }

          public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
            if (Lizzie.config.selectAvoidMoves == 999) {
              selectAvoidAllMove.setSelected(true);
              selectAvoidOneMove.setSelected(false);
              selectAvoidCustomMove.setSelected(false);
            } else if (Lizzie.config.selectAvoidMoves == 1) {
              selectAvoidAllMove.setSelected(false);
              selectAvoidOneMove.setSelected(true);
              selectAvoidCustomMove.setSelected(false);
            } else {
              selectAvoidAllMove.setSelected(false);
              selectAvoidOneMove.setSelected(false);
              selectAvoidCustomMove.setSelected(true);
            }
            txtLimitLengthAvoid.setText(String.valueOf(Lizzie.config.selectAvoidCustomMoves));
          }
        });

    selectAvoidAllMove.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            Lizzie.config.selectAvoidMoves = 999;
            Lizzie.config.uiConfig.put("select-avoid-moves", Lizzie.config.selectAvoidMoves);
          }
        });

    selectAvoidOneMove.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            Lizzie.config.selectAvoidMoves = 1;
            Lizzie.config.uiConfig.put("select-avoid-moves", Lizzie.config.selectAvoidMoves);
          }
        });

    selectAvoidCustomMove.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            Lizzie.config.selectAvoidCustomMoves =
                Utils.parseTextToInt(txtLimitLengthAvoid, Lizzie.config.selectAvoidCustomMoves);
            Lizzie.config.uiConfig.put(
                "select-avoid-custom-moves", Lizzie.config.selectAvoidCustomMoves);

            Lizzie.config.selectAvoidMoves = Lizzie.config.selectAvoidCustomMoves;
            Lizzie.config.uiConfig.put("select-avoid-moves", Lizzie.config.selectAvoidMoves);
          }
        });

    selectAvoidMore = new JButton(horizonDown);
    selectAvoidMore.setMargin(new Insets(0, 0, 0, 0));
    selectAvoidMore.setFocusable(false);
    selectAvoidMore.setPreferredSize(
        new Dimension(
            (Lizzie.config.isFrameFontSmall() ? 12 : (Lizzie.config.isFrameFontMiddle() ? 15 : 18)),
            (Lizzie.config.isFrameFontSmall()
                ? 20
                : (Lizzie.config.isFrameFontMiddle() ? 25 : 30))));
    selectAvoidMore.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            selectAvoidPopup.show(
                toolPanel,
                selectAvoid.getX(),
                selectAvoidMore.getY() + selectAvoidMore.getHeight());
          }
        });

    if (Lizzie.leelaz != null && (Lizzie.leelaz.isKatago || Lizzie.leelaz.isZen)) {
      selectAvoid.setVisible(false);
    }
    if (clearSelect != null) remove(clearSelect);
    clearSelect = new JFontButton(iconClear);
    clearSelect.addActionListener(
        new ActionListener() {

          public void actionPerformed(ActionEvent e) {
            selectAvoid.setIcon(iconAvoid);
            selectAllow.setIcon(iconAllow);
            featurecat.lizzie.gui.Input.selectMode = false;
            LizzieFrame.boardRenderer.removeSelectedRect();
            if (Lizzie.frame.independentMainBoard != null)
              Lizzie.frame.independentMainBoard.boardRenderer.removeSelectedRect();
            Lizzie.frame.refresh();
            if (LizzieFrame.avoidcoords != "" || LizzieFrame.allowcoords != "")
              Lizzie.board.clearbestmovesafter(Lizzie.board.getHistory().getStart());
            LizzieFrame.avoidcoords = "";
            LizzieFrame.allowcoords = "";
            Lizzie.frame.isKeepingForce = false;
            if (Lizzie.leelaz.isPondering()) Lizzie.leelaz.ponder();
          }
        });
    clearSelect.setFocusable(false);
    clearSelect.setMargin(new Insets(0, -2, 0, -2));
    if (Lizzie.leelaz != null && !Lizzie.leelaz.isKatago && !Lizzie.config.showDoubleMenu)
      this.add(clearSelect);
    clearSelect.setToolTipText(resourceBundle.getString("Menu.clearSelectToolTipText"));
    toggleShowForce(Lizzie.config.showForceMenu);
    // add(clearSelect);
    //
    if (!Lizzie.config.showDoubleMenu) {
      doubleMenuNewGame = new JFontButton(resourceBundle.getString("Menu.newGameBtn"));
      doubleMenuPauseGame = new JFontButton(resourceBundle.getString("Menu.pauseGameBtn"));
      doubleMenuNewGame.setFocusable(false);
      doubleMenuPauseGame.setFocusable(false);

      JPopupMenu newGamePopup = new JPopupMenu();

      JFontMenuItem engineGame =
          new JFontMenuItem(resourceBundle.getString("Menu.newEngineGame")); // ("引擎对局(Alt+E)");
      engineGame.addActionListener(
          new ActionListener() {
            public void actionPerformed(ActionEvent e) {
              Lizzie.frame.startEngineGameDialog();
            }
          });

      JFontMenuItem analyzeGame = new JFontMenuItem(); // ("人机对局(分析模式 N)");

      analyzeGame.setLayout(null);
      if (Lizzie.config.isChinese)
        analyzeGame.setPreferredSize(
            new Dimension(
                (Lizzie.config.useJavaLooks ? -31 : 0)
                    + (Lizzie.config.isFrameFontSmall()
                        ? 200
                        : (Lizzie.config.isFrameFontMiddle() ? 250 : 300)),
                (Lizzie.config.useJavaLooks
                    ? (Lizzie.config.isFrameFontSmall()
                        ? 20
                        : (Lizzie.config.isFrameFontMiddle() ? 25 : 30))
                    : (Lizzie.config.isFrameFontSmall()
                        ? 25
                        : (Lizzie.config.isFrameFontMiddle() ? 27 : 30)))));
      else
        analyzeGame.setPreferredSize(
            new Dimension(
                (Lizzie.config.useJavaLooks ? -31 : 0)
                    + (Lizzie.config.isFrameFontSmall()
                        ? 253
                        : (Lizzie.config.isFrameFontMiddle() ? 310 : 380)),
                (Lizzie.config.useJavaLooks
                    ? (Lizzie.config.isFrameFontSmall()
                        ? 20
                        : (Lizzie.config.isFrameFontMiddle() ? 25 : 30))
                    : (Lizzie.config.isFrameFontSmall()
                        ? 25
                        : (Lizzie.config.isFrameFontMiddle() ? 27 : 30)))));
      JFontLabel lblAnalyzeGame =
          new JFontLabel(resourceBundle.getString("Menu.newAnalyzeModeGame"));

      lblAnalyzeGame.setBounds(
          Lizzie.config.useJavaLooks ? 6 : 37,
          (Lizzie.config.useJavaLooks
              ? -1
              : (Lizzie.config.isFrameFontSmall()
                  ? 2
                  : (Lizzie.config.isFrameFontMiddle() ? 1 : -1))),
          320,
          Lizzie.config.isFrameFontSmall() ? 20 : (Lizzie.config.isFrameFontMiddle() ? 25 : 30));
      JButton aboutAnalyzeGame = new JFontButton("?");
      aboutAnalyzeGame.addActionListener(
          new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
              Lizzie.frame.showAnalyzeGenmoveInfo();
            }
          });
      aboutAnalyzeGame.setFocusable(false);
      aboutAnalyzeGame.setMargin(new Insets(0, 0, 0, 0));
      if (Lizzie.config.isChinese)
        aboutAnalyzeGame.setBounds(
            (Lizzie.config.useJavaLooks ? -31 : 0)
                + (Lizzie.config.isFrameFontSmall()
                    ? 177
                    : (Lizzie.config.isFrameFontMiddle() ? 220 : 270)),
            (Lizzie.config.useJavaLooks
                ? 1
                : (Lizzie.config.isFrameFontSmall()
                    ? 3
                    : (Lizzie.config.isFrameFontMiddle() ? 2 : 1))),
            Config.menuHeight - 2,
            Config.menuHeight - 2);
      else
        aboutAnalyzeGame.setBounds(
            (Lizzie.config.useJavaLooks ? -31 : 0)
                + (Lizzie.config.isFrameFontSmall()
                    ? 230
                    : (Lizzie.config.isFrameFontMiddle() ? 280 : 350)),
            (Lizzie.config.useJavaLooks
                ? 1
                : (Lizzie.config.isFrameFontSmall()
                    ? 3
                    : (Lizzie.config.isFrameFontMiddle() ? 2 : 1))),
            Config.menuHeight - 2,
            Config.menuHeight - 2);
      analyzeGame.add(aboutAnalyzeGame);
      analyzeGame.add(lblAnalyzeGame);
      analyzeGame.addActionListener(
          new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
              Lizzie.frame.startAnalyzeGameDialog();
            }
          });

      JFontMenuItem genmoveGame =
          new JFontMenuItem(
              resourceBundle.getString("Menu.newGenmoveGame")); // ("人机对局(Genmove模式 Alt+N)");
      genmoveGame.addActionListener(
          new ActionListener() {
            public void actionPerformed(ActionEvent e) {
              Lizzie.frame.startNewGame();
            }
          });

      newGamePopup.add(genmoveGame);
      newGamePopup.add(analyzeGame);
      newGamePopup.add(engineGame);

      doubleMenuNewGame.addActionListener(
          new ActionListener() {
            public void actionPerformed(ActionEvent e) {
              if (EngineManager.isEngineGame) {
                Lizzie.engineManager.stopEngineGame(-1, true);
                return;
              }
              if (Lizzie.frame.isPlayingAgainstLeelaz || Lizzie.frame.isAnaPlayingAgainstLeelaz) {
                Lizzie.frame.togglePonderMannul();
                return;
              }
              newGamePopup.show(
                  !Lizzie.config.showDoubleMenu && Lizzie.config.isFrameFontSmall()
                      ? komiContentPanel
                      : LizzieFrame.menu,
                  Lizzie.config.showDoubleMenu
                      ? doubleMenuNewGame.getX()
                      : komiPanel.getX() + startPos,
                  doubleMenuNewGame.getY() + doubleMenuNewGame.getHeight() - 1);
            }
          });

      doubleMenuPauseGame.addActionListener(
          new ActionListener() {
            public void actionPerformed(ActionEvent e) {
              pauseGame();
            }
          });
      doubleMenuResign = new JFontButton(resourceBundle.getString("Menu.resignBtn"));
      doubleMenuResign.setFocusable(false);
      doubleMenuResign.setVisible(false);
      doubleMenuResign.addActionListener(
          new ActionListener() {
            public void actionPerformed(ActionEvent e) {
              if (Lizzie.frame.isPlayingAgainstLeelaz || Lizzie.frame.isAnaPlayingAgainstLeelaz) {
                if (Lizzie.frame.playerIsBlack) {
                  GameInfo gameInfo = Lizzie.board.getHistory().getGameInfo();
                  gameInfo.setResult(resourceBundle.getString("Leelaz.whiteWin"));
                } else {
                  GameInfo gameInfo = Lizzie.board.getHistory().getGameInfo();
                  gameInfo.setResult(resourceBundle.getString("Leelaz.blackWin"));
                }
                Lizzie.frame.togglePonderMannul();
                return;
              }
            }
          });
      doubleMenuNewGame.setMargin(new Insets(0, 0, 0, 0));
      doubleMenuPauseGame.setMargin(new Insets(0, 0, 0, 0));
      doubleMenuResign.setMargin(new Insets(0, 0, 0, 0));
      if (!first) toggleDoubleMenuGameStatus();
    }
    //
    // add(komiPanel);
    if (!first) doubleMenu(false);
  }

  public void doubleMenu(boolean first) {
    if (!Lizzie.config.showDoubleMenu) {
      Lizzie.frame.topPanel.setVisible(false);
      if (first) toggleDoubleMenuGameStatus();
      return;
    }
    Lizzie.frame.topPanel.setVisible(true);
    Lizzie.frame.topPanel.removeAll();
    Lizzie.frame.topPanel.add(btnDoubleMenu);
    btnDoubleMenu.setPreferredSize(
        new Dimension((int) btnDoubleMenu.getPreferredSize().getWidth(), Config.menuHeight));
    if (Lizzie.config.showBasicBtn) {
      rankMarkOn = new ImageIcon();
      rankMarkOff = new ImageIcon();
      ImageIcon iconOpen = new ImageIcon();
      ImageIcon iconSave = new ImageIcon();
      ImageIcon iconAnalyze = new ImageIcon();
      ImageIcon iconHawkeye = new ImageIcon();
      ImageIcon iconSetMain = new ImageIcon();
      ImageIcon iconBackMain = new ImageIcon();
      ImageIcon iconChangeTurn = new ImageIcon();
      ImageIcon iconMarkup1 = new ImageIcon();
      ImageIcon iconMarkup2 = new ImageIcon();
      ImageIcon markupLabel1 = new ImageIcon();
      ImageIcon markupLabel2 = new ImageIcon();
      ImageIcon markupCircle1 = new ImageIcon();
      ImageIcon markupCircle2 = new ImageIcon();
      ImageIcon markupX1 = new ImageIcon();
      ImageIcon markupX2 = new ImageIcon();
      ImageIcon markupSquare1 = new ImageIcon();
      ImageIcon markupSquare2 = new ImageIcon();
      ImageIcon markupsanjiao1 = new ImageIcon();
      ImageIcon markupsanjiao2 = new ImageIcon();
      ImageIcon eraser1 = new ImageIcon();
      ImageIcon eraser2 = new ImageIcon();
      ImageIcon clear = new ImageIcon();
      ImageIcon flash = new ImageIcon();
      try {
        flash.setImage(
            ImageIO.read(getClass().getResourceAsStream("/assets/flash.png"))
                .getScaledInstance(
                    Config.menuIconSize, Config.menuIconSize, java.awt.Image.SCALE_SMOOTH));
        iconOpen.setImage(
            ImageIO.read(getClass().getResourceAsStream("/assets/open.png"))
                .getScaledInstance(
                    Config.menuIconSize, Config.menuIconSize, java.awt.Image.SCALE_SMOOTH));
        iconSave.setImage(
            ImageIO.read(getClass().getResourceAsStream("/assets/save.png"))
                .getScaledInstance(
                    Config.menuIconSize, Config.menuIconSize, java.awt.Image.SCALE_SMOOTH));
        iconAnalyze.setImage(
            ImageIO.read(getClass().getResourceAsStream("/assets/analyze.png"))
                .getScaledInstance(
                    Config.menuIconSize, Config.menuIconSize, java.awt.Image.SCALE_SMOOTH));
        iconHawkeye.setImage(
            ImageIO.read(getClass().getResourceAsStream("/assets/hawkeye2.png"))
                .getScaledInstance(
                    Config.menuIconSize, Config.menuIconSize, java.awt.Image.SCALE_SMOOTH));
        iconSetMain.setImage(
            ImageIO.read(getClass().getResourceAsStream("/assets/setmain.png"))
                .getScaledInstance(
                    Config.menuIconSize, Config.menuIconSize, java.awt.Image.SCALE_SMOOTH));
        iconBackMain.setImage(
            ImageIO.read(getClass().getResourceAsStream("/assets/backmain.png"))
                .getScaledInstance(
                    Config.menuIconSize, Config.menuIconSize, java.awt.Image.SCALE_SMOOTH));
        iconChangeTurn.setImage(
            ImageIO.read(getClass().getResourceAsStream("/assets/pass.png"))
                .getScaledInstance(
                    Config.menuIconSize, Config.menuIconSize, java.awt.Image.SCALE_SMOOTH));
        iconMarkup1.setImage(
            ImageIO.read(getClass().getResourceAsStream("/assets/mark1.png"))
                .getScaledInstance(
                    Config.menuIconSize, Config.menuIconSize, java.awt.Image.SCALE_SMOOTH));
        iconMarkup2.setImage(
            ImageIO.read(getClass().getResourceAsStream("/assets/mark2.png"))
                .getScaledInstance(
                    Config.menuIconSize, Config.menuIconSize, java.awt.Image.SCALE_SMOOTH));
        markupLabel1.setImage(
            ImageIO.read(getClass().getResourceAsStream("/assets/label.png"))
                .getScaledInstance(
                    Config.menuIconSize, Config.menuIconSize, java.awt.Image.SCALE_SMOOTH));
        markupLabel2.setImage(
            ImageIO.read(getClass().getResourceAsStream("/assets/label2.png"))
                .getScaledInstance(
                    Config.menuIconSize, Config.menuIconSize, java.awt.Image.SCALE_SMOOTH));
        markupX1.setImage(
            ImageIO.read(getClass().getResourceAsStream("/assets/x.png"))
                .getScaledInstance(
                    Config.menuIconSize, Config.menuIconSize, java.awt.Image.SCALE_SMOOTH));
        markupX2.setImage(
            ImageIO.read(getClass().getResourceAsStream("/assets/x2.png"))
                .getScaledInstance(
                    Config.menuIconSize, Config.menuIconSize, java.awt.Image.SCALE_SMOOTH));
        markupCircle1.setImage(
            ImageIO.read(getClass().getResourceAsStream("/assets/circle.png"))
                .getScaledInstance(
                    Config.menuIconSize, Config.menuIconSize, java.awt.Image.SCALE_SMOOTH));
        markupCircle2.setImage(
            ImageIO.read(getClass().getResourceAsStream("/assets/circle2.png"))
                .getScaledInstance(
                    Config.menuIconSize, Config.menuIconSize, java.awt.Image.SCALE_SMOOTH));
        markupSquare1.setImage(
            ImageIO.read(getClass().getResourceAsStream("/assets/square.png"))
                .getScaledInstance(
                    Config.menuIconSize, Config.menuIconSize, java.awt.Image.SCALE_SMOOTH));
        markupSquare2.setImage(
            ImageIO.read(getClass().getResourceAsStream("/assets/square2.png"))
                .getScaledInstance(
                    Config.menuIconSize, Config.menuIconSize, java.awt.Image.SCALE_SMOOTH));
        markupsanjiao1.setImage(
            ImageIO.read(getClass().getResourceAsStream("/assets/sanjiao.png"))
                .getScaledInstance(
                    Config.menuIconSize, Config.menuIconSize, java.awt.Image.SCALE_SMOOTH));
        markupsanjiao2.setImage(
            ImageIO.read(getClass().getResourceAsStream("/assets/sanjiao2.png"))
                .getScaledInstance(
                    Config.menuIconSize, Config.menuIconSize, java.awt.Image.SCALE_SMOOTH));
        eraser1.setImage(
            ImageIO.read(getClass().getResourceAsStream("/assets/eraser.png"))
                .getScaledInstance(
                    Config.menuIconSize, Config.menuIconSize, java.awt.Image.SCALE_SMOOTH));
        eraser2.setImage(
            ImageIO.read(getClass().getResourceAsStream("/assets/eraser2.png"))
                .getScaledInstance(
                    Config.menuIconSize, Config.menuIconSize, java.awt.Image.SCALE_SMOOTH));
        clear.setImage(
            ImageIO.read(getClass().getResourceAsStream("/assets/clear.png"))
                .getScaledInstance(
                    Config.menuIconSize, Config.menuIconSize, java.awt.Image.SCALE_SMOOTH));
        rankMarkOn.setImage(
            ImageIO.read(getClass().getResourceAsStream("/assets/RankMarkOn.png"))
                .getScaledInstance(
                    Config.menuIconSize, Config.menuIconSize, java.awt.Image.SCALE_SMOOTH));
        rankMarkOff.setImage(
            ImageIO.read(getClass().getResourceAsStream("/assets/RankMarkOff.png"))
                .getScaledInstance(
                    Config.menuIconSize, Config.menuIconSize, java.awt.Image.SCALE_SMOOTH));
      } catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }

      JFontButton btnOpen = new JFontButton(iconOpen);
      btnOpen.setFocusable(false);
      btnOpen.setPreferredSize(new Dimension(Config.menuHeight, Config.menuHeight));
      btnOpen.setToolTipText(resourceBundle.getString("Menu.btnOpen.toolTipText")); // "打开棋谱(O)");
      btnOpen.addActionListener(
          new ActionListener() {
            public void actionPerformed(ActionEvent e) {
              Lizzie.frame.openFile();
            }
          });

      JFontButton btnSave = new JFontButton(iconSave);
      btnSave.setFocusable(false);
      btnSave.setPreferredSize(new Dimension(Config.menuHeight, Config.menuHeight));
      btnSave.setToolTipText(resourceBundle.getString("Menu.btnSave.toolTipText")); // ("保存棋谱(S)");
      btnSave.addActionListener(
          new ActionListener() {
            public void actionPerformed(ActionEvent e) {
              Lizzie.frame.saveOriFile();
            }
          });

      JPopupMenu flashAnalyzePopup = new JPopupMenu();
      JFontMenuItem flashAnalyzeAllGame =
          new JFontMenuItem(resourceBundle.getString("Menu.flashAnalyzeAllGame")); // "闪电分析(全局)");
      flashAnalyzeAllGame.addActionListener(
          new ActionListener() {
            public void actionPerformed(ActionEvent e) {
              Lizzie.frame.flashAnalyzeGame(true);
            }
          });

      JFontMenuItem flashAnalyzePartGame =
          new JFontMenuItem(resourceBundle.getString("Menu.flashAnalyzePartGame")); // "闪电分析(部分)");
      flashAnalyzePartGame.addActionListener(
          new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
              Lizzie.frame.flashAnalyzePart();
            }
          });

      JFontMenuItem flashAnalyzeSettings =
          new JFontMenuItem(resourceBundle.getString("Menu.flashAnalyzeSettings")); // "闪电分析设置");
      flashAnalyzeSettings.addActionListener(
          new ActionListener() {
            public void actionPerformed(ActionEvent e) {
              Lizzie.frame.flashAnalyzeSettings();
            }
          });

      flashAnalyzePopup.add(flashAnalyzeAllGame);
      flashAnalyzePopup.add(flashAnalyzePartGame);
      flashAnalyzePopup.add(flashAnalyzeSettings);

      JFontButton btnFlashAnalyze = new JFontButton(flash);
      btnFlashAnalyze.setFocusable(false);
      btnFlashAnalyze.setPreferredSize(new Dimension(Config.menuHeight, Config.menuHeight));
      btnFlashAnalyze.setToolTipText(resourceBundle.getString("Menu.btnAnalyze.btnFlashAnalyze"));
      btnFlashAnalyze.addActionListener(
          new ActionListener() {
            public void actionPerformed(ActionEvent e) {
              flashAnalyzePopup.show(
                  Lizzie.frame.topPanel,
                  btnFlashAnalyze.getX(),
                  btnFlashAnalyze.getY() + btnFlashAnalyze.getHeight());
            }
          });

      JFontButton btnAnalyze = new JFontButton(iconAnalyze);
      btnAnalyze.setFocusable(false);
      btnAnalyze.setPreferredSize(new Dimension(Config.menuHeight, Config.menuHeight));
      btnAnalyze.setToolTipText(
          resourceBundle.getString("Menu.btnAnalyze.toolTipText")); // ("自动分析(A)");
      if (Lizzie.frame.toolbarHeight > 0 && Lizzie.config.batchOpen) {
        btnAnalyze.addActionListener(
            new ActionListener() {
              public void actionPerformed(ActionEvent e) {
                StartAnaDialog newgame = new StartAnaDialog(false, Lizzie.frame);
                newgame.setVisible(true);
                if (newgame.isCancelled()) {
                  LizzieFrame.toolbar.resetAutoAna();
                  return;
                }
              }
            });
      } else {
        JPopupMenu autoAnalyzePopup = new JPopupMenu();
        final JFontMenuItem autoAnalyze =
            new JFontMenuItem(resourceBundle.getString("Menu.autoAnalyze")); // ("自动分析(A)");
        autoAnalyze.addActionListener(
            new ActionListener() {
              @Override
              public void actionPerformed(ActionEvent e) {
                StartAnaDialog newgame = new StartAnaDialog(false, Lizzie.frame);
                newgame.setVisible(true);
                if (newgame.isCancelled()) {
                  LizzieFrame.toolbar.resetAutoAna();
                  return;
                }
              }
            });
        autoAnalyzePopup.add(autoAnalyze);

        final JFontMenuItem batchAnalyze =
            new JFontMenuItem(resourceBundle.getString("Menu.batchAnalyze")); // ("批量分析(Ctrl+O)");
        batchAnalyze.addActionListener(
            new ActionListener() {
              @Override
              public void actionPerformed(ActionEvent e) {
                Lizzie.frame.openFileWithAna(false);
              }
            });
        autoAnalyzePopup.add(batchAnalyze);

        final JFontMenuItem batchAnalysisMode =
            new JFontMenuItem(
                resourceBundle.getString("Menu.batchAnalysisMode")); // ("批量分析(闪电模式)");
        batchAnalysisMode.addActionListener(
            new ActionListener() {
              @Override
              public void actionPerformed(ActionEvent e) {
                Lizzie.frame.openFileWithAna(true);
              }
            });
        autoAnalyzePopup.add(batchAnalysisMode);

        final JFontMenuItem stopAutoAnalyze =
            new JFontMenuItem(resourceBundle.getString("Menu.stopAutoAnalyze")); // ("停止自动(批量)分析");
        autoAnalyzePopup.add(stopAutoAnalyze);
        stopAutoAnalyze.addActionListener(
            new ActionListener() {
              public void actionPerformed(ActionEvent e) {
                LizzieFrame.toolbar.stopAutoAna(true, true);
              }
            });

        final JFontMenuItem batchAnalyzeTable =
            new JFontMenuItem(resourceBundle.getString("Menu.batchAnalyzeTable")); // ("批量分析进度表");
        autoAnalyzePopup.add(batchAnalyzeTable);
        batchAnalyzeTable.addActionListener(
            new ActionListener() {
              public void actionPerformed(ActionEvent e) {
                Lizzie.frame.openAnalysisTable();
              }
            });

        btnAnalyze.addActionListener(
            new ActionListener() {
              public void actionPerformed(ActionEvent e) {
                autoAnalyzePopup.show(
                    Lizzie.frame.topPanel,
                    btnAnalyze.getX(),
                    btnAnalyze.getY() + btnAnalyze.getHeight());
              }
            });
      }

      JFontButton btnHawkeye = new JFontButton(iconHawkeye);
      btnHawkeye.setPreferredSize(new Dimension(Config.menuHeight, Config.menuHeight));
      btnHawkeye.setFocusable(false);
      btnHawkeye.setToolTipText(
          resourceBundle.getString("Menu.btnHawkeye.toolTipText")); // ("自动分析(A)");
      btnHawkeye.addActionListener(
          new ActionListener() {
            public void actionPerformed(ActionEvent e) {
              Lizzie.frame.toggleBadMoves();
            }
          });

      JPopupMenu rankMarkPopup = new JPopupMenu();
      addRankMarkMenu(rankMarkPopup, null);

      btnRankMark = new JFontButton();
      btnRankMark.setPreferredSize(new Dimension(Config.menuHeight, Config.menuHeight));
      btnRankMark.setFocusable(false);
      btnRankMark.setToolTipText(
          resourceBundle.getString("Menu.btnRankMark.toolTipText")); // ("自动分析(A)");
      btnRankMark.addActionListener(
          new ActionListener() {
            public void actionPerformed(ActionEvent e) {
              // aaa+地步工具栏
              rankMarkPopup.show(
                  Lizzie.frame.topPanel,
                  btnRankMark.getX(),
                  btnRankMark.getY() + btnRankMark.getHeight());
            }
          });
      setBtnRankMark();

      JFontButton btnSetMain = new JFontButton(iconSetMain);
      btnSetMain.setPreferredSize(new Dimension(Config.menuHeight, Config.menuHeight));
      btnSetMain.setFocusable(false);
      btnSetMain.setToolTipText(
          resourceBundle.getString("Menu.btnSetMain.toolTipText")); // ("设为主分支(L)");
      btnSetMain.addActionListener(
          new ActionListener() {
            public void actionPerformed(ActionEvent e) {
              Lizzie.frame.setAsMain();
            }
          });

      JFontButton btnBackMain = new JFontButton(iconBackMain);
      btnBackMain.setPreferredSize(new Dimension(Config.menuHeight, Config.menuHeight));
      btnBackMain.setFocusable(false);
      btnBackMain.setToolTipText(
          resourceBundle.getString("Menu.btnBackMain.toolTipText")); // ("返回主分支(T)");
      btnBackMain.addActionListener(
          new ActionListener() {
            public void actionPerformed(ActionEvent e) {
              Lizzie.frame.moveToMainTrunk();
            }
          });

      JFontButton btnChangeTurn = new JFontButton(iconChangeTurn);
      btnChangeTurn.setPreferredSize(new Dimension(Config.menuHeight, Config.menuHeight));
      btnChangeTurn.setFocusable(false);
      btnChangeTurn.setToolTipText(
          resourceBundle.getString("Menu.btnChangeTurn.toolTipText")); // ("设为主分支(L)");
      btnChangeTurn.addActionListener(
          new ActionListener() {
            public void actionPerformed(ActionEvent e) {
              Lizzie.board.changeNextTurn();
            }
          });

      JFontButton btnMarkup = new JFontButton();
      if (Lizzie.config.isShowingMarkupTools) btnMarkup.setIcon(iconMarkup2);
      else btnMarkup.setIcon(iconMarkup1);
      btnMarkup.setFocusable(false);
      btnMarkup.setPreferredSize(new Dimension(Config.menuHeight, Config.menuHeight));
      btnMarkup.setToolTipText(resourceBundle.getString("Menu.btnMarkup.toolTipText")); // "标记工具");
      btnMarkup.addActionListener(
          new ActionListener() {
            public void actionPerformed(ActionEvent e) {
              Lizzie.frame.setMarkupType(false, 0);
              Lizzie.config.isShowingMarkupTools = !Lizzie.config.isShowingMarkupTools;
              Lizzie.config.uiConfig.put("show-markup-tools", Lizzie.config.isShowingMarkupTools);
              doubleMenu(false);
            }
          });

      JFontButton btnMarkupLabel = new JFontButton();
      if (Lizzie.frame.markupType == 1) btnMarkupLabel.setIcon(markupLabel2);
      else btnMarkupLabel.setIcon(markupLabel1);
      btnMarkupLabel.setFocusable(false);
      btnMarkupLabel.setPreferredSize(new Dimension(Config.menuHeight, Config.menuHeight));
      btnMarkupLabel.addActionListener(
          new ActionListener() {
            public void actionPerformed(ActionEvent e) {
              if (Lizzie.frame.markupType == 1) Lizzie.frame.setMarkupType(false, 0);
              else Lizzie.frame.setMarkupType(true, 1);
              doubleMenu(false);
            }
          });

      JFontButton btnMarkupCircle = new JFontButton();
      if (Lizzie.frame.markupType == 2) btnMarkupCircle.setIcon(markupCircle2);
      else btnMarkupCircle.setIcon(markupCircle1);
      btnMarkupCircle.setFocusable(false);
      btnMarkupCircle.setPreferredSize(new Dimension(Config.menuHeight, Config.menuHeight));
      btnMarkupCircle.addActionListener(
          new ActionListener() {
            public void actionPerformed(ActionEvent e) {
              if (Lizzie.frame.markupType == 2) Lizzie.frame.setMarkupType(false, 0);
              else Lizzie.frame.setMarkupType(true, 2);
              doubleMenu(false);
            }
          });

      JFontButton btnMarkupX = new JFontButton();
      if (Lizzie.frame.markupType == 3) btnMarkupX.setIcon(markupX2);
      else btnMarkupX.setIcon(markupX1);
      btnMarkupX.setFocusable(false);
      btnMarkupX.setPreferredSize(new Dimension(Config.menuHeight, Config.menuHeight));
      btnMarkupX.addActionListener(
          new ActionListener() {
            public void actionPerformed(ActionEvent e) {
              if (Lizzie.frame.markupType == 3) Lizzie.frame.setMarkupType(false, 0);
              else Lizzie.frame.setMarkupType(true, 3);
              doubleMenu(false);
            }
          });

      JFontButton btnMarkupSquare = new JFontButton();
      if (Lizzie.frame.markupType == 4) btnMarkupSquare.setIcon(markupSquare2);
      else btnMarkupSquare.setIcon(markupSquare1);
      btnMarkupSquare.setFocusable(false);
      btnMarkupSquare.setPreferredSize(new Dimension(Config.menuHeight, Config.menuHeight));
      btnMarkupSquare.addActionListener(
          new ActionListener() {
            public void actionPerformed(ActionEvent e) {
              if (Lizzie.frame.markupType == 4) Lizzie.frame.setMarkupType(false, 0);
              else Lizzie.frame.setMarkupType(true, 4);
              doubleMenu(false);
            }
          });

      JFontButton btnMarkupTri = new JFontButton();
      if (Lizzie.frame.markupType == 5) btnMarkupTri.setIcon(markupsanjiao2);
      else btnMarkupTri.setIcon(markupsanjiao1);
      btnMarkupTri.setFocusable(false);
      btnMarkupTri.setPreferredSize(new Dimension(Config.menuHeight, Config.menuHeight));
      btnMarkupTri.addActionListener(
          new ActionListener() {
            public void actionPerformed(ActionEvent e) {
              if (Lizzie.frame.markupType == 5) Lizzie.frame.setMarkupType(false, 0);
              else Lizzie.frame.setMarkupType(true, 5);
              doubleMenu(false);
            }
          });

      JFontButton btnMarkupEraser = new JFontButton();
      if (Lizzie.frame.markupType == 6) btnMarkupEraser.setIcon(eraser2);
      else btnMarkupEraser.setIcon(eraser1);
      btnMarkupEraser.setFocusable(false);
      btnMarkupEraser.setPreferredSize(new Dimension(Config.menuHeight, Config.menuHeight));
      btnMarkupEraser.addActionListener(
          new ActionListener() {
            public void actionPerformed(ActionEvent e) {
              if (Lizzie.frame.markupType == 6) Lizzie.frame.setMarkupType(false, 0);
              else Lizzie.frame.setMarkupType(true, 6);
              doubleMenu(false);
            }
          });

      JFontButton btnMarkupClear = new JFontButton(clear);
      btnMarkupClear.setFocusable(false);
      btnMarkupClear.setPreferredSize(new Dimension(Config.menuHeight, Config.menuHeight));
      btnMarkupClear.setToolTipText(
          resourceBundle.getString("Menu.btnMarkupClear.toolTipText")); // ("清除");
      btnMarkupClear.addActionListener(
          new ActionListener() {
            public void actionPerformed(ActionEvent e) {
              Lizzie.board.getHistory().getData().getProperties().clear();
              Lizzie.frame.refresh();
            }
          });

      // Lizzie.frame.topPanel.addSeparator();
      Lizzie.frame.topPanel.add(btnOpen);
      Lizzie.frame.topPanel.add(btnSave);
      Lizzie.frame.topPanel.add(btnFlashAnalyze);
      Lizzie.frame.topPanel.add(btnAnalyze);
      Lizzie.frame.topPanel.add(btnHawkeye);
      Lizzie.frame.topPanel.add(btnRankMark);
      Lizzie.frame.topPanel.add(btnChangeTurn);
      Lizzie.frame.topPanel.add(btnSetMain);
      Lizzie.frame.topPanel.add(btnBackMain);
      Lizzie.frame.topPanel.add(btnMarkup);
      if (Lizzie.config.isShowingMarkupTools) {
        Lizzie.frame.topPanel.add(btnMarkupLabel);
        Lizzie.frame.topPanel.add(btnMarkupCircle);
        Lizzie.frame.topPanel.add(btnMarkupX);
        Lizzie.frame.topPanel.add(btnMarkupSquare);
        Lizzie.frame.topPanel.add(btnMarkupTri);
        Lizzie.frame.topPanel.add(btnMarkupEraser);
        Lizzie.frame.topPanel.add(btnMarkupClear);
      }
      Lizzie.frame.topPanel.addSeparator(new Dimension(8, Config.menuHeight + 2));
    }
    Lizzie.frame.topPanel.add(black);
    Lizzie.frame.topPanel.add(white);
    Lizzie.frame.topPanel.add(blackwhite);
    blackwhite.setPreferredSize(
        new Dimension((int) blackwhite.getPreferredSize().width, Config.menuHeight));
    Lizzie.frame.topPanel.add(playPass);
    playPass.setPreferredSize(
        new Dimension((int) playPass.getPreferredSize().width, Config.menuHeight));
    if (Lizzie.config.showEditbar)
      Lizzie.frame.topPanel.addSeparator(new Dimension(8, Config.menuHeight + 2));
    if (Lizzie.config.showForceMenu) {
      this.remove(selectAllowMore);
      this.remove(selectAvoidMore);
      ImageIcon horizonDown;
      horizonDown = new ImageIcon();
      try {
        horizonDown.setImage(
            ImageIO.read(getClass().getResourceAsStream("/assets/horizonDown.png"))
                .getScaledInstance(
                    Lizzie.config.isFrameFontSmall()
                        ? 12
                        : (Lizzie.config.isFrameFontMiddle() ? 15 : 18),
                    Lizzie.config.isFrameFontSmall()
                        ? 20
                        : (Lizzie.config.isFrameFontMiddle() ? 25 : 30),
                    java.awt.Image.SCALE_SMOOTH));
      } catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }

      JPopupMenu selectAllowPopup = new JPopupMenu();
      JFontCheckBoxMenuItem selectAllowCustomMove = new JFontCheckBoxMenuItem();
      JFontTextField txtLimitLengthAllow = new JFontTextField();
      txtLimitLengthAllow.setDocument(new IntDocument());
      txtLimitLengthAllow.setPreferredSize(
          new Dimension(
              50,
              Lizzie.config.isFrameFontSmall()
                  ? 20
                  : (Lizzie.config.isFrameFontMiddle() ? 22 : 24)));
      selectAllowCustomMove.setLayout(null);
      JFontLabel customNumberAllow =
          new JFontLabel(
              resourceBundle.getString(
                  "Menu.selectLimitCustomMoves")); // "Custom limit moves:");//自定义限制手数:
      customNumberAllow.setBounds(
          Lizzie.config.useJavaLooks ? 20 : 37,
          Lizzie.config.isFrameFontSmall() ? 1 : (Lizzie.config.isFrameFontMiddle() ? 1 : 3),
          180,
          25);
      selectAllowCustomMove.add(customNumberAllow);
      selectAllowCustomMove.add(txtLimitLengthAllow);
      if (Lizzie.config.isChinese) {
        txtLimitLengthAllow.setBounds(
            Lizzie.config.isFrameFontSmall()
                ? 126
                : (Lizzie.config.isFrameFontMiddle() ? 156 : 188),
            Lizzie.config.isFrameFontSmall() ? 4 : (Lizzie.config.isFrameFontMiddle() ? 3 : 4),
            40,
            Lizzie.config.isFrameFontSmall() ? 20 : (Lizzie.config.isFrameFontMiddle() ? 22 : 24));
        selectAllowCustomMove.setPreferredSize(
            new Dimension(
                Lizzie.config.isFrameFontSmall()
                    ? 170
                    : (Lizzie.config.isFrameFontMiddle() ? 205 : 233),
                Lizzie.config.isFrameFontSmall()
                    ? 27
                    : (Lizzie.config.isFrameFontMiddle() ? 27 : 30)));
      } else {
        txtLimitLengthAllow.setBounds(
            Lizzie.config.isFrameFontSmall()
                ? 156
                : (Lizzie.config.isFrameFontMiddle() ? 191 : 228),
            Lizzie.config.isFrameFontSmall() ? 4 : (Lizzie.config.isFrameFontMiddle() ? 3 : 4),
            40,
            Lizzie.config.isFrameFontSmall() ? 20 : (Lizzie.config.isFrameFontMiddle() ? 22 : 24));
        selectAllowCustomMove.setPreferredSize(
            new Dimension(
                Lizzie.config.isFrameFontSmall()
                    ? 200
                    : (Lizzie.config.isFrameFontMiddle() ? 240 : 273),
                Lizzie.config.isFrameFontSmall()
                    ? 27
                    : (Lizzie.config.isFrameFontMiddle() ? 27 : 30)));
      }

      JFontCheckBoxMenuItem selectAllowAllMove =
          new JFontCheckBoxMenuItem(
              resourceBundle.getString(
                  "Menu.selectLimitAllMoves")); // "Limit All moves");//限制整个搜索过程
      JFontCheckBoxMenuItem selectAllowOneMove =
          new JFontCheckBoxMenuItem(
              resourceBundle.getString("Menu.selectLimitOneMove")); // ("Limit first move");//仅限制第一手
      selectAllowPopup.add(selectAllowAllMove);
      selectAllowPopup.add(selectAllowOneMove);
      selectAllowPopup.add(selectAllowCustomMove);
      selectAllowPopup.setVisible(true);
      selectAllowPopup.setVisible(false);

      selectAllowPopup.addPopupMenuListener(
          new PopupMenuListener() {
            public void popupMenuCanceled(PopupMenuEvent e) {}

            public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
              Lizzie.config.selectAllowCustomMoves =
                  Utils.parseTextToInt(txtLimitLengthAllow, Lizzie.config.selectAllowCustomMoves);
              Lizzie.config.uiConfig.put(
                  "select-allow-custom-moves", Lizzie.config.selectAllowCustomMoves);
              if (selectAllowCustomMove.isSelected()) {
                Lizzie.config.selectAllowMoves = Lizzie.config.selectAllowCustomMoves;
                Lizzie.config.uiConfig.put("select-allow-moves", Lizzie.config.selectAllowMoves);
              }
              Runnable runnable =
                  new Runnable() {
                    public void run() {
                      try {
                        Thread.sleep(100);
                      } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                      }
                      if (Lizzie.leelaz.isPondering()) Lizzie.leelaz.ponder();
                    }
                  };
              Thread thread = new Thread(runnable);
              thread.start();
            }

            public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
              if (Lizzie.config.selectAllowMoves == 999) {
                selectAllowAllMove.setSelected(true);
                selectAllowOneMove.setSelected(false);
                selectAllowCustomMove.setSelected(false);
              } else if (Lizzie.config.selectAllowMoves == 1) {
                selectAllowAllMove.setSelected(false);
                selectAllowOneMove.setSelected(true);
                selectAllowCustomMove.setSelected(false);
              } else {
                selectAllowAllMove.setSelected(false);
                selectAllowOneMove.setSelected(false);
                selectAllowCustomMove.setSelected(true);
              }
              txtLimitLengthAllow.setText(String.valueOf(Lizzie.config.selectAllowCustomMoves));
            }
          });

      selectAllowAllMove.addActionListener(
          new ActionListener() {
            public void actionPerformed(ActionEvent e) {
              Lizzie.config.selectAllowMoves = 999;
              Lizzie.config.uiConfig.put("select-allow-moves", Lizzie.config.selectAllowMoves);
            }
          });

      selectAllowOneMove.addActionListener(
          new ActionListener() {
            public void actionPerformed(ActionEvent e) {
              Lizzie.config.selectAllowMoves = 1;
              Lizzie.config.uiConfig.put("select-allow-moves", Lizzie.config.selectAllowMoves);
            }
          });

      selectAllowCustomMove.addActionListener(
          new ActionListener() {
            public void actionPerformed(ActionEvent e) {
              Lizzie.config.selectAllowCustomMoves =
                  Utils.parseTextToInt(txtLimitLengthAllow, Lizzie.config.selectAllowCustomMoves);
              Lizzie.config.uiConfig.put(
                  "select-allow-custom-moves", Lizzie.config.selectAllowCustomMoves);

              Lizzie.config.selectAllowMoves = Lizzie.config.selectAllowCustomMoves;
              Lizzie.config.uiConfig.put("select-allow-moves", Lizzie.config.selectAllowMoves);
            }
          });

      JButton selectAllowMore = new JButton(horizonDown);
      selectAllowMore.setFocusable(false);
      selectAllowMore.setPreferredSize(
          new Dimension(
              (Lizzie.config.isFrameFontSmall()
                  ? 12
                  : (Lizzie.config.isFrameFontMiddle() ? 15 : 18)),
              (Lizzie.config.isFrameFontSmall()
                  ? 20
                  : (Lizzie.config.isFrameFontMiddle() ? 25 : 30))));
      selectAllowMore.addActionListener(
          new ActionListener() {
            public void actionPerformed(ActionEvent e) {
              selectAllowPopup.show(
                  Lizzie.frame.topPanel,
                  selectAllow.getX(),
                  selectAllowMore.getY() + selectAllowMore.getHeight());
            }
          });

      JPopupMenu selectAvoidPopup = new JPopupMenu();
      JFontCheckBoxMenuItem selectAvoidCustomMove = new JFontCheckBoxMenuItem();
      JFontTextField txtLimitLengthAvoid = new JFontTextField();
      txtLimitLengthAllow.setDocument(new IntDocument());
      txtLimitLengthAvoid.setPreferredSize(
          new Dimension(
              50,
              Lizzie.config.isFrameFontSmall()
                  ? 20
                  : (Lizzie.config.isFrameFontMiddle() ? 22 : 24)));
      selectAvoidCustomMove.setLayout(null);
      JFontLabel customNumberAvoid =
          new JFontLabel(
              resourceBundle.getString(
                  "Menu.selectLimitCustomMoves")); // "Custom limit moves:");//自定义限制手数:
      customNumberAvoid.setBounds(
          Lizzie.config.useJavaLooks ? 20 : 37,
          Lizzie.config.isFrameFontSmall() ? 1 : (Lizzie.config.isFrameFontMiddle() ? 1 : 3),
          180,
          25);
      selectAvoidCustomMove.add(customNumberAvoid);
      selectAvoidCustomMove.add(txtLimitLengthAvoid);
      if (Lizzie.config.isChinese) {
        txtLimitLengthAvoid.setBounds(
            Lizzie.config.isFrameFontSmall()
                ? 126
                : (Lizzie.config.isFrameFontMiddle() ? 156 : 188),
            Lizzie.config.isFrameFontSmall() ? 4 : (Lizzie.config.isFrameFontMiddle() ? 3 : 4),
            40,
            Lizzie.config.isFrameFontSmall() ? 20 : (Lizzie.config.isFrameFontMiddle() ? 22 : 24));
        selectAvoidCustomMove.setPreferredSize(
            new Dimension(
                Lizzie.config.isFrameFontSmall()
                    ? 170
                    : (Lizzie.config.isFrameFontMiddle() ? 205 : 233),
                Lizzie.config.isFrameFontSmall()
                    ? 27
                    : (Lizzie.config.isFrameFontMiddle() ? 27 : 30)));
      } else {
        txtLimitLengthAvoid.setBounds(
            Lizzie.config.isFrameFontSmall()
                ? 156
                : (Lizzie.config.isFrameFontMiddle() ? 191 : 228),
            Lizzie.config.isFrameFontSmall() ? 4 : (Lizzie.config.isFrameFontMiddle() ? 3 : 4),
            40,
            Lizzie.config.isFrameFontSmall() ? 20 : (Lizzie.config.isFrameFontMiddle() ? 22 : 24));
        selectAvoidCustomMove.setPreferredSize(
            new Dimension(
                Lizzie.config.isFrameFontSmall()
                    ? 200
                    : (Lizzie.config.isFrameFontMiddle() ? 240 : 273),
                Lizzie.config.isFrameFontSmall()
                    ? 27
                    : (Lizzie.config.isFrameFontMiddle() ? 27 : 30)));
      }

      JFontCheckBoxMenuItem selectAvoidAllMove =
          new JFontCheckBoxMenuItem(
              resourceBundle.getString(
                  "Menu.selectLimitAllMoves")); // "Limit All moves");//限制整个搜索过程
      JFontCheckBoxMenuItem selectAvoidOneMove =
          new JFontCheckBoxMenuItem(
              resourceBundle.getString("Menu.selectLimitOneMove")); // ("Limit first move");//仅限制第一手
      selectAvoidPopup.add(selectAvoidAllMove);
      selectAvoidPopup.add(selectAvoidOneMove);
      selectAvoidPopup.add(selectAvoidCustomMove);
      selectAvoidPopup.setVisible(true);
      selectAvoidPopup.setVisible(false);

      selectAvoidPopup.addPopupMenuListener(
          new PopupMenuListener() {
            public void popupMenuCanceled(PopupMenuEvent e) {}

            public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
              Lizzie.config.selectAvoidCustomMoves =
                  Utils.parseTextToInt(txtLimitLengthAvoid, Lizzie.config.selectAvoidCustomMoves);
              Lizzie.config.uiConfig.put(
                  "select-avoid-custom-moves", Lizzie.config.selectAvoidCustomMoves);
              if (selectAvoidCustomMove.isSelected()) {
                Lizzie.config.selectAvoidMoves = Lizzie.config.selectAvoidCustomMoves;
                Lizzie.config.uiConfig.put("select-avoid-moves", Lizzie.config.selectAvoidMoves);
              }
              Runnable runnable =
                  new Runnable() {
                    public void run() {
                      try {
                        Thread.sleep(100);
                      } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                      }
                      if (Lizzie.leelaz.isPondering()) Lizzie.leelaz.ponder();
                    }
                  };
              Thread thread = new Thread(runnable);
              thread.start();
            }

            public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
              if (Lizzie.config.selectAvoidMoves == 999) {
                selectAvoidAllMove.setSelected(true);
                selectAvoidOneMove.setSelected(false);
                selectAvoidCustomMove.setSelected(false);
              } else if (Lizzie.config.selectAvoidMoves == 1) {
                selectAvoidAllMove.setSelected(false);
                selectAvoidOneMove.setSelected(true);
                selectAvoidCustomMove.setSelected(false);
              } else {
                selectAvoidAllMove.setSelected(false);
                selectAvoidOneMove.setSelected(false);
                selectAvoidCustomMove.setSelected(true);
              }
              txtLimitLengthAvoid.setText(String.valueOf(Lizzie.config.selectAvoidCustomMoves));
            }
          });

      selectAvoidAllMove.addActionListener(
          new ActionListener() {
            public void actionPerformed(ActionEvent e) {
              Lizzie.config.selectAvoidMoves = 999;
              Lizzie.config.uiConfig.put("select-avoid-moves", Lizzie.config.selectAvoidMoves);
            }
          });

      selectAvoidOneMove.addActionListener(
          new ActionListener() {
            public void actionPerformed(ActionEvent e) {
              Lizzie.config.selectAvoidMoves = 1;
              Lizzie.config.uiConfig.put("select-avoid-moves", Lizzie.config.selectAvoidMoves);
            }
          });

      selectAvoidCustomMove.addActionListener(
          new ActionListener() {
            public void actionPerformed(ActionEvent e) {
              Lizzie.config.selectAvoidCustomMoves =
                  Utils.parseTextToInt(txtLimitLengthAvoid, Lizzie.config.selectAvoidCustomMoves);
              Lizzie.config.uiConfig.put(
                  "select-avoid-custom-moves", Lizzie.config.selectAvoidCustomMoves);

              Lizzie.config.selectAvoidMoves = Lizzie.config.selectAvoidCustomMoves;
              Lizzie.config.uiConfig.put("select-avoid-moves", Lizzie.config.selectAvoidMoves);
            }
          });

      JButton selectAvoidMore = new JButton(horizonDown);
      selectAvoidMore.setFocusable(false);
      selectAvoidMore.setPreferredSize(
          new Dimension(
              (Lizzie.config.isFrameFontSmall()
                  ? 12
                  : (Lizzie.config.isFrameFontMiddle() ? 15 : 18)),
              (Lizzie.config.isFrameFontSmall()
                  ? 20
                  : (Lizzie.config.isFrameFontMiddle() ? 25 : 30))));
      selectAvoidMore.addActionListener(
          new ActionListener() {
            public void actionPerformed(ActionEvent e) {
              selectAvoidPopup.show(
                  Lizzie.frame.topPanel,
                  selectAvoid.getX(),
                  selectAvoidMore.getY() + selectAvoidMore.getHeight());
            }
          });

      Lizzie.frame.topPanel.add(selectAllow);
      selectAllow.setPreferredSize(
          new Dimension((int) selectAllow.getPreferredSize().width, Config.menuHeight));
      Lizzie.frame.topPanel.add(selectAllowMore);
      Lizzie.frame.topPanel.add(selectAvoid);
      selectAvoid.setPreferredSize(
          new Dimension((int) selectAvoid.getPreferredSize().width, Config.menuHeight));
      Lizzie.frame.topPanel.add(selectAvoidMore);
      Lizzie.frame.topPanel.add(clearSelect);
      clearSelect.setPreferredSize(
          new Dimension((int) clearSelect.getPreferredSize().width, Config.menuHeight));
      Lizzie.frame.topPanel.addSeparator(new Dimension(8, Config.menuHeight + 2));
      //  forceSep.setVisible(selectAllow.isVisible());
      //  selectAvoid.setVisible(selectAllow.isVisible());
      // clearSelect.setVisible(selectAllow.isVisible());
    }

    doubleMenuNewGame =
        new JFontButton(
            (EngineManager.isEngineGame
                    || Lizzie.frame.isPlayingAgainstLeelaz
                    || Lizzie.frame.isAnaPlayingAgainstLeelaz)
                ? resourceBundle.getString("Menu.endGameBtn")
                : resourceBundle.getString("Menu.newGameBtn"));
    doubleMenuPauseGame = new JFontButton(resourceBundle.getString("Menu.pauseGameBtn"));
    if (Lizzie.leelaz != null && Lizzie.leelaz.isGamePaused)
      doubleMenuPauseGame.setText(resourceBundle.getString("Menu.continueGameBtn"));
    else doubleMenuPauseGame.setText(resourceBundle.getString("Menu.pauseGameBtn"));
    doubleMenuNewGame.setFocusable(false);
    doubleMenuPauseGame.setFocusable(false);

    JPopupMenu newGamePopup = new JPopupMenu();

    JFontMenuItem engineGame =
        new JFontMenuItem(resourceBundle.getString("Menu.newEngineGame")); // ("引擎对局(Alt+E)");
    engineGame.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            Lizzie.frame.startEngineGameDialog();
          }
        });

    JFontMenuItem analyzeGame = new JFontMenuItem(); // ("人机对局(分析模式 N)");

    analyzeGame.setLayout(null);
    if (Lizzie.config.isChinese)
      analyzeGame.setPreferredSize(
          new Dimension(
              (Lizzie.config.useJavaLooks ? -31 : 0)
                  + (Lizzie.config.isFrameFontSmall()
                      ? 200
                      : (Lizzie.config.isFrameFontMiddle() ? 250 : 300)),
              (Lizzie.config.useJavaLooks
                  ? (Lizzie.config.isFrameFontSmall()
                      ? 20
                      : (Lizzie.config.isFrameFontMiddle() ? 25 : 30))
                  : (Lizzie.config.isFrameFontSmall()
                      ? 25
                      : (Lizzie.config.isFrameFontMiddle() ? 27 : 30)))));
    else
      analyzeGame.setPreferredSize(
          new Dimension(
              (Lizzie.config.useJavaLooks ? -31 : 0)
                  + (Lizzie.config.isFrameFontSmall()
                      ? 253
                      : (Lizzie.config.isFrameFontMiddle() ? 310 : 380)),
              (Lizzie.config.useJavaLooks
                  ? (Lizzie.config.isFrameFontSmall()
                      ? 20
                      : (Lizzie.config.isFrameFontMiddle() ? 25 : 30))
                  : (Lizzie.config.isFrameFontSmall()
                      ? 25
                      : (Lizzie.config.isFrameFontMiddle() ? 27 : 30)))));
    JFontLabel lblAnalyzeGame =
        new JFontLabel(
            resourceBundle.getString(
                "Menu.newAnalyzeModeGame")); // "Custom limit moves:");//自定义限制手数:
    lblAnalyzeGame.setBounds(
        Lizzie.config.useJavaLooks ? 6 : 37,
        (Lizzie.config.useJavaLooks
            ? -1
            : (Lizzie.config.isFrameFontSmall()
                ? 2
                : (Lizzie.config.isFrameFontMiddle() ? 1 : -1))),
        320,
        Lizzie.config.isFrameFontSmall() ? 20 : (Lizzie.config.isFrameFontMiddle() ? 25 : 30));
    JButton aboutAnalyzeGame = new JFontButton("?");
    aboutAnalyzeGame.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            Lizzie.frame.showAnalyzeGenmoveInfo();
          }
        });
    aboutAnalyzeGame.setFocusable(false);
    aboutAnalyzeGame.setMargin(new Insets(0, 0, 0, 0));
    if (Lizzie.config.isChinese)
      aboutAnalyzeGame.setBounds(
          (Lizzie.config.useJavaLooks ? -31 : 0)
              + (Lizzie.config.isFrameFontSmall()
                  ? 177
                  : (Lizzie.config.isFrameFontMiddle() ? 220 : 270)),
          (Lizzie.config.useJavaLooks
              ? 1
              : (Lizzie.config.isFrameFontSmall()
                  ? 3
                  : (Lizzie.config.isFrameFontMiddle() ? 2 : 1))),
          Config.menuHeight - 2,
          Config.menuHeight - 2);
    else
      aboutAnalyzeGame.setBounds(
          (Lizzie.config.useJavaLooks ? -31 : 0)
              + (Lizzie.config.isFrameFontSmall()
                  ? 230
                  : (Lizzie.config.isFrameFontMiddle() ? 280 : 350)),
          (Lizzie.config.useJavaLooks
              ? 1
              : (Lizzie.config.isFrameFontSmall()
                  ? 3
                  : (Lizzie.config.isFrameFontMiddle() ? 2 : 1))),
          Config.menuHeight - 2,
          Config.menuHeight - 2);
    analyzeGame.add(aboutAnalyzeGame);
    analyzeGame.add(lblAnalyzeGame);
    analyzeGame.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            Lizzie.frame.startAnalyzeGameDialog();
          }
        });

    JFontMenuItem genmoveGame =
        new JFontMenuItem(
            resourceBundle.getString("Menu.newGenmoveGame")); // ("人机对局(Genmove模式 Alt+N)");
    genmoveGame.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            Lizzie.frame.startNewGame();
          }
        });

    newGamePopup.add(genmoveGame);
    newGamePopup.add(analyzeGame);
    newGamePopup.add(engineGame);

    doubleMenuNewGame.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            if (EngineManager.isEngineGame) {
              Lizzie.engineManager.stopEngineGame(-1, true);
              return;
            }
            if (Lizzie.frame.isPlayingAgainstLeelaz || Lizzie.frame.isAnaPlayingAgainstLeelaz) {
              Lizzie.frame.togglePonderMannul();
              if (Lizzie.config.isChinese) doubleMenu(false);
              return;
            }
            newGamePopup.show(
                Lizzie.frame.topPanel,
                doubleMenuNewGame.getX(),
                doubleMenuNewGame.getY() + doubleMenuNewGame.getHeight());
          }
        });

    doubleMenuPauseGame.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            pauseGame();
            if (!Lizzie.config.isChinese) doubleMenu(false);
          }
        });
    doubleMenuResign = new JFontButton(resourceBundle.getString("Menu.resignBtn"));
    doubleMenuResign.setFocusable(false);
    doubleMenuResign.setVisible(false);
    doubleMenuResign.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            if (Lizzie.frame.isPlayingAgainstLeelaz || Lizzie.frame.isAnaPlayingAgainstLeelaz) {
              if (Lizzie.frame.playerIsBlack) {
                GameInfo gameInfo = Lizzie.board.getHistory().getGameInfo();
                gameInfo.setResult(resourceBundle.getString("Leelaz.whiteWin"));
              } else {
                GameInfo gameInfo = Lizzie.board.getHistory().getGameInfo();
                gameInfo.setResult(resourceBundle.getString("Leelaz.blackWin"));
              }
              Lizzie.frame.togglePonderMannul();
              return;
            }
          }
        });

    doubleMenuStopGame = new JFontButton(resourceBundle.getString("Menu.endGameBtn"));
    doubleMenuStopGame.setFocusable(false);
    doubleMenuStopGame.setVisible(false);
    doubleMenuStopGame.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            if (EngineManager.isEngineGame) {
              Lizzie.engineManager.stopEngineGame(-1, true);
              return;
            }
            if (Lizzie.frame.isPlayingAgainstLeelaz || Lizzie.frame.isAnaPlayingAgainstLeelaz) {
              Lizzie.frame.togglePonderMannul();
              if (Lizzie.config.isChinese) doubleMenu(false);
              return;
            }
          }
        });

    if (Lizzie.config.showDoubleMenuGameControl) {
      Lizzie.frame.topPanel.add(doubleMenuNewGame);
      Lizzie.frame.topPanel.add(doubleMenuStopGame);
      doubleMenuStopGame.setVisible(false);
      doubleMenuNewGame.setPreferredSize(
          new Dimension(doubleMenuNewGame.getPreferredSize().width, Config.menuHeight));
      doubleMenuStopGame.setPreferredSize(
          new Dimension(doubleMenuStopGame.getPreferredSize().width, Config.menuHeight));
      Lizzie.frame.topPanel.add(doubleMenuPauseGame);
      doubleMenuPauseGame.setPreferredSize(
          new Dimension((int) doubleMenuPauseGame.getPreferredSize().width, Config.menuHeight));
      Lizzie.frame.topPanel.add(doubleMenuResign);
      doubleMenuResign.setPreferredSize(
          new Dimension((int) doubleMenuResign.getPreferredSize().width, Config.menuHeight));
      doubleMenuNewGame.setMargin(new Insets(0, 0, 0, 0));
      doubleMenuPauseGame.setMargin(new Insets(0, 0, 0, 0));
      doubleMenuResign.setMargin(new Insets(0, 0, 0, 0));
      Lizzie.frame.topPanel.addSeparator(new Dimension(8, Config.menuHeight + 2));
      toggleDoubleMenuGameStatus();
    }
    setKomiPanelInDoubleMenu();
    if (!first) {
      Lizzie.frame.reSetLoc();
      Lizzie.frame.topPanel.revalidate();
      repaint();
    }
  }

  private void addRankMarkMenu(JPopupMenu rankPopupMenu, JMenu rankJMenu) {
    JFontCheckBoxMenuItem rankCustomMove = new JFontCheckBoxMenuItem();
    JFontTextField txtCustomMove = new JFontTextField();
    txtCustomMove.setDocument(new IntDocument());
    Document dtCustomMove = txtCustomMove.getDocument();
    dtCustomMove.addDocumentListener(
        new DocumentListener() {
          public void insertUpdate(DocumentEvent e) {
            Lizzie.config.txtMoveRankMarkLastMove =
                Math.max(
                    1, Utils.parseTextToInt(txtCustomMove, Lizzie.config.txtMoveRankMarkLastMove));
            Lizzie.config.uiConfig.put(
                "txt-move-rank-mark-last-move", Lizzie.config.txtMoveRankMarkLastMove);
          }

          public void removeUpdate(DocumentEvent e) {
            Lizzie.config.txtMoveRankMarkLastMove =
                Math.max(
                    1, Utils.parseTextToInt(txtCustomMove, Lizzie.config.txtMoveRankMarkLastMove));
            Lizzie.config.uiConfig.put(
                "txt-move-rank-mark-last-move", Lizzie.config.txtMoveRankMarkLastMove);
          }

          public void changedUpdate(DocumentEvent e) {
            Lizzie.config.txtMoveRankMarkLastMove =
                Math.max(
                    1, Utils.parseTextToInt(txtCustomMove, Lizzie.config.txtMoveRankMarkLastMove));
            Lizzie.config.uiConfig.put(
                "txt-move-rank-mark-last-move", Lizzie.config.txtMoveRankMarkLastMove);
          }
        });

    rankCustomMove.setLayout(null);
    JFontLabel lblCustomMove =
        new JFontLabel(resourceBundle.getString("Menu.rankMenu.lblCustomMove"));
    lblCustomMove.setBounds(
        Lizzie.config.useJavaLooks ? 20 : 37,
        Lizzie.config.isFrameFontSmall()
            ? Lizzie.config.useJavaLooks ? -3 : 1
            : (Lizzie.config.isFrameFontMiddle() ? 1 : 3),
        180,
        25);
    rankCustomMove.add(lblCustomMove);
    rankCustomMove.add(txtCustomMove);

    if (Lizzie.config.isChinese) {
      txtCustomMove.setBounds(
          (Lizzie.config.useJavaLooks ? 0 : 17)
              + (Lizzie.config.isFrameFontSmall()
                  ? 103
                  : (Lizzie.config.isFrameFontMiddle() ? 131 : 158)),
          Lizzie.config.isFrameFontSmall()
              ? Lizzie.config.useJavaLooks ? 0 : 3
              : (Lizzie.config.isFrameFontMiddle() ? 3 : 4),
          Lizzie.config.isFrameFontSmall() ? 30 : (Lizzie.config.isFrameFontMiddle() ? 40 : 50),
          Lizzie.config.isFrameFontSmall() ? 20 : (Lizzie.config.isFrameFontMiddle() ? 22 : 24));
      lblCustomMove.setPreferredSize(
          new Dimension(
              Lizzie.config.isFrameFontSmall()
                  ? 170
                  : (Lizzie.config.isFrameFontMiddle() ? 205 : 233),
              Lizzie.config.isFrameFontSmall()
                  ? 27
                  : (Lizzie.config.isFrameFontMiddle() ? 27 : 30)));
    } else {
      txtCustomMove.setBounds(
          (Lizzie.config.useJavaLooks ? 20 : 37)
              + (Lizzie.config.isFrameFontSmall()
                  ? 103
                  : (Lizzie.config.isFrameFontMiddle() ? 131 : 158)),
          Lizzie.config.isFrameFontSmall()
              ? Lizzie.config.useJavaLooks ? 0 : 3
              : (Lizzie.config.isFrameFontMiddle() ? 3 : 4),
          Lizzie.config.isFrameFontSmall() ? 30 : (Lizzie.config.isFrameFontMiddle() ? 35 : 50),
          Lizzie.config.isFrameFontSmall() ? 20 : (Lizzie.config.isFrameFontMiddle() ? 22 : 24));
      lblCustomMove.setPreferredSize(
          new Dimension(
              Lizzie.config.isFrameFontSmall()
                  ? 170
                  : (Lizzie.config.isFrameFontMiddle() ? 205 : 233),
              Lizzie.config.isFrameFontSmall()
                  ? 27
                  : (Lizzie.config.isFrameFontMiddle() ? 27 : 30)));
    }
    JFontCheckBoxMenuItem rankLastMove =
        new JFontCheckBoxMenuItem(resourceBundle.getString("Menu.rankMenu.rankLastMove"));
    JFontCheckBoxMenuItem rankAllMove =
        new JFontCheckBoxMenuItem(resourceBundle.getString("Menu.rankMenu.rankAllMove"));
    JFontCheckBoxMenuItem rankNoneMove =
        new JFontCheckBoxMenuItem(resourceBundle.getString("Menu.rankMenu.rankNoneMove"));
    JFontCheckBoxMenuItem setCustomMoves =
        new JFontCheckBoxMenuItem(
            resourceBundle.getString("Menu.rankMenu.setCustomMoves")
                + Lizzie.config.txtMoveRankMarkLastMove);
    JFontCheckBoxMenuItem showMoveRankInOrigin =
        new JFontCheckBoxMenuItem(resourceBundle.getString("Menu.showMoveRankInOrigin"));
    JFontMenuItem chkUseWinScore = new JFontMenuItem();
    chkUseWinScore.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));
    chkUseWinScore.add(new JFontLabel(resourceBundle.getString("Menu.rankMenu.base"))); // 依据:
    JFontCheckBox chkWin = new JFontCheckBox(resourceBundle.getString("Menu.rankMenu.win"));
    chkWin.setOpaque(false);
    JFontCheckBox chkScore = new JFontCheckBox(resourceBundle.getString("Menu.rankMenu.score"));
    chkScore.setOpaque(false);
    chkWin.setToolTipText(resourceBundle.getString("Menu.rankMenu.base.tooltips"));
    chkScore.setToolTipText(resourceBundle.getString("Menu.rankMenu.base.tooltips"));
    chkUseWinScore.setToolTipText(resourceBundle.getString("Menu.rankMenu.base.tooltips"));
    chkWin.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            if (!chkScore.isSelected() && !chkWin.isSelected()) {
              chkScore.setSelected(true);
              Lizzie.config.useScoreLossInMoveRank = chkScore.isSelected();
              Lizzie.config.uiConfig.put(
                  "use-score-loss-in-move-rank", Lizzie.config.useScoreLossInMoveRank);
            }
            Lizzie.config.useWinLossInMoveRank = chkWin.isSelected();
            Lizzie.config.uiConfig.put(
                "use-win-loss-in-move-rank", Lizzie.config.useWinLossInMoveRank);
            Lizzie.frame.refresh();
          }
        });
    chkScore.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            if (!chkScore.isSelected() && !chkWin.isSelected()) {
              chkWin.setSelected(true);
              Lizzie.config.useWinLossInMoveRank = chkWin.isSelected();
              Lizzie.config.uiConfig.put(
                  "use-win-loss-in-move-rank", Lizzie.config.useWinLossInMoveRank);
            }
            Lizzie.config.useScoreLossInMoveRank = chkScore.isSelected();
            Lizzie.config.uiConfig.put(
                "use-score-loss-in-move-rank", Lizzie.config.useScoreLossInMoveRank);
            Lizzie.frame.refresh();
          }
        });
    chkUseWinScore.add(chkWin);
    chkUseWinScore.add(chkScore);
    if (rankPopupMenu != null) {
      rankPopupMenu.add(rankNoneMove);
      rankPopupMenu.add(rankLastMove);
      rankPopupMenu.add(rankCustomMove);
      rankPopupMenu.add(rankAllMove);
      rankPopupMenu.addSeparator();
      rankPopupMenu.add(chkUseWinScore);
    } else {
      rankJMenu.add(rankNoneMove);
      rankJMenu.add(rankLastMove);
      if (!Lizzie.config.useJavaLooks) {
        setCustomMoves.addActionListener(
            new ActionListener() {
              @Override
              public void actionPerformed(ActionEvent e) {
                SwingUtilities.invokeLater(
                    new Runnable() {
                      public void run() {
                        String result =
                            JOptionPane.showInputDialog(
                                Lizzie.frame,
                                resourceBundle.getString("Menu.rankMenu.setCustomMoves.message"),
                                Lizzie.config.txtMoveRankMarkLastMove);
                        if (result != null)
                          try {
                            int numbers = Integer.parseInt(result);
                            Lizzie.config.txtMoveRankMarkLastMove = Math.max(1, numbers);
                            Lizzie.config.uiConfig.put(
                                "txt-move-rank-mark-last-move",
                                Lizzie.config.txtMoveRankMarkLastMove);
                            Lizzie.config.moveRankMarkLastMove =
                                Lizzie.config.txtMoveRankMarkLastMove;
                            Lizzie.config.uiConfig.put(
                                "move-rank-mark-last-move", Lizzie.config.moveRankMarkLastMove);
                            setBtnRankMark();
                            Lizzie.frame.refresh();
                          } catch (NumberFormatException ex) {
                            Utils.showMsg(resourceBundle.getString("Menu.inputIntegerHint"));
                            return;
                          }
                      }
                    });
              }
            });
        rankJMenu.add(setCustomMoves);
      } else rankJMenu.add(rankCustomMove);
      rankJMenu.add(rankAllMove);

      rankJMenu.addSeparator();

      showMoveRankInOrigin.addActionListener(
          new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
              // TODO Auto-generated method stub
              Lizzie.config.toggleDisableMoveRankInOrigin();
            }
          });
      rankJMenu.add(showMoveRankInOrigin);
      rankJMenu.add(chkUseWinScore);
    }
    rankCustomMove.setPreferredSize(
        new Dimension(
            (rankPopupMenu != null
                    ? rankPopupMenu.getPreferredSize().width
                    : rankJMenu.getPreferredSize().width - (Lizzie.config.useJavaLooks ? 20 : 40))
                + (Lizzie.config.isFrameFontSmall()
                    ? 30
                    : (Lizzie.config.isFrameFontMiddle() ? 40 : 50)),
            (Lizzie.config.useJavaLooks
                ? (Lizzie.config.isFrameFontSmall()
                    ? 20
                    : (Lizzie.config.isFrameFontMiddle() ? 25 : 30))
                : (Lizzie.config.isFrameFontSmall()
                    ? 25
                    : (Lizzie.config.isFrameFontMiddle() ? 27 : 30)))));
    chkUseWinScore.setPreferredSize(
        new Dimension(
            (int) rankCustomMove.getPreferredSize().getWidth(),
            (int) (rankCustomMove.getPreferredSize().getHeight() * 1.2)));
    rankNoneMove.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            // TBD
            Lizzie.config.moveRankMarkLastMove = -1;
            Lizzie.config.uiConfig.put(
                "move-rank-mark-last-move", Lizzie.config.moveRankMarkLastMove);
            setBtnRankMark();
            Lizzie.frame.refresh();
          }
        });
    rankLastMove.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            Lizzie.config.hiddenMoveNumber();
            Lizzie.config.moveRankMarkLastMove = 1;
            Lizzie.config.uiConfig.put(
                "move-rank-mark-last-move", Lizzie.config.moveRankMarkLastMove);
            setBtnRankMark();
            Lizzie.frame.refresh();
          }
        });
    rankAllMove.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            Lizzie.config.hiddenMoveNumber();
            Lizzie.config.moveRankMarkLastMove = 0;
            Lizzie.config.uiConfig.put(
                "move-rank-mark-last-move", Lizzie.config.moveRankMarkLastMove);
            setBtnRankMark();
            Lizzie.frame.refresh();
          }
        });
    rankCustomMove.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            Lizzie.config.hiddenMoveNumber();
            Lizzie.config.txtMoveRankMarkLastMove =
                Math.max(
                    1, Utils.parseTextToInt(txtCustomMove, Lizzie.config.txtMoveRankMarkLastMove));
            Lizzie.config.uiConfig.put(
                "txt-move-rank-mark-last-move", Lizzie.config.txtMoveRankMarkLastMove);
            Lizzie.config.moveRankMarkLastMove = Lizzie.config.txtMoveRankMarkLastMove;
            Lizzie.config.uiConfig.put(
                "move-rank-mark-last-move", Lizzie.config.moveRankMarkLastMove);
            setBtnRankMark();
            Lizzie.frame.refresh();
          }
        });

    if (rankPopupMenu != null) {
      rankPopupMenu.addPopupMenuListener(
          new PopupMenuListener() {
            public void popupMenuCanceled(PopupMenuEvent e) {}

            public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {}

            public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
              rankAllMove.setState(false);
              rankCustomMove.setState(false);
              rankNoneMove.setState(false);
              rankLastMove.setState(false);
              chkWin.setSelected(Lizzie.config.useWinLossInMoveRank);
              chkScore.setSelected(Lizzie.config.useScoreLossInMoveRank);
              txtCustomMove.setText(String.valueOf(Lizzie.config.txtMoveRankMarkLastMove));
              if (Lizzie.config.allowMoveNumber != 0) {
                rankNoneMove.setState(true);
              } else {
                switch (Lizzie.config.moveRankMarkLastMove) {
                  case 0:
                    rankAllMove.setState(true);
                    break;
                  case 1:
                    rankLastMove.setState(true);
                    break;
                  case -1:
                    rankNoneMove.setState(true);
                    break;
                }
                if (Lizzie.config.moveRankMarkLastMove > 1
                    && Lizzie.config.moveRankMarkLastMove == Lizzie.config.txtMoveRankMarkLastMove)
                  rankCustomMove.setState(true);
              }
            }
          });
    } else {
      rankJMenu.addMenuListener(
          new MenuListener() {
            public void menuSelected(MenuEvent e) {
              if (Lizzie.config.disableMoveRankInOrigin) showMoveRankInOrigin.setSelected(true);
              else showMoveRankInOrigin.setSelected(false);
              rankAllMove.setState(false);
              rankCustomMove.setState(false);
              rankNoneMove.setState(false);
              rankLastMove.setState(false);
              chkWin.setSelected(Lizzie.config.useWinLossInMoveRank);
              chkScore.setSelected(Lizzie.config.useScoreLossInMoveRank);
              txtCustomMove.setText(String.valueOf(Lizzie.config.txtMoveRankMarkLastMove));
              if (Lizzie.config.allowMoveNumber != 0) {
                rankNoneMove.setState(true);
              } else {
                switch (Lizzie.config.moveRankMarkLastMove) {
                  case 0:
                    rankAllMove.setState(true);
                    break;
                  case 1:
                    rankLastMove.setState(true);
                    break;
                  case -1:
                    rankNoneMove.setState(true);
                    break;
                }
                if (Lizzie.config.moveRankMarkLastMove > 1
                    && Lizzie.config.moveRankMarkLastMove
                        == Lizzie.config.txtMoveRankMarkLastMove) {
                  rankCustomMove.setState(true);
                  setCustomMoves.setState(true);
                }
              }
            }

            @Override
            public void menuDeselected(MenuEvent e) {}

            @Override
            public void menuCanceled(MenuEvent e) {}
          });
    }
  }

  private void pauseGame() {
    // TODO Auto-generated method stub
    if (EngineManager.isEngineGame) {
      LizzieFrame.toolbar.btnEnginePkStop.doClick();
      return;
    }
    if (Lizzie.frame.isAnaPlayingAgainstLeelaz || Lizzie.frame.isPlayingAgainstLeelaz) {
      if (Lizzie.frame.isAnaPlayingAgainstLeelaz) {
        Lizzie.leelaz.togglePonder();
        Lizzie.leelaz.isGamePaused = !Lizzie.leelaz.isPondering();
      }
      if (Lizzie.frame.isPlayingAgainstLeelaz) {
        Lizzie.leelaz.isGamePaused = !Lizzie.leelaz.isGamePaused;
      }
      if (Lizzie.leelaz.isGamePaused)
        doubleMenuPauseGame.setText(resourceBundle.getString("Menu.continueGameBtn"));
      else doubleMenuPauseGame.setText(resourceBundle.getString("Menu.pauseGameBtn"));

      Lizzie.frame.refresh();
      return;
    }
  }

  private void setKomiPanelInDoubleMenu() {
    remove(komiPanel);
    lblKomiSpinner.setBounds(
        1,
        Lizzie.config.isFrameFontSmall() ? 1 : (Lizzie.config.isFrameFontMiddle() ? 3 : 6),
        Lizzie.config.isFrameFontSmall() ? 35 : (Lizzie.config.isFrameFontMiddle() ? 40 : 47),
        18);
    txtKomi.setBounds(
        Lizzie.config.isFrameFontSmall() ? 31 : (Lizzie.config.isFrameFontMiddle() ? 39 : 49),
        Lizzie.config.isFrameFontSmall() ? 0 : (Lizzie.config.isFrameFontMiddle() ? 2 : 3),
        Lizzie.config.isFrameFontSmall() ? 35 : (Lizzie.config.isFrameFontMiddle() ? 40 : 47),
        Lizzie.config.isFrameFontSmall()
            ? (Lizzie.config.useJavaLooks ? 20 : 19)
            : (Lizzie.config.isFrameFontMiddle()
                ? (Lizzie.config.useJavaLooks ? 22 : 21)
                : (Lizzie.config.useJavaLooks ? 26 : 25)));
    komiPanel.setPreferredSize(
        new Dimension(
            Lizzie.config.isFrameFontSmall() ? 82 : (Lizzie.config.isFrameFontMiddle() ? 95 : 113),
            Config.menuHeight));

    sepForPdaWrn = new JToolBar.Separator(new Dimension(8, Config.menuHeight));
    Lizzie.frame.topPanel.add(komiPanel);
    Lizzie.frame.topPanel.add(sepForPdaWrn);

    lblCustomPda = new JFontLabel(resourceBundle.getString("Menu.separateLblPda"));
    lblCustomPda.setVisible(false);
    Lizzie.frame.topPanel.add(lblCustomPda);
    Lizzie.frame.topPanel.add(txtPDA);
    customPDAMorePanel = new JPanel();
    customPDAMorePanel.add(more2);
    more2.setBounds(
        0,
        Lizzie.config.isFrameFontSmall()
            ? (Lizzie.config.useJavaLooks ? 1 : 0)
            : (Lizzie.config.isFrameFontMiddle() ? 1 : 2),
        Lizzie.config.isFrameFontSmall() ? 20 : (Lizzie.config.isFrameFontMiddle() ? 23 : 26),
        Lizzie.config.isFrameFontSmall()
            ? (Lizzie.config.useJavaLooks ? 17 : 20)
            : (Lizzie.config.isFrameFontMiddle() ? 23 : 26));
    customPDAMorePanel.setLayout(null);
    customPDAMorePanel.setPreferredSize(new Dimension(more2.getWidth(), Config.menuHeight));
    Lizzie.frame.topPanel.add(customPDAMorePanel);
    txtPDA.setPreferredSize(
        new Dimension(
            Lizzie.config.isFrameFontSmall() ? 43 : (Lizzie.config.isFrameFontMiddle() ? 52 : 64),
            Lizzie.config.isFrameFontSmall() ? 18 : (Lizzie.config.isFrameFontMiddle() ? 21 : 24)));

    Lizzie.frame.topPanel.add(chkPDA);
    chkPDA.setPreferredSize(
        new Dimension((int) chkPDA.getPreferredSize().getWidth(), Config.menuHeight - 3));
    lblGfPDAForDouble = new JFontLabel(resourceBundle.getString("Menu.separateLblPda"));
    Lizzie.frame.topPanel.add(lblGfPDAForDouble);
    Lizzie.frame.topPanel.add(txtGfPDA);
    txtGfPDA.setPreferredSize(
        new Dimension(
            Lizzie.config.isFrameFontSmall() ? 33 : (Lizzie.config.isFrameFontMiddle() ? 36 : 42),
            Lizzie.config.isFrameFontSmall() ? 18 : (Lizzie.config.isFrameFontMiddle() ? 21 : 23)));

    Lizzie.frame.topPanel.add(chkWRN);
    chkWRN.setPreferredSize(
        new Dimension((int) chkWRN.getPreferredSize().getWidth(), Config.menuHeight - 3));
    lblWRNForDouble = new JFontLabel(resourceBundle.getString("Menu.separateLblWrn"));
    Lizzie.frame.topPanel.add(lblWRNForDouble);
    Lizzie.frame.topPanel.add(txtWRN);
    txtWRN.setPreferredSize(
        new Dimension(
            Lizzie.config.isFrameFontSmall() ? 33 : (Lizzie.config.isFrameFontMiddle() ? 38 : 46),
            Lizzie.config.isFrameFontSmall() ? 18 : (Lizzie.config.isFrameFontMiddle() ? 21 : 23)));

    setPdaAndWrnByEngineForDouble();

    if (Lizzie.config.showTimeControlInMenu || Lizzie.config.showPlayoutControlInMenu) {
      Lizzie.frame.topPanel.addSeparator(new Dimension(8, Config.menuHeight + 2));
      Lizzie.frame.topPanel.add(
          new JFontLabel(resourceBundle.getString("Menu.lblLimit"))); // "限制:"));

      if (Lizzie.config.showTimeControlInMenu) {
        Lizzie.frame.topPanel.add(chkTime);
        Lizzie.frame.topPanel.add(txtTimeLimit);
      }
      if (Lizzie.config.showPlayoutControlInMenu) {
        Lizzie.frame.topPanel.add(chkPlayOut);
        Lizzie.frame.topPanel.add(txtPlayOutsLimit);
      }
      Lizzie.frame.topPanel.addSeparator(new Dimension(2, 0));
    }

    if (chkShowBlack == null) {
      chkShowBlack = new JFontCheckBox(resourceBundle.getString("Menu.Black"));
      chkShowBlack.addActionListener(
          new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
              // TBD
              LizzieFrame.toolbar.chkShowBlack.setSelected(chkShowBlack.isSelected());
              Lizzie.frame.refresh();
            }
          });
    }
    if (!chkShowBlack.isPreferredSizeSet())
      chkShowBlack.setPreferredSize(
          new Dimension(
              (int) chkShowBlack.getPreferredSize().getWidth()
                  + (Lizzie.config.shouldWidenCheckBox ? 4 : 0),
              Config.menuHeight - 3));
    if (chkShowWhite == null) {
      chkShowWhite = new JFontCheckBox(resourceBundle.getString("Menu.White"));
      chkShowWhite.addActionListener(
          new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
              // TBD
              LizzieFrame.toolbar.chkShowWhite.setSelected(chkShowWhite.isSelected());
              Lizzie.frame.refresh();
            }
          });
    }
    if (!chkShowWhite.isPreferredSizeSet())
      chkShowWhite.setPreferredSize(
          new Dimension(
              (int) chkShowWhite.getPreferredSize().getWidth()
                  + (Lizzie.config.shouldWidenCheckBox ? Lizzie.config.isChinese ? 4 : 5 : 0),
              Config.menuHeight - 3));
    if (chkAnalyzeBlack == null) {
      chkAnalyzeBlack = new JFontCheckBox(resourceBundle.getString("Menu.Black"));
      chkAnalyzeBlack.addActionListener(
          new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
              // TBD
              Lizzie.config.analyzeBlack = chkAnalyzeBlack.isSelected();
              if (Lizzie.board.getHistory().isBlacksTurn()) {
                if (Lizzie.leelaz.isPondering()) {
                  if (Lizzie.config.analyzeBlack) Lizzie.leelaz.ponder();
                  else Lizzie.leelaz.nameCmdfornoponder();
                }
              }
            }
          });
    }
    if (!chkAnalyzeBlack.isPreferredSizeSet())
      chkAnalyzeBlack.setPreferredSize(
          new Dimension(
              (int) chkAnalyzeBlack.getPreferredSize().getWidth()
                  + (Lizzie.config.shouldWidenCheckBox ? 4 : 0),
              Config.menuHeight - 3));
    if (chkAnalyzeWhite == null) {
      chkAnalyzeWhite = new JFontCheckBox(resourceBundle.getString("Menu.White"));
      chkAnalyzeWhite.addActionListener(
          new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
              // TBD
              Lizzie.config.analyzeWhite = chkAnalyzeWhite.isSelected();
              if (!Lizzie.board.getHistory().isBlacksTurn()) {
                if (Lizzie.leelaz.isPondering()) {
                  if (Lizzie.config.analyzeWhite) Lizzie.leelaz.ponder();
                  else Lizzie.leelaz.nameCmdfornoponder();
                }
              }
            }
          });
    }
    if (!chkAnalyzeWhite.isPreferredSizeSet())
      chkAnalyzeWhite.setPreferredSize(
          new Dimension(
              (int) chkAnalyzeWhite.getPreferredSize().getWidth()
                  + (Lizzie.config.shouldWidenCheckBox ? 4 : 0),
              Config.menuHeight - 3));
    if (chkShowWinrate == null) {
      chkShowWinrate = new JFontCheckBox(resourceBundle.getString("Menu.winrate"));
      chkShowWinrate.addActionListener(
          new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
              // TBD
              Lizzie.config.showWinrateInSuggestion = !Lizzie.config.showWinrateInSuggestion;
              Lizzie.config.uiConfig.put(
                  "show-winrate-in-suggestion", Lizzie.config.showWinrateInSuggestion);
              Lizzie.frame.refresh();
            }
          });
    }
    if (!chkShowWinrate.isPreferredSizeSet())
      chkShowWinrate.setPreferredSize(
          new Dimension(
              (int) chkShowWinrate.getPreferredSize().getWidth()
                  + (Lizzie.config.shouldWidenCheckBox ? 4 : 0),
              Config.menuHeight - 3));
    if (chkShowPlayouts == null) {
      chkShowPlayouts = new JFontCheckBox(resourceBundle.getString("Menu.visits"));
      chkShowPlayouts.addActionListener(
          new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
              // TBD
              Lizzie.config.showPlayoutsInSuggestion = !Lizzie.config.showPlayoutsInSuggestion;
              Lizzie.config.uiConfig.put(
                  "show-playouts-in-suggestion", Lizzie.config.showPlayoutsInSuggestion);
              Lizzie.frame.refresh();
            }
          });
    }
    if (!chkShowPlayouts.isPreferredSizeSet())
      chkShowPlayouts.setPreferredSize(
          new Dimension(
              (int) chkShowPlayouts.getPreferredSize().getWidth()
                  + (Lizzie.config.shouldWidenCheckBox ? 4 : 0),
              Config.menuHeight - 3));
    if (chkShowScore == null) {
      chkShowScore = new JFontCheckBox(resourceBundle.getString("Menu.score"));
      chkShowScore.addActionListener(
          new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
              // TBD
              Lizzie.config.showScoremeanInSuggestion = !Lizzie.config.showScoremeanInSuggestion;
              Lizzie.config.uiConfig.put(
                  "show-scoremean-in-suggestion", Lizzie.config.showScoremeanInSuggestion);
              Lizzie.frame.refresh();
            }
          });
    }
    if (!chkShowScore.isPreferredSizeSet())
      chkShowScore.setPreferredSize(
          new Dimension(
              (int) chkShowScore.getPreferredSize().getWidth()
                  + (Lizzie.config.shouldWidenCheckBox ? 4 : 0),
              Config.menuHeight - 3));
    if (Lizzie.config.showAnalyzeController) {
      JFontLabel lblShowAnalyze = new JFontLabel(resourceBundle.getString("Menu.lblShowAnalyze"));
      chkAnalyzeBlack.setSelected(Lizzie.config.analyzeBlack);
      chkAnalyzeWhite.setSelected(Lizzie.config.analyzeWhite);
      Lizzie.frame.topPanel.addSeparator(new Dimension(8, Config.menuHeight + 2));
      Lizzie.frame.topPanel.add(lblShowAnalyze);
      Lizzie.frame.topPanel.add(chkAnalyzeBlack);
      Lizzie.frame.topPanel.add(chkAnalyzeWhite);
    }

    if (Lizzie.config.showDoubleMenuVar || Lizzie.config.showDoubleMenuMoveInfo) {
      JFontLabel lblShowCandidate =
          new JFontLabel(resourceBundle.getString("Menu.lblShowCandidate"));

      chkShowBlack.setSelected(LizzieFrame.toolbar.chkShowBlack.isSelected());
      chkShowWhite.setSelected(LizzieFrame.toolbar.chkShowWhite.isSelected());

      if (Lizzie.config.showWinrateInSuggestion) chkShowWinrate.setSelected(true);
      if (Lizzie.config.showPlayoutsInSuggestion) chkShowPlayouts.setSelected(true);
      if (Lizzie.config.showScoremeanInSuggestion) chkShowScore.setSelected(true);

      Lizzie.frame.topPanel.addSeparator(new Dimension(8, Config.menuHeight + 2));
      Lizzie.frame.topPanel.add(lblShowCandidate);
      if (Lizzie.config.showDoubleMenuVar) {
        Lizzie.frame.topPanel.add(chkShowBlack);
        Lizzie.frame.topPanel.add(chkShowWhite);
      }
      if (Lizzie.config.showDoubleMenuMoveInfo) {
        //  Lizzie.frame.topPanel.add(lblMoveInfo);
        if (Lizzie.config.showDoubleMenuVar)
          Lizzie.frame.topPanel.addSeparator(new Dimension(8, Config.menuHeight + 2));
        Lizzie.frame.topPanel.add(chkShowWinrate);
        Lizzie.frame.topPanel.add(chkShowPlayouts);
        Lizzie.frame.topPanel.add(chkShowScore);
      }
    }
    setRules.setMargin(new Insets(0, 0, 1, 0));
    setLzSaiParam.setMargin(new Insets(0, 0, 1, 0));
    setBoardSize.setMargin(new Insets(0, 0, 1, 0));
    saveLoad.setMargin(new Insets(0, 0, 1, 0));
    if (Lizzie.config.showRuleMenu
        || Lizzie.config.showParamMenu
        || Lizzie.config.showGobanMenu
        || Lizzie.config.showSaveLoadMenu)
      Lizzie.frame.topPanel.addSeparator(new Dimension(8, Config.menuHeight + 2));
    if (Lizzie.config.showRuleMenu) {
      setRules.setVisible(true);
      Lizzie.frame.topPanel.add(setRules);
    }
    if (Lizzie.config.showParamMenu) {
      setLzSaiParam.setVisible(true);
      Lizzie.frame.topPanel.add(setLzSaiParam);
    }
    if (Lizzie.config.showGobanMenu) {
      setBoardSize.setVisible(true);
      Lizzie.frame.topPanel.add(setBoardSize);
    }
    if (Lizzie.config.showSaveLoadMenu) {
      saveLoad.setVisible(true);
      Lizzie.frame.topPanel.add(saveLoad);
    }
    // Lizzie.frame.reSetLoc();
  }

  public void setPdaAndWrn(double pda, double wrn) {
    ShouldIgnoreDtChange = true;
    if (pda == 0) {
      chkPDA.setSelected(false);
      txtGfPDA.setText("0");
      txtGfPDA.setEnabled(false);
    } else {
      chkPDA.setSelected(true);
      txtGfPDA.setText(String.valueOf(pda));
      txtGfPDA.setEnabled(true);
    }
    if (wrn == 0) {
      chkWRN.setSelected(false);
      txtWRN.setText("0");
      txtWRN.setEnabled(false);
    } else {
      chkWRN.setSelected(true);
      txtWRN.setText(String.valueOf(wrn));
      txtWRN.setEnabled(true);
    }
    ShouldIgnoreDtChange = false;
  }

  public void setPdaAndWrnByEngineForDouble() {
    // TODO Auto-generated method stub
    boolean needRemoveS = true;
    if (showPDA
        || (EngineManager.isEngineGame
            && (Lizzie.engineManager.engineList.get(EngineManager.engineGameInfo.firstEngineIndex)
                    .isKataGoPda
                || Lizzie.engineManager.engineList.get(
                        EngineManager.engineGameInfo.secondEngineIndex)
                    .isKataGoPda))) {
      lblCustomPda.setVisible(true);
      txtPDA.setVisible(true);
      more2.setVisible(true);
      customPDAMorePanel.setVisible(true);
      lblGfPDAForDouble.setVisible(false);
      chkPDA.setVisible(false);
      txtGfPDA.setVisible(false);
      needRemoveS = false;
    } else {
      lblCustomPda.setVisible(false);
      txtPDA.setVisible(false);
      more2.setVisible(false);
      customPDAMorePanel.setVisible(false);
      if (Lizzie.config.showPDAInMenu
          && !isEngineGame()
          && Lizzie.leelaz != null
          && Lizzie.leelaz.isKatago) {
        chkPDA.setVisible(true);
        lblGfPDAForDouble.setVisible(true);
        txtGfPDA.setVisible(true);
        needRemoveS = false;
      } else {
        lblGfPDAForDouble.setVisible(false);
        chkPDA.setVisible(false);
        txtGfPDA.setVisible(false);
      }
    }

    if (Lizzie.config.showWRNInMenu
        && !isEngineGame()
        && Lizzie.leelaz != null
        && Lizzie.leelaz.isKatago) {
      needRemoveS = false;
      lblWRNForDouble.setVisible(true);
      chkWRN.setVisible(true);
      txtWRN.setVisible(true);
    } else {
      lblWRNForDouble.setVisible(false);
      chkWRN.setVisible(false);
      txtWRN.setVisible(false);
    }
    if (needRemoveS) sepForPdaWrn.setVisible(false);
    else sepForPdaWrn.setVisible(true);
  }

  private void setKomiPanelExtra() {
    SwingUtilities.invokeLater(
        new Thread() {
          public void run() {
            if (Lizzie.config.showDoubleMenu && Lizzie.frame != null) {
              doubleMenu(false);
              return;
            }
            if (toolPanel != null) {
              komiPanel.remove(toolPanel);
              toolPanel.removeAll();
            }
            toolPanel = new JToolBar();
            if (Lizzie.config.showDoubleMenuGameControl) {
              toolPanel.add(combinedSeparatorGameControl);
              toolPanel.add(doubleMenuNewGame);
              toolPanel.add(doubleMenuPauseGame);
              toolPanel.add(doubleMenuResign);
            }
            toolPanel.add(combinedSeparatorPlaceStone);
            toolPanel.add(black);
            toolPanel.add(white);
            toolPanel.add(blackwhite);
            toolPanel.add(playPass);
            toolPanel.add(combinedSeparatorForce);
            toolPanel.add(selectAllow);
            toolPanel.add(selectAllowMore);
            toolPanel.add(selectAvoid);
            toolPanel.add(selectAvoidMore);
            toolPanel.add(clearSelect);

            if (Lizzie.config.showTimeControlInMenu || Lizzie.config.showPlayoutControlInMenu) {
              toolPanel.addSeparator(new Dimension(8, Config.menuHeight + 2));
              toolPanel.add(new JFontLabel(resourceBundle.getString("Menu.lblLimit"))); // "限制:"));
              if (Lizzie.config.showTimeControlInMenu) {
                toolPanel.add(chkTime);
                toolPanel.add(txtTimeLimit);
              }
              if (Lizzie.config.showPlayoutControlInMenu) {
                toolPanel.add(chkPlayOut);
                toolPanel.add(txtPlayOutsLimit);
              }
              toolPanel.addSeparator(new Dimension(2, 0));
            }

            if (chkShowBlack == null) {
              chkShowBlack = new JFontCheckBox(resourceBundle.getString("Menu.Black"));
              chkShowBlack.addActionListener(
                  new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                      // TBD
                      LizzieFrame.toolbar.chkShowBlack.setSelected(chkShowBlack.isSelected());
                      Lizzie.frame.refresh();
                    }
                  });
            }
            if (chkShowWhite == null) {
              chkShowWhite = new JFontCheckBox(resourceBundle.getString("Menu.White"));
              chkShowWhite.addActionListener(
                  new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                      // TBD
                      LizzieFrame.toolbar.chkShowWhite.setSelected(chkShowWhite.isSelected());
                      Lizzie.frame.refresh();
                    }
                  });
            }

            if (chkAnalyzeBlack == null) {
              chkAnalyzeBlack = new JFontCheckBox(resourceBundle.getString("Menu.Black"));
              chkAnalyzeBlack.addActionListener(
                  new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                      // TBD
                      Lizzie.config.analyzeBlack = chkAnalyzeBlack.isSelected();
                      if (Lizzie.board.getHistory().isBlacksTurn()) {
                        if (Lizzie.leelaz.isPondering()) {
                          if (Lizzie.config.analyzeBlack) Lizzie.leelaz.ponder();
                          else Lizzie.leelaz.nameCmdfornoponder();
                        }
                      }
                    }
                  });
            }
            if (chkAnalyzeWhite == null) {
              chkAnalyzeWhite = new JFontCheckBox(resourceBundle.getString("Menu.White"));
              chkAnalyzeWhite.addActionListener(
                  new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                      // TBD
                      Lizzie.config.analyzeWhite = chkAnalyzeWhite.isSelected();
                      if (!Lizzie.board.getHistory().isBlacksTurn()) {
                        if (Lizzie.leelaz.isPondering()) {
                          if (Lizzie.config.analyzeWhite) Lizzie.leelaz.ponder();
                          else Lizzie.leelaz.nameCmdfornoponder();
                        }
                      }
                    }
                  });
            }

            if (chkShowWinrate == null) {
              chkShowWinrate = new JFontCheckBox(resourceBundle.getString("Menu.winrate"));
              chkShowWinrate.addActionListener(
                  new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                      // TBD
                      Lizzie.config.showWinrateInSuggestion =
                          !Lizzie.config.showWinrateInSuggestion;
                      Lizzie.config.uiConfig.put(
                          "show-winrate-in-suggestion", Lizzie.config.showWinrateInSuggestion);
                      Lizzie.frame.refresh();
                    }
                  });
            }

            if (chkShowPlayouts == null) {
              chkShowPlayouts = new JFontCheckBox(resourceBundle.getString("Menu.visits"));
              chkShowPlayouts.addActionListener(
                  new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                      // TBD
                      Lizzie.config.showPlayoutsInSuggestion =
                          !Lizzie.config.showPlayoutsInSuggestion;
                      Lizzie.config.uiConfig.put(
                          "show-playouts-in-suggestion", Lizzie.config.showPlayoutsInSuggestion);
                      Lizzie.frame.refresh();
                    }
                  });
            }

            if (chkShowScore == null) {
              chkShowScore = new JFontCheckBox(resourceBundle.getString("Menu.score"));
              chkShowScore.addActionListener(
                  new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                      // TBD
                      Lizzie.config.showScoremeanInSuggestion =
                          !Lizzie.config.showScoremeanInSuggestion;
                      Lizzie.config.uiConfig.put(
                          "show-scoremean-in-suggestion", Lizzie.config.showScoremeanInSuggestion);
                      Lizzie.frame.refresh();
                    }
                  });
            }

            if (Lizzie.config.showAnalyzeController) {
              JFontLabel lblShowAnalyze =
                  new JFontLabel(resourceBundle.getString("Menu.lblShowAnalyze"));
              chkAnalyzeBlack.setSelected(Lizzie.config.analyzeBlack);
              chkAnalyzeWhite.setSelected(Lizzie.config.analyzeWhite);
              toolPanel.addSeparator();
              toolPanel.add(lblShowAnalyze);
              toolPanel.add(chkAnalyzeBlack);
              toolPanel.add(chkAnalyzeWhite);
            }

            if (Lizzie.config.showDoubleMenuVar || Lizzie.config.showDoubleMenuMoveInfo) {
              JFontLabel lblShowCandidate =
                  new JFontLabel(resourceBundle.getString("Menu.lblShowCandidate"));

              chkShowBlack.setSelected(LizzieFrame.toolbar.chkShowBlack.isSelected());
              chkShowWhite.setSelected(LizzieFrame.toolbar.chkShowWhite.isSelected());

              if (Lizzie.config.showWinrateInSuggestion) chkShowWinrate.setSelected(true);
              if (Lizzie.config.showPlayoutsInSuggestion) chkShowPlayouts.setSelected(true);
              if (Lizzie.config.showScoremeanInSuggestion) chkShowScore.setSelected(true);

              toolPanel.addSeparator();
              toolPanel.add(lblShowCandidate);
              if (Lizzie.config.showDoubleMenuVar) {
                toolPanel.add(chkShowBlack);
                toolPanel.add(chkShowWhite);
              }
              if (Lizzie.config.showDoubleMenuMoveInfo) {
                //  Lizzie.frame.topPanel.add(lblMoveInfo);
                if (Lizzie.config.showDoubleMenuVar) toolPanel.addSeparator();
                toolPanel.add(chkShowWinrate);
                toolPanel.add(chkShowPlayouts);
                toolPanel.add(chkShowScore);
              }
            }
            JPanel btnPanel = new JPanel(null);
            int pos = 0;
            if (Lizzie.config.showRuleMenu
                || Lizzie.config.showParamMenu
                || Lizzie.config.showGobanMenu
                || Lizzie.config.showSaveLoadMenu) toolPanel.addSeparator();
            toolPanel.add(setRules);
            toolPanel.add(setLzSaiParam);
            toolPanel.add(setBoardSize);
            toolPanel.add(saveLoad);
            if (Lizzie.config.showRuleMenu) {
              setRules.setBounds(
                  pos,
                  Lizzie.config.isFrameFontSmall()
                      ? 0
                      : (Lizzie.config.isFrameFontMiddle() ? 0 : 2),
                  Lizzie.config.isFrameFontSmall()
                      ? 40
                      : (Lizzie.config.isFrameFontMiddle()
                          ? 46
                          : (Lizzie.config.isChinese ? 52 : 55)),
                  Lizzie.config.isFrameFontSmall()
                      ? 20
                      : (Lizzie.config.isFrameFontMiddle() ? 23 : 26));
              setRules.setVisible(true);
              btnPanel.add(setRules);
              pos +=
                  Lizzie.config.isFrameFontSmall()
                      ? 40
                      : (Lizzie.config.isFrameFontMiddle()
                          ? 46
                          : (Lizzie.config.isChinese ? 52 : 55));
            } else setRules.setVisible(false);
            if (Lizzie.config.showParamMenu) {
              if (Lizzie.config.isChinese) {
                setLzSaiParam.setBounds(
                    pos,
                    Lizzie.config.isFrameFontSmall()
                        ? 0
                        : (Lizzie.config.isFrameFontMiddle() ? 0 : 2),
                    Lizzie.config.isFrameFontSmall()
                        ? 40
                        : (Lizzie.config.isFrameFontMiddle() ? 46 : 52),
                    Lizzie.config.isFrameFontSmall()
                        ? 20
                        : (Lizzie.config.isFrameFontMiddle() ? 23 : 26));
                pos +=
                    Lizzie.config.isFrameFontSmall()
                        ? 40
                        : (Lizzie.config.isFrameFontMiddle() ? 46 : 52);
              } else {
                setLzSaiParam.setBounds(
                    pos,
                    Lizzie.config.isFrameFontSmall()
                        ? 0
                        : (Lizzie.config.isFrameFontMiddle() ? 0 : 2),
                    Lizzie.config.isFrameFontSmall()
                        ? 48
                        : (Lizzie.config.isFrameFontMiddle() ? 62 : 75),
                    Lizzie.config.isFrameFontSmall()
                        ? 20
                        : (Lizzie.config.isFrameFontMiddle() ? 23 : 26));
                pos +=
                    Lizzie.config.isFrameFontSmall()
                        ? 48
                        : (Lizzie.config.isFrameFontMiddle() ? 62 : 75);
              }
              setLzSaiParam.setVisible(true);
              btnPanel.add(setLzSaiParam);

            } else setLzSaiParam.setVisible(false);
            if (Lizzie.config.showGobanMenu) {
              if (Lizzie.config.isChinese) {
                setBoardSize.setBounds(
                    pos,
                    Lizzie.config.isFrameFontSmall()
                        ? 0
                        : (Lizzie.config.isFrameFontMiddle() ? 0 : 2),
                    Lizzie.config.isFrameFontSmall()
                        ? 40
                        : (Lizzie.config.isFrameFontMiddle() ? 46 : 52),
                    Lizzie.config.isFrameFontSmall()
                        ? 20
                        : (Lizzie.config.isFrameFontMiddle() ? 23 : 26));
                pos +=
                    Lizzie.config.isFrameFontSmall()
                        ? 40
                        : (Lizzie.config.isFrameFontMiddle() ? 46 : 52);
              } else {
                setBoardSize.setBounds(
                    pos,
                    Lizzie.config.isFrameFontSmall()
                        ? 0
                        : (Lizzie.config.isFrameFontMiddle() ? 0 : 2),
                    Lizzie.config.isFrameFontSmall()
                        ? 45
                        : (Lizzie.config.isFrameFontMiddle() ? 55 : 65),
                    Lizzie.config.isFrameFontSmall()
                        ? 20
                        : (Lizzie.config.isFrameFontMiddle() ? 23 : 26));
                pos +=
                    Lizzie.config.isFrameFontSmall()
                        ? 45
                        : (Lizzie.config.isFrameFontMiddle() ? 55 : 65);
              }
              setBoardSize.setVisible(true);
              btnPanel.add(setBoardSize);
            } else setBoardSize.setVisible(false);
            if (Lizzie.config.showSaveLoadMenu) {
              saveLoad.setBounds(
                  pos,
                  Lizzie.config.isFrameFontSmall()
                      ? 0
                      : (Lizzie.config.isFrameFontMiddle() ? 0 : 2),
                  Lizzie.config.isFrameFontSmall()
                      ? 40
                      : (Lizzie.config.isFrameFontMiddle()
                          ? 46
                          : (Lizzie.config.isChinese ? 52 : 55)),
                  Lizzie.config.isFrameFontSmall()
                      ? 20
                      : (Lizzie.config.isFrameFontMiddle() ? 23 : 26));
              saveLoad.setVisible(true);
              btnPanel.add(saveLoad);
              pos +=
                  Lizzie.config.isFrameFontSmall()
                      ? 40
                      : (Lizzie.config.isFrameFontMiddle()
                          ? 46
                          : (Lizzie.config.isChinese ? 52 : 55));
            } else saveLoad.setVisible(false);
            toolPanel.add(btnPanel);
            komiPanel.add(toolPanel);
            toolPanel.setBounds(
                startPos,
                Lizzie.config.isFrameFontSmall()
                    ? -2
                    : (Lizzie.config.isFrameFontMiddle() ? -1 : -1),
                1500,
                Lizzie.config.isFrameFontSmall()
                    ? 22
                    : (Lizzie.config.isFrameFontMiddle() ? 26 : 32));
            toolPanel.setFloatable(false);
            komiPanel.updateUI();
            lblKomiSpinner.setBounds(
                1,
                Lizzie.config.isFrameFontSmall() ? 0 : (Lizzie.config.isFrameFontMiddle() ? 3 : 6),
                Lizzie.config.isFrameFontSmall()
                    ? 35
                    : (Lizzie.config.isFrameFontMiddle() ? 40 : 47),
                18);
            txtKomi.setBounds(
                Lizzie.config.isFrameFontSmall()
                    ? 31
                    : (Lizzie.config.isFrameFontMiddle() ? 39 : 49),
                Lizzie.config.isFrameFontSmall() ? 0 : (Lizzie.config.isFrameFontMiddle() ? 2 : 3),
                Lizzie.config.isFrameFontSmall()
                    ? 35
                    : (Lizzie.config.isFrameFontMiddle() ? 40 : 47),
                Lizzie.config.isFrameFontSmall()
                    ? (Lizzie.config.useJavaLooks ? 19 : 18)
                    : (Lizzie.config.isFrameFontMiddle()
                        ? (Lizzie.config.useJavaLooks ? 22 : 21)
                        : (Lizzie.config.useJavaLooks ? 26 : 25)));
            if (komiContentPanel != null) remove(komiContentPanel);
            if (Lizzie.config.isFrameFontSmall()) {
              komiContentPanel = new JPanel();
              if (!Lizzie.config.useJavaLooks) {
                komiContentPanel.setBackground(new Color(232, 232, 232));
                komiPanel.setBackground(new Color(232, 232, 232));
              }
              komiContentPanel.setLayout(null);
              komiContentPanel.add(komiPanel);
              komiPanel.setBounds(0, 1, 9999, 20);
              add(komiContentPanel);
            } else {
              add(komiPanel);
            }
          }
        });
  }

  public void toggleDoubleMenuGameStatus() {
    // if (!Lizzie.config.showDoubleMenu) return;
    SwingUtilities.invokeLater(
        new Thread() {
          public void run() {
            if (!Lizzie.frame.isAnaPlayingAgainstLeelaz
                && !Lizzie.frame.isPlayingAgainstLeelaz
                && !EngineManager.isEngineGame) doubleMenuPauseGame.setEnabled(false);
            else doubleMenuPauseGame.setEnabled(true);
            if ((Lizzie.frame.isAnaPlayingAgainstLeelaz
                    || Lizzie.frame.isPlayingAgainstLeelaz
                    || EngineManager.isEngineGame)
                && Lizzie.leelaz.isLoaded()) {
              if (Lizzie.config.showDoubleMenu) {
                doubleMenuStopGame.setVisible(true);
                doubleMenuNewGame.setVisible(false);
              } else doubleMenuNewGame.setText(resourceBundle.getString("Menu.endGameBtn"));
            } else {
              if (Lizzie.config.showDoubleMenu) {
                doubleMenuStopGame.setVisible(false);
                doubleMenuNewGame.setVisible(true);
              } else doubleMenuNewGame.setText(resourceBundle.getString("Menu.newGameBtn"));
            }
            if (Lizzie.frame.isAnaPlayingAgainstLeelaz || Lizzie.frame.isPlayingAgainstLeelaz)
              doubleMenuResign.setVisible(true);
            else doubleMenuResign.setVisible(false);
            if (EngineManager.isEngineGame) {
              if (LizzieFrame.toolbar.isPkStop)
                doubleMenuPauseGame.setText(resourceBundle.getString("Menu.continueGameBtn"));
              else doubleMenuPauseGame.setText(resourceBundle.getString("Menu.pauseGameBtn"));
              if (LizzieFrame.toolbar.isPkStop) engineMenu.setIcon(ready2);
              else engineMenu.setIcon(icon2);
            }
          }
        });
  }

  public void refreshDoubleMoveInfoStatus() {
    if (!Lizzie.config.showDoubleMenu) return;
    if (Lizzie.config.showWinrateInSuggestion) chkShowWinrate.setSelected(true);
    else chkShowWinrate.setSelected(false);
    if (Lizzie.config.showPlayoutsInSuggestion) chkShowPlayouts.setSelected(true);
    else chkShowPlayouts.setSelected(false);
    if (Lizzie.config.showScoremeanInSuggestion) chkShowScore.setSelected(true);
    else chkShowScore.setSelected(false);
  }

  public void refreshLimitStatus(boolean needReCalculatePondering) {
    chkPlayOut.setSelected(Lizzie.config.limitPlayout);
    txtPlayOutsLimit.setEnabled(Lizzie.config.limitPlayout);
    txtPlayOutsLimit.setText(String.valueOf(Lizzie.config.limitPlayouts));
    chkTime.setSelected(Lizzie.config.limitTime);
    txtTimeLimit.setEnabled(Lizzie.config.limitTime);
    txtTimeLimit.setText(String.valueOf(Lizzie.config.maxAnalyzeTimeMillis / 1000));
    if (needReCalculatePondering) reCalculateLeelazPonderingIfOutOfLimit();
  }

  private void reCalculateLeelazPonderingIfOutOfLimit() {
    if (Lizzie.leelaz.isStopPonderingByLimit()) {
      boolean shouldStartPondering = true;
      if (Lizzie.config.limitPlayout
          && Lizzie.leelaz.getBestMovesPlayouts() > Lizzie.config.limitPlayouts) {
        shouldStartPondering = false;
      }
      if (Lizzie.config.limitTime
          && (System.currentTimeMillis() - Lizzie.leelaz.getStartPonderTime())
              > Lizzie.config.maxAnalyzeTimeMillis) {
        shouldStartPondering = false;
      }
      if (shouldStartPondering) Lizzie.leelaz.ponder();
    }
  }

  public void clearAllowAvoidButtonState() {
    if (selectAvoid != null) selectAvoid.setIcon(iconAvoid);
    if (selectAllow != null) selectAllow.setIcon(iconAllow);
  }

  public void updateEngineMenu() {

    this.remove(engineMenu);
    this.remove(engineMenu2);
    engineMenu = new JFontMenu(resourceBundle.getString("Menu.noEngine"));
    engineMenu.setText(resourceBundle.getString("Menu.noEngine"));
    engineMenu.setForeground(Color.BLACK);
    engineMenu.setFont(
        new Font(Config.sysDefaultFontName, Font.BOLD, Math.max(Config.frameFontSize, 15)));
    this.add(engineMenu);
    //   if (Lizzie.config.Lizzie.config.isDoubleEngineMode()) {
    engineMenu2 = new JFontMenu(resourceBundle.getString("Menu.noEngine"));
    engineMenu2.setText(resourceBundle.getString("Menu.noEngine"));
    engineMenu2.setForeground(Color.BLACK);
    engineMenu2.setFont(
        new Font(Config.sysDefaultFontName, Font.BOLD, Math.max(Config.frameFontSize, 15)));
    this.add(engineMenu2);
    //    }
    if (!Lizzie.config.showDoubleMenu) {
      if (Lizzie.config.isFrameFontSmall() && komiContentPanel != null) {
        this.remove(komiContentPanel);
        this.add(komiContentPanel);
      } else {
        this.remove(komiPanel);
        this.add(komiPanel);
      }
    }
    //  updateMenuAfterEngine(false);

    for (int i = 0; i < engine.length; i++) {
      try {
        engineMenu.remove(engine[i]);
      } catch (Exception e) {
      }
      engine[i] = new JFontMenuItem();
      engineMenu.add(engine[i]);
      engine[i].setText("[" + (i + 1) + "]");
      engine[i].setVisible(false);
      // if (Lizzie.config.Lizzie.config.isDoubleEngineMode()) {
      try {
        engineMenu2.remove(engine2[i]);
      } catch (Exception e) {
      }
      engine2[i] = new JFontMenuItem();
      engineMenu2.add(engine2[i]);
      engine2[i].setText("[" + (i + 1) + "]");
      engine2[i].setVisible(false);
    }
    // }
    for (int i = 0; i < Lizzie.engineManager.engineList.size(); i++) {
      if (i <= 20
          && Lizzie.engineManager.engineList.get(i).isLoaded()
          && Lizzie.engineManager.engineList.get(i).process != null
          && Lizzie.engineManager.engineList.get(i).process.isAlive()) {
        engine[i].setIcon(ready);
        //  if (Lizzie.config.Lizzie.config.isDoubleEngineMode())
        engine2[i].setIcon(ready);
      }
      if (i == EngineManager.currentEngineNo && i <= 20) {
        engine[i].setIcon(icon);
        engineMenu.setText(
            "[" + (i + 1) + "] " + Lizzie.engineManager.engineList.get(i).currentEnginename);
      }
      // if (Lizzie.config.Lizzie.config.isDoubleEngineMode())
      if (i == EngineManager.currentEngineNo2 && i <= 20) {
        engine2[i].setIcon(icon);
        engineMenu2.setText(
            "[" + (i + 1) + "] " + Lizzie.engineManager.engineList.get(i).currentEnginename);
      }
    }

    ArrayList<EngineData> engineData = Utils.getEngineData();
    for (int i = 0; i < engineData.size(); i++) {
      EngineData engineDt = engineData.get(i);
      if (i > (engine.length - 2)) {
        engine[i].setText(resourceBundle.getString("Menu.moreEngines")); // ("更多引擎...");
        engine[i].setVisible(true);
        engine[i].addActionListener(
            new ActionListener() {
              public void actionPerformed(ActionEvent e) {
                JDialog chooseMoreEngine;
                chooseMoreEngine = ChooseMoreEngine.createDialog(1);
                chooseMoreEngine.setVisible(true);
              }
            });
        engineMenu.addSeparator();
        engineMenu.add(restartCurrentEngine);
        engineMenu.add(shutdownEngine);
        shutdownEngine.add(shutdownCurrentEngine);
        shutdownEngine.add(shutdownOtherEngine);
        shutdownEngine.add(shutdownAllEngine);

        if (i > (engine.length - 2)) {
          engine2[i].setText(resourceBundle.getString("Menu.moreEngines")); // ("更多引擎...");
          engine2[i].setVisible(true);
          engine2[i].addActionListener(
              new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                  JDialog chooseMoreEngine;
                  chooseMoreEngine = ChooseMoreEngine.createDialog(2);
                  chooseMoreEngine.setVisible(true);
                }
              });
          engineMenu2.addSeparator();
          engineMenu2.add(restartCurrentEngine2);
          engineMenu2.add(shutdownCurrentEngine2);
        }
        //   }
        if (!Lizzie.config.isDoubleEngineMode()) engineMenu2.setVisible(false);
        return;
      } else {
        engine[i].setText("[" + (i + 1) + "] " + engineDt.name);
        engine[i].setToolTipText(engineDt.commands);
        engine[i].setVisible(true);
        int a = i;
        engine[i].addActionListener(
            new ActionListener() {
              public void actionPerformed(ActionEvent e) {
                Lizzie.engineManager.switchEngine(a, true);
              }
            });
        engine2[i].setText("[" + (i + 1) + "] " + engineDt.name);
        engine2[i].setToolTipText(engineDt.commands);
        engine2[i].setVisible(true);

        engine2[i].addActionListener(
            new ActionListener() {
              public void actionPerformed(ActionEvent e) {
                Lizzie.engineManager.switchEngine(a, false);
              }
            });
        //  }
      }
    }
    engineMenu.addSeparator();
    engineMenu.add(restartCurrentEngine);
    engineMenu.add(shutdownEngine);
    shutdownEngine.add(shutdownCurrentEngine);
    shutdownEngine.add(shutdownOtherEngine);
    shutdownEngine.add(shutdownAllEngine);
    engineMenu2.addSeparator();
    engineMenu2.add(restartCurrentEngine2);
    engineMenu2.add(shutdownCurrentEngine2);
    if (!Lizzie.config.isDoubleEngineMode()) engineMenu2.setVisible(false);
  }

  public void changeEngineIcon(int index, int mode) {
    if (index < 0) return;
    if (index > 20) index = 20;

    if (mode == 0) engine[index].setIcon(null);
    if (mode == 1) engine[index].setIcon(stop);
    if (mode == 2) engine[index].setIcon(ready);
    if (mode == 3) engine[index].setIcon(icon);
  }

  public void changeEngineIcon2(int index, int mode) {
    SwingUtilities.invokeLater(
        new Runnable() {
          public void run() {
            int locIndex = index;
            if (locIndex < 0) return;
            if (locIndex > 20) locIndex = 20;
            if (mode == 0) engine2[locIndex].setIcon(null);
            if (mode == 1) engine2[locIndex].setIcon(stop);
            if (mode == 2) engine2[locIndex].setIcon(ready);
            if (mode == 3) engine2[locIndex].setIcon(icon);
          }
        });
  }

  public void changeicon(int index) {
    JFontMenuItem[] engine =
        index == 1 ? featurecat.lizzie.gui.Menu.engine : featurecat.lizzie.gui.Menu.engine2;
    for (int i = 0; i < 21; i++) {
      if (i < Lizzie.engineManager.engineList.size()
          && !Lizzie.engineManager.engineList.get(i).isStarted()) engine[i].setIcon(null);
      else if (engine[i].getIcon() != null
          && engine[i].getIcon() != featurecat.lizzie.gui.Menu.stop) {
        engine[i].setIcon(featurecat.lizzie.gui.Menu.ready);
      }
    }
    if (EngineManager.currentEngineNo <= 20) {
      if (engine[index == 1 ? EngineManager.currentEngineNo : EngineManager.currentEngineNo2]
              .getIcon()
          == null) {
      } else {
        engine[index == 1 ? EngineManager.currentEngineNo : EngineManager.currentEngineNo2].setIcon(
            featurecat.lizzie.gui.Menu.icon);
      }
    }
  }

  public void toggleShowRule(boolean show) {
    this.setRules.setVisible(show);
  }

  public void toggleShowLzSaiParam(boolean show) {
    this.setLzSaiParam.setVisible(show);
  }

  public void toggleShowSetBoardSize(boolean show) {
    this.setBoardSize.setVisible(show);
  }

  public void toggleShowDoubleMenuButton(boolean show) {
    this.btnDoubleMenu.setVisible(show);
  }

  public void toggleShowEditbar(boolean show) {
    this.black.setVisible(show);
    this.white.setVisible(show);
    this.blackwhite.setVisible(show);
    this.playPass.setVisible(show);
    this.combinedSeparatorPlaceStone.setVisible(show);
    if (Lizzie.frame != null) this.doubleMenu(false);
  }

  public void toggleShowForce(boolean show) {
    if (!Lizzie.config.showForceMenu) {
      this.selectAllow.setVisible(false);
      this.selectAvoid.setVisible(false);
      selectAllowMore.setVisible(false);
      selectAvoidMore.setVisible(false);
      this.clearSelect.setVisible(false);
      if (forceSep != null) forceSep.setVisible(false);
    } else {
      this.clearSelect.setVisible(true);
      selectAllowMore.setVisible(true);
      selectAvoidMore.setVisible(true);
      this.selectAllow.setVisible(true);
      this.selectAvoid.setVisible(show);
    }
    this.combinedSeparatorForce.setVisible(show);
    if (Lizzie.frame != null) this.doubleMenu(false);
  }

  public void updateFastLinks() {
    if (!Lizzie.config.showQuickLinks) {
      quickLinks.setVisible(false);
      return;
    } else {
      quickLinks.setVisible(true);
    }
    ArrayList<ProgramData> programData = getProgramData();
    quickLinks.removeAll();
    for (int i = 0; i < programData.size(); i++) {
      JFontMenuItem program = new JFontMenuItem(programData.get(i).name);
      int index = i;
      program.addActionListener(
          new ActionListener() {
            public void actionPerformed(ActionEvent e) {
              FastLink pro = new FastLink();
              pro.startProgram(programData.get(index).commands);
            }
          });
      quickLinks.add(program);
    }
    JFontMenuItem editProgram =
        new JFontMenuItem(resourceBundle.getString("Menu.editProgram")); // "设置");
    editProgram.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            LizzieFrame.openProgramDialog();
          }
        });

    quickLinks.add(editProgram);
  }

  public ArrayList<ProgramData> getProgramData() {
    ArrayList<ProgramData> ProgramData = new ArrayList<ProgramData>();
    Optional<JSONArray> enginesCommandOpt =
        Optional.ofNullable(Lizzie.config.leelazConfig.optJSONArray("program-command-list"));
    Optional<JSONArray> enginesNameOpt =
        Optional.ofNullable(Lizzie.config.leelazConfig.optJSONArray("program-name-list"));

    for (int i = 0;
        i < (enginesCommandOpt.isPresent() ? enginesCommandOpt.get().length() : 0);
        i++) {
      String commands = enginesCommandOpt.get().getString(i);
      if (!commands.equals("")) {
        String name = enginesNameOpt.isPresent() ? enginesNameOpt.get().optString(i, "") : "";
        ProgramData programDt = new ProgramData();
        programDt.commands = commands;
        programDt.name = name;
        programDt.index = i;
        ProgramData.add(programDt);
      }
    }
    return ProgramData;
  }

  public void showPda(boolean show) {
    SwingUtilities.invokeLater(
        new Runnable() {
          public void run() {
            showPDA = show;
            lblPDASpinner.setVisible(show);
            more2.setVisible(show);
            txtPDA.setVisible(show);
          }
        });
  }

  private void doAfterChangeToolarPos() {
    Lizzie.config.flashAnalyze =
        Lizzie.config.uiConfig.optBoolean(
            "flash-analyze", Lizzie.config.showDoubleMenu ? false : true);
    LizzieFrame.toolbar.reSetButtonLocation();
    Lizzie.config.uiConfig.put("show-double-menu", Lizzie.config.showDoubleMenu);
    if (Lizzie.config.showDoubleMenu) btnDoubleMenu.setIcon(iconUp);
    else btnDoubleMenu.setIcon(iconDown);
    updateMenuAfterEngine(false);
    if (!Lizzie.config.showDoubleMenu) updateMenuStatus();
  }

  public void toggleEngineMenuStatus(boolean isPondering, boolean isThinking) {
    if (engineMenu == null || EngineManager.isEngineGame) return;
    if (isThinking) {
      engineMenu.setIcon(icon2);
      if (Lizzie.config.isDoubleEngineMode()) engineMenu2.setIcon(icon2);
    } else {
      if (isPondering) {
        engineMenu.setIcon(icon2);
        if (Lizzie.config.isDoubleEngineMode()) engineMenu2.setIcon(icon2);
        if (Lizzie.frame.floatBoard != null && Lizzie.frame.floatBoard.isVisible()) {
          Lizzie.frame.floatBoard.setPonderState(true);
        }
        LizzieFrame.toolbar.analyse.setText(
            Lizzie.resourceBundle.getString("BottomToolbar.pauseAnalyse"));
      } else {
        engineMenu.setIcon(ready2);
        if (Lizzie.config.isDoubleEngineMode()) engineMenu2.setIcon(ready2);
        if (Lizzie.frame.floatBoard != null && Lizzie.frame.floatBoard.isVisible()) {
          Lizzie.frame.floatBoard.setPonderState(false);
        }
        LizzieFrame.toolbar.analyse.setText(
            Lizzie.resourceBundle.getString("BottomToolbar.analyse"));
      }
    }
    if (Lizzie.config.isDoubleEngineMode() && Lizzie.leelaz2 == null) engineMenu2.setIcon(ready2);
  }

  @Override
  protected void paintComponent(Graphics g) {
    super.paintComponent(g);
    if (!Lizzie.config.useJavaLooks
        && OS.isWindows()
        && (Boolean) Toolkit.getDefaultToolkit().getDesktopProperty("win.xpstyle.themeActive")) {
      Graphics2D g2d = (Graphics2D) g;
      g2d.setColor(new Color(232, 232, 232));
      g2d.fillRect(0, 0, getWidth(), getHeight());
    }
  }

  public void setWrnText(double wrn) {
    // TODO Auto-generated method stub
    ShouldIgnoreDtChange = true;
    txtWRN.setText(String.valueOf(wrn));
    ShouldIgnoreDtChange = false;
  }

  public void setUseWrn(boolean use, String wrn) {
    // TODO Auto-generated method stub
    ShouldIgnoreDtChange = true;
    txtWRN.setText(String.valueOf(wrn));
    ShouldIgnoreDtChange = false;
    chkWRN.setSelected(use);
    txtWRN.setEnabled(use);
  }

  public void setUseGfPda(boolean use, String pda) {
    // TODO Auto-generated method stub
    ShouldIgnoreDtChange = true;
    txtGfPDA.setText(String.valueOf(pda));
    ShouldIgnoreDtChange = false;
    chkPDA.setSelected(use);
    txtGfPDA.setEnabled(use);
  }

  private boolean isEngineGame() {
    return Lizzie.engineManager != null && Lizzie.engineManager.isEngineGame();
  }

  public void setBtnRankMark() {
    if (btnRankMark != null)
      if (Lizzie.config.allowMoveNumber == 0 && !EngineManager.isEngineGame)
        btnRankMark.setIcon(Lizzie.config.moveRankMarkLastMove < 0 ? rankMarkOff : rankMarkOn);
      else btnRankMark.setIcon(rankMarkOff);
  }
}
