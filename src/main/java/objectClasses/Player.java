package objectClasses;

import objectClasses.Enum.AnimationType;
import utilityClasses.*;
import GUI.GamePanel;
import objectClasses.Abstract.Entity;
import objectClasses.Abstract.Item;
import objectClasses.Enum.EntityType;
import objectClasses.Enum.RarityType;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

public class Player extends Entity {

    private boolean attackHitBoxDrawn;
    private InputHandler keyHandler;

    // armorItems stores all armor pieces (chest, helmet, boots, shield,..)
    private final Map<String, Armor> armorItems = new HashMap<>();
    private Weapon weapon;

    private int life;
    private boolean isInvincible;
    private final int invincibilityDuration;

    Inventory inventory;

    public Player(int positionX, int positionY, int movementSpeed, int healthPoints, int life, EntityType entityType, int viewDirection) {

        super(positionX, positionY, movementSpeed, healthPoints, entityType, viewDirection);
        this.life = life;
        this.isInvincible = false;
        this.invincibilityDuration = 200;
        this.keyHandler = new InputHandler();
        super.setAttackDelay(15);
        inventory = new Inventory(this);

        loadItems();

        setCurrentAnimationType(AnimationType.resting.toString());
    }

    public Inventory getInventory() {
        return inventory;
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

        if (!isInvincible()) {
            isInvincible = true;
            new Timer(invincibilityDuration, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    ((Timer) e.getSource()).stop();
                    isInvincible = false;
                }
            }).start();
        }
    }

    public boolean isInvincible() {
        return isInvincible;
    }

    public void setInvincible(boolean invincible) {

        this.isInvincible = invincible;
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
    public void draw(Graphics2D graph2D, Game game) {

        int x = (int) ((GamePanel.WINDOW_WIDTH - 30) / 2);
        int y = (int) ((GamePanel.WINDOW_HEIGHT - 50) / 2);

        // The current frame
        AnimationFrame frame = getEntityFrames(getCurrentAnimationType())[getCurrentFrame()];

        // Draws the character
        graph2D.drawImage(frame.getImage(),
                x + frame.getXOffset(),
                y + frame.getYOffset(),
                frame.getWidth(),
                frame.getHeight(),
                null
        );
    }

    public boolean optionsPressed() {

        if (getKeyHandler().optionsPressed) {
            getKeyHandler().optionsPressed = false;
            return true;
        }
        return false;
    }

    public boolean inventoryPressed() {

        if (getKeyHandler().inventoryPressed) {
            getKeyHandler().inventoryPressed = false;
            return true;
        }
        return false;
    }

    public boolean interactPressed() {

        return getKeyHandler().interactPressed;
    }


    public boolean isResting() {

        return getKeyHandler().isInState(0);
    }

    public boolean isAttacking() {

        return getKeyHandler().isInState(2);
    }

    public boolean isWalking() {

        return getKeyHandler().isInState(1);
    }

    public int getAttackDirection() {

        return getKeyHandler().attackDirection;
    }

    public int getWalkingDirection() {

        return getKeyHandler().walkingDirection;
    }

    public int getLastDirection() {

        return getKeyHandler().lastDirection;
    }

    public int getCurrentDirection() {

        return getKeyHandler().getCurrentDirection();
    }

    public boolean attack() {

        // Returns true if the character is allowed to attack
        // -> i.e. allowed to draw a hit box
        if (getAttackAnimationFrame() >= 4 && !attackHitBoxDrawn) {
            attackHitBoxDrawn = true;
            setWalkingAnimationFrame(0);
            setCoolDown(getAttackDelay());

            return true;
        }
        return false;
    }

    @Override
    protected void endRestingAnimation() {
        setRestingAnimationFrame(0);
    }

    @Override
    protected void endWalkingAnimation() {
        setWalkingAnimationFrame(1);
    }

    @Override
    protected void endAttackingAnimation() {

        setWalkingAnimationFrame(1);
        setAttackAnimationFrame(1);
        attackHitBoxDrawn = false;
        setCoolDown(0);

        // Sets last direction so the character faces the same
        // direction after attacking as during the animation
        //getKeyHandler().lastDirection = getKeyHandler().attackDirection;

        // Releases hit direction to be newly assigned when attacking again
        getKeyHandler().attackDirection = 10000;

        if (getKeyHandler().movementKeyPressed()) {
            getKeyHandler().setCurrentState(1);
            setCurrentAnimationType(AnimationType.walking.toString());
        } else {
            getKeyHandler().setCurrentState(0);
            setCurrentAnimationType(AnimationType.resting.toString());
        }
    }

    @Override
    protected void endDyingAnimation() {

        new Timer(2000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                setLife(getLife() - 1);
                ((Timer) e.getSource()).stop();
                if (getLife() == 0) {
                    GamePanel.showGameOverScreen();
                } else {
                    GamePanel.reloadLevel();
                }
                stopAnimationTimer();
                setDyingAnimationFrame(0);
            }
        }).start();
    }

    @Override
    protected void handleAnimation() {

        if (isDead()) {
            if (getDyingAnimationFrame() <= 4) {
                if (proceedAnimationFrame(AnimationType.dying.toString(),
                        getAnimationFrameLimit(AnimationType.dying.toString()) - 1)) {
                    endDyingAnimation();
                }
            }
        } else if (isAttacking()) {
            if (proceedAnimationFrame(AnimationType.attacking.toString(),
                    getAnimationFrameLimit(AnimationType.attacking.toString()))) {
                endAttackingAnimation();
            }

        } else if (isWalking()) {
            if (proceedAnimationFrame(AnimationType.walking.toString(),
                    getAnimationFrameLimit(AnimationType.walking.toString()))) {
                endWalkingAnimation();
            }

        } else if (isResting()) {
            if (proceedAnimationFrame(AnimationType.resting.toString(),
                    getAnimationFrameLimit(AnimationType.resting.toString()))) {
                endRestingAnimation();
            }
        }
    }

    @Override
    public void updateKnockBack(Game game) {
    }

    @Override
    public void startKnockBack() {
    }
}
