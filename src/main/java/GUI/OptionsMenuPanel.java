package GUI;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import main.Main;

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;

public class OptionsMenuPanel extends JPanel {

    private Image backgroundImage;

    private JPanel container;

    public OptionsMenuPanel() {

        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        init();
    }

    private void init() {

        try {
            backgroundImage = new ImageIcon(ImageIO.read(new File("src/main/resources/screen/mainMenuPanel/mainMenu.png"))).getImage();
        } catch (IOException e) {
            e.printStackTrace();
        }

        JPanel test = new JPanel();
        test.setOpaque(false);
        //test.setLayout(null);

        JPanel optionContainer = new JPanel();
        optionContainer.setPreferredSize(new Dimension(500, 300));
        optionContainer.setOpaque(false);
        optionContainer.setLayout(new BoxLayout(optionContainer, BoxLayout.X_AXIS));

        JLabel backButton = new JLabel("Back");
        test.add(backButton);

        container.add(test);

        Box verticalLeft = Box.createVerticalBox();
        //verticalLeft.setBackground(Color.red);
        //verticalLeft.setOpaque(true);
        verticalLeft.setPreferredSize(new Dimension(200, 300));

        Box verticalRight = Box.createVerticalBox();
        //verticalRight.setBackground(Color.green);
        //verticalRight.setOpaque(true);


        JLabel muteLabel = new JLabel("Mute");
        muteLabel.setAlignmentX(JComponent.CENTER_ALIGNMENT);
        JCheckBox muteBox = new JCheckBox();
        muteBox.setOpaque(false);
        muteBox.setAlignmentX(JComponent.CENTER_ALIGNMENT);

        JLabel musicVolumeLabel = new JLabel("Music");
        musicVolumeLabel.setAlignmentX(JComponent.CENTER_ALIGNMENT);
        JLabel soundVolumeLabel = new JLabel("Sound");
        soundVolumeLabel.setAlignmentX(JComponent.CENTER_ALIGNMENT);
        JSlider musicVolumeSlider = new JSlider();
        musicVolumeSlider.setOpaque(false);
        musicVolumeSlider.setValue(100);
        musicVolumeSlider.setAlignmentX(JComponent.CENTER_ALIGNMENT);
        JSlider soundVolumeSlider = new JSlider();
        soundVolumeSlider.setOpaque(false);
        soundVolumeSlider.setValue(100);
        soundVolumeSlider.setAlignmentX(JComponent.CENTER_ALIGNMENT);

        verticalLeft.add(muteLabel);
        verticalLeft.add(Box.createVerticalStrut(25));
        verticalLeft.add(musicVolumeLabel);
        verticalLeft.add(Box.createVerticalStrut(25));
        verticalLeft.add(soundVolumeLabel);

        verticalRight.add(muteBox);
        verticalRight.add(Box.createVerticalStrut(22));
        verticalRight.add(musicVolumeSlider);
        verticalRight.add(Box.createVerticalStrut(23));
        verticalRight.add(soundVolumeSlider);

        optionContainer.add(verticalLeft);
        optionContainer.add(Box.createHorizontalStrut(10));
        optionContainer.add(verticalRight);

        container.add(optionContainer);
        container.setPreferredSize(new Dimension(990, 553));

        this.add(container);


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

        backButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                super.mousePressed(e);

                AudioManager.play("S - c1");
                Main.showPreviousScreen();
            }
        });
    }

    public void paintComponent(Graphics g) {
        g.drawImage(backgroundImage, 0, 0, null);
    }

}
