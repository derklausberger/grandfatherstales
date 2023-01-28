package objectClasses;

import utilityClasses.AudioManager;
import GUI.GamePanel;
import GUI.InventoryPanel;
import main.Main;
import objectClasses.Enum.EntityType;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;
import utilityClasses.InputHandler;
import utilityClasses.ResourceLoader;

import javax.imageio.ImageIO;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.geom.Arc2D;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.*;
import java.util.List;

public class Game {
    private Player player;
    private List<Level> levels;
    private int currentLevelNumber;
    private static List<TileSet> tileSets;

    private Boolean[][] mapWalkedOn;

    // Arrows for every direction, ordered alphabetically
    // 0 is Down, 1 is Left, 2 is Right, 3 is Up
    private BufferedImage[] arrows = new BufferedImage[4];

    // 12 items, but 6 torch-images: 0+6 is one torch (0 is flame, 6 is
    private BufferedImage[] torches;
    private int torchFrame;

    private BufferedImage[] chests;
    private Map<Integer, Integer> chestsToOpen;

    public Game() throws IOException, ParserConfigurationException, SAXException {
        loadTileSetsFromFiles();
        loadLevelsFromFile();
        currentLevelNumber = 1;

        int x = (int) ((getCurrentLevel().getEnterPos() % 32 + 0.5) * GamePanel.NEW_TILE_SIZE);
        int y = (getCurrentLevel().getEnterPos() / 32 + 1) * GamePanel.NEW_TILE_SIZE;
        this.player = new Player(x, y, 3, 100, 3, EntityType.character, 0);

        loadTorchImages();
        loadChestImages();
        loadProjectileImages();
        chestsToOpen = new HashMap<>();


        mapWalkedOn = new Boolean[32][32];
        for (int j = 0; j < 32 * 32; j++) {
            mapWalkedOn[j / 32][j % 32] = false;
        }
    }

    public void reloadLevel() {
        try {
            Level level = new Level(getCurrentLevel().getMapXMLFile());
            level.setId(currentLevelNumber);
            levels.set(currentLevelNumber - 1, level);
        } catch (Exception e) {
            System.out.println(e);
        }

        displayLevel();
        player.setCurrentHealthPoints(player.getMaxHealthPoints());
        player.setLife(player.getLife() - 1);
        player.getKeyHandler().clearVariables();
        InventoryPanel.loadInventory();
        GamePanel.isDead = false;
    }

    public void displayLevel() {
        int x = (int) ((getCurrentLevel().getEnterPos() % 32 + 0.5) * GamePanel.NEW_TILE_SIZE);
        int y = (getCurrentLevel().getEnterPos() / 32 + 1) * GamePanel.NEW_TILE_SIZE;
        this.player.setPositionX(x);
        this.player.setPositionY(y);

        chestsToOpen = new HashMap<>();

    }

    public boolean loadNextLevel() {
        player.getKeyHandler().clearVariables();
        if (currentLevelNumber >= levels.size()) {
            return false;
        }

        currentLevelNumber++;
        return true;
    }

    private void loadProjectileImages() {
        ResourceLoader rl = ResourceLoader.getResourceLoader();

        arrows[0] = rl.getBufferedImage("/entities/projectile/arrowDown.png");
        arrows[1] = rl.getBufferedImage("/entities/projectile/arrowLeft.png");
        arrows[2] = rl.getBufferedImage("/entities/projectile/arrowRight.png");
        arrows[3] = rl.getBufferedImage("/entities/projectile/arrowUp.png");
    }

    public BufferedImage getProjectileImage(int direction) {

        return arrows[direction / 9];
    }

