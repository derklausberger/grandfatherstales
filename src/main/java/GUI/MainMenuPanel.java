package GUI;

import main.Main;
import utilityClasses.AudioManager;
import utilityClasses.ResourceLoader;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class MainMenuPanel extends JPanel {

    private final int
            LOGO_WIDTH = (int) (1274 * Main.SCALING_FACTOR / 2),
            LOGO_HEIGHT = (int) (735 * Main.SCALING_FACTOR / 2);

    public static BufferedImage backgroundImage = null;

    private final Map<String, ImageIcon> buttonImages = new HashMap<>();

    private final JPanel
            menuButtons = new JPanel();

    private final JLabel
            startButton = new JLabel(),
            optionsButton = new JLabel(),
            quitButton = new JLabel(),
            logo = new JLabel();


    public MainMenuPanel() {

        this.setLayout(new GridBagLayout());
        this.setPreferredSize(new Dimension(
                (int) (Main.DEFAULT_WINDOW_WIDTH * Main.SCALING_FACTOR),
                (int) (Main.DEFAULT_WINDOW_HEIGHT * Main.SCALING_FACTOR)));

        init();
    }

    private void init() {

        ResourceLoader rl = ResourceLoader.getResourceLoader();

        // Loads static background image
        backgroundImage = rl.getBufferedImage("/screen/mainMenuPanel/mainMenu.png");
        
        // Loads the logo image and adds it to the main menu screen
        logo.setPreferredSize(new Dimension(LOGO_WIDTH, LOGO_HEIGHT));

        BufferedImage bufferedLogo = rl.getBufferedImage("/screen/mainMenuPanel/logo.png");
        logo.setIcon(new ImageIcon((bufferedLogo)
                .getScaledInstance(LOGO_WIDTH, LOGO_HEIGHT, Image.SCALE_SMOOTH)));

        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 1;
        c.gridheight = 1;
        c.insets = new Insets(10, 10, 10, 10);
        c.anchor = GridBagConstraints.CENTER;

        this.add(logo, c);


        // reads menu button images and saves them to a map
        // to not have to reload a file for every mouse event
        BufferedImage bufferedImage;

        bufferedImage = rl.getBufferedImage("/screen/optionsMenuPanel/new.png");
        buttonImages.put("new", new ImageIcon(
                bufferedImage.getScaledInstance(
                        (int) (bufferedImage.getWidth() * Main.SCALING_FACTOR),
                        -1, Image.SCALE_SMOOTH)));

        bufferedImage = rl.getBufferedImage("/screen/optionsMenuPanel/options.png");
        buttonImages.put("options", new ImageIcon(
                bufferedImage.getScaledInstance(
                        (int) (bufferedImage.getWidth() * Main.SCALING_FACTOR),
                        -1, Image.SCALE_SMOOTH)));

        bufferedImage = rl.getBufferedImage("/screen/optionsMenuPanel/quit.png");
        buttonImages.put("quit", new ImageIcon(
                bufferedImage.getScaledInstance(
                        (int) (bufferedImage.getWidth() * Main.SCALING_FACTOR),
                        -1, Image.SCALE_SMOOTH)));

        bufferedImage = rl.getBufferedImage("/screen/optionsMenuPanel/newShadow.png");
        buttonImages.put("newHover", new ImageIcon(
                bufferedImage.getScaledInstance(
                        (int) (bufferedImage.getWidth() * Main.SCALING_FACTOR), -1, Image.SCALE_SMOOTH)));

        bufferedImage = rl.getBufferedImage("/screen/optionsMenuPanel/optionsShadow.png");
        buttonImages.put("optionsHover", new ImageIcon(
                bufferedImage.getScaledInstance(
                        (int) (bufferedImage.getWidth() * Main.SCALING_FACTOR), -1, Image.SCALE_SMOOTH)));

        bufferedImage = rl.getBufferedImage("/screen/optionsMenuPanel/quitShadow.png");
        buttonImages.put("quitHover", new ImageIcon(
                bufferedImage.getScaledInstance(
                        (int) (bufferedImage.getWidth() * Main.SCALING_FACTOR), -1, Image.SCALE_SMOOTH)));


        // Sets icons and centers horizontally inside the container
        startButton.setIcon(buttonImages.get("new"));
        startButton.setAlignmentX(JComponent.CENTER_ALIGNMENT);

        optionsButton.setIcon(buttonImages.get("options"));
        optionsButton.setAlignmentX(JComponent.CENTER_ALIGNMENT);

        quitButton.setIcon(buttonImages.get("quit"));
        quitButton.setAlignmentX(JComponent.CENTER_ALIGNMENT);

        // Centers vertically
        menuButtons.setLayout(new BoxLayout(menuButtons, BoxLayout.Y_AXIS));

        // At first, sets all buttons and background color to be invisible
        menuButtons.setVisible(false);
        menuButtons.setOpaque(false);

        // Adds all buttons and a gap between them to the container
        menuButtons.add(startButton);
        menuButtons.add(Box.createVerticalStrut(15));
        menuButtons.add(optionsButton);
        menuButtons.add(Box.createVerticalStrut(15));
        menuButtons.add(quitButton);

        this.add(menuButtons, c);

        loadAudio();
        createListeners();
    }

    GamePanel gamePanel;

    private void loadAudio() {

        // Initializes the SoundManager
        AudioManager.init();
        // Loads the audio files
        AudioManager.load("/music/Main Theme.wav", "M - mainTheme");
        AudioManager.load("/sounds/GUI/Hover 1.wav", "S - h1");
        AudioManager.load("/sounds/GUI/Click 1.wav", "S - c1");
        AudioManager.load("/sounds/GUI/Click 2.wav", "S - c2");

        AudioManager.setMusicVolume(1.0f);
        AudioManager.setSoundVolume(1.0f);
    }

    private void createListeners() {

        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                super.mousePressed(e);

                menuButtons.setVisible(!menuButtons.isVisible());
                logo.setVisible(!logo.isVisible());
            }
        });


        startButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                super.mousePressed(e);

                Main.showLoadingScreen();

                // Start a new thread to load the game resources
                new Thread(new Runnable() {
                    @Override
                    public void run() {

                        // Create the game panel and add it to the main panel
                        try {
                            if (gamePanel != null) gamePanel = null;
                            gamePanel = new GamePanel();
                            Main.showGameScreen(gamePanel);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        });
        startButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                super.mouseEntered(e);

                startButton.setIcon(buttonImages.get("newHover"));
                AudioManager.play("S - h1");
            }
        });
        startButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseExited(MouseEvent e) {
                super.mouseEntered(e);

                startButton.setIcon(buttonImages.get("new"));
            }
        });

        optionsButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                super.mousePressed(e);

                AudioManager.play("S - c1");
                Main.showOptionsScreen();
            }
        });
        optionsButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                super.mouseEntered(e);

                optionsButton.setIcon(buttonImages.get("optionsHover"));
                AudioManager.play("S - h1");
            }
        });
        optionsButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseExited(MouseEvent e) {
                super.mouseEntered(e);

                optionsButton.setIcon(buttonImages.get("options"));
            }
        });

        quitButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                super.mousePressed(e);

                // gets the main window and disposes it
                JComponent comp = (JComponent) e.getSource();
                Window window = SwingUtilities.getWindowAncestor(comp);
                window.dispose();
                System.exit(0);
            }
        });
        quitButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                super.mouseEntered(e);

                quitButton.setIcon(buttonImages.get("quitHover"));
                AudioManager.play("S - h1");
            }
        });
        quitButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseExited(MouseEvent e) {
                super.mouseEntered(e);

                quitButton.setIcon(buttonImages.get("quit"));
            }
        });
    }

    public void paintComponent(Graphics g) {
        g.drawImage(backgroundImage, 0, 0,
                (int) (Main.DEFAULT_WINDOW_WIDTH * Main.SCALING_FACTOR),
                (int) (Main.DEFAULT_WINDOW_HEIGHT * Main.SCALING_FACTOR),
                null);
    }
}