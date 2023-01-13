package GUI;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;

public class InventoryPanel extends JPanel {

    private Image backgroundImage;

    public InventoryPanel() {

        setPreferredSize(new Dimension(350, 400));
        setLayout(new GridBagLayout());
        this.setVisible(false);

        try {
            backgroundImage = new ImageIcon(ImageIO.read(new File("src/main/resources/screen/inventoryPanel/inventory.jpg"))).getImage();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void paintComponent(Graphics g) {
        g.drawImage(backgroundImage, 0, 0,
                null);
    }
}