    private void loadTorchImages() {

        ResourceLoader rl = ResourceLoader.getResourceLoader();
        // Create a File object for the directory
        File dir = rl.getFile("/entities/torch");

        torches = new BufferedImage[7];
        BufferedImage img;
        for (int i = 0; i < dir.listFiles().length; i++) {
            img = rl.getBufferedImage("/entities/torch/" + dir.list()[i]);
            torches[i] = img.getSubimage(0, 0, img.getWidth(), img.getHeight() / 2);
            torches[6] = img.getSubimage(0, img.getHeight() / 2, img.getWidth(), img.getHeight() / 2);
        }

        torchFrame = 0;
    }

    private void loadChestImages() {
        ResourceLoader rl = ResourceLoader.getResourceLoader();
        // Create a File object for the directory
        File dir = rl.getFile("/entities/chest");

        chests = new BufferedImage[3];
        for (int i = 0; i < dir.listFiles().length; i++) {
            chests[i] = rl.getBufferedImage("/entities/chest/" + dir.list()[i]);
        }
    }

    public Player getPlayer() {
        return player;
    }

    private Level getLevelById(int id) {
        for (Level level : levels) {
            if (level.getId() == id) {
                return level;
            }
        }

        return null;
    }

    public Level getCurrentLevel() {
        return getLevelById(currentLevelNumber);
    }

    public static TileSet getTilesetFromXMLFileName(String xmlFileName) {
        for (TileSet t : tileSets) {
            if (t.getXmlFileName().equals(xmlFileName)) {
                return t;
            }
        }
        return null;
    }

    // static eigentlich
    public void loadTileSetsFromFiles() throws IOException, SAXException, ParserConfigurationException {
        tileSets = new ArrayList<>();

        ResourceLoader rl = ResourceLoader.getResourceLoader();
        File dir = rl.getFile("/map/tiled");

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        for (File file : dir.listFiles()) {
            if (!file.isDirectory() && file.getAbsolutePath().endsWith(".xml")) {
                Document doc = builder.parse(file);

                String pngFileName = doc.getElementsByTagName("image").item(0).getAttributes().getNamedItem("source").getTextContent();
                String xmlFileName = file.getPath();

                int widthPixel = Integer.valueOf(doc.getElementsByTagName("image").item(0).getAttributes().getNamedItem("width").getTextContent());
                int heightPixel = Integer.valueOf(doc.getElementsByTagName("image").item(0).getAttributes().getNamedItem("height").getTextContent());

                int widthTiles = widthPixel / Integer.valueOf(doc.getElementsByTagName("tileset").item(0).getAttributes().getNamedItem("tilewidth").getTextContent());
                int heightTiles = heightPixel / Integer.valueOf(doc.getElementsByTagName("tileset").item(0).getAttributes().getNamedItem("tileheight").getTextContent());
                tileSets.add(new TileSet(pngFileName, xmlFileName, widthPixel, heightPixel, widthTiles, heightTiles));
            }
        }
    }

