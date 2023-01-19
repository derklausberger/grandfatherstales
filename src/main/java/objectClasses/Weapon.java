package objectClasses;

import objectClasses.Abstract.Item;
import objectClasses.Enum.RarityType;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Weapon extends Item {
    private int attackDamage;
    private int attackRange;

    public Weapon(String name, RarityType rarity, BufferedImage image, int attackDamage, int attackRange) {
        super(name, rarity, image);
        this.attackDamage = attackDamage;
        this.attackRange = attackRange;
    }

    public int getAttackDamage() {
        return attackDamage;
    }

    public void setAttackDamage(int attackDamage) {
        this.attackDamage = attackDamage;
    }

    public int getAttackRange() {
        return attackRange;
    }

    public void setAttackRange(int attackRange) {
        this.attackRange = attackRange;
    }

}
