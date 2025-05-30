import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Random;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.*;

public class FlappyBird extends JPanel implements ActionListener, KeyListener {
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    int boardWidth = (int) screenSize.getWidth();
    int boardHeight = (int) screenSize.getHeight();

    Image backgroundImg;
    Image birdImg;
    Image topPipeImg;
    Image bottomPipeImg;

    int birdX = boardWidth / 8;
    int birdY = boardHeight / 2;
    int birdWidth = (int) (34.0 * 1.5);
    int birdHeight = (int) (24.0 * 1.5);

    class Bird {
        int x = birdX;
        int y = birdY;
        int width = birdWidth;
        int height = birdHeight;
        Image img;

        Bird(Image img) {
            this.img = img;
        }
    }

    int pipeX = boardWidth;
    int pipeY = 0;
    int pipeWidth = 64;
    int pipeHeight = 512; //512

    class Pipe {
        int x = pipeX;
        int y = pipeY;
        int width = pipeWidth;
        int height = pipeHeight;
        Image img;
        boolean passed = false;

        Pipe(Image img) {
            this.img = img;
        }
    }

    Bird bird;
    int velocityX = -6;
    int velocityY = 0;
    int gravity = 1;

    ArrayList<Pipe> pipes;
    Random random = new Random();

    String diff;
    Timer gameLoop;
    Timer placePipeTimer;
    boolean gameOver = false;
    double score = 0;
    int difficulty = 1;

    JFrame frame;
    Clip clip;

    public FlappyBird(JFrame frame, String difficulty) {
        this.frame = frame;
        diff = difficulty;
        if (difficulty.equals("Easy")) {
            velocityX = -6;
            placePipeTimer = new Timer(1500, e -> placePipes());
            placePipeTimer.start();
            this.difficulty = 1;
        }
        else if (difficulty.equals("Medium")) {
            velocityX = -12;
            placePipeTimer = new Timer(1500, e -> placePipes());
            placePipeTimer.start();
            this.difficulty = 2;
        }
        if (difficulty.equals("Hard")) {
            velocityX = -60;
            placePipeTimer = new Timer(700, e -> placePipes());
            placePipeTimer.start();
            this.difficulty = 3;
        }
        if (difficulty.equals("Impossible")) {
            velocityX = -120;
            placePipeTimer = new Timer(150, e -> placePipes());
            placePipeTimer.start();
            this.difficulty = 4;
        }
        if (difficulty.equals("Cooked")) {
            velocityX = -140;
            placePipeTimer = new Timer(60, e -> placePipes());
            placePipeTimer.start();
            this.difficulty = 5;
        }
        if (difficulty.equals("Just Why")) {
            velocityX = -150;
            placePipeTimer = new Timer(20  , e -> placePipes());
            placePipeTimer.start();
            this.difficulty = 6;
        }

        setPreferredSize(new Dimension(boardWidth, boardHeight));
        setFocusable(true);
        addKeyListener(this);

        backgroundImg = new ImageIcon(getClass().getResource("/BG/flappybirdbg.png")).getImage();
        birdImg = new ImageIcon(getClass().getResource("/Birds/flappybird.png")).getImage();
        topPipeImg = new ImageIcon(getClass().getResource("/TopPipes/toppipe.png")).getImage();
        bottomPipeImg = new ImageIcon(getClass().getResource("/BottomPipes/bottompipe.png")).getImage();

        bird = new Bird(birdImg);
        pipes = new ArrayList<>();

        gameLoop = new Timer(1000 / 60, this);
        gameLoop.start();

        try {
          
            if (clip != null && clip.isRunning()) {
                clip.stop(); // Stop the previous clip if it's running
            }
            if (clip != null) {
                clip.close(); // Close the previous clip to release resources
            }

            java.net.URL soundURL = getClass().getResource("/Sounds/bgMusic.wav");
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(soundURL);
           
            clip = AudioSystem.getClip();
            clip.open(audioInputStream);
            clip.start();
            clip.loop(-1);
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        }
    }

