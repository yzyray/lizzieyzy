package featurecat.lizzie.gui;

import featurecat.lizzie.Lizzie;
import featurecat.lizzie.analysis.GameInfo;
import featurecat.lizzie.gui.RightClickMenu;
import featurecat.lizzie.rules.MoveLinkedList;
import featurecat.lizzie.rules.Movelist;
import featurecat.lizzie.rules.Movelistwr;
//import featurecat.lizzie.rules.Movelistwr;
import featurecat.lizzie.rules.SGFParser;
import featurecat.lizzie.rules.Stone;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JPopupMenu;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

public class RightClickMenu2 extends JPopupMenu {
//	public static int mousex;
//	public static int mousey;
	public static int[] coords;
	// private JFontMenuItem insertmode;
	private JFontMenuItem switchone;
	private JFontMenuItem deleteone;
	private JFontMenuItem 	previousMove;
	private JFontMenuItem 	moveStone;
	private JFontMenuItem 	findStone;
	 private final ResourceBundle resourceBundle = Lizzie.config.useLanguage==0? ResourceBundle.getBundle("l10n.DisplayStrings"):(Lizzie.config.useLanguage==1? ResourceBundle.getBundle("l10n.DisplayStrings", new Locale("zh", "CN")): ResourceBundle.getBundle("l10n.DisplayStrings", new Locale("en", "US")));
private boolean isFromIndependent;
	public RightClickMenu2() {

		PopupMenuListener listener = new PopupMenuListener() {
			public void popupMenuCanceled(PopupMenuEvent e) {
			}

			public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
//				if (Lizzie.leelaz.isPondering() && featurecat.lizzie.gui.RightClickMenu.isforcing) {
//					if (featurecat.lizzie.gui.RightClickMenu.isforcing) {
//						Lizzie.leelaz.analyzeAvoid("allow", Lizzie.board.getcurrentturn(),
//								featurecat.lizzie.gui.RightClickMenu.allowcoords, 1);
//					} else {
//						Lizzie.leelaz.analyzeAvoid("avoid", Lizzie.board.getcurrentturn(),
//								featurecat.lizzie.gui.RightClickMenu.avoidcoords, 30);
//					}
//				}
//				if (Lizzie.leelaz.isPondering() && !featurecat.lizzie.gui.RightClickMenu.isforcing) {
//					Lizzie.leelaz.ponder();
//				}
				Timer timer = new Timer();
			      timer.schedule(
			              new TimerTask() {
			                public void run() {
			                	Lizzie.frame.isShowingRightMenu = false;
			                  this.cancel();
			                }
			              },
			              200);
			      if(Lizzie.config.isScaled)
			      {	  Lizzie.frame.repaint();
			      if(Lizzie.frame.independentMainBoard!=null&&Lizzie.frame.independentMainBoard.isVisible())
			    	  Lizzie.frame.independentMainBoard.repaint();
			      }
			}

			public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
				Lizzie.frame.isShowingRightMenu =true;
				if(Lizzie.frame.isPlayingAgainstLeelaz||Lizzie.frame.isAnaPlayingAgainstLeelaz)				
					{previousMove.setText(resourceBundle.getString("RightClickMenu.regretOne"));//("悔棋");			
					switchone.setVisible(false);
					deleteone.setVisible(false);
					moveStone.setVisible(false);
					findStone.setVisible(false);
					}
				else 
					{previousMove.setText(resourceBundle.getString("RightClickMenu.previousMove"));//("回退一手");
					switchone.setVisible(true);
					deleteone.setVisible(true);
					moveStone.setVisible(true);
					findStone.setVisible(true);
					}

			}
		};

		this.addPopupMenuListener(listener);
		  ImageIcon iconBack = new ImageIcon();  
		  ImageIcon iconSearch = new ImageIcon();		
		  try {
			iconBack.setImage(
			           ImageIO.read(AnalysisFrame.class.getResourceAsStream("/assets/left.png")));
			iconSearch.setImage(
			           ImageIO.read(AnalysisFrame.class.getResourceAsStream("/assets/upSearch.png")));
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		findStone = new JFontMenuItem(resourceBundle.getString("RightClickMenu2.findStone"));
		findStone.setIcon(iconSearch);
		switchone = new JFontMenuItem(resourceBundle.getString("RightClickMenu2.switchone"));
		deleteone = new JFontMenuItem(resourceBundle.getString("RightClickMenu2.deleteone"));
		previousMove = new JFontMenuItem(resourceBundle.getString("RightClickMenu.previousMove"));	
		previousMove.setIcon(iconBack);
		moveStone= new JFontMenuItem(resourceBundle.getString("RightClickMenu2.moveStone"));
		this.add(moveStone);
		this.add(switchone);	
		this.add(deleteone);
		this.addSeparator();
		this.add(previousMove);
		this.add(findStone);				
		findStone.addActionListener(
		        new ActionListener() {
			          @Override
			          public void actionPerformed(ActionEvent e) {
			        	  Lizzie.board.findMove(coords);
			        	  Lizzie.frame.refresh();
			          }
			        });
		previousMove.addActionListener(
		        new ActionListener() {
			          @Override
			          public void actionPerformed(ActionEvent e) {
			        	  Lizzie.frame.undoForRightClick();
			          }
			        });
		
		switchone.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				switchone();
			}
		});

		deleteone.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				deleteone();
			}			
		});
		

		moveStone.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(isFromIndependent)
				Lizzie.frame.independentMainBoard.setDragStartInfo(coords, true);
					else
				Lizzie.frame.setDragStartInfo(coords,true);
			}			
		});
	}
	
	

	private void switchone() {
		Lizzie.board.editMove(coords,true,false);			
	}

	private void deleteone() {
		Lizzie.board.editMove(coords,false,true);		
	}
	
	public void setFromIndependent(boolean isFromIndependent)
	{
		this.isFromIndependent=isFromIndependent;
	}
	public void setCoords(int[] coords) {
		// TODO Auto-generated method stub
		this.coords=coords;
	}
//	public void Store(int x, int y) {
//		mousex = x;
//		mousey = y;
//	}
}
