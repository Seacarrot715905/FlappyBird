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
    private static final String COINS_FILE = "coins.txt";
    private int highScore = 0;
    private int coins = 0;
    private Image backgroundImg;

    public StartPanel(JFrame frame) {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        try {
            backgroundImg = new ImageIcon(getClass().getResource("/BG/flappybirdbg.png")).getImage();
        } catch (Exception e) {
            System.out.println("Background image not found.");
        }

        JLabel title = new JLabel("Flappy Bird");
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        title.setFont(new Font("Arial", Font.BOLD, 36));
        title.setForeground(Color.BLACK);

        String[] difficulties = {"Easy", "Medium", "Hard", "Impossible", "Cooked"};

        JButton startButton = new JButton("       Start        ");
        JButton shopButton = new JButton("        Shop        ");
        
        JComboBox<String> difficultyDrop = new JComboBox<>(difficulties);
        JLabel highScoreLabel = new JLabel();
        JLabel coinsLabel = new JLabel();

        
        difficultyDrop.setPreferredSize(new Dimension(120, 25));
        difficultyDrop.setMaximumSize(new Dimension(120, 900));
        difficultyDrop.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        startButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        shopButton.setAlignmentX(Component.CENTER_ALIGNMENT);
       
        highScoreLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        highScoreLabel.setForeground(Color.BLACK);

        coinsLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        coinsLabel.setForeground(Color.BLACK);

        highScore = loadHighScore();
        coins = loadCoins();
        
        highScoreLabel.setText("High Score: " + highScore);
        coinsLabel.setText("Coins: " + coins);
        
        startButton.addActionListener(e -> {
            frame.getContentPane().removeAll();
            String difficulty = (String) (difficultyDrop.getSelectedItem());
            FlappyBird flappyBird = new FlappyBird(frame, difficulty);
            frame.add(flappyBird);
            frame.revalidate();
            frame.repaint();
            flappyBird.requestFocus();
        });

        shopButton.addActionListener(e -> JOptionPane.showMessageDialog(this, "Shop is under construction!"));

        difficultyDrop.setPreferredSize(new Dimension(0, 10));

        add(Box.createVerticalGlue());
        add(title);
        add(Box.createRigidArea(new Dimension(0, 20)));
        add(highScoreLabel);
        add(Box.createRigidArea(new Dimension(0, 20)));
        add(coinsLabel);
        add(Box.createRigidArea(new Dimension(0, 20)));
        add(startButton);
        add(Box.createRigidArea(new Dimension(0, 20)));
        add(shopButton);
        add(Box.createRigidArea(new Dimension(0, 20)));
        add(difficultyDrop);
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
    private int loadCoins() {
        try (BufferedReader reader = new BufferedReader(new FileReader(COINS_FILE))) {
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
    public static void saveTotalCoins(int score, int difficulty) {
        int coinsToAdd = 0;
        switch (difficulty) {
            case 1: 
                coinsToAdd = (int)(score * 1);
                break;
            case 2: 
                coinsToAdd = (int)(score * 2); 
                break;
            case 3: 
                coinsToAdd = (int)(score * 2.5); 
                break;
            case 4: 
                coinsToAdd = (int)(score * 4); 
                break;
            case 5: 
                coinsToAdd = (int)(score * 6); 
                break;
            default: coinsToAdd = score;
        }
        
        int newTotal = 0;
        
        try {
            newTotal = new StartPanel(null).loadCoins() + coinsToAdd;
        } catch (Exception e) {
            newTotal = coinsToAdd;
        }
        if(newTotal < 0) {
            newTotal = 0;
        }
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(COINS_FILE))) {
            writer.write(String.valueOf(newTotal));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
