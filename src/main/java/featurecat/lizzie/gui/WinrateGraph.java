package featurecat.lizzie.gui;

import featurecat.lizzie.Config;
import featurecat.lizzie.Lizzie;
import featurecat.lizzie.analysis.EngineManager;
import featurecat.lizzie.rules.BoardHistoryNode;
import featurecat.lizzie.util.Utils;
import java.awt.*;
import java.awt.geom.Point2D;
import java.util.Locale;
import java.util.Optional;

public class WinrateGraph {

  private int DOT_RADIUS = 3;
  private int[] origParams = {0, 0, 0, 0};
  private int[] params = {0, 0, 0, 0, 0};
  public BoardHistoryNode mouseOverNode;
  // private int numMovesOfPlayed = 0;
  public int mode = 0;
  private double maxScoreLead = Lizzie.config.initialMaxScoreLead;
  private double weightedMaxScoreBlunder = 50;
  private boolean largeEnough = false;
  private BoardHistoryNode forkNode = null;
  private int scoreAjustMove = -10;
  private boolean scoreAjustBelow;
  private Color whiteColor = new Color(240, 240, 240);
  private boolean noC = false;

  public void draw(
      Graphics2D g,
      Graphics2D gBlunder,
      Graphics2D gBackground,
      int posx,
      int posy,
      int width,
      int height) {
    largeEnough = width > 475 && height > 335;
    BoardHistoryNode curMove = Lizzie.board.getHistory().getCurrentHistoryNode();
    BoardHistoryNode node;
    if (Lizzie.frame.isTrying) node = Lizzie.board.getHistory().getMainEnd();
    else node = curMove;
    // draw background rectangle
    int halfHeight = height / 2;
    final Paint gradient =
        new GradientPaint(
            new Point2D.Float(posx, posy),
            new Color(120, 120, 120, 180),
            new Point2D.Float(posx, posy + halfHeight),
            new Color(155, 155, 155, 185));
    final Paint gradient2 =
        new GradientPaint(
            new Point2D.Float(posx, posy + halfHeight),
            new Color(155, 155, 155, 185),
            new Point2D.Float(posx, posy + height),
            new Color(120, 120, 120, 180));
    gBackground.setPaint(gradient);
    gBackground.fillRect(posx, posy, width, halfHeight);
    gBackground.setPaint(gradient2);
    gBackground.fillRect(posx, posy + halfHeight, width, height - halfHeight);

    int strokeRadius = 1;
    // record parameters (before resizing) for calculating moveNumber
    origParams[0] = posx;
    origParams[1] = posy;
    origParams[2] = width;
    origParams[3] = height;
    int blunderBottom = posy + height;

    // resize the box now so it's inside the border
    posy += 2 * strokeRadius;
    width -= 6 * strokeRadius;
    height -= 4 * strokeRadius;

    // draw lines marking 50% 60% 70% etc.
    Stroke dashed =
        new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[] {4}, 0);
    gBackground.setStroke(dashed);

