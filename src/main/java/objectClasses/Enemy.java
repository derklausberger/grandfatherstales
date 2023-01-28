package objectClasses;

import GUI.*;
import objectClasses.Abstract.Entity;
import objectClasses.Enum.AnimationType;
import objectClasses.Enum.EntityType;
import utilityClasses.AnimationFrame;
import utilityClasses.AudioManager;
import utilityClasses.InputHandler;

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
    private int sightRadius, attackDistance;
    private int attackCollisionFrame = 4;
    private int viewDirection;

    private ArrayList<Projectile> projectiles;



    /*
        public Entity getEnemy(EnemyType type) {

        switch(type) {
        case EntityType.skeletonWarrior: return new SkeletonWarrior();
        case EntityType.skeletonArcher: return new SkeletonArcher();
        default: throw new NotSupportedException();
            }
        }


     */


    public Enemy(int positionX, int positionY, int movementSpeed, int healthPoints, int sightRadius, int attackDistance, EntityType entityType, int viewDirection) {
        super(positionX, positionY, movementSpeed, healthPoints, entityType, viewDirection);

        // Enemies don't need an armor or weapon object, only values
        super.setBlockAmount(5);
        super.setAttackDamage(10);
        super.setAttackDelay(10);
        this.sightRadius = sightRadius;
        this.attackDistance = attackDistance;

        // Delay between frame updates in milliseconds
        int animationDelay = 100;

        animationTimer = new Timer(animationDelay, new AnimationHandler());
        walkingAnimationFrame = 0;
        attackAnimationFrame = 0;
        isAttacking = false;
        projectiles = new ArrayList<>();

        if (entityType == EntityType.skeletonWarrior) {
            attackCollisionFrame = 4;
        } else if (entityType == EntityType.skeletonArcher) {
            attackCollisionFrame = 10;
        }
    }

    public void detectPlayer(Game game) {

        Point2D playerMiddle = new Point2D.Double(),
                enemyMiddle = new Point2D.Double();

        playerMiddle.setLocation(
                game.getPlayer().getPositionX() + 15,
                game.getPlayer().getPositionY() + 25);

        enemyMiddle.setLocation(
                getPositionX() + 15,
                getPositionY() + 25);

        if (!isAttacking) {
            setCurrentAnimationType(AnimationType.walking.toString());
            if (getCooldown() == 0) {
                if (enemyMiddle.distance(playerMiddle) <= sightRadius) {
                    followPlayer(game);
                } else {
                    if (animationTimer.isRunning()) {
                        animationTimer.stop();
                    }

                    walkingAnimationFrame = 0;
                    setViewDirection(viewDirection);
                    setCurrentAnimationFrame(walkingAnimationFrame);

                    if (playingWalkingSound) {
                        AudioManager.stop("S - skeletonWalking");
                        playingWalkingSound = false;
                        AudioManager.playingSkeletonWalking = false;
                    }
                }
            } else {
                if (playingWalkingSound) {
                    AudioManager.stop("S - skeletonWalking");
                    playingWalkingSound = false;
                    AudioManager.playingSkeletonWalking = false;
                }
                walkingAnimationFrame = 0;
                setViewDirection(viewDirection);
                setCurrentAnimationFrame(walkingAnimationFrame);
            }
        } else {
            attackPlayer(game);
        }
    }


    public void followPlayer(Game game) {
        animationTimer.start();

        Point2D playerMiddle = new Point2D.Double(),
                enemyMiddle = new Point2D.Double();

        playerMiddle.setLocation(
                game.getPlayer().getPositionX() + 15,
                game.getPlayer().getPositionY() + 25);

        enemyMiddle.setLocation(
                getPositionX() + 15,
                getPositionY() + 25);

        double angle, theta;
        theta = Math.atan2(playerMiddle.getY() - enemyMiddle.getY(), playerMiddle.getX() - enemyMiddle.getX());
        theta += Math.PI / 2.0;
        angle = Math.toDegrees(theta);

        if (angle < 0) {
            angle += 360;
        }

        if (enemyMiddle.distance(playerMiddle) <= attackDistance) {
            isAttacking = true;

            if (playingWalkingSound) {
                AudioManager.stop("S - skeletonWalking");
                playingWalkingSound = false;
                AudioManager.playingSkeletonWalking = false;
            }

            if (getEntityType() == EntityType.skeletonArcher) animationTimer.setDelay(80);


        } else {
            if (!playingWalkingSound) {
                if (!AudioManager.playingSkeletonWalking) {
                    AudioManager.loop("S - skeletonWalking");
                    AudioManager.playingSkeletonWalking = true;
                    playingWalkingSound = true;
                }
            }
            if (angle >= 315 && angle < 359 || angle >= 0 && angle < 45) { // -> von 315 bis 45 -> Up
                viewDirection = 27;
                //setCurrentFrame(walkingAnimationFrame + getViewDirection());
                //setCurrentAnimationFrame(walkingAnimationFrame);
                if (game.getCurrentLevel().isSolid(getPositionX() + Math.floorDiv(GamePanel.NEW_TILE_SIZE * 3, 10), getPositionY() - getMovementSpeed()) &&
                        game.getCurrentLevel().isSolid(getPositionX() - Math.floorDiv(GamePanel.NEW_TILE_SIZE * 3, 10), getPositionY() - getMovementSpeed())) {
                    setPositionY(getPositionY() - getMovementSpeed());
                }
            } else if (angle >= 45 && angle < 135) { // -> von 45 bis 135 -> Right
                viewDirection = 18;
                //setCurrentFrame(walkingAnimationFrame + getViewDirection());
                if (game.getCurrentLevel().isSolid(getPositionX() + getMovementSpeed() + Math.floorDiv(GamePanel.NEW_TILE_SIZE * 3, 10), getPositionY()) &&
                        game.getCurrentLevel().isSolid(getPositionX() + getMovementSpeed() + Math.floorDiv(GamePanel.NEW_TILE_SIZE * 3, 10), getPositionY() + Math.floorDiv(GamePanel.NEW_TILE_SIZE * 4, 10))) {
                    setPositionX(getPositionX() + getMovementSpeed());
                }
            } else if (angle >= 135 && angle < 225) { // -> von 135 bis 225 -> Down
                viewDirection = 0;
                //setCurrentFrame(walkingAnimationFrame + getViewDirection());
                if (game.getCurrentLevel().isSolid(getPositionX() - Math.floorDiv(GamePanel.NEW_TILE_SIZE * 3, 10), getPositionY() + getMovementSpeed() + Math.floorDiv(GamePanel.NEW_TILE_SIZE * 4, 10)) &&
                        game.getCurrentLevel().isSolid(getPositionX() + Math.floorDiv(GamePanel.NEW_TILE_SIZE * 3, 10), getPositionY() + getMovementSpeed() + Math.floorDiv(GamePanel.NEW_TILE_SIZE * 4, 10))) {
                    setPositionY(getPositionY() + getMovementSpeed());
                }
            } else if (angle >= 225 && angle < 315) { // -> von 225 bis 315 -> Left
                viewDirection = 9;
                //setCurrentFrame(walkingAnimationFrame + getViewDirection());
                if (game.getCurrentLevel().isSolid(getPositionX() - getMovementSpeed() - Math.floorDiv(GamePanel.NEW_TILE_SIZE * 3, 10), getPositionY() + Math.floorDiv(GamePanel.NEW_TILE_SIZE * 4, 10)) &&
                        game.getCurrentLevel().isSolid(getPositionX() - getMovementSpeed() - Math.floorDiv(GamePanel.NEW_TILE_SIZE * 3, 10), getPositionY())) {
                    setPositionX(getPositionX() - getMovementSpeed());
                }
            }
            setViewDirection(viewDirection);
            setCurrentAnimationFrame(walkingAnimationFrame);
            System.out.println(getCurrentFrame());
        }
    }

    public void attackPlayer(Game game) {
        setCurrentAnimationType(AnimationType.attacking.toString()); // type for enemy

        if (viewDirection == 27) {
            setViewDirection(18);
        } else if (viewDirection == 18) {
            setViewDirection(12);
        } else if (viewDirection == 0) {
            setViewDirection(0);
        } else if (viewDirection == 9) {
            setViewDirection(6);
        }

        setCurrentAnimationFrame(attackAnimationFrame);

        // Draws the hit box when the sword was swung (fourth image seems fine)
        // Therefore, the character has the possibility to walk out of an attack
        // and dodge it before he was actually (visually) hit
        if (attackAnimationFrame == attackCollisionFrame && !attackHitBoxDrawn) {
            attackHitBoxDrawn = true;
            walkingAnimationFrame = 0;
            getEntityType().attack(); // -> inside enum
            if (getEntityType() == EntityType.skeletonWarrior) {

                Point2D playerMiddle = new Point2D.Double(
                        game.getPlayer().getPositionX() + 15,
                        game.getPlayer().getPositionY() + 25);

                Point2D enemyMiddle = new Point2D.Double(
                        getPositionX() + 15,
                        getPositionY() + 25);

                double angle;
                double theta = Math.atan2(playerMiddle.getY() - enemyMiddle.getY(), playerMiddle.getX() - enemyMiddle.getX());
                theta += Math.PI / 2.0;
                angle = Math.toDegrees(theta);

                if (angle < 0) {
                    angle += 360;
                }

                if (enemyMiddle.distance(playerMiddle) <= attackDistance) { // if on the fourth frame still in range
                    if (game.enemyAttack(angle, getViewDirection())) {
                        game.dealDamage(this);
                    }
                }
            } else if (getEntityType() == EntityType.skeletonArcher) {
                projectiles.add(new Projectile(getPositionX(), getPositionY(), getViewDirection()));
            }
        }
    }

    public ArrayList<Projectile> getProjectiles() {
        return projectiles;
    }

    @Override
    public void draw(Graphics2D graph2D, Game game, GamePanel gamePanel) {

        Point2D playerMiddle = new Point2D.Double(
                game.getPlayer().getPositionX() + 15,
                game.getPlayer().getPositionY() + 25);

        Point2D enemyMiddle = new Point2D.Double(
                getPositionX() + 15,
                getPositionY() + 25);

        Shape enemy = new Rectangle2D.Double(
                 getPositionX() + getEntityFrames(getCurrentAnimationType())[getCurrentFrame()].getXOffset(),
                getPositionY() + getEntityFrames(getCurrentAnimationType())[getCurrentFrame()].getYOffset(),
                getEntityFrames(getCurrentAnimationType())[getCurrentFrame()].getWidth(),
                getEntityFrames(getCurrentAnimationType())[getCurrentFrame()].getHeight());

        graph2D.setColor(Color.ORANGE);
        graph2D.fill(enemy);



        Shape player = new Rectangle2D.Double(
                game.getPlayer().getPositionX(),
                game.getPlayer().getPositionY(),
                game.getPlayer().getEntityFrames(game.getPlayer().getCurrentAnimationType())[game.getPlayer().getCurrentFrame()].getWidth(),
                game.getPlayer().getEntityFrames(game.getPlayer().getCurrentAnimationType())[game.getPlayer().getCurrentFrame()].getHeight());

        graph2D.setColor(Color.ORANGE);
        graph2D.fill(player);

        graph2D.setColor(Color.BLACK);
        graph2D.fill(getCircleByCenter(playerMiddle, 10));

        graph2D.setColor(Color.GREEN);
        graph2D.fill(getCircleByCenter(enemyMiddle, 10));


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

                if (walkingAnimationFrame >= getAnimationFrameLimit(getCurrentAnimationType())) {
                    walkingAnimationFrame = 0;
                }
                if (getCooldown() > 0) {
                    reduceCooldown();
                }
            } else {
                System.out.println(attackAnimationFrame);
                attackAnimationFrame++;

                if (attackAnimationFrame >= getAnimationFrameLimit(getCurrentAnimationType())) {
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
    public void update(Game game) {
        if (this.getKnockBackDuration() <= 1) {
            this.setKnockBackDuration(0);
            this.setKnockBack(false);

            this.setMomentum(0);
        } else if (this.isKnockBack()) {
            if (GamePanel.player.getCurrentFrame() >= 19 && GamePanel.player.getCurrentFrame() < 25 + 1) { // up
                if (game.getCurrentLevel().isSolid(this.getPositionX(), this.getPositionY() - (int) (this.getMomentum() * factor[15 - this.getKnockBackDuration()])) &&
                        game.getCurrentLevel().isSolid(this.getPositionX() + GamePanel.NEW_TILE_SIZE, this.getPositionY() - (int) (this.getMomentum() * factor[15 - this.getKnockBackDuration()]))
                ) {
                    this.setPositionY(this.getPositionY() - (int) (this.getMomentum() * factor[15 - this.getKnockBackDuration()]));
                } else {
                    this.setKnockBackDuration(0);
                }
            } else if (GamePanel.player.getCurrentFrame() >= 13 && GamePanel.player.getCurrentFrame() < 18 + 1) { // right
                if (game.getCurrentLevel().isSolid(this.getPositionX() + (int) (this.getMomentum() * factor[15 - this.getKnockBackDuration()]) + GamePanel.NEW_TILE_SIZE, this.getPositionY()) &&
                        game.getCurrentLevel().isSolid(this.getPositionX() + (int) (this.getMomentum() * factor[15 - this.getKnockBackDuration()]) + GamePanel.NEW_TILE_SIZE, this.getPositionY() + GamePanel.NEW_TILE_SIZE)
                ) {
                    this.setPositionX(this.getPositionX() + (int) (this.getMomentum() * factor[15 - this.getKnockBackDuration()]));
                } else {
                    this.setKnockBackDuration(0);
                }
            } else if (GamePanel.player.getCurrentFrame() >= 1 && GamePanel.player.getCurrentFrame() < 6 + 1) { // down
                if (game.getCurrentLevel().isSolid(this.getPositionX(), this.getPositionY() + (int) (this.getMomentum() * factor[15 - this.getKnockBackDuration()]) + GamePanel.NEW_TILE_SIZE) &&
                        game.getCurrentLevel().isSolid(this.getPositionX() + GamePanel.NEW_TILE_SIZE, this.getPositionY() + (int) (this.getMomentum() * factor[15 - this.getKnockBackDuration()]) + GamePanel.NEW_TILE_SIZE)
                ) {
                    this.setPositionY(this.getPositionY() + (int) (this.getMomentum() * factor[15 - this.getKnockBackDuration()]));
                } else {
                    this.setKnockBackDuration(0);
                }
            } else if (GamePanel.player.getCurrentFrame() >= 7 && GamePanel.player.getCurrentFrame() < 12 + 1) { // left
                if (game.getCurrentLevel().isSolid(this.getPositionX() - (int) (this.getMomentum() * factor[15 - this.getKnockBackDuration()]), this.getPositionY()) &&
                        game.getCurrentLevel().isSolid(this.getPositionX() - (int) (this.getMomentum() * factor[15 - this.getKnockBackDuration()]), this.getPositionY() + GamePanel.NEW_TILE_SIZE)
                ) {
                    this.setPositionX(this.getPositionX() - (int) (this.getMomentum() * factor[15 - this.getKnockBackDuration()]));
                } else {
                    this.setKnockBackDuration(0);
                }
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
}


