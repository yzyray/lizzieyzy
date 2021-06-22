package featurecat.lizzie.gui;

import featurecat.lizzie.Lizzie;
import featurecat.lizzie.analysis.EngineManager;
import featurecat.lizzie.analysis.Leelaz;
import featurecat.lizzie.rules.BoardHistoryNode;
import java.awt.*;
import java.awt.geom.Point2D;
import java.util.Optional;

public class WinrateGraph {

  private int DOT_RADIUS = 3;
  private int[] origParams = {0, 0, 0, 0};
  private int[] params = {0, 0, 0, 0, 0};
  public BoardHistoryNode mouseOverNode;
  // private int numMovesOfPlayed = 0;
  public int mode = 0;
  public double maxcoreMean = 30.0;
  private boolean largeEnough = false;
  private BoardHistoryNode forkNode = null;
  private int scoreAjustMove = -10;
  private boolean scoreAjustBelow;
  private Color whiteColor = new Color(240, 240, 240);
  // boolean shouldShowScoreShort = false;

  //  public boolean getIsKataGo() {
  //    if (Lizzie.engineManager.isEngineGame)
  //      return Lizzie.engineManager.engineList.get(
  //                  Lizzie.engineManager.engineGameInfo.firstEngineIndex)
  //              .isKatago
  //          || Lizzie.engineManager.engineList.get(
  //                  Lizzie.engineManager.engineGameInfo.secondEngineIndex)
  //              .isKatago;
  //    else return Lizzie.leelaz.isKatago;
  //  }

  public void draw(
      Graphics2D g,
      Graphics2D gBlunder,
      Graphics2D gBackground,
      int posx,
      int posy,
      int width,
      int height) {
    largeEnough = width > 600 && height > 300;
    BoardHistoryNode curMove = Lizzie.board.getHistory().getCurrentHistoryNode();
    BoardHistoryNode node;
    if (Lizzie.frame.isTrying) node = Lizzie.board.getHistory().getMainEnd();
    else node = curMove;
    // maxcoreMean = 30.0;

    // draw background rectangle
    Paint original = g.getPaint();
    //    if (Lizzie.frame.extraMode == 1) {
    //      final Paint gradient =
    //          new GradientPaint(
    //              new Point2D.Float(posx, posy),
    //              new Color(100, 100, 100, 255),
    //              new Point2D.Float(posx, posy + height),
    //              new Color(155, 155, 155, 255));
    //
    //      g.setPaint(gradient);
    //
    //      g.fillRect(posx, posy, width, height);
    //    } else {
    final Paint gradient =
        new GradientPaint(
            new Point2D.Float(posx, posy),
            new Color(120, 120, 120, 180),
            new Point2D.Float(posx, posy + height / 2),
            new Color(155, 155, 155, 185));
    final Paint gradient2 =
        new GradientPaint(
            new Point2D.Float(posx, posy + height / 2),
            new Color(155, 155, 155, 185),
            new Point2D.Float(posx, posy + height),
            new Color(120, 120, 120, 180));
    gBackground.setPaint(gradient);
    //  g.setPaint( new Color(130, 130, 130, 130));

    gBackground.fillRect(posx, posy, width, height / 2);
    gBackground.setPaint(gradient2);
    gBackground.fillRect(posx, posy + height / 2, width, height / 2);
    // }
    // draw border
    int strokeRadius = 1;
    gBackground.setStroke(new BasicStroke(strokeRadius == 1 ? strokeRadius : 2 * strokeRadius));
    //  g.setPaint(borderGradient);
    gBackground.drawLine(
        posx + strokeRadius, posy + strokeRadius, posx - strokeRadius + width, posy + strokeRadius);

    g.setPaint(original);

    // record parameters (before resizing) for calculating moveNumber
    origParams[0] = posx;
    origParams[1] = posy;
    origParams[2] = width;
    origParams[3] = height;
    int blunderBottom = posy + height / 2 + height / 2;

    // resize the box now so it's inside the border
    // posx += 2 * strokeRadius;
    posy += 2 * strokeRadius;
    width -= 6 * strokeRadius;
    //    if (Lizzie.config.extraMode == 1
    //        || Lizzie.config.extraMode == 3
    //        || Lizzie.config.largeSubBoard && Lizzie.config.showSubBoard) {
    //      shouldShowScoreShort = true;
    //      width -= 8 * strokeRadius;
    //    } else shouldShowScoreShort = false;
    height -= 4 * strokeRadius;

    // draw lines marking 50% 60% 70% etc.
    Stroke dashed =
        new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[] {4}, 0);
    gBackground.setStroke(dashed);

    gBackground.setColor(Color.white);
    int winRateGridLines = Lizzie.frame.winRateGridLines;
    // int midline = 0;
    // int midy = 0;
    // if (Lizzie.config.showBlunderBar) {
    //   midline = (int) Math.ceil(winRateGridLines / 2.0);
    //  midy = posy + height / 2;
    //  }
    for (int i = 1; i <= winRateGridLines; i++) {
      double percent = i * 100.0 / (winRateGridLines + 1);
      int y = posy + height - (int) (height * percent / 100);
      //      if (Lizzie.config.showBlunderBar && i == midline) {
      //        midy = y;
      //      }
      gBackground.drawLine(posx, y, posx + width, y);
    }
    if (Lizzie.frame.isInPlayMode()) return;
    //    if(Lizzie.frame.extraMode==8)
    //    	{if(width>65)width=width-12;
    //    	else width=width*85/100;}
    g.setColor(Lizzie.config.winrateLineColor);
    // g.setColor(Color.BLACK);
    g.setStroke(new BasicStroke(Lizzie.config.winrateStrokeWidth));

    Optional<BoardHistoryNode> topOfVariation = Optional.empty();
    int numMoves = 0;
    if (!curMove.isMainTrunk()) {
      // We're in a variation, need to draw both main trunk and variation
      // Find top of variation
      BoardHistoryNode top = curMove.findTop();
      topOfVariation = Optional.of(top);
      // Find depth of main trunk, need this for plot scaling
      numMoves = top.getDepth() + top.getData().moveNumber - 1;
      //   g.setStroke(dashed);
    }

    // Go to end of variation and work our way backwards to the root

    while (node.next().isPresent()) {
      node = node.next().get();
    }
    if (numMoves < node.getData().moveNumber - 1) {
      numMoves = node.getData().moveNumber - 1;
    }

    if (numMoves < 1) return;
    if (numMoves < 50) numMoves = 50;

