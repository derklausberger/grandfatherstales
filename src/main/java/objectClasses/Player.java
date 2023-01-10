package objectClasses;

import GUI.AnimationFrame;
import GUI.GamePanel;
import objectClasses.Abstract.Entity;
import objectClasses.Abstract.Item;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Rectangle2D;
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

    public void draw(Graphics2D graph2D, Game game, GamePanel gamePanel, int width, int height, int startAngle, int arcAngle, ArrayList<Entity> entityArrayList) {

        Arc2D arc = new Arc2D.Double();
        Thread thread = new Thread(() -> {
            for (int i = 0; i < 5; i++) {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                //System.out.println("Thread Running iteration: " + i + " inside of: " + this.getClass().getSimpleName());
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        arc.setArc((double) (Math.floorDiv(GamePanel.WINDOW_WIDTH, 2) - Math.floorDiv(width, 2)),
                                (double) (Math.floorDiv(GamePanel.WINDOW_HEIGHT, 2) - Math.floorDiv(height, 2)),
                                (double) width, (double) height,
                                (double) startAngle, (double) arcAngle, Arc2D.PIE);

                        //graph2D.draw(arc);

                        GeneralPath path = new GeneralPath();
                        path.append(arc, false);
                        path.setWindingRule(GeneralPath.WIND_EVEN_ODD);

                        for (Entity entity : entityArrayList) {
                            if (arc.getX() + arc.getWidth() >= entity.getPositionX() &&
                                    arc.getX() <= entity.getPositionX() + GamePanel.NEW_TILE_SIZE &&
                                    arc.getY() + arc.getHeight() >= entity.getPositionY() &&
                                    arc.getY() <= entity.getPositionY() + GamePanel.NEW_TILE_SIZE)
                            {
                                if (!entity.equals(game.getPlayer())) {
                                    entity.setHealthPoints(entity.getHealthPoints() - 1);
                                    System.out.println("HIT HIT HIT HIT");
                                }
                                if (entity.getHealthPoints() <= 0) {
                                    System.out.println("was deleted");
                                    GamePanel.entityArrayList.remove(entity);
                                    break;
                                }
                            }
                        }

                        /*
                        BufferedImage bi = null;
                        try {
                            bi = (BufferedImage) new ImageIcon(ImageIO.read(new File("src/main/resources/Enemy.png"))).getImage();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        TexturePaint tp = new TexturePaint(bi, new Rectangle((Math.floorDiv(GamePanel.WINDOW_WIDTH, 2) - Math.floorDiv(width, 2)),(Math.floorDiv(GamePanel.WINDOW_HEIGHT, 2) - Math.floorDiv(height, 2)),width,height));
                        graph2D.setPaint(tp);

*/
                        graph2D.fill(path);
                        gamePanel.repaint();
                    }
                });
            }
            Thread.currentThread().interrupt();
        });

        thread.start();

    }


    @Override
    public void draw(Graphics2D graph2D, Game game, GamePanel gamePanel) {

        int x = (int) ((gamePanel.WINDOW_WIDTH - gamePanel.NEW_TILE_SIZE) / 2),
                y = (int) ((gamePanel.WINDOW_HEIGHT - gamePanel.NEW_TILE_SIZE) / 2);


        // The current frame
        AnimationFrame[] frames = getEntityFrames(getCurrentAnimationType());

        // Draws the character
        graph2D.drawImage(
                frames[getCurrentFrame()].getImage(),
                x + frames[getCurrentFrame()].getXOffset(), y  + frames[getCurrentFrame()].getYOffset(),
                frames[getCurrentFrame()].getWidth(), frames[getCurrentFrame()].getHeight(), gamePanel
        );

    /*
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

          */

    }
}
