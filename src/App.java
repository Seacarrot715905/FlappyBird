import java.awt.Dimension;
import java.awt.Toolkit;
import javax.swing.*;
import java.awt.*;
import java.io.*;

public class App {

    public static void main(String[] args) {
        JFrame frame = new JFrame("Flappy Bird");
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int boardWidth = (int) screenSize.getWidth();
        int boardHeight = (int) screenSize.getHeight();

        frame.setSize(boardWidth, boardHeight);
        frame.setLocationRelativeTo(null);
        frame.setResizable(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        showStartScreen(frame);

        frame.setVisible(true);
    }

    public static void showStartScreen(JFrame frame) {
        frame.getContentPane().removeAll();
        StartPanel startPanel = new StartPanel(frame);
        frame.add(startPanel);
        frame.revalidate();
        frame.repaint();
        startPanel.requestFocus();
    }
}

class StartPanel extends JPanel {
    private static final String HIGHSCORE_FILE = "highscore.txt";
    private int highScore = 0;
    private Image backgroundImg;

    public StartPanel(JFrame frame) {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        try {
            backgroundImg = new ImageIcon("./flappybirdbg.png").getImage();
        } catch (Exception e) {
            System.out.println("Background image not found.");
        }

        JLabel title = new JLabel("Flappy Bird");
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        title.setFont(new Font("Arial", Font.BOLD, 36));
        title.setForeground(Color.WHITE);

        JButton startButton = new JButton("Start");
        JButton shopButton = new JButton("Shop");
        JLabel highScoreLabel = new JLabel();

        startButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        shopButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        highScoreLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        highScoreLabel.setForeground(Color.WHITE);

        highScore = loadHighScore();
        highScoreLabel.setText("High Score: " + highScore);

        startButton.addActionListener(e -> {
            frame.getContentPane().removeAll();
            FlappyBird flappyBird = new FlappyBird(frame);
            frame.add(flappyBird);
            frame.revalidate();
            frame.repaint();
            flappyBird.requestFocus();
        });

        shopButton.addActionListener(e -> JOptionPane.showMessageDialog(this, "Shop is under construction!"));

        add(Box.createVerticalGlue());
        add(title);
        add(Box.createRigidArea(new Dimension(0, 20)));
        add(highScoreLabel);
        add(Box.createRigidArea(new Dimension(0, 20)));
        add(startButton);
        add(Box.createRigidArea(new Dimension(0, 20)));
        add(shopButton);
        add(Box.createVerticalGlue());
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (backgroundImg != null) {
            g.drawImage(backgroundImg, 0, 0, getWidth(), getHeight(), this);
        }
    }

    private int loadHighScore() {
        try (BufferedReader reader = new BufferedReader(new FileReader(HIGHSCORE_FILE))) {
            return Integer.parseInt(reader.readLine());
        } catch (IOException | NumberFormatException e) {
            return 0;
        }
    }

    public static void saveHighScore(int score) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(HIGHSCORE_FILE))) {
            writer.write(String.valueOf(score));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
