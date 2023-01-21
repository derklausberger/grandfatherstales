package objectClasses.Abstract;

import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import GUI.AnimationFrame;
import GUI.GamePanel;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import objectClasses.Enum.EntityType;
import objectClasses.Game;
import objectClasses.KnockBack;

import javax.imageio.ImageIO;

public abstract class Entity implements KnockBack {

    private int positionX;
    private int positionY;
    private int movementSpeed;

    private EntityType entityType;
    private boolean knockBack = false;
    private int momentum;
    private int knockBackDuration;

    // damage and amount store the combined value of all items
    // -> Easier to implement possible temporary buffs
    private int attackDamage;
    private int blockAmount;

    // cooldown -> time between attacks
    // For enemies, cooldown also determines
    // when it can move again after attacking
    // Value is actively set and reduced
    private int cooldown;

    // attackDelay -> time between attacks
    // A value for the cooldown to refer to,
    // that is set for all entities after creation
    private int attackDelay;

    private int maxHealthPoints;
    private int currentHealthPoints;

    // entityFrames -> holds all frames of all animations
    // currentAnimationType -> indicates the current type
    // currentFrame -> indicates the current frame of the current animation
    private Map<String, AnimationFrame[]> entityFrames = new HashMap<>();
    private String currentAnimationType;
    private int currentFrame;

    // Indicates the number of frames for one
    // direction of the attack animation,
    // because it differs between enemies
    private int attackAnimationFrameLimit;

    public Entity(int positionX, int positionY, int movementSpeed, int healthPoints, EntityType entityType) {

        this.positionX = positionX;
        this.positionY = positionY;
        this.movementSpeed = movementSpeed;

        this.currentHealthPoints = healthPoints;
        this.maxHealthPoints = healthPoints;
        this.entityType = entityType;

        loadAnimationFrames(entityType.toString());
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
        this.currentHealthPoints = healthPoints;
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

    public int getAttackAnimationFrameLimit() {
        return attackAnimationFrameLimit;
    }

    public void loadAnimationFrames(String entityType) {

        // Read the file into a JsonObject
        Gson gson = new Gson();
        JsonObject root = null;

        try {
            root = gson.fromJson(new FileReader("src/main/resources/jsonFiles/animationFrames.json"), JsonObject.class);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

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

            int width, height, xOffset, yOffset, index = 0;

            // Loops through the "down", "up", "left" and "right" arrays
            for (String direction : animationType.keySet()) {

                // Holds all frames from a direction
                JsonArray frames = animationType.getAsJsonArray(direction);

                if (animationName.equals("attacking")) {
                    attackAnimationFrameLimit = frames.size();
                }

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