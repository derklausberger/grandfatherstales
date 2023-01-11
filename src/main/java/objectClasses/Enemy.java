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

        int x = (int) ((GamePanel.WINDOW_WIDTH) / 2),
                y = (int) ((GamePanel.WINDOW_HEIGHT) / 2);

        // The current frame
        AnimationFrame frame = getEntityFrames(getCurrentAnimationType())[getCurrentFrame()];

        // Draws the character
        graph2D.drawImage(
                frame.getImage(),
                x + frame.getXOffset() - game.getPlayer().getPositionX() + this.getPositionX(),
                y + frame.getYOffset() - game.getPlayer().getPositionY() + this.getPositionY(),
                frame.getWidth(), frame.getHeight(), gamePanel
        );


        if(this.getCurrentHealthPoints() < this.getMaxHealthPoints()) {
            Shape healthBarOutside = new Rectangle2D.Double(
                    (int) (GamePanel.WINDOW_WIDTH / 2) - game.getPlayer().getPositionX() + this.getPositionX() - 1,
                    (int) (GamePanel.WINDOW_HEIGHT / 2) - game.getPlayer().getPositionY() + this.getPositionY() - 11,
                    (GamePanel.NEW_TILE_SIZE + 2) * 0.6,
                    3);


            graph2D.setPaint(Color.black);
            graph2D.draw(healthBarOutside);

            Shape healthBarInside = new Rectangle2D.Double(
                    (int) (GamePanel.WINDOW_WIDTH / 2) - game.getPlayer().getPositionX() + this.getPositionX(),
                    (int) (GamePanel.WINDOW_HEIGHT / 2) - game.getPlayer().getPositionY() + this.getPositionY() - 10,
                    ((double) GamePanel.NEW_TILE_SIZE / (double) this.getMaxHealthPoints() * (double) this.getCurrentHealthPoints()) * 0.6,
                    3);


            graph2D.setPaint(Color.RED);
            graph2D.fill(healthBarInside);
        }

        /*
        Shape offsetCollision = new Rectangle2D.Double(
                (int) (GamePanel.WINDOW_WIDTH / 2) - game.getPlayer().getPositionX() + this.getPositionX(),
                (int) (GamePanel.WINDOW_HEIGHT / 2) - game.getPlayer().getPositionY() + this.getPositionY(),
                GamePanel.NEW_TILE_SIZE,
                GamePanel.NEW_TILE_SIZE);

        graph2D.setColor(Color.ORANGE);
        graph2D.draw(offsetCollision);

         */
    }
}


