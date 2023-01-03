package objectClasses.Abstract;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import GUI.GamePanel;
import objectClasses.Game;

import javax.imageio.ImageIO;

public abstract class Entity {

    private int positionX;
    private int positionY;
    private int movementSpeed;

    private int healthPoints;
    private int currentAppearance;
    private ArrayList<BufferedImage> entityAppearance;

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

    public int getHealthPoints() {
        return healthPoints;
    }

    public void setHealthPoints(int healthPoints) {
        this.healthPoints = healthPoints;
    }

    public int getCurrentAppearance() {
        return currentAppearance;
    }

    public void setCurrentAppearance(int currentAppearance) {
        this.currentAppearance = currentAppearance;
    }

    public ArrayList<BufferedImage> getEntityAppearance() {
        return entityAppearance;
    }

    public void setEntityAppearance(ArrayList<BufferedImage> entityAppearance) {
        this.entityAppearance = entityAppearance;
    }

    public Entity(int positionX, int positionY, int movementSpeed, int healthPoints,
                  ArrayList<BufferedImage> entityAppearance, int currentAppearance) {

        entityAppearance = new ArrayList<>();

        BufferedImage bi = null;
        BufferedImage bi2 = null;
        BufferedImage bi3 = null;
        BufferedImage bi4 = null;

        try {
            bi = ImageIO.read(new File("src/main/resources/playerUp.png"));
            bi2 = ImageIO.read(new File("src/main/resources/playerDown.png"));
            bi3 = ImageIO.read(new File("src/main/resources/playerLeft.png"));
            bi4 = ImageIO.read(new File("src/main/resources/playerRight.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        assert false;
        entityAppearance.add(bi);
        entityAppearance.add(bi2);
        entityAppearance.add(bi3);
        entityAppearance.add(bi4);

        this.positionX = positionX;
        this.positionY = positionY;
        this.movementSpeed = movementSpeed;
        this.healthPoints = healthPoints;
        this.entityAppearance = entityAppearance;
        this.currentAppearance = currentAppearance;
    }

    public abstract void draw(Graphics2D graph2D, Game game, GamePanel gamePanel) throws IOException;
}
