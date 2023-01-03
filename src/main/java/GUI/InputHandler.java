package GUI;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class InputHandler implements KeyListener {

    protected boolean upPressed, downPressed, leftPressed, rightPressed;
    protected boolean attackPressed;
    protected int lastPressed;

    Set<Integer> keys = new HashSet<Integer>();

    public Set<Integer> getKeys() { return keys;}

    @Override
    public void keyTyped(KeyEvent e) {}
    // all 3 are required by KeyListener,
    // however we use this specific on not


    @Override
    public void keyPressed(KeyEvent e) {
        int eventCode = e.getKeyCode();

        if (eventCode == KeyEvent.VK_W) {
            upPressed = true;
            lastPressed = KeyEvent.VK_W;
        }
        if (eventCode == KeyEvent.VK_A) {
            leftPressed = true;
            lastPressed = KeyEvent.VK_A;
        }
        if (eventCode == KeyEvent.VK_S) {
            downPressed = true;
            lastPressed = KeyEvent.VK_S;
        }
        if (eventCode == KeyEvent.VK_D) {
            rightPressed = true;
            lastPressed = KeyEvent.VK_D;
        }
        if (eventCode == KeyEvent.VK_C) {
            attackPressed = true;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int eventCode = e.getKeyCode();

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
        if (eventCode == KeyEvent.VK_C) {
            attackPressed = false;
        }
    }
}
