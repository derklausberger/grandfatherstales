package objectClasses.Abstract;

import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import objectClasses.Enum.AnimationType;
import utilityClasses.AnimationFrame;
import GUI.GamePanel;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import objectClasses.Enum.EntityType;
import objectClasses.Game;
import objectClasses.KnockBack;
import utilityClasses.ResourceLoader;

import javax.imageio.ImageIO;

public abstract class Entity implements KnockBack {

    private EntityType entityType;
    private int maxHealthPoints;
    private int currentHealthPoints;

    private int positionX;
    private int positionY;
    private int movementSpeed;

    private int momentum;
    private int knockBackDuration;
    private boolean knockBack = false;

    private int attackDamage; // damage and amount store the combined value of all items
    private int blockAmount; // -> Easier to implement possible temporary buffs

    private int cooldown; // cooldown -> time between attacks // For enemies, cooldown also determines // when it can move again after attacking // Value is actively set and reduced
    private int attackDelay; // attackDelay -> time between attacks // A value for the cooldown to refer to // that is set for all entities after creation

    private Map<String, AnimationFrame[]> entityFrames = new HashMap<>(); // entityFrames -> holds all frames of all animations // currentAnimationType -> indicates the current type // currentFrame -> indicates the current frame of the current animation
    private String currentAnimationType;
    private int currentFrame;

    private int
            walkingAnimationDirections,
            attackAnimationDirections,
            dyingAnimationDirections;

    private int viewDirection;


    public Entity(int positionX, int positionY, int movementSpeed, int healthPoints, EntityType entityType, int viewDirection) {

        this.positionX = positionX;
        this.positionY = positionY;
        this.movementSpeed = movementSpeed;

        this.currentHealthPoints = healthPoints;
        this.maxHealthPoints = healthPoints;
        this.entityType = entityType;

        this.viewDirection = 0;

        loadAnimationFrames(entityType.toString());
    }

    public int getViewDirection() {
        return viewDirection;
    }

    public void setViewDirection(int viewDirection) {
        this.viewDirection = viewDirection;
    }

    public boolean isKnockBack() {
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

    public void reduceCooldown() {

        if (cooldown < 0) {
            cooldown = 0;
        } else {
            cooldown--;
        }
    }

    public EntityType getEntityType() {
        return entityType;
    }

    public void setEntityType(EntityType entityType) {
        this.entityType = entityType;
    }

    public int getCooldown() {
        return cooldown;
    }

    public void setCooldown(int cooldown) {
        this.cooldown = cooldown;
    }

    public int getAttackDelay() {
        return attackDelay;
    }

    public void setAttackDelay(int attackDelay) {
        this.attackDelay = attackDelay;
    }

    // Weapon functions
    public void setAttackDamage(int damage) {
        this.attackDamage = damage;
    }

    public int getAttackDamage() {
        return this.attackDamage;
    }

    // Armor functions
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

        int directions = 1;
        switch (getCurrentAnimationType()) {
            case "walking" -> directions = walkingAnimationDirections;
            case "attacking" -> directions = attackAnimationDirections;
            case "dying" -> directions = dyingAnimationDirections;
        }
        return directions;
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

                    Image image = null;
                    try {
                        image = ImageIO.read(new File(frame.get("image").getAsString()));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
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
        setCurrentAnimationType("walking");
    }

    public abstract void draw(Graphics2D graph2D, Game game, GamePanel gamePanel) throws IOException;
}