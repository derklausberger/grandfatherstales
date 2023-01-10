package GUI;

import main.Main;
import objectClasses.Abstract.Entity;
import objectClasses.Enemy;
import objectClasses.Game;
import org.xml.sax.SAXException;

import javax.swing.*;
import javax.xml.parsers.ParserConfigurationException;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.ArrayList;


public class GamePanel extends JPanel implements Runnable, ActionListener {

    static final int SCALE_FACTOR = 3; // 16 x 16 won't actually be displayed as 16 x 16
    static final int ORIGINAL_TILE_SIZE = 16; // 16 x 16 pixel
    public static final int NEW_TILE_SIZE = ORIGINAL_TILE_SIZE * SCALE_FACTOR;

    public static final int CHARACTER_DEFAULT_WIDTH = 30;
    public static final int CHARACTER_DEFAULT_HEIGHT = 50;

    static final int MAX_SCREEN_COL = 16; // max. 16 tiles in x
    static final int MAX_SCREEN_ROW = 12; // max. 12 tiles in y

    public static final int WINDOW_WIDTH = NEW_TILE_SIZE * MAX_SCREEN_COL; // 768 pixel
    public static final int WINDOW_HEIGHT = NEW_TILE_SIZE * MAX_SCREEN_ROW; // 576 pixel

    InputHandler keyHandler = new InputHandler(); // own class
    Thread playerThread = null;

    int cooldown = 0;

    int FPS = 60;
    // needed because repaint() is called depending
    // on the frames per second -> thus we need to
    // regulate it


    private final Game game;
    public static ArrayList<Entity> entityArrayList = new ArrayList<>();

    // This variable will keep track of the current frame of the animation
    public static int currentFrame = 0;

    // This timer will be used to control the frame rate of the animation
    private Timer timer;

    public GamePanel() throws IOException, ParserConfigurationException, SAXException {
        game = new Game();//new Player((int) ((WINDOW_WIDTH) / 2), (int) ((WINDOW_HEIGHT) / 2), 3, 5, null, 3, 1));

        this.setPreferredSize(new Dimension(WINDOW_WIDTH, WINDOW_HEIGHT));
        this.setBackground(new Color(39, 105, 195));
        this.addKeyListener(keyHandler); // adds Listener
        this.setDoubleBuffered(true); // improves rendering
        this.setFocusable(true); // GamePanel "focused" to receive key input

        entityArrayList.add(game.getPlayer());

        for (Enemy e : game.getCurrentLevel().getEnemies()) {
            entityArrayList.add(e);
        }

        startPlayerThread();

        // Set up the timer to fire every 100 milliseconds (10 frames per second)
        timer = new Timer(110, this);
        timer.start();

    }

    // Problem: when timed "badly", the first frame gets cut off
    // so that the animation jumps at the beginning from the first
    // to the second frame
    // Fix: attack animation needs an independent timer that increases
    // the current attack frame only after attack actually has been pressed
    // (not implemented yet)
    int attackFrame = 1;

