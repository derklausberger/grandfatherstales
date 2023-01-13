package objectClasses;

import GUI.AnimationFrame;
import GUI.GamePanel;
import GUI.InputHandler;
import objectClasses.Abstract.Entity;
import objectClasses.Enum.EntityTypes;
import objectClasses.Enum.RarityType;

import java.awt.*;
import java.awt.geom.*;

public class Player extends Entity {
    private int life;
    private InputHandler keyHandler;
    private boolean invincibility;
    private int invincibilityCooldown;
    private int invincibilityDuration;

    public Player(int positionX, int positionY, int movementSpeed, int healthPoints, int life, EntityTypes entityTypes) {

        super(positionX, positionY, movementSpeed, healthPoints, entityTypes);
        this.life = life;
        this.invincibility = true;
        this.invincibilityCooldown = 0;
        this.keyHandler = new InputHandler();

        super.setWeapon(new Weapon("Sword", RarityType.Common, null, 10, 100));
        super.setArmor(new Armor("Armor", RarityType.Common, null, 3));

        super.setAttackDelay(15);

    }

    public void triggerInvincibility() {
        this.invincibilityCooldown = 55;
        this.invincibilityDuration = 10; // -> safe for 20
    }

    public void reduceInvincibilityCooldown() {
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

    public int getInvincibilityCooldown() {
        return invincibilityCooldown;
    }

    public int getLife() {
        return life;
    }

    public void setLife(int life) {
        this.life = life;
    }

    public InputHandler getKeyHandler() {
        return this.keyHandler;
    }


    @Override
    public void draw(Graphics2D graph2D, Game game, GamePanel gamePanel) {

        int x = (int) ((GamePanel.WINDOW_WIDTH - GamePanel.NEW_TILE_SIZE) / 2);
        int y = (int) ((GamePanel.WINDOW_HEIGHT - GamePanel.NEW_TILE_SIZE) / 2);

        if (this.getCurrentHealthPoints() < this.getMaxHealthPoints()) {
            Shape healthBarOutside = new Rectangle2D.Double(
                    (double) Math.floorDiv(GamePanel.WINDOW_WIDTH - GamePanel.NEW_TILE_SIZE, 2) - 1,
                    (double) Math.floorDiv(GamePanel.WINDOW_HEIGHT - GamePanel.NEW_TILE_SIZE, 2) - 11,
                    GamePanel.NEW_TILE_SIZE + 2,
                    3);

            Shape healthBarInside = new Rectangle2D.Double(
                    (double) (GamePanel.WINDOW_WIDTH - GamePanel.NEW_TILE_SIZE) / 2,
                    (double) (GamePanel.WINDOW_HEIGHT - GamePanel.NEW_TILE_SIZE) / 2 - 10,
                    (double) GamePanel.NEW_TILE_SIZE / (double) this.getMaxHealthPoints() * (double) this.getCurrentHealthPoints(),
                    3);

            graph2D.setPaint(Color.black);
            graph2D.draw(healthBarOutside);
            graph2D.setPaint(Color.RED);
            graph2D.fill(healthBarInside);
        }

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
    }
}