    public void renderSolid(Graphics2D g) {
        Level level = getCurrentLevel();
        BufferedImage[][][] map = level.getMap();
        for (int i = 0; i < map.length; i++) {
            if (i != level.getTrees() && i != level.getTorches() && i != level.getChests()) {
                for (int j = 0; j < 32 * 32; j++) {
                    g.drawImage(map[i][j / 32][j % 32],
                            GamePanel.NEW_TILE_SIZE * (j % 32) - player.getPositionX() + (GamePanel.WINDOW_WIDTH / 2),
                            GamePanel.NEW_TILE_SIZE * (j / 32) - player.getPositionY() + (GamePanel.WINDOW_HEIGHT / 2),
                            GamePanel.NEW_TILE_SIZE, GamePanel.NEW_TILE_SIZE,
                            null);


                    /*
                    if (level.isSolid(
                            GamePanel.NEW_TILE_SIZE * (j % 32),
                            GamePanel.NEW_TILE_SIZE * (j / 32))) {
                        if (level.isSolid(
                                player.getPositionX() + 15,
                                player.getPositionY() + 25)
                        ) {
                            Shape rec;

                            System.out.println(mapWalkedOn[(j % 32)][(j / 32)]);

                            if (!mapWalkedOn[(j % 32)][(j / 32)]) {
                                mapWalkedOn[(j % 32)][(j / 32)] = true;

                                System.out.println("hell");
                                rec = new Rectangle(
                                        GamePanel.NEW_TILE_SIZE * (j % 32) - player.getPositionX() + (GamePanel.WINDOW_WIDTH / 2),
                                        GamePanel.NEW_TILE_SIZE * (j / 32) - player.getPositionY() + (GamePanel.WINDOW_HEIGHT / 2),
                                        GamePanel.NEW_TILE_SIZE,
                                        GamePanel.NEW_TILE_SIZE);
                                g.setColor(new Color(0.4f, 0.4f, 0.4f, 0.1f));
                                g.fill(rec);

                            } else {

                                System.out.println("dunkel");
                                rec = new Rectangle(
                                        GamePanel.NEW_TILE_SIZE * (j % 32) - player.getPositionX() + (GamePanel.WINDOW_WIDTH / 2),
                                        GamePanel.NEW_TILE_SIZE * (j / 32) - player.getPositionY() + (GamePanel.WINDOW_HEIGHT / 2),
                                        GamePanel.NEW_TILE_SIZE,
                                        GamePanel.NEW_TILE_SIZE);
                                g.setColor(new Color(0.4f, 0.4f, 0.4f, 0.3f));
                                g.fill(rec);
                            }
                        }
                    }
                     */
                }
            }
        }
    }

    public void openChest(int x, int y) {
        int chestX = x / GamePanel.NEW_TILE_SIZE;
        int chestY = y / GamePanel.NEW_TILE_SIZE;
        if (!chestsToOpen.containsKey(32 * chestY + chestX)) {
            chestsToOpen.put(32 * chestY + chestX, 30);
        }
    }

    public void renderChests(Graphics2D graph2D) {
        Level level = getCurrentLevel();
        BufferedImage[][][] map = level.getMap();
        for (int j = 0; j < 32 * 32; j++) {
            graph2D.drawImage(map[level.getChests()][j / 32][j % 32],
                    (GamePanel.NEW_TILE_SIZE * (j % 32)) - player.getPositionX() + (GamePanel.WINDOW_WIDTH / 2),
                    GamePanel.NEW_TILE_SIZE * (j / 32) - player.getPositionY() + (GamePanel.WINDOW_HEIGHT / 2),
                    GamePanel.NEW_TILE_SIZE, GamePanel.NEW_TILE_SIZE,
                    null);
        }

        for (Map.Entry<Integer, Integer> chest : chestsToOpen.entrySet()) {
            if (chest.getValue() > 0) {
                if (chest.getValue() == 1) {
                    Main.toggleRewardScreen();
                } else if (chest.getValue() == 10) {
                    getCurrentLevel().setChest(chest.getKey() % 32 * GamePanel.NEW_TILE_SIZE, chest.getKey() / 32 * GamePanel.NEW_TILE_SIZE, chests[2]);
                } else if (chest.getValue() == 20) {
                    getCurrentLevel().setChest(chest.getKey() % 32 * GamePanel.NEW_TILE_SIZE, chest.getKey() / 32 * GamePanel.NEW_TILE_SIZE, chests[1]);
                }
                chest.setValue(chest.getValue() - 1);
            }
        }
    }

    private void loadLevelsFromFile() {
        levels = new ArrayList<>();

        ResourceLoader rl = ResourceLoader.getResourceLoader();
        File dir = rl.getFile("/map/level");

        Level level;
        int i = 1;
        for (File file : dir.listFiles()) {
            if (!file.isDirectory()) {
                level = new Level(file);
                level.setId(i);
                levels.add(level);
                i++;
            }
        }

    }

