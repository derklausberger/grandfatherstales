package objectClasses;

import GUI.GamePanel;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

public class Projectile {
    private BufferedImage img;
    private int direction;
    private int x;
    private int y;

    public Projectile(int x, int y, int direction) {
        this.x = x;
        this.y = y;
        this.direction = direction;
        //if (direction == 0) {
            try {
                img = ImageIO.read(new File("src/main/resources/item/weapon/spear/spearCommon.png"));
            } catch (Exception e) {
                e.printStackTrace();
            }
        //}
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void move() {
        int movementSpeed = 2;
        if (direction == 0) {
            y += movementSpeed;
        } else if (direction == 9) {
            x -= movementSpeed;
        } else if (direction == 18) {
            x += movementSpeed;
        } else if (direction == 27) {
            y -= movementSpeed;
        }
    }

    public boolean outOfScreen() {
        if (x <= - 400 || x >= GamePanel.NEW_TILE_SIZE * 32 + 400
                || y <= -400 || y >= GamePanel.NEW_TILE_SIZE * 32 + 400) {
            return true;
        }

        return false;
    }

    public void draw(Graphics2D graph2D, Game game, GamePanel gamePanel) {
        System.out.println(x + ", " + y);
        graph2D.drawImage(img, x  + (int) ((GamePanel.WINDOW_WIDTH) / 2) - game.getPlayer().getPositionX(),
                y +  (int) ((GamePanel.WINDOW_HEIGHT) / 2) - game.getPlayer().getPositionY(), 30, 30, null);
    }
}
