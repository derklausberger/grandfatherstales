package objectClasses;

import GUI.*;
import objectClasses.Abstract.Entity;
import objectClasses.Enum.AnimationType;
import objectClasses.Enum.EntityType;
import utilityClasses.AnimationFrame;
import utilityClasses.AudioManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.*;
import java.util.ArrayList;
import java.util.Arrays;

public class Enemy extends Entity {

    private final boolean[] states = new boolean[3];

    private boolean playingWalkingSound;

    // attackHitBoxDrawn -> is set to true as soon as the third frame
    // of the attack animation is reached and the hit box is drawn
    // attackCollisionFrame -> indicates, when exactly (frame)
    // the hitbox for an attack should be drawn
    private boolean attackHitBoxDrawn;
    private final int sightRadius, attackDistance;
    private final int attackCollisionFrame;
    private int viewDirection;

    private final ArrayList<Projectile> projectiles;


    public Enemy(int positionX, int positionY, int movementSpeed, int healthPoints, int sightRadius, int attackDistance, EntityType entityType, int viewDirection) {
        super(positionX, positionY, movementSpeed, healthPoints, entityType, viewDirection);

        // Enemies don't need an armor or weapon object, only values
        super.setBlockAmount(5);
        super.setAttackDamage(10);
        super.setAttackDelay(1000);
        this.sightRadius = sightRadius;
        this.attackDistance = attackDistance;

        attackCollisionFrame = entityType.getAttackCollisionFrame();
        projectiles = new ArrayList<>();

        setWalkingAnimationFrame(0);
        setAttackAnimationFrame(0);
        setRestingAnimationFrame(0);

        // 0 -> resting
        // 1 -> walking
        // 2 -> attacking
        setCurrentState(0);

        startAnimationTimer();
    }

    private void setCurrentState(int currentState) {

        Arrays.fill(states, false);

        if (currentState < states.length && currentState >= 0) {
            states[currentState] = true;
        }
    }

    private boolean isInState(int state) {

        if (state >= states.length) return false;
        return states[state];
    }


    public void update(Game game) {

        if (isAttacking()) {
            attack(game);

        } else if (isWalking()) {
            walk(game);

        } else if (isResting()) {
            rest(game);
        }
    }

    private void recover() {

        toggleWalkingSound(true);
        setWalkingAnimationFrame(0);
        setRestingAnimationFrame(0);
        setAttackAnimationFrame(1);
        setCurrentState(0);

        setCurrentAnimationType(AnimationType.walking.toString());
        adaptViewDirection(viewDirection);
        setCurrentAnimationFrame(getRestingAnimationFrame());
    }

    private void walk(Game game) {

        if (getCoolDown() == 0) {

            //
            // Implement code to walk around in a certain area
            //

            setCurrentAnimationFrame(getWalkingAnimationFrame());
            setCurrentAnimationType(AnimationType.walking.toString());
            setCurrentState(1);

            if (isInRange(game, sightRadius)) {
                followPlayer(game);
            } else {
                recover();
            }
        } else {
            recover();
        }
    }

    private void rest(Game game) {
        // !Resting currently not implemented for enemies!

        if (getCoolDown() == 0) {
            setCurrentAnimationFrame(getRestingAnimationFrame());
            setCurrentAnimationType(AnimationType.walking.toString());
            setCurrentState(0);

            //
            // Implement code to wait a few seconds
            // before walking again
            //

            // If awake/attentive, check FOV
            if (isInRange(game, sightRadius)) {
                followPlayer(game);
            } else {
                recover();
            }
        } else {
            recover();
        }

        // e.g. if sleeping/eating,
        // don't check FOV
        // -> Makes attacks from behind possible
    }

    private Point2D getEntityMiddle(Entity entity) {

        Point2D entityMiddle = new Point2D.Double();
        entityMiddle.setLocation(
                entity.getPositionX() + 15,
                entity.getPositionY() + 25);

        return entityMiddle;
    }

    private boolean isInRange(Game game, int radius) {

        Point2D playerMiddle = getEntityMiddle(game.getPlayer()),
                enemyMiddle = getEntityMiddle(this);

        return enemyMiddle.distance(playerMiddle) <= radius;
    }

    private void toggleWalkingSound(boolean stop) {
        if (stop) {
            if (playingWalkingSound) {
                AudioManager.stop("S - skeletonWalking");
                playingWalkingSound = false;
                AudioManager.playingSkeletonWalking = false;
            }
        } else {
            if (!playingWalkingSound) {
                if (!AudioManager.playingSkeletonWalking) {
                    AudioManager.loop("S - skeletonWalking");
                    AudioManager.playingSkeletonWalking = true;
                    playingWalkingSound = true;
                }
            }
        }
    }

