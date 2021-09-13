package featurecat.lizzie.gui;

import featurecat.lizzie.Config;
import featurecat.lizzie.Lizzie;
import featurecat.lizzie.rules.BoardHistoryNode;
import java.awt.*;
import java.util.ArrayList;
import java.util.Optional;

public class VariationTreeBig {

  private int YSPACING = 20;
  private int XSPACING = 20;
  private int DOT_DIAM = 11; // Should be odd number
  private int CENTER_DIAM = 5;
  private int RING_DIAM = 15;
  private int rectBorder = 2;
  private int rect_DIAM = 4;
  private int diam = DOT_DIAM;
  private boolean isLargeScaled = Config.isScaled && Lizzie.javaScaleFactor >= 1.5;

  private ArrayList<Integer> laneUsageList;
  private BoardHistoryNode curMove;
  private Rectangle area;
  private Point clickPoint;
  private int curMoveLane = 0;
  private int maxLane = 0;

  public VariationTreeBig() {
    laneUsageList = new ArrayList<Integer>();
    area = new Rectangle(0, 0, 0, 0);
    clickPoint = new Point(0, 0);
    if (isLargeScaled) {
      YSPACING = 30;
      XSPACING = 30;
      DOT_DIAM = 16; // Should be odd number
      CENTER_DIAM = 8;
      RING_DIAM = 23;
      diam = DOT_DIAM;
      rectBorder = 3;
      rect_DIAM = 6;
    }
  }

