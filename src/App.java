import java.awt.*;
import java.io.*;
import javax.swing.*;

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
    private static final String HIGHSCOREEASY_FILE = "highscoreEasy.txt";
    private static final String HIGHSCOREMEDIUM_FILE = "highscoreMedium.txt";
    private static final String HIGHSCOREHARD_FILE = "highscoreHard.txt";
    private static final String HIGHSCORECOOKED_FILE = "highscoreCooked.txt";
    private static final String HIGHSCOREIMPOSSIBLE_FILE = "highscoreImpossible.txt";
    private static final String HIGHSCOREJUSTWHY_FILE = "highscoreJustWhy.txt";
    private static final String COINS_FILE = "coins.txt";
    private int highScoreEasy = 0;
    private int highScoreMedium = 0;
    private int highScoreHard = 0;
    private int highScoreImpossible = 0;
    private int highScoreCooked = 0;
    private int highScoreJustWhy = 0;
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

        String[] difficulties = {"Easy", "Medium", "Hard", "Impossible", "Cooked", "Just Why"};

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

        highScoreEasy = loadHighScoreEasy();
        highScoreMedium = loadHighScoreMedium();
        highScoreHard = loadHighScoreHard();
        highScoreImpossible = loadHighScoreImpossible();
        highScoreCooked = loadHighScoreCooked();
        highScoreJustWhy = loadHighScoreJustWhy();
       
        coins = loadCoins();
    
        // Periodically check and update the high score label based on difficulty selection
        Timer timer = new Timer(500, e -> {
            String difficulty = (String) (difficultyDrop.getSelectedItem());
            if(difficulty == "Easy"){
                highScoreLabel.setText("High Score " + difficulty + " : " + highScoreEasy);
            } else if(difficulty == "Medium"){
                highScoreLabel.setText("High Score " + difficulty + " : " + highScoreMedium);
            } else if(difficulty == "Hard"){
                highScoreLabel.setText("High Score " + difficulty + " : " + highScoreHard);
            } else if(difficulty == "Impossible"){
                highScoreLabel.setText("High Score " + difficulty + " : " + highScoreImpossible);
            } else if(difficulty == "Cooked"){
                highScoreLabel.setText("High Score " + difficulty + " : " + highScoreCooked);
            } else if(difficulty == "Just Why"){
                highScoreLabel.setText("High Score " + difficulty + " : " + highScoreJustWhy);
            }
            
        });
        
        timer.start();
        
        
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
        timer.restart();
    }

    

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (backgroundImg != null) {
            g.drawImage(backgroundImg, 0, 0, getWidth(), getHeight(), this);
        }
    }

    private int loadHighScoreEasy() {
        try (BufferedReader reader = new BufferedReader(new FileReader(HIGHSCOREEASY_FILE))) {
            return Integer.parseInt(reader.readLine());
        } catch (IOException | NumberFormatException e) {
            return 0;
        }
    }
    public int loadHighScoreMedium() {
        try (BufferedReader reader = new BufferedReader(new FileReader(HIGHSCOREMEDIUM_FILE))) {
            return Integer.parseInt(reader.readLine());
        } catch (IOException | NumberFormatException e) {
            return 0;
        }
    }
    private int loadHighScoreHard() {
        try (BufferedReader reader = new BufferedReader(new FileReader(HIGHSCOREHARD_FILE))) {
            return Integer.parseInt(reader.readLine());
        } catch (IOException | NumberFormatException e) {
            return 0;
        }
    }
    private int loadHighScoreImpossible() {
        try (BufferedReader reader = new BufferedReader(new FileReader(HIGHSCOREIMPOSSIBLE_FILE))) {
            return Integer.parseInt(reader.readLine());
        } catch (IOException | NumberFormatException e) {
            return 0;
        }
    }
    private int loadHighScoreCooked() {
        try (BufferedReader reader = new BufferedReader(new FileReader(HIGHSCORECOOKED_FILE))) {
            return Integer.parseInt(reader.readLine());
        } catch (IOException | NumberFormatException e) {
            return 0;
        }
    }
    private int loadHighScoreJustWhy() {
        try (BufferedReader reader = new BufferedReader(new FileReader(HIGHSCOREJUSTWHY_FILE))) {
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

    public static void saveHighScoreEasy(int score) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(HIGHSCOREEASY_FILE))) {
            writer.write(String.valueOf(score));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void saveHighScoreMedium(int score) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(HIGHSCOREMEDIUM_FILE))) {
            writer.write(String.valueOf(score));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void saveHighScoreHard(int score) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(HIGHSCOREHARD_FILE))) {
            writer.write(String.valueOf(score));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void saveHighScoreImpossible(int score) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(HIGHSCOREIMPOSSIBLE_FILE))) {
            writer.write(String.valueOf(score));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void saveHighScoreCooked(int score) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(HIGHSCORECOOKED_FILE))) {
            writer.write(String.valueOf(score));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void saveHighScoreJustWhy(int score) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(HIGHSCOREJUSTWHY_FILE))) {
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
            case 6: // Just Why
                coinsToAdd = (int) (score * 10);
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
