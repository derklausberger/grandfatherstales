package main;

import GUI.*;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;

public class Main {

    // Window size for all screens
    public static final int
            DEFAULT_WINDOW_WIDTH = 1000,
            DEFAULT_WINDOW_HEIGHT = 563;

    // Only change the scaling factor for a smaller screen
    public static final float
            SCALING_FACTOR = 1.2f;

    public static String currentScreen, previousScreen;
    private static JPanel rootPanel, blackScreen;
    private static JLayeredPane layeredPane;

    // CardLayout to switch between screens
    private static final CardLayout cardLayout = new CardLayout();

    public static void showMainScreen() {

        cardLayout.show(rootPanel, "Main Menu");
        previousScreen = currentScreen;
        currentScreen = "Main Menu";

        AudioManager.stopAll();
        // Attempt to create a smoother transition when looping
        //AudioManager.loop("M - mainTheme", 0, 175, AudioManager.getFrames("M - mainTheme") - 1);
    }

    public static void showOptionsScreen() {

        cardLayout.show(rootPanel, "Options");
        previousScreen = currentScreen;
        currentScreen = "Options";
    }

    public static void showLoadingScreen() {
        cardLayout.show(rootPanel, "Loading");
    }

    public static void showGameScreen(JPanel gamePanel) {

        gamePanel.setBounds(0, 0,
                (int) (DEFAULT_WINDOW_WIDTH * SCALING_FACTOR),
                (int) (DEFAULT_WINDOW_HEIGHT * SCALING_FACTOR));

        InventoryPanel inventoryPanel = new InventoryPanel();
        inventoryPanel.setSize(inventoryPanel.getPreferredSize());

        RewardPanel rewardPanel = new RewardPanel();
        rewardPanel.setSize(rewardPanel.getPreferredSize());

        BufferedImage bufferedBackground = null;

        try {
            bufferedBackground = ImageIO.read(new File("src/main/resources/screen/inventoryPanel/healthContainer.png"));
        } catch (Exception e) {
            e.printStackTrace();
        }

        JLabel healthContainer = new JLabel("Health Container");
        healthContainer.setSize(new Dimension(166 * 2, 37 * 2));
        healthContainer.setIcon(new ImageIcon(bufferedBackground.getScaledInstance(166 * 2, 37 * 2, Image.SCALE_SMOOTH)));

        // Creates the layered pane to hold the game and inventory panels
        layeredPane = new JLayeredPane();
        layeredPane.setPreferredSize(new Dimension(
                (int) (DEFAULT_WINDOW_WIDTH * SCALING_FACTOR),
                (int) (DEFAULT_WINDOW_HEIGHT * SCALING_FACTOR)));
        layeredPane.setSize(layeredPane.getPreferredSize());

        // Still doesn't center the inventory perfectly vertically,
        // Therefore, moved 20px upwards
        int yCenter = (layeredPane.getHeight() - inventoryPanel.getHeight()) / 2;
        inventoryPanel.setLocation((int) ((layeredPane.getWidth() / 16) * SCALING_FACTOR), yCenter - 20);

        healthContainer.setLocation(20, 20);

        rewardPanel.setLocation((layeredPane.getWidth() - rewardPanel.getWidth()) / 2, yCenter);

        blackScreen = new JPanel();
        blackScreen.setLayout(new BorderLayout());
        blackScreen.setPreferredSize(new Dimension(
                (int) (DEFAULT_WINDOW_WIDTH * SCALING_FACTOR),
                (int) (DEFAULT_WINDOW_HEIGHT * SCALING_FACTOR)));
        blackScreen.setSize(blackScreen.getPreferredSize());
        blackScreen.setVisible(false);

        layeredPane.add(blackScreen, JLayeredPane.POPUP_LAYER);
        layeredPane.add(gamePanel, JLayeredPane.DEFAULT_LAYER);
        layeredPane.add(inventoryPanel, JLayeredPane.PALETTE_LAYER);
        layeredPane.add(healthContainer, JLayeredPane.PALETTE_LAYER);
        layeredPane.add(rewardPanel, JLayeredPane.MODAL_LAYER);


        rootPanel.add(layeredPane, "Game");
        cardLayout.show(rootPanel, "Game");
        currentScreen = "Game";
    }

