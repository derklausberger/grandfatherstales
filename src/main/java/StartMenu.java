import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;


public class StartMenu extends JFrame {
    public static final int WINDOW_WIDTH = 1280;
    public static final int WINDOW_HEIGHT = 920;

    private JPanel mainPanel;
    public JLabel background;

    public StartMenu(String title) {
        super(title);

        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setContentPane(mainPanel);
        this.pack();

        background.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                background.setIcon(new ImageIcon("src/main/resources/MainMenu2.png"));
            }
        });
    }

    public static void main(String[] args) {
        JFrame frame = new StartMenu("game");
        frame.setVisible(true);
        frame.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        frame.pack();
    }
}