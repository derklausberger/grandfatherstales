package GUI;

import main.Main;
import utilityClasses.AudioManager;
import utilityClasses.ResourceLoader;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

public class MainMenuPanel extends JPanel {

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

        init();
    }

    private void init() {

        setLayout(new GridBagLayout());
        setPreferredSize(new Dimension(
                (int) (Main.DEFAULT_WINDOW_WIDTH * Main.SCALING_FACTOR),
                (int) (Main.DEFAULT_WINDOW_HEIGHT * Main.SCALING_FACTOR)));

        loadImages();
        loadAudio();

        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 1;
        c.gridheight = 1;
        c.insets = new Insets(10, 10, 10, 10);
        c.anchor = GridBagConstraints.CENTER;
        add(logo, c);

        createMenuButtons(c);
        createListeners();
    }

    private void loadImages() {

        ResourceLoader rl = ResourceLoader.getResourceLoader();

        // Loads static background image
        backgroundImage = rl.getBufferedImage("/screen/mainMenuPanel/mainMenu.png");

        // Loads the logo image and adds it to the main menu screen
        int LOGO_WIDTH = (int) (1274 * Main.SCALING_FACTOR / 2);
        int LOGO_HEIGHT = (int) (735 * Main.SCALING_FACTOR / 2);

        logo.setIcon(new ImageIcon(
                rl.getBufferedImage("/screen/mainMenuPanel/logo.png")
                        .getScaledInstance(LOGO_WIDTH, LOGO_HEIGHT, Image.SCALE_SMOOTH)));

        // Reads menu button images and saves them to a map,
        // to not have to reload a file for every mouse event
        addButtonImage("new", "/screen/optionsMenuPanel/new.png");
        addButtonImage("options", "/screen/optionsMenuPanel/options.png");
        addButtonImage("quit", "/screen/optionsMenuPanel/quit.png");
        addButtonImage("newHover", "/screen/optionsMenuPanel/newShadow.png");
        addButtonImage("optionsHover", "/screen/optionsMenuPanel/optionsShadow.png");
        addButtonImage("quitHover", "/screen/optionsMenuPanel/quitShadow.png");
    }

    private void addButtonImage(String name, String path) {

        ResourceLoader rl = ResourceLoader.getResourceLoader();
        BufferedImage bufferedImage = rl.getBufferedImage(path);
        buttonImages.put(name, new ImageIcon(
                bufferedImage.getScaledInstance(
                        (int) (bufferedImage.getWidth() * Main.SCALING_FACTOR),
                        -1, Image.SCALE_SMOOTH)));
    }

    private void createMenuButtons(GridBagConstraints c) {

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

        add(menuButtons, c);
    }

    private void loadAudio() {

        // Initializes the SoundManager
        AudioManager.init();
        // Loads the audio files
        AudioManager.load("/music/Main Theme.wav", "M - mainTheme");
        AudioManager.load("/sounds/GUI/Hover 1.wav", "S - hover1");
        AudioManager.load("/sounds/GUI/Click 1.wav", "S - click1");
        AudioManager.load("/sounds/GUI/Click 2.wav", "S - click2");

        AudioManager.setMusicVolume(1.0f);
        AudioManager.setSoundVolume(1.0f);
    }

    private void createListeners() {

        addMouseListener(new MouseAdapter() {
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

            }
        });
        startButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                super.mouseEntered(e);

                startButton.setIcon(buttonImages.get("newHover"));
                AudioManager.play("S - hover1");
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

                AudioManager.play("S - click1");
                Main.showOptionsScreen();
            }
        });
        optionsButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                super.mouseEntered(e);

                optionsButton.setIcon(buttonImages.get("optionsHover"));
                AudioManager.play("S - hover1");
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

                Main.closeWindow();
            }
        });
        quitButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                super.mouseEntered(e);

                quitButton.setIcon(buttonImages.get("quitHover"));
                AudioManager.play("S - hover1");
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