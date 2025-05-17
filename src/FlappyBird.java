import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.*;
import java.io.*;


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
    int birdWidth = 34;
    int birdHeight = 24;

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
    int pipeHeight = 512;

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

    Timer gameLoop;
    Timer placePipeTimer;
    boolean gameOver = false;
    double score = 0;

    JFrame frame;

    public FlappyBird(JFrame frame, String difficulty) {
        this.frame = frame;
        if (difficulty.equals("Easy")) {
            velocityX = -6;
            placePipeTimer = new Timer(1500, e -> placePipes());
            placePipeTimer.start();
        }
        else if (difficulty.equals("Medium")) {
            velocityX = -12;
            placePipeTimer = new Timer(1500, e -> placePipes());
            placePipeTimer.start();
        }
        if (difficulty.equals("Hard")) {
            velocityX = -60;
            placePipeTimer = new Timer(700, e -> placePipes());
            placePipeTimer.start();
        }
        if (difficulty.equals("Impossible")) {
            velocityX = -120;
            placePipeTimer = new Timer(150, e -> placePipes());
            placePipeTimer.start();
        }
        if (difficulty.equals("Cooked")) {
            velocityX = -140;
            placePipeTimer = new Timer(60, e -> placePipes());
            placePipeTimer.start();
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

    private int loadCurrentHighScore() {
        try (BufferedReader reader = new BufferedReader(new FileReader("highscore.txt"))) {
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
            StartPanel.saveHighScore((int) Math.max(score, loadCurrentHighScore()));
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            velocityY = -9;
            if (gameOver || e.getKeyCode() == KeyEvent.VK_R) {
                bird.y = birdY;
                velocityY = 0;
                pipes.clear();
                gameOver = false;
                score = 0;
                gameLoop.start();
                placePipeTimer.start();
            }
        }

        if (gameOver || e.getKeyCode() == KeyEvent.VK_R) {
            App.showStartScreen(frame);
        }
    }

    @Override public void keyTyped(KeyEvent e) {}
    @Override public void keyReleased(KeyEvent e) {}
}