package objectClasses.Abstract;

import objectClasses.Enum.RarityType;
import java.awt.image.BufferedImage;

public abstract class Item {
    private String itemName;
    private String itemStatName;

    private RarityType rarity;
    private BufferedImage image;

    public Item(String itemStatName, String itemName, RarityType rarity, BufferedImage image) {
        this.itemStatName = itemStatName;
        this.itemName = itemName;
        this.rarity = rarity;
        this.image = image;
    }

    public BufferedImage getImage() {
        return image;
    }

    public String getItemName() {
        return itemName;
    }

    public String getItemStatName() {
        return itemStatName;
    }

    public RarityType getRarity() {
        return rarity;
    }
    public int getStatValue() {
        return 0;
    }
}
