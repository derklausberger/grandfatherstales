package utilityClasses;

import java.awt.*;

public class AnimationFrame {

    private final Image image;
    private final int width, height;
    private final int xOffset, yOffset;

    public AnimationFrame(Image image, int width, int height, int xOffset, int yOffset) {
        this.image = image;
        this.width = width;
        this.height = height;
        this.xOffset = xOffset;
        this.yOffset = yOffset;
    }

    public Image getImage() {
        return image;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getXOffset() {
        return xOffset;
    }

    public int getYOffset() {
        return yOffset;
    }
}