  public Optional<BoardHistoryNode> drawTree(
      Graphics2D g,
      int posx,
      int posy,
      int startLane,
      int maxposy,
      int minposx,
      BoardHistoryNode startNode,
      int variationNumber,
      boolean calc) {
    Optional<BoardHistoryNode> node = Optional.empty();
    if (!calc) {
      if (startNode.isCurTrunk()) g.setColor(Color.WHITE);
      // else g.setColor(Lizzie.config.varPanelColor);
      else g.setColor(new Color(103, 103, 103));
    }

    // Finds depth on leftmost variation of this tree
    int depth = startNode.getDepth() + 1;
    int lane = startLane;
    // Figures out how far out too the right (which lane) we have to go not to
    // collide with other
    // variations
    int moveNumber = startNode.getData().moveNumber;
    while (lane < laneUsageList.size() && laneUsageList.get(lane) <= moveNumber + depth) {
      // laneUsageList keeps a list of how far down it is to a variation in the
      // different "lanes"
      laneUsageList.set(lane, moveNumber - 1);
      lane++;
    }
    if (lane >= laneUsageList.size()) {
      laneUsageList.add(0);
    }

    if (variationNumber > 1) laneUsageList.set(lane - 1, moveNumber - 1);
    laneUsageList.set(lane, moveNumber);

    // At this point, lane contains the lane we should use (the main branch is in
    // lane 0)
    if (lane > maxLane) maxLane = lane;
    if (startNode == curMove) curMoveLane = lane;
    BoardHistoryNode cur = startNode;
    int curposx = posx + lane * XSPACING;
    int dotoffset = DOT_DIAM / 2;
    diam = DOT_DIAM;
    // }
    int dotoffsety = diam / 2;
    int diff = (DOT_DIAM - diam) / 2;

    if (calc) {
      if (inNode(curposx + dotoffset, posy + dotoffset)) {
        return Optional.of(startNode);
      }
    } else if (lane > 0) {
      // Draw line back to main branch
      //	  boolean isFromStart=!cur.previous().get().previous().isPresent();
      if (lane - startLane > 0 || variationNumber > 1) {
        // Need a horizontal and an angled line
        drawLine(
            g,
            curposx + dotoffset,
            posy + dotoffsety,
            curposx + dotoffset - XSPACING,
            posy + dotoffsety - YSPACING,
            minposx);
        drawLine(
            g,
            posx + (startLane - variationNumber) * XSPACING + 2 * dotoffset + 1,
            posy - YSPACING + dotoffsety,
            curposx + dotoffset - XSPACING,
            posy + dotoffsety - YSPACING,
            minposx);
      } else {
        // Just an angled line
        drawLine(
            g,
            curposx + dotoffset,
            posy + dotoffsety,
            curposx + 2 * dotoffset - XSPACING,
            posy + 2 * dotoffsety - YSPACING,
            minposx);
      }
    }

    // Draw all the nodes and lines in this lane (not variations)
    Color curcolor = null;
    if (!calc) {
      curcolor = g.getColor();
      if (curposx > minposx && posy > 0) {
        if (startNode.previous().isPresent()) {
          boolean showComm = Lizzie.config.showCommentNodeColor && !cur.getData().comment.isEmpty();
          if (showComm) {
            g.setColor(Lizzie.config.commentNodeColor);
            g.fillOval(
                curposx + (DOT_DIAM + diff - RING_DIAM) / 2,
                posy + (DOT_DIAM + diff - RING_DIAM) / 2,
                RING_DIAM,
                RING_DIAM);
          }
          Color blunderColor = Lizzie.frame.getBlunderNodeColor(cur);
          g.setColor(blunderColor);
          g.fillOval(curposx + diff, posy + diff, diam, diam);
          if (curcolor != Color.WHITE) {
            g.setColor(new Color(0, 0, 0, 39));
            if (showComm)
              g.fillOval(
                  curposx + (DOT_DIAM + diff - RING_DIAM) / 2,
                  posy + (DOT_DIAM + diff - RING_DIAM) / 2,
                  RING_DIAM,
                  RING_DIAM);
            else g.fillOval(curposx + diff, posy + diff, diam, diam);
          }
          if (Lizzie.config.showVarMove) {
            g.setFont(new Font(Lizzie.config.uiFontName, Font.PLAIN, isLargeScaled ? 12 : 9));
            g.setColor(Color.WHITE);
            int moveNum = cur.getData().moveMNNumber;
            if (moveNum < 0) {
              BoardHistoryNode nodeP = cur;
              int num = 0;
              while (moveNum < 0 && nodeP.previous().isPresent()) {
                nodeP = nodeP.previous().get();
                moveNum = nodeP.getData().moveMNNumber;
                num++;
              }
              moveNum = moveNum + num;
            }
            if (moveNum < 10) g.drawString(String.valueOf(moveNum), curposx + 3, posy + diff - 5);
            else
              g.drawString(
                  String.valueOf(moveNum), moveNum >= 100 ? curposx - 3 : curposx, posy + diff - 5);
          }
          if (startNode == Lizzie.board.getHistory().getCurrentHistoryNode()) {
            if (blunderColor != Color.WHITE) g.setColor(reverseColor(blunderColor));
            else g.setColor(Color.RED);
            g.fillOval(
                curposx + (DOT_DIAM + diff - CENTER_DIAM) / 2 - 1,
                posy + (DOT_DIAM + diff - CENTER_DIAM) / 2 - 1,
                CENTER_DIAM + 2,
                CENTER_DIAM + 2);
          }
        } else {
          g.fillRect(curposx, posy, DOT_DIAM, DOT_DIAM);
          g.setColor(Color.BLACK);
          g.setStroke(new BasicStroke(1f));
          g.drawRect(curposx - 1, posy - 1, DOT_DIAM + 1, DOT_DIAM + 1);
          if (cur == Lizzie.board.getHistory().getCurrentHistoryNode()) {
            g.setColor(Color.RED);
            g.fillRect(
                curposx + rectBorder,
                posy + rectBorder,
                DOT_DIAM - rect_DIAM,
                DOT_DIAM - rect_DIAM);
          }
        }
      }
      g.setColor(curcolor);
    }

    // Draw main line
    while (cur.next(true).isPresent() && posy + YSPACING < maxposy) {
      posy += YSPACING;
      cur = cur.next(true).get();
      if (cur.isCurTrunk()) curcolor = Color.WHITE;
      else curcolor = new Color(103, 103, 103);
      if (cur.isEndDummay()) {
        continue;
      }
      if (cur == curMove) curMoveLane = lane;
      if (calc) {
        if (inNode(curposx + dotoffset, posy + dotoffset)) {
          return Optional.of(cur);
        }
      } else if (curposx > minposx && posy > 0) {
        diam = DOT_DIAM;
        // }
        dotoffsety = diam / 2;
        diff = (DOT_DIAM - diam) / 2;
        boolean showComm = Lizzie.config.showCommentNodeColor && !cur.getData().comment.isEmpty();
        if (showComm) {
          g.setColor(Lizzie.config.commentNodeColor);
          g.fillOval(
              curposx + (DOT_DIAM + diff - RING_DIAM) / 2,
              posy + (DOT_DIAM + diff - RING_DIAM) / 2,
              RING_DIAM,
              RING_DIAM);
        }
        Color blunderColor = Lizzie.frame.getBlunderNodeColor(cur);
        g.setColor(blunderColor);
        g.fillOval(curposx + diff, posy + diff, diam, diam);
        if (curcolor != Color.WHITE) {
          g.setColor(new Color(0, 0, 0, 39));
          if (showComm)
            g.fillOval(
                curposx + (DOT_DIAM + diff - RING_DIAM) / 2,
                posy + (DOT_DIAM + diff - RING_DIAM) / 2,
                RING_DIAM,
                RING_DIAM);
          else g.fillOval(curposx + diff, posy + diff, diam, diam);
        }
        if (cur == Lizzie.board.getHistory().getCurrentHistoryNode()) {
          if (blunderColor != Color.WHITE) g.setColor(reverseColor(blunderColor));
          else g.setColor(Color.RED);
          g.fillOval(
              curposx + (DOT_DIAM + diff - CENTER_DIAM) / 2 - 1,
              posy + (DOT_DIAM + diff - CENTER_DIAM) / 2 - 1,
              CENTER_DIAM + 2,
              CENTER_DIAM + 2);
        }
        g.setColor(curcolor);
        g.drawLine(
            curposx + dotoffset,
            posy - 1 + diff,
            curposx + dotoffset,
            posy - YSPACING + dotoffset + (diff > 0 ? dotoffset + 1 : dotoffsety) + 1);
        if (Lizzie.config.showVarMove) {
          g.setFont(new Font(Lizzie.config.uiFontName, Font.PLAIN, isLargeScaled ? 12 : 9));
          g.setColor(Color.WHITE);
          int moveNum = lane == 0 ? cur.getData().moveNumber : cur.getData().moveMNNumber;
          if (moveNum < 0) {
            BoardHistoryNode nodeP = cur;
            int num = 0;
            while (moveNum < 0 && nodeP.previous().isPresent()) {
              nodeP = nodeP.previous().get();
              moveNum = nodeP.getData().moveMNNumber;
              num++;
            }
            moveNum = moveNum + num;
          }
          if (moveNum < 10) g.drawString(String.valueOf(moveNum), curposx - 7, posy + diff - 1);
          else
            g.drawString(
                String.valueOf(moveNum),
                moveNum >= 100 ? curposx - 13 : curposx - 10,
                posy + diff - 1);
        }
      }
    }
    // Now we have drawn all the nodes in this variation, and has reached the bottom
    // of this
    // variation
    // Move back up, and for each, draw any variations we find
    while (cur.previous().isPresent() && (cur != startNode)) {
      cur = cur.previous().get();
      int curwidth = lane;
      // Draw each variation, uses recursion
      for (int i = 1; i < cur.numberOfChildren(); i++) {
        curwidth++;
        // Recursion, depth of recursion will normally not be very deep (one recursion
        // level for
        // every variation that has a variation (sort of))
        Optional<BoardHistoryNode> variation = cur.getVariation(i);
        if (variation.isPresent()) {
          Optional<BoardHistoryNode> subNode =
              drawTree(g, posx, posy, curwidth, maxposy, minposx, variation.get(), i, calc);
          if (calc && subNode.isPresent()) {
            return subNode;
          }
        }
      }
      posy -= YSPACING;
    }
    return node;
  }

