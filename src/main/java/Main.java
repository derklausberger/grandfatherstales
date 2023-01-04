import GUI.GamePanel;

import javax.swing.*;
import java.awt.*;

public class Main {

    public static JFrame window;

    public static void main(String[] args) {
        try {
            window = new JFrame();
            window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);


            window.setResizable(false);
            window.setTitle("Grandfather's Tales");

            GamePanel gamePanel = new GamePanel();
            window.add(gamePanel);

            window.pack();

            window.setLocationRelativeTo(null);
            window.setVisible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
