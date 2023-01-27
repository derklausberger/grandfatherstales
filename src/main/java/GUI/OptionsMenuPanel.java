package GUI;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonReader;
import main.Main;
import utilityClasses.AudioManager;
import utilityClasses.InputHandler;
import utilityClasses.ResourceLoader;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class OptionsMenuPanel extends JPanel implements KeyListener {

    private ImageIcon keyBackgroundImage, keyBackgroundImageHover;

    private JPanel optionsContainer, keyBindingsContainer;

    private final JLabel
            backLabel = new JLabel(),
            moveUpLabel = new JLabel(),
            moveUpBackground = new JLabel(),
            moveLeftLabel = new JLabel(),
            moveLeftBackground = new JLabel(),
            moveDownLabel = new JLabel(),
            moveDownBackground = new JLabel(),
            moveRightLabel = new JLabel(),
            moveRightBackground = new JLabel(),
            attackLabel = new JLabel(),
            attackBackground = new JLabel(),
            inventoryLabel = new JLabel(),
            inventoryBackground = new JLabel(),
            applyLabel = new JLabel(),
            cancelLabel = new JLabel();

    private final Map<String, JLabel> keyLabels = new HashMap<>();
    public static final Map<String, String> keyBindings = new HashMap<>();
    private String lastKey;
    private boolean listening;
    private String key = null;

    private final JSlider
            musicVolumeSlider = new JSlider(),
            soundVolumeSlider = new JSlider();

    private final JCheckBox
            muteBox = new JCheckBox();

    private final JLabel
            changeKeyBindingsLabel = new JLabel();

    // To read and write keyBindings
    Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private JsonObject jsonKeys;

    // To load resources
    ResourceLoader rl = ResourceLoader.getResourceLoader();

    public OptionsMenuPanel() {

        setLayout(new GridBagLayout());
        addKeyListener(this);
        init();
    }

    private void init() {

        Font font = rl.getFontByFilePath("DePixelBreit.ttf");

        BufferedImage bufferedBackground, bufferedBackgroundHover;

        bufferedBackground = rl.getBufferedImage("/screen/optionsMenuPanel/keyBackground.png");
        bufferedBackgroundHover = rl.getBufferedImage("/screen/optionsMenuPanel/keyBackgroundHover.png");

        loadKeyBindings();


        keyLabels.put("moveUp", moveUpLabel);
        keyLabels.put("moveLeft", moveLeftLabel);
        keyLabels.put("moveDown", moveDownLabel);
        keyLabels.put("moveRight", moveRightLabel);
        keyLabels.put("attack", attackLabel);
        keyLabels.put("inventory", inventoryLabel);
        keyLabels.put("moveUpBackground", moveUpBackground);
        keyLabels.put("moveLeftBackground", moveLeftBackground);
        keyLabels.put("moveDownBackground", moveDownBackground);
        keyLabels.put("moveRightBackground", moveRightBackground);
        keyLabels.put("attackBackground", attackBackground);
        keyLabels.put("inventoryBackground", inventoryBackground);

        muteBox.setOpaque(false);
        musicVolumeSlider.setOpaque(false);
        musicVolumeSlider.setValue(100);
        soundVolumeSlider.setOpaque(false);
        soundVolumeSlider.setValue(100);

        // Create and add "X" label
        backLabel.setText("Back");
        backLabel.setFont(font.deriveFont(22f));
        backLabel.setForeground(new Color(0x3d4663));
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 1;
        c.gridy = 0;
        c.weightx = 0;
        c.weighty = 0;
        c.insets = new Insets(20, 20, 0, 0);
        c.anchor = GridBagConstraints.NORTHWEST;
        add(backLabel, c);

        // Creates and adds container for options
        optionsContainer = new JPanel();
        optionsContainer.setOpaque(true);
        optionsContainer.setBackground(new Color(0, 0, 0, 120));
        optionsContainer.setLayout(new GridBagLayout());
        c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 1;
        c.gridwidth = GridBagConstraints.REMAINDER;
        c.gridheight = GridBagConstraints.REMAINDER;
        c.weightx = 1.0;
        c.weighty = 1.0;
        c.insets = new Insets(0, 20, 40, 20);
        c.anchor = GridBagConstraints.CENTER;
        add(optionsContainer, c);

        optionsContainer.setVisible(true);

        // Adds the keyBindingsContainer
        keyBindingsContainer = new JPanel();
        keyBindingsContainer.setVisible(false);
        keyBindingsContainer.setOpaque(true);
        keyBindingsContainer.setLayout(new GridBagLayout());
        keyBindingsContainer.setBackground(new Color(0, 0, 0, 120));
        add(keyBindingsContainer, c);


        // Create and add mute label and checkbox
        JLabel muteLabel = new JLabel("Mute");
        muteLabel.setFont(font.deriveFont(22f));
        muteLabel.setForeground(new Color(0xe0d9ae));
        c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 0;
        c.weighty = 0;
        c.insets = new Insets(80, 80, 10, 80);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.anchor = GridBagConstraints.CENTER;
        optionsContainer.add(muteLabel, c);
        c = new GridBagConstraints();
        c.gridx = 1;
        c.gridy = 0;
        c.weightx = 0;
        c.weighty = 0;
        c.insets = new Insets(80, 80, 10, 80);
        c.anchor = GridBagConstraints.CENTER;
        optionsContainer.add(muteBox, c);

        // Create and add "music" label and volume slider
        JLabel musicLabel = new JLabel("Music");
        musicLabel.setFont(font.deriveFont(22f));
        musicLabel.setForeground(new Color(0xe0d9ae));
        c.gridx = 0;
        c.gridy = 1;
        c.insets = new Insets(10, 80, 10, 80);
        c.anchor = GridBagConstraints.CENTER;
        optionsContainer.add(musicLabel, c);
        c.gridx = 1;
        c.gridy = 1;
        c.insets = new Insets(10, 80, 10, 80);
        c.anchor = GridBagConstraints.CENTER;
        optionsContainer.add(musicVolumeSlider, c);

        // Create and add "music" label and volume slider
        JLabel soundLabel = new JLabel("Sound");
        soundLabel.setFont(font.deriveFont(22f));
        soundLabel.setForeground(new Color(0xe0d9ae));
        c.gridx = 0;
        c.gridy = 2;
        c.insets = new Insets(10, 80, 10, 80);
        c.anchor = GridBagConstraints.CENTER;
        optionsContainer.add(soundLabel, c);
        c.gridx = 1;
        c.gridy = 2;
        c.insets = new Insets(10, 80, 10, 80);
        c.anchor = GridBagConstraints.CENTER;
        optionsContainer.add(soundVolumeSlider, c);

        // Adds the changeKeyBindings label
        changeKeyBindingsLabel.setText("Change Key Bindings");
        changeKeyBindingsLabel.setFont(font.deriveFont(22f));
        changeKeyBindingsLabel.setForeground(new Color(0xe0d9ae));
        c.gridx = 0;
        c.gridy = 3;
        c.insets = new Insets(60, 80, 40, 80);
        c.gridwidth = 2;
        optionsContainer.add(changeKeyBindingsLabel, c);


        keyBackgroundImage = new ImageIcon(bufferedBackground.getScaledInstance(300, 30, Image.SCALE_SMOOTH));
        keyBackgroundImageHover = new ImageIcon(bufferedBackgroundHover.getScaledInstance(300, 30, Image.SCALE_SMOOTH));

        // Sets background image of all labels on the right
        for (String labelKey : keyLabels.keySet()) {
            if (labelKey.contains("Background")) {
                keyLabels.get(labelKey).setIcon(keyBackgroundImage);
            }
        }
        // Creates key binding labels
        for (int i = 0; i < 6; i++) {

            // Constraints for key name on the left
            JLabel keyName = new JLabel();
            keyName.setFont(font.deriveFont(20f));
            keyName.setForeground(new Color(0xe0d9ae));     //0x3d4663
            c = new GridBagConstraints();
            c.gridx = 0;
            c.gridy = i;
            c.weightx = 0;
            c.weighty = 0;
            c.insets = new Insets(10, 80, 10, 80);
            c.anchor = GridBagConstraints.WEST;

            if (i == 0) c.insets = new Insets(80, 80, 10, 80);
            if (i == 5) c.insets = new Insets(10, 80, 80, 80);
            keyBindingsContainer.add(keyName, c);

            // Constraints for the key on the right
            c.gridx = 1;
            c.gridy = i;
            c.insets = new Insets(15, 80, 10, 80);
            c.anchor = GridBagConstraints.CENTER;

            if (i == 0) c.insets = new Insets(86, 80, 10, 80);
            if (i == 5) c.insets = new Insets(15, 80, 80, 80);

            // Constraints for the background images on the right
            GridBagConstraints d = new GridBagConstraints();
            d.gridx = 1;
            d.gridy = i;
            d.insets = new Insets(10, 80, 10, 80);
            d.anchor = GridBagConstraints.CENTER;
            if (i == 0) d.insets = new Insets(80, 80, 10, 80);
            if (i == 5) d.insets = new Insets(10, 80, 80, 80);

            switch (i) {
                case 0 -> {
                    keyName.setText("Move Up");
                    keyBindingsContainer.add(moveUpLabel, c);
                    keyBindingsContainer.add(moveUpBackground, d);
                }
                case 1 -> {
                    keyName.setText("Move Left");
                    keyBindingsContainer.add(moveLeftLabel, c);
                    keyBindingsContainer.add(moveLeftBackground, d);
                }
                case 2 -> {
                    keyName.setText("Move Down");
                    keyBindingsContainer.add(moveDownLabel, c);
                    keyBindingsContainer.add(moveDownBackground, d);
                }
                case 3 -> {
                    keyName.setText("Move Right");
                    keyBindingsContainer.add(moveRightLabel, c);
                    keyBindingsContainer.add(moveRightBackground, d);
                }
                case 4 -> {
                    keyName.setText("Attack");
                    keyBindingsContainer.add(attackLabel, c);
                    keyBindingsContainer.add(attackBackground, d);
                }
                case 5 -> {
                    keyName.setText("Toggle Inventory");
                    keyBindingsContainer.add(inventoryLabel, c);
                    keyBindingsContainer.add(inventoryBackground, d);
                }
            }
        }

        applyLabel.setText("Apply");
        applyLabel.setFont(font.deriveFont(22f));
        applyLabel.setOpaque(false);
        applyLabel.setForeground(new Color(0xe0d9ae));
        cancelLabel.setText("Cancel");
        cancelLabel.setFont(font.deriveFont(22f));
        cancelLabel.setOpaque(false);
        cancelLabel.setForeground(new Color(0xe0d9ae));
        c.gridx = 0;
        c.gridy = 7;
        c.insets = new Insets(5, 80, 40, 80);
        c.anchor = GridBagConstraints.WEST;
        keyBindingsContainer.add(applyLabel, c);

        c.gridx = 1;
        c.gridy = 7;
        c.insets = new Insets(5, 80, 40, 80);
        c.anchor = GridBagConstraints.EAST;
        keyBindingsContainer.add(cancelLabel, c);

        moveUpLabel.setFont(font.deriveFont(20f));
        moveUpLabel.setForeground(new Color(0xe0d9ae));
        moveLeftLabel.setFont(font.deriveFont(20f));
        moveLeftLabel.setForeground(new Color(0xe0d9ae));
        moveDownLabel.setFont(font.deriveFont(20f));
        moveDownLabel.setForeground(new Color(0xe0d9ae));
        moveRightLabel.setFont(font.deriveFont(20f));
        moveRightLabel.setForeground(new Color(0xe0d9ae));
        attackLabel.setFont(font.deriveFont(20f));
        attackLabel.setForeground(new Color(0xe0d9ae));
        inventoryLabel.setFont(font.deriveFont(20f));
        inventoryLabel.setForeground(new Color(0xe0d9ae));

        createListeners();
    }

    private void createListeners() {

        musicVolumeSlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {

                AudioManager.setMusicVolume((float) musicVolumeSlider.getValue() / 100);
                optionsContainer.setVisible(false);
                optionsContainer.setVisible(true);
                AudioManager.play("S - c2");
            }
        });

        soundVolumeSlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {

                AudioManager.setSoundVolume((float) soundVolumeSlider.getValue() / 100);
                optionsContainer.setVisible(false);
                optionsContainer.setVisible(true);
                AudioManager.play("S - c2");
            }
        });

        muteBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                AudioManager.setMute(muteBox.isSelected());
                optionsContainer.setVisible(false);
                optionsContainer.setVisible(true);
                AudioManager.play("S - c1");
            }
        });

        backLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                super.mousePressed(e);

                optionsContainer.setVisible(true);
                keyBindingsContainer.setVisible(false);

                AudioManager.play("S - c1");
                Main.showPreviousScreen();
            }
        });
        backLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                super.mouseEntered(e);

                backLabel.setForeground(new Color(0x262F3F));
                AudioManager.play("S - h1");
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
                if (Main.currentScreen == "Options") {
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
                setVisible(false);
                setVisible(true);
                AudioManager.play("S - c1");
            }
        });
        changeKeyBindingsLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                super.mouseEntered(e);

                changeKeyBindingsLabel.setForeground(new Color(0x837E65));
                setVisible(false);
                setVisible(true);
                AudioManager.play("S - h1");
            }
        });
        changeKeyBindingsLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseExited(MouseEvent e) {
                super.mouseEntered(e);

                changeKeyBindingsLabel.setForeground(new Color(0xe0d9ae));
                setVisible(false);
                setVisible(true);
            }
        });

        moveUpBackground.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                super.mousePressed(e);

                toggleKeyBackground("moveUp");
            }
        });
        moveLeftBackground.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                super.mousePressed(e);

                toggleKeyBackground("moveLeft");
            }
        });
        moveDownBackground.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                super.mousePressed(e);

                toggleKeyBackground("moveDown");
            }
        });
        moveRightBackground.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                super.mousePressed(e);

                toggleKeyBackground("moveRight");
            }
        });
        attackBackground.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                super.mousePressed(e);

                toggleKeyBackground("attack");
            }
        });
        inventoryBackground.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                super.mousePressed(e);

                toggleKeyBackground("inventory");
            }
        });

        applyLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                super.mousePressed(e);

                saveKeyBindings();
                AudioManager.play("S - c1");
                if (Main.previousScreen.equals("Game")) {
                    InputHandler.loadKeyBindings();
                }
            }
        });
        applyLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                super.mouseEntered(e);

                applyLabel.setForeground(new Color(0x837E65));
                setVisible(false);
                setVisible(true);
                AudioManager.play("S - h1");
            }
        });
        applyLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseExited(MouseEvent e) {
                super.mouseEntered(e);

                applyLabel.setForeground(new Color(0xe0d9ae));
                setVisible(false);
                setVisible(true);
            }
        });

        cancelLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                super.mousePressed(e);

                optionsContainer.setVisible(!optionsContainer.isVisible());
                keyBindingsContainer.setVisible(!keyBindingsContainer.isVisible());
                setVisible(false);
                setVisible(true);
                AudioManager.play("S - c1");
            }
        });
        cancelLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                super.mouseEntered(e);

                cancelLabel.setForeground(new Color(0x837E65));
                setVisible(false);
                setVisible(true);
                AudioManager.play("S - h1");
            }
        });
        cancelLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseExited(MouseEvent e) {
                super.mouseEntered(e);

                cancelLabel.setForeground(new Color(0xe0d9ae));
                setVisible(false);
                setVisible(true);
            }
        });
    }

    private void toggleKeyBackground(String currentKey) {

        if (key == null) {
            key = currentKey;
            lastKey = keyBindings.get(key + "Current");

        } else if (key != currentKey) {
            updateKeyBindings(KeyEvent.getKeyText(KeyEvent.VK_ESCAPE));

            key = currentKey;
            lastKey = keyBindings.get(key + "Current");
        }

        keyLabels.get(key).setText(" ");
        keyLabels.get(key + "Background").setIcon(keyBackgroundImageHover);
        listening = true;
        AudioManager.play("S - c1");
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

        } else {
            keyLabels.get(key).setText(lastKey);
        }
        keyLabels.get(key + "Background").setIcon(keyBackgroundImage);
        setVisible(false);
        setVisible(true);
    }

    private void loadKeyBindings() {

        if (jsonKeys == null) {
            String gameDir = System.getProperty("user.dir");
            File configDir = new File(gameDir, "config");
            File jsonFile = new File(configDir, "keyBindings.json");
            
            jsonKeys = rl.readConfigJsonFile(jsonFile.getPath());

        }
        // Loop through every key type/name (walkUp, inventory,..)
        for (String keyName : jsonKeys.keySet()) {

            JsonObject key = jsonKeys.getAsJsonObject(keyName);
            keyBindings.put(keyName + "Current", key.get("current").getAsString());
            keyBindings.put(keyName + "Default", key.get("default").getAsString());
        }

        moveUpLabel.setText(keyBindings.get("moveUpCurrent"));
        moveLeftLabel.setText(keyBindings.get("moveLeftCurrent"));
        moveDownLabel.setText(keyBindings.get("moveDownCurrent"));
        moveRightLabel.setText(keyBindings.get("moveRightCurrent"));
        attackLabel.setText(keyBindings.get("attackCurrent"));
        inventoryLabel.setText(keyBindings.get("inventoryCurrent"));
    }

    private void saveKeyBindings() {

        boolean keyChanged = false;
        for (String keyName : jsonKeys.keySet()) {

            JsonObject key = jsonKeys.getAsJsonObject(keyName);

            if (!key.get("current").getAsString().equals(keyBindings.get(keyName + "Current"))) {

                key.addProperty("current", keyBindings.get(keyName + "Current"));
                keyChanged = true;
            }
        }

        if (keyChanged) {
            String gameDir = System.getProperty("user.dir");
            File configDir = new File(gameDir, "config");
            
            if (!configDir.exists()) {
                configDir.mkdir();
            }
            File jsonFile = new File(configDir, "keyBindings.json");

            rl.writeJsonFile(jsonFile.getPath(), jsonKeys);
        }
    }

    public void paintComponent(Graphics g) {
        g.drawImage(MainMenuPanel.backgroundImage, 0, 0,
                (int) (Main.DEFAULT_WINDOW_WIDTH * Main.SCALING_FACTOR),
                (int) (Main.DEFAULT_WINDOW_HEIGHT * Main.SCALING_FACTOR),
                null);
    }

    public void keyTyped(KeyEvent e) {
    }

    public void keyReleased(KeyEvent e) {
    }

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