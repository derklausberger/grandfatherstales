package GUI;

import main.Main;
import objectClasses.Abstract.Item;
import objectClasses.Armor;
import objectClasses.Enum.RarityType;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;

public class RewardPanel extends JPanel {

    private final float SCALE_FACTOR = 2f;
    private final int ICON_SIZE = (int) (25 * SCALE_FACTOR);

    private BufferedImage backgroundImage;

    private final JPanel statContainer = new JPanel();

    private final JLabel
            leftItem = new JLabel(),
            middleItem = new JLabel(),
            rightItem = new JLabel(),
            statName = new JLabel(),
            oldStatLabel = new JLabel(),
            newStatLabel = new JLabel(),
            valueChangeArrow = new JLabel(),
            confirmButton = new JLabel();

    private final Item[] rewardItems = new Item[3];
    private boolean itemIsSelected = false;
    private int selectedItem = -1;

    public RewardPanel() {

        setPreferredSize(new Dimension((int) (173 * SCALE_FACTOR), (int) (193 * SCALE_FACTOR)));
        setLayout(null);
        setVisible(false);

        init();
    }

    private void init() {

        // Loads the reward background image and fonts for the stats
        File statFontFile = new File("src/main/resources/fonts/DePixelBreit.ttf");
        Font font = null;
        BufferedImage chestImage = null, helmet = null;
        try {
            backgroundImage = ImageIO.read(new File("src/main/resources/screen/rewardPanel/reward.png"));
            chestImage = ImageIO.read(new File("src/main/resources/screen/rewardPanel/rewardChest.png"));
            font = Font.createFont(Font.TRUETYPE_FONT, statFontFile);

            helmet = ImageIO.read(new File("src/main/resources/item/armor/hat/headClothRare.png"));

        } catch (Exception e) {
            e.printStackTrace();
        }
        rewardItems[0] = new Armor("helmet", RarityType.Common, helmet, 8);
        rewardItems[1] = new Armor("helmet", RarityType.Common, helmet, 2);
        rewardItems[2] = new Armor("helmet", RarityType.Common, helmet, 4);

        // Creates the chest icon in the top center
        JLabel chestIcon = new JLabel();
        chestIcon.setPreferredSize(new Dimension(102, 102));
        chestIcon.setIcon(new ImageIcon(chestImage.getScaledInstance(102, 102, Image.SCALE_SMOOTH)));

        add(chestIcon);
        chestIcon.setBounds(
                (int) (61 * SCALE_FACTOR),
                (int) (24 * SCALE_FACTOR),
                chestIcon.getPreferredSize().width,
                chestIcon.getPreferredSize().height);


        // Creates the container for the three items to choose
        JPanel rewardItemContainer = new JPanel();
        rewardItemContainer.setPreferredSize(new Dimension((int) (105 * SCALE_FACTOR), (int) (27 * SCALE_FACTOR)));
        rewardItemContainer.setLayout(new BoxLayout(rewardItemContainer, BoxLayout.X_AXIS));
        rewardItemContainer.setOpaque(false);

        leftItem.setIcon(new ImageIcon(rewardItems[0].getImage().getScaledInstance(ICON_SIZE, ICON_SIZE, Image.SCALE_SMOOTH)));
        leftItem.setBackground(new Color(0x67422E1C, true));
        middleItem.setIcon(new ImageIcon(rewardItems[1].getImage().getScaledInstance(ICON_SIZE, ICON_SIZE, Image.SCALE_SMOOTH)));
        middleItem.setBackground(new Color(0x67422E1C, true));
        rightItem.setIcon(new ImageIcon(rewardItems[2].getImage().getScaledInstance(ICON_SIZE, ICON_SIZE, Image.SCALE_SMOOTH)));
        rightItem.setBackground(new Color(0x67422E1C, true));

        rewardItemContainer.add(Box.createHorizontalStrut((int) (1 * SCALE_FACTOR)));
        rewardItemContainer.add(leftItem);
        rewardItemContainer.add(Box.createHorizontalStrut((int) (14 * SCALE_FACTOR)));
        rewardItemContainer.add(middleItem);
        rewardItemContainer.add(Box.createHorizontalStrut((int) (14 * SCALE_FACTOR)));
        rewardItemContainer.add(rightItem);

        add(rewardItemContainer);
        rewardItemContainer.setBounds(
                (int) (34 * SCALE_FACTOR),
                (int) (87 * SCALE_FACTOR),
                rewardItemContainer.getPreferredSize().width,
                rewardItemContainer.getPreferredSize().height);


        // Creates the stat container and labels
        statContainer.setPreferredSize(new Dimension((int) (135 * SCALE_FACTOR), (int) (8 * SCALE_FACTOR)));
        statContainer.setLayout(new BoxLayout(statContainer, BoxLayout.X_AXIS));
        statContainer.setOpaque(false);

        add(statContainer);
        statContainer.setBounds(
                (int) (19 * SCALE_FACTOR),
                (int) (136 * SCALE_FACTOR),
                statContainer.getPreferredSize().width,
                statContainer.getPreferredSize().height);


        statName.setFont(font.deriveFont(16f));
        oldStatLabel.setFont(font.deriveFont(16f));
        valueChangeArrow.setFont(font.deriveFont(16f));
        newStatLabel.setFont(font.deriveFont(16f));

        statContainer.add(statName);
        statContainer.add(Box.createHorizontalStrut((int) (10 * SCALE_FACTOR)));
        statContainer.add(oldStatLabel);
        statContainer.add(Box.createHorizontalStrut((int) (10 * SCALE_FACTOR)));
        statContainer.add(valueChangeArrow);
        statContainer.add(Box.createHorizontalStrut((int) (10 * SCALE_FACTOR)));
        statContainer.add(newStatLabel);

        // Creates the confirm button at the bottom
        confirmButton.setPreferredSize(new Dimension((int) (135 * SCALE_FACTOR), (int) (12 * SCALE_FACTOR)));
        confirmButton.setMinimumSize(new Dimension((int) (135 * SCALE_FACTOR), (int) (12 * SCALE_FACTOR)));
        confirmButton.setMaximumSize(new Dimension((int) (135 * SCALE_FACTOR), (int) (12 * SCALE_FACTOR)));
        confirmButton.setHorizontalAlignment(JLabel.CENTER);
        confirmButton.setText("Confirm");
        confirmButton.setFont(font.deriveFont(20f));
        confirmButton.setForeground(new Color(0x422E1C));

        add(confirmButton);
        confirmButton.setBounds(
                (int) (19 * SCALE_FACTOR),
                (int) (173 * SCALE_FACTOR),
                confirmButton.getPreferredSize().width,
                confirmButton.getPreferredSize().height);

        toggleVisibility(0, false, 2);
        createListeners();
    }

