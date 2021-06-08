package featurecat.lizzie.gui;

import static java.awt.event.KeyEvent.*;

import featurecat.lizzie.Lizzie;
import java.awt.event.*;

public class InputIndependentSubboard implements KeyListener, MouseListener, MouseWheelListener {

  @Override
  public void mouseClicked(MouseEvent e) {}

  @Override
  public void mousePressed(MouseEvent e) {
    Lizzie.frame.processIndependentPressOnSub(e);
  }

  @Override
  public void mouseWheelMoved(MouseWheelEvent e) {

    Lizzie.frame.processIndependentSubboardMouseWheelMoved(e);
  }

  @Override
  public void mouseReleased(MouseEvent e) {
    // TODO Auto-generated method stub

  }

  @Override
  public void mouseEntered(MouseEvent e) {
    // TODO Auto-generated method stub
    Lizzie.frame.processIndependentSubboardMouseEntered();
  }

  @Override
  public void mouseExited(MouseEvent e) {
    // TODO Auto-generated method stub
    Lizzie.frame.processIndependentSubboardMouseExited();
  }

  @Override
  public void keyPressed(KeyEvent e) {
    // TODO Auto-generated method stub
    switch (e.getKeyCode()) {
      case VK_SPACE:
        Lizzie.frame.togglePonderMannul();
        break;
      case VK_Q:
        if (e.isAltDown()) {
          Lizzie.frame.toggleIndependentMainBoard();
        }
        break;
      case VK_W:
        if (e.isAltDown()) {
          Lizzie.frame.toggleIndependentSubBoard();
        }
        break;
    }
  }

  @Override
  public void keyReleased(KeyEvent arg0) {
    // TODO Auto-generated method stub

  }

  @Override
  public void keyTyped(KeyEvent arg0) {
    // TODO Auto-generated method stub

  }
}
