package GUI;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.security.Key;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class InputHandler implements KeyListener {

    protected boolean upPressed, downPressed, leftPressed, rightPressed, keyPressed;
    protected boolean attackPressed, menuPressed;
    protected int lastPressed = 10000, currentPressed = 10000, lastDirection;
    private ArrayList<Integer> directions = new ArrayList<>();

    Set<Integer> keys = new HashSet<Integer>();

    public Set<Integer> getKeys() { return keys;}


    @Override
    public void keyTyped(KeyEvent e) {}
    // all 3 are required by KeyListener,
    // however we don't use this specific one


    @Override
    public void keyPressed(KeyEvent e) {
        int eventCode = e.getKeyCode();
        currentPressed = eventCode;

        if (!directions.contains(eventCode) && eventCode != KeyEvent.VK_C) {
            directions.add(eventCode);
        }

        if (lastPressed == 10000 && eventCode != KeyEvent.VK_C) lastPressed = eventCode;
        if (currentPressed != 10000) {
            keyPressed = true;
        }

        if (eventCode == KeyEvent.VK_W && lastPressed == eventCode) {
            upPressed = true;
        }
        if (eventCode == KeyEvent.VK_A && lastPressed == eventCode) {
            leftPressed = true;
        }
        if (eventCode == KeyEvent.VK_S && lastPressed == eventCode) {
            downPressed = true;
        }
        if (eventCode == KeyEvent.VK_D && lastPressed == eventCode) {
            rightPressed = true;
        }
        if (eventCode == KeyEvent.VK_C) {
            attackPressed = true;
        }
        if (eventCode == KeyEvent.VK_ESCAPE) {
            menuPressed = true;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int eventCode = e.getKeyCode();

        if (eventCode == KeyEvent.VK_W) {
            upPressed = false;
            directions.remove(directions.indexOf(eventCode));
        }
        if (eventCode == KeyEvent.VK_A) {
            leftPressed = false;
            directions.remove(directions.indexOf(eventCode));
        }
        if (eventCode == KeyEvent.VK_S) {
            downPressed = false;
            directions.remove(directions.indexOf(eventCode));
        }
        if (eventCode == KeyEvent.VK_D) {
            rightPressed = false;
            directions.remove(directions.indexOf(eventCode));
        }
        if (eventCode == KeyEvent.VK_C) {
            attackPressed = false;
        }

        if (directions.isEmpty()) {
            keyPressed = false;

            if (lastPressed == KeyEvent.VK_W) {
                lastDirection = 27;
            } else if (lastPressed == KeyEvent.VK_A) {
                lastDirection = 9;
            } else if (lastPressed == KeyEvent.VK_S) {
                lastDirection = 0;
            } else if (lastPressed == KeyEvent.VK_D) {
                lastDirection = 18;
            }
            lastPressed = 10000;

        } else {
            if (directions.get(0) == KeyEvent.VK_W) {
                upPressed = true;
                lastPressed = KeyEvent.VK_W;
            } else if (directions.get(0) == KeyEvent.VK_A) {
                leftPressed = true;
                lastPressed = KeyEvent.VK_A;
            } else if (directions.get(0) == KeyEvent.VK_S) {
                downPressed = true;
                lastPressed = KeyEvent.VK_S;
            } else if (directions.get(0) == KeyEvent.VK_D) {
                rightPressed = true;
                lastPressed = KeyEvent.VK_D;
            }
        }

    }

}
