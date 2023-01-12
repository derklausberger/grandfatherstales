package objectClasses;

import GUI.AnimationFrame;
import GUI.AudioManager;
import GUI.GamePanel;
import objectClasses.Abstract.Entity;
import objectClasses.Abstract.Item;
import objectClasses.Enum.RarityType;

import java.awt.*;
import java.awt.geom.*;
import java.util.ArrayList;

public class Player extends Entity {
    private int life;
    private Weapon weapon;
    private Armor armor;

    public int getLife() {
        return life;
    }

    public void setLife(int life) {
        this.life = life;
    }

    public Weapon getWeapon() {
        return weapon;
    }

    public void setWeapon(Weapon weapon) {
        this.weapon = weapon;
    }

    public Armor getArmor() {
        return armor;
    }

    public void setArmor(Armor armor) {
        this.armor = armor;
    }

    public Player(int positionX, int positionY, int movementSpeed, int healthPoints, int life) {

        super(positionX, positionY, movementSpeed, healthPoints);
        super.loadAnimationFrames("character");

        this.life = life;
        this.weapon = new Weapon("Sword", RarityType.Common, null, 10, 100);
        this.armor = new Armor("Armor", RarityType.Common, null, 5);
    }

    @Override
    public void draw(Graphics2D graph2D, Game game, GamePanel gamePanel) {

        int x = (int) ((GamePanel.WINDOW_WIDTH - GamePanel.NEW_TILE_SIZE) / 2),
                y = (int) ((GamePanel.WINDOW_HEIGHT - GamePanel.NEW_TILE_SIZE) / 2);

        Rectangle2D rec = new Rectangle((GamePanel.WINDOW_WIDTH - GamePanel.NEW_TILE_SIZE) / 2, (GamePanel.WINDOW_HEIGHT - GamePanel.NEW_TILE_SIZE) / 2, GamePanel.NEW_TILE_SIZE, GamePanel.NEW_TILE_SIZE);
        graph2D.setPaint(Color.CYAN);
        graph2D.draw(rec);

        if (this.getCurrentHealthPoints() < this.getMaxHealthPoints()) {
            Shape healthBarOutside = new Rectangle2D.Double(
                    (int) (GamePanel.WINDOW_WIDTH - GamePanel.NEW_TILE_SIZE)/ 2 - 1,
                    (int) (GamePanel.WINDOW_HEIGHT - GamePanel.NEW_TILE_SIZE)/ 2 - 11,
                    (GamePanel.NEW_TILE_SIZE + 2),
                    3);


            graph2D.setPaint(Color.black);
            graph2D.draw(healthBarOutside);

            Shape healthBarInside = new Rectangle2D.Double(
                    (int) (GamePanel.WINDOW_WIDTH - GamePanel.NEW_TILE_SIZE)/ 2,
                    (int) (GamePanel.WINDOW_HEIGHT - GamePanel.NEW_TILE_SIZE)/ 2 - 10,
                    ((double) GamePanel.NEW_TILE_SIZE / (double) this.getMaxHealthPoints() * (double) this.getCurrentHealthPoints()),
                    3);


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