    private void determineViewDirection(Game game) {

        Point2D playerMiddle = getEntityMiddle(game.getPlayer()),
                enemyMiddle = getEntityMiddle(this);
        double angle = game.getAngle(playerMiddle, enemyMiddle);

        if (angle >= 315 && angle < 359 || angle >= 0 && angle < 45) { // -> von 315 bis 45 -> Up
            viewDirection = 0;

        } else if (angle >= 135 && angle < 225) { // -> von 135 bis 225 -> Down
            viewDirection = 1;

        } else if (angle >= 225 && angle < 315) { // -> von 225 bis 315 -> Left
            viewDirection = 2;

        } else if (angle >= 45 && angle < 135) { // -> von 45 bis 135 -> Right
            viewDirection = 3;
        }
    }

    private void moveEnemy(Game game) {

        switch (viewDirection) {
            case 0 -> {
                if (game.getCurrentLevel().isSolid(getPositionX() + Math.floorDiv(GamePanel.NEW_TILE_SIZE * 3, 10), getPositionY() - getMovementSpeed()) &&
                        game.getCurrentLevel().isSolid(getPositionX() - Math.floorDiv(GamePanel.NEW_TILE_SIZE * 3, 10), getPositionY() - getMovementSpeed())) {
                    setPositionY(getPositionY() - getMovementSpeed());
                }
            }
            case 1 -> {
                if (game.getCurrentLevel().isSolid(getPositionX() - Math.floorDiv(GamePanel.NEW_TILE_SIZE * 3, 10), getPositionY() + getMovementSpeed() + Math.floorDiv(GamePanel.NEW_TILE_SIZE * 4, 10)) &&
                        game.getCurrentLevel().isSolid(getPositionX() + Math.floorDiv(GamePanel.NEW_TILE_SIZE * 3, 10), getPositionY() + getMovementSpeed() + Math.floorDiv(GamePanel.NEW_TILE_SIZE * 4, 10))) {
                    setPositionY(getPositionY() + getMovementSpeed());
                }
            }
            case 2 -> {
                if (game.getCurrentLevel().isSolid(getPositionX() - getMovementSpeed() - Math.floorDiv(GamePanel.NEW_TILE_SIZE * 3, 10), getPositionY() + Math.floorDiv(GamePanel.NEW_TILE_SIZE * 4, 10)) &&
                        game.getCurrentLevel().isSolid(getPositionX() - getMovementSpeed() - Math.floorDiv(GamePanel.NEW_TILE_SIZE * 3, 10), getPositionY())) {
                    setPositionX(getPositionX() - getMovementSpeed());
                }
            }
            case 3 -> {
                if (game.getCurrentLevel().isSolid(getPositionX() + getMovementSpeed() + Math.floorDiv(GamePanel.NEW_TILE_SIZE * 3, 10), getPositionY()) &&
                        game.getCurrentLevel().isSolid(getPositionX() + getMovementSpeed() + Math.floorDiv(GamePanel.NEW_TILE_SIZE * 3, 10), getPositionY() + Math.floorDiv(GamePanel.NEW_TILE_SIZE * 4, 10))) {
                    setPositionX(getPositionX() + getMovementSpeed());
                }
            }
        }
    }

    public void followPlayer(Game game) {
        setCurrentAnimationFrame(getWalkingAnimationFrame());
        setCurrentAnimationType(AnimationType.walking.toString());
        setCurrentState(1);

        determineViewDirection(game);

        if (isInRange(game, attackDistance)) {

            if (getEntityType() == EntityType.skeletonArcher) setAnimationDelay(80);
            toggleWalkingSound(true);

            setCurrentAnimationType(AnimationType.attacking.toString());
            adaptViewDirection(viewDirection);
            attack(game);

        } else {
            toggleWalkingSound(false);
            adaptViewDirection(viewDirection);
            moveEnemy(game);
        }
    }

    private void attack(Game game) {

        setCurrentState(2);
        setCurrentAnimationFrame(getAttackAnimationFrame());

        // Draws the hit box when the sword was swung (fourth image seems fine)
        // Therefore, the character has the possibility to walk out of an attack
        // and dodge it before he was actually (visually) hit
        if (getAttackAnimationFrame() == attackCollisionFrame && !attackHitBoxDrawn) {
            attackHitBoxDrawn = true;

            if (getEntityType() == EntityType.skeletonWarrior) {

                if (isInRange(game, attackDistance)) {
                    Point2D playerMiddle = getEntityMiddle(game.getPlayer()),
                            enemyMiddle = getEntityMiddle(this);

                    double angle = game.getAngle(playerMiddle, enemyMiddle);

                    if (game.enemyAttack(angle, viewDirection)) {
                        game.dealDamageToPlayer(this);
                    }
                }
            } else if (getEntityType() == EntityType.skeletonArcher) {
                projectiles.add(new Projectile(getPositionX(), getPositionY(), viewDirection));
            }
        }
    }

