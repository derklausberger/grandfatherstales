package objectClasses.Abstract;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/* extends JPanel */
public abstract class Entity {

    private int positionX;
    private int positionY;
    private int movementSpeed;

    private int healthPoints;
    private int currentAppearance;
    private ArrayList<BufferedImage> entityAppearance;

    public void setEntityAppearance(ArrayList<BufferedImage> entityAppearance) {
        this.entityAppearance = entityAppearance;
    }

    public void setPositionX(int positionX) {
        this.positionX = positionX;
    }

    public void setPositionY(int positionY) {
        this.positionY = positionY;
    }

    public int getPositionX() {
        return positionX;
    }

    public int getPositionY() {
        return positionY;
    }

    public int getMovementSpeed() {
        return movementSpeed;
    }

    public int getHealthPoints() {
        return healthPoints;
    }

    public ArrayList<BufferedImage> getEntityAppearance() {
        return entityAppearance;
    }

    public int getCurrentAppearance() {
        return currentAppearance;
    }

    public void setCurrentAppearance(int currentAppearance) {
        this.currentAppearance = currentAppearance;
    }

    public Entity(int positionX, int positionY, int movementSpeed, int healthPoints, ArrayList<BufferedImage> entityAppearance, int currentAppearance) {
        this.positionX = positionX;
        this.positionY = positionY;
        this.movementSpeed = movementSpeed;
        this.healthPoints = healthPoints;
        this.entityAppearance = entityAppearance;
        this.currentAppearance = currentAppearance;
    }

    /*
    public void paintComponent(Graphics graph) {
        super.paintComponent(graph);
        Graphics2D graph2D = (Graphics2D) graph;
        System.out.println("Here - Entity");

        try {
            bi = ImageIO.read(new File("src/main/resources/item/armor/armor_type_chainmail.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        graph2D.drawImage(bi, positionX, positionY, 100, 100, this);
        graph2D.dispose();
    }

     */
}
