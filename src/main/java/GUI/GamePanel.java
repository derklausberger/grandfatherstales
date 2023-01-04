package GUI;

import com.sun.tools.javac.Main;
import objectClasses.Abstract.Entity;
import objectClasses.Enemy;
import objectClasses.Game;
import objectClasses.Player;
import org.xml.sax.SAXException;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.xml.parsers.ParserConfigurationException;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.geom.Arc2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.awt.Shape;


public class GamePanel extends JPanel implements Runnable {

    static final int SCALE_FACTOR = 3; // 16 x 16 won't actually be displayed as 16 x 16
    static final int ORIGINAL_TILE_SIZE = 16; // 16 x 16 pixel
    public static final int NEW_TILE_SIZE = ORIGINAL_TILE_SIZE * SCALE_FACTOR;

    static final int MAX_SCREEN_COL = 16; // max. 16 tiles in x
    static final int MAX_SCREEN_ROW = 12; // max. 12 tiles in y

    public static final int WINDOW_WIDTH = NEW_TILE_SIZE * MAX_SCREEN_COL; // 768 pixel
    public static final int WINDOW_HEIGHT = NEW_TILE_SIZE * MAX_SCREEN_ROW; // 576 pixel

    InputHandler keyHandler = new InputHandler(); // own class
    Thread playerThread = null;
    Image backgroundImage, newBackgroundImage;

    int FPS = 60;
    // needed because repaint() is called depending
    // on the frames per second -> thus we need to
    // regulate it


    private final Game game;
    public static ArrayList<Entity> entityArrayList = new ArrayList<>();