    gBackground.setColor(Color.white);
    int winRateGridLines = Lizzie.frame.winRateGridLines;
    for (int i = 1; i <= winRateGridLines; i++) {
      double percent = i * 100.0 / (winRateGridLines + 1);
      int y = posy + height - (int) (height * percent / 100);
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
    double lastScore = 0;
    boolean lastNodeOk = false;
    int movenum = node.getData().moveNumber - 1;
    int lastOkMove = -1;
    //    if (Lizzie.config.dynamicWinrateGraphWidth && this.numMovesOfPlayed > 0) {
    //      numMoves = this.numMovesOfPlayed;
    //    }
    if (!Lizzie.config.showBlunderBar && width >= 150) {
      gBackground.setFont(
          new Font(Config.sysDefaultFontName, Font.PLAIN, largeEnough ? Utils.zoomOut(11) : 11));
      gBackground.setColor(new Color(200, 200, 200));
      if (numMoves <= 63) {
        for (int i = 1; i <= (numMoves / 10); i++)
          if (numMoves - i * 10 > 3)
            gBackground.drawString(
                String.valueOf(i * 10),
                posx + (i * 10 - 1) * width / numMoves - 3,
                posy + height - strokeRadius);
      } else if (numMoves <= 125) {
        for (int i = 1; i <= (numMoves / 20); i++)
          if (numMoves - i * 20 > 3)
            gBackground.drawString(
                String.valueOf(i * 20),
                posx + (i * 20 - 1) * width / numMoves - 3,
                posy + height - strokeRadius);
      } else if (numMoves < 205) {
        for (int i = 1; i <= (numMoves / 30); i++)
          if (numMoves - i * 30 > 3)
            gBackground.drawString(
                String.valueOf(i * 30),
                posx + (i * 30 - 1) * width / numMoves - 3,
                posy + height - strokeRadius);
      } else {
        for (int i = 1; i <= (numMoves / 40); i++)
          if (numMoves - i * 40 > 3)
            gBackground.drawString(
                String.valueOf(i * 40),
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
    if (EngineManager.isEngineGame || Lizzie.board.isPkBoard) {
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
          String.valueOf(Lizzie.board.getHistory().getCurrentHistoryNode().getData().moveNumber);
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
        Font f =
            new Font(Config.sysDefaultFontName, Font.BOLD, largeEnough ? Utils.zoomOut(12) : 12);
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
        double score = 0;
        if (node.getData().getPlayouts() > 0) {
          wr = node.getData().winrate;
          score = node.getData().scoreMean;
        } else if (node.previous().get().previous().get().getData().getPlayouts() > 0) {
          wr = node.previous().get().previous().get().getData().winrate;
          score = node.previous().get().previous().get().getData().scoreMean;
        }
        if (node.previous().get().previous().get().getData().getPlayouts() > 0) {
          lastWr = node.previous().get().previous().get().getData().winrate;
          lastScore = node.previous().get().previous().get().getData().scoreMean;
        } else {
          lastWr = wr;
          lastScore = score;
        }
        if (Lizzie.config.showBlunderBar) {
          gBlunder.setColor(Lizzie.config.blunderBarColor);
          double lastMoveRate = Math.abs(lastWr - wr);
          double lastMoveScoreRate =
              Math.min(1.0, Math.abs(lastScore - score) / weightedMaxScoreBlunder);
          int lastHeight = 0;
          lastHeight =
              Math.max((int) (lastMoveRate * height / 200), (int) (lastMoveScoreRate * height) / 2);
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
        lastOkMove = movenum - 2;
        if (Lizzie.config.showWinrateLine) {
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
                Font f =
                    new Font(
                        Config.sysDefaultFontName, Font.BOLD, largeEnough ? Utils.zoomOut(16) : 16);
                g.setFont(f);
                String wrString = String.format(Locale.ENGLISH, "%.1f", wr);
                int stringWidth = g.getFontMetrics().stringWidth(wrString);
                int xPos = posx + (movenum * width / numMoves) - stringWidth / 2;
                xPos = Math.max(xPos, origParams[0]);
                xPos = Math.min(xPos, origParams[0] + origParams[2] - stringWidth);
                if (wr > 50) {
                  if (wr > 90) {
                    g.drawString(
                        wrString,
                        xPos,
                        posy + (height - (int) (wr * height / 100)) + 6 * DOT_RADIUS);
                  } else {
                    g.drawString(
                        wrString,
                        xPos,
                        posy + (height - (int) (wr * height / 100)) - 2 * DOT_RADIUS);
                  }
                } else {
                  if (wr < 10) {
                    g.drawString(
                        wrString,
                        xPos,
                        posy + (height - (int) (wr * height / 100)) - 2 * DOT_RADIUS);
                  } else {
                    g.drawString(
                        wrString,
                        xPos,
                        posy + (height - (int) (wr * height / 100)) + 6 * DOT_RADIUS);
                  }
                }
              } else {
                g.setColor(whiteColor);
                g.fillOval(
                    posx + (movenum * width / numMoves) - DOT_RADIUS,
                    posy + height - (int) (wr * height / 100) - DOT_RADIUS,
                    DOT_RADIUS * 2,
                    DOT_RADIUS * 2);
                Font f =
                    new Font(
                        Config.sysDefaultFontName, Font.BOLD, largeEnough ? Utils.zoomOut(16) : 16);
                g.setFont(f);
                g.setColor(Color.WHITE);
                String wrString = String.format(Locale.ENGLISH, "%.1f", wr);
                int stringWidth = g.getFontMetrics().stringWidth(wrString);
                int xPos = posx + (movenum * width / numMoves) - stringWidth / 2;
                xPos = Math.max(xPos, origParams[0]);
                xPos = Math.min(xPos, origParams[0] + origParams[2] - stringWidth);
                if (wr > 50) {
                  if (wr < 90) {
                    g.drawString(
                        wrString,
                        xPos,
                        posy + (height - (int) (wr * height / 100)) - 2 * DOT_RADIUS);
                  } else {
                    g.drawString(
                        wrString,
                        xPos,
                        posy + (height - (int) (wr * height / 100)) + 6 * DOT_RADIUS);
                  }
                } else {
                  if (wr < 10) {
                    g.drawString(
                        wrString,
                        xPos,
                        posy + (height - (int) (wr * height / 100)) - 2 * DOT_RADIUS);
                  } else {
                    g.drawString(
                        wrString,
                        xPos,
                        posy + (height - (int) (wr * height / 100)) + 6 * DOT_RADIUS);
                  }
                }
              }
            }
          }
        }
        node = node.previous().get();
        movenum = movenum - 1;
      }
      if (saveCurMovenum > 1) {
        String wrString = String.format(Locale.ENGLISH, "%.1f", saveCurWr);
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
          Font f =
              new Font(Config.sysDefaultFontName, Font.BOLD, largeEnough ? Utils.zoomOut(16) : 16);
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
          Font f =
              new Font(Config.sysDefaultFontName, Font.BOLD, largeEnough ? Utils.zoomOut(16) : 16);
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
          double score = node.getData().scoreMean;
          int playouts = node.getData().getPlayouts();
          if (playouts > 0) {
            if (wr < 0) {
              wr = 100 - lastWr;
              score = lastScore;
            } else if (!node.getData().blackToPlay) {
              wr = 100 - wr;
              score = -score;
            }
            if (node == curMove) {
              // Draw a vertical line at the current move
              Stroke previousStroke = g.getStroke();
              int x = posx + (movenum * width / numMoves);
              g.setStroke(dashed);
              g.setColor(Color.WHITE);
              g.drawLine(x, posy, x, posy + height);
              // Show move number
              String moveNumString = String.valueOf(node.getData().moveNumber);
              //    int mw = g.getFontMetrics().stringWidth(moveNumString);
              int margin = strokeRadius;
              // int mx = x - posx < width / 2 ? x + margin : x - mw - margin;
              if (!noC) {
                Font f =
                    new Font(
                        Config.sysDefaultFontName, Font.BOLD, largeEnough ? Utils.zoomOut(12) : 12);
                g.setFont(f);
                g.setColor(Color.BLACK);
                int moveNum = node.getData().moveNumber;
                if (wr < 3) {
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

            if (lastOkMove > 0 && lastOkMove - movenum < 25) {
              if (Lizzie.config.showBlunderBar && canDrawBlunderBar) {
                gBlunder.setColor(Lizzie.config.blunderBarColor);
                double lastMoveRate = Math.abs(lastWr - wr);
                double lastMoveScoreRate =
                    Math.min(1.0, Math.abs(lastScore - score) / weightedMaxScoreBlunder);
                int lastHeight = 0;
                lastHeight =
                    Math.max(
                        (int) (lastMoveRate * height / 200),
                        (int) (lastMoveScoreRate * height) / 2);
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
              if (Lizzie.config.showWinrateLine) {
                g.drawLine(
                    posx + (lastOkMove * width / numMoves),
                    posy + height - (int) (lastWr * height / 100),
                    posx + (movenum * width / numMoves),
                    posy + height - (int) (wr * height / 100));
              }
            }
            if (forkNode != null && forkNode == node) {
              canDrawBlunderBar = true;
              g.setStroke(new BasicStroke(Lizzie.config.winrateStrokeWidth));
            }
            lastWr = wr;
            lastScore = score;
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
              lastScore = node.getData().scoreMean;
              if (!node.getData().blackToPlay) {
                lastWr = 100 - lastWr;
                lastScore = -lastScore;
              }
              // g.setStroke(new BasicStroke(Lizzie.config.winrateStrokeWidth));
              topOfVariation = Optional.empty();
              if (node.getData().getPlayouts() == 0) {
                lastNodeOk = false;
              }
            }
            if (Lizzie.config.showWinrateLine) {
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
              if (!noC) {
                String moveNumString = "" + node.getData().moveNumber;
                g.setFont(
                    new Font(
                        Config.sysDefaultFontName,
                        Font.BOLD,
                        largeEnough ? Utils.zoomOut(12) : 12));
                g.setColor(Color.BLACK);
                int moveNum = node.getData().moveNumber;
                if (moveNum < 10)
                  g.drawString(
                      moveNumString,
                      moveNum < numMoves / 2 ? x + 3 : x - 10,
                      posy + height - strokeRadius);
                else if (Lizzie.board.getHistory().getCurrentHistoryNode().getData().moveNumber
                    > 99)
                  g.drawString(
                      moveNumString,
                      moveNum < numMoves / 2 ? x + 3 : x - 22,
                      posy + height - strokeRadius);
                else
                  g.drawString(
                      moveNumString,
                      moveNum < numMoves / 2 ? x + 3 : x - 16,
                      posy + height - strokeRadius);
              }
              g.setStroke(previousStroke);
            }
          }

          if (mouseOverNode != null && node == mouseOverNode) {
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
            Font f =
                new Font(
                    Config.sysDefaultFontName, Font.BOLD, largeEnough ? Utils.zoomOut(12) : 12);
            g.setFont(f);
            g.setColor(Color.BLACK);
            int moveNum = node.getData().moveNumber;
            if (wr < 3) {
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
            if (Lizzie.config.showWinrateLine) {
              if (node.getData().getPlayouts() > 0) {
                mwr = wr;
                mmovenum = movenum;
              }
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
          double score = node.getData().scoreMean;
          int playouts = node.getData().getPlayouts();
          if (node == curMove) {
            //            if (Lizzie.config.dynamicWinrateGraphWidth
            //                && node.getData().moveNumber - 1 > this.numMovesOfPlayed) {
            //              this.numMovesOfPlayed = node.getData().moveNumber - 1;
            //              numMoves = this.numMovesOfPlayed;
            //            }
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
              Font f =
                  new Font(
                      Config.sysDefaultFontName, Font.BOLD, largeEnough ? Utils.zoomOut(12) : 12);
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
              score = lastScore;
            } else if (!node.getData().blackToPlay) {
              wr = 100 - wr;
              score = -score;
            }
            // if (Lizzie.frame.isPlayingAgainstLeelaz
            // && Lizzie.frame.playerIsBlack == !node.getData().blackToPlay) {
            // wr = lastWr;
            // }

            if (lastOkMove > 0 && Math.abs(movenum - lastOkMove) < 25) {
              if (Lizzie.config.showBlunderBar) {
                gBlunder.setColor(Lizzie.config.blunderBarColor);
                double lastMoveRate = Math.abs(lastWr - wr);
                double lastMoveScoreRate =
                    Math.min(1.0, Math.abs(lastScore - score) / weightedMaxScoreBlunder);
                int lastHeight = 0;
                lastHeight =
                    Math.max(
                        (int) (lastMoveRate * height / 200),
                        (int) (lastMoveScoreRate * height) / 2);
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
              if (Lizzie.config.showWinrateLine) {
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
            }
            if (Lizzie.config.showWinrateLine) {
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
                Font f =
                    new Font(
                        Config.sysDefaultFontName, Font.BOLD, largeEnough ? Utils.zoomOut(16) : 16);
                g.setFont(f);

                String wrString = String.format(Locale.ENGLISH, "%.1f", wr);
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
                Font fw =
                    new Font(
                        Config.sysDefaultFontName, Font.BOLD, largeEnough ? Utils.zoomOut(16) : 16);
                g.setFont(fw);
                g.setColor(Color.WHITE);
                g.fillOval(
                    posx + (movenum * width / numMoves) - DOT_RADIUS,
                    posy + height - (int) ((100 - wr) * height / 100) - DOT_RADIUS,
                    DOT_RADIUS * 2,
                    DOT_RADIUS * 2);

                wrString = String.format(Locale.ENGLISH, "%.1f", 100 - wr);
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
            }
            lastWr = wr;
            lastScore = score;
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
      if (EngineManager.isEngineGame || Lizzie.board.isPkBoard) {
        setMaxScoreLead(node);
        if (EngineManager.isEngineGame
                && (Lizzie.engineManager.engineList.get(
                            EngineManager.engineGameInfo.whiteEngineIndex)
                        .isKatago
                    || Lizzie.engineManager.engineList.get(
                            EngineManager.engineGameInfo.whiteEngineIndex)
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
                  g.setStroke(new BasicStroke(Lizzie.config.scoreLeadStrokeWidth));
                  g.drawLine(
                      posx + ((lastOkMove) * width / numMoves),
                      posy
                          + height / 2
                          - (int) (convertScoreLead(lastscoreMean) * height / 2 / maxScoreLead),
                      posx + ((movenum) * width / numMoves),
                      posy
                          + height / 2
                          - (int) (convertScoreLead(curscoreMean) * height / 2 / maxScoreLead));
                  g.setStroke(previousStroke);
                }
              }

              lastscoreMean = curscoreMean;
              lastOkMove = movenum;
            } else {
              if (EngineManager.isEngineGame
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
            g.setStroke(new BasicStroke(Lizzie.config.scoreLeadStrokeWidth));
            g.drawLine(
                posx + ((lastOkMove) * width / numMoves),
                posy
                    + height / 2
                    - (int) (convertScoreLead(lastscoreMean) * height / 2 / maxScoreLead),
                posx + ((movenum) * width / numMoves),
                posy
                    + height / 2
                    - (int) (convertScoreLead(lastscoreMean) * height / 2 / maxScoreLead));
            g.setStroke(previousStroke);
          }
          if (curmovenum > 0) {
            g.setColor(Color.YELLOW);
            Font f =
                new Font(
                    Config.sysDefaultFontName, Font.BOLD, largeEnough ? Utils.zoomOut(14) : 13);
            g.setFont(f);
            double scoreHeight = convertScoreLead(drawcurscoreMean) * height / 2 / maxScoreLead;
            int mScoreHeight = posy + height / 2 - (int) scoreHeight - 3;
            int fontHeigt = g.getFontMetrics().getAscent() - g.getFontMetrics().getDescent();
            int up = origParams[1] + fontHeigt;
            int down = origParams[1] + origParams[3];
            mScoreHeight = Math.max(up, mScoreHeight);
            mScoreHeight = Math.min(down, mScoreHeight);
            String scoreString = String.format(Locale.ENGLISH, "%.1f", drawcurscoreMean);
            int stringWidth = g.getFontMetrics().stringWidth(scoreString);
            int x = posx + (curmovenum * width / numMoves) - stringWidth / 2;
            x = Math.max(x, origParams[0]);
            x = Math.min(x, origParams[0] + origParams[2] - stringWidth);
            g.drawString(scoreString, x, mScoreHeight);
          }
        } else if (EngineManager.isEngineGame
                && (Lizzie.engineManager.engineList.get(
                            EngineManager.engineGameInfo.blackEngineIndex)
                        .isKatago
                    || Lizzie.engineManager.engineList.get(
                            EngineManager.engineGameInfo.blackEngineIndex)
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
            if (EngineManager.isEngineGame) {
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
                  g.setStroke(new BasicStroke(Lizzie.config.scoreLeadStrokeWidth));
                  g.drawLine(
                      posx + ((lastOkMove) * width / numMoves),
                      posy
                          + height / 2
                          - (int) (convertScoreLead(lastscoreMean) * height / 2 / maxScoreLead),
                      posx + ((movenum) * width / numMoves),
                      posy
                          + height / 2
                          - (int) (convertScoreLead(curscoreMean) * height / 2 / maxScoreLead));
                  g.setStroke(previousStroke);
                }
              }

              lastscoreMean = curscoreMean;
              lastOkMove = movenum;
            } else {
              if (EngineManager.isEngineGame
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
            g.setStroke(new BasicStroke(Lizzie.config.scoreLeadStrokeWidth));
            g.drawLine(
                posx + ((lastOkMove) * width / numMoves),
                posy
                    + height / 2
                    - (int) (convertScoreLead(lastscoreMean) * height / 2 / maxScoreLead),
                posx + ((movenum) * width / numMoves),
                posy
                    + height / 2
                    - (int) (convertScoreLead(lastscoreMean) * height / 2 / maxScoreLead));
            g.setStroke(previousStroke);
          }
          if (curmovenum > 0) {
            g.setColor(Color.YELLOW);
            Font f =
                new Font(
                    Config.sysDefaultFontName, Font.BOLD, largeEnough ? Utils.zoomOut(14) : 13);
            g.setFont(f);
            double scoreHeight = convertScoreLead(drawcurscoreMean) * height / 2 / maxScoreLead;
            int mScoreHeight = posy + height / 2 - (int) scoreHeight - 3;
            int fontHeigt = g.getFontMetrics().getAscent() - g.getFontMetrics().getDescent();
            int up = origParams[1] + fontHeigt;
            int down = origParams[1] + origParams[3];
            mScoreHeight = Math.max(up, mScoreHeight);
            mScoreHeight = Math.min(down, mScoreHeight);
            String scoreString = String.format(Locale.ENGLISH, "%.1f", drawcurscoreMean);
            int stringWidth = g.getFontMetrics().stringWidth(scoreString);
            int x = posx + (curmovenum * width / numMoves) - stringWidth / 2;
            x = Math.max(x, origParams[0]);
            x = Math.min(x, origParams[0] + origParams[2] - stringWidth);
            g.drawString(scoreString, x, mScoreHeight);
          }
        }
      } else if (Lizzie.leelaz.isSai || Lizzie.leelaz.isKatago || Lizzie.board.isKataBoard) {
        setMaxScoreLead(node);
        double lastscoreMean = -500;
        while (node.previous().isPresent()) {
          if (node.getData().getPlayouts() > 0) {

            double curscoreMean = node.getData().scoreMean;

            if (!node.getData().blackToPlay) {
              curscoreMean = -curscoreMean;
            }
            if (Lizzie.config.showKataGoScoreLeadWithKomi)
              curscoreMean = curscoreMean + Lizzie.board.getHistory().getGameInfo().getKomi();
            //            if (Math.abs(curscoreMean) > maxcoreMean)
            //            	maxcoreMean = Math.abs(curscoreMean);

            if (node == curMove
                || (curMove.previous().isPresent()
                    && node == curMove.previous().get()
                    && curMove.getData().getPlayouts() <= 0)) {
              curScoreMoveNum = movenum;
              drawCurSoreMean = curscoreMean;
            }
            if (mouseOverNode != null && node == mouseOverNode) {
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
                g.setStroke(new BasicStroke(Lizzie.config.scoreLeadStrokeWidth));
                g.drawLine(
                    posx + (lastOkMove * width / numMoves),
                    posy
                        + height / 2
                        - (int) (convertScoreLead(lastscoreMean) * height / 2 / maxScoreLead),
                    posx + (movenum * width / numMoves),
                    posy
                        + height / 2
                        - (int) (convertScoreLead(curscoreMean) * height / 2 / maxScoreLead));
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
      g.setColor(Color.RED);
      g.fillOval(
          posx + (mmovenum * width / numMoves) - DOT_RADIUS,
          posy + height - (int) (mwr * height / 100) - DOT_RADIUS,
          DOT_RADIUS * 2,
          DOT_RADIUS * 2);
      g.setColor(Color.BLACK);
      Font f = new Font(Config.sysDefaultFontName, Font.BOLD, largeEnough ? Utils.zoomOut(16) : 16);
      g.setFont(f);
      oriMWrHeight = posy + (height - (int) (mwr * height / 100));
      mwrHeight = oriMWrHeight + (mwr < 10 ? -5 : (mwr > 90 ? 6 : -2) * DOT_RADIUS);
      mWinFontHeight = g.getFontMetrics().getAscent() - g.getFontMetrics().getDescent();
      if (mwrHeight > origParams[1] + origParams[3]) {
        mwrHeight = origParams[1] + origParams[3] - 2;
      }

      String mwrString = String.format(Locale.ENGLISH, "%.1f", mwr);
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
      Font f = new Font(Config.sysDefaultFontName, Font.BOLD, largeEnough ? Utils.zoomOut(14) : 14);
      g.setFont(f);
      double scoreHeight = convertScoreLead(drawmSoreMean) * height / 2 / maxScoreLead;
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
      String scoreString = String.format(Locale.ENGLISH, "%.1f", drawmSoreMean);
      int stringWidth = g.getFontMetrics().stringWidth(scoreString);
      int x = posx + (mScoreMoveNum * width / numMoves) - stringWidth / 2;
      x = Math.max(x, origParams[0]);
      x = Math.min(x, origParams[0] + origParams[2] - stringWidth);
      g.drawString(scoreString, x, mScoreHeight);
    }

    int cwrHeight = -1;
    int winFontHeight = -1;
    int oriWrHeight = -1;
    noC = false;
    if (cwr >= 0) {
      Font f = new Font(Config.sysDefaultFontName, Font.BOLD, largeEnough ? Utils.zoomOut(16) : 16);
      g.setFont(f);
      g.setColor(Color.BLACK);
      oriWrHeight = posy + (height - (int) (cwr * height / 100));
      cwrHeight = oriWrHeight + (cwr < 10 ? -5 : (cwr > 90 ? 6 : -2) * DOT_RADIUS);
      winFontHeight = g.getFontMetrics().getAscent() - g.getFontMetrics().getDescent();
      if (cwrHeight > origParams[1] + origParams[3]) {
        cwrHeight = origParams[1] + origParams[3] - 2;
      }
      String wrString = String.format(Locale.ENGLISH, "%.1f", cwr);
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
      Font f = new Font(Config.sysDefaultFontName, Font.BOLD, largeEnough ? Utils.zoomOut(14) : 14);
      g.setFont(f);
      double scoreHeight = convertScoreLead(drawCurSoreMean) * height / 2 / maxScoreLead;
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
      String scoreString = String.format(Locale.ENGLISH, "%.1f", drawCurSoreMean);
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

  private double convertScoreLead(double coreMean) {
    if (coreMean > maxScoreLead) return maxScoreLead;
    if (coreMean < 0 && Math.abs(coreMean) > maxScoreLead) return -maxScoreLead;
    return coreMean;
  }

  private void setMaxScoreLead(BoardHistoryNode lastMove) {
    resetMaxScoreLead();
    while (lastMove.previous().isPresent()) {
      Double scoreMean = Math.abs(lastMove.getData().scoreMean);
      if (scoreMean > maxScoreLead) maxScoreLead = scoreMean;
      lastMove = lastMove.previous().get();
    }
    Double scoreMean = Math.abs(lastMove.getData().scoreMean);
    if (scoreMean > maxScoreLead) maxScoreLead = scoreMean;
  }

  public void setMouseOverNode(BoardHistoryNode node) {
    mouseOverNode = node;
  }

  public void clearMouseOverNode() {
    mouseOverNode = null;
  }

  public void clearParames() {
    origParams = new int[] {0, 0, 0, 0};
    params = new int[] {0, 0, 0, 0, 0};
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

  public void resetMaxScoreLead() {
    maxScoreLead = Lizzie.config.initialMaxScoreLead;
  }
}
