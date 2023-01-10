package objectClasses;

import GUI.GamePanel;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.imageio.ImageIO;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Game {
    private Player player; //list???
    private List<Level> levels;
    private int currentLevelNumber;
    private static List<TileSet> tileSets;

    public Game() throws IOException, ParserConfigurationException, SAXException {
        loadTileSetsFromFiles();
        loadLevelsFromFile();
        currentLevelNumber = 1;

        int x = (int) ((getCurrentLevel().getEnterPos() % 32 + 0.5) * GamePanel.NEW_TILE_SIZE);
        int y = (getCurrentLevel().getEnterPos() / 32 + 1) * GamePanel.NEW_TILE_SIZE;
        this.player = new Player(x, y, 3, 5, 3);
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
            if (i != level.getTrees()) {
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
            //}
        }
    }
}
