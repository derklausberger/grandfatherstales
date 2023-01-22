package objectClasses;

import GUI.AudioManager;
import GUI.GamePanel;
import main.Main;
import objectClasses.Enum.EntityType;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.imageio.ImageIO;
import javax.swing.*;
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
import java.util.*;
import java.util.List;

public class Game {
    private Player player; //list???
    private List<Level> levels;
    private int currentLevelNumber;
    private static List<TileSet> tileSets;

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
        this.player = new Player(x, y, 3, 100, 3, EntityType.character);

        loadTorchImages();
        loadChestImages();
        loadProjectileImages();
        chestsToOpen = new HashMap<>();
    }

    public void reloadLevel() {
        int x = (int) ((getCurrentLevel().getEnterPos() % 32 + 0.5) * GamePanel.NEW_TILE_SIZE);
        int y = (getCurrentLevel().getEnterPos() / 32 + 1) * GamePanel.NEW_TILE_SIZE;
        this.player.setPositionX(x);
        this.player.setPositionY(y);

        chestsToOpen = new HashMap<>();
    }

    public boolean loadNextLevel() {
        if (currentLevelNumber >= levels.size()) {
            return false;
        }

        currentLevelNumber++;
        reloadLevel();
        return true;
    }

    private void loadProjectileImages() {

        try {
            arrows[0] = ImageIO.read(new File("src/main/resources/entities/projectile/arrowDown.png"));
            arrows[1] = ImageIO.read(new File("src/main/resources/entities/projectile/arrowLeft.png"));
            arrows[2] = ImageIO.read(new File("src/main/resources/entities/projectile/arrowRight.png"));
            arrows[3] = ImageIO.read(new File("src/main/resources/entities/projectile/arrowUp.png"));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public BufferedImage getProjectileImage(int direction) {

        return arrows[direction / 9];
    }

    private void loadTorchImages() throws ParserConfigurationException, IOException {
        torches = new BufferedImage[7];
        String tileSetFolderPath = "src/main/resources/entities/torch";
        File dir = new File(tileSetFolderPath);

        BufferedImage img;
        int idx = 0;
        for (File file : Arrays.stream(dir.listFiles()).sorted().toList()) {
            if (!file.isDirectory() && file.getAbsolutePath().endsWith(".png")) {
                img = ImageIO.read(file);
                torches[idx] = img.getSubimage(0, 0, img.getWidth(), img.getHeight() / 2);
                //torches[idx + 6] = img.getSubimage(0, img.getHeight() / 2, img.getWidth(), img.getHeight() / 2);
                torches[6] = img.getSubimage(0, img.getHeight() / 2, img.getWidth(), img.getHeight() / 2);
                idx ++;
            }
        }

        torchFrame = 0;
    }

    private void loadChestImages() throws ParserConfigurationException, IOException {
        chests = new BufferedImage[3];
        String tileSetFolderPath = "src/main/resources/entities/chest";
        File dir = new File(tileSetFolderPath);

        BufferedImage img;
        int idx = 0;
        for (File file : Arrays.stream(dir.listFiles()).sorted().toList()) {
            if (!file.isDirectory() && file.getAbsolutePath().endsWith(".png")) {
                chests[idx] = ImageIO.read(file);
                idx ++;
            }
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

    public static void loadTileSetsFromFiles() throws IOException, SAXException, ParserConfigurationException {
        tileSets = new ArrayList<>();
        String tileSetFolderPath = "arbitraryResources/tiled/";
        File dir = new File(tileSetFolderPath);

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        for (File file : dir.listFiles()) {
            if (!file.isDirectory() && file.getAbsolutePath().endsWith(".xml")) {
                Document doc = builder.parse(file);

                String pngFileName = doc.getElementsByTagName("image").item(0).getAttributes().getNamedItem("source").getTextContent().replace("../", "");
                String xmlFileName = tileSetFolderPath + file.getName();

                int widthPixel = Integer.valueOf(doc.getElementsByTagName("image").item(0).getAttributes().getNamedItem("width").getTextContent());
                int heightPixel = Integer.valueOf(doc.getElementsByTagName("image").item(0).getAttributes().getNamedItem("height").getTextContent());

                int widthTiles = widthPixel / Integer.valueOf(doc.getElementsByTagName("tileset").item(0).getAttributes().getNamedItem("tilewidth").getTextContent());
                int heightTiles = heightPixel / Integer.valueOf(doc.getElementsByTagName("tileset").item(0).getAttributes().getNamedItem("tileheight").getTextContent());
                tileSets.add(new TileSet(pngFileName, xmlFileName, widthPixel, heightPixel, widthTiles, heightTiles));
            }
        }
    }

    public void renderSolid(Graphics2D g) {//} throws IOException {
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
        try {
            File dir = new File("src/main/resources/map/level/");
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
        } catch (Exception e) {
            e.printStackTrace();
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
                        (int)(GamePanel.NEW_TILE_SIZE * ((j % 32) + 0.2)) - player.getPositionX() + (GamePanel.WINDOW_WIDTH / 2),
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
                        (int)(GamePanel.NEW_TILE_SIZE * ((j % 32) + 0.2)) - player.getPositionX() + (GamePanel.WINDOW_WIDTH / 2),
                        GamePanel.NEW_TILE_SIZE * ((j / 32)) - player.getPositionY() + (GamePanel.WINDOW_HEIGHT / 2),
                        (int) (GamePanel.NEW_TILE_SIZE * 0.6),
                        (int) (GamePanel.NEW_TILE_SIZE * 0.75),
                        null);
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

            player.setCooldown(player.getAttackDelay());

            Point2D point2D = new Point2D.Double(getPlayer().getPositionX() + GamePanel.NEW_TILE_SIZE / 2, getPlayer().getPositionY() + GamePanel.NEW_TILE_SIZE / 2);

            Arc2D arc2D = new Arc2D.Double();
            arc2D.setArcByCenter(
                    point2D.getX(),
                    point2D.getY(),
                    player.getWeapon().getAttackRange(),
                    startAngle, arcAngle,
                    Arc2D.PIE);

            ArrayList<Enemy> alreadyHit = new ArrayList<>();
            for (Enemy enemy : getCurrentLevel().getEnemies()) {
                if (arc2D.contains(enemy.getPositionX(), enemy.getPositionY()) ||
                        arc2D.contains(enemy.getPositionX() + GamePanel.NEW_TILE_SIZE, enemy.getPositionY()) ||
                        arc2D.contains(enemy.getPositionX(), enemy.getPositionY() + GamePanel.NEW_TILE_SIZE) ||
                        arc2D.contains(enemy.getPositionX() + GamePanel.NEW_TILE_SIZE, enemy.getPositionY() + GamePanel.NEW_TILE_SIZE)) {
                    if (!alreadyHit.contains(enemy)) {

                        alreadyHit.add(enemy);

                        if (enemy.getBlockAmount() >= player.getAttackDamage()) {

                            // Implement block sound effect
                        } else {
                            AudioManager.play("S - skeletonHit");
                            enemy.setCurrentHealthPoints(enemy.getCurrentHealthPoints() + enemy.getBlockAmount() - player.getAttackDamage());

                            if(!enemy.isKnockBack()) {
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
