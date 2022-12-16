import javax.swing.*;

public class StartMenu extends JFrame {
    private JPanel mainPanel;

    public StartMenu(String title) {
        super(title);

        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setContentPane(mainPanel);
        this.pack();
    }

    public static void main(String[] args) {
        JFrame frame = new StartMenu("Titel");
        frame.setVisible(true);
        frame.setSize(200, 200);
    }
}