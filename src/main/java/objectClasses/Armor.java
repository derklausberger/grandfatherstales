package objectClasses;

import objectClasses.Abstract.Item;
import objectClasses.Enum.RarityType;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Armor extends Item {
    private int blockAmount;

    public Armor(String itemName, RarityType rarity, BufferedImage image, int blockAmount) {
            super("Defense", itemName, rarity, image);
            this.blockAmount = blockAmount;
        }

    public int getBlockAmount() {
        return blockAmount;
    }

    public void setBlockAmount(int blockAmount) {
        this.blockAmount = blockAmount;
    }

    @Override
    public int getStatValue() {
        return blockAmount;
    }
}
