package objectClasses.Abstract;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import GUI.GamePanel;
import objectClasses.Enum.AnimationType;
import utilityClasses.AnimationFrame;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import objectClasses.Enum.EntityType;
import objectClasses.Game;
import objectClasses.KnockBack;
import utilityClasses.ResourceLoader;

import javax.swing.*;

public abstract class Entity implements KnockBack {

    Double[] factor = {1.0, 0.98, 0.95, 0.88, 0.70, 0.56, 0.47, 0.40, 0.35, 0.30, 0.27, 0.25, 0.21, 0.20, 0.19};

    private EntityType entityType;
    private int maxHealthPoints;
    private int currentHealthPoints;

    private int positionX;
    private int positionY;
    private int movementSpeed;

    private int momentum;
    private int knockBackDuration;
    private boolean knockBack = false;

    private int attackDamage;
    private int blockAmount;

    // Indicates when an enemy can move again after attacking
    private int coolDown;
    // Value for the coolDown to refer to that is set for all entities after creation
    private int attackDelay;

    // entityFrames -> holds all frames of all animations
    // currentAnimationType -> indicates the current type
    // currentFrame -> indicates the current frame of the current animation
    private Map<String, AnimationFrame[]> entityFrames = new HashMap<>();
    private String currentAnimationType;
    private int currentFrame;

    private int
            walkingAnimationDirections,
            attackAnimationDirections,
            dyingAnimationDirections,
            restingAnimationDirections;

    private int walkingAnimationFrame,
            attackAnimationFrame,
            dyingAnimationFrame,
            restingAnimationFrame;

    private int viewDirection;

    private final Timer animationTimer;

    public Entity(int positionX, int positionY, int movementSpeed, int healthPoints, EntityType entityType, int viewDirection) {

        int animationDelay = 100;
        animationTimer = new Timer(animationDelay, new AnimationHandler());

        this.positionX = positionX;
        this.positionY = positionY;
        this.movementSpeed = movementSpeed;

        this.currentHealthPoints = healthPoints;
        this.maxHealthPoints = healthPoints;
        this.entityType = entityType;

        this.viewDirection = viewDirection;

        loadAnimationFrames(entityType.toString());
    }

    public void startAnimationTimer() {

        if (animationTimer.isRunning()) return;
        animationTimer.start();
    }

    public void stopAnimationTimer() {

        if (!animationTimer.isRunning()) return;
        animationTimer.stop();
    }

    public void setAnimationDelay(int delay) {

        animationTimer.setDelay(delay);
    }

    public int getWalkingAnimationFrame() {

        return walkingAnimationFrame;
    }

    public int getAttackAnimationFrame() {

        return attackAnimationFrame;
    }

    public int getRestingAnimationFrame() {

        return restingAnimationFrame;
    }

    public int getDyingAnimationFrame() {

        return dyingAnimationFrame;
    }

    public void setWalkingAnimationFrame(int walkingAnimationFrame) {
        this.walkingAnimationFrame = walkingAnimationFrame;
    }

    public void setAttackAnimationFrame(int attackAnimationFrame) {
        this.attackAnimationFrame = attackAnimationFrame;
    }

    public void setDyingAnimationFrame(int dyingAnimationFrame) {
        this.dyingAnimationFrame = dyingAnimationFrame;
    }

    public void setRestingAnimationFrame(int restingAnimationFrame) {
        this.restingAnimationFrame = restingAnimationFrame;
    }

    public boolean isDead() {
        return getCurrentHealthPoints() <= 0;
    }


    public int getViewDirection() {
        return viewDirection;
    }

    public void setViewDirection(int viewDirection) {
        this.viewDirection = viewDirection;
    }

    public boolean isKnockedBack() {
        return knockBack;
    }

    public void setKnockBack(boolean knockBack) {
        this.knockBack = knockBack;
    }

    public int getMomentum() {
        return momentum;
    }

    public void setMomentum(int momentum) {
        this.momentum = momentum;
    }

    public int getKnockBackDuration() {
        return knockBackDuration;
    }

    public void setKnockBackDuration(int knockBackDuration) {
        this.knockBackDuration = knockBackDuration;
    }

    public boolean reduceKnockBackDuration() {
        knockBackDuration--;
        return knockBackDuration == 0;
    }

