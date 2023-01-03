package objectClasses;

import objectClasses.Abstract.Item;
import objectClasses.Enum.RarityType;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class Weapon extends Item {
    private int attackAmount;
    private int attackRange;

    private ArrayList<BufferedImage> entityAnimationAppearance;
    private int entityAnimationDuration;

    public Weapon(String name, RarityType rarity, BufferedImage entityAppearance, int attackAmount,
                  int attackRange, ArrayList<BufferedImage> entityAnimationAppearance,
                  int entityAnimationDuration) {

        super(name, rarity, entityAppearance);

        this.attackAmount = attackAmount;
        this.attackRange = attackRange;
        this.entityAnimationAppearance = entityAnimationAppearance;
        this.entityAnimationDuration = entityAnimationDuration;
    }
}
