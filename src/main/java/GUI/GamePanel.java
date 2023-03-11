package GUI;

import main.Main;
import objectClasses.Enemy;
import objectClasses.Game;
import utilityClasses.AudioManager;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;


public class GamePanel extends JPanel implements Runnable {

    static final int SCALE_FACTOR = 3; // 16 x 16 won't actually be displayed as 16 x 16
    static final int ORIGINAL_TILE_SIZE = 16; // 16 x 16 pixel
    public static final int NEW_TILE_SIZE = ORIGINAL_TILE_SIZE * SCALE_FACTOR;

    public static final int WINDOW_WIDTH = (int) (Main.DEFAULT_WINDOW_WIDTH * Main.SCALING_FACTOR);      //NEW_TILE_SIZE * MAX_SCREEN_COL; // 768 pixel
    public static final int WINDOW_HEIGHT = (int) (Main.DEFAULT_WINDOW_HEIGHT * Main.SCALING_FACTOR);    //NEW_TILE_SIZE * MAX_SCREEN_ROW; // 576 pixel

    private Thread gameThread = null;

    // needed because repaint() is called depending
    // on the frames per second -> thus we need to
    // regulate it
    int FPS = 60;

    private Game game;
    static boolean allowThreadRemoval, reloadLevel;
    public static boolean isLoading, isOpeningChest;

    public GamePanel() {

        init();
    }

    private void init() {

        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(
                (int) (Main.DEFAULT_WINDOW_WIDTH * Main.SCALING_FACTOR),
                (int) (Main.DEFAULT_WINDOW_HEIGHT * Main.SCALING_FACTOR)));
        setBackground(new Color(39, 105, 195));
        setDoubleBuffered(true); // improves rendering
        setFocusable(true); // GamePanel "focused" to receive key input