    public Point2D getEnemyMiddle() {

        Point2D enemyMiddle = new Point2D.Double();
        enemyMiddle.setLocation(
                getPositionX() + getEntityFrames(getCurrentAnimationType())[getCurrentFrame()].getXOffset(),
                getPositionY() + getEntityFrames(getCurrentAnimationType())[getCurrentFrame()].getYOffset());
        return enemyMiddle;
    }

    public ArrayList<Projectile> getProjectiles() {
        return projectiles;
    }

    @Override
    public void draw(Graphics2D graph2D, Game game) {

        /*
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
         */

        int x = (int) ((GamePanel.WINDOW_WIDTH) / 2);
        int y = (int) ((GamePanel.WINDOW_HEIGHT) / 2);

        drawEnemy(graph2D, game, x, y);
        drawHealthBar(graph2D, game, x, y);
        drawProjectiles(graph2D, game);
    }

    private void drawEnemy(Graphics2D graph2D, Game game, int x, int y) {

        // The current frame
        AnimationFrame frame = getEntityFrames(getCurrentAnimationType())[getCurrentFrame()];

        // Draws the character
        graph2D.drawImage(
                frame.getImage(),
                x + frame.getXOffset() - game.getPlayer().getPositionX() + this.getPositionX(),
                y + frame.getYOffset() - game.getPlayer().getPositionY() + this.getPositionY(),
                frame.getWidth(), frame.getHeight(), null
        );
    }

    private void drawHealthBar(Graphics2D graph2D, Game game, int x, int y) {

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

    private void drawProjectiles(Graphics2D graph2D, Game game) {

        for (Projectile p : projectiles) {
            p.draw(graph2D, game);
        }
    }

    private boolean isAttacking() {
        return isInState(2);
    }

    private boolean isWalking() {
        return isInState(1);
    }

    private boolean isResting() {
        return isInState(0);
    }

    @Override
    protected void handleAnimation() {

        if (isDead()) {
            //   if (proceedAnimationFrame(AnimationType.dying.toString(),
            //           getAnimationFrameLimit(AnimationType.dying.toString()) - 1)) {
            //       endDyingAnimation();
            //   }
        } else if (isAttacking()) {
            if (proceedAnimationFrame(AnimationType.attacking.toString(),
                    getAnimationFrameLimit(AnimationType.attacking.toString()))) {
                endAttackingAnimation();
            }
        } else if (isWalking()) {
            if (proceedAnimationFrame(AnimationType.walking.toString(),
                    getAnimationFrameLimit(AnimationType.walking.toString()))) {
                endWalkingAnimation();
            }
        } else if (isResting()) {
            setRestingAnimationFrame(0);
            //if (proceedAnimationFrame(AnimationType.resting.toString(),
            //        getAnimationFrameLimit(AnimationType.resting.toString()))) {
            //    endRestingAnimation();
            // }
        }
    }

    @Override
    protected void endRestingAnimation() {

        // Currently no rest animation for enemies
    }

    @Override
    protected void endWalkingAnimation() {
        setWalkingAnimationFrame(1);
    }

    @Override
    protected void endAttackingAnimation() {

        attackHitBoxDrawn = false;
        setCurrentState(0);
        setAnimationDelay(100);
        setCoolDown(getAttackDelay());

        new Timer(getAttackDelay(), new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ((Timer) e.getSource()).stop();
                setCoolDown(0);
            }
        }).start();
    }

    @Override
    protected void endDyingAnimation() {

        // Currently no dying animation for enemies
    }

    @Override
    public void updateKnockBack(Game game) {

        if (!allowKnockBack(game, game.getPlayer().getCurrentDirection())) {
            setKnockBackDuration(0);
            setKnockBack(false);
            setMomentum(0);
        } else if (reduceKnockBackDuration()) {
            setKnockBack(false);
            setMomentum(0);
        }
    }

    @Override
    public void startKnockBack() {
        setKnockBackDuration(15);
        setMomentum(getKnockBackDuration() * getMovementSpeed());
        setKnockBack(true);
    }
}


