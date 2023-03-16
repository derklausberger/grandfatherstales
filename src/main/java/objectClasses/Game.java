package objectClasses;

import utilityClasses.AudioManager;
import GUI.GamePanel;
import GUI.InventoryPanel;
import main.Main;
import objectClasses.Enum.EntityType;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;
import utilityClasses.ResourceLoader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;

public class Game {
    private Player player;
    private List<Level> levels;
    private int currentLevelNumber;
    private static List<TileSet> tileSets;

    private Boolean[][] mapWalkedOn;

    private boolean loadingLevel, openingChest, gameWon;

    // Arrows for every direction, ordered alphabetically
    // 0 is Down, 1 is Left, 2 is Right, 3 is Up
    private BufferedImage[] arrows = new BufferedImage[4];

    private BufferedImage[] lifeImages = new BufferedImage[3];

    private BufferedImage[] torches;
    private int torchFrame;

    private BufferedImage[] chests;
    private Map<Integer, Integer> chestsToOpen;

    public Game() {

        init();
    }

    private void init() {

        currentLevelNumber = 1;

        try {
            loadTileSetsFromFiles();
        } catch (Exception e) {
            System.out.println(e);
        }
        loadLevelsFromFile();

        loadTorchImages();
        loadChestImages();
        loadLifeImages();
        loadProjectileImages();
        chestsToOpen = new HashMap<>();


        mapWalkedOn = new Boolean[32][32];
        for (int j = 0; j < 32 * 32; j++) {
            mapWalkedOn[j / 32][j % 32] = false;
        }

        int x = (int) ((getCurrentLevel().getEnterPos() % 32 + 0.5) * GamePanel.NEW_TILE_SIZE);
        int y = (getCurrentLevel().getEnterPos() / 32 + 1) * GamePanel.NEW_TILE_SIZE;
        player = new Player(x, y, 3, 100, 3, EntityType.character, 1);

    }

    private void resetVariables() {

        player.setInvincible(false);
        player.setCurrentHealthPoints(player.getMaxHealthPoints());
        player.getKeyHandler().initVariables();
        InventoryPanel.loadInventory(player);
    }

    public void reloadLevel() {

        loadingLevel = true;

        try {
            Level level = new Level(getCurrentLevel().getMapXMLFile());
            level.setId(currentLevelNumber);
            levels.set(currentLevelNumber - 1, level);
        } catch (Exception e) {
            System.out.println(e);
        }

        player.setCurrentAnimationType("resting");
        resetVariables();
    }

    public void displayLevel() {
        int x = (int) ((getCurrentLevel().getEnterPos() % 32 + 0.5) * GamePanel.NEW_TILE_SIZE);
        int y = (getCurrentLevel().getEnterPos() / 32 + 1) * GamePanel.NEW_TILE_SIZE;
        this.player.setPositionX(x);
        this.player.setPositionY(y);

        chestsToOpen = new HashMap<>();
        loadingLevel = false;
    }

    public boolean loadNextLevel() {

        if (currentLevelNumber >= levels.size()) {
            return false;
        }
        currentLevelNumber++;
        return true;
    }

    private void loadProjectileImages() {
        ResourceLoader rl = ResourceLoader.getResourceLoader();

        arrows[0] = rl.getBufferedImage("/entities/projectile/arrowUp.png");
        arrows[1] = rl.getBufferedImage("/entities/projectile/arrowDown.png");
        arrows[2] = rl.getBufferedImage("/entities/projectile/arrowLeft.png");
        arrows[3] = rl.getBufferedImage("/entities/projectile/arrowRight.png");
    }

    public BufferedImage getProjectileImage(int direction) {

        return arrows[direction];
    }