    private void toggleVisibility(int itemIndex, boolean isVisible, int eventType) {

        // Toggles the container's stats visibility

        if (!itemIsSelected) {
            switch (itemIndex) {
                case 0 -> leftItem.setOpaque(isVisible);
                case 1 -> middleItem.setOpaque(isVisible);
                case 2 -> rightItem.setOpaque(isVisible);
            }
            for (Component label : statContainer.getComponents()) {
                label.setVisible(isVisible);
            }
        } else if (eventType == 1) {
            // If a label was clicked, not hovered

            leftItem.setOpaque(false);
            middleItem.setOpaque(false);
            rightItem.setOpaque(false);

            switch (itemIndex) {
                case 0 -> leftItem.setOpaque(true);
                case 1 -> middleItem.setOpaque(true);
                case 2 -> rightItem.setOpaque(true);
            }
        } else {
            // If an item is selected and another one is hovered,
            // closes the function, without displaying new stats
            return;
        }

        displayStats(itemIndex, isVisible);
    }

    private void displayStats(int itemIndex, boolean isVisible) {

        if (isVisible) {
            String statType = rewardItems[itemIndex].getItemStatName();
            int oldValue, newValue;

            // Sets the value to the attack damage of the character
            if (statType.equals("Attack")) {
                oldValue = GamePanel.player.getAttackDamage();
                newValue = rewardItems[itemIndex].getStatValue();

            } else {
                // If the character already has this type of armor,
                // subtracts it from the combined block amount
                oldValue = GamePanel.player.getBlockAmount();
                try {
                    newValue = oldValue - GamePanel.player.getArmorPiece(
                            rewardItems[itemIndex].getItemName()).getStatValue()
                            + rewardItems[itemIndex].getStatValue();
                } catch (Exception e) {
                    newValue = oldValue + rewardItems[itemIndex].getStatValue();
                }
            }
            // Sets the labels' text
            statName.setText(statType);
            oldStatLabel.setText(String.valueOf(oldValue));
            valueChangeArrow.setText("->");
            newStatLabel.setText(String.valueOf(newValue));

        } else {
            // If nothing is hovered or selected,
            // asks the player to do so
            valueChangeArrow.setText("Choose a reward");
            valueChangeArrow.setVisible(true);
        }
    }

