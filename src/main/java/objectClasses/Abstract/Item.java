package objectClasses.Abstract;

import objectClasses.Enum.RarityType;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public abstract class Item {
    private String name;
    private RarityType rarity;
    private Image image;

    public Item(String name, RarityType rarity, Image image) {
        this.name = name;
        this.rarity = rarity;
        this.image = image;
    }
}