    public boolean allowKnockBack(Game game, int viewDirection) {

        // up, down, left, right
        switch (viewDirection) {
            case 0 -> {
                if (game.getCurrentLevel().isSolid(getPositionX(), getPositionY() - (int) (getMomentum() * factor[15 - getKnockBackDuration()])) &&
                        game.getCurrentLevel().isSolid(getPositionX() + GamePanel.NEW_TILE_SIZE, getPositionY() - (int) (getMomentum() * factor[15 - getKnockBackDuration()]))) {
                    setPositionY(getPositionY() - (int) (getMomentum() * factor[15 - getKnockBackDuration()]));
                    return true;
                } else {
                    return false;
                }
            }
            case 1 -> {
                if (game.getCurrentLevel().isSolid(getPositionX(), getPositionY() + (int) (getMomentum() * factor[15 - getKnockBackDuration()]) + GamePanel.NEW_TILE_SIZE) &&
                        game.getCurrentLevel().isSolid(getPositionX() + GamePanel.NEW_TILE_SIZE, getPositionY() + (int) (getMomentum() * factor[15 - getKnockBackDuration()]) + GamePanel.NEW_TILE_SIZE)) {
                    setPositionY(this.getPositionY() + (int) (getMomentum() * factor[15 - getKnockBackDuration()]));
                    return true;
                } else {
                    return false;
                }
            }
            case 2 -> {
                if (game.getCurrentLevel().isSolid(getPositionX() - (int) (getMomentum() * factor[15 - getKnockBackDuration()]), getPositionY()) &&
                        game.getCurrentLevel().isSolid(getPositionX() - (int) (getMomentum() * factor[15 - getKnockBackDuration()]), getPositionY() + GamePanel.NEW_TILE_SIZE)) {
                    setPositionX(getPositionX() - (int) (getMomentum() * factor[15 - getKnockBackDuration()]));
                    return true;
                } else {
                    return false;
                }
            }
            case 3 -> {
                if (game.getCurrentLevel().isSolid(getPositionX() + (int) (getMomentum() * factor[15 - getKnockBackDuration()]) + GamePanel.NEW_TILE_SIZE, getPositionY()) &&
                        game.getCurrentLevel().isSolid(getPositionX() + (int) (getMomentum() * factor[15 - getKnockBackDuration()]) + GamePanel.NEW_TILE_SIZE, getPositionY() + GamePanel.NEW_TILE_SIZE)) {
                    setPositionX(getPositionX() + (int) (getMomentum() * factor[15 - getKnockBackDuration()]));
                    return true;
                } else {
                    return false;
                }
            }
        }
        return false;
    }

    public EntityType getEntityType() {
        return entityType;
    }

    public int getCoolDown() {
        return coolDown;
    }

    public void setCoolDown(int coolDown) {
        this.coolDown = coolDown;
    }

    public int getAttackDelay() {
        return attackDelay;
    }

    public void setAttackDelay(int attackDelay) {
        this.attackDelay = attackDelay;
    }

    public void setAttackDamage(int damage) {
        this.attackDamage = damage;
    }

    public int getAttackDamage() {
        return this.attackDamage;
    }

    public void setBlockAmount(int blockAmount) {
        this.blockAmount = blockAmount;
    }

    public int getBlockAmount() {
        return this.blockAmount;
    }

    public int getMaxHealthPoints() {
        return maxHealthPoints;
    }

    public void setMaxHealthPoints(int maxHealthPoints) {
        this.maxHealthPoints = maxHealthPoints;
    }

    public int getPositionX() {
        return positionX;
    }

    public void setPositionX(int positionX) {
        this.positionX = positionX;
    }

    public int getPositionY() {
        return positionY;
    }

    public void setPositionY(int positionY) {
        this.positionY = positionY;
    }

    public int getMovementSpeed() {
        return movementSpeed;
    }

    public void setMovementSpeed(int movementSpeed) {
        this.movementSpeed = movementSpeed;
    }

    public int getCurrentHealthPoints() {
        return currentHealthPoints;
    }

    public void setCurrentHealthPoints(int healthPoints) {
        if (healthPoints <= 0) currentHealthPoints = 0;
        else currentHealthPoints = healthPoints;
    }

    public void setCurrentAnimationType(String currentAnimationType) {
        this.currentAnimationType = currentAnimationType;
    }

    public String getCurrentAnimationType() {
        return currentAnimationType;
    }

    public void setCurrentFrame(int currentFrame) {
        this.currentFrame = currentFrame;
    }

    public int getCurrentFrame() {
        return currentFrame;
    }