    public GamePanel() throws IOException, ParserConfigurationException, SAXException {
        game = new Game(new Player((int) ((WINDOW_WIDTH) / 2), (int) ((WINDOW_HEIGHT) / 2), 3, 5, null, 3, 1));

        this.setPreferredSize(new Dimension(WINDOW_WIDTH, WINDOW_HEIGHT));
        this.setBackground(new Color(39, 105, 195));
        this.addKeyListener(keyHandler); // adds Listener
        this.setDoubleBuffered(true); // improves rendering
        this.setFocusable(true); // GamePanel "focused" to receive key input

        entityArrayList.add(game.getPlayer());

        for (Enemy e : game.getCurrentLevel().getEnemies()) {
            entityArrayList.add(e);
        }


        backgroundImage = null;
        try {
            backgroundImage = ImageIO.read(new File("src/main/resources/map/4.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        assert backgroundImage != null;
        newBackgroundImage = backgroundImage.getScaledInstance(512 * SCALE_FACTOR, 512 * SCALE_FACTOR, Image.SCALE_FAST);



        startPlayerThread();
    }

    public void startPlayerThread() {
        playerThread = new Thread(this);
        playerThread.start();
    }

    @Override
    public void run() {

        long drawInterval = 1000000000 / FPS;
        long nextDrawTime = System.nanoTime() + drawInterval;

        while (playerThread != null) {

            try {
                update(); // updates: character positions, etc...
            } catch (IOException e) {
                e.printStackTrace();
            }
            repaint(); // actually calls paintComponent() -> noShit, is confusing

            try {
                long remainingTime = nextDrawTime - System.nanoTime();
                remainingTime = remainingTime / 1000000; // from nano to millis

                if (remainingTime < 0) {
                    remainingTime = 0;
                }

                Thread.sleep(remainingTime);
                nextDrawTime += drawInterval;

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void update() throws IOException {

        /*
        if (keyHandler.upPressed && (game.getCurrentLevel().isSolid(game.getPlayer().getPositionX(), game.getPlayer().getPositionY() - game.getPlayer().getMovementSpeed())
                && (game.getCurrentLevel().isSolid(game.getPlayer().getPositionX() + Math.floorDiv(NEW_TILE_SIZE, 3), game.getPlayer().getPositionY() - game.getPlayer().getMovementSpeed()) ||
                (game.getCurrentLevel().isSolid(game.getPlayer().getPositionX() - Math.floorDiv(NEW_TILE_SIZE, 3), game.getPlayer().getPositionY() + game.getPlayer().getMovementSpeed())))))

        {
            game.getPlayer().setPositionY(game.getPlayer().getPositionY() - game.getPlayer().getMovementSpeed());
            game.getPlayer().setCurrentAppearance(0);
        }

        if (keyHandler.downPressed && (game.getCurrentLevel().isSolid(game.getPlayer().getPositionX(), game.getPlayer().getPositionY() + game.getPlayer().getMovementSpeed())
                && (game.getCurrentLevel().isSolid(game.getPlayer().getPositionX() - Math.floorDiv(NEW_TILE_SIZE, 3), game.getPlayer().getPositionY() + game.getPlayer().getMovementSpeed()) ||
                (game.getCurrentLevel().isSolid(game.getPlayer().getPositionX() + Math.floorDiv(NEW_TILE_SIZE, 3), game.getPlayer().getPositionY() - game.getPlayer().getMovementSpeed())))))

        {
            game.getPlayer().setPositionY(game.getPlayer().getPositionY() + game.getPlayer().getMovementSpeed());
            game.getPlayer().setCurrentAppearance(1);
        }

        if (keyHandler.leftPressed && (game.getCurrentLevel().isSolid(game.getPlayer().getPositionX() - game.getPlayer().getMovementSpeed(), game.getPlayer().getPositionY())
                && (game.getCurrentLevel().isSolid(game.getPlayer().getPositionX() + Math.floorDiv(NEW_TILE_SIZE, 3), game.getPlayer().getPositionY() - game.getPlayer().getMovementSpeed())) ||
                (game.getCurrentLevel().isSolid(game.getPlayer().getPositionX() - Math.floorDiv(NEW_TILE_SIZE, 3), game.getPlayer().getPositionY() + game.getPlayer().getMovementSpeed()))))

        {
            game.getPlayer().setPositionX(game.getPlayer().getPositionX() - game.getPlayer().getMovementSpeed());
            game.getPlayer().setCurrentAppearance(2);

        }

        if (keyHandler.rightPressed && (game.getCurrentLevel().isSolid(game.getPlayer().getPositionX() + game.getPlayer().getMovementSpeed(), game.getPlayer().getPositionY())
                && (game.getCurrentLevel().isSolid(game.getPlayer().getPositionX() - Math.floorDiv(NEW_TILE_SIZE, 3), game.getPlayer().getPositionY() + game.getPlayer().getMovementSpeed()) ||
                (game.getCurrentLevel().isSolid(game.getPlayer().getPositionX() + Math.floorDiv(NEW_TILE_SIZE, 3), game.getPlayer().getPositionY() - game.getPlayer().getMovementSpeed())))))

        {
            game.getPlayer().setPositionX(game.getPlayer().getPositionX() + game.getPlayer().getMovementSpeed());
            game.getPlayer().setCurrentAppearance(3);
        }
         */


        if (keyHandler.upPressed
                && game.getCurrentLevel().isSolid(game.getPlayer().getPositionX() + Math.floorDiv(NEW_TILE_SIZE, 3), game.getPlayer().getPositionY() - game.getPlayer().getMovementSpeed() - Math.floorDiv(NEW_TILE_SIZE, 3)) &&
                game.getCurrentLevel().isSolid(game.getPlayer().getPositionX() - Math.floorDiv(NEW_TILE_SIZE, 3), game.getPlayer().getPositionY() - game.getPlayer().getMovementSpeed() - Math.floorDiv(NEW_TILE_SIZE, 3))) {
            game.getPlayer().setPositionY(game.getPlayer().getPositionY() - game.getPlayer().getMovementSpeed());
            game.getPlayer().setCurrentAppearance(0);
        } else if (keyHandler.downPressed
                && game.getCurrentLevel().isSolid(game.getPlayer().getPositionX() - Math.floorDiv(NEW_TILE_SIZE, 3), game.getPlayer().getPositionY() + game.getPlayer().getMovementSpeed() + Math.floorDiv(NEW_TILE_SIZE, 3)) &&
                game.getCurrentLevel().isSolid(game.getPlayer().getPositionX() + Math.floorDiv(NEW_TILE_SIZE, 3), game.getPlayer().getPositionY() + game.getPlayer().getMovementSpeed() + Math.floorDiv(NEW_TILE_SIZE, 3))) {
            game.getPlayer().setPositionY(game.getPlayer().getPositionY() + game.getPlayer().getMovementSpeed());
            game.getPlayer().setCurrentAppearance(1);
        } else if (keyHandler.leftPressed
                && game.getCurrentLevel().isSolid(game.getPlayer().getPositionX() - game.getPlayer().getMovementSpeed() - Math.floorDiv(NEW_TILE_SIZE, 3), game.getPlayer().getPositionY() + Math.floorDiv(NEW_TILE_SIZE, 3)) &&
                game.getCurrentLevel().isSolid(game.getPlayer().getPositionX() - game.getPlayer().getMovementSpeed() - Math.floorDiv(NEW_TILE_SIZE, 3), game.getPlayer().getPositionY() - Math.floorDiv(NEW_TILE_SIZE, 3))) {
            game.getPlayer().setPositionX(game.getPlayer().getPositionX() - game.getPlayer().getMovementSpeed());
            game.getPlayer().setCurrentAppearance(2);

        } else if (keyHandler.rightPressed
                && game.getCurrentLevel().isSolid(game.getPlayer().getPositionX() + game.getPlayer().getMovementSpeed() + Math.floorDiv(NEW_TILE_SIZE, 3), game.getPlayer().getPositionY() - Math.floorDiv(NEW_TILE_SIZE, 3)) &&
                game.getCurrentLevel().isSolid(game.getPlayer().getPositionX() + game.getPlayer().getMovementSpeed() + Math.floorDiv(NEW_TILE_SIZE, 3), game.getPlayer().getPositionY() + Math.floorDiv(NEW_TILE_SIZE, 3))) {
            game.getPlayer().setPositionX(game.getPlayer().getPositionX() + game.getPlayer().getMovementSpeed());
            game.getPlayer().setCurrentAppearance(3);
        }


        int startAngle = 0;
        int arcAngle = 0;

        if (keyHandler.attackPressed
                && cooldown == 0) {
            switch (keyHandler.lastPressed) {
                case (KeyEvent.VK_W) -> {
                    cooldown = 5;
                    startAngle = 45;
                    arcAngle = 90;

                    int width = 200;
                    int height = 200;

                    game.getPlayer().draw((Graphics2D) this.getGraphics(), game, this, width, height, startAngle, arcAngle, entityArrayList);

                }
                case (KeyEvent.VK_A) -> {
                    cooldown = 5;
                    startAngle = 135;
                    arcAngle = 90;

                    int width = 200;
                    int height = 200;

                    game.getPlayer().draw((Graphics2D) this.getGraphics(), game, this, width, height, startAngle, arcAngle, entityArrayList);
                }
                case (KeyEvent.VK_S) -> {
                    cooldown = 5;
                    startAngle = 225;
                    arcAngle = 90;

                    int width = 200;
                    int height = 200;

                    game.getPlayer().draw((Graphics2D) this.getGraphics(), game, this, width, height, startAngle, arcAngle, entityArrayList);
                }
                case (KeyEvent.VK_D) -> {
                    cooldown = 5;
                    startAngle = 315;
                    arcAngle = 90;

                    int width = 200;
                    int height = 200;

                    game.getPlayer().draw((Graphics2D) this.getGraphics(), game, this, width, height, startAngle, arcAngle, entityArrayList);
                }
            }
        }
    }


    int cooldown = 0;

    @Override
    public void paint(Graphics graph) {
        super.paintComponent(graph);
        Graphics2D graph2D = (Graphics2D) graph;

        /*
        int startAngle = 0;
        int arcAngle = 0;

        if (keyHandler.attackPressed
                && cooldown == 0) {
            switch (keyHandler.lastPressed) {
                case (KeyEvent.VK_W) -> {
                    cooldown = 60;
                    startAngle = 45;
                    arcAngle = 90;
                }
                case (KeyEvent.VK_A) -> {
                    cooldown = 60;
                    startAngle = 135;
                    arcAngle = 90;
                }
                case (KeyEvent.VK_S) -> {
                    cooldown = 60;
                    startAngle = 225;
                    arcAngle = 90;
                }
                case (KeyEvent.VK_D) -> {
                    cooldown = 60;
                    startAngle = 315;
                    arcAngle = 90;
                }
            }
        }

         */

        if (cooldown < 0) {
            cooldown = 0;
        } else {
            cooldown--;
        }


        game.render(graph2D, this);
        graph2D.drawImage(newBackgroundImage, -game.getPlayer().getPositionX() + (GamePanel.WINDOW_WIDTH / 2), -game.getPlayer().getPositionY() + (GamePanel.WINDOW_HEIGHT / 2),null);



        for (Entity entity : entityArrayList) {
            try {
                /*
                if (
                        game.getPlayer().getPositionX() >= entity.getPositionX() && entity.getPositionX() + NEW_TILE_SIZE >= game.getPlayer().getPositionX()  &&
                        game.getPlayer().getPositionY() >= entity.getPositionY() && entity.getPositionY()  + NEW_TILE_SIZE >= game.getPlayer().getPositionY()
                ) {
                    if (!entity.equals(game.getPlayer())) {
                        entity.setHealthPoints(entity.getHealthPoints() - 1);
                    }
                }

                if (entity.getHealthPoints() <= 0) {
                    entityArrayList.remove(entity);
                    break;
                }

                 */

                entity.draw(graph2D, game, this);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        graph2D.dispose();
    }

    public static void main(String[] args) {
    }
}