    // Plot
    width = (int) (width * 0.98); // Leave some space after last move
    double lastWr = 50;
    boolean lastNodeOk = false;
    int movenum = node.getData().moveNumber - 1;
    int lastOkMove = -1;
    //    if (Lizzie.config.dynamicWinrateGraphWidth && this.numMovesOfPlayed > 0) {
    //      numMoves = this.numMovesOfPlayed;
    //    }
    if (!Lizzie.config.showBlunderBar && width >= 150) {
      gBackground.setFont(new Font("", Font.PLAIN, 11));
      gBackground.setColor(new Color(200, 200, 200));
      if (numMoves <= 63) {
        for (int i = 1; i <= (numMoves / 10); i++)
          if (numMoves - i * 10 > 3)
            gBackground.drawString(
                i * 10 + "",
                posx + (i * 10 - 1) * width / numMoves - 3,
                posy + height - strokeRadius);
      } else if (numMoves <= 125) {
        for (int i = 1; i <= (numMoves / 20); i++)
          if (numMoves - i * 20 > 3)
            gBackground.drawString(
                i * 20 + "",
                posx + (i * 20 - 1) * width / numMoves - 3,
                posy + height - strokeRadius);
      } else if (numMoves < 205) {
        for (int i = 1; i <= (numMoves / 30); i++)
          if (numMoves - i * 30 > 3)
            gBackground.drawString(
                i * 30 + "",
                posx + (i * 30 - 1) * width / numMoves - 3,
                posy + height - strokeRadius);
      } else {
        for (int i = 1; i <= (numMoves / 40); i++)
          if (numMoves - i * 40 > 3)
            gBackground.drawString(
                i * 40 + "",
                posx + (i * 40 - 1) * width / numMoves - 3,
                posy + height - strokeRadius);
      }
    }
    double cwr = -1;
    int cmovenum = -1;
    double mwr = -1;
    int mmovenum = -1;
    int curScoreMoveNum = -1;
    double drawCurSoreMean = 0;
    int mScoreMoveNum = -1;
    double drawmSoreMean = 0;
    if (Lizzie.engineManager.isEngineGame || Lizzie.board.isPkBoard) {
      int saveCurMovenum = 0;
      double saveCurWr = 0;
      if (numMoves < 2) return;
      Stroke previousStroke = g.getStroke();
      int x =
          posx
              + ((Lizzie.board.getHistory().getCurrentHistoryNode().getData().moveNumber - 1)
                  * width
                  / numMoves);
      g.setStroke(dashed);
      g.setColor(Color.white);
      // if (Lizzie.board.getHistory().getCurrentHistoryNode() !=
      // Lizzie.board.getHistory().getEnd())
      g.drawLine(x, posy, x, posy + height);
      g.setStroke(previousStroke);
      String moveNumString =
          "" + Lizzie.board.getHistory().getCurrentHistoryNode().getData().moveNumber;
      //  int mw = g.getFontMetrics().stringWidth(moveNumString);
      int margin = strokeRadius;
      //      int mx = x - posx < width / 2 ? x + margin : x - mw - margin;
      //      if (node.getData().blackToPlay) {
      //
      //      } else {
      //        g.setColor(Color.BLACK);
      //      }
      g.setColor(Color.WHITE);
      if (Lizzie.board.getHistory().getCurrentHistoryNode() != Lizzie.board.getHistory().getEnd()) {
        Font f = new Font("", Font.BOLD, 12);
        g.setFont(f);
        g.setColor(Color.BLACK);
        int moveNum = Lizzie.board.getHistory().getCurrentHistoryNode().getData().moveNumber;
        if (moveNum < 10)
          g.drawString(
              moveNumString, moveNum < numMoves / 2 ? x + 3 : x - 10, posy + height - margin);
        else if (Lizzie.board.getHistory().getCurrentHistoryNode().getData().moveNumber > 99)
          g.drawString(
              moveNumString, moveNum < numMoves / 2 ? x + 3 : x - 22, posy + height - margin);
        else
          g.drawString(
              moveNumString, moveNum < numMoves / 2 ? x + 3 : x - 16, posy + height - margin);
      }
      while (node.previous().isPresent() && node.previous().get().previous().isPresent()) {
        double wr = 50;
        if (node.getData().getPlayouts() > 0) wr = node.getData().winrate;
        else if (node.previous().get().previous().get().getData().getPlayouts() > 0)
          wr = node.previous().get().previous().get().getData().winrate;
        if (node.previous().get().previous().get().getData().getPlayouts() > 0)
          lastWr = node.previous().get().previous().get().getData().winrate;
        else lastWr = wr;
        if (Lizzie.config.showBlunderBar) {
          gBlunder.setColor(Lizzie.config.blunderBarColor);
          double lastMoveRate = lastWr - wr;
          int lastHeight = 0;
          lastHeight = Math.abs((int) (lastMoveRate) * height / 200);
          // int lastWidth = Math.abs(2 * width / numMoves);
          int rectWidth =
              Math.max(
                  Lizzie.config.minimumBlunderBarWidth,
                  (int) (movenum * width / numMoves) - (int) ((movenum - 1) * width / numMoves));
          gBlunder.fillRect(
              posx + (int) ((movenum - 1) * width / numMoves),
              blunderBottom - lastHeight,
              rectWidth,
              lastHeight);
        }
        if (node.getData().blackToPlay) {
          g.setColor(Color.BLACK);
          g.drawLine(
              posx + ((movenum - 2) * width / numMoves),
              posy + height - (int) (lastWr * height / 100),
              posx + (movenum * width / numMoves),
              posy + height - (int) (wr * height / 100));

        } else {
          g.setColor(whiteColor);

          g.drawLine(
              posx + ((movenum - 2) * width / numMoves),
              posy + height - (int) (lastWr * height / 100),
              posx + (movenum * width / numMoves),
              posy + height - (int) (wr * height / 100));
        }

        lastOkMove = movenum - 2;

        if (curMove.previous().isPresent() && movenum > 1) {
          if (node == curMove) {
            saveCurMovenum = movenum;
            saveCurWr = wr;
          } else if (node == curMove.previous().get()) {
            if (node.getData().blackToPlay) {
              g.setColor(Color.BLACK);
              g.fillOval(
                  posx + (movenum * width / numMoves) - DOT_RADIUS,
                  posy + height - (int) (wr * height / 100) - DOT_RADIUS,
                  DOT_RADIUS * 2,
                  DOT_RADIUS * 2);
              Font f = new Font("", Font.BOLD, largeEnough ? 17 : 16);
              g.setFont(f);
              String wrString = String.format("%.1f", wr);
              int stringWidth = g.getFontMetrics().stringWidth(wrString);
              int xPos = posx + (movenum * width / numMoves) - stringWidth / 2;
              xPos = Math.max(xPos, origParams[0]);
              xPos = Math.min(xPos, origParams[0] + origParams[2] - stringWidth);
              if (wr > 50) {
                if (wr > 90) {
                  g.drawString(
                      wrString, xPos, posy + (height - (int) (wr * height / 100)) + 6 * DOT_RADIUS);
                } else {
                  g.drawString(
                      wrString, xPos, posy + (height - (int) (wr * height / 100)) - 2 * DOT_RADIUS);
                }
              } else {
                if (wr < 10) {
                  g.drawString(
                      wrString, xPos, posy + (height - (int) (wr * height / 100)) - 2 * DOT_RADIUS);
                } else {
                  g.drawString(
                      wrString, xPos, posy + (height - (int) (wr * height / 100)) + 6 * DOT_RADIUS);
                }
              }
            } else {
              g.setColor(whiteColor);
              g.fillOval(
                  posx + (movenum * width / numMoves) - DOT_RADIUS,
                  posy + height - (int) (wr * height / 100) - DOT_RADIUS,
                  DOT_RADIUS * 2,
                  DOT_RADIUS * 2);
              Font f = new Font("", Font.BOLD, largeEnough ? 17 : 16);
              g.setFont(f);
              g.setColor(Color.WHITE);
              String wrString = String.format("%.1f", wr);
              int stringWidth = g.getFontMetrics().stringWidth(wrString);
              int xPos = posx + (movenum * width / numMoves) - stringWidth / 2;
              xPos = Math.max(xPos, origParams[0]);
              xPos = Math.min(xPos, origParams[0] + origParams[2] - stringWidth);
              if (wr > 50) {
                if (wr < 90) {
                  g.drawString(
                      wrString, xPos, posy + (height - (int) (wr * height / 100)) - 2 * DOT_RADIUS);
                } else {
                  g.drawString(
                      wrString, xPos, posy + (height - (int) (wr * height / 100)) + 6 * DOT_RADIUS);
                }
              } else {
                if (wr < 10) {
                  g.drawString(
                      wrString, xPos, posy + (height - (int) (wr * height / 100)) - 2 * DOT_RADIUS);
                } else {
                  g.drawString(
                      wrString, xPos, posy + (height - (int) (wr * height / 100)) + 6 * DOT_RADIUS);
                }
              }
            }
          }
        }
        node = node.previous().get();
        movenum = movenum - 1;
      }
      if (saveCurMovenum > 1) {
        String wrString = String.format("%.1f", saveCurWr);
        int stringWidth = g.getFontMetrics().stringWidth(wrString);
        int xPos = posx + (saveCurMovenum * width / numMoves) - stringWidth / 2;
        xPos = Math.max(xPos, origParams[0]);
        xPos = Math.min(xPos, origParams[0] + origParams[2] - stringWidth);
        if (curMove.getData().blackToPlay) {
          g.setColor(Color.BLACK);
          g.fillOval(
              posx + (saveCurMovenum * width / numMoves) - DOT_RADIUS,
              posy + height - (int) (saveCurWr * height / 100) - DOT_RADIUS,
              DOT_RADIUS * 2,
              DOT_RADIUS * 2);
          Font f = new Font("", Font.BOLD, largeEnough ? 17 : 16);
          g.setFont(f);
          if (saveCurWr > 50) {
            if (saveCurWr > 90) {
              g.drawString(
                  wrString,
                  xPos,
                  posy + (height - (int) (saveCurWr * height / 100)) + 6 * DOT_RADIUS);
            } else {
              g.drawString(
                  wrString,
                  xPos,
                  posy + (height - (int) (saveCurWr * height / 100)) - 2 * DOT_RADIUS);
            }
          } else {
            if (saveCurWr < 10) {
              g.drawString(
                  wrString,
                  xPos,
                  posy + (height - (int) (saveCurWr * height / 100)) - 2 * DOT_RADIUS);
            } else {
              g.drawString(
                  wrString,
                  xPos,
                  posy + (height - (int) (saveCurWr * height / 100)) + 6 * DOT_RADIUS);
            }
          }
        } else {
          g.setColor(whiteColor);
          g.fillOval(
              posx + (saveCurMovenum * width / numMoves) - DOT_RADIUS,
              posy + height - (int) (saveCurWr * height / 100) - DOT_RADIUS,
              DOT_RADIUS * 2,
              DOT_RADIUS * 2);
          Font f = new Font("", Font.BOLD, largeEnough ? 17 : 16);
          g.setFont(f);
          g.setColor(Color.WHITE);
          if (saveCurWr > 50) {
            if (saveCurWr < 90) {
              g.drawString(
                  wrString,
                  xPos,
                  posy + (height - (int) (saveCurWr * height / 100)) - 2 * DOT_RADIUS);
            } else {
              g.drawString(
                  wrString,
                  xPos,
                  posy + (height - (int) (saveCurWr * height / 100)) + 6 * DOT_RADIUS);
            }
          } else {
            if (saveCurWr < 10) {
              g.drawString(
                  wrString,
                  xPos,
                  posy + (height - (int) (saveCurWr * height / 100)) - 2 * DOT_RADIUS);
            } else {
              g.drawString(
                  wrString,
                  xPos,
                  posy + (height - (int) (saveCurWr * height / 100)) + 6 * DOT_RADIUS);
            }
          }
        }
      }
    } else {
      if (mode == 0) {
        boolean canDrawBlunderBar = true;
        while (node.previous().isPresent()) {
          double wr = node.getData().winrate;
          int playouts = node.getData().getPlayouts();
          if (playouts > 0) {
            if (wr < 0) {
              wr = 100 - lastWr;
            } else if (!node.getData().blackToPlay) {
              wr = 100 - wr;
            }
            if (node == curMove) {
              // Draw a vertical line at the current move
              Stroke previousStroke = g.getStroke();
              int x = posx + (movenum * width / numMoves);
              g.setStroke(dashed);
              g.setColor(Color.WHITE);
              g.drawLine(x, posy, x, posy + height);
              // Show move number
              String moveNumString = "" + node.getData().moveNumber;
              //    int mw = g.getFontMetrics().stringWidth(moveNumString);
              int margin = strokeRadius;
              // int mx = x - posx < width / 2 ? x + margin : x - mw - margin;
              Font f = new Font("", Font.BOLD, 12);
              g.setFont(f);
              g.setColor(Color.BLACK);
              int moveNum = node.getData().moveNumber;
              if (wr < 10) {
                int fontHeight = g.getFontMetrics().getAscent() - g.getFontMetrics().getDescent();
                if (moveNum < 10)
                  g.drawString(
                      moveNumString,
                      moveNum < numMoves / 2 ? x + 3 : x - 10,
                      posy + fontHeight - margin);
                else if (Lizzie.board.getHistory().getCurrentHistoryNode().getData().moveNumber
                    > 99)
                  g.drawString(
                      moveNumString,
                      moveNum < numMoves / 2 ? x + 3 : x - 22,
                      posy + fontHeight - margin);
                else
                  g.drawString(
                      moveNumString,
                      moveNum < numMoves / 2 ? x + 3 : x - 16,
                      posy + fontHeight - margin);
              } else {
                if (moveNum < 10)
                  g.drawString(
                      moveNumString,
                      moveNum < numMoves / 2 ? x + 3 : x - 10,
                      posy + height - margin);
                else if (Lizzie.board.getHistory().getCurrentHistoryNode().getData().moveNumber
                    > 99)
                  g.drawString(
                      moveNumString,
                      moveNum < numMoves / 2 ? x + 3 : x - 22,
                      posy + height - margin);
                else
                  g.drawString(
                      moveNumString,
                      moveNum < numMoves / 2 ? x + 3 : x - 16,
                      posy + height - margin);
              }
              g.setStroke(previousStroke);
            }

            // if (Lizzie.frame.isPlayingAgainstLeelaz
            // && Lizzie.frame.playerIsBlack == !node.getData().blackToPlay) {
            // wr = lastWr;
            // }

            if (lastNodeOk) g.setColor(Lizzie.config.winrateLineColor);
            // g.setColor(Color.BLACK);
            else g.setColor(Lizzie.config.winrateMissLineColor);

            if (lastOkMove > 0 && Math.abs(movenum - lastOkMove) < 25) {
              if (Lizzie.config.showBlunderBar && canDrawBlunderBar) {
                gBlunder.setColor(Lizzie.config.blunderBarColor);
                double lastMoveRate = lastWr - wr;
                int lastHeight = 0;
                lastHeight = Math.abs((int) (lastMoveRate) * height / 200);
                // int lastWidth = Math.abs(2 * width / numMoves);
                int rectWidth =
                    Math.max(
                        Lizzie.config.minimumBlunderBarWidth,
                        (int) ((movenum + 1) * width / numMoves)
                            - (int) (movenum * width / numMoves));
                gBlunder.fillRect(
                    posx + (int) (((movenum + lastOkMove - 1)) * width / numMoves / 2),
                    blunderBottom - lastHeight,
                    rectWidth,
                    lastHeight);
              }
              g.drawLine(
                  posx + (lastOkMove * width / numMoves),
                  posy + height - (int) (lastWr * height / 100),
                  posx + (movenum * width / numMoves),
                  posy + height - (int) (wr * height / 100));
            }
            if (forkNode != null && forkNode == node) {
              canDrawBlunderBar = true;
              g.setStroke(new BasicStroke(Lizzie.config.winrateStrokeWidth));
            }
            lastWr = wr;
            lastNodeOk = true;
            // Check if we were in a variation and has reached the main trunk
            if (topOfVariation.isPresent()
                && topOfVariation.get() == node
                && node.next().isPresent()) {
              // Reached top of variation, go to end of main trunk before continuing
              canDrawBlunderBar = false;
              forkNode = topOfVariation.get();
              g.setStroke(dashed);
              while (node.next().isPresent()) {
                node = node.next().get();
              }
              movenum = node.getData().moveNumber - 1;
              lastWr = node.getData().winrate;
              if (!node.getData().blackToPlay) lastWr = 100 - lastWr;
              // g.setStroke(new BasicStroke(Lizzie.config.winrateStrokeWidth));
              topOfVariation = Optional.empty();
              if (node.getData().getPlayouts() == 0) {
                lastNodeOk = false;
              }
            }

            if (node == curMove
                || (curMove.previous().isPresent()
                    && node == curMove.previous().get()
                    && curMove.getData().getPlayouts() <= 0)) {
              g.setColor(Lizzie.config.winrateLineColor);
              g.fillOval(
                  posx + (movenum * width / numMoves) - DOT_RADIUS,
                  posy + height - (int) (wr * height / 100) - DOT_RADIUS,
                  DOT_RADIUS * 2,
                  DOT_RADIUS * 2);
              cwr = wr;
              cmovenum = movenum;
            }

            lastOkMove = lastNodeOk ? movenum : -1;
          } else {
            lastNodeOk = false;
            if (node == curMove) {
              // Draw a vertical line at the current move
              Stroke previousStroke = g.getStroke();
              int x = posx + (movenum * width / numMoves);
              g.setStroke(dashed);
              g.setColor(Color.WHITE);
              g.drawLine(x, posy, x, posy + height);
              // Show move number
              String moveNumString = "" + node.getData().moveNumber;
              g.setFont(new Font("", Font.BOLD, 12));
              g.setColor(Color.BLACK);
              int moveNum = node.getData().moveNumber;
              if (moveNum < 10)
                g.drawString(
                    moveNumString,
                    moveNum < numMoves / 2 ? x + 3 : x - 10,
                    posy + height - strokeRadius);
              else if (Lizzie.board.getHistory().getCurrentHistoryNode().getData().moveNumber > 99)
                g.drawString(
                    moveNumString,
                    moveNum < numMoves / 2 ? x + 3 : x - 22,
                    posy + height - strokeRadius);
              else
                g.drawString(
                    moveNumString,
                    moveNum < numMoves / 2 ? x + 3 : x - 16,
                    posy + height - strokeRadius);
              g.setStroke(previousStroke);
            }
          }

          if (mouseOverNode != null && node == mouseOverNode && node != curMove) {
            Stroke previousStroke = g.getStroke();
            int x = posx + (movenum * width / numMoves);
            g.setStroke(dashed);

            g.setColor(Color.CYAN);

            g.drawLine(x, posy, x, posy + height);
            // Show move number
            String moveNumString = "" + node.getData().moveNumber;
            //    int mw = g.getFontMetrics().stringWidth(moveNumString);
            int margin = strokeRadius;
            // int mx = x - posx < width / 2 ? x + margin : x - mw - margin;
            Font f = new Font("", Font.BOLD, 12);
            g.setFont(f);
            g.setColor(Color.BLACK);
            int moveNum = node.getData().moveNumber;
            if (wr < 10) {
              int fontHeight = g.getFontMetrics().getAscent() - g.getFontMetrics().getDescent();
              if (moveNum < 10)
                g.drawString(
                    moveNumString,
                    moveNum < numMoves / 2 ? x + 3 : x - 10,
                    posy + fontHeight - margin);
              else if (Lizzie.board.getHistory().getCurrentHistoryNode().getData().moveNumber > 99)
                g.drawString(
                    moveNumString,
                    moveNum < numMoves / 2 ? x + 3 : x - 22,
                    posy + fontHeight - margin);
              else
                g.drawString(
                    moveNumString,
                    moveNum < numMoves / 2 ? x + 3 : x - 16,
                    posy + fontHeight - margin);
            } else {
              if (moveNum < 10)
                g.drawString(
                    moveNumString, moveNum < numMoves / 2 ? x + 3 : x - 10, posy + height - margin);
              else if (Lizzie.board.getHistory().getCurrentHistoryNode().getData().moveNumber > 99)
                g.drawString(
                    moveNumString, moveNum < numMoves / 2 ? x + 3 : x - 22, posy + height - margin);
              else
                g.drawString(
                    moveNumString, moveNum < numMoves / 2 ? x + 3 : x - 16, posy + height - margin);
            }

            if (node.getData().getPlayouts() > 0) {
              mwr = wr;
              mmovenum = movenum;
            }

            g.setStroke(previousStroke);
          }

          node = node.previous().get();
          movenum--;
        }
        g.setStroke(new BasicStroke(1));

      } else if (mode == 1) {
        //    boolean isMain = node.isMainTrunk();
        while (node.previous().isPresent()) {
          double wr = node.getData().winrate;
          int playouts = node.getData().getPlayouts();
          if (node == curMove) {
            //            if (Lizzie.config.dynamicWinrateGraphWidth
            //                && node.getData().moveNumber - 1 > this.numMovesOfPlayed) {
            //              this.numMovesOfPlayed = node.getData().moveNumber - 1;
            //              numMoves = this.numMovesOfPlayed;
            //            }
            Leelaz.WinrateStats stats = Lizzie.leelaz.getWinrateStats();
            double bwr = stats.maxWinrate;
            if (bwr >= 0 && stats.totalPlayouts > playouts) {
              wr = bwr;
              playouts = stats.totalPlayouts;
            }
            // Draw a vertical line at the current move
            // Stroke previousStroke = g.getStroke();
            Stroke previousStroke = g.getStroke();
            int x = posx + (movenum * width / numMoves);
            g.setStroke(dashed);
            g.setColor(Color.WHITE);
            if (curMove != Lizzie.board.getHistory().getEnd())
              g.drawLine(x, posy, x, posy + height);

            // Show move number
            String moveNumString = "" + node.getData().moveNumber;
            //   int mw = g.getFontMetrics().stringWidth(moveNumString);
            int margin = strokeRadius;
            //       int mx = x - posx < width / 2 ? x + margin : x - mw - margin;
            //            if (node.getData().blackToPlay) {
            //              g.setColor(Color.WHITE);
            //            } else {
            //              g.setColor(Color.BLACK);
            //            }
            if (Lizzie.board.getHistory().getCurrentHistoryNode()
                != Lizzie.board.getHistory().getEnd()) {
              Font f = new Font("", Font.BOLD, 12);
              g.setFont(f);
              g.setColor(Color.BLACK);
              int moveNum = Lizzie.board.getHistory().getCurrentHistoryNode().getData().moveNumber;
              if (moveNum < 10)
                g.drawString(
                    moveNumString, moveNum < numMoves / 2 ? x + 3 : x - 10, posy + height - margin);
              else if (Lizzie.board.getHistory().getCurrentHistoryNode().getData().moveNumber > 99)
                g.drawString(
                    moveNumString, moveNum < numMoves / 2 ? x + 3 : x - 22, posy + height - margin);
              else
                g.drawString(
                    moveNumString, moveNum < numMoves / 2 ? x + 3 : x - 16, posy + height - margin);
            }
            g.setStroke(previousStroke);
          }
          if (playouts > 0) {
            if (wr < 0) {
              wr = 100 - lastWr;
            } else if (!node.getData().blackToPlay) {
              wr = 100 - wr;
            }
            // if (Lizzie.frame.isPlayingAgainstLeelaz
            // && Lizzie.frame.playerIsBlack == !node.getData().blackToPlay) {
            // wr = lastWr;
            // }

            if (lastOkMove > 0 && Math.abs(movenum - lastOkMove) < 25) {
              if (Lizzie.config.showBlunderBar) {
                gBlunder.setColor(Lizzie.config.blunderBarColor);
                double lastMoveRate = lastWr - wr;
                int lastHeight = 0;
                lastHeight = Math.abs((int) (lastMoveRate) * height / 200);
                // int lastWidth = Math.abs(2 * width / numMoves);
                int rectWidth =
                    Math.max(
                        Lizzie.config.minimumBlunderBarWidth,
                        (int) ((movenum + 1) * width / numMoves)
                            - (int) (movenum * width / numMoves));
                gBlunder.fillRect(
                    posx + (int) (((movenum + lastOkMove - 1)) * width / numMoves / 2),
                    blunderBottom - lastHeight,
                    rectWidth,
                    lastHeight);
              }
              //        if (isMain) {
              g.setColor(Color.BLACK);
              g.setStroke(new BasicStroke(Lizzie.config.winrateStrokeWidth));
              //              } else {
              //                g.setColor(Color.BLACK);
              //                g.setStroke(dashed);
              //              }
              //              if (lastNodeOk) g.setStroke(new BasicStroke(2f));
              //              else g.setStroke(new BasicStroke(1f));
              // g.setColor(Color.BLACK);
              g.drawLine(
                  posx + (lastOkMove * width / numMoves),
                  posy + height - (int) (lastWr * height / 100),
                  posx + (movenum * width / numMoves),
                  posy + height - (int) (wr * height / 100));
              //       if (isMain) {
              g.setColor(whiteColor);
              g.setStroke(new BasicStroke(Lizzie.config.winrateStrokeWidth));
              //              } else {
              //                g.setColor(Color.WHITE);
              //                g.setStroke(dashed);
              //              }
              //   if (lastNodeOk) g.setStroke(new BasicStroke(2f));
              //    else g.setStroke(new BasicStroke(1f));
              // g.setColor(Color.WHITE);
              g.drawLine(
                  posx + (lastOkMove * width / numMoves),
                  posy + height - (int) ((100 - lastWr) * height / 100),
                  posx + (movenum * width / numMoves),
                  posy + height - (int) ((100 - wr) * height / 100));
            }

            if (node == curMove
                || (curMove.previous().isPresent()
                    && node == curMove.previous().get()
                    && curMove.getData().getPlayouts() <= 0)) {
              g.setColor(Color.BLACK);
              g.fillOval(
                  posx + (movenum * width / numMoves) - DOT_RADIUS,
                  posy + height - (int) (wr * height / 100) - DOT_RADIUS,
                  DOT_RADIUS * 2,
                  DOT_RADIUS * 2);
              Font f = new Font("", Font.BOLD, 16);
              g.setFont(f);

              String wrString = String.format("%.1f", wr);
              int stringWidth = g.getFontMetrics().stringWidth(wrString);
              int x = posx + (movenum * width / numMoves) - stringWidth / 2;
              x = Math.max(x, origParams[0]);
              x = Math.min(x, origParams[0] + origParams[2] - stringWidth);

              if (wr > 50) {
                if (wr > 90) {
                  g.drawString(
                      wrString, x, posy + (height - (int) (wr * height / 100)) + 6 * DOT_RADIUS);
                } else {
                  g.drawString(
                      wrString, x, posy + (height - (int) (wr * height / 100)) - 2 * DOT_RADIUS);
                }
              } else {
                if (wr < 10) {
                  g.drawString(
                      wrString, x, posy + (height - (int) (wr * height / 100)) - 2 * DOT_RADIUS);
                } else {
                  g.drawString(
                      wrString, x, posy + (height - (int) (wr * height / 100)) + 6 * DOT_RADIUS);
                }
              }
              g.setColor(whiteColor);
              Font fw = new Font("", Font.BOLD, 16);
              g.setFont(fw);
              g.setColor(Color.WHITE);
              g.fillOval(
                  posx + (movenum * width / numMoves) - DOT_RADIUS,
                  posy + height - (int) ((100 - wr) * height / 100) - DOT_RADIUS,
                  DOT_RADIUS * 2,
                  DOT_RADIUS * 2);

              wrString = String.format("%.1f", 100 - wr);
              stringWidth = g.getFontMetrics().stringWidth(wrString);
              x = posx + (movenum * width / numMoves) - stringWidth / 2;
              x = Math.max(x, origParams[0]);
              x = Math.min(x, origParams[0] + origParams[2] - stringWidth);

              if (wr > 50) {
                if (wr < 90) {
                  g.drawString(
                      wrString,
                      x,
                      posy + (height - (int) ((100 - wr) * height / 100)) + 6 * DOT_RADIUS);
                } else {
                  g.drawString(
                      wrString,
                      x,
                      posy + (height - (int) ((100 - wr) * height / 100)) - 2 * DOT_RADIUS);
                }
              } else {
                if (wr > 10) {
                  g.drawString(
                      wrString,
                      x,
                      posy + (height - (int) ((100 - wr) * height / 100)) - 2 * DOT_RADIUS);
                } else {
                  g.drawString(
                      wrString,
                      x,
                      posy + (height - (int) ((100 - wr) * height / 100)) + 6 * DOT_RADIUS);
                }
              }
            }
            lastWr = wr;
            lastNodeOk = true;
            // Check if we were in a variation and has reached the main trunk
            //            if (topOfVariation.isPresent() && topOfVariation.get() == node) {
            //              // Reached top of variation, go to end of main trunk before continuing
            //              while (node.next().isPresent()) {
            //                node = node.next().get();
            //              }
            //              movenum = node.getData().moveNumber - 1;
            //              lastWr = node.getData().winrate;
            //              if (!node.getData().blackToPlay) lastWr = 100 - lastWr;
            //              // g.setStroke(new BasicStroke(2));
            //              isMain = true;
            //              topOfVariation = Optional.empty();
            //              if (node.getData().getPlayouts() == 0) {
            //                lastNodeOk = false;
            //              }
            //            }
            lastOkMove = lastNodeOk ? movenum : -1;
          } else {
            lastNodeOk = false;
          }
          // g.setStroke(new BasicStroke(1));
          node = node.previous().get();
          movenum--;
        }
      }
    }
    // 添加是否显示目差
    if (Lizzie.config.showScoreLeadLine) {
      node = curMove;
      while (node.next().isPresent()) {
        node = node.next().get();
      }
      if (numMoves < node.getData().moveNumber - 1) {
        numMoves = node.getData().moveNumber - 1;
      }

      if (numMoves < 1) return;
      lastOkMove = -1;
      movenum = node.getData().moveNumber - 1;
      //    if (Lizzie.config.dynamicWinrateGraphWidth && this.numMovesOfPlayed > 0) {
      //      numMoves = this.numMovesOfPlayed;
      //    }
      if (Lizzie.engineManager.isEngineGame || Lizzie.board.isPkBoard) {
        maxcoreMean = 15;
        setMaxScoreMean(node);
        if (Lizzie.engineManager.isEngineGame
                && (Lizzie.engineManager.engineList.get(
                            Lizzie.engineManager.engineGameInfo.whiteEngineIndex)
                        .isKatago
                    || Lizzie.engineManager.engineList.get(
                            Lizzie.engineManager.engineGameInfo.whiteEngineIndex)
                        .isSai)
            || Lizzie.board.isPkBoardKataW) {
          double lastscoreMean = -500;
          int curmovenum = -1;
          double drawcurscoreMean = 0;
          if (node.getData().blackToPlay) movenum -= 1;
          if (curMove.getData().blackToPlay && curMove.previous().isPresent())
            curMove = curMove.previous().get();
          if (node.getData().blackToPlay && node.previous().isPresent()) {
            double curscoreMean = 0;
            try {
              curscoreMean = node.previous().get().getData().scoreMean;
            } catch (Exception ex) {
            }
            if (EngineManager.isEngineGame) {
              curmovenum = movenum;
              drawcurscoreMean = curscoreMean;
              lastscoreMean = curscoreMean;
              lastOkMove = movenum;
            }
            node = node.previous().get();
          }
          while (node.previous().isPresent() && node.previous().get().previous().isPresent()) {
            if (node.getData().getPlayouts() > 0) {
              double curscoreMean = node.getData().scoreMean;
              //              if (Math.abs(curscoreMean) > maxcoreMean)
              //            	  maxcoreMean = Math.abs(curscoreMean);

              if (node == curMove) {
                curmovenum = movenum;
                drawcurscoreMean = curscoreMean;
              }
              if (lastOkMove > 0 && Math.abs(movenum - lastOkMove) < 25) {

                if (lastscoreMean > -500) {
                  // Color lineColor = g.getColor();
                  Stroke previousStroke = g.getStroke();
                  g.setColor(Lizzie.config.scoreMeanLineColor);
                  g.setStroke(new BasicStroke(1));
                  g.drawLine(
                      posx + ((lastOkMove) * width / numMoves),
                      posy
                          + height / 2
                          - (int) (convertcoreMean(lastscoreMean) * height / 2 / maxcoreMean),
                      posx + ((movenum) * width / numMoves),
                      posy
                          + height / 2
                          - (int) (convertcoreMean(curscoreMean) * height / 2 / maxcoreMean));
                  g.setStroke(previousStroke);
                }
              }

              lastscoreMean = curscoreMean;
              lastOkMove = movenum;
            } else {
              if (Lizzie.engineManager.isEngineGame
                  && (!node.next().isPresent() || !node.next().get().next().isPresent())) {
                curmovenum = movenum;
                drawcurscoreMean = node.previous().get().previous().get().getData().scoreMean;
              }
            }
            if (node.previous().isPresent() && node.previous().get().previous().isPresent())
              node = node.previous().get().previous().get();
            movenum = movenum - 2;
          }
          if (lastscoreMean > -500) {
            // Color lineColor = g.getColor();
            Stroke previousStroke = g.getStroke();
            g.setColor(Lizzie.config.scoreMeanLineColor);
            g.setStroke(new BasicStroke(1));
            g.drawLine(
                posx + ((lastOkMove) * width / numMoves),
                posy
                    + height / 2
                    - (int) (convertcoreMean(lastscoreMean) * height / 2 / maxcoreMean),
                posx + ((movenum) * width / numMoves),
                posy
                    + height / 2
                    - (int) (convertcoreMean(lastscoreMean) * height / 2 / maxcoreMean));
            g.setStroke(previousStroke);
          }
          if (curmovenum > 0) {
            g.setColor(Color.YELLOW);
            Font f = new Font("", Font.BOLD, largeEnough ? 14 : 13);
            g.setFont(f);
            double scoreHeight = convertcoreMean(drawcurscoreMean) * height / 2 / maxcoreMean;

            String scoreString = String.format("%.1f", drawcurscoreMean);
            int stringWidth = g.getFontMetrics().stringWidth(scoreString);
            int x = posx + (curmovenum * width / numMoves) - stringWidth / 2;
            x = Math.max(x, origParams[0]);
            x = Math.min(x, origParams[0] + origParams[2] - stringWidth);
            g.drawString(scoreString, x, posy + height / 2 - (int) scoreHeight - 3);
            //   + (scoreHeight / (height / 2) < -0.9 ? 0 : 5) * DOT_RADIUS);
          }
        } else if (Lizzie.engineManager.isEngineGame
                && (Lizzie.engineManager.engineList.get(
                            Lizzie.engineManager.engineGameInfo.blackEngineIndex)
                        .isKatago
                    || Lizzie.engineManager.engineList.get(
                            Lizzie.engineManager.engineGameInfo.blackEngineIndex)
                        .isSai)
            || Lizzie.board.isPkBoardKataB) {
          double lastscoreMean = -500;
          int curmovenum = -1;
          double drawcurscoreMean = 0;
          if (!node.getData().blackToPlay) movenum -= 1;
          if (!node.getData().blackToPlay && node.previous().isPresent()) {
            double curscoreMean = 0;
            try {
              curscoreMean = node.previous().get().getData().scoreMean;
            } catch (Exception ex) {
            }
            if (Lizzie.engineManager.isEngineGame) {
              curmovenum = movenum;
              drawcurscoreMean = curscoreMean;
              lastscoreMean = curscoreMean;
              lastOkMove = movenum;
            }
            node = node.previous().get();
          }
          if (!curMove.getData().blackToPlay && curMove.previous().isPresent())
            curMove = curMove.previous().get();
          while (node.previous().isPresent() && node.previous().get().previous().isPresent()) {
            if (node.getData().getPlayouts() > 0) {

              double curscoreMean = node.getData().scoreMean;
              //              if (Math.abs(curscoreMean) > maxcoreMean)
              //            	  maxcoreMean = Math.abs(curscoreMean);

              if (node == curMove) {
                curmovenum = movenum;
                drawcurscoreMean = curscoreMean;
              }
              if (lastOkMove > 0 && Math.abs(movenum - lastOkMove) < 25) {

                if (lastscoreMean > -500) {
                  // Color lineColor = g.getColor();
                  Stroke previousStroke = g.getStroke();
                  g.setColor(Lizzie.config.scoreMeanLineColor);
                  g.setStroke(new BasicStroke(1));
                  g.drawLine(
                      posx + ((lastOkMove) * width / numMoves),
                      posy
                          + height / 2
                          - (int) (convertcoreMean(lastscoreMean) * height / 2 / maxcoreMean),
                      posx + ((movenum) * width / numMoves),
                      posy
                          + height / 2
                          - (int) (convertcoreMean(curscoreMean) * height / 2 / maxcoreMean));
                  g.setStroke(previousStroke);
                }
              }

              lastscoreMean = curscoreMean;
              lastOkMove = movenum;
            } else {
              if (Lizzie.engineManager.isEngineGame
                  && (!node.next().isPresent() || !node.next().get().next().isPresent())) {
                curmovenum = movenum;
                drawcurscoreMean = node.previous().get().previous().get().getData().scoreMean;
              }
            }
            if (node.previous().isPresent() && node.previous().get().previous().isPresent())
              node = node.previous().get().previous().get();
            movenum = movenum - 2;
          }
          if (lastscoreMean > -500) {
            // Color lineColor = g.getColor();
            Stroke previousStroke = g.getStroke();
            g.setColor(Lizzie.config.scoreMeanLineColor);
            g.setStroke(new BasicStroke(1));
            g.drawLine(
                posx + ((lastOkMove) * width / numMoves),
                posy
                    + height / 2
                    - (int) (convertcoreMean(lastscoreMean) * height / 2 / maxcoreMean),
                posx + ((movenum) * width / numMoves),
                posy
                    + height / 2
                    - (int) (convertcoreMean(lastscoreMean) * height / 2 / maxcoreMean));
            g.setStroke(previousStroke);
          }
          if (curmovenum > 0) {
            g.setColor(Color.YELLOW);
            Font f = new Font("", Font.BOLD, largeEnough ? 14 : 13);
            g.setFont(f);
            double scoreHeight = convertcoreMean(drawcurscoreMean) * height / 2 / maxcoreMean;

            String scoreString = String.format("%.1f", drawcurscoreMean);
            int stringWidth = g.getFontMetrics().stringWidth(scoreString);
            int x = posx + (curmovenum * width / numMoves) - stringWidth / 2;
            x = Math.max(x, origParams[0]);
            x = Math.min(x, origParams[0] + origParams[2] - stringWidth);
            g.drawString(scoreString, x, posy + height / 2 - (int) scoreHeight - 3);
            //   + (scoreHeight / (height / 2) < -0.9 ? 0 : 5) * DOT_RADIUS);
          }
        }
      } else if (Lizzie.leelaz.isSai || Lizzie.leelaz.isKatago || Lizzie.board.isKataBoard) {
        maxcoreMean = 30;
        setMaxScoreMean(node);
        double lastscoreMean = -500;
        while (node.previous().isPresent()) {
          if (node.getData().getPlayouts() > 0) {

            double curscoreMean = node.getData().scoreMean;

            if (!node.getData().blackToPlay) {
              curscoreMean = -curscoreMean;
            }
            if (Lizzie.config.scoreMeanWinrateGraphBoard)
              curscoreMean = curscoreMean + Lizzie.board.getHistory().getGameInfo().getKomi();
            //            if (Math.abs(curscoreMean) > maxcoreMean)
            //            	maxcoreMean = Math.abs(curscoreMean);

            if (node == curMove
                || (curMove.previous().isPresent()
                    && node == curMove.previous().get()
                    && curMove.getData().getPlayouts() <= 0)) {
              curScoreMoveNum = movenum;
              drawCurSoreMean = curscoreMean;
            } else if (mouseOverNode != null && node == mouseOverNode) {
              mScoreMoveNum = movenum;
              drawmSoreMean = curscoreMean;
            }
            if (lastOkMove > 0 && Math.abs(movenum - lastOkMove) < 25) {

              if (lastscoreMean > -500) {
                // Color lineColor = g.getColor();
                Stroke previousStroke = g.getStroke();
                g.setColor(Lizzie.config.scoreMeanLineColor);
                //                if (!node.isMainTrunk()) {
                //                  g.setStroke(dashed);
                //                } else
                g.setStroke(new BasicStroke(1));
                g.drawLine(
                    posx + (lastOkMove * width / numMoves),
                    posy
                        + height / 2
                        - (int) (convertcoreMean(lastscoreMean) * height / 2 / maxcoreMean),
                    posx + (movenum * width / numMoves),
                    posy
                        + height / 2
                        - (int) (convertcoreMean(curscoreMean) * height / 2 / maxcoreMean));
                g.setStroke(previousStroke);
              }
            }

            lastscoreMean = curscoreMean;
            lastOkMove = movenum;
          }

          node = node.previous().get();
          movenum--;
        }
      }
      // g.setStroke(new BasicStroke(1));

      // record parameters for calculating moveNumber
    }
    int mwrHeight = -1;
    int mWinFontHeight = -1;
    int oriMWrHeight = -1;
    int mx = -1;
    if (mwr >= 0) {
      g.setColor(Color.MAGENTA);
      g.fillOval(
          posx + (mmovenum * width / numMoves) - DOT_RADIUS,
          posy + height - (int) (mwr * height / 100) - DOT_RADIUS,
          DOT_RADIUS * 2,
          DOT_RADIUS * 2);
      g.setColor(Color.BLACK);
      Font f = new Font("", Font.BOLD, largeEnough ? 17 : 16);
      g.setFont(f);
      oriMWrHeight = posy + (height - (int) (mwr * height / 100));
      mwrHeight = oriMWrHeight + (mwr < 10 ? -5 : (mwr > 90 ? 6 : -2) * DOT_RADIUS);
      mWinFontHeight = g.getFontMetrics().getAscent() - g.getFontMetrics().getDescent();
      if (mwrHeight > origParams[1] + origParams[3]) {
        mwrHeight = origParams[1] + origParams[3] - 2;
      }

      String mwrString = String.format("%.1f", mwr);
      int stringWidth = g.getFontMetrics().stringWidth(mwrString);
      int x = posx + (mmovenum * width / numMoves) - stringWidth / 2;
      x = Math.max(x, origParams[0]);
      x = Math.min(x, origParams[0] + origParams[2] - stringWidth);
      mx = x;
      g.drawString(mwrString, x, mwrHeight);
    }
    if (mScoreMoveNum >= 0) {
      //        if (Lizzie.config.dynamicWinrateGraphWidth
      //            && node.getData().moveNumber - 1 > this.numMovesOfPlayed) {
      //          this.numMovesOfPlayed = node.getData().moveNumber - 1;
      //          numMoves = this.numMovesOfPlayed;
      //        }
      g.setColor(Color.YELLOW);
      Font f = new Font("", Font.BOLD, largeEnough ? 15 : 14);
      g.setFont(f);
      double scoreHeight = convertcoreMean(drawmSoreMean) * height / 2 / maxcoreMean;
      int mScoreHeight = posy + height / 2 - (int) scoreHeight - 3;
      int oriScoreHeight = mScoreHeight;
      int fontHeigt = g.getFontMetrics().getAscent() - g.getFontMetrics().getDescent();
      int up = origParams[1] + fontHeigt;
      int down = origParams[1] + origParams[3];
      mScoreHeight = Math.max(up, mScoreHeight);
      mScoreHeight = Math.min(down, mScoreHeight);
      int heightDiff = Math.abs(mwrHeight - mScoreHeight);

      if (heightDiff < fontHeigt) {
        if (oriScoreHeight < oriMWrHeight) {
          if (mwrHeight - mWinFontHeight - 1 >= up) mScoreHeight = mwrHeight - mWinFontHeight - 1;
          else mScoreHeight = mwrHeight + fontHeigt + 1;
        } else if (mwrHeight + fontHeigt + 1 <= down) mScoreHeight = mwrHeight + fontHeigt + 1;
        else mScoreHeight = mwrHeight - mWinFontHeight - 1;
      }
      if (mScoreHeight > origParams[1] + origParams[3]) {
        mScoreHeight = Math.max(origParams[1] + origParams[3], mwrHeight - mWinFontHeight);
      }
      String scoreString = String.format("%.1f", drawmSoreMean);
      int stringWidth = g.getFontMetrics().stringWidth(scoreString);
      int x = posx + (mScoreMoveNum * width / numMoves) - stringWidth / 2;
      x = Math.max(x, origParams[0]);
      x = Math.min(x, origParams[0] + origParams[2] - stringWidth);
      g.drawString(scoreString, x, mScoreHeight);
    }

