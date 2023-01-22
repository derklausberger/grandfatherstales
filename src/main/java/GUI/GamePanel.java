package GUI;

import main.Main;
import objectClasses.Enemy;
import objectClasses.Game;
import objectClasses.Player;
import org.xml.sax.SAXException;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.xml.parsers.ParserConfigurationException;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;


public class GamePanel extends JPanel implements Runnable, ActionListener {

    static final int SCALE_FACTOR = 3; // 16 x 16 won't actually be displayed as 16 x 16
    static final int ORIGINAL_TILE_SIZE = 16; // 16 x 16 pixel
    public static final int NEW_TILE_SIZE = ORIGINAL_TILE_SIZE * SCALE_FACTOR;

    public static final int WINDOW_WIDTH = (int)(Main.DEFAULT_WINDOW_WIDTH * Main.SCALING_FACTOR);      //NEW_TILE_SIZE * MAX_SCREEN_COL; // 768 pixel
    public static final int WINDOW_HEIGHT = (int)(Main.DEFAULT_WINDOW_HEIGHT * Main.SCALING_FACTOR);    //NEW_TILE_SIZE * MAX_SCREEN_ROW; // 576 pixel

    private Thread playerThread = null;

    int FPS = 60;
    // needed because repaint() is called depending
    // on the frames per second -> thus we need to
    // regulate it


    private final Game game;
    //public static ArrayList<Enemy> enemyArrayList = new ArrayList<Enemy>();
    //public static ArrayList<Player> playerArrayList = new ArrayList<Player>();
    public static Player player;

    // This variable will keep track of the current frame of the animation
    public static int currentFrame = 0;

    // This timer will be used to control the frame rate of the animation
    private Timer timer;

    public GamePanel() throws IOException, ParserConfigurationException, SAXException {
        game = new Game();//new Player((int) ((WINDOW_WIDTH) / 2), (int) ((WINDOW_HEIGHT) / 2), 3, 5, null, 3, 1));
        player = game.getPlayer();


        this.setLayout(new BorderLayout());
        this.setPreferredSize(new Dimension(
                (int) (Main.DEFAULT_WINDOW_WIDTH * Main.SCALING_FACTOR),
                (int) (Main.DEFAULT_WINDOW_HEIGHT * Main.SCALING_FACTOR)));

        this.setBackground(new Color(39, 105, 195));
        this.addKeyListener(game.getPlayer().getKeyHandler()); // adds Listener
        this.setDoubleBuffered(true); // improves rendering
        this.setFocusable(true); // GamePanel "focused" to receive key input

        loadAudio();

        //playerArrayList.add(game.getPlayer());
        //enemyArrayList.addAll(game.getCurrentLevel().getEnemies());

        startPlayerThread();

        // Set up the timer to fire every 100 milliseconds (10 frames per second)
        timer = new Timer(100, this);
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

        if (!game.getPlayer().getKeyHandler().attackPressed) {
            currentFrame++;

            // End of walking animation
            if (currentFrame >= 9) {
                currentFrame = 1;
            }
        } else {
            attackFrame++;

            // End of attack animation
            if (attackFrame >= 6) {
                currentFrame = 1;
                attackFrame = 1;
                game.getPlayer().setCooldown(0);

                // Sets last direction so the character faces the same
                // direction after attacking as during the animation
                game.getPlayer().getKeyHandler().lastDirection = game.getPlayer().getKeyHandler().attackDirection;

                // Releases hit direction to be newly assigned when attacking again
                game.getPlayer().getKeyHandler().attackDirection = 10000;
                game.getPlayer().getKeyHandler().attackPressed = false;
                game.getPlayer().setCurrentAnimationType("walking");
            }
        }
    }

    public void startPlayerThread() {
        playerThread = new Thread(this);
        playerThread.start();

    }

