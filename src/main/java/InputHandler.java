import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashSet;
import java.util.Set;

public class InputHandler implements KeyListener {

    protected boolean upPressed, downPressed, leftPressed, rightPressed;
    Set<Integer> keys = new HashSet<Integer>();

    public Set<Integer> getKeys() { return keys;}

    @Override
    public void keyTyped(KeyEvent e) {}
    // all 3 are required by KeyListener,
    // however we use this specific on not


    @Override
    public void keyPressed(KeyEvent e) {
        int eventCode = e.getKeyCode();  // was used with the code that's inactive
        //keys.add(e.getKeyCode());
        //multiKeys();

        if (eventCode == KeyEvent.VK_W) {
            upPressed = true;
        }
        if (eventCode == KeyEvent.VK_A) {
            leftPressed = true;
        }
        if (eventCode == KeyEvent.VK_S) {
            downPressed = true;
        }
        if (eventCode == KeyEvent.VK_D) {
            rightPressed = true;
        }


    }

    /*
    public void multiKeys() {
        System.out.println(keys.size());
        for(Integer key: keys) {
            System.out.println(key);
            switch (key) {
                // maybe -> save in List and via getter let update do it?
                case KeyEvent.VK_W -> GamePanel.playerPosY -= GamePanel.playerSpeed;
                case KeyEvent.VK_S -> GamePanel.playerPosY += GamePanel.playerSpeed;
                case KeyEvent.VK_A -> GamePanel.playerPosX -= GamePanel.playerSpeed;
                case KeyEvent.VK_D -> GamePanel.playerPosX += GamePanel.playerSpeed;
            }
        }
    }

     */

    @Override
    public void keyReleased(KeyEvent e) {
        int eventCode = e.getKeyCode(); // was used with the code that's inactive
        //keys.remove(e.getKeyCode());

        if (eventCode == KeyEvent.VK_W) {
            upPressed = false;
        }
        if (eventCode == KeyEvent.VK_A) {
            leftPressed = false;
        }
        if (eventCode == KeyEvent.VK_S) {
            downPressed = false;
        }
        if (eventCode == KeyEvent.VK_D) {
            rightPressed = false;
        }


    }
}
