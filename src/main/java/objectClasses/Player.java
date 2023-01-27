package objectClasses;

import utilityClasses.AnimationFrame;
import GUI.GamePanel;
import utilityClasses.InputHandler;
import utilityClasses.MissingItemException;
import objectClasses.Abstract.Entity;
import objectClasses.Abstract.Item;
import objectClasses.Enum.EntityType;
import objectClasses.Enum.RarityType;
import utilityClasses.ResourceLoader;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Player extends Entity {

    @Override
    public void update(Game game) {

    }

    @Override
    public void hit() {

    }


    private InputHandler keyHandler;

    // Differentiate between drawing player or health bar
    // (because of layers when drawing)
    boolean drawPlayer = true;

    // armorItems stores all armor pieces (chest, helmet, boots, shield,..)
    private final Map<String, Armor> armorItems = new HashMap<>();
    private Weapon weapon;


    private int life;
    private boolean invincibility;
    private int invincibilityCooldown;
    private int invincibilityDuration;

    public Player(int positionX, int positionY, int movementSpeed, int healthPoints, int life, EntityType entityType, int viewDirection) {

        super(positionX, positionY, movementSpeed, healthPoints, entityType, viewDirection);
        this.life = life;
        this.invincibility = true;
        this.invincibilityCooldown = 0;
        this.keyHandler = new InputHandler();
        super.setAttackDelay(15);

        loadItems();
    }

    private void loadItems() {

        // Load infos from a file, which items the player holds and load all images
        ResourceLoader rl = ResourceLoader.getResourceLoader();
        BufferedImage sword = rl.getBufferedImage("/item/weapon/sable/sableCommon.png");

        setWeapon(new Weapon("sword", RarityType.Common, sword, 10, 100));
    }

    public void addItem(Item item) {

        // If the item is a weapon
        // Note: attackRange is currently always 100,
        // it has to be changed for different weapons later on
        if (item.getItemStatName().equals("Attack")) {
            setWeapon(new Weapon(item.getItemName(), item.getRarity(), item.getImage(), item.getStatValue(), 100));

            // If the item is an armor piece
        } else if (item.getItemStatName().equals("Defense")) {
            addArmorPiece(new Armor(item.getItemName(), item.getRarity(), item.getImage(), item.getStatValue()));
        }
    }

    public void triggerInvincibility() {
        this.invincibilityCooldown = 55;
        this.invincibilityDuration = 10; // -> safe for 20
    }

    public void reduceInvincibilityCooldown() {
        if (GamePanel.isDead) return;
        if (this.invincibilityDuration > 0) {
            this.invincibilityDuration--;
        } else {
            this.invincibilityDuration = 0;
            if (this.invincibilityCooldown <= 0) {
                this.invincibilityCooldown = 0;
                this.invincibility = false;
            } else {
                this.invincibilityCooldown--;
            }
        }
    }

    public boolean isInvincible() {
        return invincibility;
    }

    public void setInvincibility(boolean invincibility) {

        this.invincibility = invincibility;
    }

    public int getInvincibilityCooldown() {
        return invincibilityCooldown;
    }

    public int getLife() {
        return life;
    }

    public void setLife(int life) {
        this.life = life;
    }

    // Weapon functions
    public void setWeapon(Weapon weapon) {
        this.weapon = weapon;
        super.setAttackDamage(weapon.getAttackDamage());
    }

    public Weapon getWeapon() {
        return this.weapon;
    }

    // Armor functions
    public void addArmorPiece(Armor armor) {
        this.armorItems.put(armor.getItemName(), armor);
        updateArmorAmount();
    }

    public Armor getArmorPiece(String piece) throws MissingItemException {

        if (armorItems.containsKey(piece)) {
            return armorItems.get(piece);
        }
        throw new MissingItemException(piece);
    }

    private void updateArmorAmount() {
        int blockAmount = 0;
        for (Armor piece : armorItems.values()) {
            blockAmount += piece.getBlockAmount();
        }
        super.setBlockAmount(blockAmount);
    }

    public InputHandler getKeyHandler() {
        return this.keyHandler;
    }

    public void removeKeyHandler() {

        this.keyHandler = null;
    }

    @Override
    public void draw(Graphics2D graph2D, Game game, GamePanel gamePanel) {

        int x = (int) ((GamePanel.WINDOW_WIDTH - 30) / 2);
        int y = (int) ((GamePanel.WINDOW_HEIGHT - 50) / 2);

        if (drawPlayer) {
            // The current frame
            AnimationFrame frame = getEntityFrames(getCurrentAnimationType())[getCurrentFrame()];

            // Draws the character
            graph2D.drawImage(frame.getImage(),
                    x + frame.getXOffset(),
                    y + frame.getYOffset(),
                    frame.getWidth(),
                    frame.getHeight(),
                    gamePanel
            );
        } else {

            double healthBarWidth = (double) 262 / (double) this.getMaxHealthPoints() * (double) this.getCurrentHealthPoints();
            Shape healthBar = new Rectangle2D.Double(
                    24,
                    64,
                    healthBarWidth,
                    14);

            graph2D.setPaint(new Color(0x7d0027));
            graph2D.fill(healthBar);

            if (this.getCurrentHealthPoints() < this.getMaxHealthPoints()) {
                Shape healthBarBackground = new Rectangle2D.Double(
                        24 + healthBarWidth,
                        64,
                        262 - healthBarWidth,
                        14);

                graph2D.setPaint(new Color(0xA6505050, true));
                graph2D.fill(healthBarBackground);


            }
        }
        drawPlayer = !drawPlayer;

    }
}
