package objectClasses;

import GUI.GamePanel;
import utilityClasses.InputHandler;

import java.awt.*;

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

        if (direction == 0 || direction == 1) {
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

    public int getDirection() {
        return direction;
    }

    public void move() {
        int movementSpeed = 7;
        if (direction == 0) {
            y -= movementSpeed;
        } else if (direction == 1) {
            y += movementSpeed;
        } else if (direction == 2) {
            x -= movementSpeed;
        } else {
            x += movementSpeed;
        }
    }

    public boolean checkPlayerCollision(int playerX, int playerY, int walkingDirection) {

        int arrowOffset = 5;
        int playerXLeft = playerX, playerXRight = playerX + 30;
        int playerYUp = playerY, playerYDown = playerY + 50;

        switch (getDirection()) {
            case 0, 1 -> {
                switch (walkingDirection) {
                    case 0, 1 -> playerXRight -= 12;
                    default -> playerXRight -= 18;
                }
                playerXLeft -= 15;
                playerYUp -= 40;
            }
            case 2, 3 -> {
                switch (walkingDirection) {
                    case 0, 1 -> playerXLeft -= 25;
                    default -> playerXLeft -= 35;
                }
                playerXRight -= 22;
                playerYUp -= 25;
            }
        }
        playerYDown -= 25;

        return this.x < playerXRight && this.x + arrowOffset > playerXLeft
                && this.y < playerYDown && this.y + arrowOffset > playerYUp;
    }

    public boolean outOfScreen() {
        return x <= -400 || x >= GamePanel.NEW_TILE_SIZE * 32 + 400
                || y <= -400 || y >= GamePanel.NEW_TILE_SIZE * 32 + 400;
    }

    public void draw(Graphics2D graph2D, Game game) {

        graph2D.drawImage(game.getProjectileImage(direction), x  + (int) ((GamePanel.WINDOW_WIDTH) / 2) - game.getPlayer().getPositionX(),
                y +  (int) ((GamePanel.WINDOW_HEIGHT) / 2) - game.getPlayer().getPositionY(), imageWidth, imageHeight, null);
    }
}
