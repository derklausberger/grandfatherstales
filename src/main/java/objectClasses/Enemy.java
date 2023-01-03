package objectClasses;

import GUI.GamePanel;
import objectClasses.Abstract.Entity;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;


public class Enemy extends Entity {

    public Enemy(int positionX, int positionY, int movementSpeed, int healthPoints,
                 ArrayList<BufferedImage> entityAppearance, int currentAppearance) {

        super(positionX, positionY, movementSpeed, healthPoints, entityAppearance, currentAppearance);
    }

    @Override
    public void draw(Graphics2D graph2D, Game game, GamePanel gamePanel) throws IOException {

        /*
         graph2D.drawImage(
                (Image) this.entityAppearance.get(getCurrentAppearance()),
                (int) ((gamePanel.WINDOW_WIDTH - gamePanel.NEW_TILE_SIZE) / 2),
                (int) ((gamePanel.WINDOW_HEIGHT - gamePanel.NEW_TILE_SIZE) / 2),
                gamePanel.NEW_TILE_SIZE,
                gamePanel.NEW_TILE_SIZE,
                gamePanel
        );

        */

        graph2D.drawImage(
                (Image) ImageIO.read(new File("src/main/resources/Enemy2.png")),
                (int) (GamePanel.WINDOW_WIDTH / 2) - game.getPlayer().getPositionX() + this.getPositionX(),
                (int) (GamePanel.WINDOW_HEIGHT / 2) - game.getPlayer().getPositionY() + this.getPositionY(),
                GamePanel.NEW_TILE_SIZE,
                GamePanel.NEW_TILE_SIZE,
                gamePanel
        );


        Shape s = new Rectangle2D.Double(
                (int) (GamePanel.WINDOW_WIDTH / 2) - game.getPlayer().getPositionX() + this.getPositionX(),
                (int) (GamePanel.WINDOW_HEIGHT / 2) - game.getPlayer().getPositionY() + this.getPositionY(),
                GamePanel.NEW_TILE_SIZE,
                GamePanel.NEW_TILE_SIZE);

        graph2D.setColor(Color.ORANGE);
        graph2D.draw(s);
    }
}


