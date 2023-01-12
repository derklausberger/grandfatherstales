package objectClasses;

import GUI.AnimationFrame;
import GUI.AudioManager;
import GUI.GamePanel;
import objectClasses.Abstract.Entity;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.geom.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;


public class Enemy extends Entity {

    private int cooldown = 0;
    private int duration = 35;

    public Enemy(int positionX, int positionY, int movementSpeed, int healthPoints) {

        super(positionX, positionY, movementSpeed, healthPoints);

        super.loadAnimationFrames("skeletonWarrior");
    }


    public void reduceCooldown() {
        if (cooldown < 0) {
            cooldown = 0;
        } else {
            cooldown--;
        }
    }

    public void detectPlayer(Game game) {
        Ellipse2D detectPlayerCircle = getCircleByCenter(new Point2D.Double(getPositionX() + GamePanel.NEW_TILE_SIZE, getPositionY() + GamePanel.NEW_TILE_SIZE), 100);

        if (detectPlayerCircle.contains(game.getPlayer().getPositionX(), game.getPlayer().getPositionY()) ||
                detectPlayerCircle.contains(game.getPlayer().getPositionX() + GamePanel.NEW_TILE_SIZE, game.getPlayer().getPositionY()) ||
                detectPlayerCircle.contains(game.getPlayer().getPositionX(), game.getPlayer().getPositionY() + GamePanel.NEW_TILE_SIZE) ||
                detectPlayerCircle.contains(game.getPlayer().getPositionX() + GamePanel.NEW_TILE_SIZE, game.getPlayer().getPositionY() + GamePanel.NEW_TILE_SIZE)) {
            followPlayer(game);
        }
    }

    public void followPlayer(Game game) {
        Point2D playerMiddle = new Point2D.Double(game.getPlayer().getPositionX() + Math.floorDiv(GamePanel.NEW_TILE_SIZE, 2), game.getPlayer().getPositionY() + Math.floorDiv(GamePanel.NEW_TILE_SIZE, 2));
        Point2D enemyMiddle = new Point2D.Double(getPositionX() - Math.floorDiv(GamePanel.NEW_TILE_SIZE, 2), getPositionY() + Math.floorDiv(GamePanel.NEW_TILE_SIZE, 2));
        double distance = playerMiddle.distance(enemyMiddle);

        if(distance <= 100) {
            boolean up = false, down = false, left = false, right = false;
            distance = playerMiddle.distance(enemyMiddle);

            ArrayList<String> toGO = new ArrayList<String>();

            toGO.clear();

            if (distance <= 30) {
                attackPlayer(game);
            }

            if (distance <= 100 && distance > 30) {
                if (
                        playerMiddle.getX() - enemyMiddle.getX() <= 0 &&
                                playerMiddle.getY() - enemyMiddle.getY() <= 0
                ) {
                    toGO.add("up");
                    toGO.add("left");
                } else if (
                        playerMiddle.getX() - enemyMiddle.getX() <= 0 &&
                                playerMiddle.getY() - enemyMiddle.getY() >= 0
                ) {
                    toGO.add("down");
                    toGO.add("left");
                } else if (
                        playerMiddle.getX() - enemyMiddle.getX() >= 0 &&
                                playerMiddle.getY() - enemyMiddle.getY() <= 0
                ) {
                    toGO.add("up");
                    toGO.add("right");
                } else if (
                        playerMiddle.getX() - enemyMiddle.getX() >= 0 &&
                                playerMiddle.getY() - enemyMiddle.getY() >= 0
                ) {
                    toGO.add("down");
                    toGO.add("right");
                }


                for (String string : toGO
                ) {
                    switch (string) {
                        case "up" -> setPositionY(getPositionY() - 1);
                        case "down" -> setPositionY(getPositionY() + 1);
                        case "left" -> setPositionX(getPositionX() - 1);
                        case "right" -> setPositionX(getPositionX() + 1);
                    }
                }
            }
        }
    }