    public void renderTrees(Graphics2D graph2D) {
        Level level = getCurrentLevel();
        BufferedImage[][][] map = level.getMap();
        for (int j = 0; j < 32 * 32; j++) {
            graph2D.drawImage(map[level.getTrees()][j / 32][j % 32],
                    (GamePanel.NEW_TILE_SIZE * (j % 32)) - player.getPositionX() + (GamePanel.WINDOW_WIDTH / 2),
                    GamePanel.NEW_TILE_SIZE * (j / 32) - player.getPositionY() + (GamePanel.WINDOW_HEIGHT / 2),
                    GamePanel.NEW_TILE_SIZE, GamePanel.NEW_TILE_SIZE,
                    null);
        }
    }

    public void renderTorchFlames(Graphics2D graph2D) {
        Level level = getCurrentLevel();
        BufferedImage[][][] map = level.getMap();
        for (int j = 0; j < 32 * 32; j++) {
            if (map[level.getTorches()][j / 32][j % 32] != null) {

                graph2D.drawImage(torches[torchFrame / 10],
                        (int) (GamePanel.NEW_TILE_SIZE * ((j % 32) + 0.2)) - player.getPositionX() + (GamePanel.WINDOW_WIDTH / 2),
                        (int) (GamePanel.NEW_TILE_SIZE * ((j / 32) - 0.75)) - player.getPositionY() + (GamePanel.WINDOW_HEIGHT / 2),
                        (int) (GamePanel.NEW_TILE_SIZE * 0.6),
                        (int) (GamePanel.NEW_TILE_SIZE * 0.75),
                        null);
            }
        }
        if (torchFrame < 58) {
            torchFrame += 1;
        } else {
            torchFrame = 0;
        }
    }

    public void renderTorchStems(Graphics2D graph2D) {
        Level level = getCurrentLevel();
        BufferedImage[][][] map = level.getMap();
        for (int j = 0; j < 32 * 32; j++) {
            if (map[level.getTorches()][j / 32][j % 32] != null) {
                graph2D.drawImage(torches[6],
                        (int) (GamePanel.NEW_TILE_SIZE * ((j % 32) + 0.2)) - player.getPositionX() + (GamePanel.WINDOW_WIDTH / 2),
                        GamePanel.NEW_TILE_SIZE * ((j / 32)) - player.getPositionY() + (GamePanel.WINDOW_HEIGHT / 2),
                        (int) (GamePanel.NEW_TILE_SIZE * 0.6),
                        (int) (GamePanel.NEW_TILE_SIZE * 0.75),
                        null);
            }
        }
    }

    public boolean enemyAttack(double angle, int viewDirection) {

        boolean hit = false;
        if (viewDirection == 18 && angle >= 315 && angle < 359 || angle >= 0 && angle < 45) { // -> von 315 bis 45 -> Up
            hit = true;
        } else if (viewDirection == 12 && angle >= 45 && angle < 135) { // -> von 45 bis 135 -> Right
            hit = true;
        } else if (viewDirection == 0 && angle >= 135 && angle < 225) { // -> von 135 bis 225 -> Down
            hit = true;
        } else if (viewDirection == 6 && angle >= 225 && angle < 315) { // -> von 225 bis 315 -> Left
            hit = true;
        }
        return hit;
    }

    public void dealDamage(Enemy enemy) {
        if (!player.isInvincible()) {
            if (player.getBlockAmount() >= enemy.getAttackDamage()) { // Block sound
            } else {
                AudioManager.play("S - characterHit");
                player.setCurrentHealthPoints(player.getCurrentHealthPoints() + player.getBlockAmount() - enemy.getAttackDamage());
                InventoryPanel.loadInventory();
            }
            if (player.getCurrentHealthPoints() <= 0) {
                GamePanel.isDead = true;
            }
        } else {
            player.triggerInvincibility();
        }
    }

