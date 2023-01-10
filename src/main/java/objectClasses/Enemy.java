package objectClasses;

import GUI.AnimationFrame;
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

    public Enemy(int positionX, int positionY, int movementSpeed, int healthPoints) {

        super(positionX, positionY, movementSpeed, healthPoints);

        super.loadAnimationFrames("skeletonWarrior");
    }

    /*

    onsight()

    */

    @Override
    public void draw(Graphics2D graph2D, Game game, GamePanel gamePanel) {


        int x = (int) ((gamePanel.WINDOW_WIDTH - gamePanel.NEW_TILE_SIZE) / 2),
                y = (int) ((gamePanel.WINDOW_HEIGHT - gamePanel.NEW_TILE_SIZE) / 2);

        // The current frame
        AnimationFrame frame = getEntityFrames(getCurrentAnimationType())[getCurrentFrame()];

        // Draws the character
        graph2D.drawImage(
                frame.getImage(),
                x + frame.getXOffset() - game.getPlayer().getPositionX() + this.getPositionX(),
                y  + frame.getYOffset() - game.getPlayer().getPositionY() + this.getPositionY(),
                frame.getWidth(), frame.getHeight(), gamePanel
        );

       /*

        graph2D.drawImage(
                this.getEntityAppearance().get(0),
                (int) (GamePanel.WINDOW_WIDTH / 2) - game.getPlayer().getPositionX() + this.getPositionX(),
                (int) (GamePanel.WINDOW_HEIGHT / 2) - game.getPlayer().getPositionY() + this.getPositionY(),
                GamePanel.NEW_TILE_SIZE,
                GamePanel.NEW_TILE_SIZE,
                gamePanel
        );
*/


        /*
        Shape s = new Rectangle2D.Double(
                (int) (GamePanel.WINDOW_WIDTH / 2) - game.getPlayer().getPositionX() + this.getPositionX(),
                (int) (GamePanel.WINDOW_HEIGHT / 2) - game.getPlayer().getPositionY() + this.getPositionY(),
                GamePanel.NEW_TILE_SIZE,
                GamePanel.NEW_TILE_SIZE);

        graph2D.setColor(Color.ORANGE);
        graph2D.draw(s);

         */
    }
}


