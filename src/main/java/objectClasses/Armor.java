package objectClasses;

import objectClasses.Abstract.Item;
import objectClasses.Enum.RarityType;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Armor extends Item {
    private int blockAmount;

    public int getBlockAmount() {
        return blockAmount;
    }

    public void setBlockAmount(int blockAmount) {
        this.blockAmount = blockAmount;
    }

    public Armor(String name, RarityType rarity, BufferedImage image, int blockAmount) {
        super(name, rarity, image);
        this.blockAmount = blockAmount;
    }
}
