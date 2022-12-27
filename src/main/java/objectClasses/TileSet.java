package objectClasses;

public class TileSet {
    private String pngFileName;
    private String xmlFileName;

    private int widthPixel;
    private int heightPixel;

    private int widthTiles;
    private int heightTiles;

    public TileSet(String pngFileName, String xmlFileName, int widthPixel, int heightPixel, int widthTiles, int heightTiles) {
        this.pngFileName = pngFileName;
        this.xmlFileName = xmlFileName;
        this.widthPixel = widthPixel;
        this.heightPixel = heightPixel;
        this.widthTiles = widthTiles;
        this.heightTiles = heightTiles;
    }

    public String getXmlFileName() {
        return xmlFileName;
    }

    public String getPngFileName() {
        return pngFileName;
    }

    public int getWidthTiles() {
        return widthTiles;
    }
}
