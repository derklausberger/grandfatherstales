package objectClasses;

import GUI.AnimationFrame;
import GUI.GamePanel;
import objectClasses.Abstract.Entity;
import objectClasses.Abstract.Item;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class Player extends Entity {
    private int life;
    private ArrayList<Item> items;

    public Player(int positionX, int positionY, int movementSpeed, int healthPoints, int life) {

        super(positionX, positionY, movementSpeed, healthPoints);
        super.loadAnimationFrames("character");

        this.life = life;
    }

    public void draw(Graphics2D graph2D, Game game, GamePanel gamePanel, int radius, int startAngle, int arcAngle, ArrayList<Entity> entityArrayList) {

        /*
        Arc2D arc = new Arc2D.Double();
        arc.setArcByCenter((Math.floorDiv(GamePanel.WINDOW_WIDTH, 2)),
                (Math.floorDiv(GamePanel.WINDOW_HEIGHT, 2)),
                radius, startAngle, arcAngle, Arc2D.PIE);

        graph2D.setColor(Color.red);
        graph2D.draw(arc);

         */

        Arc2D arc2D = new Arc2D.Double();
        arc2D.setArcByCenter(game.getPlayer().getPositionX(), game.getPlayer().getPositionY(), radius, startAngle, arcAngle, Arc2D.PIE);


        for (Entity entity : entityArrayList) {
            if (arc2D.contains(entity.getPositionX(), entity.getPositionY()) ||
                    arc2D.contains(entity.getPositionX() + GamePanel.NEW_TILE_SIZE, entity.getPositionY()) ||
                    arc2D.contains(entity.getPositionX(), entity.getPositionY() + GamePanel.NEW_TILE_SIZE) ||
                    arc2D.contains(entity.getPositionX() + GamePanel.NEW_TILE_SIZE, entity.getPositionY() + GamePanel.NEW_TILE_SIZE)) {
                if (!entity.equals(game.getPlayer())) {
                    entity.setCurrentHealthPoints(entity.getCurrentHealthPoints() - 1);
                    System.out.println("HIT HIT HIT HIT");
                }
                if (entity.getCurrentHealthPoints() <= 0) {
                    System.out.println("was deleted");
                    GamePanel.entityArrayList.remove(entity);
                    break; // -> Move to game then remove break;
                }
            }
        }
    }


    @Override
    public void draw(Graphics2D graph2D, Game game, GamePanel gamePanel) {

        int x = (int) ((GamePanel.WINDOW_WIDTH - GamePanel.NEW_TILE_SIZE) / 2),
                y = (int) ((GamePanel.WINDOW_HEIGHT - GamePanel.NEW_TILE_SIZE) / 2);

        // The current frame
        AnimationFrame frame = getEntityFrames(getCurrentAnimationType())[getCurrentFrame()];

        // Draws the character
        graph2D.drawImage(
                frame.getImage(),
                x + frame.getXOffset(), y + frame.getYOffset(),
                frame.getWidth(), frame.getHeight(), gamePanel
        );
    }
}
