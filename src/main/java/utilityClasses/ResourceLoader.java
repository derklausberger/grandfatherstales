package utilityClasses;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import javax.imageio.ImageIO;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import java.awt.*;

import java.awt.image.BufferedImage;
import java.io.InputStreamReader;
import java.net.*;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStream;
import java.util.Objects;
import java.io.IOException;
import javax.swing.ImageIcon;

public final class ResourceLoader {

    private static final ResourceLoader RESOURCE_LOADER = new ResourceLoader();

    private ResourceLoader() {}

    public static ResourceLoader getResourceLoader() {
        return RESOURCE_LOADER;
    }

    public BufferedImage getBufferedImage(String filePath) {

        BufferedImage image = null;
        File required = null;
        URL url = null;
        String protocol = null;
        String[] pathArray = null;
        String[] filePath2 = null;

        ClassLoader loader = this.getClass().getClassLoader();
        url = loader.getResource(filePath.replaceFirst("/", ""));
        filePath2 = null;
        assert url != null;
        protocol = url.getProtocol();
        if (protocol.equals("jar")) {
            try {
                url = new URL(url.getPath());
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            protocol = url.getProtocol();
        }
        if (protocol.equals("file")) {
            pathArray = url.getPath().split("!");
            filePath2 = pathArray[0].split("/", 2);
        }
        required = new File(filePath2[1]);
        System.out.println(filePath2[1]);

        try {
            //InputStream in = this.getClass().getClassLoader().getResourceAsStream(filePath.replace("/", "\\"));
            //image = ImageIO.read(new File(new URI(filePath.toString().replace(" ","%20")).getSchemeSpecificPart()));
            image = ImageIO.read(required);
            //image = ImageIO.read((InputStream) new File("\\screen\\mainMenuPanel\\mainMenu.png").toPath());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return image;
    }

    public AudioInputStream getAudio(String filePath) {

        AudioInputStream ais = null;
        try {
            ais = AudioSystem.getAudioInputStream(new File(Objects.requireNonNull(getClass().getResource(filePath)).toURI()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ais;
    }

    public ImageIcon getImageIcon(String filePath, int LOGO_WIDTH, int LOGO_HEIGHT) {

        BufferedImage buf = null;
        ImageIcon image = null;
        System.out.println(filePath);
        buf = getBufferedImage(filePath);
        image = new ImageIcon(buf.getScaledInstance(LOGO_WIDTH, LOGO_HEIGHT, Image.SCALE_DEFAULT));

        return image;
    }


    public File getFile(String filePath) {

        File file;
        try {
            file = new File(Objects.requireNonNull(getClass().getResource(filePath)).toURI());
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }

        return file;
    }

    public Font getFontByFilePath(String filePath) {

        File file;
        Font font;
        try {
            file = new File(Objects.requireNonNull(getClass().getResource("/fonts/" + filePath)).toURI());
            font = Font.createFont(Font.TRUETYPE_FONT, file);
        } catch (URISyntaxException | IOException | FontFormatException e) {
            throw new RuntimeException(e);
        }

        return font;
    }

    public JsonObject readStaticJsonFile(String filePath) {

        Gson gson = new Gson();
        JsonObject jsonObject;

        try (InputStream inputStream = getClass().getResourceAsStream("/jsonFiles/" + filePath)) {
            jsonObject = gson.fromJson(new InputStreamReader(Objects.requireNonNull(inputStream)), JsonObject.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return jsonObject;
    }

    public JsonObject readConfigJsonFile(String filePath) {

        Gson gson = new Gson();
        JsonObject jsonObject = null;

        try {
            jsonObject = gson.fromJson(new FileReader(filePath), JsonObject.class);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return jsonObject;
    }

    public void writeJsonFile(String filePath, JsonObject jsonObject) {

        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        try (FileWriter writer = new FileWriter(filePath)) {
            gson.toJson(jsonObject, writer);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}