  public void draw(Graphics2D g, int posx, int posy, int width, int height) {
    maxLane = 0;
    draw(g, posx, posy, width, height, false);
  }

  public Optional<BoardHistoryNode> draw(
      Graphics2D g, int posx, int posy, int width, int height, boolean calc) {
    if (width <= 0 || height <= 0) {
      return Optional.empty(); // we don't have enough space
    }
    area.setBounds(posx, posy, width, height);
    // Get the lane of the current node by a dummy drawing.
    if (!calc) {
      int DUMMY = Integer.MIN_VALUE / 2;
      clickPoint.setLocation(DUMMY, DUMMY);
      curMoveLane = 0;
      draw(g, posx, posy, width, height, true); // set curMoveLane as a side effect
    }
    int lane = curMoveLane;
    // Use dense tree for saving space if large-subboard

    //    if (!calc) {
    //      g.setColor(new Color(0, 0, 0, 130));
    //      g.fillRect(posx, posy, width, height);
    //      g.setStroke(new BasicStroke(1));
    //    }
    if (!calc) g.setStroke(new BasicStroke(1));
    int middleY = posy + height / 2;
    int xoffset = 20;
    laneUsageList.clear();

    curMove = Lizzie.board.getHistory().getCurrentHistoryNode();
    // curMove = Lizzie.board.getHistory().getStart();
    // Is current move a variation? If so, find top of variation
    BoardHistoryNode top = curMove.findTop();
    int curposy = middleY - YSPACING * (curMove.getData().moveNumber - top.getData().moveNumber);
    // Go to very top of tree (visible in assigned area)
    BoardHistoryNode node = top;
    while (curposy > posy - YSPACING && node.previous().isPresent()) {
      node = node.previous().get();
      curposy -= YSPACING;
    }
    int startx = posx + xoffset;
    if (((lane + 1) * XSPACING + xoffset + DOT_DIAM - width) > 0) {
      startx = startx - ((lane + 1) * XSPACING + xoffset + DOT_DIAM - width);
    }
    if (maxLane * XSPACING + xoffset + DOT_DIAM > width) {
      if ((maxLane - lane) * XSPACING < (width - xoffset - DOT_DIAM) / 2)
        startx = startx - (maxLane - lane) * XSPACING;
      else if (lane * XSPACING + xoffset + DOT_DIAM > width)
        startx = startx - (width - xoffset - DOT_DIAM) / 2;
    }
    return drawTree(g, startx, curposy, 0, posy + height, posx, node, 0, calc);
  }