    int cwrHeight = -1;
    int winFontHeight = -1;
    int oriWrHeight = -1;
    boolean noC = false;
    if (cwr >= 0) {
      Font f = new Font("", Font.BOLD, largeEnough ? 17 : 16);
      g.setFont(f);
      g.setColor(Color.BLACK);
      oriWrHeight = posy + (height - (int) (cwr * height / 100));
      cwrHeight = oriWrHeight + (cwr < 10 ? -5 : (cwr > 90 ? 6 : -2) * DOT_RADIUS);
      winFontHeight = g.getFontMetrics().getAscent() - g.getFontMetrics().getDescent();
      if (cwrHeight > origParams[1] + origParams[3]) {
        cwrHeight = origParams[1] + origParams[3] - 2;
      }
      String wrString = String.format("%.1f", cwr);
      int stringWidth = g.getFontMetrics().stringWidth(wrString);
      int x = posx + (cmovenum * width / numMoves) - stringWidth / 2;
      x = Math.max(x, origParams[0]);
      x = Math.min(x, origParams[0] + origParams[2] - stringWidth);
      if (mx >= 0) {
        if (Math.abs(x - mx) < stringWidth) noC = true;
      }
      if (!noC) g.drawString(wrString, x, cwrHeight);
    }
    if (curScoreMoveNum >= 0 && !noC) {
      g.setColor(Color.YELLOW);
      Font f = new Font("", Font.BOLD, largeEnough ? 15 : 14);
      g.setFont(f);
      double scoreHeight = convertcoreMean(drawCurSoreMean) * height / 2 / maxcoreMean;
      int cScoreHeight = posy + height / 2 - (int) scoreHeight - 3;
      int oriScoreHeight = cScoreHeight;
      int fontHeigt = g.getFontMetrics().getAscent() - g.getFontMetrics().getDescent();
      int up = origParams[1] + fontHeigt;
      int down = origParams[1] + origParams[3];
      cScoreHeight = Math.max(up, cScoreHeight);
      cScoreHeight = Math.min(down, cScoreHeight);
      int heightDiff = Math.abs(cwrHeight - cScoreHeight);

      if (heightDiff < fontHeigt) {
        if (heightDiff <= fontHeigt / 3 && scoreAjustMove == curScoreMoveNum) {
          if (scoreAjustBelow) cScoreHeight = cwrHeight + fontHeigt + 1;
          else cScoreHeight = cwrHeight - winFontHeight - 1;
        } else {
          if (oriScoreHeight < oriWrHeight) {
            if (cwrHeight - winFontHeight - 1 >= up) {
              cScoreHeight = cwrHeight - winFontHeight - 1;
              scoreAjustBelow = false;
            } else {
              cScoreHeight = cwrHeight + fontHeigt + 1;
              scoreAjustBelow = true;
            }
          } else if (cwrHeight + fontHeigt + 1 <= down) {
            cScoreHeight = cwrHeight + fontHeigt + 1;
            scoreAjustBelow = true;
          } else {
            cScoreHeight = cwrHeight - winFontHeight - 1;
            scoreAjustBelow = false;
          }
          if (heightDiff <= fontHeigt / 3) {
            scoreAjustMove = curScoreMoveNum;
          } else scoreAjustMove = -1;
        }
      }
      String scoreString = String.format("%.1f", drawCurSoreMean);
      int stringWidth = g.getFontMetrics().stringWidth(scoreString);
      int x = posx + (curScoreMoveNum * width / numMoves) - stringWidth / 2;
      x = Math.max(x, origParams[0]);
      x = Math.min(x, origParams[0] + origParams[2] - stringWidth);
      g.drawString(scoreString, x, cScoreHeight);
    }

