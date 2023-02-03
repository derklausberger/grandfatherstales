package main;

import GUI.*;
import utilityClasses.AudioManager;
import utilityClasses.InputHandler;
import utilityClasses.ResourceLoader;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

public class Main {

    // Main window
    private static JFrame window;

    private static GamePanel gamePanel;
    private static boolean gameCreated;

    // Window size for all screens
    public static final int
            DEFAULT_WINDOW_WIDTH = 1000,
            DEFAULT_WINDOW_HEIGHT = 563;

    // Only change the scaling factor for a smaller screen
    public static final float
            SCALING_FACTOR = 1.2f;

    public static String currentScreen, previousScreen;
    private static JLabel continueMessage;
    private static JPanel rootPanel, blackScreen;
    private static JLayeredPane layeredPane;

    // CardLayout to switch between screens
    private static final CardLayout cardLayout = new CardLayout();

    public static void showMainScreen() {

        cardLayout.show(rootPanel, "Main Menu");
        previousScreen = currentScreen;
        currentScreen = "Main Menu";

        AudioManager.stopAllSounds();
    }

    public static void showOptionsScreen() {

        cardLayout.show(rootPanel, "Options");
        previousScreen = currentScreen;
        currentScreen = "Options";
    }

    public static void showLoadingScreen() {

        LoadingPanel loadingPanel = new LoadingPanel();
        rootPanel.add(loadingPanel, "Loading");

        cardLayout.show(rootPanel, "Loading");
        currentScreen = "Loading";
        createGamePanel();
    }

    public static boolean updateLoadingScreen() {

        return gameCreated;
    }

