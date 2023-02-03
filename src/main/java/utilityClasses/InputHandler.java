package utilityClasses;

import GUI.GamePanel;
import GUI.OptionsMenuPanel;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.Arrays;

public class InputHandler implements KeyListener {

    private static int upKey, leftKey, downKey, rightKey, attackKey, inventoryKey, interactKey;
    public boolean optionsPressed, inventoryPressed, interactPressed;
    public int lastDirection, attackDirection, walkingDirection;
    public ArrayList<Integer> movementKeys = new ArrayList<>();
    private final boolean[] states = new boolean[3];

    public InputHandler() {

        initVariables();
        loadKeyBindings();
    }

    public void initVariables() {

        // 0 -> resting
        // 1 -> walking
        // 2 -> attacking
        setCurrentState(0);

        lastDirection = 1;
        attackDirection = 10000;
    }

    public int getCurrentDirection() {

        if (isInState(2)) return attackDirection;
        else if (isInState(1)) return walkingDirection;
        else return lastDirection;
    }

    public static String getKeyName(String name) {

        return switch (name) {
            case "interact" -> KeyEvent.getKeyText(interactKey);
            case "inventory" -> KeyEvent.getKeyText(inventoryKey);
            default -> "";
        };
    }

    public static int getKeyCode(String name) {

        return switch (name) {
            case "interact" -> interactKey;
            case "inventory" -> inventoryKey;
            default -> 0;
        };
    }

    // In this method, a file containing the saved key
    // bindings would be loaded to update possible changes
    public static void loadKeyBindings() {

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
                keyName = keyName.toLowerCase();

                // Checks which key to bind it to
                if (keyName.contains("up")) {
                    upKey = keyCode;
                } else if (keyName.contains("left")) {
                    leftKey = keyCode;
                } else if (keyName.contains("down")) {
                    downKey = keyCode;
                } else if (keyName.contains("right")) {
                    rightKey = keyCode;
                } else if (keyName.contains("attack")) {
                    attackKey = keyCode;
                } else if (keyName.contains("inventory")) {
                    inventoryKey = keyCode;
                } else if (keyName.contains("interact")) {
                    interactKey = keyCode;
                }
            }
        }
    }

    private boolean isMovementKey(int key) {

        return key == upKey || key == leftKey || key == downKey || key == rightKey;
    }

    private int abstractKeyValue(int key) {

        if (key == upKey) return 0;
        else if (key == downKey) return 1;
        else if (key == leftKey) return 2;
        else return 3;
    }

    public void setCurrentState(int currentState) {

        Arrays.fill(states, false);

        if (currentState < states.length && currentState >= 0) {
            states[currentState] = true;
        }
    }

    public boolean movementKeyPressed() {

        return movementKeys.size() > 0;
    }

    public boolean isInState(int state) {

        if (state >= states.length) return false;
        return states[state];
    }

    private void handleAttack() {

        if (attackDirection == 10000) {
            if (walkingDirection == 10000) {
                attackDirection = lastDirection;
            } else {
                attackDirection = walkingDirection;
            }
        }
    }

    private void addMovementKey(int key) {

        if (!movementKeys.contains(key)) {
            if (movementKeys.size() < 2) {
                movementKeys.add(key);
            }
            setCurrentState(1);
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

    private void removeMovementKey(int key) {

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
            if (!isInState(2)) setCurrentState(0);
        }
    }

    private void handleKeyPressed(int key) {

        if (key == KeyEvent.VK_ESCAPE) {
            optionsPressed = true;
        } else if (key == inventoryKey) {
            inventoryPressed = true;
        } else if (key == interactKey) {
            interactPressed = true;
        } else if (key == attackKey) {
            setCurrentState(2);
        }

        if (isInState(2)) {
            handleAttack();
            return;
        }

        // Only allows movement keys to be added to the key array
        if (isMovementKey(key)) {
            key = abstractKeyValue(key);
            addMovementKey(key);
        }
    }

    private void handleKeyReleased(int key) {

        if (isMovementKey(key)) {
            key = abstractKeyValue(key);
            removeMovementKey(key);
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();

        if (GamePanel.isLoading) {
            if (key == interactKey) {
                interactPressed = true;
            }
            e.consume();
            return;
        }
        if (GamePanel.isOpeningChest) {
            e.consume();
            return;
        }
        handleKeyPressed(key);
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int key = e.getKeyCode();

        handleKeyReleased(key);
    }

    // all 3 are required by KeyListener,
    // however we don't use this specific one
    public void keyTyped(KeyEvent e) {
    }
}
