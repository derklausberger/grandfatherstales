package objectClasses;

import GUI.AnimationFrame;
import GUI.AudioManager;
import GUI.GamePanel;
import objectClasses.Abstract.Entity;
import objectClasses.Enum.EntityTypes;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.*;
import java.util.ArrayList;


public class Enemy extends Entity {

    private int walkingAnimationFrame, attackAnimationFrame;
    private final Timer animationTimer;

    // isAttacking -> indicates if the attack animation is executed
    // attackHitBoxDrawn -> is set to true as soon as the third frame
    // of the attack animation is reached and the hit box is drawn
    private boolean isAttacking, attackHitBoxDrawn;
    private int viewDirection;
    private ArrayList<Projectile> projectiles;

    public Enemy(int positionX, int positionY, int movementSpeed, int healthPoints, EntityTypes entityTypes) {
        super(positionX, positionY, movementSpeed, healthPoints, entityTypes);

        // Enemies don't need an armor or weapon object, only values
        super.setBlockAmount(5);
        super.setAttackDamage(5);
        super.setAttackDelay(10);

        // Delay between frame updates in milliseconds
        final int ANIMATION_DELAY = 110;

        animationTimer = new Timer(ANIMATION_DELAY, new AnimationHandler());
        walkingAnimationFrame = 0;
        attackAnimationFrame = 0;
        viewDirection = 0;
        isAttacking = false;
        projectiles = new ArrayList<>();
    }

    public void detectPlayer(Game game) {

        Ellipse2D sightCircle = getCircleByCenter(new Point2D.Double(
                        getPositionX() + Math.floorDiv(GamePanel.NEW_TILE_SIZE, 2),
                        getPositionY() + Math.floorDiv(GamePanel.NEW_TILE_SIZE, 2)),
                200);

        if (!isAttacking) {
            setCurrentAnimationType("walking");
            if (getCooldown() == 0) {
                if (
                        sightCircle.contains(game.getPlayer().getPositionX(), game.getPlayer().getPositionY()) ||
                                sightCircle.contains(game.getPlayer().getPositionX() + GamePanel.NEW_TILE_SIZE, game.getPlayer().getPositionY()) ||
                                sightCircle.contains(game.getPlayer().getPositionX(), game.getPlayer().getPositionY() + GamePanel.NEW_TILE_SIZE) ||
                                sightCircle.contains(game.getPlayer().getPositionX() + GamePanel.NEW_TILE_SIZE, game.getPlayer().getPositionY() + GamePanel.NEW_TILE_SIZE)) {

                    followPlayer(game);
                } else {
                    if (animationTimer.isRunning()) {
                        animationTimer.stop();
                    }
                    walkingAnimationFrame = 0;
                    setCurrentFrame(viewDirection);
                }
            } else {
                walkingAnimationFrame = 0;
                setCurrentFrame(viewDirection);
            }
        } else {
            attackPlayer(game);
        }
    }

    public void followPlayer(Game game) {

        animationTimer.start();

        boolean up = false, down = false, left = false, right = false;

        Point2D playerMiddle = new Point2D.Double(game.getPlayer().getPositionX() + Math.floorDiv(GamePanel.NEW_TILE_SIZE, 2), game.getPlayer().getPositionY() + Math.floorDiv(GamePanel.NEW_TILE_SIZE, 2));
        Point2D enemyMiddle = new Point2D.Double(getPositionX() + Math.floorDiv(GamePanel.NEW_TILE_SIZE, 2), getPositionY() + Math.floorDiv(GamePanel.NEW_TILE_SIZE, 2));

        double distance = enemyMiddle.distance(playerMiddle);
        double angle;
        double theta;

        theta = Math.atan2(playerMiddle.getY() - enemyMiddle.getY(), playerMiddle.getX() - enemyMiddle.getX());

        theta += Math.PI / 2.0;
        angle = Math.toDegrees(theta);

        if (angle < 0) {
            angle += 360;
        }

        if (angle >= 315 && angle < 359 || angle >= 0 && angle < 45) { // -> von 315 bis 45
            up = true;
            viewDirection = 27;
            setCurrentFrame(walkingAnimationFrame + viewDirection);

        } else if (angle >= 45 && angle < 135) { // -> von 45 bis 135
            right = true;
            viewDirection = 18;
            setCurrentFrame(walkingAnimationFrame + viewDirection);
        } else if (angle >= 135 && angle < 225) { // -> von 135 bis 225
            down = true;
            viewDirection = 0;
            setCurrentFrame(walkingAnimationFrame);
        } else if (angle >= 225 && angle < 315) { // -> von 225 bis 315
            left = true;
            viewDirection = 9;
            setCurrentFrame(walkingAnimationFrame + viewDirection);
        }

        if (distance <= 80) {
            isAttacking = true;
            //attackPlayer(game);
        } else {
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
    }

    public void attackPlayer(Game game) {

        setCurrentAnimationType("attacking");

        Point2D playerMiddle = new Point2D.Double(game.getPlayer().getPositionX() + Math.floorDiv(GamePanel.NEW_TILE_SIZE, 2), game.getPlayer().getPositionY() + Math.floorDiv(GamePanel.NEW_TILE_SIZE, 2));
        Point2D enemyMiddle = new Point2D.Double(getPositionX() + Math.floorDiv(GamePanel.NEW_TILE_SIZE, 2), getPositionY() + Math.floorDiv(GamePanel.NEW_TILE_SIZE, 2));

        int startAngle = 0;
        int arcAngle = 0;

        switch (viewDirection) {
            case 27 -> {
                startAngle = 45;
                arcAngle = 90;
                //System.out.println("enemy up attack");
                setCurrentFrame(attackAnimationFrame + 18);
            }
            case 9 -> {
                startAngle = 135;
                arcAngle = 90;
                //System.out.println("enemy left attack");
                setCurrentFrame(attackAnimationFrame + 6);
            }
            case 18 -> {
                startAngle = 315;
                arcAngle = 90;
                //System.out.println("enemy right attack");
                setCurrentFrame(attackAnimationFrame + 12);
            }
            case 0 -> {
                startAngle = 225;
                arcAngle = 90;
                //System.out.println("enemy down attack");
                setCurrentFrame(attackAnimationFrame);
            }
        }

        // Draws the hit box when the sword was swung (fourth image seems fine)
        // Therefore, the character has the possibility to walk out of an attack
        // and dodge it before he was actually (visually) hit
        if (attackAnimationFrame == 4 && !attackHitBoxDrawn) {

            attackHitBoxDrawn = true;
            walkingAnimationFrame = 0;

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

                if (!game.getPlayer().isInvincible()) {
                    if (game.getPlayer().getBlockAmount() >= getAttackDamage()) {

                    } else {
                        AudioManager.play("S - d");
                        game.getPlayer().setCurrentHealthPoints(game.getPlayer().getCurrentHealthPoints() + game.getPlayer().getBlockAmount() - getAttackDamage());
                    }

                    if (game.getPlayer().getCurrentHealthPoints() <= 0) {
                        if (game.getPlayer().getLife() >= 1) {
                            game.getPlayer().setLife(game.getPlayer().getLife() - 1);
                            game.getPlayer().setCurrentHealthPoints(game.getPlayer().getMaxHealthPoints());
                        } else {
                            //GamePanel.playerArrayList.remove(game.getPlayer());
                        }
                    }
                } else {
                    System.out.println("WAS INVINCIBLE");
                    game.getPlayer().triggerInvincibility();
                }
            }
        }
    }


