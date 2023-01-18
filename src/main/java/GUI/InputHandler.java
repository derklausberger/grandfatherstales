package GUI;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;

public class InputHandler implements KeyListener {

    protected static int upKey, leftKey, downKey, rightKey, attackKey, inventoryKey;
    public boolean attackPressed;
    protected boolean menuPressed, inventoryPressed;
    public int lastDirection;
    protected int attackDirection = 10000, walkingDirection;
    public ArrayList<Integer> movementKeys = new ArrayList<>();

    public InputHandler() {

        loadKeyBindings();
    }

    // In this method, a file containing the saved key
    // bindings would be loaded to update possible changes
    private void loadKeyBindings() {

        // Arrow keys are "VK_UP" / "VK_DOWN"/..
        upKey = KeyEvent.VK_W;
        leftKey = KeyEvent.VK_A;
        downKey = KeyEvent.VK_S;
        rightKey = KeyEvent.VK_D;
        attackKey = KeyEvent.VK_C;
        inventoryKey = KeyEvent.VK_E;
    }

    private boolean isMovementKey(int key) {

        return key == upKey || key == leftKey || key == downKey || key == rightKey;
    }


    // all 3 are required by KeyListener,
    // however we don't use this specific one
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();

        if (key == KeyEvent.VK_ESCAPE) {
            menuPressed = true;
        } else if (key == attackKey) {
            attackPressed = true;
        } else if (key == inventoryKey) {
            inventoryPressed = true;
        }

        // Only allows movement keys to be added to the key array
        if (!movementKeys.contains(key) && isMovementKey(key)) {
            if (movementKeys.size() < 2) {
                movementKeys.add(key);
            }
        }

        if (attackPressed && attackDirection == 10000) {
            if (walkingDirection == 10000) {
                attackDirection = lastDirection;
            } else {
                attackDirection = walkingDirection;
            }
        }

        // If one key is pressed at a time, walks that direction
        // If a second key is pressed additionally, overwrites the
        // previous direction to that key
        walkingDirection = switch (movementKeys.size()) {
            case 1 -> movementKeys.get(0);
            case 2 -> movementKeys.get(1);
            default -> 10000;
        };
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int key = e.getKeyCode();

        // Removes the movement key from the list
        if (movementKeys.contains(key)) {
            movementKeys.remove((Integer) key);
        }

        // Changes the walking direction to the
        // last direction or releases it
        if (movementKeys.size() == 1) {
            walkingDirection = movementKeys.get(0);
        } else if (movementKeys.size() == 0) {

            lastDirection = walkingDirection;
            walkingDirection = 10000;
        }
    }
}
