package objectClasses;

import objectClasses.Abstract.Item;
import objectClasses.Enum.RarityType;

import java.awt.image.BufferedImage;

public class Armor extends Item {
    private int blockAmount;

    public Armor(String name, RarityType rarity, BufferedImage entityAppearance, int blockAmount) {

        super(name, rarity, entityAppearance);

        this.blockAmount = blockAmount;
    }
}
