package main;

import GUI.*;
import objectClasses.Game;
import org.xml.sax.SAXException;

import javax.swing.*;
import javax.xml.parsers.ParserConfigurationException;
import java.awt.*;
import java.io.IOException;

public class Main {

    // Window size for all screens
    public static final int
            DEFAULT_WINDOW_WIDTH = 1000,
            DEFAULT_WINDOW_HEIGHT = 563;

    // Only change the scaling factor for a smaller screen
    public static final float
            SCALING_FACTOR = 1f;

    public static String currentScreen, previousScreen;
    private static JPanel rootPanel;
    private static JLayeredPane layeredPane;

    // CardLayout to switch between screens
    private static final CardLayout cardLayout = new CardLayout();

    public static void showMainScreen() {

        cardLayout.show(rootPanel, "Main Menu");
        previousScreen = currentScreen;
        currentScreen = "Main Menu";
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
                (int)(DEFAULT_WINDOW_WIDTH * SCALING_FACTOR),
                (int)(DEFAULT_WINDOW_HEIGHT * SCALING_FACTOR));

        InventoryPanel inventoryPanel = new InventoryPanel();
        inventoryPanel.setSize(inventoryPanel.getPreferredSize());

        // Creates the layered pane to hold the game and inventory panels
        layeredPane = new JLayeredPane();
        layeredPane.setPreferredSize(new Dimension(
                (int) (DEFAULT_WINDOW_WIDTH * SCALING_FACTOR),
                (int) (DEFAULT_WINDOW_HEIGHT * SCALING_FACTOR)));
        layeredPane.setSize(layeredPane.getPreferredSize());

        // Still doesn't center the inventory perfectly vertically,
        // Therefore, moved 20px upwards
        int x = (layeredPane.getWidth() - inventoryPanel.getWidth()) / 2;
        int y = (layeredPane.getHeight() - inventoryPanel.getHeight()) / 2;
        inventoryPanel.setLocation(x, y - 20);

        layeredPane.add(gamePanel, JLayeredPane.DEFAULT_LAYER);
        layeredPane.add(inventoryPanel, JLayeredPane.PALETTE_LAYER);

        rootPanel.add(layeredPane, "Game");
        cardLayout.show(rootPanel, "Game");
        //previousScreen = currentScreen;
        currentScreen = "Game";

    }

    public static void toggleInventory() {

        layeredPane.getComponent(0).setVisible(!layeredPane.getComponent(0).isVisible());
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

        currentScreen = "Game";
        GamePanel game = null;
        try {
            game = new GamePanel();
        } catch (Exception e) {
            e.printStackTrace();
        }
        //showGameScreen(game);
        //InventoryPanel panel = new InventoryPanel();


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