    private static void createGamePanel() {

        // Starts a new thread to load the game resources
        new Thread(new Runnable() {
            @Override
            public void run() {

                // Create the game panel and add it to the main panel
                try {
                    if (gamePanel != null) {
                        gamePanel = null;
                    }
                    gamePanel = new GamePanel();
                    createGameScreenComponents();
                    gameCreated = true;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private static void createGameScreenComponents() {

        // Sets the gamePanel to the same size as the window (full window)
        gamePanel.setBounds(0, 0,
                (int) (DEFAULT_WINDOW_WIDTH * SCALING_FACTOR),
                (int) (DEFAULT_WINDOW_HEIGHT * SCALING_FACTOR));

        // Creates the layered pane to hold all panels in different layers
        layeredPane = new JLayeredPane();
        layeredPane.setPreferredSize(new Dimension(
                (int) (DEFAULT_WINDOW_WIDTH * SCALING_FACTOR),
                (int) (DEFAULT_WINDOW_HEIGHT * SCALING_FACTOR)));
        layeredPane.setSize(layeredPane.getPreferredSize());

        // Creates the inventory panel for the character
        InventoryPanel inventoryPanel = new InventoryPanel();
        InventoryPanel.loadInventory(gamePanel.getGame().getPlayer());
        inventoryPanel.setSize(inventoryPanel.getPreferredSize());

        // Still doesn't center the inventory perfectly vertically
        // Therefore, moved 20px upwards
        inventoryPanel.setLocation((int) ((layeredPane.getWidth() / 16) * SCALING_FACTOR), (layeredPane.getHeight() - inventoryPanel.getHeight()) / 2 - 20);

        // Creates the reward panel when opening a chest
        RewardPanel rewardPanel = new RewardPanel(gamePanel.getGame());
        rewardPanel.setSize(rewardPanel.getPreferredSize());
        rewardPanel.setLocation((layeredPane.getWidth() - rewardPanel.getWidth()) / 2, (layeredPane.getHeight() - rewardPanel.getHeight()) / 2);

        // Loads the image for the health container and adds it to a label
        ResourceLoader rl = ResourceLoader.getResourceLoader();
        BufferedImage healthContainerBackground = rl.getBufferedImage("/screen/inventoryPanel/healthContainer.png");

        // Creates the health container in the top left
        JLabel healthContainer = new JLabel();
        healthContainer.setSize(new Dimension(166 * 2, 37 * 2));
        healthContainer.setIcon(new ImageIcon(healthContainerBackground.getScaledInstance(166 * 2, 37 * 2, Image.SCALE_SMOOTH)));
        healthContainer.setLocation(20, 20);

        // Creates the black screen in the background when loading levels
        blackScreen = new JPanel();
        blackScreen.setLayout(new GridBagLayout());
        blackScreen.setPreferredSize(new Dimension(
                (int) (DEFAULT_WINDOW_WIDTH * SCALING_FACTOR),
                (int) (DEFAULT_WINDOW_HEIGHT * SCALING_FACTOR)));
        blackScreen.setSize(blackScreen.getPreferredSize());
        blackScreen.setVisible(false);

        // Adds all panels to suitable layers
        layeredPane.add(blackScreen, JLayeredPane.POPUP_LAYER);
        layeredPane.add(gamePanel, JLayeredPane.DEFAULT_LAYER);
        layeredPane.add(inventoryPanel, JLayeredPane.PALETTE_LAYER);
        layeredPane.add(healthContainer, JLayeredPane.PALETTE_LAYER);
        layeredPane.add(rewardPanel, JLayeredPane.MODAL_LAYER);
        rootPanel.add(layeredPane, "Game");
    }

    public static void showGameScreen() {

        GamePanel.isLoading = false;
        cardLayout.show(rootPanel, "Game");
        currentScreen = "Game";
    }

    private static void startBlackScreenTimer(JLabel gameStateMessage) {

        // Creates a Timer to schedule the fading of the screen
        new Timer(35, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                // Fades the game screen to black by increasing the alpha value
                if (!blackScreen.isVisible()) {
                    blackScreen.setBackground(new Color(0, 0, 0, 25));
                    blackScreen.setVisible(true);
                }
                blackScreen.setBackground(new Color(0, 0, 0, blackScreen.getBackground().getAlpha() + 1));

                if (blackScreen.getBackground().getAlpha() >= 75) {
                    ((Timer) e.getSource()).stop();
                    // Display the "Game Over" text
                    gameStateMessage.setVisible(true);

                    // Schedule a delay of half a second before showing
                    // the message to continue by clicking
                    new Timer(1000, new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            // Make the main menu visible
                            ((Timer) e.getSource()).stop();

                            continueMessage.setVisible(true);
                        }
                    }).start();
                }
            }
            // Starts the fading of the screen
        }).start();
    }

    public static boolean allowContinue() {

        return continueMessage.isVisible();
    }

    public static void discardBlackScreen() {

        blackScreen.setVisible(false);
    }

    private static JLabel createBlackScreenComponents(String msg) {

        blackScreen.removeAll();

        // Loads the font and creates the message
        ResourceLoader rl = ResourceLoader.getResourceLoader();
        Font font = rl.getDefaultTextFont();

        GridBagConstraints constraints = new GridBagConstraints();

        JLabel gameStateMessage = new JLabel(msg);
        gameStateMessage.setFont(font.deriveFont(46f));
        gameStateMessage.setForeground(new Color(0x7d0027));
        gameStateMessage.setVisible(false);

        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.gridwidth = GridBagConstraints.REMAINDER;
        constraints.gridheight = GridBagConstraints.REMAINDER;
        constraints.weightx = 1;
        constraints.weighty = 1;
        constraints.anchor = GridBagConstraints.CENTER;

        blackScreen.add(gameStateMessage, constraints);

        continueMessage = new JLabel("Press " + InputHandler.getKeyName("interact") + " to continue");
        continueMessage.setFont(font.deriveFont(16f));
        continueMessage.setForeground(new Color(0xe0d9ae));
        continueMessage.setVisible(false);

        constraints.fill = GridBagConstraints.NONE;
        constraints.gridx = 1;
        constraints.gridy = 1;
        constraints.weightx = 0;
        constraints.weighty = 0;
        constraints.anchor = GridBagConstraints.SOUTHEAST;
        constraints.insets = new Insets(0, 0, 60, 40);

        blackScreen.add(continueMessage, constraints);

        return gameStateMessage;
    }

    public static void showBlackScreen(String msg) {

        startBlackScreenTimer(createBlackScreenComponents(msg));
    }

    public static void toggleInventory() {

        layeredPane.getComponentsInLayer(
                JLayeredPane.PALETTE_LAYER)[0].setVisible(
                !layeredPane.getComponentsInLayer(
                        JLayeredPane.PALETTE_LAYER)[0].isVisible());
    }

    public static void toggleRewardScreen() {

        ((RewardPanel) (layeredPane.getComponentsInLayer(JLayeredPane.MODAL_LAYER)[0])).setRewards();
        layeredPane.getComponentsInLayer(JLayeredPane.MODAL_LAYER)[0]
                .setVisible(
                        !layeredPane.getComponentsInLayer(JLayeredPane.MODAL_LAYER)[0]
                                .isVisible());
    }

    public static void showPreviousScreen() {

        cardLayout.show(rootPanel, previousScreen);

        String s = previousScreen;
        previousScreen = currentScreen;
        currentScreen = s;
    }

    private static void createPrimaryScreens() {

        // Creates the main/root panel that acts as the foundation
        rootPanel = new JPanel();
        rootPanel.setPreferredSize(new Dimension(
                (int) (DEFAULT_WINDOW_WIDTH * SCALING_FACTOR),
                (int) (DEFAULT_WINDOW_HEIGHT * SCALING_FACTOR)));

        // Sets a card layout to simply swap screens by calling a function
        rootPanel.setLayout(cardLayout);

        // Creates the "primary" screens that are loaded
        // even though the game is not started
        MainMenuPanel mainMenuPanel = new MainMenuPanel();
        rootPanel.add(mainMenuPanel, "Main Menu");

        OptionsMenuPanel optionsPanel = new OptionsMenuPanel();
        rootPanel.add(optionsPanel, "Options");
    }

    public static void closeWindow() {

        window.dispose();
        System.exit(0);
    }

    public static void main(String[] args) {

        // Creates the main window
        window = new JFrame("Grandfather's Tales");
        window.setPreferredSize(new Dimension(
                (int) (DEFAULT_WINDOW_WIDTH * SCALING_FACTOR),
                (int) (DEFAULT_WINDOW_HEIGHT * SCALING_FACTOR)));

        createPrimaryScreens();

        window.add(rootPanel);
        showMainScreen();
        //showLoadingScreen();

        // Attempt to create a smoother transition when looping
        //AudioManager.loop("M - mainTheme", 0, 175, AudioManager.getFrames("M - mainTheme") - 1);

        // Loads an image for the icon in the top left (very small and unreadable at the moment)
        ResourceLoader rl = ResourceLoader.getResourceLoader();
        window.setIconImage(new ImageIcon(rl.getBufferedImage("/screen/mainMenuPanel/logo.png")).getImage());
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setResizable(false);
        window.pack();
        window.setLocationRelativeTo(null);
        window.setVisible(true);
    }
}