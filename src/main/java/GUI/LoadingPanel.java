package GUI;


import main.Main;
import utilityClasses.ResourceLoader;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Objects;

public class LoadingPanel extends JPanel implements ActionListener {

    private JPanel container;
    private Timer timer;
    private int currentFrame = 0;
    private BufferedImage[] images;

    private JLabel character;

    public LoadingPanel() {

        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        loadImages();

        timer = new Timer(110, this);
        timer.start();
    }

    public void actionPerformed(ActionEvent e) {

        currentFrame++;

        // If we've reached the end of the animation, start over from the beginning
        if (currentFrame >= 9) {
            currentFrame = 0;
        }
        character.setIcon(new ImageIcon(Objects.requireNonNull(images[currentFrame])));
    }

    private void loadImages() {

        ResourceLoader rl = ResourceLoader.getResourceLoader();

        // Create a File object for the directory
        File dir = rl.getFile("/character/walking");

        // Create an array to hold the images
        images = new BufferedImage[9];

        int index = 0;
        for (int i = 0; i < dir.listFiles().length; i++) {

            if (i >= 18 && i <= 26) {

                images[index] = rl.getBufferedImage("/character/walking/" + dir.list()[i]);
                index++;

            }
        }

        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));

        JPanel content = new JPanel();
        content.setOpaque(false);
        content.setPreferredSize(new Dimension(600, 330));

        JPanel content2 = new JPanel();
        content2.setAlignmentX(JComponent.RIGHT_ALIGNMENT);
        content2.setOpaque(false);

        JLabel logo = new JLabel();
        logo.setOpaque(false);

        character = new JLabel();

        container.add(Box.createVerticalStrut(50));
        content.add(logo);

        content2.add(character);
        content2.add(Box.createHorizontalStrut(500));

        container.add(content);
        container.add(Box.createVerticalStrut(20));
        container.add(content2);
        this.add(container);

    }

    public void paintComponent(Graphics g) {
        g.drawImage(MainMenuPanel.backgroundImage, 0, 0,
                (int) (Main.DEFAULT_WINDOW_WIDTH * Main.SCALING_FACTOR),
                (int) (Main.DEFAULT_WINDOW_HEIGHT * Main.SCALING_FACTOR),
                null);
    }
}
