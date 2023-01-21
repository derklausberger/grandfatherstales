package objectClasses;

import GUI.AnimationFrame;
import GUI.AudioManager;
import GUI.GamePanel;
import objectClasses.Abstract.Entity;
import objectClasses.Enum.EntityType;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.*;
import java.util.ArrayList;


public class Enemy extends Entity {

    Double[] factor = {1.0, 0.98, 0.95, 0.88, 0.70, 0.56, 0.47, 0.40, 0.35, 0.30, 0.27, 0.25, 0.21, 0.20, 0.19};

    @Override
    public void update() {
        if (this.getKnockBackDuration() <= 1) {
            this.setKnockBackDuration(0);
            this.setKnockBack(false);

            this.setMomentum(0);
        } else if (this.isKnockBack()) {
            if (GamePanel.player.getCurrentFrame() >= 19 && GamePanel.player.getCurrentFrame() < 25+1) { // up
                this.setPositionY(this.getPositionY() - (int) (this.getMomentum() * factor[15 - this.getKnockBackDuration()]));
            } else if (GamePanel.player.getCurrentFrame() >= 13 && GamePanel.player.getCurrentFrame() < 18+1) { // right
                this.setPositionX(this.getPositionX() + (int) (this.getMomentum() * factor[15 - this.getKnockBackDuration()]));
            } else if (GamePanel.player.getCurrentFrame() >= 1 && GamePanel.player.getCurrentFrame() < 6+1) { // down
                this.setPositionY(this.getPositionY() + (int) (this.getMomentum() * factor[15 - this.getKnockBackDuration()]));
            } else if (GamePanel.player.getCurrentFrame() >= 7 && GamePanel.player.getCurrentFrame() < 12+1) { // left
                this.setPositionX(this.getPositionX() - (int) (this.getMomentum() * factor[15 - this.getKnockBackDuration()]));
            }
            this.setKnockBackDuration(this.getKnockBackDuration() - 1);
        }
    }

    @Override
    public void hit() {
        this.setKnockBackDuration(15);
        this.setMomentum(this.getKnockBackDuration() * this.getMovementSpeed());
        this.setKnockBack(true);
    }


    private int walkingAnimationFrame, attackAnimationFrame;
    private final Timer animationTimer;

    // isAttacking -> indicates if the attack animation is executed
    // attackHitBoxDrawn -> is set to true as soon as the third frame
    // of the attack animation is reached and the hit box is drawn
    // attackCollisionFrame -> indicates, when exactly (frame)
    // the hitbox for an attack should be drawn
    private boolean isAttacking, attackHitBoxDrawn;
    private int viewDirection, sightRadius, attackDistance;
    private int attackCollisionFrame = 4;

    private ArrayList<Projectile> projectiles;

    public Enemy(int positionX, int positionY, int movementSpeed, int healthPoints, int sightRadius, int attackDistance, EntityType entityType) {
        super(positionX, positionY, movementSpeed, healthPoints, entityType);

        // Enemies don't need an armor or weapon object, only values
        super.setBlockAmount(5);
        super.setAttackDamage(5);
        super.setAttackDelay(10);
        this.sightRadius = sightRadius;
        this.attackDistance = attackDistance;

        // Delay between frame updates in milliseconds
        int animationDelay = 100;

        animationTimer = new Timer(animationDelay, new AnimationHandler());
        walkingAnimationFrame = 0;
        attackAnimationFrame = 0;
        viewDirection = 0;
        isAttacking = false;
        projectiles = new ArrayList<>();

        if (entityType == EntityType.skeletonWarrior) {
            attackCollisionFrame = 4;
        } else if (entityType == EntityType.skeletonArcher) {
            attackCollisionFrame = 10;
        }
    }

