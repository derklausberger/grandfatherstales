package objectClasses;

import objectClasses.Abstract.Entity;
import objectClasses.Abstract.Item;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class Player extends Entity {
    private int life;
    //private List<Item> items; // should xposition be an attribute of player?

    public Player(int positionX, int positionY, int movementSpeed, int healthPoints, ArrayList<BufferedImage> entityAppearance, int life, int currentAppearance) {
        super(positionX, positionY, movementSpeed, healthPoints, entityAppearance, currentAppearance);
        this.life = life;
    }
}
