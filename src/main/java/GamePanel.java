import objectClasses.Abstract.Entity;
import objectClasses.Abstract.Item;
import objectClasses.Player;
import objectClasses.Weapon;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;


public class GamePanel extends JPanel implements Runnable {

    final int SCALE_FACTOR = 3; // 16 x 16 won't actually be displayed as 16 x 16
    final int ORIGINAL_TILE_SIZE = 16; // 16 x 16 pixel
    final int NEW_TILE_SIZE = ORIGINAL_TILE_SIZE * SCALE_FACTOR;

    final int MAX_SCREEN_COL = 16; // max. 16 tiles in x
    final int MAX_SCREEN_ROW = 12; // max. 12 tiles in y

    final int WINDOW_WIDTH = NEW_TILE_SIZE * MAX_SCREEN_COL; // 768 pixel
    final int WINDOW_HEIGHT = NEW_TILE_SIZE * MAX_SCREEN_ROW; // 576 pixel

    InputHandler keyHandler = new InputHandler(); // own class
    Thread gameThread = null;

    //public static int playerPosX = 100; // static because of InputHandler
    //public static int playerPosY = 100; // static because of InputHandler
    //public static int playerSpeed = 4; // static because of InputHandler

    int FPS = 60;
    // needed because repaint() is called depending
    // on the frames per second -> thus we need to
    // regulate it


    Player player = new Player(100, 100, 3, 5, null, 3, 1);


    public GamePanel() {
        this.setPreferredSize(new Dimension(WINDOW_WIDTH, WINDOW_HEIGHT));
        this.setBackground(Color.black);
        this.addKeyListener(keyHandler); // adds Listener
        this.setDoubleBuffered(true); // improves rendering
        this.setFocusable(true); // GamePanel "focused" to receive key input

        ArrayList<BufferedImage> entityAppearance = new ArrayList<>();

        BufferedImage bi = null;
        BufferedImage bi2 = null;
        BufferedImage bi3 = null;
        BufferedImage bi4 = null;

        try {
            bi = ImageIO.read(new File("src/main/resources/playerUp.png"));
            bi2 = ImageIO.read(new File("src/main/resources/playerDown.png"));
            bi3 = ImageIO.read(new File("src/main/resources/playerLeft.png"));
            bi4 = ImageIO.read(new File("src/main/resources/playerRight.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        assert false;
        entityAppearance.add(bi);
        entityAppearance.add(bi2);
        entityAppearance.add(bi3);
        entityAppearance.add(bi4);

        player.setEntityAppearance(entityAppearance);
        entityArrayList.add(player);

        startGameThread();
    }

    public void startGameThread() {
        gameThread = new Thread(this);
        gameThread.start();
    }

    @Override
    public void run() {

        long drawInterval = 1000000000 / FPS;
        long nextDrawTime = System.nanoTime() + drawInterval;

        while (gameThread != null) {

            // for the meantime update() not necessary because
            // inputhandler changes the pos of the player in its class
            // should be changed
            try {
                update(); // updates: character positions, etc...
            } catch (IOException e) {
                e.printStackTrace();
            }
            repaint(); // actually calls paintComponent() -> noShit, is confusing

            try {
                long remainingTime = nextDrawTime - System.nanoTime();
                remainingTime = remainingTime / 1000000; // from nano to millis

                if (remainingTime < 0) {
                    remainingTime = 0;
                }

                Thread.sleep(remainingTime);
                nextDrawTime += drawInterval;

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


    public void update() throws IOException {

        if (keyHandler.upPressed) {
            player.setPositionY(player.getPositionY() - player.getMovementSpeed());
            player.setCurrentAppearance(0);
        } else if (keyHandler.downPressed) {
            player.setPositionY(player.getPositionY() + player.getMovementSpeed());
            player.setCurrentAppearance(1);
        } else if (keyHandler.leftPressed) {
            player.setPositionX(player.getPositionX() - player.getMovementSpeed());
            player.setCurrentAppearance(2);
        } else if (keyHandler.rightPressed) {
            player.setPositionX(player.getPositionX() + player.getMovementSpeed());
            player.setCurrentAppearance(3);
        }
    }


    ArrayList<Entity> entityArrayList = new ArrayList<>();

    public void paintComponent(Graphics graph) {
        super.paintComponent(graph);
        Graphics2D graph2D = (Graphics2D) graph;

        for (Entity entity : entityArrayList) {
            graph2D.drawImage(player.getEntityAppearance().get(entity.getCurrentAppearance()), entity.getPositionX(), entity.getPositionY(), NEW_TILE_SIZE, NEW_TILE_SIZE, this);
        }

        /*
        for(Entity entity : entityArrayList){

        }

         */
        /*
        BufferedImage bi = null;

        try {
            bi = ImageIO.read(new File("src/main/resources/item/armor/armor_type_chainmail.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        graph2D.drawImage(bi, playerPosX, playerPosY, NEW_TILE_SIZE, NEW_TILE_SIZE, this);
        graph2D.dispose();

         */
    }

    public static void main(String[] args) {

    }

}