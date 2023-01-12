package main;

import GUI.*;
import objectClasses.Game;

import javax.swing.*;
import java.awt.*;

public class Main {

    private static final int
            STARTSCREEN_WIDTH = GamePanel.WINDOW_WIDTH,    // image is 1000px - 990
            STARTSCREEN_HEIGTH = GamePanel.WINDOW_HEIGHT;   // image is 563px - 553

    public static String currentScreen, previousScreen;
    private static JPanel rootPanel, optionsPanel;
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
        optionsPanel.requestFocusInWindow();
    }

    public static void showLoadingScreen() {
        cardLayout.show(rootPanel, "Loading");
    }

    public static void showGameScreen(JPanel gamePanel) {

        // Switches to the game panel when created and called
        rootPanel.add(gamePanel, "Game");
        cardLayout.show(rootPanel, "Game");
        previousScreen = currentScreen;
        currentScreen = "Game";
    }

    public static void showPreviousScreen() {

        cardLayout.show(rootPanel, previousScreen);

        String s = previousScreen;
        previousScreen = currentScreen;
        currentScreen = s;
    }

    public static void main(String[] args) {

        JFrame window = new JFrame("Grandfather's Tales");
        window.setPreferredSize(new Dimension(STARTSCREEN_WIDTH, STARTSCREEN_HEIGTH));

        // "Main" panel that acts as the foundation
        rootPanel = new JPanel();
        rootPanel.setLayout(cardLayout);

        // Creates the start, options and loading panel and adds it to the root panel
        MainMenuPanel mainPanel = new MainMenuPanel();
        rootPanel.add(mainPanel, "Main Menu");

        optionsPanel = new OptionsMenuPanel();
        rootPanel.add(optionsPanel, "Options");

        LoadingPanel loadingPanel = new LoadingPanel();
        rootPanel.add(loadingPanel, "Loading");

        window.add(rootPanel);
        showMainScreen();
        //showLoadingScreen();

        // window image at the top left (very small and unreadable at the moment)
        window.setIconImage(new ImageIcon("src/main/resources/screen/mainMenuPanel/logo.png").getImage());
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setResizable(false);
        window.pack();
        window.setLocationRelativeTo(null);
        window.setVisible(true);

        System.out.println(AudioManager.getFrames("M - mainTheme"));
    }
}

