package GUI;

import main.Main;
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

    public static Image backgroundImage = null;
    public static BufferedImage bufferedLogo = null;

    private static final int
            STARTSCREEN_WIDTH = 990,    // image is 1000px
            STARTSCREEN_HEIGTH = 553;   // image is 563px

    private Map<String, ImageIcon> buttonImages = new HashMap<>();

    private JPanel
            textContainer,
            menuButtons = new JPanel();

    private JLabel
            startButton = new JLabel(),
            optionsButton = new JLabel(),
            quitButton = new JLabel(),
            logo = new JLabel();


    public MainMenuPanel() {

        // centers the container
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        loadImages();
        loadAudio();
        createListeners();
    }

    private void loadImages() {

        // sets static background image
        //backgroundImage = new ImageIcon("src/main/resources/screen/mainMenuPanel/mainMenu.png").getImage();
        try {
            backgroundImage = new ImageIcon(ImageIO.read(new File("src/main/resources/screen/mainMenuPanel/mainMenu.png"))).getImage();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // reads GFT-logo
        // buffered images can be rescaled
        try {
            bufferedLogo = ImageIO.read(new File("src/main/resources/screen/mainMenuPanel/logo.png"));
            logo.setIcon(new ImageIcon(Objects.requireNonNull(bufferedLogo).getScaledInstance(640, 370, Image.SCALE_SMOOTH)));
        } catch (IOException e) {
            e.printStackTrace();
        }

        // reads menu button images and saves them to a map
        // to not have to reload a file for every mouse event
        BufferedImage bufferedNew = null;
        BufferedImage bufferedOptions = null;
        BufferedImage bufferedQuit = null;
        BufferedImage bufferedNewHover = null;
        BufferedImage bufferedOptionsHover = null;
        BufferedImage bufferedQuitHover = null;
        try {
            bufferedNew = ImageIO.read(new File("src/main/resources/screen/optionsMenuPanel/new.png"));
            bufferedOptions = ImageIO.read(new File("src/main/resources/screen/optionsMenuPanel/options.png"));
            bufferedQuit = ImageIO.read(new File("src/main/resources/screen/optionsMenuPanel/quit.png"));

            bufferedNewHover = ImageIO.read(new File("src/main/resources/screen/optionsMenuPanel/newShadow.png"));
            bufferedOptionsHover = ImageIO.read(new File("src/main/resources/screen/optionsMenuPanel/optionsShadow.png"));
            bufferedQuitHover = ImageIO.read(new File("src/main/resources/screen/optionsMenuPanel/quitShadow.png"));

            buttonImages.put("new", new ImageIcon(bufferedNew));
            buttonImages.put("options", new ImageIcon(bufferedOptions));
            buttonImages.put("quit", new ImageIcon(bufferedQuit));
            buttonImages.put("newHover", new ImageIcon(bufferedNewHover));
            buttonImages.put("optionsHover", new ImageIcon(bufferedOptionsHover));
            buttonImages.put("quitHover", new ImageIcon(bufferedQuitHover));

        } catch (IOException e) {
            e.printStackTrace();
        }

        // adds all buttons and a gap between them to the container
        menuButtons.add(startButton);
        menuButtons.add(Box.createVerticalStrut(15));
        menuButtons.add(optionsButton);
        menuButtons.add(Box.createVerticalStrut(15));
        menuButtons.add(quitButton);

        // first, sets all buttons and background color to be invisible
        menuButtons.setVisible(false);
        menuButtons.setOpaque(false);

        // centers vertically
        menuButtons.setLayout(new BoxLayout(menuButtons, BoxLayout.Y_AXIS));

        // sets icons and centers horizontally inside the container
        startButton.setIcon(new ImageIcon(Objects.requireNonNull(bufferedNew)));
        startButton.setAlignmentX(JComponent.CENTER_ALIGNMENT);

        optionsButton.setIcon(new ImageIcon(Objects.requireNonNull(bufferedOptions)));
        optionsButton.setAlignmentX(JComponent.CENTER_ALIGNMENT);

        quitButton.setIcon(new ImageIcon(Objects.requireNonNull(bufferedQuit)));
        quitButton.setAlignmentX(JComponent.CENTER_ALIGNMENT);

        // adds GFT-logo and buttons to text and outer container
        textContainer.add(logo);
        textContainer.add(menuButtons);

        this.add(textContainer);
    }

    private void loadAudio() {

        // Initializes the SoundManager
        AudioManager.init();
        // Loads the audio files
        AudioManager.load("src/main/resources/audio/music/Main Theme.wav", "M - mainTheme");
        AudioManager.load("src/main/resources/audio/sounds/Sword Swipe 1.wav", "S - ssw1");
        AudioManager.load("src/main/resources/audio/sounds/Sword Swipe 2.wav", "S - ssw2");
        AudioManager.load("src/main/resources/audio/sounds/Sword Hit 1.wav", "S - sh1");
        AudioManager.load("src/main/resources/audio/sounds/Hover 1.wav", "S - h1");
        AudioManager.load("src/main/resources/audio/sounds/Click 1.wav", "S - c1");
        AudioManager.load("src/main/resources/audio/sounds/Click 2.wav", "S - c2");

        AudioManager.setMusicVolume(1.0f);
        AudioManager.setSoundVolume(1.0f);

        // Attempt to create a smoother transition when looping
        //AudioManager.loop("M - mainTheme", 0, 175, AudioManager.getFrames("M - mainTheme") - 1);
    }

    private void createListeners() {

        textContainer.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                super.mousePressed(e);

                // Creates an array of the sound names
                String[] sounds = {"S - ssw1", "S - ssw1", "S - sh1"};
                // Generates a random index
                int index = (int)(Math.random() * sounds.length);
                AudioManager.play(sounds[index]);

                if (menuButtons.isVisible()) {
                    logo.setVisible(true);
                    menuButtons.setVisible(false);
                } else {
                    logo.setVisible(false);
                    menuButtons.setVisible(true);
                }
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
                            GamePanel gamePanel = new GamePanel();
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
                // stops the runtime application
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
        g.drawImage(backgroundImage, 0, 0, null);
    }

}