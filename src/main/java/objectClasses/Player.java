package objectClasses;

import GUI.GamePanel;
import objectClasses.Abstract.Entity;
import objectClasses.Abstract.Item;

import java.awt.*;
import java.awt.geom.Arc2D;
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


    public void draw(Graphics2D graph2D, Game game, GamePanel gamePanel, int width, int height, int startAngle, int arcAngle, ArrayList<Entity> entityArrayList) {

        Arc2D arc = new Arc2D.Double();
        arc.setArc((double) (Math.floorDiv(GamePanel.WINDOW_WIDTH, 2) - Math.floorDiv(width, 2)),
                (double) (Math.floorDiv(GamePanel.WINDOW_HEIGHT, 2) - Math.floorDiv(height, 2)),
                (double) width, (double) height,
                (double) startAngle, (double) arcAngle, Arc2D.PIE);

        graph2D.draw(arc);

        Arc2D arc2 = new Arc2D.Double();
        arc.setArc((double) (game.getPlayer().getPositionX() - Math.floorDiv(GamePanel.NEW_TILE_SIZE, 2)),
                (double) (game.getPlayer().getPositionY() - Math.floorDiv(GamePanel.NEW_TILE_SIZE, 2)),
                (double) width, (double) height,
                (double) startAngle, (double) arcAngle, Arc2D.PIE);

        for (Entity entity : entityArrayList) {
            // arc.intersects(entity.getPositionX() + (Math.floorDiv(GamePanel.WINDOW_WIDTH, 2) - Math.floorDiv(width, 2)

            //System.out.println(arc.getX() + "           " + arc.getY());
            //System.out.println(entity.getPositionX() + "           "  + entity.getPositionY());

            /*
            if (arc.getX() + arc.getWidth() >= entity.getPositionX() &&
                    arc.getX() <= entity.getPositionX() + GamePanel.NEW_TILE_SIZE &&
                    arc.getY() + arc.getHeight() >= entity.getPositionY() &&
                    arc.getY() <= entity.getPositionY() + GamePanel.NEW_TILE_SIZE) {
                System.out.println("hit");
            }

             */

            // arc.intersects(new Rectangle(entity.getPositionX(), entity.getPositionY(), GamePanel.NEW_TILE_SIZE, GamePanel.NEW_TILE_SIZE)
            if(arc.getX() + arc.getWidth() >= entity.getPositionX() &&
                    arc.getX() <= entity.getPositionX() + GamePanel.NEW_TILE_SIZE &&
                    arc.getY() + arc.getHeight() >= entity.getPositionY() &&
                    arc.getY() <= entity.getPositionY() + GamePanel.NEW_TILE_SIZE)
            {
                if (!entity.equals(game.getPlayer())) {
                    System.out.println("HIT HIT HIT HIT HIT HIT");
                    entity.setHealthPoints(entity.getHealthPoints() - 1);
                }
                if (entity.getHealthPoints() <= 0) {
                    entityArrayList.remove(entity);
                    break;
                }
            }
        }
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

         /*
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

          */

    }
}