    public void attackPlayer(Game game) {

        Point2D playerMiddle = new Point2D.Double(game.getPlayer().getPositionX() + Math.floorDiv(GamePanel.NEW_TILE_SIZE, 2), game.getPlayer().getPositionY() + Math.floorDiv(GamePanel.NEW_TILE_SIZE, 2));
        Point2D enemyMiddle = new Point2D.Double(getPositionX() - Math.floorDiv(GamePanel.NEW_TILE_SIZE, 2), getPositionY() + Math.floorDiv(GamePanel.NEW_TILE_SIZE, 2));

        int startAngle = 0;
        int arcAngle = 0;


        if (cooldown == 0) {
            if (
                    playerMiddle.getX() - enemyMiddle.getX() <= 0 &&
                            playerMiddle.getY() - enemyMiddle.getY() <= 0
            ) {
                startAngle = 45;
                arcAngle = 90;
                System.out.println("enemy up attack");
            } else if (
                    playerMiddle.getX() - enemyMiddle.getX() <= 0 &&
                            playerMiddle.getY() - enemyMiddle.getY() >= 0
            ) {
                startAngle = 135;
                arcAngle = 90;
                System.out.println("enemy left attack");
            } else if (
                    playerMiddle.getX() - enemyMiddle.getX() >= 0 &&
                            playerMiddle.getY() - enemyMiddle.getY() <= 0
            ) {
                startAngle = 315;
                arcAngle = 90;
                System.out.println("enemy right attack");
            } else if (
                    playerMiddle.getX() - enemyMiddle.getX() >= 0 &&
                            playerMiddle.getY() - enemyMiddle.getY() >= 0
            ) {
                startAngle = 225;
                arcAngle = 90;
                System.out.println("enemy down attack");
            }

            cooldown = duration;

            Arc2D arc2D = new Arc2D.Double();
            arc2D.setArcByCenter(
                    enemyMiddle.getX(),
                    enemyMiddle.getY(),
                    100,
                    startAngle, arcAngle,
                    Arc2D.PIE);

            if (arc2D.contains(game.getPlayer().getPositionX(), game.getPlayer().getPositionY()) ||
                    arc2D.contains(game.getPlayer().getPositionX() + GamePanel.NEW_TILE_SIZE, game.getPlayer().getPositionY()) ||
                    arc2D.contains(game.getPlayer().getPositionX(), game.getPlayer().getPositionY() + GamePanel.NEW_TILE_SIZE) ||
                    arc2D.contains(game.getPlayer().getPositionX() + GamePanel.NEW_TILE_SIZE, game.getPlayer().getPositionY() + GamePanel.NEW_TILE_SIZE)) {

                if(game.getPlayer().getArmor().getBlockAmount() >= 10) {
                    System.out.println("was hit but no damage received");
                } else {
                    game.getPlayer().setCurrentHealthPoints(game.getPlayer().getCurrentHealthPoints() + game.getPlayer().getArmor().getBlockAmount() - 10);
                }

                System.out.println("Player was HIT");
                if (game.getPlayer().getCurrentHealthPoints() <= 0) {
                    if (game.getPlayer().getLife() >= 1) {
                        game.getPlayer().setLife(game.getPlayer().getLife() - 1);
                        game.getPlayer().setCurrentHealthPoints(game.getPlayer().getMaxHealthPoints());
                    } else {
                        GamePanel.playerArrayList.remove(game.getPlayer());
                    }
                }
            }
        }
    }


    @Override
    public void draw(Graphics2D graph2D, Game game, GamePanel gamePanel) {

        int x = (int) ((GamePanel.WINDOW_WIDTH) / 2),
                y = (int) ((GamePanel.WINDOW_HEIGHT) / 2);

        // The current frame
        AnimationFrame frame = getEntityFrames(getCurrentAnimationType())[getCurrentFrame()];

        // Draws the character
        graph2D.drawImage(
                frame.getImage(),
                x + frame.getXOffset() - game.getPlayer().getPositionX() + this.getPositionX(),
                y + frame.getYOffset() - game.getPlayer().getPositionY() + this.getPositionY(),
                frame.getWidth(), frame.getHeight(), gamePanel
        );

        if (this.getCurrentHealthPoints() < this.getMaxHealthPoints()) {
            Shape healthBarOutside = new Rectangle2D.Double(
                    (int) (GamePanel.WINDOW_WIDTH / 2) - game.getPlayer().getPositionX() + this.getPositionX() - 1,
                    (int) (GamePanel.WINDOW_HEIGHT / 2) - game.getPlayer().getPositionY() + this.getPositionY() - 11,
                    (GamePanel.NEW_TILE_SIZE + 2) * 0.6,
                    3);


            graph2D.setPaint(Color.black);
            graph2D.draw(healthBarOutside);

            Shape healthBarInside = new Rectangle2D.Double(
                    (int) (GamePanel.WINDOW_WIDTH / 2) - game.getPlayer().getPositionX() + this.getPositionX(),
                    (int) (GamePanel.WINDOW_HEIGHT / 2) - game.getPlayer().getPositionY() + this.getPositionY() - 10,
                    ((double) GamePanel.NEW_TILE_SIZE / (double) this.getMaxHealthPoints() * (double) this.getCurrentHealthPoints()) * 0.6,
                    3);


            graph2D.setPaint(Color.RED);
            graph2D.fill(healthBarInside);
        }


        Shape offsetCollision = new Rectangle2D.Double(
                (int) (GamePanel.WINDOW_WIDTH / 2) - game.getPlayer().getPositionX() + this.getPositionX(),
                (int) (GamePanel.WINDOW_HEIGHT / 2) - game.getPlayer().getPositionY() + this.getPositionY(),
                GamePanel.NEW_TILE_SIZE,
                GamePanel.NEW_TILE_SIZE);

        graph2D.setColor(Color.ORANGE);
        graph2D.draw(offsetCollision);

        Rectangle2D rec = new Rectangle(game.getPlayer().getPositionX(), game.getPlayer().getPositionY(), GamePanel.NEW_TILE_SIZE, GamePanel.NEW_TILE_SIZE);
        graph2D.setPaint(Color.CYAN);
        graph2D.draw(rec);

        Ellipse2D detectPlayerCircle2 = getCircleByCenter(new Point2D.Double(getPositionX() + GamePanel.NEW_TILE_SIZE, getPositionY() + GamePanel.NEW_TILE_SIZE), 70);
        graph2D.setPaint(Color.ORANGE);
        graph2D.draw(detectPlayerCircle2);

        Ellipse2D detectPlayerCircle = getCircleByCenter(new Point2D.Double(((GamePanel.WINDOW_WIDTH + GamePanel.NEW_TILE_SIZE) / 2) - game.getPlayer().getPositionX() + this.getPositionX(), ((GamePanel.WINDOW_HEIGHT + GamePanel.NEW_TILE_SIZE) / 2) - game.getPlayer().getPositionY() + this.getPositionY()), 70);
        graph2D.setPaint(Color.ORANGE);
        graph2D.draw(detectPlayerCircle);
    }


    private static Ellipse2D getCircleByCenter(Point2D center, double radius) {
        Ellipse2D.Double myCircle = new Ellipse2D.Double(center.getX() - radius, center.getY() - radius, 2 * radius, 2 * radius);
        return myCircle;
    }
}


