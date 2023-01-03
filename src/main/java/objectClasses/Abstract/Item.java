package objectClasses.Abstract;

import objectClasses.Enum.RarityType;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

public abstract class Item {
    private String name;
    private RarityType rarity;
    private BufferedImage entityAppearance;

    public Item(String name, RarityType rarity, BufferedImage entityAppearance) {
        this.name = name;
        this.rarity = rarity;
        this.entityAppearance = entityAppearance;
    }
}
