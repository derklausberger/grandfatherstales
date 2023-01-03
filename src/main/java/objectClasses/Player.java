package objectClasses;

import GUI.GamePanel;
import objectClasses.Abstract.Entity;
import objectClasses.Abstract.Item;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class Player extends Entity {
    private int life;
    private ArrayList<Item> items;

    public Player(int positionX, int positionY, int movementSpeed, int healthPoints,
                  ArrayList<BufferedImage> entityAppearance, int life, int currentAppearance) {

        super(positionX, positionY, movementSpeed, healthPoints, entityAppearance, currentAppearance);

        this.life = life;
    }

    @Override
    public void draw(Graphics2D graph2D, Game game, GamePanel gamePanel) {


         graph2D.drawImage(
                (Image) this.getEntityAppearance().get(getCurrentAppearance()),
                (int) ((gamePanel.WINDOW_WIDTH - gamePanel.NEW_TILE_SIZE) / 2),
                (int) ((gamePanel.WINDOW_HEIGHT - gamePanel.NEW_TILE_SIZE) / 2),
                gamePanel.NEW_TILE_SIZE,
                gamePanel.NEW_TILE_SIZE,
                gamePanel
        );

        Shape s = new Rectangle2D.Double(
                (int) ((GamePanel.WINDOW_WIDTH - GamePanel.NEW_TILE_SIZE) / 2),
                (int) ((GamePanel.WINDOW_HEIGHT - GamePanel.NEW_TILE_SIZE) / 2),
                GamePanel.NEW_TILE_SIZE,
                GamePanel.NEW_TILE_SIZE);

        graph2D.setColor(Color.red);
        graph2D.draw(s);

        Shape s3 = new Rectangle2D.Double(
                (int) ((GamePanel.WINDOW_WIDTH - GamePanel.NEW_TILE_SIZE) / 2) + Math.floorDiv(GamePanel.NEW_TILE_SIZE, 6),
                (int) ((GamePanel.WINDOW_HEIGHT - GamePanel.NEW_TILE_SIZE) / 2) + Math.floorDiv(GamePanel.NEW_TILE_SIZE, 6),
                Math.floorDiv(GamePanel.NEW_TILE_SIZE, 3) * 2,
                Math.floorDiv(GamePanel.NEW_TILE_SIZE, 3) * 2);

        graph2D.setColor(Color.red);
        graph2D.draw(s3);


        Shape s2 = new Ellipse2D.Double(
                (game.getPlayer().getPositionX() - (int) Math.floorDiv(GamePanel.NEW_TILE_SIZE, 2)),
                (game.getPlayer().getPositionY() - (int) Math.floorDiv(GamePanel.NEW_TILE_SIZE, 2)),
                GamePanel.NEW_TILE_SIZE,
                GamePanel.NEW_TILE_SIZE);

        graph2D.setColor(Color.blue);
        graph2D.draw(s2);

    }
}
