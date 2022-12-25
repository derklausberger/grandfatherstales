package objectClasses;

import objectClasses.Abstract.Entity;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class Enemy extends Entity {

    public Enemy(int positionX, int positionY, int movementSpeed, int healthPoints, ArrayList<BufferedImage> entityAppearance, int currentAppearance) {
        super(positionX, positionY, movementSpeed, healthPoints, entityAppearance, currentAppearance);
    }
}