  private void drawLine(Graphics g, int x1, int y1, int x2, int y2, int minx) {
    if (x1 <= minx && x2 <= minx) {
      return;
    }
    int nx1 = x1, ny1 = y1, nx2 = x2, ny2 = y2;
    if (x1 > minx && x2 <= minx) {
      ny2 = y2 - (x1 - minx) / (x1 - x2) * (y2 - y1);
      nx2 = minx;
    } else if (x2 > minx && x1 <= minx) {
      ny1 = y1 - (x2 - minx) / (x2 - x1) * (y1 - y2);
      nx1 = minx;
    }
    g.drawLine(nx1, ny1, nx2, ny2);
  }

  public boolean inNode(int x, int y) {
    return Math.abs(clickPoint.x - x) <= XSPACING / 2 && Math.abs(clickPoint.y - y) <= YSPACING / 2;
  }

  public void onClicked(int x, int y) {
    if (area.contains(x, y)) {
      clickPoint.setLocation(x, y);
      Optional<BoardHistoryNode> node = draw(null, area.x, area.y, area.width, area.height, true);
      // if (node.isPresent()) Lizzie.frame.noautocounting();
      if (node.isPresent()) {
        Lizzie.board.moveToAnyPosition(node.get());
        Lizzie.frame.refresh();
      }
    }
  }

  private Color reverseColor(Color color) {
    // System.out.println("color=="+color);
    int r = color.getRed();
    int g = color.getGreen();
    int b = color.getBlue();
    int r_ = 255 - r;
    int g_ = 255 - g;
    int b_ = 255 - b;
    Color newColor = new Color(r_, g_, b_);
    return newColor;
  }
}
