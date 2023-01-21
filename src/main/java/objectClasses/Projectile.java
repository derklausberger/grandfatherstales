package objectClasses;

import GUI.GamePanel;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

public class Projectile {
    private int direction;
    private int x;
    private int y;
    private int imageWidth;
    private int imageHeight;

    public Projectile(int x, int y, int direction) {

        this.x = x;
        this.y = y;
        this.direction = direction;

        if (direction == 0 || direction == 27) {
            this.x += 10;
            imageWidth = 5;
            imageHeight = 31;
        } else {
            this.y += 20;
            imageWidth = 31;
            imageHeight = 5;
        }
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void move() {
        int movementSpeed = 7;
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

        graph2D.drawImage(game.getProjectileImage(direction), x  + (int) ((GamePanel.WINDOW_WIDTH) / 2) - game.getPlayer().getPositionX(),
                y +  (int) ((GamePanel.WINDOW_HEIGHT) / 2) - game.getPlayer().getPositionY(), imageWidth, imageHeight, null);
    }
}
