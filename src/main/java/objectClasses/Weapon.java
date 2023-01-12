package objectClasses;

import objectClasses.Abstract.Item;
import objectClasses.Enum.RarityType;

import java.awt.*;

public class Weapon extends Item {
    private int attackAmount;
    private int attackRange;

    public int getAttackAmount() {
        return attackAmount;
    }

    public void setAttackAmount(int attackAmount) {
        this.attackAmount = attackAmount;
    }

    public int getAttackRange() {
        return attackRange;
    }

    public void setAttackRange(int attackRange) {
        this.attackRange = attackRange;
    }

    public Weapon(String name, RarityType rarity, Image image, int attackAmount, int attackRange) {
        super(name, rarity, image);
        this.attackAmount = attackAmount;
        this.attackRange = attackRange;
    }
}
