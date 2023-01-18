package GUI;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Objects;

public class InventoryPanel extends JPanel {

    private Image backgroundImage;

    public InventoryPanel() {

        setPreferredSize(new Dimension(195, 232));
        setLayout(null);
        this.setVisible(true);

        //GamePanel.playerArrayList.get(0).getWeapon();

        try {
            backgroundImage = new ImageIcon(ImageIO.read(new File("src/main/resources/screen/inventoryPanel/inventory.jpg"))).getImage();
        } catch (IOException e) {
            e.printStackTrace();
        }

        BufferedImage icon1 = null, icon2 = null, icon3 = null, icon4 = null;
        try {
            icon1 = ImageIO.read(new File("src/main/resources/item/weapon/sable/sableCommon.png"));
            icon2 = ImageIO.read(new File("src/main/resources/item/armor/chestplate/chestChainCommon.png"));
            icon3 = ImageIO.read(new File("src/main/resources/item/armor/shield/armIronCommon.png"));
            icon4 = ImageIO.read(new File("src/main/resources/item/weapon/dagger/daggerCommon.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }


        JPanel passiveItemContainer = new JPanel();
        passiveItemContainer.setPreferredSize(new Dimension(25, 120));
        passiveItemContainer.setLayout(new BoxLayout(passiveItemContainer, BoxLayout.Y_AXIS));
        passiveItemContainer.setOpaque(false);

        for (int i = 0; i < 4; i++) {
            JLabel label = new JLabel();
            label.setIcon(new ImageIcon(icon3.getScaledInstance(25,25, Image.SCALE_SMOOTH)));
            label.setAlignmentX(JComponent.CENTER_ALIGNMENT);

            //passiveItemContainer.add(Box.createVerticalStrut(1));
            passiveItemContainer.add(label);
            passiveItemContainer.add(Box.createVerticalStrut(6));
        }

        add(passiveItemContainer);
        passiveItemContainer.setBounds(20, 14,
                passiveItemContainer.getPreferredSize().width,
                passiveItemContainer.getPreferredSize().height);


        JPanel activeItemContainer = new JPanel();
        activeItemContainer.setPreferredSize(new Dimension(50, 200));
        activeItemContainer.setLayout(new BoxLayout(activeItemContainer, BoxLayout.Y_AXIS));
        activeItemContainer.setOpaque(false);

        for (int i = 0; i < 2; i++) {
            JLabel label = new JLabel();
            label.setIcon(new ImageIcon(icon2.getScaledInstance(48,48, Image.SCALE_SMOOTH)));
            label.setAlignmentX(JComponent.CENTER_ALIGNMENT);

            activeItemContainer.add(Box.createVerticalStrut(1));
            activeItemContainer.add(label);
            activeItemContainer.add(Box.createVerticalStrut(1));

            if (i == 0) {
                activeItemContainer.add(Box.createVerticalStrut(100));
            }
        }

        add(activeItemContainer);
        activeItemContainer.setBounds(175, 50,
                activeItemContainer.getPreferredSize().width,
                activeItemContainer.getPreferredSize().height);


        JPanel statsContainer = new JPanel();
        statsContainer.setPreferredSize(new Dimension(125, 200));
        statsContainer.setLayout(new BoxLayout(statsContainer, BoxLayout.Y_AXIS));
        statsContainer.setOpaque(false);

        statsContainer.add(Box.createHorizontalStrut(75));
        for (int i = 0; i < 3; i++) {
            JLabel label = new JLabel();
            label.setAlignmentX(JComponent.CENTER_ALIGNMENT);
            label.setPreferredSize(new Dimension(50, 25));

            statsContainer.add(Box.createVerticalStrut(25));
            statsContainer.add(label);

            if (i == 2) {
                label.setText("100");
            } else if (i == 1) {
                label.setText("6");
                statsContainer.add(Box.createVerticalStrut(25));
            } else {
                label.setText("5");
                statsContainer.add(Box.createVerticalStrut(25));
            }
        }

        add(statsContainer);
        statsContainer.setBounds(225, 50,
                statsContainer.getPreferredSize().width,
                statsContainer.getPreferredSize().height);



// Add the container to the inventory panel

        JPanel inventoryContainer = new JPanel();
        inventoryContainer.setPreferredSize(new Dimension(350, 100));
        inventoryContainer.setLayout(new BoxLayout(inventoryContainer, BoxLayout.Y_AXIS));
        inventoryContainer.setOpaque(false);

        // Create the first row panel
        JPanel firstRow = new JPanel();
        firstRow.setLayout(new BoxLayout(firstRow, BoxLayout.X_AXIS));
        firstRow.setOpaque(false);

        // Create the second row panel
        JPanel secondRow = new JPanel();
        secondRow.setLayout(new BoxLayout(secondRow, BoxLayout.X_AXIS));
        secondRow.setOpaque(false);

        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 7; j++) {
                JLabel label = new JLabel();
                label.setIcon(new ImageIcon(icon1.getScaledInstance(48,48, Image.SCALE_SMOOTH)));
                if(i == 0) {
                    firstRow.add(Box.createHorizontalStrut(1));
                    firstRow.add(label);
                    firstRow.add(Box.createHorizontalStrut(1));
                } else {
                    secondRow.add(Box.createHorizontalStrut(1));
                    secondRow.add(label);
                    secondRow.add(Box.createHorizontalStrut(1));
                }
            }
        }
        // Add the two rows to the inventory container
        inventoryContainer.add(firstRow);
        inventoryContainer.add(secondRow);

        add(inventoryContainer);
        inventoryContainer.setBounds(0, (int)(this.getPreferredSize().height) - (int)(inventoryContainer.getPreferredSize().height),
                inventoryContainer.getPreferredSize().width,
                inventoryContainer.getPreferredSize().height);


    }

    public void paintComponent(Graphics g) {
        g.drawImage(backgroundImage, 0, 0,
                null);
    }
}
