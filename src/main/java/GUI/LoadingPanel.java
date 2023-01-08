package GUI;


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
    private Image[] images;

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

        // Set the directory path
        String dirPath = "src/main/resources/character/walking";

        // Create a File object for the directory
        File dir = new File(dirPath);

        // Get a list of the PNG files in the directory
        File[] files = dir.listFiles();

        // Create an array to hold the images
        images = new Image[9];

        // Load the images from the files
        int index = 0;
        for (int i = 0; i < files.length; i++) {
            try {
                if (i >= 18 && i <= 26) {
                    images[index] = ImageIO.read(files[i]);
                    index++;
                }
            } catch (IOException e) {
                e.printStackTrace();
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
        logo.setIcon(new ImageIcon(Objects.requireNonNull(MainMenuPanel.bufferedLogo).getScaledInstance(600, 330, Image.SCALE_SMOOTH)));
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
        g.drawImage(MainMenuPanel.backgroundImage, 0, 0, null);
    }
}