        isLoading = true;
        allowThreadRemoval = false;
        game = new Game();
        addKeyListener(game.getPlayer().getKeyHandler());
        loadAudio();
        startGameThread();
    }

    public Game getGame() {
        return game;
    }

    public static void loadNextLevel() {
        isLoading = true;
        Main.showBlackScreen("Loading Next Level");
    }

    public static void reloadLevel() {
        reloadLevel = true;
        isLoading = true;
        Main.showBlackScreen("Reloading Level");
    }

    public static void showVictoryScreen() {
        isLoading = true;
        Main.showBlackScreen("Congrats!");
    }

    public static void showGameOverScreen() {
        isLoading = true;
        Main.showBlackScreen("Game Over");
    }

    private void clearMemory() {
        this.removeKeyListener(game.getPlayer().getKeyHandler());
        game.getPlayer().removeKeyHandler();
        game = null;
        AudioManager.stopAllSounds();
        Main.clearGamePanel();
    }

    public void startGameThread() {
        gameThread = new Thread(this);
        gameThread.start();
    }

    private void loadAudio() {

        AudioManager.load("/sounds/attack/swordSwipe1.wav", "S - swordSwipe1");
        AudioManager.load("/sounds/attack/swordSwipe2.wav", "S - swordSwipe2");
        AudioManager.load("/sounds/attack/swordSwipe3.wav", "S - swordSwipe3");
        AudioManager.load("/sounds/attack/characterHit.wav", "S - characterHit");
        AudioManager.load("/sounds/walkingHardGround.wav", "S - characterWalking");
        AudioManager.load("/sounds/skeletonHit.wav", "S - skeletonHit");
        AudioManager.load("/sounds/skeletonWalk.wav", "S - skeletonWalking");
        AudioManager.load("/sounds/openingChest.wav", "S - openingChest");
        AudioManager.load("/music/dungeon.wav", "M - dungeon");
    }

    // Game thread
    @Override
    public void run() {

        long drawInterval = 1000000000 / FPS;
        long nextDrawTime = System.nanoTime() + drawInterval;

        while (gameThread != null) {

            if (allowThreadRemoval) {
                gameThread.interrupt();
                gameThread = null;
                clearMemory();
                return;
            }
            try {
                update();

            } catch (IOException e) {
                e.printStackTrace();
            }
            repaint();

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

        // Ensures that the game window listens to key inputs when focused
        if (Main.currentScreen.equals("Game")) requestFocusInWindow();

        // Functions that are always called,
        // even if the game is loading

        // Returns true if the below functions
        // should be canceled
        if (updateGame()) {
            return;
        }

        // Functions that are called,
        // if the game is not loading
        updateProjectiles();

        if (game.getPlayer().isDead()) {
            game.animateCharacterDying();
            updateEnemies();
            return;
        }

        // Functions that are called,
        // if the character is not dead
        if (game.isOpeningChest()) {
            AudioManager.stop("S - skeletonWalking");
            isOpeningChest = true;
            return;
        } else {
            isOpeningChest = false;
        }

        // Functions that are called,
        // if no chest is opened
        updateEnemies();

        // Checks if the inventory, options
        // or interact key was pressed
        listenForKeyInputs();

        // Updates character position
        // and appearance
        updateCharacter();
    }

    private boolean updateGame() {
        if (reloadLevel) {
            reloadLevel = false;
            isLoading = true;
            game.reloadLevel();
        }

        if (isLoading) {
            if (game.isLoadingLevel()) {
                AudioManager.stopAllSounds();
                if (Main.allowContinue()) {
                    if (game.getPlayer().interactPressed()) {

                        if (game.isGameWon() || game.isGameOver()) {
                            Main.showMainScreen();
                            allowThreadRemoval = true;
                        } else {
                            game.displayLevel();
                        }
                        Main.discardBlackScreen();
                        isLoading = false;
                    }
                }
            }
            return true;
        }
        return false;
    }

    private void updateProjectiles() {
        // For enemy projectiles
        game.handleEnemyProjectiles();
    }

    private void updateEnemies() {
        for (Enemy enemy : game.getCurrentLevel().getEnemies()) {
            if (!enemy.isDead()) {
                enemy.update(game);
                if (enemy.isKnockedBack()) {
                    enemy.updateKnockBack(game);
                }
            }
        }
    }

    private void listenForKeyInputs() {
        if (game.getPlayer().optionsPressed()) {
            Main.showOptionsScreen();
        }
        if (game.getPlayer().inventoryPressed()) {
            Main.toggleInventory();
        }
        // If interacting with chests/doors/similar objects
        if (game.getPlayer().interactPressed()) {
            game.characterInteract();
        }
    }

    private void updateCharacter() {

        // If attacking
        if (game.getPlayer().isAttacking()) {
            game.animateCharacterAttacking();
            game.checkPlayerAttack();

            // If standing
        } else if (game.getPlayer().isResting()) {
            game.animateCharacterResting();

            // If walking
        } else if (game.getPlayer().isWalking()) {
            game.animateCharacterWalking();
        }
    }

    @Override
    public void paint(Graphics graph) {
        super.paintComponent(graph);
        Graphics2D graph2D = (Graphics2D) graph;

        if (isLoading) {
            return;
        }

        // Solids, chests, torch stems
        paintFirstLayerMap(graph2D);

        // Character, enemies
        paintSecondLayerMap(graph2D);

        // Torch flames, trees, health bar, lives
        paintThirdLayerMap(graph2D);

        graph2D.dispose();
    }

    private void paintFirstLayerMap(Graphics2D graph2D) {
        game.renderSolid(graph2D);
        game.renderChests(graph2D);
        game.renderTorchStems(graph2D);
    }

    private void paintSecondLayerMap(Graphics2D graph2D) {
        game.getPlayer().draw(graph2D, game);
        for (Enemy enemy : game.getCurrentLevel().getEnemies()) {
            enemy.draw(graph2D, game);
        }
    }

    private void paintThirdLayerMap(Graphics2D graph2D) {
        game.renderTorchFlames(graph2D);
        game.renderTrees(graph2D);
        game.renderCharacterHealthBar(graph2D);
        game.renderLives(graph2D);
    }
}