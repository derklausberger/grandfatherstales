package objectClasses;

import GUI.AnimationFrame;
import GUI.AudioManager;
import GUI.GamePanel;
import GUI.InputHandler;
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

    private int walkingAnimationFrame, attackAnimationFrame;
    private final Timer animationTimer;

    private boolean playingWalkingSound;

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

                    if (playingWalkingSound) {
                        System.out.println("stopping 1");
                        AudioManager.stop("S - skeletonWalking");
                        playingWalkingSound = false;
                        AudioManager.playingSkeletonWalking = false;
                    }
                }
            } else {
                if (playingWalkingSound) {
                    System.out.println("stopping 2");
                    AudioManager.stop("S - skeletonWalking");
                    playingWalkingSound = false;
                    AudioManager.playingSkeletonWalking = false;
                }
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

            if (playingWalkingSound) {
                System.out.println("stopping 3");
                AudioManager.stop("S - skeletonWalking");
                playingWalkingSound = false;
                AudioManager.playingSkeletonWalking = false;
            }

            if (getEntityType() == EntityType.skeletonArcher) animationTimer.setDelay(80);


        } else {
            if (!playingWalkingSound) {
                if (!AudioManager.playingSkeletonWalking) {
                    System.out.println("starting");
                    AudioManager.loop("S - skeletonWalking");
                    AudioManager.playingSkeletonWalking = true;
                    playingWalkingSound = true;
                }
            }
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

                    dealDamage(game.getPlayer());

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

                int arrowOffset = 5;
                int playerXLeft = game.getPlayer().getPositionX(), playerXRight = game.getPlayer().getPositionX() + 30;
                int playerYUp = game.getPlayer().getPositionY(), playerYDown = game.getPlayer().getPositionY() + 50;
                switch (p.getDirection()) {
                    case 9, 18 -> {
                        if (game.getPlayer().getKeyHandler().walkingDirection == InputHandler.upKey
                                || game.getPlayer().getKeyHandler().walkingDirection == InputHandler.downKey) {
                            playerXLeft -= 25;
                            playerXRight -= 22;

                        } else {
                            playerXLeft -= 35;
                            playerXRight -= 22;
                        }
                        playerYUp -= 25;
                        playerYDown -= 25;
                    }
                    case 0, 27 -> {
                        if (game.getPlayer().getKeyHandler().walkingDirection == InputHandler.upKey
                                || game.getPlayer().getKeyHandler().walkingDirection == InputHandler.downKey) {
                            playerXLeft -= 15;
                            playerXRight -= 12;
                        } else {
                            playerXLeft -= 15;
                            playerXRight -= 18;
                        }
                        playerYUp -= 40;
                        playerYDown -= 25;
                    }
                }

                if (p.getX() < playerXRight && p.getX() + arrowOffset > playerXLeft
                        && p.getY() < playerYDown && p.getY() + arrowOffset > playerYUp) {

                    dealDamage(game.getPlayer());
                    System.out.println("Player was hit by projectile");

                    projectiles.remove(i);
                }
                /*
                if (p.getX() < game.getPlayer().getPositionX() + 30 && p.getX() > game.getPlayer().getPositionX()
                        && p.getY() < game.getPlayer().getPositionY() + 50 && p.getY() > game.getPlayer().getPositionY()) {
                    System.out.println("Player: " + game.getPlayer().getPositionX() + " " + game.getPlayer().getPositionY());
                    System.out.println("Projectile: " + p.getX() + " " + p.getY());
                    //System.out.println("Player was hit by projectile");
                    //attackPlayer(game);
                    projectiles.remove(i);
                }
                 */
            }
        }
    }

    private void dealDamage(Player player) {

        if (!player.isInvincible()) {
            if (player.getBlockAmount() >= getAttackDamage()) {

                // Block sound
            } else {
                AudioManager.play("S - characterHit");
                player.setCurrentHealthPoints(player.getCurrentHealthPoints() + player.getBlockAmount() - getAttackDamage());
            }

            if (player.getCurrentHealthPoints() <= 0) {
                if (player.getLife() >= 1) {
                    player.setLife(player.getLife() - 1);
                    player.setCurrentHealthPoints(player.getMaxHealthPoints());
                } else {
                    // End the game
                }
            }
        } else {
            System.out.println("WAS INVINCIBLE");
            player.triggerInvincibility();
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

    @Override
    public void update() {
        if (getKnockBackDuration() <= 1) {
            setKnockBackDuration(0);
            setKnockBack(false);

            setMomentum(0);
        } else if (isKnockBack()) {
            if (GamePanel.player.getCurrentFrame() >= 19 && GamePanel.player.getCurrentFrame() < 25+1) { // up
                setPositionY(this.getPositionY() - (int) (this.getMomentum() * factor[15 - this.getKnockBackDuration()]));
            } else if (GamePanel.player.getCurrentFrame() >= 13 && GamePanel.player.getCurrentFrame() < 18+1) { // right
                setPositionX(this.getPositionX() + (int) (this.getMomentum() * factor[15 - this.getKnockBackDuration()]));
            } else if (GamePanel.player.getCurrentFrame() >= 1 && GamePanel.player.getCurrentFrame() < 6+1) { // down
                setPositionY(this.getPositionY() + (int) (this.getMomentum() * factor[15 - this.getKnockBackDuration()]));
            } else if (GamePanel.player.getCurrentFrame() >= 7 && GamePanel.player.getCurrentFrame() < 12+1) { // left
                setPositionX(this.getPositionX() - (int) (this.getMomentum() * factor[15 - this.getKnockBackDuration()]));
            }
            setKnockBackDuration(getKnockBackDuration() - 1);
        }
    }

    @Override
    public void hit() {
        this.setKnockBackDuration(15);
        this.setMomentum(this.getKnockBackDuration() * this.getMovementSpeed());
        this.setKnockBack(true);
    }
}


