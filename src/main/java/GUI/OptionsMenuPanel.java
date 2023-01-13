package GUI;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import main.Main;
import java.awt.*;
import java.awt.event.*;

public class OptionsMenuPanel extends JPanel {

    private final JLabel
            backLabel = new JLabel();

    private final JSlider
            musicVolumeSlider = new JSlider(),
            soundVolumeSlider = new JSlider();

    private final JCheckBox
            muteBox = new JCheckBox();

    public OptionsMenuPanel() {

        setLayout(new GridBagLayout());

        init();
    }

    private void init() {

        muteBox.setOpaque(false);
        musicVolumeSlider.setOpaque(false);
        musicVolumeSlider.setValue(100);
        soundVolumeSlider.setOpaque(false);
        soundVolumeSlider.setValue(100);

        // Create and add "X" label
        backLabel.setText("Back");
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 1;
        c.gridy = 0;
        c.weightx = 0;
        c.weighty = 0;
        c.insets = new Insets(20, 20, 0, 0);
        c.anchor = GridBagConstraints.NORTHWEST;
        add(backLabel, c);

        // Create and add container for options
        JPanel optionsContainer = new JPanel();
        optionsContainer.setOpaque(false);
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

        // Create and add mute label and checkbox
        JLabel muteLabel = new JLabel("Mute");
        c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 0;
        c.weighty = 0;
        c.insets = new Insets(10, 100, 10, 100);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.anchor = GridBagConstraints.CENTER;
        optionsContainer.add(muteLabel, c);
        c = new GridBagConstraints();
        c.gridx = 1;
        c.gridy = 0;
        c.weightx = 0;
        c.weighty = 0;
        c.insets = new Insets(10, 0, 10, 40);
        c.anchor = GridBagConstraints.CENTER;
        optionsContainer.add(muteBox, c);

        // Create and add "music" label and volume slider
        JLabel musicLabel = new JLabel("Music");
        c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 1;
        c.weightx = 0;
        c.weighty = 0;
        c.insets = new Insets(10, 40, 10, 40);
        c.anchor = GridBagConstraints.CENTER;
        optionsContainer.add(musicLabel, c);
        c = new GridBagConstraints();
        c.gridx = 1;
        c.gridy = 1;
        c.weightx = 0;
        c.weighty = 0;
        c.insets = new Insets(10, 0, 10, 40);
        c.anchor = GridBagConstraints.CENTER;
        optionsContainer.add(musicVolumeSlider, c);

        // Create and add "music" label and volume slider
        JLabel soundLabel = new JLabel("Sound");
        c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 2;
        c.weightx = 0;
        c.weighty = 0;
        c.insets = new Insets(10, 40, 10, 40);
        c.anchor = GridBagConstraints.CENTER;
        optionsContainer.add(soundLabel, c);
        c = new GridBagConstraints();
        c.gridx = 1;
        c.gridy = 2;
        c.weightx = 0;
        c.weighty = 0;
        c.insets = new Insets(10, 0, 10, 40);
        c.anchor = GridBagConstraints.CENTER;
        optionsContainer.add(soundVolumeSlider, c);

        createListeners();
    }

    private void createListeners() {

        musicVolumeSlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {

                AudioManager.setMusicVolume((float) musicVolumeSlider.getValue() / 100);
                AudioManager.play("S - c2");
            }
        });

        soundVolumeSlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {

                AudioManager.setSoundVolume((float) soundVolumeSlider.getValue() / 100);
                AudioManager.play("S - c2");
            }
        });

        muteBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                AudioManager.setMute(muteBox.isSelected());
                AudioManager.play("S - c1");
            }
        });

        backLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                super.mousePressed(e);

                AudioManager.play("S - c1");
                Main.showPreviousScreen();
            }
        });
    }

    public void paintComponent(Graphics g) {
        g.drawImage(MainMenuPanel.backgroundImage, 0, 0,
                (int) (Main.DEFAULT_WINDOW_WIDTH * Main.SCALING_FACTOR),
                (int) (Main.DEFAULT_WINDOW_HEIGHT * Main.SCALING_FACTOR),
                null);
    }
}