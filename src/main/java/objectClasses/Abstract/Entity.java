package objectClasses.Abstract;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import GUI.AnimationFrame;
import GUI.GamePanel;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import objectClasses.Game;

import javax.imageio.ImageIO;

public abstract class Entity {

    private int positionX;
    private int positionY;
    private int movementSpeed;
    private int currentHealthPoints;
    private int maxHealthPoints;


    private ArrayList<BufferedImage> entityAppearance;


    private Map<String, AnimationFrame[]> entityFrames = new HashMap<>();
    private String currentAnimationType;
    private int currentFrame;


    public Entity(int positionX, int positionY, int movementSpeed, int healthPoints) {

        entityAppearance = new ArrayList<>();

        BufferedImage bi = null;

        try {
            bi = ImageIO.read(new File("src/main/resources/playerUp.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        assert false;
        entityAppearance.add(bi);

        this.positionX = positionX;
        this.positionY = positionY;
        this.movementSpeed = movementSpeed;
        this.currentHealthPoints = healthPoints;
        this.maxHealthPoints = healthPoints;
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

    public ArrayList<BufferedImage> getEntityAppearance() {
        return entityAppearance;
    }

    public void setCurrentAnimationType (String currentAnimationType) {
        this.currentAnimationType = currentAnimationType;
    }

    public String getCurrentAnimationType () {
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

    public void loadAnimationFrames(String entityType) {

        // Read the file into a JsonObject
        Gson gson = new Gson();
        JsonObject root = null;

        try {
            root = gson.fromJson(new FileReader("src/main/resources/animationFrames.json"), JsonObject.class);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        // Accesses the entity object (character/skeleton/orc/ ..)
        JsonObject entity = root.getAsJsonObject(entityType);

        // Loops through every entity's animation types
        for (String animationName : entity.keySet()) {

            // type -> walking/attack/dying
            JsonObject animationType = entity.getAsJsonObject(animationName);

            // An array to hold all frames of one animation type
            // The size is the amount of animations of one direction * 4
            // to store all four direction's frames
            AnimationFrame[] animationFrames =
                    new AnimationFrame[animationType.getAsJsonArray("down").size() * 4];

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
