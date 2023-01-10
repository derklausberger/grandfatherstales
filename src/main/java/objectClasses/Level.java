package objectClasses;

import GUI.GamePanel;
import objectClasses.Abstract.Entity;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.imageio.ImageIO;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Map;
import java.util.Random;

public class Level {
    private int id;
    private BufferedImage[][][] map;//layers height width
    private Hashtable<Integer, TileSet> tileSets;
    private ArrayList<Enemy> enemies = null;
    private int obstacles = -1, chests = -1, solid = -1, trees = -1, enterPos = -1;

    public Level(File mapXMLFile) throws ParserConfigurationException, IOException, SAXException {
        int map_width = 32;
        int map_height = 32;

        tileSets = new Hashtable<>();
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(mapXMLFile);

        NodeList nodeList = doc.getElementsByTagName("tileset");
        Node n;
        Element e;
        for (int i = 0; i < nodeList.getLength(); i++) {
            n = nodeList.item(i);
            e = (Element) n;

            tileSets.put(Integer.valueOf(e.getAttribute("firstgid")), Game
                    .getTilesetFromXMLFileName(e.getAttribute("source")
                            .replace("../", "")));
        }

        nodeList = doc.getElementsByTagName("layer");

        map = new BufferedImage[nodeList.getLength()][map_height][map_width];
        for (int i = 0; i < nodeList.getLength(); i++) {
            n = nodeList.item(i);
            e = (Element) n;

            if (e.getAttribute("name").equals("chests")) {
                chests = i;
            } else if (e.getAttribute("name").equals("obstacles")) {
                obstacles = i;
            } else if (e.getAttribute("name").equals("trees")) {
                trees = i;
            } else if (e.getAttribute("name").equals("solid")) {
                solid = i;
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

                if (tileSet != null) {
                    field -= tileSetKey;
                    fieldX = (field % tileSet.getWidthTiles()) * 16;
                    fieldY = (field / tileSet.getWidthTiles()) * 16;
                    map[i][j / map_height][j % map_width] = ImageIO.read(new File(tileSet.getPngFileName())).getSubimage(fieldX, fieldY, 16, 16);
                    if (e.getAttribute("name").equals("enter")) {
                        enterPos = j;
                        System.out.println("enter:" +  j);
                    }
                } else {
                    map[i][(int) (j / map_height)][j % map_width] = null;
                }

                j++;
            }
        }
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


    public ArrayList<Enemy> getEnemies() {
        if (enemies != null) {
            return enemies;
        }

        enemies = new ArrayList<>();
        Random random = new Random();
        // Biome, Level, Number of enemies should be contained in the xml file
        for (int i = 0; i < 10; i++) {
            int x, y;
            do {
                x = random.nextInt(32) * GamePanel.NEW_TILE_SIZE;
                y = random.nextInt(32) * GamePanel.NEW_TILE_SIZE;

            } while (!isSolid(x,y));
            Enemy e = new Enemy(x,y,1,2);
            enemies.add(e);
        }
        return enemies;
    }
}
