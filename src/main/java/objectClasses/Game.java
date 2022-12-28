package objectClasses;

import GUI.GamePanel;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.imageio.ImageIO;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.awt.*;
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

    public Game(Player player) throws IOException, ParserConfigurationException, SAXException {
        this.player = player;
        loadTileSetsFromFiles();
        loadLevelsFromFile();
        currentLevelNumber = 1;
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

    private Level getCurrentLevel() {
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

    public void render(Graphics2D g) {//} throws IOException {
        Level level = getCurrentLevel();
        for (int i = 0; i < (32 * 32); i++) {
            g.drawImage(level.getMap()[(int) i / 32][i % 32],
                    (GamePanel.NEW_TILE_SIZE * (i % 32)) - player.getPositionX(), GamePanel.NEW_TILE_SIZE * (int) (i / 32) - player.getPositionY(),
                    GamePanel.NEW_TILE_SIZE, GamePanel.NEW_TILE_SIZE,
                    null);

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
}