    public AnimationFrame[] getEntityFrames(String animationName) {
        return entityFrames.get(animationName);
    }

    public int getAnimationFrameLimit(String animationType) {
        return entityFrames.get(animationType).length / (getAnimationDirections());
    }

    public int getAnimationDirections() {

        return switch (getCurrentAnimationType()) {
            case "resting" -> restingAnimationDirections;
            case "walking" -> walkingAnimationDirections;
            case "attacking" -> attackAnimationDirections;
            case "dying" -> dyingAnimationDirections;
            default -> 1;
        };
    }

    public void adaptViewDirection(int direction) {

        // up, down, left, right
        switch (direction) {
            case 0 -> setViewDirection(getAnimationFrameLimit(getCurrentAnimationType()) * 3);
            case 1 -> setViewDirection(0);
            case 2 -> setViewDirection(getAnimationFrameLimit(getCurrentAnimationType()));
            case 3 -> setViewDirection(getAnimationFrameLimit(getCurrentAnimationType()) * 2);
        }
    }

    public void setCurrentAnimationFrame(int currentFrame) {

        int factor = getViewDirection() / (getEntityFrames(getCurrentAnimationType()).length /
                        getAnimationDirections());
        setCurrentFrame(currentFrame + getAnimationFrameLimit(getCurrentAnimationType()) * factor);
    }

    public void loadAnimationFrames(String entityType) {

        // Read the file into a JsonObject
        ResourceLoader rl = ResourceLoader.getResourceLoader();
        JsonObject root = rl.readStaticJsonFile("animationFrames.json");

        // Accesses the entity object (character/skeleton/..)
        JsonObject entity = root.getAsJsonObject(entityType);

        // Loops through every entity's animation types
        for (String animationName : entity.keySet()) {

            // type -> walking/attack/dying
            JsonObject animationType = entity.getAsJsonObject(animationName);

            // An array to hold all frames of one animation type
            // The size is the amount of animations of one direction * number of directions
            // to store all direction's frames
            AnimationFrame[] animationFrames =
                    new AnimationFrame[animationType.getAsJsonArray("down").size() * animationType.keySet().size()];

            if (animationName.equals(AnimationType.walking.toString())) {
                walkingAnimationDirections = animationType.keySet().size();
            } else if (animationName.equals(AnimationType.attacking.toString())) {
                attackAnimationDirections = animationType.keySet().size();
            } else if (animationName.equals(AnimationType.dying.toString())) {
                dyingAnimationDirections = animationType.keySet().size();
            } else if (animationName.equals(AnimationType.resting.toString())) {
                restingAnimationDirections = animationType.keySet().size();
            }

            int width, height, xOffset, yOffset, index = 0;

            // Loops through the "down", "up", "left" and "right" arrays
            for (String direction : animationType.keySet()) {

                // Holds all frames from a direction
                JsonArray frames = animationType.getAsJsonArray(direction);

                // Loops through every frame of the direction, creates an AnimationFrame
                // with the values and saves it to the according frame array
                for (JsonElement element : frames) {
                    JsonObject frame = element.getAsJsonObject();

                    Image image = rl.getImage(frame.get("image").getAsString());
                    width = frame.get("width").getAsInt();
                    height = frame.get("height").getAsInt();
                    xOffset = frame.get("xOffset").getAsInt();
                    yOffset = frame.get("yOffset").getAsInt();

                    animationFrames[index] = new AnimationFrame(image, width, height, xOffset, yOffset);
                    index++;
                }
            }
            entityFrames.put(animationName, animationFrames);
        }
        setCurrentAnimationType(AnimationType.walking.toString());
    }

    public abstract void draw(Graphics2D graph2D, Game game) throws IOException;


    private class AnimationHandler implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            handleAnimation();
        }
    }
    protected abstract void handleAnimation();
    protected abstract void endRestingAnimation();
    protected abstract void endWalkingAnimation();
    protected abstract void endAttackingAnimation();
    protected abstract void endDyingAnimation();

    protected boolean proceedAnimationFrame(String animationType, int frameLimit) {

        int animationFrame = switch (animationType) {
            case "resting" -> restingAnimationFrame++;
            case "walking" -> walkingAnimationFrame++;
            case "attacking" -> attackAnimationFrame++;
            case "dying" -> dyingAnimationFrame++;
            default -> 0;
        };
        return animationFrame + 1 >= frameLimit;
    }
}