    // das gehört angepasst, wenn pfeil bild rdy is (vllt auch noch aufteiln in 2 funktionen: moven und hitten??)
    public void moveProjectiles(Game game) {
        Projectile p;
        for (int i = projectiles.size() - 1; i >= 0; i--) {
            p = projectiles.get(i);
            if (p.outOfScreen()) {
                projectiles.remove(i);
            } else {
                p.move();
                if (p.getX() < game.getPlayer().getPositionX() + 30 && p.getX() > game.getPlayer().getPositionX()
                        && p.getY() < game.getPlayer().getPositionY() + 50 && p.getY() > game.getPlayer().getPositionY()) {
                    //attackPlayer(game);
                    projectiles.remove(i);
                }
            }
        }
    }


    @Override
    public void draw(Graphics2D graph2D, Game game, GamePanel gamePanel) {

        int x = (int) ((GamePanel.WINDOW_WIDTH) / 2);
        int y = (int) ((GamePanel.WINDOW_HEIGHT) / 2);

        // The current frame
        AnimationFrame frame = getEntityFrames(getCurrentAnimationType())[getCurrentFrame()];

        // Draws the character
        graph2D.drawImage(
                frame.getImage(),
                x + frame.getXOffset() - game.getPlayer().getPositionX() + this.getPositionX(),
                y + frame.getYOffset() - game.getPlayer().getPositionY() + this.getPositionY(),
                frame.getWidth(), frame.getHeight(), gamePanel
        );

        for(Projectile p : projectiles) {
            p.draw(graph2D, game, gamePanel);
        }

        if (this.getCurrentHealthPoints() < this.getMaxHealthPoints()) {
            Shape healthBarOutside = new Rectangle2D.Double(
                    x - game.getPlayer().getPositionX() + this.getPositionX() - 1,
                    y - game.getPlayer().getPositionY() + this.getPositionY() - 11,
                    (GamePanel.NEW_TILE_SIZE + 2) * 0.6,
                    3);

            Shape healthBarInside = new Rectangle2D.Double(
                    x - game.getPlayer().getPositionX() + this.getPositionX(),
                    y - game.getPlayer().getPositionY() + this.getPositionY() - 10,
                    ((double) GamePanel.NEW_TILE_SIZE / (double) this.getMaxHealthPoints() * (double) this.getCurrentHealthPoints()) * 0.6,
                    3);

            graph2D.setPaint(Color.black);
            graph2D.draw(healthBarOutside);
            graph2D.setPaint(Color.RED);
            graph2D.fill(healthBarInside);
        }
    }


    private static Ellipse2D getCircleByCenter(Point2D center, double radius) {
        Ellipse2D.Double myCircle = new Ellipse2D.Double(center.getX() - radius, center.getY() - radius, 2 * radius, 2 * radius);
        return myCircle;
    }

    private class AnimationHandler implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {

            if (!isAttacking) {
                walkingAnimationFrame++;

                if (walkingAnimationFrame >= 9) {
                    walkingAnimationFrame = 0;
                }
                if (getCooldown() > 0) {
                    reduceCooldown();
                }
            } else {
                attackAnimationFrame++;
                if (attackAnimationFrame >= 6) {
                    attackAnimationFrame = 0;
                    isAttacking = false;
                    attackHitBoxDrawn = false;
                    setCooldown(getAttackDelay());
                }
            }
        }
    }
}