    private void loadTorchImages() {

        ResourceLoader rl = ResourceLoader.getResourceLoader();
        // Create a File object for the directory
        File dir = rl.getFile("/entities/torch");

        torches = new BufferedImage[7];
        BufferedImage img = null;
        for (int i = 0; i < dir.listFiles().length; i++) {
            img = rl.getBufferedImage("/entities/torch/" + dir.list()[i]);
            torches[i] = img.getSubimage(0, 0, img.getWidth(), img.getHeight() / 2);
        }
        assert img != null;
        torches[6] = img.getSubimage(0, img.getHeight() / 2, img.getWidth(), img.getHeight() / 2);
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

    private void loadLifeImages() {

        ResourceLoader rl = ResourceLoader.getResourceLoader();
        lifeImages[0] = rl.getBufferedImage("/screen/inventoryPanel/oneLife.png");
        lifeImages[1] = rl.getBufferedImage("/screen/inventoryPanel/twoLives.png");
        lifeImages[2] = rl.getBufferedImage("/screen/inventoryPanel/threeLives.png");

    }

    public BufferedImage getLifeImage() {

        return switch (player.getLife()) {
            case 3 -> lifeImages[2];
            case 2 -> lifeImages[1];
            default -> lifeImages[0];
        };
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

    public void renderLives(Graphics2D graph2D) {

        graph2D.drawImage(getLifeImage(),
                46,
                34,
                100,
                18,
                null);
    }

    public void renderCharacterHealthBar(Graphics2D graph2D) {

        double healthBarWidth = (double) 262 / (double) player.getMaxHealthPoints() * (double) player.getCurrentHealthPoints();
        Shape healthBar = new Rectangle2D.Double(
                24,
                64,
                healthBarWidth,
                14);

        graph2D.setPaint(new Color(0x7d0027));
        graph2D.fill(healthBar);

        if (player.getCurrentHealthPoints() < player.getMaxHealthPoints()) {
            Shape healthBarBackground = new Rectangle2D.Double(
                    24 + healthBarWidth,
                    64,
                    262 - healthBarWidth,
                    14);

            graph2D.setPaint(new Color(0xA6505050, true));
            graph2D.fill(healthBarBackground);
        }
    }

    public boolean enemyAttack(double angle, int viewDirection) {

        boolean hit = false;
        if (viewDirection == 0 && angle >= 315 && angle < 359 || angle >= 0 && angle < 45) { // -> von 315 bis 45 -> Up
            hit = true;
        } else if (viewDirection == 3 && angle >= 45 && angle < 135) { // -> von 45 bis 135 -> Right
            hit = true;
        } else if (viewDirection == 1 && angle >= 135 && angle < 225) { // -> von 135 bis 225 -> Down
            hit = true;
        } else if (viewDirection == 2 && angle >= 225 && angle < 315) { // -> von 225 bis 315 -> Left
            hit = true;
        }
        return hit;
    }

    public void dealDamageToPlayer(Enemy enemy) {

        if (!player.isInvincible()) {
            if (player.getBlockAmount() >= enemy.getAttackDamage()) {
                // Block sound
            } else {
                AudioManager.play("S - characterHit");
                player.setCurrentHealthPoints(player.getCurrentHealthPoints() + player.getBlockAmount() - enemy.getAttackDamage());
                InventoryPanel.loadInventory(player);

                if (player.isDead()) player.setInvincible(true);
                else player.triggerInvincibility();
            }
        }
    }

    public void setOpeningChest(boolean opening) {

        openingChest = opening;
    }

    public boolean isOpeningChest() {

        return openingChest;
    }

    public boolean isLoadingLevel() {

        if (player.isDead()) return true;
        return loadingLevel;
    }

    public boolean isGameWon() {

        return gameWon;
    }

    public boolean isGameOver() {

        return player.getLife() == 0;
    }

    private void playSwordSwipeSound() {

        // Creates an array of the sound names
        String[] sounds = {"S - swordSwipe1", "S - swordSwipe2", "S - swordSwipe3"};
        // Generates a random index
        int index = (int) (Math.random() * sounds.length);
        AudioManager.play(sounds[index]);
    }

    public void animateCharacterAttacking() {

        // Plays the sword sound only once
        // at the start of the animation
        if (!player.getCurrentAnimationType().equals("attacking")) {
            AudioManager.stop("S - characterWalking");
            playSwordSwipeSound();
        }
        player.setCurrentAnimationType("attacking");

        player.adaptViewDirection(player.getAttackDirection());
        player.setCurrentAnimationFrame(player.getAttackAnimationFrame());

        player.startAnimationTimer();
    }

    public void animateCharacterWalking() {

        AudioManager.loop("S - characterWalking");
        player.setCurrentAnimationType("walking");

        player.adaptViewDirection(player.getWalkingDirection());
        player.setCurrentAnimationFrame(player.getWalkingAnimationFrame());
        moveCharacter(player.getWalkingDirection());

        player.startAnimationTimer();
    }

    public void animateCharacterResting() {

        AudioManager.stop("S - characterWalking");
        player.setCurrentAnimationType("resting");

        player.adaptViewDirection(player.getLastDirection());
        player.setCurrentAnimationFrame(player.getRestingAnimationFrame());

        player.startAnimationTimer();

        // Prevents the character to "slide" when moving, if a direction key
        // is spammed really fast
        //if (lastDirection == InputHandler.upKey
        //        || lastDirection == InputHandler.downKey) {
        //    player.setWalkingAnimationFrame(2);
        //} else player.setWalkingAnimationFrame(1);
    }

    public void animateCharacterDying() {

        player.setCurrentAnimationType("dying");
        player.adaptViewDirection(1);
        player.setCurrentAnimationFrame(player.getDyingAnimationFrame());

        player.startAnimationTimer();
    }

    private void moveCharacter(int direction) {

        int movementSpeed = player.getMovementSpeed();
        int playerX = player.getPositionX();
        int playerY = player.getPositionY();
        int xOffset = Math.floorDiv(30 * 4, 10);
        int player_height = 50;

        switch (direction) {
            case 0 -> {
                if (getCurrentLevel().isSolid(playerX + xOffset, playerY - movementSpeed + Math.floorDiv(player_height * 2, 10)) &&
                        getCurrentLevel().isSolid(playerX - xOffset, playerY - movementSpeed + Math.floorDiv(player_height * 2, 10))) {
                    player.setPositionY(player.getPositionY() - player.getMovementSpeed());
                }
            }
            case 1 -> {
                if (getCurrentLevel().isSolid(playerX - xOffset, playerY + movementSpeed + Math.floorDiv(player_height, 2)) &&
                        getCurrentLevel().isSolid(playerX + xOffset, playerY + movementSpeed + Math.floorDiv(player_height, 2))) {
                    player.setPositionY(player.getPositionY() + player.getMovementSpeed());
                }
            }
            case 2 -> {
                if (getCurrentLevel().isSolid(playerX - movementSpeed - xOffset, playerY + Math.floorDiv(player_height, 2)) &&
                        getCurrentLevel().isSolid(playerX - movementSpeed - xOffset, playerY + Math.floorDiv(player_height * 2, 10))) {
                    player.setPositionX(player.getPositionX() - player.getMovementSpeed());
                }
            }
            case 3 -> {
                if (getCurrentLevel().isSolid(playerX + movementSpeed + xOffset, playerY + Math.floorDiv(player_height, 2)) &&
                        getCurrentLevel().isSolid(playerX + movementSpeed + xOffset, playerY + Math.floorDiv(player_height * 2, 10))) {
                    player.setPositionX(player.getPositionX() + player.getMovementSpeed());
                }
            }
        }
    }

    public void characterInteract() {

        int playerX = player.getPositionX();
        int playerY = player.getPositionY();
        int yOffset = Math.floorDiv(50, 10);

        if (player.getLastDirection() == 0) {
            if (getCurrentLevel().isChest(playerX, playerY - yOffset)) {
                openingChest = true;
                openChest(playerX, playerY - yOffset);
                AudioManager.play("S - openingChest");

            } else if (getCurrentLevel().isExit(playerX, playerY - yOffset)) {
                loadingLevel = true;
                player.stopAnimationTimer();
                if (loadNextLevel()) {
                    player.getKeyHandler().initVariables();
                    GamePanel.loadNextLevel();
                } else {
                    gameWon = true;
                    GamePanel.showVictoryScreen();
                }
            }
        }
        player.getKeyHandler().interactPressed = false;
    }

    public void handleEnemyProjectiles() {

        for (Enemy enemy : getCurrentLevel().getEnemies()) {
            for (int i = enemy.getProjectiles().size() - 1; i >= 0; i--) {
                Projectile p = enemy.getProjectiles().get(i);

                // Removes projectile or deals damage depending
                // on the position of the projectile
                switch (moveEnemyProjectiles(p)) {
                    case 1:
                        dealDamageToPlayer(enemy);
                    case 0:
                        enemy.getProjectiles().remove(i);
                        break;
                }
            }
        }
    }

    private int moveEnemyProjectiles(Projectile p) {

        if (p.outOfScreen()) return 0;
        p.move();

        if (p.checkPlayerCollision(player.getPositionX(), player.getPositionY(), player.getKeyHandler().walkingDirection)) {
            return 1;
        }
        return -1;
    }

    public void checkPlayerAttack() {
        if (player.attack()) {

            for (Enemy enemy : getCurrentLevel().getEnemies()) {

                Point2D playerMiddle = new Point2D.Double(player.getPositionX() + 15, player.getPositionY() + 25);
                Point2D enemyMiddle = new Point2D.Double(enemy.getPositionX(), enemy.getPositionY());
                double angle = getAngle(enemyMiddle, playerMiddle);

                System.out.println("\n\n The angle: [" +angle+ "]");
                System.out.println("\n Distance is: [" +playerMiddle.distance(enemyMiddle)+ "]");
                System.out.println("\n Range is: [" +player.getWeapon().getAttackRange()+ "]");
                System.out.println("\n Boolean: [" + (playerMiddle.distance(enemyMiddle) <= player.getWeapon().getAttackRange())+ "]\n\n");

                if (playerMiddle.distance(enemyMiddle) <= player.getWeapon().getAttackRange()) {

                    if (isHit(angle)) {
                        if (dealDamageToEnemy(enemy)) {
                            getCurrentLevel().getEnemies().remove(enemy);
                        }
                    }
                }
            }
        }
    }

    private boolean isHit(double angle) {
        if (player.getAttackDirection() == 0) { // left
            return angle >= 315 && angle < 359 || angle >= 0 && angle < 45;
        } else if (player.getAttackDirection() == 1) { // up
            return angle >= 135 && angle < 225;
        } else if (player.getAttackDirection() == 2) { // right
            return angle >= 225 && angle < 315;
        } else if (player.getAttackDirection() == 3) {// down
            return angle >= 45 && angle < 135;
        }
        return false;
    }

    public double getAngle(Point2D playerMiddle, Point2D enemyMiddle) {

        double angle;
        double theta = Math.atan2(playerMiddle.getY() - enemyMiddle.getY(), playerMiddle.getX() - enemyMiddle.getX());
        theta += Math.PI / 2.0;
        angle = Math.toDegrees(theta);

        if (angle < 0) {
            angle += 360;
        }
        return angle;
    }

    private boolean dealDamageToEnemy(Enemy enemy) {

        if (enemy.getBlockAmount() >= player.getAttackDamage()) {
            // Implement block sound effect
        } else {
            AudioManager.play("S - skeletonHit");
            enemy.setCurrentHealthPoints(enemy.getCurrentHealthPoints() + enemy.getBlockAmount() - player.getAttackDamage());

            if (!enemy.isKnockedBack()) {
                enemy.startKnockBack();
            }
            return enemy.getCurrentHealthPoints() <= 0;
        }
        return false;
    }

    private int[] getAttackAngles() {

        int startAngle = 0, arcAngle = 0;

        if (player.getAttackDirection() == 0) {
            startAngle = 45;
            arcAngle = 90;
        } else if (player.getAttackDirection() == 1) {
            startAngle = 225;
            arcAngle = 90;
        } else if (player.getAttackDirection() == 2) {
            startAngle = 135;
            arcAngle = 90;
        } else if (player.getAttackDirection() == 3) {
            startAngle = 315;
            arcAngle = 90;
        }
        return new int[]{startAngle, arcAngle};
    }
}