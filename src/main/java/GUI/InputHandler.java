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
    protected static void loadKeyBindings() {

        // Gets the current key bindings from the options screen, instead of loading the file itself again
        int keyCode;
        for (String keyName : OptionsMenuPanel.keyBindings.keySet()) {

            // Only interested in the current key bindings, not default
            if (keyName.contains("Current")) {
                String keyValue = OptionsMenuPanel.keyBindings.get(keyName);

                // If the key value is one char long, it can be
                // converted to its key code
                if (keyValue.length() == 1) {
                    keyCode = KeyEvent.getExtendedKeyCodeForChar(
                            keyValue.toCharArray()[0]);
                } else {

                    // If the key value is longer than one char,
                    // sets the key to the default value
                    keyCode = KeyEvent.getExtendedKeyCodeForChar(
                            OptionsMenuPanel.keyBindings
                                    .get(keyName.replace("Current", "Default"))
                                    .toCharArray()[0]);

                    // Checks if the key value is one of the arrow keys
                    keyCode = switch (keyValue) {
                        case "Oben" -> KeyEvent.VK_UP;
                        case "Links" -> KeyEvent.VK_LEFT;
                        case "Unten" -> KeyEvent.VK_DOWN;
                        case "Rechts" -> KeyEvent.VK_RIGHT;
                        default -> keyCode;
                    };

                }

                // Checks which key to bind it to
                if (keyName.contains("Up")) {
                    upKey = keyCode;
                } else if (keyName.contains("Left")) {
                    leftKey = keyCode;
                } else if (keyName.contains("Down")) {
                    downKey = keyCode;
                } else if (keyName.contains("Right")) {
                    rightKey = keyCode;
                } else if (keyName.contains("attack")) {
                    attackKey = keyCode;
                } else {
                    inventoryKey = keyCode;
                }
            }
        }
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