    private boolean confirmItem() {

        if (selectedItem == -1) return false;

        GamePanel.player.addItem(rewardItems[selectedItem]);
        itemIsSelected = false;
        toggleVisibility(selectedItem, false, 1);
        selectedItem = -1;
        return true;
    }

    private void createListeners() {

        leftItem.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                super.mousePressed(e);

                itemIsSelected = selectedItem != 0;
                if (itemIsSelected) selectedItem = 0;
                else selectedItem = -1;

                toggleVisibility(0, true, 1);
                AudioManager.play("S - c1");
            }
        });
        leftItem.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                super.mouseEntered(e);

                toggleVisibility(0, true, 2);
            }
        });
        leftItem.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseExited(MouseEvent e) {
                super.mouseEntered(e);

                toggleVisibility(0, false, 2);
            }
        });

        middleItem.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                super.mousePressed(e);

                itemIsSelected = selectedItem != 1;
                if (itemIsSelected) selectedItem = 1;
                else selectedItem = -1;

                toggleVisibility(1, true, 1);
                AudioManager.play("S - c1");
            }
        });
        middleItem.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                super.mouseEntered(e);

                toggleVisibility(1, true, 2);
            }
        });
        middleItem.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseExited(MouseEvent e) {
                super.mouseEntered(e);

                toggleVisibility(1, false, 2);
            }
        });

        rightItem.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                super.mousePressed(e);

                itemIsSelected = selectedItem != 2;
                if (itemIsSelected) selectedItem = 2;
                else selectedItem = -1;

                toggleVisibility(2, true, 1);
                AudioManager.play("S - c1");
            }
        });
        rightItem.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                super.mouseEntered(e);

                toggleVisibility(2, true, 2);
            }
        });
        rightItem.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseExited(MouseEvent e) {
                super.mouseEntered(e);

                toggleVisibility(2, false, 2);
            }
        });

        confirmButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                super.mousePressed(e);

                if (confirmItem()) {
                    Main.toggleRewardScreen();
                    InventoryPanel.loadInventory();
                }
                AudioManager.play("S - c1");
            }
        });
        confirmButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                super.mouseEntered(e);

                confirmButton.setForeground(Color.black);
                AudioManager.play("S - h1");
            }
        });
        confirmButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseExited(MouseEvent e) {
                super.mouseEntered(e);

                confirmButton.setForeground(new Color(0x422E1C));
            }
        });
    }

    public void paintComponent(Graphics g) {
        g.drawImage(new ImageIcon(backgroundImage).getImage(), 0, 0,
                this.getPreferredSize().width, this.getPreferredSize().height,
                null);
    }
}