    public void moveProjectiles(Enemy enemy) {
        Projectile p;
        for (int i = enemy.getProjectiles().size() - 1; i >= 0; i--) {
            p = enemy.getProjectiles().get(i);
            if (p.outOfScreen()) {
                enemy.getProjectiles().remove(i);
            } else {
                p.move();

                int arrowOffset = 5;
                int playerXLeft = player.getPositionX(), playerXRight = player.getPositionX() + 30;
                int playerYUp = player.getPositionY(), playerYDown = player.getPositionY() + 50;
                switch (p.getDirection()) {
                    case 9, 18 -> {
                        if (player.getKeyHandler().walkingDirection == InputHandler.upKey
                                || player.getKeyHandler().walkingDirection == InputHandler.downKey) {
                            playerXLeft -= 25;
                            playerXRight -= 22;

                        } else {
                            playerXLeft -= 35;
                            playerXRight -= 22;
                        }
                        playerYUp -= 25;
                        playerYDown -= 25;
                    }
                    case 0, 27 -> {
                        if (player.getKeyHandler().walkingDirection == InputHandler.upKey
                                || player.getKeyHandler().walkingDirection == InputHandler.downKey) {
                            playerXLeft -= 15;
                            playerXRight -= 12;
                        } else {
                            playerXLeft -= 15;
                            playerXRight -= 18;
                        }
                        playerYUp -= 40;
                        playerYDown -= 25;
                    }
                }

                if (p.getX() < playerXRight && p.getX() + arrowOffset > playerXLeft
                        && p.getY() < playerYDown && p.getY() + arrowOffset > playerYUp) {

                    dealDamage(enemy);
                    enemy.getProjectiles().remove(i);
                }
            }
        }
    }