    public static void showBlackScreen(String msg) {

        blackScreen.removeAll();

        File fontFile = new File("src/main/resources/fonts/DePixelBreit.ttf");
        Font font = null;
        try {
            font = Font.createFont(Font.TRUETYPE_FONT, fontFile);
        } catch (Exception e) {
            e.printStackTrace();
        }

        JLabel message = new JLabel(msg);
        message.setFont(font.deriveFont(46f));
        message.setForeground(new Color(0x7d0027));
        message.setVisible(false);
        message.setHorizontalAlignment(JLabel.CENTER);

        blackScreen.add(message, BorderLayout.CENTER);

        // Creates a Timer to schedule the fading of the screen
        Timer fadeTimer = new Timer(35, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Fade the game screen to black by decreasing the alpha value

                if (!blackScreen.isVisible()) {
                    blackScreen.setBackground(new Color(0, 0, 0, 0));
                    blackScreen.setVisible(true);
                }
                blackScreen.setBackground(new Color(0, 0, 0, blackScreen.getBackground().getAlpha() + 1));

                if (blackScreen.getBackground().getAlpha() >= 50) {
                    ((Timer) e.getSource()).stop();
                    // Display the "Game Over" text
                    message.setVisible(true);
                    // Schedule a delay of 3 seconds before showing the main menu
                    new Timer(3000, new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            // Make the main menu visible
                            ((Timer) e.getSource()).stop();

                            if (msg.equals("Game Over") || msg.contains("Congrats!")) showMainScreen();
                            else blackScreen.setVisible(false);
                        }
                    }).start();
                }
            }
        });

// Start the fading of the screen
        fadeTimer.start();
    }


    public static void toggleInventory() {

        layeredPane.getComponentsInLayer(
                JLayeredPane.PALETTE_LAYER)[0].setVisible(
                !layeredPane.getComponentsInLayer(
                        JLayeredPane.PALETTE_LAYER)[0].isVisible());
    }

    public static void toggleRewardScreen() {
        ((RewardPanel)(layeredPane.getComponentsInLayer(JLayeredPane.MODAL_LAYER)[0])).setRandomRewards();
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

    public static void main(String[] args) {

        // Create the main window
        JFrame window = new JFrame("Grandfather's Tales");
        window.setPreferredSize(new Dimension(
                (int) (DEFAULT_WINDOW_WIDTH * SCALING_FACTOR),
                (int) (DEFAULT_WINDOW_HEIGHT * SCALING_FACTOR)));

        // "Main" panel that acts as the foundation
        rootPanel = new JPanel();
        rootPanel.setPreferredSize(new Dimension(
                (int) (DEFAULT_WINDOW_WIDTH * SCALING_FACTOR),
                (int) (DEFAULT_WINDOW_HEIGHT * SCALING_FACTOR)));

        rootPanel.setLayout(cardLayout);

        // Creates the main menu screen
        MainMenuPanel mainMenuPanel = new MainMenuPanel();
        rootPanel.add(mainMenuPanel, "Main Menu");

        OptionsMenuPanel optionsPanel = new OptionsMenuPanel();
        rootPanel.add(optionsPanel, "Options");

        LoadingPanel loadingPanel = new LoadingPanel();
        rootPanel.add(loadingPanel, "Loading");

        //currentScreen = "Game";
        //GamePanel game = null;
        try {
            //game = new GamePanel();
        } catch (Exception e) {
            e.printStackTrace();
        }
        //showGameScreen(game);


        showMainScreen();

        window.add(rootPanel);

        // window image at the top left (very small and unreadable at the moment)
        window.setIconImage(new ImageIcon("src/main/resources/screen/mainMenuPanel/logo.png").getImage());
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setResizable(false);
        window.pack();
        window.setLocationRelativeTo(null);
        window.setVisible(true);

    }
}