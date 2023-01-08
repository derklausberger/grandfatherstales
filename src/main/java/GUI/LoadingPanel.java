package GUI;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Objects;

public class LoadingPanel extends JPanel {

    private JPanel container;

    public LoadingPanel() {

        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        loadImages();

    }

    private void loadImages() {

        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));

        JPanel content = new JPanel();
        content.setOpaque(false);
        content.setPreferredSize(new Dimension(600, 330));

        JPanel content2 = new JPanel();
        content2.setLayout(new BorderLayout());
        content2.setOpaque(false);

        JLabel logo = new JLabel();
        logo.setIcon(new ImageIcon(Objects.requireNonNull(MainMenuPanel.bufferedLogo).getScaledInstance(600, 330, Image.SCALE_SMOOTH)));
        logo.setOpaque(false);

        JLabel character = new JLabel();

        BufferedImage bufferedCharacter = null;
        try {
            bufferedCharacter = ImageIO.read(new File("src/main/resources/character/walking/D1.png"));
            character.setIcon(new ImageIcon(Objects.requireNonNull(bufferedCharacter)));

        } catch (Exception e) {
            e.printStackTrace();
        }

        //content.setLayout(new BoxLayout(content, BoxLayout.PAGE_AXIS));
        container.add(Box.createVerticalStrut(50));
        content.add(logo);

        content2.add(character);
        container.add(content);
        container.add(Box.createVerticalStrut(20));
        container.add(content2);
        this.add(container);
    }

    public void paintComponent(Graphics g) {
        g.drawImage(MainMenuPanel.backgroundImage, 0, 0, null);
    }
}
