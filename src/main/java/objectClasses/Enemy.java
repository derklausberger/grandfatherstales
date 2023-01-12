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

    private int radiusOfPursuit;

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
        Ellipse2D detectPlayerCircle =
                getCircleByCenter(new Point2D.Double(
                                getPositionX() + Math.floorDiv(GamePanel.NEW_TILE_SIZE, 2),
                                getPositionY() + Math.floorDiv(GamePanel.NEW_TILE_SIZE, 2)),
                        100);

        if (
                detectPlayerCircle.contains(game.getPlayer().getPositionX(), game.getPlayer().getPositionY()) ||
                        detectPlayerCircle.contains(game.getPlayer().getPositionX() + GamePanel.NEW_TILE_SIZE, game.getPlayer().getPositionY()) ||
                        detectPlayerCircle.contains(game.getPlayer().getPositionX(), game.getPlayer().getPositionY() + GamePanel.NEW_TILE_SIZE) ||
                        detectPlayerCircle.contains(game.getPlayer().getPositionX() + GamePanel.NEW_TILE_SIZE, game.getPlayer().getPositionY() + GamePanel.NEW_TILE_SIZE)) {
            followPlayer(game);
        }
    }

    public void followPlayer(Game game) {

        boolean up = false, down = false, left = false, right = false;

        Point2D playerMiddle = new Point2D.Double(game.getPlayer().getPositionX() + Math.floorDiv(GamePanel.NEW_TILE_SIZE, 2), game.getPlayer().getPositionY() + Math.floorDiv(GamePanel.NEW_TILE_SIZE, 2));
        Point2D enemyMiddle = new Point2D.Double(getPositionX() + Math.floorDiv(GamePanel.NEW_TILE_SIZE, 2), getPositionY() + Math.floorDiv(GamePanel.NEW_TILE_SIZE, 2));

        double angle = 0;
        double theta = Math.atan2(playerMiddle.getY() - enemyMiddle.getY(), playerMiddle.getX() - enemyMiddle.getX());
        double distance = enemyMiddle.distance(playerMiddle);
        String bo = "";

        theta += Math.PI / 2.0;
        angle = Math.toDegrees(theta);

        if (angle < 0) {
            angle += 360;
        }

        if (angle >= 315 && angle < 359 || angle >= 0 && angle < 45) { // -> von 315 bis 45
            up = true;
            bo = "up";
        } else if (angle >= 45 && angle < 135) { // -> von 45 bis 135
            right = true;
            bo = "right";
        } else if (angle >= 135 && angle < 225) { // -> von 135 bis 225
            down = true;
            bo = "down";
        } else if (angle >= 225 && angle < 315) { // -> von 225 bis 315
            left = true;
            bo = "left";
        }

        if (distance <= 80) {
            attackPlayer(game, bo);
        }

        if (up) {
            if (game.getCurrentLevel().isSolid(getPositionX() + Math.floorDiv(GamePanel.NEW_TILE_SIZE * 3, 10), getPositionY() - getMovementSpeed()) &&
                    game.getCurrentLevel().isSolid(getPositionX() - Math.floorDiv(GamePanel.NEW_TILE_SIZE * 3, 10), getPositionY() - getMovementSpeed())) {
                setPositionY(getPositionY() - getMovementSpeed());
            }
        } else if (down) {
            if (game.getCurrentLevel().isSolid(getPositionX() - Math.floorDiv(GamePanel.NEW_TILE_SIZE * 3, 10), getPositionY() + getMovementSpeed() + Math.floorDiv(GamePanel.NEW_TILE_SIZE * 4, 10)) &&
                    game.getCurrentLevel().isSolid(getPositionX() + Math.floorDiv(GamePanel.NEW_TILE_SIZE * 3, 10), getPositionY() + getMovementSpeed() + Math.floorDiv(GamePanel.NEW_TILE_SIZE * 4, 10))) {
                setPositionY(getPositionY() + getMovementSpeed());
            }
        } else if (left) {
            if (game.getCurrentLevel().isSolid(getPositionX() - getMovementSpeed() - Math.floorDiv(GamePanel.NEW_TILE_SIZE * 3, 10), getPositionY() + Math.floorDiv(GamePanel.NEW_TILE_SIZE * 4, 10)) &&
                    game.getCurrentLevel().isSolid(getPositionX() - getMovementSpeed() - Math.floorDiv(GamePanel.NEW_TILE_SIZE * 3, 10), getPositionY())) {
                setPositionX(getPositionX() - getMovementSpeed());
            }
        } else if (right) {
            if (game.getCurrentLevel().isSolid(getPositionX() + getMovementSpeed() + Math.floorDiv(GamePanel.NEW_TILE_SIZE * 3, 10), getPositionY()) &&
                    game.getCurrentLevel().isSolid(getPositionX() + getMovementSpeed() + Math.floorDiv(GamePanel.NEW_TILE_SIZE * 3, 10), getPositionY() + Math.floorDiv(GamePanel.NEW_TILE_SIZE * 4, 10))) {
                setPositionX(getPositionX() + getMovementSpeed());
            }
        }
    }

    public void attackPlayer(Game game, String bo) {

        Point2D playerMiddle = new Point2D.Double(game.getPlayer().getPositionX() + Math.floorDiv(GamePanel.NEW_TILE_SIZE, 2), game.getPlayer().getPositionY() + Math.floorDiv(GamePanel.NEW_TILE_SIZE, 2));
        Point2D enemyMiddle = new Point2D.Double(getPositionX() + Math.floorDiv(GamePanel.NEW_TILE_SIZE, 2), getPositionY() + Math.floorDiv(GamePanel.NEW_TILE_SIZE, 2));

        int startAngle = 0;
        int arcAngle = 0;


        if (cooldown == 0) {
            switch (bo) {
                case "up" -> {
                    startAngle = 45;
                    arcAngle = 90;
                    System.out.println("enemy up attack");
                }
                case "left" -> {
                    startAngle = 135;
                    arcAngle = 90;
                    System.out.println("enemy left attack");
                }
                case "right" -> {
                    startAngle = 315;
                    arcAngle = 90;
                    System.out.println("enemy right attack");
                }
                case "down" -> {
                    startAngle = 225;
                    arcAngle = 90;
                    System.out.println("enemy down attack");
                }
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

                if (game.getPlayer().getArmor().getBlockAmount() >= 10) {
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


