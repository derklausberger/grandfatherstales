package GUI;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;

public class InputHandler implements KeyListener {

    protected boolean upPressed, downPressed, leftPressed, rightPressed;
    protected boolean attackPressed, menuPressed;
    protected int lastPressed = 10000, currentPressed, lastDirection;
    public ArrayList<Integer> keys = new ArrayList<>();

    @Override
    public void keyTyped(KeyEvent e) {
    }
    // all 3 are required by KeyListener,
    // however we don't use this specific one


    @Override
    public void keyPressed(KeyEvent e) {
        int eventCode = e.getKeyCode();

        if (!keys.contains(eventCode) && eventCode != KeyEvent.VK_C && eventCode != KeyEvent.VK_ESCAPE) {
            keys.add(eventCode);
        }

        if (eventCode == KeyEvent.VK_W || eventCode == KeyEvent.VK_A ||
                eventCode == KeyEvent.VK_S || eventCode == KeyEvent.VK_D ||
                eventCode == KeyEvent.VK_C || eventCode == KeyEvent.VK_ESCAPE) {


            if (lastPressed == 10000 && eventCode != KeyEvent.VK_C && eventCode != KeyEvent.VK_ESCAPE && !attackPressed) {
                lastPressed = eventCode;

                // Necessary, because e.g. the player moves upwards and attacks, he attacks upwards
                // But, when moving in any other direction after that, not releasing that movement key
                // and attacking again, it will attack facing the previous direction (upwards)
                // instead of the current direction
                lastDirection = eventCode;
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

            if (eventCode == KeyEvent.VK_ESCAPE) {
                menuPressed = true;
            }
            if (eventCode == KeyEvent.VK_C) {

                attackPressed = true;
            }

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

        if (eventCode != KeyEvent.VK_C && eventCode != KeyEvent.VK_ESCAPE) {
            keys.remove(keys.indexOf(eventCode));
        }

        if (eventCode == KeyEvent.VK_W || eventCode == KeyEvent.VK_A ||
                eventCode == KeyEvent.VK_S || eventCode == KeyEvent.VK_D) {

            if (keys.isEmpty()) {

                if (!attackPressed) {

                    lastDirection = lastPressed;
                    lastPressed = 10000;
                    //currentPressed = 10000;
                }

            } else {

                if (keys.get(0) == KeyEvent.VK_W) {
                    upPressed = true;
                    lastPressed = KeyEvent.VK_W;
                } else if (keys.get(0) == KeyEvent.VK_A) {
                    leftPressed = true;
                    lastPressed = KeyEvent.VK_A;
                } else if (keys.get(0) == KeyEvent.VK_S) {
                    downPressed = true;
                    lastPressed = KeyEvent.VK_S;
                } else if (keys.get(0) == KeyEvent.VK_D) {
                    rightPressed = true;
                    lastPressed = KeyEvent.VK_D;
                }
            }

        }
    }

}
