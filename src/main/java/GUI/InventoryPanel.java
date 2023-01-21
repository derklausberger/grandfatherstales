package GUI;

import objectClasses.Player;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class InventoryPanel extends JPanel {

    private static final float SCALE_FACTOR = 2f;
    private static final int ICON_SIZE = (int) (25 * SCALE_FACTOR);

    private BufferedImage backgroundImage;

    private final static Map<Integer, Map> inventoryItems = new HashMap<>();
    private final static JLabel
            helmet = new JLabel(),
            chest = new JLabel(),
            legs = new JLabel(),
            boots = new JLabel(),
            weapon = new JLabel(),
            shield = new JLabel(),
            damage = new JLabel(),
            block = new JLabel(),
            health = new JLabel();

    public InventoryPanel() {

        setPreferredSize(new Dimension((int) (195 * SCALE_FACTOR), (int) (243 * SCALE_FACTOR)));
        setLayout(null);
        setVisible(false);

        init();
    }

    private void init() {

        /** Echt schirch, bitte verbessern, wenn möglich */

        // Creates a map for every label, accessible by the name
        // and stores it in another map
        for (int i = 0; i < 9; i++) {
            Map<String, JLabel> icons = new HashMap<>(1);
            switch (i) {
                case 0 -> icons.put("helmet", helmet);
                case 1 -> icons.put("chest", chest);
                case 2 -> icons.put("legs", legs);
                case 3 -> icons.put("boots", boots);
                case 4 -> icons.put("weapon", weapon);
                case 5 -> icons.put("shield", shield);
                case 6 -> icons.put("damage", damage);
                case 7 -> icons.put("block", block);
                case 8 -> icons.put("health", health);
            }
            inventoryItems.put(i, icons);
        }

        Player player = GamePanel.player;

        // Loads the inventory background image and fonts for the stats container
        File statValueFontFile = new File("src/main/resources/fonts/DePixelBreit.ttf"),
                statNameFontFile = new File("src/main/resources/fonts/DePixelKlein.ttf");
        Font statValueFont = null, statNameFont = null;
        try {
            backgroundImage = ImageIO.read(new File("src/main/resources/screen/inventoryPanel/inventory.png"));

            statValueFont = Font.createFont(Font.TRUETYPE_FONT, statValueFontFile);
            statNameFont = Font.createFont(Font.TRUETYPE_FONT, statNameFontFile);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Creates the passive item container on the left (armor)
        JPanel passiveItemContainer = new JPanel();
        passiveItemContainer.setPreferredSize(new Dimension((int) (27 * SCALE_FACTOR), (int) (120 * SCALE_FACTOR)));
        passiveItemContainer.setLayout(new BoxLayout(passiveItemContainer, BoxLayout.Y_AXIS));
        passiveItemContainer.setOpaque(false);

        // Positions the four labels as placeholders without an icon
        for (int i = 0; i < 4; i++) {
            Map<String, JLabel> inventoryIcons = inventoryItems.get(i);

            for (JLabel label : inventoryIcons.values()) {

                label.setPreferredSize(new Dimension(ICON_SIZE, ICON_SIZE));
                label.setMinimumSize(new Dimension(ICON_SIZE, ICON_SIZE));
                label.setMaximumSize(new Dimension(ICON_SIZE, ICON_SIZE));
                label.setAlignmentX(JComponent.CENTER_ALIGNMENT);

                passiveItemContainer.add(Box.createVerticalStrut((int) (1 * SCALE_FACTOR)));
                passiveItemContainer.add(label);
            }
            if (i < 3) passiveItemContainer.add(Box.createVerticalStrut((int) (5 * SCALE_FACTOR)));
            else passiveItemContainer.add(Box.createVerticalStrut((int) (1 * SCALE_FACTOR)));
        }

        add(passiveItemContainer);
        passiveItemContainer.setBounds(
                (int) (13 * SCALE_FACTOR),
                (int) (24 * SCALE_FACTOR),
                passiveItemContainer.getPreferredSize().width,
                passiveItemContainer.getPreferredSize().height);


        // Creates the active item container in the middle (sword, shield)
        JPanel activeItemContainer = new JPanel();
        activeItemContainer.setPreferredSize(new Dimension((int) (27 * SCALE_FACTOR), (int) (120 * SCALE_FACTOR)));
        activeItemContainer.setLayout(new BoxLayout(activeItemContainer, BoxLayout.Y_AXIS));
        activeItemContainer.setOpaque(false);

        // Positions the two labels as placeholders without an icon
        for (int i = 4; i < 6; i++) {
            Map<String, JLabel> inventoryIcons = inventoryItems.get(i);

            for (JLabel label : inventoryIcons.values()) {

                label.setPreferredSize(new Dimension(ICON_SIZE, ICON_SIZE));
                label.setMinimumSize(new Dimension(ICON_SIZE, ICON_SIZE));
                label.setMaximumSize(new Dimension(ICON_SIZE, ICON_SIZE));
                label.setAlignmentX(JComponent.CENTER_ALIGNMENT);

                activeItemContainer.add(Box.createVerticalStrut((int) (1 * SCALE_FACTOR)));
                activeItemContainer.add(label);
                activeItemContainer.add(Box.createVerticalStrut((int) (1 * SCALE_FACTOR)));
            }
            if (i == 4) {
                activeItemContainer.add(Box.createVerticalStrut((int) (66 * SCALE_FACTOR)));
            }
        }
        add(activeItemContainer);
        activeItemContainer.setBounds(
                (int) (72 * SCALE_FACTOR),
                (int) (24 * SCALE_FACTOR),
                activeItemContainer.getPreferredSize().width,
                activeItemContainer.getPreferredSize().height);


        // Creates the stats container on the right
        JPanel statsContainer = new JPanel();
        statsContainer.setPreferredSize(new Dimension((int) (35 * SCALE_FACTOR), (int) (93 * SCALE_FACTOR)));
        statsContainer.setLayout(new BoxLayout(statsContainer, BoxLayout.Y_AXIS));
        statsContainer.setOpaque(false);

        // Positions stat name and value labels as placeholders without a value
        for (int i = 6; i < inventoryItems.size(); i++) {

            JLabel statName = new JLabel();
            statName.setPreferredSize(new Dimension((int) (35 * SCALE_FACTOR), (int) (8 * SCALE_FACTOR)));

            statsContainer.add(Box.createVerticalStrut((int) (2 * SCALE_FACTOR)));
            statsContainer.add(statName);
            statsContainer.add(Box.createVerticalStrut((int) (2 * SCALE_FACTOR)));
            statName.setFont(statNameFont.deriveFont(16f));
            statName.setForeground(new Color(0x5a3e28));

            switch (i) {
                case 6 -> statName.setText("Attack");
                case 7 -> statName.setText("Defense");
                default -> statName.setText("Health");
            }

            Map<String, JLabel> inventoryIcons = inventoryItems.get(i);
            for (JLabel label : inventoryIcons.values()) {

                label.setPreferredSize(new Dimension((int) (35 * SCALE_FACTOR), (int) (8 * SCALE_FACTOR)));
                label.setFont(statValueFont.deriveFont(16f));
                statsContainer.add(label);

                if (i < 8) statsContainer.add(Box.createVerticalStrut((int) (11 * SCALE_FACTOR)));
            }
        }

        add(statsContainer);
        statsContainer.setBounds(
                (int) (147 * SCALE_FACTOR),
                (int) (24 * SCALE_FACTOR),
                statsContainer.getPreferredSize().width,
                statsContainer.getPreferredSize().height);

        loadInventory();

        /** Currently not needed since no items
         can be held in the inventory */
        /*
        // Creates the inventory container at the bottom
        JPanel inventoryContainer = new JPanel();
        inventoryContainer.setPreferredSize(new Dimension((int) (183 * SCALE_FACTOR), (int) (53 * SCALE_FACTOR)));
        inventoryContainer.setLayout(new BoxLayout(inventoryContainer, BoxLayout.Y_AXIS));
        inventoryContainer.setOpaque(false);

        // Creates the first inventory row
        JPanel firstRow = new JPanel();
        firstRow.setLayout(new BoxLayout(firstRow, BoxLayout.X_AXIS));
        firstRow.setOpaque(false);

        // Creates the second inventory row
        JPanel secondRow = new JPanel();
        secondRow.setLayout(new BoxLayout(secondRow, BoxLayout.X_AXIS));
        secondRow.setOpaque(false);

        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 7; j++) {
                JLabel label = new JLabel();
                label.setIcon(new ImageIcon(icon1.getScaledInstance((int) (25 * SCALE_FACTOR),(int) (25 * SCALE_FACTOR), Image.SCALE_SMOOTH)));
                if (i == 0) {
                    firstRow.add(Box.createHorizontalStrut((int) (1 * SCALE_FACTOR)));
                    firstRow.add(label);
                } else {
                    secondRow.add(Box.createHorizontalStrut((int) (1 * SCALE_FACTOR)));
                    secondRow.add(label);
                }
            }
        }
        // Adds the two rows to the inventory container
        inventoryContainer.add(firstRow);
        inventoryContainer.add(secondRow);

        add(inventoryContainer);
        inventoryContainer.setBounds(
                (int) (6 * SCALE_FACTOR),
                (int) (184 * SCALE_FACTOR),
                inventoryContainer.getPreferredSize().width,
                inventoryContainer.getPreferredSize().height);

         */
    }

    public static void loadInventory() {

        // Loads the images of the armor pieces of the character
        for (int i = 0; i < 4; i++) {
            Map<String, JLabel> inventoryIcons = inventoryItems.get(i);
            for (Map.Entry<String, JLabel> pair : inventoryIcons.entrySet()) {

                try {
                    inventoryIcons.get(pair.getKey()).setIcon(
                            new ImageIcon(GamePanel.player.getArmorPiece(pair.getKey())
                                    .getImage().getScaledInstance(
                                            ICON_SIZE,
                                            ICON_SIZE,
                                            Image.SCALE_SMOOTH)));
                } catch (MissingItemException e) {
                    System.out.println(e);
                }
            }
        }

        // Loads the images of the weapon and shield of the character
        for (int i = 4; i < 6; i++) {
            BufferedImage image;
            Map<String, JLabel> inventoryIcons = inventoryItems.get(i);
            for (Map.Entry<String, JLabel> pair : inventoryIcons.entrySet()) {

                try {
                    if (i == 4) image = GamePanel.player.getWeapon().getImage();
                    else image = GamePanel.player.getArmorPiece(pair.getKey()).getImage();

                    inventoryIcons.get(pair.getKey()).setIcon(
                            new ImageIcon(image
                                    .getScaledInstance(
                                            (int) (25 * SCALE_FACTOR),
                                            (int) (25 * SCALE_FACTOR),
                                            Image.SCALE_SMOOTH)));

                } catch (MissingItemException e) {
                    System.out.println(e);
                }
            }
        }

        // Loads the stat values
        for (int i = 6; i < inventoryItems.size(); i++) {

            Map<String, JLabel> inventoryIcons = inventoryItems.get(i);
            for (Map.Entry<String, JLabel> pair : inventoryIcons.entrySet()) {

                switch (i) {
                    case 6 -> inventoryIcons.get(pair.getKey()).setText("" + GamePanel.player.getAttackDamage());
                    case 7 -> inventoryIcons.get(pair.getKey()).setText("" + GamePanel.player.getBlockAmount());
                    default -> inventoryIcons.get(pair.getKey()).setText("" + GamePanel.player.getCurrentHealthPoints());
                }
            }
        }
    }

    public void paintComponent(Graphics g) {
        g.drawImage(new ImageIcon(backgroundImage).getImage(), 0, 0,
                this.getPreferredSize().width, this.getPreferredSize().height,
                null);
    }
}