    params[0] = posx;
    params[1] = posy;
    params[2] = width;
    params[3] = height;
    params[4] = numMoves;
  }

  private double convertcoreMean(double coreMean) {

    if (coreMean > maxcoreMean) return maxcoreMean;
    if (coreMean < 0 && Math.abs(coreMean) > maxcoreMean) return -maxcoreMean;
    return coreMean;
  }

  //  private double convertWinrate(double winrate) {
  //    double maxHandicap = 10;
  //    if (Lizzie.config.handicapInsteadOfWinrate) {
  //      double handicap = Lizzie.leelaz.winrateToHandicap(winrate);
  //      // handicap == + maxHandicap => r == 1.0
  //      // handicap == - maxHandicap => r == 0.0
  //      double r = 0.5 + handicap / (2 * maxHandicap);
  //      return Math.max(0, Math.min(r, 1)) * 100;
  //    } else {
  //      return winrate;
  //    }
  //  }

  public void setMaxScoreMean(BoardHistoryNode lastMove) {
    while (lastMove.previous().isPresent()) {
      Double scoreMean = lastMove.getData().scoreMean;
      if (Math.abs(scoreMean) > maxcoreMean) maxcoreMean = Math.abs(scoreMean);
      lastMove = lastMove.previous().get();
    }
    Double scoreMean = lastMove.getData().scoreMean;
    if (Math.abs(scoreMean) > maxcoreMean) maxcoreMean = Math.abs(scoreMean);
  }

  public void setMouseOverNode(BoardHistoryNode node) {
    mouseOverNode = node;
  }

  public void clearMouseOverNode() {
    mouseOverNode = null;
  }

  public int moveNumber(int x, int y) {
    int origPosx = origParams[0];
    int origPosy = origParams[1];
    int origWidth = origParams[2];
    int origHeight = origParams[3];
    int posx = params[0];
    int width = params[2];
    int numMoves = params[4];
    if (origPosx <= x && x < origPosx + origWidth && origPosy <= y && y < origPosy + origHeight) {
      // x == posx + (movenum * width / numMoves) ==> movenum = ...
      int movenum = Math.round((x - posx) * numMoves / (float) width);
      // movenum == moveNumber - 1 ==> moveNumber = ...
      return movenum + 1;
    } else {
      return -1;
    }
  }

  /** Clears winrate status from empty board. */
  public void clear() {
    // this.numMovesOfPlayed = 0;
  }
}