    private void loadAudio() {

        AudioManager.load("src/main/resources/audio/sounds/attack/Sword Swipe 1.wav", "S - swordSwipe1");
        AudioManager.load("src/main/resources/audio/sounds/attack/Sword Swipe 2.wav", "S - swordSwipe2");
        AudioManager.load("src/main/resources/audio/sounds/attack/Sword Swipe 3.wav", "S - swordSwipe3");
        AudioManager.load("src/main/resources/audio/sounds/attack/characterHit.wav", "S - characterHit");
        AudioManager.load("src/main/resources/audio/sounds/Walking Hard Ground.wav", "S - characterWalking");
        AudioManager.load("src/main/resources/audio/sounds/skeletonHit.wav", "S - skeletonHit");
        AudioManager.load("src/main/resources/audio/sounds/skeletonWalk.wav", "S - skeletonWalking");
        AudioManager.load("src/main/resources/audio/sounds/openingChest.wav", "S - openingChest");
    }

    @Override
    public void run() {

        long drawInterval = 1000000000 / FPS;
        long nextDrawTime = System.nanoTime() + drawInterval;

        while (playerThread != null) {

            try {
                update(); // updates: character positions, etc...

                // Ensures that the game window listens to key inputs when focused
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

        if (game.getPlayer().getKeyHandler().menuPressed) {
            game.getPlayer().getKeyHandler().menuPressed = false;
            Main.showOptionsScreen();
        }
        if (game.getPlayer().getKeyHandler().inventoryPressed) {
            game.getPlayer().getKeyHandler().inventoryPressed = false;
            Main.toggleInventory();
        }

        if (!game.getPlayer().getKeyHandler().attackPressed) {

            AudioManager.loop("S - characterWalking");
            game.getPlayer().setCurrentAnimationType("walking");
            int player_height = 50;
            int player_width = 30;

            if (game.getPlayer().getKeyHandler().walkingDirection == InputHandler.upKey) {
                game.getPlayer().setCurrentFrame(currentFrame + 27);

                if (game.getCurrentLevel().isSolid(game.getPlayer().getPositionX() + Math.floorDiv(player_width * 4, 10), game.getPlayer().getPositionY() - game.getPlayer().getMovementSpeed() + Math.floorDiv(player_height * 2, 10)) &&
                        game.getCurrentLevel().isSolid(game.getPlayer().getPositionX() - Math.floorDiv(player_width * 4, 10), game.getPlayer().getPositionY() - game.getPlayer().getMovementSpeed() + Math.floorDiv(player_height * 2, 10))) {
                    game.getPlayer().setPositionY(game.getPlayer().getPositionY() - game.getPlayer().getMovementSpeed());
                } else if (game.getCurrentLevel().isChest(game.getPlayer().getPositionX(), game.getPlayer().getPositionY() - Math.floorDiv(player_height, 10))) {
                    AudioManager.play("S - openingChest");
                    game.openChest(game.getPlayer().getPositionX(), game.getPlayer().getPositionY() - Math.floorDiv(player_height, 10));
                } else if (game.getCurrentLevel().isExit(game.getPlayer().getPositionX(), game.getPlayer().getPositionY() - Math.floorDiv(player_height, 10))) {
                    if (!game.loadNextLevel()) {
                        System.out.println("this is last level");
                    }
                }
            } else if (game.getPlayer().getKeyHandler().walkingDirection == InputHandler.downKey) {
                game.getPlayer().setCurrentFrame(currentFrame);

                if (game.getCurrentLevel().isSolid(game.getPlayer().getPositionX() - Math.floorDiv(player_width * 4, 10), game.getPlayer().getPositionY() + game.getPlayer().getMovementSpeed() + Math.floorDiv(player_height, 2)) &&
                        game.getCurrentLevel().isSolid(game.getPlayer().getPositionX() + Math.floorDiv(player_width * 4, 10), game.getPlayer().getPositionY() + game.getPlayer().getMovementSpeed() + Math.floorDiv(player_height, 2))) {
                    game.getPlayer().setPositionY(game.getPlayer().getPositionY() + game.getPlayer().getMovementSpeed());
                }
            } else if (game.getPlayer().getKeyHandler().walkingDirection == InputHandler.leftKey) {
                game.getPlayer().setCurrentFrame(currentFrame + 9);

                if (game.getCurrentLevel().isSolid(game.getPlayer().getPositionX() - game.getPlayer().getMovementSpeed() - Math.floorDiv(player_width * 4, 10), game.getPlayer().getPositionY() + Math.floorDiv(player_height, 2)) &&
                        game.getCurrentLevel().isSolid(game.getPlayer().getPositionX() - game.getPlayer().getMovementSpeed() - Math.floorDiv(player_width * 4, 10), game.getPlayer().getPositionY() + Math.floorDiv(player_height * 2, 10))) {
                    game.getPlayer().setPositionX(game.getPlayer().getPositionX() - game.getPlayer().getMovementSpeed());
                }
            } else if (game.getPlayer().getKeyHandler().walkingDirection == InputHandler.rightKey) {
                game.getPlayer().setCurrentFrame(currentFrame + 18);

                if (game.getCurrentLevel().isSolid(game.getPlayer().getPositionX() + game.getPlayer().getMovementSpeed() + Math.floorDiv(player_width * 4, 10), game.getPlayer().getPositionY() + Math.floorDiv(player_height, 2)) &&
                        game.getCurrentLevel().isSolid(game.getPlayer().getPositionX() + game.getPlayer().getMovementSpeed() + Math.floorDiv(player_width * 4, 10), game.getPlayer().getPositionY() + Math.floorDiv(player_height * 2, 10))) {
                    game.getPlayer().setPositionX(game.getPlayer().getPositionX() + game.getPlayer().getMovementSpeed());
                }
            } else {
                AudioManager.stop("S - characterWalking");
                int frame = 10000, lastDirection = game.getPlayer().getKeyHandler().lastDirection;

                if (lastDirection == InputHandler.leftKey) {
                    frame = 9;
                } else if (lastDirection == InputHandler.rightKey) {
                    frame = 18;
                } else if (lastDirection == InputHandler.upKey) {
                    frame = 27;
                } else if (lastDirection == InputHandler.downKey) {
                    frame = 0;
                }
                if (frame != 10000) game.getPlayer().setCurrentFrame(frame);

                // Prevents the character to "slide" when moving, if a direction key
                // is spammed really fast
                if (lastDirection == InputHandler.upKey
                        || lastDirection == InputHandler.downKey) {
                    currentFrame = 2;
                } else currentFrame = 1;
            }
        }

        if (game.getPlayer().getKeyHandler().attackPressed) {
            if (game.getPlayer().getCurrentAnimationType().equals("walking")) {
                // Creates an array of the sound names
                String[] sounds = {"S - swordSwipe1", "S - swordSwipe2", "S - swordSwipe3"};
                // Generates a random index
                int index = (int) (Math.random() * sounds.length);
                AudioManager.play(sounds[index]);
                AudioManager.stop("S - characterWalking");
            }
            game.getPlayer().setCurrentAnimationType("attacking");

            int attackDirection = game.getPlayer().getKeyHandler().attackDirection;

            if (attackDirection == InputHandler.upKey) {
                game.getPlayer().setCurrentFrame(attackFrame + 18);

            } else if (attackDirection == InputHandler.leftKey) {
                game.getPlayer().setCurrentFrame(attackFrame + 6);

            } else if (attackDirection == InputHandler.rightKey) {
                game.getPlayer().setCurrentFrame(attackFrame + 12);

            } else game.getPlayer().setCurrentFrame(attackFrame);
        }

        for (Enemy enemy: game.getCurrentLevel().getEnemies()) {
            enemy.moveProjectiles(game);
        }
    }

    @Override
    public void paint(Graphics graph) {
        super.paintComponent(graph);
        Graphics2D graph2D = (Graphics2D) graph;

        game.getPlayer().reduceInvincibilityCooldown();
        game.checkPlayerAttack(attackFrame);

        for (Enemy enemy: game.getCurrentLevel().getEnemies()) {
            //enemy.reduceCooldown();
            enemy.detectPlayer(game);
        }

        game.renderSolid(graph2D);
        game.renderChests(graph2D);
        game.renderTorchStems(graph2D);
        game.getPlayer().draw(graph2D, game, this);
        //for (Player player : playerArrayList) { player.draw(graph2D, game, this);}
        game.renderTorchFlames(graph2D);

        for (Enemy enemy : game.getCurrentLevel().getEnemies()) {
            enemy.draw(graph2D, game, this);
            /*if(enemy.isKnockBack()) {
                enemy.update();
            }*/
        }
        game.renderTrees(graph2D);
        game.getPlayer().draw(graph2D, game, this);
        graph2D.dispose();
    }

}