    public void detectPlayer(Game game) {

        Ellipse2D sightCircle = getCircleByCenter(new Point2D.Double(
                        getPositionX() + Math.floorDiv(GamePanel.NEW_TILE_SIZE, 2),
                        getPositionY() + Math.floorDiv(GamePanel.NEW_TILE_SIZE, 2)),
                sightRadius);

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
        Point2D playerMiddle, enemyMiddle;
        double angle;

        playerMiddle = new Point2D.Double(game.getPlayer().getPositionX() + Math.floorDiv(GamePanel.NEW_TILE_SIZE, 2), game.getPlayer().getPositionY() + Math.floorDiv(GamePanel.NEW_TILE_SIZE, 2));
        enemyMiddle = new Point2D.Double(getPositionX() + Math.floorDiv(GamePanel.NEW_TILE_SIZE, 2), getPositionY() + Math.floorDiv(GamePanel.NEW_TILE_SIZE, 2));
        double theta = Math.atan2(playerMiddle.getY() - enemyMiddle.getY(), playerMiddle.getX() - enemyMiddle.getX());
        theta += Math.PI / 2.0;
        angle = Math.toDegrees(theta);

        if (angle < 0) { angle += 360;}

        if (enemyMiddle.distance(playerMiddle) <= attackDistance) {
            isAttacking = true;

            if (getEntityType() == EntityType.skeletonArcher) animationTimer.setDelay(80);


        } else {
            if (angle >= 315 && angle < 359 || angle >= 0 && angle < 45) { // -> von 315 bis 45 -> Up
                viewDirection = 27; setCurrentFrame(walkingAnimationFrame + viewDirection);
                if (game.getCurrentLevel().isSolid(getPositionX() + Math.floorDiv(GamePanel.NEW_TILE_SIZE * 3, 10), getPositionY() - getMovementSpeed()) &&
                        game.getCurrentLevel().isSolid(getPositionX() - Math.floorDiv(GamePanel.NEW_TILE_SIZE * 3, 10), getPositionY() - getMovementSpeed())) {
                    setPositionY(getPositionY() - getMovementSpeed());
                }
            } else if (angle >= 45 && angle < 135) { // -> von 45 bis 135 -> Right
                viewDirection = 18; setCurrentFrame(walkingAnimationFrame + viewDirection);
                if (game.getCurrentLevel().isSolid(getPositionX() + getMovementSpeed() + Math.floorDiv(GamePanel.NEW_TILE_SIZE * 3, 10), getPositionY()) &&
                        game.getCurrentLevel().isSolid(getPositionX() + getMovementSpeed() + Math.floorDiv(GamePanel.NEW_TILE_SIZE * 3, 10), getPositionY() + Math.floorDiv(GamePanel.NEW_TILE_SIZE * 4, 10))) {
                    setPositionX(getPositionX() + getMovementSpeed());
                }
            } else if (angle >= 135 && angle < 225) { // -> von 135 bis 225 -> Down
                viewDirection = 0; setCurrentFrame(walkingAnimationFrame);
                if (game.getCurrentLevel().isSolid(getPositionX() - Math.floorDiv(GamePanel.NEW_TILE_SIZE * 3, 10), getPositionY() + getMovementSpeed() + Math.floorDiv(GamePanel.NEW_TILE_SIZE * 4, 10)) &&
                        game.getCurrentLevel().isSolid(getPositionX() + Math.floorDiv(GamePanel.NEW_TILE_SIZE * 3, 10), getPositionY() + getMovementSpeed() + Math.floorDiv(GamePanel.NEW_TILE_SIZE * 4, 10))) {
                    setPositionY(getPositionY() + getMovementSpeed());
                }
            } else if (angle >= 225 && angle < 315) { // -> von 225 bis 315 -> Left
                viewDirection = 9; setCurrentFrame(walkingAnimationFrame + viewDirection);
                if (game.getCurrentLevel().isSolid(getPositionX() - getMovementSpeed() - Math.floorDiv(GamePanel.NEW_TILE_SIZE * 3, 10), getPositionY() + Math.floorDiv(GamePanel.NEW_TILE_SIZE * 4, 10)) &&
                        game.getCurrentLevel().isSolid(getPositionX() - getMovementSpeed() - Math.floorDiv(GamePanel.NEW_TILE_SIZE * 3, 10), getPositionY())) {
                    setPositionX(getPositionX() - getMovementSpeed());
                }
            }
        }
    }

    public void attackPlayer(Game game) {
        setCurrentAnimationType("attacking");

        int startAngle = 0;
        int arcAngle = 0;

        switch (viewDirection) {
            case 27 -> { startAngle = 45; arcAngle = 90; setCurrentFrame(attackAnimationFrame + getAttackAnimationFrameLimit() * 3);}
            case 9 -> { startAngle = 135; arcAngle = 90; setCurrentFrame(attackAnimationFrame + getAttackAnimationFrameLimit());}
            case 18 -> { startAngle = 315; arcAngle = 90; setCurrentFrame(attackAnimationFrame + getAttackAnimationFrameLimit() * 2);}
            case 0 -> { startAngle = 225; arcAngle = 90; setCurrentFrame(attackAnimationFrame);}
        }

        // Draws the hit box when the sword was swung (fourth image seems fine)
        // Therefore, the character has the possibility to walk out of an attack
        // and dodge it before he was actually (visually) hit
        if (attackAnimationFrame == attackCollisionFrame && !attackHitBoxDrawn) {

            attackHitBoxDrawn = true;
            walkingAnimationFrame = 0;

            if (getEntityType() == EntityType.skeletonWarrior) {
                Arc2D arc2D = new Arc2D.Double();
                arc2D.setArcByCenter(
                        getPositionX() + Math.floorDiv(GamePanel.NEW_TILE_SIZE, 2),
                        getPositionY() + Math.floorDiv(GamePanel.NEW_TILE_SIZE, 2),
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
            } else if (getEntityType() == EntityType.skeletonArcher) {

                projectiles.add(new Projectile(getPositionX(), getPositionY(), viewDirection));
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
                    System.out.println("Player was hit by projectile");
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

        for (Projectile p : projectiles) {
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
                if (attackAnimationFrame >= getAttackAnimationFrameLimit()) {
                    attackAnimationFrame = 0;
                    isAttacking = false;
                    attackHitBoxDrawn = false;
                    animationTimer.setDelay(100);
                    setCooldown(getAttackDelay());
                }
            }
        }
    }
}


