package objectClasses;

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
import java.util.Hashtable;
import java.util.Map;

public class Level {
    private int id;
    private BufferedImage[][] map;
    private Hashtable<Integer, TileSet> tileSets;

    public Level(File mapXMLFile) throws ParserConfigurationException, IOException, SAXException {
        int map_width = 32;
        int map_height = 32;

        tileSets = new Hashtable<>();
        map = new BufferedImage[map_height][map_width];
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
        for (int i = 0; i < nodeList.getLength(); i++) {
            n = nodeList.item(i);
            e = (Element) n;

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
                    fieldY = ((int) (field / tileSet.getWidthTiles())) * 16;
                    map[(int) (j / map_height)][j % map_width] = ImageIO.read(new File(tileSet.getPngFileName())).getSubimage(fieldX, fieldY, 16, 16);
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

    public BufferedImage[][] getMap() {
        return map;
    }

    public Hashtable<Integer, TileSet> getTileSets() {
        return tileSets;
    }
}