    public void checkPlayerAttack(int attackFrame) {
        int startAngle = 0, arcAngle = 0;

        //System.out.println(attackFrame);
        if (player.getKeyHandler().attackPressed
                && player.getCooldown() == 0) {     //&& attackFrame == 4
            switch (player.getKeyHandler().lastDirection) {
                case (KeyEvent.VK_W) -> {
                    startAngle = 45;
                    arcAngle = 90;
                }
                case (KeyEvent.VK_A) -> {
                    startAngle = 135;
                    arcAngle = 90;
                }
                case (KeyEvent.VK_S) -> {
                    startAngle = 225;
                    arcAngle = 90;
                }
                case (KeyEvent.VK_D) -> {
                    startAngle = 315;
                    arcAngle = 90;
                }
            }


            //getEntityFrames("attacking")[getCurrentFrame()].getXOffset()

            player.setCooldown(player.getAttackDelay());

/*
            Point2D point2D2 = new Point2D.Double(getPlayer().getPositionX() + GamePanel.NEW_TILE_SIZE / 2, getPlayer().getPositionY() + GamePanel.NEW_TILE_SIZE / 2);
*/

            Point2D playerMiddle = new Point2D.Double(
                    player.getPositionX() + player.getEntityFrames(player.getCurrentAnimationType())[player.getCurrentFrame()].getXOffset(),
                    player.getPositionY() + player.getEntityFrames(player.getCurrentAnimationType())[player.getCurrentFrame()].getYOffset());

/*
            Arc2D arc2D = new Arc2D.Double();
            arc2D.setArcByCenter(
                    point2D.getX(),
                    point2D.getY(),
                    player.getWeapon().getAttackRange(),
                    startAngle, arcAngle,
                    Arc2D.PIE);
*/
            ArrayList<Enemy> alreadyHit = new ArrayList<>();

            Point2D enemyMiddle = new Point2D.Double();
            for (Enemy enemy : getCurrentLevel().getEnemies()) {
                enemyMiddle.setLocation(
                        enemy.getPositionX() + enemy.getEntityFrames(enemy.getCurrentAnimationType())[enemy.getCurrentFrame()].getXOffset(),
                        enemy.getPositionY() + enemy.getEntityFrames(enemy.getCurrentAnimationType())[enemy.getCurrentFrame()].getYOffset());

                double angle;
                double theta = Math.atan2(playerMiddle.getY() - enemyMiddle.getY(), playerMiddle.getX() - enemyMiddle.getX());
                theta += Math.PI / 2.0;
                angle = Math.toDegrees(theta);

                if (angle < 0) {
                    angle += 360;
                }


                if (playerMiddle.distance(enemyMiddle) <= player.getWeapon().getAttackRange()) {
                    if (player.getViewDirection() == 27) {
                        if (angle >= 315 && angle < 359 || angle >= 0 && angle < 45) { // -> von 315 bis 45 -> Up
                            if (!alreadyHit.contains(enemy)) {
                                alreadyHit.add(enemy);
                                if (enemy.getBlockAmount() >= player.getAttackDamage()) {
                                    // Implement block sound effect
                                } else {
                                    AudioManager.play("S - skeletonHit");
                                    enemy.setCurrentHealthPoints(enemy.getCurrentHealthPoints() + enemy.getBlockAmount() - player.getAttackDamage());

                                    if (!enemy.isKnockBack()) {
                                        enemy.hit();
                                    }
                                }
                                if (enemy.getCurrentHealthPoints() <= 0) {
                                    getCurrentLevel().getEnemies().remove(enemy);
                                    break;
                                }
                            }
                        }
                    } else if (player.getViewDirection() == 18) {
                        if (angle >= 45 && angle < 135) { // -> von 45 bis 135 -> Right
                            if (!alreadyHit.contains(enemy)) {

                                alreadyHit.add(enemy);

                                if (enemy.getBlockAmount() >= player.getAttackDamage()) {

                                    // Implement block sound effect
                                } else {
                                    AudioManager.play("S - skeletonHit");
                                    enemy.setCurrentHealthPoints(enemy.getCurrentHealthPoints() + enemy.getBlockAmount() - player.getAttackDamage());

                                    if (!enemy.isKnockBack()) {
                                        enemy.hit();
                                    }

                                }
                                if (enemy.getCurrentHealthPoints() <= 0) {
                                    getCurrentLevel().getEnemies().remove(enemy);
                                    break;
                                }
                            }
                        }
                    } else if (player.getViewDirection() == 0) {
                        if (angle >= 135 && angle < 225) { // -> von 135 bis 225 -> Down
                            if (!alreadyHit.contains(enemy)) {

                                alreadyHit.add(enemy);

                                if (enemy.getBlockAmount() >= player.getAttackDamage()) {

                                    // Implement block sound effect
                                } else {
                                    AudioManager.play("S - skeletonHit");
                                    enemy.setCurrentHealthPoints(enemy.getCurrentHealthPoints() + enemy.getBlockAmount() - player.getAttackDamage());

                                    if (!enemy.isKnockBack()) {
                                        enemy.hit();
                                    }

                                }
                                if (enemy.getCurrentHealthPoints() <= 0) {
                                    getCurrentLevel().getEnemies().remove(enemy);
                                    break;
                                }
                            }
                        }
                    } else if (player.getViewDirection() == 9) {
                        if (angle >= 225 && angle < 315) { // -> von 225 bis 315 -> Left
                            if (!alreadyHit.contains(enemy)) {

                                alreadyHit.add(enemy);

                                if (enemy.getBlockAmount() >= player.getAttackDamage()) {

                                    // Implement block sound effect
                                } else {
                                    AudioManager.play("S - skeletonHit");
                                    enemy.setCurrentHealthPoints(enemy.getCurrentHealthPoints() + enemy.getBlockAmount() - player.getAttackDamage());

                                    if (!enemy.isKnockBack()) {
                                        enemy.hit();
                                    }

                                }
                                if (enemy.getCurrentHealthPoints() <= 0) {
                                    getCurrentLevel().getEnemies().remove(enemy);
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
