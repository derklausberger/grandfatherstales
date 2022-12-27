import GUI.GamePanel;

import javax.swing.*;
import java.awt.*;

public class Main {
    public static void main(String[] args) {
        try {
            JFrame window = new JFrame();
            window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            window.setBackground(Color.black);
            window.setResizable(false);
            window.setTitle("");

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