    void placePipes() {
        int randomPipeY = (int) (pipeY - pipeHeight / 4 - Math.random() * (pipeHeight / 2));
        int openingSpace = boardHeight / 4;

        Pipe topPipe = new Pipe(topPipeImg);
        topPipe.y = randomPipeY;
        pipes.add(topPipe);

        Pipe bottomPipe = new Pipe(bottomPipeImg);
        bottomPipe.y = topPipe.y + pipeHeight + openingSpace;
        pipes.add(bottomPipe);
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    public void draw(Graphics g) {
        g.drawImage(backgroundImg, 0, 0, boardWidth, boardHeight, null);
        g.drawImage(birdImg, bird.x, bird.y, bird.width, bird.height, null);

        for (Pipe pipe : pipes) {
            g.drawImage(pipe.img, pipe.x, pipe.y, pipe.width, pipe.height, null);
        }

        g.setColor(Color.white);
        g.setFont(new Font("Arial", Font.PLAIN, 32));
        if (gameOver) {
            g.drawString("Game Over: " + (int) score, 10, 35);
        } else {
            g.drawString(String.valueOf((int) score), 10, 35);
        }
    }

    public void move() {
        velocityY += gravity;
        bird.y += velocityY;
        bird.y = Math.max(bird.y, 0);

        for (Pipe pipe : pipes) {
            pipe.x += velocityX;

            if (!pipe.passed && bird.x > pipe.x + pipe.width) {
                score += 0.5;
                pipe.passed = true;
            }

            if (collision(bird, pipe)) {
                gameOver = true;
            }
        }

        if (bird.y > boardHeight) {
            gameOver = true;
        }
    }

    boolean collision(Bird a, Pipe b) {
        return a.x < b.x + b.width &&
               a.x + a.width > b.x &&
               a.y < b.y + b.height &&
               a.y + a.height > b.y;
    }

    private static int loadCurrentHighScoreEasy() {
        try (BufferedReader reader = new BufferedReader(new FileReader("highscoreEasy.txt"))) {
            return Integer.parseInt(reader.readLine());
        } catch (IOException | NumberFormatException e) {
            return 0;
        }
    }
    private static int loadCurrentHighScoreMedium() {
        try (BufferedReader reader = new BufferedReader(new FileReader("highscoreMedium.txt"))) {
            return Integer.parseInt(reader.readLine());
        } catch (IOException | NumberFormatException e) {
            return 0;
        }
    }
    private static int loadCurrentHighScoreHard() {
        try (BufferedReader reader = new BufferedReader(new FileReader("highscoreHard.txt"))) {
            return Integer.parseInt(reader.readLine());
        } catch (IOException | NumberFormatException e) {
            return 0;
        }
    }
    private static int loadCurrentHighScoreImpossible() {
        try (BufferedReader reader = new BufferedReader(new FileReader("highscoreImpossible.txt"))) {
            return Integer.parseInt(reader.readLine());
        } catch (IOException | NumberFormatException e) {
            return 0;
        }
    }
    private static int loadCurrentHighScoreCooked() {
        try (BufferedReader reader = new BufferedReader(new FileReader("highscoreCooked.txt"))) {
            return Integer.parseInt(reader.readLine());
        } catch (IOException | NumberFormatException e) {
            return 0;
        }
    }
    private static int loadCurrentHighScoreJustWhy() {
        try (BufferedReader reader = new BufferedReader(new FileReader("highscoreJustWhy.txt"))) {
            return Integer.parseInt(reader.readLine());
        } catch (IOException | NumberFormatException e) {
            return 0;
        }
    }
    

    @Override
    public void actionPerformed(ActionEvent e) {
        move();
        repaint();
        if (gameOver) {
            placePipeTimer.stop();
            gameLoop.stop();
            if (diff.equals("Easy"))
                StartPanel.saveHighScoreEasy((int) Math.max(score, loadCurrentHighScoreEasy()));
            if (diff.equals("Medium"))
                StartPanel.saveHighScoreMedium((int) Math.max(score, loadCurrentHighScoreMedium()));
            if (diff.equals("Hard"))
                StartPanel.saveHighScoreHard((int) Math.max(score, loadCurrentHighScoreHard()));
            if (diff.equals("Impossible"))
                StartPanel.saveHighScoreImpossible((int) Math.max(score, loadCurrentHighScoreImpossible()));
            if (diff.equals("Cooked"))
                StartPanel.saveHighScoreCooked((int) Math.max(score, loadCurrentHighScoreCooked()));
            if (diff.equals("Just Why"))
                StartPanel.saveHighScoreJustWhy((int) Math.max(score, loadCurrentHighScoreJustWhy()));
            StartPanel.saveTotalCoins((int)(score),getDifficulty());
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            velocityY = -9;
        }
        if (gameOver || e.getKeyCode() == KeyEvent.VK_R) {
            if (diff.equals("Easy"))
                StartPanel.saveHighScoreEasy((int) (Math.max(score, loadCurrentHighScoreEasy())));
            if (diff.equals("Medium"))
                StartPanel.saveHighScoreMedium((int) (Math.max(score, loadCurrentHighScoreMedium())));
            if (diff.equals("Hard"))
                StartPanel.saveHighScoreHard((int) (Math.max(score, loadCurrentHighScoreHard())));
            if (diff.equals("Impossible"))
                StartPanel.saveHighScoreImpossible((int) (Math.max(score, loadCurrentHighScoreImpossible())));
            if (diff.equals("Cooked"))
                StartPanel.saveHighScoreCooked((int) (Math.max(score, loadCurrentHighScoreCooked())));
            if (diff.equals("Just Why"))
                StartPanel.saveHighScoreJustWhy((int) (Math.max(score, loadCurrentHighScoreJustWhy())));
            if(gameOver  && score == 0){
                StartPanel.saveTotalCoins((int)(score-25),getDifficulty());
            }else{
                StartPanel.saveTotalCoins((int)(score),getDifficulty());
            }
            bird.y = birdY;
            velocityY = 0;
            pipes.clear();
            gameOver = false;
            score = 0;
            gameLoop.start();
            placePipeTimer.start();
        }

        if (gameOver || e.getKeyCode() == KeyEvent.VK_R) {
            App.showStartScreen(frame);
        }
    }
    public int getDifficulty() {
        return difficulty;
    }

    @Override public void keyTyped(KeyEvent e) {}
    @Override public void keyReleased(KeyEvent e) {}
}