    public void actionPerformed(ActionEvent e) {

        if (!keyHandler.attackPressed) {

            // Advance the frame counter
            currentFrame++;

            // If we've reached the end of the animation, start over from the beginning
            if (currentFrame >= 9) {
                currentFrame = 1;
            }
        } else {
            attackFrame++;

            System.out.println(attackFrame);
            if (attackFrame >= 6) {
                System.out.println(attackFrame);
                keyHandler.attackPressed = false;
                currentFrame = 1;
                attackFrame = 1;

                // Necessary, because e.g. the player moves upwards and attacks:
                // While the attack animation is executed, W is released
                // If any direction other than upwards is attempted, it will not move
                // until any key was released first
                keyHandler.lastPressed = 10000;
            }
        }
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

                if (Main.currentScreen.equals("Game")) requestFocusInWindow();
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

        if (keyHandler.menuPressed) {
            keyHandler.menuPressed = false;
            Main.showOptionsScreen();
        }

        if (!keyHandler.attackPressed) {
            game.getPlayer().setCurrentAnimationType("walking");

            if (keyHandler.upPressed) {
                game.getPlayer().setCurrentFrame(currentFrame + 27);

                if (game.getCurrentLevel().isSolid(game.getPlayer().getPositionX() + Math.floorDiv(NEW_TILE_SIZE * 3, 10), game.getPlayer().getPositionY() - game.getPlayer().getMovementSpeed()) &&
                        game.getCurrentLevel().isSolid(game.getPlayer().getPositionX() - Math.floorDiv(NEW_TILE_SIZE * 3, 10), game.getPlayer().getPositionY() - game.getPlayer().getMovementSpeed() /*- Math.floorDiv(NEW_TILE_SIZE, 3)*/)) {
                    game.getPlayer().setPositionY(game.getPlayer().getPositionY() - game.getPlayer().getMovementSpeed());
                } else if (game.getCurrentLevel().isChest(game.getPlayer().getPositionX(), game.getPlayer().getPositionY() - Math.floorDiv(NEW_TILE_SIZE, 10))) {
                    System.out.println("chest!");
                }
            } else if (keyHandler.downPressed) {
                game.getPlayer().setCurrentFrame(currentFrame);

                if (game.getCurrentLevel().isSolid(game.getPlayer().getPositionX() - Math.floorDiv(NEW_TILE_SIZE * 3, 10), game.getPlayer().getPositionY() + game.getPlayer().getMovementSpeed() + Math.floorDiv(NEW_TILE_SIZE * 4, 10)) &&
                        game.getCurrentLevel().isSolid(game.getPlayer().getPositionX() + Math.floorDiv(NEW_TILE_SIZE * 3, 10), game.getPlayer().getPositionY() + game.getPlayer().getMovementSpeed() + Math.floorDiv(NEW_TILE_SIZE * 4, 10))) {
                    game.getPlayer().setPositionY(game.getPlayer().getPositionY() + game.getPlayer().getMovementSpeed());
                }
            } else if (keyHandler.leftPressed) {
                game.getPlayer().setCurrentFrame(currentFrame + 9);

                if (game.getCurrentLevel().isSolid(game.getPlayer().getPositionX() - game.getPlayer().getMovementSpeed() - Math.floorDiv(NEW_TILE_SIZE * 3, 10), game.getPlayer().getPositionY() + Math.floorDiv(NEW_TILE_SIZE * 4, 10)) &&
                        game.getCurrentLevel().isSolid(game.getPlayer().getPositionX() - game.getPlayer().getMovementSpeed() - Math.floorDiv(NEW_TILE_SIZE * 3, 10), game.getPlayer().getPositionY()/* - Math.floorDiv(NEW_TILE_SIZE, 3)*/)) {
                    game.getPlayer().setPositionX(game.getPlayer().getPositionX() - game.getPlayer().getMovementSpeed());
                }
            } else if (keyHandler.rightPressed) {
                game.getPlayer().setCurrentFrame(currentFrame + 18);

                if (game.getCurrentLevel().isSolid(game.getPlayer().getPositionX() + game.getPlayer().getMovementSpeed() + Math.floorDiv(NEW_TILE_SIZE * 3, 10), game.getPlayer().getPositionY()) &&
                        game.getCurrentLevel().isSolid(game.getPlayer().getPositionX() + game.getPlayer().getMovementSpeed() + Math.floorDiv(NEW_TILE_SIZE * 3, 10), game.getPlayer().getPositionY() + Math.floorDiv(NEW_TILE_SIZE * 4, 10))) {
                    game.getPlayer().setPositionX(game.getPlayer().getPositionX() + game.getPlayer().getMovementSpeed());
                }
            } else {

                int i = switch (keyHandler.lastDirection) {
                    case KeyEvent.VK_A -> 9;
                    case KeyEvent.VK_D -> 18;
                    case KeyEvent.VK_W -> 27;
                    default -> 0;
                };
                game.getPlayer().setCurrentFrame(i);

                // Prevents the character to "slide" when moving, if a direction key
                // is spammed really fast
                if (keyHandler.lastDirection == KeyEvent.VK_W || keyHandler.lastDirection == KeyEvent.VK_S) {
                    currentFrame = 2;
                } else currentFrame = 1;
            }
        }


        if (keyHandler.attackPressed) {

            game.getPlayer().setCurrentAnimationType("attack");

            switch (keyHandler.lastDirection) {
                case (KeyEvent.VK_W) -> {  //Up

                    game.getPlayer().setCurrentFrame(attackFrame + 18);
                }
                case (KeyEvent.VK_A) -> {   //Left

                    game.getPlayer().setCurrentFrame(attackFrame + 6);
                }
                case (KeyEvent.VK_S) -> {   //Down

                    game.getPlayer().setCurrentFrame(attackFrame);
                }
                case (KeyEvent.VK_D) -> {  //Right

                    game.getPlayer().setCurrentFrame(attackFrame + 12);
                }
            }
        }

        /*
        int startAngle = 0;
        int arcAngle = 0;

        if (keyHandler.attackPressed
                && cooldown == 0) {
            switch (keyHandler.lastPressed) {
                case (KeyEvent.VK_W) -> {
                    cooldown = 20;
                    startAngle = 45;
                    arcAngle = 90;

                    int width = 200;
                    int height = 200;

                    game.getPlayer().draw((Graphics2D) this.getGraphics(), game, this, width, height, startAngle, arcAngle, entityArrayList);

                }
                case (KeyEvent.VK_A) -> {
                    cooldown = 20;
                    startAngle = 135;
                    arcAngle = 90;

                    int width = 200;
                    int height = 200;

                    game.getPlayer().draw((Graphics2D) this.getGraphics(), game, this, width, height, startAngle, arcAngle, entityArrayList);
                }
                case (KeyEvent.VK_S) -> {
                    cooldown = 20;
                    startAngle = 225;
                    arcAngle = 90;

                    int width = 200;
                    int height = 200;

                    game.getPlayer().draw((Graphics2D) this.getGraphics(), game, this, width, height, startAngle, arcAngle, entityArrayList);
                }
                case (KeyEvent.VK_D) -> {
                    cooldown = 20;
                    startAngle = 315;
                    arcAngle = 90;

                    int width = 200;
                    int height = 200;

                    game.getPlayer().draw((Graphics2D) this.getGraphics(), game, this, width, height, startAngle, arcAngle, entityArrayList);
                }
            }
        }

         */
    }

    @Override
    public void paint(Graphics graph) {
        super.paintComponent(graph);
        Graphics2D graph2D = (Graphics2D) graph;

        if (cooldown < 0) {
            cooldown = 0;
        } else {
            cooldown--;
        }

        game.renderSolid(graph2D);

        for (Entity entity : entityArrayList) {
            try {
                entity.draw(graph2D, game, this);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        game.renderTrees(graph2D);

        graph2D.dispose();
    }

}
