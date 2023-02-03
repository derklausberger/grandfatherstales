package objectClasses;

import GUI.GamePanel;
import objectClasses.Enum.EntityType;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import utilityClasses.ResourceLoader;

import javax.imageio.ImageIO;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Map;
import java.util.Random;

public class Level {
    private int id;
    private BufferedImage[][][] map;//layers height width
    private Hashtable<Integer, TileSet> tileSets;
    private ArrayList<Enemy> enemies;
    private File mapXMLFile;
    private int obstacles = -1, chests = -1, solid = -1, trees = -1, enterPos = -1, torches = -1, exit = -1;

    public Level(File mapXMLFile) {
        this.mapXMLFile = mapXMLFile;
        int map_width = 32;
        int map_height = 32;
        enemies = null;

        tileSets = new Hashtable<>();
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder;
        Document doc;
        try {
            builder = factory.newDocumentBuilder();
            doc = builder.parse(mapXMLFile);
        } catch (ParserConfigurationException | IOException | SAXException e) {
            throw new RuntimeException(e);
        }

        NodeList nodeList = doc.getElementsByTagName("tileset");
        Node n;
        Element e;
        for (int i = 0; i < nodeList.getLength(); i++) {
            n = nodeList.item(i);
            e = (Element) n;

            tileSets.put(
                    Integer.valueOf(e.getAttribute("firstgid")),
                    Game.getTilesetFromXMLFileName(
                            getClass().getResource(e.getAttribute("source")).toString()
                                    .replace("file:/", "")
                                    .replace('/', (char) 92)));

        }

        nodeList = doc.getElementsByTagName("layer");

        map = new BufferedImage[nodeList.getLength()][map_height][map_width];
        for (int i = 0; i < nodeList.getLength(); i++) {
            n = nodeList.item(i);
            e = (Element) n;

            switch (e.getAttribute("name")) {
                case "chests" -> chests = i;
                case "obstacles" -> obstacles = i;
                case "trees" -> trees = i;
                case "solid" -> solid = i;
                case "torches" -> torches = i;
                case "exit" -> exit = i;
            }

            int j = 0, field, tileSetKey, fieldX, fieldY;
            TileSet tileSet;
            for (String s : e.getElementsByTagName("data").item(0).getTextContent().replace("\n", "").replace("\r", "").split(",")) {
                field = Integer.parseInt(s);
                tileSetKey = 0;
                for (Map.Entry<Integer, TileSet> entry : tileSets.entrySet()) {
                    if (tileSetKey < entry.getKey() && entry.getKey() <= field) {
                        tileSetKey = entry.getKey();
                    }
                }
                tileSet = tileSets.get(tileSetKey);

                ResourceLoader rl = ResourceLoader.getResourceLoader();

                if (tileSet != null) {
                    field -= tileSetKey;
                    fieldX = (field % tileSet.getWidthTiles()) * 16;
                    fieldY = (field / tileSet.getWidthTiles()) * 16;
                    map[i][j / map_height][j % map_width] =
                            rl.getBufferedImage(tileSet.getPngFileName())
                                    .getSubimage(fieldX, fieldY, 16, 16);
                    if (e.getAttribute("name").equals("enter")) {
                        enterPos = j;
                    }
                } else {
                    map[i][(int) (j / map_height)][j % map_width] = null;
                }

                j++;
            }
        }
    }

    public File getMapXMLFile() {
        return mapXMLFile;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public BufferedImage[][][] getMap() {
        return map;
    }

    public Hashtable<Integer, TileSet> getTileSets() {
        return tileSets;
    }

    public int getEnterPos() {
        return enterPos;
    }

    public int getTrees() {
        return trees;
    }

    public int getTorches() {
        return torches;
    }

    // return true if player can move onto tile with the coordinates x and y
    public boolean isSolid(int x, int y) {
        int map_y = y / GamePanel.NEW_TILE_SIZE;
        int map_x = x / GamePanel.NEW_TILE_SIZE;

        if (y < 0 || x < 0 || map_y > 31 || map_x > 31) {
            return false;
        } else {
            if (map[solid][map_y][map_x] != null) {
                for (int i = 0; i < map.length; i++) {
                    if (i != solid && i != trees) {
                        if (map[i][map_y][map_x] != null) {
                            return false;
                        }
                    }
                }
                return true;
            }
        }
        return false;
    }

    public boolean setChest(int x, int y, BufferedImage chest) {
        if (isChest(x, y)) {
            map[chests][y / GamePanel.NEW_TILE_SIZE][x / GamePanel.NEW_TILE_SIZE] = chest;

            return true;
        }

        return false;
    }

    public boolean isChest(int x, int y) {
        int map_y = y / GamePanel.NEW_TILE_SIZE;
        int map_x = x / GamePanel.NEW_TILE_SIZE;

        if (y < 0 || x < 0 || map_y > 31 || map_x > 31) {
            return false;
            //variables für solid/chest/deko/eingang im konstruktor setzen
        } else if (map[chests][map_y][map_x] != null) {
            return true;
        }
        return false;
    }

    public boolean isExit(int x, int y) {
        int map_y = y / GamePanel.NEW_TILE_SIZE;
        int map_x = x / GamePanel.NEW_TILE_SIZE;

        if (y < 0 || x < 0 || map_y > 31 || map_x > 31) {
            return false;
        } else if (map[exit][map_y][map_x] != null) {
            return true;
        }
        return false;
    }

    public int getChests() {
        return chests;
    }

    public ArrayList<Enemy> getEnemies() {
        if (enemies != null) {
            return enemies;
        }

        enemies = new ArrayList<>();
        Random random = new Random();
        // Biome, Level, Number of enemies should be contained in the xml file
        for (int i = 0; i < id * 5; i++) {  //id * 5
            int x, y;
            do {
                x = random.nextInt(32) * GamePanel.NEW_TILE_SIZE;
                y = random.nextInt(32) * GamePanel.NEW_TILE_SIZE;

            } while (!isSolid(x, y));

            Enemy enemy;
            if (i % 2 == 0) {
                enemy = new Enemy(x, y, 1, 52, 200, 90, EntityType.skeletonWarrior, 0);
            } else {
                enemy = new Enemy(x, y, 1, 52, 300, 250, EntityType.skeletonArcher, 0);
            }
            enemies.add(enemy);
        }
        return enemies;
    }
}
