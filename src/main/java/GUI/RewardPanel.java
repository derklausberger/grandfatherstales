package GUI;

import com.google.gson.JsonObject;
import main.Main;
import objectClasses.Abstract.Item;
import objectClasses.Armor;
import objectClasses.Enum.RarityType;
import objectClasses.Game;
import objectClasses.Player;
import objectClasses.Weapon;
import utilityClasses.AudioManager;
import utilityClasses.ResourceLoader;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class RewardPanel extends JPanel {

    private final float SCALE_FACTOR = 2f;

    private BufferedImage backgroundImage;

    private JPanel statContainer;

    private final JLabel
            leftItem = new JLabel(),
            middleItem = new JLabel(),
            rightItem = new JLabel(),
            statName = new JLabel(),
            oldStatLabel = new JLabel(),
            newStatLabel = new JLabel(),
            valueChangeArrow = new JLabel(),
            confirmButton = new JLabel();

    private ArrayList<Item> rewardItemPool = new ArrayList<>();
    private final Item[] rewardItems = new Item[3];
    private int selectedItem = -1;

    private Game game;

    public RewardPanel(Game game) {

        this.game = game;
        init();
    }

    private void init() {

        setPreferredSize(new Dimension((int) (173 * SCALE_FACTOR), (int) (193 * SCALE_FACTOR)));
        setLayout(null);
        setVisible(false);

        // Loads the reward background image
        ResourceLoader rl = ResourceLoader.getResourceLoader();

        Font font = rl.getDefaultTextFont();
        BufferedImage chestImage = rl.getBufferedImage("/screen/rewardPanel/rewardChest.png");
        backgroundImage = rl.getBufferedImage("/screen/rewardPanel/reward.png");

        // Creates all content from the reward window
        createChestIcon(chestImage);
        createRewardItemContainer();
        createStatContainer(font);
        createConfirmButton(font);
        createListeners();

        toggleVisibility(0, false, 2);
        loadItems();
    }

    private void createChestIcon(BufferedImage chestImage) {

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
    }

    private void createRewardItemContainer() {

        // Creates the container for the three items to choose
        JPanel rewardItemContainer = new JPanel();
        rewardItemContainer.setPreferredSize(new Dimension((int) (105 * SCALE_FACTOR), (int) (27 * SCALE_FACTOR)));
        rewardItemContainer.setLayout(new BoxLayout(rewardItemContainer, BoxLayout.X_AXIS));
        rewardItemContainer.setOpaque(false);

        // Creates the spacing between the items
        rewardItemContainer.add(Box.createHorizontalStrut((int) (1 * SCALE_FACTOR)));
        rewardItemContainer.add(leftItem);
        rewardItemContainer.add(Box.createHorizontalStrut((int) (14 * SCALE_FACTOR)));
        rewardItemContainer.add(middleItem);
        rewardItemContainer.add(Box.createHorizontalStrut((int) (14 * SCALE_FACTOR)));
        rewardItemContainer.add(rightItem);

        leftItem.setBackground(new Color(0x67422E1C, true));
        middleItem.setBackground(new Color(0x67422E1C, true));
        rightItem.setBackground(new Color(0x67422E1C, true));

        // Sets the positioning of the reward items in the on the reward window
        rewardItemContainer.setBounds(
                (int) (34 * SCALE_FACTOR),
                (int) (87 * SCALE_FACTOR),
                rewardItemContainer.getPreferredSize().width,
                rewardItemContainer.getPreferredSize().height);

        add(rewardItemContainer);
    }

    private void createStatContainer(Font font) {

        // Creates the stat container and labels
        statContainer = new JPanel();
        statContainer.setPreferredSize(new Dimension((int) (135 * SCALE_FACTOR), (int) (8 * SCALE_FACTOR)));
        statContainer.setLayout(new BoxLayout(statContainer, BoxLayout.X_AXIS));
        statContainer.setOpaque(false);

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

        add(statContainer);
    }

    private void createConfirmButton(Font font) {

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
    }

    private void loadItems() {

        if (rewardItemPool.size() > 0) {
            rewardItemPool = new ArrayList<>();
        }
        rewardItemPool.addAll(loadWeaponsFromFile());
        rewardItemPool.addAll(loadArmorFromFile());
    }

    private ArrayList<Item> loadWeaponsFromFile() {

        ResourceLoader rl = ResourceLoader.getResourceLoader();
        JsonObject root = rl.readStaticJsonFile("weapon.json");
        ArrayList<Item> items = new ArrayList<>();

        // Loops through every type (axe, sword,..)
        for (String weaponName : root.keySet()) {
            JsonObject weaponClass = root.get(weaponName).getAsJsonArray().get(0).getAsJsonObject();

            // Loops through all rarities of the type
            for (String rarity : weaponClass.keySet()) {
                String fileName = weaponClass.get(rarity).getAsJsonArray().get(0).getAsJsonObject().get("image")
                        .getAsJsonArray().get(0).getAsJsonObject().get("image").getAsString();

                RarityType rarityType = setRarity(rarity);
                BufferedImage weaponImage = rl.getBufferedImage(fileName);

                int attackDamage = weaponClass.get(rarity).getAsJsonArray().get(0)
                        .getAsJsonObject().get("itemStat").getAsJsonArray().get(0)
                        .getAsJsonObject().get("attackDamage").getAsInt();
                int attackRange = weaponClass.get(rarity).getAsJsonArray().get(0)
                        .getAsJsonObject().get("itemStat").getAsJsonArray()
                        .get(0).getAsJsonObject().get("attackRange").getAsInt();

                Weapon weapon = new Weapon(weaponName, rarityType, weaponImage, attackDamage, attackRange);
                items.add(weapon);
            }
        }
        return items;
    }

    private ArrayList<Item> loadArmorFromFile() {

        ResourceLoader rl = ResourceLoader.getResourceLoader();
        JsonObject root = rl.readStaticJsonFile("armor.json");

        ArrayList<Item> items = new ArrayList<>();

        // Loops through every type (chest, head,..)
        for (String armorTypeName : root.keySet()) {
            JsonObject armorType = root.get(armorTypeName).getAsJsonObject();

            // Loops through all items of the type
            for (String itemName : armorType.keySet()) {
                JsonObject armorPiece = armorType.get(itemName).getAsJsonArray().get(0).getAsJsonObject();

                for (String rarity : armorPiece.keySet()) {
                    String fileName = armorPiece.get(rarity).getAsJsonArray().get(0)
                            .getAsJsonObject().get("image").getAsJsonArray().get(0)
                            .getAsJsonObject().get("image").getAsString();

                    RarityType rarityType = setRarity(rarity);

                    BufferedImage img = rl.getBufferedImage(fileName);

                    int blockAmount = armorPiece.get(rarity).getAsJsonArray().get(0)
                            .getAsJsonObject().get("itemStat").getAsJsonArray().get(0)
                            .getAsJsonObject().get("blockAmount").getAsInt();

                    Armor armor = new Armor(armorTypeName, rarityType, img, blockAmount);
                    items.add(armor);
                }
            }
        }
        return items;
    }

    private RarityType setRarity(String rarity) {

        return switch (rarity) {
            case ("common") -> RarityType.Common;
            case ("rare") -> RarityType.Rare;
            case ("epic") -> RarityType.Epic;
            case ("legendary") -> RarityType.Legendary;
            case ("unique") -> RarityType.Unique;
            default -> null;
        };
    }

    public void setRewards() {

        leftItem.setIcon(getRandomItem(0));
        middleItem.setIcon(getRandomItem(1));
        rightItem.setIcon(getRandomItem(2));
    }

    private ImageIcon getRandomItem(int index) {

        int ICON_SIZE = (int) (25 * SCALE_FACTOR);
        rewardItems[index] = rewardItemPool.get((int) (Math.random() * rewardItemPool.size()));
        return new ImageIcon(rewardItems[index].getImage().getScaledInstance(ICON_SIZE, ICON_SIZE, Image.SCALE_SMOOTH));
    }

    private void toggleVisibility(int itemIndex, boolean isVisible, int eventType) {

        // Toggles the container's stats visibility
        if (selectedItem == -1) {
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
                oldValue = game.getPlayer().getAttackDamage();
                newValue = rewardItems[itemIndex].getStatValue();

            } else {
                // If the character already has this type of armor,
                // subtracts it from the combined block amount
                oldValue = game.getPlayer().getBlockAmount();
                try {
                    newValue = oldValue - game.getPlayer().getArmorPiece(
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
            for (Component label : statContainer.getComponents()) {
                label.setVisible(false);
            }
            valueChangeArrow.setText("Choose a reward");
            valueChangeArrow.setVisible(true);
        }
    }

    private boolean confirmItem() {

        if (selectedItem == -1) return false;

        game.getPlayer().addItem(rewardItems[selectedItem]);
        game.setOpeningChest(false);
        rewardItemPool.remove(rewardItems[selectedItem]);

        // Don't set selected item to -1 before calling the function
        toggleVisibility(-1, false, 1);
        selectedItem = -1;
        return true;
    }

    private void createListeners() {

        leftItem.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                super.mousePressed(e);

                if (selectedItem == 0) selectedItem = -1;
                else selectedItem = 0;

                toggleVisibility(0, true, 1);
                AudioManager.play("S - click1");
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

                if (selectedItem == 1) selectedItem = -1;
                else selectedItem = 1;

                toggleVisibility(1, true, 1);
                AudioManager.play("S - click1");
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

                if (selectedItem == 2) selectedItem = -1;
                else selectedItem = 2;

                toggleVisibility(2, true, 1);
                AudioManager.play("S - click1");
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
                    game.getPlayer().getInventory().loadInventory();
                }
                AudioManager.play("S - click1");
            }
        });
        confirmButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                super.mouseEntered(e);

                confirmButton.setForeground(Color.black);
                AudioManager.play("S - hover1");
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
