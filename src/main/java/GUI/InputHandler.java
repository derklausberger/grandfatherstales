package GUI;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;

public class InputHandler implements KeyListener {

    public boolean attackPressed;
    protected boolean menuPressed, inventoryPressed;
    public int lastDirection;
    protected int hitDirection = 10000, currentPressed;
    public ArrayList<Integer> movementKeys = new ArrayList<>();

    @Override
    public void keyTyped(KeyEvent e) {
    }
    // all 3 are required by KeyListener,
    // however we don't use this specific one


    @Override
    public void keyPressed(KeyEvent e) {
        int eventCode = e.getKeyCode();

        if (eventCode == KeyEvent.VK_ESCAPE) {
            menuPressed = true;
        } else if (eventCode == KeyEvent.VK_C) {
            attackPressed = true;
        } else if (eventCode == KeyEvent.VK_E) {
            inventoryPressed = true;
        }

        // Only allows specified keys to be added to the key array
        if (!movementKeys.contains(eventCode) && isMovementKey(eventCode)) {
            if (movementKeys.size() < 2) {
                movementKeys.add(eventCode);
            }
        }

        if (attackPressed && hitDirection == 10000) {
            if (currentPressed == 10000) {
                hitDirection = lastDirection;
            } else {
                hitDirection = currentPressed;
            }
        }

        // If one key is pressed at a time, walks that direction
        // If a second key is pressed additionally, overwrites the
        // previous direction to that key
        currentPressed = switch (movementKeys.size()) {
            case 1 -> movementKeys.get(0);
            case 2 -> movementKeys.get(1);
            default -> 10000;
        };
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int eventCode = e.getKeyCode();

        // Removes the movement key from the list
        if (movementKeys.contains(eventCode)) {
            movementKeys.remove(movementKeys.indexOf(eventCode));
        }

        // Changes the walking direction to the
        // last direction or releases it
        if (movementKeys.size() == 1) {
            currentPressed = movementKeys.get(0);
        } else if (movementKeys.size() == 0) {

            lastDirection = currentPressed;
            currentPressed = 10000;
        }
    }

    private boolean isMovementKey(int eventCode) {

        // Could read a file to check whether the movement
        // keys are (W-A-S-D) or e.g. arrow keys
        switch (eventCode) {
            case KeyEvent.VK_W, KeyEvent.VK_A, KeyEvent.VK_S, KeyEvent.VK_D -> {
                return true;
            }
        }
        return false;
    }
}
