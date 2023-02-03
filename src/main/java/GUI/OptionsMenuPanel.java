package GUI;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.google.gson.JsonObject;
import main.Main;
import utilityClasses.AudioManager;
import utilityClasses.InputHandler;
import utilityClasses.ResourceLoader;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class OptionsMenuPanel extends JPanel implements KeyListener {

    private ImageIcon keyBackgroundImage, keyBackgroundImageHover;
    private JPanel optionsContainer, keyBindingsContainer;

    private final JLabel
            backLabel = new JLabel(),
            moveUpKey = new JLabel(),
            moveUpKeyBackground = new JLabel(),
            moveLeftKey = new JLabel(),
            moveLeftKeyBackground = new JLabel(),
            moveDownKey = new JLabel(),
            moveDownKeyBackground = new JLabel(),
            moveRightKey = new JLabel(),
            moveRightKeyBackground = new JLabel(),
            attackKey = new JLabel(),
            attackKeyBackground = new JLabel(),
            inventoryKey = new JLabel(),
            inventoryKeyBackground = new JLabel(),
            interactKey = new JLabel(),
            interactKeyBackground = new JLabel(),
            applyLabel = new JLabel(),
            defaultLabel = new JLabel(),
            cancelLabel = new JLabel();

    private final Map<String, JLabel> keyLabels = new HashMap<>();
    public static final Map<String, String> keyBindings = new HashMap<>();
    private String lastKey;
    private boolean listening;
    private String key = null;

    private final JSlider
            musicVolumeSlider = new JSlider(),
            soundVolumeSlider = new JSlider();

    private final JCheckBox muteBox = new JCheckBox();

    private final JLabel changeKeyBindingsLabel = new JLabel();

    private JsonObject jsonKeys;

    public OptionsMenuPanel() {
        init();
    }

    private void init() {
        setLayout(new GridBagLayout());
        addKeyListener(this);

        muteBox.setOpaque(false);
        musicVolumeSlider.setOpaque(false);
        musicVolumeSlider.setValue(100);
        soundVolumeSlider.setOpaque(false);
        soundVolumeSlider.setValue(100);

        ResourceLoader rl = ResourceLoader.getResourceLoader();
        Font font = rl.getDefaultTextFont();

        loadKeyBindings();
        loadImages();
        addKeyLabels();

        // Don't change the order of the below functions
        GridBagConstraints c = new GridBagConstraints();
        createBackButton(c, font);
        createOptionsContainer();
        createMuteSetting(c, font);
        createMusicSetting(c, font);
        createSoundSetting(c, font);
        createChangeKeyBindingsLabel(c, font);

        setBackgroundImages();
        createKeyBindingLabels(font);
        addConfirmLabels(font);
        setKeyFonts(font);

        createListeners();
    }

    private void loadImages() {
        ResourceLoader rl = ResourceLoader.getResourceLoader();
        keyBackgroundImage = new ImageIcon(
                rl.getBufferedImage("/screen/optionsMenuPanel/keyBackground.png")
                        .getScaledInstance(300, 30, Image.SCALE_SMOOTH));
        keyBackgroundImageHover = new ImageIcon(
                rl.getBufferedImage("/screen/optionsMenuPanel/keyBackgroundHover.png")
                        .getScaledInstance(300, 30, Image.SCALE_SMOOTH));
    }

    private void addKeyLabels() {
        keyLabels.put("moveUp", moveUpKey);
        keyLabels.put("moveLeft", moveLeftKey);
        keyLabels.put("moveDown", moveDownKey);
        keyLabels.put("moveRight", moveRightKey);
        keyLabels.put("attack", attackKey);
        keyLabels.put("inventory", inventoryKey);
        keyLabels.put("interact", interactKey);
        keyLabels.put("moveUpBackground", moveUpKeyBackground);
        keyLabels.put("moveLeftBackground", moveLeftKeyBackground);
        keyLabels.put("moveDownBackground", moveDownKeyBackground);
        keyLabels.put("moveRightBackground", moveRightKeyBackground);
        keyLabels.put("attackBackground", attackKeyBackground);
        keyLabels.put("inventoryBackground", inventoryKeyBackground);
        keyLabels.put("interactBackground", interactKeyBackground);
    }

    private void setKeyFonts(Font font) {
        moveUpKey.setFont(font.deriveFont(20f));
        moveUpKey.setForeground(new Color(0xe0d9ae));
        moveLeftKey.setFont(font.deriveFont(20f));
        moveLeftKey.setForeground(new Color(0xe0d9ae));
        moveDownKey.setFont(font.deriveFont(20f));
        moveDownKey.setForeground(new Color(0xe0d9ae));
        moveRightKey.setFont(font.deriveFont(20f));
        moveRightKey.setForeground(new Color(0xe0d9ae));
        attackKey.setFont(font.deriveFont(20f));
        attackKey.setForeground(new Color(0xe0d9ae));
        inventoryKey.setFont(font.deriveFont(20f));
        inventoryKey.setForeground(new Color(0xe0d9ae));
        interactKey.setFont(font.deriveFont(20f));
        interactKey.setForeground(new Color(0xe0d9ae));
    }

    private void createBackButton(GridBagConstraints c, Font font) {
        // Creates and adds "Back" label
        backLabel.setText("Back");
        backLabel.setFont(font.deriveFont(22f));
        backLabel.setForeground(new Color(0x3d4663));
        c.gridx = 1;
        c.gridy = 0;
        c.weightx = 0;
        c.weighty = 0;
        c.insets = new Insets(20, 20, 0, 0);
        c.anchor = GridBagConstraints.NORTHWEST;
        add(backLabel, c);
    }

    private void createOptionsContainer() {
        // Creates and adds container for options
        // and key bindings
        optionsContainer = new JPanel();
        optionsContainer.setBackground(new Color(0, 0, 0, 120));
        optionsContainer.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 1;
        c.gridwidth = GridBagConstraints.REMAINDER;
        c.gridheight = GridBagConstraints.REMAINDER;
        c.weightx = 1.0;
        c.weighty = 1.0;
        c.insets = new Insets(0, 20, 40, 20);
        c.anchor = GridBagConstraints.CENTER;
        add(optionsContainer, c);

        createKeyBindingsContainer(c);
    }

    private void createKeyBindingsContainer(GridBagConstraints c) {
        keyBindingsContainer = new JPanel();
        keyBindingsContainer.setVisible(false);
        keyBindingsContainer.setLayout(new GridBagLayout());
        keyBindingsContainer.setBackground(new Color(0, 0, 0, 120));
        add(keyBindingsContainer, c);
    }

    private void createMuteSetting(GridBagConstraints c, Font font) {
        // Creates and adds mute label and checkbox
        JLabel muteLabel = new JLabel("Mute");
        muteLabel.setFont(font.deriveFont(22f));
        muteLabel.setForeground(new Color(0xe0d9ae));
        c.gridx = 0;
        c.insets = new Insets(80, 80, 10, 80);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.anchor = GridBagConstraints.CENTER;
        optionsContainer.add(muteLabel, c);

        c.gridx = 1;
        c.fill = GridBagConstraints.NONE;
        optionsContainer.add(muteBox, c);
    }

    private void createMusicSetting(GridBagConstraints c, Font font) {
        // Creates and adds music label and volume slider
        JLabel musicLabel = new JLabel("Music");
        musicLabel.setFont(font.deriveFont(22f));
        musicLabel.setForeground(new Color(0xe0d9ae));
        c.gridx = 0;
        c.gridy = 1;
        c.insets = new Insets(10, 80, 10, 80);
        c.fill = GridBagConstraints.HORIZONTAL;
        optionsContainer.add(musicLabel, c);

        c.gridx = 1;
        optionsContainer.add(musicVolumeSlider, c);
    }

    private void createSoundSetting(GridBagConstraints c, Font font) {
        // Creates and adds sound label and volume slider
        JLabel soundLabel = new JLabel("Sound");
        soundLabel.setFont(font.deriveFont(22f));
        soundLabel.setForeground(new Color(0xe0d9ae));
        c.gridx = 0;
        c.gridy = 2;
        optionsContainer.add(soundLabel, c);

        c.gridx = 1;
        optionsContainer.add(soundVolumeSlider, c);
    }

    private void createChangeKeyBindingsLabel(GridBagConstraints c, Font font) {
        // Adds the changeKeyBindings label
        changeKeyBindingsLabel.setText("Change Key Bindings");
        changeKeyBindingsLabel.setFont(font.deriveFont(22f));
        changeKeyBindingsLabel.setForeground(new Color(0xe0d9ae));
        c.gridx = 0;
        c.gridy = 3;
        c.gridwidth = 2;
        c.insets = new Insets(60, 80, 40, 80);
        c.fill = GridBagConstraints.NONE;
        optionsContainer.add(changeKeyBindingsLabel, c);
    }

    private void setBackgroundImages() {
        // Sets background image of all labels on the right
        for (String labelKey : keyLabels.keySet()) {
            if (labelKey.contains("Background")) {
                keyLabels.get(labelKey).setIcon(keyBackgroundImage);
            }
        }
        applyLabel.setForeground(new Color(0x837E65));
    }

    private void createKeyBindingLabels(Font font) {
        // Creates key binding labels
        for (int i = 0; i < keyLabels.size() / 2; i++) {

            // Constraints for key name on the left
            JLabel keyName = new JLabel();
            keyName.setFont(font.deriveFont(20f));
            keyName.setForeground(new Color(0xe0d9ae));
            GridBagConstraints c = new GridBagConstraints();
            c.gridx = 0;
            c.gridy = i;
            c.weightx = 0;
            c.weighty = 0;
            c.insets = new Insets(10, 80, 10, 10);
            c.anchor = GridBagConstraints.WEST;

            if (i == 0) c.insets = new Insets(50, 80, 10, 10);
            if (i == keyLabels.size() / 2 - 1) c.insets = new Insets(10, 80, 80, 10);
            keyBindingsContainer.add(keyName, c);

            // Constraints for the key on the right
            c.gridx = 2;
            c.gridy = i;
            c.insets = new Insets(15, 10, 10, 80);
            c.anchor = GridBagConstraints.CENTER;

            if (i == 0) c.insets = new Insets(56, 10, 10, 80);
            if (i == keyLabels.size() / 2 - 1) c.insets = new Insets(15, 10, 80, 80);

            // Constraints for the background images on the right
            GridBagConstraints d = new GridBagConstraints();
            d.gridx = 2;
            d.gridy = i;
            d.insets = new Insets(10, 10, 10, 80);
            d.anchor = GridBagConstraints.CENTER;
            if (i == 0) d.insets = new Insets(50, 10, 10, 80);
            if (i == keyLabels.size() / 2 - 1) d.insets = new Insets(10, 10, 80, 80);

            addKeyNameText(keyName, i, c, d);
        }
    }

    private void addKeyNameText(JLabel keyName, int i, GridBagConstraints c, GridBagConstraints d) {

        switch (i) {
            case 0 -> {
                keyName.setText("Move Up");
                keyBindingsContainer.add(moveUpKey, c);
                keyBindingsContainer.add(moveUpKeyBackground, d);
            }
            case 1 -> {
                keyName.setText("Move Left");
                keyBindingsContainer.add(moveLeftKey, c);
                keyBindingsContainer.add(moveLeftKeyBackground, d);
            }
            case 2 -> {
                keyName.setText("Move Down");
                keyBindingsContainer.add(moveDownKey, c);
                keyBindingsContainer.add(moveDownKeyBackground, d);
            }
            case 3 -> {
                keyName.setText("Move Right");
                keyBindingsContainer.add(moveRightKey, c);
                keyBindingsContainer.add(moveRightKeyBackground, d);
            }
            case 4 -> {
                keyName.setText("Attack");
                keyBindingsContainer.add(attackKey, c);
                keyBindingsContainer.add(attackKeyBackground, d);
            }
            case 5 -> {
                keyName.setText("Toggle Inventory");
                keyBindingsContainer.add(inventoryKey, c);
                keyBindingsContainer.add(inventoryKeyBackground, d);
            }
            case 6 -> {
                keyName.setText("Interact");
                keyBindingsContainer.add(interactKey, c);
                keyBindingsContainer.add(interactKeyBackground, d);
            }
        }
    }

    private void addConfirmLabels(Font font) {
        applyLabel.setText("Apply");
        applyLabel.setFont(font.deriveFont(22f));
        applyLabel.setForeground(new Color(0x837E65));
        defaultLabel.setText("Default");
        defaultLabel.setFont(font.deriveFont(22f));
        defaultLabel.setForeground(new Color(0xe0d9ae));
        cancelLabel.setText("Cancel");
        cancelLabel.setFont(font.deriveFont(22f));
        cancelLabel.setForeground(new Color(0xe0d9ae));

        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = keyLabels.size() / 2 + 1;
        c.insets = new Insets(5, 80, 40, 0);
        c.anchor = GridBagConstraints.WEST;
        keyBindingsContainer.add(applyLabel, c);

        c.gridx = 1;
        c.insets = new Insets(5, 70, 40, 0);
        c.anchor = GridBagConstraints.CENTER;
        keyBindingsContainer.add(defaultLabel, c);

        c.gridx = 2;
        c.insets = new Insets(5, 0, 40, 80);
        c.anchor = GridBagConstraints.EAST;
        keyBindingsContainer.add(cancelLabel, c);
    }

    private void createListeners() {

        musicVolumeSlider.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                super.mousePressed(e);

                handleMouseEvent("S - click2");
            }
        });
        musicVolumeSlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {

                AudioManager.setMusicVolume((float) musicVolumeSlider.getValue() / 100);
                handleMouseEvent(null);
            }
        });

        soundVolumeSlider.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                super.mousePressed(e);

                handleMouseEvent("S - click2");
            }
        });
        soundVolumeSlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {

                AudioManager.setSoundVolume((float) soundVolumeSlider.getValue() / 100);
                handleMouseEvent(null);
            }
        });

        muteBox.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                super.mousePressed(e);

                handleMouseEvent(null);
            }
        });
        muteBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                AudioManager.setMute(muteBox.isSelected());
                handleMouseEvent("S - click1");
            }
        });
        muteBox.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                super.mouseEntered(e);

                handleMouseEvent(null);
            }
        });
        muteBox.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseExited(MouseEvent e) {
                super.mouseEntered(e);

                handleMouseEvent(null);
            }
        });

        backLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                super.mousePressed(e);

                optionsContainer.setVisible(true);
                keyBindingsContainer.setVisible(false);

                AudioManager.play("S - click1");
                setBackgroundImages();
                Main.showPreviousScreen();
            }
        });
        backLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                super.mouseEntered(e);

                backLabel.setForeground(new Color(0x262F3F));
                AudioManager.play("S - hover1");
            }
        });
        backLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseExited(MouseEvent e) {
                super.mouseEntered(e);

                backLabel.setForeground(new Color(0x3d4663));
                // Necessary because the options screen wouldn't display
                // after closing and opening it again, due to the label
                // triggering the setVisible function
                if (Main.currentScreen.equals("Options")) {
                    setVisible(false);
                    setVisible(true);
                }
            }
        });

        changeKeyBindingsLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                super.mousePressed(e);

                loadKeyBindings();
                optionsContainer.setVisible(!optionsContainer.isVisible());
                keyBindingsContainer.setVisible(!keyBindingsContainer.isVisible());
                handleMouseEvent("S - click1");
            }
        });
        changeKeyBindingsLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                super.mouseEntered(e);

                changeKeyBindingsLabel.setForeground(new Color(0x837E65));
                handleMouseEvent("S - hover1");
            }
        });
        changeKeyBindingsLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseExited(MouseEvent e) {
                super.mouseEntered(e);

                changeKeyBindingsLabel.setForeground(new Color(0xe0d9ae));
                handleMouseEvent(null);
            }
        });

        moveUpKeyBackground.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                super.mousePressed(e);

                toggleKeyBackground("moveUp");
            }
        });
        moveLeftKeyBackground.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                super.mousePressed(e);

                toggleKeyBackground("moveLeft");
            }
        });
        moveDownKeyBackground.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                super.mousePressed(e);

                toggleKeyBackground("moveDown");
            }
        });
        moveRightKeyBackground.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                super.mousePressed(e);

                toggleKeyBackground("moveRight");
            }
        });
        attackKeyBackground.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                super.mousePressed(e);

                toggleKeyBackground("attack");
            }
        });
        inventoryKeyBackground.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                super.mousePressed(e);

                toggleKeyBackground("inventory");
            }
        });
        interactKeyBackground.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                super.mousePressed(e);

                toggleKeyBackground("interact");
            }
        });

        applyLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                super.mousePressed(e);

                saveKeyBindings();
                if (Main.previousScreen.equals("Game")) {
                    InputHandler.loadKeyBindings();
                }
            }
        });
        applyLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                super.mouseEntered(e);

                if (checkIfKeyChanged(false)) {
                    applyLabel.setForeground(new Color(0x837E65));
                    handleMouseEvent("S - hover1");
                }
            }
        });
        applyLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseExited(MouseEvent e) {
                super.mouseEntered(e);

                if (checkIfKeyChanged(false)) {
                    applyLabel.setForeground(new Color(0xe0d9ae));
                } else {
                    applyLabel.setForeground(new Color(0x837E65));
                }
                handleMouseEvent(null);
            }
        });

        defaultLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                super.mousePressed(e);

                loadDefaultKeyBindings();
                handleMouseEvent("S - click1");
            }
        });
        defaultLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                super.mouseEntered(e);

                defaultLabel.setForeground(new Color(0x837E65));
                handleMouseEvent("S - hover1");
            }
        });
        defaultLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseExited(MouseEvent e) {
                super.mouseEntered(e);

                defaultLabel.setForeground(new Color(0xe0d9ae));
                handleMouseEvent(null);
            }
        });

        cancelLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                super.mousePressed(e);

                optionsContainer.setVisible(!optionsContainer.isVisible());
                keyBindingsContainer.setVisible(!keyBindingsContainer.isVisible());
                handleMouseEvent("S - click1");
                setBackgroundImages();
            }
        });
        cancelLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                super.mouseEntered(e);

                cancelLabel.setForeground(new Color(0x837E65));
                handleMouseEvent("S - hover1");
            }
        });
        cancelLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseExited(MouseEvent e) {
                super.mouseEntered(e);

                cancelLabel.setForeground(new Color(0xe0d9ae));
                handleMouseEvent(null);
            }
        });
    }

    private void handleMouseEvent(String audio) {
        // Toggles the visibility to avoid
        // the display bug
        requestFocusInWindow();
        setVisible(false);
        setVisible(true);
        if (audio == null) return;
        AudioManager.play(audio);
    }

    private void toggleKeyBackground(String currentKey) {
        if (key == null) {
            key = currentKey;
            lastKey = keyBindings.get(key + "Current");

        } else if (!key.equals(currentKey)) {
            updateKeyBindings(KeyEvent.getKeyText(KeyEvent.VK_ESCAPE));

            key = currentKey;
            lastKey = keyBindings.get(key + "Current");
        }

        keyLabels.get(key).setText(" ");
        keyLabels.get(key + "Background").setIcon(keyBackgroundImageHover);
        listening = true;
        AudioManager.play("S - click1");
        requestFocusInWindow();
    }

    private void updateKeyBindings(String keyText) {

        if (!keyText.equals(KeyEvent.getKeyText(KeyEvent.VK_ESCAPE))) {

            String keyName = keyLabels.entrySet().stream()
                    .filter(entry -> keyText.equals(entry.getValue().getText()))
                    .findFirst()
                    .map(Map.Entry::getKey)
                    .orElse(null);

            if (keyName != null) {
                keyBindings.replace(keyName + "Current", "Not Bound");
                keyLabels.get(keyName).setText("Not Bound");
            }

            keyBindings.replace(key + "Current", keyText);
            keyLabels.get(key).setText(keyText);

            if (checkIfKeyChanged(false)) {
                applyLabel.setForeground(new Color(0xe0d9ae));
            } else {
                applyLabel.setForeground(new Color(0x837E65));
            }

        } else {
            keyLabels.get(key).setText(lastKey);
        }
        keyLabels.get(key + "Background").setIcon(keyBackgroundImage);
        handleMouseEvent(null);
    }

    private void loadKeyBindings() {

        if (jsonKeys == null) {
            String gameDir = System.getProperty("user.dir");
            File configDir = new File(gameDir, "config");
            File jsonFile = new File(configDir, "keyBindings.json");

            ResourceLoader rl = ResourceLoader.getResourceLoader();
            jsonKeys = rl.readConfigJsonFile(jsonFile.getPath());

        }
        // Loop through every key type/name (walkUp, inventory,..)
        for (String keyName : jsonKeys.keySet()) {

            JsonObject key = jsonKeys.getAsJsonObject(keyName);
            keyBindings.put(keyName + "Current", key.get("current").getAsString());
            keyBindings.put(keyName + "Default", key.get("default").getAsString());
        }
        setKeyText();
    }

    private void setKeyText() {

        moveUpKey.setText(keyBindings.get("moveUpCurrent"));
        moveLeftKey.setText(keyBindings.get("moveLeftCurrent"));
        moveDownKey.setText(keyBindings.get("moveDownCurrent"));
        moveRightKey.setText(keyBindings.get("moveRightCurrent"));
        attackKey.setText(keyBindings.get("attackCurrent"));
        inventoryKey.setText(keyBindings.get("inventoryCurrent"));
        interactKey.setText(keyBindings.get("interactCurrent"));
    }

    private void loadDefaultKeyBindings() {

        boolean keyChanged = false;
        for (String keyName : jsonKeys.keySet()) {

            JsonObject key = jsonKeys.getAsJsonObject(keyName);

            if (!keyBindings.get(keyName + "Current").equals(key.get("default").getAsString())) {
                keyBindings.replace(keyName + "Current", key.get("default").getAsString());
                keyChanged = true;
            }
        }
        setKeyText();

        setBackgroundImages();
        if (keyChanged) applyLabel.setForeground(new Color(0xe0d9ae));
    }

    private boolean checkIfKeyChanged(boolean applyPressed) {

        boolean keyChanged = false;
        for (String keyName : jsonKeys.keySet()) {

            JsonObject key = jsonKeys.getAsJsonObject(keyName);

            if (!key.get("current").getAsString().equals(keyBindings.get(keyName + "Current"))) {
                keyChanged = true;

                if (applyPressed) {
                    key.addProperty("current", keyBindings.get(keyName + "Current"));
                }
            }
        }
        return keyChanged;
    }

    private void saveKeyBindings() {

        if (checkIfKeyChanged(true)) {

            AudioManager.play("S - click1");

            String gameDir = System.getProperty("user.dir");
            File configDir = new File(gameDir, "config");
            
            if (!configDir.exists()) {
                configDir.mkdir();
            }
            File jsonFile = new File(configDir, "keyBindings.json");

            ResourceLoader rl = ResourceLoader.getResourceLoader();
            rl.writeJsonFile(jsonFile.getPath(), jsonKeys);
        }
    }

    public void paintComponent(Graphics g) {
        g.drawImage(MainMenuPanel.backgroundImage, 0, 0,
                (int) (Main.DEFAULT_WINDOW_WIDTH * Main.SCALING_FACTOR),
                (int) (Main.DEFAULT_WINDOW_HEIGHT * Main.SCALING_FACTOR),
                null);
    }

    public void keyTyped(KeyEvent e) {}

    public void keyReleased(KeyEvent e) {}

    @Override
    public void keyPressed(KeyEvent e) {
        int keyCode;
        if (listening) {
            listening = false;
            keyCode = e.getKeyCode();
            updateKeyBindings(KeyEvent.getKeyText(keyCode));
            key = null;
        }
    }
}