package GUI;

import main.Main;
import utilityClasses.ResourceLoader;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Objects;

public class LoadingPanel extends JPanel implements ActionListener {

    private JPanel bottomImageContainer;
    private Timer timer;
    private int currentFrame;

    private JLabel characterImage, enemyImage;
    private JTextPane hintTextPane;

    public LoadingPanel() {

        init();
    }

    private void init() {

        setLayout(new BorderLayout());

        createContainers();
        loadImages();
        currentFrame = 0;

        new Timer(10, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                if (Main.currentScreen.equals("Loading")) {
                    ((Timer) e.getSource()).stop();
                    setVisible(false);
                    setVisible(true);
                    timer.start();
                    characterImage.setIcon(new ImageIcon(Objects.requireNonNull(getClass().getResource("/screen/loadingPanel/character.gif"))));
                    enemyImage.setIcon(new ImageIcon(Objects.requireNonNull(getClass().getResource("/screen/loadingPanel/skeleton.gif"))));
                }
            }
        }).start();

        timer = new Timer(103, this);
    }

    public void actionPerformed(ActionEvent e) {

        if (!Main.currentScreen.equals("Loading")) {
            timer.stop();
            return;
        }
        setVisible(false);
        setVisible(true);

        if (currentFrame <= 15) {
            // Moves the images closer together by increasing
            // the left and right border inset
            Border currentBorder = bottomImageContainer.getBorder();
            Border newBorder = BorderFactory.createCompoundBorder(currentBorder, BorderFactory.createEmptyBorder(0, 1, 0, 1));
            bottomImageContainer.setBorder(newBorder);
        }

        // Increases the frame. If the last one is
        // reached, starts over from the beginning
        currentFrame++;

        if (currentFrame >= 20) {
            currentFrame = 0;
        }
    }

    private void createContainers() {

        // Creates the vertical container to hold hints and images
        JPanel container = new JPanel();
        container.setOpaque(false);
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));

        add(container, BorderLayout.SOUTH);

        createBottomImageContainer();
        container.add(bottomImageContainer);
        container.add(Box.createVerticalStrut(20));
        container.add(createHintContainer());
    }

    private JPanel createHintContainer() {

        // Loads the font for the hints
        ResourceLoader rl = ResourceLoader.getResourceLoader();
        Font font = rl.getDefaultTextFont();

        // Creates the hint container
        JPanel hintContainer = new JPanel();
        hintContainer.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));
        hintContainer.setBackground(new Color(0xB3E0D9AE, true));
        hintContainer.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Creates the pane to hold the hint text
        hintTextPane = new JTextPane();
        hintTextPane.setOpaque(false);
        hintTextPane.setEditable(false);
        hintTextPane.setFocusable(false);
        hintTextPane.setPreferredSize(new Dimension((int) (Main.DEFAULT_WINDOW_WIDTH * Main.SCALING_FACTOR / 6 * 5), 30));

        // Style used to center the text within the text pane
        StyledDocument doc = hintTextPane.getStyledDocument();
        SimpleAttributeSet center = new SimpleAttributeSet();
        StyleConstants.setAlignment(center, StyleConstants.ALIGN_CENTER);
        doc.setParagraphAttributes(0, doc.getLength(), center, false);

        hintTextPane.setText("You can only carry one item of each type at a time");
        hintTextPane.setFont(font.deriveFont(19f));
        hintTextPane.setForeground(new Color(0x262F3F));
        hintTextPane.setBackground(Color.red);
        hintContainer.add(hintTextPane);

        return hintContainer;
    }

    private void createBottomImageContainer() {

        // Creates the image container to hold the character and enemy
        bottomImageContainer = new JPanel();
        bottomImageContainer.setOpaque(false);
        bottomImageContainer.setBackground(new Color(0, 0, 0, 0));
        bottomImageContainer.setLayout(new BoxLayout(bottomImageContainer, BoxLayout.X_AXIS));
        bottomImageContainer.setBorder(new EmptyBorder(0, 100, 0, 100));

        characterImage = new JLabel();
        bottomImageContainer.add(characterImage);

        // Adds a filling between the images to separate them evenly
        bottomImageContainer.add(Box.createHorizontalGlue());

        enemyImage = new JLabel();
        bottomImageContainer.add(enemyImage);
    }

    private void loadImages() {

        ResourceLoader rl = ResourceLoader.getResourceLoader();

        enemyImage.setIcon(rl.getImageIcon("/enemy/skeletonWarrior/walking/L1.png"));
    }

    public void paintComponent(Graphics g) {
        g.drawImage(MainMenuPanel.backgroundImage, 0, 0,
                (int) (Main.DEFAULT_WINDOW_WIDTH * Main.SCALING_FACTOR),
                (int) (Main.DEFAULT_WINDOW_HEIGHT * Main.SCALING_FACTOR),
                null);
    }
}